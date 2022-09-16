<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

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
  	PageAttribs attributi = new PageAttribs(user, "ValidazioneMobilitaGeneralePage");
  	boolean canModify = attributi.containsButton("SALVA");
	String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String prgMobilitaIscrApp = (String) serviceRequest.getAttribute("prgMobilitaIscrApp");
	String prgMovimento = StringUtils.getAttributeStrNotNull(serviceRequest, "prgMovimento");
	String codComune = "";
	String cdnLavoratoreDB = "";
    String strCodiceFiscaleLav = "";
    String strNomeLav = "";
    String strCognomeLav = "";
    String strSessoLav = "";
    String datNascLav = "";
    String strCodiceFiscaleDB = "";
	String strNomeLavDB = "";
	String strCognomeLavDB = "";
	String datNascLavDB = "";
    String codComNascLav = "";
    String codCittadinanzaLav = "";
    String codComDom = "";
    String strIndirizzoDom = "";
    String strCapDom = "";
    String strTelDom = "";
    String codComRes = "";
	String strIndirizzoRes = "";
	String strCapRes = "";
	String strTelRes = "";
	String strCellLav = "";
	String strMailLav = "";
	String codCpiLav = "";
	String flgCambiamentiDati = "";
	String datScadenzaPermSogg = "";
	String codTipoTitolo = "";
	String codTipoTitoloGenerico = "";
    String strFlgCfOk = "";
    String strFlgDatiOk = "";
    String prgAzienda = "";
    String prgUnita = "";
    BigDecimal prgAziendaBig = null;
    BigDecimal prgUnitaBig = null;
    String strCodiceFiscaleAz = "";
    String strPartitaIvaAz = "";
    String strRagioneSocialeAz  = "";
    String strRagioneSocialeAzTrunc = "";
    String strIndirizzoUAz = "";
    String strComuneUAz = "";
    String strCapUAz = "";
    String natGiuridicaAz = "";
    String codNatGiuridicaAz = "";
    String strIndirizzoUAzDB = "";
    String codAtecoUAz = "";
    String strDesAtecoUAz = "";
    String codCCNLAz = "";
    String descrCCNLAz = "";
    String codCCNLMov = "";
  	String descrCCNLMov = "";
  	String strTelUAz = "";
    String strFaxUAz = "";
    String strUAMail = "";
    String posInps = "";
    String posInps1 = "";
    String posInps2 = "";
    String patInail = "";
    String patInail1 = "";
    String patInail2 = "";
    String strReferente = ""; //da recuperare
    String codInterinale = "INT";
  	Vector vectRicercaLav = null;
  	String codTipoAzienda = "";
  	String strNumAlboInterinali = "";
  	String strNumRegistroCommitt = "";
  	String flgInterAssPropria = ""; //da recuperare
  	String codTipoMob = "";
  	String dataInizioMov = "";
  	String dataFineMov = "";
  	String dataInizioMob = "";
  	String dataFineMob = "";
  	String dataFineMobOrig = "";
  	String dataInizioIndennita = "";
  	String dataFineIndennita = "";
  	String dataMaxDiff = "";
  	String dataCRT = "";
  	String codMotivoFine = "";
  	String indennita_flg = "";
  	String codMansione = "";
  	String descMansione = "";
  	String dataComunicaz = "";
  	String codGradoMov = "";
  	String strLivello = "";
  	String flagDiff = "";
  	String strIndirizzoDB = "";
	String codAtecoDB = "";
	String strTelDB = "";
	String strFaxDB = "";
	String codComuneDB = "";
	String capDB = "";
	String codCcnlDB = "";
	String strEmailDB = "";
	String strComuneDB = "";
	String strDesAtecoDB = "";
	String descrCCNLDB = "";
	String posInpsDB = "";
	String strNumRegistroCommittDB = "";
	String strPartitaIvaAzDB = "";
	String ragSocDB = "";
	String patInailDB = "";
	String codTipoAziendaDB = "";
	String descrTipoAziendaDB = "";
	String strNumAlboInterinaliDB = "";
	boolean validazioneEseguita = false;
	boolean unitaDaSessione = false;
	String numCRT = "";
	String regCRT = "";
	String provCRT = "";
	String motScorrDataMaxDiff = "";
	String numOreSett = "";
    String dataDomanda = ""; 
    String flgNonImprenditore = "";
    String flgCasoDubbio = "";
    String codDomanda = "";
	
  	//Mi dice se i dati dell'azienda sul DB sono aggiornati o da aggiornare rispetto 
  	//a quelli relativi al movimento collegato alla mobilità
  	boolean datiAziendaAggiornati = true;
  	//Mi dice quante unita ho trovato nel comune indicato
	int numUnitaConIndirizzoDiverso = 0;
	//Mi dice quante unita ho trovato con lo stesso indirizzo nel comune indicato
	int numUnitaConIndirizzoUguale = 0;
	vectRicercaLav = serviceResponse.getAttributeAsVector("M_Mob_Valida_RicercaLavoratoreCF.ROWS.ROW");
	
	if (serviceRequest.containsAttribute("VALIDAZIONE_MANUALE") && 
  		serviceRequest.getAttribute("VALIDAZIONE_MANUALE").toString().equals("OK")) {
  		validazioneEseguita = true;	
	}
	
  	if (validazioneEseguita) {
  		//ho appena fatto la validazione; i valori li prendo dalla serviceRequest
  		//e i campi non sono modificabili
  		canModify = false;
  		strRagioneSocialeAz = serviceRequest.getAttribute("strRagioneSocialeAz").toString();
	  	strNumAlboInterinali = serviceRequest.getAttribute("STRNUMALBOINTERINALI").toString();
	  	codTipoAzienda = serviceRequest.getAttribute("codTipoAzienda").toString();
	  	strIndirizzoUAz = serviceRequest.getAttribute("strIndirizzoUAz").toString();
	  	strPartitaIvaAz = serviceRequest.getAttribute("strPartitaIvaAz").toString();
	  	strCapUAz = serviceRequest.getAttribute("strUACap").toString();
	  	strTelUAz = serviceRequest.getAttribute("strTelUAz").toString();
	  	strFaxUAz = serviceRequest.getAttribute("strFaxUAz").toString();
	  	codTipoMob = serviceRequest.getAttribute("codTipoMob").toString();
	  	dataInizioMov = serviceRequest.getAttribute("datInizMov").toString();
	  	dataFineMov = serviceRequest.getAttribute("datFineMov").toString();
	  	dataInizioMob = serviceRequest.getAttribute("datInizio").toString();
	  	dataFineMob = serviceRequest.getAttribute("datFine").toString();
	  	dataFineMobOrig = serviceRequest.getAttribute("datFineOrig").toString();
	  	dataInizioIndennita = serviceRequest.getAttribute("dataInizioIndenn").toString();
	  	dataFineIndennita = serviceRequest.getAttribute("dataFineIndenn").toString();
	  	dataMaxDiff = serviceRequest.getAttribute("datMaxDiff").toString();
	  	dataCRT = serviceRequest.getAttribute("datCRT").toString();
		numCRT = (String) serviceRequest.getAttribute("numCRT");
		regCRT = (String) serviceRequest.getAttribute("regioneCRT");
		provCRT = (String) serviceRequest.getAttribute("provCRT");
	  	codMotivoFine = serviceRequest.getAttribute("MotDecad").toString();
	  	prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
	  	prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
	  	cdnLavoratoreDB = serviceRequest.getAttribute("CDNLAVORATORE").toString();
	  	indennita_flg = serviceRequest.getAttribute("flgIndennita").toString();
	  	descMansione = serviceRequest.getAttribute("DESCMANSIONE").toString();
	  	codMansione = serviceRequest.getAttribute("codMansione").toString();
	  	strCodiceFiscaleAz = serviceRequest.getAttribute("strCodiceFiscale").toString();
	  	strCodiceFiscaleLav = serviceRequest.getAttribute("strCodiceFiscaleLav").toString();
	  	codComune = serviceRequest.getAttribute("codComune").toString();
	  	strComuneUAz = serviceRequest.getAttribute("strComuneUAz").toString();
	  	strUAMail = serviceRequest.getAttribute("strUAMail").toString();
	  	codAtecoUAz = serviceRequest.getAttribute("codAtecoUAz").toString();
	  	strDesAtecoUAz = serviceRequest.getAttribute("strDesAtecoUAz").toString();
	  	codCCNLAz = serviceRequest.getAttribute("codCCNL").toString();
	  	descrCCNLAz = serviceRequest.getAttribute("strCCNL").toString();
	  	posInps = serviceRequest.getAttribute("STRPOSINPS").toString();
	  	patInail = serviceRequest.getAttribute("STRPATINAIL").toString();
	  	strNumRegistroCommitt = serviceRequest.getAttribute("STRNUMREGISTROCOMMITT").toString();
	  	strCognomeLav = serviceRequest.getAttribute("strCognomeLav").toString();
	  	strNomeLav = serviceRequest.getAttribute("strNomeLav").toString();
	  	strSessoLav = serviceRequest.getAttribute("strSessoLav").toString();
	  	datNascLav = serviceRequest.getAttribute("datNascLav").toString();
	  	codComNascLav = serviceRequest.getAttribute("codComNascLav").toString();
	  	codCittadinanzaLav = serviceRequest.getAttribute("codCittadinanzaLav").toString();
	  	codComDom = serviceRequest.getAttribute("codComDomLav").toString();
	  	strIndirizzoDom = serviceRequest.getAttribute("strIndirizzoDomLav").toString();
	  	strCapDom = serviceRequest.getAttribute("strCapDomLav").toString();
	  	strTelDom = serviceRequest.getAttribute("strTelDomLav").toString();
	  	codComRes = serviceRequest.getAttribute("codComResLav").toString();
	  	strIndirizzoRes = serviceRequest.getAttribute("strIndirizzoResLav").toString();
	  	strCapRes = serviceRequest.getAttribute("strCapResLav").toString();
	  	strTelRes = serviceRequest.getAttribute("strTelResLav").toString();
	  	datScadenzaPermSogg = serviceRequest.getAttribute("datScadenza").toString();
	  	codTipoTitolo = serviceRequest.getAttribute("codTipoTitolo").toString();
	  	codTipoTitoloGenerico = serviceRequest.getAttribute("codTipoTitoloGenerico").toString();
	  	strCellLav = serviceRequest.getAttribute("strCellLav").toString();
		strMailLav = serviceRequest.getAttribute("strMailLav").toString();
		codCpiLav = serviceRequest.getAttribute("codCpiLav").toString();
		dataComunicaz = serviceRequest.getAttribute("dataComunicaz").toString();
		codGradoMov = serviceRequest.getAttribute("codGradoHid").toString();
		codCCNLMov = serviceRequest.getAttribute("codCCNL").toString();
		descrCCNLMov = serviceRequest.getAttribute("strCCNL").toString();
		strLivello = serviceRequest.getAttribute("strLivello").toString();
		flagDiff = serviceRequest.getAttribute("flagDiff").toString();
		natGiuridicaAz = serviceRequest.getAttribute("natGiuridicaAz").toString();
		codNatGiuridicaAz = serviceRequest.getAttribute("codNatGiuridicaAz").toString();
		motScorrDataMaxDiff = (String) serviceRequest.getAttribute("MotScorrDataMaxDiff");
		numOreSett = serviceRequest.getAttribute("numOreSett").toString();
	    dataDomanda = serviceRequest.getAttribute("dataDomanda").toString();
	    flgNonImprenditore = serviceRequest.getAttribute("flgNonImprenditore").toString();
	    flgCasoDubbio = StringUtils.getAttributeStrNotNull(serviceRequest, "flgCasoDubbio");
	    codDomanda = StringUtils.getAttributeStrNotNull(serviceRequest, "CODDOMANDA");
  	}
  	else {
	  	Vector vectMobilitaAppoggio = serviceResponse.getAttributeAsVector("M_MobilitaGetDettaglioMobApp.ROWS.ROW");
	  	SourceBean rigaMob = null;
	  	if (vectMobilitaAppoggio.size() == 1) {
			rigaMob = (SourceBean) vectMobilitaAppoggio.elementAt(0);
			strIndirizzoUAz = StringUtils.getAttributeStrNotNull(rigaMob, "STRUAINDIRIZZO");
	  		strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull(rigaMob, "STRCODICEFISCALE");
		    strNomeLav = StringUtils.getAttributeStrNotNull(rigaMob, "STRNOME");
		    strCognomeLav = StringUtils.getAttributeStrNotNull(rigaMob, "STRCOGNOME");
		    datNascLav = StringUtils.getAttributeStrNotNull(rigaMob, "DATNASC");
	  		datScadenzaPermSogg = StringUtils.getAttributeStrNotNull(rigaMob, "DATSCADENZA");
	  		codTipoTitolo = StringUtils.getAttributeStrNotNull(rigaMob, "CODTIPOTITOLO");
			codTipoTitoloGenerico = StringUtils.getAttributeStrNotNull(rigaMob, "CODTIPOTITOLOGENERICO");
			strCellLav = StringUtils.getAttributeStrNotNull(rigaMob, "STRCELL");
			strMailLav = StringUtils.getAttributeStrNotNull(rigaMob, "STREMAIL");
			codCpiLav = StringUtils.getAttributeStrNotNull(rigaMob, "CODCPILAV");
			dataComunicaz = StringUtils.getAttributeStrNotNull(rigaMob, "DATCOMUNICAZ");
			flagDiff = StringUtils.getAttributeStrNotNull(rigaMob, "FLGDIFFERIMENTO");
			strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull(rigaMob, "STRAZCODICEFISCALE");
			strPartitaIvaAz = StringUtils.getAttributeStrNotNull(rigaMob, "STRAZPARTITAIVA");
			strRagioneSocialeAz = StringUtils.getAttributeStrNotNull(rigaMob, "STRAZRAGIONESOCIALE");
			codTipoAzienda = StringUtils.getAttributeStrNotNull(rigaMob, "CODAZTIPOAZIENDA");
			strCapUAz = StringUtils.getAttributeStrNotNull(rigaMob, "STRUACAP");
	        codAtecoUAz = StringUtils.getAttributeStrNotNull(rigaMob, "CODAZATECO");
	        codCCNLAz = StringUtils.getAttributeStrNotNull(rigaMob, "CODAZCCNL");
	        descrCCNLAz = StringUtils.getAttributeStrNotNull(rigaMob, "DESCCCNL");
	        strTelUAz = StringUtils.getAttributeStrNotNull(rigaMob, "STRUATEL");
	        strFaxUAz = StringUtils.getAttributeStrNotNull(rigaMob, "STRUAFAX");
	        strUAMail = StringUtils.getAttributeStrNotNull(rigaMob, "STREMAIL");
	        codComune = StringUtils.getAttributeStrNotNull(rigaMob, "CODUACOM");
	        strComuneUAz = StringUtils.getAttributeStrNotNull(rigaMob, "STRDESCRCOMUNE");
	        strDesAtecoUAz = StringUtils.getAttributeStrNotNull(rigaMob, "STRDESCRATECO");
	        descrCCNLAz = StringUtils.getAttributeStrNotNull(rigaMob, "DESCCCNL"); 
	        strNumRegistroCommitt = StringUtils.getAttributeStrNotNull(rigaMob, "STRNUMREGISTROCOMMITT");
	        strNumAlboInterinali = StringUtils.getAttributeStrNotNull(rigaMob, "STRAZNUMALBOINTERINALI");
	        posInps = StringUtils.getAttributeStrNotNull(rigaMob, "STRPOSINPS");       
	        flgNonImprenditore = StringUtils.getAttributeStrNotNull(rigaMob, "FLGNONIMPRENDITORE"); 
			flgCasoDubbio = StringUtils.getAttributeStrNotNull(rigaMob, "FLGCASODUBBIO");
			codDomanda = StringUtils.getAttributeStrNotNull(rigaMob, "CODDOMANDA");
		}
	  	//se vengo dalla linguettà mobilità i valori relativi alla mobilità vanno presi
	  	//dalla request perchè potrebbero essere stati modificati
	  	if (serviceRequest.containsAttribute("PROVENIENZA") && 
	  		serviceRequest.getAttribute("PROVENIENZA").toString().equals("linguetta"))  {
	  		codTipoMob = (String) serviceRequest.getAttribute("codTipoMob");
	  		dataInizioMov = (String) serviceRequest.getAttribute("datInizMovHid");
	  		dataFineMov = (String) serviceRequest.getAttribute("datFineMovHid");
	  		dataInizioMob = (String) serviceRequest.getAttribute("datInizioHid");
	  		dataFineMob = (String) serviceRequest.getAttribute("datFine");
	  		dataFineMobOrig = (String) serviceRequest.getAttribute("datFineOrig");
	  		dataMaxDiff = (String) serviceRequest.getAttribute("datMaxDiff");
	  		indennita_flg = (String) serviceRequest.getAttribute("flgIndennita");
	  		dataInizioIndennita = (String) serviceRequest.getAttribute("dataInizioIndenn");
	  		dataFineIndennita = (String) serviceRequest.getAttribute("dataFineIndenn");
	  		dataCRT = (String) serviceRequest.getAttribute("datCRT");
	  		codMotivoFine = (String) serviceRequest.getAttribute("MotDecad");
	  		codCCNLMov = (String) serviceRequest.getAttribute("codCCNL");
	  		descrCCNLMov = (String) serviceRequest.getAttribute("strCCNL");
	  		codGradoMov = (String) serviceRequest.getAttribute("codGradoHid");
	  		strLivello = (String) serviceRequest.getAttribute("strLivello");
	  		codMansione = (String) serviceRequest.getAttribute("CODMANSIONE");
			descMansione = (String) serviceRequest.getAttribute("DESCMANSIONE");
			numCRT = (String) serviceRequest.getAttribute("numCRT");
			regCRT = (String) serviceRequest.getAttribute("regioneCRT");
			provCRT = (String) serviceRequest.getAttribute("provCRT");
			motScorrDataMaxDiff = (String) serviceRequest.getAttribute("MotScorrDataMaxDiff");
			numOreSett = (String)serviceRequest.getAttribute("numOreSett");
		    dataDomanda = (String)serviceRequest.getAttribute("dataDomanda");
		    flgNonImprenditore = serviceRequest.getAttribute("flgNonImprenditore").toString();
		    flgCasoDubbio = StringUtils.getAttributeStrNotNull(serviceRequest, "flgCasoDubbio");
			codDomanda = StringUtils.getAttributeStrNotNull(serviceRequest, "CODDOMANDA");
	  	}
	  	else {
	  		if (rigaMob != null) {
		  		codTipoMob = StringUtils.getAttributeStrNotNull(rigaMob, "CODTIPOMOB");
		  		dataInizioMov = StringUtils.getAttributeStrNotNull(rigaMob, "DATINIZIOMOV");
	  			dataFineMov = StringUtils.getAttributeStrNotNull(rigaMob, "DATFINEMOV");
		  		dataInizioMob = StringUtils.getAttributeStrNotNull(rigaMob, "DATINIZIO");
		  		dataFineMob = StringUtils.getAttributeStrNotNull(rigaMob, "DATFINE");
		  		dataFineMobOrig = dataFineMob;
				dataInizioIndennita = StringUtils.getAttributeStrNotNull(rigaMob, "DATINIZIOINDENNITA");
		  		dataFineIndennita = StringUtils.getAttributeStrNotNull(rigaMob, "DATFINEINDENNITA");
		  		motScorrDataMaxDiff = StringUtils.getAttributeStrNotNull(rigaMob, "CODMOTIVODIFF");
		  		dataMaxDiff = StringUtils.getAttributeStrNotNull(rigaMob, "DATMAXDIFF");
		  		dataCRT = StringUtils.getAttributeStrNotNull(rigaMob, "DATCRT");
				regCRT = StringUtils.getAttributeStrNotNull(rigaMob, "regioneCRT");
				provCRT = StringUtils.getAttributeStrNotNull(rigaMob, "provCRT");
				numCRT = StringUtils.getAttributeStrNotNull(rigaMob, "NUMCRT");
		  		codMotivoFine = StringUtils.getAttributeStrNotNull(rigaMob, "CODMOTIVOFINE");
		  		indennita_flg = StringUtils.getAttributeStrNotNull(rigaMob, "FLGINDENNITA");
		  		codCCNLMov = StringUtils.getAttributeStrNotNull(rigaMob, "CODCCNL");
		  		descrCCNLMov = StringUtils.getAttributeStrNotNull(rigaMob, "DESCCCNLMOV");
		  		codGradoMov = StringUtils.getAttributeStrNotNull(rigaMob, "CODGRADO");
				strLivello = StringUtils.getAttributeStrNotNull(rigaMob, "STRLIVELLO");
				codMansione = StringUtils.getAttributeStrNotNull(rigaMob, "CODMANSIONE");
				descMansione = StringUtils.getAttributeStrNotNull(rigaMob, "DESCMANSIONE");
				flgNonImprenditore = StringUtils.getAttributeStrNotNull(rigaMob, "FLGNONIMPRENDITORE"); 
				flgCasoDubbio = StringUtils.getAttributeStrNotNull(rigaMob, "FLGCASODUBBIO");
				codDomanda = StringUtils.getAttributeStrNotNull(rigaMob, "CODDOMANDA");
	  		}
	  	}
	  	
	  	if (vectRicercaLav.size() > 0) {
	    	SourceBean rigaLavDB = (SourceBean) vectRicercaLav.elementAt(0);
	    	cdnLavoratoreDB = rigaLavDB.getAttribute("CDNLAVORATORE").toString();
	    	strCodiceFiscaleDB = StringUtils.getAttributeStrNotNull(rigaLavDB, "STRCODICEFISCALE");
			strNomeLavDB = StringUtils.getAttributeStrNotNull(rigaLavDB, "STRNOME");
			strCognomeLavDB = StringUtils.getAttributeStrNotNull(rigaLavDB, "STRCOGNOME");
			datNascLavDB = StringUtils.getAttributeStrNotNull(rigaLavDB, "DATNASC");
	    	strFlgCfOk = StringUtils.getAttributeStrNotNull(rigaLavDB, "FLGCFOK");
	    	strSessoLav = StringUtils.getAttributeStrNotNull(rigaLavDB, "STRSESSO");
	    	codCittadinanzaLav = StringUtils.getAttributeStrNotNull(rigaLavDB, "CODCITTADINANZA");
	    	codComDom = StringUtils.getAttributeStrNotNull(rigaLavDB, "CODCOMDOM"); 
	    	strIndirizzoDom = StringUtils.getAttributeStrNotNull(rigaLavDB, "STRINDIRIZZODOM");
	    	strCapDom = StringUtils.getAttributeStrNotNull(rigaLavDB, "STRCAPDOM");
	    	strTelDom = StringUtils.getAttributeStrNotNull(rigaLavDB, "STRTELDOM");
	    	codComRes = StringUtils.getAttributeStrNotNull(rigaLavDB, "CODCOMRES"); 
	    	strIndirizzoRes = StringUtils.getAttributeStrNotNull(rigaLavDB, "STRINDIRIZZORES");
	    	strCapRes = StringUtils.getAttributeStrNotNull(rigaLavDB, "STRCAPRES");
	    	strTelRes = StringUtils.getAttributeStrNotNull(rigaLavDB, "STRTELRES");
	  	}
	  	else {
	  		if (rigaMob != null) {
		  		//prendo i dati relativi al lavoratore dalla mobilità che si deve validare
		    	strSessoLav = StringUtils.getAttributeStrNotNull(rigaMob, "STRSESSO");
		    	codComNascLav = StringUtils.getAttributeStrNotNull(rigaMob, "CODCOMNASC");
		    	codCittadinanzaLav = StringUtils.getAttributeStrNotNull(rigaMob, "CODCITTADINANZA");
		    	codComDom = StringUtils.getAttributeStrNotNull(rigaMob, "CODCOMDOM"); 
		    	strIndirizzoDom = StringUtils.getAttributeStrNotNull(rigaMob, "STRINDIRIZZODOM");
		    	strCapDom = StringUtils.getAttributeStrNotNull(rigaMob, "STRCAPDOM");
		    	strTelDom = StringUtils.getAttributeStrNotNull(rigaMob, "STRTELDOM");
		    	codComRes = StringUtils.getAttributeStrNotNull(rigaMob, "CODCOMRES"); 
		    	strIndirizzoRes = StringUtils.getAttributeStrNotNull(rigaMob, "STRINDIRIZZORES");
		    	strCapRes = StringUtils.getAttributeStrNotNull(rigaMob, "STRCAPRES");
		    	strTelRes = StringUtils.getAttributeStrNotNull(rigaMob, "STRTELRES");
			}    	
	  	}
  		Vector vectRicercaAz = serviceResponse.getAttributeAsVector("M_MovRicercaAziendaCF.ROWS.ROW"); 
		Vector vectRicercaComuneUnitaAz = serviceResponse.getAttributeAsVector("M_MovRicercaUnitaAziendaCF.ROWS.ROW");
		
	  	Vector vectorRicercaIndirizzoUnitaAz = new Vector();
	  	if (vectRicercaAz.size() == 1) {
			SourceBean rigaAzValMov = (SourceBean) vectRicercaAz.elementAt(0);
			strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull(rigaAzValMov, "strcodicefiscale");
			strPartitaIvaAzDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "strpartitaiva");
			strFlgDatiOk = StringUtils.getAttributeStrNotNull(rigaAzValMov, "FLGDATIOK");
			ragSocDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "strragionesociale");
			natGiuridicaAz = StringUtils.getAttributeStrNotNull(rigaAzValMov, "descGiuridica");
	    	codNatGiuridicaAz = StringUtils.getAttributeStrNotNull(rigaAzValMov, "codnatgiuridica");
	    	patInailDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "strpatinail");
	    	codTipoAziendaDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "codtipoazienda");
	    	descrTipoAziendaDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "descTipoAzienda");
	    	strNumAlboInterinaliDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "strnumalbointerinali");
	    	prgAziendaBig = (BigDecimal)rigaAzValMov.getAttribute("prgAzienda");
			//Gestione aziende interinali
			boolean assInterna = false;
			boolean interinale = codTipoAzienda.equalsIgnoreCase(codInterinale);
			String flgAssPropria = "";
			if (interinale) {
				if (flgInterAssPropria.equalsIgnoreCase("S")) {
			    	assInterna = true;
			      	flgAssPropria = "S";
			    } 
			    else {
			      	flgAssPropria = "N";      
			    }
			}
			//Estraggo dalla ricerca le unita che hanno lo stesso indirizzo riportato nel movimento
			for (int i = 0; i < vectRicercaComuneUnitaAz.size(); i++) {
				SourceBean unita = (SourceBean) vectRicercaComuneUnitaAz.get(i);
				String indUnita = StringUtils.getAttributeStrNotNull(unita, "strindirizzo");
				if (indUnita.equalsIgnoreCase(strIndirizzoUAz)) {
					vectorRicercaIndirizzoUnitaAz.add(unita);
				}
			}
			numUnitaConIndirizzoDiverso = vectRicercaComuneUnitaAz.size();
			numUnitaConIndirizzoUguale = vectorRicercaIndirizzoUnitaAz.size();
			SourceBean rigaUAzValMov = null;
			if (numUnitaConIndirizzoUguale == 1) {
				//Unita trovata sul DB	      
				rigaUAzValMov = (SourceBean) vectorRicercaIndirizzoUnitaAz.elementAt(0);
			}
			else {
				//Guardo se in sessione ho una scelta dell'utente in merito all'unita aziendale
				NavigationCache sceltaUnitaAzienda = (NavigationCache) sessionContainer.getAttribute("SCELTAUNITAAZIENDA_VALIDAZIONEMOB");
				String prgUnitaTMP = null;
			  	if (sceltaUnitaAzienda != null) {
			  		if (prgMobilitaIscrApp.equals(sceltaUnitaAzienda.getField("PRGMOBILITAISCRAPP").toString())) {
				  		prgUnitaTMP = (String) sceltaUnitaAzienda.getField("PRGUNITA");
						//Se non sono nulli setto l'unità aziendale
						if (prgUnitaTMP != null) { 
					      	//Cerco nel vettore delle unita quella con progressivo unita coincidente
							for (int j = 0; j < vectRicercaComuneUnitaAz.size(); j++) {
								SourceBean unitaCorrente = (SourceBean) vectRicercaComuneUnitaAz.get(j);
								String prgUnitaCorrente = unitaCorrente.getAttribute("PRGUNITA").toString();
								if (prgUnitaCorrente.equals(prgUnitaTMP)) {
									rigaUAzValMov = unitaCorrente;
									unitaDaSessione = true;
								}
							}
				    	}
				   	}
				   	else {
				   		//Se il prgMobilitaIscrApp non coincide cancello l'oggetto perchè è vecchio
						sessionContainer.delAttribute("SCELTAUNITAAZIENDA_VALIDAZIONEMOB");
				   	}
			  	}
			}
			
			if (rigaUAzValMov != null) {
				prgUnitaBig = (BigDecimal)rigaUAzValMov.getAttribute("prgUnita");
				strIndirizzoDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strindirizzo");
				codComuneDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "codcom");
		    	strComuneDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strDescrComune");
		        capDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strcap");
		        codAtecoDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "codateco");
		        strDesAtecoDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strDescrAteco");
		        codCcnlDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "codccnl");
		        descrCCNLDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "descCCNL");
		        strTelDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strtel");
		        strFaxDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strfax");
		        strEmailDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "STREMAIL");
		        posInpsDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strnumeroinps");
		        strNumRegistroCommittDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "STRNUMREGISTROCOMMITT");
			}
		}	
  	}
  	
	String strNomeCognomeLav = strCognomeLav + " " + strNomeLav;
  	
  	if (!strFlgCfOk.equals("")) {
		if (strFlgCfOk.equalsIgnoreCase("S")) {
	    	strFlgCfOk = "Si";
	   	}
	   	else {
        	if (strFlgCfOk.equalsIgnoreCase("N")) {
            	strFlgCfOk = "No";
          	}
      	}  
	}
	//Oggetti per l'applicazione dello stile
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
		
	if (prgAziendaBig != null) prgAzienda = prgAziendaBig.toString();
	if (prgUnitaBig != null) prgUnita = prgUnitaBig.toString();
		
	if (!strFlgDatiOk.equals("")) {
	    if (strFlgDatiOk.equalsIgnoreCase("S")) {
	    	strFlgDatiOk = "Si";
	    }
	    else {
	        if (strFlgDatiOk.equalsIgnoreCase("N")) {
	          strFlgDatiOk = "No";
	        }
		} 
	}
	
	//Gestione spezzatino Pat Inail
	patInail1 = patInail.substring(0, (patInail.length() >= 8 ? 8 : patInail.length()));
	patInail2 = patInail.substring((patInail.length() >= 8 ? 8 : patInail.length()), (patInail.length() >= 10 ? 10 : patInail.length()));
	//Gestione spezzatino posizione Inps
	posInps1 = posInps.substring(0, (posInps.length() >= 2 ? 2 : posInps.length()));
	posInps2 = posInps.substring((posInps.length() >= 2 ? 2 : posInps.length()), (posInps.length() >= 15 ? 15 : posInps.length()));
	//Visualizzazione ragione sociale
	strRagioneSocialeAzTrunc = strRagioneSocialeAz;
	if (strRagioneSocialeAz.length() >= 30) {
		strRagioneSocialeAzTrunc = strRagioneSocialeAz.substring(0,26) + "...";
	}
	
	if (!validazioneEseguita) {
		//leggo i valori dalla request se sono presenti in quanto potrebbero essere stati
		//modificati rispetto a quelli presenti nella tabella di appoggio (inserimento lavoratore)
		if (serviceRequest.containsAttribute("datNascLav")) {
			datNascLav = serviceRequest.getAttribute("datNascLav").toString();	
		}
			
		if (vectRicercaLav.size() > 0) {
		    if ((!strCodiceFiscaleLav.toUpperCase().equals(strCodiceFiscaleDB.toUpperCase())) || 
		     (!strNomeLav.toUpperCase().equals(strNomeLavDB.toUpperCase())) ||     
		     (!strCognomeLav.toUpperCase().equals(strCognomeLavDB.toUpperCase())) ||
		     (!datNascLav.equals(datNascLavDB))) {
		     flgCambiamentiDati = "L";
		    }
		}
		
		if ( (prgAzienda != null && !prgAzienda.equals("")) &&
	    	 (prgUnita != null && !prgUnita.equals("")) ) {
	    	//Confronto i dati per vedere se ci sono differenze
		  	if ( (!strIndirizzoUAz.equals("") && !strIndirizzoUAz.equalsIgnoreCase(strIndirizzoDB)) ||     
		   	  	 (!codAtecoUAz.equals("") && !codAtecoUAz.equals(codAtecoDB)) ||
		      	 (!strTelUAz.equals("") && !strTelUAz.equals(strTelDB)) || 
		      	 (!strFaxUAz.equals("") && !strFaxUAz.equals(strFaxDB)) ||  
		      	 (!codComune.equals("") && !codComune.equals(codComuneDB)) || 
		      	 (!strCapUAz.equals("") && !strCapUAz.equals(capDB)) ||
		      	 (!codCCNLAz.equals("") && !codCCNLAz.equalsIgnoreCase(codCcnlDB)) || 
		      	 (!strUAMail.equals("") && !strUAMail.equals(strEmailDB)) ||
		      	 (!strPartitaIvaAz.equals("") && !strPartitaIvaAz.equals(strPartitaIvaAzDB)) ||
		      	 (!strRagioneSocialeAz.equals("") && !strRagioneSocialeAz.equalsIgnoreCase(ragSocDB)) ||
		      	 (!strNumAlboInterinali.equals("") && !strNumAlboInterinali.equals(strNumAlboInterinaliDB)) ) {
		  		datiAziendaAggiornati = false;
		  	}
		  	//Visualizzo i dati dell'azienda contenuti sul DB invece di
		  	//quelli del movimento associato alla mobilità che si deve validare
	 	  	strIndirizzoUAz = strIndirizzoDB; 
		  	strComuneUAz = strComuneDB;
		  	codComune = codComuneDB;    
		  	strCapUAz = capDB;   
		  	strTelUAz = strTelDB;  
		  	strFaxUAz = strFaxDB; 
		  	strUAMail = strEmailDB; 
		  	codAtecoUAz = codAtecoDB;  
		  	strDesAtecoUAz = strDesAtecoDB; 
		  	strNumRegistroCommitt = strNumRegistroCommittDB;
		  	codCCNLAz = codCcnlDB;  
		  	descrCCNLAz = descrCCNLDB;
		}
	}
	
	
	
