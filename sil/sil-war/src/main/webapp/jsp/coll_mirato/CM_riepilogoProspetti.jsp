<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"
	import="com.engiweb.framework.base.*,com.engiweb.framework.error.*,it.eng.sil.security.*,
			it.eng.afExt.utils.*,it.eng.sil.util.*,java.math.*,com.engiweb.framework.message.*,
			it.eng.afExt.utils.MessageCodes,java.util.*,
			it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%
	String queryString = null;
	ProfileDataFilter filter = new ProfileDataFilter(user,
			"CMProspRiepilogoPage");
	boolean canView = filter.canView();
	if (!canView) {
		response
				.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

	ResponseContainer respCont = ResponseContainerAccess
			.getResponseContainer(request);

	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"PAGE");
	String back_page = serviceRequest.getAttribute("BACK_PAGE") == null ? ""
			: (String) serviceRequest.getAttribute("BACK_PAGE");
	String message = "INSERT";
	
	// Prendo la config per Non Vedente e Masso Fisioterapista
	String resultConfigNonVedenti_MassoFisioterapista = serviceResponse.containsAttribute("M_GetConfig_NonVedenti_MassoFisioterapista.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfig_NonVedenti_MassoFisioterapista.ROWS.ROW.NUM").toString():"0";
	// Prendo la config per la Legge Battistoni
	String conf_battistoni = serviceResponse.containsAttribute("M_GetConfig_LeggeBattistoni.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfig_LeggeBattistoni.ROWS.ROW.NUM").toString():"0";
    String configEtichette = serviceResponse.containsAttribute("M_GetConfigEtichette_PI.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigEtichette_PI.ROWS.ROW.NUM").toString():"0";
    
    boolean checkProspetto2011 = serviceResponse.containsAttribute("M_Check_Prospetti_Inviati_Entro_Il_31122011.ROWS.ROW.PROSPETTO2011") && serviceResponse.getAttribute("M_Check_Prospetti_Inviati_Entro_Il_31122011.ROWS.ROW.PROSPETTO2011").toString().equalsIgnoreCase("TRUE")?true:false;
    
    int numLavArt18Batt = 0;
    if (!checkProspetto2011) {
    	SourceBean rowLav68 = serviceResponse.containsAttribute("CMProspLavL68ListModule.ROWS")?(SourceBean)serviceResponse.getAttribute("CMProspLavL68ListModule.ROWS"):null;
	    if (rowLav68 != null) {
	    	Vector listaLav68 = rowLav68.getAttributeAsVector("ROW");
	    	for (int i=0;i<listaLav68.size();i++) {
	    		SourceBean rowLav = (SourceBean)listaLav68.get(i);
	    		String categoria = StringUtils.getAttributeStrNotNull(rowLav, "CODMONOCATEGORIA");
	    		if (categoria.equalsIgnoreCase("A")) {
	    			String dataInizioRapp = StringUtils.getAttributeStrNotNull(rowLav, "datiniziorapp");
	    			if (!dataInizioRapp.equals("") && DateUtils.compare(dataInizioRapp, "17/01/2000") <= 0) {
	    				numLavArt18Batt = numLavArt18Batt + 1;	
	    			}
	    		}
	    	}	
	    }
    }
    
    String checkSospensione = "-1";
	String checkScopertura = "-1";
	String checkExistForza = "-1";
	String err_percEsonero = "-1";
	String err_percCompensazione = "-1";
	String err_numCompInProv = "-1";
	String err_numCompArt18 = "-1";
	String err_numCompRiduz = "-1";
	String err_numCompensEcc = "-1";
	String err_dataConsegnaProspetto = "-1";
	String err_dataProspetto = "-1";
	String err_dataRiferimento = "-1";
	String err_checkProspStoriciz = "-1";
	String codiceRit = "-1";
	String codiceRitSare = "-1";

	String totaleEsclusione = "";
	String totaleEsclusioneArt18 = "";
	String prgProspettoInf = "";
	String prgAzienda = "";
	String prgUnita = "";
	String prgAzReferente = "";
	String numOreCcnl = "";
	String codProvincia = "";
	String codMonoStatoProspetto = "";
	String codMonoCategoria = "";
	String codMonoProv = "";
	String numAnnoRifProspetto = "";
	String datConsegnaProspetto = "";
	String datRifInForza = "";
	String datProspetto = "";
	String numDipendentiNazionale = "";
	String codMonocategoria = "";
	String numDipendentiTot = "";
	String numBaseComputo = "";
	String numBaseComputoArt3 = "";
	String numBaseComputoArt18 = "";
	String numBaseComputoArt3Naz = "";
	String numBaseComputoArt18Naz = "";
	String numQuotaDisabili = "";
	String numDisabiliNom = "";
	String numDisabiliNum = "";
	String numQuotaArt18 = "";
	String numArt18Nom = "";
	String numArt18Num = "";
	String datPrimaAssunzione = "";
	String datSecondaSssunzione = "";
	String numDisForza = "";
	String numDisForzaRifNomi = "";
	String numDisForzaNomi = "";
	String numDisForzaRifNume = "";
	String numDisForzaNume = "";
	String numArt18Forza = "";
	String numArt18ForzaRifNomi = "";
	String numArt18ForzaNomi = "";
	String numArt18ForzaRifNume = "";
	String numArt18ForzaNume = "";
	String flgEsonero = "";
	String flgEsoneroAutCert = "";
	String esoneroParziale = "NO";
	String esoneroParzAutCert = "NO";
	String sospensione = "NO";
	String compTerr = "NO";
	String gradualita = "NO";
	String numPercEsonero = "";
	String flgGradualita = "";
	String flgCompTerritoriale = "";
	String flgSospensione = "";
	String numConvenzioni = "";
	String numLavoratoriConv = "";
	// New Donisi Conv.
	String numtotlavconvart18 = "";
	String numtotlavconvdis = "";
	
	//New Scopertura
	String numscopcentnonvedenti = "";
	String numscopmassofisioterapisti = "";
	
	
	String dataProxAssConv = "";
	String numDisConvNume = "";
	String numDisConvNomi = "";
	String numArt18ConvNume = "";
	String numArt18ConvNomi = "";
	String numDisCompTerrNomi = "";
	String numDisCompTerrNume = "";
	String numArt18CompTerrNomi = "";
	String numArt18CompTerrNume = "";
	String numDisEsonNomi = "";
	String numDisEsonNume = "";
	String strNote = "";
	String numKloProspettoInf = "";
	String datFineEsonero = "";
	String flgEsonRichProroga = "";
	String datEsonRichProroga = "";
	String datConcGradualita = "";
	String numAssGradualita = "";
	String datSospensione = "";
	String numQuotaDisGrad = "";
	String numLavInForzaGrad = "";
	//New Donisi - l.Battistoni
	String numdisbattistoni = "";
	String numart18battistoni = "";
	String numbattistoniinforza = "";
	//New 

	// dati calcolati
	BigDecimal numBaseComputoBD = new BigDecimal(0);
	BigDecimal numDisForzaNomiTot = new BigDecimal(0);
	BigDecimal numDisForzaNumeTot = new BigDecimal(0);
	BigDecimal numDisConvTot = new BigDecimal(0);
	BigDecimal numDisCompTerrTot = new BigDecimal(0);
	BigDecimal numDisEsonTot = new BigDecimal(0);

	//f
	BigDecimal disScoperturaTot = new BigDecimal(0);
	BigDecimal disScoperturaTotNomi = new BigDecimal(0);
	BigDecimal disScoperturaTotNume = new BigDecimal(0);
	BigDecimal numArt18ForzaNomiTot = new BigDecimal(0);
	BigDecimal numArt18ForzaNumeTot = new BigDecimal(0);
	BigDecimal numArt18ConvTot = new BigDecimal(0);
	BigDecimal numArt18CompTerrTot = new BigDecimal(0);
	BigDecimal art18ScoperturaTot = new BigDecimal(0);
	BigDecimal art18ScoperturaTotNomi = new BigDecimal(0);
	BigDecimal art18ScoperturaTotNume = new BigDecimal(0);

	// variabili per la protocollazione
	String docInOut = "";
	String docRif = "";
	String docTipo = "";
	String docCodRif = "";
	String docCodTipo = "";
	BigDecimal numProtV = null;
	BigDecimal numAnnoProtV = null;
	String dataOraProt = "";
	boolean noButton = false;
	String datProtV = "";
	String oraProtV = "";
	Vector rowsProt = null;
	SourceBean rowProt = null;
	//String codStatoAtto = "NP";
	BigDecimal numAnnoProt = null;
	BigDecimal numProtocollo = null;
	String dataProt = "";
	String DatAcqRil = "";
	String DatInizio = "";
	String dataOdierna = "";
	String oraProt = "";
	Vector rowsDoc1 = null;
	SourceBean rowDoc1 = null;
	String codStatoAttoV = "";
	String prgDocumento = "";
	boolean canSalvaStato = false;
	boolean nuovoProspetto = false;
	String flgCompetenza = "S";
	String flgSedeProspetto = "";
	String numTDMeno9mesi = "";
	
	String messaggiVerifica = serviceResponse.containsAttribute("CMDettRiepilogoProspModule.MSGCONTROLLICONGRUENZA")?serviceResponse.getAttribute("CMDettRiepilogoProspModule.MSGCONTROLLICONGRUENZA").toString():"";

	InfCorrentiAzienda infCorrentiAzienda = null;
	//INFORMAZIONI OPERATORE
	Object cdnUtIns = null;
	Object dtmIns = null;
	Object cdnUtMod = null;
	Object dtmMod = null;
	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest,
			"cdnfunzione");
	Testata operatoreInfo = null;
	Linguette l = null;

	String codStatoAtto = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "codStatoAtto");
	String protocolla = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "protocolla");
	
	String statoPS = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "STATOPS");

	SourceBean prospetto = (SourceBean) serviceResponse
			.getAttribute("CMDettRiepilogoProspModule.ROWS.ROW");
	if (prospetto != null) {
		message = "UPDATE";
		
		numTDMeno9mesi = prospetto.getAttribute("NUMTDMENO9MESI") == null ? "" : ((BigDecimal)prospetto.getAttribute("NUMTDMENO9MESI")).toString();	
		flgSedeProspetto = StringUtils.getAttributeStrNotNull(prospetto,"flgSede");
		flgCompetenza = StringUtils.getAttributeStrNotNull(prospetto,"flgCompetenza");
		totaleEsclusione = (prospetto.getAttribute("totaleEsclusione") == null || ("")
				.equals(prospetto.getAttribute("totaleEsclusione"))) ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("totaleEsclusione")).toString();
		totaleEsclusioneArt18 = (prospetto.getAttribute("TOTALEESCLUSIONEART18") == null || ("")
				.equals(prospetto.getAttribute("TOTALEESCLUSIONEART18"))) ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("TOTALEESCLUSIONEART18")).toString();
		prgProspettoInf = prospetto.getAttribute("prgProspettoInf") == null ? ""
				: ((BigDecimal) prospetto
						.getAttribute("prgProspettoInf")).toString();
		prgAzienda = prospetto.getAttribute("prgAzienda") == null ? ""
				: ((BigDecimal) prospetto.getAttribute("prgAzienda"))
						.toString();
		prgUnita = prospetto.getAttribute("prgUnita") == null ? ""
				: ((BigDecimal) prospetto.getAttribute("prgUnita"))
						.toString();
		prgAzReferente = prospetto.getAttribute("prgAzReferente") == null ? ""
				: ((BigDecimal) prospetto
						.getAttribute("prgAzReferente")).toString();
		numOreCcnl = prospetto.getAttribute("numOreCcnl") == null ? ""
				: ((BigDecimal) prospetto.getAttribute("numOreCcnl"))
						.toString();
		codProvincia = (String) prospetto.getAttribute("codProvincia");
		codMonoStatoProspetto = (String) prospetto
				.getAttribute("codMonoStatoProspetto");
		codMonoCategoria = (String) prospetto
				.getAttribute("codMonoCategoria");
		codMonoProv = (String) prospetto.getAttribute("codMonoProv");
		numAnnoRifProspetto = prospetto
				.getAttribute("numAnnoRifProspetto") == null ? ""
				: ((BigDecimal) prospetto
						.getAttribute("numAnnoRifProspetto"))
						.toString();
		datConsegnaProspetto = prospetto
				.getAttribute("datConsegnaProspetto") == null ? ""
				: (String) prospetto
						.getAttribute("datConsegnaProspetto");
		datRifInForza = prospetto.getAttribute("datRifInForza") == null ? ""
				: (String) prospetto.getAttribute("datRifInForza");
		datProspetto = prospetto.getAttribute("datProspetto") == null ? ""
				: (String) prospetto.getAttribute("datProspetto");
		numDipendentiNazionale = prospetto
				.getAttribute("numDipendentiNazionale") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numDipendentiNazionale"))
						.toString();
		codMonocategoria = (String) prospetto
				.getAttribute("codMonoCategoria");
		numDipendentiTot = prospetto.getAttribute("numDipendentiTot") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numDipendentiTot")).toString();
		numBaseComputo = prospetto.getAttribute("numbasecomputo") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numbasecomputo")).toString();
		numBaseComputoArt3 = prospetto.getAttribute("numbasecomputoart3") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numbasecomputoart3")).toString();
		numBaseComputoArt18 = prospetto.getAttribute("numbasecomputoart18") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numbasecomputoart18")).toString();
		numBaseComputoArt3Naz = prospetto.getAttribute("numbasecomputoart3naz") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numbasecomputoart3naz")).toString();
		numBaseComputoArt18Naz = prospetto.getAttribute("numbasecomputoart18naz") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numbasecomputoart18naz")).toString();
		numQuotaDisabili = prospetto.getAttribute("numquotadisabili") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numquotadisabili")).toString();
		numDisabiliNom = prospetto.getAttribute("numdisabilinom") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numdisabilinom")).toString();
		numDisabiliNum = prospetto.getAttribute("numdisabilinum") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numdisabilinum")).toString();
		numQuotaArt18 = prospetto.getAttribute("numquotaart18") == null ? "0"
				: ((BigDecimal) prospetto.getAttribute("numquotaart18"))
						.toString();
		numArt18Nom = prospetto.getAttribute("numArt18nom") == null ? "0"
				: ((BigDecimal) prospetto.getAttribute("numArt18nom"))
						.toString();
		numArt18Num = prospetto.getAttribute("numArt18num") == null ? "0"
				: ((BigDecimal) prospetto.getAttribute("numArt18num"))
						.toString();
		datPrimaAssunzione = prospetto
				.getAttribute("datPrimaAssunzione") == null ? ""
				: (String) prospetto.getAttribute("datPrimaAssunzione");
		datSecondaSssunzione = prospetto
				.getAttribute("datSecondaSssunzione") == null ? ""
				: (String) prospetto
						.getAttribute("datSecondaSssunzione");
		numDisForza = prospetto.getAttribute("numdisforza") == null ? "0"
				: ((BigDecimal) prospetto.getAttribute("numdisforza"))
						.toString();
		numDisForzaRifNomi = prospetto
				.getAttribute("numDisForzarifnomi") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numDisForzarifnomi")).toString();
		numDisForzaNomi = prospetto.getAttribute("numDisForzanomi") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numDisForzanomi")).toString();
		numDisForzaRifNume = prospetto
				.getAttribute("numDisForzarifnume") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numDisForzarifnume")).toString();
		numDisForzaNume = prospetto.getAttribute("numDisForzanume") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numDisForzanume")).toString();
		numArt18Forza = prospetto.getAttribute("numart18forza") == null ? "0"
				: ((BigDecimal) prospetto.getAttribute("numart18forza"))
						.toString();
		numArt18ForzaRifNomi = prospetto
				.getAttribute("numArt18ForzaRifNomi") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numArt18ForzaRifNomi"))
						.toString();
		numArt18ForzaNomi = prospetto.getAttribute("numArt18ForzaNomi") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numArt18ForzaNomi")).toString();
		numArt18ForzaRifNume = prospetto
				.getAttribute("numArt18ForzaRifNume") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numArt18ForzaRifNume"))
						.toString();
		numArt18ForzaNume = prospetto.getAttribute("numArt18ForzaNume") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numArt18ForzaNume")).toString();
		flgEsonero = prospetto.getAttribute("flgEsonero") == null ? "" : prospetto.getAttribute("flgEsonero").toString();
		flgEsoneroAutCert = prospetto.getAttribute("FLGESONEROAUTOCERT") == null ? "" : prospetto.getAttribute("FLGESONEROAUTOCERT").toString();
		if (("S").equalsIgnoreCase(flgEsonero)) {
			esoneroParziale = "SI";
		}
		if (("S").equalsIgnoreCase(flgEsoneroAutCert)) {
			esoneroParzAutCert = "SI";
		}
		numPercEsonero = prospetto.getAttribute("numPercEsonero") == null ? ""
				: ((BigDecimal) prospetto
						.getAttribute("numPercEsonero")).toString();
		flgGradualita = (String) prospetto
				.getAttribute("flgGradualita");
		if (("S").equalsIgnoreCase(flgGradualita)) {
			gradualita = "SI";
		}
		flgCompTerritoriale = (String) prospetto
				.getAttribute("flgCompTerritoriale");
		if (("S").equalsIgnoreCase(flgCompTerritoriale)) {
			compTerr = "SI";
		}
		flgSospensione = (String) prospetto
				.getAttribute("flgSospensione");
		if (("S").equalsIgnoreCase(flgSospensione)) {
			sospensione = "SI";
		}
		numConvenzioni = prospetto.getAttribute("numConvenzioni") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numConvenzioni")).toString();
		numLavoratoriConv = prospetto.getAttribute("numLavoratoriConv") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numLavoratoriConv")).toString();
		
		//new conv
		numtotlavconvart18 = prospetto.getAttribute("numtotlavconvart18") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numtotlavconvart18")).toString();
		numtotlavconvdis = prospetto.getAttribute("numtotlavconvdis") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numtotlavconvdis")).toString();
		//new Scopertura
		
		numscopcentnonvedenti = prospetto.getAttribute("numscopcentnonvedenti") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numscopcentnonvedenti")).toString();
		numscopmassofisioterapisti = prospetto.getAttribute("numscopmassofisioterapisti") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numscopmassofisioterapisti")).toString();
		dataProxAssConv = prospetto.getAttribute("dataProxAssConv") == null ? ""
				: (String) prospetto.getAttribute("dataProxAssConv");
		numDisConvNume = prospetto.getAttribute("numDisConvNume") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numDisConvNume")).toString();
		numDisConvNomi = prospetto.getAttribute("numDisConvNomi") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numDisConvNomi")).toString();
		numArt18ConvNume = prospetto.getAttribute("numArt18ConvNume") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numArt18ConvNume")).toString();
		numArt18ConvNomi = prospetto.getAttribute("numArt18ConvNomi") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numArt18ConvNomi")).toString();
		numDisCompTerrNomi = prospetto
				.getAttribute("numDisCompTerrNomi") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numDisCompTerrNomi")).toString();
		numDisCompTerrNume = prospetto
				.getAttribute("numDisCompTerrNume") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numDisCompTerrNume")).toString();
		numArt18CompTerrNomi = prospetto
				.getAttribute("numArt18CompTerrNomi") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numArt18CompTerrNomi"))
						.toString();
		numArt18CompTerrNume = prospetto
				.getAttribute("numArt18CompTerrNume") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numArt18CompTerrNume"))
						.toString();
		numDisEsonNomi = prospetto.getAttribute("numDisEsonNomi") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numDisEsonNomi")).toString();
		numDisEsonNume = prospetto.getAttribute("numDisEsonNume") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numDisEsonNume")).toString();
		strNote = (String) prospetto.getAttribute("strNote");
		numKloProspettoInf = prospetto
				.getAttribute("numKloProspettoInf") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numKloProspettoInf")).toString();

		numdisbattistoni = prospetto.getAttribute("numdisbattistoni") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numdisbattistoni")).toString();

		numart18battistoni = prospetto
				.getAttribute("numart18battistoni") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numart18battistoni")).toString();

		numbattistoniinforza = prospetto
				.getAttribute("numbattistoniinforza") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("numbattistoniinforza"))
						.toString();

		datFineEsonero = prospetto.getAttribute("DATFINEESONERO") == null ? ""
				: (String) prospetto.getAttribute("DATFINEESONERO");
		flgEsonRichProroga = prospetto
				.getAttribute("FLGESONRICHPROROGA") == null ? ""
				: (String) prospetto.getAttribute("FLGESONRICHPROROGA");
		datEsonRichProroga = prospetto
				.getAttribute("DATESONRICHPROROGA") == null ? ""
				: (String) prospetto.getAttribute("DATESONRICHPROROGA");
		datConcGradualita = prospetto.getAttribute("DATCONCGRADUALITA") == null ? ""
				: (String) prospetto.getAttribute("DATCONCGRADUALITA");
		numAssGradualita = prospetto.getAttribute("NUMASSGRADUALITA") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("NUMASSGRADUALITA")).toString();
		datSospensione = prospetto.getAttribute("DATSOSPENSIONE") == null ? ""
				: (String) prospetto.getAttribute("DATSOSPENSIONE");
		numQuotaDisGrad = prospetto.getAttribute("NUMQUOTADISGRAD") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("NUMQUOTADISGRAD")).toString();
		numLavInForzaGrad = prospetto.getAttribute("NUMLAVINFORZAGRAD") == null ? "0"
				: ((BigDecimal) prospetto
						.getAttribute("NUMLAVINFORZAGRAD")).toString();

		codStatoAtto = (String) prospetto.getAttribute("codStatoAtto");
		numProtV = SourceBeanUtils.getAttrBigDecimal(prospetto,
				"NUMPROTOCOLLO", null);
		numAnnoProtV = SourceBeanUtils.getAttrBigDecimal(prospetto,
				"numAnnoProt", null);
		dataOraProt = StringUtils.getAttributeStrNotNull(prospetto,
				"dataOraProt");
		if (!dataOraProt.equals("")) {
			oraProtV = dataOraProt.substring(11, 16);
			datProtV = dataOraProt.substring(0, 10);
		}

		// calcolo basecomputo = tot numero dipendenti - tot esclusioni	    
		BigDecimal numDipendentiTotBD = new BigDecimal(numDipendentiTot);
		BigDecimal totaleEsclusioneBD = new BigDecimal(totaleEsclusione);
		numBaseComputoBD = numDipendentiTotBD
				.subtract(totaleEsclusioneBD);
		// disabili in forza nominativo		
		numDisForzaNomiTot = (new BigDecimal(numDisForzaNomi))
				.add(new BigDecimal(numDisForzaRifNomi));
		// disabili in forza numerico
		numDisForzaNumeTot = (new BigDecimal(numDisForzaNume))
				.add(new BigDecimal(numDisForzaRifNume));
		// art18 in forza nominativo		
		numArt18ForzaNomiTot = (new BigDecimal(numArt18ForzaNomi))
				.add(new BigDecimal(numArt18ForzaRifNomi));
		// art18 in forza numerico
		numArt18ForzaNumeTot = (new BigDecimal(numArt18ForzaNume))
				.add(new BigDecimal(numArt18ForzaRifNume));
		//tot convenzioni disabili
		numDisConvTot = (new BigDecimal(numDisConvNomi))
				.add(new BigDecimal(numDisConvNume));
		//tot convenzione art18 		
		numArt18ConvTot = (new BigDecimal(numArt18ConvNomi))
				.add(new BigDecimal(numArt18ConvNume));
		//tot comp terr disabili
		numDisCompTerrTot = (new BigDecimal(numDisCompTerrNomi))
				.add(new BigDecimal(numDisCompTerrNume));
		//tot comp terr art18 		
		numArt18CompTerrTot = (new BigDecimal(numArt18CompTerrNomi))
				.add(new BigDecimal(numArt18CompTerrNume));
		//tot esoneri disabili
		numDisEsonTot = (new BigDecimal(numDisEsonNomi))
				.add(new BigDecimal(numDisEsonNume));
		//scopertura nominativa disabile
		disScoperturaTotNomi = (new BigDecimal(numDisabiliNom))
				.subtract(numDisForzaNomiTot);
		disScoperturaTotNomi = disScoperturaTotNomi
				.subtract(new BigDecimal(numDisConvNomi));
		disScoperturaTotNomi = disScoperturaTotNomi.add(new BigDecimal(
				numDisCompTerrNomi));
		disScoperturaTotNomi = disScoperturaTotNomi
				.subtract(new BigDecimal(numDisEsonNomi));
		//scopertura nominativa art18	
		art18ScoperturaTotNomi = (new BigDecimal(numArt18Nom))
				.subtract(numArt18ForzaNomiTot);
		art18ScoperturaTotNomi = art18ScoperturaTotNomi
				.subtract(new BigDecimal(numArt18ConvNomi));
		art18ScoperturaTotNomi = art18ScoperturaTotNomi
				.add(new BigDecimal(numArt18CompTerrNomi));
		//scopertura numerica disabile
		disScoperturaTotNume = (new BigDecimal(numDisabiliNum))
				.subtract(numDisForzaNumeTot);
		disScoperturaTotNume = disScoperturaTotNume
				.subtract(new BigDecimal(numDisConvNume));
		disScoperturaTotNume = disScoperturaTotNume.add(new BigDecimal(
				numDisCompTerrNume));
		disScoperturaTotNume = disScoperturaTotNume
				.subtract(new BigDecimal(numDisEsonNume));
		//scopertura numerica art18		
		art18ScoperturaTotNume = (new BigDecimal(numArt18Num))
				.subtract(numArt18ForzaNumeTot);
		art18ScoperturaTotNume = art18ScoperturaTotNume
				.subtract(new BigDecimal(numArt18ConvNume));
		art18ScoperturaTotNume = art18ScoperturaTotNume
				.add(new BigDecimal(numArt18CompTerrNume));
		//calcolo scopertura totale disabile
		disScoperturaTot = disScoperturaTotNomi
				.add(disScoperturaTotNume);
		
		if ("1".equals(conf_battistoni) || "2".equals(conf_battistoni)){
			//Scopertura Dis  -  battistoni
			disScoperturaTot = disScoperturaTot.subtract(new BigDecimal(numdisbattistoni));
		}
		//calcolo scopertura totale art18
		art18ScoperturaTot = art18ScoperturaTotNomi
				.add(art18ScoperturaTotNume);
		
		//Solo per Umbria
		if ("1".equals(conf_battistoni)){
			//Scopertura Art 18 -  battistoni
			art18ScoperturaTot = art18ScoperturaTot.subtract(new BigDecimal(numart18battistoni));
		}
		//
		cdnUtIns = prospetto.getAttribute("cdnUtIns");
		dtmIns = prospetto.getAttribute("dtmIns");
		cdnUtMod = prospetto.getAttribute("cdnUtMod");
		dtmMod = prospetto.getAttribute("dtmMod");

		operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	}

	SourceBean rowInfoDeDoc = (SourceBean) serviceResponse
			.getAttribute("M_GetInfoDeDoc_Prospetti.ROWS.ROW");
	if (rowInfoDeDoc != null) {
		docInOut = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,
				"striodoc");
		docRif = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,
				"strambitodoc");
		docTipo = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,
				"strtipodoc");
		docCodRif = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,
				"codambitodoc");
		docCodTipo = StringUtils.getAttributeStrNotNull(rowInfoDeDoc,
				"codtipodoc");
	}

	//LINGUETTE		
	l = new Linguette(user, cdnfunzione, _page, new BigDecimal(
			prgProspettoInf), codStatoAtto);
	l.setCodiceItem("PRGPROSPETTOINF");

	//info dell'unità aziendale	
	if (prgAzienda != null && prgUnita != null
			&& !prgAzienda.equals("") && !prgUnita.equals("")) {
		infCorrentiAzienda = new InfCorrentiAzienda(sessionContainer,
				prgAzienda, prgUnita);
		infCorrentiAzienda.setPaginaLista("CMProspListaPage");
	}

	PageAttribs attributi = new PageAttribs(user,
			"CMProspRiepilogoPage");

	boolean canModify = false;
	boolean canStampaProspetto = false;
	boolean readOnlyStr = false;
	String msgProspettiInCorsoAnno = "";
	String msgProspettiInCorsoAnno1 = "";
	String msgProspettiInCorsoAnno2 = "";

	canStampaProspetto = attributi.containsButton("STAMPA");
	
	boolean canStoricizzaUscita = attributi.containsButton("STORICIZZAUO");
	
	canModify = attributi.containsButton("AGGIORNA");
	if (("N").equalsIgnoreCase(codMonoStatoProspetto)
			|| ("S").equalsIgnoreCase(codMonoStatoProspetto)
			|| ("V").equalsIgnoreCase(codMonoStatoProspetto)
			|| ("U").equalsIgnoreCase(codMonoStatoProspetto)
			|| ("N").equalsIgnoreCase(flgCompetenza)) {
		canModify = false;
	}
	
	if (codMonoStatoProspetto.equalsIgnoreCase(ProspettiConstant.STATO_IN_CORSO_ANNO)) {
		if (!flgSedeProspetto.equalsIgnoreCase("S")) {
			msgProspettiInCorsoAnno = "ATTENZIONE: nell'anagrafica dell'azienda non è stata indicata la sede legale, pertanto i calcoli potrebbero non essere corretti";	
		}
		Integer nBaseCompArt3 = !numBaseComputoArt3Naz.equals("")?new Integer(numBaseComputoArt3Naz):new Integer("0");
		Integer nBaseCompArt18 = !numBaseComputoArt18Naz.equals("")?new Integer(numBaseComputoArt18Naz):new Integer("0");
		if (!checkProspetto2011 && nBaseCompArt3.intValue() < 15 && nBaseCompArt18.intValue() <= 50) {
			msgProspettiInCorsoAnno1 = "ATTENZIONE: dai dati inseriti l'azienda non risulta in obbligo";	
		}
		if (!numTDMeno9mesi.equals("")) {
			Integer esclusioneVal = new Integer(numTDMeno9mesi);
			if (esclusioneVal.intValue() > 0) {
				msgProspettiInCorsoAnno2 = "ATTENZIONE: sono state utilizzate esclusioni non più valide";
			}
		}
	}

	String fieldReadOnly = canModify ? "false" : "true";

	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

	// verifico se ci sono eventuali warning dal controllo prima della storicizzazione
	SourceBean checkWarnings = (SourceBean) serviceResponse
			.getAttribute("CMCONTROLLARIEPILOGOMODULE.ROWS");
	if (checkWarnings != null) {
		Vector rows = checkWarnings.getAttributeAsVector("ROW");

		// dovrebbe essercene solo uno
		if (rows != null) {
			SourceBean row = (SourceBean) rows.get(0);
			codiceRit = (String) row.getAttribute("CODICERIT");
			if (("0").equals(codiceRit)) {
				err_percEsonero = (String) row
						.getAttribute("err_percEsonero");
				err_percCompensazione = (String) row
						.getAttribute("err_percCompensazione");
				err_numCompInProv = (String) row
						.getAttribute("err_numCompInProv");
				err_numCompArt18 = (String) row
						.getAttribute("err_numCompArt18");
				err_numCompRiduz = (String) row
						.getAttribute("err_numCompRiduz");
				err_numCompensEcc = (String) row
						.getAttribute("err_numCompensEcc");
				err_dataConsegnaProspetto = (String) row
						.getAttribute("err_dataConsegnaProspetto");
				err_dataProspetto = (String) row
						.getAttribute("err_dataProspetto");
				err_dataRiferimento = (String) row
						.getAttribute("err_dataRiferimento");
				err_checkProspStoriciz = (String) row
						.getAttribute("err_checkProspStoriciz");
				checkScopertura = (String) row
						.getAttribute("CHECKSCOPERTURA");
				checkExistForza = (String) row
						.getAttribute("CHECKEXISTFORZA");
			}
		}
	}

	// verifico se ci sono eventuali warning dal controllo prima della storicizzazione de prospetto da sare
	SourceBean checkWarningsSare = (SourceBean) serviceResponse
			.getAttribute("CMSTORICIZZARIEPILOGOSAREMODULE.ROWS");
	if (checkWarningsSare != null) {
		Vector rowsSare = checkWarningsSare.getAttributeAsVector("ROW");

		// dovrebbe essercene solo uno
		if (rowsSare != null) {
			SourceBean rowSare = (SourceBean) rowsSare.get(0);
			codiceRitSare = (String) rowSare.getAttribute("CODICERIT");
			if (("0").equals(codiceRitSare)) {
				err_percEsonero = (String) rowSare
						.getAttribute("err_percEsonero");
				err_percCompensazione = (String) rowSare
						.getAttribute("err_percCompensazione");
				err_numCompInProv = (String) rowSare
						.getAttribute("err_numCompInProv");
				err_numCompArt18 = (String) rowSare
						.getAttribute("err_numCompArt18");
				err_numCompRiduz = (String) rowSare
						.getAttribute("err_numCompRiduz");
				err_numCompensEcc = (String) rowSare
						.getAttribute("err_numCompensEcc");
				err_dataConsegnaProspetto = (String) rowSare
						.getAttribute("err_dataConsegnaProspetto");
				err_dataProspetto = (String) rowSare
						.getAttribute("err_dataProspetto");
				err_dataRiferimento = (String) rowSare
						.getAttribute("err_dataRiferimento");
				err_checkProspStoriciz = (String) rowSare
						.getAttribute("err_checkProspStoriciz");
			}
		}
	}

	/*
	SourceBean errori = (SourceBean) serviceResponse.getAttribute("ERRORS");
	
	if (errori != null) {
		rowsErrori = errori.getAttributeAsVector("USER_ERROR");			
	}
	 */

	EMFErrorHandler errorHandler = respCont.getErrorHandler();
	Collection errList = errorHandler.getErrors();
	Iterator iter = errList.iterator();
