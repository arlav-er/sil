<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.module.movimenti.constant.Properties,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
 InfoRegioneSingleton regione = InfoRegioneSingleton.getInstance();  
 String regionePolo = regione.getCodice(); 
 String azioni = "Azioni";
 if(Properties.RER.equalsIgnoreCase(regionePolo)) {	
	azioni = "Attività";
 }

 String _page = (String) serviceRequest.getAttribute("PAGE"); 

 // elimina dalla request l'attributo CHIUDI_DID, 
 // che rappresenta la pressione del relativo pulsante di chiusura multipla did
 // per evitare che questo valore venga salvato dalla lista
 // con conseguente esecuzione del modulo di chiusura multipla did anche
 // quando non richiesto (vedi quando si clicca su pagina successiva)
 serviceRequest.delAttribute("CHIUDI_DID");
 //stesso discorso per l'attributo AGGIORNA_ESITI
 serviceRequest.delAttribute("AGGIORNA_ESITI");
  	
/*  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  
	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{

  // NOTE: Attributi della pagina (pulsanti e link) 
  boolean canInsert = attributi.containsButton("nuovo");
  boolean canDelete=attributi.containsButton("rimuovi");

*/  
  
 // da togliere ovviamente
 PageAttribs attributi = new PageAttribs(user, _page);
 boolean canInsert=true;
 boolean canDelete=true;
 boolean canPrint = attributi.containsButton("STAMPA");
 boolean canPrintSelected = attributi.containsButton("STAMPA_FILTRATI");
 boolean canAzioniSAP = attributi.containsButton("AZIONI_SAP");
 
 boolean canAnnullaMultiploEsiti = false;
 String tipoGruppoCollegato = user.getCodTipo();
 if (tipoGruppoCollegato.equalsIgnoreCase(MessageCodes.Login.COD_TIPO_GRUPPO_SOGGETTI_ACCREDITATI) && Properties.RER.equalsIgnoreCase(regionePolo)) {
	canAnnullaMultiploEsiti = true;		  
 }
 
 List sezioni = attributi.getSectionList();
 boolean canVisualizzaChiusuraDidDaProfilatura = sezioni.contains("CLOSEDID");
  
  //********************
  
  
 //Programmi
 
 String prgprogrammaq = StringUtils.getAttributeStrNotNull(serviceRequest,"prgprogrammaq");
 
 //leggo i parametri di richiesta 
 String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 String codServizio=StringUtils.getAttributeStrNotNull(serviceRequest, "codServizio");
 String dataColloquioDa=StringUtils.getAttributeStrNotNull(serviceRequest, "dataColloquioDa");
 String dataColloquioA=StringUtils.getAttributeStrNotNull(serviceRequest, "dataColloquioA");
 String prgAzioniRag=StringUtils.getAttributeStrNotNull(serviceRequest, "prgAzioniRag"); 
 String prgAzioni=StringUtils.getAttributeStrNotNull(serviceRequest, "prgAzioni");
 String dataStimataDa=StringUtils.getAttributeStrNotNull(serviceRequest, "dataStimataDa");  
 String dataStimataA=StringUtils.getAttributeStrNotNull(serviceRequest, "dataStimataA"); 
 String codEsito=StringUtils.getAttributeStrNotNull(serviceRequest, "esito");
 String codEsitoSifer=StringUtils.getAttributeStrNotNull(serviceRequest, "codEsitoSifer");
 String azioniEsitoDiverso=StringUtils.getAttributeStrNotNull(serviceRequest, "azioniEsitoDiverso");
 String codEsitoRendicont=StringUtils.getAttributeStrNotNull(serviceRequest, "esitorendicont");
 String dataSvolgimentoDa=StringUtils.getAttributeStrNotNull(serviceRequest, "dataSvolgimentoDa");  
 String dataSvolgimentoA=StringUtils.getAttributeStrNotNull(serviceRequest, "dataSvolgimentoA");   
 String azioniNonConclCheck=StringUtils.getAttributeStrNotNull(serviceRequest, "azioniNonConclCheck");  
 String azioniVoucherCheck=StringUtils.getAttributeStrNotNull(serviceRequest, "azioniVoucherCheck");
 String cfEnteAtt=StringUtils.getAttributeStrNotNull(serviceRequest, "cfEnteAtt");
 String sedeEnteAtt=StringUtils.getAttributeStrNotNull(serviceRequest, "sedeEnteAtt");
 String codCPI=StringUtils.getAttributeStrNotNull(serviceRequest, "codCPI");
 
 String cpi_H=StringUtils.getAttributeStrNotNull(serviceRequest, "cpi_H");
 String servizio_H=StringUtils.getAttributeStrNotNull(serviceRequest, "servizio_H");
 String obiettivo_H=StringUtils.getAttributeStrNotNull(serviceRequest, "obiettivo_H");
 String azione_H=StringUtils.getAttributeStrNotNull(serviceRequest, "azione_H");
 
 String canCIG = (String) serviceResponse.getAttribute("M_Configurazione_CIG.ROWS.ROW.canCIG");
	
