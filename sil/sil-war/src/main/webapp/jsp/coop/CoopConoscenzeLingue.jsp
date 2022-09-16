<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*,
  it.eng.afExt.utils.*,
  it.eng.sil.security.ProfileDataFilter,     
  java.lang.*,
  java.text.*, 
  java.util.*,
  it.eng.sil.module.coop.GetDatiPersonali,
  java.math.*,
  com.engiweb.framework.security.*,
  it.eng.sil.security.PageAttribs,  
  it.eng.sil.security.User"%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%

	String _current_page = (String) serviceRequest.getAttribute("PAGE");
	int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);

	boolean canView=filter.canView();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	else {

%>

<html>

<head>
  <title>Conoscenze Linguistiche</title>

  <link rel="stylesheet" href="../../css/stiliCoop.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetailCoop.css"/>

  <af:linkScript path="../../js/"/>

</head>

<body class="gestione" onload="rinfresca()">


  <%@ include file="_testataLavoratore.inc"%>
  <%@ include file="_linguetta.inc" %>
      
  <af:form method="POST" action="AdapterHTTP" name="MainForm">

		<p align="center">
		    <font color="red">
		      <af:showErrors />
		    </font>
		</p>
	
	  <af:list moduleName="M_COOP_ListConoscenzeLing_dalla_cache" skipNavigationButton="1"/>          
      
  </af:form> 
  
</body>

</html>

<% } %>