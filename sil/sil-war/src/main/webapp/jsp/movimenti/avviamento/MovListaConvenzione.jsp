<!-- @author: Savino - 05/02/2007 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.module.movimenti.*,
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
	<title>Lista Convenzioni Avviamento</title>
	<script language="Javascript">
		datConvNullaOsta = "";
		numConvNullaOsta = "";
		
    	function riportaConveNullaOsta(newNumConvNullaOsta, newDataConvNullaOsta) {
    		numConvNullaOsta = newNumConvNullaOsta;
    		datConvNullaOsta = newDataConvNullaOsta;
    		opener.aggiornaConvNullaOsta();
    	} 
	</script>
</head>

<body class="gestione" onload="">
	<CENTER>
	  <af:list moduleName="M_MovListaConvenzione" jsSelect="riportaConveNullaOsta" skipNavigationButton="1" />	
	  <af:list moduleName="M_MovListaNullaOsta" jsSelect="riportaConveNullaOsta" skipNavigationButton="1" />
	
		<input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
	</CENTER> 
	   
</body>  
