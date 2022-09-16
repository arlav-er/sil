<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.*,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"

%>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
    


<%
    String MODULE_NAME="SELECT_DETTAGLIO_AGENDA_MOD";
    String moduleSpi = "COMBO_SPI";

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
    String esitoApp = StringUtils.getAttributeStrNotNull(serviceRequest,"esitoApp");
    String strRagSoc = StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc");
    String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
    String strCognome = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
    String strNome = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
    String dataDal = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDal");
    String dataAl = StringUtils.getAttributeStrNotNull(serviceRequest, "dataAl");
    String piva = StringUtils.getAttributeStrNotNull(serviceRequest, "piva");
    String strCodiceFiscaleAz= StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscaleAz");
    String sel_anpal= StringUtils.getAttributeStrNotNull(serviceRequest, "strIdCoap");

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
      linkDett += "&piva=" + piva;
      linkDett += "&strCodiceFiscaleAz=" + strCodiceFiscaleAz;
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

	// codice per combo pair
	Vector rowsEsito = serviceResponse.getAttributeAsVector("COMBO_ESITO_APPUNTAMENTO.ROWS.ROW");

    SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
    SourceBean r = (SourceBean) cont.getAttribute("ROWS.ROW");

    
        String CODCPI = StringUtils.getAttributeStrNotNull(r, "CODCPI");
        String data = StringUtils.getAttributeStrNotNull(r, "DATA");
        String orario = StringUtils.getAttributeStrNotNull(r, "ORARIO");
        //String PRGAPPUNTAMENTO = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGAPPUNTAMENTO");
        String prgAppuntamentoStr =   (String) serviceRequest.getAttribute("PRGAPPUNTAMENTO"); 
        String prgSlot = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGSLOT");
        BigDecimal prgAppuntamento = null;
        if ((prgAppuntamentoStr!=null) && prgAppuntamentoStr.equals("") ){
          prgAppuntamento = (BigDecimal) r.getAttribute("PRGAPPUNTAMENTO");             
        }else{
          prgAppuntamento = new  BigDecimal(prgAppuntamentoStr);
        }
        String NUMMINUTI = "";
        BigDecimal numMinuti = (BigDecimal) r.getAttribute("NUMMINUTI");
        if(numMinuti!=null) { NUMMINUTI = numMinuti.toString(); }
        String numOraInizioEff = StringUtils.getAttributeStrNotNull(r,"NUMORAINIZIOEFF");
        String numOraFineEff = StringUtils.getAttributeStrNotNull(r,"NUMORAFINEEFF");
        String CODSERVIZIO = StringUtils.getAttributeStrNotNull(r,"CODSERVIZIO");
        String PRGSPI = "";
        BigDecimal prgSpi = (BigDecimal) r.getAttribute("PRGSPI");
        if(prgSpi!=null) { PRGSPI = prgSpi.toString(); }
        String PRGSPIEFF = "";
        BigDecimal prgSpiEff = (BigDecimal) r.getAttribute("PRGSPIEFF");
        if(prgSpiEff!=null) { PRGSPIEFF = prgSpiEff.toString(); }
        String PRGAMBIENTE = "";
        BigDecimal prgAmbiente = (BigDecimal) r.getAttribute("PRGAMBIENTE");
        if(prgAmbiente!=null) { PRGAMBIENTE = prgAmbiente.toString(); }
        //CLOB TXTNOTE=(CLOB) r.getAttribute("TXTNOTE");
        String txtNote = StringUtils.getAttributeStrNotNull(r,"TXTNOTE");
        String PRGTIPOPRENOTAZIONE = "";
        BigDecimal prgTipoPrenotazione = (BigDecimal) r.getAttribute("PRGTIPOPRENOTAZIONE");
        if(prgTipoPrenotazione!=null) { PRGTIPOPRENOTAZIONE = prgTipoPrenotazione.toString(); }
        String STRTELRIF = StringUtils.getAttributeStrNotNull(r,"STRTELRIF");
        String STREMAILRIF = StringUtils.getAttributeStrNotNull(r,"STREMAILRIF");
        String STRTELMOBILERIF = StringUtils.getAttributeStrNotNull(r,"STRTELMOBILERIF");
        String CODEFFETTOAPPUNT = StringUtils.getAttributeStrNotNull(r,"CODEFFETTOAPPUNT");
        String CODESITOAPPUNT = StringUtils.getAttributeStrNotNull(r,"CODESITOAPPUNT");
        String CODSTATOAPPUNTAMENTO = StringUtils.getAttributeStrNotNull(r, "CODSTATOAPPUNTAMENTO");
        String PRGAZIENDA = "";
        BigDecimal prgAzienda = (BigDecimal) r.getAttribute("PRGAZIENDA");
        if(prgAzienda!=null) { PRGAZIENDA = prgAzienda.toString(); }
        String PRGUNITA = "";
        BigDecimal prgUnita = (BigDecimal) r.getAttribute("PRGUNITA");
        if(prgUnita!=null) { PRGUNITA = prgUnita.toString(); }
        String rowRagioneSociale = StringUtils.getAttributeStrNotNull(r, "STRRAGIONESOCIALE");
        String rowPIva = StringUtils.getAttributeStrNotNull(r,"STRPARTITAIVA");
        String rowIndirizzo = StringUtils.getAttributeStrNotNull(r,"STRINDIRIZZO");
        String rowComune = StringUtils.getAttributeStrNotNull(r,"COMUNE_AZ");
        String rowTel = StringUtils.getAttributeStrNotNull(r,"STRTEL");
        BigDecimal NUMKLOAGENDA=(BigDecimal) r.getAttribute("NUMKLOAGENDA");        

        BigDecimal cdnUtIns = (BigDecimal) r.getAttribute("CDNUTINS");
        String dtmIns = (String) r.getAttribute("DTMINS");
        BigDecimal cdnUtMod = (BigDecimal) r.getAttribute("CDNUTMOD");
        String dtmMod = (String) r.getAttribute("DTMMOD");

        Testata testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

        String OLD_STATO = CODSTATOAPPUNTAMENTO;
        linkDett += "&OLDSTATO=" + OLD_STATO;
        String cdnParUtente = Integer.toString(user.getCodut());
        
        String data_cod = StringUtils.getAttributeStrNotNull(serviceRequest,"DATA_COD");
        linkDett += "&DATA_COD=" + data_cod;
        
        linkDett += "&PRGSPI=" +  prgSpi;
        linkDett += "&PRGSPIEFF=" +  prgSpiEff;
        linkDett += "&CODSERVIZIO=" +  CODSERVIZIO;
        
        
        String flgInvioEsitoAnpal = StringUtils.getAttributeStrNotNull(r,"FLGINVIOMIN_ESITOAPP");
        String idAppuntamentoAnpal = StringUtils.getAttributeStrNotNull(r,"idAnpal");
        boolean isAppAnpal = false;
        if(StringUtils.isFilledNoBlank(idAppuntamentoAnpal)){
        	isAppAnpal = true;
        }
    
        String codEsitoMinAppuntamento = StringUtils.getAttributeStrNotNull(r,"CODMINESITOAPP");
        String codCpiMin = StringUtils.getAttributeStrNotNull(r,"CODCPIMIN");
        boolean rigaEsitoAnpalOk = serviceResponse.containsAttribute("M_InvioEsitoAppuntamentoANPAL.ESITO_APPUNTAMENTO_ANPAL_OK");
        String msgEsitoAnpal = null;
        if(rigaEsitoAnpalOk){
        	msgEsitoAnpal = (String) serviceResponse.getAttribute("M_InvioEsitoAppuntamentoANPAL.ESITO_APPUNTAMENTO_ANPAL_OK");
        } 
        boolean rigaEsitoAnpalKo = serviceResponse.containsAttribute("M_InvioEsitoAppuntamentoANPAL.ESITO_APPUNTAMENTO_ANPAL_KO");
        if(rigaEsitoAnpalKo){
        	msgEsitoAnpal = (String) serviceResponse.getAttribute("M_InvioEsitoAppuntamentoANPAL.ESITO_APPUNTAMENTO_ANPAL_KO");
        }
