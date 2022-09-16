<!-- INSERIMENTO DETTAGLIO AVVIAMENTO -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                   
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.module.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.module.movimenti.InfoLavoratore,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%  
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "MovDettaglioAvviamentoInserisciPage");
  boolean canModify = attributi.containsButton("INSERISCI");
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");

  //Oggetti per l'applicazione dello stile
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

  //Guardo il contesto in cui opero
  String currentcontext = "inserisci";
  boolean inserisci = true;
  boolean salva = false;
  boolean valida = false;
  boolean rettifica = false;
  boolean sospeso = false;
  
  //guardo se provengo da un salvataggio o da un'altra linguetta o se sto consultando (sola lettura)
  String action = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTION");
  boolean aggiorna = action.equalsIgnoreCase("aggiorna");
  boolean naviga = action.equalsIgnoreCase("naviga");
  boolean consulta = false;

  //guardo se precedentemente ero in consultazione
  String actionprec = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTIONPREC");
  boolean actionprecaggiorna = actionprec.equalsIgnoreCase("aggiorna");
  boolean actionprecnaviga = actionprec.equalsIgnoreCase("naviga");
  boolean actionprecconsulta = actionprec.equalsIgnoreCase("consulta");

  //Il boolean seguente indica che si sta eseguendo un inserimento collegato
  boolean insertCollegato = false;
  String collegato = StringUtils.getAttributeStrNotNull(serviceRequest, "COLLEGATO");
  if (inserisci && collegato.equalsIgnoreCase("precedente")) {
    insertCollegato = true;
  }
  
  String strLavCollMirato = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATORECOLLMIRATO");
  String flgLegge68="";
  String forzaInsEtaApprendista = StringUtils.getAttributeStrNotNull(serviceRequest, "FORZA_INSERIMENTO_ETA_APPRENDISTATO");
  if (forzaInsEtaApprendista.equals("")) forzaInsEtaApprendista = "false";
  
%>

