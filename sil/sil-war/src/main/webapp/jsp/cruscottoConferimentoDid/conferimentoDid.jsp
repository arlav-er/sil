<%@page import="it.eng.sil.module.amministrazione.redditoAttivazione.Decodifica.Provenienza"%>
<%@page import="it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.TipoEvento"%>
<%@ page
	contentType="text/html;charset=utf-8"
	
	import="javax.xml.datatype.XMLGregorianCalendar,
			it.eng.sil.module.conf.did.ConferimentoUtility,
			com.engiweb.framework.base.*,
			it.eng.sil.pojo.yg.sap.due.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String  titolo = "Nuovo Conferimento DID";
	String 	msgProfNuovo = "I dati del profiling sono obbligatori solo in caso di data dichiarazione uguale o successiva al 04/12/2017";
	String  msgProfConvalida = "Inviando i dati del profiling per la conferma, il Ministero effettuerà il ricalcolo del profiling in base ai dati inseriti";
	String  messaggioProfiling = "";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  _funzione   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
 	String prgConferimentoDID =  SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgConferimentoDID");
 	
 	// Se valorizzata la pagina di provenienza e' l'elenco Conferimenti DID e serve gestire il pulsante CHIUDI per il ritorno
 	String pageProvenienza = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PROVENIENZA");
 	
 	String codTipoEvento = null;		
 	String codStatoInvio = null;

	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);
	boolean isInvioConferma=false;
	boolean isInvioConvalida=false;
	boolean isInvioRevoca=false;
	
	// Se la pagina chiamante di provenienza e' l'Elenco Conferimenti DID e non il Cruscotto
	boolean isProvenienzaElenco=false;
	
	boolean canModify = true;	 
	boolean canInvioConferma = attributi.containsButton("INVIO_CONF_DID");
 	boolean canInvioConvalida = canInvioConferma;
 	boolean canInvioRevoca =canInvioConferma;
	
	String numKLoConfDid = null;
	String codiceFiscale = null;
	String entePromotore = null;
	String codiceEnteTit = null;
	String dataDichiarazione = null;
	String dataProfiling = null;
	String flgPIvaNonMovimentata = null;
	String sessoLav = null;
	String strSessoLav = null;
	String codiceCittadinanza = null;
	String codiceCittEstero = null;
	String dataNascita = null;
	String etaLav = null;
	String codTitoloStudio=null;
	String descrTitolo = null;
	String provinciaRes = null;
	String codProvincia = null;
	String codProvinciaDaInviare = null;
	String flgEspLav = null;
	String condOccupaz = null;
	String mesiDisocc = null;
	String codProf = null;
	String mesiRicLav = null;
	String codIscrCorso = null;
	String numNucleo = null;
	String flgFigliCarico = null;
	String flgFigliMinorenni = null;
	String descrStatoInvio = null;
	String indiceSvantaggio = null;
	String idProfiling = null;
	String condOccupaz_calc = null;
	String mesiDisocc_calc = null;
	
	if(!StringUtils.isEmpty(pageProvenienza)){
		isProvenienzaElenco = true;
	}
	
	boolean prevalorizzaData = false;
	if(serviceResponse.containsAttribute("M_ST_CONFIG_CONF_DID") && serviceResponse.getAttribute("M_ST_CONFIG_CONF_DID") !=null){
		SourceBean configurazione = (SourceBean) serviceResponse.getAttribute("M_ST_CONFIG_CONF_DID.ROWS.ROW");
		String valoreConfig =  configurazione.getAttribute("STRVALORE").toString();
		if(StringUtils.isFilledNoBlank(valoreConfig) && valoreConfig.equalsIgnoreCase("S")){
			prevalorizzaData = true;
		}
	}
	
	boolean esisteDidAttiva = false;
	SourceBean rowDidAttiva = (SourceBean)serviceResponse.getAttribute("M_CCD_GET_DID.ROWS.ROW");
	if (rowDidAttiva != null && rowDidAttiva.getAttribute("PRGDICHDISPONIBILITA") != null){
		esisteDidAttiva = true;
		flgPIvaNonMovimentata = StringUtils.getAttributeStrNotNull(rowDidAttiva, "FLGLAVOROAUTONOMO");
	}
	
	boolean isInserimento = true;
	boolean showProfiling = true;
	if(serviceResponse.containsAttribute("M_CCD_GET_Conferimento_Did_From_Prg") && serviceResponse.getAttribute("M_CCD_GET_Conferimento_Did_From_Prg") !=null){
		isInserimento = false;
		titolo = "Dettaglio Conferimento DID";
	}else{
		if(serviceRequest.containsAttribute("CODPFTIPOEVENTO")){ //inserimento o conferma o conferimento did sil
			codTipoEvento = serviceRequest.getAttribute("CODPFTIPOEVENTO").toString();
			if(codTipoEvento.equals(ConferimentoUtility.EVENTOCONVALIDA)){
				titolo = "Conferma Conferimento DID";
				messaggioProfiling = msgProfConvalida;
				if(serviceRequest.containsAttribute("dataDichNonPresente")){
					String dataDichNonPresente = serviceRequest.getAttribute("dataDichNonPresente").toString();
					if(StringUtils.isFilledNoBlank(dataDichNonPresente) && dataDichNonPresente.equalsIgnoreCase("NO")){
						if(serviceRequest.containsAttribute("dataDichSap")){
							String dataDichiarazioneSap = serviceRequest.getAttribute("dataDichSap").toString();
							if(StringUtils.isFilledNoBlank(dataDichiarazioneSap)){
								dataDichiarazione = dataDichiarazioneSap;
							}else{
								dataDichiarazione = DateUtils.getNow();
							}
						}
					}
					if(StringUtils.isFilledNoBlank(dataDichNonPresente) && dataDichNonPresente.equalsIgnoreCase("SI")){
						if(serviceRequest.containsAttribute("dataDichSapN00")){
							String dataDichSapN00 = serviceRequest.getAttribute("dataDichSapN00").toString();
							if(StringUtils.isFilledNoBlank(dataDichSapN00)){
								dataDichiarazione = dataDichSapN00;
							}else{
								dataDichiarazione = DateUtils.getNow();
							}
						}
					}
				}
			}else{
				messaggioProfiling = msgProfNuovo;
			}
			if(serviceRequest.containsAttribute("DATADICHIARAZIONEDIDSIL")){
				String dataDidSil = serviceRequest.getAttribute("DATADICHIARAZIONEDIDSIL").toString();
				if(StringUtils.isFilledNoBlank(dataDidSil)){
					dataDichiarazione = dataDidSil;
				}else{
					dataDichiarazione = DateUtils.getNow();
				}
			}
		}
		if(codTipoEvento.equals(ConferimentoUtility.EVENTOINSERIMENTO) && prevalorizzaData){
			dataDichiarazione = DateUtils.getNow();
		}
		codStatoInvio = ConferimentoUtility.STATODAINVIARE;
		
	}
	SourceBean dettaglioProfiling = (SourceBean) serviceResponse.getAttribute("M_CCD_GET_Conferimento_Did_From_Prg.ROWS.ROW");
 	if(!isInserimento && dettaglioProfiling != null){
        numKLoConfDid = dettaglioProfiling.getAttribute("NUMKLOCONFDID").toString();
        codiceEnteTit = dettaglioProfiling.getAttribute("CODENTETIT").toString();
        entePromotore = StringUtils.getAttributeStrNotNull(dettaglioProfiling, "entePromotore"); 
        codTipoEvento = dettaglioProfiling.getAttribute("CODPFTIPOEVENTO").toString();
        if(codTipoEvento.equals(ConferimentoUtility.EVENTOCONVALIDA)){
        	messaggioProfiling = msgProfConvalida;
        }else if(codTipoEvento.equals(ConferimentoUtility.EVENTOINSERIMENTO)){
        	messaggioProfiling = msgProfNuovo;
        }else{
        	messaggioProfiling = "";
        }
        dataDichiarazione = dettaglioProfiling.getAttribute("DATDID").toString();
        dataProfiling = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"DATPROFILING");
      
        codStatoInvio =   StringUtils.getAttributeStrNotNull(dettaglioProfiling,"CODMONOSTATOINVIO");
        descrStatoInvio =  StringUtils.getAttributeStrNotNull(dettaglioProfiling,"strCodMonoStatoInvio"); 
        if(dettaglioProfiling.containsAttribute("NUMETA")){
        	BigDecimal nEtaLav = (BigDecimal)dettaglioProfiling.getAttribute("NUMETA") ;
        	etaLav = String.valueOf(nEtaLav);
        }
		codiceCittadinanza = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"CODPFCITTADINANZA") ; 
		codiceCittEstero = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"CODPFPRESENZAIT") ; 
		codTitoloStudio = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"CODTITOLO"); 
		descrTitolo = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"descrTitolo");
		flgEspLav = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"FLGESPLAVORO"); 
		condOccupaz = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"CODPFCONDOCCUP");  
		if(dettaglioProfiling.containsAttribute("NUMMESIDISOCC")){
        	BigDecimal nMesiDisocc = (BigDecimal)dettaglioProfiling.getAttribute("NUMMESIDISOCC") ;
        	mesiDisocc = String.valueOf(nMesiDisocc);
        }
		codProf = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"CODPFPOSIZIONEPROF"); 
		if(dettaglioProfiling.containsAttribute("NUMMESIRICERCALAV")){
        	BigDecimal nMesiRicLav  = (BigDecimal)dettaglioProfiling.getAttribute("NUMMESIRICERCALAV") ;
        	mesiRicLav = String.valueOf(nMesiRicLav);
        }
		codIscrCorso = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"CODPFISCRCORSO"); 
		if(dettaglioProfiling.containsAttribute("NUMNUCLEOFAM")){
        	BigDecimal nNucleo  = (BigDecimal)dettaglioProfiling.getAttribute("NUMNUCLEOFAM") ;
        	numNucleo = String.valueOf(nNucleo);
        }
		flgFigliCarico = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"FLGFIGLIACARICO"); 
		flgFigliMinorenni = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"FLGFIGLIMINORENNI"); 
		if(dettaglioProfiling.containsAttribute("DECPROFILING")){
        	BigDecimal nIndice  = (BigDecimal)dettaglioProfiling.getAttribute("DECPROFILING") ;
        	indiceSvantaggio = String.valueOf(nIndice);
        }
		idProfiling = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"IDSPROFILING"); 
		condOccupaz_calc = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"CODPFCONDOCCUP_CALC"); 
		if(dettaglioProfiling.containsAttribute("NUMMESIDISOCC_CALC")){
        	BigDecimal nMesiCalc  = (BigDecimal)dettaglioProfiling.getAttribute("NUMMESIDISOCC_CALC") ;
        	mesiDisocc_calc = String.valueOf(nMesiCalc);
        }
		codProvinciaDaInviare = StringUtils.getAttributeStrNotNull(dettaglioProfiling, "codprovinciamin");
		codProvincia = StringUtils.getAttributeStrNotNull(dettaglioProfiling, "CODPROVINCIARES");
		provinciaRes = StringUtils.getAttributeStrNotNull(dettaglioProfiling, "provincia");
	}
	
	if(codStatoInvio.equals(ConferimentoUtility.STATODAINVIARE) && 
			(codTipoEvento.equals(ConferimentoUtility.EVENTOINSERIMENTO) || codTipoEvento.equals(ConferimentoUtility.EVENTOCONVALIDA))){
		canModify = true;
		isInvioRevoca = false;
		if(codTipoEvento.equals(ConferimentoUtility.EVENTOINSERIMENTO)){
			isInvioConferma = true;
			isInvioConvalida = false;
		}else{
			isInvioConferma = false;
			isInvioConvalida = true;
		}
	}else{
		canModify = false;
		isInvioConferma = false;
		isInvioConvalida = false;
		if(codStatoInvio.equals(ConferimentoUtility.STATODAINVIARE)){
			isInvioRevoca = true;
		}else{
			isInvioRevoca = false;
		}
	}
	
	SourceBean didlav  = (SourceBean) serviceResponse.getAttribute("M_Get_Info_Lavoratore.ROWS.ROW");
	dataNascita = StringUtils.getAttributeStrNotNull(didlav, "DATNASC");
	codiceFiscale = StringUtils.getAttributeStrNotNull(didlav, "STRCODICEFISCALE");
	sessoLav = StringUtils.getAttributeStrNotNull(didlav, "STRSESSO");
	if(sessoLav.equalsIgnoreCase("M")){
		strSessoLav = "M - Maschio";
	}else if(sessoLav.equalsIgnoreCase("F")){
		strSessoLav = "F - Femmina";
	}
	if(codStatoInvio.equals(ConferimentoUtility.STATODAINVIARE)){
		SourceBean provRes =(SourceBean) serviceResponse.getAttribute("M_CCD_PROVINCIA_RES.ROWS.ROW");
		if(provRes!=null){
			codProvinciaDaInviare = StringUtils.getAttributeStrNotNull(provRes, "codprovincia");
			codProvincia = StringUtils.getAttributeStrNotNull(provRes, "codprovinciasil");
			provinciaRes = StringUtils.getAttributeStrNotNull(provRes, "provincia");
		}
		SourceBean didEnte =(SourceBean) serviceResponse.getAttribute("M_CCD_ENTE_PROMOTORE_FROM_CDNLAV.ROWS.ROW");
		if (didEnte != null ) {
			codiceEnteTit = StringUtils.getAttributeStrNotNull(didEnte, "codice");
			entePromotore = codiceEnteTit;
			if(StringUtils.isFilledNoBlank(entePromotore)){
				entePromotore += " - ";
			}
			entePromotore += StringUtils.getAttributeStrNotNull(didEnte, "descrizione"); 
		}
	}
	if(isInserimento){
		if(StringUtils.getAttributeStrNotNull(didlav, "CODCITTADINANZA").equals("000")){
			codiceCittEstero = "A01";
			codiceCittadinanza = "A02";
		}else{
			SourceBean estero = (SourceBean) serviceResponse.getAttribute("M_CCD_NASCITA_ESTERO.ROWS.ROW");
			String nascitaEstero = StringUtils.getAttributeStrNotNull(estero, "codiceEstero");
			if(!nascitaEstero.equals("99")){
				codiceCittEstero = "A02";
			}
			if(StringUtils.getAttributeStrNotNull(didlav, "FLGCEE").equals("S")){
				codiceCittadinanza ="A01";
			}else{
				codiceCittadinanza = "A03";
			}
		}
		SourceBean ultimoTitolo = (SourceBean) serviceResponse.getAttribute("M_GetPrincTitolo.ROWS.ROW");
		if (ultimoTitolo != null ) {
			if(StringUtils.getAttributeStrNotNull(ultimoTitolo, "Flgpfconfdid").equals("S")){
				codTitoloStudio = StringUtils.getAttributeStrNotNull(ultimoTitolo, "codtitolo");
				descrTitolo = StringUtils.getAttributeStrNotNull(ultimoTitolo, "strdescrizione");
			}
		}
	}
	 
	// Sola lettura: viene usato per tutti i campi di input
	String readOnlyData = "false"; 
	if(codTipoEvento.equals(ConferimentoUtility.EVENTOREVOCA)){
		canModify = false;
		showProfiling = false;
		readOnlyData = "true";
	}
	if(codTipoEvento.equals(ConferimentoUtility.EVENTOCONVALIDA) || codStatoInvio.equals(ConferimentoUtility.STATOINVIATO) || serviceRequest.containsAttribute("DATADICHIARAZIONEDIDSIL") ){
		readOnlyData = "true";
	}
	String readonly = String.valueOf(!canModify);
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	 
%>

