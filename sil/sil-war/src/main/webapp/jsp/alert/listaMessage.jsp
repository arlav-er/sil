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
<%
	String cdnFunzione = (String) serviceRequest.getAttribute("cdnfunzione");
%>

<html>

<head>
	<title>Lista Messaggi</title>
 	<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
	<link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
	<af:linkScript path="../../js/" />
	<script language="javascript">
      function nuovaRicerca(){
      	// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
      
        var url = "AdapterHTTP?PAGE=RicercaMessagePage&cdnfunzione=<%=cdnFunzione%>";
		setWindowLocation(url);
      }
      function nuovoMessaggio(){
      	// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
      
        var url = "AdapterHTTP?PAGE=InsertMessageFormPage&cdnfunzione=<%=cdnFunzione%>";
		setWindowLocation(url);
      }
    </script>
</head>

<body onload="checkError();" class="gestione">
	<af:error/>
	<af:showMessages prefix="InsertMessage"/>
	<af:showMessages prefix="DeleteMessage"/>
	<p align="center">
	<af:list moduleName="ListaMessage"/>
	<br>&nbsp;
	<p align="center">
		<input type="button" class="pulsante" VALUE="Nuovo messaggio" onClick="nuovoMessaggio()"/>
	</p>
	<p align="center">
		<input type="button" class="pulsante" VALUE="Nuova ricerca" onClick="nuovaRicerca()"/>
	</p>
</body>

</html>
