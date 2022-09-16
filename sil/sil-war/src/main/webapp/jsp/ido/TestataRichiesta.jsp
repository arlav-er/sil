<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
  it.eng.sil.module.movimenti.constant.Properties,
  it.eng.sil.util.*,
  it.eng.afExt.utils.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.User,
  java.text.*, 
  java.util.*,
  java.math.*,
  java.util.GregorianCalendar,
  java.text.SimpleDateFormat,
  it.eng.sil.security.*

"%>
<%@ include file="../global/noCaching.inc"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%     
  final String FORMATO_DATA     = "dd/MM/yyyy";
  SimpleDateFormat df = new SimpleDateFormat(FORMATO_DATA);
  
  boolean flag_insert   = serviceRequest.containsAttribute("inserisci");
  boolean reiteraRichAz = serviceRequest.containsAttribute("reiteraRichAz");
  //////////// GESTIONE PAGE DELLA LISTA CHIAMANTE (AL PRIMO CARICAMENTO) A CUI TORNARE
  // si riprende la pagina di lista da cui e' stata caricata la pagina
  String pagina_back=StringUtils.getAttributeStrNotNull(serviceRequest,"ret");
  // se e' null vuol dire che si e' ricaricata la pagina navigando tra le linguette: la si riprende dalla sessione
  // altrimenti la si inserisce nel sessionContainer
  // Inoltre bisogna resettare il "prgAlternativa" in sessione se si entra in questa pagina dalla lista richieste
  //  o dalla gestione incrocio, avendo selezionato una richiesta della lista, oppure se si e' in fase di inserimento
  // provenienti da una lista richieste, lista azienda,  dettaglio unità azienda.
  if (pagina_back.equals("")) {
  	pagina_back = (String)sessionContainer.getAttribute("PAGE_RIC_BACK_TO_LIST");
  	if (flag_insert) {
  		sessionContainer.delAttribute("prgAlternativa");
    	sessionContainer.setAttribute("prgAlternativa", "1");
  	}  		
  }
  else {
    if (pagina_back.equalsIgnoreCase("IdoListaRichiestePage") || pagina_back.equalsIgnoreCase("GestIncrocioPage")  ) {
    	sessionContainer.delAttribute("prgAlternativa");
    	sessionContainer.setAttribute("prgAlternativa", "1");
    }
  	sessionContainer.setAttribute("PAGE_RIC_BACK_TO_LIST", pagina_back);  
  }
  ///////////////////////////////////////////////////////////////////
SourceBean richiestaRow=null;
int numGPubb = 0;
int numGRich = 0;
int configOnline = 0;
//Sezione data pubblicazione e richiesta
GregorianCalendar dataRich = new GregorianCalendar();
GregorianCalendar dataPubb = new GregorianCalendar();

String dtPubb = DateUtils.getNow();
String dtPubbScad="";
String dtRichScad="";

SourceBean numGg = (SourceBean)serviceResponse.getAttribute("M_GetNumGgPubb.ROWS.ROW");
if (numGg != null) {
  numGPubb = Integer.parseInt(numGg.getAttribute("NUM").toString());
}

dataPubb.add(Calendar.DATE,numGPubb);
dtPubbScad = df.format(dataPubb.getTime());

//Num GG DATA Rich
SourceBean numGgRich = (SourceBean)serviceResponse.getAttribute("M_GetNumGgRich.ROWS.ROW");
if (numGgRich != null) {
  numGRich = Integer.parseInt(numGgRich.getAttribute("NUM").toString());
}

dataRich.add(Calendar.DATE,numGRich);
dtRichScad = df.format(dataRich.getTime());

boolean canASOnline = false;
/*Tale dato resta modificabile fintanto che:
• La richiesta di personale non è stata pubblicata, oppure
• È già stato effettuato lo scarico della graduatoria
*/
SourceBean configAsOnline = (SourceBean)serviceResponse.getAttribute("M_Config_AsOnline.ROWS.ROW");
if (configAsOnline != null) {
	configOnline = Integer.parseInt(configAsOnline.getAttribute("NUMVALORECONFIG").toString());
	if (configOnline ==1){
		canASOnline = true;
	}
}


//System.gc();
//--------------------------
String prgRichiestaAz="";
String numAnnoRichiesta="";
String numRichiestaAnno="";
String numRichiestaAnnoVis="";
String prgAzienda="";
String prgUnita="";
String codCpi="";
String datRichiesta="";
String flgArt16="";
boolean flgPubblicata = false;
boolean flgStatoEvasioneAS = false;
//modifica Esposito
boolean flgStatoRich = true;

String datScadenza="";

String numProfRichiesti="";
String strLocalita="";
String prgSpi="";
String strCognomeRiferimento="";
String strNomeRiferimento="";
String strTelRiferimento="";
String strFaxRiferimento="";
String strEmailRiferimento="";
String flgAutomunito="";
String flgMotomunito="";
String flgMilite="";
String codTrasferta="";
String flgFuoriSede="";
String txtNoteOperatore="";
String cdnUtIns="";
String dtmIns="";
String cdnUtMod="";
String dtmMod="";
String anno="";
String flgVittoAlloggio="";
String flgTurismo = "";
String flgVitto = "";
String strSesso="";
String displayMotGenere = "none";
String codMotGenere="";
String strMotivSesso="";
String codArea="";
String flgIncrocio="";
String flgGraduatoria = "";
String codTipoLSU = "";
String displayTipoLSU = "none";
String cdnStatoRich = "";
String flgSvantaggiati="";
String strMotSvantaggiati="";
String datVerificaSvan="";

String flgDisNonIscr="";
String strMotNonIscr="";
String datVerificaDis="";

int cdnTipoGruppo = user.getCdnTipoGruppo();
if (cdnTipoGruppo==1) {
  codCpi = user.getCodRif();
}

String datChiamata="";
String numPostoMB="";
String numPostoAS="";
String numPostoMilitare="";
String flgRiusoGraduatoria="";
String numPostoLSU="";
String checkNumAs="";
String checkNumLsu="";
String checkNumMilitare="";

String displayTipoNumerico = "none";
String displayTipoArt1 = "none";
String datChiamataCM = "";
String numPostiCM = "";
//modifica Esposito
String numAnnoRedditoCM = "";
String codmonoCMcategoria = "";

String checkNumPostiCM = "";
String codMonoTipoGrad = "";
String codTipoLista = "";
String flAsOnline=null;
String dtAsOnline=null;
String codEvasione=null;
boolean flgStatoEvasioneCMA = false;
boolean flgStatoEvasioneCMG = false;
boolean flgStatoEvasioneCatCM = false;
   
boolean flagNuovo  = true;
boolean canModifyAS = true;
boolean canModifyCM = true;
boolean canModifyCatCM = true;
boolean canDelete  = false;
boolean canModify = false;  
boolean eUnaCopia  = false;
boolean canReitera = false; 
boolean canIncrocio = false;
boolean canGraduatoria = false;
boolean canInsert = false;
boolean canManage = false;
boolean gestioneRosaNominativa = false;
boolean checkVdA = false;
// Modifica S.O. il 19/04/2004 -- messo a null genera una exception if(numStorico.intVal.....
//BigDecimal numStorico = null;
BigDecimal numStorico = new BigDecimal("0");

BigDecimal numkloRichiestaAz=null;
SourceBean richiestaRowReiterata = null;
SourceBean richiestaRowInserita = null;
String flgPubbCresco="";

Testata operatoreInfo=null;
InfCorrentiAzienda infCorrentiAzienda= null;


boolean CRESCO = false;
boolean manageDataCresco = false;
String numConfigCresco = serviceResponse.containsAttribute("M_CONFIG_CRESCO.ROWS.ROW.NUM")?
	serviceResponse.getAttribute("M_CONFIG_CRESCO.ROWS.ROW.NUM").toString():Properties.DEFAULT_CONFIG;
if(Properties.CUSTOM_CONFIG.equalsIgnoreCase(numConfigCresco)){
	CRESCO = true;
}  


String configSV = serviceResponse.containsAttribute("M_GetConfigSV.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigSV.ROWS.ROW.NUM").toString():"0";

