package it.eng.sil.coop.services;

import java.io.File;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.myblen.ws.RicezioneEsitoMatchSILProxy;
import it.eng.sil.Values;
import it.eng.sil.coop.CoopApplicationException_Lavoratore;
import it.eng.sil.coop.bean.blen.CandidaturaEstesa;
import it.eng.sil.coop.bean.blen.Match;
import it.eng.sil.coop.bean.blen.Risultato;
import it.eng.sil.coop.bean.blen.constant.ServiziConstant;
import it.eng.sil.coop.bean.blen.dto.DatiAzienda;
import it.eng.sil.coop.bean.blen.dto.EsitoMatching;
import it.eng.sil.coop.bean.blen.dto.Lavoratore;
import it.eng.sil.coop.bean.blen.dto.RichiestaIDO;
import it.eng.sil.coop.bean.blen.dto.Rosa;
import it.eng.sil.coop.bean.blen.dto.RosaInsert;
import it.eng.sil.coop.bean.blen.input.candidatura.Candidatura;
import it.eng.sil.coop.bean.blen.input.ricerca.AltreInformazioni;
import it.eng.sil.coop.bean.blen.input.ricerca.Vacancy;
import it.eng.sil.junit.BaseTestCase;
import it.eng.sil.util.blen.xml.XMLValidator;

public class MatchingBlen implements IFaceService {

	protected static final String XSD_PATH = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "myportal" + File.separator;

	protected static final File inputCandidaturaXsd = new File(XSD_PATH + "clic_lavoro_candidatura_038_blen.xsd");
	protected static final File inputRicercaPersonaleXsd = new File(XSD_PATH + "clic_lavoro_ricerca_personale_064.xsd");

	public class MatchingBlenError extends EMFInternalError {
		private static final long serialVersionUID = 1L;
		private Risultato risultato;

		public MatchingBlenError(EMFInternalError internalError, Risultato risultato) {
			super(internalError);
			this.risultato = risultato;
		}

		protected Risultato getRisultato() {
			return this.risultato;
		}
	}

