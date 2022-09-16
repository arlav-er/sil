<!-- CONSULTA DETTAGLIO CESSAZIONE -->
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
  PageAttribs attributi = new PageAttribs(user, "MovDettaglioCessazioneConsultaPage");
  boolean canModify = false;
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");

  //Oggetti per l'applicazione dello stile
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

  //Guardo il contesto in cui opero
  String currentcontext = "consulta";
  boolean inserisci = false;
  boolean salva = false;
  boolean valida = false;
  boolean rettifica = false;

  //guardo se provengo da un salvataggio o da un'altra linguetta
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

  //inizializzazione variabili
  String prgMovimento = "";
  String prgMovimentoApp = "";
  String prgAzienda = "";
  String prgUnita = "";
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
  String posInps = "";
  String patInail = "";
  String codTipoAzienda = "";
  String strNumAlboInterinali = "";
  String strNumRegistroCommitt = ""; 
  String dataInizioAvv = "";
  String dataInizioMovPrec = "";
  String dataFineMovPrec = "";
  String codMonoTempoAvv = "";
  String prgMovimentoPrec = "";
  String prgMovimentoSucc = "";
  String codtipomovprec = "";
  String prgAziendaUtil = ""; 
  String prgUnitaUtil = ""; 
  String luogoDiLavoro = "";
  String personaleInterno = "";
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
  String numContratto = "";
  String dataInizio = "";
  String dataFine = "";
  String legaleRapp = "";
  String numSoggetti = "";
  String classeDip = "";
  String codMonoTempo = "";
  String numGGEffettuatiAgr =null;
  //Variabili per la gestione della protocollazione
  BigDecimal prgDoc = null;
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String     datProtV     = "";
  String     oraProtV     = "";
  String     docInOut     = "";
  String     docRif       = "";
  String     codStatoAttoV = "";
  String strNotaModifica = "";

	//Giovanni D'Auria 18/02/05
	String codOrario = "";
	String numOreSett = "";
	//*********************
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

  //Utlizzata nel caso che si inserisca una cessazione NON collegata ad un movimento precedente
  //Questo campo NONDEVE essere visibile in dettaglio (al momento è inserito solo perchè altrimenti non compile la jsp?!?
  String datInizioAVVperCVE = "";

  SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMov.ROWS.ROW");

  if (dataOrigin != null) {
    //I dati di azienda e lavoratore servono per le informazioni all'inizio della pagina
    prgAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "prgAzienda");
    prgUnita = StringUtils.getAttributeStrNotNull(dataOrigin, "prgUnita");
    cdnLavoratore = StringUtils.getAttributeStrNotNull(dataOrigin, "cdnLavoratore");

    prgMovimento = StringUtils.getAttributeStrNotNull(dataOrigin, "prgMovimento");
    //Gestione del lock per il movimento
    numKloMov = StringUtils.getAttributeStrNotNull(dataOrigin, "numKloMov");
    //Gestione dei movimenti collegati
    prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
    prgMovimentoSucc = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOSUCC");

    //Dati per le informazioni di testata nella tabella principale del dettaglio 
    dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");
    dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");
    datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
    codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE"); 
    numGgTraMovComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGTRAMOVCOMUNICAZIONE");
    datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov");
    codMvCessazione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMvCessazione");
    codMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMansione");   
    codGrado = StringUtils.getAttributeStrNotNull(dataOrigin, "codGrado"); 
    numLivello = StringUtils.getAttributeStrNotNull(dataOrigin, "numLivello");   
    descrMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrMansione");   
    descrTipoMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoMansione");
    //Utilizzato per la gestione della visualizzazione dei giorni di ritardo
    codMonoMovDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOMOVDICH");
    codTipoMov = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOV");
    codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOAZIENDA");
    codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss");
    numGGEffettuatiAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGEFFETTUATIAGR");
    codStatoAtto = StringUtils.getAttributeStrNotNull(dataOrigin,"codStatoAtto");
    codMotAnnullamento = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMOTANNULLAMENTO");
    codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo"); 
    //per la visualizzazione dei campi aggiunti il 18/02/05   codOrario e numOreSett 
    codOrario=StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");
    numOreSett=StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");
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
     if((rows != null) && (rows.size()>0))
     {
       row = (SourceBean)rows.get(rows.size()-1);
       prgDoc        = SourceBeanUtils.getAttrBigDecimal(row, "prgdocumento", null);
       numProtV      = SourceBeanUtils.getAttrBigDecimal(row, "numprotocollo", null);
       numAnnoProtV  = SourceBeanUtils.getAttrBigDecimal(row, "numannoprot", null); 
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

  //Oggetti per la generazione delle informazioni sul lavoratore e azienda
  InfCorrentiLav testataLav = null;
  InfCorrentiAzienda testataAz = null;
  if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) {
    testataLav = new InfCorrentiLav( RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    testataAz = new InfCorrentiAzienda(prgAzienda,prgUnita);
  }  

  //Mi dice se devo mostrare il campo della data di inizio avviamento
  boolean mostraCampoInizioAvviamento = false;
  
  strNotaModifica = StringUtils.getAttributeStrNotNull(serviceRequest,"strNotaModifica");
%>

<html>
  <head>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
        <af:linkScript path="../../js/"/>
    <title>Dettaglio Movimento</title>
    <!--<%@ include file="../../presel/Function_CommonRicercaCCNL.inc" %>-->
    <%@ include file="../../global/fieldChanged.inc" %>
    <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,
                                 "cdnLavoratore="+cdnLavoratore+
                                 "&prgMovimento="+prgMovimento+
                                 "&PAGERITORNOLISTA="+StringUtils.getAttributeStrNotNull(serviceRequest, "PAGERITORNOLISTA"));
      %>
    <!--
    var consulta = <%=consulta%>
    var precedente = <%=precedente%>  

    //Notifica del cambio di stato occupazionale
    function notificaCambioStatoOcc() {
	<% if (StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovInserisciCessazione.CAMBIOSTATOOCC").equals("TRUE")) {%>
    alert("Lo stato occupazionale del lavoratore è cambiato.");
	<%}%>
    }

    var inserisci = '<%=inserisci%>';
    var prgAziendaS = '<%=prgAzienda%>';
    var prgUnitaS = '<%=prgUnita%>';
    var contesto = 'consulta';
    -->
    </SCRIPT>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/cessazione/func_cessazione.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/Indietro.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/Linguette.js" language="JavaScript"></script>

    <%@ include file="../common/include/_funzioniGenerali.inc" %> 
  </head>

  <body class="gestione" onload="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');rinfresca();notificaCambioStatoOcc();gestVisualGiorniRitardo();">
  <% if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) { testataLav.show(out); }%>
  <% if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) { testataAz.show(out); }%>  

  <%@ include file="../common/include/GestioneRisultati.inc" %>

    <%@ include file="../../movimenti/common/include/LinguetteCessazione.inc" %>
    <center>
      <af:form name="Frm1" method="POST" action="AdapterHTTP">

        <%out.print(htmlStreamTop);%> 
        <%@ include file="../../movimenti/common/include/_protocollazione.inc" %>
        <table class="main">
          <%@ include file="../../movimenti/common/include/InfoTestataMovimento.inc" %>
          <%@ include file="include/campi_cessazione.inc" %>          
    	  <%@ include file="../../movimenti/common/include/NavigazioneRettificato.inc" %>
        </table>
        <%out.print(htmlStreamBottom);%>


        <input type="hidden" name="PRGMOVIMENTO" value="<%=prgMovimento%>"/>

