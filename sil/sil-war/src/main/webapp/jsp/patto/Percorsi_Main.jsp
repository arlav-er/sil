<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<!-- NOTE: Gestione Patto -->
<%@ taglib uri="patto" prefix="pt" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,
it.eng.sil.util.amministrazione.impatti.PattoBean,
it.eng.sil.security.User,it.eng.afExt.utils.*,it.eng.sil.util.*,java.util.*,java.math.*,java.io.*,it.eng.sil.security.PageAttribs,
it.eng.sil.module.movimenti.constant.Properties, it.eng.sil.module.voucher.*,
com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %> 

<%
  boolean onlyInsert = serviceRequest.getAttribute("ONLY_INSERT") == null ? false
      : true;
  boolean tmp = false;//booleano di appoggio per provenienza da dettaglio o cancellazione
  if (!onlyInsert) {
    tmp = serviceRequest.containsAttribute("ONLY_INSERT_DETT");
  }
  /* Savino 27/02/2006: notare che se 
   *   1. onlyInsert==true  -> tmp=false sempre
   *   2. onlyInsert==false -> tmp=false|true
   */
  String STRPARTITAIVA = "";
  // --- NOTE: Gestione Patto
  String PRG_TAB_DA_ASSOCIARE = null;
  SourceBean row = null;
  String COD_LST_TAB = "OR_PER";
  // ---
  String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  String prgColloquio = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGCOLLOQUIO");
  String codServizio = StringUtils.getAttributeStrNotNull(serviceRequest, "CODSERVIZIO");
  String codMonoProgramma = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMONOPROGRAMMA");
  boolean esistePattoApertoProroga = false;
  
  //Gestione Programma Corso
  boolean esisteIscrCorsiProgramma = false;
  boolean EsisteProgramma = false;               // Prendere il risultato
  String imgSelSezioneProgramma = EsisteProgramma?"../../img/aperto.gif":"../../img/chiuso.gif";
  String displayProgramma = EsisteProgramma? "":"none";
  String prgProgrammaq = "";
  String prgCorso = null; 
  String infoProg = "";  
    String strtitolo = "";
    String datinizio = "";
    String datfine = "";
    String enteProgramma = "";
    String statoProgramma = "";
  
	SourceBean rowRegione = (SourceBean) serviceResponse.getAttribute("M_GetCodRegione.ROWS.ROW");
	String regione = StringUtils.getAttributeStrNotNull(rowRegione, "codregione"); 
	String azione = "Azione";
	String misura = "Obiettivo"; 
	if(Properties.RER.equalsIgnoreCase(regione)) {	
		azione = "Attività";
		misura = "Prestazione";
	}  
    
   /*Borriello operatori azioni*/
   String configOperatoriAz = serviceResponse.containsAttribute("M_GetConfigOperatoriAzioni.ROWS.ROW.STRVALORE")?
		  	serviceResponse.getAttribute("M_GetConfigOperatoriAzioni.ROWS.ROW.STRVALORE").toString():"N";
   boolean isGestioneOperatoriAzione = configOperatoriAz.equalsIgnoreCase("S")? true : false;
   boolean isDescrPrestazione = isGestioneOperatoriAzione;
   SourceBean sbPrgSpiLoggedOp = null;
   String prgSpiOperatoreLoggato = null;
   if(isGestioneOperatoriAzione){
	   sbPrgSpiLoggedOp = (SourceBean) serviceResponse.getAttribute("M_GET_OPERATORE_SPI_TS_UTENTE.ROWS.ROW");
	   if(sbPrgSpiLoggedOp!=null){
		   prgSpiOperatoreLoggato = Utils.notNull(sbPrgSpiLoggedOp.getAttribute("PRGSPI"));
	   }
   }
   String prgOperatorePROPSpi = null;
   String prgOperatoreAVVSpi = null;
   String prgOperatoreCONCSpi = null;
   String reqOpPROPSpi = "false";
   String reqOpAVVSpi = "false";
   String reqOpCONCSpi = "false";
   String strPrestazione = null; 
   /*fine*/
   
  ///////////////////////////////////////////////////////////
  String nonFiltrare = Utils.notNull(serviceRequest.getAttribute("NONFILTRARE"));
  String prgAzioneRagg = null;
  Object azioni = null;
  String prgAzioni = null;
  String datInizioAz = null;
  String datFineAz = null;
  String flgMisuraYei = null;
  String codEsito = null;
  String codEsitoSifer = null;
  String dtmEsitoFormazione = null;
  String dtmAvvioFormazione = null;
  String dtmFineFormazione = null;
  String strNoteFormazione = null;
  String codEsitoRendicont = null;
  String datInizioEsito = null;
  String datFineEsito = null;
  String descrcpi = "";
  String dataStimata = null;
  String dataEffettiva = null;
  String strNote = "";
  String prgPercorso = "";
  String dataScadConferma = null;
  String dataStipula = null;
  String dataScadConfermaAzionePatto = "";
  String dataStipulaAzionePatto = "";
  String codTipoPattoAperto = null;
  String codCpiPattoAperto = null;
  String flgPatto297Aperto = null;
  String codStatoPattoAperto = null;
  String codServiziCIG = null;
  String flgGruppo = null;
  String numPartecipanti = "";
  String flgMediatore = null;
  String flgAbilita = null;
  BigDecimal cdnUtins = null;
  String dtmins = null;
  BigDecimal cdnUtmod = null;
  String cfEntePromotorePercorso = "";
  String flgFormazioneCurr = "";
  String codStatoVoucher = "";
  String codAttivazione = "";
  String descStatoVoucher = "";
  String dataAssegnazioneVCH = "";
  String dataAttivazioneVCH = "";
  String dataChiusuraVCH = "";
  String datMaxAttivazione = "";
  String datMaxErogazione = "";
  String utenteAssVoucher = "";
  String utenteAttVoucher = "";
  String utenteChiVoucher = "";
  String decValTotaleVch = "";
  String decValSpesoVch = "";
  String strdecValTotaleVch = "";
  String strdecValSpesoVch = "";
  String strcfenteaccreditato = "";
  String sedeEnteAccreditato = "";
  String codsede = "";
  String strFlagCM = "";
   
  //reddito cittadinanza
  String prgRedditoCitt = "";
  String protInpsRedditoCitt = "";
  
  String dtmmod = null;
  Testata operatoreInfo = null;
  String moduloAdesione = "M_GetAdesioniGG_Insert";
  String prgKeyAdesione = "";
  boolean provenienzaListaAzioniConcordate = false; //parametro che indica se stiamo provenendo dalla lista d
  //delle azioni concordate
  provenienzaListaAzioniConcordate = serviceRequest.containsAttribute("LISTAAZIONICONCORDATE");
  
  String codTipologiaDurata = "";
  String numYgDurataMin = "";
  String numYgDurataMax = "";
  String numygDurataEff = "";
  String flgNonModificare = "";
  String datAdesioneGG = "";
  String azioneDescrizione = "";
  String datAvvioAzione = null;
  String datDichAzione = null;
  BigDecimal prgPattoProroga = null;
  BigDecimal numkloPattoProroga = null;
  String dataColloquioPercorso = "";
  String dataDecreto2015 = "";
  String codServizioColloquio = "";
  String codEnte = "";
  
  /*Borriello luglio 2019 - Reddito di cittadinanza */
  boolean canViewRDC = false;
  String flagInvioRc1 = serviceResponse.containsAttribute("M_InfoTsGenerale.ROWS.ROW.FLGINVIORC1")?
		  	serviceResponse.getAttribute("M_InfoTsGenerale.ROWS.ROW.FLGINVIORC1").toString():"N";
  if(flagInvioRc1.equalsIgnoreCase("S")){
	  canViewRDC = true;
  }
		  	
  //
  Vector percorsiConcordati = serviceResponse.getAttributeAsVector("M_LISTPERCORSI.ROWS.ROW");
  String module = (String) serviceRequest.getAttribute("MODULE");
  /* if (module==null || module.equals("")) {
       azioni = serviceResponse.getAttribute("M_GETAZIONI.ROWS");
       prgAzioneRagg = (String)serviceRequest.getAttribute("PRGAZIONERAGG");
       codEsito = Utils.notNull(serviceRequest.getAttribute("codEsito"));
       dataStimata = Utils.notNull(serviceRequest.getAttribute("datStimata"));
       note = Utils.notNull(serviceRequest.getAttribute("strnote"));
   }*/

  String checkCIGColl = "false";
  String canCIG = (String) serviceResponse.getAttribute("M_Configurazione_CIG.ROWS.ROW.canCIG");
  if (canCIG.equals("true")) {
    checkCIGColl = (String) serviceResponse
        .getAttribute("M_Check_CIG_In_Colloquio.ROWS.ROW.checkCIGColl");
  }
  
  SourceBean rowColloquioPercorso = (SourceBean) serviceResponse.getAttribute("M_GetInfoColloquio.rows.row");
  if (rowColloquioPercorso != null) {
    dataColloquioPercorso = rowColloquioPercorso.getAttribute("DATCOLLOQUIO").toString();
    dataDecreto2015 = rowColloquioPercorso.getAttribute("DATDECRETO2015").toString();
    codServizioColloquio = rowColloquioPercorso.getAttribute("CODSERVIZIO").toString(); 
    descrcpi = Utils.notNull(rowColloquioPercorso.getAttribute("descrcpi"));
    if(StringUtils.isFilledNoBlank(codServizioColloquio)){
    	codServizio = codServizioColloquio;
    	codMonoProgramma = Utils.notNull(rowColloquioPercorso.getAttribute("CODMONOPROGRAMMA"));
    }
  }

  //////////////////////////////////
  String _page = (String) serviceRequest.getAttribute("PAGE");
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify = attributi.containsButton("aggiorna");
  boolean canInsert = attributi.containsButton("inserisci");
  boolean canDelete = attributi.containsButton("cancella");
  boolean canPrint = attributi.containsButton("stampa");
  boolean canPrintRei = attributi.containsButton("stampa_rei");
  boolean canCheckDataAdesioneYG = attributi.containsButton("CHECK_DATA_ADESIONE_YG");
  boolean canCheckUtenteYG = attributi.containsButton("CHECK_UTENTE_YG");
  
  List  sezioni = attributi.getSectionList();
  boolean canProgramma = sezioni.contains("PROGRAMMACORSO");  
  boolean canViewDiagnosi = sezioni.contains("DIAGNOSI_FUNZ");
  
  
  boolean canListaCompleta = serviceRequest
      .containsAttribute("LISTAAZIONICONCORDATE")
      && serviceRequest.getAttribute("LISTAAZIONICONCORDATE") != null;
  boolean flagChanged = false;
  
  boolean canGestioneLegamePatto = true;
  
  if (Utils.notNull(codMonoProgramma).equals(PattoBean.DB_MISURE_L14)) {
	canInsert = false;
	canModify = false;
	canDelete = false;
  }
  else {
	  if (Utils.notNull(codMonoProgramma).equalsIgnoreCase(PattoBean.DB_MISURE_DOTE) ||
		  Utils.notNull(codMonoProgramma).equalsIgnoreCase(PattoBean.DB_MISURE_DOTE_IA)) {
		  canInsert = false;
		  if(("D").compareTo(user.getCodTipo())==0) {
			  canModify = true;
			  canGestioneLegamePatto = false;
		  } else canModify = false;
	  }
  }

  boolean readOnlyStr = !canModify;
  String fieldReadOnly = canModify ? "false" : "true";
  String fieldReadOnly_flgNonModificare = canModify ? "false" : "true";
  
  boolean nuovo = true;
  if (serviceResponse.containsAttribute("M_GetPercorso")) {
    nuovo = false;
  } else {
    nuovo = true;
  }
  
  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if (apriDiv == null) {
    apriDiv = "none";
  } else {
    apriDiv = "";
  }
  apriDiv = (serviceRequest.containsAttribute("APRIDIV")) ? ""
      : "none";
  String url_nuovo = "AdapterHTTP?PAGE=PercorsiPage"
      + "&CDNLAVORATORE="
      + cdnLavoratore
      + "&CDNFUNZIONE="
      + _funzione
      + "&PRGCOLLOQUIO="
      + prgColloquio
      + "&APRIDIV=1"
      + /* Savino 27/02/2006: problema di navigazione dalla pagina del patto*/
      "NONFILTRARE="
      + nonFiltrare
      + "&COD_LST_TAB="
      + COD_LST_TAB
      + "&"
      + QueryString.toURLParameter(serviceRequest,
          "DA_PATTO_COD_LST_TAB");
  if (onlyInsert || tmp) {
    url_nuovo += "&statoSezioni="
        + StringUtils.getAttributeStrNotNull(serviceRequest,
            "statoSezioni");
    url_nuovo += "&pageChiamante="
        + StringUtils.getAttributeStrNotNull(serviceRequest,
            "PAGECHIAMANTE");
    url_nuovo += "&ONLY_INSERT_DETT=1";//Corrisponde all'ONLY_INSERT      
  }

  SourceBean sbConfigDuaraAz = (SourceBean) serviceResponse.getAttribute("M_ConfigDurataAzione.rows.row");
  boolean checkDurataAz_Tutte = false;
  boolean checkA03FC = false;
  
  if (sbConfigDuaraAz != null && sbConfigDuaraAz.containsAttribute("strvalore") && 
    sbConfigDuaraAz.getAttribute("strvalore").toString().equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {
    checkDurataAz_Tutte = true;
  }
  
  SourceBean sbConfigA03FC = (SourceBean) serviceResponse.getAttribute("M_ConfigFormalizzazioneCompetenze.rows.row");
  if (sbConfigA03FC != null && sbConfigA03FC.containsAttribute("num")) {
    String numConfig = sbConfigA03FC.getAttribute("num").toString();
    if (numConfig.equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {
      checkA03FC = true;  
    }
  }
  
  String numConfigVoucher = serviceResponse.containsAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM")?
        serviceResponse.getAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM").toString():Properties.DEFAULT_CONFIG;
        
  String numConfigDelAssAz = serviceResponse.containsAttribute("M_CONFIG_CANCELLA_ASSOCIAZIONI.ROWS.ROW.NUM")?
  		serviceResponse.getAttribute("M_CONFIG_CANCELLA_ASSOCIAZIONI.ROWS.ROW.NUM").toString():Properties.DEFAULT_CONFIG;
        
  if (!nuovo) {
    moduloAdesione = "M_GetAdesioniGG_Update";
    row = (SourceBean) serviceResponse
        .getAttribute("M_GETPERCORSO.ROWS.ROW");
    //SourceBean row = (SourceBean)percorsiConcordati.get(0);
    prgRedditoCitt = Utils.notNull(row.getAttribute("PRGRDC")); 
  	protInpsRedditoCitt = Utils.notNull(row.getAttribute("STRPROTOCOLLOINPS")); 
    
	descrcpi = Utils.notNull(row.getAttribute("descrcpi"));
    dataStimata = Utils.notNull(row.getAttribute("DATSTIMATA"));
    datAvvioAzione = Utils.notNull(row.getAttribute("datAvvioAzione"));
    datDichAzione = Utils.notNull(row.getAttribute("datDichiarazione"));
    dataEffettiva = Utils.notNull(row.getAttribute("DATEFFETTIVA"));
    dataStipulaAzionePatto = Utils.notNull(row.getAttribute("datstipula"));
    dataScadConfermaAzionePatto = Utils.notNull(row.getAttribute("datscadconferma"));
    prgAzioni = Utils.notNull(row.getAttribute("PRGAZIONI"));
    flgMisuraYei = Utils.notNull(row.getAttribute("flgMisuraYei"));
    codEsito = Utils.notNull(row.getAttribute("CODESITO"));
    codEsitoSifer = Utils.notNull(row.getAttribute("CODESITOSIFER"));
    dtmEsitoFormazione = Utils.notNull(row.getAttribute("DTMESITOFORMAZIONE"));
    dtmAvvioFormazione = Utils.notNull(row.getAttribute("DATAVVIOATTIVITAFORMATIVA"));
    dtmFineFormazione = Utils.notNull(row.getAttribute("DATFINEAZIONEFORMAZIONE"));
    strNoteFormazione = Utils.notNull(row.getAttribute("STRNOTEESITOFORMAZIONE"));
    codEsitoRendicont = Utils.notNull(row.getAttribute("CODESITORENDICONT"));
    codEnte = Utils.notNull(row.getAttribute("CODENTE")); 
    strNote = Utils.notNull(row.getAttribute("STRNOTE"));
    prgPercorso = Utils.notNull(row.getAttribute("PRGPERCORSO"));
    codServiziCIG = Utils.notNull(row.getAttribute("codServiziCIG"));
    flgMediatore = Utils.notNull(row.getAttribute("flgMediatore"));
    flgAbilita = Utils.notNull(row.getAttribute("flgAbilita"));
    PRG_TAB_DA_ASSOCIARE = prgPercorso;
    cdnUtins = (BigDecimal) row.getAttribute("cdnUtins");
    dtmins = StringUtils.getAttributeStrNotNull(row, "dtmins");
    cdnUtmod = (BigDecimal) row.getAttribute("cdnUtmod");
    dtmmod = StringUtils.getAttributeStrNotNull(row, "dtmmod");
    operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);    
    prgProgrammaq = Utils.notNull(row.getAttribute("prgprogrammaq")); 
    infoProg     = Utils.notNull(row.getAttribute("strtitolo"));
    flgGruppo = Utils.notNull(row.getAttribute("flgGruppo"));
    numPartecipanti = Utils.notNull(row.getAttribute("numPartecipanti"));
    flgFormazioneCurr = Utils.notNull(row.getAttribute("flgFormazione"));
     
    enteProgramma = Utils.notNull(row.getAttribute("strragionesociale"));
    datinizio = Utils.notNull(row.getAttribute("datinizio"));
        datfine   = Utils.notNull(row.getAttribute("datfine"));
        statoProgramma = Utils.notNull(row.getAttribute("descrstatoprog"));
    
        cfEntePromotorePercorso = Utils.notNull(row.getAttribute("CFENTEPROMOTORE"));
    
    esisteIscrCorsiProgramma = serviceResponse.containsAttribute("M_GetCorsiIscritti.rows.row");
    if(!"".equals(prgProgrammaq)){
      EsisteProgramma = true; 
    } else {
      EsisteProgramma = false;
    }
    
    imgSelSezioneProgramma = EsisteProgramma?"../../img/aperto.gif":"../../img/chiuso.gif";
    displayProgramma = EsisteProgramma? "":"none";
    
    codTipologiaDurata = Utils.notNull(row.getAttribute("codTipologiaDurata"));
    numYgDurataMin = Utils.notNull(row.getAttribute("numYgDurataMin"));
    numYgDurataMax = Utils.notNull(row.getAttribute("numYgDurataMax"));
    numygDurataEff = Utils.notNull(row.getAttribute("numygDurataEff"));
    flgNonModificare = Utils.notNull(row.getAttribute("flg_non_modificare"));
    datAdesioneGG = Utils.notNull(row.getAttribute("datAdesioneGG"));
    prgKeyAdesione = Utils.notNull(row.getAttribute("codiceAdesioneGG"));
    azioneDescrizione = Utils.notNull(row.getAttribute("azione"));
    codStatoVoucher = Utils.notNull(row.getAttribute("codStatoVoucher"));
    codAttivazione = Utils.notNull(row.getAttribute("codAttivazione"));
    strcfenteaccreditato = Utils.notNull(row.getAttribute("strcfenteaccreditato"));
    sedeEnteAccreditato = Utils.notNull(row.getAttribute("sedeEnteAccreditato"));
    codsede = Utils.notNull(row.getAttribute("codsede"));
    dataAssegnazioneVCH = Utils.notNull(row.getAttribute("datassegnazione"));
    dataAttivazioneVCH = Utils.notNull(row.getAttribute("datattivazione"));
    dataChiusuraVCH = Utils.notNull(row.getAttribute("datfineerogazione"));
    datMaxAttivazione = Utils.notNull(row.getAttribute("datmaxattivazione"));
    datMaxErogazione = Utils.notNull(row.getAttribute("datmaxerogazione"));
    utenteAssVoucher = Utils.notNull(row.getAttribute("utenteAssegna"));
    utenteAttVoucher = Utils.notNull(row.getAttribute("utenteAttiva"));
    utenteChiVoucher = Utils.notNull(row.getAttribute("utenteConcluso"));
    descStatoVoucher = Utils.notNull(row.getAttribute("descStatoVoucher"));
    decValTotaleVch =  Utils.notNull(row.getAttribute("decvaltot"));	
    decValSpesoVch =  Utils.notNull(row.getAttribute("decspesaeffettiva"));
    strFlagCM = Utils.notNull(row.getAttribute("flgCM"));
   	if(StringUtils.isFilledNoBlank(strFlagCM) && strFlagCM.equalsIgnoreCase("S")){
   		strFlagCM = "Si";
   	}else{
   		strFlagCM = "No";
   	}
    if (decValTotaleVch != null) {
      strdecValTotaleVch = decValTotaleVch.toString();
    }
    if (decValSpesoVch != null) {
      strdecValSpesoVch = decValSpesoVch.toString();
    }
    
    if ("S".equalsIgnoreCase(flgNonModificare)) {
      fieldReadOnly = "true";
      fieldReadOnly_flgNonModificare = "true";
    }
    
    if (!fieldReadOnly_flgNonModificare.equalsIgnoreCase("true")) {
      if (numConfigVoucher.equalsIgnoreCase(Properties.CUSTOM_CONFIG) && !codStatoVoucher.equals("") && 
              !codStatoVoucher.equalsIgnoreCase(StatoEnum.ANNULLATO.getCodice())) {
        fieldReadOnly_flgNonModificare = "true";
      }
    }
  }else{
	  if(StringUtils.isFilledNoBlank(prgSpiOperatoreLoggato)){
   		prgOperatorePROPSpi = prgSpiOperatoreLoggato;
   	 }
  }
  //GESTIONE OPERATORI AZIONE 
      /*Borriello operatori azioni*/
    if(isGestioneOperatoriAzione){  
    	 String strTemp = "";
		if(!nuovo){
       	 	strPrestazione = Utils.notNull(row.getAttribute("descrPrestazione"));
       		strTemp= Utils.notNull(row.getAttribute("indicatoreObbligOpAz"));
		}
        if(!StringUtils.isEmptyNoBlank(dataColloquioPercorso)){
        	 int differenzaDate = DateUtils.compare(dataColloquioPercorso, "01/08/2016");
        	 //dataColloquioPercorso>= 01/08/2016
        	 if(differenzaDate>=0){
        		 isGestioneOperatoriAzione = true;
				if(!nuovo){
					 prgOperatorePROPSpi = Utils.notNull(row.getAttribute("PRGSPIPROPOSTA"));
					 prgOperatoreAVVSpi = Utils.notNull(row.getAttribute("PRGSPIAVVIO"));
					 prgOperatoreCONCSpi = Utils.notNull(row.getAttribute("PRGSPICONCLUSIONE"));
					 if (codEsito.equalsIgnoreCase("PRO") || codEsito.equalsIgnoreCase("CC")
							 || codEsito.equalsIgnoreCase("NP1") || codEsito.equalsIgnoreCase("NP2")
							 || codEsito.equalsIgnoreCase("AGA") || codEsito.equalsIgnoreCase("AGD")
							 || codEsito.equalsIgnoreCase("NG") || codEsito.equalsIgnoreCase("NI")
							 || codEsito.equalsIgnoreCase("PRG") || codEsito.equalsIgnoreCase("PRI")) {       
						if(StringUtils.isEmptyNoBlank(prgOperatorePROPSpi) && StringUtils.isFilledNoBlank(prgSpiOperatoreLoggato)){
							prgOperatorePROPSpi= prgSpiOperatoreLoggato;
						} 
						prgOperatoreAVVSpi = null;
						prgOperatoreCONCSpi =null;
					}else  if (codEsito.equalsIgnoreCase("AVV") || codEsito.equalsIgnoreCase("AG1")  || codEsito.equalsIgnoreCase("AGS")) {       
						if(StringUtils.isEmptyNoBlank(prgOperatorePROPSpi) && StringUtils.isFilledNoBlank(prgSpiOperatoreLoggato)){
							prgOperatorePROPSpi= prgSpiOperatoreLoggato;
						}
						if(StringUtils.isEmptyNoBlank(prgOperatoreAVVSpi) && StringUtils.isFilledNoBlank(prgSpiOperatoreLoggato)){
							prgOperatoreAVVSpi= prgSpiOperatoreLoggato;
						}
						prgOperatoreCONCSpi = null;
					}else  if (codEsito.equalsIgnoreCase("RIF") || codEsito.equalsIgnoreCase("RES") || codEsito.equalsIgnoreCase("CAN") ) {       
						if(StringUtils.isEmptyNoBlank(prgOperatorePROPSpi) && StringUtils.isFilledNoBlank(prgSpiOperatoreLoggato)){
							prgOperatorePROPSpi= prgSpiOperatoreLoggato;
						}
						if(StringUtils.isEmptyNoBlank(prgOperatoreCONCSpi) && StringUtils.isFilledNoBlank(prgSpiOperatoreLoggato)){
							prgOperatoreCONCSpi= prgSpiOperatoreLoggato;
						}  	 
						prgOperatoreAVVSpi = null;
					}else if (codEsito.equalsIgnoreCase("FC") || codEsito.equalsIgnoreCase("INT")
								|| codEsito.equalsIgnoreCase("NR") || codEsito.equalsIgnoreCase("PR")
								|| codEsito.equalsIgnoreCase("DEC") || codEsito.equalsIgnoreCase("ESN")
								|| codEsito.equalsIgnoreCase("TRA") || codEsito.equalsIgnoreCase("ESC") ) {       
						if(StringUtils.isEmptyNoBlank(prgOperatorePROPSpi) && StringUtils.isFilledNoBlank(prgSpiOperatoreLoggato)){
							prgOperatorePROPSpi= prgSpiOperatoreLoggato;
						}
						if(StringUtils.isEmptyNoBlank(prgOperatoreAVVSpi) && StringUtils.isFilledNoBlank(prgSpiOperatoreLoggato)){
							prgOperatoreAVVSpi= prgSpiOperatoreLoggato;
						}
						if(StringUtils.isEmptyNoBlank(prgOperatoreCONCSpi) && StringUtils.isFilledNoBlank(prgSpiOperatoreLoggato)){
							prgOperatoreCONCSpi= prgSpiOperatoreLoggato;
						}
					}else{
						//obbligatoriamente null
						prgOperatorePROPSpi = null;
						prgOperatoreAVVSpi = null;
						prgOperatoreCONCSpi =null;
					}
			 	}	    
        	 }else{
        		 isGestioneOperatoriAzione = false;
        	 }
        }else{
        	isGestioneOperatoriAzione = false;
        }
    }//fine
    //FINE GESTIONE OPERATORI AZIONE
  SourceBean pattoAperto = (SourceBean) serviceResponse.getAttribute("M_PATTOAPERTO");
  if (pattoAperto != null) {
    prgPattoProroga = (BigDecimal) pattoAperto
        .getAttribute("ROWS.ROW.PRGPATTOLAVORATORE");
    if (prgPattoProroga != null) {
      esistePattoApertoProroga = true;  
    }
    dataStipula = (String) pattoAperto
        .getAttribute("ROWS.ROW.datStipula");
    dataScadConferma = (String) pattoAperto
        .getAttribute("ROWS.ROW.datScadConferma");
    codStatoPattoAperto = (String) pattoAperto
        .getAttribute("ROWS.ROW.codStatoAtto");
    codTipoPattoAperto = (String) pattoAperto
        .getAttribute("ROWS.ROW.codtipopatto");
    flgPatto297Aperto = (String) pattoAperto
        .getAttribute("ROWS.ROW.flgPatto297");
    numkloPattoProroga = (BigDecimal) pattoAperto
      .getAttribute("ROWS.ROW.NUMKLOPATTOLAVORATORE");
    if (numkloPattoProroga != null) {
      numkloPattoProroga = numkloPattoProroga.add(new BigDecimal("1"));
    }
    codCpiPattoAperto = (String) pattoAperto
        .getAttribute("ROWS.ROW.codcpi");
    
    if (dataStipulaAzionePatto.equals("")) {
      dataStipulaAzionePatto = dataStipula;
    }
    if (dataScadConfermaAzionePatto.equals("")) {
      dataScadConfermaAzionePatto = dataScadConferma;
    }
  }

  Vector listaEsiti = serviceResponse
      .getAttributeAsVector("M_DEESITO.ROWS.ROW");
  Vector listaEsitiRendicont = serviceResponse
      .getAttributeAsVector("M_DEESITORENDICONT.ROWS.ROW");

  String codEsitoRendicontDefault = "";

  if (codEsito != null) {
    for (int i = 0; i < listaEsiti.size(); i++) {
      SourceBean esitoB = (SourceBean) listaEsiti.get(i);
      String esito = Utils.notNull(esitoB.getAttribute("CODICE"));
      String esitoRendicont = Utils.notNull(esitoB
          .getAttribute("CODESITORENDICONT"));
      if (esito.equals(codEsito)) {
        codEsitoRendicontDefault = esitoRendicont;
      }
    }
  }

  /* se sto caricando un percorso concordate e se codEsitoRendicont non è presente nella tabella OR_PERCORSO_CONCORDATO 
   * gli assegno il valore di default all'interno della tabella DE_ESITO */
  if ((codEsitoRendicont == null || "".equals(codEsitoRendicont))
      && codEsito != null) {
    codEsitoRendicont = codEsitoRendicontDefault;
  }

  SourceBean anLav = (SourceBean) serviceResponse
      .getAttribute("M_GetCittadinanza.ROWS.ROW");
  String codCittadinanza = StringUtils.getAttributeStrNotNull(anLav,
      "codcittadinanza");
  String codCittadinanza2 = StringUtils.getAttributeStrNotNull(anLav,
      "codcittadinanza2");

  boolean cittCheck = false;
  if (codCittadinanza.equals("JUG") || codCittadinanza.equals("CUR")
      || codCittadinanza.equals("COR")
      || codCittadinanza.equals("SDR")) {
    cittCheck = true;
  } else if (codCittadinanza2.equals("JUG")
      || codCittadinanza2.equals("CUR")
      || codCittadinanza2.equals("COR")
      || codCittadinanza2.equals("SDR")) {
    cittCheck = true;
  }

  Calendar oggi = Calendar.getInstance();
  String giornoDB = Integer.toString(oggi.get(5));
  if (giornoDB.length() == 1) {
    giornoDB = '0' + giornoDB;
  }
  String meseDB = Integer.toString(oggi.get(2) + 1);
  if (meseDB.length() == 1) {
    meseDB = '0' + meseDB;
  }
  String annoDB = Integer.toString(oggi.get(1));
  String dataOdierna = giornoDB + "/" + meseDB + "/" + annoDB;
  //
  Vector rows = serviceResponse.getAttributeAsVector("M_DeAzioni_NoGroup.ROWS.ROW");
  ComboPair docComboPairPrest = new ComboPair(rows, prgAzioni,"prgAzPrest", "codice", "descrizionePrestazione", "arrayAzPrestazioni");
   
  Vector rowsConfigMappServ1 = serviceResponse.getAttributeAsVector("M_DeAzioniRagg_CodServizio.ROWS.ROW"); 

  String azioniModuleName= "M_DeAzioniRagg_CodServizio";
  if (!nuovo) {
	  azioniModuleName = "M_DeAzioniRagg_CodServizio_Dett";  
  }
  
  String umbriaGestAz = Properties.DEFAULT_CONFIG;

  if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
  		umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
  }
  if(umbriaGestAz.equalsIgnoreCase(Properties.CUSTOM_CONFIG)){
  		azione = "Misura";
		misura = "Servizio";
  }
  
  String queryString = null;
  boolean canStampaAttErogMisura = false;
  if (!nuovo) {
	  if(canPrint && Properties.CAL.equalsIgnoreCase(regione) && flgMisuraYei.equalsIgnoreCase("S") 
			  && (codEsito.equalsIgnoreCase("AVV") || codEsito.equalsIgnoreCase("FC")
			|| codEsito.equalsIgnoreCase("TRA") || codEsito.equalsIgnoreCase("ESC")  )) {
		  canStampaAttErogMisura = true;
	  }
  }  
  
  boolean canStampaBuonoReimpiego = false;
  if(canPrintRei && Properties.UMB.equalsIgnoreCase(regione) && codServizio.equals(MessageCodes.Patto.COD_SERVIZIO_URI)) {
	for (int i = 0; i < percorsiConcordati.size(); i++) {
  		SourceBean percorsoB = (SourceBean) percorsiConcordati.get(i);
		String azioneIesima = Utils.notNull(percorsoB.getAttribute("azione"));
		String codEsitoIesima = Utils.notNull(percorsoB.getAttribute("codesito"));	
		
		if (azioneIesima.equals(MessageCodes.Patto.STR_AZIONE_B2_1) && codEsitoIesima.equals("FC")) {
			canStampaBuonoReimpiego = true;
		}
	}
  }  
  
  boolean canViewCampoCodEnte = false;  
  String strDisabledCampoCodEnte = "true";
  String strRequiredCampoCodEnte = "false";
  String tipoGruppoCollegato = user.getCodTipo();
  String tipoGruppoS = it.eng.sil.module.voucher.Properties.SOGGETTO_ACCREDITATO;
  
  if(Properties.UMB.equalsIgnoreCase(regione) && codServizio.equals(MessageCodes.Patto.COD_SERVIZIO_URI)) {
	  canViewCampoCodEnte = true;
	  if (tipoGruppoCollegato.equalsIgnoreCase(tipoGruppoS) || "".equals(codEnte)) {
		  strDisabledCampoCodEnte = canModify ? "false" : "true";	
		  if (tipoGruppoCollegato.equalsIgnoreCase(tipoGruppoS)) {
		  	strRequiredCampoCodEnte = "true";
		  }
	  }
  }  
 %>

