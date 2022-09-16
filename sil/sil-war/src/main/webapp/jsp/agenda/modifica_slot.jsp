<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  oracle.sql.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
    


<%
    String MODULE_NAME="MDETTSLOT";
    String cod_vista = StringUtils.getAttributeStrNotNull(serviceRequest,"cod_vista");
    String mod = StringUtils.getAttributeStrNotNull(serviceRequest,"MOD");
    if(mod.equals("")) { mod = "0"; }
    
    String giorno = StringUtils.getAttributeStrNotNull(serviceRequest,"giorno");
    String mese = StringUtils.getAttributeStrNotNull(serviceRequest,"mese");
    String anno = StringUtils.getAttributeStrNotNull(serviceRequest,"anno");
    String giornoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"giornoDB");
    String meseDB = StringUtils.getAttributeStrNotNull(serviceRequest,"meseDB");
    String annoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"annoDB");
    
    
    String sel_operatore = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore");
    String sel_servizio = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_servizio");
    String sel_aula = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_aula");
    String dataDal = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDal");
    String dataAl = StringUtils.getAttributeStrNotNull(serviceRequest, "dataAl");
    
    String linkDett = "&MOD=" + mod + "&cod_vista=" + cod_vista;
    if(mod.equals("0")) {
      linkDett += "&giornoDB=" + giornoDB + "&meseDB=" + meseDB + "&annoDB=" + annoDB;
      linkDett += "&giorno=" + giorno + "&mese=" + mese + "&anno=" + anno;
    }
    if(mod.equals("2")) {
      linkDett += "&sel_operatore=" + sel_operatore;
      linkDett += "&sel_servizio=" + sel_servizio;
      linkDett += "&sel_aula=" + sel_aula;
      linkDett += "&dataDal=" + dataDal;
      linkDett += "&dataAl=" + dataAl;
      linkDett += "&mese=" + mese;
      linkDett += "&anno=" + anno;
    }
%>

<%  
    SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
    SourceBean r = (SourceBean) cont.getAttribute("ROWS.ROW");

    
    String codCpi = StringUtils.getAttributeStrNotNull(r, "CODCPI");
    String data = StringUtils.getAttributeStrNotNull(r, "DATA");
    String orario = StringUtils.getAttributeStrNotNull(r, "ORARIO");
    //String PRGAPPUNTAMENTO = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGAPPUNTAMENTO");
    String prgSlotStr =   (String) serviceRequest.getAttribute("PRGSLOT"); 
    BigDecimal prgSlot = null;
    if ((prgSlotStr!=null) && prgSlotStr.equals("") ){
      prgSlot = (BigDecimal) r.getAttribute("PRGSLOT");
    }else{
      prgSlot = new  BigDecimal(prgSlotStr);
    }
    String numMinuti = StringUtils.getAttributeStrNotNull(r, "NUMMINUTI");
    String codServizio = StringUtils.getAttributeStrNotNull(r,"CODSERVIZIO");
    String prgAmbiente = StringUtils.getAttributeStrNotNull(r, "PRGAMBIENTE");
    String numAziende = StringUtils.getAttributeStrNotNull(r, "NUMAZIENDE");
    String numLavoratori = StringUtils.getAttributeStrNotNull(r, "NUMLAVORATORI");
    String numAziendePrenotate = StringUtils.getAttributeStrNotNull(r, "NUMAZIENDEPRENOTATE");
    String numLavPrenotati = StringUtils.getAttributeStrNotNull(r, "NUMLAVPRENOTATI");
    String strNote = StringUtils.getAttributeStrNotNull(r, "STRNOTE");
    String flgPubblico = StringUtils.getAttributeStrNotNull(r, "FLGPUBBLICO");
    String codStatoSlot = StringUtils.getAttributeStrNotNull(r, "CODSTATOSLOT");    
    BigDecimal numKloSlot = (BigDecimal) r.getAttribute("NUMKLOSLOT");        

    BigDecimal cdnUtIns = (BigDecimal) r.getAttribute("CDNUTINS");
    String dtmIns = (String) r.getAttribute("DTMINS");
    BigDecimal cdnUtMod = (BigDecimal) r.getAttribute("CDNUTMOD");
    String dtmMod = (String) r.getAttribute("DTMMOD");

    Testata testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
    boolean nuovo = false;
    
    String comboSlot = "COMBO_STATO_SLOT";
    
    String labelServizio = "Servizio";
    String umbriaGestAz = "0";
    if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
    	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
    }
    if(umbriaGestAz.equalsIgnoreCase("1")){
    	labelServizio = "Area";
    }
    String titleServizio = "Codice "+labelServizio;
%>
<%
String btnSalva = "Aggiorna";
String btnChiudi = "Chiudi senza aggiornare";
//PageAttribs attributi = new PageAttribs(user, "DettaglioSlotPage");
//boolean canModify = attributi.containsButton("salva");
boolean canModify = true;
if(!canModify) { btnChiudi = "Chiudi"; }
%>
<html>
<HEAD>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Dettaglio Slot</title>
</head>

<body class="gestione" onLoad="rinfresca()">

<p class="titolo">Dettaglio Slot</p>
<p align="left">
<%
SourceBean contErr = (SourceBean) serviceResponse.getAttribute("MINCONGRUENZESLOT");
Vector rowsErr = contErr.getAttributeAsVector("ROWS.ROW");
SourceBean rowErr = null;
int j = 0;
String txtErr = "";
if(rowsErr.size()>0) {
%>
  <img src="../../img/warning.gif">&nbsp;<b>Attenzione, si sono verificate le seguenti incongruenze con altri slot:</b>
  <ul>
<%
  for(j=0; j<rowsErr.size(); j++) {
    rowErr = (SourceBean) rowsErr.elementAt(j);
    txtErr = (String) rowErr.getAttribute("STRTIPOERR");
%>
    <li><%=txtErr%></li>
<% }%>
  </ul>
<%}%>
</p>

<%@ include file="dett_slot.inc" %>

</body>
</html>
