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
                  it.eng.sil.util.amministrazione.impatti.EventoAmministrativo,
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
 	if (serviceResponse.containsAttribute("M_MovInserisciMovimentoCompleto.PRGMOVIMENTO")) {
    	canModify = false;
  	}
  	//Oggetti per l'applicazione dello stile
  	String htmlStreamTop    = StyleUtils.roundTopTable(canModify);
    String htmlStreamBottom = StyleUtils.roundBottomTableNoBr(canModify);
    
    //Guardo il contesto in cui opero
  	String currentcontext = "inserisci";
  	boolean inserisci = true;
  	boolean salva = false;
  	boolean valida = false; 
  	boolean rettifica = false;
  	boolean sospeso = false;
  	boolean isCollegato = false;
  	
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
  	
  	String forzaInsEtaApprendista = StringUtils.getAttributeStrNotNull(serviceRequest, "FORZA_INSERIMENTO_ETA_APPRENDISTATO");
  	if (forzaInsEtaApprendista.equals("")) forzaInsEtaApprendista = "false";
  
  	%>
  	<%@ include file="GestioneOggettoMovimento.inc" %> 
  	<%@ include file="common/include/_segnalazioniGgRit.inc" %>
	
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
  	String codMonoMovDich = "O";
  	String strFlgCfOk = "";
  	String strFlgDatiOk = "";
  	String codStatoAtto = "";
  	String codMotAnnullamento = "";
  	String strReferente = "";
  	String codCpi = ""; 
  	String codCpiLav = "";
  	String codMonoTempo = "";
  	String datFineMov = "";
  	String datFineMovPF = "";
  	String codMvCessazione = "";
  	String codMvCessazionePrec = "";
  	String datInizioAVVperCVE = "";
  	String strnumagsomm = "";
  	String codMvTrasformazione = "";
  	
	String descrStatoOcc ="";
    String datInizioOcc="";
    String datAnzOcc="";
    
    String datInizioMovTra="";
  	String datInizioMovCes="";
  	String datInizioMovPro="";
  	String datFineMovPro="";
    
    String numAziSomm="";
    String titolo="";
    String sezTitolo="";
    
    String prgAziendaTra ="";
    String prgUnitaTra ="";
    String strCodiceFiscaleAzTra="";
    String strPartitaIvaAzTra = "";
	String strRagioneSocialeAzTruncTra = "";
	String strIndirizzoUAzTra = "";
	String strComuneUAzTra = "";
	String strCapUAzTra = "";
	String strRagioneSocialeAzTra="";

	//Di supporto per il ritardo
  	String numGgTraMovComunicaz = "";
  	//Per omogeneità per le info della testata del mov.
  	String datInizioMov = "";
  	String datInizioProt = "";
  	//Giovanni D'Auria 21/02/2005 inizio
  	String codOrario="";
    String numOreSett="";
  	//fine
  	
  	//Decreto gennaio 2013
  	String flgLavInMobilita = "";
  	String flgLavStagionale = "";
  	String flgProsecuzione = "";
  	String codVariazione = "";
  	//fine Decreto gennaio 2013
  	
  	int num = 6;
	
	//campi relativi al dettaglio
  	String strMatricola = "";
  	String descrTipoAss = "";
  	String codMonoTipo = "";
  	String flgContrattoTI="";
  	String codNormativa = "";
  	String codMansione = "";
  	String numProtocolloV = "";
  	String annoProtV = "" ;
  	BigDecimal prgDoc = null;
  	String strDesAttivita = "";
  	String codContratto = "";
  	String flgSocio = "";
  	String codCCNL = "";
  	String strDescrizioneCCNL = "";
  	String decRetribuzioneMen = "";
  	String decRetribuzioneAnn = "";
  	String decRetribuzioneAnnProsp = "";
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
  	//tirocinio
  	String strCodFiscPromotoreTir = "";
  	//boolean movIsEnabled = true;
  	String codCCNLAzienda = "";
  	String codStatoAttoV = "";
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
  	String flgLegge68="";
  	String flgAssObbl="";
    String codCatAssObbl="";
  	String flgLavoroAgr="";
  	String flgDistParziale="";
  	String flagAziEstera="";
  	String datFineDistacco="";
  	String codComunicazione="";
  	String codTipoComunicazione="";
    String codComunicazionePrec="";
    String img0 = "../../img/chiuso.gif";
  	String img1 = "../../img/aperto.gif";
  	//Decreto 15/11/2011 Azienda somministrazione estera
    String ragSocSommEstera = "";
    String cfSommEstera = "";
    String modificataAz ="";
    
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
  	
  	// recupero informazioni protocollazione
  	numProtV = SourceBeanUtils.getAttrBigDecimal(serviceRequest, "numProtocollo", null);
  	
  	
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
  	//================================================================
 
  	codMvCessazionePrec = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMVCESSAZIONEPREC");
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
    String decMonoProv = "Mov. inserito <strong>Manualmente</strong>";
  	String valDisplay="none";
    String codTipoTitoloStudio = "";
    String socio="";
	
	String codCategoria   = "";
  	String codLavorazione = "";

  	String strEnteRilascio  = "";
  	strEnteRilascio = StringUtils.getAttributeStrNotNull(serviceRequest, "STRENTERILASCIO");
  	String strDisabile = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATOREDISABILE");
  	
  	String lavCollMirato = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATORECOLLMIRATO");
   	//DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
  	String codTipoDocEx = "";
  	String strNumDocEx = "";
  	String codMotivoPermSoggEx = "";
  	String questuraPermSogg = "";
  	String codtipoenteprev = "";
  	String strEnte = "";
  	String strcodiceenteprev = "";
  	String datScadenza="";
  	String flgAlloggio="";
    String flgPagamentoRimpatrio="";
    String flgAzUtilizEstera="";
    String dataFineAffittoRamo="";
    String flgSoggInItalia = "";
  	
  	String datiniziomissione = "";
  	String datfinemissione = "";
  	
  	String codsoggetto = "";
  	String flgTrasferimento = "";
  	String configEntePromotore = serviceResponse.containsAttribute("M_Config_Ente_Promotore.ROWS.ROW.NUM")?
		  	serviceResponse.getAttribute("M_Config_Ente_Promotore.ROWS.ROW.NUM").toString():"0";
  	String datNormativaPrec297 = "";
	SourceBean sbConfigDataNormativa = (SourceBean) serviceResponse.getAttribute("M_CONFIG_DATA_NORMATIVA_297.rows.row");
	if (sbConfigDataNormativa != null && sbConfigDataNormativa.containsAttribute("strvalore")) {
		datNormativaPrec297 = sbConfigDataNormativa.getAttribute("strvalore").toString();
	}
	else {
		datNormativaPrec297 = EventoAmministrativo.DATA_NORMATIVA_DEFAULT;
	}
  	Vector rowsComboOrario = serviceResponse.getAttributeAsVector("ComboTipoOrario.ROWS.ROW");
  	Vector rowsCatAssObbl = serviceResponse.getAttributeAsVector("ComboCategoriaAssObbligatoria.ROWS.ROW");
  	
  	//Prendo le Agevolazioni dall'eventuale movimento precedente
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
 
  	//Numero protocollo da applicare se in inserimento nuovo
  	rows = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
  	if(rows != null && !rows.isEmpty()) { 
  		row = (SourceBean) rows.elementAt(0);
    	prAutomatica     = (String) row.getAttribute("FLGPROTOCOLLOAUT");
    	if ( prAutomatica.equalsIgnoreCase("N") ){ numProtEditable = true; }
    	estReportDefautl = (String) row.getAttribute("CODTIPOFILEESTREPORT");
    	numProtV         = SourceBeanUtils.getAttrBigDecimal(row, "NUMPROTOCOLLO", null);
    	numAnnoProtV     = (BigDecimal) row.getAttribute("NUMANNOPROT");
    	kLockProt        = (BigDecimal) row.getAttribute("NUMKLOPROTOCOLLO");
  	}
  	
  	//boolean readOnlyDatFine = !canModify || codTipoMov.equals("TRA");
  
  	//Setto l'origine dei dati generali da recuperare in caso di inserimento o validazione, 
  	//mi arrivano dall'oggetto in sessione se sto inserendo e l'oggetto è abilitato,
  	//altrimenti dalla request
  	SourceBean dataOrigin = serviceRequest;
  	codCategoria   = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCATEGORIA");
    
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
  	if (serviceResponse.containsAttribute("M_MovInserisciMovimentoCompleto")) {
 		codMonoMovDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOMOVDICH");
 	}
  	if ( (insertCollegato && daLista) || (serviceResponse.containsAttribute("M_MovInserisciMovimentoCompleto.PRGMOVIMENTO")) ) { 
  		dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMov.ROWS.ROW");
  	}
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
		
		strRagioneSocialeAzTruncTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAzTruncTra");
		strIndirizzoUAzTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAzTra");
		strComuneUAzTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAzTra");
		strCapUAzTra=StringUtils.getAttributeStrNotNull(dataOrigin, "strCapUAzTra");
		
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
	    modificataAz = StringUtils.getAttributeStrNotNull(dataOrigin, "MODIFICATAAZIENDA");
	 	codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo");
	    strLuogoDiLavoro = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLUOGODILAVORO");
	    dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");
	    dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");    
	    datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
	    codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE");
	    codMonoTipo = StringUtils.getAttributeStrNotNull(dataOrigin,"codMonoTipo");
	    flgContrattoTI = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGCONTRATTOTI");
	    datFineMovPF = StringUtils.getAttributeStrNotNull(dataOrigin, "datFinePF");
	    strMatricola = StringUtils.getAttributeStrNotNull(dataOrigin, "strMatricola");
	    // numAziSomm = StringUtils.getAttributeStrNotNull(dataOrigin, "DECINDENSOM");
	    codtipoenteprev = StringUtils.getAttributeStrNotNull(dataOrigin,"CODENTE");
	    strEnte     = StringUtils.getAttributeStrNotNull(dataOrigin,"STRENTE");
	    strcodiceenteprev = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEENTEPREV");
	    //Devo riportare il codTipoAss in cima alla pagina e passarlo agli altri dettagli e all'inserimento
	    codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss");
	    if (codTipoAss.equals("")) descrTipoAss = ""; 
    	else descrTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoAss"); 
	    codMonoTempoAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTEMPOAVV");
	    codtipomovprec = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOVPREC");
	    strReferente = StringUtils.getAttributeStrNotNull(dataOrigin, "STRREFERENTE"); 
	    codTipoTitoloStudio = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOTITOLOlav");
	    
	    // apprendistato
	    strCognomeTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "STRCOGNOMETUTORE");
  		strNomeTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNOMETUTORE");
  		strCodiceFiscaleTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "STRCODICEFISCALETUTORE");
	    
	 	codQualificaSrq = StringUtils.getAttributeStrNotNull(dataOrigin, "CODQUALIFICASRQ");
		descQualificaSrq = StringUtils.getAttributeStrNotNull(dataOrigin, "descQualificaSrq");
		codTipoEntePromotore = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOENTEPROMOTORE");
		
		strCodFiscPromotoreTir = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODFISCPROMOTORETIR");
		codCategoriaTir = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCATEGORIATIR");
		codTipologiaTir = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOLOGIATIR");
		strDenominazioneTir = StringUtils.getAttributeStrNotNull(dataOrigin,"STRDENOMINAZIONETIR");
		codSoggPromotoreMin = StringUtils.getAttributeStrNotNull(dataOrigin,"CODSOGGPROMOTOREMIN");
		
		if (strDenominazioneTir != null) {
			strDenominazioneTir = JavaScript.escape(strDenominazioneTir);
		}
		
		flgTitolareTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGTITOLARETUTORE");
		numAnniEspTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMANNIESPTUTORE");
		strLivelloTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLIVELLOTUTORE");
		codMansioneTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMANSIONETUTORE");
		strMansioneTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "STRMANSIONETUTORE");
		strTipoMansioneTutore = StringUtils.getAttributeStrNotNull(dataOrigin, "STRTIPOMANSIONETUTORE");
		flgArtigiana = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGARTIGIANA");
		strNote = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNOTE");
		//fine
  		
  		//codNormativa = StringUtils.getAttributeStrNotNull(dataOrigin, "codNormativa");   
	    strDesAttivita = StringUtils.getAttributeStrNotNull(dataOrigin, "strDesAttivita");   
	    codContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "codContratto");   
	    flgSocio = StringUtils.getAttributeStrNotNull(dataOrigin, "flgSocio");   
	    codCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNL");   
	    
	    descrStatoOcc = StringUtils.getAttributeStrNotNull(dataOrigin, "descrStatoOcc");
	    datInizioOcc = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioOcc");
	    datAnzOcc = StringUtils.getAttributeStrNotNull(dataOrigin, "datAnzOcc");
	    
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
	    
		if(codTipoMov.equals("CES")) {
			codLavorazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODLAVORAZIONECES");
			flgLavoroAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLAVOROAGRCES");
			if(codLavorazione.equals("")) {
				codLavorazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODLAVORAZIONE");
			}
		} else {
			codLavorazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODLAVORAZIONE");
			flgLavoroAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLAVOROAGR");
		}
	    
		flgDistParziale = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGDISTPARZIALE");
		datiniziomissione = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIORAPLAV");
	    datfinemissione = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINERAPLAV");
	    flagAziEstera = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGDISTAZESTERA");
	    strDescrizioneCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "strCCNL");   
	    decRetribuzioneMen = StringUtils.getAttributeStrNotNull(dataOrigin, "decRetribuzioneMen"); 
    	if(!decRetribuzioneMen.equals("")) {
    		decRetribuzioneMen=decRetribuzioneMen.replace(',','.');
      		Float decMen = new Float(decRetribuzioneMen);
      		float decRetribAnnInt = decMen.floatValue() * 12;
      		//int dec = (int) decRetribAnnInt;
      		int dec = (int) Math.round(decRetribAnnInt);
      		decRetribuzioneMen = String.valueOf(decMen);
    		decRetribuzioneAnn = String.valueOf(dec);	
 		} 
    	
        if(codTipoMov.equals("AVV") || (codTipoMov.equals("CES") && collegato.equalsIgnoreCase("nessuno")) ) {
   	  		datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOV");
   	  		datInizioProt = datInizioMov;
   	    } else {	
     		datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");
        } if(codTipoMov.equals("CES")) {
    		datInizioMovCes = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMovCes");
    		if(datInizioMovCes.equals("")) {
    			datInizioMovCes = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOV");
    		}
    		datInizioProt = datInizioMovCes;
    		codMvCessazione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMvCessazione"); 
        }
        if(codTipoMov.equals("PRO")) {
      		datFineMovPro = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMovPro");
    	    //datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMovEff");
   	    } 
        if(codTipoMov.equals("TRA")) {
      		datInizioMovTra = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMovTra");
  			if(datInizioMovTra.equals("")) {
  				datInizioMovTra = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOV");
  			}
    		datInizioProt = datInizioMovTra;
    		codMvTrasformazione = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOTRASF");
      }
	  codMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMansione");
      descrMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrMansione"); 
	  descrTipoMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoMansione");
      if(descrMansione.equals("")) {   
			descrMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "DESCMANSIONE"); 
			descrTipoMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "strTipoMansione");
    	}
      
      if(codTipoMov.equals("CES")) {
      	numLivello = StringUtils.getAttributeStrNotNull(dataOrigin, "numLivelloCes");
      } 
      if(numLivello.equals("")) {
    	  numLivello = StringUtils.getAttributeStrNotNull(dataOrigin, "numLivello");
  	  }
	         
	    codGrado = StringUtils.getAttributeStrNotNull(dataOrigin, "codGrado");
		codOrario = StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");
		numOreSett = StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");
		socio = StringUtils.getAttributeStrNotNull(dataOrigin, "socio");
	    
	    flgArtigiana = StringUtils.getAttributeStrNotNull(dataOrigin, "flgArtigiana");   
	    codMonoStato = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoStato");  
	    numGgTraMovComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGTRAMOVCOMUNICAZIONE");
	    numGGPrevistiAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGPREVISTIAGR");
	    datInizioAVVperCVE = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVVCEV");
	    numGGEffettuatiAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGPREVISTIAGR");
	    
	    //Autorizzazione alla durata dei movimenti a TD data dall'utente
	    autorizzaDurataTD = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGAUTORIZZADURATATD");
	    flgLegge68 = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLEGGE68");
	    flgAssObbl = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGASSOBBL");
		codCatAssObbl = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCATASSOBBL");
	    numConvenzione = StringUtils.getAttributeStrNotNull(dataOrigin, "numConvenzione");
	    datConvenzione = StringUtils.getAttributeStrNotNull(dataOrigin, "datConvenzione");
	    datFineDistacco = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEDISTACCO");
	    datFineSgravio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINESGRAVIO");
	    decImportoConcesso = StringUtils.getAttributeStrNotNull(dataOrigin, "DECIMPORTOCONCESSO");
	    
		//Decreto gennaio 2013
	  	flgLavInMobilita = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLAVOROINMOBILITA");
	  	flgLavStagionale = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLAVOROSTAGIONALE");
	  	flgProsecuzione = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGPROSECUZIONE");
	  	//il codice variazione va sbiancato
	  	codVariazione = "";
	  	//fine Decreto gennaio 2013
	  	
	}//fine if inserimento
  	
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
	
	//recupero i dati relativi all'apprendistato
  	if(strCognomeTutore.equals("")) {
  		SourceBean app = (SourceBean)serviceResponse.getAttribute("M_MovGetApprendistato.ROWS.ROW");
  		if (app != null) {
	  		strCognomeTutore = StringUtils.getAttributeStrNotNull(app,"STRCOGNOMETUTORE");
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
  		}
  	} 
	
  	SourceBean app = (SourceBean)serviceResponse.getAttribute("M_MovGetApprendistato.ROWS.ROW");
	if (app != null) {
	  	strCodFiscPromotoreTir = StringUtils.getAttributeStrNotNull(app, "STRCODFISCPROMOTORETIR");  
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
      	ragSocSommEstera = datiAz.getRagSocSommEstera();
        cfSommEstera = datiAz.getCFSommEstera();
      	valDisplay="";
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
      	descrStatoOcc = datiLav.getStatoOcc(); 
      	datInizioOcc = datiLav.getDatInizioOcc();
      	datAnzOcc = datiLav.getDatAnzOcc();
	}
 
  	//se sto eseguendo un inserimento collegato devo fare qualche elaborazione
  	if (insertCollegato && daLista && dataOrigin != null) {
    
    	//Se non arrivo da una linguetta i dati che ho estratto sono quelli del movimento precedente,
    	//devo aggiustare alcuni campi
  //  	if (!daLinguetta) {
      		//Il codTipoMov estratto è quello del precedente
      		codtipomovprec = codTipoMov;
      		codTipoMov = "";
      		numGgTraMovComunicaz = "";
      		//anche il codMonoTempo è del precedente
      		codMonoTempoAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo");
      		//anche il progressivo e il klomov sono del precedente
      		prgMovimentoPrec = prgMovimento;
      		numKloMovPrec = numKloMov;
      		//datInizioMov = "";
    }
 // 	}
  	
  	//Se è la prima volta che visualizzo la pagina devo prevalorizzare alcuni campi
  	//if (!mov.isEnabled() && (dataOrigin != null)) {
    //	codCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNLAz");
    //	strDescrizioneCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "descrCCNLAz");
	//	numMesiApprendistato = serviceResponse.containsAttribute("M_MovGetDettMovApp.ROWS.ROW")?serviceResponse.getAttribute("M_MovGetNumDurataApprendist.ROWS.ROW.NUMMESIAPPRENDISTATO").toString():"";
	//}
 	
 	
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
  	if(collegato.equals("")) {
  		collegato = "nessuno";
  	}
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
    	}else {
        	if (strFlgDatiOk.equalsIgnoreCase("N")){
          		strFlgDatiOk = "No";
        	}
        }
  	}
 
  	if (strFlgCfOk!=null){
      	if (strFlgCfOk.equalsIgnoreCase("S")){
        	strFlgCfOk = "Si";
      	}else {
       		if (strFlgCfOk.equalsIgnoreCase("N")){
            	strFlgCfOk = "No";
          	}
     	}     
  	}    
  	//Substring delle info troppo lunghe
 	String strRagioneSocialeAzTrunc = strRagioneSocialeAz;
  	if (strRagioneSocialeAz.length() > 36){
    	strRagioneSocialeAzTrunc = strRagioneSocialeAz.substring(0,34) + "...";
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
  	
  	//abilito l'oggetto se non era già abilitato e non sono in consultazione
 	//if (!mov.isEnabled()) {
    //	mov.enable();
  	//}
  	
  	 //boolean mostraCampoInizioAvviamento = !insertCollegato;
  	 boolean mostraCampoInizioAvviamento = false;
     String strOnSubmit="";
%>
 
<html>
  <head>
  	<%@ include file="../presel/Function_CommonRicercaCCNL.inc" %>  
    <%@ include file="DynamicRefreshCombo.inc" %>
    <%@ include file="../global/fieldChanged.inc" %>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    <title>Dettaglio Movimento</title>
    <script language="Javascript">
    var vettCodiceOrario = new Array();
    var vettTipoOrario = new Array();
    var vettCatLavAssObbl = new Array();
    var vettFlg68CatLavAssObbl = new Array();
    var countSubmit = 0;
    var aziendaUtilVar = "N";
    <% if ( (!prgAziendaUtil.equals("")) && (!prgUnitaUtil.equals("")) ){%>
      aziendaUtilVar = "S";
    <%}%>
    
    <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,
                                 "cdnLavoratore="+cdnLavoratore+
                                 "&prgMovimento="+prgMovimento+
                                 "&PAGERITORNOLISTA="+StringUtils.getAttributeStrNotNull(serviceRequest, "PAGERITORNOLISTA"));
      %>
 
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
    var rettifica = 'false';
    var precedente = <%=(prgMovimentoPrec.equals("") ? "false" : "true")%>;
    var successivo = <%=(prgMovimentoSucc.equals("") ? "false" : "true")%>;
    var prgAziendaS = '<%=prgAzienda%>';
    var prgUnitaS = '<%=prgUnita%>';
    var consulta = <%=consulta%>;
    var provenienza = '<%=provenienza%>';
    
    var finestraAperta;
    var contesto = 'inserisci';
    var sospesoJS = <%=sospeso%>;
    var codiceFiscaleLav = "<%=strCodiceFiscaleLav %>";
	var codiceFiscaleAz = "<%=strCodiceFiscaleAz %>";
	var oggettoInSessione = true;
	var configEntePromotore = '<%=configEntePromotore%>';
	var codRegioneCalabria = "<%=Properties.REGIONE_CALABRIA%>";
	
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

    //Notifica del cambio di stato occupazionale
    function notificaCambioStatoOcc() {
		if(document.Frm1.CODTIPOMOV.value=="CES") {
			<% if (StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovInserisciCessazione.CAMBIOSTATOOCC").equals("TRUE")) {%>
    			alert("Lo stato occupazionale del lavoratore è cambiato.");
			<%}%>
    	} else {
    		if(document.Frm1.CODTIPOMOV.value=="PRO" || document.Frm1.CODTIPOMOV.value=="TRA") {
    			<% if (StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovInserisciTrasfPro.CAMBIOSTATOOCC").equals("TRUE")) {%>
    				alert("Lo stato occupazionale del lavoratore è cambiato.");
				<%}%>
    		}
    	} 
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
    
    <%@ include file="common/include/calcolaDiffGiorniNew.inc" %>

 	<%@ include file="common/include/_funzioniGenerali.inc" %>
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
  <body class="gestione" onload="caricaPagina();selezionaComboAgevolazioni();">
  <br/><p class="titolo"></p>
  <%@ include file="common/include/GestioneRisultati.inc" %>
  <%-- 
      Gestione profilatura: posizionato qui per motivi di compatibilità con il resto 
      delle variabili della pagina.
  --%>
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
       			<img id='tendinaAzienda' alt='Chiudi' src='<%=img0%>' onclick='cambiaDatiAzienda(this,"datiAzienda_","5")';/>
                Sede Azienda&nbsp;&nbsp; 
                <% if (!insertCollegato && !daAzienda) {%> 
                 <a id='ricercaAzienda' href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda', '', '', '');">
                 <img src="../../img/binocolo.gif" alt="Cerca azienda nel DB"></a>
        		 &nbsp;<a href="#" id="scollegaAzienda" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Scollega azienda"></a>
   				 <%}%>
                 &nbsp;<a href="#" id="DettaglioSedeAzienda" style="position:absolute; display:<%=valDisplay%>" onClick="javascript:apriUnitaAziendale(document.Frm1.PRGAZIENDA.value,document.Frm1.PRGUNITA.value,<%=_funzione%>,'0');">            
                 <img src="../../img/detail.gif" alt="Dettaglio azienda"></a> 
				</div>
            </td>
          </tr> 
          
          <!-- sezione riservata all'azienda che effettua il movimento e eventuale aziennda util. -->
         <%@ include file="generale/include/aziendaNew.inc" %>
          <tr><td colspan=2>&nbsp;&nbsp;&nbsp;</td></tr>
          <tr>
            <td>         
              <div class='sezione2' id='lavoratore'>								
                
                <img id='tendinaLavoratore' alt='Chiudi' src='<%=img0%>' onclick='cambiaLav(this,"datiLavoratore","datiLav","<%=cdnLavoratore%>")';/>
              	Lavoratore&nbsp;&nbsp;
                <%
       			if (!insertCollegato && !daLavoratore) {%>                
                    <a id='ricercaLavoratore' href="#" onClick="javascript:apriLavoratore('LavoratoriNew', 'aggLav', '');"><img src="../../img/binocolo.gif" alt="Cerca lavoratore nel DB"></a>
           		<%}%>
               
                <%boolean modificaTitolo = true;%>
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
    
    <input type="hidden" name="RESETCFL" value="false"/>
    <input type="hidden" name="NUMKLOMOV" value="<%=numKloMov%>"/>
    <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
    <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
    <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
    <input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=prgAziendaUtil%>"/>
    <input type="hidden" name="PRGUNITAUTILIZ" value="<%=prgUnitaUtil%>"/>
    <input type="hidden" name="PRGAZIENDAUTILIZTRA" value="<%=prgAziendaUtil%>"/>
    <input type="hidden" name="PRGUNITAUTILIZTRA" value="<%=prgUnitaUtil%>"/>
    <input type="hidden" name="STRAZINTNUMCONTRATTO" value="<%=numContratto%>">
    <input type="hidden" name="DATAZINTINIZIOCONTRATTO" value="<%=dataInizio%>">
    <input type="hidden" name="DATAZINTFINECONTRATTO" value="<%=dataFine%>">
    <input type="hidden" name="STRAZINTRAP" value="<%=legaleRapp%>">
    <input type="hidden" name="NUMAZINTSOGGETTI" value="<%=numSoggetti%>">
    <input type="hidden" name="NUMAZINTDIPENDENTI" value="<%=classeDip%>">
    <input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>"/>
    <input type="hidden" name="CODTIPOAZIENDATRA" value="<%=codTipoAzienda%>"/>
    <input type="hidden" name="CODTIPOAZIENDAUTIL" value="<%=codTipoAziendaUtil%>"/>
    <input type="hidden" name="DATAINIZIOAVV" value="<%=dataInizioAvv%>"/>
    <input type="hidden" name="MODIFICATAAZIENDA" value="<%=modificataAz%>"/>
    <input type="hidden" name="PRGMOVIMENTOAPP" value="<%=prgMovimentoApp%>"/>
    <input type="hidden" name="DATINIZIOMOVPREC" value="<%=dataInizioMovPrec%>"/>
    <input type="hidden" name="CODMONOTEMPOAVV" value="<%=codMonoTempoAvv%>"/>
    <input type="hidden" name="DATFINEMOVPREC" value="<%=dataFineMovPrec%>"/>
    <input type="hidden" name="DATFINEMOVEFFETTIVA" value="<%=datFineMovEff%>"/>      
    <input type="hidden" name="CODMONOTIPOFINE" value="<%=codMonoTipoFine%>"/>  	
 	<input type="hidden" name="CODCPILAV" value="<%=codCpiLav%>"/>	
    <input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>      
    <input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
    <input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>
    <input type="hidden" name="ACTION" value="aggiorna"/>
    <input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
    <input type="hidden" name="COLLEGATO" value="<%=collegato%>"/>
    <input type="hidden" name="ACTIONPREC" value="<%=action%>"/>
    <input type="hidden" name="PAGE" value="InserisciMovimentoCompletoPage"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
    <input type="hidden" name="CODCPI" value="<%=codCpi%>"/> 
    <input type="hidden" name="CODCPITRA" value="<%=codCpi%>"/>      
    <input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
    <input type="hidden" name="CODMONOPROV" value="M"/>
    <input type="hidden" name="NUOVODOCUMENTO" value=""/>
    <input type="hidden" name="strRagioneSocialeAzUtil" value="<%=strRagioneSocialeAzUtil%>"/>
    <input type="hidden" name="strIndirizzoUAzUtil" value="<%=strIndirizzoUAzUtil%>"/>
    <input type="hidden" name="strComuneUAzUtil" value="<%=strComuneUAzUtil%>"/>
    <input type="hidden" name="strCodiceFiscaleAzUtil" value="<%=strCodiceFiscaleAzUtil%>"/>
    
    <!--Gestione comunicazioni in ritardo per lavoratori disabili-->
	<input type="hidden" name="LAVORATOREDISABILE" value="<%=strDisabile%>"/>
	
	<input type="hidden" name="LAVORATORECOLLMIRATO" value="<%=lavCollMirato%>"/>
	
	<!-- Sezione di gestione impatti tenendo conto della profilatura -->
    <input type="hidden" name="permettiImpatti" value="" />
    <input type="hidden" name="CANVIEW" value=""/>
    <input type="hidden" name="CANEDITLAV" value="" />
    <input type="hidden" name="CANEDITAZ" value="" />
  	<!-- DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO -->
  	<input type="hidden" name="CODTIPODOCEX"  value="<%=codTipoDocEx%>" />
  	<input type="hidden" name="CODMOTIVOPERMSOGGEX"  value="<%=codMotivoPermSoggEx%>" />
  	<input type="hidden" name="STRNUMDOCEX"  value="<%=strNumDocEx%>" />
  	<input type="hidden" name="DATSCADENZA"  value="<%=datScadenza%>" />
  	<input type="hidden" name="QUESTURALAV"  value="<%=questuraPermSogg%>" />
  	<input type="hidden" name="FLGSISTEMAZIONEALLOGGIATIVA"  value="<%=flgAlloggio%>" />
  	<input type="hidden" name="FLGPAGAMENTORIMPATRIO"  value="<%=flgPagamentoRimpatrio%>" />
  	<input type="hidden" name="FLGAZUTILIZESTERA"  value="<%=flgAzUtilizEstera%>" />
  	<input type="hidden" name="FLGSOGGINITALIA"  value="<%=flgSoggInItalia%>" />
  	
  	<input type="hidden" name="CODENTE"     value="<%=codtipoenteprev%>" />
  	<input type="hidden" name="PROVENIENZA" value="linguetta"/>
    
  	<input type="hidden" name="STRNUMAGSOMMINISTRAZIONE" value="<%=strnumagsomm%>" />
  	<input type="hidden" name="CODSOGGETTO"         value="<%=codsoggetto%>" />
  	<input type="hidden" name="FLGTRASFER" value="<%=flgTrasferimento%>"/>
	<!-- GESTIONE APPRENDISTATO -->
  	<input type="hidden" name="STRCOGNOMETUTORE" value="<%=strCognomeTutore%>" />
  	<input type="hidden" name="STRNOMETUTORE" value="<%=strNomeTutore%>" />
  	<input type="hidden" name="STRCODICEFISCALETUTORE" value="<%=strCodiceFiscaleTutore%>" />
  	  	
  	<input type="hidden" name="CODQUALIFICASRQ" value="<%=codQualificaSrq%>" />
  	<input type="hidden" name="DESCQUALIFICASRQ" value="<%=descQualificaSrq%>" />
  	<input type="hidden" name="FLGTITOLARETUTORE" value="<%=flgTitolareTutore%>" />
  	<input type="hidden" name="NUMANNIESPTUTORE" value="<%=numAnniEspTutore%>" />
  	<input type="hidden" name="STRLIVELLOTUTORE" value="<%=strLivelloTutore%>" />
  	<input type="hidden" name="CODMANSIONETUTORE" value="<%=codMansioneTutore%>" />
  	<input type="hidden" name="STRMANSIONETUTORE" value="<%=strMansioneTutore%>" />
  	<input type="hidden" name="STRTIPOMANSIONETUTORE" value="<%=strTipoMansioneTutore%>" />
  	<input type="hidden" name="CODTIPOASSNOAVV" value="" />
  	<input type="hidden" name="NUMMESIAPPRENDISTATO" value="<%=numMesiApprendistato%>" />
  	<input type="hidden" name="FLGARTIGIANA" value="<%=flgArtigiana%>" />
  	<!-- tirocinio -->
  	<input type="hidden" name="STRCODFISCPROMOTORETIR" value="<%=strCodFiscPromotoreTir%>" />
  	<input type="hidden" name="CODCATEGORIATIR" value="<%=codCategoriaTir%>" />
  	<input type="hidden" name="CODTIPOLOGIATIR" value="<%=codTipologiaTir%>" />
  	<input type="hidden" name="STRDENOMINAZIONETIR" value="<%=strDenominazioneTir%>" />
  	<input type="hidden" name="CODSOGGPROMOTOREMIN" value="<%=codSoggPromotoreMin%>" />
  	<input type="hidden" name="CODTIPOENTEPROMOTORE" value="<%=codTipoEntePromotore%>" />
  	
  	<input type="hidden" name="STRNOTE" value="<%=strNote%>" />
  	<input type="hidden" name="FORZA_INSERIMENTO" value="false"/>
  	<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false"/>
  	<input type="hidden" name="FORZA_INSERIMENTO_ETA_APPRENDISTATO" value="<%=forzaInsEtaApprendista%>"/>
  	<input type="hidden" name="CONFERMA_CONTROLLO_MOV_SIMILI" value="<%=confermaMovSimili%>"/>
  	<input type="hidden" name="CONFIRM_DISOC_LUNGADURATA" value="<%=confermaDiscoLungaDurata%>"/>
  	<input type="hidden" name="CONFIRM_NO_MOBILITA" value="<%=confermaNoMobilita%>"/>    
	<input type="hidden" name="FLGAUTORIZZADURATATD" value="<%=autorizzaDurataTD%>"/>
	<input type="hidden" name="CODREGIONE" value=""/>
	<input type="hidden" name="CODREGIONETRA" value=""/>
	<input type="hidden" name="ADD_MOVIMENTO" value="">
    <input type="hidden" name="Differenze" value="">
    <input type="hidden" name="datInizioProt" value="<%=datInizioProt%>" />
    <input type="hidden" name="strTestataMovimento"  value="<%=strTestataMovimento%>" />
    <input type="hidden" name="decMonoProv"  value="<%=decMonoProv%>" />
    <input type="hidden" name="datInizioMovPro"  value="<%=datInizioMovPro%>" />
    <input type="hidden" name="PRGAZIENDATRA"  value="<%=prgAziendaTra%>" />
    <input type="hidden" name="PRGUNITATRA"  value="<%=prgUnitaTra%>" />
    <input type="hidden" name="DATAINIZIOAVVCEV"  value="<%=datInizioMov%>" />
   </table>
   </center>
	<%out.print(htmlStreamBottom);%>
	
	<%@ include file="generale/include/campiMovimentiNew.inc" %>
	
	<% if (canModify) {%>  
  		<table>
  			<tr>
  				<td>
  					<input type="submit" class="pulsanti" name="submitbutton" value="Inserisci" id="btnSubmitMov" onclick="resetFlagForzatura();"/>
				</td>
			</tr>
		</table>
	<%}%>	
   <%@ include file="common/include/GestioneCollegati.inc" %>        
  	<%@ include file="common/include/PulsanteRitornoLista.inc" %>
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
		visualizzaAziUt();
		gestioneProfilo(document.Frm1.CODTIPOMOV);
		controllaInfoLavoratore();
		abilitazioneConvenzione();
		gestisciCampi();
		getSezioni();
		notificaCambioStatoOcc();
		campiAgricoltura();
		gestisciFlagAziEsterna();
	}
    
  	function invia(){
  		if (countSubmit == 0) {
  			countSubmit++;
	   		if(selezionaPulsanteApprendistato() && checkObb() && checkDatInizioMovImpatti("<%=datNormativaPrec297%>")
				&& checkDataFineTD() && checkNumOreSettimanali() && controllaConvenzione() && checkAvvAgr() 
				&& checkCampiAziSomm() && ControllaInserimento("<%=codMonoTipo%>") 
				&& controlloLavAutonomo() && controllaRappLavAccessorio() && gestioneCompetenze() ) {
				return false;
			}
			else {
				countSubmit = 0;
				return false;	
			}
		}
		else {
			return false;
		}
	}
	
	</script>
 </body>
 <%@ include file="common/include/GestioneScriptRisultati.inc" %>
</html>
