package it.eng.sil.coop.bean.blen.dto;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.bean.blen.constant.ServiziConstant;
import it.eng.sil.coop.bean.blen.input.candidatura.AbilitazioniPatenti;
import it.eng.sil.coop.bean.blen.input.candidatura.Candidatura;
import it.eng.sil.coop.bean.blen.input.candidatura.ConoscenzeInformatiche;
import it.eng.sil.coop.bean.blen.input.candidatura.DatiCurriculari;
import it.eng.sil.coop.bean.blen.input.candidatura.DatiSistema;
import it.eng.sil.coop.bean.blen.input.candidatura.EsperienzeLavorative;
import it.eng.sil.coop.bean.blen.input.candidatura.Formazione;
import it.eng.sil.coop.bean.blen.input.candidatura.Istruzione;
import it.eng.sil.coop.bean.blen.input.candidatura.Lingue;
import it.eng.sil.coop.bean.blen.input.candidatura.ProfessioneDesiderataDisponibilita;
import it.eng.sil.coop.bean.blen.input.candidatura.SiNo;
import it.eng.sil.coop.bean.blen.input.candidatura.TipologiaDurata;
import it.eng.sil.coop.utils.UtilityCodifiche;

public class Lavoratore {

	private static final String COMPLETATO = "C";
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RichiestaIDO.class.getName());

	public static void insertValiditaCurriculumIfNeeded(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore)
			throws EMFInternalError {

		boolean isCvValido = isCurriculumValid(transExec, cdnLavoratore);

		if (!isCvValido) {

			BigDecimal maxGiorniValiditaCV = getMaxGiorniValiditaCV(transExec);

			Object[] inputParameters = new Object[4];
			inputParameters[0] = cdnLavoratore;
			inputParameters[1] = maxGiorniValiditaCV;
			inputParameters[2] = ServiziConstant.UTENTE_BLEN;
			inputParameters[3] = ServiziConstant.UTENTE_BLEN;

			executeInsert(transExec, "BLEN_INSERT_VALIDITA_CV", inputParameters);

		}

	}

	public static String getCpiCompetenzaLavoratore(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore)
			throws EMFInternalError {

		String codCpi = null;

		Object[] inputParameters = new Object[1];
		inputParameters[0] = cdnLavoratore;

		SourceBean sb = (SourceBean) transExec.executeQuery("BLEN_SELECT_CPI_COMPETENZA_LAV", inputParameters,
				"SELECT");
		if (sb != null && sb.containsAttribute("ROW")) {
			codCpi = (String) sb.getAttribute("ROW.codcpi");
		}
		return codCpi;
	}

	private static BigDecimal getMaxGiorniValiditaCV(TransactionQueryExecutor transExec) throws EMFInternalError {
		BigDecimal giorni = null;
		SourceBean sb = (SourceBean) transExec.executeQuery("GET_MaxNumGGValCurr", null, "SELECT");
		if (sb.containsAttribute("ROW")) {
			giorni = (BigDecimal) sb.getAttribute("ROW.num");
		}
		return giorni;
	}

	private static boolean isCurriculumValid(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore)
			throws EMFInternalError {

		Object[] inputParameters = new Object[1];
		inputParameters[0] = cdnLavoratore;

		BigDecimal esiste = null;
		SourceBean sb = (SourceBean) transExec.executeQuery("CL_ESISTE_CV_VALIDO", inputParameters, "SELECT");

		if (sb != null && sb.containsAttribute("ROW")) {
			esiste = (BigDecimal) sb.getAttribute("ROW.ESISTE");
			if (esiste != null && esiste.compareTo(new BigDecimal(1)) == 0) {
				return true;
			}
		}

		return false;

	}

	public static BigDecimal insertLavoratore(TransactionQueryExecutor transExec, Candidatura candidaturaInput)
			throws EMFInternalError {

		BigDecimal newCdnLavoratore = getNewCdnLavoratore(transExec);

		insertAnLavortatore(transExec, candidaturaInput, newCdnLavoratore);

		updateAnLavStoriaInf(transExec, candidaturaInput, newCdnLavoratore);

		DatiCurriculari datiCurriculari = candidaturaInput.getDatiCurriculari();
		if (datiCurriculari != null) {

			insertProfessioneDesiderataDisponibilita(transExec, newCdnLavoratore,
					datiCurriculari.getProfessioneDesiderataDisponibilita());

			insertEsperienzeLavorative(transExec, newCdnLavoratore, datiCurriculari.getEsperienzeLavorative());

			insertIstruzione(transExec, newCdnLavoratore, datiCurriculari.getIstruzione());

			insertFormazione(transExec, newCdnLavoratore, datiCurriculari.getFormazione());

			insertLingue(transExec, newCdnLavoratore, datiCurriculari.getLingue());

			insertConoscenzeInformatiche(transExec, newCdnLavoratore, datiCurriculari.getConoscenzeInformatiche());

			insertAbilitazioniPatenti(transExec, newCdnLavoratore, datiCurriculari.getAbilitazioniPatenti());

		}

		insertDatiSistema(transExec, newCdnLavoratore, candidaturaInput.getDatiSistema());

		return newCdnLavoratore;

	}

	private static void insertProfessioneDesiderataDisponibilita(TransactionQueryExecutor transExec,
			BigDecimal cdnLavoratore, List<ProfessioneDesiderataDisponibilita> professioneDesiderataDisponibilita)
			throws EMFInternalError {
		if (professioneDesiderataDisponibilita != null) {
			for (ProfessioneDesiderataDisponibilita professioneDD : professioneDesiderataDisponibilita) {
				insertProfessioneDesiderataDisponibilita(transExec, cdnLavoratore, professioneDD);
			}
		}
	}

	private static void insertProfessioneDesiderataDisponibilita(TransactionQueryExecutor transExec,
			BigDecimal cdnLavoratore, ProfessioneDesiderataDisponibilita professioneDD) throws EMFInternalError {
		BigDecimal prgMansione = insertMansione(transExec, cdnLavoratore, professioneDD);
		if (prgMansione != null) {
			insertOrari(transExec, prgMansione, professioneDD.getIdmodalitalavorativa());
		}
	}

	private static void insertOrari(TransactionQueryExecutor transExec, BigDecimal prgMansione, List<String> listOrari)
			throws EMFInternalError {

		if (prgMansione == null) {
			return;
		}

		for (Iterator iterator = listOrari.iterator(); iterator.hasNext();) {
			String orarioMin = (String) iterator.next();
			insertOrario(transExec, prgMansione, orarioMin);
		}

	}

	private static void insertOrario(TransactionQueryExecutor transExec, BigDecimal prgMansione, String orarioMin)
			throws EMFInternalError {
		Object[] inputParameters = new Object[4];

		String codOrario = UtilityCodifiche.getOrarioSil(orarioMin);

		if (codOrario == null) {
			_logger.warn("codice orario non trovato, si scarta");
			return;
		}

		inputParameters[0] = prgMansione; // PRGMANSIONE,
		inputParameters[1] = codOrario; // CODORARIO,
		inputParameters[2] = ServiziConstant.UTENTE_BLEN;
		; // CDNUTINS,
		inputParameters[3] = ServiziConstant.UTENTE_BLEN;
		; // CDNUTMOD,

		executeInsert(transExec, "INSERT_ORARIO_IN_MANSIONE", inputParameters);
	}

	private static boolean isMansioneAlreadyInserted(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore,
			String codMansione) throws EMFInternalError {

		Object[] inputParameters = new Object[2];
		inputParameters[0] = codMansione;
		inputParameters[1] = cdnLavoratore;

		BigDecimal esiste = null;
		SourceBean sb = (SourceBean) transExec.executeQuery("BLEN_EXISTS_MANSIONE_LAV", inputParameters, "SELECT");

		if (sb != null && sb.containsAttribute("ROW")) {
			esiste = (BigDecimal) sb.getAttribute("ROW.ESISTE");
			if (esiste != null && esiste.compareTo(new BigDecimal(1)) == 0) {
				return true;
			}
		}

		return false;

	}

	private static BigDecimal insertMansione(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore,
			ProfessioneDesiderataDisponibilita professioneDD) throws EMFInternalError {
		Object[] inputParameters = new Object[12];

		String codMansione = UtilityCodifiche.getMansioneSil(transExec, professioneDD.getIdprofessione());

		if (isMansioneAlreadyInserted(transExec, cdnLavoratore, codMansione)) {
			return null;
		}

		if (codMansione == null) {
			return null;
		}

		BigDecimal newPrgMansione = getNewPrgMansione(transExec);
		inputParameters[0] = newPrgMansione;// PRGMANSIONE
		inputParameters[1] = cdnLavoratore;// CDNLAVORATORE
		inputParameters[2] = codMansione;// CODMANSIONE
		SiNo esp = professioneDD.getEsperienzasettore();
		inputParameters[3] = (esp != null && esp.compareTo(SiNo.SI) == 0 ? "S"
				: esp != null && esp.compareTo(SiNo.NO) == 0 ? "N" : null);// FLGESPERIENZA
		inputParameters[4] = "S"; // FLGDISPONIBILE
		inputParameters[5] = null;// FLGDISPFORMAZIONE"
		inputParameters[6] = null;// FLGESPFORM"
		inputParameters[7] = null;// FLGPIP"
		inputParameters[8] = null;// CODMONOTEMPO
		inputParameters[9] = ServiziConstant.UTENTE_BLEN; // _CDUT_"
		inputParameters[10] = ServiziConstant.UTENTE_BLEN; // _CDUT_"
		inputParameters[11] = professioneDD.getDescrizioneprofessione();// STRNOTE

		executeInsert(transExec, "INSERT_MANSIONE", inputParameters);

		return newPrgMansione;
	}

	private static void insertAbilitazioniPatenti(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore,
			AbilitazioniPatenti abilitazioniPatenti) throws EMFInternalError {
		if (abilitazioniPatenti != null) {
			for (String patente : abilitazioniPatenti.getIdpatenteguida()) {
				insertAbilitazione(transExec, cdnLavoratore, patente);
			}

			for (String patentino : abilitazioniPatenti.getIdpatentino()) {
				insertAbilitazione(transExec, cdnLavoratore, patentino);
			}

			for (String albo : abilitazioniPatenti.getIdalbo()) {
				insertAbilitazione(transExec, cdnLavoratore, albo);
			}
		}
	}

	private static void insertAbilitazione(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore,
			String abilitaz) throws EMFInternalError {

		String codAbilitazioneSil = null;

		if (abilitaz != null) {

			// se non si trova il codice corrispondente nel sil non si procede con l'insert
			codAbilitazioneSil = UtilityCodifiche.getAbilitazioneSil(transExec, abilitaz);
			if (codAbilitazioneSil == null || "".equals(codAbilitazioneSil)) {
				_logger.warn("Errore nel recupero del cod abilitazione sil");
				return;
			}

			Object[] inputParameters = new Object[5];

			inputParameters[0] = cdnLavoratore;// cdnLavoratore
			inputParameters[1] = codAbilitazioneSil;// tipoAbilitazione
			inputParameters[2] = null;// strNote
			inputParameters[3] = ServiziConstant.UTENTE_BLEN;// _CDUT_
			inputParameters[4] = ServiziConstant.UTENTE_BLEN;// _CDUT_

			executeInsert(transExec, "INSERT_PR_ABILITAZIONE", inputParameters);

		}
	}

	private static void insertConoscenzeInformatiche(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore,
			ConoscenzeInformatiche conoscenzeInformatiche) throws EMFInternalError {

		if (conoscenzeInformatiche != null) {

			String[] conoscenzaInformatica = UtilityCodifiche.getConoscenzaInformatica(transExec,
					conoscenzeInformatiche.getTipoconoscenza());

			if (conoscenzaInformatica != null) {

				String codTipoInfo = conoscenzaInformatica[0];
				String codDettInfo = conoscenzaInformatica[1];

				Object[] inputParameters = new Object[10];

				inputParameters[0] = cdnLavoratore;// CDNLAVORATORE
				inputParameters[1] = codTipoInfo; // CODTIPOINFO
				inputParameters[2] = codDettInfo; // CODDETTINFO
				inputParameters[3] = null;// STRDESCINFO
				inputParameters[4] = ServiziConstant.CONOSCENZA_INFORMATICA_CODIFICA_INESISTENTE;// CDNGRADO
				inputParameters[5] = null;// CODMODOINFO
				inputParameters[6] = null;// STRMODINFO
				inputParameters[7] = null;// FLGCERTIFICATO
				inputParameters[8] = ServiziConstant.UTENTE_BLEN;// CDNUTINS
				inputParameters[9] = ServiziConstant.UTENTE_BLEN;// CDNUTMOD

				executeInsert(transExec, "INSERT_CONOSCENZA_INFO", inputParameters);
			}
		}
	}

	private static void insertLingue(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore, List<Lingue> lingue)
			throws EMFInternalError {
		if (lingue != null) {
			for (Lingue lingua : lingue) {
				insertLingua(transExec, cdnLavoratore, lingua);
			}
		}
	}

	private static boolean isLinguaAlreadyInserted(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore,
			String codLingua) throws EMFInternalError {

		Object[] inputParameters = new Object[2];
		inputParameters[0] = codLingua;
		inputParameters[1] = cdnLavoratore;

		BigDecimal esiste = null;
		SourceBean sb = (SourceBean) transExec.executeQuery("BLEN_EXISTS_LINGUA", inputParameters, "SELECT");

		if (sb != null && sb.containsAttribute("ROW")) {
			esiste = (BigDecimal) sb.getAttribute("ROW.ESISTE");
			if (esiste != null && esiste.compareTo(new BigDecimal(1)) == 0) {
				return true;
			}
		}

		return false;

	}

	private static void insertLingua(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore, Lingue lingua)
			throws EMFInternalError {

		if (isLinguaAlreadyInserted(transExec, cdnLavoratore, lingua.getIdlingua())) {
			return;
		}

		BigDecimal cdnGradoLetto = UtilityCodifiche.getLivelloConoscenzaLinSil(transExec, lingua.getIdlivelloletto());
		BigDecimal cdnGradoScritto = UtilityCodifiche.getLivelloConoscenzaLinSil(transExec,
				lingua.getIdlivelloscritto());
		BigDecimal cdnGradoParlato = UtilityCodifiche.getLivelloConoscenzaLinSil(transExec,
				lingua.getIdlivelloparlato());

		if (cdnGradoLetto == null || cdnGradoScritto == null || cdnGradoParlato == null) {
			_logger.warn(
					"Attenzione: codiceLivelloLetto (" + cdnGradoLetto + ") o codiceLivelloScritto (" + cdnGradoScritto
							+ ") o codiceLivelloParlato (" + cdnGradoParlato + ") non trovato, si scarta la lingua.");
			return;
		}

		Object[] inputParameters = new Object[11];

		inputParameters[0] = lingua.getIdlingua();// CODLINGUA
		inputParameters[1] = cdnLavoratore;// CDNLAVORATORE
		inputParameters[2] = cdnGradoLetto; // CDNGRADOLETTO
		inputParameters[3] = cdnGradoScritto; // CDNGRADOSCRITTO
		inputParameters[4] = cdnGradoParlato; // CDNGRADOPARLATO
		inputParameters[5] = null;// CODMODLINGUA
		inputParameters[6] = null;// STRMODLINGUA
		inputParameters[7] = null;// FLGCERTIFICATO
		inputParameters[8] = null;// FLGPRIMALINGUA
		inputParameters[9] = ServiziConstant.UTENTE_BLEN;// CDNUTINS
		inputParameters[10] = ServiziConstant.UTENTE_BLEN;// CDNUTMOD

		executeInsert(transExec, "INSERT_CONOSCENZA_LING", inputParameters);

	}

	private static void insertIstruzione(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore,
			List<Istruzione> istruzione) throws EMFInternalError {
		if (istruzione != null) {
			for (Istruzione istr : istruzione) {
				insertIstruzione(transExec, cdnLavoratore, istr);
			}
		}

	}

	private static boolean isTitoloStudioAlreadyInserted(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore,
			String codTitolo) throws EMFInternalError {

		Object[] inputParameters = new Object[2];
		inputParameters[0] = codTitolo;
		inputParameters[1] = cdnLavoratore;

		BigDecimal esiste = null;
		SourceBean sb = (SourceBean) transExec.executeQuery("BLEN_EXISTS_TITOLO_STUDIO", inputParameters, "SELECT");

		if (sb != null && sb.containsAttribute("ROW")) {
			esiste = (BigDecimal) sb.getAttribute("ROW.ESISTE");
			if (esiste != null && esiste.compareTo(new BigDecimal(1)) == 0) {
				return true;
			}
		}

		return false;

	}

	private static void insertIstruzione(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore, Istruzione istr)
			throws EMFInternalError {

		String titoloStudioSil = UtilityCodifiche.getTitoloStudioSil(transExec, istr.getTitolostudio());

		if (titoloStudioSil == null) {
			_logger.warn("Errore nel recupero del titolo studio");
			return;
		}

		// controlla se il titolo di studio è già stato inserito,
		// un indice su db non permette due entry con stesso titolo
		if (isTitoloStudioAlreadyInserted(transExec, cdnLavoratore, titoloStudioSil)) {
			_logger.warn("Titolo studio già inserito");
			return;
		}

		Object[] inputParameters = new Object[23];

		inputParameters[0] = getNewPrgStudio(transExec);// prgstudio
		inputParameters[1] = cdnLavoratore;// cdnLavoratore
		inputParameters[2] = titoloStudioSil;// codTipoTitolo
		inputParameters[3] = titoloStudioSil;// codTitolo
		inputParameters[4] = istr.getDescrizioneistruzione();// strSpecifica
		inputParameters[5] = null;// flgPrincipale
		inputParameters[6] = null;// codMonoStatoTit
		inputParameters[7] = null;// strIstScolastico
		inputParameters[8] = null;// strIndirizzo
		inputParameters[9] = null;// strLocalita
		inputParameters[10] = null;// codCom
		inputParameters[11] = COMPLETATO;// codMonoStato
		inputParameters[12] = null;// numAnniFreq
		inputParameters[13] = null;// numAnniPrev
		inputParameters[14] = null;// strMotAbbandono
		inputParameters[15] = null;// numAnno
		inputParameters[16] = UtilityCodifiche.getVoto(istr.getVotazione());// strVoto
		inputParameters[17] = UtilityCodifiche.getEsimi(istr.getVotazione());// strEsimi
		inputParameters[18] = null;// strTitTesi
		inputParameters[19] = null;// strArgTesi
		inputParameters[20] = null;// flgLode
		inputParameters[21] = ServiziConstant.UTENTE_BLEN;// CDUT_
		inputParameters[22] = ServiziConstant.UTENTE_BLEN;// _CDUT_

		executeInsert(transExec, "INSERT_PR_STUDIO", inputParameters);
	}

	private static void updateAnLavStoriaInf(TransactionQueryExecutor transExec, Candidatura candidaturaInput,
			BigDecimal newCdnLavoratore) throws EMFInternalError {

		String codCpiVCompetenza = UtilityCodifiche.getCPIFromComune(transExec,
				candidaturaInput.getLavoratore().getDomicilio().getIdcomune());

		Object[] inputParameters = new Object[3];
		inputParameters[0] = codCpiVCompetenza;
		inputParameters[1] = codCpiVCompetenza;
		inputParameters[2] = newCdnLavoratore;

		executeInsert(transExec, "BLEN_UPDATE_CPI_COMPETENZA", inputParameters);

	}

	private static void insertAnLavortatore(TransactionQueryExecutor transExec, Candidatura candidaturaInput,
			BigDecimal newCdnLavoratore) throws EMFInternalError {
		Object[] inputParameters = new Object[30];
		inputParameters[0] = newCdnLavoratore; // cdnLavoratore;
		inputParameters[1] = candidaturaInput.getLavoratore().getDatiAnagrafici().getCodicefiscale(); // strCodiceFiscale;
		inputParameters[2] = candidaturaInput.getLavoratore().getDatiAnagrafici().getCognome(); // strCognome;
		inputParameters[3] = candidaturaInput.getLavoratore().getDatiAnagrafici().getNome(); // strNome;
		inputParameters[4] = candidaturaInput.getLavoratore().getDatiAnagrafici().getSesso(); // strSesso;
		inputParameters[5] = getDataNascita(candidaturaInput); // datNasc;
		inputParameters[6] = candidaturaInput.getLavoratore().getDatiAnagrafici().getIdcomune(); // codComNas;
		inputParameters[7] = candidaturaInput.getLavoratore().getDatiAnagrafici().getIdcittadinanza(); // codCittadinanzaHid;
		inputParameters[8] = null; // codCittadinanza2Hid;
		inputParameters[9] = null; // codstatoCivile;
		inputParameters[10] = null; // flgMilite;
		inputParameters[11] = null; // numFigli;
		inputParameters[12] = null; // strNote;
		inputParameters[13] = null; // FLGCFOK;
		inputParameters[14] = ServiziConstant.UTENTE_BLEN; // userid;
		inputParameters[15] = ServiziConstant.UTENTE_BLEN; // userid;
		inputParameters[16] = candidaturaInput.getLavoratore().getDomicilio().getIdcomune(); // codComRes (reso uguale a
																								// codComdom);
		inputParameters[17] = candidaturaInput.getLavoratore().getRecapiti().getIndirizzo(); // strIndirizzores (reso
																								// uguale a
																								// strIndirizzodom);
		inputParameters[18] = null; // strLocalitares;
		inputParameters[19] = candidaturaInput.getLavoratore().getDomicilio().getCap(); // strCapRes;
		inputParameters[20] = candidaturaInput.getLavoratore().getDomicilio().getIdcomune(); // codComdom;
		inputParameters[21] = candidaturaInput.getLavoratore().getRecapiti().getIndirizzo(); // strIndirizzodom;
		inputParameters[22] = null; // strLocalitadom;
		inputParameters[23] = candidaturaInput.getLavoratore().getDomicilio().getCap(); // strCapDom;
		inputParameters[24] = null; // strTelRes;
		inputParameters[25] = candidaturaInput.getLavoratore().getRecapiti().getTelefono(); // strTelDom;
		inputParameters[26] = null; // strTelAltro;
		inputParameters[27] = candidaturaInput.getLavoratore().getRecapiti().getCellulare(); // strCell;
		inputParameters[28] = candidaturaInput.getLavoratore().getRecapiti().getEmail(); // strEmail;
		inputParameters[29] = candidaturaInput.getLavoratore().getRecapiti().getFax(); // strFax;

		executeInsert(transExec, "INSERT_AN_LAVORATOREANAGIND", inputParameters);
	}

	private static void insertFormazione(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore,
			List<Formazione> formazione) throws EMFInternalError {
		if (formazione != null) {
			for (Formazione formaz : formazione) {
				insertFormazione(transExec, cdnLavoratore, formaz);
			}
		}

	}

	private static void insertFormazione(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore,
			Formazione formaz) throws EMFInternalError {

		// si ricerca tra i corsi presenti a sistema per vedere se presente quello indicato
		// se non lo si trova si inserisce solo la descrizione, può essere null!
		String codCorso = UtilityCodifiche.getCodCorso(transExec, formaz.getTitolocorso());

		it.eng.sil.coop.bean.blen.input.candidatura.Attestazionecheck idAttestazioneEnum = formaz.getIdattestazione();
		String idAttestazione = (idAttestazioneEnum != null) ? idAttestazioneEnum.value() : null;

		Object[] inputParameters = new Object[31];

		inputParameters[0] = getNewPrgCorso(transExec);// PRGCORSO
		inputParameters[1] = cdnLavoratore;// CDNLAVORATORE
		inputParameters[2] = codCorso;// CODCORSO
		inputParameters[3] = (codCorso == null) ? formaz.getTitolocorso() : null; // STRDESCRIZIONE
		inputParameters[4] = null;// STRCONTENUTO
		inputParameters[5] = null;// STRENTE
		inputParameters[6] = formaz.getIdsede();// CODCOMENTEHid
		inputParameters[7] = null;// STRLOCALITAENTE
		inputParameters[8] = null;// STRINDIRIZZOENTE
		inputParameters[9] = null;// NUMANNO
		inputParameters[10] = null;// NUMMESI
		inputParameters[11] = null;// NUMORE
		if (formaz.getIdtipologiadurata() != null) {
			if (formaz.getIdtipologiadurata().compareTo(TipologiaDurata.M) == 0) {
				inputParameters[10] = formaz.getDurata();// NUMMESI
			} else if (formaz.getIdtipologiadurata().compareTo(TipologiaDurata.O) == 0) {
				inputParameters[11] = formaz.getDurata();// NUMORE
			} else if (formaz.getIdtipologiadurata().compareTo(TipologiaDurata.A) == 0) {
				inputParameters[10] = Integer.parseInt(formaz.getDurata()) * 12;// NUMMESI
			} else if (formaz.getIdtipologiadurata().compareTo(TipologiaDurata.G) == 0) {
				inputParameters[11] = Integer.parseInt(formaz.getDurata()) * 8;// NUMORE
			}
		}
		inputParameters[12] = null;// NUMORESPESE
		inputParameters[13] = null;// FLGCOMPLETATO
		inputParameters[14] = null;// STRMOTCESSAZIONE
		inputParameters[15] = UtilityCodifiche.getCodTipoCertificato(transExec, idAttestazione);// CODTIPOCERTIFICATO
		inputParameters[16] = null;// CDNAMBITODISCIPLINARE
		inputParameters[17] = null;// FLGSTAGE
		inputParameters[18] = null;// NUMORESTAGE
		inputParameters[19] = null;// STRAZIENDA
		inputParameters[20] = null;// CODCOMAZIENDAHid
		inputParameters[21] = null;// STRLOCALITAAZIENDA
		inputParameters[22] = null;// STRINDIRIZZOAZIENDA
		inputParameters[23] = null;// STRNOTACORSO
		inputParameters[24] = ServiziConstant.UTENTE_BLEN;// CDNUTINS
		inputParameters[25] = ServiziConstant.UTENTE_BLEN;// CDNUTMOD
		inputParameters[26] = null;
		inputParameters[27] = null;
		inputParameters[28] = null;
		inputParameters[29] = null;
		inputParameters[30] = null;

		executeInsert(transExec, "INSERT_FOR_PRO", inputParameters);
	}

	private static void insertEsperienzeLavorative(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore,
			List<EsperienzeLavorative> esperienzeLavorative) throws EMFInternalError {
		if (esperienzeLavorative != null) {
			for (EsperienzeLavorative esperienza : esperienzeLavorative) {
				insertEsperienza(transExec, cdnLavoratore, esperienza);
			}
		}
	}

	private static void insertEsperienza(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore,
			EsperienzeLavorative esperienza) throws EMFInternalError {

		Object[] inputParameters = new Object[32];

		/*
		 * 0 prgesplavoro, 1 prgmansione, 2 codcontratto, 3 codrapportolav, 4 codateco, 5 codccnl, 6 strriflegge, 7
		 * strdesattivita, 8 strlivello, 9 codarea, 10 nummeseinizio, 11 numannoinizio, 12 nummesefine, 13 numannofine,
		 * 14 nummesi, 15 numore, 16 numoresett, 17 codorario, 18 strcodfiscaleazienda, 19 strpartitaivaazienda,
		 * strragsocialeazienda, codcomazienda, strindirizzoazienda, codnatgiuridica, strtipoclienti, numstipendio,
		 * flgcompletato, codmvcessazione, strmotcessazione, codtipocertificato, cdnutins, dtmins, cdnutmod, dtmmod
		 */

		String codmansione = UtilityCodifiche.getMansioneSil(transExec, esperienza.getQualificasvolta());

		BigDecimal prgMansione = UtilityCodifiche.getPrgMansioneLavoratore(transExec, cdnLavoratore, codmansione);

		if (prgMansione == null) {
			// esperienza non collegabile a nessuna mansione desiderata, la si ignora
			return;
		}

		inputParameters[0] = getNewPrgEspLavoro(transExec);// PRGESPLAVORO
		inputParameters[1] = prgMansione; // PRGMANSIONE
		inputParameters[2] = UtilityCodifiche.getCodiceContratto(esperienza.getTipoesperienza()); // CODCONTRATTO
		inputParameters[3] = esperienza.getTipoesperienza(); // CODRAPPORTOLAV A00 DA VERIFICARE, MA IN TEORIA NON c'E
																// BISOGNO DI TRASCODIFICA.
		inputParameters[4] = null;// CODATECO
		inputParameters[5] = null;// CODCCNL
		inputParameters[6] = null;// STRRIFLEGGE
		inputParameters[7] = esperienza.getPrincipalimansioni();// STRDESATTIVITA
		inputParameters[8] = null;// STRLIVELLO
		inputParameters[9] = null;// CODAREA
		if (esperienza.getDatainizio() != null) {
			inputParameters[10] = esperienza.getDatainizio().getMonth();// NUMMESEINIZIO
			inputParameters[11] = esperienza.getDatainizio().getYear();// NUMANNOINIZIO
		} else {
			// anno inizio è obbligatorio, lo si ricava dalla data attuale assieme al mese
			Date dataOdierna = new Date();
			DateFormat meseDF = new SimpleDateFormat("MM");
			DateFormat annoDF = new SimpleDateFormat("yyyy");
			inputParameters[10] = new BigDecimal(meseDF.format(dataOdierna)); // NUMMESEINIZIO
			inputParameters[11] = new BigDecimal(annoDF.format(dataOdierna)); // NUMANNOINIZIO
		}

		if (esperienza.getDatafine() != null) {
			inputParameters[12] = esperienza.getDatafine().getMonth();// NUMMESEFINE
			inputParameters[13] = esperienza.getDatafine().getYear();// NUMANNOFINE
		} else {
			inputParameters[12] = null;// NUMMESEFINE
			inputParameters[13] = null;// NUMANNOFINE
		}
		inputParameters[14] = null;// NUMMESI
		inputParameters[15] = null;// NUMORE
		inputParameters[16] = null;// NUMORESETT
		inputParameters[17] = null;// CODORARIO
		inputParameters[18] = null;// STRCODFISCALEAZIENDA
		inputParameters[19] = null;// STRPARTITAIVAAZIENDA
		inputParameters[20] = esperienza.getNomedatore();// STRRAGSOCIALEAZIENDA
		inputParameters[21] = null;// CODCOMAZIENDA
		inputParameters[22] = esperienza.getIndirizzodatore();// STRINDIRIZZOAZIENDA
		inputParameters[23] = null;// CODNATGIURIDICA
		inputParameters[24] = null;// STRTIPOCLIENTI
		inputParameters[25] = null;// NUMSTIPENDIO
		inputParameters[26] = null;// FLGCOMPLETATO
		inputParameters[27] = null;// CODMVCESSAZIONE
		inputParameters[28] = null;// STRMOTCESSAZIONE
		inputParameters[29] = null;// CODTIPOCERTIFICATO
		inputParameters[30] = ServiziConstant.UTENTE_BLEN;// _CDUT_
		inputParameters[31] = ServiziConstant.UTENTE_BLEN;// _CDUT_

		executeInsert(transExec, "INSERT_ESP_LAVORO", inputParameters);
	}

	private static void insertDatiSistema(TransactionQueryExecutor transExec, BigDecimal newCdnLavoratore,
			DatiSistema datiSistema) {

	}

	public static String getDataNascita(Candidatura candidaturaInput) {
		XMLGregorianCalendar datanascita = candidaturaInput.getLavoratore().getDatiAnagrafici().getDatanascita();
		GregorianCalendar data = new GregorianCalendar(datanascita.getYear(), datanascita.getMonth() - 1,
				datanascita.getDay());
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String formattedDate = df.format(new Date(data.getTimeInMillis()));
		return formattedDate;
	}

	private static BigDecimal getNewCdnLavoratore(TransactionQueryExecutor transExec) throws EMFInternalError {
		BigDecimal cdnlavoratore = null;
		SourceBean aziendaSB = (SourceBean) transExec.executeQuery("GET_AN_NEW_LAVORATORE", null, "SELECT");
		if (aziendaSB.containsAttribute("ROW")) {
			cdnlavoratore = (BigDecimal) aziendaSB.getAttribute("ROW.CDNLAVORATORE");
		}
		return cdnlavoratore;
	}

	private static BigDecimal getNewPrgStudio(TransactionQueryExecutor transExec) throws EMFInternalError {
		SourceBean res = (SourceBean) transExec.executeQuery("GET_NEW_TITOLO", null, "SELECT");
		BigDecimal prgStudio = (BigDecimal) res.getAttribute("ROW.PRGSTUDIO");
		return prgStudio;
	}

	private static BigDecimal getNewPrgCorso(TransactionQueryExecutor transExec) throws EMFInternalError {
		SourceBean res = (SourceBean) transExec.executeQuery("FORPRO_NEXTVAL", null, "SELECT");
		BigDecimal prgCorso = (BigDecimal) res.getAttribute("ROW.DO_NEXTVAL");
		return prgCorso;
	}

	private static BigDecimal getNewPrgMansione(TransactionQueryExecutor transExec) throws EMFInternalError {
		SourceBean res = (SourceBean) transExec.executeQuery("SELECT_MANSIONE_SEQUENCE", null, "SELECT");
		BigDecimal prgMansione = (BigDecimal) res.getAttribute("ROW.PRGMANSIONE");
		return prgMansione;
	}

	private static BigDecimal getNewPrgEspLavoro(TransactionQueryExecutor transExec) throws EMFInternalError {
		SourceBean res = (SourceBean) transExec.executeQuery("GETSEQUENCE_ESPLAV", null, "SELECT");
		BigDecimal prgEspLavoro = (BigDecimal) res.getAttribute("ROW.PRGESPLAVORO");
		return prgEspLavoro;
	}

	private static boolean isInsertFailed(Object objRes) {
		return objRes == null || !(objRes instanceof Boolean && ((Boolean) objRes).booleanValue() == true);
	}

	private static void executeInsert(TransactionQueryExecutor transExec, String statementName,
			Object[] inputParameters) throws EMFInternalError {
		Object objRes = transExec.executeQuery(statementName, inputParameters, "INSERT");

		if (isInsertFailed(objRes)) {
			_logger.error("Inserimento fallito: " + statementName);
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Inserimento fallito: " + statementName);
		}
	}

}
