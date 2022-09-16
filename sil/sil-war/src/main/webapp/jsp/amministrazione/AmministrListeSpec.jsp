<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
				 com.engiweb.framework.dispatching.module.AbstractModule,
				 com.engiweb.framework.configuration.ConfigSingleton,
				 com.engiweb.framework.util.QueryExecutor,
				 it.eng.afExt.utils.StringUtils,
				 it.eng.afExt.utils.DateUtils,
				 it.eng.afExt.utils.MessageCodes,
				 it.eng.sil.security.User,
				 it.eng.sil.security.PageAttribs,
				 it.eng.sil.security.ProfileDataFilter,
				 java.util.*,java.math.*,
				 java.io.*,
				 com.engiweb.framework.security.*,
				 it.eng.sil.util.*
				 " %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	boolean confirmContinuaIns = false;
	boolean confirmContinuaMod = false;
	String msgConferma = null;
	String errorConf = null;
	
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	String _pagelistamob = (String) serviceRequest.getAttribute("PAGE_LISTA_MOB"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
    PageAttribs attributi = new PageAttribs(user, _page);
    Vector warningRows = new Vector();
    boolean canUpdate = false;
    boolean canComboStato = true;
	if (serviceResponse.containsAttribute("M_InsMobilitaIscr")) warningRows = serviceResponse.getAttributeAsVector("M_InsMobilitaIscr.WARNINGMOBILITA.ROW");
	else if (serviceResponse.containsAttribute("M_SaveMobilitaIscr")) warningRows = serviceResponse.getAttributeAsVector("M_SaveMobilitaIscr.WARNINGMOBILITA.ROW");
	boolean infStorButt=false;
	boolean canInsert = false;
	boolean canAggiorna = false;
	boolean readOnlyStr=false;
	boolean canModifyNote=false;
	boolean canManage = false;
	boolean canDelete = false;
	boolean canPrint = false;
	boolean sceltaMov = true;
	boolean canViewSchedaDispo = false;
	boolean canInviaDomanda = false;
	boolean readOnlyStrFlag = false;
	boolean canDocumentiAssociati = false;
	boolean canTipoListaPatronato = false;
	boolean canAggiornaMBPatronato = false;
	
	String str_conf_MBDUBBIO = serviceResponse.containsAttribute("M_GetConfigValue.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigValue.ROWS.ROW.NUM").toString():"0";
	
	boolean canView=filter.canViewLavoratore();
	if (! canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}	
	else {
		//I pulsanti AGGIORNA ed INSERISCI al momento non esistono in banca dati,
		//se si aggiungerà il pulsante aggiorna si dovrà stabilire il legame tra AGGIORNA ed AGGIORNA_NOTE	
      	canAggiorna = attributi.containsButton("AGGIORNA");
      	canInsert   =  attributi.containsButton("INSERISCI");
     	infStorButt =  attributi.containsButton("INF_STOR");
      	canModifyNote = attributi.containsButton("AGGIORNA_NOTE");
      	canDelete = attributi.containsButton("CANCELLA");
      	canViewSchedaDispo = attributi.containsButton("DISPONIBILITA");
      	canPrint = attributi.containsButton("STAMPA");
      	canInviaDomanda = attributi.containsButton("INVIO_DOMANDA");
      	canDocumentiAssociati = attributi.containsButton("DOC_ASS");
    }
	
	if (Sottosistema.MO.isOff()) {
		readOnlyStr = true; //disabilito l'aggiornamento
		canInsert = false; //disabilito l'inserimento
	}

	//lettura configurazione mobilità
	String configuarazioneMob = "0";  //configurazione di default
	String labelStatoDomanda = "Stato della richiesta";
	String labelDataCrt = "Data CRT";
	String labelRegioneCrt = "Regione CRT";
	String labelProvinciaCrt = "Provincia CRT";
	String labelNumeroCrt = "Numero CRT";
	if (serviceResponse.containsAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM")) {
		configuarazioneMob = serviceResponse.getAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM").toString();
		if (configuarazioneMob.equals("1")) {
			labelStatoDomanda = "Stato della domanda";
			labelDataCrt = "Data CPM/Delibera Provinciale";
			labelRegioneCrt = "Regione CPM";
			labelProvinciaCrt = "Provincia CPM";
			labelNumeroCrt = "Numero CPM";
		}		
	}
	
    SourceBean listeSpec_Row= null;
    BigDecimal PRGUNITA     = null;
    BigDecimal PRGAZIENDA   = null;
    String rowRagioneSociale= null;
    String dataInizMov      = null;
    String dataFineMov      = null;
    String dataMaxDiff		= null;
    String motScorrDataMaxDiff = null;
    String rowIndirizzo     = null;
    String rowComune        = null;
    String rowProv			= null;
    String rowCf			= null;
    String rowPIva          = null;
    String rowTel           = null;
    String dataInizio       = null;
    String dataFine         = null;
    String dataFineOrig		= null;
    String codMBTipoLetto   = null;
    BigDecimal codMBStato	= null;
    String indennita_flg    = null;
    String nonImprenditore_flg = null;
    String dataInizioIndenn = null;
    String dataFineIndenn    = null;
    String mans             = null;
    String codMans			= null;
    String codGrado = null;
	String numLivello = null;
	String codCCNL = null;
	String strDescrizioneCCNL = null;
	String datCRT			= null;
    String numCRT           = null;
    String provCRT			= null;
    String regCRT			= null;
    String strNote          = null;
    String motivoFine	   = null;
    String dtmIns           = null;
    String dtmMod           = null;
    String numOreSett       = null;
    String dataDomanda      = null;
    BigDecimal prgMobilita  = null;
    BigDecimal prgMovimento = null;
    BigDecimal cdnUtMod     = null;
    BigDecimal cdnUtIns     = null;
    BigDecimal keyLock      = null;
    boolean flag_insert     = false;
    boolean buttonAnnulla   = false;
    String codMonoAttiva = null;
    String motivoRiapertura = null;
    InfCorrentiLav testata= null;
    Linguette l  =null;
    Vector listeSpecRows= serviceResponse.getAttributeAsVector("M_GETMOBILITAISCR.ROWS.ROW");
 	Testata operatoreInfo = null;   
   	String flagDispo = "";
   	String codDomanda = "";
   	String flgCasoDubbio = null;
   	String strDescCasoDubbio = null;
 
    ///////////////////////////////
    SourceBean row=null;
    String PRG_TAB_DA_ASSOCIARE = null;
    String COD_LST_TAB="AM_MB_IS";
    /////////////////////////////

	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
    String _funzione = cdnFunzione;

     testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
     if ((_pagelistamob != null) && (!_pagelistamob.equals(""))) {
	     testata.setPaginaLista(_pagelistamob);
     }
     // Savino 11/10/2005: o si ritorna alla lista della mobilita' o da nessun altra lista
     else testata.setSkipLista(true);
    
     //"Creo" le linguette --
     int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
     //String _page = (String) serviceRequest.getAttribute("PAGE"); 
      l  = new Linguette(user,  _cdnFunz, _page, new BigDecimal(cdnLavoratore));
      
	   if(serviceResponse.containsAttribute("M_InsMobilitaIscr")){
	  		errorConf = (String)serviceResponse.getAttribute("M_InsMobilitaIscr.RECORD.RESULT");
	   }else if(serviceResponse.containsAttribute("M_SaveMobilitaIscr")){
	  	 	errorConf = (String)serviceResponse.getAttribute("M_SaveMobilitaIscr.RECORD.RESULT");
	   }
      
     //----------------------
 
     if(serviceRequest.containsAttribute("newRecord") || ((errorConf!=null) && errorConf.equals("ERRORINS")))
     {//si vuole inserire un nuovo record
      flag_insert = true; buttonAnnulla = true;
      String prgmob = (String)serviceRequest.getAttribute("PRGMOBILITAISCR");
      if(!prgmob.equals("")){
    	  prgMobilita = new BigDecimal(prgmob);
      }
     }
     else
     { if(listeSpecRows != null && !listeSpecRows.isEmpty()) {
        listeSpec_Row  = (SourceBean) listeSpecRows.elementAt(0);
        row = listeSpec_Row;
        prgMobilita    = (BigDecimal) listeSpec_Row.getAttribute("PRGMOBILITAISCR");
        prgMovimento   = (BigDecimal) listeSpec_Row.getAttribute("PRGMOVIMENTO");
        if (prgMovimento != null) sceltaMov = false;
        PRGUNITA       = (BigDecimal) listeSpec_Row.getAttribute("PRGUNITA");
        PRGAZIENDA     = (BigDecimal) listeSpec_Row.getAttribute("PRGAZIENDA");
        if (PRGUNITA == null) PRGUNITA = (BigDecimal) listeSpec_Row.getAttribute("PRGUNITAOLD");
        if (PRGAZIENDA == null) PRGAZIENDA = (BigDecimal) listeSpec_Row.getAttribute("PRGAZIENDAOLD");
        rowRagioneSociale = (String)  listeSpec_Row.getAttribute("STRRAGIONESOCIALE");
        if (rowRagioneSociale == null) rowRagioneSociale = (String)listeSpec_Row.getAttribute("STRRAGIONESOCIALEOLD"); 
        rowIndirizzo   = (String) listeSpec_Row.getAttribute("STRINDIRIZZO");
        if (rowIndirizzo == null) rowIndirizzo = (String)listeSpec_Row.getAttribute("STRINDIRIZZOOLD"); 
        rowComune      = (String)     listeSpec_Row.getAttribute("COMUNE");
        if (rowComune == null) rowComune = (String)listeSpec_Row.getAttribute("COMUNEOLD");

        rowProv      = (String)     listeSpec_Row.getAttribute("STRTARGA");
        if (rowProv == null) rowProv = (String)listeSpec_Row.getAttribute("STRTARGAOLD");
        
        rowCf        = (String)     listeSpec_Row.getAttribute("CF");
        if (rowCf == null) rowCf = (String)listeSpec_Row.getAttribute("CFOLD");
		
        rowPIva       = (String)     listeSpec_Row.getAttribute("STRPARTITAIVA");
        if (rowPIva == null) rowPIva = (String)listeSpec_Row.getAttribute("STRPARTITAIVAOLD");
        
        rowTel         = (String)     listeSpec_Row.getAttribute("STRTEL");
        if (rowTel == null) rowTel = (String)listeSpec_Row.getAttribute("STRTELOLD");
        dataFineMov    = (String)     listeSpec_Row.getAttribute("DATFINEMOV");
        if (dataFineMov == null) dataFineMov = (String)listeSpec_Row.getAttribute("DATFINEMOVOLD");
        dataInizMov    = (String)     listeSpec_Row.getAttribute("DATINIZIOMOV");
      	if (dataInizMov == null) dataInizMov = (String)listeSpec_Row.getAttribute("DATINIZIOMOVOLD");  
        dataInizio     = (String)     listeSpec_Row.getAttribute("DATINIZIO"); 
        dataFine       = (String)     listeSpec_Row.getAttribute("DATFINE");
        dataFineOrig       = (String)     listeSpec_Row.getAttribute("DATFINEORIG");
        dataMaxDiff	   = (String) 	  listeSpec_Row.getAttribute("DATMAXDIFF");	        
        motScorrDataMaxDiff = (String) listeSpec_Row.getAttribute("CODMOTIVODIFF");
        motivoFine	= (String) listeSpec_Row.getAttribute("CODMOTIVOFINE");
        datCRT=(String) listeSpec_Row.getAttribute("DATCRT");
        numCRT = (String) listeSpec_Row.getAttribute("STRNUMATTO");
        provCRT=(String) listeSpec_Row.getAttribute("PROVCRT");
        regCRT = (String) listeSpec_Row.getAttribute("REGCRT");        
        codMBTipoLetto = (String)     listeSpec_Row.getAttribute("CODTIPOMOB");
      	codMBStato = (BigDecimal)     listeSpec_Row.getAttribute("CDNMBSTATORICH");
        codMonoAttiva = (String)     listeSpec_Row.getAttribute("CODMONOATTIVA");
        indennita_flg  = (String)     listeSpec_Row.getAttribute("FLGINDENNITA"); ; 
        nonImprenditore_flg = (String)     listeSpec_Row.getAttribute("FLGNONIMPRENDITORE"); ; 
        dataInizioIndenn = (String) listeSpec_Row.getAttribute("DATINIZIOINDENNITA");
        dataFineIndenn= (String) listeSpec_Row.getAttribute("DATFINEINDENNITA");        
        mans           = (String)     listeSpec_Row.getAttribute("MANSIONE");
        codMans = (String)     listeSpec_Row.getAttribute("CODMANSIONE");
        codGrado = (String)     listeSpec_Row.getAttribute("CODGRADO");
        numLivello = (String)     listeSpec_Row.getAttribute("STRLIVELLO");
        codCCNL = (String)     listeSpec_Row.getAttribute("CODCCNL");
        strDescrizioneCCNL = (String) listeSpec_Row.getAttribute("DESCCCNL");
        dataDomanda = (String)     listeSpec_Row.getAttribute("DATADOMANDA");
        numOreSett = (String)     listeSpec_Row.getAttribute("NUMORESETT");
        strNote        = (String)     listeSpec_Row.getAttribute("STRNOTE");
        keyLock        = (BigDecimal) listeSpec_Row.getAttribute("NUMKLOMOBISCR");
        cdnUtIns       = (BigDecimal) listeSpec_Row.getAttribute("CDNUTINS");
        cdnUtMod       = (BigDecimal) listeSpec_Row.getAttribute("CDNUTMOD");
        dtmIns         = (String)     listeSpec_Row.getAttribute("DTMINS");
        dtmMod         = (String)     listeSpec_Row.getAttribute("DTMMOD");
        flagDispo = StringUtils.getAttributeStrNotNull(listeSpec_Row, "FLGSCHEDA");
        codDomanda = StringUtils.getAttributeStrNotNull(listeSpec_Row, "CODDOMANDA");
        PRG_TAB_DA_ASSOCIARE = prgMobilita.toString();
        motivoRiapertura = (String)listeSpec_Row.getAttribute("CODMVRIAPERTURA");
        flgCasoDubbio = (String)listeSpec_Row.getAttribute("casoDubbio");
        strDescCasoDubbio = (String)listeSpec_Row.getAttribute("strDescCasoDubbio");
       }
       else
       {//non ci sono dati inerenti il lavoratore: possiamo solo inserire un nuovo record
        flag_insert = true;
       }
     }//else
	
     if (user.getCodTipo().equalsIgnoreCase(MessageCodes.Login.COD_TIPO_GRUPPO_PATRONATO)) {
 		canTipoListaPatronato = true;
 		Vector rowsTipoLista = serviceResponse.getAttributeAsVector("M_GetDeMbTipoPatronato.ROWS.ROW");
 		if (!flag_insert) {
 			int sizeTipoLista = rowsTipoLista.size();
 			for (int j=0;(j<sizeTipoLista && !canAggiornaMBPatronato);j++) {
 		  		SourceBean rowCurr = (SourceBean)rowsTipoLista.get(j);
 		  		String codiceLista = rowCurr.getAttribute("CODICE").toString();
 		  		if (codiceLista.equalsIgnoreCase(codMBTipoLetto)) {
 		  			canAggiornaMBPatronato = true;	
 		  		}
 		  	}
 			canAggiorna = canAggiornaMBPatronato;
 			readOnlyStr = !canAggiornaMBPatronato;
 			canModifyNote = canAggiornaMBPatronato;
 		}
 	}
	
   	if(flag_insert){
		if (configuarazioneMob.equals("1")) {
			dataDomanda = DateUtils.getNow();
		}
   		canManage=canInsert;
   		canModifyNote=false;
   	}else{
   		canManage = (canModifyNote || (!readOnlyStr));
   	}
   
  	String htmlStreamTop = StyleUtils.roundTopTable(canManage);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canManage);
   
   operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

   //Se la mobilità non è attiva non è possibile stampare!
   if(!serviceRequest.containsAttribute("newRecord") && !serviceRequest.containsAttribute("cancella") && !flag_insert){   
	   if(!codMonoAttiva.equals("A")){
	   		canPrint = false;
	   }
   }   

   //Check di esistenza dello storico delle mobilita'
   boolean storicoMob = serviceResponse.containsAttribute("M_HasStorMob.ROWS.ROW");
   
  String codCpi = user.getCodRif();
  String strDescrizione = "";
  String strCodice = "";
  SourceBean rowDescrizione = null;
  
  Vector vettCpi = serviceResponse.getAttributeAsVector("M_GetDeCpi.ROWS.ROW");
  if (vettCpi != null && !vettCpi.equals("")){
  	for (int i=0;i<vettCpi.size();i++)  {
   		rowDescrizione = (SourceBean)vettCpi.get(i);
		strCodice = rowDescrizione.getAttribute("CODICECPI").toString();
		if (strCodice.equals(codCpi)) {
			strDescrizione = rowDescrizione.getAttribute("DESCRIZIONECPI").toString();
			strDescrizione = StringUtils.replace(strDescrizione,"'","\\'");
			break;		
		}
	}
 }
 
 if (Sottosistema.MO.isOff()) {
 	sceltaMov = false;
 }	
 
	Vector ccnlDescRows= serviceResponse.getAttributeAsVector("M_GetCCNLDesc.ROWS.ROW");

 
 if(errorConf!=null && errorConf.equals("ERRORINS")){
	 confirmContinuaIns = true;
	 msgConferma = "Lo stato della richiesta non è congruente col tipo di lista di mobilità. Si vuole procedere al salvataggio dei dati?";
 }else if(errorConf!=null && errorConf.equals("ERRORSAVE")){
	 confirmContinuaMod = true;
	 msgConferma = "Lo stato della richiesta non è congruente col tipo di lista di mobilità. Si vuole procedere al salvataggio dei dati?";
 }
	
	String codFisc = null;
	String codCom = null;
	String codMansione = null;
	String tipoMansione = null;
	String comune = null;
	String descMansione = null;
	String indirizzo = null;
	String motDecad = null;
	String motScorr = null;
	String pIva = null;
	String prgAzienda = null;
	String prgUnita = null;
	String prgLavPattoSc = null;
	String prgPattoLav = null;
	String prg_tab = null;
	String prov = null;
	String ragione = null;
	String codccnl = null;
	String codGrad = null;
	String codStatoMob = null;
	String codTipoMob = null;
	String DATCRT = null;
	String datFine = null;
	String datFineMovHid = null;
	String datFineOrig = null;
	String datDomanda = null;
	String oreSettimanali = null;
	String datInizMovHid = null;
	String datInizioHid = null;
	String datMaxDiff = null;
	String dataFineInden = null;
	String dataInizioInden = null;
	String flgIndennita = null;
	String flgNonImprenditore = null;
	String numcrt = null;
	String prgmov = null;
	String provcrt = null;
	String regioneCRT = null;
	String strCCNL = null;
	String strLivello = null;
	String strTel = null;
	
	String statoPatto = null;
	String cod_lst_tab = null;
	String codStatoPattoLav = null;
	String datStipula = null;
	String prg_lav_patto_sc = null;
	String prg_patto_lavoratore = null;
	String prgTab = null;
	String tipoPatto = null;
	String datProtInf = null;
	String operazioneColPatto = null;
	String flgCasoDubbio_Req = null;
	
	if(confirmContinuaIns || confirmContinuaMod){
		codFisc = (String)serviceRequest.getAttribute("CF");
		codCom = (String)serviceRequest.getAttribute("CODCOM");
		codMansione = (String)serviceRequest.getAttribute("CODMANSIONE");
		tipoMansione = (String)serviceRequest.getAttribute("CODTIPOMANSIONE");
		comune = (String)serviceRequest.getAttribute("Comune");
		descMansione = (String)serviceRequest.getAttribute("DESCMANSIONE");
		indirizzo = (String)serviceRequest.getAttribute("Indirizzo");
		motDecad = (String)serviceRequest.getAttribute("MotDecad");
		motScorr = (String)serviceRequest.getAttribute("MotScorrDataMaxDiff");
		pIva = (String)serviceRequest.getAttribute("PIva");
		prgAzienda = (String)serviceRequest.getAttribute("PRGAZIENDA");
		prgUnita = (String)serviceRequest.getAttribute("PRGUNITA");
		prgLavPattoSc = (String)serviceRequest.getAttribute("PRG_LAV_PATTO_SCELTA");
		prgPattoLav = (String)serviceRequest.getAttribute("PRG_PATTO_LAVORATORE");
		prov = (String)serviceRequest.getAttribute("Prov");
		ragione = (String)serviceRequest.getAttribute("Ragione");
		codccnl = (String)serviceRequest.getAttribute("codCCNL");
		codGrad = (String)serviceRequest.getAttribute("codGrado");
		codStatoMob = (String)serviceRequest.getAttribute("codStatoMob");
		codTipoMob = (String)serviceRequest.getAttribute("codTipoMob");
		DATCRT = (String)serviceRequest.getAttribute("datCRT");
		datFine = (String)serviceRequest.getAttribute("datFine");
		datFineMovHid = (String)serviceRequest.getAttribute("datFineMovHid");
		datFineOrig = (String)serviceRequest.getAttribute("datFineOrig");
		datInizMovHid = (String)serviceRequest.getAttribute("datInizMovHid");
		datInizioHid = (String)serviceRequest.getAttribute("datInizioHid");
		if(datInizioHid.equals("") || datInizioHid==null){
			datInizioHid = (String)serviceRequest.getAttribute("datInizio");
		}
		datMaxDiff = (String)serviceRequest.getAttribute("datMaxDiff");
		dataFineInden = (String)serviceRequest.getAttribute("dataFineIndenn");
		dataInizioInden = (String)serviceRequest.getAttribute("dataInizioIndenn");
		flgIndennita = (String)serviceRequest.getAttribute("flgIndennita");
		flgNonImprenditore = (String)serviceRequest.getAttribute("flgNonImprenditore");
		numcrt = (String)serviceRequest.getAttribute("numCRT");
		prgmov = (String)serviceRequest.getAttribute("prgMovimento");
		provcrt = (String)serviceRequest.getAttribute("provCRT");
		regioneCRT = (String)serviceRequest.getAttribute("regioneCRT");
		strCCNL = (String)serviceRequest.getAttribute("strCCNL");
		strLivello = (String)serviceRequest.getAttribute("strLivello");
		strTel = (String)serviceRequest.getAttribute("strTel");
		datDomanda = (String)serviceRequest.getAttribute("dataDomanda");
		oreSettimanali = (String)serviceRequest.getAttribute("numOreSett");
		statoPatto = (String)serviceRequest.getAttribute("StatoPatto");
		cod_lst_tab = (String)serviceRequest.getAttribute("COD_LST_TAB");
		codStatoPattoLav = (String)serviceRequest.getAttribute("COD_STATO_PATTO_LAVORATORE");
		datStipula = (String)serviceRequest.getAttribute("DATSTIPULA");
		prg_lav_patto_sc = (String)serviceRequest.getAttribute("PRG_LAV_PATTO_SCELTA");
		prg_patto_lavoratore = (String)serviceRequest.getAttribute("PRG_PATTO_LAVORATORE");
		prgTab = (String)serviceRequest.getAttribute("PRG_TAB");
		tipoPatto = (String)serviceRequest.getAttribute("TipoPatto");
		datProtInf = (String)serviceRequest.getAttribute("datProtocolloInf");
		operazioneColPatto = (String)serviceRequest.getAttribute("operazioneColPatto");
		flgCasoDubbio_Req = (String)serviceRequest.getAttribute("casoDubbio");
	}
