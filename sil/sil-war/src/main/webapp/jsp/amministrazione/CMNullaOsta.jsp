<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.bean.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>
 
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
//Servono per gestire il layout grafico
  
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  String _funzione =(String) serviceRequest.getAttribute("CDNFUNZIONE");
  
  
  //String htmlStreamTop = StyleUtils.roundTopTable(true);
  //String htmlStreamBottom = StyleUtils.roundBottomTable(true);
  boolean isInsert = true;
  SourceBean rowRich = null;
  SourceBean rowDoc = null;
  Vector rowsDoc      = null;  
  String  prgAzienda = null;
  String  cdnLavoratore = null;
  String  prgUnita = null;
  
  PageAttribs attributi = new PageAttribs(user, _page);
  
  boolean canInsert=false;
  boolean canUpdate=false;
  boolean canDocAss=false;
  boolean canSalvaStato=false;
  boolean canPrint=false;
  
  boolean canModify = true;
  
  if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		
		canInsert = attributi.containsButton("INSERISCI");
		canUpdate = attributi.containsButton("AGGIORNA");
		canDocAss = attributi.containsButton("DOC_ASSOCIATI");
		canSalvaStato = attributi.containsButton("SALVA-STATO");
		canPrint = attributi.containsButton("STAMPA");
	}
  
  boolean nuovoNullaOsta = !(serviceRequest.getAttribute("nuovoNullaOsta")==null || 
                            ((String)serviceRequest.getAttribute("nuovoNullaOsta")).length()==0);
                            
  if (nuovoNullaOsta){
  	if (!canInsert){
  		canSalvaStato = canInsert;
  	}
  	else if (!canSalvaStato){
  		canInsert = canSalvaStato;
  	} 
  }
  
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
  String CODSTATOATTO = "NP";
  BigDecimal numAnnoProt = null;
  BigDecimal numProtocollo = null;
  String dataProt = "";
  String DatAcqRil = "";
  String DatInizio = "";
  
  String oraProt = "";
  Vector rowsDoc1     = null;
  SourceBean rowDoc1  = null;
  String codStatoAttoV = "";
  
  String queryString = null;
  
  String valDisplay="";
  String displayStampa="";
  
  String PRGNULLAOSTA="";
  String prgDocumento="";
  String codCCNL=""; 
  String CODMANSIONE=""; 
  String CODMONOCATEGORIA=""; 
  String CODMONOTEMPO=""; 
  String CODMONOTIPO=""; 
  String CODMOTIVO=""; 
  String codTipoContratto = "";
  String strTipoContratto = "";
  String codAgevolazione = "";
  String CODORARIO=""; 
  String DATFINE=""; 
  String DATSPEDIZIONE=""; 
  String DECORESETT=""; 
  String DECRETRIBUZIONE=""; 
  String FLGCONVENZIONE=""; 
  String NUMLIVELLO=""; 
  String PRGAZIENDAUTILIZ=""; 
  String PRGUNITAUTILIZ=""; 
  String DATAINIZIO="";
  String DESCMANSIONE="";
  String strCCNL="";
  String diagnosi="";
  
  String strCodiceFiscaleLav = "";
  String strNomeCognomeLav = "";
  String strNomeLav = "";
  String strCognomeLav = "";
  String strCodiceFiscaleAzi = "";
  String strPIva = "";
  String strRagSoc = "";
  String strIndSede = "";
  String strCodFiscAziUt = "";
  String strPIvaUt = "";
  String strRagSocUt = "";
  String strIndSedeUt = "";
  String codMonoTempo = "";
  String codOrario = "";
  String statoNullaOsta = "";
  
  Vector infoDati = null;
  SourceBean rowInfoDati = null;
  
  String NUMKLONULLAOSTA = "";
  String NUMKLONULLAOSTAAGG = "";
  String prgConv = "";
  String prgConvApp = "";
  
  String dtmIns = "";
  String cdnUtMod = "";
  String dtmMod = "";
  String cdnUtIns = "";
  
  Testata operatoreInfo = null;
  
  prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGAZIENDA");
  cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNLAVORATORE");
  prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGUNITA");
  strNomeCognomeLav = StringUtils.getAttributeStrNotNull(serviceRequest,"strNomeCognomeLav");
  String infoDatiNOLav = StringUtils.getAttributeStrNotNull(serviceRequest, "infoDatiNOLav");
  String infoDatiNOAzi = StringUtils.getAttributeStrNotNull(serviceRequest, "infoDatiNOAzi");
  String cdnLavoratoreEncrypt = "";
  if(!cdnLavoratore.equals("")) {
  	cdnLavoratoreEncrypt = EncryptDecryptUtils.encrypt(cdnLavoratore);
  }
  
  diagnosi = StringUtils.getAttributeStrNotNull(serviceRequest, "diagnosi");
  
  strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull(serviceRequest,"codiceFiscaleLavoratore");
  strNomeLav = StringUtils.getAttributeStrNotNull(serviceRequest, "nome"); 
  strCognomeLav = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cognome");
  strCodiceFiscaleAzi = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codFiscaleAzienda");
  strPIva = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "pIva");
  strRagSoc = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "ragioneSociale");
  strIndSede = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "IndirizzoAzienda");
  
  if (!strNomeLav.equals("") && !strCognomeLav.equals("")) {
  		strNomeCognomeLav = strCognomeLav + " " + strNomeLav;
  }
  String PROVINCIA_ISCR = serviceRequest
			.getAttribute("PROVINCIA_ISCR") == null ? ""
			: (String) serviceRequest
			.getAttribute("PROVINCIA_ISCR");
	
  String COD_PROVINCIA_ISCR = "";
  String COD_PROVINCIA_ISCR_OLD = "";

  SourceBean beanProt = null;
  
  String contprot = StringUtils.getAttributeStrNotNull(serviceRequest,"canModify");
  
  DatAcqRil = StringUtils.getAttributeStrNotNull(serviceRequest,"DatAcqRil");
  DatInizio = StringUtils.getAttributeStrNotNull(serviceRequest,"DatInizio");
  docInOut = StringUtils.getAttributeStrNotNull(serviceRequest,"striodoc");
  docRif = StringUtils.getAttributeStrNotNull(serviceRequest,"strambitodoc");
  docTipo = StringUtils.getAttributeStrNotNull(serviceRequest,"strtipodoc");
  docCodRif = StringUtils.getAttributeStrNotNull(serviceRequest,"codAmbito");      
  docCodTipo = StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoDocumento");
  COD_PROVINCIA_ISCR = StringUtils.getAttributeStrNotNull(serviceRequest,"COD_PROVINCIA_ISCR");
  
  if(!nuovoNullaOsta) {
  		infoDati = serviceResponse.getAttributeAsVector("M_InfoNullaOsta.ROWS.ROW");
		if(infoDati != null && !infoDati.isEmpty()) { 
			rowInfoDati = (SourceBean) infoDati.firstElement();
			
			prgAzienda = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "PRGAZIENDA"); 
  			cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "CDNLAVORATORE");
  			cdnLavoratoreEncrypt = EncryptDecryptUtils.encrypt(cdnLavoratore); 
  			prgUnita = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "prgUnita");
  			PRGAZIENDAUTILIZ = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "PRGAZIENDAUTILIZ");
  			PRGUNITAUTILIZ = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "PRGUNITAUTILIZ");
  			NUMKLONULLAOSTA = String.valueOf(((BigDecimal)rowInfoDati.getAttribute("NUMKLONULLAOSTA")).intValue());
			PRGNULLAOSTA = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "PRGNULLAOSTA");
			prgDocumento = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "PRGDOCUMENTO");
			strCodiceFiscaleLav = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "codFiscLav"); 
			strNomeLav = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "nome"); 
			strCognomeLav = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "cognome");
			strCodiceFiscaleAzi = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "codFiscAzi");
			strPIva = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "PIva");
			strRagSoc = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "ragSoc");
			strIndSede = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "indirizzo");
			strCodFiscAziUt = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "codFiscAziUt");
	  		strPIvaUt = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "PIvaUt");
	  		strRagSocUt = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "ragSocUt");
	  		strIndSedeUt = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "indirizzoUt");
	  		codCCNL = StringUtils.getAttributeStrNotNull(rowInfoDati,"CCNL");
  			strCCNL = StringUtils.getAttributeStrNotNull(rowInfoDati,"STRCCNL");
	  		CODMANSIONE = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"qualifica");
			DESCMANSIONE = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"DESCMANSIONE");
			CODMONOCATEGORIA = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"Categoria");
			CODMONOTEMPO = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"tempo");
			CODMONOTIPO = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"tipo");
			CODMOTIVO = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"motivo");
			CODORARIO = StringUtils.getAttributeStrNotNull(rowInfoDati,"orario");
			codTipoContratto = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"TipoContratto");
			strTipoContratto = StringUtils.getAttributeStrNotNull(rowInfoDati,"strTipoContratto");
			codAgevolazione = StringUtils.getAttributeStrNotNull(rowInfoDati,"CODAGEVOLAZIONE");
			DATFINE = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"datFineCm");
			DATSPEDIZIONE = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"datSped");
			DECORESETT = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"ORESETT");
			DECRETRIBUZIONE = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"retribuzione");
			NUMLIVELLO = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"livello");
			DATAINIZIO = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"datInizioCm");
			FLGCONVENZIONE = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"FLGCONVENZIONE");
			prgConv = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"conv");
			dtmIns = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "DTMINS");
			cdnUtMod = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "CDNUTMOD");
			dtmMod = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "DTMMOD");
			cdnUtIns = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "CDNUTINS");
			PROVINCIA_ISCR = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"PROVINCIA_ISCR");
			COD_PROVINCIA_ISCR = SourceBeanUtils.getAttrStrNotNull(rowInfoDati,"COD_PROVINCIA_ISCR");
			COD_PROVINCIA_ISCR_OLD = COD_PROVINCIA_ISCR;
		}
		
		if(!dtmIns.equals("") && !cdnUtMod.equals("") && !dtmMod.equals("") && !cdnUtIns.equals("")) {
			operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
		}
		
		strNomeCognomeLav = strCognomeLav + " " + strNomeLav;
		
		CODSTATOATTO = StringUtils.getAttributeStrNotNull(serviceRequest,"CODSTATOATTO");
  		if(CODSTATOATTO.equals("PR") || CODSTATOATTO.equals("AN")){
			canModify = false;
			if(CODSTATOATTO.equals("AN")){
  				noButton = true;
  			}
  		}
  		
  		if ((CODSTATOATTO.equals("PR") || CODSTATOATTO.equals("AN")) ) {
  			numProtV = SourceBeanUtils.getAttrBigDecimal(serviceRequest, "NUMPROTOCOLLO", null);
	   		numAnnoProtV = SourceBeanUtils.getAttrBigDecimal(serviceRequest, "numAnnoProt", null); 
	   		dataOraProt = StringUtils.getAttributeStrNotNull(serviceRequest,"dataOraProt");
	 		if (!dataOraProt.equals("")) {
	 			oraProtV = dataOraProt.substring(11,16);
	 			datProtV = dataOraProt.substring(0,10);
	 		}
    	}
  } else {
  		SourceBean rowInfoDeDoc = (SourceBean) serviceResponse.getAttribute("M_GetInfoDeDoc_NullaOsta.ROWS.ROW");
  		if(rowInfoDeDoc != null){ 
	    	docInOut   = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,"striodoc");
	  		docRif     = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,"strambitodoc");
	  		docTipo    = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,"strtipodoc");
	  		docCodRif  = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,"codambitodoc");      
	  		docCodTipo = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,"codtipodoc");
  		}
  }
    
    queryString = "cdnFunzione="+_funzione+"&PAGE="+_page+"&CODAMBITO=L68"+"&cdnLavoratore="+cdnLavoratore+"&CODSTATOATTO="+CODSTATOATTO+
	"&CODTIPOCONTRATTO="+codTipoContratto+"&CODTIPODOCUMENTO=NULOST"+"&DATACQRIL="+DatAcqRil+"&DATAORAPROT="+dataOraProt+
	"&DATAPROT="+dataProt+"&DATINIZIO="+DATAINIZIO+"&INFODATINOAZI="+infoDatiNOAzi+"&INFODATINOLAV="+infoDatiNOLav+
	"&NUMANNOPROT="+numAnnoProtV+"&NUMKLONULLAOSTA="+NUMKLONULLAOSTA+"&NUMPROTOCOLLO="+numProtV+
	"&PRGNULLAOSTA="+PRGNULLAOSTA+"&prgDocumento="+prgDocumento+"&STRAMBITODOC=DOCUMENTAZIONE L68"+"&STRIODOC="+docInOut+"&STRTIPODOC="+docTipo;
	
  	
  String htmlStreamTop    = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

