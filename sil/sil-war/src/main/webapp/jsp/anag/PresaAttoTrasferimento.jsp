<!-- Pagina dei trasferimenti amministrativi -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%!  
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("it.eng.sil._jsp.MobilitaImporta.jsp");
%>


<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  
                  com.engiweb.framework.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.Values,
                  
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 
	boolean coopAbilitata= false;
    String coopAttiva = System.getProperty("cooperazione.enabled");
    if (coopAttiva != null && coopAttiva.equals("true")) {
  		coopAbilitata = true;
    }
  
	String queryString = null;
	String tipoTrasferimento="A"; //presa d'atto
	SourceBean sbError = (SourceBean)serviceResponse.getAttribute("M_PresaAttoTrasferimento");
  	//se ci sono stati degli errori durante il trasferimento strResult = "false"
  	String strResult = (sbError != null && sbError.containsAttribute("trasferito"))?sbError.getAttribute("trasferito").toString():"";
	String stampato = null;
	//Controllo se in sessione ho il CodCpi di destinazione (cdnTipoGruppo dell'utente == 1)
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	String codCpiUser = user.getCodRif();
	boolean codCpiUserNotFound = false;
	if(cdnTipoGruppo != 1) {
		codCpiUserNotFound = true;
	}
	
	String CDNLAVORATORE = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNLAVORATORE");
	
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
	boolean trasferito  = false;
	String strTrasferisci =  StringUtils.getAttributeStrNotNull(serviceRequest, "trasferisci");
	if ("true".equalsIgnoreCase(strTrasferisci)) {
		trasferisci = true;
		String strTrasferito = StringUtils.getAttributeStrNotNull(serviceResponse,"M_PresaAttoTrasferimento.trasferito");
		stampato = Utils.notNull(serviceRequest.getAttribute("stampato"));
		if ("true".equalsIgnoreCase(strTrasferito) || stampato.equals("true")) {
			trasferito = true;
			canModify = false;
		}
	}
	
	//Variabili
	String PRGLAVSTORIAINF = "";
	String dataInizio = ""; 
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
	String STRCODICEFISCALEOLD = ""; 
	String CDNUTMODSCHEDAANAGPROF = ""; 
	String DTMMODSCHEDAANAGPROF = ""; 
	String CODMONOTIPOORIG = ""; 
	String DATTRASFERIMENTO = ""; 
	String CODCPIORIG = ""; 
	String DESCRCPIORIG = ""; 	
	String DATDICHIARAZIONE = ""; 
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
	String datInizio= ""; 
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
		_logger.debug( "PresaAttoTrasferimento.jsp: (dataOrigin != null && !codCpiUserNotFound)");
		
		PRGLAVSTORIAINF = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGLAVSTORIAINF");
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
		STRCODICEFISCALEOLD =  StringUtils.getAttributeStrNotNull(dataOrigin, "STRCODICEFISCALEOLD");
		CDNUTMODSCHEDAANAGPROF =  StringUtils.getAttributeStrNotNull(dataOrigin, "CDNUTMODSCHEDAANAGPROF");
		DTMMODSCHEDAANAGPROF =  StringUtils.getAttributeStrNotNull(dataOrigin, "DTMMODSCHEDAANAGPROF");
		CODMONOTIPOORIG =  StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOORIG");
		DATTRASFERIMENTO =   StringUtils.getAttributeStrNotNull(dataOrigin, "DATTRASFERIMENTO");
		DATDICHIARAZIONE =   StringUtils.getAttributeStrNotNull(dataOrigin, "DATDICHIARAZIONE");		
		CODCPIORIG =  StringUtils.getAttributeStrNotNull(dataOrigin, "CODCPIORIG");
		DESCRCPIORIG =  StringUtils.getAttributeStrNotNull(dataOrigin, "DESCRCPIORIG");	
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
		NUMKLOLAVSTORIAINF =  StringUtils.getAttributeStrNotNull(dataOrigin, "NUMKLOLAVSTORIAINF");
		// 16/08/2006 savino: presa atto in cooperazione
		stampato = String.valueOf("S".equals(dataOrigin.getAttribute("flgStampaDoc")));
		if (!NUMKLOLAVSTORIAINF.equals("")) {NUMKLOLAVSTORIAINF = String.valueOf(Integer.parseInt(NUMKLOLAVSTORIAINF) + 1);}
	}
	
	//Tranne nel caso di trasferimento andato bene questi dati li prendo dal DB e li incremento di uno
	if (!trasferito) {
		_logger.debug(  "PresaAttoTrasferimento.jsp: (!trasferito)");
		SourceBean DBOriginLav = (SourceBean) serviceResponse.getAttribute("M_TraGetDettTrasf.ROWS.ROW");	
		SourceBean DBOriginElencoAnag = (SourceBean) serviceResponse.getAttribute("M_TraGetKloElencoAnagTrasf.ROWS.ROW");
		SourceBean DBOriginPatto = (SourceBean) serviceResponse.getAttribute("M_TraGetKloPattoTrasf.ROWS.ROW");	
		if (DBOriginLav != null) {
			numKloLavoratore = (BigDecimal) DBOriginLav.getAttribute("NUMKLOLAVORATORE");
			if (numKloLavoratore != null) {
				numKloLavoratore = numKloLavoratore.add(new BigDecimal(1));
			}
		} else dataNotFound = true;
		if (DBOriginElencoAnag != null) {
			prgElencoAnag = StringUtils.getAttributeStrNotNull(DBOriginElencoAnag, "PRGELENCOANAGRAFICO");		
			numKloElencoAnag =  (BigDecimal) DBOriginElencoAnag.getAttribute("NUMKLOELENCOANAG");
			if (numKloElencoAnag != null) {
				numKloElencoAnag = numKloElencoAnag.add(new BigDecimal(1));
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
		_logger.debug(  "PresaAttoTrasferimento.jsp: (dataOriginStatoOccupazionale != null && !codCpiUserNotFound && !(trasferisci && !trasferito))");
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
	
	String codRegioneCpiOrig = StringUtils.getAttributeStrNotNull(serviceResponse, "M_SELECTREGIONECPIORIG.ROWS.ROW.CODREGIONE");
	String codRegioneCpiUser = StringUtils.getAttributeStrNotNull(serviceResponse, "M_SELECTREGIONECPIUSER.ROWS.ROW.CODREGIONE");
	
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
	boolean isInterProvincia = false;
	boolean isInterRegione = false;
	boolean isPresaAttoInterProvincia = false;
	boolean isPresaAttoInterRegione = false;
	boolean isUserIncompetente = false;
	boolean esterno = false;
	boolean funzioneDaImplementare = false;
	if (!trasferisci && !codCpiUserNotFound) {
		_logger.debug(  "(!trasferisci && !codCpiUserNotFound)");	
		CODCPIUSER =  StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDescrCpiUser.ROWS.ROW.CODCPI");
		DESCRCPIUSER = StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDescrCpiUser.ROWS.ROW.DESCRCPI");
		CODCOMCPIUSER = StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDescrCpiUser.ROWS.ROW.CODCOMCPI");
		CODPROVCPIUSER = StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDescrCpiUser.ROWS.ROW.CODPROVCPI");

		if(CODMONOTIPOCPI.equalsIgnoreCase("C") && CODCPIUSER.equalsIgnoreCase(codCpiTit)) {
			cpiCoincidenti = true;
		} else if (CODMONOTIPOCPI.equalsIgnoreCase("T")) {
			funzioneDaImplementare = true;
		} else if (!CODMONOTIPOCPI.equalsIgnoreCase("C")) {
			isUserIncompetente = true;
		}
		if (codRegioneCpiUser.equalsIgnoreCase(codRegioneCpiOrig))
			isPresaAttoInterProvincia = true;		
		else
			isPresaAttoInterRegione = true;			
		CODCPIORIG = codCpiTit;
		DESCRCPIORIG = strCpiTit;
		codCpiTit = CODCPIUSER;
		strCpiTit = DESCRCPIUSER;
		
		codComdom = "";
		strComdom = "";
		strCapDom = "";
		STRLOCALITADOM = "";
		STRINDIRIZZODOM = "";
		
		DATTRASFERIMENTO = oggi;
		dataInizio = oggi;
		
		//Decupero la data della DID se presente
		DATDICHIARAZIONE = StringUtils.getAttributeStrNotNull(serviceResponse, "M_TraGetDataDID.ROWS.ROW.DATDICHIARAZIONE");
		
		//Imposto la funzione per il controllo dei codici del comune di comicilio
		controllaCodComInCPI = "controllaCodComInCPI";
		
	} else if (trasferisci && trasferito) {
		//Se ho già trasferito non posso cambiare più nulla
		canModify = false;
	}
	String 	pageDocAssociata =(String)  serviceRequest.getAttribute("pageDocAssociata");
	//Codici dei comuni all'interno del CPI corrente
	Iterator iterCodComCPI = serviceResponse.getAttributeAsVector("M_PresaAttoListComPerPolo.ROWS.ROW").iterator();

	//Imposto la funzione per il controllo dei codici del comune di comicilio
	controllaCodComInCPI = "controllaCodComInCPI";
	
	// 11/08/2006 savino: presa atto in cooperazione
	// controllo se devo utilizzare il cpi presente in una richiesta presa atto (tab. ca_presa_atto)
	String prgPresaAtto = Utils.notNull(serviceResponse.getAttribute("M_RICHIESTAPRESATTO.rows.row.prgPresaAtto"));	
	String codCpiRichiestaPresaAtto = null, descCpiRichiestaPresaAtto=null, indirizzoDomRichiestaPresaAtto=null,
	codComDomRichiestaPresaAtto=null, dataTrasferimentoPresaAtto=null, strComDomRichiestaPresaAtto=null,
	strCapDomPresaAtto=null;
	if (!"".equals(prgPresaAtto)  && coopAbilitata) {		
		SourceBean richiestaPresaAtto = (SourceBean)serviceResponse.getAttribute("M_RICHIESTAPRESATTO.rows.row");
		codCpiRichiestaPresaAtto  = (String)richiestaPresaAtto.getAttribute("codCpiRich");
		descCpiRichiestaPresaAtto = (String)richiestaPresaAtto.getAttribute("descCpiRich");
		indirizzoDomRichiestaPresaAtto = (String)richiestaPresaAtto.getAttribute("strIndirizzoDom");
		codComDomRichiestaPresaAtto = (String)richiestaPresaAtto.getAttribute("codComDom");
		strComDomRichiestaPresaAtto = (String)richiestaPresaAtto.getAttribute("descComDom");
		strCapDomPresaAtto             = (String)richiestaPresaAtto.getAttribute("strCap");
		dataTrasferimentoPresaAtto = (String)richiestaPresaAtto.getAttribute("datTrasferimento");
		
		codCpiTit = codCpiRichiestaPresaAtto;
		strCpiTit = descCpiRichiestaPresaAtto;
		DATTRASFERIMENTO = dataTrasferimentoPresaAtto;
		codComdom = codComDomRichiestaPresaAtto;
		strComdom = strComDomRichiestaPresaAtto;
		strCapDom = strCapDomPresaAtto;
		STRINDIRIZZODOM = indirizzoDomRichiestaPresaAtto;
	}
	// fine modifica 11/08/20006
	
	//Messaggio da visualizzare in fase di apertura della popup
	String message = "";
	if (dataNotFound) {
		message = "Impossibile recuperare le informazioni del lavoratore. Controllare ";
	} else if (dataOccNotFound) {
		message = "Impossibile recuperare le informazioni sullo stato occupazionale del lavoratore.";
	} else if (isUserIncompetente) {
		message = "Il CPI dell\\\'utente non è competente per il lavoratore, impossibile effettuare la presa d'atto.";
	} /* else if (isPresaAttoInterProvincia) {
		message = "Provincia del CPI dell\\\'utente non coincidente con quella del lavoratore, impossibile effettuare la presa d'atto.";
	} */ else if (codCpiUserNotFound) {
		message = "CPI dell\\\'utente non trovato, impossibile effettuare la presa d'atto.";
	} else if (funzioneDaImplementare) {
		message = "Il CPI attuale è solo titolare dei dati, impossibile effettuare la presa d'atto in questo caso,\\nfunzionalità non ancora implemenetata.";
	}
	
%>
<%
	// dato che si stampa dopo una submit con method = post allora la query-string non viene valorizzata.
	// Bisogna valorizzarla a mano...
	String _page = (String)serviceRequest.getAttribute("PAGE");
	queryString = "PAGE="+_page+"&cdnLavoratore="+CDNLAVORATORE+"&trasferisci=true&stampato=true";	
%>
<html>
  <head>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    
    <title>Presa d'atto di trasferimento in uscita</title>
	<%@ include file="../global/fieldChanged.inc" %>
    <%@ include file="../global/Function_CommonRicercaComune.inc" %>
	<%@ include file="../anag/Function_CommonRicercaCPI.inc" %>
	<%@ include file="../global/confrontaData.inc" %>
	<%@ include file="../documenti/_apriGestioneDoc.inc"%>
    <script language="Javascript">
    <!--
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
		} else return true;
	}
	
	function getCPIDesc() {
  
	  if (document.Frm1.codCPI.value != "-1") {
		  btFindCPI_onclick( document.Frm1.codCPI,
		                     document.Frm1.codCPIHid,
		                     document.Frm1.strCPI,
		                     document.Frm1.strCPIHid,
		                     'codice');
  	  } else {
  	  		document.Frm1.strCPI.value="";
  	  		document.Frm1.strCPIHid.value="";
  	  }
	}

	//funzioni per discriminare i comuni
	//regola: non posso scegliere comuni all'interno del mio polo provinciale
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
		codCpi= document.Frm1.codCPI.value;
		if (codCom!="") {
			if (codCpi!=-1) {
				found = false;
				for (var i = 0; ((i < codComList.length) && (!found)); i++) {
					if (String(codComList[i]).toUpperCase() == String(codCom).toUpperCase()) {found = true;}
				}
				if (found) {
					alert("Il domicilio deve essere esterno alla provincia del proprio CPI");
					return false;
				} else return true;
			} else {
				alert("Non è possibile prendere atto di un trasferimento all'estero");
				return false;
			}	
		} else {
			alert("Il comune di destinazione è inesistente");
			return false;
		}
	}


    function vuoiProseguire() {
     if (confirm("Si sta cercando di Trasferire un lavoratore.\nVuoi continuare?")) {
          return true;
     }
     else {return false;}
    }

	<%=(!message.equals("") ? "alert('" + message + "');window.close();" : "")%>
	-->
    </SCRIPT>
  </head>

  <body class="gestione" onload="rinfresca();<%=(trasferito ? "updateMain();" : "")%>" >