<html>
<head>
   <script type="text/javascript"  src="../../js/script_comuni.js">
  </script>
<title><%= titolo %></title>
  	<link rel="stylesheet" type="text/css" href="../../css/stili.css">
  	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  	<link rel="stylesheet" type="text/css" href="../../css/stiliTemplate.css"/>
  	<link rel="stylesheet" type="text/css" href="../../css/jqueryui/jquery-ui.min.css">
  	<link rel="stylesheet" type="text/css" href="../../css/jqueryui/jqueryui-sil.css">
  	

<af:linkScript path="../../js/"/>
  <script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
  <script src="../../js/jqueryui/jquery-ui.min.js"></script>
<%-- INCLUDERE QUI ALTRI JAVASRIPT CON CLAUSOLE DEL TIPO:
<script language="Javascript" src="../../js/xxx.js"></script>
--%>

<script language="Javascript">
var contextPath = "<%=request.getContextPath()%>";
$(function() {
$( "[name='strTitolo']" ).autocomplete({
	//width: 300,
    max: 10,
    delay: 100,
    minLength: 3,
    autoFocus: true,
    cacheLength: 1,
    scroll: true,
    highlight: true,
    
	source: function(request, response) {
		
        $.ajax({
            url: contextPath + "/services/autocompleteServletComponent?prefixQueryName=TITOLOSTUDIOCONFDID",
            dataType: "json",
            data: request,
            success: function( data, textStatus, jqXHR) {
              //  console.log( data);
             var items = data.matchingItems;
             if(items.length <=0){
            	 items = new Array();
            	 var noResult ={id: "", value:""};
            	 items[0]=noResult;
            	 $( "[name='codTitolo']" ).val(null);
            	 
             }
                response(items);
                
             },
            error: function(jqXHR, textStatus, errorThrown){
                 console.log( textStatus);
            }
    
        });
    },
    select: function(event, ui) {
      
    	$( "[name='codTitolo']" ).val(ui.item.id);
    }
   
  });
  
$( "[name='strTitolo']" ).on( "keydown", function( event ) {
	  switch( event.keyCode ) {
	    case $.ui.keyCode.BACKSPACE:
	      $( "[name='codTitolo']" ).val(null);
	      break;
	    case $.ui.keyCode.DELETE:
	      $( "[name='codTitolo']" ).val(null);
	      break;
	    case $.ui.keyCode.SPACE:
		  $( "[name='codTitolo']" ).val(null);
		  break;
	  }
	});
  
});
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
	 	controlliDataDid();
	}
	  var flagChanged = false;  

    function fieldChanged(objName) {
    	<%if (canModify) {
        	out.print("flagChanged = true;");
     	}
    	%>
    	if ( typeof(objName) == "undefined" || objName == null ) {
    		document.Frm1.DATDID.focus();
    	}
  	}
	
	function goToCruscottoDid(){
		if (isInSubmit()) return;
		
		<%if(isProvenienzaElenco){%>
    		url="AdapterHTTP?PAGE=<%=pageProvenienza%>";
    		url += "&dataDichDid="+"<%=dataDichiarazione%>";
    	<%} else{%>
    		url="AdapterHTTP?PAGE=SitAttualeConfDIDPage";
	    <%} %>
	    
	      url += "&CDNFUNZIONE="+"<%=_funzione%>";      
	      url += "&cdnLavoratore="+"<%=cdnLavoratore%>";     
 	      setWindowLocation(url);
	}
	
	
	function validateIntegerPositive(inputName) {
		var ctrlObj = eval("document.forms[0]." + inputName);

	  if (ctrlObj.value == "")
	    return true;    // va bene se non ho inserito nulla
	  else {
	    var ok = isInteger(ctrlObj.value);
	    ok = ok && ( parseInt(ctrlObj.value) > 0);

	    if (!ok) {
	      alert("Numero non corretto nel campo " + ctrlObj.title + " (inserire valori interi positivi)");
	      ctrlObj.focus();
	    }
	    return ok;
	  }
	}

	function validateIntegerPositiveOZero(inputName) {
	  var ctrlObj = eval("document.forms[0]." + inputName);

	  if (ctrlObj.value == "")
	    return true;    // va bene se non ho inserito nulla
	  else {
	    var ok = isInteger(ctrlObj.value);
	    ok = ok && ( parseInt(ctrlObj.value) >= 0);

	    if (!ok) {
	      alert("Numero non corretto nel campo " + ctrlObj.title + " (inserire valori interi maggiori o uguali a zero)");
	      ctrlObj.focus();
	    }
	    return ok;
	  }
	}
	
	function automatismiCampi(inputName){
		
		 var ctrlObj = eval("document.forms[0]." + inputName);
		 
		 if(inputName == "FLGESPLAVORO"){
			  if (ctrlObj.value == "N"){
				  //document.Frm1.CODPFCONDOCCUP.value="A01";
				  document.Frm1.NUMMESIDISOCC.value=0;
				  document.Frm1.CODPFPOSIZIONEPROF.value="NO";
			  }
			  return;
		 }else 
		 if(inputName == "NUMNUCLEOFAM"){
			 if (ctrlObj.value == 1){
				 document.Frm1.FLGFIGLIACARICO.value="N";
				 document.Frm1.FLGFIGLIMINORENNI.value="N";
			 }
			 return;
		 }else 
		 if(inputName == "FLGFIGLIACARICO"){
			 if (ctrlObj.value == "N"){
 				 document.Frm1.FLGFIGLIMINORENNI.value="N";
			 }
			 return;
		 }else 
		return;
	}
	
	function checkAllRequired(){
		if(!isRequired('NUMETA') || !validateIntegerPositive('NUMETA') ){
			  return false;	
	    }
		if(!isRequired('CODPFCITTADINANZA')){
			  return false;	
	    }
		if(!isRequired('CODPFPRESENZAIT')){
			  return false;	
	    }
		if(!isRequired('codTitolo')){
			  return false;	
	    }
		if(!isRequired('FLGESPLAVORO')){
			  return false;	
	    }
		if(!isRequired('CODPFCONDOCCUP')){
			  return false;	
	    }
		if(!isRequired('NUMMESIDISOCC') || !validateIntegerPositiveOZero('NUMMESIDISOCC') ){
			  return false;	
	    }
		if(!isRequired('CODPFPOSIZIONEPROF')){
			  return false;	
	    }
		if(!isRequired('NUMMESIRICERCALAV') || !validateIntegerPositiveOZero('NUMMESIRICERCALAV') ){
			  return false;	
	    }
		if(!isRequired('CODPFISCRCORSO')){
			  return false;	
	    }
		if(!isRequired('NUMNUCLEOFAM') || !validateIntegerPositive('NUMNUCLEOFAM') ){
			  return false;	
	    }
		if(!isRequired('FLGFIGLIACARICO')){
			  return false;	
	    }
		if(!isRequired('FLGFIGLIMINORENNI')){
			  return false;	
	    }
		return true;
	}
	
	function checkCampiAggiuntiva() {
		var revoca = "<%= ConferimentoUtility.EVENTOREVOCA%>";
		var convalida = "<%= ConferimentoUtility.EVENTOCONVALIDA%>";
		var inserimento = "<%= ConferimentoUtility.EVENTOINSERIMENTO%>";
		if(document.Frm1.CODPFTIPOEVENTO.value == revoca){
			return true;
		}
		var dataDid = document.Frm1.DATDID.value;
		var dataDicembre ="04/12/2017";
		var checkObbl = confrontaDate(dataDicembre, dataDid);
		var controllaFlg = false;	
		if(document.Frm1.CODPFTIPOEVENTO.value == inserimento){
			// campi obbligatori data dichiarazione >= 04/12/2017
			if(checkObbl >= 0 ){
				document.Frm1.EMPTY.value = "NO";
				document.Frm1.FLGVARIABILIPROFILING.value ="S";
				return checkAllRequired();
			}else{
				controllaFlg = true;
			}
		}
		if(controllaFlg || document.Frm1.CODPFTIPOEVENTO.value == convalida){
			if(!isRequired('FLGVARIABILIPROFILING')){
				  return false;	
		    }else{
		    	if(document.Frm1.FLGVARIABILIPROFILING.value == "S"){
		    		document.Frm1.EMPTY.value = "NO";
		    		return checkAllRequired();
		    	}else if(document.Frm1.FLGVARIABILIPROFILING.value == "N"){
		    		document.Frm1.EMPTY.value = "OK";
					return true;
		    	}
		    }
		}
	}

	function controlliDataDid() {
		var inserimento = "<%= ConferimentoUtility.EVENTOINSERIMENTO%>";
		var convalida = "<%= ConferimentoUtility.EVENTOCONVALIDA%>";
		var dataDid = document.Frm1.DATDID.value;
		var dataNascitaLav = "<%=dataNascita%>";
		var eta;
		var dataOdiernaCalcolo = "<%=DateUtils.getNow()%>";
		eta = calcAge(dataNascitaLav, dataOdiernaCalcolo);
		document.Frm1.NUMETA.value = eta;
		var div = document.getElementById("sezioneFlgProfiling");
		// campi obbligatori data dichiarazione >= 04/12/2017
		// fino al 3 dicembre 2017 mostro il radio button
		if (document.Frm1.CODPFTIPOEVENTO.value == inserimento) { 
			var dataDicembre ="04/12/2017";
			var checkObbl = confrontaDate(dataDicembre, dataDid);
			if(checkObbl < 0 ){
				div.style.display = "";
			}else{
				document.Frm1.FLGVARIABILIPROFILING.value = 'S';
				div.style.display = "none";
			}
		}else if(document.Frm1.CODPFTIPOEVENTO.value == convalida){
			div.style.display = "";
		}
			
	  }
	
	function controlloVisSezProfiling() {
		var inserimento = "<%= ConferimentoUtility.EVENTOINSERIMENTO%>";
		var convalida = "<%= ConferimentoUtility.EVENTOCONVALIDA%>";
		var dataDid = document.Frm1.DATDID.value;
		var div = document.getElementById("sezioneFlgProfiling");
		// campi obbligatori data dichiarazione >= 04/12/2017
		// fino al 3 dicembre 2017 mostro il radio button
		if (document.Frm1.CODPFTIPOEVENTO.value == inserimento) { 
			var dataDicembre ="04/12/2017";
			var checkObbl = confrontaDate(dataDicembre, dataDid);
			if(checkObbl < 0 ){
				div.style.display = "";
			}else{
				document.Frm1.FLGVARIABILIPROFILING.value = 'S';
				div.style.display = "none";
			}
		}else if(document.Frm1.CODPFTIPOEVENTO.value == convalida){
			div.style.display = "";
		}
			
	  }

	 function isDate(dateStr) {
	    var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
	    var matchArray = dateStr.match(datePat); // is the format ok?

	    if (matchArray == null) {
	      return false;
	    }

	    month = matchArray[3]; // p@rse date into variables
	    day = matchArray[1];
	    year = matchArray[5];

	    if (month < 1 || month > 12) { // check month range
	      return false;
	    }

	    if (day < 1 || day > 31) {
	      return false;
	    }

	    if ((month==4 || month==6 || month==9 || month==11) && day==31) {
	      return false;
	    }

	    if (month == 2) { // check for february 29th
	      var isleap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
	      if (day > 29 || (day==29 && !isleap)) {
	        return false;
	      }
	    }

	    return true; // date is valid
	  }

	 function controlliDataDidManuale() {
		var inserimento = "<%= ConferimentoUtility.EVENTOINSERIMENTO%>";
		var convalida = "<%= ConferimentoUtility.EVENTOCONVALIDA%>";
		var dataDid = document.Frm1.DATDID.value;
		var dataDidControllo=new String(dataDid);
		if (!isDate(dataDidControllo)) {
      		return;
		}
		var dataNascitaLav = "<%=dataNascita%>";
		var eta;
		var dataOdiernaCalcolo = "<%=DateUtils.getNow()%>";
		eta = calcAge(dataNascitaLav, dataOdiernaCalcolo);
		document.Frm1.NUMETA.value = eta;
		var div = document.getElementById("sezioneFlgProfiling");
		// campi obbligatori data dichiarazione >= 04/12/2017
		// fino al 3 dicembre 2017 mostro il radio button
		if (document.Frm1.CODPFTIPOEVENTO.value == inserimento) { 
			var dataDicembre ="04/12/2017";
			var checkObbl = confrontaDate(dataDicembre, dataDid);
			if(checkObbl < 0 ){
				div.style.display = "";
			}else{
				document.Frm1.FLGVARIABILIPROFILING.value = 'S';
				div.style.display = "none";
			}
		}else if(document.Frm1.CODPFTIPOEVENTO.value == convalida){
			div.style.display = "";
		}
			
	}

	    
	 function calcAge (birthday, endDay) {
		 var parts = birthday.split("/");
		 var timestamp = new Date(parts[2], parts[1] - 1, parts[0]);
		 var partsD = endDay.split("/");
		 var today = new Date(partsD[2], partsD[1] - 1, partsD[0]);
		 var age = today.getFullYear() - timestamp.getFullYear();
		 var m = today.getMonth() - timestamp.getMonth();
		 if (m < 0 || (m === 0 && today.getDate() < timestamp.getDate())) {
		 	age--;
		 }
		 return age;
	}
	 
