<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
 
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.module.movimenti.constant.Properties,
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
  String prgMovimento = "";
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "MovValidaDettaglioGeneralePage");
  boolean canModify = attributi.containsButton("SALVA");
  boolean canArchiviaProfilo = attributi.containsButton("ARCHIVIA");
  boolean canArchiviaConfig = false;
  boolean canArchivia = false;
  String strArchivia = "false";
  
  if (serviceResponse.containsAttribute("M_GETCONFIG_ARCHIVIA_MOV")) {
	  SourceBean sbConfigArchivia = (SourceBean)serviceResponse.getAttribute("M_GETCONFIG_ARCHIVIA_MOV.ROWS.ROW");
	  if (sbConfigArchivia != null) {  
		  canArchiviaConfig = sbConfigArchivia.containsAttribute("STRVALORE")&&sbConfigArchivia.getAttribute("STRVALORE").toString().equalsIgnoreCase("true")?true:false;
	  }
	  if (canArchiviaConfig && canArchiviaProfilo) {
		canArchivia = true;
		strArchivia = "true";	  
	  }
	  else {
		canArchivia = false;
	    strArchivia = "false";         
	  }
  }
  else {
	  strArchivia = StringUtils.getAttributeStrNotNull(serviceRequest, "CONFIGARCHMOV");
	  if (strArchivia.equalsIgnoreCase("true")) {
	    canArchivia = true; 
	  }
	  else {
	    canArchivia = false;
	  }
  }
  
  String configEntePromotore = serviceResponse.containsAttribute("M_Config_Ente_Promotore.ROWS.ROW.NUM")?
		  	serviceResponse.getAttribute("M_Config_Ente_Promotore.ROWS.ROW.NUM").toString():"0";
  String datNormativaPrec297 = "";
  SourceBean sbConfigDataNormativa = (SourceBean) serviceResponse.getAttribute("M_CONFIG_DATA_NORMATIVA_297.rows.row");
  if (sbConfigDataNormativa != null && sbConfigDataNormativa.containsAttribute("strvalore")) {
	datNormativaPrec297 = sbConfigDataNormativa.getAttribute("strvalore").toString();
  }
  else {
	datNormativaPrec297 = it.eng.sil.util.amministrazione.impatti.EventoAmministrativo.DATA_NORMATIVA_DEFAULT;
  }
  
  boolean canModifyProtocol = false;
  boolean canModifyStato = false;
  String collegato = StringUtils.getAttributeStrNotNull(serviceRequest, "COLLEGATO");
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");

  //Oggetti per l'applicazione dello stile
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTableNoBr(canModify);
  
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
  	canArchivia = false;
  }
  
  String codMvCessazionePrec = "";
  boolean inserisci = false;
  boolean salva = false;
  boolean valida = true;
  boolean consulta = false;
  boolean rettifica = false;
  boolean isCollegato = false;
  boolean daLista = true;
  
  //Guardo da dove provengo
  String provenienza = StringUtils.getAttributeStrNotNull(serviceRequest, "PROVENIENZA");
  boolean daValidazione = provenienza.equalsIgnoreCase("validazione");
  boolean daLinguetta =  provenienza.equalsIgnoreCase("linguetta"); 
  String strDisabile = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATOREDISABILE");
  //Controllo se sto eseguendo il refresh della pagina
  boolean isRefresh = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTION").equalsIgnoreCase("refresh");
  
  //Cancello l'oggetto del movimento in sessione se non arrivo da una linguetta
  if (!daLinguetta && !isRefresh) {
    sessionContainer.delAttribute("MOVIMENTOCORRENTE");
  }
  
  if (daLinguetta) {
	  codMvCessazionePrec = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMVCESSAZIONEPREC");  
  }
  
  String forzaInsEtaApprendista = StringUtils.getAttributeStrNotNull(serviceRequest, "FORZA_INSERIMENTO_ETA_APPRENDISTATO");
  if (forzaInsEtaApprendista.equals("")) forzaInsEtaApprendista = "false";
  %>
 <%@ include file="GestioneOggettoMovimento.inc" %>
 <%@ include file="common/include/_segnalazioniGgRit.inc" %> 
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
  String codComuneUAzTra = "";
  String strCapUAz = "";
  String strTelUAz = "";
  String strFaxUAz = "";
  String strEmailUAz = "";
  String codAtecoUAz = "";
  String strDesAtecoUAz = "";
  String codCCNLAz = "";
  String descrCCNLAz = "";
  String codCCNL = "";
  String strDescrizioneCCNL = "";
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
  //String descrTipoAziendaUtil = "";
  String strNumAlboInterinali = "";
  String strNumRegistroCommitt = "";
  String flgInterAssPropria = "";
  String strLuogoDiLavoro = "";
  String prgAziendaUtil = "";
  String prgUnitaUtil = ""; 
  String datFineMovEff = "";
  String codMonoTipoFine = "";
  String codMonoMovDich = "";
  String strFlgDatiOk = "";
  String codStatoAtto = "";
  String codMonoProv = "";
  String strReferente = "";
  String numGgTraMovComunicaz = "";
  String datFineMov = "";
  String datFineMovPF = "";
  String codTipoTitoloStudio = "";
  String codMotAnnullamento = ""; 
  String codMvCessazione = "";
  String sezTitolo = ""; 
  String numGGEffettuatiAgr = "";
  String codAgevolazione = "";
  String descrMansione = "";
  String codMansione="";
  String descrTipoMansione = "";
  String decRetribuzioneAnn = "";
  String autorizzaDurataTD = ""; 
  String numConvenzione = "";
  String datConvenzione = "";
  String datFineSgravio = "";
  String decImportoConcesso = "";
  String flgLegge68="";
  String flgAssObbl="";
  String codCatAssObbl="";
  String flgLavoroAgr="";
  String flgDistParziale="";
  String flagAziEstera="";
  
  String codMvTrasformazione = "";
  String flgAutocertificazione = "N";
  
  String descrTipoAss="";
  String codContratto="";
  String codMonoTipo="";
  String flgContrattoTI="";
  String datfinemissione="";
  String datiniziomissione="";
 
  String strMatricola="";
  String titolo="";
  
  //Apprendistato
  String strCognomeTutore = "";
  String strNomeTutore = "";
  String strCodiceFiscaleTutore = "";
  String codTipoEntePromotore = "";
  String codQualificaSrq = "";
  String strNote = "";
  String flgTitolareTutore = "";
  String numAnniEspTutore = "";
  String strLivelloTutore = "";
  String codMansioneTutore = "";
  String flgArtigiana = "";
  //tirocinio
  String strCodFiscPromotoreTir = "";
  String codCategoriaTir = "";
  String codTipologiaTir = "";
  String strDenominazioneTir = "";
  String codSoggPromotoreMin = "";
  
  String codCpiLav = "";
  String modificataAz = "";
  String numLivello="";
  String descrStatoOcc ="";
  String datInizioOcc="";
  String datAnzOcc="";
  String decRetribuzioneMen = "";
  String numGGPrevistiAgr = "";
  String strDesAttivita = "";
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
  
  String datFineDistacco="";
  
  String prgAziendaTra = "";
  String prgUnitaTra = "";
  String datInizioMovPro = "";
  
  String codComunicazione="";
  String codComunicazionePrec="";
  String datInizioAVVperCVE = "";
  
  String flgAzUtilizEstera="";
  String dataFineAffittoRamo="";
  
  //Variabili per la gestione della protocollazione ================

  //Reperisco la data e ora corrente
  //String dataOraProt = (new SimpleDateFormat("dd/MM/yyyy HH:mm")).format(new Date());
  
  String prAutomatica     = null;
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
  String codLavorazione = "";
  
  //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
  String strEnte = "";
  String strcodiceenteprev = "";
  String codtipocontratto = "";
  String datFineMovPrec = "";
  String strCodiceFiscaleAzTra="";
  String strRagioneSocialeAzTruncTra = "";
  String strIndirizzoUAzTra = "";
  String strComuneUAzTra = "";
  String strCapUAzTra = "";
  String strRagioneSocialeAzTra="";
  String datInizioMovTra="";
  String datInizioMovCes="";
  String datFineMovPro="";
  String datInizioProt="";
  String strComuneUAzPrec = "";
  
  //Decreto gennaio 2013
  String flgLavInMobilita = "";
  String flgLavStagionale = "";
  String flgProsecuzione = "";
  String codVariazione = "";
  //fine Decreto gennaio 2013
  
  //Decreto 15/11/2011 Azienda somministrazione estera
  String ragSocSommEstera = "";
  String cfSommEstera = "";
  
  //Decreto Novembre 2019
  String strVersioneTracciato = "";
  
  String valDisplay="none";

  // Stefy e Davide 11/04/2005
  boolean unitaDaSessione = false;
  //boolean mostraRicercaAZ = false;
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
    descrStatoOcc = StringUtils.getAttributeStrNotNull(rigaLavValMov, "descrStatoOcc");
	datInizioOcc = StringUtils.getAttributeStrNotNull(rigaLavValMov, "datInizioOcc");
	datAnzOcc = StringUtils.getAttributeStrNotNull(rigaLavValMov, "datAnzOcc");
  }
  
  Vector rowsComboOrario = serviceResponse.getAttributeAsVector("ComboTipoOrario.ROWS.ROW");
  Vector rowsCatAssObbl = serviceResponse.getAttributeAsVector("ComboCategoriaAssObbligatoria.ROWS.ROW");
  
  Vector rowsAgevolazioni = serviceResponse.getAttributeAsVector("M_MovGetDettMovAppAgevolazioni.ROWS.ROW");
  for (int nAgev=0;nAgev<rowsAgevolazioni.size();nAgev++) {
		SourceBean appAgev = (SourceBean)rowsAgevolazioni.get(nAgev);
		String codAgevolazioneCurr = appAgev.getAttribute("codAgevolazione").toString();
		if (codAgevolazione.equals("")) {
			codAgevolazione = codAgevolazioneCurr;
		}
		else {
			codAgevolazione = codAgevolazione + "," + codAgevolazioneCurr;		
		}
  }
  
  //Numero protocollo da applicare se in inserimento nuovo
  rows = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
  if(rows != null && !rows.isEmpty()) { 
  	row = (SourceBean) rows.elementAt(0);
   	prAutomatica     = (String) row.getAttribute("FLGPROTOCOLLOAUT");
   	if ( prAutomatica.equalsIgnoreCase("N") ){ numProtEditable = true; }
   		numProtV         = SourceBeanUtils.getAttrBigDecimal(row, "NUMPROTOCOLLO", null);
   		numAnnoProtV     = (BigDecimal) row.getAttribute("NUMANNOPROT");
   		kLockProt        = (BigDecimal) row.getAttribute("NUMKLOPROTOCOLLO");
 	}
   
  String strEnteRilascio = StringUtils.getAttributeStrNotNull(serviceRequest, "STRENTERILASCIO");

  SourceBean dataOrigin = null;
  String controllaProroghe = "";
  SourceBean dataProrogheMis = (SourceBean) serviceResponse.getAttribute("M_ControllaProrogheSomm");
  String pageRetListaDaArch = StringUtils.getAttributeStrNotNull(serviceRequest, "PAGERITORNOLISTA");
  //Se sto eseguendo un refresh recupero i dati da visualizzare dalla request e non dalla sessione
  if (isRefresh) {  	
  	dataOrigin = serviceRequest;
  } else { 
	dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovApp.ROWS.ROW");
  }
  
  if (dataOrigin != null) {
  	if (strEnteRilascio.equals("")) {
  		strEnteRilascio = StringUtils.getAttributeStrNotNull(dataOrigin, "strEnteRilascio");
  	}
  	
  	if(dataProrogheMis != null && dataProrogheMis.containsAttribute("ROW")) {
    	codtipomov = StringUtils.getAttributeStrNotNull(dataProrogheMis, "ROW.CODTIPOMOV");
    	datFineMovPro = StringUtils.getAttributeStrNotNull(dataProrogheMis, "ROW.DATFINEMOV");
    	controllaProroghe = StringUtils.getAttributeStrNotNull(dataProrogheMis, "ROW.controllaProroghe");
    }
  	
  	else {
  		codtipomov = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOV");
  	}
  		
  	codMvTrasformazione = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOTRASF"); 
  	
  	//Dati azienda da visualizzare e da confrontare (sempre dal movimento sul DB)
  	strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strCodiceFiscaleAz");
  	strPartitaIvaAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strPartitaIvaAz");
  	if(codtipomov.equals("TRA") && codMvTrasformazione.equals("TL") && !isRefresh ) {
  		strRagioneSocialeAz = StringUtils.getAttributeStrNotNull(dataOrigin, "STRRAGIONESOCIALEAZPREC");
  		strTelUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "STRTELAZPREC");  
		strFaxUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "STRFAXAZPREC"); 
		strEmailUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "STREMAILAZPREC");
		strIndirizzoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "STRINDIRIZZOAZPREC");
		strComuneUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAzPrec");
		strComuneUAzPrec = strComuneUAz;
		codComuneUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "CODCOMAZPREC");    
		strCapUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "STRCAPAZPREC");
		codAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "CODATECOAZPREC");  
		strDesAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "descrAtecoUAzPrec");
		patInail = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPATAZPREC");
	} else {
    	strRagioneSocialeAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAz");
		strFlgDatiOk = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGDATIOK");
		codCCNLAz = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNLAz");  
		descrCCNLAz = StringUtils.getAttributeStrNotNull(dataOrigin, "descrCCNLAz"); 
		natGiuridicaAz = StringUtils.getAttributeStrNotNull(dataOrigin, "natGiuridicaAz");  
		codNatGiuridicaAz = StringUtils.getAttributeStrNotNull(dataOrigin, "codNatGiuridicaAz");
		strNumAlboInterinali = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMALBOINTERINALI");
		strIndirizzoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAz");
		strComuneUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAz");
		strCapUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strCapUAz");   
		strTelUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strTelUAz");  
		strFaxUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strFaxUAz"); 
		strEmailUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "strEmailUAz"); 
		codAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "codAtecoUAz");  
		strDesAtecoUAz = StringUtils.getAttributeStrNotNull(dataOrigin, "descrAtecoUAz"); 
		strNumRegistroCommitt = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMREGISTROCOMMITT");
		posInps = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPOSINPS");
    	patInail = StringUtils.getAttributeStrNotNull(dataOrigin, "strPatInail");
    	strReferente = StringUtils.getAttributeStrNotNull(dataOrigin, "STRREFERENTE");
    }
    
	codTipoComunicazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOCOMUNIC");
	codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOAZIENDA");
	descrTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "DESCRTIPOAZIENDA");
	flgInterAssPropria = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGINTERASSPROPRIA");
	codFiscAzPrec = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEFISCALEAZPREC");
    codComAzPrec = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCOMAZPREC");
    indirizAzPrec = StringUtils.getAttributeStrNotNull(dataOrigin,"STRINDIRIZZOAZPREC");
	flgTrasferimento = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGTRASFER");
	numGgTraMovComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGTRAMOVCOMUNICAZIONE");
    strEnte     = StringUtils.getAttributeStrNotNull(dataOrigin,"STRENTE");
    strcodiceenteprev   = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEENTEPREV");
    codtipocontratto    = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOCONTRATTO");

    ragSocSommEstera = StringUtils.getAttributeStrNotNull(dataOrigin,"RAGSOCAZESTERA");
    cfSommEstera = StringUtils.getAttributeStrNotNull(dataOrigin,"CODFISCAZESTERA");
    codTipoEntePromotore = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOENTEPROMOTORE");
    strCodFiscPromotoreTir = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODFISCPROMOTORETIR");
    codCategoriaTir = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCATEGORIATIR");
	codTipologiaTir = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOLOGIATIR");
	strDenominazioneTir = StringUtils.getAttributeStrNotNull(dataOrigin,"STRDENOMINAZIONETIR");
	codSoggPromotoreMin = StringUtils.getAttributeStrNotNull(dataOrigin,"CODSOGGPROMOTOREMIN");
	//Decreto Novembre 2019
	strVersioneTracciato = StringUtils.getAttributeStrNotNull(dataOrigin,"STRVERSIONETRACCIATO");
		
	if (strDenominazioneTir != null) {
		strDenominazioneTir = JavaScript.escape(strDenominazioneTir);
	}	
	
	//Decreto gennaio 2013
  	datFineMovPF = StringUtils.getAttributeStrNotNull(dataOrigin,"DATFINEPF");
  	flgLavInMobilita = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLAVOROINMOBILITA");
  	flgLavStagionale = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLAVOROSTAGIONALE");
  	flgProsecuzione = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGPROSECUZIONE");
  	codVariazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODVARIAZIONE");
  	//fine Decreto gennaio 2013
  	
  }  

  //Recupero la data di inizio dell'avviamento collegato alla cessazione (se non c'è ho una stringa vuota)
  datInizioAVVperCVE = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovGetDettMovAppCVE.ROWS.ROW.datInizioMov");
  String prgMovimentoAppCVE = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovGetDettMovAppCVE.ROWS.ROW.PRGMOVIMENTOAPP");
  
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
    
    strMatricola = StringUtils.getAttributeStrNotNull(dataOrigin, "strMatricola");
    codContratto=StringUtils.getAttributeStrNotNull(dataOrigin, "codContratto");
 	codMonoTipo=StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTipo");
 	flgContrattoTI=StringUtils.getAttributeStrNotNull(dataOrigin, "FLGCONTRATTOTI");
 	datiniziomissione = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIORAPLAV");
    datfinemissione = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINERAPLAV");
    //numAziSomm=StringUtils.getAttributeStrNotNull(dataOrigin, "numAziSomm");
  	titolo=StringUtils.getAttributeStrNotNull(dataOrigin, "titolo");
  	
 	//Dati lavoratore
    strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull(dataOrigin, "strCodiceFiscaleLav"); 
    strNomeLav = StringUtils.getAttributeStrNotNull(dataOrigin, "strNomeLav");
    strNomeLavoratore = strNomeLav;
    strCognomeLav = StringUtils.getAttributeStrNotNull(dataOrigin, "strCognomeLav");
    datNascLav = StringUtils.getAttributeStrNotNull(dataOrigin, "datNascLav");
    codCpiLav = StringUtils.getAttributeStrNotNull(dataOrigin, "CODCPILAV");
   	modificataAz = StringUtils.getAttributeStrNotNull(dataOrigin, "MODIFICATAAZIENDA");
  }
  //In tutti i casi i progressivi di azienda e unita sono memorizzati in sessione
  //Li recupero conservando lo stato di abilitazione dell'oggetto
  mov.enable();
  prgAziendaUtil = (String) mov.getField("PRGAZIENDAUTILIZ");
  prgUnitaUtil = (String) mov.getField("PRGUNITAUTILIZ");
  //Se sono nulli li setto a stringa vuota
  if (prgAziendaUtil == null) {prgAziendaUtil = "";}  
  if (prgUnitaUtil == null) {prgUnitaUtil = "";}
  
  //Ripristino lo stato di abilitazione precedente dell'oggetto in sessione
  //if (!wasMovEnabled) {mov.disable();}
  
  //Se il cdnLvoartore non è stato impostato e l'ho trovato su DB lo imposto a quel valore
  if (cdnLavoratore.equals("") && !(cdnLavoratoreDB == null) && !cdnLavoratoreDB.equals("")) {
  	cdnLavoratore = cdnLavoratoreDB;
  }
  
  if (serviceRequest.containsAttribute("codAgevolazione")) {
  	codAgevolazione = "";
	rowsAgevolazioni = serviceRequest.getAttributeAsVector("codAgevolazione");
	for (int nAgev=0;nAgev<rowsAgevolazioni.size();nAgev++) {
		String codAgevolazioneCurr = rowsAgevolazioni.get(nAgev).toString();
		if (codAgevolazione.equals("")) {
			codAgevolazione = codAgevolazioneCurr;
		}
		else {
			codAgevolazione = codAgevolazione + "," + codAgevolazioneCurr;		
		}
	 }	
  }
  
  //Estraggo gli altri dati della pagina dall'origine 
  if (dataOrigin != null) {
    datComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin, "datComunicaz");  	
    
    codTipoMov = codtipomov;
    dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");
    dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");
    
    datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
    codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE");
    codMonoMovDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOMOVDICH");
    if (codMonoMovDich.equalsIgnoreCase("C")){
    	flgAutocertificazione = "S";
  	}
    codMonoProv = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOPROV");
    codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss");
    descrTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoAss");
    codMonoTipo=StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTipo");
    decRetribuzioneMen = StringUtils.getAttributeStrNotNull(dataOrigin, "decRetribuzioneMen"); 
    //Decreto Novembre 2019
    decRetribuzioneAnn = StringUtils.getAttributeStrNotNull(dataOrigin, "decRetribuzioneAnn");
    
	if(decRetribuzioneAnn.equals("")){
	    if(!decRetribuzioneMen.equals("")) {
			decRetribuzioneMen=decRetribuzioneMen.replace(',','.');
	  		Float decMen = new Float(decRetribuzioneMen);
	  		float decRetribAnnInt = decMen.floatValue() * 12;
	  		//int dec = (int) decRetribAnnInt;
	  		int dec = (int) Math.round(decRetribAnnInt);
	  		decRetribuzioneMen = String.valueOf(decMen);
			decRetribuzioneAnn = String.valueOf(dec);	
		}
		
	}
	
    codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTEMPO");
    codOrario = StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");
	codCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNL");   
	strDescrizioneCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "strCCNL"); 
	numOreSett = StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");
	datFineSgravio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINESGRAVIO");
    decImportoConcesso = StringUtils.getAttributeStrNotNull(dataOrigin, "DECIMPORTOCONCESSO"); 
    codMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMansione");
    descrMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrMansione"); 
	descrTipoMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoMansione");
    if(descrMansione.equals("")) {   
		descrMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "DESCMANSIONE"); 
		descrTipoMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "strTipoMansione");
    }
    strDesAttivita = StringUtils.getAttributeStrNotNull(dataOrigin, "strDesAttivita");
    datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMov");
    datFineMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMovPrec");
    if(!codTipoMov.equals("AVV")) {
    	datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC"); 
    } else {	
  		datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOV");
  		datInizioProt = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOV");
  	}
    if(!codTipoMov.equals("AVV")) {
    	//Mi dice se devo mostrare il campo della data di inizio avviamento
    	if (!"".equals(datInizioAVVperCVE) && !collegato.equals("precedente") ) {
    		datInizioMov = datInizioAVVperCVE;
    	}
    	if (codTipoMov.equals("CES")) {
    		datInizioMovCes = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMovCes");
        	if(datInizioMovCes.equals("")) {
        		datInizioMovCes = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOV");
        	}
        	datInizioProt = datInizioMovCes;
        	codMvCessazione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMvCessazione");	
    	}
    }
    if(codTipoMov.equals("PRO") && controllaProroghe.equals("")) {
  		datFineMovPro = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMovPro");
  		datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMovPrec");
  		if (datFineMov.equals("")) {
  			if(isRefresh)  {
  				datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOV");	
  			}
  			else {
  				datFineMov = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovGetDettMovAppCVE.ROWS.ROW.datFineMov");	
  			}	
  		}
    	if(datFineMovPro.equals("")) {
    		if(isRefresh)  {
    			datFineMovPro = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
    		}
    		else {
    			datFineMovPro = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOV");
    		}
    	}
	}  
    if(codTipoMov.equals("TRA")) {
    	datInizioMovTra = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMovTra");
    	if(datInizioMovTra.equals("")) {
    		datInizioMovTra = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOV");
    	}
    	datInizioProt = datInizioMovTra;
    }
    
    codMonoTempoAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTEMPOAVV");
    codtipomovprec = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOVPREC");
    strLuogoDiLavoro = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLUOGODILAVORO");
    codTipoTitoloStudio = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOTITOLOlav");
  	flgLegge68 = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLEGGE68");
  	flgAssObbl = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGASSOBBL");
	codCatAssObbl = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCATASSOBBL");
    numConvenzione = StringUtils.getAttributeStrNotNull(dataOrigin, "numConvenzione");
	datConvenzione = StringUtils.getAttributeStrNotNull(dataOrigin, "datConvenzione");
	strRagioneSocialeAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAzUtil");
    strIndirizzoUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAzUtil");
    strComuneUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAzUtil");
    codComunicazione = StringUtils.getAttributeStrNotNull(dataOrigin, "CODCOMUNICAZIONE");
	codComunicazionePrec = StringUtils.getAttributeStrNotNull(dataOrigin, "CODCOMUNICAZIONEPREC");
    if (isRefresh) {
    	datFineDistacco = StringUtils.getAttributeStrNotNull(dataOrigin,"DATFINEDISTACCO");
    	strRagioneSocialeAzTruncTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAzTruncTra");
		strIndirizzoUAzTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAzTra");
		codComuneUAzTra=StringUtils.getAttributeStrNotNull(dataOrigin, "codComuneUAzTra");
		strRagioneSocialeAzTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAzTra");
		strComuneUAzTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAzTra");
		strCapUAzTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strCapUAzTra");
		if(codTipoMov.equals("CES")) {
	      	numLivello = StringUtils.getAttributeStrNotNull(dataOrigin, "numLivelloCes");
	      	codLavorazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODLAVORAZIONECES");
	      	flgLavoroAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLAVOROAGRCES");
	      	
	    } else { 
	    	numLivello = StringUtils.getAttributeStrNotNull(dataOrigin, "numLivello");
	    	codLavorazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODLAVORAZIONE");
	    	flgLavoroAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLAVOROAGR");
	  	}
		
		if (strVersioneTracciato == null || strVersioneTracciato.equals("")){ 
			strVersioneTracciato = StringUtils.getAttributeStrNotNull(dataOrigin,"STRVERSIONETRACCIATOhid");
		}
		
		if (codComunicazione == null || codComunicazione.equals("")){
			codComunicazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCOMCOAPP");
		}
		if (codComunicazionePrec == null || codComunicazionePrec.equals("")){
			codComunicazionePrec = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCOMCOAPPPREC");	
		}
	}
    else {
    	datFineDistacco = StringUtils.getAttributeStrNotNull(dataOrigin,"DATAZINTFINECONTRATTO");
    	numLivello = StringUtils.getAttributeStrNotNull(dataOrigin, "numLivello");
    	codLavorazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODLAVORAZIONE");
    	flgLavoroAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLAVOROAGR");
    	strRagioneSocialeAzTruncTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAz");
    	strRagioneSocialeAzTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAz");
    	strIndirizzoUAzTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAz");
    	codComuneUAzTra=StringUtils.getAttributeStrNotNull(dataOrigin, "codComuneUAz");
    	strComuneUAzTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAz");
		strCapUAzTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strCapUAz");
	}
    
    flgDistParziale = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGDISTPARZIALE");
    flagAziEstera = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGDISTAZESTERA");
    // Stefy 07/04/05 - se la pagina è stata ricaricata dalla scelta azienda sul movimento precedente
    // prendo i valori del movimento precedente dalla request
    if(prgMovimentoPrec.length()==0 && isRefresh) {
    	prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
    	numKloMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMKLOMOVPREC");
    	//isSceltaMovPrec = true;  
    }
    autorizzaDurataTD = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGAUTORIZZADURATATD");
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
    strCognomeTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "STRCOGNOMETUTORE");
  	strNomeTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNOMETUTORE");
  	strCodiceFiscaleTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "STRCODICEFISCALETUTORE");
	codQualificaSrq = StringUtils.getAttributeStrNotNull(dataOrigin, "CODQUALIFICASRQ");
	strNote = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNOTE");
	flgTitolareTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGTITOLARETUTORE");
	numAnniEspTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMANNIESPTUTORE");
    strLivelloTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLIVELLOTUTORE");
    codMansioneTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMANSIONETUTORE");
	flgArtigiana = StringUtils.getAttributeStrNotNull(dataOrigin, "flgArtigiana");
	numGGPrevistiAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGPREVISTIAGR");
   	numGGEffettuatiAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGEFFETTUATIAGR");
	flgAzUtilizEstera=StringUtils.getAttributeStrNotNull(dataOrigin, "FLGAZUTILIZESTERA");
    dataFineAffittoRamo=StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEAFFITTORAMO");
    //tirocinio
    strCodFiscPromotoreTir = StringUtils.getAttributeStrNotNull(dataOrigin, "STRCODFISCPROMOTORETIR");  
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
 
  //String ragSocDB_collegato = "";
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
	  posInps = strPosInpsDB;
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
     (!codTipoAzienda.equals("") && !codTipoAzienda.equalsIgnoreCase(codTipoAzDB))  ||
     (!strNumAlboInterinali.equals("") && !strNumAlboInterinali.equals(strNumAlboInterDB))) {
      datiAziendaAggiornati = false;
	} 
	    
	//Visualizzo i dati dell'azienda contenuti sul DB invece di quelli del movimento
	strPartitaIvaAz = pIvaDB;
	strRagioneSocialeAz = ragSocDB;
	strFlgDatiOk = flgDatiOkDB;
	codTipoAzienda = codTipoAzDB;
	descrTipoAzienda = descrTipoAzDB;
	natGiuridicaAz = descrNatGiurDB;  
	codNatGiuridicaAz = codNatGiuridicaDB;
	strNumAlboInterinali = strNumAlboInterDB;
	patInail = strPatInailDB;
	
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
	  posInps = strPosInpsDB;
	}
  }
}//else 
  
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

  if (strFlgDatiOk!=null){
    if (strFlgDatiOk.equalsIgnoreCase("S")){
      strFlgDatiOk = "Si";
    }else
        if (strFlgDatiOk.equalsIgnoreCase("N")){
          strFlgDatiOk = "No";
        }
  }
  
  //Substring delle info troppo lunghe
  String strRagioneSocialeAzTrunc = strRagioneSocialeAz;
  if (strRagioneSocialeAz.length() >= 36){
    strRagioneSocialeAzTrunc = strRagioneSocialeAz.substring(0,34) + "...";
  }
  if (strDesAtecoUAz.length() >= 22){
    strDesAtecoUAz = strDesAtecoUAz.substring(0,19) + "...";
  }
  if (natGiuridicaAz.length() >= 30){
    natGiuridicaAz = natGiuridicaAz.substring(0,26) + "...";
  }
  
  String flgCambiamentiDati = "";
  if (vectRicercaLav.size() > 0) {
    if ((!strCodiceFiscaleLav.toUpperCase().equals(codFiscaleLavDB.toUpperCase())) || 
     (!strNomeLavoratore.toUpperCase().equals(nomeLavDB.toUpperCase())) ||     
     (!strCognomeLav.toUpperCase().equals(cognomeLavDB.toUpperCase())) ||
     (!datNascLav.equals(datNascDB))) {
         
     flgCambiamentiDati = "L";
    }
  }
  
  boolean eseguiRicercaPrecedente = false;

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
  
    String queryString = null;