<%
	infCorrentiLav.show(out); 
%>
	<font color="red"><af:showErrors/></font>
	<font color="green">
		<af:showMessages prefix="M_PRESAATTOTRASFERIMENTO"/>
	</font>
    <center>
  	  <p class="titolo">Presa d'atto di un Trasferimento in uscita</p>
      <af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="vuoiProseguire()">
        <%out.print(htmlStreamTop);%>
        <table class="main"> 
			<%@ include file="PresaAtto.inc" %>
        </table>
        <%out.print(htmlStreamBottom);%>
        <input type="hidden" name="PAGE" value="PrendiAttoTrasferimentoPage"/>
        <input type="hidden" name="CDNLAVORATORE" value="<%=CDNLAVORATORE%>"/>
        <input type="hidden" name="strCodiceFiscale" value="<%=strCodiceFiscale%>"/>
        <input type="hidden" name="strCognome" value="<%=strCognome%>"/>
        <input type="hidden" name="strNome" value="<%=strNome%>"/>
        <input type="hidden" name="datNasc" value="<%=datNasc%>"/>
        <input type="hidden" name="codComNas" value="<%=codComNas%>"/>
        <input type="hidden" name="STRCODICEFISCALEOLD" value="<%=STRCODICEFISCALEOLD%>"/>               
        <input type="hidden" name="NUMKLOLAVSTORIAINF" value="<%=NUMKLOLAVSTORIAINF%>"/>
        <input type="hidden" name="PRGLAVSTORIAINF" value="<%=PRGLAVSTORIAINF%>"/>       
        <input type="hidden" name="NUMKLOLAVORATORE" value="<%=String.valueOf(numKloLavoratore)%>"/>
        <input type="hidden" name="PRGELENCOANAGRAFICO" value="<%=prgElencoAnag%>"/>
        <input type="hidden" name="NUMKLOELENCOANAG" value="<%=String.valueOf(numKloElencoAnag)%>"/>
        <input type="hidden" name="PRGPATTOLAVORATORE" value="<%=prgPattoLav%>"/>
        <input type="hidden" name="NUMKLOPATTOLAVORATORE" value="<%=String.valueOf(numKloPattoLav)%>"/>                           
        <input type="hidden" name="trasferisci" value="true"/>
        <input type="hidden" name="pageDocAssociata" value="<%=pageDocAssociata%>"/>
        <input type="hidden" name="datInizio" value="<%=datInizio%>"/>
        <input type="hidden" name="codRegioneCpiUser" value="<%=codRegioneCpiUser%>"/>
        <%-- 11/08/2006 savino: presa atto in cooperazione --%>
        <input type="hidden" name="prgPresaAtto" value="<%=prgPresaAtto%>"/>
<% if (!trasferito) {%>
		<input type="submit" name="avanti" class="pulsante" value="Prendi atto del trasferimento"/>
<%} else { %>
	<input type="button" name="chiudi" class="pulsante" value="Chiudi" onclick="window.close()"/>
	<%if (stampato==null || !stampato.equals("true")) { %>
	<input class="pulsante" type="button"  value="Stampa doc. presa atto" 
  				onclick="apriGestioneDoc('RPT_STAMPA_RICHIESTA_DOC',
  										'&cdnLavoratore=<%=CDNLAVORATORE%>&pagina=<%=pageDocAssociata%>&isPresaAtto=true&bodyEventFunction=onunload%3D&quot;parent.window.opener.top.main.aggiorna()&quot;',
										'TRDOC','REPORTFRAMEMAINRELOADPAGE', true);">
	<%}%>
<%}%>
        <input type="hidden" name="CODMONOTIPOCPI" value="T" />
        <% if (isPresaAttoInterRegione) { %>
	        <input type="hidden" name="CODMONOTIPOORIG" value="F" />
	    <% } else { %>
	        <input type="hidden" name="CODMONOTIPOORIG" value="R" />
	    <% } %> 
      </af:form>      
</center> 
  </body>
</html>  



