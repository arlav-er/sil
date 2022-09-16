<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>


        
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  java.util.*,
                  com.engiweb.framework.security.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<html lang="ita">
<head>
	<title>Messaggi</title>
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<af:linkScript path="../../js/"/>
</head>
<body style="overflow:auto">
	<% String messaggio=(String)session.getAttribute("messaggio");%>

	<af:form action="../../servlet/fv/AdapterHTTP" method="POST" dontValidate="true" name="Frm1">	
		<table class="main">
			<tr>
				<td>
					<p align ="center"><font size="3"><B>&nbsp;Avviso</B></font></p>
				</td>
			</tr>
		</table>
		<table class="main">
		
			<%=messaggio%>
			
		</table>
		<div align="center">	
			<input type="submit" class="pulsanti" name="submit" value="Non visualizzare più questi messaggi">
		</div>
	</af:form>
	
<% session.removeAttribute("messaggio"); %>

</body>
</html>
