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
	String _page = "PTOnLineStoricoPage";
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
    PageAttribs attributi = new PageAttribs(user, _page);

	
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
%>

<html>
<head>
<title>Storico Invii Patto/Accordo Online</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/Javascript">
</script>

</head>
<body class="gestione" onload="rinfresca();">
	<center>
		<font color="red"> 
			<af:showErrors />
		</font>
        
	</center>
  	<af:list moduleName="PTOnLineStoricoModule"/>
  	<center>
 	   <input type="button" class="pulsanti" value="Chiudi" onClick="window.close()" />
	</center>
	 
	<br />
	
</body>
</html>
