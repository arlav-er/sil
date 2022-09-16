<%// Modifica  effettuata per ottimizzare il caricamento di pagine jsp che non 
  // la sessione http 
  %>

<%@ page contentType="text/html;charset=utf-8"%>
<%@ page session="false"%>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>

<% InfoRegioneSingleton regione = InfoRegioneSingleton.getInstance(); %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<html lang="ita">
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>
<script language="JavaScript" src="../../js/monitoraggio.js"></script>
<script language="JavaScript">
function ripristinaFS(){
	l_fsMenu =window.top.document.getElementById('fsMenu');
	if (l_fsMenu.cols=='0,*'){
		window.top.initTimerChiusura();
	}
}

</script>

</head>
<body class="gestione" onLoad="rinfresca();ripristinaFS();">

<table width="100%" height="100%">
	<tr valign="middle">
		<td align="center" class="presentazione" width="100%" height="100%"><img
			src="../../img/loghi/<%=regione.getCodice()%>_siler.gif" alt="SIL" border="0" width="107" height="47"> <br/>
		<%=regione.getDispAccesso()%></td>
	</tr>
</table>

</body>
</html>
