<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			com.engiweb.framework.util.JavaScript,
			java.text.SimpleDateFormat,
			java.util.*,
			java.math.*,
			it.eng.sil.security.*,
			it.eng.sil.util.*,
			it.eng.sil.bean.*,
			it.eng.afExt.utils.*,
			it.eng.sil.Values,
			it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil,
			it.eng.sil.module.movimenti.InfoLavoratore"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
  String strChiaveTabella = "";
  
  String prAutomatica     = null; 
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  BigDecimal kLockProt    = null;
  String dataOraProt = null;
  String oraProt  = null;
  String dataProt = null;

  String pageDocAssociata = (String) serviceRequest.getAttribute("pageDocAssociata");
  String trasferisci = (String) serviceRequest.getAttribute("trasferisci");

  // se si inserisce un nuovo documento o se si cambia la combo dello stato da NP a PR
  // ATTENZIONE: permette di gestire il nuovo numero di protocollo (se si vuole protocollare il documento. Vedi blocco P1)
  boolean isNewDoc = SourceBeanUtils.getAttrBoolean(serviceRequest, "NUOVO", false);
  // Utilizzato solo in caso di refresh della pagina
  boolean refreshPr = SourceBeanUtils.getAttrBoolean(serviceRequest, "REFRESH_PR", false);
  // RECUPERO IL MODULO CHE HA ESEGUITO L'AZIONE DELLA PAGE (li provo tutti)
  SourceBean activeModule = (SourceBean) serviceResponse.getAttribute("dettagliDocumento");
  if (activeModule == null)
  		activeModule = (SourceBean) serviceResponse.getAttribute("salvaDocumento");
  if (activeModule == null)
  		activeModule = (SourceBean) serviceResponse.getAttribute("nuovoDocumento");

  // se si sta passando da un doc non protocollato ad un doc protocollato allora bisogna riprendere
  // le info sulla protocollazione valida 
  if (refreshPr) {
  		activeModule = (SourceBean) serviceResponse.getAttribute("M_RefreshDocumento");
  }

  SourceBean cpiModule = (SourceBean) serviceResponse.getAttribute("M_GetCpiDocumento.ROWS.ROW");

  String frameName = (String)serviceRequest.getAttribute("FRAME_NAME");
  if (frameName == null) {
  	frameName = (String)activeModule.getAttribute("FRAME_NAME");
  }

  String fromPattoDettaglio = (String)serviceRequest.getAttribute("fromPattoDettaglio");
  if (fromPattoDettaglio == null) {
  	fromPattoDettaglio = (String)activeModule.getAttribute("fromPattoDettaglio");
  }
  boolean fromPatto=fromPattoDettaglio!=null;
  /*
   * Controlla se le operazioni del modulo SalvaDocumento sono andate a buon fine,
   * in modo da regolare la questione del ritorno al dettaglio in fase di inserimento
   */
  boolean operationSuccess = activeModule.containsAttribute("OPERAZIONI_OK");
  SourceBean activeModuleTemp = (SourceBean) serviceResponse.getAttribute("salvaDocumento");
  if (activeModuleTemp == null) {
	operationSuccess = true;
  }
  Documento doc = (Documento) activeModule.getAttribute("Documento");
  // CONTROLLO DOCAREA
  // Il file viene cancellato direttamente nella classe SalvaDocumento
  /*
  if (doc.getTempFilePreProtocollo()!=null) {
		if (!doc.getTempFilePreProtocollo().delete()) {
			response.setContentType("text/html");
			out.write("<html><body><p><h1>Impossibile cancellare il file temporaneo inviato a DOCAREA. Contattare l'amministratore di sistema.</h1></p></body></html>");
			TracerSingleton.log("download.jsp", TracerSingleton.CRITICAL, "Impossibile cancellare il file PROTOCOLLATO inviato a DocArea: "+doc.getTempFilePreProtocollo().getAbsolutePath());
			TracerSingleton.log("download.jsp", TracerSingleton.CRITICAL, "DATI REQUEST: ", serviceRequest);
			TracerSingleton.log("download.jsp", TracerSingleton.CRITICAL, "CONTATTARE L'AMMINISTRATORE DI SISTEMA");
			// cancello anche il file temporaneo registrato in locale
			if (doc.getTempFile()!=null) doc.getTempFile().delete();
			return;
		}
	}
  */
  // Info operatore
  BigDecimal cdnUtins = doc.getCdnUtIns();
  String     dtmins   = doc.getDtmIns();
  BigDecimal cdnUtmod = doc.getCdnUtMod();
  String     dtmmod   = doc.getDtmMod();
  String codIO = Utils.notNull(doc.getCodMonoIO());
  String codMotAnnullamentoAtto = doc.getCodMotAnnullamentoAtto();
  
  String codStatoAtto = doc.getCodStatoAtto();
  if(isNewDoc)
  	codStatoAtto = "PR";
  // Savino: ripulire questo commento
  // anche se non e' ancora protocollato perche' si e' in fase di inserimento lo considero tale
  //boolean docProtocollato = "PR".equals(codStatoAtto) && doc.getPrgDocumento();
  
  Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);

  // NB: leggo i dati da "activeModule" e non dalla request!
  int     cdnfunzione    = SourceBeanUtils.getAttrInt(activeModule, "cdnfunzione");
  String  cdnLavoratore  = SourceBeanUtils.getAttrStrNotNull(activeModule, "cdnLavoratore");
  String  prgAzienda     = SourceBeanUtils.getAttrStrNotNull(activeModule, "prgAzienda");
  String  prgUnita       = SourceBeanUtils.getAttrStrNotNull(activeModule, "prgUnita");
  boolean lookLavoratore = SourceBeanUtils.getAttrBoolean(activeModule, "lookLavoratore", false);
  boolean lookAzienda    = SourceBeanUtils.getAttrBoolean(activeModule, "lookAzienda", false);
  String  contesto       = SourceBeanUtils.getAttrStrNotNull(activeModule, "contesto");

  // DIVERSAMENTE dalla ricerca (in cui la "look" prevale sul codice),
  // Se è "fissato" il codice dell'entità, non posso fare la lookup corrispondente
  // NOTA: *non* faccio:  if isFilled(cod) { look = false; }
  // MA RIMPIAZZO TUTTI GLI "if look" CON "if look && isEmpty(cod)"

  // il "goBackListPage" serve per sapere da che lista sono arrivato qua
  // (sempre che si sono arrivato tramite una lista e non direttamente)
  // nota: e' fissato nell'XML di config del modulo di lista (nella "SELECT_CAPTION").
  String goBackListPage = SourceBeanUtils.getAttrStrNotNull(activeModule, "goBackListPage");
  
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");

  if (isNewDoc) {
	strChiaveTabella = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "strChiaveTabella");
  } else {
	strChiaveTabella = SourceBeanUtils.getAttrStrNotNull(activeModule, "strChiaveTabella");
	if (strChiaveTabella.equals(""))
		strChiaveTabella = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "strChiaveTabella");
  }


  String infStoriche = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "infStoriche");
  if (StringUtils.isEmpty(infStoriche)) {
    infStoriche = SourceBeanUtils.getAttrStrNotNull(activeModule, "infStoriche");
  }
  
  boolean isPresenteDocumentoGiaFirmatoGrafometricamente = serviceResponse.containsAttribute("M_VerificaDocumentoGiaFirmatoGrafometricamente.IS_DOCUMENTO_GIA_FIRMATO_GRAFOMETRICAMENTE");
  
  boolean isDocumentoGiaFirmatoGrafometricamente = false;
  if(isPresenteDocumentoGiaFirmatoGrafometricamente){
	  String siNoDocumento = serviceResponse.getAttribute("M_VerificaDocumentoGiaFirmatoGrafometricamente.IS_DOCUMENTO_GIA_FIRMATO_GRAFOMETRICAMENTE").toString();
	  if (siNoDocumento.equalsIgnoreCase("S")){
		  isDocumentoGiaFirmatoGrafometricamente = true;
	  }
  } else{
	  isDocumentoGiaFirmatoGrafometricamente = true;
  }

  boolean isConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma"); 
//boolean isButtonGestioneConsenso = false;
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
  

