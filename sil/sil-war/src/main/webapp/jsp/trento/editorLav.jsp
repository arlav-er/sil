
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="net.fckeditor.*"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ taglib uri="aftags" prefix="af"%>

<%@ page
	import="com.engiweb.framework.base.*,
	        com.engiweb.framework.base.SourceBean,
			com.engiweb.framework.configuration.ConfigSingleton,
			com.engiweb.framework.error.EMFErrorHandler,
    		it.eng.afExt.utils.DateUtils, 
    		it.eng.sil.security.User, 
    		it.eng.sil.security.*, 
    		it.eng.afExt.utils.*,
    		it.eng.sil.util.*, 
    		java.lang.*,
    		java.text.*, 
    		java.math.*,  
    		java.sql.*,   
    		oracle.sql.*,  
    		java.util.*"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="http://java.fckeditor.net" prefix="FCK"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>FCKeditor - JSP Sample</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="robots" content="noindex, nofollow" />
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">

<% String queryString = null; %>
<%@ include file="_apriGestioneDoc.inc"%>

<%

String pageToProfile = "ListaStampeParLavPage";
ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}

//Gestione Attributi
  PageAttribs attributi = new PageAttribs(user, pageToProfile);
  boolean canGeneraStampa = attributi.containsButton("GENERA_STAMPA"); //X TEST

	String FILETEMPLATE = "";
	String PRGTEMPLATESTAMPA = null;
	
	String CDNLAVORATORE = (String) serviceRequest.getAttribute("CDNLAVORATORE");
	String PRGAZIENDA = (String) serviceRequest.getAttribute("PRGAZIENDA");
	String PRGUNITA = (String) serviceRequest.getAttribute("PRGUNITA");
	PRGTEMPLATESTAMPA = (String) serviceRequest.getAttribute("PRGTEMPLATESTAMPA");
	String tipoDocumento = (String)serviceRequest.getAttribute("TIPODOCUMENTO");
	String  dominioDati  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "DOMINIO");	
	String pageBack = (String) serviceRequest.getAttribute("PAGEBACK");
	String prgDocumentoIns = serviceRequest.containsAttribute("prgdocumento") ? serviceRequest.getAttribute("prgdocumento").toString() : "";
	String codStatoAtto = serviceRequest.containsAttribute("codStatoAtto") ? serviceRequest.getAttribute("codStatoAtto").toString() : "";
	String codCpi = serviceRequest.containsAttribute("codCpi") ? serviceRequest.getAttribute("codCpi").toString() : "";
	int _funzione = Integer.parseInt(serviceRequest.getAttribute("CDNFUNZIONE")==null?"0":(String) serviceRequest.getAttribute("CDNFUNZIONE"));
	
	String isFirmaGrafometrica = (String) serviceRequest.getAttribute("FIRMAGRAFOMETRICA");
	String ipOperatore = (String) serviceRequest.getAttribute("IPOPERATORE");

	//System.out.println("dominio dati: "+ dominioDati);
				//System.out.println("CARICA I DATI CPI");

	String INDIRIZZO_STAMPA = "";
	String COD_CPI = "";
	String DESC_CPI = "";
	String INDIRIZZO = "";
	String LOCALITA = "";
	String CAP = "";
	String CODCOM = "";
	String CODPROVINCIA = "";
	String COMUNE = "";
	String PROVINCIA = "";
	String TELEFONO = "";
	String FAX = "";
	String CPIEMAIL = "";
	String ORARIO = "";
	String RESPONSABILE = "";
	String EMAIL_PEC = "";
	String EMAIL_ADL = "";
	
	
	//CARICA I DATI CPI
	SourceBean cont2 = (SourceBean) serviceResponse
			.getAttribute("MDATICPI");
	SourceBean row2 = (SourceBean) cont2.getAttribute("ROWS.ROW");
	if(row2 != null) {
		INDIRIZZO_STAMPA = row2.containsAttribute("INDIRIZZO_STAMPA") ? row2
				.getAttribute("INDIRIZZO_STAMPA").toString() : "";
		COD_CPI = row2.containsAttribute("COD_CPI") ? row2
			.getAttribute("COD_CPI").toString() : "";
		DESC_CPI = row2.containsAttribute("DESC_CPI") ? row2
			.getAttribute("DESC_CPI").toString() : "";
		INDIRIZZO = row2.containsAttribute("INDIRIZZO") ? row2
			.getAttribute("INDIRIZZO").toString() : "";
		LOCALITA = row2.containsAttribute("LOCALITA") ? row2
			.getAttribute("LOCALITA").toString() : "";
		CAP = row2.containsAttribute("CAP") ? row2.getAttribute(
			"CAP").toString() : "";
		CODCOM = row2.containsAttribute("CODCOM") ? row2
			.getAttribute("CODCOM").toString() : "";
		CODPROVINCIA = row2.containsAttribute("CODPROVINCIA") ? row2
			.getAttribute("CODPROVINCIA").toString() : "";
		COMUNE = row2.containsAttribute("COMUNE") ? row2
			.getAttribute("COMUNE").toString() : "";
		PROVINCIA = row2.containsAttribute("PROVINCIA") ? row2
			.getAttribute("PROVINCIA").toString() : "";
		TELEFONO = row2.containsAttribute("TELEFONO") ? row2
			.getAttribute("TELEFONO").toString() : "";
		FAX = row2.containsAttribute("FAX") ? row2.getAttribute(
			"FAX").toString() : "";
		CPIEMAIL = row2.containsAttribute("EMAIL") ? row2.getAttribute(
			"EMAIL").toString() : "";
		ORARIO = row2.containsAttribute("ORARIO") ? row2
			.getAttribute("ORARIO").toString() : "";
		RESPONSABILE = row2.containsAttribute("RESPONSABILE") ? row2
			.getAttribute("RESPONSABILE").toString() : "";
		EMAIL_PEC = row2.containsAttribute("EMAILPEC") ? row2
					.getAttribute("EMAILPEC").toString() : "";
		EMAIL_ADL = row2.containsAttribute("EMAILADL") ? row2
					.getAttribute("EMAILADL").toString() : "";
			//System.out.println("CARICA I DATI CPI .... OK");
	}
	String STRCODICEFISCALE = "";
	String STRCOGNOME = "";
	String STRNOME = "";
	String STRSESSO = "";
	String DATNASC = "";
	String STRCOMNAS = "";
	String STRPROVNAS = "";
	String STRCELL = "";
	String STREMAIL = "";
	String STRCITTADINANZA = "";
	String STRNAZIONE = "";
	String STRCITTADINANZA2 = "";
	String STRNAZIONE2 = "";
	String STRCOMRES = "";
	String PROVRES = "";
	String STRINDIRIZZORES = "";
	String STRLOCALITARES = "";
	String STRCAPRES = "";
	String STRCOMDOM = "";
	String PROVDOM = "";
	String STRINDIRIZZODOM = "";
	String STRLOCALITADOM = "";
	String STRCAPDOM = "";
	String STRTELRES = "";
	String STRTELDOM = "";
	String DATADID = "";
	String categoriaCM = "";
	String DataAppPAT = "";
	String OraAppPAT = "";
	String DataAppOrientamPAI = "";
	String OraAppOrientamPAI = "";
	String DataInizioCM = "";
	String descrizioneStatoOcc = "", mesiAnzianita = "", giorniAnzianita = "";
	String descrizioneCpiTrasferimento = "";
	String dataInizioTrasferimentoCpi = ""	;	
	String dataanzianital68 = "";
	String numiscrizione = ""	;	
