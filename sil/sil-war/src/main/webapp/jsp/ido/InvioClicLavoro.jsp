<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@page import="org.apache.commons.lang.time.FastDateFormat"%>

<%@ page import="
  com.engiweb.framework.base.*,
  it.eng.sil.security.*,
  it.eng.sil.util.*,
  it.eng.afExt.utils.*,
  java.util.*,
  java.lang.*,
  java.math.*,
  it.eng.sil.module.movimenti.constant.Properties
" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String _page = (String) serviceRequest.getAttribute("PAGE");
	int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
	String prgRichiestaAz = (String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
	
	FastDateFormat fdf = FastDateFormat.getInstance("dd/MM/yyyy");
	String data_invio = fdf.format(new Date());
	String dataInvioOld = "";
	BigDecimal prgAzienda=null;
	BigDecimal prgUnita=null;
	String strPrgAziendaMenu="";
	String strPrgUnitaMenu="";
	String strCodiciEsiti = "";
	SourceBean rigaTestata = null;
	SourceBean rigaTestataPubb = null;
	String cdnStatoRich = "";
	String desMansione = "";
	String numLavRichiesti = "";
	String dataPubblicazione = "";
	String mailSede = "";
	String prgUnitaAz = "";
	String codRapportoLav = "";
	String flgNullaOsta = "";
	boolean existSede = false;
	SourceBean contTestata = (SourceBean) serviceResponse.getAttribute("M_GetTestataRichiesta");
	SourceBean contTestataPubb = (SourceBean) serviceResponse.getAttribute("M_GetTestataRichiestaPubb");
	
	SourceBean contInvioClicLavoro = (SourceBean) serviceResponse.getAttribute("M_GetDatiClicLavoro.ROWS.ROW");
	
	SourceBean datiAzienda = (SourceBean) serviceResponse.getAttribute("M_GetDatiSedeRichPersClicLavoro.ROWS.ROW");
	String codAteco = "", codFiscSede = "", ragSocialeSede = "", comuneSede = "";
	if(datiAzienda != null){
		existSede = true;
		codAteco = (String)datiAzienda.getAttribute("CODATECODOT");
		codFiscSede = (String)datiAzienda.getAttribute("STRCODICEFISCALE");
		ragSocialeSede = (String)datiAzienda.getAttribute("STRRAGIONESOCIALE");
		comuneSede = (String)datiAzienda.getAttribute("CODCOM");
		mailSede = (String)datiAzienda.getAttribute("EMAIL");
		prgUnitaAz = ((BigDecimal)datiAzienda.getAttribute("PRGUNITA")).toString();
	}
	
	SourceBean listaMansioni = (SourceBean) serviceResponse.getAttribute("M_GetListaMansioniClicLavoro");
	Vector vectMansioni = listaMansioni.getAttributeAsVector("ROWS.ROW");
	int sizeMansioni = vectMansioni.size();
	if(sizeMansioni == 1){
		SourceBean sb =(SourceBean) vectMansioni.elementAt(0);
		desMansione = sb.getAttribute("DESMANSIONE").toString();
	}
	
	SourceBean listaComuni = (SourceBean) serviceResponse.getAttribute("M_GetListaComuniRichiestaClicLavoro");
	Vector vectComuni = listaComuni.getAttributeAsVector("ROWS.ROW");
	int sizeComuni = vectComuni.size();
	
	Vector rows_VectorTestata = null, rows_VectorTestataPubb = null;
	rows_VectorTestata = contTestata.getAttributeAsVector("ROWS.ROW");
	rows_VectorTestataPubb = contTestataPubb.getAttributeAsVector("ROWS.ROW");
	
	if (rows_VectorTestataPubb.size()!=0) {
	  	rigaTestataPubb=(SourceBean) rows_VectorTestataPubb.elementAt(0);
	  	if(rigaTestataPubb.containsAttribute("datPubblicazione")){
	  		dataPubblicazione = rigaTestataPubb.getAttribute("datPubblicazione").toString();
	  	}
	  	if(rigaTestataPubb.containsAttribute("CODRAPPORTOLAV")){
	  		codRapportoLav = rigaTestataPubb.getAttribute("CODRAPPORTOLAV").toString();
	  	}
	  	if(rigaTestataPubb.containsAttribute("FLGNULLAOSTA")){
	  		flgNullaOsta = rigaTestataPubb.getAttribute("FLGNULLAOSTA").toString();
	  	}
	}
	if (rows_VectorTestata.size()!=0) {
	  	rigaTestata=(SourceBean) rows_VectorTestata.elementAt(0);
	  	numLavRichiesti = rigaTestata.getAttribute("numrichiesta").toString();
	  	cdnStatoRich = rigaTestata.getAttribute("cdnStatoRich").toString();
	  	prgAzienda = (BigDecimal) rigaTestata.getAttribute("PRGAZIENDA");
	  	if (prgAzienda != null) {  
	    	strPrgAziendaMenu = prgAzienda.toString();
	  	}
	  	prgUnita = (BigDecimal) rigaTestata.getAttribute("PRGUNITA");
	  	if (prgUnita != null) {
	    	strPrgUnitaMenu = prgUnita.toString();
	  	}
	}

// 	boolean canModify = true;
	boolean canInvio = false;
	String readOnly = "false";
	boolean canAnteprima = false;
	
	if (cdnStatoRich != null && new Long(cdnStatoRich).intValue() == 5) {
    	readOnly = "true";    	
    }
		
	PageAttribs attributi = new PageAttribs(user, _page);
	canInvio = attributi.containsButton("AGGIORNA");
	canAnteprima = attributi.containsButton("ANTEPRIMA");
	
	boolean canSendReq = canInvio && readOnly.equals("false");
	
	//NOTE: Attributi della pagina (pulsanti e link) 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setPrgAzienda(prgAzienda);
	filter.setPrgUnita(prgUnita);	
		
	String htmlStreamTop = StyleUtils.roundTopTable(canSendReq);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canSendReq);	
	
	String strProfiloRichiesto = "";
	String data_scad = "";
	String codOfferta = "";
	String desStatoInvio = "IN ATTESA DI PRIMO INVIO";
	String statoInvioClicLavoro = null;
	String statoInvioClicLavoroCurrent = null;
	String codCpi = null;
	int cdnUt = user.getCodut();
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	if (cdnTipoGruppo == 1) {
		codCpi = user.getCodRif();
	}
		
	if (contInvioClicLavoro != null){
		dataInvioOld = StringUtils.getAttributeStrNotNull(contInvioClicLavoro, "DATINVIO");
		strProfiloRichiesto = StringUtils.getAttributeStrNotNull(contInvioClicLavoro, "STRPROFILORICHIESTO");
		data_scad = StringUtils.getAttributeStrNotNull(contInvioClicLavoro, "DATSCADENZA");
		codOfferta = StringUtils.getAttributeStrNotNull(contInvioClicLavoro, "CODOFFERTA");
		desStatoInvio = StringUtils.getAttributeStrNotNull(contInvioClicLavoro, "DESCRIZIONESTATOINVIOCL");
		statoInvioClicLavoro = StringUtils.getAttributeStrNotNull(contInvioClicLavoro, "CODSTATOINVIOCL");
		codCpi =  StringUtils.getAttributeStrNotNull(contInvioClicLavoro, "CODCPI");
	} else {
		String dataOdierna = DateUtils.getNow();
	    data_scad = DateUtils.aggiungiNumeroGiorni(dataOdierna, 60);
	}
	
	statoInvioClicLavoroCurrent = statoInvioClicLavoro;
	
	if(statoInvioClicLavoro == null  || "PA".equals(statoInvioClicLavoro)){
		statoInvioClicLavoro = "PA";
	}else {
		statoInvioClicLavoro = "VA";
	}	
	
	SourceBean rowTestataRichiesta = (SourceBean) serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW");
	String datRichiesta = StringUtils.getAttributeStrNotNull(rowTestataRichiesta, "datRichiesta");
	String datScadenzaRich = StringUtils.getAttributeStrNotNull(rowTestataRichiesta, "datScadenza");
	
	BigDecimal prgVacancy = null;
	if (contInvioClicLavoro != null){
		prgVacancy = (BigDecimal)contInvioClicLavoro.getAttribute("PRGVACANCY"); 
	}	
	
	SourceBean rowStatoEvas = (SourceBean) serviceResponse.getAttribute("M_IdoGetStatoRich.ROWS.ROW");
	String codEvasione = "", statoPubblic = "";
	  if( rowStatoEvas != null ) 
	  { 
		  codEvasione   = StringUtils.getAttributeStrNotNull(rowStatoEvas,"CODEVASIONE");
		  statoPubblic  = StringUtils.getAttributeStrNotNull(rowStatoEvas,"STATOPUBBLICAZIONE");
	  }
	
	//String codCpiCapoluogo = "";
	//try {
	//	SourceBean cpiSB = (SourceBean)serviceResponse.getAttribute("M_CL_GET_CPI_CAPOLUOGO.ROWS.ROW");
	//	codCpiCapoluogo = cpiSB.getAttribute("CODCPICAPOLUOGO").toString();
	//} catch (Exception e) { }
	
	// Se la vacancy è scaduta non è possibile procedere al suo invio
	boolean isVacancyScaduta = false; 
	if (DateUtils.compare(new Date(), DateUtils.getDate(data_scad)) > 0) {
		isVacancyScaduta = true;
	}
	
	canSendReq = canSendReq && !isVacancyScaduta;
	boolean sincronizzaButtonEnabled = (canSendReq && (prgVacancy == null || "PI".equals(statoInvioClicLavoroCurrent) || "VI".equals(statoInvioClicLavoroCurrent) ));
	
	//per trento
	boolean BLOCCA_FLUSSO = false;
	String numConfigFlusso = serviceResponse.containsAttribute("M_CONFIG_FLUSSO_VACANCY.ROWS.ROW.NUM")?
	 			serviceResponse.getAttribute("M_CONFIG_FLUSSO_VACANCY.ROWS.ROW.NUM").toString():Properties.DEFAULT_CONFIG;
	if(Properties.CUSTOM_CONFIG.equalsIgnoreCase(numConfigFlusso)){
		BLOCCA_FLUSSO = true;
	}
	if(BLOCCA_FLUSSO){
		readOnly = "true";
	}