%>

<html>
<head>
<title>Amministrazione - Liste Speciali: mobilita</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<%@ include file="CommonScript.inc"%>
<%@ include file="MovimentiRicercaSoggetto.inc" %>
<script language="Javascript" src="../../js/docAssocia.js"></script>

<% String queryString = "cdnLavoratore="+cdnLavoratore+"&cdnFunzione="+cdnFunzione + "&page="+_page; %>
<% //String queryString = null; %>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<script  language="JavaScript">
	function apriListaAziendeUnita(cdnLav, prgAzienda){
	    var f = "AdapterHTTP?PAGE=AmstrListaAziendePage&cdnLavoratore=" + cdnLav;
	    if (prgAzienda != "") {
	    	f = f + "&prgAzienda=" + prgAzienda;	
	    }
	    var t = "Ricerca_Azienda";
	    var feat = "toolbar=no,location=no,directories=no,status=NO,menubar=no,scrollbars=yes,resizable=yes,width=700,height=400,top=100,left=100"
	    window.open(f, t, feat);
	}
    function cancCampi()
    { window.document.Frm1.PRGAZIENDA.value = "";
      window.document.Frm1.PRGUNITA.value   = "";
      window.document.Frm1.Ragione.value    = "";
      window.document.Frm1.Indirizzo.value  = "";
      window.document.Frm1.Comune.value     = "";
      window.document.Frm1.PIva.value       = "";
      window.document.Frm1.CF.value       = "";
	  window.document.Frm1.Prov.value       = "";      
      window.document.Frm1.strTel.value     = "";
      window.document.Frm1.datInizMov.value = "";
      window.document.Frm1.datFineMov.value = "";
      window.document.Frm1.datFine.value      = "";
      window.document.Frm1.datFineOrig.value  = "";
      window.document.Frm1.datMaxDiff.value      = "";
      window.document.Frm1.flgIndennita.value = "";
      window.document.Frm1.flgNonImprenditore.value = "";
      window.document.Frm1.dataInizioIndenn.value = "";
      window.document.Frm1.dataFineIndenn.value = "";
      var objIndennita = document.getElementById('opzioni_indennita');
      objIndennita.style.display = "none";
      window.document.Frm1.DESCMANSIONE.value = "";
      window.document.Frm1.CODMANSIONE.value  = "";
      window.document.Frm1.codCCNL.value = "";
      window.document.Frm1.strCCNL.value = "";
      window.document.Frm1.codGrado.value = "";
      window.document.Frm1.strLivello.value = "";
      window.document.Frm1.prgMovimento.value = "";
      window.document.Frm1.datInizio.value    = "";
      window.document.Frm1.casoDubbio.value = "";
      //quando deassocio il movimento alla mobilità devo 
      //rendere editabili i seguenti campi
      window.document.Frm1.datInizMov.readOnly = false;
      window.document.Frm1.datInizMov.className="inputEdit";
      window.document.Frm1.datFineMov.readOnly = false;
      window.document.Frm1.datFineMov.className="inputEdit";
      //window.document.Frm1.datInizio.readOnly = false;
      //window.document.Frm1.datInizio.className="inputEdit";
      window.document.Frm1.datInizMov.disabled = false;
      window.document.Frm1.datInizMov.className="inputEdit";
      window.document.Frm1.datFineMov.disabled = false;
      window.document.Frm1.datFineMov.className="inputEdit";
      //window.document.Frm1.datInizio.disabled = false;
      //window.document.Frm1.datInizio.className="inputEdit";
      window.document.Frm1.dataDomanda.value  = "";
      window.document.Frm1.numOreSett.value  = "";
      var objCercaAzienda = window.document.getElementById('sezione_azienda');
      objCercaAzienda.style.display = "inline";
      var objDate = window.document.getElementById('sezione_valorizza_date');
      objDate.style.display = "inline";
    }
    function getFormObj() {return document.Frm1;}
    
 	function deleteMobilita(pagina,parametri)
	{ 
	  	// Se la pagina è già in submit, ignoro questo nuovo invio!
	  	if (isInSubmit()) return;
  		if (confirm("Sei sicuro di voler eliminare la mobilità? L'iscrizione potrebbe essere legata ad un patto.")) {
			if (document.Frm1.PRG_LAV_PATTO_SCELTA.value != '') {
				parametri = parametri + "&PRG_LAV_PATTO_SCELTA=" + document.Frm1.PRG_LAV_PATTO_SCELTA.value;
			}
	 		setWindowLocation("AdapterHTTP?PAGE="+pagina +parametri);
  		}
	}
	
	
    function stampa(){
    	apriGestioneDoc('RPT_DETTAGLIO_MOBILITA', '&prgMobilita=<%=prgMobilita%>&codCpi=<%=codCpi%>&strDescrizione=<%=strDescrizione%>&cdnLavoratore=<%=cdnLavoratore%>&regioneCRT=' + document.Frm1.regioneCRT.value + '&provCRT=' + document.Frm1.provCRT.value,'ALMOBO')
	}
	
	function aggiornaAzienda(){
		document.Frm1.Ragione.value = opened.dati.ragioneSociale;
		document.Frm1.PRGAZIENDA.value = opened.dati.prgAzienda;
    	document.Frm1.PRGUNITA.value   = opened.dati.prgUnita;
    	document.Frm1.Indirizzo.value  = opened.dati.strIndirizzoAzienda;
    	document.Frm1.Comune.value     = opened.dati.comuneAzienda;
    	document.Frm1.PIva.value       = opened.dati.partitaIva;
    	document.Frm1.CF.value       = opened.dati.codiceFiscaleAzienda;
    	document.Frm1.CODCOM.value     = opened.dati.codComuneAzienda;
		opened.close();
   	}

	function caricaGestioneSchedaInformativaUpdateFlag(prg,numKlo) {
		var url = "AdapterHTTP?PAGE=MobGestioneDisponibilitaPage";
		url += "&PRGMOBILITAISCR="+prg;
		url += "&FLGDISPONIBILITA=S";
		url += "&NUMKLOMOBISCR="+numKlo;
		window.open(url, "Disponibilità", 'toolbar=0, scrollbars=1, height=500, width=550');
	}
	
	function checkScorrimento() {
		if (document.Frm1.OPERAZIONEMOBILITA.value == 'AGGIORNA') {
			var dataFineIndennita = document.Frm1.dataFineIndenn.value;
			var dataFineIndennitaPrec = document.Frm1.dataFineIndennHid.value;
			if (dataFineIndennita != dataFineIndennitaPrec) {
				if(confirm('Data fine indennizzo modificata, se vuoi effettuare lo scorrimento clicca su OK,\naltrimenti ANNULLA per non effettuare lo scorrimento')) {
					document.Frm1.ABILITASCORRIMENTO.value = "true";
				}
			}
		}
		return true;
	}
	
	function continuaInsMob(valore) {
	
	  window.document.Frm1.CONTINUA_INSERIMENTO_MOB.value = valore;
	  window.document.Frm1.PRGAZIENDA.value = "<%=prgAzienda %>";
      window.document.Frm1.PRGUNITA.value   = "<%=prgUnita %>";
      window.document.Frm1.Ragione.value    = "<%=ragione %>";
      window.document.Frm1.Indirizzo.value  = "<%=indirizzo %>";
      window.document.Frm1.Comune.value     = "<%=comune %>";
      window.document.Frm1.PIva.value       = "<%=pIva %>";
      window.document.Frm1.CF.value       = "<%=codFisc%>";
	  window.document.Frm1.Prov.value       = "<%=prov %>";      
      window.document.Frm1.strTel.value     = "<%=strTel %>";
      window.document.Frm1.datInizMovHid.value = "<%=datInizMovHid  %>";
      window.document.Frm1.datFineMovHid.value = "<%=datFineMovHid  %>";
      window.document.Frm1.datInizMov.value = "<%=datInizMovHid  %>";
      window.document.Frm1.datFineMov.value = "<%=datFineMovHid  %>";
      window.document.Frm1.datFine.value      = "<%=datFine  %>";
      window.document.Frm1.datFineOrig.value  = "<%=datFineOrig  %>";
      window.document.Frm1.datMaxDiff.value      = "<%=datMaxDiff  %>";
      window.document.Frm1.flgIndennita.value = "<%=flgIndennita   %>";
      window.document.Frm1.flgNonImprenditore.value = "<%=flgNonImprenditore %>";
      window.document.Frm1.dataInizioIndenn.value = "<%=dataInizioInden  %>";
      window.document.Frm1.dataFineIndenn.value = "<%=dataFineInden  %>";
      window.document.Frm1.DESCMANSIONE.value = "<%=descMansione %>";
      window.document.Frm1.CODMANSIONE.value  = "<%=codMansione%>";
      window.document.Frm1.codCCNL.value = "<%=codccnl %>";
      window.document.Frm1.strCCNL.value = "<%=strCCNL  %>";
      window.document.Frm1.codGrado.value = "<%=codGrad %>";
      window.document.Frm1.codGradoHid.value = "<%=codGrad %>";
      window.document.Frm1.strLivello.value = "<%=strLivello %>";
      window.document.Frm1.prgMovimento.value = "<%= prgmov%>";
      window.document.Frm1.datInizio.value    = "<%=datInizioHid %>";
      window.document.Frm1.CODCOM.value = "<%=codCom %>"; 
      window.document.Frm1.CODTIPOMANSIONE.value = "<%=tipoMansione %>";
	  window.document.Frm1.MotDecad.value = "<%=motDecad %>";
	  window.document.Frm1.MotScorrDataMaxDiff.value = "<%=motScorr %>";
	  window.document.Frm1.codStatoMob.value = "<%=codStatoMob  %>";
	  window.document.Frm1.codTipoMob.value = "<%=codTipoMob  %>";
	  window.document.Frm1.datCRT.value = "<%=DATCRT  %>";
	  window.document.Frm1.numCRT.value = "<%=numcrt   %>";
	  window.document.Frm1.provCRT.value = "<%=provcrt  %>";
	  window.document.Frm1.regioneCRT.value = "<%=regioneCRT %>";
	  window.document.Frm1.strLivello.value = "<%=strLivello %>";
	  window.document.Frm1.dataDomanda.value  = "<%=datDomanda  %>";
	  window.document.Frm1.numOreSett.value  = "<%=oreSettimanali  %>";
	  window.document.Frm1.StatoPatto.value = "<%= statoPatto %>";
	  window.document.Frm1.COD_LST_TAB.value = "<%= cod_lst_tab %>";
	  window.document.Frm1.COD_STATO_PATTO_LAVORATORE.value = "<%= codStatoPattoLav %>";
	  window.document.Frm1.DATSTIPULA.value = "<%= datStipula %>";		
	  window.document.Frm1.PRG_LAV_PATTO_SCELTA.value = "<%= prg_lav_patto_sc %>";
	  window.document.Frm1.PRG_PATTO_LAVORATORE.value = "<%= prg_patto_lavoratore %>";
	  window.document.Frm1.PRG_TAB.value = "<%= prgTab %>";
	  window.document.Frm1.TipoPatto.value = "<%= tipoPatto %>";
	  window.document.Frm1.datProtocolloInf.value = "<%= datProtInf %>";
	  window.document.Frm1.operazioneColPatto.value = "<%= operazioneColPatto %>";
	  window.document.Frm1.casoDubbio.value = "<%=flgCasoDubbio_Req   %>";
	  
	  if(valore=="true"){
	  	doFormSubmit(document.Frm1);
	  }
	}
	
	function continuaModMob(valore) {
	
	  window.document.Frm1.CONTINUA_AGGIORNAMENTO_MOB.value = valore;
	  window.document.Frm1.PRGAZIENDA.value = "<%=prgAzienda %>";
      window.document.Frm1.PRGUNITA.value   = "<%=prgUnita %>";
      window.document.Frm1.Ragione.value    = "<%=ragione %>";
      window.document.Frm1.Indirizzo.value  = "<%=indirizzo %>";
      window.document.Frm1.Comune.value     = "<%=comune %>";
      window.document.Frm1.PIva.value       = "<%=pIva %>";
      window.document.Frm1.CF.value       = "<%=codFisc%>";
	  window.document.Frm1.Prov.value       = "<%=prov %>";      
      window.document.Frm1.strTel.value     = "<%=strTel %>";
      window.document.Frm1.datInizMovHid.value = "<%=datInizMovHid  %>";
      window.document.Frm1.datFineMovHid.value = "<%=datFineMovHid  %>";
      window.document.Frm1.datInizMov.value = "<%=datInizMovHid  %>";
      window.document.Frm1.datFineMov.value = "<%=datFineMovHid  %>";
      window.document.Frm1.datFine.value      = "<%=datFine  %>";
      window.document.Frm1.datFineOrig.value  = "<%=datFineOrig  %>";
      window.document.Frm1.datMaxDiff.value      = "<%=datMaxDiff  %>";
      window.document.Frm1.flgIndennita.value = "<%=flgIndennita   %>";
      window.document.Frm1.flgNonImprenditore.value = "<%=flgNonImprenditore   %>";
      window.document.Frm1.dataInizioIndenn.value = "<%=dataInizioInden  %>";
      window.document.Frm1.dataFineIndenn.value = "<%=dataFineInden  %>";
      window.document.Frm1.DESCMANSIONE.value = "<%=descMansione %>";
      window.document.Frm1.CODMANSIONE.value  = "<%=codMansione%>";
      window.document.Frm1.codCCNL.value = "<%=codccnl %>";
      window.document.Frm1.strCCNL.value = "<%=strCCNL  %>";
      window.document.Frm1.codGrado.value = "<%=codGrad %>";
      window.document.Frm1.codGradoHid.value = "<%=codGrad %>";
      window.document.Frm1.strLivello.value = "<%=strLivello %>";
      window.document.Frm1.prgMovimento.value = "<%= prgmov%>";
      window.document.Frm1.datInizioHid.value    = "<%=datInizioHid %>";
      window.document.Frm1.CODCOM.value = "<%=codCom %>"; 
      window.document.Frm1.CODTIPOMANSIONE.value = "<%=tipoMansione %>";
	  window.document.Frm1.MotDecad.value = "<%=motDecad %>";
	  window.document.Frm1.MotScorrDataMaxDiff.value = "<%=motScorr %>";
	  window.document.Frm1.codStatoMob.value = "<%=codStatoMob  %>";
	  window.document.Frm1.codTipoMob.value = "<%=codTipoMob  %>";
	  window.document.Frm1.datCRT.value = "<%=DATCRT  %>";
	  window.document.Frm1.numCRT.value = "<%=numcrt   %>";
	  window.document.Frm1.provCRT.value = "<%=provcrt  %>";
	  window.document.Frm1.regioneCRT.value = "<%=regioneCRT %>";
	  window.document.Frm1.strLivello.value = "<%=strLivello %>";
	  window.document.Frm1.dataDomanda.value  = "<%=datDomanda  %>";
	  window.document.Frm1.numOreSett.value  = "<%=oreSettimanali  %>";
	  window.document.Frm1.StatoPatto.value = "<%= statoPatto %>";
	  window.document.Frm1.COD_LST_TAB.value = "<%= cod_lst_tab %>";
	  window.document.Frm1.COD_STATO_PATTO_LAVORATORE.value = "<%= codStatoPattoLav %>";
	  window.document.Frm1.DATSTIPULA.value = "<%= datStipula %>";		
	  window.document.Frm1.PRG_LAV_PATTO_SCELTA.value = "<%= prg_lav_patto_sc %>";
	  window.document.Frm1.PRG_PATTO_LAVORATORE.value = "<%= prg_patto_lavoratore %>";
	  window.document.Frm1.PRG_TAB.value = "<%= prgTab %>";
	  window.document.Frm1.TipoPatto.value = "<%= tipoPatto %>";
	  window.document.Frm1.datProtocolloInf.value = "<%= datProtInf %>";
	  window.document.Frm1.operazioneColPatto.value = "<%= operazioneColPatto %>";
	  window.document.Frm1.casoDubbio.value = "<%=flgCasoDubbio_Req   %>";

	  doFormSubmit(document.Frm1);
	}
	
	function conferma() {
		<%
		if (confirmContinuaIns) {
		%>
			if (confirm("<%=msgConferma%>")){ 
				continuaInsMob('true');
			}else{
				continuaInsMob('false');
			}
	<%}else if(confirmContinuaMod){ %>
			if (confirm("<%=msgConferma%>")) continuaModMob('true');
	<% } %>
	}

	function inviaDomanda() {
		var codTipoMob = '<%=Utils.notNull(codTipoMob)  %>';
		if (codTipoMob==''){
			codTipoMob=document.Frm1.codTipoMob[document.Frm1.codTipoMob.selectedIndex].value;
		}
		

		if (codTipoMob != "S1" && codTipoMob != "S2"){
			alert('ATTENZIONE: è possibile inviare domande di mobilità per i soli lavoratori che si trovano in uno dei seguenti stati:\n'+
					'- SOSPESI MOBILITA\' L.223/91\n'+
					'- SOSPESI MOBILITA\' L.236/93.');
			return false;
		}
		
		var test = <%=codMBStato%>;

		if (test == "2") {
			return true;
		}	
		
		alert('Per essere inviata l\'iscrizione si deve trovare nello stato \"DA APPROVARE\"');
		return false;		
	}	
	
