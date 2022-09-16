<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<html>

<head>
	<meta http-equiv="refresh" content="60; url=AdapterHTTP?PAGE=UTENTICONNESSIPAGE&CDNFUNZIONE=125">
  	<title>Lista Utenti Connessi</title>
 	<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
	<link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
	<af:linkScript path="../../js/" />
</head>

<body onload="checkError();" class="gestione">
	<af:error/>
	<p align="center">
	<af:list moduleName="UtentiConnessi"/>
	</p>
</body>

</html>
