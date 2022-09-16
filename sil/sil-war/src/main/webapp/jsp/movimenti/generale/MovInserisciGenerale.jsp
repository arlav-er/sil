<!-- PAGINA PER L'INSERIMENTO DELLA PARTE GENERALE -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                   
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>
 
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%   
  String codInterinale = "INT";
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "MovDettaglioGeneraleInserisciPage");
  boolean canModify = attributi.containsButton("INSERISCI");
  boolean canModifyProtocol = attributi.containsButton("SalvaProtocollo");  
  boolean canModifyStato = attributi.containsButton("SALVA");
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
  
  //guardo se ho effettuato un salvataggio o sto navigando tra le linguette
  String action = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTION");
  boolean aggiorna = action.equalsIgnoreCase("aggiorna");
  boolean naviga = action.equalsIgnoreCase("naviga");
 
  //guardo se precedentemente ero in consultazione
  String actionprec = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTIONPREC");
  boolean actionprecaggiorna = actionprec.equalsIgnoreCase("aggiorna");
  boolean actionprecnaviga = actionprec.equalsIgnoreCase("naviga");
  boolean actionprecconsulta = actionprec.equalsIgnoreCase("consulta");
  
  //Guardo da dove provengo
  String provenienza = StringUtils.getAttributeStrNotNull(serviceRequest, "PROVENIENZA");   
  boolean daLista = provenienza.equalsIgnoreCase("ListaMov");
  boolean daLavoratore = provenienza.equalsIgnoreCase("lavoratore");
  boolean daAzienda = provenienza.equalsIgnoreCase("azienda"); 
  boolean daLinguetta =  provenienza.equalsIgnoreCase("linguetta"); 
 
  //Il parametro seguente indica che si sta eseguendo un inserimento collegato
  boolean insertCollegato = false;
  String collegato = StringUtils.getAttributeStrNotNull(serviceRequest, "COLLEGATO");
  if (collegato.equalsIgnoreCase("precedente")) {
    insertCollegato = true;
  }
 
  //Spiano l'oggetto del movimento in sessione se non arrivo da una linguetta
  if (!daLinguetta) {
    sessionContainer.delAttribute("MOVIMENTOCORRENTE");
  }
  %>
  <%@ include file="../GestioneOggettoMovimento.inc" %> 
  <%
  
  //inizializzazione variabili
  String prgMovimento = "";  
  String prgMovimentoApp = ""; 
  String prgMovimentoPrec = "";
  String prgMovimentoRett = "";
  String prgMovimentoSucc = "";  
  String dataInizioAvv = "";
  String dataInizioMovPrec = "";  
  String codTipoAss = "";
  String codQualificaSrq = "";
  String dataFineMovPrec = "";
  String codMonoTempoAvv = "";
  String prgAzienda = "";
  String prgUnitaValMov = "";
  String prgUnita = "";
  String cdnLavoratore = "";
  String strPartitaIvaAz = "";
  String strCodiceFiscaleAz = "";
  String strRagioneSocialeAz = "";
  String strIndirizzoUAz = "";
  String strComuneUAz = "";
  String codComuneUAz = "";
  String strCapUAz = "";
  String strTelUAz = "";
  String strFaxUAz = "";
  String codAtecoUAz = "";
  String strDesAtecoUAz = "";
  String codCCNLAz = "";
  String descrCCNLAz = "";
  String natGiuridicaAz = "";
  String codNatGiuridicaAz = "";  
  String strPartitaIvaAzUtil = "";
  String strCodiceFiscaleAzUtil = "";
  String strRagioneSocialeAzUtil = "";
  String strIndirizzoUAzUtil = "";
  String strComuneUAzUtil = "";
  String descrCCNLAzUtil = "";
  String strCodiceFiscaleLav = "";
  String strNomeLav = "";
  String strCognomeLav = "";
  String datNascLav = "";
  String codTipoMov = "";//Di supporto per omogeneità con le altre pagine
  String codtipomovprec = "";  
  String datComunicaz = "";
  String numKloMov = "";
  String numKloMovPrec = "";  
  String posInps = "";
  String posInps1 = "";
  String posInps2 = "";
  String patInail = "";
  String patInail1 = "";
  String patInail2 = "";  
  String codTipoAzienda = "";
  String codTipoAziendaUtil = "";
  String descrTipoAzienda = "";
  String descrTipoAziendaUtil = "";
  String strNumAlboInterinali = "";
  String strNumRegistroCommitt = "";
  String flgInterAssPropria = "";
  String strLuogoDiLavoro = "";
  String prgAziendaUtil = "";
  String prgAziendaValMov = "";
  String prgUnitaUtil = "";
  String seguente = "";
  String datFineMovEff = "";
  String codMonoTipoFine = "";
  String codMonoMovDich = "";
  String strFlgCfOk = "";
  String strFlgDatiOk = "";
  String codStatoAtto = "";
  String codMotAnnullamento = "";
  String strReferente = "";
  String codCpi = ""; 
  String codCpiLav = "";
  String codMonoTempo = "";
  String datFineMov = ""; 
  //Di supporto per il ritardo
  String numGgTraMovComunicaz = "";
  
  
  //Per omogeneità per le info della testata del mov.
  String datInizioMov = "";
  
  
	//Giovanni D'Auria 21/02/2005 inizio
	String codOrario="";
	String numOreSett="";
	//fine

  
  //Variabili per la gestione della protocollazione ================
  String prAutomatica     = null; 
  String estReportDefautl = null;
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String     datProtV     = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
  String     oraProtV     = (new SimpleDateFormat("HH:mm")).format(new Date());
  String     docInOut     = "I";
  String     docRif       = "Movimenti amministrativi";
  BigDecimal kLockProt    = null;
  boolean numProtEditable = false;
  Vector rows             = null;
  SourceBean row          = null;
  Vector vectRicercaAz = null;
  Vector vectRicercaUnitaAz = null;
  Vector vectRicercaLav = null;
  //================================================================
 
  String ragSocDB = "";
  String pIvaDB = "";
  String codNatGiuridicaDB = "";
  String codTipoAzDB = "";
  String codCnnlDB = "";
  String strPatInailDB = "";
  String strNumAlboInterDB = "";
  String strPosInpsDB = "";
  String codAtecoDB = "";
  String strTelDB = "";
  String strFaxDB = "";
  String strIndirizzoDB = "";
  String codComuneDB = "";
  String capDB = "";
  String cdnLavoratoreDB = "";
  String strNomeLavoratore = "";
  String nomeLavDB = "";
  String cognomeLavDB = "";
  String datNascDB = "";
  String codFiscaleLavDB = "";
  SourceBean goInserisciLav = null;
  String strPageSalto = "";
  String cdnFunzione = "";
  String numContratto = "";
  String dataInizio = "";
  String dataFine = "";
  String legaleRapp = "";
  String numSoggetti = "";
  String classeDip = "";
  String decMonoProv = "Movimento inserito <strong>Manualmente</strong>";
  String valDisplay="none";
  String codTipoTitoloStudio = "";

  String codCategoria   = "";
  String codLavorazione = "";

  String strEnteRilascio  = "";
  strEnteRilascio = StringUtils.getAttributeStrNotNull(serviceRequest, "STRENTERILASCIO");
  String strDisabile = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATOREDISABILE");
  String strLavCollMirato = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATORECOLLMIRATO");
   
  //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
  String codtipodocex = "";
  String strnumdocex = "";
  String codmotivopermsoggex = "";
  String codtipoenteprev = "";
  String strcodiceenteprev = "";
  String flgsocio = "";
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
  String flgTrasferimento = "";
 
  //Numero protocollo da applicare se in inserimento nuovo
  rows = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
  if(rows != null && !rows.isEmpty())
  { row = (SourceBean) rows.elementAt(0);
    prAutomatica     = (String) row.getAttribute("FLGPROTOCOLLOAUT");
    if ( prAutomatica.equalsIgnoreCase("N") ){ numProtEditable = true; }
    estReportDefautl = (String) row.getAttribute("CODTIPOFILEESTREPORT");
    numProtV         = SourceBeanUtils.getAttrBigDecimal(row, "NUMPROTOCOLLO", null);
    numAnnoProtV     = (BigDecimal) row.getAttribute("NUMANNOPROT");
    kLockProt        = (BigDecimal) row.getAttribute("NUMKLOPROTOCOLLO");
  }
  
  
  //Setto l'origine dei dati generali da recuperare in caso di inserimento o validazione, 
  //mi arrivano dall'oggetto in sessione se sto inserendo e l'oggetto è abilitato,
  //altrimenti dalla request
  SourceBean dataOrigin = serviceRequest;
    //16/01/2001 Davide: aggiungiunti campi inerenti l'agricoltura
    codCategoria   = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCATEGORIA");
    codLavorazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODLAVORAZIONE");

  if (mov.isEnabled()) {
    dataOrigin = mov.getFieldsAsSourceBean();
  }
  
  //Seleziono l'origine dei dati da visualizzare nella pagina
  //L'origine dei dati è l'oggetto in sessione, tranne nel caso in cui il contesto è 
  //di validazione, di salvataggio,
  //o di inserimento di un movimento collegato con provenienza dalla lista.
  //Spiegazione: se eseguo un inserimento collegato provenendo dalla lista 
  //"M_MovGetDettMov.ROWS.ROW" contiene i dati del movimento precedente,
  //in tutti gli altri casi contiene i dati del movimento corrente  
  if (insertCollegato && daLista) { dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMov.ROWS.ROW");}
 
  //Estraggo i dati della pagina dall'origine tranne nel caso di inserimento senza 
  //precedente con provenienza da lista.
  if (!(inserisci && !insertCollegato && daLista) && (dataOrigin != null)) {
    codStatoAtto = StringUtils.getAttributeStrNotNull(dataOrigin, "CODSTATOATTO");
    //Progressivi se ci sono
    prgMovimento = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTO");
    numKloMov = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMKLOMOV");
    prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
    numKloMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMKLOMOVPREC");  
    prgMovimentoSucc = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOSUCC");
    prgMovimentoApp = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOAPP");
    prgAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDA");
    prgUnita = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITA");
    cdnLavoratore = StringUtils.getAttributeStrNotNull(dataOrigin, "CDNLAVORATORE");
    if (cdnLavoratore.equals("null")) cdnLavoratore="";
    prgAziendaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDAUTILIZ");
    prgUnitaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITAUTILIZ");
    numContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTNUMCONTRATTO");
    dataInizio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTINIZIOCONTRATTO");
    dataFine = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTFINECONTRATTO");
    legaleRapp = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTRAP");
    numSoggetti = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMAZINTSOGGETTI");
    classeDip = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMAZINTDIPENDENTI");
    //Dati sede azienda
    strPartitaIvaAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strPartitaIvaAz");
    strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strCodiceFiscaleAz");
    strRagioneSocialeAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAz");
    strFlgCfOk = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGCFOK");
    strFlgDatiOk = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGDATIOK");
    codCCNLAz = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNLAz");  
    descrCCNLAz = StringUtils.getAttributeStrNotNull(dataOrigin, "descrCCNLAz"); 
    strIndirizzoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAz");
    strComuneUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAz");
    codComuneUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "codComuneUAz");    
    strCapUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strCapUAz");   
    strTelUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strTelUAz");  
    strFaxUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strFaxUAz"); 
    codAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "codAtecoUAz");  
    strDesAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strDesAtecoUAz"); 
    codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOAZIENDA");
    descrTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "DESCRTIPOAZIENDA");
    natGiuridicaAz = StringUtils.getAttributeStrNotNull(dataOrigin, "natGiuridicaAz");  
    codNatGiuridicaAz = StringUtils.getAttributeStrNotNull(dataOrigin, "codNatGiuridicaAz");     
    strNumAlboInterinali = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMALBOINTERINALI");
    strNumRegistroCommitt = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMREGISTROCOMMITT");
    //Dati azienda utilizzatrice se ci sono
    strPartitaIvaAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strPartitaIvaAzUtil");
    strCodiceFiscaleAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strCodiceFiscaleAzUtil");
    strRagioneSocialeAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAzUtil");
    strIndirizzoUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAzUtil");
    strComuneUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAzUtil");
    descrTipoAziendaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "DESCRTIPOAZIENDAUTIL");
    descrCCNLAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "descrCCNLAzUtil");
    //Dati lavoratore
    strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull(dataOrigin, "strCodiceFiscaleLav"); 
    strNomeLav = StringUtils.getAttributeStrNotNull(dataOrigin, "strNomeLav");
    strNomeLavoratore = strNomeLav;
    strCognomeLav = StringUtils.getAttributeStrNotNull(dataOrigin, "strCognomeLav");
    datNascLav = StringUtils.getAttributeStrNotNull(dataOrigin, "datNascLav");
    //altri dati della pagina
    posInps = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPOSINPS");
    patInail = StringUtils.getAttributeStrNotNull(dataOrigin, "strPatInail");
    flgInterAssPropria = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGINTERASSPROPRIA");
    codTipoMov = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOV");
    datComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin, "datComunicaz");
    datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMov");  
    codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo");
    datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov");    
    strLuogoDiLavoro = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLUOGODILAVORO");
    dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");
    dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");    
    datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
    codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE");
    codMonoMovDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOMOVDICH");
    //Devo riportare il codTipoAss in cima alla pagina e passarlo agli altri dettagli e all'inserimento
    codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss"); 
    codMonoTempoAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTEMPOAVV");
    codtipomovprec = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOVPREC");
    strReferente = StringUtils.getAttributeStrNotNull(dataOrigin, "STRREFERENTE"); 
    codTipoTitoloStudio = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOTITOLOlav");
    //Giovanni D'Auria 21/02/2005 inizio
	codOrario = StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");
	numOreSett = StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");
	//fine
	codQualificaSrq = StringUtils.getAttributeStrNotNull(dataOrigin, "CODQUALIFICASRQ");
	codtipocontratto=StringUtils.getAttributeStrNotNull(dataOrigin, "codtipocontratto");
  } 
 
  //Se arrivo dalla lista non devo prevalorizzare la data di comunicazione
  if (daLista) {
  	datComunicaz = "";
  }
  
    //Genero informazioni sull'azienda
    if (!prgAzienda.equals("") && !prgUnita.equals("") && !prgAzienda.equals("null") && !prgUnita.equals("null")) {
      InfoAzienda datiAz = new InfoAzienda(prgAzienda, prgUnita);
      strPartitaIvaAz = datiAz.getPIva();
      strCodiceFiscaleAz = datiAz.getCodiceFiscale();
      strRagioneSocialeAz = datiAz.getRagioneSociale();
      strIndirizzoUAz = datiAz.getIndirizzo();
      strComuneUAz = datiAz.getComune();
      codTipoAzienda = datiAz.getTipoAz();
      strCapUAz = datiAz.getCapAz();
      strFaxUAz = datiAz.getFaxAz();
      strTelUAz = datiAz.getTelAz();
      descrTipoAzienda = datiAz.getDescrTipoAz();
      strNumAlboInterinali = datiAz.getNumAlboInter();
      strNumRegistroCommitt = datiAz.getNumRegComm();
      codCCNLAz = datiAz.getCCNL();
      descrCCNLAz = datiAz.getDescrCCNL();
      codAtecoUAz = datiAz.getCodAtecoAz();
      strDesAtecoUAz = datiAz.getDescrAtecoAz();  
      natGiuridicaAz = datiAz.getDescrNatGiurAz(); 
      codNatGiuridicaAz = datiAz.getCodNatGiurAz(); 
      strFlgDatiOk = datiAz.getFlgDatiOk();
      codCpi = datiAz.getCodCpi();
      valDisplay="inline";
    }
 
    //Genero informazioni sull'azienda utilizzatrice    
    if (!prgAziendaUtil.equals("") && !prgUnitaUtil.equals("")) {
      //Oggetto per la generazione delle informazioni sull'azienda utilizzatrice
      InfoAzienda datiAzUtil = new InfoAzienda(prgAziendaUtil, prgUnitaUtil);
      strPartitaIvaAzUtil = datiAzUtil.getPIva();
      strCodiceFiscaleAzUtil = datiAzUtil.getCodiceFiscale();
      strRagioneSocialeAzUtil = datiAzUtil.getRagioneSociale();
      strIndirizzoUAzUtil = datiAzUtil.getIndirizzo();
      strComuneUAzUtil = datiAzUtil.getComune();
      codTipoAziendaUtil = datiAzUtil.getTipoAz();
      descrTipoAziendaUtil = datiAzUtil.getDescrTipoAz();  
      descrCCNLAzUtil = datiAzUtil.getDescrCCNL();
    }
 
    //Genero informazioni sul lavoratore    
    if (!cdnLavoratore.equals("") && !cdnLavoratore.equals("null")) {
      //Oggetto per la generazione delle informazioni sul lavoratore
      InfoLavoratore datiLav = new InfoLavoratore(new BigDecimal(cdnLavoratore));
      strCodiceFiscaleLav = datiLav.getCodFisc();
      strNomeLav = datiLav.getNome();
      strCognomeLav = datiLav.getCognome();
      datNascLav = datiLav.getDataNasc();
      strFlgCfOk = datiLav.getFlgCfOk();
      codCpiLav = datiLav.getCodCpiLav();
    }
 
  //se sto eseguendo un inserimento collegato devo fare qualche elaborazione
  if (insertCollegato && (dataOrigin != null)) {
    
    //Se non arrivo da una linguetta i dati che ho estratto sono quelli del movimento precedente,
    //devo aggiustare alcuni campi
    if (!daLinguetta) {
      //Il codTipoMov estratto è quello del precedente
      codtipomovprec = codTipoMov;
      codTipoMov = "";
      //anche il codMonoTempo è del precedente
      codMonoTempoAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo");
      //anche il progressivo e il klomov sono del precedente
      prgMovimentoPrec = prgMovimento;
      numKloMovPrec = numKloMov;
      datInizioMov = ""; 
    }
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
 
  //Unifico nome e cognome lavoratore
  String strNomeCognomeLav = strCognomeLav + " " + strNomeLav;
 
  //Gestione aziende interinali
  boolean assInterna = false;
  boolean interinale = codTipoAzienda.equalsIgnoreCase(codInterinale);
  String flgAssPropria = "";
  if (interinale) {
    if (flgInterAssPropria.equalsIgnoreCase("S")) {
      assInterna = true;
      flgAssPropria = "S";
    } else {
      flgAssPropria = "N";      
    }
  }
 
  //Gestione spezzatino posizione Inps
  posInps1 = posInps.substring(0, (posInps.length() >= 2 ? 2 : posInps.length()));
  posInps2 = posInps.substring((posInps.length() >= 2 ? 2 : posInps.length()), (posInps.length() >= 15 ? 15 : posInps.length()));
 
  //Gestione spezzatino Pat Inail
  patInail1 = patInail.substring(0, (patInail.length() >= 8 ? 8 : patInail.length()));
  patInail2 = patInail.substring((patInail.length() >= 8 ? 8 : patInail.length()), (patInail.length() >= 10 ? 10 : patInail.length()));

  if (strFlgDatiOk!=null){
    if (strFlgDatiOk.equalsIgnoreCase("S")){
      strFlgDatiOk = "Si";
    }else
        if (strFlgDatiOk.equalsIgnoreCase("N")){
          strFlgDatiOk = "No";
        }
  }
 
  if (strFlgCfOk!=null){
      if (strFlgCfOk.equalsIgnoreCase("S")){
        strFlgCfOk = "Si";
      }else
          if (strFlgCfOk.equalsIgnoreCase("N")){
            strFlgCfOk = "No";
          }
  }    
  //Substring delle info troppo lunghe
  String strRagioneSocialeAzTrunc = strRagioneSocialeAz;
  if (strRagioneSocialeAz.length() >= 30){
    strRagioneSocialeAzTrunc = strRagioneSocialeAz.substring(0,26) + "...";
  }
  if (strDesAtecoUAz.length() >= 22){
    strDesAtecoUAz = strDesAtecoUAz.substring(0,19) + "...";
  }
  if (natGiuridicaAz.length() >= 30){
    natGiuridicaAz = natGiuridicaAz.substring(0,26) + "...";
  }
  
  //Variabili per la gestione delle linguette con file di include
  String codTipoMovCorr = codTipoMov;
  String codTipoMovPrec = codtipomovprec;
  boolean consulta = false;


%>
 
<html>
  <head>
    <%@ include file="../../global/fieldChanged.inc" %>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    <title>Dettaglio Movimento</title>
    <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
    var aziendaUtilVar = "N";
    <% if ( (!prgAziendaUtil.equals("")) && (!prgUnitaUtil.equals("")) ){%>
      aziendaUtilVar = "S";
    <%}%>
 

    var _funzione = '<%=_funzione%>';
    var codInterinale = '<%=codInterinale%>';
    var codTipoAzienda = '<%=codTipoAzienda%>';
    var codNatGiuridicaAz = '<%=codNatGiuridicaAz%>';
    var prgAziendaValMov = '<%=prgAziendaValMov%>';
    var canModify = '<%=canModify%>';
    var strPageSalto = '<%=strPageSalto%>';
    var codTipoMovCorr = '<%=codTipoMovCorr%>';
    var codTipoMovPrec = '<%=codTipoMovPrec%>';
    var cdnFunzione = '<%=cdnFunzione%>';
    var inserisci = '<%=inserisci%>';
    var contesto = 'inserisci';
    var precedente = <%=(prgMovimentoPrec.equals("") ? "false" : "true")%>;
    var successivo = <%=(prgMovimentoSucc.equals("") ? "false" : "true")%>;
    var prgAziendaS = '<%=prgAzienda%>';
    var prgUnitaS = '<%=prgUnita%>';
    


    function proseguiF(contesto)
    {
        // Aggiungo la parte di controllo della data e ora di protocollazione.
        // Questi campi si trovano nel file _protocollazione.inc
        // I campi non sono stati resi obbligatori perchè alcuni documenti provenienti dal porting
        // non presentano la data di protocollazione. Il controllo quindi va fatto SOLO in fase di inserimento e
        // non anche in fase di aggiornamento, cosa che avverrebbe rendendoli obbligatori (mettendo l'attributo required="true")
        //   By Davide

        if (document.Frm1.dataProt.value == "")
        { alert("La data di protocollazione è obbligatoria");
          return;
        }
        if (document.Frm1.oraProt.value == "")
        { alert("L'ora di protocollazione è obbligatoria");
          return;
        }
        else if ( !checkAndFormatTime(document.Frm1.oraProt) )
        { return;
        }
        //-------------------------------------------------------------------------------------------------------

        successivoF(contesto)

    }
    
    </SCRIPT>    
    <script type="text/javascript" src="../../js/movimenti/generale/DatiLavoratore.js" language="JavaScript"></script>
    <%-- gestione controllo dati lavoratore (se eta <15, se in mobilita o in collocamento mirato) --%>
    <%@ include file="include/_functionControlloDatiLavoratore.inc" %>
    
    <%@ include file="include/_functionGestPrec.inc" %>
    <!-- Gestione Profili -->
    <%@ include file="../common/include/_gestioneProfili.inc" %> 
    
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiRicercaSoggetto.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/apriAziendaUtil.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/apriAziendaMov.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/_lavoratore.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/Linguette.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/func_generale.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/Avanti.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>

  </head>
 
  <body class="gestione" onload="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');rinfresca();visualizzaInterinali('<%=codTipoAzienda%>', <%=assInterna%>);gestioneProfilo(document.Frm1.CODTIPOMOV);controllaInfoLavoratore();">
	
	<%@ include file="../common/include/LinguetteGenerale.inc" %>

  <%-- 
      Gestione profilatura: posizionato qui per motivi di compatibilità con il resto 
      delle variabili della pagina.
  --%>
  <%@ include file="../common/include/_commonFuncProfili.inc" %>
  
      <af:form name="Frm1" method="POST" action="AdapterHTTP">
      <center>
        <%out.print(htmlStreamTop);%>
        <!-- Parte della protocollazione -->
        <%@ include file="include/_protocollazione.inc" %>
        <table class="main" border="0" width="96%" cellpadding="0" cellspacing="0">
        <%@ include file="../../movimenti/common/include/InfoTestataMovimento.inc" %>        
          <tr>
            <td>          
              <div class='sezione2' id='SedeAzienda'>
                <img id='tendinaAzienda' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("datiAzienda"));'/>
                Sede Azienda&nbsp;&nbsp; 
                <% if (!insertCollegato && !daLinguetta && !daAzienda) {%> 
                 <a id='ricercaAzienda' href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda', '', '', '');"><img src="../../img/binocolo.gif" alt="Cerca azienda nel DB"></a>
        		 &nbsp;<a href="#" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Scollega azienda"></a>
                 <div id="DettaglioSedeAzienda" style="position:absolute; display:<%=valDisplay%>">
                 &nbsp;
                <%}%>
                  <a href="#" onClick="javascript:apriUnitaAziendale(document.Frm1.PRGAZIENDA.value,document.Frm1.PRGUNITA.value,<%=_funzione%>,'0');"><img src="../../img/detail.gif" alt="Dettaglio azienda"></a>
                </div>
              </div>
            </td>
          </tr>       
          <!-- sezione riservata all'azienda che effettua il movimento e eventuale aziennda util. -->
          <%@ include file="include/azienda.inc" %>
          <tr>
            <td>         
              <div class='sezione2' id='lavoratore'>
                <img id='tendinaLavoratore' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("datiLavoratore"));'/>
                Lavoratore&nbsp;&nbsp;
                <%
                  if (!insertCollegato && !daLavoratore && !daLinguetta) {%>                
                    <a id='ricercaLavoratore' href="#" onClick="javascript:apriSelezionaSoggetto('Lavoratori', 'aggiornaLavoratore', '', '', '');"><img src="../../img/binocolo.gif" alt="Cerca lavoratore nel DB"></a>
                <%}%>
                <%boolean modificaTitolo = true;%>
                <%@ include file="include/_titoloDiStudio.inc" %>
                <%@ include file="include/_sintesiLavoratore.inc" %>  
                <%@ include file="include/_movimentiLavoratore.inc" %>  
             </div>
            </td>
          </tr>
          <!-- sezione riservata al lavoratore e tipo movimento -->   
          <%@ include file="include/_lavoratore.inc" %>
          <%@ include file="include/_datiGenerali.inc" %>
        </table>
        <%out.print(htmlStreamBottom);%>
    <input type="hidden" name="RESETCFL" value="false"/>	
    
    <input type="hidden" name="CODTIPOASS" value="<%=codTipoAss%>"/>
    <input type="hidden" name="CODQUALIFICASRQ" value="<%=codQualificaSrq%>"/>
    <input type="hidden" name="NUMKLOMOV" value="<%=numKloMov%>"/>
    <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
    <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
    <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
    <input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=prgAziendaUtil%>"/>
    <input type="hidden" name="PRGUNITAUTILIZ" value="<%=prgUnitaUtil%>"/>
    <input type="hidden" name="STRAZINTNUMCONTRATTO" value="<%=numContratto%>">
    <input type="hidden" name="DATAZINTINIZIOCONTRATTO" value="<%=dataInizio%>">
    <input type="hidden" name="DATAZINTFINECONTRATTO" value="<%=dataFine%>">
    <input type="hidden" name="STRAZINTRAP" value="<%=legaleRapp%>">
    <input type="hidden" name="NUMAZINTSOGGETTI" value="<%=numSoggetti%>">
    <input type="hidden" name="NUMAZINTDIPENDENTI" value="<%=classeDip%>">
    <input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>"/>
    <input type="hidden" name="CODTIPOAZIENDAUTIL" value="<%=codTipoAziendaUtil%>"/>
    <input type="hidden" name="DATAINIZIOAVV" value="<%=dataInizioAvv%>"/>
    <input type="hidden" name="DATINIZIOMOVPREC" value="<%=dataInizioMovPrec%>"/>     
    <input type="hidden" name="CODMONOTEMPOAVV" value="<%=codMonoTempoAvv%>"/>
    <input type="hidden" name="DATFINEMOVPREC" value="<%=dataFineMovPrec%>"/>
    <input type="hidden" name="DATFINEMOVEFFETTIVA" value="<%=datFineMovEff%>"/>      
    <input type="hidden" name="CODMONOTIPOFINE" value="<%=codMonoTipoFine%>"/>
    <input type="hidden" name="CODMONOTEMPO" value="<%=codMonoTempo%>"/>   	
 
    <input type="hidden" name="CODCPILAV" value="<%=codCpiLav%>"/>	
    <input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>      
    <input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
    <input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>
        
    <input type="hidden" name="ACTION" value="aggiorna"/>
    <input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
    <input type="hidden" name="COLLEGATO" value="<%=collegato%>"/>
    <input type="hidden" name="ACTIONPREC" value="<%=action%>"/>
    <input type="hidden" name="PAGE" value="MovDettaglioGeneraleInserisciPage"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
    <input type="hidden" name="CODCPI" value="<%=codCpi%>"/>      
    <input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
    <input type="hidden" name="CODMONOPROV" value="M"/>
    <input type="hidden" name="NUOVODOCUMENTO" value=""/>
    <input type="hidden" name="strRagioneSocialeAzUtil" value="<%=strRagioneSocialeAzUtil%>"/>
    <input type="hidden" name="strIndirizzoUAzUtil" value="<%=strIndirizzoUAzUtil%>"/>
    <input type="hidden" name="strComuneUAzUtil" value="<%=strComuneUAzUtil%>"/>

  	<input type="hidden" name="CODCATEGORIA" value="<%=codCategoria%>"/>
  	<input type="hidden" name="CODLAVORAZIONE" value="<%=codLavorazione%>"/>
  	<!--Gestione comunicazioni in ritardo per lavoratori disabili-->
	<input type="hidden" name="LAVORATOREDISABILE" value="<%=strDisabile%>"/>
	
    <!-- Sezione di gestione impatti tenendo conto della profilatura -->
    <input type="hidden" name="permettiImpatti" value="" />
    <input type="hidden" name="CANVIEW" value="" />
    <input type="hidden" name="CANEDITLAV" value="" />
    <input type="hidden" name="CANEDITAZ" value="" />
    <input type="hidden" name="LAVORATORECOLLMIRATO" value="<%=strLavCollMirato%>"/>
  <!-- DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO -->
  <input type="hidden" name="CODTIPODOCEX"        value="<%=codtipodocex%>" />
  <input type="hidden" name="STRNUMDOCEX"         value="<%=strnumdocex%>" />
  <input type="hidden" name="CODMOTIVOPERMSOGGEX" value="<%=codmotivopermsoggex%>" />
  <input type="hidden" name="CODENTE"     value="<%=codtipoenteprev%>" />
  <input type="hidden" name="STRCODICEENTEPREV"   value="<%=strcodiceenteprev%>" />
  <input type="hidden" name="FLGSOCIO"            value="<%=flgsocio%>" />
  <input type="hidden" name="CODTIPOTRASF"        value="<%=codtipotrasf%>" />
  <input type="hidden" name="CODTIPOCONTRATTO"    value="<%=codtipocontratto%>" />
  <input type="hidden" name="DATFINEDISTACCO"    value="" />
  <input type="hidden" name="STRNUMAGSOMMINISTRAZIONE"        value="<%=strnumagsomm%>" />
  <input type="hidden" name="DECINDENSOM"     value="<%=strindennitasom%>" />
  <input type="hidden" name="STRRISCHIOASBSIL"    value="<%=strrischioasbsil%>" />
  <input type="hidden" name="DECVOCETAR1"     value="<%=strvocetariffa1%>" />
  <input type="hidden" name="DECVOCETAR2"     value="<%=strvocetariffa2%>" />
  <input type="hidden" name="DECVOCETAR3"     value="<%=strvocetariffa3%>" />
  <input type="hidden" name="CODSOGGETTO"         value="<%=codsoggetto%>" />
  <input type="hidden" name="FLGTRASFER" value="<%=flgTrasferimento%>"/>
    
	<center>
    <%@ include file="../common/include/GestioneCollegati.inc" %>        
  	<%@ include file="../common/include/PulsanteRitornoLista.inc" %>
    </center>
    </center> 
    </af:form>     
  <script language="javascript">
    if (aziendaUtilVar != "N"){
      document.Frm1.STRLUOGODILAVORO.readOnly = true;
    }
  </script>
 </body>
</html>