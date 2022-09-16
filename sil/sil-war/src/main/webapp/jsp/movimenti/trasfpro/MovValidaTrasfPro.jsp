<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file ="../../global/noCaching.inc" %>
<%@ include file ="../../global/getCommonObjects.inc" %>

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
  PageAttribs attributi = new PageAttribs(user, "MovValidaTrasfProPage");
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
  boolean rettifica = false;

  //guardo se provengo da un salvataggio o da un'altra linguetta
  String action = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTION");
  boolean aggiorna = false;
  boolean naviga = action.equalsIgnoreCase("naviga");
  boolean consulta = false;

  //guardo se precedentemente ero in consultazione
  String actionprec = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTIONPREC");
  boolean actionprecaggiorna = false;
  boolean actionprecnaviga = false;
  boolean actionprecconsulta = false;

  //Il boolean seguente indica che si sta eseguendo un inserimento collegato
  boolean insertCollegato = false;
  String collegato = StringUtils.getAttributeStrNotNull(serviceRequest, "COLLEGATO");
  String queryString = null;
  String forzaInsEtaApprendista = StringUtils.getAttributeStrNotNull(serviceRequest, "FORZA_INSERIMENTO_ETA_APPRENDISTATO");
  if (forzaInsEtaApprendista.equals("")) forzaInsEtaApprendista = "false";