boolean docFirmato = false;
if(serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.IS_DOC_FIRMATO")!=null){
	docFirmato = ((Boolean)serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.IS_DOC_FIRMATO")).booleanValue();
}
	
boolean docFirmabile = false;
if(serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.IS_DOC_FIRMABILE")!=null){
	docFirmabile =  ((Boolean)serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.IS_DOC_FIRMABILE")).booleanValue();
}
  

String message = "";



SourceBean docpi3 = null;
String numeroProtocollo = null;
String dataProtocollo = null;
String dataInvio = null;
String statoInvio = null;
String numeroPratica = null;
String docType = null;
String dataacqril = null;
String oggetto = null;
String codicePAT = null;
String statoConsenso = null;

Documento documentoSil = null;
String descrizioneInvioPi3 = null;
Object lstDocAllegatiObj = null;
String descrizioneTipoProt = null;
String codiceTrattamento = null;
String descrizioneTrattamento = null;
String _protocollazionePi3 = null;
String _protocollazionePi3Error = null;
String isPraticaAlreadyProcessed =null;

InfoLavoratore infoLav = null;
String mittenteDestinatarioPi3="";
if(!StringUtils.isEmpty(cdnLavoratore)){
	infoLav = new InfoLavoratore(new BigDecimal(cdnLavoratore));
	mittenteDestinatarioPi3 = infoLav.getCodFisc();
}

boolean isDocPi3 = false;
boolean pi3Readonly = false;

isDocPi3 = serviceResponse.containsAttribute("M_GET_PRG_CONFIG_PROT.ROWS.ROW.PRGCONFIGPROT");


pi3Readonly = serviceResponse.containsAttribute("M_ExistDocumentiPi3.ROWS.ROW");

	  
	
		if(isDocPi3){

		 	SourceBean rowPrgconfigprot = (SourceBean) serviceResponse.getAttribute("M_GET_PRG_CONFIG_PROT.ROWS.ROW");
		  	BigDecimal prgConfigProt = (BigDecimal)rowPrgconfigprot.getAttribute("PRGCONFIGPROT");
			_protocollazionePi3 = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.PROTOCOLLAZIONE_PI3");
			if(!StringUtils.isEmpty(_protocollazionePi3) && _protocollazionePi3.equalsIgnoreCase("ERROR"))
				_protocollazionePi3Error = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.PROTOCOLLAZIONE_PI3_ERROR");
			docType = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.docType");
			dataacqril = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.dataacqril");
			oggetto = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.oggetto");
			codicePAT = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.codicePAT");
			documentoSil = (Documento) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.documentoSIL");
			//numeroProtocollo = am_protocollo_pitre.strsegnatura
			statoConsenso = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.statoConsenso");
			descrizioneInvioPi3 = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.descrizioneInvioPi3");
			lstDocAllegatiObj = (Object) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.lstDocAllegati");
			descrizioneTipoProt = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.descrizioneTipoProt");
			codiceTrattamento = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.CODTIPOTRATTAMENTO");
			descrizioneTrattamento = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.DESCRIZIONETRATTAMENTO");
			isPraticaAlreadyProcessed = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneDocPi3.isPraticaAlreadyProcessed");
	  		GregorianCalendar cal = new GregorianCalendar(); 
	  		numeroPratica = cal.get(Calendar.YEAR) + "/" + doc.getNumProtocollo();
			
	  		
	  		if(docType!=null && docType.equals("A") && docFirmabile && isConsensoAttivo)
	  			if(docFirmato)
	  				message = "Il documento e' stato sottoscritto con firma grafometrica e sara' inviato in automatico a PiTre";
	  			else
	  				message = "Si e' verificato un problema con la firma grafometrica, il documento andra' sottoscritto con firma autografa e non sara' inviato in automatico a PiTre";
	  		
	  		if(docType!=null && docType.equals("A") && docFirmabile && !isConsensoAttivo)
	  			message = "Il consenso all'uso della firma per il lavoratore e' " + statoConsenso + ", pertanto il documento pdf andra' sottoscritto con firma autografa e non sara' inviato in automatico a PiTre";
 
	  		if(docType!=null && docType.equals("A") && !docFirmabile )
	  			message = "Il documento andra' sottosccritto con firma autografa e pertanto non sara' inviato in automatico a PiTre";
/*
	  		if(docType!=null && docType.equals("P") )
	  			message = "Il documento sara' inviato in automatico a PiTre";
*/
	  		
	  		
		  	if(pi3Readonly){
		  		
		  		docpi3 = (SourceBean) serviceResponse.getAttribute("M_ExistDocumentiPi3.ROWS.ROW");
		  	  	numeroProtocollo = (String) docpi3.getAttribute("strsegnatura");
		  	  	//numeroPratica = (String) docpi3.getAttribute("STRNUMPRATICA");

			  	dataProtocollo = (String) docpi3.getAttribute("datprot");
			  	dataInvio = (String) docpi3.getAttribute("datinvio");
			  	statoInvio = (String) docpi3.getAttribute("CODSTATODOC");
		
		  	}

		}  
  // gestione attributi	
  String _page = "DocumentiAssociatiPage";
  PageAttribs attributi = new PageAttribs(user, _page);
	
  boolean canModify = attributi.containsButton("AGGIORNA");
  boolean canInsert = attributi.containsButton("INSERISCI");
  boolean canAnnullaDoc = attributi.containsButton("ANNULLADOC");
  boolean preparaInvio = attributi.containsButton("PREPARA_INVIO");
  boolean canRiFirmaGrafometrica = attributi.containsButton("RIFIRMA_GRAFOMETRICA");
  
  /*
  TracerSingleton.log(
				Values.APP_NAME,
				TracerSingleton.DEBUG,
				"debug della jsp dettagliDocumento. canModify="+ canModify + 
						 ", canInsert="+ canInsert +
						 ", canAnnullaDoc="+canAnnullaDoc + 
						 ", isNewDoc="+ isNewDoc + 
						 ", codStatoAtto="+codStatoAtto + 
						 ", Oggetto Documento (doc)="+doc
						 );
*/						 
  // Savino 19/09/05: annullato o comunque non gestibile
  // si puo' gestire un documento se: 
  //		- il n. di prot. non e' presente e si trova in 'NP'
  // 		- il n. di prot. e' presente e si trova in 'PR' 
  //		se (false) il documento e' annullato e/o non gestibile
  // ATTENZIONE: 
  //     1. SE DOVESSERO CAMBIARE GLI STATI GESTIBILI DAL DOCUMENTO (AL MOMENTO SONO 3: NP, PR, AU)
  //             BISOGNEREBBE RIVEDERE QUESTA CONDIZIONE  
  //     2. OCCHIO A codStatoAtto SE IL DOCUMENTO E' NUOVO VIENE POSTO A 'PR'
  boolean annullato = //("AU".equals(codStatoAtto) || "AN".equals(codStatoAtto)  ) ||
  					  !(codStatoAtto.equals("NP") && doc.getNumProtocollo()==null) &&
	                  (!codStatoAtto.equals("PR"));
  // se il documento e' nello stato di ANNULLATO non sono piu' possibili operazioni su di esso
  canAnnullaDoc = (!annullato) && canAnnullaDoc;
  canModify = !annullato&&canModify;
  canInsert =!annullato&&canInsert;
  
  // RESTRIZIONE DEL PERMESSO DI MODIFICA IN BASE ALLO STATO DEL DOCUMENTO (STORICO? PROTOCOLLATO?)
  // e'possibile selezionare un documento storicizzato anche al di fuori della lista delle informazioni storiche.
  // Per esempio con una ricerca specifica sulla data di fine validita' oppure nel momento in cui inseriamo o 
  // modifichiamo il documento: al successivo ricaricamento della pagina e' necessario stabilire se sia o meno 
  // valido. Un documento con data fine validita' = alla data odierna e' un docuemnto ancora valido.
  // Mantengo il flag infStoriche perche' permette di capire se si proviene dalla lista delle inf. storiche
  String sysdate = DateUtils.getNow();
  String dataFine = doc.getDatFine();
  dataFine = (dataFine==null || dataFine.equals("")) ? sysdate: dataFine; 
  if (/*infStoriche.equalsIgnoreCase("true") || */DateUtils.compare(dataFine, sysdate)<0) {		// sono in STORICO? Se sì non posso modificarlo
	canModify = false;
  }
  else {	// Se NON sono in STORICO,
	// TODO gestire meglio i casi di errore
	if (operationSuccess && (doc != null) && ! isNewDoc) {
//		if (doc.getNumProtocollo() != null) { purtroppo non e' vero: un documento in PR puo' non avere il numero
//                                            di protocollo, pero' ha la data protocollo
		if (doc.getDatProtocollazione()!=null || doc.getNumProtocollo()!=null) {
		
			// Documento già PROTOCOLLATO, non è possibile modificare i campi
			canModify = false;
		}
	}
	else {
		// Savino 15/09/05: caso in cui fallisce l'operazione
		if (!operationSuccess  && ! isNewDoc && 
				(doc != null && (doc.getNumProtocollo()!=null) || doc.getDatProtocollazione()!=null))
			canModify = false;
	}
	
	//Nel caso si siano verificati errori durante l'aggiornamento della data di fine validita'
	//setto a readOnly=true tutti i campi escluso la data di fine val.
	boolean aggiornaDatFineVal = SourceBeanUtils.getAttrBoolean(activeModule, "aggiornaDatFineVal", false);
	if (! operationSuccess && aggiornaDatFineVal) {
		if (doc.getNumProtocollo() != null || doc.getDatProtocollazione()!=null) {

			// Documento già PROTOCOLLATO, non è possibile modificare i campi
			canModify = false;
		}
	}
  } //else-if "infStoriche"

  boolean canEditPage  = (canInsert &&   isNewDoc) ||
						 (canModify && ! isNewDoc);
						 // Posso editare e poi salvare o inserire se e solo se
						 // posso inserire e il documento è "nuovo" oppure
						 // posso salvare e il documento esiste già.

  String  readonly = (!canEditPage ? "true" : "false");
  
  boolean readOnlyStr = readonly.equalsIgnoreCase("true");

%>

<%  // NUMERO DI PROTOCOLLO
  
  boolean isNuovoProtocollo = false;
  boolean numProtEditable = false;

  //boolean nuovo = SourceBeanUtils.getAttrBoolean(serviceRequest, "NUOVO", false);
  //**********
  // blocco P1
  //**********
  if (isNewDoc) {
   //Numero protocollo da applicare se in inserimento nuovo
	Vector rows = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
	if (rows != null && !rows.isEmpty()) {
		//Reperisco la data e l'ora odierna per la protocollazione
		dataOraProt = (new SimpleDateFormat("dd/MM/yyyy HH:mm")).format(new Date());
   
		SourceBean row = (SourceBean) rows.elementAt(0);
		prAutomatica     = (String) row.getAttribute("FLGPROTOCOLLOAUT");
		if ( prAutomatica.equalsIgnoreCase("S") ) {
			numProtEditable = false; 
			isNuovoProtocollo = true;
			numProtV     = (BigDecimal) row.getAttribute("NUMPROTOCOLLO");
			numAnnoProtV = (BigDecimal) row.getAttribute("NUMANNOPROT");
		}
		else {
			numProtEditable = true; 
			numAnnoProtV = new BigDecimal(dataOraProt.substring(6,10));
			isNuovoProtocollo = false;
		}
     
		//NON USATO: estReportDefautl = (String) row.getAttribute("CODTIPOFILEESTREPORT");
		kLockProt = (BigDecimal) row.getAttribute("NUMKLOPROTOCOLLO");

		//Se ho effettuato un refresh della pagina, devo recuperare i dati che ha inserito l'utente
		if (refreshPr) {
			doc = (Documento) activeModule.getAttribute("Documento");
		} else {
			doc.setDatAcqril(it.eng.afExt.utils.DateUtils.getNow());
		}
	}
  }
  else {
	dataOraProt = Utils.notNull(doc.getDatProtocollazione());
	if (doc.getNumProtocollo() != null) {
		numProtV = doc.getNumProtocollo();
	}
	if (doc.getNumAnnoProt() != null) {
		numAnnoProtV = doc.getNumAnnoProt();
	}
  }

  if (dataOraProt != null && !dataOraProt.equals("") && !dataOraProt.equals("null null")) {
  // la protocollazione docarea restituisce solo la data 
  	if (dataOraProt.length()>11)
		oraProt  = dataOraProt.substring(11,16);
	else 
		oraProt="00:00";
	dataProt = dataOraProt.substring(0,10);
  }

%>

<%
  // DATA FINE VALIDITA'
  boolean canModifyDatFineVal = false;
  if ( !annullato && attributi.containsButton("AGGIORNA") && ! isNewDoc) {
	  
	  boolean canModifyIfProt     = false;
	  boolean canModifyIfNoProt   = false;
	  
	  //if (! isNewDoc) {
		//Gestione modifica data fine validità
		SourceBean gestDoc = (SourceBean) serviceResponse.getAttribute("M_GetCodMonoGestDoc.ROWS.ROW");
		if (gestDoc != null) {
			String codMonoGestDoc = SourceBeanUtils.getAttrStrNotNull(gestDoc, "CODMONOGESTDOC");
			canModifyIfProt   = codMonoGestDoc.equalsIgnoreCase("P");  //solo se protocollata
			canModifyIfNoProt = codMonoGestDoc.equalsIgnoreCase("M");
		}
	  //}
	  // UN DOCUMEMNTO PROTOCOLLATO PUO' NON AVERE UN N. DI PRTOTOCOLLO (DA PORTING)
	  // invece la data di protocollazione in un documento protocolato c'e' sempre
	  //if (canModifyIfProt && (numProtV != null)) {
	  if (canModifyIfProt && doc.getDatProtocollazione()!=null) {
		canModifyDatFineVal = true;
	  }
	  else {
		if (/*numProtV == null*/ doc.getDatProtocollazione()==null) {
			canModifyDatFineVal = true;
		}
		else {
		//if (canModifyIfNoProt && (numProtV==null)) {
		//  canModifyDatFineVal = true;
		//} else {
		// QUESTA CONDIZIONE SEMPRE FALSA
		// Savino 22/09/05: commentata
		  // if (isNewDoc) canModifyDatFineVal = true;
		//}
		}
	  }
  }
%>

<%  
  boolean forzaSolaLettura = false;
  // nel caso in cui si voglia (vedi il caso delle dichiarazioni/attestazioni PRIMA del 13/04/2005..)
  // che i documenti siano comunque accessibili in sola lettura (anche se non protocollati) 
  // si puo' passare alla request il parametro
  // "forzaSolaLettura"
  // comunque ora non viene usato da nessuna funzione
  if (serviceRequest.containsAttribute("forzaSolaLettura"))
  	forzaSolaLettura=true;
  if (!readonly.equalsIgnoreCase("true")) {
  	readonly=String.valueOf(forzaSolaLettura);
  	canModify=!forzaSolaLettura;
  	readOnlyStr=forzaSolaLettura;
  	canModifyDatFineVal=!forzaSolaLettura;
  }
%>

<%
  // DICHIARAZIONI/ATTESTAZIONI: la pagina puo' essere in modifica ma non deve comparire il pulsante
  // nuovo documento (13/04/2005  Andrea Savino)
  boolean nuovoDocVisibile = attributi.containsButton("NUOVO") && cdnfunzione!=135;



%>  
<% // CARTA DI IDENTITA' VALIDA DA DID
  String strCI = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "CARTAIDENTITA");
  // e' stato richiesto un documento di ID
  boolean cartaIdentita = strCI.equalsIgnoreCase("CARTAIDENTITA");
  if (! cartaIdentita) {
	//Controllo se provengo dall'inserimento del doc. di identificazione
	cartaIdentita = activeModule.containsAttribute("CARTAIDENTITA");
   }

  //Controllo se è necessario salvare i dati della DID
  boolean salvaDIDTemporanea = SourceBeanUtils.getAttrBoolean(serviceRequest, "SALVADATIDID", false);

  Vector rows_doc = serviceResponse.getAttributeAsVector("M_GETCARTAIDENTITAVALIDA.ROWS.ROW");
  BigDecimal prgDocIdentPrincipale = null;
  if (rows_doc != null && !rows_doc.isEmpty()) { 
	SourceBean row = (SourceBean) rows_doc.elementAt(0);
	prgDocIdentPrincipale = (BigDecimal) row.getAttribute("PRGDOCUMENTO");
  } else {
	//Prendo l'informazione dalla response del modulo che è stato chiamato
	if (! operationSuccess) {
		String prgTmp = SourceBeanUtils.getAttrStrNotNull(activeModule, "prgDocIdentPrincipale");
		if (StringUtils.isFilled(prgTmp)) {
			prgDocIdentPrincipale = new BigDecimal(prgTmp);
		}
	}
  }
 %> 
 
<%
  String queryString = SourceBeanUtils.getAttrStrNotNull(activeModule, "QUERY_STRING");
  // Contiene il "queryString" a cui tornare dopo un eventuale "APRI".

  // GG 12/1/05: problema su "inserisci - dettaglio - apri - indietro": se vado a mettere
  // in "QUERY_STRING" la "PRIMA" queryString, nel caso di inserimento avrei quella per
  // la pagina di INSERIMENTO (dopo l'"indietro" tornerei nella pagina di inserimento!)
  // e se la rimuovo, si userebbe la queryString del SALVATAGGIO (dopo l'inserimento) perciò
  // devo ricrearmi il "QUERY_STRING" COME SE SI fosse arrivati da un "DETTAGLIO".
  // Quando? quando si è dopo un SALVA (tutt'alpiù non si modifica nulla).
  if ((activeModule!= null) && activeModule.getName().equalsIgnoreCase("salvaDocumento") &&
  	  StringUtils.isFilled(queryString)) {
  		
  		// Rimuovo parametri di salvataggio/inserimento
  		queryString = QueryString.removeParameter(queryString, "NUOVO");
  		
  		// Aggiungo parametri non noti al momento dell'inserimento (prgDocumento)
  		if (! QueryString.existsParameter(queryString, "prgDocumento")) {
  			queryString += "&prgDocumento=" + doc.getPrgDocumento();
  		}
  }

%>

<%
// POPUP EVIDENZE
String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
int _fun = 1;
if (cdnfunzione > 0) { _fun = cdnfunzione; }
EvidenzePopUp jsEvid = null;
boolean bevid = attributi.containsButton("EVIDENZE");
if(strApriEv.equals("1") && bevid && !cdnLavoratore.equals("")) { // Savino 29/09/05: un documento puo' riferirsi ad una azienda o anche a niente.
	jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
}
%> 
<%  
  String htmlStreamTop    = StyleUtils.roundTopTable(! readOnlyStr);
  String htmlStreamBottom = StyleUtils.roundBottomTable(! readOnlyStr);
%>  
<%
  String pagina = SourceBeanUtils.getAttrStrNotNull(activeModule, "pagina");
  String popUp  = SourceBeanUtils.getAttrStrNotNull(activeModule, "popUp");

%>
<%

	// verifica se all'utente è associato un profilo 'Patronato'
	boolean isTipoGruppoPatronato = false;
	if (user.getCodTipo().equalsIgnoreCase(MessageCodes.Login.COD_TIPO_GRUPPO_PATRONATO)) {
		isTipoGruppoPatronato = true;
	}

%>
<html>
<head>
<title>Dettaglio documento</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<% if (strApriEv.equals("1") && bevid && !cdnLavoratore.equals("")) { jsEvid.show(out); }%>
<af:linkScript path="../../js/"/>
<script language="Javascript" src="../../js/documenti/docPopup.js"></script>
<script language="Javascript" src="../../js/documenti/dettagliDocumento.js"></script>
<script language="Javascript" src="../../js/documenti/lookEntityDoc.js"></script>
<script language="Javascript" src="../../js/ComboPair.js"></script>

<script language="Javascript">
  
	// Rilevazione Modifiche da parte dell'utente
	var flagChanged = false;
	
	function fieldChanged() {
		<% if (readonly.equalsIgnoreCase("false")) { %> 
			flagChanged = true;
		<%}%> 
	}


	/*
	 * Rende TRUE se si può cambiare pagina. Se i dati sono stati modificati
	 * sarà l'utente a decidere se lo si può fare.
	 */
	function canGoAway() {
		if (flagChanged)
			return confirm("I dati sono cambiati.\nProcedere lo stesso?");
		else
			return true;
	}


	<%
	// Se devo Aggiornare la Pagina Chiamante (ex-"aggiornaPaginaChiamante"), eseguo il codice JS
	if (
		serviceRequest.containsAttribute("SALVA") && !fromPatto &&
		(pagina.equalsIgnoreCase("DispoDettaglioPage") || pagina.equalsIgnoreCase("AnagDettaglioPageIndirizzi")) &&
		(serviceResponse.getContainedAttributes("ERRORS").size() == 0)
		) {
			%>
			urlpage ="AdapterHTTP?";
			<% if (salvaDIDTemporanea) { %>
				urlpage += "AGGIORNACHIAMANTE=true&";
				urlpage += "datDichiarazione=" + window.opener.document.Frm1.datDichiarazione.value + "&";

				<% if (pagina.equalsIgnoreCase("DispoDettaglioPage")) { %> 
					urlpage += "PAGE=<%=pagina%>&";
					urlpage += "CODTIPODICHDISP=" + window.opener.document.Frm1.CODTIPODICHDISP.value + "&";
					urlpage += "DATSCADCONFERMA=" + window.opener.document.Frm1.DATSCADCONFERMA.value + "&";
					urlpage += "DATSCADEROGAZSERVIZI=" + window.opener.document.Frm1.DATSCADEROGAZSERVIZI.value + "&";
					urlpage += "flgDidL68=" + window.opener.document.Frm1.flgDidL68.value + "&";
					urlpage += "STRNOTE=" + window.opener.document.Frm1.strNote.value + "&";   
				<% } else { %>
					<%-- caso pagina="AnagDettaglioPageIndirizzi" --%>
					urlpage += "PAGE=TrasferimentoPage&";
					urlpage += "REFRESH=Y&";
					
					urlpage += "CODCPIORIG=" + window.opener.document.Frm1.CODCPIORIG.value + "&";
					urlpage += "CODCPIUTENTE=" + window.opener.document.Frm1.codCPI.value + "&";
					
					urlpage += "DATTRASFERIMENTO=" + window.opener.document.Frm1.DATTRASFERIMENTO.value + "&";
					urlpage += "STRINDIRIZZODOM=" + window.opener.document.Frm1.STRINDIRIZZODOM.value + "&";
					urlpage += "STRLOCALITADOM=" + window.opener.document.Frm1.STRLOCALITADOM.value + "&";
					urlpage += "codComdom=" + window.opener.document.Frm1.codComdom.value + "&";
					urlpage += "strComdom=" + window.opener.document.Frm1.strComdom.value + "&";
					urlpage += "strCapDom=" + window.opener.document.Frm1.strCapDom.value + "&";
					urlpage += "CODSTATOOCCUPAZ=" + window.opener.document.Frm1.CODSTATOOCCUPAZ.value + "&";
					urlpage += "codStatoOccupazRagg=" + window.opener.document.Frm1.codStatoOccupazRagg.value + "&";
					urlpage += "datAnzianitaDisoc=" + window.opener.document.Frm1.datAnzianitaDisoc.value + "&";
					urlpage += "datcalcolomesisosp=" + window.opener.document.Frm1.datcalcolomesisosp.value + "&";
					urlpage+="msgIRLav=" + window.opener.document.Frm1.msgIRLav.value + "&";
      				urlpage+="msgXIR=" + window.opener.document.Frm1.msgXIR.value + "&";
      				urlpage+="isInterRegione=" + window.opener.document.Frm1.isInterRegione.value + "&";
     			 	urlpage+="isInterProvincia=" + window.opener.document.Frm1.isInterProvincia.value + "&";
					
					urlpage += "datcalcoloanzianita=" + window.opener.document.Frm1.datcalcoloanzianita.value + "&";
					urlpage += "mesiAnzPrecInt=" + window.opener.document.Frm1.numAnzianitaPrec297.value + "&";
					urlpage += "CODMONOCALCOLOANZIANITAPREC297=" + window.opener.document.Frm1.CODMONOCALCOLOANZIANITAPREC297.value + "&";
					
					urlpage += "NUMMESISOSPPREC=" + window.opener.document.Frm1.numMesiSosp.value + "&";
					urlpage += "FLG181=" + window.opener.document.Frm1.FLG181.value + "&";
					urlpage += "STRNOTE=" + window.opener.document.Frm1.STRNOTE.value + "&";
					urlpage += "pageDocAssociata=<%=pageDocAssociata%>&";
					urlpage += "trasferisci=<%=trasferisci%>&";
				<% } %>
			<% } else { %>
				<%-- caso else di "if salvaDIDTemporanea" --%>
				urlpage += "PAGE=<%=pagina%>&";
			<% } %>
			
			//urlpage += "datDichiarazione";
			urlpage += "CDNFUNZIONE=<%=cdnfunzione%>&";
			urlpage += "CDNLAVORATORE=<%=cdnLavoratore%>&";
			urlpage += "PRGAZIENDA=<%= prgAzienda %>&PRGUNITA=<%= prgUnita %>&";
		
			<% if (popUp.equalsIgnoreCase("true")) { %>
				window.opener.prepareSubmit();
			<% } %>
		    
			<% if (frameName == null) frameName = "main"; %>
			window.open(urlpage, "<%=frameName%>");
	<%
	} // if aggiornaPaginaChiamante
	%>

	
	function aggiornaPrgDocPadre() {
	  if ((window.opener != null) && (window.opener != undefined) && (window.opener.document.Frm1!=null) && (window.opener.document.Frm1.PRGDOCUMENTO!=null)) {
	    <% if (doc!=null && doc.getPrgDocumento() !=null && !fromPatto) { %>
	         window.opener.document.Frm1.PRGDOCUMENTO.value = "<%=doc.getPrgDocumento()%>";
	    <% } else  if (serviceRequest.containsAttribute("SALVA")) {%>
	    	window.opener.location.reload();
	    <%}%>
	  }
	}

	function controllaRiferimento() {
		if (document.form.codAmbito.value == "ID") {
			<% if (prgDocIdentPrincipale == null) { %>
				document.form.flgDocIdentifP.value = "S";
			<% } else { 
					if (! prgDocIdentPrincipale.equals(doc.getPrgDocumento())) {%>
						document.form.flgDocIdentifP.value = "N";
			<%		}
			   } %>
		}
	}

	function controllaFlagDocPrincipale() {

		if (document.form.codAmbito.value == "ID" && document.form.flgDocIdentifP.value == "S") {
			<% if ((prgDocIdentPrincipale != null) && isNewDoc) {%>
				if (confirm("Esiste gia' un altro documento di identificazione principale. Continuare?")) {
					document.form.sostituisciIdentificazione.value = "true";
				} else {
					document.form.sostituisciIdentificazione.value = "false";
					document.form.flgDocIdentifP.value = "N";         
				}
			<% } %>
		}
		return true;
	}

	function goBackSomewhere() {
		<%
		String goBackButtonTitle;	// Titolo del bottone (se nulla o vuota, non c'è il bottone)
		boolean goBackIsInList = false;
		// A seconda del contenuto di "goBackListPage" decido dove tornare
		if (StringUtils.isFilled(goBackListPage)) {
			
			%>
			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;

			if (! canGoAway()) {
				return;
			}
			<%

			// Recupero l'eventuale URL generato dalla LISTA precedente
			String token = "_TOKEN_" + goBackListPage;
			String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
			if (StringUtils.isFilled(goBackListUrl)) {
				goBackIsInList = true;

        		if (popUp.equalsIgnoreCase("true")) {
        			if (infStoriche.equalsIgnoreCase("true")) {
        				goBackButtonTitle = "Torna alla lista dei documenti delle inf. storiche";
        			} else {
	            		if (! cartaIdentita) {
	            			goBackButtonTitle = "Torna alla lista dei documenti associati";
	            		} else {
		            		goBackButtonTitle = "Torna alla lista";
	            		}
	            	}
	            } else {
		        	goBackButtonTitle = "Torna alla lista dei documenti";
	            }
				%>
				setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
				<%
			} else {
				goBackButtonTitle = "Torna alla ricerca";
				String ricPage = goBackListPage.equalsIgnoreCase("ListaDocumentiPage")
								 ? "DocumentiRicercaPage" : "RicercaDocAssociatiPage";
				%>
				var url = "AdapterHTTP?PAGE=<%= ricPage %>" +
									"&cdnLavoratore=<%=cdnLavoratore%>" +
									"&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>" +
									"&lookLavoratore=<%=lookLavoratore%>" +
									"&lookAzienda=<%=lookAzienda%>" +
									"&contesto=<%=contesto%>" +
									"&pagina=<%=pagina%>" +
									"&popUp=<%=popUp%>" +
									"&strChiaveTabella=<%=strChiaveTabella%>" +
									"&infStoriche=<%=infStoriche%>" +
									"&cdnfunzione=<%=cdnfunzione%>";
				setWindowLocation(url);
				<%
			}
		}
		else {
			goBackButtonTitle = null;	// no bottone
		}
		%>
	}


	function nuovo() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		if (! canGoAway()) return;
		
		var url = "AdapterHTTP?PAGE=DettagliDocumentoPage" +
							"&NUOVO=true" +
							"&cdnLavoratore=<%=cdnLavoratore%>" +
							"&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>" +
							"&lookLavoratore=<%=lookLavoratore%>" +
							"&lookAzienda=<%=lookAzienda%>" +
							"&contesto=<%=contesto%>" +
							"&pagina=<%=pagina%>" +
							"&popUp=<%=popUp%>" +
							"&strChiaveTabella=<%=strChiaveTabella%>" +
							"&FRAME_NAME=<%=Utils.notNull(frameName)%>" +
							"&goBackListPage=<%= goBackListPage %>" +
							"&cdnfunzione=<%=cdnfunzione%>" + 
							"&SALVADATIDID=<%=salvaDIDTemporanea%>"; <%-- Savino 29/09/05: se dalla pop-up del patto o 
									del trasferimento o della did si inserisce un nuovo documento, bisogna portarsi
									dietro questo parametro col suo valore altrimenti non viene ricaricata nel frame
									sottostante la pagina giusta --%>
        <%if (fromPatto) {%>url += "&fromPattoDettaglio=1";<%}%>
		setWindowLocation(url);
	}

	function togliInfo(idAziLav) {
		togliInfo_gen(idAziLav, <%= readOnlyStr %>);
	}

	function displayLookInfo(idAziLav) {
		displayLookInfo_gen(idAziLav, <%= readOnlyStr %>);
	}

	function onLoad() {
		<% if (! popUp.equalsIgnoreCase("true")) { %>
			rinfresca();
		<% } %>
		aggiornaPrgDocPadre();
		<%if(strApriEv.equals("1") && bevid && !cdnLavoratore.equals("")) { %>apriEvidenze();<%}%>
	}
	<%-- Savino 15/09/05: variabile usata anche nella funzione protocollazione_onChange() per determinare il 
	                      comportamento al cambio della selezione della combo codStatoAtto 
	--%>
	// stato dell'atto del documento registrato nel db (se presente)
	var _codStatoAtto = "<%=doc.getCodStatoAtto()%>";
	
	function checkAnnullaDoc(ancheSeNonCambiato){		
		var attoCollegato = <%=doc.getStrChiaveTabella()!=null && "N".equals(doc.getFlgAnnullamentoDocumento())%>;
		var warningAttoCollegato = <%=doc.getStrChiaveTabella()!=null && "S".equals(doc.getFlgAnnullamentoDocumento())%>;
		if ((document.form.CODSTATOATTO.value=="AU") && attoCollegato) {
			alert("Il documento non può essere annullato direttamente ma solo tramite l'annullamento dell'atto collegato.");
			return false;
		}		
		if(ancheSeNonCambiato && document.form.CODSTATOATTO.value==_codStatoAtto){
			alert("Stato atto non modificato");
			return false;
		}		
		if(document.form.CODSTATOATTO.value=="AU"  && 
			!(document.form.codMotAnnullamentoAtto != null && document.form.codMotAnnullamentoAtto.value!="")){
			alert("Motivo di annullamento documento obbligatorio");
			return false;
		}
		if(document.form.CODSTATOATTO.value!="AU"  && 
			(document.form.codMotAnnullamentoAtto != null && document.form.codMotAnnullamentoAtto.value!="")){
			alert("Motivo di annullamento obbligatorio se si annulla il documento");
			document.form.codMotAnnullamentoAtto.focus();
			return false;
		}
		if (document.form.CODSTATOATTO.value=="AU"){
			if(warningAttoCollegato) 
				return confirm("La funzionalità annulla il documento, che risulta collegato ad un atto:\n Vuoi proseguire?");
			else 
				return confirm("La funzionalità annulla il documento.\n Vuoi proseguire?");
		}
		
		<% SourceBean rowStatoProsp = (SourceBean) serviceResponse.getAttribute("M_GetStatoProspetto.ROWS.ROW"); 
		   if (rowStatoProsp != null) {
		   		String statoProspetto  = SourceBeanUtils.getAttrStrNotNull(rowStatoProsp, "statoProspetto"); %>
				var statoProsp = "<%=statoProspetto%>";
				if(document.form.CODSTATOATTO.value=="PR" && statoProsp == 'A') {
					alert("Impossibile protocollare un prospetto in corso d'anno");
					return false;
				}
		<%}%>
		return true;
	}
	
	//se si proviene dal nulla osta tramite il pulsante Associa documenti non deve essere possibile inserire 
   //un nuovo documento associato al Nulla Osta
	function controllaNullaOsta(){
		<% if(pagina.equals("CMNullaOstaPage")) {%>
			if(document.form.codTipoDocumento.value == "NULOST") {
				alert("Utilizzare un tipo documento diverso da Nulla Osta (per es. 'doc. collegato a Nulla Osta') ");
				return false;
			}
		<%} else {%>
			return true;
		<%}%>
	}
	
	
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

	function sendPi3(){
		document.form.action = document.form.action + "PROT_PI3=OK&actionPi3=W&prgDocumento=<%=doc.getPrgDocumento()%>&CODSTATOATTO=<%=doc.getCodStatoAtto()%>&cdnLavoratore=<%=cdnLavoratore%>&cdnFunzione=<%=cdnfunzione%>&";
		document.form.action = document.form.action + "DOCUMENTTYPE=<%=doc.getCodTipoDocumento()%>&NUMPROT=<%=numeroProtocollo%>&docType=<%=docType%>&CONSENSO=<%=isConsensoAttivo%>";
		return true;
	}
	
	function firmaGrafometricamente(){
		
		if (confirm("L'operazione avvierà il processo di firma grafometrica. Si desidera continuare?")) {
			document.formFirma.submit();
		}
	}

	</script>

