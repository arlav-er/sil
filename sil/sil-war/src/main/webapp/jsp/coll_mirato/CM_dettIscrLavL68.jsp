<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.*,com.engiweb.framework.util.*,it.eng.sil.module.movimenti.*,it.eng.afExt.utils.*,it.eng.sil.security.*,it.eng.sil.util.*,it.eng.sil.bean.*,it.eng.sil.module.anag.*,it.eng.sil.*,java.util.*,java.text.*,java.math.*,java.io.*,com.engiweb.framework.security.*" %>
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
	String ConfigGraduatoria_cm = serviceResponse
			.containsAttribute("M_GetConfigGraduatoria_cm.ROWS.ROW.codmonotipogradcm") ? serviceResponse
			.getAttribute(
					"M_GetConfigGraduatoria_cm.ROWS.ROW.codmonotipogradcm")
			.toString()
			: "0";
			
	String configAnzOrdinaria_cm = serviceResponse.containsAttribute("M_CONFIG_ANZIANITA_ORDINARIA_CM.ROWS.ROW.NUM") ? 
			serviceResponse.getAttribute("M_CONFIG_ANZIANITA_ORDINARIA_CM.ROWS.ROW.NUM").toString(): "0";
	
	boolean iscrCMFuoriResidenza = false;
	SourceBean rowCanIscr = (SourceBean)serviceResponse.getAttribute("CM_CHECK_CAN_ISCR_DECRYPT");
	if (rowCanIscr != null && rowCanIscr.containsAttribute("IscrizioneFuoriResidenza")) {
		iscrCMFuoriResidenza = true;
	}
	
	String resultStatoOccup = "";	
	SourceBean StatoOccupaz = (SourceBean) serviceResponse.getAttribute("M_Get_Stato_Occup.ROWS.ROW");
	if (StatoOccupaz != null) {
		resultStatoOccup = StatoOccupaz.getAttribute(
				"RESULT_STATO_OCCUP").toString();
	}		
			
	String configuraLabelPsichica_mentale=(String)Utils.getConfigValue("LABEL").getAttribute("ROW.NUM"); 	//La Valle d'Aosta vuole l'etichetta "mentale" al posto di "psichica"
	 boolean labelMentale = false;
	 if (configuraLabelPsichica_mentale.equals("1")) //Siamo in Valle d'Aosta, enjoy skiing!
		 labelMentale = true;		
			
	SourceBean rowConfigL68 = (SourceBean) serviceResponse.getAttribute("M_ConfigStampaIscrizioneL68.ROWS.ROW");
	String configL68 = it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;
	if (rowConfigL68 != null){
		configL68 = rowConfigL68.getAttribute("num").toString();
	}		
	String cdnLavoratore = (String) serviceRequest
			.getAttribute("CDNLAVORATORE");

	String codCPI = user.getCodRif();
	String cdnLavoratoreDecrypt = EncryptDecryptUtils
			.decrypt(cdnLavoratore);

	
	String PROVINCIA_ISCR = serviceRequest
			.getAttribute("PROVINCIA_ISCR") == null ? ""
			: (String) serviceRequest
			.getAttribute("PROVINCIA_ISCR");

	String _page = (String) serviceRequest.getAttribute("PAGE");
	String cdnFunz = serviceRequest.getAttribute("CDNFUNZIONE")!=null?serviceRequest.getAttribute("CDNFUNZIONE").toString():"";
	int _cdnFunz = Integer.parseInt((String) serviceRequest
			.getAttribute("CDNFUNZIONE"));
	String selTutte = (String) serviceRequest.getAttribute("selTutte");
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratoreDecrypt));

	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canInsert = false;
	boolean canUpdate = false;
	boolean canSalvaStato = false;
	boolean canPrint = false;
	boolean canPrintProvvisorio = false;
	boolean esisteStorico = false;
	boolean canCalcPunt = false;

	String disableTipo = "false";
	String disableFineAtto = "false";

	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	
	canInsert = attributi.containsButton("INSERISCI");
	canUpdate = attributi.containsButton("AGGIORNA");
	canSalvaStato = attributi.containsButton("SALVA-STATO");
	canPrint = attributi.containsButton("STAMPA");
	canPrintProvvisorio = attributi.containsButton("STAMPA_PROV");
	canCalcPunt = attributi.containsButton("Calcolo_punteggiol68");

	String fieldReadOnly = canUpdate ? "false" : "true";
	String fieldforStoricoReadOnly = canUpdate ? "false" : "true";

	InfCorrentiLav testata = new InfCorrentiLav(RequestContainer
			.getRequestContainer().getSessionContainer(),
			cdnLavoratoreDecrypt, user);

	Linguette l = new Linguette(user, _cdnFunz, _page,
			cdnLavoratoreDecrypt, false);

	boolean nuovaIscrizione = !(serviceRequest
			.containsAttribute("prgCMIscr"))
			|| serviceRequest.getAttribute("prgCMIscr") == null;

	if (nuovaIscrizione) {
		if (!canInsert) {
			canSalvaStato = canInsert;
		} else if (!canSalvaStato) {
			canInsert = canSalvaStato;
		}
	}

	String docInOut = "";
	String docRif = "";
	String docTipo = "";
	String docCodRif = "";
	String docCodTipo = "";
	BigDecimal numProtV = null;
	BigDecimal numAnnoProtV = null;
	String dataOraProt = "";
	boolean noButton = false;
	String datProtV = "";
	String oraProtV = "";
	Vector rowsProt = null;
	SourceBean rowProt = null;
	String CODSTATOATTO = "NP";
	String DatAcqRil = "";
	String DatInizio = "";

	String message = "";
	String prgCmIscr = "";
	String numIscrizione = "";
	
	String codCmTipoIscr = "";
	String dataInizio = "";
	String dataFine = "";
	String codTipoInvalidita = "";
	String numPercInvalidita = "";
	String codAccertSanitario = "";
	String datAccertSanitario = "";
	String strNote = "";
	String prgSpiMod = "";
	String cdnUtIns = "";
	String dtmIns = "";
	String cdnUtMod = "";
	String dtmMod = "";
	String numKloCmIscr = "";
	String datAnzianita68 = "";
	String datAnzOrdinariaPregressa = "";
	String datUltimaIscr = "";
	String codMotivoFineAtto = "";
	String prgVerbaleAcc = "";
	String flgAutomImpatti = "";
	String codMonoTipoRagg = "";
	String codCmAnnota = "";

	SourceBean dettIscr = (SourceBean) serviceResponse
			.getAttribute("CM_GET_DETT_ISCR.ROWS.ROW");
	if (dettIscr != null) {
		message = "UPDATE";
		prgCmIscr = dettIscr.getAttribute("PRGCMISCR") == null ? ""
				: ((BigDecimal) dettIscr.getAttribute("PRGCMISCR"))
						.toString();
		numIscrizione = dettIscr.getAttribute("NUMISCRIZIONE") == null ? ""
				: ((BigDecimal) dettIscr.getAttribute("NUMISCRIZIONE"))
						.toString();
		
		codCmTipoIscr = dettIscr.getAttribute("CODCMTIPOISCR") == null ? ""
				: (String) dettIscr.getAttribute("CODCMTIPOISCR");
		dataInizio = dettIscr.getAttribute("DATINIZIO") == null ? ""
				: (String) dettIscr.getAttribute("DATINIZIO");
		dataFine = dettIscr.getAttribute("DATFINE") == null ? ""
				: (String) dettIscr.getAttribute("DATFINE");
		codTipoInvalidita = dettIscr.getAttribute("CODTIPOINVALIDITA") == null ? ""
				: (String) dettIscr.getAttribute("CODTIPOINVALIDITA");
		numPercInvalidita = dettIscr.getAttribute("NUMPERCINVALIDITA") == null ? ""
				: ((BigDecimal) dettIscr
						.getAttribute("NUMPERCINVALIDITA")).toString();
		codAccertSanitario = dettIscr
				.getAttribute("CODACCERTSANITARIO") == null ? ""
				: (String) dettIscr.getAttribute("CODACCERTSANITARIO");
		datAccertSanitario = dettIscr
				.getAttribute("DATACCERTSANITARIO") == null ? ""
				: (String) dettIscr.getAttribute("DATACCERTSANITARIO");
		strNote = dettIscr.getAttribute("STRNOTE") == null ? ""
				: (String) dettIscr.getAttribute("STRNOTE");
		prgSpiMod = dettIscr.getAttribute("PRGSPIMOD") == null ? ""
				: ((BigDecimal) dettIscr.getAttribute("PRGSPIMOD"))
						.toString();
		cdnUtIns = dettIscr.getAttribute("CDNUTINS") == null ? ""
				: ((BigDecimal) dettIscr.getAttribute("CDNUTINS"))
						.toString();
		dtmIns = dettIscr.getAttribute("DTMINS") == null ? ""
				: (String) dettIscr.getAttribute("DTMINS");
		cdnUtMod = dettIscr.getAttribute("CDNUTMOD") == null ? ""
				: ((BigDecimal) dettIscr.getAttribute("CDNUTMOD"))
						.toString();
		dtmMod = dettIscr.getAttribute("DTMMOD") == null ? ""
				: (String) dettIscr.getAttribute("DTMMOD");
		numKloCmIscr = dettIscr.getAttribute("NUMKLOCMISCR") == null ? ""
				: ((BigDecimal) dettIscr.getAttribute("NUMKLOCMISCR"))
						.toString();
		datAnzianita68 = dettIscr.getAttribute("DATANZIANITA68") == null ? ""
				: (String) dettIscr.getAttribute("DATANZIANITA68");
		datAnzOrdinariaPregressa = dettIscr.getAttribute("DATANZIANITAORDINARIO") == null ? ""
				: (String) dettIscr.getAttribute("DATANZIANITAORDINARIO");
		datUltimaIscr = dettIscr.getAttribute("DATULTIMAISCR") == null ? ""
				: (String) dettIscr.getAttribute("DATULTIMAISCR");
		codMotivoFineAtto = dettIscr.getAttribute("CODMOTIVOFINEATTO") == null ? ""
				: (String) dettIscr.getAttribute("CODMOTIVOFINEATTO");
		prgVerbaleAcc = dettIscr.getAttribute("PRGVERBALEACC") == null ? ""
				: ((BigDecimal) dettIscr.getAttribute("PRGVERBALEACC"))
						.toString();
		flgAutomImpatti = dettIscr.getAttribute("FLGAUTOMATICO") == null ? ""
				: (String) dettIscr.getAttribute("FLGAUTOMATICO");
		codMonoTipoRagg = dettIscr.getAttribute("codMonoTipoRagg") == null ? ""
				: (String) dettIscr.getAttribute("codMonoTipoRagg");
		codCmAnnota = dettIscr.getAttribute("CODCMANNOTA") == null ? ""
				: (String) dettIscr.getAttribute("CODCMANNOTA");
		// recupero informazioni inerenti il documento associato all'iscrizione
		numProtV = SourceBeanUtils.getAttrBigDecimal(dettIscr,
				"NUMPROTOCOLLO", null);
		dataOraProt = SourceBeanUtils.getAttrStrNotNull(dettIscr,
				"DATPROTOCOLLO");
		if (!dataOraProt.equals("")) {
			oraProtV = dataOraProt.substring(11, 16);
			datProtV = dataOraProt.substring(0, 10);
		}
		numAnnoProtV = SourceBeanUtils.getAttrBigDecimal(dettIscr,
				"NUMANNOPROT", null);
		CODSTATOATTO = dettIscr.getAttribute("CODSTATOATTO") == null ? ""
				: (String) dettIscr.getAttribute("CODSTATOATTO");
		docInOut = StringUtils.getAttributeStrNotNull(dettIscr,
				"striodoc");
		docRif = StringUtils.getAttributeStrNotNull(dettIscr,
				"strambitodoc");
		docTipo = StringUtils.getAttributeStrNotNull(dettIscr,
				"strtipodoc");
		docCodRif = StringUtils.getAttributeStrNotNull(dettIscr,
				"codambitodoc");
		docCodTipo = StringUtils.getAttributeStrNotNull(dettIscr,
				"codtipodoc");
		PROVINCIA_ISCR = StringUtils.getAttributeStrNotNull(dettIscr,
				"PROVINCIA_ISCR");
	} else {
		message = "INSERT";
		SourceBean operatore = (SourceBean) serviceResponse
				.getAttribute("M_Select_PrgSPI_Da_CDNUT.ROWS.ROW");
		if ((operatore != null)
				&& (operatore.getAttribute("PRGSPI") != null)) {
			if (!operatore.getAttribute("PRGSPI").equals("")) {
				prgSpiMod = operatore.getAttribute("PRGSPI").toString();
			}
		}
		SourceBean rowInfoDeDoc = (SourceBean) serviceResponse
				.getAttribute("CM_GetInfoDeDoc_IscrL68.ROWS.ROW");
		if (rowInfoDeDoc != null) {
			docInOut = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,
					"striodoc");
			docRif = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,
					"strambitodoc");
			docTipo = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,
					"strtipodoc");
			docCodRif = StringUtils.getAttributeStrNotNull(
					rowInfoDeDoc, "codambitodoc");
			docCodTipo = StringUtils.getAttributeStrNotNull(
					rowInfoDeDoc, "codtipodoc");
		}
	}

	Testata operatoreInfo = null;
	String dataOdierna = "";

	String img0 = "../../img/chiuso.gif";
	String img1 = "../../img/aperto.gif";
	String hasDataFine = "false";

	Calendar oggi = Calendar.getInstance();
	String giornoDB = Integer.toString(oggi.get(5));
	if (giornoDB.length() == 1) {
		giornoDB = '0' + giornoDB;
	}
	String meseDB = Integer.toString(oggi.get(2) + 1);
	if (meseDB.length() == 1) {
		meseDB = '0' + meseDB;
	}
	String annoDB = Integer.toString(oggi.get(1));
	dataOdierna = giornoDB + "/" + meseDB + "/" + annoDB;

	String datAnzianita68TipoD = "";
	String datAnzianita68TipoA = "";

	if (nuovaIscrizione) {
		datUltimaIscr = dataOdierna;
		dataInizio = dataOdierna;
		SourceBean datAnzianitaSbTipoD = (SourceBean) serviceResponse
				.getAttribute("CM_GET_DATANZIANITA68_ISCR_PREC_D.ROWS.ROW");
		if (datAnzianitaSbTipoD != null) {
			datAnzianita68TipoD = datAnzianitaSbTipoD
					.getAttribute("DATANZIANITA68") == null ? ""
					: (String) datAnzianitaSbTipoD
							.getAttribute("DATANZIANITA68");
		}
		SourceBean datAnzianitaSbTipoA = (SourceBean) serviceResponse
				.getAttribute("CM_GET_DATANZIANITA68_ISCR_PREC_A.ROWS.ROW");
		if (datAnzianitaSbTipoA != null) {
			datAnzianita68TipoA = datAnzianitaSbTipoA
					.getAttribute("DATANZIANITA68") == null ? ""
					: (String) datAnzianitaSbTipoA
							.getAttribute("DATANZIANITA68");
		}
	}

	if (!nuovaIscrizione) {
		operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
		disableTipo = "true";

		if (datUltimaIscr.equals("")) {
			datUltimaIscr = dataInizio;
		}

		if (CODSTATOATTO.equals("PR") || CODSTATOATTO.equals("AN")) {
			fieldReadOnly = "true";
			fieldforStoricoReadOnly = "true";
			if (CODSTATOATTO.equals("PR") && !dataFine.equals("")
					&& !codMotivoFineAtto.equals("")) {
				disableFineAtto = "true";
			}
			if (CODSTATOATTO.equals("PR") && dataFine.equals("")
					&& codMotivoFineAtto.equals("")) {
				fieldforStoricoReadOnly = "false";
			}
			if (CODSTATOATTO.equals("AN")) {
				noButton = true;
				disableFineAtto = "true";
			}
		}

		//Controllo l'esistenza dello storico modifiche
		esisteStorico = serviceResponse
				.containsAttribute("CM_Storico_Mod_IscrL68.ROWS.ROW");
	}

	String paramCmIsc1 = null;
	SourceBean paramSb = (SourceBean) serviceResponse
			.getAttribute("M_GetParamCmIsc1.ROWS.ROW");
	if (paramSb != null) {
		paramCmIsc1 = paramSb.getAttribute("PARAM_CM_ISC_1").toString();
	}

	String datAnzianitaDisoc = null;
	SourceBean paramSbDisocc = (SourceBean) serviceResponse
			.getAttribute("M_Get_Data_Anz_Disocc.ROWS.ROW");
	if (paramSbDisocc != null) {
		datAnzianitaDisoc = paramSbDisocc.getAttribute(
				"DATANZIANITADISOC").toString();
	}

	String cpiCompLav = "";
	SourceBean cpiCompSb = (SourceBean) serviceResponse
			.getAttribute("CM_GET_CPI_COMP.ROWS.ROW");
	if (cpiCompSb != null) {
		cpiCompLav = (String) cpiCompSb.getAttribute("DESCRIZIONE");
	}

	String urlDiLista = "";
	if (selTutte != null) {
		urlDiLista = "PAGE=CMIscrizioniLavoratorePage"
				+ "&CDNFUNZIONE=" + _cdnFunz + "&CDNLAVORATORE="
				+ cdnLavoratoreDecrypt + "&selTutte=" + selTutte;
	} else {
		urlDiLista = (String) sessionContainer
				.getAttribute("_TOKEN_COLLMIRATORISULTRICERCAPAGE");
		selTutte = "";
	}

	String htmlStreamTop = StyleUtils.roundTopTable(canInsert
			&& canUpdate && disableFineAtto.equals("false"));
	String htmlStreamBottom = StyleUtils.roundBottomTable(canInsert
			&& canUpdate && disableFineAtto.equals("false"));
	
	
	//APRI_EV="1" CDNFUNZIONE="195" CDNLAVORATORE="831682BC1A4583ED" CODSTATOATTO="PR" MODULE="CM_GET_DETT_ISCR" PAGE="CMIscrizioniLavoratorePage" PROVINCIA_ISCR="PERUGIA"
    //prgCMIscr="35075"
	String queryStringBack = "page=" + _page + "&CDNLAVORATORE=" + cdnLavoratore + "&cdnFunzione=" + cdnFunz + "&MODULE=CM_GET_DETT_ISCR";
	queryStringBack = queryStringBack +  "&PROVINCIA_ISCR=" + PROVINCIA_ISCR + "&CODSTATOATTO="+ CODSTATOATTO + "&prgCMIscr=" + prgCmIscr;

