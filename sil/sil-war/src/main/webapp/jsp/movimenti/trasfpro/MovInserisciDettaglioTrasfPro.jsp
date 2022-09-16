<!-- INSERIMENTO TRASF/PROROGA -->
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
  PageAttribs attributi = new PageAttribs(user, "MovDettaglioTrasfProInserisciPage");
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
  boolean consulta = false;

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
  String codMonoTempo = "";
  String datFineMov = "";
  String codOrario = "";
  String numOreSett = "";
  String codMansione = "";
  String descrMansione = "";
  String descrTipoMansione = "";
  String strDesAttivita = "";
  String codGrado = "";
  String codGradoPrec = "";
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
  String dataFineMovPrec = "";
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
  boolean movIsEnabled = false;
  String autorizzaDurataTD = "";
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
  // CM 07/02/2007 savino: incentivi art. 13. E' stato modificato il file campi_trasfpro.inc 
  String datFineSgravio = "";
  String decImportoConcesso = "";
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
  // questo campo e' valorizzato a true quando il processor di controllo movimenti simili chiude conferma 
  // all'operatore dell'inserimento. Se poi arriva un'altra confirm bisogna evitare di ripassare per il controllo
  // dei movimenti simili  
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
  
  //in caso di inserimento estraggo i dati generali necessari all'inserimento, altrimenti mi bastano i progressivi del movimento o
  //della tabella di appoggio
  if (dataOrigin != null) {
    codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOAZIENDA");
    strNumAlboInterinali = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMALBOINTERINALI");
    strNumRegistroCommitt = StringUtils.getAttributeStrNotNull(dataOrigin, "STRNUMREGISTROCOMMITT");
    posInps = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPOSINPS");
    patInail = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPATINAIL");
    datComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin, "datComunicaz");
    prgAziendaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDAUTILIZ");
    prgUnitaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITAUTILIZ");
    luogoDiLavoro = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLUOGODILAVORO");
    numContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "numContratto");
    dataInizio = StringUtils.getAttributeStrNotNull(dataOrigin, "dataInizio");
    dataFine = StringUtils.getAttributeStrNotNull(dataOrigin, "dataFine");
    legaleRapp = StringUtils.getAttributeStrNotNull(dataOrigin, "legaleRapp");
    numSoggetti = StringUtils.getAttributeStrNotNull(dataOrigin, "numSoggetti");
    classeDip = StringUtils.getAttributeStrNotNull(dataOrigin, "classeDip");
    personaleInterno = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGINTERASSPROPRIA");
  } 


  if (insertCollegato && !mov.isEnabled()) {dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMov.ROWS.ROW");}

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

    //dati della trasf/pro
    datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov");
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
    codGradoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "codGradoPrec");
    decRetribuzioneMen = StringUtils.getAttributeStrNotNull(dataOrigin, "decRetribuzioneMen");
    numProroghe = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMPROROGHE");  
    // 06/02/2007 Savino: CM incentivi ant. 13
    datFineSgravio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINESGRAVIO");
    decImportoConcesso = StringUtils.getAttributeStrNotNull(dataOrigin, "DECIMPORTOCONCESSO");
    
    //Autorizzazione alla durata dei movimenti a TD data dall'utente
    autorizzaDurataTD = StringUtils.getAttributeStrNotNull(dataOrigin, "FLGAUTORIZZADURATATD");
    if (codTipoAzienda.equalsIgnoreCase("INT")) {
    	datiniziomissione = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIORAPLAV");
    	datfinemissione = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINERAPLAV");
   	}
  }
  
  //Estraggo dal DB i dati sui movimenti precedente e successivo e setto i boolean
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
  	if (codTipoAzienda.equalsIgnoreCase("INT")) {
  		codTipoAss = "AF2";
  	} else {
  		codTipoAss = "TR2";
  	}
  	codMonoTempo = "I";
  }
  
  //Oggetti per la generazione delle informazioni sul lavoratore e azienda
  InfCorrentiLav testataLav = null;
  InfCorrentiAzienda testataAz = null;
  if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) {
    testataLav = new InfCorrentiLav( RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    testataAz = new InfCorrentiAzienda(prgAzienda,prgUnita);
  }  

  //Se sto inserendo una proroga devo modificare le date di inizio e fine per uniformarle a quelle del
  //movimento precedente estratto dal DB e devo incrementare il numero di proroghe fatte (estratto sopra)
  //Ma solo se non sto ripescando i dati dall'oggetto in sessione, altrimenti queste operazioni le ho
  //già fatte
  //MORALE DELLA FAVOLA: il seguente blocco viene eseguito UNA volta sola la prima volta che viene caricata la pagina.
  if (!mov.isEnabled()) {
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    Date currentDate = new Date();    
    if (codTipoMov.equalsIgnoreCase("PRO")) {
      if (precedente) {
	      //Se ho una proroga la data di inizio deve essere il giorno successivo alla data 
	      //fine mov precedente
	      try {
	        //Utilizzo il Calendar per aggiungere un giorno alla data di fine Movimento precedente
	        Date d = formatter.parse(datFineMov);
	        GregorianCalendar gc = new GregorianCalendar();
	        gc.setTime(d);
	        gc.add(Calendar.DATE, 1);
	        datInizioMov = formatter.format(gc.getTime());
	      } catch (ParseException pe) {
	        //Se ho problemi non indico nulla
	        datInizioMov = "";}
	      try {
	        datFineMov = (String) mov.getField("datFineMov");
	        if (datFineMov == null) {datFineMov = "";}
	      } catch (IllegalArgumentException iae) {
	        datFineMov = "";
	      } 
	      
	      if (!numProroghe.equals("")) {
            numProroghe = String.valueOf((new Integer(numProroghe).intValue()) + 1);
      	  }
	  } else {
	  	//Se non ho il precedente imposto i valori al default
	  	datInizioMov = formatter.format(currentDate);
	    datFineMov = "";
	    numProroghe = "0";	  	
	  }

    } else if(codTipoMov.equalsIgnoreCase("TRA")) {
      //In caso di trasformazione suggerisco la data odierna
      datInizioMov = ""; //formatter.format(currentDate);
	  if((datFineMov.equals("") || (datFineMov==null)) && codMonoTempoAvv.equals("D") )  
	    datFineMov = (dataFineMovPrec == null) ? "" : dataFineMovPrec;
	  if(codMonoTempo.equals("I") )  
	    datFineMov = "";
    }
    
    //Preseleziono i dati del CCNL su quelli dell'azienda se non ho un mov precedente da cui raccoglierli
    if (!precedente) {
    	codCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNLAz");
    	strDescrizioneCCNL = StringUtils.getAttributeStrNotNull(dataOrigin, "descrCCNLAz"); 
    }
  }//=======================================================================
  movIsEnabled = mov.isEnabled();
  //abilito l'oggetto se non era già abilitato
  if (!mov.isEnabled() && !consulta) {
    mov.enable();
  }

  boolean readOnlyDatFine = !canModify || codTipoMov.equals("TRA");
  
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
    var consulta = <%=consulta%>;
    var tempoiniziale = '<%=codMonoTempo%>';
    var redditoiniziale = '<%=decRetribuzioneMen%>';  
    var contesto = 'inserisci';
    var codTipoAzienda = '<%=codTipoAzienda%>';
    var precedente = <%=precedente%>;
    var inserisci = '<%=inserisci%>';
    var oggettoInSessione = <%=movIsEnabled%>;
    
    //Notifica del cambio di stato occupazionale
    function notificaCambioStatoOcc() {
	<% if (StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovInserisciTrasfPro.CAMBIOSTATOOCC").equals("TRUE")) {%>
    alert("Lo stato occupazionale del lavoratore è cambiato.");
	<%}%>
    }   
    -->
	</SCRIPT>
    <%-- necessari per il controllo del lavoro autonomo --%>
    <script  language="Javascript">
		var codiceFiscaleLav = "<%=strCodiceFiscaleLav %>";
		var codiceFiscaleAz = "<%=strCodiceFiscaleAz %>";		
    </script>
    <script type="text/javascript" src="../../js/movimenti/common/Indietro.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script>    
    <script type="text/javascript" src="../../js/movimenti/common/_confirmDaStOcc.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_confirmDaControlloMov.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/trasfpro/func_trasfpro.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/Linguette.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>
    
    <%@ include file="../common/include/calcolaDiffGiorni.inc" %>
    <%@ include file="../common/include/_funzioniGenerali.inc" %>
    <%@ include file="../common/include/GestioneDataFine.inc" %>
  </head>

  <body class="gestione" onload="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');rinfresca();notificaCambioStatoOcc();calcolaDiffGiorni(document.Frm1.datInizioMov, document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange,oggettoInSessione);gestVisualGiorniRitardo();riportaGradoPrec()">
  <% if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) { testataLav.show(out); }%>
  <% if (!prgAzienda.equals("") && !prgUnita.equals("") && !cdnLavoratore.equals("")) { testataAz.show(out); }%>

	<%@ include file="../common/include/GestioneRisultati.inc" %>
    <%@ include file="../common/include/LinguetteTrasfPro.inc" %>
    <center>
      <af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="checkDatInizioMovImpatti(document.Frm1.datInizioMov)  && controlloLavAutonomo() && checkCambioGrado()">
        <%out.print(htmlStreamTop);%>
        <%@ include file="../../movimenti/common/include/_protocollazione.inc" %>
        <table class="main" border="0" width="96%" cellpadding="0" cellspacing="0">
			<%@ include file="../../movimenti/common/include/InfoTestataMovimento.inc" %>
