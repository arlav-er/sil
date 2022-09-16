<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
    // NOTE: Attributi della pagina (pulsanti e link)     
	//Oggetti per l'applicazione dello stile
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);

	SourceBean row = (SourceBean)serviceResponse.getAttribute("M_getDichLav.rows.row");	
	String dataInizio = Utils.notNull(row.getAttribute("datInizio"));
	String tipoDich = Utils.notNull(row.getAttribute("tipoDich"));
	BigDecimal cdnUtIns =  (BigDecimal) row.getAttribute("CDNUTINS");
	String dtmIns =  (String)     row.getAttribute("DTMINS");
	BigDecimal cdnUtMod =  (BigDecimal) row.getAttribute("CDNUTMOD");
	String dtmMod  =  (String) row.getAttribute("DTMMOD");
			
	
	
	Testata operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);	
%>
<html>
	<head>
		<title>Percorso lavoratore: dich. reddito</title>
		<link rel="stylesheet" href="../../css/stili.css" type="text/css">
		<af:linkScript path="../../js/"/>
	<SCRIPT language="JavaScript">
	</SCRIPT>	
	</head>
	<body >
		<br>
      <af:form name="Form1" method="POST" action="AdapterHTTP">
		<center>		
			<%out.print(htmlStreamTop);%>
			<table width="100%">
				<tr><td ><p class="titolo">Dichiarazione reddito</p></td></tr>
				<tr>
					<td align="center" width="100%">              			
						<table width="100%">
							
							<tr><td colspan="3">&nbsp;</tr>
							<tr>
								<td class="etichetta">Data: 
								<td class="campo" colspan="2"><strong><%=dataInizio%></strong>
							</tr>
							<tr>
								<td class="etichetta">Tipo dichiarazione: 
								<td class="campo" colspan="2"><strong><%=tipoDich%></strong>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			<%out.print(htmlStreamBottom);%>
		</center>
		<center>
			<% operatoreInfo.showHTML(out);%>	
			<br>
			<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
		</center>
	</af:form>
</body>
</html>