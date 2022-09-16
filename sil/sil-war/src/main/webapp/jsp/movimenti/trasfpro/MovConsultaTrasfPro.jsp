<!-- CONSULTAZIONE TRASFORMAZIONE/PROROGA DEL MOVIMENTO -->
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
  PageAttribs attributi = new PageAttribs(user, "MovDettaglioTrasfProConsultaPage");
  String codInterinale = "INT";
  boolean canModify = false;
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
 
  //Oggetti per l'applicazione dello stile
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

  String currentcontext = "consulta";
  boolean inserisci = false;
  boolean valida = false;
  boolean rettifica = false;
  
  //guardo se provengo da un salvataggio o da un'altra linguetta
  boolean consulta = true;
  
  //guardo se precedentemente ero in consultazione
  String actionprec = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTIONPREC");
  boolean actionprecaggiorna = actionprec.equalsIgnoreCase("aggiorna");
  boolean actionprecnaviga = actionprec.equalsIgnoreCase("naviga");
  boolean actionprecconsulta = actionprec.equalsIgnoreCase("consulta");
  %>
  <%@ include file="../GestioneOggettoMovimento.inc" %> 
  <%
  //inizializzazione variabili
  String prgMovimento = "";
  String prgMovimentoApp = "";
  String prgAzienda = "";
  String prgUnita = "";
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
  String posInps = "";
  String patInail = "";
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
  //Variabili per la gestione della protocollazione
  BigDecimal prgDoc = null;
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String     datProtV     = "";
  String     oraProtV     = "";
  String     docInOut     = "";
  String     docRif       = "";
  String     codStatoAttoV = "";
  // CM 07/02/2007 Savino: incentivi arg. 13. E' stato modificato il file campi_trasfpro.inc
  String datFineSgravio = "";
  String decImportoConcesso = "";
  String strNotaModifica = "";
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
  
  SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMov.ROWS.ROW");
  if (dataOrigin != null) {
    //I dati di azienda e lavoratore servono per le informazioni all'inizio della pagina
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
    codStatoAtto = StringUtils.getAttributeStrNotNull(dataOrigin, "CODSTATOATTO");
    codMotAnnullamento = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMOTANNULLAMENTO");
    //Gestione dei movimenti collegati
    prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
    prgMovimentoSucc = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOSUCC"); 
    //Il codTipoMov serve nel caso che l'utente lo abbia cambiato e stia facendo un aggiornamento, è sempre letto dalla request
    codTipoMov = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOV");
    //Devo riportare il codTipoAss in cima alla pagina e passarlo agli altri dettagli e all'inserimento
    codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss");
    prgAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "prgAzienda");
    prgUnita = StringUtils.getAttributeStrNotNull(dataOrigin, "prgUnita");
    cdnLavoratore = StringUtils.getAttributeStrNotNull(dataOrigin, "cdnLavoratore");

    //Dati per le informazioni di testata nella tabella principale del dettaglio 
    dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");
    dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");    
    datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
    codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE");
    numGgTraMovComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGTRAMOVCOMUNICAZIONE");  
    prgMovimento = StringUtils.getAttributeStrNotNull(dataOrigin, "prgMovimento");
    //Gestione del lock per il movimento
    numKloMov = StringUtils.getAttributeStrNotNull(dataOrigin, "numKloMov");
    datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov");
    codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo");
    datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMov");
    codOrario = StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");
    numOreSett = StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");
    codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOAZIENDA");
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
    decretribmensanata = StringUtils.getAttributeStrNotNull(dataOrigin,"DECRETRIBUZIONEMENSANATA");
	datasanata = StringUtils.getAttributeStrNotNull(dataOrigin,"DATSITSANATA");
	tipodichsanata = StringUtils.getAttributeStrNotNull(dataOrigin,"TIPODICHSANATA");
	codiceDich = StringUtils.getAttributeStrNotNull(dataOrigin,"CODICEDICH");
	datInizioMovSupReddito = StringUtils.getAttributeStrNotNull(dataOrigin,"DATINIZIOMOVSUPREDDITO");
    numProroghe = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMPROROGHE");  
    // 06/02/2007 Savino: CM incentivi ant. 13. 
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
    flgsocio            = StringUtils.getAttributeStrNotNull(dataOrigin,"FLGSOCIO");
   }
  	Vector	rows = serviceResponse.getAttributeAsVector("DettagliDocumentoDaPrgMov.ROWS.ROW");
	SourceBean row = null; 
    if((rows != null) && (rows.size()>0))    {
		row = (SourceBean)rows.get(rows.size()-1);
		prgDoc        = (BigDecimal) row.getAttribute("prgdocumento");
		numProtV      = SourceBeanUtils.getAttrBigDecimal(row, "numProtocollo", null);
		numAnnoProtV  = (BigDecimal) row.getAttribute("numannoprot"); 
		datProtV      = StringUtils.getAttributeStrNotNull(row,"datprot");
		oraProtV      = StringUtils.getAttributeStrNotNull(row,"oraprot");
		docInOut      = StringUtils.getAttributeStrNotNull(row,"codmonoio");
		docRif       = StringUtils.getAttributeStrNotNull(row,"riferimento");       
    }
    codStatoAttoV = Utils.notNull(codStatoAtto); 
    ////////// fine gestione dati sezione protocollazione
  
  //Estraggo dal DB i dati sui movimenti precedente e successivo e setto i boolean
  boolean precedente = false;
  boolean successivo = false;

  boolean insertCollegato = false;
  String collegato = "";
  
  //Oggetti per la generazione delle informazioni sul lavoratore e azienda
  InfCorrentiLav testataLav = null;
  InfCorrentiAzienda testataAz = null;
  if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) {
    testataLav = new InfCorrentiLav( RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    testataAz = new InfCorrentiAzienda(prgAzienda,prgUnita);
  }  
   
  //abilito l'oggetto se non era già abilitato
  if (!mov.isEnabled()) {
    mov.enable();
  }
  
boolean readOnlyDatFine = !canModify;

strNotaModifica = StringUtils.getAttributeStrNotNull(serviceRequest,"strNotaModifica");
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
        attributi.showHyperLinks(out, requestContainer,responseContainer,
                                 "cdnLavoratore="+cdnLavoratore+
                                 "&prgMovimento="+prgMovimento+
                                 "&PAGERITORNOLISTA="+StringUtils.getAttributeStrNotNull(serviceRequest, "PAGERITORNOLISTA"));
      %>
  var finestraAperta;
  var collegato = '<%=collegato%>';
  var consulta = '<%=consulta%>';
  var codMomoTempo = '<%=codMonoTempo%>';
  var inserisci = <%=inserisci%>;
  var precedente = <%=precedente%>;
  var redditoiniziale = '<%=decRetribuzioneMen%>';
  var contesto = 'consulta';
  </SCRIPT>
  <script type="text/javascript" src="../../js/movimenti/common/Indietro.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/trasfpro/func_trasfpro.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script>
  <script type="text/javascript" src="../../js/movimenti/common/Linguette.js" language="JavaScript"></script>

  <%@ include file="../common/include/_funzioniGenerali.inc" %>
  <%@ include file="../common/include/CollegaSuccessivo.inc" %> 
</head>
 
<body class="gestione" onLoad="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');gestVisualGiorniRitardo();">
<% if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) { testataLav.show(out); }%>
<% if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) { testataAz.show(out); }%>

