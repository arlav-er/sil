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
  Vector vectRicercaAz = null;
  Vector vectRicercaUnitaAz = null;
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  String contesto = serviceRequest.containsAttribute("CONTESTO")?serviceRequest.getAttribute("CONTESTO").toString():"";
  String prgAziendaUtil = serviceRequest.getAttribute("PRGAZIENDAUTIL").toString();
  String prgUnitaUtil = serviceRequest.getAttribute("PRGUNITAUTIL").toString();
	String context = StringUtils.getAttributeStrNotNull( serviceRequest, "context");
	boolean validazione = context.equalsIgnoreCase("valida");

  vectRicercaAz = serviceResponse.getAttributeAsVector("M_MovRicercaAziendaUtilCF.ROWS.ROW");
  if (vectRicercaAz.size() > 0) {
    vectRicercaUnitaAz = serviceResponse.getAttributeAsVector("M_MovRicercaUnitaAziendaUtilCF.ROWS.ROW");
  }   

  String canModify = serviceRequest.containsAttribute("canModify")?serviceRequest.getAttribute("canModify").toString():"";
  if (canModify.toUpperCase().equals("FALSE")) {
    disabledField = true;
  }

  String strCodiceFiscale = serviceRequest.containsAttribute("STRCODICEFISCALEAZIENDA")?serviceRequest.getAttribute("STRCODICEFISCALEAZIENDA").toString():"";
  String strRagioneSociale = serviceRequest.containsAttribute("strRagioneSocialeAzUtil")?serviceRequest.getAttribute("strRagioneSocialeAzUtil").toString():"";
  String strIndirizzoAziendaUtil = serviceRequest.containsAttribute("strIndirizzoAziendaUtil")?serviceRequest.getAttribute("strIndirizzoAziendaUtil").toString():"";
  String strComuneAziendaUtil = serviceRequest.containsAttribute("strComuneAziendaUtil")?serviceRequest.getAttribute("strComuneAziendaUtil").toString():"";
  String numContratto = serviceRequest.containsAttribute("numContratto")?serviceRequest.getAttribute("numContratto").toString():"";
  String dataInizio = serviceRequest.containsAttribute("dataInizio")?serviceRequest.getAttribute("dataInizio").toString():"";
  String dataFine = serviceRequest.containsAttribute("dataFine")?serviceRequest.getAttribute("dataFine").toString():"";
  String legaleRapp = serviceRequest.containsAttribute("legaleRapp")?serviceRequest.getAttribute("legaleRapp").toString():"";
  String numSoggetti = serviceRequest.containsAttribute("numSoggetti")?serviceRequest.getAttribute("numSoggetti").toString():"";
  String classeDip = serviceRequest.containsAttribute("classeDip")?serviceRequest.getAttribute("classeDip").toString():"";
  String funzione = serviceRequest.containsAttribute("FUNZ_AGG")?serviceRequest.getAttribute("FUNZ_AGG").toString():"";
  String codTipoTrasf = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPOTRASF");
  String flagAziEstera = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGDISTAZESTERA");

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Cerca azienda</title>
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

//Controlla che la data di inizio contratto si aprecedente a quella di fine se entrambe sono specificate
function controlDate() {
	var datainizio = new String(document.Frm1.dataInizio.value);
	var datafine = new String(document.Frm1.dataFine.value);
	var checkData = 0;
	if (datainizio != null && datainizio != '' && datafine != null && datafine != '') {
		checkData = compareDate(datainizio, datafine);	
	}
	if (checkData > 0) {
		alert("Data di inizio contratto successiva alla data di fine contratto");
		return false;
	} else return true;
}

</script>
</head>

<body class="gestione" onload="rinfresca();">
<% if (codTipoTrasf.equalsIgnoreCase("DL") && !flagAziEstera.equals("S")) {%>
	<p class="titolo">Ricerca Azienda Distaccataria</p>
<%} else {%>
	<p class="titolo">Ricerca Azienda utilizzatrice / Ente promotore</p>
<%}%>
<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<table>
<tr>
  <td class="etichetta">Ragione Sociale</td>
  <td class="campo">
    <af:textBox classNameBase="input" name="strRagioneSocialeAzUtil" title="Ragione Sociale" size="50" value="<%=strRagioneSociale%>" readonly="true" required="true"/>
    <%if (!disabledField) {%>
      <a href="#" onClick="javascript:cercaAzienda();"><img src="../../img/binocolo.gif" alt="Cerca azienda nel DB"></a>
  <%}%>
  </td>
</tr>
<tr>
  <td class="etichetta">Indirizzo</td>
  <td class="campo">
    <af:textBox classNameBase="input" name="strIndirizzoAziendaUtil" title="Indirizzo" size="60" value="<%=strIndirizzoAziendaUtil%>" readonly="true"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Comune</td>
  <td class="campo">
    <af:textBox classNameBase="input" name="strComuneAziendaUtil" title="Comune" size="60" value="<%=strComuneAziendaUtil%>" readonly="true"/>
  </td>
</tr>
</table>

<p align="center">
<% if (codTipoTrasf.equalsIgnoreCase("DL")) {%>
    <input type="hidden" name="numContratto" value="">
	<input type="hidden" name="dataInizio" value="">
	<input type="hidden" name="dataFine" value="">
<%} else {%>
<table>
<tr><td>
  <table>
    <tr>
    <td class="etichetta" nowrap="nowrap">Num. Convenzione/Contratto</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="numContratto" title="Numero Contratto" size="12" value="<%=numContratto%>" readonly="<%=String.valueOf(disabledField)%>" maxlength="11"/>
    </td>
    <td class="etichetta" nowrap="nowrap">&nbsp;Data Convenzione/Inizio Contratto</td>
    <td class="campo" nowrap="nowrap">
      <af:textBox classNameBase="input" name="dataInizio" type="date" validateOnPost="true" title="Data Inizio Contratto" size="12" value="<%=dataInizio%>" readonly="<%=String.valueOf(disabledField)%>" maxlength="10"/>
    </td>
    <td class="etichetta" nowrap="nowrap">&nbsp;Data Fine</td>
    <td class="campo" nowrap="nowrap">
      <af:textBox classNameBase="input" name="dataFine" type="date" validateOnPost="true" title="Data Fine Contratto" size="12" value="<%=dataFine%>" readonly="<%=String.valueOf(disabledField)%>" validateWithFunction="controlDate" maxlength="10"/>
    </td>
    </tr>
  </table>

</td></tr>
</table>
<%}%>
</p>
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
<input type="hidden" name="PRGAZIENDAUTIL" value="<%=prgAziendaUtil%>">
<input type="hidden" name="PRGUNITAUTIL" value="<%=prgUnitaUtil%>">
<input type="hidden" name="AGG_FUNZ" value="">
<input type="hidden" name="TipoAzienda" value="">
<input type="hidden" name="NatGiuridicaAz" value="">
<input type="hidden" name="CONTESTO" value="UTIL">
<input type="hidden" name="PRGAZIENDA" value="<%=prgAziendaUtil%>">
<input type="hidden" name="AGGIUNGIUNITA" value="YES">
<input type="hidden" name="legaleRapp" value="<%=legaleRapp%>">
<input type="hidden" name="numSoggetti" value="<%=numSoggetti%>">
<input type="hidden" name="classeDip" value="<%=classeDip%>">
<input type="hidden" name="CODTIPOTRASF" value="<%=codTipoTrasf%>">
<input type="hidden" name="FLGDISTAZESTERA" value="<%=flagAziEstera%>">

</center>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