// 	String DataAppPAI = "";
// 	String OraAppPAI = "";
	
	
//	System.out.println("CARICA I DATI DEL LAVORATORE");
	if (StringUtils.isFilled(CDNLAVORATORE)) {
		//CARICA I DATI DEL LAVORATORE
		SourceBean cont3 = (SourceBean) serviceResponse
				.getAttribute("MDATILAVORATORE");
		SourceBean row3 = (SourceBean) cont3.getAttribute("ROWS.ROW");
	
		STRCODICEFISCALE = row3.containsAttribute("STRCODICEFISCALE") ? row3
				.getAttribute("STRCODICEFISCALE").toString() : "";
		STRCOGNOME = row3.containsAttribute("STRCOGNOME") ? row3
				.getAttribute("STRCOGNOME").toString() : "";
		STRNOME = row3.containsAttribute("STRNOME") ? row3
				.getAttribute("STRNOME").toString() : "";
		STRSESSO = row3.containsAttribute("STRSESSO") ? row3
				.getAttribute("STRSESSO").toString() : "";
		DATNASC = row3.containsAttribute("DATNASC") ? row3
				.getAttribute("DATNASC").toString() : "";
		STRCOMNAS = row3.containsAttribute("STRCOMNAS") ? row3
				.getAttribute("STRCOMNAS").toString() : "";
		STRPROVNAS = row3.containsAttribute("STRPROVNAS") ? row3
				.getAttribute("STRPROVNAS").toString() : "";
		STRCELL = row3.containsAttribute("STRCELL") ? row3
				.getAttribute("STRCELL").toString() : "";
		STREMAIL = row3.containsAttribute("STREMAIL") ? row3
				.getAttribute("STREMAIL").toString() : "";
		STRCITTADINANZA = row3.containsAttribute("STRCITTADINANZA") ? row3
				.getAttribute("STRCITTADINANZA").toString() : "";
		STRNAZIONE = row3.containsAttribute("STRNAZIONE") ? row3
				.getAttribute("STRNAZIONE").toString() : "";
		STRCITTADINANZA2 = row3.containsAttribute("STRCITTADINANZA2") ? row3
				.getAttribute("STRCITTADINANZA2").toString() : "";
		STRNAZIONE2 = row3.containsAttribute("STRNAZIONE2") ? row3
				.getAttribute("STRNAZIONE2").toString() : "";
		STRCOMRES = row3.containsAttribute("STRCOMRES") ? row3
				.getAttribute("STRCOMRES").toString() : "";
		PROVRES = row3.containsAttribute("PROVRES") ? row3
				.getAttribute("PROVRES").toString() : "";
		STRINDIRIZZORES = row3.containsAttribute("STRINDIRIZZORES") ? row3
				.getAttribute("STRINDIRIZZORES").toString() : "";
		STRLOCALITARES = row3.containsAttribute("STRLOCALITARES") ? row3
				.getAttribute("STRLOCALITARES").toString() : "";
		STRCAPRES = row3.containsAttribute("STRCAPRES") ? row3
				.getAttribute("STRCAPRES").toString() : "";
		STRCOMDOM = row3.containsAttribute("STRCOMDOM") ? row3
				.getAttribute("STRCOMDOM").toString() : "";
		PROVDOM = row3.containsAttribute("PROVDOM") ? row3
				.getAttribute("PROVDOM").toString() : "";
		STRINDIRIZZODOM = row3.containsAttribute("STRINDIRIZZODOM") ? row3
				.getAttribute("STRINDIRIZZODOM").toString() : "";
		STRLOCALITADOM = row3.containsAttribute("STRLOCALITADOM") ? row3
				.getAttribute("STRLOCALITADOM").toString() : "";
		STRCAPDOM = row3.containsAttribute("STRCAPDOM") ? row3
				.getAttribute("STRCAPDOM").toString() : "";
		STRTELRES = row3.containsAttribute("STRTELRES") ? row3
				.getAttribute("STRTELRES").toString() : "";
		STRTELDOM = row3.containsAttribute("STRTELDOM") ? row3.getAttribute("STRTELDOM").toString() : "";
		
		//CARICA I DATI DID DEL LAVORATORE
		SourceBean moduleDid = (SourceBean) serviceResponse.getAttribute("MDatiDidLavoratore");
		if (moduleDid != null) {
			SourceBean rowDid = (SourceBean) moduleDid.getAttribute("ROWS.ROW");
			if (rowDid != null) {
				DATADID = rowDid.containsAttribute("datdichiarazione") ? rowDid.getAttribute("datdichiarazione").toString() : "";
			}	
		}
		
		//DataAppOrientam
		SourceBean moduleDataAppOrientam = (SourceBean) serviceResponse.getAttribute("MDatiDataAppOrientamLavoratore");
		if (moduleDataAppOrientam != null) {
			SourceBean rowDataAppOrientam = (SourceBean) moduleDataAppOrientam.getAttribute("ROWS.ROW");
			if (rowDataAppOrientam != null) {
				DataAppOrientamPAI = rowDataAppOrientam.containsAttribute("dataappuntamento") ? rowDataAppOrientam.getAttribute("dataappuntamento").toString() : "";
				OraAppOrientamPAI = rowDataAppOrientam.containsAttribute("ora") ? rowDataAppOrientam.getAttribute("ora").toString() : "";
			}	
		}
		
		//DataAppPatronato
		SourceBean moduleDataAppPat = (SourceBean) serviceResponse.getAttribute("MDatiDataAppPatronato");
		if (moduleDataAppPat != null) {
			SourceBean rowDataAppPat = (SourceBean) moduleDataAppPat.getAttribute("ROWS.ROW");
			if (rowDataAppPat != null) {
				DataAppPAT = rowDataAppPat.containsAttribute("dataappuntamento") ? rowDataAppPat.getAttribute("dataappuntamento").toString() : "";
				OraAppPAT = rowDataAppPat.containsAttribute("ora") ? rowDataAppPat.getAttribute("ora").toString() : "";
			}	
		}

		// CARICA I DATI ISCRIZIONE CM LAVORATORE
		SourceBean moduleDatiDataCMIscrizione = (SourceBean) serviceResponse.getAttribute("MDatiDataCMIscrizione");
		if (moduleDatiDataCMIscrizione != null) {
			SourceBean rowDataCMIscrizione = (SourceBean) moduleDatiDataCMIscrizione.getAttribute("ROWS.ROW");
			if (rowDataCMIscrizione != null) {
				DataInizioCM = rowDataCMIscrizione.containsAttribute("DATULTIMAISCR") ? rowDataCMIscrizione.getAttribute("DATULTIMAISCR").toString() : "";
				categoriaCM = rowDataCMIscrizione.containsAttribute("descrizione") ? rowDataCMIscrizione.getAttribute("descrizione").toString() : "";
				dataanzianital68 = rowDataCMIscrizione.containsAttribute("dataanzianital68") ? rowDataCMIscrizione.getAttribute("dataanzianital68").toString() : "";
				numiscrizione = rowDataCMIscrizione.containsAttribute("numiscrizione") ? rowDataCMIscrizione.getAttribute("numiscrizione").toString() : "";
			}	
		}
		
		//CARICA I DATI STATO OCCUPAZIONALE DEL LAVORATORE
		SourceBean moduleStatoOcc = (SourceBean) serviceResponse.getAttribute("M_GetInfoStatoOccDatiLavoratore");
		if (moduleStatoOcc != null) {
			SourceBean rowOcc = (SourceBean) moduleStatoOcc.getAttribute("ROWS.ROW");
			if (rowOcc != null) {
				descrizioneStatoOcc = rowOcc.containsAttribute("DescrizioneStato") ? rowOcc.getAttribute("DescrizioneStato").toString() : "";
				mesiAnzianita = rowOcc.containsAttribute("mesiAnzianita") ? rowOcc.getAttribute("mesiAnzianita").toString() : "";
				giorniAnzianita = rowOcc.containsAttribute("giorniAnzianita") ? rowOcc.getAttribute("giorniAnzianita").toString() : "";
			}	
		}
		
		// CARICA I DATI DA an_lav_storia_inf E de_cpi
		SourceBean moduleLavStoriaInf = (SourceBean) serviceResponse.getAttribute("M_GetLavStoriaInfoStampeParam");
		if (moduleLavStoriaInf != null) {
			SourceBean rowLavInf = (SourceBean) moduleLavStoriaInf.getAttribute("ROWS.ROW");
			if (rowLavInf != null) {
				descrizioneCpiTrasferimento = rowLavInf.containsAttribute("descrizioneCpiTrasferimento") ? rowLavInf.getAttribute("descrizioneCpiTrasferimento").toString() : "";
				dataInizioTrasferimentoCpi = rowLavInf.containsAttribute("dataInizioTrasferimentoCpi") ? rowLavInf.getAttribute("dataInizioTrasferimentoCpi").toString() : "";
			}	
		}
		
// 		//DataAppOrientam
// 		SourceBean moduleDataAppPAI = (SourceBean) serviceResponse.getAttribute("MDatiDataAppPAILavoratore");
// 		if (moduleDataAppPAI != null) {
// 			SourceBean rowDataAppPAI = (SourceBean) moduleDataAppPAI.getAttribute("ROWS.ROW");
// 			if (rowDataAppPAI != null) {
// 				DataAppPAI = rowDataAppPAI.containsAttribute("dataappuntamento") ? rowDataAppPAI.getAttribute("dataappuntamento").toString() : "";
// 				OraAppPAI = rowDataAppPAI.containsAttribute("ora") ? rowDataAppPAI.getAttribute("ora").toString() : "";
// 			}	
// 		}
		
	}
	//System.out.println("CARICA I DATI DEL LAVORATORE....OK");

	
	String RagSociale = "";
	String PartitaIva = "";
	String CodiceFiscale = "";
	String NaturaGiuridica = "";
	String TipoAzienda = "";
	String SitoInternet = "";
	String AttivAteco = "";
	String NumSoci = "";
	String NumDipendenti = "";
	String NumCollaboratori = "";
	String NumAltraPosizione = "";
	String DataInizioAttivita = "";
	String DataFineAttivita = "";
	String PatInail = "";
	String FlgObbl68 = "";
	String NumAlboInterinali = "";
	String NumAgSomministrazione = "";
	String DataCambioRagsociale = "";
	
	
	String DenominazioneSedeLegale = "";
	String IndirizzoSedeLegale = "";
	String ComuneSedeLegale = "";
	String CapSedeLegale = "";
	String TelSedeLegale = "";
	String FaxSedeLegale = "";
	String EmailSedeLegale = "";
	
	//CARICA I DATI DELL' AZIENDA
	//	System.out.println("CARICA I DATI AZIENDA");

	if (StringUtils.isFilled(PRGAZIENDA)) {
		SourceBean datiAz = (SourceBean) serviceResponse
				.getAttribute("MDATIAZIENDA");
		SourceBean rowAz = (SourceBean) datiAz.getAttribute("ROWS.ROW");
		
		if (rowAz != null) {
			RagSociale = rowAz.containsAttribute("RagSociale") ? rowAz.getAttribute("RagSociale").toString() : "";
			PartitaIva = rowAz.containsAttribute("PartitaIva") ? rowAz.getAttribute("PartitaIva").toString() : "";
			CodiceFiscale = rowAz.containsAttribute("CodiceFiscale") ? rowAz.getAttribute("CodiceFiscale").toString() : "";
			NaturaGiuridica = rowAz.containsAttribute("NaturaGiuridica") ? rowAz.getAttribute("NaturaGiuridica").toString() : "";			
			TipoAzienda = rowAz.containsAttribute("TipoAzienda") ? rowAz.getAttribute("TipoAzienda").toString() : "";
			SitoInternet = rowAz.containsAttribute("SitoInternet") ? rowAz.getAttribute("SitoInternet").toString() : "";
			AttivAteco = rowAz.containsAttribute("AttivAteco") ? rowAz.getAttribute("AttivAteco").toString() : "";
			NumSoci = rowAz.containsAttribute("NumSoci") ? rowAz.getAttribute("NumSoci").toString() : "";			
			NumDipendenti = rowAz.containsAttribute("NumDipendenti") ? rowAz.getAttribute("NumDipendenti").toString() : "";
			NumCollaboratori = rowAz.containsAttribute("NumCollaboratori") ? rowAz.getAttribute("NumCollaboratori").toString() : "";
			NumAltraPosizione = rowAz.containsAttribute("NumAltraPosizione") ? rowAz.getAttribute("NumAltraPosizione").toString() : "";
			DataInizioAttivita = rowAz.containsAttribute("DataInizioAttivita") ? rowAz.getAttribute("DataInizioAttivita").toString() : "";			
			DataFineAttivita = rowAz.containsAttribute("DataFineAttivita") ? rowAz.getAttribute("DataFineAttivita").toString() : "";
			PatInail = rowAz.containsAttribute("PatInail") ? rowAz.getAttribute("PatInail").toString() : "";
			FlgObbl68 = rowAz.containsAttribute("FlgObbl68") ? rowAz.getAttribute("FlgObbl68").toString() : "";
			NumAlboInterinali = rowAz.containsAttribute("NumAlboInterinali") ? rowAz.getAttribute("NumAlboInterinali").toString() : "";
			NumAgSomministrazione = rowAz.containsAttribute("NumAgSomministrazione") ? rowAz.getAttribute("NumAgSomministrazione").toString() : "";
			DataCambioRagsociale = rowAz.containsAttribute("DataCambioRagsociale") ? rowAz.getAttribute("DataCambioRagsociale").toString() : "";
				
		}
		//CARICA I DATI LEGALI DELL' AZIENDA
		//System.out.println("CARICA I DATI LEGALI AZIENDA");
		SourceBean datiLegAz = (SourceBean) serviceResponse
				.getAttribute("MDATILEGALIAZIENDA");
		SourceBean rowLegAz = (SourceBean) datiLegAz.getAttribute("ROWS.ROW");
		if (rowLegAz != null) {
			DenominazioneSedeLegale = rowLegAz.containsAttribute("DenominazioneSedeLegale") ? rowLegAz.getAttribute("DenominazioneSedeLegale").toString() : "";
			IndirizzoSedeLegale = rowLegAz.containsAttribute("IndirizzoSedeLegale") ? rowLegAz.getAttribute("IndirizzoSedeLegale").toString() : "";
			ComuneSedeLegale = rowLegAz.containsAttribute("ComuneSedeLegale") ? rowLegAz.getAttribute("ComuneSedeLegale").toString() : "";
			CapSedeLegale = rowLegAz.containsAttribute("CapSedeLegale") ? rowLegAz.getAttribute("CapSedeLegale").toString() : "";			
			TelSedeLegale = rowLegAz.containsAttribute("TelSedeLegale") ? rowLegAz.getAttribute("TelSedeLegale").toString() : "";
			FaxSedeLegale = rowLegAz.containsAttribute("FaxSedeLegale") ? rowLegAz.getAttribute("FaxSedeLegale").toString() : "";
			EmailSedeLegale = rowLegAz.containsAttribute("EmailSedeLegale") ? rowLegAz.getAttribute("EmailSedeLegale").toString() : "";
			//System.out.println("CARICA I DATI LEGALI AZIENDA ...OK");
		}
	}
	//System.out.println("CARICA I DATI AZIENDA ...OK");

	
	

	
	
	SourceBean cont4 = (SourceBean) serviceResponse.getAttribute("MDETTAGLIOEDITORLAV");
	SourceBean row4 = (SourceBean) cont4.getAttribute("ROWS.ROW");

	FILETEMPLATE = row4.containsAttribute("FILETEMPLATE") ? row4.getAttribute("FILETEMPLATE").toString() : "";
	FILETEMPLATE = FILETEMPLATE.replace("\n", "").replace("\r", "");
	
	
	//sostituisco gli ALIAS con i dati dell'azienda
	FILETEMPLATE = FILETEMPLATE.replace("@RagSociale", RagSociale);
	FILETEMPLATE = FILETEMPLATE.replace("@PartitaIva", PartitaIva);
	FILETEMPLATE = FILETEMPLATE.replace("@CodiceFiscaleAzienda", CodiceFiscale);
	FILETEMPLATE = FILETEMPLATE.replace("@NaturaGiuridica", NaturaGiuridica);
	FILETEMPLATE = FILETEMPLATE.replace("@TipoAzienda", TipoAzienda);
	FILETEMPLATE = FILETEMPLATE.replace("@SitoInternet", SitoInternet);
	FILETEMPLATE = FILETEMPLATE.replace("@AttivAteco",AttivAteco);
	FILETEMPLATE = FILETEMPLATE.replace("@NumSoci", NumSoci);
	FILETEMPLATE = FILETEMPLATE.replace("@NumDipendenti", NumDipendenti);
	FILETEMPLATE = FILETEMPLATE.replace("@NumCollaboratori", NumCollaboratori);
	FILETEMPLATE = FILETEMPLATE.replace("@NumAltraPosizione", NumAltraPosizione);
	FILETEMPLATE = FILETEMPLATE.replace("@DataInizioAttivita", DataInizioAttivita);
	FILETEMPLATE = FILETEMPLATE.replace("@DataFineAttivita", DataFineAttivita);
	FILETEMPLATE = FILETEMPLATE.replace("@PatInail",PatInail);
	FILETEMPLATE = FILETEMPLATE.replace("@FlgObbl68", FlgObbl68);
	FILETEMPLATE = FILETEMPLATE.replace("@NumAlboInterinali", NumAlboInterinali);
	FILETEMPLATE = FILETEMPLATE.replace("@NumAgSomministrazione", NumAgSomministrazione);
	FILETEMPLATE = FILETEMPLATE.replace("@DataCambioRagsociale",DataCambioRagsociale);
	
	//sostituisco gli ALIAS con i dati legali dell'azienda
	FILETEMPLATE = FILETEMPLATE.replace("@DenominazioneSedeLegale", DenominazioneSedeLegale);
	FILETEMPLATE = FILETEMPLATE.replace("@IndirizzoSedeLegale", IndirizzoSedeLegale);
	FILETEMPLATE = FILETEMPLATE.replace("@ComuneSedeLegale", ComuneSedeLegale);
	FILETEMPLATE = FILETEMPLATE.replace("@CapSedeLegale", CapSedeLegale);
	FILETEMPLATE = FILETEMPLATE.replace("@TelSedeLegale", TelSedeLegale);
	FILETEMPLATE = FILETEMPLATE.replace("@FaxSedeLegale", FaxSedeLegale);
	FILETEMPLATE = FILETEMPLATE.replace("@EmailSedeLegale",EmailSedeLegale);
	
	//sostituisco gli ALIAS con i dati del lavoratore
	FILETEMPLATE = FILETEMPLATE.replace("@NomeLavoratore", STRNOME);
	FILETEMPLATE = FILETEMPLATE.replace("@CognomeLavoratore",STRCOGNOME);
	FILETEMPLATE = FILETEMPLATE.replace("@CodiceFiscale",STRCODICEFISCALE);
	FILETEMPLATE = FILETEMPLATE.replace("@Sesso", STRSESSO);
	FILETEMPLATE = FILETEMPLATE.replace("@DataNascita", DATNASC);
	FILETEMPLATE = FILETEMPLATE.replace("@Nazione", STRNAZIONE);
	FILETEMPLATE = FILETEMPLATE.replace("@Cittadinanza",STRCITTADINANZA);
	FILETEMPLATE = FILETEMPLATE.replace("@SecondaCittadinanza",STRCITTADINANZA2);
	FILETEMPLATE = FILETEMPLATE.replace("@ComuneNascita", STRCOMNAS);
	FILETEMPLATE = FILETEMPLATE.replace("@ProvinciaNascita", STRPROVNAS);
	FILETEMPLATE = FILETEMPLATE.replace("@Cellulare", STRCELL);
	FILETEMPLATE = FILETEMPLATE.replace("@Email", STREMAIL);
	FILETEMPLATE = FILETEMPLATE.replace("@IndirizzoDomicilio",STRINDIRIZZODOM);
	FILETEMPLATE = FILETEMPLATE.replace("@IndirizzoResidenza",STRINDIRIZZORES);
	FILETEMPLATE = FILETEMPLATE.replace("@ComuneDomicilio",STRCOMDOM);
	FILETEMPLATE = FILETEMPLATE.replace("@ComuneResidenza",STRCOMRES);
	FILETEMPLATE = FILETEMPLATE.replace("@SecondaNazionalita",STRNAZIONE2);
	FILETEMPLATE = FILETEMPLATE.replace("@ProvinciaResidenza",PROVRES);
	FILETEMPLATE = FILETEMPLATE.replace("@LocalitaResidenza",STRLOCALITARES);
	FILETEMPLATE = FILETEMPLATE.replace("@CapResidenza", STRCAPRES);
	FILETEMPLATE = FILETEMPLATE.replace("@ProvinciaDomicilio",PROVDOM);
	FILETEMPLATE = FILETEMPLATE.replace("@LocalitaDomicilio",STRLOCALITADOM);
	FILETEMPLATE = FILETEMPLATE.replace("@CapDomicilio", STRCAPDOM);
	FILETEMPLATE = FILETEMPLATE.replace("@TelefonoResidenza",STRTELRES);
	FILETEMPLATE = FILETEMPLATE.replace("@TelefonoDomicilio",STRTELDOM);
	FILETEMPLATE = FILETEMPLATE.replace("@DataDid", DATADID);
	FILETEMPLATE = FILETEMPLATE.replace("@CategoriaCM", categoriaCM);
	
	// sostituisco gli ALIAS con i dati dello stato occupazionale del lavoratore
	FILETEMPLATE = FILETEMPLATE.replace("@StatoOccupazionaleCorrente", descrizioneStatoOcc);
	FILETEMPLATE = FILETEMPLATE.replace("@MesiAnzianitaCorrente", mesiAnzianita);
	FILETEMPLATE = FILETEMPLATE.replace("@GiorniAnzianitaCorrente", giorniAnzianita);
	FILETEMPLATE = FILETEMPLATE.replace("@DataTrasferimentoCpi", dataInizioTrasferimentoCpi);
	FILETEMPLATE = FILETEMPLATE.replace("@DescrizioneTrasferimentoCpi", descrizioneCpiTrasferimento);
	
	// sostituisco gli ALIAS con i iscrizione CM per L68
	FILETEMPLATE = FILETEMPLATE.replace("@DataAnazianitaL68", dataanzianital68);
	FILETEMPLATE = FILETEMPLATE.replace("@NumeroIscrizione", numiscrizione);
	
	int cdnProfilo = 0;
	cdnProfilo = user.getCdnProfilo();
	if(cdnProfilo==9){
		FILETEMPLATE = FILETEMPLATE.replace("@DataAppPAT", "<b>Il giorno " + DataAppPAT);
		FILETEMPLATE = FILETEMPLATE.replace("@OraAppPAT","<b> alle ore " + OraAppPAT);
		FILETEMPLATE = FILETEMPLATE.replace("@DataAppOrientamPAI", "");
		FILETEMPLATE = FILETEMPLATE.replace("@OraAppOrientamPAI", "");	
	}else{
		FILETEMPLATE = FILETEMPLATE.replace("@DataAppOrientamPAI", "<b>Il giorno " + DataAppOrientamPAI);
		FILETEMPLATE = FILETEMPLATE.replace("@OraAppOrientamPAI", "<b> alle ore " + OraAppOrientamPAI);	
		FILETEMPLATE = FILETEMPLATE.replace("@DataAppPAT", "");
		FILETEMPLATE = FILETEMPLATE.replace("@OraAppPAT","");
	}
	FILETEMPLATE = FILETEMPLATE.replace("@DataIscrCM", DataInizioCM);
	