<%-- CM 09/02/2007 Savino: viene incluso un nuovo file che continene i campi degli incentivi art. 13. Il file originario viene ancora usato dalla
pagina della validazione. --%>
			<%@ include file="../trasfpro/include/campi_trasfpro_cm.inc" %>
        </table>
        <%out.print(htmlStreamBottom);%>

        <input type="hidden" name="DATCOMUNICAZ" value="<%=datComunicaz%>"/> 
        <input type="hidden" name="CODTIPOASS" value="<%=codTipoAss%>"/>        
        <input type="hidden" name="CODMONOTIPO" value="<%=codMonoTipo%>"/>
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
        <input type="hidden" name="numContratto" value="<%=numContratto%>">
        <input type="hidden" name="dataInizio" value="<%=dataInizio%>">
        <input type="hidden" name="dataFine" value="<%=dataFine%>">
        <input type="hidden" name="legaleRapp" value="<%=legaleRapp%>">
        <input type="hidden" name="numSoggetti" value="<%=numSoggetti%>">
        <input type="hidden" name="classeDip" value="<%=classeDip%>">
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
        <input type="hidden" name="CODCPI" value="<%=codCpi%>" />
        <input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>" />
        <center>
        <input type="submit" class="pulsanti" name="submitbutton" value="Inserisci" onclick="resetFlagForzatura();"/>
        <input type="hidden" name="FORZA_INSERIMENTO" value="false"/>
        <input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false"/>
        <input type="hidden" name="FORZA_INSERIMENTO_ETA_APPRENDISTATO" value="<%=forzaInsEtaApprendista%>"/>
        <input type="hidden" name="CONFERMA_CONTROLLO_MOV_SIMILI" value="<%=confermaMovSimili%>"/>

	    <!--Gestione autorizzazione per movimenti a TD-->
	    <input type="hidden" name="FLGAUTORIZZADURATATD" value="<%=autorizzaDurataTD%>"/>
	    
	    <!-- Gestione grado per trasormazione-->
	    <input type="hidden" name="codGradoPrec" value="<%=codGradoPrec%>"/>
	    
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
        </center>
        <br/>
        <%@ include file="../common/include/GestioneCollegati.inc" %>
		<%@ include file="../common/include/PulsanteRitornoLista.inc" %>
      </af:form>      
		</center>
	</body>
  <%@ include file="../common/include/GestioneScriptRisultati.inc" %>
</html>

