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
	String  titolo = "Elenco SIL con conflitti sul lavoratore";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	boolean isConflittoUnificazione = false;
	

	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	
	PageAttribs attributi = new PageAttribs(user, _page);
	
	// Recupero la ROW contenuta nella RISPOSTA DEL MODULO
	Vector conflittiRows = serviceResponse.getAttributeAsVector("M_GetConflittiUnificazione.ROWS.ROW");
	
	if (conflittiRows != null && conflittiRows.size() > 0){
		isConflittoUnificazione = true;
	}
	
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
<link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>

<af:linkScript path="../../js/"/>

<script type="text/Javascript">
<%//Genera il Javascript che si occuperà di inserire i links nel footer
if (!cdnLavoratore.equals("")){
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);	
}
%>
</script>
</head>

<body class="gestione">

<%
InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
testata.show(out);
Linguette l  = new Linguette(user, Integer.parseInt(cdnfunzioneStr), _page, new BigDecimal(cdnLavoratore));
l.show(out);
%>
<af:form dontValidate="true">
<table class="main">
<af:list moduleName="M_GetConflittiUnificazione"/>
</table>
</af:form>
</body>
</html>
