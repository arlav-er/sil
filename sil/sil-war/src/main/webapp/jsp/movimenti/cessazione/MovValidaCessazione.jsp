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
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "MovValidaCessazionePage");
  boolean canModify = attributi.containsButton("SALVA");
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");

  //Oggetti per l'applicazione dello stile
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

  //Guardo il contesto in cui opero
  String currentcontext = "valida";
  
  if (serviceRequest.containsAttribute("CURRENTCONTEXT")) {
  	String context = StringUtils.getAttributeStrNotNull(serviceRequest,"CURRENTCONTEXT");
  	if (context.equals("validaArchivio")) {
  		currentcontext = context;
  	}
  }
  
  boolean inserisci = false;
  boolean salva = false;
  boolean valida = true;

  //guardo se provengo da un salvataggio o da un'altra linguetta
  String action = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTION");
  boolean aggiorna = false;
  boolean naviga = action.equalsIgnoreCase("naviga");
  boolean consulta = false;
  boolean rettifica = false;

  //guardo se precedentemente ero in consultazione
  String actionprec = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTIONPREC");
  boolean actionprecaggiorna = false;
  boolean actionprecnaviga = false;
  boolean actionprecconsulta = false;

  //Il boolean seguente indica che si sta eseguendo un inserimento collegato
  boolean insertCollegato = false;
  String collegato = StringUtils.getAttributeStrNotNull(serviceRequest, "COLLEGATO");
  String strDisabile = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATOREDISABILE");
  String forzaInsEtaApprendista = StringUtils.getAttributeStrNotNull(serviceRequest, "FORZA_INSERIMENTO_ETA_APPRENDISTATO");
  if (forzaInsEtaApprendista.equals("")) forzaInsEtaApprendista = "false";
