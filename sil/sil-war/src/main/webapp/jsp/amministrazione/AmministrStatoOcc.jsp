<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.sil.util.amministrazione.impatti.Controlli,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                     
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean,
                  it.eng.sil.module.movimenti.InfoLavoratore,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	int errorCode = -1;
	String msgConferma = "";
	boolean confirmContinuaRicalcolo = false;
	boolean confirmContinuaRicalcoloSOccPrec297 = false;
	
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE");  
	String continuaRicaloloSOcc = serviceRequest.containsAttribute("CONTINUA_CALCOLO_SOCC")?serviceRequest.getAttribute("CONTINUA_CALCOLO_SOCC").toString():"false";
	String continuaRicaloloSOccPrec297 = serviceRequest.containsAttribute("CONTINUA_CALCOLO_SOCC_PREC_297")?serviceRequest.getAttribute("CONTINUA_CALCOLO_SOCC_PREC_297").toString():"false";
	String operazioneScelta = serviceRequest.containsAttribute("OPERAZIONESCELTA")?serviceRequest.getAttribute("OPERAZIONESCELTA").toString():"";
	
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  	PageAttribs attributi = new PageAttribs(user, _page);

 	boolean readOnlyStr     = true;
  	boolean canInsert       = false;
  	boolean infStorButt     = false;
  	boolean canDocAss       = false;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
      infStorButt = attributi.containsButton("INF_STOR");
      canDocAss = attributi.containsButton("DOCUMENTI_ASSOCIATI");
      canInsert = attributi.containsButton("INSERISCI");
      readOnlyStr = !attributi.containsButton("AGGIORNA");
    	if((!canInsert) && (readOnlyStr)){
    		//canInsert=false;
        //rdOnly=true;
    	}else{
        boolean canEdit=filter.canEditLavoratore();
        if (canInsert){
          canInsert=canEdit;
        }
        if (!readOnlyStr){
          readOnlyStr=!canEdit;
        }        
    	}
    	
    
    Iterator errors = responseContainer.getErrorHandler().getErrors().iterator();
  	while (errors.hasNext()) {
   		com.engiweb.framework.error.EMFUserError error = (com.engiweb.framework.error.EMFUserError)errors.next();
    	errorCode = error.getCode();
		if (errorCode == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE ||
			errorCode == MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE ||
			errorCode == MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI ||
			errorCode == MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA ||
			errorCode == MessageCodes.StatoOccupazionale.INSERISCI_STATO_OCC_PREC_NORMATIVA_297 || errorCode == MessageCodes.StatoOccupazionale.INSERT_CALCOLA_STATO_OCC_PREC_NORMATIVA_297) {
			if (errorCode == MessageCodes.StatoOccupazionale.INSERISCI_STATO_OCC_PREC_NORMATIVA_297 || errorCode == MessageCodes.StatoOccupazionale.INSERT_CALCOLA_STATO_OCC_PREC_NORMATIVA_297) {
				confirmContinuaRicalcoloSOccPrec297 = true;
				msgConferma = error.getDescription();
			}
			else {
				confirmContinuaRicalcolo = true;
				if (errorCode == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE) {
					msgConferma = "Esistono stati occupazionali inseriti/modificati manualmente che potrebbero essere cancellati dal ricalcolo impatti.";
				}
				else {
					if (errorCode == MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE) {
						msgConferma = "Esistono stati occupazionali inseriti/modificati manualmente che non saranno cancellati dal ricalcolo impatti.";	
					}
					else {
						if (errorCode == MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI) {
							msgConferma = "Esistono stati occupazionali con mesi anzianità/sospensione non calcolati dal Sil che potrebbero essere cancellati dal ricalcolo impatti.";
						}
						else {
							msgConferma = "Esistono stati occupazionali di disoccupazione con anzianità diversa dalla data di stipula DID/iscrizione alla mobilità che potrebbero essere cancellati dal ricalcolo impatti.";
						}
					}
				}
			}
		}
    }	
  }
%>