%>

<html>
  <head>
  	<title> Nulla osta </title>
   	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
	<%@ include file="../documenti/_apriGestioneDoc.inc"%>
	<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

	<af:linkScript path="../../js/"/>
	<SCRIPT language="JavaScript" src="../../js/CommonXMLHTTPRequest.js"></SCRIPT>
	
	
   <SCRIPT language="JavaScript" src="../../js/Function_CommonRicercaCCNL.js"></SCRIPT>
   
   <script language="Javascript" src="../../js/docAssocia.js"></script>
   <script language="Javascript">
   
    var imgChiusa = "../../img/chiuso.gif";
	var imgAperta = "../../img/aperto.gif";

	var sezioni = new Array();
	
	var imgCancAss = "../../img/patto_elim.gif";
	
	var flagChanged = false;

	var provinciaAmbitoTerr = "<%=COD_PROVINCIA_ISCR_OLD%>";
	
	function azzeraConvenzioneCambioTerritoriale(){
		var prgConv = document.Frm1.prgConv.value;	
		if(prgConv!=""){
			alert("Procedere prima con la dissociazione convenzione.");
			document.Frm1.PROVINCIA_ISCR.value = document.Frm1.PROVINCIAISCROLD.value;			
		}
		else {
			provinciaAmbitoTerr = "<%=COD_PROVINCIA_ISCR_OLD%>";
			document.Frm1.PROVINCIAISCROLD.value = document.Frm1.PROVINCIA_ISCR.value;
		}
	}
	
	function Sezione(sezione, img,aperta){    
    	this.sezione=sezione;
    	this.sezione.aperta=aperta;
    	this.img=img;
	}
	
	function cambia(immagine, sezione) {
		if (sezione.style.display == '') {
			sezione.style.display = 'none';
			sezione.aperta = false;
			immagine.src = imgChiusa;
			immagine.alt = 'Apri';
		}
		else if (sezione.style.display == "none") {
			sezione.style.display = "";
			sezione.aperta = true;
			immagine.src = imgAperta;
    		immagine.alt = "Chiudi";
		}
	}
	
	function selezionaAzienda()
    {
		var prgConv = document.Frm1.prgConv.value;
	
		if(prgConv!=""){
			if(!confirm("Il cambiamento dell'azienda comporta la dissociazione della convenzione.\nSi desidera proseguire?")){
			return false;
			}
			document.Frm1.prgConv.value = "";
			document.getElementById("IMG0").src="../../img/patto_ass.gif";
			document.Frm1.assDiss.value="(Associa convenzione)";
			<% if (nuovoNullaOsta) { %>
			    var url = "AdapterHTTP?PAGE=SelezionaAziendaNOPage&MOV_SOGG=Aziende&AGG_FUNZ=funzionediaggiornamento&cdnFunzione=<%=_funzione%>";
        	<%} else { %>
        		var NUMKLONULLAOSTA = document.Frm1.NUMKLONULLAOSTA.value;
        		var prgNullaOsta = document.Frm1.PRGNULLAOSTA.value;
        		var url = "AdapterHTTP?PAGE=SelezionaAziendaNOPage&MOV_SOGG=Aziende&AGG_FUNZ=funzionediaggiornamento&cdnFunzione=<%=_funzione%>&prgNullaOsta="+prgNullaOsta+"&NUMKLONULLAOSTA="+NUMKLONULLAOSTA;	
        		
        	<%}%>
        } else {
        	var url = "AdapterHTTP?PAGE=SelezionaAziendaNOPage&MOV_SOGG=Aziende&AGG_FUNZ=funzionediaggiornamento&cdnFunzione=<%=_funzione%>";
        }
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
    	opened = window.open(url, "_blank", feat);
    }
    
    function funzionediaggiornamento(prgAzienda, prgUnita, aziCodFiscale, aziPIva, aziRagSociale,aziIndirizzo,comune,cap) {

		document.Frm1.prgAzienda.value = prgAzienda;
		document.Frm1.prgUnita.value = prgUnita;
		document.Frm1.aziCodFiscale.value = aziCodFiscale;
		document.Frm1.aziPIva.value = aziPIva;
		document.Frm1.aziRagSociale.value = aziRagSociale;
		if(aziIndirizzo != "" && comune != "" && cap != "") {
			document.Frm1.aziIndirizzo.value = aziIndirizzo + ' (' + comune + ', ' + cap + ')';
		} else if(aziIndirizzo != "" && (comune == "" || cap == "") ) {	
			document.Frm1.aziIndirizzo.value = aziIndirizzo;
		}
		
		opened.close();
	}
	
	function azzeraAzienda(){
      	document.Frm1.prgAzienda.value = "";
      	document.Frm1.prgUnita.value = "";
		document.Frm1.aziCodFiscale.value = "";
		document.Frm1.aziPIva.value = "";
		document.Frm1.aziRagSociale.value = "";
		document.Frm1.aziIndirizzo.value = "";
    }
    
    function selezionaAziendaUt()
	{
		var url = "AdapterHTTP?PAGE=SelezionaAziendaNOPage&AGG_FUNZ=riempiDatiAziendaUt&cdnFunzione=<%=_funzione%>";
		var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
	    	 	   "width=600,height=500,top=50,left=100";
		var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
		opened = window.open(url, "_blank", feat);
    }
    
    function riempiDatiAziendaUt(PRGAZIENDAUTILIZ, PRGUNITAUTILIZ, aziCodFiscaleUt, aziPIvaUt, aziRagSocialeUt,aziIndirizzoUt,comuneUt,capUt) {

		document.Frm1.PRGAZIENDAUTILIZ.value = PRGAZIENDAUTILIZ;
		document.Frm1.PRGUNITAUTILIZ.value = PRGUNITAUTILIZ;
		document.Frm1.aziCodFiscaleUt.value = aziCodFiscaleUt;
		document.Frm1.aziPIvaUt.value = aziPIvaUt;
		document.Frm1.aziRagSocialeUt.value = aziRagSocialeUt;
		if(aziIndirizzoUt != "" && comuneUt != "" && capUt != "") {
			document.Frm1.aziIndirizzoUt.value = aziIndirizzoUt + ' (' + comuneUt + ', ' + capUt + ')';
		} else if(aziIndirizzoUt != "" && (comuneUt == "" || capUt == "") ) {	
			document.Frm1.aziIndirizzoUt.value = aziIndirizzoUt;
		}
		opened.close();
	}
	
	function azzeraAziendaUt(){
      	document.Frm1.PRGAZIENDAUTILIZ.value = "";
      	document.Frm1.PRGUNITAUTILIZ.value = "";
		document.Frm1.aziCodFiscaleUt.value = "";
		document.Frm1.aziPIvaUt.value = "";
		document.Frm1.aziRagSocialeUt.value = "";
		document.Frm1.aziIndirizzoUt.value = "";
    }
    
    function selLavIscrCM(context, funzSetLav)
	{
		var url = "AdapterHTTP?PAGE=SelLavNOPage&fromWhere=" + context + "&AGG_FUNZ=" + funzSetLav + "&cdnFunzione=<%=_funzione%>";
		var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
		     	   "width=600,height=500,top=50,left=100";
    	opened = window.open(url, "_blank", feat);
    	opened.focus();
	}
	
	function riempiDatiLavIscrCM(cdnLavoratore, strCodiceFiscaleLav, strCognome, strNome, flgCfOk) {
	  	document.Frm1.cdnLavoratore.value = cdnLavoratore;
	  	document.Frm1.strCodiceFiscaleLav.value = strCodiceFiscaleLav;
	  	document.Frm1.strNomeCognomeLav.value = strCognome + " " + strNome;
	  	opened.close();
	}
	
	function azzeraLavIscrCM(){
      	document.Frm1.cdnLavoratore.value = "";
      	document.Frm1.strCodiceFiscaleLav.value = "";
        document.Frm1.strNomeCognomeLav.value = "";
    }
	
	function visualizzaTempo(tempo) {
  		var tempovalue = tempo.options[tempo.selectedIndex].value;
  		var datFineLabel = document.getElementById("labeldata");
  		var datFineCampo = document.getElementById("campodata");
 
		if (tempovalue == "I" || tempovalue == "") {
    		document.Frm1.DATFINE.value = "";
    		datFineLabel.style.display = "none";
    		datFineCampo.style.display = "none";
    	}
  		else {
    		datFineLabel.style.display="";
    		datFineCampo.style.display="";
  		}   
	}
	
	function visualizzaOrario(orario) {
  		var orariovalue = orario.options[orario.selectedIndex].value;
  		var oreSettLabel = document.getElementById("labelore");
  		var oreSettCampo = document.getElementById("campoore");
  		
		if (orariovalue == "PTO" || orariovalue == "PTV" || orariovalue == "M") {
			oreSettLabel.style.display="";
 			oreSettCampo.style.display="";
    	}
  		else {
    		document.Frm1.DECORESETT.value = "";
    		oreSettLabel.style.display = "none";
 			oreSettCampo.style.display = "none";
  		}   
	}
	
	function visualizzaMotivo(tipologia){
		var motivovalue = tipologia.options[tipologia.selectedIndex].value;
		var motivoLabel = document.getElementById("labelTipologia");
  		var motivoCampo = document.getElementById("campoTipologia");
		
		if (motivovalue == "M") {
			motivoLabel.style.display="";
 			motivoCampo.style.display="";
    	}
  		else {
  			document.Frm1.CODMOTIVO.value = "";
    		motivoLabel.style.display = "none";
 			motivoCampo.style.display = "none";
  		}   
	}
	
	function checkMansione(nameMansione) {
		var cod = new String(eval('document.Frm1.' + nameMansione + '.value'));
		if (cod == "") return true;
			if (cod.substring(4, 6) == '00') {
				if (confirm("Non è stata indicata una mansione specifica. Continuare?")) {
					return controllaQualificaOnSubmit(nameMansione);
				}
			} else return controllaQualificaOnSubmit(nameMansione);
	}

	function controllaQualificaOnSubmit(campoQualifica) {
		var qualifica = new String(eval('document.Frm1.' + campoQualifica + '.value'));
		var exist = false;
		try {
			exist = controllaEsistenzaChiave(qualifica, "CODMANSIONE", "DE_MANSIONE");
		} catch (e) {
			return confirm("Impossibile controllare che la qualifica " + qualifica + " esista, proseguire comunque?");
		}
		if (!exist) {
			alert("Il codice della qualifica " + qualifica + " non esiste");
			return false;
		} else return true;
	}
	
	
	function selectMansioneOnClickNOsta(codMansione, codMansioneHid, descMansione) {	
  		if (codMansione.value==""){
    		descMansione.value="";
    		     
  		}
  		else if (codMansione.value!=codMansioneHid.value){
  			window.open("AdapterHTTP?PAGE=RicercaMansionePage&FLGIDO=S&codMansione="+codMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');     
  		}
	}
	
	function selectMansionePerDescrizioneNOsta(desMansione) {
		window.open("AdapterHTTP?PAGE=RicercaMansionePage&FLGIDO=S&desMansione="+desMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');          
	}
	
	function controllaCCNLOnSubmit(campoCCNL) {
		var field = eval('document.Frm1.' + campoCCNL);
		var codCCNL = new String(field.value);
		codCCNL = codCCNL.toUpperCase();
		field.value = codCCNL;
		if (codCCNL == "") return true;
		var exist = false;
		try {
			exist = controllaEsistenzaChiave(codCCNL, "CODCCNL", "DE_CONTRATTO_COLLETTIVO");
		} catch (e) {
			return confirm("Impossibile controllare che il codice del contratto collettivo " + codCCNL + " esista, proseguire comunque?");
		}
		if (!exist) {
			alert("Il codice del contratto collettivo " + codCCNL + " non esiste");
			return false;
		}
		else return true;
	}
	
	function controllaTipoContrattoOnSubmit(campoTipoContratto) {
		var field = eval('document.Frm1.' + campoTipoContratto);
		var tipoContratto = new String(field.value);
		tipoContratto = tipoContratto.toUpperCase();
		field.value = tipoContratto;
		if (tipoContratto == "") return true;
		var exist = false;
		try {
			exist = controllaEsistenzaChiave(tipoContratto, "CODTIPOCONTRATTO", "DE_TIPO_CONTRATTO");
		} catch (e) {
		return confirm("Impossibile controllare che il tipo di Contratto " + tipoContratto + " esista, proseguire comunque?");
		}
		if (!exist) {
			alert("Il codice della Tipologia di contratto " + tipoContratto + " non esiste");
			return false;
		} 
		else return true;
	}
	
	function selezionaConvenzione(immagine){
		if(document.Frm1.prgConv.value != ""){
				if(!confirm("Si desidera cancellare l'associazione?")) {
						return false;
				}
				document.Frm1.prgConv.value = "";
				document.getElementById("IMG0").src="../../img/patto_ass.gif";
				document.Frm1.assDiss.value="(Associa convenzione)";
				provinciaAmbitoTerr = "<%=COD_PROVINCIA_ISCR_OLD%>";
   		}
   		else {
   			if (provinciaAmbitoTerr == ""){
   				provinciaAmbitoTerr = document.Frm1.PROVINCIA_ISCR.value;   				
   			}
   		 	if(document.Frm1.prgAzienda.value==""){
				alert("Inserire prima i dati della Sede Azienda");
				return false;
			}
			else {
  				var prgAzienda = document.Frm1.prgAzienda.value;
  				var url = "AdapterHTTP?PAGE=RicercaConvenzioniPage&AGG_FUNZ=riempiAssociazione&PROVINCIA_ISCR=<%=PROVINCIA_ISCR%>&COD_PROVINCIA_ISCR="+ provinciaAmbitoTerr + "&cdnFunzione=<%=_funzione%>&prgAzienda="+prgAzienda;
				var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
	    					"width=600,height=500,top=50,left=100";
				opened = window.open(url, "_blank", feat);
				opened.focus();
			}
    	} 
    }	  		
	
	function riempiAssociazione(prgConvenzioni) {
		document.Frm1.prgConv.value = prgConvenzioni;
		if (document.Frm1.prgConv.value != ""){
			document.getElementById("IMG0").src="../../img/patto_elim.gif";
			document.Frm1.assDiss.value="(Dissocia convenzione)";
		 } else {
			document.getElementById("IMG0").src="../../img/patto_ass.gif";
			document.Frm1.assDiss.value="(Associa convenzione)";
		 }
		opened.close();
	}
	
 	function apriUnitaAziendale(prgAz,prgUn,cdnFunz) {
        var f = "AdapterHTTP?PAGE=IdoUnitaAziendaPage&PRGAZIENDA=" + prgAz + "&PRGUNITA=" + prgUn + "&CDNFUNZIONE=" + cdnFunz;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=75,left=100";
        opened = window.open(f, t, feat);
        window.openPopUpAzienda = true;
    }
    
    function tornaLista() {
		if (isInSubmit()) return;
		<%
		// Recupero l'eventuale URL generato dalla LISTA precedente
		String token = "_TOKEN_" + "CMListaNullaOstaPage";
		String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());	
		%>
			setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
		}
		
	function controllaConvenzione(){
		<% if(!nuovoNullaOsta && !canModify) {%>
			return true;
		<%} else { %>
			if(document.Frm1.FLGCONVENZIONE.value == "S" && document.Frm1.prgConv.value == ""){
				if(!confirm("Non è stata associata nessuna convenzione\nSi desidera proseguire?")){
					return false;
				}
			}
			if((document.Frm1.FLGCONVENZIONE.value == "N" || document.Frm1.FLGCONVENZIONE.value == "")
				&& document.Frm1.prgConv.value != ""){
					alert("Non è stata richiesta nessuna convenzione");
					return false;
			}
			return true;
		<%}%>
 	}
 	
 	function ControllaDate(){
    	var dataDa = document.Frm1.DATAINIZIOCM;
    	var dataA = document.Frm1.DATFINE; 
    	if ((dataDa.value != "") && (dataA.value != "")) {
      		if (compareDate(dataDa.value,dataA.value) > 0) {
      			alert(dataDa.title + " maggiore di " + dataA.title);
      			dataDa.focus();
	    		return false;
	  		}	
	  	}
	  	return true;
  	}
  	
  	function cercaTipoContrattoNOsta(criterio){
  		var f;
  		var descr;
  			f = "AdapterHTTP?PAGE=SelezionaTipoContrattoNullaOstaPage&CODMONOTEMPO=" + document.Frm1.CODMONOTEMPO.value;
 			f = f + "&CRITERIO=" + criterio;
  			f = f + "&codTipoContratto=" + document.Frm1.codTipoContratto.value;
  			
  			var descr = document.Frm1.strTipoContratto.value;
  			descr = descr.replace("%","&amp;");
			f = f + "&strTipoContratto=" + descr;
			f = f + "&updateFunctionName=aggiornaTipoContrattoNOsta";
		var t = "_blank";
  		var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100";
  		window.open(f, t, feat);
	}
	
	function aggiornaTipoContrattoNOsta(codice, descrizione) {
  		document.Frm1.codTipoContratto.value = codice;
  		document.Frm1.strTipoContratto.value = descrizione;
  		//refreshComboCollegate();
  	}
  	
  	/*function refreshComboCollegate() {  
  		args = '&CODMONOTEMPO='+ document.Frm1.CODMONOTEMPO.value; 
  		args = args + '&CODTIPOASS='+ document.Frm1.codTipoAss.value;      
  		refreshCombo('ComboNormativaSelettiva', 'Frm1.codNormativa', args);
  	}*/
  	
  	/*var opened;
    function refreshCombo(comboModuleName, comboName, argomenti){
        var f = "AdapterHTTP?PAGE=FantasmaRefreshComboPage&COMBOMODULENAME=" + comboModuleName + "&COMBONAME=" + comboName + argomenti;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1,height=1,top=0,left=0";
        opened = window.open(f, t, feat);
    
	}
    function clearCombo(comboname) {
    	combo = eval('window.document.' + comboname);
		for (var i = 0; i < combo.options.length; i++) {
        	combo.options[i] = null;
      	}
    	return combo.options.length;
    }*/
	
	function addOption(comboname, position, description, value, defaultSelected, selected) {
    	combo = eval('window.document.' + comboname);
      	combo.options[position] = new Option(description, value, defaultSelected, selected);
    }  
    
  	
  	function visualizzaStampa(){
  		<% if(!nuovoNullaOsta) {%>
  			var sezDiagnosi = document.getElementById("sezDia");
  			if(document.Frm1.CODMONOCATEGORIA.value == "D" && document.Frm1.CODMONOTIPO.value == "M"){
  				sezDiagnosi.style.display="";
  			} else {
  				sezDiagnosi.style.display="none";
  				document.Frm1.diagnosi.value="N";
  			}
  		<%}%>
  	}
  	
  	function ripristinaDaAnnulla(){
  		document.Frm1.CODMONOCATEGORIA.value = '<%=CODMONOCATEGORIA%>';
  		document.Frm1.CODMONOTIPO.value = '<%=CODMONOTIPO%>';
  		visualizzaMotivo(document.Frm1.CODMONOTIPO);
  		visualizzaStampa();
  		document.Frm1.CODMONOTEMPO.value = '<%=CODMONOTEMPO%>';
  		visualizzaTempo(document.Frm1.CODMONOTEMPO);
  		document.Frm1.CODORARIO.value = '<%=CODORARIO%>';
  		visualizzaOrario(document.Frm1.CODORARIO);
  	}  

	function ripristinaIconaConvCollegata() {
 	 	document.Frm1.prgConv.value = '<%=prgConv%>';
 	 	document.Frm1.prgAzienda.value = '<%=prgAzienda%>';
 	 	<% if(prgConv == "") {%>
 			document.getElementById("IMG0").src="../../img/patto_ass.gif";
 		<%} else  if(prgConv != "") {%>
 			document.getElementById("IMG0").src="../../img/patto_elim.gif";
 		<% } %>		 
	}    

</script>

<script type="text/javascript" src="../../js/movimenti/generale/func_campiMov.js" language="JavaScript"></script>

</head>
 
<%
  //Oggetti per l'applicazione dello stile
 // htmlStreamTop = StyleUtils.roundTopTable(true);
 // htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>
<body class="gestione" onload="rinfresca();">
  <p class="titolo"> Nulla osta</p>

<af:showErrors/>
<af:showMessages prefix="M_Insert_NullaOsta"/>
<af:showMessages prefix="M_Save_NullaOsta"/>
<af:showMessages prefix="SalvaNullaOstaDoc"/>

<%= htmlStreamTop %>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaConvenzione() && ControllaDate()">

<%@ include file="ProtocollazioneNullaOsta.inc" %>

<script>

function gestisci_Protocollazione(){
		<%-- DOCAREA: basta questo controllo. I valori della protocollazione vengono impostati dalla classe Documento stessa. --%>
		var protocolloLocale = <%=it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale() %>;
		if (!protocolloLocale) return;
	 
		<% if(CODSTATOATTO.equals("NP")) { %>
		if (document.Frm1.CODSTATOATTO.value == "PR"){
			<%  rowsProt = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
	  			if(rowsProt != null && !rowsProt.isEmpty()) { 
					rowProt = (SourceBean) rowsProt.elementAt(0);
		   			numProtV = SourceBeanUtils.getAttrBigDecimal(rowProt, "NUMPROTOCOLLO", null);
	   				numAnnoProtV = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
	   				dataOraProt = (String) rowProt.getAttribute("DATAORAPROT");
	  			}
	  			oraProtV = dataOraProt.substring(11,16);
	  			datProtV = dataOraProt.substring(0,10);
	  		%>
	  		document.Frm1.oraProt.value = "<%=oraProtV%>";
			document.Frm1.dataProt.value= "<%=datProtV%>"; 
			document.Frm1.numProtocollo.value="<%=numProtV%>";  
			document.Frm1.numAnnoProt.value= "<%=numAnnoProtV%>";
			document.Frm1.dataOraProt.value="<%=dataOraProt%>"; 
	  	}
	  	if (document.Frm1.CODSTATOATTO.value == "AN" || document.Frm1.CODSTATOATTO.value == "NP") {
			document.Frm1.numAnnoProt.value = "";
	  		document.Frm1.numProtocollo.value = "";
	  		document.Frm1.dataProt.value = "";
	  		document.Frm1.oraProt.value = "";
	  		document.Frm1.dataOraProt.value = "";
		}
		<%}%>
	}	
	
	function controllaDoc(){
	 	var codStatoAtto = "<%=CODSTATOATTO%>";
	 	document.Frm1.CODSTATOATTO.value = codStatoAtto;
		if (document.Frm1.CODSTATOATTO.value == "NP"){
				document.Frm1.numAnnoProt.value = "";
	  			document.Frm1.numProtocollo.value = "";
	  			document.Frm1.dataProt.value = "";
	  			document.Frm1.oraProt.value = "";
	  			document.Frm1.dataOraProt.value = "";
    	}
    }	

	function aggiornaDocumento(){
		var f;
 		var codStatoAtto = "<%=CODSTATOATTO%>";
 		 
 		if (document.Frm1.CODSTATOATTO.value == codStatoAtto){
	 		alert("Stato atto non modificato");
			return false;
	 	}
	 	
	 	else {
	 	
 		 <% if (nuovoNullaOsta) { %>
 		 	controllaDatARilascio();
 		 <%}%>
 		 f = "AdapterHTTP?PAGE=CMNullaOstaPage&aggiornaDoc=1";
 		 f = f + "&CDNFUNZIONE=<%=_funzione%>";
		 f = f + "&PRGNULLAOSTA=<%=PRGNULLAOSTA%>";
		 f = f + "&cdnLavoratore=<%=cdnLavoratore%>";
		 f = f + "&prgAzienda=<%=prgAzienda%>";
		 f = f + "&prgUnita=<%=prgUnita%>";
		 f = f + "&PRGAZIENDAUTILIZ=<%=PRGAZIENDAUTILIZ%>";
		 f = f + "&PRGUNITAUTILIZ=<%=PRGUNITAUTILIZ%>";
		 f = f + "&CODSTATOATTO=" + document.Frm1.CODSTATOATTO.value;
	     f = f + "&NUMKLONULLAOSTA=<%=NUMKLONULLAOSTA%>";
	     f = f + "&prgConv=<%=prgConv%>";
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
	     f = f + "&infoDatiNOLav=<%=infoDatiNOLav%>";
	     f = f + "&infoDatiNOAzi=<%=infoDatiNOAzi%>";
	     document.location = f;
	 	}
	 }
	 
	 
	function controllaDatARilascio(){
		if (document.Frm1.CODSTATOATTO.value == "NP"){
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
				String dataOdierna = giornoDB + "/" + meseDB + "/" + annoDB;  
			%>
			document.Frm1.DatAcqRil.value = document.Frm1.dataOdierna.value;
			document.Frm1.DatInizio.value = document.Frm1.dataOdierna.value;
		}
		else {
			document.Frm1.DatAcqRil.value = "<%=datProtV%>";
			document.Frm1.DatInizio.value = "<%=datProtV%>";
		}
	}
	
	function fieldChanged() {
		flagChanged = true;
   }
	
	function StampaNullaOsta(){
		var diagnosi = '';
		if (isInSubmit()) return;
		ok=true;
  			if (flagChanged) {
     			if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
         			ok=false;
     			}
  			}
  		if (ok) {
 			<% if(CODMONOCATEGORIA.equals("D") && CODMONOTIPO.equals("M")) {%>
				diagnosi = document.Frm1.diagnosi.value;
			<%} else {%>
				diagnosi = "N";
			<%}%>
			apriGestioneDoc('RPT_STAMPA_NULLAOSTA','&cdnLavoratoreEncrypt=<%=cdnLavoratoreEncrypt%>&cdnLavoratore=<%=cdnLavoratore%>&prgNullaOsta=<%=PRGNULLAOSTA%>&prgDoc=<%=prgDocumento%>&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>&DatInizio=<%=DATAINIZIO%>&diagnosi='+diagnosi,'NULOST');
  		
  		}
  	}	

