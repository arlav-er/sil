<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="/jsp/global/noCaching.inc"%>
<%@ include file="/jsp/global/getCommonObjects.inc"%>
<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.configuration.ConfigSingleton,it.eng.sil.util.*,it.eng.afExt.utils.StringUtils,it.eng.sil.security.ProfileDataFilter,java.text.*,java.util.*,java.math.*,it.eng.sil.security.*"%>
<%--

--%>
<html>
<head>
<title>Registro Protocollo Giornaliero</title>
<link rel="stylesheet"
	href="<%=request.getContextPath() %>/css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath() %>/css/listdetail.css" />
<%@ include file="/jsp/global/checkFormatData.inc"%>
<af:linkScript path='<%=request.getContextPath()+"/js/" %>' />
<script language="JavaScript" type="text/javascript"
	src="<%=request.getContextPath() %>/js/script_comuni.js"></script>
<%--
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
--%>
</head>
<body class="gestione" onload="rinfresca();">
<%--
<af:showMessages prefix="M_DELETE_AM_OBBLIGO_ISTRUZIONE" />
<p class="titolo">Registro Protocollo Giornaliero</p>
--%>
<af:showMessages prefix="M_GetReportProtocolloGiornaliero" />
<af:showErrors />

<p align="center"><af:list moduleName="M_GetReportProtocolloGiornaliero" /></p>
<br />
<af:form name="dettCondizione" method="POST" action="AdapterHTTP">
	<center><input type="hidden" name="PAGE"
		value="RegistroProtocolloGiornalieroMainPage"> <input
		type="submit" class="pulsanti" value="Nuovo Report" /></center>
</af:form>
<br />
<%--
	out.print(htmlStreamTop);
skipNavigationButton="1"
	out.print(htmlStreamBottom);
--%>
</body>
</html>