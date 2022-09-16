<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                it.eng.sil.security.*,
                java.lang.*,
                java.text.*,
                java.util.*,
                it.eng.sil.util.Linguette,
                java.math.BigDecimal,
                it.eng.sil.util.InfCorrentiLav" %>
                
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<html>
<head>
<META HTTP-EQUIV="expires" CONTENT="0">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META HTTP-EQUIV="pragma" CONTENT="private">
<af:linkScript path="../../js/" />

</head>
<body>
    <h1>Livello di log modificato!!! </h1> 
    <h1>DEBUG = <%=serviceResponse.getAttribute("DEBUG")%></h1> 
    <h1>MINLOGLEVEL = <%=serviceResponse.getAttribute("MINLOGSEVERITY") %></h1> 

  </body>
</html>
