<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, java.io.*, 
                it.eng.sil.security.*,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*, oracle.sql.CLOB"%>


<%@ taglib uri="aftags" prefix="af"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%
	Testata operatoreInfo = null;
	String _page = (String) serviceRequest.getAttribute("PAGE");
	String _module = (String) serviceRequest.getAttribute("MODULE");
	
	String backfrom = (String) serviceRequest.getAttribute("backfrom");
	
	String pageToProfile = "RicercaTemplatePage";
	ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

	String cdnUtins = "";
	String cdnUtmod = "";
	String dtmins = "";
	String dtmmod = "";

	BigDecimal NUMKLOTEMP=null;
	BigDecimal NUMKLOCONFPROT = null;
	String STRNOME = "";
	String STRNOTE = "";
	String CODAMBITOTEM = "";
	String CODTIPODOMINIO = "";
	String CODTIPOPROTOCOLLAZIONE = "";
	String CODTIPOTRATTAMENTO = "";
	String FLGPREDISPOSTO = "";
	String PRGTITOLARIO = "";
	String FLGFIRMAGRAFO = "";
	String FLGOPPAT = "";
	String FLGCM = "";
	String FLGTRASMISSIONEINTERNA = "";
	String DATINIZIOVAL = "";
	String DATFINEVAL = "";
	String DESCCODAMBITOTEM = "";
	String DESCDOMINIO = "";
	String PRGTEMPLATESTAMPA = "";
	String PRGCONFIGPROT = "";
	String PRGCLASSIF = "";
	String FILETEMPLATE = "";
	
	String moduleName = "";
	String pageName = "DettaglioTemplatePage";
	String btnSalvaDatiGeneraliTemplate = "";
	String operazioneEditor = "";
	String btnModificaEditorTemplate = "";
	String templateOperation = "";
	String duplicaTemplate = StringUtils.getAttributeStrNotNull(serviceRequest, "DUPLICATEMPLATE");
	CODTIPODOMINIO = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPODOMINIO");
	
	//qui torno dalla modifica dell'editor
	//if ("DettaglioTemplatePage".equals(_page) && "MAggiornaEditor".equals(_module))
	//{
	//	PRGTEMPLATESTAMPA = (String) serviceRequest.getAttribute("PRGTEMPLATESTAMPA");
	//	moduleName = "MAggiornaEditor";
	//	pageName  = "DettaglioTemplatePage";
	//}
	//entro in questa if se richiedo, dalla lista dei template, un dettaglio dati di un template 
	// o dalla modifica dell'editor di un dettaglio dati di un template
	boolean isErrorInsTemplate = false;
	SourceBean rowTemplate = null;
	Vector templatePresenti = null;
	SourceBean templateRow = null;
	if ("DettaglioTemplatePage".equals(_page))
	{
		if (!duplicaTemplate.equalsIgnoreCase("S")) {
			templatePresenti = serviceResponse.getAttributeAsVector("MUpdateCaricaInfoTemplatePresenti.ROWS.ROW");
			templateRow = (SourceBean) serviceResponse.getAttribute("MDETTAGLIOTEMPLATE.ROWS.ROW");
			if (templateRow == null || templateRow.getAttribute("PRGTEMPLATESTAMPA") == null) {
				isErrorInsTemplate = true;	
			}
		}
		else {
			templatePresenti = serviceResponse.getAttributeAsVector("MCaricaInfoTemplatePresenti.ROWS.ROW");	
		}
		if (duplicaTemplate.equalsIgnoreCase("S") || isErrorInsTemplate) {
			//modulo da richiamare al submit
			moduleName = "MInsTemplate";
			//operazioneEditor = "Apri Editor";
			btnSalvaDatiGeneraliTemplate = "Inserisci";
			btnModificaEditorTemplate = "Apri Editor";
		}
		else {
			//modulo da richiamare al submit
			if(templateRow.getAttribute("PRGCONFIGPROT")==null || templateRow.getAttribute("PRGCONFIGPROT").equals("")){
				templateOperation = "udpate_insert";
				moduleName = "MAggiornaInsTemplate"; 
			}else{
				templateOperation = "udpate";
				moduleName = "MAggiornaTemplate";
			}
			operazioneEditor = "Modifica";
			btnModificaEditorTemplate = "Apri Editor";
			btnSalvaDatiGeneraliTemplate = "Aggiorna";	
		}
		
		if (!duplicaTemplate.equalsIgnoreCase("S") && isErrorInsTemplate) {
			STRNOME = StringUtils.getAttributeStrNotNull(serviceRequest, "STRNOME");
			CODAMBITOTEM = StringUtils.getAttributeStrNotNull(serviceRequest, "ambito");
			STRNOTE = StringUtils.getAttributeStrNotNull(serviceRequest, "note");
			CODTIPODOMINIO = StringUtils.getAttributeStrNotNull(serviceRequest, "dominio");
			CODTIPOPROTOCOLLAZIONE = StringUtils.getAttributeStrNotNull(serviceRequest, "codTipoProtocollazione");
			CODTIPOTRATTAMENTO = StringUtils.getAttributeStrNotNull(serviceRequest, "codTipoTratt");
			DATINIZIOVAL = StringUtils.getAttributeStrNotNull(serviceRequest, "dataInizio");
			DATFINEVAL = StringUtils.getAttributeStrNotNull(serviceRequest, "dataFine");
			FLGPREDISPOSTO = StringUtils.getAttributeStrNotNull(serviceRequest, "flgPredisposto");
			PRGTITOLARIO = StringUtils.getAttributeStrNotNull(serviceRequest, "prgTitolario");
			FLGFIRMAGRAFO = StringUtils.getAttributeStrNotNull(serviceRequest, "flgFirmaGrafo");
			FLGOPPAT = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGOPPAT");
			FLGCM = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGCM");
			FLGTRASMISSIONEINTERNA = StringUtils.getAttributeStrNotNull(serviceRequest, "flgTrasmissioneInterna");
			PRGCLASSIF = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGCLASSIF");
		}
		else {
			SourceBean cont = (SourceBean) serviceResponse
					.getAttribute("MDETTAGLIOTEMPLATE");
			SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
		
			//dati letti dalla query di dettaglio
			if (!duplicaTemplate.equalsIgnoreCase("S") && !isErrorInsTemplate) {
				STRNOME = row.containsAttribute("STRNOME") ? row.getAttribute(
						"STRNOME").toString() : "";
			}
			STRNOTE = row.containsAttribute("STRNOTE") ? row.getAttribute(
					"STRNOTE").toString() : "";
		
			CODAMBITOTEM = row.containsAttribute("CODAMBITOTEM") ? row
					.getAttribute("CODAMBITOTEM").toString() : "";
			CODTIPODOMINIO = row.containsAttribute("CODTIPODOMINIO") ? row
					.getAttribute("CODTIPODOMINIO").toString() : "";
			CODTIPOPROTOCOLLAZIONE = row.containsAttribute("CODTIPOPROTOCOLLAZIONE") ? row
					.getAttribute("CODTIPOPROTOCOLLAZIONE").toString() : "";
			CODTIPOTRATTAMENTO = row.containsAttribute("CODTIPOTRATTAMENTO") ? row
					.getAttribute("CODTIPOTRATTAMENTO").toString() : "";
			DATINIZIOVAL = row.containsAttribute("DATINIZIOVAL") ? row
					.getAttribute("DATINIZIOVAL").toString() : "";
			DATFINEVAL = row.containsAttribute("DATFINEVAL") ? row
					.getAttribute("DATFINEVAL").toString() : "";
			FLGPREDISPOSTO = row.containsAttribute("FLGPREDISPOSTO") ? row
					.getAttribute("FLGPREDISPOSTO").toString() : "";
			PRGTITOLARIO = row.containsAttribute("PRGTITOLARIO") ? row
					.getAttribute("PRGTITOLARIO").toString() : "";
			FLGFIRMAGRAFO = row.containsAttribute("FLGFIRMAGRAFO") ? row
					.getAttribute("FLGFIRMAGRAFO").toString() : "";
			FLGOPPAT = row.containsAttribute("FLGOPPAT") ? row
					.getAttribute("FLGOPPAT").toString() : "";
			FLGCM = row.containsAttribute("FLGCM") ? row
					.getAttribute("FLGCM").toString() : "";
			PRGCLASSIF = row.containsAttribute("PRGCLASSIF") ? row
					.getAttribute("PRGCLASSIF").toString() : "";
			
			FLGTRASMISSIONEINTERNA = row.containsAttribute("FLGTRASMISSIONEINTERNA") ? row
					.getAttribute("FLGTRASMISSIONEINTERNA").toString() : "";	
					
			if (duplicaTemplate.equalsIgnoreCase("S")) {
				if (row.containsAttribute("FILETEMPLATE"))
				{
					CLOB clobdata = (CLOB) row.getAttribute("FILETEMPLATE");
					
					StringBuffer sb1 = new StringBuffer();
					
					Reader reader = clobdata.getCharacterStream();
					BufferedReader in = new BufferedReader(reader);
					String line = null;
					while ((line = in.readLine()) != null) {
						sb1.append(line);
					}
					if(in != null){
						in.close();
					}
					
					FILETEMPLATE = sb1.toString(); // this is the clob data converted into string
					FILETEMPLATE = StringUtils.formatValue4Html(FILETEMPLATE);
				} 	
			}			
			
			if (!duplicaTemplate.equalsIgnoreCase("S") && !isErrorInsTemplate) {
				PRGTEMPLATESTAMPA = row.containsAttribute("PRGTEMPLATESTAMPA")
						? row.getAttribute("PRGTEMPLATESTAMPA").toString()
						: "";
				PRGCONFIGPROT = row.containsAttribute("PRGCONFIGPROT")
						? row.getAttribute("PRGCONFIGPROT").toString()
						: "";
				PRGCLASSIF = row.containsAttribute("PRGCLASSIF")
						? row.getAttribute("PRGCLASSIF").toString()
						: "";
						
				NUMKLOTEMP = (BigDecimal) row.getAttribute("NUMKLOTEMP");
				NUMKLOCONFPROT = (BigDecimal) row.getAttribute("NUMKLOCONFPROT");
			    // fine
		
				cdnUtins = row.containsAttribute("cdnUtins") ? row.getAttribute(
					"cdnUtins").toString() : "";
				dtmins = row.containsAttribute("dtmins") ? row.getAttribute(
					"dtmins").toString() : "";
				cdnUtmod = row.containsAttribute("cdnUtmod") ? row.getAttribute(
					"cdnUtmod").toString() : "";
				dtmmod = row.containsAttribute("dtmmod") ? row.getAttribute(
					"dtmmod").toString() : "";
			}	
		}
	}
	/**qui entro se:
	   Provengo dalla lista con una richiesta di INSERIMENTO
	   			OPPURE
	   Provengo dalla PAGE di inserimento dei dati dell'editor salvati in SESSIONE
	   */
	else if ("InsTemplatePage".equals(_page))
	{
		templatePresenti = serviceResponse.getAttributeAsVector("MCaricaInfoTemplatePresenti.ROWS.ROW");
		moduleName = "MInsTemplate";
		//operazioneEditor = "Apri Editor";
		btnSalvaDatiGeneraliTemplate = "Inserisci";
		btnModificaEditorTemplate = "Apri Editor";
		//VERIFICO SE CI SONO DATI SALVATI IN CACHE
		NavigationCache formInserimentoTemplate = null;
		if (sessionContainer.getAttribute("EDITORCACHE") != null) {
			formInserimentoTemplate = (NavigationCache) sessionContainer.getAttribute("EDITORCACHE");
			STRNOME = formInserimentoTemplate.getField("STRNOME").toString();
			CODAMBITOTEM = formInserimentoTemplate.getField("AMBITO").toString();
			CODTIPODOMINIO = formInserimentoTemplate.getField("DOMINIO").toString();
			CODTIPOPROTOCOLLAZIONE = formInserimentoTemplate.getField("CODTIPOPROTOCOLLAZIONE").toString();
			CODTIPOTRATTAMENTO = formInserimentoTemplate.getField("CODTIPOTRATTAMENTO").toString();
			FLGPREDISPOSTO = formInserimentoTemplate.getField("FLGPREDISPOSTO").toString();
			PRGTITOLARIO = formInserimentoTemplate.getField("PRGTITOLARIO").toString();
			FLGFIRMAGRAFO = formInserimentoTemplate.getField("FLGFIRMAGRAFO").toString();
			FLGOPPAT = formInserimentoTemplate.getField("FLGOPPAT").toString();
			FLGCM = formInserimentoTemplate.getField("FLGCM").toString();
			FLGTRASMISSIONEINTERNA = formInserimentoTemplate.getField("FLGTRASMISSIONEINTERNA").toString();
			DATFINEVAL = formInserimentoTemplate.getField("DATAFINE").toString();
			DATINIZIOVAL = formInserimentoTemplate.getField("DATAINIZIO").toString();
			STRNOTE = formInserimentoTemplate.getField("NOTE").toString();
			formInserimentoTemplate.setField("FILETEMPLATE", (String) serviceRequest.getAttribute("FILETEMPLATE"));
			FILETEMPLATE  =  (String) serviceRequest.getAttribute("FILETEMPLATE");
			PRGCLASSIF =  (String) serviceRequest.getAttribute("PRGCLASSIF");
		}
		else {
			DATFINEVAL = MessageCodes.General.DEFAULT_DATA_FINE_VAL;
			PRGCLASSIF =  (String) serviceRequest.getAttribute("PRGCLASSIF");
		}
	}
		  
	String btnAnnulla = "Torna alla lista";
	String goBackUrl = (String) sessionContainer.getAttribute("_TOKEN_GESTTEMPLATEPAGE");

	operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
	PageAttribs attributi = new PageAttribs(user, pageToProfile);
	boolean canModify = attributi.containsButton("SALVA_TEMPLATE");
	boolean canAggiungiAll = attributi.containsButton("AGGIUNGI_ALLEGATO");
	//boolean canModify = true; //X TEST
	if (canModify) {
		btnAnnulla = "Chiudi";
	}
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
	
