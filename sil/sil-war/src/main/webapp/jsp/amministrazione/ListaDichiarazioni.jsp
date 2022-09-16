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
	String  titolo = "lista dichiarazioni/attestazioni";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	//
    InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    testata.setSkipLista(true);
	PageAttribs attributi = new PageAttribs(user, _page);	
	boolean canInsert = attributi.containsButton("INSERISCI");
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
	
		var url = "AdapterHTTP?PAGE=InsDichiarazionePage" +		
						"&MODELLIDAVISUALIZZARE=2&MODELLIDAVISUALIZZARE=3" + 
		          		"&cdnLavoratore=<%=cdnLavoratore%>" + 
						"&cdnfunzione=<%=cdnfunzione%>";
		setWindowLocation(url);
	}

	

</script>
<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  %>
</script>
</head>

<body class="gestione" onload="rinfresca()">

<%testata.show(out);%>
<af:showErrors />



<af:form dontValidate="true" action="AdapterHTTP">

<p class="titolo"><%= titolo %></p>


<table class="main">
    
    <%-- STAMPA LA LISTA --%>
	<af:list moduleName="ListaDichiarazioniLav"/>

</table>


<%-- BOTTONI --%>
<table class="main">

  <% if (canInsert) { %>
	<tr>
		<td>
			<input type="button" class="pulsante" value="Nuova dichiarazione/attestazione"
					onClick="nuovo()" />
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
  <% } %>
	
</table>

</af:form>

</body>
</html>