// 	FILETEMPLATE = FILETEMPLATE.replace("@DataAppPAI", DataAppPAI);
// 	FILETEMPLATE = FILETEMPLATE.replace("@OraAppPAI", OraAppPAI);
	

	
	//sostituisco gli ALIAS con i dati del CPI
	FILETEMPLATE = FILETEMPLATE.replace("@CodiceCPI", COD_CPI);
	FILETEMPLATE = FILETEMPLATE.replace("@Nome", DESC_CPI);
	FILETEMPLATE = FILETEMPLATE.replace("@Indirizzo", INDIRIZZO);
	FILETEMPLATE = FILETEMPLATE.replace("@Localita", LOCALITA);
	FILETEMPLATE = FILETEMPLATE.replace("@Cap", CAP);
	FILETEMPLATE = FILETEMPLATE.replace("@CodComune", CODCOM);
	FILETEMPLATE = FILETEMPLATE.replace("@CodProvincia",CODPROVINCIA);
	FILETEMPLATE = FILETEMPLATE.replace("@Comune", COMUNE);
	FILETEMPLATE = FILETEMPLATE.replace("@Provincia", PROVINCIA);
	FILETEMPLATE = FILETEMPLATE.replace("@Telefono", TELEFONO);
	FILETEMPLATE = FILETEMPLATE.replace("@Fax", FAX);
	
	FILETEMPLATE = FILETEMPLATE.replace("@CPIEmail", CPIEMAIL);
	FILETEMPLATE = FILETEMPLATE.replace("@Orario", ORARIO);
	FILETEMPLATE = FILETEMPLATE.replace("@Responsabile", RESPONSABILE);
	FILETEMPLATE = FILETEMPLATE.replace("@emailPEC", EMAIL_PEC);
	FILETEMPLATE = FILETEMPLATE.replace("@emailADL", EMAIL_ADL);
	

	
	
	StringBuffer sezioneAllegati = new StringBuffer();
	
	sezioneAllegati.append("<div >");
	sezioneAllegati.append("<b>Lista Allegati</b>");
	sezioneAllegati.append("</div>");
	sezioneAllegati.append("<hr />");
	sezioneAllegati.append("<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\">");
	sezioneAllegati.append("<tbody><tr>");
	sezioneAllegati.append("<td><b>Nome</b></td>");
	sezioneAllegati.append("<td><b>Protocollo</b></td>");
 	sezioneAllegati.append("<td width='25%'><b>Stato Atto</b></td>");
