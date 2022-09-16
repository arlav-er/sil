<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*"%>
                 
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<html>
<head>
<title>
Ricerca Avanzata Area Professionale
</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 <SCRIPT TYPE="text/javascript">
 function selectTipoQualifica_onClick(objTipo) {
	if (objTipo.value != "") {
    	var url = "AdapterHTTP?PAGE=CurrAlberoQualifichePage&padre="+objTipo.value;
      	setWindowLocation(url);
    }
 }
 </SCRIPT>
</head>
<body onload="rinfresca();">
<af:form name="FrmQualifica" method="POST" action="AdapterHTTP">
<br>
<center>
<p class="titolo"><b>Area Professionale - ricerca avanzata</b></p>
<table class="lista" align="center">
<tr>
	<td>Ricerca per Albero-Indicare il primo livello</td>
	<td>
    	<af:comboBox title="Tipo" name="codPadreQualifica" moduleName="M_ListPadreQualificheSRQ"/>
    	
    	<A href="javascript:selectTipoQualifica_onClick(document.FrmQualifica.codPadreQualifica);">
	      	<img src="../../img/binocolo.gif" alt="Cerca">
	    </A>
    	
    </td>
</tr>
</table>
</center>
</af:form>
<br>
<center>
<table>
<tr><td align="center">
<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/>
</td>
</tr>
</table>
</center>
</body>
</html>