if (!flag_insert) {
   // ************************************
   // AGGIORNAMENTO
   // ************************************
   	
	
  richiestaRowReiterata = (SourceBean) serviceResponse.getAttribute("M_REITERARICHAZ.ROW");
  if( richiestaRowReiterata != null ) { 
    prgRichiestaAz = richiestaRowReiterata.getAttribute("prgRichiestaAZ").toString();
  }

  if( prgRichiestaAz == null || prgRichiestaAz.equals("") ) {
    richiestaRowInserita = (SourceBean) serviceResponse.getAttribute("M_InsertRichiesta.ROWS.ROW");
    if( richiestaRowInserita != null ) { 
      prgRichiestaAz = richiestaRowInserita.getAttribute("prgRichiestaAZ").toString();
    }
  }

  if( prgRichiestaAz == null || prgRichiestaAz.equals("") ) {
    prgRichiestaAz= (String)serviceRequest.getAttribute("prgRichiestaAZ");
  }
  
  richiestaRow= (SourceBean) serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW");
  
  if( richiestaRow != null ) {  
    flagNuovo = false;
    if( prgRichiestaAz == null || prgRichiestaAz.equals("") ) {
      prgRichiestaAz = richiestaRow.getAttribute("prgRichiestaAZ").toString();
    }
    if(!reiteraRichAz){



      datScadenza              = StringUtils.getAttributeStrNotNull(richiestaRow, "datScadenza");
      datRichiesta     = StringUtils.getAttributeStrNotNull(richiestaRow, "datRichiesta");
      numAnnoRichiesta = richiestaRow.containsAttribute("NUMANNO") ? richiestaRow.getAttribute("NUMANNO").toString() : "";
      numRichiestaAnno = richiestaRow.containsAttribute("NUMRICHIESTA") ? richiestaRow.getAttribute("NUMRICHIESTA").toString() : "";
      numRichiestaAnnoVis = richiestaRow.containsAttribute("NUMRICHIESTAVIS") ? richiestaRow.getAttribute("NUMRICHIESTAVIS").toString() : "";
      anno             = datRichiesta.substring(6);
      codCpi             = StringUtils.getAttributeStrNotNull(richiestaRow, "codCpi");
    }
    

    numStorico         = (BigDecimal) richiestaRow.getAttribute("NUMSTORICO");
    eUnaCopia          = (numStorico.intValue()!=0) ? true : false;
    prgAzienda         = richiestaRow.getAttribute("PRGAZIENDA").toString();
    prgUnita           = richiestaRow.getAttribute("PRGUNITA").toString();
    flgArt16           = StringUtils.getAttributeStrNotNull(richiestaRow, "flgArt16");
    numProfRichiesti         = richiestaRow.getAttribute("numProfRichiesti").toString();
    strLocalita              = StringUtils.getAttributeStrNotNull(richiestaRow, "strLocalita");
    prgSpi                   = richiestaRow.containsAttribute("prgSpi") ? richiestaRow.getAttribute("prgSpi").toString() : "";
    strCognomeRiferimento    = StringUtils.getAttributeStrNotNull(richiestaRow, "strCognomeRiferimento");
    strNomeRiferimento       = StringUtils.getAttributeStrNotNull(richiestaRow, "strNomeRiferimento");
    strTelRiferimento        = StringUtils.getAttributeStrNotNull(richiestaRow, "strTelRiferimento");
    strFaxRiferimento        = StringUtils.getAttributeStrNotNull(richiestaRow, "strFaxRiferimento");
    strEmailRiferimento      = StringUtils.getAttributeStrNotNull(richiestaRow, "strEmailRiferimento");
    flgAutomunito            = StringUtils.getAttributeStrNotNull(richiestaRow, "flgAutomunito");
    flgMilite                = StringUtils.getAttributeStrNotNull(richiestaRow, "flgMilite");  
    codTrasferta             = StringUtils.getAttributeStrNotNull(richiestaRow, "codTrasferta");
    flgFuoriSede             = StringUtils.getAttributeStrNotNull(richiestaRow, "flgFuoriSede");  
    txtNoteOperatore         = StringUtils.getAttributeStrNotNull(richiestaRow, "txtNoteOperatore");  
    flgMotomunito            = StringUtils.getAttributeStrNotNull(richiestaRow, "flgMotomunito");  
    flgVittoAlloggio         = StringUtils.getAttributeStrNotNull(richiestaRow, "flgVittoAlloggio"); 
    flgVitto         		 = StringUtils.getAttributeStrNotNull(richiestaRow, "flgVitto"); 
    flgTurismo		         = StringUtils.getAttributeStrNotNull(richiestaRow, "flgTurismo"); 
    strSesso                 = StringUtils.getAttributeStrNotNull(richiestaRow, "strSesso"); 
    if (!strSesso.equals("")) {
    	displayMotGenere = "";    
    }
   	codMotGenere			 = StringUtils.getAttributeStrNotNull(richiestaRow, "codMotGenere");    
    strMotivSesso            = StringUtils.getAttributeStrNotNull(richiestaRow, "strMotivSesso"); 
   	codArea                  = StringUtils.getAttributeStrNotNull(richiestaRow, "codArea"); 
    cdnUtIns                 = richiestaRow.getAttribute("cdnUtIns").toString();  
    dtmIns                   = StringUtils.getAttributeStrNotNull(richiestaRow, "dtmIns");  
    cdnUtMod                 = richiestaRow.getAttribute("cdnUtMod").toString();  
    dtmMod                   = StringUtils.getAttributeStrNotNull(richiestaRow, "dtmMod");
    flgIncrocio              = StringUtils.getAttributeStrNotNull(richiestaRow,"flgIncrocio");
    flgGraduatoria           = (String)richiestaRow.getAttribute("FLGGRADUATORIA");
    flgPubblicata            = StringUtils.getAttributeStrNotNull(richiestaRow,"FLGPUBBLICATA").equalsIgnoreCase("S");          
	numkloRichiestaAz        = richiestaRow.containsAttribute("numkloRichiestaAz") ? (BigDecimal) richiestaRow.getAttribute("numkloRichiestaAz") : null;
    
	if (canASOnline = true) {
		flAsOnline       =StringUtils.getAttributeStrNotNull(richiestaRow, "FLGASONLINE"); 
		
		dtAsOnline      = StringUtils.getAttributeStrNotNull(richiestaRow, "DTMASONLINE");
		
		if(reiteraRichAz){
			flAsOnline = null;
			dtAsOnline = null;
		}
	}
	codEvasione = StringUtils.getAttributeStrNotNull(richiestaRow,"CODEVASIONE");
	
    if (richiestaRow.containsAttribute("cdnStatoRich"))
        cdnStatoRich             =  richiestaRow.getAttribute("cdnStatoRich").toString();
    
     
    if(numkloRichiestaAz != null)
      numkloRichiestaAz = numkloRichiestaAz.add(new BigDecimal(1));
                    
		flgStatoEvasioneAS = (StringUtils.getAttributeStrNotNull(richiestaRow,"CODEVASIONE")).equalsIgnoreCase("AS");          
	    datChiamata = StringUtils.getAttributeStrNotNull(richiestaRow, "DATCHIAMATA");  	    
	    numPostoAS = richiestaRow.getAttribute("NUMPOSTOAS") != null ? ((BigDecimal)richiestaRow.getAttribute("NUMPOSTOAS")).toString() : "";
	    numPostoMB =  richiestaRow.getAttribute("NUMPOSTOMB") != null ? ((BigDecimal)richiestaRow.getAttribute("NUMPOSTOMB")).toString() : "";
	    numPostoMilitare =  richiestaRow.getAttribute("NUMPOSTOMILITARE") != null ? ((BigDecimal)richiestaRow.getAttribute("NUMPOSTOMILITARE")).toString() : "";
	    numPostoLSU =  richiestaRow.getAttribute("NUMPOSTOLSU") != null ? ((BigDecimal)richiestaRow.getAttribute("NUMPOSTOLSU")).toString() : "";
	    flgRiusoGraduatoria = StringUtils.getAttributeStrNotNull(richiestaRow, "FLGRIUSOGRADUATORIA");          	
	    checkNumAs = StringUtils.getAttributeStrNotNull(richiestaRow, "CHECK_NUM_AS");          	
	    checkNumLsu = StringUtils.getAttributeStrNotNull(richiestaRow, "CHECK_NUM_LSU");          	
	    checkNumMilitare = StringUtils.getAttributeStrNotNull(richiestaRow, "CHECK_NUM_MILITARE");          	
	    codTipoLSU = StringUtils.getAttributeStrNotNull(richiestaRow, "CODTIPOLSU");  	    
	    if (!numPostoLSU.equals("") && !numPostoLSU.equals("0")) {
    		displayTipoLSU = "";
    	}	
	
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) {	
	// END-PARTE-TEMP
	
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP
		
		flgStatoEvasioneCMA = StringUtils.getAttributeStrNotNull(richiestaRow,"CODEVASIONE").equalsIgnoreCase("CMA");          
		
		flgStatoEvasioneCatCM = StringUtils.getAttributeStrNotNull(richiestaRow,"CODEVASIONE").equalsIgnoreCase("MIR") ||
								StringUtils.getAttributeStrNotNull(richiestaRow,"CODEVASIONE").equalsIgnoreCase("MPP") ||
								StringUtils.getAttributeStrNotNull(richiestaRow,"CODEVASIONE").equalsIgnoreCase("MPA");
		
		flgStatoEvasioneCMG = StringUtils.getAttributeStrNotNull(richiestaRow,"CODEVASIONE").equalsIgnoreCase("CMG");
		datChiamataCM = StringUtils.getAttributeStrNotNull(richiestaRow, "DATCHIAMATACM");  	    
		numPostiCM = richiestaRow.getAttribute("NUMPOSTICM") != null ? ((BigDecimal)richiestaRow.getAttribute("NUMPOSTICM")).toString() : ""; 
		//modifica Esposito
		numAnnoRedditoCM = richiestaRow.getAttribute("NUMANNOREDDITOCM") != null ? ((BigDecimal)richiestaRow.getAttribute("NUMANNOREDDITOCM")).toString() : "";      	
		codmonoCMcategoria = StringUtils.getAttributeStrNotNull(richiestaRow, "CODMONOCMCATEGORIA");
		
		checkNumPostiCM = StringUtils.getAttributeStrNotNull(richiestaRow, "CHECK_NUM_POSTI_CM");          	
		codMonoTipoGrad = StringUtils.getAttributeStrNotNull(richiestaRow, "CODMONOTIPOGRAD");  	    	    
		codTipoLista = StringUtils.getAttributeStrNotNull(richiestaRow, "CODTIPOLISTA");	
		if (("D").equalsIgnoreCase(codMonoTipoGrad) || ("A").equalsIgnoreCase(codMonoTipoGrad)) {
			displayTipoNumerico = "";
		}
		else if (("G").equalsIgnoreCase(codMonoTipoGrad)) {    
			displayTipoArt1 = "";
		}
	    
  	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
	
	if (configSV.equals("1")) {
		flgSvantaggiati=StringUtils.getAttributeStrNotNull(richiestaRow, "FLGSVANTAGGIATI"); 
		if ("S".equalsIgnoreCase(flgSvantaggiati)) {
			strMotSvantaggiati=StringUtils.getAttributeStrNotNull(richiestaRow, "STRMOTSVANTAGGIATI"); 
			datVerificaSvan=StringUtils.getAttributeStrNotNull(richiestaRow, "DATVERIFICASVAN"); 
		}
		flgDisNonIscr=StringUtils.getAttributeStrNotNull(richiestaRow, "FLGDISNONISCR"); 
		if ("S".equalsIgnoreCase(flgDisNonIscr)) {
			strMotNonIscr=StringUtils.getAttributeStrNotNull(richiestaRow, "STRMOTNONISCR"); 
			datVerificaDis=StringUtils.getAttributeStrNotNull(richiestaRow, "DATVERIFICADIS"); 
		}
  	}
	
	//flgPubbCresco = StringUtils.getAttributeStrNotNull(richiestaRow, "flgPubbCresco");
   	if (CRESCO && it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(richiestaRow, "flgPubbCresco").equalsIgnoreCase("S")) {
   		manageDataCresco = true;
	}
	
  }

  infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda,prgUnita,prgRichiestaAz);
  operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
}
else {
   // ************************************
   // INSERIMENTO
   // ************************************

  if (serviceResponse.containsAttribute("M_GETPRGSPIUT.ROWS.ROW.PRGSPI")){
    prgSpi     = (String)serviceResponse.getAttribute("M_GETPRGSPIUT.ROWS.ROW.PRGSPI").toString();
  }
  else {
    prgSpi = "";
  }
  if (serviceResponse.containsAttribute("M_GETTESTATARICHIESTA.ROWS.ROW.PRGRICHIESTAAZ"))
	  prgRichiestaAz = serviceResponse.getAttribute("M_GETTESTATARICHIESTA.ROWS.ROW.PRGRICHIESTAAZ").toString();
  prgAzienda = (String)serviceRequest.getAttribute("prgAzienda");
  prgUnita   = (String)serviceRequest.getAttribute("prgUnita");

  if( prgAzienda != null && prgUnita!=null && !prgAzienda.equals("") && !prgUnita.equals("") ) {
   // sessionContainer = RequestContainer.getRequestContainer().getSessionContainer();
  	infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda,prgUnita, prgRichiestaAz); 
  	
  }
}
  if (infCorrentiAzienda!=null) {
	  if (pagina_back!=null &&(pagina_back.equalsIgnoreCase("IdoListaRichiestePage") 
	  						|| pagina_back.equalsIgnoreCase("IdoListaPubbPage")))
	    infCorrentiAzienda.setPaginaLista(pagina_back);  
	  else infCorrentiAzienda.setSkipLista(true);
  }
  //Setto data richiesta e scadenza se vuote
  if ( (datRichiesta == null) || ( datRichiesta.compareTo("")==0) ){
    datRichiesta=dtPubb;
  } 
  if ( (datScadenza==null) || (datScadenza.compareTo("")==0) ) {
    datScadenza=dtRichScad;
  } 
  ///////////////////////////////////////////////////////////////////////////////// 
  // PROFILATURA E VISIBILITA'
  ProfileDataFilter filter = new ProfileDataFilter(user, "IdoTestataAziendaPage");
  boolean canView = false;
  
  if ( !flag_insert ) {
    filter.setPrgAzienda(new BigDecimal(prgAzienda));
    filter.setPrgUnita(new BigDecimal(prgUnita));
    canView=filter.canViewUnitaAzienda();
  } else {
    canView = true; // sono in inserimento, la visibilità dei dati non ha senso
  }
  
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

  PageAttribs attributi = new PageAttribs(user, "IdoTestataRichiestaPage");
  checkVdA = attributi.containsButton("CHECKVDA");
  canGraduatoria = attributi.containsButton("GRADUATORIA");
  if ((reiteraRichAz) || (cdnStatoRich.compareTo("4")!=0 && cdnStatoRich.compareTo("5")!=0)){
	canReitera = attributi.containsButton("REITERA");
    canDelete= attributi.containsButton("CANCELLA");
    canModify= attributi.containsButton("AGGIORNA");
    canModifyAS = attributi.containsButton("AGGIORNA");
    canModifyCM = attributi.containsButton("AGGIORNA");
    canModifyCatCM = attributi.containsButton("AGGIORNA");
    canIncrocio = attributi.containsButton("INCROCIO");    
    canInsert=attributi.containsButton("INSERISCI");
  }
  else if (cdnStatoRich.compareTo("4")==0 && attributi.containsButton("INSERISCI")) {
	gestioneRosaNominativa = true;
  }
  
  //modifica Esposito
  if( cdnStatoRich.compareTo("1")!=0 && cdnStatoRich.compareTo("")!=0 ){
  	flgStatoRich = false;
  }
  
  if ( !canDelete && !canModify && !canReitera && !canIncrocio && ! canInsert) {
      //
    } else {
      boolean canEdit = false;
      if ( !flag_insert ) {
        canEdit = filter.canEditUnitaAzienda();
      } else { 
        canEdit = true;   // sono in inserimento, la visibilità dei dati non ha senso
      }
      if (flgPubblicata) {           
	      canModifyAS = false;        
	      canModifyCM = false; 
	      canModifyCatCM = false;
      }
      if ( !canEdit ) {
      	canReitera = false;
        canDelete = false;
        canModify = false;     
        canModifyAS = false;   
        canModifyCM = false;
        canModifyCatCM = false;
        canInsert= false;
        canIncrocio= false;
      }
    }
  
  // andrea 28/02/05
  if (flag_insert){ // la pagina e' in inserimento
  	canManage = canInsert;  
  	canModifyCatCM = false;
  }else  
  	if (reiteraRichAz){  // la pagina e' in reitera richiesta
  		canManage = canReitera;
  		canModifyAS = canReitera;
  		canModifyCM = canReitera;
  		canModifyCatCM = false;
  		codmonoCMcategoria = ""; //in reiterazione il campo non viene ricopiato
  	}
  	else{ // la pagina e' in modifica
	  	canManage = canModify;
	}

  if( !flag_insert && !flgStatoEvasioneCatCM ){
  	canModifyCatCM = false;
  }
	
///////////////////////////////////////////////  
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String _page = (String) serviceRequest.getAttribute("PAGE");
  String htmlStreamTop = StyleUtils.roundTopTable(canManage);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canManage);

  // gestione visualizzazione sezione dati generali
  boolean legatoADatiGen = false;
  if (flag_insert) {
	  legatoADatiGen = true;
  }
  else if (!StringUtils.isEmptyNoBlank(codArea) ||
	!StringUtils.isEmptyNoBlank(flgFuoriSede) ||
	!StringUtils.isEmptyNoBlank(strLocalita) ||
	!StringUtils.isEmptyNoBlank(flgAutomunito) ||
	!StringUtils.isEmptyNoBlank(codTrasferta) ||
	!StringUtils.isEmptyNoBlank(flgMotomunito) ||
	!StringUtils.isEmptyNoBlank(flgVittoAlloggio) ||
	!StringUtils.isEmptyNoBlank(flgVitto) ||
	!StringUtils.isEmptyNoBlank(flgTurismo) ||
	!StringUtils.isEmptyNoBlank(flgMilite) ||
	!StringUtils.isEmptyNoBlank(strSesso)) {
		legatoADatiGen = true;
  }
  String imgSelSezioneDatiGen = legatoADatiGen?"../../img/aperto.gif":"../../img/chiuso.gif";
  String displaySDatiGen = legatoADatiGen? "":"none";
  
  //gestione visualizzazione sezione svantaggiati
  boolean legatoAgliSvantaggiati = false;
  if (flag_insert) {
	  legatoAgliSvantaggiati = true;
  }
  else if (!StringUtils.isEmptyNoBlank(strMotSvantaggiati) || !StringUtils.isEmptyNoBlank(datVerificaSvan) ) {
 		legatoAgliSvantaggiati = true;
  }
  
  String imgSelSezioneSvantaggiati = legatoAgliSvantaggiati?"../../img/aperto.gif":"../../img/chiuso.gif";
  String displaySvantaggiati = legatoAgliSvantaggiati? "":"none";
  
  
  //gestione visualizzazione sezione disabili non iscritti
  boolean legatoAiDisabiliNonIscritti = false;
  if (flag_insert) {
	  legatoAiDisabiliNonIscritti = true;
  }  else if (!StringUtils.isEmptyNoBlank(strMotNonIscr) || !StringUtils.isEmptyNoBlank(datVerificaDis) ) {
  		legatoAiDisabiliNonIscritti = true;	
  }
  
  String imgSelSezioneDisabili = legatoAiDisabiliNonIscritti?"../../img/aperto.gif":"../../img/chiuso.gif";
  String displayDisabili = legatoAiDisabiliNonIscritti? "":"none";
  
   // gestione visualizzazione sezione riferimenti
  boolean legatoAlRiferimento = false;
  if (flag_insert) {
	  legatoAlRiferimento = true;
  }
  else if (!StringUtils.isEmptyNoBlank(strCognomeRiferimento) ||
	!StringUtils.isEmptyNoBlank(strNomeRiferimento) ||
	!StringUtils.isEmptyNoBlank(strTelRiferimento) ||
	!StringUtils.isEmptyNoBlank(strFaxRiferimento) ||
	!StringUtils.isEmptyNoBlank(strEmailRiferimento)) {
		legatoAlRiferimento = true;
  }
  
  String imgSelSezioneRiferimento = legatoAlRiferimento?"../../img/aperto.gif":"../../img/chiuso.gif";
  String displaySRiferimento = legatoAlRiferimento? "":"none";

  // gestione visualizzazione sezione Categoria CM
  boolean legatoACategoriaCM = false;
  if (flag_insert||reiteraRichAz) {
	  legatoACategoriaCM = false;
  }
  else if ( flgStatoEvasioneCatCM ) {
		legatoACategoriaCM = true;
  }

  String imgSelSezioneCategoriaCM = legatoACategoriaCM?"../../img/aperto.gif":"../../img/chiuso.gif";
  String displaySCategoriaCM = legatoACategoriaCM? "":"none";
 
%>
<html>

<head>
  <title>Richiesta</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>

  