/**
	Parte aggiunta per la visualizzazione dell'ultima diagnosi funzionale
	Pablo P. 29-03-11
	INIZIO
*/
//boolean showDiagnosiFunz = false;
List sezioni = attributi.getSectionList();
boolean canViewDiagnosi = sezioni.contains("DIAGNOSI_FUNZ");
//boolean canViewDiagnosi = attributi.containsButton("DIAGNOSI_FUNZ"); //da profilare

String prgUltimaIscrLavTipo = "0";
if (serviceResponse.containsAttribute("M_Get_Prg_Ultima_Iscrizione_Lav_tipo.rows.row.prgUltimaIscrLavTipo"))
	prgUltimaIscrLavTipo = ((BigDecimal)serviceResponse.getAttribute("M_Get_Prg_Ultima_Iscrizione_Lav_tipo.rows.row.prgUltimaIscrLavTipo")).toString();

SourceBean diagnosiFunz = null;
Vector diagnosiFunzVect = null;
String data_inizio_diagnosi=null;
String data_fine_diagnosi=null;
String data_revisione_diagnosi=null;
boolean invalidPsichica = false;
boolean invalidFisica=false;
boolean invalidSensoriale=false;
boolean invalidIntellettiva=false;

if (prgUltimaIscrLavTipo.equals(prgCmIscr) && serviceResponse.containsAttribute("M_Get_Last_Diagnosi_Funz.ROWS.ROW")){
	//showDiagnosiFunz = canViewDiagnosi;
	
	diagnosiFunzVect = serviceResponse.getAttributeAsVector("M_Get_Last_Diagnosi_Funz.ROWS.ROW");
	if (diagnosiFunzVect.size() == 1) { 				
		diagnosiFunz = (SourceBean)diagnosiFunzVect.get(0);
		data_inizio_diagnosi = StringUtils.getAttributeStrNotNull(diagnosiFunz,"data_inizio");
		data_revisione_diagnosi = StringUtils.getAttributeStrNotNull(diagnosiFunz,"data_revisione");
		data_fine_diagnosi = StringUtils.getAttributeStrNotNull(diagnosiFunz,"data_fine");
		invalidPsichica = StringUtils.getAttributeStrNotNull(diagnosiFunz,"FLGINVALIDPSICHICA").equalsIgnoreCase("S");
		invalidFisica = StringUtils.getAttributeStrNotNull(diagnosiFunz,"FLGINVALIDFISICA").equalsIgnoreCase("S");
		invalidSensoriale = StringUtils.getAttributeStrNotNull(diagnosiFunz,"FLGINVALIDSENSORIALE").equalsIgnoreCase("S");
		invalidIntellettiva = StringUtils.getAttributeStrNotNull(diagnosiFunz,"FLGINVALIDINTELLETTIVA").equalsIgnoreCase("S");
	}
}
// FINE
%>

