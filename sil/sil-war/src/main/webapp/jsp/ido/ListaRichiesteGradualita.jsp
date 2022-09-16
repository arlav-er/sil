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
	String  titolo = "Lista richieste gradualità";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");

	//Profilatura ------------------------------------------------
 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

  	PageAttribs attributi = new PageAttribs(user, _page); 

  	boolean canInsert = false; 

  	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	} else {
  		canInsert = attributi.containsButton("INSERISCI"); 
  	} 

	String  prgAzienda    = (String)serviceRequest.getAttribute("prgAzienda");
	String  prgUnita      = (String)serviceRequest.getAttribute("prgUnita");
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

		var url = "AdapterHTTP?PAGE=CMRichGradRicercaPage" + "&cdnfunzione=<%=cdnfunzione%>";
		<%if (prgAzienda!=null) {%>
		url+="&prgAzienda=<%=prgAzienda%>";
		url+="&prgUnita=<%=prgUnita%>";
		<%}%>
		setWindowLocation(url);
	}
	<% if (prgAzienda!=null) {
       //Genera il Javascript che si occuperà di inserire i links nel footer
       		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
       }%>
</script>
<script language="Javascript">
	<% if (prgAzienda!=null && prgUnita!=null) { %>
		window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
	<% } %>
</script>
</head>

<body class="gestione" onload="rinfresca()">

<af:showErrors />

<af:form dontValidate="true">

	<af:list moduleName="M_LISTA_RICH_GRAD_CM" />
	<%if (prgAzienda == null && prgUnita == null) {%>
	<table class="main">  
		<tr>
			<td>
				<input type="button" class="pulsante" name="ricerca" value="Torna alla ricerca" onclick="goBackRicerca()" />
			</td>
		</tr>
	</table>
	<%}%>
</af:form>
<% if (canInsert) { %>
<af:form name="Frm2" method="POST" action="AdapterHTTP" >
	<input type="hidden" name="PAGE"              	value="CMRichGradPage" />
	<input type="hidden" name="cdnFunzione"       	value="<%=cdnfunzione%>" />
	<input type="hidden" name="nuovo"             	value="true" />
	<input type="hidden" name="salva"             	value="false" />
	<input type="hidden" name="CODSTATOATTO_P"      value="NP" /> <%-- PA --%>
	<input type="hidden" name="CODSTATORICHIESTA"   value="DA" />
	<input type="hidden" name="goBackListPage"      value="CMRichGradListaPage" />
	<input type="hidden" name="nuovaRichiesta"      value="1" />
	<% if (prgAzienda!=null) { %>
		<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
	<% } %>
	<% if (prgUnita!=null) { %>
		<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
	<% } %>
	<center><input type="submit" class="pulsanti" name="inserisci" value="Nuova Richiesta" /></center>
</af:form>
<% } %>
</body>
</html>