<SCRIPT TYPE="text/javascript">
<!--
	function checkDisable() {		
	    <%
	    if (canManage) {
	    %>
			disableNumFigureProf();	
			<%
			// INIT-PARTE-TEMP
			if (Sottosistema.CM.isOff()) {	
			// END-PARTE-TEMP
			
			// INIT-PARTE-TEMP
			} else {
			// END-PARTE-TEMP
			%>
				disableNumFigureProfCM();
			<%
			// INIT-PARTE-TEMP
			}
			// END-PARTE-TEMP
			%>
		<%			
		}
		%>	  
  	}

	function settaNumeroFigureProf(){
    	var numFigureProf = 0;
    	
    	var numPostoAS = parseInt(document.Frm1.numPostoAS.value);
    	var numPostoMB = parseInt(document.Frm1.numPostoMB.value);
    	var numPostoLSU = parseInt(document.Frm1.numPostoLSU.value);
    	var numPostoMilitare = parseInt(document.Frm1.numPostoMilitare.value);
    	
    	if (!isNaN(numPostoAS)) {		    	
	    	numFigureProf += numPostoAS;
    	}
    	if (!isNaN(numPostoMB)) {		    	
	    	numFigureProf += numPostoMB;
    	}
    	if (!isNaN(numPostoLSU)) {		    	
	    	numFigureProf += numPostoLSU;
    	}
    	if (!isNaN(numPostoMilitare)) {		    	
	    	numFigureProf += numPostoMilitare;
    	}	    	
		
		disableNumFigureProf();					
		
		document.Frm1.numProfRichiesti.value = numFigureProf;
		      
	}
	
	function settaNumeroFigureProfCM(){
    	var numFigureProf = 0;
    	
    	var numPostiCM = parseInt(document.Frm1.numPostiCM.value);
    	
    	if (!isNaN(numPostiCM)) {		    	
	    	numFigureProf += numPostiCM;
    	}    		    	
		
		disableNumFigureProfCM();					
		
		document.Frm1.numProfRichiesti.value = numFigureProf;
		      
	}
	
	function disableNumFigureProf(){
		
		var numPostoAS = parseInt(document.Frm1.numPostoAS.value);
    	var numPostoMB = parseInt(document.Frm1.numPostoMB.value);
    	var numPostoLSU = parseInt(document.Frm1.numPostoLSU.value);
    	var numPostoMilitare = parseInt(document.Frm1.numPostoMilitare.value);    	    	
    			
		if (isNaN(numPostoAS) && isNaN(numPostoMB) && isNaN(numPostoLSU) && isNaN(numPostoMilitare)) {			
			document.Frm1.numProfRichiesti.disabled = false;
			document.Frm1.numProfRichiesti.className = "input";		
			document.Frm1.numProfRichiesti.readOnly = false;	
			<%
			// INIT-PARTE-TEMP
			if (Sottosistema.CM.isOff()) {	
			// END-PARTE-TEMP
			
			// INIT-PARTE-TEMP
			} else {
			// END-PARTE-TEMP
			%>	
				enableFieldsCM();
			<%
			// INIT-PARTE-TEMP
			} 
			// END-PARTE-TEMP			
			%>		
		}			
		else {																
			document.Frm1.numProfRichiesti.className = "inputView";				
			document.Frm1.numProfRichiesti.readOnly = true;	
			
			<%
			// INIT-PARTE-TEMP
			if (Sottosistema.CM.isOff()) {	
			// END-PARTE-TEMP
			
			// INIT-PARTE-TEMP
			} else {
			// END-PARTE-TEMP
			%>	
				disableFieldsCM();
			<%
			// INIT-PARTE-TEMP
			} 
			// END-PARTE-TEMP			
			%>	
		}
	}
	
	function disableNumFigureProfCM(){
		var numPostiCM = parseInt(document.Frm1.numPostiCM.value);
		
		if(isNaN(numPostiCM)){			
			document.Frm1.numProfRichiesti.disabled = false;
			document.Frm1.numProfRichiesti.className = "input";		
			document.Frm1.numProfRichiesti.readOnly = false;
			
			enableFieldsArt16();
		}    				
		else {																
			document.Frm1.numProfRichiesti.className = "inputView";				
			document.Frm1.numProfRichiesti.readOnly = true;	
			disableFieldsArt16();
		}
	}

  function enableFields(){
    document.Frm1.datRichiesta.disabled = false;
    document.Frm1.datScadenza.disabled = false;
    document.Frm1.codCpi.disabled = false;
    document.Frm1.numProfRichiesti.disabled = false;


    document.Frm1.strLocalita.disabled = true;
    document.Frm1.flgAutomunito.disabled = false;
    document.Frm1.flgMilite.disabled = false;
    document.Frm1.flgFuoriSede.disabled = false;
    document.Frm1.codTrasferta.disabled = false;

    document.Frm1.strCognomeRiferimento.disabled = false;
    document.Frm1.strNomeRiferimento.disabled = false;
    document.Frm1.strTelRiferimento.disabled = false;
    document.Frm1.strFaxRiferimento.disabled = false;
    document.Frm1.strEmailRiferimento.disabled = false;



    document.Frm1.txtNoteOperatore.disabled = false;
    document.Frm1.prgSpi.disabled = false;

    document.Frm1.flgMotomunito.disabled = false;
    document.Frm1.flgVittoAlloggio.disabled=false;
    document.Frm1.flgVitto.disabled=false;
    document.Frm1.flgTurismo.disabled=false;
    document.Frm1.strSesso.disabled=false;
    document.Frm1.codMotGenere.disabled=false;
    document.Frm1.strMotivSesso.disabled=false;
    document.Frm1.codArea.disabled=false;

    document.Frm1.datRichiesta.className = "input";
    document.Frm1.datScadenza.className = "input";
    document.Frm1.codCpi.className = "input";
    document.Frm1.numProfRichiesti.className = "input";


    document.Frm1.strLocalita.className = "inputView";
    document.Frm1.flgAutomunito.className = "input";
    document.Frm1.flgMilite.className = "input";
    document.Frm1.flgFuoriSede.className = "input";
    document.Frm1.codTrasferta.className = "input";

    document.Frm1.strCognomeRiferimento.className = "input";
    document.Frm1.strNomeRiferimento.className = "input";
    document.Frm1.strTelRiferimento.className = "input";
    document.Frm1.strFaxRiferimento.className = "input";
    document.Frm1.strEmailRiferimento.className = "input";

    document.Frm1.txtNoteOperatore.className = "textarea";
    document.Frm1.prgSpi.className = "input";

    document.Frm1.flgMotomunito.className = "input";
    document.Frm1.flgVittoAlloggio.className="input";
    <%if (checkVdA) {%>
    document.Frm1.flgVitto.className="input";
    document.Frm1.flgTurismo.className="input";
    <%}%>
    document.Frm1.strSesso.className="input";
    document.Frm1.codMotGenere.className = "input";
    document.Frm1.strMotivSesso.className="textarea";
    document.Frm1.codArea.className="input";
    
    
    <%
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) {	
	// END-PARTE-TEMP
	
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP
	%>
		document.Frm1.datChiamataCM.disabled = false;
		document.Frm1.numPostiCM.disabled=false; 
		//modifica Esposito
		document.Frm1.numAnnoRedditoCM.disabled=false;
		//document.Frm1.codmonoCMcategoria.disabled=false;
		
		document.Frm1.codMonoTipoGrad.disabled=false;
		document.Frm1.codTipoLista.disabled=false;
		
		
		document.Frm1.datChiamataCM.className = "input";
		document.Frm1.numPostiCM.className = "input";
		
		//modifica Esposito
		document.Frm1.numAnnoRedditoCM.className = "input";	
		//document.Frm1.codmonoCMcategoria.className = "input";
		
		document.Frm1.codMonoTipoGrad.className = "input";
		document.Frm1.codTipoLista.className = "input";	
	<%
	// INIT-PARTE-TEMP
	} 
	// END-PARTE-TEMP			
	%>
		
	document.Frm1.datChiamata.disabled = false;
	document.Frm1.numPostoAS.disabled=false; 
    document.Frm1.numPostoMB.disabled=false;
    document.Frm1.numPostoLSU.disabled=false;
    document.Frm1.numPostoMilitare.disabled=false;
    document.Frm1.flgRiusoGraduatoria.disabled=false;
    document.Frm1.tipoLSU.disabled=false;

	document.Frm1.datChiamata.className = "input";
	document.Frm1.numPostoAS.className = "input";
    document.Frm1.numPostoMB.className = "input";
    document.Frm1.numPostoLSU.className = "input";
    document.Frm1.numPostoMilitare.className = "input";
	document.Frm1.flgRiusoGraduatoria.className = "input";		
	document.Frm1.flgRiusoGraduatoria.className = "input";			
		
  }

  function disableFields(){
<% if(infCorrentiAzienda == null) { %>
      document.Frm1.datRichiesta.disabled = true;
      document.Frm1.datScadenza.disabled = true;
      document.Frm1.codCpi.disabled = true;
      document.Frm1.numProfRichiesti.disabled = true;


      document.Frm1.strLocalita.disabled = true;
      document.Frm1.flgAutomunito.disabled = true;

      document.Frm1.flgMilite.disabled = true;
      document.Frm1.flgFuoriSede.disabled = true;
      document.Frm1.codTrasferta.disabled = true;

      document.Frm1.strCognomeRiferimento.disabled = true;
      document.Frm1.strNomeRiferimento.disabled = true;
      document.Frm1.strTelRiferimento.disabled = true;
      document.Frm1.strFaxRiferimento.disabled = true;
      document.Frm1.strEmailRiferimento.disabled = true;



      document.Frm1.txtNoteOperatore.disabled = true;
      document.Frm1.prgSpi.disabled = true;

      document.Frm1.flgMotomunito.disabled = true;
      document.Frm1.flgVittoAlloggio.disabled=true;
      <%if (checkVdA) {%>
      document.Frm1.flgVitto.disabled=true;
      document.Frm1.flgTurismo.disabled=true;
      <%}%>
      document.Frm1.strSesso.disabled=true;
      document.Frm1.codMotGenere.disabled=true;
      document.Frm1.strMotivSesso.disabled=true;
      document.Frm1.codArea.disabled=true;

      document.Frm1.datRichiesta.className = "inputView";
      document.Frm1.datScadenza.className = "inputView";
      document.Frm1.codCpi.className = "inputView";
      document.Frm1.numProfRichiesti.className = "inputView";


      document.Frm1.strLocalita.className = "inputView";
      document.Frm1.flgAutomunito.className = "inputView";
      document.Frm1.flgMilite.className = "inputView";
      document.Frm1.flgFuoriSede.className = "inputView";
      document.Frm1.codTrasferta.className = "inputView";

      document.Frm1.strCognomeRiferimento.className = "inputView";
      document.Frm1.strNomeRiferimento.className = "inputView";
      document.Frm1.strTelRiferimento.className = "inputView";
      document.Frm1.strFaxRiferimento.className = "inputView";
      document.Frm1.strEmailRiferimento.className = "inputView";



      document.Frm1.txtNoteOperatore.className = "textareaView";
      document.Frm1.prgSpi.className = "inputView";

      document.Frm1.flgMotomunito.className = "inputView";
      document.Frm1.flgVittoAlloggio.className="inputView";
      <%if (checkVdA) {%>
      	document.Frm1.flgVitto.className="inputView";
      	document.Frm1.flgTurismo.className="inputView";
      <%}%>
      document.Frm1.strSesso.className="inputView";
      document.Frm1.codMotGenere.className="inputView";
      document.Frm1.strMotivSesso.className="textareaView";
      document.Frm1.codArea.className="inputView";
      

		document.Frm1.datChiamata.disabled = true;
		document.Frm1.numPostoAS.disabled=true; 
	    document.Frm1.numPostoMB.disabled=true;
	    document.Frm1.numPostoLSU.disabled=true;
	    document.Frm1.numPostoMilitare.disabled=true;
	    document.Frm1.flgRiusoGraduatoria.disabled=true;
	    document.Frm1.tipoLSU.disabled=true;
	
		document.Frm1.datChiamata.className = "inputView";
		document.Frm1.numPostoAS.className = "inputView";
	    document.Frm1.numPostoMB.className = "inputView";
	    document.Frm1.numPostoLSU.className = "inputView";
	    document.Frm1.numPostoMilitare.className = "inputView";
		document.Frm1.flgRiusoGraduatoria.className = "inputView";
		document.Frm1.tipoLSU.className = "inputView";
		
				
	<%
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) {	
	// END-PARTE-TEMP
	
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP
	%>
		document.Frm1.datChiamataCM.disabled = true;
		document.Frm1.numPostiCM.disabled=true;
		
		//modifica Esposito
		document.Frm1.numAnnoRedditoCM.disabled=true; 
		//document.Frm1.codMonoCMcategoria.disabled=true;
		
		document.Frm1.codMonoTipoGrad.disabled=true;
		document.Frm1.codTipoLista.disabled=true;
		
		
		document.Frm1.datChiamataCM.className = "inputView";
		document.Frm1.numPostiCM.className = "inputView";	
		
		//modifica Esposito
		document.Frm1.numAnnoRedditoCM.className = "inputView";
		//document.Frm1.codMonoCMcategoria.className = "inputView";
		
		document.Frm1.codMonoTipoGrad.className = "inputView";
		document.Frm1.codTipoLista.className = "inputView";	
	<%
	// INIT-PARTE-TEMP
	} 
	// END-PARTE-TEMP		
	%>
      
<% } %>
  }
  
  
  function controllaNumProfili() {
    var s = document.Frm1.numProfRichiesti.title;
    if (document.Frm1.numProfRichiesti.value == 0) {
      //alert("Numero non corretto nel campo "+ s);
      alert("Il numero delle figure professionali richieste\r\ndeve essere maggiore di zero");
      var sezione = document.getElementById("T_S_DATI_GEN");
      if(sezione.style.display=="none") {
      	cambia("I_SEL_S_DATI_GEN", document.getElementById("T_S_DATI_GEN"));
      }
	  document.Frm1.numProfRichiesti.focus();
						   
      return false;
    } else {  
    	    	
			check = ControllaLSU();
			if (!check) {
				return false;
			}
						
			check = ControllaAvviamentoCM();		
			if (!check) {
				return false;
			}
			
      	    return true;  	
    }
  }
  
  function verificaDati() {
    if( document.Frm1.prgAzienda.value == "" ) {
      alert("Selezionare una Azienda con il pulsante Ricerca!");
      return false;
    }
	<% if (configSV.equals("1")) { %>
		if(document.Frm1.flgSV.checked && (document.Frm1.strMotSvantaggiati.value == "" || document.Frm1.datVerificaSvan.value == "")) {
			alert("I campi Motivazione e Data Verifica Svantaggio devono essere valorizzati entrambi");
			return false;
		}
		if(document.Frm1.flgDis.checked && (document.Frm1.strMotNonIscr.value == "" || document.Frm1.datVerificaDis.value == "")) {
			alert("I campi Motivazione e Data Verifica Non Iscritti devono essere valorizzati entrambi");
			return false;
		}
	<%}%>
	else {
		if(controllaFunzTL() && riportaControlloUtente( ControllaSesso() && controllaDateRichiestaScadenza() && controllaNumProfili() ) ) {
    		SettaAnnoRichiesta();
    	    doFormSubmit(document.Frm1);
    	}
	}
  }

  function SettaAnnoRichiesta() {
    strDataRichiesta = document.Frm1.datRichiesta.value;
    d1annoRichiesta = parseInt(strDataRichiesta.substr(6,4),10);
    document.Frm1.numAnno.value = d1annoRichiesta;
  }

  function ControllaSesso() {
    var bSessoOk = 1;
    if (!document.Frm1.strSesso.disabled) {
      if (document.Frm1.strSesso.value != "") {
        if (document.Frm1.codMotGenere.value == "") {
          bSessoOk = 0;
        }
      }
      else {
      	document.Frm1.codMotGenere.value = "";
      	document.Frm1.strMotivSesso.value = "";
      	return true;
      }
    }
    
   	if (document.Frm1.codMotGenere.value == "ALT") {
   		if (document.Frm1.strMotivSesso.value == "") {
   			alert("Inserire motivazione per la voce selezionata!");
   			return false;
   		}
   	}
    
    if (bSessoOk == 0) {
      alert("Inserire motivazione per il sesso!");
      return false;
    }
    else {
      return true;
    }
  }

  function controllaDataChiamata() {	
	    
    <%
    if (!reiteraRichAz) {
    
		// INIT-PARTE-TEMP
		if (Sottosistema.CM.isOff()) {
		// END-PARTE-TEMP		
		%>
			<%
			if (flgStatoEvasioneAS) {
		    %>
				if (document.Frm1.datChiamata.value == "" || document.Frm1.datChiamata.value == null) {
					alert("La data chiamata deve essere valorizzata per richieste\r\ncon stato di evasione Asta (art.16 L.56/87)!");
					return false;
				}		
				
				return true;	
		    <%	    
		    }	    	   	    
		    else { 	    
		    %>
		    	return true;	
		    <%
		    }
		    %> 	
		<%	
		// INIT-PARTE-TEMP
		} else {
		// END-PARTE-TEMP	
		%>			
		    <%
			if (flgStatoEvasioneAS) {
		    %>
				if (document.Frm1.datChiamata.value == "" || document.Frm1.datChiamata.value == null) {
					alert("La data chiamata deve essere valorizzata per richieste\r\ncon stato di evasione Asta (art.16 L.56/87)!");
					return false;
				}		
				
				return true;	
		    <%	    
		    }	    
		    else if (flgStatoEvasioneCMA) {
		    %>
		    	if (document.Frm1.datChiamataCM.value == "" || document.Frm1.datChiamataCM.value == null) {
					alert("La data chiamata deve essere valorizzata per richieste\r\ncon stato di evasione CM:Avviamento Numerico!");
					return false;
				}		
				
				return true;	
		    <%
		    }
		    else if (flgStatoEvasioneCMG) {
		    %>
		    	if (document.Frm1.codTipoLista.value == "" || document.Frm1.codTipoLista.value == null) {
					alert("Il tipo di iscrizione art.1 deve essere valorizzato per richieste\r\ncon stato di evasione CM:Graduatoria art.1!");
					return false;
				}		
				
				return true;	
		    <%	
		    }	    
		    else { 	    
		    %>
		    	return true;	
		    <%
		    }
		    %>  
		<%
		// INIT-PARTE-TEMP
		}
		// END-PARTE-TEMP
	}
	else {
	%>
		return true;
	<%
	}
	%>   
  }

  function enableFieldsAzienda(){

    document.Frm1.codTipoAzienda.disabled = false;
    document.Frm1.codTipoAzienda.className = "input";
    
    document.Frm1.cf.disabled = false;
    document.Frm1.cf.className = "input";

    document.Frm1.piva.disabled = false;
    document.Frm1.piva.className = "input";

    document.Frm1.RagioneSociale.disabled = false;
    document.Frm1.RagioneSociale.className = "input";

    document.Frm1.Indirizzo.disabled = false;
    document.Frm1.Indirizzo.className = "input";

    document.Frm1.codCom.disabled = false;
    document.Frm1.codCom.className = "input";

    document.Frm1.desComune.disabled = false;
    document.Frm1.desComune.className = "input";
  }

  function disableFieldsAzienda(){

      document.Frm1.codTipoAzienda.disabled = true;
      document.Frm1.codTipoAzienda.className = "inputView";
    
      document.Frm1.cf.disabled = true;
      document.Frm1.cf.className = "inputView";

      document.Frm1.piva.disabled = true;
      document.Frm1.piva.className = "inputView";

      document.Frm1.RagioneSociale.disabled = true;
      document.Frm1.RagioneSociale.className = "inputView";

      document.Frm1.Indirizzo.disabled = true;
      document.Frm1.Indirizzo.className = "inputView";

      document.Frm1.codCom.disabled = true;
      document.Frm1.codCom.className = "inputView";

      document.Frm1.desComune.disabled = true;
      document.Frm1.desComune.className = "inputView";
  }


  function btFindAzienda_onClick(TipoAzienda,CodiceFiscale,PIva,RagioneSociale,Indirizzo,CodComune,DesComune){
    //disableFieldsAzienda();
    if ((RagioneSociale.value.length > 0) || 
        (Indirizzo.value.length > 0) ||
        (CodiceFiscale.value.length > 0) || 
        (PIva.value.length > 0) ||
        (CodComune.value.length > 0) ) 
    {
	    var s= "AdapterHTTP?PAGE=RicercaAziendaPage";
	    s = s + "&CodTipoAzienda="+TipoAzienda.value;
	    s = s + "&CodiceFiscale="+CodiceFiscale.value;
	    s = s + "&PIva="+PIva.value;
	    s = s + "&RagioneSociale="+RagioneSociale.value;
	    s = s + "&Indirizzo="+Indirizzo.value;
	    s = s + "&CodComune="+CodComune.value;
	    s = s + "&DesComune="+DesComune.value;    
	    window.open(s,"Azienda", 'toolbar=0, scrollbars=1');
    } else {
      alert("Inserire almeno uno dei seguenti campi nella ricerca:\n\tComune\n\tIndirizzo\n\tCodice Fiscale\n\tPartita IVA\n\tRagione Sociale\n");
    }
  }