<html>

<head>
  <title>Percorsi</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css"/> 
  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jquery.selectBoxIt.css">
  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jqueryui-sil.css">


  <script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
  <script src="../../js/jqueryui/jquery-ui.min.js"></script>
  <script src="../../js/jqueryui/jquery.selectBoxIt.min.js"></script>
 
  <script type="text/javascript">
    $(function() {
    	$("[name='prgAzioneRagg']").selectBoxIt({
            theme: "default",
            autoWidth: false
        }); 
    	$("[name='prgAzioneRagg']").on('change', function() {
			comboPair.populate();nascondiEntePromotore();toggleVisAzioneDiGruppo();toggleVisGaranziaGiovani();updateDescrPrestazione('clearAll');fieldChanged();
			if ($("[name='prgAzioni']").data("selectBox-selectBoxIt")) {
				$("[name='prgAzioni']").data("selectBox-selectBoxIt").refresh();
			}
    	});	  
		<% if (fieldReadOnly.equals("false") && !nuovo) {%>    	     	
	        $("[name='prgAzioni']").selectBoxIt({
	            theme: "default",
	            autoWidth: false
	        });
	    	$("[name='prgAzioni']").on('change', function() {
	    		fieldChanged();toggleVisAzioneDiGruppo();abilitaEntePromotore();toggleVisGaranziaGiovani();updateDescrPrestazione();checkDataDichiarazione();
	    	});
    	<%}%> 
    	$("[name='codEsito']").selectBoxIt({
            theme: "default",
            autoWidth: false
        });
    	$("[name='codEsito']").on('change', function() {
    		fieldChanged();toggleVisDatEffettiva('TRUE');cambiaEsito();requireDatEffettiva();cambiaOperatoriAzione();
    	});
	});	    
    </script>   
  
  
  
  
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
  <%@ include file="../movimenti/MovimentiRicercaSoggetto.inc" %>
  
  <af:linkScript path="../../js/"/>
  <%@ include file="../documenti/_apriGestioneDoc.inc"%>


  <SCRIPT TYPE="text/javascript"><!--
  <%@ include file="../patto/_sezioneDinamica_script.inc"%>
    // --- NOTE: Gestione Patto
    <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
    <%@ include file="../patto/_controlloDate_script.inc" %>
    
    function getFormObj() {return document.Frm1;}
    var dataStipula = '<%=Utils.notNull(dataStipula)%>';
    var dataScadConferma = '<%=Utils.notNull(dataScadConferma)%>';
    var dataStipulaAzionePatto = '<%=Utils.notNull(dataStipulaAzionePatto)%>';
    var dataScadConfermaAzionePatto = '<%=Utils.notNull(dataScadConfermaAzionePatto)%>';
    var codTipoPattoAperto = "<%=Utils.notNull(codTipoPattoAperto)%>";
    var flgPatto297Aperto = "<%=Utils.notNull(flgPatto297Aperto)%>";
    var codStatoPattoAperto = "<%=Utils.notNull(codStatoPattoAperto)%>";
    var azioneJS = "<%=StringUtils.formatValue4Javascript(azione)%>";
    var vTipoAttivitaIDAzione = new Array();
    var vTipoAttivitaAzione = new Array();
    var vTipoProgettoAzione = new Array();
    var vFlgFormazioneAzione = new Array();
    var vFlgRinnoPatto = new Array();
    var vIDEsito = new Array();
    var vDescEsito = new Array();
    var vFlgFormazioneEsito = new Array();
    var checkDurataAz_Tutte = '<%=checkDurataAz_Tutte%>';
    var checkA03FC = '<%=checkA03FC%>';
    var esistePattoApertoProroga = '<%=esistePattoApertoProroga%>';
    var vIDAzionePolAttive = new Array();
  var dataColloquioPercorso = "<%=Utils.notNull(dataColloquioPercorso)%>";
  var dataDecreto2015 = "<%=Utils.notNull(dataDecreto2015)%>";
  var tipoGruppoUtCollegato = '<%=tipoGruppoCollegato%>';
  var tipoGruppoUtS = '<%=tipoGruppoS%>';
    
    var opened;
    
    function caricaDatiAzioni() {
      <%
      int contatoreRiga = 0;
        Vector listaTipoAttivitaAzioni = serviceResponse.getAttributeAsVector("M_DeAzioni_NoGroup.ROWS.ROW");
        Vector listaEsitiFormazione = serviceResponse.getAttributeAsVector("M_DeEsito.ROWS.ROW");
        Vector listaAzioniPolAttive = serviceResponse.getAttributeAsVector("M_DeAzioni_Politiche_Attive.ROWS.ROW");
        for (int j = 0; j < listaTipoAttivitaAzioni.size(); j++) {
        SourceBean rowTipoAzione = (SourceBean)listaTipoAttivitaAzioni.get(j);
        String tipoAttivita = (String)rowTipoAzione.getAttribute("codtipoattivita");
        String tipoProgettoAzione = (String)rowTipoAzione.getAttribute("codprogetto");
        %>        
        vTipoAttivitaAzione[<%=contatoreRiga%>] = '<%=tipoAttivita%>' ;
        vTipoProgettoAzione[<%=contatoreRiga%>] = '<%=tipoProgettoAzione%>' ;
        vTipoAttivitaIDAzione[<%=contatoreRiga%>] = <%=(BigDecimal)rowTipoAzione.getAttribute("codice")%> ;
        vFlgFormazioneAzione[<%=contatoreRiga%>] = '<%=Utils.notNull(rowTipoAzione.getAttribute("flgFormazione"))%>' ;
        vFlgRinnoPatto[<%=contatoreRiga%>] = '<%=Utils.notNull(rowTipoAzione.getAttribute("flgrinnovopatto"))%>' ;
        <%
        contatoreRiga = contatoreRiga + 1;
      }
        for (int j = 0; j < listaEsitiFormazione.size(); j++) {
          SourceBean rowEsitoFormazione = (SourceBean)listaEsitiFormazione.get(j);
          String idEsito = (String)rowEsitoFormazione.getAttribute("codice");
        String flgFormazioneEsito = Utils.notNull(rowEsitoFormazione.getAttribute("flgFormazione"));
          String descEsito = Utils.notNull(rowEsitoFormazione.getAttribute("descrizione"));
          if (flgFormazioneEsito.equals("")) {
            flgFormazioneEsito = "N"; 
          }
          %>
          vIDEsito[<%=j%>] = '<%=idEsito%>';
          vFlgFormazioneEsito[<%=j%>] = '<%=flgFormazioneEsito%>';
          vDescEsito[<%=j%>] = '<%=descEsito%>';
        <%}
        for (int j = 0; j < listaAzioniPolAttive.size(); j++) {
          SourceBean rowAzionePolAttiva = (SourceBean)listaAzioniPolAttive.get(j);
          BigDecimal idAzionePol = (BigDecimal)rowAzionePolAttiva.getAttribute("codice");
          %>
          vIDAzionePolAttive[<%=j%>] = '<%=idAzionePol%>';
        <%}%>
        
        var isNuovo = <%=nuovo%>;
        if(isNuovo) {
            if (document.Frm1.prgKeyAdesione.options.length > 1) {
              document.Frm1.prgKeyAdesione.selectedIndex=1;
            }
        }
      
    }

    function abilitaEntePromotore() {
        var abilitaBottoneEnteProm = false;
        var flgNonModificareCurr = '<%=flgNonModificare%>';
    if (flgNonModificareCurr != 'S') {
      var codiciObj = eval('document.Frm1.prgAzioni');
      for (var i=0;i<codiciObj.options.length;i++) {
        if (codiciObj.options[i].selected) {
                for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
            if (vTipoAttivitaIDAzione[j] == codiciObj.options[i].value) {
              if (vTipoAttivitaAzione[j] == 'C06' ||
                vTipoAttivitaAzione[j] == 'D01' ||
                vTipoAttivitaAzione[j] == 'E01' ||
                vTipoAttivitaAzione[j] == 'E02' ||
                vTipoAttivitaAzione[j] == 'E03') {
                abilitaBottoneEnteProm = true;
              }
            }
                }
              }
          }
    }
    else {
      var codiciSel = document.Frm1.prgAzioni.value;
            for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
        if (vTipoAttivitaIDAzione[j] == codiciSel) {
          if (vTipoAttivitaAzione[j] == 'C06' ||
            vTipoAttivitaAzione[j] == 'D01' ||
            vTipoAttivitaAzione[j] == 'E01' ||
            vTipoAttivitaAzione[j] == 'E02' ||
            vTipoAttivitaAzione[j] == 'E03') {
            abilitaBottoneEnteProm = true;
          }
        }
            }
    } 
        
    var fieldEnte = document.getElementById("divEnteFields");
      var cftextbox = document.Frm1.strCodiceFiscaleEnte;
    if (abilitaBottoneEnteProm) {
      fieldEnte.style.display = '';
    }
    else {
        cftextbox.value = "";
        fieldEnte.style.display = 'none';
      }
    }

    function nascondiEntePromotore() {
      var fieldEnte = document.getElementById("divEnteFields");
      var cftextbox = document.Frm1.strCodiceFiscaleEnte;
      cftextbox.value = "";
    fieldEnte.style.display = 'none';
    }   
    
    function RicercaProgramma(cdnLav) {
        var urlpage="";
        urlpage+="AdapterHTTP?";
        urlpage+="cdnLavoratore="+cdnLav+"&";
        urlpage+="PAGE=RicercaProgrammaPage";
        opened = window.open(urlpage,"RicercaProgrammi", 'toolbar=0, scrollbars=1,width=800,height=400'); 
    }


    function resetProgramma() {
      <%if (esisteIscrCorsiProgramma) {%>
       alert('Non è possibile cambiare il programma.\nIl lavoratore è iscritto ai corsi del programma.');
       return false;      
        <%}else{%>
          if(confirm("Sei sicuro di voler disassociare il programma al percorso?")) {
            document.Frm1.prgProgrammaq.value="";
            document.Frm1.infoProg.value="";
            <%if(!"".equals(prgProgrammaq)) { %>                        
                document.Frm1.nesistecorso.value="";
              <%  }  %> 
            document.Frm1.statoprog.value="";           
            document.Frm1.datinizioprog.value="";
            document.Frm1.datfineprog.value="";
            document.Frm1.statoprog.value="";
            document.Frm1.enteprog.value="";            
          }
          return true;
    <%}%> 
    }
    
    function EsisteCorso() {
      <%if (esisteIscrCorsiProgramma) {%>
         alert('Non è possibile cambiare il programma.\nIl lavoratore è iscritto ai corsi del programma.');
         return false;      
       <%}else{%>
          RicercaProgramma(<%=cdnLavoratore%>);
       <%}%>
    }
     
    function aggiornaProgramma(){
      
        document.Frm1.prgProgrammaq.value = opened.dati.prgprogrammaq;        
        document.Frm1.infoProg.value = opened.dati.infoProg; 
    document.Frm1.datinizioprog.value= opened.dati.datinizio;
    document.Frm1.datfineprog.value=opened.dati.datfine;
    document.Frm1.statoprog.value=opened.dati.codstatoprogramma;
    document.Frm1.enteprog.value=opened.dati.strRagSocAzienda;
    opened.close();
        cambia(true, document.getElementById("T_S_Programma"));
         
      }
    
    function associazioneAlPattoPossibile() {
		if (codTipoPattoAperto == '<%=PattoBean.DB_MISURE_L14%>') {  
			return "Non è possibile associare un'azione al patto Programma Personalizzato L.14/2015";
		}
      	dataStimata = document.Frm1.datStimata.value;
      	if (dataStipulaAzionePatto=="") return true;
      	if (!validateDate("datStimata")) return "associazione al patto non possibile";
      	if (!(compDate(dataStimata, dataStipulaAzionePatto)>=0)) {
			return "associazione al patto/accordo non possibile: " + azioneJS + " non rientrante nel range di validità del patto/accordo";
      	}
      	else {
          	return null;
      	}
    }
    
    function controlloRange(flgObj) { 
      dataStimata = document.Frm1.datStimata.value;     
      if (dataStipulaAzionePatto=="") return true;
      if (!validateDate("datStimata")) return false;
      if (  (!(compDate(dataStimata, dataStipulaAzionePatto)>=0))
  			&& document.Frm1.PRG_PATTO_LAVORATORE.value!="" ){
        alert("Data stimata fuori dal range di validità del patto/accordo");
        return false;
      }
      return true;
    }

    // ------------------------------------------------------------
    
    /**
    * chiama la pagina di dettaglio
    */

    function dettaglio(prgPercorso, prgAzioneRagg, prgPattoLavoratore) {
      // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
    
        var s= "AdapterHTTP?"
        s += "PAGE=PERCORSIPAGE&";
        s += "PRGPERCORSO=" + prgPercorso+"&";
        s += "CDNLAVORATORE=<%=cdnLavoratore%>&";
        s += "PRGCOLLOQUIO=<%=prgColloquio%>&";
        s += "PRGAZIONERAGG="+prgAzioneRagg+"&";
        s += "CDNFUNZIONE=<%=cdnFunzione%>&";
        s += "DETTAGLIO=TRUE&";
        s += "NONFILTRARE=<%=nonFiltrare%>&";
        s += "APRIDIV=1&";
        s += "COD_LST_TAB=<%=COD_LST_TAB%>&";
        s += "<%=QueryString.toURLParameter(serviceRequest,
          "DA_PATTO_COD_LST_TAB")%>";
        s += "&prgPattoLavoratore="+prgPattoLavoratore;
        <%if (onlyInsert || tmp) {%>
      s += "&statoSezioni=<%=StringUtils.getAttributeStrNotNull(serviceRequest,
            "statoSezioni")%>";
      s += "&pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,
            "PAGECHIAMANTE")%>";
      s += "&ONLY_INSERT_DETT=1";//Corrisponde all'ONLY_INSERT      
        <%}%>
        setWindowLocation(s);
    }

    /**
    * cancella un percorso legato o meno al patto e ritorna questa stessa pagina
    */

    function cancella(prgPercorso, azione, flgNonModificare, prgLavPattoScelta) {
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
    
    if (flgNonModificare == "S") {
      alert("Non è consentito cancellare questa " + azioneJS + " perchè è stata presa in carico dalla Formazione");
      return;
    }

      var s="Eliminare il percorso\n" + azione + " ?";
      if ( confirm(s) ) {
  
        var s= "AdapterHTTP?";
        s += "PAGE=PERCORSIPAGE&";
        s += "MODULE=M_DELETEPERCORSO&";
        s += "PRGPERCORSO=" + prgPercorso + "&";
        s += "OPERAZ=D&";
        s += "PRGCOLLOQUIO=<%=prgColloquio%>&";
        s += "CDNLAVORATORE=<%=cdnLavoratore%>&";
        s += "CDNFUNZIONE=<%=cdnFunzione%>&";
        s += "NONFILTRARE=<%=nonFiltrare%>&";
        s += "COD_LST_TAB=<%=COD_LST_TAB%>&";
        s += "<%=QueryString.toURLParameter(serviceRequest,
        "DA_PATTO_COD_LST_TAB")%>";
        // --- NOTE: Gestione Patto -----------
        s += getParametersForPatto(prgLavPattoScelta);
        // ------------------------------------
    <%if (onlyInsert || tmp) {%>
    s += "&statoSezioni=<%=StringUtils.getAttributeStrNotNull(serviceRequest,
          "statoSezioni")%>";
    s += "&pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,
          "PAGECHIAMANTE")%>";
    s += "&ONLY_INSERT_DETT=1";//Corrisponde all'ONLY_INSERT      
        <%}%>
        setWindowLocation(s);
      }
      return;
    }

    /**
    * controllo (e chiamata del controllo patto) prima dell' invio della form
    */

    function controlla() {       
      var cfFieldEnteProm = false;
      var checkAttivitaB06C06 = false;
      var flgNonModificareCurr = '<%=flgNonModificare%>';
    if (flgNonModificareCurr != 'S') {
      var codiciObj = eval('document.Frm1.prgAzioni');
      for (var i=0;i<codiciObj.options.length;i++) {
        if (codiciObj.options[i].selected) {
                for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
            if (vTipoAttivitaIDAzione[j] == codiciObj.options[i].value) {
              if (vTipoAttivitaAzione[j] == 'C06' ||
                vTipoAttivitaAzione[j] == 'D01' ||
                vTipoAttivitaAzione[j] == 'E01' ||
                vTipoAttivitaAzione[j] == 'E02' ||
                vTipoAttivitaAzione[j] == 'E03') {
                if ( (document.Frm1.codEsitoRendicont.value != '') && 
                   (document.Frm1.codEsitoRendicont.value == 'E' || document.Frm1.codEsitoRendicont.value == 'ENR') ) {
                  cfFieldEnteProm = true;
                }
                else {
                  if (document.Frm1.codEsito.value == 'AVV' || document.Frm1.codEsito.value == 'INT' || 
                    document.Frm1.codEsito.value == 'FC' || document.Frm1.codEsito.value == 'RIF' ||
                   	document.Frm1.codEsito.value == 'RES' || document.Frm1.codEsito.value == 'CAN' ||
                   	document.Frm1.codEsito.value == 'DEC' || document.Frm1.codEsito.value == 'ESN' ||
                   	document.Frm1.codEsito.value == 'TRA' || document.Frm1.codEsito.value == 'ESC') {
                    cfFieldEnteProm = true;
                  }
                }
              }
              if (vTipoAttivitaAzione[j] == 'B06' || vTipoAttivitaAzione[j] == 'C06') {
                checkAttivitaB06C06 = true;
              }
            }
                }
              }
          }
    }
    else {
      var codiciSel = document.Frm1.prgAzioni.value;
      for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
        if (vTipoAttivitaIDAzione[j] == codiciSel) {
          if (vTipoAttivitaAzione[j] == 'C06' ||
            vTipoAttivitaAzione[j] == 'D01' ||
            vTipoAttivitaAzione[j] == 'E01' ||
            vTipoAttivitaAzione[j] == 'E02' ||
            vTipoAttivitaAzione[j] == 'E03') {
            if ( (document.Frm1.codEsitoRendicont.value != '') && 
               (document.Frm1.codEsitoRendicont.value == 'E' || document.Frm1.codEsitoRendicont.value == 'ENR') ) {
              cfFieldEnteProm = true;
            }
            else {
              if (document.Frm1.codEsito.value == 'AVV' || document.Frm1.codEsito.value == 'INT' || 
                document.Frm1.codEsito.value == 'FC' || document.Frm1.codEsito.value == 'RIF' ||
                document.Frm1.codEsito.value == 'RES' || document.Frm1.codEsito.value == 'CAN'||
                document.Frm1.codEsito.value == 'ESN' || document.Frm1.codEsito.value == 'DEC'||
                document.Frm1.codEsito.value == 'TRA' || document.Frm1.codEsito.value == 'ESC') {
                cfFieldEnteProm = true;
              }
            }
          }
          if (vTipoAttivitaAzione[j] == 'B06' || vTipoAttivitaAzione[j] == 'C06') {
            checkAttivitaB06C06 = true;
          }
        }
          }
    }
    
    if (cfFieldEnteProm) {
      if (document.Frm1.strCodiceFiscaleEnte.value == '') {
        if ( (document.Frm1.codEsitoRendicont.value != '') && 
             (document.Frm1.codEsitoRendicont.value == 'E' || document.Frm1.codEsitoRendicont.value == 'ENR') ) {
          alert("Per " + azioneJS + " ed Esito di rendicontazione scelti è obbligatorio inserire il CF Azienda Ospitante.");
        }
        else {
          alert("Per " + azioneJS + " ed Esito scelto è obbligatorio inserire il CF Azienda Ospitante.");  
        }
        document.Frm1.strCodiceFiscaleEnte.focus();
        return false;
      }
    }
    
    var flgNonModificareCurr = '<%=flgNonModificare%>';
    
    if (checkAzioneAdesioneGG() && checkAzioniPolAttiveGG()) {
      alert("Si stanno inserendo sia Pol. Att. in GG che un'adesione GG. Le Pol. Att. verranno legate automaticamente all'adesione che si sta inserendo.");
    }
        
    if (checkAzioneAdesioneGG()) {    
      if (checkAzioniPolAttiveGG()) {   
        var dataStimataAzioneGG = document.Frm1.datStimata.value;
        var dataAdesioneCollegata = document.Frm1.datAdesioneGG.value;
        if (dataStimataAzioneGG != '' && dataAdesioneCollegata != '' && compDate(dataStimataAzioneGG, dataAdesioneCollegata) < 0) {
          if (!confirm("La data stimata della Politica attiva del lavoratore risulta precedente alla data della sua adesione GG, vuoi proseguire?")) {
            return false;
          } 
        }
      }
    }
    else {      
      if (checkAzioniPolAttiveGG()) {       
        var dataStimataAzioneGG = document.Frm1.datStimata.value;
        var dataAdesioneSelezionata = document.Frm1.prgKeyAdesione.value;
        if (dataAdesioneSelezionata != '-' && dataAdesioneSelezionata != '') {
          if (dataStimataAzioneGG != '' && compDate(dataStimataAzioneGG, document.Frm1.prgKeyAdesione.options[document.Frm1.prgKeyAdesione.selectedIndex].text) < 0) {
            if (!confirm("La data stimata della Politica attiva del lavoratore risulta precedente alla data della sua adesione GG, vuoi proseguire?")) {
              return false;
            } 
          }
        }               
      }
    }

    if (checkAzioniPolAttiveGG()) {
      var codEsitoSel = document.Frm1.codEsito.value;
      for (var j = 0; j < vIDEsito.length; j++) {
        if (vIDEsito[j] == codEsitoSel) {
          if (vFlgFormazioneEsito[j] != 'S') {
            alert("Non è possibile selezionare l'esito " + vDescEsito[j] + " in presenza di politica attiva in GG.\n" +
                "Gli esiti utilizzabili per le pol attive in GG sono:\nPROPOSTO/A\nAVVIATO/A\nRIFIUTATO/A\nINTERROTTO/A\n" +
                "CONCLUSO/A\nANNULLATO/A DI UFFICIO\nANNULLATO PER TRASFERIMENTO");
            return false; 
          }
        }
          }
    }
    
    if (!checkAzioneAdesioneGG() && checkAzioniPolAttiveGG()) {     
      var dataAdesioneSelezionata = document.Frm1.prgKeyAdesione.value;
      if (dataAdesioneSelezionata == '') {
        if (!confirm("Attenzione: la politica attiva in GG del lavoratore non ha il riferimento alla sua adesione GG, vuoi proseguire?")) {
          return false;
        }
      } 
    }
    
    if (document.Frm1.codEsito.value == 'AVV' || document.Frm1.codEsito.value == 'INT' || document.Frm1.codEsito.value == 'FC'
    		|| document.Frm1.codEsito.value == 'INF' || document.Frm1.codEsito.value == 'NR'
    		|| document.Frm1.codEsito.value == 'DEC' || document.Frm1.codEsito.value == 'ESN'
    		|| document.Frm1.codEsito.value == 'TRA' || document.Frm1.codEsito.value == 'ESC') {
      if (document.Frm1.datAvvioAzione.value == '') {
        alert('Il campo Data avvio ' + azioneJS + ' è obbligatorio.');
        document.Frm1.datAvvioAzione.focus();
        return false;
      }
    }

    if (document.Frm1.codEsito.value == 'INF') {
      if (flgNonModificareCurr != 'S') {
        alert('Attenzione, esito "Interrotto da Formazione" è utilizzabile solo dalla Formazione.');        
        return false;
      }
    }
    
    if (checkAttivitaB06C06 && checkA03FC == 'true') {
      if (codTipoPattoAperto != '<%=PattoBean.DB_MISURE_OVER_30%>' && codTipoPattoAperto != '<%=PattoBean.DB_MISURE_OVER_45%>' && 
    	  codTipoPattoAperto != '<%=PattoBean.DB_MISURE_INCLUSIONE_ATTIVA%>') {
      alert("Si ricorda che è necessario inserire anche come " + azioneJS + ": \"A03FC - Formalizzazione delle competenze\"."); 
    }
    }
    
    if (esistePattoApertoProroga == 'true' && flgPatto297Aperto == 'S' && codTipoPattoAperto != '<%=PattoBean.DB_MISURE_GARANZIA_GIOVANI%>' && 
    	codTipoPattoAperto != '<%=PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA%>' && codTipoPattoAperto != '<%=PattoBean.DB_MISURE_OVER_30%>' &&
    	codTipoPattoAperto != '<%=PattoBean.DB_MISURE_OVER_45%>' && codTipoPattoAperto != '<%=PattoBean.DB_MISURE_INCLUSIONE_ATTIVA%>' &&
    	codStatoPattoAperto == 'PR' && 
        checkAzioniFlgRinnovo() && document.Frm1.PRG_PATTO_LAVORATORE.value != '' && document.Frm1.PRGPATTOPROROGA.value != '' &&
        document.Frm1.PRG_PATTO_LAVORATORE.value == document.Frm1.PRGPATTOPROROGA.value) {
      if (document.Frm1.codEsito.value == 'CC' || document.Frm1.codEsito.value == 'PRO'
    	  || document.Frm1.codEsito.value == 'PRG'|| document.Frm1.codEsito.value == 'PRI') {
        var dataStimataAzioneRinnovo = document.Frm1.datStimata.value;
        if (dataStimataAzioneRinnovo != '' && dataStipula != '' && compDate(dataStimataAzioneRinnovo, dataStipula) >= 0) {
          document.Frm1.RINNOVAPATTO.value = 'S';  
        }
      }
      else {
        if (document.Frm1.codEsito.value == 'FC' || document.Frm1.codEsito.value == 'TRA' || document.Frm1.codEsito.value == 'ESC') {
          var dataEffettivaAzioneRinnovo = document.Frm1.datEffettiva.value;
          if (dataEffettivaAzioneRinnovo != '' && dataStipula != '' && compDate(dataEffettivaAzioneRinnovo, dataStipula) >= 0) {
            document.Frm1.RINNOVAPATTO.value = 'S';
          }
        }
      }
    }
