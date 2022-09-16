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

<%@ include file="noCaching.inc" %>
<%@ include file="getCommonObjects.inc" %>

<html>

<HEAD>
 <TITLE>Sessione scaduta</TITLE>
 <META HTTP-EQUIV="expires" CONTENT="0">
 <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
 <META HTTP-EQUIV="pragma" CONTENT="private">

 <link REL="STYLESHEET" TYPE="text/css" HREF="../../css/main.css">
 <af:linkScript path="../../js/"/>

</HEAD>

<body class="gestione" onLoad="rinfresca()">
<p align="center"><img border="0" src="../../img/nonImplementata.gif"></p>
</body>
</html>
