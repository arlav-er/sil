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
                  java.text.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%  
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "MovValidaAvviamentoPage");
  boolean canModify = attributi.containsButton("SALVA");
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
   
  //Oggetti per l'applicazione dello stile
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

  //Setto il contesto in cui opero
  
  String currentcontext = "valida";
  String context = "";
  
  if (serviceRequest.containsAttribute("CONTEXT")) {
  	context = StringUtils.getAttributeStrNotNull(serviceRequest,"CONTEXT");
  }
  if (serviceRequest.containsAttribute("CURRENTCONTEXT")) {
  	context = StringUtils.getAttributeStrNotNull(serviceRequest,"CURRENTCONTEXT");
  }  
  if (context.equals("validaArchivio")) {
  	currentcontext = context;
  }
  String forzaInsEtaApprendista = StringUtils.getAttributeStrNotNull(serviceRequest, "FORZA_INSERIMENTO_ETA_APPRENDISTATO");
  if (forzaInsEtaApprendista.equals("")) forzaInsEtaApprendista = "false";
  
  boolean inserisci = false;
  boolean salva = false;
  boolean valida = true;
  boolean rettifica = false;

  //guardo se provengo da un salvataggio o da un'altra linguetta
  String action = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTION");
  boolean aggiorna = false;
  boolean naviga = action.equalsIgnoreCase("naviga");
  boolean consulta = false;

  //guardo se precedentemente ero in consultazione
  String actionprec = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTIONPREC");
  boolean actionprecaggiorna = false;
  boolean actionprecnaviga = false;
  boolean actionprecconsulta = false;

  //Il boolean seguente indica che si sta eseguendo un inserimento collegato
  boolean insertCollegato = false;
  String collegato = StringUtils.getAttributeStrNotNull(serviceRequest, "COLLEGATO");
  
