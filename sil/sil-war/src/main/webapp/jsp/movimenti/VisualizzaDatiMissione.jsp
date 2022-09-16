<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.module.movimenti.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
	String nomeCognome = StringUtils.getAttributeStrNotNull(serviceRequest, "STRNOMECOGNOME");
	String codiceFisc = StringUtils.getAttributeStrNotNull(serviceRequest, "CF");
%>

<html>
<head>
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
	
	
	<af:linkScript path="../../js/"/>
	<title>Lista Missioni</title>
	<script language="Javascript">
		
	</script>
</head>

<body class="gestione">
<p></p>
<p class="titolo">Lista Missioni di <%=nomeCognome%> - CF : <%=codiceFisc%></p>

<p align="center">
	<CENTER>
		<af:list moduleName="Mov_ListaMissione" skipNavigationButton="1" />	
	  	<input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
	</CENTER> 
</body>  