//Variabili necessarie per il pulsante "Ritorno ai percorsi" (solo da menu contestuale)
 String prgColloquio = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGCOLLOQUIO"); 
 String cdnFunzione = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNFUNZIONE"); 

 String DATFINE = StringUtils.getAttributeStrNotNull(serviceRequest, "DATFINE");
 String CODMOTIVOFINEATTO = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMOTIVOFINEATTO");
 String NUMDELIBERA = StringUtils.getAttributeStrNotNull(serviceRequest, "NUMDELIBERA");
 
 String esitoFiltro = StringUtils.getAttributeStrNotNull(serviceRequest, "esitoFiltro");
 
 String MODULE = StringUtils.getAttributeStrNotNull(serviceRequest, "MODULE");
 String MESSAGE = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
 String LIST_PAGE = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");
 
 //verifico se sono in ambito contestuale
 boolean isContestuale=serviceRequest.containsAttribute("CDNLAVORATORE");
 String cdnLavoratore=""; 
 InfCorrentiLav infCorrentiLav = null;
 
 
 if (isContestuale) {
 	cdnLavoratore=(String) StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
	infCorrentiLav= new InfCorrentiLav(sessionContainer, cdnLavoratore, user); 
 }  

 	// configurazione chiusura did multipla
 	boolean canChiusuraDidMultipla = false;
	String numConfigurazioneChiusuraDidMultipla = SourceBeanUtils.getAttrStrNotNull(serviceResponse, "M_GetNumConfigurazioneChiusuraDidMultipla.ROWS.ROW.NUM");
 	if ("1".equals(numConfigurazioneChiusuraDidMultipla)) {
 		canChiusuraDidMultipla = true;
 	}
 	
 	canChiusuraDidMultipla = canChiusuraDidMultipla && "".equals(prgprogrammaq) && !isContestuale && canVisualizzaChiusuraDidDaProfilatura; 
 	
 	String chiudiDid = serviceRequest.getAttribute("CHIUDI_DID") == null ? "" : (String)serviceRequest.getAttribute("CHIUDI_DID"); 	
 	if (chiudiDid != "" || ("CHIUDI_DID").equalsIgnoreCase(chiudiDid)) {
 		chiudiDid = "";
 	}
 	
 	canAnnullaMultiploEsiti = canAnnullaMultiploEsiti && "".equals(prgprogrammaq) && isContestuale;
 	
 	String aggiornaEsiti = serviceRequest.getAttribute("AGGIORNA_ESITI") == null ? "" : (String)serviceRequest.getAttribute("AGGIORNA_ESITI"); 	
 	if (aggiornaEsiti != "" || ("AGGIORNA_ESITI").equalsIgnoreCase(aggiornaEsiti)) {
 		aggiornaEsiti = "";
 	}	
 	
	String labelServizio = "Servizio";
	String labelAzione = "Azione";
	String labelAzioni = "Azioni";
	String labelObiettivo = "Obiettivo";
	String umbriaGestAz = "0";
	if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
		umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
	}
	if(umbriaGestAz.equalsIgnoreCase("1")){
		labelServizio = "Area";
		labelAzione = "Misura";
		labelAzioni = "Misure";
		labelObiettivo = "Servizio";
	}
	
	String queryString = "PAGE=AzioniConcordateListaPage&CDNFUNZIONE=" + _funzione + "&codServizio=" + codServizio + "&dataColloquioDa=" + dataColloquioDa + 
			"&dataColloquioA=" + dataColloquioA + "&prgAzioniRag=" + prgAzioniRag + "&prgAzioni=" + prgAzioni + "&dataStimataDa=" + dataStimataDa + "&dataStimataA=" + dataStimataA + 
			"&esito=" + codEsito + "&esitorendicont=" + codEsitoRendicont + "&codEsitoSifer=" + codEsitoSifer + "&azioniEsitoDiverso=" + azioniEsitoDiverso + "&dataSvolgimentoDa=" + 
			dataSvolgimentoDa + "&dataSvolgimentoA=" + dataSvolgimentoA + "&azioniNonConclCheck=" + azioniNonConclCheck + "&azioniVoucherCheck=" + azioniVoucherCheck + 
			"&cfEnteAtt=" + cfEnteAtt + "&sedeEnteAtt=" + sedeEnteAtt + "&codCPI=" + codCPI + "&canCIG=" + canCIG + "&esitoFiltro=" + esitoFiltro;
	if (isContestuale) queryString += "&cdnLavoratore=" + cdnLavoratore;
	if (!"".equals(MODULE)) queryString += "&MODULE=" + MODULE;
	if (!"".equals(MESSAGE)) queryString += "&MESSAGE=" + MESSAGE;
	if (!"".equals(LIST_PAGE)) queryString += "&LIST_PAGE=" + LIST_PAGE;
