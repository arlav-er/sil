package it.eng.sil.coop.webservices.agenda.appuntamento;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.apapi.XmlUtils;
import it.eng.sil.sms.ContattoSMS;

/**
 * 
 * @author Modificato getDispAppuntamento e fissaAppuntamento aggiunto verificaAppuntamento, getPrenotazioni e
 *         annullaAppuntamento da Giacomo Pandini
 *
 */
public class AppuntamentoService {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AppuntamentoService.class.getName());

	private File input_fissaAppuntamento_SchemaFile = new File(ConfigSingleton.getRootPath() + File.separator
			+ "WEB-INF" + File.separator + "xsd" + File.separator + "agenda" + File.separator + "appuntamento"
			+ File.separator + "fissa" + File.separator + "InputFissaAppuntamento.xsd");

	private File output_fissaAppuntamento_SchemaFile = new File(ConfigSingleton.getRootPath() + File.separator
			+ "WEB-INF" + File.separator + "xsd" + File.separator + "agenda" + File.separator + "appuntamento"
			+ File.separator + "fissa" + File.separator + "OutputFissaAppuntamento.xsd");

	private File input_disponibilita_SchemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "agenda" + File.separator + "appuntamento" + File.separator
			+ "disponibilita" + File.separator + "InputDispAppuntamento.xsd");

	private File output_disponibilita_SchemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "agenda" + File.separator + "appuntamento" + File.separator
			+ "disponibilita" + File.separator + "OutputDispAppuntamento.xsd");

	private File input_verifica_SchemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "agenda" + File.separator + "appuntamento" + File.separator
			+ "verifica" + File.separator + "InputVerificaApp.xsd");

	private File output_verifica_SchemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "agenda" + File.separator + "appuntamento" + File.separator
			+ "verifica" + File.separator + "OutputVerificaApp.xsd");

	private File input_annulla_anpal_SchemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "agenda" + File.separator + "appuntamento" + File.separator
			+ "annulla" + File.separator + "InputAnnullaApp_Anpal.xsd");

	@SuppressWarnings("unused")
	private File input_annulla_SchemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "agenda" + File.separator + "appuntamento" + File.separator
			+ "annulla" + File.separator + "InputAnnullaApp.xsd");

	private File output_annulla_SchemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "agenda" + File.separator + "appuntamento" + File.separator
			+ "annulla" + File.separator + "OutputAnnullaApp.xsd");

	private File input_prenotazioni_SchemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "agenda" + File.separator + "appuntamento" + File.separator
			+ "prenotazioni" + File.separator + "InputGetPrenotazioni.xsd");

	private File output_prenotazioni_SchemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "agenda" + File.separator + "appuntamento" + File.separator
			+ "prenotazioni" + File.separator + "OutputGetPrenotazioni.xsd");
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	String correctUTF8(String pString) throws UnsupportedEncodingException {
		return new String(pString.getBytes(), "UTF-8");
	}

	// Metodo utilizzato per controllare che la richiesta XML utilizzi un codice provincia uguale a quello specificato
	// in ts_generale
	// se il polo non e' regionale, altrimenti codice provincia della stessa regione
	@SuppressWarnings("unused")
	private boolean controlloCoerenzaSIL(DataConnection conn, String idProvincia) throws SQLException {
		String query = Constants.QUERY.CONTROLLO_COERENZA_SIL;
		String generalCPI;
		String codRegioneSil;
		String flgPoloReg;
		String codRegioneProv;
		PreparedStatement preparedStatement = null;
		ResultSet dataResult = null;
		PreparedStatement preparedStatementReg = null;
		ResultSet lResultSet = null;
		try {
			preparedStatement = conn.getInternalConnection().prepareStatement(query);
			dataResult = preparedStatement.executeQuery();
			if (dataResult.next()) {
				generalCPI = dataResult.getString(1);
				codRegioneSil = dataResult.getString(2);
				flgPoloReg = dataResult.getString(3);
				if (flgPoloReg.equalsIgnoreCase("N")) {
					return idProvincia.equals(generalCPI);
				} else {
					String queryReg = Constants.QUERY.CONTROLLO_COERENZA_SIL_REGIONALE;
					preparedStatementReg = conn.getInternalConnection().prepareStatement(queryReg);
					preparedStatementReg.setString(1, idProvincia);
					lResultSet = preparedStatementReg.executeQuery();
					if (lResultSet.next()) {
						codRegioneProv = lResultSet.getString(1);
						return codRegioneProv.equals(codRegioneSil);
					}
				}
			}
			return false;
		} finally {
			if (dataResult != null)
				dataResult.close();
			if (preparedStatement != null)
				preparedStatement.close();
			if (lResultSet != null)
				lResultSet.close();
			if (preparedStatementReg != null)
				preparedStatementReg.close();

		}
	}

	private String createXmlRispostaErrore(String codErrore, String descrizioneErrore)
			throws UnsupportedEncodingException {

		String outputXml = "";

		JAXBContext jaxbContext;
		StringWriter writer = new StringWriter();

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta.Esito esito = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta.Esito();
		esito.setCodice(codErrore);
		esito.setDescrizione(correctUTF8(descrizioneErrore));

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta risposta = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta();
		risposta.setEsito(esito);

		try {
			jaxbContext = JAXBContext
					.newInstance(it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione input XML", e1);
		}

		outputXml = writer.toString();

		return outputXml;

	}

	private String createXmlRispostaAppuntamentoEsistente(ResultSet res)
			throws DatatypeConfigurationException, ParseException, Exception {

		StringWriter writer = null;
		JAXBContext jaxbContext = null;

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta risposta = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta();

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta.Esito esito = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta.Esito();
		esito.setCodice(Constants.ESITO.PRENOTAZIONE_ESISTENTE_S);
		esito.setDescrizione(correctUTF8(Constants.ESITO.DESC_PRENOTAZIONE_ESISTENTE_S));
		risposta.setEsito(esito);

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta.DatiAppuntamento datiAppuntamento = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta.DatiAppuntamento();

		datiAppuntamento.setOraAppuntamento(res.getString("oraSlot"));
		datiAppuntamento.setAmbiente(res.getString("ambiente"));
		datiAppuntamento.setSiglaOperatore(res.getString("strsiglaoperatore"));
		datiAppuntamento.setDataAppuntamento(it.eng.sil.coop.webservices.myportal.servizicittadino.utils.DateUtils
				.stringToGregorianDate(res.getString("dataSlot")));
		datiAppuntamento.setDenominazioneCPI(res.getString("descCpi"));
		datiAppuntamento.setIdCPI(res.getString("codcpi"));
		datiAppuntamento.setIndirizzoCPI(res.getString("strindirizzo"));
		datiAppuntamento.setIndirizzoCPIstampa(res.getString("strindirizzostampa"));
		datiAppuntamento.setCodStato(res.getString("codstatoappuntamento"));
		datiAppuntamento.setNumMinuti(new BigInteger(res.getString("numminuti")));
		risposta.setDatiAppuntamento(datiAppuntamento);

		writer = new StringWriter();
		try {
			jaxbContext = JAXBContext
					.newInstance(it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione output XML", e1);
			throw new Exception(e1);
		}

		return writer.toString();
	}

	private String createXmlRispostaOk(EsitoRicercaSlot esitoRicercaSlot, EsitoAppuntamento esitoAppuntamento,
			String codCpi, boolean isReturnIdApp) throws DatatypeConfigurationException, ParseException, Exception {

		StringWriter writer = null;
		JAXBContext jaxbContext = null;

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta risposta = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta();

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta.Esito esito = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta.Esito();
		esito.setCodice(Constants.ESITO.OK);
		esito.setDescrizione(correctUTF8(Constants.ESITO.DESC_OK_FISSA));
		risposta.setEsito(esito);

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta.DatiAppuntamento datiAppuntamento = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta.DatiAppuntamento();
		datiAppuntamento.setOraAppuntamento(esitoRicercaSlot.getOra());
		datiAppuntamento.setAmbiente(esitoAppuntamento.getAmbiente());
		datiAppuntamento.setSiglaOperatore(esitoAppuntamento.getSiglaSpiSlot());
		datiAppuntamento.setDataAppuntamento(it.eng.sil.coop.webservices.myportal.servizicittadino.utils.DateUtils
				.stringToGregorianDate(esitoRicercaSlot.getData()));
		datiAppuntamento.setDenominazioneCPI(esitoAppuntamento.getDescrizioneCpi());
		datiAppuntamento.setIdCPI(codCpi);
		datiAppuntamento.setIndirizzoCPI(esitoAppuntamento.getIndirizzoCpi());
		datiAppuntamento.setIndirizzoCPIstampa(esitoAppuntamento.getIndirizzoStampaCpi());
		datiAppuntamento.setCodStato("2");
		datiAppuntamento.setNumMinuti(new BigInteger(esitoRicercaSlot.getDurata()));
		if (isReturnIdApp) {
			datiAppuntamento.setIdAppuntamento(esitoAppuntamento.getPrgAppuntamento().toString());
		}
		risposta.setDatiAppuntamento(datiAppuntamento);

		writer = new StringWriter();
		try {
			jaxbContext = JAXBContext
					.newInstance(it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.output.Risposta.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione output XML", e1);
			throw new Exception(e1);
		}

		return writer.toString();
	}

	public String fissaAppuntamento(String inputXml) {

		String outputXml = null;

		String strEmail = "";

		String codiceRichiesta = null;
		String identificativoProvincia = null;
		String prgSlot = null;
		String codCpi = null;
		String dataDa = null;
		String dataA = null;
		String mattinoPomeriggio = null;
		String lavoratoreAzienda = null;
		String strCodiceFiscale = null;
		String strCell = null;

		// lav

		String strNome = null;
		String strCognome = null;
		String datNasc = null;
		String codComNas = null;
		String strSesso = null;
		String codCittadinanza = null;
		String strIndirizzoDom = null;
		String codComDom = null;
		String strIndirizzoRes = null;
		String codComRes = null;
		String flgEmail = null;
		String flgSMS = null;
		String flgInvioSMS = null;

		boolean isLavoratore = false;
		boolean isAzienda = false;
		boolean esisteLavoratore = false;
		boolean isAppuntamentoOk = false;
		boolean isReturnIdApp = false;
		String idCoap = null;

		DataConnection conn = null;

		EsitoAppuntamento esitoAppuntamento = null;
		EsitoRicercaSlot esitoRicercaSlot = null;
		BigDecimal cdnLavoratore = null;

		Object[] args = null;

		try {

			it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.input.Appuntamento appuntamento = null;
			JAXBContext jaxbContext = null;

			//////////////////////
			// INPUT PROCESSING //
			//////////////////////

			boolean inputXmlIsValid = XmlUtils.isXmlValid(inputXml, input_fissaAppuntamento_SchemaFile);
			if (!inputXmlIsValid) {
				outputXml = createXmlRispostaErrore(Constants.ESITO.ERRORE_VALIDAZIONE_INPUT,
						Constants.ESITO.DESC_ERRORE_VALIDAZIONE_INPUT);
				return outputXml;
			}

			try {
				jaxbContext = JAXBContext.newInstance(
						it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.input.Appuntamento.class);
				appuntamento = (it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.input.Appuntamento) jaxbContext
						.createUnmarshaller().unmarshal(new StringReader(inputXml));
			} catch (JAXBException e) {
				_logger.error("Errore parsing input xml: " + inputXml, e);
				outputXml = createXmlRispostaErrore(Constants.ESITO.ERRORE_GENERICO,
						Constants.ESITO.DESC_ERRORE_GENERICO);
				return outputXml;
			}

			// lettura dati da input
			// se nella request si ha returnIdApp=true allora
			// nella response si restituisce anche l'identificativo appuntamento (AG_AGENDA.returnIdApp)

			identificativoProvincia = appuntamento.getIdProvincia();
			strCodiceFiscale = appuntamento.getCodiceFiscale();
			if (appuntamento.getDatiContatto() != null) {
				strCell = appuntamento.getDatiContatto().getCellulare();
				flgInvioSMS = appuntamento.getDatiContatto().getConsensoSMS();
				strEmail = appuntamento.getDatiContatto().getEmail();
			}
			if (appuntamento.getParametriAppuntamento() != null) {
				codCpi = appuntamento.getParametriAppuntamento().getIdCPI();
				codiceRichiesta = appuntamento.getParametriAppuntamento().getCodiceRichiesta();
				if (appuntamento.getParametriAppuntamento().getContattiAutomatici() != null) {
					flgSMS = appuntamento.getParametriAppuntamento().getContattiAutomatici().getInvioSMS();
					flgEmail = appuntamento.getParametriAppuntamento().getContattiAutomatici().getInvioEmail();
				}
				if (appuntamento.getParametriAppuntamento().getIdentificativoSlot() != null) {
					prgSlot = appuntamento.getParametriAppuntamento().getIdentificativoSlot().toString();
				}
				if (appuntamento.getParametriAppuntamento().getDatiRicerca() != null) {
					mattinoPomeriggio = appuntamento.getParametriAppuntamento().getDatiRicerca().getMattinaPomeriggio();
					if (appuntamento.getParametriAppuntamento().getDatiRicerca().getDataDal() != null) {
						dataDa = DateUtils.formatXMLGregorian(
								appuntamento.getParametriAppuntamento().getDatiRicerca().getDataDal());
					}
					if (appuntamento.getParametriAppuntamento().getDatiRicerca().getDataAl() != null) {
						dataA = DateUtils.formatXMLGregorian(
								appuntamento.getParametriAppuntamento().getDatiRicerca().getDataAl());
					}
				}
			}
			if (appuntamento.getUtenteLavoratore() != null) {
				if (appuntamento.getUtenteLavoratore().getUtenteServizio() != null
						&& Constants.UTENTE_SERVIZIO_LAVORATORE
								.equalsIgnoreCase(appuntamento.getUtenteLavoratore().getUtenteServizio())) {
					lavoratoreAzienda = Constants.UTENTE_SERVIZIO_LAVORATORE;
				}
				if (appuntamento.getUtenteLavoratore().getLavoratore() != null) {
					if (appuntamento.getUtenteLavoratore().getLavoratore().getDomicilio() != null) {
						if (appuntamento.getUtenteLavoratore().getLavoratore().getDomicilio().getComune() != null) {
							codComDom = appuntamento.getUtenteLavoratore().getLavoratore().getDomicilio().getComune();
						}
						strIndirizzoDom = appuntamento.getUtenteLavoratore().getLavoratore().getDomicilio()
								.getIndirizzo();
					}
					if (appuntamento.getUtenteLavoratore().getLavoratore().getResidenza() != null) {
						if (appuntamento.getUtenteLavoratore().getLavoratore().getResidenza().getComune() != null) {
							codComRes = appuntamento.getUtenteLavoratore().getLavoratore().getResidenza().getComune();
						}
						strIndirizzoRes = appuntamento.getUtenteLavoratore().getLavoratore().getResidenza()
								.getIndirizzo();
					}
					if (appuntamento.getUtenteLavoratore().getLavoratore().getSesso() != null) {
						strSesso = appuntamento.getUtenteLavoratore().getLavoratore()
								.getSesso() == it.eng.sil.coop.webservices.agenda.appuntamento.xml.fissa.input.Sesso.M
										? "M"
										: "F";
					}

					strNome = appuntamento.getUtenteLavoratore().getLavoratore().getNome();
					strCognome = appuntamento.getUtenteLavoratore().getLavoratore().getCognome();
					datNasc = DateUtils
							.formatXMLGregorian(appuntamento.getUtenteLavoratore().getLavoratore().getDataNascita());
					codComNas = appuntamento.getUtenteLavoratore().getLavoratore().getComuneNascita();
					codCittadinanza = appuntamento.getUtenteLavoratore().getLavoratore().getCittadinanza();
				}

			}
			if (appuntamento.getUtenteAzienda() != null && appuntamento.getUtenteAzienda().getUtenteServizio() != null
					&& Constants.UTENTE_SERVIZIO_AZIENDA
							.equalsIgnoreCase(appuntamento.getUtenteAzienda().getUtenteServizio())) {
				lavoratoreAzienda = Constants.UTENTE_SERVIZIO_AZIENDA;
			}

			if (lavoratoreAzienda != null) {
				if (Constants.UTENTE_SERVIZIO_LAVORATORE.equalsIgnoreCase(lavoratoreAzienda)) {
					isLavoratore = true;
				} else if (Constants.UTENTE_SERVIZIO_AZIENDA.equalsIgnoreCase(lavoratoreAzienda)) {
					isAzienda = true;
				}
			}
			if (appuntamento.isReturnIdApp() != null && appuntamento.isReturnIdApp()) {
				isReturnIdApp = true;
			}

			////////////////////////
			// INIZIO TRANSAZIONE //
			////////////////////////

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			conn = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			TransactionQueryExecutor trans = new TransactionQueryExecutor(conn, null, null);
			trans.initTransaction();

			////////////////////////
			// RICERCA LAVORATORE //
			////////////////////////

			BigDecimal prgAzienda = null;
			BigDecimal prgUnita = null;

			String strCellDb = null;
			String flgInvioSmsDb = null;
			String numLock = null;

			if (isLavoratore) {

				args = new Object[1];
				args[0] = strCodiceFiscale;
				SourceBean cellLavoratoreSourceBean = (SourceBean) trans.executeQuery("SELECT_CELL_LAV_BY_CF", args,
						TransactionQueryExecutor.SELECT);
				if (cellLavoratoreSourceBean != null && cellLavoratoreSourceBean.containsAttribute("ROW")) {

					cdnLavoratore = SourceBeanUtils.getAttrBigDecimal(cellLavoratoreSourceBean, "ROW.CDNLAVORATORE");
					strCellDb = SourceBeanUtils.getAttrStr(cellLavoratoreSourceBean, "ROW.STRCELL");
					flgInvioSmsDb = SourceBeanUtils.getAttrStr(cellLavoratoreSourceBean, "ROW.FLGINVIOSMS");
					numLock = SourceBeanUtils.getAttrStr(cellLavoratoreSourceBean, "ROW.NUMKLOLAVORATORE");

					esisteLavoratore = true;

				}
			}

			///////////////////////////////////
			// CONTROLLI COERENZA LAVORATORE //
			///////////////////////////////////

			EsitoControlliCoerenza esitoControlliCoerenza = AgendaUtils.effettuaControlliCoerenza(conn,
					identificativoProvincia, prgSlot, codCpi, cdnLavoratore, codComDom, codComRes, codComNas,
					codCittadinanza, strCell, strEmail, flgSMS, flgEmail, flgInvioSMS, lavoratoreAzienda,
					codiceRichiesta);

			_logger.debug("esitoControlliCoerenza.isSuccess: " + esitoControlliCoerenza.isSuccess());
			_logger.debug("esitoControlliCoerenza.getCodErrore: " + esitoControlliCoerenza.getCodErrore());
			_logger.debug(
					"esitoControlliCoerenza.getDescrizioneErrore: " + esitoControlliCoerenza.getDescrizioneErrore());

			if (!esitoControlliCoerenza.isSuccess()) {
				conn.rollBackTransaction();
				outputXml = createXmlRispostaErrore(esitoControlliCoerenza.getCodErrore(),
						esitoControlliCoerenza.getDescrizioneErrore());
				return outputXml;
			}

			///////////////////////////////////
			// STORED PROCEDURE RICERCA SLOT //
			///////////////////////////////////

			esitoRicercaSlot = AgendaUtils.cercaSlot(conn, codiceRichiesta, identificativoProvincia, prgSlot, codCpi,
					dataDa, dataA, mattinoPomeriggio, lavoratoreAzienda);

			_logger.debug("Ricerca Slot trovato? " + esitoRicercaSlot.isTrovato());
			_logger.debug("Ricerca Slot slot.prgSlot: " + esitoRicercaSlot.getPrgSlot());
			_logger.debug("Ricerca Slot slot.codErrore: " + esitoRicercaSlot.getCodErrore());
			_logger.debug("Ricerca Slot slot.descrizioneErrore: " + esitoRicercaSlot.getDescrizioneErrore());
			_logger.debug("Ricerca Slot slot.data: " + esitoRicercaSlot.getData());
			_logger.debug("Ricerca Slot slot.ora: " + esitoRicercaSlot.getOra());
			_logger.debug("Ricerca Slot slot.durata: " + esitoRicercaSlot.getDurata());

			if (!esitoRicercaSlot.isTrovato()) {
				conn.rollBackTransaction();
				outputXml = createXmlRispostaErrore(esitoRicercaSlot.getCodErrore(),
						esitoRicercaSlot.getDescrizioneErrore());
				return outputXml;
			}

			///////////////////////////
			// LAVORATORE PROCESSING //
			///////////////////////////

			if (isLavoratore) {

				if (esisteLavoratore) {

					// (eventuale) aggiornamento lavoratore

					if (strCellDb == null && flgInvioSmsDb == null && cdnLavoratore != null) {

						// strCell e flgInvioSMS non presenti, aggiornare parametri

						args = new Object[4];
						args[0] = strCell;
						args[1] = flgInvioSMS;
						args[2] = numLock;
						args[3] = cdnLavoratore;

						try {

							trans.executeQuery("UPDATE_CELL_LAV_BY_CF", args, TransactionQueryExecutor.UPDATE);

							// se aggiornati il numero di cell e flg invio sms presi dai dati di input al servizio
							strCellDb = strCell;
							flgInvioSmsDb = flgInvioSMS;

							_logger.debug("Lavoratore aggiornato con successo, cdnLavoratore: " + cdnLavoratore);

						} catch (Exception e) {
							_logger.error("Impossibile aggiornare il record del lavoratore: " + cdnLavoratore);
						}

					}

				} else {

					// inserimento nuovo lavoratore
					EsitoInserimentoLavoratore esitoInserimentoLavoratore = LavoratoreUtils.inserisciLavoratore(

							trans, Constants.UTENTE_PORTALE, codCpi, strCodiceFiscale, strNome, strCognome, datNasc,
							codComNas, strSesso, codCittadinanza, strIndirizzoDom, codComDom, strIndirizzoRes,
							codComRes, strCell, strEmail, flgInvioSMS

					);

					if (esitoInserimentoLavoratore == null || !esitoInserimentoLavoratore.isSuccess()) {

						_logger.error("Errore durante inserimento nuovo lavoratore: " + strCodiceFiscale);
						conn.rollBackTransaction();
						outputXml = createXmlRispostaErrore(esitoInserimentoLavoratore.getCodErrore(),
								esitoInserimentoLavoratore.getDescrizioneErrore());
						return outputXml;

					}

					cdnLavoratore = esitoInserimentoLavoratore.getCdnLavoratore();

					_logger.debug("Lavoratore inserito con successo, cdnLavoratore: " + cdnLavoratore);

				}

			}

			//////////////////////////////////////
			// CHECK APPUNTAMENTI CONTEMPORANEI //
			//////////////////////////////////////
			// TODO:GESTIONE SE NON è LAVORATORE
			String query = Constants.QUERY.CHECK_PRESENZA_APPUNTAMENTI_CONTEMPORANEI;
			PreparedStatement preparedStatement = conn.getInternalConnection().prepareStatement(query);
			preparedStatement.setBigDecimal(1, esitoRicercaSlot.getPrgSlot());
			preparedStatement.setBigDecimal(2, cdnLavoratore);
			_logger.debug("Controlla appuntamenti contemporanei. Query: " + query);
			_logger.debug("Controlla appuntamenti contemporanei. Parametri: " + esitoRicercaSlot.getPrgSlot() + ", "
					+ cdnLavoratore);
			ResultSet lResultSet = preparedStatement.executeQuery();
			if (lResultSet.next()) {
				_logger.info("Già presente un appuntamento alla stessa ora e per la stessa persona");
				conn.rollBackTransaction();
				outputXml = createXmlRispostaErrore(Constants.ESITO.PRENOTAZIONE_CONTEMPORANEA_ESISTENTE,
						Constants.ESITO.DESC_PRENOTAZIONE_CONTEMPORANEA_ESISTENTE);
				return outputXml;
			}

			////////////////////////////
			// PARAMETRO PRENOTAZIONI //
			///////////////////////////
			String flagPrenotazioni = null;
			String queryServizio = "select strvalore from ts_config_loc where codtipoconfig = '" + codiceRichiesta
					+ "' "
					+ "and strcodrif = (select codprovinciasil from ts_generale where prggenerale = 1) and num = 8";
			preparedStatement = conn.getInternalConnection().prepareStatement(queryServizio);
			lResultSet = preparedStatement.executeQuery();
			if (lResultSet.next()) {
				flagPrenotazioni = lResultSet.getString("strvalore");
				_logger.debug("Parametro prenotazioni presente con valore: " + flagPrenotazioni);
			}

			if (flagPrenotazioni != null) {
				if (flagPrenotazioni.equals("T")) {
					query = Constants.QUERY.FISSA_FLAG_APPUNTAMENTO + " order by ag_agenda.dtmdataora asc ";
					preparedStatement = conn.getInternalConnection().prepareStatement(query);
					preparedStatement.setString(1, strCodiceFiscale);
					preparedStatement.setString(2, codCpi);
					_logger.debug("Controlla esistenza appuntamento. Query: " + query);
					_logger.debug("Controlla esistenza appuntamento. Parametri: " + strCodiceFiscale + ", " + codCpi);
					lResultSet = preparedStatement.executeQuery();
					if (lResultSet.next()) {
						_logger.info("Già presente un appuntamento con codice fiscale " + strCodiceFiscale
								+ " e codCpi " + codCpi);
						conn.rollBackTransaction();
						outputXml = createXmlRispostaErrore(Constants.ESITO.PRENOTAZIONE_ESISTENTE_T,
								Constants.ESITO.DESC_PRENOTAZIONE_ESISTENTE_T);
						return outputXml;
					}
				} else if (flagPrenotazioni.equals("S")) {
					query = Constants.QUERY.FISSA_FLAG_APPUNTAMENTO + " and ag_agenda.codservizio IN "
							+ " ( SELECT strvalore" + " FROM TS_CONFIG_LOC "
							+ " WHERE num = 0 AND strcodrif = (select codprovinciasil from ts_generale where prggenerale = 1) AND codtipoconfig = ? ) "
							+ " order by ag_agenda.dtmdataora asc ";
					preparedStatement = conn.getInternalConnection().prepareStatement(query);
					preparedStatement.setString(1, strCodiceFiscale);
					preparedStatement.setString(2, codCpi);
					preparedStatement.setString(3, codiceRichiesta);
					_logger.debug("Controlla esistenza appuntamento. Query: " + query);
					_logger.debug("Controlla esistenza appuntamento. Parametri: " + strCodiceFiscale + ", " + codCpi
							+ ", " + identificativoProvincia + ", " + codiceRichiesta);
					lResultSet = preparedStatement.executeQuery();
					if (lResultSet.next()) {
						_logger.info("Già presente un appuntamento con codice fiscale " + strCodiceFiscale + ", codCpi "
								+ codCpi + " e codice richiesta " + codiceRichiesta);
						conn.rollBackTransaction();
						outputXml = createXmlRispostaAppuntamentoEsistente(lResultSet);

						boolean outputXmlIsValid = XmlUtils.isXmlValid(outputXml, output_fissaAppuntamento_SchemaFile);
						if (!outputXmlIsValid) {
							_logger.error("Varlidazione fallita xml out");
							outputXml = createXmlRispostaErrore(Constants.ESITO.ERRORE_GENERICO,
									Constants.ESITO.DESC_ERRORE_GENERICO);
						}
						return outputXml;
					}
				}
			}

			// Controllo redmine 7769 umbria reimpiego
			if (isLavoratore && strCodiceFiscale != null && !strCodiceFiscale.equals("")) {

				String queryRei = checkRei(conn, codiceRichiesta);
				if (queryRei != null && !queryRei.equals("")) {
					preparedStatement = conn.getInternalConnection().prepareStatement(queryRei);
					preparedStatement.setString(1, strCodiceFiscale);

					lResultSet = preparedStatement.executeQuery();
					if (lResultSet != null && lResultSet.next()) {

						int countRei = lResultSet.getInt(1);
						if (countRei == 0) {
							_logger.info("Condizioni aggiuntive per prenotazione non superate per codice fiscale "
									+ strCodiceFiscale + " e codice richiesta " + codiceRichiesta);
							conn.rollBackTransaction();
							outputXml = createXmlRispostaErrore(
									Constants.ESITO.ERRORE_PRENOTAZIONE_CONTROLLI_AGGIUNTIVI,
									Constants.ESITO.DESC_PRENOTAZIONE_CONTROLLI_AGGIUNTIVI);
							return outputXml;
						}
					}
				}
			}

			//////////////////
			// APPUNTAMENTO //
			//////////////////

			esitoAppuntamento = null;

			if (esitoRicercaSlot.isTrovato() && esitoRicercaSlot.getPrgSlot() != null
					&& (cdnLavoratore != null || (prgAzienda != null && prgUnita != null))) {
				String do_Commit = "false";

				esitoAppuntamento = AgendaUtils.prenotaAppuntamento(conn, codCpi, esitoRicercaSlot.getPrgSlot(),
						cdnLavoratore, prgAzienda, prgUnita, strEmail, // del lavoratore o dell'azienda
						strCell, // del lavoratore o dell'azienda,
						do_Commit);

				_logger.debug("Prenota Appuntamento successo? " + esitoAppuntamento.isSuccess());
				_logger.debug(".codErrore " + esitoAppuntamento.getCodErrore());
				_logger.debug(".getDescrizioneErrore " + esitoAppuntamento.getDescrizioneErrore());
				_logger.debug(".getDescrizioneCpi " + esitoAppuntamento.getDescrizioneCpi());
				_logger.debug(".getIndirizzoCpi " + esitoAppuntamento.getIndirizzoCpi());
				_logger.debug(".getIndirizzoStampaCpi " + esitoAppuntamento.getIndirizzoStampaCpi());
				_logger.debug(".getTelefonoCpi " + esitoAppuntamento.getTelefonoCpi());

				_logger.debug(".isSpiServizioDisp " + esitoAppuntamento.isSpiServizioDisp());
				_logger.debug(".getNomeSpiServizio " + esitoAppuntamento.getNomeSpiServizio());
				_logger.debug(".getCognomeSpiServizio " + esitoAppuntamento.getCognomeSpiServizio());
				_logger.debug(".getTelSpiServizio " + esitoAppuntamento.getTelSpiServizio());
				_logger.debug(".getSiglaSpiServizio " + esitoAppuntamento.getSiglaSpiServizio());

				_logger.debug(".isSpiSlotDisp " + esitoAppuntamento.isSpiSlotDisp());
				_logger.debug(".getNomeSpiSlot " + esitoAppuntamento.getNomeSpiSlot());
				_logger.debug(".getCognomeSpiSlot " + esitoAppuntamento.getCognomeSpiSlot());
				_logger.debug(".getTelSpiSlot " + esitoAppuntamento.getTelSpiSlot());
				_logger.debug(".getSiglaSpiSlot " + esitoAppuntamento.getSiglaSpiSlot());

				_logger.debug(".getAmbiente " + esitoAppuntamento.getAmbiente());
				_logger.debug(".getPrgAppuntamento " + esitoAppuntamento.getPrgAppuntamento().toString());

			}

			///////////////////////
			// OUTPUT PROCESSING //
			///////////////////////

			isAppuntamentoOk = esitoAppuntamento != null && esitoAppuntamento.isSuccess();

			if (isAppuntamentoOk) {

				idCoap = appuntamento.getIdCoap();
				if (StringUtils.isFilledNoBlank(idCoap)) {
					String do_Commit = "false";
					String esitoAggiornamento = AgendaUtils.aggiornaIdCoapAppuntamento(conn,
							esitoAppuntamento.getPrgAppuntamento(), idCoap, new BigDecimal(363), // FIXME
							do_Commit);

					if (esitoAggiornamento.equalsIgnoreCase("OK")) {
						isAppuntamentoOk = true;
					} else {
						isAppuntamentoOk = false;
					}
				}

				outputXml = createXmlRispostaOk(esitoRicercaSlot, esitoAppuntamento, codCpi, isReturnIdApp);

			} else {

				if (esitoAppuntamento.getCodErrore().equalsIgnoreCase(Constants.ESITO.ERRORE_CONCORRENZA)) {
					outputXml = createXmlRispostaErrore(esitoAppuntamento.getCodErrore(),
							esitoAppuntamento.getDescrizioneErrore());
				}

				if (outputXml == null) {
					// potrebbero già essere avvenuti altri errori
					outputXml = createXmlRispostaErrore(Constants.ESITO.ERRORE_GENERICO,
							Constants.ESITO.DESC_ERRORE_GENERICO);
				}

			}

			////////////////////////
			// VALIDAZIONE OUTPUT //
			////////////////////////

			boolean outputXmlIsValid = XmlUtils.isXmlValid(outputXml, output_fissaAppuntamento_SchemaFile);
			if (!outputXmlIsValid) {
				outputXml = createXmlRispostaErrore(Constants.ESITO.ERRORE_GENERICO,
						Constants.ESITO.DESC_ERRORE_GENERICO);
			}

			if (isAppuntamentoOk) {
				conn.commitTransaction();
			} else {
				conn.rollBackTransaction();
			}

		} catch (Exception e) {

			_logger.error("Eccezione: prenotazione appuntamento ", e);

			try {
				outputXml = createXmlRispostaErrore(Constants.ESITO.ERRORE_GENERICO,
						Constants.ESITO.DESC_ERRORE_GENERICO);
			} catch (Exception eg) {
				_logger.debug("Errore in fase di generazione di una risposta con errore ", eg);
			}

			try {
				if (conn != null) {
					conn.rollBackTransaction();
				}
			} catch (EMFInternalError e1) {
				_logger.error("Eccezione: roll back transazione appuntamento ", e1);
			}

		} finally {
			Utils.releaseResources(conn, null, null);
		}

		//////////////////////////////////////
		// TERMINE SE UTENTE NON LAVORATORE //
		//////////////////////////////////////

		if (!isAppuntamentoOk || isAzienda) {
			return outputXml;
		}

		///////////////
		// INVIO SMS //
		///////////////

		boolean isSmsOk = false;
		String prgSpiContatto = ContattoUtils.recuperaPrgSpiPerContatto(Constants.UTENTE_PORTALE);

		if (isLavoratore && flgInvioSMS != null && "S".equalsIgnoreCase(flgInvioSMS) && flgSMS != null
				&& "S".equalsIgnoreCase(flgSMS)) {

			String codTipoSms = SMSUtils.getCodTipoSms(codiceRichiesta, identificativoProvincia);

			ContattoSMS contattoSms = new ContattoSMS();

			if (codTipoSms != null) {

				if (codTipoSms.equalsIgnoreCase(Constants.SMS.GARANZIA_GIOVANI)
						|| codTipoSms.equalsIgnoreCase(Constants.SMS.PATRONATO_SINDACATO)) {

					isSmsOk = contattoSms.creaPerAppuntamentoOnline(cdnLavoratore, strCell, strNome, strCognome, codCpi,
							esitoAppuntamento.getDescrizioneCpi(),
							(esitoAppuntamento.getAmbiente() != null
									&& !"".equalsIgnoreCase(esitoAppuntamento.getAmbiente()))
											? esitoAppuntamento.getAmbiente()
											: esitoAppuntamento.getIndirizzoCpi(),
							esitoAppuntamento.getTelefonoCpi(), esitoRicercaSlot.getData(), esitoRicercaSlot.getOra(),
							prgSpiContatto, esitoAppuntamento.isSuccess(), esitoAppuntamento.isSpiSlotDisp(),
							esitoAppuntamento.getNomeSpiSlot(), esitoAppuntamento.getCognomeSpiSlot(),
							esitoAppuntamento.getTelSpiSlot(), codTipoSms);
				}

				// inserire qui eventuali altre gestioni di tipologie di sms

			}

		}

		/////////////////
		// INVIO EMAIL //
		/////////////////

		boolean isEmailOk = false;

		if (isLavoratore && flgEmail != null && "S".equalsIgnoreCase(flgEmail)) {

			String mailMessage = "";

			String[] emailSpecs = EmailUtils.recuperaSpecsEmail(codiceRichiesta, identificativoProvincia);
			String oggettoEmail = emailSpecs[0];
			String mittenteEmail = emailSpecs[1];
			String codTipoEmail = emailSpecs[2];
			String strdestinatari = emailSpecs[3];
			// String strdestcc = emailSpecs[4]; // per ora non citato in analisi
			String strdestccn = emailSpecs[5];

			if (codTipoEmail != null && codTipoEmail.equalsIgnoreCase(Constants.EMAIL.GARANZIA_GIOVANI)) {

				mailMessage = "Salve " + strNome + " " + strCognome + ", "
						+ "hai preso un appuntamento presso il nostro CPI di " + esitoAppuntamento.getDescrizioneCpi()
						+ ", " + "" + esitoAppuntamento.getIndirizzoCpi() + ", "
						+ "per un colloquio legato alla tua adesione a Garanzia Giovani. " + "Il giorno fissato è il "
						+ esitoRicercaSlot.getData() + " alle ore " + esitoRicercaSlot.getOra() + "";

				if (esitoAppuntamento.getAmbiente() != null && !"".equalsIgnoreCase(esitoAppuntamento.getAmbiente())) {
					mailMessage += " presso " + esitoAppuntamento.getAmbiente();
				}

				mailMessage += ". " + "In caso di impossibilità, telefonare al " + esitoAppuntamento.getTelefonoCpi()
						+ " per fissare un nuovo appuntamento. "
						+ "Ti ricordiamo che se hai già rilasciato una Dichiarazione di Immediata Disponibilità la mancata partecipazione al colloquio "
						+ "con il CPI ti farà perdere lo stato di disoccupazione.";

			} else {
				if (codTipoEmail != null && codTipoEmail.equalsIgnoreCase(Constants.EMAIL.GENERICO_PROMEMORIA)) {
					if (esitoAppuntamento.getAmbiente() != null && !esitoAppuntamento.getAmbiente().equals("")) {
						mailMessage = "Salve " + strNome + " " + strCognome + ", "
								+ "hai prenotato un appuntamento con il CPI di " + esitoAppuntamento.getDescrizioneCpi()
								+ " " + "per il giorno " + esitoRicercaSlot.getData() + " alle ore "
								+ esitoRicercaSlot.getOra() + " " + "presso " + esitoAppuntamento.getAmbiente() + ".";
					} else {
						mailMessage = "Salve " + strNome + " " + strCognome + ", "
								+ "hai prenotato un appuntamento presso il nostro CPI di "
								+ esitoAppuntamento.getDescrizioneCpi() + ", " + "per il giorno "
								+ esitoRicercaSlot.getData() + " alle ore " + esitoRicercaSlot.getOra() + ".";
					}
				}
			}

			isEmailOk = EmailUtils.insertContattoAndSendEmail(cdnLavoratore, strCell, codCpi, strEmail, strNome,
					strCognome, strEmail, esitoRicercaSlot.getData(), esitoRicercaSlot.getOra(),
					esitoRicercaSlot.getDurata(), esitoAppuntamento.getDescrizioneCpi(),
					esitoAppuntamento.getIndirizzoCpi(), esitoAppuntamento.getTelefonoCpi(), prgSpiContatto,
					esitoAppuntamento.isSuccess(), esitoAppuntamento.isSpiSlotDisp(),
					esitoAppuntamento.getNomeSpiSlot(), esitoAppuntamento.getCognomeSpiSlot(),
					esitoAppuntamento.getTelSpiSlot(), codiceRichiesta, identificativoProvincia, mailMessage,
					mittenteEmail, oggettoEmail, strdestinatari, strdestccn);

		}

		//////////////////////////
		// INSERIMENTO EVIDENZA //
		//////////////////////////

		if (isLavoratore) {

			String strEvidenza = "";

			strEvidenza += "App: " + (isAppuntamentoOk ? "Sì" : "No") + "; ";
			strEvidenza += "Sms: " + (isSmsOk ? "Sì" : "No") + "; ";
			strEvidenza += "E-mail: " + (isEmailOk ? "Sì" : "No") + ". ";

			if (isAppuntamentoOk) {

				strEvidenza += "Per il lavoratore è stato fissato tramite sistema esterno "
						+ "un appuntamento il giorno " + esitoRicercaSlot.getData() + " alle ore "
						+ esitoRicercaSlot.getOra() + " presso il CPI " + esitoAppuntamento.getDescrizioneCpi()
						+ " per il Servizio " + esitoRicercaSlot.getDescrizioneServizio() + ".";

			} else {
				strEvidenza += "Non è stato possibile fissare in automatico l'appuntamento.";
			}

			if (!isSmsOk) {
				strEvidenza += "\n\nErrore durante l'invio della notifica SMS.";
			} else {
				strEvidenza += "\n\nSMS inviato correttamente.";
			}

			if (!isEmailOk) {
				strEvidenza += "\n\nErrore durante l'invio della notifica Email.";
			} else {
				strEvidenza += "\n\nEmail inviata correttamente.";
			}

			strEvidenza += "\n\nTipo richiesta: " + codiceRichiesta;

			EvidenzaUtils.insertEvidenza(cdnLavoratore, Constants.NUM_CONFIGURAZIONE, strEvidenza, codiceRichiesta);

		}

		return outputXml;

	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Compone la risposta XML in caso di errore
	private String createXmlRispostaErroreDisponibilita(String codErrore, String descrizioneErrore)
			throws UnsupportedEncodingException {

		String outputXml = "";

		JAXBContext jaxbContext;
		StringWriter writer = new StringWriter();

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp.Esito esito = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp.Esito();
		esito.setCodice(codErrore);
		esito.setDescrizione(correctUTF8(descrizioneErrore));

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp risposta = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp();
		risposta.setEsito(esito);

		try {
			jaxbContext = JAXBContext.newInstance(
					it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione input XML", e1);
		}

		outputXml = writer.toString();

		return outputXml;
	}

	// Compone la risposta XML in caso di risposta a buon fine
	private String createXmlRispostaDisponibilitaOk(ResultSet data, BigInteger maxSlot)
			throws DatatypeConfigurationException, ParseException, Exception {

		StringWriter writer = null;
		JAXBContext jaxbContext = null;

		boolean exit = false;
		String dataRif = null;
		String dataSlot = null;
		String dataOraSlot = null;

		int numMaxSlot = -1;
		if (maxSlot != null) {
			numMaxSlot = maxSlot.intValue();
		}
		// Genera oggetto Esito e lo valorizza
		it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp.Esito esito = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp.Esito();
		esito.setCodice(Constants.ESITO.OK);
		esito.setDescrizione(correctUTF8(Constants.ESITO.DESC_OK_DISPONIBILITA));
		// Genera oggetto Risposta e lo valorizza
		it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp risposta = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp();
		risposta.setEsito(esito);

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp.ElencoDisponibilita elencoDisponibilita = null;
		it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp.ElencoDisponibilita.DatiAppuntamento datiAppuntamento = null;
		List<it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp.ElencoDisponibilita.DatiAppuntamento> appuntamenti = null;
		int indice = 0;
		if (data.isBeforeFirst()) {
			// Scorre i valori trovati
			while (data.next() && !exit) {
				dataOraSlot = data.getString("dataOraSlot");
				dataSlot = data.getString("dataSlot");
				if (dataRif == null) { // Se primo valore inizializza l'elenco delle disponibilità
					dataRif = dataSlot;
					// Genera l'elenco dati disponibilità e lo valorizza
					elencoDisponibilita = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp.ElencoDisponibilita();
					elencoDisponibilita.setDataRif(it.eng.sil.coop.webservices.myportal.servizicittadino.utils.DateUtils
							.stringToGregorianDate(dataOraSlot));
					appuntamenti = elencoDisponibilita.getDatiAppuntamento();
				}
				// Genera un oggetto dati appuntamento e lo valorizza
				datiAppuntamento = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp.ElencoDisponibilita.DatiAppuntamento();
				// Ottiene i valori e li aggiunge ai dati appuntamento
				BigDecimal idSlot = data.getBigDecimal("prgslot");
				String idCpi = data.getString("codcpi");
				String oraSlot = data.getString("oraSlot");
				String descCpi = data.getString("descCpi");
				String indirizzoCpi = data.getString("strindirizzo");
				String indirizzoCpiStampa = data.getString("strindirizzostampa");
				String siglaOperatore = data.getString("strsiglaoperatore");
				String ambiente = data.getString("ambiente");
				Integer numMinuti = data.getInt("numminuti");

				datiAppuntamento.setIdentificativoSlot(idSlot.toBigInteger());
				datiAppuntamento
						.setDataAppuntamento(it.eng.sil.coop.webservices.myportal.servizicittadino.utils.DateUtils
								.stringToGregorianDate(dataSlot));
				datiAppuntamento.setOraAppuntamento(oraSlot);
				datiAppuntamento.setIdCPI(idCpi);
				datiAppuntamento.setDenominazioneCPI(descCpi);
				if (indirizzoCpi != null && !indirizzoCpi.equals("")) {
					datiAppuntamento.setIndirizzoCPI(indirizzoCpi);
				}
				if (indirizzoCpiStampa != null && !indirizzoCpiStampa.equals("")) {
					datiAppuntamento.setIndirizzoCPIstampa(indirizzoCpiStampa);
				}
				if (siglaOperatore != null && !siglaOperatore.equals("")) {
					datiAppuntamento.setSiglaOperatore(siglaOperatore);
				}
				if (ambiente != null) {
					datiAppuntamento.setAmbiente(ambiente);
				}
				datiAppuntamento.setNumMinuti(numMinuti);
				// Aggiunge all'elenco degli appuntamenti l'appuntamento generato
				appuntamenti.add(indice, datiAppuntamento);
				indice = indice + 1;
				// mi fermo a numMaxSlot se definito
				if (numMaxSlot > 0 && indice == numMaxSlot) {
					exit = true;
				}
			}
		}
		// Aggiunge alla risposta l'elenco disponibilità
		if (elencoDisponibilita != null) {
			risposta.setElencoDisponibilita(elencoDisponibilita);
		}

		writer = new StringWriter();
		try {
			jaxbContext = JAXBContext.newInstance(
					it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.output.RispostaDisp.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione output XML", e1);
			throw new Exception(e1);
		}

		return writer.toString();
	}

	// Compone la query per il calcolo della disponibità, genera solo la parte da From in poi
	private String getSqlFromQueryDisponibilita(String utenteServizio, String identificativoProvincia, String codCpi,
			String codiceRichiesta, String dataDa, String dataA, String mattinoPomeriggio, BigInteger ambiente,
			HashMap<String, String> parametri) throws Exception {
		String servizio = parametri.get("SERVIZIO");
		String gg = parametri.get("GIORNI");
		String orario = parametri.get("ORARIO");
		String dataMaxDal = null;

		String query = Constants.QUERY.DISPONIBILITA_FROM;
		String condizioneWhere = "where de_stato_slot.flgprenotabile = 'S' and ag_slot.flgpubblico = 'S' ";

		if (utenteServizio != null) {
			if (utenteServizio.equalsIgnoreCase(Constants.UTENTE_SERVIZIO_LAVORATORE)) {
				condizioneWhere = condizioneWhere + "and ag_slot.numlavoratori > 0 ";
			} else {
				if (utenteServizio.equalsIgnoreCase(Constants.UTENTE_SERVIZIO_AZIENDA)) {
					condizioneWhere = condizioneWhere + "and ag_slot.numaziende > 0 ";
				}
			}
		}

		if (codCpi != null && !codCpi.equals("")) {
			condizioneWhere = condizioneWhere + "and ag_slot.codcpi = '" + codCpi + "' ";
		}

		if (servizio != null && !servizio.equals("")) {
			condizioneWhere = condizioneWhere + "and ag_slot.codservizio IN (" + servizio + ") ";
		}

		String oggi = DateUtils.getNow();
		if (gg != null && !gg.equals("")) {
			dataMaxDal = DateUtils.aggiungiNumeroGiorni(oggi, new Integer(gg).intValue());
		} else {
			dataMaxDal = DateUtils.aggiungiNumeroGiorni(oggi, 1);
		}
		if (dataDa != null && !dataDa.equals("")) {
			if (DateUtils.compare(dataDa, dataMaxDal) > 0) {
				dataMaxDal = dataDa;
			}
		}
		if (dataMaxDal != null && !dataMaxDal.equals("")) {
			condizioneWhere = condizioneWhere + "and trunc(ag_slot.dtmdataora) >= to_date('" + dataMaxDal
					+ "', 'dd/mm/yyyy') ";
		}

		if (dataA != null && !dataA.equals("")) {
			condizioneWhere = condizioneWhere + "and trunc(ag_slot.dtmdataora) <= to_date('" + dataA
					+ "', 'dd/mm/yyyy') ";
		}

		if (orario != null && !orario.equals("")) {
			String[] vettOrario = orario.split(";");
			String orarioMin = vettOrario[0].trim();
			String orarioMax = vettOrario[1].trim();
			condizioneWhere = condizioneWhere
					+ "and ag_slot.dtmdataora between to_date(to_char(ag_slot.dtmdataora, 'dd/mm/yyyy') || ' ' || '"
					+ orarioMin
					+ "', 'dd/mm/yyyy hh24.mi.ss') and to_date(to_char(ag_slot.dtmdataora, 'dd/mm/yyyy') || ' ' || '"
					+ orarioMax + "', 'dd/mm/yyyy hh24.mi.ss') ";
		}

		if (ambiente != null) {
			query = query
					+ " inner join de_ambiente on (ag_slot.prgambiente = de_ambiente.prgambiente and de_ambiente.prgambiente = "
					+ ambiente + " ) ";
		} else {
			query = query + " left join de_ambiente on (ag_slot.prgambiente = de_ambiente.prgambiente) ";
		}

		if (!condizioneWhere.equals("")) {
			query = query + condizioneWhere;
		}

		query = query + " order by ag_slot.dtmdataora asc";

		return query;
	}

	// Genera alcuni parametri utili alla query
	private HashMap<String, String> recuperaParametiDisponibilita(DataConnection conn, String codiceRichiesta,
			String identificativoProvincia, String matPom) throws Exception {
		HashMap<String, String> parametri = new HashMap<String, String>();
		PreparedStatement preparedStatementConfig = null;
		ResultSet resultSetConfig = null;
		String servizio = "";
		try {
			// SERVIZIO
			String queryServizio = "select strvalore from ts_config_loc where codtipoconfig = '" + codiceRichiesta
					+ "' "
					+ "and strcodrif = (select codprovinciasil from ts_generale where prggenerale = 1) and num = 0";
			preparedStatementConfig = conn.getInternalConnection().prepareStatement(queryServizio);
			resultSetConfig = preparedStatementConfig.executeQuery();
			if (resultSetConfig.isBeforeFirst()) {
				// Crea una stringa unica che contiene tutti i valori di servizio nel formato da utilizzare nella query
				while (resultSetConfig.next()) {
					servizio = servizio + "'" + resultSetConfig.getString("strvalore") + "',";
				}
			}
			if (servizio != null && !servizio.equals("")) {
				servizio = servizio.substring(0, servizio.length() - 1);
				parametri.put("SERVIZIO", servizio);
			} else {
				return null; // Servizio mancante
			}
			// ORARIO
			int numConfigMP = 0;
			if (matPom != null) {
				if (matPom.equalsIgnoreCase(Constants.MATTINO)) {
					numConfigMP = 1;
				} else {
					if (matPom.equalsIgnoreCase(Constants.POMERIGGIO)) {
						numConfigMP = 2;
					}
				}
			}
			if (numConfigMP > 0) {
				String orarioMP = null;
				String queryMP = "select strvalore from ts_config_loc where codtipoconfig = '" + codiceRichiesta + "' "
						+ "and strcodrif = (select codprovinciasil from ts_generale where prggenerale = 1) and num = "
						+ numConfigMP;
				preparedStatementConfig = conn.getInternalConnection().prepareStatement(queryMP);
				resultSetConfig = preparedStatementConfig.executeQuery();
				if (resultSetConfig.isBeforeFirst()) {
					if (resultSetConfig.next()) {
						orarioMP = resultSetConfig.getString("strvalore");
					}
				}
				if (orarioMP != null && !orarioMP.equals("")) {
					parametri.put("ORARIO", orarioMP);
				}
				/*
				 * else { throw new Exception("Parametro di configurazione orario mattino/pomeriggio mancante"); }
				 */
			}
			// GIORNI
			String queryGG = "select strvalore from ts_config_loc where codtipoconfig = '" + codiceRichiesta + "' "
					+ "and strcodrif = (select codprovinciasil from ts_generale where prggenerale = 1) and num = 3";
			preparedStatementConfig = conn.getInternalConnection().prepareStatement(queryGG);
			resultSetConfig = preparedStatementConfig.executeQuery();
			if (resultSetConfig.isBeforeFirst()) {
				if (resultSetConfig.next()) {
					String gg = resultSetConfig.getString("strvalore");
					parametri.put("GIORNI", gg);
				}
			}
		} catch (Exception e) {
			_logger.debug("Errore in fase di recupero parametri di configurazione con config = " + codiceRichiesta, e);
			throw new Exception("Parametro di configurazione mancante");
		} finally {
			if (resultSetConfig != null) {
				resultSetConfig.close();
			}
			if (preparedStatementConfig != null) {
				preparedStatementConfig.close();
			}
		}
		return parametri;
	}

	// Metodo del WS
	public String getDispAppuntamento(String inputXml) {
		// parametri input
		String utenteServizio = null;
		String codiceRichiesta = null;
		String identificativoProvincia = null;
		String codCpi = null;
		String dataDa = null;
		String dataA = null;
		String mattinoPomeriggio = null;
		BigInteger ambiente = null;
		BigInteger ampiezza = null;
		BigInteger numMaxSlot = null;

		// risposta del servizio
		String outputXml = null;

		// oggetti per la connection
		DataConnection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet dataResult = null;

		try {
			it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.input.DispAppuntamento dispAppuntamento = null;
			JAXBContext jaxbContext = null;

			// VALIDAZIONE INPUT
			boolean inputXmlIsValid = XmlUtils.isXmlValid(inputXml, input_disponibilita_SchemaFile);
			if (!inputXmlIsValid) {
				_logger.error("Errore validazione xml input");
				outputXml = createXmlRispostaErroreDisponibilita(Constants.ESITO.ERRORE_VALIDAZIONE_INPUT,
						Constants.ESITO.DESC_ERRORE_VALIDAZIONE_INPUT);
				return outputXml;
			}

			try {
				jaxbContext = JAXBContext.newInstance(
						it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.input.DispAppuntamento.class);
				dispAppuntamento = (it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.input.DispAppuntamento) jaxbContext
						.createUnmarshaller().unmarshal(new StringReader(inputXml));
			} catch (JAXBException e) {
				_logger.error("Errore parsing input xml: " + inputXml, e);
				// outputXml = createXmlRispostaErroreGenericoDisponibilita();
				outputXml = createXmlRispostaErroreDisponibilita(Constants.ESITO.ERRORE_GENERICO,
						Constants.ESITO.DESC_ERRORE_GENERICO);
				return outputXml;
			}

			// Recupero parametri della richiesta
			utenteServizio = dispAppuntamento.getUtenteServizio();
			identificativoProvincia = dispAppuntamento.getIdProvincia();
			if (dispAppuntamento.getParametriAppuntamento() != null) {
				codCpi = dispAppuntamento.getParametriAppuntamento().getIdCPI();
				codiceRichiesta = dispAppuntamento.getParametriAppuntamento().getCodiceRichiesta();
				if (dispAppuntamento.getParametriAppuntamento().getAmpiezza() != null) {
					ampiezza = dispAppuntamento.getParametriAppuntamento().getAmpiezza();
				}
				if (dispAppuntamento.getParametriAppuntamento().getDatiRicerca() != null) {
					if (dispAppuntamento.getParametriAppuntamento().getDatiRicerca().getDataDal() != null) {
						dataDa = DateUtils.formatXMLGregorian(
								dispAppuntamento.getParametriAppuntamento().getDatiRicerca().getDataDal());
					}
					if (dispAppuntamento.getParametriAppuntamento().getDatiRicerca().getDataAl() != null) {
						dataA = DateUtils.formatXMLGregorian(
								dispAppuntamento.getParametriAppuntamento().getDatiRicerca().getDataAl());
					}
					if (dispAppuntamento.getParametriAppuntamento().getDatiRicerca().getMattinaPomeriggio() != null) {
						mattinoPomeriggio = dispAppuntamento.getParametriAppuntamento().getDatiRicerca()
								.getMattinaPomeriggio();
					}
					if (dispAppuntamento.getParametriAppuntamento().getDatiRicerca().getAmbiente() != null) {
						ambiente = dispAppuntamento.getParametriAppuntamento().getDatiRicerca().getAmbiente();
					}
					if (dispAppuntamento.getParametriAppuntamento().getNumMaxSlot() != null) {
						numMaxSlot = dispAppuntamento.getParametriAppuntamento().getNumMaxSlot();
					}
				}
			}
			// Ottiene la connessione al DB
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			conn = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			// Controllo coerenza CPI
			// DA RIPRISTINARE
			/*
			 * if(!controlloCoerenzaSIL(conn,identificativoProvincia)){
			 * _logger.error("Codice CPI nell'XML differente da quello del sistema"); outputXml =
			 * createXmlRispostaErroreDisponibilita(Constants.ESITO.ERRORE_COERENZA_PROVINCIA_XML,Constants.ESITO.
			 * DESC_ERRORE_COERENZA_PROVINCIA_XML); return outputXml; }
			 */

			// Genera un hash map contenete i parametri di disponibilità ORARIO, GIORNI, SERVIZIO
			HashMap<String, String> parametri = new HashMap<String, String>();
			parametri = recuperaParametiDisponibilita(conn, codiceRichiesta, identificativoProvincia,
					mattinoPomeriggio);
			if (parametri == null) {
				_logger.error("Servizo mancante");
				outputXml = createXmlRispostaErroreDisponibilita(Constants.ESITO.SERVIZIO_MANCANTE,
						Constants.ESITO.DESC_SERVIZIO_MANCANTE);
				return outputXml;
			}
			// Genera la parte dal FROM in poi della query per valutare la disponibilità
			String queryFromDisponibilita = getSqlFromQueryDisponibilita(utenteServizio, identificativoProvincia,
					codCpi, codiceRichiesta, dataDa, dataA, mattinoPomeriggio, ambiente, parametri);
			// Trova la prima data all'interno dell'intervallo di date specificato
			String query = Constants.QUERY.DISPONIBILITA_SELECT_DATA + queryFromDisponibilita;
			preparedStatement = conn.getInternalConnection().prepareStatement(query);
			_logger.debug("Ricerca disponibilità prima data. Query:  " + query);
			dataResult = preparedStatement.executeQuery();
			// Se la query trova ha risultato, prendo il primo giorno e in base al valore di ampiezza genero la parte
			// FORM della query risultato
			if (dataResult.next()) {
				// Prende il valore della prima data disponibile
				String dataSlot = dataResult.getString("dataSlot");
				// Risultato settimanale, ritorno anche i valori nei giorni successivi specificati
				if (ampiezza != null) {
					DateFormat lDateFormat = new SimpleDateFormat("dd/MM/yyyy");
					Calendar lCalendar = Calendar.getInstance();
					Date lDate = lDateFormat.parse(dataSlot);
					lCalendar.setTime(lDate);
					lCalendar.add(Calendar.DATE, ampiezza.intValue());
					String dateTo = lDateFormat.format(lCalendar.getTime());
					queryFromDisponibilita = getSqlFromQueryDisponibilita(utenteServizio, identificativoProvincia,
							codCpi, codiceRichiesta, dataSlot, dateTo, mattinoPomeriggio, ambiente, parametri);
					// Risultato giornaliero, ritorno i valori solo nel giorno trovato
				} else {
					queryFromDisponibilita = getSqlFromQueryDisponibilita(utenteServizio, identificativoProvincia,
							codCpi, codiceRichiesta, dataSlot, dataSlot, mattinoPomeriggio, ambiente, parametri);
				}
			}
			// Calcola il risultato effettivo
			query = Constants.QUERY.DISPONIBILITA_SELECT_RESULT + queryFromDisponibilita;
			preparedStatement = conn.getInternalConnection().prepareStatement(query);
			_logger.debug("Ricerca disponibilità. Query:  " + query);
			dataResult = preparedStatement.executeQuery();
			// Crea la rispota XML
			outputXml = createXmlRispostaDisponibilitaOk(dataResult, numMaxSlot);

			// VALIDAZIONE OUTPUT
			boolean outputXmlIsValid = XmlUtils.isXmlValid(outputXml, output_disponibilita_SchemaFile);
			if (!outputXmlIsValid) {
				_logger.error("Errore validazione xml output");
				outputXml = createXmlRispostaErroreDisponibilita(Constants.ESITO.ERRORE_GENERICO,
						Constants.ESITO.DESC_ERRORE_GENERICO);
			} else {
				_logger.info("Operazione getDispAppuntamento ha restituito gli slot disponibili");
			}
		} catch (Exception e) {
			_logger.error("Eccezione in metodo getDispAppuntamento ", e);

			try {
				outputXml = createXmlRispostaErroreDisponibilita(Constants.ESITO.ERRORE_GENERICO,
						Constants.ESITO.DESC_ERRORE_GENERICO);
			} catch (Exception eg) {
				_logger.debug("Errore in fase di generazione di una risposta con errore ", eg);
			}
		} finally {
			try {
				if (dataResult != null) {
					dataResult.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException eSql) {
				_logger.error("Eccezione: errore chiusura ResultSet o  PreparedStatement", eSql);
			}
			Utils.releaseResources(conn, null, null);
		}

		return outputXml;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	// Compone la risposta XML in caso di errore
	private String createXmlRispostaErroreVerifica(String codErrore, String descrizioneErrore)
			throws UnsupportedEncodingException {
		String outputXml = "";

		JAXBContext jaxbContext;
		StringWriter writer = new StringWriter();

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.output.RispostaVerifica.Esito esito = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.output.RispostaVerifica.Esito();
		esito.setCodice(codErrore);
		esito.setDescrizione(correctUTF8(descrizioneErrore));

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.output.RispostaVerifica risposta = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.output.RispostaVerifica();
		risposta.setEsito(esito);

		try {
			jaxbContext = JAXBContext.newInstance(
					it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.output.RispostaVerifica.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione input XML", e1);
		}

		outputXml = writer.toString();

		return outputXml;
	}

	private String createXmlRispostaVerificaOk(ResultSet data)
			throws SQLException, ParseException, DatatypeConfigurationException, Exception {
		StringWriter writer = null;
		JAXBContext jaxbContext = null;

		// Genera oggetto Esito e lo valorizza
		it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.output.RispostaVerifica.Esito esito = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.output.RispostaVerifica.Esito();
		esito.setCodice(Constants.ESITO.OK);
		esito.setDescrizione(correctUTF8(Constants.ESITO.DESC_OK_DISPONIBILITA));
		// Genera oggetto Risposta e lo valorizza
		it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.output.RispostaVerifica risposta = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.output.RispostaVerifica();
		risposta.setEsito(esito);
		// Genera oggetto DatiAppuntamento
		it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.output.RispostaVerifica.DatiAppuntamento datiAppuntamento = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.output.RispostaVerifica.DatiAppuntamento();

		// Ottiene i valori dal result set e li aggiunge ai dati appuntamento
		String dataAppuntamento = data.getString("dataSlot");
		String oraAppuntamento = data.getString("oraSlot");
		String idCPI = data.getString("codcpi");
		String denominazioneCPI = data.getString("descCpi");
		String indirizzoCPI = data.getString("strindirizzo");
		String indirizzoCPIStampa = data.getString("strindirizzostampa");
		String siglaOperatore = data.getString("strsiglaoperatore");
		String ambiente = data.getString("ambiente");
		String codStato = data.getString("codstatoappuntamento");
		Integer numMinuti = data.getInt("numminuti");

		datiAppuntamento.setDataAppuntamento(it.eng.sil.coop.webservices.myportal.servizicittadino.utils.DateUtils
				.stringToGregorianDate(dataAppuntamento));
		datiAppuntamento.setOraAppuntamento(oraAppuntamento);
		datiAppuntamento.setIdCPI(idCPI);
		datiAppuntamento.setDenominazioneCPI(denominazioneCPI);
		datiAppuntamento.setIndirizzoCPI(indirizzoCPI);
		datiAppuntamento.setIndirizzoCPIstampa(indirizzoCPIStampa);
		if (ambiente != null && !ambiente.equals("")) {
			datiAppuntamento.setAmbiente(ambiente);
		}
		if (siglaOperatore != null && !siglaOperatore.equals("")) {
			datiAppuntamento.setSiglaOperatore(siglaOperatore);
		}
		datiAppuntamento.setCodStato(codStato);
		datiAppuntamento.setNumMinuti(numMinuti);

		// Aggiunge alla risposta i dati appuntamento
		risposta.setDatiAppuntamento(datiAppuntamento);

		writer = new StringWriter();
		try {
			jaxbContext = JAXBContext.newInstance(
					it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.output.RispostaVerifica.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione output XML", e1);
			throw new Exception(e1);
		}
		return writer.toString();
	}

	public String verificaAppuntamento(String inputXml) {
		// parametri input
		String codiceFiscale = null;
		String utenteServizio = null;
		String codiceRichiesta = null;
		String dataAppuntamento = null;
		String oraAppuntamento = null;
		String idCPI = null;
		String idProvincia = null;

		// risposta del servizio
		String outputXml = null;

		// oggetti per la connection
		DataConnection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet dataResult = null;

		try {
			it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.input.VerificaApp verificaApp = null;
			JAXBContext jaxbContext = null;

			// VALIDAZIONE INPUT
			boolean inputXmlIsValid = XmlUtils.isXmlValid(inputXml, input_verifica_SchemaFile);
			if (!inputXmlIsValid) {
				_logger.error("Errore validazione xml input");
				outputXml = createXmlRispostaErroreVerifica(Constants.ESITO.ERRORE_VALIDAZIONE_INPUT,
						Constants.ESITO.DESC_ERRORE_VALIDAZIONE_INPUT);
				return outputXml;
			}

			try {
				jaxbContext = JAXBContext.newInstance(
						it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.input.VerificaApp.class);
				verificaApp = (it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.input.VerificaApp) jaxbContext
						.createUnmarshaller().unmarshal(new StringReader(inputXml));
			} catch (JAXBException e) {
				_logger.error("Errore parsing input xml: " + inputXml, e);
				// outputXml = createXmlRispostaErroreGenericoVerifica();
				outputXml = createXmlRispostaErroreVerifica(Constants.ESITO.ERRORE_GENERICO,
						Constants.ESITO.DESC_ERRORE_GENERICO);
				return outputXml;
			}
			// Recupero parametri della richiesta
			codiceFiscale = verificaApp.getCodiceFiscale();
			utenteServizio = verificaApp.getUtenteServizio();
			if (verificaApp.getDatiAppuntamento() != null) {
				codiceRichiesta = verificaApp.getDatiAppuntamento().getCodiceRichiesta();
				dataAppuntamento = DateUtils
						.formatXMLGregorian(verificaApp.getDatiAppuntamento().getDataAppuntamento());
				oraAppuntamento = verificaApp.getDatiAppuntamento().getOraAppuntamento();
				idCPI = verificaApp.getDatiAppuntamento().getIdCPI();
				idProvincia = verificaApp.getDatiAppuntamento().getIdProvincia();
			}

			// Ottiene la connessione al DB
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			conn = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			// Controllo coerenza CPI
			// DA RIPRISTINARE
			/*
			 * if(!controlloCoerenzaSIL(conn,idProvincia)){
			 * _logger.error("Codice CPI nell'XML differente da quello del sistema"); outputXml =
			 * createXmlRispostaErroreVerifica(Constants.ESITO.ERRORE_COERENZA_PROVINCIA_XML,Constants.ESITO.
			 * DESC_ERRORE_COERENZA_PROVINCIA_XML); return outputXml; }
			 */

			// Ottengo il servizio utilizzato
			String queryServizio = "select strvalore from ts_config_loc where codtipoconfig = '" + codiceRichiesta
					+ "' "
					+ "and strcodrif = (select codprovinciasil from ts_generale where prggenerale = 1) and num = 0";
			preparedStatement = conn.getInternalConnection().prepareStatement(queryServizio);
			dataResult = preparedStatement.executeQuery();
			if (!dataResult.next()) {
				_logger.error("Servizio mancante");
				outputXml = createXmlRispostaErroreVerifica(Constants.ESITO.SERVIZIO_MANCANTE,
						Constants.ESITO.DESC_SERVIZIO_MANCANTE);
			}

			// TODO: gestione solo utente Lavoratore
			if (utenteServizio.equals(Constants.UTENTE_SERVIZIO_LAVORATORE)) {
				String query = Constants.QUERY.VERIFICA_APPUNTAMENTO_QUERY;
				preparedStatement = conn.getInternalConnection().prepareStatement(query);
				preparedStatement.setString(1, codiceFiscale);
				preparedStatement.setString(2, dataAppuntamento);
				preparedStatement.setString(3, oraAppuntamento);
				preparedStatement.setString(4, idCPI);
				preparedStatement.setString(5, codiceRichiesta);
				_logger.debug("Verifica appuntamento. Query:  " + query);
				_logger.debug("Verifica appuntamento. Parametri:  " + codiceFiscale + ", " + dataAppuntamento + ", "
						+ oraAppuntamento + ", " + idCPI + ", " + codiceRichiesta);
				dataResult = preparedStatement.executeQuery();
			}
			if (dataResult.next()) {
				outputXml = createXmlRispostaVerificaOk(dataResult);
				if (dataResult.next()) {
					_logger.error("Presenza di appuntamenti multipli");
					outputXml = createXmlRispostaErroreVerifica(Constants.ESITO.APPUNTAMENTI_MULTIPLI,
							Constants.ESITO.DESC_APPUNTAMENTI_MULTIPLI);
				}
				// VALIDAZIONE OUTPUT
				boolean outputXmlIsValid = XmlUtils.isXmlValid(outputXml, output_verifica_SchemaFile);
				if (!outputXmlIsValid) {
					_logger.error("Errore validazione xml output");
					outputXml = createXmlRispostaErroreVerifica(Constants.ESITO.ERRORE_GENERICO,
							Constants.ESITO.DESC_ERRORE_GENERICO);
				} else {
					_logger.info("Operazione verificaAppuntamento ha restituito l'appuntamento cercato");
				}
			} else {
				// Nessun appuntamento trovato
				_logger.info("Appuntamento ricercato non presente");
				outputXml = createXmlRispostaErroreVerifica(Constants.ESITO.APPUNTAMENTO_NON_PRESENTE,
						Constants.ESITO.DESC_VERIFICA_APPUNTAMENTO_NON_PRESENTE);
			}
		} catch (Exception e) {
			_logger.error("Eccezione: trova disponibilità appuntamento ", e);
			try {
				outputXml = createXmlRispostaErroreVerifica(Constants.ESITO.ERRORE_GENERICO,
						Constants.ESITO.DESC_ERRORE_GENERICO);
			} catch (Exception eg) {
				_logger.debug("Errore in fase di generazione di una risposta con errore ", eg);
			}
		} finally {
			try {
				if (dataResult != null) {
					dataResult.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException eSql) {
				_logger.error("Eccezione: errore chiusura ResultSet o  PreparedStatement", eSql);
			}
			Utils.releaseResources(conn, null, null);
		}
		return outputXml;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private String createXmlRispostaErroreAnnulla(String codErrore, String descrizioneErrore)
			throws UnsupportedEncodingException {
		String outputXml = "";

		JAXBContext jaxbContext;
		StringWriter writer = new StringWriter();

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla.Esito esito = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla.Esito();
		esito.setCodice(codErrore);
		esito.setDescrizione(correctUTF8(descrizioneErrore));

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla risposta = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla();
		risposta.setEsito(esito);

		try {
			jaxbContext = JAXBContext.newInstance(
					it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione input XML", e1);
		}

		outputXml = writer.toString();

		return outputXml;
	}

	private String createXmlRispostaStatoNonAttivo(String codice) throws UnsupportedEncodingException {
		String outputXml = "";

		JAXBContext jaxbContext;
		StringWriter writer = new StringWriter();

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla.Esito esito = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla.Esito();
		esito.setCodice(Constants.ESITO.APPUNTAMENTO_STATO_NON_ATTIVO);
		esito.setDescrizione(correctUTF8(Constants.ESITO.DESC_APPUNTAMENTO_STATO_NON_ATTIVO));
		if (codice != null) {
			esito.setCodice(codice);
		}

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla risposta = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla();
		risposta.setEsito(esito);

		try {
			jaxbContext = JAXBContext.newInstance(
					it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione input XML", e1);
		}

		outputXml = writer.toString();

		return outputXml;
	}

	private String createXmlRispostaAnnullaOK(String codStato)
			throws SQLException, ParseException, DatatypeConfigurationException, Exception {
		StringWriter writer = null;
		JAXBContext jaxbContext = null;

		// Genera oggetto Esito e lo valorizza
		it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla.Esito esito = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla.Esito();
		esito.setCodice(Constants.ESITO.OK);
		esito.setDescrizione(correctUTF8(Constants.ESITO.DESC_OK_ANNULLA));
		if (codStato != null)
			esito.setCodStato(codStato);
		// Genera oggetto Risposta e lo valorizza
		it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla risposta = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla();
		risposta.setEsito(esito);

		writer = new StringWriter();
		try {
			jaxbContext = JAXBContext.newInstance(
					it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.output.RispostaAnnulla.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione output XML", e1);
			throw new Exception(e1);
		}
		return writer.toString();
	}

	private boolean allineaSlot(DataConnection conn, String codCPI, BigDecimal prgAppuntamento, String codVecchioStato)
			throws Exception {
		StoredProcedureCommand command = null;
		DataResult dr = null;
		int paramIndex = 0;

		try {
			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand(SQLStatements.getStatement("ALLINEA_SLOT"));

			ArrayList parameters = new ArrayList(8);
			// Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("codParCpi", Types.VARCHAR, codCPI));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("prgParAppuntamento", Types.BIGINT, prgAppuntamento));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("nParAllineaAppuntamento", Types.INTEGER, null));// ??
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("codParVecchioStato", Types.VARCHAR, codVecchioStato));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT, Constants.COD_UT)); // Utente sil ws
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flagCancella", Types.INTEGER, null)); // ??
			command.setAsInputParameters(paramIndex++);

			// parametri di Output
			parameters.add(conn.createDataField("ErrCodeOut", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Reperisco i valori di output della stored
			// 1. Codice di Ritorno
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			// String codiceRit = (String) df.getObjectValue();
			String codiceRit = df.getStringValue();
			// 2. ErrCodeOut
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			// String ErrCodeOut = (String) df.getObjectValue();
			String ErrCodeOut = df.getStringValue();
			if (codiceRit.equals("0") && ErrCodeOut == null)
				return true;
			return false;
		} catch (Exception e) {
			return false;
		} finally {
			Utils.releaseResources(null, command, dr);
		}
	}

	public String annullaAppuntamento(String inputXml) {

		// parametri input
		String codiceFiscale = null;
		String utenteServizio = null;
		String codiceRichiesta = null;
		String dataAppuntamento = null;
		String oraAppuntamento = null;
		String idCPI = null;
		String idProvincia = null;
		// parametri input nuovo anpal
		String idAppuntamentoAR = null;
		String idAppuntamentoCoap = null;

		// risposta del servizio
		String outputXml = null;

		// oggetti per la connection
		DataConnection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet dataResult = null;

		try {
			// it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.input.AnnullaApp verificaApp = null;
			it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.input.anpal.AnnullaApp verificaAppAnpal = null;
			JAXBContext jaxbContext = null;

			// VALIDAZIONE INPUT
			// boolean inputXmlIsValid = XmlUtils.isXmlValid(inputXml, input_annulla_SchemaFile );
			boolean inputXmlIsValid = XmlUtils.isXmlValid(inputXml, input_annulla_anpal_SchemaFile);
			boolean isInputAnpal = false;
			if (!inputXmlIsValid) {
				_logger.error("Errore validazione xml input");
				outputXml = createXmlRispostaErroreAnnulla(Constants.ESITO.ERRORE_VALIDAZIONE_INPUT,
						Constants.ESITO.DESC_ERRORE_VALIDAZIONE_INPUT);
				return outputXml;
			}

			// Parsing input
			try {
				// if(isInputAnpal){
				jaxbContext = JAXBContext.newInstance(
						it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.input.anpal.AnnullaApp.class);
				verificaAppAnpal = (it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.input.anpal.AnnullaApp) jaxbContext
						.createUnmarshaller().unmarshal(new StringReader(inputXml));
				/*
				 * }else{ jaxbContext =
				 * JAXBContext.newInstance(it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.input.AnnullaApp.
				 * class); verificaApp = (it.eng.sil.coop.webservices.agenda.appuntamento.xml.annulla.input.AnnullaApp)
				 * jaxbContext.createUnmarshaller().unmarshal(new StringReader(inputXml)); }
				 */
			} catch (JAXBException e) {
				_logger.error("Errore parsing input xml: " + inputXml, e);
				// outputXml = createXmlRispostaErroreGenericoAnnulla();;
				outputXml = createXmlRispostaErroreAnnulla(Constants.ESITO.ERRORE_GENERICO,
						Constants.ESITO.DESC_ERRORE_GENERICO);
				return outputXml;
			}

			// Recupero parametri della richiesta
			if (verificaAppAnpal.getCodiceFiscale() == null && verificaAppAnpal.getIdAppuntamentoAR() != null
					&& verificaAppAnpal.getIdAppuntamentoCoap() != null) {
				isInputAnpal = true;
			}

			if (isInputAnpal) {
				idAppuntamentoAR = verificaAppAnpal.getIdAppuntamentoAR();
				idAppuntamentoCoap = verificaAppAnpal.getIdAppuntamentoCoap();
				idCPI = verificaAppAnpal.getIdCPI();
			} else {
				codiceFiscale = verificaAppAnpal.getCodiceFiscale();
				utenteServizio = verificaAppAnpal.getUtenteServizio();
				if (verificaAppAnpal.getDatiAppuntamento() != null) {
					codiceRichiesta = verificaAppAnpal.getDatiAppuntamento().getCodiceRichiesta();
					dataAppuntamento = DateUtils
							.formatXMLGregorian(verificaAppAnpal.getDatiAppuntamento().getDataAppuntamento());
					oraAppuntamento = verificaAppAnpal.getDatiAppuntamento().getOraAppuntamento();
					idCPI = verificaAppAnpal.getDatiAppuntamento().getIdCPI();
					idProvincia = verificaAppAnpal.getDatiAppuntamento().getIdProvincia();
				}
			}

			// Ottiene la connessione al DB
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			conn = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			// Controllo coerenza CPI
			// DA RIPRISTINARE
			/*
			 * if(!controlloCoerenzaSIL(conn,idProvincia)){
			 * _logger.error("Codice CPI nell'XML differente da quello del sistema"); outputXml =
			 * createXmlRispostaErroreAnnulla(Constants.ESITO.ERRORE_COERENZA_PROVINCIA_XML,Constants.ESITO.
			 * DESC_ERRORE_COERENZA_PROVINCIA_XML); return outputXml; }
			 */

			if (!isInputAnpal) {
				// Controlla che il servizio non sia mancante
				String queryServizio = "select strvalore from ts_config_loc where codtipoconfig = '" + codiceRichiesta
						+ "' "
						+ "and strcodrif = (select codprovinciasil from ts_generale where prggenerale = 1) and num = 0";
				preparedStatement = conn.getInternalConnection().prepareStatement(queryServizio);
				dataResult = preparedStatement.executeQuery();
				if (!dataResult.next()) {
					_logger.error("Servizio Mancante per provincia: " + idProvincia + " e codice richiesta: "
							+ codiceRichiesta);
					outputXml = createXmlRispostaErroreAnnulla(Constants.ESITO.SERVIZIO_MANCANTE,
							Constants.ESITO.DESC_SERVIZIO_MANCANTE);
				}

				// TODO: gestione solo utente Lavoratore
				if (utenteServizio.equals("L")) {
					// Controlla presenza appuntamento
					String query = Constants.QUERY.SEARCH_APPUNTAMENTO_QUERY;
					preparedStatement = conn.getInternalConnection().prepareStatement(query);
					preparedStatement.setString(1, codiceFiscale);
					preparedStatement.setString(2, dataAppuntamento);
					preparedStatement.setString(3, oraAppuntamento);
					preparedStatement.setString(4, idCPI);
					preparedStatement.setString(5, codiceRichiesta);
					_logger.debug("Ricerca appuntamento. Query:  " + query);
					_logger.debug("Ricerca appuntamento. Parametri:  " + codiceFiscale + ", " + dataAppuntamento + ", "
							+ oraAppuntamento + ", " + idCPI + ", " + codiceRichiesta);
					dataResult = preparedStatement.executeQuery();
				}
				if (dataResult.next()) {
					BigDecimal prgApp = new BigDecimal(dataResult.getString("PRGAPPUNTAMENTO"));
					String codCpi = dataResult.getString("codcpi");
					String codStato = dataResult.getString("codstatoappuntamento");
					BigDecimal numklo = new BigDecimal(dataResult.getString("numkloagenda"));
					// Controlla validita flag
					if (dataResult.getString("flgattivo").equals("S")) {
						if (dataResult.next()) { // Presenza di risultati multipli
							// outputXml = createXmlRispostaAppuntamentiMultipliAnnulla();
							outputXml = createXmlRispostaErroreAnnulla(Constants.ESITO.APPUNTAMENTI_MULTIPLI,
									Constants.ESITO.DESC_APPUNTAMENTI_MULTIPLI);
						} else {
							// Controlla se possibile annullare l'appuntamento
							queryServizio = "select strvalore from ts_config_loc where codtipoconfig = '"
									+ codiceRichiesta + "' "
									+ "and strcodrif = (select codprovinciasil from ts_generale where prggenerale = 1) and num = 9";
							preparedStatement = conn.getInternalConnection().prepareStatement(queryServizio);
							dataResult = preparedStatement.executeQuery();
							if (dataResult.next()) {
								// Ottiene l'offset di giorni relativo alla cancellazione
								int offset = Integer.parseInt(dataResult.getString("strvalore"));

								DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
								// Differenza tra data odierna e ultima data utile per l'annullamento
								Date dateApp = format.parse(dataAppuntamento);
								Date dateNow = new Date();
								Calendar cal = Calendar.getInstance();
								cal.setTime(dateApp);
								cal.add(Calendar.DATE, -(offset));
								dateApp = cal.getTime();

								if ((dateApp.getTime() - dateNow.getTime()) >= 0) {
									// Possibile annullare, annulla l'appuntamento

									// Aggiornamento in transazione con l'allineamento degli slot
									DataConnection connNew = null;
									TransactionQueryExecutor trans = null;
									try {
										connNew = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
										trans = new TransactionQueryExecutor(connNew, null, null);
										trans.initTransaction();

										String query = Constants.QUERY.DELETE_APPUNTAMENTO_QUERY;
										preparedStatement = connNew.getInternalConnection().prepareStatement(query);
										preparedStatement.setBigDecimal(1, numklo.add(new BigDecimal(1)));
										preparedStatement.setString(2, Constants.COD_ANNULLA);
										preparedStatement.setString(3, Constants.COD_UT);
										preparedStatement.setString(4, codCpi);
										preparedStatement.setBigDecimal(5, prgApp);
										_logger.debug("Eliminazione Appuntamento. Query:  " + query);
										_logger.debug("Eliminazione Appuntamento. Parametri:  " + "numklo" + ", "
												+ Constants.COD_ANNULLA + ", " + Constants.COD_UT + ", " + codCpi + ", "
												+ prgApp);
										dataResult = preparedStatement.executeQuery();

										if (allineaSlot(connNew, codCpi, prgApp, codStato)) {
											outputXml = createXmlRispostaAnnullaOK(Constants.COD_ANNULLA);

											// VALIDAZIONE OUTPUT
											boolean outputXmlIsValid = XmlUtils.isXmlValid(outputXml,
													output_annulla_SchemaFile);
											if (!outputXmlIsValid) {
												trans.rollBackTransaction();
												trans = null;
												_logger.error("Validazione XML Output fallita: " + outputXml);
												outputXml = createXmlRispostaErroreAnnulla(
														Constants.ESITO.ERRORE_GENERICO,
														Constants.ESITO.DESC_ERRORE_GENERICO);
											} else {
												trans.commitTransaction();
												trans = null;
												_logger.info("Operazione annullaAppuntamento andata a buon fine");
											}
										} else {
											trans.rollBackTransaction();
											trans = null;
											_logger.error("Allineamento slot fallito con parametri: " + codCpi + ","
													+ prgApp + ", " + codStato);
											throw new Exception("Errore nell'allineamento slot");
										}
									} catch (Exception e) {
										if (trans != null) {
											trans.rollBackTransaction();
										}
										throw new Exception("Eccezione, impossibile annullare appuntamento");
									}

								} else {
									// Non possibile annullare
									_logger.info("Appuntamento non annullabile");
									outputXml = createXmlRispostaErroreAnnulla(
											Constants.ESITO.APPUNTAMENTO_NON_ANNULLABILE,
											Constants.ESITO.DESC_APPUNTAMENTO_NON_ANNULLABILE);
								}
							} else {
								// Offset giorni per annullamento non trovato
								_logger.error("Servizio Mancante per provincia: " + idProvincia
										+ " e codice richiesta: " + codiceRichiesta);
								outputXml = createXmlRispostaErroreAnnulla(Constants.ESITO.SERVIZIO_MANCANTE,
										Constants.ESITO.DESC_SERVIZIO_MANCANTE);
							}
						}
					} else {
						// Appuntamento in stato non attivo
						_logger.info("Appuntamento non in stato attivo");
						outputXml = createXmlRispostaStatoNonAttivo(null);
					}
				} else {
					// Nessun appuntamento trovato
					_logger.info("Appuntamento non trovato");
					outputXml = createXmlRispostaErroreAnnulla(Constants.ESITO.APPUNTAMENTO_NON_PRESENTE,
							Constants.ESITO.DESC_ANNULLA_APPUNTAMENTO_NON_PRESENTE);
				}
			} else {
				// Controlla presenza appuntamento anpal
				String query = Constants.QUERY.SEARCH_ANPAL_APPUNTAMENTO_QUERY;
				preparedStatement = conn.getInternalConnection().prepareStatement(query);
				preparedStatement.setString(1, idCPI);
				preparedStatement.setString(2, idAppuntamentoCoap);
				preparedStatement.setString(3, idAppuntamentoAR);
				_logger.debug("Ricerca appuntamento anpal. Query:  " + query);
				_logger.debug("Ricerca appuntamento anpal. Parametri:  " + idCPI + ", " + idAppuntamentoCoap + ", "
						+ idAppuntamentoAR);
				dataResult = preparedStatement.executeQuery();
				if (dataResult.next()) {
					BigDecimal prgApp = new BigDecimal(dataResult.getString("PRGAPPUNTAMENTO"));
					String codCpi = dataResult.getString("codcpi");
					String codStato = dataResult.getString("codstatoappuntamento");
					BigDecimal numklo = new BigDecimal(dataResult.getString("numkloagenda"));
					dataAppuntamento = dataResult.getString("dataAppuntamento");
					// Controlla validita flag
					if (dataResult.getString("flgattivo").equals("S")) {
						if (dataResult.next()) { // Presenza di risultati multipli
							// outputXml = createXmlRispostaAppuntamentiMultipliAnnulla();
							outputXml = createXmlRispostaErroreAnnulla(Constants.ESITO.APPUNTAMENTI_MULTIPLI,
									Constants.ESITO.DESC_APPUNTAMENTI_MULTIPLI);
						} else {

							String queryGiorni = "select num from ts_config_loc where codtipoconfig = 'COAP_ANN' "
									+ " and strcodrif = (select codregionesil from ts_generale )";

							preparedStatement = conn.getInternalConnection().prepareStatement(queryGiorni);
							dataResult = preparedStatement.executeQuery();
							if (dataResult.next()) {
								// Ottiene l'offset di giorni relativo alla cancellazione
								int offset = Integer.parseInt(dataResult.getString("num"));

								DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
								// Differenza tra data odierna e ultima data utile per l'annullamento
								Date dateApp = format.parse(dataAppuntamento);
								Date dateNow = new Date();
								Calendar cal = Calendar.getInstance();
								cal.setTime(dateApp);
								cal.add(Calendar.DATE, -(offset));
								dateApp = cal.getTime();

								if ((dateApp.getTime() - dateNow.getTime()) >= 0) {
									// Possibile annullare, annulla l'appuntamento

									// Aggiornamento in transazione con l'allineamento degli slot
									DataConnection connNew = null;
									TransactionQueryExecutor trans = null;
									try {
										connNew = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
										trans = new TransactionQueryExecutor(connNew, null, null);
										trans.initTransaction();

										String queryDelete = Constants.QUERY.DELETE_APPUNTAMENTO_QUERY;
										preparedStatement = connNew.getInternalConnection()
												.prepareStatement(queryDelete);
										preparedStatement.setBigDecimal(1, numklo.add(new BigDecimal(1)));
										preparedStatement.setString(2, Constants.COD_ANNULLA);
										preparedStatement.setString(3, Constants.COD_UT);
										preparedStatement.setString(4, codCpi);
										preparedStatement.setBigDecimal(5, prgApp);
										_logger.debug("Eliminazione Appuntamento. Query:  " + queryDelete);
										_logger.debug("Eliminazione Appuntamento. Parametri:  " + "numklo" + ", "
												+ Constants.COD_ANNULLA + ", " + Constants.COD_UT + ", " + codCpi + ", "
												+ prgApp);
										dataResult = preparedStatement.executeQuery();

										if (allineaSlot(connNew, codCpi, prgApp, codStato)) {
											outputXml = createXmlRispostaAnnullaOK(Constants.COD_ANNULLA);

											// VALIDAZIONE OUTPUT
											boolean outputXmlIsValid = XmlUtils.isXmlValid(outputXml,
													output_annulla_SchemaFile);
											if (!outputXmlIsValid) {
												trans.rollBackTransaction();
												trans = null;
												_logger.error("Validazione XML Output fallita: " + outputXml);
												outputXml = createXmlRispostaErroreAnnulla(
														Constants.ESITO.ERRORE_GENERICO,
														Constants.ESITO.DESC_ERRORE_GENERICO);
											} else {
												trans.commitTransaction();
												trans = null;
												_logger.info("Operazione annullaAppuntamento andata a buon fine");
											}
										} else {
											trans.rollBackTransaction();
											trans = null;
											_logger.error("Allineamento slot fallito con parametri: " + codCpi + ","
													+ prgApp + ", " + codStato);
											throw new Exception("Errore nell'allineamento slot");
										}
									} catch (Exception e) {
										if (trans != null) {
											trans.rollBackTransaction();
										}
										throw new Exception("Eccezione, impossibile annullare appuntamento");
									}

								} else {
									// Non possibile annullare
									_logger.info("Appuntamento non annullabile");
									outputXml = createXmlRispostaErroreAnnulla(
											Constants.ESITO.APPUNTAMENTO_NON_ANNULLABILE,
											Constants.ESITO.DESC_APPUNTAMENTO_NON_ANNULLABILE);
								}
							} else {
								// Offset giorni per annullamento non trovato
								_logger.error("Servizio Mancante per codice richiesta: COAP_ANN");
								outputXml = createXmlRispostaErroreAnnulla(Constants.ESITO.SERVIZIO_MANCANTE,
										Constants.ESITO.DESC_SERVIZIO_MANCANTE);
							}
						}
					} else {
						// Appuntamento in stato non attivo
						_logger.info("Appuntamento non in stato attivo");
						outputXml = createXmlRispostaStatoNonAttivo(null);
					}
				} else {
					// Nessun appuntamento trovato
					_logger.info("Appuntamento non trovato");
					outputXml = createXmlRispostaErroreAnnulla(Constants.ESITO.APPUNTAMENTO_NON_PRESENTE,
							Constants.ESITO.DESC_ANNULLA_APPUNTAMENTO_NON_PRESENTE);
				}

			}

		} catch (Exception e) {
			_logger.error("Eccezione, impossibile annullare appuntamento ", e);
			try {
				if (conn != null)
					conn.rollBackTransaction();
				outputXml = createXmlRispostaErroreAnnulla(Constants.ESITO.ERRORE_GENERICO,
						Constants.ESITO.DESC_ERRORE_GENERICO);
			} catch (Exception eg) {
				_logger.debug("Errore in fase di generazione di una risposta con errore ", eg);
			}
		} finally {
			try {
				if (dataResult != null) {
					dataResult.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException eSql) {
				_logger.error("Eccezione: errore chiusura ResultSet o  PreparedStatement", eSql);
			}
			Utils.releaseResources(conn, null, null);
		}
		return outputXml;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private String createXmlRispostaErrorePrenotazioni(String codErrore, String descrizioneErrore)
			throws UnsupportedEncodingException {
		String outputXml = "";

		JAXBContext jaxbContext;
		StringWriter writer = new StringWriter();

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni.Esito esito = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni.Esito();
		esito.setCodice(codErrore);
		esito.setDescrizione(correctUTF8(descrizioneErrore));

		it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni risposta = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni();
		risposta.setEsito(esito);

		try {
			jaxbContext = JAXBContext.newInstance(
					it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione input XML", e1);
		}

		outputXml = writer.toString();

		return outputXml;
	}

	private String createXmlRispostaPrenotazioniOK(ResultSet pResultSet, String codiceFiscale, boolean isReturnIdApp)
			throws SQLException, ParseException, DatatypeConfigurationException, Exception {
		StringWriter writer = null;
		JAXBContext jaxbContext = null;

		// Genera oggetto Esito e lo valorizza
		it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni.Esito esito = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni.Esito();
		esito.setCodice(Constants.ESITO.OK);
		esito.setDescrizione(correctUTF8(Constants.ESITO.DESC_OK_PRENOTAZIONI));

		// Genera oggetto Risposta e lo valorizza
		it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni risposta = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni();
		risposta.setEsito(esito);

		// Se vi sono appuntamenti
		if (pResultSet.isBeforeFirst()) {
			it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni.ElencoPrenotazioni elencoPrenotazioni = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni.ElencoPrenotazioni();
			// Li scorrere inserendoli nella risposta
			while (pResultSet.next()) {
				it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni.ElencoPrenotazioni.DatiAppuntamento datiAppuntamento = new it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni.ElencoPrenotazioni.DatiAppuntamento();
				datiAppuntamento.setIdProvincia(pResultSet.getString("codprovincia"));
				datiAppuntamento
						.setDataAppuntamento(it.eng.sil.coop.webservices.myportal.servizicittadino.utils.DateUtils
								.stringToGregorianDate(pResultSet.getString("dataSlot")));
				datiAppuntamento.setOraAppuntamento(pResultSet.getString("oraSlot"));
				datiAppuntamento.setIdCPI(pResultSet.getString("codcpi"));
				datiAppuntamento.setDenominazioneCPI(pResultSet.getString("strdescrizione"));
				datiAppuntamento.setIndirizzoCPI(pResultSet.getString("strindirizzo"));
				datiAppuntamento.setIndirizzoCPIstampa(pResultSet.getString("strIndirizzoStampa"));
				String optionalValue = pResultSet.getString("strSiglaOperatore");
				if (optionalValue != null)
					datiAppuntamento.setSiglaOperatore(optionalValue);
				optionalValue = pResultSet.getString("ambiente");
				if (optionalValue != null)
					datiAppuntamento.setAmbiente(optionalValue);
				datiAppuntamento.setCodStato(pResultSet.getString("codstatoappuntamento"));
				datiAppuntamento.setNumMinuti(BigInteger.valueOf(pResultSet.getInt("numMinuti")));
				optionalValue = pResultSet.getString("strTelMobileRif");
				if (optionalValue != null)
					datiAppuntamento.setCellRif(optionalValue);
				optionalValue = pResultSet.getString("strEmailRif");
				if (optionalValue != null)
					datiAppuntamento.setEmailRif(optionalValue);
				optionalValue = pResultSet.getString("codtipoconfig");
				if (optionalValue != null)
					datiAppuntamento.setCodicePrestazione(optionalValue);
				else
					datiAppuntamento.setCodicePrestazione("ALTRO");
				datiAppuntamento.setCodiceFiscale(codiceFiscale);

				if (isReturnIdApp) {
					if (pResultSet.getBigDecimal("prgappuntamento") != null)
						datiAppuntamento.setIdAppuntamento(pResultSet.getBigDecimal("prgappuntamento").toString());
				}

				elencoPrenotazioni.getDatiAppuntamento().add(datiAppuntamento);
			}
			risposta.setElencoPrenotazioni(elencoPrenotazioni);
		}

		writer = new StringWriter();
		try {
			jaxbContext = JAXBContext.newInstance(
					it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.output.RispostaGetPrenotazioni.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione output XML", e1);
			throw new Exception(e1);
		}
		return writer.toString();
	}

	public String getPrenotazioni(String inputXml) throws UnsupportedEncodingException {

		// parametri input
		String codiceFiscale = null;
		String utenteServizio = null;
		String idProvincia = null;
		boolean isReturnIdApp = false;

		String parametroRisultati = null;

		// risposta del servizio
		String outputXml = null;

		// oggetti per la connection
		DataConnection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet dataResult = null;

		try {
			it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.input.GetPrenotazioni getPrenotazioni = null;
			JAXBContext jaxbContext = null;

			// VALIDAZIONE INPUT
			boolean inputXmlIsValid = XmlUtils.isXmlValid(inputXml, input_prenotazioni_SchemaFile);
			if (!inputXmlIsValid) {
				_logger.error("Errore validazione xml");
				outputXml = createXmlRispostaErrorePrenotazioni(Constants.ESITO.ERRORE_VALIDAZIONE_INPUT,
						Constants.ESITO.DESC_ERRORE_VALIDAZIONE_INPUT);
				return outputXml;
			}

			// Parsing input
			try {
				jaxbContext = JAXBContext.newInstance(
						it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.input.GetPrenotazioni.class);
				getPrenotazioni = (it.eng.sil.coop.webservices.agenda.appuntamento.xml.prenotazioni.input.GetPrenotazioni) jaxbContext
						.createUnmarshaller().unmarshal(new StringReader(inputXml));
			} catch (JAXBException e) {
				_logger.error("Errore parsing input xml: " + inputXml, e);
				outputXml = createXmlRispostaErrorePrenotazioni(Constants.ESITO.ERRORE_GENERICO,
						Constants.ESITO.DESC_ERRORE_GENERICO);
				return outputXml;
			}

			// Recupero parametri della richiesta
			codiceFiscale = getPrenotazioni.getCodiceFiscale();
			utenteServizio = getPrenotazioni.getUtenteServizio();
			idProvincia = getPrenotazioni.getIdProvincia();

			isReturnIdApp = getPrenotazioni.isReturnIdApp() != null ? getPrenotazioni.isReturnIdApp() : false;

			// Ottiene la connessione al DB
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			conn = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			// Controllo coerenza CPI
			// DA RIPRISTINARE
			/*
			 * if(!controlloCoerenzaSIL(conn,idProvincia)){
			 * _logger.error("Codice CPI nell'XML differente da quello del sistema"); outputXml =
			 * createXmlRispostaErrorePrenotazioni(Constants.ESITO.ERRORE_COERENZA_PROVINCIA_XML,Constants.ESITO.
			 * DESC_ERRORE_COERENZA_PROVINCIA_XML); return outputXml; }
			 */

			// Controlla che il servizio non sia mancante
			String queryServizio = "select strvalore from ts_config_loc where codtipoconfig = 'AP_ALTRO' "
					+ "and strcodrif = (select codprovinciasil from ts_generale where prggenerale = 1)";
			preparedStatement = conn.getInternalConnection().prepareStatement(queryServizio);
			dataResult = preparedStatement.executeQuery();
			if (dataResult.next()) {
				parametroRisultati = dataResult.getString(1);
			}

			// TODO: gestione solo utente Lavoratore
			if (utenteServizio.equals("L")) {
				String query = Constants.QUERY.GET_PRENOTAZIONI_QUERY;
				if ((parametroRisultati != null) && (parametroRisultati.equals("T"))) {
					preparedStatement = conn.getInternalConnection().prepareStatement(query);
					preparedStatement.setString(1, codiceFiscale);
				} else {
					query = query + Constants.QUERY.GET_PRENOTAZIONI_QUERY_ON_LINE;
					preparedStatement = conn.getInternalConnection().prepareStatement(query);
					preparedStatement.setString(1, codiceFiscale);
				}

				_logger.debug("Get prenotazioni. Query:  " + query);
				_logger.debug(
						"Get prenotazioni. Parametri:  " + codiceFiscale + ", " + idProvincia + ", " + idProvincia);
				dataResult = preparedStatement.executeQuery();
				outputXml = createXmlRispostaPrenotazioniOK(dataResult, codiceFiscale, isReturnIdApp);
			}

			// VALIDAZIONE OUTPUT
			boolean outputXmlIsValid = XmlUtils.isXmlValid(outputXml, output_prenotazioni_SchemaFile);
			if (!outputXmlIsValid) {
				_logger.error("Errore parsing output xml: " + outputXml);
				outputXml = createXmlRispostaErroreVerifica(Constants.ESITO.ERRORE_GENERICO,
						Constants.ESITO.DESC_ERRORE_GENERICO);
			} else {
				_logger.info("Operazione getPrenotazioni ha restituito gli appuntamenti dell'utente");
			}

		} catch (Exception e) {
			_logger.error("Eccezione in metodo getPrenotazioni ", e);
			outputXml = createXmlRispostaErrorePrenotazioni(Constants.ESITO.ERRORE_GENERICO,
					Constants.ESITO.DESC_ERRORE_GENERICO);
		} finally {
			try {
				if (dataResult != null) {
					dataResult.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException eSql) {
				_logger.error("Eccezione: errore chiusura ResultSet o  PreparedStatement", eSql);
			}
			Utils.releaseResources(conn, null, null);
		}
		return outputXml;
	}

	private String checkRei(DataConnection conn, String codiceRichiesta) throws Exception {
		PreparedStatement preparedStatementConfig = null;
		ResultSet resultSetConfig = null;
		String valore = null;
		try {
			// REI
			String query = "select strvalore from ts_config_loc where codtipoconfig = '" + codiceRichiesta + "' "
					+ "and strcodrif = (select codprovinciasil from ts_generale where prggenerale = 1) and num = 10";
			preparedStatementConfig = conn.getInternalConnection().prepareStatement(query);
			resultSetConfig = preparedStatementConfig.executeQuery();

			if (resultSetConfig != null && resultSetConfig.next()) {
				valore = resultSetConfig.getString("strvalore");
			}
		} catch (Exception e) {
			_logger.debug("Errore in fase di recupero parametri di configurazione con config = " + codiceRichiesta, e);
			throw new Exception("Parametro di configurazione mancante");
		} finally {
			if (resultSetConfig != null) {
				resultSetConfig.close();
			}
			if (preparedStatementConfig != null) {
				preparedStatementConfig.close();
			}
		}
		return valore;
	}

}
