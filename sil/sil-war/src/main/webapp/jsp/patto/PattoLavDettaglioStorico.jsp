<%@page import="it.eng.sil.util.amministrazione.impatti.PattoBean"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.PageAttribs,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,                   
                  it.eng.sil.util.*,
                  it.eng.sil.module.patto.bean.*,
                  it.eng.sil.module.movimenti.constant.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  java.lang.*,
                  java.text.*,
                  
                  it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil"   %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	boolean hasImpAssociati = false;
	boolean isProtocollato = false;
	String msgNoProtAccordo = "Non è possibile stampare un accordo generico se il lavoratore si trova in 150.";
	Patto p = null;				
	SourceBean pattorow=(SourceBean) serviceResponse.getAttribute("M_PattoDettaglioStorico.ROWS.ROW");
	p = new Patto(pattorow);
//	String cdnLavoratore= (String )serviceRequest.getAttribute("cdnLavoratore");
	String cdnLavoratore = p.getCdnLavoratore();
	
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  	PageAttribs attributi = new PageAttribs(user, _page);
    // Savino 05/10/05: l'attributo stampa non viene piu' gestito
	//boolean canPrint =false;
	boolean infStorButt=false;
	boolean canInsert = false;
	boolean canModify = false;
	
	boolean canEdit=false;
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	
	boolean canRiapriPatto = attributi.containsButton("RIAPRI_PATTO");
    String labelServizio = "Servizio";
    String umbriaGestAz = "0";
    if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
    	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
    }
    if(umbriaGestAz.equalsIgnoreCase("1")){
    	labelServizio = "Area";
    }
%>

<%  

    String     CODSTATOATTO         = null;
    
    boolean    flag_insert   = false;
    boolean    buttonAnnulla = false;
    String display1 = "none";
    String img1 = "../../img/chiuso.gif";
    String hasDataUscita = "false";
    // se c'e' la did valida allora ...
    // la condizione e': codStatoAttoDid!=null && codStatoAttoDid.equals("PR");
    boolean noAccordoGen = false;
    String dataInizioSoAperto = "";
    // la data iscrizione all'elenco anagrafico puo' essere quella legata alla did legata al patto oppure
    // quella corrente. Se non c'e' la did non si puo' fare il patto, a meno che il lavoratore non sia in 
    // mobilita'. In questo caso il record del patto non sara' associato al record della did, quindi
    // bisogna utilizzare la data di iscrizione all'elenco anagracico corrente.
    String dataInizioElAnagCorrente="";
    ////////////////////////////////////
    int cdnFunzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    String prgPattoLavoratore = (String) serviceRequest.getAttribute("prgPattoLavoratore");
    //String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
    String queryString = "PAGE=PattoLavDettaglioPage&cdnLavoratore="+cdnLavoratore+"&CDNFUNZIONE="+cdnFunzione;
    boolean obiettiviDelPattoModify = true;
    InfCorrentiLav testata= null;
    Linguette l = null;
    Testata operatoreInfo=null;
	//cdnLavoratoreBD = new BigDecimal(cdnLavoratore);
  	String dataRiferimentoIndice = "";
    String indiceSvantaggio = "";
    String indiceSvantaggio2 = "";
    String readOnlyStringDataRif = "false";
    String readOnlyStringIndice = "false";
    String dataStipulaOrig = "";
    String dataStipulaPattoCheck = "";
    String statoOccRaggruppamento = "";
    String descrizioneStatoOccAccordo = "";
    int size = 11, max = 65;
    String codVCHProfiling = "", decDoteProcessoAssegnato = "", decDoteProcessoResidua = "", decDoteRisultatoAssegnato = "", decDoteRisultatoResidua = "";
    String numConfigVoucher = serviceResponse.containsAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM")?
  			serviceResponse.getAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM").toString():it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;
  	
	//Borriello - settembre 2017 - Dati Ente Accreditato
	String codSedeSoggAcc = "";
	String codFiscaleSoggAcc = "";
	String ragioneSocialeSoggAcc = "";
	String noteAppuntamentoSoggAcc = "";
	String indirizzoSoggAcc = "";
	String comuneSoggAcc = "";
	String provinciaSoggAcc = "";
	// fine borriello		
  			
    String operazioneRiapertura = "false";
	if (serviceResponse.getAttribute("M_RIAPRI_PATTO.ESITORIAPERTURA") != null && 
		serviceResponse.getAttribute("M_RIAPRI_PATTO.ESITORIAPERTURA").toString().equalsIgnoreCase("OK")){
		operazioneRiapertura = "true";	
	}
  	String prgLavoratoreProfilo = "";
  	//campi assegno di ricollocazione
  	String decImportoAr ="",  datNaspi = "", noteElemAttivazione="";
  	String dataRiferimentoIndice150 = "";
    String indiceSvantaggio150 = "";
    String strProfiling150 = "";
    SourceBean row = null;
    // Savino 14/10/2005: prendo le informazioni correnti e le utilizzo quando necessario
    Vector rowInfoCorrenti  = serviceResponse.getAttributeAsVector("M_GETINFCORRPATTO.ROWS.ROW");
    InfoCorrenti infoCorrenti = null;
    if(rowInfoCorrenti != null && !rowInfoCorrenti.isEmpty()) {  
        row  = (SourceBean) rowInfoCorrenti.elementAt(0);
        infoCorrenti = new InfoCorrenti(row);
    }
    else infoCorrenti = new InfoCorrenti();
 
    row = null;
    //se abbiamo nella servicerequest il parametro "inserisci" è stato richiesto l'inserimento di un nuovo patto ("Inserisci nuovo")
    // Questo significa che la pagina deve essere vuota e si debbono inserire ex novo i dati. Il patto esistente verra' chiuso da un trigger.
    /* TODO cancellato dallo storico
    if (serviceRequest.containsAttribute("inserisci") && (!serviceRequest.containsAttribute("DUPLICA") || !serviceRequest.getAttribute("DUPLICA").equals("DUPLICA"))) { 
        flag_insert   = true;
        buttonAnnulla = true;
        p = new Patto();
%>
        <!--SEZIONE PER IL CALCOLO DELLA DATA DI SCADENZA-->
        <%-- @ include file="aggiungiGiorni.inc" --%>         
<%        
        SourceBean numGg = (SourceBean)serviceResponse.getAttribute("M_GetNumGG.ROWS.ROW");
        p.setInfoScadenzaPatto(numGg);
%>              
<%
        //Recupero informazioni per l'accordo generico
        
        // Savino 14/10/2005: prendo le info generali per il patto297, tanto sono le stesse anche se 
        //					  ci alcuni campi non vengono utilizzati
        //	nascondo il codice
    }// fine ("inserisci")
    else 
    */
    //{ 
    	//Controllo se vi sono impegni legati al patto. Se non ci sono la stampa non deve essere possibile
		Vector impAssPatto = serviceResponse.getAttributeAsVector("M_GetImpegniLegatiPatto.ROWS.ROW");
		if(impAssPatto!=null && impAssPatto.size()>0){
			SourceBean sbImp = (SourceBean)impAssPatto.get(impAssPatto.size()-1);
			if(sbImp!=null && !sbImp.getAttribute("PRGLAVPATTOSCELTA").equals("")){
				hasImpAssociati = true;
			}
		}		
        //row=(SourceBean) serviceResponse.getAttribute("M_PattoDettaglioStorico.ROWS.ROW");
        //if(row != null){
        //	 p = new Patto(row);
        	/****************/
        //      cdnLavoratore        =  p.getCdnLavoratore();
			 CODSTATOATTO = p.getCodStatoAtto();	
             //
             obiettiviDelPattoModify = flag_insert || !CODSTATOATTO.equals("PR");
              
             dataRiferimentoIndice = p.getDataRiferimento();
             if (p.getIndiceSvantaggio() != null) {
                 indiceSvantaggio = p.getIndiceSvantaggio().toBigInteger().toString();
             } else {
                 indiceSvantaggio = "";  
             }
             if (p.getIndiceSvantaggio2() != null) {
                 indiceSvantaggio2 = p.getIndiceSvantaggio2().toBigInteger().toString();
             } else {
                 indiceSvantaggio2 = "";  
             }
             
             dataStipulaPattoCheck = p.getDatStipula();
             dataStipulaOrig = p.getDatStipulaOrig();
             
             if (!indiceSvantaggio.equals("") || !indiceSvantaggio2.equals("")) {
           	  readOnlyStringIndice = "true";  
             }
             if (!dataRiferimentoIndice.equals("")) {
           	  readOnlyStringDataRif = "true";  
             }
             if (p.getDecDoteProcessoAssegnato() != null) {
           	  decDoteProcessoAssegnato = p.getDecDoteProcessoAssegnato().toString(); 
             }
             if (p.getDecDoteProcessoResidua() != null) {
           	  decDoteProcessoResidua = p.getDecDoteProcessoResidua().toString(); 
             }
             if (p.getDecDoteRisultatoAssegnato() != null) {
           	  decDoteRisultatoAssegnato = p.getDecDoteRisultatoAssegnato().toString(); 
             }
             if (p.getDecDoteRisultatoResidua() != null) {
           	  decDoteRisultatoResidua = p.getDecDoteRisultatoResidua().toString(); 
             }
             codVCHProfiling = p.getCodVchProfiling();
             if (p.getPrgLavoratoreProfilo() != null) {
           	  prgLavoratoreProfilo = p.getPrgLavoratoreProfilo().toString();
           	  prgLavoratoreProfilo = prgLavoratoreProfilo + "--" + codVCHProfiling;
             }
             
             if (CODSTATOATTO.equalsIgnoreCase("PR")) {
            	 isProtocollato = true;
	              if (serviceResponse.containsAttribute("M_GetInfoStatoOccPerAccGen.ROWS.ROW")) {
	          	  	Vector rowsStatoOccAccGen  = serviceResponse.getAttributeAsVector("M_GetInfoStatoOccPerAccGen.ROWS.ROW");
	          	    if (rowsStatoOccAccGen != null && !rowsStatoOccAccGen.isEmpty()) {
	          	    	SourceBean rowStatoOccAccordo  = (SourceBean) rowsStatoOccAccGen.elementAt(0);
	          	   		descrizioneStatoOccAccordo = StringUtils.getAttributeStrNotNull(rowStatoOccAccordo, "DescrizioneStato");
	          	   		if (!descrizioneStatoOccAccordo.equals("")) {
		        			int sizeDesc = descrizioneStatoOccAccordo.length() + 3;
		        			if (sizeDesc > max) {
		        				descrizioneStatoOccAccordo = descrizioneStatoOccAccordo.substring(0, max) + "...";
		        			}
		            	}
	              	}
	              }
             }
           //campi assegno ricollocazione
             if (p.getImportoAr() != null) {
           	  decImportoAr = p.getImportoAr().toString();
             }
             if (p.getDatNaspi() != null) {
           	  datNaspi = p.getDatNaspi();
             }
              if (p.getNoteElemAttivazione() != null) {
           	  noteElemAttivazione = p.getNoteElemAttivazione();
             }
              if (p.getDataRiferimento150() != null) {
            	  dataRiferimentoIndice150 = p.getDataRiferimento150();
              }
              if (p.getIndiceSvantaggio150() != null) {
            	  indiceSvantaggio150 = p.getIndiceSvantaggio150();
              }
              if (p.getStrProfiling() != null) {  
            	  strProfiling150 = p.getStrProfiling();
              } 
            //campi soggetto accreditato (ente)
            codSedeSoggAcc = p.getCodiceSedeEnte();
            codFiscaleSoggAcc = p.getCodiceFiscaleEnte();
            ragioneSocialeSoggAcc = p.getRagioneSocialeEnte();
            noteAppuntamentoSoggAcc = p.getNoteEnte();
            indirizzoSoggAcc = p.getIndirizzoEnte();
            comuneSoggAcc = p.getComuneEnte();
            provinciaSoggAcc = p.getSiglaProvinciaEnte();
            if(StringUtils.isFilledNoBlank(provinciaSoggAcc)){
          	  comuneSoggAcc = comuneSoggAcc + " (" + provinciaSoggAcc +")";
            }
            
        //}
        
        //if(row != null)     
        //else { 
        /****************/
        //p = new Patto();
        //altrimenti il record è vuoto, non ci sono inf. correnti: possiamo solo inserire
        //      flag_insert = true;        