/*    
    if (dataColloquioPercorso != '' && dataDecreto2015 != '' && compDate(dataColloquioPercorso, dataDecreto2015) >= 0) {
      if (isDataEffettivaRequired) {
        var dataStimataPercorso = document.Frm1.datStimata.value;
        var dataEffettivaPercorso = document.Frm1.datEffettiva.value;
        if (dataStimataPercorso != '' && dataEffettivaPercorso != '' && compDate(dataStimataPercorso, dataEffettivaPercorso) > 0) {
          alert("La data stimata deve essere minore o uguale alla data conclusione effettiva/prevista.");
          return false;
        }
      }
    }
*/    
        if (!controllaPatto() || !controlloRange() )
            return false;   
        else return true;
    }

    
    <%if (onlyInsert || tmp) {
        //questa funzione js potra' essere chiamata soltanto quando la pagina viene chiamata dal template di associazione al patto%>
      function indietroPopUpAssociazione() {
          var urlpage="AdapterHTTP?";
          urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
          urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
          <%List codLstTab = serviceRequest
            .getAttributeAsVector("DA_PATTO_COD_LST_TAB");
        for (int i = 0; i < codLstTab.size(); i++) {%>
              urlpage+="COD_LST_TAB=<%=(String) codLstTab.get(i)%>&";
          <%}%>
          urlpage+="statoSezioni=<%=StringUtils.getAttributeStrNotNull(serviceRequest,
            "statoSezioni")%>&";
          urlpage+="PAGE=AssociazioneAlPattoTemplatePage&";
          urlpage+="pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,
            "PAGECHIAMANTE")%>&";
          urlpage+="NONFILTRARE=<%=nonFiltrare%>&";
          //window.open(urlpage,"_self");
          setWindowLocation(urlpage);
      }
  <%}%>
 

 
  // Rilevazione Modifiche da parte dell'utente
  var flagChanged = false;
  
  function fieldChanged() {
   <%if (!fieldReadOnly.equalsIgnoreCase("true")) {%> 
      flagChanged = true;
   <%}%> 
  }  
  
    //Imposta il flag per l'aggiornamento della data di scadenza del patto
    function confermaAggiornaDataScadPatto(){
      //alert(document.Frm1.inserisci);
      if(document.Frm1.inserisci != null && document.Frm1.inserisci != undefined && document.Frm1.TipoPatto.value != "" && dataScadConfermaAzionePatto != ""){
        var msg = "Si vuole aggiornare la data di scadenza del patto con la data stimata maggiore tra le\nazioni selezionate?";
          msg += "\n(OK = si prosegue con l'associazione aggiornando la data di scadenza del patto\nAnnulla = si prosegue con l'associazione senza aggiornare la data di scadenza del patto)";
          if(confirm(msg)){          
            document.Frm1.action += "?AGGIORNADATASCADPATTO=true";
          }
      }
      return true;
    }
    
    //Funzione per rilevare la modifica dell'azione
    function rilevaModificheAzione(){
      var isNuovo = <%=nuovo%>;
      //alert(isNuovo);
      if(flagChanged && !isNuovo){
        var old = '<%=prgAzioni%>';
        if(old=='null') old='';
        var newVal = document.Frm1.prgAzioni.value;
        //alert("old " + old + " new " + newVal);
        if(old!=newVal){
          document.Frm1.AZIONECAMBIATA.value = "true";
          if(document.Frm1.TipoPatto.value != "")
            document.Frm1.CONPATTO.value = "true";
        }
      }
      return true;
    }


