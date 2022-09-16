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
String prgSettTipo = (String) serviceRequest.getAttribute("PRGSETTIPO");
String codCpi = (String) serviceRequest.getAttribute("CODCPI");

String MODULE_NAME = "MDESCRSETTTIPO";
SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
SourceBean row = (SourceBean) cont.getAttribute("ROW");
String strDescrizioneSettimana = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONESETTIMANA");

int numGiornoSett = Integer.parseInt(serviceRequest.getAttribute("NUMGIORNOSETT").toString());
String aGiorni[] = {"", "Luned&igrave;", "Marted&igrave;", "Mercoled&igrave;", "Gioved&igrave;", "Venerd&igrave;", "Sabato", "Domenica" };
String prgGiorno = (String) serviceRequest.getAttribute("PRGGIORNO");

// DETTAGLIO
MODULE_NAME = "MDETTSLOTTIPO";
cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
row = (SourceBean) cont.getAttribute("ROW");
//BigDecimal prgSpi = (BigDecimal) row.getAttribute("PRGSPI");
String prgSpi = "";
BigDecimal prgSpiBd = (BigDecimal) row.getAttribute("PRGSPI");
if(prgSpiBd !=null) { prgSpi = prgSpiBd.toString(); }
String codServizio = StringUtils.getAttributeStrNotNull(row, "CODSERVIZIO");
String strDalle = StringUtils.getAttributeStrNotNull(row, "STRORADALLE");
String strAlle = StringUtils.getAttributeStrNotNull(row, "STRORAALLE");
String numMinuti = "";
BigDecimal minuti = (BigDecimal) row.getAttribute("NUMMINUTI");
if(minuti!=null) { numMinuti = minuti.toString(); }
String numQta = "";
BigDecimal qta = (BigDecimal) row.getAttribute("NUMQTA");
if(qta!=null) { numQta = qta.toString(); }
String flgPubblico = StringUtils.getAttributeStrNotNull(row, "FLGPUBBLICO");
String codDestinatario = StringUtils.getAttributeStrNotNull(row, "CODDESTINATARIO");
String prgAmbiente = "";
BigDecimal prgAula = (BigDecimal) row.getAttribute("PRGAMBIENTE");
if(prgAula!=null) { prgAmbiente = prgAula.toString(); }

String frmModule = "MUpdSlotTipo";

String labelServizio = "Servizio";
String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equalsIgnoreCase("1")){
	labelServizio = "Area";
}
%>
<%
String btnSalva = "Aggiorna";
String btnChiudi = "Chiudi senza aggiornare";
//PageAttribs attributi = new PageAttribs(user, "SELECT_AGENDA_PAGE");
//boolean canModify = attributi.containsButton("salva");
boolean canModify = true;
if(!canModify) { btnChiudi = "Chiudi"; }
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Modifica Slot Tipo</title>
</head>

<body class="gestione" onload="rinfresca()">
<br>
<p class="titolo">Modifica Slot Tipo</p>

<%@ include file="dett_slot_tipo.inc" %>

</body>
</html>

