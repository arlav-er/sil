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
String moduleName = "";
String tipoRicerca = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
if (tipoRicerca.equalsIgnoreCase("codice")) {
	moduleName = "M_CercaQualificaCodSRQ";
} 
else {
    if (tipoRicerca.equalsIgnoreCase("descrizione")) {
    	moduleName = "M_CercaQualificaDescSRQ";
    }
}
Vector vettRows = serviceResponse.getAttributeAsVector(moduleName + ".ROWS.ROW");
%>
<html>
<head>
<title>
Area Professionale
</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 <SCRIPT TYPE="text/javascript">
 function AggiornaForm(codice, descrizione, descrizioneTipo) {
 	window.opener.document.Frm1.CODQUALIFICASRQ.value = codice;
	window.opener.document.Frm1.DESCQUALIFICASRQ.value = descrizione.replace('^', '\'');
	window.close();
 }
</SCRIPT>
</head>
<body onload="rinfresca();">
<af:form name="FrmQualifica" method="POST" action="AdapterHTTP">
<af:list moduleName="<%=moduleName%>" jsSelect="AggiornaForm"/>
<br>
<center>
<table>
<tr>
<td>
<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/>
</td>
</tr>
</table>
</center>
</af:form>
<%
SourceBean risultato = (SourceBean) serviceResponse.getAttribute(moduleName);
SourceBean rowsSB = (SourceBean) risultato.getAttribute("ROWS");
int numPages = 0;

if (rowsSB != null && rowsSB.containsAttribute("NUM_PAGES") && !rowsSB.getAttribute("NUM_PAGES").toString().equals("")) {
	numPages = ((Integer) rowsSB.getAttribute("NUM_PAGES")).intValue();
}
if (vettRows != null && vettRows.size() == 1 && numPages == 1) {
	SourceBean rowElem = (SourceBean)vettRows.get(0);
	String codiceElem = rowElem.containsAttribute("codice")?rowElem.getAttribute("codice").toString():"";
	String descElem = rowElem.containsAttribute("descrizione")?rowElem.getAttribute("descrizione").toString():"";
	String tipoElem = rowElem.containsAttribute("descrizioneTipo")?rowElem.getAttribute("descrizioneTipo").toString():"";
%>
	<script type="text/javascript">
		AggiornaForm('<%=codiceElem%>', '<%=descElem%>','<%=tipoElem%>');
	</script>
<%
}
%>
</body>
</html>