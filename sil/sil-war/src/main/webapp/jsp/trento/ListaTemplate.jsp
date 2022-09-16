<%@ page contentType="text/html;charset=utf-8"%>
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
	
	String pageToProfile = "RicercaTemplatePage";
	ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
	PageAttribs attributi = new PageAttribs(user, pageToProfile);

	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	SourceBean serviziRows = (SourceBean) serviceResponse
			.getAttribute("MListaTemplate");

	boolean canNuovo = false;
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	else {
		canNuovo = attributi.containsButton("NUOVO_TEMPLATE");
	}

	boolean canOrdina = false;
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	else {
		canOrdina = attributi.containsButton("ORDINA_TEMPLATE");
	}	
	
	String listPage = "1";
	if (serviceRequest.containsAttribute("LIST_PAGE")) {
		listPage = (String) serviceRequest.getAttribute("LIST_PAGE");
	}
	
	String codAzione = "";
	String txtOut = "";
	
	String STRNOME = (String) serviceRequest.getAttribute("STRNOME");
	String CODTIPODOMINIO = (String) serviceRequest
			.getAttribute("CODTIPODOMINIO");
	String CODAMBITOTEM = (String) serviceRequest
			.getAttribute("CODAMBITOTEM");
	String DATINIZIOVAL = (String) serviceRequest
			.getAttribute("DATINIZIOVAL");
	String DATFINEVAL = (String) serviceRequest
			.getAttribute("DATFINEVAL");
	String DESCCODAMBITOTEM = (String) serviceRequest
			.getAttribute("DESCCODAMBITOTEM");
	String DESCDOMINIO = (String) serviceRequest
			.getAttribute("DESCDOMINIO");
	String PRGTEMPLATESTAMPA = (String) serviceRequest
			.getAttribute("PRGTEMPLATESTAMPA");
	String nomeClassif = (String) serviceRequest.getAttribute("nomeClassif");
	
	String PRGCLASSIF = (String) serviceRequest.getAttribute("PRGCLASSIF");

	sessionContainer.delAttribute("EDITORCACHE");

	String[] fields = {"STRNOME", "CODTIPODOMINIO", "CODAMBITOTEM",
			"DATINIZIOVAL", "DATFINEVAL", "PRGTEMPLATESTAMPA",
			"DESCCODAMBITOTEM", "DESCDOMINIO", "prgClassif", "nomeClassif"};

	NavigationCache formRicercaTemplate = null;
	if (sessionContainer.getAttribute("TEMPLATECACHE") != null) {
		formRicercaTemplate = (NavigationCache) sessionContainer
				.getAttribute("TEMPLATECACHE");
		
		if (formRicercaTemplate.getField("STRNOME") != null){
			STRNOME = formRicercaTemplate.getField("STRNOME").toString();
		}
		if (formRicercaTemplate.getField("CODTIPODOMINIO") != null){
			CODTIPODOMINIO = formRicercaTemplate.getField("CODTIPODOMINIO").toString();
		}
		if (formRicercaTemplate.getField("CODAMBITOTEM") != null){
			CODAMBITOTEM = formRicercaTemplate.getField("CODAMBITOTEM").toString();
		}
		if (formRicercaTemplate.getField("DATINIZIOVAL") != null){
			DATINIZIOVAL = formRicercaTemplate.getField("DATINIZIOVAL").toString();
		}
		if (formRicercaTemplate.getField("DATFINEVAL") != null){
			DATFINEVAL = formRicercaTemplate.getField("DATFINEVAL").toString();
		}
		if (formRicercaTemplate.getField("DESCCODAMBITOTEM") != null){
			DESCCODAMBITOTEM = formRicercaTemplate.getField("DESCCODAMBITOTEM").toString();
		}
		if (formRicercaTemplate.getField("DESCDOMINIO") != null){
			DESCDOMINIO = formRicercaTemplate.getField("DESCDOMINIO").toString();
		}
		if (formRicercaTemplate.getField("prgClassif") != null){
			PRGCLASSIF= formRicercaTemplate.getField("prgClassif").toString();
		}
		if (formRicercaTemplate.getField("nomeClassif") != null){
			nomeClassif= formRicercaTemplate.getField("nomeClassif").toString();
		}

	} else {
		//salvo in cache
		formRicercaTemplate = new NavigationCache(fields);
		formRicercaTemplate.enable();
		for (int i = 0; i < fields.length; i++)
			formRicercaTemplate.setField(fields[i],
					(String) serviceRequest.getAttribute(fields[i]));
		sessionContainer.setAttribute("TEMPLATECACHE",
				formRicercaTemplate);
	}

	if (STRNOME != null && !STRNOME.equals("") && !STRNOME.equals("null")) {
		txtOut += "template <strong>" + STRNOME + "</strong>; ";
	}
	if (DESCCODAMBITOTEM != null && !DESCCODAMBITOTEM.equals("") && !DESCCODAMBITOTEM.equals("null")) {
		txtOut += "ambito template <strong>" + DESCCODAMBITOTEM
				+ "</strong>; ";
	}
	if (DESCDOMINIO != null && !DESCDOMINIO.equals("") && !DESCDOMINIO.equals("null")) {
		txtOut += "dominio dati <strong>" + DESCDOMINIO + "</strong>; ";
	}
	if (DATINIZIOVAL != null && !DATINIZIOVAL.equals("") && !DATINIZIOVAL.equals("null")) {
		txtOut += "data inizio validita' <strong>" + DATINIZIOVAL
				+ "</strong>; ";
	}
	if (DATFINEVAL != null && !DATFINEVAL.equals("") && !DATFINEVAL.equals("null")) {
		txtOut += "data fine validita' <strong>" + DATFINEVAL
				+ "</strong>; ";
	}
	if (nomeClassif != null && !nomeClassif.equalsIgnoreCase("") && !nomeClassif.equals("null")) {
		txtOut += "classificazione <strong>" + nomeClassif
				+ "</strong>; ";
	}