<%@ include file="../common/include/_segnalazioniGgRit.inc" %>
<%@ include file="../GestioneOggettoMovimento.inc" %> 
<%
  //inizializzazione variabili
  String prgMovimento = "";
  String prgMovimentoPrec = "";
  String prgMovimentoSucc = "";  
  String prgMovimentoApp = "";
  String prgAzienda = "";
  String codAtecoUAz = "";
  String prgUnita = "";
  String cdnLavoratore = "";
  String datComunicaz = "";
  String codTipoMov = "";
  String datInizioMov = "";
  String strMatricola = "";
  String codMonoTempo = "";
  String datFineMov = "";
  String codOrario = "";
  String numOreSett = "";
  String codTipoAss = "";
  String descrTipoAss = "";
  String codMonoTipo = "";
  String codNormativa = "";
  String codMansione = "";
  String strDesAttivita = "";
  String codContratto = "";
  String flgSocio = "";
  String codCCNL = "";
  String strDescrizioneCCNL = "";  
  String decRetribuzioneMen = "";
  String numLivello = "";
  String codAgevolazione = "";
  String cdnUtIns = "";
  String dtmIns = "";
  String numKloMov = "";
  String numKloMovPrec = ""; 
  String codGrado = "";
  String flgArtigiana = "";
  String codMonoStato = "";
  String posInps = "";
  String patInail = "";
  String codTipoAzienda = "";
  String codNatGiurAz = "";  
  String strNumAlboInterinali = "";
  String strNumRegistroCommitt = "";
  String prgAziendaUtil = ""; 
  String prgUnitaUtil = ""; 
  String luogoDiLavoro = "";
  String personaleInterno = "";
  String codMonoTempoAvv = ""; 
  String dataFineMovPrec = "";
  String dataInizioAvv = "";
  String dataInizioMovPrec = "";  
  String codtipomovprec = "";
  String codMonoMovDich = ""; 
  String datFineMovEff = "";
  String codMonoTipoFine = "";
  String descrMansione = "";
  String descrTipoMansione = "";
  String numGgTraMovComunicaz = "";

  String decretribmensanata = "";
  String datasanata = "";
  String tipodichsanata = "";
  String datInizioMovSupReddito = "";
  String codiceDich = "";
  String prgDichLav = "";  

  String codStatoAtto = "";  
  String numProtocolloV = "";  
  String annoProtV = "" ;
  String kLockProt = "";
  String strEnteRilascio = "";
  String flgAutocertificazione = "N";
  String numContratto = "";
  String dataInizio = "";
  String dataFine = "";
  String legaleRapp = "";
  String numSoggetti = "";
  String classeDip = "";
  String autorizzaDurataTD = "";

  //Apprendistato
  String strCognomeTutore = "";
  String strNomeTutore = "";
  String strCodiceFiscaleTutore = "";
  String flgTitolareTutore = "";
  boolean titolareTutore = false;
  String numAnniEspTutore = "";
  String strLivelloTutore = "";
  String codMansioneTutore = "";
  String strMansioneTutore = "";
  String strTipoMansioneTutore = "";
  String datVisitaMedica = "";
  String strNote = "";
  String numMesiApprendistato = "";
  String flgArtigiano = "";
  boolean artigiano = false;
  boolean movIsEnabled = false;
  String codMotAnnullamento = "";
  String codCCNLAzienda = "";
  //
  String strCodiceFiscaleLav = "";
  String strCodiceFiscaleAz  = "";
  //Variabili per la gestione della protocollazione
  BigDecimal prgDoc = null;
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String     datProtV     = "";
  String     oraProtV     = "";
  String     docInOut     = "";
  String     docRif       = "";
  String     codStatoAttoV = "";
  // agricoltura 
  String numGGPrevistiAgr = null;
  String numGGEffettuatiAgr = null;
  String codCategoria   = "";
  String codLavorazione = "";
  //qualifica srq per apprendistato
  String codQualificaSrq = "";
  String descQualificaSrq = "";
  String visSezioneSRQ = "inline";
  // CM 07/02/2007 Savino: convenzioni ed incentivi art. 13
  String numConvenzione = "";
  String datConvenzione = "";
  String datFineSgravio = "";
  String decImportoConcesso = "";
   //
  //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
  String codtipodocex = "";
  String strnumdocex = "";
  String codmotivopermsoggex = "";
  String codtipoenteprev = "";
  String strcodiceenteprev = "";
  String codtipotrasf = "";
  String codtipocontratto = "";
  String strnumagsomm = "";
  String strindennitasom = "";
  String datiniziomissione = "";
  String datfinemissione = "";
  String strrischioasbsil = "";
  String strvocetariffa1 = "";
  String strvocetariffa2 = "";
  String strvocetariffa3 = "";
  String codsoggetto = "";
  // recupero informazioni protocollazione
  numProtV = SourceBeanUtils.getAttrBigDecimal(serviceRequest, "numProtocollo", null);
  datProtV = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "dataProt");
  oraProtV = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "oraProt");
  docInOut     = "I";
  docRif       = "Movimenti amministrativi";
  codStatoAtto = "";
  String strNumAnnoProt = StringUtils.getAttributeStrNotNull(serviceRequest, "numAnnoProt");
  if (numProtV == null)
  	numProtV = SourceBeanUtils.getAttrBigDecimal(serviceRequest, "numProtocollo2", null);
  if (datProtV.equals(""))
  	datProtV = StringUtils.getAttributeStrNotNull(serviceRequest, "dataProt2");
  if (oraProtV.equals(""))
  	oraProtV = StringUtils.getAttributeStrNotNull(serviceRequest, "oraProt2");  
  if (strNumAnnoProt.equals(""))
  	strNumAnnoProt = StringUtils.getAttributeStrNotNull(serviceRequest, "numAnnoProt2");  
  if (strNumAnnoProt!=null && !strNumAnnoProt.equals(""))
  	numAnnoProtV = new BigDecimal(strNumAnnoProt);
  	

  SourceBean dataOrigin = serviceRequest;
    //16/01/2001 Davide: aggiungiunti campi inerenti l'agricoltura
    codCategoria   = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCATEGORIA");
    codLavorazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODLAVORAZIONE");

  // questo campo e' valorizzato a true quando il processor di controllo movimenti simili chiede conferma 
  // all'operatore dell'inserimento. Se poi arriva un'altra confirm bisogna evitare di ripassare per il controllo
  // dei movimenti simili  
  String confermaMovSimili = StringUtils.getAttributeStrNotNull(dataOrigin, "CONFERMA_CONTROLLO_MOV_SIMILI");
  if (confermaMovSimili.equals("")) confermaMovSimili="false";

  //altri campi di confirm
  String confermaDiscoLungaDurata = StringUtils.getAttributeStrNotNull(dataOrigin, "CONFIRM_DISOC_LUNGADURATA");
  String confermaNoMobilita       = StringUtils.getAttributeStrNotNull(dataOrigin, "CONFIRM_NO_MOBILITA");
  String strAzIntNumContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTNUMCONTRATTO");
  String datAzIntInizioContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTINIZIOCONTRATTO");
  
  //Setto l'origine dei dati generali da recuperare in caso di inserimento, 
  //mi arrivano dall'oggetto in sessione se sto inserendo o validando e l'oggetto
  // è abilitato, altrimenti dalla request  
  if (mov.isEnabled()) {
    dataOrigin = mov.getFieldsAsSourceBean();
  }
  //
  strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull( serviceRequest, "STRCODICEFISCALELAV");
  strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull( serviceRequest, "STRCODICEFISCALEAZ");
  if (strCodiceFiscaleLav.equals(""))
  	strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull(dataOrigin, "STRCODICEFISCALELAV");
  if (strCodiceFiscaleAz.equals(""))
    strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull(dataOrigin, "STRCODICEFISCALEAZ");
  //Solo dall'oggetto in sessione
  strCognomeTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCOGNOMETUTORE");
  strNomeTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNOMETUTORE");
  strCodiceFiscaleTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEFISCALETUTORE"); 
  flgTitolareTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGTITOLARETUTORE");
  if (flgTitolareTutore.equals("S")) titolareTutore = true;
  numAnniEspTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMANNIESPTUTORE");
  strLivelloTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"STRLIVELLOTUTORE");
  codMansioneTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"CODMANSIONETUTORE");
  strTipoMansioneTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"STRTIPOMANSIONETUTORE");
  strMansioneTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"STRMANSIONETUTORE");
  datVisitaMedica = StringUtils.getAttributeStrNotNull(dataOrigin,"DATVISITAMEDICA");
  strNote = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNOTE");
  codCCNLAzienda=StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNLAz");
  numMesiApprendistato = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMMESIAPPRENDISTATO");
  flgArtigiano = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGARTIGIANO");
  if (flgArtigiano.equals("S")) artigiano = true;
  
  //Recupero dati sempre presenti nella request
  codMonoTempoAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTEMPOAVV");
  codMonoMovDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOMOVDICH");
  if (codMonoMovDich.equalsIgnoreCase("C")){
    flgAutocertificazione = "S";
  }

  numProtocolloV = SourceBeanUtils.getAttrStrNotNull(dataOrigin, "numProtocollo");
  annoProtV = StringUtils.getAttributeStrNotNull(dataOrigin, "numAnnoProt");
  kLockProt = StringUtils.getAttributeStrNotNull(dataOrigin, "KLOCKPROT");
  strEnteRilascio = StringUtils.getAttributeStrNotNull(dataOrigin, "STRENTERILASCIO");    
  codMotAnnullamento = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMOTANNULLAMENTO");
  codStatoAtto = StringUtils.getAttributeStrNotNull(dataOrigin, "CODSTATOATTO");
  codStatoAttoV = codStatoAtto;
  sospeso = codStatoAtto.equalsIgnoreCase("SS") ? true : false;
  
  //Il codTipoMov serve nel caso che l'utente lo abbia cambiato e stia facendo un aggiornamento, è sempre letto dalla request
  codTipoMov = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOV");

  //Gestione dei movimenti collegati
  prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
  prgMovimentoSucc = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOSUCC"); 

  //in caso di inserimento estraggo i dati generali necessari all'inserimento o validazione,
  //altrimenti mi bastano i progressivi del movimento o della tabella di appoggio
  if (dataOrigin != null) { 
    //Il codTipoAzienda e la NatGiuridica servono per il refresh delle combo collegate
    codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOAZIENDA");
    codNatGiurAz = StringUtils.getAttributeStrNotNull(dataOrigin, "CODNATGIURIDICAAZ");
    strNumAlboInterinali = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMALBOINTERINALI");
    strNumRegistroCommitt = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMREGISTROCOMMITT");
    posInps = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPOSINPS");
    patInail = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPATINAIL");
    datComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin, "datComunicaz");
    prgAziendaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDAUTILIZ");
    prgUnitaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITAUTILIZ");
    luogoDiLavoro = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLUOGODILAVORO");
    personaleInterno = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGINTERASSPROPRIA");
    if (codTipoAzienda.equals("INT") && personaleInterno.equals("S")){
       codTipoAzienda="AZI";
    }
    numContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "numContratto");
    dataInizio = StringUtils.getAttributeStrNotNull(dataOrigin, "dataInizio");
    dataFine = StringUtils.getAttributeStrNotNull(dataOrigin, "dataFine");
    legaleRapp = StringUtils.getAttributeStrNotNull(dataOrigin, "legaleRapp");
    numSoggetti = StringUtils.getAttributeStrNotNull(dataOrigin, "numSoggetti");
    classeDip = StringUtils.getAttributeStrNotNull(dataOrigin, "classeDip");
  }
  
  //Setto l'origine dei dati da visualizzare, in fase di consultazione mi arrivano dal DB
  if (insertCollegato) { dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMov.ROWS.ROW");}

  if (dataOrigin != null) {
    //I dati di azienda e lavoratore servono per le informazioni all'inizio della pagina
    prgAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "prgAzienda");
    prgUnita = StringUtils.getAttributeStrNotNull(dataOrigin, "prgUnita");
    cdnLavoratore = StringUtils.getAttributeStrNotNull(dataOrigin, "cdnLavoratore");
	codAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "CODATECOUAZ");

    //Dati per le informazioni di testata nella tabella principale del dettaglio 
    dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");
    dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");
    datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
    codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE"); 
  }
  
  //Estraggo i dati da visualizzare
  if ((dataOrigin != null)) {
    datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov");   
    strMatricola = StringUtils.getAttributeStrNotNull(dataOrigin, "strMatricola");   
    codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo");   
    datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMov");   
    codOrario = StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");   
    numOreSett = StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");   
    codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss");   
    if (codTipoAss.equals(""))
      descrTipoAss = ""; 
    else 
   	descrTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoAss");
    codMonoTipo = StringUtils.getAttributeStrNotNull(dataOrigin,"codMonoTipo");
    codNormativa = StringUtils.getAttributeStrNotNull(dataOrigin, "codNormativa");   
    codMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMansione");   
    strDesAttivita = StringUtils.getAttributeStrNotNull(dataOrigin, "strDesAttivita");   
    codContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "codContratto");   
    flgSocio = StringUtils.getAttributeStrNotNull(dataOrigin, "flgSocio");   
    codCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNL");   
    strDescrizioneCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "strCCNL");   
    decRetribuzioneMen = StringUtils.getAttributeStrNotNull(dataOrigin, "decRetribuzioneMen");   
    numLivello = StringUtils.getAttributeStrNotNull(dataOrigin, "numLivello");   
    codAgevolazione = StringUtils.getAttributeStrNotNull(dataOrigin, "codAgevolazione");   
    codGrado = StringUtils.getAttributeStrNotNull(dataOrigin, "codGrado");   
    flgArtigiana = StringUtils.getAttributeStrNotNull(dataOrigin, "flgArtigiana");   
    codMonoStato = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoStato");  
    descrMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrMansione");   
    descrTipoMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoMansione");
    numGgTraMovComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGTRAMOVCOMUNICAZIONE");
    numGGPrevistiAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGPREVISTIAGR");
    flgLegge68 = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLEGGE68");
    
    //Autorizzazione alla durata dei movimenti a TD data dall'utente
    autorizzaDurataTD = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGAUTORIZZADURATATD");
    //QUALIFICA SRQ
    codQualificaSrq = StringUtils.getAttributeStrNotNull(dataOrigin, "CODQUALIFICASRQ");
    descQualificaSrq = StringUtils.getAttributeStrNotNull(dataOrigin, "descQualificaSrq");
    // 06/02/2007 Savino: convenzione(CM)
    numConvenzione = StringUtils.getAttributeStrNotNull(dataOrigin, "numConvenzione");
    datConvenzione = StringUtils.getAttributeStrNotNull(dataOrigin, "datConvenzione");
    // 06/02/2007 Savino: CM incentivi ant. 13
    datFineSgravio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINESGRAVIO");
    decImportoConcesso = StringUtils.getAttributeStrNotNull(dataOrigin, "DECIMPORTOCONCESSO");
    if (codTipoAzienda.equalsIgnoreCase("INT")) {
    	datiniziomissione = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIORAPLAV");
    	datfinemissione = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINERAPLAV");
    }
  } 
  
  //Se è la prima volta che visualizzo la pagina devo prevalorizzare alcuni campi
  if (!mov.isEnabled() && (dataOrigin != null)) {
    codCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNLAz");
    strDescrizioneCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "descrCCNLAz");
	numMesiApprendistato = serviceResponse.containsAttribute("M_MovGetDettMovApp.ROWS.ROW")?serviceResponse.getAttribute("M_MovGetNumDurataApprendist.ROWS.ROW.NUMMESIAPPRENDISTATO").toString():"";
  }

  //Estraggo dal DB i dati sui movimenti precedente e successivo e setto i boolean
  boolean precedente = false;
  boolean successivo = false;
  InfMovimentoCollegato infMovPrec = new InfMovimentoCollegato(prgMovimentoPrec);
  InfMovimentoCollegato infMovSucc = new InfMovimentoCollegato(prgMovimentoSucc);
  if (infMovPrec.exists()) {
    precedente = true;
    numKloMovPrec = infMovPrec.getNumKloMov();
    codtipomovprec = infMovPrec.getCodTipoMov();
    dataFineMovPrec = infMovPrec.getDatFineMov();
    if (dataFineMovPrec == null) {dataFineMovPrec = "";}
  }
  if (infMovSucc.exists()) {
    successivo = true;
  }
  //Aggiorno il valore di collegato 
  collegato = "nessuno";
  if (precedente && successivo) {collegato = "entrambi";}
  else if (precedente) {collegato = "precedente";}
  else if (successivo) {collegato = "successivo";}
  
  //Gestione del lock per il movimento
  if (dataOrigin != null){
    numKloMov = StringUtils.getAttributeStrNotNull(dataOrigin, "numKloMov");
  }
  //Oggetti per la generazione delle informazioni sul lavoratore e azienda
  InfCorrentiLav testataLav = null;
  InfCorrentiAzienda testataAz = null;
  if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) {
    testataLav = new InfCorrentiLav( RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    testataAz = new InfCorrentiAzienda(prgAzienda,prgUnita);
  } 
  
  movIsEnabled = mov.isEnabled();
  //abilito l'oggetto se non era già abilitato e non sono in consultazione
  if (!mov.isEnabled()) {
    mov.enable();
  }
  
%>

<html>
  <head>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
        <af:linkScript path="../../js/"/>
    <title>Dettaglio Movimento</title>
    <%@ include file="../../presel/Function_CommonRicercaCCNL.inc" %>  
    <%@ include file="../../movimenti/DynamicRefreshCombo.inc" %>
    <%@ include file="../../global/fieldChanged.inc" %>
    <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,
                                 "cdnLavoratore="+cdnLavoratore+
                                 "&prgMovimento="+prgMovimento+
                                 "&PAGERITORNOLISTA="+StringUtils.getAttributeStrNotNull(serviceRequest, "PAGERITORNOLISTA"));
      %>
     <!--
      var consulta = <%=consulta%>;
      var codTipoAzienda = '<%=codTipoAzienda%>';
      var codNatGiuridicaAz = '<%=codNatGiurAz%>';
      var finestraAperta;
      var precedente = <%=precedente%>;
      var inserisci = '<%=inserisci%>';
      var oggettoInSessione = <%=movIsEnabled%>;
      var contesto = 'inserisci';
      var sospesoJS = <%=sospeso%>;
            
    -->
    </SCRIPT>
    <%-- necessari per il controllo del lavoro autonomo --%>
    <script  language="Javascript">
		var codiceFiscaleLav = "<%=strCodiceFiscaleLav %>";
		var codiceFiscaleAz = "<%=strCodiceFiscaleAz %>";
    </script>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
