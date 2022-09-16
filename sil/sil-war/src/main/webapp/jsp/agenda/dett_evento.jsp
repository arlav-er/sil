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
String cod_vista = StringUtils.getAttributeStrNotNull(serviceRequest,"cod_vista");
String mod = StringUtils.getAttributeStrNotNull(serviceRequest,"MOD");
if(mod.equals("")) { mod = "0"; }
%>

<%
//User user = (User) sessionContainer.getAttribute(User.USERID);
int cdnUt = user.getCodut();
int cdnTipoGruppo = user.getCdnTipoGruppo();
String codCpi  = StringUtils.getAttributeStrNotNull(serviceRequest,"CODCPI");
if (codCpi.equalsIgnoreCase("")) {
	if(cdnTipoGruppo==1) { 
		codCpi =  user.getCodRif(); 
	}
	if(cdnTipoGruppo!=1 || codCpi.equalsIgnoreCase("") || codCpi==null ) { 
		// PAGINA_DI_ERRORE
		//response.sendRedirect("../../servlet/fv/AdapterHTTP?PAGE=SelezionaCPIPage");
	}
}
%>
<%

SourceBean row = (SourceBean) serviceResponse.getAttribute("MDETTEVENTO.ROWS.ROW");

String codCpiEvento = StringUtils.getAttributeStrNotNull(row, "CODCPIEVENTO");
//String prgEvento = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGEVENTO");
String prgEventoStr =   (String) serviceRequest.getAttribute("PRGEVENTO"); 
BigDecimal prgEvento = null;
if ((prgEventoStr!=null) && prgEventoStr.equals("") ){
  prgEvento = (BigDecimal) row.getAttribute("PRGEVENTO");             
}else{
  prgEvento = new  BigDecimal(prgEventoStr);
}
String data = StringUtils.getAttributeStrNotNull(row, "DATEVENTO");
String strDescrizioneBreve = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONEBREVE");
String txtEvento = StringUtils.getAttributeStrNotNull(row,"TXTEVENTO");

String datInizioPub = StringUtils.getAttributeStrNotNull(row, "DATINIZIOPUB");
String flgPubblico = StringUtils.getAttributeStrNotNull(row, "FLGPUBBLICO");

String data_ins = StringUtils.getAttributeStrNotNull(row, "DATA_INS");
String data_mod = StringUtils.getAttributeStrNotNull(row, "DATA_MOD");
String ut_ins = StringUtils.getAttributeStrNotNull(row, "UT_INS");
String ut_mod = StringUtils.getAttributeStrNotNull(row, "UT_MOD");

BigDecimal numKloEvento = (BigDecimal) row.getAttribute("NUMKLOEVENTO");
numKloEvento = numKloEvento.add(new BigDecimal(1));

BigDecimal cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
String dtmIns = (String) row.getAttribute("DTMINS");
BigDecimal cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
String dtmMod = (String) row.getAttribute("DTMMOD");

Testata testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

%>
<%
String btnSalva = "Aggiorna";
String btnChiudi = "Chiudi senza aggiornare";
//PageAttribs attributi = new PageAttribs(user, "PEvento");
//boolean canModify = attributi.containsButton("salva");
boolean canModify = true;
if(!canModify) { btnChiudi = "Chiudi"; }
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Dettaglio Evento</title>
</head>

<body class="gestione">

<br>
<p class="titolo">Dettaglio Evento</p>
<%@ include file="DettEvento.inc" %>
<br>
</body>
</html>