%>

<html>
<head>
<title>Lista Template</title>
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
	  	<af:showMessages prefix="MDeleteTemplate"/>
	  	<af:showMessages prefix="MOrdinaTemplate"/>
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

	<af:list moduleName="MListaTemplate" />
	<center>
		<af:form method="POST" action="AdapterHTTP" dontValidate="true">
			<input type="hidden" name="PAGE" value="InsTemplatePage" />
			<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>" />
			<input type="hidden" name="LIST_PAGE" value="<%=listPage%>" />
			<input type="hidden" name="PRGCLASSIF" value="<%=PRGCLASSIF%>" />
			<input type="hidden" name="CODTIPODOMINIO" value="<%=CODTIPODOMINIO%>" />

			<%
				if (canNuovo) {
			%>
			<input class="pulsante" type="submit" name="inserisci"
				value="Nuovo template" onClick="" />
			<%
				}
			%>

			

			<input type="button" class="pulsanti" name="torna"
				value="Torna alla ricerca"
				onclick="go('PAGE=RicercaTemplatePage&cdnFunzione=<%=cdnFunzione%>&codAmbitoTem=<%=CODAMBITOTEM%>&codTipoDominio=<%=CODTIPODOMINIO%>&STRNOME=<%=STRNOME%>&DATINIZIOVAL=<%=DATINIZIOVAL%>&DATFINEVAL=<%=DATFINEVAL%>')">
				
			<BR /><BR />
			
			<%
				if (canOrdina) {
			%>
			<input class="pulsante" type="button" name="ordina"
				value="Ordina template" onClick="go('PAGE=OrdinaTemplatePage&cdnFunzione=<%=cdnFunzione%>&codAmbitoTem=<%=CODAMBITOTEM%>&codTipoDominio=<%=CODTIPODOMINIO%>&STRNOME=<%=STRNOME%>&DATINIZIOVAL=<%=DATINIZIOVAL%>&DATFINEVAL=<%=DATFINEVAL%>')" />
			<%
				}
			%>
		</af:form>
	</center>

</body>
</html>