<%@page import="it.eng.sil.util.amministrazione.impatti.PattoBean"%>
<%@page import="it.eng.sil.coop.webservices.doteCMS.RicezioneDomandaDote"%>
<%@page import="it.eng.sil.module.movimenti.constant.Properties"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.PageAttribs,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,                   
                  it.eng.sil.util.*,
                  it.eng.sil.module.patto.bean.*,
                  it.eng.sil.module.movimenti.constant.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  java.lang.*,
                  java.text.*,
                  
                  it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil"   %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String skipControlli = "";
	boolean hasImpAssociati = false;
	boolean isProtocollato = false;
	boolean hasFormazioneAssPatto = false;
	boolean existAzioneGG = false;
	BigDecimal prgDocumento = null;
	String msgNoProtPatto = "Non è possibile stampare un patto/accordo se non ha impegni associati.";
	String msgNoProtAccordo = "Non è possibile stampare un accordo generico se il lavoratore si trova in 150.";
	Patto p = null;				
	Object stampaPattoConf = null;
	//String cdnLavoratoreStr= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String cdnLavoratore= (String )serviceRequest.getAttribute("cdnLavoratore");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
    int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  	PageAttribs attributi = new PageAttribs(user, _page);
    // Savino 05/10/05: l'attributo stampa non viene piu' gestito
	//boolean canPrint =false;
	boolean infStorButt=false;
	boolean canInsert = false;
	boolean canModify = false;
	boolean canEdit=false;
	boolean canInvioSap = false;
	boolean canRistampa = false;
	boolean canPi3 = false; // Protocollazione Pi3
	boolean viewProfilePi3 = false;
	boolean canConferimentiDID = false; // Visualizza Conferimenti DID
	boolean viewProfileConferimentiDID = false; // Profilatura per Conferimenti DID
	boolean viewStoricoProfGG = false; // Storico Profiling GG
	boolean canView=filter.canViewLavoratore();
	boolean isProgrammiKO = false;
	if (!canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	} 
	
	//Borriello maggio 2020 Patto Online
	String codiceAbilitazioneServizi = null;
	String dataInvioAccettazioneServizi = null;
	String dataAccettazioneServizi = null;
	String dataUltimaStampa = null;
	String statoPattoOnLine = null;
	String tipoAccettazionePattoOnLine = null;
	String flgPattoOnLine = null;
	String flgReinvioPattoOnLine = null;
	boolean isPattoOnLine = false;
	boolean canInvioPtOnLine = false;
	boolean canStatoPtOnLine = false;
	boolean canButtonPtOnLine = false;
	boolean canReinvioPtOnLine = false;
	boolean canStoricoPtOnLine = false;
	boolean storicoPattoOnLine = false;
    // fine borriello
  	String configPattoOnLine = serviceResponse.containsAttribute("M_Config_PattoOnLine.ROWS.ROW.STRVALORECONFIG")?
		  	serviceResponse.getAttribute("M_Config_PattoOnLine.ROWS.ROW.STRVALORECONFIG").toString():Properties.DEFAULT_CONFIG;
	 storicoPattoOnLine = serviceResponse.containsAttribute("M_PattiOnLineStoricizzati.ROWS.ROW");
	  	
	// Savino 05/10/05: l'attributo stampa non viene piu' gestito
      //canPrint    = attributi.containsButton("stampa");  
	    infStorButt = attributi.containsButton("STORICO");
    	canInsert = attributi.containsButton("INSERISCI");
    	canModify   = attributi.containsButton("aggiorna");
    	canInvioSap   = attributi.containsButton("INVIO_SAP");
    	canRistampa = attributi.containsButton("RISTAMPA");
    	viewProfilePi3 = attributi.containsButton("PROTOCOLLO_PI3"); // Protocollazione Pi3
    	viewProfileConferimentiDID = attributi.containsButton("VIEW_CONFERIMENTI_DID"); // Conferimenti DID
    	viewStoricoProfGG  = attributi.containsButton("ALL_PROF_GG"); // Storico Profiling GG
    	canButtonPtOnLine = attributi.containsButton("INVIO_PTONLINE");
    	canStatoPtOnLine =  attributi.containsButton("RIC_PTONLINE");
    	canReinvioPtOnLine =  attributi.containsButton("REINVIO_PTONLINE");
    	canStoricoPtOnLine	= attributi.containsButton("STORICO_PTONLINE");	
    	//rdOnly     = !attributi.containsButton("AGGIORNA");
    	if((!canInsert) && (!canModify)){
    		//canInsert=false;
        //rdOnly=true;
    	}else{
        	canEdit=filter.canEditLavoratore();
	        if (canInsert){
	        	canInsert=canEdit;
	        }
	        if (canModify){
	          	canModify=canEdit;
	        }
	    }

%>

<%  
//    final String FORMATO_DATA     = "dd/MM/yyyy";
//    SimpleDateFormat df = new SimpleDateFormat(FORMATO_DATA);
//    BigDecimal cdnLavoratoreBD        = null;
//    String     strCognome           = null;
//    String     strNome              = null;
//    BigDecimal prgDichDispon        = null;
//    String     DATSTIPULA           = null;
  	String     CODSTATOATTO         = null;
    
//    BigDecimal PRGSTATOOCCUPAZ      = null;
//    String     FLGCOMUNICAZESITI    = null;
//    String     FLGPATTO297          = null;
//    BigDecimal PRGPATTOLAVORATORE   = null;
//    String     DATSCADCONFERMA      = null;
//    String     STRNOTE              = null;
//    String     CODMOTIVOFINEATTO    = null;
//    String     DATFINE              = null;
/*
 *   BigDecimal cdnUtIns             = null;
 *   String     dtmIns               = null;
 *   BigDecimal cdnUtMod             = null;
 *   String     dtmMod               = null;
 */    
//    BigDecimal NUMKLOPATTOLAVORATORE= new BigDecimal(-1.0);
//    String     codCPI               = null;
//    String     descCPI              = null;
    // si utilizzano i codice e la classe Testata.
   // String     CognIns              = null;
   // String     NomIns               = null;
   // String     CognMod              = null;
   // String     NomMod               = null;
//    String     DATDICHIARAZIONE     = null;
//    String     datInizio            = null;
//    String     descStato            = null;
// non viene utilizzato. Si usa direttamente la descrizione dello stato gia' decodificata
//    String     codStatoOcc          = null;
//    String     codServizio          = null;

//    BigDecimal PRGSTATOOCCUPAZACC = null;
//    String descStatoAcc           = null;
//    String descCPIAcc             = null;
//    String codCPIAcc              = null;
                 
//    String     codStatoAttoDid = null;
//    String     codTipoPatto  = null;
    boolean    flag_insert   = false;
    boolean    buttonAnnulla = false;
    String display1 = "none";
    String img1 = "../../img/chiuso.gif";
    String hasDataUscita = "false";
    // se c'e' la did valida allora ...
    // la condizione e': codStatoAttoDid!=null && codStatoAttoDid.equals("PR");
    boolean noAccordoGen = false;
    String dataInizioSoAperto = "";
    // la data iscrizione all'elenco anagrafico puo' essere quella legata alla did legata al patto oppure
    // quella corrente. Se non c'e' la did non si puo' fare il patto, a meno che il lavoratore non sia in 
    // mobilita'. In questo caso il record del patto non sara' associato al record della did, quindi
    // bisogna utilizzare la data di iscrizione all'elenco anagracico corrente.
    String dataInizioElAnagCorrente="";
    ////////////////////////////////////
    int cdnFunzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    //String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
    String queryString = "PAGE=PattoLavDettaglioPage&cdnLavoratore="+cdnLavoratore+"&CDNFUNZIONE="+cdnFunzione;
    boolean obiettiviDelPattoModify = true;
    InfCorrentiLav testata= null;
    Linguette l = null;
    Testata operatoreInfo=null;