</script>

  <input type="hidden" name="PAGE" value="CMNullaOstaPage"/>
  <input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
  <input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
  <input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
  <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/> 
  <input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=PRGAZIENDAUTILIZ%>"/> 
  <input type="hidden" name="PRGUNITAUTILIZ" value="<%=PRGUNITAUTILIZ%>"/> 
  <input type="hidden" name="infoDatiNOLav" value="<%=infoDatiNOLav%>"/> 
  <input type="hidden" name="infoDatiNOAzi" value="<%=infoDatiNOAzi%>"/>  
  <input type="hidden" name="prgConv" value="<%=prgConv%>"/> 
  <input type="hidden" name="DatAcqRil" value="<%=DatAcqRil%>" />
  <input type="hidden" name="DatInizio" value="<%=DatInizio%>" />
  <input type="hidden" name="dataOdierna" value="<%=dataOdierna%>"/>
  <input type="hidden" name="FlgCodMonoIO" value="<%=docInOut%>"/>
  <input type="hidden" name="PROVINCIAISCROLD" value="<%=COD_PROVINCIA_ISCR_OLD%>"/>
  <% if (!PRGNULLAOSTA.equals("")) { %>
  	<input type="hidden" name="PRGNULLAOSTA" value="<%= PRGNULLAOSTA %>">
  <% } %>
 
 