<%@ include file="../patto/_associazioneXPatto_scripts.inc" %>

</script>

<%@ include file="UnderConstrScript.inc"%>
<script language="Javascript">
<%
    attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);    
%>
<%@ include file="../patto/_sezioneDinamica_script.inc"%>
</script>
<script language="Javascript">
  window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
</head>
<body class="gestione" onload="rinfresca();controllaCasoDubbio();conferma();">

<%
    testata.show(out);
    if (l!=null) {
    	if (l.getSize() > 0) { 
	    	l.show(out);
	    } else {%>
		    <p class="titolo">Mobilit&agrave;</p>
	    <%}  
	}
%>
<font color="red"><af:showErrors/> </font>
<font color="green">
 <af:showMessages prefix="M_SaveMobilitaIscr"/>
 <af:showMessages prefix="M_InsMobilitaIscr"/>
 <af:showMessages prefix="UpdateNoteMobilita"/>
 <af:showMessages prefix="M_DeleteMobilita"/>
 <af:showMessages prefix="M_InvioComunicazioneMBOInd"/> 
</font>

<script language="javascript">
  var flgInsert = <%=flag_insert%>;
  
</script>
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaPatto() && controllaStatoAtto(flgInsert,this) && controlloMobilita() && checkScorrimento()">
<ul>
<%
for (int r=0;r<warningRows.size();r++) {
	SourceBean sbRow = (SourceBean)warningRows.get(r);
 	out.println("<li>" + sbRow.getAttribute("DETTAGLIO").toString() + "</li>");
}
%>
</ul>
<%out.print(htmlStreamTop);%>
<%@ include file="MobilitaCampiLayOut.inc"%>
<% %>
<%@ include file="../patto/_associazioneDettaglioMobilitaXPatto.inc"%> 
<br>
<%if(!flag_insert) {%>
  <table class="main">
  <tr> 
  <td align="center" colspan="2">
  <%if (Sottosistema.MO.isOn()) {%>
  	<input class="pulsante" type="button" name="btnEventiScorrimento" value="Eventi scorrimento" onclick="visPaginaEventiScorrimento('<%=cdnLavoratore%>')">
  <%}
  if(!readOnlyStr && canAggiorna) {%>
  	<input class="pulsante" type="submit" name="save" value="Aggiorna" onclick="document.Frm1.OPERAZIONEMOBILITA.value = 'AGGIORNA'">	
  <%}
  if(canDelete && (prgMobilita!=null)) {%>
  	<input class="pulsante" type="button" name="annulla" value="Cancella"
    	onClick="deleteMobilita('AmministrListeSpecPage','&cdnLavoratore=<%=cdnLavoratore%>&prgMobilitaIscr=<%=Utils.notNull(prgMobilita)%>&CDNFUNZIONE=<%=_cdnFunz%>&cancella=true&dataInizio=<%=dataInizMov%>&PAGE_LISTA_MOB=<%=Utils.notNull(_pagelistamob)%>&datInizio=<%=Utils.notNull(dataInizio)%>')">
  <%}
  if (canPrint) {%>
	<input type="button" class="pulsanti" value="Stampa" onclick="stampa()"/>
  <%}%>
  
  <%
  	/**QUERY PER ESTRARRE DEI CASI CON CUI TESTARE IL PULSANTE
  	select al.strcodicefiscale,aa.strragionesociale, ami.*
	from am_mobilita_iscr ami
	join an_lavoratore al on ami.cdnlavoratore = al.cdnlavoratore
	join de_mb_tipo amt on ami.codtipomob = amt.codmbtipo
	join an_azienda aa on  ami.prgazienda = aa.prgazienda
	where amt.codmonoattiva = 'S'
	and ami.CDNMBSTATORICH = 2;
  	**/
  
  
  
  	boolean abilitaInvio = false;
  	abilitaInvio = (codMBStato != null && 	//abilito il pulsante se lo stato è da 'DA APPROVARE'
  					(2 == codMBStato.intValue()) &&
  					"S".equalsIgnoreCase(codMonoAttiva) && //e se codMonoattività è  'S'
  					canInviaDomanda	//e l'utente è abilitato
  					  );
  	
  	if (abilitaInvio) {
  %>
	<input type="submit" class="pulsanti" name="inviaDomandaNCR" value="Invia domanda" onclick="return inviaDomanda()"/>
	<input type="hidden" name="codMBStato" value="<%=codMBStato%>"/>
	<input type="hidden" name="codMonoAttiva" value="<%=codMonoAttiva%>"/>
  <%} %>
  </td>  	
  </tr>
  </table>
  
  <table class="main">
  <tr>
  <td align="left">
    <!--<td width="50%" align="right">-->
    <%if(!readOnlyStr && canInsert)
      {%><input class="pulsante" type="submit" name="newRecord" value="Nuova iscrizione storica"><%}%>
    </td>
    <td align="right">
  	<%if(infStorButt)
      {%><input class="pulsante<%=((storicoMob)?"":"Disabled")%>"
               type="button" name="infSotriche" value="Informazioni storiche" 
               onClick="infoStoriche('MobilitaInfoStorPage','&cdnLavoratore=<%=cdnLavoratore%>&cdnfunzione=<%= cdnFunzione%>')"
               <%=(!storicoMob)?"disabled=\"True\"":""%>>
           <%if ( canDocumentiAssociati ) { %>    
           &nbsp;&nbsp;<input type="button", class="pulsante" name="docuAssociati" value="Documenti associati" 
     	   onclick="docAssociati(<%=cdnLavoratore%>,'AmministrListeSpecPage',<%=cdnFunzione%>,'',<%=Utils.notNull(prgMobilita)%>)"/>
     	   <%} %>
    <%} else {%>
    	<%if ( canDocumentiAssociati ) { %>
   		<input type="button", class="pulsante" name="docuAssociati" value="Documenti associati" 
     	   onclick="docAssociati(<%=cdnLavoratore%>,'AmministrListeSpecPage',<%=cdnFunzione%>,'',<%=Utils.notNull(prgMobilita)%>)"/>
     	   <%} %>
   <%}%>
   </td>
  </tr>
  </table>
  
<%}