<script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>
<%@ include file="_apriGestioneDoc.inc"%>

</head>

<body class="gestione" onload="onLoad()">
<p class="titolo">Documento</p>

<af:showErrors />
<af:showMessages prefix="SalvaDocumento"/>

<a name="iniziodati"></a>

<% if (StringUtils.isFilled(goBackButtonTitle) && goBackIsInList) { %>
	<table width="95%"  width="96%" align="center">
	<tr><td>
		<a href="#" onClick="goBackSomewhere();return false;"
		   title="<%= goBackButtonTitle %>"><img src="../../img/rit_lista.gif" align="right" border="0"></a>
	</td></tr>
	</table>
<% } %>

<af:form name="form" action="AdapterHTTP?PAGE=DettagliDocumentoPage&SALVA=true&" method="POST" 
		 encType="multipart/form-data">

<script language="javascript">
	document.form.action = document.form.action + "pageDocAssociata=<%=pageDocAssociata%>&";
</script>

<input type="hidden" id="PROT_PI3" name="PROT_PI3" value="KO" />
<input type="hidden" id="actionPi3" name="actionPi3" value="" />
<input type="hidden" name="DOCUMENTTYPE" value="<%=doc.getCodTipoDocumento()%>">
<input type="hidden" name="NUMPROT" value="<%=numeroProtocollo%>">
<input type="hidden" name="docType" value="<%=docType%>">
<input type="hidden" name="CONSENSO" value="<%=isConsensoAttivo%>">



