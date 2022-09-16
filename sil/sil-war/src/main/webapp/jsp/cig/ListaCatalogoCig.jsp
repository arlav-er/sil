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


<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Lista Catalogo</title>

<script language="Javascript">
	
	function AggiornaFormCatalogo(numidproposta,numrecid,strcodiceproposta,strsede){
		window.opener.document.Frm1.numidproposta.value = numidproposta;
		window.opener.document.Frm1.numrecid.value = numrecid;
		window.opener.document.Frm1.strcodiceproposta.value = strcodiceproposta;
    	window.opener.document.Frm1.strsede.value = strsede;
    	window.close();
	}
    
</script>
</head>

<body class="gestione">

	<af:list moduleName="M_ListaCatalogoCig" jsSelect="AggiornaFormCatalogo"/>
    
    <table class="main">
  		<tr><td>&nbsp;</td></tr>
  		<tr>
    		<td align="center">
      			<input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
    		</td>
  		</tr>
  	</table>

</body>
</html>