<div class='sezione2' id='SedeAzienda'>
	<img id='tendinaAzienda' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("divLookAzi_look"));'/>
	Sede Azienda&nbsp;&nbsp;*
     <% if ( (canModify || nuovoNullaOsta) && infoDatiNOAzi.equals("false")) { %>
        <a href="#" onClick="selezionaAzienda();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
   		&nbsp;<a href="#" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Scollega azienda"></a>
     <% } %>
    <% if ( !prgAzienda.equals("") && !prgUnita.equals("") ) { %>
    <a href="#" onClick="javascript:apriUnitaAziendale(document.Frm1.prgAzienda.value,document.Frm1.prgUnita.value,<%=_funzione%>,'0');">
    <img src="../../img/detail.gif" alt="Dettaglio azienda"></a>
	<%}%>
</div>
        
<div id="divLookAzi_look" style="display:<%=valDisplay%>">

<table class="main">
	<tr>
		<td class="etichetta">Codice fiscale</td>
  		<td class="campo">
			<af:textBox classNameBase="input" title="Sede Azienda" readonly="true" name="aziCodFiscale" size="30" maxlength="16" value="<%=strCodiceFiscaleAzi%>" onKeyUp="fieldChanged();"/>
			<SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('aziCodFiscale')"; </SCRIPT>
			&nbsp;&nbsp;Partita IVA&nbsp;&nbsp;
			<af:textBox classNameBase="input" readonly="true" name="aziPIva" size="30" maxlength="30" value="<%=strPIva%>" onKeyUp="fieldChanged();"/>
		</td>
	</tr>
	<tr valign="top">
		<td class="etichetta">Ragione Sociale</td>
		<td class="campo">
			<af:textBox classNameBase="input" readonly="true" name="aziRagSociale" size="75" maxlength="120" value="<%=strRagSoc%>" onKeyUp="fieldChanged();"/>
		</td>
	</tr>
	<tr valign="top">
		<td class="etichetta">Indir. Sede</td>
		<td class="campo">
			<af:textBox classNameBase="input" readonly="true" name="aziIndirizzo" size="75" maxlength="120" value="<%=strIndSede%>" onKeyUp="fieldChanged();"/>
		</td>
	</tr>
