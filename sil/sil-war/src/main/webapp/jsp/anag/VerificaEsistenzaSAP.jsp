<%@page import="it.eng.sil.pojo.yg.sap.LavoratoreType"%>
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

	Boolean updateResponse = (Boolean) serviceResponse.getAttribute("M_SAPCALLVERIFICAESISTENZASAP.UPDATE");
	boolean update = updateResponse != null ? updateResponse.booleanValue() : false;
	String codMinSap = (String) serviceResponse.getAttribute("M_SAPCALLVERIFICAESISTENZASAP.CODMINSAP");
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
    <title>Verifica esistenza SAP</title>
    <link rel="stylesheet" type="text/css" href="../../css/stiliCoop.css"/>
    <af:linkScript path="../../js/" />
</head>

<body class="gestione" onload="rinfresca();reloadSap();">
    <p class="titolo"><br><b>Verifica esistenza SAP</b></p>
	
	<%out.print(htmlStreamTop);%>
    <center>
        <font color="red">
            <af:showErrors/>
        </font>
        <font color="green">
            <af:showMessages prefix="M_SapCallVerificaEsistenzaSap"/>
        </font>
    </center>
    <%out.print(htmlStreamBottom);%>
	
	<center><input class="pulsante" type = "button" name="chiudi" value="Chiudi" onclick="window.close()"/></center>
</body>

</html>
