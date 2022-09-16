<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, 
                it.eng.sil.security.*,
                it.eng.sil.module.trento.Consenso,
                it.eng.sil.module.movimenti.constant.Properties,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
	
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%

String pageToProfile = "ListaStampeParLavPage";

ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}
	// NOTE: Attributi della pagina (pulsanti e link) 
	PageAttribs attributi = new PageAttribs(user, pageToProfile);

boolean protocollazione = (serviceRequest.containsAttribute("GENERASTAMPA") && 
		  serviceRequest.getAttribute("GENERASTAMPA").toString().equalsIgnoreCase("STAMPA"));
boolean visualizzaStampa = (serviceRequest.containsAttribute("GENERASTAMPA") && 
		  serviceRequest.getAttribute("GENERASTAMPA").toString().equalsIgnoreCase("VISUALIZZA"));


Testata operatoreInfo = null;
	String _page = (String) serviceRequest.getAttribute("PAGE");
	String _module = (String) serviceRequest.getAttribute("MODULE");
	String dominio = (String)serviceRequest.getAttribute("dominio");
	String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String prgAzienda = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
	
//     InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
//     testata.setSkipLista(true);

//	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	int _funzione = Integer.parseInt(serviceRequest.getAttribute("CDNFUNZIONE")==null?"0":
		(String) serviceRequest.getAttribute("CDNFUNZIONE"));
	
    String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");

// 	if (!filter.canView()) {
// 		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
// 	}

	String cdnUtins = "";
	String cdnUtmod = "";
	String dtmins = "";
	String dtmmod = "";
	String prgDocumentoIns = "";
	
	String STRNOTE = "";
	String DATAACQUISIZIONE = "";
	String codIO = "";
	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	DATAACQUISIZIONE = formatter.format(new Date());
	
	String DATINIZIOVAL = "";
	String DATFINEVAL = "";
	String PRGTEMPLATESTAMPA = "";
	String moduleName = "MSalvaStampaParam";
	String pageName = "InsStampaParamPage";
	String btnSalva = "Salva";
	String codStatoAtto = "NP";
	String tipoDocumento = "";
	String btnAnnulla = "Torna alla lista";
	boolean isInInsert = true;
	boolean canModifyCampi = true;
	boolean abilitaBottoni = true;
	boolean canInsertAllegati = attributi.containsButton("INSERISCI_ALLEGATO"); //X TEST
	boolean canGeneraStampa = attributi.containsButton("GENERA_STAMPA"); //X TEST
	
	
	SourceBean salvaStampaRow = (SourceBean) serviceResponse.getAttribute("MSalvaStampaParam");
	SourceBean CM_LAV_IS_ISCRITTO = (SourceBean) serviceResponse.getAttribute("CM_LAV_IS_ISCRITTO.ROWS.ROW");
	boolean checkProtocollaDocumento = serviceResponse.containsAttribute("MProtocollaDocPadre.ECCEZIONEPROTOCOLLA");
	String codiceErroreProtocolla = "";
	if (checkProtocollaDocumento){
		codiceErroreProtocolla = serviceResponse.getAttribute("MProtocollaDocPadre.ECCEZIONEPROTOCOLLA").toString();
	}
	
	boolean checkWarningReport = serviceResponse.containsAttribute("MProtocollaDocPadre.WARNINGREPORT");
	ArrayList codiciWarnings = null;
	String codiceWarningsString = "";
	if (checkWarningReport){
		codiciWarnings = (ArrayList) serviceResponse.getAttribute("MProtocollaDocPadre.WARNINGREPORT");
		codiceWarningsString = codiciWarnings.toString();
	}
	
	
	int ISCRIZIONE = Integer.valueOf(""+CM_LAV_IS_ISCRITTO.getAttribute("ISCRIZIONE")).intValue();