<%
    //Indica che formattazione dei mesi di disp. ed anz, deve visualizzare
    boolean visualizzazione = false;
    Vector statoOccRows = serviceResponse.getAttributeAsVector("M_GETSTATOOCCUPAZIONALE.ROWS.ROW");
    Vector cat181Rows   = serviceResponse.getAttributeAsVector("M_GET181CAT.ROWS.ROW");
    Vector laureaRows   = serviceResponse.getAttributeAsVector("M_GetLaureaPerCat181.ROWS.ROW");
    Vector movimenti   = serviceResponse.getAttributeAsVector("M_GETMOVIMENTI.ROWS.ROW");

    SourceBean row          = null;
    //String cdnLavoratore    = null;  
    String codStatoOccLetto = null;
    String cod181           = null;
    String dataInizio       = null;
    String dataFine         = null;
    String strNote          = null;
    String dtmIns           = null;
    String dtmMod           = null;
    String indennizzo       = null;
    String pensionato       = null;
    String strNumeroAtto    = null;
    String dataAtto         = null;
    String dataRichRevisione= null;
    String dataRicGiurisdz  = null;
    String dataAnzDisoc     = null;
    String codStatoAt       = null;
    BigDecimal redditoStr   = null;
    BigDecimal mesiAnz      = null;
    String mesiAnzInt       = null;
    String giorniAnzInt       = null;
    String codMonoProvenienza = "";
    BigDecimal giorniAnzDallaData	= null;
    String giorniAnzDallaDataInt  = null;
    BigDecimal mesiAnzPrec      = null;
    String mesiAnzPrecInt       = null;
    String codMonoCalcAnzPrec   = null;
    
    BigDecimal numMesiSosp  = null;
    String numMesiSospInt   = null;
    
    BigDecimal numGGRestantiMesiSospFornero2014  = null;
    String numGGRestantiMesiSospFornero2014Int  = null;
    String numGiorniSospFornero2014  = null;
    String mesiAnzFornero = null;
    String infoSospensioneFornero = null;
    String infoMesiRischioDisocc = null;
    String infoMesiRischioDisoccCompleto = null;
    String infoMesiRischioDisoccVis = null;
    String dataFornero2014 = MessageCodes.General.DATA_DECRETO_FORNERO_2014;
    BigDecimal numMesiSospFornero2014  = null;
    String numMesiSospFornero2014Int  = null;
    BigDecimal numGGRestantiRischioDisocc  = null;
    String numGGRestantiRischioDisoccInt  = null;
    
    BigDecimal numMesiSospPrec  = null;
    String numMesiSospPrecInt   = null;

    String datcalcoloanzianita = null;
    String datcalcolomesisosp = null;
    
    String codStatoOccRagg  = null;
    BigDecimal cdnUtIns     = null;
    BigDecimal cdnUtMod     = null;
    BigDecimal keyLock      = null;
    BigDecimal prgStatoOccupaz = null;
    String cat181           = "";
    String cpiTit           = null;
    BigDecimal mesiInattivita = null;
    boolean flag_insert     = false;
    boolean buttonAnnulla   = false;
    /*boolean readOnlyStr     = true;
    boolean canInsert       = false;
    boolean infStorButt     = false;*/
    String dataFineMovimento = null;
    String sesso = null;
    int anniInattivita = -1;
    Testata operatoreInfo   = null;   
    Linguette l  = null;
    //cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
    InfCorrentiLav testata = null;
    String disoccInoccText = "";
    //Creo le inf. riassuntive del lavoratore
    testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    //testata.setMaxLenStatoOcc(50);
    //testata.setPaginaLista("StatoOccRisultRicercaPage");
    //Info sul lavoratore
    InfoLavoratore _lav = new InfoLavoratore(new BigDecimal(cdnLavoratore));
    
    //testata.show(out);

    //Creo le linguette --
    int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
    String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
    //String _page = (String) serviceRequest.getAttribute("PAGE"); 
    l  = new Linguette(user,_cdnFunz,_page,new BigDecimal(cdnLavoratore));
   
    if(serviceRequest.containsAttribute("newRecord"))   { 
        //si vuole inserire un nuovo record
        flag_insert = true; buttonAnnulla = true;
        prgStatoOccupaz = new BigDecimal((String) serviceRequest.getAttribute("prgStatoOccupaz"));        
    }
    else {  
        if(statoOccRows != null && !statoOccRows.isEmpty())  { 
            row = (SourceBean) statoOccRows.elementAt(0);      
            dataInizio      = (String)      row.getAttribute("DATINIZIO");
            dataFine        = (String)      row.getAttribute("DATFINE");
            codStatoOccLetto= (String)      row.getAttribute("CODSTATOOCCUPAZ");
            cod181          = (String)      row.getAttribute("CODCATEGORIA181");
            indennizzo      = (String)      row.getAttribute("FLGINDENNIZZATO");
            pensionato      = (String)      row.getAttribute("FLGPENSIONATO");
            numMesiSosp     = (BigDecimal)  row.getAttribute("NUMMESISOSP");
            numMesiSospPrec = (BigDecimal)  row.getAttribute("NUMMESISOSPPREC");
            redditoStr      = (BigDecimal)  row.getAttribute("NUMREDDITO");
            dataAnzDisoc    = (String)      row.getAttribute("DATANZIANITADISOC");
            strNote         = (String)      row.getAttribute("STRNOTE");
            cdnUtIns        = (BigDecimal)  row.getAttribute("CDNUTINS");
            dtmIns          = (String)      row.getAttribute("DTMINS");
            cdnUtMod        = (BigDecimal)  row.getAttribute("CDNUTMOD");
            dtmMod          = (String)      row.getAttribute("DTMMOD");
            mesiAnz         = (BigDecimal)  row.getAttribute("MESI_ANZ");
            giorniAnzDallaData  = (BigDecimal)  row.getAttribute("giorni_anz");
            mesiAnzPrec         = (BigDecimal)  row.getAttribute("MESI_ANZ_PREC");
            codMonoCalcAnzPrec  = (String)      row.getAttribute("CODMONOCALCOLOANZIANITAPREC297");
            strNumeroAtto   = (String)      row.getAttribute("STRNUMATTO");
            dataAtto        = (String)      row.getAttribute("DATATTO");
            dataRichRevisione=(String)      row.getAttribute("DATRICHREVISIONE");
            dataRicGiurisdz = (String)      row.getAttribute("DATRICORSOGIURISDIZ");
            codStatoAt      = (String)      row.getAttribute("CODSTATOATTO");
            keyLock         = (BigDecimal)  row.getAttribute("NUMKLOSTATOOCCUPAZ");
            prgStatoOccupaz = (BigDecimal)  row.getAttribute("PRGSTATOOCCUPAZ");
            codStatoOccRagg = (String)      row.getAttribute("codstatooccupazragg");
            sesso = _lav.getSesso();  //(String)row.getAttribute("STRSESSO");
            codMonoProvenienza = (String)row.getAttribute("CODMONOPROVENIENZA");
            datcalcolomesisosp = (String)      row.getAttribute("datcalcolomesisosp");
            datcalcoloanzianita = (String)      row.getAttribute("datcalcoloanzianita");
            //
    		infoSospensioneFornero = (String)row.getAttribute("mesiSospFornero2014");
    		infoMesiRischioDisocc = (String)row.getAttribute("mesi_rischio_disocc");
    		infoMesiRischioDisoccCompleto = (String)row.getAttribute("mesi_rischio_disocc_completo");
            cpiTit = (String) row.getAttribute("CODCPITIT");
            operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
        }
        else { 
        //non ci sono dati inerenti il lavoratore: possiamo solo inserire un nuovo record
          flag_insert = true;
        }
    }//else
	
	//se sto forzando l'inserimento dello stato occupazionale in presenza di stati
	//occupazionali gestiti manualmente, allora i valori devono essere presi dalla request
	if (confirmContinuaRicalcolo || confirmContinuaRicalcoloSOccPrec297) {
    	flag_insert = true;
    	buttonAnnulla = true;
    	dataInizio = serviceRequest.containsAttribute("DATINIZIO")?(String)serviceRequest.getAttribute("DATINIZIO"):"";
    	codStatoOccLetto = serviceRequest.containsAttribute("codStatoOcc")?(String)serviceRequest.getAttribute("codStatoOcc"):"";
    	codStatoOccRagg = serviceRequest.containsAttribute("nuovoRaggStatoOccupaz")?(String)serviceRequest.getAttribute("nuovoRaggStatoOccupaz"):"";
    	if (serviceRequest.containsAttribute("numMesiSosp") && !serviceRequest.getAttribute("numMesiSosp").toString().equals("")) {
    		numMesiSospPrec = new BigDecimal((String)serviceRequest.getAttribute("numMesiSosp"));
    	}
    	if (serviceRequest.containsAttribute("numMesiSospSucc") && !serviceRequest.getAttribute("numMesiSospSucc").toString().equals("")) {
    		numMesiSosp = new BigDecimal((String)serviceRequest.getAttribute("numMesiSospSucc"));
    	}
    	if (serviceRequest.containsAttribute("numMesiAnzianita") && !serviceRequest.getAttribute("numMesiAnzianita").toString().equals("")) {
    		mesiAnz = new BigDecimal((String)serviceRequest.getAttribute("numMesiAnzianita"));
    	}
    	if (serviceRequest.containsAttribute("numAnzianitaPrec297") && !serviceRequest.getAttribute("numAnzianitaPrec297").toString().equals("")) {
    		mesiAnzPrec = new BigDecimal((String)serviceRequest.getAttribute("numAnzianitaPrec297"));
    	}
    	datcalcoloanzianita = (String)serviceRequest.getAttribute("datcalcoloanzianita");
    	datcalcolomesisosp = (String) serviceRequest.getAttribute("datcalcolomesisosp");
    	dataAnzDisoc    = (String)serviceRequest.getAttribute("DATANZIANITADISOC");
    	strNote         = (String)serviceRequest.getAttribute("STRNOTE");
    	indennizzo      = (String)serviceRequest.getAttribute("FLGINDENNIZZATO");
        pensionato      = (String)serviceRequest.getAttribute("FLGPENSIONATO");
        codMonoCalcAnzPrec  = (String)serviceRequest.getAttribute("CODMONOCALCOLOANZIANITAPREC297");
        strNumeroAtto   = (String)serviceRequest.getAttribute("STRNUMATTO");
        dataAtto        = (String)serviceRequest.getAttribute("DATATTO");
        dataRichRevisione=(String)serviceRequest.getAttribute("DATRICHREVISIONE");
        dataRicGiurisdz = (String)serviceRequest.getAttribute("datRicorsoGiurisdz");
        codStatoAt      = (String)serviceRequest.getAttribute("CODSTATOATTO");
        codMonoProvenienza = (String)serviceRequest.getAttribute("CODMONOPROVENIENZA");
        infoSospensioneFornero = (String)serviceRequest.getAttribute("mesiSospFornero2014");
        infoMesiRischioDisocc = (String)serviceRequest.getAttribute("mesi_rischio_disocc");
        infoMesiRischioDisoccCompleto = (String)row.getAttribute("mesi_rischio_disocc_completo");
    }
    
	if (infoSospensioneFornero != null && !infoSospensioneFornero.equals("")) {
    	String [] sospFornero = infoSospensioneFornero.split("-");
    	if (sospFornero.length == 4) {
    		numMesiSospFornero2014 = new BigDecimal(sospFornero[0]);
        	numGiorniSospFornero2014 = sospFornero[1];
        	mesiAnzFornero = sospFornero[2];
        	numGGRestantiMesiSospFornero2014 = new BigDecimal(sospFornero[3]);
    	}	
    }
	
	if (infoMesiRischioDisoccCompleto != null && !infoMesiRischioDisoccCompleto.equals("")) {
    	String [] mesiRischio = infoMesiRischioDisoccCompleto.split("-");
    	if (mesiRischio.length == 2) {
    	    numGGRestantiRischioDisocc = new BigDecimal(mesiRischio[1]);
    	}	
    }
	
	if (infoMesiRischioDisocc == null || infoMesiRischioDisocc.equals("")) {
		infoMesiRischioDisocc = "0";
	}
	
    if(numMesiSosp != null) {
    	numMesiSospInt = String.valueOf( numMesiSosp.intValue() );	
    }
    else {
    	numMesiSospInt = "0";	
    }
    
    if (numMesiSospFornero2014 != null) {
    	numMesiSospFornero2014Int = String.valueOf( numMesiSospFornero2014.intValue() );	
    }
    else {
    	numMesiSospFornero2014Int = "0";	
    }
      
    if(numMesiSospPrec != null) {
      	numMesiSospPrecInt = String.valueOf( numMesiSospPrec.intValue() );
    } else {
    	numMesiSospPrecInt = "0";	
    }
    
    if(mesiAnz != null) {
    	mesiAnzInt = String.valueOf( mesiAnz.intValue() );	
    }
    else {
    	mesiAnzInt = "0";	
    }
    
    if(mesiAnzPrec != null) {     
    	mesiAnzPrecInt     = String.valueOf( mesiAnzPrec.intValue() );
    } else {
    	mesiAnzPrecInt = "0";	
    }
    
    if (giorniAnzDallaData != null) {
    	giorniAnzDallaDataInt = String.valueOf( giorniAnzDallaData.intValue() );
    } else {
    	giorniAnzDallaDataInt = "0";	
    }
    
    int ggTotaleRestantiSospensioni = 0;
    if (numGGRestantiMesiSospFornero2014 != null) {
    	numGGRestantiMesiSospFornero2014Int = String.valueOf( numGGRestantiMesiSospFornero2014.intValue() );
    	ggTotaleRestantiSospensioni = numGGRestantiMesiSospFornero2014.intValue();
    }
    else {
    	numGGRestantiMesiSospFornero2014Int = "0";	
    }
    if (numGGRestantiRischioDisocc != null) {
    	numGGRestantiRischioDisoccInt = String.valueOf( numGGRestantiRischioDisocc.intValue() );
    	ggTotaleRestantiSospensioni = ggTotaleRestantiSospensioni + numGGRestantiRischioDisocc.intValue();
    }
    else {
    	numGGRestantiRischioDisoccInt = "0";	
    }
    int mesiAggiuntiviSospensioni = ggTotaleRestantiSospensioni/30;
    int ggResiduoTotaleSospensioni = ggTotaleRestantiSospensioni%30;
    
    int meseDiffAnzianitaGiorni = 0;
    giorniAnzInt = "0";
    int ggResiduiAnzianita = Integer.parseInt(giorniAnzDallaDataInt);
    
    if (ggResiduiAnzianita >= ggResiduoTotaleSospensioni) {
    	ggResiduiAnzianita = ggResiduiAnzianita - ggResiduoTotaleSospensioni;
    	giorniAnzInt = String.valueOf(ggResiduiAnzianita);
    }
    else {
    	if (ggResiduoTotaleSospensioni > 0) {
       		ggResiduiAnzianita = ggResiduiAnzianita + (30 - ggResiduoTotaleSospensioni);
       		meseDiffAnzianitaGiorni = 1;
       	}
       	giorniAnzInt = String.valueOf(ggResiduiAnzianita%30);	
    }
    
    infoMesiRischioDisoccVis = infoMesiRischioDisocc + " e (" + numGGRestantiRischioDisoccInt + " giorni) ";
    
    if (numMesiSospInt!=null && Integer.parseInt(numMesiSospInt)<0) numMesiSospInt = "0"; //null;
    if (numMesiSospPrecInt!=null && Integer.parseInt(numMesiSospPrecInt)<0) numMesiSospPrecInt = "0"; 
    if (mesiAnzInt!=null && Integer.parseInt(mesiAnzInt)<0) mesiAnzInt = "0";//null;
    if (mesiAnzPrecInt!=null && Integer.parseInt(mesiAnzPrecInt)<0) mesiAnzPrecInt = "0";
    if (numMesiSospFornero2014Int!=null && Integer.parseInt(numMesiSospFornero2014Int)<0) numMesiSospFornero2014Int = "0";
    // movimenti
    
    if (movimenti!=null && movimenti.size()>0) {
        row = (SourceBean)movimenti.get(0);
        dataFineMovimento = (String)row.getAttribute("DATAFINEMOVIMENTO");
        mesiInattivita = (BigDecimal)row.getAttribute("mesiInattivita");
        //if (mesiInattivita!=null&& mesiInattivita.intValue()>0) anniInattivita= mesiInattivita.intValue()/12;
    }
        
    BigDecimal eta = new BigDecimal("-1"); 
    String flgObbSco = null;
    String flgLaurea = "";
    String annoNascita = null;
    if(cat181Rows != null && !cat181Rows.isEmpty())  { 
        row = (SourceBean) cat181Rows.elementAt(0);
        eta     = (BigDecimal) row.getAttribute("ANNI");
        flgObbSco = (String)     row.getAttribute("FLGOBBLIGOSCOLASTICO");
        annoNascita = (String)row.getAttribute("datNasc");
        
    }
    
    
    flgLaurea = laureaRows != null && !laureaRows.isEmpty() ? "S":"N";
    cat181 = Controlli.getCat181(annoNascita, DateUtils.getNow(), flgObbSco, flgLaurea);
    
    String codCat181 = null;
    
    if (cat181!=null && cat181.equalsIgnoreCase("GIOVANE")) codCat181 ="G";
    //Numero effettivo dimesi di anzianità
    BigDecimal totMesiAnz = new BigDecimal(String.valueOf(Integer.parseInt(mesiAnzPrecInt)+Integer.parseInt(mesiAnzInt)-
    		(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt)+Integer.parseInt(numMesiSospFornero2014Int)+mesiAggiuntiviSospensioni+
    		 Integer.parseInt(infoMesiRischioDisocc))));
    disoccInoccText = Controlli.disoccInoccLungaDurata(codStatoOccRagg, totMesiAnz, codCat181);//mesiAnz
    boolean  donnaInReinserimento = Controlli.donnaInInserimentoLavorativo(codStatoOccRagg, mesiInattivita, sesso);
    
    String displayAnzSosp = "none";
	if (codStatoOccRagg != null && (codStatoOccRagg.charAt(0)=='D' || codStatoOccRagg.charAt(0)=='I')) {
		displayAnzSosp = "";
	}
	
    String htmlStreamTop = StyleUtils.roundTopTable(canInsert);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canInsert);
    String moduloDeStatoOcc = flag_insert ? "M_GETDESTATOOCCGESTIBILI":"M_GETDESTATOOCC";    
    Vector gruppiStatoOccRows=null;
    if (flag_insert) {
  		gruppiStatoOccRows = serviceResponse.getAttributeAsVector("M_GETDESTATOOCCGESTIBILI.ROWS.ROW");
  	}
  	else {
  		gruppiStatoOccRows = serviceResponse.getAttributeAsVector("M_GETDESTATOOCC.ROWS.ROW");
  	}	
  	SourceBean row_gruppoStatoOcc = null;
	
	//CHECK STORICO stato occupazionale  per la visibilità del pulsante apposito
   	boolean storicoSocc = serviceResponse.containsAttribute("M_HasStorStatoOcc.ROWS.ROW");
	
	if (datcalcoloanzianita != null && !datcalcoloanzianita.equals("") && DateUtils.compare(datcalcoloanzianita, dataFornero2014) >= 0) {
		dataFornero2014 = datcalcoloanzianita;
	}
	
	if (mesiAnzFornero == null || mesiAnzFornero.equals("") || Integer.parseInt(mesiAnzFornero)<0) {
		mesiAnzFornero = "0";
	}
