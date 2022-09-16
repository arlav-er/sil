<!-- INSERIMENTO DETTAGLIO CESSAZIONE -->
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
  PageAttribs attributi = new PageAttribs(user, "MovDettaglioCessazioneInserisciPage");
  boolean canModify = attributi.containsButton("INSERISCI");
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");

  //Oggetti per l'applicazione dello stile
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

  //Guardo il contesto in cui opero
  String currentcontext = "inserisci";
  boolean inserisci = true;
  boolean salva = false;
  boolean valida = false;
  boolean rettifica = false;

  //guardo se provengo da un salvataggio o da un'altra linguetta
  String action = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTION");
  boolean aggiorna = action.equalsIgnoreCase("aggiorna");
  boolean naviga = action.equalsIgnoreCase("naviga");
  boolean consulta = action.equalsIgnoreCase("consulta");

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
  String strDisabile = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATOREDISABILE");
  String forzaInsEtaApprendista = StringUtils.getAttributeStrNotNull(serviceRequest, "FORZA_INSERIMENTO_ETA_APPRENDISTATO");
  if (forzaInsEtaApprendista.equals("")) forzaInsEtaApprendista = "false";
%>
<%@ include file="../common/include/_segnalazioniGgRit.inc" %>
<%@ include file="../GestioneOggettoMovimento.inc" %> 
<%
  //inizializzazione variabili
  String prgMovimento = "";
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
  String codMonoTempo = "";
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
  boolean movIsEnabled = false;
  String numGGEffettuatiAgr = null;
  String codMotAnnullamento = "";
  //
  String strCodiceFiscaleLav = "";
  String strCodiceFiscaleAz  = "";
  String codMonoTipo = "";
  //Utlizzata nel caso che si inserisca una cessazione NON collegata ad un movimento precedente
  String datInizioAVVperCVE = "";
  //Aggiunta di D'auria Giovanni 17/02/05
  String numOreSett="";
  String codOrario="";
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
  
  //Variabili per la gestione della protocollazione
  BigDecimal prgDoc = null;
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String     datProtV     = "";
  String     oraProtV     = "";
  String     docInOut     = "";
  String     docRif       = "";
  String     codStatoAttoV = "";  
  //Recupero CODCPI
  int cdnUt = user.getCodut();
  int cdnTipoGruppo = user.getCdnTipoGruppo();
  String codCpi="";
  if(cdnTipoGruppo == 1) {
    codCpi =  user.getCodRif();
  }
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
  
  // questo campo e' valorizzato a true quando il processor di controllo movimenti simili chiude conferma 
  // all'operatore dell'inserimento. Se poi arriva un'altra confirm bisogna evitare di ripassare per il controllo
  // dei movimenti simili  
  String confermaMovSimili = StringUtils.getAttributeStrNotNull(serviceRequest, "CONFERMA_CONTROLLO_MOV_SIMILI");
  if (confermaMovSimili.equals("")) confermaMovSimili="false";
  
  //Setto l'origine dei dati generali da recuperare in caso di inserimento o validazione, 
  //mi arrivano dall'oggetto in sessione se sto inserendo e l'oggetto è abilitato,
  //altrimenti dalla request
  SourceBean dataOrigin = serviceRequest;
  
  if (mov.isEnabled() && !consulta) {
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
  
  //Il codipoMov serve nel caso che l'utente lo abbia cambiato e stia facendo un aggiornamento,
  //è sempre letto dalla request  
  codTipoMov = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOV");
  //Devo riportare il codTipoAss e il codMonoTempo in cima alla pagina e passarlo agli altri dettagli 
  //e all'inserimento
  codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss"); 
  codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo");   

  //in caso di inserimento estraggo i dati generali necessari dalla request
  codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOAZIENDA");
  strNumAlboInterinali = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMALBOINTERINALI");
  strNumRegistroCommitt = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMREGISTROCOMMITT");
  posInps = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPOSINPS");
  patInail = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPATINAIL");
  datComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin, "datComunicaz");
  prgAziendaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDAUTILIZ");
  prgUnitaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITAUTILIZ");
  luogoDiLavoro = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLUOGODILAVORO");
  personaleInterno = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGINTERASSPROPRIA");
  numContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "numContratto");
  dataInizio = StringUtils.getAttributeStrNotNull(dataOrigin, "dataInizio");
  dataFine = StringUtils.getAttributeStrNotNull(dataOrigin, "dataFine");
  legaleRapp = StringUtils.getAttributeStrNotNull(dataOrigin, "legaleRapp");
  numSoggetti = StringUtils.getAttributeStrNotNull(dataOrigin, "numSoggetti");
  classeDip = StringUtils.getAttributeStrNotNull(dataOrigin, "classeDip");
  datInizioAVVperCVE = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVVCEV");
   
  //Setto l'origine dei dati da visualizzare, in fase di consultazione mi arrivano dal DB
  if (insertCollegato && (consulta || !mov.isEnabled())) { 
    dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMov.ROWS.ROW");
  }

  if (dataOrigin != null) {
    //I dati di azienda e lavoratore servono per le informazioni all'inizio della pagina
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
    //Gestione dei movimenti collegati
    //prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
    //prgMovimentoSucc = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOSUCC");
  
    datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov");
    //Aggiunta di Giovanni D'Auria il 18/02/05
     //per la visualizzazione dei campi aggiunti il 18/02/05   codOrario e numOreSett 
    codOrario=StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");
    numOreSett=StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett"); 
    //**********
    
    codtipocontratto=StringUtils.getAttributeStrNotNull(dataOrigin, "codtipocontratto");
    codMvCessazione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMvCessazione");
    codMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "codMansione");   
    codGrado = StringUtils.getAttributeStrNotNull(dataOrigin, "codGrado"); 
    numLivello = StringUtils.getAttributeStrNotNull(dataOrigin, "numLivello");   
    descrMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrMansione");   
    descrTipoMansione = StringUtils.getAttributeStrNotNull(dataOrigin, "descrTipoMansione");
    numGGEffettuatiAgr = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGEFFETTUATIAGR");
    if (codTipoAzienda.equalsIgnoreCase("INT")) {
  		datiniziomissione = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIORAPLAV");
   		datfinemissione = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINERAPLAV");
    }
  }
  //In fase di inserimento di un movimento collegato devo fare qualche elaborazione
  //Ma solo se non sto ripescando i dati dall'oggetto in sessione, altrimenti queste operazioni le ho
  //già fatte
  if (insertCollegato && !mov.isEnabled()) {
    if ((dataOrigin != null)) {
      //La data di cessazione è prevalorizzata alla data di fine del movimento precedente 
      //datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMov");    
      datInizioMov = "";
    }
    
    //Annullo il valore di default del motivo della cessazione
    codMvCessazione = "";
    
    //if (datInizioMov.equals("")) {
      //Se non ho una data prevista indico quella odierna
      //DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
      //Date currentDate = new Date();   
      //datInizioMov = formatter.format(currentDate);
    //}
  }


  //Estraggo dal DB i dati sui movimenti precedente e successivo e setto i boolean, solo se non si tratta di un 
  //movimento scollegato
  boolean precedente = false;
  boolean successivo = false;
  if (insertCollegato) {
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
  } else {
  	//Imposto i valori di default di alcune proprietà
  	codTipoAss = "NO0";
  	codMonoTempo = "I";	
  }
  
  //Oggetti per la generazione delle informazioni sul lavoratore e azienda
  InfCorrentiLav testataLav = null;
  InfCorrentiAzienda testataAz = null;
  if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) {
    testataLav = new InfCorrentiLav( RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    testataAz = new InfCorrentiAzienda(prgAzienda,prgUnita);
  }  

  movIsEnabled = mov.isEnabled();
  //abilito l'oggetto se non era già abilitato
  if (!mov.isEnabled() && !consulta) {
    mov.enable();
  }
  
  //Mi dice se devo mostrare il campo della data di inizio avviamento
  boolean mostraCampoInizioAvviamento = !insertCollegato;
  String strOnSubmit="";
  if(mostraCampoInizioAvviamento){
  	strOnSubmit="checkDatInizioMovImpattiCessazione(document.Frm1.datInizioMov, document.Frm1.DATINIZIOMOVPREC) && controlloLavAutonomo()  && checkNumOreSettimanali() && checkOrario()";
  }else{
  	strOnSubmit="checkDatInizioMovImpattiCessazione(document.Frm1.datInizioMov, document.Frm1.DATINIZIOMOVPREC) && controlloLavAutonomo()";
  }
  
%>

<html>
  <head>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
        <af:linkScript path="../../js/"/>
    <title>Dettaglio Movimento</title>
    <%@ include file="../../presel/Function_CommonRicercaCCNL.inc" %>    
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
    
    //Giovanni D'Auria 30/03/05 ore 11:11  
       function visualizzaCombo(){ 
      		var val;
  			if(document.Frm1.DATAINIZIOAVVCEV == undefined ){
  				val="";
  			}else{
       	       val=document.Frm1.DATAINIZIOAVVCEV.value; 
       	       var orario =document.getElementById("orario");
       	       if(val==""){
       	       	orario.style.display="none";
       	       }else{
       	       	orario.style.display="inline";
       	       }
       	    }
       }
    //************************************
    
      
    
    //Notifica del cambio di stato occupazionale
    function notificaCambioStatoOcc() {
	<% if (StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovInserisciCessazione.CAMBIOSTATOOCC").equals("TRUE")) {%>
    alert("Lo stato occupazionale del lavoratore è cambiato.");
	<%}%>
    }
	var contesto = 'inserisci';
    var inserisci = '<%=inserisci%>';
    var prgAziendaS = '<%=prgAzienda%>';
    var prgUnitaS = '<%=prgUnita%>';
    var oggettoInSessione = <%=movIsEnabled%>;
    -->
    </SCRIPT>
    <%-- necessari per il controllo del lavoro autonomo --%>
    <script  language="Javascript">
		var codiceFiscaleLav = "<%=strCodiceFiscaleLav %>";
		var codiceFiscaleAz = "<%=strCodiceFiscaleAz %>";		
    </script>
    <script type="text/javascript" src="../../js/movimenti/common/Indietro.js" language="JavaScript"></script> 
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/cessazione/func_cessazione.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_confirmDaStOcc.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_confirmDaControlloMov.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/Linguette.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>
    
    
    
    <%@ include file="../common/include/calcolaDiffGiorni.inc" %>
    <%@ include file="../common/include/_funzioniGenerali.inc" %>
  </head>

  <body class="gestione" onload="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');rinfresca();calcolaDiffGiorni(document.Frm1.datInizioMov, document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange,oggettoInSessione);notificaCambioStatoOcc();gestVisualGiorniRitardo();impostaGrado();visualizzaCombo()">
<% if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) { testataLav.show(out); }%>
<% if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) { testataAz.show(out); }%>  
  <%@ include file="../common/include/GestioneRisultati.inc" %>
    <%@ include file="../../movimenti/common/include/LinguetteCessazione.inc" %>
    <center>
      <af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="<%=strOnSubmit%>">
        <%out.print(htmlStreamTop);%>
        <%@ include file="../../movimenti/common/include/_protocollazione.inc" %>
        <table class="main" border="0">
          <%@ include file="../../movimenti/common/include/InfoTestataMovimento.inc" %>
          <%@ include file="include/campi_cessazione.inc" %>  
           
        </table>          
        <%out.print(htmlStreamBottom);%>

        <input type="hidden" name="DATCOMUNICAZ" value="<%=datComunicaz%>"/> 
        <input type="hidden" name="CODTIPOASS" value="<%=codTipoAss%>"/>        
        <input type="hidden" name="CODMONOTIPO" value="<%=codMonoTipo%>"/>
    <%if(!insertCollegato) {%>
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
       
        <input type="hidden" name="PAGE" value="InserisciMovimentoCompletoPage"/>
        <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<%--
        <input type="hidden" name="numProtocollo" value="<%=numProtocolloV%>"/>
        <input type="hidden" name="numAnnoProt" value="<%=annoProtV%>"/>
--%>       
        <input type="hidden" name="KLOCKPROT"  value="<%=kLockProt%>">
        <input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
        <input type="hidden" name="FLGAUTOCERTIFICAZIONE" value="<%=flgAutocertificazione%>" />
        <input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>" />
		<input type="hidden" name="FORZA_INSERIMENTO" value="false"/>
		<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false"/>
		<input type="hidden" name="FORZA_INSERIMENTO_ETA_APPRENDISTATO" value="<%=forzaInsEtaApprendista%>"/>
		<input type="hidden" name="CONFERMA_CONTROLLO_MOV_SIMILI" value="<%=confermaMovSimili%>"/>
		<!--Gestione comunicazioni in ritardo per lavoratori disabili-->
		<input type="hidden" name="LAVORATOREDISABILE" value="<%=strDisabile%>"/>
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
<table><tr><td align="center"><input type="submit" class="pulsanti" name="submitbutton" value="Inserisci" onclick="resetFlagForzatura();"/></td><tr>
       <tr><td>&nbsp;</td><tr>
</table>

<%@ include file="../common/include/GestioneCollegati.inc" %>
<%@ include file="../common/include/PulsanteRitornoLista.inc" %>

</af:form>      
</center> 
</body>

  <%@ include file="../common/include/GestioneScriptRisultati.inc" %>

</html>  