function indietro() {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
  	if (isInSubmit()) return;

 	if(flagChanged) { 
 	 	if(!confirm("I dati sono cambiati.\nProcedere lo stesso?")) { 
 	 	 	return false;
   		}
 	}
  	var urlpage="AdapterHTTP?";
    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&";
    urlpage+="PAGE=COLLOQUIOPAGE&";
    urlpage+="DATA_COD=&";
    urlpage+="CODSERVIZIO=<%=codServizio%>&";
  	urlpage+="PRGCOLLOQUIO=<%=prgColloquio%>&";
  	<%
  		if(StringUtils.isFilledNoBlank(prgSpiOperatoreLoggato)){
  	%>
  		urlpage+="PRGSPI=<%=prgSpiOperatoreLoggato%>&";
  	 <%
  		}else{
  	%>
  		urlpage+="PRGSPI=&";
  	<%
  		} 
  	%>
	<%-- Savino 27/02/2006: parametri sbagliati e che in questo contesto sono inutili? 
	la funzione viene chiamata solo se (!onlyInsert && !tmp) --%>
	urlpage+="PAGECHIAMANTE=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"statoSezioni")%>&";
	urlpage+="STTOSEZIONI=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"PAGECHIAMANTE")%>&";   

    <%if (onlyInsert) {%>
		urlpage+="COD_LST_TAB=<%=COD_LST_TAB%>&";
		urlpage+="ONLY_INSERT=&";
		urlpage+="NONFILTRARE=<%=nonFiltrare%>&";
    <%}%>
    setWindowLocation(urlpage);
}
    
    
    function listaCompletaAzioniConcordate() {
      var url="AdapterHTTP?";
      url+="CDNFUNZIONE=<%=cdnFunzione%>&";
      url+="CDNLAVORATORE=<%=cdnLavoratore%>&";
    url+="PAGE=AzioniConcordateListaPage&";
    url+="PRGCOLLOQUIO=<%=prgColloquio%>";
    
    setWindowLocation(url);
    
    }
    
    listaEsitiRendicont = new Array();
    
  function inizializza_esiti(){
    <%for (int i = 0; i < listaEsiti.size(); i++) {
        SourceBean esito = (SourceBean) listaEsiti.get(i);
        String cod_Esito = Utils.notNull(esito.getAttribute("CODICE"));
        String cod_EsitoRendicont = Utils.notNull(esito
            .getAttribute("CODESITORENDICONT"));%>
              
        listaEsitiRendicont["<%=cod_Esito%>"] = "<%=cod_EsitoRendicont%>";
    <%}%>
    //cambiaEsito();
  }

  function inizializza_esitirendicont(){
    <%for (int i = 0; i < listaEsitiRendicont.size(); i++) {
        SourceBean esitoRendicont = (SourceBean) listaEsitiRendicont
            .get(i);
        String cod_EsitoRendicont = Utils.notNull(esitoRendicont
            .getAttribute("CODICE"));
        String desc_EsitoRendicont = Utils.notNull(esitoRendicont
            .getAttribute("DESCRIZIONE"));
        String txtEsito = "";%>         
        listaEsitiRendicont[<%=i%>] = "<%=cod_EsitoRendicont%>";
    <%}%>
    //cambiaEsitoRendicont();
  }

  
  
  function cambiaEsitoRendicont() {
    //se vi è un valore di default per il campo
    if (document.Frm1.codEsitoRendicontHid.value != '') {
      //se si sta impostando un valore diverso da quello di default
      if (document.Frm1.codEsitoRendicont.value != document.Frm1.codEsitoRendicontHid.value) {
        //chiedi conferma 
        if (confirm('Attenzione!\nIl valore selezionato non è quello predefinito per l\'esito corrispondente.\nSi desidera continuare?')) {
          //se risponde in maniera positiva cambia il valore
          fieldChanged();         
        }
        else {
          //altrimenti reimposta il precedente
          document.Frm1.codEsitoRendicont.value = document.Frm1.codEsitoRendicontOld.value;
        }
      }
    }
    //in tutti i casi il valore precedente è uguale a quello attuale.
    document.Frm1.codEsitoRendicontOld.value = document.Frm1.codEsitoRendicont.value

    //controlla l'obbligatorietà della data effettiva
    requireDatEffettiva();
    
    return true;
  }

  var isDataEffettivaRequired = false;
