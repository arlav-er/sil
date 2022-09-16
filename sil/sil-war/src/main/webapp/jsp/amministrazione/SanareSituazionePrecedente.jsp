<%--

    Moduli utilizzati : M_GetInfoSanamentoSituazioneAmministrativa. Contiene piu' result set
    
--%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.PageAttribs,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.text.SimpleDateFormat,
                  com.engiweb.framework.security.*,
                  it.eng.sil.module.movimenti.GraficaUtils,
                  it.eng.sil.util.amministrazione.impatti.*,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
/////////////////////////////////
    String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    String forzaInserimento = serviceRequest.containsAttribute("FORZA_INSERIMENTO")?serviceRequest.getAttribute("FORZA_INSERIMENTO").toString():"false";
    String continuaRicalcoloSOcc = serviceRequest.containsAttribute("CONTINUA_CALCOLO_SOCC")?serviceRequest.getAttribute("CONTINUA_CALCOLO_SOCC").toString():"false";
    String urlDiLista = null;
    PageAttribs attributi = new PageAttribs(user,(String) serviceRequest.getAttribute("PAGE"));
    boolean canModify = attributi.containsButton("aggiorna");    
    int movInizioLista = 0;
    String dataInizioMovimentoDid = "";
    /**/
	ProfileDataFilter filter = new ProfileDataFilter(user, (String) serviceRequest.getAttribute("PAGE"));
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));


	boolean readOnlyStr     = true;
	boolean canInsert       = false;
	boolean infStorButt     = false;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
        infStorButt = attributi.containsButton("INF_STOR");
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
    }
    ///////
    canModify = !readOnlyStr;
    ///////////*canModify = true;*/
    //
    boolean confirm = false, erroreGenerico= false;
    int errorCode = -1;
    boolean confirmUscitaMobilita = false;
    boolean confirmContinuaRicalcolo = false;

    //////
//    String readOnlyStr   =canModify?"false":"true";
    readOnlyStr = !canModify;
    
    
/////////////////////////////////
    String htmlStreamTop = StyleUtils.roundTopTable(canModify);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

/////////////////////////////////
    SourceBean rowContainer = (SourceBean)serviceResponse.getAttribute("M_GetInfoSanamentoSituazioneAmministrativa");
    Vector dispoStoricizzate = rowContainer.getAttributeAsVector("GET_DISPO_STORICIZZATE.ROWS.ROW");
    Vector dispoValida = rowContainer.getAttributeAsVector("GET_DISPO_VALIDA.ROWS.ROW");
    Vector statiOccupazionali = rowContainer.getAttributeAsVector("GET_STATI_OCC.ROWS.ROW");
    Vector pattoAperto = rowContainer.getAttributeAsVector("GET_PATTO_APERTO.ROWS.ROW");
    Vector pattiStoricizzati = rowContainer.getAttributeAsVector("GET_PATTI_STORICIZZATI.ROWS.ROW");
    Vector movimenti = rowContainer.getAttributeAsVector("GET_MOVIMENTI.ROWS.ROW");
    SourceBean []movimentiDaRicaricare = (SourceBean[])serviceResponse.getAttribute("M_SANASITUAZIONEAMMINISTRATIVA.movimenti_ricostruiti");
    String result = (String)serviceResponse.getAttribute("M_SANASITUAZIONEAMMINISTRATIVA.RESULT");
