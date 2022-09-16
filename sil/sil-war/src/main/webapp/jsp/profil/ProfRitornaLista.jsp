<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>

<%
  String cdnFunzione = (String) serviceRequest.getAttribute("cdnfunzione");
  String denominazione = (String) serviceRequest.getAttribute("STRDENOMINAZIONERIC");
  String tipoGruppo = (String) serviceRequest.getAttribute("TIPOGRUPPORIC");
  String flagStandard   = (String) serviceRequest.getAttribute("FLGSTANDARDRIC");  
%>

<html>
<head>
	<af:linkScript path="../../js/" />
</head>

<body onload="javascript:doFormSubmit(document.Frm1);">
<af:form action="AdapterHTTP?PAGE=ProfListaGruppiPage" method="POST" name="Frm1" dontValidate="true">
<input type="hidden" name="STRDENOMINAZIONERIC" value="<%=denominazione%>"/>
<input type="hidden" name="TIPOGRUPPORIC" value="<%=tipoGruppo%>"/>
<input type="hidden" name="FLGSTANDARDRIC" value="<%=flagStandard%>"/>
</af:form>
</body>
</html>