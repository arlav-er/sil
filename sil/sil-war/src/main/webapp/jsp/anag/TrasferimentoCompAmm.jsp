<!-- Pagina dei trasferimenti amministrativi -->
<%@page import="it.eng.sil.util.amministrazione.impatti.EventoAmministrativo"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.tracing.*,
                  com.engiweb.framework.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
  				  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.module.movimenti.InfoLavoratore,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.error.EMFUserError" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%--
	QUESTA PAGINA UTILIZZA DATI IN SESSION_CONTAINER. Si tratta della serviceRequest e delle informazioni sull'esito
	del trasferimento e della stampa provenienti dalla action Trasferimento. Se dopo la stampa, avvenuta con successo
	o meno, si dovesse chiudere la pop-up, l' oggetto sessionContainer rimarrebbe sporco fino al successivo caricamento
	di questa pagina.
	gli attributi sono: trasferito (se il lavoratore e' stato trasferito con successo), 
						SERVICE_REQUEST (e' la serviceRequest della pagina prima della chiamata della stampa), 
						reportResult (se la stampa e protocollazione e' avvenuta con successo). 
--%>
<%  
	final int STAMPA_TRASF_DA_PROTOCOLLARE = 0; //la stampa trasf. e' fallita, bisogna stamparla
	final int STAMPA_RICH_DOC_LAV_DA_PROTOCOLLARE   = 1; // stampa trasferimento protocollata, bisogna stampare il documento
	final int STAMPE_PROTOCOLLATE          = 2; // operazione completata (in questo stato non dovrebbe mai entrare)
	int statoTrasf = -1; 
	String tipoTrasferimento="TP";
	String queryString = null;
	//Controllo se in sessione ho il CodCpi di destinazione (cdnTipoGruppo dell'utente == 1)
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	String codCpiUser = user.getCodRif();
	boolean codCpiUserNotFound = false;
	if(cdnTipoGruppo != 1) {
		codCpiUserNotFound = true;
	}
	
	String CDNLAVORATORE = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNLAVORATORE");
	String codCpiOrig = "";
	String codMonoTipoCpiOrig = "";
	String desCpiOrig= "";
	String provCpiOrig= "";
	String regCpiOrig= "";
	String userCodCpi = StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDescrCpiUser.ROWS.ROW.CODCPI");
	String userProvCpi= StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDescrCpiUser.ROWS.ROW.CODPROVCPI");
	String userRegCpi= StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDescrCpiUser.ROWS.ROW.CODREGCPI");
	String msgIRLav= "";
	String msgXIR= "";
	boolean poloInCoop= false;
	boolean isInterProvincia= false;
	boolean isInterRegione= false;
	boolean coopAttiva = false;
	boolean isRefresh = serviceRequest.containsAttribute("REFRESH");
	String IntraProvinciale = "false";
	if (!isRefresh) {  
		IntraProvinciale = (String) serviceResponse.getAttribute("M_CHECKSTESSAPROVINCIA.IntraProvinciale");
	}
	if (IntraProvinciale.equals("false")) {
		if (isRefresh) {
  			String coopAbilitata = System.getProperty("cooperazione.enabled");
  			if (coopAbilitata != null && coopAbilitata.equals("true")) {
  				coopAttiva = true;
  			}
			codCpiOrig = StringUtils.getAttributeStrNotNull(serviceRequest,"CODCPIORIG");
			SourceBean descrCpiOrig = (SourceBean) serviceResponse.getAttribute("M_COMMONGETDESCRCPI.ROWS.ROW");
			if (descrCpiOrig != null) {
				desCpiOrig=StringUtils.getAttributeStrNotNull(descrCpiOrig, "DESCRCPI");	
				provCpiOrig=StringUtils.getAttributeStrNotNull(descrCpiOrig, "CODPROVCPI");
				regCpiOrig=StringUtils.getAttributeStrNotNull(descrCpiOrig, "CODREGCPI");
			}
			msgIRLav =  StringUtils.getAttributeStrNotNull(serviceRequest, "msgIRLav");
			msgXIR =  StringUtils.getAttributeStrNotNull(serviceRequest, "msgXIR");
			String isInterProv =  StringUtils.getAttributeStrNotNull(serviceRequest, "isInterProvincia");
			String isInterReg =  StringUtils.getAttributeStrNotNull(serviceRequest, "isInterRegione");
			if (coopAttiva) {
				tipoTrasferimento="TIP";
			} else {
				tipoTrasferimento="TIPNC";
			}
			if (isInterProv.equals("ER") && isInterReg.equals("ER")) {
				isInterRegione = true;
				isInterProvincia = true;
				tipoTrasferimento="ERR";
			} else if (isInterProv.equals("SI") && isInterReg.equals("NO")) {
				isInterProvincia = true;
			} else if (isInterReg.equals("SI") && isInterProv.equals("NO")) {
				isInterRegione = true;
			}
			if (coopAttiva && isInterProvincia && tipoTrasferimento.equals("TIP")) {
				if (((String)serviceResponse.getAttribute("M_COOP_CheckProvinciaAttiva.poloInCoop")).equals("true")) {
					poloInCoop = true;
				}				
			}	
		} else {
			if (((String) serviceResponse.getAttribute("M_CHECKSTESSAPROVINCIA.coopAttiva")).equals("true")) {
				coopAttiva = true;
				String codicefiscale = (String) serviceResponse.getAttribute("M_GetDatiLav.ROWS.ROW.STRCODICEFISCALE");
				SourceBean cpiMaster = (SourceBean)  serviceResponse.getAttribute("M_COOP_GETCPIMASTERIR.ROWS.ROW");
				if (cpiMaster == null || !cpiMaster.containsAttribute("codCpiMaster")) {
					// le impostazioni seguenti individuano uno stato di errore
					isInterRegione = true;
					isInterProvincia = true;
					tipoTrasferimento = "ERR";
					//
					Collection errLista =  responseContainer.getErrorHandler().getErrors();
					Iterator iter = errLista.iterator();
					while (iter.hasNext()){
						Object objerror = iter.next();
						if (objerror instanceof EMFUserError) {
							EMFUserError errorCode =(EMFUserError)objerror;
					
							msgXIR= "Procedere selezionando il CPI manualmente";
							if (errorCode.getCode() == MessageCodes.Coop.ERR_IR_LAV_NOTFOUND) 
								msgIRLav = "Lavoratore non presente sull'indice regionale";
							if (errorCode.getCode() == MessageCodes.Coop.ERR_CONNESSIONE_IR || 
								errorCode.getCode() == MessageCodes.Coop.ERR_CPI_MASTER_IR ||
								errorCode.getCode() == MessageCodes.General.OPERATION_FAIL)
								msgIRLav = "Non è stato possibile contattare l'indice regionale";
							break;
						}
					}
				} else {
					SourceBean descrCpiMaster = (SourceBean) serviceResponse.getAttribute("M_COMMONGETDESCRCPI.ROWS.ROW");		
					codCpiOrig=StringUtils.getAttributeStrNotNull(cpiMaster, "codCpiMaster");
					codMonoTipoCpiOrig=StringUtils.getAttributeStrNotNull(cpiMaster, "codTipoMaster");
					if (codMonoTipoCpiOrig.equals("T")) {
						codCpiOrig= "";
						codMonoTipoCpiOrig= "";
						msgXIR= "Procedere selezionando il CPI manualmente";
						msgIRLav = "Dall'indice regionale il lavoratore risulta esterno alla regione";
						isInterRegione = true;
						tipoTrasferimento="TIP";	
					} else {	
						if (!codCpiOrig.equals(userCodCpi)) {
							desCpiOrig=StringUtils.getAttributeStrNotNull(descrCpiMaster, "DESCRCPI");	
							provCpiOrig=StringUtils.getAttributeStrNotNull(descrCpiMaster, "CODPROVCPI");
							regCpiOrig=StringUtils.getAttributeStrNotNull(descrCpiMaster, "CODREGCPI");
							if (!provCpiOrig.equals(userProvCpi)) {
								if (((String)serviceResponse.getAttribute("M_COOP_CheckProvinciaAttiva.poloInCoop")).equals("true")) {
									poloInCoop = true;
								}
								isInterProvincia = true;
								tipoTrasferimento="TIP";
							} else {
								codCpiOrig= "";
								codMonoTipoCpiOrig= "";
								desCpiOrig= "";	
								provCpiOrig= "";
								regCpiOrig= "";
								msgIRLav= "Dall'indice regionale risulta che il CPI di provenienza è intraprovinciale";
								msgXIR= "Procedere selezionando il CPI manualmente";
							}
						} else {
							codCpiOrig= "";
							codMonoTipoCpiOrig= "";
							msgIRLav= "Dall'indice regionale risulta che il CPI di provenienza ed il nuovo coincidono";
							msgXIR= "Procedere selezionando il CPI manualmente";
						}	
					}
				}
			} else {
				tipoTrasferimento = "TIPNC";
			}
		}
	}
	
	//String codCpiOrig_Req = StringUtils.getAttributeStrNotNull(serviceRequest,"CODCPIORIG");
	String codCpiUtente_Req= StringUtils.getAttributeStrNotNull(serviceRequest,"CODCPIUTENTE");
	
	//Creazione oggetto per info Lavoratore
	InfCorrentiLav infCorrentiLav = new InfCorrentiLav(requestContainer.getSessionContainer(), CDNLAVORATORE, user);
	infCorrentiLav.setSkipLista(true);

	// NOTE: Attributi della pagina (pulsanti e link) 
	boolean canModify = true;
	String _funzione =  StringUtils.getAttributeStrNotNull(serviceRequest,"CDNFUNZIONE");

	//Oggetti per l'applicazione dello stile
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

	//Recupero contesto
	boolean trasferisci = false;
	boolean trasferito = false;
	boolean protocollato = true;

	String strTrasferisci =  StringUtils.getAttributeStrNotNull(serviceRequest, "trasferisci");

	if ("true".equalsIgnoreCase(strTrasferisci)) {
		trasferisci = true;
		String strTrasferito = (String)RequestContainer.getRequestContainer().getSessionContainer().getAttribute("trasferito");
		RequestContainer.getRequestContainer().getSessionContainer().delAttribute("trasferito");
		if ("true".equalsIgnoreCase(strTrasferito)) {
			trasferito = true;
			canModify = false;
			String reportOperation = (String)RequestContainer.getRequestContainer().getSessionContainer().getAttribute("reportResult");
			RequestContainer.getRequestContainer().getSessionContainer().delAttribute("reportResult");
			if ("ERROR".equals(reportOperation)) {
				statoTrasf = STAMPA_TRASF_DA_PROTOCOLLARE;
			} 
			else {
				String richDocStampata = (String)RequestContainer.getRequestContainer().getSessionContainer().getAttribute("richDocStampata");
				//RequestContainer.getRequestContainer().getSessionContainer().delAttribute("richDocStampata");
				if  (richDocStampata!=null && richDocStampata.equals("true"))
					statoTrasf = STAMPE_PROTOCOLLATE;
				else 
					statoTrasf = STAMPA_RICH_DOC_LAV_DA_PROTOCOLLARE;
				}
		}
		else { // e' fallito il trasferimento: ripristino la precedente serviceRequest. statoTrasf rimane = -1
	  		serviceRequest = (SourceBean)RequestContainer.getRequestContainer().getSessionContainer().getAttribute("SERVICE_REQUEST");
	  		RequestContainer.getRequestContainer().getSessionContainer().delAttribute("SERVICE_REQUEST");
		}
	}
	// ripulisco il sessionContainer
	RequestContainer.getRequestContainer().getSessionContainer().delAttribute("richDocStampata");
	RequestContainer.getRequestContainer().getSessionContainer().delAttribute("SERVICE_REQUEST");
	RequestContainer.getRequestContainer().getSessionContainer().delAttribute("reportResult");
	RequestContainer.getRequestContainer().getSessionContainer().delAttribute("trasferito");	
	/* ho eliminato per ora il riporto degli errori nella jsp madre
	// se si e' verificato un errore dopo il trasferimento e la stampa lo riporto a video
	EMFUserError error = (EMFUserError)getRequestContainer(request).getSessionContainer().getAttribute("errore_in_trasferimento");
	if (error!=null) {
		getRequestContainer(request).getSessionContainer().delAttribute("errore_in_trasferimento");
	    getResponseContainer(request).getErrorHandler().addError(error);
	}*/	
	//Variabili
	String PRGLAVSTORIAINF = "";
	String datInizio = ""; 
	String codCpiTit = ""; 
	String strCpiTit = "";
	String CODCOMCPITIT = "";	
	String CODPROVCPITIT = "";	
	String CODMONOTIPOCPI = "";
	String CODMONOTIPOCPIOLD = "";	 
	String codComdom = "";
	String strComdom = "";
	String strCapDom = "";	 
	String strCodiceFiscale="";
	String strCognome="";
	String strNome="";
	String datNasc="";
	String codComNas="";
	String strComNas="";
	String STRCODICEFISCALEOLD = ""; 
	String CDNUTMODSCHEDAANAGPROF = ""; 
	String DTMMODSCHEDAANAGPROF = ""; 
	String CODMONOTIPOORIG = "";
	String dataInizio=""; 
	String DATTRASFERIMENTO = "";  	
	String DATDICHIARAZIONE = ""; 
	boolean isPresenteDid = false;
	String FLG181 = "";
	String dataAnzDisoc = "";
	BigDecimal mesiAnz = null;
	BigDecimal mesiAnzPrec = null;
	BigDecimal numMesiSosp = null;
	BigDecimal numMesiSospPrec = null;	
	String FLGCESSATO = ""; 
	String CODMOTIVOCESSATO = ""; 
	String CODSTATOOCCUPAZORIG = "";
	String CODSTATOOCCUPAZ = "";	
	String codStatoOccRagg = ""; 
	String CODLAVFLAG = "";
	String CODSTATOOCCUPAZDERIVATODAPRO = "";
	String STRCHIAVETABELLAPROLABOR = ""; 
	String DATCHIAVETABELLAPROLABOR = ""; 
	String STRNOMETABELLAPROLABOR = ""; 
	String DATFINE = "";
	String STRNOTE = ""; 
	String CDNUTINS = ""; 
	String DTMINS = ""; 
	String CDNUTMOD = ""; 
	String DTMMOD = ""; 
	String NUMKLOLAVSTORIAINF = ""; 
	String codMonoCalcAnzPrec = ""; 
	String datcalcoloanzianita = "";
	String datcalcolomesisosp = "";
	String CODCPIUSER = "";
	String DESCRCPIUSER = "";
	String CODCOMCPIUSER = "";
	String CODPROVCPIUSER = "";
	String CODREGCPIUSER = "";
	BigDecimal prgStatoOccupaz = null;
	boolean visualizzazione = false;
    String numMesiSospInt   = null;
    String numMesiSospPrecInt   = null;
    String mesiAnzInt       = null;    
    String mesiAnzPrecInt       = null;
    String STRLOCALITADOM = "";
    String STRINDIRIZZODOM = "";
    String controllaCodComInCPI = "";
    BigDecimal numKloLavoratore = null;
    String prgElencoAnag = null;
    BigDecimal numKloElencoAnag = null;
    String prgPattoLav = null;
    BigDecimal numKloPattoLav = null;
    String pageDocAssociata = null;
	//Data odierna da impostare nella datTrasferimento
	String oggi = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
	
  	//Origine dati del trasferimento
  	boolean dataNotFound = false;
  	SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_TRAGETDETTTRASF.ROWS.ROW");
  	if (trasferisci && !trasferito) {
  		//Se stavo trasferendo ed è andata male riprendo i dati dalla request, così l'utente non se li deve
  		//riscrivere
  		dataOrigin = serviceRequest;
	} else if (dataOrigin == null){
		dataNotFound = true;
	}
	
	//Recupero dati
	if (dataOrigin != null && !codCpiUserNotFound) {
		PRGLAVSTORIAINF = Utils.notNull(dataOrigin.getAttribute( "PRGLAVSTORIAINF"));
		datInizio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIO");
		codCpiTit = StringUtils.getAttributeStrNotNull(dataOrigin, "codCPI");
		strCpiTit = StringUtils.getAttributeStrNotNull(dataOrigin, "strCPI");		
		CODCOMCPITIT =  StringUtils.getAttributeStrNotNull(dataOrigin, "CODCOMCPITIT");
		CODPROVCPITIT =  StringUtils.getAttributeStrNotNull(dataOrigin, "CODPROVCPITIT");
		CODMONOTIPOCPI =  StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOCPI");
		codComdom =  StringUtils.getAttributeStrNotNull(dataOrigin, "codComdom");
		strComdom =  StringUtils.getAttributeStrNotNull(dataOrigin, "strComdom");
		strCapDom =  StringUtils.getAttributeStrNotNull(dataOrigin, "strCapDom");
		STRLOCALITADOM = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLOCALITADOM");
		STRINDIRIZZODOM	= StringUtils.getAttributeStrNotNull(dataOrigin, "STRINDIRIZZODOM");
		strCodiceFiscale =  StringUtils.getAttributeStrNotNull(dataOrigin, "strCodiceFiscale");
		strCognome =  StringUtils.getAttributeStrNotNull(dataOrigin, "STRCOGNOME");
		strNome =  StringUtils.getAttributeStrNotNull(dataOrigin, "STRNOME");
		datNasc =  StringUtils.getAttributeStrNotNull(dataOrigin, "DATNASC");
		codComNas =  StringUtils.getAttributeStrNotNull(dataOrigin, "CODCOMNAS");
		strComNas =  StringUtils.getAttributeStrNotNull(dataOrigin, "STRCOMNAS");
		STRCODICEFISCALEOLD =  StringUtils.getAttributeStrNotNull(dataOrigin, "STRCODICEFISCALEOLD");
		CDNUTMODSCHEDAANAGPROF =  StringUtils.getAttributeStrNotNull(dataOrigin, "CDNUTMODSCHEDAANAGPROF");
		DTMMODSCHEDAANAGPROF =  StringUtils.getAttributeStrNotNull(dataOrigin, "DTMMODSCHEDAANAGPROF");
		CODMONOTIPOORIG =  StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOORIG");
		DATTRASFERIMENTO =   StringUtils.getAttributeStrNotNull(dataOrigin, "DATTRASFERIMENTO");
		DATDICHIARAZIONE =   StringUtils.getAttributeStrNotNull(dataOrigin, "DATDICHIARAZIONE");		
		if (tipoTrasferimento.equals("TIPNC")) {
			if (CODMONOTIPOCPI.equals("T")) {
				isInterRegione = true;
			} else {
				isInterProvincia = true;
			}
			codCpiOrig =  StringUtils.getAttributeStrNotNull(dataOrigin, "CODCPIORIG");
			desCpiOrig =  StringUtils.getAttributeStrNotNull(dataOrigin, "DESCRCPIORIG");			
		}
		if (IntraProvinciale.equals("true")) {
			codCpiOrig =  StringUtils.getAttributeStrNotNull(dataOrigin, "codCPI");
			desCpiOrig =  StringUtils.getAttributeStrNotNull(dataOrigin, "strCPI");
		}	
		FLG181 =  StringUtils.getAttributeStrNotNull(dataOrigin, "FLG181");		
		FLGCESSATO =  StringUtils.getAttributeStrNotNull(dataOrigin, "FLGCESSATO");
		CODMOTIVOCESSATO =  StringUtils.getAttributeStrNotNull(dataOrigin, "CODMOTIVOCESSATO");
		CODSTATOOCCUPAZORIG =  StringUtils.getAttributeStrNotNull(dataOrigin, "CODSTATOOCCUPAZORIG");	
		CODLAVFLAG =  StringUtils.getAttributeStrNotNull(dataOrigin, "CODLAVFLAG");
		CODSTATOOCCUPAZDERIVATODAPRO =  StringUtils.getAttributeStrNotNull(dataOrigin, "CODSTATOOCCUPAZDERIVATODAPRO");
		STRCHIAVETABELLAPROLABOR =  StringUtils.getAttributeStrNotNull(dataOrigin, "STRCHIAVETABELLAPROLABOR");
		DATCHIAVETABELLAPROLABOR = StringUtils.getAttributeStrNotNull(dataOrigin, "DATCHIAVETABELLAPROLABOR");
		STRNOMETABELLAPROLABOR =  StringUtils.getAttributeStrNotNull(dataOrigin, "STRNOMETABELLAPROLABOR");
		DATFINE =  StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINE");
		STRNOTE =  StringUtils.getAttributeStrNotNull(dataOrigin, "STRNOTE");
		CDNUTINS =  StringUtils.getAttributeStrNotNull(dataOrigin, "CDNUTINS");
		DTMINS =  StringUtils.getAttributeStrNotNull(dataOrigin, "DTMINS");
		CDNUTMOD =  StringUtils.getAttributeStrNotNull(dataOrigin, "CDNUTMOD");
		DTMMOD =  StringUtils.getAttributeStrNotNull(dataOrigin, "DTMMOD");
		NUMKLOLAVSTORIAINF =  Utils.notNull(dataOrigin.getAttribute( "NUMKLOLAVSTORIAINF"));
		if (!NUMKLOLAVSTORIAINF.equals("")) {NUMKLOLAVSTORIAINF = String.valueOf(Integer.parseInt(NUMKLOLAVSTORIAINF) + 1);}
	}

	if (isRefresh) {
		codComdom =  StringUtils.getAttributeStrNotNull(serviceRequest, "codComdom");
		strComdom =  StringUtils.getAttributeStrNotNull(serviceRequest, "strComdom");
		strCapDom =  StringUtils.getAttributeStrNotNull(serviceRequest, "strCapDom");
		STRLOCALITADOM = StringUtils.getAttributeStrNotNull(serviceRequest, "STRLOCALITADOM");
		STRINDIRIZZODOM	= StringUtils.getAttributeStrNotNull(serviceRequest, "STRINDIRIZZODOM");
		DATTRASFERIMENTO =   StringUtils.getAttributeStrNotNull(serviceRequest, "DATTRASFERIMENTO");
		FLG181 =  StringUtils.getAttributeStrNotNull(serviceRequest, "FLG181");		
		STRNOTE =  StringUtils.getAttributeStrNotNull(serviceRequest, "STRNOTE");
	}
	
	//Tranne nel caso di trasferimento andato bene questi dati li prendo dal DB e li incremento di uno
	if (!trasferito) {
		SourceBean DBOriginLav = (SourceBean) serviceResponse.getAttribute("M_TraGetDettTrasf.ROWS.ROW");	
		SourceBean DBOriginPatto = (SourceBean) serviceResponse.getAttribute("M_TraGetKloPattoTrasf.ROWS.ROW");	
		if (DBOriginLav != null) {
			numKloLavoratore = (BigDecimal) DBOriginLav.getAttribute("NUMKLOLAVORATORE");
			if (numKloLavoratore != null) {
				numKloLavoratore = numKloLavoratore.add(new BigDecimal(1));
			}
		} else dataNotFound = true;
		if (DBOriginPatto != null) {				
			prgPattoLav =  StringUtils.getAttributeStrNotNull(DBOriginPatto, "PRGPATTOLAVORATORE");
			numKloPattoLav =  (BigDecimal) DBOriginPatto.getAttribute("NUMKLOPATTOLAVORATORE");
			if (numKloPattoLav != null) {
				numKloPattoLav = numKloPattoLav.add(new BigDecimal(1));
			}		
		}
	}
	
	//Origine dati Stato occupazionale
	boolean dataOccNotFound = false;
  	SourceBean dataOriginStatoOccupazionale = (SourceBean) serviceResponse.getAttribute("M_GETSTATOOCCUPAZIONALE.ROWS.ROW");
  	if (trasferisci && !trasferito) {
  		//Se stavo trasferendo ed è andata male riprendo i dati dalla request, così l'utente non se li deve
  		//riscrivere
  		dataOriginStatoOccupazionale = serviceRequest;
	} else if (dataOriginStatoOccupazionale == null) {
		dataOccNotFound = true;
	}
		
	//Recupero dati Stato occupazionale, sono obbligato a farlo così perchè devo riutilizzare la query già 
	//creata per il modulo M_GETSTATOOCCUPAZIONALE
	if (dataOriginStatoOccupazionale != null && !codCpiUserNotFound && !(trasferisci && !trasferito)) {
        dataInizio      = (String)      dataOriginStatoOccupazionale.getAttribute("DATINIZIO");
        CODSTATOOCCUPAZ = (String)      dataOriginStatoOccupazionale.getAttribute("CODSTATOOCCUPAZ");
        numMesiSosp     = (BigDecimal)  dataOriginStatoOccupazionale.getAttribute("NUMMESISOSP");
        numMesiSospPrec = (BigDecimal)  dataOriginStatoOccupazionale.getAttribute("NUMMESISOSPPREC");
        dataAnzDisoc    = (String)      dataOriginStatoOccupazionale.getAttribute("DATANZIANITADISOC");
        mesiAnz         = (BigDecimal)  dataOriginStatoOccupazionale.getAttribute("MESI_ANZ");
        mesiAnzPrec         = (BigDecimal)  dataOriginStatoOccupazionale.getAttribute("MESI_ANZ_PREC");
        codMonoCalcAnzPrec  = (String)      dataOriginStatoOccupazionale.getAttribute("CODMONOCALCOLOANZIANITAPREC297");
        prgStatoOccupaz = (BigDecimal)  dataOriginStatoOccupazionale.getAttribute("PRGSTATOOCCUPAZ");
        codStatoOccRagg = (String)      dataOriginStatoOccupazionale.getAttribute("codstatooccupazragg");
        datcalcolomesisosp = (String)      dataOriginStatoOccupazionale.getAttribute("datcalcolomesisosp");
        datcalcoloanzianita = (String)      dataOriginStatoOccupazionale.getAttribute("datcalcoloanzianita");					
	} else if (dataOriginStatoOccupazionale != null && !codCpiUserNotFound) {
		//Visto che i dati li devo riprendere dalla request non posso riutilizzare lo stesso codice perchè 
		//in request ho tutte stringhe...
		dataInizio      = (String)      dataOriginStatoOccupazionale.getAttribute("DATINIZIO");
        CODSTATOOCCUPAZ = (String)      dataOriginStatoOccupazionale.getAttribute("CODSTATOOCCUPAZ");
        String strNumMesiSosp = (String) dataOriginStatoOccupazionale.getAttribute("NUMMESISOSP");
        if (strNumMesiSosp != null && !strNumMesiSosp.equals("")) numMesiSosp = new BigDecimal(strNumMesiSosp);
        String strNumMesiSospPrec = (String) dataOriginStatoOccupazionale.getAttribute("NUMMESISOSPPREC");
        if (strNumMesiSospPrec != null && !strNumMesiSospPrec.equals("")) numMesiSospPrec = new BigDecimal(strNumMesiSospPrec);
        dataAnzDisoc    = (String) dataOriginStatoOccupazionale.getAttribute("DATANZIANITADISOC");
        String strMesiAnz = (String) dataOriginStatoOccupazionale.getAttribute("MESI_ANZ");
        if (strMesiAnz != null && !strMesiAnz.equals("")) mesiAnz = new BigDecimal(strMesiAnz);        
        String strMesiAnzPrec = (String) dataOriginStatoOccupazionale.getAttribute("MESI_ANZ_PREC");
        if (strMesiAnzPrec != null && !strMesiAnzPrec.equals("")) mesiAnzPrec = new BigDecimal(strMesiAnzPrec);           
        codMonoCalcAnzPrec  = (String)      dataOriginStatoOccupazionale.getAttribute("CODMONOCALCOLOANZIANITAPREC297");
        String strPrgStatoOccupaz = (String) dataOriginStatoOccupazionale.getAttribute("PRGSTATOOCCUPAZ");
        if (strPrgStatoOccupaz != null && !strPrgStatoOccupaz.equals("")) prgStatoOccupaz = new BigDecimal(strPrgStatoOccupaz); 
        codStatoOccRagg = (String)      dataOriginStatoOccupazionale.getAttribute("codstatooccupazragg");
        datcalcolomesisosp = (String)      dataOriginStatoOccupazionale.getAttribute("datcalcolomesisosp");
        datcalcoloanzianita = (String)      dataOriginStatoOccupazionale.getAttribute("datcalcoloanzianita");		
	}

	if (isRefresh) {
        CODSTATOOCCUPAZ = (String)      serviceRequest.getAttribute("CODSTATOOCCUPAZ");
        datcalcolomesisosp =(String)    serviceRequest.getAttribute("datcalcolomesisosp");
		try {	
			numMesiSospPrec = new BigDecimal  ((String)serviceRequest.getAttribute("NUMMESISOSPPREC"));
			}
		catch (Exception e) {
				numMesiSospPrec=new BigDecimal(0);
			}
        dataAnzDisoc    = (String)      serviceRequest.getAttribute("DATANZIANITADISOC");
        codStatoOccRagg = (String)      serviceRequest.getAttribute("codstatooccupazragg");
        datcalcoloanzianita=StringUtils.getAttributeStrNotNull(serviceRequest, "datcalcoloanzianita");
//		datcalcolomesisosp=StringUtils.getAttributeStrNotNull(serviceRequest, "datcalcolomesisosp");
		mesiAnzPrecInt=StringUtils.getAttributeStrNotNull(serviceRequest, "mesiAnzPrecInt");
		try {	
			mesiAnzPrec = new BigDecimal(mesiAnzPrecInt);
			}
		catch (Exception e) {
				mesiAnzPrec=new BigDecimal(0);
			}
		if  (mesiAnzPrecInt!=null)
		codMonoCalcAnzPrec=StringUtils.getAttributeStrNotNull(serviceRequest, "CODMONOCALCOLOANZIANITAPREC297");
		DATDICHIARAZIONE=StringUtils.getAttributeStrNotNull(serviceRequest, "DATDICHIARAZIONE");
	}
	
    if(numMesiSosp != null) 
      numMesiSospInt = String.valueOf( numMesiSosp.intValue() );
    else
      numMesiSospInt = "0";
      
    if(numMesiSospPrec != null) {
      numMesiSospPrecInt = String.valueOf( numMesiSospPrec.intValue() );
    } else 
        numMesiSospPrecInt = "0";
        
    if(mesiAnz != null)     
      mesiAnzInt = String.valueOf( mesiAnz.intValue() );
    else
      mesiAnzInt = "0";
      
    if(mesiAnzPrec != null){     
      mesiAnzPrecInt     = String.valueOf( mesiAnzPrec.intValue() );
    } else 
        mesiAnzPrecInt = "0";
        
    if (numMesiSospInt!=null && Integer.parseInt(numMesiSospInt)<0) numMesiSospInt = "0"; //null;
    if (numMesiSospPrecInt!=null && Integer.parseInt(numMesiSospPrecInt)<0) numMesiSospPrecInt = "0"; 
    if (mesiAnzInt!=null && Integer.parseInt(mesiAnzInt)<0) mesiAnzInt = "0";//null;
    if (mesiAnzPrecInt!=null && Integer.parseInt(mesiAnzPrecInt)<0) mesiAnzPrecInt = "0";
    //if ((mesiAnzInt!=null) && (numMesiSospInt!=null)) mesiAnzInt = String.valueOf(Integer.parseInt(mesiAnzInt) - Integer.parseInt(numMesiSospInt));
    // movimenti	
    BigDecimal totMesiAnz = new BigDecimal(String.valueOf(Integer.parseInt(mesiAnzPrecInt)+Integer.parseInt(mesiAnzInt)-(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt))));

	//Se devo ancora iniziare il trasferimento faccio alcune elaborazioni
	boolean cpiCoincidenti = false;
	boolean isUserIncompetente = false;
	
	if (!trasferisci && !codCpiUserNotFound) {
		CODCPIUSER =  StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDescrCpiUser.ROWS.ROW.CODCPI");
		DESCRCPIUSER = StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDescrCpiUser.ROWS.ROW.DESCRCPI");
		CODCOMCPIUSER = StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDescrCpiUser.ROWS.ROW.CODCOMCPI");
		CODPROVCPIUSER = StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDescrCpiUser.ROWS.ROW.CODPROVCPI");
		CODREGCPIUSER = StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDescrCpiUser.ROWS.ROW.CODREGCPI");
		
		if(CODMONOTIPOCPI.equalsIgnoreCase("C") && CODCPIUSER.equalsIgnoreCase(codCpiTit)) {
			cpiCoincidenti = true;
		} else if (CODMONOTIPOCPI.equalsIgnoreCase("T")) {
			
			if (CODSTATOOCCUPAZ==null || CODSTATOOCCUPAZ.equalsIgnoreCase("")) {
				CODSTATOOCCUPAZ = "C";
				codStatoOccRagg = "A";
			}
		} else if (!CODMONOTIPOCPI.equalsIgnoreCase("C")) {
			isUserIncompetente = true;
		}

		codCpiTit = CODCPIUSER;
		strCpiTit = DESCRCPIUSER;

		if (!isRefresh) {
			codComdom = "";
			strComdom = "";
			strCapDom = "";
			STRLOCALITADOM = "";
			STRINDIRIZZODOM = "";
			DATTRASFERIMENTO = oggi;
		    DATDICHIARAZIONE = StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDataDID.ROWS.ROW.DATDICHIARAZIONE");
		}
		dataInizio = oggi;
		//Decupero la data della DID se presente
		if (serviceResponse.containsAttribute("M_TraGetDataDID.ROWS.ROW.DATDICHIARAZIONE")) isPresenteDid  = true;
		//Imposto la funzione per il controllo dei codici del comune di comicilio
		controllaCodComInCPI = "controllaCodComInCPI";
		
	} else if (trasferisci && trasferito) {
		//Se ho già trasferito non posso cambiare più nulla
		canModify = false;
	}

	if (!trasferito) {
		SourceBean DBOriginElencoAnag = (SourceBean) serviceResponse.getAttribute("M_TraGetKloElencoAnagTrasf.ROWS.ROW");
		if (DBOriginElencoAnag != null) {
			prgElencoAnag = StringUtils.getAttributeStrNotNull(DBOriginElencoAnag, "PRGELENCOANAGRAFICO");		
			numKloElencoAnag =  (BigDecimal) DBOriginElencoAnag.getAttribute("NUMKLOELENCOANAG");
			if (numKloElencoAnag != null) {
				numKloElencoAnag = numKloElencoAnag.add(new BigDecimal(1));
			}
		} else {
			if (!IntraProvinciale.equals("false")) {
				dataNotFound = true;
			}
		}
	}	
	
	pageDocAssociata =(String)  serviceRequest.getAttribute("pageDocAssociata");
	//Codici dei comuni all'interno del CPI corrente
	Iterator iterCodComCPI = serviceResponse.getAttributeAsVector("M_TraGetListComPerCPI.ROWS.ROW").iterator();
	
	//Messaggio da visualizzare in fase di apertura della popup
	String message = "";
	if (dataNotFound) {
		message = "Impossibile recuperare le informazioni del lavoratore. Controllare ";
	} else if (dataOccNotFound && !IntraProvinciale.equals("false")) {
		message = "Impossibile recuperare le informazioni sullo stato occupazionale del lavoratore.";
	} else if (isUserIncompetente) {
		message = "Il CPI dell\\\'utente non è competente per il lavoratore, impossibile effettuare il trasferimento.";
	} else if (cpiCoincidenti) {
		message = "CPI dell\\\'utente coincidente con quello del lavoratore, impossibile trasferire.";
	} else if (codCpiUserNotFound) {
		message = "CPI dell\\\'utente non trovato, impossibile trasferire il lavoratore.";
	}

    //Gestione Privacy in fase di inserimento DID
    String dataPrivacy = "";
    String flgautoriz = "";
    SourceBean privacySb = null;
    //Controllo presenza obbligo scolastico
    SourceBean obbligoScol = null;
    String flgObbligo = "";
    String dataNascita = "";
    int eta = 0;

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
      InfoLavoratore infoLav = new InfoLavoratore(new BigDecimal(CDNLAVORATORE));
      dataNascita = infoLav.getDataNasc();
      eta =  DateUtils.getEta(dataNascita);


    BigDecimal codCartaIdentita = new BigDecimal(0);
    String codCartaIdentitaStr = "";
    String datInizioDocIdent = "";
    String datScadDocIdent = "";

    boolean hasCartaIdentita = serviceResponse.containsAttribute("M_GETCARTAIDENTITAVALIDA.ROWS.ROW.PRGDOCUMENTO");    
    Vector codCartaIdent = serviceResponse.getAttributeAsVector("M_GETCARTAIDENTITAVALIDA.ROWS.ROW");
    if (hasCartaIdentita){
      SourceBean cI = (SourceBean)codCartaIdent.get(0);
      codCartaIdentita = (BigDecimal)cI.getAttribute("PRGDOCUMENTO");
//    datInizioDocIdent = StringUtils.getAttributeStrNotNull(cI, "DatInizio");
//    datScadDocIdent = StringUtils.getAttributeStrNotNull(cI, "DatFine");
    }
    if (!codCartaIdentita.equals(new BigDecimal(0)))
      codCartaIdentitaStr = codCartaIdentita.toString();
    String target = "trasfPresaAtto";

  	boolean viewPrivacyDocIdent = (isInterRegione || isInterProvincia) && !tipoTrasferimento.equals("ERR");
  
  	if (IntraProvinciale.equals("true")) {
  		String coopAbilitata = System.getProperty("cooperazione.enabled");
  		if (coopAbilitata != null && coopAbilitata.equals("true")) {
  			coopAttiva = true;
  		}
	}
  	String dataNormativaPrec297 = "";
    SourceBean sbConfigDataNormativa = (SourceBean) serviceResponse.getAttribute("M_CONFIG_DATA_NORMATIVA_297.rows.row");
	if (sbConfigDataNormativa != null && sbConfigDataNormativa.containsAttribute("strvalore")) {
		dataNormativaPrec297 = sbConfigDataNormativa.getAttribute("strvalore").toString();
	}
	else {
		dataNormativaPrec297 = EventoAmministrativo.DATA_NORMATIVA_DEFAULT;
	}

	

    boolean isConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma"); 
    //boolean isButtonGestioneConsenso = false;
    boolean isConsensoAttivo = false;
    String ipOperatore = null;

    if(isConsenso){
	  isConsensoAttivo= serviceResponse.containsAttribute("M_VerificaAmConsensoFirma.consensoFirmaAttivo");
	  if(isConsensoAttivo){
		 ipOperatore = serviceResponse.getAttribute("M_VerificaAmConsensoFirma.ipOperatore").toString();
	  }
}
  	
//   	String ipOperatore = "";
//   	boolean isConsenso = false;
//   	boolean isConsensoAttivo = false;
    
  	
  	
  	//Controllo di eseguibilità "Gestione Consenso"
    /*    ProfileDataFilter filterConsenso = new ProfileDataFilter(user, "HomeConsensoPage");
  	  isButtonGestioneConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma.viewGestioneConsensoBtn") && filterConsenso.canView();*/
/*
    boolean isConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma"); 
//    boolean isButtonGestioneConsenso = false;
    boolean isConsensoAttivo = false;
    
    if(isConsenso){
   	  isConsensoAttivo= serviceResponse.containsAttribute("M_VerificaAmConsensoFirma.consensoFirmaAttivo");
   	  if(isConsensoAttivo){
		 ipOperatore = serviceResponse.getAttribute("M_VerificaAmConsensoFirma.ipOperatore").toString();
	  }
    }
	*/
%>

<html>
  <head>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    
    <title>Trasferimento Competenza Amministrativa</title>
	<%@ include file="../global/fieldChanged.inc" %>
    <%@ include file="../global/Function_CommonRicercaComune.inc" %>
	<%@ include file="../anag/Function_CommonRicercaCPI.inc" %>
	<%@ include file="../global/confrontaData.inc" %>
	<%@ include file="../documenti/_apriGestioneDoc.inc"%>
	<script language="Javascript" src="../../js/docAssocia.js"></script>
    <script language="Javascript">
    <!--
    function apriGestioneConsenso( ) {
  	  var urlpage="AdapterHTTP?";
  	    urlpage+="PAGE=HomeConsensoPage&";
  	    urlpage+="CDNFUNZIONE=<%=_funzione%>&";
  	    urlpage+="cdnLavoratore=<%=CDNLAVORATORE%>&";
//  	    setOpenerWindowLocation(urlpage);
		self.close();
  	    window.open(urlpage, "main");
  	}
    
	function codCPIUpperCase(inputName){
	
	  var ctrlObj = eval("document.forms[0]." + inputName);
	  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();")
		return true;
	}
	
	//Aggiorna la pagina sottostante alla pop-up, ma solo se contiene la funzione aggiorna
	function updateMain() {
		if (window.opener != null && window.opener.aggiorna != null) {
			window.opener.aggiorna();
		}
	}
	
	//segnala se la data è precedente o successiva a quella odierna,
	//non permette trasferimenti in date future
	function segnalaData (dateFieldName) {
		check = compareDate(new String('<%=oggi%>'), new String(document.Frm1.DATTRASFERIMENTO.value));
		if (check == -1) {
			alert("Impossibile effettuare trasferimenti in data futura");
			return false;
		} else if (check == 1) {
			return confirm("La data del trasferimento è precedente alla data odierna, procedere comunque?");
		} else {
			if (document.Frm1.isInterProvincia.value == 'SI' || document.Frm1.isInterRegione.value == 'SI'){	
				if (document.Frm1.codStatoOccupazRagg.value == 'D' || document.Frm1.codStatoOccupazRagg.value == 'I') {
					return true;
				} else {
					document.Frm1.datDichiarazione.value=document.Frm1.DATTRASFERIMENTO.value;
					return true;
				}
			} else {
				document.Frm1.datDichiarazione.value=document.Frm1.DATTRASFERIMENTO.value;
				return true;
			}
		}
	}

	function controllaDate() {
		var ret=true;
        var codiceRaggruppamento = document.Frm1.codStatoOccupazRagg.value;

		if (document.Frm1.isInterProvincia.value == 'SI' || document.Frm1.isInterRegione.value == 'SI') {		
			if (document.Frm1.codStatoOccupazRagg.value == 'D' || document.Frm1.codStatoOccupazRagg.value == 'I') {
				if (document.Frm1.datAnzianitaDisoc.value=="") {
					alert("Indicare l'anzianità di disoccupazione");
					ret= false;
				}

				if (ret && document.Frm1.datcalcolomesisosp.value=="") {
					alert("Indicare la data per il calcolo dei mesi di sospensione");
					ret= false;
				}

				if (ret && document.Frm1.datcalcoloanzianita.value=="") {
					alert("Indicare la data per il calcolo dei mesi di anzianità");
					ret= false;
				}

				if (ret && document.Frm1.datDichiarazione.value=="") {
					alert("Indicare la data DID");
					ret= false;
				}
				if (ret) {
		    		check = compareDate(new String("<%=dataNormativaPrec297%>"), new String(document.Frm1.datDichiarazione.value));
					if (check == 1) {
						alert("La data della DID non può precedere il <%=dataNormativaPrec297%>" );
						ret= false;
					}
				}
	
				if (ret) {
		    		check = compareDate(new String(document.Frm1.datDichiarazione.value), new String(document.Frm1.datAnzianitaDisoc.value));
					if (check == -1) {
						alert("La data DID non può precedere la data\ndi anzianità disoccupazione");
						ret= false;
					}
				}
				if (ret) {
		    		check = compareDate(new String("<%=oggi%>"), new String(document.Frm1.datDichiarazione.value));
					if (check == -1) {
						alert("Non sono ammesse date future")
						ret= false;
					}
				}
	
				if (ret) {
		    		check = compareDate(new String(document.Frm1.datcalcolomesisosp.value), new String(document.Frm1.datAnzianitaDisoc.value));
					if (check == -1) {
						alert("La data per il calcolo dei mesi di sospensione\nnon può precedere la data\ndi anzianità disoccupazione");
						ret= false;
					}
				}
				if (ret) {
		    		check = compareDate(new String("<%=oggi%>"), new String(document.Frm1.datcalcolomesisosp.value));
					if (check == -1) {
						alert("Non sono ammesse date future")
						ret= false;
					}
				}
				if (ret) {
		    		check = compareDate(new String(document.Frm1.datcalcoloanzianita.value), new String(document.Frm1.datAnzianitaDisoc.value));
					if (check == -1) {
						alert("La data per il calcolo dei mesi di anzianità\nnon può precedere la data\ndi anzianità disoccupazione");
						ret= false;
					}
				}
				if (ret) {
		    		check = compareDate(new String("<%=oggi%>"), new String(document.Frm1.datcalcoloanzianita.value));
					if (check == -1) {
						alert("Non sono ammesse date future")
						ret= false;
					}
				}
				if (ret && (<%=eta%> > 60)){
		            ret = confirm("Il lavoratore ha più di 60 anni.\nControllare la situazione pensionistica.\nSi desidera continuare?");
				}			
			} 
			return ret;
		} else {
	  	  	return true;
	  	}
 	}
	
	
	function aggiornaDataDichiarazione() {
		if (document.Frm1.datDichiarazione.value=="") {
			document.Frm1.datDichiarazione.value=document.Frm1.datAnzianitaDisoc.value;
		}
	}

	function aggiornaDataSosp() {
		if (document.Frm1.datcalcolomesisosp.value=="") {
			document.Frm1.datcalcolomesisosp.value=document.Frm1.DATTRASFERIMENTO.value;
		}
		if (document.Frm1.datcalcoloanzianita.value=="") {
			document.Frm1.datcalcoloanzianita.value=document.Frm1.DATTRASFERIMENTO.value;
		}
		return true;
	}



	//Controlla che il codCom selezionato sia all'interno del CPI di provenienza
	var codComList = new Array(<%
    		boolean firstElement = true;
    		while (iterCodComCPI.hasNext()) {
    			if (!firstElement) {out.print(", ");}
    			else {firstElement = false;}
    			out.print("\"" + (String) ((SourceBean) iterCodComCPI.next()).getAttribute("CODCOM") + "\"");
    		}
    %>);
    
	function controllaCodComInCPI(tagName) {
		codCom = document.Frm1.codComdom.value;
		found = false;
		for (var i = 0; ((i < codComList.length) && (!found)); i++) {
			if (String(codComList[i]).toUpperCase() == String(codCom).toUpperCase()) {found = true;}
		}
		if (!found) {
			alert("Occorre indicare un comune di domicilio all'interno di quelli gestiti dal proprio CPI");
			return false;
		} else return true;
		
	}

	function apriPrivacy(cdnLav,cdnFunzione){
	    //Per evidenziare se effettuare il refresh della pagina dopo aver chiuso la pagina della privacy
	    <% if (canModify) { %>
	      window.open("AdapterHTTP?PAGE=PrivacyDettaglioPage&CDNLAVORATORE=" + cdnLav + "&CDNFUNZIONE=" + cdnFunzione + "&FROM_TRASF=S" + "&PAGEDOCASSOCIATA=<%=pageDocAssociata%>" ,'','toolbar=0,scrollbars=1,width=700, height=500, left=10, top=10');
	    <%}else{%>
	      window.open("AdapterHTTP?PAGE=PrivacyDettaglioPage&CDNLAVORATORE=" + cdnLav + "&CDNFUNZIONE=" + cdnFunzione + "&PAGEDOCASSOCIATA=<%=pageDocAssociata%>",'','toolbar=0,scrollbars=1,width=700, height=500, left=10, top=10');
	    <%}%>      
	} 

	function aggiornaRaggruppamento() {
		if (document.Frm1.CODSTATOOCCUPAZ.selectedIndex > 0) {
			document.Frm1.codStatoOccupazRagg.value = dett_ragg[document.Frm1.CODSTATOOCCUPAZ.selectedIndex-1];
		} 
		else {
			document.Frm1.codStatoOccupazRagg.value = "";
		}
		if (document.Frm1.codStatoOccupazRagg.value == "I" || document.Frm1.codStatoOccupazRagg.value == "D")
			mostra("divAnzianita");
		else
			nascondi("divAnzianita");
	    return true;
	}

	function inizializzaDiv(){
	    <% if (codStatoOccRagg.equalsIgnoreCase("I") || codStatoOccRagg.equalsIgnoreCase("D")) { %>
			mostra("divAnzianita");
		<% } else  { %>
			nascondi("divAnzianita");
		<% } %>
	    return true;
	}

function controllaPresenzaPrivacy(){
  var isPresentePrivacyValida = false;
  var datPrivacyjs = '<%=dataPrivacy%>';
  var flgPresaVisione = '<%=flgautoriz%>';
  var dtPrivacy = 0;
  var dtDID = 0;
  var codiceRaggruppamento = document.Frm1.codStatoOccupazRagg.value;
  
  <% if (canModify && !isPresenteDid) { %>
  		if (document.Frm1.isInterProvincia.value == 'SI' || document.Frm1.isInterRegione.value == 'SI') {				
	     	if (codiceRaggruppamento!=null && codiceRaggruppamento!="" && (codiceRaggruppamento=='D' || codiceRaggruppamento =='I')) {
    	 		if(datPrivacyjs != ""){
	       			dtPrivacy = datPrivacyjs.substr(6,4) + datPrivacyjs.substr(3,2) + datPrivacyjs.substr(0,2);
	     		}
         		var tmp = document.Frm1.datDichiarazione.value;
         		var tmp2 = document.Frm1.DATTRASFERIMENTO.value;
	     		dtDID = tmp.substr(6,4) + tmp.substr(3,2) + tmp.substr(0,2);
	     		dtTrasf = tmp2.substr(6,4) + tmp2.substr(3,2) + tmp2.substr(0,2);
 	     		if((dtPrivacy != 0) && (dtDID != 0)){
	       			if((dtPrivacy >= dtDID) && (flgPresaVisione == 'S') && (dtPrivacy <= dtTrasf)) isPresentePrivacyValida = true;
	     		}
	 		} else {
         		isPresentePrivacyValida = true;
		 	}
		 } else {
         	isPresentePrivacyValida = true;
		 }
  <%} else {%>
          isPresentePrivacyValida = true;
  <%  } %>
  if(!isPresentePrivacyValida){
    alert("Non è stata presa visione della informativa\n"+
          "relativa al trattamento dei dati personali\n"+
          "tra la data DID e la data di trasferimento");
  }
  return isPresentePrivacyValida;
}

function controllaPresenzaDocValido(){
  var isPresenteDocValido = false;

  var codiceRaggruppamento = document.Frm1.codStatoOccupazRagg.value;
  <% if (canModify && !isPresenteDid) { %>
			if (document.Frm1.isInterProvincia.value == 'SI' || document.Frm1.isInterRegione.value == 'SI') {
	     		if (codiceRaggruppamento!=null && codiceRaggruppamento!="" && (codiceRaggruppamento=='D' || codiceRaggruppamento =='I')) {
   			 		isPresenteDocValido = <%=hasCartaIdentita%>;
		 		} else {
	         		isPresenteDocValido = true;
		 		}
		 	} else {
	         	isPresenteDocValido = true;
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
  var codiceRaggruppamento = document.Frm1.codStatoOccupazRagg.value;
	
   	<% if (canModify && !isPresenteDid) { %>
			if (document.Frm1.isInterProvincia.value == 'SI' || document.Frm1.isInterRegione.value == 'SI') {
     			<% if((eta > 15) && (eta < 30)){%>  
          			if (codiceRaggruppamento!=null && codiceRaggruppamento!="" && (codiceRaggruppamento=='D' || codiceRaggruppamento =='I') && flgObbligojs != 'S'){
            			if (confirm("Il lavoratore per cui si sta inserendo la DID non ha assolto l'obbligo scolastico.\nSi desidera proseguire con l'operazione?")){
              				isPresenteObblScol = true;
            			}
          			} else isPresenteObblScol = true;
   				<%} else {%>
            		isPresenteObblScol = true;
        		<%}%> 
      		} else isPresenteObblScol = true;
   	<% } else { %>
          isPresenteObblScol = true;
  	<% } %>

  return isPresenteObblScol;
}

function mostra(id)
{ 
  var div = document.getElementById(id);
  div.style.display="";
}

function nascondi(id)
{ 
  var div = document.getElementById(id);
  div.style.display="none";
}


	function controllaDocumentoIdentita(presente) {
	    if (!presente)
	     alert("Stampa non possibile: il documento di riconoscimento del lavoratore non e' presente");    
	}

    var dett_cod=new Array();
    var dett_ragg=new Array();
    var dett_des=new Array();

 function caricaStatiOccupaz() {
<%  
    Vector statiOccupaz = null;
    SourceBean row_statoOccupaz;
    
    statiOccupaz = serviceResponse.getAttributeAsVector("M_GETDESTATOOCC.ROWS.ROW");

    for(int i=0; i<statiOccupaz.size(); i++)  { 
      row_statoOccupaz = (SourceBean) statiOccupaz.elementAt(i);
      out.print("dett_ragg["+i+"]=\""+ row_statoOccupaz.getAttribute("CODSTATOOCCUPAZRAGG").toString()+"\";\n");
      out.print("dett_cod["+i+"]=\""+ row_statoOccupaz.getAttribute("CODICE").toString()+"\";\n");
      out.print("dett_des["+i+"]=\""+ row_statoOccupaz.getAttribute("DESCRIZIONE").toString()+"\";\n");
    }
%>
	return true;
  }	

	function apriDocIdentificazione(){
	  docDettaglioIdentif('<%=CDNLAVORATORE%>', '<%=codCartaIdentita%>',
						  'AnagDettaglioPageIndirizzi','<%=_funzione%>', '<%=target%>', false);
	  
	}

	<%=(!message.equals("") ? "alert('" + message + "');window.close();" : "")%>

    function controllaPresenzaCpiDiProvenienza() {
    	if (document.Frm1.CODCPIORIG.value == "") {
    		alert("Il campo CPI di provenienza è obbligatorio"); 
			return false;

  		} else return true;
    }
   
    function vuoiProseguire() {
     if (confirm("Si sta cercando di Trasferire un lavoratore.\nVuoi continuare?")) {
          return true;
     }
     else {return false;}
    }
	-->
    </SCRIPT>
  </head>
<%-- in questa pagina l'aggiornamento della pagina degli indirizzi e' necessaria solo per avere come ultima chiamata
	una get. Verra' poi aggiornata dal frame di stampa --%>
  <body class="gestione" onload="caricaStatiOccupaz() && aggiornaDataSosp() && rinfresca();<%=(!trasferito ? "updateMain();" : "")%>" >
<%
	infCorrentiLav.show(out); 
%>
	<font color="red"><af:showErrors/></font>
	<font color="green">
		<af:showMessages prefix="TraIntraProvinciale"/>
	</font>
    <center>
  	  <p class="titolo">Dati Fissati nel Trasferimento di Competenza Amministrativa</p>
  	  	<%if(isConsenso){%>
	   	  <af:showMessages prefix="M_VerificaAmConsensoFirma"/>
    	<%}%>
      <af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="ctrlPresenzaObblScol() && controllaDate() && controllaPresenzaPrivacy() && controllaPresenzaDocValido() && vuoiProseguire()">
        <%out.print(htmlStreamTop);%>
        <table class="main"> 
			<%@include file="TrasferimentiCommon.inc" %>
        </table>
        <%out.print(htmlStreamBottom);%>
        <input type="hidden" name="PAGE" value="REPORTFRAMEBACKPAGE"/>
        <input type="hidden" name="CDNLAVORATORE" value="<%=CDNLAVORATORE%>"/>
        <input type="hidden" name="strCodiceFiscale" value="<%=strCodiceFiscale%>"/>
        <input type="hidden" name="strCognome" value="<%=strCognome%>"/>
        <input type="hidden" name="strNome" value="<%=strNome%>"/>
        <input type="hidden" name="datNasc" value="<%=datNasc%>"/>
        <input type="hidden" name="codComNas" value="<%=codComNas%>"/>
        <input type="hidden" name="strComNas" value="<%=strComNas%>"/>                
        <input type="hidden" name="STRCODICEFISCALEOLD" value="<%=STRCODICEFISCALEOLD%>"/>              
        <input type="hidden" name="NUMKLOLAVSTORIAINF" value="<%=NUMKLOLAVSTORIAINF%>"/>
        <input type="hidden" name="PRGLAVSTORIAINF" value="<%=PRGLAVSTORIAINF%>"/>       
        <input type="hidden" name="NUMKLOLAVORATORE" value="<%=String.valueOf(numKloLavoratore)%>"/>
        <input type="hidden" name="PRGELENCOANAGRAFICO" value="<%=prgElencoAnag%>"/>
        <input type="hidden" name="NUMKLOELENCOANAG" value="<%=String.valueOf(numKloElencoAnag)%>"/>
        <input type="hidden" name="PRGPATTOLAVORATORE" value="<%=prgPattoLav%>"/>
        <input type="hidden" name="NUMKLOPATTOLAVORATORE" value="<%=String.valueOf(numKloPattoLav)%>"/>                           
        <input type="hidden" name="trasferisci" value="true"/>
        <input type="hidden" name="firmaGrafometrica" value="<%=String.valueOf(isConsensoAttivo)%>"/>
        <input type="hidden" name="ipOperatore" value="<%=ipOperatore%>"/>
        
        <!-- dati per la protocollazione automatica -->
        <input type="hidden" name="ACTION_REDIRECT" value="RPT_STAMPA_TRASFERIMENTO" />
        <input type="hidden" name="QUERY_STRING" value="PAGE=TrasferimentoPage&amp;CDNLAVORATORE=<%=CDNLAVORATORE%>&amp;CDNFUNZIONE=<%=_funzione%>&amp;trasferisci=true&amp;pageDocAssociata=<%=pageDocAssociata%>&amp;CODCPIORIG=<%=codCpiOrig%>&amp;CODCPIUTENTE=<%=codCpiUtente_Req%>"/>
        <input type="hidden" name="REFRESH_MAIN" value="true"/>
		<% if ((isInterProvincia) && !tipoTrasferimento.equals("ERR")) { %>
	        <input type="hidden" name="isInterProvincia" value="SI"/>
	        <input type="hidden" name="isInterRegione" value="NO"/>
		<% } %>
		<% if ((isInterRegione) && !tipoTrasferimento.equals("ERR")) { %>
	        <input type="hidden" name="isInterRegione" value="SI"/>
	        <input type="hidden" name="isInterProvincia" value="NO"/>
		<% } %>
		<% if (tipoTrasferimento.equals("ERR")) { %>
	        <input type="hidden" name="isInterRegione" value="ER"/>
	        <input type="hidden" name="isInterProvincia" value="ER"/>
		<% } %>
		<% if (IntraProvinciale.equals("true") || (!isInterProvincia && !isInterRegione)) { %>
			<input type="hidden" name="isInterRegione" value="NO"/>
	        <input type="hidden" name="isInterProvincia" value="NO"/>
	    <% } %>   
        <%
        	// gestione della stampa e della protocollazione senza passare per la relativa maschera
        	/* ripresi dal modulo della maschera di stampa ma impostati come parametri fissi
        	serviceResponse.getAttribute("M_GETTIPODOC.ROWS.ROW.RIFERIMENTO");
        	serviceResponse.getAttribute("M_GETPROTOCOLLAZIONE.ROWS.ROW.FLGPROTOCOLLOAUT");
        	serviceResponse.getAttribute("M_GETPROTOCOLLAZIONE.ROWS.ROW.NUMKLOPROTOCOLLO");
        	*/
        	String annoProt = SourceBeanUtils.getAttrStrNotNull(serviceResponse,"M_GETPROTOCOLLAZIONE.ROWS.ROW.NUMANNOPROT");
        	String numProt  = SourceBeanUtils.getAttrStrNotNull(serviceResponse,"M_GETPROTOCOLLAZIONE.ROWS.ROW.NUMPROTOCOLLO");
        	String docInOut = SourceBeanUtils.getAttrStrNotNull(serviceResponse,"M_GETTIPODOC.ROWS.ROW.STRIO");
        	//
        	Calendar now = Calendar.getInstance();    
			String giorno = Integer.toString(now.get(Calendar.DATE));    if(giorno.length()<=1) { giorno = "0" + giorno; }
			String mese   = Integer.toString(now.get(Calendar.MONTH)+1); if(mese.length()<=1)   { mese   = "0" + mese; }
			String anno   = Integer.toString(now.get(Calendar.YEAR));    if(anno.length()<=1)   { anno   = "0" + anno; }
			String dataProt = giorno +"/"+ mese +"/"+ anno; 			
			String ora = Integer.toString(now.get(Calendar.HOUR_OF_DAY)); if(ora.length()<=1)    { ora    = "0" + ora; }
			String minuti = Integer.toString(now.get(Calendar.MINUTE));   if(minuti.length()<=1) { minuti = "0" + minuti; }
			String oraProt = ora + ":" + minuti;
			String dataOraProt = dataProt + " "  + oraProt;
        %>
        <input type="hidden" name="annoProt" value="<%=annoProt%>"/>
        <input type="hidden" name="apri" value="true"/>
        <input type="hidden" name="asAttachment" value="false"/>
        <input type="hidden" name="dataOraProt" value="<%= dataOraProt%>"/>
        <input type="hidden" name="docInOut" value="<%=docInOut%>"/>
        <input type="hidden" name="numProt" value="<%=numProt%>"/>                                               
        <input type="hidden" name="protAutomatica" value="S"/>                    
        <input type="hidden" name="rptAction" value="RPT_STAMPA_TRASFERIMENTO"/>                                                                    
        <input type="hidden" name="salvaDB" value="true"/>           
        <input type="hidden" name="tipoDoc" value="TRCPI"/>                           
        <input type="hidden" name="tipoFile" value="PDF"/>
        <input type="hidden" name="pagina" value="<%=pageDocAssociata%>"/>
        <input type="hidden" name="pageDocAssociata" value="<%=pageDocAssociata%>"/> 

    <table>
		<tr>
		  <td>	  	
		  	<input class="pulsante" type="button" style="display:<%=((viewPrivacyDocIdent)?"":"none")%>" name="privacy" value="Privacy" onClick="javascript:apriPrivacy('<%=CDNLAVORATORE%>', '<%=_funzione%>');">
		  </td>
		  <td>
		    <input class="pulsante" type="button" style="display:<%=((viewPrivacyDocIdent)?"":"none")%>" name="docIdent" value="Documento di identificazione" onClick="javascript:apriDocIdentificazione();">   
		  </td>
		</tr>
	</table>
<% if (!trasferito) {%>
			<center>
			<% if (coopAttiva) { %>
			<input type="submit" name="avanti_coop" class="pulsante" value="Trasferisci Competenza"/>
			<% } else  { %>
            <input type="submit" name="avanti" class="pulsante" value="Trasferisci Competenza"/>
            <% } %>
			</center>	    
<%} else { %>
	<input type="button" name="chiudi" class="pulsante" value="Chiudi" onclick="window.close()"/>
<%}if (trasferito ) {
		if (statoTrasf==STAMPA_TRASF_DA_PROTOCOLLARE) {	
%>
	        <input type="SUBMIT"  class="pulsante" value="Stampa modulo trasferimento" name="stampa_report"/> 
	    <%} else if (statoTrasf==STAMPA_RICH_DOC_LAV_DA_PROTOCOLLARE){%>
	    	<input type="button"  class="pulsante" value="Stampa richiesta doc. trasf." onclick="apriGestioneDoc('RPT_STAMPA_RICHIESTA_DOC',
  										'&cdnLavoratore=<%=CDNLAVORATORE%>&pagina=<%=pageDocAssociata%>&isPresaAtto=false&pageDocAssociata=<%=pageDocAssociata%>',
										'TRDOC','REPORTFRAMEMAINRELOADPAGE', true);"/> 
	    <%}%>
<%}%>
        	<input type="hidden" name="CODMONOTIPOCPI" value="C" />
        <% if (tipoTrasferimento.equalsIgnoreCase("TP")) { %>
	        <input type="hidden" name="CODMONOTIPOORIG" value="I" />
	    <% } else { 
	         if (isInterRegione) { %>
		        <input type="hidden" name="CODMONOTIPOORIG" value="F" />
		    <% } else { %>
		        <input type="hidden" name="CODMONOTIPOORIG" value="R" />
		    <% } %> 
	    <% } %>      
	    </af:form>      
	</center> 
  </body>
</html>  