%>

<html>
<head>
<title>Riepilogo Prospetto</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<af:linkScript path="../../js/" />
<script language="javascript">	
	
	var flagChanged = false;  

	function fieldChanged() {
    	<%if (canModify) {
				out.print("flagChanged = true;");
			}%>
	}	
		
	function ricalcolo() {

		if (isInSubmit()) return;
		if (confirm("Attenzione: i dati modificati non verranno salvati, \nsei sicuro di voler ricalcolare il riepilogo?")) {
			if (controllaDate()) {
				var url = "AdapterHTTP?PAGE=CMProspRiepilogoPage";
				url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
				url += "&CDNFUNZIONE=<%=cdnfunzione%>";
				url += "&codStatoAtto=<%=codStatoAtto%>";
	
				setWindowLocation(url);  
			}
		}
	}
	
	function annullaModifiche() {
                      
        if (isInSubmit()) return;
                      
		var url = "AdapterHTTP?PAGE=CMProspRiepilogoPage";
		url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
		url += "&CDNFUNZIONE=<%=cdnfunzione%>";
		url += "&codStatoAtto=<%=codStatoAtto%>";

		setWindowLocation(url);  		
	}
		
	function storicizzaProsp(statoProspetto) {
		document.Frm1.protocolla.value = 'N';
		document.Frm1.codStatoAtto.value = '<%=codStatoAtto%>';
		if (controllaDate()) {
		 	var a = document.Frm1.numDisForzaRifNomi.value;
		 	var b = document.Frm1.numDisForzaRifNume.value;
		 	var c = document.Frm1.numArt18ForzaRifNomi.value;
			var d = document.Frm1.numArt18ForzaRifNume.value;
						
			
			if(document.Frm1.datRifInForza.value == "") {				
				if ((a == "" || a == 0) && (b == "" || b == 0) && (c == "" || c == 0) && (d == "" || d == 0)) {
					   	
				}			
				else {
					alert("Attenzione: se sono valorizzati i valori in forza manuali, è necessario specificare la relativa data.");
		      		return false;	
				}	
	    	}  
	    	else if(document.Frm1.datConsegnaProspetto.value == "" ) {
	      		alert("Attenzione: la data di consegna prospetto è obbligatoria per storicizzare un prospetto.");
	      		return false;
	    	}  
	    	else if(document.Frm1.datProspetto.value == "" ) {
	      		alert("Attenzione: la data prospetto 'situazione al' è obbligatoria per storicizzare un prospetto.");
	      		return false;
	    	}      	
	    	
			if (confirm("Attenzione: sei sicuro di voler storicizzare il prospetto?")) {
				if (confirm("Si vuole protocollare il prospetto?")) {
					document.Frm1.protocolla.value = 'S';
				}
			controllaProsp('false', statoProspetto);	  
			}
		}		  		
	}
	
	function controllaProsp(verifica, statoProspetto) {
		var url = "AdapterHTTP?PAGE=CMProspRiepilogoPage";
		url += "&MODULE=CMControllaRiepilogoModule";
		url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";		
		url += "&DATCONSEGNAPROSPETTO="+document.Frm1.datConsegnaProspetto.value;
		url += "&DATPROSPETTO="+document.Frm1.datProspetto.value;
		url += "&DATRIFINFORZA="+document.Frm1.datRifInForza.value;
		url += "&NUMART18FORZARIFNUME="+document.Frm1.numArt18ForzaRifNume.value;
		url += "&NUMART18FORZARIFNOMI="+document.Frm1.numArt18ForzaRifNomi.value;
		url += "&NUMDISFORZARIFNUME="+document.Frm1.numDisForzaRifNume.value;
		url += "&NUMDISFORZARIFNOMI="+document.Frm1.numDisForzaRifNomi.value;
		url += "&numKloProspettoInf=<%=numKloProspettoInf%>";		
		url += "&CDNFUNZIONE=<%=cdnfunzione%>";
		url += "&codStatoAtto="+document.Frm1.codStatoAtto.value;
		url += "&protocolla="+document.Frm1.protocolla.value;
		url += "&STATOPS="+statoProspetto;
		setWindowLocation(url);  
	}
	
	
	function controllaStoricizzazione() {
	
		var checkWarning = "-1";
		
		<%if (("1").equals(err_percEsonero)) {%>
			checkWarning = "2";
		<%} else if (("1").equals(err_percCompensazione)) {%>
			checkWarning = "2";
		<%} else if (("1").equals(err_numCompInProv)) {%>
			checkWarning = "2";
		<%} else if (("1").equals(err_numCompArt18)) {%>
			checkWarning = "2";
		<%} else if (("1").equals(err_numCompRiduz)) {%>
			checkWarning = "2";
		<%} else if (("1").equals(err_numCompensEcc)) {%>
			checkWarning = "2";
		<%} else if (("1").equals(err_dataConsegnaProspetto)) {%>
			checkWarning = "2";
		<%} else if (("1").equals(err_dataProspetto)) {%>
			checkWarning = "2";
		<%} else if (("1").equals(err_dataRiferimento)) {%>
			checkWarning = "2";
		<%} else if (("1").equals(err_checkProspStoriciz)) {%>
			checkWarning = "2";
		<%} else if (("1").equals(checkExistForza)) {%>
			checkWarning = "2";
		<%} else if (("1").equals(checkScopertura)) {%>
			checkWarning = "1";
		<%} else if (("1").equals(checkSospensione)) {%>
			checkWarning = "1";
		<%} else if (("0").equals(checkExistForza)
					&& ("0").equals(checkScopertura)) {%>
			checkWarning = "0";
		<%}%> 
		storicizzazione(checkWarning);
	}
	
	function storicizzazione(checkWarning) {
		if (isInSubmit()) return;
		var url = "AdapterHTTP?PAGE=CMProspRiepilogoPage";
		url += "&MODULE=CMStoricizzaRiepilogoModule";
		url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
		url += "&numKloProspettoInf=<%=numKloProspettoInf%>";	
		url += "&CDNFUNZIONE=<%=cdnfunzione%>";
		url += "&codStatoAtto=<%=codStatoAtto%>";
		url += "&protocolla="+document.Frm1.protocolla.value;
		url += "&numAnnoRifProspetto=<%=numAnnoRifProspetto%>";
		url += "&prgAzienda=<%=prgAzienda%>";
		url += "&prgUnita=<%=prgUnita%>";
		url += "&STATOPS=<%=statoPS%>";
		
		if (checkWarning != null  && checkWarning != "") {		
			if (checkWarning == "0") {
				
				setWindowLocation(url); 
			}  
			else if (checkWarning == "1") {
				if (confirm("Attenzione: sono presenti dei warning, \nsei sicuro di voler storicizzare il prospetto?")) {			
					
					setWindowLocation(url);  
				}	
			}	
			else if (checkWarning == "2") {
				alert("Attenzione: sono presenti alcuni errori, non è possibile storicizzare il prospetto.");
				
			}	
		}			
	}


	function annullaProspetto() {
		
		var url = "AdapterHTTP?PAGE=CMProspRiepilogoPage";
		url += "&MODULE=CMAnnullaRiepilogoModule";
		url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";				
		url += "&numKloProspettoInf=<%=numKloProspettoInf%>";		
		url += "&CDNFUNZIONE=<%=cdnfunzione%>";  
		url += "&codStatoAtto=<%=codStatoAtto%>";

		setWindowLocation(url);  
	}

	function copiaProspetto() {

		if (isInSubmit()) return;
		if (confirm("Attenzione: sei sicuro di voler generare una copia del prospetto?")) {
			var url = "AdapterHTTP?PAGE=CMProspGeneraCopiaPage";		
	    	url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
	    	url += "&CDNFUNZIONE=<%=cdnfunzione%>";
	    	url += "&codStatoAtto=<%=codStatoAtto%>";
	    	window.open(url, "", 'toolbar=NO,statusbar=YES,height=200,width=400,scrollbars=YES,resizable=YES');	
		}		
	}
	
	function stampaProspetto() {
		var prg = <%=prgProspettoInf%>;
	    apriGestioneDoc('RPT_PROSPETTO','&PRGPROSPETTOINF='+prg,'PROSP');
	}
	
	function controllaDate() {
		
		var dataRiferimento = new String(document.Frm1.datRifInForza.value);
		var dataConsegna = new String(document.Frm1.datConsegnaProspetto.value);		
		var dataProspetto = new String(document.Frm1.datProspetto.value);	
      	var annoProspetto = '<%=numAnnoRifProspetto%>'

      	var annoDataConsegna = dataConsegna.substr(6,4);
      	var annoDataRiferimento = dataRiferimento.substr(6,4);
      	var annoDataProspetto = dataProspetto.substr(6,4);
      	
      	var numDataProspetto = dataProspetto.substr(6,4) + dataProspetto.substr(3,2) + dataProspetto.substr(0,2);
      	var numDataRiferimento = dataRiferimento.substr(6,4) + dataRiferimento.substr(3,2) + dataRiferimento.substr(0,2);
      	     	
      	
      	if (dataRiferimento != null  && dataRiferimento != "") {	
      		if (annoDataRiferimento > annoProspetto) {
      			alert("Attenzione: l'anno della data 'Inserimento in forza manuale fino al' deve essere minore o uguale dell'anno di riferimento del prospetto.");
      			return false;
      		}      		
      	}
      	if (dataConsegna != null  && dataConsegna != "") {	
      		if (annoDataConsegna < annoProspetto) {
      			alert("Attenzione: l'anno della data consegna deve essere maggiore o uguale dell'anno di riferimento del prospetto.");
      			return false;
      		}
      	}
      	if (dataProspetto != null  && dataProspetto != "") {	
      		if (annoDataProspetto != annoProspetto) {
      			alert("Attenzione: l'anno della data prospetto 'Situazione al' deve essere uguale dell'anno di riferimento del prospetto.");
      			return false;
      		}      		 
      	}
      	if (dataProspetto != null  && dataProspetto != "" && dataRiferimento != null  && dataRiferimento != "") {	
      		if (numDataProspetto < numDataRiferimento) {
      			alert("Attenzione: la data prospetto 'Situazione al' deve essere maggiore o uguale alla data di riferimento.");
      			return false;
      		}
      	}
      	 
      	return true;
				
	}
	
	
	function storicizzaProspSARE() {
		if (isInSubmit()) return;
		if (confirm("Attenzione: sei sicuro di voler storicizzare il prospetto?")) {
			
			var url = "AdapterHTTP?PAGE=CMProspRiepilogoPage";
			url += "&MODULE=CMStoricizzaRiepilogoSareModule";
			url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
			url += "&numKloProspettoInf=<%=numKloProspettoInf%>";	
			url += "&CDNFUNZIONE=<%=cdnfunzione%>";
			url += "&codStatoAtto=<%=codStatoAtto%>";
			
			setWindowLocation(url); 
		}
		
	}
	
	function controllaCopia() {
		
		var checkWarning = "-1";
		<%String codProvinciaBis = "";
			String numAnnoRifProspBis = "";
			Exception ex = null;
			EMFUserError userError = null;
			int codError = 0;
			while (iter.hasNext()) {
				ex = (Exception) iter.next();

				if (ex instanceof EMFUserError) {
					userError = (EMFUserError) ex;
					codError = userError.getCode();

					if (MessageCodes.CollocamentoMirato.ERROR_COPIA_PROSPETTO_DUPLICATO == codError) {
						codProvinciaBis = serviceRequest
								.getAttribute("CODPROVINCIA") == null ? ""
								: (String) serviceRequest
										.getAttribute("CODPROVINCIA");
						numAnnoRifProspBis = serviceRequest
								.getAttribute("NUMANNORIFPROSPETTO") == null ? ""
								: (String) serviceRequest
										.getAttribute("NUMANNORIFPROSPETTO");%>
					checkWarning = "1";   		 
				<%break;
					}
				}
			}%>
		
		// CHECKANNULLA=1 indica che viene annullato il prospetto in corso d'anno e ricreato partendo da questo visualizzato								
		if (checkWarning == "1") {		
			var url = "AdapterHTTP?PAGE=CMProspRiepilogoPage";		
			url += "&MODULE=CMGeneraCopiaRiepilogoModule";
		    url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
		    url += "&CODPROVINCIA=<%=codProvinciaBis%>"
		    url += "&NUMANNORIFPROSPETTO=<%=numAnnoRifProspBis%>";
		    url += "&CDNFUNZIONE=<%=cdnfunzione%>";
		    url += "&CHECKANNULLA=1";
		    url += "&codStatoAtto=NP";
		    url += "&nuovo=true";
    		
    		if (confirm("Attenzione esiste già a sistema un copia in corso d'anno.\r\nSi vuole procedere con la creazione di una nuova copia in corso d'anno\r\ne l'annullamento di quello in corso d'anno attualmente presente nel sistema?")) {				
				setWindowLocation(url);  
			}	
		}			
	}	
	
