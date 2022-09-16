<%@page import="it.eng.sil.module.voucher.Properties"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.StringUtils, it.eng.sil.module.voucher.*,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*
                  " %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String _page = (String) serviceRequest.getAttribute("PAGE");
PageAttribs pageAtts = new PageAttribs(user, "PattoAzioniLinguettaPage");
boolean canStampaVoucher = false;
boolean canModify = false;

canStampaVoucher = pageAtts.containsButton("STAMPAVCH");
canModify = canStampaVoucher;

String cdnLav = serviceRequest.containsAttribute("CDNLAVORATORE")?serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String cdnFunzione = serviceRequest.containsAttribute("CDNFUNZIONE")?serviceRequest.getAttribute("CDNFUNZIONE").toString():"";
String prgPattoLavoratore = serviceRequest.containsAttribute("prgPattoLavoratore")?serviceRequest.getAttribute("prgPattoLavoratore").toString():"";

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

String queryString = null;
%>
<html>
<head>
	<%@ include file="../global/fieldChanged.inc" %>
	<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<title>Stampa Cod. Attivazione TDA</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 
 <script language="JavaScript">

 function stampaVoucher() {
	 if (document.Frm1.codAttivazione.value == "") {
		 alert("Non sono presenti titoli di acquisto");
		 window.close();
	 }
	 else {
	 	apriGestioneDoc('RPT_STAMPA_TDA_CODATTIVAZIONE','&cdnLavoratore=<%=cdnLav%>&cdnFunzione=<%=cdnFunzione%>&prgPattoLavoratore=<%=prgPattoLavoratore%>&strchiavetabella=' + document.Frm1.codAttivazione.value, '<%=Properties.TIPO_DOC_TDA_ATTIVAZIONE%>');
	 }
 }
 
 </script>
 
 </head>
 
  <body class="gestione">
  
  <p class="titolo">Stampa Cod. Attivazione TDA</p>

<p>
	<font color="green">
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<af:form name="Frm1" method="POST" action="AdapterHTTP">
<%out.print(htmlStreamTop);%>
<table cellpadding="0" cellspacing="0" border="0" width="100%">

<tr>
 	<td class="etichetta">Codice attivazione&nbsp;</td>
 	<td class="campo">
	  <af:comboBox name="codAttivazione" moduleName="M_GetCodiceAttivazionePatto"
        	classNameBase="input" required="true" title="Codice attivazione" onChange="fieldChanged();"/>
	</td>
</tr>

</table>
<br>
<center>
<table>
<tr>
<%if (canStampaVoucher) {%>
	<td><input class="pulsante" type="button" name="btnProseguiStampa" value="Stampa" onclick="stampaVoucher();"/></td>
	<td>&nbsp;</td>
<%}%>
<td><input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/></td>
</tr>
</table>
</center>
<%out.print(htmlStreamBottom);%>

<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLav%>">
<input type="hidden" name="PAGE" value="GestioneStatoDocPage">
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
<input type="hidden" name="prgPattoLavoratore" value="<%=prgPattoLavoratore%>">
<input type="hidden" name="rptAction" value="RPT_STAMPA_TDA_CODATTIVAZIONE">
<input type="hidden" name="tipoDoc" value="<%=Properties.TIPO_DOC_TDA_ATTIVAZIONE%>">

</af:form>
</body>
</html>