else {%>
<table class="main">
<tr>
   <td width="33%"/>
   <td width="33%" align="center">
    <%if(canInsert){%><input class="pulsante" type="submit" name="insert" value="Inserisci"><%}%>
   </td>
   <td width="33%" align="center">
    <%if(buttonAnnulla) {%>
      <input class="pulsante" type="button" name="annulla" value="Chiudi senza inserire"
             onClick="openPage('AmministrListeSpecPage','&cdnLavoratore=<%=cdnLavoratore%>&prgMobilitaIscr=<%=Utils.notNull(prgMobilita)%>&CDNFUNZIONE=<%=_cdnFunz%>')">
    <%}%>
   </td>
</tr>
<tr>
	<td>&nbsp;
	<%if((canDelete) && (prgMobilita!=null) && !serviceRequest.containsAttribute("newRecord") && !(errorConf!=null && errorConf.equals("ERRORINS"))) {%>
      <input class="pulsante" type="button" name="annulla" value="Cancella"
             onClick="deleteMobilita('AmministrListeSpecPage','&cdnLavoratore=<%=cdnLavoratore%>&prgMobilitaIscr=<%=Utils.notNull(prgMobilita)%>&CDNFUNZIONE=<%=_cdnFunz%>&cancella=true&dataInizio=<%=dataInizMov%>&PAGE_LISTA_MOB=<%=Utils.notNull(_pagelistamob)%>&datInizio=<%=Utils.notNull(dataInizio)%>')">
    <%}%>
	</td>
    <td colspan="2" align="right">
    <%if(infStorButt)  {%>
    		<input class="pulsante<%=((storicoMob)?"":"Disabled")%>" type="button" name="infSotriche" value="Informazioni storiche" 
               onClick="infoStoriche('MobilitaInfoStorPage','&cdnLavoratore=<%=cdnLavoratore%>&cdnfunzione=<%= cdnFunzione%>')"
               <%=(!storicoMob)?"disabled=\"True\"":""%>>
        
    <%}%>
</tr>

</table>
<%}
  out.print(htmlStreamBottom);