<input type="hidden" name="NUOVO" value="" />
<input type="hidden" name="cdnFunzione" value="<%=cdnfunzione%>" />

<%-- Chiavi per le lookup (vedi file INC) --%>
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
<input type="hidden" name="lookLavoratore" value="<%=lookLavoratore%>" />
<input type="hidden" name="lookAzienda" value="<%=lookAzienda%>" />
<input type="hidden" name="contesto" value="<%=contesto%>" />

<input type="hidden" name="pagina" value="<%=pagina%>" />
<input type="hidden" name="strChiaveTabella" value="<%=strChiaveTabella%>" />
<input type="hidden" name="popUp" value="<%=popUp%>" />
<input type="hidden" name="infStoriche" value="<%=infStoriche%>" />
<input type="hidden" name="goBackListPage" value="<%=goBackListPage%>" />

<input type="hidden" name="prgDocumento" value="<%= Utils.notNull(doc.getPrgDocumento()) %>" />
<input type="hidden" name="numKloDocumento" value="<%= Utils.notNull(doc.getNumKloDocumento()) %>" />

<input type="hidden" name="nomeDoc" value="<%=doc.getStrNomeDoc()%>" />

<input type="hidden" name="QUERY_STRING" value="<%= queryString %>" />

<% String cartaidentitaValue = (isNewDoc && cartaIdentita) ? "CARTAIDENTITA":""; %>
<input type="hidden" name="CARTAIDENTITA" value="<%= cartaidentitaValue %>" />
<% if (StringUtils.isFilled(frameName)) { %>
	<input type="hidden" name="FRAME_NAME" value="<%= frameName %>" />
<% } %>
<%if (fromPatto) {%>
    <input type="hidden" name="fromPattoDettaglio" value="1">
<%}%>
<%-- Savino 07/10/05: la gestione del lock del protocollo avviene tramite js
if (isNuovoProtocollo) { %>
	<input type="hidden" name="KLOCKPROT" value="<%= kLockProt %>" />
<% } 
	VERRA' ABILITATA CON LA CHIAMATA DELLA FUNZIONE ASSOCIATA ALLA COMBO CODSTATOATTO