%>
              <!--SEZIONE PER IL CALCOLO DELLA DATA DI SCADENZA-->
              <%--@ include file="aggiungiGiorni.inc" --%>
<%              
       //       SourceBean numGg = (SourceBean)serviceResponse.getAttribute("M_GetNumGG.ROWS.ROW");
       //       p.setInfoScadenzaPatto(numGg);
%>
              
<%
              //Recupero informazioni per l'accordo generico
              
              // Savino 14/10/2005: prendo le info generali del 150. Vedi commento precedente
              
        //}
        
    //} //else
    //p.setInfoCorrenti(infoCorrenti);
    /////////////////////  protocollazione     /////////////////////////////////
    Vector rows= serviceResponse.getAttributeAsVector("InfoProtocolloPattoStoricizzato.ROWS.ROW");
    String numProt  = "";
    String annoProt = "";
    String dataProt = "";
    String oraProt  = "";
    String docInOrOut = "";
    String docRif     = "";
    if(rows != null && !rows.isEmpty())    { 
        row = (SourceBean) rows.elementAt(0);
        numProt  = SourceBeanUtils.getAttrStrNotNull(row,"NUMPROTOCOLLO");
        BigDecimal annoP = (BigDecimal) row.getAttribute("ANNOPROT");
        annoProt = annoP!=null ? annoP.toString() : "";
        dataProt = StringUtils.getAttributeStrNotNull(row,"datprot");
        oraProt  = StringUtils.getAttributeStrNotNull(row,"oraProt");
        docInOrOut  = StringUtils.getAttributeStrNotNull(row,"CODMONOIO");
        docRif  = StringUtils.getAttributeStrNotNull(row,"STRDESCRIZIONE");
    }
    // Savino 11/10/05: comunque bisogna riprendere la data di iscrizione all'elenco anagrafico corrente
    Vector rowCpI  = serviceResponse.getAttributeAsVector("M_GETINFCORRPATTO.ROWS.ROW");
	if(rowCpI != null && !rowCpI.isEmpty()) {
    	row  = (SourceBean) rowCpI.elementAt(0);
        dataInizioElAnagCorrente = (String)row.getAttribute("DATINIZIO");
    }
    ///////////////////////////////////////////////////////////////////////////////////
    operatoreInfo= new Testata(p.getCdnUtIns(), p.getDtmIns(), p.getCdnUtMod(), p.getDtmMod());
    testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    testata.setPaginaLista("PattoInformazioniStorichePage");

    // TODO Savino: le linguette bisogna cambiarle
    l = new Linguette(user, cdnFunzione, _page,new BigDecimal(prgPattoLavoratore));     
    
    l.setCodiceItem("prgPattoLavoratore");
    String readOnlyString = String.valueOf(!canModify);
    if (!canModify) {
    	readOnlyStringIndice = "true";
    	readOnlyStringDataRif = "true";
    }
    obiettiviDelPattoModify = canModify ? obiettiviDelPattoModify:false; 
    
    //Se si tratta di un inserimento e il lavoratore ha già la DID mostro solo la tipologia patto 150
    //e la seleziono di default
    boolean soloPatto297 = false;
    if (!p.getDatDichiarazione().equals("") && p.getCodStatoAttoDid().equals("PR")) {
    // Savino 11/10/2005: non so perche' viene fatta questa scelta. Se si deve caricare un accordo generico viene
    //				visto come patto 150.
    // commentato
    // Ho capito:)) se siamo in fase di inserimento, si vuole impedire che si possa scegliere l'accordo
    	soloPatto297 = true;
    }
    String dataUltimoProtocolloModificato = (String)serviceResponse.getAttribute("M_PattoProtocollatoModificato.rows.row.datUltimoProtocollo");
    //Controlla presenza storico
    boolean storicoPatto = serviceResponse.containsAttribute("M_PATTISTORICIZZATI.ROWS.ROW");
    
    Vector rowCodificaPatto  = new Vector();
    SourceBean row_CurrPatto = null;
    if (serviceResponse.containsAttribute("M_GetCodificaTipoPatto.ROWS.ROW")) {
    	rowCodificaPatto  = serviceResponse.getAttributeAsVector("M_GetCodificaTipoPatto.ROWS.ROW");	
    }
  	//Recupero informazioni per l'accordo generico
  	if (serviceResponse.containsAttribute("M_GetInfoPerAccGen.ROWS.ROW")) {
  		Vector rowsAccGen  = serviceResponse.getAttributeAsVector("M_GetInfoPerAccGen.ROWS.ROW");
  	    if(rowsAccGen != null && !rowsAccGen.isEmpty()) {
  	    	SourceBean rowAccordo  = (SourceBean) rowsAccGen.elementAt(0);
  	    	statoOccRaggruppamento = StringUtils.getAttributeStrNotNull(rowAccordo,"codStatoOccupazRagg");
  	    	if (statoOccRaggruppamento.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.RAGG_DISOCCUPATO) || 
  	    		statoOccRaggruppamento.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.RAGG_INOCCUPATO)) {
  	    		noAccordoGen = true;
  	    		dataInizioSoAperto = StringUtils.getAttributeStrNotNull(rowAccordo,"datainizioso");
  	    	}
  	    }	
  	}
  	
  	//Borriello maggio 2020 Patto Online
	String codiceAbilitazioneServizi = null;
	String dataInvioAccettazioneServizi = null;
	String dataAccettazioneServizi = null;
	String statoPattoOnLine = null;
	String tipoAccettazionePattoOnLine = null;
	String dataUltimaStampa = null;
	String flgPattoOnLine = null;
    boolean isPattoOnLine = false;
    boolean storicoPattoOnLine = false;
    boolean canStoricoPtOnLine = false;
    
    storicoPattoOnLine = serviceResponse.containsAttribute("M_PattiChiusiOnLineStoricizzati.ROWS.ROW");
    flgPattoOnLine = p.getFlagPattoOnLine();
    if (flgPattoOnLine == null) flgPattoOnLine = "";
    canStoricoPtOnLine	= attributi.containsButton("STORICO_PTONLINE");
    if(serviceResponse.containsAttribute("M_Config_PattoOnLine")){
    	String configPattoOnLine = serviceResponse.containsAttribute("M_Config_PattoOnLine.ROWS.ROW.STRVALORECONFIG")?
    		  	serviceResponse.getAttribute("M_Config_PattoOnLine.ROWS.ROW.STRVALORECONFIG").toString():it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;
      	if(configPattoOnLine.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.CUSTOM_CONFIG) || flgPattoOnLine.equalsIgnoreCase("S")){
      		isPattoOnLine = true;
      		codiceAbilitazioneServizi = p.getCodiceAbilitazioneServizi();
            dataInvioAccettazioneServizi = p.getDataInvioAccettazioneServizi();
            dataAccettazioneServizi = p.getDataAccettazioneServizi();
            statoPattoOnLine = p.getStatoPattoOnLine();
            tipoAccettazionePattoOnLine = p.getTipoAccettazionePattoOnLine();
            dataUltimaStampa = p.getDataUltimaStampa();
       	}
    }
   //fine patto on line 
%>

<%
String msgConfermaRiapertura = "";
if (serviceResponse.getAttribute("M_RIAPRI_PATTO.CONFERMARIAPERTURA") != null) {
	msgConfermaRiapertura = serviceResponse.getAttribute("M_RIAPRI_PATTO.CONFERMARIAPERTURA").toString();
}
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
// POPUP EVIDENZE
String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
int _fun = 1;
if((cdnFunzione>0) ) { _fun = cdnFunzione; }
EvidenzePopUp jsEvid = null;
boolean bevid = attributi.containsButton("EVIDENZE");
if(strApriEv.equals("1") && bevid) {
	jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
}
Vector mobilita = new Vector();
Vector statiOccupazionali = new Vector();
String raggStatoOccAperto = "";
if (Sottosistema.MO.isOn()) {
	// E' possibile stipulare un patto anche non avendo la did. 
	//Ma bisogna che il lavoratore abbia periodi di mobilità , sia in 150 alla data
	//stipula del patto e sia in 150 allo stato attuale(stato occupazionale aperto)
	mobilita = serviceResponse.getAttributeAsVector("M_IscrizioniMobilita.rows.row");
	statiOccupazionali =  serviceResponse.getAttributeAsVector("M_Stati_Occupazionali_Intervalli.rows.row");
	for (int j=0;j<statiOccupazionali.size();j++) {
		SourceBean sOcc = (SourceBean)statiOccupazionali.get(j);
		if (sOcc.getAttribute("datFine") == null) {
			raggStatoOccAperto = sOcc.getAttribute("CODSTATOOCCUPAZRAGG").toString();
			break;
		} 
	}
}
%>
<html>
<head>
<title>Dettaglio patto/accordo lavoratore</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<af:linkScript path="../../js/"/>
<script language="Javascript" src="../../js/docAssocia.js"></script>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<%@ include file="PattoSezioniATendina.inc" %>

