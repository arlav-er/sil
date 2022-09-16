<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.configuration.ConfigSingleton,
                 com.engiweb.framework.base.*,
                 com.engiweb.framework.dispatching.module.*,
                 com.engiweb.framework.error.*,
                 com.engiweb.framework.security.*,
                 com.engiweb.framework.tracing.*,
                 it.eng.sil.util.*,
                 it.eng.sil.security.*,
                 java.util.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<html>

<HEAD>
 <TITLE>Errore!</TITLE>
 <META HTTP-EQUIV="expires" CONTENT="0">
 <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
 <META HTTP-EQUIV="pragma" CONTENT="private">

 <link REL="STYLESHEET" TYPE="text/css" HREF="../../css/main.css">
 <af:linkScript path="../../js/" />

</HEAD>

<body>

<%
    EMFErrorHandler engErrorHandler = responseContainer.getErrorHandler();
    SimpleErrorHandler seh= new SimpleErrorHandler(engErrorHandler);

    if (seh.containsErrors()) {

      %><%@ include file="showError.inc" %><%

    } else {
      %><p class="LISTAERROR">Si &egrave; verificato un errore sconosciuto!</p><%
    }
%>

<br><br>

</body>
</html>