%>
<%@ include file ="../common/include/_segnalazioniGgRit.inc" %>
<%@ include file ="../GestioneOggettoMovimento.inc" %> 
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
  String codMonoTempo = "";
  String datFineMov = "";
  String codOrario = "";
  String numOreSett = "";
  String codMansione = "";
  String descrMansione = "";
  String descrTipoMansione = "";
  String strDesAttivita = "";
  String codGrado = "";
  String numLivello = "";
  String codAgevolazione = "";
  String codCCNL = "";
  String strDescrizioneCCNL = "";
  String numKloMov = "";
  String numKloMovPrec = "";
  String codTipoAzienda = "";
  String strNumAlboInterinali = "";
  String strNumRegistroCommitt = ""; 
  String codMonoTempoAvv = ""; 
  String dataInizioAvv = "";
  String dataInizioMovPrec = "";  
  String prgMovimentoPrec = "";
  String prgMovimentoSucc = "";
  String codtipomovprec = "";
  String prgAziendaUtil = ""; 
  String prgUnitaUtil = ""; 
  String luogoDiLavoro = "";
  String personaleInterno = "";
  String decRetribuzioneMen = "";
  String codMonoMovDich = "";  
  String datFineMovEff = "";
  String codMonoTipoFine = "";
  String codTipoAss = "";
  String numProroghe = "";
  String numGgTraMovComunicaz = "";
  String codMotAnnullamento = "";
  
  String decretribmensanata = "";
  String datasanata = "";
  String tipodichsanata = "";
  String datInizioMovSupReddito = "";
  String codiceDich = "";
  String prgDichLav = "";  
  String codStatoAtto = "";
  String numProtocolloV = "";  
  String annoProtV = "" ;
  String kLockProt = "";
  String strEnteRilascio = "";
  String flgAutocertificazione = "N";
  String numContratto = "";
  String dataInizio = "";
  String dataFine = "";
  String legaleRapp = "";
  String numSoggetti = "";
  String classeDip = "";
  String autorizzaDurataTD = "";
  boolean movIsEnabled = false;
  //
  String strCodiceFiscaleLav = "";
  String strCodiceFiscaleAz  = "";
  String codMonoTipo = "";
  //Variabili per la gestione della protocollazione
  BigDecimal prgDoc = null;
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String     datProtV     = "";
  String     oraProtV     = "";
  String     docInOut     = "";
  String     docRif       = "";
  String     codStatoAttoV = "";
  String codTipoComunicazione = "";
  String codFiscAzPrec = "";
  String codComAzPrec = "";
  String indirizAzPrec = "";
  String flgTrasferimento = "";
  String strOnSubmit = "";
  //
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
  String dataFineMovPrec = "";
  
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
  //mi arrivano dall'oggetto in sessione se sto navigando e l'oggetto è abilitato,
  //altrimenti dalla request
  SourceBean dataOrigin = serviceRequest;
  if (mov.isEnabled()) {
    dataOrigin = mov.getFieldsAsSourceBean();
  }
  //
  codMonoTipo = StringUtils.getAttributeStrNotNull(serviceResponse, "M_GetTipoAss.rows.row.codMonoTipo"); 
  strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull( serviceRequest, "STRCODICEFISCALELAV");
  strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull( serviceRequest, "STRCODICEFISCALEAZ");
  if (strCodiceFiscaleLav.equals(""))
  	strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull(dataOrigin, "STRCODICEFISCALELAV");
  if (strCodiceFiscaleAz.equals(""))
    strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull(dataOrigin, "STRCODICEFISCALEAZ");
  //Recupero dati sempre presenti nella request
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
  
  //Gestione dei movimenti collegati
  prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
  prgMovimentoSucc = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOSUCC"); 

  //Il codTipoMov serve nel caso che l'utente lo abbia cambiato e stia facendo un aggiornamento, è sempre letto dalla request
  codTipoMov = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOV");
  //Devo riportare il codTipoAss in cima alla pagina e passarlo agli altri dettagli e all'inserimento
  codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss"); 
  dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");  
  //In caso di validazione estraggo i dati necessari dalla request (per ora quelli di azienda e lavoratore sono immutabili,
  //quindi non li estraggo...)
  if (dataOrigin != null) {
    datComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin, "datComunicaz");
    luogoDiLavoro = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLUOGODILAVORO");
  } 
  
  //Setto l'origine dei dati da visualizzare
  if (!mov.isEnabled()) { 
    dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovApp.ROWS.ROW");
  }

  if (dataOrigin != null) {
    //I dati di azienda e lavoratore servono per le informazioni all'inizio della pagina
    /*
	    prgAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "prgAzienda");
	    prgUnita = StringUtils.getAttributeStrNotNull(dataOrigin, "prgUnita");
    */
    codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOAZIENDA");
    strNumAlboInterinali = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMALBOINTERINALI");
    strNumRegistroCommitt = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMREGISTROCOMMITT");
    prgAziendaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDAUTILIZ");
    prgUnitaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITAUTILIZ");

    numContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "numContratto");
    dataInizio = StringUtils.getAttributeStrNotNull(dataOrigin, "dataInizio");
    dataFine = StringUtils.getAttributeStrNotNull(dataOrigin, "dataFine");
    legaleRapp = StringUtils.getAttributeStrNotNull(dataOrigin, "legaleRapp");
    numSoggetti = StringUtils.getAttributeStrNotNull(dataOrigin, "numSoggetti");
    classeDip = StringUtils.getAttributeStrNotNull(dataOrigin, "classeDip");
    personaleInterno = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGINTERASSPROPRIA");
      
    cdnLavoratore = StringUtils.getAttributeStrNotNull(dataOrigin, "cdnLavoratore");
    //Dati per le informazioni di testata nella tabella principale del dettaglio
    dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");
    dataFineMovPrec = StringUtils.getAttributeStrNotNull(serviceRequest, "DATFINEMOVPREC");    
    datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
    codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE");
    numGgTraMovComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGTRAMOVCOMUNICAZIONE");
    datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov");
    if (codTipoMov.equalsIgnoreCase("PRO") && datInizioMov.equalsIgnoreCase("") && !dataFineMovPrec.equals("")) {
    	datInizioMov = DateUtils.giornoSuccessivo(dataFineMovPrec);
    }
    codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo");
    datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMov");
    codOrario = StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");
    numOreSett = StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");
    codMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMansione");
    descrMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrMansione");   
    descrTipoMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoMansione");
    strDesAttivita = StringUtils.getAttributeStrNotNull(dataOrigin, "strDesAttivita"); 
    codCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNL");
    strDescrizioneCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "strCCNL");
    numLivello = StringUtils.getAttributeStrNotNull(dataOrigin, "numLivello");
    codAgevolazione = StringUtils.getAttributeStrNotNull(dataOrigin, "codAgevolazione");
    codGrado = StringUtils.getAttributeStrNotNull(dataOrigin, "codGrado");
    decRetribuzioneMen = StringUtils.getAttributeStrNotNull(dataOrigin, "decRetribuzioneMen");
    numProroghe = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMPROROGHE");    
    prgMovimentoApp = StringUtils.getAttributeStrNotNull(dataOrigin, "prgMovimentoApp");
    
    //Autorizzazione alla durata dei movimenti a TD data dall'utente
    autorizzaDurataTD = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGAUTORIZZADURATATD");
    //lettura tipo comunicazione
    codTipoComunicazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOCOMUNIC");
    //lettura campi legati all'azienda precedente nel caso di trasferimento azienda in validazione
    codFiscAzPrec = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEFISCALEAZPREC");
    codComAzPrec = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCOMAZPREC");
    indirizAzPrec = StringUtils.getAttributeStrNotNull(dataOrigin,"STRINDIRIZZOAZPREC");
    flgTrasferimento = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGTRASFER");

    //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
    codtipodocex        = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPODOCEX");
    strnumdocex         = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNUMDOCEX");
    codmotivopermsoggex = StringUtils.getAttributeStrNotNull(dataOrigin,"CODMOTIVOPERMSOGGEX");
    codtipoenteprev     = StringUtils.getAttributeStrNotNull(dataOrigin,"CODENTE");
    strcodiceenteprev   = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEENTEPREV");
    flgsocio            = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGSOCIO");
    codtipotrasf        = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOTRASF");
    codtipocontratto    = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOCONTRATTO");
    strnumagsomm        = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNUMAGSOMMINISTRAZIONE");
    strindennitasom     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECINDENSOM");
    if (codTipoAzienda.equalsIgnoreCase("INT")) {
    	datiniziomissione   = StringUtils.getAttributeStrNotNull(dataOrigin,"DATINIZIORAPLAV");
    	datfinemissione     = StringUtils.getAttributeStrNotNull(dataOrigin,"DATFINERAPLAV");
    }
    strrischioasbsil    = StringUtils.getAttributeStrNotNull(dataOrigin,"STRRISCHIOASBSIL");
    strvocetariffa1     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR1");
    strvocetariffa2     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR2");
    strvocetariffa3     = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR3");
    codsoggetto         = StringUtils.getAttributeStrNotNull(dataOrigin,"CODSOGGETTO");
  }
	
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
  	strOnSubmit = strOnSubmit + "checkDatInizioMovImpatti(document.Frm1.datInizioMov)  && controlloLavAutonomo()";	
  }
	
  //Estraggo dal DB i dati sui movimenti precedente e successivo e setto i boolean
  boolean precedente = false;
  boolean successivo = false;
  if (collegato.equalsIgnoreCase("nessuno")) {
  	if (codTipoAzienda.equalsIgnoreCase("INT")) {
  		codTipoAss = "AF2";
  	} else {
  		codTipoAss = "TR2";
  	}
  	codMonoTempo = "I";  	
  }

  movIsEnabled = mov.isEnabled();
  //abilito l'oggetto se non era già abilitato
  if (!mov.isEnabled()) {
    mov.enable();
  }
  
  //boolean readOnlyDatFine = !canModify || codTipoMov.equals("TRA") ;
  boolean readOnlyDatFine = !canModify;
  //In validazione la data fine è modificabile anche in caso di trasformazioni a tempo determinato quando 
  //non indicata perchè in questo caso non è riportata nel tracciato sare 
  //(e l'utente potrebbe non aver selezionato un movimento precedente)
  if (  readOnlyDatFine && 
  		codTipoMov.equalsIgnoreCase("TRA") && 
  		codMonoTempo.equalsIgnoreCase("D") && 
  		valida ) {
  	readOnlyDatFine = false;									   
  }

    prgAzienda = (String) mov.getField("prgAzienda");
    prgUnita = (String) mov.getField("prgUnita");
    