%>

<%
boolean nuovoApp=false;
MODULE_NAME = "MALLINEASLOT";
String msgAllineamento = "";
if(serviceResponse.containsAttribute(MODULE_NAME)) {
  cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
  SourceBean rowAllineamento = (SourceBean) cont.getAttribute("ROW");
  String CodiceRit = "";
  if(rowAllineamento!=null) { CodiceRit = StringUtils.getAttributeStrNotNull(rowAllineamento, "CodiceRit"); }
  if(CodiceRit.equals("0")) { msgAllineamento = "Allineamento Slot avvenuto con successo"; }
  else { 
    if(CodiceRit.equals("-1")) { msgAllineamento = "Errore nell'esecuzione della procedura."; }
    else {  msgAllineamento = "Allineamento Slot non riuscito"; }
  }
}

MODULE_NAME = "MDATI_SLOT_APP";
cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
String oraSlot = "";
String minutiSlot = "";
String codServizioSlot = "";
String prgSpiSlot = "";
BigDecimal numLavoratoriDisp = null;
SourceBean rowSlot = null;
if(cont!=null) { rowSlot = (SourceBean) cont.getAttribute("ROW"); }
if(rowSlot!=null) {
  oraSlot = StringUtils.getAttributeStrNotNull(rowSlot, "ORASLOT");
  minutiSlot = StringUtils.getAttributeStrNotNull(rowSlot, "MINUTISLOT");
  codServizioSlot = StringUtils.getAttributeStrNotNull(rowSlot, "CODSERVIZIOSLOT");
  prgSpiSlot = StringUtils.getAttributeStrNotNull(rowSlot, "PRGSPISLOT");
  numLavoratoriDisp = (BigDecimal)rowSlot.getAttribute("numLavoratoriDisp");
}
%>

