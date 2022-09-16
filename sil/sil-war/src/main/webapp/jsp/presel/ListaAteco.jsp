
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


  boolean codateco=(boolean) serviceRequest.containsAttribute("codateco");

  if (codateco) {
    moduleName="M_CercaAtecoCODATECO";
  } else {
    moduleName="M_CercaAtecoDESATECO";
  }
  
  boolean flgTornaLista = serviceRequest.containsAttribute("TORNALISTA");  
%>



<html>
<head>
<title>Attività Impresa-Ricerca avanzata</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />

<SCRIPT TYPE="text/javascript">
<!--
function AggiornaForm (codAteco, strAteco, strTipoAteco) {

	if (isScaduto(strAteco)) {
		alert("Non è possibile inserire una Attività scaduta");
		window.opener.document.Frm1.codAteco.value = "";
	} else {
  
		  window.opener.document.Frm1.codAteco.value = codAteco;
	    window.opener.document.Frm1.codAtecoHid.value=codAteco;
			window.opener.document.Frm1.strAteco.value = strAteco.replace('^', '\'');
	  	window.opener.document.Frm1.strTipoAteco.value = strTipoAteco.replace('^', '\'');
	  	window.close();
	}
}

function ricercaAvanzataAteco() {
  window.location="AdapterHTTP?PAGE=RicercaAtecoAvanzataPage";
}


function isScaduto(str) {
	if (opener.flagRicercaPage != "S") { // Se NON provengo da una page di ricerca ammetto solo quelli non scaduti.
		if (str.substring(str.length-9) == "(scaduto)") {
			return true;
		}
	}
	return false;
}


-->
</SCRIPT>
<%
	Integer currentPage = (Integer) serviceResponse.getAttribute(moduleName+".ROWS.CURRENT_PAGE");
	Integer numPages = (Integer) serviceResponse.getAttribute(moduleName+".ROWS.NUM_PAGES");
	Vector rows = serviceResponse.getAttributeAsVector(moduleName+".ROWS.ROW");
    if (rows.size()==1 && currentPage.intValue()==1 && numPages.intValue()==1) {
    	SourceBean row = (SourceBean)rows.get(0);    
        String codAteco = (String)row.getAttribute("CODATECO");
        String strAteco= (String)row.getAttribute("STRATECO");
        String strTipoAteco= (String)row.getAttribute("STRTIPOATECO");
		%>        
      <script type="text/javascript">
        AggiornaForm("<%=codAteco%>","<%=strAteco%>", "<%=strTipoAteco%>");
      </script>
    	
<%  }%>
</head>
<body class="gestione">


<af:list moduleName="<%= moduleName%>" jsSelect="AggiornaForm" />
<br/>
<center>
<table>
<tr>
<td colspan="2">
<% if (flgTornaLista) {%>
	<input type="button" class="pulsante" name="torna" value="Torna alla ricerca" onClick="javascript:ricercaAvanzataAteco();"/>
<%}%>
	<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/>
</td>
<tr>
</table>
</center>
</body>
</html>
