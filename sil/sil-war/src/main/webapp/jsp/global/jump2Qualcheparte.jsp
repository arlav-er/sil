<%@ page

	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.sil.util.*,
			it.eng.sil.module.Jump2Module,
			it.eng.afExt.utils.*"

	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	// Ottengo la nuova URL come risposta del modulo "Jump2Module"
	String jump2url = null;
	SourceBean jump2ModuleResp = (SourceBean) serviceResponse.getAttribute("Jump2Module");
	if (jump2ModuleResp != null) {

		jump2url = (String) jump2ModuleResp.getAttribute(Jump2Module.ATTRIB_jump2url);
	}

	if (StringUtils.isFilled(jump2url)) {

		response.sendRedirect(jump2url);
		%>
		<%-- PER DEBUG:
		<html>
		<head></head>
		<body>
			jump2url = "<%= jump2url %>"
		</body>
		</html>
		--%>
		<%
	}
	else {
		%>
		<html>
		<head></head>
		<body>
			Impossibile saltare alla pagina.
		</body>
		</html>
		<%
	}
%>
	
	