<%if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>

<SCRIPT language="JavaScript">
<%@ include file="_controlloDate_script.inc"%>
<%@ include file="_sezioneDinamica_script.inc"%>
<!--
var operazioneRiapertura = "<%=operazioneRiapertura%>";
var msgConfermaRiapertura = "<%=msgConfermaRiapertura%>";
var motivoChiusuraAccAutomatica = "<%=it.eng.sil.module.movimenti.constant.Properties.CHIUSURA_AUTOMATICA_ACCORDO%>";
var statoOccRagg = "<%=statoOccRaggruppamento%>";
var arrayCodificaPatto = new Array();
var arrayFlgPatto297 = new Array();
<%for(int indexPatto=0; indexPatto < rowCodificaPatto.size(); indexPatto++)  { 
	row_CurrPatto = (SourceBean) rowCodificaPatto.elementAt(indexPatto);
    out.print("arrayCodificaPatto["+indexPatto+"]=\""+ row_CurrPatto.getAttribute("CODICE").toString()+"\";\n");
    out.print("arrayFlgPatto297["+indexPatto+"]=\""+ row_CurrPatto.getAttribute("FLGPATTO297").toString()+"\";\n");
}
%>

function sceglipage(Scelta){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (Scelta == 1) {
    // torna indietro senza inserire   
        var urlpage="AdapterHTTP?";
        urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
        urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
        urlpage+="PAGE=PattoLavDettaglioPage";
        setWindowLocation(urlpage);
    }       
}

