<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,
                com.engiweb.framework.util.JavaScript,java.math.*,
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
String cod_vista = StringUtils.getAttributeStrNotNull(serviceRequest, "cod_vista");

String mod = StringUtils.getAttributeStrNotNull(serviceRequest,"MOD");
if(mod.equals("")) { mod = "0"; }

String sel_operatore = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore");
String sel_servizio = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_servizio");
String sel_aula = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_aula");
String strRagSoc = StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc");
String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
String strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscaleAz");
String piva = StringUtils.getAttributeStrNotNull(serviceRequest,"piva");
String strCognome = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
String strNome = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
String dataDal = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDal");
String dataAl = StringUtils.getAttributeStrNotNull(serviceRequest, "dataAl");

%>

<%
String MODULE_NAME="MDETTCONTATTO";
SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");

String codCpi = "";
BigDecimal numKloContatto = null;
String prgContatto = "";
String datContatto = "";
Calendar now = Calendar.getInstance();
String strOraContatto = Integer.toString(now.get(Calendar.HOUR)) + ":" + Integer.toString(now.get(Calendar.MINUTE));
String prgSpiContatto = "";
BigDecimal prgSpi = null;
String txtContatto = "";
String flgRicontattare = "";
String strIo = "";
String datEntroIl = "";
String prgMotContatto = "";
BigDecimal prgMot = null;
String prgTipoContatto = "";
BigDecimal prgTipo = null;
String prgEffettoContatto = "";
BigDecimal prgEffetto = null;
String prgUnita = "";
BigDecimal prgUn = null;
String prgAzienda = "";
BigDecimal prgAz = null;
String cdnLavoratore = "";
BigDecimal cdnLav = null;
String strDatiContatto = "";
String strDisponibilitaRosa = "";
String flgInviatoSMS = "";
String strCellSMSInvio = "";
//invio SMS

String ico = "../../img/b.gif";

BigDecimal cdnUtIns = null;
String dtmIns = "";
BigDecimal cdnUtMod = null;
String dtmMod = ""; 

Testata testata = null;
boolean errIns = false;

if(row!=null) {
        BigDecimal prgCont = (BigDecimal) row.getAttribute("PRGCONTATTO");
        if(prgCont!=null) { prgContatto = prgCont.toString(); }
        //prgContatto = serviceRequest.getAttribute("PRGCONTATTO").toString();
        codCpi = serviceRequest.getAttribute("CODCPICONTATTO").toString();
        numKloContatto = (BigDecimal) row.getAttribute("NUMKLOCONTATTO");
        datContatto = row.getAttribute("DATCONTATTO").toString();
        strOraContatto = StringUtils.getAttributeStrNotNull(row, "STRORACONTATTO");
        prgSpi = (BigDecimal) row.getAttribute("PRGSPICONTATTO");
        if(prgSpi!=null) { prgSpiContatto = prgSpi.toString(); }
        txtContatto = StringUtils.getAttributeStrNotNull(row, "TXTCONTATTO");
        flgRicontattare = StringUtils.getAttributeStrNotNull(row, "FLGRICONTATTARE");
        strIo = StringUtils.getAttributeStrNotNull(row, "STRIO");
        datEntroIl = StringUtils.getAttributeStrNotNull(row, "DATENTROIL");
        prgMot = (BigDecimal) row.getAttribute("PRGMOTCONTATTO");
        if(prgMot!=null) { prgMotContatto = prgMot.toString(); }
        prgTipo = (BigDecimal) row.getAttribute("PRGTIPOCONTATTO"); 
        if(prgTipo!=null) { prgTipoContatto = prgTipo.toString(); }
        prgEffetto = (BigDecimal) row.getAttribute("PRGEFFETTOCONTATTO");
        if(prgEffetto!=null) { prgEffettoContatto = prgEffetto.toString(); }
        prgUn = (BigDecimal) row.getAttribute("PRGUNITA");
        if(prgUn!=null) { prgUnita = prgUn.toString(); }
        prgAz = (BigDecimal) row.getAttribute("PRGAZIENDA");
        if(prgAz!=null) { prgAzienda = prgAz.toString(); }
        cdnLav = (BigDecimal) row.getAttribute("CDNLAVORATORE");
        if(cdnLav!=null) { cdnLavoratore = cdnLav.toString(); }

        if(!cdnLavoratore.equals("")) { 
          strDatiContatto = StringUtils.getAttributeStrNotNull(row, "STRDATILAV"); 
          ico = "../../img/omino.gif";
        }
        if(!prgAzienda.equals("")) { 
          strDatiContatto = StringUtils.getAttributeStrNotNull(row, "STRDATIAZIENDA"); 
          ico = "../../img/azienda.gif";
        }

        cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
        dtmIns = (String) row.getAttribute("DTMINS");
        cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
        dtmMod = (String) row.getAttribute("DTMMOD");
        strDisponibilitaRosa = StringUtils.getAttributeStrNotNull(row, "STRDISPONIBILITAROSA");
        //invioSMS
        flgInviatoSMS = StringUtils.getAttributeStrNotNull(row, "FLGINVIATOSMS");
		strCellSMSInvio = StringUtils.getAttributeStrNotNull(row, "STRCELLSMSINVIO");
        testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
} else {
        datContatto = giornoDB + "/" + meseDB + "/" + annoDB;
        strOraContatto = StringUtils.getAttributeStrNotNull(serviceRequest, "STRORACONTATTO");
        prgSpiContatto = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGSPICONTATTO");
        txtContatto = StringUtils.getAttributeStrNotNull(serviceRequest, "TXTCONTATTO");
        flgRicontattare = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGRICONTATTARE");
        strIo = StringUtils.getAttributeStrNotNull(serviceRequest, "STRIO");
        datEntroIl = StringUtils.getAttributeStrNotNull(serviceRequest, "DATENTROIL");
        prgMotContatto = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOTCONTATTO");
        prgTipoContatto = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGTIPOCONTATTO");
        prgEffettoContatto = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGEFFETTOCONTATTO");
        prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");
        prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
        cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
        strDatiContatto = StringUtils.getAttributeStrNotNull(serviceResponse, "TXTDATICONTATTO");
        errIns = true;
        strDisponibilitaRosa = "";
        flgInviatoSMS =  "";
        strCellSMSInvio= ""; 
        //testata = new Testata(null,null,null,null);
}

%>
<%
String btnSalva = "Aggiorna";
String btnChiudi = "Chiudi senza aggiornare";
//PageAttribs attributi = new PageAttribs(user, "ModContattoPage");
//boolean canModify = attributi.containsButton("salva");
boolean canModify = true;
if(!canModify) { btnChiudi = "Chiudi"; }
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Modifica Contatto</title>
</head>

<body class="gestione">
<br>
<p class="titolo">Modifica Contatto</p>

<%@ include file="dett_contatto.inc" %>

</body>
</html>