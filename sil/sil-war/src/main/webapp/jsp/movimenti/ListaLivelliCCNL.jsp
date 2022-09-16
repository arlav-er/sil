<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String  titolo = "Scelta Livello";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">

function AggiornaForm(numlivelloParam) {
	if (window.opener.document.Frm1.numLivelloOldHid != null) {
		window.opener.document.Frm1.numLivelloOldHid.value = window.opener.document.Frm1.numLivello.value;
	}
	
	window.opener.document.Frm1.numLivello.value = numlivelloParam;

	if (window.opener.document.Frm1.numLivelloCes != null) {
		window.opener.document.Frm1.numLivelloCes.value = numlivelloParam;
	}

	if (window.opener.document.Frm1.numLivelloOldHid != null) {
		if(window.opener.document.Frm1.numLivello.value != window.opener.document.Frm1.numLivelloOldHid.value){
			window.opener.document.Frm1.decRetribuzioneAnn.value = "";//sbianca compenso annuale se cambia il livello		
		}
	}
	window.close();
}

</script>
</head>

<body class="gestione">

	<af:showErrors />
	<af:showMessages prefix="M_RicercaLivelloFromCCNL"/>
	<af:form dontValidate="true">

	<p class="titolo"><%= titolo %></p>
	<table class="main">
    
    <%-- STAMPA LA LISTA --%>
	<af:list moduleName="M_RicercaLivelloFromCCNL" jsSelect="AggiornaForm"/>

	</table>


	<%-- BOTTONI --%>
	<table class="main">
	<tr>
		<td>
			<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();" />
		</td>
	</tr>
	</table>

	</af:form>

</body>
</html>