<html>
  <head>
  	<title> ISCRIZIONE COLLOCAMENTO MIRATO LEGGE 68/99</title> 
  	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
	<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
	
	
	
	<%
			String queryString = null;
		%>
	<%@ include file="../documenti/_apriGestioneDoc.inc"%>

	<af:linkScript path="../../js/"/>
	
   <script language="Javascript">
   
    var imgChiusa = "../../img/chiuso.gif";
	var imgAperta = "../../img/aperto.gif";
	var sezioni = new Array();
	var nuovaIscrizioneL68 = <%=nuovaIscrizione%>;
	var iscrCMFuoriResidenza = <%=iscrCMFuoriResidenza%>;
	
	function Sezione(sezione, img,aperta){    
    	this.sezione=sezione;
    	this.sezione.aperta=aperta;
    	this.img=img;
	}
	
	function cambia(immagine, sezione) {
		if (sezione.style.display == 'inline') {
			sezione.style.display = 'none';
			sezione.aperta = false;
			immagine.src = imgChiusa;
			immagine.alt = 'Apri';
		}
		else if (sezione.style.display == "none") {
			sezione.style.display = "inline";
			sezione.aperta = true;
			immagine.src = imgAperta;
    		immagine.alt = "Chiudi";
		}
	}
	
	function initSezioni(sezione){
		sezioni.push(sezione);
	}
	
	function ControllaDate(){
    	var dataOdierna = '<%=dataOdierna%>';
    	
	  	if (compareDate(document.Frm1.DATULTIMAISCR.value,dataOdierna) > 0) {
  			alert("La " + document.Frm1.DATULTIMAISCR.title + " deve essere minore o uguale della data odierna ");
    		return false;
	  	}
		
	  	<%if (!nuovaIscrizione && !CODSTATOATTO.equals("NP")) {%>
	  		
			if(document.Frm1.DATDATAFINE.value !=""){
			  	if (compareDate(document.Frm1.DATDATAFINE.value,dataOdierna) > 0) {
		  			alert("La " + document.Frm1.DATDATAFINE.title + " deve essere minore o uguale della data odierna ");
		    		return false;
			  	}  		
	  		
	  			if(document.Frm1.DATULTIMAISCR.value !=""){
		  			if (compareDate(document.Frm1.DATULTIMAISCR.value,document.Frm1.DATDATAFINE.value) > 0) {
	      				alert("La " + document.Frm1.DATDATAFINE.title + " deve essere maggiore della " + document.Frm1.DATULTIMAISCR.title);
		    			return false;
		  			}
		  		}	
			}
		  	if(document.Frm1.DATDATAFINE.value != "" && document.Frm1.CODMOTIVOFINEATTO.value == ""){
		  		alert("Il campo " + document.Frm1.CODMOTIVOFINEATTO.title + " è obbligatorio");
	      		return false;
	      	}
		  	if(document.Frm1.DATDATAFINE.value == "" && document.Frm1.CODMOTIVOFINEATTO.value != ""){
		  		alert("Il campo " + document.Frm1.DATDATAFINE.title + " è obbligatorio");
	      		return false;
	      	}

	  	<%}%>

	  	if (nuovaIscrizioneL68 && document.Frm1.CODSTATOATTO.value == "PR" && iscrCMFuoriResidenza) {
			if (!confirm("Attenzione: il lavoratore iscritto al collocamento mirato non risulta residente nel CPI di riferimento.")) {
				return false;
			}	
	  	}

		//DONA 08/04/2010 CONTROLLO NON BLOCCANTE PER TRENTO data di anzianità < data anzianità di disoccupazione
		
		<%if (paramCmIsc1 != null && ("1").equals(paramCmIsc1)) {%>
			if (compareDate(document.Frm1.DATULTIMAISCR.value, document.Frm1.DATANZIANITADISOCC.value) < 0) {
	  			return confirm("La data d'iscrizione al CM è antecedente rispetto all'anzianità d'iscrizione all'ordinario. \r\nQuesto comporterà un'impossibilità di calcolo del punteggio sull'anzianità e quindi del punteggio per le graduatorie.");	    		
		  	}
		<%}%>
		
		document.Frm1.DATDATAINIZIO.value = document.Frm1.DATULTIMAISCR.value;

	  	return true;
	}
		
	var flagChanged = false;
        
    function fieldChanged() {
    	<%if (!canUpdate) {%> 
         	flagChanged = true;
    	<%}%> 
   	}

    function controllaCampi() {
		if ((_arrFunz != undefined) && (_arrFunz != null)) {
			for (var i=0; i<_arrFunz.length; i++) {
				var ok = eval(_arrFunz[i]);
				if (!ok) {
					return false;
				}
			}
		}
		return true;
	}
	
	function valorizzacodCMTipoIscr() {
		var d = document.getElementById('CategoriaD');
		var a = document.getElementById('CategoriaA');

		if (a.style.display == 'inline') {
			document.Frm1.CODCMTIPOISCR.value = document.Frm1.codCMTipoIscrA.value;
		} 
		else if (d.style.display == 'inline') {
			document.Frm1.CODCMTIPOISCR.value = document.Frm1.codCMTipoIscrD.value;
		} 
		if (document.Frm1.CODCMTIPOISCR.value == '') {
			alert("Il campo " + document.Frm1.CODCMTIPOISCR.title + " è obbligatorio");
			return false;
		}
		return true;
	}


	
    function inizializzaCategoria(codMonoTipoRagg) {
		var d = document.getElementById('CategoriaD');
		var a = document.getElementById('CategoriaA');
		var v = document.getElementById('CategoriaVuota');
		if (codMonoTipoRagg == '') {
			v.style.display = 'inline';
			a.style.display = 'none';
			d.style.display = 'none';
		} 
		else if (codMonoTipoRagg == 'A') {
			a.style.display = 'inline';
		}
		else if (codMonoTipoRagg == 'D') {
			d.style.display = 'inline';
		}
    }

	function visualizzaCategoria() {
		var d = document.getElementById('CategoriaD');
		var a = document.getElementById('CategoriaA');
		var v = document.getElementById('CategoriaVuota');
		if (document.Frm1.codMonoTipoRagg.value == 'D') {
			d.style.display = 'inline';
			a.style.display = 'none';
			v.style.display = 'none';
			document.Frm1.DATANZIANITA68.value = '<%=datAnzianita68TipoD%>';
		} 
		else if (document.Frm1.codMonoTipoRagg.value == 'A') {
			d.style.display = 'none';
			a.style.display = 'inline';
			v.style.display = 'none';	
			document.Frm1.DATANZIANITA68.value = '<%=datAnzianita68TipoA%>';
		} 
		else if (document.Frm1.codMonoTipoRagg.value == '') { 			
			//alert("ehi");
			d.style.display = 'none';
			a.style.display = 'none';
			v.style.display = 'inline';		
			document.Frm1.DATANZIANITA68.value = '<%=datAnzianita68%>';
		}
	}
	
	function checkPercentualeInvalidita() {
		if (document.Frm1.codMonoTipoRagg.value == 'A') {
			if (document.Frm1.NUMPERCINVALIDITA.value != '') {
				alert('La percentuale di invalidità non è valorizzabile nel caso di Altre categorie protette.');
				return false;
			}
			return true;
		} 
		else if (document.Frm1.codMonoTipoRagg.value == 'D') {
			if (document.Frm1.CODCMTIPOISCR.value == '01' || document.Frm1.CODCMTIPOISCR.value == '02' || document.Frm1.CODCMTIPOISCR.value == '03') {
				if (parseInt(document.Frm1.NUMPERCINVALIDITA.value) < 1 || parseInt(document.Frm1.NUMPERCINVALIDITA.value) > 8) {
					alert('La percentuale di invalidità in tali casi può assumere valore da 1 a 8.');
					return false;
				}
				return true;
			}	
			if (document.Frm1.CODCMTIPOISCR.value == '04') {
				if (parseInt(document.Frm1.NUMPERCINVALIDITA.value) < 34 || parseInt(document.Frm1.NUMPERCINVALIDITA.value) > 100) {
					alert('La percentuale di invalidità in tali casi può assumere valore da 34 a 100.');
					return false;
				}
				return true;
			}
			if (document.Frm1.CODCMTIPOISCR.value == '08' || document.Frm1.CODCMTIPOISCR.value == '09' || document.Frm1.CODCMTIPOISCR.value == '11' || document.Frm1.CODCMTIPOISCR.value == '12' || document.Frm1.CODCMTIPOISCR.value == '13') {
				if (parseInt(document.Frm1.NUMPERCINVALIDITA.value) < 46 || parseInt(document.Frm1.NUMPERCINVALIDITA.value) > 100) {
					alert('La percentuale di invalidità in tali casi può assumere valore da 46 a 100.');
					return false;
				}
				return true;
			}			
		} 
	}
	
	function calcolaMesiAnzianita() {
		varNameData = "";
		varValueData = "";
		varNameDataPregressaOrd = "";
		varValueDataPregressaOrd = "";
		if (document.Frm1.DATANZIANITA68.value == "") { 
			if (document.Frm1.DATULTIMAISCR.value == "") { 
				alert("Per il calcolo del numero dei mesi è indispensabile la Data di anzianità o d'iscrizione!");
			} else {
				varNameData = 'DATULTIMAISCR';
				varValueData = document.Frm1.DATULTIMAISCR;	
			}
		} else {
			varNameData = 'DATANZIANITA68';
			varValueData = document.Frm1.DATANZIANITA68;
		}
		if (varNameData != "") {
			if (validateDate(varNameData)) {
				var f = "AdapterHTTP?PAGE=CMMesiAnzianitaPage&CDNFUNZIONE=" + <%=_cdnFunz%> + 
						"&CDNLAVORATORE=" + <%=cdnLavoratoreDecrypt%> + 
						"&DATANZIANITA68=" + varValueData.value;
				varNameDataPregressaOrd = 'DATANZORDINARIAPREGRESSA';
				if (validateDate(varNameDataPregressaOrd)) {
					varValueDataPregressaOrd = document.Frm1.DATANZORDINARIAPREGRESSA;
					f += "&DATANZORDINARIAPREGRESSA=" + varValueDataPregressaOrd.value;
				}
	 			var t = "_blank";
	    		var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no," + 
	    		"resizable=yes,width=400,height=200,top=350,left=400";
	    		window.open(f, t, feat);		
			}
		}	
	}
	
	function StampaCollMirato(){
		/* Questa non dovrebbe piu servire */
		<%if (configL68.equals(it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG)){ %>
			apriGestioneDoc('RPT_STAMPA_COLLMIRATO', '&cdnLavoratore=<%=cdnLavoratoreDecrypt%>&cdnLavoratoreEncrypt=<%=cdnLavoratore%>&prgCmIscr=<%=prgCmIscr%>','SI68')
		<%}	%>
	}
		function StampaCollMirato_temp(){
			<%if (configL68.equals(it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG)){%>	
				apriGestioneDoc('RPT_STAMPA_COLLMIRATO_PROV', '&cdnLavoratore=<%=cdnLavoratoreDecrypt%>&cdnLavoratoreEncrypt=<%=cdnLavoratore%>&def=0&prgCmIscr=<%=prgCmIscr%>','SI68')
			<%} else {%>
				//apriGestioneProtocollazioneParam(parametri, tipoDoc, pageDaChiamare)
				apriGestioneProtocollazioneParam('&cdnLavoratore=<%=cdnLavoratoreDecrypt%>&cdnLavoratoreEncrypt=<%=cdnLavoratore%>&def=0&prgCmIscr=<%=prgCmIscr%>','SI68', 'CMIscrizioniLavoratorePage');
			<%}	%>
	}
		function StampaCollMirato_def(){
			<%if (configL68.equals(it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG)){%>		
				apriGestioneDoc('RPT_STAMPA_COLLMIRATO_PROV', '&cdnLavoratore=<%=cdnLavoratoreDecrypt%>&cdnLavoratoreEncrypt=<%=cdnLavoratore%>&def=1&prgCmIscr=<%=prgCmIscr%>','SI68')
			<%} else {%>
				//apriGestioneProtocollazioneParam(parametri, tipoDoc, pageDaChiamare)
				apriGestioneProtocollazioneParam('&cdnLavoratore=<%=cdnLavoratoreDecrypt%>&cdnLavoratoreEncrypt=<%=cdnLavoratore%>&def=1&prgCmIscr=<%=prgCmIscr%>','SI68', 'CMIscrizioniLavoratorePage');
			<%}	%>
	}
	
	function apriStoricoMod(contesto) {
		var url = "AdapterHTTP?PAGE=CMStoricoModIscrL68Page";		
		url += "&CONTEXT=" + contesto;
		url += "&PRGCMISCR=<%=prgCmIscr%>";
		url += "&CDNFUNZIONE=<%=_cdnFunz%>";
	
		window.open(url, "", 'toolbar=NO,statusbar=YES,width=950,height=500,top=100,left=35,scrollbars=YES,resizable=YES');
	}



	function controllo_data(stringa){
		
		var espressione = /^[0-9]{2}\/[0-9]{2}\/[0-9]{4}$/;
		if (!espressione.test(stringa))
		{
		    return false;
		}else{
			anno = parseInt(stringa.substr(6),10);
			mese = parseInt(stringa.substr(3, 2),10);
			giorno = parseInt(stringa.substr(0, 2),10);			
			
			var data=new Date(anno, mese-1, giorno);
			
			if(data.getFullYear()==anno && data.getMonth()+1==mese && data.getDate()==giorno){				
				return true;
			}else{				
				return false;
			}
		}
	}


	function controllo_anno(stringa){		
		var espressione = /^[0-9]{4}$/;
		if (!espressione.test(stringa))	{			
		    return false;
		}else{
			return true;
			
		}
	}
	
	function CalcolaPunteggioPres() {
		
		var risultControl = 1;

		if(document.Frm1.Data_Riferimento.value != "" ){
			
			if(!controllo_data(document.Frm1.Data_Riferimento.value)){
				alert("Data di riferimento non valida.");
				return false;	
			}						
		}
		
		if(document.Frm1.anno_rif.value != "" ){
			if(!controllo_anno(document.Frm1.anno_rif.value)){
				alert("Anno non valido.");
				return false;	
			}						
		}
					
		risultControl = ControlPunteggioPresunto();		

		switch(risultControl)
		{
		case 1:
		    url =  "AdapterHTTP?PAGE=P_CMIscrizioniLavCalcolaPunteggioPresunto";		
		  	url += "&MODULE=M_CMIscrizioniLavCalcolaPunteggioPresunto";
			url += "&cdnLavoratore=<%=cdnLavoratoreDecrypt%>";
			url += "&DATDATAINIZIO=" + document.Frm1.DATDATAINIZIO.value ;
			url += "&DATULTIMAISCR=" + document.Frm1.DATULTIMAISCR.value;
			url += "&NUMPERCINVALIDITA=" + document.Frm1.NUMPERCINVALIDITA.value;
			url += "&Data_Riferimento=" + document.Frm1.Data_Riferimento.value;		
			url += "&anno_rif=" + document.Frm1.anno_rif.value;
			url += "&POPUP=true";
			url += "&tipoincrocio=<%=codMonoTipoRagg%>";
			url += "&ConfigGradCM=<%=ConfigGraduatoria_cm%>";	
			url += "&prgCmIscr=<%=prgCmIscr%>";	

			
			window.open(url,'PunteggioPresunto' , 'menubar=NO,location=NO,statusbar=YES,width=470,height=310,scrollbars=YES,resizable=YES');
			break;		  
		case -1:
			alert("Non è possibile calcolare il punteggio con Data Precedente all'ultima iscrizione/reiscrizione del Lavoratore al Collocamento Mirato: l'ultima data d'iscrizione risulta "+document.Frm1.DATULTIMAISCR.value+".");
			break;
		case -2:
			alert("Non è possibile calcolare il punteggio del lavoratore poiché alla data di riferimento il lavoratore non risulta iscritto al Collocamento mirato.");
			break;
		case -3:
			alert("Non è possibile calcolare il punteggio del lavoratore poiché alla data di riferimento il lavoratore non risulta disoccupato.");
			break;
		case -4:
			alert("Non è possibile calcolare il punteggio del lavoratore. Questa iscrizione è chiusa.");
			break;

					
		}		
	}

	
	function ControlPunteggioPresunto(){
				
		
		var dataOdierna = '<%=dataOdierna%>';
		//Controllo delle date inserite, caso siano vuote prendo come riferimento:
		//Data_Riferimento = la data odierna
		//anno_rif         = l'anno precedente a quello corrente
		if(document.Frm1.Data_Riferimento.value==null || document.Frm1.Data_Riferimento.value==""){			
			document.Frm1.Data_Riferimento.value = dataOdierna;
		}
		if(document.Frm1.anno_rif.value == null || document.Frm1.anno_rif.value==""){			
			var data = new Date();	
			var year = data.getFullYear();				
			document.Frm1.anno_rif.value = year-1;			
		}


		//var
	
		var DatUltimaIscr = document.Frm1.DATULTIMAISCR.value;
		var AnnoRif = document.Frm1.anno_rif.value;
		var DatRiferimento = document.Frm1.Data_Riferimento.value;
		var DatInizio = document.Frm1.DATDATAINIZIO.value;
		var DataFine = '<%=dataFine%>'; 

		

		//Controllo -1 : Data Precedente all'ultima iscrizione/reiscrizione   	
	  	if (compareDate(DatUltimaIscr,DatRiferimento) > 0) {	  		
    		return -1;    	 
	  	}

	  	if(DataFine!=""){			
			return -4;	    	    	
		}


		
	  	
	  	//Controllo -2 : N iscrito al CM     		  	
					
		if(compareDate(DatRiferimento,DatInizio) < 0)  {		   
				//alert(DatInizio+DatRiferimento+DataFine); 
		  		return -2;
	    }	    	
		
			
		//Controllo -3 : Il lavoratore non risulta disoccupato (result diverso 0 )       
	    <% if( ("0".equals(resultStatoOccup)))  { %>			
	    	return -3;
		<% } else { %>		
		   	return 1;	  	  
		<%}%>
	    

		

   }

	
	function submitAndInsStoricoMod() {
		if ( controllaCampi() && checkPercentualeInvalidita() && ControllaDate() ) {
			
			if ((document.Frm1.DATANZIANITA68OLD.value != document.Frm1.DATANZIANITA68.value) || 
				(document.Frm1.CODTIPOINVALIDITAOLD.value != document.Frm1.CODTIPOINVALIDITA.value) || 
				(document.Frm1.NUMPERCINVALIDITAOLD.value != document.Frm1.NUMPERCINVALIDITA.value) || 
				(document.Frm1.DATACCERTSANITARIOOLD.value != document.Frm1.DATACCERTSANITARIO.value) || 
				(document.Frm1.CODACCERTSANITARIOOLD.value != document.Frm1.CODACCERTSANITARIO.value) || 
				(document.Frm1.PRGVERBALEACCOLD.value != document.Frm1.PRGVERBALEACC.value) ||
				(document.Frm1.PRGSPIMODOLD.value != document.Frm1.PRGSPIMOD.value) || 
				(document.Frm1.STRNOTEOLD.value != document.Frm1.strNote.value) ||
				(document.Frm1.CODCMANNOTAOLD.value != document.Frm1.CODCMANNOTA.value)) {
				
				apriStoricoMod('DETTAGLIO');
								
			} else {
				document.Frm1.submit(); 
			}
		}
	}



	
	function tornaAllaLista() {
		var f;
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		setWindowLocation("AdapterHTTP?<%=StringUtils.formatValue4Javascript(urlDiLista)%>");							
    }	
	