//    cdnLavoratoreBD = new BigDecimal(cdnLavoratore);
  
    String dataRiferimentoIndice = "";
    String indiceSvantaggioVecchio = "";
    String indiceSvantaggio = "";
    String indiceSvantaggio2 = "";
    String codTipoPatto = "";
    Vector programmiPatto = null;
    String dataStipulaPattoCheck = "";
    String dataStipulaOrig = "";
    String statoOccRaggruppamento = "";
    String descrizioneStatoOccAccordo = "";
    String prgLavoratoreProfilo = "";
    int size = 11, max = 65;
    String decDoteProcessoAssegnato = "", decDoteProcessoResidua = "", decDoteRisultatoAssegnato = "", decDoteRisultatoResidua = "";
    //campi assegno ricollocazione
    String decImportoAr ="",  datNaspi = "", noteElemAttivazione="";
    //profilo dlgs150
    String indiceSvantaggio150= "";
    String dataRiferimentoIndice150 = "";
    String numProfiling150="";
    String strProfiling150="";
    
  //Borriello - settembre 2017 - Dati Ente Accreditato
  String codSedeSoggAcc = "";
  String codFiscaleSoggAcc = "";
  String noteAppuntamentoSoggAcc = ""; 
  boolean checkBtnModificheSuccessive = false;
    // fine borriello
    
  String configModAzPatto = serviceResponse.containsAttribute("M_Config_MODAZPT.ROWS.ROW.NUM")?
		  	serviceResponse.getAttribute("M_Config_MODAZPT.ROWS.ROW.NUM").toString():Properties.DEFAULT_CONFIG;
		  	
  	if(configModAzPatto.equalsIgnoreCase(Properties.CUSTOM_CONFIG)){
  		checkBtnModificheSuccessive = true;
   	}
  //	System.out.println("checkBtnModificheSuccessive: " + checkBtnModificheSuccessive);
		  	
		  	SourceBean row = null;
    // Savino 14/10/2005: prendo le informazioni correnti e le utilizzo quando necessario
    Vector rowInfoCorrenti  = serviceResponse.getAttributeAsVector("M_GETINFCORRPATTO.ROWS.ROW");
    InfoCorrenti infoCorrenti = null;
    if(rowInfoCorrenti != null && !rowInfoCorrenti.isEmpty()) {  
        row  = (SourceBean) rowInfoCorrenti.elementAt(0);
        infoCorrenti = new InfoCorrenti(row);
       /*
        *PRGSTATOOCCUPAZ = (BigDecimal) row.getAttribute("PRGSTATOOCCUPAZ");
        *prgDichDispon =  (BigDecimal) row.getAttribute("PRGDICHDISPONIBILITA");      
        *descStato       = (String)     row.getAttribute("DESCRIZIONESTATO");
        *datInizio       = (String)     row.getAttribute("DATINIZIO");
        *descCPI         = (String)     row.getAttribute("CPITITOLARE");              																																	 
        *codCPI          = (String)     row.getAttribute("CODCPITIT");             																																	 
        *DATDICHIARAZIONE= (String)     row.getAttribute("DATDICHIARAZIONE");                
        * // Savino 14/10/2005: non piu' necessario. La query considera solo le did valide
        * //codStatoAttoDid =(String)row.getAttribute("codStatoAttoDid");          
        * // noAccordoGen = codStatoAttoDid!=null && codStatoAttoDid.equals("PR");
        *noAccordoGen = codStatoAttoDid!=null;
        */
    }
    else infoCorrenti = new InfoCorrenti();
 
    row = null;
    
    Vector rowInfoProfiling  = serviceResponse.getAttributeAsVector("M_Get_Profiling_Patto.ROWS.ROW");
    if (rowInfoProfiling != null && !rowInfoProfiling.isEmpty()) {
    	SourceBean profilingBean  = (SourceBean) rowInfoProfiling.elementAt(0);
    	dataRiferimentoIndice = StringUtils.getAttributeStrNotNull(profilingBean, "datriferimento");
    	BigDecimal numIndiceSvantaggioBD = (BigDecimal) profilingBean.getAttribute("NUMINDICESVANTAGGIO");
    	BigDecimal numIndiceSvantaggioBD2 = (BigDecimal) profilingBean.getAttribute("NUMINDICESVANTAGGIO2");
    	if (numIndiceSvantaggioBD != null) {
    		indiceSvantaggio = numIndiceSvantaggioBD.toString();
    	} else {
    		indiceSvantaggio = "";
    	}
    	if (numIndiceSvantaggioBD2 != null) {
    		indiceSvantaggio2 = numIndiceSvantaggioBD2.toString();
    	} else {
    		indiceSvantaggio2 = "";
    	}
        indiceSvantaggioVecchio = StringUtils.getAttributeStrNotNull(profilingBean, "strindicesvantaggioold");
    }
    
    Vector rowCodificaPatto  = serviceResponse.getAttributeAsVector("M_GetCodificaTipoPatto.ROWS.ROW");
    SourceBean row_CurrPatto = null;
    String programmaPattoCurr = null;
  	//Recupero informazioni per l'accordo generico
  	if (serviceResponse.containsAttribute("M_GetInfoPerAccGen.ROWS.ROW")) {
	    Vector rowsAccGen  = serviceResponse.getAttributeAsVector("M_GetInfoPerAccGen.ROWS.ROW");
	    if(rowsAccGen != null && !rowsAccGen.isEmpty()) {
	    	SourceBean rowAccordo  = (SourceBean) rowsAccGen.elementAt(0);
	    	statoOccRaggruppamento = StringUtils.getAttributeStrNotNull(rowAccordo,"codStatoOccupazRagg");
	    	if (statoOccRaggruppamento.equalsIgnoreCase(Properties.RAGG_DISOCCUPATO) || statoOccRaggruppamento.equalsIgnoreCase(Properties.RAGG_INOCCUPATO)) {
	    		noAccordoGen = true;
	    		dataInizioSoAperto = StringUtils.getAttributeStrNotNull(rowAccordo,"datainizioso");
	    	}
	    }
  	}
  	
  	Vector listaAzioniCollegatePP = serviceResponse.getAttributeAsVector("M_GetAzioneLegatePatto.ROWS.ROW");
  	if (listaAzioniCollegatePP != null && listaAzioniCollegatePP.size() > 0) {
  		for (int jAz = 0; jAz < listaAzioniCollegatePP.size(); jAz++) {
    		SourceBean rowTipoAzione = (SourceBean)listaAzioniCollegatePP.get(jAz);
    		String flg_misurayei = (String)rowTipoAzione.getAttribute("flg_misurayei");
    		if (flg_misurayei.equalsIgnoreCase("S")) {
    			existAzioneGG = true;
    		}
  		}
  	}
  	
  	//scheda partecipante
	SourceBean schedaPartecipante = null;
  	boolean checkSchedaPartecipante = false;
  	boolean checkSchedaPart_flagConferma = false;
  	String numConfigSchedaPartecipante = serviceResponse.containsAttribute("M_CONFIG_SCHEDA_PARTECIPANTE.ROWS.ROW.NUM")?
   			serviceResponse.getAttribute("M_CONFIG_SCHEDA_PARTECIPANTE.ROWS.ROW.NUM").toString():Properties.DEFAULT_CONFIG;
   	if(numConfigSchedaPartecipante.equalsIgnoreCase(Properties.DEFAULT_CONFIG)){
   		checkSchedaPartecipante = true;
   		checkSchedaPart_flagConferma = true;
   	}else{
   		schedaPartecipante = (SourceBean) serviceResponse.getAttribute("M_SchedaPartecipantePatto.ROWS.ROW");
   	  	if (schedaPartecipante != null) {
   	  		checkSchedaPartecipante = true;
   	  		checkSchedaPart_flagConferma = schedaPartecipante.getAttribute("FLGCONFERMA").toString().equalsIgnoreCase("S");
   	  	}
   	}
   	
   	boolean prestazioniAssociateEmpty = false;
   	if (serviceResponse.containsAttribute("M_GetPrestazioniAssociatePOC.ROWS.ROW")) {
   		SourceBean rowPrestazioni = (SourceBean) serviceResponse.getAttribute("M_GetPrestazioniAssociatePOC.ROWS.ROW");
   		BigDecimal numPrestazioni = (BigDecimal)rowPrestazioni.getAttribute("numPrestazioni");
		
		if (numPrestazioni != null && numPrestazioni.intValue() == 0 ) {
			prestazioniAssociateEmpty = true;
		}
   	}
   
    //se abbiamo nella servicerequest il parametro "inserisci" è stato richiesto l'inserimento di un nuovo patto ("Inserisci nuovo")
    // Questo significa che la pagina deve essere vuota e si debbono inserire ex novo i dati. Il patto esistente verra' chiuso da un trigger.
    boolean gestNumGGeProf150 = false;
    if (serviceRequest.containsAttribute("inserisci") && (!serviceRequest.containsAttribute("DUPLICA") || !serviceRequest.getAttribute("DUPLICA").equals("DUPLICA"))) { 
        flag_insert   = true;
        buttonAnnulla = true;
        p = new Patto();
        gestNumGGeProf150 = true;
    }// fine ("inserisci")
    else { 
    	//Controllo se vi sono impegni legati al patto. Se non ci sono la stampa non deve essere possibile
		Vector impAssPatto = serviceResponse.getAttributeAsVector("M_GetImpegniLegatiPatto.ROWS.ROW");
		if(impAssPatto!=null && impAssPatto.size()>0){
			SourceBean sbImp = (SourceBean)impAssPatto.get(impAssPatto.size()-1);
			if(sbImp!=null && !sbImp.getAttribute("PRGLAVPATTOSCELTA").equals("")){
				hasImpAssociati = true;
			}
		}
		
		if (serviceResponse.containsAttribute("M_Get_Formazione_Legati_Patto.ROWS.ROW")) {
			msgNoProtPatto = "Non è possibile stampare un patto/accordo se non ha impegni e/o attività di formazione associati.";
			int numCorsiFormazPatto = ((BigDecimal) serviceResponse.getAttribute("M_Get_Formazione_Legati_Patto.ROWS.ROW.numAttivitaFormazionePatto")).intValue();
			if (numCorsiFormazPatto > 0) {
				hasFormazioneAssPatto = true;
			}
		}
		
		programmiPatto = serviceResponse.containsAttribute("M_GETPATTOLAV.MISURE_PROGRAMMI")?(Vector)serviceResponse.getAttribute("M_GETPATTOLAV.MISURE_PROGRAMMI"):null;
		//isProgrammiKO = serviceResponse.containsAttribute("M_GETPATTOLAV.MISURE_PROGRAMMI_NO_AZIONI");
		
		if(configPattoOnLine.equalsIgnoreCase(Properties.CUSTOM_CONFIG)){
	  		isPattoOnLine = true;
	   	}
        row=(SourceBean) serviceResponse.getAttribute("M_GETPATTOLAV.ROWS.ROW");
        if(row != null){
        	 p = new Patto(row);
        	 /* Patto ON LINE 
        	 Borriello maggio 2020
        	 */
        	 flgPattoOnLine = p.getFlagPattoOnLine();
        	 if(configPattoOnLine.equalsIgnoreCase(Properties.DEFAULT_CONFIG) 
        			 &&  StringUtils.isFilledNoBlank(flgPattoOnLine) 
        			 && flgPattoOnLine.equalsIgnoreCase("S")){
        		 isPattoOnLine = true;
        	 }
        	if(isPattoOnLine){
           		codiceAbilitazioneServizi = p.getCodiceAbilitazioneServizi();
           		dataInvioAccettazioneServizi = p.getDataInvioAccettazioneServizi();
           		dataAccettazioneServizi = p.getDataAccettazioneServizi();
           		statoPattoOnLine = p.getStatoPattoOnLine();
           		tipoAccettazionePattoOnLine = p.getTipoAccettazionePattoOnLine();
           		if(StringUtils.isEmptyNoBlank(statoPattoOnLine) && ( StringUtils.isEmptyNoBlank(flgPattoOnLine) ||  ( StringUtils.isFilledNoBlank(flgPattoOnLine)&& flgPattoOnLine.equals("S") ) )){  
           			canInvioPtOnLine = true;
           		}else if(StringUtils.isFilledNoBlank(statoPattoOnLine) || ( StringUtils.isFilledNoBlank(flgPattoOnLine)&& flgPattoOnLine.equals("N")) ){
           			canInvioPtOnLine = false;
           		}
           		dataUltimaStampa = p.getDataUltimaStampa();
           		flgReinvioPattoOnLine = p.getFlagReinvioPattoOnLine();
        	}

        	 
        	 
        	/****************/
              cdnLavoratore        =  p.getCdnLavoratore();      
				CODSTATOATTO = p.getCodStatoAtto();	

              codTipoPatto = p.getCodTipoPatto();
              obiettiviDelPattoModify = flag_insert || !CODSTATOATTO.equals("PR");
              dataRiferimentoIndice = p.getDataRiferimento();
              if (p.getIndiceSvantaggio() != null) {
            	  indiceSvantaggio = p.getIndiceSvantaggio().toString();
              } else {
            	  indiceSvantaggio = "";  
              }
              if (p.getIndiceSvantaggio2() != null) {
            	  indiceSvantaggio2 = p.getIndiceSvantaggio2().toString();
              } else {
            	  indiceSvantaggio2 = "";  
              }
              indiceSvantaggioVecchio = p.getIndiceSvantaggioVecchio();
              dataStipulaPattoCheck = p.getDatStipula();
              dataStipulaOrig = p.getDatStipulaOrig();
              if (p.getDecDoteProcessoAssegnato() != null) {
            	  decDoteProcessoAssegnato = p.getDecDoteProcessoAssegnato().toString(); 
              }
              if (p.getDecDoteProcessoResidua() != null) {
            	  decDoteProcessoResidua = p.getDecDoteProcessoResidua().toString(); 
              }
              if (p.getDecDoteRisultatoAssegnato() != null) {
            	  decDoteRisultatoAssegnato = p.getDecDoteRisultatoAssegnato().toString(); 
              }
              if (p.getDecDoteRisultatoResidua() != null) {
            	  decDoteRisultatoResidua = p.getDecDoteRisultatoResidua().toString(); 
              }
              if (p.getPrgLavoratoreProfilo() != null) {
            	  prgLavoratoreProfilo = p.getPrgLavoratoreProfilo().toString();
            	  prgLavoratoreProfilo = prgLavoratoreProfilo + "--" + p.getCodVchProfiling();
              }
              
              if (CODSTATOATTO.equalsIgnoreCase("PR")) {
            	  isProtocollato = true;
	              if (serviceResponse.containsAttribute("M_GetInfoStatoOccPerAccGen.ROWS.ROW")) {
	          	  	Vector rowsStatoOccAccGen  = serviceResponse.getAttributeAsVector("M_GetInfoStatoOccPerAccGen.ROWS.ROW");
	          	    if (rowsStatoOccAccGen != null && !rowsStatoOccAccGen.isEmpty()) {
	          	    	SourceBean rowStatoOccAccordo  = (SourceBean) rowsStatoOccAccGen.elementAt(0);
	          	   		descrizioneStatoOccAccordo = StringUtils.getAttributeStrNotNull(rowStatoOccAccordo, "DescrizioneStato");
	          	   		if (!descrizioneStatoOccAccordo.equals("")) {
		        			int sizeDesc = descrizioneStatoOccAccordo.length() + 3;
		        			if (sizeDesc > max) {
		        				descrizioneStatoOccAccordo = descrizioneStatoOccAccordo.substring(0, max) + "...";
		        			}
		            	}
	              	}
	              }
	              
	              //canPi3 = true; // Protocollazione Pi3
              }
              
              if(viewProfilePi3){
            	  if (CODSTATOATTO.equalsIgnoreCase("PR")) {
            		  canPi3 = true; // Protocollazione Pi3
            	  }
              }

              
              if (!p.getDatDichiarazione().equals("") && !p.getPrgDichDisponibilita().equals("")) {
	              if (viewProfileConferimentiDID){
	             	  if (CODSTATOATTO!=null  && (CODSTATOATTO.equals("PR") || CODSTATOATTO.equals("PP"))) {
	             		  canConferimentiDID = true; // Conferimenti DID
	             	  }
	              }
              }
              
              //campi assegno ricollocazione
              if (p.getImportoAr() != null) {
            	  decImportoAr = p.getImportoAr().toString();
              }
              if (p.getDatNaspi() != null) {
            	  datNaspi = p.getDatNaspi();
              }
              if (p.getNoteElemAttivazione() != null) {
            	  noteElemAttivazione = p.getNoteElemAttivazione();
              }
              if (p.getDataRiferimento150() != null) {
            	  dataRiferimentoIndice150 = p.getDataRiferimento150();
              }
              if (p.getIndiceSvantaggio150() != null) {
            	  indiceSvantaggio150 = p.getIndiceSvantaggio150();
              }
              if (p.getStrNumProfiling() != null) {  
            	  numProfiling150 = p.getStrNumProfiling();
              }
              if (p.getStrProfiling() != null) {  
            	  strProfiling150 = p.getStrProfiling();
              }              
              
              //campi soggetto accreditato (ente)
              codSedeSoggAcc = p.getCodiceSedeEnte();
              codFiscaleSoggAcc = p.getCodiceFiscaleEnte();
              noteAppuntamentoSoggAcc = p.getNoteEnte();
        }//if(row != null)     
        else { 
        /****************/
        	p = new Patto();
        	//altrimenti il record è vuoto, non ci sono inf. correnti: possiamo solo inserire
           	flag_insert = true;
           	isProtocollato = false;
           	gestNumGGeProf150 = true;
        }
    } //else
    	
    if(gestNumGGeProf150){	
	    %>
		    <!--SEZIONE PER IL CALCOLO DELLA DATA DI SCADENZA-->
		    <%-- @ include file="aggiungiGiorni.inc" --%>         
		<%
		if (serviceResponse.containsAttribute("M_GetDatiProfilingDid.ROWS.ROW")) {
	   		SourceBean rowDatiProfiling150 = (SourceBean) serviceResponse.getAttribute("M_GetDatiProfilingDid.ROWS.ROW");
	   		dataRiferimentoIndice150 = Utils.notNull(rowDatiProfiling150.getAttribute("datprofiling"));
			indiceSvantaggio150 = Utils.notNull(rowDatiProfiling150.getAttribute("decprofiling"));
        	numProfiling150 = Utils.notNull(rowDatiProfiling150.getAttribute("numprofiling"));
        	strProfiling150 = Utils.notNull(rowDatiProfiling150.getAttribute("strprofiling"));
		}
    }	
    	
    p.setInfoCorrenti(infoCorrenti);
    /////////////////////  protocollazione     /////////////////////////////////
    Vector rows= serviceResponse.getAttributeAsVector("M_PROTOCOLLATO.ROWS.ROW");
    String numProt  = "";
    String annoProt = "";
    String dataProt = "";
    String oraProt  = "";
    String docInOrOut = "";
    String docRif     = "";
    String nomeDocumento = "";
    
    if(rows != null && !rows.isEmpty())    { 
        row = (SourceBean) rows.elementAt(0);
        numProt  = SourceBeanUtils.getAttrStrNotNull(row,"NUMPROTOCOLLO");
        BigDecimal annoP = (BigDecimal) row.getAttribute("ANNOPROT");
        annoProt = annoP!=null ? annoP.toString() : "";
        dataProt = StringUtils.getAttributeStrNotNull(row,"datprot");
        oraProt  = StringUtils.getAttributeStrNotNull(row,"oraProt");
        docInOrOut  = StringUtils.getAttributeStrNotNull(row,"CODMONOIO");
        docRif  = StringUtils.getAttributeStrNotNull(row,"STRDESCRIZIONE");
        nomeDocumento = StringUtils.getAttributeStrNotNull(row,"strnomedoc");
        prgDocumento = SourceBeanUtils.getAttrBigDecimal(row,"prgdocumento", null);
    }
    // Savino 11/10/05: comunque bisogna riprendere la data di iscrizione all'elenco anagrafico corrente
    Vector rowCpI  = serviceResponse.getAttributeAsVector("M_GETINFCORRPATTO.ROWS.ROW");
	if(rowCpI != null && !rowCpI.isEmpty()) {
    	row  = (SourceBean) rowCpI.elementAt(0);
        dataInizioElAnagCorrente = (String)row.getAttribute("DATINIZIO");
    }
    ///////////////////////////////////////////////////////////////////////////////////
    operatoreInfo= new Testata(p.getCdnUtIns(), p.getDtmIns(), p.getCdnUtMod(), p.getDtmMod());
    testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    testata.setPaginaLista("ListaPattoLavPage");

    //String _page = (String) serviceRequest.getAttribute("PAGE"); 
    l = new Linguette(user, cdnFunzione, _page,new BigDecimal(cdnLavoratore));
    
    /*
    * PageAttribs attributi = new PageAttribs(user, "PattoLavDettaglioPage");
    * boolean canModify   = attributi.containsButton("aggiorna");
    * boolean canInsert   = attributi.containsButton("INSERISCI");
    * // Savino 05/10/05: l'attributo stampa non viene piu' gestito
	* //    boolean canPrint    = attributi.containsButton("stampa");
    * boolean infStorButt = attributi.containsButton("STORICO");    
    */
    String[] tipi_patti_check = {MessageCodes.General.PATTO_L14, MessageCodes.General.PATTO_DOTE, MessageCodes.General.PATTO_DOTE_IA}; 
    
	if (Arrays.asList(tipi_patti_check).contains(codTipoPatto)) {
    	canEdit = false;
    	canInsert = false;
    	canModify = false;		
	}
    
    String readOnlyString = String.valueOf(!canModify);
    obiettiviDelPattoModify = canModify ? obiettiviDelPattoModify:false; 
    
    //Se si tratta di un inserimento e il lavoratore ha già la DID mostro solo la tipologia patto 150
    //e la seleziono di default
    boolean soloPatto297 = false;
    if (!p.getDatDichiarazione().equals("") && p.getCodStatoAttoDid().equals("PR")) {
    // Savino 11/10/2005: non so perche' viene fatta questa scelta. Se si deve caricare un accordo generico viene
    //				visto come patto 150.
    // commentato
    // Ho capito:)) se siamo in fase di inserimento, si vuole impedire che si possa scegliere l'accordo
    	soloPatto297 = true;
    }
    String dataUltimoProtocolloModificato = (String)serviceResponse.getAttribute("M_PattoProtocollatoModificato.rows.row.datUltimoProtocollo");
    //Controlla presenza storico
    boolean storicoPatto = serviceResponse.containsAttribute("M_PATTISTORICIZZATI.ROWS.ROW");
    String numConfigVoucher = serviceResponse.containsAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM")?
  			serviceResponse.getAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM").toString():Properties.DEFAULT_CONFIG;
    
 	// controllo per invio SAP
 	//boolean checkInvioSap = true;
    //BigDecimal checkSap = (BigDecimal)serviceResponse.getAttribute("M_CheckSapYG.ROWS.ROW.CHECKSAP");
    //if (checkSap.compareTo(new BigDecimal("0")) > 0) {
    //	checkInvioSap = false;
    //}
     //Controllo di eseguibilità "Gestione Consenso"
	boolean isConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma"); 
