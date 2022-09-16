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
  String prgMovimento = null;
  
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "MovValidaDettaglioGeneralePage");
  boolean canModify = attributi.containsButton("SALVA");
  boolean canModifyProtocol = false;
  boolean canModifyStato = false;
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

  
  boolean inserisci = false;
  boolean salva = false;
  boolean valida = true;
  boolean consulta = false;
  boolean rettifica = false;
      
  //Guardo da dove provengo
  String provenienza = StringUtils.getAttributeStrNotNull(serviceRequest, "PROVENIENZA");
  boolean daValidazione = provenienza.equalsIgnoreCase("validazione");
  boolean daLinguetta =  provenienza.equalsIgnoreCase("linguetta"); 
  String strDisabile = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATOREDISABILE");
  //Controllo se sto eseguendo il refresh della pagina
  boolean isRefresh = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTION").equalsIgnoreCase("refresh");
  // Stefy 07/04/2005
  //boolean isSceltaMovPrec = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTION").equalsIgnoreCase("SCELTAMOVPREC");
  //if(isSceltaMovPrec) { isRefresh = true; }
  
  //Cancello l'oggetto del movimento in sessione se non arrivo da una linguetta
  if (!daLinguetta && !isRefresh) {
    sessionContainer.delAttribute("MOVIMENTOCORRENTE");
  }
  %>
  <%@ include file="../GestioneOggettoMovimento.inc" %> 
  <%
  
  //inizializzazione variabili
  String prgMovimentoApp = ""; 
  String prgMovimentoPrec = "";
  String prgMovimentoSucc = "";  
  String prgMovimentoRett = "";
  String dataInizioAvv = "";
  String dataInizioMovPrec = "";  
  String codTipoAss = "";
  String codMonoTempoAvv = "";
  String codMonoTempo = "";  
  String prgAzienda = "";
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
  String strEmailUAz = "";
  String codAtecoUAz = "";
  String strDesAtecoUAz = "";
  String codCCNLAz = "";
  String descrCCNLAz = "";
  String natGiuridicaAz = "";
  String codNatGiuridicaAz = "";  
  String strCodiceFiscaleLav = "";
  String strNomeLav = "";
  String strCognomeLav = "";
  String datNascLav = "";
  String codtipomov = "";
  String codTipoMov = "";//Di supporto per omogeneità con le altre pagine
  String codtipomovprec = "";  
  String datComunicaz = "";
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
  String prgUnitaUtil = ""; 
  String datFineMovEff = "";
  String codMonoTipoFine = "";
  String codMonoMovDich = "";
  String strFlgCfOk = "";
  String strFlgDatiOk = "";
  String codStatoAtto = "";
  String codMonoProv = "";
  String strReferente = "";
  String numGgTraMovComunicaz = "";
  String datFineMov = "";
  String codTipoTitoloStudio = "";
  String codMotAnnullamento = "";  
  
  String codCpiLav = "";
  String modificataAz = "";
  
  String strRagioneSocialeAzUtil ="";
  String strIndirizzoUAzUtil = "";
  String strComuneUAzUtil = "";
  String descrComUAzUtil = "";
  String codCpi = "";
  String  numContratto = "";
  String  dataInizio = "";
  String  dataFine = "";
  String  legaleRapp = "";
  String  numSoggetti = "";
  String  classeDip = "";
  String codQualificaSrq = "";
  String codTipoComunicazione = "";
  //Per omogeneità per le info della testata del mov.
  String datInizioMov = "";
  String codFiscAzPrec = "";
  String codComAzPrec = "";
  String indirizAzPrec = "";
  String flgTrasferimento = "";
  //Giovanni D'Auria 21/02/2005 inizio 
  String codOrario="";
  String numOreSett="";
  //fine
  
  //Variabili per la gestione della protocollazione ================

  //Reperisco la data e ora corrente
  //String dataOraProt = (new SimpleDateFormat("dd/MM/yyyy HH:mm")).format(new Date());
  
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

  Vector vectRicercaLav = null;

  // FINE Variabili per la gestione della protocollazione ================
 
  String cdnLavoratoreDB = "";
  String strNomeLavoratore = "";
  String nomeLavDB = "";
  String cognomeLavDB = "";
  String datNascDB = "";
  String codFiscaleLavDB = "";
  SourceBean goInserisciLav = null;
  String strPageSalto = "";
  String cdnFunzione = "";
  //18/01/2006 Davide: introdotte per gestire il caso di giorni in agricoltura  
  String codCategoria   = "";
  String codLavorazione = "";
  String strVersioneTracciato = "";

  //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
  String codtipodocex = "";
  String strnumdocex = "";
  String codmotivopermsoggex = "";
  String codtipoenteprev = "";
  String strcodiceenteprev = "";
  String flgsocio = "";
  String codtipotrasf = "";
  String dataFineDistacco = "";
  String codtipocontratto = "";
  String strnumagsomm = "";
  String strindennitasom = "";
  String strrischioasbsil = "";
  String strvocetariffa1 = "";
  String strvocetariffa2 = "";
  String strvocetariffa3 = "";
  String codsoggetto = "";
  String datFineMovPrec = "";

  // Stefy e Davide 11/04/2005
  boolean unitaDaSessione = false;
  boolean mostraRicercaAZ = false;
  boolean aggiornaAzienda = false;
  
  String strLavCollMirato = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATORECOLLMIRATO");
  
  vectRicercaLav = serviceResponse.getAttributeAsVector("M_MovRicercaLavoratoreCF.ROWS.ROW");
  if (vectRicercaLav.size() > 0) {
    SourceBean rigaLavValMov = (SourceBean) vectRicercaLav.elementAt(0);
    cdnLavoratoreDB = rigaLavValMov.getAttribute("CDNLAVORATORE").toString();
    codFiscaleLavDB = StringUtils.getAttributeStrNotNull(rigaLavValMov, "STRCODICEFISCALE");
    nomeLavDB = StringUtils.getAttributeStrNotNull(rigaLavValMov, "STRNOME");
    cognomeLavDB = StringUtils.getAttributeStrNotNull(rigaLavValMov, "STRCOGNOME");
    datNascDB = StringUtils.getAttributeStrNotNull(rigaLavValMov, "DATNASC");
  }
  else {
  	//La seguente parte è stata tolta perchè non ha alcun senso... 
    //goInserisciLav = (SourceBean) serviceResponse.getAttribute("M_MovimentiGoInserisciLavoratore.ROW");
    //if (goInserisciLav != null) {
    //  strPageSalto = goInserisciLav.getAttribute("GOPAGE").toString();
    //  cdnFunzione = goInserisciLav.getAttribute("GOCDNFUNZ").toString();
    //}
  }

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
   
  String strEnteRilascio  = "";
  strEnteRilascio = StringUtils.getAttributeStrNotNull(serviceRequest, "STRENTERILASCIO");

  //Setto l'origine dei dati generali da recuperare
  SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovApp.ROWS.ROW"); 
  
  if (dataOrigin != null) {
  	  if (strEnteRilascio.equals("")) {
  	  	strEnteRilascio = StringUtils.getAttributeStrNotNull(dataOrigin, "strEnteRilascio");
  	  }
	  //Dati azienda da visualizzare e da confrontare (sempre dal movimento sul DB)
	  strPartitaIvaAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strPartitaIvaAz");
	  strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strCodiceFiscaleAz");
	  strRagioneSocialeAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAz");
	  strFlgDatiOk = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGDATIOK");
	  codCCNLAz = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNLAz");  
	  descrCCNLAz = StringUtils.getAttributeStrNotNull(dataOrigin, "descrCCNLAz"); 
	  codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOAZIENDA");
	  descrTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "DESCRTIPOAZIENDA");
	  natGiuridicaAz = StringUtils.getAttributeStrNotNull(dataOrigin, "natGiuridicaAz");  
	  codNatGiuridicaAz = StringUtils.getAttributeStrNotNull(dataOrigin, "codNatGiuridicaAz");
	  strNumAlboInterinali = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMALBOINTERINALI");
		    
	  //Dati sede da visualizzare e da confrontare (sempre dal movimento sul DB)
	  strIndirizzoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAz");
	  strComuneUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAz");
	  codComuneUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "codComuneUAz");    
	  strCapUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strCapUAz");   
	  strTelUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strTelUAz");  
	  strFaxUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strFaxUAz"); 
	  strEmailUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strEmailUAz"); 
	  codAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "codAtecoUAz");  
	  strDesAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strDesAtecoUAz"); 
	  strNumRegistroCommitt = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMREGISTROCOMMITT");  
	  
	  codTipoComunicazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOCOMUNIC");
      codFiscAzPrec = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEFISCALEAZPREC");
      codComAzPrec = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCOMAZPREC");
      indirizAzPrec = StringUtils.getAttributeStrNotNull(dataOrigin,"STRINDIRIZZOAZPREC");
	  flgTrasferimento = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGTRASFER");
	  
      //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
      codtipodocex        = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPODOCEX");
      strnumdocex         = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNUMDOCEX");
      codmotivopermsoggex = StringUtils.getAttributeStrNotNull(dataOrigin,"CODMOTIVOPERMSOGGEX");
      codtipoenteprev     = StringUtils.getAttributeStrNotNull(dataOrigin,"CODENTE");
      strcodiceenteprev   = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEENTEPREV");
      flgsocio            = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGSOCIO");
      codtipotrasf        = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOTRASF"); 
      codtipocontratto    = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOCONTRATTO");
      strnumagsomm        = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNUMAGSOMMINISTRAZIONE");
      strindennitasom     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECINDENSOM");
      strrischioasbsil    = StringUtils.getAttributeStrNotNull(dataOrigin,"STRRISCHIOASBSIL");
      strvocetariffa1     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR1");
      strvocetariffa2     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR2");
      strvocetariffa3     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR3");
      codsoggetto         = StringUtils.getAttributeStrNotNull(dataOrigin,"CODSOGGETTO");
  }
  
  //mi arrivano dall'oggetto in sessione se l'oggetto è abilitato,
  //altrimenti dal DB
  if (mov.isEnabled()) {
    dataOrigin = mov.getFieldsAsSourceBean();
  }
 
  //Estraggo i dati della pagina dall'origine 
  if (dataOrigin != null) {
    codStatoAtto = StringUtils.getAttributeStrNotNull(dataOrigin, "CODSTATOATTO");   

    //Progressivi
    prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
    numKloMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMKLOMOVPREC");  
    prgMovimentoSucc = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOSUCC");
    prgMovimentoApp = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOAPP");
    prgAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDA");
    prgUnita = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITA");
    cdnLavoratore = StringUtils.getAttributeStrNotNull(dataOrigin, "CDNLAVORATORE");
    prgAziendaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDAUTILIZ");
    prgUnitaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITAUTILIZ");
       
    //Dati lavoratore
    strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull(dataOrigin, "strCodiceFiscaleLav"); 
    strNomeLav = StringUtils.getAttributeStrNotNull(dataOrigin, "strNomeLav");
    strNomeLavoratore = strNomeLav;
    strCognomeLav = StringUtils.getAttributeStrNotNull(dataOrigin, "strCognomeLav");
    datNascLav = StringUtils.getAttributeStrNotNull(dataOrigin, "datNascLav");
    strFlgCfOk = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGCFOK");
    
    //Davide 07/04/2005 aggiunto a seguito della modifica per carlo correzione inserimento lavoratore
    codCpiLav = StringUtils.getAttributeStrNotNull(dataOrigin, "CODCPILAV");
    
	//Giovanni D'Auria 21/02/2005 inizio
		codOrario = StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");
		numOreSett = StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");
	//fine    

    //Davide 07/04/2005 serve per verificare se l'azienda è stata modificata o meno
    modificataAz = StringUtils.getAttributeStrNotNull(dataOrigin, "MODIFICATAAZIENDA");
  }
  //In tutti i casi i progressivi di azienda e unita sono memorizzati in sessione
  //Li recupero conservando lo stato di abilitazione dell'oggetto
  boolean wasMovEnabled = mov.isEnabled();
  mov.enable();
  prgAziendaUtil = (String) mov.getField("PRGAZIENDAUTILIZ");
  prgUnitaUtil = (String) mov.getField("PRGUNITAUTILIZ");
 	  
  //Se sono nulli li setto a stringa vuota
  if (prgAziendaUtil == null) {prgAziendaUtil = "";}  
  if (prgUnitaUtil == null) {prgUnitaUtil = "";}
  
  //Ripristino lo stato di abilitazione precedente dell'oggetto in sessione
  if (!wasMovEnabled) {mov.disable();}
  
  //Se il cdnLvoartore non è stato impostato e l'ho trovato su DB lo imposto a quel valore
  if (cdnLavoratore.equals("") && !(cdnLavoratoreDB == null) && !cdnLavoratoreDB.equals("")) {
  	cdnLavoratore = cdnLavoratoreDB;
  }
     
  //Se sto eseguendo un refresh recupero i dati da visualizzare dalla request e non dalla sessione
  if (isRefresh) {  	
  	dataOrigin = serviceRequest;
  }  
    
  //Estraggo gli altri dati della pagina dall'origine 
  if (dataOrigin != null) {
    datComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin, "datComunicaz");  	
    posInps = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPOSINPS");
    patInail = StringUtils.getAttributeStrNotNull(dataOrigin, "strPatInail");
    strReferente = StringUtils.getAttributeStrNotNull(dataOrigin, "STRREFERENTE");
    flgInterAssPropria = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGINTERASSPROPRIA");
    codtipomov = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOV");
    codTipoMov = codtipomov;
    dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");
    dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");
    datFineMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVPREC");
    datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
    codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE");
    codMonoMovDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOMOVDICH");
    codMonoProv = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOPROV");
    codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss"); 
    codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTEMPO"); 
    datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMov");
    datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov");
    codMonoTempoAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTEMPOAVV");
    codtipomovprec = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOVPREC");
    strLuogoDiLavoro = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLUOGODILAVORO");
    codTipoTitoloStudio = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOTITOLOlav");
    
    // Stefy 07/04/05 - se la pagina è stata ricaricata dalla scelta azienda sul movimento precedente
    // prendo i valori del movimento precedente dalla request
    if(prgMovimentoPrec.length()==0 && isRefresh) {
    	prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
    	numKloMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMKLOMOVPREC");
    	//isSceltaMovPrec = true;  
    }
    strRagioneSocialeAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAzUtil");
    strIndirizzoUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAzUtil");
    strComuneUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAzUtil");
    codCpi = StringUtils.getAttributeStrNotNull(dataOrigin, "codCpi");
    numContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTNUMCONTRATTO");
    dataInizio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTINIZIOCONTRATTO");
    dataFine = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTFINECONTRATTO");
    legaleRapp = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTRAP");
    numSoggetti = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMAZINTSOGGETTI");
    classeDip = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMAZINTDIPENDENTI");
 
    modificataAz = StringUtils.getAttributeStrNotNull(dataOrigin, "MODIFICATAAZIENDA");
    codQualificaSrq = StringUtils.getAttributeStrNotNull(dataOrigin, "CODQUALIFICASRQ");
  }


  //Guardo se in sessione ho una scelta dell'utente in merito all'unita aziendale
  NavigationCache sceltaUnitaAzienda = (NavigationCache) sessionContainer.getAttribute("SCELTAUNITAAZIENDA");
  if (sceltaUnitaAzienda != null) {
  	String prgAziendaTMP = (String) sceltaUnitaAzienda.getField("PRGAZIENDA");
  	String prgUnitaTMP = (String) sceltaUnitaAzienda.getField("PRGUNITA");
	//Se non sono nulli setto l'unità aziendale
	if( prgAziendaTMP != null && prgAziendaTMP.length()>0 && 
	      prgUnitaTMP != null && prgUnitaTMP.length()>0) {
	      prgAzienda = prgAziendaTMP;
	      prgUnita = prgUnitaTMP;
	      unitaDaSessione = true;
    }
  }
  
  //Se non ho trovato il luogo di lavoro in sessione lo riscostruisco a partire dai dati sul DB
  if (!isRefresh && strLuogoDiLavoro.equals("")) {
  	strRagioneSocialeAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAzUtil");
  	strIndirizzoUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAzUtil");
  	descrComUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAzUtil");
  	if (!strRagioneSocialeAzUtil.equals("")) {
  		strLuogoDiLavoro = strRagioneSocialeAzUtil + " - " + strIndirizzoUAzUtil + "(" + descrComUAzUtil + ")";
  	}
  }
	 
  //Mi dice se i dati dell'azienda sul DB sono aggiornati o da aggiornare rispetto a quelli del movimento
  boolean datiAziendaAggiornati = true;
  //Mi dice quante unita ho trovato nel comune indicato
  int numUnitaConIndirizzoDiverso = 0;
  //Mi dice quante unita ho trovato con lo stesso indirizzo nel comune indicato
  int numUnitaConIndirizzoUguale = 0;
 
     String ragSocDB_collegato = "";
	SourceBean rigaUAzValMov = null;
  