<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
%>

</script>
</head>

<body class="gestione" onload="onLoad()">

<%
	// TESTATA LAVORATORE
	if (StringUtils.isFilled(cdnLavoratore)) {
		InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
		testata.show(out);
	}

%>
    <script language="Javascript">
      	if(window.top.menu != undefined){
    	    window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
    	}
    </script>	


<af:form name="Frm1" action="AdapterHTTP" method="POST" onSubmit="checkCampiAggiuntiva()">
<p class="titolo"><%= titolo %></p>

	<input type="hidden" name="PAGE" value="EsitoConferimentoDIDPage"/>
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" />
	<input type="hidden" name="PRGCONFERIMENTODID" value="<%=Utils.notNull(prgConferimentoDID)%>" />
	<input type="hidden" name="NUMKLOCONFDID" value="<%=Utils.notNull(numKLoConfDid)%>" />
	<input type="hidden" name="CODPFTIPOEVENTO" value="<%=Utils.notNull(codTipoEvento)%>" />
 	<input type="hidden" name="CODENTETIT" value="<%=Utils.notNull(codiceEnteTit)%>">  
 	<input type="hidden" name="CODMONOSTATOINVIO" value="<%=Utils.notNull(codStatoInvio)%>"> 
 	<input type="hidden" name="CODPROVINCIARES" value="<%=Utils.notNull(codProvincia) %>" />
 	<input type="hidden" name="CODPROVINCIAMINRES" value="<%=Utils.notNull(codProvinciaDaInviare) %>" />
  	<input type="hidden" name="STRSESSO" value="<%=Utils.notNull(sessoLav) %>" /> 
 	<input type="hidden" name="EMPTY" /> 
 
 	
