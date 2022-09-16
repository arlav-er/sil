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
	
	String pageToProfile = "RicercaClassificazionePage";
	ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	String cdnUtins = "";
	String cdnUtmod = "";
	String dtmins = "";
	String dtmmod = "";
	
	String prgTipoDominio  = StringUtils.getAttributeStrNotNull(serviceRequest,"prgTipoDominio");
	    
	if (StringUtils.isEmpty(prgTipoDominio)){
		if(serviceResponse.containsAttribute("ComboPrgTipoDominio")){
			Vector listeDominioDati = serviceResponse.getAttributeAsVector("ComboPrgTipoDominio.ROWS.ROW");
		    SourceBean dominioDato  = (SourceBean) listeDominioDati.elementAt(0);
		    String codiceDato = (String) dominioDato.getAttribute("CODICE");
		    prgTipoDominio = codiceDato;
		}
	}

	BigDecimal NUMKLOCLASSIF=null;
	String STRNOME = "";
	String DESCRIZIONE = "";
	String DESCDOMINIO = "";
	String PRGCLASSIF = "";
	
	String moduleName = "";
	String pageName = "GestClassificazionePage";
	String btnSalvaDatiGeneraliClassificazione = "";

	DESCDOMINIO = (String) serviceRequest.getAttribute("DESCDOMINIO");

	SourceBean classificazioneRow = null;
	if ("AggClassificazionePage".equals(_page)){
		
		btnSalvaDatiGeneraliClassificazione = "Aggiorna";
		moduleName = "MAggiornaClassificazione";
		
		classificazioneRow = (SourceBean) serviceResponse.getAttribute("MDETTAGLIOCLASSIFICAZIONE.ROWS.ROW");
		if (classificazioneRow != null) {
			STRNOME = classificazioneRow.containsAttribute("STRNOME") ? classificazioneRow.getAttribute("STRNOME").toString() : "";
			DESCRIZIONE = classificazioneRow.containsAttribute("DESCRIZIONE") ? classificazioneRow.getAttribute("DESCRIZIONE").toString() : "";
			PRGCLASSIF = classificazioneRow.containsAttribute("PRGCLASSIF") ? classificazioneRow.getAttribute("PRGCLASSIF").toString() : "";
			NUMKLOCLASSIF = classificazioneRow.containsAttribute("NUMKLOCLASSIF") ? (BigDecimal) classificazioneRow.getAttribute("NUMKLOCLASSIF") : new BigDecimal(0);
		}
		
	} else if ("InsClassificazionePage".equals(_page)) {
		
		btnSalvaDatiGeneraliClassificazione = "Inserisci";
		moduleName = "MInsClassificazione";

		//VERIFICO SE CI SONO DATI SALVATI IN CACHE
		NavigationCache formInserimentoClassificazione = null;
		if (sessionContainer.getAttribute("EDITORCACHE") != null) {
			formInserimentoClassificazione = (NavigationCache) sessionContainer.getAttribute("EDITORCACHE");
			STRNOME = formInserimentoClassificazione.getField("STRNOME").toString();
			prgTipoDominio = formInserimentoClassificazione.getField("prgTipoDominio").toString();
			DESCRIZIONE = formInserimentoClassificazione.getField("DESCRIZIONE").toString();
		}
	}
		  
	String btnAnnulla = "Torna alla lista";
	String goBackUrl = (String) sessionContainer.getAttribute("_TOKEN_GESTCLASSIFICAZIONEPAGE");

	operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
	PageAttribs attributi = new PageAttribs(user, pageToProfile);
	boolean canModify = attributi.containsButton("SALVA_CLASSIFICAZIONE");
	//boolean canModify = true; //X TEST
	if (canModify) {
		btnAnnulla = "Chiudi";
	}
	
	//canModify = true; // TODO: da levare quando verranno lanciati gli script
	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

	%>

