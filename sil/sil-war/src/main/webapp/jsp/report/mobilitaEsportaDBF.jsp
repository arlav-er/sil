<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ page session="true"%>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*, it.eng.sil.bean.*, it.eng.sil.action.report.amministrazione.*" %>


<% String queryString = null; %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<title>Esportazione file in formato DBF</title>
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<af:linkScript path="../../js/" />
	
	<%
		MobilitaExpThread mobT = (MobilitaExpThread) sessionContainer.getAttribute(MobilitaEsportaDBF.MOBILITA_ESPORTATORE_THREAD);
	String URL_RUNNING  = "AdapterHTTP?ACTION_NAME=MOB_ESPORTA_DBF";
	String URL_DOWNLOAD = "AdapterHTTP?ACTION_NAME=DOWNLOAD_ZIP_DBF";
	String sessionId = request.getSession().getId();
	sessionContainer.setAttribute("HTTP_SESSION_ID",sessionId);
	int ritardo = 30;
	
	String url = URL_RUNNING;
	if  (!mobT.isRunning()) {
			url = URL_DOWNLOAD;
			ritardo = 5;
	}
	
	if (mobT.isRunning()) {%>
		<style type="text/css">
			body {cursor:wait}
		</style>
	<%}%>



<meta http-equiv="Refresh"
	content="<%=ritardo%>;URL=<%=url%>" />

</head>
<body class="gestione" onload="rinfresca()">
<p class="titolo">Esportazione file in formato DBF</p>

<%
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	out.print(htmlStreamTop);
%>
	<p>&nbsp;</p>

<%

	Iterator iter = mobT.iterator();
	while (iter.hasNext()) {
		String msg = (String) iter.next();
	%><p align="left"><%=msg%></p>
	<%}%>
	<p>&nbsp;</p>
<%	
	out.print(htmlStreamBottom);
%>

<table align="center" cellspacing="0" cellpadding="0" border="0px" width="94%">
<tr><td align="left" class="prof_ro">

				<%
				
				if (mobT.isRunning()) {%>
					Esportazione in corso. Attendere prego...
				<%}else{%>
					Esportazione completata.<br/>
					Lo scarico del file partirà automaticamente fra qualche secondo.
					</td></tr>
					
					<tr><td class="prof_ro">
					Una volta generata la stampa delle richieste di mobilità sospese, tali mobilità acquisiranno lo stato INVIATO.
					</td></tr>
					<br/>
					<tr><td align="center" class="prof_ro">
					<input class="pulsante" type="button" name="Stampa" value="Stampa" onClick="apriGestioneDoc('RPT_MOB_DBF','','MREA0')"/>
   					</td></tr>
   					</table>
					
				<%}
				%>

</body>
</html>
