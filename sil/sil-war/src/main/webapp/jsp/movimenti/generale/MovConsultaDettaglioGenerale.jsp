<!-- CONSULTAZIONE DETTAGLIO GENERALE DEL MOVIMENTO -->
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
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*" %>
 
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%   
  String codInterinale = "INT";
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "MovDettaglioGeneraleConsultaPage");
  //boolean canModify = attributi.containsButton("SALVA");
  boolean canModifyProtocol = attributi.containsButton("SalvaProtocollo");  
  boolean canModifyStato = attributi.containsButton("SalvaProtocollo");
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  boolean canModify = canModifyProtocol;
  boolean canModifyMov = attributi.containsButton("MODIFICA_MOV");
  
  
  //Oggetti per l'applicazione dello stile
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
  //Oggetti per l'applicazione dello stile
  String htmlStreamTop2 = StyleUtils.roundTopTable(false);
  String htmlStreamBottom2 = StyleUtils.roundBottomTable(false);

  String currentcontext = "consulta";
  boolean inserisci = false;
  boolean valida = false;  
  boolean rettifica = false;
  
  //Guardo se provengo da una linguetta
  String provenienza = StringUtils.getAttributeStrNotNull(serviceRequest, "PROVENIENZA");
  boolean daLinguetta =  provenienza.equalsIgnoreCase("linguetta");

  //guardo se precedentemente ero in consultazione
  String actionprec = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTIONPREC");
  boolean actionprecaggiorna = actionprec.equalsIgnoreCase("aggiorna");
  boolean actionprecnaviga = actionprec.equalsIgnoreCase("naviga");
  boolean actionprecconsulta = actionprec.equalsIgnoreCase("consulta");

