<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    it.eng.afExt.utils.DateUtils,
    it.eng.sil.security.User,
    it.eng.afExt.utils.*,it.eng.sil.util.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%>
    
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>


<html>
<head>
  <title>Lista Assegnazione</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 <af:linkScript path="../../js/" />
 <script>
 	function riportaValori(prgSpi, codServizio, prgAmbiente, cognome, nome){
 		opener.assegnazioneScelta(prgSpi, codServizio, prgAmbiente, cognome, nome); 
 	}
 </script>
</head>
<body class="gestione">
<font color="red">
  <af:showErrors/>
</font>

<af:list moduleName="MListaAssegnazioneAppuntamento" jsSelect="riportaValori"/>
<center>
<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
</center>
</body>
</html>