if ((prgAzienda != null && !prgAzienda.equals("")) &&
    (prgUnita != null && !prgUnita.equals("")) ){
	rigaUAzValMov =  (SourceBean) serviceResponse.getAttribute("M_MovRicercaUnitaAziendaPerPRG.ROWS.ROW"); 	   
	numUnitaConIndirizzoUguale = 1;
	//Unita trovata (dal DB o dalla sessione), recupero i dati dell'azienda contenuti nel DB
	if (rigaUAzValMov != null) {

	  //Dati azienda da visualizzare e da confrontare (sempre dal movimento sul DB)
	  strPartitaIvaAz = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strPartitaIva");
	  strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strCodiceFiscale");
	  strRagioneSocialeAz = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strRagioneSociale");
	  codTipoAzienda = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "CODTIPOAZIENDA");
	  descrTipoAzienda = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "DESCRTIPOAZIENDA");
	  natGiuridicaAz = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "descGiuridica");  
	  codNatGiuridicaAz = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "codNatGiuridica");
	  strNumAlboInterinali = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "STRNUMALBOINTERINALI");
		    

		//prgUnita = rigaUAzValMov.getAttribute("PRGUNITA").toString();
		String strPosInpsDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strnumeroinps");
		String codAtecoDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "codateco");
		String descrAtecoDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strDescrAteco");
		String strTelDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strtel");
		String strFaxDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strfax");
		String strIndirizzoDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strindirizzo");
		String codComuneDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "codcom");
		String strDescrComDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strDescrComune");
		String capDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strcap");
		String strEmailDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "STREMAIL");
		String numRegCommittDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "STRNUMREGISTROCOMMITT");
		String codCcnlDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "codccnl");
		String descrCcnlDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "descCCNL");
	     //davide 
	    ragSocDB_collegato = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strragionesociale");
	    
	  //Confronto i dati per vedere se ci sono differenze
	  if ((!strIndirizzoUAz.equals("") && !strIndirizzoUAz.equalsIgnoreCase(strIndirizzoDB)) ||     
	   (!codAtecoUAz.equals("") && !codAtecoUAz.equals(codAtecoDB)) ||
	   (!strTelUAz.equals("") && !strTelUAz.equals(strTelDB)) || 
	   (!strFaxUAz.equals("") && !strFaxUAz.equals(strFaxDB)) ||  
	   (!codComuneUAz.equals("") && !codComuneUAz.equals(codComuneDB)) || 
	   (!strCapUAz.equals("") && !strCapUAz.equals(capDB)) ||
	   (!codCCNLAz.equals("") && !codCCNLAz.equalsIgnoreCase(codCcnlDB)) || 
	   (!strEmailUAz.equals("") && !strEmailUAz.equals(strEmailDB))) {
	   datiAziendaAggiornati = false;
	  }
	  
      //Visualizzo i dati dell'azienda contenuti sul DB invece dei quelli del movimento
	  strIndirizzoUAz = strIndirizzoDB; 
	  strComuneUAz = strDescrComDB;
	  codComuneUAz = codComuneDB;    
	  strCapUAz = capDB;   
	  strTelUAz = strTelDB;  
	  strFaxUAz = strFaxDB; 
	  strEmailUAz = strEmailDB; 
	  codAtecoUAz = codAtecoDB;  
	  strDesAtecoUAz = descrAtecoDB; 
	  strNumRegistroCommitt = numRegCommittDB;
	  codCCNLAz = codCcnlDB;  
	  descrCCNLAz = descrCcnlDB;		  		  	      
	}


} else {
  //Guardo se ho identificato l'azienda e/o l'unita aziendale, in questo caso mostro i 
  //dati del DB e li confronto per sapere se sono aggiornati
  Vector vectRicercaAz = serviceResponse.getAttributeAsVector("M_MovRicercaAziendaCF.ROWS.ROW");
  String prgAziendaEsisteInDB = "";
  if (vectRicercaAz.size() == 1) {
	//Azienda trovata, recupero i dati dell'azienda contenuti nel DB
	SourceBean rigaAzValMov = (SourceBean) vectRicercaAz.elementAt(0);
	prgAzienda = rigaAzValMov.getAttribute("PRGAZIENDA").toString();
	//prgAziendaEsisteInDB = rigaAzValMov.getAttribute("PRGAZIENDA").toString();
	String ragSocDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "strragionesociale");
	String pIvaDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "strpartitaiva");
	String codNatGiuridicaDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "codnatgiuridica");
	String descrNatGiurDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "descGiuridica");
	String codTipoAzDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "codtipoazienda");
	String descrTipoAzDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "descTipoAzienda");	    
	//String codCcnlDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "codccnl");
	//String descrCcnlDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "descCCNL");
	String strPatInailDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "strpatinail");
	String strNumAlboInterDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "strnumalbointerinali");
	String flgDatiOkDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "FLGDATIOK");
	    
	//Confronto i dati per vedere se ci sono differenze
	if ((!strPartitaIvaAz.equals("") && !strPartitaIvaAz.equals(pIvaDB)) ||   
     (!strRagioneSocialeAz.equals("") && !strRagioneSocialeAz.equalsIgnoreCase(ragSocDB))  || 
     //(!codCCNLAz.equals("") && !codCCNLAz.equalsIgnoreCase(codCcnlDB)) || 
     (!strNumAlboInterinali.equals("") && !strNumAlboInterinali.equals(strNumAlboInterDB))) {
      datiAziendaAggiornati = false;
	} 
	    
	//Visualizzo i dati dell'azienda contenuti sul DB invece di quelli del movimento
	strPartitaIvaAz = pIvaDB;
	strRagioneSocialeAz = ragSocDB;
	strFlgDatiOk = flgDatiOkDB;
	//codCCNLAz = codCcnlDB;  
	//descrCCNLAz = descrCcnlDB; 
	codTipoAzienda = codTipoAzDB;
	descrTipoAzienda = descrTipoAzDB;
	natGiuridicaAz = descrNatGiurDB;  
	codNatGiuridicaAz = codNatGiuridicaDB;
	strNumAlboInterinali = strNumAlboInterDB;	    	    
	
	//Stefy & Davide 12/04/2005 Modificata per visualizzare e confrontare correttamente i dati quando ho già scelto un'azienda
	Vector vectRicercaComuneUnitaAz = null;
	Vector vectorRicercaIndirizzoUnitaAz = new Vector();

	  vectRicercaComuneUnitaAz = serviceResponse.getAttributeAsVector("M_MovRicercaUnitaAziendaCF.ROWS.ROW");

		//Estraggo dalla ricerca le unita che hanno lo stesso indirizzo riportato nel movimento
		for (int i = 0; i < vectRicercaComuneUnitaAz.size(); i++) {
			SourceBean unita = (SourceBean) vectRicercaComuneUnitaAz.get(i);
			String indUnita = StringUtils.getAttributeStrNotNull(unita, "strindirizzo");
			if (indUnita.equalsIgnoreCase(strIndirizzoUAz)) {
				vectorRicercaIndirizzoUnitaAz.add(unita);
			}
		}
		    
		numUnitaConIndirizzoDiverso = vectRicercaComuneUnitaAz.size();
		numUnitaConIndirizzoUguale = vectorRicercaIndirizzoUnitaAz.size();
	    
	    
	if (numUnitaConIndirizzoUguale == 1) {
		//Unita trovata sul DB	      
		rigaUAzValMov = (SourceBean) vectorRicercaIndirizzoUnitaAz.elementAt(0);
	} else {
		//Guardo se in sessione ho una scelta dell'utente in merito all'unita aziendale
		sceltaUnitaAzienda = (NavigationCache) sessionContainer.getAttribute("SCELTAUNITAAZIENDA");
		if (sceltaUnitaAzienda != null) {
			//Controllo se si riferisce al movimento corrente o se è precedente
			if (prgMovimentoApp.equals(sceltaUnitaAzienda.getField("PRGMOVIMENTOAPP").toString())) {
				prgUnita = (String) sceltaUnitaAzienda.getField("PRGUNITA");
				if (prgUnita != null) {
					//Cerco nel vettore delle unita quella con progressivo unita coincidente
					for (int j = 0; j < vectRicercaComuneUnitaAz.size(); j++) {
						SourceBean unitaCorrente = (SourceBean) vectRicercaComuneUnitaAz.get(j);
						String prgUnitaCorrente = unitaCorrente.getAttribute("PRGUNITA").toString();
						if (prgUnitaCorrente.equals(prgUnita)) {
							rigaUAzValMov = unitaCorrente;
							unitaDaSessione = true;
						}
					}
				} else prgUnita = "";
			} else {
				//Se il prgMovimentoApp non coincide cancello l'oggetto perchè è vecchio
				sessionContainer.delAttribute("SCELTAUNITAAZIENDA");
			}
		}
	}
	    
	//Unita trovata (dal DB o dalla sessione), recupero i dati dell'azienda contenuti nel DB
	if (rigaUAzValMov != null) {
		prgUnita = rigaUAzValMov.getAttribute("PRGUNITA").toString();
		String strPosInpsDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strnumeroinps");
		String codAtecoDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "codateco");
		String descrAtecoDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strDescrAteco");
		String strTelDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strtel");
		String strFaxDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strfax");
		String strIndirizzoDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strindirizzo");
		String codComuneDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "codcom");
		String strDescrComDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strDescrComune");
		String capDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strcap");
		String strEmailDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "STREMAIL");
		String numRegCommittDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "STRNUMREGISTROCOMMITT");
		String codCcnlDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "codccnl");
		String descrCcnlDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "descCCNL");
	     //davide 
	    ragSocDB_collegato = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strragionesociale");
	    
	  //Confronto i dati per vedere se ci sono differenze
	  if ((!strIndirizzoUAz.equals("") && !strIndirizzoUAz.equalsIgnoreCase(strIndirizzoDB)) ||     
	   (!codAtecoUAz.equals("") && !codAtecoUAz.equals(codAtecoDB)) ||
	   (!strTelUAz.equals("") && !strTelUAz.equals(strTelDB)) || 
	   (!strFaxUAz.equals("") && !strFaxUAz.equals(strFaxDB)) ||  
	   (!codComuneUAz.equals("") && !codComuneUAz.equals(codComuneDB)) || 
	   (!strCapUAz.equals("") && !strCapUAz.equals(capDB)) ||
	   (!codCCNLAz.equals("") && !codCCNLAz.equalsIgnoreCase(codCcnlDB)) || 
	   (!strEmailUAz.equals("") && !strEmailUAz.equals(strEmailDB))) {
	   datiAziendaAggiornati = false;
	  }
	  
      //Visualizzo i dati dell'azienda contenuti sul DB invece dei quelli del movimento
	  strIndirizzoUAz = strIndirizzoDB; 
	  strComuneUAz = strDescrComDB;
	  codComuneUAz = codComuneDB;    
	  strCapUAz = capDB;   
	  strTelUAz = strTelDB;  
	  strFaxUAz = strFaxDB; 
	  strEmailUAz = strEmailDB; 
	  codAtecoUAz = codAtecoDB;  
	  strDesAtecoUAz = descrAtecoDB; 
	  strNumRegistroCommitt = numRegCommittDB;
	  codCCNLAz = codCcnlDB;  
	  descrCCNLAz = descrCcnlDB;		  		  	      
	}
  }
}//else davide
  
  //Estraggo dal DB i dati sui movimenti precedente e successivo e setto i boolean
  boolean precedente = false;
  boolean successivo = false;
  
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
 
  //Decodifica del CODMONOPROV
  String decMonoProv = "";
  if (codMonoProv.equalsIgnoreCase("M")) {
    decMonoProv = "Movimento inserito <strong>Manualmente</strong>";
  } else   if (codMonoProv.equalsIgnoreCase("S")) {
    decMonoProv = "Movimento inserito <strong>Da SARE</strong>";  
  } else   if (codMonoProv.equalsIgnoreCase("F")) {
    decMonoProv = "Movimento importato <strong>Da File</strong>";   
  }

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
  String codTipoMovCorr = codtipomov;
  String codTipoMovPrec = codtipomovprec;
  
  String flgCambiamentiDati = "";
  if (vectRicercaLav.size() > 0) {
    if ((!strCodiceFiscaleLav.toUpperCase().equals(codFiscaleLavDB.toUpperCase())) || 
     (!strNomeLavoratore.toUpperCase().equals(nomeLavDB.toUpperCase())) ||     
     (!strCognomeLav.toUpperCase().equals(cognomeLavDB.toUpperCase())) ||
     (!datNascLav.equals(datNascDB))) {
         
     flgCambiamentiDati = "L";
    }
  }
  
  String collegato = StringUtils.getAttributeStrNotNull(dataOrigin, "COLLEGATO");
  //Se possiedo tutti i dati per la ricerca del movimento precedente e non l'ho ancora impostato 
  //la faccio scattare
  
      //Davide
      /*if(!collegato.equalsIgnoreCase("precedente")) {
	     strRagioneSocialeAz = ragSocDB_collegato;
	  }
	  */
  
  //if (!isRefresh) {
  /*if (!collegato.equalsIgnoreCase("precedente")) {
      prgAzienda = prgAziendaEsisteInDB; 
  }
  */
  
  boolean eseguiRicercaPrecedente = false;
  /*
  if (!cdnLavoratore.equals("") && 
  	  !prgAzienda.equals("") && 
  	  !prgUnita.equals("") &&
  	  !codTipoMov.equals("") &&
  	  collegato.equals("")) {
  	eseguiRicercaPrecedente = true;
  }
  */
  // Modificato il 5/04/2005 da Stefy deve cercare il movimento precedente anche se non trova l'unità aziendale
  // (in questo caso si ricercano i movimenti aperti compatibili con la testata
  if (!cdnLavoratore.equals("") && 
  	  !prgAzienda.equals("") && 
  	  !codTipoMov.equals("") &&
  	  collegato.equals("")) {
  	eseguiRicercaPrecedente = true;
  }
  
  if (codTipoComunicazione.equals(MessageCodes.General.RETTIFICA_COMUNICAZIONE_PREC) || 
		  codTipoComunicazione.equals(MessageCodes.General.ANNULLAMENTO_COMUNICAZIONE_PREC) || 
		  codTipoComunicazione.equals(MessageCodes.General.ANNULLAMENTO_DA_UFFICIO_COMUNICAZIONE_PREC)) {
  	eseguiRicercaPrecedente = false;
  }
  
  if (( !datiAziendaAggiornati && codtipomov.equalsIgnoreCase("AVV") && 
        (prgAzienda != null && !prgAzienda.equals("")) &&
        (prgUnita != null && !prgUnita.equals(""))
      ) ||
      ( !datiAziendaAggiornati && !codtipomov.equalsIgnoreCase("AVV") &&
        (prgAzienda != null && !prgAzienda.equals("")) &&
        (prgUnita != null && !prgUnita.equals("")) &&
        !collegato.equalsIgnoreCase("precedente")
      )
      ) {
     aggiornaAzienda = true;
  }
   
  if (codtipotrasf.equalsIgnoreCase("DL")) {
	if (daLinguetta) {
		dataFineDistacco = (String) mov.getField("DATFINEDISTACCO");  
	}	
	else {
		if (!isRefresh) {
			dataFineDistacco = dataFine;
		}
		else {
			//in refresh dataOrigin = serviceRequest
			dataFineDistacco = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEDISTACCO");	
		}
			
	}	
  }