// Commento le parti riguardanti i controlli sulla presenza del consenso.
// Tali controlli andranno ripresi e rivisti quando si lavorerà sull'integrazione
// 	if (salvaStampaRow != null && salvaStampaRow.containsAttribute("CONSENSOASSENTE")) {
// 		abilitaBottoni = false;	
// 	}
	SourceBean stampaRow = (SourceBean) serviceResponse.getAttribute("MDETTAGLIOSTAMPAPARAM.ROWS.ROW");
	if (stampaRow != null && stampaRow.getAttribute("PRGDOCUMENTO") != null) {
		isInInsert = false;
		canModifyCampi = false;
		cdnUtins = stampaRow.containsAttribute("cdnUtins") ? stampaRow.getAttribute("cdnUtins").toString() : "";
		dtmins = stampaRow.containsAttribute("dtmins") ? stampaRow.getAttribute("dtmins").toString() : "";
		cdnUtmod = stampaRow.containsAttribute("cdnUtmod") ? stampaRow.getAttribute("cdnUtmod").toString() : "";
		dtmmod = stampaRow.containsAttribute("dtmmod") ? stampaRow.getAttribute("dtmmod").toString() : "";
		prgDocumentoIns = stampaRow.containsAttribute("prgdocumento") ? stampaRow.getAttribute("prgdocumento").toString() : "";
		PRGTEMPLATESTAMPA = stampaRow.containsAttribute("prgtemplatestampa") ? stampaRow.getAttribute("prgtemplatestampa").toString() : "";
		codIO = stampaRow.containsAttribute("codmonoio") ? stampaRow.getAttribute("codmonoio").toString() : "";
		tipoDocumento = stampaRow.containsAttribute("tipodocumento") ? stampaRow.getAttribute("tipodocumento").toString() : "";
		if (codIO.equals("")) {
			codIO = "N";
		}
		codStatoAtto = stampaRow.containsAttribute("codStatoAtto") ? stampaRow.getAttribute("codStatoAtto").toString() : "";
		DATAACQUISIZIONE = stampaRow.containsAttribute("datacqril") ? stampaRow.getAttribute("datacqril").toString() : "";
		DATINIZIOVAL = stampaRow.containsAttribute("datinizio") ? stampaRow.getAttribute("datinizio").toString() : ""; 
		DATFINEVAL = stampaRow.containsAttribute("datfine") ? stampaRow.getAttribute("datfine").toString() : "";
		STRNOTE = stampaRow.containsAttribute("strNote") ? stampaRow.getAttribute("strNote").toString() : "";
	}
	else {
		if (serviceResponse.containsAttribute("MDETTAGLIOSTAMPAPARAM")) {
			PRGTEMPLATESTAMPA = serviceRequest.containsAttribute("PRGTEMPLATESTAMPA") ? serviceRequest.getAttribute("PRGTEMPLATESTAMPA").toString() : "";
			codIO = serviceRequest.containsAttribute("FlgCodMonoIO") ? serviceRequest.getAttribute("FlgCodMonoIO").toString() : "";
			codStatoAtto = serviceRequest.containsAttribute("codStatoAtto") ? serviceRequest.getAttribute("codStatoAtto").toString() : "";
			DATAACQUISIZIONE = serviceRequest.containsAttribute("dataAcquisizione") ? serviceRequest.getAttribute("dataAcquisizione").toString() : "";
			DATINIZIOVAL = serviceRequest.containsAttribute("dataInizio") ? serviceRequest.getAttribute("dataInizio").toString() : ""; 
			DATFINEVAL = serviceRequest.containsAttribute("dataFine") ? serviceRequest.getAttribute("dataFine").toString() : "";
			STRNOTE = serviceRequest.containsAttribute("note") ? serviceRequest.getAttribute("note").toString() : "";	
			tipoDocumento = stampaRow.containsAttribute("tipodocumento") ? stampaRow.getAttribute("tipodocumento").toString() : "";
		}
	}
	
// 	String queryString = "";
// 	 String _pageBackFromStampa = "ListaStampeParLavPage";
// 	  queryString = "CDNLAVORATORE="+cdnLavoratore+"&page="+_pageBackFromStampa+"&cdnFunzione="+_funzione+"&TIPODOCUMENTO=" + tipoDocumento; 
	
	  String queryString = "";

	  String _pageBackFromStampa = "ListaStampeParLavPage";

	  queryString = "CDNLAVORATORE="+cdnLavoratore+"&DOMINIO="+dominio+"&prgAzienda="+prgAzienda+"&prgUnita="+prgUnita+"&page="+_pageBackFromStampa+"&cdnFunzione="+_funzione+"&TIPODOCUMENTO=" + tipoDocumento; 


	  
	operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
	boolean canModify = attributi.containsButton("SALVA_ALLEGATO");
	//boolean canModify = true; //X TEST
	//boolean canInsertAllegati = true; //X TEST
	
	boolean checkAllegati = false;
	SourceBean moduleCheckAllegati = (SourceBean) serviceResponse.getAttribute("MCheckAllegatiDocPadre");
	if (moduleCheckAllegati != null) {
		if (moduleCheckAllegati.containsAttribute("ALLEGATIOBBL")) {
			checkAllegati = true;	
		}
	}
	
	Vector rowsAllegatiTemplateObbl = serviceResponse.getAttributeAsVector("MCheckAllegatiDocPadre.rowsAllegatiTemplateObbl.ROWS.ROW");
	String arrNameTypeStampa[] = null;
	StringBuilder sbNameTypeStampa = new StringBuilder();
	if (rowsAllegatiTemplateObbl.size() > 0) {
		  arrNameTypeStampa = new String[rowsAllegatiTemplateObbl.size()];
	   	for(int i=0; i<rowsAllegatiTemplateObbl.size(); i++)  { 
	   		SourceBean rowAllegato = (SourceBean) rowsAllegatiTemplateObbl.elementAt(i);
	  		String strNameTypeStampa = (String) rowAllegato.getAttribute("NOMETIPOSTAMPA");
	  		arrNameTypeStampa[i] = strNameTypeStampa;
	  		sbNameTypeStampa.append(strNameTypeStampa + ";");
	   }
	}
	
 	String msgConfermaGeneraStampa = "";
 	//boolean checkFirma = false;
	/* 
	Commento le parti riguardanti i controlli sulla presenza del consenso.
	Tali controlli andranno ripresi e rivisti quando si lavorerà sull'integrazione
	
	*/


	boolean checkFirma = false;
	checkFirma = serviceResponse.containsAttribute("MCheckFirmaGrafometrica.FLGFIRMAGRAFO"); 