// 	String moduleListTemplate = "M_ST_TEMPLATE_STAMPA";
// 	SourceBean content = null;
// 	content = (SourceBean) serviceResponse.getAttribute(moduleListTemplate);
	
// 	Vector rowsTemplate = content.getAttributeAsVector("ROWS.ROW");
	
// 	SourceBean rowTemplateForSort = (SourceBean) content.getAttribute("ROW");
	%>

<head>
  <%@ include file="SezioniATendina.inc" %>
  <%@ include file="../global/fieldChanged.inc" %>
  <%--
  <script type="text/javascript" src="../../js/angular.js"></script>
  	<script>


var app = angular.module('myApp', []);
app.controller('ArrayController', function ($scope) {
    $scope.peoples = [
                      <%
                      int indexTemplate;
                      
                      for(indexTemplate=0; indexTemplate < rowsTemplate.size(); indexTemplate++) {
                    	  rowTemplateForSort = (SourceBean) rowsTemplate.elementAt(indexTemplate);
                      		String nomeTemplate = StringUtils.getAttributeStrNotNull(rowTemplateForSort, "STRNOME");
                   			if(indexTemplate==0){
                      			
                      %>
                     		 	{name: '<%=nomeTemplate%>'}
                      <%
                      		}else{
                      %>
     							,{name: '<%=nomeTemplate%>'} 
     					<%
                      		}
                      }
     					%>
     	];
    $scope.moveUp = function () {
        for(var i = 0; i < $scope.persons.length; i++) {
            var idx = $scope.peoples.indexOf($scope.persons[i])
            //console.log(idx);
            if (idx > 0) {
                var itemToMove = $scope.peoples.splice(idx, 1)
                //console.log(itemToMove[0])
                $scope.peoples.splice(idx-1, 0, itemToMove[0]);
                
            }
        }
        return false;
    };
    
   $scope.moveDown = function () {
        for(var i = 0; i < $scope.persons.length; i++) {
            var idx = $scope.peoples.indexOf($scope.persons[i])
            //console.log(idx);
            if (idx < $scope.peoples.length) {
                var itemToMove = $scope.peoples.splice(idx, 1)
                //console.log(itemToMove[0])
                $scope.peoples.splice(idx+1, 0, itemToMove[0]);
                
            }
        }
        return false;
    };    
});
</script>
  --%>
  
  <script type="text/javascript">

  
  
  	var templateNome = new Array();
    <%
    int nContElem = 0;
    String strNameTemplate = "";
    	if (templatePresenti.size() > 0) {
     	for(int i=0; i<templatePresenti.size(); i++)  { 
    		rowTemplate = (SourceBean) templatePresenti.elementAt(i);
    		strNameTemplate = (String) rowTemplate.getAttribute("nome");
           	out.print("templateNome["+nContElem+"]=\""+ strNameTemplate + "\";\n"); 
           	nContElem = nContElem + 1;
     	}
 	}%>
  	
	function annulla() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		if (flagChanged == true) {
			if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) {
				return;
			} 
		}
		
		var url = "AdapterHTTP?";
		var backFrom = document.getElementById("BACK_FROM");
		if (backFrom.value != null && backFrom.value != ''){
			setWindowLocation(url + "PAGE=RicercaTemplatePage");
		} else{
		    setWindowLocation(url + "<%=StringUtils.formatValue4Javascript(goBackUrl) %>");
		}
	}
	

	function UpperCaseCodice(objCodice) {
		if (objCodice.value != '') {
			objCodice.value = (objCodice.value).toUpperCase();
		}
	}

	/**
	Questa funzione viene chiamata quando si preme il tasto modifica/apri editor
	*/
	function ctrPage() {
		
	  	// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;
	  	
		if (flagChanged == true) {
			if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) {
				return;
			} 
		}
		
		var elem = document.getElementById("page");
		elem.value = "EditorPage";
		var elem2 = document.getElementById("module");
		elem2.value = "MDettaglioEditor";
		
		//INPOSTO IL DOMINIO DATI PER VERIFICARE SE FAT APPARIRE LA SELCT BOX DOMINIO DATI LAVORATORE
		var dom = document.getElementById("dominioDati");
		var com = document.getElementsByName("dominio")[0];
		//console.log(com.options[com.selectedIndex].text);
		dom.value = com.options[com.selectedIndex].text;
		
	}

  	
  	function goEditorPage(){
  	
  		
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		if (flagChanged == true) {
			if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) {
				return;
			} 
		}
		
		var urlpage = "AdapterHTTP?CDNFUNZIONE="+<%=cdnFunzione%>+"&PAGE=EditorPage";
		setWindowLocation(urlpage );
		 
}
  	
	
	function checkValidita() {
		var checkResult = true;
		if(checkDateValidita())
			checkResult = checkProtocollazione();
		return checkResult;
	}
	
	function checkProtocollazione() {
		var checkResult = true;
		if(document.getElementsByName("codTipoProtocollazione")[0].value=='A'&&
				(document.getElementsByName("codTipoTratt")[0].value=='R'||document.getElementsByName("codTipoTratt")[0].value=='NA')){
			
			checkResult = false;
			alert('Per la protocollazione in entrata, scegliere come trattamento del documento il valore "Protocollo".');
			
		}
		
		if(document.getElementsByName("codTipoProtocollazione")[0].value=='P'&&
				(document.getElementsByName("codTipoTratt")[0].value=='R'||document.getElementsByName("codTipoTratt")[0].value=='NA')
				&&document.getElementsByName("flgFirmaGrafo").checked){
			
			checkResult = false;
			alert('Per il documento non è previsto l\'uso della firma grafometrica');
			
		}
		if(document.getElementsByName("codTipoProtocollazione")[0].value=='P'&&
				(document.getElementsByName("codTipoTratt")[0].value=='R'||document.getElementsByName("codTipoTratt")[0].value=='NA')){
			
			checkResult = false;
			alert('Per la protocollazione in uscita, il trattamento del documento è "Protocollo".');
			
		}
		
		if(document.getElementsByName("codTipoProtocollazione")[0].value=='G'&&
				document.getElementsByName("codTipoTratt")[0].value=='P'){
			
			checkResult = false;
			alert('Per il documento non protocollato, il trattamento del documento è Repertorio oppure Non Applicabile');
			
		}


		return checkResult;
	}
	
	function checkDateValidita() {
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
		
		ok = true;
		if (strData1 != "" && strData2 != "") {
			if (data2 < data1) {
				alert("La data di fine validità deve essere successiva o uguale alla data di inizio validità");
				document.getElementsByName("dataInizio").item(0).focus();
				ok = false;
			}
		}

		if (ok) {
			var strNomeTemplateIns = document.frmTemplate.STRNOME.value;
			var existTemplate = false;
		    var xCont;
		    if (templateNome.length > 0 ) {
		        for (xCont=0;xCont<templateNome.length;xCont++) {
		        	if (strNomeTemplateIns.toUpperCase() == templateNome[xCont].toUpperCase()) {
		        		existTemplate = true;
	                    xCont = templateNome.length;
	                }
		        }
		    }
			
			if (existTemplate) {
				alert("Un template con lo stesso nome è già stato inserito");
				ok = false;
			}
		}
		
		return ok;
	}

	function dettaglioAllegato(pageName, moduleName, prgConfigProt, prgAllegato, cdnFunz, prgTempStampa) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		var f = "AdapterHTTP?PAGE=DettaglioAllegatiTemplatePage&CDNFUNZIONE=" + cdnFunz + 
		"&PRGCONFIGPROT=" + prgConfigProt + "&PRGCONFIGPROTDOCTIPO=" + prgAllegato + "&PRGTEMPLATESTAMPA=" + prgTempStampa + "&CODTIPODOMINIO=<%=CODTIPODOMINIO%>";  
		var t = "_blank";
		var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes," + 
		"resizable=yes,width=850,height=250,top=450,left=350";
		window.open(f, t, feat);
	}

	<%if (!_page.equalsIgnoreCase("InsTemplatePage") && !duplicaTemplate.equalsIgnoreCase("S") && !isErrorInsTemplate) {%>
	function apriGestioneAllegati() {
		var f = "AdapterHTTP?PAGE=DettaglioAllegatiTemplatePage&CDNFUNZIONE=<%=cdnFunzione%>&CODTIPODOMINIO=<%=CODTIPODOMINIO%>&PRGCONFIGPROT=<%=PRGCONFIGPROT%>&PRGTEMPLATESTAMPA=<%=PRGTEMPLATESTAMPA%>";
 
		var t = "_blank";
		var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes," + 
		"resizable=yes,width=850,height=250,top=450,left=350";
		window.open(f, t, feat);
		//alert(f);
	}
	<%}%>
		
  </script>
  <script language="javascript">



	function setTipoTrattamento(sel){
		
	  	if(sel == 'A' || sel == 'P'){
	  		document.getElementsByName("codTipoTratt")[0].value='P';
	  	}
	}
	  
	  

