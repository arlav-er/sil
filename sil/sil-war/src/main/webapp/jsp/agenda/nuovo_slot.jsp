<!-- @author: Stefania Orioli - Jan 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.tags.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    it.eng.afExt.utils.DateUtils,
    it.eng.sil.security.User,
    it.eng.afExt.utils.*,it.eng.sil.util.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"
%>

    
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>

<%
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

int cdnUt = user.getCodut();
int cdnTipoGruppo = user.getCdnTipoGruppo();
String codCpi;
if(cdnTipoGruppo==1) { codCpi =  user.getCodRif(); }
else { codCpi = requestContainer.getAttribute("agenda_codCpi").toString(); }
%>
  
<%
  String data=giornoDB + "/" + meseDB + "/" + annoDB;
  String orario = "";
  String prgSlot = "";
  String dtmDataOra = "";
  String numMinuti = "";
  String codServizio = "";
  String prgAmbiente = "";
  String strNote = "";
  String flgPubblico = "";
  String numAziende = "";
  String numLavoratori = "";
  String numAziendePrenotate = "";
  String numLavPrenotati = "";
  String codStatoSlot = "";
  
  String numKloSlot = "";        
  String prgAppuntamento = "";    
  String codCpiAppuntamento = "";

  Testata testata = new Testata(null,null,null,null);
  boolean nuovo = true;
  
  String comboSlot = "COMBO_STATO_SLOT_SETTIMANA_TIPO";
%>
<%
String btnSalva = "Inserisci";
String btnChiudi = "Chiudi senza inserire";
boolean canModify = true;
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Nuovo Slot</title>  
</head>

<body class="gestione" onload="checkError();">
<af:error />

<p class="titolo">Nuovo Slot</p>
<%@ include file="dett_slot.inc" %>

</body>
</html>