/////////////////////////////////
    String dataPattoAperto = null, statoOccupazionale = null, statoOccupazionaleRagg = null, dataSitSanata = null,
    tipoDichiarazione = "gen", limite="inf", checkSelezionato = null, codTipoPatto = null,
    dataPattoChiuso = null, dataRichiestaRevisione=null, dataRicorsoGiurisdizionale=null, 
    revisione=null, dataDID=null,  statoOccAssDIDSt=null, note=null,
    descStatoOccAssDIDSt=null, dataPattoStorico=null, dataDIDStoricizzata=null, codMotivoFineAtto=null;
    SourceBean movInizioDid = null;
    boolean statiSospensioneAnzianita=false;
    boolean mostraRisultati = result!=null && result.equals("OK");
    BigDecimal prgDichDisponibilita=null, numKloDichDisp=null;
    BigDecimal prgStatoOccupazDID = null;
    SourceBean statoOccupazSospensione=null;
    String numMesiSosp = "";
    String prgMovimentoSupLimite = Utils.notNull(serviceRequest.getAttribute("PRGMOVIMENTOSUPLIMITE"));
    
    if (serviceRequest.containsAttribute("datSitSanata"))
    	dataSitSanata = (String)serviceRequest.getAttribute("datSitSanata");
    else dataSitSanata = DateUtils.getNow();
    note =(String) serviceRequest.getAttribute("strNote");
    //
    if (serviceRequest.containsAttribute("tipoDichiarazione")) {
    	tipoDichiarazione = Utils.notNull(serviceRequest.getAttribute("tipoDichiarazione"));
    	limite = Utils.notNull(serviceRequest.getAttribute("limite"));
    	checkSelezionato = Utils.notNull(serviceRequest.getAttribute("checkSelezionato"));
    }
    
    //
    if (dispoValida!=null && dispoValida.size()>0) {
        SourceBean did = (SourceBean )dispoValida.get(0);
        dataDID = (String)did.getAttribute("datDichiarazione");
        prgDichDisponibilita = (BigDecimal)did.getAttribute("prgDichDisponibilita");
        numKloDichDisp = (BigDecimal)did.getAttribute("numKloDichDisp");
        prgStatoOccupazDID = (BigDecimal)did.getAttribute("prgStatoOccupaz");
        //statoOccupazionale = (String)did.getAttribute("codStatoOccupaz");
    }
    else {
        if (dispoStoricizzate!=null && dispoStoricizzate.size()>0) {
            SourceBean didStorica = (SourceBean)dispoStoricizzate.get(0);
            dataDIDStoricizzata = (String)didStorica.getAttribute("DATDICHIARAZIONE");
            statoOccAssDIDSt = (String)didStorica.getAttribute("CODSTATOOCCUPAZ");
            descStatoOccAssDIDSt = (String)didStorica.getAttribute("stoccdescrizione");
            codMotivoFineAtto = (String)didStorica.getAttribute("codMotivoFineAtto");
            prgDichDisponibilita = (BigDecimal)didStorica.getAttribute("prgDichDisponibilita");
            numKloDichDisp = (BigDecimal)didStorica.getAttribute("numKloDichDisp");
        }
    }
    if (pattoAperto !=null && pattoAperto.size()>0) {
        SourceBean patto = (SourceBean)pattoAperto.get(0);
        dataPattoAperto = (String )patto.getAttribute("datStipula");
        codTipoPatto = (String)patto.getAttribute("CODSTATOATTO");
    }
    if (pattiStoricizzati!=null && pattiStoricizzati.size()>0) {  
    	 SourceBean patto = (SourceBean)pattiStoricizzati.lastElement();
         String dataStipula = (String )patto.getAttribute("datStipula");
         dataPattoStorico = dataStipula;        
    }
    if (statiOccupazionali!=null && statiOccupazionali.size()>0) {
        SourceBean statoOcc = (SourceBean)statiOccupazionali.lastElement();
        statoOccupazionale = (String)statoOcc.getAttribute("codStatoOccupaz");
        statoOccupazionaleRagg = (String)statoOcc.getAttribute("codStatooccupazRagg");
        dataRicorsoGiurisdizionale = (String)statoOcc.getAttribute("datricorsogiurisdiz");
        dataRichiestaRevisione = (String)statoOcc.getAttribute("datRichRevisione");
        numMesiSosp = (statoOcc.getAttribute("numMesiSosp")!=null) ? statoOcc.getAttribute("numMesiSosp").toString() : "0";
        boolean flag = false;
        for (int i=0;i < statiOccupazionali.size();i++) {
            statoOcc = (SourceBean)statiOccupazionali.get(i);
            if (flag) {
            	String codStatoOcc = (String)statoOcc.getAttribute("codStatoOccupaz");
	            if (codStatoOcc.equals("B1")) {
	                statiSospensioneAnzianita=true;	
	                statoOccupazSospensione = statoOcc;
	                break;
	            }
            }
            else {
		       	BigDecimal prgStatoOccupaz = (BigDecimal)statoOcc.getAttribute("prgStatoOccupaz");
		       	if (prgStatoOccupazDID ==null || prgStatoOccupaz.intValue()==prgStatoOccupazDID.intValue()) {
		       		flag = true;
		       		i--;
		       	}
           	}            
        }
    }
    boolean trovatoSucc = false;
    String dataRif = null; //data dell'eventuale DID
    if (dataDID!=null) dataRif = dataDID;
      	else if (dataDIDStoricizzata!=null) dataRif = dataDIDStoricizzata;
    for (int i=0;i<movimenti.size();i++) {
		SourceBean movimento = (SourceBean)movimenti.get(i);
		String dataInizio = (String)movimento.getAttribute("datInizioMov");		    		
		String dataFine = (String)movimento.getAttribute("datFineMovEffettiva");	
		String codTipoMov = (String)movimento.getAttribute("CODTIPOMOV");
        String codStatoAtto = (String)movimento.getAttribute("CODSTATOATTO");
        String codMonoTipoAss = Utils.notNull(movimento.getAttribute("codMonoTipo"));
        String codTipoAvviamento = Utils.notNull(movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE));
        /**  Seleziono soltanto i movimenti di AVV|PRO|TRA validi, ovvero protocollati  */
        if (!(!Utils.notNull(codTipoMov).equals("CES") && Utils.notNull(codStatoAtto).equals("PR")))
        	continue;                        	    		
        //non considerare I TIROCINI (CODMONOTIPO) = T), TIPO AVVIAMENTO Z.09.02 (VECCHIO CODICE RS3)
		//(cessazione attività lavorativa dopo un periodo di sospeso per contrazione), non possono essere sanati
        if (codMonoTipoAss.equalsIgnoreCase("T") || codTipoAvviamento.equalsIgnoreCase("Z.09.02")) {
        	continue;
        }
        	
        if (Utils.notNull(codTipoMov).equals("AVV")) {
			if (dataRif!=null && (DateUtils.compare(dataRif, dataInizio)<=0 || 
			   (DateUtils.compare(dataRif, dataInizio)>0 && (dataFine==null || DateUtils.compare(dataRif, dataFine)<=0)))) {
				// il movimento e' un movimento aperto nel periodo di stipula della did		
				movInizioDid = movimento;
				dataInizioMovimentoDid = movInizioDid.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			}
			else {
				BigDecimal prgMovSucc = (BigDecimal)movimento.getAttribute("prgMovimentoSucc");
	        	while (prgMovSucc != null) {
	        		trovatoSucc = false;
	        		for (int j=0;j<movimenti.size();j++) {
	        			SourceBean movimentoSucc = (SourceBean)movimenti.get(j);
	        			BigDecimal prgMovCorrente = (BigDecimal)movimentoSucc.getAttribute("prgMovimento");
	        			if (prgMovCorrente.equals(prgMovSucc)) {
	        				trovatoSucc = true;
	        				dataInizio = (String)movimentoSucc.getAttribute("datInizioMov");		    		
							dataFine = (String)movimentoSucc.getAttribute("datFineMovEffettiva");
							if (dataRif!=null && (DateUtils.compare(dataRif, dataInizio)<=0 || 
							   (DateUtils.compare(dataRif, dataInizio)>0 && (dataFine==null || DateUtils.compare(dataRif, dataFine)<=0)))) {
								// il movimento e' un movimento aperto nel periodo di stipula della did		
								movInizioDid = movimento;
								dataInizioMovimentoDid = movInizioDid.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
								prgMovSucc = null;
								break;
							}
							else {
								prgMovSucc = (BigDecimal)movimentoSucc.getAttribute("prgMovimentoSucc");
							}
	        			}
	        		}
	        		if (!trovatoSucc) {
	        			prgMovSucc = null;
	        		}
	        	}
	       	}
	       	if (movInizioDid != null) {
	       		break;
	       	}
        }
        else {	
	       	if (dataRif!=null && (DateUtils.compare(dataRif, dataInizio)<=0 || 
			(DateUtils.compare(dataRif, dataInizio)>0 && (dataFine==null || DateUtils.compare(dataRif, dataFine)<=0)))) {
				// il movimento e' un movimento aperto nel periodo di stipula della did		
				movInizioDid = movimento;
				dataInizioMovimentoDid = movInizioDid.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
				break;
			}
		}
	}

    // ricerca primo movimento aperto nel periodo di stipula della did

    // ricerca primo movimento non sanato
    for (int i=movimenti.size()-1;i>=0;i--) {
    	SourceBean m = (SourceBean)movimenti.get(i);
    	if (m.getAttribute("codTipoMov").equals("CES") || 
    		!m.getAttribute("codStatoAtto").equals("PR")) 
    		continue;
    	String dataInizioSit = (String)m.getAttribute("datSitSanata");
    	if (dataInizioSit!=null && !dataInizioSit.equals("")) {
    		movInizioLista = i+1;
    		break;
    	}
    }
    boolean revisioneORichiesta = (Utils.notNull(dataRicorsoGiurisdizionale).length()>0 ||
    					Utils.notNull(dataRichiestaRevisione).length()>0);
    boolean blocca = /*(dataDID==null && dataDIDStoricizzata==null) || */(  					
    				(statoOccupazionaleRagg!=null && revisioneORichiesta && statoOccupazionaleRagg.equals("O")) 
    				|| (statoOccupazionaleRagg!=null && statoOccupazionaleRagg.equals("I"))
    				|| (statoOccupazionaleRagg!=null && revisioneORichiesta && statoOccupazionaleRagg.equals("A") && 
    				   (statoOccupazionale!=null && statoOccupazionale.equals("C") || statoOccupazionale.equals("C0") 
    					|| statoOccupazionale.equals("D") || statoOccupazionale.equals("E") ||
    					statoOccupazionale.equals("F"))));
    					
    canModify = canModify && !blocca && !mostraRisultati;
    
    /////////////
        Iterator errors = responseContainer.getErrorHandler().getErrors().iterator();
    while (errors.hasNext()) {
    	com.engiweb.framework.error.EMFUserError error = (com.engiweb.framework.error.EMFUserError)errors.next();
    	errorCode = error.getCode();
			if (errorCode == MessageCodes.StatoOccupazionale.REDDITO_INFERIORE_OP_SANARE ) {
				confirm = true;
				limite = "inf";
			}
			else {
				if (errorCode ==MessageCodes.StatoOccupazionale.REDDITO_SUPERIORE_OP_SANARE ||
				 		errorCode ==MessageCodes.DID.REDDITO_SUPERIORE_LIMITE) {
					confirm = true;
					limite = "sup";
				}
				else { 
					if (errorCode == MessageCodes.Mobilita.USCITA_MOBILITA ||
					    errorCode == MessageCodes.Mobilita.MOVIMENTO_NON_COMPATIBILE_CON_MOBILITA) {
						confirmUscitaMobilita = true;
					}
					else {
						if (errorCode == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE ||
							errorCode == MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE ||
							errorCode == MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI ||
							errorCode == MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA) {
							confirmContinuaRicalcolo = true;
						}
						else {
							erroreGenerico = true;
						}
					}
				}
		 	}
		
    }
    //Variabili per la gestione della protocollazione ================
	String prAutomatica     = null; 
	String estReportDefautl = null;
	BigDecimal numProt     = null;
	BigDecimal annoProt = null;
	String     dataProt     = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
	String     oraProt     = (new SimpleDateFormat("HH:mm")).format(new Date());
	String     docInOrOut     = "";
	String     docRif       = "";
	boolean numProtEditable = false;
	Vector rowsPR           = null;
	SourceBean rowPR        = null;
	String     CODSTATOATTO = "";

	SourceBean resp = (SourceBean) serviceResponse.getAttribute("GetDichProtocollato.ROWS.ROW");
	if(resp != null) { 
		dataProt = (String) resp.getAttribute("DATAPROT");
		oraProt = (String) resp.getAttribute("ORAPROT");
		annoProt =  (BigDecimal) resp.getAttribute("NUMANNOPROT");
		numProt = (BigDecimal) resp.getAttribute("NUMPROTOCOLLO"); 
		CODSTATOATTO = (String) resp.getAttribute("CODSTATOATTO");
		docRif = (String) resp.getAttribute("RIF");
		docInOrOut = (String) resp.getAttribute("CODMONOIO");
	}
	else {
		rowsPR = serviceResponse.getAttributeAsVector("GetDichDaProtocollare.ROWS.ROW");
		rowPR = (SourceBean) rowsPR.elementAt(0);
		prAutomatica     = (String) rowPR.getAttribute("FLGPROTOCOLLOAUT");
		if ( prAutomatica.equalsIgnoreCase("N") ) { 
			numProtEditable = true; 
		}
		else { 
			numProt          = (BigDecimal) rowPR.getAttribute("NUMPROTOCOLLO");
		}
		annoProt      = new BigDecimal(dataProt.substring(6,10));
		estReportDefautl = (String) rowPR.getAttribute("CODTIPOFILEESTREPORT");
		docInOrOut = (String) rowPR.getAttribute("STRIO");
		docRif = (String) rowPR.getAttribute("RIF");
		CODSTATOATTO = "PA";
	}
	
	
    
   String codRif= user.getCodRif();
   
   //Variabile per la visualizzazione di tutti i movimenti
   String viewMovimenti = StringUtils.getAttributeStrNotNull( serviceRequest,"viewTuttiMovimenti"); 
   int viewTuttiMovimenti = 0;
   
   if( !viewMovimenti.equals("") ) {
   	viewTuttiMovimenti = Integer.parseInt( viewMovimenti );
   }
   else {
    viewMovimenti = "0";  
   }

