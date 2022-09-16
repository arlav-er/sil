<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 
  String moduleName="";

  String campoCodice = (String)serviceRequest.getAttribute("retcod");
  String campoDesc = (String)serviceRequest.getAttribute("retnome");
  String campoMotivo = (String)serviceRequest.getAttribute("retmotivo");
  
  boolean isCodice=(boolean) serviceRequest.containsAttribute("codiceReiezione");

  if (isCodice) {
    moduleName="M_RicercaCodiceReiezione";
  }
%>



<html>
<head>
<title>Ricerca Reiezione</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />
<SCRIPT language="Javascript" type="text/javascript">

</SCRIPT>
<script language="Javascript">
     <% 

  
      %>
</script>
<SCRIPT TYPE="text/javascript">
<!--
function AggiornaForm (codReiezione, strdescrizione, strmotivodomanda) {

	if (isScaduto(strdescrizione)) {
		alert("Non è possibile inserire un codice reiezione scaduto");
		window.opener.document.Frm1.<%=campoCodice%>.value = "";
	} else {
	  window.opener.document.Frm1.<%=campoCodice%>.value = codReiezione;
	  window.opener.document.Frm1.<%=campoDesc%>.value = strdescrizione.replace('^', '\'');
	  window.opener.document.Frm1.<%=campoMotivo%>.value = strmotivodomanda.replace('^', '\'');
	  window.close();
	  
	}
}

function isScaduto(str) {
	if (str.substring(str.length-9) == "(scaduto)") {
		return true;
	}
	return false;
}

-->
</SCRIPT>
</head>
<body class="gestione">
<%
	Integer currentPage = (Integer) serviceResponse.getAttribute(moduleName+".ROWS.CURRENT_PAGE");
	Integer numPages = (Integer) serviceResponse.getAttribute(moduleName+".ROWS.NUM_PAGES");
    Vector rows = serviceResponse.getAttributeAsVector(moduleName+".ROWS.ROW");
    if (rows.size()==1 && currentPage.intValue()==1 && numPages.intValue()==1) {           
		SourceBean row = (SourceBean)rows.get(0);    
        Object codReiezione = row.getAttribute("codReiezione");
        String strdescrizione = (String)StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONE");
        strdescrizione=strdescrizione.replace('\'', '^');
        String strMotivoReiezione = (String)StringUtils.getAttributeStrNotNull(row, "strmotivodomanda");
        strMotivoReiezione=strMotivoReiezione.replace('\'', '^');
        StringBuffer jsCommand = new StringBuffer();
        jsCommand.append("AggiornaForm('");
        jsCommand.append(codReiezione.toString());
        jsCommand.append("','");
        jsCommand.append(strdescrizione);
        jsCommand.append("','");
        jsCommand.append(strMotivoReiezione);
        jsCommand.append("');");
        System.out.println(jsCommand.toString());
                 
%>
	<script><%=jsCommand.toString()%></script>
	
		
	
<%  } else {%>
<af:list moduleName="<%= moduleName%>" jsSelect="AggiornaForm" />
<br/>
<table width="100%">
<tr>
<td align="center">
	<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/>
</td>
<tr>
</table>
<%  } %>

</body>
</html>