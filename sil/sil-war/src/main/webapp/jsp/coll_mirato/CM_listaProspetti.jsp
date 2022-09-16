<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

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
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspListaPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
		
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String prgAzienda    = (String)serviceRequest.getAttribute("prgAzienda");
	String prgUnita = (String)serviceRequest.getAttribute("prgUnita");				
	PageAttribs attributi = new PageAttribs(user, "CMProspListaPage");
	boolean canInsert =	false;
	canInsert =	attributi.containsButton("INSERISCI");
%>

<html>
<head>
<title>Lista delle Convenzioni</title>
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

		var url = "AdapterHTTP?PAGE=CMProspRicercaPage" + "&cdnfunzione=<%=cdnfunzione%>";
		<%if (prgAzienda!=null || !("").equals(prgAzienda)) {%>
			url+="&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>";
		<%}%>
		setWindowLocation(url);
	}
	<% 
	if (prgAzienda!=null || !("").equals(prgAzienda)) {
    	//Genera il Javascript che si occuperà di inserire i links nel footer
    	attributi.showHyperLinks(out, requestContainer,responseContainer,"");
    }
    %>
</script>
<script language="Javascript">
<% if (!prgAzienda.equals("") && !prgUnita.equals("")) { %>
	window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
<% } %>
</script>

</head>

<body class="gestione" onload="rinfresca()">

<af:showErrors />

<af:list moduleName="CMProspListaModule" />

<af:form name="Frm1" method="POST" action="AdapterHTTP">
	<input type="hidden" name="PAGE" value="CMProspDettPage"/>
	<input type="hidden" name="BACK_PAGE" value="CMProspListaPage"/>   
	<input type="hidden" name="CDNFUNZIONE" value="<%=cdnfunzione%>"/>
	<input type="hidden" name="nuovo" value="true" />	
	<input type="hidden" name="prgAzienda" 	value="<%=prgAzienda==null ? "" : prgAzienda%>" />	
	<input type="hidden" name="prgUnita" value="<%=prgUnita==null ? "" : prgUnita%>" />	
	<table class="main"> 
		<%
		if (canInsert) {
		%> 
			<tr>
				<td>
					<input type="submit" class="pulsante" name="inserisci" value="Nuovo Prospetto" />
				</td>
			</tr>
		<%
		}
		%>
		<tr>
			<td>
				<input type="button" class="pulsante" name="ricerca" value="Torna alla ricerca" onclick="goBackRicerca()" />
			</td>
		</tr>
	</table>

</af:form>

</body>
</html>