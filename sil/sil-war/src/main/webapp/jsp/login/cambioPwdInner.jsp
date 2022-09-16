<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file ="../global/noCaching.inc" %>
<%@ include file ="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.util.Linguette,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);

// Inizio della pagina vera e propria
BigDecimal nGiorniPwdScaduta = (BigDecimal) serviceResponse.getAttribute("RECUPERAPWD.rows.row.NUMGGPWDSCADUTA");
if (nGiorniPwdScaduta == null) nGiorniPwdScaduta = new BigDecimal(0D);

String updateOk = (String) serviceResponse.getAttribute("cambioPwdSalva.update_OK");

boolean esitoOK = false;

if ((updateOk != null) && updateOk.equalsIgnoreCase("true")) {
	esitoOK = true;
}

%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">

<%-- NO REDIRECT QUI --%>

<af:linkScript path="../../js/" />

<%@ include file ="./cambioPwdJS.inc" %>

</head>
<body class="gestione" onLoad="rinfresca()">
<br />
<p class="titolo">Cambio della password</p>

<af:showMessages prefix="cambioPwdSalva" />
<af:showErrors />

<%if (!esitoOK) {%>

<br />

<af:form method="POST" action="AdapterHTTP" name="Frm1"
	onSubmit="valida()">
	<af:textBox type="hidden" name="PAGE" value="cambioPwdInnerSalvaPage" />

	<%= htmlStreamTop %>
	<table class="main">

		<%@ include file ="./cambioPwd.inc" %>

		<tr>
			<td>&nbsp;</td>
		</tr>

		<tr>
			<td></td>
			<td nowrap class="campo"><input class="pulsante" type="submit"
				name="SALVA" VALUE="Salva" /> <input class="pulsante" type="reset"
				VALUE="Annulla" /></td>
		</tr>
	</table>
	<%= htmlStreamBottom %>

</af:form>

<%}else{%>
	<%= htmlStreamTop %>
	<table class="main">
		<tr>
			<td>La password &egrave; stata cambiata</td>
		</tr>
	</table>
	<%= htmlStreamBottom %>
<%}%>



</body>
</html>