</script>

<script language="Javascript">
<%attributi.showHyperLinks(out, requestContainer, responseContainer,
					"cdnLavoratore=" + cdnLavoratoreDecrypt);%>
</script>
</head>
<body class="gestione" onload="rinfresca();inizializzaCategoria('<%=codMonoTipoRagg%>')">
<script language="Javascript">
     window.top.menu.caricaMenuLav( <%=_cdnFunz%> ,  <%=cdnLavoratoreDecrypt%>);
</script>
<%
	testata.show(out);
	l.show(out);
%>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="valorizzacodCMTipoIscr() && ControllaDate() && checkPercentualeInvalidita()">

<input type="hidden" name="PAGE" value="CMIscrizioniLavoratorePage"/>
<input type="hidden" name="MODULE" value="CM_SAVE_ISCR"/>
<input type="hidden" name="cdnFunzione" value="<%=_cdnFunz%>" />
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratoreDecrypt%>"/> 
<input type="hidden" name="cdnLavoratoreEncrypt" value="<%=cdnLavoratore%>"/> 
<input type="hidden" name="DatAcqRil" value="<%=dataOdierna%>" />
<input type="hidden" name="DatInizio" value="<%=dataOdierna%>" />
<input type="hidden" name="PRGCMISCR" value="<%=prgCmIscr%>"/>
<input type="hidden" name="selTutte" value="<%=selTutte%>"/>

