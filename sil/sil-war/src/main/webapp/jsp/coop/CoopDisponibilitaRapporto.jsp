<%@ page contentType="text/html;charset=utf-8"%>

<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  it.eng.sil.module.coop.GetDatiPersonali,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

    ProfileDataFilter filter = new ProfileDataFilter(user, _page);
    boolean canView=filter.canView();
    
    if (!canView){
      response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
    }
		else {
%>



<html>

<head>
  <title>Disponibilità di tipi di rapporto per mansioni</title>
  <af:linkScript path="../../js/"/>
  <link rel="stylesheet" href="../../css/stiliCoop.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetailCoop.css"/>

</head>

<body class="gestione" onload="">

  <%@ include file="_testataLavoratore.inc"%>
  <%@ include file="_linguetta.inc" %>
  
  <af:form method="POST" action="AdapterHTTP" name="MainForm">

   <p align="center">
      <font color="red">
        <af:showErrors />
      </font>
	 </p>

    <div align="center">
     
         
      <af:list moduleName="M_COOP_ListContrattiMansioni_dalla_cache" skipNavigationButton="1"/>
    
  </af:form>
  
</body>

</html>

<% } %>

