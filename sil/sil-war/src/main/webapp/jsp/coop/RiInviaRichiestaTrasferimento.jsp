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


<html>
<head>
 <TITLE>RiInviaRichiestaTrasferimento</TITLE>
 <!-- ../jsp/anag/TrasferimentiStoricoElenco.jsp -->
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <af:linkScript path="../../js/"/>

 
</head>
<body  class="gestione" onload="rinfresca()">
<center>
<font color="red">
    <af:showErrors/>
    <af:showMessages prefix="M_COOP_PRESAATTO"/>
</font>
</center>

<center><input class="pulsante" type = "button" name="chiudi" value="Chiudi" onclick="window.close()"/></center>
</body>
</html>