//  	sezioneAllegati.append("<td><b>Caricamento Successivo</b></td>");
	sezioneAllegati.append("<td><b>Tipo Allegato</b></td>");
	sezioneAllegati.append("</tr>");
	
	
	SourceBean moduleAllegati = (SourceBean) serviceResponse.getAttribute("MListaAllegatiDocStampaParam.rows");
	
	Vector vectSB = moduleAllegati.getAttributeAsVector("ROW");
	Iterator iter = vectSB.iterator();
	boolean checkAllegati = false;
	while (iter.hasNext()) {
		SourceBean allegati = (SourceBean) iter.next();
		//if(allegati.getAttribute("FLGPRESAVISIONE")!=null&&(allegati.getAttribute("FLGPRESAVISIONE").equals("S")||allegati.getAttribute("FLGPRESAVISIONE").equals("s"))){
			if(!checkAllegati)
				checkAllegati = true;
			String nomeAll = allegati.containsAttribute("AM_DOC_DESCRIZIONE") ? allegati.getAttribute("AM_DOC_DESCRIZIONE").toString() : "";
			String numProt = allegati.containsAttribute("AM_NUMERO_PROTOCOLLO") ? allegati.getAttribute("AM_NUMERO_PROTOCOLLO").toString() : "";
			String descAll = allegati.containsAttribute("STRDESCRIZIONE") ? allegati.getAttribute("STRDESCRIZIONE").toString() : "";
			sezioneAllegati.append("<tr><td>"+nomeAll+"</td>");
			sezioneAllegati.append("<td>"+numProt+"</td>");
	 		sezioneAllegati.append("<td>"+allegati.getAttribute("statoatto")+"</td>");
// 	 		if(allegati.getAttribute("FLGCARICSUCCESSIVO")!=null)
// 		 		sezioneAllegati.append("<td>"+allegati.getAttribute("FLGCARICSUCCESSIVO")+"</td>");
// 	 		else
// 	 			sezioneAllegati.append("<td></td>");
			sezioneAllegati.append("<td>"+descAll+"</td>");
			sezioneAllegati.append("</tr>");
		//}
	} // while
		
	sezioneAllegati.append("</tbody></table>");
		
	if(moduleAllegati.getAttribute("ROW")!= null&&checkAllegati) {
		FILETEMPLATE = FILETEMPLATE.replace("@Allegati",sezioneAllegati);
	}		
	else
		FILETEMPLATE = FILETEMPLATE.replace("@Allegati","");
			
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
	String dataStr = sdf.format(new java.util.Date());

	FILETEMPLATE = FILETEMPLATE.replace("@DataSistema", dataStr);
	
	
	NavigationCache formInserimentoTemplate = null;
	
	String htmlStreamTop = StyleUtils.roundTopTable(true);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(true);




	FCKeditor fckEditor = new FCKeditor(request, "EditorDefault");
	fckEditor.setToolbarSet("MyTB");
	fckEditor.setBasePath("/js/fckeditor");
	fckEditor.setWidth("750px");
	fckEditor.setHeight("600px");
	fckEditor.setConfig("language", "it");
	fckEditor.setValue(FILETEMPLATE);
