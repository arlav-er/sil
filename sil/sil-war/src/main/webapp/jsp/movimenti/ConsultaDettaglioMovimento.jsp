<%@page import="it.eng.sil.module.movimenti.enumeration.CodContrattoEnum"%>
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
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*" %>
 
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%   
  String codInterinale = "INT";
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "MovDettaglioGeneraleConsultaPage");
  //boolean canModify = attributi.containsButton("SALVA");
  boolean canModifyProtocol = attributi.containsButton("SalvaProtocollo");  
  boolean canModifyStato = attributi.containsButton("SalvaProtocollo");
  boolean canInviaMail = attributi.containsButton("INVIA_MAIL");
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  boolean canModify = canModifyProtocol;
  boolean canModifyMov = attributi.containsButton("MODIFICA_MOV");
  
  //Oggetti per l'applicazione dello stile
  String htmlStreamTop = StyleUtils.roundTopTable(!canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTableNoBr(!canModify);
  
  //Oggetti per l'applicazione dello stile
  String htmlStreamTop2 = StyleUtils.roundTopTable(false);
  String htmlStreamBottom2 = StyleUtils.roundBottomTableNoBr(false);

  String currentcontext = "consulta";
  boolean inserisci = false;
  boolean valida = false;  
  boolean rettifica = false;
  boolean isCollegato = false;
  
  boolean daLista = true;
  
  String descrStatoOcc ="";
  String datInizioOcc="";
  String datAnzOcc="";
  
  //Guardo se provengo da una linguetta
  String provenienza = StringUtils.getAttributeStrNotNull(serviceRequest, "PROVENIENZA");
  boolean daLinguetta =  provenienza.equalsIgnoreCase("linguetta");

  //guardo se precedentemente ero in consultazione
  String actionprec = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTIONPREC");
  boolean actionprecaggiorna = actionprec.equalsIgnoreCase("aggiorna");
  boolean actionprecnaviga = actionprec.equalsIgnoreCase("naviga");
  boolean actionprecconsulta = actionprec.equalsIgnoreCase("consulta");

%>
<%@ include file="common/include/_segnalazioniGgRit.inc" %>
<%
  //inizializzazione variabili
  String prgMovimento = "";  
  String prgMovimentoApp = ""; 
  String prgMovimentoPrec = "";
  String prgMovimentoSucc = "";
  String prgMovimentoRett = "";
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
  String codtipomov = "";
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
  String codMvCessazione = "";
  String codMvCessazionePrec = "";
  String sezTitolo = "";
 
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
  String codMonoProv = "";
  String strReferente = "";
  String codMonoTempo = "";
  String datFineMov = "";
  String datFineMovPF = "";
  String codMotAnnullamento = "";
  String codCpi = "";
  String titolo="";
  
  String datFineDistacco="";
  String prgAziendaTra ="";
  String prgUnitaTra ="";
  String strCodiceFiscaleAzTra="";
  String strPartitaIvaAzTra = "";
  String strRagioneSocialeAzTruncTra = "";
  String strIndirizzoUAzTra = "";
  String strComuneUAzTra = "";
  String strCapUAzTra = "";
  String strRagioneSocialeAzTra="";
  String datInizioProt="";
  String valDisplay="none";
  String pageRitornoLista ="";
  
  String codTipoDocEx="";
  String strNumDocEx="";
  String codMotivoPermSoggEx="";
  String datScadenza="";
  String questuraPermSogg = "";
  String flgAlloggio="";
  String flgPagamentoRimpatrio="";
  String flgAzUtilizEstera="";
  String dataFineAffittoRamo="";
  String flgSoggInItalia = "";
  
  String strNotaModifica = "";
  StringBuffer strUrlBack = new StringBuffer();	
  
  
  //campi relativi al dettaglio
  String strMatricola = "";
  String descrTipoAss = "";
  String codMonoTipo = "";
  String flgContrattoTI="";
  String codNormativa = "";
  String codMansione = "";
  String numProtocolloV = "";
  String annoProtV = "" ;
  String strDesAttivita = "";
  String codContratto = "";
  String gestionedecreto150 = "";
  String codCCNL = "";
  String strDescrizioneCCNL = "";
  String decRetribuzioneMen = "";
  String numLivello = "";
  String codAgevolazione = "";
  String cdnUtIns = "";
  String dtmIns = "";
  String codGrado = "";
  String flgArtigiana = "";
  String codMonoStato = "";
  String codNatGiurAz = "";  
  String luogoDiLavoro = "";
  String personaleInterno = "";
  String descrMansione = "";
  String descrTipoMansione = "";
  String strkLockProt = ""; 
  String flgAutocertificazione = "N";
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
  String codTipoEntePromotore = "";
  String codCategoriaTir = "";
  String codTipologiaTir = "";
  String strDenominazioneTir = "";
  String codSoggPromotoreMin = "";
  String strTipoMansioneTutore = "";
  String strNote = "";
  String numMesiApprendistato = "";
  String flgArtigiano = "";
  boolean artigiano = false;
  boolean movIsEnabled = false;
  String codCCNLAzienda = "";
  String codStatoAttoV = "";
  String strCodFiscPromotoreTir = "";

  // agricoltura 
  String numGGPrevistiAgr = "";
  String numGGEffettuatiAgr = "";
  String descQualificaSrq = "";
  String visSezioneSRQ = "";
  // CM 07/02/2007 Savino: convenzioni ed incentivi art. 13
  String numConvenzione = "";
  String datConvenzione = "";
  String datFineSgravio = "";
  String decImportoConcesso = "";
  //int decRetribAnnInt;
  String decRetribuzioneAnn ="";
  String decRetribuzioneAnnProsp = "";
  String prgMovimentoProtDaRett = "";
  
  String codLavorazione = "";
  String socio="";
  String img0 = "../../img/chiuso.gif";
  String img1 = "../../img/aperto.gif";
  String codOrario="";
  String numOreSett="";
  
  String prgMovimentoOld="";
  //Variabili per la gestione della protocollazione
  BigDecimal prgDoc = null;
  String prAutomatica     = null; 
  String estReportDefautl = null;
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String     datProtV     = "";
  String     oraProtV     = "";
  String     docInOut     = "";
  String     docRif       = "";
  BigDecimal kLockProt    = null;
  boolean numProtEditable = false;
  Vector rows             = null;
  SourceBean row          = null;
  Vector vectRicercaAz    = null;
  Vector vectRicercaUnitaAz = null;
  Vector vectRicercaLav   = null;
 
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
  String numGgTraMovComunicaz = "";
  String datInizioMov = "";
  String codTipoTitoloStudio = "";
  boolean hasDocuments =false;
  String flgLegge68="";
  String flgAssObbl="";
  String codCatAssObbl="";
  String flgLavoroAgr="";
  String flgDistParziale="";
  String flagAziEstera="";
  
  boolean isSanato = false;
  String codTipoDich = "";
  String codtipoenteprev = "";
  String strEnte = "";
  String strcodiceenteprev = "";
  String codTipoContratto = "";
  String descrTipoContratto = "";
  String strnumagsomm = "";
  String decindensom = "";
  String datiniziomissione = "";
  String datfinemissione = "";
  String codsoggetto = "";
  String codMvTrasformazione = "";
  
  String datInizioMovTra="";
  String datInizioMovCes="";
  String datFineMovPro="";
  
  String codComunicazione="";
  String codComunicazionePrec="";
  //Decreto 15/11/2011 Azienda somministrazione estera
  String ragSocSommEstera = "";
  String cfSommEstera = "";
  
  //Decreto gennaio 2013
  String flgLavInMobilita = "";
  String flgLavStagionale = "";
  String flgProsecuzione = "";
  String codVariazione = "";
  //fine Decreto gennaio 2013
  
  String decRetribuzioneMenSanata = "";
  String codTipoComunicazione = "";
  
  String missione="N";
  String configEntePromotore = serviceResponse.containsAttribute("M_Config_Ente_Promotore.ROWS.ROW.NUM")?
		  	serviceResponse.getAttribute("M_Config_Ente_Promotore.ROWS.ROW.NUM").toString():"0";
  
  String strEnteRilascio  = "";
  strEnteRilascio = StringUtils.getAttributeStrNotNull(serviceRequest, "STRENTERILASCIO");
  pageRitornoLista = StringUtils.getAttributeStrNotNull(serviceRequest,"PAGERITORNOLISTA");
  prgMovimentoProtDaRett = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOVIMENTOPROTDARETTIFICA");
  SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMov.ROWS.ROW");
  
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  cdnUtins        = StringUtils.getAttributeStrNotNull(dataOrigin,"CDNUTINS");
  dtmins          = StringUtils.getAttributeStrNotNull(dataOrigin,"DTMINS");
  cdnUtmod        = StringUtils.getAttributeStrNotNull(dataOrigin,"CDNUTMOD");
  dtmmod          = StringUtils.getAttributeStrNotNull(dataOrigin,"DTMMOD");
  Testata operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  //fine	
  
  codMvCessazionePrec        = StringUtils.getAttributeStrNotNull(dataOrigin,"CODMVCESSAZIONEPREC");
  
  Vector rowsComboOrario = serviceResponse.getAttributeAsVector("ComboTipoOrario.ROWS.ROW");
  Vector rowsCatAssObbl = serviceResponse.getAttributeAsVector("ComboCategoriaAssObbligatoria.ROWS.ROW");
  
  Vector rowsAgevolazioni = serviceResponse.getAttributeAsVector("M_MovGetDettMovAgevolazioni.ROWS.ROW");
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
  
  //Estraggo i dati della pagina dall'origine tranne nel caso di inserimento senza 
  //precedente con provenienza da lista.
  if (dataOrigin != null) {
  	if (strEnteRilascio.equals("")) {
  		strEnteRilascio = StringUtils.getAttributeStrNotNull(dataOrigin, "strEnteRilascio");
  	}
    //Per il calcolo del ritardo in caso di salvataggio
    //datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov"); 
    
    codStatoAtto = StringUtils.getAttributeStrNotNull(dataOrigin, "CODSTATOATTO");
    codMotAnnullamento = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMOTANNULLAMENTO");
    
    //Progressivi se ci sono
    
    prgMovimento = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTO");
    numKloMov = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMKLOMOV");
    prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
    numKloMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMKLOMOVPREC");  
    prgMovimentoSucc = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOSUCC");
    prgMovimentoApp = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOAPP");
    prgMovimentoRett = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTORETT");
    prgAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDA");
    prgUnita = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITA");
    cdnLavoratore = StringUtils.getAttributeStrNotNull(dataOrigin, "CDNLAVORATORE");
    prgAziendaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDAUTILIZ");
    prgUnitaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITAUTILIZ");
    numContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTNUMCONTRATTO");
    dataInizio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTINIZIOCONTRATTO");
    dataFine = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTFINECONTRATTO");
    legaleRapp = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTRAP");
    numSoggetti = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMAZINTSOGGETTI");
    classeDip = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMAZINTDIPENDENTI");
    //Recupero dettagli documento(Numprotocollo, anno)
    rows = serviceResponse.getAttributeAsVector("DettagliDocumentoDaPrgMov.ROWS.ROW");
    Vector prgdocs = new Vector();
    for(int k = 0; k < rows.size() ; k++){
    	SourceBean sb = (SourceBean)rows.get(k);
    	prgdocs.add(sb.getAttribute("prgdocumento"));
    	
    }
    
    //controllo se ci sono più documenti associati al movimento
    hasDocuments = rows.size()>1;
    
     if((rows != null) && (rows.size()>0))
     {
       row = (SourceBean)rows.get(rows.size()-1);
       prgDoc        = (BigDecimal) row.getAttribute("prgdocumento");
       numProtV      = SourceBeanUtils.getAttrBigDecimal(row, "numProtocollo", null);
       numAnnoProtV  = (BigDecimal) row.getAttribute("numannoprot"); 
       datProtV      = StringUtils.getAttributeStrNotNull(row,"datprot");
       oraProtV      = StringUtils.getAttributeStrNotNull(row,"oraprot");
       docInOut      = StringUtils.getAttributeStrNotNull(row,"codmonoio");
       docRif       = StringUtils.getAttributeStrNotNull(row,"riferimento");
      
     }
     
     //Memorizzo il prgDocumento nella sessione per poi ottenerlo nelle pagine successive
     //bisogna memorizzarlo nella sessione perchè può capitare, come nel caso si trovino movimenti simili, che 
     //venga chiesto all'operatore se desidera forzare l'inserimento oppure no.
     //In tal caso si avrà un ricaricamento della pagina e quindi si perderanno i dati salvati nella request.
     SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
     sessione.setAttribute("PRGDOCUMENTI", prgdocs);
     
   
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
    cfSommEstera = StringUtils.getAttributeStrNotNull(dataOrigin, "CODFISCAZESTERA");
    ragSocSommEstera = StringUtils.getAttributeStrNotNull(dataOrigin, "RAGSOCAZESTERA");
    codTipoEntePromotore = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOENTEPROMOTORE");
    strCodFiscPromotoreTir = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODFISCPROMOTORETIR");
    
    codCategoriaTir = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCATEGORIATIR");
	codTipologiaTir = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOLOGIATIR");
	strDenominazioneTir = StringUtils.getAttributeStrNotNull(dataOrigin,"STRDENOMINAZIONETIR");
	codSoggPromotoreMin = StringUtils.getAttributeStrNotNull(dataOrigin,"CODSOGGPROMOTOREMIN");
	
	if (strDenominazioneTir != null) {
		strDenominazioneTir = JavaScript.escape(strDenominazioneTir);
	}
    
    //Dati azienda utilizzatrice se ci sono
    strPartitaIvaAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strPartitaIvaAzUtil");
    strCodiceFiscaleAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strCodiceFiscaleAzUtil");
    strRagioneSocialeAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAzUtil");
    //Escapizzo l'azienda utilizzatrice
    strRagioneSocialeAzUtil = StringUtils.replace(strRagioneSocialeAzUtil, "\"", "'");
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
    
    descrStatoOcc = StringUtils.getAttributeStrNotNull(dataOrigin, "descrStatoOcc");
    datInizioOcc = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioOcc");
    datAnzOcc = StringUtils.getAttributeStrNotNull(dataOrigin, "datAnzOcc");
    
    codCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNL");
    strDescrizioneCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "strCCNL");
    
    //altri dati della pagina
    
    posInps = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPOSINPS");
    patInail = StringUtils.getAttributeStrNotNull(dataOrigin, "strPatInail");
    flgInterAssPropria = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGINTERASSPROPRIA");
    codtipomov = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOV");
    codTipoMov = codtipomov;
    datComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin, "datComunicaz");
    strLuogoDiLavoro = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLUOGODILAVORO");
    dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");
    dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");        
    datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
    codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE");
    codMonoTipo = StringUtils.getAttributeStrNotNull(dataOrigin,"codMonoTipo");
    flgContrattoTI = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGCONTRATTOTI");
    datFineMovPF = StringUtils.getAttributeStrNotNull(dataOrigin, "datFinePF");
    
    codMonoMovDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOMOVDICH");
    codMonoProv = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOPROV");    
    //Devo riportare il codTipoAss in cima alla pagina e passarlo agli altri dettagli e all'inserimento
    codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss");
	if (codTipoAss.equals("")) descrTipoAss = ""; 
    else descrTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoAss");
	codContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "CODCONTRATTOTIPOASS");
	gestionedecreto150 = StringUtils.getAttributeStrNotNull(dataOrigin, "gestionedecreto150");
    codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo");
    codOrario = StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");
	numOreSett = StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");
	decRetribuzioneMen = StringUtils.getAttributeStrNotNull(dataOrigin, "decRetribuzioneMen"); 
	decRetribuzioneAnn = StringUtils.getAttributeStrNotNull(dataOrigin, "decRetribuzioneAnn"); 
	decRetribuzioneAnnProsp = StringUtils.getAttributeStrNotNull(dataOrigin, "RETRIBUZIONEPROSPETTICA");
	if (!decRetribuzioneAnnProsp.equals("")) {
		decRetribuzioneAnnProsp = decRetribuzioneAnnProsp.replace(',','.');	
	}
	decRetribuzioneMenSanata = StringUtils.getAttributeStrNotNull(dataOrigin, "DECRETRIBUZIONEMENSANATA");
	codTipoDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODICEDICH");
	if(!codTipoDich.equals("")){
		isSanato=true;
	}
	
	if (isSanato) {
		if (!decRetribuzioneMenSanata.equals("")) {
			decRetribuzioneMen = decRetribuzioneMenSanata.replace(',','.');
			Float decRetribuzioneMenFloat = new Float(decRetribuzioneMen);
			float decRetribuzioneAnnFloat = decRetribuzioneMenFloat.floatValue() * 12;
			int decRetribuzioneAnnRound = (int) Math.round(decRetribuzioneAnnFloat);
			decRetribuzioneMen = String.valueOf(decRetribuzioneMenFloat);
			decRetribuzioneAnn = String.valueOf(decRetribuzioneAnnRound);
		} else {
			decRetribuzioneMen = "";
			decRetribuzioneAnn = "";
		}
					
	} else {
		if(decRetribuzioneAnn.equals("")) {
		  	if(!decRetribuzioneMen.equals("")) {
		  		decRetribuzioneMen=decRetribuzioneMen.replace(',','.');
		  		Float decMen = new Float(decRetribuzioneMen);
		  		float decRetribAnnInt = decMen.floatValue() * 12;
		  		int dec = (int) Math.round(decRetribAnnInt);
		  		decRetribuzioneMen = String.valueOf(decMen);
		  		decRetribuzioneAnn = String.valueOf(dec);	
			}
		}
		else {
			if(!decRetribuzioneMen.equals("")) {
				decRetribuzioneMen=decRetribuzioneMen.replace(',','.');
				Float decMen = new Float(decRetribuzioneMen);
				decRetribuzioneMen = String.valueOf(decMen);	
			}
			
		}
	}
	
	
    
	if(!codTipoMov.equals("AVV")) {
    	datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC"); 
    } else {	
  		datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOV");
  		datInizioProt = datInizioMov;
  	} 
  	if(codTipoMov.equals("CES")) {
    	datInizioMovCes = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOV");
    	datInizioProt = datInizioMovCes;
    	codMvCessazione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMvCessazione"); 
    }
    if(codTipoMov.equals("PRO")) {
    	datFineMovPro = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMov");
    	//datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMovEff");
    } else {
    	datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMov");
    }
    if(codTipoMov.equals("TRA")) {
    	datInizioMovTra = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOV");
		datInizioProt = datInizioMovTra;
		codMvTrasformazione = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOTRASF"); 
    }
    
   	codMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMansione");  
	descrMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrMansione");
	numLivello = StringUtils.getAttributeStrNotNull(dataOrigin, "numLivello");     
	codGrado = StringUtils.getAttributeStrNotNull(dataOrigin, "codGrado");
	descrTipoMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoMansione");
	strMatricola = StringUtils.getAttributeStrNotNull(dataOrigin, "strMatricola"); 
	
	autorizzaDurataTD = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGAUTORIZZADURATATD");
	
	socio = StringUtils.getAttributeStrNotNull(dataOrigin, "socio");
    codMonoTempoAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTEMPOAVV");
    codtipomovprec = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOVPREC");
    strReferente = StringUtils.getAttributeStrNotNull(dataOrigin, "STRREFERENTE");
    //Visualizzazione ritardo
    numGgTraMovComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGTRAMOVCOMUNICAZIONE");
    codTipoTitoloStudio = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOTITOLOlav");
    
    strCognomeTutore = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCOGNOMETUTORE");
    
	codTipoDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODICEDICH");
	strDesAttivita = StringUtils.getAttributeStrNotNull(dataOrigin, "strDesAttivita");
	codComunicazione = StringUtils.getAttributeStrNotNull(dataOrigin, "CODCOMUNICAZIONE");
	codComunicazionePrec = StringUtils.getAttributeStrNotNull(dataOrigin, "CODCOMUNICAZIONEPREC");
	codTipoDocEx=StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPODOCEX");
	strNumDocEx=StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMDOCEX");
	codMotivoPermSoggEx=StringUtils.getAttributeStrNotNull(dataOrigin, "CODMOTIVOPERMSOGGEX");
	questuraPermSogg=StringUtils.getAttributeStrNotNull(dataOrigin, "QUESTURALAV");
	datScadenza=StringUtils.getAttributeStrNotNull(dataOrigin, "DATSCADENZALav");
	flgAlloggio=StringUtils.getAttributeStrNotNull(dataOrigin, "FLGSISTEMAZIONEALLOGGIATIVA");
	flgPagamentoRimpatrio=StringUtils.getAttributeStrNotNull(dataOrigin, "FLGPAGAMENTORIMPATRIO");
	flgAzUtilizEstera=StringUtils.getAttributeStrNotNull(dataOrigin, "FLGAZUTILIZESTERA");
    dataFineAffittoRamo=StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEAFFITTORAMO");
    flgSoggInItalia = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGSOGGINITALIA");
    //Decreto gennaio 2013
  	flgLavInMobilita = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLAVOROINMOBILITA");
  	flgLavStagionale = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLAVOROSTAGIONALE");
  	flgProsecuzione = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGPROSECUZIONE");
  	codVariazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODVARIAZIONE");
  	//fine Decreto gennaio 2013
  	
	//recupero i dati relativi all'apprendistato
  	SourceBean app = (SourceBean)serviceResponse.getAttribute("M_MovGetApprendistato.ROWS.ROW");
  	if (app != null) {
	  //strCognomeTutore = StringUtils.getAttributeStrNotNull(app,"STRCOGNOMETUTORE");
	  strNomeTutore = StringUtils.getAttributeStrNotNull(app,"STRNOMETUTORE");
	  strCodiceFiscaleTutore = StringUtils.getAttributeStrNotNull(app,"STRCODICEFISCALETUTORE");
	  flgTitolareTutore = StringUtils.getAttributeStrNotNull(app,"FLGTITOLARETUTORE");
	  if (flgTitolareTutore.equals("S")) titolareTutore = true;
	  numAnniEspTutore = StringUtils.getAttributeStrNotNull(app,"NUMANNIESPTUTORE");
	  strLivelloTutore = StringUtils.getAttributeStrNotNull(app,"STRLIVELLOTUTORE");
	  codMansioneTutore = StringUtils.getAttributeStrNotNull(app,"CODMANSIONETUTORE");
	  strNote = StringUtils.getAttributeStrNotNull(app,"STRNOTE");
	  flgArtigiano = StringUtils.getAttributeStrNotNull(app,"FLGARTIGIANA");
	  if (flgArtigiano.equals("S")) artigiano = true;
	  codQualificaSrq = StringUtils.getAttributeStrNotNull(app, "CODQUALIFICASRQ");
	  descQualificaSrq = StringUtils.getAttributeStrNotNull(app, "descQualificaSrq");
	  strMansioneTutore = StringUtils.getAttributeStrNotNull(app, "STRMANSIONETUTORE");
	  strTipoMansioneTutore = StringUtils.getAttributeStrNotNull(app, "STRTIPOMANSIONETUTORE");
	  flgArtigiana = StringUtils.getAttributeStrNotNull(app,"FLGARTIGIANA");
	  if (flgArtigiana.equals("S")) artigiano = true;
	  //questo campo anche se è del tirocinio si trova nalla tabella am_movimento_apprendistato
	  strCodFiscPromotoreTir = StringUtils.getAttributeStrNotNull(app, "STRCODFISCPROMOTORETIR"); 
  	}
	
	codCpi = StringUtils.getAttributeStrNotNull(dataOrigin, "CODCPI");
	codLavorazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODLAVORAZIONE");
	flgLegge68 = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLEGGE68");
	flgAssObbl = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGASSOBBL");
	codCatAssObbl = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCATASSOBBL");
	
	numGGEffettuatiAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGEFFETTUATIAGR");
	numGGPrevistiAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGPREVISTIAGR");
	flgLavoroAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLAVOROAGR");
	datFineDistacco = StringUtils.getAttributeStrNotNull(dataOrigin,"DATFINEDISTACCO");
	flgDistParziale = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGDISTPARZIALE");
	flagAziEstera = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGDISTAZESTERA");
	
	 if(flgLegge68.equals("") || flgLegge68.equals("N")) {
    	numConvenzione = "";
    	datConvenzione = "";
    	datFineSgravio = "";
    	decImportoConcesso= "";
    } else {
    	numConvenzione = StringUtils.getAttributeStrNotNull(dataOrigin, "numConvenzione");
    	datConvenzione = StringUtils.getAttributeStrNotNull(dataOrigin, "datConvenzione");
    	datFineSgravio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINESGRAVIO");
    	decImportoConcesso = StringUtils.getAttributeStrNotNull(dataOrigin, "DECIMPORTOCONCESSO");
    }
	