%>
 
<html>
<head>
  <title>Estrazione <%=labelAzioni %> Concordate</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/> 
 <af:linkScript path="../../js/"/>
 <script type="text/Javascript">
	function stampa_azioni_selezionate() {
	   var count = 0;
	   var pkAzioneConcordata = "";
	   
	   if(form1.checkbox_azione_concordata!=undefined) {
			var lenChecks = form1.checkbox_azione_concordata.length;

			if (!lenChecks) {
				// se esiste checkbox_azione_concordata, ma non è un array (non ha length)
				// allora è il caso in cui è presente un unico checkbox
				if (form1.checkbox_azione_concordata.checked) {
					count = 1;
					pkAzioneConcordata+="&pkAzioneConcordata="+form1.checkbox_azione_concordata.value;
				}
			} else {
				for(i=0;i<lenChecks;i++) {
					if (form1.checkbox_azione_concordata[i].checked) {
						count += 1;
						pkAzioneConcordata+="&pkAzioneConcordata="+form1.checkbox_azione_concordata[i].value;
					}
				}
			}			
		} else {
			alert("Nessuna selezione possibile");
			return false;
		}
		if (!count) {
			alert("Effettuare almeno una selezione");
			return false;
		}

		stampa_azioni(pkAzioneConcordata);
	 }

	function stampa_azioni(pkAzioneConcordata) {
		var parameters = '&cdnLavoratore=<%=cdnLavoratore%>&codServizio=<%=codServizio%>&dataColloquioDa=<%=dataColloquioDa%>&dataColloquioA=<%=dataColloquioA%>' +
	   	'&prgAzioniRag=<%=prgAzioniRag%>&prgAzioni=<%=prgAzioni%>&dataStimataDa=<%=dataStimataDa%>&dataStimataA=<%=dataStimataA%>&esito=<%=codEsito%>' +
	   	'&esitorendicont=<%=codEsitoRendicont%>&codEsitoSifer=<%=codEsitoSifer%>&azioniEsitoDiverso=<%=azioniEsitoDiverso%>&dataSvolgimentoDa=<%=dataSvolgimentoDa%>' +
	   	'&dataSvolgimentoA=<%=dataSvolgimentoA%>&azioniNonConclCheck=<%=azioniNonConclCheck%>&azioniVoucherCheck=<%=azioniVoucherCheck%>' +
	   	'&cfEnteAtt=<%=cfEnteAtt%>&sedeEnteAtt=<%=sedeEnteAtt%>&codCPI=<%=codCPI%>&canCIG=<%=canCIG%>&esitoFiltro=<%=esitoFiltro%>';

		if (pkAzioneConcordata != null) parameters += pkAzioneConcordata;

	    apriGestioneDoc('RPT_AZIONI_CONCORDATE',parameters,'ALVARO');    
	 }	 
	</script>
	 
    <!-- include lo script che permette di aprire la PopUp che gestisce i documenti (salvataggio/protocollazione) -->
  <%@ include file="../documenti/_apriGestioneDoc.inc"%>

 <script type="text/Javascript">
 
  function tornaAlPercorso(){
    var urlPage = "AdapterHTTP?";
	urlPage+="cdnLavoratore=<%=cdnLavoratore %>&";
	urlPage+="CDNFUNZIONE=<%=cdnFunzione %>&";
    urlPage+="PRGCOLLOQUIO=<%=prgColloquio%>&";
	urlPage+="PAGE=PERCORSIPAGE";
	urlPage+="&statoSezioni=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"statoSezioni")%>";
    urlPage+="&pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"PAGECHIAMANTE")%>";
    setWindowLocation(urlPage);
  }

 function visualizzaAzioniSAP() {
	 var urlpage="AdapterHTTP?";
	 var labelAzioniTmp = "<%=labelAzioni%>";
	 urlpage+="CDNFUNZIONE=<%=_funzione%>&PAGE=AzioniConcordateSapPage&CDNLAVORATORE=<%=cdnLavoratore%>";
	 window.open(urlpage, labelAzioniTmp + ' concordate per SAP', 'toolbar=NO,statusbar=YES,width=900,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
 }
 
 function tornaAllaRicerca()
  {  // Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;
     
     url="AdapterHTTP?PAGE=AzioniConcordateRicercaPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&codServizio="+"<%=codServizio%>";
     url += "&dataColloquioDa="+"<%=dataColloquioDa%>";
     url += "&dataColloquioA="+"<%=dataColloquioA%>";
     url += "&prgAzioniRag="+"<%=prgAzioniRag%>";
     url += "&prgAzioni="+"<%=prgAzioni%>";
     url += "&dataStimataDa="+"<%=dataStimataDa%>";
     url += "&dataStimataA="+"<%=dataStimataA%>";
     url += "&codEsito="+"<%=codEsito%>";
     url += "&codEsitoSifer="+"<%=codEsitoSifer%>";
     url += "&azioniEsitoDiverso="+"<%=azioniEsitoDiverso%>";
     url += "&codEsitoRendicont="+"<%=codEsitoRendicont%>";  
     url += "&dataSvolgimentoDa="+"<%=dataSvolgimentoDa%>";
     url += "&dataSvolgimentoA="+"<%=dataSvolgimentoA%>";
     url += "&azioniNonConclCheck="+"<%=azioniNonConclCheck%>";
     url += "&azioniVoucherCheck="+"<%=azioniVoucherCheck%>";
     url += "&cfEnteAtt="+"<%=cfEnteAtt%>";
     url += "&sedeEnteAtt="+"<%=sedeEnteAtt%>";
     url += "&codCPI="+"<%=codCPI%>";                
     setWindowLocation(url);
  }

 function indietroProgramma() {
	    url="AdapterHTTP?PAGE=ProgrammiPage";
	    url += "&CDNFUNZIONE="+"<%=_funzione%>";
	    url += "&prgprogrammaq="+"<%=prgprogrammaq%>";	
	    setWindowLocation(url);
	}
   
   <%if (!isContestuale) {%>
	if (window.top.menu != undefined){
  		window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
	}
   <%}%>

   function checkAllCheckboxMob() {
		if(form1.checkbox_azione_concordata!=undefined) {
			var b = form1.SelDeselAll.checked;
			var lenChecks = form1.checkbox_azione_concordata.length;

			if (!lenChecks) {
				form1.checkbox_azione_concordata.checked = b;
			} else {
			  	for(i=0; i<lenChecks; i++) {
			  		form1.checkbox_azione_concordata[i].checked = b;
				}
			}
		} else {
			alert("Nessuna selezione possibile");
			form1.SelDeselAll.checked = !form1.SelDeselAll.checked;
			return false;
		}
	}

	function checkAtLeastOneCheckboxMob(from) {
	   var count = 0;
	   
	   if(form1.checkbox_azione_concordata!=undefined) {
			var lenChecks = form1.checkbox_azione_concordata.length;

			if (!lenChecks) {
				// se esiste checkbox_azione_concordata, ma non è un array (non ha length)
				// allora è il caso in cui è presente un unico checkbox
				if (form1.checkbox_azione_concordata.checked) {
					count = 1;
				}
			} else {
				for(i=0;i<lenChecks;i++) {
					if (form1.checkbox_azione_concordata[i].checked) {
						count += 1;
					}
				}
			}
		} else {
			alert("Nessuna selezione possibile");
			return false;
		}
		if (!count) {
			alert("Effettuare almeno una selezione");
			return false;
		} 

		if (from == "CHIUDI_DID") {
			var chiudiDid = document.getElementById("CHIUDI_DID");
			chiudiDid.value = "CHIUDI_DID";
		} else if (from == "AGGIORNA_ESITI") {
			var aggiornaEsiti = document.getElementById("AGGIORNA_ESITI");
			aggiornaEsiti.value = "AGGIORNA_ESITI";
		}

		return true;
   }

   function completeSubmit(from) {  

	var complSub = true;
	if (from != "") {
		complSub = checkAtLeastOneCheckboxMob(from);
	}
	   
	if (complSub) { 
		prepareSubmit();
		document.form1.submit();
	}  
   }	   

   function apriDettaglioVoucher(url) {
   		var f = "AdapterHTTP?PAGE=DettaglioScadenzaVoucherPage&" + url;
	    var t = "Dettaglio Voucher";	    
	    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=500,top=250,left=250";
	    window.open(f, t, feat);
   }
   
</script>
 
</head>
<!--<body  onload="checkError();" class="gestione" > -->
<body  class="gestione" onload="rinfresca();">

<br/><p class="titolo">ELENCO <%=labelAzioni.toUpperCase() %> CONCORDATE</p>


<%String attr   = null;
  String attr2 = null;
  String valore = null;
  String valore2 = null;
  String txtOut = "";
  
  attr= "servizio_H";
  valore = (String) serviceRequest.getAttribute(attr);
  if(valore != null && !valore.equals(""))  {
  	txtOut += labelServizio + ": <strong>"+ valore +"</strong>; ";
  }
  attr= "dataColloquioDa";
  attr2= "dataColloquioA";
  valore = (String) serviceRequest.getAttribute(attr);
  valore2= (String) serviceRequest.getAttribute(attr2);
  if(valore != null && !valore.equals(""))
  	{txtOut += "Data programma da: <strong>"+ valore +"</strong> a: <strong> "+ valore2 +"</strong>; ";
   }
   attr= "obiettivo_H";
   valore = (String) serviceRequest.getAttribute(attr);
   if(valore != null && !valore.equals("")) 
   	 {txtOut += labelObiettivo + ": <strong>"+ valore +"</strong>; ";
   }
   attr= "azione_H";
   valore = (String) serviceRequest.getAttribute(attr);
   if(valore != null && !valore.equals(""))
 	 {txtOut += labelAzione + ": <strong>"+ valore +"</strong>; ";
   }%>  
  <%attr= "dataStimataDa";
    attr2= "dataStimataA";
    valore = (String) serviceRequest.getAttribute(attr);
    valore2= (String) serviceRequest.getAttribute(attr2);
    if(valore != null && !valore.equals(""))
      {txtOut += "Data stimata da: <strong>"+ valore +"</strong> a: <strong> " + valore2 + "</strong>; ";
    }%>
  <%-- <%attr= "esito_H";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
      {txtOut += "Esito: <strong>"+ valore +"</strong>; ";
    }%> --%>
    
 <% { String strDescrizione = "";
 	String strCodice = "";
 	String strCodiceSel = "";
    SourceBean rowDescrizione = null;
    Vector vettEsitiSel = new Vector();
    Vector vettEsiti = serviceResponse.getAttributeAsVector("M_DeEsito.ROWS.ROW");
    String strEsiti = (String)serviceRequest.getAttribute("esito");
   	if (strEsiti != null && !strEsiti.equals("")) {
   	  vettEsitiSel = StringUtils.split(strEsiti,",");
    }
   	
   	if (vettEsiti.size() == vettEsitiSel.size()) {
   		strDescrizione = "qualsiasi";
   	}
   	else {	  	
	  	for (int k=0;k<vettEsitiSel.size();k++) {
	  		strCodiceSel = vettEsitiSel.get(k).toString();
	 		for (int i=0;i<vettEsiti.size();i++)  {
	 			rowDescrizione = (SourceBean)vettEsiti.get(i);
	 			strCodice = rowDescrizione.getAttribute("codice").toString();
	 			if (strCodiceSel.equals(strCodice)) {
	 				if (strDescrizione.equals("")) {
						strDescrizione = strDescrizione + rowDescrizione.getAttribute("DESCRIZIONE").toString();
					}
					else {
						strDescrizione = strDescrizione + ',' + rowDescrizione.getAttribute("DESCRIZIONE").toString();
					}
					break;		
	 			}
	 		}
	  	}
   	}
  	 
    if (!strDescrizione.equals("")) {
    	txtOut += "Esito: <strong>"+ strDescrizione +"</strong>; ";
    }
     
    }%>
    
    <%attr= "codEsitoSifer";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	String strDescEsitoSifer = "";
    	SourceBean rowDescSifer = null;
    	String strCodiceSifer = "";
    	Vector vettEsitiSifer = serviceResponse.getAttributeAsVector("M_DeEsito_Sifer.ROWS.ROW");
    	for (int i=0;i<vettEsitiSifer.size();i++)  {
    		rowDescSifer = (SourceBean)vettEsitiSifer.get(i);
    		strCodiceSifer = rowDescSifer.getAttribute("codice").toString();
 			if (valore.equals(strCodiceSifer)) {
 				strDescEsitoSifer = rowDescSifer.getAttribute("DESCRIZIONE").toString();
 				break;
 			}
 		}
    	
    	if (!strDescEsitoSifer.equals("")) {
    		txtOut += "Esito Formazione: <strong>"+ strDescEsitoSifer +"</strong>; ";
    	}
    }%>
    
    <%attr= "azioniEsitoDiverso";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && valore.equalsIgnoreCase("on"))
      {txtOut += " <strong>Solo "+labelAzioni.toLowerCase() +" con Esito Formazione Diverso da Esito SIL</strong>; ";
    }%>
    
    
  <%{ String strDescrizione = "";
 	String strCodice = "";
 	String strCodiceSel = "";
    SourceBean rowDescrizione = null;
    Vector vettEsitiRendicontSel = new Vector();
    Vector vettEsitiRendicont = serviceResponse.getAttributeAsVector("M_DeEsitoRendicont.ROWS.ROW");
    String strEsitiRendicont = (String)serviceRequest.getAttribute("esitorendicont");
   	if (strEsitiRendicont != null && !strEsitiRendicont.equals("")) {
   	  vettEsitiRendicontSel = StringUtils.split(strEsitiRendicont,",");
    }
   	
   	if (vettEsitiRendicont.size() == vettEsitiRendicontSel.size()) {
   		strDescrizione = "qualsiasi";
   	}
   	else {	  	
	  	for (int k=0;k<vettEsitiRendicontSel.size();k++) {
	  		strCodiceSel = vettEsitiRendicontSel.get(k).toString();
	 		for (int i=0;i<vettEsitiRendicont.size();i++)  {
	 			rowDescrizione = (SourceBean)vettEsitiRendicont.get(i);
	 			strCodice = rowDescrizione.getAttribute("codice").toString();
	 			if (strCodiceSel.equals(strCodice)) {
	 				if (strDescrizione.equals("")) {
						strDescrizione = strDescrizione + rowDescrizione.getAttribute("DESCRIZIONE").toString();
					}
					else {
						strDescrizione = strDescrizione + ',' + rowDescrizione.getAttribute("DESCRIZIONE").toString();
					}
					break;		
	 			}
	 		}
	  	}
   	}

    
    if (!strDescrizione.equals("")) {
    	txtOut += "Esito Rendicontazione: <strong>"+ strDescrizione +"</strong>; ";
    }
     
    }%>
    
    <%attr= "dataSvolgimentoDa";
	attr2= "dataSvolgimentoA";
    valore = (String) serviceRequest.getAttribute(attr);
    valore2= (String) serviceRequest.getAttribute(attr2);
    if(valore != null && !valore.equals(""))
      {txtOut += "Data svolgimento/conclusione da: <strong>"+ valore +"</strong> a: <strong> " + valore2 + "</strong>; ";
    }%>
  <%attr= "azioniNonConclCheck";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
      {txtOut += " <strong>Solo "+labelAzioni.toLowerCase() +" non concluse</strong>; ";
    }%>
    <%attr= "azioniVoucherCheck";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
      {txtOut += " <strong>Solo Voucher</strong>; ";
    }%>
  <%attr= "cpi_H";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) 
      {txtOut += " Cpi comp.: <strong>" + valore + "</strong>; ";
   }%>
