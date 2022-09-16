<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"	
	import="com.engiweb.framework.base.*,
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
<%@ include file="CM_CommonScripts.inc" %>

<%	
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspDettPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	
	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	String prgAzienda = serviceRequest.getAttribute("prgAzienda") == null? "" : (String)serviceRequest.getAttribute("prgAzienda");
	String prgUnita = serviceRequest.getAttribute("prgUnita") == null? "" : (String)serviceRequest.getAttribute("prgUnita");
	String prgProspettoInf = "";	
	String prgAzReferente = "";	
	String codiceFiscale = "";
	String pIva	= ""; 
	String ragSociale = "";
	String codProvincia = "";
	// in fase di inserimento viene selezionata la prov del SIL
	InfoProvinciaSingleton provincia = InfoProvinciaSingleton.getInstance(); 
	codProvincia = provincia.getCodice();
	// Prendo la config per Non Vedente e Masso Fisioterapista
	String resultConfigNonVedenti_MassoFisioterapista = serviceResponse.containsAttribute("M_GetConfig_NonVedenti_MassoFisioterapista.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfig_NonVedenti_MassoFisioterapista.ROWS.ROW.NUM").toString():"0";
	// Prendo la config per stato prospetto

    String configEtichette = serviceResponse.containsAttribute("M_GetConfigEtichette_PI.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigEtichette_PI.ROWS.ROW.NUM").toString():"0";
	
   	boolean checkProspetto2011 = serviceResponse.containsAttribute("M_Check_Prospetti_Inviati_Entro_Il_31122011.ROWS.ROW.PROSPETTO2011") && serviceResponse.getAttribute("M_Check_Prospetti_Inviati_Entro_Il_31122011.ROWS.ROW.PROSPETTO2011").toString().equalsIgnoreCase("TRUE")?true:false;
    
	String flgVerOperatore = "";
	String numannorifprospetto = "";
	String datconsegnaprospetto = "";		
	String codMonoStatoProspetto = "";
	String codMonoProv = "";
	String codMonoCategoria = "";
	String referente = "";
	String strTelefono = "";
	String strFax = "";
	String strEmail = "";
	String indirizzoLegale = "";
	String attivita = "";
	String ccnl = "";
	String datprimaassunzione = "";
	String datsecondaassunzione = "";
	String numoreccnl = "";
	String numDipendentiNazionale = "";
	//New Donisi 02/07/2010
	String numcentnonvedentiforza = "";	
	String nummassofisioterapistiforza = "";
	String numcentnonvedentiobbligo = "";
	String nummassofisioterapistiobbligo = "";
	
	String flg15dipendenti = "";
	
	String numDipendentiTot = "";
	BigDecimal numkloprospettoinf = new BigDecimal("0");
	String categoria = "";	
	String strNote = "";	
	String displayDateAss = "none";
	String visualizza_display 	= "none";	
		  
	Object cdnUtIns	= null;
	Object dtmIns = null;
	Object cdnUtMod = null;
	Object dtmMod = null;	
	boolean flag_insert = false;
	String pagina_back = null;
	InfCorrentiAzienda infCorrentiAzienda= null;	
	//INFORMAZIONI OPERATORE
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");	 
	Testata operatoreInfo 	= 	null;   
	Linguette l = null;
	
	// variabili per la protocollazione
	String docInOut   = "";
	String docRif     = "";
	String docTipo    = "";
	String docCodRif  = "";      
	String docCodTipo = "";
	BigDecimal numProtV     = null;
	BigDecimal numAnnoProtV = null;
	String dataOraProt      = "";
	boolean noButton = false; 
	String datProtV     = "";
	String oraProtV     = "";
	Vector rowsProt     = null;
	SourceBean rowProt  = null;
	String codStatoAtto = "NP";
	BigDecimal numAnnoProt = null;
	BigDecimal numProtocollo = null;
	String dataProt = "";
	String DatAcqRil = "";
	String DatInizio = "";
	String dataOdierna = "";
	String oraProt = "";
	Vector rowsDoc1     = null;
	SourceBean rowDoc1  = null;
	String codStatoAttoV = "";
	String prgDocumento="";
	boolean canSalvaStato=false;
	String codComunicazione = "";
	String codComunicazioneOrig = "";
	String codComunicazioneAnn = "";
	String flgCapoGruppo = "";
	String flgCapoGruppoEstera = "";
	String codFiscCapoGruppo = "";
	String flgCompetenza = "";
	String numBCArt18 = "";
	String numBCArt3 = "";
	
	boolean nuovoProspetto = !(serviceRequest.getAttribute("nuovo")==null || 
            ((String)serviceRequest.getAttribute("nuovo")).length()==0);
	
	if (nuovoProspetto){
		canSalvaStato = false;
	}
	else {
		canSalvaStato = true; 
	}
	
	SourceBean dett = (SourceBean) serviceResponse.getAttribute("CMProspDatiGeneraliModule.ROWS.ROW");
	
	if (dett != null) {
		// caso modifica	
		//prgAzienda = (String)serviceRequest.getAttribute("prgAzienda");
		//prgUnita = (String)serviceRequest.getAttribute("prgUnita");
		prgAzienda = dett.getAttribute("prgAzienda") == null? "" : ((BigDecimal)dett.getAttribute("prgAzienda")).toString();
		prgUnita = dett.getAttribute("prgUnita") == null? "" : ((BigDecimal)dett.getAttribute("prgUnita")).toString();
		flgVerOperatore = (String)dett.getAttribute("flgVerOperatore");
		prgAzReferente = dett.getAttribute("prgAzReferente")==null ? "" : ((BigDecimal)dett.getAttribute("prgAzReferente")).toString();
		prgProspettoInf = ((BigDecimal)dett.getAttribute("prgProspettoInf")).toString();
		codProvincia = (dett.getAttribute("codprovincia")).toString();
		numannorifprospetto = ((BigDecimal)dett.getAttribute("numannorifprospetto")).toString();
		datconsegnaprospetto = (String)dett.getAttribute("datconsegnaprospetto");
		codMonoStatoProspetto = (String)dett.getAttribute("codMonoStatoProspetto");
		codMonoProv = (String)dett.getAttribute("codMonoProv");
		codMonoCategoria = (String)dett.getAttribute("codMonoCategoria");
		referente = (String)dett.getAttribute("referente");
		strTelefono = (String)dett.getAttribute("strTelefono");
		strFax = (String)dett.getAttribute("strFax");   
		strEmail = (String)dett.getAttribute("strEmail");
		indirizzoLegale = (String)dett.getAttribute("indLegale");		
		attivita = (String)dett.getAttribute("attivita");	
		ccnl = (String)dett.getAttribute("ccnl");
		codComunicazione = (String)dett.getAttribute("codComunicazione");
		codComunicazioneOrig = (String)dett.getAttribute("codComunicazioneOrig");
		codComunicazioneAnn = dett.getAttribute("codComunicazioneAnn") == null ? "" : (String)dett.getAttribute("codComunicazioneAnn");
		numoreccnl = dett.getAttribute("numoreccnl") == null ? "" : ((BigDecimal)dett.getAttribute("numoreccnl")).toString();
		numDipendentiNazionale = dett.getAttribute("numDipendentiNazionale") == null ? "" : ((BigDecimal)dett.getAttribute("numDipendentiNazionale")).toString();
		//new Donisi 02/04/2010
		numcentnonvedentiforza = dett.getAttribute("numcentnonvedentiforza") == null ? "" : ((BigDecimal)dett.getAttribute("numcentnonvedentiforza")).toString();
		nummassofisioterapistiforza = dett.getAttribute("nummassofisioterapistiforza") == null ? "" : ((BigDecimal)dett.getAttribute("nummassofisioterapistiforza")).toString();
		numcentnonvedentiobbligo = dett.getAttribute("numcentnonvedentiobbligo") == null ? "" : ((BigDecimal)dett.getAttribute("numcentnonvedentiobbligo")).toString();
		nummassofisioterapistiobbligo = dett.getAttribute("nummassofisioterapistiobbligo") == null ? "" : ((BigDecimal)dett.getAttribute("nummassofisioterapistiobbligo")).toString();
		
		flg15dipendenti = dett.getAttribute("flg15dipendenti") == null? "" : (String)dett.getAttribute("flg15dipendenti");
		
		numDipendentiTot = dett.getAttribute("numDipendentiTot") == null ? "" : ((BigDecimal)dett.getAttribute("numDipendentiTot")).toString();
		numkloprospettoinf = dett.getAttribute("numkloprospettoinf") == null ? new BigDecimal("0") : (BigDecimal)dett.getAttribute("numkloprospettoinf");
		datprimaassunzione = (String)dett.getAttribute("datprimaassunzione");
		datsecondaassunzione = (String)dett.getAttribute("datsecondaassunzione");		
		strNote = (String)dett.getAttribute("strNote");
		codStatoAtto = (String)dett.getAttribute("codStatoAtto");
		DatAcqRil = (String)dett.getAttribute("DatAcqRil"); 
		DatInizio = (String)dett.getAttribute("DatInizio"); 
		docInOut = (String)dett.getAttribute("striodoc"); 
		docRif = (String)dett.getAttribute("strambitodoc"); 
		docTipo = (String)dett.getAttribute("strtipodoc"); 
		docCodRif = (String)dett.getAttribute("codambitodoc");     
		docCodTipo = (String)dett.getAttribute("codtipodoc");
		flgCapoGruppo = StringUtils.getAttributeStrNotNull(dett,"flgCapoGruppo");
		flgCapoGruppoEstera = StringUtils.getAttributeStrNotNull(dett,"flgCapoGruppoEstera");
		codFiscCapoGruppo = StringUtils.getAttributeStrNotNull(dett,"strCFAZCapoGruppo");
		numBCArt18 = dett.getAttribute("NUMBASECOMPUTOART18") == null ? "" :((BigDecimal)dett.getAttribute("NUMBASECOMPUTOART18")).toString();
		numBCArt3 = dett.getAttribute("NUMBASECOMPUTOART3") == null ? "" :((BigDecimal)dett.getAttribute("NUMBASECOMPUTOART3")).toString();
		flgCompetenza = StringUtils.getAttributeStrNotNull(dett,"flgCompetenza");
		
		if(codStatoAtto.equals("PR") || codStatoAtto.equals("AN")){
			if(codStatoAtto.equals("AN")){
  				noButton = true;
  				canSalvaStato = false;
  			}
			numProtV = SourceBeanUtils.getAttrBigDecimal(dett, "NUMPROTOCOLLO", null);
	   		numAnnoProtV = SourceBeanUtils.getAttrBigDecimal(dett, "numAnnoProt", null); 
	   		dataOraProt = StringUtils.getAttributeStrNotNull(dett,"dataOraProt");
	 		if (!dataOraProt.equals("")) {
	 			oraProtV = dataOraProt.substring(11,16);
	 			datProtV = dataOraProt.substring(0,10);
	 		}
		}
		
		cdnUtIns = dett.getAttribute("cdnUtIns");
		dtmIns = dett.getAttribute("dtmIns");
		cdnUtMod = dett.getAttribute("cdnUtMod");
		dtmMod = dett.getAttribute("dtmMod");
		 
		if ((!("").equals(datprimaassunzione) && datprimaassunzione != null) || (!("").equals(datsecondaassunzione) && datsecondaassunzione != null)) {
    		displayDateAss = "";
    	}		
    	
    	//LINGUETTE		
		if (prgProspettoInf != null || !("").equals(prgProspettoInf)){
			l = new Linguette( user,  cdnfunzione, _page, new BigDecimal(prgProspettoInf), codStatoAtto );
			l.setCodiceItem("PRGPROSPETTOINF");
		}				
  		
  		operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	}
	else {
		// caso inserimento		
		flag_insert = true;
		checkProspetto2011 = false;
		flgCompetenza = "S";
		// anno precedente
		//int annoPrec = (DateUtils.getAnno() - 1); 
		int annoPrec = (DateUtils.getAnno()); 
		numannorifprospetto = (new BigDecimal(annoPrec)).toString();
		
		SourceBean rowInfoDeDoc = (SourceBean) serviceResponse.getAttribute("M_GetInfoDeDoc_Prospetti.ROWS.ROW");
  		if(rowInfoDeDoc != null){ 
	    	docInOut   = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,"striodoc");
	  		docRif     = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,"strambitodoc");
	  		docTipo    = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,"strtipodoc");
	  		docCodRif  = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,"codambitodoc");      
	  		docCodTipo = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,"codtipodoc");
		}
	}
	
  	//info dell'unità aziendale	
	if( prgAzienda != null && prgUnita!=null && !prgAzienda.equals("") && !prgUnita.equals("") ) {	
  		infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda, prgUnita);   	
  		infCorrentiAzienda.setPaginaLista("CMProspListaPage");
  	}  		  	
		     	    	
	String url = "";
	String goBackTitle = "";
	if (infCorrentiAzienda.formatBackList(sessionContainer, "CMProspListaPage") == null) {
		url = "PAGE=CMProspRicercaPage&cdnfunzione="+cdnfunzione;
		if (prgAzienda!=null || !("").equals(prgAzienda)) {
			url+="&prgAzienda="+prgAzienda+"&prgUnita="+prgUnita;
		}
		goBackTitle = "Torna alla ricerca";
	} else if (prgAzienda==null || ("").equals(prgAzienda)) { 
		String token = "_TOKEN_" + "CMProspListaPage";
		url = (String) sessionContainer.getAttribute(token.toUpperCase());
		goBackTitle = "Torna alla lista";
	}
	
	PageAttribs attributi = new PageAttribs(user, "CMProspDettPage");	
	
	boolean canModify 		= 	false;
	boolean readOnlyStr     = 	false; 
			
	canModify =	attributi.containsButton("AGGIORNA");  
	
	if (("N").equalsIgnoreCase(codMonoStatoProspetto) || ("S").equalsIgnoreCase(codMonoStatoProspetto) 
		|| ("V").equalsIgnoreCase(codMonoStatoProspetto) || ("U").equalsIgnoreCase(codMonoStatoProspetto)
		|| ("N").equalsIgnoreCase(flgCompetenza)) {
    	canModify = false;
    }
	
	String fieldReadOnly = canModify ? "false" : "true";  	
	String strReadOnly = "false";
		    
    	
	if (flag_insert && canModify) {
		strReadOnly = "false";
	}	
	else {
		strReadOnly = "true";
	}
	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<title>Dettaglio Prospetto</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>