function apriInfoStoriche(cdnlav) {
    uripopup="AdapterHTTP?PAGE=PattoInformazioniStorichePage&cdnLavoratore="+cdnlav;
    window.open (uripopup, "InformazioniStoriche", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
}

function mostra(id){
    var div = document.getElementById(id);
    div.style.display="";
    div.style.visibility="visible";
}

function nascondi(id){
    var div = document.getElementById(id);
    div.style.display="none";
    div.style.visibility="hidden";
}

// by Davide
function showIfsetVar() {
	var valoreSel = document.Frm1.flgPatto297.value;
    //se patto 150                          
    if(valoreSel != "" && valoreSel == "S")  { 
        selezionePatto297();    
    }//se accordo                                                                
    else if(valoreSel != "" && valoreSel == "N")  {  
        selezioneAccordoGenerico();   
   	}   
    else { 
    	selezioneVuota();
    }    
    return true;
}//showIfsetVar()

function selezioneAccordoGenerico() {
    nascondi("infoPatto1");                                                      
    mostra  ("infoPatto2");                                                      
    nascondi("infoPatto3");                                                      
    mostra  ("infoPatto4");                                                      
    nascondi("labelVisulizza");                                                  
    mostra  ("labelNascondi");                                                   
    nascondi("titoloIni");                                                       
    nascondi("titolo297");                                                       
    mostra  ("titoloAcc");
    document.Frm1.datInizio.validateOnPost = "false";                      
    document.Frm1.datDichDisponibilita.validateOnPost = "false";           
    document.Frm1.PRGDICHDISPONIBILITA.value = "";                             
}
function selezioneVuota() {
    nascondi("infoPatto1");                                                      
    nascondi("infoPatto2");                                                      
    nascondi("infoPatto3");                                                      
    nascondi("infoPatto4");                                                      
    nascondi("labelVisulizza");                                                  
    nascondi("labelNascondi");                                                   
    mostra  ("titoloIni");                                                       
    nascondi("titolo297");                                                       
    nascondi("titoloAcc");
}
function selezionePatto297() {
    mostra  ("infoPatto1");                                                        
    mostra  ("infoPatto2");                                                        
    mostra  ("infoPatto3");                                                        
    mostra  ("infoPatto4");                                                        
    mostra  ("labelVisulizza");                                                    
    nascondi("labelNascondi");                                                   
    nascondi("titoloIni");                                                       
    mostra  ("titolo297");                                                         
    nascondi("titoloAcc");                                                       
    document.Frm1.datInizio.validateOnPost = "true";                       
    document.Frm1.datDichDisponibilita.validateOnPost = "true";            
    document.Frm1.PRGDICHDISPONIBILITA.value = document.Frm1.PRGDICHDISP.value;
}

function toDichDisp() {
    var urlpage="AdapterHTTP?";
    urlpage+="fromPattoDettaglio=1&";
    urlpage+="cdnFunzione=<%=cdnFunzione%>&";
    urlpage+="cdnLavoratore=<%=cdnLavoratore%>&"; 
    urlpage+="pageChiamante=PattoLavDettaglioPage&";
    urlpage+="page=DispoDettaglioPage&";
    // e' il nome del frame che deve essere refreshato nel caso si inserisca un nuovo documento
    urlpage+="FRAME_NAME=DichiarazioneDiponibilita";
	window.open(urlpage,"DichiarazioneDiponibilita", 'toolbar=0, scrollbars=1, resizable=1,height=600,width=800'); 
}
function underConstr() { alert("Funzionaliltà non ancora attivata."); }

//funzione che controlla se un campo è stato modificato o meno
var flagChanged = false;
var siStipulaPatto297 = <%=noAccordoGen%>;
var dataInizioSoAperto = "<%=dataInizioSoAperto%>";

function fieldChanged() {
 <% if (!readOnlyString.equalsIgnoreCase("true")){ %> 
    flagChanged = true;
 <%}%> 
}
 
//Impossibile stampare un patto non protocollato se non sono stati associati degli impegni
function controllaStampaPatto(){ 
	var ok = true;
	if ( (document.Frm1.flgPatto297.value == 'N') && (statoOccRagg == 'D' || statoOccRagg == 'I') ) {
		alert("<%=msgNoProtAccordo%>");
		ok = false;
	}

	if (ok) {
		<% if(!hasImpAssociati){ %>
			alert('Non è possibile stampare un patto/accordo se non ha impegni associati.');
			ok = false;
		<% } %>
	}
	
	if (ok) {
	<%-- Savino 23/09/05: dato che si puo' stampare solo se il patto e' PP il valore inviato sara' sempre lo stesso.
	     Sara' poi l'action a cambiarne il valore nel caso si sia scelto di protocollare --%>
		codStatoAtto = document.Frm1.CODSTATOATTO.value;
	<%-- Savino 10/10/2005: finora veniva gestito solo il tipo documento 150 --%>
		if (document.Frm1.flgPatto297.value == "S") {
			tipoDoc = "PT297";
			apriGestioneDoc('RPT_PATTO','&cdnLavoratore=<%=cdnLavoratore%>&pagina=<%=_page%>&strChiaveTabella=<%=p.getPrgPattoLavoratore()%>&codStatoAtto='+codStatoAtto,tipoDoc);
		} 
		else {
			tipoDoc = "ACLA";
			apriGestioneDoc('RPT_ACCORDO_GENERICO','&cdnLavoratore=<%=cdnLavoratore%>&pagina=<%=_page%>&strChiaveTabella=<%=p.getPrgPattoLavoratore()%>&codStatoAtto='+codStatoAtto,tipoDoc);
		}
	}
}

function settaFlag297() {
	var codificaTipologiaPatto = document.Frm1.codCodificaPatto.value;
	var indiceP = 0;
	for (indiceP=0; indiceP<arrayCodificaPatto.length ;indiceP++) {
		if (arrayCodificaPatto[indiceP] == codificaTipologiaPatto) {
			flagPatto297 = arrayFlgPatto297[indiceP];
			document.Frm1.flgPatto297.value = flagPatto297;
		}
	}
	return true;
}

/**
* Controllo della presenza della dichiarazione di immediata disponibilita'
* Se non e' stata firmata la dichiarazione di immediata disponibilita' il patto non puo' essere stipulato
*/
function hasDichiarazioneDisponibilita() {
	var codStatoAttoDid = "<%=p.getCodStatoAttoDid() %>";
	flagPatto297 = document.Frm1.flgPatto297.value;
	var dataStipula = document.Frm1.DATSTIPULA.value;
	 
    <%-- Savino 27/09/05 --%>
    //if ((document.Frm1.PRGDICHDISPONIBILITA.value=="" || codStatoAttoDid=="PA") && flagPatto297.value=="S" ) {
    <%-- Savino 10/10/05: il 150 e' ora possibile anche se non si ha la did ma si è in mobilita'. Deve pero' sempre esserci la iscrizione all'elenco anagrafico --%>
    
    if ( (document.Frm1.PRGDICHDISPONIBILITA.value=="" ||  codStatoAttoDid!="PR" ) && flagPatto297=="S" && !iscrittoMobilita() &&
    	 (!siStipulaPatto297 || compDate(dataStipula, dataInizioSoAperto)<0) ) {
    // e' stato scelto un patto 150 ma il lavoratore non ha fatto la dichiarazione di immediata disponibilita'
        if (confirm("La dichiarazione di immediata disponibilità non e' stata rilasciata o è incompleta: proseguire con la dichiarazione?")) {
            document.Frm1.codCodificaPatto.options.selectedIndex=0;
            document.Frm1.flgPatto297.value="";
            selezioneVuota();
            toDichDisp();
        }
        else {        
            // si resetta la combo
            document.Frm1.codCodificaPatto.options.selectedIndex=0;
            document.Frm1.flgPatto297.value="";
            selezioneVuota();
        }
    }
    else {
    	<%-- Savino 11/10/05: controlliamo la presenza dell'iscrizione all'elenco anagrafico --%>
        if (document.Frm1.datInizio.value=="" && (iscrittoMobilita() || siStipulaPatto297)) {
        	alert("Il lavoratore non risulta iscritto nell'elenco anagrafico.");
        	document.Frm1.codCodificaPatto.options.selectedIndex=0;
        	document.Frm1.flgPatto297.value="";
        	selezioneVuota();
        	return false;
        }
    }
}



<%-- Savino 13/10/2005 carico in un vettore le date inizio mobilita' e le date di tutti gli stati occupazionali.
		si puo' stipulare il patto se il lavoratore, nel periodo della data stipula, era in mobilita' ed era 
		disoccupato o inoccupato.
--%>
function iscrittoMobilita() {
	var mobilita= new Array();
	var statoOccupazionale = new Array();
	var raggStatoOccAperto = "<%=raggStatoOccAperto%>";
	if (raggStatoOccAperto != 'D' && raggStatoOccAperto != 'I') 
		return false;
	<%for (int i = 0;i<mobilita.size();i++){
		out.println("\tmobilita["+i+"]= new Object();");
		out.println("\tmobilita["+i+"].datInizio='"+((SourceBean)mobilita.get(i)).getAttribute("datInizio")+"'");
	}
	int j=0;
	for (int i = 0;i<statiOccupazionali.size();i++){
		SourceBean stOcc = (SourceBean)statiOccupazionali.get(i);
		if (!(stOcc.getAttribute("codStatoOccupazRagg").equals("D") || 
			stOcc.getAttribute("codStatoOccupazRagg").equals("I") )	)	
			continue;
		// se D o I allora si carica il vettore js
		out.println("\tstatoOccupazionale["+j+"]= new Object();");
		out.println("\tstatoOccupazionale["+j+"].datInizio='"+stOcc.getAttribute("datInizio")+"'");
		out.println("\tstatoOccupazionale["+j+"].datFine='"+Utils.notNull(stOcc.getAttribute("datFine"))+"'");
		j++;
	}	
	// a questo punto si controlla che la data stipula rientri nel range: 
	// dataStipulaPatto>=dataInizioMob[i] && dataInizioDisoc[j] <= dataStipulaPatto<= dataFineDisoc[j] 
	%>	
	if (!validateDate('DATSTIPULA')) {		
		return false;
	}
	var dataStipula = document.Frm1.DATSTIPULA.value;
	for (i=0;i<mobilita.length;i++) {
		if (compDate(dataStipula, mobilita[i].datInizio)>=0) {
			for (j=0;j<statoOccupazionale.length;j++) {
				if (compDate(dataStipula, statoOccupazionale[j].datInizio)>=0 &&
					(statoOccupazionale[j].datFine=="" || compDate(dataStipula, statoOccupazionale[j].datFine)<=0))
						return true;
			}
		}
	}
	return false;
}

/**
* Controlli generici sulle date ed altro prima dell' invio della form
*/
function controlla() {
	//var dataMobilita = "<%--=dataInizioMobilita--%>";
    dataDich = document.Frm1.datDichDisponibilita.value;
    dataStipula = document.Frm1.DATSTIPULA.value;
    dataFine = document.Frm1.DATFINE.value;
    var flagPatto297 = document.Frm1.flgPatto297.value;
    var codMotivoFineIndex = 0;
    var dataRiferimentoIndice = document.Frm1.DATRIFERIMENTOINDICE.value;
    <%-- Savino 11/10/2005  sposto qui il controllo sulla data iscrizione elenco anagrafico, obbligatoria sempre --%>
    if (document.Frm1.datInizio.value=="") {
    	alert("Il lavoratore non è iscritto all'elenco anagrafico");
    	return false;
    }
    try {
        selInd = document.Frm1.CODMOTIVOFINEATTO.selectedIndex;
        codMotivoFineIndex = selInd;
    }catch(e) {}
    if ( (flagPatto297 == "N") && (statoOccRagg == "D" || statoOccRagg == "I") ) {
		alert("Non è possibile inserire un accordo generico se il lavoratore si trova in 150");
		return false;
	}
    if (compDate(dataDich, dataStipula)>0) {
        alert(Messages.Date.ERR_DATA_PATTO);
        return false;
    }
    if (isFuture(dataStipula)) {
        alert(Messages.Date.ERR_DATA_STIP);
        return false;
    }
    if (isOld(dataStipula) && !confirm(Messages.Date.WARNING_STIPULA)) {        
        return false;
    }
    // Controllo sulla chiusura del patto
    if (dataFine!="" && codMotivoFineIndex==0) {
        alert(Messages.Date.ERR_DATA_MOTIVO);
        return false;
    }
    if (dataFine=="" && codMotivoFineIndex>0) {
        alert(Messages.Date.ERR_DATA_MOTIVO2);
        return false;
    }
    if (dataFine!="" && compDate(dataStipula , dataFine)>0){
        alert(Messages.Date.ERR_DATA_STIP_USCITA);
        return false;
    }
  	if (dataFine==""  && !iscrittoMobilita() && (!siStipulaPatto297 || compDate(dataStipula, dataInizioSoAperto)<0) && dataDich=="" && flagPatto297 == "S") {
  		alert("Mobilità non presente o successiva alla data stipula: patto non stipulabile");
  		return false;
  	}
  	if (dataRiferimentoIndice != "" && isFuture(dataRiferimentoIndice)) {
        alert("La data di riferimento non può essere successiva alla data odierna");
        return false;
    }
  	if (document.Frm1.CODMOTIVOFINEATTO.value != "" && document.Frm1.CODMOTIVOFINEATTO.value == motivoChiusuraAccAutomatica) {
		alert("Motivo chiusura patto/accordo non gestibile manualmente");
        return false;
	}
    return true;
}
function solo297(tipo297) {	
	var flagTipo297 = document.Frm1.flgPatto297.value;
	<%-- Savino 10/10/05: bisogna impedire che un patto esistente possa cambiare tipo --%>
	<%if (!p.getFlgPatto297().equals("")) {%>
		patto297 = <%=p.isPatto297()%>;
		if ((patto297 && flagTipo297=="N") || (!patto297 && flagTipo297=="S")) {
			alert("Impossibile modificare la tipologia del patto/accordo.");
			document.Frm1.flgPatto297.value = '<%=p.getFlgPatto297()%>';
			for (i=0;i<document.Frm1.codCodificaPatto.length;i++) {
	            if (document.Frm1.codCodificaPatto.options[i].value == '<%=p.getCodificaPatto()%>') {
	            	document.Frm1.codCodificaPatto.selectedIndex=i;
	                break;
	            }
	        }	
			return false;
		}
	<%}%>
	return true;
}
/** 
* deprecato
*/
function apriStampa(RPT,paramPerReport,tipoDoc){ 
  //RPT: attributo name del tag ACTION nel file action.xml relativo al report da visualizzare
  //paramPerReport: parametri necessari a visualizzare il report 
  //tipoDoc: codice relativo al tipo di documento cosi come inserito nel campo CODTIPODOCUMENTO della tab. DE_DOC_TIPO
  
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var urlpage="AdapterHTTP?ACTION_NAME="+RPT;
  urlpage+=paramPerReport; //Quelli che nella calsse sono inseriti nel vettore params
  urlpage+="&tipoDoc="+tipoDoc;
  urlpage+="&asAttachment=false";
  if(confirm("Vuoi PROTOCOLARE il file pirma di visualizzarlo?\n\nSeleziona\n- \"OK\"        per PROTOCOLLARE prima di visualizzare la stampa\n- \"Annulla\"  per visualizzare SENZA PROTOCOLLARE"))
  { urlpage+="&salvaDB=true";
  }
  else
  { urlpage+="&salvaDB=false";
  }
    
  setWindowLocation(urlpage);
    
}//apriStampa()

function apriPaginaModifiche() {
	var urlpage="AdapterHTTP?";    
    urlpage+="&cdnFunzione=<%=cdnFunzione%>";
    urlpage+="&cdnLavoratore=<%=cdnLavoratore%>"; 
    urlpage+="&prgPattoLavoratore=<%=p.getPrgPattoLavoratore()%>"; 
    urlpage+="&page=ModifichePattoPage";
    if (document.Frm1.flgPatto297.value == "S") {
    	urlpage+="&CODTIPODOCUMENTO=PT297";
    }
    else {
    	urlpage+="&CODTIPODOCUMENTO=ACLA";   
    }
	window.open(urlpage,"modificheAlPatto", 'toolbar=0, scrollbars=1, resizable=1,height=600,width=800'); 
}
var codStatoAttoCaricato = "<%=Utils.notNull(CODSTATOATTO)%>"; 
/**
* La protocollazione puo' avvenire solo attraverso la fase di stampa, quindi lo stato atto "protocollato" non puo' essere selezionato dall'operatore
*/
function controllaStato(statoObj) {
    stato = "";
    try {
        statoIndex = statoObj.selectedIndex;
        statoValue = statoObj.options[statoIndex].value; 
        stato = statoValue;
    }catch(e) {alert(e)}
    if (stato=="PR" && codStatoAttoCaricato!=stato) {
        alert("La protocollazione e' possibile dalla maschera di stampa");
        for (i=0;i<statoObj.length;i++) {
            if (statoObj.options[i].value==codStatoAttoCaricato) {
                statoObj.selectedIndex=i;
                return;
            }

        }
    }
}
function visualizzaSezioneADR(){
	if (document.Frm1.codTipoPatto.value == "ADR") {
		mostra("sezADR");
		mostra("infoADR");
	}
	else {
		document.Frm1.datInizioNaspi.value = '';
		document.Frm1.decAssegnoNaspi.value = '';
		document.Frm1.noteElemAttivazione.value = '';
		nascondi("sezADR");
		nascondi("infoADR");
	}	
}

//Funzione che permette di scegliere 'accordo generico' solo se non vi è una DID associata.
function permettiAccordo(){
  <% if (noAccordoGen) { %>
        if (document.Frm1.flgPatto297.value == "N"){ 
          alert("Impossibile scegliere 'Accordo Generico' in quanto vi è una DID corrispondente.");
          document.Frm1.flgPatto297.value = "";
          selezioneVuota();
          return false;
        }
  <% } else { %>
          if (document.Frm1.flgPatto297.value == "N"){ 
          <%-- Savino 11/10/05: questa chiamata produce per un qualche errore il reset del campo del cpi. 
          			Commentata per il momento --%>
//            valorizzaCampiPerAccGen();
          }
    <% } %>
    return true;
}

function nuovoPattoDaPrec(stato){
  if (stato)
    document.Frm1.DUPLICA.value = "DUPLICA";
  else
    document.Frm1.DUPLICA.value = "";
}

function controlloPersonalizzato(inputName){
  var ctrlObj = eval("document.forms[0]." + inputName);
    
  if(document.Frm1.flgPatto297.value == "S"){
    if (ctrlObj.value=="") {
      alert("Il campo " + ctrlObj.title + " è obbligatorio");
  		ctrlObj.focus();
    	return false;
    }
  }
  return true;
}

function valorizzaCampiPerAccGen(){
  document.Frm1.codCPI.value="<%=p.getCodCpi()%>";
  document.Frm1.codCPI2.value="<%=p.getDescCpi()%>";

  document.Frm1.prgStatoOccupaz.value="<%=p.getPrgStatoOccupaz()%>"; 
    <%
    /*int sizeAcc = 11, maxAcc = 65;
     * if(descStatoAcc != null)
     * { sizeAcc = descStatoAcc.length()+3;
     *   if(sizeAcc > maxAcc) 
     *   { descStatoAcc = descStatoAcc.substring(0,maxAcc)+"...";
     *     sizeAcc = maxAcc + 4;
     *   }
     * }
     */
    %>
  document.Frm1.prgStatoOccupaz2.value="<%=p.getPrgStatoOccupaz()%>";
}

function disabilitaBottone() {
	if (document.Frm1.insert_pattolav_button != null) {
		document.Frm1.insert_pattolav_button.disabled = true;	
		document.Frm1.insert_pattolav.disabled = false;	
	}
	else
		if (document.Frm1.Salva_button != null) {
			document.Frm1.Salva_button.disabled = true;	
			document.Frm1.Salva.disabled = false;	
		}
			
	return true;	
}
	function cambiAnnoProt(dataPRObj,annoProtObj) {
 	  var dataProt = dataPRObj.value;
 	  var lun = dataProt.length;
	
	  //Stiamo modificando la data di protocollazione. Quindi cambia anche l'anno di protocollazione
	  annoProtObj.value = ""; 
	  
	  if (lun > 5) {
	    var tmpDate = new Object();
	    tmpDate.value = dataProt;
	    if ( checkFormatDate(tmpDate) ) {
	       annoProtObj.value = tmpDate.value.substr(6,10);      
	    }
	    else if (lun==8 || lun==10) {
	      alert("La data di protocollazione non è corretta");
	    }
	  }
	}

	function riapriPatto(prgPatto) {
		var f;
      	f = "AdapterHTTP?PAGE=PattoLavDettaglioInformazioniStorichePage";
      	f = f + "&PRGPATTOLAVORATORE=" + prgPatto;
      	f = f + "&cdnFunzione=<%=cdnFunzione%>";
      	f = f + "&OPERAZIONEMODULE=RIAPRI";
	  	document.location = f;
	}

	function riapriPattoOnLoad() {
		if (operazioneRiapertura == "true") {
			alert("Operazione completata con successo");
 			var queryStringOpener = "&PAGE=PattoLavDettaglioPage&cdnFunzione=13&CDNLAVORATORE=<%=cdnLavoratore%>";
  			window.opener.location = "AdapterHTTP?" + queryStringOpener;
  			window.close();
		}
		else {
			if (msgConfermaRiapertura != "") {
				if(confirm(msgConfermaRiapertura)) {
					var f;
			      	f = "AdapterHTTP?PAGE=PattoLavDettaglioInformazioniStorichePage";
			      	f = f + "&PRGPATTOLAVORATORE=<%=p.getPrgPattoLavoratore()%>";
			      	f = f + "&cdnFunzione=<%=cdnFunzione%>";
			      	f = f + "&OPERAZIONEMODULE=RIAPRI&FORZATURA=true";
				  	document.location = f;
				}
			}
		}
	}
	
	function visualizzaSezionePattoOnLine(){
		var valore ;
			if(document.Frm1.flgPattoOnLine.options){
				var indiceSel = document.Frm1.flgPattoOnLine.options.selectedIndex;
				valore =  document.Frm1.flgPattoOnLine.options[indiceSel].value;
			}else{
				valore = "<%=flgPattoOnLine%>";
			
			}
		if (valore=='S') {
			mostra("sezpattoonline");
			mostra("ptOnlineSez");
		}
		else {
			nascondi("sezpattoonline");
			nascondi("ptOnlineSez");
		}	 
	}
	
	function storicoInviiPtOnLine() {
    	var prgPatto = document.Frm1.PRGPATTOLAVORATORE.value;
    	var f = "AdapterHTTP?PAGE=PTOnLineStoricoPage" + "&PRGPATTOLAVORATORE=" + prgPatto ;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=580,height=400,top=100,left=100"
        window.open(f, t, feat);
    }
	
	


-->
</SCRIPT>
<style >
<%@ include file="_sezioneDinamica_css.inc"%>
</style>
</head>

<body class="gestione" onLoad="showIfsetVar();rinfresca();visualizzaSezionePattoOnLine();visualizzaSezioneADR();riapriPattoOnLoad();<%if(strApriEv.equals("1") && bevid) { %> apriEvidenze() <%}%>">
<%
    if (testata!=null)testata.show(out);
    if (l!=null)l.show(out);
%>
<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_SavePattoLav"/>
 <af:showMessages prefix="M_InsertPattoLav"/>
 <af:showMessages prefix="DUPLICA_PATTO_MOD"/>
 <af:showMessages prefix="M_RIAPRI_PATTO"/>
</font>


<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="solo297(document.Frm1.flgPatto297) && controlla() && disabilitaBottone() ">
<center>
<%out.print(htmlStreamTop);%>
<table class="main" border="0">
<tr>
  <td colspan="6" class="azzurro_bianco">
  <!--<div style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:lightblue">-->
    <!--<%out.print(htmlStreamTop);%>-->
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr><td class="etichetta2">Stato&nbsp;Patto</td>
        <td>
        <table cellpadding="0" cellspacing="0" border="0" width="100%"><tr>
        <%
            String codAtto = Utils.notNull(CODSTATOATTO);
            if (codAtto.equals("")) codAtto = "PP";
            
        %>
        <td><af:comboBox classNameBase="input" name="CODSTATOATTO"  moduleName="M_STATOATTOPATTO" selectedValue="<%=codAtto%>"
                         addBlank="true" blankValue="" required="true" title="Stato patto" 
                         disabled="<%=\"true\"/*readOnlyString*/%>" onChange="fieldChanged();controllaStato(this);"/>
        </td>
        <td align="right">anno&nbsp;</td>
        <td><af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="text" name="annoProt" value="<%=annoProt!=null ? annoProt : \"\"%>"
                                     readonly="true" title="Anno protocollo" size="5" maxlength="4"/></td>
        <td align="right"> &nbsp;&nbsp;num.&nbsp;</td>
        <td><af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="text" name="numProt" value="<%=numProt%>"
                                     readonly="true" title="Numero protocollo" size="8" maxlength="100"/></td>
            <td class="etichetta2">data
               <af:textBox name="dataProt" 
                           type="date" 
                           value="<%=dataProt%>" 
                           size="11" 
                           maxlength="10"
                           title="data di protocollazione"  
                           classNameBase="input" 
                           readonly="true" 
                           validateOnPost="true" 
                           required="false" 
                           trim ="false" 
                           onKeyUp="cambiAnnoProt(this,annoProt)" 
                           onBlur="checkFormatDate(this)"
                /></td>
       		<% if (ProtocolloDocumentoUtil.protocollazioneLocale()) { %>
            <td class="etichetta2">ora
               <af:textBox name="oraProt"
                           type="text"
                           value="<%=oraProt%>"
                           size="6" 
                           maxlength="5"
                           title="data di protocollazione"  
                           classNameBase="input" 
                           readonly="true"
                           validateOnPost="false" 
                           required="false" 
                           trim ="false"
                      /></td>
            <% } else { %>
            <td><input type="hidden" name="oraProt" value="00:00">
            <% } %>
        </tr>
        </table>
    </td></tr>
    <tr><td class="etichetta2">Doc. di</td>
    <td>
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
        <tr><td class="campo2">
               <af:textBox name="docInOrOut" type="text"
                           value="<%=(docInOrOut.equalsIgnoreCase(\"I\")) ? \"Input\" : \"Output\"%>"
                           size="6" 
                           maxlength="6"
                           title="data di protocollazione"  
                           classNameBase="input" 
                           readonly="true"
                           validateOnPost="false" 
                           required="false" 
                           trim ="false"
                           /></td>
            <td class="etichetta2">Rif.</td>
            <td class="campo2">
               <af:textBox name="docInOrOut" type="text"
                           value="<%=docRif%>"
                           size="<%=docRif.length()+5%>"
                           title="data di protocollazione"  
                           classNameBase="input" 
                           readonly="true"
                           validateOnPost="false" 
                           required="false" 
                           trim ="false"
                           /></td>
        </tr>
        </table>
    </td>
    </tr></table>


    
  <!--</div>--></td>
</tr>  
<tr><td><br/></td></tr>
<table class="main">
<tr>
    <td colspan=2>
        <table>
            <tr>
            <td class="etichetta">Tipologia&nbsp;</td> 
                <td>
                  <input type="hidden" name="flgPatto297" value="<%=p.getFlgPatto297()%>" size="10" maxlength="10">
                  <af:comboBox name="codCodificaPatto" classNameBase="input"  addBlank="false" required="true" title="Tipologia" 
                  		moduleName="M_GetCodificaTipoPatto" selectedValue="<%=p.getCodificaPatto()%>"
                      	onChange="fieldChanged();settaFlag297() && solo297(this) && showIfsetVar() && hasDichiarazioneDisponibilita() && permettiAccordo();" 
                      	disabled="<%=String.valueOf(!canModify || isProtocollato)%>" />
                </td>
                <%if(isPattoOnLine) {%>  
                <td class="etichetta">Patto/Accordo stipulato con modalità online&nbsp;</td> 
                <td>
                  <af:comboBox name="flgPattoOnLine" classNameBase="input"  addBlank="true" required="false" title="Patto stipulato con modalità online" 
                  		moduleName="M_GenericComboSiNo" selectedValue="<%=flgPattoOnLine%>"
                      	disabled="true" />
                </td>
           		 <%}%>
                </tr>
            <%if (!p.getCodTipoPatto().equalsIgnoreCase(PattoBean.DB_ADESIONE_NUOVO_PROGRAMMA)) {%>
	            <tr>
	                <td class=etichetta2>Misure concordate&nbsp;</td>
	                <td nowrap>
	                    <af:comboBox name="codTipoPatto" classNameBase="input"  addBlank="true" required="true" title="Misure concordate" 
	                        selectedValue="<%=p.getCodTipoPatto()%>"
	                        moduleName="M_GETDETIPOPATTO" onChange="fieldChanged();visualizzaSezioneADR();" disabled="<%=String.valueOf(!obiettiviDelPattoModify)%>" />            
	                </td>
	            </tr>
	      	<%}%>
        </table>
    </td>
</tr>
<tr><td><br></td></tr>
<tr>
  <td colspan="2"><!-- Le tre etichette potrebbero diventare diverse --->
    <div id="titoloIni" style="display:"     class="sezione2">Informazioni amministrative collegate (selezionare la tipologia)</div>
    <div id="titolo297" style="display:none" class="sezione2">Informazioni amministrative collegate</div>
    <div id="titoloAcc" style="display:none" class="sezione2">Informazioni amministrative collegate</div>
  </td>
</tr>      

<tr><td colspan="2">
<div id="infoPatto1" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td class="etichetta"> 
      Data inserimento nell'elenco anagrafico 
    </td>
    <td class="campo">    
    <% if (!(p.isAccordoGenerico() && p.getDatInizio().equals(""))){ 
    		// se il lavoratore e' in mobilita', non ha la did anche se disoccupato, e lo stato e' PP, allora il record
    		// non e' associato alla did quindi non viene estratta la data dell'elAnag. Per cui bisogna prendere
    		// quella corrente.
    		// Savino 13/10/05: purtroppo questo non e' piu' vero. Bisogna decidere quali devono essere le info
    		//		da visualizzare quando il patto e' ancora in pp
    		/*
    		if (iscrittoMobilita && Utils.notNull(CODSTATOATTO).equals("PP") && Utils.notNull(FLGPATTO297).equals("S")) 
    			datInizio = dataInizioElAnagCorrente;
    	TracerSingleton.log("",TracerSingleton.DEBUG, "iscrittoMobilita="+iscrittoMobilita + ", CODSTATOATTO="+CODSTATOATTO+", FLGPATTO297="+FLGPATTO297 + ", dataInizioElAnagCorrente="+dataInizioElAnagCorrente);
    	*/
    %>
      <af:textBox name="datInizio" value="<%=p.getDatInizio()%>" classNameBase="input" type="date" validateOnPost="true" 
                  readonly="true" required="false" title="Data inserimento nell'elenco anagrafico" onKeyUp="fieldChanged();"
                  size="12" maxlength="10"  validateWithFunction="" />&nbsp;*
    <%} else {  // accordo ma manca l'iscrizione all'elenco anagrafico %>
        <input name="datInizio" value="">
    <%}%></td>
  </tr>
</table></div>
</td></tr>

<tr><td colspan="2">
<div id="infoPatto2" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td class="etichetta"> 
      <%-- Savino 22/02/2006: sostituita etichetta<div id="labelVisulizza" style="display">Cpi titolare dei dati&nbsp;</div> --%>
      <div id="labelVisulizza" style="display">Cpi competente&nbsp;</div>
      <div id="labelNascondi" style="display:none">Cpi con cui &egrave; stato stipulato l'accordo</div>
    </td>
    <td>
     <input name="codCPI" value="<%=p.getCodCpi()%>" type="hidden"  size="10" maxlength="10"  >    
     <af:textBox name="codCPI2" value="<%=p.getDescCpi()%>" classNameBase="input" type="text" 
           onKeyUp="fieldChanged();" validateOnPost="true" required="true" title="CpI" readonly="true" size="32" maxlength="32"/>
    </td>    
  </tr>
</table></div>
</td></tr>

<tr><td colspan="2">
<div id="infoPatto3" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td colspan="1" class="etichetta">Data immediata disponibilità</td>
    <td colspan="1">    
    <% if (!(p.isAccordoGenerico() && p.getDatDichiarazione().equals(""))){ %>
      <af:textBox name="datDichDisponibilita" value="<%=p.getDatDichiarazione()%>" classNameBase="input" type="date" 
                  required="false" readonly="true" title="Data immediata disponibilità" validateOnPost="false"
                  onKeyUp="fieldChanged();" size="12" maxlength="10" validateWithFunction="" />&nbsp;*
    <%} else {%>
    <input name="datDichDisponibilita">
    <%}%>
    </td>
  </tr>
</table></div>
</td></tr>

<tr><td colspan="2">
<div id="infoPatto4" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
<tr>
  <td class="etichetta">Stato occupazionale</td>
  <td  class="campo">
    <input type="hidden" name="prgStatoOccupaz" value="<%=p.getPrgStatoOccupaz()%>" size="10" maxlength="10"  >            
    <%
    /*
      if(descStato != null)
      { size = descStato.length()+3;
        if(size > max) 
        { descStato = descStato.substring(0,max)+"...";
          size = max + 4;
        }
      }
      */
      if (p.getFlgPatto297().equalsIgnoreCase("S")) {
    	  size = p.getDescrizioneStatoTagliato(max).length() + 4;
    	  %>
    	  <af:textBox name="prgStatoOccupaz2" value="<%=p.getDescrizioneStatoTagliato(max) %>" classNameBase="input"  
	          readonly="true" required="false" title="Stato occupazionale" onKeyUp="fieldChanged();" 
	          size="<%=size%>" maxlength="100"/>
    	  <%
      }
      else {
    	  size = descrizioneStatoOccAccordo.length() + 4;
    	  %>
    	  <af:textBox name="prgStatoOccupaz2" value="<%=descrizioneStatoOccAccordo %>" classNameBase="input"  
		      readonly="true" required="false" title="Stato occupazionale" onKeyUp="fieldChanged();" 
		      size="<%=size%>" maxlength="100"/>
    	  <%
      }
      %>
  </td>
</tr>
</table></div>
</td></tr>

  <tr >
    <td valign="top" colspan="2"><div class="sezione2"/></div></td>
  </tr>

  <tr>
    <td class="etichetta">Data stipula &nbsp;</td>
    <td>
      <table cellpadding="0" cellspacing="0" border="0" frame="box" width="100%">
        <tr><td width="25%" nowrap>
            <af:textBox name="DATSTIPULA" value="<%=p.getDatStipula()%>" classNameBase="input" type="date"
                        readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();"  validateOnPost="true"
                        required="true" title="Data stipula" 
                        size="12" maxlength="10"/>
            </td>
            <% if (p.getCodServizio().equals("")){ %>
            	<td colspan="2"><input type="hidden" name="CODSERVIZIO" value=""></td>
            <%} else {%>
            	<td class="etichetta"><%=labelServizio %> che stipula il patto/accordo</td>
            	<td class="campo">
	              <af:comboBox name="CODSERVIZIO" size="1" title="Codice servizio"
	                             multiple="false" disabled="<%=readOnlyString%>" 
	                             onChange="fieldChanged();" required="false"
	                             focusOn="false" moduleName="COMBO_SERVIZIO" classNameBase="input"
	                             selectedValue="<%=p.getCodServizio() %>" addBlank="true" blankValue=""/>    
	            </td>
             <%}%>            
        </tr>
      </table>
    </td>    
</tr>

  <%if (!p.getDatScadConferma().equals("")) {%>
  	<tr>
  		<td class="etichetta">Data scadenza conferma &nbsp;</td>
  		<td class="campo">
	      <af:textBox name="DATSCADCONFERMA" value="<%=p.getDatScadConferma()%>" type="date"
              readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();" classNameBase="input"  validateOnPost="true"                  
              required="false" title="Data scadenza conferma" size="12" maxlength="10"/>
	    </td>
	</tr>
  <%} else {%>
  		<input type="hidden" name="DATSCADCONFERMA" value=""/>
  <%}%>
  
  <%if (!dataStipulaOrig.equals("")) {%>
  	<tr>
  		<td class="etichetta">Data stipula originaria &nbsp;</td>
  		<td class="campo">
           <af:textBox name="DATASTIPULAORIGVA18" value="<%=dataStipulaOrig%>" type="date"
               readonly="true" classNameBase="input" title="Data stipula originaria" size="12" maxlength="10"/>    
         </td>
  	</tr>
   <%}%>
   
  <!--tr >
    <td class="etichetta"><br/>Impegno di comuncazione esiti</td><td></td>
  </tr-->
  <tr><td><br></td></tr>
<!-- Borriello maggio 2020 Patto online -->
<%if(isPattoOnLine){ %>
<tr>
  <td colspan="2">
    <div id="sezpattoonline" style="display:none" class="sezione2">Patto/Accordo On Line</div>
  </td>
</tr>
 	<tr><td colspan="2">
 	<div id="ptOnlineSez" style="display: none;">
	<table cellpadding="1" cellspacing="0" border="0" width="100%">
	<tr>
	    <td class="etichetta" nowrap>Codice di abilitazione ai servizi amministrativi</td>
	    <td class="campo">
	    	<af:textBox name="STRCODABIPORTALE" value="<%=Utils.notNull(codiceAbilitazioneServizi)%>" type="text"
	           readonly="true"   classNameBase="input"                  
	           title="Codice di abilitazione ai servizi amministrativi" size="15" maxlength="10"/>
	     </td>
	</tr>
	<tr>
	    <td class="etichetta" nowrap>Data e ora invio per accettazione</td>
	    <td class="campo">
	    	<af:textBox name="DTMINVIOPORTALE" value="<%=dataInvioAccettazioneServizi%>" type="text"
		         readonly="true" classNameBase="input"                  
		         title="Data e ora invio per accettazione" size="20"  />
	     </td>
	</tr>
	<tr>
	    <td class="etichetta" nowrap>Stato Patto On Line</td>
	    <td class="campo">
	    	<af:comboBox classNameBase="input" name="CODMONOACCETTAZIONE"  moduleName="M_ComboStato_PattoOnLine" selectedValue="<%=statoPattoOnLine%>"
                         addBlank="true" blankValue=""   title="Stato Patto On Line" 
                         disabled="true"  />
	     </td>
	</tr>
	<tr>
	    <td class="etichetta" nowrap>Data e ora accettazione</td>
	    <td class="campo">
	    	<af:textBox name="DTMACCETTAZIONE" value="<%=dataAccettazioneServizi%>" type="text"
		         readonly="true" classNameBase="input"                  
		         title="Data e ora accettazione" size="20"  />
	     </td>
	</tr>
	<tr>
	    <td class="etichetta" nowrap>Data e ora ultima stampa</td>
	    <td class="campo">
	    	<af:textBox name="DTMULTIMASTAMPA" value="<%=dataUltimaStampa%>" type="text"
		         readonly="true" classNameBase="input"                  
		         title="Data e ora ultima stampa" size="20"  />
	     </td>
	     
		<%if(canStoricoPtOnLine && storicoPattoOnLine){ %>
	      <td colspan="2" align="center">      	
	     	 <input type="button" name="btnStoricoPattoOnLine" class="pulsanti<%=((storicoPattoOnLine)?"":"Disabled")%>"  
	     	 value="Storico invii patto/accordo" onclick="javascript:storicoInviiPtOnLine();"
	      <%=(!storicoPattoOnLine)?"disabled=\"True\"":""%>>
	     </td>
	     <%} %>
	</tr>
	</table>
	 </div>
	</td>
	</tr>

	<tr><td><br></td></tr>
<%}%>
<!-- fine patto on line -->  
<tr>
  <td colspan="2">
    <div id="sezprofiling" style="display:" class="sezione2">Profiling (Garanzia Giovani)</div>
  </td>
</tr>

<%if (DateUtils.compare(dataStipulaPattoCheck, MessageCodes.General.DATA_PROFILING_2015) < 0) {%>
	<tr><td colspan="2">
	<table cellpadding="1" cellspacing="0" border="0" width="100%">
	<tr>
	    <td class="etichetta">Indice di svantaggio &nbsp;</td>
	    <td class="campo">
	    	<af:textBox name="INDICESVANTAGGIO" value="<%=indiceSvantaggio%>" type="number"
	           readonly="<%=readOnlyStringIndice%>" onKeyUp="fieldChanged();" classNameBase="input"  validateOnPost="true"                  
	           title="Indice di svantaggio" size="5" maxlength="1"/>
	     </td>
	</tr>
	</table>
	</td>
	</tr>
<%}%>

<tr><td colspan="2">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
<tr>
    <td class="etichetta">Indice GG/Dlgs 150 &nbsp;</td>
    <td class="campo">
    	<af:textBox name="INDICESVANTAGGIO2" value="<%=indiceSvantaggio2%>" type="number"
	         readonly="<%=readOnlyStringIndice%>" onKeyUp="fieldChanged();" classNameBase="input"  validateOnPost="true"                  
	         title="Indice GG" size="5" maxlength="1"/>
     </td>
</tr>
</table>
</td>
</tr>

<tr><td colspan="2">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
<tr>        			

    <td class="etichetta">Data riferimento &nbsp;</td>
    <td class="campo">
    	<af:textBox name="DATRIFERIMENTOINDICE" value="<%=dataRiferimentoIndice%>" type="date"
	       	readonly="<%=readOnlyStringDataRif%>" onKeyUp="fieldChanged();" classNameBase="input"  validateOnPost="true"                  
	           title="Data riferimento" size="12" maxlength="10"/>
     </td>
</tr>
</table>
</td>
</tr>

<tr><td><br></td></tr>

<tr>
  <td colspan="2">
    <div id="sezprofiling" style="display:" class="sezione2">Profiling (Dlgs 150)</div>
  </td>
</tr>

<tr><td colspan="2">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
<tr>
    <td class="etichetta">Indice Dlgs 150</td>
    <td class="campo">
    	<af:textBox name="INDICESVANTAGGIO150" value="<%=indiceSvantaggio150%>" type="number"
	         readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();" classNameBase="input"  validateOnPost="true"                  
	         title="Indice Dlgs 150" size="12" maxlength="11"/>
	    &nbsp;&nbsp;&nbsp;<af:textBox name="CATEGORIAINDICESV150" classNameBase="input" readonly="true" value="<%= strProfiling150%>"/>	         
     </td>
</tr>
</table>
</td>
</tr>

<tr><td colspan="2">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
<tr>
    <td class="etichetta">Data riferimento</td>
    <td class="campo">
    	<af:textBox name="DATRIFERIMENTOINDICE150" value="<%=dataRiferimentoIndice150%>" type="date"
	       	readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();" classNameBase="input"  validateOnPost="true"                  
	           title="Data riferimento" size="12" maxlength="10"/>
     </td>
</tr>
</table>
</td>
</tr>

<% if(!Utils.notNull(codFiscaleSoggAcc).equals("")){ %>
    <tr class="note">
      <td colspan="2">
      <div class="sezione2">
        <img id='tendinaSoggettoAcc' alt="Chiudi" src="../../img/aperto.gif" onclick="cambiaTendina(this,'soggAccSez',document.Frm1.codFiscaleSoggAcc);"/>&nbsp;&nbsp;&nbsp;Soggetto Accreditato
      &nbsp;&nbsp;
      </div>
      </td>
	</tr>
    <tr>
      <td colspan="2">
        <div id="soggAccSez" style="display: none;">
          <table class="main" width="100%" border="0">
              <tr>
                <td class="etichetta">Codice Fiscale</td>
                <td class="campo">
                  <af:textBox classNameBase="input" type="text" name="codFiscaleSoggAcc" readonly="true" value="<%=codFiscaleSoggAcc%>" size="30" maxlength="16"/>
                  <input type="hidden" name="codSedeSoggAcc"  value="<%=codSedeSoggAcc %>" >               
                </td>
              </tr>
              <tr>
                <td class="etichetta">Ragione Sociale</td>
                <td class="campo">
                  <af:textBox classNameBase="input" type="text" name="ragioneSocialeSoggAcc" readonly="true" value="<%=ragioneSocialeSoggAcc%>" size="80" maxlength="200"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">Indirizzo</td>
                <td class="campo">
                  <af:textBox classNameBase="input" type="text" name="indirizzoSoggAcc" readonly="true" value="<%=indirizzoSoggAcc%>" size="60" maxlength="100"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">Comune</td>
                <td class="campo">
                  <af:textBox classNameBase="input" type="text" name="comuneSoggAcc" readonly="true" value="<%=comuneSoggAcc%>" size="60" maxlength="100"/>
                </td>
              </tr>
              <tr>
			    <td class="etichetta">
			      Appuntamento&nbsp;
			    </td>
			    <td class="campo">
			      <af:textArea name="strNoteEnte" value="<%=noteAppuntamentoSoggAcc%>"
			                   cols="60" rows="4" maxlength="2000"  classNameBase="textarea" 
			                   readonly="true" />
			    </td>
			  </tr>
            </table>
        </div>
    </td>
  </tr>
 <%} %>


<%if ( numConfigVoucher.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.CUSTOM_CONFIG)){%>
	<tr><td><br></td></tr>
	<tr>
	  <td colspan="2">
	    <div id="sezprofilingvoucher" style="display:" class="sezione2">Profiling (voucher)</div>
	  </td>
	</tr>
	
	<tr><td colspan="2">
	      <table cellpadding="1" cellspacing="0" border="0" width="100%">
	        <tr>
	        	<td class="etichetta" nowrap>Indice</td>
	        	<td class="campo">
	        		<input type="hidden" name="CODVCHPROFILING" value="<%=codVCHProfiling%>">
	            	
	            	<af:comboBox name="TXTCODVCHPROFILING" size="1" title="Indice" selectedValue="<%=codVCHProfiling%>" 
	            	multiple="false" required="false" focusOn="false" moduleName="M_GET_VCH_PROFILING" 
	            	classNameBase="input" addBlank="true" blankValue="" disabled="true"/>
                    
                    <af:comboBox name="CODVCHPROFILINGPROFILO" size="1" title="Indice" selectedValue="<%=prgLavoratoreProfilo%>"
                    	multiple="false" required="false"
                    	focusOn="false" moduleName="M_GET_VCH_PROFILING_FROM_PROFILO" classNameBase="input"
                    	addBlank="true" blankValue="" disabled="<%=readOnlyString%>"/>
	            </td>
	            <td class="etichetta" nowrap>Dote a processo</td>
	            <td class="campo">
	              <af:textBox name="decDoteProcessoAssegnato" value="<%=decDoteProcessoAssegnato%>" type="number"
	         		readonly="true" classNameBase="input" title="Dote a processo" size="5"/>    
	            </td>   
	            <td class="etichetta" nowrap>Residuo Dote a processo</td>
	            <td class="campo">
	              <af:textBox name="decDoteProcessoResidua" value="<%=decDoteProcessoResidua%>" type="number"
	         		readonly="true" classNameBase="input" title="Residuo Dote a processo" size="5"/>    
	            </td>        
	        </tr>
	        <tr>
	        	<td nowrap>&nbsp;</td>
	        	<td nowrap>&nbsp;</td>
	            <td class="etichetta" nowrap>Dote a risultato</td>
	            <td class="campo">
	              <af:textBox name="decDoteRisultatoAssegnato" value="<%=decDoteRisultatoAssegnato%>" type="number"
	         		readonly="true" classNameBase="input" title="Dote a risultato" size="5"/>    
	            </td>   
	            <td class="etichetta" nowrap>Residuo Dote a risultato</td>
	            <td class="campo">
	              <af:textBox name="decDoteRisultatoResidua" value="<%=decDoteRisultatoResidua%>" type="number"
	         		readonly="true" classNameBase="input" title="Residuo Dote a risultato" size="5"/>    
	            </td>         
	        </tr>
	        
	      </table>
	    </td>
	</tr>
	
	<tr><td><br></td></tr>
<%} else {%>
	<tr>
	<td>
	<input type="hidden" name="CODVCHPROFILING" value="<%=p.getCodVchProfiling() %>">
	<input type="hidden" name="CODVCHPROFILINGPROFILO" value="<%=prgLavoratoreProfilo %>">
	<input type="hidden" name="decDoteProcessoAssegnato" value="<%=decDoteProcessoAssegnato%>">
	<input type="hidden" name="decDoteProcessoResidua" value="<%=decDoteProcessoResidua%>">
	<input type="hidden" name="decDoteRisultatoAssegnato" value="<%=decDoteRisultatoAssegnato%>">
	<input type="hidden" name="decDoteRisultatoResidua" value="<%=decDoteRisultatoResidua%>">
	<br>
	</td>
	</tr>
<%}%>
  