//   	SourceBean moduleCheckFirma = (SourceBean) serviceResponse.getAttribute("MCheckFirmaGrafometrica");
//   	if (moduleCheckFirma != null) {
// 		if (moduleCheckFirma.containsAttribute("FIRMAGRAFOMETRICA")) {
// 			checkFirma = true;
// 			if (moduleCheckFirma.containsAttribute("FLGFIRMAGRAFO")) {
// 				Consenso consensoLav = (Consenso)sessionContainer.getAttribute("CONSENSO_" + cdnLavoratore);
// 				if (consensoLav != null) {
// 					String codStatoConsenso = consensoLav.getCodice();
// 					if (codStatoConsenso != null) {
// 						if (codStatoConsenso.equalsIgnoreCase(Consenso.REVOCATO)) {
// 							msgConfermaGeneraStampa = "Attenzione, il consenso &egrave; revocato e quindi occorre procedere con la firma autografa del documento";
// 						}
// 						else {
// 							if (codStatoConsenso.equalsIgnoreCase(Consenso.NON_DISPONIBILE)) {
// 								msgConfermaGeneraStampa = "Attenzione, consenso non disponibile, se si desidera procedere, il documento sarà sottoscritto "
// 									+ "con firma autografa";
// 							}
// 						}	
// 					}
// 				}
// 				else {
// 					msgConfermaGeneraStampa = "Attenzione, consenso non disponibile, se si desidera procedere, il documento sarà sottoscritto "
// 							+ "con firma autografa";	
// 				}
// 			}
// 		}
//   	}
  	   //Controllo di eseguibilità "Gestione Consenso"
	boolean isConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma"); 
	boolean isConsensoAttivo = false;
	
//	boolean firmaGrafometrica = serviceRequest.containsAttribute("FIRMAGRAFOMETRICA") ? Boolean.parseBoolean(serviceRequest.getAttribute("FIRMAGRAFOMETRICA").toString()) : false;
//	isConsensoAttivo = firmaGrafometrica;
	String ipOperatore=null;
	if(isConsenso){
/*	 	ProfileDataFilter filterConsenso = new ProfileDataFilter(user, "HomeConsensoPage");
		isButtonGestioneConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma.viewGestioneConsensoBtn") 
				  						&& filterConsenso.canView();*/
	 	isConsensoAttivo= serviceResponse.containsAttribute("M_VerificaAmConsensoFirma.consensoFirmaAttivo");
	 	if(isConsensoAttivo){
	 		ipOperatore = serviceResponse.getAttribute("M_VerificaAmConsensoFirma.ipOperatore").toString();
	 	}
	 	checkFirma = checkFirma && isConsensoAttivo;
	 //	checkFirma = isConsensoAttivo;
	}
	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
 	//Vector rowsClassificazioni = serviceResponse.getAttributeAsVector("ComboClassificazioni.ROWS.ROW");
 	Vector rowsClassificazioni=null;
 	int cdnProfilo = 0;
	cdnProfilo = user.getCdnProfilo();
	if(cdnProfilo==9){
	 	rowsClassificazioni = serviceResponse.getAttributeAsVector("ComboClassificazioniStampeDominioPat.ROWS.ROW");
	}
	else if(ISCRIZIONE>=1){
		rowsClassificazioni = serviceResponse.getAttributeAsVector("ComboClassificazioniStampeDominio.ROWS.ROW");
	}
	else {
		rowsClassificazioni = serviceResponse.getAttributeAsVector("ComboClassificazioniStampeDominioNoCM.ROWS.ROW");
	}
 	SourceBean row = null;
 	java.math.BigDecimal prgClassif = new java.math.BigDecimal(0);
 	java.math.BigDecimal prgTemplateStampa = new java.math.BigDecimal(0);
 	java.math.BigDecimal prgConfigProt = new java.math.BigDecimal(0);
 	String nomeClassif = "";
 	String nomeStampa = "";
 	String actualClassif = "";
 	String codTipoDominioLista = "";

%>

<head>
  <%@ include file="SezioniATendina.inc" %>
  <%@ include file="../global/fieldChanged.inc" %>
  <%@ include file="../global/setFlagIO.inc" %>
  <%@ include file="CommonFunctionStampa.inc" %>
  <%@ include file="_apriGestioneDoc.inc"%>
  <%
