<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.configuration.*,
                  com.engiweb.framework.security.*" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

  String rootPathReport = ConfigSingleton.getRootPath();
  boolean debug = false;
  List params = new ArrayList();
  boolean ancora = true;
  int i=0;
  while (ancora){
    String paramVal = (String) serviceRequest.getAttribute("PROMPT" + i);
    if (paramVal== null){
      ancora = false;
    }
    else{
      params.add(paramVal);
      i++;
    }
  } 

  String report = (String) serviceRequest.getAttribute("REPORT");

  String url = "../../servlet/ReportServlet";
  String rootPath = "file:" + rootPathReport + "/WEB-INF/report/";
  url += "?report=" + rootPath + report;
  
  url += "&init=pdf";
  url += "&dll=psdora4dati.dll";

  for (int j = 0; j< params.size(); j++){
      String parVal = (String) params.get(j);
      url += "&prompt" + j + "=" + parVal;
  }

  // random
  url += "&d=" + Math.round(Math.random()*100);

%>


<%if (!debug){ %>
  <html>
    <head>
      <META HTTP-EQUIV="Refresh" CONTENT="0;URL=<%=url%>">
    </head>
  <body>
  </body>
  </html>
  
<%} else { %>  

<html>
  <head>
	  <af:linkScript path="../../js/" />
  </head>
  <body>

  <table border=1>

  <tr>
    <th>Parametro</th>
    <th>Valore</th>
  </tr>

<%
  for (int j = 0; j< params.size(); j++){
      String parVal = (String) params.get(j);
  %>    
  <tr>
    <td>prompt[<%=j%>] </td>
    <td><%=parVal%></td>
  </tr>
  <% }
%>


  <tr>
    <td>Report richiesto </td>
    <td><%=report%></td>
  </tr>
  <tr>
    <td>URL completa</td>
    <td><%=url%></td>
  </tr>

  </table>


<p>Clicca <a href="<%=url%>">qui</a> per procedere nella visualizzazione del report</p>

<%@ include file="/jsp/MIT.inc" %>
</body>
</html>

<%}%>