/* gestione vecchia 
  function requireDatEffettiva() {
	  alert(document.Frm1.codEsitoRendicont.value);
    if ( (document.Frm1.codEsitoRendicont.value == 'E') || 
       (dataColloquioPercorso != '' && dataDecreto2015 != '' && compDate(dataColloquioPercorso, dataDecreto2015) >= 0 && document.Frm1.codEsito.value == 'AVV') ) {
      document.getElementById('datEffettivaMandatory').innerHTML = '*';
      isDataEffettivaRequired = true;
    }
    else {
      document.getElementById('datEffettivaMandatory').innerHTML = '';
      document.Frm1.datEffettiva.value = '';
      isDataEffettivaRequired = false;
    }     
    toggleVisDatEffettiva('FALSE');
  }
  */
  
  function requireDatEffettiva() {
	  var esitoVar = document.Frm1.codEsito.value;
    if ( esitoVar == 'PRO' || esitoVar == 'RES' || esitoVar == 'RIF' || esitoVar == 'NA' 
    		|| esitoVar == 'ANT' || esitoVar == 'CAN' || esitoVar == 'PRG'  || esitoVar == 'PRI' ) 
    {
      document.getElementById('datEffettivaMandatory').innerHTML = '';
      document.Frm1.datEffettiva.value = '';
      isDataEffettivaRequired = false;
    }
    if (esitoVar == 'AVV' || esitoVar == 'NR' || esitoVar == 'INT' || esitoVar == 'INF' 
		|| esitoVar == 'FC' || esitoVar == 'DEC' || esitoVar == 'ESN' || esitoVar == 'TRA' || esitoVar == 'ESC') {
    	if (dataColloquioPercorso != '' && dataDecreto2015 != '' && compDate(dataColloquioPercorso, dataDecreto2015) >= 0) {
        	document.getElementById('datEffettivaMandatory').innerHTML = '*';
        	isDataEffettivaRequired = true;
    	}
    	else {
    		document.getElementById('datEffettivaMandatory').innerHTML = '';
    		isDataEffettivaRequired = false;
    	}	
    }    
    toggleVisDatEffettiva('FALSE');
  }
  
  	function checkDataEffettiva() {
    	if (isDataEffettivaRequired) {
      		/*se non vi è nessun valore indica che il campo è obbligatorio*/
      		if (document.Frm1.datEffettiva.value == '') {
        		alert('Il campo Data conclusione effettiva/prevista è obbligatorio.');
        		document.Frm1.datEffettiva.focus();
        		return false;
      		}
    	}
   		/*se il valore all'internod el campo è corretto formalmente*/
   		if (checkFormatDate(document.Frm1.datEffettiva)) {
     		/*se il valore è futuro indica che non può esserlo*/
     		var dataOdierna = '<%=dataOdierna%>';
     		if (compareDate(document.Frm1.datEffettiva.value,dataOdierna) > 0) {
       			if (document.Frm1.codEsito.value != 'AVV') {
         			alert("Non è possibile inserire una data conclusione futura con esito rendicontazione 'Erogato'.");
         			document.Frm1.datEffettiva.focus();
         			return false;
       			}
      		}
   		}
    	return true;
  	}


  function toggleVisDatEffettiva(azzera){     
    var divDataEffettiva = document.getElementById("divDataEffettiva");
//    if (azzera=='TRUE') {
//      document.Frm1.datEffettiva.value='';
//    }
//    if (document.Frm1.codEsito.value=='FC' ||
//      document.Frm1.codEsitoRendicont.value == 'E') {
//      divDataEffettiva.style.display='';
//    } else {
//      divDataEffettiva.style.display='none';      
//    }
	
	var esitoVar = document.Frm1.codEsito.value;
	if (esitoVar == 'AVV' || esitoVar == 'NR' || esitoVar == 'INT' || esitoVar == 'INF' 
		|| esitoVar == 'FC'|| esitoVar == 'DEC'|| esitoVar == 'ESN'|| esitoVar == 'TRA'|| esitoVar == 'ESC') {
		divDataEffettiva.style.display='';	
	} else {
		divDataEffettiva.style.display='none';
	}

	//if ( (document.Frm1.codEsitoRendicont.value =='E') ||
    //   (dataColloquioPercorso != '' && dataDecreto2015 != '' && compDate(dataColloquioPercorso, dataDecreto2015) >= 0 && document.Frm1.codEsito.value == 'AVV') ) {
    //  divDataEffettiva.style.display='';
    //} else {
    //  divDataEffettiva.style.display='none';
    //}
  }

  var isAzioneDiGruppoRequired = false;
  var isNumPartecipantiRequired = false;
  var isDataAdesioneYGToFill = false;

  function checkAzioniFlgFormazioneS() {
    var flgNonModificareCurr = '<%=flgNonModificare%>';
    if (flgNonModificareCurr != 'S') {
      var codiciObj = eval('document.Frm1.prgAzioni');
      for (var i=0;i<codiciObj.options.length;i++) {
        if (codiciObj.options[i].selected) {
          for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
            if (vTipoAttivitaIDAzione[j] == codiciObj.options[i].value) {
              if (vFlgFormazioneAzione[j] == 'S') {
                return true;
              }
            }
          }
            }
        }
    }
    else {
      var codiciSel = document.Frm1.prgAzioni.value;
      for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
        if (vTipoAttivitaIDAzione[j] == codiciSel) {
          if (vFlgFormazioneAzione[j] == 'S') {
            return true;
          }
        }
      }
    }
      return false;
  }

  function checkAzioniFlgRinnovo() {
    var flgNonModificareCurr = '<%=flgNonModificare%>';
    if (flgNonModificareCurr != 'S') {
      var codiciObj = eval('document.Frm1.prgAzioni');
      for (var i=0;i<codiciObj.options.length;i++) {
        if (codiciObj.options[i].selected) {
          for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
            if (vTipoAttivitaIDAzione[j] == codiciObj.options[i].value) {
              if (vFlgRinnoPatto[j] == 'S') {
                return true;
              }
            }
          }
            }
        }
    }
    else {
      var codiciSel = document.Frm1.prgAzioni.value;
      for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
        if (vTipoAttivitaIDAzione[j] == codiciSel) {
          if (vFlgRinnoPatto[j] == 'S') {
            return true;
          }
        }
      }
    }
      return false;
  }
  
  function checkAzioniPolAttiveGG() {
    var flgNonModificareCurr = '<%=flgNonModificare%>';
    if (flgNonModificareCurr != 'S') {
      var codiciObj = eval('document.Frm1.prgAzioni');
      for (var i=0;i<codiciObj.options.length;i++) {
        if (codiciObj.options[i].selected) {
          for (var j = 0; j < vIDAzionePolAttive.length; j++) {
            if (vIDAzionePolAttive[j] == codiciObj.options[i].value) {
              return true;
            }
          }
            }
        }
    }
    else {
      var codiciSel = document.Frm1.prgAzioni.value;
      for (var j = 0; j < vIDAzionePolAttive.length; j++) {
        if (vIDAzionePolAttive[j] == codiciSel) {
          return true;
        }
      }
    }
      return false;
  }

  function checkAzioniAllFlgFormazioneS() {
    var flgNonModificareCurr = '<%=flgNonModificare%>';
    if (flgNonModificareCurr != 'S') {
      var codiciObj = eval('document.Frm1.prgAzioni');
      var azioniGG = false;
      for (var i=0;i<codiciObj.options.length;i++) {
        if (codiciObj.options[i].selected) {
          for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
            if (vTipoAttivitaIDAzione[j] == codiciObj.options[i].value) {
              if (vFlgFormazioneAzione[j] == 'S') {
                azioniGG = true;
              }
              else {
                return false;
              } 
            }
          }
            }
        }
    }
    else {
      var codiciSel = document.Frm1.prgAzioni.value;
      for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
        if (vTipoAttivitaIDAzione[j] == codiciSel) {
          if (vFlgFormazioneAzione[j] == 'S') {
            azioniGG = true;
          }
          else {
            return false;
          } 
        }
      }
    }
      return azioniGG;
  }


  function esisteAzioneFlgFormazioneS() {
    var flgNonModificareCurr = '<%=flgNonModificare%>';
    if (flgNonModificareCurr != 'S') {
      var codiciObj = eval('document.Frm1.prgAzioni');
      for (var i=0;i<codiciObj.options.length;i++) {
        if (codiciObj.options[i].selected) {
          for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
            if (vTipoAttivitaIDAzione[j] == codiciObj.options[i].value) {
              if (vFlgFormazioneAzione[j] == 'S') {
                return true;
              } 
            }
          }
            }
        }
    }
    else {
      var codiciSel = document.Frm1.prgAzioni.value;
      for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
        if (vTipoAttivitaIDAzione[j] == codiciSel) {
          if (vFlgFormazioneAzione[j] == 'S') {
            return true;
          } 
        }
      }
    }
      return false;
  }

  
  function checkAzioneAdesioneGG() {
    var flgNonModificareCurr = '<%=flgNonModificare%>';
    if (flgNonModificareCurr != 'S') {
      var codiciObj = eval('document.Frm1.prgAzioni');
      for (var i=0;i<codiciObj.options.length;i++) {
        if (codiciObj.options[i].selected) {
          if (codiciObj.options[i].text == 'Adesione GG') {
            return true;
          }
            }
        }
    }
    else {
      var codiciObj = eval('document.Frm1.prgAzioni');
      if (codiciObj.text == 'Adesione GG') {
        return true;
      }
    }
      return false;
  }
  
  function toggleVisGaranziaGiovani() {   
    if (checkAzioneAdesioneGG()) {
      divGaranziaGiovani.style.display='';
      isDataAdesioneYGToFill = true;
    } else {
      divGaranziaGiovani.style.display='none'; 
      isDataAdesioneYGToFill = false;   
    }

    if (!checkAzioneAdesioneGG()) {
      if (checkAzioniPolAttiveGG()) {
        divSceltaAdesione.style.display=''; 
      }
      else {
        divSceltaAdesione.style.display='none'; 
      }
    }
    else {
      divSceltaAdesione.style.display='none';
    }
  }

  function toggleVisAzioneDiGruppo(){
    var isNuovoPercorso = <%=nuovo%>;
    var flgNonModificareCurr = '<%=flgNonModificare%>';
    if (isNuovoPercorso || flgNonModificareCurr != 'S') {
      var divAzioneDiGruppo = document.getElementById("divAzioneDiGruppo");
      
      if (checkAzioniFlgFormazioneS()) {
        divAzioneDiGruppo.style.display='';
        isAzioneDiGruppoRequired = true;
      } else {
        divAzioneDiGruppo.style.display='none';  
        isAzioneDiGruppoRequired = false;    
      }
      toggleVisNumPartecipanti();
    }
  }
  
  function checkDataDichiarazione() {
	var isNuovo = <%=nuovo%>;
	if (!isNuovo) {
  		var codiciSel = document.Frm1.prgAzioni.value;
		for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
		    if (vTipoAttivitaIDAzione[j] == codiciSel) {
		      if (vTipoAttivitaAzione[j] == 'A02' && vTipoProgettoAzione[j] == '05') {
		    	 divDataDichirazione.style.display='';
		      }
		      else {
		    	  divDataDichirazione.style.display='none';
		      }
		    }
		}
	}
	else {
		var isAzionePresaInCarico = false;
		var codiciObj = eval('document.Frm1.prgAzioni');
    	for (var i=0;i<codiciObj.options.length;i++) {
      		if (codiciObj.options[i].selected) {
              for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
          		if (vTipoAttivitaIDAzione[j] == codiciObj.options[i].value) {
	            	if (vTipoAttivitaAzione[j] == 'A02' && vTipoProgettoAzione[j] == '05') {
	            		isAzionePresaInCarico = true;
	            		divDataDichirazione.style.display='';
	            	}
	          	}
              }
            }
        }
        if (isAzionePresaInCarico) {
        	divDataDichirazione.style.display='';   
        }
        else {
        	divDataDichirazione.style.display='none';
        }       
	}
  }
  
  function toggleVisNumPartecipanti(){
    var divNumPartecipanti = document.getElementById("divNumPartecipanti");
    if (document.Frm1.flgGruppo.value == 'S') {
      divNumPartecipanti.style.display='';
      isNumPartecipantiRequired = true;
    } else {
      divNumPartecipanti.style.display='none';      
      isNumPartecipantiRequired = false;
    }
  }

  function checkAzione1AeYG() {
    var isNuovoPercorso = <%=nuovo%>;
    var flgNonModificareCurr = '<%=flgNonModificare%>';
    if (isAzioneDiGruppoRequired) {
      if (document.Frm1.flgGruppo.value == '') {
        alert('Il campo Azione di Gruppo è obbligatorio.');
        document.Frm1.flgGruppo.focus();
        return false;
      }
    } else {
      if (isNuovoPercorso || flgNonModificareCurr != 'S') {
        document.Frm1.flgGruppo.value = '';
      }
    }
    
    if (isNumPartecipantiRequired) {
      if (document.Frm1.numPartecipanti.value == '') {
        alert('Il campo Numero Partecipanti è obbligatorio.');
        document.Frm1.numPartecipanti.focus();
        return false;
      }
    } else {
      if (isNuovoPercorso || flgNonModificareCurr != 'S') {
        document.Frm1.numPartecipanti.value = '';
      }
    }

    if (!isDataAdesioneYGToFill) {
      document.Frm1.datAdesioneGG.value = '';
    }
    else {
      if (document.Frm1.datAdesioneGG.value == '') {
        alert('Il campo Data Adesione è obbligatorio.');
        document.Frm1.datAdesioneGG.focus();
        return false;
      }
    }
    return true;
  }

  function cambiaEsito(){
    var selEsito = document.Frm1.codEsito; 
    var esitoRend = document.Frm1.codEsitoRendicont;
    var esitoRendHid = document.Frm1.codEsitoRendicontHid;
    var esitoRendOld = document.Frm1.codEsitoRendicontOld;
    var isNuovoPercorso = <%=nuovo%>;
    var flgNonModificareCurr = '<%=flgNonModificare%>';
    if (selEsito.value == "") {       
      esitoRend.value = "";
      esitoRendOld.value = "";
      esitoRendHid.value = "";
    }
    else {
      esitoRend.value = listaEsitiRendicont[selEsito.value];
      esitoRendOld.value = listaEsitiRendicont[selEsito.value];
      esitoRendHid.value = listaEsitiRendicont[selEsito.value];
    }
  }


 //Borriello: gestione obbligatorietà operatori che gestiscono l'azione
 var arrayAzPrestazioni = new Array();     
 <%= docComboPairPrest.makeArrayJSChild()%>
 //console.log(arrayAzPrestazioni);
 function updateDescrPrestazione(isClear){
    	var isNuovoP =<%= nuovo%>;
      	 var isDescrP = <%= isDescrPrestazione%>;
      	 if(!isNuovoP && isDescrP){
      		if(isClear !== undefined && isClear !== null && isClear!==""){
      			getFormObj().strPrestazione.value='';
      		}else{
      			var prgAzSelected = getFormObj().prgAzioni.value;
      			
      			 
          		var obj = arrayAzPrestazioni.filter(function ( obj ) {
          		    return obj.prgAzione === prgAzSelected;
          		})[0];
          		var prest =obj.descrPrestazione;
          		if(prest=="null" || prest == "undefined" || prest=="NULL"){
          			prest='';
          		} 
          		getFormObj().strPrestazione.value=prest;	 
      		}
      		
      	 }
       }

 
 
 var isprgOperatorePROPSpiRequired = false;
 var isprgOperatoreAVVSpiRequired = false;
 var isprgOperatoreCONCSpiRequired = false;
 var inOnChangeEsito = false;
 
 function cambiaOperatoriAzione(){
	var operatoreLoggatoSpi = <%=StringUtils.isFilledNoBlank(prgSpiOperatoreLoggato)%>;
 	var isToDo = <%= isGestioneOperatoriAzione %>;
 	if(isToDo){
 		isOnchangeEsito = true;
	    var selEsito = document.Frm1.codEsito; 
	    var opSpiPVal = <%=StringUtils.isEmptyNoBlank(prgOperatorePROPSpi)%>;
	    var opSpiAVal = <%=StringUtils.isEmptyNoBlank(prgOperatoreAVVSpi)%>;
	    var opSpiCVal = <%=StringUtils.isEmptyNoBlank(prgOperatoreCONCSpi)%>;
	    if (selEsito.value == "PRO" || selEsito.value == "CC"
	    	|| selEsito.value == "NP1" || selEsito.value == "NP2"
	    	|| selEsito.value == "AGA" || selEsito.value == "AGD"
	    	|| selEsito.value == "NG" || selEsito.value == "NI"
	    	|| selEsito.value == "PRG" || selEsito.value == "PRI") 
	    {       
	    	isprgOperatorePROPSpiRequired= true;
	    	isprgOperatoreAVVSpiRequired=false;
	    	isprgOperatoreCONCSpiRequired=false;
	    	if(operatoreLoggatoSpi && opSpiPVal){
	    		if(document.Frm1.prgOperatorePROPSpi.value==''){
	    			document.Frm1.prgOperatorePROPSpi.value= <%=prgSpiOperatoreLoggato%>;
	    		}
	    	}	 
	    }else  if (selEsito.value == "AVV" || selEsito.value == "AG1" || selEsito.value == "AGS") {       
	    	isprgOperatorePROPSpiRequired = true;
	    	isprgOperatoreAVVSpiRequired=true;
	    	isprgOperatoreCONCSpiRequired=false;
	    	if(operatoreLoggatoSpi && opSpiPVal){
	    		if(document.Frm1.prgOperatorePROPSpi.value==''){
	    			document.Frm1.prgOperatorePROPSpi.value= <%=prgSpiOperatoreLoggato%>;
	    		}
	    	 
	    	}
	    	if(operatoreLoggatoSpi && opSpiAVal){
	    		if(document.Frm1.prgOperatoreAVVSpi.value==''){
	    			document.Frm1.prgOperatoreAVVSpi.value= <%=prgSpiOperatoreLoggato%>;
	    		}
 	    	}
	    }else  if (selEsito.value == "RIF" || selEsito.value == "RES" || selEsito.value == "CAN") {       
	    	isprgOperatorePROPSpiRequired = true;
	    	isprgOperatoreAVVSpiRequired=false;
	    	isprgOperatoreCONCSpiRequired=true;
	    	if(operatoreLoggatoSpi && opSpiPVal){
	    		if(document.Frm1.prgOperatorePROPSpi.value==''){
	    			document.Frm1.prgOperatorePROPSpi.value= <%=prgSpiOperatoreLoggato%>;
	    		}
	    	}
	    	if(operatoreLoggatoSpi && opSpiCVal){
	    		if(document.Frm1.prgOperatoreCONCSpi.value==''){
	    			document.Frm1.prgOperatoreCONCSpi.value= <%=prgSpiOperatoreLoggato%>;
	    		}
 	    	}
	    }else if (selEsito.value == "FC" || selEsito.value == "INT"
	    		|| selEsito.value == "NR" || selEsito.value == "PR"
	    		|| selEsito.value == "DEC" || selEsito.value == "ESN"
	    		|| selEsito.value == "TRA" || selEsito.value == "ESC") 
	    {       
	    	isprgOperatorePROPSpiRequired = true;
	    	isprgOperatoreAVVSpiRequired=true;
	    	isprgOperatoreCONCSpiRequired=true;
	    	if(operatoreLoggatoSpi && opSpiPVal){
	    		if(document.Frm1.prgOperatorePROPSpi.value==''){
	    			document.Frm1.prgOperatorePROPSpi.value= <%=prgSpiOperatoreLoggato%>;
	    		}
 	    	}
	    	if(operatoreLoggatoSpi && opSpiAVal){
	    		if(document.Frm1.prgOperatoreAVVSpi.value==''){
	    			document.Frm1.prgOperatoreAVVSpi.value= <%=prgSpiOperatoreLoggato%>;
	    		}
	    	}
	    	if(operatoreLoggatoSpi && opSpiCVal){
	    		if(document.Frm1.prgOperatoreCONCSpi.value==''){
	    			document.Frm1.prgOperatoreCONCSpi.value= <%=prgSpiOperatoreLoggato%>;
	    		}
 	    	}
	    }else{
	    	isprgOperatorePROPSpiRequired = false;
	    	isprgOperatoreAVVSpiRequired=false;
	    	isprgOperatoreCONCSpiRequired=false;
	    }	
 	}
}
	 
 function checkOperatoriAzione() {
	var isToDo = <%= isGestioneOperatoriAzione %>;
	if(!isToDo){
		return true;
	}
	 var esitoDescr = document.Frm1.codEsito.options[document.Frm1.codEsito.selectedIndex].label;
	//ci sono stati eventi onchange 
	if(inOnChangeEsito){
		  
		if (isprgOperatorePROPSpiRequired) {
		   if (document.Frm1.prgOperatorePROPSpi.value == '') {
		     alert('Il campo Operatore proposta è obbligatorio.');
		     document.Frm1.prgOperatorePROPSpi.focus();
		     return false;
		   }
		}
		if (!isprgOperatorePROPSpiRequired) {
 		   if (document.Frm1.prgOperatorePROPSpi.value !== '') {
		     alert("Il campo 'Operatore proposta' non deve essere valorizzato quando l'esito è '" + esitoDescr + "'" );
		     return false;
		   }
		}
		if (isprgOperatoreAVVSpiRequired) {
		   if (document.Frm1.prgOperatoreAVVSpi.value == '') {
			     alert('Il campo Operatore avvio è obbligatorio.');
			     document.Frm1.prgOperatoreAVVSpi.focus();
			     return false;
			}
		}
		if (!isprgOperatoreAVVSpiRequired) {
			 if (document.Frm1.prgOperatorePROPSpi.value !== '') {
			     alert("Il campo 'Operatore avvio' non deve essere valorizzato quando l'esito è '" + esitoDescr + "'" );
			     return false;
			  }
		}
		if (isprgOperatoreCONCSpiRequired) {
		   	if (document.Frm1.prgOperatoreCONCSpi.value == '') {
			     alert('Il campo Operatore conclusione è obbligatorio.');
			     document.Frm1.prgOperatoreCONCSpi.focus();
			     return false;
			  }
		}
		if (!isprgOperatoreCONCSpiRequired) {
			if (document.Frm1.prgOperatorePROPSpi.value !== '') {
			     alert("Il campo 'Operatore conclusione' non deve essere valorizzato quando l'esito è '" + esitoDescr + "'" );
			     return false;
			  }
		}
	}else{ //non ci son stati eventi onchange: devo vedere rispetto al valore iniziale
		var selEsito = document.Frm1.codEsito; 
	    if (selEsito.value == "PRO" || selEsito.value == "CC"
	    	|| selEsito.value == "NP1" || selEsito.value == "NP2"
		    || selEsito.value == "AGA" || selEsito.value == "AGD"
		    || selEsito.value == "NG" || selEsito.value == "NI"
		    || selEsito.value == "PRG" || selEsito.value == "PRI") 
	    {       
	    	 if (document.Frm1.prgOperatorePROPSpi.value == '') {
			     alert('Il campo Operatore proposta è obbligatorio.');
			     document.Frm1.prgOperatorePROPSpi.focus();
			     return false;
			 }
	    	 if (document.Frm1.prgOperatoreAVVSpi.value !== '') {
	    		 alert("Il campo 'Operatore avvio' non deve essere valorizzato quando l'esito è '" + esitoDescr + "'" );
			     return false;
			}
	    	 if (document.Frm1.prgOperatoreCONCSpi.value !== '') {
	    		 alert("Il campo 'Operatore conclusione' non deve essere valorizzato quando l'esito è '" + esitoDescr + "'" );
			     return false;
			}
	    }else  if (selEsito.value == "AVV" || selEsito.value == "AG1" || selEsito.value == "AGS") {       
	    	if (document.Frm1.prgOperatorePROPSpi.value == '') {
			     alert('Il campo Operatore proposta è obbligatorio.');
			     document.Frm1.prgOperatorePROPSpi.focus();
			     return false;
			}
	    	if (document.Frm1.prgOperatoreAVVSpi.value == '') {
			     alert('Il campo Operatore avvio è obbligatorio.');
			     document.Frm1.prgOperatoreAVVSpi.focus();
			     return false;
			}
	    	if (document.Frm1.prgOperatoreCONCSpi.value !== '') {
	    		 alert("Il campo 'Operatore conclusione' non deve essere valorizzato quando l'esito è '" + esitoDescr + "'" );
			     return false;
			}
	    }else  if (selEsito.value == "RIF" || selEsito.value == "RES" || selEsito.value == "CAN") {       
	    	if (document.Frm1.prgOperatorePROPSpi.value == '') {
			     alert('Il campo Operatore proposta è obbligatorio.');
			     document.Frm1.prgOperatorePROPSpi.focus();
			     return false;
			}
	    	 if (document.Frm1.prgOperatoreAVVSpi.value !== '') {
	    		 alert("Il campo 'Operatore avvio' non deve essere valorizzato quando l'esito è '" + esitoDescr + "'" );
			     return false;
			}
	    	if (document.Frm1.prgOperatoreCONCSpi.value == '') {
			     alert('Il campo Operatore conclusione è obbligatorio.');
			     document.Frm1.prgOperatoreCONCSpi.focus();
			     return false;
			}
	    }else if (selEsito.value == "FC" || selEsito.value == "INT"
    			|| selEsito.value == "NR" || selEsito.value == "PR"
    			|| selEsito.value == "DEC" || selEsito.value == "ESN"
    			|| selEsito.value == "TRA" || selEsito.value == "ESC") 
	    {       
	    	if (document.Frm1.prgOperatorePROPSpi.value == '') {
			     alert('Il campo Operatore proposta è obbligatorio.');
			     document.Frm1.prgOperatorePROPSpi.focus();
			     return false;
			}
	    	if (document.Frm1.prgOperatoreAVVSpi.value == '') {
			     alert('Il campo Operatore avvio è obbligatorio.');
			     document.Frm1.prgOperatoreAVVSpi.focus();
			     return false;
			}
	    	if (document.Frm1.prgOperatoreCONCSpi.value == '') {
			     alert('Il campo Operatore conclusione è obbligatorio.');
			     document.Frm1.prgOperatoreCONCSpi.focus();
			     return false;
			}
	    }else{
	    	if (document.Frm1.prgOperatorePROPSpi.value !== '') {
	    		 alert("Il campo 'Operatore proposta' non deve essere valorizzato quando l'esito è '" + esitoDescr + "'" );
			     return false;
			}
	    	if (document.Frm1.prgOperatoreAVVSpi.value !== '' ) {
	    		 alert("Il campo 'Operatore avvio' non deve essere valorizzato quando l'esito è '" + esitoDescr + "'" );
			     return false;
			}
	    	 if (document.Frm1.prgOperatoreCONCSpi.value !== '' ) {
	    		 alert("Il campo 'Operatore conclusione' non deve essere valorizzato quando l'esito è '" + esitoDescr + "'" );
			     return false;
			}
	    }	
	}
	
  		return true;
	}
 
  function cittadinanzaCheck(cittCheck) {
    var codEs = document.Frm1.codEsito.value;
    if (cittCheck) {
      if (codEs == 'FC' || codEs == 'PR'  || codEs == 'TRA'  || codEs == 'ESC') {
        alert("Attenzione! E' necessario modificare la cittadinanza con un valore compatibile con le codifiche ministeriali per poter salvare queste modifiche.");
        return false;
      } 
    }
    return true;  
  } 

  --></SCRIPT>
  <script>
    <!--
    function showAzioni(){
        // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
    
        document.Frm1.MODULE.value="";
        doFormSubmit(document.Frm1);        
    }

    function apriDettaglioVoucher(url) {
      var f = "AdapterHTTP?PAGE=DettaglioScadenzaVoucherPage&" + url;
      var t = "Dettaglio Voucher";      
      var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=500,top=250,left=250";
      window.open(f, t, feat);
  }
    function apriDettaglioCondizionalita(url) {
    	var f = "AdapterHTTP?PAGE=ListaAzioniCondizionalitaPage&" + url;
        var t = "Elenco Eventi Condizionalità";      
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=400,top=250,left=250";
        window.open(f, t, feat);
    } 
  function apriCheckUtenteYG() {
    var f = "AdapterHTTP?PAGE=CheckUtenteYGPage&CDNLAVORATORE=<%=cdnLavoratore %>";
      var t = "VerificaPresaInCaricoMin";     
      var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=950,height=180,top=250,left=250";
    window.open(f, t, feat);  
  }

  function apriCheckDataAdesioneYG() {
    var f = "AdapterHTTP?PAGE=CheckDataAdesioneYGPage&CDNLAVORATORE=<%=cdnLavoratore %>";

    var p = "";
    var len = document.Frm1.sceltaRegioni.length;
    var i;
    for (i = 0; i < len; i++) {
      if (document.Frm1.sceltaRegioni[i].checked) {
        p = document.Frm1.sceltaRegioni[i].value;
        break;
      }
    }
    f = f + "&cercaIn=" + p;

    var t = "VerificaDataAdesionePortale";      
      var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=700,height=300,top=250,left=250";
    window.open(f, t, feat);    
  }
  
    function verificaAzioneProgramma(){
        // 1 programma per azione
        <%if (nuovo) {  %>
      if(!document.Frm1.infoProg || document.Frm1.infoProg.value==null || document.Frm1.infoProg.value=="" ){
              return true;
        } else {
          return confirm("Il programma sarà inserito per ogni " + azioneJS + " scelta.\nVuoi proseguire?"); 
      }   
        <%} %>
        return true;
    }

    function cercaTirocinioApprendistato(cdnLav) {
        var urlpage="";
        urlpage+="AdapterHTTP?";
        urlpage+="cdnLavoratore="+cdnLav+"&";
        urlpage+="PAGE=MovimentiEntePromotorePage";
        opened = window.open(urlpage,"RicercaProgrammi", 'toolbar=0, scrollbars=1,width=800,height=400'); 
    }

    function checkCodiceFiscale(inputName) {
      var abilitaBottoneEnteProm = false;
      var flgNonModificareCurr = '<%=flgNonModificare%>';
    if (flgNonModificareCurr != 'S') {
      var codiciObj = eval('document.Frm1.prgAzioni');
      for (var i=0;i<codiciObj.options.length;i++) {
        if (codiciObj.options[i].selected) {
                for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
            if (vTipoAttivitaIDAzione[j] == codiciObj.options[i].value) {
              if (vTipoAttivitaAzione[j] == 'C06' ||
                vTipoAttivitaAzione[j] == 'D01' ||
                vTipoAttivitaAzione[j] == 'E01' ||
                vTipoAttivitaAzione[j] == 'E02' ||
                vTipoAttivitaAzione[j] == 'E03') {
                abilitaBottoneEnteProm = true;
              }
            }
                }
              }
          }
    }
    else {
      var codiciSel = document.Frm1.prgAzioni.value;
      for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
        if (vTipoAttivitaIDAzione[j] == codiciSel) {
          if (vTipoAttivitaAzione[j] == 'C06' ||
            vTipoAttivitaAzione[j] == 'D01' ||
            vTipoAttivitaAzione[j] == 'E01' ||
            vTipoAttivitaAzione[j] == 'E02' ||
            vTipoAttivitaAzione[j] == 'E03') {
            abilitaBottoneEnteProm = true;
          }
        }
          }
    }
    
      if (abilitaBottoneEnteProm && document.Frm1.strCodiceFiscaleEnte.value != '') {
          var cfObj = eval("document.Frm1." + inputName);
          cfObj.value=cfObj.value.toUpperCase();
          cf=cfObj.value;
          ok=true;
          msg="";
          if (cf.length==16 ) {
            for (i=0; i<16 && ok; i++) {
                c=cf.charAt(i);
                  if (i>=0 && i<=5){
                    ok=!isDigit(c);
                      msg="Errore nei primi sei caratteri del codice fiscale:\ndevono essere delle lettere";
                  } else if  (i==6 || i==7) { 
                      ok=isDigit(c);
                      msg="Errore nel settimo o nell'ottavo carattere del codice fiscale:\ndevono essere numeri";
                  } else if (i==8) {
                      ok=!isDigit(c);
                      msg="Errore nel nono carattere del codice fiscale:\ndeve essere una lettera";
                  } else if (i==9 || i==10) {
                      ok=isDigit(c);
                    msg="Erore nel decimo o nell'undicesimo carattere del codice fiscale";
                  } else if (i==11) {
                      ok=!isDigit(c);
                      msg="Errore nell'undicesimo carattere del codice fiscale:\ndeve essere una lettera";
                  } else if (i>=12 && i <=14) {
                      ok=isDigit(c);
                      msg="Errore nel tredicesimo, nel quattordicesimo o nel penultimo carattere del codice fiscale:\ndevono essere dei numeri";
                  } else if (i==15) {
                      ok=!isDigit(c);
                      msg="Errore nell'ultimo carattere del codice fiscale:\ndeve essere una lettera";
                  }
              }
          } 
          else {
            if (cf.length==11) {
                var regEx = /^[0-9]{11}/;
                  if (cf.search(regEx)==-1) { 
                    msg="Se di 11 cifre il codice fiscale deve essere solo numerico";
                      ok=false;
                  }
            }
            else {
                ok=false;
                msg="Il codice fiscale deve essere di 11 o di 16 caratteri";
            }
          }
         
          if (!ok) {
              alert(msg);
              cfObj.focus();
          }
          return ok;
        }
      else {
          return true;
      }
    }

    function stampa(){
    	apriGestioneDoc('RPT_EROGAZIONE_SERVIZIO', '&pagina=<%=_page%>&cdnLavoratore=<%=cdnLavoratore%>&prgColloquio=<%=prgColloquio%>&prgPercorso=<%=prgPercorso%>&strchiavetabella=<%=prgPercorso%>','EROSER')
	}  

    function stampa_rei(){
    	apriGestioneDoc('RPT_BUONO_REIMPIEGO', '&pagina=<%=_page%>&cdnLavoratore=<%=cdnLavoratore%>&strchiavetabella=<%=cdnLavoratore%>','REI')
	}

    <%if(canViewCampoCodEnte){ %>
    function checkOperatoreModifica() {
    	if (tipoGruppoUtCollegato != tipoGruppoUtS && document.Frm1.codEnte.value != '') {
    		alert("Operazione non consentita");
          	document.Frm1.codEnte.options.selectedIndex=0;
    	}
    }	
    <%} %>
        
    -->
  </script>
  <script src="../../js/ComboPair.js"></script>

