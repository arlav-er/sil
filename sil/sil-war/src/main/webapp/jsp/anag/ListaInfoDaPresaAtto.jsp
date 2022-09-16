
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
	String  titolo = "Lista informazioni da presa atto";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = (String)serviceRequest.getAttribute("cdnLavoratore");
	PageAttribs attributi = null;
	if (cdnLavoratore!=null) {		// ho profilato solo la pagina di ricerca
		attributi = new PageAttribs(user, "RicercaInfoDaPresaAttoPage");
	}
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">	


	/*
	 * Torna alla pagina di ricerca
	 */
	function goBackRicerca() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		var url = "AdapterHTTP?PAGE=RicercaInfoDaPresaAttoPage" + "&cdnfunzione=<%=cdnfunzione%>";
		<%if (cdnLavoratore!=null) {%>
		url+="&cdnLavoratore=<%=cdnLavoratore%>";
		<%}%>
		setWindowLocation(url);
	}
	<% if (cdnLavoratore!=null) {
       //Genera il Javascript che si occuperà di inserire i links nel footer
       		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
       }%>
</script>
</head>

<body class="gestione" onload="rinfresca()">

<af:showErrors />

<% 
	if (cdnLavoratore!=null ) { // se sono nel contesto del lavoratore mostro la testata
   		InfCorrentiLav testata = new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
   		testata.setSkipLista(true);
		testata.show(out);
	  }
%>

<af:form dontValidate="true">

	<af:list moduleName="M_LISTA_INFO_DA_PRESA_ATTO" />


<%-- BOTTONI --%>
<table class="main">  

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