%>

<html>
<head>
<title>Stato Occupazionale</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>


<%@ include file="CommonScript.inc"%>
<script language="Javascript" src="../../js/docAssocia.js"></script>
<!-- include lo script che permette l'apertura dei documenti associati -->
<script>
<!--
    // stato occupazionale aperto
    statoOccupazionale =  "<%=Utils.notNull(statoOccupazionale)%>";    
    // stato occupazionale di raggruppamento aperto
    statoOccupazionaleRagg = "<%=Utils.notNull(statoOccupazionaleRagg)%>";    
    decadutoPerRichiestaDelLavoratore = <%=Utils.notNull(codMotivoFineAtto).equals("A10")%>;
    // bisogna leggere tutti gli stati occupazionali letti per controllare 
    sospensioneAnzianita =  <%=statiSospensioneAnzianita %>;
    ricorsoGiurisdizionale =  <%=Utils.notNull(dataRicorsoGiurisdizionale).length()>0 %>;
	dataPatto = "<%=Utils.notNull(dataPattoAperto).equals("")? Utils.notNull(dataPattoStorico):Utils.notNull(dataPattoAperto)%>";
    revisione =  <%=Utils.notNull(dataRichiestaRevisione).length()>0 %>;    
    // nel periodo che si vuole sanare
    esistenzaPatto =  <%=!Utils.notNull(dataPattoStorico).equals("") || !Utils.notNull(dataPattoAperto).equals("") %>;    
    esistenzaDID = <%=dataDID!=null || dataDIDStoricizzata!=null %>
    mostraRisultati = <%=mostraRisultati%>;
    // il campo in questione e' codStatoOccupaz codice Altro XX
        <%
            boolean  decaduto = false;
            codMotivoFineAtto = Utils.notNull(codMotivoFineAtto);
            if (codMotivoFineAtto.equals("A13")
	           || codMotivoFineAtto.equals("A7") || codMotivoFineAtto.equals("A8")
	           || codMotivoFineAtto.equals("A9") || codMotivoFineAtto.equals("GI")
               || codMotivoFineAtto.equals("A11") || codMotivoFineAtto.equals("PU")
               || codMotivoFineAtto.equals("TD")
               || codMotivoFineAtto.equals("CC") || codMotivoFineAtto.equals("UE"))                    
                 decaduto = true;
            if (codMotivoFineAtto.equals("A10")) decaduto = false; // su richiesta dell' interessato puo' passare
        %>
    decadutoDallaDisoccupazione =  <%=decaduto%>;
    
    
    function obbligoRichiestaRiesame(mostraAlert) {
    <%-- nel caricamento della pagina dopo l'esecuzione con successo dell'operazione non bisogna mostrare l'alert --%>
    	if (mostraRisultati) return false;
        if ((statoOccupazionaleRagg=='O'  || statoOccupazionaleRagg=='A')&& decadutoDallaDisoccupazione 
        		&&!decadutoPerRichiestaDelLavoratore) {
            if (!isSuperamentoLimite()) {
            	if (mostraAlert) {
	                //alert(RICHIESTA_RIESAME);
	                alert("Attenzione, lo stato occupazionale di DECADUTO non permette di riacquisire lo stato di disoccupazione tramite la dichiarazione reddituale.");
    	            //setSuperamentoLimite(true);                
    	        }
            }
        }
        return false;
    }


 
    /* controlla se e' possibile sanare la situazione precedente */
    function operazionePossibile(mostraAlert) {
    	if (mostraRisultati) return true;
    	
    	if (statoOccupazionale=='A22') {
    		if (mostraAlert) alert("Il lavoratore e' inoccupato: non deve sanare alcuna situazione amministrativa");
    		return false;
    	}
    	if ((statoOccupazionale=='C' || statoOccupazionale=='C0' || statoOccupazionale=='D' || 
    			statoOccupazionale=='E' || statoOccupazionale=='F') && (ricorsoGiurisdizionale || revisione)) {
    		if (mostraAlert) alert("Esiste un riesame in corso, non e' possibile sanare la situazione precedente prima dell' esito.");    	    	
    		return false;
    	}
		if (statoOccupazionale=='C1'  && (!ricorsoGiurisdizionale || !revisione) && decadutoPerRichiestaDelLavoratore)
			;
		else {
			if (statoOccupazionale=='C1' || statoOccupazionale=='C11' || statoOccupazionale=='C14' || 
	    		statoOccupazionale=='C13' || statoOccupazionale=='C12') {
    		if (mostraAlert) alert("Attenzione, lo stato occupazionale di DECADUTO non permette di riacquisire lo stato di disoccupazione tramite la dichiarazione reddituale.");
    		return true;
    		}
    	}
        if ( statoOccupazionaleRagg=='O' 
            &&( revisione || ricorsoGiurisdizionale )) {
                if (mostraAlert) alert("Esiste un riesame in corso, non e' possibile sanare la situazione precedente prima dell' esito.");                
                return false;
        }
        if (!sospensioneAnzianita && statoOccupazionaleRagg=='D' && !isSuperamentoLimite()) {
        	//tipoDich = document.getElementsByName("tipoDichiarazione");
			if (!isSuperamentoLimite) {				
				if (mostraAlert) {
					if (confirm("Non esistono stati di sospensione di anzianità. Si vuole comunque dichiarare il dettaglio del mancato superamento del limite?")){
						setDichiarazioneGenerica(false);
						impostaDati();
					}
					else {
						setSuperamentoLimite(true);
						impostaDati();
						return false;
						//alert("Non esistono stati di sospensione di anzianità per cui non e' necessario dichiarare il mancato superamento del limite");
					}
				}
				else {
					;
				}								
			}
			        
		}
		if (obbligoRichiestaRiesame(false))
			return false;
        return true;     
    }

	function chiudi_ins(){
		<%
		if (sessionContainer!=null){
		    String token = "_TOKEN_" + "ListaDichRedPage";
			urlDiLista = "AdapterHTTP?" + (String)sessionContainer.getAttribute(token.toUpperCase());}		
		%>
		setWindowLocation("<%=urlDiLista%>");
	}
    
    var dataInizioGestione ; // e' la data dalla quale si parte, ovvero la data inizio del movimento scelto 
    var dataStipulaPatto ; // e' la data della stipula del patto, nel caso esista
    var decaduto ; // variabile booleana; lo stato occupaz. chiuso era con codice altro.....

//-->
</script>
<script>
<!--
ESISTENZA_PATTO = "Esiste un patto <%=Utils.notNull(codTipoPatto).equals("PR")?"protocollato":"non protocollato"%> nel periodo che si vuole sanare. Proseguire?";    
RICHIESTA_RIESAME="Dichiarazione mancato superamento del limite del reddito non ammessa perchè il soggetto risulta/risultava decaduto (motivo fine atto): e' possibile soltanto chiedere un riesame dell'atto entro 10 GG dalla notifica";