--%>
	<input type="hidden" name="KLOCKPROT" value="" disabled/>
	
<input type="hidden" name="prgDocIdentPrincipale" value="<%=Utils.notNull(prgDocIdentPrincipale)%>" />
<input type="hidden" name="sostituisciIdentificazione" value="" />

<!-- utilizzato per il refresh della pagina -->
<input type="hidden" name="REFRESH_PR" value="" />
<input type="hidden" name="AGGIORNADATFINEVAL" value="" />




<%--  LAYER DI INFO E LOOKUP PER LAVORATORE E AZIENDA  --%>

<div id="divLookLav_info" style="display:none">
	<%
	if (StringUtils.isFilled(cdnLavoratore) && ! popUp.equalsIgnoreCase("true")) {
		%>
		<span class="sezioneMinimale">
			Lavoratore&nbsp;&nbsp;
			<%
			boolean enableDelLav = lookLavoratore && ! contesto.equalsIgnoreCase("L") && ! readOnlyStr;
			// NB: sarebbe "look && isEmpty(cod)" <==> escludo il LOOK se ho un codice dato
			if (enableDelLav) {
				%>
				<a href="#" onClick="togliInfo('Lav');return false"><img src="../../img/del.gif" border="0" alt="togli lavoratore"></a>
				<%
			}
			%>
		</span>
		<%
		InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
		testata.show(out);
	}
	%>
	<br/>
</div>

<div id="divLookLav_look" style="display:none">
	<span class="sezioneMinimale">
		Lavoratore&nbsp;&nbsp;
		<a href="#" onClick="apriSelezionaLavoratore();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
	</span>
	<%= htmlStreamTop %>
	<table class="main" width="100%">
		<%-- ***************************************************************************** --%>
		<%@ include file="lookLavoratore.inc" %>
		<%-- ***************************************************************************** --%>
	</table>        
	<%= htmlStreamBottom %>
</div>

<%
if (StringUtils.isFilled(cdnLavoratore) && ! popUp.equalsIgnoreCase("true")) {

	Linguette linguette = new Linguette(user, cdnfunzione, _page, new BigDecimal(cdnLavoratore));
	linguette.show(out);
	%><p/><%
}
%>


<div id="divLookAzi_info" style="display:none">
	<%
	if (StringUtils.isFilled(prgAzienda) && ! popUp.equalsIgnoreCase("true")) {
		%>
		<span class="sezioneMinimale">
			Azienda&nbsp;&nbsp;
			<%
			boolean enableDelAzi = lookAzienda && ! contesto.equalsIgnoreCase("A") && ! readOnlyStr;
			// NB: sarebbe "look && isEmpty(cod)" <==> escludo il LOOK se ho un codice dato
			if (enableDelAzi) {
				%>
				<a href="#" onClick="togliInfo('Azi');return false"><img src="../../img/del.gif" border="0" alt="togli azienda"></a>
				<%
			}
			%>
		</span>
		<%
		InfCorrentiAzienda testata = new InfCorrentiAzienda(prgAzienda, prgUnita);
		testata.show(out);
	}
	%>
	<br/>
</div>

<div id="divLookAzi_look" style="display:none">
	<span class="sezioneMinimale">
		Azienda&nbsp;&nbsp;
		<a href="#" onClick="apriSelezionaAzienda();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
	</span>
	<%= htmlStreamTop %>
	<table class="main" width="100%">
		<%-- ***************************************************************************** --%>
		<%@ include file="lookAzienda.inc" %>
		<%-- ***************************************************************************** --%>
	</table>
	<%= htmlStreamBottom %>
</div>

<script language="Javascript">

	displayLookInfo("Lav");

	displayLookInfo("Azi");

</script>

<%= htmlStreamTop %>

<table class="main" >    
    <%--
    <tr>
      <td class="etichetta">Stato Atto</td>
      <td class="campo" colspan="3">
          <af:comboBox classNameBase="input" name="CODSTATOATTO" moduleName="COMBO_STATO_ATTO_DOC"
                    title="Stato atto del documento" selectedValue="<%=codStatoAtto%>"
                    disabled="<%=String.valueOf(readonly)%>" onChange="fieldChanged(); protocollazione_onChange()" />
              <%
              String protocollazione = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "CODSTATOATTO");
              // GG 9-3-05: se in protocoll.MANUALE, devo recuperare il valore della combo di "protocollazione".
              boolean selectS = (numProtV != null) || protocollazione.equalsIgnoreCase("PR");
              %>     
      </td>
    </tr>
    --%>
    <tr>
        <td  colspan="4" class="azzurro_bianco">
	        <table id="sezioneProt" style="display:"  border="0" width="100%">
	        	<tr>
			    	<td class="etichetta2">Stato atto</td>
			        <td>
			        	<table cellpadding="0" cellspacing="0" border="0" width="100%">
			        		<tr>
					        <%  // Savino 19/09/05: commentato codice. canAnnullaDoc impostata insieme alle altre 
					            //                  variabili di accessibilita'
					            //String codAtto = Utils.notNull(codStatoAtto);
					            //if (codAtto.equals("")) codAtto = "PA";
								//boolean annullaDoc =  /*(canAnnullaDoc && docProtocollato) 
								// || (canInsert && isNewDoc);*/
					        %>
					        
			        					
			        			<td>
			        			<af:comboBox classNameBase="input" name="CODSTATOATTO" moduleName="COMBO_STATO_ATTO_DOC"
				                    title="Stato atto del documento" selectedValue="<%=codStatoAtto%>"
				                    disabled="<%=String.valueOf(!canAnnullaDoc)%>" onChange="fieldChanged();protocollazione_onChange()" />
				              <%
				              //String protocollazione = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "CODSTATOATTO");
				              // GG 9-3-05: se in protocoll.MANUALE, devo recuperare il valore della combo di "protocollazione".
				              //boolean selectS = (numProtV != null) || protocollazione.equalsIgnoreCase("PR");
				              // savino: sembrano non servire a niente
				              %>   			        				
			        			</td>
			        			