</script>
<SCRIPT TYPE="text/javascript">
 
  window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
  
</SCRIPT>
</head>

<body class="gestione"
	onload="rinfresca();controllaStoricizzazione();controllaCopia();">
<%
	if (infCorrentiAzienda != null) {
%>
<div id="infoCorrAz" style="display: ">
<%
	infCorrentiAzienda.show(out);
%>
</div>
<%
	}

	l.show(out);
%>

<p class="titolo">Riepilogo del Prospetto</p>
<%if (!msgProspettiInCorsoAnno.equals("")) {%>
<p class="titolo"><font color="red"><%=msgProspettiInCorsoAnno%></font></p>
<%}
if (!msgProspettiInCorsoAnno1.equals("")) {%>
<p class="titolo"><font color="red"><%=msgProspettiInCorsoAnno1%></font></p>	
<%}
if (!msgProspettiInCorsoAnno2.equals("")) {%>
<p class="titolo"><font color="red"><%=msgProspettiInCorsoAnno2%></font></p>	
<%}
if (!messaggiVerifica.equals("")) {%>
<p class="titolo"><font color="red"><%=messaggiVerifica%></font></p>	
<%}%>

<center><font color="green"> <af:showMessages
	prefix="CMDettRiepilogoSaveModule" /> <af:showMessages
	prefix="CMControllaRiepilogoModule" /> <af:showMessages
	prefix="CMStoricizzaRiepilogoModule" /> <af:showMessages
	prefix="CMAnnullaRiepilogoModule" /> <af:showMessages
	prefix="CMGeneraCopiaRiepilogoModule" /> </font></center>