//	boolean isButtonGestioneConsenso = false;
	boolean isConsensoAttivo = false;
	String statoConsenso = null;
	String ipOperatore=null;
	if(isConsenso){
/*	 	ProfileDataFilter filterConsenso = new ProfileDataFilter(user, "HomeConsensoPage");
		isButtonGestioneConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma.viewGestioneConsensoBtn") 
				  						&& filterConsenso.canView();*/
		statoConsenso = serviceResponse.getAttribute("M_VerificaAmConsensoFirma.statoConsenso").toString();
				
	 	isConsensoAttivo= serviceResponse.containsAttribute("M_VerificaAmConsensoFirma.consensoFirmaAttivo");
	 	if(isConsensoAttivo){
	 		 ipOperatore = serviceResponse.getAttribute("M_VerificaAmConsensoFirma.ipOperatore").toString();
	 	  }
	}
	
	  String codMinSap = "";
	  Vector vectCodSap = serviceResponse.getAttributeAsVector("M_SapGestioneServiziGetCodMin.ROWS.ROW");
	  if ((vectCodSap != null) && (vectCodSap.size() > 0)) {
		SourceBean beanLastInsert = (SourceBean) vectCodSap.get(0);
		codMinSap = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODMINSAP");
	  }
	  
	  boolean disableRichiestaSap = false;
	  if (("").equalsIgnoreCase(codMinSap)) {
		disableRichiestaSap = true;
	  }  

%>

<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>


<%
// POPUP EVIDENZE
String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
int _fun = 1;
if((cdnFunzione>0) ) { _fun = cdnFunzione; }
EvidenzePopUp jsEvid = null;
boolean bevid = attributi.containsButton("EVIDENZE");
if(strApriEv.equals("1") && bevid) {
	jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
}
Vector mobilita = new Vector();
Vector statiOccupazionali = new Vector();
String raggStatoOccAperto = "";
if (Sottosistema.MO.isOn()) {
	// E' possibile stipulare un patto anche non avendo la did. 
	//Ma bisogna che il lavoratore abbia periodi di mobilità , sia in 150 alla data
	//stipula del patto e sia in 150 allo stato attuale(stato occupazionale aperto)
	mobilita = serviceResponse.getAttributeAsVector("M_IscrizioniMobilita.rows.row");
	statiOccupazionali =  serviceResponse.getAttributeAsVector("M_Stati_Occupazionali_Intervalli.rows.row");
	for (int j=0;j<statiOccupazionali.size();j++) {
		SourceBean sOcc = (SourceBean)statiOccupazionali.get(j);
		if (sOcc.getAttribute("datFine") == null) {
			raggStatoOccAperto = sOcc.getAttribute("CODSTATOOCCUPAZRAGG").toString();
			break;
		} 
	}
	if (mobilita.size() > 0) soloPatto297 = true;
}
%>
<html>
<head>
<title>Dettaglio patto/accordo lavoratore</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<af:linkScript path="../../js/"/>
<script language="Javascript" src="../../js/docAssocia.js"></script>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<%@ include file="PattoSezioniATendina.inc" %>

<%if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>

<SCRIPT language="JavaScript">
<%@ include file="_controlloDate_script.inc"%>
<%@ include file="_sezioneDinamica_script.inc"%>
<!--