function checkDate(objData1, objData2) {

  strData1=objData1.value;
  strData2=objData2.value;

  //costruisco la data della richiesta
  d1giorno=parseInt(strData1.substr(0,2),10);
  d1mese=parseInt(strData1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero :P
  d1anno=parseInt(strData1.substr(6,4),10);
  data1=new Date(d1anno, d1mese, d1giorno);

  //costruisce la data di scadenza
  d2giorno=parseInt(strData2.substr(0,2),10);
  d2mese=parseInt(strData2.substr(3,2),10)-1;
  d2anno=parseInt(strData2.substr(6,4),10);
  data2=new Date(d2anno, d2mese, d2giorno);
  
  ok=true;
  if (data2 < data1) {
      alert("La "+ objData2.title +" è precedente alla "+ objData1.title);
      objData2.focus();
      ok=false;
   }
  return ok;
}


function setLocalitaVisible() {
  var objLocalita=eval("document.forms[0].strLocalita");
  var objFuoriSede=eval("document.forms[0].flgFuoriSede");
  
  flgFuoriSede=objFuoriSede.value;
  
 

  if (flgFuoriSede=="S") {
      <% if(canManage) {%>
          objLocalita.disabled= false; 
          objLocalita.className = "input";
      <% } else {%>  
        objLocalita.disabled= true; 
        objLocalita.className = "inputView";
      <%}%>
  }
  else {
        objLocalita.value="";
        objLocalita.disabled=true;
        objLocalita.className = "inputView";
  }

}


function controllaDateRichiestaScadenza(inputName) {
  return checkDate(document.forms[0].datRichiesta, document.forms[0].datScadenza);
}


  function resetFieldsAzienda(){
    document.Frm1.prgAzienda.value = "";
    document.Frm1.prgUnita.value = "";
    document.Frm1.codTipoAzienda.selectedIndex = 0;
    document.Frm1.cf.value = "";
    document.Frm1.piva.value = "";
    document.Frm1.RagioneSociale.value = "";
    document.Frm1.Indirizzo.value = "";
    document.Frm1.codCom.value = "";
    document.Frm1.desComune.value = "";
  }


  function annullaRicerca(){
    resetFieldsAzienda();
    enableFieldsAzienda();
    document.Frm1.prgAzienda.value = "<%=prgAzienda%>";
    document.Frm1.prgUnita.value   = "<%=prgUnita%>";

    //Resetto i campi del riferimento
    document.Frm1.strCognomeRiferimento.value = "";
    document.Frm1.strNomeRiferimento.value    = "";
    document.Frm1.strTelRiferimento.value     = "";
    document.Frm1.strFaxRiferimento.value     = "";
    document.Frm1.strEmailRiferimento.value   = "";
  }

function mostra(id)
{var div = document.getElementById(id);
 div.style.display="";
}

function nascondi(id)
{var div = document.getElementById(id);
 div.style.display="none";
}

function cambiaAzienda()
{ mostra("ricercaAzienda");
  nascondi("infoCorrAz");
  nascondi("cambiaAzButton");
  mostra("tornaAzButton");
}

function tornaAzCorrente()
{ nascondi("ricercaAzienda");
  mostra("infoCorrAz");
  mostra("cambiaAzButton");
  nascondi("tornaAzButton");
  annullaRicerca();
}

// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


function fieldChanged() {
    <%if (flag_insert || canModify) {out.print("flagChanged = true;");}%>
}


function go(url, alertFlag) {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var _url = "AdapterHTTP?" + url;
  if (alertFlag == 'TRUE' ) {
    if (confirm('Confermi operazione'))
    setWindowLocation(_url);
  }
  else
    setWindowLocation(_url);
}

function codComuneUpperCase(inputName){
  var ctrlObj = eval("document.forms[0]." + inputName);
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();")
	return true;
}

function  Riferimento() {
      this.Nome=null;
      this.Cognome=null;
      this.Telefono=null;
      this.Fax=null;
      this.E_mail=null;
      this.copia = copia;
}

function copia(rifer)
{

rifer.Nome = this.Nome;
rifer.Cognome = this.Cognome;
rifer.Telefono = this.Telefono;
rifer.Fax = this.Fax;
rifer.E_mail = this.E_mail;
return (rifer);
}

var lista_riferimenti=new Array();

function CaricaRiferimenti(ArrayRif){

  //lista_riferimenti=ArrayRif;
  //document.Frm1.Riferimenti.options[0]=null;
  opzione = new Option("", 0 , false, false);
//  document.Frm1.Riferimenti.options[0]=opzione;

  var len = ArrayRif.length;
  //alert("LEN::"+len+"  "+ArrayRif [0]);
  if (len == 0)
  {
      nascondi("comboRiferimenti");
  }
  else if (len == 1)
  {   document.Frm1.strCognomeRiferimento.value = ArrayRif [0].Cognome;
      document.Frm1.strNomeRiferimento.value = ArrayRif [0].Nome;
      document.Frm1.strTelRiferimento.value = ArrayRif [0].Telefono;
      document.Frm1.strFaxRiferimento.value = ArrayRif [0].Fax;
      document.Frm1.strEmailRiferimento.value = ArrayRif [0].E_mail;
      nascondi("comboRiferimenti");
  }
  else
  { document.Frm1.Riferimenti.options[0]=opzione;  
  	for (k=1; k<ArrayRif.length+1;k++) {
          rifer = new Riferimento ();
          lista_riferimenti[k]=ArrayRif [k-1].copia(rifer);
          opzione = new Option(lista_riferimenti[k].Cognome+" "+lista_riferimenti[k].Nome , k , false, false);
          document.Frm1.Riferimenti.options[k]=opzione;
      }//for
      mostra("comboRiferimenti");
  }
}//CaricaRiferimenti(_)



function SettaParametriRiferimenti(sel){

    k=parseInt(sel.value,10);

    if (k!=0) {
      document.Frm1.strCognomeRiferimento.value = lista_riferimenti[k].Cognome;
      document.Frm1.strNomeRiferimento.value    = lista_riferimenti[k].Nome;
      document.Frm1.strTelRiferimento.value     = lista_riferimenti[k].Telefono;
      document.Frm1.strFaxRiferimento.value     = lista_riferimenti[k].Fax;
      document.Frm1.strEmailRiferimento.value   = lista_riferimenti[k].E_mail;
    
    } else {
      document.Frm1.strCognomeRiferimento.value = "";
      document.Frm1.strNomeRiferimento.value    = "";
      document.Frm1.strTelRiferimento.value     = "";
      document.Frm1.strFaxRiferimento.value     = "";
      document.Frm1.strEmailRiferimento.value   = "";
    
    }
  
}//SettaParametriRiferimenti(_)

function controllaMotivoGenere() {
	var flgSesso = eval(document.Frm1.strSesso);
	var codMotGenere = eval(document.Frm1.codMotGenere);
	if (codMotGenere.value != 'ALT') {
		document.Frm1.strMotivSesso.disabled = true;
	}
	var div = document.getElementById("MOTIVAZIONESESSO");
  	if((flgSesso.value!=null) && (flgSesso.value!=""))
  	{ 
  		mostra("MOTIVAZIONESESSO");
	}
  	else
  	{ 
  		nascondi("MOTIVAZIONESESSO");
 	}
}//controllaMotivoGenere(_)

function controllaMotivazione() {
	if (document.Frm1.codMotGenere.value == 'ALT') {
		document.Frm1.strMotivSesso.disabled = false;
	}
	else {
		document.Frm1.strMotivSesso.value = "";
		document.Frm1.strMotivSesso.disabled = true;
	}
}


	function controllaTipoLSU() {
		var numLSU = eval(document.Frm1.numPostoLSU);
		var codTipoLSU = eval(document.Frm1.tipoLSU);
	
		var div = document.getElementById("CODTIPOLSU");
	  	if((numLSU.value!=null) && (numLSU.value!="") && (numLSU.value!="0"))
	  	{ 
	  		mostra("CODTIPOLSU");
		}
	  	else
	  	{ 
	  		nascondi("CODTIPOLSU");
	 	}
	}	
	
	
	function ControllaLSU() {
	    var bLsuOk = 1;
	    if (!document.Frm1.numPostoLSU.disabled) {
	      if (document.Frm1.numPostoLSU.value != "" && document.Frm1.numPostoLSU.value != "0" ) {
	        if (document.Frm1.tipoLSU.value == "") {
	          bLsuOk = 0;
	        }
	      }
	      else {
	      	document.Frm1.tipoLSU.value = "";
	      	document.Frm1.numPostoLSU.value = "";
	      	return true;
	      }
	    }
	    
	    if (bLsuOk == 0) {
	      alert("Inserire il tipo LSU!");
	      return false;
	    }
	    else {
	      return true;
	    }
	}

	function disableFieldsIscr() {
		<% if (configSV.equals("1")) { %>
			if(document.Frm1.flgSV.checked) {
				document.Frm1.datVerificaSvan.disabled = false;
				document.Frm1.datVerificaSvan.readonly=false;
				document.Frm1.datVerificaSvan.className="inputEdit";
				document.Frm1.strMotSvantaggiati.disabled = false;
			} else {
				document.Frm1.datVerificaSvan.disabled = true;
				document.Frm1.datVerificaSvan.readonly=true;
				document.Frm1.datVerificaSvan.className="viewRiepilogo";
				document.Frm1.strMotSvantaggiati.disabled = true;
			}
			if(document.Frm1.flgDis.checked) {
				document.Frm1.datVerificaDis.disabled = false;
				document.Frm1.datVerificaDis.readonly=false;
				document.Frm1.datVerificaDis.className="inputEdit";
				document.Frm1.strMotNonIscr.disabled = false;
			} else {
				document.Frm1.datVerificaDis.disabled = true;
				document.Frm1.datVerificaDis.readonly=true;
				document.Frm1.datVerificaDis.className="viewRiepilogo";
				document.Frm1.strMotNonIscr.disabled = true;
			}
		<%}%>
	}
			
	function settaFlag() {
		if(document.Frm1.flgSV.checked) {
		   document.Frm1.datVerificaSvan.disabled = false;
		   document.Frm1.datVerificaSvan.readonly=false;
		   document.Frm1.strMotSvantaggiati.disabled = false;
		   document.Frm1.datVerificaSvan.className="inputEdit";
		   document.Frm1.flgSvantaggiati.value="S";
		   document.Frm1.datVerificaSvan.value = '<%=DateUtils.getNow()%>';
		} else {
			document.Frm1.flgSvantaggiati.value="";
			document.Frm1.datVerificaSvan.className="viewRiepilogo";
			document.Frm1.datVerificaSvan.disabled = true;
			document.Frm1.datVerificaSvan.readonly=true;
			document.Frm1.strMotSvantaggiati.disabled = true;
			document.Frm1.datVerificaSvan.value = "";
			document.Frm1.strMotSvantaggiati.value = "";
		}
 	}

	function settaFlagDis() {
		if(document.Frm1.flgDis.checked) {
		   document.Frm1.datVerificaDis.disabled = false;
		   document.Frm1.datVerificaDis.readonly=false;
		   document.Frm1.datVerificaDis.className="inputEdit";
		   document.Frm1.strMotNonIscr.disabled = false;
		   document.Frm1.flgDisNonIscr.value="S";
		   document.Frm1.datVerificaDis.value = '<%=DateUtils.getNow()%>';
		} else {
			document.Frm1.datVerificaDis.disabled = true;
			document.Frm1.datVerificaDis.readonly=true;
			document.Frm1.datVerificaDis.className="viewRiepilogo";
			document.Frm1.strMotNonIscr.disabled = true;
			document.Frm1.flgDisNonIscr.value="";
			document.Frm1.datVerificaDis.value = "";
			document.Frm1.strMotNonIscr.value = "";
		}
 	}

	function verificaIscrizioni() {
		<% if (configSV.equals("1")) { %>
			if(document.Frm1.flgSV.checked && (document.Frm1.strMotSvantaggiati.value == "" || document.Frm1.datVerificaSvan.value == "")) {
				alert("I campi Motivazione e Data Verifica Svantaggio devono essere valorizzati entrambi");
  				return false;
  			}
			if(document.Frm1.flgDis.checked && (document.Frm1.strMotNonIscr.value == "" || document.Frm1.datVerificaDis.value == "")) {
				alert("I campi Motivazione e Data Verifica Non Iscritti devono essere valorizzati entrambi");
  				return false;
  			}
  		<%}%>
  		return true;	
	}

<%
// INIT-PARTE-TEMP
if (Sottosistema.CM.isOff()) {
// END-PARTE-TEMP		
	
// INIT-PARTE-TEMP
}
else {
// END-PARTE-TEMP
%> 
	function checkAvviamentoCM() {
		var codMonoTipoGrad = eval(document.Frm1.codMonoTipoGrad);
	
		var div = document.getElementById("CODTIPONUMERICO");
		var div = document.getElementById("CODTIPOART1");
	  	if((codMonoTipoGrad.value!=null) && (codMonoTipoGrad.value!="")) { 
	  		if (codMonoTipoGrad.value == "A" || codMonoTipoGrad.value == "D") {
	  			document.Frm1.codTipoLista.value = "";
	  			nascondi("CODTIPOART1");
	  			mostra("CODTIPONUMERICO");
	  		}
	  		if (codMonoTipoGrad.value == "G") {
		  		document.Frm1.datChiamataCM.value = "";
	  			nascondi("CODTIPONUMERICO");
	  			mostra("CODTIPOART1");
	  		}
		}
	  	else{ 
	  		document.Frm1.codTipoLista.value = "";
	  		document.Frm1.datChiamataCM.value = "";
	  		nascondi("CODTIPONUMERICO");
	  		nascondi("CODTIPOART1");
	 	}	
	}
	
	function ControllaAvviamentoCM() {
		var codMonoTipoGrad = document.Frm1.codMonoTipoGrad.value;
		var codTipoLista = document.Frm1.codTipoLista.value;
		var numPostiCM = parseInt(document.Frm1.numPostiCM.value);	
		//modifica Esposito
		var numAnnoRedditoCM = parseInt(document.Frm1.numAnnoRedditoCM.value);			
		var numAnnoRedditoCM_str = document.Frm1.numAnnoRedditoCM.value;
		
		if (codMonoTipoGrad == "A" || codMonoTipoGrad == "D") {
  			if (document.Frm1.datChiamataCM.value == "" || document.Frm1.datChiamataCM.value == null) {
				alert("La data chiamata deve essere valorizzata per richieste\r\ncon tipo di avviamento numerico!");
				return false;
			}	
  		}
  		if (codMonoTipoGrad == "G") {
  			if (document.Frm1.codTipoLista.value == "" || document.Frm1.codTipoLista.value == null) {
				alert("Il tipo di iscrizione art.1 deve essere valorizzato per richieste\r\ncon tipo di avviamento Graduatoria art.1!");
				return false;
			}
  		}
  		//modifica Esposito
  		if (codMonoTipoGrad != "" && numAnnoRedditoCM_str == ""){
  			alert("L'anno di riferimento per il reddito deve essere valorizzato");
  			return false;
  		}
		if (codMonoTipoGrad != "" && isNaN(numAnnoRedditoCM) ){
  			alert("L'anno di riferimento per il reddito contiene un valore non valido");
  			return false;
  		}
  		if (codMonoTipoGrad == "" && numAnnoRedditoCM_str != "" ){
  			alert("L'anno di riferimento per il reddito va valorizzato solo se è valorizzato il campo 'tipo'");
  			return false;
  		}
  		
		return true;	
	}

	function disableFieldsArt16(){      

		document.Frm1.datChiamata.disabled = true;
		document.Frm1.numPostoAS.disabled=true; 
	    document.Frm1.numPostoMB.disabled=true;
	    document.Frm1.numPostoLSU.disabled=true;
	    document.Frm1.numPostoMilitare.disabled=true;
	    document.Frm1.flgRiusoGraduatoria.disabled=true;
	    document.Frm1.tipoLSU.disabled=true;
	
		document.Frm1.datChiamata.className = "inputView";
		document.Frm1.numPostoAS.className = "inputView";
	    document.Frm1.numPostoMB.className = "inputView";
	    document.Frm1.numPostoLSU.className = "inputView";
	    document.Frm1.numPostoMilitare.className = "inputView";
		document.Frm1.flgRiusoGraduatoria.className = "inputView";
		document.Frm1.tipoLSU.className = "inputView";
	}
	
	
	function disableFieldsCM(){      
		document.Frm1.datChiamataCM.disabled = true;
		document.Frm1.numPostiCM.disabled=true; 
		//modifica Esposito
		document.Frm1.numAnnoRedditoCM.disabled=true;
		
		document.Frm1.codMonoTipoGrad.disabled=true;
		document.Frm1.codTipoLista.disabled=true;
		
		
		document.Frm1.datChiamataCM.className = "inputView";
		document.Frm1.numPostiCM.className = "inputView";
		//modifica Esposito
		document.Frm1.numAnnoRedditoCM.className = "inputView";	
		
		document.Frm1.codMonoTipoGrad.className = "inputView";
		document.Frm1.codTipoLista.className = "inputView";	
	}
	
	function enableFieldsArt16(){      

		document.Frm1.datChiamata.disabled = false;
		document.Frm1.numPostoAS.disabled=false; 
	    document.Frm1.numPostoMB.disabled=false;
	    document.Frm1.numPostoLSU.disabled=false;
	    document.Frm1.numPostoMilitare.disabled=false;
	    document.Frm1.flgRiusoGraduatoria.disabled=false;
	    document.Frm1.tipoLSU.disabled=false;
	
		document.Frm1.datChiamata.className = "input";
		document.Frm1.numPostoAS.className = "input";
	    document.Frm1.numPostoMB.className = "input";
	    document.Frm1.numPostoLSU.className = "input";
	    document.Frm1.numPostoMilitare.className = "input";
		document.Frm1.flgRiusoGraduatoria.className = "input";
		document.Frm1.tipoLSU.className = "input";
	}
	
	
	function enableFieldsCM(){      
		document.Frm1.datChiamataCM.disabled = false;
		document.Frm1.numPostiCM.disabled=false; 
		//modifica Esposito
		document.Frm1.numAnnoRedditoCM.disabled=false;
		
		document.Frm1.codMonoTipoGrad.disabled=false;
		document.Frm1.codTipoLista.disabled=false;
		
		
		document.Frm1.datChiamataCM.className = "input";
		document.Frm1.numPostiCM.className = "input";
		//modifica Esposito
		document.Frm1.numAnnoRedditoCM.className = "input";	
		
		document.Frm1.codMonoTipoGrad.className = "input";
		document.Frm1.codTipoLista.className = "input";	
	}

<%
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%> 

<% if (StringUtils.isFilled(prgAzienda) && StringUtils.isFilled(prgUnita)) {
  out.print("window.top.menu.caricaMenuAzienda("+_funzione+","+prgAzienda+","+prgUnita+");");
}%>

-->

</SCRIPT>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
	   	if (Utils.notNull(prgAzienda).length()>0 && Utils.notNull(prgUnita).length()>0)
	        attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita+"&prgRichiestaAz="+Utils.notNull(prgRichiestaAz));
      %>
</script>

<%@ include file="_apriGestionePubblSpecifica.inc" %>

<script language="Javascript">
	var sezioni = new Array();
	
	function cambia(immagine, sezione) {
		if (sezione.aperta==null) sezione.aperta=true;
		if (sezione.aperta) {
			sezione.style.display="none";
			sezione.aperta=false;
			immagine.src="../../img/chiuso.gif";
		}
		else {
			sezione.style.display="";
			sezione.aperta=true;
			immagine.src="../../img/aperto.gif";
		}
	}
	function Sezione(sezione, img,aperta){    
	    this.sezione=sezione;
	    this.sezione.aperta=aperta;
	    this.img=img;
	}
	
	function initSezioni(sezione){
		sezioni.push(sezione);
	}
</script>

</head>

<body class="gestione" onload="rinfresca();disableFields();setLocalitaVisible();checkDisable();disableFieldsIscr();">
<%
    if(infCorrentiAzienda != null) {%>
       <div id="infoCorrAz" style="display:"><%infCorrentiAzienda.show(out); %></div>
  <%}
      
    if (!flag_insert && !reiteraRichAz) {

        if(prgRichiestaAz != null && !prgRichiestaAz.equals("") ) {
          Linguette l = new Linguette( user,  _funzione, _page, new BigDecimal(prgRichiestaAz));
          l.setCodiceItem("PRGRICHIESTAAZ");    
          l.show(out);
        }
    }
%>

<p align="center">
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="ControllaSesso() && controllaDateRichiestaScadenza() && controllaNumProfili() && controllaDataChiamata() && verificaIscrizioni()">
  <input type="hidden" name="codEvasione" value="<%=codEvasione%>"/>
  <input type="hidden" name="numAnno" value=""/>
  <input type="hidden" name="numRichiesta" value=""/>
  <input type="hidden" name="numStorico" value="0"/>
  <center>
    <font color="green">
    <!--<%if (!flag_insert) {%>-->
      <af:showMessages prefix="M_SaveTestataRichiesta"/>
      <af:showMessages prefix="M_ReiteraRichAz"/>
    <!--<%}else{%>-->
      <af:showMessages prefix="M_InsertRichiesta"/>
    <!--<%}%>-->
    </font>
  </center>
  <center>
    <font color="red"><af:showErrors /></font>
  </center>
  
  <%out.print(htmlStreamTop);%>
  <table class="main" border="0">
    <tr>
      <td width="25%">&nbsp;</td>
      <td width="25%">&nbsp;</td>
      <td width="25%">&nbsp;</td>
      <td width="25%">&nbsp;</td>
    </tr>
    <%-- FV controllare ....  (Andrea: cosa?)--%>
    <%-- if(flag_insert && reiteraRichAz) { andrea 14/04/05 --%>
    <% if((canManage) && reiteraRichAz) { %>
    <tr><td colspan="4" align="left">
         <div id="cambiaAzButton" style="display:">
          <input type="button" class="pulsante" name="cambiaAz" value="Cambia Azienda" onClick="cambiaAzienda()"/>
          	<%-- 	QUANDO SI POTRANNO CANCELLARE QUESTE RIGHE DI CODICE ? --%>
		          <!--button type="button" class="ListButtonChangePage" name="CambiaAzineda" onClick="cambiaAzienda()">
		          <span align="center"><img id="immIndietro" src="../../img/ChangeAziendaLargeBut.gif" alt="Cambia azienda" align="absmiddle" /></span>
		          </button-->
          </div>
         <div id="tornaAzButton" style="display:none">
          <input type="button" class="pulsante" name="tornaAz" value="Torna all'azienda corrente" onClick="tornaAzCorrente()"/>
	          <!--button type="button" class="ListButtonChangePage" name="RipristinaAzienda" onClick="tornaAzCorrente()">
	          <span align="center"><img id="immIndietro" src="../../img/RestoreAzienda.gif" alt="Cambia azienda" align="absmiddle" /></span>
	          </button-->
          </div>
    </td></tr><tr><td>&nbsp;</td></tr>
    <tr><td colspan="4">
    <div id="ricercaAzienda" style="display:none">
    <table class="main">
    <%}%>
    <% if(((infCorrentiAzienda ==null || reiteraRichAz) && canManage)) { %>
      <tr>
        <td class="etichetta">Tipo Azienda</td>
        <td class="campo" colspan="3">
          <af:comboBox classNameBase="input"
                      title="Tipo Azienda"
                      name="codTipoAzienda"
                      moduleName="M_GetTipiAzienda"
                      addBlank="true" />
        </td>
      </tr>
      <tr>
        <td class="etichetta">Codice Fiscale</td>
        <td class="campo" colspan="3">
          <input type="text" name="cf" value="" size="20" maxlength="16"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Partita IVA</td>
        <td class="campo" colspan="3">
          <input type="text" name="piva" value="" size="20" maxlength="11"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Ragione Sociale</td>
        <td class="campo" colspan="3">
          <input type="text" name="RagioneSociale" value="" size="20" maxlength="100"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Indirizzo</td>
        <td class="campo" colspan="3">
          <input type="text" name="Indirizzo" value="" size="20" maxlength="100"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Comune</td>
        <td class="campo" colspan="3">
          <INPUT type="text" name="codCom" size="4" value="" maxlength="4" class="inputEdit" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.desComune, document.Frm1.desComuneHid, null, null, 'codice');"/>
          <A HREF="javascript:if(!document.Frm1.codCom.disabled) btFindComuneCAP_onclick(document.Frm1.codCom, document.Frm1.desComune, null, 'codice','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
          <INPUT type="HIDDEN" name="codComHid" size="20" value=""/>
          <INPUT type="text" name="desComune" size="30" value="" maxlength="50" class="inputEdit" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.desComune, document.Frm1.desComuneHid, null, null, 'descrizione');" onkeypress="if(event.keyCode==13) { event.keyCode=9; this.blur(); }"/>
          <A HREF="javascript:if(!document.Frm1.codCom.disabled) btFindComuneCAP_onclick(document.Frm1.codCom, document.Frm1.desComune, null, 'descrizione','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
          <INPUT type="HIDDEN" name="desComuneHid" size="20" value=""/>
       </td>                
    </tr>
    <tr><td colspan="4" align="center">
      <input class="pulsante" type="button" name="ricercaAzienda" value="Ricerca azienda" onclick="if(!document.Frm1.codCom.disabled) btFindAzienda_onClick(document.Frm1.codTipoAzienda,document.Frm1.cf,document.Frm1.piva,document.Frm1.RagioneSociale,document.Frm1.Indirizzo,document.Frm1.codCom,Frm1.desComune)"/>
      <input class="pulsante" type="button" name="annullaRicercaAzienda" value="Cancella" onclick="annullaRicerca()"/>
    </td></tr>
    <tr ><td colspan="4" ><hr width="90%"/></td></tr>      
    <%}%>
    <%if((canManage) && reiteraRichAz){%>
    </table>
    </div>
    </td>
    </tr>
    
    <% }//if(infCorrentiAzienda == null || reiteraRichAz) %>
    
    
    <% if (!flag_insert) { %>
        <tr valign="top">
        <%if(reiteraRichAz){%>
          <input type="hidden" name="prgRichiestaAZ" value="<%=prgRichiestaAz%>">
        <%} else { // semplice visualizzazione (no inserimento e non reiterazione) %>
          <td class="etichetta2" >Numero richiesta </td>
          <td class="campo2"><b><%=numRichiestaAnnoVis%></b>
          	<input type="hidden" name="prgNumeroRichiestaAZ" value="<%=numRichiestaAnno%>">
          </td>
          <input type="hidden" name="prgRichiestaAZ" value="<%=prgRichiestaAz%>">
        <%}%>
      <% } %>
      <td class="etichetta2" >Anno</td>
      <td class="campo2"><input type="text" readonly class="inputView" value="<%=numAnnoRichiesta%>"></td>
    </tr>
     <%
      String canModifyDate= "";
      if(manageDataCresco){
    	  manageDataCresco = !manageDataCresco && canManage;
    	  canModifyDate = String.valueOf(!(manageDataCresco));
      }else{
    	  canModifyDate = String.valueOf(!(canManage));
      }
      %>
    <tr valign="top">
      <td class="etichetta2">Data richiesta</td>
      <td class="campo2">
        <af:textBox classNameBase="input" title="Data richiesta" type="date" 
        	validateOnPost="true" onKeyUp="fieldChanged();" 
        	name="datRichiesta" size="11" maxlength="10" value="<%=datRichiesta%>" 
        	readonly="<%= canModifyDate %>" required="true" />
      </td>
      <td class="etichetta2">Data Scadenza</td>
     
      <td class="campo2">
        <af:textBox classNameBase="input"
                    title="Data scadenza"
                    readonly="<%= canModifyDate %>"
                    type="date"
                    validateOnPost="true"
                    name="datScadenza"
                    size="11"
                    maxlength="10"
                    value="<%=datScadenza%>"
                    onKeyUp="fieldChanged();"
                    required="true"/>
      </td>
    </tr>
  

	<tr><td colspan="4" >
		<table colspacing="0" cellspacing="0" border="0" width="100%">
			<tr><td width="20%"></td><td width="30%"></td><td width="20%"></td><td width="30%"></td></tr>
			<tr>
		    	<td colspan="4" width="100%">
		    		<table class='sezione' cellspacing=0 cellpadding=0>
						<tr>					
							<td  width=18>
		                    	<img id='I_SEL_S_DATI_GEN' src='<%=imgSelSezioneDatiGen%>' onclick='cambia(this, document.getElementById("T_S_DATI_GEN"))'></td>					
							<td class="sezione_titolo">Dati generali</td>							
						</tr>
					</table>
		     	</td>
		    </tr>
		    <tr>
    			<td colspan=4 align="center">
			     	<TABLE id='T_S_DATI_GEN' style='width:100%;display:<%=displaySDatiGen%>'>     
			        	<script>initSezioni( new Sezione(document.getElementById('T_S_DATI_GEN'),
			        									 document.getElementById('I_SEL_S_DATI_GEN'),
			        									 <%=legatoADatiGen%>)
			        						);
			        	</script>  
		    
					    <tr>
					      <td class="etichetta2">Numero figure professionali richieste</td>
					      <td class="campo2"><!--colspan="3"-->
					        <af:textBox classNameBase="input"
					                    readonly="<%= String.valueOf(!(canManage)) %>"
					                    title="Num. profili richiesti"
					                    type="integer"
					                    validateOnPost="true"
					                    name="numProfRichiesti"
					                    value="<%=numProfRichiesti%>"
					                    onKeyUp="fieldChanged();"
					                    required="true"
					                    size="10"
					                    maxlength="38"/>
					      </td>
					      <td class="etichetta2">Area di inserimento</td>
					      <td class="campo2"><!--colspan="3"-->
					        <af:comboBox classNameBase="input" onChange="fieldChanged();" name="codArea" 
					        	selectedValue="<%=codArea%>" disabled="<%= String.valueOf(!(canManage)) %>" moduleName="M_GetArea" 
					        	addBlank="true" />
					      </td>
					    </tr>
					
					    <tr valign="top">
					          <td class="etichetta2">Fuori sede</td>
					      <td class="campo2">
					        <af:comboBox 
					          name="flgFuoriSede"
					          classNameBase="input"
					          onChange="fieldChanged();setLocalitaVisible();">
					  
					          <option value=""  <% if ( "".equalsIgnoreCase(flgFuoriSede) )  { %>SELECTED<% } %> ></option>
					          <option value="S" <% if ( "S".equalsIgnoreCase(flgFuoriSede) ) { %>SELECTED<% } %> >Sì</option>
					          <option value="N" <% if ( "N".equalsIgnoreCase(flgFuoriSede) ) { %>SELECTED<% } %> >No</option>
					        </af:comboBox>
					        <script language="javascript">
					          if (<%=String.valueOf(!(canManage))%>) {
					            document.Frm1.flgFuoriSede.disabled = true;
					          }
					        </script>
					      </td>
					        <td class="etichetta2">Località</td>
					        <td class="campo2">
					          <af:textBox name="strLocalita"
					                      classNameBase="input"
					                      value="<%=strLocalita%>"
					                      onKeyUp="fieldChanged();"
					                      maxlength="50"/>
					
					        </td>
					    </tr>
					
					    <tr valign="top">
					      <td class="etichetta2">Automunito</td>
					      <td class="campo2">
					        <af:comboBox 
					          name="flgAutomunito"
					          classNameBase="input"
					          onChange="fieldChanged()">
					  
					          <option value=""  <% if ( "".equalsIgnoreCase(flgAutomunito) )  { %>SELECTED<% } %> ></option>
					          <option value="S" <% if ( "S".equalsIgnoreCase(flgAutomunito) ) { %>SELECTED<% } %> >Sì</option>
					          <option value="N" <% if ( "N".equalsIgnoreCase(flgAutomunito) ) { %>SELECTED<% } %> >No</option>
					          <option value="P" <% if ( "P".equalsIgnoreCase(flgAutomunito) ) { %>SELECTED<% } %> >Preferibile</option>          
					        </af:comboBox>
					        <script language="javascript">
					          if (<%=String.valueOf(!(canManage))%>) {
					            document.Frm1.flgAutomunito.disabled = true;
					          }
					        </script>
					      </td>
					       
					            <td class="etichetta2">Trasferta</td>
					            <td class="campo2">
					              <af:comboBox classNameBase="input"
					                          onChange="fieldChanged();"
					                          name="codTrasferta"
					                          selectedValue="<%=codTrasferta%>"
					                          disabled="<%= String.valueOf(!(canManage)) %>"
					                          moduleName="MListTrasferte"
					                          addBlank="true" />
					            </td>
					    </tr>
					
					
					   <tr valign="top">
					      <td class="etichetta2">Motomunito</td>
					      <td class="campo2">
					        <af:comboBox 
					          name="flgMotomunito"
					          classNameBase="input"
					          onChange="fieldChanged()">
					  
					          <option value=""  <% if ( "".equalsIgnoreCase(flgMotomunito) )  { %>SELECTED<% } %> ></option>
					          <option value="S" <% if ( "S".equalsIgnoreCase(flgMotomunito) ) { %>SELECTED<% } %> >Sì</option>
					          <option value="N" <% if ( "N".equalsIgnoreCase(flgMotomunito) ) { %>SELECTED<% } %> >No</option>
					          <option value="P" <% if ( "P".equalsIgnoreCase(flgMotomunito) ) { %>SELECTED<% } %> >Preferibile</option>
					        </af:comboBox>
					        <script language="javascript">
					          if (<%=String.valueOf(!(canManage))%>) {
					            document.Frm1.flgMotomunito.disabled = true;
					          }
					        </script>
					      </td>
					      <td class="etichetta2">Disponibilità alloggio aziendale</td>
					      <td class="campo2">
					        <af:comboBox 
					          name="flgVittoAlloggio"
					          classNameBase="input"
					          onChange="fieldChanged()">
					          <option value=""  <% if ( "".equalsIgnoreCase(flgVittoAlloggio) )  { %>SELECTED<% } %> ></option>
					          <option value="S" <% if ( "S".equalsIgnoreCase(flgVittoAlloggio) ) { %>SELECTED<% } %> >Sì</option>
					          <option value="N" <% if ( "N".equalsIgnoreCase(flgVittoAlloggio) ) { %>SELECTED<% } %> >No</option>
					        </af:comboBox>
					        <script language="javascript">
					          if (<%=String.valueOf(!(canManage))%>) {
					            document.Frm1.flgVittoAlloggio.disabled = true;
					          }
					        </script>
					      </td>
					  </tr>
			          <tr>
			            <td class="etichetta2">Milite esente/assolto</td>
			            <td class="campo2">
			              <af:comboBox 
			                name="flgMilite"
			                classNameBase="input"
			                onChange="fieldChanged()">
			  
			                <option value=""  <% if ( "".equalsIgnoreCase(flgMilite) )  { %>SELECTED<% } %> ></option>
			                <option value="S" <% if ( "S".equalsIgnoreCase(flgMilite) ) { %>SELECTED<% } %> >Sì</option>
			                <option value="N" <% if ( "N".equalsIgnoreCase(flgMilite) ) { %>SELECTED<% } %> >No</option>
			                <option value="P" <% if ( "P".equalsIgnoreCase(flgMilite) ) { %>SELECTED<% } %> >Preferibile</option>                
			              </af:comboBox>
			              <script language="javascript">
			                if (<%=String.valueOf(!(canManage))%>) {
			                  document.Frm1.flgMilite.disabled = true;
			                }
			              </script>
			            </td>
			            <%
			            if (checkVdA) {
			            %>
			              <td class="etichetta2">Solo richieste con vitto</td>
					      <td class="campo2">
					        <af:comboBox 
					          name="flgVitto"
					          classNameBase="input"
					          onChange="fieldChanged()">
					          <option value=""  <% if ( "".equalsIgnoreCase(flgVitto) )  { %>SELECTED<% } %> ></option>
					          <option value="S" <% if ( "S".equalsIgnoreCase(flgVitto) ) { %>SELECTED<% } %> >Sì</option>
					          <option value="N" <% if ( "N".equalsIgnoreCase(flgVitto) ) { %>SELECTED<% } %> >No</option>
					        </af:comboBox>
					        <script language="javascript">
					          if (<%=String.valueOf(!(canManage))%>) {
					            document.Frm1.flgVitto.disabled = true;
					          }
					        </script>
					      </td>
					   <%
			           }
			           else {
			           %>
			           		<td class="etichetta2"></td>
					   		<td class="campo2"><input type="hidden" name="flgVitto" value="" /></td>
			           <%	   
			           }			           
					   %>   
			         </tr>
		  
				     <tr>
				     <%
			         if (checkVdA) {
			         %>				    
				        <td colspan="2"><br/></td>
				        <td class="etichetta2">Turismo</td>
					      <td class="campo2">
					        <af:comboBox 
					          name="flgTurismo"
					          classNameBase="input"
					          onChange="fieldChanged()">
					          <option value=""  <% if ( "".equalsIgnoreCase(flgTurismo) )  { %>SELECTED<% } %> ></option>
					          <option value="S" <% if ( "S".equalsIgnoreCase(flgTurismo) ) { %>SELECTED<% } %> >Sì</option>
					          <option value="N" <% if ( "N".equalsIgnoreCase(flgTurismo) ) { %>SELECTED<% } %> >No</option>
					        </af:comboBox>
					        <script language="javascript">
					          if (<%=String.valueOf(!(canManage))%>) {
					            document.Frm1.flgTurismo.disabled = true;
					          }
					        </script>
					      </td>
					<%
		            }
		            else {
		            %>
		            	<td class="etichetta2"></td>
				     	<td class="campo2"><input type="hidden" name="flgTurismo" value="" /></td>
		            <%	   
		            }			           
				    %>   
				    </tr>
				
				    <tr valign="top">
				      <td class="etichetta2">Sesso</td>
				      <td class="campo2" colspan="3">
				        <af:comboBox 
				          name="strSesso"
				          classNameBase="input"
				          onChange="fieldChanged();controllaMotivoGenere();">
				          <option value=""  <% if ( "".equalsIgnoreCase(strSesso) )  { %>SELECTED<% } %> ></option>
				          <option value="M" <% if ( "M".equalsIgnoreCase(strSesso) ) { %>SELECTED<% } %> >M</option>
				          <option value="F" <% if ( "F".equalsIgnoreCase(strSesso) ) { %>SELECTED<% } %> >F</option>
				        </af:comboBox>
				        <script language="javascript">
				          if (<%=String.valueOf(!(canManage))%>) {
				            document.Frm1.strSesso.disabled = true;
				          }
				        </script>
				      </td>
				    </tr>
				    
