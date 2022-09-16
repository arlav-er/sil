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
//User user = (User) sessionContainer.getAttribute(User.USERID);
int cdnUt = user.getCodut();
int cdnTipoGruppo = user.getCdnTipoGruppo();
String codCpi;
if(cdnTipoGruppo==1) { codCpi =  user.getCodRif(); }
else { codCpi = requestContainer.getAttribute("agenda_codCpi").toString(); }

String numKloGiornoNl = "";
String prgGiornoNl = "";
String datInizioVal = data;
String datFineVal = "";

Calendar gc = Calendar.getInstance();
gc.set(Integer.parseInt(annoDB), Integer.parseInt(meseDB)-1, Integer.parseInt(giornoDB));
int numGSett = gc.get(Calendar.DAY_OF_WEEK)-1;
if(numGSett == 0) { numGSett = 7; }

String tipo = StringUtils.getAttributeStrNotNull(serviceRequest, "tipo");

Testata testata = new Testata(null,null,null,null);
boolean errIns = false;
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
  <title>Trasforma in Riposo Settimanale</title>
</head>

<body class="gestione">
<br>
<p class="titolo">Trasforma in Riposo Settimanale</p>

<%@ include file="DettGSett.inc" %>
</body>
</html>