%>
<html>
<head>
  <%@ include file ="../../global/fieldChanged.inc" %>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
      <af:linkScript path="../../js/"/>
  <title>Dettaglio Movimento da Validare</title>
  <%@ include file="../../presel/Function_CommonRicercaCCNL.inc" %>    
  <%@ include file="../../documenti/_apriGestioneDoc.inc"%> 
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>    
  var consulta = <%=consulta%>;
  var tempoiniziale = '<%=codMonoTempo%>';
  var redditoiniziale = '<%=decRetribuzioneMen%>';
  var finestraAperta;
  var precedente = <%=precedente%>;
  var inserisci = <%=inserisci%>;
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
  <script type="text/javascript" src="../../js/movimenti/trasfpro/func_trasfpro.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/_confirmDaStOcc.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/_confirmDaControlloMov.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/Linguette.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>
  
  <%@ include file="../common/include/calcolaDiffGiorni.inc" %>
  <%@ include file="../common/include/_funzioniGenerali.inc" %>
  <%@ include file="../common/include/GestioneDataFine.inc" %>
</head>
<body class="gestione" onLoad="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');calcolaDiffGiorni(document.Frm1.datInizioMov, document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange,oggettoInSessione);gestVisualGiorniRitardo();">
<%@ include file="../common/include/GestioneRisultati.inc" %>
<%@ include file="../common/include/LinguetteTrasfPro.inc" %>
<center>
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="<%=strOnSubmit%>">
<%out.print(htmlStreamTop);%>
<%@ include file="../../movimenti/common/include/_protocollazione.inc" %>
<table class="main" border="0" width="96%" cellpadding="0" cellspacing="0">
	<%@ include file ="../../movimenti/common/include/InfoTestataMovimento.inc" %>
	<%@ include file="../trasfpro/include/campi_trasfpro.inc" %>