</table>
</div>

<div class='sezione2' id='SedeAzienda'>
	<img id='tendinaAzienda' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("divLookAziUt_look"));'/>
	Sede Azienda Utilizzatrice&nbsp;&nbsp; 
     <% if (canModify || nuovoNullaOsta) { %>
        <a href="#" onClick="selezionaAziendaUt();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
   		&nbsp;<a href="#" onClick="javascript:azzeraAziendaUt();"><img src="../../img/del.gif" alt="Azzera campi azienda"></a>
	 <%}%>
	 <% if ( !PRGAZIENDAUTILIZ.equals("") && !PRGUNITAUTILIZ.equals("") ) { %>
		<a href="#" onClick="javascript:apriUnitaAziendale(document.Frm1.PRGAZIENDAUTILIZ.value,document.Frm1.PRGUNITAUTILIZ.value,<%=_funzione%>,'0');"><img src="../../img/detail.gif" alt="Dettaglio azienda"></a>
	<%}%>
</div>
        
<div id="divLookAziUt_look" style="display:<%=valDisplay%>">

<table class="main">
	<tr>
		<td class="etichetta">Codice fiscale</td>
  		<td class="campo">
			<af:textBox classNameBase="input" readonly="true" name="aziCodFiscaleUt" size="30" maxlength="16" value="<%=strCodFiscAziUt%>" onKeyUp="fieldChanged();"/>
			&nbsp;&nbsp;Partita IVA&nbsp;&nbsp;
			<af:textBox classNameBase="input" readonly="true" name="aziPIvaUt" size="30" maxlength="30" value="<%=strPIvaUt%>" onKeyUp="fieldChanged();"/>
		</td>
	</tr>
	<tr valign="top">
		<td class="etichetta">Ragione Sociale</td>
		<td class="campo">
			<af:textBox classNameBase="input" readonly="true" name="aziRagSocialeUt" size="75" maxlength="120" value="<%=strRagSocUt%>" onKeyUp="fieldChanged();"/>
		</td>
	</tr>
	<tr valign="top">
		<td class="etichetta">Indir. Sede</td>
		<td class="campo">
			<af:textBox classNameBase="input" readonly="true" name="aziIndirizzoUt" size="75" maxlength="120" value="<%=strIndSedeUt%>" onKeyUp="fieldChanged();"/>
		</td>
	</tr>
