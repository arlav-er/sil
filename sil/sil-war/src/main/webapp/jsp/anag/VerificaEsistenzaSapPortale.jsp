<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,it.eng.sil.security.User,it.eng.sil.security.ProfileDataFilter,it.eng.afExt.utils.*,it.eng.sil.pojo.yg.sap.*,it.eng.sil.util.*,java.util.*,java.math.*,java.io.*,it.eng.sil.security.PageAttribs,com.engiweb.framework.security.*"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	PageAttribs attributi = new PageAttribs(user, _current_page);
	
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);

	String codMinSap = (String) serviceResponse.getAttribute("M_SapCallVerificaEsistenzaSapPortale.CODMINSAP");
	if (codMinSap == null) {
		codMinSap = "0";
	}
%>

<script>

function reloadSap() {	
	var url = "AdapterHTTP?PAGE=SapGestioneServiziPage";
	url += "&CDNLAVORATORE=<%=cdnLavoratore%>";
	url += "&CDNFUNZIONE=<%=cdnFunzione%>";
	window.opener.location.replace(url);			
}

</script>

<html>

<head>
    <title>Verifica esistenza SAP Portale</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetailCoop.css"/>
    <af:linkScript path="../../js/" />
</head>

<body class="gestione" onload="rinfresca()">

<af:form method="POST" action="AdapterHTTP" name="Frm1" id="Frm1" dontValidate="true">
	<p align="center">
		<af:showMessages prefix="M_SapCallVerificaEsistenzaSapPortale"/>	
		<af:showErrors/>		
    </p>

	<af:list moduleName="M_SapCallVerificaEsistenzaSapPortale" canInsert="0" skipNavigationButton="1"/>

	<center><input class="pulsante" type="button" name="chiudi" value="Chiudi" onclick="window.close()"/></center>
</af:form>
</body>

</html>