%>
<%@ include file="../common/include/_segnalazioniGgRit.inc" %>
<%@ include file="../GestioneOggettoMovimento.inc" %> 
<%
  //inizializzazione variabili
  String prgAzienda = "";
  String prgUnita = "";
  String cdnLavoratore = "";
    
  String prgMovimento = "";
  String prgMovimentoPrec = "";
  String prgMovimentoSucc = "";  
  String prgMovimentoApp = "";
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
  String codTipoAzienda = "";
  String codNatGiurAz = "";  
  String strNumAlboInterinali = "";
  String strNumRegistroCommitt = "";
  String prgAziendaUtil = ""; 
  String prgUnitaUtil = ""; 
  String codAtecoUAz = "";
  String luogoDiLavoro = "";
  String personaleInterno = "";
  String codMonoTempoAvv = ""; 
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
  String codMotAnnullamento = "";
  String autorizzaDurataTD = "";  
  
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
  
  String numConvenzione = "";
  String datConvenzione = "";
  String datFineSgravio = "";
  String decImportoConcesso = "";
  
  String flgLegge68="";
  
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
  // agricoltura
  String numGGPrevistiAgr = null;
  String numGGEffettuatiAgr = null; 
  //18/01/2006 Davide: introdotte per gestire il caso di giorni in agricoltura  
  String codCategoria   = "";
  String codLavorazione = "";
  String codLivelloAgr = "";
  String strVersioneTracciato = "";
  //qualifica srq per apprendistato
  String codQualificaSrq = "";
  String descQualificaSrq = "";
  String visSezioneSRQ = "inline";
  String codTipoComunicazione = "";
  String strOnSubmit = "";

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
  
  String strLavCollMirato = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATORECOLLMIRATO");

  // recupero informazioni protocollazione
  numProtV = SourceBeanUtils.getAttrBigDecimal(serviceRequest, "numProtocollo", null);
  datProtV = StringUtils.getAttributeStrNotNull(serviceRequest, "dataProt");
  oraProtV = StringUtils.getAttributeStrNotNull(serviceRequest, "oraProt");
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
  //Variabile per il cambiamento dei dati
  String flgCambiamentiDati = "";
  boolean movIsEnabled = false;
  String codFiscaleLavDB = StringUtils.getAttributeStrNotNull(serviceRequest, "STRCODICEFISCALE");
  //RECUPERO DEL FLAG DALLA PAGINA GENERALE
  flgCambiamentiDati = (String) serviceRequest.getAttribute("flgCambiamentiDati");  
  //Setto l'origine dei dati generali da recuperare in caso di inserimento, 
  //mi arrivano dall'oggetto in sessione se sto inserendo o validando e l'oggetto
  // è abilitato, altrimenti dalla request
  SourceBean dataOrigin = serviceRequest;
  String strAzIntNumContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTNUMCONTRATTO");
  String datAzIntInizioContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTINIZIOCONTRATTO");
  String confermaMovSimili = StringUtils.getAttributeStrNotNull(dataOrigin, "CONFERMA_CONTROLLO_MOV_SIMILI");
  if (confermaMovSimili.equals("")) confermaMovSimili="false";
  if (mov.isEnabled()) {
    dataOrigin = mov.getFieldsAsSourceBean();
  }

  //altri campi di confirm
  String confermaDiscoLungaDurata = StringUtils.getAttributeStrNotNull(dataOrigin, "CONFIRM_DISOC_LUNGADURATA");
  String confermaNoMobilita       = StringUtils.getAttributeStrNotNull(dataOrigin, "CONFIRM_NO_MOBILITA");

  //
  prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "prgAzienda");
  prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "prgUnita");
  
  cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
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
  cdnLavoratore = StringUtils.getAttributeStrNotNull(dataOrigin, "cdnLavoratore");
  if (flgTitolareTutore.equals("S")) titolareTutore = true;
  numAnniEspTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMANNIESPTUTORE");
  strLivelloTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"STRLIVELLOTUTORE");
  codMansioneTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"CODMANSIONETUTORE");
  strTipoMansioneTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"STRTIPOMANSIONETUTORE");
  strMansioneTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"STRMANSIONETUTORE");
  datVisitaMedica = StringUtils.getAttributeStrNotNull(dataOrigin,"DATVISITAMEDICA");
  strNote = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNOTE");
  numMesiApprendistato = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMMESIAPPRENDISTATO");
  flgArtigiano = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGARTIGIANO");
  if (flgArtigiano.equals("S")) artigiano = true;
  //recupero i dati relativi all'apprendistato
  SourceBean app = (SourceBean)serviceResponse.getAttribute("M_MovGetApprendistato.ROWS.ROW");
  if (!mov.isEnabled() && app != null) {
	  strCognomeTutore = StringUtils.getAttributeStrNotNull(app,"STRCOGNOMETUTORE");
	  strNomeTutore = StringUtils.getAttributeStrNotNull(app,"STRNOMETUTORE");
	  strCodiceFiscaleTutore = StringUtils.getAttributeStrNotNull(app,"STRCODICEFISCALETUTORE");
	  flgTitolareTutore = StringUtils.getAttributeStrNotNull(app,"FLGTITOLARETUTORE");
	  if (flgTitolareTutore.equals("S")) titolareTutore = true;
	  numAnniEspTutore = StringUtils.getAttributeStrNotNull(app,"NUMANNIESPTUTORE");
	  strLivelloTutore = StringUtils.getAttributeStrNotNull(app,"STRLIVELLOTUTORE");
	  codMansioneTutore = StringUtils.getAttributeStrNotNull(app,"CODMANSIONETUTORE");
	  datVisitaMedica = StringUtils.getAttributeStrNotNull(app,"DATVISITAMEDICA");
	  strNote = StringUtils.getAttributeStrNotNull(app,"STRNOTE");
	  flgArtigiano = StringUtils.getAttributeStrNotNull(app,"FLGARTIGIANA");
	  if (flgArtigiano.equals("S")) artigiano = true;
  }
  
  //Recupero dati sempre presenti nella request
  dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");
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
    datComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin, "datComunicaz");
    prgAziendaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDAUTILIZ");
    prgUnitaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITAUTILIZ");
    codAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "CODATECOUAZ");
    luogoDiLavoro = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLUOGODILAVORO");
    personaleInterno = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGINTERASSPROPRIA");
    numContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "numContratto");
    dataInizio = StringUtils.getAttributeStrNotNull(dataOrigin, "dataInizio");
    dataFine = StringUtils.getAttributeStrNotNull(dataOrigin, "dataFine");
    legaleRapp = StringUtils.getAttributeStrNotNull(dataOrigin, "legaleRapp");
    numSoggetti = StringUtils.getAttributeStrNotNull(dataOrigin, "numSoggetti");
    classeDip = StringUtils.getAttributeStrNotNull(dataOrigin, "classeDip");
  }
  
  //Setto l'origine dei dati da visualizzare, in fase di consultazione mi arrivano dal DB
  if (!mov.isEnabled()) { 
    dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovApp.ROWS.ROW");
    if(numMesiApprendistato.equals("")) numMesiApprendistato = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMMESIAPPRENDISTATO");
    if(numMesiApprendistato.equals("") && (boolean)serviceResponse.containsAttribute("M_MOVGETNUMDURATAAPPRENDIST.ROWS.ROW.NUMMESIAPPRENDISTATO")){
	    numMesiApprendistato = (SourceBeanUtils.getAttrBigDecimal(serviceResponse,"M_MOVGETNUMDURATAAPPRENDIST.ROWS.ROW.NUMMESIAPPRENDISTATO")).toString();
	}
  }

  if (dataOrigin != null) {
    //Dati per le informazioni di testata nella tabella principale del dettaglio 
    dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");    
    
    
    
    datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
    codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE");
    prgMovimentoApp = StringUtils.getAttributeStrNotNull(dataOrigin, "prgMovimentoApp"); 
    datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov");   
    strMatricola = StringUtils.getAttributeStrNotNull(dataOrigin, "strMatricola");   
    codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo");   
    datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMov");   
    codOrario = StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");   
    numOreSett = StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");   
    codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss");   
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
    //17/01/2001 Davide: aggiungiunti campi inerenti l'agricoltura
    codCategoria   = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCATEGORIA");
    codLavorazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODLAVORAZIONE");
    codLivelloAgr  = StringUtils.getAttributeStrNotNull(dataOrigin,"CODLIVELLOAGR");
    strVersioneTracciato = StringUtils.getAttributeStrNotNull(dataOrigin,"STRVERSIONETRACCIATO");
    //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
    codtipodocex        = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPODOCEX");
    strnumdocex         = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNUMDOCEX");
    codmotivopermsoggex = StringUtils.getAttributeStrNotNull(dataOrigin,"CODMOTIVOPERMSOGGEX");
    codtipoenteprev     = StringUtils.getAttributeStrNotNull(dataOrigin,"CODENTE");
    strcodiceenteprev   = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEENTEPREV");
    codtipotrasf        = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOTRASF");
    codtipocontratto    = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOCONTRATTO");
    strnumagsomm        = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNUMAGSOMMINISTRAZIONE");
    strindennitasom     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECINDENSOM");
    
    numConvenzione = StringUtils.getAttributeStrNotNull(dataOrigin, "numConvenzione");
    datConvenzione = StringUtils.getAttributeStrNotNull(dataOrigin, "datConvenzione");
    
    datFineSgravio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINESGRAVIO");
    decImportoConcesso = StringUtils.getAttributeStrNotNull(dataOrigin, "DECIMPORTOCONCESSO");
    
    flgLegge68 = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLEGGE68");
    
    if (codTipoAzienda.equalsIgnoreCase("INT")) {
    	datiniziomissione   = StringUtils.getAttributeStrNotNull(dataOrigin,"DATINIZIORAPLAV");
   	 	datfinemissione     = StringUtils.getAttributeStrNotNull(dataOrigin,"DATFINERAPLAV");
   	} 	
    strrischioasbsil    = StringUtils.getAttributeStrNotNull(dataOrigin,"STRRISCHIOASBSIL");
    strvocetariffa1     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR1");
    strvocetariffa2     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR2");
    strvocetariffa3     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR3");
    codsoggetto         = StringUtils.getAttributeStrNotNull(dataOrigin,"CODSOGGETTO");

    //Gestione del lock per il movimento
    numKloMov = StringUtils.getAttributeStrNotNull(dataOrigin, "numKloMov");

    //Autorizzazione alla durata dei movimenti a TD data dall'utente
    autorizzaDurataTD = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGAUTORIZZADURATATD");
    //QUALIFICA SRQ
	codQualificaSrq = StringUtils.getAttributeStrNotNull(dataOrigin, "CODQUALIFICASRQ");
    descQualificaSrq = StringUtils.getAttributeStrNotNull(dataOrigin, "descQualificaSrq");
    //lettura tipo comunicazione
    codTipoComunicazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOCOMUNIC");
  }
  
  if (codTipoComunicazione.equals("04")) {
    //si tratta dell'annullamento di una comunicazione precedente 
    //e quindi bisogna eseguire solo questa funzione lato client
  	strOnSubmit = "checkTipoComunicazione(codTipoComunicaz)";
  }
  else {
  	if (codTipoComunicazione.equals("03")) {
  		//si tratta della rettifica di una comunicazione precedente 
  		strOnSubmit = "checkTipoComunicazione(codTipoComunicaz) && ";	
  	}
  	strOnSubmit = strOnSubmit + "checkCampoObbligatorio() && checkDatInizioMovImpatti(document.Frm1.datInizioMov) && controlloLavAutonomo() && selezionaPulsanteApprendistato()";
  }
  
  if (Sottosistema.CM.isOn()) {
  		strOnSubmit = strOnSubmit + " && controllaConvenzione() ";
  }
  		
  //18/01/2006 Davide se ci sono giorni in agricoltura il livello non è quello provenienete da numLivello ma da codLivelloAgr
  if (!mov.isEnabled()) { 
  //..questo vale solo al momento del primo caricamento della pagina dopo di che il valore viene inserito in numLivello
  //quindi in caso di navigazione tra linguette (e quindi oggetto in sessione abilitato) il valore corretto è quello di numLivello
	if(!numGGPrevistiAgr.equals("")) {
		if (Integer.parseInt(numGGPrevistiAgr) > 0) {
		   	StringTokenizer sToken = new StringTokenizer(strVersioneTracciato,",");
		   	String tk = "-1";
		   	if (sToken.hasMoreTokens()) { 
		   		tk = sToken.nextToken(); 
		   	}
		   	if (Integer.parseInt(tk) >= 2 && Integer.parseInt(tk) < 3) {
	    		numLivello = codLivelloAgr;
	    	}
	        decRetribuzioneMen = "";
	    }
	}
  }
  
  //Estraggo dal DB i dati sui movimenti precedente e successivo e setto i boolean
  boolean precedente = false;
  boolean successivo = false;

  movIsEnabled = mov.isEnabled();
  //abilito l'oggetto se non era già abilitato
  if (!mov.isEnabled()) {
    mov.enable();
  }
  String queryString = null;
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
      <af:linkScript path="../../js/"/>
  <title>Dettaglio Movimento da Validare</title>
  <%@ include file="../../global/fieldChanged.inc" %>
  <%@ include file="../../presel/Function_CommonRicercaCCNL.inc" %>  
  <%@ include file="../../movimenti/DynamicRefreshCombo.inc" %> 
  <%@ include file="../../documenti/_apriGestioneDoc.inc"%> 
  <script language="Javascript">

  <% 
  	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>    
     
  var consulta = <%=consulta%>;
  var contesto = 'valida';
  var codTipoAzienda = '<%=codTipoAzienda%>';
  var codNatGiuridicaAz = '<%=codNatGiurAz%>';
  var finestraAperta;
  var precedente = <%=precedente%>;
  var oggettoInSessione = <%=movIsEnabled%>;
  var codTipoComunicaz = "<%=codTipoComunicazione%>";
  
  function stampaMovimento(prgMovimento){

	apriGestioneDoc('RPT_MOVIMENTI_DA_VALIDARE','&TIPOSTAMPA=DAVALIDARE&prgMovimento='+prgMovimento,'STMOV');
  }
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
  <script type="text/javascript" src="../../js/movimenti/common/_confirmDaControlloMov.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/avviamento/gestioneAutorizzazione.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/Indietro.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/Linguette.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/_confirmDaStOcc.js" language="JavaScript"></script>
  
  <%@ include file="../common/include/calcolaDiffGiorni.inc" %>
  <%@ include file="../common/include/_funzioniGenerali.inc" %>
