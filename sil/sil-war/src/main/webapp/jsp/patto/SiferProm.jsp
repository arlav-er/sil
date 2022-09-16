<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page
	import="com.engiweb.framework.base.*,
                it.eng.sil.security.*,
                it.eng.afExt.utils.*,
  it.eng.sil.util.*,
                java.lang.*,
                java.text.*,
                java.util.*,
                it.eng.sil.util.*,
                java.math.BigDecimal"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%
	String cpihash = (String) serviceRequest.getAttribute("CPIH");
	String _current_page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user,
			_current_page);

	PageAttribs attributi = new PageAttribs(user, _current_page);
	boolean notavailable = false;

	String urlorienter = StringUtils.getAttributeStrNotNull(
			serviceResponse,
			"M_Configurazione_PromSifer.ROWS.ROW.strvalore");
	if (urlorienter.length() < 2)
		notavailable = true;
	urlorienter += "?cpi=" + cpihash;
%>

<%@page import="it.eng.sil.util.Utils"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

</head>

<body>
	<%
		if (notavailable) {
	%>
	<h3>Servizio non disponibile per questa provincia</h3>
	<%
		} else {
	%>
	<iframe src="<%=urlorienter%>" height="500" width="100%"></iframe>
	<%
		}
	%>
</body>
</html>
