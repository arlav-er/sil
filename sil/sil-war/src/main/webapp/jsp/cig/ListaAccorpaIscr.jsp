<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
  
  String aggFunz = (String) serviceRequest.getAttribute("AGG_FUNZ");
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  String soggetto = (String) serviceRequest.getAttribute("MOV_SOGG");
  String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "prgAzienda");
  String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "prgUnita");
  
%>


<html>
<head>
  <title>Lista Iscrizioni</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/" />
  
  <script language="JavaScript">
 
  </script>
  
</head>
<body class="gestione">
<af:form dontValidate="true">
<af:list moduleName="M_AccorpaIscrizione" />

<table class="main">
<tr>
  <td align="center">
	<input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
  </td>
</tr>
</table>

</af:form>
</body>
</html>