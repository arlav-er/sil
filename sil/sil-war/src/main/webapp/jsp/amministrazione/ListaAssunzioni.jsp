<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 
  String moduleName="";
 
   boolean codicAss=(boolean) serviceRequest.containsAttribute("codiceAss");

  if (codicAss) {
    moduleName="M_CercaAssunzioneCODASS";
  } else {
    moduleName="M_CercaAssunzioneDESCRIZIONE";
  }
%>



<html>
<head>
<title>Ricerca Assunzioni</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />
<SCRIPT language="Javascript" type="text/javascript">

function AggiornaForm(codiceAss, desrAss) {
	  
	  window.opener.document.Frm1.codAss.value = codiceAss;
	  window.opener.document.Frm1.codAssHid.value=codiceAss;
	  window.opener.document.Frm1.strAss.value = desrAss.replace('^', '\'');	  
	  window.close();
}

</SCRIPT>
</head>
<body class="gestione">
<%
	Integer currentPage = (Integer) serviceResponse.getAttribute(moduleName+".ROWS.CURRENT_PAGE");
	Integer numPages = (Integer) serviceResponse.getAttribute(moduleName+".ROWS.NUM_PAGES");
    Vector rows = serviceResponse.getAttributeAsVector(moduleName+".ROWS.ROW");
    if (rows.size()==1 && currentPage.intValue()==1 && numPages.intValue()==1) {           
		SourceBean row = (SourceBean)rows.get(0);    
        String codAss = (String)row.getAttribute("CODTIPOASS");
        String strdescrizione = (String)StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONE");
        strdescrizione=strdescrizione.replace('\'', '^');    
        
        StringBuffer jsCommand = new StringBuffer();
        jsCommand.append("AggiornaForm('");
        jsCommand.append(codAss);
        jsCommand.append("','");
        jsCommand.append(strdescrizione);        
        jsCommand.append("','");
        
}%> 
<af:list moduleName="<%= moduleName%>" jsSelect="AggiornaForm" />
<br/>
<table width="100%">
<tr>
<td align="center">
	<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/>
	
</td>
<tr>
</table>


</body>
</html>