</script>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Dettaglio Template</title>
  <%-- 
    <link rel="stylesheet" href="http://www.html.it/guide/esempi/jqueryui/esempi/css/smoothness/jquery-ui-1.7.2.custom.css" type="text/css" />
  <script type="text/javascript" src="http://www.html.it/guide/esempi/jqueryui/esempi/js/jquery-1.3.2.min.js" /></script>
  <script type="text/javascript" src="http://www.html.it/guide/esempi/jqueryui/esempi/js/jquery-ui-1.7.2.custom.min.js" ></script>
<style type="text/css">
#sortable1{ list-style-type: none; margin: 0; padding: 0;  margin-right: 10px; } 
#sortable1 li{ margin: 0 3px 3px 3px; padding: 3px; width: 300px; }
</style>
	<script type="text/javascript">
	$(function() {
		$("#sortable1").sortable({
			connectWith: '.connectedSortable'
		}).disableSelection();
	});
	</script>
	--%>
</head>
<body>
	<font color="red">
  		<af:showErrors/>
	</font>
	<font color="green">
	  	<af:showMessages prefix="MInsTemplate"/>
	  	<af:showMessages prefix="MAggiornaTemplate"/>
	  	<af:showMessages prefix="MAggiornaInsTemplate"/>
	  	<af:showMessages prefix="MInsAllegatoConfig"/>
	  	<af:showMessages prefix="MAggiornaAllegatoConfig"/>
	  	<af:showMessages prefix="MDeleteAllegatoConfig"/>
	  	<af:showMessages prefix="MAggiornaEditor"/>
	</font>

	<p class="titolo">
		<%if ( ("InsTemplatePage".equals(_page)) || ("DettaglioTemplatePage".equals(_page) && (duplicaTemplate.equalsIgnoreCase("S") || isErrorInsTemplate)) ) {%>
			Inserisci Template
		<%} else {%>
			Consulta Template
		<%}%>
	</p>
	<%
		out.print(htmlStreamTop);
	%>

	<af:form name="frmTemplate" action="AdapterHTTP" method="POST" onSubmit="checkValidita()">
		<input type="hidden" id="page" name="PAGE" value="<%=pageName%>">
		<input type="hidden" id="module" name="MODULE" value="<%=moduleName%>">
		<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
		<input type="hidden" name="PRGTEMPLATESTAMPA" value="<%=PRGTEMPLATESTAMPA%>">
		<input type="hidden" name="PRGCONFIGPROT" value="<%=PRGCONFIGPROT%>">
		<input type="hidden" name="PRGCLASSIF" value="<%=Utils.notNull(PRGCLASSIF)%>">
		<input type="hidden" name="templateOperation" value="<%=templateOperation%>">
		<input type="hidden" name="NUMKLOTEMP" value="<%=Utils.notNull(NUMKLOTEMP)%>">
		<input type="hidden" name="NUMKLOCONFPROT" value="<%=Utils.notNull(NUMKLOCONFPROT)%>">
		<!-- inizio campi gestione allegati -->
		<input type="hidden" name="NUMKLOCONFPROTDOC" value="">
		<input type="hidden" name="PRGCONFIGPROTDOCTIPO" value="">
		<input type="hidden" name="tipoAllegatoTemplate" value="">
		<input type="hidden" name="CODTIPODOCUMENTO" value="">
		<input type="hidden" name="CODTIPODOMINIO" value="<%=CODTIPODOMINIO%>">
		<input type="hidden" name="STRDESCRIZIONE" value="">
		<input type="hidden" name="PRGTEMPLATEALLEGATO" value="">
		<input type="hidden" name="FLGOBBL" value="">
		<!-- fine campi gestione allegati -->
		<!-- tipoOpeEditor (tipo operazione editor) serve per determinare se la label pulsante nell'editor di AMM è "nuovo" o "modifica" -->
		<input type="hidden" id="tipoOpeEditor" name="TIPOOPERAZIONEEDITOR" value="<%=operazioneEditor%>">
		<input type="hidden" name="FILETEMPLATE" value="<%=FILETEMPLATE%>">
		<input type="hidden" id="dominioDati" name="DOMINIODATI" value="">
		<!-- IL TIPO DI EDITOR è "A" (AMMINISTRAZIONE) -->
		<input type="hidden" name="TIPOEDITOR" value="A">
		
		<!-- SE SI PROVIENE DALLA RICERCA ATTRAVERSO LE CLASSIFICAZIONI -->
		<input type="hidden" id="BACK_FROM" name="BACK_FROM" value="<%=Utils.notNull(backfrom)%>">
		
		<p align="center">
		<table class="main">
			<tr>
				<td class="etichetta">Nome template</td>
				<td class="campo"><af:textBox type="text" 
						name="STRNOME"
						title="Nome template" required="true" size="50" value="<%=STRNOME%>"
						onKeyUp="fieldChanged();" classNameBase="input"
						readonly="<%=String.valueOf(!canModify)%>" maxlength="100" /></td>
			</tr>

			<tr>
				<td class="etichetta">Tipo documento</td>
				<td class="campo"><af:comboBox name="ambito"
						size="1" title="Tipo documento" multiple="false"
						required="true" focusOn="false" moduleName="ComboTipoDocumento"
						classNameBase="input" disabled="<%=String.valueOf(!canModify)%>"
						onChange="fieldChanged()"
						selectedValue="<%=CODAMBITOTEM%>" addBlank="true"
						blankValue=""  /></td>
			</tr>
			
			
			<tr>
				<td class="etichetta">Classificazioni</td>
				<td class="campo"><af:comboBox name="classificazioni"
						size="1" title="classificazioni" multiple="false"
						required="true" focusOn="false" moduleName="ComboClassificazioniPerDominioDati"
						classNameBase="input"
						onChange="fieldChanged()"
						selectedValue="<%=PRGCLASSIF%>" /></td>
			</tr>