%>
  <br><center><% operatoreInfo.showHTML(out); %></center>
  <input type="hidden" name="PAGE" value="AmministrListeSpecPage"/>
  <input type="hidden" name="cdnLavoratore"   value="<%=Utils.notNull(cdnLavoratore)%>"/>
  <input type="hidden" name="CDNFUNZIONE"     value="<%=_cdnFunz%>"/>
  <input type="hidden" name="cdnUtMod"        value="<%=Utils.notNull(cdnUtMod)%>"/>
  <input type="hidden" name="numKloMobIscr"   value="<%=Utils.notNull(keyLock)%>"/>
  <input type="hidden" name="prgMobilitaIscr" value="<%=Utils.notNull(prgMobilita)%>"/>
  <input type="hidden" name="prgMovimento"    value="<%=Utils.notNull(prgMovimento)%>"/>
  <input type="hidden" name="soloNote"    value=""/>
  <input type="hidden" name="PAGE_LISTA_MOB"    value="<%= Utils.notNull(_pagelistamob)%>"/>
  <input type="hidden" name="ABILITASCORRIMENTO" value="false"/>
  <input type="hidden" name="OPERAZIONEMOBILITA" value=""/>
  <input type="hidden" name="CONTINUA_INSERIMENTO_MOB" value="false"/>
  <input type="hidden" name="CONTINUA_AGGIORNAMENTO_MOB" value="false"/>
  

</af:form>

</body>
</html>
