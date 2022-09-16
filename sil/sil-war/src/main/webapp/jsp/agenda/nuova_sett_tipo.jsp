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
int cdnUt = user.getCodut();
int cdnTipoGruppo = user.getCdnTipoGruppo();
String codCpi;
if(cdnTipoGruppo==1) { codCpi =  user.getCodRif(); }
else { codCpi = requestContainer.getAttribute("agenda_codCpi").toString(); }

Calendar gc = Calendar.getInstance();

String numKlo = "";
String prgSettTipo = "";
String strDescrizioneSettimana = "";
String datInizioVal = "";
String datFineVal = "";


Testata testata = new Testata(null,null,null,null);

String moduleName = "MInsSettTipo";
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
  <title>Nuova Settimana Tipo</title>
</head>

<body class="gestione"  onload="rinfresca()">
<br>
<p class="titolo">Nuova Settimana Tipo</p>

<%@ include file="dett_sett_tipo.inc" %>
</body>
</html>