<% 
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) { 
	// END-PARTE-TEMP
%>	
    <script type="text/javascript" src="../../js/movimenti/avviamento/func_avviamento.js" language="JavaScript"></script>
<% 
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP
%>   
    <script type="text/javascript" src="../../js/movimenti/avviamento/func_avviamento_cm.js" language="JavaScript"></script>
<% 
	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
%>
    <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_confirmDaStOcc.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_confirmDaControlloMov.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/avviamento/gestioneAutorizzazione.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/Indietro.js" language="JavaScript"></script> 
    <script type="text/javascript" src="../../js/movimenti/common/Linguette.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>

    <%@ include file="../common/include/calcolaDiffGiorni.inc" %>
    <%@ include file="../common/include/_funzioniGenerali.inc" %>
  </head>
<% 
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOn()) { 
	// END-PARTE-TEMP
%>	
  <body class="gestione" onload="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');rinfresca();calcolaDiffGiorni(document.Frm1.datInizioMov, document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange,oggettoInSessione);gestVisualGiorniRitardo();impostaGrado();abilitazioneConvenzione()">
<% 
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP
%>
  <body class="gestione" onload="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');rinfresca();calcolaDiffGiorni(document.Frm1.datInizioMov, document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange,oggettoInSessione);gestVisualGiorniRitardo();impostaGrado();">
<%    
	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
%>
  
  <% if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) { testataLav.show(out); }%>
  <% if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) { testataAz.show(out); }%>

  <%@ include file="../common/include/GestioneRisultati.inc" %>
  
  <%@ include file="../common/include/LinguetteAvviamento.inc" %>
  <%@ include file="../avviamento/include/gestioneAutorizzazione.inc" %>
  <center>