%>

<script type="text/javascript"
	src="../../js/fckeditor/editor/js/jquery-1.11.2.js"> </script>
<script type="text/javascript"
	src="../../js/fckeditor/editor/js/jquery.fileDownload.js"> </script>
<script type="text/javascript">
var contextPath = "<%=request.getContextPath()%>";
var scheme =      "<%=request.getScheme()     %>";
var header =      '<%=request.getHeader("Host")%>';
var indietro = false;

function settaOperazione(operazione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	document.frmTemplate.GENERASTAMPA.value = operazione;
	document.frmTemplate.submit();
	if(operazione=="BACK")
		indietro = true;
}

function viewDoc(){
	
	alert('<%=fckEditor%>');
}

function controlloAngoliFirma(){
	if(!indietro){
		var oEditorStr = FCKeditorAPI.GetInstance('EditorDefault');
		
		var str2 = oEditorStr.GetXHTML(true);
		var numeroOccorrenzeDI_2 = (str2.match(new RegExp("@DI_2","g"))||[]).length;
		var numeroOccorrenzeDI_1 = (str2.match(new RegExp("@DI_1","g"))||[]).length;
		var numeroOccorrenzeDD_2 = (str2.match(new RegExp("@DD_2","g"))||[]).length;
		var numeroOccorrenzeDD_1 = (str2.match(new RegExp("@DD_1","g"))||[]).length;
	// 	alert("trovate "+ numeroOccorrenzeDI_2 +" in str2 di @DI_2");
	// 	alert("trovate "+ numeroOccorrenzeDI_1 +" in str2 di @DI_1");
	// 	alert("trovate "+ numeroOccorrenzeDD_2 +" in str2 di @DD_2");
	// 	alert("trovate "+ numeroOccorrenzeDD_1 +" in str2 di @DD_1");
		if((numeroOccorrenzeDI_2==0&&numeroOccorrenzeDI_1>0)||(numeroOccorrenzeDI_2>0&&numeroOccorrenzeDI_1==0)){
			alert("Attenzione, gli angoli di firma dell'operatore non sono stati inseriti correttamente per cui, in fase di generazione stampa, si procederà con la firma autografa dell'operatore");
		}
		if((numeroOccorrenzeDD_2==0&&numeroOccorrenzeDD_1==0)||(numeroOccorrenzeDD_2>0&&numeroOccorrenzeDD_1==0)||(numeroOccorrenzeDD_2==0&&numeroOccorrenzeDD_1>0)){
			
			alert("Attenzione, gli angoli di firma del lavoratore non sono stati inseriti per cui, in fase di generazione stampa, si procederà con la firma autografa del lavoratore");
			document.getElementById('FIRMAGRAFOMETRICA').value="false";
		}
	}	
	//return false;
}
</script>