</table>
<%out.print(htmlStreamBottom);%>
<input type="hidden" name="CODTIPOASS" value="<%=codTipoAss%>"/>
<input type="hidden" name="CODMONOTIPO" value="<%=codMonoTipo%>"/>
<input type="hidden" name="PRGMOVIMENTOAPP" value="<%=prgMovimentoApp%>"/>
<input type="hidden" name="DATCOMUNICAZ" value="<%=datComunicaz%>"/>  
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
<input type="hidden" name="STRLUOGODILAVORO" value="<%=luogoDiLavoro%>"/>
<input type="hidden" name="FLGINTERASSPROPRIA" value="<%=personaleInterno%>"/>
<input type="hidden" name="CODMONOMOVDICH" value="<%=codMonoMovDich%>"/>
<input type="hidden" name="DATFINEMOVEFFETTIVA" value="<%=datFineMovEff%>"/>      
<input type="hidden" name="CODMONOTIPOFINE" value="<%=codMonoTipoFine%>"/>
<input type="hidden" name="NUMPROROGHE" value="<%=numProroghe%>"/>
<input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>
<input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
<input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>
<input type="hidden" name="CODMONOTIPOFINEPREC" value="<%=(codTipoMov.equals("TRA") ? "T" : "P")%>"/> 
<input type="hidden" name="FLGMODTEMPO" value="N"/>
<input type="hidden" name="FLGMODREDDITO" value="N"/>
<input type="hidden" name="ACTION" value="aggiorna"/>
<input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
<input type="hidden" name="ACTIONPREC" value="<%=action%>"/>
<input type="hidden" name="PROVENIENZA" value="linguetta"/>
<input type="hidden" name="PAGE" value="MovEffettuaValidazionePage"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<%--
<input type="hidden" name="numProtocollo" value="<%=numProtocolloV%>"/>
<input type="hidden" name="numAnnoProt" value="<%=annoProtV%>"/>
--%>
<input type="hidden" name="KLOCKPROT"  value="<%=kLockProt%>">
<input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
<input type="hidden" name="FLGAUTOCERTIFICAZIONE" value="<%=flgAutocertificazione%>" />
<input type="hidden" name="CODCPI" value="<%=codCpi%>" />
<input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>" />
<input type="hidden" name="CONFERMA_CONTROLLO_MOV_SIMILI" value="<%=confermaMovSimili%>"/>

<input type="hidden" name="FORZA_INSERIMENTO" value="false"/>
<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false"/>
<input type="hidden" name="FORZA_INSERIMENTO_ETA_APPRENDISTATO" value="<%=forzaInsEtaApprendista%>"/>
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

<!--Gestione autorizzazione per movimenti a TD-->
<input type="hidden" name="FLGAUTORIZZADURATATD" value="<%=autorizzaDurataTD%>"/>
<center>
<%
if (canModify) {%>
  <input type="submit" class="pulsanti" name="submitbutton" value="Valida" onclick="resetFlagForzatura();"/>
<%}%>&nbsp;
  <!-- Commentato per prossimo rilascio
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