%>

<%
// POPUP EVIDENZE
String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
int _fun = 1;
if((cdnFunzione!=null) && !cdnFunzione.equals("")) { _fun = Integer.parseInt(cdnFunzione); }
EvidenzePopUp jsEvid = null;
boolean bevid = attributi.containsButton("EVIDENZE");
if(strApriEv.equals("1") && bevid) {
	jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnTipoGruppo(), user.getCdnProfilo());
}	
%> 

<html>
<head>
<title>Stato Occupazionale</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<%if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>

<%@ include file="../global/confrontaData.inc"%>

<%@ include file="CommonScript.inc"%>    
<script language="Javascript" src="../../js/docAssocia.js"></script>
<!-- include lo script che permette l'apertura dei documenti associati-->

<script>
<!--
<%@ include file="../patto/_controlloDate_script.inc"%>
var gruppoStatoOcc = new Array();
var dettaglioStatoOcc = new Array();
<%for(int index=0; index < gruppiStatoOccRows.size(); index++)  { 
	row_gruppoStatoOcc = (SourceBean) gruppiStatoOccRows.elementAt(index);
    out.print("gruppoStatoOcc["+index+"]=\""+ row_gruppoStatoOcc.getAttribute("CODSTATOOCCUPAZRAGG").toString()+"\";\n");
    out.print("dettaglioStatoOcc["+index+"]=\""+ row_gruppoStatoOcc.getAttribute("CODICE").toString()+"\";\n");
   
}
%>

