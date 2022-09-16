<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../amministrazione/openPage.inc" %>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.ProfileDataFilter,  
  java.lang.*,
  java.text.*, 
  java.util.*,
  it.eng.sil.module.coop.GetDatiPersonali,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

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
  <title>Abilitazioni (preselezione)</title>

  <link rel="stylesheet" href="../../css/stiliCoop.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetailCoop.css"/>
  
  <af:linkScript path="../../js/"/>

</head>

<body class="gestione" onload="rinfresca()">

  <%@ include file="_testataLavoratore.inc"%>
  <%@ include file="_linguetta.inc" %>
  
  <af:form method="POST" action="AdapterHTTP" name="Frm1">
 
  	<p align="center">
       <font color="red">
         <af:showErrors />
       </font>
    </p>
  
    <af:list moduleName="M_COOP_GetLavoratoreAbilitazioni_dalla_cache" skipNavigationButton="1"/>          
   
  </af:form>
  
</body>

</html>

<% } %>