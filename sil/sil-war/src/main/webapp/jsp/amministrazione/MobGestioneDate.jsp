<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.module.movimenti.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%      
String dataFineMov = StringUtils.getAttributeStrNotNull(serviceRequest,"DATFINEMOV");
String  prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGAZIENDA");
String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGUNITA");
String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest,"cdnLavoratore");
String codComune = StringUtils.getAttributeStrNotNull(serviceRequest,"CODCOM");
String dataInizioMob = DateUtils.giornoSuccessivo(dataFineMov);
String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);

SourceBean sbComExCassaMezz = (SourceBean)serviceResponse.getAttribute("M_MOB_MESIMOBEXCASSAMEZZ.ROWS.ROW");
BigDecimal numMesiComExCassaMezz = null;
String strMesiComExCassaMezz = "0";
if (sbComExCassaMezz != null) {
	strMesiComExCassaMezz = sbComExCassaMezz.containsAttribute("NUMMESIMOBEXCASSAMEZZ")?sbComExCassaMezz.getAttribute("NUMMESIMOBEXCASSAMEZZ").toString():"0";
	if (strMesiComExCassaMezz.equals("")) {
		strMesiComExCassaMezz = "0";
	}
	numMesiComExCassaMezz = new BigDecimal(strMesiComExCassaMezz);
}
BigDecimal numMesi = new BigDecimal(serviceResponse.getAttribute("M_MOB_DURATAMOBILITA.DURATA_MOB.MESI").toString());
String dataFineMob = DateUtils.aggiungiMesi(dataInizioMob, numMesi.intValue(), -1);
String dataFineMaxDiff = DateUtils.aggiungiMesi(dataInizioMob, (2 * numMesi.intValue()), -1);
if (!strMesiComExCassaMezz.equals("0")) {
	dataFineMob = DateUtils.aggiungiMesi(dataFineMob, numMesiComExCassaMezz.intValue(), 0);
	dataFineMaxDiff = DateUtils.aggiungiMesi(dataFineMaxDiff, numMesiComExCassaMezz.intValue(), 0);
}
String dataFineMobOrig = dataFineMob;

%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/" />

<title></title>
<script language="Javascript">

function salvaValori() {
	window.opener.document.Frm1.datInizio.value = document.frmDate.datInizio.value;
	window.opener.document.Frm1.datFineOrig.value = document.frmDate.datFineOrig.value;
	window.opener.document.Frm1.datFine.value = document.frmDate.datFine.value;
	window.opener.document.Frm1.datMaxDiff.value = document.frmDate.datMaxDiff.value;
	window.close();
}

function chiudiFinestra() {
	window.close();
}

</script>
</head>

<body class="gestione">
<p class="titolo">&nbsp;</p>
<form name="frmDate">
<%out.print(htmlStreamTop);%>
<center>
<table class="main">

<tr><td colspan="2">&nbsp;</td></tr>

<tr>
    <td nowrap class="etichetta">Data inizio mobilit&agrave;</td>
    <td nowrap><af:textBox classNameBase="input" type="date" name="datInizio" value="<%=Utils.notNull(dataInizioMob)%>"
  			    title="Data inizio" readonly="true" size="11" maxlength="10"/>
    </td>
</tr>

<tr>
  <td nowrap class="etichetta">Data fine originaria</td>
  <td nowrap><af:textBox classNameBase="input" type="date" name="datFineOrig" value="<%=Utils.notNull(dataFineMobOrig)%>" 
                title="Data fine originaria" readonly="true" size="11" maxlength="10"/>
  </td>
</tr>

<tr>
  <td nowrap class="etichetta">Data fine</td>
  <td nowrap><af:textBox classNameBase="input" type="date" name="datFine" value="<%=Utils.notNull(dataFineMob)%>"
                title="Data fine" readonly="true" size="11" maxlength="10"/></td>
</tr>

<tr>
  <td nowrap class="etichetta">Data max differ.</td>
  <td nowrap><af:textBox classNameBase="input" type="date" name="datMaxDiff" value="<%=Utils.notNull(dataFineMaxDiff)%>"
                title="Data max differimento" readonly="true" size="11" maxlength="10"/></td>
</tr>

</table>
</center>
<%out.print(htmlStreamBottom);%>
<center>
<table>
<tr>
<td>
<input class="pulsante" type="button" name="salva" value="Salva" onclick="salvaValori();">
&nbsp;&nbsp;<input class="pulsante" type="button" name="chiudi" value="Chiudi senza aggiornare" onclick="chiudiFinestra();">     
</td>
</tr>
</table>
</center>
</form>
</body>
</html>
