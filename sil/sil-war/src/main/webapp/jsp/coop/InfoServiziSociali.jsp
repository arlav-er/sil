<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                java.text.*, java.util.*,it.eng.sil.util.*,                 
                it.eng.sil.Values,
                it.eng.afExt.utils.*,
                java.math.*, 
                it.eng.sil.security.* "%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%! 
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("it.eng.sil._jsp.CaricaDatiPersonali.jsp");
%>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
  SourceBean resOsserv = (SourceBean)serviceResponse.getAttribute("M_GET_INFO_SERVIZI_SOCIALI_OSSERVATORIO");
  SourceBean resGarsia = (SourceBean)serviceResponse.getAttribute("M_GET_INFO_SERVIZI_SOCIALI_GARSIA");
  SourceBean resGradus = (SourceBean)serviceResponse.getAttribute("M_GET_INFO_SERVIZI_SOCIALI_GRADUS");
  SourceBean resSosia = (SourceBean)serviceResponse.getAttribute("M_GET_INFO_SERVIZI_SOCIALI_SOSIA");
%>

<html>
<head>
<title>Info servizi sociali</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<af:linkScript path="../../js/"/>

</head>

<body class="gestione" onload="">
<br/>
<ul>
	<% 
		if(resOsserv.containsAttribute("ERRNUM")){
	%>
	<li>Richieste : <%=(String)resOsserv.getAttribute("ERRDESCRIPTION") %>
	<%} %>
	<% 
		if(resGarsia.containsAttribute("ERRNUM")){
	%>
	<li>Serivzi per Anziani : <%=(String)resGarsia.getAttribute("ERRDESCRIPTION") %>
	<%} %>
	<% 
		if(resGradus.containsAttribute("ERRNUM")){
	%>
	<li>Graduatorie : <%=(String)resGradus.getAttribute("ERRDESCRIPTION") %>
	<%} %>
	<% 
		if(resSosia.containsAttribute("ERRNUM")){
	%>
	<li>Servizi Individuali : <%=(String)resSosia.getAttribute("ERRDESCRIPTION") %>
	<%} %>
</ul>

<center>
	<div class="sezione2">Dati Richieste</div>
	<af:list moduleName="M_GET_INFO_SERVIZI_SOCIALI_OSSERVATORIO" skipNavigationButton="1"/>
	<div class="sezione2">Dati Servizi per Anziani</div>
	<af:list moduleName="M_GET_INFO_SERVIZI_SOCIALI_GARSIA" skipNavigationButton="1"/>
	<div class="sezione2">Dati Servizi Individuali</div>
	<af:list moduleName="M_GET_INFO_SERVIZI_SOCIALI_SOSIA" skipNavigationButton="1"/>
	<div class="sezione2">Dati Graduatorie</div>
	<af:list moduleName="M_GET_INFO_SERVIZI_SOCIALI_GRADUS" skipNavigationButton="1"/>    
</center>
<div align="center">
	<input type="button" value="Chiudi" class="pulsanti" onclick="window.close()">
</div>
</body>
</html>