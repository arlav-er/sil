<!-- PAGINA PER LA RETTIFICA DELLA PARTE GENERALE -->
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
	// NOTE: Attributi della pagina (pulsanti e link) 
	PageAttribs attributi = new PageAttribs(user, "MovDettaglioGeneraleRettificaPage");
	boolean canModify = attributi.containsButton("RETTIFICA");
	boolean canModifyProtocol = attributi.containsButton("SALVAPROTOCOLLO");  
	boolean canModifyStato = attributi.containsButton("SALVA");
	String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	
	//Oggetti per l'applicazione dello stile
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
	//Guardo il contesto in cui opero
	String currentcontext = "inserisci";
	boolean inserisci = false;
	boolean salva = false;
	boolean valida = false;
	boolean rettifica = true;
	
	//Guardo da dove provengo (da una linguetta o da un dettaglio di un movimento da rettificare)
	String provenienza = StringUtils.getAttributeStrNotNull(serviceRequest, "PROVENIENZA");   
	boolean daLinguetta =  provenienza.equalsIgnoreCase("linguetta");
	String strDisabile = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATOREDISABILE");
	//Spiano l'oggetto del movimento in sessione se non arrivo da una linguetta
	if (!daLinguetta) {
		sessionContainer.delAttribute("MOVIMENTOCORRENTE");
	}
  %>
  <%@ include file="../GestioneOggettoMovimento.inc" %> 
  <%@ include file="../common/include/DichiarazioneVariabili.inc" %> 
  <%
  
	//Inserisco nell'oggetto in sessione i dati dell'apprendistato del movimento originale
	mov.setFieldsFromSourceBean((SourceBean)serviceResponse.getAttribute("M_MovGetApprendistato.ROWS.ROW"));
  
  	String codCpiLav = "";
	String codMonoMovDichRett = "";
	String codStatoAttoRett = "";
	String datComunicazRett = "";
	String numGGTraMovComunicazRett = "";
	String prgMovimentoRett = "";  	
	String numKloMovRett = "";
	String codCpi = "";
    String codMotAnnullamentoRett = "";
    String codMotAnnullamento = "";
    	  	
	//Variabili per la gestione della protocollazione ================
	String prAutomatica = null;
	String estReportDefautl = null;
	BigDecimal numProtV = null;
	BigDecimal numAnnoProtV = null;
	BigDecimal prgDocumento = null;
	String datProtV = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
	String oraProtV = (new SimpleDateFormat("HH:mm")).format(new Date());
	String docInOut = "I";
	String docRif = "Movimenti amministrativi";
	BigDecimal kLockProt = null;
	boolean numProtEditable = false;
	Vector rows = null;
	SourceBean row = null;
	Vector vectRicercaAz = null;
	Vector vectRicercaUnitaAz = null;
	Vector vectRicercaLav = null;
	//FINE Variabili per la gestione della protocollazione================
	
	//Giovanni D'Auria 21/02/2005 inizio
	String codOrario="";
	String numOreSett="";
	//fine
	String codQualificaSrq = "";
  //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
  String codtipoenteprev = "";
  String strcodiceenteprev = "";
  String flgsocio = "";
  String codtipotrasf = "";
  String codtipocontratto = "";
  String strnumagsomm = "";
  String decindensom = "";
  String datiniziomissione = "";
  String datfinemissione = "";
  String strrischioasbsil = "";
  String decvocetar1 = null;
  String decvocetar2 = null;
  String decvocetar3 = null;
  String codsoggetto = "";
  String flgTrasferimento = "";
  String datFineDistacco = "";

	if (!daLinguetta) {
  		//Attivo la cache in sessione
  		mov.enable();
  		//Preimposto la sessione con i dati del movimento sul DB
  		mov.setFieldsFromSourceBean((SourceBean) serviceResponse.getAttribute("M_MovGetDettMov.ROWS.ROW"));
  	}
	decMonoProv = "Movimento inserito <strong>Manualmente</strong>";
	strEnteRilascio = StringUtils.getAttributeStrNotNull(serviceRequest, "STRENTERILASCIO");

	//Numero protocollo da applicare se in inserimento nuovo
	rows = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
	if (rows != null && !rows.isEmpty()) {
		row = (SourceBean) rows.elementAt(0);
		prAutomatica = (String) row.getAttribute("FLGPROTOCOLLOAUT");
		if (prAutomatica.equalsIgnoreCase("N")) {
			numProtEditable = true;
		}
		estReportDefautl = (String) row.getAttribute("CODTIPOFILEESTREPORT");
		numProtV     = SourceBeanUtils.getAttrBigDecimal(row, "NUMPROTOCOLLO", null);
		numAnnoProtV = (BigDecimal) row.getAttribute("NUMANNOPROT");
		kLockProt    = (BigDecimal) row.getAttribute("NUMKLOPROTOCOLLO");
	}
	
	//Progressivo Documento associato al movimento
	 rows = serviceResponse.getAttributeAsVector("DettagliDocumentoDaPrgMov.ROWS.ROW");
     if((rows != null) && (rows.size()>0))
     {
       row = (SourceBean)rows.get(rows.size()-1);
       prgDocumento = (BigDecimal) row.getAttribute("prgdocumento");
     }
  
   String strLavCollMirato = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATORECOLLMIRATO");
	//L'origine dei dati è sempre la sessione
	SourceBean dataOrigin = mov.getFieldsAsSourceBean();
	//Estraggo i dati della pagina dall'origine
	if (dataOrigin != null) {
		if (strEnteRilascio.equals("")) {
  			strEnteRilascio = StringUtils.getAttributeStrNotNull(dataOrigin, "strEnteRilascio");
  		}
		codStatoAtto =  StringUtils.getAttributeStrNotNull(dataOrigin, "CODSTATOATTO");
		//Progressivi se ci sono
		prgMovimento = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTO");
		numKloMov = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMKLOMOV");
		prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
		numKloMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMKLOMOVPREC");
		prgMovimentoSucc = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOSUCC");
		prgMovimentoRett = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTORETT");
		numKloMovRett = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMKLOMOVRETT");		
		prgMovimentoApp = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOAPP");
		prgAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDA");
		prgUnita = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITA");
		cdnLavoratore = StringUtils.getAttributeStrNotNull(dataOrigin, "CDNLAVORATORE");
		if (cdnLavoratore.equals("null")) cdnLavoratore = "";
		prgAziendaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDAUTILIZ");
		prgUnitaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITAUTILIZ");
		numContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTNUMCONTRATTO");
		dataInizio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTINIZIOCONTRATTO");
		dataFine = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTFINECONTRATTO");
		legaleRapp = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTRAP");
		datFineDistacco = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEDISTACCO");
		numSoggetti = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMAZINTSOGGETTI");
		classeDip = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMAZINTDIPENDENTI");
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
		codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo");
		datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov");
		strLuogoDiLavoro = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLUOGODILAVORO");
		dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");
		dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");
		datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
		codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE");
		codMonoMovDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOMOVDICH");
		//Devo riportare il codTipoAss in cima alla pagina e passarlo agli altri dettagli e all'inserimento
		codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss");
		codMonoTempoAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTEMPOAVV");
		codtipomovprec = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOVPREC");
		strReferente = StringUtils.getAttributeStrNotNull(dataOrigin, "STRREFERENTE");
		codTipoTitoloStudio = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOTITOLOlav");
		
		//Giovanni D'Auria 21/02/2005 inizio
		codOrario = StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");
		numOreSett = StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");
		//fine
		codQualificaSrq = StringUtils.getAttributeStrNotNull(dataOrigin, "CODQUALIFICASRQ");
		//17/01/2001 Davide: aggiungiunti campi inerenti l'agricoltura
	    codCategoria   = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCATEGORIA");
	    codLavorazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODLAVORAZIONE");

    //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
    codtipoenteprev     = StringUtils.getAttributeStrNotNull(dataOrigin,"CODENTE");
    strcodiceenteprev   = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEENTEPREV");
    codtipotrasf        = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOTRASF");
    codtipocontratto    = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOCONTRATTO");
    strnumagsomm        = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNUMAGSOMMINISTRAZIONE");
    decindensom         = StringUtils.getAttributeStrNotNull(dataOrigin,"DECINDENSOM");
    //datiniziomissione   = StringUtils.getAttributeStrNotNull(dataOrigin,"DATINIZIORAPLAV");
    //datfinemissione     = StringUtils.getAttributeStrNotNull(dataOrigin,"DATFINERAPLAV");
    strrischioasbsil    = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGRISCHIOSIAS");
    decvocetar1         = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR1");
    decvocetar2         = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR2");
    decvocetar3         = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR3");
    codsoggetto         = StringUtils.getAttributeStrNotNull(dataOrigin,"CODSOGGETTO");
    flgsocio            = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGSOCIO");
	}
   
	//Ricreo i dati dell'azienda da visualizzare
	if (!prgAzienda.equals("")
		&& !prgUnita.equals("")
		&& !prgAzienda.equals("null")
		&& !prgUnita.equals("null")) {
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
		valDisplay = "inline";
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
    }
 
	//Se non arrivo da una linguetta devo fare alcune elaborazioni
	if (!daLinguetta) {
		//Il numKloMov ed il progressivo del movimento estratto è quello del movimento da rettificare
		prgMovimentoRett = prgMovimento;
		prgMovimento = "";
		numKloMovRett = numKloMov;
		numKloMov = "";	
		codMonoMovDichRett = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMONOMOVDICH");
		codStatoAttoRett = StringUtils.getAttributeStrNotNull(serviceRequest, "CODSTATOATTO");
		datComunicazRett = StringUtils.getAttributeStrNotNull(serviceRequest, "DATCOMUNICAZ");
		numGGTraMovComunicazRett = StringUtils.getAttributeStrNotNull(serviceRequest, "NUMGGTRAMOVCOMUNICAZ");
		codMotAnnullamentoRett = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMOTANNULLAMENTO");
	}

	//Estraggo dal DB i dati sui movimenti precedente e successivo e setto i boolean
	boolean precedente = false;
	boolean successivo = false;
	boolean isCollegato = false;
	InfMovimentoCollegato infMovPrec = new InfMovimentoCollegato(prgMovimentoPrec);
	InfMovimentoCollegato infMovSucc = new InfMovimentoCollegato(prgMovimentoSucc);
	if (infMovPrec.exists()) {
		precedente = true;
		isCollegato = true;
		numKloMovPrec = infMovPrec.getNumKloMov();
		codtipomovprec = infMovPrec.getCodTipoMov();
		dataFineMovPrec = infMovPrec.getDatFineMov();
		if (dataFineMovPrec == null) {
			dataFineMovPrec = "";
		}
	}
	if (infMovSucc.exists()) {
		successivo = true;
		isCollegato = true;
	}
	//Aggiorno il valore di collegato 
	String collegato = "nessuno";
	if (precedente && successivo) {
		collegato = "entrambi";
	} else if (precedente) {
		collegato = "precedente";
	} else if (successivo) {
		collegato = "successivo";
	}
 
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
	posInps2 = posInps.substring((posInps.length() >= 2 ? 2 : posInps.length()), 
								 (posInps.length() >= 15 ? 15 : posInps.length()));
	//Gestione spezzatino Pat Inail
	patInail1 = patInail.substring(0, (patInail.length() >= 8 ? 8 : patInail.length()));
	patInail2 = patInail.substring((patInail.length() >= 8 ? 8 : patInail.length()),
								   (patInail.length() >= 10 ? 10 : patInail.length()));

	if (strFlgDatiOk != null) {
		if (strFlgDatiOk.equalsIgnoreCase("S")) {
			strFlgDatiOk = "Si";
		} else if (strFlgDatiOk.equalsIgnoreCase("N")) {
			strFlgDatiOk = "No";
		}
	}
	if (strFlgCfOk != null) {
		if (strFlgCfOk.equalsIgnoreCase("S")) {
			strFlgCfOk = "Si";
		} else if (strFlgCfOk.equalsIgnoreCase("N")) {
			strFlgCfOk = "No";
		}
	}  
	
	//Substring delle info troppo lunghe
	String strRagioneSocialeAzTrunc = strRagioneSocialeAz;
	if (strRagioneSocialeAz.length() >= 30) {
		strRagioneSocialeAzTrunc = strRagioneSocialeAz.substring(0, 26) + "...";
	}
	if (strDesAtecoUAz.length() >= 22) {
		strDesAtecoUAz = strDesAtecoUAz.substring(0, 19) + "...";
	}
	if (natGiuridicaAz.length() >= 30) {
		natGiuridicaAz = natGiuridicaAz.substring(0, 26) + "...";
	}
  
  //Variabili per la gestione delle linguette con file di include
  String codTipoMovCorr = codTipoMov;
  String codTipoMovPrec = codtipomovprec;
  boolean consulta = false;
