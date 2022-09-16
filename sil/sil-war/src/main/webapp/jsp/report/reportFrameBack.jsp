<%--

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
function scriviFrame() {
    actionFrame = window.REPORT.document;
    actionFrame.open();
    actionFrame.write('<%=JSPReportUtil.getFormToAction(request)%>');
    actionFrame.close();
}
var flag= false;

function chiamaReport() {
    window.REPORT.document.form1.submit();
   	flag=true;
}

function f() {
	if (flag){
		if (parent.window.opener.top.main.aggiorna!=null) {			
			parent.window.opener.top.main.aggiorna();
		}
		else 
			parent.window.opener.top.main.location.reload();
	}
}
</script>
</head>
<%
//    String queryString = (String)requestContainer.getAttribute("HTTP_REQUEST_QUERY_STRING");
//String queryString = (String)requestContainer.getAttribute("REFERER");
    String queryString = JSPReportUtil.getQueryString(request);
    //String actionParameters = getURLAction(request);
%>
<%String aggiornaMain = null;
String refreshMain = request.getParameter("REFRESH_MAIN");
aggiornaMain = refreshMain !=null && refreshMain.equals("true") ? "true":"false";
String datTrasferimento = request.getParameter("DATTRASFERIMENTO");
if (datTrasferimento == null) {
	datTrasferimento = "";	
}
%>
<FRAMESET rows="35%,*" onload="scriviFrame()">
  <FRAME  frameborder="0" noresize name="NAVIGA" scrolling="auto" src="AdapterHTTP?PAGE=ReportTopPage&QUERY_STRING=<%=JSPReportUtil.getQueryStringForURL(queryString)%>&REFRESH_MAIN=<%=aggiornaMain%>&INIBISCI_INDIETRO=true&DATATRASFCOMP=<%=datTrasferimento%>" >
  <FRAME frameborder="0" name="REPORT" noresize src="" onload="<%=aggiornaMain.equals("true")? "f()":"" %>" >
  <%-- System.out.println("AdapterHTTP?"+actionParameters);
System.out.println("../../jsp/report/reportTop.jsp?QUERY_STRING="+queryString);
Enumeration e = request.getHeaderNames();
while (e.hasMoreElements()) {
String k = (String)e.nextElement();
System.out.println(k +"="+request.getHeader(k));
}
  --%>

</FRAMESET>
</HTML>
<%--!
/**
* Crea lo url per la chiamata della Action che genera il pdf. 
* 1) Elimina la page che ha chiamato questa jsp (ReportFramePage)
* 2) Imposta il parametro ACTION_NAME valorizzandolo col valore del parametro ACTION_REDIRECT
* 3) I restanti paramtri vengono replicati
*/
String getURLAction(HttpServletRequest request) {
/*
    Map map = request.getParameterMap();
    map.remove("PAGE");
    String action =(String) map.get("ACTION_REDIRECT");
    map.remove("ACTION_REDIRECT");
    Set s = map.keySet();
    Iterator iter = s.iterator();
    StringBuffer sb = new StringBuffer();
/*    sb.append("AdapterHTTP?");*/
    StringBuffer sb = new StringBuffer();
    Enumeration names = request.getParameterNames();
    String action = request.getParameter("ACTION_REDIRECT");
    sb.append("ACTION_NAME=");
    sb.append(action);
    sb.append("&");
    while(names.hasMoreElements())  {
        String key = (String)names.nextElement();
        if (key.toUpperCase().equals("PAGE") || key.toUpperCase().equals("ACTION_REDIRECT")  || key.toUpperCase().equals("QUERY_STRING"))
            continue;
        sb.append(key);
        sb.append("=");
        sb.append(request.getParameter(key));
        sb.append("&");
    }
    return sb.toString();
}
--%>
<%!
/**
* codifica la stringa prima che venga usata come url
*/
/*
String getQueryString(String qs) {    
    StringTokenizer st = new StringTokenizer(qs, "&");
    StringBuffer sb = new StringBuffer();
    while (st.hasMoreTokens()) {
        String par = (String)st.nextToken();
        int i = par.indexOf("=");
        if (i<0)continue;
        sb.append(par.substring(0,i));
        sb.append("%3D");
        if (par.length()>i+1)
            sb.append(par.substring(i+1));
        sb.append("%26");
    }
    return sb.toString();
}
*/
/*
String getFormToAction(HttpServletRequest request) {
    StringBuffer sb = new StringBuffer();    
    Enumeration names = request.getParameterNames();
    //
    sb.append("<html><head></head><body onload=\"window.parent.chiamaReport()\"><form name=\"form1\" method=\"post\" action=\"AdapterHTTP\" target=\"REPORT\">");
    String action = request.getParameter("ACTION_REDIRECT");
    sb.append(makeInput("ACTION_NAME",action));
    while(names.hasMoreElements())  {
        String key = (String)names.nextElement();
        if (key.toUpperCase().equals("PAGE") || key.toUpperCase().equals("ACTION_REDIRECT")  || key.toUpperCase().equals("QUERY_STRING")
        || key.equals("REQUEST_CONTAINER") || key.equals("RESPONSE_CONTAINER"))
            continue;
        sb.append(makeInput(key, request.getParameter(key)));
    }
    sb.append("</form></body></html>");
    return sb.toString();
}
String makeInput(String name, String value) {
    return "<input type=\"hidden\" name=\""+name+"\" value=\""+value+"\">";
}
*/
%>