<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%}%>

<af:form name="form1" action="AdapterHTTP" method="POST">

<% if (canChiusuraDidMultipla || (isContestuale && "".equals(prgprogrammaq))) { %>

	<input type="hidden" name="PAGE" value="AzioniConcordateListaPage" />
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>" />
	<input type="hidden" name="codServizio" value="<%=codServizio%>" />
	<input type="hidden" name="dataColloquioDa" value="<%=dataColloquioDa %>" />
	<input type="hidden" name="dataColloquioA" value="<%=dataColloquioA %>" />
	<input type="hidden" name="prgAzioniRag" value="<%=prgAzioniRag %>" />
	<input type="hidden" name="prgAzioni" value="<%=prgAzioni %>" />
	<input type="hidden" name="dataStimataDa" value="<%=dataStimataDa %>" />
	<input type="hidden" name="dataStimataA" value="<%=dataStimataA %>" />
	
	<input type="hidden" name="codEsitoSifer" value="<%=codEsitoSifer %>" />
	<input type="hidden" name="azioniEsitoDiverso" value="<%=azioniEsitoDiverso %>" />
	<input type="hidden" name="esito" value="<%=codEsito %>" />
	
	<input type="hidden" name="esitorendicont" value="<%=codEsitoRendicont %>" />
	<input type="hidden" name="dataSvolgimentoDa" value="<%=dataSvolgimentoDa %>" />
	<input type="hidden" name="dataSvolgimentoA" value="<%=dataSvolgimentoA %>" />
	<input type="hidden" name="azioniNonConclCheck" value="<%=azioniNonConclCheck %>" />
	<input type="hidden" name="azioniVoucherCheck" value="<%=azioniVoucherCheck %>" />
	<input type="hidden" name="cfEnteAtt" value="<%=cfEnteAtt %>" />
	<input type="hidden" name="sedeEnteAtt" value="<%=sedeEnteAtt %>" />
	<input type="hidden" name="codCPI" value="<%=codCPI %>" />
	
	<input type="hidden" name="cpi_H" value="<%=cpi_H %>" />
	<input type="hidden" name="servizio_H" value="<%=servizio_H %>" />
	<input type="hidden" name="obiettivo_H" value="<%=obiettivo_H %>" />
	<input type="hidden" name="azione_H" value="<%=azione_H %>" />
	
	<% if (!"".equals(MODULE)) { %>
	<input type="hidden" name="MODULE" value="<%=MODULE %>" />
	<% } %>
	<% if (!"".equals(MESSAGE)) { %>
	<input type="hidden" name="MESSAGE" value="<%=MESSAGE %>" />
	<% } %>
	<% if (!"".equals(LIST_PAGE)) { %>
		<input type="hidden" name="LIST_PAGE" value="<%=LIST_PAGE %>" />
	<% } %>
	
	<% if (canChiusuraDidMultipla) { %>
	<table align="center">
	<tr>
		<td class="azzurro_bianco">	
			<input type="hidden" id="CHIUDI_DID" name="CHIUDI_DID" value="<%=chiudiDid%>" />
			<table width="100%">
				
				<tr>
					<td>Data chiusura atto</td>
					<td>Motivo chiusura atto</td>
					<td>Numero di determina</td>
				</tr>
				
				<tr>
					
					<td>
						<af:textBox type="date" 
									name="DATFINE" 
									title="Data chiusura atto" 
									validateOnPost="true" 
									value="" 
									size="10" 
									maxlength="10" 
									required="true" 
						/>
					</td>
					
					<td>
						<af:comboBox 	classNameBase="input" 
										addBlank="true" 
										name="CODMOTIVOFINEATTO"
					    				multiple="false"
			            				moduleName="M_MotFineAtto" 
			            				required="true" 
			            				selectedValue=""
			            				title="Motivo fine atto"
			            />
					</td>
					
					<td>
						<af:textBox classNameBase="input" 
									name="NUMDELIBERA" 
									value=""
	               					size="15" 
	               					maxlength="20" 
	               					title="Numero di determina"
	               					required="false"
	               					type="string" 
	               		/>
					</td>
					
				</tr>
				 
			    <tr>
			    	<td align="center" colspan="3">
				    	<input class="pulsante" type="button" name="chiudiDid" value="Chiudi DID" onclick="completeSubmit('CHIUDI_DID')"/>
			    	</td>
				</tr>
				
			</table>
		</td>			
		<td>&nbsp;</td>
	</tr>
	</table>		
	<%} else if (isContestuale && "".equals(prgprogrammaq)) { 
		infCorrentiLav.show(out);%>
			<table align="center">
			<tr><td>&nbsp;</td></tr>
			<tr>
				<input type="hidden" id="cdnLavoratore" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
				<%if (canAnnullaMultiploEsiti) {%>
				<td class="azzurro_bianco">	
					<input type="hidden" id="AGGIORNA_ESITI" name="AGGIORNA_ESITI" value="<%=aggiornaEsiti%>" />
					<table width="100%">
						<tr>
					    	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						    <td>
						    	<input class="pulsante" type="button" name="aggiornaSelezionati" value="Imposta" onclick="completeSubmit('AGGIORNA_ESITI')"/>
					    	</td>
					    	<td>&nbsp;&nbsp;</td>
		      				<td class="campo">
								<af:comboBox className="inputView" name="conEsitoFiltro" moduleName="M_DeEsito_Filtro" selectedValue="NA" disabled="true" title="Esito"/>
							</td>	
					    </tr>
					</table>
				</td><td>&nbsp;</td>
				<%}%>
				<td class="azzurro_bianco">	
					<table width="100%">
					<tr>
						<td class="etichetta" nowrap>Filtra per Esito</td>
						<td class="campo">
							<af:comboBox 	classNameBase="input" addBlank="true" name="esitoFiltro" multiple="false"
											moduleName="M_DeEsito_Filtro" required="false" selectedValue="<%=esitoFiltro%>" title="Esito"/>
						</td>
						<td>
							<input class="pulsante" type="button" name="Applica" value="Applica" onclick="completeSubmit('')"/>
						</td>
					</tr>
 					</table>
				</td>			
			</tr>
			</table>					  		
    <%} %>						
<% } %>

	<table width="96%" align="center">
		<tr valign="middle">
			<td align="left" class="azzurro_bianco">
			<input type="checkbox" name="SelDeselAll" onClick="javascript:checkAllCheckboxMob();"/>&nbsp;Seleziona/Deseleziona tutti
			</td>
		</tr>
	</table>