<script language="Javascript">
    if(window.top.menu != undefined)
       window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
<script language="Javascript">
<%//if (!nuovo)
      attributi.showHyperLinks(out, requestContainer, responseContainer,
          "cdnLavoratore=" + cdnLavoratore + "&PRGCOLLOQUIO="
              + prgColloquio);%>
</script>

<script>
  
  function controllaYei() {
    
    var codTipologiaDurata = document.getElementsByName("CODTIPOLOGIADURATA")[0].value;
    var numYgDurataMin = document.getElementsByName("NUMYGDURATAMIN")[0].value;
    var numYgDurataMax = document.getElementsByName("NUMYGDURATAMAX")[0].value;
    var numYgDurataEff = document.getElementsByName("NUMYGDURATAEFF")[0].value;

    if (numYgDurataMin != '' || numYgDurataMax != '' || numYgDurataEff != '') {
      if (codTipologiaDurata == '') {
        alert("E' obbligatorio indicare la tipologia durata.");
        return false;
      }
    }
    
    if (checkDurataAz_Tutte == 'true') {
      if (numYgDurataEff == '' && (document.Frm1.codEsitoRendicont.value == "E" || document.Frm1.codEsitoRendicont.value == "ENR")) {
        alert("La durata effettiva è obbligatoria per questa " + azioneJS + " erogata.");
        return false;
      }
    }
    else {
      if (checkAzioniFlgFormazioneS()) {
        if (numYgDurataEff == '' && (document.Frm1.codEsitoRendicont.value == "E" || document.Frm1.codEsitoRendicont.value == "ENR")) {
          alert("La durata effettiva è obbligatoria per questa " + azioneJS + " erogata.");
          return false;
        }
      }
    }
    return true;
  }
  
  
  function controllaProtInpsRDC(){
	 	var isNuovo = <%=nuovo%>;
		var isMod = <%=canModify%>;
		var checkRdc = <%=canViewRDC%>;
		if (isMod && checkRdc) {
			if (isNuovo) {
		  		var codiciSel = document.Frm1.prgAzioni.value;
				for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
					var prgReddCitt = document.Frm1.prgRDC.value;
				    if (vTipoAttivitaIDAzione[j] == codiciSel) {
				      if (vTipoAttivitaAzione[j] == 'A02' && vTipoProgettoAzione[j] == '08') {
				    	 if(prgReddCitt == null || prgReddCitt ==''){
				    		 return confirm("Attenzione: non è stata collegata l'azione alla notifica RDC del beneficiario tramite il campo \"Protocollo INPS\" da ricercare a sistema.\nVuoi proseguire?"); 
				    	 }
				      }
				    }
				}
			}
			else {
				var isAzionePresaInCarico = false;
				var codiciObj = eval('document.Frm1.prgAzioni');
		    	for (var i=0;i<codiciObj.options.length;i++) {
		      		if (codiciObj.options[i].selected) {
		              for (var j = 0; j < vTipoAttivitaIDAzione.length; j++) {
		            	var prgReddCitt = document.Frm1.prgRDC.value;
		          		if (vTipoAttivitaIDAzione[j] == codiciObj.options[i].value) {
		          			console.log(vTipoAttivitaAzione[j]);
		          			 if (vTipoAttivitaAzione[j] == 'A02' && vTipoProgettoAzione[j] == '08') {
			            		if(prgReddCitt == null || prgReddCitt ==''){
			            			return confirm("Attenzione: non è stata collegata l'azione alla notifica RDC del beneficiario tramite il campo \"Protocollo INPS\" da ricercare a sistema.\nVuoi proseguire?"); 
						    	 }
			            	}
			          	}
		              }
		            }
		        }
			}
		}
		return true;
	  }
  