%>
 
<html>
  <head>
    <%@ include file="../global/fieldChanged.inc" %>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    <title>Validazione manuale mobilità</title>
    <script language="Javascript">
    <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
   	%>
   	var opened;
    var _funzione = '<%=_funzione%>';
    var codInterinale = '<%=codInterinale%>';
    var codTipoAzienda = '<%=codTipoAzienda%>';
    var codNatGiuridicaAz = '<%=codNatGiuridicaAz%>';
    var canModify = '<%=canModify%>';
    var inserisci = 'false';
    var contesto = 'valida';
    var prgMobilitaApp = '<%=prgMobilitaIscrApp%>';
    </SCRIPT>
    
    <script language="Javascript">
    
    function gestioneMobilita () {
    	document.Frm1.submit();
    }
    
 	</script>    
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
  </head>
 
  <body class="gestione">
  <div class="menu">
	<a id="linguettaGenerale" href='#' class='sel1'><span class='tr_round1'>&nbsp;Generale&nbsp;</span></a>
	<a id="linguettaMobilita" href='#' onclick='gestioneMobilita();' class='bordato1'><span class='tr_round11'>&nbsp;Mobilità&nbsp;</span></a>
  </div>
  <af:form name="Frm1" method="POST" action="AdapterHTTP">
	<center>
  	<%out.print(htmlStreamTop);%>
  	<%@ include file="validazione/include/_gestisciAzienda.inc" %>
  	<table class="main" border="0" width="96%" cellpadding="0" cellspacing="0">
  	 <tr>
    	<td>          
	      <div class='sezione2' id='SedeAzienda'>
	        <img id='tendinaAzienda' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("datiAzienda"));'/>
	        Sede Azienda&nbsp;&nbsp;
	        <%if (prgAzienda.equals("")) { //Devo inserire l'azienda%>
	          <a href="#" onClick="javascript:apriInserisciAzienda();"><img src="../../img/add2.gif" alt="nuova azienda" title="Inserisci azienda"></a>
	        <%} else if ( (prgUnita.equals("")) && (numUnitaConIndirizzoDiverso == 0) ) { //Devo inserire l'unita%>
	          <a href="#" onClick="javascript:apriInserisciUnitaAzienda();"><img src="../../img/add2.gif" alt="nuova unità" title="Inserisci unit&agrave; aziendale"></a>  
	        <%} else if ( (prgUnita.equals("")) && (numUnitaConIndirizzoDiverso > 0) ) { //Devo scegliere l'unita%>
	          <a href="#" title="Scegli unita aziendale" onClick="javascript:apriScegliUnitaAzienda(document.Frm1.PRGAZIENDA.value,<%=_funzione%>);"><img src="../../img/binocolo.gif" alt="scegli unità tra quelle trovate" title="scegli unità tra quelle trovate"></a>      	
	        <%} else if (!prgAzienda.equals("") && !prgUnita.equals("")) { //Ho trovato anche l'unita%>
	          <a href="#" onClick="javascript:apriUnitaAziendale(document.Frm1.PRGAZIENDA.value,document.Frm1.PRGUNITA.value,<%=_funzione%>);"><img src="../../img/detail.gif" alt="Dettaglio azienda" title="Dettaglio azienda"></a>	
		      <% if (!datiAziendaAggiornati) {%>&nbsp;
		        <a href="#" onClick="javascript:apriAggiornaAzienda(document.Frm1.PRGAZIENDA.value,document.Frm1.PRGUNITA.value,<%=_funzione%>,document.Frm1.prgMobilitaIscrApp.value);"><img src="../../img/DB_img.gif" title="Aggiorna dati azienda" alt="Aggiorna dati azienda" title="Aggiorna dati azienda"></a>  
		        <%}
		     }%>  
	      </div>         
    	</td>
     </tr>
     
  	  <!-- sezione riservata all'azienda  -->
      <%@ include file="validazione/include/azienda.inc" %>
  	  <tr>
      <td>          
        <div class="sezione2" id="lavoratore">
          <img id="tendinaLavoratore" alt="Chiudi" src="../../img/aperto.gif" onclick='cambia(this, document.getElementById("datiLavoratore"));'/>
          Lavoratore&nbsp;&nbsp;
          <%if (!validazioneEseguita && vectRicercaLav.size() == 0) {%>
		  	<%@ include file="validazione/include/_inserisciLavoratore.inc" %>
		  <%}  
          boolean modificaTitolo = true;%>
          <%@ include file="validazione/include/_titoloDiStudio.inc" %>
		  <%@ include file="validazione/include/_sintesiLavoratore.inc" %> 
		  <%@ include file="validazione/include/_movimentiLavoratore.inc" %>
        </div>
      </td>
    </tr>
	<!-- sezione riservata al lavoratore  -->
	<%@ include file="validazione/include/_lavoratore.inc" %>
	</table>
	<%out.print(htmlStreamBottom);%>
	<input type="hidden" name="PAGE" value="ValidazioneMobilitaDettaglioPage" />
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratoreDB%>"/>
	<input type="hidden" name="prgMobilitaIscrApp" value="<%=prgMobilitaIscrApp%>"/>
	<input type="hidden" name="codTipoMob" value="<%=codTipoMob%>"/>
	<input type="hidden" name="datInizMov" value="<%=dataInizioMov%>"/>
	<input type="hidden" name="datInizMovHid" value="<%=dataInizioMov%>"/>
	<input type="hidden" name="datFineMov" value="<%=dataFineMov%>"/>
	<input type="hidden" name="datFineMovHid" value="<%=dataFineMov%>"/>
	<input type="hidden" name="datInizio" value="<%=dataInizioMob%>"/>
	<input type="hidden" name="datInizioHid" value="<%=dataInizioMob%>"/>
	<input type="hidden" name="datFineOrig" value="<%=dataFineMobOrig%>"/>
	<input type="hidden" name="datFine" value="<%=dataFineMob%>"/>
	<input type="hidden" name="dataInizioIndenn" value="<%=dataInizioIndennita%>"/>
	<input type="hidden" name="dataFineIndenn" value="<%=dataFineIndennita%>"/>
	<input type="hidden" name="datMaxDiff" value="<%=dataMaxDiff%>"/>
	<input type="hidden" name="datCRT" value="<%=dataCRT%>"/>
	<input type="hidden" name="MotDecad" value="<%=codMotivoFine%>"/>
	<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
	<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
	<input type="hidden" name="DESCMANSIONE" value="<%=descMansione%>"/>
	<input type="hidden" name="codComune" value="<%=codComune%>"/>
	<input type="hidden" name="strComuneUAz" value="<%=strComuneUAz%>"/>
	<input type="hidden" name="strUACap" value="<%=strCapUAz%>"/>
	<input type="hidden" name="strUAMail" value="<%=strUAMail%>"/>
	<input type="hidden" name="dataComunicaz" value="<%=dataComunicaz%>"/>
	<input type="hidden" name="CODTIPOASS" value="NO0"/>
 	<input type="hidden" name="CODTIPOMOV" value="AVV"/>
 	<input type="hidden" name="CODMONOTEMPO" value="I"/>
 	<input type="hidden" name="codTipoAzienda" value="<%=codTipoAzienda%>"/>
	<input type="hidden" name="codMansione" value="<%=codMansione%>"/>
	<input type="hidden" name="codGrado" value="<%=codGradoMov%>"/>
	<input type="hidden" name="codGradoHid" value="<%=codGradoMov%>"/>
	<input type="hidden" name="codCCNL" value="<%=codCCNLMov%>"/>
	<input type="hidden" name="strCCNL" value="<%=descrCCNLMov%>"/>
	<input type="hidden" name="strLivello" value="<%=strLivello%>"/>
	<input type="hidden" name="flagDiff" value="<%=flagDiff%>"/>
	<input type="hidden" name="flgIndennita" value="<%=indennita_flg%>"/>
	<input type="hidden" name="flgCambiamentiDati" value="<%=flgCambiamentiDati%>">
	<input type="hidden" name="numCRT" value="<%=numCRT%>">
	<input type="hidden" name="regioneCRT" value="<%=regCRT%>">
	<input type="hidden" name="provCRT" value="<%=provCRT%>">
	<input type="hidden" name="MotScorrDataMaxDiff" value="<%=motScorrDataMaxDiff%>">
	<input type="hidden" name="prgMovimento" value="<%=prgMovimento%>">
	<input type="hidden" name="numOreSett" value="<%=numOreSett%>">
	<input type="hidden" name="dataDomanda" value="<%=dataDomanda%>">
	<input type="hidden" name="flgNonImprenditore" value="<%=flgNonImprenditore%>">
	<input type="hidden" name="flgCasoDubbio" value="<%=flgCasoDubbio%>">
	<input type="hidden" name="CODDOMANDA" value="<%=codDomanda%>">
	<%if (validazioneEseguita) {%>
		<input type="hidden" name="VALIDAZIONE_MANUALE" value="OK"/>
	<%}%>
	<center>
	<%@ include file="validazione/include/PulsanteRitornoLista.inc" %> 
	</center>
    </af:form>
    
    <%if (!validazioneEseguita && vectRicercaLav.size() == 0) {%>
	    <af:form name="frmLav">
	    <input type="hidden" name="codFiscaleLavMov" value="<%=strCodiceFiscaleLav%>">
	    <input type="hidden" name="nomeLavMov" value="<%=strNomeLav%>">
	    <input type="hidden" name="cognomeLavMov" value="<%=strCognomeLav%>">
	    <input type="hidden" name="datNascitaLavMov" value="<%=datNascLav%>">
	    </af:form>
	<%}%>
	
 </body>
</html>