<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                   
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.text.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String pageRetLista = "MOVLISTAVALIDAZIONEPAGE";
String token = "_TOKEN_" + pageRetLista;
String urlScript = "";
String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
if (urlDiLista!=null) {
	urlScript = "goTo('" + urlDiLista + "');";
}
else {
	urlScript = "goTo('PAGE=" + pageRetLista + "');";
}
%>
<html>
<head>
<af:linkScript path="../../js/"/>
<script language="Javascript">
	var locationLista = <%=urlScript%>;
	locationLista;
</script>
</head>
<body>
</body>
</html>