</script>

</head>

<body class="gestione" onload="rinfresca();toggleVisDatEffettiva('FALSE');inizializza_esiti();requireDatEffettiva();caricaDatiAzioni();toggleVisAzioneDiGruppo();abilitaEntePromotore();toggleVisGaranziaGiovani();checkDataDichiarazione();">
  <script language="javascript">
    var flgInsert = <%=nuovo%>;
  </script>

<%
  if (!onlyInsert && !tmp) {
    InfCorrentiLav _testata = new InfCorrentiLav(RequestContainer
        .getRequestContainer().getSessionContainer(),
        cdnLavoratore, user);
    _testata.show(out);
    Linguette _linguetta = new Linguette(user,
        Integer.parseInt(cdnFunzione), "LISTACOLLOQUIPAGE",
        new BigDecimal(cdnLavoratore));
    _linguetta.show(out);
  }
%>

  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controlla() && rilevaModificheAzione() && controllaStatoAtto(flgInsert,this) && confermaAggiornaDataScadPatto() && controllaProtInpsRDC()" >

      <input type="hidden" name="PAGE" value="PERCORSIPAGE">
      <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
      <input type="hidden" name="prgProgrammaq" value="<%=prgProgrammaq%>">
      <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
      <input type="hidden" name="PRGCOLLOQUIO" value="<%=prgColloquio%>"/>
      <input type="hidden" name="pageChiamante" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,
            "PAGECHIAMANTE")%>">
      <input type="hidden" name="AZIONECAMBIATA" value="">
      <input type="hidden" name="CONPATTO" value="">
      <input type="hidden" name="descAzioneDaModificare" value="<%=azioneDescrizione%>">
      <input type="hidden" name="RINNOVAPATTO" value="">
      <input type="hidden" name="PRGPATTOPROROGA" value="<%=Utils.notNull(prgPattoProroga)%>">
      <input type="hidden" name="NUMKLOPATTOPROROGA" value="<%=Utils.notNull(numkloPattoProroga)%>">
      <input type="hidden" name="DATASTIPULAPROROGA" value="<%=Utils.notNull(dataStipula)%>">
      <input type="hidden" name="DATSCADENZAPROROGA" value="<%=Utils.notNull(dataScadConferma)%>">
      <input type="hidden" name="CODSTATOPATTOPROROGA" value="<%=Utils.notNull(codStatoPattoAperto)%>">
      <input type="hidden" name="FLGPATTO297PROROGA" value="<%=Utils.notNull(flgPatto297Aperto)%>">
      <input type="hidden" name="CODTIPOPATTOPROROGA" value="<%=Utils.notNull(codTipoPattoAperto)%>">
      <input type="hidden" name="CODCPIPATTOPROROGA" value="<%=Utils.notNull(codCpiPattoAperto)%>">
      <input type="hidden" name="codServizio"  value="<%=Utils.notNull(codServizio) %>" />
      <input type="hidden" name="datInizioProgramma"  value="<%=Utils.notNull(dataColloquioPercorso) %>" />
      
      <%=QueryString.toInputHidden(serviceRequest,
            "DA_PATTO_COD_LST_TAB")%>
      <%
        if (!nuovo) {
      %>
         <input type="hidden" name="PRGPERCORSO" value="<%=prgPercorso%>">   
      <%
          }
         %>
      <%
        if (onlyInsert || tmp) {
      %>
        <input type = "hidden" name="ONLY_INSERT" value="1">          
        <input type = "hidden" name="statoSezioni" value="<%=StringUtils.getAttributeStrNotNull(
              serviceRequest, "statoSezioni")%>">
        <input type = "hidden" name="NONFILTRARE" value="<%=nonFiltrare%>">                  
      <%
                          }
                        %>
      <center>
        <af:showMessages prefix="M_DeletePercorso"/>
        <af:showMessages prefix="M_InsertPercorso"/>
        <af:showMessages prefix="M_UpdatePercorso"/>
        <af:showErrors/>
      </center>  
      <div align="center">

      <af:list moduleName="M_ListPercorsi" skipNavigationButton="1"
               configProviderClass="it.eng.sil.module.patto.DynamicEstrazionePercorsiConfig" 
               canDelete="<%=canDelete ? \"1\" : \"0\"%>"  
               jsSelect="dettaglio" jsDelete="cancella"/>
      
		<%
              if (canInsert) {
            %>
            <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuovo Percorso"/>        
      <%
                }
              %>
      &nbsp;&nbsp;
        <%
          if (!onlyInsert && !tmp) {
        %>
          <input type="button" class="pulsanti" onClick="indietro();" value="Chiudi"/>              
        <%
                        } else {
                      %>
             <button onclick="indietroPopUpAssociazione()" class="pulsanti">Indietro</button>
        <%
          }
        %>
      &nbsp;&nbsp;
        <%
          if (canStampaBuonoReimpiego) {
        %>        
        	<input name="StampaRei" type="button" class="pulsanti" value="Stampa" onclick="stampa_rei()"> 
        <% 
          }
          if (canListaCompleta) {
        %>
        <p><center>
          <input type="button" class="pulsanti" onClick="listaCompletaAzioniConcordate();" value="Lista Completa" />
        </center></p>
        <%
          }
        %>
        
        <p><center>
    <%
      /* if (provenienzaListaAzioniConcordate) {
                                 if (sessionContainer!=null){
                                 String token = "_TOKEN_" + "AZIONICONCORDATELISTAPAGE";
                                 String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
                                 if (urlDiLista!=null){
                                 out.println("<a href=\"" +
                                 "AdapterHTTP?"+ urlDiLista + "\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></a>");
                                 }
                                 }
                                 }*/
    %> </center>
        </p>       
      </div>
  <%
    String divStreamTop = StyleUtils.roundLayerTop(canModify);
      String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
  %>

      <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
       style="position:absolute; width:100%; left:10; top:200px; z-index:6; display:<%=apriDiv%>;">

    <%
      out.print(divStreamTop);
    %>
        <table width="100%">
          <tr>
            <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
            <td height="16" class="azzurro_bianco">
            <%
              if (nuovo) {
            %>
              Nuovo percorso
            <%
              } else {
            %>
              Percorso
            <%
              }
            %>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>
    <br>
       <table class="main">
       
       		<tr><td class="etichetta">CPI</td>
		      	<td class="campo"><%=descrcpi%></td>
		      </tr>
       
          <tr>
            <td class="etichetta"><%=misura %></td>
                <%
                 
                    ComboPair docComboPair = new ComboPair(rows, prgAzioni,
                        "prgAzioneRagg");
                    prgAzioneRagg = docComboPair.getCodRef();
                    
                %>
	                <td class="campo"><af:comboBox
	                    name="prgAzioneRagg"
	                    disabled="<%=fieldReadOnly%>"  
	                        moduleName="<%=azioniModuleName%>" 
	                        selectedValue="<%= prgAzioneRagg %>" addBlank="true" required="true" classNameBase="input" title="<%=misura%>"/>
	                </td>            
            </tr>
              <%--if (azioni!=null){--%>
            <tr>
                <td class="etichetta"><%=azione %></td>
                <td class="campo" nowrap>
                <%
                  if (fieldReadOnly.equals("false")) {
                      if (nuovo) {
                %>
                        <af:comboBox  multiple="true" size="5" onChange="fieldChanged();toggleVisAzioneDiGruppo();abilitaEntePromotore();toggleVisGaranziaGiovani();checkDataDichiarazione();" 
                          disabled="<%=fieldReadOnly%>" name="prgAzioni" 
                         addBlank="false" required="true" title="<%=azione %>"/>
                    <%
                      } else {
                    %>
                        <af:comboBox disabled="<%=fieldReadOnly%>" name="prgAzioni"  
                            selectedValue="<%= prgAzioni %>" moduleName="M_DeAzioni_NoGroup" classNameBase="input"
                            addBlank="true" required="true" title="<%=azione%>"/>
                    <%
                      }
                    %>
                </td>
                <script>
                var arrayFiglio = new Array();                
                <%=docComboPair.makeArrayJSChild()%>
                var comboPair = new ComboPair(document.Frm1.prgAzioneRagg, document.Frm1.prgAzioni, arrayFiglio,true);
                comboPair.populate('<%=docComboPair.getCodRef()%>', '<%=prgAzioni%>');
               
                </script>
                <%
                  } else {
                %>                
                        <af:comboBox disabled="true" name="prgAzioni" selectedValue="<%= prgAzioni %>" moduleName="M_DeAzioni_NoGroup" 
                          classNameBase="input" addBlank="true" required="true" title="<%=azione%>"/>
                <%
                  }
                %>
     
           <%if (!nuovo && isDescrPrestazione) {  %>
            <tr>
	         	 <td class="etichetta">Prestazione</td>
		         <td class="campo">
		                <af:textBox  title="Prestazione" classNameBase="input" readonly="true"  type="text"
		                    name="strPrestazione"   value="<%= strPrestazione%>"  size="80"/>
		      	 </td>
      		</tr>
          	<%
               }
             %>
      <%if (!nuovo && flgNonModificare.equalsIgnoreCase("S")) {
        String strflgGruppo = "";
        if ("S".equalsIgnoreCase(flgGruppo)) {
          strflgGruppo = "Sì";  
        }
        else {
          if ("N".equalsIgnoreCase(flgGruppo)) {
            strflgGruppo = "No";  
          }
        }
        if (flgFormazioneCurr.equalsIgnoreCase("S")) {%>
          <tr>
            <td colspan="2">
              <table class="main">
              <tr>
              <td class="etichetta">Azione di Gruppo</td>
              <td style="width:5%" class="campo">
                <af:textBox name="flgGruppoVis" classNameBase="input" size="3" type="text" value="<%= strflgGruppo %>" readonly="true"/>
                <input type="hidden" name="flgGruppo" value="<%= flgGruppo %>"/>
              </td>
              <td style="width:5%" align="left">*</td>
              <% if ("S".equalsIgnoreCase(flgGruppo)) {%>
                <td>
                  <table class="main">
                  <tr>
                    <td style="width:60%" class="etichetta">Numero Partecipanti</td>
                    <td style="width:5%" class="campo" nowrap>
                      <af:textBox name="numPartecipantiVis" classNameBase="input" size="4" type="integer" value="<%= numPartecipanti %>" readonly="true"/>
                      <input type="hidden" name="numPartecipanti" value="<%= numPartecipanti %>"/>
                    </td>
                  </tr>
                  </table>
                </td>
              <% } else {%>
                <td>
                  <input type="hidden" name="numPartecipanti" value="<%= numPartecipanti %>"/>
                </td>
              <% }%>
              </tr>
              </table>
            </td>
          </tr>
          
          <tr>
          <td colspan="2">
          <table class="main">
            <tr>
              <td class="etichetta">Adesione</td> 
              <td class="campo">
                <af:comboBox onChange="fieldChanged();" disabled="true" name="prgKeyAdesione"
                            moduleName="<%=moduloAdesione%>" selectedValue="<%= prgKeyAdesione %>"
                            addBlank="true" required="false" classNameBase="input" title="Adesione"/>
                </td>
            </tr>
          </table>
          </td>
          </tr>
          
        <%} else {%>
          <tr>
          <td>
            <input type="hidden" name="flgGruppo" value="<%= flgGruppo %>"/>
            <input type="hidden" name="numPartecipanti" value="<%= numPartecipanti %>"/>
            <input type="hidden" name="prgKeyAdesione" value=""/>
          </td>
          </tr>
        <%}%>
      <%} else {%>
          <tr>
          <td colspan="2">
          <div id="divAzioneDiGruppo" style="display: none">
            <table class="main">
              <tr>
                <td class="etichetta">Azione di Gruppo</td> 
                <td style="width:5%" class="campo">
                	  <af:comboBox onChange="fieldChanged();toggleVisNumPartecipanti();"  name="flgGruppo" classNameBase="input" 
                            disabled="<%=fieldReadOnly%>"  
                              moduleName="M_GenericComboSiNo" selectedValue="<%= flgGruppo %>"
                              addBlank="true" required="false"  title="Azione di Gruppo"/>
                  </td> 
                  <td style="width:5%" align="left">*</td>  
                  <td>            
                <div id="divNumPartecipanti" style="display: none">
                <table class="main">
                  <tr>
                    <td style="width:60%" class="etichetta">Numero Partecipanti</td>
                    <td style="width:5%" class="campo" nowrap>
                      <af:textBox 
                            name="numPartecipanti" 
                            classNameBase="input" 
                        size="4" 
                        maxlength="4"
                        type="integer"
                        validateOnPost="true"
                        onKeyUp="fieldChanged();" 
                        value="<%= numPartecipanti %>" 
                        readonly="<%=fieldReadOnly%>"
                        title="Numero Partecipanti"/></td>
                    <td style="width:35%" align="left">*</td>
                  </tr>
                </table>
                </div>
                </td>
              </tr>
            </table>
          </div>
          </td>
        </tr>
        
        <tr>
          <td colspan="2">
          <div id="divSceltaAdesione" style="display: none">
            <table class="main">
              <tr>
                <td class="etichetta">Adesione</td> 
                <td class="campo">
                  <af:comboBox onChange="fieldChanged();"
                            disabled="<%=fieldReadOnly%>" name="prgKeyAdesione"  
                              moduleName="<%=moduloAdesione%>" selectedValue="<%= prgKeyAdesione %>"
                              addBlank="true" required="false" classNameBase="input" title="Adesione"/>
                  </td>
              </tr>
            </table>
          </div>
          </td>
        </tr>
      <%}
    
          if (checkCIGColl.equals("true")) {%>
        <input type="hidden" name="showCampiCIG" value="true"/>
        <tr>
            <td class="etichetta">Prestazione CIG</td>
            <td class="campo">
            <af:comboBox onChange="fieldChanged();" disabled="<%=fieldReadOnly%>"  name="CODSERVIZICIG"  
                selectedValue="<%=codServiziCIG %>" moduleName="M_Prestazione_CIG" classNameBase="input"
                addBlank="true" required="true" title="Prestazione CIG"/> 
          </td>
        </tr>
      <tr>
      <td class="etichetta">Presenza di mediatore culturale</td> 
      <td class="campo">
              <af:comboBox   name="flgMediatore" classNameBase="input" 
                            disabled="<%=fieldReadOnly%>"  
                              moduleName="M_GenericComboSiNo" selectedValue="<%= flgMediatore %>"
                              addBlank="true" required="false"  title="Presenza di mediatore culturale"/>
        </td> 
      </tr>   
      <tr>
      <td class="etichetta">Lavoratore con disabilit&agrave;</td> 
      <td class="campo">
        <af:comboBox   name="flgAbilita" classNameBase="input" 
                            disabled="<%=fieldReadOnly%>"  
                              moduleName="M_GenericComboSiNo" selectedValue="<%= flgAbilita %>"
                              addBlank="true" required="false"  title="Lavoratore con disabilita"/>
        </td> 
      </tr>                 
        <%} %>
        
        
          
      <tr>
          <td class="etichetta">Data stimata</td>
          <td class="campo">
                <af:textBox onKeyUp="fieldChanged();" title="Data stimata" classNameBase="input" readonly="<%=fieldReadOnly%>"  type="date"
                    name="datStimata"  validateOnPost="true" required = "true" value="<%= dataStimata%>" inline="onChange='controlloRange(this)'" 
                    size="12" maxlength="10"/>
       </td>
      </tr>
      
      <tr>
          <td class="etichetta">Data avvio</td>
          <td class="campo">
                <af:textBox onKeyUp="fieldChanged();" title="Data avvio" classNameBase="input" readonly="<%=fieldReadOnly%>"  type="date"
                    name="datAvvioAzione" validateOnPost="true" value="<%= datAvvioAzione%>" size="12" maxlength="10"/>
       </td>
      </tr>
      
      
      <tr>
      <td colspan="2">
          <div id="divDataDichirazione" style="display:none">
          <table class="main">
            <tr>
              	<td class="etichetta">Data dichiarazione</td>
              	<td class="campo">
                <af:textBox onKeyUp="fieldChanged();" title="Data dichiarazione" classNameBase="input" readonly="<%=fieldReadOnly%>"  type="date"
                    name="datDichAzione" validateOnPost="true" value="<%= datDichAzione%>" size="12" maxlength="10"/>
       			</td>
            </tr>
          </table>
          </div>
     </td>
     </tr>
      
      
      
      
      <tr>
          <td class="etichetta">&nbsp;</td>
          <td class="campo">
              <div id="divEnteFields" style="display:none">
              <input type="button" class="pulsanti" name="btnEntePromotore" value="CF Azienda Ospitante" <%=(("false").equalsIgnoreCase(fieldReadOnly)?"":"disabled=\"True\"")%> onClick="cercaTirocinioApprendistato(<%=cdnLavoratore%>);">
              <af:textBox name="strCodiceFiscaleEnte"  value="<%= cfEntePromotorePercorso%>" title="CF Azienda Ospitante" size="18" maxlength="16" 
          classNameBase="input" validateWithFunction="checkCodiceFiscale" readonly="<%=fieldReadOnly%>" />
            </div>
       </td>
      </tr>
      
          <tr>
            <td class="etichetta">Esito</td>
          <td class="campo" >
          
                  <af:comboBox disabled="<%=fieldReadOnly_flgNonModificare%>"  name="codEsito" classNameBase="input"
                      moduleName="M_DEESITO" selectedValue="<%= codEsito %>" addBlank="true" required="true" title="Esito"/>        
                         
          </td>          
      
          </tr> 
    <tr>
      <td colspan="2">
      <table class="main">
        <tr>
          <td class="etichetta">Esito rendicontazione</td>
          <td style="width:30%" class="campo"><af:comboBox
            onChange="cambiaEsitoRendicont();" disabled="<%=fieldReadOnly_flgNonModificare%>"
            name="codEsitoRendicont" moduleName="M_DEESITORENDICONT"
            selectedValue="<%= codEsitoRendicont %>" addBlank="true" classNameBase="input"
            required="true" title="Esito rendicontazione" /> <input
            type="hidden" name="codEsitoRendicontHid"
            value="<%=codEsitoRendicontDefault%>" /> <input type="hidden"
            name="codEsitoRendicontOld" value="<%=codEsitoRendicont%>" /></td>
          <td>
          <div id="divDataEffettiva" style="display: none">
          <table class="main">
            <tr>
              <td style="width:90%" class="etichetta" nowrap>Data conclusione effettiva/prevista</td>
              <td style="width:5%" class="campo" nowrap><af:textBox
                onKeyUp="fieldChanged();" title="Data effettiva"
                classNameBase="input" readonly="<%=fieldReadOnly_flgNonModificare%>" type="date"
                name="datEffettiva" validateOnPost="true" required="false"
                value="<%= dataEffettiva%>"
                inline="onChange='controlloRange(this)'" size="12"
                maxlength="10" /></td>
              <td style="width:5%" id="datEffettivaMandatory"></td>
            </tr>
          </table>
          </div>
          </td>
        </tr>
      </table>
      </td>
      
    </tr> 
   
       <input type="hidden" name="isRedditoCittadinanza" value="<%= canViewRDC %>"/>
      <af:textBox type="hidden"	name="prgRDC" value="<%=prgRedditoCitt  %>" />
      <%if(canViewRDC){ %>
      		<%@ include file="../notificheRDC/RicercaNotificheRDC.inc" %>
 			<tr>
            <td class="etichetta">Protocollo INPS</td>
                
                <td class="campo"> 
                <af:textBox
                onKeyUp="fieldChanged();" title="Protocollo INPS"
                classNameBase="input" readonly="true" type="text"
                name="rdcProtocolloINPS" validateOnPost="true" required="false"
                value="<%=protInpsRedditoCitt %>"
                size="45"
                maxlength="110" />
                <%if(canModify){ %>
              
                <A
					HREF="javascript:btFindNotificaRDC_onclick(document.Frm1.prgRDC);"><IMG
					name="image" border="0" src="../../img/binocolo.gif"
					alt="cerca notifica rdc"/></a>&nbsp; 
					
                  <%} %>
                </td>
            </tr>
     <%}%>
        <%if(canViewCampoCodEnte){ %>
    <tr>
    	<td class="etichetta">Operatore Modifica</td> 
      	<td class="campo">
      	
        	<af:comboBox name="codEnte" classNameBase="input" title="Operatore Modifica" 
        	onChange="fieldChanged();checkOperatoreModifica();"
        	 required="<%=strRequiredCampoCodEnte%>" disabled="<%=strDisabledCampoCodEnte%>"      
            	moduleName="M_GenericComboSiNo" selectedValue="<%= flgAbilita %>" addBlank="true"/>
        </td>
	</tr>     
	<%}else{%>
		<input type="hidden" name="codEnte" value="<%=codEnte%>"/>
	<%}%>    
    <input type="hidden" name="isGestioneOperatoriAzione" value="<%= isGestioneOperatoriAzione %>"/>
          <%if(isGestioneOperatoriAzione){ %>
 			<tr>
            <td class="etichetta">Operatore proposta</td>
                
                <td class="campo"><af:comboBox
                    name="prgOperatorePROPSpi"
                    disabled="<%=fieldReadOnly%>"  
                    	 addBlank="true"
                        moduleName="M_GET_OPERATORI_SPI" 
                        selectedValue="<%= prgOperatorePROPSpi %>"
                        required="<%=reqOpPROPSpi %>" classNameBase="input"
                        onChange="fieldChanged();" 
                        title="Operatore proposta"/>
                </td>
            </tr>
             <tr>
            <td class="etichetta">Operatore avvio</td>
                
                <td class="campo"><af:comboBox
                    name="prgOperatoreAVVSpi"
                    disabled="<%=fieldReadOnly%>"  
                     addBlank="true"
                        moduleName="M_GET_OPERATORI_SPI" 
                        selectedValue="<%= prgOperatoreAVVSpi %>"
                        required="<%=reqOpAVVSpi %>" classNameBase="input"
                        onChange="fieldChanged();" 
                        title="Operatore avvio"/>
                </td>
            </tr>
            <tr>
            <td class="etichetta">Operatore conclusione</td>
                <td class="campo"><af:comboBox
                    name="prgOperatoreCONCSpi"
                    disabled="<%=fieldReadOnly%>"  
                     addBlank="true"
                        moduleName="M_GET_OPERATORI_SPI" 
                        selectedValue="<%= prgOperatoreCONCSpi %>"
                        required="<%=reqOpCONCSpi %>" classNameBase="input"
                        onChange="fieldChanged();" 
                        title="Operatore conclusione"/>
                </td>
            </tr>
            <%}else{%>
            	<input type="hidden" name="prgOperatorePROPSpi" value=""/>
            	<input type="hidden" name="prgOperatoreAVVSpi" value=""/>
            	<input type="hidden" name="prgOperatoreCONCSpi" value=""/>
            <%}%>
    
    <tr>
            <td class="etichetta">Note</td>
            <td class="campo">
                <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strnote" maxlength="500" cols="60" rows="5" value="<%=strNote%>" readonly="<%=fieldReadOnly%>" />
            </td>
        </tr>
        
        <!--  Garanzia Giovani -->
    <tr>
      <td colspan=2> 
        <div id="divGaranziaGiovani" style="display: none">               
               <table id="sezione_YG" width="100%" border=0>
             <tr>
                <td colspan="4">    
                    <table class='sezione' cellspacing=0 cellpadding=0>
                <tr>          
                  <td width=18></td>          
                  <td class="sezione_titolo">Garanzia Giovani</td>
                </tr>
              </table>
            </td>
          </tr>              
          <tr>
              <td colspan=4 align="center">
                  <table id="T_S_YG" style="width:100%;">     
                    <tr>
                      <td class="etichetta">Data Adesione</td>
                      <td class="campo">
                            <af:textBox onKeyUp="fieldChanged();" title="Data Adesione" classNameBase="input" readonly="<%=fieldReadOnly%>"  type="date"
                              name="datAdesioneGG" validateOnPost="true" value="<%= datAdesioneGG %>" size="12" maxlength="10"/>
                  </td>
                  </tr>
                          <%if (canCheckDataAdesioneYG && canModify) {%>
                          <tr>
                  <td class="etichetta">
                    <input type="button" onclick="apriCheckDataAdesioneYG()" value="Verifica data adesione (Portale)" class="pulsanti">
                  </td>
                  <td class="campo">
                    <input type="radio" name="sceltaRegioni" value="MiaReg" CHECKED/> solo mia Regione 
                    &nbsp;&nbsp;&nbsp;
                          <input type="radio" name="sceltaRegioni" value="TutteReg"/> tutte le Regioni
                        </td>           
                </tr> 
                <%} %>            
                          <%if (canCheckUtenteYG) {%>
                          <tr>
                  <td class="etichetta">
                    <input type="button" onclick="apriCheckUtenteYG()" value="Verifica presa in carico (Min.)" class="pulsanti">
                  </td>
                </tr> 
                <%} %>            
                  </table>
              </td>
          </tr>              
               </table>
              </div>
            </td>
        </tr>         
        
        <%if (canProgramma) {%>                                            
          <tr>
            <td colspan=2>
          <table   id="sezione_programma" width="100%" border=0>
          <tr>
            <td colspan="4">
               
              <table class='sezione' cellspacing=0 cellpadding=0>
                <tr>  
                    <td  width=18><img id='I_SEL_S_Programma' src='<%=imgSelSezioneProgramma%>' onclick='cambia(this, document.getElementById("T_S_Programma"))'></td>                
                  <td class="sezione_titolo">Programma - Corsi</td>   
                  <td class="sezione_pulsante"><a  href='#' alt='Legame col Programma' onclick='EsisteCorso()'><img src='../../img/binocolo.gif'></a> </td>
                          <td class="sezione_pulsante"><a  href='#' alt='Cancella' onclick='resetProgramma()'><img src='../../img/del.gif'></a></td>
                                  
                </tr>
              </table>
             </td>
          </tr>
          <tr>
            
              
             
              <td colspan=4 align="center">
                <TABLE id='T_S_Programma' style='width:100%;display:<%=displayProgramma%>'>  
                <script>initSezioni( new Sezione(document.getElementById('T_S_Programma'),
                           document.getElementById('I_SEL_S_Programma'),
                           <%=EsisteProgramma%>)
                    );
                </script>                        
                
                <tr>            
              <td class="etichetta2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Programma&nbsp;</td>        
              <td class=campo2>
                  <af:textBox type="text"
                          name="infoProg"
                          value="<%= it.eng.sil.util.Utils.notNull(infoProg)%>"
                          classNameBase="input"
                          
                          required="false"
                          readonly="true"
                          size="50"
                          maxlength="50"/>
                   
          </td>   
            <td class="etichetta2">Ente&nbsp;</td> 
              <td class=campo2>
                  <af:textBox type="text"
                              name="enteprog"
                              value="<%=it.eng.sil.util.Utils.notNull(enteProgramma)%>"
               
                              classNameBase="input"
                              required="false"
                              readonly="true"
                              size="80"
                              maxlength="80"/>
              </td>
           <td class="etichetta2">Stato&nbsp;</td> 
              <td class=campo2>
                  <af:textBox type="text"
                              name="statoprog"
                              value="<%=it.eng.sil.util.Utils.notNull(statoProgramma)%>"
               
                              classNameBase="input"
                              required="false"
                              readonly="true"
                              size="30"
                              maxlength="30"/>
              </td>
            
          
          </tr>
          <tr>
             <td class="etichetta2" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Data Inizio&nbsp;</td>
              <td class=campo2> 
                   <af:textBox type="text"
              
                              readonly="true"
                              required="false"
                              classNameBase="input"
                              name="datinizioprog"
                              value="<%= it.eng.sil.util.Utils.notNull(datinizio)%>"
                              size="12"
                              maxlength="12"/>    
              </td>  
              
              <td class="etichetta2" >Data Fine&nbsp;</td>        
              <td class=campo2>
                   <af:textBox type="text"
                           
                              readonly="true"
                              required="false"
                              classNameBase="input"
                              name="datfineprog"
                              value="<%= it.eng.sil.util.Utils.notNull(datfine)%>"
                              size="11"
                              maxlength="10"/>                 
          </td> 
            
              
          </tr>
                
                <br>
                <br>          
                  <%  if (esisteIscrCorsiProgramma) {   %>
                  <tr> 
                  <td colspan=4 align="center"> 
                        <br>
                      <af:list moduleName="M_GetCorsiIscritti" skipNavigationButton="1"  />
                 </td>    
                </tr>
                <%  } else if(!"".equals(prgProgrammaq)) { %>                       
                 <td  colspan=4 align="center"><br><br><input type="text" name="nesistecorso" class="viewRiepilogoTrasparent2nobold" readonly="true" value="Il lavoratore non è inscritto a nessun corso del Programma." size="80" /> </td> 
                
                <%  }  %>  
                  
                 </table>            
               </td>
           </tr>
           
                     
                         
               
      </table>
            </td>
          </tr> 
          
          <%}%>
            <!--  sez voucher -->
            <%
            if (numConfigVoucher.equalsIgnoreCase(Properties.CUSTOM_CONFIG) && !codStatoVoucher.equals("") && 
              !codStatoVoucher.equalsIgnoreCase(StatoEnum.ANNULLATO.getCodice())) {%>
              <tr>
        <td colspan=2>
                <%@ include file="Percorsi_Main_Voucher.inc" %>
              </td>
              </tr>
            <%}%>
            
            <!--  sez formazione -->
      <tr>
        <td colspan=2>               
                <%@ include file="Percorsi_Main_Formazione.inc" %>
              </td>
            </tr>
          
      <!--  durata YEI -->
      <tr>
        <td colspan=2>               
                <%@ include file="Percorsi_Main_Durata.inc" %>
              </td>
            </tr>
          
          <!--  associazione al patto -->
          <tr>
            <td colspan=2>               
            <%-- Savino 05/10/05: Soluzione temporanea al problema di una azione legata a due patti.
            Evito che cio' possa accadere impedendo di aprire la finestra della selezione del patto.
            Quando saranno modificate le pagine per il dettaglio di una azione, specifica di quel patto, allora
            si potra' ripristinare il vecchio/solito file di include "_associazioneDettaglioXPatto.inc" --%>
              <%@ include file="../patto/_associazioneDettaglioXPattoSoloAzioni.inc" %>
            </td>
          </tr>
          
          
          
          
          <% if (nuovo) {
                                                                      %>
          <tr>
            <td colspan="2" align="center">
            <input type="submit" class="pulsanti" name="inserisci" value="Inserisci" onclick="javascript: return (checkAzione1AeYG()&&checkDataEffettiva()&&verificaAzioneProgramma()&&controllaYei()&&checkOperatoriAzione())">
            <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
            </td>
          </tr>
          <%
            }
          %>          
          <%
                      if (!nuovo) {
                    %>
            <tr>
              <td colspan="2" align="center">
          		<%if (canStampaAttErogMisura) {%>              
					<input name="Stampa" type="button" class="pulsanti" value="Stampa" onclick="stampa()">        
    			<%}
                if (canModify) {
              %>
                <input type="submit" class="pulsanti" name="salva" value="Aggiorna" onclick="javascript: return (cittadinanzaCheck(<%=cittCheck%>) && checkAzione1AeYG() && checkDataEffettiva() && verificaAzioneProgramma() && controllaYei() && checkOperatoriAzione())">
              <%
                }
              %>
                <input type="button" 
                  class="pulsanti" 
                  name="annulla" 
                  value="<%=canModify ? "Chiudi senza aggiornare" : "Chiudi"%>" 
                  onClick="ChiudiDivLayer('divLayerDett')">
              </td>
            </tr>
      <tr>
        <td colspan="2" align="center">
        <%
          if (operatoreInfo != null)
                operatoreInfo.showHTML(out);
        %>
        </td>
      </tr>
          <%
            }
          %>
        </table>
      </div>
 <%
  out.print(divStreamBottom);
 %>              
    </af:form>
    
  
</body>

</html>