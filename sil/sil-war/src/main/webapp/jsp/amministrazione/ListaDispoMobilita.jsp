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
<% 
String progressivo = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGMOBILITAISCR");
Vector dispoRows = null;


dispoRows = serviceResponse.getAttributeAsVector("M_MobListaDisponibilita.ROWS.ROW");
boolean canDelete = false;
//NOTE: Attributi della pagina (pulsanti e link) 
PageAttribs attributi = new PageAttribs(user, "MobGestioneDisponibilitaPage");	 
canDelete = attributi.containsButton("RIMUOVI");

%>
<html>
<head>

<title>
</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />

</head>
<body>
<%
if (dispoRows.size() > 0) {%>
	<p>
	<af:list moduleName="M_MobListaDisponibilita" 
		canDelete="<%=canDelete ? \"1\" : \"0\"%>"
		skipNavigationButton="1" />
	</p>
<%}
else {%>
	<center>
	<table class="lista" align="center">
    <tr><td align="center"><b>Non &egrave; stata indicata nessuna disponibilit&agrave;</b></td></tr>
    </table>
    </center>
<%}%>
<br>
<center>
<table>
<tr>
<td>
<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.parent.close();"/>
</td>
</tr>
</table>
</center>
</body>
</html>