<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.error.*,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String moduleName = "M_EsportaMigrVisStorico";
	SourceBean moduleResponse = (SourceBean) serviceResponse.getAttribute(moduleName);
%>
<html>
<head>
	<title>Storico Esportazione Migrazioni</title>
	<link rel="stylesheet" href="../../css/stili.css" type="text/css" />
	<link rel="stylesheet" href="../../css/listdetail.css" type="text/css" />
	<af:linkScript path="../../js/"/>
	<script language="JavaScript" src="../../js/lookup.js"></script>
	<script language="JavaScript">
		centraFinestra (700, 540);
	</script>
</head>

<body class="gestione">

<af:showErrors/>
<af:showMessages prefix="<%= moduleName %>"/>

<af:form name="formina" method="POST" action="AdapterHTTP" dontValidate="true">
<input type="hidden" name="PAGE" value="EsportaMigrazioniPage" />
<input type="hidden" name="<%= StepByStepConst.PARAM_COMANDO %>" value="<%= StepByStepConst.COMANDO_INFO %>" />

<table class="main">
	<tr>
		<td>
			<af:list moduleName="<%= moduleName %>" />
		</td>
	</tr>
	<tr>
		<td>
			<span class="bottoni">
				<input type="button" class="pulsanti" value="Chiudi" name="chiudi"
				       onClick="javascript:window.close()" />
			</span>
		</td>
	</tr>
</table>

</af:form>

</body>
</html>