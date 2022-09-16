<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                   
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.module.*,
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
  boolean canModify = false;
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  PageAttribs attributi = new PageAttribs(user, "MovDettaglioAvviamentoConsultaPage");
  
  //Oggetti per l'applicazione dello stile
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

  //Guardo il contesto in cui opero
  String currentcontext = "consulta";
  boolean inserisci = false;
  boolean salva = false;
  boolean valida = false;
  boolean rettifica = false;
  
  //guardo se provengo da un salvataggio o da un'altra linguetta o se sto consultando (sola lettura)
  String action = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTION");
  boolean aggiorna = action.equalsIgnoreCase("aggiorna");
  boolean naviga = action.equalsIgnoreCase("naviga");
  boolean consulta = true;

  //guardo se precedentemente ero in consultazione
  String actionprec = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTIONPREC");
  boolean actionprecaggiorna = actionprec.equalsIgnoreCase("aggiorna");
  boolean actionprecnaviga = actionprec.equalsIgnoreCase("naviga");
  boolean actionprecconsulta = actionprec.equalsIgnoreCase("consulta");

  //Il boolean seguente indica che si sta eseguendo un inserimento collegato
  boolean insertCollegato = false;
  String collegato = StringUtils.getAttributeStrNotNull(serviceRequest, "COLLEGATO");
  if (inserisci && collegato.equalsIgnoreCase("precedente")) {
    insertCollegato = true;
  }

