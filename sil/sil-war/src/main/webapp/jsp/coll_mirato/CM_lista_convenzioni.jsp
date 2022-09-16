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
	
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
		
	int     cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");

	//Profilatura ------------------------------------------------
 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

  	PageAttribs attributi = new PageAttribs(user, _page); 

  	boolean canInsert = false; 

  	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	} else {
  		canInsert = attributi.containsButton("INSERISCI"); 
  	} 
  	
  	sessionContainer.delAttribute("COMEFROM");
  	sessionContainer.setAttribute("COMEFROM","CL");

	String  prgAzienda    = (String)serviceRequest.getAttribute("prgAzienda");
	String prgUnita = (String)serviceRequest.getAttribute("prgUnita");

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

		var url = "AdapterHTTP?PAGE=CMConveRicercaPage" + "&cdnfunzione=<%=cdnfunzione%>";
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
		<% } else { %>
			window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
		<% }%>		
</script>
</head>

<body class="gestione" onload="rinfresca()">

<af:showErrors />

<af:form name="Frm1" method="POST" action="AdapterHTTP">
	<input type="hidden" name="PAGE" value=	"CMInsConvenzionePage"/>
	<input type="hidden" name="CDNFUNZIONE" value="<%=cdnfunzione%>"/>
	<input type="hidden" name="anno" value=""/>
	<script language="Javascript">
		var miaData = new Date();
		var anno = miaData.getFullYear();
		document.Frm1.anno.value = anno;
	</script>
	<input type="hidden" name="CODSTATOATTO"      	value="NP" />
	<input type="hidden" name="CODSTATOCONV"   value="PO" />
	<input type="hidden" name="goBackListaPage"      value="CMConveListaPage" />
	<% if (prgAzienda != null){ %>
	<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
	<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
	<% } %>

	<af:list moduleName="CM_LISTA_CONVENZIONI" />

	<table class="main">  
		<% if (canInsert) { %>
		<tr>
			<td>
				<input type="submit" class="pulsante" name="nuova" value="Nuova convenzione" />
			</td>
		</tr>
		<% } %>
		<tr>
			<td>
				<input type="button" class="pulsante" name="ricerca" value="Torna alla ricerca" onclick="goBackRicerca()" />
			</td>
		</tr>
	</table>

</af:form>

</body>
</html>