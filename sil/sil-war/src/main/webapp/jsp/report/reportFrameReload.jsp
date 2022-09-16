<%--
	il parametro fondamentale di questa jsp e' "bodyEventFunction".
	Se presente viene usato per l'evento del body del frame nascosto. 
--%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  java.lang.*,
                  java.text.*,it.eng.sil.security.PageAttribs,it.eng.sil.util.patto.PageProperties
                  "   %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ include file ="../global/noCaching.inc" %>
<%@ include file ="../global/getCommonObjects.inc" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title></title>
<script>
<%
  String bodyEventFunction=null;
  if (serviceRequest.containsAttribute("bodyEventFunction"))
  	bodyEventFunction = (String)serviceRequest.getAttribute("bodyEventFunction");
  else 
  	bodyEventFunction = "onunload=\"parent.window.opener.top.main.location.reload()\"";
%>
function scriviFrame() {
    actionFrame = window.REPORT.document;
    actionFrame.open();
    actionFrame.write('<%=JSPReportUtil.getFormToAction(request)%>');
    actionFrame.close();
    actionFrame = window.NAVIGA.document;
    actionFrame.open();
    actionFrame.write('<html><head></head><body <%=bodyEventFunction%> ></BODY></HTML>');
    actionFrame.close();
}
function chiamaReport() {
    window.REPORT.document.form1.submit();
}

</script>
</head>
<%
    String queryString = JSPReportUtil.getQueryString(request);
%>
<%String aggiornaMain = null;
String refreshMain = request.getParameter("REFRESH_MAIN");
aggiornaMain = refreshMain !=null && refreshMain.equals("true") ? "true":"false";
%>
<FRAMESET rows="0,*" onload="scriviFrame()">
  <FRAME  frameborder="0" noresize name="NAVIGA" scrolling="no" src="" >
  <FRAME frameborder="0" name="REPORT" noresize src="">
</FRAMESET>
</HTML>