</head>
<% 
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOn()) { 
	// END-PARTE-TEMP
%>	
<body class="gestione" onLoad="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');calcolaDiffGiorni(document.Frm1.datInizioMov, document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange,oggettoInSessione);gestVisualGiorniRitardo();abilitazioneConvenzione();">
<% 
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP
%>

<body class="gestione" onLoad="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');calcolaDiffGiorni(document.Frm1.datInizioMov, document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange,oggettoInSessione);gestVisualGiorniRitardo();">

<%    
	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
%>

<%@ include file="../common/include/GestioneRisultati.inc" %>
<%@ include file="../common/include/LinguetteAvviamento.inc" %>
<%@ include file="../avviamento/include/gestioneAutorizzazione.inc" %>
<center>
<% 
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOn()) { 
	// END-PARTE-TEMP
%>	
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="<%=strOnSubmit%>">
<%out.print(htmlStreamTop);%>
<%@ include file="../../movimenti/common/include/_protocollazione.inc" %>
<table class="main" cellspacing="0"  cellpadding="0" width="96%" border="0">
	<%@ include file="../../movimenti/common/include/InfoTestataMovimento.inc" %>     
	<%@ include file="../avviamento/include/campi_avviamento_cm.inc" %>
</table>

<%out.print(htmlStreamBottom);%>
<input type="hidden" name="flgCambiamentiDati" value="<%=flgCambiamentiDati%>">

<input type="hidden" name="PRGMOVIMENTOAPP" value="<%=prgMovimentoApp%>"/>
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
<input type="hidden" name="DATCOMUNICAZ" value="<%=datComunicaz%>"/>
<input type="hidden" name="NUMKLOMOV" value="<%=numKloMov%>"/>
<input type="hidden" name="CODTIPOMOV" value="<%=codTipoMov%>"/>      
<input type="hidden" name="DATAINIZIOAVV" value="<%=dataInizioAvv%>"/>
<input type="hidden" name="DATINIZIOMOVPREC" value="<%=dataInizioMovPrec%>"/> 
<input type="hidden" name="CODMONOTEMPOAVV" value="<%=codMonoTempoAvv%>"/>
<input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>"/>
<input type="hidden" name="CODNATGIURIDICAAZ" value="<%=codNatGiurAz%>"/>        
<input type="hidden" name="STRNUMALBOINTERINALI" value="<%=strNumAlboInterinali%>"/> 
<input type="hidden" name="STRNUMREGISTROCOMMITT" value="<%=strNumRegistroCommitt%>"/> 
<input type="hidden" name="STRLUOGODILAVORO" value="<%=luogoDiLavoro%>"/>
<input type="hidden" name="FLGINTERASSPROPRIA" value="<%=personaleInterno%>"/>
<input type="hidden" name="CODMONOMOVDICH" value="<%=codMonoMovDich%>"/>
<input type="hidden" name="DATFINEMOVEFFETTIVA" value="<%=datFineMovEff%>"/>      
<input type="hidden" name="CODMONOTIPOFINE" value="<%=codMonoTipoFine%>"/>
<input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>      
<input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
<input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>
<input type="hidden" name="ACTION" value="aggiorna"/>
<input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
<input type="hidden" name="ACTIONPREC" value="<%=action%>"/>
<input type="hidden" name="PROVENIENZA" value="linguetta"/>
<input type="hidden" name="PAGE" value="MovEffettuaValidazionePage"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<input type="hidden" name="LAVORATORECOLLMIRATO" value="<%=strLavCollMirato%>"/>
<%--
<input type="hidden" name="numProtocollo" value="<%=numProtocolloV%>"/>
<input type="hidden" name="numAnnoProt" value="<%=annoProtV%>"/>
--%>
<input type="hidden" name="KLOCKPROT"  value="<%=kLockProt%>">
<input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
<input type="hidden" name="FLGAUTOCERTIFICAZIONE" value="<%=flgAutocertificazione%>" />
<input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>" />
<input type="hidden" name="FORZA_INSERIMENTO" value="false"/>
<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false"/>
<input type="hidden" name="FORZA_INSERIMENTO_ETA_APPRENDISTATO" value="<%=forzaInsEtaApprendista%>"/>
<!-- GESTIONE TIROCINIO -->
<input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=prgAziendaUtil%>"/>
<input type="hidden" name="STRAZINTNUMCONTRATTO" value="<%=strAzIntNumContratto%>"/>
<input type="hidden" name="DATAZINTINIZIOCONTRATTO" value="<%=datAzIntInizioContratto%>"/>
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
<input type="hidden" name="CONFERMA_CONTROLLO_MOV_SIMILI" value="<%=confermaMovSimili%>"/>
<input type="hidden" name="CONFIRM_NO_MOBILITA" value="<%=confermaNoMobilita%>"/>
<input type="hidden" name="CONFIRM_DISOC_LUNGADURATA" value="<%=confermaDiscoLungaDurata%>"/>
<input type="hidden" name="STRCODICEFISCALE" value="<%=codFiscaleLavDB%>" />
<%-- 18/01/2006 Davide introdotto perchè utilizzato nel controllo dei campi agricoltura in caso di giorni in agricoltura --%>
<input type="hidden" name="STRVERSIONETRACCIATO" value="<%=strVersioneTracciato%>"/>
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

<!--Gestione autorizzazione per movimenti a TD-->
<input type="hidden" name="FLGAUTORIZZADURATATD" value="<%=autorizzaDurataTD%>"/>
<center>        
<%
if (canModify) {%>
  <input type="submit" class="pulsanti" name="submitbutton" value="Valida" onclick="resetFlagForzatura();"/>
<%}%>&nbsp;
  <!--	Commentato per prossimo rilascio
  <input type="button" class="pulsanti" name="stampa" value="Stampa" onclick="stampaMovimento('<%//=prgMovimentoApp%>')"/>
  -->
<%@ include file="../common/include/GestioneCollegati.inc" %>
<%@ include file="../common/include/PulsanteRitornoLista.inc" %> 
</center>
</af:form>   

<% 
	// INIT-PARTE-TEMP
	} else { // pagina senza convenzioni ed incentivi art. 13
	// END-PARTE-TEMP
%>     

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="<%=strOnSubmit%>">
<%out.print(htmlStreamTop);%>
<%@ include file="../../movimenti/common/include/_protocollazione.inc" %>
<table class="main" cellspacing="0"  cellpadding="0" width="96%" border="0">
	<%@ include file="../../movimenti/common/include/InfoTestataMovimento.inc" %>     
	<%@ include file="../avviamento/include/campi_avviamento.inc" %> 
</table>
<%out.print(htmlStreamBottom);%>
<input type="hidden" name="flgCambiamentiDati" value="<%=flgCambiamentiDati%>">

<input type="hidden" name="PRGMOVIMENTOAPP" value="<%=prgMovimentoApp%>"/>
<input type="hidden" name="DATCOMUNICAZ" value="<%=datComunicaz%>"/>
<input type="hidden" name="NUMKLOMOV" value="<%=numKloMov%>"/>
<input type="hidden" name="CODTIPOMOV" value="<%=codTipoMov%>"/>      
<input type="hidden" name="DATAINIZIOAVV" value="<%=dataInizioAvv%>"/>
<input type="hidden" name="DATINIZIOMOVPREC" value="<%=dataInizioMovPrec%>"/> 
<input type="hidden" name="CODMONOTEMPOAVV" value="<%=codMonoTempoAvv%>"/>
<input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>"/>
<input type="hidden" name="CODNATGIURIDICAAZ" value="<%=codNatGiurAz%>"/>        
<input type="hidden" name="STRNUMALBOINTERINALI" value="<%=strNumAlboInterinali%>"/> 
<input type="hidden" name="STRNUMREGISTROCOMMITT" value="<%=strNumRegistroCommitt%>"/> 
<input type="hidden" name="STRLUOGODILAVORO" value="<%=luogoDiLavoro%>"/>
<input type="hidden" name="FLGINTERASSPROPRIA" value="<%=personaleInterno%>"/>
<input type="hidden" name="CODMONOMOVDICH" value="<%=codMonoMovDich%>"/>
<input type="hidden" name="DATFINEMOVEFFETTIVA" value="<%=datFineMovEff%>"/>      
<input type="hidden" name="CODMONOTIPOFINE" value="<%=codMonoTipoFine%>"/>
<input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>      
<input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
<input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>
<input type="hidden" name="ACTION" value="aggiorna"/>
<input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
<input type="hidden" name="ACTIONPREC" value="<%=action%>"/>
<input type="hidden" name="PROVENIENZA" value="linguetta"/>
<input type="hidden" name="PAGE" value="MovEffettuaValidazionePage"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>

<%--
<input type="hidden" name="numProtocollo" value="<%=numProtocolloV%>"/>
<input type="hidden" name="numAnnoProt" value="<%=annoProtV%>"/>
--%>
<input type="hidden" name="KLOCKPROT"  value="<%=kLockProt%>">
<input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
<input type="hidden" name="FLGAUTOCERTIFICAZIONE" value="<%=flgAutocertificazione%>" />
<input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>" />
<input type="hidden" name="FORZA_INSERIMENTO" value="false"/>
<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false"/>
<input type="hidden" name="FORZA_INSERIMENTO_ETA_APPRENDISTATO" value="<%=forzaInsEtaApprendista%>"/>
<!-- GESTIONE TIROCINIO -->
<input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=prgAziendaUtil%>"/>
<input type="hidden" name="STRAZINTNUMCONTRATTO" value="<%=strAzIntNumContratto%>"/>
<input type="hidden" name="DATAZINTINIZIOCONTRATTO" value="<%=datAzIntInizioContratto%>"/>
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
<input type="hidden" name="CONFERMA_CONTROLLO_MOV_SIMILI" value="<%=confermaMovSimili%>"/>
<input type="hidden" name="CONFIRM_NO_MOBILITA" value="<%=confermaNoMobilita%>"/>
<input type="hidden" name="CONFIRM_DISOC_LUNGADURATA" value="<%=confermaDiscoLungaDurata%>"/>
<%-- 18/01/2006 Davide introdotto perchè utilizzato nel controllo dei campi agricoltura in caso di giorni in agricoltura --%>
<input type="hidden" name="STRVERSIONETRACCIATO" value="<%=strVersioneTracciato%>"/>
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

<!--Gestione autorizzazione per movimenti a TD-->
<input type="hidden" name="FLGAUTORIZZADURATATD" value="<%=autorizzaDurataTD%>"/>
<center>        
<%
if (canModify) {%>
  <input type="submit" class="pulsanti" name="submitbutton" value="Valida" onclick="resetFlagForzatura();"/>
<%}%>&nbsp;
  <!--	Commentato per prossimo rilascio
  <input type="button" class="pulsanti" name="stampa" value="Stampa" onclick="stampaMovimento('<%//=prgMovimentoApp%>')"/>
  -->
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
<%@ include file="../common/include/GestioneScriptRisultati.inc" %>
</body>
</html>  