function invia(mostraAlert) {
    if (!controlloPatto()) 
    		return false;
    if (!operazionePossibile(mostraAlert))  return false;
    if (!setCheckSelezionato()) return false;
    return true;
}
function controlloPatto() {
	if (esistenzaPatto && (statoOccupazionaleRagg=='D' || statoOccupazionaleRagg=='I')) {
    	if (!confirm(ESISTENZA_PATTO))
    		return false; 
    }
    return true;
}
function setCheckSelezionato() {
	dataDaCuiPartire = getDataInizioPeriodoDaSanare();
    if (dataDaCuiPartire=="") return false;
    
    if (isSuperamentoLimite()) {
	    document.Form1.checkSelezionato.value = getRigaCheckSelezionato();
	    document.Form1.PRGMOVIMENTOSUPLIMITE.value = getPrgRigaCheckSelezionato();
	}
	else {
		if (statoOccupazionaleRagg!='O')
		    document.Form1.checkSelezionato.value = movimentoLegatoSospensione;
		else if (movimentoLegatoSospensione!="")
			document.Form1.checkSelezionato.value = movimentoLegatoSospensione;
		else document.Form1.checkSelezionato.value = 0;
	}
	return true;
}
/*
function disabilitaCheckDa(x){    
}
*/
function setSuperamentoLimite(f) {
	x=0;
	if (f)
		x=1;
	o = document.getElementsByName("limite");
	o[x].checked=true;	
}
function isSuperamentoLimite() {
	o = document.getElementsByName("limite");
	return o[1].checked;
}
function isDichiarazioneGenerica() {
	o = document.getElementsByName("tipoDichiarazione");
	return o[1].checked;
}
function getDataInizioPeriodoDaSanare() {

	if (listaRetribuzioni.length==0){
		alert("Non esistono movimenti nel periodo da sanare");
		return "";
	}
	if (isDichiarazioneGenerica()&& !isSuperamentoLimite()) {
		riga = 0;
	}
	else if (isDichiarazioneGenerica()&& isSuperamentoLimite()) {
		riga = getRigaCheckSelezionato();
	}
	else {
		riga = getRigaPrimoMovimentoSanato();

	}
	if (riga==-1) {
		if (isDichiarazioneGenerica())
			alert("Non esistono movimenti da sanare");
		else
			alert("E' necessario immettere/cambiare almeno una retribuzione da sanare o selezionare un movimento");
		return "";
	}
	return document.getElementById("dataInizio_" + riga).value;
}
function disabilitaCheck(x) {	
    var isMovSelezionato = false;
    if (x.charAt(0)=='A') {
	    for (i=0;i<listaCheck.length;i++) {
	        if (listaCheck[i].id != x) {
                listaCheck[i].disabled = true;
                if (isMovSelezionato)
                    listaCheck[i].checked = true;
            }
            else {
            	isMovSelezionato = true;
            }
	    }
    }
    else {
    	for (i=0;i<listaCheck.length;i++) {
    		if (listaCheck[i].id != x) listaCheck[i].disabled = true;
	    }
    }
}
function abilitaRetribuzioneDa(x) {	
	j = 0;
	for (i=0;i<listaCheck.length;i++) {
		if (listaCheck[i].id == x) {
			j = i;
			break;
		}
	}
	if (x.charAt(0)=='A') {
		for (i=j;i<listaRetribuzioni.length;i++) {
	        listaRetribuzioni[i].disabled = false;
	    }
    }
    else {
    	for (i=j;i<listaReddito.length;i++) {
	        listaReddito[i].disabled = false;
	    }
    }
}
function riabilitaTuttiCheck(x) {
	if (x.charAt(0)=='A') {
	    for (i=0;i<listaCheck.length;i++) {
	        listaCheck[i].disabled = false;
            listaCheck[i].checked = false;
	    }
    }
    else {
    	for (i=0;i<listaCheck.length;i++) {
	        listaCheck[i].disabled = false;
	    }
    }
}
function disabilitaRetribuzioni(x) {
	if (x.charAt(0)=='A') {
	    for (i=0;i<listaRetribuzioni.length;i++) {
	        listaRetribuzioni[i].disabled = true;
	    }
    }
    else {
    	for (i=0;i<listaReddito.length;i++) {
	        listaReddito[i].disabled = true;
	    }
    }
}
function abilitaRetribuzioniSanate() {
	for (i=0;i<listaRetribuzioniSanate.length;i++) {
		listaRetribuzioniSanate[i].disabled = false;
		listaRetribuzioniSanate[i].readonly = false;
		listaRetribuzioniSanate[i].className = "";
	}
}
function disabilitaRetribuzioniSanate() {
	for (i=0;i<listaRetribuzioniSanate.length;i++) {
		listaRetribuzioniSanate[i].disabled = true;
		listaRetribuzioniSanate[i].readonly = false;
		listaRetribuzioniSanate[i].className = "inputView";
	}
}
function nascondiTRpreDid(){
	for (i=0;i<listaTR.length;i++) {
		listaTR[i].style.display="none";
	}
	listaTRAperta = false;
}
function mostraTRpreDid(){
	for (i=0;i<listaTR.length;i++) {
		listaTR[i].style.display="";
	}
	listaTRAperta = true;
}
function nascondiCheck() {
	for (i=0;i<listaCheck.length;i++) {
		listaCheck[i].style.display = "none";
		listaCheck[i].checked = false;
		listaCheck[i].disabled = false;
	}
	checkSelezionato = null;
	document.Form1.PRGMOVIMENTOSUPLIMITE.value = "";
}
function mostraCheck() {
	for (i=0;i<listaCheck.length;i++)
		listaCheck[i].style.display = "";
}
function mostraEtichetta(){
	document.getElementById("etichetta_dichiarazione").style.display="";
}
function nascondiEtichetta(){
	document.getElementById("etichetta_dichiarazione").style.display="none";
}
/**
 * 
 */
function getRigaCheckSelezionato() {
	ret =-1;
	try {
	for (i=0;i<listaCheck.length;i++) {    	
	   if (listaCheck[i].checked) {
		   ret =i;
	       break;	   
	   }
	}	
	}catch(e) {
		alert(i + ":" + e);
	}
	return ret;
}

function getPrgRigaCheckSelezionato() {
	ret =-1;
	try {
	for (i=0;i<listaCheck.length;i++) {    	
	   if (listaCheck[i].checked) {
		   ret = listaCheck[i].id.substring(1);
	       break;	   
	   }
	}	
	}catch(e) {
		alert(i + ":" + e);
	}
	return ret;
}

function getRigaPrimoMovimentoSanato() {
	ret = -1;;
	for (i=0;i<listaRetribuzioni.length;i++) {
		if (listaRetribuzioni[i].value != listaRetribuzioniSanate[i].value) {			
			ret =i;
			break;
		}
	}
	return ret;
}
function conferma() {
	<% 	int op = 0;
		String messaggioConfirm = null;
		if (confirm) {
			if (limite.equals("sup")) {
				if(errorCode ==MessageCodes.DID.REDDITO_SUPERIORE_LIMITE) {
					messaggioConfirm = "Reddito superiore al limite: la dichiarazione di immediata disponibilità se aperta in ricostruzione della storia potrebbe venire chiusa: proseguire con la dichiarazione di superamento del limite?";
					op = 1;
				}
				else {
					messaggioConfirm = "Reddito superiore al limite. Proseguire con la dichiarazione di superamento del limite?";					
					op = 0;
				}
			}
			else {
				messaggioConfirm = "Reddito inferiore al limite. Proseguire con la dichiarazione di mancato superamento del limite?";
				op = 0;
			}
	%>
		if (<%=limite.equals("sup")%> && !isSuperamentoLimite()){
			alert("Reddito superiore al limite: impossibile proseguire con la dichiarazione di superamento del limite");
			return;
		}
		if (<%=limite.equals("inf")%> && isSuperamentoLimite()){
			alert("Reddito inferiore al limite: impossibile proseguire con la dichiarazione di mancato superamento del limite");
			return;
		}			
		if (confirm("<%=messaggioConfirm%>") && invia(false)) ripetiOperazione(<%=op%>); 
	<%} 
	  else {
	  	if (confirmUscitaMobilita) {
	  		messaggioConfirm = "Esistono movimenti a tempo indeterminato in periodi di mobilità. Proseguire con la dichiarazione?";
	  		op = 1;
	  		%>
	  		if (confirm("<%=messaggioConfirm%>")) ripetiOperazione(<%=op%>);
	  	<%}
	  	else {
	  		if (confirmContinuaRicalcolo) {
	  			if (errorCode == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE) {
	  				messaggioConfirm = "Esistono stati occupazionali inseriti/modificati manualmente che potrebbero essere cancellati dal ricalcolo impatti. Vuoi proseguire?";
	  			}
	  			else {
	  				messaggioConfirm = "Esistono stati occupazionali inseriti/modificati manualmente che non saranno cancellati dal ricalcolo impatti. Vuoi proseguire?";
	  			}
	  			%>
	  			if (confirm("<%=messaggioConfirm%>") && invia(false)) continuaRicalcolo(document.Form1.FORZA_INSERIMENTO.value);
	  		<%}
	  	 }
	  }
	%>
}