<script type="text/javascript"
	src="../../js/fckeditor/editor/js/customeditor.js"></script>
<script type="text/javascript"
	src="../../js/fckeditor/editor/js/dragdrop.js"></script>

</head>

<body>
<div id="layout">
	<p class="titolo"><b>Creazione modello stampa</b></p>
	<%
			if(isFirmaGrafometrica.equalsIgnoreCase("true")){						
				 %>
	<form name="frmTemplate" action="AdapterHTTP" method="POST" id="formSpil" onsubmit="controlloAngoliFirma()">
	<%
			}else{
	%>
	<form name="frmTemplate" action="AdapterHTTP" method="POST" id="formSpil">
	<%
			}
	%>
		<%out.print(htmlStreamTop);%>

		<table  class="main" border="0">
			<tr align="center">
				<td>
				
				<%


					out.println(fckEditor);
					
				%>
				
				<!-- questi campi servono per la modifica dell'editor -->
				<input type="hidden" id="page" name="PAGE" value="<%=pageBack%>">
				<input type="hidden" id="filetemplate" name="FILETEMPLATE" value="">
				<input type="hidden" name="PRGTEMPLATESTAMPA" value="<%=PRGTEMPLATESTAMPA%>">
				<input type="hidden" name="PRGDOCUMENTO" value="<%=prgDocumentoIns%>">
				<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
				<input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>">
				<input type="hidden" name="GENERASTAMPA" value="">
				<input type="hidden" name="PROTOCOL" value="">
				<input type="hidden" name="HOST" value="">
				<input type="hidden" name="PORT" value="">
				<input type="hidden" name="DOMINIO" value="<%=dominioDati%>">
				<!-- IL TIPO DI EDITOR DA GESTIRE è "L" (LAVORATORE) -->
				<input type="hidden" name="TIPOEDITOR" value="L">
		
				<!-- questi campi sono la trasposizione dei campi presenti nel popup GestisciStatoDoc -->		
				<input type="hidden" name="CDNLAVORATORE" value="<%=CDNLAVORATORE%>">
				<input type="hidden" name="PRGAZIENDA" value="<%=PRGAZIENDA%>"/>
				<input type="hidden" name="PRGUNITA" value="<%=PRGUNITA%>"/>
				<input type="hidden" name="codCpi" value="<%= codCpi %>">
				<input type="hidden" name="salvaDB" type="checkbox" checked="false" value="SalvaDB"/>
				<input type="hidden" name="apri" type="checkbox" checked="true" value="Apri"/>
				<input type="hidden" name="asAttachment" value=""/>
				<input type="hidden" name="apriFileBlob" value=""/>
				<input type="hidden" name="protocolla" value=""/>
				<input type="hidden" name="annoProt" value="" />
		  		<input type="hidden" name="numProt"  value="" />
		  		<input type="hidden" name="dataProt" value="" />
		  		<input type="hidden" name="oraProt"  value="" />
				<input type="hidden" name="dataOraProt"  value="" />
				<input type="hidden" name="protAutomatica"  value="" />
				<input type="hidden" name="docInOut"  value="" />
				
				<!-- i dati CPI sono per la Servlet  -->
				<input type="hidden" name="INDIRIZZO_STAMPA" value="<%=INDIRIZZO_STAMPA%>">
		        <input type="hidden" name="COD_CPI" value="<%=COD_CPI%>">
		        <input type="hidden" name="DESC_CPI" value="<%=DESC_CPI%>">
		        <input type="hidden" name="INDIRIZZO" value="<%=INDIRIZZO%>">
		        <input type="hidden" name="LOCALITA" value="<%=LOCALITA%>">
		        <input type="hidden" name="CAP" value="<%=CAP%>">
		        <input type="hidden" name="CODCOM" value="<%=CODCOM%>">
		        <input type="hidden" name="CODPROVINCIA" value="<%=CODPROVINCIA%>">
		        <input type="hidden" name="COMUNE" value="<%=COMUNE%>">
		        <input type="hidden" name="PROVINCIA" value="<%=PROVINCIA%>">
		        <input type="hidden" name="TELEFONO" value="<%=TELEFONO%>">
		        <input type="hidden" name="FAX" value="<%=FAX%>">
		        <input type="hidden" name="CPIEMAIL" value="<%=CPIEMAIL%>">
		        <input type="hidden" name="ORARIO" value="<%=ORARIO%>">
		        <input type="hidden" name="RESPONSABILE" value="<%=RESPONSABILE%>">
		        <input type="hidden" id="FIRMAGRAFOMETRICA" name="FIRMAGRAFOMETRICA" value="<%=isFirmaGrafometrica%>">
				<input type="hidden" name="IPOPERATORE" value="<%=ipOperatore%>">
				</td>
			</tr>
			
			<!-- pulsanti Anteprima Stampa Indietro  -->
			<tr>
				<td>
				
				
								<%
 				String onClick = "";
								
