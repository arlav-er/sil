<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
 
<%@ page import="
                  com.engiweb.framework.base.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
//Genero il token ed il pulsante di ritorno alla lista
String pageRetLista = StringUtils.getAttributeStrNotNull(serviceRequest, "PAGERITORNOLISTA");
String token = "_TOKEN_" + pageRetLista;
String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
%>
<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>
<script type="text/javascript">
var queryStringUrl = '<%=urlDiLista%>';  
</script>
</head>
<body class="gestione">
</body>

<script type="text/javascript">
	goTo(queryStringUrl);
</script>
</html>