<%
	boolean registratoEProtocollato =  !isNewDoc && doc!=null && operationSuccess && numAnnoProtV!=null;
	String flgProtLocale = ProtocolloDocumentoUtil.protocollazioneLocale() ? "S" : "N" ;
	String annoProtocollo = "";
	if (ProtocolloDocumentoUtil.protocollazioneLocale() || registratoEProtocollato) 
		annoProtocollo = Utils.notNull(numAnnoProtV);
	if (!ProtocolloDocumentoUtil.protocollazioneLocale() && !registratoEProtocollato) {
%>
								<td align="">anno&nbsp;</td>
								<td>
			        			<td align=""> &nbsp;&nbsp;num.&nbsp;</td>
			        			<td>
								<td class="etichetta2">data
									<input type="hidden" name="numAnnoProt">
									<input type="hidden" name="numProtocollo">
									<input type="hidden" name="dataProt">
									<input type="hidden" name="oraProt" value="00:00">
								</td>
<%  } else {%>
								<td align="right">anno&nbsp;</td>
			        			<td>
			        				<af:textBox name="numAnnoProt" type="integer" validateOnPost="true"
			                            value="<%= annoProtocollo %>"
			                            title="Anno di protocollazione" classNameBase="input"
			                            size="4" maxlength="4"
			                            onKeyUp="fieldChanged()" required="false"
			                            readonly="true" />
								</td>
			        			<td align="right"> &nbsp;&nbsp;num.&nbsp;</td>
<% 


		if ("S".equals(flgProtLocale) || registratoEProtocollato) { %>
			        			<td>
			    				 	<af:textBox name="numProtocollo"
			                			value="<%= Utils.notNull(numProtV) %>"
			                            title="Numero di protocollo" classNameBase="input"
			                            size="10" maxlength="100"
			                            onKeyUp="fieldChanged()" required="false"
			                            readonly="<%= String.valueOf(!numProtEditable) %>" />
			                    </td>
	<% } else {%>
			<af:textBox type="HIDDEN" name="numProtocollo" value=""/>
	<% } %>
			        			<td class="etichetta2">data
			        		        <af:textBox name="dataProt" type="date" validateOnPost="true"
			               				value="<%=dataProt%>"
			               				title="data di protocollazione" classNameBase="input"
			               				size="11" maxlength="10"
			               				required="false" trim="false"
			               				readonly="<%= String.valueOf(!numProtEditable) %>"
			                    		onKeyUp="cambiAnnoProt(this,numAnnoProt)"
			                    		onBlur="checkFormatDate(this)" />
			            		</td>
<%-- 
L'ora della protocollazione non viene piu' gestita dalla protocollazione DOCAREA. Quindi se ci troviamo in questo caso bisogna nascondere il campo.
Bisogna disabilitare il caricamento delle info di protocollazione (data e numero e ora) nel caso in cui siamo nel caso di DOCAREA.
Le funzioni js utilizzano tutti i campi, per cui anche se vengono nascosti bisogna avere dei campi nascosti con quel nome in modo da
	evitare errori.
--%>
	<% if ("S".equals(flgProtLocale)) { %>
			        			<td class="etichetta2">ora
			           				<af:textBox name="oraProt" type="time" validateOnPost="false"
			               				value="<%=oraProt%>"
			               				title="ora di protocollazione" classNameBase="input"
			               				size="6" maxlength="5"
			               				required="false" trim="false"
			               				readonly="<%= String.valueOf(!numProtEditable) %>"
			               				onBlur="checkAndFormatTime(this)" />
			               			
	<% } else { %>
			<af:textBox type="HIDDEN" name="oraProt" value="00:00"/>
	<% } %>
<%} // fine sezione protocollazione locale o docarea ma col documento protocollato %>
<% if(dataProt == null) {
        dataProt = "";
    }
    if(oraProt == null) {
        oraProt = "";
   }
 %>
					               <af:textBox type="HIDDEN" name="dataOraProt" value="<%=dataProt + \" \" + oraProt%>"/>
					               <af:textBox type="HIDDEN" name="tipoProt" value="<%=prAutomatica%>"/>
					               <af:textBox type="HIDDEN" name="flgProtLocale" value="<%= flgProtLocale %>"/>
			                    </td>
			   				 </tr>
			    		</table>
			    	</td>
			    </tr>
			    <tr><%-- se nuovo doc. elimino una colonna spostando a sx tutta la tabella --%>
					<td class="etichetta2"><%= (!isNewDoc) ? "Motivo Annull. Atto":"Doc. di&nbsp;" %></td>
			    	<td>
			    		<table cellpadding="0" cellspacing="0" border="0" width="100%">
			        		<tr>
			    				<% if(!isNewDoc){ 
			    				// Savino 20/09/05: un documento non gestibile potrebbe avere un motivo annullamento 
			    				//            non gestibile dal documento, per cui in questo caso bisogna caricare
			    				//            tutta la tabella di decodifica dell'anullamento documento
			    						String moduloDecodAnnullamento = (canAnnullaDoc) ? 
			    						                                     "M_getMotivoAnnullamentoDocFiltrata" :
			    						                                     "M_getTuttiMotiviAnnullamentoDoc";
			    				%>
			        			<td nowrap="nowrap">
			     					<af:comboBox classNameBase="input" 
			     					    name="codMotAnnullamentoAtto" moduleName="<%=moduloDecodAnnullamento%>" 
			     					    addBlank="true" selectedValue="<%=codMotAnnullamentoAtto%>" 
			     					    disabled="<%=String.valueOf(!canAnnullaDoc)%>"/>
			        			</td>	
			    				<td class="etichetta2">Doc. di</td>		
			        			<% } %>
						       	<%						       	
								if (isNewDoc && cartaIdentita) codIO = "I";
						       	%>
			    				<td class="campo2" nowrap="nowrap">
					               <af:comboBox name="FlgCodMonoIO" selectedValue="<%=codIO%>" addBlank="true"
			                         title="Documento di Input /Output" classNameBase="input"
			                         disabled="<%=String.valueOf(readonly)%>"
			                         required="true"
			                         onChange="fieldChanged()">
										<option value="I" <%=(codIO.equalsIgnoreCase("I"))? "SELECTED=\"true\"" : "" %>>Input</option>
										<option value="O" <%=(codIO.equalsIgnoreCase("O"))? "SELECTED=\"true\"" : "" %>>Output</option>
		              			   </af:comboBox>
					            </td>
			            		<td class="etichetta2">Rif.</td>
			            		<td class="campo2" nowrap="nowrap">
			               			<%
						            Vector rows = new Vector();
			               			if (isTipoGruppoPatronato) {
			               				// logica utente patronato
			               				rows = serviceResponse.getAttributeAsVector("ComboTipoDocumentoPatronato.ROWS.ROW");
			               			} else {
			               				// logica vecchia
			               				rows = serviceResponse.getAttributeAsVector("ComboTipoDocumento.ROWS.ROW");
			               			}
			               			if (isNewDoc && cartaIdentita) {
						                doc.setCodTipoDocumento("CI");
						            }
						            ComboPair docComboPair = new ComboPair(rows, doc.getCodTipoDocumento(), "codambitodoc");
						            String codAmbitoDoc = docComboPair.getCodRef();
						            if (codAmbitoDoc == null) {  // GG 23/12/2004, in caso di refresh pagina
						            	codAmbitoDoc = SourceBeanUtils.getAttrStrNotNull(activeModule, "codAmbitoDaCombo");
						            }
						            boolean disabledCodAmbito = readOnlyStr;
						            if (isNewDoc && canInsert && cartaIdentita) {
						                codAmbitoDoc = "ID";
						                disabledCodAmbito = true;
						            }
						            %>
						            <% if (isTipoGruppoPatronato) { %>
						            <af:comboBox name="codAmbito" moduleName="ComboAmbitoPatronato" addBlank="true"
						                  selectedValue="<%= codAmbitoDoc %>"
						                  title="Riferimento documento" classNameBase="input"
						                  required="true"
						                  onChange="fieldChanged(); codAmbito_onChange()"
						                  disabled="<%=String.valueOf(disabledCodAmbito)%>" />
						            <% } else { %>
						            <af:comboBox name="codAmbito" moduleName="ComboAmbito" addBlank="true"
						                  selectedValue="<%= codAmbitoDoc %>"
						                  title="Riferimento documento" classNameBase="input"
						                  required="true"
						                  onChange="fieldChanged(); codAmbito_onChange()"
						                  disabled="<%=String.valueOf(disabledCodAmbito)%>" />
						            <% } %>
			                     </td>
					             <% if(canAnnullaDoc){ %>
					             <td>
					             	<input type="submit" class="pulsanti" name="AnnullaDoc" value="Salva" 
					             		onclick="return checkAnnullaDoc(true)&&controllaFlagDocPrincipale()&&checkCampi();">
					             </td>
					             <% } %>
			        		</tr>
			       		</table>
			    	</td>
				 </tr>
	        </table>
	    </td>
    </tr>
    <%-- Savino 20/09/05: la sezione protocollazione ora deve sempre essere visibile
	<script language="Javascript">
		//showLayer("sezioneProt", (document.form.CODSTATOATTO.value == "PR"));
	</script>
    --%>
    <%--
    <tr><td class="etichetta">Doc.&nbsp;di</td> 
        <td colspan="3">
	        <table colspacing="0" colpadding="0" border="0" width="100%">
		        <tr><td class="campo2" width="20%" nowrap="nowrap">
					<%					
					if (isNewDoc && cartaIdentita) codIO = "I";
					%>
		            <af:comboBox name="FlgCodMonoIO" selectedValue="<%=codIO%>" addBlank="true"
		                         title="Documento di Input /Output" classNameBase="input"
		                         disabled="<%=String.valueOf(readonly)%>"
		                         required="true"
		                         onChange="fieldChanged()">
							<option value="I" <%=(codIO.equalsIgnoreCase("I"))? "SELECTED=\"true\"" : "" %>>Input</option>
							<option value="O" <%=(codIO.equalsIgnoreCase("O"))? "SELECTED=\"true\"" : "" %>>Output</option>
		              </af:comboBox>
		            </td>
		                
		            <td class="etichetta2">Riferimento</td>
		            <td class="campo" nowrap="nowrap">
			            <%
			            Vector rows = serviceResponse.getAttributeAsVector("ComboTipoDocumento.ROWS.ROW");
			            if (isNewDoc && cartaIdentita) {
			                doc.setCodTipoDocumento("CI");
			            }
			            ComboPair docComboPair = new ComboPair(rows, doc.getCodTipoDocumento(), "codambitodoc");
			            String codAmbitoDoc = docComboPair.getCodRef();
			            if (codAmbitoDoc == null) {  // GG 23/12/2004, in caso di refresh pagina
			            	codAmbitoDoc = SourceBeanUtils.getAttrStrNotNull(activeModule, "codAmbitoDaCombo");
			            }
			            boolean disabledCodAmbito = readOnlyStr;
			            if (isNewDoc && canInsert && cartaIdentita) {
			                codAmbitoDoc = "ID";
			                disabledCodAmbito = true;
			            }
			            %>
			            <af:comboBox name="codAmbito" moduleName="ComboAmbito" addBlank="true"
			                  selectedValue="<%= codAmbitoDoc %>"
			                  title="Riferimento documento" classNameBase="input"
			                  required="true"
			                  onChange="fieldChanged(); codAmbito_onChange()"
			                  disabled="<%=String.valueOf(disabledCodAmbito)%>" />
		            </td>
		        </tr>
	        </table>
        </td>        
    </tr>
    --%>
    <tr>
        <td class="etichetta">
       		Tipo documento
        </td>
        <td class="campo" colspan="3">
	        <% if (readonly.equals("false")) { %>        

	            <af:comboBox name="codTipoDocumento"
	                  required="true"
	                  title="Tipo documento" classNameBase="input"
	                  onChange="fieldChanged()" disabled="<%=readonly%>" />

	            <script language="Javascript">
	                var arrayFiglio = new Array();
	                <%= docComboPair.makeArrayJSChild() %>
	                var comboPair = new ComboPair(document.form.codAmbito, document.form.codTipoDocumento, arrayFiglio,true);
	                comboPair.populate("<%=codAmbitoDoc%>", "<%=doc.getCodTipoDocumento()%>");
	            </script>

	        <% } else { %>
	        	<input type="hidden" name="codTipoDocumento" value="<%=doc.getCodTipoDocumento()%>">
                <af:textBox name="strTipoDoc" type="text"
                            value="<%=Utils.notNull(doc.getStrTipoDoc())%>"
                            classNameBase="input" size="60" readonly="true" />
	        <% } %>
        </td>
    </tr>

    <tr>
        <td class="etichetta">
            Descrizione
        </td>
        <td class="campo" colspan="3">
            <af:textBox name="strDescrizione" title="Descrizione"
            		value="<%= Utils.notNull(doc.getStrDescrizione()) %>"
                    classNameBase="input" size="50" maxlength="100"
                    onKeyUp="fieldChanged()" readonly="<%=readonly%>" />
        </td>
    </tr>
    <%-- Andrea 23/05/05: spostati due campi --%>
    <tr>
      <td colspan="4">
        <div class="sezione2"></div>
      </td>
    </tr>
    <tr>
        <td class="etichetta">
            Data di acquisizione / rilascio<br/>da parte del Centro per l'impiego
        </td>
        <td class="campo" colspan="3">
            <af:textBox type="date" name="DatAcqril" validateOnPost="true"
			            value="<%= Utils.notNull(doc.getDatAcqril()) %>"
            			title="Data di acquisizione / rilascio"  classNameBase="input"
            			size="12" maxlength="10"
            			required="true"
            			readonly="<%=readonly%>" onKeyUp="fieldChanged()" />
        </td>
    </tr>

    <tr>
        <td class="etichetta">
            Ente di acquisizione/rilascio
        </td>
        <td class="campo" colspan="3">
            <af:textBox name="StrEnteRilascio" value="<%= Utils.notNull(doc.getStrEnteRilascio()) %>"
						title="Ente di acquisizione/rilascio"
						classNameBase="input" size="50" maxlength="100"
						onKeyUp="fieldChanged()" readonly="<%=readonly%>" />
        </td>
    </tr>
    
    
    <tr>
      <td colspan="4">
        <div class="sezione2"></div>
      </td>
    </tr>
    <tr>
        <td class="etichetta">
            Data inizio validit&agrave;
        </td>
        <td class="campo2" nowrap="nowrap">
            <af:textBox name="DatInizio" type="date" validateOnPost="true" 
                        value="<%= Utils.notNull(doc.getDatInizio()) %>"
                        title="Data inizio validit&agrave;" classNameBase="input"
                        size="12" maxlength="10"
                        required="true"
                        onKeyUp="fieldChanged()" readonly="<%=readonly%>" />
        </td>
        <td class="etichetta">
            Data di fine validit&agrave;
        </td>
        <td class="campo2" nowrap="nowrap">
            <af:textBox 
                    type="date" validateOnPost="true" 
                    name="datFine" title="Data di fine validit&agrave;" value="<%= doc.getDatFine() %>"
                    classNameBase="input" size="12" maxlength="10"
                    onKeyUp="fieldChanged()" readonly="<%=String.valueOf(! canModifyDatFineVal)%>" />
            &nbsp;&nbsp;
            <input type="submit" id="aggDatFineVal" value="Aggiorna" class="pulsanti"
                   onClick="return checkAnnullaDoc(false)&&checkCampi();" style="display:none" />
        </td>
    </tr>
	<script language="Javascript">
		<%
		boolean showButtonNear = canModifyDatFineVal && !canModify ;
		if (! isNewDoc && showButtonNear) { %>
			showLayer("aggDatFineVal", <%= String.valueOf(showButtonNear) %>);
	        document.form.AGGIORNADATFINEVAL.value = '<%= showButtonNear ? "true" : "" %>';
			// AndS 20/09/05: commentata chiamata
			//showLayer("sezioneProt", (document.form.CODSTATOATTO.value == "PR") );
		<% } %>
	</script>
	
	
    <tr>
        <td class="etichetta">
            Num.documento o prot.esterno
        </td>
        <td class="campo" colspan="3">
            <af:textBox name="StrNumDoc" value="<%= Utils.notNull(doc.getStrNumDoc()) %>"
					title="Numero documento o protocollo esterno" 
					classNameBase="input" size="18" maxlength="15"
					onKeyUp="fieldChanged()" readonly="<%=readonly%>" />
        </td>
    </tr>
    
	<tr>
		<td class="etichetta">
			CPI di riferimento
		</td>
		<td class="campo" colspan="3">
			<% 
			String codCpiDoc = SourceBeanUtils.getAttrStrNotNull(cpiModule, "CODCPI");
			String cpiDescr  = SourceBeanUtils.getAttrStrNotNull(cpiModule, "STRDESCRIZIONE") + " - " + codCpiDoc;
			%>
			<input type="hidden" name="codCpi" value="<%= codCpiDoc %>" />
			<af:textBox name="cpiDescr" value="<%= cpiDescr %>"
					classNameBase="input" size="60" maxlength="100"
					readonly="true" />
		</td>
	</tr>

    <tr>
      <td colspan="4">
        <div class="sezione2"></div>
      </td>
    </tr>
    <tr>
    <tr>
        <td class="etichetta">
            Autocertificazione
        </td>
        <td class="campo" colspan="3">
			<%
			String codAC = Utils.notNull(doc.getFlgAutocertificazione());
			%>
            <af:comboBox name="flgAutocertificazione" selectedValue="<%=codAC%>" addBlank="true"
	                  title="Autocertificazione" classNameBase="input"
	                  onChange="fieldChanged()" disabled="<%=readonly%>">
				<option value="S" <%=(codAC.equalsIgnoreCase("S"))? "SELECTED=\"true\"" : "" %>>Sì</option>
				<option value="N" <%=(codAC.equalsIgnoreCase("N"))? "SELECTED=\"true\"" : "" %>>No</option>
            </af:comboBox>
        </td>
    </tr>

    <tr>
        <td class="etichetta">
            Documento amministrativo
        </td>
        <td class="campo" colspan="3">
			<%
			String codDA = Utils.notNull(doc.getFlgDocAmm());
			%>
            <af:comboBox name="flgDocAmm" selectedValue="<%=codDA%>" addBlank="true"
	                  title="Documento amministrativo" classNameBase="input"
	                  onChange="fieldChanged()" disabled="<%=readonly%>" >
				<option value="S" <%=(codDA.equalsIgnoreCase("S"))? "SELECTED=\"true\"" : "" %>>Sì</option>
				<option value="N" <%=(codDA.equalsIgnoreCase("N"))? "SELECTED=\"true\"" : "" %>>No</option>
            </af:comboBox>
        </td>
    </tr>
    <tr>
        <td class="etichetta">
            Documento di identificazione principale 
        </td>
        <td class="campo" colspan="3">
			<%
			String codIP = Utils.notNull(doc.getFlgDocIdentifP());
			if (isNewDoc && cartaIdentita) codIP = "S";
			%>
            <af:comboBox name="flgDocIdentifP" selectedValue="<%=codIP%>" addBlank="true"
		                  title="Documento IdentifP" classNameBase="input"
		                  onChange="fieldChanged()" disabled="<%=readonly%>" >
				<option value="S" <%=(codIP.equalsIgnoreCase("S"))? "SELECTED=\"true\"" : "" %>>Sì</option>
				<option value="N" <%=(codIP.equalsIgnoreCase("N"))? "SELECTED=\"true\"" : "" %>>No</option>
            </af:comboBox>
        </td>
    </tr>
		<%--  
    <tr>
        <td class="etichetta">
            Data di acquisizione / rilascio<br/>da parte del Centro per l'impiego
        </td>
        <td class="campo" colspan="3">
            <af:textBox type="date" name="DatAcqril" validateOnPost="true"
			            value="<%= Utils.notNull(doc.getDatAcqril()) %>"
            			title="Data di acquisizione / rilascio"  classNameBase="input"
            			size="12" maxlength="10"
            			required="true"
            			readonly="<%=readonly%>" onKeyUp="fieldChanged()" />
        </td>
    </tr>

    <tr>
        <td class="etichetta">
            Ente di acquisizione/rilascio
        </td>
        <td class="campo" colspan="3">
            <af:textBox name="StrEnteRilascio" value="<%= Utils.notNull(doc.getStrEnteRilascio()) %>"
						title="Ente di acquisizione/rilascio"
						classNameBase="input" size="50" maxlength="100"
						onKeyUp="fieldChanged()" readonly="<%=readonly%>" />
        </td>
    </tr>
    --%>
