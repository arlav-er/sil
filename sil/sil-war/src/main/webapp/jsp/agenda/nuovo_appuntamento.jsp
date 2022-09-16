<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
    com.engiweb.framework.base.*,
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
    java.util.*"%>

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
    String sel_anpal = StringUtils.getAttributeStrNotNull(serviceRequest,"strIdCoap");
    String sel_servizio = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_servizio");
    String sel_aula = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_aula");
    String esitoApp = StringUtils.getAttributeStrNotNull(serviceRequest,"esitoApp");
    String strRagSoc = StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc");
    String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
    String strCognome = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
    String strNome = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
    String dataDal = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDal");
    String dataAl = StringUtils.getAttributeStrNotNull(serviceRequest, "dataAl");
    String piva = StringUtils.getAttributeStrNotNull(serviceRequest, "piva");
    String strCodiceFiscaleAz= StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscaleAz");
	BigDecimal numLavoratoriDisp = null;

    String linkDett = "&MOD=" + mod + "&cod_vista=" + cod_vista;
    if(mod.equals("0")) {
      linkDett += "&giornoDB=" + giornoDB + "&meseDB=" + meseDB + "&annoDB=" + annoDB;
      linkDett += "&giorno=" + giorno + "&mese=" + mese + "&anno=" + anno;
    }
    if(mod.equals("2")) {
      linkDett += "&sel_operatore=" + sel_operatore;
      linkDett += "&strIdCoap=" + sel_anpal;
      linkDett += "&sel_servizio=" + sel_servizio;
      linkDett += "&sel_aula=" + sel_aula;
      linkDett += "&esitoApp=" + esitoApp;
      linkDett += "&strRagSoc=" + strRagSoc;
      linkDett += "&strCodiceFiscale=" + strCodiceFiscale;
      linkDett += "&strCognome=" + strCognome;
      linkDett += "&strNome=" + strNome;
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
  String moduleSpi = "COMBO_SPI_SCAD";
  String data=giornoDB + "/" + meseDB + "/" + annoDB;
  String CODCPI = codCpi;
  String orario = "";
  String prgAppuntamento = "";
  String DTMDATAORA = "";
  String NUMMINUTI = "";
  String numOraInizioEff = "";
  String numOraFineEff = "";
  String CODSERVIZIO = "";
  String PRGSPI = "";
  String PRGAMBIENTE = "";
  String PRGSPIEFF = "";
  //CLOB TXTNOTE=(CLOB) serviceResponse.getAttribute(MODULE_NAME + ".ROW.TXTNOTE");
  String txtNote = "";
  String PRGTIPOPRENOTAZIONE = "";
  String STRTELRIF = "";
  String STREMAILRIF = "";
  String STRTELMOBILERIF = "";
  String CODEFFETTOAPPUNT = "";
  String CODESITOAPPUNT = "";
  String CODSTATOAPPUNTAMENTO = "";
  String NUMKLOAGENDA = "";        
  String PRGAZIENDA = "";    
  String PRGUNITA = "";
  String OLD_STATO = "5";
  
  linkDett += "&OLDSTATO=" + OLD_STATO;

  String rowRagioneSociale = "";
  String rowPIva = "";
  String rowIndirizzo = "";
  String rowComune = "";
  String rowTel = "";

  Testata testata = new Testata(null,null,null,null);

  String prgSlot = "";
  boolean nuovoApp = true;
  String oraSlot = "";
  String minutiSlot = "";
  String codServizioSlot = "";
  String prgSpiSlot = "";
  String cdnParUtente = Integer.toString(user.getCodut());
  
  String data_cod = data;
  Vector rowsEsito = serviceResponse.getAttributeAsVector("COMBO_ESITO_APPUNTAMENTO.ROWS.ROW");
  String flgInvioEsitoAnpal = "";
  String idAppuntamentoAnpal = "";
  boolean isAppAnpal = false;
  String codEsitoMinAppuntamento = "";
  String codCpiMin = "";
  boolean rigaEsitoAnpalOk = false;
  boolean rigaEsitoAnpalKo = false;
  String msgEsitoAnpal = null;
  boolean canInvioAnpal = false;
  boolean viewUltimoInvioAnpal = false;
  String dataUltimoInvioAnpal="";
  String esitoNotificaUltimoInvioAnpal = "";
  String esitoAppuntamentoUltimoInvioAnpal = "";

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
  <script src="../../js/ComboPair.js"></script>
  
  <title>Nuovo appuntamento</title>  
</head>

<body class="gestione" onload="checkError();">
<af:error />


<p class="titolo">Nuovo Appuntamento</p>
<p>
<%@ include file="dett_appuntamento.inc" %>

</body>
</html>