<tr>
    <td colspan="4">
        <table class='sezione2' cellspacing=0 cellpadding=0>
            <tr>
                <td  width=18>
                    <img id='IMG1' src='<%=img1%>' onclick='cambia(this, document.getElementById("TBL1"))'></td>
                        <td  class='titolo_sezione'>Chiusura patto/accordo</td>    				
                <td align='right' width='30'></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td colspan=4 align="center">
        <TABLE id='TBL1' style='width:100%;display:<%=display1%>'>     
        <script>initSezioni(new Sezione(document.getElementById('TBL1'),document.getElementById('IMG1'),<%=hasDataUscita%>));</script>
			<td class="etichetta">
                Data fine patto/accordo &nbsp;
            </td>
            <td class="campo">
                <af:textBox  name="DATFINE" value="<%=p.getDatFine()%>" classNameBase="input" type="date" 
                readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();" validateOnPost="true"   size="12" maxlength="10"/>
            </td>
			<tr>
			    <td class="etichetta">Motivo fine patto/accordo&nbsp;</td>
                <td class="campo">
                    <af:comboBox name="CODMOTIVOFINEATTO" moduleName="M_MOTFINEATTOPATTO" selectedValue="<%=p.getCodMotivoFineAtto() %>" 
                          disabled="<%=readOnlyString%>" onChange="fieldChanged();" classNameBase="input" addBlank="true"/>
                </td>
			</tr>
        </table>
    </td>