<center> 
<font color="red">
    <af:showErrors/>
</font>
<% if (canChiusuraDidMultipla) { %>
<af:showMessages prefix="M_ChiusuraDidMultipla"/>
<% } else if (canAnnullaMultiploEsiti) {%>
<af:showMessages prefix="M_AnnullaMultiploEsitiAttivita"/>
<% } %>
</center>
<af:list moduleName="M_ListaAzioniConcordate" configProviderClass="it.eng.sil.module.patto.DynamicEstrazioneAzioniConcordateConfig" canDelete="<%= canDelete ? \"1\" : \"0\" %>" canInsert="<%= canInsert ? \"1\" : \"0\" %>" getBack="<%= isContestuale ? \"false\" : \"true\"%>"/>
<table align="center">
<%	if (isContestuale && canAzioniSAP) { %>
	<tr>
		<td colspan="2" align="center">
			<input type="button" class="pulsanti" value="Visualizza <%= labelAzioni.toLowerCase() %> conc. per SAP" onclick="visualizzaAzioniSAP();" />
		</td>
	</tr>
<% 	}  
	if (canPrint && "".equals(prgprogrammaq)) { %>
	<tr>
		<td align="center">
			<input type="button" class="pulsanti" value="Stampa" onclick="stampa_azioni()" />
<% 	}  
	if (canPrintSelected && "".equals(prgprogrammaq)) { %>
			<input type="button" class="pulsanti" value="Stampa selezionati" onclick="stampa_azioni_selezionate()" />
		</td>
	</tr>
<% 	} else { %>
		</td>
	</tr>
<%	}
	if (!isContestuale && "".equals(prgprogrammaq)) { %>
	<tr>
		<td colspan="2" align="center">
			<input class="pulsante" type="button" name="torna" value="Torna alla ricerca" onclick="tornaAllaRicerca()"/>
		</td>
	</tr>
<% 	}
	if (!"".equals(prgprogrammaq)) { %>
	<tr>
		<td colspan="2" align="center">
			<input class="pulsante" type="button" name="torna" value="Torna al Programma" onclick="indietroProgramma()"/>
		</td>
	</tr>
<% 	}%>
</table>
</af:form>
<br/>
</body>
</html>