function controllaStatoOccSelezionato(combo) {
  var nuovoInserimento = <%=flag_insert%>;   
  <%
  	String sOcc = null;
  	sOcc =  statoOccRows.size()>0?(String)((SourceBean) statoOccRows.elementAt(0)).getAttribute("CODSTATOOCCUPAZ"):"C";
  %>
  var statoOccupazionaleAperto = "<%=sOcc%>";
  if (nuovoInserimento) {
  	
    if (combo.value!="" && (combo.value=='C1' || combo.value=='C11' || combo.value=='C12' || combo.value=='C13' || combo.value=='C14')) {
    	if (!(statoOccupazionaleAperto=='B2' || statoOccupazionaleAperto=='B1' ||  statoOccupazionaleAperto=='A22' ||
    		 statoOccupazionaleAperto=='A212'||  statoOccupazionaleAperto=='A21' || statoOccupazionaleAperto=='A213' || statoOccupazionaleAperto=='A223')) {
	        alert("Si puo' diventare decaduto solo se lo stato occupazionale precedente e' di inoccupazione o disoccupazione ai sensi del 150");
	        combo.value="";            
        }
    }
    else if (combo.value!="" && (combo.value=='A1')) {
    	if (!(statoOccupazionaleAperto=='B' )) {
	        alert("Si puo diventare occupato in cerca di altra occupazione solo se lo stato occupazionale precedente é occupato");
	        combo.value="";            
        }
     }
     else if (combo.value!="" && (combo.value=='D' || combo.value=='E' || combo.value=='F' )) {
     	if (statoOccupazionaleAperto!='C') {
     		alert("Si puo diventare segnalato o proveniente dal flusso scolastico solo se lo stato occupazionale precedente e' altro o assente");
	        combo.value="";            
     	}
     }
     else if (combo.value!="" && combo.value=="NT"){
     	alert("Stato occupazionale non gestibile");
     	combo.value="";
     }
  }
}

function controllaDataInizioFine() {
  var nuovoInserimento = <%=flag_insert%>;
  
  if (nuovoInserimento) {
    dataInizio = new String(document.form1.datInizio.value);
    //var result = checkDateWithNowDate (dataInizio);
    //var result = compDate(dataInizio, getNow());
    if (isOld(dataInizio)) {

      if (confirm("Data inizio stato occupazionale minore della data odierna, continuare?")) {
        return true;
      }
      else {
        return false;
      }
    }
    else {
      if (isFuture(dataInizio)) {
        alert ("Data inizio stato occupazionale non può essere maggiore della data odierna");
        return false;
      }
      else {
        return true;
      }
    }
  }
  return true;
}
function controllaStatoOccupazionale () {
	
	var nuovoInserimento = <%=flag_insert%>;   
	var nuovoStatoOccupaz = "";
  <%
  	String sOccRagg = null;
  	sOccRagg =  statoOccRows.size()>0?(String)((SourceBean) statoOccRows.elementAt(0)).getAttribute("CODSTATOOCCUPAZRAGG"):"A";
  %>
  var ret = true;
  var statoOccupazionaleRaggAperto = "<%=sOccRagg%>";
  nuovoStatoOccupaz = CalcolaGruppoStatoOccupaz (document.form1.codStatoOcc.value);
  if (nuovoStatoOccupaz=='D' || nuovoStatoOccupaz=='I') {
  	if (document.form1.datAnzianitaDisoc.value=='') {
  		alert("Inserire la data di anzianità");
  		document.form1.datAnzianitaDisoc.focus();
  		return false;
  	}  	
    if (document.form1.datcalcolomesisosp.value=='') {
  		alert("Inserire la data calcolo mesi sosp.");
  		document.form1.datcalcolomesisosp.focus();
  		return false;
  	}
    if (document.form1.datcalcoloanzianita.value=='') {
  		alert("Inserire la data calcolo mesi anzianità");
  		document.form1.datcalcoloanzianita.focus();
  		return false;
  	}
  }
  if (nuovoInserimento) {    
  	document.form1.nuovoRaggStatoOccupaz.value = nuovoStatoOccupaz;
  	if ((statoOccupazionaleRaggAperto == "D" || statoOccupazionaleRaggAperto == "I") 
  	    && (nuovoStatoOccupaz == "A" || nuovoStatoOccupaz == "O")) {
  		ret = confirm("Il nuovo stato occupazionale potrebbe far decadere la dichiarazione di immediata disponibilità/mobilità e, se stipulato, il patto. Continuare?");
  	}
  }
  return ret;
}
function insNuovo () {
	prepareSubmit();
	document.form1.newRecord.disabled=false;
	document.form1.submit();
}

function CalcolaGruppoStatoOccupaz (valore) {
	var indice = 0;
	for (i=0; i<dettaglioStatoOcc.length ;i++) {
		if (dettaglioStatoOcc[i] == valore) {
			indice = i;
			i = dettaglioStatoOcc.length;
		}
	}
	return gruppoStatoOcc[indice];
}


function calcolaGiornoPrec(data) {
	
	var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
	var datePat1 = /^(\d{1,1})(\/|-)(\d{1,1})(\/|-)(\d{4})$/;
	var datePat2 = /^(\d{1,1})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
	var datePat3 = /^(\d{1,2})(\/|-)(\d{1,1})(\/|-)(\d{4})$/;
	var datePat4 = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{2})$/;
	var datePat5 = /^(\d{1,1})(\/|-)(\d{1,1})(\/|-)(\d{2})$/;
	var datePat6 = /^(\d{1,1})(\/|-)(\d{1,2})(\/|-)(\d{2})$/;
	var datePat7 = /^(\d{1,2})(\/|-)(\d{1,1})(\/|-)(\d{2})$/;

	var matchArray1 = data.match(datePat);
  	var matchArray2 = data.match(datePat1);
	var matchArray3 = data.match(datePat2);
	var matchArray4 = data.match(datePat3);
	var matchArray5 = data.match(datePat4);
	var matchArray6 = data.match(datePat5);
	var matchArray7 = data.match(datePat6);
	var matchArray8 = data.match(datePat7);
	
	var matchArray;
	
	
  	if (matchArray1 != null) {
  		matchArray = matchArray1;
  	}
  	if (matchArray2 != null) {
  		matchArray = matchArray2;
  	}
  	if (matchArray3 != null) {
  		matchArray = matchArray3;
 	}
  	if (matchArray4 != null) {
  		matchArray = matchArray4;
  	}
  	if (matchArray5 != null) {
  		matchArray = matchArray5;
  	}
  	if (matchArray6 != null) {
  		matchArray = matchArray6;
  	}
  	if (matchArray7 != null) {
  		matchArray = matchArray7;
  	}
  	if (matchArray8 != null) {
  		matchArray = matchArray8;
  	}

    month = matchArray[3];
    day = matchArray[1];
    year = matchArray[5];
    	
	if (day >= 2) {
		day = day - 1;	
	}
	else {
		if (month == 1) {
			month = 12;
			year = year - 1;
			day = 31;	
		}
		else {
			if ((month - 1) == 6 || (month - 1) == 9 || (month - 1 ) == 11) { //mesi di 30 giorni
				month = month - 1;
				day = 30;		
			}
			else {
				if ((month - 1) == 2) {
					month = month - 1;
					if (year % 4 == 0) {
						day = 29;
					}
					else {
						day = 28;
					}
				}
				else {
					month = month - 1;
					day = 31;
				}
			}  
		}
	}
	return day + "/" + month + "/" + year;
}


function checkIsDate(dateObj) {
  var dateStr = dateObj.value;
  var day;
  var month;
  var year;
  var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
  var datePat1 = /^(\d{1,1})(\/|-)(\d{1,1})(\/|-)(\d{4})$/;
  var datePat2 = /^(\d{1,1})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
  var datePat3 = /^(\d{1,2})(\/|-)(\d{1,1})(\/|-)(\d{4})$/;
  var datePat4 = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{2})$/;
  var datePat5 = /^(\d{1,1})(\/|-)(\d{1,1})(\/|-)(\d{2})$/;
  var datePat6 = /^(\d{1,1})(\/|-)(\d{1,2})(\/|-)(\d{2})$/;
  var datePat7 = /^(\d{1,2})(\/|-)(\d{1,1})(\/|-)(\d{2})$/;
  
  var matchArray1 = dateStr.match(datePat);
  var matchArray2 = dateStr.match(datePat1);
  var matchArray3 = dateStr.match(datePat2);
  var matchArray4 = dateStr.match(datePat3);
  var matchArray5 = dateStr.match(datePat4);
  var matchArray6 = dateStr.match(datePat5);
  var matchArray7 = dateStr.match(datePat6);
  var matchArray8 = dateStr.match(datePat7);
  
  var matchArray;
  var bDataOk = false;
  if (matchArray1 != null) {
  	matchArray = matchArray1;
  	bDataOk = true;
  }
  if (matchArray2 != null) {
  	bDataOk = true;
  	matchArray = matchArray2;
  }
  if (matchArray3 != null) {
  	bDataOk = true;
  	matchArray = matchArray3;
  }
  if (matchArray4 != null) {
  	bDataOk = true;
  	matchArray = matchArray4;
  }
  if (matchArray5 != null) {
  	bDataOk = true;
  	matchArray = matchArray5;
  }
  if (matchArray6 != null) {
  	bDataOk = true;
  	matchArray = matchArray6;
  }
  if (matchArray7 != null) {
  	bDataOk = true;
  	matchArray = matchArray7;
  }
  if (matchArray8 != null) {
  	bDataOk = true;
  	matchArray = matchArray8;
  }
  
  if (!bDataOk) {
  	return false;
  }
  
  month = matchArray[3];
  day = matchArray[1];
  year = matchArray[5];
  
  if (month < 1 || month > 12) { // check month range
    return false;
  }

  if (day < 1 || day > 31) {
    return false;
  }

  if ((month==4 || month==6 || month==9 || month==11) && day==31) {
    return false;
  }

  if (month == 2) { // check for february 29th
    var isleap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
    if (day > 29 || (day==29 && !isleap)) {
      return false;
    }
  }
  dateObj.value = day+"/"+month+"/"+year;
  return true; // date is valid
}


