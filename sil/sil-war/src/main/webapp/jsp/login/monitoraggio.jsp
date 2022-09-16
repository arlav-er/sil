<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.configuration.ConfigSingleton,
  it.eng.sil.security.User, java.net.URLEncoder"
%><%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ taglib uri="aftags" prefix="af" %><%
	 
    String token 	  = (String) serviceResponse.getAttribute("MONITORAGGIO.TOKEN");
	if ( token!=null){
			
		  	String username   = (String) sessionContainer.getAttribute("_USERID_");
			String baseUrl = (String) ConfigSingleton.getInstance().getAttribute("DWH.url.value");
			String redirectURL = baseUrl  + "faces/private/" +  
					   			 username  + 
					   			"?j_username="  + username + 
					   			"&j_password="  +URLEncoder.encode(token);
			%>
		
			<html>

			<HEAD>
			 <TITLE>Accesso negato</TITLE>
			 <META HTTP-EQUIV="expires" CONTENT="0">
			 <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
			 <META HTTP-EQUIV="pragma" CONTENT="private">
			
			 <link REL="STYLESHEET" TYPE="text/css" HREF="../../css/stili.css">
			
			</HEAD>

			<body class="gestione">
				<SCRIPT>
				window.top.initTimerApertura();
				window.location.href="<%=redirectURL%>";
				</SCRIPT>
			</body>
			</html>
	<%} else {%>
	
			
			<html>
			
			<HEAD>
			 <TITLE>Accesso negato</TITLE>
			 <META HTTP-EQUIV="expires" CONTENT="0">
			 <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
			 <META HTTP-EQUIV="pragma" CONTENT="private">
			
			 <link REL="STYLESHEET" TYPE="text/css" HREF="../../css/stili.css">
			<af:linkScript path="../../js/"/>
			
			</HEAD>
			
			<body class="gestione">
			
			<p align="center">&nbsp;</p>
			<p class="titolo">Accesso non consentito al modulo di monitoraggio</p>
			<p class="titolo">Richiedere i relativi diritti all'amministratore</p>
			<p align="center">&nbsp;</p>
			<p align="center"><img border="0" src="../../img/accessoNegato.gif"></p>
			
			</body>
			</html>
	
	<%}%>
	