%>

<html>
  <head>
    <%@ include file="../../global/fieldChanged.inc" %>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
        <af:linkScript path="../../js/"/>
    <title>Dettaglio Movimento da Validare</title>
    <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
    var aziendaUtilVar = "N";
    <% if ( (!prgAziendaUtil.equals("")) && (!prgUnitaUtil.equals("")) ){%>
      aziendaUtilVar = "S";
    <%}%>
    var precedente = <%=(prgMovimentoPrec.equals("") ? "false" : "true")%>;
    var successivo = <%=(prgMovimentoSucc.equals("") ? "false" : "true")%>;
    var prgMovApp = '<%=prgMovimentoApp%>';    
    var codtipomovprec = '<%=codtipomovprec%>';
    var _funzione = '<%=_funzione%>';
    var codInterinale = '<%=codInterinale%>';
    var codTipoAzienda = '<%=codTipoAzienda%>';
    var codNatGiuridicaAz = '<%=codNatGiuridicaAz%>';
    var canModify = '<%=canModify%>';
    var codTipoMovCorr = '<%=codTipoMovCorr%>';
    var codTipoMovPrec = '<%=codTipoMovPrec%>';
    var cdnFunzione = '<%=cdnFunzione%>';
    var inserisci = 'false';
    var contesto = 'valida';
    var strPageSalto = '<%=strPageSalto%>';
    </SCRIPT>
    <%-- gestione controllo dati lavoratore (se eta <15, se in mobilita o in collocamento mirato) --%>
    <script type="text/javascript" src="../../js/movimenti/generale/DatiLavoratore.js" language="JavaScript"></script>
    <%@ include file="include/_functionControlloDatiLavoratore.inc" %>
     <!-- Gestione profilatura -->
    <%@ include file="../common/include/_gestioneProfili.inc" %>
    <%@ include file="include/_functionGestPrec.inc" %>
            
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiRicercaSoggetto.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/func_generale.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/apriAziendaUtil.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/apriAziendaMov.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/_lavoratore.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/Linguette.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/Avanti.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>

    <%@ include file="../common/include/_funzioniGenerali.inc" %>
  </head>
 
  <body class="gestione" onload="<%=(eseguiRicercaPrecedente ? "gestisciPrecedente();" : "")%>inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');rinfresca();visualizzaInterinali('<%=codTipoAzienda%>', <%=assInterna%>);gestisciRitardo();controllaInfoLavoratore();"><!--13/07/04 momentaneamente tolto, perchè da rivedere: gestioneProfilo(document.Frm1.CODTIPOMOV);-->
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
        <%if (prgAzienda.equals("")) { //Devo inserire l'azienda%>
          <a href="#" onClick="javascript:apriInserisciAzienda();"><img src="../../img/add2.gif" alt="nuova azienda" title="Inserisci azienda"></a>
        <%} else if (prgUnita.equals("") && numUnitaConIndirizzoDiverso == 0) { //Devo inserire l'unita%>
          <a href="#" onClick="javascript:apriInserisciUnitaAzienda();"><img src="../../img/add2.gif" alt="nuova unità" title="Inserisci unit&agrave; aziendale"></a>  
        <%} else if ((prgUnita.equals("") && numUnitaConIndirizzoDiverso > 0)) { //Devo scegliere l'unita%>
          <a href="#" title="Scegli unita aziendale" onClick="javascript:apriScegliUnitaAzienda(document.Frm1.PRGAZIENDA.value, <%=_funzione%>, document.Frm1.PRGMOVIMENTOAPP.value);"><img src="../../img/binocolo.gif" alt="scegli unità tra quelle trovate" title="scegli unità tra quelle trovate"></a>      	
        <%} else if (!prgAzienda.equals("") && !prgUnita.equals("")) { //Ho trovato anche l'unita%>
          <a href="#" onClick="javascript:apriUnitaAziendale(document.Frm1.PRGAZIENDA.value,document.Frm1.PRGUNITA.value,<%=_funzione%>,'0');"><img src="../../img/detail.gif" alt="Dettaglio azienda" title="Dettaglio azienda"></a>
			<% if (!(unitaDaSessione || numUnitaConIndirizzoUguale==1)) {%>&nbsp;     
			<a href="#" onClick="javascript:apriScegliUnitaAzienda(document.Frm1.PRGAZIENDA.value, <%=_funzione%>, document.Frm1.PRGMOVIMENTOAPP.value);"><img src="../../img/binocolo.gif" alt="scegli unità tra quelle trovate" title="Scegli unita aziendale"></a>      
           	<%}%> 
		 	<% if (aggiornaAzienda) {%>&nbsp;
	        <a href="#" onClick="javascript:apriAggiornaAzienda(document.Frm1.PRGAZIENDA.value,document.Frm1.PRGUNITA.value,<%=_funzione%>,document.Frm1.PRGMOVIMENTOAPP.value);"><img src="../../img/DB_img.gif" title="Aggiorna dati azienda" alt="Aggiorna dati azienda" title="Aggiorna dati azienda"></a>  
	        <%}%>  
	     <%}%>     
      </div>         
    </td>        
    </tr>       
    
    <!-- sezione riservata all'azienda che effettua il movimento e eventuale aziennda util. -->
    <%@ include file="include/azienda.inc" %>
    
    <tr>
      <td>          
        <div class="sezione2" id="lavoratore">
          <img id="tendinaLavoratore" alt="Chiudi" src="../../img/aperto.gif" onclick='cambia(this, document.getElementById("datiLavoratore"));'/>
          Lavoratore&nbsp;&nbsp;
        <%if (vectRicercaLav.size() == 0) {%>
          <div id="AggiungiLavoratoreMovimento" style="display:inline;">
          <a href="#" onClick="javascript:apriInserisciLavoratore();"><img src="../../img/add2.gif" alt="nuovo lavoratore"></a>
          </div>
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
  
  <%-- // inserito su richiesta di Davide per conto di Carlo il 5/04/2005 da Stefy  --%>
  <input type="hidden" name="CODCPILAV" value="<%=codCpiLav%>"/>	
  
  <input type="hidden" name="RESETCFL" value="false"/>	
  <input type="hidden" name="CODTIPOASS" value="<%=codTipoAss%>"/>
  <input type="hidden" name="CODQUALIFICASRQ" value="<%=codQualificaSrq%>"/>
  <input type="hidden" name="CODTIPOCOMUNIC" value="<%=codTipoComunicazione%>"/>
  <input type="hidden" name="PRGMOVIMENTOAPP" value="<%=prgMovimentoApp%>"/>
  <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
  <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
  <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
  <input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>"/>
  <input type="hidden" name="CODTIPOAZIENDAUTIL" value="<%=codTipoAziendaUtil%>"/>
  <input type="hidden" name="DATAINIZIOAVV" value="<%=dataInizioAvv%>"/>
  <input type="hidden" name="DATINIZIOMOVPREC" value="<%=dataInizioMovPrec%>"/>   
  <input type="hidden" name="CODMONOTEMPOAVV" value="<%=codMonoTempoAvv%>"/>
  <input type="hidden" name="DATFINEMOVEFFETTIVA" value="<%=datFineMovEff%>"/>      
  <input type="hidden" name="CODMONOTIPOFINE" value="<%=codMonoTipoFine%>"/>
  <input type="hidden" name="CODMONOTEMPO" value="<%=codMonoTempo%>"/>
   
  <input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>
  <input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
  <input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>
  <input type="hidden" name="DATFINEMOVPREC" value="<%=datFineMovPrec%>"/>
        
  <input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
  <input type="hidden" name="ACTION" value="refresh"/>  
  
  <input type="hidden" name="LAVORATORECOLLMIRATO" value="<%=strLavCollMirato%>"/>
  
  <input type="hidden" name="COLLEGATO" value="<%=collegato%>"/>      
  <input type="hidden" name="PAGE" value="MovValidaDettaglioGeneralePage"/>
  <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
        
  <input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
  <input type="hidden" name="CODMONOPROV" value="<%=codMonoProv%>"/>
  <input type="hidden" name="NUOVODOCUMENTO" value=""/>

  <!-- Sezione di gestione impatti tenendo conto della profilatura -->
  <input type="hidden" name="permettiImpatti" value="" />
  <input type="hidden" name="CANVIEW" value="" />
  <input type="hidden" name="CANEDITLAV" value="" />
  <input type="hidden" name="CANEDITAZ" value="" />
  
  <input type="hidden" name="flgCambiamentiDati" value="<%=flgCambiamentiDati%>">
  <!--Gestione comunicazioni in ritardo per lavoratori disabili-->
  <input type="hidden" name="LAVORATOREDISABILE" value="<%=strDisabile%>"/>
  	
  <input type="hidden" name="CODCPI" value="<%=codCpi%>"/>      
  <input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=prgAziendaUtil%>"/>
  <input type="hidden" name="PRGUNITAUTILIZ" value="<%=prgUnitaUtil%>"/>
  <input type="hidden" name="STRAZINTNUMCONTRATTO" value="<%=numContratto%>">
  <input type="hidden" name="DATAZINTINIZIOCONTRATTO" value="<%=dataInizio%>">
  <input type="hidden" name="DATAZINTFINECONTRATTO" value="<%=dataFine%>">
  <input type="hidden" name="STRAZINTRAP" value="<%=legaleRapp%>">
  <input type="hidden" name="NUMAZINTSOGGETTI" value="<%=numSoggetti%>">
  <input type="hidden" name="NUMAZINTDIPENDENTI" value="<%=classeDip%>">
  <input type="hidden" name="strRagioneSocialeAzUtil" value="<%=strRagioneSocialeAzUtil%>"/>
  <input type="hidden" name="strIndirizzoUAzUtil" value="<%=strIndirizzoUAzUtil%>"/>
  <input type="hidden" name="strComuneUAzUtil" value="<%=strComuneUAzUtil%>"/>
  <input type="hidden" name="MODIFICATAAZIENDA" value="<%=modificataAz%>"/>

  <!-- DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO -->
  <input type="hidden" name="CODTIPODOCEX"        value="<%=codtipodocex%>" />
  <input type="hidden" name="STRNUMDOCEX"         value="<%=strnumdocex%>" />
  <input type="hidden" name="CODMOTIVOPERMSOGGEX" value="<%=codmotivopermsoggex%>" />
  <input type="hidden" name="CODENTE"     value="<%=codtipoenteprev%>" />
  <input type="hidden" name="STRCODICEENTEPREV"   value="<%=strcodiceenteprev%>" />
  <input type="hidden" name="FLGSOCIO"            value="<%=flgsocio%>" />
  <input type="hidden" name="CODTIPOTRASF"        value="<%=codtipotrasf%>" />
  <input type="hidden" name="DATFINEDISTACCO"    value="<%=dataFineDistacco%>" />
  <input type="hidden" name="STRCODICEFISCALE" value="<%=strCodiceFiscaleLav%>" />
  
  <!-- Aggiungo i campi relativi all'azienda precedente nel caso di validazione 
  di una trasformazione con flag trasferimento = "S" -->
  <input type="hidden" name="FLGTRASFER" value="<%=flgTrasferimento%>"/>
  <%if ( codTipoMov.equalsIgnoreCase("TRA") && flgTrasferimento.equalsIgnoreCase("S") ) {
  %>
	  <input type="hidden" name="STRCODICEFISCALEAZPREC" value="<%=codFiscAzPrec%>"/>
	  <input type="hidden" name="CODCOMAZPREC" value="<%=codComAzPrec%>"/>
	  <input type="hidden" name="STRINDIRIZZOAZPREC" value="<%=indirizAzPrec%>"/>  
  <%}%>
  <input type="hidden" name="CODTIPOCONTRATTO"    value="<%=codtipocontratto%>" />
  <input type="hidden" name="STRNUMAGSOMMINISTRAZIONE"        value="<%=strnumagsomm%>" />
  <input type="hidden" name="DECINDENSOM"     value="<%=strindennitasom%>" />
  <input type="hidden" name="STRRISCHIOASBSIL"    value="<%=strrischioasbsil%>" />
  <input type="hidden" name="DECVOCETAR1"     value="<%=strvocetariffa1%>" />
  <input type="hidden" name="DECVOCETAR2"     value="<%=strvocetariffa2%>" />
  <input type="hidden" name="DECVOCETAR3"     value="<%=strvocetariffa3%>" />
  <input type="hidden" name="CODSOGGETTO"         value="<%=codsoggetto%>" />

  <center>
  <%@ include file="../common/include/GestioneCollegati.inc" %>
  <%@ include file="../common/include/PulsanteRitornoLista.inc" %>  
  </center>
  </center>
  </af:form>
  <center>

  <%if (vectRicercaLav.size() == 0) {%>
    <af:form name="frmLav">
    <input type="hidden" name="codFiscaleLavMov" value="<%=strCodiceFiscaleLav%>">
    <input type="hidden" name="nomeLavMov" value="<%=strNomeLavoratore%>">
    <input type="hidden" name="cognomeLavMov" value="<%=strCognomeLav%>">
    <input type="hidden" name="datNascitaLavMov" value="<%=datNascLav%>">
    </af:form>
  <%}%>
      
  </center>  

  <script language="javascript">
    <% if ((!prgAziendaUtil.equals("")) && (!prgUnitaUtil.equals(""))){%>
      document.Frm1.STRLUOGODILAVORO.readOnly = true;
    <%}%>
  </script>
 </body>
</html>