function DispalyAnzSosp(comboStatoOcc) {
	var gruppoStatoOccupaz = CalcolaGruppoStatoOccupaz (comboStatoOcc.value);
	var dataPrec = "";
	if (gruppoStatoOccupaz == 'D' || gruppoStatoOccupaz == 'I') {
		if (validateDate('datInizio')) {
			// dataPrec = calcolaGiornoPrec (document.form1.datInizio.value);
			document.form1.datAnzianitaDisoc.value = document.form1.datInizio.value;
			<%-- Andrea 23/05/05 le date di calcolo .. vengono valorizzate con la data inizio e non piu' con il giorno prec --%>
			document.form1.datcalcolomesisosp.value = document.form1.datInizio.value;
			document.form1.datcalcoloanzianita.value = document.form1.datInizio.value;
		}
		else {
			document.form1.datAnzianitaDisoc.value = "";
			document.form1.datcalcolomesisosp.value = "";
			document.form1.datcalcoloanzianita.value = "";
		}
		mostra ("SezioneAnzSosp");
	}
	else {
		document.form1.datAnzianitaDisoc.value = "";
		document.form1.datcalcolomesisosp.value = "";
		document.form1.datcalcoloanzianita.value = "";
		document.form1.numMesiSosp.value = 0;
		document.form1.numAnzianitaPrec297.value = 0;
		nascondi ("SezioneAnzSosp");
	}
}


function ValorizzaDateAnzSosp(oggetto) {
	var dataPrec = "";
	if (checkIsDate(oggetto)) {
		//dataPrec = calcolaGiornoPrec (oggetto.value);
		document.form1.datAnzianitaDisoc.value = oggetto.value;
		<%-- Andrea 23/05/05 le date di calcolo .. vengono valorizzate con la data inizio e non piu' con il giorno prec --%>
		document.form1.datcalcolomesisosp.value = oggetto.value;
		document.form1.datcalcoloanzianita.value = oggetto.value;
	}
	else {
		document.form1.datAnzianitaDisoc.value = "";
		document.form1.datcalcolomesisosp.value = "";
		document.form1.datcalcoloanzianita.value = "";
	}
}

function VisualizzaSezioneDate() {
	mostra("sezioneDataSosp");
	mostra("sezioneMesiSosp");
	mostra("sezioneCalcolaSosp");
	mostra("sezioneDataAnz");
	mostra("sezioneMesiAnz");
	mostra("sezioneCalcolaAnz");
	nascondi("btnMostraDate");
	nascondi("sezioneMesiSospApp");
	mostra("sezioneMesiSospApp1");
	nascondi("sezioneMesiAnzApp");
	mostra("sezioneMesiAnzApp1");
}

function dettaglioAnzianita() {
	var f = "AdapterHTTP?PAGE=DettaglioMesiAnzianitaForneroPage&CDNFUNZIONE=" + <%=_cdnFunz%> + 
	"&CDNLAVORATORE=" + <%=cdnLavoratore%>;  
	var t = "_blank";
	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes," + 
	"resizable=yes,width=400,height=400,top=350,left=400";
	window.open(f, t, feat);
}

//-->
</script>
<script language="Javascript">

     window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);

</script>
<script language="Javascript">
  <%
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>

</head>
<body class="gestione" onload="isSetVar();rinfresca();conferma();<%if(strApriEv.equals("1") && bevid) { %> apriEvidenze() <%}%>">
<%
    if (testata!=null) testata.show(out);
    if (l!=null) {
    	if (l.getSize() > 0) {
	    	l.show(out);
	    } else {%>
			<p class="titolo">Stato Occupazionale</p>
	    <%}  
	}   
%>

<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_SaveStatoOcc"/>
 <af:showMessages prefix="M_InsStatoOcc"/>
</font>

<af:form name="form1" method="POST" action="AdapterHTTP" onSubmit="controllaDataInizioFine() && controllaStatoOccupazionale()">

<script language="JavaScript">
<!--
var operazioneStatoOcc = "";
var showButtonImg = new Image();
var hideButtonImg = new Image();
showButtonImg.src=" ../../img/aperto.gif";
hideButtonImg.src=" ../../img/chiuso.gif"

function continuaRicalcolo(valore) {
	document.form1.CONTINUA_CALCOLO_SOCC.value = valore;
  	doFormSubmit(document.form1);
}


function continuaInserimentoStatoOccPrec297(valore) {
	document.form1.CONTINUA_CALCOLO_SOCC_PREC_297.value = valore;
  	doFormSubmit(document.form1);
}

function inserimentoStatoOcc(op) {
	operazioneStatoOcc = op;
	document.form1.OPERAZIONESCELTA.value = op;
}

function conferma() {
	<%
	if (confirmContinuaRicalcolo) {
	%>
		if (confirm("<%=msgConferma%>")) continuaRicalcolo('true');
	<%}
	else {
		if (confirmContinuaRicalcoloSOccPrec297) {
		%>
			if (confirm("<%=msgConferma%>")) continuaInserimentoStatoOccPrec297('true');
		<%}
	}%>
}

function isSetVar()
{ var opzioneSel = document.form1.codStatoAtto.value;
  var numAtto    = document.form1.strNumAtto.value;

  if((opzioneSel != null && opzioneSel != "") || (numAtto != null && numAtto != ""))
  { onOff();
    mostra("datiRicorsoInAtto");
  }
  
}//isSetVar()

function onOff()
{	var div1 = document.getElementById("dett");
	var idImm = document.getElementById("imm1");
	if (div1.style.display=="")
  {	nascondi("dett");
		idImm.src = hideButtonImg.src
	} 
	else
  {	mostra  ("dett");
		idImm.src = showButtonImg.src;
	}
}//onOff()

function mostra(id)
{ var div = document.getElementById(id);
  div.style.display="";
}

function nascondi(id)
{ var div = document.getElementById(id);
  div.style.display="none";
}

//-->
</script>


<div align="left" id="datiRicorsoInAtto" style="display:none">
<font  color="red">
 <UL><LI><strong>Revisione/Ricorso in atto</strong></LI></UL>
</font>
</div>

<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main" border="0"> <!-- inizio tabella main -->
<tr>
  <td class="etichetta">Data inizio</td>    
  <td colspan="3">
    
    <table align="left" border="0" width="100%">
      <tr><td width="30%">
      <%
      if (!flag_insert) {
      %>
        <af:textBox classNameBase="input" type="date" name="datInizio" value="<%=dataInizio%>" validateOnPost="true"
                    required="true" readonly="true" onKeyUp="fieldChanged();" size="11" maxlength="10"/>
      <%
      }
      else {
      %>
        <af:textBox classNameBase="input" type="date" name="datInizio" value="<%=dataInizio%>" validateOnPost="true" 
                    required="true" onKeyUp="fieldChanged();ValorizzaDateAnzSosp(this);"  size="11" maxlength="10" title="Data inizio"/>
      <%
      }
      %>
      </td>
      <td class="etichetta">Provenienza</td>
      <td class="campo">
          <af:comboBox classNameBase="input" name="CODMONOPROVENIENZA" addBlank="true" disabled="true" required="true">
            <OPTION value="A" <%if ((codMonoProvenienza!=null) && codMonoProvenienza.equalsIgnoreCase("A")) out.print("SELECTED=\"true\"");%>>Da reg. anag.</OPTION>
            <OPTION value="D" <%if ((codMonoProvenienza!=null) && codMonoProvenienza.equalsIgnoreCase("D")) out.print("SELECTED=\"true\"");%>>Da D.I.D.</OPTION>
            <OPTION value="B" <%if ((codMonoProvenienza!=null) && codMonoProvenienza.equalsIgnoreCase("B")) out.print("SELECTED=\"true\"");%>>Da mobilità</OPTION>
            <OPTION value="M" <%if ((codMonoProvenienza!=null) && codMonoProvenienza.equalsIgnoreCase("M")) out.print("SELECTED=\"true\"");%>>Da movimenti</OPTION>
            <OPTION value="T" <%if ((codMonoProvenienza!=null) && codMonoProvenienza.equalsIgnoreCase("T")) out.print("SELECTED=\"true\"");%>>Da trasferimento comp.</OPTION>
            <OPTION value="P" <%if ((codMonoProvenienza!=null) && codMonoProvenienza.equalsIgnoreCase("P")) out.print("SELECTED=\"true\"");%>>Da Porting</OPTION>
            <OPTION value="G" <%if ((codMonoProvenienza!=null) && codMonoProvenienza.equalsIgnoreCase("G")) out.print("SELECTED=\"true\"");%>>Agg. manuale</OPTION>
            <OPTION value="O" <%if ((codMonoProvenienza!=null) && codMonoProvenienza.equalsIgnoreCase("O")) out.print("SELECTED=\"true\"");%>>Reg./Agg. manuale</OPTION>
            <OPTION value="N" <%if ((codMonoProvenienza!=null) && (codMonoProvenienza.equalsIgnoreCase("N") || flag_insert)) out.print("SELECTED=\"true\"");%>>Reg. manuale</OPTION>
          </af:comboBox>
      </td>
      </tr>
    </table>              
    
  </td>
