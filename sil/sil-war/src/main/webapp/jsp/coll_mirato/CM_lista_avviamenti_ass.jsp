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
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<script language="Javascript">

function AggiornaFormAssunzione(codContratto, codmansione, strmansione){
	window.opener.document.Frm1.codStato.value = "AVV";
	window.opener.document.Frm1.codContratto.value = codContratto;
    window.opener.document.Frm1.CODMANSIONE.value = codmansione;
    window.opener.document.Frm1.DESCMANSIONE.value = strmansione;
    window.close();
}
    
</script>
</head>

<body class="gestione">

<%out.print(htmlStreamTop);%>
	<p align="center">
    	<af:list moduleName="CM_AVVIAMENTI_ASS" skipNavigationButton="1"
        		jsSelect="AggiornaFormAssunzione"/>          
    </p>
	<p>
		&nbsp;
	</p>
	<p align="center">
		<input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
  	</p>
<%out.print(htmlStreamBottom);%>

</body>
</html>