<%
String btnSalva = "Aggiorna";
String btnChiudi = "Chiudi";
PageAttribs attributi = new PageAttribs(user, "SELECT_AGENDA_PAGE");
boolean canInvioAnpal = attributi.containsButton("INVIO_APP_ANPAL");
boolean canModify = true;
if(!canModify) { btnChiudi = "Chiudi"; }


//Dati ultimo invio anpal
boolean viewUltimoInvioAnpal = false;
String dataUltimoInvioAnpal="";
String esitoNotificaUltimoInvioAnpal = "";
String esitoAppuntamentoUltimoInvioAnpal = "";
 
if(serviceResponse.containsAttribute("GetAppUltimoInvioAnpal") && serviceResponse.getAttribute("GetAppUltimoInvioAnpal") !=null){
	SourceBean ultimoInvioAnpal = (SourceBean) serviceResponse.getAttribute("GetAppUltimoInvioAnpal.ROWS.ROW");
	if(ultimoInvioAnpal!=null){
		dataUltimoInvioAnpal =  StringUtils.getAttributeStrNotNull(ultimoInvioAnpal, "dataInvio");
		esitoNotificaUltimoInvioAnpal =  StringUtils.getAttributeStrNotNull(ultimoInvioAnpal, "esitoNotifica");
		esitoAppuntamentoUltimoInvioAnpal =  StringUtils.getAttributeStrNotNull(ultimoInvioAnpal, "esitoAppuntamento");
		viewUltimoInvioAnpal = true;
	}
	
}

%>
<html>
<HEAD>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script src="../../js/ComboPair.js"></script>
  <title>Dettaglio Appuntamento</title>
</head>
 
<body class="gestione" onLoad="rinfresca();mappaEsiti();verificaInvioAnpal();">
<p class="titolo">Dettaglio Appuntamento</p>

<p align="left">
<%
SourceBean contErr = (SourceBean) serviceResponse.getAttribute("MINCONGRUENZE");
Vector rowsErr = contErr.getAttributeAsVector("ROWS.ROW");
SourceBean rowErr = null;
int j = 0;
String txtErr = "";
if(rowsErr.size()>0) {
%> 
  <img src="../../img/warning.gif">&nbsp;<b>Attenzione, si sono verificate le seguenti incongruenze con altri appuntamenti:</b>
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
<%if(!msgAllineamento.equals("")) {%>
  <ul><li><%=msgAllineamento%></li></ul>
<%}%>

<%if(rigaEsitoAnpalOk){ %>
	<ul><li> <font color="green"><%=msgEsitoAnpal%></font></li></ul>
<%}else if(rigaEsitoAnpalKo){%>
	<ul><li><font color="red"><%=msgEsitoAnpal%></font></li></ul>
<%}%>

<!--/p-->
<%@ include file="dett_appuntamento.inc" %>

</body>
</html>