%>
<%
  //inizializzazione variabili
  String prgMovimento = "";
  String prgMovimentoPrec = "";
  String prgMovimentoSucc = "";  
  String prgMovimentoApp = "";
  String prgAzienda = "";
  String prgUnita = "";
  String cdnLavoratore = "";
  String datComunicaz = "";
  String codTipoMov = "";
  String datInizioMov = "";
  String strMatricola = "";
  String codMonoTempo = "";
  String datFineMov = "";
  String codOrario = "";
  String numOreSett = "";
  String codTipoAss = "";
  String descrTipoAss = "";
  String codMonoTipo = "";         
  String codNormativa = "";
  String codMansione = "";
  String strDesAttivita = "";
  String codContratto = "";
  String flgSocio = "";
  String codCCNL = "";
  String strDescrizioneCCNL = "";  
  String decRetribuzioneMen = "";
  String numLivello = "";
  String codAgevolazione = "";
  String cdnUtIns = "";
  String dtmIns = "";
  String numKloMov = "";
  String numKloMovPrec = ""; 
  String codGrado = "";
  String flgArtigiana = "";
  String codMonoStato = "";
  String posInps = "";
  String patInail = "";
  String codTipoAzienda = "";
  String codNatGiurAz = "";  
  String strNumAlboInterinali = "";
  String strNumRegistroCommitt = "";
  String prgAziendaUtil = ""; 
  String prgUnitaUtil = ""; 
  String luogoDiLavoro = "";
  String personaleInterno = "";
  String codMonoTempoAvv = ""; 
  String dataFineMovPrec = "";
  String dataInizioAvv = "";
  String dataInizioMovPrec = "";
  String codtipomovprec = ""; 
  String codMonoMovDich = ""; 
  String datFineMovEff = "";
  String codMonoTipoFine = "";
  String descrMansione = "";
  String descrTipoMansione = "";
  String numGgTraMovComunicaz = "";
  String decretribmensanata = "";
  String datasanata = "";
  String tipodichsanata = "";
  String datInizioMovSupReddito = "";
  String codiceDich = "";
  String prgDichLav = "";
  String codMotAnnullamento = "";
  
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
  // agricoltura
  String numGGPrevistiAgr = null;
  String numGGEffettuatiAgr = null; 
  String codCategoria   = null;
  String codLavorazione = null;
  //Variabili per la gestione della protocollazione
  BigDecimal prgDoc = null;
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String     datProtV     = "";
  String     oraProtV     = "";
  String     docInOut     = "";
  String     docRif       = "";
  String codStatoAttoV = "";
  String strLavCollMirato = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATORECOLLMIRATO");
  String flgLegge68="";
  //qualifica srq per apprendistato
  String codQualificaSrq = "";
  String descQualificaSrq = "";
  String visSezioneSRQ = "inline";
  // CM 07/02/2007 Savino: convenzioni ed incentivi arg. 13
  String numConvenzione = "";
  String datConvenzione = "";
  String datFineSgravio = "";
  String decImportoConcesso = "";
  String strNotaModifica = "";  
  //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
  String codtipoenteprev = "";
  String strcodiceenteprev = "";
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

  SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMov.ROWS.ROW");

  if (dataOrigin != null) {
	prgMovimento = StringUtils.getAttributeStrNotNull(dataOrigin, "prgMovimento");
	//Gestione del lock per il movimento
	numKloMov = StringUtils.getAttributeStrNotNull(dataOrigin, "numKloMov");
	//Gestione dei movimenti collegati
	prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
	prgMovimentoSucc = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOSUCC");

    codStatoAtto = StringUtils.getAttributeStrNotNull(dataOrigin, "CODSTATOATTO");      
    codMotAnnullamento = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMOTANNULLAMENTO");  
    datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov");   
    strMatricola = StringUtils.getAttributeStrNotNull(dataOrigin, "strMatricola");   
    codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo"); 
    codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOAZIENDA");  
    datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMov");   
    codOrario = StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");   
    numOreSett = StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");   
    codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss");   
    descrTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoAss");
    codMonoTipo = StringUtils.getAttributeStrNotNull(dataOrigin,"codMonoTipo");
    codNormativa = StringUtils.getAttributeStrNotNull(dataOrigin, "codNormativa");   
    codMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMansione");   
    strDesAttivita = StringUtils.getAttributeStrNotNull(dataOrigin, "strDesAttivita");   
    codContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "codContratto");   
    flgSocio = StringUtils.getAttributeStrNotNull(dataOrigin, "flgSocio");   
    codCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNL");   
    strDescrizioneCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "strCCNL");   
    decRetribuzioneMen = StringUtils.getAttributeStrNotNull(dataOrigin, "decRetribuzioneMen");   
    numLivello = StringUtils.getAttributeStrNotNull(dataOrigin, "numLivello");   
    codAgevolazione = StringUtils.getAttributeStrNotNull(dataOrigin, "codAgevolazione");   
    codGrado = StringUtils.getAttributeStrNotNull(dataOrigin, "codGrado");   
    flgArtigiana = StringUtils.getAttributeStrNotNull(dataOrigin, "flgArtigiana");   
    codMonoStato = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoStato");  
    descrMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrMansione");   
    descrTipoMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoMansione");
    numGgTraMovComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGTRAMOVCOMUNICAZIONE");
	decretribmensanata = StringUtils.getAttributeStrNotNull(dataOrigin,"DECRETRIBUZIONEMENSANATA");
	datasanata = StringUtils.getAttributeStrNotNull(dataOrigin,"DATSITSANATA");
	tipodichsanata = StringUtils.getAttributeStrNotNull(dataOrigin,"TIPODICHSANATA");
	codiceDich = StringUtils.getAttributeStrNotNull(dataOrigin,"CODICEDICH");
	datInizioMovSupReddito = StringUtils.getAttributeStrNotNull(dataOrigin,"DATINIZIOMOVSUPREDDITO");
    codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOAZIENDA");
    codNatGiurAz = StringUtils.getAttributeStrNotNull(dataOrigin, "CODNATGIURIDICAAZ");
    
    //I dati di azienda e lavoratore servono per le informazioni all'inizio della pagina
    prgAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "prgAzienda");
    prgUnita = StringUtils.getAttributeStrNotNull(dataOrigin, "prgUnita");
    cdnLavoratore = StringUtils.getAttributeStrNotNull(dataOrigin, "cdnLavoratore");

    //Dati per le informazioni di testata nella tabella principale del dettaglio 
    dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");
    dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");
    datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
    codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE"); 
    //Utilizzato per la gestione della visualizzazione dei giorni di ritardo
    codMonoMovDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOMOVDICH");
    codTipoMov = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOV");
    numGGPrevistiAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGPREVISTIAGR");
    //16/01/2001 Davide: aggiungiunti campi inerenti l'agricoltura
    codCategoria   = StringUtils.getAttributeStrNotNull(dataOrigin,"CODCATEGORIA");
    codLavorazione = StringUtils.getAttributeStrNotNull(dataOrigin,"CODLAVORAZIONE");
    //QUALIFICA SRQ
    codQualificaSrq = StringUtils.getAttributeStrNotNull(dataOrigin, "CODQUALIFICASRQ");
    descQualificaSrq = StringUtils.getAttributeStrNotNull(dataOrigin, "descQualificaSrq");
    
    flgLegge68 = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGLEGGE68");
    if(flgLegge68.equals("") || flgLegge68.equals("N")){
    	numConvenzione = "";
    	datConvenzione = "";
    }else {
    	numConvenzione = StringUtils.getAttributeStrNotNull(dataOrigin, "numConvenzione");
    	datConvenzione = StringUtils.getAttributeStrNotNull(dataOrigin, "datConvenzione");
    }
    
    // 06/02/2007 Savino: CM incentivi ant. 13
    datFineSgravio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINESGRAVIO");
    decImportoConcesso = StringUtils.getAttributeStrNotNull(dataOrigin, "DECIMPORTOCONCESSO");
    //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
    codtipoenteprev     = StringUtils.getAttributeStrNotNull(dataOrigin,"CODENTE");
    strcodiceenteprev   = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEENTEPREV");
    codtipotrasf        = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOTRASF");
    codtipocontratto    = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOCONTRATTO");
    strnumagsomm        = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNUMAGSOMMINISTRAZIONE");
    decindensom         = StringUtils.getAttributeStrNotNull(dataOrigin,"DECINDENSOM");
    datiniziomissione   = StringUtils.getAttributeStrNotNull(dataOrigin,"DATINIZIORAPLAV");
    datfinemissione     = StringUtils.getAttributeStrNotNull(dataOrigin,"DATFINERAPLAV");
    strrischioasbsil    = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGRISCHIOSIAS");
    decvocetar1         = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR1");
    decvocetar2         = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR2");
    decvocetar3         = StringUtils.getAttributeStrNotNull(dataOrigin,"DECVOCETAR3");
    codsoggetto         = StringUtils.getAttributeStrNotNull(dataOrigin,"CODSOGGETTO");
  } 
  Vector rows = serviceResponse.getAttributeAsVector("DettagliDocumentoDaPrgMov.ROWS.ROW");
  SourceBean row = null; 
 if((rows != null) && (rows.size()>0)) {
   row = (SourceBean)rows.lastElement();
   prgDoc        = SourceBeanUtils.getAttrBigDecimal(row, "prgdocumento", null);
   numProtV      = SourceBeanUtils.getAttrBigDecimal(row, "numprotocollo", null);
   numAnnoProtV  = SourceBeanUtils.getAttrBigDecimal(row, "numannoprot", null); 
   datProtV      = StringUtils.getAttributeStrNotNull(row,"datprot");
   oraProtV      = StringUtils.getAttributeStrNotNull(row,"oraprot");
   docInOut      = StringUtils.getAttributeStrNotNull(row,"codmonoio");
   docRif        = StringUtils.getAttributeStrNotNull(row,"riferimento");
 }
  codStatoAttoV = Utils.notNull(codStatoAtto);
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
  collegato = "nessuno";
  if (precedente && successivo) {collegato = "entrambi";}
  else if (precedente) {collegato = "precedente";}
  else if (successivo) {collegato = "successivo";}
  
  //Gestione del lock per il movimento
  if (dataOrigin != null){
    numKloMov = StringUtils.getAttributeStrNotNull(dataOrigin, "numKloMov");
  }
  //Oggetti per la generazione delle informazioni sul lavoratore e azienda
  InfCorrentiLav testataLav = null;
  InfCorrentiAzienda testataAz = null;
  if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) {
    testataLav = new InfCorrentiLav( RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    testataAz = new InfCorrentiAzienda(prgAzienda,prgUnita);
  } 
  
  strNotaModifica = StringUtils.getAttributeStrNotNull(serviceRequest,"strNotaModifica");
%>

<html>
  <head>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    <title>Dettaglio Movimento</title>
    <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,
                                 "cdnLavoratore="+cdnLavoratore+
                                 "&prgMovimento="+prgMovimento+
                                 "&PAGERITORNOLISTA="+StringUtils.getAttributeStrNotNull(serviceRequest, "PAGERITORNOLISTA"));
      %>
    <!--
      var consulta = <%=consulta%>;
      var codTipoAzienda = '<%=codTipoAzienda%>';
      var codNatGiuridicaAz = '<%=codNatGiurAz%>';
      var finestraAperta;
      var precedente = <%=precedente%>;
      var inserisci = '<%=inserisci%>';
      var contesto = 'consulta';
      <%-- evita un errore js navigando nei campi in sola lettura con il tasto tab --%>
      function PulisciRicercaCCNL () {}
    -->
    </SCRIPT>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