<center><font color="red"><af:showErrors /></font></center>
<%@ include file="verificaProspetto.inc"%>

<af:form name="Frm1" method="POST" action="AdapterHTTP"
	onSubmit="controllaDate()">
	<input type="hidden" name="PAGE" value="CMProspRiepilogoPage" />
	<input type="hidden" name="MODULE" value="CMDettRiepilogoSaveModule" />
	<input type="hidden" name="MESSAGE" value="<%=message%>" />
	<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
	<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
	<%if (checkProspetto2011) {%>
		<input type="hidden" name="CHECK2011" value="true" />
	<%}%>
	<input type="hidden" name="prgProspettoInf"
		value="<%=prgProspettoInf%>" />
	<input type="hidden" name="cdnfunzione" value="<%=cdnfunzione%>" />
	<input type="hidden" name="numKloProspettoInf"
		value="<%=numKloProspettoInf%>" />
	<input type="hidden" name="protocolla" value="<%=protocolla%>" />

	<%out.print(htmlStreamTop);%>
	
	<table class="main" border="0">
		<%@ include file="_protocollazioneProspetti.inc"%>
		<tr>
			<td class="etichetta2">Categoria</td>
			<td class="campo2"><af:comboBox name="codMonoCategoria"
				classNameBase="input" disabled="true">
				<option value="" <%if ("".equalsIgnoreCase(codMonoCategoria)) {%>
					SELECTED="true" <%}%>></option>
				<option value="A" <%if ("A".equalsIgnoreCase(codMonoCategoria)) {%>
					SELECTED="true" <%}%>>più di 50 dipendenti</option>
				<option value="B" <%if ("B".equalsIgnoreCase(codMonoCategoria)) {%>
					SELECTED="true" <%}%>>da 36 a 50 dipendenti</option>
				<option value="C" <%if ("C".equalsIgnoreCase(codMonoCategoria)) {%>
					SELECTED="true" <%}%>>da 15 a 35 dipendenti</option>
			</af:comboBox></td>
			<td class="etichetta2">Num. dipendenti</td>
			<td class="campo2"><af:textBox type="integer"
				title="Num lav. Prov." className="viewRiepilogo"
				name="numDipendentiTot"
				value="<%= String.valueOf(numDipendentiTot)%>" size="4"
				maxlength="4" readonly="true" /></td>
		</tr>
		<tr>
			<td class="etichetta2">Situazione al</td>
			<td class="campo2"><af:textBox classNameBase="input"
				title="Data consegna prospetto" type="date" validateOnPost="true"
				onKeyUp="fieldChanged();" name="datProspetto" size="11"
				maxlength="10" value="<%= String.valueOf(datProspetto)%>"
				readonly="<%=fieldReadOnly%>" /></td>
				
				
			<%if (checkProspetto2011) { %>
				<td class="etichetta2">Totale esclusioni</td>
				<td class="campo2"><af:textBox type="integer"
					title="Totale esclusioni" className="viewRiepilogo"
					name="totaleEsclusione"
					value="<%= String.valueOf(totaleEsclusione)%>" size="4"
					maxlength="4" readonly="true" /></td>
			<%} else {%>
				<td class="etichetta2">Esclusioni Art.3&nbsp;
				<af:textBox type="integer"
					title="Esclusioni Art.3" className="viewRiepilogo"
					name="totaleEsclusione"
					value="<%= String.valueOf(totaleEsclusione)%>" size="4"
					maxlength="4" readonly="true" /></td>
				<td class="etichetta2">Esclusioni Art.18&nbsp;
				<af:textBox type="integer"
					title="Esclusioni Art.18" className="viewRiepilogo"
					name="totaleEsclusioneArt18"
					value="<%= String.valueOf(totaleEsclusioneArt18)%>" size="4"
					maxlength="4" readonly="true" /></td>
			<%}%>
		</tr>
		<tr>
			<td class="etichetta2">Data Consegna</td>
			<td class="campo2"><af:textBox classNameBase="input"
				title="Data consegna prospetto" type="date" validateOnPost="true"
				onKeyUp="fieldChanged();" name="datConsegnaProspetto" size="11"
				maxlength="10" value="<%= String.valueOf(datConsegnaProspetto)%>"
				readonly="<%=fieldReadOnly%>" /></td>
			
			<%if (checkProspetto2011) { %>
				<td class="etichetta2">Base di computo</td>
				<td class="campo2"><af:textBox type="Number"
					title="Base di computo" className="viewRiepilogo"
					name="numBaseComputo" value="<%= String.valueOf(numBaseComputo)%>"
					size="4" maxlength="4" validateOnPost="true" readonly="true" /></td>
			<%} else {%>
				<td class="etichetta2">Base di computo Art.3&nbsp;
				<af:textBox type="Number"
					title="Base di computo Art.3" className="viewRiepilogo"
					name="numBaseComputo" value="<%= String.valueOf(numBaseComputoArt3)%>"
					size="4" maxlength="4" validateOnPost="true" readonly="true" /></td>
				<td class="etichetta2">Base di computo Art.18&nbsp;
				<af:textBox type="Number"
					title="Base di computo Art.18" className="viewRiepilogo"
					name="numBaseComputoArt18" value="<%= String.valueOf(numBaseComputoArt18)%>"
					size="4" maxlength="4" validateOnPost="true" readonly="true" /></td>
			<%}%>
		</tr>
		<tr>
			<td class="etichetta2">Inserimento in forza manuale fino al</td>
			<td class="campo2"><af:textBox classNameBase="input"
				title="Inserimento in forza manuale fino al" type="date"
				validateOnPost="true" onKeyUp="fieldChanged();" name="datRifInForza"
				size="11" maxlength="10" value="<%= String.valueOf(datRifInForza)%>"
				readonly="<%=fieldReadOnly%>" /></td>
			<td class="etichetta2">&nbsp;</td>
			<td class="campo2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="4">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="4">
			<table>
				<tr>
					<td>&nbsp;</td>
					<td class="campo_readFree">Disabili Art.3</td>
					<td class="campo_readFree">Categorie Protette Art.18</td>
				</tr>
				<tr>
					<td>
					<table>
						<tr>
							<td style="padding: 3px;"><af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="riga1"
								value="&nbsp;" readonly="true" /></td>
						</tr>
						<tr>
							<td style="padding: 3px;"><af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="riga2"
								value="Quota" readonly="true" /></td>
						</tr>
						<tr>
							<td style="padding: 3px;"><af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="riga3"
								value="In forza" readonly="true" /></td>
						</tr>
						<tr>
							<td style="padding: 3px;"><af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="riga4"
								value="In Convenzione" readonly="true" /></td>
						</tr>
						<tr>
							<td style="padding: 3px;"><af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="riga5"
								value="Comp. Terr." readonly="true" /></td>
						</tr>
						<tr>
							<td style="padding: 3px;"><af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="riga6"
								value="Esonero" readonly="true" /></td>
						</tr>
						<% if ("1".equals(conf_battistoni)||"2".equals(conf_battistoni)){%>
						<tr>
							<td style="padding: 3px;"><af:textBox type="text" size="25"
								className="viewRiepilogoTrasparent" title="" name="riga6"
								value="Lav. Battistoni(esuberi)" readonly="true" /></td>
						</tr>
						<%}%>						
						<tr>
							<td style="padding: 3px;"><af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="riga7"
								value="Scopertura" readonly="true" /></td>
						</tr>
					</table>
					</td>
					<td>
					<table class="appuntamenti">
						<tr>
							<td class="campocentrato">&nbsp;</td>
							<td class="campocentrato">Nominativo</td>
							<td class="campocentrato">Numerico</td>
						</tr>
						<tr>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Quota totale"
								name="numQuotaDisabili"
								value="<%= String.valueOf(numQuotaDisabili)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Quota nominativa"
								name="numDisabiliNom"
								value="<%= String.valueOf(numDisabiliNom)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Quota numerica"
								name="numDisabiliNum"
								value="<%= String.valueOf(numDisabiliNum)%>" size="3"
								maxlength="4" readonly="true" /></td>
						</tr>
						<tr>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="In forza" name="numDisForza"
								value="<%= String.valueOf(numDisForza)%>" size="3" maxlength="4"
								readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="In forza nominativa alla data"
								readonly="true" name="numDisForzaNomi"
								value="<%= String.valueOf(numDisForzaNomi)%>" size="3"
								maxlength="4" validateOnPost="true" /> + <af:textBox
								type="Number" title="In forza nominativa"
								readonly="<%=fieldReadOnly%>" name="numDisForzaRifNomi"
								value="<%= String.valueOf(numDisForzaRifNomi)%>" size="3"
								maxlength="4" validateOnPost="true" onKeyUp="fieldChanged();" />
							&nbsp; <af:textBox type="Number" className="viewRiepilogo"
								title="In forza nominativa" name="numDisForzaNomiTot"
								value="<%= String.valueOf(numDisForzaNomiTot)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="In forza numerica alla data"
								readonly="true" name="numDisForzaNume"
								value="<%= String.valueOf(numDisForzaNume)%>" size="3"
								maxlength="4" validateOnPost="true" /> + <af:textBox
								type="Number" title="In forza numerica"
								readonly="<%=fieldReadOnly%>" name="numDisForzaRifNume"
								value="<%= String.valueOf(numDisForzaRifNume)%>" size="3"
								maxlength="4" validateOnPost="true" onKeyUp="fieldChanged();" />
							&nbsp; <af:textBox type="Number" className="viewRiepilogo"
								title="In forza numerica" name="numDisForzaNumeTot"
								value="<%= String.valueOf(numDisForzaNumeTot)%>" size="3"
								maxlength="4" readonly="true" /></td>
						</tr>
						<tr>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="In convenzione"
								name="numDisConvTot" value="<%= String.valueOf(numDisConvTot)%>"
								size="3" maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="In convenzione nominativa"
								name="numDisConvNomi"
								value="<%= String.valueOf(numDisConvNomi)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="In convenzione numerica"
								name="numDisConvNume"
								value="<%= String.valueOf(numDisConvNume)%>" size="3"
								maxlength="4" readonly="true" /></td>
						</tr>
						<tr>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Competenze territoriali"
								name="numDisCompTerrTot"
								value="<%= String.valueOf(numDisCompTerrTot)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo"
								title="Competenze territoriali nominativa"
								name="numDisCompTerrNomi"
								value="<%= String.valueOf(numDisCompTerrNomi)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo"
								title="Competenze territoriali numerica"
								name="numDisCompTerrNume"
								value="<%= String.valueOf(numDisCompTerrNume)%>" size="3"
								maxlength="4" readonly="true" /></td>
						</tr>
						<tr>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Esonero" name="numDisEsonTot"
								value="<%= String.valueOf(numDisEsonTot)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Esonero" name="numDisEsonNomi"
								value="<%= String.valueOf(numDisEsonNomi)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Esonero" name="numDisEsonNume"
								value="<%= String.valueOf(numDisEsonNume)%>" size="3"
								maxlength="4" readonly="true" /></td>
						</tr>
						<% if ("1".equals(conf_battistoni)||"2".equals(conf_battistoni)){%>
						<tr>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Lav. L. Battistoni"
								name="numdisbattistoni"
								value="<%= String.valueOf(numdisbattistoni)%>" size="3"
								maxlength="4" readonly="true" /></td>
						</tr>
						<%}%>

						<tr>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Scopertura"
								name="disScoperturaTot"
								value="<%= String.valueOf(disScoperturaTot)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Scopertura nominativa"
								name="disScoperturaTotNomi"
								value="<%= String.valueOf(disScoperturaTotNomi)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Scopertura numerica"
								name="disScoperturaTotNume"
								value="<%= String.valueOf(disScoperturaTotNume)%>" size="3"
								maxlength="4" readonly="true" /></td>
						</tr>
					</table>
					</td>
					<td>
					<table class="appuntamenti">
						<tr>
							<td class="campocentrato">&nbsp;</td>
							<td class="campocentrato">Nominativo</td>
							<td class="campocentrato">Numerico</td>
						</tr>
						<tr>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Quota totale"
								name="numQuotaArt18" value="<%= String.valueOf(numQuotaArt18)%>"
								size="3" maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Quota nominativa"
								name="numArt18Nom" value="<%= String.valueOf(numArt18Nom)%>"
								size="3" maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Quota numerica"
								name="numArt18Num" value="<%= String.valueOf(numArt18Num)%>"
								size="3" maxlength="4" readonly="true" /></td>
						</tr>
						<tr>
							<td align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="In forza" name="numArt18Forza"
								value="<%= String.valueOf(numArt18Forza)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="In forza nominativa alla data"
								readonly="true" name="numArt18ForzaNomi"
								value="<%= String.valueOf(numArt18ForzaNomi)%>" size="3"
								maxlength="4" validateOnPost="true" /> + <af:textBox
								type="Number" title="In forza nominativa"
								readonly="<%=fieldReadOnly%>" name="numArt18ForzaRifNomi"
								value="<%= String.valueOf(numArt18ForzaRifNomi)%>" size="3"
								maxlength="4" validateOnPost="true" onKeyUp="fieldChanged();" />
							&nbsp; <af:textBox type="Number" className="viewRiepilogo"
								title="In forza nominativa" name="numArt18ForzaNomiTot"
								value="<%= String.valueOf(numArt18ForzaNomiTot)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="In forza numerica alla data"
								readonly="true" name="numArt18ForzaNume"
								value="<%= String.valueOf(numArt18ForzaNume)%>" size="3"
								maxlength="4" validateOnPost="true" /> + <af:textBox
								type="Number" title="In forza numerica"
								readonly="<%=fieldReadOnly%>" name="numArt18ForzaRifNume"
								value="<%= String.valueOf(numArt18ForzaRifNume)%>" size="3"
								maxlength="4" validateOnPost="true" onKeyUp="fieldChanged();" />
							&nbsp; <af:textBox type="Number" className="viewRiepilogo"
								title="In forza numerica" name="numArt18ForzaNumeTot"
								value="<%= String.valueOf(numArt18ForzaNumeTot)%>" size="3"
								maxlength="4" readonly="true" /></td>
						</tr>
						<tr>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="In convenzione"
								name="numArt18ConvTot"
								value="<%= String.valueOf(numArt18ConvTot)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="In convenzione nominativa"
								name="numArt18ConvNomi"
								value="<%= String.valueOf(numArt18ConvNomi)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="In convenzione numerica"
								name="numArt18ConvNume"
								value="<%= String.valueOf(numArt18ConvNume)%>" size="3"
								maxlength="4" readonly="true" /></td>
						</tr>
						<tr>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Competenze territoriali"
								name="numArt18CompTerrTot"
								value="<%= String.valueOf(numArt18CompTerrTot)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo"
								title="Competenze territoriali nominativa"
								name="numArt18CompTerrNomi"
								value="<%= String.valueOf(numArt18CompTerrNomi)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo"
								title="Competenze territoriali numerica"
								name="numArt18CompTerrNume"
								value="<%= String.valueOf(numArt18CompTerrNume)%>" size="3"
								maxlength="4" readonly="true" /></td>
						</tr>
						<% if ("1".equals(conf_battistoni)||"2".equals(conf_battistoni)){%>
						<tr>
							<td nowrap="nowrap" align="right" style="padding: 3px;"><af:textBox
								type="Number" className="viewRiepilogoTrasparent" title=""
								name="vuoto" value="" size="3" maxlength="4" readonly="true" />
							</td>
							<td nowrap="nowrap" align="right">&nbsp;</td>
							<td nowrap="nowrap" align="right">&nbsp;</td>
						</tr>
						<%}%>
						<% if ("1".equals(conf_battistoni)){%>
						<tr>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Legge Art. 18 Battistoni"
								name="numart18battistoni"
								value="<%= String.valueOf(numart18battistoni)%>" size="3"
								maxlength="4" readonly="true" /></td>

						</tr>
						<%}else{%>
						<tr>
							<td>&nbsp;</td>
						</tr>	
									
						<%}%>
						<tr>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Scopertura"
								name="art18ScoperturaTot"
								value="<%= String.valueOf(art18ScoperturaTot)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Scopertura nominativa"
								name="art18ScoperturaTotNomi"
								value="<%= String.valueOf(art18ScoperturaTotNomi)%>" size="3"
								maxlength="4" readonly="true" /></td>
							<td nowrap="nowrap" align="right"><af:textBox type="Number"
								className="viewRiepilogo" title="Scopertura numerica"
								name="art18ScoperturaTotNume"
								value="<%= String.valueOf(art18ScoperturaTotNume)%>" size="3"
								maxlength="4" readonly="true" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td class="campo_readFree">Gradualità</td>
					<td>&nbsp;</td>
				</tr>
				<tr valign="top">
					<td>
					<table>
					<% if(configEtichette.equals("0")){ %>
						<tr>
							<td style="padding: 5px;"><af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="rigaGrad1"
								value="Quota grad." readonly="true" /></td>
						</tr>
						<tr>
							<td style="padding: 5px;"><af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="rigaGrad2"
								value="In Forza grad." readonly="true" /></td>
						</tr>
						<tr>
							<td style="padding: 5px;"><af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="rigaGrad3"
								value="Num. ass. grad." readonly="true" /></td>
						</tr>
						
					<% }else if(configEtichette.equals("1")){ %>
						<tr>
							<td style="padding: 0px;"><af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="rigaGrad1" 
								value="Quota lavoratori da" readonly="true" />
								<!--af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="rigaGrad12"
								value="assumere in grad." readonly="true" /-->
								</td>
						</tr>
						<tr>
							<td style="padding: 0px;">
								<af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="rigaGrad12"
								value="assumere in grad." readonly="true" /></td>
						</tr>
						<tr>
							<td style="padding: 0px; padding-top: 5px;"><af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="rigaGrad2"
								value="Lavoratori in forza" readonly="true" />
								</td>
						</tr>
						<tr>
							<td style="padding: 0px;">
								<af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="rigaGrad22"
								value="dopo passaggio" readonly="true" /></td>
						</tr>
						<tr>
							<td style="padding: 0px; padding-top: 5px;"><af:textBox type="text"  
								className="viewRiepilogoTrasparent" title="" name="rigaGrad3" 
								value="Num. assunz. ancora" readonly="true" />
								</td>
						</tr>
						<tr>
							<td style="padding: 0px;">
								<af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="rigaGrad32"
								value="da effettuare in " readonly="true" />
								</td>
						</tr>
						<tr>
							<td style="padding: 0px;">
								<af:textBox type="text"
								className="viewRiepilogoTrasparent" title="" name="rigaGrad32"
								value="seguito al passaggio" readonly="true" /></td>
						</tr>
					<% } %>

					</table>
					</td>
					<td>
						<table class="appuntamenti">
						<tr>
						<% if(configEtichette.equals("0")){ %>
							<td style="padding: 3px;">
						<% }else if(configEtichette.equals("1")){ %>
							<td style="padding: 8px; padding-top: 7px;">
						<% } %>
							<af:textBox type="Number"
								title="Quota Gradualità" readonly="<%=fieldReadOnly%>"
								name="numQuotaDisGrad"
								value="<%= String.valueOf(numQuotaDisGrad)%>" size="3"
								maxlength="4" validateOnPost="true" onKeyUp="fieldChanged();" />
							</td>
						</tr>
						<tr>
						<% if(configEtichette.equals("0")){ %>
							<td style="padding: 3px;">
						<% }else if(configEtichette.equals("1")){ %>
							<td style="padding: 8px; padding-top: 10px;">
						<% } %>
							<af:textBox type="Number"
								title="In forza gradualità" readonly="<%=fieldReadOnly%>"
								name="numLavInForzaGrad"
								value="<%= String.valueOf(numLavInForzaGrad)%>" size="3"
								maxlength="4" validateOnPost="true" onKeyUp="fieldChanged();" />
							</td>
						</tr>
						<tr>
						<% if(configEtichette.equals("0")){ %>
							<td style="padding: 3px;">
						<% }else if(configEtichette.equals("1")){ %>
							<td style="padding: 8px; padding-top: 15px;">
						<% } %>
							<af:textBox type="Number"
								title="Num assunti in gradualità" readonly="<%=fieldReadOnly%>"
								name="numAssGradualita"
								value="<%= String.valueOf(numAssGradualita)%>" size="3"
								maxlength="4" validateOnPost="true" onKeyUp="fieldChanged();" />
							</td>
						</tr>
						
					
						
						
					</table>
						
								
					</td>
					<td>
					<table>
						<tr>
							<td class="etichetta2">Num. convenz. attive</td>
							<td class="campo2"><af:textBox
								title="Numero convenzioni attive" className="viewRiepilogo"
								name="numConvenzioni" value="<%=numConvenzioni%>" size="3"
								maxlength="4" validateOnPost="true" readonly="true" /></td>
						</tr>
						<tr>
							<td class="etichetta2">Num. lav. in convenzione</td>
							<td class="campo2"><af:textBox
								title="Numero lavoratori in convenzione"
								className="viewRiepilogo" name="numLavoratoriConv"
								value="<%=numLavoratoriConv%>" size="3" maxlength="4"
								validateOnPost="true" readonly="true" /></td>
						</tr>
						
						<tr>
							<td class="etichetta2">Art. 18 Num. Tot. Conv. Previsti</td>
							<td class="campo2"><af:textBox
								title="Art. 18 Num. Tot. Conv. Previsti"
								className="viewRiepilogo" name="numtotlavconvart18"
								value="<%=numtotlavconvart18%>" size="3" maxlength="4"
								validateOnPost="true" readonly="true" /></td>
						</tr>
							<tr>
							<td class="etichetta2">Dis. Num. Tot. Conv. Previsti</td>
							<td class="campo2"><af:textBox
								title="Dis. Num. Tot. Conv. Previsti"
								className="viewRiepilogo" name="numtotlavconvdis"
								value="<%=numtotlavconvdis%>" size="3" maxlength="4"
								validateOnPost="true" readonly="true" /></td>
						</tr>
						<tr>
							<td class="etichetta2">Data prossima ass. in conv.</td>
							<td class="campo2"><af:textBox
								title="Data prossima assunzione in convenzione"
								className="viewRiepilogo" name="dataProxAssConv"
								value="<%= dataProxAssConv%>" size="11" maxlength="10"
								validateOnPost="true" readonly="true" /></td>
						</tr>
						<tr>
							<td class="etichetta2">Data Passaggio</td>
							<td class="campo2"><af:textBox title="Data Passaggio"
								className="viewRiepilogo" name="datConcGradualita"
								value="<%= datConcGradualita%>" size="11" maxlength="10"
								validateOnPost="true" readonly="true" /></td>
						</tr>
						<tr>
							<td class="etichetta2">Perc. esonero</td>
							<td class="campo2"><af:textBox title="Per. esonero"
								className="viewRiepilogo" name="numPercEsonero"
								value="<%= numPercEsonero%>" size="3" maxlength="4"
								readonly="true" />%</td>
						</tr>
						
					</table>

					</td>
				</tr>
				<% if ("1".equals(conf_battistoni)||"2".equals(conf_battistoni)){%>
					<tr>
						<td>&nbsp;</td>
						<td class="campo_readFree">Categoria Protetta Art.18</td>
						<td>&nbsp;</td>
					</tr>
					
					<tr valign="top">
					
						<%if (checkProspetto2011) {%>
							<td style="padding: 0px;"><af:textBox type="text" size="35"
								className="viewRiepilogoTrasparent" title="" name="rigaGrad1"
								value="Totale in forza" readonly="true" />
							</td>
						<%} else {%>
							<td style="padding: 0px;"><af:textBox type="text" size="35"
								className="viewRiepilogoTrasparent" title="" name="rigaGrad1"
								value="Di cui in forza al 17/01/2000" readonly="true" />
							</td>
						<%}%>
						<td>
						<table class="appuntamenti">
							<tr>
								<%if (checkProspetto2011) {%>	
									<td class="campo2"><af:textBox className="viewRiepilogo"
										title="In forza Art. 18 L. Battistoni"
										readonly="true" name="numbattistoniinforza"
										value="<%= String.valueOf(numbattistoniinforza)%>" size="3"
										maxlength="4" validateOnPost="true" />
									</td>
								<%} else {%>
									<td class="campo2"><af:textBox type="Number"
										title="In forza Art. 18 L. Battistoni"
										readonly="<%=fieldReadOnly%>" name="numbattistoniinforza"
										value="<%= String.valueOf(numbattistoniinforza)%>" size="3"
										maxlength="4" validateOnPost="true" /> 
									</td>
								<%}%>
							</tr>
	
						</table>
						</td>
						
						<td>
						<table>
							<%// if ("1".equals(resultConfigNonVedenti_MassoFisioterapista)){%>																				
																															
							<tr align="right"> 
								<td class="etichetta2">Scopertura Central Non Vedenti</td>
								<td class="campo2"><af:textBox className="viewRiepilogo"
									readonly="true"	name="numscopcentnonvedenti"
									value="<%= String.valueOf(numscopcentnonvedenti)%>" size="3"
									maxlength="4" validateOnPost="true"  />
								</td>
							</tr>
							<tr>
								<td class="etichetta2">Scopertura Masso Fisioterapisti</td>
									<td class="campo2"><af:textBox className="viewRiepilogo"
									title="Scopertura masso fisioterapista" readonly="true"
									name="numscopmassofisioterapisti"
									value="<%= String.valueOf(numscopmassofisioterapisti)%>" size="3"
									maxlength="4" validateOnPost="true" />
								</td>
							</tr>
							<%//}%>						
						</table>
						</td>
					</tr>
					
					<%
					if (!checkProspetto2011) {
						if (!numbattistoniinforza.equals("")) {
							int numBatt1701200 = new Integer(numbattistoniinforza).intValue();
							if (numBatt1701200 != numLavArt18Batt) {%>
								<tr>
						 		<td class="etichetta2">warning:incongruenza con num. lav. art.18	
						 		</td>
						 		<td>&nbsp;</td>
						 		<td>&nbsp;</td>
								</tr>
							<%}
						}
						else {
							if (numLavArt18Batt > 0) {%>
								<tr>
						 		<td class="etichetta2">warning:incongruenza con num. lav. art.18	
						 		</td>
						 		<td>&nbsp;</td>
						 		<td>&nbsp;</td>
								</tr>
							<%}
						}
					}
				}%>
			</table>
			</td>
		</tr>
		
		<tr>
		<td colspan="4">
		<table align="center">
			<tr>
				<td class="etichetta2">Esonero parziale AUTORIZZ.</td>
				<td class="campo2"><af:textBox title="Esonero parziale AUTORIZZ."
					className="viewRiepilogo" name="esoneroParziale"
					value="<%=esoneroParziale%>" size="2" maxlength="4"
					validateOnPost="true" readonly="true" /></td>
				<td>&nbsp;</td>
				<td class="etichetta2">Esonero parz. AUTOCERT.</td>
				<td class="campo2"><af:textBox title="Esonero parz. AUTOCERT."
					className="viewRiepilogo" name="esoneroParzAutCert"
					value="<%=esoneroParzAutCert%>" size="2" maxlength="4"
					validateOnPost="true" readonly="true" /></td>
				<td>&nbsp;</td>
				<td class="etichetta2">Sospensione</td>
				<td class="campo2"><af:textBox title="Sospensione"
					className="viewRiepilogo" name="sospensione"
					value="<%=sospensione%>" size="2" maxlength="4"
					validateOnPost="true" readonly="true" /></td>
				<td>&nbsp;</td>
				<td class="etichetta2">Comp. Terr.</td>
				<td class="campo2"><af:textBox title="Competenze Territoriali"
					className="viewRiepilogo" name="compTerr" value="<%=compTerr%>"
					size="2" maxlength="4" validateOnPost="true" readonly="true" /></td>
				<td>&nbsp;</td>
				<td class="etichetta2">Gradualità</td>
				<td class="campo2"><af:textBox title="Gradualità"
					className="viewRiepilogo" name="gradualita" value="<%=gradualita%>"
					size="2" maxlength="4" validateOnPost="true" readonly="true" /></td>
			</tr>
		</table>
		</td>
		</tr>
	
		<tr>
			<td colspan="4">&nbsp;</td>
		</tr>
		<%
			if (canModify) {
		%>
		<tr>
			<td colspan="4" align="center"><input type="submit"
				class="pulsante" name="aggiorna" value="Aggiorna" />
			&nbsp;&nbsp;&nbsp; <input type="button" class="pulsante"
				name="annulla" value="Annulla" onclick="annullaModifiche()" />
			&nbsp;&nbsp;&nbsp; <input type="button" class="pulsante"
				name="esegui ricalcolo" value="Esegui Ricalcolo"
				onclick="ricalcolo()" /></td>
		</tr>
		<%
			}
		%>
		<tr>
			<td colspan="4" align="center">
			<%
			if (canStampaProspetto) {
			%>
			<input type="button" class="pulsante" name="prospetto" value="Stampa Prospetto" onclick="stampaProspetto()" />
			<%
		 	}
				if (("A").equalsIgnoreCase(codMonoStatoProspetto)) {%>
				<input type="button" class="pulsante" name="storicizza"
				value="Storicizza" onclick="storicizzaProsp('S')" /> 
				
				<% if (canStoricizzaUscita) {%>
				&nbsp;&nbsp;&nbsp; <input type="button" class="pulsante" name="storicizzaUscita"
				value="Uscita dall'obbligo" onclick="storicizzaProsp('U')" />
				<%}%>
				
 	<%} else if ( (("S").equalsIgnoreCase(codMonoStatoProspetto)
 				|| ("N").equalsIgnoreCase(codMonoStatoProspetto)
 				|| ("V").equalsIgnoreCase(codMonoStatoProspetto)
 				|| ("U").equalsIgnoreCase(codMonoStatoProspetto)) && (("S").equalsIgnoreCase(flgCompetenza)) ) {
 %> <input type="button" class="pulsante" name="copia"
				value="Genera Copia" onclick="copiaProspetto()" /> <%
 	}

 		if ( (!("N").equalsIgnoreCase(codMonoStatoProspetto)) && (("S").equalsIgnoreCase(flgCompetenza)) ) {
 %> &nbsp;&nbsp;&nbsp; <input type="button" class="pulsante"
				name="annullaProspetti" value="Annulla Prospetto"
				onclick="annullaProspetto()" /> <%
 	}
 %>
			</td>
		</tr>
	</table>
	<%
		out.print(htmlStreamBottom);
	%>
</af:form>
<p align="center">
<%
	if (operatoreInfo != null)
		operatoreInfo.showHTML(out);
%>
</p>
<br />

</body>
</html>