<%
String disable_dominio = "false";
String required = "false";

if ( ("InsTemplatePage".equals(_page)) || ("DettaglioTemplatePage".equals(_page) && (duplicaTemplate.equalsIgnoreCase("S") || isErrorInsTemplate)) ) {
			disable_dominio="true";
			required="true";
		} else {
			disable_dominio="true";
			required="false";
		}
		%>
			<tr>
				<td class="etichetta">Dominio dati</td>
				<td class="campo"><af:comboBox name="dominio"
						size="1" title="Dominio dati" multiple="false" 
						 focusOn="false" moduleName="ComboPrgTipoDominio"
						classNameBase="input" 
						disabled="<%=disable_dominio%>"
						required="<%=required%>"
						onChange="fieldChanged()"
						selectedValue="<%=CODTIPODOMINIO%>" /></td>
			</tr>

			<tr>
				<td class="etichetta">Inizio validita'</td>
				<td class="campo">
				     <af:textBox type="date" name="dataInizio"
							title="Inizio validita'" required="true" value="<%=DATINIZIOVAL%>" size="12" maxlength="10"
							validateOnPost="true" disabled="<%=String.valueOf(!canModify)%>"/>&nbsp;&nbsp;Fine validita'&nbsp;&nbsp;
					<af:textBox
							type="date" name="dataFine" required="true" value="<%=DATFINEVAL%>" size="12" maxlength="10"
							title="Fine validita'"
							validateOnPost="true" disabled="<%=String.valueOf(!canModify)%>"/></td></td>
			</tr>

			<tr>
				<td class="etichetta">Visibile agli operatori Patronato</td>
				<td class="campo">
					<input type="checkbox" name="FLGOPPAT" value="1" <%if(FLGOPPAT.equals("1")) { out.print("CHECKED"); }%>>
				</td>
  			</tr>
			<tr>
				<td class="etichetta">Visibile solo per gli iscritti al CM</td>
				<td class="campo">
					<input type="checkbox" name="FLGCM" value="1" <%if(FLGCM.equals("1")) { out.print("CHECKED"); }%>>
				</td>
  			</tr>

			<tr>
				<td class="etichetta">Note</td>
				  
				<td class="campo">
					<af:textArea name="note" cols="60" rows="4" title="Note" readonly="<%=String.valueOf(!canModify)%>" classNameBase="input"
					value="<%=STRNOTE%>" />
				</td>
			</tr>
		        	<tr>
        	<td class="campo" colspan="2">
        	<br /><br />
        	</td>
        	</tr>
        	<%--
			<tr>
          		<td class="etichetta" colspan="2">
            	<div class='sezione2' id='OrdTempl'>
               	 <img id='tendinaOrdTempl' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("ordTemplSez"));'/>
                
               	 	Ordinamento Template&nbsp;  
            	</div>
            	
            	
          		</td>
          		
        	</tr>

        	<tr>
        	<td class="campo" colspan = "2" align="center">
        		<div ng-app='myApp' ng-controller="ArrayController" >
   				 <select id="select" size="9" ng-model="persons" ng-options="item as item.name for item in peoples" multiple></select>
    				<br/>
				    <a href="#" ng-click="moveUp()">Sposta su</a>
				    <br/>
				    <a href="#" ng-click="moveDown()">Sposta giu</a>
				    <br/>
				</div>
			</td>
			</tr>
        	<tr>
        	<td class="campo" colspan="2">
        	<br /><br />
        	</td>
        	</tr>
        	
        	 --%>
			<tr>
          		<td class="etichetta" colspan="2">
            	<div class='sezione2' id='UlteInfo'>
               	 <img id='tendinaInfo' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("altreInfoSez"));'/>
                	Configurazione dati protocollo &nbsp;  
            	</div>
          		</td>
        	</tr>
        	
        	<tr>
          	<td colspan="2">
            	<div id="altreInfoSez" style="display:inline">
				<table class="main" width="100%" border="0">
					<tr>
						<td class="etichetta">Tipo protocollazione</td>
						<td class="campo"><af:comboBox name="codTipoProtocollazione"
								size="1" title="Tipo protocollazione" multiple="false" 
								required="true" focusOn="false" moduleName="ComboTipoProtocollazioneTemplate"
								classNameBase="input" disabled="<%=String.valueOf(!canModify)%>"
								onChange="fieldChanged();setTipoTrattamento(this.value)"
								selectedValue="<%=CODTIPOPROTOCOLLAZIONE%>" addBlank="true"
								blankValue="" /></td>
					</tr>
					<tr>
						<td class="etichetta">Tipo Trattamento </td>
						<td class="campo"><af:comboBox name="codTipoTratt"
								size="1" title="Tipo Trattamento" multiple="false" 
								required="true" focusOn="false" moduleName="ComboTipoTrattamentoTemplate"
								classNameBase="input" disabled="<%=String.valueOf(!canModify)%>"
								onChange="fieldChanged()"
								selectedValue="<%=CODTIPOTRATTAMENTO%>" addBlank="true"
								blankValue="" /></td>
					</tr>