<%-- 
	07/02/2007 Savino: CM 
	se CM e' off verra' chiamata la funzione di controllo sulla convenzione, ma questa non fara' niente perche' in essa
	c'e' il controllo sulla variabile javascript CM_OFF 
--%>
<% 
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOn()) { 
	// END-PARTE-TEMP
%>	
  <af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="selezionaPulsanteApprendistato() && checkDatInizioMovImpatti(document.Frm1.datInizioMov) && controlloLavAutonomo() && checkNumOreSettimanali() && ControllaInserimento() && controllaConvenzione()">
  
  <%out.print(htmlStreamTop);%>
  <%@ include file="../../movimenti/common/include/_protocollazione.inc" %>
  <table class="main" cellspacing="0"  cellpadding="0" width="96%" border="0">
    <%@ include file="../../movimenti/common/include/InfoTestataMovimento.inc" %>       
    <%@ include file="../avviamento/include/campi_avviamento_cm.inc" %>
  </table>
  <%out.print(htmlStreamBottom);%>
  <input type="hidden" name="DATCOMUNICAZ" value="<%=datComunicaz%>"/>
  <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
  <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
  <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
  <input type="hidden" name="NUMKLOMOV" value="<%=numKloMov%>"/>
  <input type="hidden" name="CODTIPOMOV" value="<%=codTipoMov%>"/>    
  <input type="hidden" name="DATAINIZIOAVV" value="<%=dataInizioAvv%>"/>     
  <input type="hidden" name="DATINIZIOMOVPREC" value="<%=dataInizioMovPrec%>"/> 
  <input type="hidden" name="CODMONOTEMPOAVV" value="<%=codMonoTempoAvv%>"/>
  <input type="hidden" name="DATFINEMOVPREC" value="<%=dataFineMovPrec%>"/>
  <input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>"/>
  <input type="hidden" name="CODNATGIURIDICAAZ" value="<%=codNatGiurAz%>"/>        
  <input type="hidden" name="STRNUMALBOINTERINALI" value="<%=strNumAlboInterinali%>"/> 
  <input type="hidden" name="STRNUMREGISTROCOMMITT" value="<%=strNumRegistroCommitt%>"/>
  <input type="hidden" name="STRPOSINPS" value="<%=posInps%>"/>     
  <input type="hidden" name="STRPATINAIL" value="<%=patInail%>"/>  
  <input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=prgAziendaUtil%>"/> 
  <input type="hidden" name="PRGUNITAUTILIZ" value="<%=prgUnitaUtil%>"/>
  <input type="hidden" name="STRAZINTNUMCONTRATTO" value="<%=strAzIntNumContratto%>"/>
  <input type="hidden" name="DATAZINTINIZIOCONTRATTO" value="<%=datAzIntInizioContratto%>"/>
  <input type="hidden" name="STRLUOGODILAVORO" value="<%=luogoDiLavoro%>"/>
  <input type="hidden" name="FLGINTERASSPROPRIA" value="<%=personaleInterno%>"/>
  <input type="hidden" name="CODMONOMOVDICH" value="<%=codMonoMovDich%>"/>
  <input type="hidden" name="DATFINEMOVEFFETTIVA" value="<%=datFineMovEff%>"/>      
  <input type="hidden" name="CODMONOTIPOFINE" value="<%=codMonoTipoFine%>"/>
  <input type="hidden" name="numContratto" value="<%=numContratto%>">
  <input type="hidden" name="dataInizio" value="<%=dataInizio%>">
  <input type="hidden" name="dataFine" value="<%=dataFine%>">
  <input type="hidden" name="legaleRapp" value="<%=legaleRapp%>">
  <input type="hidden" name="numSoggetti" value="<%=numSoggetti%>">
  <input type="hidden" name="classeDip" value="<%=classeDip%>">
  <input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>      
  <input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
  <input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>

  <input type="hidden" name="ACTION" value="aggiorna"/>
  <input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
  <input type="hidden" name="COLLEGATO" value="<%=collegato%>"/>
  <input type="hidden" name="ACTIONPREC" value="<%=action%>"/>
  <input type="hidden" name="PROVENIENZA" value="linguetta"/>
        
  <input type="hidden" name="PAGE" value="InserisciMovimentoCompletoPage"/>
  <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<%--
  <input type="hidden" name="numProtocollo" value="<%=numProtocolloV%>"/>
  <input type="hidden" name="numAnnoProt" value="<%=annoProtV%>"/>
--%>
  <input type="hidden" name="KLOCKPROT"  value="<%=kLockProt%>">
  <input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
  <input type="hidden" name="FLGAUTOCERTIFICAZIONE" value="<%=flgAutocertificazione%>" />
  <input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>" />

  <!-- GESTIONE APPRENDISTATO -->                  
  <input type="hidden" name="STRCOGNOMETUTORE" value="<%=strCognomeTutore%>" />
  <input type="hidden" name="STRNOMETUTORE" value="<%=strNomeTutore%>" />
  <input type="hidden" name="STRCODICEFISCALETUTORE" value="<%=strCodiceFiscaleTutore%>" />
  <input type="hidden" name="FLGTITOLARETUTORE" value="<%=flgTitolareTutore%>" />
  <input type="hidden" name="NUMANNIESPTUTORE" value="<%=numAnniEspTutore%>" />
  <input type="hidden" name="STRLIVELLOTUTORE" value="<%=strLivelloTutore%>" />
  <input type="hidden" name="CODMANSIONETUTORE" value="<%=codMansioneTutore%>" />
  <input type="hidden" name="STRMANSIONETUTORE" value="<%=strMansioneTutore%>" />
  <input type="hidden" name="STRTIPOMANSIONETUTORE" value="<%=strTipoMansioneTutore%>" />
  <input type="hidden" name="DATVISITAMEDICA" value="<%=datVisitaMedica%>" />
  <input type="hidden" name="NUMMESIAPPRENDISTATO" value="<%=numMesiApprendistato%>" />
  <input type="hidden" name="FLGARTIGIANA" value="<%=flgArtigiana%>" />
  <input type="hidden" name="STRNOTE" value="<%=strNote%>" />
  <input type="hidden" name="FORZA_INSERIMENTO" value="false"/>
  <input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false"/>
  <input type="hidden" name="FORZA_INSERIMENTO_ETA_APPRENDISTATO" value="<%=forzaInsEtaApprendista%>"/>
  <input type="hidden" name="CONFERMA_CONTROLLO_MOV_SIMILI" value="<%=confermaMovSimili%>"/>
  <input type="hidden" name="CONFIRM_DISOC_LUNGADURATA" value="<%=confermaDiscoLungaDurata%>"/>
  <input type="hidden" name="CONFIRM_NO_MOBILITA" value="<%=confermaNoMobilita%>"/>
  <input type="hidden" name="codCCNLAz" value="<%=codCCNLAzienda%>"/>
   <input type="hidden" name="LAVORATORECOLLMIRATO" value="<%=strLavCollMirato%>"/>
  
  <!--Gestione autorizzazione per movimenti a TD-->
  <input type="hidden" name="FLGAUTORIZZADURATATD" value="<%=autorizzaDurataTD%>"/>  
<center>        
  <input type="submit" class="pulsanti" name="submitbutton" value="Inserisci" onclick="resetFlagForzatura();"/>
<%@ include file="../common/include/GestioneCollegati.inc" %>
<%@ include file="../common/include/PulsanteRitornoLista.inc" %>
</center>
      </af:form>  
      
<% 
	// INIT-PARTE-TEMP
	} else { // pagina senza convenzioni ed incentivi art. 13
	// END-PARTE-TEMP
%>    
  <af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="selezionaPulsanteApprendistato() && checkDatInizioMovImpatti(document.Frm1.datInizioMov) && controlloLavAutonomo() && checkNumOreSettimanali() && ControllaInserimento()">

  <%out.print(htmlStreamTop);%>
  <%@ include file="../../movimenti/common/include/_protocollazione.inc" %>
  <table class="main" cellspacing="0"  cellpadding="0" width="96%" border="0">
    <%@ include file="../../movimenti/common/include/InfoTestataMovimento.inc" %>       
    <%@ include file="../avviamento/include/campi_avviamento.inc" %>
  </table>
  <%out.print(htmlStreamBottom);%>
  <input type="hidden" name="DATCOMUNICAZ" value="<%=datComunicaz%>"/>
  <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
  <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
  <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
  <input type="hidden" name="NUMKLOMOV" value="<%=numKloMov%>"/>
  <input type="hidden" name="CODTIPOMOV" value="<%=codTipoMov%>"/>    
  <input type="hidden" name="DATAINIZIOAVV" value="<%=dataInizioAvv%>"/>     
  <input type="hidden" name="DATINIZIOMOVPREC" value="<%=dataInizioMovPrec%>"/> 
  <input type="hidden" name="CODMONOTEMPOAVV" value="<%=codMonoTempoAvv%>"/>
  <input type="hidden" name="DATFINEMOVPREC" value="<%=dataFineMovPrec%>"/>
  <input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>"/>
  <input type="hidden" name="CODNATGIURIDICAAZ" value="<%=codNatGiurAz%>"/>        
  <input type="hidden" name="STRNUMALBOINTERINALI" value="<%=strNumAlboInterinali%>"/> 
  <input type="hidden" name="STRNUMREGISTROCOMMITT" value="<%=strNumRegistroCommitt%>"/>
  <input type="hidden" name="STRPOSINPS" value="<%=posInps%>"/>     
  <input type="hidden" name="STRPATINAIL" value="<%=patInail%>"/>  
  <input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=prgAziendaUtil%>"/> 
  <input type="hidden" name="PRGUNITAUTILIZ" value="<%=prgUnitaUtil%>"/>
  <input type="hidden" name="STRAZINTNUMCONTRATTO" value="<%=strAzIntNumContratto%>"/>
  <input type="hidden" name="DATAZINTINIZIOCONTRATTO" value="<%=datAzIntInizioContratto%>"/>
  <input type="hidden" name="STRLUOGODILAVORO" value="<%=luogoDiLavoro%>"/>
  <input type="hidden" name="FLGINTERASSPROPRIA" value="<%=personaleInterno%>"/>
  <input type="hidden" name="CODMONOMOVDICH" value="<%=codMonoMovDich%>"/>
  <input type="hidden" name="DATFINEMOVEFFETTIVA" value="<%=datFineMovEff%>"/>      
  <input type="hidden" name="CODMONOTIPOFINE" value="<%=codMonoTipoFine%>"/>
  <input type="hidden" name="numContratto" value="<%=numContratto%>">
  <input type="hidden" name="dataInizio" value="<%=dataInizio%>">
  <input type="hidden" name="dataFine" value="<%=dataFine%>">
  <input type="hidden" name="legaleRapp" value="<%=legaleRapp%>">
  <input type="hidden" name="numSoggetti" value="<%=numSoggetti%>">
  <input type="hidden" name="classeDip" value="<%=classeDip%>">
  <input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>      
  <input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
  <input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>

  <input type="hidden" name="ACTION" value="aggiorna"/>
  <input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
  <input type="hidden" name="COLLEGATO" value="<%=collegato%>"/>
  <input type="hidden" name="ACTIONPREC" value="<%=action%>"/>
  <input type="hidden" name="PROVENIENZA" value="linguetta"/>
        
  <input type="hidden" name="PAGE" value="InserisciMovimentoCompletoPage"/>
  <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<%--
  <input type="hidden" name="numProtocollo" value="<%=numProtocolloV%>"/>
  <input type="hidden" name="numAnnoProt" value="<%=annoProtV%>"/>
--%>
  <input type="hidden" name="KLOCKPROT"  value="<%=kLockProt%>">
  <input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
  <input type="hidden" name="FLGAUTOCERTIFICAZIONE" value="<%=flgAutocertificazione%>" />
  <input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>" />

  <!-- GESTIONE APPRENDISTATO -->
  <input type="hidden" name="STRCOGNOMETUTORE" value="<%=strCognomeTutore%>" />
  <input type="hidden" name="STRNOMETUTORE" value="<%=strNomeTutore%>" />
  <input type="hidden" name="STRCODICEFISCALETUTORE" value="<%=strCodiceFiscaleTutore%>" />
  <input type="hidden" name="FLGTITOLARETUTORE" value="<%=flgTitolareTutore%>" />
  <input type="hidden" name="NUMANNIESPTUTORE" value="<%=numAnniEspTutore%>" />
  <input type="hidden" name="STRLIVELLOTUTORE" value="<%=strLivelloTutore%>" />
  <input type="hidden" name="CODMANSIONETUTORE" value="<%=codMansioneTutore%>" />
  <input type="hidden" name="STRMANSIONETUTORE" value="<%=strMansioneTutore%>" />
  <input type="hidden" name="STRTIPOMANSIONETUTORE" value="<%=strTipoMansioneTutore%>" />
  <input type="hidden" name="DATVISITAMEDICA" value="<%=datVisitaMedica%>" />
  <input type="hidden" name="NUMMESIAPPRENDISTATO" value="<%=numMesiApprendistato%>" />
  <input type="hidden" name="FLGARTIGIANA" value="<%=flgArtigiana%>" />
  <input type="hidden" name="STRNOTE" value="<%=strNote%>" />
  <input type="hidden" name="FORZA_INSERIMENTO" value="false"/>
  <input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false"/>
  <input type="hidden" name="FORZA_INSERIMENTO_ETA_APPRENDISTATO" value="<%=forzaInsEtaApprendista%>"/>
  <input type="hidden" name="CONFERMA_CONTROLLO_MOV_SIMILI" value="<%=confermaMovSimili%>"/>
  <input type="hidden" name="CONFIRM_DISOC_LUNGADURATA" value="<%=confermaDiscoLungaDurata%>"/>
  <input type="hidden" name="CONFIRM_NO_MOBILITA" value="<%=confermaNoMobilita%>"/>
  <input type="hidden" name="codCCNLAz" value="<%=codCCNLAzienda%>"/>
  
  <!--Gestione autorizzazione per movimenti a TD-->
  <input type="hidden" name="FLGAUTORIZZADURATATD" value="<%=autorizzaDurataTD%>"/>  
  <!-- DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO -->
  <input type="hidden" name="CODTIPODOCEX"        value="<%=codtipodocex%>" />
  <input type="hidden" name="STRNUMDOCEX"         value="<%=strnumdocex%>" />
  <input type="hidden" name="CODMOTIVOPERMSOGGEX" value="<%=codmotivopermsoggex%>" />
  <input type="hidden" name="CODENTE"     value="<%=codtipoenteprev%>" />
  <input type="hidden" name="STRCODICEENTEPREV"   value="<%=strcodiceenteprev%>" />
  <input type="hidden" name="CODTIPOTRASF"        value="<%=codtipotrasf%>" />
  <input type="hidden" name="CODTIPOCONTRATTO"    value="<%=codtipocontratto%>" />
  <input type="hidden" name="STRNUMAGSOMMINISTRAZIONE"        value="<%=strnumagsomm%>" />
  <input type="hidden" name="DECINDENSOM"     value="<%=strindennitasom%>" />
  <input type="hidden" name="STRRISCHIOASBSIL"    value="<%=strrischioasbsil%>" />
  <input type="hidden" name="DECVOCETAR1"     value="<%=strvocetariffa1%>" />
  <input type="hidden" name="DECVOCETAR2"     value="<%=strvocetariffa2%>" />
  <input type="hidden" name="DECVOCETAR3"     value="<%=strvocetariffa3%>" />
  <input type="hidden" name="CODSOGGETTO"         value="<%=codsoggetto%>" />
<center>        
  <input type="submit" class="pulsanti" name="submitbutton" value="Inserisci" onclick="resetFlagForzatura();"/>
<%@ include file="../common/include/GestioneCollegati.inc" %>
<%@ include file="../common/include/PulsanteRitornoLista.inc" %>                         
</center>
      </af:form>  
<% 
	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
%>
      		</center>
  <script language="javascript">
    visualizzaPulsanteApprendistato("<%=codMonoTipo%>");
  </script>		
	</body>
  <%@ include file="../common/include/GestioneScriptRisultati.inc" %>
</html>  
