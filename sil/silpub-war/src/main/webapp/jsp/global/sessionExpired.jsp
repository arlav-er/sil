<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.configuration.ConfigSingleton,
                 com.engiweb.framework.base.*,
                 com.engiweb.framework.dispatching.module.*,
                 com.engiweb.framework.error.*,
                 com.engiweb.framework.security.*,
                 it.eng.sil.util.*,
                 it.eng.sil.security.*,                 
                 java.util.*" %>

<%-- @ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" --%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%-- @ include file="../global/getCommonObjects.inc"  --%>

<%
	response.sendRedirect("../../index.html");
%>

<html>

<HEAD>
 <TITLE>Sessione scaduta</TITLE>
 <META HTTP-EQUIV="expires" CONTENT="0">
 <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
 <META HTTP-EQUIV="pragma" CONTENT="private">

 <link REL="STYLESHEET" TYPE="text/css" HREF="../../css/main.css">
 <af:linkScript path="../../js/" />
 
 <script language="JavaScript">
        <!-- 
         if (self != top) 
            top.location = "AdapterHTTP?ACTION_NAME=SESSION_EXPIRED_ACTION"
        
        -->
</script>



</HEAD>

<body class="gestione">
</body>
</html>
