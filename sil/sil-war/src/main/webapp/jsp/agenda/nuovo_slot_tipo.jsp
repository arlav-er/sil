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


// NUOVO
String prgGiorno = "";
String prgSpi = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGSPI");
String codServizio = StringUtils.getAttributeStrNotNull(serviceRequest, "CODSERVIZIO");
String strDalle = "";
String strAlle = "";
String numMinuti = "";
String numQta = "";
String prgAmbiente = "";
String codDestinatario = "";
String flgPubblico = "";

String frmModule = "MInsSlotTipo";
String btnSalva = "Inserisci";
String btnChiudi = "Chiudi senza inserire";

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
boolean canModify = true;
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Modifica Slot Tipo</title>
</head>

<body class="gestione"  onload="rinfresca()">
<br>
<p class="titolo">Modifica Slot Tipo</p>

<%@ include file="dett_slot_tipo.inc" %>

</body>
</html>

