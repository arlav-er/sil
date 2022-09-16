<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                 
                com.engiweb.framework.security.*,
                it.eng.afExt.utils.*, java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.*,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp"
%>

<%@ taglib uri="cal" prefix="cal" %>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 
  String prgSlot=serviceRequest.getAttribute("PRGSLOT").toString();
  String codcpi=serviceRequest.getAttribute("CODCPI").toString();
%>

<html>
<head>
  <title>Lista Operatori</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 <af:linkScript path="../../js/" />
</head>

<body class="gestione">
  <af:list moduleName="MListaAgOperatori"/>
<table class="main" align="center">
<tr><td>&nbsp;</td></tr>
<tr>
  <td align="center">
    <input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
  </td>
</tr>
</table>

</body>
</html>