</tr>

<tr>
  <td colspan="2">
    <div id="sezADR" style="display:none" class="sezione2">Assegno di ricollocazione</div>
  </td>
</tr>


<tr><td colspan="2">
<div id="infoADR" style="display:none">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td class="etichetta">Data inizio fruizione NASPI</td>
    <td class="campo">
    <af:textBox name="datInizioNaspi" value="<%=datNaspi %>" classNameBase="input" type="date" validateOnPost="true" 
                  readonly="<%=readOnlyString%>" required="false" title="Data inizio fruizione NASPI" onKeyUp="fieldChanged();"
                  size="12" maxlength="10"  validateWithFunction="" />
    </td>
  </tr>
  <tr>
    <td class="etichetta">Importo massimo assegno di ricollocazione</td>
    <td class="campo">
    <af:textBox name="decAssegnoNaspi" value="<%=decImportoAr%>" type="number"
		readonly="<%=readOnlyString%>" classNameBase="input" title="Importo massimo assegno di ricollocazione" size="9"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Elementi di attivazione richiesti</td>
    <td class="campo"> 
    	 <af:textArea name="noteElemAttivazione" value="<%=noteElemAttivazione%>"
                   cols="60" rows="4" maxlength="2000" classNameBase="textarea" 
                   readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();"/>
    </td>
  </tr>