%>
<%@ include file="../common/include/_segnalazioniGgRit.inc" %>
<%@ include file="../GestioneOggettoMovimento.inc" %> 
<%
  //inizializzazione variabili
  String prgMovimento = "";
  String prgMovimentoApp = "";
  String prgAzienda = "";
  String prgUnita = "";
  String codAtecoUAz = "";
  String cdnLavoratore = "";
  String datComunicaz = "";
  String codTipoMov = "";
  String datInizioMov = "";
  String datFineMov = "";
  String codMvCessazione = "";
  
  String codMansione = "";
  String codGrado = "";
  String numLivello = "";
  String descrTipoMansione = "";
  String descrMansione = "";

  String numKloMov = "";
  String numKloMovPrec = "";  
  String codTipoAzienda = "";
  String strNumAlboInterinali = "";
  String strNumRegistroCommitt = ""; 
  String dataInizioAvv = "";
  String dataInizioMovPrec = ""; 
  String codMonoTempoAvv = "";
  String codMonoTempo = "";
  String prgMovimentoPrec = "";
  String prgMovimentoSucc = "";
  String codtipomovprec = "";
  String prgAziendaUtil = ""; 
  String prgUnitaUtil = "";
  String codMonoMovDich = ""; 
  String datFineMovEff = "";
  String codMonoTipoFine = "";
  String codTipoAss = "";
  String numGgTraMovComunicaz = "";
  String codMotAnnullamento = "";
  
  String codStatoAtto = "";
  String numProtocolloV = "";  
  String annoProtV = "" ;
  String kLockProt = "";
  String strEnteRilascio = "";
  String flgAutocertificazione = "N";
  String strRagioneSocialeAzUtil = "";
  String numContratto = "";
  String dataInizio = "";
  String dataFine = "";
  String legaleRapp = "";
  String numSoggetti = "";
  String classeDip = "";
  boolean movIsEnabled = false;
  String numGGEffettuatiAgr = null;
  String datInizioAVVperCVE = "";
  String strOnSubmit= "";
  String codTipoComunicazione = "";
  
  //Giovanni D'Auria 21/02/2005 inizio
		String codOrario="";
		String numOreSett="";
	//fine

  //
  String codMonoTipo = "";
  String strCodiceFiscaleLav = "";
  String strCodiceFiscaleAz  = "";
  //Variabili per la gestione della protocollazione
  BigDecimal prgDoc = null;
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String     datProtV     = "";
  String     oraProtV     = "";
  String     docInOut     = "";
  String     docRif       = "";
  String     codStatoAttoV = "";
  //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
  String codtipodocex = "";
  String strnumdocex = "";
  String codmotivopermsoggex = "";
  String codtipoenteprev = "";
  String strcodiceenteprev = "";
  String flgsocio = "";
  String codtipotrasf = "";
  String codtipocontratto = "";
  String strnumagsomm = "";
  String strindennitasom = "";
  String datiniziomissione = "";
  String datfinemissione = "";
  String strrischioasbsil = "";
  String strvocetariffa1 = "";
  String strvocetariffa2 = "";
  String strvocetariffa3 = "";
  String codsoggetto = "";
  //
  // recupero informazioni protocollazione
  numProtV = SourceBeanUtils.getAttrBigDecimal(serviceRequest, "numProtocollo", null);
  datProtV = StringUtils.getAttributeStrNotNull(serviceRequest, "dataProt");
  oraProtV = StringUtils.getAttributeStrNotNull(serviceRequest, "oraProt");
  docInOut     = "I";
  docRif       = "Movimenti amministrativi";
  codStatoAtto = "";
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
  //Recupero CODCPI
  int cdnUt = user.getCodut();
  int cdnTipoGruppo = user.getCdnTipoGruppo(); 
  String codCpi="";
  if(cdnTipoGruppo == 1) {
    codCpi =  user.getCodRif();
  }
  //---------------
  String confermaMovSimili = StringUtils.getAttributeStrNotNull(serviceRequest, "CONFERMA_CONTROLLO_MOV_SIMILI");
  if (confermaMovSimili.equals("")) confermaMovSimili="false";
  //Setto l'origine dei dati generali da recuperare in caso di inserimento o validazione, 
  //mi arrivano dall'oggetto in sessione se sto inserendo e l'oggetto è abilitato,
  //altrimenti dalla request
  SourceBean dataOrigin = serviceRequest;
  if (mov.isEnabled()) {
    dataOrigin = mov.getFieldsAsSourceBean();
    //leggo il campo numGGEffettuatiAgr dall' oggetto in sessione
    numGGEffettuatiAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGEFFETTUATIAGR");
  }
  //Gestione dei movimenti collegati
  prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
  prgMovimentoSucc = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOSUCC"); 

  //Recupero dati sempre presenti nella request
  dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");
  codMonoTempoAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTEMPOAVV");
  codMonoMovDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOMOVDICH");
  if (codMonoMovDich.equalsIgnoreCase("C")){
    flgAutocertificazione = "S";
  }

  numProtocolloV = SourceBeanUtils.getAttrStrNotNull(dataOrigin, "numProtocollo");
  annoProtV = StringUtils.getAttributeStrNotNull(dataOrigin, "numAnnoProt");
  kLockProt = StringUtils.getAttributeStrNotNull(dataOrigin, "KLOCKPROT");
  strEnteRilascio = StringUtils.getAttributeStrNotNull(dataOrigin, "STRENTERILASCIO");
  codMotAnnullamento = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMOTANNULLAMENTO");
  codStatoAtto = StringUtils.getAttributeStrNotNull(dataOrigin, "CODSTATOATTO");
  codStatoAttoV = codStatoAtto;
  //Il codipoMov serve nel caso che l'utente lo abbia cambiato ;è sempre letto dalla request  
  codTipoMov = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOV");
  //Devo riportare il codTipoAss in cima alla pagina e passarlo agli altri dettagli 
  //e all'inserimento
  codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss"); 
 
  //In caso di validazione estraggo i dati necessari dalla request 
  //(per ora quelli di azienda e lavoratore sono immutabili,
  //quindi non li estraggo...)
  if (dataOrigin != null) {
    datComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin, "datComunicaz");
  }
 
  //Setto l'origine dei dati da visualizzare, in fase di consultazione mi arrivano dal DB
  if (!mov.isEnabled()) { 
    dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovApp.ROWS.ROW");
    //leggo il campo numGGEffettuatiAgr dal db(prima da NUMGGEFFETTUATIAGR e poi da NUMGGPREVISTIAGR)
    numGGEffettuatiAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGEFFETTUATIAGR");
    if (numGGEffettuatiAgr.equals("")) {
    	numGGEffettuatiAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGPREVISTIAGR");
    }
  }
  //
  codMonoTipo = StringUtils.getAttributeStrNotNull(serviceResponse, "M_GetTipoAss.rows.row.codMonoTipo"); 
    strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull( serviceRequest, "STRCODICEFISCALELAV");
  strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull( serviceRequest, "STRCODICEFISCALEAZ");
  if (strCodiceFiscaleLav.equals(""))
  	strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull(dataOrigin, "STRCODICEFISCALELAV");
  if (strCodiceFiscaleAz.equals(""))
    strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull(dataOrigin, "STRCODICEFISCALEAZ");
  if (dataOrigin != null) {
    //I dati di azienda e lavoratore servono per le informazioni all'inizio della pagina
    /*
    prgAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "prgAzienda");
    prgUnita = StringUtils.getAttributeStrNotNull(dataOrigin, "prgUnita");
    */
    cdnLavoratore = StringUtils.getAttributeStrNotNull(dataOrigin, "cdnLavoratore");
    //Dati per le informazioni di testata nella tabella principale del dettaglio
    dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");    
    datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
    codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE"); 
    numGgTraMovComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGTRAMOVCOMUNICAZIONE");
    prgMovimentoApp = StringUtils.getAttributeStrNotNull(dataOrigin, "prgMovimentoApp");
    datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov");
    codMvCessazione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMvCessazione");
    codMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMansione");   
    codGrado = StringUtils.getAttributeStrNotNull(dataOrigin, "codGrado"); 
    codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOAZIENDA");
    numLivello = StringUtils.getAttributeStrNotNull(dataOrigin, "numLivello");   
    descrMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrMansione");   
    descrTipoMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoMansione");
    //lettura tipo comunicazione
    codTipoComunicazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOCOMUNIC");
    //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
    codtipodocex        = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPODOCEX");
    strnumdocex         = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNUMDOCEX");
    codmotivopermsoggex = StringUtils.getAttributeStrNotNull(dataOrigin,"CODMOTIVOPERMSOGGEX");
    codtipoenteprev     = StringUtils.getAttributeStrNotNull(dataOrigin,"CODENTE");
    strcodiceenteprev   = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEENTEPREV");
    codtipotrasf        = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOTRASF");
    codtipocontratto    = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOCONTRATTO");
    strnumagsomm        = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNUMAGSOMMINISTRAZIONE");
    strindennitasom     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECINDENSOM");
    if (codTipoAzienda.equalsIgnoreCase("INT")) {
    	datiniziomissione = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIORAPLAV");
    	datfinemissione = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINERAPLAV");
   	}
    strrischioasbsil    = StringUtils.getAttributeStrNotNull(dataOrigin,"STRRISCHIOASBSIL");
    strvocetariffa1     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR1");
    strvocetariffa2     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR2");
    strvocetariffa3     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR3");
    codsoggetto         = StringUtils.getAttributeStrNotNull(dataOrigin,"CODSOGGETTO");
    
    //Giovanni D'Auria 21/02/2005 inizio
		codOrario = StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");
		numOreSett = StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");
	//fine
    
  }
  
  //i dati sui movimenti precedente e successivo sono recuperati nel frame collegati
  boolean precedente = false;
  boolean successivo = false;  
  if(collegato.equalsIgnoreCase("nessuno")){
  	//Imposto i valori di default di alcune proprietà
  	codTipoAss = "NO0";
  	codMonoTempo = "I";	
  }
  movIsEnabled = mov.isEnabled();
  //abilito l'oggetto se non era già abilitato
  if (!mov.isEnabled()) {
    mov.enable();
  }
  
  //Recupero la data di inizio dell'avviamento collegato alla cessazione (se non c'è ho una stringa vuota)
  datInizioAVVperCVE = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovGetDettMovAppCVE.ROWS.ROW.datInizioMov");
  
  //Mi dice se devo mostrare il campo della data di inizio avviamento
  boolean mostraCampoInizioAvviamento = collegato.equalsIgnoreCase("nessuno") && !"".equals(datInizioAVVperCVE);

  prgAzienda = (String) mov.getField("prgAzienda");
  prgUnita = (String) mov.getField("prgUnita");
  
  if (codTipoComunicazione.equals("04")) { 
    //si tratta dell'annullamento di una comunicazione precedente 
    //e quindi bisogna eseguire solo questa funzione lato client
  	strOnSubmit = "checkTipoComunicazione(codTipoComunicaz)";
  }
  else {
  	if (codTipoComunicazione.equals("03")) {
  		//si tratta della rettifica di una comunicazione precedente 
		strOnSubmit = "checkTipoComunicazione(codTipoComunicaz) && ";
	} 
    if (mostraCampoInizioAvviamento) {
  		strOnSubmit = strOnSubmit + "checkDatInizioMovImpattiCessazione(document.Frm1.datInizioMov, document.Frm1.DATINIZIOMOVPREC) && controlloLavAutonomo()&& checkNumOreSettimanali() && checkOrario()";
  	}
  	else {
  		strOnSubmit = strOnSubmit + "checkDatInizioMovImpattiCessazione(document.Frm1.datInizioMov, document.Frm1.DATINIZIOMOVPREC) && controlloLavAutonomo()";
  	}
  }
  String queryString = null;