<%if (inserisci) {%>
        <input type="hidden" name="DATCOMUNICAZ" value="<%=datComunicaz%>"/> 
    <% if (precedente) {%>
        <input type="hidden" name="CODTIPOASS" value="<%=codTipoAss%>"/>        
    <%}%>
<%}%>
        <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
        <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
        <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
        <input type="hidden" name="NUMKLOMOV" value="<%=numKloMov%>"/>
        <input type="hidden" name="CODTIPOMOV" value="<%=codTipoMov%>"/>      
        <input type="hidden" name="DATINIZIOMOVPREC" value="<%=dataInizioMovPrec%>"/> 
        <input type="hidden" name="CODMONOTEMPOAVV" value="<%=codMonoTempoAvv%>"/>
        <input type="hidden" name="DATFINEMOVPREC" value="<%=dataFineMovPrec%>"/>
        <input type="hidden" name="CODTIPOAZIENDA" value="<%=codTipoAzienda%>"/>
        <input type="hidden" name="STRNUMALBOINTERINALI" value="<%=strNumAlboInterinali%>"/> 
        <input type="hidden" name="STRNUMREGISTROCOMMITT" value="<%=strNumRegistroCommitt%>"/>
        <input type="hidden" name="STRPOSINPS" value="<%=posInps%>"/>     
        <input type="hidden" name="STRPATINAIL" value="<%=patInail%>"/>  
        <input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=prgAziendaUtil%>"/> 
        <input type="hidden" name="PRGUNITAUTILIZ" value="<%=prgUnitaUtil%>"/>
        <input type="hidden" name="STRLUOGODILAVORO" value="<%=luogoDiLavoro%>"/>
        <input type="hidden" name="FLGINTERASSPROPRIA" value="<%=personaleInterno%>"/>
        <input type="hidden" name="numContratto" value="<%=numContratto%>">
        <input type="hidden" name="dataInizio" value="<%=dataInizio%>">
        <input type="hidden" name="dataFine" value="<%=dataFine%>">
        <input type="hidden" name="legaleRapp" value="<%=legaleRapp%>">
        <input type="hidden" name="numSoggetti" value="<%=numSoggetti%>">
        <input type="hidden" name="classeDip" value="<%=classeDip%>">
        <input type="hidden" name="CODMONOMOVDICH" value="<%=codMonoMovDich%>"/>
        <input type="hidden" name="DATFINEMOVEFFETTIVA" value="<%=datFineMovEff%>"/>      
        <input type="hidden" name="CODMONOTIPOFINE" value="<%=codMonoTipoFine%>"/>

        <input type="hidden" name="CODTIPOMOVPREC" value="<%=codtipomovprec%>"/>
        <input type="hidden" name="PRGMOVIMENTOPREC" value="<%=prgMovimentoPrec%>"/>
        <input type="hidden" name="NUMKLOMOVPREC" value="<%=numKloMovPrec%>"/>
        <input type="hidden" name="CODMONOTIPOFINEPREC" value="C"/>   

        <input type="hidden" name="ACTION" value="aggiorna"/>
        <input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
        <input type="hidden" name="COLLEGATO" value="<%=collegato%>"/>
        <input type="hidden" name="ACTIONPREC" value="<%=action%>"/>
        <input type="hidden" name="PROVENIENZA" value="linguetta"/>
       
        <input type="hidden" name="PAGE" value="MovDettaglioCessazioneConsultaPage"/>
        <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>

        <input type="hidden" name="numProtocollo" value="<%=numProtocolloV%>"/>
        <input type="hidden" name="numAnnoProt" value="<%=annoProtV%>"/>
        <input type="hidden" name="KLOCKPROT"  value="<%=kLockProt%>">
        <input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
        <input type="hidden" name="FLGAUTOCERTIFICAZIONE" value="<%=flgAutocertificazione%>" />
        <input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>" />
       	<!-- variabile utilizzata per la visualizzazione o meno della Nota Modifiche --> 
        <%  if(serviceRequest.containsAttribute("forzaVal")) {%>
           	<input type="hidden" name="forzaVal" value="1"/>
           	<input type="hidden" name="strNotaModifica" value="<%=strNotaModifica%>"/>
       <%}%>
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
	</body>
  <%@ include file="../common/include/GestioneScriptRisultati.inc" %>
</html>  