%>
 
<html>
  <head>
    <%@ include file="../../global/fieldChanged.inc" %>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    <title>Dettaglio Movimento</title>
    <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
    var aziendaUtilVar = "N";
    <% if ( (!prgAziendaUtil.equals("")) && (!prgUnitaUtil.equals("")) ){%>
      aziendaUtilVar = "S";
    <%}%>
 

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
    var inserisci = 'false';
    var contesto = 'rettifica';
    var precedente = <%=precedente%>;
    var prgAziendaS = '<%=prgAzienda%>';
    var prgUnitaS = '<%=prgUnita%>';
    


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
    
    </SCRIPT>    
    <script type="text/javascript" src="../../js/movimenti/generale/DatiLavoratore.js" language="JavaScript"></script>
    <%-- gestione controllo dati lavoratore (se eta <15, se in mobilita o in collocamento mirato) --%>
    <%@ include file="include/_functionControlloDatiLavoratore.inc" %>
    
    <%@ include file="include/_functionGestPrec.inc" %>
    <!-- Gestione Profili -->
    <%@ include file="../common/include/_gestioneProfili.inc" %> 
    
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiRicercaSoggetto.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/apriAziendaUtil.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/apriAziendaMov.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/_lavoratore.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/Linguette.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/func_generale.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/Avanti.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>

  </head>
 
  <body class="gestione" onload="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');rinfresca();visualizzaInterinali('<%=codTipoAzienda%>', <%=assInterna%>);gestioneProfilo(document.Frm1.CODTIPOMOV);controllaInfoLavoratore();">

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
                <% if (!isCollegato && !daLinguetta) {%> 
                 <a id='ricercaAzienda' href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda', '', '', '');"><img src="../../img/binocolo.gif" alt="Cerca azienda nel DB"></a>
        		 &nbsp;<a href="#" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Scollega azienda"></a>
                 <div id="DettaglioSedeAzienda" style="position:absolute; display:<%=valDisplay%>">
                 &nbsp;
                <%}%>
                  <a href="#" onClick="javascript:apriUnitaAziendale(document.Frm1.PRGAZIENDA.value,document.Frm1.PRGUNITA.value,<%=_funzione%>,'0');"><img src="../../img/detail.gif" alt="Dettaglio azienda"></a>
                </div>
              </div>
            </td>
          </tr>       
          <!-- sezione riservata all'azienda che effettua il movimento e eventuale aziennda util. -->
          <%@ include file="include/azienda.inc" %>
          <tr>
            <td>         
              <div class='sezione2' id='lavoratore'>
                <img id='tendinaLavoratore' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("datiLavoratore"));'/>
                Lavoratore&nbsp;&nbsp;
                <%
                  if (!isCollegato && !daLinguetta) {%>                
                    <a id='ricercaLavoratore' href="#" onClick="javascript:apriSelezionaSoggetto('Lavoratori', 'aggiornaLavoratore', '', '', '');"><img src="../../img/binocolo.gif" alt="Cerca lavoratore nel DB"></a>
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
    <input type="hidden" name="RESETCFL" value="false"/>	
    
    <input type="hidden" name="CODTIPOASS" value="<%=codTipoAss%>"/>
    <input type="hidden" name="CODQUALIFICASRQ" value="<%=codQualificaSrq%>"/>
    <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
    <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
    <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
    <input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=prgAziendaUtil%>"/>
    <input type="hidden" name="PRGUNITAUTILIZ" value="<%=prgUnitaUtil%>"/>
    <input type="hidden" name="STRAZINTNUMCONTRATTO" value="<%=numContratto%>">
    <input type="hidden" name="DATAZINTINIZIOCONTRATTO" value="<%=dataInizio%>">
    <input type="hidden" name="DATAZINTFINECONTRATTO" value="<%=dataFine%>">
    <input type="hidden" name="STRAZINTRAP" value="<%=legaleRapp%>">
    <input type="hidden" name="NUMAZINTSOGGETTI" value="<%=numSoggetti%>">
    <input type="hidden" name="NUMAZINTDIPENDENTI" value="<%=classeDip%>">
    <input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>"/>
    <input type="hidden" name="CODTIPOAZIENDAUTIL" value="<%=codTipoAziendaUtil%>"/>
    <input type="hidden" name="DATAINIZIOAVV" value="<%=dataInizioAvv%>"/>
    <input type="hidden" name="DATINIZIOMOVPREC" value="<%=dataInizioMovPrec%>"/>     
    <input type="hidden" name="CODMONOTEMPOAVV" value="<%=codMonoTempoAvv%>"/>
    <input type="hidden" name="DATFINEMOVPREC" value="<%=dataFineMovPrec%>"/>
    <input type="hidden" name="DATFINEMOVEFFETTIVA" value="<%=datFineMovEff%>"/>      
    <input type="hidden" name="CODMONOTIPOFINE" value="<%=codMonoTipoFine%>"/>
    <input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>      
    <input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
    <input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>
    <input type="hidden" name="ACTION" value="aggiorna"/>
    <input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
    <input type="hidden" name="COLLEGATO" value="<%=collegato%>"/>
    <input type="hidden" name="PAGE" value="MovDettaglioGeneraleRettificaPage"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
    <input type="hidden" name="CODCPILAV" value="<%=codCpiLav%>"/>
    <input type="hidden" name="CODCPI" value="<%=codCpi%>"/>       
    <input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
    <input type="hidden" name="CODMONOPROV" value="M"/>
    <input type="hidden" name="NUOVODOCUMENTO" value=""/>
    <input type="hidden" name="strRagioneSocialeAzUtil" value="<%=strRagioneSocialeAzUtil%>"/>
    <input type="hidden" name="strIndirizzoUAzUtil" value="<%=strIndirizzoUAzUtil%>"/>
    <input type="hidden" name="strComuneUAzUtil" value="<%=strComuneUAzUtil%>"/>
    <%-- 17/01/2006 Davide gestione dei campi in caso di giorni in agricoltura   --%>
    <input type="hidden" name="CODCATEGORIA" value="<%=codCategoria%>"/>
    <input type="hidden" name="CODLAVORAZIONE" value="<%=codLavorazione%>"/>
	<!--Gestione comunicazioni in ritardo per lavoratori disabili-->
	<input type="hidden" name="LAVORATOREDISABILE" value="<%=strDisabile%>"/>
	<input type="hidden" name="LAVORATORECOLLMIRATO" value="<%=strLavCollMirato%>"/>

    <!--Dati relativi al documento associato al movimento -->
    <input type="hidden" name="PRGDOCUMENTO" value="<%=prgDocumento%>"/>
  <!-- DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO -->
  <input type="hidden" name="CODENTE"     value="<%=codtipoenteprev%>" />
  <input type="hidden" name="STRCODICEENTEPREV"   value="<%=strcodiceenteprev%>" />
  <input type="hidden" name="CODTIPOTRASF"        value="<%=codtipotrasf%>" />
  <input type="hidden" name="DATFINEDISTACCO"    value="<%=datFineDistacco%>" />
  <input type="hidden" name="CODTIPOCONTRATTO"    value="<%=codtipocontratto%>" />
  <input type="hidden" name="STRNUMAGSOMMINISTRAZIONE"        value="<%=strnumagsomm%>" />
  <input type="hidden" name="DECINDENSOM"     value="<%=decindensom%>" />
  <!--<input type="hidden" name="DATINIZIORAPLAV"   value="<%=datiniziomissione%>" />
  <input type="hidden" name="DATFINERAPLAV"     value="<%=datfinemissione%>" />-->
  <input type="hidden" name="FLGRISCHIOSIAS"    value="<%=strrischioasbsil%>" />
  <input type="hidden" name="DECVOCETAR1"     value="<%=decvocetar1%>" />
  <input type="hidden" name="DECVOCETAR2"     value="<%=decvocetar2%>" />
  <input type="hidden" name="DECVOCETAR3"     value="<%=decvocetar3%>" />
  <input type="hidden" name="CODSOGGETTO"         value="<%=codsoggetto%>" />
  <input type="hidden" name="FLGSOCIO"            value="<%=flgsocio%>" />
  <input type="hidden" name="FLGTRASFER" value="<%=flgTrasferimento%>"/>

	<%if (!daLinguetta) { 
	//Alla prima visulizzazione inserisco nell'oggetto in sessione i dati del movimento da rettificare%>
    <input type="hidden" name="PRGMOVIMENTORETT" value="<%=prgMovimentoRett%>"/>
    <input type="hidden" name="NUMKLOMOVRETT" value="<%=numKloMovRett%>"/>
    <input type="hidden" name="CODMONOMOVDICHRETT" value="<%=codMonoMovDichRett%>"/>
    <input type="hidden" name="CODSTATOATTORETT" value="<%=codStatoAttoRett%>"/>
    <input type="hidden" name="DATCOMUNICAZRETT" value="<%=datComunicazRett%>"/>
    <input type="hidden" name="NUMGGTRAMOVCOMUNICAZRETT" value="<%=numGGTraMovComunicazRett%>"/>
    <input type="hidden" name="CODMOTANNULLAMENTORETT" value="<%=codMotAnnullamentoRett%>"/>    
	<%}%>

    <!-- Sezione di gestione impatti tenendo conto della profilatura -->
    <input type="hidden" name="permettiImpatti" value="" />
    <input type="hidden" name="CANVIEW" value="" />
    <input type="hidden" name="CANEDITLAV" value="" />
    <input type="hidden" name="CANEDITAZ" value="" />
	<center>
    <%@ include file="../common/include/GestioneCollegati.inc" %>        
  	<%@ include file="../common/include/PulsanteRitornoLista.inc" %>
    </center>
    </center> 
    </af:form>     
  <script language="javascript">
    if (aziendaUtilVar != "N"){
      document.Frm1.STRLUOGODILAVORO.readOnly = true;
    }
  </script>
 </body>
</html>