%>

<html>
<head>
  <%@ include file="../../global/fieldChanged.inc" %>
  <%@ include file="../../documenti/_apriGestioneDoc.inc"%> 
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
      <af:linkScript path="../../js/"/>
  <title>Dettaglio Movimento da Validare</title>
  <%@ include file="../../presel/Function_CommonRicercaCCNL.inc" %>    
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
  var consulta = <%=consulta%>;
  var precedente = <%=precedente%>;
  var oggettoInSessione = <%=movIsEnabled%>;
  var contesto = 'valida';
  var codTipoComunicaz = "<%=codTipoComunicazione%>";
  function stampaMovimento(prgMovimento){

	apriGestioneDoc('RPT_MOVIMENTI_DA_VALIDARE','&TIPOSTAMPA=DAVALIDARE&prgMovimento='+prgMovimento,'STMOV');
  }
  </SCRIPT>
  <%-- necessari per il controllo del lavoro autonomo --%>
    <script  language="Javascript">
		var codiceFiscaleLav = "<%=strCodiceFiscaleLav %>";
		var codiceFiscaleAz = "<%=strCodiceFiscaleAz %>";		
    </script>
  <script type="text/javascript" src="../../js/movimenti/common/Indietro.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/cessazione/func_cessazione.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/_confirmDaControlloMov.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/Linguette.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/_confirmDaStOcc.js" language="JavaScript"></script>

  
  <%@ include file="../common/include/calcolaDiffGiorni.inc" %>
  <%@ include file="../common/include/_funzioniGenerali.inc" %>
