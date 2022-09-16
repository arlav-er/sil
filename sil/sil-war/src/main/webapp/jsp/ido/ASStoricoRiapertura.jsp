<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*,
                java.math.*
                " %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
	Vector storicoRows 	= serviceResponse.getAttributeAsVector("M_GETMOTIVOAPERTURARICHLIST.ROWS.ROW");
	SourceBean storico 	= null;
	  
	String prgrichAz	= "";
	prgrichAz			=  StringUtils.getAttributeStrNotNull(serviceRequest,"PRGRICHIESTAAZ");	  
%>

<html>
<head>
	<title>Storico riapertura delle richieste </title>
	<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
	<link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
 	<af:linkScript path="../../js/" />
</head>

<body onload="checkError();" class="gestione">
<af:error/>
<p align="center">


<af:list moduleName="M_GETMOTIVOAPERTURARICHLIST" />

<table class="main">
	<tr>
  		<td>&nbsp;
  		</td>
  	</tr>
  	<tr>
  		<td align="center"><input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
  		</td>
  	</tr>
</table>

<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgrichAz%>" />

</body>
</html>