// 								<input type="hidden" id="page" name="PAGE" value="DettaglioDocumentoPadreStampParamPage">
// 								<input type="hidden" id="filetemplate" name="FILETEMPLATE" value="">
// 								<input type="hidden" name="PRGTEMPLATESTAMPA" value="361">
// 								<input type="hidden" name="PRGDOCUMENTO" value="5687105">
// 								<input type="hidden" name="CDNFUNZIONE" value="347">
// 								<input type="hidden" name="CODSTATOATTO" value="NP">
// 								<input type="hidden" name="GENERASTAMPA" value="STAMPA">
// 								<input type="hidden" name="DOMINIO" value="DL">
// 								<!-- IL TIPO DI EDITOR DA GESTIRE è "L" (LAVORATORE) -->
// 								<input type="hidden" name="TIPOEDITOR" value="L">
								
								
		if(isFirmaGrafometrica.equalsIgnoreCase("true")){						
 			onClick = "apriGestioneDoc('RPT_STAMPA_PARAM','&firmaGrafometrica=OK&ipOperatore=" + ipOperatore +"&cdnLavoratore=" + CDNLAVORATORE + "&prgAzienda=" + PRGAZIENDA + "&prgUnita=" + PRGUNITA + "&codCPI=" + COD_CPI+ "&prgDocumento=" + prgDocumentoIns+ "','"+tipoDocumento+"')";
		}else{

			onClick = "apriGestioneDoc('RPT_STAMPA_PARAM','&cdnLavoratore=" + CDNLAVORATORE + "&prgAzienda=" + PRGAZIENDA + "&prgUnita=" + PRGUNITA + "&codCPI=" + COD_CPI+ "&prgDocumento=" + prgDocumentoIns+ "','"+tipoDocumento+"')";
				
		}
 		if(canGeneraStampa){
 			if(isFirmaGrafometrica.equalsIgnoreCase("true")){	
 		%> 
		
				<input type="button" class="pulsanti" name="btnGeneraStampa" value="Stampa" onClick="controlloAngoliFirma();<%=onClick%>">
				
				<%
				
 			}else{
 				%> 
 				
				<input type="button" class="pulsanti" name="btnGeneraStampa" value="Stampa" onClick="<%=onClick%>">
				
				<%
 			}
				
 		}%>
				
				
				
				
<!-- 				<input type="submit" value="Stampa" class="pulsanti" onClick="settaOperazione('STAMPA');"/> -->
				<input type="submit" id="indietro" value="Indietro" class="pulsanti" onClick="settaOperazione('BACK');"/>
				</td>
			</tr>
			
		</table>
		<%out.print(htmlStreamBottom);%>
	</form>
	</div>
</body>
</html>