<!-- 					<tr> -->
<!-- 						<td class="etichetta">Predisposto</td> -->
<!-- 						<td class="campo"> -->
<%-- 							<input type="checkbox" name="flgPredisposto" value="1" <%if(FLGPREDISPOSTO.equals("1")) { out.print("CHECKED"); }%>> --%>
<!-- 		  				</td> -->
<!-- 					</tr> -->
					
					<tr>
						<td class="etichetta">Codice di classificazione</td>
						<td class="campo"><af:comboBox name="prgTitolario"
								size="1" title="Titolario di classificazione" multiple="false" 
								required="false" focusOn="false" moduleName="ComboTitolarioClassificazioneTemplate"
								classNameBase="input" disabled="<%=String.valueOf(!canModify)%>"
								onChange="fieldChanged()"
								selectedValue="<%=PRGTITOLARIO%>" addBlank="true"
								blankValue="" /></td>
					</tr>
			
					<tr>
						<td class="etichetta">Firma grafometrica</td>
						<td class="campo">
							<input type="checkbox" name="flgFirmaGrafo" value="1" <%if(FLGFIRMAGRAFO.equals("1")) { out.print("CHECKED"); }%>>
						</td>
		  			</tr>
		  			<%--
		  			<tr>
		  				</td>
		  				<td class="etichetta">Trasmissione interna</td>
		  				<td class="campo">
		  					<input type="checkbox" name="flgTrasmissioneInterna" value="1" <%if(FLGTRASMISSIONEINTERNA.equals("1")) { out.print("CHECKED"); }%>>
		  				</td>
					</tr>
					 --%>
					 <input type="hidden" name="flgTrasmissioneInterna" value="<%if(FLGTRASMISSIONEINTERNA.equals("1")) { out.print("1"); } else { out.print("0"); }%>" />
				</table>
				</div>
			</td>
			</tr>
			
			<%if (!_page.equalsIgnoreCase("InsTemplatePage") && !duplicaTemplate.equalsIgnoreCase("S") && !isErrorInsTemplate) {%>
			
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
						<af:list moduleName="MListaAllegatiTemplate" skipNavigationButton="1" jsSelect="dettaglioAllegato" />
						</td>
						</tr>
						<%if (canAggiungiAll) {%>
						<tr>
						<td colspan="2">
						<input type="button" class="pulsanti" name="btnAddAllegati"
						value="Aggiungi allegati" onclick="apriGestioneAllegati();">
						</td>
						</tr>
						<%}%>
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
				<%if (canModify) {%> 
					<!-- salva i dati generali del template -->
					<input type="submit" class="pulsanti" name="SALVA" value="<%=btnSalvaDatiGeneraliTemplate%>">
					<!-- richiama l'editor -->
					<%if (!_page.equalsIgnoreCase("InsTemplatePage") && !duplicaTemplate.equalsIgnoreCase("S") && !isErrorInsTemplate) {%>
						<input type="submit" class="pulsanti" name="MODIFICA"
						value="<%=btnModificaEditorTemplate%>" onclick="UpperCaseCodice(document.frmTemplate.STRNOME) &ctrPage();">
					<%}
 				}%>
				</td>
				<td align="center"><input type="button" class="pulsanti" name="ANNULLA" value="<%=btnAnnulla%>" onClick="annulla();">
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
</body>
</html>