%>
<%@ include file="../common/include/_segnalazioniGgRit.inc" %>
<%
  //inizializzazione variabili
  String prgMovimento = "";  
  String prgMovimentoApp = ""; 
  String prgMovimentoPrec = "";
  String prgMovimentoSucc = "";
  String prgMovimentoRett = "";
  String dataInizioAvv = "";
  String dataInizioMovPrec = "";  
  String codTipoAss = "";
  String codQualificaSrq = "";
  String dataFineMovPrec = "";
  String codMonoTempoAvv = "";
  String prgAzienda = "";
  String prgUnitaValMov = "";
  String prgUnita = "";
  String cdnLavoratore = "";
  String strPartitaIvaAz = "";
  String strCodiceFiscaleAz = "";
  String strRagioneSocialeAz = "";
  String strIndirizzoUAz = "";
  String strComuneUAz = "";
  String codComuneUAz = "";
  String strCapUAz = "";
  String strTelUAz = "";
  String strFaxUAz = "";
  String codAtecoUAz = "";
  String strDesAtecoUAz = "";
  String codCCNLAz = "";
  String descrCCNLAz = "";
  String natGiuridicaAz = "";
  String codNatGiuridicaAz = "";  
  String strPartitaIvaAzUtil = "";
  String strCodiceFiscaleAzUtil = "";
  String strRagioneSocialeAzUtil = "";
  String strIndirizzoUAzUtil = "";
  String strComuneUAzUtil = "";
  String descrCCNLAzUtil = "";
  String strCodiceFiscaleLav = "";
  String strNomeLav = "";
  String strCognomeLav = "";
  String datNascLav = "";
  String codtipomov = "";
  String codTipoMov = "";//Di supporto per omogeneità con le altre pagine
  String codtipomovprec = "";  
  String datComunicaz = "";
  String numKloMov = "";
  String numKloMovPrec = "";  
  String posInps = "";
  String posInps1 = "";
  String posInps2 = "";
  String patInail = "";
  String patInail1 = "";
  String patInail2 = "";  
  String codTipoAzienda = "";
  String codTipoAziendaUtil = "";
  String descrTipoAzienda = "";
  String descrTipoAziendaUtil = "";
  String strNumAlboInterinali = "";
  String strNumRegistroCommitt = "";
  String flgInterAssPropria = "";
  String strLuogoDiLavoro = "";
  String prgAziendaUtil = "";
  String prgAziendaValMov = "";
  String prgUnitaUtil = ""; 
  String seguente = "";
  String datFineMovEff = "";
  String codMonoTipoFine = "";
  String codMonoMovDich = "";
  String strFlgCfOk = "";
  String strFlgDatiOk = "";
  String codStatoAtto = "";
  String codMonoProv = "";
  String strReferente = "";
  String codMonoTempo = "";
  String datFineMov = "";
  String codMotAnnullamento = "";
  String codCpi = "";
  
  String strNotaModifica = "";
  StringBuffer strUrlBack = new StringBuffer();	
  
  	
  //Giovanni D'Auria 21/02/2005 inizio
	String codOrario="";
	String numOreSett="";
  //fine
  	
  
  //Variabili per la gestione della protocollazione
  BigDecimal prgDoc = null;
  String prAutomatica     = null; 
  String estReportDefautl = null;
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String     datProtV     = "";
  String     oraProtV     = "";
  String     docInOut     = "";
  String     docRif       = "";
  BigDecimal kLockProt    = null;
  boolean numProtEditable = false;
  Vector rows             = null;
  SourceBean row          = null;
  Vector vectRicercaAz    = null;
  Vector vectRicercaUnitaAz = null;
  Vector vectRicercaLav   = null;
 
  String ragSocDB = "";
  String pIvaDB = "";
  String codNatGiuridicaDB = "";
  String codTipoAzDB = "";
  String codCnnlDB = "";
  String strPatInailDB = "";
  String strNumAlboInterDB = "";
  String strPosInpsDB = "";
  String codAtecoDB = "";
  String strTelDB = "";
  String strFaxDB = "";
  String strIndirizzoDB = "";
  String codComuneDB = "";
  String capDB = "";
  String cdnLavoratoreDB = "";
  String strNomeLavoratore = "";
  String nomeLavDB = "";
  String cognomeLavDB = "";
  String datNascDB = "";
  String codFiscaleLavDB = "";
  SourceBean goInserisciLav = null;
  String strPageSalto = "";
  String cdnFunzione = "";
  String numContratto = "";
  String dataInizio = "";
  String dataFine = "";
  String legaleRapp = "";
  String numSoggetti = "";
  String classeDip = "";  
  String numGgTraMovComunicaz = "";
  String datInizioMov = "";
  String codTipoTitoloStudio = "";
  boolean hasDocuments =false;
  

  boolean isSanato = false;
  String codTipoDich = "";

  //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
  String codtipoenteprev = "";
  String strcodiceenteprev = "";
  String flgsocio = "";
  String codtipotrasf = "";
  String dataFineDistacco = "";
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

  String strEnteRilascio  = "";
  strEnteRilascio = StringUtils.getAttributeStrNotNull(serviceRequest, "STRENTERILASCIO");
  String strLavCollMirato = StringUtils.getAttributeStrNotNull(serviceRequest, "LAVORATORECOLLMIRATO");
  
  SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMov.ROWS.ROW");
 
  //13/06/2005 Davide G. > Inserimento inf. operatore
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  cdnUtins        = StringUtils.getAttributeStrNotNull(dataOrigin,"CDNUTINS");
  dtmins          = StringUtils.getAttributeStrNotNull(dataOrigin,"DTMINS");
  cdnUtmod        = StringUtils.getAttributeStrNotNull(dataOrigin,"CDNUTMOD");
  dtmmod          = StringUtils.getAttributeStrNotNull(dataOrigin,"DTMMOD");
  Testata operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  //fine	
  
  //Estraggo i dati della pagina dall'origine tranne nel caso di inserimento senza 
  //precedente con provenienza da lista.
  if (dataOrigin != null) {
  	if (strEnteRilascio.equals("")) {
  		strEnteRilascio = StringUtils.getAttributeStrNotNull(dataOrigin, "strEnteRilascio");
  	}
    //Per il calcolo del ritardo in caso di salvataggio
    datInizioMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datInizioMov"); 
    
    codStatoAtto = StringUtils.getAttributeStrNotNull(dataOrigin, "CODSTATOATTO");
    codMotAnnullamento = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMOTANNULLAMENTO");
    
    //Progressivi se ci sono
    prgMovimento = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTO");
    numKloMov = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMKLOMOV");
    prgMovimentoPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOPREC");
    numKloMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMKLOMOVPREC");  
    prgMovimentoSucc = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOSUCC");
    prgMovimentoApp = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTOAPP");
    prgMovimentoRett = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGMOVIMENTORETT");
    prgAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDA");
    prgUnita = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITA");
    cdnLavoratore = StringUtils.getAttributeStrNotNull(dataOrigin, "CDNLAVORATORE");
    prgAziendaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDAUTILIZ");
    prgUnitaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITAUTILIZ");
    numContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTNUMCONTRATTO");
    dataInizio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTINIZIOCONTRATTO");
    dataFine = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTFINECONTRATTO");
    legaleRapp = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTRAP");
    numSoggetti = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMAZINTSOGGETTI");
    classeDip = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMAZINTDIPENDENTI");
    //Recupero dettagli documento(Numprotocollo, anno)
    rows = serviceResponse.getAttributeAsVector("DettagliDocumentoDaPrgMov.ROWS.ROW");
    Vector prgdocs = new Vector();
    for(int k = 0; k < rows.size() ; k++){
    	SourceBean sb = (SourceBean)rows.get(k);
    	prgdocs.add(sb.getAttribute("prgdocumento"));
    	
    }
    serviceResponse.getAttributeAsVector("DettagliDocumentoDaPrgMov.ROWS.ROW.PRGDOCUMENTO");

    //controllo se ci sono più documenti associati al movimento
    hasDocuments = rows.size()>1;
    
     if((rows != null) && (rows.size()>0))
     {
       row = (SourceBean)rows.get(rows.size()-1);
       prgDoc        = (BigDecimal) row.getAttribute("prgdocumento");
       numProtV      = SourceBeanUtils.getAttrBigDecimal(row, "numProtocollo", null);
       numAnnoProtV  = (BigDecimal) row.getAttribute("numannoprot"); 
       datProtV      = StringUtils.getAttributeStrNotNull(row,"datprot");
       oraProtV      = StringUtils.getAttributeStrNotNull(row,"oraprot");
       docInOut      = StringUtils.getAttributeStrNotNull(row,"codmonoio");
       docRif       = StringUtils.getAttributeStrNotNull(row,"riferimento");
      
     }
     
     
     //Memorizzo il prgDocumento nella sessione per poi ottenerlo nelle pagine successive
     //bisogna memorizzarlo nella sessione perchè può capitare, come nel caso si trovino movimenti simili, che 
     //venga chiesto all'operatore se desidera forzare l'inserimento oppure no.
     //In tal caso si avrà un ricaricamento della pagina e quindi si perderanno i dati salvati nella request.
     SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
     sessione.setAttribute("PRGDOCUMENTI", prgdocs);
     
   
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
    //Escapizzo l'azienda utilizzatrice
    strRagioneSocialeAzUtil = StringUtils.replace(strRagioneSocialeAzUtil, "\"", "'");
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
    codtipomov = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOV");
    codTipoMov = codtipomov;
    datComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin, "datComunicaz");
    strLuogoDiLavoro = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLUOGODILAVORO");
    dataInizioAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAINIZIOAVV");
    dataInizioMovPrec = StringUtils.getAttributeStrNotNull(dataOrigin, "DATINIZIOMOVPREC");        
    datFineMovEff = StringUtils.getAttributeStrNotNull(dataOrigin, "DATFINEMOVEFFETTIVA");
    codMonoTipoFine = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTIPOFINE");
    codMonoMovDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOMOVDICH");
    codMonoProv = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOPROV");    
    //Devo riportare il codTipoAss in cima alla pagina e passarlo agli altri dettagli e all'inserimento
    codTipoAss = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAss"); 
    codMonoTempo = StringUtils.getAttributeStrNotNull(dataOrigin, "codMonoTempo");
    datFineMov = StringUtils.getAttributeStrNotNull(dataOrigin, "datFineMov");    
    codMonoTempoAvv = StringUtils.getAttributeStrNotNull(dataOrigin, "CODMONOTEMPOAVV");
    codtipomovprec = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOMOVPREC");
    strReferente = StringUtils.getAttributeStrNotNull(dataOrigin, "STRREFERENTE");
    //Visualizzazione ritardo
    numGgTraMovComunicaz = StringUtils.getAttributeStrNotNull(dataOrigin,"NUMGGTRAMOVCOMUNICAZIONE");
    codTipoTitoloStudio = StringUtils.getAttributeStrNotNull(dataOrigin, "CODTIPOTITOLOlav");
    
    //Giovanni D'Auria 21/02/2005 inizio
	codOrario = StringUtils.getAttributeStrNotNull(dataOrigin, "codOrario");
	numOreSett = StringUtils.getAttributeStrNotNull(dataOrigin, "numOreSett");
	//fine
	codQualificaSrq = StringUtils.getAttributeStrNotNull(dataOrigin, "CODQUALIFICASRQ");
	codTipoDich = StringUtils.getAttributeStrNotNull(dataOrigin, "CODICEDICH");
	// modifica comunicazione
	codCpi = StringUtils.getAttributeStrNotNull(dataOrigin, "CODCPI");
	
	if(!codTipoDich.equals("")){
		isSanato=true;
	}
    //DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO
    codtipoenteprev     = StringUtils.getAttributeStrNotNull(dataOrigin,"CODENTE");
    strcodiceenteprev   = StringUtils.getAttributeStrNotNull(dataOrigin,"STRCODICEENTEPREV");
    codtipotrasf        = StringUtils.getAttributeStrNotNull(dataOrigin,"CODTIPOTRASF");
    dataFineDistacco    = StringUtils.getAttributeStrNotNull(dataOrigin,"DATFINEDISTACCO");
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
  
  strNotaModifica = StringUtils.getAttributeStrNotNull(dataOrigin,"STRNOTE");

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
  posInps2 = posInps.substring((posInps.length() >= 2 ? 2 : posInps.length()), (posInps.length() >= 15 ? 15 : posInps.length()));
  //Gestione spezzatino Pat Inail
  patInail1 = patInail.substring(0, (patInail.length() >= 8 ? 8 : patInail.length()));
  patInail2 = patInail.substring((patInail.length() >= 8 ? 8 : patInail.length()), (patInail.length() >= 10 ? 10 : patInail.length()));
 
  //Decodifica del CODMONOPROV
  String decMonoProv = "";
  if (codMonoProv.equalsIgnoreCase("M")) {
  decMonoProv = "Movimento inserito <strong>Manualmente</strong>";
  } else   if (codMonoProv.equalsIgnoreCase("S")) {
    decMonoProv = "Movimento inserito <strong>Da SARE</strong>";  
  } else   if (codMonoProv.equalsIgnoreCase("F")) {
    decMonoProv = "Movimento importato <strong>Da File</strong>";   
  } else   if (codMonoProv.equalsIgnoreCase("P")) {
    decMonoProv = "Movimento <strong>Da Prolabor</strong>"; 
  } else   if (codMonoProv.equalsIgnoreCase("C")) {
    decMonoProv = "Movimento <strong>Da Cooperazione</strong>"; 
  }
  
  
  if (strFlgDatiOk!=null){
    if (strFlgDatiOk.equalsIgnoreCase("S")){
      strFlgDatiOk = "Si";
    }else
        if (strFlgDatiOk.equalsIgnoreCase("N")){
          strFlgDatiOk = "No";
        }
  }
 
  if (strFlgCfOk!=null){
      if (strFlgCfOk.equalsIgnoreCase("S")){
        strFlgCfOk = "Si";
      }else
          if (strFlgCfOk.equalsIgnoreCase("N")){
            strFlgCfOk = "No";
          }
  }    
  //Substring delle info troppo lunghe
  String strRagioneSocialeAzTrunc = strRagioneSocialeAz;
  if (strRagioneSocialeAz.length() >= 30){
    strRagioneSocialeAzTrunc = strRagioneSocialeAz.substring(0,26) + "...";
  }
  if (strDesAtecoUAz.length() >= 22){
    strDesAtecoUAz = strDesAtecoUAz.substring(0,19) + "...";
  }
  if (natGiuridicaAz.length() >= 30){
    natGiuridicaAz = natGiuridicaAz.substring(0,26) + "...";
  }
  
  //Variabili per la gestione delle linguette con file di include
  String codTipoMovCorr = codtipomov;
  String codTipoMovPrec = codtipomovprec;
  boolean consulta = true;
  boolean precedente = (prgMovimentoPrec.equals("") ? false : true);
  canModify = false;

  String urlBack = "";

  urlBack ="PAGE=MovDettaglioGeneraleConsultaPage" +
	  	       "&PRGMOVIMENTO=" + prgMovimento + 
	  	       "&CDNFUNZIONE=" + _funzione +
	  	       "&PROVENIENZA="+ provenienza +
	  	       "&PAGERITORNOLISTA=" + (String) StringUtils.getAttributeStrNotNull(serviceRequest,"PAGERITORNOLISTA");
	
	if(serviceRequest.containsAttribute("forzaVal")) {
		String addParameter = "&forzaVal=1";
		urlBack += addParameter;
	}
		  					

 