var listaTRAperta = false;
function sezioneMovimentiPrec() {
	resetMovimentiPrec();
	if (listaTRAperta){
		nascondiTRpreDid();
		document.Form1.viewTuttiMovimenti.value = '0';
	}
	else{ 
		mostraTRpreDid();
		document.Form1.viewTuttiMovimenti.value = '1';
	}
}
function resetMovimentiPrec() {
	for (i=0;i<listaTR.length;i++) {
		listaRetribuzioniSanate[i].value = listaRetribuzioni[i].value;
	}
}
function impostaDati() {
	if (isDichiarazioneGenerica() && isSuperamentoLimite()) {		
		mostraCheck();
		<%if (prgMovimentoSupLimite != null && !prgMovimentoSupLimite.equals("")) { %>
			o = document.getElementById("A" + <%=prgMovimentoSupLimite%>);
			if (o != null) {
				o.checked = true;
				onOf(o);
			}
		<%}%>
	}
	if (isDichiarazioneGenerica()) {
		disabilitaRetribuzioniSanate();
		nascondiTRpreDid();
		mostraBottone(false);
	}
}
function selezionaDichiarazione(o) {
    if (obbligoRichiestaRiesame(true)) return ;
}
function setDichiarazioneGenerica(gen) {
	tipoDich = document.getElementsByName("tipoDichiarazione");
	ind = gen?1:0;
	tipoDich[ind].checked=true;
}
function selezionaTipo() {
	varLimite = document.getElementsByName("limite");
	tipoDich = document.getElementsByName("tipoDichiarazione");
	var numMesiSospVar = "<%=numMesiSosp%>";
	if (varLimite[0].checked) {
		if (!sospensioneAnzianita && statoOccupazionaleRagg=='D' && !isSuperamentoLimite() && (numMesiSospVar=="0")) {
			if (
				confirm("Non esistono stati/mesi di sospensione di anzianità. Si vuole comunque dichiarare il dettaglio del mancato superamento del limite?")){
					setDichiarazioneGenerica(false);
			}
			else {
				setSuperamentoLimite(true);
				//alert("Non esistono stati di sospensione di anzianità per cui non e' necessario dichiarare il mancato superamento del limite");
			}
			//return;
		}
	}
	
	if (obbligoRichiestaRiesame(true) )
		return false;
	if (varLimite[0].checked) {
		nascondiCheck();
	}
	else {
		if (tipoDich[1].checked) {
			mostraCheck();	
		}
		else {
			nascondiCheck(); 
		}
	}
	if (tipoDich[1].checked) {
		disabilitaRetribuzioniSanate();
		mostraBottone(false);
		nascondiTRpreDid();
		//mostraEtichetta();
	}
	else {	
		abilitaRetribuzioniSanate();
		//mostraTRpreDid();
		mostraBottone(true);
		//nascondiEtichetta();
	}
	setCampiRetSanata();
}

//Nasconde la colonna della retribuzione mens. sanata (solo in una condizione: Generica, mancato superamento)
function setCampiRetSanata(){
	//varLimite = document.getElementsByName("limite");
	tipoDich = document.getElementsByName("tipoDichiarazione");
	var c = "<%=movimenti.size()%>";
	v1 = document.getElementById("retMensSan");
	if (tipoDich[1].checked){ //&& varLimite[0].checked
		v1.style.display="none";
		for (i=0;i<=c;i++){
			v2 = document.getElementById("retMensSanValue_"+i);
			if (v2!=null)
				v2.style.display="none";
		}
	}
	else 
	{
		v1.style.display="inline";
		for (i=0;i<=c;i++){
			v2 = document.getElementById("retMensSanValue_"+i);
			if (v2!=null)
				v2.style.display="inline";
		}
	}
	return true;
}

function mostraBottone(b) {
	o = document.getElementsByName("tutti_i_movimenti");
	o[0].style.display=b?"":"none";
}
function onOf(o) {
	if (checkSelezionato == null) {
        // disabilito tutti i check tranne questo
        disabilitaCheck(o.id);
        // disabilito i campi retribuzione precedenti
        abilitaRetribuzioneDa(o.id);
        checkSelezionato = o;
    }
    else if (checkSelezionato.id == o.id) {
        if (!o.checked) {
        // ho ricliccato sullo stesso check
           riabilitaTuttiCheck(o.id);
           disabilitaRetribuzioni(o.id);
           checkSelezionato = null;
        }
    }
    else {
        return;
    }
}
function ripetiOperazione(n) {
	o = document.getElementsByName("FLAG_OP_SANARE_FORZATURA");
	o1 = document.getElementsByName("FORZA_INSERIMENTO");
	o2 = document.getElementsByName("CONTINUA_CALCOLO_SOCC");
	o2[0].value = document.Form1.CONTINUA_CALCOLO_SOCC.value;
	switch(n) {
		case 0: o[0].value = "S"; break;
		case 1: o1[0].value = "true"; break;
		case 2: o[0].value = "S"; o1[0].value = "true"; break;
		default: return;
	}
	doFormSubmit(document.Form1);
}

function continuaRicalcolo(valore) {
	document.Form1.FORZA_INSERIMENTO.value = valore;
  	document.Form1.CONTINUA_CALCOLO_SOCC.value = "true";
  	doFormSubmit(document.Form1);
}


var listaCheck = new Array();
var listaRetribuzioni = new Array();
var listaRetribuzioniSanate = new Array();
var listaReddito = new Array();
var listaTR = new Array();
var checkSelezionato=null;
var movimentoLegatoSospensione = "";
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
  <%@ include file="../patto/_controlloDate_script.inc"%>
function controllaData(data){
	var isOk = true;
	
	if(isFuture(data)){
		alert('La data di dichiarazione non può essere successiva alla data odierna.');
		isOk = false;
	}
	else {
		if(isOld(data)){
			if(!confirm('La data dichiarazione risulta essere precedente alla data odierna. Si desidera proseguire?'))
				isOk = false;
			
		}
	}
return isOk;
}

/*
 * Utilizzata per nascondere le righe precedenti alla DID nel caricamento della pagina.
 * Si poteva utilizzare direttamente la function nascondiTRpreDid(), ma nell'onLoad del body
 * c'è bisogno di una funzione che alla fine restituisca un booleano.
 */
function funcNascondiTRPreDID(){
	nascondiTRpreDid();
	return true;
}

/*
 * Funzione per la conferma per l'esecuzione dell'operazione 
 */
 function confermaOperazione(){
 	var prosegui = false;
 	if (confirm('Si desidera proseguire sanando la situazione amministrativa?')){
 		prosegui = true;
 	}
 	return prosegui;
 }
 
 function settaDataInizioMovSupReddito(obj, numeroMovimenti, prgMovimento) {
 	var objDataInizio;
 	objDataInizio = eval("document.Form1.dataInizio_" + numeroMovimenti);
 	if (objDataInizio != null) {
 		document.Form1.datInizioMovSupReddito.value = objDataInizio.value;
 	}
 	if (obj.checked) {
 		document.Form1.PRGMOVIMENTOSUPLIMITE.value = prgMovimento;
 	}
 	else {
 		document.Form1.PRGMOVIMENTOSUPLIMITE.value = "";
 	}
 }
 
 function avvisaSeNonPossibileSanare(){
 	<%-- nel caricamento della pagina dopo l'esecuzione con successo dell'operazione non bisogna mostrare i messaggi --%>
    var bReturnFunction = true;
    if (mostraRisultati) return false;
    varLimite = document.getElementsByName("limite");
    tipoDich = document.getElementsByName("tipoDichiarazione");
    var numMesiSospVar = "<%=numMesiSosp%>";
    
    if ((varLimite[0].checked) &&  (tipoDich[1].checked)) {
		if (!sospensioneAnzianita && statoOccupazionaleRagg=='D' && !isSuperamentoLimite() && (numMesiSospVar=="0")) {
			if (
				confirm("Non esistono stati/mesi di sospensione di anzianità. Si vuole comunque dichiarare il dettaglio del mancato superamento del limite?")){
				setDichiarazioneGenerica(false);
				bReturnFunction = false;
			}
			else {
				setSuperamentoLimite(true);
			}
		}
		else {
			if (!esistenzaDID) {
				alert("Il lavoratore non ha rilasciato la dichiarazione di immediata disponibilità: non e' possibile sanare la situazione amministrativa con una dichiarazione di mancato superamento generica");
    			bReturnFunction = false;
			}
		}
	} 
    
    if (listaRetribuzioni.length==0) {
		alert("Non esistono movimenti nel periodo da sanare");
		bReturnFunction = false;
	}
	return bReturnFunction;
 }
