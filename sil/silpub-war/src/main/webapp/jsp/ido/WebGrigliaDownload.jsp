<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 java.math.*,
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.util.*,        
                 it.eng.sil.security.*"%>
            
<%@ taglib uri="aftags" prefix="af"%> 
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String _page = (String) serviceRequest.getAttribute("PAGE");

String titolo="";
Vector vec = serviceResponse.getAttributeAsVector("M_GetFileDownload.ROWS.ROW");
 if(vec != null && vec.size() > 0 ){
	SourceBean rowTitolo = (SourceBean) vec.firstElement();
	titolo = (String) rowTitolo.getAttribute("strDescrizione");
}


%>
<html>
<head>
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
	<af:linkScript path="../../js/"/>
	
	<script language="Javascript">
	
	function doDownload(nomefile,strCodRif){
		
		var url="../../html/moduli/" +strCodRif+ "/" + nomefile;	
		window.open(url,'TheNewpop','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=yes');
	}

</script>

</head>
<body class="gestione">
<br/>

<center><p class="titolo"><%= titolo %></p></center> 

<table class="main" cellspacing="0" cellpadding="0">

<af:form dontValidate="true">
<af:list moduleName="M_GetFileDownload" jsSelect="doDownload"/>
<P align="center"><a href="../../index.html">Home Sezione Pubblica del SIL</a></P>
<br>&nbsp;
</table>
</af:form> 
<%@ include file="/jsp/MIT.inc" %>
</body>
</html>
