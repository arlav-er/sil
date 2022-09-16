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
  String fromWhere = (String) serviceRequest.getAttribute("fromWhere");
%>

<html>
<head>
  <title>Lista Lavoratore</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/" />
</head>

<body class="gestione">
<af:form dontValidate="true">
<% if (fromWhere.equals("ricerca")) {%>
	<af:list moduleName="M_GetLavIscrCMfromRic" />
<% } else if (fromWhere.equals("dettaglio")) {%>
	<af:list moduleName="M_GetLavIscrCMfromDet" />
<% }%>
	<center>
    	<input type="button" class="pulsanti" value="Chiudi" onClick="window.close()" />
	</center>
	
</af:form>
</body>
</html>
