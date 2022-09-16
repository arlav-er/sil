<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="com.engiweb.framework.base.*,
				java.util.*,
				it.eng.sil.security.*,
				it.eng.sil.util.* " %>
      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
	// Layout grafico
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);

	// NOTE: Attributi della pagina (pulsanti e link) 
	String _page = (String) serviceRequest.getAttribute("PAGE");
	PageAttribs attributi = new PageAttribs(user,_page);
	
%>

<html>
<head>
<title>Stampe Mobilita</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<% String queryString = null; %>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
    
<script language="JavaScript">

function toPageRicerca(strPage,sex) {
	var f;
	f = "AdapterHTTP?PAGE=" + strPage;
	f = f + "&CDNFUNZIONE=<%=cdnFunzione%>";
	f = f + "&SESSO="+sex;
	document.location = f;
}

</script>
</head>

<body class="gestione" onload="rinfresca()">
<br/>

<% String onClick; %>
<%= htmlStreamTop %>
<table class="main">
	<tr><td width="33%">&nbsp;</td><td width="34%">&nbsp;</td><td width="33%">&nbsp;</td></tr>
	<tr><td colspan="3"><p class="titolo">Gestione stampe</p></td></tr>
	<tr><td>&nbsp;</td></tr>
	
	<tr>
	  <td></td>
	  <td nowrap align="left">&nbsp;
		<%
		onClick = "toPageRicerca('RicercaComitatoSilPage')";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif" 
			alt="Lista mobilit&agrave; per Comitato/Iscritti con sospesi" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Lista mobilit&agrave; per Comitato/Iscritti con sospesi</a>
	  </td>
	  <td></td>
	</tr>
	
	<tr>
	  <td></td>
	  <td nowrap align="left">&nbsp;
		<%
		onClick = "toPageRicerca('RicercaMobilitaUtentiEsterniPage')";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif" 
			alt="Lista mobilit&agrave; per utenti esterni" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Lista mobilit&agrave; per utenti esterni</a>
	  </td>
	  <td></td>
	</tr>
		
	<tr>
	  <td></td>
	  <td nowrap align="left">&nbsp;
		<%
		onClick = "toPageRicerca('RicercaMobCancellatiPage')";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Lista Cancellati" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Lista Cancellati</a>
	  </td>
	  <td></td>
	</tr>
	
	<tr>
      <td></td>
      <td nowrap align="left">&nbsp;
        <%
        onClick = "toPageRicerca('ProvincialePage','F')";
        %>
        <a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
            alt="Lista Cancellati" /></a>&nbsp;
        <a href="#" onClick="<%= onClick %>">Donne Provinciali con et&agrave; maggiore di 45 anni per Servizio Lavoro</a>
      </td>
      <td></td>
    </tr>
    
    <tr>
      <td></td>
      <td nowrap align="left">&nbsp;
        <%
        onClick = "toPageRicerca('ProvincialePage','M')";
        %>
        <a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
            alt="Lista Cancellati" /></a>&nbsp;
        <a href="#" onClick="<%= onClick %>">Uomini Provinciali con et&agrave; maggiore di 50 anni per Servizio Lavoro</a>
      </td>
      <td></td>
    </tr>
	
	<tr><td colspan="3">&nbsp;</td></tr>
	<tr><td colspan="3">&nbsp;</td></tr>
</table>
<%= htmlStreamBottom %>

</body>
</html>