</tr>
 
<tr>
  <td class="etichetta">Stato occupazionale </td>
  <td colspan="3">
  
  <table width="100%" border="0"><tr><td> 
     <af:comboBox classNameBase="input" name="codStatoOcc" moduleName="<%=moduloDeStatoOcc%>" selectedValue="<%=codStatoOccLetto%>" addBlank="true"
                  title="Stato occupazionale" required="true" onChange="fieldChanged();controllaStatoOccSelezionato(this);DispalyAnzSosp(this);" 
                  disabled="<%=String.valueOf(readOnlyStr || !flag_insert)%>" />
  </td></tr>
  </table>
  
  </td>
</tr>

<tr><td colspan="4">
<div id="SezioneAnzSosp" style="display:<%=displayAnzSosp%>">
<table>
<tr>
  <td class="etichetta" align="top">Anzianità di disoccupazione dal </td>
  <td colspan="3">
    <table border="0" width="100%">
    <tr><td width="25%" nowrap="nowrap">
        <af:textBox classNameBase="input" type="date" name="datAnzianitaDisoc" value="<%=dataAnzDisoc%>" 
               validateOnPost="true" readonly="<%= String.valueOf(readOnlyStr)%>" 
               onKeyUp="fieldChanged();" size="11" maxlength="10" />
        </td>
     <%-- if (dataInizio!=null && dataAnzDisoc!=null && !dataInizio.equals(dataAnzDisoc)) {%>                    
         <td class="etichetta">CPI titolare dei dati</td>
         <td class="campo"><%=Utils.notNull(cpiTit)%></td>
     <% } --%>
     	<td class="etichetta">&nbsp;</td>
        <td class="campo">&nbsp;</td>
    </tr>
    </table>              
    
  </td>
</tr>
</table>  <!-- fine tabella main -->

<table> <!-- inizio tabella main per mesi sosp e mesi anz. -->
<tr>
<td>
	<table>
	<tr>
	<td class="etichetta" nowrap>Mesi sosp. complessivi</td>
              <%
              String displaySezioneSosp = "none";
              String displayBottoneDate = "";
              String displaySezioneMesiSospApp = "";
              
              String sMesiSospPrecFornero = String.valueOf(Integer.parseInt(numMesiSospInt));
              String sMesiSospSuccFornero = String.valueOf(Integer.parseInt(numMesiSospFornero2014Int));
              String sMesiSosp = String.valueOf(Integer.parseInt(sMesiSospPrecFornero) + Integer.parseInt(sMesiSospSuccFornero) 
            		  + Integer.parseInt(infoMesiRischioDisocc) + mesiAggiuntiviSospensioni);
              
			  if (((datcalcolomesisosp == null) && (datcalcoloanzianita == null)) || (dataAnzDisoc !=null && (!dataAnzDisoc.equals(datcalcolomesisosp) || !dataAnzDisoc.equals(datcalcoloanzianita)))
			  		|| (!numMesiSospPrecInt.equalsIgnoreCase("0") || !mesiAnzPrecInt.equalsIgnoreCase("0") || (codMonoCalcAnzPrec!=null && !codMonoCalcAnzPrec.equalsIgnoreCase("")) ) ) {
			  	displaySezioneSosp = "";
			  	displayBottoneDate = "none";
			  }
			  %>
			  	<td class="campo" nowrap>
			  	<div id="sezioneMesiSospApp" style="display:<%=displayBottoneDate%>">
			  	<af:textBox classNameBase="input" type="integer" name="numMesiSospSuccApp" value="<%=sMesiSosp%>"
                	readonly="true" size="4" maxlength="3"/>
                	<b>&nbsp;(e&nbsp;<%=String.valueOf(ggResiduoTotaleSospensioni)%>&nbsp;giorni)&nbsp;</b>
              	</div>
              	</td>
              <%
              visualizzazione = true;
              %>
                <td class="etichetta" nowrap align="left">
                <div id="sezioneDataSosp" style="display:<%=displaySezioneSosp%>">
                  prima del&nbsp;<af:textBox classNameBase="input" type="date" name="datcalcolomesisosp" value="<%=Utils.notNull(datcalcolomesisosp)%>" validateOnPost="true"
                              readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();"  size="11" maxlength="10"/>
                </div>              
                </td>
                
                <td class="campo" nowrap>
                  <div id="sezioneMesiSosp" style="display:<%=displaySezioneSosp%>">
                  <af:textBox classNameBase="input" type="integer" name="numMesiSosp" value="<%=numMesiSospPrecInt%>" validateOnPost="true"
                              readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();"  size="4" maxlength="3"/>
                  </div>
                </td>
                
                <td class="etichetta" nowrap>
                <div id="sezioneCalcolaSosp" style="display:<%=displaySezioneSosp%>">
                &nbsp;&nbsp;+&nbsp;succ.
                 </div>
                </td>

              <td class="campo" nowrap>
              <div id="sezioneMesiSospApp1" style="display:<%=displaySezioneSosp%>">
              	<af:textBox classNameBase="input" type="integer" name="numMesiSospSucc" value="<%=sMesiSosp%>"  validateOnPost="true"
                	readonly="true" onKeyUp="fieldChanged();"  size="4" maxlength="3"/>
              </div>
              </td>
              
              <td nowrap>
                =
              </td>
              
              <td class="etichetta" nowrap>
 				<b><%=String.valueOf(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt)+Integer.parseInt(numMesiSospFornero2014Int)+mesiAggiuntiviSospensioni)%></b>
            	<input type="hidden" name="totNumMesiSosp" value="<%=String.valueOf(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt)+Integer.parseInt(numMesiSospFornero2014Int)+mesiAggiuntiviSospensioni)%>">
              </td>
              
              <% if ( !visualizzazione ) {%>
                  <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                  <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                  <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                  <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                  <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                  <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
              <% } %>
    </tr>
                 
	<tr>
    <td class="etichetta" nowrap>Mesi anz. complessivi</td>
     	<% 
      	visualizzazione = true;
      	String displaySezioneMesiAnzApp = "";
      	int calcoloMesiAnzComplessivi = Integer.parseInt(mesiAnzPrecInt)+Integer.parseInt(mesiAnzInt)-
      			(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt)+Integer.parseInt(numMesiSospFornero2014Int)+Integer.parseInt(infoMesiRischioDisocc)+mesiAggiuntiviSospensioni);
      	if (calcoloMesiAnzComplessivi > 0 && meseDiffAnzianitaGiorni > 0) {
      		calcoloMesiAnzComplessivi = calcoloMesiAnzComplessivi - meseDiffAnzianitaGiorni;
      	}
      	int mesiAnzComplessiviPostFornero = Integer.parseInt(mesiAnzFornero)-(Integer.parseInt(numMesiSospFornero2014Int)+Integer.parseInt(infoMesiRischioDisocc)+mesiAggiuntiviSospensioni);
      	if (mesiAnzComplessiviPostFornero > 0 && meseDiffAnzianitaGiorni > 0) {
      		mesiAnzComplessiviPostFornero = mesiAnzComplessiviPostFornero - meseDiffAnzianitaGiorni;		
      	}
      	%>
  		<td class="campo" nowrap>
		<div id="sezioneMesiAnzApp" style="display:<%=displayBottoneDate%>">			
		<af:textBox classNameBase="input" type="integer" name="numMesiAnzianitaApp" value="<%=mesiAnzInt%>"
                readonly="true" size="4" maxlength="3"/>
        <b>&nbsp;(e&nbsp;<%=giorniAnzDallaDataInt%>&nbsp;giorni)&nbsp;</b>
    	</div> 
  		</td>
      	
      	<td class="etichetta" nowrap align="left">
        <div id="sezioneDataAnz" style="display:<%=displaySezioneSosp%>">
            prima del&nbsp;<af:textBox classNameBase="input" type="date" name="datcalcoloanzianita" value="<%=Utils.notNull(datcalcoloanzianita)%>" validateOnPost="true"
                          readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();"  size="11" maxlength="10"/>
        </div>
        </td>
        
        <td class="campo" nowrap>
         <div id="sezioneMesiAnz" style="display:<%=displaySezioneSosp%>">
          <af:textBox classNameBase="input" type="integer" name="numAnzianitaPrec297" value="<%=mesiAnzPrecInt%>"  validateOnPost="true"
                      readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="4" maxlength="3"/>
          &nbsp;
          <%if(readOnlyStr){%>
          	<af:textBox classNameBase="input" type="text" name="CODMONOCALCOLOANZIANITAPREC297" value="<%=codMonoCalcAnzPrec%>" readonly="true" size="2" maxlength="3"/>
          <%}else{%>
	          <af:comboBox classNameBase="input" name="CODMONOCALCOLOANZIANITAPREC297" addBlank="false" onChange="fieldChanged();" disabled="<%=String.valueOf(readOnlyStr)%>">
	            <OPTION value="" <%if (codMonoCalcAnzPrec == null) out.print("SELECTED=\"true\"");%>></OPTION>
	            <OPTION value="A" <%if ((codMonoCalcAnzPrec!=null) && codMonoCalcAnzPrec.equalsIgnoreCase("A")) out.print("SELECTED=\"true\"");%>>A</OPTION>
	            <OPTION value="M" <%if ((codMonoCalcAnzPrec!=null) && codMonoCalcAnzPrec.equalsIgnoreCase("M")) out.print("SELECTED=\"true\"");%>>M</OPTION>
	          </af:comboBox>                    
	      <%}%>
          </div>
        </td>
        
        <td class="etichetta" nowrap>
        <div id="sezioneCalcolaAnz" style="display:<%=displaySezioneSosp%>">
        &nbsp;&nbsp;+&nbsp;succ.
        </div>
        </td>
        
       <td class="campo" nowrap>
		<div id="sezioneMesiAnzApp1" style="display:<%=displaySezioneSosp%>">
        <af:textBox classNameBase="input" type="integer" name="numMesiAnzianita" value="<%=mesiAnzInt%>" validateOnPost="true"
                    readonly="true" onKeyUp="fieldChanged();" size="4" maxlength="3"/>
        </div>
      </td>
      
      <td class="etichetta" nowrap>
        -&nbsp;sosp.&nbsp;<af:textBox classNameBase="input" type="integer" name="_totNumMesiSosp" value="<%=String.valueOf(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt)+Integer.parseInt(numMesiSospFornero2014Int)+mesiAggiuntiviSospensioni)%>"  validateOnPost="true"
                    readonly="true" onKeyUp="fieldChanged();"  size="4" maxlength="3"/>
         -&nbsp;rischio disocc.&nbsp;<af:textBox classNameBase="input" type="integer" name="_totNumMesiRischioDisocc" value="<%=infoMesiRischioDisocc%>"
                    readonly="true" size="4"/>&nbsp;=
      </td>
      
      <td class="etichetta" nowrap>
         <b><%=String.valueOf(calcoloMesiAnzComplessivi)%></b>
      	<input type="hidden" name="totNumMesiAnz" value="<%=String.valueOf(calcoloMesiAnzComplessivi)%>">
      	<b>&nbsp;(e&nbsp;<%=giorniAnzInt%>&nbsp;giorni)&nbsp;</b>
      </td>
      
      <% if ( !visualizzazione ) {%>
          <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
          <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
          <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
          <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
          <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
          <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
      <% } %>
     
     </tr>
     
     <tr>
     <td class="etichetta" nowrap>Mesi anzianità calcolati dal&nbsp;<%=dataFornero2014 %></td>
     <td class="campo" nowrap>
     	<b><%=mesiAnzFornero%></b>
     	<b>&nbsp;(e&nbsp;<%=giorniAnzDallaDataInt%>&nbsp;giorni)&nbsp;</b>
     </td>
     <td class="etichetta" nowrap>
        -&nbsp;sosp.
     </td>
     <td class="campo" nowrap>
     	<b><%=String.valueOf(Integer.parseInt(numMesiSospFornero2014Int) + Integer.parseInt(infoMesiRischioDisocc) + mesiAggiuntiviSospensioni)%></b>
     	<b>&nbsp;(e&nbsp;<%=String.valueOf(ggResiduoTotaleSospensioni)%>&nbsp;giorni)&nbsp;</b>
     </td>
     <td nowrap>
        =
     </td>
     <td class="etichetta" nowrap>
        <b><%=String.valueOf(mesiAnzComplessiviPostFornero)%></b>
        <b>&nbsp;(e&nbsp;<%=giorniAnzInt%>&nbsp;giorni)&nbsp;</b>
     </td>
     <%if (!flag_insert) {%>
    	&nbsp;<td><input class="pulsante" type="button" onclick="dettaglioAnzianita()" value="Dettaglio anzianità (ex legge Fornero)"></td>
     <%}%>
     </tr>
     
     </table>
     
     <%
     if (codStatoOccRagg != null && !readOnlyStr) {%>
    	<tr>
   		<td colspan="4" nowrap align="left">
   		<div id="btnMostraDate" style="display:<%=displayBottoneDate%>">
    	<table border="0" width="100%" class="main">
     	<tr><td align="left">
         <input class="pulsante" type="button" name="btnDisplaySezione"
         		value="Gestione manuale Sosp e Anzianità"
         	    onClick="VisualizzaSezioneDate();">
	     </td>
	     </tr>
	     </table>
	     </div>  
	     </td>
	     </tr>
     <% } %>
   
