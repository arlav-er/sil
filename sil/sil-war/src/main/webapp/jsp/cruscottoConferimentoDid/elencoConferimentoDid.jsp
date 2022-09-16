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
	String  titolo = "Elenco Conferimenti DID";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"CDNLAVORATORE");
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
<af:showMessages prefix="M_CCD_ElencoConferimentiDid"/>


<af:form dontValidate="true">

<p class="titolo"><%= titolo %></p>


<table class="main">
    
    <%-- STAMPA LA LISTA --%>
	<af:list moduleName="M_CCD_ElencoConferimentiDid"/>

</table>


<%-- BOTTONI --%>
<table class="main">

	<tr>
		<td>
			<input type="button" class="pulsante" name="chiudi" value="Chiudi"
					onclick="window.close();" />
		</td>
	</tr>
</table>

</af:form>

</body>
</html>