</table>
</div>

<div class='sezione2' id='Lavoratore'>
	<img id='tendinaLavoratore' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("divLookLav_look"));'/>
	Lavoratore&nbsp;&nbsp;*
   <% if ( (canModify || nuovoNullaOsta) && infoDatiNOLav.equals("false")) { %>
        <a href="#" onClick="selLavIscrCM('ricerca','riempiDatiLavIscrCM');return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
    	&nbsp;<a href="#" onClick="javascript:azzeraLavIscrCM();"><img src="../../img/del.gif" alt="Azzera campi lavoratore"></a>
    <% } %>
    <%@ include file="infoLavoratore.inc" %>  
</div>

<div id="divLookLav_look" style="display:<%=valDisplay%>">

<table class="main">
	<tr>
	   <td class="etichetta">C.F.</td>
	   <td class="campo">
	        <af:textBox classNameBase="input" readonly="true" name="strCodiceFiscaleLav" title="Lavoratore" size="20" maxlength="16" value="<%=strCodiceFiscaleLav%>" onKeyUp="fieldChanged();"/>	
	   		<SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('strCodiceFiscaleLav')"; </SCRIPT>
	   <td>	
	</tr>
	<tr valign="top">	
	   <td class="etichetta">Cognome&nbsp;Nome</td>
	   <td class="campo">	
	   		<af:textBox classNameBase="input" readonly="true" name="strNomeCognomeLav" title="Nome Lavoratore" size="60" maxlength="50" value="<%=strNomeCognomeLav%>" onKeyUp="fieldChanged();"/>	   
	   </td>
	</tr>
	<tr valign="top">	
	   <td class="etichetta">D/A</td>
	   <td class="campo">
	   		<af:comboBox title="D/A" classNameBase="input" name="CODMONOCATEGORIA" onChange="visualizzaStampa();fieldChanged();" disabled="<%=String.valueOf(!canModify)%>" required="true">
	        	<OPTION value="" ></OPTION>
	        	<OPTION value="D" <% if (CODMONOCATEGORIA.equals("D")) {%>selected="true" <%}%> >DISABILE</OPTION>
	            <OPTION value="A" <% if (CODMONOCATEGORIA.equals("A")) {%>selected="true" <%}%> >CATEGORIA PROTETTA EX ART.18</OPTION>
	       	</af:comboBox>
	   </td>
	</tr>