</script>
 
</head>
    <body class="gestione" onload="avvisaSeNonPossibileSanare();funcNascondiTRPreDID();rinfresca();impostaDati();operazionePossibile(true);obbligoRichiestaRiesame(true);conferma();setCampiRetSanata()">
    <%@ include  file="_intestazione.inc" %>
    <%@ include  file="../global/confrontaData.inc" %>
    <af:showMessages prefix="M_SANASITUAZIONEAMMINISTRATIVA"/>
    <af:showErrors/>
    <br/><br/>
    <center>
    
    <%out.print(htmlStreamTop);%>
<af:form name="Form1" method="POST"  action="AdapterHTTP" onSubmit="confermaOperazione() && controllaData(document.Form1.datSitSanata.value) && avvisaSeNonPossibileSanare() && invia(true)">

<table>
	<tr>
		<td class="azzurro_bianco">
    		<table class="main" cellpadding="0" cellspacing="0" border="0" width="100%">
    			<tr>
    				<td class="etichetta2">Stato atto</td>
        			<td>
        				<table cellpadding="0" cellspacing="0" border="0" width="100%">
        					<tr>
        						<%	String codAt = Utils.notNull(CODSTATOATTO);
					            	if (codAt.equals("")) codAt = "PA"; %>
						    	<td>
						    		<af:comboBox  classNameBase="input" disabled ="true"  name="COD"  
					                          	moduleName="M_STATOATTODISPO" 
					                          	selectedValue="<%=codAt%>" addBlank="false"/>
					        	</td>
					        	<% if (mostraRisultati || ProtocolloDocumentoUtil.protocollazioneLocale())  { %>
					        	<td align="right">anno&nbsp;</td>
        						<td>
        							<af:textBox classNameBase="input" type="text" name="annoProt" value="<%=Utils.notNull(annoProt)%>"
                                     readonly="true" title="Anno protocollo" size="5" maxlength="4" trim="false"/>
                                </td>
        						<td align="right"> &nbsp;&nbsp;num.&nbsp;</td>
        						<td>
        							<af:textBox classNameBase="input" type="text" 
        								name="numProt" value="<%=Utils.notNull(numProt)%>"
                                    	 readonly="true" title="Numero protocollo" size="8" 
                                    	 maxlength="100" trim="false"/>
                                </td>
        						<td class="etichetta2">data
				           			<af:textBox name="dataProt" type="date" value="<%=dataProt%>" size="11" 
				                       maxlength="10" title="data di protocollazione" classNameBase="input" 
				                       readonly="true" required="false" trim="false"/>
				                </td>
					                <%if (ProtocolloDocumentoUtil.protocollazioneLocale()) {%>
							    <td class="etichetta2">ora
							    	<af:textBox name="oraProt" type="text" value="<%=oraProt%>" size="6"  maxlength="5"
							                       title="data di protocollazione" classNameBase="input" readonly="true"
							                        required="false" trim="false"/>
							    </td>
							    	<%} else {%>
							    	<td><input type="hidden" name="oraProt" value="00:00">
							    	<%} %>
							    <%} else {%>
							    <td align="right">anno&nbsp;</td>
        						<td>
        							<af:textBox classNameBase="input" type="text" name="annoProt" value=""
                                     readonly="true" title="Anno protocollo" size="5" maxlength="4" trim="false"/>
                                </td>
	        						<td align="right"> &nbsp;&nbsp;num.&nbsp;</td>
	        						<td>
	        							<af:textBox classNameBase="input" type="text" 
	        								name="numProt" value=""
	                                    	 readonly="true" title="Numero protocollo" size="8" 
	                                    	 maxlength="100" trim="false"/>
	                                </td>
	        						<td class="etichetta2">data
					           			<af:textBox name="dataProt" type="date" value="" size="11" 
					                       maxlength="10" title="data di protocollazione" classNameBase="input" 
					                       readonly="true" required="false" trim="false"/>
					                </td>
								    <td class="etichetta2">
								    	<af:textBox name="oraProt" type="hidden" value="" size="6"  maxlength="5"
								                       title="data di protocollazione" classNameBase="input" readonly="true"
								                        required="false" trim="false"/>
								    </td>
							    <%}%>
    						</tr>
	   					</table>
    				</td>
    			</tr>
    			<tr>
    				<td class="etichetta2">Doc. di</td>
    				<td>
    					<table cellpadding="0" cellspacing="0" border="0" width="100%">
        					<tr>
        						<td class="campo2">
        						<%
        						String docInOrOutDesc = docInOrOut;
        						if ("I".equals(docInOrOut)) docInOrOutDesc= "Input";
        						else if ("O".equals(docInOrOut)) docInOrOutDesc = "Output";
        						%>
        						<input type="hidden" name="docInOrOut" value="<%=docInOrOut%>">
									<af:textBox name="docInOrOut_desc" type="text" value="<%=docInOrOutDesc%>" size="<%=docInOrOutDesc.length()+5%>"
				                           title="data di protocollazione" classNameBase="input" readonly="true"
				                           validateOnPost="false" required="false" trim ="false"/>
					            </td>
            					<td class="etichetta2">Rif.</td>
            					<td class="campo2">
            						<af:textBox name="rif" value="<%=docRif%>" size="<%=docRif.length()+15%>"
				                           title="riferimento" classNameBase="input" readonly="true"
				                           validateOnPost="false" required="false" trim ="false"/>
				                </td>
        					</tr>
        				</table>
    				</td>
    			</tr>
    		</table>
		</td>            
	</tr>