<%--
    <tr>
        <td class="etichetta">
            Modalit&agrave; di acquisizione/rilascio
        </td>
        <td class="campo" colspan="3">
            <af:textBox name="codModalitaAcqril" title="Descrizione" value="<%= Utils.notNull(doc.getCodModalitaAcqril()) %>"
                    classNameBase="input" size="10" maxlength="8"
          onKeyUp="fieldChanged()" readonly="<%=readonly%>" />
        </td>
    </tr>
    <tr>
        <td class="etichetta">
            Tipo di file
        </td>
        <td class="campo" colspan="3">
            <af:comboBox name="codTipoFile"
                  moduleName="ComboTipoFile"
                  addBlank="true"
                  selectedValue="<%= Utils.notNull(doc.getCodTipoFile()) %>"
                  title="Tipo di file" classNameBase="combobox"
                  onChange="fieldChanged()" disabled="<%=readonly%>"  />
        </td>
    </tr>
--%>
    <% if (!isNewDoc) { %>
    <tr>
        <td class="etichetta">
            Nome documento
        </td>
        <td class="campo" colspan="3">
            <af:textBox name="strNomeDoc"
            			value="<%= Utils.notNull(doc.getStrNomeDoc()) %>"
            			title="Nome documento" classNameBase="input"
            			size="50" maxlength="100"
             			readonly="<%=readonly%>"
            			onKeyUp="fieldChanged()" />
        </td>
    </tr>
    <% } %>

    <tr>
        <td class="etichetta">
            Note
        </td>
        <td class="campo" colspan="3">
            <af:textArea name="strNote"
						maxlength="1000" classNameBase="textarea" 
						title="Note" value="<%= Utils.notNull(doc.getStrNote()) %>"
						readonly="<%=readonly%>"
						onKeyUp="fieldChanged()" />
        </td>
    </tr>