</table>
</div>
</td></tr>


<tr><td><br></td></tr>



  
  <tr>
    <td class="etichetta">
      Note&nbsp;
    </td>
    <td class="campo">
      <af:textArea name="strNote" value="<%=p.getStrNote()%>"
                   cols="60" rows="4" maxlength="100" classNameBase="textarea" 
                   readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();"/>
    </td>
  </tr>


  <%if (!flag_insert) {%>
  <tr>
  <td>
    <input type="hidden" name="NUMKLOPATTOLAVORATORE2" value="<%=p.getNumKlockPattoLavoratore()%>" size="10" maxlength="10"  >
    <%--NUMKLOPATTOLAVORATORE= NUMKLOPATTOLAVORATORE.add(new BigDecimal(1)); --%>
    <input type="hidden" name="NUMKLOPATTOLAVORATORE" value="<%=p.getNextKlock()%>">
  </td>        
  </tr>
<%}%>
</table>
<BR><center><% operatoreInfo.showHTML(out);%></center>


<BR><% String NomePagina = "";%>

<table class="main" border="0">

<% if (!flag_insert) {%>
   <%--
      <input class="pulsante" type="button" name="Documenti associati" value="Documenti associati"
           onClick="docOpenPopupAbstract('DocumentiAssociatiPage',
           '&cdnLavoratore=<%=cdnLavoratore%>&lookLavoratore=false&lookAzienda=true&contesto=L&infStoriche=true',
           'PattoLavDettaglioPage','<%=cdnFunzione%>',null,'<%=p.getPrgPattoLavoratore() %>')">  
    -->
   </td>
  <td align="center">
  	<%if(canModify){%>
  		<input type="hidden" name="Salva" class="pulsanti" value="Aggiorna" disabled=true>
  		<input type="submit" name="Salva_button" class="pulsanti" value="Aggiorna">
  	<%}%>
  </td>
  <td align="right">    
  <%-- TODO Savino: non si stampa ...
    <%if(canEdit && !CODSTATOATTO.equals("PR")){%>
    	<input name="Invia" type="button" class="pulsanti" value="Stampa patto/accordo"
        	   onclick="controllaStampaPatto()">        
    <%}%>
    --%>

