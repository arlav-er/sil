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

String numKloContatto = "";
String prgContatto = "";
String datContatto = giornoDB + "/" + meseDB + "/" + annoDB;
Calendar now = Calendar.getInstance();
//String strOraContatto = Integer.toString(now.getHours()) + ":" + Integer.toString(now.getMinutes());
String ora = Integer.toString(now.get(Calendar.HOUR_OF_DAY));
if(ora.length()<=1) { ora = "0" + ora; }
String minuti = Integer.toString(now.get(Calendar.MINUTE));
if(minuti.length()<=1) { minuti = "0" + minuti; }
//String strOraContatto = Integer.toString(now.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(now.get(Calendar.MINUTE));
String strOraContatto = ora + ":" + minuti;

String prgSpiContatto = "";
BigDecimal prgSpi = null;
SourceBean content = (SourceBean) serviceResponse.getAttribute("MSPI_UTENTE");
if(content!=null) {
  SourceBean row = (SourceBean) content.getAttribute("ROWS.ROW");
  if(row!=null) { prgSpi = (BigDecimal) row.getAttribute("PRGSPI"); }
}
if(prgSpi!=null) { prgSpiContatto = prgSpi.toString(); }

String txtContatto = "";
String flgRicontattare = "";
String strIo = "";
String datEntroIl = "";
String prgMotContatto = "";
String prgTipoContatto = "";
String prgEffettoContatto = "";
String prgUnita = "";
String prgAzienda = "";
String cdnLavoratore = "";
String strDatiContatto = "";
String strDisponibilitaRosa = "";
String flgInviatoSMS = "";
String strCellSMSInvio = "";

String ico = "../../img/b.gif";

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
  <title>Nuovo Contatto</title>
</head>

<body class="gestione">
<br>
<p class="titolo">Nuovo Contatto</p>

<%@ include file="dett_contatto.inc" %>

</body>
</html>
