<%@page import="it.eng.sil.module.amministrazione.DettaglioMesiAnzianita"%>
<%@page import="it.eng.sil.tags.calendario"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.tracing.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.bean.*,
                  it.eng.sil.module.anag.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>
<%
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
 	int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
 	String dettaglioRisultato = "";
 	boolean esitoOK = true;
	String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
	SourceBean row_Anzianita = (SourceBean)serviceResponse.getAttribute("M_CALCOLA_DETTAGLIO_MESI_ANZ_FORNERO.ROW");
	if (row_Anzianita.containsAttribute("Esito") && row_Anzianita.getAttribute("Esito").toString().equalsIgnoreCase("OK")) {
		dettaglioRisultato = row_Anzianita.containsAttribute("Risultato")?row_Anzianita.getAttribute("Risultato").toString():"";
		//anno-mese-1-giornisosp#anno-mese-0-giornisosp
	}
	else {
		esitoOK = false;
	}
  	String htmlStreamTop = StyleUtils.roundTopTable(false);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
  <head>
  	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
  </head>
  <body class="gestione">
  <%=htmlStreamTop%>
  <center>
	<font color="green">
		<af:showErrors/>
    </font>
  </center>
  <% if (esitoOK) {%>
  <br/><p class="titolo">Dettaglio mesi di anzianità (ex legge Fornero)</p>
  <af:form name="Frm1">
	<table class="main">
		<tr>
			<td>Anno</td>
			<td>Mese</td>
			<td>Mese anz.</td>
			<td>Giorni sosp.</td>
		</tr>
		
		<%
		String []dettaglioMesi = dettaglioRisultato.split("#"); 
		for (int i=0;i<dettaglioMesi.length;i++) {
			String []meseCurr = dettaglioMesi[i].split("-");%>
			<tr>
				<td><%=meseCurr[0]%></td>
				<td><%=DettaglioMesiAnzianita.mesi[Integer.parseInt(meseCurr[1]) - 1]%></td>
				<td><%=meseCurr[2]%></td>
				<td><%=meseCurr[3]%></td>
			</tr>
		<%}%>
	</table>
  </af:form>
  <%}%>
  <p align="center">
	<input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
  </p>
  <%=htmlStreamBottom%>

  </body>
</html>