	public static Risultato _101 = new Risultato("101", "DATO PRESO IN CARICO DAL SISTEMA");
	public static Risultato _901 = new Risultato("901", "PARAMETRI OBBLIGATORI MANCANTI");
	public static Risultato _902 = new Risultato("902", "PARAMETRI NON RICONOSCIUTI");
	public static Risultato _904 = new Risultato("904", "DECRIPTAZIONE DATI NON RIUSCITA");
	public static Risultato _912 = new Risultato("912",
			"VALIDAZIONE XML: SPAZIO DEI NOMI NON RICONOSCIUTO O NON VALIDO");
	public static Risultato _913 = new Risultato("913", "VALIDAZIONE XML: NON SUPERATA (XSD)");
	public static Risultato _951 = new Risultato("951", "ERRORE DB: CONNESSIONE NON RIUSCITA");
	public static Risultato _952 = new Risultato("952", "ERRORE DB: SALVATAGGIO DEL DATO NON RIUSCITO");
	public static Risultato _999 = new Risultato("999", "ERRORE GENERICO");

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MatchingBlen.class.getName());

	public void insertCandidatura(TransactionQueryExecutor transExec, String inputXML, String strDataRichiesta,
			BigDecimal prgRichiestaAz, String prgRosa, String prgIncrocio) throws EMFInternalError {

		/////////////////////////////////
		// VALIDAZIONE XML CANDIDATURA //
		/////////////////////////////////

		if (!isXmlValid(inputXML, inputCandidaturaXsd)) {
			_logger.error("Errore validazione xml Candidatura");
			throw new MatchingBlenError(
					new EMFInternalError(EMFErrorSeverity.ERROR, "Errore validazione xml Candidatura"), _913);
		}

		//////////////////////////////////
		// PROCESSING DELLA CANDIDATURA //
		//////////////////////////////////

		JAXBContext jaxbContext;
		Candidatura candidaturaInput = null;
		try {
			jaxbContext = JAXBContext.newInstance(Candidatura.class);
			candidaturaInput = (Candidatura) jaxbContext.createUnmarshaller().unmarshal(new StringReader(inputXML));
			doInsert(transExec, candidaturaInput, prgRichiestaAz, prgRosa, prgIncrocio);
		} catch (JAXBException ex) {
			_logger.error("Errore validazione xml candidatura", ex);
			throw new MatchingBlenError(new EMFInternalError(EMFErrorSeverity.ERROR,
					"Errore validazione xml candidatura: " + ex.getMessage()), _913);
		}
	}

	private void doInsert(TransactionQueryExecutor transExec, Candidatura candidaturaInput, BigDecimal prgRichiestaAz,
			String prgRosa, String prgIncrocio) throws EMFInternalError {
		String codiceFiscale = candidaturaInput.getLavoratore().getDatiAnagrafici().getCodicefiscale();

		// Ricerca Lavoratore dato codice fiscale
		BigDecimal cdnLavoratore = getCdnLavoratore(transExec, codiceFiscale);
		if (cdnLavoratore == null) {
			// Controllo dati CF se c'è errore lancia un'eccezione di tipo
			// CfException
			try {
				CF_utils.verificaCF(codiceFiscale, candidaturaInput.getLavoratore().getDatiAnagrafici().getNome(),
						candidaturaInput.getLavoratore().getDatiAnagrafici().getCognome(),
						candidaturaInput.getLavoratore().getDatiAnagrafici().getSesso().toString(),
						Lavoratore.getDataNascita(candidaturaInput),
						candidaturaInput.getLavoratore().getDatiAnagrafici().getIdcomune());
			} catch (CfException e) {
				_logger.error("Errore nella verifica del Codice Fiscale", e);
				throw new EMFInternalError(EMFErrorSeverity.ERROR,
						"Errore nella verifica del Codice Fiscale: " + e.getMessage());
			}

			// se il lavoratore non è presente bisogna inserirlo
			cdnLavoratore = Lavoratore.insertLavoratore(transExec, candidaturaInput);
		}

		Lavoratore.insertValiditaCurriculumIfNeeded(transExec, cdnLavoratore);

		// candidaturaInput.getDatiSistema().getIdintermediario()
		// String codCpiCompLav = Lavoratore.getCpiCompetenzaLavoratore(transExec, cdnLavoratore);

		Rosa.insertLavARosaNominativa(transExec, cdnLavoratore, prgRichiestaAz, prgIncrocio, prgRosa, null);

	}

	public BigDecimal insertRicercaPersonale(TransactionQueryExecutor transExec, String inputXML,
			String strDataRichiesta) throws EMFInternalError {

		/////////////////////////////
		// VALIDAZIONE XML VACANCY //
		/////////////////////////////

		if (!isXmlValid(inputXML, inputRicercaPersonaleXsd)) {
			_logger.error("Errore validazione xml Vacancy");
			throw new MatchingBlenError(new EMFInternalError(EMFErrorSeverity.ERROR, "Errore validazione xml Vacancy"),
					_913);
		}

		//////////////////////////////
		// PROCESSING DELLA VACANCY //
		//////////////////////////////

		JAXBContext jaxbContext;
		Vacancy vacancyInput = null;
		BigDecimal prgRichiestaAz = null;
		try {
			jaxbContext = JAXBContext.newInstance(Vacancy.class);
			vacancyInput = (Vacancy) jaxbContext.createUnmarshaller().unmarshal(new StringReader(inputXML));

			// ricerca la richiesta di personale in base al campo del xml:
			// vacancy.datisistema.codiceofferta

			String codOfferta = vacancyInput.getDatiSistema().getCodiceofferta();

			if (codOfferta != null) {
				prgRichiestaAz = getPrgRichiestaAz(transExec, codOfferta);
			}

			// se la richiesta non è già presente nel db la aggiunge

			if (prgRichiestaAz == null) {
				prgRichiestaAz = doInsert(transExec, vacancyInput, strDataRichiesta);
			}

		} catch (EMFInternalError ex) {
			_logger.error("Errore nell'inserimento della Vacancy", ex);
			throw new MatchingBlenError(new EMFInternalError(EMFErrorSeverity.ERROR,
					"Errore nell'inserimento della Vacancy: " + ex.getMessage()), _999);
		} catch (Exception ex) {
			_logger.error("Errore validazione xml Vacancy", ex);
			throw new MatchingBlenError(
					new EMFInternalError(EMFErrorSeverity.ERROR, "Errore validazione xml Vacancy: " + ex.getMessage()),
					_913);

		}
		return prgRichiestaAz;
	}

	private BigDecimal doInsert(TransactionQueryExecutor transExec, Vacancy vacancyInput, String strDataRichiesta)
			throws EMFInternalError {

		String codiceFiscale = vacancyInput.getDatoreLavoro().getDatiAnagrafici().getCodicefiscale();

		// Ricerca Azienda dato codice fiscale
		if (codiceFiscale == null) {
			_logger.error("Codice Fiscale non impostato");
			throw new MatchingBlenError(
					new EMFInternalError(new EMFInternalError(EMFErrorSeverity.ERROR, "Codice Fiscale non impostato")),
					_901);
		}
		BigDecimal prgUnAz = null;
		BigDecimal prgAz = null;

		DatiAzienda azienda = new DatiAzienda(vacancyInput.getDatoreLavoro().getDatiAnagrafici(),
				vacancyInput.getDatoreLavoro().getDatiContatto());
		prgAz = azienda.getAzienda(transExec);

		if (prgAz == null) {
			// se l'azienda non c'e' bisogna inserirla
			prgAz = azienda.insertTestata(transExec);
			if (prgAz != null) {
				prgUnAz = azienda.insertSedeAzienda(prgAz, transExec);
			}

		} else {
			Vector sedi = azienda.getSediAzienda(prgAz, transExec);
			boolean sedeTrovata = false;
			if (sedi != null && sedi.size() > 0) {
				Vector<Integer> risultatoSede = azienda.cercaSedeAzienda(sedi);
				if (risultatoSede != null && risultatoSede.size() > 1) {
					Integer ris = risultatoSede.get(0);
					if (ris.intValue() != ServiziConstant.SEDE_AZIENDA_NON_TROVATA) {
						sedeTrovata = true;
						SourceBean sede = (SourceBean) sedi.get(risultatoSede.get(1).intValue());
						prgUnAz = (BigDecimal) sede.getAttribute("PRGUNITA");
					}
				}
			}
			if (!sedeTrovata) {
				prgUnAz = azienda.insertSedeAzienda(prgAz, transExec);
			}
		}

		if (prgAz == null || prgUnAz == null) {
			_logger.error("Errore nella ricerca azienda");
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Errore nella ricerca azienda");
		}

		DatiAzienda.setSedeLegaleIfNeeded(transExec, prgAz, prgUnAz);

		AltreInformazioni altreInfo = vacancyInput.getAltreInformazioni();
		RichiestaIDO ido = new RichiestaIDO(vacancyInput.getRichiesta(), vacancyInput.getAltreInformazioni(), azienda);

		BigDecimal prgRichiestaAz = ido.insertRichiesta(strDataRichiesta, prgAz, prgUnAz, transExec);

		if (prgRichiestaAz == null || prgRichiestaAz.compareTo(new BigDecimal(0)) < 0) {
			_logger.error("Errore nell'inserimento richiesta di personale");
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Errore nell'inserimento richiesta di personale");
		}

		return prgRichiestaAz;
	}

	private BigDecimal getPrgRichiestaAz(TransactionQueryExecutor transExec, String codOfferta)
			throws EMFInternalError {
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codOfferta;
		BigDecimal prgRichiestaAz = null;
		SourceBean prgRichiestaAzSB = (SourceBean) transExec.executeQuery("BLEN_GET_PRGRICHIESTAAZ_BY_CODOFFERTA",
				inputParameters, "SELECT");
		if (prgRichiestaAzSB.containsAttribute("ROW")) {
			prgRichiestaAz = (BigDecimal) prgRichiestaAzSB.getAttribute("ROW.PRGRICHIESTAAZ");
		}
		return prgRichiestaAz;
	}

	private BigDecimal getCdnLavoratore(TransactionQueryExecutor transExec, String codiceFiscale)
			throws EMFInternalError {
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codiceFiscale;
		BigDecimal cdnlavoratore = null;
		SourceBean lavSB = (SourceBean) transExec.executeQuery("SELECT_AN_LAVORATORE", inputParameters, "SELECT");
		if (lavSB.containsAttribute("ROW")) {
			cdnlavoratore = (BigDecimal) lavSB.getAttribute("ROW.CDNLAVORATORE");
		}
		return cdnlavoratore;
	}

	public EsitoMatching matching(Message msg) {
		TransactionQueryExecutor transExec = null;
		Risultato risultato = null;
		EsitoMatching esito = null;
		try {
			ObjectMessage message = (ObjectMessage) msg;
			Serializable arrObj = message.getObject();
			List l = (List) arrObj;
			Match match = (Match) l.get(0);
			esito = new EsitoMatching(match.getCodDomanda(), new Risultato("", ""));

			String dataCalcolo = DateUtils.getNow();

			// INIZIO TRANSAZIONE
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);

			transExec.initTransaction();
			_logger.info("initTransaction autocommit:"
					+ transExec.getDataConnection().getInternalConnection().getAutoCommit());
			// inserisco richiesta
			BigDecimal prgRichiestaAz = insertRicercaPersonale(transExec, match.getDomanda(), dataCalcolo);

			// creo rosa
			RosaInsert rosa = Rosa.insertRosaNominativa(transExec, prgRichiestaAz);

			// inserisco lavoratori nella rosa
			CandidaturaEstesa[] candidature = match.getCandidature();

			for (int i = 0; i < candidature.length; i++) {
				insertCandidatura(transExec, candidature[i].getCandidatura(), dataCalcolo, prgRichiestaAz,
						rosa.getPrgRosa(), rosa.getPrgIncrocio());
			}
			// FINE TRANSAZIONE
			transExec.commitTransaction();
			// PER TEST -->> transExec.rollBackTransaction();
			risultato = _101;
		} catch (MatchingBlenError e) {
			_logger.error("Errore metodo matching: " + e.getMessage());
			risultato = e.getRisultato();
			try {
				transExec.rollBackTransaction();
			} catch (EMFInternalError e1) {
				_logger.error("Errore nella rollBackTransaction: " + e.getMessage());

			}
		} catch (EMFInternalError e) {
			_logger.error("Errore metodo matching: " + e.getMessage());
			risultato = _999;
			rollback(transExec, e);
		} catch (SQLException e) {
			_logger.error("Errore metodo matching: " + e.getMessage());
			risultato = _952;
			rollback(transExec, e);

		} catch (Exception e) {
			_logger.error("Errore metodo matching: " + e.getMessage());
			risultato = _999;
			rollback(transExec, e);
		}
		esito.setRisultato(risultato);
		return esito;
	}

	private void rollback(TransactionQueryExecutor transExec, Exception e) {
		try {
			transExec.rollBackTransaction();
		} catch (EMFInternalError e1) {
			_logger.error("Errore nella rollBackTransaction: " + e.getMessage());
		}
	}

	public void send(Message msg) throws CoopApplicationException_Lavoratore, JMSException {
		try {
			EsitoMatching esito = matching(msg);
			callWebserviceRiezioneEsito(esito);
		} catch (RemoteException e) {
			_logger.error("Errore nella chiamata del ws RiezioneEsito: " + e.getMessage());
		}
	}

	public void callWebserviceRiezioneEsito(EsitoMatching esito) throws RemoteException {
		Object[] inputParameters = new Object[1];
		inputParameters[0] = "SIL_CLICLAV_MYBLEN"; // codservizio
		SourceBean ret = (SourceBean) QueryExecutor.executeQuery("WS_LOGON_BLEN", inputParameters, "SELECT",
				Values.DB_SIL_DATI);

		String username = (String) ret.getAttribute("ROW.STRUSERID");
		String pwd = (String) ret.getAttribute("ROW.STRPASSWORD");

		inputParameters = new Object[1]; // END_POINT_NAME
		inputParameters[0] = "InvioEsitoMatchMyBlen";
		ret = (SourceBean) QueryExecutor.executeQuery("SELECT_TS_ENDPOINT", inputParameters, "SELECT",
				Values.DB_SIL_DATI);

		String strUrl = (String) ret.getAttribute("ROW.STRURL");

		RicezioneEsitoMatchSILProxy proxy = new RicezioneEsitoMatchSILProxy();

		_logger.info("setEndpoint RicezioneEsitoMatchSILProxy: " + strUrl);

		proxy.setEndpoint(strUrl);

		_logger.info("Esito codice: " + esito.getCodcie());

		_logger.info("Esito risultato: " + esito.getRisultato().getCodiceEMessaggio());

		proxy.getEsitoMatching(username, pwd, esito.getCodcie(), esito.getRisultato().getCodiceEMessaggio());
	}

	public static void main(String[] args) {
		System.setProperty("CONTEXT_NAME", "sil");
		BaseTestCase.getInstance();

		MatchingBlen mb = new MatchingBlen();

		EsitoMatching esito = new EsitoMatching("001");
		esito.setRisultato(_101);

		try {
			mb.callWebserviceRiezioneEsito(esito);
			System.out.println("OK!");

		} catch (RemoteException e) {
			e.printStackTrace();
			System.out.println("KO!");

		}
		System.out.println("FINE!");

	}

	protected boolean isXmlValid(String inputXML, File schemaFile) {

		String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFile);

		if (validityErrors != null) {
			_logger.error("Errori di validazione file XML:\n" + validityErrors);
			return false;
		}
		return true;

	}

}
