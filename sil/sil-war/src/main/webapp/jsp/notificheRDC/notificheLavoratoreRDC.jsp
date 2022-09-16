<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.math.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
String _page = (String) serviceRequest.getAttribute("PAGE");
ProfileDataFilter filter = new ProfileDataFilter(user, _page);

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
}

//NOTE: Attributi della pagina (pulsanti e link) 
PageAttribs attributi = new PageAttribs(user, _page);

String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
%>

<html>
<head>
<title>Notifiche Reddito di cittadinanza</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/Javascript">
	<%//Genera il Javascript che si occuperà di inserire i links nel footer
	if (!cdnLavoratore.equals(""))
		attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);
	%>
</script>

</head>

<body class="gestione">

<%
InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
testata.show(out);
Linguette l  = new Linguette(user, Integer.parseInt(_funzione), _page, new BigDecimal(cdnLavoratore));
l.show(out);
%>

<af:list moduleName="M_LavoratoreNotificheRDC" />

</body>
</html>