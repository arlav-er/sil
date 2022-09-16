<%--
    CODACCERTAMENTOSANITARIO: NELLA QUERY E' STATO ANNULLATO PER CUI RISULTERA' SEMPRE NULL.
--%>

<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %> 

        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs, 
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.util.*,
                  java.util.*,
                  it.eng.sil.util.amministrazione.impatti.Controlli,
                  com.engiweb.framework.error.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*,
                  java.text.DateFormat,
                  java.text.SimpleDateFormat,
                  it.eng.sil.module.movimenti.InfoLavoratore,
                  java.math.*,
                  it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil,
                  it.eng.sil.module.consenso.GConstants" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratoreStr= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratoreStr));
  	PageAttribs attributi = new PageAttribs(user, _page);

  	boolean canPrint=false;
	boolean infStorButt=false;
	boolean canInsert = false;
	boolean canConfermaDid = false;
	boolean buttonDidInps = true;
	boolean rdOnly=true;
	boolean flag_competenza = true;
	boolean canRiapriIscrL68 = false;
	boolean canRichiestaSAP = false;
	boolean canPi3 = false; // Protocollazione Pi3
	boolean viewProfilePi3 = false; // Profilatura per Protocollazione Pi3
	boolean canConferimentiDID = false; // Visualizza Conferimenti DID
	boolean viewProfileConferimentiDID = false; // Profilatura per Conferimenti DID
	
	BigDecimal prgDocumento = null;
	String prgDocumentoStr = "";
	String motivoAnnullamento = "";
	boolean canView=filter.canViewLavoratore();
	boolean canAnnullaDid = false;
	if (!canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
    canPrint    =  attributi.containsButton("STAMPA");  
    infStorButt = attributi.containsButton("STORICO");
   	canInsert = attributi.containsButton("INSERISCI");
   	rdOnly     = !attributi.containsButton("AGGIORNA");
   	canAnnullaDid = attributi.containsButton("ANNULLADID"); 
   	canRiapriIscrL68 = attributi.containsButton("RIAPRIISCRL68");
   	canConfermaDid = attributi.containsButton("CONFIRM_DID");
   	buttonDidInps = attributi.containsButton("DIDINPS");
   	canRichiestaSAP = attributi.containsButton("RICHIESTA_SAP");
    	viewProfilePi3 = attributi.containsButton("PROTOCOLLO_PI3"); // Protocollazione Pi3
    viewProfileConferimentiDID = attributi.containsButton("VIEW_CONFERIMENTI_DID"); // Conferimenti DID
    	
   	if((!canInsert) && (rdOnly)){
   		//canInsert=false;
       //rdOnly=true;
   	}else{
       boolean canEdit=filter.canEditLavoratore();
       if (canInsert){
         canInsert=canEdit;
       }
       if (!rdOnly){
         rdOnly=!canEdit;
       }        
   	}
   	
    final String FORMATO_DATA     = "dd/MM/yyyy";
    SimpleDateFormat df = new SimpleDateFormat(FORMATO_DATA);
    // se questa pagina e' stato chiamata dal patto ....
    // questa variabile attiva un pulsante di ritorno al patto
    boolean fromPatto = serviceRequest.getAttribute("fromPattoDettaglio")==null?false:true;
    String target = null;
    // e' possibile che questa pagina venga aperta in una popUp dalla pagina del patto: in questo caso il refresh dalla pagina del documento deve
    // avvenire verso la popUp e non nel frame "main" del patto. A questo punto sara' necessario aggiornare la pagina del patto e chiudere la popUp 
    // Il nome del frame della popUp: se provengo dal patto allora ho di sicuro anche il nome del frame
    if (fromPatto) target = (String)serviceRequest.getAttribute("FRAME_NAME");
    else target="main";
    //
    //mi serve per regione calabria
    InfoProvinciaSingleton provinciaSil = InfoProvinciaSingleton.getInstance(); 
	String codProvinciaSil = provinciaSil.getCodice();
	//
    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    //String cdnLavoratoreStr = (String)serviceRequest.getAttribute("CDNLAVORATORE");
    String queryString = "PAGE=DispoDettaglioPage&CDNLAVORATORE="+cdnLavoratoreStr+"&CDNFUNZIONE="+cdnFunzione;
    if (fromPatto) {
    	queryString+="&fromPattoDettaglio=1";
    	queryString+="&FRAME_NAME="+target;
    }
    //
    String alertMsg=null; 
    BigDecimal cdnLavoratore = null;
    String     strCognome    = null;
    String     strNome       = null;
  
    BigDecimal prgDichDisponibilita = null;
    BigDecimal prgDichDisponibilitaConfirm = null;
    String     prgDichDisponibilitaStr = "";
    String     CODSTATOATTO = null;
    String     CODSTATOATTOANNULLA = null;
    String     datDichiarazione   = null;
    String 	   datDichiarazioneAnnulla = null;
    String 	   datDichiarazioneRiapri = null;
    BigDecimal prgElencoAnagrafico= null ;
    String     CODTIPODICHDISP    = null;
    String     CODULTIMOCONTRATTO = null;
    BigDecimal PRGSTATOOCCUPAZ    = null;
    String     CODSTATOOCCUPAZ    = null;
    String     descStato          = null;
    String     DATSCACDNUTINSERMA = null;
    String     DATSCADCONFERMA    = null;
    String     DATSCADEROGAZSERVIZI=null;
    String     STRNOTE            = null;
    String     CODMOTIVOFINEATTO  = null;
    String     DATFINE            = null;
    String	   NUMDELIBERA        = null;
    BigDecimal cdnUtIns           = null;
    String     dtmIns             = null;
    BigDecimal cdnUtMod           = null;
    String     dtmMod             = null;
    BigDecimal NUMKLODICHDISP     = null;
    BigDecimal NUMKLOPATTO 		  = null;
    String 	   NUMKLOPATTOStr 	  = "";
    String 	   NUMKLODICHDISPStr  = "";
    String     datInizio          = null;
    String     codCPI             = null;    
    String     descCPI            = null;
    String     codStatoOccRaggiunto = null;
    String     codMotivoRiaperturaAtto ="";
    String     flgRischioDisoccupazione = "";
    String 	   flgLavoroAutonomo = "";
    String	   datLicenziamento = null;
    String 	   flgDidL68 = "";
    String     numColloq = "";
    String     numStipP  = "";
    String     regione = "";
    String accertamentoSanitario = "";
    String dataInizioCM ="";
    String datInizioDocIdent = "";
    String datScadDocIdent = "";
    BigDecimal prgPatto = null;
  	String prgPattoStr = "";
    
    BigDecimal codCartaIdentita = new BigDecimal(0);
    String codCartaIdentitaStr = "";
    boolean flag_insert           = false;
    boolean buttonAnnulla         = false;
    String display0 = "none";
    //    String display1 = "none";
    String img0 = "../../img/chiuso.gif";    
    String img1 = "../../img/chiuso.gif";
    InfCorrentiLav testata=null;
    Testata operatoreInfo = null;
    String _cdnFunz = (String) serviceRequest.getAttribute("CDNFUNZIONE");
    String strErrorCode = "";
    String msgConferma = "";
    String moduleNameError = "";
    boolean confirmChiusura = false;
    boolean confirmRiapertura = false;
    boolean confirmAnnulla = false;
    SourceBean sbError = (SourceBean) serviceResponse.getAttribute("M_RIAPRIDID.RECORD.PROCESSOR.ERROR");
    if (sbError == null) {
    	sbError = (SourceBean) serviceResponse.getAttribute("M_AnnullaDid.RECORD.PROCESSOR.ERROR");
    	if (sbError == null) {
    		sbError = (SourceBean) serviceResponse.getAttribute("M_SaveDispo.RECORD.PROCESSOR.ERROR");
    		if (sbError != null) {
    			moduleNameError = "M_SaveDispo";
    			confirmChiusura = true;
    		}
    	}
    	else {
    		moduleNameError = "M_AnnullaDid";
    		confirmAnnulla = true;
    	}
    }
    else {
    	moduleNameError = "M_RIAPRIDID";
    	confirmRiapertura = true;
    }
    String forzaRicostruzione = "false";
    String continuaRicalcolo = "false";
    Vector rowsParam = null;
    SourceBean valoriParametri = null;
    if (sbError != null) {
    	strErrorCode = sbError.getAttribute("code").toString();
    	String errorStatoOccErrato = "" + MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA;
    	if (moduleNameError.equalsIgnoreCase("M_AnnullaDid") && strErrorCode.equals(errorStatoOccErrato)) {
    		msgConferma = "Esistono stati occupazionali di disoccupazione con anzianità diversa dalla data di stipula DID/iscrizione alla mobilità che potrebbero essere cancellati dal ricalcolo impatti oppure la DID viene annullata e vengono cancellati gli stati di disoccupazione corrispondenti.";	
    	}
    	else {
    		msgConferma = sbError.getAttribute("messagecode").toString();
    	}
		msgConferma = msgConferma + " Vuoi proseguire?";
    	rowsParam = serviceResponse.getAttributeAsVector(moduleNameError + ".RECORD.PROCESSOR.CONFIRM.PARAM");
    	if (rowsParam != null && rowsParam.size() > 0) {
    		valoriParametri = (SourceBean) rowsParam.get(0);
	    	forzaRicostruzione = valoriParametri.getAttribute("value").toString();
	    	valoriParametri = (SourceBean) rowsParam.get(1);
	    	continuaRicalcolo = valoriParametri.getAttribute("value").toString();
	   	}
    }   
    boolean operazioneFallita = !responseContainer.getErrorHandler().getErrors().isEmpty();
    //Gestione Privacy in fase di inserimento DID
    String dataPrivacy = "";
    String flgautoriz = "";
    SourceBean privacySb = null;
    //Controllo presenza obbligo scolastico
    SourceBean obbligoScol = null;
    String flgObbligo = "";
    String dataNascita = "";
    int eta = 0;

    Vector rowCpIComp  = serviceResponse.getAttributeAsVector("M_GETCPICORR.ROWS.ROW");
    if(rowCpIComp != null && !rowCpIComp.isEmpty()) {
      	SourceBean firstrow = (SourceBean) rowCpIComp.elementAt(0);
        String codCPItit  = (String) firstrow.getAttribute("CODCPITIT");
        String codmonotipocpi = Utils.notNull(firstrow.getAttribute("CODMONOTIPOCPI"));
        String codCpiUser = Utils.notNull(user.getCodRif());
        if (!codmonotipocpi.equals("C") || !codCPItit.equals(codCpiUser)){
			flag_competenza = false;
        }
    }
    
    String numConfigDidL68 = serviceResponse.containsAttribute("M_CONFIG_DID_L68.ROWS.ROW.NUM")?
  			serviceResponse.getAttribute("M_CONFIG_DID_L68.ROWS.ROW.NUM").toString():it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;
  	
  			
  	String numConfigDidSAP = serviceResponse.containsAttribute("M_CONFIG_SAP_DID.ROWS.ROW.STRVALORE")?
  	 			serviceResponse.getAttribute("M_CONFIG_SAP_DID.ROWS.ROW.STRVALORE").toString():it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;
  		  			
  			
    Vector rows= serviceResponse.getAttributeAsVector("M_GETDISPO.ROWS.ROW");
    /* 14/06/2004 da trigger 
    SourceBean ultimoStorico= (SourceBean)serviceResponse.getAttribute("M_UltimoStoricoDispo.ROWS.ROW");
    String ultimaDataFine = (String)ultimoStorico.getAttribute("datFine");*/
    if (rows.isEmpty())
    {   //flag_insert=true;
        //String cdnLavoratoreStr = (String) serviceRequest.getAttribute("CDNLAVORATORE");
        cdnLavoratore = new BigDecimal(cdnLavoratoreStr);
    } 
    
    if (rows != null && !rows.isEmpty()) {
        flag_insert = false;
        SourceBean row = (SourceBean) rows.elementAt(0);
        cdnLavoratore        = (BigDecimal) row.getAttribute("CDNLAVORATORE");
        //strCognome           = (String)     row.getAttribute("strCognome");          																																				 
        //strNome              = (String)     row.getAttribute("strNome");             																																	 
        prgDichDisponibilita = (BigDecimal) row.getAttribute("prgDichDisponibilita");																																	 
        CODSTATOATTO         = (String)     row.getAttribute("CODSTATOATTO");        																																	 
        PRGSTATOOCCUPAZ      = (BigDecimal) row.getAttribute("prgStatooccupaz");     																																	 
        CODSTATOOCCUPAZ      = (String)     row.getAttribute("codStatoOccupaz");     																																	 
        datDichiarazione     = (String)     row.getAttribute("datDichiarazione");    																																	 
        prgElencoAnagrafico  = (BigDecimal) row.getAttribute("prgElencoAnagrafico"); 																																	 
        CODTIPODICHDISP      = (String)     row.getAttribute("CODTIPODICHDISP");     																																	 
        CODULTIMOCONTRATTO   = (String)     row.getAttribute("CODULTIMOCONTRATTO"); 																																	 
        DATSCADCONFERMA      = (String)     row.getAttribute("DATSCADCONFERMA");     																																	 
        DATSCADEROGAZSERVIZI = (String)     row.getAttribute("DATSCADEROGAZSERVIZI");																																		 
        STRNOTE              = (String)     row.getAttribute("STRNOTE");             																																	 
        CODMOTIVOFINEATTO    = (String)     row.getAttribute("CODMOTIVOFINEATTO");   																																	 
        DATFINE              = (String)     row.getAttribute("DATFINE");
        NUMDELIBERA          = (String)     row.getAttribute("NUMDELIBERA");//  row.getAttribute("NUMDELIBERA") != null ? (BigDecimal)row.getAttribute("NUMDELIBERA") : null;
        NUMKLODICHDISP       = (BigDecimal) row.getAttribute("NUMKLODICHDISP");      																																	 
        datInizio            = (String)     row.getAttribute("datInizio");           																																	 
        codCPI               = (String)     row.getAttribute("codCpi");              																																	 
        descCPI              = (String)     row.getAttribute("descCPI");             																																	 
        descStato       = (String)     row.getAttribute("DESCRIZIONESTATO");
        codStatoOccRaggiunto = (String)row.getAttribute("codStatoOccupazRagg");
     	codMotivoRiaperturaAtto = StringUtils.getAttributeStrNotNull(row,"CODMOTIVORIAPERTURAATTO");
        cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
        dtmIns   = (String)     row.getAttribute("DTMINS");
        cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
        dtmMod   = (String)     row.getAttribute("DTMMOD");
        prgPatto = (BigDecimal) row.getAttribute("prgPatto");
        prgPattoStr = prgPatto!=null?prgPatto.toString():"";
        NUMKLOPATTO = (BigDecimal) row.getAttribute("numKloPatto");
     	accertamentoSanitario = Utils.notNull((String)row.getAttribute("CODACCERTSANITARIO"));
        dataInizioCM = Utils.notNull(row.getAttribute("dataInizioCM"));
        regione = Utils.notNull(row.getAttribute("CODREGIONE"));
        if (accertamentoSanitario.equals("N") || accertamentoSanitario.equals("M") || accertamentoSanitario.equals("A")) 
        CODTIPODICHDISP="OC";
        flgRischioDisoccupazione = StringUtils.getAttributeStrNotNull(row,"FLGRISCHIODISOCCUPAZIONE");
       	datLicenziamento = (String)     row.getAttribute("datLicenziamento");
       	flgDidL68 = StringUtils.getAttributeStrNotNull(row,"FLGDIDL68");
       	flgLavoroAutonomo = StringUtils.getAttributeStrNotNull(row,"FLGLAVOROAUTONOMO");
    } 
    else {
      int giorno,mese,anno;
      flag_insert = true;
      //Gestione dei numGG per la sipula del patto e del primo colloquio
      SourceBean numGg = (SourceBean)serviceResponse.getAttribute("M_GetNumGG.ROWS.ROW");
      numStipP = numGg.getAttribute("NUMGGSTIPULAPATTO").toString();
      numColloq = numGg.getAttribute("NUMGGCOLLOQUIOOR").toString();
      String dataDichiarazioneUtile = DateUtils.getNow();
      //prevalorizzo datDichiarazione per tutte le provincie e regione eccetto che per la calabria
      if(!codProvinciaSil.equalsIgnoreCase("79")){
    	  datDichiarazione = DateUtils.getNow();
    	  dataDichiarazioneUtile = datDichiarazione;
      } 
      giorno = Integer.parseInt(dataDichiarazioneUtile.substring(0,2));
      mese = Integer.parseInt(dataDichiarazioneUtile.substring(3,5));
      anno = Integer.parseInt(dataDichiarazioneUtile.substring(6,10));
      GregorianCalendar dataDich = new GregorianCalendar(anno,(mese-1),giorno);
      GregorianCalendar dataDichD = new GregorianCalendar(anno,(mese-1),giorno);
      //colloquio
      dataDich.set(Calendar.DATE,(giorno+Integer.parseInt(numColloq)));
      DATSCADCONFERMA = df.format(dataDich.getTime());
      //Stipula
      dataDichD.set(Calendar.DATE,(giorno + Integer.parseInt(numStipP)));
      DATSCADEROGAZSERVIZI = df.format(dataDichD.getTime());  
      
      Vector rowCpI  = serviceResponse.getAttributeAsVector("M_GETINFVALIDEDISPO.ROWS.ROW");
      if(rowCpI != null && !rowCpI.isEmpty()) {
         SourceBean row  = (SourceBean) rowCpI.elementAt(0);
         prgElencoAnagrafico  = (BigDecimal) row.getAttribute("prgElencoAnagrafico");
         PRGSTATOOCCUPAZ = (BigDecimal) row.getAttribute("PRGSTATOOCCUPAZ");
         descStato       = (String)     row.getAttribute("DESCRIZIONESTATO");
         if (descStato!=null && descStato.trim().equals(":")) descStato = "";
         datInizio       = (String)     row.getAttribute("DATINIZIO");
         descCPI         = (String)     row.getAttribute("CPITITOLARE");              																																	 
         codCPI          = (String)     row.getAttribute("CODCPITIT");         
         codStatoOccRaggiunto = (String)row.getAttribute("codStatoOccupazRagg");
         accertamentoSanitario = Utils.notNull((String)row.getAttribute("CODACCERTSANITARIO"));
         dataInizioCM = Utils.notNull((String)row.getAttribute("dataInizioCM"));
         if (accertamentoSanitario.equals("N") || accertamentoSanitario.equals("M") || accertamentoSanitario.equals("A")) 
            CODTIPODICHDISP="OC";
      }//if()

      //Gestione Privacy in fase di inserimento della DID
      privacySb = (SourceBean)serviceResponse.getAttribute("M_AutPrivacy.ROWS.ROW");
      if(privacySb!=null){
        dataPrivacy = (String)privacySb.getAttribute("datinizio");
        flgautoriz = (String)privacySb.getAttribute("flgautoriz");
      }

      //Controllo sulla presenza dell'obblico scolastico per lavoratori da 15 a 30 anni
      obbligoScol = (SourceBean)serviceResponse.getAttribute("M_GetObbligoFormativo.ROWS.ROW");
      if(obbligoScol!=null){
        flgObbligo = (String)obbligoScol.getAttribute("flgobbligoscolastico");
      }
      InfoLavoratore infoLav = new InfoLavoratore(new BigDecimal(cdnLavoratoreStr));
      dataNascita = infoLav.getDataNasc();
      eta =  DateUtils.getEta(dataNascita);
    }//else 
    
    
    if (confirmChiusura) {
    	CODMOTIVOFINEATTO = (String)serviceRequest.getAttribute("CODMOTIVOFINEATTO");
    	DATFINE = (String)serviceRequest.getAttribute("DATFINE");
    	NUMDELIBERA = (	serviceRequest.getAttribute("NUMDELIBERA") != null &&
    					!serviceRequest.getAttribute("NUMDELIBERA").toString().equals("")) ? 
    						//	new BigDecimal(serviceRequest.getAttribute("NUMDELIBERA").toString())
    					 serviceRequest.getAttribute("NUMDELIBERA").toString()
    					 :null;
    	STRNOTE = (String)serviceRequest.getAttribute("STRNOTE");
    }
    else {
    	if (confirmRiapertura) {
    		if (serviceRequest.getAttribute("numKloDid") != null && 
    		    !serviceRequest.getAttribute("numKloDid").toString().equals("")) {
    			NUMKLODICHDISP = new BigDecimal((String)serviceRequest.getAttribute("numKloDid"));
    			NUMKLODICHDISPStr = NUMKLODICHDISP!=null?NUMKLODICHDISP.toString():"";
    		}
    		if (serviceRequest.getAttribute("prgDichDisponibilita") != null && 
    		    !serviceRequest.getAttribute("prgDichDisponibilita").toString().equals("")) {
    			prgDichDisponibilitaConfirm = new BigDecimal((String)serviceRequest.getAttribute("prgDichDisponibilita"));
    			prgDichDisponibilitaStr = prgDichDisponibilitaConfirm!=null?prgDichDisponibilitaConfirm.toString():"";
    		}
    		STRNOTE = (String)serviceRequest.getAttribute("note");
    		prgPattoStr = (String)serviceRequest.getAttribute("prgPatto");
    		if (serviceRequest.getAttribute("numKloPatto") != null && 
    		    !serviceRequest.getAttribute("numKloPatto").toString().equals("")) {
    			NUMKLOPATTO = new BigDecimal((String)serviceRequest.getAttribute("numKloPatto"));
    			NUMKLOPATTOStr = NUMKLOPATTO!=null?NUMKLOPATTO.toString():"";
    		}
    		codMotivoRiaperturaAtto = (String)serviceRequest.getAttribute("codMotivoRiapertura");
    		datDichiarazioneRiapri = (String)serviceRequest.getAttribute("datDichiarazione");
    	}
    	else {
    		if (confirmAnnulla) {
    			if (serviceRequest.getAttribute("numKloDid") != null && 
	    		    !serviceRequest.getAttribute("numKloDid").toString().equals("")) {
	    			NUMKLODICHDISP = new BigDecimal((String)serviceRequest.getAttribute("numKloDid"));
	    			NUMKLODICHDISPStr = NUMKLODICHDISP!=null?NUMKLODICHDISP.toString():"";
	    		}
	    		if (serviceRequest.getAttribute("prgDichDisponibilita") != null && 
	    		    !serviceRequest.getAttribute("prgDichDisponibilita").toString().equals("")) {
	    			prgDichDisponibilitaConfirm = new BigDecimal((String)serviceRequest.getAttribute("prgDichDisponibilita"));
	    			prgDichDisponibilitaStr = prgDichDisponibilitaConfirm!=null?prgDichDisponibilitaConfirm.toString():"";
	    		}
    			STRNOTE = (String)serviceRequest.getAttribute("note");
	    		prgPattoStr = (String)serviceRequest.getAttribute("prgPatto");
	    		datDichiarazioneAnnulla = (String)serviceRequest.getAttribute("datDichiarazione");
	    		if (serviceRequest.getAttribute("prgDocumento") != null && 
	    		    !serviceRequest.getAttribute("prgDocumento").toString().equals("")) {
	    			prgDocumento = new BigDecimal((String)serviceRequest.getAttribute("prgDocumento"));
	    			prgDocumentoStr = prgDocumento!=null?prgDocumento.toString():"";
	    		}
	    		CODSTATOATTOANNULLA = (String)serviceRequest.getAttribute("statoAtto");
	    		motivoAnnullamento = (String)serviceRequest.getAttribute("motivoAnnullamento");
	    	}
    	}
    }
                  
    boolean hasCartaIdentita = serviceResponse.containsAttribute("M_GETCARTAIDENTITAVALIDA.ROWS.ROW");    
    Vector codCartaIdent = serviceResponse.getAttributeAsVector("M_GETCARTAIDENTITAVALIDA.ROWS.ROW");
    if (codCartaIdent!=null && codCartaIdent.size()>0){
      SourceBean cI = (SourceBean)codCartaIdent.get(0);
      codCartaIdentita = (BigDecimal)cI.getAttribute("PRGDOCUMENTO");
      datInizioDocIdent = StringUtils.getAttributeStrNotNull(cI, "DatInizio");
      datScadDocIdent = StringUtils.getAttributeStrNotNull(cI, "DatFine");
    }
    if (!codCartaIdentita.equals(new BigDecimal(0)))
      codCartaIdentitaStr = codCartaIdentita.toString();
    String codContrattoMovimento = (String)serviceResponse.getAttribute("M_UltimoMov.ROWS.ROW.codContratto");
   ///////////////////////////////////////////////////////////////////////////////////////////////////   
   // la pagina viene caricata dal menu e non dal patto
   if (!fromPatto) { // 
       operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
   
       if (serviceRequest.getAttribute("Inserisci")!=null && serviceRequest.getAttribute("CDNLAVORATORE")!=null)
            cdnLavoratore = new BigDecimal((String)serviceRequest.getAttribute("CDNLAVORATORE"));
        
       testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
       testata.setPaginaLista("ListaDispoPage");
       
   }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    rows= serviceResponse.getAttributeAsVector("M_PROTOCOLLATO.ROWS.ROW");
    BigDecimal numProt  = null;
    
    String annoProt = "";
    String dataProt = "";
    String oraProt  = "";
    String docInOrOut = "";
    String docDiIO    = "";
    String docRif     = "";
    if(rows != null && !rows.isEmpty())
    { SourceBean row = (SourceBean) rows.elementAt(0);
      numProt  = SourceBeanUtils.getAttrBigDecimal(row, "numprotocollo", null);
      prgDocumento = SourceBeanUtils.getAttrBigDecimal(row,"prgdocumento", null);
      BigDecimal annoP = (BigDecimal) row.getAttribute("ANNOPROT");
      annoProt = annoP!=null ? annoP.toString() : "";
      dataProt = StringUtils.getAttributeStrNotNull(row,"datprot");
      oraProt  = StringUtils.getAttributeStrNotNull(row,"oraProt");
      docInOrOut  = StringUtils.getAttributeStrNotNull(row,"CODMONOIO");
      docRif  = StringUtils.getAttributeStrNotNull(row,"STRDESCRIZIONE");
    }
    
    if( docInOrOut.equalsIgnoreCase("I") ) 
    { docDiIO = "Input";
    } 
    else if ( docInOrOut.equalsIgnoreCase("O") )
    { docDiIO = "Output";
    }    


    boolean impedisciChiusura = rdOnly || flag_insert;     
    if (CODSTATOATTO!=null  && CODSTATOATTO.equals("PR")) {
        rdOnly = true;
        canInsert = false;
        canPrint = false;                
    }
    
    if (viewProfilePi3){
    	 if (CODSTATOATTO!=null  && CODSTATOATTO.equals("PR")) {
    		 canPi3 = true; // Protocollazione Pi3
    	 }
    }
    
    if (viewProfileConferimentiDID){
   		if (CODSTATOATTO!=null  && CODSTATOATTO.equals("PR")) {
   			canConferimentiDID = true; // Conferimenti DID
   	 	}
   	}
    
    //Se è andato male un inserimento oppure sto aggiornando la pagina dopo aver inserito un documento 
    //recupero i dati editabili dalla request
   	String aggDaInsertDoc = StringUtils.getAttributeStrNotNull(serviceRequest, "AGGIORNACHIAMANTE");
   	boolean aggiornamentoDaInserimento = aggDaInsertDoc.equalsIgnoreCase("true");
    if ((flag_insert && operazioneFallita) || aggiornamentoDaInserimento ) {
    	datDichiarazione = StringUtils.getAttributeStrNotNull(serviceRequest, "datDichiarazione");
    	CODTIPODICHDISP = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPODICHDISP");
    	DATSCADCONFERMA = StringUtils.getAttributeStrNotNull(serviceRequest, "DATSCADCONFERMA");
    	DATSCADEROGAZSERVIZI = StringUtils.getAttributeStrNotNull(serviceRequest, "DATSCADEROGAZSERVIZI");
    	STRNOTE = StringUtils.getAttributeStrNotNull(serviceRequest, "STRNOTE");
    	flgDidL68 = StringUtils.getAttributeStrNotNull(serviceRequest,"flgDidL68");
    }

  //Controllo di esistenza dello storico
  boolean storicoDid = serviceResponse.containsAttribute("M_HasStorDid.ROWS.ROW");  
  //Controllo di esistenza did inps
  boolean hasDidInps = serviceResponse.containsAttribute("M_HasDidInps.ROWS.ROW"); 
  String strCodFisc = StringUtils.getAttributeStrNotNull(serviceResponse, "M_GetLavoratoreAnag.ROWS.ROW.STRCODICEFISCALE");
  //Controllo di eseguibilità "Gestione Consenso"
  boolean isConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma"); 
//  boolean isButtonGestioneConsenso = false;
  boolean isConsensoAttivo = false;
  String ipOperatore = null;
  if(isConsenso){
/*	  ProfileDataFilter filterConsenso = new ProfileDataFilter(user, "HomeConsensoPage");
	  isButtonGestioneConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma.viewGestioneConsensoBtn") 
			  						&& filterConsenso.canView();*/
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
String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>

<%
// POPUP EVIDENZE
String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
int _fun = 1;
if((cdnFunzione!=null) && !cdnFunzione.equals("")) { _fun = Integer.parseInt(cdnFunzione); }
EvidenzePopUp jsEvid = null;
boolean bevid = attributi.containsButton("EVIDENZE");
if(strApriEv.equals("1") && bevid) {
	jsEvid = new EvidenzePopUp(cdnLavoratoreStr, _fun, user.getCdnGruppo(), user.getCdnProfilo());
}


String codStatoAtto = Utils.notNull(CODSTATOATTO);
if (codStatoAtto.equals("")){
	codStatoAtto = "PA";
}
boolean didProtocollata = false;
if(codStatoAtto.equalsIgnoreCase("PR")){
	didProtocollata = true;
}

Vector cat181Rows = serviceResponse.getAttributeAsVector("M_GET181CAT.ROWS.ROW");
Vector laureaRows = serviceResponse.getAttributeAsVector("M_GetLaureaPerCat181.ROWS.ROW");
String flgObbSco = "";
String annoNascita = "";

if(cat181Rows != null && !cat181Rows.isEmpty()) { 
	SourceBean row = (SourceBean) cat181Rows.elementAt(0);
	flgObbSco = (String) row.getAttribute("FLGOBBLIGOSCOLASTICO");
	annoNascita = (String)row.getAttribute("datNasc");
}

String flgLaurea = laureaRows != null && !laureaRows.isEmpty() ? "S":"N";
String cat181 = "";

String checkIscrLavL68Chiuse = "false";
SourceBean iscrCM = (SourceBean)serviceResponse.getAttribute("CMIscrLavL68Chiuse.ROW");
if(iscrCM != null){
	checkIscrLavL68Chiuse = (String)iscrCM.getAttribute("checkIscrLavL68Chiuse"); 
}

boolean didValida = didProtocollata && (DATFINE == null);
  
boolean reopenIscrL68 = canRiapriIscrL68 && didValida && checkIscrLavL68Chiuse.equals("true");

	//configurazione chiusura did multipla
	boolean canChiusuraDidMultipla = false;
	String numConfigurazioneChiusuraDidMultipla = SourceBeanUtils.getAttrStrNotNull(serviceResponse, "M_GetNumConfigurazioneChiusuraDidMultipla.ROWS.ROW.NUM");
	if ("1".equals(numConfigurazioneChiusuraDidMultipla)) {
		canChiusuraDidMultipla = true;
	}
	
%> 

<html>
<head>
<title>Dettaglio delle disponibilità</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<af:linkScript path="../../js/"/>

<script language="Javascript" src="../../js/docAssocia.js"></script>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<SCRIPT TYPE="text/javascript">
<!--
function ripetiOperazione(contesto) {
	if (contesto == "chiusura") {
		document.Frm1.Salva.disabled = false;
		doFormSubmit(document.Frm1);
	}
	else {
		if (contesto == "riapertura") {
			doFormSubmit(document.FrmRiapertura);
		}
		else {
			if (contesto == "annulla") {
				doFormSubmit(document.FrmRiannulla);
			}	
		}
	}
}

function ripetiRiapertura() {
	doFormSubmit(document.FrmRiapertura);
}

function mostraMessaggi() {     
    msg = "<%=Utils.notNull(alertMsg)%>";
    if (msg!="")
        alert(msg);
}

function apriPopUpStoriche( cdnlav ) {
    popupurl="AdapterHTTP?PAGE=DispoLavInformazioniStorichePage&cdnLavoratore="+cdnlav;
    <%if (!flag_competenza) {%> popupurl+="&flag_competenza=false" <%}%> 
    window.open (popupurl, "InformazioniStoriche", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
}
function goDidInps(codfis) {
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
    //DIDINPS senza spazi per colpa di explorer
    var urlpage="AdapterHTTP?PAGE=DidInpsListaByLavPage&strcodicefiscale="+codfis;
    window.open (urlpage, 'DIDINPS', 'toolbar=NO,statusbar=YES,height=700,width=800,scrollbars=YES,resizable=YES');
  }

  function sceglipage(Scelta, cdnLavoratore, prgDichDisp){
     // Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;
  
	 if (Scelta == 1) {
        document.Frm1.Salva.value = "Salva";
        document.Frm1.PAGE.value="DispoDettaglioPage";   
        doFormSubmit(document.Frm1);    
     }
  	 else if (Scelta == 2) {
        var urlpage="AdapterHTTP?ACTION_NAME=RPT_DISPONIBILITA&";
        urlpage+="cdnLavoratore="+cdnLavoratore+"&"; 
        urlpage+="prgDichDisp="+prgDichDisp+"&";
        urlpage+="asAttachment=true";
        setWindowLocation(urlpage);
     }
  	 else if (Scelta == 3) {
        document.Frm1.Inserisci.value = "Inserisci";
        document.Frm1.PAGE.value="DispoDettaglioPage";   
        doFormSubmit(document.Frm1);
     }
     else if (Scelta==4) {
        document.Frm1.PAGE.value="DispoDettaglioPage";   
        doFormSubmit(document.Frm1);
     }
  }

function tornaAlPatto(){
    var urlpage="AdapterHTTP?";
    urlpage+="PAGE=PattoLavDettaglioPage&";
    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
    urlpage+="cdnLavoratore=<%=cdnLavoratoreStr%>&";
//    setOpenWindowLocation(urlpage, 'main');
	window.open(urlpage, 'main');
    close();
}

//Effettua il refresh del patto se si proviene da esso
function refreshPatto(){
  <%if(fromPatto && !flag_insert && "PR".equals(CODSTATOATTO)){%>
    var urlpage="AdapterHTTP?";
    urlpage+="PAGE=PattoLavDettaglioPage&";
    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
    urlpage+="cdnLavoratore=<%=cdnLavoratoreStr%>&";
//    setOpenerWindowLocation(urlpage);
    window.open(urlpage, "main");
  <%}%>
}

function underConstr() { alert("Funzionaliltà non ancora attivata.");}

//funzione che controlla se un campo è stato modificato o meno
        var flagChanged = false;
        
        function fieldChanged() {
         <% if (!rdOnly){ %> 
            flagChanged = true;
         <%}%> 
        }

  
  function apriStampa(RPT,paramPerReport,tipoDoc)
  { //RPT: attributo name del tag ACTION nel file action.xml relativo al report da visualizzare
    //paramPerReport: parametri necessari a visualizzare il report 
    //tipoDoc: codice relativo al tipo di documento cosi come inserito nel campo CODTIPODOCUMENTO della tab. DE_DOC_TIPO

    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
    
    var urlpage="AdapterHTTP?ACTION_NAME="+RPT;
    urlpage+=paramPerReport; //Quelli che nella classe sono inseriti nel vettore params
    urlpage+="&tipoDoc="+tipoDoc;

    if(confirm("Vuoi PROTOCOLARE il file prima di visualizzarlo?\n\nSeleziona\n- \"OK\"        per PROTOCOLLARE prima di visualizzare la stampa\n- \"Annulla\"  per visualizzare SENZA PROTOCOLLARE"))
    { urlpage+="&salvaDB=true";
    }
    else
    { urlpage+="&salvaDB=false";
    }

    setWindowLocation(urlpage);
    
  }//apriStampa(_,_,_)
  
  function inserisciNuovo () {
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
  
    var urlpage="AdapterHTTP?PAGE=DispoDettaglioPage&";
    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
    urlpage+="cdnLavoratore=<%=cdnLavoratoreStr%>&";
    urlpage+="Inserisci=0";
    setWindowLocation(urlpage);
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
	
  function riapriIscrL68() {
    
	var urlpage="AdapterHTTP?PAGE=CMIscrLavL68ChiusePage&FROM_PAGE=DispoDettaglioPage";
    urlpage+="&CDNFUNZIONE=<%=cdnFunzione%>";
    urlpage+="&cdnLavoratore=<%=cdnLavoratoreStr%>";
    urlpage+="&dataDichDid=<%=datDichiarazione%>";
    window.open (urlpage, "UltimeIscrizioniAlCollocamentoMirato", 'toolbar=NO,statusbar=YES,height=400,width=700,top=200,left=250,scrollbars=YES,resizable=NO');
 
  }

  function apriPopUpConfermaAnn(prgDid,cdnLav) {
	if (isInSubmit()) return;
    var urlpage="AdapterHTTP?PAGE=ConfermaAnnualeDidPage&";
    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
    urlpage+="cdnLavoratore="+cdnLav+"&";
    urlpage+="prgDichDisp="+prgDid;
    urlpage+="&dataDichDid=<%=datDichiarazione%>";
    <%if(isConsensoAttivo){%>
    	urlpage+="&firmaGrafometrica=OK&ipOperatore=<%=ipOperatore%>";
    <%}%>
    window.open(urlpage, "Conferma", 'toolbar=no,location=no,directories=no,status=NO,menubar=no,height=500,width=800,top=200,left=250,scrollbars=YES,resizable=NO');
  }

  function apriGestioneConsenso( ) {
	  var urlpage="AdapterHTTP?";
	    urlpage+="PAGE=HomeConsensoPage&";
	    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
	    urlpage+="cdnLavoratore=<%=cdnLavoratoreStr%>&";
//	    setOpenerWindowLocation(urlpage);
	    window.open(urlpage, "main");
	}
  
  function callWS() {
    if(!confirm("Sicuro di voler invocare la richiesta SAP?")) {  
    	return false;
	}
    
    var urlpage="AdapterHTTP?";
    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&PAGE=SapVisualizzaPage&MODULE=M_SapCallRichiestaSap&CODMINSAP=<%=codMinSap%>&CDNLAVORATORE=<%=cdnLavoratoreStr%>";
   
    window.open(urlpage,'RichiestaSAP','toolbar=NO,statusbar=YES,width=700,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
  }  
  
  function callPi3() {
	  	//alert('Pi3');
	    var urlpage="AdapterHTTP?";
	    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&PAGE=Pi3HomePage&MODULE=M_SapCallRichiestaSap&CODMINSAP=<%=codMinSap%>&CDNLAVORATORE=<%=cdnLavoratoreStr%>&NUMPROT=<%=numProt%>&prgDocumento=<%=prgDocumento%>&pagina=DispoDettaglioPage&strChiaveTabella=<%=Utils.notNull(prgDichDisponibilita)%>";
	   
	    window.open(urlpage,'Invio Protocollazione Pi3','toolbar=NO,statusbar=YES,width=800,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
	  }
  
  function callConferimentiDID() {
	  	//alert('Pi3');
	    var urlpage="AdapterHTTP?";
	    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&PAGE=ElencoConferimentiDIDPage&CDNLAVORATORE=<%=cdnLavoratoreStr%>&pagina=DispoDettaglioPage&dataDichDid=<%=datDichiarazione%>";
	    
	    window.open(urlpage,'Elenco Conferimenti DID','toolbar=NO,statusbar=YES,width=800,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
	  }
  
//-->
</SCRIPT>
<SCRIPT>
<!--
var sezioni = new Array();

function cambia(immagine, sezione) {
	if (sezione.aperta==null) sezione.aperta=true;
	if (sezione.aperta) {
		sezione.style.display="none";
		sezione.aperta=false;
		immagine.src="../../img/chiuso.gif";
	}
	else {
		sezione.style.display="inline";
		sezione.aperta=true;
		immagine.src="../../img/aperto.gif";
	}
}
function Sezione(sezione, img,aperta){    
    this.sezione=sezione;
    this.sezione.aperta=aperta;
    this.img=img;
}

function initSezioni(sezione){
	sezioni.push(sezione);
}
//-->
</SCRIPT>
<script>
<%@ include file="_controlloDate_script.inc"%>
</script>
<script>
<!--
var codAccertamentoSanitario = "<%= accertamentoSanitario %>";
var dataInizioCM = "<%= dataInizioCM %>";
var apriStampa = <%=serviceRequest.containsAttribute("insert_dispo") && !operazioneFallita %>;
//var ultimaDataFine = "Utils.notNull(ultimaDataFine)"; 
var codStatoAttoCaricato = "<%=Utils.notNull(CODSTATOATTO)%>"; 

var flagInsert = <%=flag_insert%>;
var controllaInModifica = flagInsert;
var flagCompetenzaLav = <%=flag_competenza%>;
//se numConfigDidSAP = 0 allora l'invio in automatico della SAP a seguito della Stipula della DID e' attivo, 
//se 1 allora l'invio automatico dopo la stipula della DID deve essere disabilitato.
var configInvioSapDid = <%= numConfigDidSAP.equals(it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG)%>;


function controlla() {
    dataUscita = document.Frm1.DATFINE.value;
    dataInserimento = document.Frm1.datDichiarazione.value;    
    codAccSan = document.Frm1.CODTIPODICHDISP.value;
    
    if (controllaInModifica && isFuture(dataInserimento)) {
        alert(Messages.Date.ERR_DATA_STIP);
        return false;
    }
    if (controllaInModifica && isOld(dataInserimento) && !confirm(Messages.Date.WARNING_STIPULA)) {        
        return false;
    }
    if (isFuture(dataUscita)) {
        alert(Messages.Date.ERR_USCITA);
        return false;
    }
    if (dataUscita!="" &&  compDate(dataInserimento, dataUscita)>0) {
        alert(Messages.Date.ERR_CANC_INS);
        return false;
    }
    if (dataUscita!="" && document.Frm1.CODMOTIVOFINEATTO.selectedIndex==0){       
        alert(Messages.Date.ERR_MOTIVO_USCITA);
        return false;
    }
    if  (dataUscita=="" && document.Frm1.CODMOTIVOFINEATTO.selectedIndex>0) {
        alert(Messages.Date.ERR_DATA_USCITA);
        return false;
    }
	if (<%=eta%> > 60){
        if (!confirm("Il lavoratore ha più di 60 anni.\nControllare la situazione pensionistica.\nSi desidera continuare?")){
        	return false;
        }
	}
    /*if (ultimaDataFine!="" && dataUscita!="") {
        if (compDate(dataUscita, ultimaDataFine)<0) alert("data fine minore della data fine ultimo record storico");
    }*/
    <%
        if (!flag_insert) {
    %>
        if (dataUscita=="") {
            alert("Non e' possibile modificare i dati ma solo chiudere l'atto");
            return false;
        } 
        var motiviFineAttoInM = new Array();
        <%
        	Vector motiviFineAtto = serviceResponse.getAttributeAsVector("M_MOTFINEATTO.ROWS.ROW"); 
        	int j=0;
        	for (int i=0;i<motiviFineAtto.size();i++) {
        		SourceBean row = (SourceBean)motiviFineAtto.get(i);
        		String flgImpattiAmm = (String)row.getAttribute("flgImpattiAmm");
        		if (flgImpattiAmm!=null && flgImpattiAmm.equals("M")) {
        			out.println("motiviFineAttoInM[" + j++ + "]='" + row.getAttribute("codice") + "';");
        		}        		
        	}
        %>
        for (i=0;i<motiviFineAttoInM.length;i++) {
        	if (motiviFineAttoInM[i]==document.Frm1.CODMOTIVOFINEATTO.value) 
        		if (!confirm('Vuoi gestire manualmente questo motivo fine atto?')) return false;
        }
    <%}%>
    return true;
}
function disabilitaBottone() {
	if (document.Frm1.insert_dispo_button !=null ) {
		document.Frm1.insert_dispo_button.disabled=true;	
		document.Frm1.insert_dispo.disabled=false;	
	}
	else if (document.Frm1.Salva_button !=null ) {
		document.Frm1.Salva.disabled=false;
		document.Frm1.Salva_button.disabled=true;
	}
	return true;	
}
function controllaDocumentoIdentita(presente) {
	if (configInvioSapDid && !flagCompetenzaLav) {
		if (!confirm("A seguito della protocollazione sarà modificata la competenza amministrativa del lavoratore e si procederà all'invio della SAP")) {
			return false;
		}	
	}
	else if(configInvioSapDid){
		if (!confirm("A seguito della protocollazione si procederà all'invio della SAP")) {
			return false;
		}
	}else if(!configInvioSapDid && !flagCompetenzaLav){
		if (!confirm("A seguito della protocollazione sarà modificata la competenza amministrativa del lavoratore")) {
			return false;
		}
	}
    if (presente) 
    <%if (fromPatto && !isConsensoAttivo) {%>
	    apriGestioneDoc('RPT_DISPONIBILITA','&cdnLavoratore=<%=Utils.notNull(cdnLavoratore)%>&FRAME_NAME=<%=target%>&prgDichDisp=<%=Utils.notNull(prgDichDisponibilita)%>&datDichiarazione=<%=datDichiarazione%>&pagina=DispoDettaglioPage&numKloDichDisp=<%=NUMKLODICHDISP%>&strChiaveTabella=<%=Utils.notNull(prgDichDisponibilita)%>&codstatoatto=PR',
    		'IM', 'REPORTFRAMEMAINRELOADPAGE', false);
    <%}else if(!isConsensoAttivo){%>
    	apriGestioneDoc('RPT_DISPONIBILITA','&cdnLavoratore=<%=Utils.notNull(cdnLavoratore)%>&FRAME_NAME=<%=target%>&prgDichDisp=<%=Utils.notNull(prgDichDisponibilita)%>&datDichiarazione=<%=datDichiarazione%>&pagina=DispoDettaglioPage&numKloDichDisp=<%=NUMKLODICHDISP%>&strChiaveTabella=<%=Utils.notNull(prgDichDisponibilita)%>&codstatoatto=PR','IM');
    <%}else if (fromPatto && isConsensoAttivo) {%>
	    apriGestioneDoc('RPT_DISPONIBILITA','&cdnLavoratore=<%=Utils.notNull(cdnLavoratore)%>&FRAME_NAME=<%=target%>&prgDichDisp=<%=Utils.notNull(prgDichDisponibilita)%>&datDichiarazione=<%=datDichiarazione%>&pagina=DispoDettaglioPage&numKloDichDisp=<%=NUMKLODICHDISP%>&strChiaveTabella=<%=Utils.notNull(prgDichDisponibilita)%>&codstatoatto=PR&firmaGrafometrica=OK&ipOperatore=<%=ipOperatore%>',
    		'IM', 'REPORTFRAMEMAINRELOADPAGE', false);
    <%}else if(isConsensoAttivo){%>
    	apriGestioneDoc('RPT_DISPONIBILITA','&cdnLavoratore=<%=Utils.notNull(cdnLavoratore)%>&FRAME_NAME=<%=target%>&prgDichDisp=<%=Utils.notNull(prgDichDisponibilita)%>&datDichiarazione=<%=datDichiarazione%>&pagina=DispoDettaglioPage&numKloDichDisp=<%=NUMKLODICHDISP%>&strChiaveTabella=<%=Utils.notNull(prgDichDisponibilita)%>&codstatoatto=PR&firmaGrafometrica=OK&ipOperatore=<%=ipOperatore%>','IM');
    <%}%>
    else alert("Stampa non possibile: il documento di riconoscimento del lavoratore non e' presente");    
}
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
function apriDialogStampa(){
    if (apriStampa) {
        controllaDocumentoIdentita(<%=hasCartaIdentita%>);
    }
}

function apriDettaglioStatoOccupazDich(){
  var urlPage = "AdapterHTTP?PAGE=InfoStatoOccupazPage&PRGDICHDISPONIBILITA=<%=prgDichDisponibilita%>";
  var t = "_blank";
  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=400,top=30,left=180";
  window.open(urlPage, t, feat);
}

function apriDocIdentificazione(){
	docDettaglioIdentif('<%=cdnLavoratore%>', '<%=codCartaIdentita%>',
						'DispoDettaglioPage','<%=_cdnFunz%>', '<%=target%>','<%=Utils.notNull(prgDichDisponibilita)%>', <%=fromPatto%>);
}

function apriPrivacy(cdnLav,cdnFunzione){
    //Per evidenziare se effettuare il refresh della pagina dopo aver chiuso la pagina della privacy    
    <% if (flag_insert) { String fromPattoParam = fromPatto?"&fromPattoDettaglio=1":"";%>
      window.open("AdapterHTTP?PAGE=PrivacyDettaglioPage&CDNLAVORATORE=" + cdnLav + "&CDNFUNZIONE=" + cdnFunzione + "&FROM_DID=S" + "&FRAME_NAME=<%=target%><%=fromPattoParam%>",'','toolbar=0,scrollbars=1,width=700, height=500, left=10, top=10');
    <%}else{%>
      window.open("AdapterHTTP?PAGE=PrivacyDettaglioPage&CDNLAVORATORE=" + cdnLav + "&CDNFUNZIONE=" + cdnFunzione,'','toolbar=0,scrollbars=1,width=700, height=500, left=10, top=10');
    <%}%>      
} 

function controllaPresenzaPrivacy(){
  var isPresentePrivacyValida = false;
  var datPrivacyjs = '<%=dataPrivacy%>';
  var flgPresaVisione = '<%=flgautoriz%>';
  var dtPrivacy = 0;
  var dtDID = 0;
  var dtOdierna = "<%= DateUtils.getNow() %>";
  var dtOdiernaFormat = dtOdierna.substr(6,4) + dtOdierna.substr(3,2) + dtOdierna.substr(0,2);
  <% if (flag_insert) { %>
    if(datPrivacyjs != ""){
      dtPrivacy = datPrivacyjs.substr(6,4) + datPrivacyjs.substr(3,2) + datPrivacyjs.substr(0,2);
    }

    if(document.Frm1.datDichiarazione.value != ""){
      var tmp = document.Frm1.datDichiarazione.value;
      dtDID = tmp.substr(6,4) + tmp.substr(3,2) + tmp.substr(0,2);
    }
    
    if(dtPrivacy != 0){
      if((dtPrivacy <= dtOdiernaFormat) && flgPresaVisione == 'S') isPresentePrivacyValida = true;
    }
  <%} else {%>
          isPresentePrivacyValida = true;
  <%  } %>
  if(!isPresentePrivacyValida){
    alert("Non è stata presa visione della informativa\nrelativa al trattamento dei dati personali\nprima dell'inserimento della DID.");
  }
  return isPresentePrivacyValida;
}

//
function controllaPresenzaDocValido(){
  var isPresenteDocValido = false;
  var datInizoDocjs = '<%=datInizioDocIdent%>';
  var datScadDocjs = '<%=datScadDocIdent%>';
  var dtInizioDoc = 0;
  var dtScadDoc = 0;
  var dtDID = 0;
  <% if (flag_insert) { %>
    if(datInizoDocjs != ""){
      dtInizioDoc = datInizoDocjs.substr(6,4) + datInizoDocjs.substr(3,2) + datInizoDocjs.substr(0,2)
    }
    if(datScadDocjs != ""){
      dtScadDoc = datScadDocjs.substr(6,4) + datScadDocjs.substr(3,2) + datScadDocjs.substr(0,2)
    }

    if(document.Frm1.datDichiarazione.value != ""){
      var tmp = document.Frm1.datDichiarazione.value;
      dtDID = tmp.substr(6,4) + tmp.substr(3,2) + tmp.substr(0,2);
    }

    if((dtInizioDoc != 0) && (dtDID != 0)){
      if(dtInizioDoc <= dtDID){ 
      	isPresenteDocValido = true;
      	if(dtScadDoc != 0){
      		 if(dtScadDoc >= dtDID)
	      		 isPresenteDocValido = true;
	      	 else 
	      	 	isPresenteDocValido = false;
	    }
	  }
	     
    }
  <%} else {%>
          isPresenteDocValido = true;
  <%  } %>
  if(!isPresenteDocValido){
    alert("Non è presente un documento di identificazione valido.");
  }
  return isPresenteDocValido;
}

function ctrlPresenzaObblScol(){
  var isPresenteObblScol = false;
  var flgObbligojs = '<%=flgObbligo%>';

   <% if (flag_insert) { 
        if((eta > 15) && (eta < 30)){%>  
          if(flgObbligojs != 'S'){
            if(confirm("Attenzione, prima della DID inserire le informazioni diritto/dovere\nistruzione/formazione o possesso di laurea.\nSi desidera proseguire con l'operazione?")){
              isPresenteObblScol = true;
            }
          } else isPresenteObblScol = true;
   <%   }//if((eta >= 15) && (eta <= 30))
          else {%>
            isPresenteObblScol = true;
        <%}
      }//if (flag_insert)
        else {%>
          isPresenteObblScol = true;
  <%    } %>
  return isPresenteObblScol;
}



function onLoadFun()
{
	<%if(strApriEv.equals("1") && bevid) { %>
	   apriEvidenze();
	<%}%>
	rinfresca();
	apriDialogStampa();
	refreshPatto();
	mostraMessaggi();
	<% if(serviceResponse.containsAttribute("M_RIAPRIDID.NON_RIAPERTA")){ %>
		alert("Non è possibile riaprire la DID");
	<% } %>
}

/**
 * Questa funzione riapre la Dich.di Immediata Dispo.
 * il cui progressivo è preso in input come primo 
 * parametro
 */
function riapriDid(prgDichDisp,numKloDid,datDichiarazione,note,prgPatto,numKloPatto,codMotivoRiapertura){
	
	if (isInSubmit()) return;
    var urlpage="AdapterHTTP?PAGE=DispoDettaglioPage&";
    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
    urlpage+="cdnLavoratore=<%=cdnLavoratoreStr%>&";
    urlpage+="riapriDid=true&";
    urlpage+="numKloDid="+numKloDid+"&";
    urlpage+="prgDichDisp="+prgDichDisp+"&";
    urlpage+="prgDichDisponibilita="+prgDichDisp+"&";
    urlpage+="note="+note+"&";
    urlpage+="prgPatto="+prgPatto+"&";
    urlpage+="numKloPatto="+numKloPatto+"&";
    urlpage+="codMotivoRiapertura="+codMotivoRiapertura+"&";
    urlpage+="datDichiarazione="+datDichiarazione;
    urlpage+="&FORZA_INSERIMENTO="+document.Frm1.FORZA_INSERIMENTO.value;
    urlpage+="&CONTINUA_CALCOLO_SOCC="+document.Frm1.CONTINUA_CALCOLO_SOCC.value;
	
    setWindowLocation(urlpage);
}

/**
*Questa function invoca la page che annulla la did
*/
function annullaDid(){

	if(document.Frm1.CODSTATOATTO.value=="PR"){
		alert("Stato atto non modificato");
		return;
	}
	if(document.Frm1.CODSTATOATTO.value=="PA"){
		alert("Stato atto non gestibile");
		return;
	}
	
	if(document.Frm1.MotivoAnnullamentoAtto == null || document.Frm1.MotivoAnnullamentoAtto.value==""){
		alert("Motivo di annullamento DID obbligatorio");
		return;
	}
	
	if(confirm("La funzionalità annulla la DID e ricalcola in corrispondenza gli impatti.\n Vuoi proseguire?")){
		if (isInSubmit()) return;
  
		    var urlpage="AdapterHTTP?PAGE=DispoDettaglioPage&";
		    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
		    urlpage+="cdnLavoratore=<%=cdnLavoratoreStr%>&";
		    urlpage+="annullaDid=true&";
		    urlpage+="numKloDid=<%=NUMKLODICHDISP%>&";
		    urlpage+="prgDichDisp=<%=prgDichDisponibilita%>&";
		    urlpage+="prgDichDisponibilita=<%=prgDichDisponibilita%>&";
		    urlpage+="statoAtto="+document.Frm1.CODSTATOATTO.value+"&";
		    urlpage+="motivoAnnullamento="+document.Frm1.MotivoAnnullamentoAtto.value+"&";
		    urlpage+="prgPatto=<%=prgPattoStr%>&";
		    urlpage+="prgDocumento=<%=prgDocumento%>&";
		    urlpage+="datDichiarazione=<%=datDichiarazione%>";
			urlpage+="&FORZA_INSERIMENTO="+document.Frm1.FORZA_INSERIMENTO.value;
    		urlpage+="&CONTINUA_CALCOLO_SOCC="+document.Frm1.CONTINUA_CALCOLO_SOCC.value;
    	
		    setWindowLocation(urlpage);
	}
}

function annullaDidChiusa(prgDichDisp,numKloDid,statoAtto,motivoAnnullamento,prgPatto,dataDichiarazione,prgDocumento){

	if ( isInSubmit() ) return;
    
		    var urlpage="AdapterHTTP?PAGE=DispoDettaglioPage&";
		    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
		    urlpage+="cdnLavoratore=<%=cdnLavoratoreStr%>&";
		    urlpage+="annullaDid=true&";
		    urlpage+="numKloDid="+numKloDid+"&";
		    urlpage+="prgDichDisp="+prgDichDisp+"&";
		    urlpage+="prgDichDisponibilita="+prgDichDisp+"&";
		    urlpage+="statoAtto="+statoAtto+"&";
		    urlpage+="motivoAnnullamento="+motivoAnnullamento+"&";
		    urlpage+="prgPatto="+prgPatto+"&";
		    urlpage+="prgDocumento="+prgDocumento+"&";
		    urlpage+="datDichiarazione="+dataDichiarazione;
		    
		    setWindowLocation(urlpage);
	}
	
	
	function ricevuta() {
    	apriGestioneDoc('RPT_DISPO_RICEVUTA','&cdnLavoratore=<%=Utils.notNull(cdnLavoratore)%>&prgDichDisp=<%=Utils.notNull(prgDichDisponibilita)%>&codCPI=<%=Utils.notNull(codCPI)%>','RIM');
   	}


	function requireDatLicenziamento() {
		var visDatLicenziamento = document.getElementById("visDatLicenziamento");
		if (document.Frm1.flgRischioDisoccupazione.value == 'S') {
	      	visDatLicenziamento.style.display = '';
	    }
	    else {
	      document.Frm1.datLicenziamento.value = '';
	      visDatLicenziamento.style.display = 'none';
	    }     
	}

	function controllaDataLicenziamento() {
		if (document.Frm1.flgRischioDisoccupazione.value == 'S') {
			return isRequired('datLicenziamento');
	    }
	    return true;
	}

//-->
</script>


<%if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>

<script language="Javascript">
<%
    attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
%>
</script>
<style>
table.sezione2 {
	border-collapse:collapse;
	font-family: Verdana,"Times New Roman", Arial, Sans-serif; 
	font-size: 12px;
	font-weight: bold;
	border-bottom-width: 2px; 
	border-bottom-style: solid;
	border-bottom-color: #000080;
	color:#000080; 
	position: relative;
	width: 94%;
	left: 4%;
	text-align: left;
	text-decoration: none;	
}
</STYLE>
</head>
<body  class="gestione" onLoad="onLoadFun();requireDatLicenziamento();">
<%
    if (testata!=null)testata.show(out);
%>
<% if (!fromPatto) {%>
<script language="Javascript">
   if ((window.top.menu != undefined) && (window.top != undefined)){
     window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
   }
</script>
<%}%>
<font color="red"><af:showErrors/></font>
<%if (accertamentoSanitario.equals("N") || accertamentoSanitario.equals("M")) { %>
<div><font color="red">
IL SOGGETTO E' ISCRITTO NELLE LISTE DEL COLLOCAMENTO MIRATO: IL TIPO DI ISCRIZIONE NON 
PERMETTE LA STIPULA DELLA DICHIARAZIONE DI DISPONIBILITA'
</font></div>
<%}%>
<font color="green">
 <af:showMessages prefix="M_SaveDispo"/>
 <af:showMessages prefix="M_InsertDispo"/>
 <% Vector did = (Vector)serviceResponse.getAttributeAsVector("M_GETDISPO.ROWS.ROW");
 	if (  did != null && !did.isEmpty() ) { %>
 <af:showMessages prefix="M_RiapriDid"/>
 <% } %>
 
</font>


<% if(flag_insert){%>
<!--
<font color="red">
<div>
    <UL>
      <li>PER REGISTRARE UNA DID DEVE ESSERE INSERITO UN DOCUMENTO DI RICONOSCIMENTO</li>
    </UL>
<div>
</font>
-->
<%}%>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="ctrlPresenzaObblScol() && controllaPresenzaPrivacy() && controllaPresenzaDocValido() && controllaDataLicenziamento() && controlla() && disabilitaBottone()">
<p align="center">
<table class="main">

<tr><td class="titolo" colspan="5"><p class="titolo">Dichiarazione immediata disponibilità</p></td></tr>

<tr><td><br/></td></tr>
</table>

	<%if(isConsenso){%>
 	   	  <af:showMessages prefix="M_VerificaAmConsensoFirma"/>
	   	 	
     <%}%>
<%out.print(htmlStreamTop);%>
<table class="main" border="0">
<tr>
  <td colspan="6" class="azzurro_bianco">
    <!--<%out.print(htmlStreamTop);%>-->
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    
    <tr>
    	<td class="etichetta2">Stato atto</td>
        <td>
        	<table cellpadding="0" cellspacing="0" border="0" width="100%">
        		<tr>
		        <%
		            String codAtto = Utils.notNull(CODSTATOATTO);
		            if (codAtto.equals("")) codAtto = "PA";
		        	boolean annullaDid = (canAnnullaDid && didProtocollata && !impedisciChiusura);
		        %>
		        
        					
        			<td>
        				<af:comboBox  classNameBase="input" disabled ="<%= String.valueOf(!annullaDid)%>"  name="CODSTATOATTO"  
                          moduleName="M_getStatoAttoDid" selectedValue="<%=codStatoAtto%>" required="true"/>
        			</td>
        			<td align="right">anno&nbsp;</td>
        			<td>
        				<af:textBox classNameBase="input" type="text" name="annoProt" value="<%=annoProt!=null ? annoProt : \"\"%>"
                                     readonly="true" title="Anno protocollo" size="5" maxlength="4" onKeyUp="fieldChanged();"/>
					</td>
        			<td align="right"> &nbsp;&nbsp;num.&nbsp;</td>
        			<td><af:textBox classNameBase="input" type="text" name="numProt" value="<%=Utils.notNull(numProt)%>"
                                     readonly="true" title="Numero protocollo" size="8" maxlength="100" onKeyUp="fieldChanged();"/>
                    </td>
        			<td class="etichetta2">data
        		        <af:textBox name="dataProt" type="date" value="<%=dataProt%>" size="11" maxlength="10"
                       				title="data di protocollazione" classNameBase="input" readonly="true" validateOnPost="true" 
                       				required="false" trim ="false" onKeyUp="cambiAnnoProt(this,annoProt)" onBlur="checkFormatDate(this)"/>
            		</td>
            		<% if (ProtocolloDocumentoUtil.protocollazioneLocale()) { %>
        			<td class="etichetta2">ora
           				<af:textBox name="oraProt" type="text" value="<%=oraProt%>" size="6" maxlength="5" title="data di protocollazione"  
                       				classNameBase="input" readonly="true" validateOnPost="false" required="false" trim ="false"/>
                    </td>
                    <% } else { %>
                    <td><input type="hidden" name="oraProt" value="00:00">
                    <% } %>
   				 </tr>
    		</table>
    	</td>
    </tr>
    
    <tr>
		<td class="etichetta2"><% if(annullaDid){ %>Motivo Annull. Atto
    	<% 	} else { %>Doc. di
    	<% 	}  %>
    	</td>    	
    	<td>
    	
    		<table cellpadding="0" cellspacing="0" border="0" width="100%">
        		<tr>

    				<% if(annullaDid){ %>
        			<td>
     					<af:comboBox classNameBase="input" name="MotivoAnnullamentoAtto" moduleName="M_getMotivoAnnullamentoDid" addBlank="true"/>*
        			</td>	
    				<td class="etichetta2"> Doc. di </td>
			       	<% } %>
    				<td class="campo2">
		               <af:textBox 	name="docDiIO" type="text" value="<%= docDiIO%>" size="6"  maxlength="6"
                       			   	title="data di protocollazione" classNameBase="input" readonly="true" validateOnPost="false" 
                       				required="false" trim ="false" />
		               <af:textBox name="docInOrOut" type="hidden" value="<%= docInOrOut%>" size="6" maxlength="6" title="data di protocollazione"  
		                           classNameBase="input" readonly="true" validateOnPost="false" required="false" trim ="false"/>
		            </td>
            		<td class="etichetta2">Rif.</td>
            		<td class="campo2">
               			<af:textBox name="docInOrOut" type="text" value="<%=docRif%>" size="<%=docRif.length()%>"
                           			title="data di protocollazione" classNameBase="input" readonly="true"
                           			validateOnPost="false" required="false" trim ="false"/>
                     </td>
		             <% if(annullaDid){ %>
		             <td>
		             	<input type="button" class="pulsanti" name="AnnullaDId" value="Salva" onclick="annullaDid()">
		             </td>
		             <% } %>
        		</tr>
       		</table>
    	</td>
    </tr>
    
</table>
        <!--<%out.print(htmlStreamBottom);%>-->
      <!--</div>-->
      </td>
    </tr>
    <tr><td><br/></td></tr>
    </table>

<!--<%out.print(htmlStreamTop);%>-->
<table class="main">
<tr ><td colspan="5"><div class="sezione">Informazioni amministrative collegate</div></td></tr>

<tr>
   <td class="etichetta"> Data inserimento nell'elenco anagrafico &nbsp;</td>
     <td class="campo"><af:textBox classNameBase="input" type="date" name="datInizio" value="<%=Utils.notNull(datInizio)%>"
                                   validateOnPost="true" readonly="true" onKeyUp="fieldChanged();"
                                   required="false" title="Data inserimento nell'elenco anagrafico"
                                   size="11" maxlength="10"/></td>
</tr>

<tr>
    <%-- Savino 22/02/2006 sostituita etichetta 
    <td class="etichetta"> Cpi titolare dei dati&nbsp;</td>
    --%>
    <td class="etichetta"> Cpi Competente&nbsp;</td>
    <td class="campo"><%String strCpI = null;
          if(descCPI != null) {
            strCpI = descCPI;
            strCpI += " - " + codCPI;
          }%>
          <af:textBox classNameBase="input" type="text" name="codCPI" value="<%=Utils.notNull(strCpI)%>" validateOnPost="true" 
                      readonly="true" onKeyUp="fieldChanged();" required="false" title="Cpi titolare dei dati"
                      size="45" maxlength="25"/>
    </td>    
</tr>

<tr>
 <td class="etichetta">Stato occupazionale&nbsp;</td>
  <td nowrap class="campo">
    <input type="hidden" name="prgStatoOccupaz" value="<%=Utils.notNull(PRGSTATOOCCUPAZ)%>" >
    <input type="hidden" name="codStatoOccupaz" value="<%=Utils.notNull(CODSTATOOCCUPAZ)%>" >
    <%int size = 11, max = 65;
      if(descStato != null)
      { size = descStato.length()+3;
        if(size > max) 
        { descStato = descStato.substring(0,max)+"...";
          size = max + 4;
        }
      }
    %><af:textBox classNameBase="input" name="prgStatoOccupaz2" readonly="true" value="<%=Utils.notNull(descStato)%>"
                  required="false" title="Stato occupazionale" onKeyUp="fieldChanged();" size="<%=size%>" maxlength="100"/>
  </td>
  <% if(codAtto.equals("PR")){ %>
      <td>
        <input type="button" name="dettaglioStatooccupaz"  class="pulsanti"  value="Dettaglio" onClick="javascript:apriDettaglioStatoOccupazDich()">
      </td>
  <% } %>
</tr>

<tr ><td colspan="5"><div class="sezione"></div></td></tr>

<tr>
  <td colspan="1" class="etichetta">Data dichiarazione</td>
  <td colspan="1" class="campo">
    <af:textBox classNameBase="input" type="date" name="datDichiarazione" value="<%=Utils.notNull(datDichiarazione)%>"
                readonly="<%=String.valueOf(rdOnly||!flag_insert)%>" onKeyUp="fieldChanged();" required="true" title="Data dichiarazione/conferma"
                validateOnPost="true" size="12" maxlength="10" />
  </td>
</tr>
<tr>
<% if (prgDichDisponibilita!=null) {
    /*
    *   Questo perche' il prgDichDisponibilita e' valorizzato quando c' e' una riga da visualizzare. In fase di inserimento questo 
    *   valore e' assente e per non doverlo mettere in sessione conviene nascondere questo parametro. Il modulo che va ad eseguire 
    *   la query, non trovando questo parametro eseguira' la query (equivalente) che utilizza il parametro cdnLavoratore, sempre
    *   presente nella pagina in ogni suo stato.
    */
%> 
    <td><input type="hidden" name="prgDichDisponibilita" value="<%=Utils.notNull(prgDichDisponibilita)%>" size="22" maxlength="16"></td>
    <%}%>
</tr>


<%--
<% if(flag_insert) {%>
<tr colspan>
  <td class="etichetta">Tipo dichiarazione&nbsp;</td>
  <td   class="campo">
    <af:comboBox disabled="<%=String.valueOf(rdOnly)%>"  name="CODTIPODICHDISP"  classNameBase="input"
                 addBlank="true" blankValue="" required="true" title="Tipo dichiarazione">
        <option value="ID">Immediata disponibilità</option>
    </af:comboBox>
  </td>
</tr>


<%} else {%>
--%>
<tr>
  <td class="etichetta">Tipo dichiarazione&nbsp;</td>
  <td   class="campo">
      <%String cod = Utils.notNull(CODTIPODICHDISP);
    if (cod.equals("")) cod = "ID";%>
    <af:comboBox disabled="<%=String.valueOf(rdOnly)%>"  name="CODTIPODICHDISP"  moduleName="M_TIPODICHDISP" 
                selectedValue="<%=cod%>" classNameBase="input" onChange="fieldChanged();"
                 addBlank="true" blankValue="" required="true" title="Tipo dichiarazione"/>
  </td>
</tr>
<%--}--%>
<tr><td colspan="2">&nbsp;</td><tr>
  <td class="etichetta" >Attività lavorativa precedente<br> o in corso</td>
<!--</tr>
<tr>
    <td></td>-->
  <td   class="campo">
  <% String codCon = Utils.notNull(CODULTIMOCONTRATTO);
  if (codCon.equals("")) codCon =   codContrattoMovimento; %>
   <af:comboBox disabled ="<%=\"true\"/*String.valueOf(rdOnly)*/%>"  name="CODULTIMOCONTRATTO"  moduleName="M_CONTRATTO" classNameBase="input"
                            selectedValue="<%=codCon%>"  addBlank="true" onChange="fieldChanged();"/>
  </td>
</tr>

<tr><td><br/></td></tr>

<tr>
    <td class="etichetta">Data scadenza primo colloquio orientamento&nbsp;</td>
    <td class="campo">
        <af:textBox classNameBase="input" readonly="<%=String.valueOf(rdOnly)%>" onKeyUp="fieldChanged();" type="date"
                    name="DATSCADCONFERMA" value="<%=Utils.notNull(DATSCADCONFERMA)%>" validateOnPost="true" required="true"
                    title="Data scadenza primo colloquio orientamento" size="12" maxlength="10"/></td>
</tr>

<tr>
    <td class="etichetta">Data scadenza stipula patto&nbsp;</td>
    <td class="campo">
       <af:textBox classNameBase="input" readonly="<%=String.valueOf(rdOnly)%>" onKeyUp="fieldChanged();" type="date"
                   name="DATSCADEROGAZSERVIZI" value="<%=Utils.notNull(DATSCADEROGAZSERVIZI)%>" validateOnPost="true" 
                   size="12" maxlength="10"/></td>
  
</tr>

<tr>
    <td class="etichetta">Dich. P.Iva non movimentata&nbsp;</td>
    <td class="campo">
    	<af:comboBox classNameBase="input" name="flgLavoroAutonomo" addBlank="false" onChange="fieldChanged();" 
    		title="Dich. P.Iva non movimentata" disabled="<%=String.valueOf(rdOnly||!flag_insert)%>">
            <OPTION value=""  <%if (flgLavoroAutonomo.equals("") || flgLavoroAutonomo.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>></OPTION>
            <OPTION value="S" <%if (flgLavoroAutonomo.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
        </af:comboBox>
    </td>
</tr>

<tr>
    <td class="etichetta">Rischio disoccupazione&nbsp;</td>
    <td class="campo">
    	<af:comboBox classNameBase="input" name="flgRischioDisoccupazione" addBlank="false" onChange="fieldChanged();requireDatLicenziamento();" 
    		title="Rischio disoccupazione" disabled="<%=String.valueOf(rdOnly||!flag_insert)%>">
            <OPTION value=""  <%if (flgRischioDisoccupazione.equals("") || flgRischioDisoccupazione.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>></OPTION>
            <OPTION value="S" <%if (flgRischioDisoccupazione.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
        </af:comboBox>
    </td>
</tr>

<tr id="visDatLicenziamento" style="display: none">
    <td class="etichetta">Data licenziamento&nbsp;</td>
    <td class="campo">
       <af:textBox classNameBase="input" readonly="<%=String.valueOf(rdOnly||!flag_insert)%>" onKeyUp="fieldChanged();" type="date"
                   name="datLicenziamento" value="<%=Utils.notNull(datLicenziamento)%>" validateOnPost="true"  
                   title="Data licenziamento" size="12" maxlength="10"/>&nbsp;*&nbsp;</td>
</tr>

<%if (numConfigDidL68.equals(it.eng.sil.module.movimenti.constant.Properties.CUSTOM_CONFIG)) {%>
	<tr>
	    <td class="etichetta">DID fittizia finalizzata all'iscrizione L.68/99&nbsp;</td>
	    <td class="campo">
	    	<af:comboBox classNameBase="input" name="flgDidL68" addBlank="false" onChange="fieldChanged();" 
	    		title="DID fittizia finalizzata all'iscrizione L.68/99" disabled="<%=String.valueOf(rdOnly||!flag_insert)%>">
	            <OPTION value=""  <%if (flgDidL68.equals("") || flgDidL68.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>></OPTION>
	            <OPTION value="S" <%if (flgDidL68.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
	        </af:comboBox>
	    </td>
	</tr>
<%} else {%>
	<input type="hidden" name="flgDidL68" value="<%=Utils.notNull(flgDidL68)%>" >
<%}%>
<tr><td><br/></td></tr>

<tr>
    <td colspan=2>    
        <table class='sezione2' cellspacing=0 cellpadding=0>
            <tr>
                <td  width=18>
                    <img id='IMG0' src='<%=img0 %>' onclick='cambia(this, document.getElementById("TBL0"))'></td>
                        <td  class='titolo_sezione'>Chiusura dell'atto</td>    				
                <td align='right' width='30'></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td colspan=2>
        <TABLE id='TBL0' style='width:100%;display:<%=display0%>'>     
        <script>initSezioni(new Sezione(document.getElementById('TBL0'),document.getElementById('IMG0'),false));</script>
			<tr>
                <td class="etichetta">Data fine atto &nbsp;</td>
                <td class="campo">
                  <af:textBox classNameBase="input" readonly="<%=String.valueOf(impedisciChiusura)%>" onKeyUp="fieldChanged();" type="date"
                              name="DATFINE" value="<%=Utils.notNull(DATFINE)%>" validateOnPost="true" 
                              size="12" maxlength="10"/></td>
            </tr>
            <tr>
                <td class="etichetta">Motivo fine atto&nbsp;</td>
                <td class="campo">
                  <af:comboBox disabled ="<%=String.valueOf(impedisciChiusura)%>"  name="CODMOTIVOFINEATTO" onChange="fieldChanged();"  
                            moduleName="M_MOTFINEATTO" selectedValue="<%=Utils.notNull(CODMOTIVOFINEATTO)%>"  
                            classNameBase ="input" addBlank="true"/></td>
            </tr>
            
            <% if (canChiusuraDidMultipla) { %>
            <tr>
                <td class="etichetta">Numero determina&nbsp;</td>
                <td class="campo">
            		<af:textBox classNameBase="input" 
            					type="string" 
            					name="NUMDELIBERA" 
            					value="<%=Utils.notNull(NUMDELIBERA)%>" 
            					validateOnPost="true" 
                      			readonly="<%=String.valueOf(impedisciChiusura)%>" 
                      			onKeyUp="fieldChanged();" 
                      			required="false" 
                      			title="Numero determina"
                      			size="45" 
                      			maxlength="20"
                      			disabled="<%=String.valueOf(impedisciChiusura)%>"
                    />
            	</td>
            </tr>
            <% } else { %>
            <input type="hidden" name="NUMDELIBERA" value="<%=Utils.notNull(NUMDELIBERA)%>" />
            <% } %>
        </table>
    </td>
</tr>

<!-- sezione riapertura atto !-->
<% if ( !codMotivoRiaperturaAtto.equals("") &&
  	    ! confirmRiapertura &&
        ! confirmAnnulla) { %>

<tr>
    <td colspan=2>    
        <table class='sezione2' cellspacing=0 cellpadding=0>
            <tr>
                <td  width=18>
                   <img id='IMG1' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("SEZ_RIAP"))'></td>
                        <td  class='titolo_sezione'>Riapertura dell'atto</td>    				
                <td align='right' width='30'></td>
            </tr>
		</table> 
		
        <table id='SEZ_RIAP' style='width:100%;display:inline'>   
            <tr>
			    <td class="etichetta">Motivo riapertura atto</td>
			    <td class="campo" width="20%">
			    	
			    	<af:comboBox name="codMotivoRiapertura"  moduleName="M_getMotivoRiaperturaDid" classNameBase="input"
			    				 selectedValue="<%=codMotivoRiaperturaAtto%>"  addBlank="true" disabled="true"
			    				 />
			      	&nbsp;*&nbsp;&nbsp;
			    </td>
			</tr>
		</table>

    </td>
</tr>

	
<% } %>


<tr ><td colspan="5"><br></td></tr>
<tr>
    <td class="etichetta">Note&nbsp;</td>
    <td class="campo">
        <af:textArea classNameBase="textarea" name="strNote" value="<%=Utils.notNull(STRNOTE)%>"
                 cols="60" rows="4" maxlength="1000"
                 onKeyUp="fieldChanged();" readonly="<%=String.valueOf(rdOnly&&impedisciChiusura)%>"/></td>
</tr>



<%if (!flag_insert) {%>
<tr><!--    <td>NUMKLODICHDISP&nbsp;</td> -->
    <td><input type="hidden" name="NUMKLODICHDISP2" value="<%=Utils.notNull(NUMKLODICHDISP)%>" size="10" maxlength="10" >
        <%int keyLock = NUMKLODICHDISP==null ? -1 : Integer.parseInt(NUMKLODICHDISP.toString()); keyLock++;%>
        <input type="hidden" name="NUMKLODICHDISP"  value="<%=keyLock%>"/>
    </td>
        
</tr>
<%}%>

</table>

<BR>
<center>
  <% //if (!fromPatto&&operatoreInfo!=null) operatoreInfo.showHTML(out); %>
</center>

<table class="main">
<% if (!flag_insert) { %>
<tr>
  	<td align="right" width="25%" >
    <%if (fromPatto) {%>
    	<input class="pulsante" type="button" name="docIdent" value="Documento di identificazione"       
             onClick="javascript:apriDocIdentificazione();">   
    <%}else {%>
        <input class="pulsante" type="button" name="Documenti associati" value="Documenti associati"
           onClick="docAssociati('<%=cdnLavoratore%>','DispoDettaglioPage','<%=_cdnFunz%>', '<%=target%>','<%=Utils.notNull(prgDichDisponibilita)%>')">    
    <%}%>
    </td>
    <td align="center" width="25%" ></td>
    <td align="left" width="25%" ><%if(!impedisciChiusura){%>
      <input type="hidden" name="Salva" value="Aggiorna" disabled="true">
      <input type="submit" name="Salva_button"  class="pulsanti"  value="Aggiorna"><%}%>
    </td>
    <td align="center" width="25%" >
    	<% if(canPrint){ %>
    		<input name="Invia" type="button" class="pulsanti" value="stampa la dichiarazione"
           		   onclick="controllaDocumentoIdentita(<%=hasCartaIdentita%>)" >
    	<%} if(regione.equals("2") && !canPrint) {%>
    		<input name="Invia" type="button" class="pulsanti" value="Ricevuta DID"
           		onclick="ricevuta()" >
    	<%}%>
    </td>
</tr>
    <tr>
	  <td align="right" width="25%" >
	    <input class="pulsante" type="button" name="privacy" value="Privacy" onClick="javascript:apriPrivacy('<%=cdnLavoratore%>', '<%=cdnFunzione%>');">
	  </td>
	  <td align="center" width="25%" ></td>
	  <td align="left" width="25%">
	    <%if(reopenIscrL68){%>
	      <input class="pulsante" type="button" name="riaprIscrL68" value="Riapri iscrizione L68"       
	             onClick="javascript:riapriIscrL68();"/>
	    <%}%>  
	  </td>
	  <td align="left" width="25%">  
	    <%if(infStorButt){%>
	      <input type="button" name="infSotriche" value="Informazioni storiche"
				 class="pulsanti<%=((storicoDid)?"":"Disabled")%>" <%=(!storicoDid)?"disabled=\"true\"":""%>
				 onClick="apriPopUpStoriche('<%= cdnLavoratore %>')" />
	    <%}%>
	  </td>
    </tr>
    <tr>
    <td align="right" width="25%" >
    <%if(canRichiestaSAP && !fromPatto) { %>
    	<input class="pulsanti<%=((disableRichiestaSap)?"Disabled":"")%>" onclick="callWS()" type="button" name="richiesta" value="Richiesta SAP" <%=(disableRichiestaSap)?"disabled=\"True\"":""%>>
    <%}%>
    </td>
    <td align="center" width="25%" ></td>
    <td align="left" width="25%" >
    <%if(buttonDidInps){ %>
      <input class="pulsanti<%=((hasDidInps)?"":"Disabled")%>" <%=(!hasDidInps)?"disabled=\"true\"":""%> type="button" name="didinps" value="DID INPS" onclick="goDidInps('<%= strCodFisc %>')">
    <%}%>
    </td>
	<%if(canConfermaDid){%>
	<td align="left" width="25%" >
		<input type="button" name="confirmDid" value="Conferma Dich. annuale" class="pulsanti" 
			onClick="apriPopUpConfermaAnn('<%=Utils.notNull(prgDichDisponibilita)%>','<%=cdnLavoratore%>')" />
	<%} else {%>
	 <td align="center" width="25%" ></td>
    <% }%>
    </tr>
    
    <tr>
	    <td align="right" width="25%">
	    <%if(canPi3) { %>
	    	<input class="pulsanti<%=((canPi3)?"":"Disabled")%>" onclick="javascript:callPi3()" type="button" name="protocollazionePi3" value="Protocollazione PiTre">
	    <%}%>
	    </td>
	    <td align="center" width="25%" ></td>
	    <td align="left" width="25%" ></td>
	    <td align="center" width="25%" ></td>
   	</tr>
   	
   	<tr>
	    <td align="right" width="25%">
	    <%if(canConferimentiDID) { %>
	    	<input class="pulsanti<%=((canConferimentiDID)?"":"Disabled")%>" onclick="javascript:callConferimentiDID()" type="button" name="conferimentiDID" value="Visualizza conferimento DID">
	    <%}%>
	    </td>
	    <td align="center" width="25%" ></td>
	    <td align="left" width="25%" ></td>
	    <td align="center" width="25%" ></td>
   	</tr>
    
  <%} else if(canInsert){%>
  <tr><td><br/></td></tr>
	<tr>
	   <td align="right" width="25%">
	      <input class="pulsante" type="button" name="docIdent" value="Documento di identificazione"       
	             onClick="javascript:apriDocIdentificazione();">   
	   </td>
	   <td width="25%" align="right">
	   <input type="hidden" name = "insert_dispo" value="Inserisci" disabled=true>
	   <input type="submit" class="pulsanti" name="insert_dispo_button" value="Inserisci"></td>
	   <td width="25%" align="right">
	     <%if(buttonAnnulla){ %>
	        <input class="pulsanti" type="button" name="annulla" value="Chiudi senza inserire" onclick="sceglipage(4)">
	        <% }else if (fromPatto) { %>
	        <input type="button" class="pulsanti" onclick="tornaAlPatto()" value="Torna al patto">
	        <%}%>
	   </td>
	   <td align="center" width="25%" ></td>
	</tr>    
    <tr>
    <td align="right" width="25%" ><input class="pulsante" type="button" name="privacy" value="Privacy" onClick="javascript:apriPrivacy('<%=cdnLavoratore%>', '<%=cdnFunzione%>');"></td>
    <td align="right" width="25%" >
    <%if(canRichiestaSAP && !fromPatto) { %>
    	<input class="pulsanti<%=((disableRichiestaSap)?"Disabled":"")%>" onclick="callWS()" type="button" name="richiesta" value="Richiesta SAP" <%=(disableRichiestaSap)?"disabled=\"True\"":""%>>
    <%}%>
    </td>
    <td align="center" width="25%" >
    <%if(buttonDidInps){ %>
      <input class="pulsanti<%=((hasDidInps)?"":"Disabled")%>" <%=(!hasDidInps)?"disabled=\"true\"":""%> type="button" name="didinps" value="DID INPS" onclick="goDidInps('<%= strCodFisc %>')">
     <%}%>
     </td>
  	<td align="left" width="25%" >
    <%if(infStorButt){%>
      <input type="button" name="infSotriche" value="Informazioni storiche"
			 class="pulsanti<%=((storicoDid)?"":"Disabled")%>" <%=(!storicoDid)?"disabled=\"true\"":""%>
			 onClick="apriPopUpStoriche('<%= cdnLavoratore %>')" />
    <%}%>
  	</td>
</tr>
<%} else if(!flag_competenza || (flag_insert && !canInsert)){  %> 
<tr>
  <td align="right" width="25%" >
  <%if(canRichiestaSAP){ %>
	<input class="pulsanti<%=((disableRichiestaSap)?"Disabled":"")%>" onclick="callWS()" type="button" name="richiesta" value="Richiesta SAP" <%=(disableRichiestaSap)?"disabled=\"True\"":""%>>
  <%}%>
  </td>
  <td align="center" width="25%">
  <%if(buttonDidInps){ %>
      <input class="pulsanti<%=((hasDidInps)?"":"Disabled")%>" <%=(!hasDidInps)?"disabled=\"true\"":""%> type="button" name="didinps" value="DID INPS" onclick="goDidInps('<%= strCodFisc %>')">
      <% } %>
     </td>
    <td align="right" width="25%">
    <%if(infStorButt){%>
      <input type="button" name="infSotriche" value="Informazioni storiche"
			 class="pulsanti<%=((storicoDid)?"":"Disabled")%>" <%=(!storicoDid)?"disabled=\"true\"":""%>
			 onClick="apriPopUpStoriche('<%= cdnLavoratore %>')" />
    <%}%>
  </td>
  <td align="center" width="25%"></td>
</tr> 
<%}
  if (!flag_insert && fromPatto) { %>
<tr>
    <td colspan="4" align="center"><input type="button" class="pulsanti" onclick="tornaAlPatto()" value="Torna al patto"/></td>
</tr>
<%  } %>
</table>

<input  type="hidden" name="CDNLAVORATORE" value="<%=Utils.notNull(cdnLavoratore)%>">    
<input  type="hidden" name="CDNUTINS" value="<%=Utils.notNull(cdnUtIns)%>">    
<input  type="hidden" name="CDNUTMOD" value="<%=Utils.notNull(cdnUtMod)%>">    
<input  type="hidden" name="DTMINS" value="<%=Utils.notNull(dtmIns)%>">    

<% if (fromPatto) {%>
<input type="hidden" name="fromPattoDettaglio" value="">
<input type="hidden" name="FRAME_NAME" value="<%=target%>">
<%}%>
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"> 
<input type="hidden" name="REPORT" value="">      
<input type="hidden" name="PROMPT0" value=""> 
<input type="hidden" name="PROMPT1" value=""> 
<input type="hidden" name="PAGE" value="DispoDettaglioPage">
<input type="hidden" name="prgElencoAnagrafico" value="<%=Utils.notNull(prgElencoAnagrafico)%>">
<input type="hidden" name="PRGDOCUMENTO" value="<%=codCartaIdentitaStr%>">
<input type="hidden" name="cat181" value="<%=cat181%>">
<input type="hidden" name="flgObbSco" value="<%=flgObbSco%>">
<input type="hidden" name="flgLaurea" value="<%=flgLaurea%>">
<input type="hidden" name="annoNascita" value="<%=annoNascita%>">
<input type="hidden" name="FORZA_INSERIMENTO" value="<%=forzaRicostruzione%>">
<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="<%=continuaRicalcolo%>">
<%out.print(htmlStreamBottom);%>
</af:form>
<%if (confirmRiapertura) {%>
<af:form name="FrmRiapertura" method="POST" action="AdapterHTTP">
<input type="hidden" name="PAGE" value="DispoDettaglioPage">
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
<input type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>">
<input type="hidden" name="riapriDid" value="true">
<input type="hidden" name="FORZA_INSERIMENTO" value="<%=forzaRicostruzione%>">
<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="<%=continuaRicalcolo%>">
<input type="hidden" name="numKloDid" value="<%=NUMKLODICHDISPStr%>">
<input type="hidden" name="prgDichDisp" value="<%=prgDichDisponibilitaStr%>">
<input type="hidden" name="prgDichDisponibilita" value="<%=prgDichDisponibilitaStr%>">
<input type="hidden" name="note" value="<%=Utils.notNull(STRNOTE)%>">
<input type="hidden" name="prgPatto" value="<%=prgPattoStr%>">
<input type="hidden" name="numKloPatto" value="<%=NUMKLOPATTOStr%>">
<input type="hidden" name="codMotivoRiapertura" value="<%=codMotivoRiaperturaAtto%>">
<input type="hidden" name="datDichiarazione" value="<%=datDichiarazioneRiapri%>">
</af:form>
<%}
else {
	if (confirmAnnulla) {%>
	<af:form name="FrmRiannulla" method="POST" action="AdapterHTTP">
	<input type="hidden" name="PAGE" value="DispoDettaglioPage">
	<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
	<input type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>">
	<input type="hidden" name="annullaDid" value="true">
	<input type="hidden" name="FORZA_INSERIMENTO" value="<%=forzaRicostruzione%>">
	<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="<%=continuaRicalcolo%>">
	<input type="hidden" name="numKloDid" value="<%=NUMKLODICHDISPStr%>">
	<input type="hidden" name="prgDichDisp" value="<%=prgDichDisponibilitaStr%>">
	<input type="hidden" name="prgDichDisponibilita" value="<%=prgDichDisponibilitaStr%>">
	<input type="hidden" name="prgPatto" value="<%=prgPattoStr%>">
	<input type="hidden" name="motivoAnnullamento" value="<%=motivoAnnullamento%>">
	<input type="hidden" name="prgDocumento" value="<%=prgDocumentoStr%>">
	<input type="hidden" name="statoAtto" value="<%=CODSTATOATTOANNULLA%>">
	<input type="hidden" name="datDichiarazione" value="<%=datDichiarazioneAnnulla%>">
	</af:form>
	<%}
}
%>
<center>
  <% if (!fromPatto&&operatoreInfo!=null) operatoreInfo.showHTML(out); %>
</center>
<%if (confirmChiusura) {%>
	<script language="Javascript">
		if (confirm("<%=msgConferma%>")) { 
			ripetiOperazione('chiusura');
		}
		else {
			document.Frm1.FORZA_INSERIMENTO.value = "false";
			document.Frm1.CONTINUA_CALCOLO_SOCC.value = "false";
			document.Frm1.DATFINE.value = "";
			document.Frm1.strNote.value = "";
			document.Frm1.CODMOTIVOFINEATTO.value = "";
			document.Frm1.NUMDELIBERA.value = "";
		}
	</script>
<%}
else {
	if (confirmRiapertura) {%>
		<script language="Javascript">
		if (confirm("<%=msgConferma%>")) { 
			ripetiOperazione('riapertura');
		}
		else {
			document.Frm1.FORZA_INSERIMENTO.value = "false";
			document.Frm1.CONTINUA_CALCOLO_SOCC.value = "false";
			document.FrmRiapertura.FORZA_INSERIMENTO.value = "false";
			document.FrmRiapertura.CONTINUA_CALCOLO_SOCC.value = "false";
		}
		</script>	
	<%}
	else {
		if (confirmAnnulla) {%>
			<script language="Javascript">
			if (confirm("<%=msgConferma%>")) { 
				ripetiOperazione('annulla');
			}
			else {
				document.Frm1.FORZA_INSERIMENTO.value = "false";
				document.Frm1.CONTINUA_CALCOLO_SOCC.value = "false";
				document.FrmRiannulla.FORZA_INSERIMENTO.value = "false";
				document.FrmRiannulla.CONTINUA_CALCOLO_SOCC.value = "false";
			}
			</script>	
		<%}
	}
}%>
</body>
</html>