/*	
	if(!codTipoDich.equals("")){
		isSanato=true;
	}
*/    
    codtipoenteprev     = StringUtils.getAttributeStrNotNull(dataOrigin,"CODENTE");
    strEnte     = StringUtils.getAttributeStrNotNull(dataOrigin,"STRENTE");
    strcodiceenteprev   = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEENTEPREV");
    codTipoContratto    = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOCONTRATTO");
    descrTipoContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoContratto");
    strnumagsomm        = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNUMAGSOMMINISTRAZIONE");
    decindensom         = StringUtils.getAttributeStrNotNull(dataOrigin,"DECINDENSOM");
    datiniziomissione   = StringUtils.getAttributeStrNotNull(dataOrigin,"DATINIZIORAPLAV");
    datfinemissione     = StringUtils.getAttributeStrNotNull(dataOrigin,"DATFINERAPLAV");
    codsoggetto         = StringUtils.getAttributeStrNotNull(dataOrigin,"CODSOGGETTO");
  }
  
  strNotaModifica = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNOTE");

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
  decMonoProv = "Mov. inserito <strong>Manualmente</strong>";
  } else   if (codMonoProv.equalsIgnoreCase("S")) {
    decMonoProv = "Mov. inserito <strong>Da SARE</strong>";  
  } else   if (codMonoProv.equalsIgnoreCase("F")) {
    decMonoProv = "Mov. importato <strong>Da File</strong>";   
  } else   if (codMonoProv.equalsIgnoreCase("P")) {
    decMonoProv = "Mov. <strong>Da Prolabor</strong>"; 
  } else   if (codMonoProv.equalsIgnoreCase("C")) {
    decMonoProv = "Mov. <strong>Da Cooperazione</strong>"; 
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
  if (strRagioneSocialeAz.length() >= 36){
    strRagioneSocialeAzTrunc = strRagioneSocialeAz.substring(0,34) + "...";
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
  boolean consulta = true;
  boolean precedente = (prgMovimentoPrec.equals("") ? false : true);
  canModify = false;

  String urlBack = "";

  urlBack ="PAGE=MovDettaglioGeneraleConsultaPage" +
	  	       "&PRGMOVIMENTO=" + prgMovimento + 
	  	       "&CDNFUNZIONE=" + _funzione +
	  	       "&PROVENIENZA="+ provenienza +
	  	       "&CURRENTCONTEXT=" + currentcontext +
	  	       "&PAGERITORNOLISTA=" + pageRitornoLista +
	  	       "&MostraTra=MostraTra";
  
  if(serviceRequest.containsAttribute("forzaVal")) {
		String addParameter = "&forzaVal=1";
		urlBack += addParameter;
  }
  
  prgMovimentoOld = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovGetDettMovOld.ROWS.ROW.prgMovimento");
  SourceBean rowCount = null;
  int EsisteMissione = 0;
  rowCount = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovMissioni.ROWS.ROW");
  if(rowCount != null) {
  	EsisteMissione = Integer.valueOf(""+rowCount.getAttribute("missioni")).intValue();
  	if(EsisteMissione > 0) {
  		missione = "S";
  	} else {
		missione = "N";  
  	}
  }
%>

<%
  //controllo se il bottone di invio mail è effettivamente visualizzabile o meno
  boolean showBtnInviaMail = false;
  if(canInviaMail && "PR".equals(codStatoAtto) && ( "AVV".equals(codTipoMov)||  "TRA".equals(codTipoMov))){
	  showBtnInviaMail = true;
  }
  
  //controllo se il bottone Intermittenti è visualizzabile
  boolean isContrattoIntermittente = codContratto.equalsIgnoreCase(Properties.CONTRATTO_LAVORO_INTERMITTENTE);
  if (isContrattoIntermittente){
	  if (!gestionedecreto150.equals("1") || !codTipoMov.equalsIgnoreCase("AVV") || !codStatoAtto.equalsIgnoreCase("PR")) {
		  isContrattoIntermittente = false;
	  }	  
  }
%>
 
<html>
  <head>
   	<%@ include file="../presel/Function_CommonRicercaCCNL.inc" %> 
    <%@ include file="../global/fieldChanged.inc" %>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
        <af:linkScript path="../../js/"/>
    <title>Consulta Dettaglio Movimento Generale</title>
    <%-- ../jsp/movimenti/generale/MovConsultaDettaglioGenerele.jsp --%>
	<%-- include lo script che permette di aprire la PopUp che gestisce i documenti (salvataggio/protocollazione) --%>
	<% String queryString = null; %>
	<%@ include file="../documenti/_apriGestioneDoc.inc"%>
	<script language="Javascript" src="../../js/docAssocia.js"></script>
    <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>    
    var aziendaUtilVar = "N";
    <% if ( (!prgAziendaUtil.equals("")) && (!prgUnitaUtil.equals("")) ){%>
      aziendaUtilVar = "S";
    <%}%>
    
    var codtipomovprec = '<%=codtipomovprec%>';

    <% //Variabili da passare ai file .js %>
    var vettCodiceOrario = new Array();
    var vettTipoOrario = new Array();
    var vettCatLavAssObbl = new Array();
    var vettFlg68CatLavAssObbl = new Array();
    var _funzione = '<%=_funzione%>';
    var canModify = '<%=canModify%>';
    var cdnFunzione = '<%=cdnFunzione%>';
    var codTipoMovCorr = '<%=codTipoMovCorr%>'; 
    var codInterinale = '<%=codInterinale%>';
    var codNatGiuridicaAz = '<%=codNatGiuridicaAz%>';
    var prgAziendaValMov = '<%=prgAziendaValMov%>';
    var codTipoAzienda = '<%=codTipoAzienda%>';
    var strPageSalto = '<%=strPageSalto%>';
    var codTipoMovPrec = '<%=codTipoMovPrec%>';
    var inserisci = 'false';
    var contesto = 'consulta';
    var rettifica = 'false';
    var precedente = <%=(prgMovimentoPrec.equals("") ? "false" : "true")%>;
    var successivo = <%=(prgMovimentoSucc.equals("") ? "false" : "true")%>;
    var isSanato = <%=isSanato%>;
    var oggettoInSessione = true;
    var consulta = <%=consulta%>;
    var configEntePromotore = '<%=configEntePromotore%>';
    var codRegioneCalabria = '<%=Properties.REGIONE_CALABRIA%>';
    
    function stampaMovimento(prgMovimento){
		HTTPrequest = "<%=getQueryString(urlBack)%>";
		apriGestioneDoc('RPT_MOVIMENTI','&TIPOSTAMPA=SINGOLO&prgMovimento='+ prgMovimento,'STMOV');
    }
    function modificaMovimento(){
    	var w=700; 
    	var l=((screen.availWidth)-w)/2;
		var h=800;   
		var t=((screen.availHeight)-h)/2;
  		var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  		var tit = '_blank';
		var url = "AdapterHTTP?PAGE=ModificaMovimentoPage&PRGMOV=" + <%=prgMovimento%> + "&CDNFUNZIONE=" + _funzione + "&PRECEDENTE=" + precedente;
		window.open(url,tit,feat);
	}

	function modificaMovimentoIntermittente(){
		var w=700; 
    	var l=((screen.availWidth)-w)/2;
		var h=800;   
		var t=((screen.availHeight)-h)/2;
  		var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  		var tit = '_blank';
		var url = "AdapterHTTP?PAGE=ModificaMovimentoIntermittentePage&PRGMOV=" + <%=prgMovimento%> + "&CDNFUNZIONE=" + _funzione + "&CDNLAVORATORE=" + <%=cdnLavoratore%>;
		window.open(url,tit,feat);	
	}	
	
	function visualizzaMovimentoOld(){
    var f;
 		f = "AdapterHTTP?PAGE=visualizzaMovimentoOldPage";
 		f = f + "&prgMovimento=<%=prgMovimento%>";
 		f = f + "&prgMovimentoRett=<%=prgMovimentoRett%>";
 		f = f + "&prgMovimentoProtDaRett=<%=prgMovimentoProtDaRett%>";
 		f = f + "&CURRENTCONTEXT=<%=currentcontext%>";
 		f = f + "&CODTIPOMOV=<%=codTipoMov%>";
 		f = f + "&PAGERITORNOLISTA=<%=pageRitornoLista%>"; 
 		f = f + "&CDNFUNZIONE=" + _funzione;
 		setWindowLocation(f);
	}
	
	function visualizzaDatiMissione(){
    	var w=700; 
    	var l=((screen.availWidth)-w)/2;
		var h=800;   
		var t=((screen.availHeight)-h)/2;
  		var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  		var tit = '_blank';
		var url = "AdapterHTTP?PAGE=VisualizzaDatiMissionePage";
 		url = url + "&prgMovimento=<%=prgMovimento%>";
 		url = url + "&STRNOMECOGNOME=<%=strNomeCognomeLav%>";
 		url = url + "&CF=<%=strCodiceFiscaleLav%>";
 		url = url + "&CDNFUNZIONE=" + _funzione;
 		url = url + "&PRECEDENTE=" + precedente;
 		window.open(url,tit,feat);
	}
	
	function apriInvioByMail(prgMovimento){
		<%
		if(showBtnInviaMail){
		%>
	    	var w=700; 
	    	var l=((screen.availWidth)-w)/2;
			var h=800;   
			var t=((screen.availHeight)-h)/2;
	  		var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
	  		var tit = '_blank';
			var url = "AdapterHTTP?PAGE=RichiestaIncentiviLoadMail";
	 		url = url + "&prgMovimento=<%=prgMovimento%>";
	 		
	 		url = url + "&strCognomeLav="+encodeURIComponent("<%=strCognomeLav%>");
	 		url = url + "&strNomeLav="+encodeURIComponent("<%=StringUtils.formatValue4Html(strNomeLav)%>");
	 		url = url + "&strCodiceFiscaleLav="+encodeURIComponent("<%=StringUtils.formatValue4Html(strCodiceFiscaleLav)%>");
	 		url = url + "&strRagioneSocialeAz="+encodeURIComponent("<%=StringUtils.formatValue4Html(strRagioneSocialeAz)%>");
	 		url = url + "&strCodiceFiscaleAz="+encodeURIComponent("<%=StringUtils.formatValue4Html(strCodiceFiscaleAz)%>");
	 		url = url + "&CDNFUNZIONE=" + _funzione;
	 		url = url + "&PRECEDENTE=" + precedente;
	 		window.open(url,tit,feat);
		<%} else {%>
			alert("Il tipo e lo stato del movimento non e' conforme con i requisiti di invio. Il movimento deve essere protocollato e deve essere un avviamento oppure una trasformazione");
		<%}	%>
	}
	
    </SCRIPT>
	
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
   	
	<script type="text/javascript" src="../../js/movimenti/generale/DatiLavoratore.js" language="JavaScript"></script>
    <%-- gestione controllo dati lavoratore (se eta <15, se in mobilita o in collocamento mirato) --%>
    <%@ include file="generale/include/_functionControlloDatiLavoratore.inc" %>
    <%@ include file="generale/include/_referenteLegale.inc" %>
	<%@ include file="common/include/_funzioniGenerali.inc" %>
	<%@ include file="common/include/CollegaSuccessivo.inc" %> 
    <%@ include file="generale/include/_functionGestPrecNew.inc" %>
    <!-- Gestione Profili -->
 	<%@ include file="common/include/_gestioneProfili.inc" %> 
    <%@ include file="avviamento/include/gestioneAutorizzazione.inc" %>
    <script type="text/javascript" src="../../js/movimenti/generale/func_campiMov.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiRicercaSoggetto.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/apriAziendaUtil.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/apriAziendaMovNew.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/_lavoratoreNew.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/func_generaleNew.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_confirmDaStOcc.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_confirmDaControlloMov.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/avviamento/gestioneAutorizzazione.js" language="JavaScript"></script>

  </head>
  <body class="gestione" onload="caricaPagina();">
  <br/><p class="titolo"></p>
    <%@ include file="common/include/GestioneRisultati.inc" %>
    
    <af:showMessages prefix="M_MovSalvaGenConsulta"/>
    <af:showErrors/>
      <af:form name="Frm1" method="POST" action="AdapterHTTP">
      <center>
        <%out.print(htmlStreamTop);%>
        <!-- Parte della protocollazione -->
        <%@ include file="generale/include/_protocollazioneNew.inc" %>
         <table class="main" border="0" width="96%" cellpadding="0" cellspacing="0">
          <tr>
            <td>
            <div class='sezione2' id='SedeAzienda'>  
               <img id='tendinaAzienda' alt='Chiudi' src='<%=img0%>' onclick='cambiaDatiAzienda(this,"datiAzienda_","5")';/>
                Sede Azienda&nbsp;&nbsp;                
                &nbsp;
                <a href="#" onClick="javascript:apriUnitaAziendale(document.Frm1.PRGAZIENDA.value,document.Frm1.PRGUNITA.value,<%=_funzione%>,'0');"><img src="../../img/detail.gif" alt="Dettaglio azienda"></a>
                &nbsp;<a href="#" onClick="javascript:apriReferentePermessoSoggiorno();"><img src="../../img/carta_permesso.gif" alt="Legale Rappresentante"></a>
              </div>
            </td>
          </tr>       
          <!-- sezione riservata all'azienda che effettua il movimento e eventuale aziennda util. -->
          <%@ include file="generale/include/aziendaNew.inc" %>
          <tr>
            <td>          
              <div class='sezione2' id='lavoratore'>
                <img id='tendinaLavoratore' alt='Chiudi' src='<%=img0%>' onclick='cambiaLav(this,"datiLavoratore","datiLav","<%=cdnLavoratore%>")';/>
                Lavoratore&nbsp;&nbsp;
                <%boolean modificaTitolo = false;%>
                <%@ include file="generale/include/_titoloDiStudio.inc" %>
                <%@ include file="generale/include/_sintesiLavoratore.inc" %>  
                <%@ include file="generale/include/_movimentiLavoratore.inc" %>
                <%@ include file="generale/include/_permessoSoggiorno.inc" %>  
             </div>
            </td>
          </tr>
          <!-- sezione riservata al lavoratore -->   
         <%@ include file="generale/include/_lavoratoreNew.inc" %>
         <%@ include file="generale/include/_datiGeneraliNew.inc" %>
        
        <input type="hidden" name="PRGMOVIMENTO" value="<%=prgMovimento%>"/>
        <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
        <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
        <input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=prgAziendaUtil%>"/>
        <input type="hidden" name="PRGUNITAUTILIZ" value="<%=prgUnitaUtil%>"/>
        <input type="hidden" name="NUMKLOMOV" value="<%=numKloMov%>"/>
        <input type="hidden" name="ACTION" value=""/>
        <input type="hidden" name="MostraTra" value="MostraTra"/>
        <input type="hidden" name="PAGE" value="MovDettaglioGeneraleConsultaPage"/>
        <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
        <input type="hidden" name="NUOVODOCUMENTO" value=""/>  
        <input type="hidden" name="STRAZINTNUMCONTRATTO" value="<%=numContratto%>">
        <input type="hidden" name="DATAZINTINIZIOCONTRATTO" value="<%=dataInizio%>">
        <input type="hidden" name="DATAZINTFINECONTRATTO" value="<%=dataFine%>">
        <input type="hidden" name="STRAZINTRAP" value="<%=legaleRapp%>">
        <input type="hidden" name="NUMAZINTSOGGETTI" value="<%=numSoggetti%>">
        <input type="hidden" name="NUMAZINTDIPENDENTI" value="<%=classeDip%>">
        <input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
        <input type="hidden" name="strRagioneSocialeAzUtil" value="<%=strRagioneSocialeAzUtil%>"/>
        <input type="hidden" name="strIndirizzoUAzUtil" value="<%=strIndirizzoUAzUtil%>"/>
        <input type="hidden" name="strComuneUAzUtil" value="<%=strComuneUAzUtil%>"/>
        <input type="hidden" name="strCodiceFiscaleAzUtil" value="<%=strCodiceFiscaleAzUtil%>"/>        
        <input type="hidden" name="FORZA_INSERIMENTO" value="false"/>
        <input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false"/>
        <input type="hidden" name="CODMONOTEMPO" value="<%=codMonoTempo%>"/>   	
        <!-- Data di inizio mov -->
        <input type="hidden" name="DATAINIZIO" value="<%=datInizioMov%>" />
        <input type="hidden" name="TIPOPROTOCOLLO" value="" />
  		<input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>        
        
        <!-- salvataggio dati -->
        <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
        
         <%  if(serviceRequest.containsAttribute("forzaVal")) {%>
           	<input type="hidden" name="forzaVal" value="1"/>
           	<input type="hidden" name="strNotaModifica" value="<%=strNotaModifica%>"/>
        <%}%>
         
  <!-- Sezione di gestione impatti tenendo conto della profilatura -->
  <input type="hidden" name="permettiImpatti" value="" />
  <input type="hidden" name="CANVIEW" value="" />
  <input type="hidden" name="CANEDITLAV" value="" />
  <input type="hidden" name="CANEDITAZ" value="" />
  
  <!-- Sezione documento associato al movimenti -->
  <input type="hidden" name="PRGDOCUMENTO" value="<%=prgDoc%>" />
  <input type="hidden" name="CODCPI" value="<%=codCpi%>" />
  <!-- DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO -->
  <input type="hidden" name="CODENTE"     value="<%=codtipoenteprev%>" />
  <input type="hidden" name="STRNUMAGSOMMINISTRAZIONE"        value="<%=strnumagsomm%>" />
  <input type="hidden" name="DECINDENSOM"     value="<%=decindensom%>" />
  <input type="hidden" name="DATINIZIORAPLAV"   value="<%=datiniziomissione%>" />
  <input type="hidden" name="DATFINERAPLAV"     value="<%=datfinemissione%>" />
  <input type="hidden" name="CODSOGGETTO"         value="<%=codsoggetto%>" />
  
    <!-- GESTIONE APPRENDISTATO -->
  <input type="hidden" name="STRCOGNOMETUTORE" value="<%=strCognomeTutore%>" />
  <input type="hidden" name="STRNOMETUTORE" value="<%=strNomeTutore%>" />
  <input type="hidden" name="DESCQUALIFICASRQ" value="<%=descQualificaSrq%>" />
  <input type="hidden" name="FLGTITOLARETUTORE" value="<%=flgTitolareTutore%>" />
  <input type="hidden" name="NUMANNIESPTUTORE" value="<%=numAnniEspTutore%>" />
  <input type="hidden" name="STRLIVELLOTUTORE" value="<%=strLivelloTutore%>" />
  <input type="hidden" name="CODMANSIONETUTORE" value="<%=codMansioneTutore%>" />
  <input type="hidden" name="STRMANSIONETUTORE" value="<%=strMansioneTutore%>" />
  <input type="hidden" name="STRTIPOMANSIONETUTORE" value="<%=strTipoMansioneTutore%>" />
  <input type="hidden" name="STRNOTE" value="<%=strNote%>" />
  <input type="hidden" name="FLGARTIGIANA" value="<%=flgArtigiana%>" />	
  <!-- tirocinio -->
  <input type="hidden" name="STRCODICEFISCALETUTORE" value="<%=strCodiceFiscaleTutore%>" />
  	
  <input type="hidden" name="CODQUALIFICASRQ" value="<%=codQualificaSrq%>"/>
  <input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>" />
  <input type="hidden" name="NUMMESIAPPRENDISTATO" value="<%=numMesiApprendistato%>" />
  <input type="hidden" name="ADD_MOVIMENTO" value="">
  <input type="hidden" name="Differenze" value="">
  <input type="hidden" name="datInizioProt" value="<%=datInizioProt%>" />
  <input type="hidden" name="strTestataMovimento"  value="<%=strTestataMovimento%>" />
  <input type="hidden" name="decMonoProv"  value="<%=decMonoProv%>" />
  <input type="hidden" name="PRGAZIENDATRA"  value="<%=prgAziendaTra%>" />
  <input type="hidden" name="PRGUNITATRA"  value="<%=prgUnitaTra%>" />
  <input type="hidden" name="PRGMOVIMENTOLD"  value="<%=prgMovimentoOld%>" />
  <input type="hidden" name="MISSIONE"  value="<%=missione%>" />
  <input type="hidden" name="CODTIPODOCEX"  value="<%=codTipoDocEx%>" />
  <input type="hidden" name="CODMOTIVOPERMSOGGEX"  value="<%=codMotivoPermSoggEx%>" />
  <input type="hidden" name="STRNUMDOCEX"  value="<%=strNumDocEx%>" />
  <input type="hidden" name="DATSCADENZA"  value="<%=datScadenza%>" />
  <input type="hidden" name="QUESTURALAV"  value="<%=questuraPermSogg%>" />
  <input type="hidden" name="FLGSISTEMAZIONEALLOGGIATIVA"  value="<%=flgAlloggio%>" />
  <input type="hidden" name="FLGPAGAMENTORIMPATRIO"  value="<%=flgPagamentoRimpatrio%>" />
  <input type="hidden" name="FLGAZUTILIZESTERA"  value="<%=flgAzUtilizEstera%>" />
  <input type="hidden" name="FLGSOGGINITALIA"  value="<%=flgSoggInItalia%>" />
  <input type="hidden" name="STRCODFISCPROMOTORETIR" value="<%=strCodFiscPromotoreTir%>" />
  
  <input type="hidden" name="CODCATEGORIATIR" value="<%=codCategoriaTir%>" />
  <input type="hidden" name="CODTIPOLOGIATIR" value="<%=codTipologiaTir%>" />
  <input type="hidden" name="STRDENOMINAZIONETIR" value="<%=strDenominazioneTir%>" />
  <input type="hidden" name="CODSOGGPROMOTOREMIN" value="<%=codSoggPromotoreMin%>" />
  <input type="hidden" name="CODTIPOENTEPROMOTORE" value="<%=codTipoEntePromotore%>" />
  
  </table>
  </center>
	<%out.print(htmlStreamBottom);%>
	
	<%@ include file="generale/include/campiMovimentiNew.inc" %>
  
    <table>
    <tr>
    	<td align="center" colspan="1"><%operatoreInfo.showHTML(out);%></td>
    </tr>
    <%//Pulsanti per la gestione della navigazione dei movimenti rettificati
		if (!prgMovimentoProtDaRett.equals("") || !prgMovimentoRett.equals("")) {%>
		<tr>
			<td align="center">
	          	<%//Consultazione di un movimento rettificato
	          	if (!prgMovimentoRett.equals("")) {%>
					<input type="button" class="pulsante" name="consulta" value="Consulta rettificato" onClick="javascript:consultaRettificato('<%=prgMovimentoRett%>');"/>
	          	<%} 
	          	//Ritorno al movimento protocollato da uno rettificato
				if (!prgMovimentoProtDaRett.equals("") && !prgMovimentoProtDaRett.equals(prgMovimento)) {%>&nbsp;
	          		<input type="button" class="pulsante" name="consulta" value="Torna al protocollato" onClick="javascript:consultaProtocollatoDaRettifica('<%=prgMovimentoProtDaRett%>');"/>
	          		<input type="hidden" name="PRGMOVIMENTOPROTDARETTIFICA" value="<%=prgMovimentoProtDaRett%>"/>       
				<%}%>
			</td>
		</tr> 
       <%}%>   
         <tr>
         	<td align="center">
            	<input type="button" class="pulsante" name="Stampa" value="Stampa" onClick="stampaMovimento(<%=prgMovimento%>)"/>
              	<%if(canModifyMov ){%>
              		<input type="button" class="pulsante" name="Modifica" value="Modifica" onClick="modificaMovimento();"/>
              		<%if (isContrattoIntermittente){%>
              			<input type="button" class="pulsante" name="btnIntermittenti" value="Giornate lavorate" onClick="modificaMovimentoIntermittente();"/>
              		<%}%>
              	<%} if(!prgMovimentoOld.equals("")){%>
	          		<input type="button" , class="pulsante"
					name="Dettaglio movimento salvato"
					value="Dettaglio movimento salvato"
					onclick="visualizzaMovimentoOld(<%=prgMovimento%>)" /> <%} if(EsisteMissione > 0){%>
	          		<input type="button", class="pulsante" name="Missioni" value="Missioni" onclick="visualizzaDatiMissione()"/>&nbsp;&nbsp;&nbsp;&nbsp;	
          		<%}%>
          	</td>
          </tr>
          <%if(hasDocuments){%>
          <tr>
          	<td align="right">          	
          		<input type="button", class="pulsante" name="docuAssociati" value="Documenti associati" onclick="docAssociati(<%=cdnLavoratore%>,'MovDettaglioGeneraleConsultaPage',<%=_funzione%>,'',<%=prgMovimento%>)"/>&nbsp;&nbsp;&nbsp;&nbsp;	
          	</td>
          </tr>	
         <%}%>
          <%if(canInviaMail){%>
          <tr>
          	<td align="center">          	
          		<input type="button" , class="pulsante"
					name="InvioMovByMail" value="Invio dati per domanda incentivi"
					onclick="apriInvioByMail(<%=prgMovimento%>)" />&nbsp;&nbsp;&nbsp;&nbsp;	
          	</td>
          </tr>	
         <%}%>
    </table>
 	<center>
  		<%@ include file="common/include/GestioneCollegati.inc" %>
  </center> 
	
	<%  if(serviceRequest.containsAttribute("forzaVal")) {%>
    	<center>
			<%out.print(htmlStreamTop2);%>
				<%@ include file="generale/include/_NotaModifiche.inc" %>
			<%out.print(htmlStreamBottom2);%>
		</center>
	<%}%> 
		<center>
			<%@ include file="common/include/PulsanteRitornoLista.inc" %>
		</center> 
	</af:form>
	
	
	<script language="javascript">
    
    if (aziendaUtilVar != "N"){
      document.Frm1.STRLUOGODILAVORO.readOnly = true;
    }
    
    visualizzaPulsanteApprendistato("<%=codMonoTipo%>", "<%=flgContrattoTI%>");
   
   function caricaPagina(){
	 	inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');
		rinfresca();
		visualizzaInterinali('<%=codTipoAzienda%>', <%=assInterna%>);
		gestioneProfilo(document.Frm1.CODTIPOMOV);
		visualizzaAziUtConsulta();
		controllaInfoLavoratore();
		gestisciCampi();
		getSezioni();
		campiAgricoltura();
	}
	
   </script>
 </body>
 <%@ include file="common/include/GestioneScriptRisultati.inc" %>
</html>
