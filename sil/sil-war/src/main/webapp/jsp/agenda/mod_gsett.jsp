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
String giorno = StringUtils.getAttributeStrNotNull(serviceRequest,"giorno");
String mese = StringUtils.getAttributeStrNotNull(serviceRequest,"mese");
String anno = StringUtils.getAttributeStrNotNull(serviceRequest,"anno");
String giornoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"giornoDB");
String meseDB = StringUtils.getAttributeStrNotNull(serviceRequest,"meseDB");
String annoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"annoDB");

String mod = StringUtils.getAttributeStrNotNull(serviceRequest,"MOD");
if(mod.equals("")) { mod = "0"; }
String data=giornoDB + "/" + meseDB + "/" + annoDB;
%>

<%
String codCpi = "";
BigDecimal numKloGiornoNl = null;
String prgGiornoNl = "";
String datInizioVal = "";
String datFineVal = "";
BigDecimal numGS = null;
int numGSett;

String tipo = "";

boolean errIns = false;
%>

<%
String MODULE_NAME="MDETTGSETT";
SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");

if(row != null) {
    BigDecimal prg = (BigDecimal) row.getAttribute("PRGGIORNONL");
    if(prg!=null) { prgGiornoNl = prg.toString(); }
    codCpi = StringUtils.getAttributeStrNotNull(row, "CODCPI");
    numKloGiornoNl = (BigDecimal) row.getAttribute("NUMKLOGIORNONL");
    // numKloGiornoNl = numKloGiornoNl.add(new BigDecimal(1)); // il +1 è inglobato nello statement
    numGS = (BigDecimal) row.getAttribute("NUMGSETT");
    numGSett = Integer.parseInt(numGS.toString());
    datInizioVal = StringUtils.getAttributeStrNotNull(row, "DATINIZIOVAL");
    datFineVal = StringUtils.getAttributeStrNotNull(row, "DATFINEVAL");

    tipo = StringUtils.getAttributeStrNotNull(serviceRequest, "tipo");

    errIns = false;
} else {
    codCpi = (String) serviceRequest.getAttribute("CODCPI");
    numKloGiornoNl = null;
    prgGiornoNl = "";
    datInizioVal = (String) serviceRequest.getAttribute("DATINIZIOVAL");
    datFineVal = (String) serviceRequest.getAttribute("DATFINEVAL");
    numGSett = Integer.parseInt(serviceRequest.getAttribute("NUMGSETT").toString());
    
    tipo = StringUtils.getAttributeStrNotNull(serviceRequest, "tipo");

    errIns = true;
}
Testata testata = new Testata(null,null,null,null);
%>
<%
String btnSalva = "Aggiorna";
String btnChiudi = "Chiudi senza aggiornare";
//PageAttribs attributi = new PageAttribs(user, "ModGSettPage");
//boolean canModify = attributi.containsButton("salva");
boolean canModify = true;
if(!canModify) { btnChiudi = "Chiudi"; }
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Modifica Riposo Settimanale</title>
</head>

<body class="gestione">
<br>
<p class="titolo">Modifica Riposo Settimanale</p>

<%@ include file="DettGSett.inc" %>
</body>
</html>

