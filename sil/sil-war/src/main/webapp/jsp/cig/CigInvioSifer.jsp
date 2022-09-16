<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>

<%@ page
	import="com.engiweb.framework.base.*,java.lang.*,java.text.*,java.util.*,it.eng.sil.util.*,it.eng.afExt.utils.StringUtils,it.eng.sil.security.*"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%
	String _funzione = (String) serviceRequest.getAttribute("cdnFunzione");
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
	 String dataSiferDa = StringUtils.getAttributeStrNotNull(serviceRequest, "INIZIO");
	 String dataSiferA =  StringUtils.getAttributeStrNotNull(serviceRequest ,"FINE");
	 SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	 if(StringUtils.isEmptyNoBlank(dataSiferDa)){
		 Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			Date inizio = new Date(cal.getTimeInMillis());
		 dataSiferDa = sdf.format(inizio);
	 }
	 if(StringUtils.isEmptyNoBlank(dataSiferA)){
		 dataSiferA = sdf.format(new Date());
	 }
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Invio Ws Sifer</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/" />
</head>
<body>

<font color="red"><af:showErrors /></font>
<af:showMessages prefix="M_CigInvioSifer" />
<%
	String xmlGenerato = (String) serviceResponse.getAttribute("M_CigInvioSifer.xmlGenerato");
	out.print(htmlStreamTop);
%>
<af:form action="AdapterHTTP" method="POST" name="Frm1"
	onSubmit="checkCampi()">
	<input type="hidden" name="PAGE" value="CigInvioSiferPage" />
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
	<table class="main">
		<tr>
			<td class="etichetta">Codice Fiscale</td>
			<td class="campo"><af:textBox title="Codice Fiscale"
				validateOnPost="true" classNameBase="input" name="CODICEFISCALE" /></td>
		</tr>
		<tr>
			<td>Oppure</td>
		</tr>
		<tr>
			<td class="etichetta">Data Iniziale</td>
			<td class="campo"><af:textBox title="Data Iniziale"
				validateOnPost="true" type="date" classNameBase="input"
				name="INIZIO" required="true" /></td>
		</tr>
		<tr>
			<td class="etichetta">Data Finale</td>
			<td class="campo"><af:textBox title="Data Finale"
				validateOnPost="true" type="date" classNameBase="input" name="FINE"
				required="true" /></td>
		</tr>
		<tr>
			<td>
			<button name="Invia" type="submit" class="input">Invia</button>
			</td>
		</tr>
	</table>
</af:form>
<!-- se esiste posto qui l'xml prodotto -->
<h1>Xml Prodotto</h1>
<hr>
<code> <%--=xmlGenerato--%> <br>

<%=StringUtils.formatValue4Html(xmlGenerato)%> </code>
<hr>
<%
	out.print(htmlStreamBottom);
%>
</body>
</html>