</table>
</div>

<table class="main" width="100%">

<%
	if (!PRGNULLAOSTA.equals(""))  {
%>
<tr>
	<td class="etichetta">Ambito Territoriale</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" name="PROVINCIA_ISCR" value="<%=PROVINCIA_ISCR%>"  
        	readonly="true" size="20"/>
    </td>
</tr>
<%
	}else{
%>

<tr>
    <td class="etichetta">Ambito Territoriale</td>
    <td colspan=3 class="campo">
    	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
        	classNameBase="input" addBlank="true" required="true" onChange="azzeraConvenzioneCambioTerritoriale();" />
    </td>
</tr>
		
<%		
	}
%>
<tr>
	 <td class="etichetta">Tipologia Nulla Osta</td>
		<td class="campo" colspan="3">
			<af:comboBox classNameBase="input" name="CODMONOTIPO" title="Tipologia Nulla Osta" disabled="<%=String.valueOf(!canModify)%>" required = "true" onChange="visualizzaMotivo(this);visualizzaStampa();fieldChanged();">
				<OPTION value="" ></OPTION>
				<OPTION value="M" <% if (CODMONOTIPO.equals("M")) {%>selected="true" <%}%> >NOMINATIVA</OPTION>
            	<OPTION value="R" <% if (CODMONOTIPO.equals("R")) {%>selected="true" <%}%> >NUMERICA</OPTION>
			</af:comboBox>
       		&nbsp;&nbsp;Convenzione&nbsp;
       		<af:comboBox title="Convenzione" classNameBase="input" name="FLGCONVENZIONE" disabled="<%=String.valueOf(!canModify)%>" onChange="fieldChanged();">
        		<OPTION value="" ></OPTION>
        		<OPTION value="S" <% if (FLGCONVENZIONE.equals("S")) {%>selected="true" <%}%> >Sì</OPTION>
        		<OPTION value="N" <% if (FLGCONVENZIONE.equals("N")) {%>selected="true" <%}%> >No</OPTION>
       		</af:comboBox>
       		<% if(prgConv == "" && canModify) {%>
	 		<a href="#" onClick="selezionaConvenzione(this);return false;">
            <img id='IMG0' src="../../img/patto_ass.gif" alt="Associa convenzione"></a>
           	&nbsp;
           	<af:textBox name="assDiss" size="30" classNameBase="input" value="(Associa convenzione)" readonly="true" />
	 		<%} if(prgConv != "" && canModify) {%>
	 		<a href="#" onClick="selezionaConvenzione(this);return false;">
            <img id='IMG0' src="../../img/patto_elim.gif" alt="Dissocia convenzione"></a>
           	&nbsp;
           	<af:textBox name="assDiss" size="30" classNameBase="input" value="(Dissocia convenzione)" readonly="true" />
	 		<% } %>
	 	</td>
</tr>
<tr>
	<td class="etichetta"><div id="labelTipologia" style="display:<%= CODMONOTIPO.equals("M")?"":"none"%>">Motivo</div></td>
    <td class="campo" colspan="5"><div id="campoTipologia" style="display:<%=CODMONOTIPO.equals("M")?"":"none"%>">
		<af:comboBox title="Motivo" classNameBase="input" name="CODMOTIVO" selectedValue="<%=CODMOTIVO%>" addBlank="true" moduleName="M_MotivoRichiesta" onChange="fieldChanged();" disabled="<%=String.valueOf(!canModify)%>" />
	   	</div>
	</td>            
</tr> 
<tr><td colspan="8"><div class="sezione2"></div></td></tr>
<tr>
	<td class="etichetta">Data inizio</td>	
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
					<af:textBox type="date" title="Data inizio" classNameBase="input"  name="DATAINIZIOCM" value="<%=DATAINIZIO%>" onKeyUp="fieldChanged();" size="12" maxlength="10" readonly="<%=String.valueOf(!canModify)%>" validateOnPost="true" required="true"/>
				</td>
				<td>&nbsp;</td>
				<td class="etichetta2">Tempo</td>
				<td class="campo2">
					<af:comboBox classNameBase="input" name="CODMONOTEMPO" title="Tempo" disabled="<%=String.valueOf(!canModify)%>" onChange="visualizzaTempo(this);aggiornaTipoContrattoNOsta('','');fieldChanged();" required="true">
						<option value="" ></option>
						<option value="D" <% if (CODMONOTEMPO.equals("D")) {%>selected="true" <%}%>>Determinato</option>
						<option value="I" <% if (CODMONOTEMPO.equals("I")) {%>selected="true" <%}%>>Indeterminato</option>
					</af:comboBox>
				</td>
				<td>&nbsp;</td>
					<td id="labeldata" style="display:<%=CODMONOTEMPO.equals("D")?"":"none"%>">Data&nbsp;fine&nbsp;&nbsp;</td>
					<td id="campodata" style="display:<%=CODMONOTEMPO.equals("D")?"":"none"%>">	 
				<af:textBox type="date" title="Data fine" name="DATFINE" classNameBase="input" onKeyUp="fieldChanged();" value="<%=DATFINE%>" size="12" maxlength="10" readonly="<%=String.valueOf(!canModify)%>" validateOnPost="true"/>
			</td>
		  </tr>
	   </table>
	</td>
</tr>
<tr>
	<td class="etichetta">Orario</td>	
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
					<af:comboBox classNameBase="input" name="CODORARIO" selectedValue="<%=CODORARIO%>" title="Orario" addBlank="true" disabled="<%=String.valueOf(!canModify)%>" moduleName="ComboTipoOrario" onChange="visualizzaOrario(this);fieldChanged();">
					</af:comboBox>
				</td>
				<td>&nbsp;</td>
				<td id="labelore" style="display:<%=(CODORARIO.equals("P") || CODORARIO.equals("V") || CODORARIO.equals("M") )?"":"none"%>">Ore&nbsp;settimanali&nbsp;</td>
				<td id="campoore" style="display:<%=(CODORARIO.equals("P") || CODORARIO.equals("V") || CODORARIO.equals("M") )?"":"none"%>">
					<af:textBox classNameBase="input" name="DECORESETT" value="<%=DECORESETT%>" size="10" maxlength="10" onKeyUp="fieldChanged();" readonly="<%=String.valueOf(!canModify)%>" validateOnPost="true"/>
				</td>
		  </tr>
	   </table>
	</td>