</table> <!-- fine tabella main per mesi sosp e mesi anz. -->

<table class="main" border="0"> 
<tr>
  <td>&nbsp;</td>
  <td class="campo">
    <%if(totMesiAnz != null && totMesiAnz.compareTo(new BigDecimal(24)) >= 0)  { %><!--mesiAnz-->
      <af:textBox classNameBase="input" type="text" name="tipoAnzianita" readonly="true"
                   value="soggetto alla legge 407/90" size="40"/>
    <% } %>
 </td>
</tr> 
<tr><td colspan=4>&nbsp;</td></tr>
<tr>
    <td colspan="4"><div class="sezione2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Categoria D.lgs. 181</div>
    </td>
</tr>
<%   if (cat181!=null && cat181.length()>0) {
		if (("giovane").equalsIgnoreCase(cat181)) {
			cat181 = cat181 + " (Min: Giovani)";
		}
		else if (("adolescente").equalsIgnoreCase(cat181)) {
			cat181 = cat181 + " (Min: Adolescenti)";
		}
		else if (("adulto").equalsIgnoreCase(cat181)) {
			cat181 = cat181 + " (Min: Adulto)";
		}
%>       
<tr>
    <td></td>
    <td colspan=3 class="campo"><af:textBox classNameBase="input" type="date" name="categoria181" value="<%=cat181%>" 
        required="false" readonly="true" size="40"/>
    </td>
</tr>
<%   }%>
<% if (disoccInoccText.length()>0) { %>
<tr>
    <td></td>
    <td colspan=3 class="campo">
            <af:textBox classNameBase="input" type="text" name="tipoDisoccupazione" readonly="true" 
                                 value="<%= disoccInoccText %>" size="40"/>
    </td>
</tr>
<%}%>

