<%@ page contentType="text/html;charset=utf-8" %>

<%@ page import=" com.engiweb.framework.base.*,
					it.eng.sil.security.*,
					it.eng.sil.util.*,
					it.eng.afExt.utils.*,
					java.util.*,
					java.math.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<% 

String prgDocumentoFirmato = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "prgDocumento");
String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");

//String queryString = "prgDocumento="+prgDocumentoFirmato+"&cdnLavoratore="+cdnLavoratore+"&cdnFunzione="+cdnfunzione + "&page=DettagliDocumentoPage";
String queryString =  SourceBeanUtils.getAttrStrNotNull(serviceRequest,"QUERY_STRING");

%>

<%@ include file="_apriGestioneDoc.inc"%>

<%@ taglib uri="aftags" prefix="af" %>

<%
	//String _page = "DocumentiAssociatiPage";
	String  _page          = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	
	
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
	boolean lookLavoratore   = SourceBeanUtils.getAttrBoolean(serviceRequest, "lookLavoratore", false);
	boolean lookAzienda      = SourceBeanUtils.getAttrBoolean(serviceRequest, "lookAzienda", false);
	String  contesto         = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "contesto");
	String  pagina           = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"pagina");

	String  strChiaveTabella = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"strChiaveTabella");
	String  goBackListPage   = _page;
	
	

	String frameName         = SourceBeanUtils.getAttrStr(serviceRequest, "FRAME_NAME", "main");
	// e' nome del frame da cui e' partita la richiesta del documento di identita', 
	// dichiarazione di immediata disponibilita' oppure il patto 150. In questo caso il refresh deve essere della popUp
	// aperta dalla pagina del patto e non del frame "main", come nel caso della did.

	String  titolo = "";
	
	
	
	
%>

<html>
<head>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>



</head>

<body class="gestione">

<af:error />

<af:form dontValidate="true">

<table class="main" width="100%">

    <tr><td>&nbsp;</td></tr>
    <tr><td><p class="titolo"><%=titolo%></p></td></tr>
    <tr><td>&nbsp;</td></tr>	

</table>


	<script language="Javascript">
		visualizzaDocumento('DOWNLOAD','','<%=prgDocumentoFirmato%>');
	</script>

<table class="main" cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr><td width="33%">&nbsp;</td>
		
		<td width="33%">
		</td> 
	</tr>
</table>

<br/>

</af:form>

</body>
</html>
