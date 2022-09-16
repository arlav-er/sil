<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*,
                 it.eng.sil.util.*"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);

  String moduleName="";

  boolean isCodcpi=(boolean) serviceRequest.containsAttribute("codcpi");

  if (isCodcpi) {
    moduleName="M_RicercaCodCPI";
  } else {
    moduleName="M_RicercaDesCPI";
  }
%>

<!-- @author riccardi
	 Modificata AggiornaForm aggiungendo un parametro nella request indicante la provenienza di contesto applicativo poichè:
	 Es.: Nel caso del trasferimento di un lavoratore i campi da aggiornare sono denominati diversamente
	 P.S.: Il parametro aggiunto potrebbe essere utilizzato anche in altri casi (se non sono tanti) 
-->

<html>
<head>
<title>Ricerca CPI</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />
<SCRIPT TYPE="text/javascript">

<!--
function AggiornaForm (Codcpi, Strdescrizione) {
	  <%if (serviceRequest.containsAttribute("provenienza")&& serviceRequest.getAttribute("provenienza").equals("trasferimento")) {%>
	  		var codCpiNuovo = window.opener.document.Frm1.codCPI.value;
	  		var strCpiNuovo = window.opener.document.Frm1.strCPI.value;
	  		if (codCpiNuovo == Codcpi || strCpiNuovo == Strdescrizione.replace('^', '\'')) {
	  			window.opener.document.Frm1.CODCPIORIG.value = "";
	  			window.opener.document.Frm1.DESCRCPIORIG.value = "";
	  			if (window.opener.document.Frm1.msgIRLav.value != "" && window.opener.document.Frm1.msgIRLav.value != " ") {
	  				window.opener.document.Frm1.msgIRLav.value = "Il CPI di provenienza non può coincidere con il nuovo CPI competente";
	  				window.opener.document.Frm1.msgXIR.value = "Procedere selezionando il CPI manualmente";
	  			} else {
	  				window.opener.document.Frm1.msgIRLav.value = "Il CPI di provenienza non può coincidere con il nuovo CPI competente";
	  				window.opener.document.Frm1.msgXIR.value = " ";
	  			}
	  			window.opener.document.Frm1.isInterProvincia.value = "SI";
	  			window.opener.document.Frm1.isInterRegione.value = "NO";
	  			window.opener.document.Frm1.privacy.style.display = "none";
	  			window.opener.document.Frm1.docIdent.style.display = "none";
	  			window.opener.document.Frm1.CODMONOTIPOORIG.value = "I";
	  			window.opener.document.Frm1.Coop.value = "Invio cartaceo";
      			window.opener.document.Frm1.imgCoop.src = "../../img/text.gif";
				window.close();
	  		} else {
	  			window.opener.document.Frm1.CODCPIORIG.value = Codcpi;
      			window.opener.document.Frm1.DESCRCPIORIG.value = Strdescrizione.replace('^', '\'');
      			var s= "AdapterHTTP?PAGE=CheckInCoopCpiPage&codCpi=" + Codcpi;
      			window.open(s,"InCoopCPI", 'toolbar=0, scrollbars=1');
      		}
	  <%} else {%>
      		window.opener.document.Frm1.codCPI.value = Codcpi;
      		window.opener.document.Frm1.strCPI.value = Strdescrizione.replace('^', '\'');
      		window.close();
      <%}%>
}

-->
</SCRIPT>
</head>

<body class="gestione">
<script>window.focus();</script>
<%
    Vector rows = serviceResponse.getAttributeAsVector(moduleName+".ROWS.ROW");
    if (rows.size()==1) {   
        SourceBean row = (SourceBean)rows.get(0);
        String codcpi = (String)row.getAttribute("CODCPI");
        String strdescrizione = (String)row.getAttribute("STRDESCRIZIONE");    
        StringBuffer jsCommand = new StringBuffer();
        jsCommand.append("AggiornaForm('");
        jsCommand.append(codcpi);
        jsCommand.append("','");
        strdescrizione = strdescrizione.replace('\'', '^');
        jsCommand.append(strdescrizione);        
        jsCommand.append("');");
%>
    <script><%=jsCommand.toString()%></script>
<%  } else if (rows.size()!=0) {%>
    <af:list moduleName="<%= moduleName%>" jsSelect="AggiornaForm" />
<%  } else {%>
      <br/><br/>
      <%out.print(htmlStreamTop);%>
      <table  class="main" border="0">
      <tr><td colspan="2">&nbsp;</td></tr>
      <tr><td colspan="2"><strong>Nessun comune trovato per il codice CPI dato.</strong></td></tr>
      <tr><td colspan="2"><strong>Controllare che il codice sia corretto</strong></td></tr> 
      <tr><td colspan="2">&nbsp;</td></tr>
      <tr><td colspan="2"><input type="button" class="pulsante" name="chiudi" value="chiudi" onClick="window.close(); return false;"/></td></tr>
      <tr><td colspan="2">&nbsp;</td></tr>
      <tr><td colspan="2">&nbsp;</td></tr>
      </table>
      <%out.print(htmlStreamBottom);%>
<%  } %>
</body>
</html>