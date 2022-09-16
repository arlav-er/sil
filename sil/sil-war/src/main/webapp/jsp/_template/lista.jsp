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
	String  titolo = "xxx";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">

	function nuovo() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
	
		var url = "AdapterHTTP?PAGE=DettaglioXxxPage" +
							"&NUOVO=true" +
							"&cdnfunzione=<%=cdnfunzione%>";
		setWindowLocation(url);
	}


	/*
	 * Torna alla pagina di ricerca
	 */
	function goBackRicerca() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		var url = "AdapterHTTP?PAGE=RicercaXxxPage" +
							"&cdnfunzione=<%=cdnfunzione%>";
		setWindowLocation(url);
	}

</script>
</head>

<body class="gestione">

<af:showErrors />
<af:showMessages prefix="ListXxxModule"/>


<af:form dontValidate="true">

<p class="titolo"><%= titolo %></p>


<table class="main">
    
    <%-- STAMPA LA LISTA --%>
	<af:list moduleName="ListXxxModule"/>

</table>


<%-- BOTTONI --%>
<table class="main">

  <%// if (canInsert) { %>
	<tr>
		<td>
			<input type="button" class="pulsante" value="Nuovo"
					onClick="nuovo()" />
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
  <%// } %>

	<tr>
		<td>
			<input type="button" class="pulsante" name="ricerca" value="Torna alla ricerca"
					onclick="goBackRicerca()" />
		</td>
	</tr>
</table>

</af:form>

</body>
</html>
