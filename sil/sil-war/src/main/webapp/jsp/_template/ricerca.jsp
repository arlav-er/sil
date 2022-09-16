<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.afExt.utils.*,
			it.eng.sil.security.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String  titolo = "xxx";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");

	// CONTROLLO PERMESSI SULLA PAGINA
	// PageAttribs attributi = new PageAttribs(user, _page);

	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>
<script language="Javascript" src="../../js/xxx.js"></script>
<script language="Javascript">

	//...

</script>
</head>

<body class="gestione">

<af:showErrors />

<p class="titolo"><%= titolo %></p>

<af:form name="form" action="AdapterHTTP" method="POST">
<input type="hidden" name="PAGE" value="ListXxxPage" />
<input type="hidden" name="cdnFunzione" value="<%=cdnfunzione%>" />

<%= htmlStreamTop %>
<table class="main">

	<tr>
		<td colspan="2">
			<div class="sezione2">Nome sezione... </div>
		</td>
	</tr>

	<tr>
		<td class="etichetta">xxx</td>
		<td class="campo">

			<%--
				NB: impstando gli attributi "validateOnPost" e "required" dei singoli
				"af:tag" si possono fare i controlli di validazione di base.
			--%>
		
			<af:textBox name="strxxx" type="text"
						value=""
						size="40" maxlength="101"
						required="false" />

			<af:textBox name="datxxx" type="date"
						value=""
						size="11" maxlength="10"
						validateOnPost="true"
						required="false" />

			<af:comboBox name="cdnxxx"
						moduleName="ComboXxxModule" 
						addBlank="true"
						required="false" />
		</td>
	</tr>

	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>

	<tr>
		<td colspan="2">
			<span class="bottoni">
				<input type="submit" class="pulsanti" name="cerca" value="Cerca" />
				&nbsp;&nbsp;
				<input type="reset" class="pulsanti" name="annulla" value="Annulla" />
			</span>
		</td>
	</tr>

</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>