%>
 
<html>
  <head>
    <%@ include file="../../global/fieldChanged.inc" %>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
        <af:linkScript path="../../js/"/>
    <title>Consulta Dettaglio Movimento Generele</title>
    <%-- ../jsp/movimenti/generale/MovConsultaDettaglioGenerele.jsp --%>
	<%-- include lo script che permette di aprire la PopUp che gestisce i documenti (salvataggio/protocollazione) --%>
	<% String queryString = null; %>
	<%@ include file="../../documenti/_apriGestioneDoc.inc"%>
	<script language="Javascript" src="../../js/docAssocia.js"></script>
    <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>    
    var aziendaUtilVar = "N";
    <% if ( (!prgAziendaUtil.equals("")) && (!prgUnitaUtil.equals("")) ){%>
      aziendaUtilVar = "S";
    <%}%>
    
    var codtipomovprec = '<%=codtipomovprec%>';

    <% //Variabili da passare ai file .js %>
    var _funzione = '<%=_funzione%>';
    var canModify = '<%=canModify%>';
    var cdnFunzione = '<%=cdnFunzione%>';
    var codTipoMovCorr = '<%=codTipoMovCorr%>'; 
    var codInterinale = '<%=codInterinale%>';
    var codNatGiuridicaAz = '<%=codNatGiuridicaAz%>';
    var prgAziendaValMov = '<%=prgAziendaValMov%>';
    var codTipoAzienda = '<%=codTipoAzienda%>';
    var strPageSalto = '<%=strPageSalto%>';
    var codTipoMovPrec = '<%=codTipoMovPrec%>';
    var inserisci = 'false';
    var contesto = 'consulta';
    var precedente = <%=(prgMovimentoPrec.equals("") ? "false" : "true")%>;
    var successivo = <%=(prgMovimentoSucc.equals("") ? "false" : "true")%>;
    var isSanato = <%=isSanato%>;


    function stampaMovimento(prgMovimento){
		HTTPrequest = "<%=getQueryString(urlBack)%>";
		apriGestioneDoc('RPT_MOVIMENTI','&TIPOSTAMPA=SINGOLO&prgMovimento='+ prgMovimento,'STMOV');
    }
    function modificaMovimento(){
    	var w=700; 
    	var l=((screen.availWidth)-w)/2;
		var h=800;   
		var t=((screen.availHeight)-h)/2;
  		var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  		var tit = '_blank';
		var url = "AdapterHTTP?PAGE=ModificaMovimentoPage&PRGMOV=" + <%=prgMovimento%> + "&CDNFUNZIONE=" + _funzione + "&PRECEDENTE=" + precedente;
		window.open(url,tit,feat);

    
    }
    
    </SCRIPT>

    <!-- Gestione profilatura -->
    <%@ include file="../common/include/_gestioneProfili.inc" %>

    <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiRicercaSoggetto.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/Linguette.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/apriAziendaUtil.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/generale/apriAziendaMov.js" language="JavaScript"></script>
   	<script type="text/javascript" src="../../js/movimenti/generale/func_generale.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/Avanti.js" language="JavaScript"></script> 
    <script type="text/javascript" src="../../js/movimenti/common/_commonFunction.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/_confirmDaStOcc.js" language="JavaScript"></script>
    <%@ include file="../common/include/calcolaDiffGiorni.inc" %>
    <%@ include file="../common/include/_funzioniGenerali.inc" %> 
    <%@ include file="../common/include/CollegaSuccessivo.inc" %>       
  </head>
 
  <body class="gestione" onload="inizializzaCollegati('<%=prgMovimentoPrec%>', '<%=prgMovimentoSucc%>');rinfresca();visualizzaInterinali('<%=codTipoAzienda%>', <%=assInterna%>);gestioneProfilo(document.Frm1.CODTIPOMOV);gestisciRitardo();">
    	<%@ include file="../../movimenti/common/include/LinguetteGenerale.inc" %>
    <%-- 
        Gestione profilatura: posizionato qui per motivi di compatibilità con il resto 
        delle variabili della pagina.
    --%>
    <%@ include file="../common/include/_commonFuncProfili.inc" %>
    
    <%@ include file="../common/include/GestioneRisultati.inc" %>
    
    <af:showMessages prefix="M_MovSalvaGenConsulta"/>
    <af:showErrors/>
      <af:form name="Frm1" method="POST" action="AdapterHTTP">
      <center>
        <%out.print(htmlStreamTop);%>
        <!-- Parte della protocollazione --> 
        <%@ include file="../../movimenti/generale/include/_protocollazione.inc" %>
        <table class="main" border="0" width="96%" cellpadding="0" cellspacing="0">
        <%@ include file="../../movimenti/common/include/InfoTestataMovimento.inc" %>       
          <tr>
            <td>          
              <div class='sezione2' id='SedeAzienda'>
                <img id='tendinaAzienda' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("datiAzienda"));'/>
                Sede Azienda&nbsp;&nbsp;                
                &nbsp;
                <a href="#" onClick="javascript:apriUnitaAziendale(document.Frm1.PRGAZIENDA.value,document.Frm1.PRGUNITA.value,<%=_funzione%>,'0');"><img src="../../img/detail.gif" alt="Dettaglio azienda"></a>
              </div>
            </td>
          </tr>       
          <!-- sezione riservata all'azienda che effettua il movimento e eventuale aziennda util. -->
          <%@ include file="../../movimenti/generale/include/azienda.inc" %>
          <tr>
            <td>          
              <div class='sezione2' id='lavoratore'>
                <img id='tendinaLavoratore' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("datiLavoratore"));'/>
                Lavoratore&nbsp;&nbsp;
                <%boolean modificaTitolo = false;%>
                <%@ include file="../../movimenti/generale/include/_titoloDiStudio.inc" %>
                <%@ include file="../../movimenti/generale/include/_sintesiLavoratore.inc" %>
                <%@ include file="../../movimenti/generale/include/_movimentiLavoratore.inc" %> 
              </div>
            </td>
          </tr>
          <!-- sezione riservata al lavoratore e tipo movimento -->         
          <%@ include file="../../movimenti/generale/include/_lavoratore.inc" %>
          <%@ include file="../../movimenti/generale/include/_datiGenerali.inc" %>
          <%//Pulsanti per la gestione della navigazione dei movimenti rettificati
			String prgMovimentoProtDaRett = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOVIMENTOPROTDARETTIFICA");
          	if (!prgMovimentoProtDaRett.equals("") || !prgMovimentoRett.equals("")) {%>
			<tr>
				<td>
	          	<%//Consultazione di un movimento rettificato
	          	if (!prgMovimentoRett.equals("")) {%>
					<input type="button" class="pulsante" name="consulta" value="Consulta rettificato" onClick="javascript:consultaRettificato('<%=prgMovimentoRett%>');"/>
	          	<%} 
	          	//Ritorno al movimento protocollato da uno rettificato
				if (!prgMovimentoProtDaRett.equals("") && !prgMovimentoProtDaRett.equals(prgMovimento)) {%>&nbsp;
	          		<input type="button" class="pulsante" name="consulta" value="Torna al protocollato" onClick="javascript:consultaProtocollatoDaRettifica('<%=prgMovimentoProtDaRett%>');"/>
	          		<input type="hidden" name="PRGMOVIMENTOPROTDARETTIFICA" value="<%=prgMovimentoProtDaRett%>"/>       
				<%}%>
				</td>
			</tr> 
          <%}%>       
          <tr>
          	<td>
              <input type="button" class="pulsante" name="Stampa" value="Stampa" onClick="stampaMovimento(<%=prgMovimento%>)"/>
              <%if(canModifyMov ){%>
              	<input type="button" class="pulsante" name="Modifica" value="Modifica" onClick="modificaMovimento()"/>
              <%}%>
          	</td>
          </tr>

         <%if(hasDocuments){%>
          <TR>
          	<TD align="right">          	
          		<INPUT type="button", class="pulsante" name="docuAssociati" value="Documenti associati" onclick="docAssociati(<%=cdnLavoratore%>,'MovDettaglioGeneraleConsultaPage',<%=_funzione%>,'',<%=prgMovimento%>)"/>&nbsp;&nbsp;&nbsp;&nbsp;	
          	</TD>
          </TR>	
         <%}%>
          <%-- 13/06/2005 Davide G. Inserimento info dell'operatore --%>
          <tr><td align="center" colspan="1">
			   <%operatoreInfo.showHTML(out);%>
		      </td>
          </tr>
		</table>
        <%out.print(htmlStreamBottom);%>  
	    <input type="hidden" name="PRGMOVIMENTO" value="<%=prgMovimento%>"/>
        <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
        <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
        <input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=prgAziendaUtil%>"/>
        <input type="hidden" name="PRGUNITAUTILIZ" value="<%=prgUnitaUtil%>"/>
        <input type="hidden" name="NUMKLOMOV" value="<%=numKloMov%>"/>
        <input type="hidden" name="ACTION" value=""/>
        <input type="hidden" name="PAGE" value="MovDettaglioGeneraleConsultaPage"/>
        <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
        <input type="hidden" name="NUOVODOCUMENTO" value=""/>  
        <input type="hidden" name="STRAZINTNUMCONTRATTO" value="<%=numContratto%>">
        <input type="hidden" name="DATAZINTINIZIOCONTRATTO" value="<%=dataInizio%>">
        <input type="hidden" name="DATAZINTFINECONTRATTO" value="<%=dataFine%>">
        <input type="hidden" name="STRAZINTRAP" value="<%=legaleRapp%>">
        <input type="hidden" name="NUMAZINTSOGGETTI" value="<%=numSoggetti%>">
        <input type="hidden" name="NUMAZINTDIPENDENTI" value="<%=classeDip%>">
        <input type="hidden" name="STRENTERILASCIO" value="<%=strEnteRilascio%>" />
        <input type="hidden" name="strRagioneSocialeAzUtil" value="<%=strRagioneSocialeAzUtil%>"/>
        <input type="hidden" name="strIndirizzoUAzUtil" value="<%=strIndirizzoUAzUtil%>"/>
        <input type="hidden" name="strComuneUAzUtil" value="<%=strComuneUAzUtil%>"/>
        <input type="hidden" name="FORZA_INSERIMENTO" value="false"/>
        <input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false"/>
        <input type="hidden" name="CODMONOTEMPO" value="<%=codMonoTempo%>"/>   	
        <!-- Data di inizio mov -->
        <input type="hidden" name="DATAINIZIO" value="<%=datInizioMov%>" />
        <input type="hidden" name="TIPOPROTOCOLLO" value="" />
  		<input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>"/>
  		
  		<input type="hidden" name="LAVORATORECOLLMIRATO" value="<%=strLavCollMirato%>"/>        
        
        <!-- salvataggio dati -->
        <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
        
         <%  if(serviceRequest.containsAttribute("forzaVal")) {%>
           	<input type="hidden" name="forzaVal" value="1"/>
           	<input type="hidden" name="strNotaModifica" value="<%=strNotaModifica%>"/>
        <%}%>
         
		<!-- Sezione di gestione impatti tenendo conto della profilatura -->
        <input type="hidden" name="permettiImpatti" value="" />
        <input type="hidden" name="CANVIEW" value="" />
        <input type="hidden" name="CANEDITLAV" value="" />
        <input type="hidden" name="CANEDITAZ" value="" />
        
        <!-- Sezione documento associato al movimenti -->
        <input type="hidden" name="PRGDOCUMENTO" value="<%=prgDoc%>" />
        <input type="hidden" name="CODCPI" value="<%=codCpi%>" />
  <!-- DAVIDE 29/05/2007: nuovi campi aggiunti con il tracciato UNICO -->
  <input type="hidden" name="CODENTE"     value="<%=codtipoenteprev%>" />
  <input type="hidden" name="STRCODICEENTEPREV"   value="<%=strcodiceenteprev%>" />
  <input type="hidden" name="CODTIPOTRASF"        value="<%=codtipotrasf%>" />
  <input type="hidden" name="DATFINEDISTACCO"    value="<%=dataFineDistacco%>" />
  <input type="hidden" name="CODTIPOCONTRATTO"    value="<%=codtipocontratto%>" />
  <input type="hidden" name="STRNUMAGSOMMINISTRAZIONE"        value="<%=strnumagsomm%>" />
  <input type="hidden" name="DECINDENSOM"     value="<%=decindensom%>" />
  <input type="hidden" name="DATINIZIORAPLAV"   value="<%=datiniziomissione%>" />
  <input type="hidden" name="DATFINERAPLAV"     value="<%=datfinemissione%>" />
  <input type="hidden" name="FLGRISCHIOSIAS"    value="<%=strrischioasbsil%>" />
  <input type="hidden" name="DECVOCETAR1"     value="<%=decvocetar1%>" />
  <input type="hidden" name="DECVOCETAR2"     value="<%=decvocetar2%>" />
  <input type="hidden" name="DECVOCETAR3"     value="<%=decvocetar3%>" />
  <input type="hidden" name="CODSOGGETTO"         value="<%=codsoggetto%>" />
  <input type="hidden" name="FLGSOCIO"            value="<%=flgsocio%>" />
       	<center>
		<%@ include file="../common/include/GestioneCollegati.inc" %>
		</center> 	
       
        
    	<%  if(serviceRequest.containsAttribute("forzaVal")) {%>
    		<center>
			<%out.print(htmlStreamTop2);%>
				<%@ include file="../generale/include/_NotaModifiche.inc" %>
			<%out.print(htmlStreamBottom2);%>
		</center>
		
		<%}%> 
		
		<center>
			<%@ include file="../common/include/PulsanteRitornoLista.inc" %>
		</center> 
		
		  </af:form>
	
	
	<script language="javascript">
    
    if (aziendaUtilVar != "N"){
      document.Frm1.STRLUOGODILAVORO.readOnly = true;
    }
    
    </script>
  <%@ include file="../common/include/GestioneScriptRisultati.inc" %>
 </body>
</html>