<%@ include file="../common/include/GestioneRisultati.inc" %>
  
<%@ include file="../common/include/LinguetteTrasfPro.inc" %>
<center>
<af:form name="Frm1" method="POST" action="AdapterHTTP">
<%out.print(htmlStreamTop);%>
<%@ include file="../../movimenti/common/include/_protocollazione.inc" %>
<table class="main" border="0" width="96%" cellpadding="0" cellspacing="0">
	<%@ include file="../../movimenti/common/include/InfoTestataMovimento.inc" %>
<%-- CM 09/02/2007 Savino: viene incluso un nuovo file che continene i campi degli incentivi art. 13. Il file originario viene ancora usato dalla
pagina della validazione. --%>
	<%@ include file="../trasfpro/include/campi_trasfpro_cm.inc" %>
    <%@ include file="../../movimenti/common/include/NavigazioneRettificato.inc" %>
</table>
<%out.print(htmlStreamBottom);%>
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="NUMKLOMOV" value="<%=numKloMov%>"/>
<input type="hidden" name="CODTIPOMOV" value="<%=codTipoMov%>"/>      
<input type="hidden" name="DATINIZIOMOVPREC" value="<%=dataInizioMovPrec%>"/> 
<input type="hidden" name="CODMONOTEMPOAVV" value="<%=codMonoTempoAvv%>"/>
<input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>"/>
        
<input type="hidden" name="STRNUMALBOINTERINALI" value="<%=strNumAlboInterinali%>"/> 
<input type="hidden" name="STRNUMREGISTROCOMMITT" value="<%=strNumRegistroCommitt%>"/>
<input type="hidden" name="STRPOSINPS" value="<%=posInps%>"/>     
<input type="hidden" name="STRPATINAIL" value="<%=patInail%>"/>  
<input type="hidden" name="STRLUOGODILAVORO" value="<%=luogoDiLavoro%>"/>
<input type="hidden" name="FLGINTERASSPROPRIA" value="<%=personaleInterno%>"/>
<input type="hidden" name="CODMONOMOVDICH" value="<%=codMonoMovDich%>"/>
<input type="hidden" name="DATFINEMOVEFFETTIVA" value="<%=datFineMovEff%>"/>      
<input type="hidden" name="CODMONOTIPOFINE" value="<%=codMonoTipoFine%>"/>
<input type="hidden" name="NUMPROROGHE" value="<%=numProroghe%>"/>
<input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>
<input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
<input type="hidden" name="PRGMOVIMENTO" value="<%=prgMovimento%>"/>
<input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>
<input type="hidden" name="CODMONOTIPOFINEPREC" value="<%=(codTipoMov.equals("TRA") ? "T" : "P")%>"/> 
<input type="hidden" name="FLGMODTEMPO" value="N"/>
<input type="hidden" name="FLGMODREDDITO" value="N"/>
<input type="hidden" name="ACTION" value=""/>
<input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
<input type="hidden" name="COLLEGATO" value="<%=collegato%>"/>
<input type="hidden" name="ACTIONPREC" value="<%=actionprec%>"/>
<input type="hidden" name="PROVENIENZA" value="linguetta"/>
<input type="hidden" name="PAGE" value="MovDettaglioTrasfProConsultaPage"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<input type="hidden" name="numProtocollo" value="<%=numProtocolloV%>"/>
<input type="hidden" name="numAnnoProt" value="<%=annoProtV%>"/>
<input type="hidden" name="KLOCKPROT"  value="<%=kLockProt%>">
<input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
<input type="hidden" name="FLGAUTOCERTIFICAZIONE" value="<%=flgAutocertificazione%>" />
<input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>" />
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
  <input type="hidden" name="FLGSOCIO"            value="<%=flgsocio%>" />

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
</center>
</af:form>      

</body>
<%@ include file="../common/include/GestioneScriptRisultati.inc" %>
</html>
