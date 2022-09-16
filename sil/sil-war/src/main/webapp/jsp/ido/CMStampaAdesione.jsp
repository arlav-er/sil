<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="com.engiweb.framework.base.*,
				java.util.*,
				it.eng.sil.security.*,
				it.eng.sil.util.*,
				it.eng.afExt.utils.*" %>
				
				
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%	
	String prgRosa = (String) serviceRequest.getAttribute("PRGROSA");
	String prgNominativo = (String) serviceRequest.getAttribute("PRGNOMINATIVO");
	String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
	
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
	
%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<% String queryString = null; %>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
    
<script language="JavaScript">

function stampa(){
	apriGestioneDoc('RPT_STAMPA_ADESIONE', '&CDNLAVORATORE=<%=cdnLavoratore%>&PRGNOMINATIVO=<%=prgNominativo%>&PRGROSA=<%=prgRosa%>','CMADENUM')
}


function indietro() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

     url="AdapterHTTP?PAGE=CMListaAdesioniPage";     
     url += "&PRGROSA="+"<%=prgRosa%>";
     url += "&CDNLAVORATORE="+"<%=cdnLavoratore%>";
     
     setWindowLocation(url);
 }

</script>
</head>
<body class="gestione" onLoad="rinfresca();stampa();">
<br/>

<af:form name="form1" method="POST" action="AdapterHTTP">
<%= htmlStreamTop %>
<table class="main" width="100%">
	<tr><td width="33%">&nbsp;</td><td width="50%">&nbsp;</td><td width="33%">&nbsp;</td></tr>
	<tr><td colspan="3"><p class="titolo">Elenco stampe</p></td></tr>
	<tr><td>&nbsp;</td></tr>
	
	<tr><td colspan="3">&nbsp;</td></tr>
	<tr><td colspan="3">&nbsp;</td></tr>
</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>
