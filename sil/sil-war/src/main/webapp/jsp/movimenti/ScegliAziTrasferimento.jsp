<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
				  com.engiweb.framework.tags.DefaultErrorTag,
                   
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.module.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
	<af:linkScript path="../../js/"/>
	<title>Lista Aziende Trasferimento</title>

</head>

<body class="gestione">
	<CENTER>
	  <af:list moduleName="M_MovimentiGetListaAziendeNew" configProviderClass="it.eng.sil.module.movimenti.DynamicRicAziendeConfig"/>	
	 	<input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
	</CENTER> 
	   
</body>  
