<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                 it.eng.sil.util.*,
                 it.eng.sil.security.User,
                 java.math.* " %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
SourceBean dati = (SourceBean) serviceResponse.getAttribute("M_GetDocAllegatoMBO.ROWS.ROW");
BigDecimal prgDocumento = null;
if (dati != null) {
	prgDocumento =  (BigDecimal) dati.getAttribute("PRGDOCUMENTO");	
}
%>
<html>
<head>
<title>Visualizza allegato</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">

<af:linkScript path="../../js/"/>
<script language="JavaScript">
	function visualizzaAllegato() {
		<%if (prgDocumento != null) {%>
		  	var urlDoc = "AdapterHTTP?";
		  	urlDoc += "PAGE=REPORTFRAMEPAGE";
		  	urlDoc += "&ACTION_REDIRECT=DOWNLOAD";
		  	urlDoc += "&prgDocumento=<%=prgDocumento.toString()%>";
		  	urlDoc +="&apriFileBlob=true";
		  	document.location=urlDoc;
		<%}%>
	}
</script>

</head>

<body class="gestione" onload="visualizzaAllegato();">
<%
if (prgDocumento == null) {
%>
	<%out.print(htmlStreamTop);%>
	<table align="center" width="100%" border="0">
	<tr><td align="center"><b>Non esistono allegati associati alla domanda</b></td></tr>
	</table>
<%}%>
<af:showErrors/>
</body>
</html>