<%   if (donnaInReinserimento) {%>        
<tr>
    <td></td>
    <td colspan=3 class="campo"><af:textBox classNameBase="input" type="text" name="donnaInserimento" readonly="true" 
                             value="Donna in reinserimento lavorativo (Min: Donna in reinserimento)" size="70"/> 
    </td>
</tr>
<%   } %>
<tr><td colspan=4><br></td></tr>
<tr>
  <td class="etichetta">Indennità di disocc.</td>
    <td colspan="3" class="campo">
          <af:comboBox classNameBase="input" name="flgIndennizzato" addBlank="false" onChange="fieldChanged();" disabled="<%=String.valueOf(readOnlyStr)%>">
          <OPTION value=""  <%if (indennizzo == null) out.print("SELECTED=\"true\"");%>></OPTION>
          <OPTION value="S" <%if (indennizzo != null && indennizzo.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
          <OPTION value="N" <%if (indennizzo != null && indennizzo.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
        </af:comboBox>
    </td>
</tr>

</table>


<tr><td colspan=4><br></td></tr>
<tr><td colspan="4"><div class="sezione2"></div></td></tr>
<tr>
    <td class="etichetta">Pensionato.</td>
    <td colspan="3" class="campo">
        <af:comboBox classNameBase="input" name="flgPensionato" addBlank="false" onChange="fieldChanged();" disabled="<%=String.valueOf(readOnlyStr)%>">
            <OPTION value=""  <%if (pensionato == null) out.print("SELECTED=\"true\"");%>></OPTION>
            <OPTION value="S" <%if (pensionato != null && pensionato.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
            <OPTION value="N" <%if (pensionato != null && pensionato.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
        </af:comboBox>     
    </td>
</tr>

<tr>
  <td colspan="4">
  <div class="sezione2">
    <img id='imm1' alt="mostra/nascondi" src="../../img/chiuso.gif" onClick="onOff()"/>&nbsp;&nbsp;&nbsp;Revisione / Ricorso
  </div>
  </td>
</tr>
<tr><td colspan="4">
<div id="dett" style="display:none">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
 <tr>
   <td class="etichetta">Num. Atto</td>
   <td>
    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
      <tr><td width="25%">
          <af:textBox classNameBase="input" type="text" name="strNumAtto" value="<%=Utils.notNull(strNumeroAtto)%>"  
                      readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="18" maxlength="15"/></td>
          <td class="etichetta">Data Atto</td>
          <td><af:textBox classNameBase="input" type="date" name="datAtto" value="<%=dataAtto%>"  
                  readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
      </tr>
    </table>
   </td>
 </tr>
 <tr>
    <td class="etichetta">Stato Atto</td>
    <td colspan="3">
       <af:comboBox name="codStatoAtto" moduleName="M_GETSTATOATTO" selectedValue="<%=Utils.notNull(codStatoAt)%>"
                    classNameBase="input" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>"/>
    </td>
 </tr>
 <tr>
    <td class="etichetta">Data Revisione</td>
    <td colspan="3"><af:textBox classNameBase="input" type="date" name="datRichRevisione" value="<%=dataRichRevisione%>"  
                                readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
 </tr>
 <tr>
    <td class="etichetta">Data Ricorso</td>
    <td colspan="3"><af:textBox classNameBase="input" type="date" name="datRicorsoGiurisdz" value="<%=dataRicGiurisdz%>"  
                  readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
 </tr>
</table></div>
</td></tr>

<tr><td colspan="4"><div class="sezione2"/></td></tr>
<tr><td colspan="4">
  <table border="0" width="100%">
  <tr>
  <td class="etichetta">Note<br/></td>
  <td class="campo">
    <af:textArea classNameBase="textarea" name="strNote" value="<%=strNote%>"
                 cols="60" rows="4" maxlength="1000"
                 onKeyUp="fieldChanged();" readonly="<%=String.valueOf(readOnlyStr)%>"  />
  </td>
  </tr>
  </table>
</td></tr>
</table>
<!--
<%
out.print(htmlStreamBottom);
out.print(htmlStreamTop);
%>
-->
<table class="main">
<%if(!flag_insert){%>
<tr>
  <td width="33%"></td>
  <td width="33%" align="center">
  <%if(!readOnlyStr){%>
    <%keyLock= keyLock.add(new BigDecimal(1));%>
    <input class="pulsante" type="submit" name="saveStatoOcc" value="Aggiorna">
  <%}%>
  </td>
<td width="33%"></td>
</tr>
<tr><td><br/></td></tr>
<tr>
  <td width="33%"></td>
  <td align="center" width="33%">
   <%if(!readOnlyStr && canInsert)
     {%><input class="pulsante" type="button" onclick="insNuovo()"  value="Inserisci nuovo stato occ."><%}%>
     <input type="hidden" name="newRecord" value="1" disabled/>
  </td>
  <td width="33%" align="right">
    <%if(canDocAss) {%>
    <input class="pulsante" type="button" name="Documenti associati" value="Documenti associati"
           onClick="docAssociati('<%=cdnLavoratore%>','<%=_page%>','<%=_cdnFunz%>','','<%=Utils.notNull(prgStatoOccupaz)%>')">
    <%}%>
  </td>
  <!--
  <td align="right" width="33%">
  <%if(infStorButt){%>
    <input class="pulsante" type="button" name="infSotriche" value="Informazioni storiche"
           onClick="infoStoriche('StatoOccInfoStorPage','&cdnLavoratore=<%=cdnLavoratore%>')">
  <%}%>
  </td>
  -->
</tr>
<%} else if(canInsert) {%>
<tr>
   <td width="33%" align="center">
    <input class="pulsante" type="submit" name="insertRicalcola" value="Inserisci e Ricalcola" onClick="inserimentoStatoOcc('1');">
   </td>
   <td width="33%" align="center">
    <input class="pulsante" type="submit" name="insert" value="Inserisci" onClick="inserimentoStatoOcc('0');">
   </td>
   <td width="33%" align="right" nowrap>
   <%if(buttonAnnulla) {%>
      <input class="pulsante" type="button" name="annulla" value="Chiudi senza inserire"
             onClick="openPage('StatoOccupazionalePage','&cdnLavoratore=<%=cdnLavoratore%>&prgStatoOccupaz=<%=Utils.notNull(prgStatoOccupaz)%>&CDNFUNZIONE=<%=_cdnFunz%>')">
   <%}%>
   <!--
   &nbsp;&nbsp;&nbsp;
    <input class="pulsante" type="button" name="Documenti associati" value="Documenti associati"
           onClick="docAssociati('<%=cdnLavoratore%>','<%=_page%>','<%=_cdnFunz%>','','<%=Utils.notNull(prgStatoOccupaz)%>')">
    -->
   </td>
</tr>
<!--<tr><td><br/></td></tr>-->
<!--
<tr>
  <td colspan="3" align="right">
  <%if(infStorButt){%>
    <input class="pulsante" type="button" name="infSotriche" value="Informazioni storiche"
           onClick="infoStoriche('StatoOccInfoStorPage','&cdnLavoratore=<%=cdnLavoratore%>')">
  <%}%>
  </td>
</tr>
-->
<%}//else if(canInsert)%>
<tr><td><br/></td></tr>
<!--
<tr>
  <td colspan="3" align="right">
    <input class="pulsante" type="button" name="Documenti associati" value="Documenti associati"
           onClick="docAssociati('<%=cdnLavoratore%>','<%=_page%>','<%=_cdnFunz%>')">
  </td>
</tr>
-->
<tr>
  <td colspan="4" align="right">
  <%if(infStorButt){%>
    <input class="pulsante<%=((storicoSocc)?"":"Disabled")%>" type="button" name="infSotriche" value="Informazioni storiche"
           onClick="infoStoriche('StatoOccInfoStorPage','&cdnLavoratore=<%=cdnLavoratore%>')"
           <%=(!storicoSocc)?"disabled=\"True\"":""%>>
  <%}%>
  </td>
</tr>
</table>
<%out.print(htmlStreamBottom);

if (operatoreInfo!=null) {
%>
  <center><table><tr><td align="center">
<%
  operatoreInfo.showHTML(out); 
%>
  </td></tr></table></center>
<%
}
%>
<input type="hidden" name="PAGE" value="StatoOccupazionalePage"/>
<input type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
<input type="hidden" name="keyLockStatoOcc" value="<%=Utils.notNull(keyLock)%>"/>
<input type="hidden" name="prgStatoOccupaz" value="<%=Utils.notNull(prgStatoOccupaz)%>"/>
<input type="hidden" name="mesiSospFornero2014" value="<%=Utils.notNull(infoSospensioneFornero)%>"/>
<input type="hidden" name="mesi_rischio_disocc" value="<%=Utils.notNull(infoMesiRischioDisocc)%>"/>
<%if (confirmContinuaRicalcolo || confirmContinuaRicalcoloSOccPrec297) {%>
<input type="hidden" name="nuovoRaggStatoOccupaz" value="<%=codStatoOccRagg%>"/>
<%}else {%>
<input type="hidden" name="nuovoRaggStatoOccupaz" value=""/>
<%}
if (flag_insert && canInsert) {%>
	<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="<%=continuaRicaloloSOcc%>">
	<input type="hidden" name="CONTINUA_CALCOLO_SOCC_PREC_297" value="<%=continuaRicaloloSOccPrec297%>">
	<input type="hidden" name="OPERAZIONESCELTA" value="<%=operazioneScelta%>">
<%}
if (confirmContinuaRicalcolo || confirmContinuaRicalcoloSOccPrec297) {
	if (operazioneScelta.equals("1")) {%>
		<input type="hidden" name="insertRicalcola" value="">
	<%}else { 
		if (operazioneScelta.equals("0")) {%>
			<input type="hidden" name="insert" value="">
		<%}
	}
}%>

</af:form>

</body>
</html>