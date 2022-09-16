<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<!-- @author: Giordano Gritti -->
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*, 
                java.math.*,
                com.engiweb.framework.message.*,
                it.eng.afExt.utils.StringUtils,
                it.eng.afExt.utils.MessageCodes"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>
<%
		//String data = "";
		//String ora = "";
		//String strcell = "";
		//String descrizione = "";		
		
		String cdnlavoratore = "";      	
      	
		String _page = (String) serviceRequest.getAttribute("PAGE");
		ProfileDataFilter filter = new ProfileDataFilter(user, _page);
		BigDecimal _prgAppunt = ((BigDecimal) serviceResponse.getAttribute("PRGAPPUNTAMENTO"));
		BigDecimal _cdnlavoratore = ((BigDecimal) serviceResponse.getAttribute("CDNLAVORATORE"));
		BigDecimal _codcpi = ((BigDecimal) serviceResponse.getAttribute("CODCPI"));
		Vector vettRis = serviceResponse.getAttributeAsVector("M_AGENDA_SMS_INVIO.SMS_NONINVIATI.LAVORATORE");		 
		String error = (String) serviceResponse.getAttribute("M_AGENDA_SMS_INVIO.SMS_NONINVIATI.ERROR");		 
				

		//formattazione pagina jsp
		String htmlStreamTop = StyleUtils.roundTopTable(false);
		String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Esito dell'invio degli SMS</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<af:linkScript path="../../js/" />
<script TYPE="text/javascript">

</script>

</head>

<body class="main" onload="rinfresca()">
<table width="100%" align="center">
	<tr>
		<td>&nbsp;</td>
	</tr>

</table>
<%out.print(htmlStreamTop);%>
<p>&nbsp;</p>
<table class="main" align="center">
<%if (error != null && error.equals("160004")) {%>
	<tr>
		<td><b>L'utente non ha un operatore associato, è impossibile proseguire l'invio del SMS</b></td>
	</tr>
<%} else if (error != null && error.equals("160007")) {%>
	<tr>
		<td><b>Imposibile inviare: non è stato possibile contattare il server di posta</b></td>
	</tr>
<%} else if (error != null) {%>
	<tr>
		<td><b>Imposibile inviare SMS: errore generico</b></td>
	</tr>
<%} else {%>
<% if (vettRis.size()== 0 ) {%>
	<tr>
		<td>&nbsp;</td>
	</tr>	
	<tr>
		<td class="titolo" align="center" colspan="4"><b>tutti gli SMS sono stati processati! </b></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>	
<%} else { %>
	<tr>
		<td>&nbsp;</td>
	</tr>	
	<tr>
		<td class="titolo" align="center" colspan="4"><b>Non è stato possibile inviare gli SMS per i seguenti lavoratori: </b></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>

	<%
	//scorro il vettore per l'esito dell'invio SMS
	for (int i = 0;i<vettRis.size();i++) {
	SourceBean rowVector = (SourceBean)vettRis.get(i);	
		
	String errorcode = "";
	String errorMes = "";
	String errorMes2 = "";		
	errorcode = (String)rowVector.getAttribute("errorCode");	
	errorMes = MessageBundle.getMessage(errorcode.toString());
	
	if (errorcode != null && !errorcode.equals("") && (new Integer(errorcode).intValue()) == MessageCodes.SMS.TESTO_TROPPO_LUNGO) {
		String maxLenghtSms = (String) serviceRequest.getAttribute("MAXLENGTHSMS");
		if (maxLenghtSms==null || maxLenghtSms.equals(""))
			maxLenghtSms="160";
		errorMes = errorMes.replace("%0", maxLenghtSms);
	}
	


	%>
	<tr>
		<td align="left">Cognome: <%=rowVector.getAttribute("STRCOGNOME")%></td>
		<td align="left">Nome: <%=rowVector.getAttribute("STRNOME")%></td>
		<td align="left">Codice Fiscale: <%=rowVector.getAttribute("STRCODICEFISCALE")%></td>
		<td align="left">Cause: <%=errorMes%></td>
	</tr>
	<%}%>
	<tr>
		<td>&nbsp;</td>
	</tr>
<%}%>	
<%}%>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td align="center" colspan="4" ><%	String urlDiLista = (String) sessionContainer.getAttribute("_TOKEN_AGENDA_SMS_LISTALAVORATORI_PAGE");
											if (urlDiLista != null) {
											out.println("<div align=\"center\"><a href=\"#\" onClick=\"goTo('" + urlDiLista + "')\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></div>");
											}%>
		</td>
	</tr>

</table>
<%out.print(htmlStreamBottom);%>
</body>
</html>


