<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    it.eng.afExt.utils.DateUtils,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*,
    com.engiweb.framework.security.*,it.eng.sil.security.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ taglib uri="cal" prefix="cal" %>
<%@ taglib uri="aftags" prefix="af"%>
<%
  String text_messaggio="";

  //EMFErrorHandler engErrorHandler = getErrorHandler();
  //if (serviceResponse.containsAttribute("INSERT_DETTAGLIO_AGENDA.USER_MESSAGE")){
  //  text_messaggio=(String)serviceResponse.getAttribute("INSERT_DETTAGLIO_AGENDA.USER_MESSAGE.TEXT").toString();
  //}
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Errore agenda</title>
  <af:linkScript path="../../js/" />
  <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
</head>

<body>
<textarea rows="5" cols="80"><%=serviceResponse.toXML()%></textarea>
<h1><font color="#FF0000">Si è verificato un errore </font></h1>
</body>
</html>