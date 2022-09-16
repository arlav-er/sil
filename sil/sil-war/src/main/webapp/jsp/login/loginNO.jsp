<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file ="../global/noCaching.inc" %>
<%@ include file ="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html lang="ita">
<head>
  <META HTTP-EQUIV="expires" CONTENT="0">
  <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
  <META HTTP-EQUIV="pragma" CONTENT="private">
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<af:linkScript path="../../js/" />
</head>
<%
  String msgId = (String) serviceRequest.getAttribute("msg");
  String msgError = serviceRequest.getAttribute("msgError") == null ? "" :(String) serviceRequest.getAttribute("msgError");
  String msg="";
  String msg1="";
  
  if (msgId.equalsIgnoreCase("KO")){
    msg= "Autenticazione fallita";
    msg1= "Reinserire <em>login </em>e <em>password</em>";
    
  }else  if (msgId.equalsIgnoreCase("BLOCCATO")){
    msg= "Account bloccato";
  }else  if (msgId.equalsIgnoreCase("NON_ANCORA_VALIDO")){
    msg= "Account non ancora valido";
  }else  if (msgId.equalsIgnoreCase("SCADUTO")){
    msg= "Account scaduto";
  }else  {
    msg= "Errore generico";
  } 
  
%>


<body>
      <br>
      <hr>
        <div align="center" ID="IdDiv" style="BACKGROUND-COLOR: gold; COLOR: black;">

          <strong><%=msg%><br> 
            <%=msg1%>
            <br>
            <br>
            <%= msgError%>
          </strong>

        </div>
      <hr>
      <br>
      <p align="center"><input type="button" class="pulsanti" value="CHIUDI" onClick="window.close();"></p>
      
</body>
</html>