</tr>
<tr>
	<td class="etichetta">Tipo Contratto</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" title="Tipo Contratto" type="text" name="codTipoContratto" onKeyUp="fieldChanged();" value="<%=codTipoContratto%>" size="8" maxlength="7" readonly="<%=String.valueOf(!canModify)%>" validateWithFunction='<%= (canModify) ? "controllaTipoContrattoOnSubmit" : ""%>'/>&nbsp;
		<af:textBox type="hidden" name="codTipoContrattoHid" value="<%=codTipoContratto%>"/>																																																		
	<% if (canModify) {%>
			<a href="javascript:cercaTipoContrattoNOsta('codice');">
			<img src="../../img/binocolo.gif" alt="Cerca per codice"></a>&nbsp;
		<%}%>              
		<af:textBox type="text" classNameBase="input" name="strTipoContratto"  onKeyUp="fieldChanged();" value="<%=strTipoContratto%>" size="50" readonly="<%=String.valueOf(!canModify)%>" validateOnPost="true" />&nbsp;
		<af:textBox type="hidden" name="strTipoContrattoHid" value="<%=strTipoContratto%>" />
		<%if (canModify) {%>
			<a href="javascript:cercaTipoContrattoNOsta('descrizione');">
			<img src="../../img/binocolo.gif" alt="Cerca per descrizione"></a>
		<%}%>
	</td>
</tr>
<tr>
	<td class="etichetta">Qualifica</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" title="Qualifica" name="CODMANSIONE" value="<%=CODMANSIONE%>" onKeyUp="fieldChanged();" size="7" readonly="<%=String.valueOf(!canModify)%>" maxlength="7" validateWithFunction='<%= (canModify) ? "checkMansione" : ""%>'/>
		<af:textBox type="hidden" name="codMansioneHid" value="<%=CODMANSIONE%>" />  
		<%if (canModify) {%>
		    <a href="javascript:selectMansioneOnClickNOsta(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE);">
			<img src="../../img/binocolo.gif" alt="Cerca"></A>
		<%}%> 
		<af:textBox classNameBase="input" type="text" size="60" name="DESCMANSIONE" onKeyUp="fieldChanged();" value="<%=DESCMANSIONE%>" readonly="<%=String.valueOf(!canModify)%>" />
		<af:textBox type="hidden" name="strTipoMansione" value="<%=DESCMANSIONE%>" />
		<%if (canModify) {%>
			<A href="javascript:selectMansionePerDescrizioneNOsta(document.Frm1.DESCMANSIONE);">
			<img src="../../img/binocolo.gif" alt="Cerca per descrizione"></A>
		<%}%>
		</td>	
</tr>
<tr>
	<td class="etichetta">Retribuzione</td>
	<td class="campo">
		<af:textBox classNameBase="input" name="DECRETRIBUZIONE" value="<%=DECRETRIBUZIONE%>" onKeyUp="fieldChanged();" title="retribuzione" maxlength="10" size="10" readonly="<%=String.valueOf(!canModify)%>"/>€
	</td>
</tr>
<tr>
    <td class="etichetta">Codice CCNL</td>
    <td class="campo" colspan="5">
     <af:textBox classNameBase="input" type="text" name="codCCNL" onKeyUp="PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');fieldChanged();" readonly="<%=String.valueOf(!canModify)%>" size="5" maxlength="4" value="<%=codCCNL%>" validateWithFunction='<%= (canModify) ? "controllaCCNLOnSubmit" : ""%>' />
      <af:textBox type="hidden" name="codCCNLHid" value="<%=codCCNL%>"/>
      <%if (canModify) {%>
      <A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');">
      <IMG name="image" src="../../img/binocolo.gif" alt="cerca per codice"/></a>
      <%}%> 
      <af:textBox type="text" classNameBase="input" name="strCCNL" onKeyUp="PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, Frm1.strCCNLHid, 'descrizione');fieldChanged();" readonly="<%=String.valueOf(!canModify)%>" size="60" value="<%=strCCNL%>" />
      <af:textBox type="hidden" name="strCCNLHid" value="<%=strCCNL%>" />
      <%if (canModify) {%>
      <A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'descrizione');">
      <IMG name="image" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
      
      <%}%>
    </td>
</tr>
<tr>
	<td class="etichetta">Agevolazione</td>	
	<td colspan="5" class="campo2">
		<af:comboBox classNameBase="input" name="CODAGEVOLAZIONE" selectedValue="<%=codAgevolazione%>" title="Agevolazione" addBlank="true" disabled="<%=String.valueOf(!canModify)%>" moduleName="ComboAgevolazioneNullaOsta" onChange="fieldChanged();" />
	</td>
</tr>
<tr>
	<td class="etichetta">Data spedizione</td>	
	<td class="campo">
		<af:textBox type="date" classNameBase="input" title="Data spedizione" name="DATSPEDIZIONE" value="<%=DATSPEDIZIONE%>" onKeyUp="fieldChanged();"  size="12" maxlength="10" validateOnPost="true" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>
</table>  
&nbsp;&nbsp;&nbsp;&nbsp;
<table> 
  <%if (canInsert) {
	if(nuovoNullaOsta){%>
		<tr>
			<td>
				<input type="submit" name="Salva" class="pulsanti" value="Salva" onClick="controllaDatARilascio()"/>
				<input type="hidden" name="inserisciDoc" value="1"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="reset" name="reset" class="pulsanti" value="Annulla" onClick="ripristinaDaAnnulla();ripristinaIconaConvCollegata();"/>
	
   <%}
   } if(canUpdate){
		if(!nuovoNullaOsta && canModify) {%>
		<tr>
			<td>
				<input type="submit" name="Aggiorna" class="pulsanti" value="Aggiorna" onClick="controllaDoc()"/>
				<input type="hidden" name="aggiornamento" value="1">
				<input type="hidden" name="NUMKLONULLAOSTA" value="<%= NUMKLONULLAOSTA %>">
				&nbsp;&nbsp;&nbsp;&nbsp; 
				<input type="reset" name="reset" class="pulsanti" value="Annulla" onClick="ripristinaDaAnnulla();ripristinaIconaConvCollegata();"/>
		<%} 
	}%>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onClick="tornaLista();"/>
   		&nbsp;&nbsp;&nbsp;&nbsp;
   	<% if(canDocAss) {
   		if(!nuovoNullaOsta)  {%>
   			<input class="pulsanti" type ="button" value="Documenti associati" onClick="docAssociati('<%=cdnLavoratore%>','CMNullaOstaPage','<%=_funzione%>','','<%=PRGNULLAOSTA%>')"/>  
   		<%}
    }%>
    
    </table> 
    <%if(canPrint && !CODSTATOATTO.equals("AN") && !nuovoNullaOsta) {%>
    
    <div class='sezione2' id='Stampa'>
		<img id='tendinaStampa' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("divStampa_look"));'/>Stampa&nbsp;&nbsp; 
    </div>
    <div id="divStampa_look" style="display:<%=displayStampa%>">
	<table style="border-collapse:collapse">
		<tr><td colspan=2>&nbsp;</td></tr>
			<tr>
				<td class="campo2" nowrap="nowrap">
				  <div id="sezDia" style="display:<%=(CODMONOCATEGORIA.equals("D")&& CODMONOTIPO.equals("M"))?"":"none"%>">
				  Con diagnosi
   					<af:comboBox classNameBase="input" name="diagnosi" selectedValue="<%=diagnosi%>"> 
            			<option value="S"selected>Si</option>
            			<option value="N">No</option>
            		</af:comboBox>
        		</div>
        		<td>&nbsp;&nbsp;</td>
				<td nowrap="nowrap">
				&nbsp;&nbsp;&nbsp;&nbsp;
    				<input type="button" name="stampa" class="pulsanti" value="Stampa" onClick="StampaNullaOsta();"/>
    			</td>
			  </tr>
	   </table>
</div>
<%}%>

<%= htmlStreamBottom %>

<% if (!nuovoNullaOsta){ %>
<center>
	<table>
		<tr>
			<td align="center">
				<%operatoreInfo.showHTML(out);%>
			</td>
		</tr>
	</table>
</center>
<%}%>
</af:form>  
</body>
</html>