<%= htmlStreamTop %>
<table class="main">
<tr> 
    <td class="etichetta"> 
      Codice Fiscale
    </td>
    <td class="campo">    
      <af:textBox name="CODICEFISCALE" value="<%=Utils.notNull(codiceFiscale)%>" classNameBase="input"  
                  readonly="true" required="true" title="Codice Fiscale"  
                  size="20" />    
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
<tr>
	<td nowrap class="etichetta"> 
      Codice Ente Promotore
    </td>
    <td nowrap class="campo">    
      <af:textBox name="ENTEPROMOTORE" value="<%=Utils.notNull(entePromotore)%>" classNameBase="input"  
                  readonly="true" required="true" title="Codice Ente Promotore"  
                  size="40"/>
       
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
<tr>
    <td  nowrap class="etichetta"> 
      Data Dichiarazione
    </td>
    <td class="campo">   
      <af:textBox name="DATDID" value="<%=Utils.notNull(dataDichiarazione)%>" 
      		classNameBase="input" type="date"
      		validateOnPost="true" onBlur="checkFormatDate(this);controlloVisSezProfiling();"
           	readonly="<%= readOnlyData %>" required="true" title="Data Dichiarazione"  
           	size="11" maxlength="12" onKeyUp="fieldChanged(this);"/>    
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
<tr>
    <td  nowrap class="etichetta"> 
      Dich. P.Iva non movimentata
    </td>
    <td class="campo">
     <af:comboBox name="flgLavoroAutonomo" size="1"
               title="Dich. P.Iva non movimentata" required="false"
               moduleName="M_GenericComboOptionSi"
               selectedValue="<%=Utils.notNull(flgPIvaNonMovimentata)%>"
               addBlank="true" disabled="<%= String.valueOf(esisteDidAttiva)%>"
               classNameBase="input">
  	 </af:comboBox> 
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
<%if(!isInserimento){ %>
<tr> 
    <td class="etichetta"> 
     Tipo Conferimento
    </td>
    <td class="campo">    
       <af:comboBox name="descrTipoEvento" classNameBase="input"
							moduleName="M_CCD_COMBO_MN_PF_TIPO_EVENTO" 
							selectedValue="<%=Utils.notNull(codTipoEvento)%>" 
							addBlank="true"
							title ="Tipo Conferimento"
							disabled ="true"
							required="false" />
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
<tr>
	<td nowrap class="etichetta"> 
      Stato Invio
    </td>
    <td nowrap class="campo">    
      <af:textBox name="descrStatoInvio" value="<%=Utils.notNull(descrStatoInvio)%>" classNameBase="input"  
                  readonly="true" required="true" title="Stato Invio"  
                  size="25"/>
       
	</td>
	<td colspan="2">&nbsp;</td>
</tr>

</table>

<table class="main">
<%}%>
<%if(showProfiling){ %>
<tr>
  <td colspan="4">
    <div class="sezione">Variabili di Profiling</div>
  </td>
</tr>  
<tr>
  <td colspan="4" class="titolo" style="font-style: italic; font-weight: bold; left : 4%!important; position:relative; text-align: left;">
     <%= messaggioProfiling %>
   </td>
</tr>  
<%if(canModify){ %>
<tr id="sezioneFlgProfiling"  style="display: none">
	<td  nowrap class="etichetta">Invio dati di profiling</td>
	<td class="campo">
		<af:comboBox name="FLGVARIABILIPROFILING" classNameBase="input"
						moduleName="M_ComboSiNo" 
						title="Invio dati di profiling"
						addBlank="true"
						disabled ="<%=readonly %>"
						required="true" />
	</td>
	<td colspan="2">&nbsp;</td>	
</tr>  

<%} %>
<tr> <td colspan="4">&nbsp;</td></tr>
	<tr>
		<td  nowrap class="etichetta">Et&agrave;</td>
		<td class="campo">
			<af:textBox name="NUMETA" type="number" classNameBase="input"
				title ="Età"
				value="<%=Utils.notNull(etaLav)%>"
				validateOnPost="true"
				readonly="<%= readonly %>" 
				size="3" maxlength="3"
				required="false"
				onKeyUp="fieldChanged(this)" />
		</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td  nowrap class="etichetta">Genere</td>
		<td class="campo">
			<af:textBox name="sessoLav" type="text" classNameBase="input"
				value="<%=Utils.notNull(strSessoLav)%>"
				readonly="true" 
				size="18" 
				required="false" 
				onKeyUp="fieldChanged(this)"/>
		</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td  nowrap class="etichetta">Cittadinanza</td>
		<td class="campo">
			<af:comboBox name="CODPFCITTADINANZA" classNameBase="input"
						moduleName="M_CCD_COMBO_MN_PF_CITTADINANZA" 
						selectedValue="<%=codiceCittadinanza%>" 
						addBlank="true"
						title="Cittadinanza"
						disabled ="<%=readonly %>"
						required="false" />
		</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td  nowrap class="etichetta">Durata pres. in Italia</td>
		<td class="campo">
			<af:comboBox name="CODPFPRESENZAIT" classNameBase="input"
						moduleName="M_CCD_COMBO_MN_PF_PRESENZAIT" 
						selectedValue="<%=codiceCittEstero%>" 
						title="Durata presenza in Italia"
						addBlank="true"
						disabled ="<%=readonly %>"
						required="false" />
		</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	
	<tr>
      <td class="etichetta" nowrap>Titolo di studio</td>
   
      <td class="campo" nowrap>
        	<%if(canModify){ %>
            <af:textBox  classNameBase="input" type="text" title="Titolo di studio" name="strTitolo" size="45" 
            	readonly="<%=readonly %>"  value="<%=Utils.notNull(descrTitolo) %>"    />
            <%}else{ %>
            <af:textArea name="strTitolo" cols="60"
					rows="2" title="Titolo di studio" readonly="<%=readonly %>" classNameBase="input"
					value="<%=Utils.notNull(descrTitolo) %>" />
            <%}%>
     </td>

      <td colspan="2">&nbsp;</td>	
    </tr>
    <tr>
      <td class="etichetta" nowrap>Codice Titolo</td>
   
      <td class="campo" nowrap>
        	<af:textBox classNameBase="input" 
        	type="text" readonly="true" name="codTitolo"  title="Codice titolo di studio" 
        	value="<%=Utils.notNull(codTitoloStudio) %>" />
     </td>

      <td colspan="2">&nbsp;</td>	
    </tr>
  <tr> 
    <td  nowrap class="etichetta"> 
     Provincia di Residenza
    </td>
    <td class="campo">   
      <af:textBox name="prRes" value="<%=Utils.notNull(provinciaRes)%>" classNameBase="input"  
                  readonly="true" required="false" title="Provincia di Residenza"  
                  size="50" />    
	</td>
	<td colspan="2">&nbsp;</td>	
	</tr>
	<tr>
 		<td  nowrap class="etichetta">Hai mai avuto un lavoro?</td>
		<td class="campo">
			<af:comboBox name="FLGESPLAVORO" classNameBase="input"
						moduleName="M_ComboSiNo" 
						selectedValue="<%= Utils.notNull(flgEspLav) %>" 
						title="Esperienza lavorativa"
						addBlank="true"
						disabled ="<%=readonly %>"
						required="false" 
						onChange="automatismiCampi('FLGESPLAVORO')"/>
		</td>
		<td colspan="2">&nbsp;</td>	
	</tr>
	<tr>
 		<td class="etichetta">Condizione occupazione un anno prima</td>
		<td class="campo">
			<af:comboBox name="CODPFCONDOCCUP" classNameBase="input"
						moduleName="M_CCD_COMBO_PF_OCCUP" 
						selectedValue="<%= Utils.notNull(condOccupaz) %>" 
						title="Condizione Occupazionale"
						addBlank="true"
						disabled ="<%=readonly %>"
						required="false" />
		</td>
		<td colspan="2">&nbsp;</td>	
	</tr>
	<tr>
		<td class="etichetta">Da quanti mesi ha concluso l'ultimo rapporto di lavoro</td>
		<td class="campo">
			<af:textBox name="NUMMESIDISOCC" 
				type="number" 
				classNameBase="input"
				value="<%= Utils.notNull(mesiDisocc) %>"
				validateOnPost="true"
				readonly="<%= readonly %>" 
				size="5"
				title="Numero mesi disoccupazione"
				required="false"
				onKeyUp="fieldChanged(this)" />
		</td>
		<td colspan="2">&nbsp;</td>	
	</tr>
	<tr>
 		<td class="etichetta">Posizione nella professione dell'ultima occupazione svolta</td>
		<td class="campo">
			<af:comboBox name="CODPFPOSIZIONEPROF" classNameBase="input"
						moduleName="M_CCD_COMBO_PF_POSIZIONE" 
						selectedValue="<%= Utils.notNull(codProf) %>" 
						addBlank="true"
						title = "Posizione professionale"
						disabled ="<%=readonly %>"
						required="false" />
		</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td class="etichetta">Da quanti mesi stai cercando lavoro</td>
		<td class="campo">
			<af:textBox name="NUMMESIRICERCALAV" 
				type="number" 
				classNameBase="input"
				value="<%= Utils.notNull(mesiRicLav) %>"
				validateOnPost="true"
				readonly="<%= readonly %>" 
				size="5"
				title ="Numero mesi ricerca lavoro"
				required="false"
				onKeyUp="fieldChanged(this)" />
		</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
 		<td class="etichetta">Attualmente &egrave; iscritto a scuola/universit&agrave; o corso di formazione professionale (IFP, IFTS, ITS)</td>
		<td class="campo">
			<af:comboBox name="CODPFISCRCORSO" classNameBase="input"
						moduleName="M_CCD_COMBO_PF_ISCRCORSO" 
						selectedValue="<%= Utils.notNull(codIscrCorso) %>" 
						addBlank="true"
						disabled ="<%=readonly %>"
						title = "Codice iscizione corsi"
						required="false" />
		</td>
		<td colspan="2">&nbsp;</td>	
	</tr>
	<tr>
		<td class="etichetta">Numero di componenti della famiglia</td>
		<td class="campo">
			<af:textBox name="NUMNUCLEOFAM" 
				type="number" 
				classNameBase="input"
				value="<%= Utils.notNull(numNucleo) %>"
				validateOnPost="true"
				readonly="<%= readonly %>" 
				size="5"
				title="Numero componenti famiglia"
				required="false"
				onKeyUp="fieldChanged(this)" 
				onBlur="automatismiCampi('NUMNUCLEOFAM')" />
		</td>
		<td colspan="2">&nbsp;</td>	
	</tr>
	<tr>
 		<td  nowrap class="etichetta">Presenza di figli coabitanti e/o a carico</td>
		<td class="campo">
			<af:comboBox name="FLGFIGLIACARICO" classNameBase="input"
						moduleName="M_ComboSiNo" 
						selectedValue="<%= Utils.notNull(flgFigliCarico) %>" 
						addBlank="true"
						title="Figli a carico"
						disabled ="<%=readonly %>"
						required="false" 
						onChange="automatismiCampi('FLGFIGLIACARICO')"/>
		</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
 		<td  nowrap class="etichetta">Presenza di figli coabitanti e/o a carico con meno di 18 anni</td>
		<td class="campo">
			<af:comboBox name="FLGFIGLIMINORENNI" classNameBase="input"
						moduleName="M_ComboSiNo" 
						selectedValue="<%= Utils.notNull(flgFigliMinorenni) %>" 
						title="Figli minorenni a carico"
						addBlank="true"
						disabled ="<%=readonly %>"
						required="false" />
		</td>
		<td colspan="2">&nbsp;</td>
</tr>
<%if(codStatoInvio.equals(ConferimentoUtility.STATOINVIATO)){ %>
		<tr>
	 		<td  nowrap class="etichetta">Indice di svantaggio</td>
			<td class="campo">
				<af:textBox name="DECPROFILING" 
				type="text" 
				classNameBase="input"
				value="<%= Utils.notNull(indiceSvantaggio) %>"
 				readonly="<%= readonly %>" 
				size="11"
				title="Indice di svantaggio"
				required="false"
				 />
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
	 		<td  nowrap class="etichetta">Data Profiling</td>
			<td class="campo">
				<af:textBox name="DATAPROFILING" 
				type="text" 
				classNameBase="input"
				value="<%= Utils.notNull(dataProfiling) %>"
 				readonly="true" 
				size="11"
				title="Data profiling"
				required="false"
				 />
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
	 		<td  nowrap class="etichetta">Identificativo Profiling</td>
			<td class="campo">
				<af:textBox name="IDSPROFILING" 
				type="text" 
				classNameBase="input"
				value="<%= Utils.notNull(idProfiling) %>"
 				readonly="<%= readonly %>" 
				size="5"
				title="Identificativo Profiling"
				required="false"
				/>
			</td>
			<td colspan="2">&nbsp;</td>
	</tr>
			<tr>
	 		<td  nowrap class="etichetta">Cond. Occupazionale 1 anno prima calc.</td>
			<td class="campo">
				<af:comboBox name="CODPFCONDOCCUP_CALC" classNameBase="input"
							moduleName="M_CCD_COMBO_PF_OCCUP" 
							selectedValue="<%= condOccupaz_calc %>" 
							addBlank="true"
							title="Cond. Occupazionale 1 anno prima calc"
							disabled ="<%=readonly %>"
							required="false" />
			</td>
			<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
	 		<td  nowrap class="etichetta">Durata disoccupazione calcolata</td>
			<td class="campo">
			<af:textBox name="NUMMESIDISOCC_CALC" 
				type="text" 
				classNameBase="input"
				value="<%= Utils.notNull(mesiDisocc_calc) %>"
 				readonly="<%= readonly %>" 
				size="5"
				title="Durata disoccupazione calcolata"
				required="false"
				/>
			</td>
			<td colspan="2">&nbsp;</td>
	</tr>
<%	} %>
<%} %>
	<tr>
			<%if(canInvioConferma && isInvioConferma){ %>
			<td colspan="4">
			<center>
				<input type="submit" class="pulsanti" name="invioConfermaDid" value="Invia Conferimento DID"  />
 			</center>
			</td>
			<%} %>
			<%if(canInvioConvalida && isInvioConvalida){ %>
			<td colspan="4">
			<center>
				<input type="submit" class="pulsanti" name="invioConvalidaDid" value="Invia Conferma DID"  />
 			</center>
			</td>
			<%} %>
			<%if(canInvioRevoca && isInvioRevoca){ %>
			<td colspan="4">
			<center>
				<input type="submit" class="pulsanti" name="invioRevocaDid" value="Invia Revoca DID"  />
 			</center>
			</td>
	
			<%} %>
		</tr>
			<%if(readonly.equalsIgnoreCase("true")){ %>
				<tr>
				<td colspan="4">
					<center>
						<input type="button" class="pulsanti" name="chiudi" value="Chiudi" onclick="goToCruscottoDid();" />
		 			</center>
				</td>
				</tr>
			<%} %>
		
		</table>
 
<%= htmlStreamBottom %>

</af:form>

</body>
</html>
