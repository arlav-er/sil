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
String data=giornoDB + "/" + meseDB + "/" + annoDB;
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

String strDescrizioneBreve = "";
String txtEvento = "";
String datInizioPub = "";
String flgPubblico = "";
String prgEvento = "";

String numKloEvento = null;

Testata testata = new Testata(null,null,null,null);
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
  <title>Nuovo Evento</title>
</head>

<body class="gestione">
<br>
<p class="titolo">Inserisci Nuovo Evento</p>

<%@ include file="DettEvento.inc" %>

</body>
</html>
