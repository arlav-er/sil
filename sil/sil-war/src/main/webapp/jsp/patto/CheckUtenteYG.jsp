<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,
                java.text.*,
                java.util.*,
                java.math.*,
                it.eng.afExt.utils.*,
                it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ taglib uri="aftags" prefix="af"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
	String htmlStreamTop = StyleUtils.roundTopTable(false);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>


<html>
<head>
 <title>Verifica presa in carico GG (Min.)</title>
 <link rel="stylesheet" type="text/css" href="../../css/stiliCoop.css"/>
 <af:linkScript path="../../js/"/>

 
</head>
<!--<body  onload="checkError();" class="gestione" > -->
<body class="gestione" onload="rinfresca()">
<p class="titolo"><br><b>Verifica presa in carico GG (Min.)</b></p>
<%out.print(htmlStreamTop);%>
<center>
<font color="red">
	<af:showErrors/>
</font>
<font color="green">
 	<af:showMessages prefix="M_YG_CheckUtente"/>
</font>
</center>
<%out.print(htmlStreamBottom);%>
<center><input class="pulsante" type = "button" name="chiudi" value="Chiudi" onclick="window.close()"/></center>
</body>
</html>
