<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  
  if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }else{
 
  //Profilatura ------------------------------------------------
  //PageAttribs attributi = new PageAttribs(user, "ListaTabDecodPage");
  //String _page = (String) serviceRequest.getAttribute("PAGE");
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("INSERISCI");
  boolean canDelete= attributi.containsButton("CANCELLA");
  //boolean canAggiorna= attributi.containsButton("AGGIORNA");
		
//Servono per gestire il layout grafico
String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>



<html>
<head>
    <title>GestioneModelloStampa.jsp</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
    <af:linkScript path="../../js/"/>


<script language="JavaScript">
</script>
</head>

<body class="gestione" onload="rinfresca()">

		<af:list moduleName="M_GetModello" 
             canDelete="<%= canDelete ? \"1\" : \"0\" %>" 
             canInsert="<%= canModify ? \"1\" : \"0\" %>" />

</body>
</html>
<%}%>