<input type="hidden" name="DATANZIANITADISOCC" value="<%=datAnzianitaDisoc%>"/>

<br/><p class="titolo">Iscrizione Collocamento Mirato Legge 68/99</p>
  	 
<af:showErrors/>
	 
<%=htmlStreamTop%>

<%@ include file="_protocollazioneIscrL68.inc"%>
<%@ include file="../documenti/parametriche/_apriGestioneDocParam.inc"%>

<script>

	function gestisci_Protocollazione(){
		<%-- DOCAREA: basta questo controllo. I valori della protocollazione vengono impostati dalla classe Documento stessa. --%>
		var protocolloLocale = <%=it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil
								.protocollazioneLocale()%>;
		if (!protocolloLocale) return;
	 
		<%if (CODSTATOATTO.equals("NP")) {%>
			if (document.Frm1.CODSTATOATTO.value == "PR"){
			<%rowsProt = serviceResponse
							.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
					if (rowsProt != null && !rowsProt.isEmpty()) {
						rowProt = (SourceBean) rowsProt.elementAt(0);
						numProtV = SourceBeanUtils.getAttrBigDecimal(rowProt,
								"NUMPROTOCOLLO", null);
						numAnnoProtV = (BigDecimal) rowProt
								.getAttribute("NUMANNOPROT");
						dataOraProt = (String) rowProt
								.getAttribute("DATAORAPROT");
						oraProtV = dataOraProt.substring(11, 16);
						datProtV = dataOraProt.substring(0, 10);
					}%>
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
	 	var codStatoAtto = document.Frm1.CODSTATOATTO.value;
		var codStatoAttoInReq = "<%=CODSTATOATTO%>";
	 	codStatoAtto = codStatoAttoInReq;
		if (codStatoAtto == "NP"){
			document.Frm1.numAnnoProt.value = "";
	  		document.Frm1.numProtocollo.value = "";
	  		document.Frm1.dataProt.value = "";
	  		document.Frm1.oraProt.value = "";
	  		document.Frm1.dataOraProt.value = "";
    	}
    }	

	function aggiornaDocumento() {
		var f;
		var codStatoAtto = document.Frm1.CODSTATOATTO.value;
		
		var codStatoAttoInReq = "<%=CODSTATOATTO%>"; 		
 		if (codStatoAttoInReq == codStatoAtto){
	 		alert("Stato atto non modificato");
			return false;
	 	}
	 	
		else {
			if (codStatoAtto == "PR" && iscrCMFuoriResidenza) {
				if (!confirm("Attenzione: il lavoratore iscritto al collocamento mirato non risulta residente nel CPI di riferimento.")) {
					return false;
				}	
		  	}
		  	
	 		var numAnnoProt = document.Frm1.numAnnoProt.value;
	 		var numProtocollo = document.Frm1.numProtocollo.value;
	 		var dataOraProt = document.Frm1.dataOraProt.value;
			var codTipoDocumento = document.Frm1.codTipoDocumento.value;
	 		var tipoProt = document.Frm1.tipoProt.value;
	 		var FlgcodMonoIO = document.Frm1.FlgcodMonoIO.value;
	 		var codAmbito = document.Frm1.codAmbito.value;
	 		var prvIscrizione = document.Frm1.PROVINCIA_ISCR.value;
	 		  		 
			f = "AdapterHTTP?PAGE=CMIscrizioniLavoratorePage&aggiornaDoc=1&MESSAGE=UPDATE";		
			f = f + "&MODULE=CM_SAVE_ISCR";
			f = f + "&cdnLavoratore=<%=cdnLavoratoreDecrypt%>";
			f = f + "&CDNFUNZIONE=<%=_cdnFunz%>";
			f = f + "&selTutte=<%=selTutte%>";
			f = f + "&PRGCMISCR=<%=prgCmIscr%>"; 
			f = f + "&NUMKLOCMISCR=<%=numKloCmIscr%>";
			f = f + "&CODSTATOATTO=" + codStatoAtto;
			f = f + "&numAnnoProt=" + numAnnoProt;		  			  
			f = f + "&numProtocollo=" + numProtocollo;		  			  
			f = f + "&dataOraProt=" + dataOraProt;
			f = f + "&codTipoDocumento=" + codTipoDocumento;
			f = f + "&tipoProt=" + tipoProt;
			f = f + "&FlgcodMonoIO=" + FlgcodMonoIO;
			f = f + "&codAmbito=" + codAmbito;
			f = f + "&PROVINCIA_ISCR=" + prvIscrizione;
		    
		    document.location = f;
		}
	}

</script>

<!--  Ale  -->
<table cellpadding="2" cellspacing="2">
  <tbody>
    <tr>
      <td>

<!--  Ale  - <table class="main">-->
<table>
<%
	if (dettIscr != null) {
%>
<tr>
	<td class="etichetta">Ambito Territoriale</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" name="PROVINCIA_ISCR" value="<%=PROVINCIA_ISCR%>"  
        	readonly="true" size="20"  />
    </td>
</tr>
<%
	}else{
%>

<tr>
    <td class="etichetta">Ambito Territoriale</td>
    <td colspan=3 class="campo">
    	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
        	classNameBase="input" addBlank="true" title="Ambito Territoriale" required="true" />
    </td>
</tr>
		
<%		
	}
%>

<tr>
    <td class="etichetta">Data ultima iscrizione/reiscrizione</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="date" title="Data ultima iscrizione/reiscrizione" 
    		name="DATULTIMAISCR" value="<%=datUltimaIscr%>" required="true" readonly="<%=fieldReadOnly%>" 
    		onKeyUp="fieldChanged();" validateOnPost="true" size="11" maxlength="10" />
    </td>
    <td><input type="hidden" name="DATDATAINIZIO" value="<%=dataInizio%>" /></td>
</tr>
<%
	if (!nuovaIscrizione) {
%>	
<tr>
	<td class="etichetta">Numero d'iscrizione</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="integer" name="NUMISCRIZIONE" value="<%=numIscrizione%>"  
        	readonly="true" size="10" maxlength="10"/>
    </td>
</tr>
<%
	}
%>
<tr>
    <td class="etichetta2">Tipo</td>
	<td class="campo2">
		<af:comboBox name="codMonoTipoRagg" title="Tipo" classNameBase="input" required="true" disabled="<%=disableTipo%>" onChange="visualizzaCategoria()">					  
			<option value=""  <%if ("".equalsIgnoreCase(codMonoTipoRagg)) {%>SELECTED="true"<%}%>></option>
		    <option value="A" <%if ("A".equalsIgnoreCase(codMonoTipoRagg)) {%>SELECTED="true"<%}%>>Altre categorie protette</option>
		    <option value="D" <%if ("D".equalsIgnoreCase(codMonoTipoRagg)) {%>SELECTED="true"<%}%>>Disabili</option>
	    </af:comboBox>        
	</td>
</tr>
<tr>
    <td class="etichetta">Categoria </td>
    <td class=campo2 width="40%">
    	<div id="CategoriaVuota" style="display:none">
    		<af:comboBox name="codCMTipoIscrVuota" classNameBase="input" title="Categoria iscrizione"  
    			addBlank="true" onChange="fieldChanged();" disabled="<%=fieldReadOnly%>"/>*
    	</div>
    	<div id="CategoriaD" style="display:none">
    		<af:comboBox name="codCMTipoIscrD" moduleName="CM_GET_DE_TIPO_ISCR_D" 
    			selectedValue="<%=codCmTipoIscr%>" classNameBase="input" title="Categoria iscrizione" 
    			addBlank="true" disabled="<%=fieldReadOnly%>"/>*
    	</div>
    	<div id="CategoriaA" style="display:none">
    		<af:comboBox name="codCMTipoIscrA" moduleName="CM_GET_DE_TIPO_ISCR_A" 
    			selectedValue="<%=codCmTipoIscr%>" classNameBase="input" title="Categoria iscrizione" 
    			addBlank="true" disabled="<%=fieldReadOnly%>"/>*
    	</div>
    	<input type="hidden" name="CODCMTIPOISCR" title="Categoria" value="<%=codCmTipoIscr%>" />
    </td>
</tr>
<tr>
    <td class="etichetta">Data anzianità iscrizione al CM</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="date" name="DATANZIANITA68" title="Data anzianità iscrizione" 
    		value="<%=datAnzianita68%>" readonly="<%=fieldforStoricoReadOnly%>" onKeyUp="fieldChanged();" validateOnPost="true" size="11" maxlength="10" />
    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    	<input type="button" class="pulsanti" name="MesiAnzianita" value="Mesi Anzianità" onClick="calcolaMesiAnzianita()"/>
	</td>	
</tr>
<%if (configAnzOrdinaria_cm.equals("1")) {%>
	<tr>
	    <td class="etichetta">Data anzianità pregressa ordinario riconosciuta</td>
	    <td colspan=3 class="campo">
	    	<af:textBox classNameBase="input" type="date" name="DATANZORDINARIAPREGRESSA" title="Data anzianità pregressa ordinario riconosciuta" 
	    		value="<%=datAnzOrdinariaPregressa%>" readonly="<%=fieldforStoricoReadOnly%>" onKeyUp="fieldChanged();" validateOnPost="true" size="11" maxlength="10" />
		</td>	
	</tr>
<%} else {%>
	<input type="hidden" name="DATANZORDINARIAPREGRESSA" value="<%=datAnzOrdinariaPregressa%>" />
<%}%>
<tr>
    <td class="etichetta">Tipo invalidità</td>
    <td colspan=3 class="campo">
    	<af:comboBox name="CODTIPOINVALIDITA" moduleName="CM_GET_DE_TIPO_INVAL" selectedValue="<%=codTipoInvalidita%>"
        	classNameBase="input" addBlank="true" onChange="fieldChanged();" disabled="<%=fieldforStoricoReadOnly%>"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Percentuale invalidità</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="integer" name="NUMPERCINVALIDITA" title="Percentuale invalidità" 
    		value="<%=numPercInvalidita%>" readonly="<%=fieldforStoricoReadOnly%>" onKeyUp="fieldChanged();" validateOnPost="true" size="4" maxlength="3" />%
    </td>
</tr>
<%
	if (paramCmIsc1 != null && ("1").equals(paramCmIsc1)) {
%>	

	<tr>
	    <td class="etichetta">Annota per fuori lista</td>
	    <td colspan=3 class="campo">
	    	<af:comboBox name="CODCMANNOTA" moduleName="M_Get_De_Cm_Annota" selectedValue="<%=codCmAnnota%>"
	        	classNameBase="input" addBlank="true" onChange="fieldChanged();" disabled="<%=fieldforStoricoReadOnly%>"/>
	    </td>
	</tr>
<%
	} else {
%>
	<input type="hidden" name="CODCMANNOTA" value="">
<%
	}
%>

<tr>
    <td class="etichetta">Data accertamento sanitario</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="date" name="DATACCERTSANITARIO" title="Data accertamento sanitario" 
    		value="<%=datAccertSanitario%>" readonly="<%=fieldforStoricoReadOnly%>" onKeyUp="fieldChanged();" validateOnPost="true" size="11" maxlength="10" />
   	</td>
</tr>
<tr>
    <td class="etichetta">Tipo accertamento sanitario</td>
    <td colspan=3 class="campo">
    	<af:comboBox name="CODACCERTSANITARIO" moduleName="CM_GET_DE_ACC_SANIT" selectedValue="<%=codAccertSanitario%>"
        	classNameBase="input" title="Tipo accertamento sanitario" addBlank="true" onChange="fieldChanged();" disabled="<%=fieldforStoricoReadOnly%>"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Verbale accertamento</td>
    <td colspan=3 class="campo">
    	<af:comboBox name="PRGVERBALEACC" moduleName="CM_GET_VERB_ACC" selectedValue="<%=prgVerbaleAcc%>"
        	classNameBase="input" title="Verbale accertamento" addBlank="true" onChange="fieldChanged();" disabled="<%=fieldforStoricoReadOnly%>"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Operatore di riferimento</td>
    <td colspan=3 class="campo">
    	<af:comboBox name="PRGSPIMOD" moduleName="COMBO_SPI_SCAD" selectedValue="<%=prgSpiMod%>"
        	classNameBase="input" title="Operatore di riferimento" addBlank="true" onChange="fieldChanged();" disabled="<%=fieldforStoricoReadOnly%>"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Centro per l'impiego competente</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" name="CPICOMP" value="<%=cpiCompLav%>" readonly="true" size="30" maxlength="28" />
    </td>
</tr>
<%
	if (!nuovaIscrizione) {
%>	
<tr>
	<td class="etichetta">Chiusura con automatismo</td>
    <td colspan=3 class="campo">
    	<af:comboBox name="flgAutomImpatti" title="Chiusura con automatismo" classNameBase="input" disabled="true">					  
			<option value=""  <%if ("".equalsIgnoreCase(flgAutomImpatti)) {%>SELECTED="true"<%}%>></option>
		    <option value="S" <%if ("S".equalsIgnoreCase(flgAutomImpatti)) {%>SELECTED="true"<%}%>>Sì</option>
		    <option value="N" <%if ("N".equalsIgnoreCase(flgAutomImpatti)) {%>SELECTED="true"<%}%>>No</option>
	    </af:comboBox>  
    </td>
</tr>
<%
	}
%>
<tr><td colspan=4><br></td></tr>
<tr>
	<td class="etichetta">
  		Note<br/>
  	</td>
    <td class="campo" colspan="3">
    	<af:textArea classNameBase="textarea" name="strNote" value="<%=strNote%>"
                cols="60" rows="4" maxlength="2000"
                onKeyUp="fieldChanged();" readonly="<%=fieldforStoricoReadOnly%>"  />
  	</td>
</tr>
<%if(canViewDiagnosi) { %>
	<tr><td colspan=4><br></td></tr>
	<%	if (diagnosiFunzVect == null){%>
		<tr >
			<td colspan="2" align="center" class="etichetta">
			Diagnosi funzionale non disponibile per questa iscrizione
			</td>
		</tr>
	<% } else if (diagnosiFunzVect.size() == 1){ %>
	<tr>
		<td colspan="2"> <div class="sezione2">Diagnosi funzionale</div></td>
	</tr>
	<tr>
		<td class="etichetta">Data inizio diagnosi</td>
		<td class="campo">
			<af:textBox type="data" disabled="true" size="11" name="data_inizio_diagnosi" value="<%=Utils.notNull(data_inizio_diagnosi) %>"/>
  		</td>
	</tr>
	<tr>
		<td class="etichetta">Data fine diagnosi</td>
		<td class="campo">
			<af:textBox type="data" disabled="true" size="11" name="data_fine_diagnosi" value="<%=Utils.notNull(data_fine_diagnosi) %>"/>
  		</td>
	</tr>
	<tr>
		<td class="etichetta">Data revisione accertamento</td>
		<td class="campo">
			<af:textBox type="data" disabled="true" size="11" name="data_revisione_diagnosi" value="<%=Utils.notNull(data_revisione_diagnosi) %>"/>
  		</td>
	</tr>
	<tr>
		<td class="etichetta" valign="top">Tipo invalidità</td>
		<td class="campo">
			<input type="checkbox" disabled="disabled" name="invalidFisica" <%=invalidFisica?"checked":"" %>>Fisica</input>
			<input type="checkbox" disabled="disabled" name="invalidPsichica" <%=invalidPsichica?"checked":"" %>><%=labelMentale?"Mentale":"Psichica"%></input>
			<input type="checkbox" disabled="disabled" name="invalidIntelletiva" <%=invalidIntellettiva?"checked":"" %>>Intellettiva</input>
			<input type="checkbox" disabled="disabled" name="invalidSensoriale" <%=invalidSensoriale?"checked":"" %>>Sensoriale</input>
		</td>
	</tr>
	<% } else if (diagnosiFunzVect.size() > 1) { %>
	<tr>
		<td colspan="2" align="center" class="etichetta">
			Non è possibile individuare la Diagnosi funzionale più recente
		</td>
	</tr>
	<% } 
}%>
		
<%	

	if (!nuovaIscrizione && !CODSTATOATTO.equals("NP")) {
%>	
<tr>
	<td colspan="2">
		<div class="sezione2">
	    	<img id='IMG1' src='<%=dataFine.equals("")
							&& codMotivoFineAtto.equals("") ? img0 : img1%>' onclick='cambia(this, document.getElementById("TBL1"))'/>Chiusura/cancellazione
	  	</div>
  	</td>
</tr>
<tr>
	<td colspan=3>
    	<table id='TBL1' style='display:<%=dataFine.equals("")
									&& codMotivoFineAtto.equals("") ? "none"
									: "inline"%>'>  
    	 <script>initSezioni(new Sezione(document.getElementById('TBL1'),document.getElementById('IMG1'),<%=hasDataFine%>));</script>
				<tr>
		    		<td class="etichetta">Data cancellazione</td>
		    		<td class="campo"> 
                		<af:textBox classNameBase="input" validateOnPost="true" readonly="<%=disableFineAtto%>" onKeyUp="fieldChanged();" title="Data cancellazione" type="date" name="DATDATAFINE" value="<%=dataFine%>" size="11" maxlength="10"/>
		            </td>
				</tr>
				<tr>
		    		<td class="etichetta">Motivazione cancellazione</td>
		    		<td class="campo"> 
		    			<af:comboBox classNameBase="input" title="Motivo cancellazione" disabled="<%=disableFineAtto%>" onChange="fieldChanged()" name="CODMOTIVOFINEATTO" moduleName="CM_Motivo_Fine_Atto" addBlank="true" selectedValue="<%=codMotivoFineAtto%>">
   						</af:comboBox>
   					</td>
		    	</tr>
		  </table>
	</td>
</tr>
<tr><td colspan=4><br></td></tr>
<%
	}
%>

</table>



<!--  Ale  -->
<%
	if ("2".equals(ConfigGraduatoria_cm) && !nuovaIscrizione && canCalcPunt) {
%>
</td>
<td>
 
	<table>
	
	<tr>
	<td colspan="2"><center><b> Calcolo Punteggio </b><br><br></center></td>
	
  	</tr>
	
	
	<tr>
  	  <td class="etichetta">	Data Riferimento</td>
  	  <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="date" name="Data_Riferimento" title="Data Riferimento" 
    		onKeyUp="fieldChanged();" validateOnPost="true" size="11" maxlength="10" />
   		</td>
	</tr>
	
	
	<tr>
    <td class="etichetta">Anno Reddito</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" name="anno_rif"  size="11" maxlength="10" />
    </td>
	</tr>
	
	<tr>
    <td colspan=3 class="campo">
    <!-- <center> <input type="submit" name="Calcola Punteggio" class="pulsanti" value="calcola" onclick="CalcolaPunteggioPres()" /> </center> -->
    <center> <input type="button" class="pulsante" name="Calcola Punteggio" value="calcola" onclick="javascript:CalcolaPunteggioPres()" /> </center> 
    </td>
	</tr>
		
		
		<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
		
			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>	
  	
  	
  	<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
		
			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>	
  	
  	<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
		
			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>	
  	
  			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>	
  			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
	
		<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>	
  	
  
		<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>	
  		
  			<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>	
  	
  		<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>	
  		
  	
  	
	</table>

</td>
<%
	}
%>
</tr>
  </tbody>
</table>







<table>
  	<%
  		if (nuovaIscrizione && canInsert) {
  	%>
		<tr><td width="33%"></td><td align="center" width="33%"></td><td width="33%"></td></tr>  
		<tr>
			<td colspan="3" align="center">
				<input type="submit" name="inserisci" class="pulsanti" value="Inserisci"/>
				&nbsp;&nbsp;&nbsp;&nbsp; 
				<input type="reset" name="reset" class="pulsanti" value="Annulla" onClick="inizializzaCategoria('<%=codMonoTipoRagg%>');"/>  
			</td>
			<td colspan="2">&nbsp;&nbsp;&nbsp;</td>  			
		  	<td align="right">
		      <input type="button" class="pulsante" name="tornaLista" value="Torna alla lista" onclick="tornaAllaLista()" />
	  		</td>			
	<%
					}
						if (!nuovaIscrizione && canUpdate) {
				%>
		<tr><td width="33%"></td><td align="center" width="33%"></td><td width="33%"></td></tr> 
		<tr>
			<td align="center" colspan="3">
			<%
				if (disableFineAtto.equals("false")) {
							if (CODSTATOATTO.equals("PR")) {
			%>
					<input type="button" name="Aggiorna" class="pulsanti" value="Aggiorna" onClick="controllaDoc(); submitAndInsStoricoMod();"/>
				<%
					} else {
				%>
					<input type="submit" name="Aggiorna" class="pulsanti" value="Aggiorna" onClick="controllaDoc();"/>
				<%
					}
				%>
				&nbsp;&nbsp;&nbsp;
				<input type="reset" name="reset" class="pulsanti" value="Annulla"/>
	
			
			
			
					
				<input type="hidden" name="NUMKLOCMISCR" value="<%=numKloCmIscr%>"> 
				
				<input type="hidden" name="INSSTORICO" value="">
				<input type="hidden" name="DATANZIANITA68OLD" value="<%=datAnzianita68%>">
				<input type="hidden" name="CODTIPOINVALIDITAOLD" value="<%=codTipoInvalidita%>">
				<input type="hidden" name="CODCMANNOTAOLD" value="<%=codCmAnnota%>">
				<input type="hidden" name="NUMPERCINVALIDITAOLD" value="<%=numPercInvalidita%>">
				<input type="hidden" name="DATACCERTSANITARIOOLD" value="<%=datAccertSanitario%>">
				<input type="hidden" name="CODACCERTSANITARIOOLD" value="<%=codAccertSanitario%>">
				<input type="hidden" name="PRGVERBALEACCOLD" value="<%=prgVerbaleAcc%>">
				<input type="hidden" name="PRGSPIMODOLD" value="<%=prgSpiMod%>">
				<input type="hidden" name="STRNOTEOLD" value="<%=strNote%>">
				<input type="hidden" name="CDNUTMODOLD" value="<%=cdnUtMod%>">
				<input type="hidden" name="DTMMODOLD" value="<%=dtmMod%>"> 			
				<input type="hidden" name="CODMONOMOTIVOMODIFICA" value="">
	  		<%
	  			}
	  		%>				  			
			</td> 
			<td colspan="2">&nbsp;&nbsp;&nbsp;</td>  			
		  	<td align="right">
		      <input type="button" name="modStorico" value="Storico Modifiche"
					 class="pulsanti<%=((esisteStorico) ? "" : "Disabled")%>" <%=(!esisteStorico) ? "disabled=\"true\"" : ""%>
					 onClick="apriStoricoMod('LISTA')" />
	  		</td>				  
	    </tr>
	    
	    <tr><td width="33%"></td><td align="center" width="33%"></td><td width="33%"></td></tr>
	    <tr>
			<td align="center" colspan="3">
			<%
				if (CODSTATOATTO.equals("PR") && dataFine.equals("")
								&& codMotivoFineAtto.equals("")) {
							if (canPrintProvvisorio && codMonoTipoRagg.equals("D")) {
			%>
				<input type="button" name="stampa" class="pulsanti" value="Stampa Provvisoria" onClick="StampaCollMirato_temp()"/>
				&nbsp;&nbsp;&nbsp;
				<%
					}
								if (canPrint) {
				%>
				  <input type="button" name="stampa" class="pulsanti" value="Stampa" onClick="StampaCollMirato_def()"/>
				<%
					}
				%>
			</td>	
			<%
					}
				%>
			<td colspan="2">&nbsp;&nbsp;&nbsp;</td>
		  	<td align="right">
		      <input type="button" class="pulsante" name="tornaLista" value="Torna alla lista" onclick="tornaAllaLista()" />
	  		</td>
	  	</tr>	
	<%
			}
		%>				
	
		<input type="hidden" name="MESSAGE" value="<%=message%>"/>
</table> 
 
<%=htmlStreamBottom%>

<%
	if (!nuovaIscrizione) {
%>
<center>
	<table>
		<tr>
			<td align="center">
				<%
					operatoreInfo.showHTML(out);
				%>
			</td>
		</tr>
	</table>
</center>
<%
	}
%>

</af:form>  

<!-- NOTA da passare poi a campi hidden -->
<af:form name="frmTemplate" method="POST" action="AdapterHTTP">
	<input type="hidden" name="PAGE" value="ElaboraStampaParametricaPage" />
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratoreDecrypt%>" />
	<input type="hidden" name="CODCPI" value="<%=codCPI%>" />
	<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunz%>" />
	<input type="hidden" name="PRGTEMPLATESTAMPA" value="" />
	<input type="hidden" name="GENERASTAMPA" value="" />
	<input type="hidden" name="PROTOCOL" value="" />
	<input type="hidden" name="HOST" value="" />
	<input type="hidden" name="PORT" value="" />
	<input type="hidden" name="TIPODOC" value="" />
	<input type="hidden" name="NOMETEMPLATE" value="" />
	<input type="hidden" name="PAGEBACK" value="<%=queryStringBack%>">
	<input type="hidden" name="PRGCMISCR" value="<%=prgCmIscr%>" />
</af:form>
</body>
</html>

