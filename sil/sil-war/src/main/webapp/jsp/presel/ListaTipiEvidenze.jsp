<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ taglib uri="aftags" prefix="af"%>

<%@ page
	import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                java.text.*, java.util.*,it.eng.sil.util.*, 
                it.eng.afExt.utils.*,
                java.math.*, it.eng.sil.security.* "%>


<%@ taglib uri="aftags" prefix="af"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	String _page = StringUtils.getAttributeStrNotNull(serviceRequest, "PAGE");
	String _cdnFunzione = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNFUNZIONE");
	boolean canInsert = false;
	boolean canDelete = false;
	boolean canModify = false;
	PageAttribs attributi = new PageAttribs(user, _page);

	canInsert = attributi.containsButton("INSERISCI");
	canDelete = attributi.containsButton("CANCELLA");
	canModify = attributi.containsButton("AGGIORNA");

	String msg = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
	String lp = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");
%>

<html>
<head>
<title>Tipi Evidenze</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
</head>
<body class="gestione" onLoad="rinfresca();">

	<p>
		<af:showErrors />
		<af:showMessages prefix="MSalvaTipoEvidenza" />
		<af:showMessages prefix="MCancTipoEvidenza" />
	</p>

	<p class="titolo">Lista Tipi Evidenze</p>

	<af:list moduleName="MListaTipiEvidenze"
		canDelete="<%=canDelete ? \"1\" : \"0\"%>" />

	<af:form method="POST" name="frm" action="AdapterHTTP"
		dontValidate="true">
		<div align="center">

			<input type="hidden" name="PAGE" value="NuovoTipoEvidenzaPage" /> <input
				type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunzione%>" /> <input
				type="hidden" name="MSG" value="<%=msg%>" /> <input type="hidden"
				name="LP" value="<%=lp%>" />

			<%
				if (canInsert) {
			%>
			<input class="pulsanti" type="submit" name="inserisci"
				value="Nuovo tipo evidenza" />
			<%
				}
			%>
		
	</af:form>
	</div>

</body>
</html>