<% 
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) { 
	// END-PARTE-TEMP
%>	
    <script type="text/javascript" src="../../js/movimenti/avviamento/func_avviamento.js" language="JavaScript"></script>
<% 
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP
%>   
    <script type="text/javascript" src="../../js/movimenti/avviamento/func_avviamento_cm.js" language="JavaScript"></script>
<% 
	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
%>
    <script type="text/javascript" src="../../js/movimenti/avviamento/gestioneAutorizzazione.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script> 
    <script type="text/javascript" src="../../js/movimenti/common/Indietro.js" language="JavaScript"></script> 
    <script type="text/javascript" src="../../js/movimenti/common/Linguette.js" language="JavaScript"></script>
    <%@ include file="../common/include/_funzioniGenerali.inc" %>
    <%@ include file="../common/include/CollegaSuccessivo.inc" %> 
    <%-- 07/02/2007 Savino: aggiunto questo file per evitare errori js quando si salta tra i campi con il tasto TAB --%>
    <%@ include file="../../global/fieldChanged.inc" %>
  </head>

<body class="gestione" onload="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');rinfresca();gestVisualGiorniRitardo();"><!--notificaCambioStatoOcc();-->
  <% if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) { testataLav.show(out); }%>
  <% if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) { testataAz.show(out); }%>

  <%@ include file="../common/include/GestioneRisultati.inc" %>
  
  <%@ include file="../common/include/LinguetteAvviamento.inc" %>
  <%@ include file="../avviamento/include/gestioneAutorizzazione.inc" %>  
  <center>