%>
 
<html>
  <head>
    <%@ include file="../presel/Function_CommonRicercaCCNL.inc" %> 
    <%@ include file="../global/fieldChanged.inc" %>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
        <af:linkScript path="../../js/"/>
    <title>Dettaglio Movimento da Validare</title>
    <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
    var vettCodiceOrario = new Array();
    var vettTipoOrario = new Array();
    var vettCatLavAssObbl = new Array();
    var vettFlg68CatLavAssObbl = new Array();
    var aziendaUtilVar = "N";
    var countSubmit = 0;
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
    var codTipoMovCorr = '<%=codtipomov%>';
    var codTipoMovPrec = '<%=codtipomovprec%>';
    var cdnFunzione = '<%=cdnFunzione%>';
    var inserisci = 'false';
    var contesto = 'valida';
    var rettifica = 'false';
    var valida = 'true';
    var strPageSalto = '<%=strPageSalto%>';
    var consulta = <%=consulta%>;
    var codTipoComunicaz = "<%=codTipoComunicazione%>";
    var configEntePromotore = '<%=configEntePromotore%>';
    var codRegioneCalabria = "<%=Properties.REGIONE_CALABRIA%>";
    </SCRIPT>
    
    <%-- necessari per il controllo del lavoro autonomo --%>
    <script  language="Javascript">
		var codiceFiscaleLav = "<%=strCodiceFiscaleLav %>";
		var codiceFiscaleAz = "<%=strCodiceFiscaleAz %>";	
		var oggettoInSessione = true;
		var controllaProroghe = "<%=controllaProroghe%>";
		
		function controllaDatiSomministrazione() {
			if(controllaProroghe != '') {
				alert("ATTENZIONE: la data fine proroga della MISSIONE è superiore alla data fine dell' AVVIAMENTO. Pertanto sistema considera il movimento come una PROROGA");
				return true;
			}  
		}	
    </script>
    
    <%
	if(rowsComboOrario != null && !rowsComboOrario.isEmpty()) {
    	Vector codiceOrarioDB = new Vector();
    	Vector tipoOrarioDB = new Vector();
    	for (int iOrario=0;iOrario<rowsComboOrario.size();iOrario++) {
    		SourceBean rowOrario = (SourceBean) rowsComboOrario.elementAt(iOrario);
        	String codiceOrarioCurr = (String) rowOrario.getAttribute("codice");
        	String tipoOrarioCurr = (String) rowOrario.getAttribute("tipoOrario");
        	codiceOrarioDB.add(iOrario, codiceOrarioCurr);
        	tipoOrarioDB.add(iOrario, tipoOrarioCurr);        	
    	}
       	%>
       	<script language="Javascript">
       		<%
	    	for (int k=0;k<codiceOrarioDB.size();k++) {
	      		out.print("vettCodiceOrario["+k+"]='"+codiceOrarioDB.get(k).toString()+"';\n");
	      		out.print("vettTipoOrario["+k+"]='"+tipoOrarioDB.get(k).toString()+"';\n");
	      	}
		    %>
		</script>
   	<%}
    
    if(rowsCatAssObbl != null && !rowsCatAssObbl.isEmpty()) {
    	Vector categoriaLavAssDB = new Vector();
    	Vector tipoFlgLegge68DB = new Vector();
    	for (int iCat=0;iCat<rowsCatAssObbl.size();iCat++) {
    		SourceBean rowCat = (SourceBean) rowsCatAssObbl.elementAt(iCat);
        	String codiceCatCurr = (String) rowCat.getAttribute("codice");
        	String tipoFlag68Curr = (String) rowCat.getAttribute("flgLegge68");
        	categoriaLavAssDB.add(iCat, codiceCatCurr);
        	tipoFlgLegge68DB.add(iCat, tipoFlag68Curr);        	
    	}
       	%>
       	<script language="Javascript">
       		<%
	    	for (int k=0;k<categoriaLavAssDB.size();k++) {
	      		out.print("vettCatLavAssObbl["+k+"]='"+categoriaLavAssDB.get(k).toString()+"';\n");
	      		out.print("vettFlg68CatLavAssObbl["+k+"]='"+tipoFlgLegge68DB.get(k).toString()+"';\n");
	      	}
		    %>
		</script>
   	<%}%>
    
    <%-- gestione controllo dati lavoratore (se eta <15, se in mobilita o in collocamento mirato) --%>
    <script type="text/javascript" src="../../js/movimenti/generale/DatiLavoratore.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/func_ValidaDettaglioMovimento.js" language="JavaScript"></script>
    <%@ include file="generale/include/_functionControlloDatiLavoratore.inc" %>
    <%@ include file="generale/include/_referenteLegale.inc" %>
     <!-- Gestione profilatura -->
    <%@ include file="common/include/_gestioneProfili.inc" %>
    
  	<%@ include file="avviamento/include/gestioneAutorizzazione.inc" %> 
    <%@ include file="generale/include/_functionGestPrecNew.inc" %>
    <%@ include file="common/include/calcolaDiffGiorniNew.inc" %>
    
   	<script type="text/javascript" src="../../js/movimenti/generale/func_campiMov.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiRicercaSoggetto.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script>
   	<script type="text/javascript" src="../../js/movimenti/generale/_lavoratoreNew.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/func_generaleNew.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/apriAziendaUtil.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/apriAziendaMovNew.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_confirmDaStOcc.js" language="JavaScript"></script>
	<script type="text/javascript" src="../../js/movimenti/avviamento/gestioneAutorizzazione.js" language="JavaScript"></script>
    <%@ include file="common/include/_funzioniGenerali.inc" %>
  
  </head>
 
  <body class="gestione" onload="caricaPagina();selezionaComboAgevolazioni();">
  <br/><p class="titolo"></p>
  <%@ include file="common/include/GestioneRisultati.inc" %>
 
 <%@ include file="common/include/_commonFuncProfiliNew.inc" %>
      <af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="invia()"> 
  	  <center>
  		<%out.print(htmlStreamTop);%>
        <!-- Parte della protocollazione -->
        <%@ include file="generale/include/_protocollazioneNew.inc" %>
  
  <table class="main" border="0" width="96%" cellpadding="0" cellspacing="0">
  <tr>
    <td>                                        
      <div class='sezione2' id='SedeAzienda'>   
        <img id='tendinaAzienda' alt='Chiudi' src='../../img/chiuso.gif' onclick='cambiaDatiAzienda(this,"datiAzienda_","5")';/>
        Sede Azienda&nbsp;&nbsp;
        <%//Devo inserire l azienda %>
          <a href="#" id="DettaglioInserisciAzienda" style="display:<%=prgAzienda.equals("")?"":"none"%>" onClick="javascript:apriInserisciAziendaNew();"><img src="../../img/add2.gif" alt="nuova azienda" title="Inserisci azienda"></a>
         <%//Devo inserire l unita%>
          <a href="#" id="DettaglioInserisciUnita" style="display:<%=!prgAzienda.equals("") && (prgUnita.equals("") && numUnitaConIndirizzoDiverso == 0)?"":"none"%>" onClick="javascript:apriInserisciUnitaAziendaNew();"><img src="../../img/add2.gif" alt="nuova unità" title="Inserisci unit&agrave; aziendale"></a>  
        <%//Devo scegliere l unita%>
          <a href="#" id="ApriScegliUnita" style="display:<%=(prgUnita.equals("") && numUnitaConIndirizzoDiverso > 0)?"":"none"%>" title="Scegli unita aziendale" onClick="javascript:apriScegliUnitaAziendaNew(document.Frm1.PRGAZIENDA.value, <%=_funzione%>, document.Frm1.PRGMOVIMENTOAPP.value);"><img src="../../img/binocolo.gif" alt="scegli unità tra quelle trovate" title="scegli unit&agrave; tra quelle trovate"></a>      	
        <%//Ho trovato anche l unita%>
          <a href="#" id="DettaglioApriUnita" style="display:<%=(!prgAzienda.equals("") && !prgUnita.equals(""))?"":"none"%>" onClick="javascript:apriUnitaAziendale(document.Frm1.PRGAZIENDA.value,document.Frm1.PRGUNITA.value,<%=_funzione%>,'0');"><img src="../../img/detail.gif" alt="Dettaglio azienda" title="Dettaglio azienda"></a>
		<%//Scelgo Unita tra quelle trovate%>&nbsp;     
		  <a href="#" id="ScegliUnitaTrovate" style="display:<%=(!prgAzienda.equals("") && !prgUnita.equals("") && !(unitaDaSessione || numUnitaConIndirizzoUguale==1))?"":"none"%>" onClick="javascript:apriScegliUnitaAziendaNew(document.Frm1.PRGAZIENDA.value, <%=_funzione%>, document.Frm1.PRGMOVIMENTOAPP.value);"><img src="../../img/binocolo.gif" alt="scegli unità tra quelle trovate" title="Scegli unit&agrave; aziendale"></a>      
        <%//Aggiorna dati azienda%>&nbsp;
	      <a href="#" id="aggiornaAzienda" style="display:<%=!prgAzienda.equals("") && !prgUnita.equals("") && aggiornaAzienda ?"":"none"%>" onClick="javascript:apriAggiornaAziendaNew(document.Frm1.PRGAZIENDA.value,document.Frm1.PRGUNITA.value,<%=_funzione%>,document.Frm1.PRGMOVIMENTOAPP.value);"><img src="../../img/DB_img.gif" title="Aggiorna dati azienda" alt="Aggiorna dati azienda" title="Aggiorna dati azienda"></a>
	      &nbsp;<a href="#" onClick="javascript:apriReferentePermessoSoggiorno();"><img src="../../img/carta_permesso.gif" alt="Legale Rappresentante"></a>
	  </div>         
    </td>        
  </tr>       
    
    <!-- sezione riservata all'azienda che effettua il movimento e eventuale aziennda util. -->
    <%@ include file="generale/include/aziendaNew.inc" %>
    
    <tr>
      <td>          
      	<div class="sezione2" id="lavoratore">
        <img id='tendinaLavoratore' alt='Chiudi' src='../../img/chiuso.gif' onclick='cambiaLav(this,"datiLavoratore","datiLav","<%=cdnLavoratore%>")';/>
        Lavoratore&nbsp;&nbsp;
        <%if (vectRicercaLav.size() == 0) {%>
       		<a href="#" id="AggiungiLavoratoreMovimento" title="Nuovo lavoratore" style="display:''" onClick="javascript:apriInserisciLavoratore();"><img src="../../img/add2.gif" alt="nuovo lavoratore" ></a>
        <%}%>
        <%boolean modificaTitolo = true;%>
        <%@ include file="generale/include/_titoloDiStudio.inc" %>
        <%@ include file="generale/include/_sintesiLavoratore.inc" %>  
        <%@ include file="generale/include/_movimentiLavoratore.inc" %>
        <%@ include file="generale/include/_permessoSoggiorno.inc" %> 
       </div>
      </td>
    </tr>
    <!-- sezione riservata al lavoratore e tipo movimento -->         
    <%@ include file="generale/include/_lavoratoreNew.inc" %>
    <%@ include file="generale/include/_datiGeneraliNew.inc" %>
 
  <%-- // inserito su richiesta di Davide per conto di Carlo il 5/04/2005 da Stefy  --%>
  <input type="hidden" name="CODCPILAV" value="<%=codCpiLav%>"/>	
  
  <input type="hidden" name="RESETCFL" value="false"/>
  
  <!-- GESTIONE APPRENDISTATO E TIROCINIO -->
  <input type="hidden" name="STRCOGNOMETUTORE" value="<%=strCognomeTutore%>" />
  <input type="hidden" name="STRNOMETUTORE" value="<%=strNomeTutore%>" />
  <input type="hidden" name="STRCODICEFISCALETUTORE" value="<%=strCodiceFiscaleTutore%>" />
  <input type="hidden" name="CODQUALIFICASRQ" value="<%=codQualificaSrq%>"/>
  <input type="hidden" name="FLGTITOLARETUTORE" value="<%=flgTitolareTutore%>" />
  <input type="hidden" name="NUMANNIESPTUTORE" value="<%=numAnniEspTutore%>" />
  <input type="hidden" name="STRLIVELLOTUTORE" value="<%=strLivelloTutore%>" />
  <input type="hidden" name="CODMANSIONETUTORE" value="<%=codMansioneTutore%>" />
  <input type="hidden" name="STRNOTE" value="<%=strNote%>" />
  <input type="hidden" name="FLGARTIGIANA" value="<%=flgArtigiana%>" />
  <input type="hidden" name="PROVENIENZA" value="linguetta"/>
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
  <!-- tirocinio -->
  <input type="hidden" name="STRCODFISCPROMOTORETIR" value="<%=strCodFiscPromotoreTir%>" />
  
  <input type="hidden" name="CODCATEGORIATIR" value="<%=codCategoriaTir%>" />
  <input type="hidden" name="CODTIPOLOGIATIR" value="<%=codTipologiaTir%>" />
  <input type="hidden" name="STRDENOMINAZIONETIR" value="<%=strDenominazioneTir%>" />
  <input type="hidden" name="CODSOGGPROMOTOREMIN" value="<%=codSoggPromotoreMin%>" />
  <input type="hidden" name="CODTIPOENTEPROMOTORE" value="<%=codTipoEntePromotore%>" />
  
  <input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>
  <input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
  <input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>
  <input type="hidden" name="DATFINEMOVPREC" value="<%=datFineMovPrec%>"/>
        
  <input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
  <input type="hidden" name="ACTION" value="refresh"/>  
  
  <input type="hidden" name="LAVORATORECOLLMIRATO" value="<%=strLavCollMirato%>"/>
  <input type="hidden" name="FLGAUTORIZZADURATATD" value="<%=autorizzaDurataTD%>"/>
  
  <input type="hidden" name="COLLEGATO" value="<%=collegato%>"/>      
  <input type="hidden" name="PAGE" value="MovEffettuaValidazionePage"/>
  <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
  <input type="hidden" name="FORZA_INSERIMENTO" value="false"/>
  <input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false"/>
  <input type="hidden" name="FLGAUTOCERTIFICAZIONE" value="<%=flgAutocertificazione%>" />
  <input type="hidden" name="FORZA_INSERIMENTO_ETA_APPRENDISTATO" value="<%=forzaInsEtaApprendista%>"/>
  <input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
  <input type="hidden" name="CODMONOPROV" value="<%=codMonoProv%>"/>
  <input type="hidden" name="NUOVODOCUMENTO" value=""/>

  <!-- Sezione di gestione impatti tenendo conto della profilatura -->
  <input type="hidden" name="permettiImpatti" value="" />
  <input type="hidden" name="CANVIEW" value="" />
  <input type="hidden" name="CANEDITLAV" value="" />
  <input type="hidden" name="CANEDITAZ" value="" />
  <input type="hidden" name="CODCOMCOAPP" value="<%=codComunicazione%>" />
  <input type="hidden" name="CODCOMCOAPPPREC" value="<%=codComunicazionePrec%>" />
  
  <input type="hidden" name="flgCambiamentiDati" value="<%=flgCambiamentiDati%>">
  <!--Gestione comunicazioni in ritardo per lavoratori disabili-->
  <input type="hidden" name="LAVORATOREDISABILE" value="<%=strDisabile%>"/>
  	
  <input type="hidden" name="CODCPI" value="<%=codCpi%>"/>
  <input type="hidden" name="codComune" value="<%=codComuneUAz%>"/>
        
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
  <input type="hidden" name="MostraTra" value="MostraTra"/>
  <input type="hidden" name="FLGAZUTILIZESTERA"  value="<%=flgAzUtilizEstera%>" />
  <input type="hidden" name="STRCODICEFISCALE" value="<%=strCodiceFiscaleLav%>" />
  
  <!-- Aggiungo i campi relativi all'azienda precedente nel caso di validazione 
  di una trasformazione con flag trasferimento = "S" -->
  <input type="hidden" name="FLGTRASFER" value="<%=flgTrasferimento%>"/>
  <%if ( codTipoMov.equalsIgnoreCase("TRA") && flgTrasferimento.equalsIgnoreCase("S") ) { %>
	  
	  <input type="hidden" name="STRCODICEFISCALEAZPREC" value="<%=codFiscAzPrec%>"/>
	  <input type="hidden" name="CODCOMAZPREC" value="<%=codComAzPrec%>"/>
	  <input type="hidden" name="STRINDIRIZZOAZPREC" value="<%=indirizAzPrec%>"/>  
	  
  <%}%>
  <input type="hidden" name="strComuneUAzPrec" value="<%=strComuneUAzPrec%>"/>
  <input type="hidden" name="ADD_MOVIMENTO" value="">
  <input type="hidden" name="Differenze" value="">
  <input type="hidden" name="strTestataMovimento"  value="<%=strTestataMovimento%>" />
  <input type="hidden" name="datInizioMovPro"  value="<%=datInizioMovPro%>" />
  <input type="hidden" name="PRGAZIENDATRA"  value="<%=prgAziendaTra%>" />
  <input type="hidden" name="PRGUNITATRA"  value="<%=prgUnitaTra%>" />
  <input type="hidden" name="codComuneUAzTra"  value="<%=codComuneUAzTra%>" />
  <input type="hidden" name="CODREGIONE" value=""/>
  <input type="hidden" name="CONFIGARCHMOV"  value="<%=strArchivia%>" />
  
  </table>
  </center>
	<%out.print(htmlStreamBottom);%>
	<%@ include file="generale/include/campiMovimentiValidazioneNew.inc" %>
	<%if (canModify) {%>  
  		<input title="Valida movimento corrente" type="submit" class="pulsanti" name="submitbutton" value="Valida" id="btnSubmitMov" onclick="resetFlagForzatura()"/>
  	<%}%>
  
  <center>
  	<%@ include file="common/include/GestioneCollegati.inc" %>
  	<%@ include file="common/include/PulsanteRitornoLista.inc" %>  
  </center>
 </af:form>
 
  <br>
  <center>
  <%
  	if (canArchivia) {
  		if (!pageRetListaDaArch.equals("")) {
	  		if (pageRetListaDaArch.equalsIgnoreCase("MOVRISULTVALIDAZIONEPAGE")) {
	  			String prgRitornaRisultatoDaArch = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGRISULTATO");
	  		%>
	  			<form name="frmArchivia" method="POST" action="AdapterHTTP" onSubmit="confermaArchiviazione()">
	  			<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
  				<input type="hidden" name="PRGMOVIMENTOAPP" value="<%=prgMovimentoApp%>"/>
  				<input type="hidden" name="PRGMOVIMENTOAPPCVEARCH" value="<%=prgMovimentoAppCVE%>"/>
  				<input type="hidden" name="OPERAZIONE" value="ARCHIVIA"/>
	  			<input type="hidden" name="PAGE" value="MovRisultValidazionePage"/>
	  			<input type="hidden" name="PAGERISULTVALMASSIVA" value="SAME"/>
	  			<input type="hidden" name="PRGRISULTATO" value="<%=prgRitornaRisultatoDaArch%>"/>
	  			<input type="hidden" name="CDNLAVORATOREARCH" value="<%=cdnLavoratore%>"/>
	  			<input type="hidden" name="PAGERITORNOLISTA" value="<%=pageRetListaDaArch%>"/>
	  			<input type="submit" class="pulsanti" name="submitbuttonArchivia" value="Archivia Movimento"/>
	  			</form>
  			<%}
  			else {
  				if (isRefresh) {
  					%>
  					<form name="frmArchivia" method="POST" action="AdapterHTTP" onSubmit="confermaArchiviazione()">
  		  			<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
  	  				<input type="hidden" name="PRGMOVIMENTOAPP" value="<%=prgMovimentoApp%>"/>
  	  				<input type="hidden" name="PRGMOVIMENTOAPPCVEARCH" value="<%=prgMovimentoAppCVE%>"/>
  		  			<input type="hidden" name="PAGE" value="ArchiviaMovimentiAppoggioPage"/>
  		  			<input type="hidden" name="CDNLAVORATOREARCH" value="<%=cdnLavoratore%>"/>
  		  			<input type="hidden" name="PAGERITORNOLISTA" value="<%=pageRetListaDaArch%>"/>
  		  			<input type="submit" class="pulsanti" name="submitbuttonArchivia" value="Archivia Movimento"/>
  		  			</form>
  				<%}
  				}
	  		}%>
 		<%}
  	%>
  </center>
 
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
   	visualizzaPulsanteApprendistato("<%=codMonoTipo%>", "<%=flgContrattoTI%>");
    
    function caricaPagina(){
    	rinfresca();
    	controllaDatiSomministrazione();
    	<% if (eseguiRicercaPrecedente) { %> gestisciPrecedente(); <% } %>
    	inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');
    	visualizzaInterinali('<%=codTipoAzienda%>', <%=assInterna%>);
    	gestisciRitardo();
    	visualizzaAziUt();
    	controllaInfoLavoratore();
    	gestisciCampi();
    	//abilitazioneConvenzione();
    	abilitazioneConvenzioneOnLoad();
    	campiAgricoltura();
    	//calcolaDiffGiorniNew(document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange,true);
    	getSezioni();
    	gestisciFlagAziEsterna();
    }
   
    function invia(){
    	<% if (codTipoComunicazione.equals("03")) {%>
	    	if (document.Frm1.CODCONTRATTO.value == "<%=Properties.CONTRATTO_LAVORO_INTERMITTENTE%>") {
	    		if (!confirm("Attenzione: in caso di rettifica di un rapporto di lavoro con contratto intermittente le giornate di lavoro dichiarate non comprese nella durata del rapporto saranno cancellate, si desidera proseguire?")) {
			  		return false;
		  		}
	  	  	}
	   	<%}%>
    	if (countSubmit == 0){	
    	  countSubmit++; 
    	 	<% if (codTipoComunicazione.equals("04") || codTipoComunicazione.equals("03")) {%>
    	   		   if (!checkTipoComunicazione(codTipoComunicaz)) {
    	   		     countSubmit = 0;
    	   		     return false;	 
    	   		   }
  			<%} %>
	  		if(selezionaPulsanteApprendistato() && checkObb() && checkDatInizioMovImpatti("<%=datNormativaPrec297%>")
				&& checkDataFineTD() && checkNumOreSettimanali() && controllaConvenzione() && checkAvvAgr() 
				&& checkCampiAziSomm() && ControllaInserimento("<%=codMonoTipo%>") 
				&& controlloLavAutonomo() && gestioneCompetenze() ) {
				return false;
			}
			else{
				countSubmit = 0;
				return false;
			}
		}else {
			return false;
		}
	}
	
	</script>
 </body>
 <%@ include file="common/include/GestioneScriptRisultati.inc" %>
</html>
