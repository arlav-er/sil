<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>


<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
BigDecimal numKlo = null;
String prgSettTipo = "";
String codCpi;

String strDescrizioneSettimana = "";
String datInizioVal = "";
String datFineVal = "";

String moduleName = "MUpdSettTipo";
//String btnSalva = "Salva";
//String btnAnnulla = "Chiudi senza salvare";

String MODULE_NAME="MDETTSETTTIPO";
SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
SourceBean row = (SourceBean) cont.getAttribute("ROW");

BigDecimal prg = (BigDecimal) row.getAttribute("PRGSETTIPO");
if(prg!=null) { prgSettTipo = prg.toString(); }
codCpi = StringUtils.getAttributeStrNotNull(row, "CODCPI");
strDescrizioneSettimana = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONESETTIMANA");
numKlo = (BigDecimal) row.getAttribute("NUMKLO");
datInizioVal = StringUtils.getAttributeStrNotNull(row, "DATINIZIOVAL");
datFineVal = StringUtils.getAttributeStrNotNull(row, "DATFINEVAL");
%>
<%
String btnSalva = "Aggiorna";
String btnChiudi = "Chiudi senza aggiornare";
//PageAttribs attributi = new PageAttribs(user, "ModSettimanaTipoPage");
//boolean canModify = attributi.containsButton("salva");
boolean canModify = true;
if(!canModify) { btnChiudi = "Chiudi"; }
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Modifica Settimana Tipo</title>
</head>

<body class="gestione"  onload="rinfresca()">
<br>
<p class="titolo">Modifica Settimana Tipo</p>

<%@ include file="dett_sett_tipo.inc" %>

</body>
</html>

