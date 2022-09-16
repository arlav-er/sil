<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.User,
  com.engiweb.framework.security.*" %>
  
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<title>Colloqui Lavoratore</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />
</head>
<body>
<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
SourceBean contColloqui = (SourceBean) serviceResponse.getAttribute("M_ListaColloquiLav");
Vector rows_VectorColloqui = null;
rows_VectorColloqui = contColloqui.getAttributeAsVector("ROWS.ROW");
if (rows_VectorColloqui.size() > 0) {
%>
  <af:list moduleName="M_ListaColloquiLav" skipNavigationButton="1"/>
<%
}
else {
%>
  <br>
  <%out.print(htmlStreamTop);%>
  <p align="center">
  <table class="lista" align="center">
  <tr><td align="center"><b>Nessun colloquio per il lavoratore</b></td></tr>
  </table></p>
<%
  out.print(htmlStreamBottom);
}
%>
<center>
<table><tr><td align="center">
<input type="button" class="pulsanti" name="buttChiudi" value="Chiudi" onClick="javascript:window.close();">
</td></tr></table>
</center>
</body>
</html>