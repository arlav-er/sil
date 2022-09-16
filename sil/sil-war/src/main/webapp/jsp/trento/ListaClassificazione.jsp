<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ taglib uri="aftags" prefix="af"%>

<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.configuration.ConfigSingleton,com.engiweb.framework.error.EMFErrorHandler,
    it.eng.afExt.utils.DateUtils, it.eng.sil.security.User, it.eng.afExt.utils.*,it.eng.sil.util.*, java.lang.*,
    java.text.*, java.math.*,  java.sql.*,   oracle.sql.*,  java.util.*, it.eng.sil.security.*"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%
	String _page = (String) serviceRequest.getAttribute("PAGE");
	
	String pageToProfile = "RicercaClassificazionePage";
	ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
	PageAttribs attributi = new PageAttribs(user, pageToProfile);

	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	BigDecimal bdNUMKLOCLASSIF = null;
	String NUMKLOCLASSIF = "";

	boolean canNuovo = false;
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	else {
		canNuovo = attributi.containsButton("NUOVA_CLASSIFICAZIONE");
	}

	boolean canOrdina = false;
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	else {
		canOrdina = attributi.containsButton("ORDINA_CLASSIFICAZIONE");
	}	
	
	// TODO: da levare quando verranno lanciati gli script
	//canNuovo = true;
	//canOrdina = true;
	
	if (serviceRequest.containsAttribute("NUMKLOCLASSIF")){
		NUMKLOCLASSIF = (String) serviceRequest.getAttribute("NUMKLOCLASSIF");
		if (!StringUtils.isEmpty(NUMKLOCLASSIF) && !NUMKLOCLASSIF.toLowerCase().equalsIgnoreCase("null")){
			bdNUMKLOCLASSIF = new BigDecimal(NUMKLOCLASSIF);
			bdNUMKLOCLASSIF = bdNUMKLOCLASSIF.add(new BigDecimal(1));
			NUMKLOCLASSIF = bdNUMKLOCLASSIF.toString();
		}
	}
	
	String txtOut = "";
	
	String STRNOME = (String) serviceRequest.getAttribute("STRNOME");
	String prgTipoDominio = (String) serviceRequest.getAttribute("prgTipoDominio");
	String DESCDOMINIO = (String) serviceRequest.getAttribute("DESCDOMINIO");
	String PRGCLASSIF = (String) serviceRequest.getAttribute("PRGCLASSIF");

	String[] fields = {"STRNOME", "prgTipoDominio", "PRGCLASSIF"};

	NavigationCache formRicercaClassificazione = null;
	if (sessionContainer.getAttribute("CLASSIFICAZIONECACHE") != null) {
		formRicercaClassificazione = (NavigationCache) sessionContainer.getAttribute("CLASSIFICAZIONECACHE");
		STRNOME = formRicercaClassificazione.getField("STRNOME").toString();
		prgTipoDominio = formRicercaClassificazione.getField("prgTipoDominio").toString();
	} else {
		//salvo in cache
		formRicercaClassificazione = new NavigationCache(fields);
		formRicercaClassificazione.enable();
		for (int i = 0; i < fields.length; i++)
			formRicercaClassificazione.setField(fields[i], (String) serviceRequest.getAttribute(fields[i]));
		sessionContainer.setAttribute("CLASSIFICAZIONECACHE",formRicercaClassificazione);
	}

	if (STRNOME != null && !STRNOME.equals("")) {
		txtOut += "Classificazione <strong>" + STRNOME + "</strong>; ";
	}
	
	if (DESCDOMINIO != null && !DESCDOMINIO.equals("")) {
		txtOut += "dominio dati <strong>" + DESCDOMINIO + "</strong>; ";
	}

%>

<html>
<head>
<title>Lista Classificazione</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
<script type="text/javascript">
	function go(url, alertFlag) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit())
			return;

		var _url = "AdapterHTTP?" + url;
		if (alertFlag == 'TRUE') {
			if (confirm('Confermi operazione')) {
				setWindowLocation(_url);
			}
		} else {
			setWindowLocation(_url);
		}
	}
</script>
</head>
<body class="gestione">
	<font color="red">
  		<af:showErrors/>
	</font>
	<font color="green">
	  	<af:showMessages prefix="MDeleteClassificazione"/>
	  	<af:showMessages prefix="MAggiornaClassificazione"/>
	</font>

	<p align="center">
		<%
			txtOut = "Filtri di ricerca:<br/> " + txtOut;
		%>
	
	<table cellpadding="2" cellspacing="10" border="0" width="100%">
		<tr>
			<td
				style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color: #dcdcdc">
				<%
					out.print(txtOut);
				%>
			</td>
		</tr>
	</table>
	<%%>

	<af:list moduleName="MListaClassificazione" />
	
	<center>
		<af:form method="POST" action="AdapterHTTP" dontValidate="true">
			<input type="hidden" name="PAGE" value="InsClassificazionePage" />
			<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>" />
			<input type="hidden" name="NUMKLOCLASSIF" value="<%=NUMKLOCLASSIF%>">
			<input type="hidden" name="DESCDOMINIO" value="<%=DESCDOMINIO%>" />
			<input type="hidden" name="prgTipoDominio" value="<%=prgTipoDominio%>" />

			<%
				if (canNuovo) {
			%>
			<input class="pulsante" type="submit" name="inserisci"
				value="Nuova Classificazione" onClick="" />
			<%
				}
			%>

			

			<input type="button" class="pulsanti" name="torna"
				value="Torna alla ricerca"
				onclick="go('PAGE=RicercaClassificazionePage&cdnFunzione=<%=cdnFunzione%>&prgTipoDominio=<%=prgTipoDominio%>&STRNOME=<%=STRNOME%>')">
				
			<BR /><BR />
			
			<%
				if (canOrdina) {
			%>
			<input class="pulsante" type="button" name="ordina"
				value="Ordina Classificazione" onClick="go('PAGE=OrdinaClassificazionePage&cdnFunzione=<%=cdnFunzione%>&prgTipoDominio=<%=prgTipoDominio%>&STRNOME=<%=STRNOME%>&DESCDOMINIO=<%=DESCDOMINIO%>')" />
			<%
				}
			%>
		</af:form>
	</center>

</body>
</html>