<tr>
  <!--<td width="33%"></td>-->
  <td align="left" colspan="2" width="33%">
    <%if(canInsert){%>
      <input type="submit" class="pulsanti" name="Inserisci" value="Inserisci nuovo">&nbsp;&nbsp;
      <%-- Savino 05/10/2005: il pulsante viene nascosto per evitare che le azioni vengano associate a due patti 
      --%>
      <input type="submit" class="pulsanti" name="DuplicaPatto" value="Inserisci nuovo da precedente" onClick="nuovoPattoDaPrec(true);">
      <%----%>
    <%}%>
  </td>
  <td align="right" width="33%">
    <%if(infStorButt){%>
      <input type="button" name="infSotriche" class="pulsanti<%=((storicoPatto)?"":"Disabled")%>" value="Informazioni storiche" onClick="apriInfoStoriche('<%= cdnLavoratore %>')"
      <%=(!storicoPatto)?"disabled=\"True\"":""%>>
    <%}%>
  </td>
</tr>
<%} else {%>
<!--tr>
  <td></td>controllaPatto()
  <td align="center"><!-- <input type="submit" name="Salva" value="Aggiorna"></td> -->
  <!--td align="right"><!--<input type="submit" name="Cancella" value="cancella il record">-->
    <!--input name="Invia" type="button" class="pulsanti" value="Stampa patto/accordo" onclick="sceglipage(2,null)">      
  </td>
</tr-->
<tr><td><br/></td></tr>
<tr>
   <td width="33%" align="center">      
   </td>
   <%if (canInsert){%>
   		<td width="33%" align="center">
   			<input type="hidden" class="pulsanti" name="insert_pattolav" value="Inserisci" disabled=true>
   			<input type="submit" class="pulsanti" name="insert_pattolav_button" value="Inserisci">
   		</td>
   <%}%>
   <td width="33%" align="right">
     <%if(buttonAnnulla)
     {%><input class="pulsanti" type="button" name="annulla" value="Chiudi senza inserire" onclick="sceglipage(1);"><%}%>
   </td>
</tr>
<tr><td>&nbsp;</td></tr>
    <%if(infStorButt){%>
      <tr>
  	  <td colspan="3" align="right">
      <input type="button" name="infSotriche" class="pulsanti<%=((storicoPatto)?"":"Disabled")%>" value="Informazioni storiche" onClick="apriInfoStoriche('<%= cdnLavoratore %>')"
      <%=(!storicoPatto)?"disabled=\"True\"":""%>>
      </td>
      </tr>
    <%}%>
<%}%>

</table>

<input type="hidden" name="PAGE" value="PattoLavDettaglioPage">
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
<%if (p.getCodTipoPatto().equalsIgnoreCase(PattoBean.DB_ADESIONE_NUOVO_PROGRAMMA)) {%>
<input type="hidden" name="codTipoPatto" value="<%=PattoBean.DB_ADESIONE_NUOVO_PROGRAMMA%>">
<%}%>
<!-- per creare un nuovo patto a partire dal precedente -->
<%if(!flag_insert){%>
<input type="hidden" name="DUPLICA" value="">
<%}%>
<%-- TODO Savino: bisogna mostrare le modifiche di un patto storicizzato? --%>
<%if (false && dataUltimoProtocolloModificato!=null) {%>
<input class="pulsanti" type="button" value="modifiche succ. al <%=dataUltimoProtocolloModificato%>" onclick="apriPaginaModifiche()">
<%}%>
<%--if (PRGPATTOLAVORATORE==null) PRGPATTOLAVORATORE=(BigDecimal)sessionContainer.getAttribute("PRGPATTOLAVORATORE");--%>

<input  type="hidden" name="PRGPATTOLAVORATORE" value="<%=p.getPrgPattoLavoratore()%>" size="22" maxlength="16">
<input  type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>" size="22" maxlength="16">

<!-- variabile di appoggio usata per memorizzare il progresivo della dichiarazione di disp. utilizzata poi nello script showIfSetVar()-->
<input  type="hidden" name="PRGDICHDISP" value="<%= p.getPrgDichDisponibilita() %>">

<input type="hidden" name="PRGDICHDISPONIBILITA" value="<%= p.getPrgDichDisponibilita() %>"/>
<input type="hidden" name="REPORT" value="patto/patto.rpt">
<input type="hidden" name="PROMPT0" value="<%= cdnLavoratore %>">

<%if (canRiapriPatto && p.getDatFine() != null && !p.getDatFine().equals("")){%>
	<input class="pulsanti" type="button" value="Riapri Patto" onclick="riapriPatto('<%=p.getPrgPattoLavoratore()%>');">
<%}%>

<%out.print(htmlStreamBottom);%>
</af:form>

</body>
</html>