<af:form name="Frm1" method="POST" action="AdapterHTTP">
  <%out.print(htmlStreamTop);%>
    <%@ include file="../../movimenti/common/include/_protocollazione.inc" %>
  <table class="main" cellspacing="0"  cellpadding="0" width="96%" border="0">
  	<%@ include file="../../movimenti/common/include/InfoTestataMovimento.inc" %>      
<% // N.B. una volta attivato il collocamento mirato il file campi_avviamento.inc potra' essere cancellato 
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) { 
	// END-PARTE-TEMP
%>	
    <%@ include file="../avviamento/include/campi_avviamento.inc" %>
<% 
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP
%>
    <%@ include file="../avviamento/include/campi_avviamento_cm.inc" %>
<% 
	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
%>
    <%@ include file="../../movimenti/common/include/NavigazioneRettificato.inc" %>    
  </table>
  <%out.print(htmlStreamBottom);%>
  <input type="hidden" name="PRGMOVIMENTO" value="<%=prgMovimento%>"/>
  <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
  <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
  <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
  <input type="hidden" name="NUMKLOMOV" value="<%=numKloMov%>"/>
  <input type="hidden" name="CODTIPOMOV" value="<%=codTipoMov%>"/>      
  <input type="hidden" name="DATINIZIOMOVPREC" value="<%=dataInizioMovPrec%>"/> 
  <input type="hidden" name="CODMONOTEMPOAVV" value="<%=codMonoTempoAvv%>"/>
  <input type="hidden" name="DATFINEMOVPREC" value="<%=dataFineMovPrec%>"/>
  <input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>"/>
  <input type="hidden" name="CODNATGIURIDICAAZ" value="<%=codNatGiurAz%>"/>        
  <input type="hidden" name="STRNUMALBOINTERINALI" value="<%=strNumAlboInterinali%>"/> 
  <input type="hidden" name="STRNUMREGISTROCOMMITT" value="<%=strNumRegistroCommitt%>"/>
  <input type="hidden" name="STRPOSINPS" value="<%=posInps%>"/>     
  <input type="hidden" name="STRPATINAIL" value="<%=patInail%>"/>  
  <input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=prgAziendaUtil%>"/> 
  <input type="hidden" name="PRGUNITAUTILIZ" value="<%=prgUnitaUtil%>"/>
  <input type="hidden" name="STRLUOGODILAVORO" value="<%=luogoDiLavoro%>"/>
  <input type="hidden" name="FLGINTERASSPROPRIA" value="<%=personaleInterno%>"/>
  <input type="hidden" name="CODMONOMOVDICH" value="<%=codMonoMovDich%>"/>
  <input type="hidden" name="DATFINEMOVEFFETTIVA" value="<%=datFineMovEff%>"/>      
  <input type="hidden" name="CODMONOTIPOFINE" value="<%=codMonoTipoFine%>"/>
  <input type="hidden" name="numContratto" value="<%=numContratto%>">
  <input type="hidden" name="dataInizio" value="<%=dataInizio%>">
  <input type="hidden" name="dataFine" value="<%=dataFine%>">
  <input type="hidden" name="legaleRapp" value="<%=legaleRapp%>">
  <input type="hidden" name="numSoggetti" value="<%=numSoggetti%>">
  <input type="hidden" name="classeDip" value="<%=classeDip%>">
  <input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>      
  <input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
  <input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>
  <input type="hidden" name="ACTION" value="aggiorna"/>
  <input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
  <input type="hidden" name="COLLEGATO" value="<%=collegato%>"/>
  <input type="hidden" name="ACTIONPREC" value="<%=action%>"/>
  <input type="hidden" name="PROVENIENZA" value="linguetta"/>
  <input type="hidden" name="PAGE" value="MovDettaglioAvviamentoConsultaPage"/>
  <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
  <input type="hidden" name="numProtocollo" value="<%=numProtocolloV%>"/>
  <input type="hidden" name="numAnnoProt" value="<%=annoProtV%>"/>
  <input type="hidden" name="KLOCKPROT"  value="<%=kLockProt%>">
  <input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
  <input type="hidden" name="FLGAUTOCERTIFICAZIONE" value="<%=flgAutocertificazione%>" />
  <input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>" />
  <input type="hidden" name="LAVORATORECOLLMIRATO" value="<%=strLavCollMirato%>"/>
  <!-- GESTIONE APPRENDISTATO -->
  <input type="hidden" name="STRCOGNOMETUTORE" value="" />
  <input type="hidden" name="STRNOMETUTORE" value="" />
  <input type="hidden" name="STRCODICEFISCALETUTORE" value="" />
  <input type="hidden" name="FLGTITOLARETUTORE" value="" />
  <input type="hidden" name="NUMANNIESPTUTORE" value="" />
  <input type="hidden" name="STRLIVELLOTUTORE" value="" />
  <input type="hidden" name="CODMANSIONETUTORE" value="" />
  <input type="hidden" name="STRMANSIONETUTORE" value="" />
  <input type="hidden" name="STRTIPOMANSIONETUTORE" value="" />
  <input type="hidden" name="DATVISITAMEDICA" value="" />
  <input type="hidden" name="NUMMESIAPPRENDISTATO" value="" />
  <input type="hidden" name="FLGARTIGIANA" value="" />
  <input type="hidden" name="STRNOTE" value="" />
  <!-- DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO -->
  <input type="hidden" name="CODENTE"     value="<%=codtipoenteprev%>" />
  <input type="hidden" name="STRCODICEENTEPREV"   value="<%=strcodiceenteprev%>" />
  <input type="hidden" name="CODTIPOTRASF"        value="<%=codtipotrasf%>" />
  <input type="hidden" name="CODTIPOCONTRATTO"    value="<%=codtipocontratto%>" />
  <input type="hidden" name="STRNUMAGSOMMINISTRAZIONE"        value="<%=strnumagsomm%>" />
  <input type="hidden" name="DECINDENSOM"     value="<%=decindensom%>" />
  <input type="hidden" name="FLGRISCHIOSIAS"    value="<%=strrischioasbsil%>" />
  <input type="hidden" name="DECVOCETAR1"     value="<%=decvocetar1%>" />
  <input type="hidden" name="DECVOCETAR2"     value="<%=decvocetar2%>" />
  <input type="hidden" name="DECVOCETAR3"     value="<%=decvocetar3%>" />
  <input type="hidden" name="CODSOGGETTO"         value="<%=codsoggetto%>" />
  <!-- variabile utilizzata per la visualizzazione o meno della Nota Modifiche --> 
  <%  if(serviceRequest.containsAttribute("forzaVal")) {%>
   			<input type="hidden" name="forzaVal" value="1"/>
           	<input type="hidden" name="strNotaModifica" value="<%=strNotaModifica%>"/>
  <%}%>
<center>
<%@ include file="../common/include/GestioneCollegati.inc" %> 
</center>
	<% if(serviceRequest.containsAttribute("forzaVal")) {%>
    <center>
		<%out.print(htmlStreamTop);%>
			<%@ include file="../generale/include/_NotaModifiche.inc" %>
		<%out.print(htmlStreamBottom);%>
	</center>
<%}%> 
<center>
<%@ include file="../common/include/PulsanteRitornoLista.inc" %> 
</center>
</af:form>      
</center>
<script language="javascript">
  visualizzaPulsanteApprendistato("<%=codMonoTipo%>");
</script>
</body>
<%@ include file="../common/include/GestioneScriptRisultati.inc" %>
</html>  