if(visualizzaStampa) {
	%>
	<script language="Javascript">
	  var urlDoc = "AdapterHTTP?";
	  urlDoc += "PAGE=REPORTFRAMESTAMPAPAGE";
	  urlDoc += "&QUERY_STRING="+HTTPrequest;
	  //alert(urlDoc);
	  document.location=urlDoc;
	  
	  

	</script>
	<%	

}else{
%>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script type="text/javascript" src="../../js/fckeditor/editor/js/jquery-1.11.2.js">
  </script>
  
  <script type="text/javascript">
  
	function checkDate() {
		ok = true;
		var e = document.getElementById("PRGTEMPLATESTAMPA_ID").selectedIndex;
		
		if(e==-1){
			alert("Il campo Lista per Classificazione è obbligatorio");
			document.getElementById("PRGTEMPLATESTAMPA_ID").focus();
			ok = false;
		}
		
		var objData1 = document.getElementsByName("dataInizio");
		var objData2 = document.getElementsByName("dataFine");

		strData1 = objData1.item(0).value;
		strData2 = objData2.item(0).value;

		//costruisco la data di inizio
		if (strData1 != "") {
			d1giorno = parseInt(strData1.substr(0, 2), 10);
			d1mese = parseInt(strData1.substr(3, 2), 10) - 1; //il conteggio dei mesi parte da zero 
			d1anno = parseInt(strData1.substr(6, 4), 10);
			data1 = new Date(d1anno, d1mese, d1giorno);
		}

		//costruisce la data di fine
		if (strData2 != "") {
			d2giorno = parseInt(strData2.substr(0, 2), 10);
			d2mese = parseInt(strData2.substr(3, 2), 10) - 1;
			d2anno = parseInt(strData2.substr(6, 4), 10);
			data2 = new Date(d2anno, d2mese, d2giorno);
		}

		
		if (strData1 != "" && strData2 != "") {
			if (data2 < data1) {
				alert("La data di fine validità deve essere successiva o uguale alla data di inizio validità");
				document.getElementsByName("dataInizio").item(0).focus();
				ok = false;
			}
		}
		return ok;
	}
	
	  function apriGestioneConsenso( ) {
		  var urlpage="AdapterHTTP?";
		    urlpage+="PAGE=HomeConsensoPage&";
		    urlpage+="CDNFUNZIONE=<%=_funzione%>&";
		    urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
//		    setOpenerWindowLocation(urlpage);
		    window.open(urlpage, "main");
		}
	

  </script>
  <script>
  var comboTemplateStampeDominio = new Array();
	<%
	SourceBean comboTemplateStampeDominio = (SourceBean) serviceResponse.getAttribute("ComboClassificazioniStampeDominio.ROWS");
	
	Vector vectSB = comboTemplateStampeDominio.getAttributeAsVector("ROW");
	Iterator iter = vectSB.iterator();
	
	while (iter.hasNext()) {
		SourceBean comboTemplateStampeDominioRow = (SourceBean) iter.next();
		if (comboTemplateStampeDominioRow != null){
	%>
			var codiceArr = '<%=comboTemplateStampeDominioRow.getAttribute("CODICE").toString()%>';
			comboTemplateStampeDominio[codiceArr]= new Array();
			comboTemplateStampeDominio[codiceArr]['STRIO']='<%=comboTemplateStampeDominioRow.getAttribute("strio").toString()%>';
			comboTemplateStampeDominio[codiceArr]['FLGFIRMAGRAFO']='<%=comboTemplateStampeDominioRow.getAttribute("FLGFIRMAGRAFO").toString()%>';

			
	<%	
		}
	}
	%> 

	

  </script>
  <script language="JavaScript" type="text/javascript">
	<!--
		
		function openSelect(){
			var viewClassificazioniObj = document.getElementById('viewClassificazioni');
			var viewClassificazioniObj2 = document.getElementById('lista');
			
			
			if (viewClassificazioniObj.style.display == 'none'){
				viewClassificazioniObj.style.display = 'block';
				viewClassificazioniObj2.style.display = 'table-row';
			} else{
				viewClassificazioniObj.style.display = 'none';
				viewClassificazioniObj2.style.display = 'none';
			}
		}
		
		
		function sizeTbl(t) {
		  var tbl = document.getElementById('template_'+t);

		  var imgFolderClose = document.getElementById('folder_close_'+t);
		  var imgFolderOpen = document.getElementById('folder_open_'+t);
		  
		  if (tbl.style.display == 'none'){
			  tbl.style.display='block';
			  imgFolderClose.style.display='none';
			  imgFolderOpen.style.display='block';
		  } else{ 
			  tbl.style.display = 'none';
			  imgFolderClose.style.display='block';
			  imgFolderOpen.style.display='none';
		  }
		}
		
		
		function valorizeClassif(value, text){
			var doClassificazioneObj = document.getElementById('PRGTEMPLATESTAMPA_ID');
			//remove all options from list
			doClassificazioneObj.innerHTML = "";
			
			//add new option
			doClassificazioneObj.options[doClassificazioneObj.options.length] = new Option(text, value);
			
			var viewClassificazioniObj = document.getElementById('viewClassificazioni');
			var viewClassificazioniObj2 = document.getElementById('lista');
			viewClassificazioniObj.style.display = 'none';
			viewClassificazioniObj2.style.display = 'none';
		}
				
	// -->
</script> 
  <title>Dettaglio Template</title>
</head>
<body>


<%
	// TESTATA LAVORATORE
	if (StringUtils.isFilled(cdnLavoratore)) {
		InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
		testata.setSkipLista(true);
		testata.show(out);
	}
	
	// TESTATA AZIENDA
	if (StringUtils.isFilled(prgAzienda)) {
		InfCorrentiAzienda testata = new InfCorrentiAzienda(prgAzienda, prgUnita);
		testata.setSkipLista(true);
		testata.show(out);
	}
	

%>
<%-- 	<%testata.show(out);%> --%>
	<font color="red"> 
		<af:showErrors />
	</font>
	<font color="green">
		<af:showMessages prefix="MSalvaStampaParam"/>
		<af:showMessages prefix="MSalvaAllegatoStampaParam"/>
		<af:showMessages prefix="MProtocollaDocPadre"/>
		<af:showMessages prefix="MSalvaDocumentoAllegatoStampaParam"/>
		<div id="messaggioConsenso" style="display:none">
		<%if(isConsenso){%>
 	   	  <af:showMessages prefix="M_VerificaAmConsensoFirma"/>
	   	<%}%>
	   	</div>
	</font>
 

<%if (protocollazione) {%>
<script language="Javascript">
visualizzaDocumento('DOWNLOAD','&ErroreProtocollazione=<%=codiceErroreProtocolla%>&warnings=<%=codiceWarningsString%>','<%=prgDocumentoIns%>');
</script>
<%} else {
%>


	<p class="titolo">Crea Stampa Parametrica</p>
	<%
		out.print(htmlStreamTop);
	%>
	

	<af:form name="form" action="AdapterHTTP" method="POST" onSubmit="checkDate()">
		<input type="hidden" id="page" name="PAGE" value="<%=pageName%>">
		<input type="hidden" id="pageBack" name="PAGEBACK" value="InsStampaParamPage">
		<input type="hidden" id="module" name="MODULE" value="<%=moduleName%>">
		<input type="hidden" name="PRGDOCUMENTO" value="<%=prgDocumentoIns%>">
		<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
		<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
		<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
		<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
		<input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>">
		<input type="hidden" name="TIPODOCUMENTO" value="<%=tipoDocumento%>" />
		<input type="hidden" name="codTipoDocumento" value="<%=tipoDocumento%>">
		<input type="hidden" name="DOMINIO" value="<%=dominio%>">
		<!-- IL TIPO DI EDITOR DA GESTIRE è "L" (LAVORATORE) -->
		<input type="hidden" name="TIPOEDITOR" value="L">
		<input type="hidden" id="FIRMAGRAFOMETRICA" name="FIRMAGRAFOMETRICA" value="<%=checkFirma%>">
		<input type="hidden" name="IPOPERATORE" value="<%=ipOperatore%>">

		<p align="center">
		<table class="main">
			
			<%-- <%
			
			int cdnProfilo = 0;
			cdnProfilo = user.getCdnProfilo();
			if(cdnProfilo==9){
			%>
			<tr>
				<td class="etichetta">Nome template</td>
				<td class="campo"><af:comboBox name="PRGTEMPLATESTAMPA"
						size="1" title="Nome template" multiple="false" 
						required="true" focusOn="false" moduleName="ComboTemplateStampeDominioPat"
						classNameBase="input" disabled="<%=String.valueOf(!canModifyCampi)%>"
						onChange="fieldChanged();setFlagIO(this.value)"
						selectedValue="<%=PRGTEMPLATESTAMPA%>" addBlank="true"
						blankValue="" />
				</td>
			</tr>
			<%}else if(ISCRIZIONE==1){ %>			
			<tr>
				<td class="etichetta">Nome template</td>
				<td class="campo"><af:comboBox name="PRGTEMPLATESTAMPA"
						size="1" title="Nome template" multiple="false" 
						required="true" focusOn="false" moduleName="ComboTemplateStampeDominio"
						classNameBase="input" disabled="<%=String.valueOf(!canModifyCampi)%>"
						onChange="fieldChanged();setFlagIO(this.value)"
						selectedValue="<%=PRGTEMPLATESTAMPA%>" addBlank="true"
						blankValue="" />
				</td>
			</tr>
			
			<%}else{ %>			
			<tr>
				<td class="etichetta">Nome template</td>
				<td class="campo"><af:comboBox name="PRGTEMPLATESTAMPA"
						size="1" title="Nome template" multiple="false" 
						required="true" focusOn="false" moduleName="ComboTemplateStampeDominioNoCM"
						classNameBase="input" disabled="<%=String.valueOf(!canModifyCampi)%>"
						onChange="fieldChanged();setFlagIO(this.value)"
						selectedValue="<%=PRGTEMPLATESTAMPA%>" addBlank="true"
						blankValue="" />
				</td>
			</tr>
			<%} %> --%>
			<%

			if(canModifyCampi){
			%>
			<tr>
			   		<td class="etichetta">
						Lista per Classificazione
					</td>
					<td class="campo" >
						<SELECT name="PRGTEMPLATESTAMPA" id="PRGTEMPLATESTAMPA_ID" style="width:650px;" onClick="javascript:openSelect();" />
					</td>
			</tr>

			   <tr id="lista">
			   		<td class="etichetta">
						&nbsp;
					</td>
					<td class="campo">
					
					<table class='master' border=0 id='viewClassificazioni' style='display:none;border-left: 1px solid #e2e3ea;border-right: 1px solid #e2e3ea;border-bottom: 1px solid #e2e3ea;width:650px;border-collapse: collapse;'>
				
						<tr style='background-color: #e8f3ff;'>
							<td width='650px' style='background-color: white;'>
							
								<%
								if (rowsClassificazioni.size() > 0) {
								      for(int i=0; i<rowsClassificazioni.size(); i++)  { 
								        row = (SourceBean) rowsClassificazioni.elementAt(i);
								        
								        prgClassif = (java.math.BigDecimal) row.getAttribute("PRGCLASSIF");
								        prgTemplateStampa = (java.math.BigDecimal) row.getAttribute("PRGTEMPLATESTAMPA");
								        prgConfigProt = (java.math.BigDecimal) row.getAttribute("PRGCONFIGPROT");
								        
								        codTipoDominioLista = (String) row.getAttribute("codTipoDominio");
								        
								        nomeClassif = (String) row.getAttribute("nomeClassif");
								        nomeStampa = (String) row.getAttribute("nomeStampa");
								        
								        if (!actualClassif.equalsIgnoreCase(nomeClassif)){
								        	actualClassif = nomeClassif;
						
											if(i>0){%>
														</div>
													</td>
												</tr>
											</table>
											<table>
												<tr>
													<td style="vertical-align:top;">
														<a href="javascript:sizeTbl('<%=i%>')" style="color: black;">
															<img id='folder_close_<%=i%>' src='../../img/folder_0.png' style='display:block;' />
															<img id='folder_open_<%=i%>' src='../../img/folder_1.png' style='display:none;' />
														</a>
													</td>
													
													<td>
														<!-- <a href="javascript:sizeTbl('<%=i%>')" style="color: black;"> -->
														<%-- <a href="javascript:valorizeClassif('<%=prgClassif%>', '<%=nomeClassif%>')" style="color: black;">
														<!-- <a href="javascript:go('PAGE=GestTemplatePage&MODULE=MListaTemplate&PRGCLASSIF=<%=prgClassif%>&nomeClassif=<%=actualClassif%>&cdnFunzione=<%=cdnFunzione%>&codTipoDominio=<%=codTipoDominioLista%>&backfrom=ricerca', 'false');"  title="Ricerca i Template associati a questa Classificazione"> -->
															<%=actualClassif%> sfsfsefwerwer
														</a> --%>
														<span width='95%' style='text-decoration: none;'><%=actualClassif%></span>
														<div id='template_<%=i%>' style='display:none;'>
															<p>
																<span width='5%'>- </span>
																<!-- <a href="javascript:go('PAGE=DettaglioTemplatePage&MODULE=MDettaglioTemplate&MOSTRATEMPLATE=S&PRGTEMPLATESTAMPA=<%=prgTemplateStampa%>&PRGCONFIGPROT=<%=prgConfigProt%>&cdnFunzione=<%=cdnFunzione%>&codTipoDominio=<%=codTipoDominioLista%>&backfrom=ricerca', 'false');"  title="vai alla pagina di Dettaglio del Template"> -->
<%-- 																	<span width='95%' style='text-decoration: none;'> <%=nomeStampa%></span>
 --%>																<!-- </a> -->
																<a href="javascript:valorizeClassif('<%=prgTemplateStampa%>', '<%=nomeStampa.replace("\'","\\'") %>');fieldChanged();setFlagIO('<%=prgTemplateStampa%>')" style="color: black;">
																	<span width='95%' style='text-decoration: none;'> <%=nomeStampa%></span>
																</a>
															</p>
														
											<%} else{ %>
											<table>
												<tr>
													<td style="vertical-align:top;">
														<a href="javascript:sizeTbl('<%=i%>')" style="color: black;">
															<img id='folder_close_<%=i%>' src='../../img/folder_0.png' style='display:block;' />
															<img id='folder_open_<%=i%>' src='../../img/folder_1.png' style='display:none;' />
														</a>
													</td>
													
													<td>
														<!-- <a href="javascript:sizeTbl('<%=i%>')" style="color: black;"> -->
														<%-- <a href="javascript:valorizeClassif('<%=prgClassif%>', '<%=nomeClassif%>')" style="color: black;">
														<!-- <a href="javascript:go('PAGE=GestTemplatePage&MODULE=MListaTemplate&PRGCLASSIF=<%=prgClassif%>&nomeClassif=<%=actualClassif%>&cdnFunzione=<%=cdnFunzione%>&codTipoDominio=<%=codTipoDominioLista%>&backfrom=ricerca', 'false');"  title="Ricerca i Template associati a questa Classificazione"> -->
															<%=actualClassif%> sdsdsdsd
														</a> --%>
														<span width='95%' style='text-decoration: none;'><%=actualClassif%> </span>
														<div id='template_<%=i%>' style='display:none;'>
															<p>
																<span width='5%'>- </span>
																<!-- <a href="javascript:go('PAGE=DettaglioTemplatePage&MODULE=MDettaglioTemplate&MOSTRATEMPLATE=S&PRGTEMPLATESTAMPA=<%=prgTemplateStampa%>&PRGCONFIGPROT=<%=prgConfigProt%>&cdnFunzione=<%=cdnFunzione%>&codTipoDominio=<%=codTipoDominioLista%>&backfrom=ricerca', 'false');" title="vai alla pagina di Dettaglio del Template"> -->
<%-- 																	<span width='95%' style='text-decoration: none;'> <%=nomeStampa%></span>
 --%>																<!-- </a> -->
																<a href="javascript:valorizeClassif('<%=prgTemplateStampa%>', '<%=nomeStampa.replace("\'","\\'")%>');fieldChanged();setFlagIO('<%=prgTemplateStampa%>')" style="color: black;">
																	<span width='95%' style='text-decoration: none;'> <%=nomeStampa%></span>
																</a>
															</p>
														
											<%} //if >0 %>
										
										<% } else{ %>
										
															<p>
																<span width='5%'>- </span>
																<!-- <a href="javascript:go('PAGE=DettaglioTemplatePage&MODULE=MDettaglioTemplate&MOSTRATEMPLATE=S&PRGTEMPLATESTAMPA=<%=prgTemplateStampa%>&PRGCONFIGPROT=<%=prgConfigProt%>&cdnFunzione=<%=cdnFunzione%>&codTipoDominio=<%=codTipoDominioLista%>&backfrom=ricerca', 'false');" title="vai alla pagina di Dettaglio del Template"> -->
<%-- 																	<span width='95%' style='text-decoration: none;'> <%=nomeStampa%></span>
 --%>																<!-- </a> -->
																<a href="javascript:valorizeClassif('<%=prgTemplateStampa%>', '<%=nomeStampa.replace("\'","\\'")%>');fieldChanged();setFlagIO('<%=prgTemplateStampa%>')" style="color: black;">
																	<span width='95%' style='text-decoration: none;'> <%=nomeStampa%></span>
																</a>
															</p>
										
										<% } // else %>
									
									<% } //for %>
													</div>
												</td>
											</tr>
										</table>
								
								<% } //if size > 0 %>
								
								</td>*
							</tr>
						</table>
					</td>
				</tr>			   
			<tr> 
			<%}else{ 
				
				int cdnProfiloReadOnly = 0;
				cdnProfiloReadOnly = user.getCdnProfilo();
				if(cdnProfiloReadOnly==9){
				%>
				<tr>
					<td class="etichetta">Lista per Classificazione</td>
					<td class="campo"><af:comboBox name="PRGTEMPLATESTAMPA"
							size="1" title="Lista per Classificazione"
							required="true" focusOn="false" moduleName="ComboClassificazioniStampeDominioPat"
							classNameBase="input" disabled="<%=String.valueOf(!canModifyCampi)%>"
							selectedValue="<%=PRGTEMPLATESTAMPA%>"
							blankValue="" />
					</td>
				</tr>
				<%}else if(ISCRIZIONE>=1){ %>			
				<tr>
					<td class="etichetta">Lista per Classificazione</td>
					<td class="campo"><af:comboBox name="PRGTEMPLATESTAMPA"
							size="1" title="Lista per Classificazione"
							required="true" focusOn="false" moduleName="ComboClassificazioniStampeDominio"
							classNameBase="input" disabled="<%=String.valueOf(!canModifyCampi)%>"
							selectedValue="<%=PRGTEMPLATESTAMPA%>" 
							blankValue="" />
					</td>
				</tr>
				
				<%}else{ %>			
				<tr>
					<td class="etichetta">Lista per Classificazione</td>
					<td class="campo"><af:comboBox name="PRGTEMPLATESTAMPA"
							size="1" title="Lista per Classificazione" 
							required="true"  moduleName="ComboClassificazioniStampeDominioNoCM"
							classNameBase="input" disabled="<%=String.valueOf(!canModifyCampi)%>"							
							selectedValue="<%=PRGTEMPLATESTAMPA%>" />
					</td>
				</tr>
				<%} %>
			<%} %>
			<td class="etichetta">Doc di Input/Output</td> 
 			<td class="campo"> 
               <af:comboBox name="FlgCodMonoIO" addBlank="false"
                        title="Documento di Input/Output/Non Applicabile" classNameBase="input" 
                       disabled="<%=String.valueOf(!canModifyCampi)%>" 
                       required="true" 
                        onChange="fieldChanged()"> 
 					<option value="I" <%=(codIO.equalsIgnoreCase("I")||codIO.equalsIgnoreCase(""))? "SELECTED=\"true\"" : "" %>>Input</option> 
 					<option value="O" <%=(codIO.equalsIgnoreCase("O"))? "SELECTED=\"true\"" : "" %>>Output</option> 
<%--  					<option value="N" <%=(codIO.equalsIgnoreCase("N"))? "SELECTED=\"true\"" : "" %>>Non Applicabile</option> --%>
            		</af:comboBox> 
             </td> 
            </tr> 

			<tr>
				<td class="etichetta">Data di acquisizione/rilascio da parte del Centro per l'impiego</td>
				<td class="campo">
				     <af:textBox type="date" name="dataAcquisizione" classNameBase="input"
							title="Data di acquisizione" required="true" value="<%=DATAACQUISIZIONE%>" size="12" maxlength="10"
							validateOnPost="true" readonly="<%=String.valueOf(!canModifyCampi)%>"/> 
				</td>
			</tr>

			<tr>
				<td class="etichetta">Data inizio validita'</td>
				<td class="campo">
				     <af:textBox type="date" name="dataInizio" classNameBase="input"
							title="Data inizio validita'" required="true" value="<%=DATINIZIOVAL%>" size="12" maxlength="10"
							validateOnPost="true" readonly="<%=String.valueOf(!canModifyCampi)%>"/> &nbsp;&nbsp;&nbsp;&nbsp;Data fine validita'&nbsp;&nbsp;
					<af:textBox
							type="date" name="dataFine" required="false" value="<%=DATFINEVAL%>" size="12" maxlength="10"
							title="Data fine validita'" classNameBase="input" 
							validateOnPost="true" readonly="<%=String.valueOf(!canModifyCampi)%>"/></td></td>
			</tr>
			
			<tr>
				<td class="etichetta">Note</td>
				<td class="campo">    
					<af:textArea name="note" cols="60" rows="4" title="Note" readonly="<%=String.valueOf(!canModifyCampi)%>" classNameBase="input"
					value="<%=STRNOTE%>" maxlength="1000" />
				</td>
			</tr>
			
			<%if (canInsertAllegati && !canModifyCampi) {%>
			
				<tr>
	          		<td class="etichetta" colspan="2">
	            	<div class='sezione2' id='UlteInfoAllegati'>
	               	 <img id='tendinaInfo' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("altreInfoSezAllegati"));'/>
	                	Allegati&nbsp;  
	            	</div>
	          		</td>
	        	</tr>
				
				<tr>
	          	<td colspan="2">
	            	<div id="altreInfoSezAllegati" style="display:inline">
					<table class="main" width="100%" border="0">
						<tr>
						<td colspan="2">
						<af:list moduleName="MListaAllegatiStampaParam" skipNavigationButton="1" />
						</td>
						</tr>
					</table>
					</div>
				</td>
				</tr>
			<%}%>
			
		</table>
		<br>
		<table>
			<tr>
				<td align="center">
					<%
					if (isInInsert&&canModify) {
					%>
						<input type="submit" class="pulsanti" name="salva" value="<%=btnSalva%>" >
					<%} else{
						if (abilitaBottoni) {
							if (!codStatoAtto.equalsIgnoreCase(Properties.STATO_ATTO_PROTOC)) {
								if(canInsertAllegati){
							%>
							
								<input type="button" class="pulsanti" name="addAllegati" value="Inserisci Allegato" onClick="gestioneAllegatiDocumento();">&nbsp;
								<%
								}
								if(canGeneraStampa){
								%>
								<input type="button" class="pulsanti" name="btnGeneraStampa" value="Genera Stampa" onClick="generaStampa('<%=sbNameTypeStampa%>');">
<!-- 								<input type="button" class="pulsanti" name="btnPiTre" value="Protocollazione PiTre">&nbsp; -->
<!-- 								<input type="button" class="pulsanti" name="btnRicevuta" value="Stampa Ricevuta"> -->
							<%	}
							}
						}
					}%>
				</td>
				<td align="center"><input type="button" class="pulsanti"
					name="ANNULLA" value="<%=btnAnnulla%>" onClick="annulla();">
				</td>
			</tr>
		</table>
	  	</p>
	</af:form>
	<div align="center">
		<%
			operatoreInfo.showHTML(out);
		%>
	</div>
	<%
		out.print(htmlStreamBottom);
	%>
	<%}%>
</body>
</html>
<%}%>