var motivoChiusuraAccAutomatica = "<%=Properties.CHIUSURA_AUTOMATICA_ACCORDO%>";
var tipoPattoGG = "<%=MessageCodes.General.PATTO_GARANZIA_GIOVANI%>";
var tipoPattoGGUmbria = "<%=MessageCodes.General.PATTO_GARANZIA_GIOVANI_UMBRIA%>";
var tipiPattoToCheck = "<%=MessageCodes.General.PATTO_L14%>-<%=MessageCodes.General.PATTO_DOTE%>-<%=MessageCodes.General.PATTO_DOTE_IA%>";
var statoOccRagg = "<%=statoOccRaggruppamento%>";
var arrayCodificaPatto = new Array();
var arrayProgrammiPatto = new Array();
var arrayFlgPatto297 = new Array();
var checkAzioneGG = '<%=existAzioneGG%>';
var vCodDoteProfiling = new Array();
var vTipoServizioIndice = new Array();
var vDoteIndice = new Array();
var checkSchedaPart = '<%=checkSchedaPartecipante%>';
var prestazAssocEmpty = '<%=prestazioniAssociateEmpty%>';
var checkSchedaPart_Flag = '<%=checkSchedaPart_flagConferma%>';
var checkIsProgrammiKO = '<%=isProgrammiKO%>';
var checkBtnModificheSuccessive = '<%=checkBtnModificheSuccessive%>';
<%
if (numConfigVoucher.equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {
	Vector listaDoteIndice = serviceResponse.getAttributeAsVector("M_GET_DOTE_VCH_PROFILING.ROWS.ROW");
	for (int j = 0; j < listaDoteIndice.size(); j++) {
		SourceBean rowDote = (SourceBean)listaDoteIndice.get(j);
		String codiceIndice = (String)rowDote.getAttribute("codvchprofiling");
		String codiceTipoServizio = (String)rowDote.getAttribute("codvchtiposervizio");
		BigDecimal doteIndice = (BigDecimal)rowDote.getAttribute("decdote");
		%>
		vCodDoteProfiling[<%=j%>] = '<%=codiceIndice%>';
		vTipoServizioIndice[<%=j%>] = '<%=codiceTipoServizio%>';
		vDoteIndice[<%=j%>] = <%=(BigDecimal)rowDote.getAttribute("decdote")%>;
		<%
	}
}
%>

<%for(int indexPatto=0; indexPatto < rowCodificaPatto.size(); indexPatto++)  { 
	row_CurrPatto = (SourceBean) rowCodificaPatto.elementAt(indexPatto);
    out.print("arrayCodificaPatto["+indexPatto+"]=\""+ row_CurrPatto.getAttribute("CODICE").toString()+"\";\n");
    out.print("arrayFlgPatto297["+indexPatto+"]=\""+ row_CurrPatto.getAttribute("FLGPATTO297").toString()+"\";\n");
}

if (programmiPatto != null && programmiPatto.size()>0) {
	for(int indexProg=0; indexProg < programmiPatto.size(); indexProg++)  { 
		programmaPattoCurr = (String) programmiPatto.elementAt(indexProg);
	    out.print("arrayProgrammiPatto["+indexProg+"]=\""+ programmaPattoCurr +"\";\n");
	}	
}
%>

function sceglipage(Scelta){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (Scelta == 1) {
    // torna indietro senza inserire   
        var urlpage="AdapterHTTP?";
        urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
        urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
        urlpage+="PAGE=PattoLavDettaglioPage";
        setWindowLocation(urlpage);
    }       
}

function apriInfoStoriche(cdnlav) {
    uripopup="AdapterHTTP?PAGE=PattoInformazioniStorichePage&cdnLavoratore="+cdnlav;
    window.open (uripopup, "InformazioniStoriche", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
}

function mostra(id){
    var div = document.getElementById(id);
    div.style.display="";
    div.style.visibility="visible";
}

function nascondi(id){
    var div = document.getElementById(id);
    div.style.display="none";
    div.style.visibility="hidden";
}

// by Davide
function showIfsetVar() {
	var valoreSel = document.Frm1.flgPatto297.value;
    //se patto 150                          
    if(valoreSel != "" && valoreSel == "S")  { 
        selezionePatto297();    
    }//se accordo                                                                
    else if(valoreSel != "" && valoreSel == "N")  {  
        selezioneAccordoGenerico();   
   	}   
    else { 
    	selezioneVuota();
    }    
    return true;
}//showIfsetVar()

function selezioneAccordoGenerico() {
	
    nascondi("infoPatto1");                                                      
    mostra  ("infoPatto2");                                                      
    nascondi("infoPatto3");                                                      
    mostra  ("infoPatto4");                                                      
    nascondi("labelVisulizza");                                                  
    mostra  ("labelNascondi");                                                   
    nascondi("titoloIni");                                                       
    nascondi("titolo297");                                                       
    mostra  ("titoloAcc");
    document.Frm1.datInizio.validateOnPost = "false";                      
    document.Frm1.datDichDisponibilita.validateOnPost = "false";           
    document.Frm1.PRGDICHDISPONIBILITA.value = "";
    document.Frm1.CODVCHPROFILING.value = "";
    document.Frm1.CODVCHPROFILING.disabled = true;
   /* document.Frm1.decDoteProcessoAssegnato.value = "";
	document.Frm1.decDoteProcessoResidua.value = "";
	document.Frm1.decDoteRisultatoAssegnato.value = "";
	document.Frm1.decDoteRisultatoResidua.value = "";               */          
}
function selezioneVuota() {
    nascondi("infoPatto1");                                                      
    nascondi("infoPatto2");                                                      
    nascondi("infoPatto3");                                                      
    nascondi("infoPatto4");                                                      
    nascondi("labelVisulizza");                                                  
    nascondi("labelNascondi");                                                   
    mostra  ("titoloIni");                                                       
    nascondi("titolo297");                                                       
    nascondi("titoloAcc");
}
function selezionePatto297() {
    mostra  ("infoPatto1");                                                        
    mostra  ("infoPatto2");                                                        
    mostra  ("infoPatto3");                                                        
    mostra  ("infoPatto4");                                                        
    mostra  ("labelVisulizza");                                                    
    nascondi("labelNascondi");                                                   
    nascondi("titoloIni");                                                       
    mostra  ("titolo297");                                                         
    nascondi("titoloAcc");                                                       
    document.Frm1.datInizio.validateOnPost = "true";                       
    document.Frm1.datDichDisponibilita.validateOnPost = "true";            
    document.Frm1.PRGDICHDISPONIBILITA.value = document.Frm1.PRGDICHDISP.value;
    document.Frm1.CODVCHPROFILING.disabled = false;
}

function toDichDisp() {
    var urlpage="AdapterHTTP?";
    urlpage+="fromPattoDettaglio=1&";
    urlpage+="cdnFunzione=<%=cdnFunzione%>&";
    urlpage+="cdnLavoratore=<%=cdnLavoratore%>&"; 
    urlpage+="pageChiamante=PattoLavDettaglioPage&";
    urlpage+="page=DispoDettaglioPage&";
    // e' il nome del frame che deve essere refreshato nel caso si inserisca un nuovo documento
    urlpage+="FRAME_NAME=DichiarazioneDiponibilita";
	window.open(urlpage,"DichiarazioneDiponibilita", 'toolbar=0, scrollbars=1, resizable=1,height=600,width=800'); 
}
function underConstr() { alert("Funzionaliltà non ancora attivata."); }

//funzione che controlla se un campo è stato modificato o meno
var flagChanged = false;
var siStipulaPatto297 = <%=noAccordoGen%>;
var dataInizioSoAperto = "<%=dataInizioSoAperto%>";

function fieldChanged() {
	 
 <% if (!readOnlyString.equalsIgnoreCase("true")){ %> 
  
    flagChanged = true;
 <%}%> 
}
 
//Impossibile stampare un patto non protocollato se non sono stati associati degli impegni e se non esiste una scheda partecipante
function controllaStampaPatto(ristampa) {
	var isConsensoAttivo = <%= isConsensoAttivo%>;
	var ok = true;
	if ( (document.Frm1.flgPatto297.value == 'N') && (statoOccRagg == 'D' || statoOccRagg == 'I') ) {
		alert("<%=msgNoProtAccordo%>");
		ok = false;
	}

	if (ok && checkIsProgrammiKO == 'true') {
		alert("Non è possibile stampare un patto/accordo se esiste un programma aperto senza attività di formazione associati.");
		ok = false;	
	}
	if (ok) {
		<% if(!hasImpAssociati && !hasFormazioneAssPatto){ %>
			alert("<%=msgNoProtPatto%>");
			ok = false;
		<% } %>		
	}
	if(ok && checkSchedaPart == 'false'){
		alert("Non è possibile stampare un patto/accordo se non è stata compilata la scheda partecipante.");
		ok = false;
	//	return ok;
	}/*else{
		if(ok && checkSchedaPart_Flag != 'true'){
		 
			if (!confirm("Nella scheda partecipante sono state riportate le infomazioni indicate nel patto precedente. Se si conferma con OK  le informazioni saranno associate al nuovo patto altrimenti accedere alla linguetta 6 per aggiornare i dati.")) {
				ok = false;
			}
		}
	}*/
    
	if (ok && check_Misura_Programma('POC') && document.Frm1.flgPatto297.value == "S" && prestazAssocEmpty == 'true') {
		alert("Per la tipologia di programma 'Piano d'intervento per l'occupazione' non sono state indicate le prestazioni necessarie.");
		ok = false;
	}
		
	if (ok) {
	<%-- Savino 23/09/05: dato che si puo' stampare solo se il patto e' PP il valore inviato sara' sempre lo stesso.
	     Sara' poi l'action a cambiarne il valore nel caso si sia scelto di protocollare --%>
		codStatoAtto = document.Frm1.CODSTATOATTO.value;
		<%-- Savino 10/10/2005: finora veniva gestito solo il tipo documento 150 --%>
		if (document.Frm1.flgPatto297.value == "S") {
			tipoDoc = "PT297";
		if (ristampa && !isConsensoAttivo) {
				apriGestioneDoc('RPT_PATTO','&ristampaPT=S&cdnLavoratore=<%=cdnLavoratore%>&datStipula=<%=p.getDatStipula()%>&pagina=<%=_page%>&strChiaveTabella=<%=p.getPrgPattoLavoratore()%>&tipoMisura=<%=p.getCodTipoPatto()%>&codStatoAtto='+codStatoAtto,tipoDoc);
			}
		else if(!ristampa && !isConsensoAttivo){
				apriGestioneDoc('RPT_PATTO','&cdnLavoratore=<%=cdnLavoratore%>&datStipula=<%=p.getDatStipula()%>&pagina=<%=_page%>&strChiaveTabella=<%=p.getPrgPattoLavoratore()%>&tipoMisura=<%=p.getCodTipoPatto()%>&codStatoAtto='+codStatoAtto,tipoDoc);	
		}else if(ristampa && isConsensoAttivo){
			apriGestioneDoc('RPT_PATTO','&ristampaPT=S&cdnLavoratore=<%=cdnLavoratore%>&datStipula=<%=p.getDatStipula()%>&pagina=<%=_page%>&strChiaveTabella=<%=p.getPrgPattoLavoratore()%>&firmaGrafometrica=OK&ipOperatore=<%=ipOperatore%>&codStatoAtto='+codStatoAtto,tipoDoc);
		}else if(!ristampa && isConsensoAttivo){
			apriGestioneDoc('RPT_PATTO','&cdnLavoratore=<%=cdnLavoratore%>&datStipula=<%=p.getDatStipula()%>&pagina=<%=_page%>&strChiaveTabella=<%=p.getPrgPattoLavoratore()%>&firmaGrafometrica=OK&ipOperatore=<%=ipOperatore%>&codStatoAtto='+codStatoAtto,tipoDoc);	
			}
		} 
		else {
			tipoDoc = "ACLA";
		if (ristampa && !isConsensoAttivo) {
				apriGestioneDoc('RPT_ACCORDO_GENERICO','&ristampaPT=S&cdnLavoratore=<%=cdnLavoratore%>&datStipula=<%=p.getDatStipula()%>&pagina=<%=_page%>&strChiaveTabella=<%=p.getPrgPattoLavoratore()%>&tipoMisura=<%=p.getCodTipoPatto()%>&codStatoAtto='+codStatoAtto,tipoDoc);
			}
		else if(!ristampa && !isConsensoAttivo){
				apriGestioneDoc('RPT_ACCORDO_GENERICO','&cdnLavoratore=<%=cdnLavoratore%>&datStipula=<%=p.getDatStipula()%>&pagina=<%=_page%>&strChiaveTabella=<%=p.getPrgPattoLavoratore()%>&tipoMisura=<%=p.getCodTipoPatto()%>&codStatoAtto='+codStatoAtto,tipoDoc);	
		}else if(ristampa && isConsensoAttivo){
			apriGestioneDoc('RPT_ACCORDO_GENERICO','&ristampaPT=S&cdnLavoratore=<%=cdnLavoratore%>&datStipula=<%=p.getDatStipula()%>&pagina=<%=_page%>&strChiaveTabella=<%=p.getPrgPattoLavoratore()%>&firmaGrafometrica=OK&ipOperatore=<%=ipOperatore%>&codStatoAtto='+codStatoAtto,tipoDoc);
		}else if(!ristampa && isConsensoAttivo){
			apriGestioneDoc('RPT_ACCORDO_GENERICO','&cdnLavoratore=<%=cdnLavoratore%>&datStipula=<%=p.getDatStipula()%>&pagina=<%=_page%>&strChiaveTabella=<%=p.getPrgPattoLavoratore()%>&firmaGrafometrica=OK&ipOperatore=<%=ipOperatore%>&codStatoAtto='+codStatoAtto,tipoDoc);	
			}
		}
	}
}


function settaFlag297() {
	var codificaTipologiaPatto = document.Frm1.codCodificaPatto.value;
	if (codificaTipologiaPatto == "") {
		document.Frm1.flgPatto297.value = "";
	}
	else {
		var indiceP = 0;
		for (indiceP=0; indiceP<arrayCodificaPatto.length ;indiceP++) {
			if (arrayCodificaPatto[indiceP] == codificaTipologiaPatto) {
				flagPatto297 = arrayFlgPatto297[indiceP];
				document.Frm1.flgPatto297.value = flagPatto297;
			}
		}
	}
	return true;
}

function checkMisura() {
	if (tipiPattoToCheck.indexOf(document.Frm1.codTipoPatto.value) > -1) {
		alert("Misura non consentita");
      	document.Frm1.codTipoPatto.options.selectedIndex=0;
	}
}


/**
* Controllo della presenza della dichiarazione di immediata disponibilita'
* Se non e' stata firmata la dichiarazione di immediata disponibilita' il patto non puo' essere stipulato
*/
function hasDichiarazioneDisponibilita() {
	var codStatoAttoDid = "<%=p.getCodStatoAttoDid() %>";
	flagPatto297 = document.Frm1.flgPatto297.value;
	var dataStipula = document.Frm1.DATSTIPULA.value;
	 
    <%-- Savino 27/09/05 --%>
    //if ((document.Frm1.PRGDICHDISPONIBILITA.value=="" || codStatoAttoDid=="PA") && flagPatto297.value=="S" ) {
    <%-- Savino 10/10/05: il 150 e' ora possibile anche se non si ha la did ma si è in mobilita'. Deve pero' sempre esserci la iscrizione all'elenco anagrafico --%>
    
    if ( (document.Frm1.PRGDICHDISPONIBILITA.value=="" ||  codStatoAttoDid!="PR" ) && flagPatto297=="S" && !iscrittoMobilita() && 
    	 (!siStipulaPatto297 || compDate(dataStipula, dataInizioSoAperto)<0) ) {
    // e' stato scelto un patto 150 ma il lavoratore non ha fatto la dichiarazione di immediata disponibilita'
        if (confirm("La dichiarazione di immediata disponibilità non e' stata rilasciata o è incompleta: proseguire con la dichiarazione?")) {
            document.Frm1.codCodificaPatto.options.selectedIndex=0;
            document.Frm1.flgPatto297.value="";
            selezioneVuota();
            toDichDisp();
        }
        else {        
            // si resetta la combo
            document.Frm1.codCodificaPatto.options.selectedIndex=0;
            document.Frm1.flgPatto297.value="";
            selezioneVuota();
        }
    }
    else {
    	<%-- Savino 11/10/05: controlliamo la presenza dell'iscrizione all'elenco anagrafico --%>
        if (document.Frm1.datInizio.value=="" && (iscrittoMobilita() || siStipulaPatto297)) {
        	alert("Il lavoratore non risulta iscritto nell'elenco anagrafico.");
        	document.Frm1.codCodificaPatto.options.selectedIndex=0;
        	document.Frm1.flgPatto297.value="";
        	selezioneVuota();
        	return false;
        }
    }
}
<%-- Savino 13/10/2005 carico in un vettore le date inizio mobilita' e le date di tutti gli stati occupazionali.
		si puo' stipulare il patto se il lavoratore, nel periodo della data stipula, era in mobilita' ed era 
		disoccupato o inoccupato.
--%>
function iscrittoMobilita() {
	var mobilita= new Array();
	var statoOccupazionale = new Array();
	var raggStatoOccAperto = "<%=raggStatoOccAperto%>";
	if (raggStatoOccAperto != 'D' && raggStatoOccAperto != 'I') 
		return false; 
	<%for (int i = 0;i<mobilita.size();i++){
		out.println("\tmobilita["+i+"]= new Object();");
		out.println("\tmobilita["+i+"].datInizio='"+((SourceBean)mobilita.get(i)).getAttribute("datInizio")+"'");
	}
	int j=0;
	for (int i = 0;i<statiOccupazionali.size();i++){
		SourceBean stOcc = (SourceBean)statiOccupazionali.get(i);
		if (!(stOcc.getAttribute("codStatoOccupazRagg").equals("D") || 
			stOcc.getAttribute("codStatoOccupazRagg").equals("I") )	)	
			continue;
		// se D o I allora si carica il vettore js
		out.println("\tstatoOccupazionale["+j+"]= new Object();");
		out.println("\tstatoOccupazionale["+j+"].datInizio='"+stOcc.getAttribute("datInizio")+"'");
		out.println("\tstatoOccupazionale["+j+"].datFine='"+Utils.notNull(stOcc.getAttribute("datFine"))+"'");
		j++;
	}	
	// a questo punto si controlla che la data stipula rientri nel range: 
	// dataStipulaPatto>=dataInizioMob[i] && dataInizioDisoc[j] <= dataStipulaPatto<= dataFineDisoc[j] 
	%>	
	if (!validateDate('DATSTIPULA')) {		
		return false;
	}
	var dataStipula = document.Frm1.DATSTIPULA.value;
	for (i=0;i<mobilita.length;i++) {
		if (compDate(dataStipula, mobilita[i].datInizio)>=0) {
			for (j=0;j<statoOccupazionale.length;j++) {
				if (compDate(dataStipula, statoOccupazionale[j].datInizio)>=0 &&
					(statoOccupazionale[j].datFine=="" || compDate(dataStipula, statoOccupazionale[j].datFine)<=0))
						return true;
			}
		}
	}
	return false;
}
/**
* Controlli generici sulle date ed altro prima dell' invio della form
*/

function controlla() {
	 
	var result = true;
	//var dataMobilita = "<%--=dataInizioMobilita--%>";
    dataDich = document.Frm1.datDichDisponibilita.value;
    dataStipula = document.Frm1.DATSTIPULA.value;
    dataFine = document.Frm1.DATFINE.value;
    var flagPatto297 = document.Frm1.flgPatto297.value;
    var codMotivoFineIndex = 0;

    if (tipiPattoToCheck.indexOf(document.Frm1.codTipoPatto.value) > -1) {
    	alert("Misura non consentita");
    	return false;
    }
    
    var dataRiferimentoIndice = document.Frm1.DATRIFERIMENTOINDICE.value;
    <%-- Savino 11/10/2005  sposto qui il controllo sulla data iscrizione elenco anagrafico, obbligatoria sempre --%>
    if (document.Frm1.datInizio.value=="") {
    	alert("Il lavoratore non è iscritto all'elenco anagrafico");
    	result = false;
    }
    try {
        selInd = document.Frm1.CODMOTIVOFINEATTO.selectedIndex;
        codMotivoFineIndex = selInd;
    }catch(e) {}
    if ( (flagPatto297 == "N") && (statoOccRagg == "D" || statoOccRagg == "I") ) {
		alert("Non è possibile inserire un accordo generico se il lavoratore si trova in 150");
		result = false;
		return result;
	}
    if (compDate(dataDich, dataStipula)>0) {
        alert(Messages.Date.ERR_DATA_PATTO);
        result = false;
    }
	if (isFuture(dataStipula)) {
        alert(Messages.Date.ERR_DATA_STIP);
        result = false;
    }
    if (isOld(dataStipula) && !confirm(Messages.Date.WARNING_STIPULA)) {        
    	result = false;
    }
    // Controllo sulla chiusura del patto
    if (dataFine!="" && codMotivoFineIndex==0) {
        alert(Messages.Date.ERR_DATA_MOTIVO);
        result = false;
    }
    if (dataFine=="" && codMotivoFineIndex>0) {
        alert(Messages.Date.ERR_DATA_MOTIVO2);
        result = false;
    }
    if (dataFine!="" && compDate(dataStipula , dataFine)>0){
        alert(Messages.Date.ERR_DATA_STIP_USCITA);
        result = false;
    }
  	if (dataFine==""  && !iscrittoMobilita() && (!siStipulaPatto297 || compDate(dataStipula, dataInizioSoAperto)<0) && dataDich=="" && flagPatto297 == "S") {
  		alert("Mobilità e/o stato occupazionale non compatibile con la data stipula : patto non stipulabile");
  		result = false;
  	}
  	if (dataRiferimentoIndice != "" && isFuture(dataRiferimentoIndice)) {
        alert("La data di riferimento non può essere successiva alla data odierna");
        result = false;
    }
	if (document.Frm1.CODMOTIVOFINEATTO.value != "" && document.Frm1.CODMOTIVOFINEATTO.value == motivoChiusuraAccAutomatica) {
		alert("Motivo chiusura patto/accordo non gestibile manualmente");
		result = false;
	}
	if (compDate(dataStipula, '<%=MessageCodes.General.DATA_PROFILING_2015%>') < 0) {
		if (!controllaFixedFloat('INDICESVANTAGGIO', 38, 0)) {         
			result = false;
	    }
		if (!controllaFixedFloat('INDICESVANTAGGIO2', 38, 0)) {         
			result = false;
	    }
	    if (document.Frm1.INDICESVANTAGGIO.value != "" && document.Frm1.INDICESVANTAGGIO.value != null && document.Frm1.INDICESVANTAGGIO.value != "1" && document.Frm1.INDICESVANTAGGIO.value != "2" && document.Frm1.INDICESVANTAGGIO.value != "3" && document.Frm1.INDICESVANTAGGIO.value != "4") {
	        alert("I valori accettati nel campo Indice di svantaggio sono 1, 2, 3 e 4");
	        result = false;
	    }
	    if (document.Frm1.INDICESVANTAGGIO2.value != "" && document.Frm1.INDICESVANTAGGIO2.value != null && document.Frm1.INDICESVANTAGGIO2.value != "1" && document.Frm1.INDICESVANTAGGIO2.value != "2" && document.Frm1.INDICESVANTAGGIO2.value != "3" && document.Frm1.INDICESVANTAGGIO2.value != "4") {
	        alert("I valori accettati nel campo Indice GG sono 1, 2, 3 e 4");
	        result = false;
	    }
	    if (check_Misura_Programma(tipoPattoGG)) {
		    if (document.Frm1.INDICESVANTAGGIO.value == null || document.Frm1.INDICESVANTAGGIO.value == "" ||
		    	document.Frm1.INDICESVANTAGGIO2.value == null || document.Frm1.INDICESVANTAGGIO2.value == "") {
		        alert("Attenzione, si consiglia di valorizzare i campi indice di svantaggio e Indice GG.");
		        //controllo non bloccante
		    }
	    }
	}
	else {
		document.Frm1.INDICESVANTAGGIO.value = "";
		if (!controllaFixedFloat('INDICESVANTAGGIO2', 38, 0)) {         
			result = false;
	    }
		if (document.Frm1.INDICESVANTAGGIO2.value != "" && document.Frm1.INDICESVANTAGGIO2.value != null && document.Frm1.INDICESVANTAGGIO2.value != "1" && document.Frm1.INDICESVANTAGGIO2.value != "2" && document.Frm1.INDICESVANTAGGIO2.value != "3" && document.Frm1.INDICESVANTAGGIO2.value != "4") {
	        alert("I valori accettati nel campo Indice GG sono 1, 2, 3 e 4");
	        result = false;
	    }
		if (check_Misura_Programma(tipoPattoGG)) {
			if (document.Frm1.INDICESVANTAGGIO2.value == null || document.Frm1.INDICESVANTAGGIO2.value == "") {
				alert("Attenzione, si consiglia di valorizzare il campo Indice GG.");
		        //controllo non bloccante
		    }
		}
	}
	
	if(!check_se_Patto_Programma('datInizioNaspi', 'ADR')){
		return false;
	}

	if(!check_se_Patto_Programma('decAssegnoNaspi', 'ADR')){
		return false;
	}
	if (!controllaFixedFloat('decAssegnoNaspi', 6, 2)) {
		return false;
	}
	
	if(!check_se_Patto_Programma('INDICESVANTAGGIO150', 'ADR') || !check_se_Patto_Programma('INDICESVANTAGGIO150', 'POC')){
		return false;
	}	
	if (!controllaFixedFloat('INDICESVANTAGGIO150', 1, 9)) {
		return false;
	}
	if (document.Frm1.INDICESVANTAGGIO150.value != "" && document.Frm1.INDICESVANTAGGIO150.value != null) {
		var num = parseFloat(document.Frm1.INDICESVANTAGGIO150.value).toFixed(9);
		if(num < 0 || num > 1){
			alert("I valori accettati nel campo Indice Dlgs 150 sono i valori decimali compresi tra 0 ed 1");
			return false;
		}
    }

	if(!check_se_Patto_Programma('DATRIFERIMENTOINDICE150', 'ADR') || !check_se_Patto_Programma('DATRIFERIMENTOINDICE150', 'POC')){
		return false;
	}	
    return result;
}

function solo297(tipo297) {	
	
	var flagTipo297 = document.Frm1.flgPatto297.value;
	<%-- Savino 10/10/05: bisogna impedire che un patto esistente possa cambiare tipo --%>
	<%if (!p.getFlgPatto297().equals("")) {%>
		patto297 = <%=p.isPatto297()%>;
		if ((patto297 && flagTipo297=="N") || (!patto297 && flagTipo297=="S")) {
			alert("Impossibile modificare la tipologia del patto/accordo.");
			document.Frm1.flgPatto297.value = '<%=p.getFlgPatto297()%>';
			for (i=0;i<document.Frm1.codCodificaPatto.length;i++) {
	            if (document.Frm1.codCodificaPatto.options[i].value == '<%=p.getCodificaPatto()%>') {
	            	document.Frm1.codCodificaPatto.selectedIndex=i;
	                break;
	            }
	        }	
			return false;
		}
	<%} else {%>
		if (flagTipo297.value == 'S') {
			selezionePatto297();
		}
		else {
			if (flagTipo297.value == 'N') {
				selezioneAccordoGenerico();
			}
			else {
				selezioneVuota();
			}	
		}
	<%}%>
	return true;
}
/** 
* deprecato
*/
function apriStampa(RPT,paramPerReport,tipoDoc){ 
  //RPT: attributo name del tag ACTION nel file action.xml relativo al report da visualizzare
  //paramPerReport: parametri necessari a visualizzare il report 
  //tipoDoc: codice relativo al tipo di documento cosi come inserito nel campo CODTIPODOCUMENTO della tab. DE_DOC_TIPO
  
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var urlpage="AdapterHTTP?ACTION_NAME="+RPT;
  urlpage+=paramPerReport; //Quelli che nella calsse sono inseriti nel vettore params
  urlpage+="&tipoDoc="+tipoDoc;
  urlpage+="&asAttachment=false";
  if(confirm("Vuoi PROTOCOLARE il file pirma di visualizzarlo?\n\nSeleziona\n- \"OK\"        per PROTOCOLLARE prima di visualizzare la stampa\n- \"Annulla\"  per visualizzare SENZA PROTOCOLLARE"))
  { urlpage+="&salvaDB=true";
  }
  else
  { urlpage+="&salvaDB=false";
  }
    
  setWindowLocation(urlpage);
    
}//apriStampa()

function apriPaginaModifiche() {
	var urlpage="AdapterHTTP?";    
    urlpage+="&cdnFunzione=<%=cdnFunzione%>";
    urlpage+="&cdnLavoratore=<%=cdnLavoratore%>"; 
    urlpage+="&prgPattoLavoratore=<%=p.getPrgPattoLavoratore()%>"; 
    urlpage+="&page=ModifichePattoPage";
    if (document.Frm1.flgPatto297.value == "S") {
    	urlpage+="&CODTIPODOCUMENTO=PT297";
    }
    else {
    	urlpage+="&CODTIPODOCUMENTO=ACLA";   
    }
	window.open(urlpage,"modificheAlPatto", 'toolbar=0, scrollbars=1, resizable=1,height=600,width=800'); 
}
var codStatoAttoCaricato = "<%=Utils.notNull(CODSTATOATTO)%>"; 
/**
* La protocollazione puo' avvenire solo attraverso la fase di stampa, quindi lo stato atto "protocollato" non puo' essere selezionato dall'operatore
*/
function controllaStato(statoObj) {
    stato = "";
    try {
        statoIndex = statoObj.selectedIndex;
        statoValue = statoObj.options[statoIndex].value; 
        stato = statoValue;
    }catch(e) {alert(e)}
    if (stato=="PR" && codStatoAttoCaricato!=stato) {
        alert("La protocollazione e' possibile dalla maschera di stampa");
        for (i=0;i<statoObj.length;i++) {
            if (statoObj.options[i].value==codStatoAttoCaricato) {
                statoObj.selectedIndex=i;
                return;
            }

        }
    }
}

function ricavaDote(indiceProfilingVoucher) {
	if (indiceProfilingVoucher.value == "") {
		document.Frm1.decDoteProcessoAssegnato.value = "";
		document.Frm1.decDoteRisultatoAssegnato.value = "";
	}
	else {
		for (var j = 0; j < vCodDoteProfiling.length; j++) {
			if (vCodDoteProfiling[j] == indiceProfilingVoucher.value) {
				if (vTipoServizioIndice[j] == 'SP') {
				 	document.Frm1.decDoteProcessoAssegnato.value = vDoteIndice[j];
				}
				else {
					document.Frm1.decDoteRisultatoAssegnato.value = vDoteIndice[j];		
				}
			}
		}
	}
}

function ricavaDoteProfilo(indiceProfilingVoucher) {
	var indiceProfiloLavoratore = indiceProfilingVoucher.value;
	if (indiceProfiloLavoratore == "") {
		document.Frm1.decDoteProcessoAssegnato.value = "";
		document.Frm1.decDoteRisultatoAssegnato.value = "";
	}
	else {
		
		var tmp1 = indiceProfiloLavoratore.split('--');
	    var codvchProfilingLavoratore = tmp1[1];
		for (var j = 0; j < vCodDoteProfiling.length; j++) {
			if (vCodDoteProfiling[j] == codvchProfilingLavoratore) {
				if (vTipoServizioIndice[j] == 'SP') {
				 	document.Frm1.decDoteProcessoAssegnato.value = vDoteIndice[j];
				}
				else {
					document.Frm1.decDoteRisultatoAssegnato.value = vDoteIndice[j];		
				}
			}
		}
	}
}

//Funzione che permette di scegliere 'accordo generico' solo se non vi è una DID associata.
function permettiAccordo(){
  <% if (noAccordoGen) { %>
        if (document.Frm1.flgPatto297.value == "N") { 
          alert("Impossibile scegliere 'Accordo Generico' in quanto il lavoratore si trova in 150.");
          document.Frm1.codCodificaPatto.options.selectedIndex=0;
          document.Frm1.flgPatto297.value = "";
          selezioneVuota();
          return false;
        }
  <% } else { %>
          if (document.Frm1.flgPatto297.value == "N"){ 
          <%-- Savino 11/10/05: questa chiamata produce per un qualche errore il reset del campo del cpi. 
          			Commentata per il momento --%>
//            valorizzaCampiPerAccGen();
          }
    <% } %>
    return true;
}

function nuovoPattoDaPrec(stato){
  if (stato)
    document.Frm1.DUPLICA.value = "DUPLICA";
  else
    document.Frm1.DUPLICA.value = "";
}

function controlloPersonalizzato(inputName){
  var ctrlObj = eval("document.forms[0]." + inputName);
    
  if(document.Frm1.flgPatto297.value == "S"){
    if (ctrlObj.value=="") {
      alert("Il campo " + ctrlObj.title + " è obbligatorio");
  		ctrlObj.focus();
    	return false;
    }
  }
  return true;
}

function valorizzaCampiPerAccGen(){
  document.Frm1.codCPI.value="<%=p.getCodCpi()%>";
  document.Frm1.codCPI2.value="<%=p.getDescCpi()%>";

  document.Frm1.prgStatoOccupaz.value="<%=p.getPrgStatoOccupaz()%>"; 
    <%
    /*int sizeAcc = 11, maxAcc = 65;
     * if(descStatoAcc != null)
     * { sizeAcc = descStatoAcc.length()+3;
     *   if(sizeAcc > maxAcc) 
     *   { descStatoAcc = descStatoAcc.substring(0,maxAcc)+"...";
     *     sizeAcc = maxAcc + 4;
     *   }
     * }
     */
    %>
  document.Frm1.prgStatoOccupaz2.value="<%=p.getPrgStatoOccupaz()%>";
}

function disabilitaBottone() {
	if (document.Frm1.insert_pattolav_button != null) {
		document.Frm1.insert_pattolav_button.disabled = true;	
		document.Frm1.insert_pattolav.disabled = false;	
	}
	else
		if (document.Frm1.Salva_button != null) {
			document.Frm1.Salva_button.disabled = true;	
			document.Frm1.Salva.disabled = false;	
		}
			
	return true;	
}
	function cambiAnnoProt(dataPRObj,annoProtObj) {
 	  var dataProt = dataPRObj.value;
 	  var lun = dataProt.length;
	
	  //Stiamo modificando la data di protocollazione. Quindi cambia anche l'anno di protocollazione
	  annoProtObj.value = ""; 
	  
	  if (lun > 5) {
	    var tmpDate = new Object();
	    tmpDate.value = dataProt;
	    if ( checkFormatDate(tmpDate) ) {
	       annoProtObj.value = tmpDate.value.substr(6,10);      
	    }
	    else if (lun==8 || lun==10) {
	      alert("La data di protocollazione non è corretta");
	    }
	  }
	}

	function settaOperazioneNuovoPatto(operazione) {
		document.Frm1.OPINSERISCINUOVO.value = operazione;
	}

	function doCheckSubmit() {
 		if(document.Frm1.skipControlli.value == 'SKIP'){
 			if (document.Frm1.OPINSERISCINUOVO.value == 'false'){
 				return disabilitaBottone();
 			}else{
 				return true;
 			}
 				
		}else{
			if (document.Frm1.OPINSERISCINUOVO.value == 'false')	{
				return solo297(document.Frm1.flgPatto297) && controlla() && disabilitaBottone() ;
			}
			else {
				return true;
			}	
		}
				
	}

	function invioSap(){
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		if (!checkInvioSap) return; 
		
	    // torna indietro senza inserire   
        var urlpage="AdapterHTTP?";
        urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
        urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
        urlpage+="PAGE=PattoLavDettaglioPage&";
        urlpage+="INVIOSAPYG=1";
        setWindowLocation(urlpage);      
	}

	function visualizzaSezioneADR(){
		if (check_Misura_Programma('ADR')) {
			mostra("sezADR");
			mostra("infoADR");
		}
		else {
			document.Frm1.datInizioNaspi.value = '';
			document.Frm1.decAssegnoNaspi.value = '';
			document.Frm1.noteElemAttivazione.value = '';
			nascondi("sezADR");
			nascondi("infoADR");
		}	
	}
	
	function check_se_Patto_Programma(inputName, tipoPatto){
	  var ctrlObj = eval("document.forms[0]." + inputName);
	  for (var j = 0; j < arrayProgrammiPatto.length; j++) {
            if (arrayProgrammiPatto[j] == tipoPatto) {
		    	if (ctrlObj.value=="") {
		    		alert("Il campo " + ctrlObj.title + " è obbligatorio");
		  			ctrlObj.focus();
		    		return false;
		    	}
            }
	  }
	  return true;
	}

	function check_Misura_Programma(tipoPatto){
		  for (var j = 0; j < arrayProgrammiPatto.length; j++) {
	            if (arrayProgrammiPatto[j] == tipoPatto) {
	            	return true;
	            }
		  }
		  return false;
		}
	
	function visualizzaSezionePattoOnLine(){
		var valore ;
			if(document.Frm1.flgPattoOnLine.options){
				var indiceSel = document.Frm1.flgPattoOnLine.options.selectedIndex;
				valore =  document.Frm1.flgPattoOnLine.options[indiceSel].value;
			}else{
				valore = "<%=flgPattoOnLine%>";
			
			}
		if (valore=='S') {
			mostra("sezpattoonline");
			mostra("ptOnlineSez");
		}
		else {
			nascondi("sezpattoonline");
			nascondi("ptOnlineSez");
		}	 
	}
	
	function submitPattoOnLine(oper){
		
		if (document.Frm1.STRCODABIPORTALE.value==''){
			alert("Impossibile invocare l'operazione. Inserire un valore per "+ document.Frm1.STRCODABIPORTALE.title);
			document.document.Frm1.STRCODABIPORTALE.focus();
		}else{
	        if ((oper==="invio" || oper==="reinvio") && flagChanged==true){
	         	alert("I dati sono stati modificati. Per poter inviare il patto/accordo è necessario prima salvare le modifiche");
	        	 return;
	        }
			if (document.Frm1.btnInvioPattoOnLine != null) {
				document.Frm1.btnInvioPattoOnLine.disabled = true;
				document.Frm1.btnInvioPattoOnLine.setAttribute("class", "pulsantiDisabled");
			} else if (document.Frm1.btnStatoPattoOnLine != null) {
					document.Frm1.btnStatoPattoOnLine.disabled = true;
					document.Frm1.btnStatoPattoOnLine.setAttribute("class", "pulsantiDisabled");
			}else if (document.Frm1.btnReinvioPattoOnLine != null) {
					document.Frm1.btnReinvioPattoOnLine.disabled = true;
					document.Frm1.btnReinvioPattoOnLine.setAttribute("class", "pulsantiDisabled");
			}
	        if(oper==="reinvio"){
				document.Frm2.controllaStato.value="REINVIO";
			} 
			if(oper==="stato"){
				document.Frm2.controllaStato.value="STATO";
			} 
			document.Frm2.submit();
		}
	}
	
	function storicoInviiPtOnLine() {
    	var prgPatto = document.Frm2.PRGPATTOLAVORATORE.value;
    	var f = "AdapterHTTP?PAGE=PTOnLineStoricoPage" + "&PRGPATTOLAVORATORE=" + prgPatto ;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=580,height=400,top=100,left=100"
        window.open(f, t, feat);
    }
	
	function makePattoOnLine(){
		
		var msg= "L'operazione modificherà il Patto/Accordo in \"Patto/Accordo stipulato con modalità online\" e non sarà più possibile modificarlo.\nSi vuole procedere?";
		if (confirm(msg)){
			if (document.Frm1.btnModificaPattoOnLine != null) {
				document.Frm1.btnModificaPattoOnLine.disabled = true;
				document.Frm1.btnModificaPattoOnLine.setAttribute("class", "pulsantiDisabled");
			}
			document.Frm3.submit();
		}else{
			return false;
		}	
 
	}
    
	
-->
</SCRIPT>
<script type="text/javascript" language="Javascript">
var isDataScadConferma2modify = '0';

<%

stampaPattoConf=serviceResponse.getAttribute("M_ST_CONF_STAMPA_PATTO.ROWS.ROW.NUM");
if ("1".equals(stampaPattoConf)){
	%>
isDataScadConferma2modify = '1';
<%}%>
//365 gg 24h 60 min 60 sec 1000 mSec
var millisec365day=365*24*60*60*1000;

	 function modificaDataScadConferma() {
		 if (isDataScadConferma2modify == '1') {
			 var d;
			 d = document.Frm1.DATSTIPULA.value;
			 // dd/mm/yyyy
			 meseinizio=d.substring(3,5);
	         giornoinizio=d.substring(0,2);
	         annoinizio=d.substring(6,10);
			 meseiniziojs=(parseInt(meseinizio,10)-1);

			//alert('datStipula input:'+ d );
	         var datStipula = new Date(annoinizio, meseiniziojs, giornoinizio);
			//alert('datStipula:'+ datStipula );
				msecFinali=datStipula.getTime()+ millisec365day;
			//alert('msecFinali:'+ msecFinali );
	         var datScadConferma= new Date(msecFinali);
			//alert('datScadConferma:'+ datScadConferma );
	         
// 	         nuovoanno = parseInt(annoinizio)+1;
	         nuovomese = (datScadConferma.getMonth()+1);
	         if(nuovomese<10) nuovomese='0'+nuovomese;
 	         nuovogiorno = datScadConferma.getDate();
	         if(nuovogiorno<10) nuovogiorno='0'+nuovogiorno;
	         
	         var ddate=nuovogiorno +"/"+nuovomese+"/"+datScadConferma.getFullYear();
			 
			 document.Frm1.DATSCADCONFERMA.value = ddate;
			 
// 		 } else {
// 			alert('isDataScadConferma2modify:'+isDataScadConferma2modify);
		 }
		 
	  } 
	  function apriGestioneConsenso( ) {
		  var urlpage="AdapterHTTP?";
		    urlpage+="PAGE=HomeConsensoPage&";
		    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
		    urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
//		    setOpenerWindowLocation(urlpage);
		    window.open(urlpage, "main");
		}
	  

	  function callPi3() {
		  	//alert('Pi3');
		   
		  	var urlpage="AdapterHTTP?";
		    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&PAGE=Pi3HomePattoPage&MODULE=M_SapCallRichiestaSap&CODMINSAP=<%=codMinSap%>&STATOCONSENSO=<%=statoConsenso%>&CONSENSO=<%=""+isConsensoAttivo%>&CDNLAVORATORE=<%=cdnLavoratore%>&NUMPROT=<%=numProt%>&prgDocumento=<%=prgDocumento%>&nomeDocumento=<%=nomeDocumento%>&pagina=PattoLavDettaglioPage&strChiaveTabella=<%=p.getPrgPattoLavoratore()%>";
		  // alert(urlpage);
		    window.open(urlpage,'Invio Protocollazione Pi3','toolbar=NO,statusbar=YES,width=800,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
		    
		  }
	  
	  function callConferimentiDID() {
		  	//alert('View Conferimenti DID');
		    var urlpage="AdapterHTTP?";
		    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&PAGE=ElencoConferimentiDIDPage&CDNLAVORATORE=<%=cdnLavoratore%>&pagina=PattoLavDettaglioPage&dataDichDid=<%=p.getDatDichiarazione()%>";
		    
		    window.open(urlpage,'Elenco Conferimenti DID','toolbar=NO,statusbar=YES,width=800,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
		  }

	function createCell(attributo) {
		var cell = document.createElement("td"); 
		var cellText = document.createTextNode(attributo);
		cell.appendChild(cellText);
		return cell;
	}

	function generateJSTableDeProfiling150() {
	    // get the reference for the body
	    var body = document.getElementsByTagName("body")[0];
	   
	    // creates a <table> element and a <tbody> element
	    var tbl = document.createElement("table");
	    var tblBody = document.createElement("tbody");
		<%
	  	Vector tableDeProfiling150 = serviceResponse.getAttributeAsVector("M_GetDeProfiling150.ROWS.ROW");
		
		if(tableDeProfiling150 != null && !tableDeProfiling150.isEmpty())    {
		  	for (int j150 = 0; j150 < tableDeProfiling150.size(); j150++) {
		  		SourceBean rowDP150 = (SourceBean)tableDeProfiling150.get(j150);
			%>
				var row = document.createElement("tr");	
				row.appendChild(createCell(<%=(BigDecimal)rowDP150.getAttribute("numprofiling")%>));
				row.appendChild(createCell(<%=(BigDecimal)rowDP150.getAttribute("fascia_da")%>));
				row.appendChild(createCell(<%=(BigDecimal)rowDP150.getAttribute("fascia_a")%>));
				row.appendChild(createCell('<%=Utils.notNull(rowDP150.getAttribute("strprofiling"))%>'));
				tblBody.appendChild(row);
			<%
		  	}
		}
		%>
	    tbl.appendChild(tblBody);

	    body.appendChild(tbl);

		tbl.setAttribute("id", "De_Profiling_150");
	    tbl.setAttribute("border", "1");
		tbl.style.display = "none";
	}

	function getDataCell(objTable, i) {
		return objTable.getElementsByTagName("td")[i].childNodes[0].data; 
	}

	function valorizzaCategoriaIS150(indiceSvantaggio) {
		var valoreIS150 = indiceSvantaggio.value.replace(",",".");
		if (isNaN(valoreIS150)) {
			document.Frm1.NUMPROFILING.value = "";
			document.Frm1.CATEGORIAINDICESV150.value = "";
			return;
		}
		var trovato = false;
		var mytable = document.getElementById("De_Profiling_150");
		var mytablebody = mytable.getElementsByTagName("tbody")[0];
		var myrows = mytablebody.getElementsByTagName("tr");

		if(myrows.length > 0) {
			var numRows = myrows.length;
			for (var j150 = 0; j150 < numRows && !trovato; j150++) {
	  			var myrow = myrows[j150];
				if (parseFloat(valoreIS150) >= parseFloat(getDataCell(myrow, 1)) && parseFloat(valoreIS150) <= parseFloat(getDataCell(myrow, 2))) {
					document.Frm1.NUMPROFILING.value = getDataCell(myrow, 0);
					document.Frm1.CATEGORIAINDICESV150.value = getDataCell(myrow, 3);
					trovato = true;
				}				  
			}
		}
			  
		if (!trovato) {
			document.Frm1.NUMPROFILING.value = "";
			document.Frm1.CATEGORIAINDICESV150.value = "";
		}
	}
	
	
	
  	function goProfilingGG(){
   		var urlpage="AdapterHTTP?";
  		var nomePagina = "AllProfilingGGPage";
   		urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&PAGE=";
   		urlpage += nomePagina;
		urlpage +="&CDNLAVORATORE=<%=cdnLavoratore%>";
  		window.open(urlpage,'Profiling GG','toolbar=NO,statusbar=YES,width=900,height=500,top=50,left=100,scrollbars=YES,resizable=YES');
	}
	  
</script>
<script type="text/javascript" language="Javascript">
<%
    attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);    
%>
    window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
<%--
 --%>
<style>
<%@ include file="_sezioneDinamica_css.inc"%>
</style>
</head>

<body class="gestione" onLoad="showIfsetVar();rinfresca();visualizzaSezionePattoOnLine();visualizzaSezioneADR();<%if(strApriEv.equals("1") && bevid) { %> apriEvidenze(); <%}%>generateJSTableDeProfiling150();">
<%
    if (testata!=null)testata.show(out);
    if (l!=null)l.show(out);
%>
<font color="red"><af:showErrors/></font>
<af:showMessages prefix="M_SavePattoLav"/>
<af:showMessages prefix="M_InsertPattoLav"/>
<af:showMessages prefix="DUPLICA_PATTO_MOD"/>
<af:showMessages prefix="M_InvioSapYG"/> 
<af:showMessages prefix="M_InvioPtOnline"/>
<af:showMessages prefix="M_ModificaPattoPtOnline"/>
<%if(isConsenso){%>
	<af:showMessages prefix="M_VerificaAmConsensoFirma"/>
<%}%>

<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="doCheckSubmit()">

<center>
<%out.print(htmlStreamTop);%>
<table class="main" border="0">
<tr>
  <td colspan="6" class="azzurro_bianco">
  <!--<div style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:lightblue">-->
    <!--<%out.print(htmlStreamTop);%>-->
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr><td class="etichetta2">Stato&nbsp;Patto</td>
        <td>
        <table cellpadding="0" cellspacing="0" border="0" width="100%"><tr>
        <%
            String codAtto = Utils.notNull(CODSTATOATTO);
            if (codAtto.equals("")) codAtto = "PP";
            
        %>
        <td><af:comboBox classNameBase="input" name="CODSTATOATTO"  moduleName="M_STATOATTOPATTO" selectedValue="<%=codAtto%>"
                         addBlank="true" blankValue="" required="true" title="Stato patto" 
                         disabled="<%=\"true\"/*readOnlyString*/%>" onChange="fieldChanged();controllaStato(this);"/>
        </td>
        <td align="right">anno&nbsp;</td>
        <td><af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="text" name="annoProt" value="<%=annoProt!=null ? annoProt : \"\"%>"
                                     readonly="true" title="Anno protocollo" size="5" maxlength="4"/></td>
        <td align="right"> &nbsp;&nbsp;num.&nbsp;</td>
        <td><af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="text" name="numProt" value="<%=numProt%>"
                                     readonly="true" title="Numero protocollo" size="8" maxlength="100"/></td>
            <td class="etichetta2">data
               <af:textBox name="dataProt" 
                           type="date" 
                           value="<%=dataProt%>" 
                           size="11" 
                           maxlength="10"
                           title="data di protocollazione"  
                           classNameBase="input" 
                           readonly="true" 
                           validateOnPost="true" 
                           required="false" 
                           trim ="false" 
                           onKeyUp="cambiAnnoProt(this,annoProt)" 
                           onBlur="checkFormatDate(this)"
                /></td>
       		<% if (ProtocolloDocumentoUtil.protocollazioneLocale()) { %>
            <td class="etichetta2">ora
               <af:textBox name="oraProt"
                           type="text"
                           value="<%=oraProt%>"
                           size="6" 
                           maxlength="5"
                           title="data di protocollazione"  
                           classNameBase="input" 
                           readonly="true"
                           validateOnPost="false" 
                           required="false" 
                           trim ="false"
                      /></td>
            <% } else { %>
            <td><input type="hidden" name="oraProt" value="00:00">
            <% } %>
        </tr>
        </table>
    </td></tr>
    <tr><td class="etichetta2">Doc. di</td>
    <td>
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
        <tr><td class="campo2">
               <af:textBox name="docInOrOut" type="text"
                           value="<%=(docInOrOut.equalsIgnoreCase(\"I\")) ? \"Input\" : \"Output\"%>"
                           size="6" 
                           maxlength="6"
                           title="data di protocollazione"  
                           classNameBase="input" 
                           readonly="true"
                           validateOnPost="false" 
                           required="false" 
                           trim ="false"
                           /></td>
            <td class="etichetta2">Rif.</td>
            <td class="campo2">
               <af:textBox name="docInOrOut" type="text"
                           value="<%=docRif%>"
                           size="<%=docRif.length()+5%>"
                           title="data di protocollazione"  
                           classNameBase="input" 
                           readonly="true"
                           validateOnPost="false" 
                           required="false" 
                           trim ="false"
                           /></td>
        </tr>
        </table>
    </td>
    </tr></table>


    
  <!--</div>--></td>
</tr>  
<tr><td><br/></td></tr>



<table class="main">
<tr>
    <td colspan=2>
        <table>
          	
            <tr>
            	<td class="etichetta">Tipologia&nbsp;</td> 
                <td>
                  <input type="hidden" name="flgPatto297" value="<%=p.getFlgPatto297()%>" size="10" maxlength="10">
                  <af:comboBox name="codCodificaPatto" classNameBase="input"  addBlank="true" required="true" title="Tipologia" 
                  		moduleName="M_GetCodificaTipoPatto" selectedValue="<%=p.getCodificaPatto()%>"
                      	onChange="fieldChanged();settaFlag297() && solo297(this) && showIfsetVar() && hasDichiarazioneDisponibilita() && permettiAccordo();" 
                      	disabled="<%=String.valueOf(!canModify || isProtocollato)%>" />
                </td>
                <%if(configPattoOnLine.equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {%>  
                <td class="etichetta">Patto/Accordo stipulato con modalità online&nbsp;</td> 
                <td>
                  <af:comboBox name="flgPattoOnLine" classNameBase="input"  addBlank="true" required="false" title="Patto stipulato con modalità online" 
                  		moduleName="M_GenericComboSiNo" selectedValue="<%=flgPattoOnLine%>"
                      	onChange="fieldChanged();visualizzaSezionePattoOnLine();" 
                      	disabled="<%=String.valueOf(!canModify || isProtocollato)%>" />
                </td>
	                <%if(isProtocollato && StringUtils.isEmptyNoBlank(p.getDatFine()) 
	                		&& (StringUtils.isEmptyNoBlank(flgPattoOnLine) || flgPattoOnLine.equalsIgnoreCase("N"))) {%>  
	                		<td colspan="2" align="center">
						    	<input class="pulsanti" type="button" name="btnModificaPattoOnLine" value="Modifica in patto/accordo online"  
						    		onclick="makePattoOnLine();"/>
						     </td>
	                 <%}%>
                 <%} else if( isPattoOnLine) {%>  
                <td class="etichetta">Patto/Accordo stipulato con modalità online&nbsp;</td> 
                <td>
                  <af:comboBox name="flgPattoOnLine" classNameBase="input"  addBlank="true" required="false" title="Patto stipulato con modalità online" 
                  		moduleName="M_GenericComboSiNo" selectedValue="<%=flgPattoOnLine%>"
                      	disabled="true" />
                </td>
                 <%}%>
            </tr>
            <%if (!flag_insert && !codTipoPatto.equalsIgnoreCase(PattoBean.DB_ADESIONE_NUOVO_PROGRAMMA)) {%>  
	            <tr>
	                <td class="etichetta2">Misure concordate&nbsp;</td>
	                <td nowrap>
	                    <af:comboBox name="codTipoPatto" classNameBase="input"  addBlank="true" required="true" title="Misure concordate" 
	                        selectedValue="<%=codTipoPatto%>"
                        moduleName="M_GETDETIPOPATTO" onChange="fieldChanged();visualizzaSezioneADR();checkMisura();" disabled="<%=String.valueOf(!obiettiviDelPattoModify)%>" />
	                                    
	                </td>
	            </tr>
            <%}%>
        </table>
    </td>
</tr>
<tr><td><br></td></tr>
<tr>
  <td colspan="2"><!-- Le tre etichette potrebbero diventare diverse --->
    <div id="titoloIni" style="display:"     class="sezione2">Informazioni amministrative collegate (selezionare la tipologia)</div>
    <div id="titolo297" style="display:none" class="sezione2">Informazioni amministrative collegate</div>
    <div id="titoloAcc" style="display:none" class="sezione2">Informazioni amministrative collegate</div>
  </td>
</tr>      

<tr><td colspan="2">
<div id="infoPatto1" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td class="etichetta"> 
      Data inserimento nell'elenco anagrafico 
    </td>
    <td class="campo">    
    <% if (!(p.isAccordoGenerico() && p.getDatInizio().equals(""))){ 
    		// se il lavoratore e' in mobilita', non ha la did anche se disoccupato, e lo stato e' PP, allora il record
    		// non e' associato alla did quindi non viene estratta la data dell'elAnag. Per cui bisogna prendere
    		// quella corrente.
    		// Savino 13/10/05: purtroppo questo non e' piu' vero. Bisogna decidere quali devono essere le info
    		//		da visualizzare quando il patto e' ancora in pp
    		/*
    		if (iscrittoMobilita && Utils.notNull(CODSTATOATTO).equals("PP") && Utils.notNull(FLGPATTO297).equals("S")) 
    			datInizio = dataInizioElAnagCorrente;
    	TracerSingleton.log("",TracerSingleton.DEBUG, "iscrittoMobilita="+iscrittoMobilita + ", CODSTATOATTO="+CODSTATOATTO+", FLGPATTO297="+FLGPATTO297 + ", dataInizioElAnagCorrente="+dataInizioElAnagCorrente);
    	*/
    %>
      <af:textBox name="datInizio" value="<%=p.getDatInizio()%>" classNameBase="input" type="date" validateOnPost="true" 
                  readonly="true" required="false" title="Data inserimento nell'elenco anagrafico" onKeyUp="fieldChanged();"
                  size="12" maxlength="10"  validateWithFunction="" />&nbsp;*
    <%} else {  // accordo ma manca l'iscrizione all'elenco anagrafico %>
        <input name="datInizio" value="">
    <%}%></td>
  </tr>
</table></div>
</td></tr>

<tr><td colspan="2">
<div id="infoPatto2" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td class="etichetta"> 
      <%-- Savino 22/02/2006: sostituita etichetta<div id="labelVisulizza" style="display">Cpi titolare dei dati&nbsp;</div> --%>
      <div id="labelVisulizza" style="display">Cpi competente&nbsp;</div>
      <div id="labelNascondi" style="display:none">Cpi con cui &egrave; stato stipulato l'accordo</div>
    </td>
    <td>
     <input name="codCPI" value="<%=p.getCodCpi()%>" type="hidden"  size="10" maxlength="10"  >    
     <af:textBox name="codCPI2" value="<%=p.getDescCpi()%>" classNameBase="input" type="text" 
           onKeyUp="fieldChanged();" validateOnPost="true" required="true" title="CpI" readonly="true" size="32" maxlength="32"/>
    </td>    
  </tr>
</table></div>
</td></tr>

<tr><td colspan="2">
<div id="infoPatto3" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td colspan="1" class="etichetta">Data immediata disponibilità</td>
    <td colspan="1">    
    <% if (!(p.isAccordoGenerico() && p.getDatDichiarazione().equals(""))){ %>
      <af:textBox name="datDichDisponibilita" value="<%=p.getDatDichiarazione()%>" classNameBase="input" type="date" 
                  required="false" readonly="true" title="Data immediata disponibilità" validateOnPost="false"
                  onKeyUp="fieldChanged();" size="12" maxlength="10" validateWithFunction="" />&nbsp;*
    <%} else {%>
    <input name="datDichDisponibilita">
    <%}%>
    </td>
  </tr>
</table></div>
</td></tr>

<tr><td colspan="2">
<div id="infoPatto4" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
<tr>
  <td class="etichetta">Stato occupazionale</td>
  <td  class="campo">
    <input type="hidden" name="prgStatoOccupaz" value="<%=p.getPrgStatoOccupaz()%>" size="10" maxlength="10"  >            
    <%
    /*
      if(descStato != null)
      { size = descStato.length()+3;
        if(size > max) 
        { descStato = descStato.substring(0,max)+"...";
          size = max + 4;
        }
      }
      */
      if (p.getFlgPatto297().equalsIgnoreCase("") || p.getFlgPatto297().equalsIgnoreCase("S")) {
    	  size = p.getDescrizioneStatoTagliato(max).length() + 4;
    	  %>
    	  <af:textBox name="prgStatoOccupaz2" value="<%=p.getDescrizioneStatoTagliato(max) %>" classNameBase="input"
	          readonly="true" required="false" title="Stato occupazionale" onKeyUp="fieldChanged();" 
	          size="<%=size%>" maxlength="100"/>
    	  <%
      }
      else {
    	  size = descrizioneStatoOccAccordo.length() + 4;
    	  %>
    	  <af:textBox name="prgStatoOccupaz2" value="<%=descrizioneStatoOccAccordo %>" classNameBase="input" 
	          readonly="true" required="false" title="Stato occupazionale" onKeyUp="fieldChanged();" 
	          size="<%=size%>" maxlength="100"/>
    	  <%
      } 
    %>
  </td>
</tr>
</table></div>
</td></tr>

  <tr >
    <td valign="top" colspan="2"><div class="sezione2"/></div></td>
  </tr>

  <tr>
    <td class="etichetta">Data stipula &nbsp;</td>
    <td>
      <table cellpadding="0" cellspacing="0" border="0" width="100%">
        <tr><td width="25%" nowrap>
            <af:textBox name="DATSTIPULA" value="<%=p.getDatStipula()%>" classNameBase="input" type="date"
                        readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();"  validateOnPost="true"
                        required="true" title="Data stipula" 
                        size="12" maxlength="10"/>
            </td>
            <% if (p.getCodServizio().equals("")){ %>
            	<td colspan="2"><input type="hidden" name="CODSERVIZIO" value=""></td>
            <%} else {%>
            	<td class="etichetta">Servizio che stipula il patto/accordo</td>
            	<td class="campo">
            	<af:comboBox name="CODSERVIZIO" size="1" title="Codice servizio"
	                             multiple="false" disabled="true" required="false"
	                             focusOn="false" moduleName="COMBO_SERVIZIO" classNameBase="input"
	                             selectedValue="<%=p.getCodServizio() %>" addBlank="true" blankValue=""/>
	            </td>
             <%}%>           
        </tr>
      </table>
    </td>    
</tr>

<%if (!dataStipulaOrig.equals("")) {%>
	<tr>
	<td class="etichetta">Data stipula originaria &nbsp;</td>
	<td class="campo">
	   <af:textBox name="DATASTIPULAORIGVA18" value="<%=dataStipulaOrig%>" type="date"
	       readonly="true" classNameBase="input" title="Data stipula originaria" size="12" maxlength="10"/>    
	</td>
<%}%>
  
  <!--tr >
    <td class="etichetta"><br/>Impegno di comuncazione esiti</td><td></td>
  </tr-->
  <tr><td><br></td></tr>
<!-- Borriello maggio 2020 Patto online -->
<%if(isPattoOnLine){ 
		
	String readOnlyStrIdCoab = String.valueOf(!canModify ||  !canInvioPtOnLine);
	if(configPattoOnLine.equalsIgnoreCase(Properties.DEFAULT_CONFIG)){
		readOnlyStrIdCoab ="true";
		canButtonPtOnLine = false;
		canReinvioPtOnLine = false;
		canStatoPtOnLine  = false;
	}

%>
<tr>
  <td colspan="2">
    <div id="sezpattoonline" style="display:none" class="sezione2">Patto/Accordo On Line</div>
  </td>
</tr>
 	<tr><td colspan="2">
 	<div id="ptOnlineSez" style="display: none;">
	<table cellpadding="1" cellspacing="0" border="0" width="100%">
	<tr>
	    <td class="etichetta" nowrap>Codice di abilitazione ai servizi amministrativi</td>
	    <td class="campo">
	    	<af:textBox name="STRCODABIPORTALE" value="<%=codiceAbilitazioneServizi%>" type="text" onKeyUp="fieldChanged();"
	           readonly="<%=readOnlyStrIdCoab%>" classNameBase="input"  validateOnPost="true"                      
	           title="Codice di abilitazione ai servizi amministrativi" size="15" maxlength="10"/>
	     </td>
	</tr>
	<tr>
	    <td class="etichetta" nowrap>Data e ora invio per accettazione</td>
	    <td class="campo">
	    	<af:textBox name="DTMINVIOPORTALE" value="<%=dataInvioAccettazioneServizi%>" type="text"
		         readonly="true" classNameBase="input"                  
		         title="Data e ora invio per accettazione" size="20"  />
	     </td>
	</tr>
	<tr>
	    <td class="etichetta" nowrap>Stato Patto On Line</td>
	    <td class="campo">
	    	<af:comboBox classNameBase="input" name="CODMONOACCETTAZIONE"  moduleName="M_ComboStato_PattoOnLine" selectedValue="<%=statoPattoOnLine%>"
                         addBlank="true" blankValue=""   title="Stato Patto On Line" 
                         disabled="true"  />
	     </td>
	</tr>
	<tr>
	    <td class="etichetta" nowrap>Data e ora accettazione</td>
	    <td class="campo">
	    	<af:textBox name="DTMACCETTAZIONE" value="<%=dataAccettazioneServizi%>" type="text"
		         readonly="true" classNameBase="input"                  
		          title="Data e ora accettazione"  size="20"  />
	     </td>
	</tr>
	<tr>
	    <td class="etichetta" nowrap>Data e ora ultima stampa</td>
	    <td class="campo">
	    	<af:textBox name="DTMULTIMASTAMPA" value="<%=dataUltimaStampa%>" type="text"
		         readonly="true" classNameBase="input"                  
		         title="Data e ora ultima stampa" size="20"  />
	     </td>
	<%if(canButtonPtOnLine && isProtocollato && canInvioPtOnLine){ %>
     <td colspan="2" align="center">
    	<input class="pulsanti" type="button" name="btnInvioPattoOnLine" value="Invio patto/accordo per presa visione"  
    		onclick="submitPattoOnLine('invio');"/>
     </td>
     <%} %>
    <%if(StringUtils.isEmptyNoBlank(p.getDatFine())&& StringUtils.isFilledNoBlank(flgReinvioPattoOnLine) &&  canReinvioPtOnLine && isProtocollato && flgReinvioPattoOnLine.equalsIgnoreCase("S")){ %>
     <td colspan="2" align="center">
    	<input class="pulsanti" type="button" name="btnReinvioPattoOnLine" value="Invio aggiornamento patto/accordo"  
    		onclick="submitPattoOnLine('reinvio');"/>
     </td>
     <%} %>
	<%if(isProtocollato && canStatoPtOnLine && StringUtils.isFilledNoBlank(statoPattoOnLine) && !statoPattoOnLine.equalsIgnoreCase("A")){ %>
     <td colspan="2" align="center">
    	<input class="pulsanti" type="button" name="btnStatoPattoOnLine" value="Verifica accettazione patto/accordo"  
    		onclick="submitPattoOnLine('stato');"/>
     </td>
     <%} %>	  
	</tr>
	
	  <%if(canStoricoPtOnLine && storicoPattoOnLine){ %>
	  <tr>
	  <td class="etichetta" nowrap>&nbsp;</td>
	    <td class="campo">
	    	&nbsp;&nbsp;
	     </td>
      <td colspan="2" align="center">      	
     	 <input type="button" name="btnStoricoPattoOnLine" class="pulsanti<%=((storicoPattoOnLine)?"":"Disabled")%>"  
     	 value="Storico invii patto/accordo" onclick="javascript:storicoInviiPtOnLine();"
      <%=(!storicoPattoOnLine)?"disabled=\"True\"":""%>>
     </td>
     </tr>
     <%} %>	  
	</table>
	 </div>
	</td>
	</tr>

	<tr><td><br></td></tr>
<%}else{ %>
	<input type="hidden" name="STRCODABIPORTALE" value="<%=Utils.notNull(codiceAbilitazioneServizi)%>" />
	<input type="hidden" name="flgPattoOnLine" value="<%=Utils.notNull(flgPattoOnLine)%>" />
<%}  %>
<!-- fine patto on line -->  
<tr>
  <td colspan="2">
    <div id="sezprofiling" style="display:" class="sezione2">Profiling (Garanzia Giovani)</div>
  </td>
</tr>

<%if (flag_insert || DateUtils.compare(dataStipulaPattoCheck, MessageCodes.General.DATA_PROFILING_2015) < 0) {%>
	<tr><td colspan="2">
	<table cellpadding="1" cellspacing="0" border="0" width="100%">
	<tr>
	    <td class="etichetta">Indice di svantaggio</td>
	    <td class="campo">
	    	<af:textBox name="INDICESVANTAGGIO" value="<%=indiceSvantaggio%>" type="number"
	           readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();" classNameBase="input"  validateOnPost="true"                  
	           title="Indice di svantaggio" size="5" maxlength="1"/>
	     </td>
	</tr>
	</table>
	</td>
	</tr>
<%} else {%>
  	<tr>
	<td>
	<input type="hidden" name="INDICESVANTAGGIO" value="<%=indiceSvantaggio%>">
	</td>
	</tr>
<%}%>

<tr><td colspan="2">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
<tr>
    <td class="etichetta">Indice GG</td>
    <td class="campo">
    	<af:textBox name="INDICESVANTAGGIO2" value="<%=indiceSvantaggio2%>" type="number"
	         readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();" classNameBase="input"  validateOnPost="true"                  
	         title="Indice GG" size="5" maxlength="1"/>
     </td>
</tr>
</table>
</td>
</tr>

<tr><td colspan="2">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
<tr>
    <td class="etichetta">Data riferimento</td>
    <td class="campo">
    	<af:textBox name="DATRIFERIMENTOINDICE" value="<%=dataRiferimentoIndice%>" type="date"
	       	readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();" classNameBase="input"  validateOnPost="true"                  
	           title="Data riferimento" size="12" maxlength="10"/>
     </td>
     <%if(viewStoricoProfGG){ %>
     <td colspan="2" align="center">
    	<input class="pulsanti" type="button" name="btnGetProfilingGG" onclick="goProfilingGG();" value="Storico Profiling GG"/>
     </td>
     <%} %>
</tr>
</table>
</td>
</tr>
<tr><td><br></td></tr>

<tr>
  <td colspan="2">
    <div id="sezprofiling" style="display:" class="sezione2">Profiling (Dlgs 150)</div>
  </td>
</tr>

<tr><td colspan="2">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
<tr>
    <td class="etichetta">Indice Dlgs 150</td>
    <td class="campo">
    	<af:textBox name="INDICESVANTAGGIO150" value="<%=indiceSvantaggio150%>" type="number"
	         readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();valorizzaCategoriaIS150(this);" classNameBase="input"  validateOnPost="true"                  
	         title="Indice Dlgs 150" size="12" maxlength="11"/>
	    <input type = "hidden" name ="NUMPROFILING" value="<%= numProfiling150%>">
	    &nbsp;&nbsp;&nbsp;<af:textBox name="CATEGORIAINDICESV150" classNameBase="input" readonly="true" value="<%= strProfiling150%>"/>
     </td>
</tr>
</table>
</td>
</tr>

<tr><td colspan="2">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
<tr>
    <td class="etichetta">Data riferimento</td>
    <td class="campo">
    	<af:textBox name="DATRIFERIMENTOINDICE150" value="<%=dataRiferimentoIndice150%>" type="date"
	       	readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();" classNameBase="input"  validateOnPost="true"                  
	           title="Data riferimento Dlgs 150" size="12" maxlength="10"/>
     </td>
</tr>
</table>
</td>
</tr>

<%if (numConfigVoucher.equalsIgnoreCase(Properties.CUSTOM_CONFIG)){%>
	<tr><td><br></td></tr>
	<tr>
	  <td colspan="2">
	    <div id="sezprofilingvoucher" style="display:" class="sezione2">Profiling (voucher)</div>
	  </td>
	</tr>
	
	<tr><td colspan="2">
	      <table cellpadding="1" cellspacing="0" border="0" width="100%">
	        <tr>
	        	<td class="etichetta" nowrap>Indice</td>
	        	<td class="campo">
	        		<input type="hidden" name="CODVCHPROFILING" value="<%=p.getCodVchProfiling()%>">
	            	
	            	<af:comboBox name="TXTCODVCHPROFILING" size="1" title="Indice" selectedValue="<%=p.getCodVchProfiling()%>" multiple="false" required="false"
                    	focusOn="false" moduleName="M_GET_VCH_PROFILING" classNameBase="input" addBlank="true" blankValue="" disabled="true"/>
                    
                    <af:comboBox name="CODVCHPROFILINGPROFILO" size="1" title="Indice" selectedValue="<%=prgLavoratoreProfilo%>"
                    	multiple="false" onChange="fieldChanged();ricavaDoteProfilo(this);" required="false"
                    	focusOn="false" moduleName="M_GET_VCH_PROFILING_FROM_PROFILO" classNameBase="input"
                    	addBlank="true" blankValue="" disabled="<%=readOnlyString%>"/>
	            </td>
	            <td class="etichetta" nowrap>Dote a processo</td>
	            <td class="campo">
	              <af:textBox name="decDoteProcessoAssegnato" value="<%=decDoteProcessoAssegnato%>" type="number"
	         		readonly="true" classNameBase="input" title="Dote a processo" size="5"/>    
	            </td>   
	            <td class="etichetta" nowrap>Residuo Dote a processo</td>
	            <td class="campo">
	              <af:textBox name="decDoteProcessoResidua" value="<%=decDoteProcessoResidua%>" type="number"
	         		readonly="true" classNameBase="input" title="Residuo Dote a processo" size="5"/>    
	            </td>         
	        </tr>
	        <tr>
	        	<td nowrap>&nbsp;</td>
	        	<td nowrap>&nbsp;</td>
	            <td class="etichetta" nowrap>Dote a risultato</td>
	            <td class="campo">
	              <af:textBox name="decDoteRisultatoAssegnato" value="<%=decDoteRisultatoAssegnato%>" type="number"
	         		readonly="true" classNameBase="input" title="Dote a risultato" size="5"/>    
	            </td>   
	            <td class="etichetta" nowrap>Residuo Dote a risultato</td>
	            <td class="campo">
	              <af:textBox name="decDoteRisultatoResidua" value="<%=decDoteRisultatoResidua%>" type="number"
	         		readonly="true" classNameBase="input" title="Residuo Dote a risultato" size="5"/>    
	            </td>         
	        </tr>
	        
	      </table>
	    </td>
	</tr>
	
	<tr><td><br></td></tr>
<%} else {%>
	<tr>
	<td>
	<input type="hidden" name="CODVCHPROFILING" value="<%=p.getCodVchProfiling()%>">
	<input type="hidden" name="CODVCHPROFILINGPROFILO" value="<%=prgLavoratoreProfilo%>">
	<input type="hidden" name="decDoteProcessoAssegnato" value="<%=decDoteProcessoAssegnato%>">
	<input type="hidden" name="decDoteProcessoResidua" value="<%=decDoteProcessoResidua%>">
	<input type="hidden" name="decDoteRisultatoAssegnato" value="<%=decDoteRisultatoAssegnato%>">
	<input type="hidden" name="decDoteRisultatoResidua" value="<%=decDoteRisultatoResidua%>">
	<br>
	</td>
	</tr>
<%}%>

<tr>
  <td colspan="2">
    <div id="sezADR" style="display:none" class="sezione2">Assegno di ricollocazione</div>
  </td>
</tr>

<tr><td colspan="2">
<div id="infoADR" style="display:none">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td class="etichetta">Data inizio fruizione NASPI *</td>
    <td class="campo">
    <af:textBox name="datInizioNaspi" value="<%=datNaspi %>" classNameBase="input" type="date" validateOnPost="true" 
                  readonly="<%=readOnlyString%>" required="false" title="Data inizio fruizione NASPI" onKeyUp="fieldChanged();"
                  size="12" maxlength="10"  validateWithFunction="" />
    </td>
  </tr>
  <tr>
    <td class="etichetta">Importo massimo assegno di ricollocazione *</td>
    <td class="campo">
    <af:textBox name="decAssegnoNaspi" value="<%=decImportoAr%>" type="number"
		readonly="<%=readOnlyString%>" classNameBase="input" title="Importo massimo assegno di ricollocazione" size="9"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Elementi di attivazione richiesti</td>
    <td class="campo"> 
    	 <af:textArea name="noteElemAttivazione" value="<%=noteElemAttivazione%>"
                   cols="60" rows="4" maxlength="2000" classNameBase="textarea" 
                   readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();"/>
    </td>
  </tr>
</table>
</div>
</td></tr>

<tr><td><br></td></tr>

<tr>
    <td colspan="4">
        <table class='sezione2' cellspacing=0 cellpadding=0>
            <tr>
                <td  width=18>
                    <img id='IMG1' src='<%=img1%>' onclick='cambia(this, document.getElementById("TBL1"))'></td>
                        <td  class='titolo_sezione'>Chiusura patto/accordo</td>    				
                <td align='right' width='30'></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td colspan=4 align="center">
        <TABLE id='TBL1' style='width:100%;display:<%=display1%>'>     
        <script>initSezioni(new Sezione(document.getElementById('TBL1'),document.getElementById('IMG1'),<%=hasDataUscita%>));</script>
			<td class="etichetta">
                Data fine patto/accordo &nbsp;
            </td>
            <td class="campo">
                <af:textBox  name="DATFINE" value="<%=p.getDatFine()%>" classNameBase="input" type="date" 
                readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();" validateOnPost="true"   size="12" maxlength="10"/>
            </td>
			<tr>
			    <td class="etichetta">Motivo fine patto/accordo&nbsp;</td>
                <td class="campo">
                    <af:comboBox name="CODMOTIVOFINEATTO" moduleName="M_MOTFINEATTOPATTO" selectedValue="<%=p.getCodMotivoFineAtto() %>" 
                          disabled="<%=readOnlyString%>" onChange="fieldChanged();" classNameBase="input" addBlank="true"/>
                </td>
			</tr>
        </table>
    </td>
</tr>

<tr><td><br></td></tr>

  <tr>
    <td class="etichetta">
      Note&nbsp;
    </td>
    <td class="campo">
      <af:textArea name="strNote" value="<%=p.getStrNote()%>"
                   cols="60" rows="4" maxlength="100" classNameBase="textarea" 
                   readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();"/>
    </td>
  </tr>


  <%if (!flag_insert) {%>
  <tr>
  <td>
    <input type="hidden" name="NUMKLOPATTOLAVORATORE2" value="<%=p.getNumKlockPattoLavoratore()%>" size="10" maxlength="10"  >
    <%--NUMKLOPATTOLAVORATORE= NUMKLOPATTOLAVORATORE.add(new BigDecimal(1)); --%>
    <input type="hidden" name="NUMKLOPATTOLAVORATORE" value="<%=p.getNextKlock()%>">
  </td>        
  </tr>
<%}%>
</table>
<BR><center><% operatoreInfo.showHTML(out);%></center>

<BR><% String NomePagina = "";%>

<table class="main" border="0">

<% if (!flag_insert) { %>
<tr>
   <td width="33%" align="left">
      <input class="pulsante" type="button" name="Documenti associati" value="Documenti associati"
             onClick="docAssociati('<%=cdnLavoratore%>','<%=_page%>','<%=cdnFunzione%>','','<%=p.getPrgPattoLavoratore() %>')">
   </td>
  <td align="center">
  	<%if(canModify){%>
  		<input type="hidden" name="Salva" class="pulsanti" value="Aggiorna" disabled=true>
  		<input type="submit" name="Salva_button" class="pulsanti" id="aggiornaSubmit" value="Aggiorna">
  	<%}%>
  </td>
  <td align="right">    
    <%if(canEdit && !CODSTATOATTO.equals("PR")){%>
    	<input name="Invia" type="button" class="pulsanti" value="Stampa patto/accordo"
        	   onclick="controllaStampaPatto(false)">        
    <%}%>
  </td>
</tr>
<tr><td><br/></td></tr>
<tr>
  <!--<td width="33%"></td>-->
  <td align="left" width="33%">&nbsp;
    <%if(canInsert){%>
      <%-- Landi 23/01/2019: il pulsante viene nascosto per evitare di inserire un nuovo patto in presenza di uno ancora aperto
      <input type="submit" class="pulsanti" name="Inserisci" value="Inserisci nuovo" onclick="settaOperazioneNuovoPatto('true');">&nbsp;&nbsp;
      --%>
      <%-- Savino 05/10/2005: il pulsante viene nascosto per evitare che le azioni vengano associate a due patti
      <input type="submit" class="pulsanti" name="DuplicaPatto" value="Inserisci nuovo da precedente" onClick="nuovoPattoDaPrec(true);">
      --%>
    <%}%>
  </td>
  <td align="center">
  </td>
  <td align="right" width="33%">
  	<%if(CODSTATOATTO.equals("PR") && canEdit && canRistampa) {%>
  		<input name="Ristampa" type="button" class="pulsanti" value="Ristampa patto/accordo" onclick="controllaStampaPatto(true)">
  	<%}%>
    <%if(infStorButt){%>
      <input type="button" name="infSotriche" class="pulsanti<%=((storicoPatto)?"":"Disabled")%>" value="Informazioni storiche" onClick="apriInfoStoriche('<%= cdnLavoratore %>')"
      <%=(!storicoPatto)?"disabled=\"True\"":""%>>
    <%}%>
  </td>
</tr>


<%-- inserito il pulsante di invio protocollazione Pi3 --%>    <tr>
	    <td align="right" width="25%">
	    <%if(canPi3) { %>
	    	<input class="pulsanti<%=((canPi3)?"":"Disabled")%>" onclick="javascript:callPi3()" type="button" name="protocollazionePi3" value="Protocollazione PiTre">
	    <%}%>
	    </td>
	    <td align="center" width="25%" ></td>
	    <td align="left" width="25%" ></td>
	    <td align="center" width="25%" ></td>
   	</tr>
   	
   	<%-- pulsante di Visualizzazione Conferimenti DID --%>
   	<tr>
	    <td align="left" width="25%">
	    <%if(canConferimentiDID) { %>
	    	<input class="pulsanti<%=((canConferimentiDID)?"":"Disabled")%>" onclick="javascript:callConferimentiDID()" type="button" name="conferimentiDID" value="Visualizza conferimento DID">
	    <%}%>
	    </td>
	    <td align="center" width="25%" ></td>
	    <td align="left" width="25%" ></td>
	    <td align="center" width="25%" ></td>
   	</tr>

<%} else {%>
<!--tr>
  <td></td>controllaPatto()
  <td align="center"><!-- <input type="submit" name="Salva" value="Aggiorna"></td> -->
  <!--td align="right"><!--<input type="submit" name="Cancella" value="cancella il record">-->
    <!--input name="Invia" type="button" class="pulsanti" value="Stampa patto/accordo" onclick="sceglipage(2,null)">      
  </td>
</tr-->
<tr><td><br/></td></tr>
<tr>
   <td width="33%" align="center">      
   </td>
   <%if (canInsert){%>
   		<td width="33%" align="center">
   			<input type="hidden" class="pulsanti" name="insert_pattolav" value="Inserisci">
   			<input type="submit" class="pulsanti" name="insert_pattolav_button" id="inserisciSubmit" value="Inserisci">
   		</td>
   <%}%>
   <td width="33%" align="right">
     <%if(buttonAnnulla)
     {%><input class="pulsanti" type="button" name="annulla" value="Chiudi senza inserire" onclick="sceglipage(1);"><%}%>
   </td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
  <td colspan="3" align="right">
    <%if(infStorButt){%>
      <input type="button" name="infSotriche" class="pulsanti<%=((storicoPatto)?"":"Disabled")%>" value="Informazioni storiche" onClick="apriInfoStoriche('<%= cdnLavoratore %>')"
      <%=(!storicoPatto)?"disabled=\"True\"":""%>>
    <%}%>
  </td>
</tr>
<%} //else%>
</table>
<input type="hidden" name="PAGE" value="PattoLavDettaglioPage">
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
<%if (flag_insert || codTipoPatto.equalsIgnoreCase(PattoBean.DB_ADESIONE_NUOVO_PROGRAMMA)) {%>
<input type="hidden" name="codTipoPatto" value="<%=PattoBean.DB_ADESIONE_NUOVO_PROGRAMMA%>">
<%}%>
<!-- per creare un nuovo patto a partire dal precedente -->
<%if(!flag_insert){%>
<input type="hidden" name="DUPLICA" value="">
<%}%>
<%if (dataUltimoProtocolloModificato!=null && checkBtnModificheSuccessive) {%>
<input class="pulsanti" type="button" value="modifiche succ. al <%=dataUltimoProtocolloModificato%>" onclick="apriPaginaModifiche()">
<%}%>
<%--if (PRGPATTOLAVORATORE==null) PRGPATTOLAVORATORE=(BigDecimal)sessionContainer.getAttribute("PRGPATTOLAVORATORE");--%>

<input  type="hidden" name="PRGPATTOLAVORATORE" value="<%=p.getPrgPattoLavoratore()%>" size="22" maxlength="16">
<input  type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>" size="22" maxlength="16">

<input  type="hidden" name="DATSTIPULAORIG" value="<%=p.getDatStipula()%>">
<input  type="hidden" name="CODTIPOPATTOORIG" value="<%=p.getCodTipoPatto()%>">

<!-- variabile di appoggio usata per memorizzare il progresivo della dichiarazione di disp. utilizzata poi nello script showIfSetVar()-->
<input  type="hidden" name="PRGDICHDISP" value="<%= p.getPrgDichDisponibilita() %>">

<input type="hidden" name="PRGDICHDISPONIBILITA" value="<%= p.getPrgDichDisponibilita() %>"/>
<input type="hidden" name="REPORT" value="patto/patto.rpt">
<input type="hidden" name="PROMPT0" value="<%= cdnLavoratore %>">
<input type="hidden" name="OPINSERISCINUOVO" value="false">
<input type="hidden" name="CONFIGVOUCHER"  value="<%= numConfigVoucher %>">
<input type="hidden" name="skipControlli" value="<%= skipControlli %>">
<input type="hidden" name="CONSENSO" value="<%=isConsensoAttivo %>" />
<input type="hidden" name="codFiscaleSoggAcc"  value="<%=Utils.notNull(codFiscaleSoggAcc)%>">
<input type="hidden" name="codSedeSoggAcc"  value="<%=Utils.notNull(codSedeSoggAcc)%>">
<input type="hidden" name="strNoteEnte"  value="<%=Utils.notNull(noteAppuntamentoSoggAcc)%>">
<input type="hidden" name="DATSCADCONFERMA" value="<%= p.getDatScadConferma() %>"/>

<%out.print(htmlStreamBottom);%>
</af:form>
<af:form method="POST" action="AdapterHTTP" name="Frm2">
	<input type="hidden" name="PAGE" value="PattoLavDettaglioPage">
	<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
	<input  type="hidden" name="PRGPATTOLAVORATORE" value="<%=p.getPrgPattoLavoratore()%>">
	<input  type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>">
	<input type="hidden" name="operazionePtOnline" value="PTONLINE">
	<input type="hidden" name="controllaStato" value="">
</af:form>
<af:form method="POST" action="AdapterHTTP" name="Frm3">
	<input type="hidden" name="PAGE" value="PattoLavDettaglioPage">
	<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
	<input type="hidden" name="PRGPATTOLAVORATORE" value="<%=p.getPrgPattoLavoratore()%>">
	<input type="hidden" name="NUMKLOPATTOLAVORATORE" value="<%=p.getNextKlock()%>">
	<input type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>">
	<input type="hidden" name="modPtOnline" value="PTONLINE">
</af:form>

</body>
</html>