<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ page import="
  com.engiweb.framework.base.*,
  java.lang.*,
  java.text.*,
  java.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.*,
  it.eng.sil.util.*" %>

<%@ include file="../global/getCommonObjects.inc"%>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%

  boolean disabledField = false;
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  
  String prgAziendaDist = StringUtils.getAttributeStrNotNull( serviceRequest, "PRGAZIENDADIST"); 
  String prgUnitaDist = StringUtils.getAttributeStrNotNull( serviceRequest, "PRGUNITADIST"); 
  String context = StringUtils.getAttributeStrNotNull( serviceRequest, "context");
  

  String canModify = serviceRequest.containsAttribute("canModify")?serviceRequest.getAttribute("canModify").toString():"";
  if (canModify.toUpperCase().equals("FALSE")) {
   disabledField = true;
  }

  String strRagioneSocialeAzDis = StringUtils.getAttributeStrNotNull(serviceRequest, "strRagioneSocialeAzUtil");
  String strIndirizzoAziendaDis = StringUtils.getAttributeStrNotNull(serviceRequest, "strIndirizzoAziendaUtil");
  String strComuneAziendaDis = StringUtils.getAttributeStrNotNull(serviceRequest, "strComuneAziendaUtil");
  String funzione = serviceRequest.containsAttribute("FUNZ_AGG")?serviceRequest.getAttribute("FUNZ_AGG").toString():"";

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Cerca azienda distaccataria</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<%@ include file="../global/confrontaData.inc"%>
<script language="Javascript">
function cercaAzienda() {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
  
  document.Frm1.PAGE.value = "MovimentiSelezionaAziendaUtil";
  doFormSubmit(document.Frm1);
}


</script>
</head>

<body class="gestione" onload="rinfresca();">
<p class="titolo">Ricerca Azienda Distaccataria</p>
<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<table>
<tr>
  <td class="etichetta">Ragione Sociale</td>
  <td class="campo">
    <af:textBox classNameBase="input" name="strRagioneSocialeAzDis" title="Ragione Sociale" size="50" value="<%=strRagioneSocialeAzDis%>" readonly="true" required="true"/>
    <%if (!disabledField) {%>
      <a href="#" onClick="javascript:cercaAzienda();"><img src="../../img/binocolo.gif" alt="Cerca azienda nel DB"></a>
  <%}%>
  </td>
</tr>
<tr>
  <td class="etichetta">Indirizzo</td>
  <td class="campo">
    <af:textBox classNameBase="input" name="strIndirizzoAziendaDis" title="Indirizzo" size="60" value="<%=strIndirizzoAziendaDis%>" readonly="true"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Comune</td>
  <td class="campo">
    <af:textBox classNameBase="input" name="strComuneAziendaDis" title="Comune" size="60" value="<%=strComuneAziendaDis%>" readonly="true"/>
  </td>
</tr>
</table>
<br>
<center>
<table>
<tr>
<%
if (!disabledField) {%>
  <td align="center">
  <input type="submit" class="pulsante" name="confermaSelAzUtil" value="Avanti">
  </td>
<%}%>
<td align="center">
<input type="button" class="pulsante" name="chiudiSelAzUtil" value="Chiudi" onclick="javascript:window.close();">
</td>
</tr>
</table>
<input type="hidden" name="PAGE" value="MovimentiRefreshAziendaUtilPage">
<input type="hidden" name="FUNZ_AGG" value="<%=funzione%>">
<input type="hidden" name="PRGAZIENDADIST" value="<%=prgAziendaDist%>">
<input type="hidden" name="PRGUNITADIST" value="<%=prgUnitaDist%>">
<input type="hidden" name="AGG_FUNZ" value="">
<input type="hidden" name="TipoAzienda" value="">
<input type="hidden" name="NatGiuridicaAz" value="">
<input type="hidden" name="CONTESTO" value="UTIL">
<input type="hidden" name="PRGAZIENDA" value="<%=prgAziendaDist%>">

</center>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
