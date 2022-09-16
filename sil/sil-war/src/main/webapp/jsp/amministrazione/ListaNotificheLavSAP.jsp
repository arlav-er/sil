<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
	// viene profilata la pagina di ricerca tramite MENU
	String _page = "SAPRicercaPage";
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);

	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		     
%>

<html>
<head>
<title>Lista Notifiche SAP</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/Javascript">
</script>

</head>
<body class="gestione" onload="rinfresca();">
	<af:form dontValidate="true">
	
		<af:list moduleName="M_ListaNotificheSAPLav"/>
		
		<center>
			<input class="pulsante" type="button" name="Chiudi" value="Chiudi" onclick="window.close()" />
		</center>
	</af:form>
	<br />
	
</body>
</html>
<%
	}
%>