<script language="Javascript">
<% if (!prgAzienda.equals("") && !prgUnita.equals("")) { %>
	window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
<% } %>
</script>

<script language="javascript">	
	
	var flagChanged = false;  


	function fieldChanged() {
    	<%if (flag_insert || canModify) {out.print("flagChanged = true;");}%>
	}
	

	function mostra(id){
		var div = document.getElementById(id);
 		div.style.display="";
	}

	function nascondi(id){
		var div = document.getElementById(id);
 		div.style.display="none";
	}
  
	function visualizzaAssunzione(check) {
		var val = check.value;
		if (val == "C") {
			mostra("ASSUNZIONI");
		}
		else {		
			//document.Frm1.datprimaassunzione.value == "";   
			//document.Frm1.datsecondaassunzione.value == "";
			nascondi("ASSUNZIONI");
		}
	}


	function flag15lav() {

		var flg15dipendenti =  "<%=flg15dipendenti%>" ; 
		
		if (flg15dipendenti=="S"&&document.Frm1.codMonoCategoria.value!="C") { 
			alert("Il flag del passaggio a 15 dipendenti nella pagina delle autorizzazioni e la categoria di appartenenza dichiarata nei dati generali devono essere coerenti.");
			document.Frm1.codMonoCategoria.value="C";
			return false;
	  	} else {	  		
		    return true;	
		}
	}

	

	function checkDate(objData1, objData2) {

		strData1=objData1.value;
		strData2=objData2.value;
		
		//costruisco la data della richiesta
		d1giorno=parseInt(strData1.substr(0,2),10);
		d1mese=parseInt(strData1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero :P
		d1anno=parseInt(strData1.substr(6,4),10);
		data1=new Date(d1anno, d1mese, d1giorno);
		
		//costruisce la data di scadenza
		d2giorno=parseInt(strData2.substr(0,2),10);
		d2mese=parseInt(strData2.substr(3,2),10)-1;
		d2anno=parseInt(strData2.substr(6,4),10);
		data2=new Date(d2anno, d2mese, d2giorno);
  
  		ok=true;
  		if (data2 < data1) {
      		alert("La "+ objData2.title +" è precedente alla "+ objData1.title);
      		objData2.focus();
      		ok=false;
   		}
  		return ok;
	}

	function controllaCampi() {
		var numDip = parseInt(document.Frm1.numDipendentiNazionale.value);
		
		if (document.Frm1.prgAzienda.value == "") {
			alert("Attenzione:è necessario selezionare un'azienda!");
			return false;
		}

		<%if (checkProspetto2011) {%>
			if (document.Frm1.codMonoCategoria.value == "") {
				alert("Attenzione: è necessario indicare la fascia di appartenenza!");
				return false;
			}
			else if (document.Frm1.codMonoCategoria.value == "A") {
			    if (!isNaN(numDip)) {
					if (numDip < 51) {
						alert("Attenzione: incongruenza fra la fascia di appartenenza\r\ne il numero dei lavoratori indicati!");
						return false;
					}
				}			
			}		
			else if (document.Frm1.codMonoCategoria.value == "B") {
				 if (!isNaN(numDip)) {	
					if (numDip < 36 || numDip > 50) {
						alert("Attenzione: incongruenza fra la fascia di appartenenza\r\ne il numero dei lavoratori indicati!");
						return false;
					}
				}
			}
			else if (document.Frm1.codMonoCategoria.value == "C") {
				if (!isNaN(numDip)) {
					if (numDip < 15 || numDip > 35) {
						alert("Attenzione: incongruenza fra la fascia di appartenenza\r\ne il numero dei lavoratori indicati!");
						return false;
					}
				}
				//controllo date asssunzione 
				// 11/05/2007 dona: eliminato controllo
				//if (document.Frm1.datprimaassunzione.value == "") {
				//	alert("La data prima assunzione deve essere valorizzata per prospetti\r\ndi azienda di fascia C!");
				//	return false;
				//}	
				
				if (document.Frm1.datprimaassunzione.value != "" && document.Frm1.datsecondaassunzione.value != "") {
					if (checkDate(document.Frm1.datprimaassunzione, document.Frm1.datsecondaassunzione) == false) {
						return false;	
					}
				}
			}	
		<% } else {%>
			var numBcArt = parseInt(document.Frm1.numBaseComputoArt3.value);

			if (document.Frm1.codMonoCategoria.value == "") {
				if (!isNaN(numBcArt)) {
					if (numBcArt >= 15) {
						alert("Attenzione: è necessario indicare la fascia di appartenenza!");
						return false;
					}	
				}	
			}
			else {
				if (document.Frm1.codMonoCategoria.value == "A") {
					if (!isNaN(numBcArt)) {
						if (numBcArt < 51) {
							alert("Attenzione: incongruenza fra la fascia di appartenenza\r\ne la base computo nazionale!");
							return false;
						}	
					}
				}
				else {
					if (document.Frm1.codMonoCategoria.value == "B") {
						if (!isNaN(numBcArt)) {	
							if (numBcArt < 36 || numBcArt > 50) {
								alert("Attenzione: incongruenza fra la fascia di appartenenza\r\ne la base computo nazionale!");
								return false;
							}
						}
					}
					else {
						if (document.Frm1.codMonoCategoria.value == "C") {
							if (!isNaN(numBcArt)) {
								if (numBcArt < 15 || numBcArt > 35) {
									alert("Attenzione: incongruenza fra la fascia di appartenenza\r\ne la base computo nazionale!");
									return false;
								}
							}
							if (document.Frm1.datprimaassunzione.value != "" && document.Frm1.datsecondaassunzione.value != "") {
								if (checkDate(document.Frm1.datprimaassunzione, document.Frm1.datsecondaassunzione) == false) {
									return false;	
								}
							}
						}
					}
				}
			}
		<%}%>
						
		if (!controllaFixedFloat('numoreccnl', 7, 3)) {			
			return false;
		}
		
		if (document.Frm1.flgCapoGruppo.value == "S" && document.Frm1.strCFAZCapoGruppo.value == "") {
			alert("Attenzione:il campo codice fiscale capogruppo deve essere valorizzato quando il prospetto è stato presentato dal capogruppo!");
			return false;	
		}
		
		if ( (document.Frm1.flgCapoGruppoEstera.value == "S" || document.Frm1.flgCapoGruppoEstera.value == "N") && 
			 (document.Frm1.strCFAZCapoGruppo.value == "") ) {
			alert("Attenzione:il campo codice fiscale capogruppo deve essere valorizzato quando si indica se l'azienda capogruppo è un'azienda estera oppure no!");
			return false;	
		}
		
		if ((document.Frm1.flgCapoGruppoEstera.value == "" || document.Frm1.flgCapoGruppoEstera.value == "N") &&
				document.Frm1.strCFAZCapoGruppo.value != "") {
			if (checkCFAz("strCFAZCapoGruppo") == false) {
				return false;
			}
		}
		
		return true;      
	}
	
</script>
<script language="Javascript">

	var imgChiusa = "../../img/chiuso.gif";
	var imgAperta = "../../img/aperto.gif";	
		  
	function cambia(immagine, sezione) {
		if (sezione.aperta==null) {
			sezione.aperta=true;
		}
		if (document.Frm1.strRagioneSociale.value!="" || document.Frm1.strRagioneSociale.value != "" || 
			document.Frm1.strRagioneSociale.value!= "" ){
			if (sezione.aperta) {
				sezione.style.display="inline";
				sezione.aperta=false;
				immagine.src="../../img/aperto.gif";
			}
			else {
				sezione.style.display="none";
				sezione.aperta=true;
				immagine.src="../../img/chiuso.gif";
			}
		}
	}	
	
	function Sezione(sezione, img,aperta){ 
		this.sezione=sezione;
	   	this.sezione.aperta=aperta;
	   	this.img=img;
	}
		
	function apriAzienda() {
    	var url = "AdapterHTTP?PAGE=ProspettiSelezionaAziendaPage&AGG_FUNZ=riempiDatiDet&cdnFunzione=<%=cdnfunzione%>";
       	var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
				   "width=600,height=500,top=50,left=100";
    	opened = window.open(url, "_blank", feat);
    	opened.focus();
	}
	
		
	function riempiDatiDet(prgAzienda, prgUnita, strCodiceFiscale, strPartitaIva, strRagioneSociale, numoreccnl) {			
		document.Frm1.prgAziendaApp.value = prgAzienda;
		document.Frm1.prgAzienda.value = prgAzienda;
		document.Frm1.prgUnita.value = prgUnita;
		document.Frm1.strCodiceFiscale.value = strCodiceFiscale;
   		document.Frm1.strPartitaIva.value = strPartitaIva;
		document.Frm1.strRagioneSociale.value = strRagioneSociale;
		document.Frm1.numoreccnl.value = numoreccnl;
		var imgV = document.getElementById("tendinaAzienda");
		var divVar = document.getElementById("aziendaSez");
		divVar.style.display = "inline";
		imgV.src=imgAperta;
		opened.close();
	}
	
	function ripristina(){
		if (document.Frm1.prgAziendaApp.value == null || document.Frm1.prgAziendaApp.value == "") {		   
			var sezione = document.getElementById("aziendaSez");
			var image = document.getElementById("tendinaAzienda");
	   		sezione.style.display="none";
	   		image.src="../../img/chiuso.gif";
		}	   
   	}
   	
   		
   	function azzeraAzienda (){
   		document.Frm1.prgAziendaApp.value = '';
   		document.Frm1.prgAzienda.value = '';
   		document.Frm1.prgUnita.value = '';
   		document.Frm1.strCodiceFiscale.value = '';
   		document.Frm1.strPartitaIva.value = '';
   		document.Frm1.strRagioneSociale.value = '';
		var imgV = document.getElementById("tendinaAzienda");
		var divVar = document.getElementById("aziendaSez");
		divVar.style.display = "none";
		imgV.src=imgChiusa;
	}
	
	function apriRef() {
    	var url = "AdapterHTTP?PAGE=ProspettiSelezionaReferentePage&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&AGG_FUNZ=riempiDatiRef&cdnFunzione=<%=cdnfunzione%>";
       	var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
				   "width=600,height=500,top=50,left=100";	   
    	opened = window.open(url, "_blank", feat);
    	opened.focus();
	}
	
	function riempiDatiRef(prgAzReferente, strReferente, strTelefono, strFax, strEmail) {
		document.Frm1.prgAzReferente.value = prgAzReferente;
		document.Frm1.strReferente.value = strReferente;
		document.Frm1.strTelefono.value = strTelefono;
		document.Frm1.strFax.value = strFax;
		document.Frm1.strEmail.value = strEmail;
		opened.close();
	}
	
</script>
</head>

<body class="gestione" onload="rinfresca()">

<%
if(infCorrentiAzienda != null) {
%>
	<div id="infoCorrAz" style="display:"><%infCorrentiAzienda.show(out); %></div>
<%
}
if (!flag_insert) {
	l.show(out);
}
%>

<p class="titolo">Dati generali del Prospetto</p>

<center>
	<font color="green">
		<af:showMessages prefix="CMProspDatiGenInsModule"/>
		<af:showMessages prefix="CMProspDatiGenUpdModule"/>
		<af:showMessages prefix="CMAnnullaRiepilogoModule"/>
    </font>
</center>
<center>
	<font color="red"><af:showErrors /></font>
</center>
  

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaCampi()">
<input type="hidden" name="PAGE" value="CMProspDettPage"/>
<input type="hidden" name="MODULE" value="<%= flag_insert ? "CMProspDatiGenInsModule" : "CMProspDatiGenUpdModule" %>"/>
<input type="hidden" name="prgAzReferente" value="<%=prgAzReferente%>" />
<input type="hidden" name="prgAziendaApp" value="" />
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>"/>
<input type="hidden" name="prgUnita" value="<%=prgUnita%>"/>
<input type="hidden" name="prgProspettoInf" value="<%=prgProspettoInf%>"/>
<input type="hidden" name="numkloprospettoinf" value="<%=numkloprospettoinf%>"/>  
<input type="hidden" name="cdnfunzione" value="<%=cdnfunzione%>"/>
<input type="hidden" name="DatAcqRil" value="<%=DatAcqRil%>" />
<input type="hidden" name="DatInizio" value="<%=DatInizio%>" />
<input type="hidden" name="dataOdierna" value="<%=dataOdierna%>"/>
<input type="hidden" name="FlgCodMonoIO" value="<%=docInOut%>"/>
<input type="hidden" name="flgCompetenza" value="<%=flgCompetenza%>"/>
<%if (checkProspetto2011) { %>
<input type="hidden" name="flgCapoGruppo" value=""/>
<input type="hidden" name="flgCapoGruppoEstera" value=""/>
<input type="hidden" name="strCFAZCapoGruppo" value=""/>
<input type="hidden" name="numBaseComputoArt3" value=""/>
<input type="hidden" name="numBaseComputoArt18" value=""/>
<%}%>
<%out.print(htmlStreamTop);%>

<table class="main" border="0">

	<%@ include file="_protocollazioneProspetti.inc" %>
	
	<script>
	
	function aggiornaDocumento(){
		var f;
 		var codStatoAtto = "<%=codStatoAtto%>";
 		
 		if (document.Frm1.codStatoAtto.value == codStatoAtto){
	 		alert("Stato atto non modificato");
			return false;
	 	}
	 	
	 	if (document.Frm1.codMonoStatoProspetto.value == 'A' &&
	 		document.Frm1.codStatoAtto.value == 'PR') {
	 		alert("Impossibile protocollare un prospetto in corso d'anno");
	 		document.Frm1.codStatoAtto.value = codStatoAtto;
	 		document.Frm1.numAnnoProt.value = '';
	 		document.Frm1.numProtocollo.value = '';
	 		document.Frm1.dataProt.value = '';
	 		document.Frm1.oraProt.value = '';
	 		return false;
	 	}
	 	
	 	f = "AdapterHTTP?PAGE=CMProspDettPage&aggiornaDoc=1";
 		f = f + "&CDNFUNZIONE=<%=cdnfunzione%>";
		f = f + "&prgProspettoInf=<%=prgProspettoInf%>";
		f = f + "&prgAzienda=<%=prgAzienda%>";
		f = f + "&prgUnita=<%=prgUnita%>";
		f = f + "&codStatoAtto=" + document.Frm1.codStatoAtto.value;
		f = f + "&canModify=<%=canModify%>";
	    f = f + "&oraProt=" +document.Frm1.oraProt.value;
	    f = f + "&dataProt="+document.Frm1.dataProt.value;
	    f = f + "&numProtocollo="+document.Frm1.numProtocollo.value;
	    f = f + "&numAnnoProt="+document.Frm1.numAnnoProt.value;
	    f = f + "&dataOraProt="+document.Frm1.dataOraProt.value;
	    f = f + "&DatAcqRil="+document.Frm1.DatAcqRil.value;
	    f = f + "&DatInizio="+document.Frm1.DatInizio.value;
	    f = f + "&tipoProt=S";
	    f = f + "&codAmbito=<%=docCodRif%>";
	    f = f + "&strambitodoc=<%=docRif%>";
	    f = f + "&codTipoDocumento=<%=docCodTipo%>";
	    f = f + "&strtipodoc=<%=docTipo%>";
	    f = f + "&striodoc=<%=docInOut%>";
	    f = f + "&FlgCodMonoIO=<%=docInOut%>";
	    f = f + "&numannorifprospetto=<%=numannorifprospetto%>";
	    f = f + "&codMonoStatoProspetto=<%=codMonoStatoProspetto%>";
	    
	    if(document.Frm1.codStatoAtto.value == "AN"){
			f = f + "&annullamento=1";
			f = f + "&numkloprospettoinf=<%=numkloprospettoinf%>";
		} else {
			f = f + "&annullamento=0";
		}
		document.location = f;
	}
	 
	 
	 function controllaDatARilascio(){
		if (document.Frm1.codStatoAtto.value == "NP"){
			<%
				Calendar oggi = Calendar.getInstance();
				String giornoDB = Integer.toString(oggi.get(5));
				if (giornoDB.length() == 1){
					giornoDB = '0' + giornoDB;
				}
				String meseDB = Integer.toString(oggi.get(2)+1);
				if (meseDB.length() == 1){
					meseDB = '0' + meseDB;
				}
				String annoDB = Integer.toString(oggi.get(1));
				dataOdierna = giornoDB + "/" + meseDB + "/" + annoDB;  
			%>
			document.Frm1.DatAcqRil.value = "<%=dataOdierna%>";
			document.Frm1.DatInizio.value = "<%=dataOdierna%>";
		}
		else {
			document.Frm1.DatAcqRil.value = "<%=datProtV%>";
			document.Frm1.DatInizio.value = "<%=datProtV%>";
		}
		return true;
	}
	
	function gestione_Protocollazione() {
	<%-- DOCAREA: basta questo controllo. I valori della protocollazione vengono impostati dalla classe Documento stessa. --%>
	var protocolloLocale = <%=it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale() %>;
	if (!protocolloLocale) return;

	<% if(!nuovoProspetto && codStatoAtto.equals("NP")) { %>
		if (document.Frm1.codStatoAtto.value == "PR"){
			<% 
				rowsProt = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
	  	    	if(rowsProt != null && !rowsProt.isEmpty()) { 
					rowProt = (SourceBean) rowsProt.elementAt(0);
		   			numProtV = SourceBeanUtils.getAttrBigDecimal(rowProt, "NUMPROTOCOLLO", null);
	   				numAnnoProtV = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
	   				dataOraProt = (String) rowProt.getAttribute("DATAORAPROT");
	  				oraProtV = dataOraProt.substring(11,16);
	  				datProtV = dataOraProt.substring(0,10);
	  			}
	  		%>
	  		document.Frm1.oraProt.value = "<%=oraProtV%>";
			document.Frm1.dataProt.value= "<%=datProtV%>"; 
			document.Frm1.numProtocollo.value="<%=numProtV%>";  
			document.Frm1.numAnnoProt.value= "<%=numAnnoProtV%>";
			document.Frm1.dataOraProt.value="<%=dataOraProt%>"; 
	  	}
	  	if (document.Frm1.codStatoAtto.value == "AN" || document.Frm1.codStatoAtto.value == "NP") {
			document.Frm1.numAnnoProt.value = "";
	  		document.Frm1.numProtocollo.value = "";
	  		document.Frm1.dataProt.value = "";
	  		document.Frm1.oraProt.value = "";
	  		document.Frm1.dataOraProt.value = "";
		}
		<%}%>		 	
	 }
	 
	 function annullamentoDoc() {
	 	var statoProsp = document.Frm1.codMonoStatoProspetto.value;
	 	if(statoProsp == "N"){
	 		document.Frm1.annullamento.value = 1;
	 	}
	}
	
	function controllaDoc(){
	 	var codStatoAtto = "<%=codStatoAtto%>";
	 	document.Frm1.codStatoAtto.value = codStatoAtto;
		if (document.Frm1.codStatoAtto.value == "NP"){
				document.Frm1.numAnnoProt.value = "";
	  			document.Frm1.numProtocollo.value = "";
	  			document.Frm1.dataProt.value = "";
	  			document.Frm1.oraProt.value = "";
	  			document.Frm1.dataOraProt.value = "";
    	}
    }
	 
	</script>
			
	<tr>
		<td class="etichetta2">Verificato da Operatore</td>
		<td class="campo2">
			<af:comboBox name="flgVerOperatore" classNameBase="input" disabled="<%=fieldReadOnly%>" title="Flag verifica Operatore">	  	
			    <option value=""  <% if ( "".equalsIgnoreCase(flgVerOperatore) )  { %>SELECTED="true"<% } %> ></option>            
	            <option value="N" <% if ( "N".equalsIgnoreCase(flgVerOperatore) )  { %>SELECTED="true"<% } %>>No</option> 
	            <option value="S" <% if ( "S".equalsIgnoreCase(flgVerOperatore) )  { %>SELECTED="true"<% } %>>Sì</option>               
	        </af:comboBox> 
	    </td>
	</tr>
	
	<%
	if (!checkProspetto2011) {
	%>
		<tr>
			<td class="etichetta2">Prospetto presentato dalla Capogruppo</td>
			<td class="campo2">
				<af:comboBox name="flgCapoGruppo" required="true" classNameBase="input" disabled="<%=fieldReadOnly%>" title="Flag capogruppo">	  	
				    <option value=""  <% if ( "".equalsIgnoreCase(flgCapoGruppo) )  { %>SELECTED="true"<% } %> ></option>            
		            <option value="N" <% if ( "N".equalsIgnoreCase(flgCapoGruppo) )  { %>SELECTED="true"<% } %>>No</option> 
		            <option value="S" <% if ( "S".equalsIgnoreCase(flgCapoGruppo) )  { %>SELECTED="true"<% } %>>Sì</option>               
		        </af:comboBox> 
		    </td>
		</tr>
		<tr>
		    <td class="etichetta2">Codice Fiscale Capogruppo</td>
			<td class="campo2">
				<af:textBox classNameBase="input" title="Codice fiscale capogruppo" type="text" name="strCFAZCapoGruppo" 
					value="<%=codFiscCapoGruppo%>" size="50" maxlength="100" readonly="<%=fieldReadOnly%>" />
			</td>
		</tr>
		
		
		<tr>
			<td class="etichetta2">Capogruppo estera</td>
			<td class="campo2">
				<af:comboBox name="flgCapoGruppoEstera" classNameBase="input" disabled="<%=fieldReadOnly%>" title="Flag capogruppo estera">	  	
				    <option value=""  <% if ( "".equalsIgnoreCase(flgCapoGruppoEstera) )  { %>SELECTED="true"<% } %> ></option>            
		            <option value="N" <% if ( "N".equalsIgnoreCase(flgCapoGruppoEstera) )  { %>SELECTED="true"<% } %>>No</option> 
		            <option value="S" <% if ( "S".equalsIgnoreCase(flgCapoGruppoEstera) )  { %>SELECTED="true"<% } %>>Sì</option>               
		        </af:comboBox> 
		    </td>
		</tr>
		
		
		
		<tr><td colspan="4">&nbsp;</td></tr>
	<% }

if(infCorrentiAzienda == null) {  
%>
	<tr class="note">
   		<td colspan="4">
   			<div class="sezione2">
    		<img id='tendinaAzienda' alt="Chiudi" src="../../img/chiuso.gif" onclick='cambia(this,document.getElementById("aziendaSez"))'/>&nbsp;&nbsp;&nbsp;Azienda       																													
         	&nbsp;&nbsp;
  			<% 
  			if (canModify) {
  				if (!StringUtils.isFilled(prgAzienda)) { 
  			%> 
				<a href="#" onClick="apriAzienda();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>  			
	 			&nbsp;<a href="#" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
	 		<% 
  				}
  			}   			
  			%>
  			</div>
     	</td>
	</tr>
    <tr>
		<td colspan="4">
			<table id='aziendaSez' style='display:<%=visualizza_display%>'>   
	    		<script>new Sezione(document.getElementById('aziendaSez'),document.getElementById('tendinaAzienda'),false);</script>
				<tr valign="top">
					<td class="etichetta">Codice Fiscale</td>
					<td class="campo">
						<input type="text" name="strCodiceFiscale" class="inputView" readonly="true" value="" size="30" maxlength="16"/>
					</td>
				</tr>
				<tr valign="top">
					<td class="etichetta">Partita IVA</td>
					<td class="campo">
						<input type="text" name="strPartitaIva" class="inputView" readonly="true" value="" size="30" maxlength="30"/>
					</td>
				</tr>
				<tr valign="top">
					<td class="etichetta">Ragione Sociale</td>
					<td class="campo">
						<input type="text" name="strRagioneSociale" class="inputView" readonly="true" value="" size="75" maxlength="120"/>
					</td>
				</tr>
				</table>
		</td>
	</tr>
<%
}
else {
	if (!flag_insert) {    
%>     			
		<tr>
			<td class="etichetta2">Indir. Sede Legale</td>
			<td class="campo2" colspan="3">
				<af:textBox classNameBase="input" name="indirizzoLegale" size="60" value="<%=indirizzoLegale%>" readonly="true" maxlength="80" />
		    </td> 	    
		</tr>
			
		<tr class="note">
	   		<td colspan="4">
	   			<div class="sezione2"> 
	   			&nbsp;Referente&nbsp;&nbsp;    		
	   			<% 
  				if (canModify) {  				
  				%> 
					<a href="#" onClick="apriRef();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a> 			
				<%
				}
				%>
				</div>
	     	</td>
		</tr>	
		<tr>
			<td class="etichetta2">Referente</td>
			<td class="campo2" colspan="3">		
				<af:textBox classNameBase="input" name="strReferente" size="60" value="<%=referente%>" readonly="true" maxlength="80" /> 
		    </td> 
		</tr>
		<tr>
		    <td class="etichetta2">Tel.</td>
		 	<td class="campo2">
				<af:textBox classNameBase="input" name="strTelefono" size="20" value="<%=strTelefono%>" readonly="true" maxlength="50" />
		    </td>	  
		    <td class="etichetta2">Fax</td>
			<td class="campo2">
				<af:textBox classNameBase="input" name="strFax" size="20" value="<%=strFax%>" readonly="true" maxlength="50" />
		    </td>
		</tr>	    
		<tr>
			<td class="etichetta2">Email</td>
			<td class="campo2" colspan="3">
				<af:textBox classNameBase="input" name="strEmail" size="60" value="<%=strEmail%>" readonly="true" maxlength="80" />
		    </td> 	    
		</tr>
<%
	}
}
if (!flag_insert) {
%>
	<tr><td colspan="4" ><div class="sezione2"/></td></tr>
	<tr><td colspan="4">&nbsp;</td></tr>	
	<tr>
		<td class="etichetta" nowrap>Codice comunicazione</td>	
		<td colspan="3" class="campo2">
			<table style="border-collapse:collapse">
				<tr>
					<td class="campo2" nowrap>
						<af:textBox classNameBase="input" name="codComunicazione" value="<%=codComunicazione%>" size="20" maxlength="16" validateOnPost="true" readonly="<%=strReadOnly%>"/>
					</td>
					<td class="etichetta" nowrap>Codice comunicazione orig</td>
          			<td class="campo" nowrap>
            			<af:textBox classNameBase="input" name="codComunicazioneOrig" value="<%=codComunicazioneOrig%>" size="20" maxlength="16" validateOnPost="true" readonly="<%=strReadOnly%>"/>
          			</td>
          			<td class="etichetta" nowrap>Codice comunicazione ann</td>
          			<td class="campo" nowrap>
            			<af:textBox classNameBase="input" name="codComunicazioneAnn" value="<%=codComunicazioneAnn%>" size="20" maxlength="16" validateOnPost="true" readonly="<%=strReadOnly%>"/>
          			</td>
				</tr>
			</table>
		</td>
	</tr>
<%
}
%>	
	<tr>
		<td class="etichetta2">Provincia</td>
		<td class="campo2" colspan="3">
			<af:comboBox name="codProvincia" classNameBase="input" multiple="false" moduleName="M_GetIDOProvince" selectedValue="<%=codProvincia%>" required="true" disabled="<%=strReadOnly%>" title="Provincia"/>
	    </td> 	    
	</tr>
	<tr>	
		<td class="etichetta2">Anno</td>
		<td class="campo2">
			<af:textBox type="integer" title="Anno" classNameBase="input" name="numannorifprospetto" value="<%= numannorifprospetto%>" size="4" maxlength="4" validateOnPost="true" onKeyUp="fieldChanged();" required="true" readonly="<%=strReadOnly%>"/>
	    </td>
	    <td class="etichetta2">Data Consegna</td>
		<td class="campo2">
			<af:textBox classNameBase="input" title="Data consegna" type="date" 
				        			validateOnPost="true" onKeyUp="fieldChanged();" 
				        			name="datconsegnaprospetto" size="11" maxlength="10" value="<%= datconsegnaprospetto%>"   
				        			readonly="<%=fieldReadOnly%>" />  
	    </td>
	</tr>
<%
if (!flag_insert) {     
%>		
	<tr>
		<td class="etichetta2">Stato del prospetto</td>
		<td class="campo2">
			<af:comboBox name="codMonoStatoProspetto" classNameBase="input" required="true" disabled="true" title="Stato del prospetto">	  	
			    <option value=""  <% if ( "".equalsIgnoreCase(codMonoStatoProspetto) )  { %>SELECTED="true"<% } %> ></option>            
	            <option value="A" <% if ( "A".equalsIgnoreCase(codMonoStatoProspetto) )  { %>SELECTED="true"<% } %>>In corso d'anno</option>
	            <option value="N" <% if ( "N".equalsIgnoreCase(codMonoStatoProspetto) )  { %>SELECTED="true"<% } %>>Annullato</option> 
	            <option value="S" <% if ( "S".equalsIgnoreCase(codMonoStatoProspetto) )  { %>SELECTED="true"<% } %>>Storicizzato</option>               
	        	<option value="V" <% if ( "V".equalsIgnoreCase(codMonoStatoProspetto) )  { %>SELECTED="true"<% } %>>SARE: storicizzato</option>
	        	<option value="U" <% if ( "U".equalsIgnoreCase(codMonoStatoProspetto) )  { %>SELECTED="true"<% } %>>Storicizzato: uscita dall'obbligo</option> 	        	
	        </af:comboBox> 
	    </td> 
	    <td class="etichetta2">Provenienza</td>
		<td class="campo2">
			<af:comboBox name="codMonoProv" classNameBase="input" required="true" disabled="true" title="Provenienza">	  	 
				<option value=""  <% if ( "".equalsIgnoreCase(codMonoProv) )  { %>SELECTED="true"<% } %> ></option>           
	            <option value="M" <% if ( "M".equalsIgnoreCase(codMonoProv) )  { %>SELECTED="true"<% } %>>Manuale</option>
	            <option value="F" <% if ( "F".equalsIgnoreCase(codMonoProv) )  { %>SELECTED="true"<% } %>>Da File</option>               	        
	            <option value="S" <% if ( "S".equalsIgnoreCase(codMonoProv) )  { %>SELECTED="true"<% } %>>Da SARE</option>               	        
	        </af:comboBox> 
	    </td>	  
	</tr>		
	<tr>
		<td class="etichetta2">Attività</td>
		<td class="campo2" colspan="3">
			<af:textArea classNameBase="textarea" name="attivita" value="<%=attivita%>"
                    cols="60" rows="3" onKeyUp="fieldChanged();" readonly="true"  />			
	    </td> 
	</tr>
	<tr>
	    <td class="etichetta2">CCNL</td>
	 	<td class="campo2">
	 		<af:textArea classNameBase="textarea" name="ccnl" value="<%=ccnl%>"
                    cols="60" rows="3" onKeyUp="fieldChanged();" readonly="true"  />	
	    </td>	  
	<% if(configEtichette.equals("0")){ %>
		<td class="etichetta2">Ore</td>
	<% }else if(configEtichette.equals("1")){ %>
		<td class="etichetta2">Ore settimanali</td>
	<% }%>
		<td class="campo2">
			<af:textBox classNameBase="input" type="number" name="numoreccnl" size="5" maxlength="11"  value="<%=numoreccnl%>" 
						title="Ore" onKeyUp="fieldChanged();" required="true" readonly="<%=fieldReadOnly%>"/>
	    </td>
	</tr>	
<%
}
else {
%>
	<tr>
		<td class="etichetta2">Stato del prospetto</td>
		<td class="campo2">
			<af:comboBox name="codMonoStatoProspetto" classNameBase="input" title="Stato del prospetto" required="true" disabled="<%=fieldReadOnly%>">	  	
	            <option value="A" <% if ( "A".equalsIgnoreCase(codMonoStatoProspetto) )  { %>SELECTED="true"<% } %>>In corso d'anno</option>
	        </af:comboBox> 
	    </td> 
	    <td class="etichetta2">Provenienza</td>
		<td class="campo2">
			<af:comboBox name="codMonoProv" classNameBase="input" title="Provenienza" required="true" disabled="<%=fieldReadOnly%>">	  	 				
	            <option value="M" <% if ( "M".equalsIgnoreCase(codMonoProv) )  { %>SELECTED="true"<% } %>>Manuale</option>               	        
	        </af:comboBox> 
	    </td>	   
	</tr>	
	<tr>
	    <td class="etichetta2"></td>
	 	<td class="campo2">			
	    </td>	
		<td class="etichetta2">Ore</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="number" name="numoreccnl" size="5" maxlength="11"  value="<%=numoreccnl%>" 
						title="Ore" onKeyUp="fieldChanged();" required="true" readonly="<%=fieldReadOnly%>"/>
	    </td>	    
	</tr>
<%
}
%>
	<tr>
		<td class="etichetta2">Categoria azienda L.68/99</td>
		<td class="campo2" colspan="3">
			<af:comboBox name="codMonoCategoria" classNameBase="input" onChange="visualizzaAssunzione(this);fieldChanged();flag15lav();" disabled="<%=fieldReadOnly%>" title="Categoria" >	  
	            <option value=""  <% if ( "".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>></option>
	            <option value="A" <% if ( "A".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>più di 50 dipendenti</option>
	           	<option value="B" <% if ( "B".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>da 36 a 50 dipendenti</option>               
	           	<option value="C" <% if ( "C".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>da 15 a 35 dipendenti</option> 
	        </af:comboBox> 
	    </td> 
	</tr>	
	<tr>
		<%if (checkProspetto2011) {%>
	    	<td class="etichetta2">Num. lav. nazion. computabili <br>(al netto delle esclusioni)</td>
	    <%} else {%>
	    	<td class="etichetta2">Num. lav. in forza nazionali</td>
	    <%}%>
	 	<td class="campo2">
			<af:textBox type="integer" classNameBase="input" title="Num. lav. nazion. computabili (al netto delle esclusioni)" name="numDipendentiNazionale" value="<%= numDipendentiNazionale%>" size="5" maxlength="6" required="true" validateOnPost="true" onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>"/>
	    </td>	  
	    <td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	
	<%if (!checkProspetto2011) {%>
		<tr>
			<td class="etichetta2">Base computo nazionale Art.3</td>
			<td class="campo2">
				<af:textBox type="integer" classNameBase="input" title="Base computo nazionale Art.3" name="numBaseComputoArt3" value="<%= numBCArt3%>" size="5" maxlength="6" validateOnPost="true" onKeyUp="fieldChanged();" required="true" readonly="<%=fieldReadOnly%>"/>
			</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="etichetta2">Base computo nazionale Art.18</td>
			<td class="campo2">
				<af:textBox type="integer" classNameBase="input" title="Base computo nazionale Art.18" name="numBaseComputoArt18" value="<%= numBCArt18%>" size="5" maxlength="6" validateOnPost="true" onKeyUp="fieldChanged();" required="true" readonly="<%=fieldReadOnly%>"/>
			</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
	<%}%>
	
	<%// if ("1".equals(resultConfigNonVedenti_MassoFisioterapista)){%> 
	<tr>
	    <td class="etichetta2">Num. totale lav. Provinciali</td>
		<td class="campo2">
			<af:textBox type="integer" classNameBase="input" title="Num. totale lav. Provinciali" name="numDipendentiTot" value="<%= numDipendentiTot%>" size="5" maxlength="6" validateOnPost="true" onKeyUp="fieldChanged();" required="true" readonly="<%=fieldReadOnly%>"/>
	    </td>
	    <td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
	    <td class="etichetta2">Num. Obbligo Centralinista Non Vedenti</td>
		<td class="campo2">
			<af:textBox type="integer" classNameBase="input" title="Num. Obbligo Centralinista Non Vedenti" name="numcentnonvedentiobbligo" value="<%=numcentnonvedentiobbligo%>" size="4" maxlength="4" validateOnPost="true" onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>"/>
	    </td>
	    <td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
	    <td class="etichetta2">Num. Obbligo Masso Fisioterapisti</td>
		<td class="campo2">
			<af:textBox type="integer" classNameBase="input" title="Num. totale lav. prov." name="nummassofisioterapistiobbligo" value="<%=nummassofisioterapistiobbligo%>" size="4" maxlength="4" validateOnPost="true" onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>"/>
	    </td>
	    <td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<%//}%>
	
	<tr>
		<td colspan="4">	
			<div id="ASSUNZIONI" style="display:<%=displayDateAss%>" >
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>		
					    <td class="etichetta2">Data prima assunz.</td>
					 	<td class="campo2">			
							<af:textBox classNameBase="input" title="Data prima assunzione" type="date" 
								        			validateOnPost="true" onKeyUp="fieldChanged();" 
								        			name="datprimaassunzione" size="11" maxlength="10" value="<%= datprimaassunzione%>"   
								        			readonly="<%=fieldReadOnly%>" />  
					    </td>	  
					    <td class="etichetta2">Data seconda assunz.</td>
						<td class="campo2">
							<af:textBox classNameBase="input" title="Data seconda assunzione" type="date" 
								        			validateOnPost="true" onKeyUp="fieldChanged();" 
								        			name="datsecondaassunzione" size="11" maxlength="10" value="<%= datsecondaassunzione%>"   
								        			readonly="<%=fieldReadOnly%>" /> 		
					    </td>    	
					</tr> 
				</table> 
			</div>  
		</td>
	</tr>
	<tr>
	    <td class="etichetta2">Note</td>
	 	<td colspan="3" class="campo2">			
	 		<af:textArea classNameBase="textarea" name="strNote" value="<%=strNote%>"
                    cols="60" rows="4" maxlength="2000"
                    onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>"  />
	    </td>			
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr>
		<td colspan="4" >
		 <% if (canModify) {
		 		if (!flag_insert) { 
		 			if(!codStatoAtto.equals("AN")) { %>
		 				<input type="submit" class="pulsante" name="aggiorna" value="Aggiorna" onClick="annullamentoDoc();controllaDoc()"/>
		 				<input type="hidden" name="annullamento" value="0">
						<input type="hidden" name="aggiornamento" value="1">
		 	   		<% } 
		 		} else { %>
					<input type="submit" class="pulsante" name="inserisci" value="Inserisci" onClick="controllaDatARilascio()"/>
					<input type="hidden" name="inserisciDoc" value="1"/>
			  <% }		
		 } if (!url.equals("")) { %>
			&nbsp;&nbsp;&nbsp;
			<input class="pulsante" type="button" name="back" value="<%= goBackTitle%>" onclick="goTo('<%= url%>')" />
		<% } %>
		</td>
	</tr>
</table> 
<%out.print(htmlStreamBottom);%>
</af:form>
<p align="center">
<% if (operatoreInfo!=null) operatoreInfo.showHTML(out);%>
</p>
<br/>

</body>
</html>