%>

	<%@ include file="_infCorrentiAzienda.inc" %> 
	<html>
	<head>
		<title>Invio Cliclavoro</title>
		<link rel="stylesheet" href="../../css/stili.css" type="text/css">
		<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
		<af:linkScript path="../../js/"/>
		<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
		<SCRIPT TYPE="text/javascript">
			var flagChanged = false;
		  	window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=strPrgAziendaMenu%>, <%=strPrgUnitaMenu%>);  
		    <% 
			    //Genera il Javascript che si occuperà di inserire i links nel footer
			    attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
		    %>
		</SCRIPT>
		
		<script type="text/javascript" language="Javascript">   		
	     
			function SubmitSeConfermaInvio(autoConfirm) {

				//controllo correttezza date
				var dataScadenza = document.Frm1.DATA_SCADENZA.value;
				var dataInvio = document.Frm1.DATA_INVIO.value;

				if (dataScadenza == null || dataScadenza === "") {
					alert("La data di scadenza è obbligatoria");
					if (!autoConfirm) {
						return false;
					}
				}

				annoScadenza = parseInt(dataScadenza.substr(6),10);
				meseScadenza = parseInt(dataScadenza.substr(3, 2),10);
				giornoScadenza = parseInt(dataScadenza.substr(0, 2),10);
			     
				annoInvio = parseInt(dataInvio.substr(6),10);
				meseInvio = parseInt(dataInvio.substr(3, 2),10);
				giornoInvio = parseInt(dataInvio.substr(0, 2),10);

				var dataScad = new Date(annoScadenza, meseScadenza-1, giornoScadenza);
				var dataInv = new Date(annoInvio, meseInvio-1, giornoInvio);
				var dataSys = new Date();
				var oneDayMillis = 86400000; 

				//la differenza dei 60 giorni viene calcolata sulla sysdate e non sulla dataInvio
				differenzaSys = dataScad - dataSys;		//differenza tra oggi e la data di scadenza    
				differenza    = dataScad - dataInv;     //differenza tra la data di invio e la data di scadenza
				giorni_differenzaSys = new String(differenzaSys/oneDayMillis);	//differenza tra oggi e la data di scadenza in giorni
				giorni_differenza    = new String(differenza/oneDayMillis);		//differenza tra la data di invio e la data di scadenza in giorni
				if (giorni_differenzaSys > 60){
					var thirtydaysmillis = 60*oneDayMillis;
					var dataSys60millis = dataSys.getTime() + thirtydaysmillis;
					var dataSys60 = new Date(dataSys60millis);		//la data tra 60 giorni				
					var day60 = dataSys60.getDate();				//il giorno
					var month60 = pad(dataSys60.getMonth()+1,2);	//il mese
					var year60 = dataSys60.getFullYear();  			//l'anno
					//if (!autoConfirm) alert("La data di scadenza non è valida, la richiesta di personale dura al massimo 60 giorni (da oggi al " + day60 + "/" + month60 + "/" + year60 + ").");
					alert("La data di scadenza non è valida, la richiesta di personale dura al massimo 60 giorni (da oggi al " + day60 + "/" + month60 + "/" + year60 + ").");
					if (!autoConfirm) {
						return false;
					}
				}
				if (giorni_differenza <= 0){
					//if (!autoConfirm) alert("La data di scadenza dev'essere successiva alla data di invio");
					alert("La data di scadenza dev'essere successiva alla data di invio");
					if (!autoConfirm) {
						return false;
					}
				}
				
				var datRichiesta = document.Frm1.datRichiesta.value;
				var datScadenzaRich = document.Frm1.datScadenzaRich.value;

				annoRichiesta = parseInt(datRichiesta.substr(6),10);
				meseRichiesta = parseInt(datRichiesta.substr(3, 2),10);
				giornoRichiesta = parseInt(datRichiesta.substr(0, 2),10);
			     
				annoScadenzaRich = parseInt(datScadenzaRich.substr(6),10);
				meseScadenzaRich = parseInt(datScadenzaRich.substr(3, 2),10);
				giornoScadenzaRich = parseInt(datScadenzaRich.substr(0, 2),10);

				var dataRichiesta = new Date(annoRichiesta, meseRichiesta-1, giornoRichiesta);
				var dataScadenzaRich = new Date(annoScadenzaRich, meseScadenzaRich-1, giornoScadenzaRich);
				
				if(dataInv < dataRichiesta){
					//if (!autoConfirm) alert("La data di invio a Cliclavoro deve essere compresa nel periodo di validità della richiesta di personale.");
					alert("La data di invio a Cliclavoro deve essere compresa nel periodo di validità della richiesta di personale.");
					return false;
				}
				if(dataInv > dataScadenzaRich){
					//if (!autoConfirm) alert("La data di invio a Cliclavoro deve essere compresa nel periodo di validità della richiesta di personale.");
					alert("La data di invio a Cliclavoro deve essere compresa nel periodo di validità della richiesta di personale.");
					return false;
				}
				/*
				if(dataScad > dataScadenzaRich){
					if (!autoConfirm) alert("Il periodo di validità su Cliclavoro deve essere compreso nel periodo di validità della richiesta di personale.");
					return false;
				}
				*/
				//Controllo la presenza della Sede Legale
				var existSede = document.Frm1.existSede.value;
				if (existSede == null || existSede == "" || existSede == " " || existSede == "null" || existSede == "false"){
					//if (!autoConfirm) alert("Per poter completare l'invio della vacancy è necessario inserire la sede legale del datore di lavoro.");
					alert("Per poter completare l'invio della vacancy è necessario inserire la sede legale del datore di lavoro.");
					return false;
				}

				//Controllo codica fiscale Sede Legale
				var codFiscSede = document.Frm1.codFiscSede.value;
				if (codFiscSede == null || codFiscSede == "" || codFiscSede == " " || codFiscSede == "null"){
					//if (!autoConfirm) alert("Il codice fiscale dell'azienda deve essere valorizzato.");
					alert("Il codice fiscale della sede legale dell'azienda deve essere valorizzato.");
					return false;
				}

				//Controllo ragione sociale Sede Legale
				var ragSocialeSede = document.Frm1.ragSocialeSede.value;
				if (ragSocialeSede == null || ragSocialeSede == "" || ragSocialeSede == " " || ragSocialeSede == "null"){
					//if (!autoConfirm) alert("La ragione sociale dell'azienda deve essere valorizzata.");
					alert("La ragione sociale della sede legale dell'azienda deve essere valorizzata.");
					return false;
				}
				//Controllo codAteco Sede Legale
				var codAteco = document.Frm1.codAteco.value;
				if (codAteco == null || codAteco == "" || codAteco == " " || codAteco == "null"){
					//if (!autoConfirm) alert("Per poter completare l'invio della vacancy è necessario inserire il Codice Attività della sede legale del datore di lavoro e verificare che disponga della codifica ministeriale.");
					alert("Per poter completare l'invio della vacancy è necessario inserire il Codice Attività della sede legale del datore di lavoro.");
					return false;
				}

				//Controllo comune Sede Legale
				var comuneSede = document.Frm1.comuneSede.value;
				if (comuneSede == null || comuneSede == "" || comuneSede == " " || comuneSede == "null"){
					//if (!autoConfirm) alert("Il codice comune dell'azienda deve essere valorizzato.");
					alert("Il codice comune della sede legale dell'azienda deve essere valorizzato.");
					return false;
				}

				//Controllo mail Sede Legale
				var mailSede = document.Frm1.mailSede.value;
				if (mailSede == null || mailSede == "" || mailSede == " " || mailSede == "null"){
					//if (!autoConfirm) alert("Il campo email dell'azienda deve essere valorizzato.");
					alert("Il campo email della sede legale dell'azienda deve essere valorizzato.");
					return false;
				}

				//Controllo il numero dei lavoratori richiesti
				var numLavRichiesti = document.Frm1.numLavRichiesti.value;
				if (numLavRichiesti == null || numLavRichiesti == "" || numLavRichiesti == " " || numLavRichiesti == "null"){
					//if (!autoConfirm) alert("Il numero dei lavoratori richiesti deve essere valorizzato.");
					alert("Il numero dei lavoratori richiesti deve essere valorizzato.");
					return false;
				}

				//Controllo Mansioni
				var mansioni = document.Frm1.sizeMansioni.value;
				if (mansioni != "1"){
					//if (!autoConfirm) alert("Per poter completare l'invio della vacancy è necessario inserire una mansione del Profilo n. 1 che dispone della codifica ministeriale e selezionare per l'invio a Cliclavoro.");
					alert("Per poter completare l'invio della vacancy è necessario inserire una mansione del Profilo n. 1");
					return false;
				}

				//Controllo la presenza della descrizione dell'unica mansione
				if (document.Frm1.desmansione.value == null || document.Frm1.desmansione.value.length == 0){
					//if (!autoConfirm) alert("Non esiste una descrizione della qualifica professionale offerta, ovvero dell'unica mansione del Profilo n. 1 selezionata per l'invio a Cliclavoro.");
					alert("Non esiste una descrizione della qualifica professionale offerta, ovvero dell'unica mansione del Profilo n. 1 selezionata per l'invio a Cliclavoro.");
					return false;
				}

				//Controllo Comuni
				var comuni = document.Frm1.sizeComuni.value;
				if (comuni != "1"){
					//if (!autoConfirm) alert("Per poter completare l'invio della vacancy è necessario inserire un territorio di tipo comune e selezionare per l'invio a Cliclavoro.");
					alert("Per poter completare l'invio della vacancy è necessario inserire un territorio di tipo comune e selezionare per l'invio a Cliclavoro.");
					return false;
				}
				
				//Controllo Modalità di Evasione e Stato Pubblicazione
				var codEvasione = document.Frm1.codEvasione.value;
				var statoPubblic = document.Frm1.statoPubblic.value;
				if (codEvasione !== "DFD" && codEvasione !== "DPR" && codEvasione !== "DFA" && codEvasione !== "DRA" || statoPubblic != "In pubblicazione"){
					//if (!autoConfirm) alert("Per poter completare l'invio della vacancy a Cliclavoro è necessario che lo stato della vacancy sia 'In pubblicazione' e la Modalità di evasione sia 'Pubblicazione Palese' o 'Pubblicazione Palese / Preselezione' o 'Pubblicazione Anonima' oppure 'Pubblicazione Anonima / Preselezione'.");
					alert("Per poter completare l'invio della vacancy a Cliclavoro è necessario che lo stato della vacancy sia 'In pubblicazione' e la Modalità di evasione sia 'Pubblicazione Palese' o 'Pubblicazione Palese / Preselezione' o 'Pubblicazione Anonima' oppure 'Pubblicazione Anonima / Preselezione'.");
					return false;
				}

				//Controllo data di pubblicazione
				var dataPubblicazione = document.Frm1.dataPubblicazione.value;
				if (dataPubblicazione == null || dataPubblicazione == "" || dataPubblicazione == " " || dataPubblicazione == "null"){
					//if (!autoConfirm) alert("La data di pubblicazione deve essere valorizzata.");
					alert("La data di pubblicazione deve essere valorizzata.");
					return false;
				}

				//Controllo esistenza Rapporto di Lavoro
				var codRapportoLav = document.Frm1.codRapportoLav.value;
				if (codRapportoLav == null || codRapportoLav === ""){
					//if (!autoConfirm) alert("Per poter completare l'invio della vacancy a Cliclavoro è necessario selezionare un Rapporto di Lavoro.");
					alert("Per poter completare l'invio della vacancy è necessario inserire  il rapporto di lavoro del Profilo n. 1 nella sezione Pubblicazione.");
					return false;
				}
				
				//Controllo esistenza Nulla Osta
				var flgNullaOsta = document.Frm1.flgNullaOsta.value;
				if (flgNullaOsta == null || flgNullaOsta === ""){
					alert("Per poter completare l'invio della vacancy è necessario valorizzare il campo di richiesta del Nulla Osta del profilo n. 1 nella sezione Pubblicazione.");
					return false;
				}
				
				if (autoConfirm) {
					return true;
				}

				return confirm("Sei sicuro di voler procedere all'invio della richiesta di personale su Cliclavoro?");
			}


			function pad(number, length) {
				   
			    var str = '' + number;
			    while (str.length < length) {
			        str = '0' + str;
			    }
			   
			    return str;

			}
			
			function apriAnteprimaInvio() {
				var prgRichiestaAz = document.Frm1.prgRichiestaAz.value;
				var prgUnitaAz = document.Frm1.prgUnitaAz.value;
				var codCpi = document.Frm1.codCpiHidden.value;
				var prgAlternativa = document.Frm1.prgAlternativa.value;
				var statoInvioClicLavoro = document.Frm1.statoInvioClicLavoro.value;
				var prgAzienda = document.Frm1.prgAzienda.value;
		    	var opened;
		        var f = "AdapterHTTP?PAGE=IdoInvioClicLavoroAnteprimaPage&prgRichiestaAz="+prgRichiestaAz
		        				+ "&prgUnitaAz="+prgUnitaAz+"&codCpi="+codCpi+"&prgAlternativa="+prgAlternativa
		        				+ "&statoInvioClicLavoro="+statoInvioClicLavoro+"&prgAzienda="+prgAzienda+"&DATA_SCADENZA="+document.Frm1.DATA_SCADENZA.value; 
		        var t = "_blank";
		        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=650,top=30,left=180";
		        opened = window.open(f, t, feat);
		    }

			function setProfiloRicercatoReadOnlyIfNeeded () {
				var profiloRichiestoElement = document.Frm1.PROFILORICHIESTO;
				var dataScadenzaElement = document.Frm1.DATA_SCADENZA;
				var sincronizzaButtonDisabled = <%=""+(!sincronizzaButtonEnabled)%>;
				if (!SubmitSeConfermaInvio(true) || sincronizzaButtonDisabled) {
					profiloRichiestoElement.disabled = true;
					////dataScadenzaElement.disabled = true;
				}
			}
			
		</script>
		
	</head>
	
	<body class="gestione" onload="rinfresca();setProfiloRicercatoReadOnlyIfNeeded();">
		<%
			if (infCorrentiAzienda != null) {
				infCorrentiAzienda.show(out); 	
			}
			if(prgRichiestaAz != null && !prgRichiestaAz.equals("") ) {
				Linguette l = new Linguette( user,  _funzione, _page, new BigDecimal(prgRichiestaAz));
			    l.setCodiceItem("prgRichiestaAz");    
			    l.show(out);
			}
		%> 

	<center>
      <font color="green">
        <af:showMessages prefix="M_UpdateClicLavoro" />
        <af:showMessages prefix="M_InsertClicLavoro" />        
      </font>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>

	<p align="center">						
		
		<af:form name="Frm1" action="AdapterHTTP" method="post" onSubmit="SubmitSeConfermaInvio()">
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>">
			<input type="hidden" name="TIPOLOGIA" value="1">
			<input type="hidden" name="EXEC_SEND" value="YES">
			<input type="hidden" name="PAGE" value="<%=_page%>">
			<input type="hidden" name="prgRichiestaAz" value="<%=prgRichiestaAz%>">
			<input type="hidden" name="codTipoComunicazioneCl" value="01">
			<input type="hidden" name="prgVacancy" value="<%=prgVacancy%>">
			<input type="hidden" name="statoInvioClicLavoro" value="<%=statoInvioClicLavoro%>">
			<input type="hidden" name="datRichiesta" value="<%=datRichiesta%>">
			<input type="hidden" name="datScadenzaRich" value="<%=datScadenzaRich%>">
			<input type="hidden" name="codAteco" value="<%=codAteco%>">
			<input type="hidden" name="sizeMansioni" value="<%=sizeMansioni%>">
			<input type="hidden" name="sizeComuni" value="<%=sizeComuni%>">
			<input type="hidden" name="codOfferta" value="<%=codOfferta%>">
			<input type="hidden" name="desmansione" value="<%=desMansione%>">
			<input type="hidden" name="codFiscSede" value="<%=codFiscSede%>">
			<input type="hidden" name="ragSocialeSede" value="<%=StringUtils.formatValue4Javascript(ragSocialeSede)%>">
			<input type="hidden" name="comuneSede" value="<%=comuneSede%>">
			<input type="hidden" name="mailSede" value="<%=mailSede%>">
			<input type="hidden" name="numLavRichiesti" value="<%=numLavRichiesti%>">
			<input type="hidden" name="dataPubblicazione" value="<%=dataPubblicazione%>">
			<input type="hidden" name="existSede" value="<%=existSede%>">
			<input type="hidden" name="codEvasione" value="<%=codEvasione%>">
			<input type="hidden" name="statoPubblic" value="<%=statoPubblic%>">
			<input type="hidden" name="prgUnitaAz" value="<%=prgUnitaAz%>">
			<input type="hidden" name="codCpiHidden" value="<%=codCpi%>">
			<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>">
			<input type="hidden" name="prgAlternativa" value="1">
			<input type="hidden" name="codRapportoLav" value="<%=codRapportoLav%>">
			<input type="hidden" name="flgNullaOsta" value="<%=flgNullaOsta%>">
			
			<% if (contInvioClicLavoro == null) {%>
				<input type="hidden" name="insertModule" value="INSERT">
			<% } else { %>
				<input type="hidden" name="updateModule" value="UPDATE">
			<% } %>
			
			<%
				out.print(htmlStreamTop);
			%>
			<br/><b>
			Sincronizza l'offerta di lavoro con Cliclavoro
			</b><br/><br/>
			<table class="main">
				<%--
				<tr>
					<td class="etichetta">Codice Vacancy</td>
					<td class="campo">
						<af:textBox classNameBase="input" name="CODVACANCY" value='<%= (codOfferta == null ? "" : codOfferta) %>'
							validateOnPost="true" readonly="true" size="50"/>
					</td>
				</tr>
				--%>
				<input type="hidden" name="CODVACANCY" value="<%= (codOfferta == null ? "" : codOfferta) %>"/>
				<tr>
					<td class="etichetta">Profilo Richiesto</td>
					<td class="campo">
						<af:textArea classNameBase="input" title="Profilo Richiesto" value="<%= strProfiloRichiesto %>" readonly='<%=""+(!sincronizzaButtonEnabled)%>'
							cols="50" name="PROFILORICHIESTO" maxlength="1800" required="true" />
					</td>
				</tr>
				<tr>
					<td class="etichetta">Data ultima sincronizzazione</td>
					<td class="campo"><af:textBox readonly="<%= readOnly %>"
							classNameBase="input" type="date" name="DATA_INVIO_OLD"
							value="<%= dataInvioOld %>" validateOnPost="true"
							size="11" maxlength="11" />
							<input type="hidden" name="DATA_INVIO" value="<%= data_invio %>"/>
					</td>
				</tr>		
				<%--
				<tr>
					<td class="etichetta">Stato Invio</td>
					<td class="campo">
						 <af:textBox classNameBase="input" name="DESSTATOINVIOCOMUNICAZIONE" value="<%= desStatoInvio %>" readonly="<%= readOnly %>"
							validateOnPost="true" readonly="true" size="70"/>
					</td>
				</tr>
				--%>		
				<tr>
					<td class="etichetta">Data scadenza</td>
					<% boolean newReadOnly = !BLOCCA_FLUSSO && !sincronizzaButtonEnabled; %>
					<td class="campo">		
						<af:textBox name="DATA_SCADENZA" title="Data scadenza" value="<%= data_scad %>"
             			  type="date" classNameBase="input" validateOnPost="true" readonly='<%=(""+newReadOnly)%>'
	                 	  size="11" maxlength="11"/>
					</td>
				</tr>
				<%--
				<input type="hidden" name="DATA_SCADENZA" value="<%=data_scad%>"/>
				--%>
				<%--
				<tr>
					<td class="etichetta">CPI intermediario</td>
					<td class="campo"><af:comboBox classNameBase="input"
							name="CODCPI" selectedValue="<%=codCpi%>" disabled="<%= readOnly %>"
							required="true" moduleName="M_GetCpiPoloProvinciale" />
					</td>
				</tr>
				--%>
				<input type="hidden" name="CODCPI" value="<%=codCpi%>"/>
			</table>
			<table>
				<tr>
				<% if (!BLOCCA_FLUSSO) { %>
					<% if(canAnteprima){ %>
						<td>
							<input type="button" class="pulsante" name="anteprima" value="Anteprima Invio" onclick="apriAnteprimaInvio();"/>
						</td>
					<% } %>
				<% }  %>
				<td>
				<% if (!BLOCCA_FLUSSO) { %>
					<% if (canInvio) { %>
						<% if (sincronizzaButtonEnabled){ %>
							<input type="submit" class="pulsanti" name="invio" value="Sincronizza">
						<% } else { %>
							<input type="submit" class="pulsanteDisabled" name="invio" value="Sincronizza" disabled="disabled">
						<% }  %>
					<% }  %>
				<% }  %>
				</td>
				</tr>
			</table>
		<%out.print(htmlStreamBottom);%>			
		</af:form>
	</p>	
	</body>
	</html>