</table>
  <%
  String prgDocumento = StringUtils.getStringValueNotNull( doc.getPrgDocumento() );
  String strNomeDoc = doc.getStrNomeDoc();
  boolean blobVuoto = ! doc.getExistBlob();
  boolean giaEsisteFileInDb = StringUtils.isFilled(strNomeDoc);
  %>

  <% if (giaEsisteFileInDb) { %>
        <table class="main">
            <tr>
              <td class="etichetta">
                Doc. archiviato
              </td>
              <td class="campo" nowrap="nowrap">
				<%
				String myJsBlobVuoto = "javascript:blobVuotoWarning('" + JavaScript.escape(strNomeDoc) + "')";
				%>
	
				Apri
				<%
				String myJsA;
				if (blobVuoto) myJsA = myJsBlobVuoto;
				else           myJsA = "javascript:visualizzaDocumento('DOWNLOAD',''," + prgDocumento + ")";
				%>
				<a href="<%= myJsA %>" onclick="return canGoAway();"><img src="../../img/text.gif" border="0" alt="Apre il documento" /></a>
				&nbsp;&nbsp;

				Scarica
				<%
				String myJsS;
				if (blobVuoto) myJsS = myJsBlobVuoto;
				else           myJsS = "AdapterHTTP?ACTION_NAME=DOWNLOAD&PRGDOCUMENTO=" + prgDocumento + "&asAttachment=true";
				%>
				<a href="<%= myJsS %>"><img src="../../img/download.gif" border="0" alt="Salva il documento" /></a>
 
				<% if (canModify) { %>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					Sostituisci documento
					<input type="checkbox" name="cb" value="N" onClick="showLayer('dett', document.form.cb.checked);" />

					<%-- per la gestione del refresh per quanto riguarda la memorizzazione del file --%>
					<%if (isNewDoc) { %>
					<input type="hidden" name="strNomeDoc" value="<%= Utils.notNull(strNomeDoc) %>" />
					<%}%>
				<% } %>
	          </td>
	        </tr>
        </table>
        
        <%
    	   if(isDocPi3){
        %>
        <table class="main">
			 <% if(!pi3Readonly){ %>
			 <tr>
			        <td  colspan="4" class="azzurro_bianco">
						   <table id="sezioneProt" style="display:"  border="0" width="100%">
			<% } %>
			
			
								<tr>
								  	<td class="etichetta" colspan="2">
								    	<div class='sezione2' id='Protocollazione_PiTre'>
								        	Protocollazione PiTre&nbsp;  
								    	</div>
								  	</td>  
								</tr>
								<%
								if (!StringUtils.isEmpty(_protocollazionePi3) && _protocollazionePi3.equalsIgnoreCase("TRUE")){ %>
	
									<tr>
									<td class="etichetta" nowrap colspan="2"><p class="titolo">Protocollazione Pi3 inviata con successo.
											<br/>
											<%--Motivo: <%=_protocollazionePi3Error%> --%>
											</p></td>
								</tr>
		
								<%} 
								else if (!StringUtils.isEmpty(_protocollazionePi3) && _protocollazionePi3.equalsIgnoreCase("ERROR")){ %>
									
									<tr>
									<td class="etichetta" nowrap colspan="2"><p class="titolo">Protocollazione Pi3 non inviata.
											<br/>
											Motivo: <%=_protocollazionePi3Error%></p></td>
								</tr>
									
		
							<%} 
				
					if (!StringUtils.isEmpty(message)){ %>
								<tr>
									<td class="etichetta" nowrap colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td class="etichetta" colspan="2" style="text-align: center;"><%=message%></td>
								</tr>
								<tr>
									<td class="etichetta" nowrap colspan="2">&nbsp;</td>
								</tr>

						
				<%
					}
				%>

								<tr>
							        <td class="etichetta">
		            					Numero protocollo :
		        					</td>
									<td class="campo" colspan="3">
									<af:textBox name="numeroProtocollo"
		            			value="<%=numeroProtocollo%>"
		            			title="Numero protocollo" classNameBase="input"
		            			size="50" maxlength="100"
		             			readonly="<%=readonly%>" /></td>
									
								</tr>
						
								<tr>
							        <td class="etichetta">
		            					Data protocollo :  
		        					</td>
		
									<td class="campo" colspan="3"><af:textBox name="dataProtocollo"
		            			value="<%=dataProtocollo%>"
		            			title="Data protocollo" classNameBase="input"
		            			size="50" maxlength="100"
		             			readonly="<%=readonly%>" /></td>
									
								</tr>

								<tr>
							        <td class="etichetta">
		            					Data invio :  
		        					</td>
		
									<td class="campo" colspan="3">
									<af:textBox name="dataInvio"
		            			value="<%=dataInvio%>"
		            			title="Data invio protocollo" classNameBase="input"
		            			size="50" maxlength="100"
		             			readonly="<%=readonly%>" /></td>
									
								</tr>
						
								<tr>
							        <td class="etichetta">
		            					Stato del documento inviato  :  
		        					</td>
		
									<td class="campo" colspan="3">
									<af:textBox name="statoInvio"
		            			value="<%=statoInvio%>"
		            			title="Stato invio protocollo" classNameBase="input"
		            			size="50" maxlength="100"
		             			readonly="<%=readonly%>" /></td>
									
								</tr>						
								<tr>
									<td class="etichetta" nowrap>Numero Pratica SPIL&nbsp;</td>
									<td class="campo" colspan="3">
										<af:textBox classNameBase="input" type="text" name="numeroPratica" value="<%=numeroPratica%>" readonly="true" title="numeroPratica" size="11" />
									</td>
								</tr>
								<tr>
									<td class="etichetta" nowrap>Tipo protocollazione&nbsp;</td>
									<td class="campo">
										<b>
											<af:textBox classNameBase="input" type="text" name="trattamentoDocumento" value="<%=descrizioneTipoProt%>" readonly="true" title="trattamentoDocumento" size="25" />
										</b>
									</td>
								</tr>
				
								<tr>
									<td class="etichetta" nowrap>Trattamento &nbsp;</td>
									<td class="campo">
										<b>
											<af:textBox classNameBase="input" type="text" name="trattamento" value="<%=descrizioneTrattamento%>" readonly="true" title="trattamento" size="15" /> 
										</b>
									</td>
								</tr>
								<tr>
									<td class="etichetta" nowrap>Data ricevimento documento&nbsp;</td>
									<td class="campo">
										<af:textBox classNameBase="input" type="text" name="dataRicevimentoDocumento" value="<%=dataacqril%>" readonly="true" title="dataRicevimentoDocumento" size="11" />
									</td>
								</tr>
					
								<tr>
									<td class="etichetta" nowrap>Mittente/destinatario&nbsp;</td>
									<td class="campo">
										<af:textBox classNameBase="input" type="text" name="mittenteDestinatario" value="<%=mittenteDestinatarioPi3%>" readonly="true" title="mittenteDestinatario" size="18" />
									</td>
								</tr>
								
								<tr>
									<td class="etichetta" nowrap>Oggetto&nbsp;</td>
									<td class="campo">
										<af:textBox classNameBase="input" type="text" name="oggetto" value="<%=oggetto%>" readonly="true" title="oggetto" size="60" />
									</td>
								</tr>
				
								<tr>
									<td class="etichetta" nowrap>Codice di classificazione da titolario PAT&nbsp;</td>
									<td class="campo">
										<%
											if(codicePAT != null){ %>
												<af:textBox classNameBase="input" type="text" name="codicePAT" value="<%=codicePAT%>" readonly="true" title="codicePAT" size="11" />
											<%} else{ %>
												<af:comboBox addBlank="true" name="codicePAT" moduleName="M_GET_CODICI_FROM_TITOLARIO" selectedValue="<%=codicePAT%>" />
											<%
											}
										%>
									</td>
								</tr>
								<tr>
						         	<td class="etichetta" colspan="2">
						           	<div class='sezione2' id='UlteInfoAllegati'>
						              	 <img id='tendinaInfo' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("altreInfoSezAllegati"));'/>
						               	Allegati&nbsp;  
						           	</div>
						         	</td>
						       	</tr>
				
								<tr>
						         	<td colspan="2">
						           	<div id="altreInfoSezAllegati" style="display:inline">
									<table  width="100%" cellspacing="0" cellpadding="3" border="0">
										<tr>
										<td colspan="2">
										<af:list moduleName="M_GetDocAssociatiPi3" skipNavigationButton="1" />
										</td>
										</tr>
									</table>
									</div>
									</td>
								</tr>
				<% 
				
				if(preparaInvio){
					if(StringUtils.isEmpty(isPraticaAlreadyProcessed) || isPraticaAlreadyProcessed.equalsIgnoreCase("FALSE")){ %>
					
								<tr>
								<td colspan="2" align="center">
									<input type="submit" name="inviaPi3" class="pulsanti" value="PREPARA INVIO" onclick="sendPi3()" />
									</td>
								</tr>

							

				<% 	}else{ %>
	
								<tr>
									<!-- <td colspan="2" align="center">
<!-- 									<input disabled="disabled" type="button" name="inviaPi3" class="pulsantiDisabled" value="PREPARA INVIO" onClick="sendPi3()" /></td> -->
									<td colspan="2" align="center"><input disabled="disabled" type="button" name="inviaPi3" class="pulsantiDisabled" value="PREPARA INVIO" /></td>
									
								</tr>	
					
				  <% 
				  	}
		        }
				
				if(!pi3Readonly){ 
					 %>
						  </td>        	 	          				                   	          	         
						</tr>
		  			</table>
				<%	
				}
				%>
				
				 </table>
				 <%
        	} 
        
			 %>

				 
        <div id="dett" style="display:none">
     <%}%>

        <table class="main" <% if (readOnlyStr) { %>style="display:none"<% } %>>
          <tr>
            <td class="etichetta">
              Seleziona il documento:
            </td>
            <td>
               <img src="../../img/upload.gif" border="0" />
               <input type="FILE" name="FILE1" size="50" onClick="fieldChanged()" />
            </td>
          </tr>
         </table>

  <%if (giaEsisteFileInDb) {%>
        </div>
  <%}%>


<br/>

<table class="main">
<%-- PER ORA NON MOSTRO IL BOTTONE DI "prenotazione protocollo" ^^^
		<%
		// Mostro il bottone di "prenotazione protocollo" solo
		// se in fase di NUOVO ma NON è una "CartaIdentita" (es. doc. fissato via DID)
		if (isNewDoc && !readOnlyStr && !cartaIdentita) {
		%>
			<tr>
				<td>
					<input type="button" name="protoVeloceB" class="pulsanti" value="Prenotazione protocollo"
							title="Imposta in automatico alcuni campi utili la protocollazione veloce"
							onClick="protoVeloce()" />
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
		<%
		}
		%>
--%>
		<% if ( ! readOnlyStr ) { %>
			<tr>
				<td>
					<% if (canModify || canInsert) {
						String etichBot = isNewDoc ? "Inserisci" : "Aggiorna";
							%>
							<input type="submit" class="pulsanti"
									value="<%= etichBot %>"
									onClick="return checkAnnullaDoc(false)&&controllaFlagDocPrincipale()&&checkCampi()&&controllaNullaOsta();" />
	
							&nbsp;&nbsp;&nbsp;&nbsp;
					<% } %>

					<script language="Javascript">
						function resetForm() {
							document.form.reset();
							comboPair.populate("<%=docComboPair.getCodRef()%>", "<%=doc.getCodTipoDocumento()%>");
							document.form.codTipoDocumento.value = "<%=doc.getCodTipoDocumento()%>";
							flagChanged = false;
						}
					</script>
					<input type="button" class="pulsanti" value="Annulla"
					       onClick="resetForm()"/>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
	    <% } %>
	    
	    <%
	    	if(canRiFirmaGrafometrica){
				if(!isDocumentoGiaFirmatoGrafometricamente){ %>
					
					<tr>
						<td colspan="2" align="center">
							<input type="button" name="redoFirma" class="pulsanti" value="Firma Grafometrica" onclick='firmaGrafometricamente()' />
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
						
				<%}
			}%>

		<% if ( (!isNewDoc && !forzaSolaLettura) && nuovoDocVisibile) { %>
			<tr>
				<td>
					<input type="button" name="nuovoB" class="pulsanti" value="  Nuovo documento  "
							onClick="nuovo()" />
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
		<% } %>
		
		

		<% if (StringUtils.isFilled(goBackButtonTitle)) { %>
			<tr>
				<td>
					<input type="button" name="torna" class="pulsanti" value="  <%= goBackButtonTitle %>  "
							onClick="goBackSomewhere()" />
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
		<% } %>
		
		<% if (popUp.equalsIgnoreCase("true")) { %>
			<tr>
				<td>
					<input class="pulsanti" type="button" value="Chiudi"
					       onClick="closePopupAndRefresh()" />
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
		<% } %>

</table>
<%= htmlStreamBottom %>
<% if (! isNewDoc) operatoreInfo.showHTML(out); %>

</af:form>

<af:form name="formFirma" action="AdapterHTTP" method="POST">
	<input type="hidden" name="PAGE" value="RedoFirmaGrafometricaPage" />
	<input type="hidden" name="prgDocumento" value="<%=doc.getPrgDocumento()%>" />
	<input type="hidden" name="cdnFunzione" value="<%=cdnfunzione%>" />
	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
	<input type="hidden" name="QUERY_STRING" value="<%=queryString %>" />
</af:form>

<% if (salvaDIDTemporanea) {%>
	<script language="javascript">
		document.form.action = document.form.action + "SALVADATIDID=true&";
	</script>
<%}%>


</body>
</html>