</table>       
    <%out.print(htmlStreamBottom);%>       
    
        <af:textBox name="codRif" type="hidden" value="<%=codRif%>"/>
        
        <table>
        	<tr><td align="left">
            <table>
	            <tr>
                    <td class="etichetta2" style="width:300;"><div id="etichetta_dichiarazione" style="display:">Il lavoratore dichiara il : </div>
                    <td  class="etichetta2">mancato superamento del limite<td><input type="radio" name="limite" value="inf" <%= limite.equals("inf")?"checked":""%>  onclick="selezionaTipo()" >
                    <td  class="etichetta2">superamento del limite<input type="radio" name="limite"  value="sup" <%= limite.equals("sup")?"checked":""%> onclick="selezionaTipo()" >
                </tr>
            </table>
            <tr><td align="left">
            <table>
                <tr>
                    <td class="etichetta">tipo dichiarazione: 
                    <td class="etichetta">Dettaglio<td><input type="radio" name="tipoDichiarazione" value="dett" <%= tipoDichiarazione.equals("dett")?"checked":""%> onclick="selezionaTipo()" >
                    <td class="etichetta">Generica<input type="radio" name="tipoDichiarazione" value="gen"  <%= tipoDichiarazione.equals("gen")?"checked":""%> onclick="selezionaTipo()" >
                </tr>
            </table>
            <tr><td align="left">
            <table>
            	<tr>
            		<td class="etichetta">Data dichiarazione
            		<td class="campo"><af:textBox name="datSitSanata" value="<%=Utils.notNull(dataSitSanata)%>" readonly="String.valueOf(!canModify)" 
                    		 type="date" classNameBase="input" size="11" maxlength="10"   required="true" validateOnPost="true"/>
                    <td><input type="button" class="pulsanti" name="tutti_i_movimenti" style="display:" value="tutti i movimenti" onclick="sezioneMovimentiPrec()"></td>
            	</tr>
            </table>
         </table>
        <%out.print(htmlStreamTop);%>
            <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">
            <input type="hidden" name="aggiorna" value="dettaglio">
            <input type="hidden" name="PAGE" value="SanareSituazionePrecPage">
            <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
            <input type="hidden" name="checkSelezionato" value="">
            <input type="hidden" name="prgDichDisponibilita" value="<%=Utils.notNull(prgDichDisponibilita)%>">
            <input type="hidden" name="numKloDichDisp" value="<%=Utils.notNull(numKloDichDisp)%>">     
            <input type="hidden" name="datDichiarazione" value="<%=Utils.notNull(dataDID!=null ? dataDID:dataDIDStoricizzata)%>">     
            <input type="hidden" name="FLAG_OP_SANARE_FORZATURA" value="N">
            <input type="hidden" name="FORZA_INSERIMENTO" value="<%=forzaInserimento%>">
            <input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="<%=continuaRicalcoloSOcc%>">
            <input type="hidden" name="PRGMOVINIZIODID" value="<%= movInizioDid!=null?movInizioDid.getAttribute("prgMovimento").toString():""%>">
            <input type="hidden" name="DATAINIZIOMOVIMENTODID" value="<%= dataInizioMovimentoDid %>">
            <input type="hidden" name="PRGMOVIMENTOSUPLIMITE" value="">
         
            <table class="main">
            	<tr>
            		<th></th>            		
            		<th>Data inizio</th>
            		<th>Data fine</th>
            		<th>Tipo</th>
            		<th >Tempo</th>
            		<th>Retr. mensile<br/>registrata (&#8364;)</th>
            		<th id="retMensSan" style="display:inline">Retr. mensile<br/>sanata (&#8364;)</th>
            		<th >Ragione sociale</th>
            		<th >Indirizzo</th>
                    <th >Comune</th>  
                    <th >CAP</th>
            	</tr>
                <tr>
                <%  int numeroMovimenti = 0; boolean flagMovTrovato = false;
                	BigDecimal prgMovimentoSucc = null;
                	SourceBean movimentoSucc = null;
                	BigDecimal prgMovimentoApp = null;
                	String dataInizioApp = "";
                	String dataFineApp = "";
                	String codTipoMovApp = "";
                	int visualizzaMovimentoPartenza = 0;
                	BigDecimal prgDichLavObj = (BigDecimal)serviceResponse.getAttribute("M_SANASITUAZIONEAMMINISTRATIVA.prgDichLav");
                	int prgDichLav =-1;
                	String codTipoAvviamento = "";
                	if (prgDichLavObj!=null ) prgDichLav = prgDichLavObj.intValue();
                	for (int i=0; i< movimenti.size(); i++) { 
                        SourceBean movimento = (SourceBean )movimenti.get(i);
                        String codTipoMov = (String)movimento.getAttribute("CODTIPOMOV");
                        if (codTipoMov.equals("AVV")) {
                        	codTipoMov = "A";
                        }
                        else {
                        	if (codTipoMov.equals("PRO")) {
                        		codTipoMov = "P";
                        	}
                        	else {
                        		if (codTipoMov.equals("TRA")) {
                        			codTipoMov = "T";
                        		}
                        	}
                        }
                        
                        String codStatoAtto = (String)movimento.getAttribute("CODSTATOATTO");
                        String codMonoTipoAss = Utils.notNull(movimento.getAttribute("codMonoTipo"));
                        codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)?movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString():"";
                        /**  Seleziono soltanto i movimenti di AVV|PRO|TRA validi, ovvero protocollati  */
                        if (!(!Utils.notNull(codTipoMov).equals("CES") && Utils.notNull(codStatoAtto).equals("PR")))
                        	continue;
                        //non considerare I TIROCINI (CODMONOTIPO) = T), TIPO AVVIAMENTO Z.09.02(VECCHIO CODICE RS3)
						//(cessazione attività lavorativa dopo un periodo di sospeso per contrazione)                        
						//(non possono essere sanati)
                        if (codMonoTipoAss.equalsIgnoreCase("T") || codTipoAvviamento.equalsIgnoreCase("Z.09.02")) {
	                    	continue;
	                    }
                        String dataInizio = (String)movimento.getAttribute("datInizioMov");
                        String dataFine = (String)movimento.getAttribute("datFineMovEffettiva");
                        BigDecimal retribuzione = (BigDecimal)movimento.getAttribute("decRetribuzioneMen");
                        BigDecimal retribuzioneSanata = (BigDecimal)movimento.getAttribute("decRetribuzioneMenSanata");
                        String dataSanata = (String)movimento.getAttribute("datSitSanata");
                        String codTipoDich = (String)movimento.getAttribute("codTipoDich");
                        BigDecimal numKlo = (BigDecimal)movimento.getAttribute("numKloMov");
                        BigDecimal prgMovimento = (BigDecimal)movimento.getAttribute("prgMovimento");
                        BigDecimal prgStatoOccupaz = (BigDecimal)movimento.getAttribute("prgStatoOccupaz");
                        BigDecimal prgDichLavMov = (BigDecimal)movimento.getAttribute("prgDichLav");
                        String codTipoAss = (String)movimento.getAttribute("CODTIPOASS");
                        String codMonoTempo = (String)movimento.getAttribute("codMonoTempo");
                        String ragioneSociale = Utils.notNull(movimento.getAttribute("STRRAGIONESOCIALE"));
                        String indirizzo = Utils.notNull(movimento.getAttribute("STRINDIRIZZO"));
                        String cap = Utils.notNull(movimento.getAttribute("STRCAP"));
                        String comune = Utils.notNull(movimento.getAttribute("comune"));
                        BigDecimal prgMovSucc = (BigDecimal)movimento.getAttribute("prgMovimentoSucc");
                        //
                        String dataInizioItem = "dataInizio_"+numeroMovimenti;
                        String dataInizioId = "id='" + dataInizioItem + "'";
                        String dataFineItem = "dataFine_"+numeroMovimenti;
                        String codTipoMovItem = "codTipoMov_"+numeroMovimenti;
                        String codMonoTempoItem = "codMonoTempo_"+numeroMovimenti;
                        String retribuzioneItem = "retribuzione_"+numeroMovimenti;
                        String retribuzioneId = "id='"+retribuzioneItem+"'" ;
                        String checkId = "A"+numeroMovimenti;
                        String retribuzioneSanataItem = "retribuzioneSanata_"+numeroMovimenti;
                        String retribuzioneSanataId = "id='"+retribuzioneSanataItem+"'"  + " style='text-align: right'";          
                        //
                        if (dataSanata!=null) {
                        	out.println("<script>situazioneSanata=true;</script>");
                        }             
                        if (!flagMovTrovato && statoOccupazSospensione !=null && DateUtils.compare(
                        		(String)statoOccupazSospensione.getAttribute("datInizio"), dataInizio)<=0) {
                        	flagMovTrovato = true;		
                        	out.write("<script>movimentoLegatoSospensione=" + numeroMovimenti + ";</script>");
                        }      
                        if (confirm || erroreGenerico || confirmUscitaMobilita || confirmContinuaRicalcolo) {
                        	// cerco il movimento con i dati originari
                        	SourceBean m  =null;
                        	for (int t=0;t<movimentiDaRicaricare.length;t++) {
                        		m = (SourceBean)movimentiDaRicaricare[t];
                        		BigDecimal prg = (BigDecimal)m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
                        		if (prg.intValue()==prgMovimento.intValue())
                        			break;
                        	}
                        	BigDecimal retribuzionePrecedente = (BigDecimal)m.getAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA);
                        	retribuzioneSanata = retribuzionePrecedente;
                        } 
                        String TR = "<tr ";
                        String trAttr = "";
                        String trScript = "";
                        String inputHidden="";
                        
                        if (movInizioLista >=0 && i<movInizioLista) { 
                        	if (!(mostraRisultati && prgDichLavMov!=null && prgDichLavMov.intValue()==prgDichLav))
		                        continue;
	                    }
	                    
	                    if (dataRif!=null && (DateUtils.compare(dataRif, dataInizio)<=0 || (DateUtils.compare(dataRif, dataInizio)>0 && 
				    			(dataFine==null || DateUtils.compare(dataRif, dataFine)<=0)))) {
				    		// il movimento e' un movimento aperto nel periodo di stipula della did				    		
				    	}
				    	else {
				    		//se il movimento ha un successivo (proroga/trasformazione), il movimento
				    		//va visualizzato se uno qualsiasi dei successivi 
				    		//si interseca col periodo di stipula della did (dataRif)
				    		visualizzaMovimentoPartenza = 0;
							if (movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC) != null) {
								//&& dataFine != null && dataRif!=null && DateUtils.getAnno(dataFine) == DateUtils.getAnno(dataRif))
								prgMovimentoSucc = (BigDecimal)movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC);
								for (int iContSucc=0;iContSucc<movimenti.size();iContSucc++) { 
			                        movimentoSucc = (SourceBean )movimenti.get(iContSucc);
			                        dataInizioApp = (String)movimentoSucc.getAttribute("datInizioMov");
                        			dataFineApp = (String)movimentoSucc.getAttribute("datFineMovEffettiva");
			                        prgMovimentoApp = (BigDecimal)movimentoSucc.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			                        codTipoMovApp = (String)movimentoSucc.getAttribute("CODTIPOMOV");
			                        if (prgMovimentoApp.equals(prgMovimentoSucc)) {
			          					if (!codTipoMovApp.equals("CES")) {
				          					if (dataRif!=null && (DateUtils.compare(dataRif, dataInizioApp)<=0 || (DateUtils.compare(dataRif, dataInizioApp)>0 && 
					    					   (dataFineApp==null || DateUtils.compare(dataRif, dataFineApp)<=0)))) {
				                        		visualizzaMovimentoPartenza = 1;
				                        		break;
				                       		}
				                       		else {
				                       			if (movimentoSucc.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC) != null) {
				                       				prgMovimentoSucc = (BigDecimal)movimentoSucc.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC);
				                       			}
				                       			else {
				                       				break;
				                       			}
				                       		}	
			                        	}
			                        	else {
			                        		break;
			                        	}
			                       	}
								}
								if (visualizzaMovimentoPartenza == 0 && viewTuttiMovimenti == 0) {
									trAttr = " id='TR_" + numeroMovimenti + "'";
	                        		trScript = "<script>listaTR.push(document.getElementById('TR_" + numeroMovimenti + "'))</script>";
	                        		inputHidden="<input type='hidden' name='solo_dettaglio_"+numeroMovimenti + "' value=''>";
								}
							}
				    		else if (movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC) == null && viewTuttiMovimenti == 0) {
	                        	trAttr = " id='TR_" + numeroMovimenti + "'";
	                        	trScript = "<script>listaTR.push(document.getElementById('TR_" + numeroMovimenti + "'))</script>";
	                        	inputHidden="<input type='hidden' name='solo_dettaglio_"+numeroMovimenti + "' value=''>";
	                        }
                        }               
                        trAttr += ">";
                        
                        out.println(TR + trAttr);
                        out.println(trScript);
                        out.println(inputHidden);
                %>    
                    <td width="30"><input type="checkbox" name="_<%=numeroMovimenti%>" id="A<%=prgMovimento%>" onclick="onOf(this);settaDataInizioMovSupReddito(this, <%=numeroMovimenti%>, <%=prgMovimento.toString()%>);" style="display:none">
                    <script>listaCheck.push(document.getElementById("A<%=prgMovimento%>"));</script>
                    </td>
                    
                    <td><af:textBox name="<%=dataInizioItem%>" value="<%=Utils.notNull(dataInizio)%>" readonly="true" 
                    		 type="text" classNameBase="input" size="11" maxlength="10" inline="<%=dataInizioId%>" /></td>
                    		 
                    <td><af:textBox name="<%=dataFineItem%>" value="<%=Utils.notNull(dataFine)%>"  size="11" maxlength="10"
                                classNameBase="input"  type="text" readonly="true" /></td>
                           
                     <td><af:textBox name="<%=codTipoMovItem%>" value="<%=Utils.notNull(codTipoMov)%>" size="2" maxlength="1"
                                classNameBase="input"  type="text" readonly="true" /></td>
                                
                    <td><af:textBox name="<%=codMonoTempoItem%>" value="<%=Utils.notNull(codMonoTempo)%>"  size="2" maxlength="3"
                                classNameBase="input"  type="text" readonly="true" /></td>
                    <%--<td><af:textBox name="reddito_0" value="" readonly="false" /></td>--%>
                    <td nowrap><af:textBox name="<%=retribuzioneItem%>"  readonly="true"
                    			classNameBase="input" type="number" inline=" style='text-align: right'"
                                value="<%=Utils.notNull(retribuzione)%>"  size="10" maxlength="9"    />
                        <script>o_i = document.getElementsByName("<%=retribuzioneItem%>");listaRetribuzioni.push(o_i[0]);</script>
                    </td>
                    <%
                    	String retribuzioneGiaSanata = !Utils.notNull(dataSanata).equals("")?"true":"false";
                    	String retribuzioneValue = Utils.notNull(retribuzioneSanata).equals("")&& (!mostraRisultati && !confirm && !confirmUscitaMobilita && !confirmContinuaRicalcolo && !erroreGenerico)?Utils.notNull(retribuzione):Utils.notNull(retribuzioneSanata);
                    %>
                    <td id="<%="retMensSanValue_"+i%>" style="display:inline" nowrap ><af:textBox name="<%=retribuzioneSanataItem%>" inline="<%= retribuzioneSanataId%>"
                                value="<%= retribuzioneValue%>"  
                                type="number" validateOnPost="true" required="false"
                                disabled="<%=retribuzioneGiaSanata%>" readonly="<%=String.valueOf(!canModify)%>"
                                size="10" maxlength="6"  />
                        <script>listaRetribuzioniSanate.push(document.getElementById("<%=retribuzioneSanataItem%>"));</script>
                    </td>
                    <td class="campo2"><b><%=ragioneSociale%></b></td>
                    <td class="campo2"><b><%=indirizzo%></b></td>
                    <td class="campo2"><b><%=comune%></b></td>
                    <td class="campo2"><b><%=cap%></b></td>
                    <td>
                        <input type="hidden" name="numKloMov_<%=numeroMovimenti%>"       value="<%=Utils.notNull(numKlo)%>">
                        <input type="hidden" name="prgMovimento_<%=numeroMovimenti%>"    value="<%=Utils.notNull(prgMovimento)%>">
                        <input type="hidden" name="prgStatoOccupaz_<%=numeroMovimenti%>" value="<%=Utils.notNull(prgStatoOccupaz)%>">
                        <input type="hidden" name="CODTIPOASS_<%=numeroMovimenti%>"      value="<%=Utils.notNull(codTipoAss)%>">
                        <input type="hidden" name="prgMovimentoSucc_<%=numeroMovimenti%>"      value="<%=Utils.notNull(prgMovSucc)%>">
                        <%--<input type="hidden" name="CODTIPOMOV_<%=numeroMovimenti%>"      value="<%=Utils.notNull(codTipoMov)%>"> --%>
                        <%--<input type="hidden" name="codMonoTempo_<%=numeroMovimenti%>"    value="<%=Utils.notNull(codMonoTempo)%>">--%>                        
                    </td>
               </tr> 
               <% 
               numeroMovimenti++;
              	 } %>
              <tr><td colspan=9>&nbsp;</td></tr>
              <tr><td></td><td class="etichetta2" style="vertical-align:top">note
              	<td colspan=7 class="campo"><af:textArea cols="80" rows="5" name="strNote" validateOnPost="true" maxlength="1000" value="<%=Utils.notNull(note)%>"/>
              </tr>
               <tr>
                <% if (canModify) {%>
                <td colspan=3><input type="submit" name="aggiorna"  value="Esegui operazione" id="aggiorna" class="pulsanti"></td>
                <%}%>
                <!--<td colspan=3><input type="button" name="chiudi"  value="Torna alla lista" id="chiudi" class="pulsanti" onclick="chiudi_ins()"></td>-->
               </tr>
               <tr><td><br/></td></tr>
               <tr>
               		<td colspan="9" align="left">
		                N.B.: il reddito sanato viene valorizzato a zero in caso di dichiarazione generica di mancato superamento.
		                <br/>
		                N.B.: il reddito sanato viene valorizzato a nullo in caso di dichiarazione generica di superamento.
	                </td>
               </tr>
               <input type="hidden" name="numeroMovimenti" value="<%=numeroMovimenti%>">
               <input type="hidden" name="datInizioMovSupReddito" value="">
               <input type="hidden" name="viewTuttiMovimenti" value="<%=viewMovimenti%>">
            </table>
        <%out.print(htmlStreamBottom);%>        
        </af:form>
	</center>        
    </body>
</html>