</head>

<body class="gestione" onload="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');rinfresca();calcolaDiffGiorni(document.Frm1.datInizioMov, document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange,oggettoInSessione);gestVisualGiorniRitardo(); visualizzaOreSett(document.Frm1.codOrario);">
<!-- Visualizzazione sezione errori -->
<%@ include file="../common/include/GestioneRisultati.inc" %>
<%@ include file="../common/include/LinguetteCessazione.inc" %>
<center>
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="<%=strOnSubmit%>">
<%out.print(htmlStreamTop);%>
<%@ include file="../../movimenti/common/include/_protocollazione.inc" %>
<table class="main">
    <%@ include file="../../movimenti/common/include/InfoTestataMovimento.inc" %>
	<%@ include file="../cessazione/include/campi_cessazione.inc" %>
</table>
<%out.print(htmlStreamBottom);%>
<!--Gestione comunicazioni in ritardo per lavoratori disabili-->
<input type="hidden" name="LAVORATOREDISABILE" value="<%=strDisabile%>"/>
<input type="hidden" name="PRGMOVIMENTOAPP" value="<%=prgMovimentoApp%>"/>
<input type="hidden" name="DATCOMUNICAZ" value="<%=datComunicaz%>"/>
<input type="hidden" name="CODTIPOASS" value="<%=codTipoAss%>"/>  
<input type="hidden" name="CODMONOTIPO" value="<%=codMonoTipo%>"/>
<%if(collegato.equalsIgnoreCase("nessuno")) {%>
	<input type="hidden" name="CODMONOTEMPO" value="<%=codMonoTempo%>"/>   	
<%}%>
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="NUMKLOMOV" value="<%=numKloMov%>"/>
<input type="hidden" name="CODTIPOMOV" value="<%=codTipoMov%>"/>      
<input type="hidden" name="DATAINIZIOAVV" value="<%=dataInizioAvv%>"/>
<input type="hidden" name="DATINIZIOMOVPREC" value="<%=dataInizioMovPrec%>"/> 
<input type="hidden" name="CODMONOTEMPOAVV" value="<%=codMonoTempoAvv%>"/>
<input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>"/>
<input type="hidden" name="STRNUMALBOINTERINALI" value="<%=strNumAlboInterinali%>"/> 
<input type="hidden" name="STRNUMREGISTROCOMMITT" value="<%=strNumRegistroCommitt%>"/>
<input type="hidden" name="CODMONOMOVDICH" value="<%=codMonoMovDich%>"/>
<input type="hidden" name="DATFINEMOVEFFETTIVA" value="<%=datFineMovEff%>"/>      
<input type="hidden" name="CODMONOTIPOFINE" value="<%=codMonoTipoFine%>"/>
<input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>
<input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
<input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>
<input type="hidden" name="CODMONOTIPOFINEPREC" value="C"/>   
<input type="hidden" name="ACTION" value="aggiorna"/>
<input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
<input type="hidden" name="ACTIONPREC" value="<%=action%>"/>
<input type="hidden" name="PROVENIENZA" value="linguetta"/>
<input type="hidden" name="PAGE" value="MovEffettuaValidazionePage"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<input type="hidden" name="FORZA_INSERIMENTO" value="false"/>
<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false"/>
<input type="hidden" name="FORZA_INSERIMENTO_ETA_APPRENDISTATO" value="<%=forzaInsEtaApprendista%>"/>
<%--
<input type="hidden" name="numProtocollo" value="<%=numProtocolloV%>"/>
<input type="hidden" name="numAnnoProt" value="<%=annoProtV%>"/>
--%>
<input type="hidden" name="KLOCKPROT"  value="<%=kLockProt%>">
<input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
<input type="hidden" name="FLGAUTOCERTIFICAZIONE" value="<%=flgAutocertificazione%>" />
<input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>" />
<input type="hidden" name="CONFERMA_CONTROLLO_MOV_SIMILI" value="<%=confermaMovSimili%>"/>
  <!-- DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO -->
  <input type="hidden" name="CODTIPODOCEX"        value="<%=codtipodocex%>" />
  <input type="hidden" name="STRNUMDOCEX"         value="<%=strnumdocex%>" />
  <input type="hidden" name="CODMOTIVOPERMSOGGEX" value="<%=codmotivopermsoggex%>" />
  <input type="hidden" name="CODENTE"     value="<%=codtipoenteprev%>" />
  <input type="hidden" name="STRCODICEENTEPREV"   value="<%=strcodiceenteprev%>" />
  <input type="hidden" name="FLGSOCIO"            value="<%=flgsocio%>" />
  <input type="hidden" name="CODTIPOTRASF"        value="<%=codtipotrasf%>" />
  <input type="hidden" name="CODTIPOCONTRATTO"    value="<%=codtipocontratto%>" />
  <input type="hidden" name="STRNUMAGSOMMINISTRAZIONE"        value="<%=strnumagsomm%>" />
  <input type="hidden" name="DECINDENSOM"     value="<%=strindennitasom%>" />
  <input type="hidden" name="STRRISCHIOASBSIL"    value="<%=strrischioasbsil%>" />
  <input type="hidden" name="DECVOCETAR1"     value="<%=strvocetariffa1%>" />
  <input type="hidden" name="DECVOCETAR2"     value="<%=strvocetariffa2%>" />
  <input type="hidden" name="DECVOCETAR3"     value="<%=strvocetariffa3%>" />
  <input type="hidden" name="CODSOGGETTO"         value="<%=codsoggetto%>" />
  <input type="hidden" name="COLLEGATO" value="<%=collegato%>"/>
<center>
<%
if (canModify) {%>
  <input type="submit" class="pulsanti" name="submitbutton" value="Valida" onclick="resetFlagForzatura();"/>
<%}%>&nbsp;
  <!--	Commentato per prossimo rilascio
  <input type="button" class="pulsanti" name="stampa" value="Stampa" onclick="stampaMovimento('<%//=prgMovimentoApp%>')"/>
  -->
<%@ include file="../common/include/GestioneCollegati.inc" %>
<%@ include file="../common/include/PulsanteRitornoLista.inc" %> 
</center>
</af:form>      
</center> 
</body>
<%@ include file="../common/include/GestioneScriptRisultati.inc" %>
</html>