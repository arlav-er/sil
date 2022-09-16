<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<% 
  String moduleName="";

  boolean flgFrequente=(boolean) serviceRequest.containsAttribute("flgFrequente");
  boolean flagCM=(boolean) serviceRequest.containsAttribute("flagCM");

  if(!flagCM) moduleName="M_CercaMansioneDESMANSIONE";
  else moduleName="M_CercaMansioneDESMANSIONE_CM";
  
%>

<html>
<head>
<title>
Mansioni-Ricerca avanzata
</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />
<SCRIPT TYPE="text/javascript">
<!--
function AggiornaForm (codMansione, desMansione, strTipoMansione) {

  <% if (!serviceRequest.containsAttribute("RICERCA2") )  {%>
  
	  window.opener.document.Frm1.CODMANSIONE.value = codMansione;
    window.opener.document.Frm1.codMansioneHid.value=codMansione;
		window.opener.document.Frm1.DESCMANSIONE.value = desMansione.replace('^', '\'');
  	window.opener.document.Frm1.strTipoMansione.value = strTipoMansione.replace('^', '\'');

    <%}  else {
    // sono giunto a questa pagina da una pagina che gestisce in modo autonomo la valorizzazione dei campi
    %>    
    window.opener.setValues(codMansione, desMansione.replace('^', '\''), strTipoMansione.replace('^', '\''));
    <%}%>
    if (window.opener.document.Frm1.paginaMansione != null){
      window.opener.visualizzaTipoDescrMansione("inline");
    }    
   	window.close();
}

-->
</SCRIPT>
</head>
<body class="gestione">
<af:list moduleName="<%=moduleName%>" jsSelect="AggiornaForm" />
<br>
<p align="center">
<input type="button" class="pulsanti" name="chiudi" value="chiudi" onClick="javascript:window.close();"/>
</p>
<%@ include file="/jsp/MIT.inc" %>
</body>
</html>