<tr valign="top"><td colspan="4">
<div id="MOTIVAZIONESESSO" style="display:<%=displayMotGenere%>">
  <table colspacing="0" cellspacing="0" border="0" width="100%">
  <tr><td class="etichetta2">Motivazione</td>
  <td class="campo2" colspan="3">
    <af:comboBox classNameBase="input"
                          onChange="fieldChanged();controllaMotivazione();"
                          name="codMotGenere"
                          selectedValue="<%=codMotGenere%>"
                          disabled="<%= String.valueOf(!(canManage)) %>"
                          moduleName="COMBO_MOTIVO_SESSO"
                          addBlank="true" />
    </td>
    </tr>
      <tr><td class="etichetta2"></td>
      	<td class="campo2" colspan="3">
        <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" 
        			 name="strMotivSesso" cols="50" value="<%=strMotivSesso%>" 
        			 maxlength="100" readonly="<%= String.valueOf(!(canManage)) %>" />
        <script language="javascript">
          if (<%=codMotGenere.equals("ALT")%>) {
            document.Frm1.strMotivSesso.disabled = false;
          }
          else {
          	document.Frm1.strMotivSesso.disabled = true;
          }
        </script>
      </td>
      
    </tr>  
   </table>
</div>
</td></tr>
				</table>
				</td>
			</tr>
		</table>
	</td></tr>
  </table>
  <% if (configSV.equals("1")) { %>
   <table id="sezione_svantaggiati" width="100%" border="0">			
			<tr>
				<td width="20%"></td>
				<td width="30%"></td>
				<td width="20%"></td>
				<td width="30%"></td>
			</tr>
	     	<tr>
	     		<td colspan="4" width="100%">
	     			<table class='sezione' cellspacing=0 cellpadding=0>
						<tr>					
							<td  width=18>
		                    	<img id='I_SEL_SVANTAGGIATI' src='<%=imgSelSezioneSvantaggiati%>' 
		                    		  onclick='cambia(this, document.getElementById("T_S_SVANTAGGIATI"))'>
		                    </td>					
							<td class="sezione_titolo">Svantaggiati</td>							
						</tr>
					</table>
	     		</td>
	     	</tr>    
	     	<tr>
    			<td colspan=4 align="center">
			     	<TABLE id='T_S_SVANTAGGIATI' style='width:80%;display:<%=displaySvantaggiati%>'>     
			        	<script>initSezioni( new Sezione(document.getElementById('T_S_SVANTAGGIATI'),
			        									 document.getElementById('I_SEL_SVANTAGGIATI'),
			        									 <%=legatoAgliSvantaggiati%>) );
			        	</script>     
			        	<tr valign="top">
			        		<td class="etichetta2">Svantaggiati</td>
   							<td class="campo2">
   								<input type="checkbox" title="Flag Svantaggiati" name="flgSV" value="" <%=flgSvantaggiati.equals("S") ? "CHECKED" : ""%>
   				   					   onclick="settaFlag();"  onKeyUp="fieldChanged();" <%=String.valueOf(!canManage )%> />
   							</td>
   							<input type="hidden" name="flgSvantaggiati" value="<%=flgSvantaggiati%>" />
   						</tr>
   						<tr>
   							<td class="etichetta">Motivazione</td>
    						<td class="campo">
      							<af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strMotSvantaggiati" cols="50" title="Motivazione Svantaggiati" 
                   						 	 value="<%=strMotSvantaggiati%>"  maxlength="100" readonly="<%=String.valueOf(!canManage )%>" />
    						</td>
    					</tr>
   						<tr>
   							<td class="etichetta">Data Verifica Svantaggio</td>
      						<td class="campo">
      							<af:textBox classNameBase="input" type="date" name="datVerificaSvan"  size="11" maxlength="10" 
      									    value="<%=datVerificaSvan%>" onKeyUp="fieldChanged();" readonly="<%=String.valueOf(!canManage )%>" 
   											title="Data Verifica Svantaggio" validateOnPost="true" />
      						</td>
   						</tr>
   				</table>
		    </td>
		</tr>
	</table>
	
	<table id="sezione_disabili" width="100%" border="0">			
		<tr>
			<td width="20%"></td>
			<td width="30%"></td>
			<td width="20%"></td>
			<td width="30%"></td>
		</tr>
	    <tr>
	    	<td colspan="4" width="100%">
	     		<table class='sezione' cellspacing=0 cellpadding=0>
					<tr>					
						<td  width=18>
		                	<img id='I_SEL_DIS_NON_ISCR' src='<%=imgSelSezioneDisabili%>' 
		                         onclick='cambia(this, document.getElementById("T_S_DIS_NON_ISCR"))'>
		                </td>					
					    <td class="sezione_titolo">Disabili Non Iscritti</td>							
					</tr>
				</table>
	     	</td>
	     </tr>    
	     <tr>
    		<td colspan=4 align="center">
		     	<TABLE id='T_S_DIS_NON_ISCR' style='width:80%;display:<%=displayDisabili%>'>     
		        	<script>initSezioni( new Sezione(document.getElementById('T_S_DIS_NON_ISCR'),
		        									 document.getElementById('I_SEL_DIS_NON_ISCR'),
		        									 <%=legatoAiDisabiliNonIscritti%>) );
		        	</script>     
		        	<tr valign="top">
		        		<td class="etichetta2">Disabili Non Iscritti</td>
   						<td class="campo2">
   							<input type="checkbox" title="Flag Disabili Non Iscritti" name="flgDis" value="" <%=flgDisNonIscr.equals("S") ? "CHECKED" : ""%>
   			   					   onclick="settaFlagDis();"  onKeyUp="fieldChanged();" <%=String.valueOf(!canManage )%> />
   						</td>
   						<input type="hidden" name="flgDisNonIscr" value="<%=flgDisNonIscr%>" />
   					</tr>
   					<tr>
   						<td class="etichetta">Motivazione</td>
    					<td class="campo">
      						<af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strMotNonIscr" cols="50" title="Motivazione Disabili Non Iscritti" 
                   					 	 value="<%=strMotNonIscr%>"  maxlength="100" readonly="<%=String.valueOf(!canManage )%>" />
    					</td>
    				</tr>
   					<tr>
   						<td class="etichetta">Data Verifica Non Iscritti</td>
      					<td class="campo">
      						<af:textBox classNameBase="input" type="date" name="datVerificaDis"  size="11" maxlength="10" 
      								    value="<%=datVerificaDis%>" onKeyUp="fieldChanged();" readonly="<%=String.valueOf(!canManage )%>" 
   										title="Data Verifica Non Iscritti" validateOnPost="true"/>
      					</td>
   					</tr>
   			</table>
		 </td>
	  </tr>
	</table>
	
	<% } %>
	
	<table id="sezione_riferimento" width="100%" border="0">

     <tr><td width="20%"></td><td width="30%"></td><td width="20%"></td><td width="30%"></td></tr>
     <tr >
     	<td colspan="4" width="100%">
     		<table class='sezione' cellspacing=0 cellpadding=0>
				<tr>					
					<td  width=18>
                    	<img id='I_SEL_S_RIFERIMENTO' src='<%=imgSelSezioneRiferimento%>' onclick='cambia(this, document.getElementById("T_S_RIFERIMENTO"))'></td>					
					<td class="sezione_titolo">Riferimento</td>							
				</tr>
			</table>
     	</td>
     </tr>
     <tr><td colspan="4">
		<TABLE id='T_S_RIFERIMENTO' style='width:100%;display:<%=displaySRiferimento%>'>     
	    	<script>initSezioni( new Sezione(document.getElementById('T_S_RIFERIMENTO'),
	    									 document.getElementById('I_SEL_S_RIFERIMENTO'),
	    									 <%=legatoAlRiferimento%>)
	    						);
	    	</script>  
			<%if(flag_insert || reiteraRichAz){%>
			     <tr><td colspan="4"><div id="comboRiferimenti" style="display:">
			     <table cellpadding="0" cellspacing="0" border="0" width="100%">
			     <tr><td width="20%"></td><td width="30%"></td><td width="20%"></td><td width="30%"></td></tr>
			       <tr><td class="etichetta2">Riferimenti</td>
			           <td class="campo2" colspan="3">
			              <af:comboBox classNameBase="input" name="Riferimenti" onChange="SettaParametriRiferimenti(this);"
			                           addBlank="true" disabled="<%=String.valueOf(!(canManage)) %>">
			              </af:comboBox>
			           </td>
			     </tr>
			    <%SourceBean riga = null;
			    Vector RiferimentiAzienda = serviceResponse.getAttributeAsVector("M_GETREFERENTIAZIENDA.ROWS.ROW");    %>
						<script type="text/javascript">
			      var ArrayRif = new Array ();
			      <%for (int i = 0; i < RiferimentiAzienda.size(); i++) {
			          riga=(SourceBean) RiferimentiAzienda.elementAt(i);
			          strCognomeRiferimento = StringUtils.getAttributeStrNotNull(riga, "STRCOGNOME");
			          strNomeRiferimento    = StringUtils.getAttributeStrNotNull(riga, "STRNOME");
			          strTelRiferimento     = StringUtils.getAttributeStrNotNull(riga, "STRTELEFONO");
			          strFaxRiferimento     = StringUtils.getAttributeStrNotNull(riga, "STRFAX");
			          strEmailRiferimento   = StringUtils.getAttributeStrNotNull(riga, "STREMAIL");
//			        strEmailRiferimento nella tabella DO_RICHIESTA_AZ è VARCHAR2(50 BYTE) 
//			        ma proviene da STREMAIL - tabella AN_AZ_REFERENTE dove è VARCHAR2(80 BYTE) 
			          strEmailRiferimento   = strEmailRiferimento.substring(0,Math.min(50,strEmailRiferimento.length()));
			      %>
			          var riferimenti = new Riferimento();
			          riferimenti.Cognome="<%=strCognomeRiferimento%>";
			          riferimenti.Nome="<%=strNomeRiferimento%>";
			          riferimenti.Telefono="<%=strTelRiferimento%>";
			          riferimenti.Fax="<%=strFaxRiferimento%>";
			          riferimenti.E_mail="<%=strEmailRiferimento%>";
			          ArrayRif.push(riferimenti);
			     <% }//for %>
			      </script>
					</table></div></td></tr>
			<%}%>
			          <tr valign="top">
			            <td class="etichetta2">Cognome</td>
			            <td class="campo2">
			              <af:textBox classNameBase="input" title="Cognome nel riferimento" onKeyUp="fieldChanged();" 
			              name="strCognomeRiferimento" value="<%=strCognomeRiferimento%>" 
			              readonly="<%= String.valueOf(!(canManage)) %>"  maxlength="40" />
			            </td>
			            <td class="etichetta2">Nome</td>
			            <td class="campo2">
			              <af:textBox classNameBase="input" onKeyUp="fieldChanged();" 
			              	name="strNomeRiferimento" size="20" value="<%=strNomeRiferimento%>" 
			              	readonly="<%= String.valueOf(!(canManage)) %>" maxlength="40" />
			            </td>
			          </tr>
			
			          <tr valign="top">
			            <td class="etichetta2">Telefono</td>
			            <td class="campo2">
			              <af:textBox classNameBase="input" title="Telefono nel riferimento" 
			              onKeyUp="fieldChanged();" name="strTelRiferimento" size="30" 
			              value="<%=strTelRiferimento%>" readonly="<%= String.valueOf(!(canManage)) %>" maxlength="50" />
			            </td>
			            <td class="etichetta2">Fax</td>
			            <td class="campo2">
			              <af:textBox classNameBase="input" onKeyUp="fieldChanged();" 
			              	name="strFaxRiferimento" size="20" value="<%=strFaxRiferimento%>" 
			              	readonly="<%= String.valueOf(!(canManage)) %>" maxlength="20" />
			            </td>
			          </tr>
			
			          <tr valign="top">
			            <td class="etichetta2">Email</td>
			            <td class="campo2" colspan="3">
			              <af:textBox classNameBase="input" onKeyUp="fieldChanged();" 
			              	name="strEmailRiferimento" size="30" 
			              	value="<%=strEmailRiferimento%>" readonly="<%= String.valueOf(!(canManage)) %>" maxlength="50" />
			            </td>
			          </tr>
			<%if( flag_insert || reiteraRichAz){%>
				<script TYPE="text/javascript">CaricaRiferimenti(ArrayRif);</script>
			<%}%>
 			</table>         
          </td>             
          </tr>
   </table>
   <br>
      	
	<%
	boolean legatoAllAsta = false;
	if (flag_insert) {
	  legatoAllAsta = true;
    }
    else if (!StringUtils.isEmptyNoBlank(datChiamata) ||
	!StringUtils.isEmptyNoBlank(numPostoMB) ||
	!StringUtils.isEmptyNoBlank(numPostoAS) ||
	!StringUtils.isEmptyNoBlank(numPostoMilitare) ||
	!StringUtils.isEmptyNoBlank(flgRiusoGraduatoria) ||
	!StringUtils.isEmptyNoBlank(numPostoLSU)) {
		legatoAllAsta = true;
	}
	// gestione visualizzazione sezione art 16  	
  	String imgSelSezioneAsta = legatoAllAsta?"../../img/aperto.gif":"../../img/chiuso.gif";
  	String displaySAsta = legatoAllAsta? "":"none";
	%>
		<input type="hidden" name="flagAS" value="1" />
		<table id="sezione_asta_art16" width="100%" border="0">			
			<tr>
				<td width="20%"></td>
				<td width="30%"></td>
				<td width="20%"></td>
				<td width="30%"></td>
			</tr>
	     	<tr>
	     		<td colspan="4" width="100%">
	     			<table class='sezione' cellspacing=0 cellpadding=0>
						<tr>					
							<td  width=18>
		                    	<img id='I_SEL_S_ASTA' src='<%=imgSelSezioneAsta%>' onclick='cambia(this, document.getElementById("T_S_ASTA"))'></td>					
							<td class="sezione_titolo">Dati asta art.16 Legge 56/87</td>							
						</tr>
					</table>
	     		</td>
	     	</tr>    
	     	<tr>
    			<td colspan=4 align="center">
			     	<TABLE id='T_S_ASTA' style='width:80%;display:<%=displaySAsta%>'>     
			        	<script>
			        	        initSezioni( new Sezione(document.getElementById('T_S_ASTA'),
			        									 document.getElementById('I_SEL_S_ASTA'),
			        									 <%=legatoAllAsta%>)
			        						);
			        	</script>     
				        <tr valign="top">
				            <td class="etichetta2">Data chiamata</td>
				      		<td class="campo2">
				        		<af:textBox classNameBase="input" title="Data chiamata" type="date" 
				        			validateOnPost="true" onKeyUp="fieldChanged();" 
				        			name="datChiamata" size="11" maxlength="10" value="<%= datChiamata%>"   
				        			readonly="<%= String.valueOf(! (canModifyAS)) %>" />  
				      		</td>   
				            <td class="etichetta2">n. posti mobilità</td>
				            <td class="campo2">
				              <af:textBox classNameBase="input" onKeyUp="fieldChanged();settaNumeroFigureProf();" 
				              	name="numPostoMB" size="5" value="<%= numPostoMB%>" 
				              	readonly="<%= String.valueOf(!(canModifyAS)) %>" maxlength="4" />
				            </td>
						</tr>
						<tr valign="top">
				        	<td class="etichetta2">n. posti art. 16</td>
				            <td class="campo2">
				              <af:textBox classNameBase="input" onKeyUp="fieldChanged();settaNumeroFigureProf();" 
				              	name="numPostoAS" size="5" value="<%= numPostoAS%>" 
				              	readonly="<%= String.valueOf(!(canModifyAS)) %>" maxlength="4" />
				            </td>
				            <td class="etichetta2">n. posti ex militari</td>
				            <td class="campo2">
				              <af:textBox classNameBase="input" onKeyUp="fieldChanged();settaNumeroFigureProf();" 
				              	name="numPostoMilitare" size="5" value="<%= numPostoMilitare%>" 
				              	readonly="<%= String.valueOf(!(canModifyAS)) %>" maxlength="4" />
				            </td>
						</tr>
					  	<tr valign="top">
				        	<td class="etichetta2">Riutilizzo graduatoria</td>
				            <td class="campo2">
				              <af:comboBox 
				                name="flgRiusoGraduatoria"
				                classNameBase="input"
				                onChange="fieldChanged()">
				  
				                <option value=""  <% if ( "".equalsIgnoreCase(flgRiusoGraduatoria) )  { %>SELECTED<% } %> ></option>
				                <option value="S" <% if ( "S".equalsIgnoreCase(flgRiusoGraduatoria) ) { %>SELECTED<% } %> >Sì</option>
				                <option value="N" <% if ( "N".equalsIgnoreCase(flgRiusoGraduatoria) ) { %>SELECTED<% } %> >No</option>               
				              </af:comboBox>
				              <script language="javascript">
				                if (<%=String.valueOf(!(canModifyAS))%>) {
				                  document.Frm1.flgRiusoGraduatoria.disabled = true;
				                }
				              </script>
				            </td>
				            <td class="etichetta2">n. posti ex LSU</td>
				            <td class="campo2">
				              <af:textBox classNameBase="input" onKeyUp="fieldChanged();settaNumeroFigureProf();controllaTipoLSU();" 
				              	name="numPostoLSU" size="5" value="<%= numPostoLSU%>"    
				              	readonly="<%= String.valueOf(!(canModifyAS)) %>" maxlength="4" />
				            </td>
						</tr>
						
        				<%	boolean canModifyFlgAs = false;
        					if (canASOnline) { //verificare
        						 canModifyFlgAs = canModifyAS;
        					 
        						if(StringUtils.isFilledNoBlank(dtAsOnline)){
        							canModifyFlgAs = false;
        						}
        				
        				%>
						<tr valign="top">
		 					<td class="etichetta2">Istanze Online</td>
	     					<td class="campo2">
	     					 <af:comboBox name="FLGASONLINE" classNameBase="input"  
	     					 addBlank="true" 
	     					 required="false" 
	     					 title="Istanze online" 
                  				moduleName="M_GenericComboSiNo" 
                  				selectedValue="<%=flAsOnline%>" 
                  				disabled="<%=String.valueOf(!(canModifyFlgAs))%>"
                      		/>
	     					</td>
	     					<td>&nbsp;</td><td>&nbsp;</td>
	     				</tr>    
	     				<tr valign="top">
	     			        <td class="etichetta2">Data e ora aggiornamento istanze</td>
				            <td class="campo2">
				              <af:textBox classNameBase="input" 
				              	name="dtAggIst" size="30" value="<%= dtAsOnline%>"    
				              	readonly="true"/>
				            </td><td>&nbsp;</td><td>&nbsp;</td>
				           </tr> 
			 			<%}else{%>
			 				<input type="hidden" name="FLGASONLINE" />
			 			<%}%>			  
						
							
						<tr valign="top">
							<td></td>
							<td></td>	
							<td colspan="2">
								<div id="CODTIPOLSU" style="display:<%=displayTipoLSU%>">
									<table colspacing="0" cellspacing="0" border="0" width="100%">
								  		<tr>
								  			<td class="etichetta2">Tipo LSU</td>
									  		<td class="campo2">
									  		<af:comboBox name="tipoLSU"
									                     size="1"
									                     title=""
									                     multiple="false"
									                     required="false"
									                     focusOn="false"
									                     moduleName="COMBO_TIPO_LSU"
									                     addBlank="true"
									                     blankValue=""
									                     selectedValue = "<%=codTipoLSU%>"					                     
									                     classNameBase="input"					                    
									        />									  			
									              <script language="javascript">
										                if (<%=String.valueOf(!(canModifyAS))%>) {
										                  document.Frm1.tipoLSU.disabled = true;
										                }
										          </script>
								   			</td>		      		
								    	</tr>  
								   </table>
								</div>
							</td>
						</tr>
						
					</table>
		    	</td>
			</tr>    
	    	<tr><td colspan=4><br></td></tr>
	   	</table>		   
    <%
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) {
	// END-PARTE-TEMP
	%>

	<%
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP
	%>
	
	<%
	boolean legatoAllAstaCM = false;
	if (flag_insert) {
	  legatoAllAstaCM = true;
    }
    else if (!StringUtils.isEmptyNoBlank(datChiamataCM) ||
	!StringUtils.isEmptyNoBlank(numPostiCM)) {
		legatoAllAstaCM = true;
	}
	// gestione visualizzazione sezione art 16  	
  	String imgSelSezioneAstaCM = legatoAllAstaCM?"../../img/aperto.gif":"../../img/chiuso.gif";
  	String displaySAstaCM = legatoAllAstaCM? "":"none";
	%>  
   
   		<input type="hidden" name="flagCM" value="1" />
		<table id="sezione_asta_cm" width="100%" border="0">			
			<tr>
				<td width="20%"></td>
				<td width="30%"></td>
				<td width="20%"></td>
				<td width="30%"></td>
			</tr>
	     	<tr>
	     		<td colspan="4" width="100%">
	     			<table class='sezione' cellspacing=0 cellpadding=0>
						<tr>					
							<td  width=18>
		                    	<img id='I_SEL_S_ASTA_CM' src='<%=imgSelSezioneAstaCM%>' onclick='cambia(this, document.getElementById("T_S_ASTA_CM"))'></td>					
							<td class="sezione_titolo">Dati graduatorie CM</td>							
						</tr>
					</table>
	     		</td>
	     	</tr>    
	     	<tr>
    			<td colspan=4 align="center">
			     	<TABLE id='T_S_ASTA_CM' style='width:80%;display:<%=displaySAstaCM%>'>     
			        	<script>
			        	        initSezioni( new Sezione(document.getElementById('T_S_ASTA_CM'),
			        									 document.getElementById('I_SEL_S_ASTA_CM'),
			        									 <%=legatoAllAstaCM%>)
			        						);
			        	</script>     
			        	<!--modifica Esposito-->				        
						<tr valign="top">				        	
							<td class="etichetta2">Tipo</td>
				            <td class="campo2">
				            	<af:comboBox name="codMonoTipoGrad" classNameBase="input" disabled="<%= String.valueOf(!canModifyCM) %>" onChange="checkAvviamentoCM()">	  	
							    	<option value=""  <% if ( "".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %> ></option>            
					            	<option value="D" <% if ( "D".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %>>Avviamento numerico art.8</option>
					            	<option value="A" <% if ( "A".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %>>Avviamento numerico art.18</option> 
					            	<option value="G" <% if ( "G".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %>>Graduatoria art.1</option>               					        		
					        	</af:comboBox>  
				            </td>	
				            <td class="etichetta2">n. posti</td>
				            <td class="campo2">
				              <af:textBox classNameBase="input" onKeyUp="fieldChanged();settaNumeroFigureProfCM();" 
				              	name="numPostiCM" size="5" value="<%= numPostiCM%>" 
				              	readonly="<%= String.valueOf(!canModifyCM) %>" maxlength="4" />
				            </td>					            				            				            			            
						</tr>
						<!--modifica Esposito-->
						<tr valign="top">				        	
							<td class="etichetta2"></td>
				            <td class="campo2"></td>	
				            <td class="etichetta2">Anno di riferimento<br>per il reddito</td>
				            <td class="campo2">
				              <af:textBox classNameBase="input" onKeyUp="fieldChanged();" 
				              	name="numAnnoRedditoCM" size="5" value="<%= numAnnoRedditoCM%>" 
				              	readonly="<%= String.valueOf(!canModifyCM) %>" maxlength="4" />
				            </td>					            				            				            			            
						</tr>  
						
						<tr valign="top">
							<td colspan="2">&nbsp;</td>						
							<td colspan="2" width="100%" align="right">
								<div id="CODTIPONUMERICO" style="display:<%=displayTipoNumerico%>" align="right">
									<table colspacing="0" cellspacing="0" border="0" width="100%" align="right">
								  		<tr>
								  			<td class="etichetta2">Data chiamata</td>
								      		<td class="campo2">
								        		<af:textBox classNameBase="input" title="Data chiamata CM" type="date" 
								        			validateOnPost="true" onKeyUp="fieldChanged();" 
								        			name="datChiamataCM" size="11" maxlength="10" value="<%= datChiamataCM%>"   
								        			readonly="<%= String.valueOf(!canModifyCM) %>" /> 
								        			<!--modifica Esposito--> 
								      		</td>			      		
								    	</tr>  
								   </table>
								</div>
							</td>
						</tr>
						<tr valign="top">
							<td colspan="2">&nbsp;</td>
							<td colspan="2" width="100%" align="right">
								<div id="CODTIPOART1" style="display:<%=displayTipoArt1%>" align="right">
									<table colspacing="0" cellspacing="0" border="0" width="100%" align="right">
								  		<tr>
								  			<td class="etichetta2">Tipo iscrizione art.1</td>
								      		<td class="campo2">
									        	<af:comboBox classNameBase="input" onChange="fieldChanged();" name="codTipoLista" 
									        		selectedValue="<%=codTipoLista%>" disabled="<%= String.valueOf(!(canModifyCM)) %>" moduleName="COMBO_LISTE_SPECIALI_CM" 
									        		addBlank="true" />
									      	</td>	    		      		
								    	</tr>  
								   </table>
								</div>
							</td>
						</tr>
						
					</table>
		    	</td>
			</tr>    
	    	<!--<tr><td colspan=4><br></td></tr>-->
	   	</table>
   <%
	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
	%>
   
   <table id="categoriaCM" width="100%" border="0">

     <tr><td width="20%"></td><td width="30%"></td><td width="20%"></td><td width="30%"></td></tr>
     <tr >
     	<td colspan="4" width="100%">
     		<table class='sezione' cellspacing=0 cellpadding=0>
				<tr>					
					<td  width=18>
                    	<img id='I_SEL_S_CATEGORIACM' src='<%=imgSelSezioneCategoriaCM%>' onclick='cambia(this, document.getElementById("T_S_CATEGORIACM"))'></td>					
					<td class="sezione_titolo">Categoria CM</td>							
				</tr>
			</table>
     	</td>
     </tr>
     <tr><td colspan="4">
		<TABLE id='T_S_CATEGORIACM' style='width:100%;display:<%=displaySCategoriaCM%>'>     
	    	<script>initSezioni( new Sezione(document.getElementById('T_S_CATEGORIACM'),
	    									 document.getElementById('I_SEL_S_CATEGORIACM'),
	    									 <%=legatoACategoriaCM%>) );
	    	</script>  
	          <tr valign="top">
	            <td class="etichetta2">Categoria CM</td>
	            <td class="campo2">
	              <af:comboBox classNameBase="input" 
	              				onChange="fieldChanged();" 
	              				name="codMonoCMcategoria" 
	              				title="Categoria CM"
	              				required="<%=String.valueOf(canModifyCatCM) %>"
	              				disabled="<%= String.valueOf(!canModifyCatCM) %>">
	              	<option value="" <% if ( "".equalsIgnoreCase(codmonoCMcategoria) ) { %>SELECTED="true"<% } %>></option>
	              	<option value="D" <% if ( "D".equalsIgnoreCase(codmonoCMcategoria) ) { %>SELECTED="true"<% } %> >Disabili</option> 
	              	<option value="A" <% if ( "A".equalsIgnoreCase(codmonoCMcategoria) ) { %>SELECTED="true"<% } %>>Categoria protetta ex. Art. 18</option> 
	              	<option value="E" <% if ( "E".equalsIgnoreCase(codmonoCMcategoria) ) { %>SELECTED="true"<% } %>>Entrambi</option> 
	              </af:comboBox>
	            </td>
	            <td class="etichetta2" colspan="2">&nbsp;</td>
	          </tr>
 			</table>         
      </td>             
      </tr>
   </table>
   
   <table class="main"  border=0>
    <tr ><td colspan="4" ><hr width="90%"/></td></tr>

    <tr valign="top">
      <td class="etichetta2">Centro per l'impiego</td>
      <td class="campo2" colspan="3">
        <af:comboBox classNameBase="input" onChange="fieldChanged();" 
        name="codCpi" selectedValue="<%=codCpi%>" 
        disabled="<%=String.valueOf( ( (!flagNuovo||cdnTipoGruppo==1) || (!(canManage))) )%>" 
        moduleName="M_ElencoCPI" addBlank="true" title="Centro impiego" required="true"/>
      </td>
    </tr>
    
    <tr valign="top">
      <td class="etichetta2">Note operatore</td>
      <td class="campo2" colspan="3">
        <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" 
        name="txtNoteOperatore" cols="50" value="<%=txtNoteOperatore%>" 
        readonly="<%= String.valueOf(!(canManage)) %>" maxlength="2000" />
      </td>
    </tr>
    <tr>
      <td class="etichetta">Operatore di riferimento</td>
      <td class="campo" colspan=3>
      
      <% if (reiteraRichAz || flag_insert) { %>
      		<af:comboBox name="prgSpi"
	                     size="1"
	                     title="Operatore di riferimento"
	                     multiple="false"
	                     required="false"
	                     focusOn="false"
	                     moduleName="COMBO_SPI_ONLY_VALID"
	                     addBlank="true"
	                     blankValue=""
	                     selectedValue = "<%=prgSpi%>"
	                     onChange="fieldChanged();"
	                     classNameBase="input"
	                     disabled="<%= String.valueOf(!(canManage)) %>"
	        />
      <% } else { %>
	      	<af:comboBox name="prgSpi"
	                     size="1"
	                     title="Operatore di riferimento"
	                     multiple="false"
	                     required="false"
	                     focusOn="false"
	                     moduleName="COMBO_SPI"
	                     addBlank="true"
	                     blankValue=""
	                     selectedValue = "<%=prgSpi%>"
	                     onChange="fieldChanged();"
	                     classNameBase="input"
	                     disabled="<%= String.valueOf(!(canManage)) %>"
	        />
      <% } %>
      </td>
    </tr>

  </table>
  <!-- <a href="AdapterHTTP?PAGE=IdoGestStatoRichiestaPage&prgRichiestaAZ=<%=prgRichiestaAz%>&cdnFunzione=<%=_funzione%>">Vai allo stato richiesta</a> -->
			<br/>

<input type="hidden" name="prgAzienda" value="<%= (prgAzienda == null) ? "" : prgAzienda%>" />
<input type="hidden" name="prgUnita" value="<%= (prgUnita==null) ? "" : prgUnita %>" />
<input type="hidden" name="cdnUtMod" value="<%= cdnUtMod %>"/>
<input type="hidden" name="numkloRichiestaAz" value="<%= numkloRichiestaAz %>"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
<input type="hidden" name="cdnGruppo" value="<%=user.getCdnGruppo()%>" />
<input type="hidden" name="checkNumAs" value="<%=checkNumAs%>" />
<input type="hidden" name="checkNumLsu" value="<%=checkNumLsu%>" />
<input type="hidden" name="checkNumMilitare" value="<%=checkNumMilitare%>" />
<input type="hidden" name="checkNumPostiCM" value="<%=checkNumPostiCM%>" />


<% if (!flag_insert) { %>
    <input type="hidden" name="PAGE" value="IdoTestataRichiestaPage"/>
    <%if(canManage && reiteraRichAz){%>
      <input class="pulsante" type="submit" name="salvaReiter" value="Salva reiterazione"/>
    <%} else { 
        if (canManage) { %>
           <input class="pulsante" type="submit" name="salva" value="Aggiorna"/>
        <%}%>
        <%if(canReitera && !eUnaCopia){%>
        	<input class="pulsante" type="submit" name="reiteraRichAz" value="Reitera richiesta"/><%}%>
        <%}%>
    <% //if (canDelete) { 
        if ((pagina_back.equals("IdoListaRichiestePage") || pagina_back.equalsIgnoreCase("IdoListaPubbPage"))
      		&& !eUnaCopia) { // CANCELLARE?.......%>
        <%--input class="pulsante" type="button" name="back" value="Torna alla pagina di ricerca" onclick="goTo('PAGE=IdoRichiestaRicercaPage&cdnFunzione=<%=_funzione%>')" /--%>      
        <!--<input class="pulsante" type="button" name="back" value="Torna alla pagina di ricerca" onclick="go('PAGE=IdoRichiestaRicercaPage&cdnFunzione=<%=_funzione%>','FALSE')" />-->
        <%-- Momentaneamente il pulsante di "Torna alla lista" è stato sostituito con "Torna alla pagina ricerca" in attesa che venga implementato 
             il meccanismo che permetta il ritorno alla alla lista riutilizzando i criteri di filtro
        <input class="pulsante" type="button" name="back" value="Torna alla lista" onclick="javascript:history.back();" />
        --%>
    <%  } else if (!eUnaCopia) {%>
        <input class="pulsante" type="button" name="back" value="Torna alla pagina di ricerca" onclick="goTo('PAGE=IdoRichiestaRicercaPage&cdnFunzione=<%=_funzione%>')" />
      <%}%>         
    <%  if (pagina_back.equals("GestIncrocioPage") && eUnaCopia) {%>
        <%String prgOrig = prgRichiestaAz;%>
        <input class="pulsante" type="button" name="PulsanteIncrocioBack" value="Torna alla Gestione Incrocio"
            onclick="goTo('PAGE=GestIncrocioPage&cdnFunzione=<%=_funzione%>&prgAzienda=<%=prgAzienda%>&prgRichiestaAZ=<%=prgRichiestaAz%>&prgUnita=<%=prgUnita%>&PRGORIG=<%=prgOrig%>&flgCopia=0&flgGestCopia=1')" />        
    <%  } else if (eUnaCopia) {%>
        <%String prgOrig = prgRichiestaAz;%>
        <input class="pulsante" type="button" name="PulsanteIncrocioBack" value="Torna alla Gestione Incrocio"
            onclick="goTo('PAGE=GestIncrocioPage&cdnFunzione=<%=_funzione%>&prgAzienda=<%=prgAzienda%>&prgRichiestaAZ=<%=prgRichiestaAz%>&prgUnita=<%=prgUnita%>&PRGORIG=<%=prgOrig%>&flgCopia=0&flgGestCopia=1')" />
    <%  } %>
  
<%} else { // (!flag_insert) siamo in inserimento %>
	<%if (canManage) {%>
      <input class="pulsante" type="button" name="inserisci" value="Inserisci" onclick="verificaDati();"/>

    <%}%>
      <input type="hidden" name="PAGE" value="IdoTestataRichiestaPage"/>
      <input type="hidden" name="ret" value="IdoTestataAziendaPage"/>
      <input type="hidden" name="prgRichiestaAZ" value="<%= prgRichiestaAz %>"/>
      <input type="hidden" name="inserisciRichiesta" value="Inserisci"/>
<%}//end if%>

 <%if((canIncrocio && !eUnaCopia && !reiteraRichAz && !flag_insert && flgIncrocio.equals("S")) 
       || gestioneRosaNominativa){
    String prgOrig = prgRichiestaAz;
	String parIncrocioStoricizza = "PAGE=GestIncrocioPage&cdnFunzione="+_funzione+"&prgAzienda="+prgAzienda+"&prgRichiestaAZ="
		+prgRichiestaAz+"&prgUnita="+prgUnita+"&PRGORIG="+prgOrig;
	if ( flgIncrocio.equals("S")) parIncrocioStoricizza +="&StoricizzaRichiesta=true"; 
%>
      <input class="pulsante" type="button" name="PulsanteIncrocio" value="Gestione Incrocio"
            onclick="goTo('<%=parIncrocioStoricizza%>')" />      
   <%}%>

<%
if((canGraduatoria && !eUnaCopia && !reiteraRichAz && !flag_insert) 
       || gestioneRosaNominativa){
	if(("S").equalsIgnoreCase(flgGraduatoria)){
		
		String parGraduatoria = "";
		
		//	INIT-PARTE-TEMP
		if (Sottosistema.CM.isOff()) {	
		// END-PARTE-TEMP
		 
			parGraduatoria = "PAGE=ASGestGraduatoriePage&cdnFunzione="+_funzione+"&prgAzienda="+prgAzienda+"&prgRichiestaAZ="
							+prgRichiestaAz+"&prgUnita="+prgUnita+"&prgOrig="+prgRichiestaAz;
		
		// INIT-PARTE-TEMP
		} else {
		// END-PARTE-TEMP
			
			if (flgStatoEvasioneAS) {
				parGraduatoria = "PAGE=ASGestGraduatoriePage&cdnFunzione="+_funzione+"&prgAzienda="+prgAzienda+"&prgRichiestaAZ="
								+prgRichiestaAz+"&prgUnita="+prgUnita+"&prgOrig="+prgRichiestaAz;
			}
			else {
				parGraduatoria = "PAGE=CMGestGraduatoriePage&cdnFunzione="+_funzione+"&prgAzienda="+prgAzienda+"&prgRichiestaAZ="
								+prgRichiestaAz+"&prgUnita="+prgUnita+"&prgOrig="+prgRichiestaAz;								
			}
		// INIT-PARTE-TEMP
		} 
		// END-PARTE-TEMP
%>
	<input class="pulsante" type="button" name="PulsanteGraduatoria" value="Graduatorie"
            onclick="goTo('<%=parGraduatoria%>')" />   
<%
	}
}
%>
 
</af:form>
<%out.print(htmlStreamBottom);%>
<br/>
<p align="center">
<% if (!flag_insert && !reiteraRichAz) operatoreInfo.showHTML(out);%>
</p>
<br/>

</body>
</html>