<head>
  <%@ include file="SezioniATendina.inc" %>
  <%@ include file="../global/fieldChanged.inc" %>
  
  <script type="text/javascript">
 	
 	function valorizzaHidden() {
 		
 		var prgTipoDominioSelect = eval('document.frmClassificazione.prgTipoDominio');
 		var dominioSelezionato = prgTipoDominioSelect.options[prgTipoDominioSelect.selectedIndex].value;
 		var dominioSceltoPrecedentemente = "<%=prgTipoDominio%>";
 		
 		if ( (dominioSelezionato != null && dominioSelezionato != '') && (dominioSceltoPrecedentemente != null && dominioSceltoPrecedentemente != '') ){
	 		if (dominioSelezionato != dominioSceltoPrecedentemente){
	 			if (confirm("Modificando il Dominio Dati il nuovo valore sarà trasmesso anche ad eventuali Template associati.\n \nProcedere lo stesso ?")) {
	 				return true;
	 			} else{
	 				return false;
	 			}
	 		}
 		}
 		
 	  	return true;
 	}
  	
	function annulla() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		if (flagChanged == true) {
			if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) {
				return;
			} 
		}
		var url = "AdapterHTTP?";
		//alert('url: ' + url + "<%=StringUtils.formatValue4Javascript(goBackUrl) %>");
	    setWindowLocation(url + "<%=StringUtils.formatValue4Javascript(goBackUrl) %>");
	}	
		
  </script>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Dettaglio Classificazione</title>
</head>
<body>
	<font color="red">
  		<af:showErrors/>
	</font>
	<font color="green">
	  	<af:showMessages prefix="MSalvaClassificazione"/>
	  	<af:showMessages prefix="MAggiornaClassificazione"/>
	  	<af:showMessages prefix="MAggiornaInsClassificazione"/>
	</font>

	<p class="titolo">
		<%if ( ("InsClassificazionePage".equals(_page)) || ("DettaglioClassificazionePage".equals(_page)) ) {%>
			Inserisci Classificazione
		<%} else {%>
			Visualizza Classificazione
		<%}%>
	</p>
	<%
		out.print(htmlStreamTop);
	%>

	<af:form name="frmClassificazione" action="AdapterHTTP" method="POST" onSubmit="valorizzaHidden()">
		<input type="hidden" id="page" name="PAGE" value="<%=pageName%>">
		<input type="hidden" id="OPERAZIONE" name="OPERAZIONE" value="<%=btnSalvaDatiGeneraliClassificazione%>">
		<input type="hidden" id="module" name="MODULE" value="<%=moduleName%>">
		<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
		<input type="hidden" name="PRGCLASSIF" value="<%=PRGCLASSIF%>">
		<input type="hidden" name="NUMKLOCLASSIF" value="<%=Utils.notNull(NUMKLOCLASSIF)%>">
		<input type="hidden" name="DESCDOMINIO" value="<%=DESCDOMINIO%>" />
		
		<p align="center">
		<table class="main">
			<tr>
				<td class="etichetta">Nome Classificazione</td>
				<td class="campo"><af:textBox type="text" 
						name="STRNOME"
						title="Nome Classificazione" required="true" size="50" value="<%=STRNOME%>"
						onKeyUp="fieldChanged();" classNameBase="input"
						readonly="<%=String.valueOf(!canModify)%>" maxlength="100" /></td>
			</tr>

<%
String disable_dominio = "false";
String required = "false";

if ( ("InsClassificazionePage".equals(_page)) || ("DettaglioClassificazionePage".equals(_page)) ) {
			disable_dominio="false";
			required="true";
		}
		%>
			<tr>
				<td class="etichetta">Dominio dati</td>
				<td class="campo"><af:comboBox name="prgTipoDominio"
						size="1" title="Dominio dati" multiple="false" 
						 focusOn="false" moduleName="ComboPrgTipoDominio"
						classNameBase="input" 
						disabled="<%=disable_dominio%>"
						required="<%=required%>"
						onChange="fieldChanged()"
						selectedValue="<%=prgTipoDominio%>" /></td>
			</tr>
			
			<tr>
				<td class="etichetta">Descrizione</td>
				  
				<td class="campo">
					<af:textArea name="descrizione" cols="60" rows="4" title="DESCRIZIONE" maxlength="200" readonly="<%=String.valueOf(!canModify)%>" classNameBase="input"
					value="<%=DESCRIZIONE%>" />
				</td>
			</tr>
		        	<tr>
        	<td class="campo" colspan="2">
        	<br /><br />
        	</td>
        	</tr>
			
		</table>
		
		<br>
		<table>
			<tr>
				<td align="center">
				<%if (canModify) {%> 
					<!-- salva i dati generali del template -->
					<input type="submit" class="pulsanti" name="SALVA" value="<%=btnSalvaDatiGeneraliClassificazione%>">
 				<%}%>
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