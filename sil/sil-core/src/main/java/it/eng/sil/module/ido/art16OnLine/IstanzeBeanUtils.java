package it.eng.sil.module.ido.art16OnLine;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.webservices.agenda.appuntamento.Constants;
import it.eng.sil.coop.webservices.agenda.appuntamento.EsitoInserimentoLavoratore;
import it.eng.sil.coop.webservices.agenda.appuntamento.LavoratoreUtils;
import it.eng.sil.coop.webservices.art16online.istanze.AnagraficaType;
import it.eng.sil.coop.webservices.art16online.istanze.AnagraficaTypeSesso;
import it.eng.sil.coop.webservices.art16online.istanze.CandidaturaType;
import it.eng.sil.coop.webservices.art16online.istanze.ContattiType;
import it.eng.sil.coop.webservices.art16online.istanze.EsitoType;
import it.eng.sil.coop.webservices.art16online.istanze.ExtraUEType;
import it.eng.sil.coop.webservices.art16online.istanze.ISEEType;
import it.eng.sil.coop.webservices.art16online.istanze.IstanzaArt16Type;
import it.eng.sil.coop.webservices.art16online.istanze.IstanzaType;
import it.eng.sil.coop.webservices.art16online.istanze.ResidenzaType;
import it.eng.sil.coop.webservices.art16online.istanze.ResponseIstanzaArt16;
import it.eng.sil.coop.webservices.art16online.istanze.xsd.types.IstanzaArt16Type.ListaCandidature;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.xml.XMLValidator;
import oracle.sql.BLOB;

public class IstanzeBeanUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(IstanzeBeanUtils.class.getName());

	public static final String COD_NULL_DEFAULT = new String("NT");
	public static final String ELABORAZIONE_IN_CORSO = new String("I");
	public static final String ELABORAZIONE_TERMINATA = new String("T");

	File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "articolo16OnLine" + File.separator + "response_istanze_art16.xsd");

	private it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResponseIstanzaArt16 responseXsd;

	public BigDecimal gestioneIstanza(ResponseIstanzaArt16 response, BigDecimal cdnUtente, String prgRosa,
			String prgRichiestaAz, MultipleTransactionQueryExecutor transExec) throws Exception {
		if (response != null) {
			try {
				transExec.initTransaction();
				BigDecimal prgIstanza = getPrgCaricoIstanza(transExec);

				int numeroCandidati = response.getIstanzaArt16().getListaCandidature().length;

				this.responseXsd = getResponseXsd(response);

				String xmlResponse = getXmlResponse(responseXsd);

				String validityErrors = XMLValidator.getValidityErrors(xmlResponse, schemaFile);
				if (validityErrors != null && validityErrors.length() > 0) {
					String validityError = "Errore di validazione xml ResponseIstanzaArt16: " + validityErrors;
					_logger.error(validityError);
					return null;
				}
				// INSERIMENTO DO_CARICA_ISTANZA
				Object[] parameters = new Object[6];
				parameters[0] = prgIstanza;
				parameters[1] = new BigDecimal(numeroCandidati);
				parameters[2] = ELABORAZIONE_IN_CORSO;
				parameters[3] = cdnUtente;
				parameters[4] = cdnUtente;
				parameters[5] = new BigDecimal(prgRosa);
				boolean success = ((Boolean) transExec.executeQuery("INSERT_DO_CARICA_ISTANZA", parameters, "INSERT"))
						.booleanValue();

				saveBLOB(transExec.getDataConnection(), prgIstanza, xmlResponse);

				// UPDATE IDO DTMASONLINE
				parameters = new Object[2];
				parameters[0] = cdnUtente;
				parameters[1] = new BigDecimal(prgRichiestaAz);
				success = success && ((Boolean) transExec.executeQuery("UPDATE_IDO_DTMASONLINE", parameters, "UPDATE"))
						.booleanValue();
				if (success) {
					transExec.commitTransaction();
				} else {
					transExec.rollBackTransaction();
					return null;
				}
				return prgIstanza;

			} catch (EMFInternalError e) {
				_logger.fatal("EMFInternalError", e);
				throw e;
			} catch (JAXBException e) {
				_logger.fatal("JAXBException", e);
				throw e;
			} catch (DatatypeConfigurationException e) {
				_logger.fatal("DatatypeConfigurationException", e);
				throw e;
			} catch (ParseException e) {
				_logger.fatal("ParseException", e);
				throw e;
			}

		} else {
			return null;
		}
	}

	public EsitoInserimentoLavoratore gestioneLavoratore(
			it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType candidatura, BigDecimal cdnUtente,
			BigDecimal cdnGruppo, String codCpiInput, String prgRosa, String prgRichiestaAz,
			MultipleTransactionQueryExecutor trans) {

		EsitoInserimentoLavoratore esitoInserimentoLavoratore = new EsitoInserimentoLavoratore();
		it.eng.sil.coop.webservices.art16online.istanze.xsd.types.AnagraficaType anag = candidatura.getAnagrafica();
		it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResidenzaType res = candidatura.getResidenza();
		it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ContattiType contatti = candidatura.getContatti();
		boolean success = false;
		BigDecimal cdnLavoratore = null;
		BigDecimal numKlo = null;

		try {

			SourceBean lavoratoreSourceBean = ((SourceBean) trans.executeQuery("SELECT_AN_LAVORATORE",
					new Object[] { candidatura.getAnagrafica().getCodicefiscale() }, "SELECT"));
			if (lavoratoreSourceBean != null && lavoratoreSourceBean.containsAttribute("ROW.CDNLAVORATORE")) {
				// lavoratore esistente, lo aggiorno
				cdnLavoratore = (BigDecimal) lavoratoreSourceBean.getAttribute("ROW.CDNLAVORATORE");
				numKlo = (BigDecimal) lavoratoreSourceBean.getAttribute("ROW.NUMKLOLAVORATORE");
				success = ((Boolean) trans.executeQuery("UPDATE_AN_LAVORATORE_ASONLINE",
						new Object[] { res.getComune(), res.getIndirizzo(), res.getCap(), cdnUtente,
								numKlo.add(new BigDecimal("1")), contatti.getTelefono(), contatti.getCellulare(),
								contatti.getEmail(), cdnLavoratore },
						"UPDATE")).booleanValue();
				if (!success) {
					_logger.warn("Errore durante aggiornamento lavoratore nella tabella an_lavoratore: "
							+ anag.getCodicefiscale());
					esitoInserimentoLavoratore.setSuccess(false);
					esitoInserimentoLavoratore
							.setCodErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_ERR_AGG_ANAG);
					esitoInserimentoLavoratore
							.setDescrizioneErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.DESC_CANDIDATURA_ERR_AGG_ANAG);
					return esitoInserimentoLavoratore;
				}

			} else {
				// lavoratore non censito, lo inserisco in an_anagrafica

				cdnLavoratore = LavoratoreUtils.getNextCdnLavoratore(trans);

				String dataNascita = DateUtils.formatXMLGregorian(anag.getDatanascita());

				success = ((Boolean) trans.executeQuery("INSERT_AMRDC_ANLAVORATORE",
						new Object[] { cdnLavoratore, anag.getCodicefiscale(), anag.getCognome(), anag.getNome(),
								anag.getSesso(), dataNascita, anag.getComune(), anag.getCittadinanza(), res.getComune(),
								anag.getComune(), cdnUtente, cdnUtente, res.getIndirizzo(), res.getCap(),
								res.getIndirizzo(), res.getCap(), contatti.getEmail(), contatti.getCellulare(), "S",
								contatti.getTelefono() },
						"INSERT")).booleanValue();

				if (!success) {
					_logger.warn("Errore durante inserimento lavoratore nella tabella an_lavoratore: "
							+ anag.getCodicefiscale());
					esitoInserimentoLavoratore.setSuccess(false);
					esitoInserimentoLavoratore
							.setCodErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_ERR_INS_ANAG);
					esitoInserimentoLavoratore
							.setDescrizioneErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.DESC_CANDIDATURA_ERR_INS_ANAG);
					return esitoInserimentoLavoratore;
				}
				// aggiorno datinizio AM_STATO_OCCUPAZ per non avere problemi con la funzione ASStoricizzaStatoOcc
				SourceBean datiAmStatoOccupaz = ((SourceBean) trans.executeQuery("GET_INFO_AM_STATO_OCCUPAZ",
						new Object[] { cdnLavoratore }, "SELECT"));
				BigDecimal numKloStatoOccupaz = null;
				BigDecimal prgStatoOccupaz = null;
				if (datiAmStatoOccupaz != null) {
					numKloStatoOccupaz = (BigDecimal) datiAmStatoOccupaz.getAttribute("ROW.NUMKLOSTATOOCCUPAZ");
					prgStatoOccupaz = (BigDecimal) datiAmStatoOccupaz.getAttribute("ROW.PRGSTATOOCCUPAZ");

					SourceBean dataSb = ((SourceBean) trans.executeQuery("GET_DATAINIZIO_FROM_RICH",
							new Object[] { new BigDecimal(prgRichiestaAz) }, "SELECT"));

					String dataInizio = (String) dataSb.getAttribute("ROW.VALORE");

					success = ((Boolean) trans.executeQuery("UPDATE_DATINIZIO_STATO_OCCUPAZ",
							new Object[] { dataInizio, numKloStatoOccupaz, prgStatoOccupaz }, "UPDATE")).booleanValue();

				} else {
					success = false;
				}

				if (!success) {
					_logger.warn("Errore durante inserimento lavoratore nella tabella an_lavoratore: "
							+ anag.getCodicefiscale());
					esitoInserimentoLavoratore.setSuccess(false);
					esitoInserimentoLavoratore
							.setCodErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_ERR_INS_ANAG);
					esitoInserimentoLavoratore
							.setDescrizioneErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.DESC_CANDIDATURA_ERR_INS_ANAG);
					return esitoInserimentoLavoratore;
				}
				if (candidatura.getExtraUE() != null) {
					// inserisco i dati del permesso di soggiorno

					it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ExtraUEType permesso = candidatura
							.getExtraUE();
					// inserimento dati permesso di soggiorno

					String dataPermesso = DateUtils.formatXMLGregorian(permesso.getScadenzatitolosogg());

					BigDecimal prgPermessoSoggiorno = getPrgPermSogg(trans);
					success = ((Boolean) trans.executeQuery("INSERT_AM_EX_PERM_SOGG",
							new Object[] { prgPermessoSoggiorno, cdnLavoratore, permesso.getMotivopermesso(),
									permesso.getTitolosoggiorno(), null, null, dataPermesso, null, cdnUtente, cdnUtente,
									permesso.getNumerotitolosogg() },
							"INSERT")).booleanValue();

					if (!success) {
						_logger.warn("Errore durante inserimento lavoratore nella tabella AM_EX_PERM_SOGG: "
								+ anag.getCodicefiscale());
						esitoInserimentoLavoratore.setSuccess(false);
						esitoInserimentoLavoratore
								.setCodErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_ERR_INS_ANAG);
						esitoInserimentoLavoratore.setDescrizioneErrore(
								Constants.ESITO_DO_RISULTATO_ISTANZA.DESC_CANDIDATURA_ERR_INS_ANAG);
						return esitoInserimentoLavoratore;
					}
				}
			}

			// regola competenza (codmonotipocpi)
			SourceBean sbCpi = (SourceBean) trans.executeQuery("GET_COD_CPI", new Object[] { res.getComune() },
					"SELECT");
			String codRif = "";
			if (sbCpi != null) {
				codRif = (String) sbCpi.getAttribute("ROW.codcpi");
			}

			String codCpiTit = null;
			String codCpiOrig = null;
			String codTipoCpi = null;
			// TODO
			SourceBean rowCpi = LavoratoreUtils.getInfoCpiProvinciaComune(trans, res.getComune());

			if (rowCpi != null) {
				rowCpi = rowCpi.containsAttribute("ROW") ? (SourceBean) rowCpi.getAttribute("ROW") : rowCpi;
				String codCpiDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPI");
				codCpiDomicilio = codCpiDomicilio.trim();
				String codProvinciaDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "PROVINCIACPI");
				String codProvinciaSil = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODPROVINCIASIL");
				if (codCpiDomicilio.equalsIgnoreCase(COD_NULL_DEFAULT)) {
					codCpiDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPICAPOLUOGO");
				}
				if (codProvinciaDomicilio.equalsIgnoreCase(codProvinciaSil)) {
					codTipoCpi = Properties.CODMONOTIPOCPI_COMP;
					codCpiTit = codCpiDomicilio;
				} else {
					codTipoCpi = Properties.CODMONOTIPOCPI_TIT;
					codCpiTit = codRif;
					if (codCpiTit == null || codCpiTit.length() < 9) {
						codCpiTit = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPICAPOLUOGOGEN");
					}
					codCpiOrig = codCpiDomicilio;
				}
				// aggiornamento an_lav_storia_inf

				Object[] inputParameters = new Object[1];
				inputParameters[0] = cdnLavoratore;
				SourceBean anLavStoriaInfSourceBean = ((SourceBean) trans
						.executeQuery("SELECT_DATI_AN_LAV_STORIA_INF_APPUNTAMENTO_ONLINE", inputParameters, "SELECT"));

				anLavStoriaInfSourceBean = anLavStoriaInfSourceBean.containsAttribute("ROW")
						? (SourceBean) anLavStoriaInfSourceBean.getAttribute("ROW")
						: anLavStoriaInfSourceBean;

				BigDecimal prgLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean.getAttribute("PRGLAVSTORIAINF");
				BigDecimal numkloLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean
						.getAttribute("NUMKLOLAVSTORIAINF");

				inputParameters = new Object[9];
				inputParameters[0] = codCpiTit;
				inputParameters[1] = cdnUtente;
				inputParameters[2] = cdnUtente;
				inputParameters[3] = codTipoCpi;
				inputParameters[4] = codCpiOrig;
				inputParameters[5] = numkloLavStoriaInf.add(new BigDecimal("1"));
				inputParameters[6] = res.getComune();
				inputParameters[7] = "Inserimento da Istanza ART. 16 online";
				inputParameters[8] = prgLavStoriaInf;

				success = ((Boolean) trans.executeQuery("UPDATE_AN_LAV_STORIA_INF_INS_LAV_IMPORTA_SAP", inputParameters,
						"UPDATE")).booleanValue();
				;

				if (!success) {
					_logger.warn("Errore durante aggiornamento tabella an_lav_storia_inf: " + anag.getCodicefiscale());
					esitoInserimentoLavoratore.setSuccess(false);
					esitoInserimentoLavoratore
							.setCodErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_ERR_AGG_ANAG);
					esitoInserimentoLavoratore
							.setDescrizioneErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.DESC_CANDIDATURA_ERR_AGG_ANAG);
					return esitoInserimentoLavoratore;
				}

			} else {
				_logger.warn("Errore durante aggiornamento tabella an_lav_storia_inf: " + anag.getCodicefiscale());
				esitoInserimentoLavoratore.setSuccess(false);
				esitoInserimentoLavoratore.setCodErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_ERR_AGG_ANAG);
				esitoInserimentoLavoratore
						.setDescrizioneErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.DESC_CANDIDATURA_ERR_AGG_ANAG);
				return esitoInserimentoLavoratore;
			}

			// eseguiti tutti gli step correttamente
			esitoInserimentoLavoratore.setSuccess(true);
			esitoInserimentoLavoratore.setCdnLavoratore(cdnLavoratore);
			// In questo caso è un OK e non un errore
			esitoInserimentoLavoratore.setCodErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_OK);
			esitoInserimentoLavoratore.setDescrizioneErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.DESC_CANDIDATURA_OK);

		} catch (Exception e) {
			String errMsg = "ASOnline: inserimento lavoratore fallito: " + anag.getCodicefiscale();
			_logger.error(errMsg, e);
			esitoInserimentoLavoratore.setSuccess(false);
			esitoInserimentoLavoratore.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
			esitoInserimentoLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);
		}
		return esitoInserimentoLavoratore;
	}

	public EsitoInserimentoLavoratore gestioneISEELavoratore(
			it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType candidatura, BigDecimal cdnUtente,
			String prgRosa, String prgRichiestaAz, MultipleTransactionQueryExecutor trans) {

		EsitoInserimentoLavoratore esitoIseeLavoratore = new EsitoInserimentoLavoratore();
		it.eng.sil.coop.webservices.art16online.istanze.xsd.types.AnagraficaType anag = candidatura.getAnagrafica();

		boolean success = false;
		BigDecimal cdnLavoratore = null;

		try {

			SourceBean lavoratoreSourceBean = ((SourceBean) trans.executeQuery("SELECT_AN_LAVORATORE",
					new Object[] { candidatura.getAnagrafica().getCodicefiscale() }, "SELECT"));
			if (lavoratoreSourceBean != null && lavoratoreSourceBean.containsAttribute("ROW.CDNLAVORATORE")) {
				// lavoratore esistente, lo aggiorno
				cdnLavoratore = (BigDecimal) lavoratoreSourceBean.getAttribute("ROW.CDNLAVORATORE");
			} else {
				esitoIseeLavoratore.setSuccess(false);
				esitoIseeLavoratore.setCodErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_ERR_ISEE);
				esitoIseeLavoratore
						.setDescrizioneErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.DESC_CANDIDATURA_ERR_ISEE);
				return esitoIseeLavoratore;
			}

			if (candidatura.getISEE() != null) {
				success = gestioneIsee(candidatura.getISEE(), cdnLavoratore, cdnUtente, prgRosa, prgRichiestaAz, trans);
				if (!success) {
					_logger.warn("Errore durante gestione isee candidato: " + anag.getCodicefiscale());
					esitoIseeLavoratore.setSuccess(false);
					esitoIseeLavoratore.setCodErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_ERR_ISEE);
					esitoIseeLavoratore
							.setDescrizioneErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.DESC_CANDIDATURA_ERR_ISEE);
					return esitoIseeLavoratore;
				}
			}

			// eseguiti tutti gli step correttamente
			esitoIseeLavoratore.setSuccess(true);
			esitoIseeLavoratore.setCdnLavoratore(cdnLavoratore);
			// In questo caso è un OK e non un errore
			esitoIseeLavoratore.setCodErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_OK);
			esitoIseeLavoratore.setDescrizioneErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.DESC_CANDIDATURA_OK);

		} catch (Exception e) {
			String errMsg = "ASOnline: inserimento lavoratore fallito: " + anag.getCodicefiscale();
			_logger.error(errMsg, e);
			esitoIseeLavoratore.setSuccess(false);
			esitoIseeLavoratore.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
			esitoIseeLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);
		}
		return esitoIseeLavoratore;
	}

	public EsitoInserimentoLavoratore gestioneAdesioneLavoratore(
			it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType candidatura, BigDecimal cdnUtente,
			BigDecimal cdnGruppo, String prgRosa, MultipleTransactionQueryExecutor trans) {

		EsitoInserimentoLavoratore esitoAdesioneLavoratore = new EsitoInserimentoLavoratore();
		it.eng.sil.coop.webservices.art16online.istanze.xsd.types.AnagraficaType anag = candidatura.getAnagrafica();

		boolean success = false;
		BigDecimal cdnLavoratore = null;

		try {

			SourceBean lavoratoreSourceBean = ((SourceBean) trans.executeQuery("SELECT_AN_LAVORATORE",
					new Object[] { candidatura.getAnagrafica().getCodicefiscale() }, "SELECT"));
			if (lavoratoreSourceBean != null && lavoratoreSourceBean.containsAttribute("ROW.CDNLAVORATORE")) {
				// lavoratore esistente, lo aggiorno
				cdnLavoratore = (BigDecimal) lavoratoreSourceBean.getAttribute("ROW.CDNLAVORATORE");

			} else {
				esitoAdesioneLavoratore.setSuccess(false);
				esitoAdesioneLavoratore.setCodErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_ERR_ISEE);
				esitoAdesioneLavoratore
						.setDescrizioneErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.DESC_CANDIDATURA_ERR_ISEE);
				return esitoAdesioneLavoratore;
			}
			// adesione candidato online
			success = insertOrUpdateAdesioneASOnline(prgRosa, cdnLavoratore, cdnUtente, cdnGruppo, candidatura, trans);
			if (!success) {
				_logger.warn("Errore durante adesione candidato alla graduatoria: " + anag.getCodicefiscale());
				esitoAdesioneLavoratore.setSuccess(false);
				esitoAdesioneLavoratore.setCodErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_ERR_GRADUATORIA);
				esitoAdesioneLavoratore
						.setDescrizioneErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.DESC_CANDIDATURA_ERR_GRADUATORIA);
				return esitoAdesioneLavoratore;
			}

			// eseguiti tutti gli step correttamente
			esitoAdesioneLavoratore.setSuccess(true);
			esitoAdesioneLavoratore.setCdnLavoratore(cdnLavoratore);
			// In questo caso è un OK e non un errore
			esitoAdesioneLavoratore.setCodErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.CANDIDATURA_OK);
			esitoAdesioneLavoratore.setDescrizioneErrore(Constants.ESITO_DO_RISULTATO_ISTANZA.DESC_CANDIDATURA_OK);

		} catch (Exception e) {
			String errMsg = "ASOnline: inserimento lavoratore fallito: " + anag.getCodicefiscale();
			_logger.error(errMsg, e);
			esitoAdesioneLavoratore.setSuccess(false);
			esitoAdesioneLavoratore.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
			esitoAdesioneLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);
		}
		return esitoAdesioneLavoratore;
	}

	public it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResponseIstanzaArt16 getResponseXsd(
			ResponseIstanzaArt16 response) throws DatatypeConfigurationException, ParseException {
		it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResponseIstanzaArt16 resXSD = new it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResponseIstanzaArt16();

		it.eng.sil.coop.webservices.art16online.istanze.xsd.types.EsitoType esito = new it.eng.sil.coop.webservices.art16online.istanze.xsd.types.EsitoType();
		esito.setCodice(response.getEsito().getCodice());
		esito.setDescrizione(response.getEsito().getDescrizione());
		resXSD.setEsito(esito);

		it.eng.sil.coop.webservices.art16online.istanze.xsd.types.IstanzaArt16Type istanza = new it.eng.sil.coop.webservices.art16online.istanze.xsd.types.IstanzaArt16Type();
		istanza.setAnno(response.getIstanzaArt16().getAnno());
		istanza.setNumero(response.getIstanzaArt16().getNumero());

		CandidaturaType[] allCandidati = response.getIstanzaArt16().getListaCandidature();

		it.eng.sil.coop.webservices.art16online.istanze.xsd.types.IstanzaArt16Type.ListaCandidature lista = new it.eng.sil.coop.webservices.art16online.istanze.xsd.types.IstanzaArt16Type.ListaCandidature();

		for (int i = 0; i < allCandidati.length; i++) {

			CandidaturaType candidato = allCandidati[i];

			it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType candidatoXSD = new it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType();

			it.eng.sil.coop.webservices.art16online.istanze.xsd.types.AnagraficaType anag = new it.eng.sil.coop.webservices.art16online.istanze.xsd.types.AnagraficaType();
			anag.setCittadinanza(candidato.getAnagrafica().getCittadinanza());
			anag.setSesso(candidato.getAnagrafica().getSesso().getValue());
			anag.setCodicefiscale(candidato.getAnagrafica().getCodicefiscale());
			anag.setCognome(candidato.getAnagrafica().getCognome());
			anag.setNome(candidato.getAnagrafica().getNome());
			anag.setComune(candidato.getAnagrafica().getComune());
			anag.setDatanascita(
					DateUtils.toXMLGregorianCalendarDate(DateUtils.format(candidato.getAnagrafica().getDatanascita())));

			it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResidenzaType res = new it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResidenzaType();
			res.setCap(candidato.getResidenza().getCap());
			res.setComune(candidato.getResidenza().getComune());
			res.setIndirizzo(candidato.getResidenza().getIndirizzo());

			it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ContattiType contatti = new it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ContattiType();
			contatti.setCellulare(candidato.getContatti().getCellulare());
			contatti.setEmail(candidato.getContatti().getEmail());
			contatti.setTelefono(candidato.getContatti().getTelefono());

			it.eng.sil.coop.webservices.art16online.istanze.xsd.types.IstanzaType ist = new it.eng.sil.coop.webservices.art16online.istanze.xsd.types.IstanzaType();
			ist.setProtocollo(candidato.getIstanza().getProtocollo());
			ist.setIdistanza(candidato.getIstanza().getIdistanza());
			ist.setDatacandidatura(DateUtils
					.toXMLGregorianCalendarDate(DateUtils.format(candidato.getIstanza().getDatacandidatura())));
			ist.setAnnoprotocollo(candidato.getIstanza().getAnnoprotocollo());

			candidatoXSD.setAnagrafica(anag);
			candidatoXSD.setResidenza(res);
			candidatoXSD.setContatti(contatti);
			candidatoXSD.setIstanza(ist);

			if (candidato.getExtraUE() != null) {
				it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ExtraUEType imm = new it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ExtraUEType();
				imm.setMotivopermesso(candidato.getExtraUE().getMotivopermesso());
				imm.setNumerotitolosogg(candidato.getExtraUE().getNumerotitolosogg());
				imm.setTitolosoggiorno(candidato.getExtraUE().getTitolosoggiorno());
				imm.setScadenzatitolosogg(DateUtils
						.toXMLGregorianCalendarDate(DateUtils.format(candidato.getExtraUE().getScadenzatitolosogg())));

				candidatoXSD.setExtraUE(imm);
			}
			if (candidato.getISEE() != null) {
				it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ISEEType isee = new it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ISEEType();
				isee.setNumannoreddito(candidato.getISEE().getNumannoreddito());
				isee.setValore(candidato.getISEE().getValore());
				isee.setDatainizio(
						DateUtils.toXMLGregorianCalendarDate(DateUtils.format(candidato.getISEE().getDatainizio())));

				candidatoXSD.setISEE(isee);
			}

			lista.getCandidatura().add(candidatoXSD);
		}

		istanza.setListaCandidature(lista);

		resXSD.setIstanzaArt16(istanza);
		return resXSD;
	}

	public ResponseIstanzaArt16 getResponseFromXsd(
			it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResponseIstanzaArt16 response)
			throws DatatypeConfigurationException, ParseException {
		ResponseIstanzaArt16 resXSD = new ResponseIstanzaArt16();

		EsitoType esito = new EsitoType();
		esito.setCodice(response.getEsito().getCodice());
		esito.setDescrizione(response.getEsito().getDescrizione());
		resXSD.setEsito(esito);

		IstanzaArt16Type istanza = new IstanzaArt16Type();
		istanza.setAnno(response.getIstanzaArt16().getAnno());
		istanza.setNumero(response.getIstanzaArt16().getNumero());

		ListaCandidature listaCand = response.getIstanzaArt16().getListaCandidature();

		List<it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType> lista = listaCand
				.getCandidatura();

		CandidaturaType[] listaCandidature = new CandidaturaType[lista.size()];

		for (int i = 0; i < lista.size(); i++) {

			it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType candidato = lista.get(i);

			CandidaturaType candidatoXSD = new CandidaturaType();

			AnagraficaType anag = new AnagraficaType();
			anag.setCittadinanza(candidato.getAnagrafica().getCittadinanza());
			anag.setSesso(AnagraficaTypeSesso.fromValue(candidato.getAnagrafica().getSesso()));
			anag.setCodicefiscale(candidato.getAnagrafica().getCodicefiscale());
			anag.setCognome(candidato.getAnagrafica().getCognome());
			anag.setNome(candidato.getAnagrafica().getNome());
			anag.setComune(candidato.getAnagrafica().getComune());
			anag.setDatanascita(
					DateUtils.getDate(DateUtils.formatXMLGregorian(candidato.getAnagrafica().getDatanascita())));

			ResidenzaType res = new ResidenzaType();
			res.setCap(candidato.getResidenza().getCap());
			res.setComune(candidato.getResidenza().getComune());
			res.setIndirizzo(candidato.getResidenza().getIndirizzo());

			ContattiType contatti = new ContattiType();
			contatti.setCellulare(candidato.getContatti().getCellulare());
			contatti.setEmail(candidato.getContatti().getEmail());
			contatti.setTelefono(candidato.getContatti().getTelefono());

			IstanzaType ist = new IstanzaType();
			ist.setProtocollo(candidato.getIstanza().getProtocollo());
			ist.setIdistanza(candidato.getIstanza().getIdistanza());
			ist.setDatacandidatura(
					DateUtils.getDate(DateUtils.formatXMLGregorian(candidato.getIstanza().getDatacandidatura())));
			ist.setAnnoprotocollo(candidato.getIstanza().getAnnoprotocollo());

			candidatoXSD.setAnagrafica(anag);
			candidatoXSD.setResidenza(res);
			candidatoXSD.setContatti(contatti);
			candidatoXSD.setIstanza(ist);

			if (candidato.getExtraUE() != null) {
				ExtraUEType imm = new ExtraUEType();
				imm.setMotivopermesso(candidato.getExtraUE().getMotivopermesso());
				imm.setNumerotitolosogg(candidato.getExtraUE().getNumerotitolosogg());
				imm.setTitolosoggiorno(candidato.getExtraUE().getTitolosoggiorno());
				imm.setScadenzatitolosogg(DateUtils
						.getDate(DateUtils.formatXMLGregorian(candidato.getExtraUE().getScadenzatitolosogg())));

				candidatoXSD.setExtraUE(imm);
			}
			if (candidato.getISEE() != null) {
				ISEEType isee = new ISEEType();
				isee.setNumannoreddito(candidato.getISEE().getNumannoreddito());
				isee.setValore(candidato.getISEE().getValore());
				isee.setDatainizio(
						DateUtils.getDate(DateUtils.formatXMLGregorian(candidato.getISEE().getDatainizio())));

				candidatoXSD.setISEE(isee);
			}

			listaCandidature[i] = candidatoXSD;
		}

		istanza.setListaCandidature(listaCandidature);

		resXSD.setIstanzaArt16(istanza);
		return resXSD;
	}

	private boolean gestioneIsee(it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ISEEType isee,
			BigDecimal cdnLavoratore, BigDecimal cdnUtente, String prgRosa, String prgRichiestaAz,
			MultipleTransactionQueryExecutor transExec) {

		StoredProcedureCommand command = null;
		try {
			DataConnection conn = transExec.getDataConnection();

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(
					"{ call ? := PG_INCROCIO.ASGestioneIseeOnline( ?, ?, ?, ?, ?, ?, ?) }");

			// imposto i parametri
			List parameters = new ArrayList(8);
			parameters.add(conn.createDataField("result", Types.BIGINT, null));
			command.setAsOutputParameters(0);
			parameters.add(conn.createDataField("p_cdnLavoratore", Types.BIGINT, cdnLavoratore));
			command.setAsInputParameters(1);
			parameters.add(conn.createDataField("p_dataIstanza", Types.VARCHAR,
					DateUtils.formatXMLGregorian(isee.getDatainizio())));
			command.setAsInputParameters(2);
			parameters.add(conn.createDataField("p_valoreIsee", Types.BIGINT, isee.getValore()));
			command.setAsInputParameters(3);
			parameters.add(conn.createDataField("p_annoRiferimento", Types.BIGINT, isee.getNumannoreddito()));
			command.setAsInputParameters(4);
			parameters.add(conn.createDataField("p_cdnutente", Types.BIGINT, cdnUtente));
			command.setAsInputParameters(5);
			parameters.add(conn.createDataField("p_prgrosa", Types.VARCHAR, prgRosa));
			command.setAsInputParameters(6);
			parameters.add(conn.createDataField("p_prgRichAz", Types.BIGINT, new BigDecimal(prgRichiestaAz)));
			command.setAsInputParameters(7);
			// eseguo la stored
			DataResult dr = command.execute(parameters);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();

			DataField df = pdr.getPunctualDatafield();
			Long codRitorno = (Long) df.getObjectValue();

			if (codRitorno.intValue() == 0) {
				return true;
			}

			return false;

		} catch (Exception e) {
			_logger.error("gestioneIsee", e);
		}
		return false;
	}

	private boolean insertOrUpdateAdesioneASOnline(String p_prgRosaIesima, BigDecimal cdnLavoratore,
			BigDecimal cdnUtente, BigDecimal cdnGruppo,
			it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType candidatura,
			MultipleTransactionQueryExecutor transExec) {

		boolean checkSuccess = false;
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		DataResult dr2 = null;
		StoredProcedureCommand command2 = null;
		try {
			String codiceRit = "";
			String errCode = "";
			int paramIndex = 0;
			ArrayList parameters = null;

			conn = transExec.getDataConnection();

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(
					"{ call ? := PG_INCROCIO.ASInsAdesioneOnlineLav(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");

			String p_codMonoISEE = null;
			BigDecimal p_valoreIsee = null;
			if (candidatura.getISEE() != null) {
				p_codMonoISEE = "S";
				p_valoreIsee = candidatura.getISEE().getValore();
			} else {
				p_codMonoISEE = "E";
			}

			parameters = new ArrayList(11);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgRosa
			parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.BIGINT, new BigInteger(p_prgRosaIesima)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_cdnLavoratore
			parameters.add(conn.createDataField("p_cdnLavoratore", java.sql.Types.BIGINT, cdnLavoratore));
			command.setAsInputParameters(paramIndex++);
			// 4. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, cdnUtente));
			command.setAsInputParameters(paramIndex++);
			// 5. p_cdnGruppo
			parameters.add(conn.createDataField("p_cdnGruppo", java.sql.Types.BIGINT, cdnGruppo));
			command.setAsInputParameters(paramIndex++);
			// 6. p_numvaloreisee
			parameters.add(conn.createDataField("p_numvaloreisee", java.sql.Types.BIGINT, p_valoreIsee));
			command.setAsInputParameters(paramIndex++);
			// 7. p_codmonoisee
			parameters.add(conn.createDataField("p_codmonoisee", java.sql.Types.VARCHAR, p_codMonoISEE));
			command.setAsInputParameters(paramIndex++);
			// 8. p_NUMANNOPROTISTANZA
			parameters.add(conn.createDataField("p_NUMANNOPROTISTANZA", java.sql.Types.BIGINT,
					new BigDecimal(candidatura.getIstanza().getAnnoprotocollo())));
			command.setAsInputParameters(paramIndex++);
			// 9. p_STRPROTISTANZA
			parameters.add(conn.createDataField("p_STRPROTISTANZA", java.sql.Types.VARCHAR,
					candidatura.getIstanza().getProtocollo()));
			command.setAsInputParameters(paramIndex++);
			// 10. p_STRIDISTANZA
			parameters.add(conn.createDataField("p_STRIDISTANZA", java.sql.Types.VARCHAR,
					candidatura.getIstanza().getIdistanza()));
			command.setAsInputParameters(paramIndex++);
			// parametri di Output
			// 11. p_errCode
			parameters.add(conn.createDataField("p_errCode", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Reperisco i valori di output della stored
			// 0. Codice di Ritorno
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();
			// 1. ErrCodeOut
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			errCode = df.getStringValue();

			// Predispongo la Response
			int msgCode = 0;//
			String msg = null;
			if (codiceRit.equals("3")) {
				// adesione esistente --> procedo con update
				msgCode = MessageCodes.IDO.ERR_INS_ADESIONE_MORE_PROFILO;
				msg = "Inserimento Adesione Online: errore non è possibile aderire a più profili per lo stesso tipo, proseguo con update";
				_logger.debug(msg);

				command = null;
				outputParams = null;
				dr = null;
				conn = null;
				paramIndex = 0;
				parameters = null;
				parameters = new ArrayList(11);

				conn = transExec.getDataConnection();

				command2 = (StoredProcedureCommand) conn.createStoredProcedureCommand(
						"{ call ? := PG_INCROCIO.ASUpdateAdesioneOnlineLav(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");
				// 1.Parametro di Ritorno
				parameters.add(conn.createDataField("codiceRit", java.sql.Types.BIGINT, null));
				command2.setAsOutputParameters(paramIndex++);
				// preparazione dei Parametri di Input
				// 2. p_prgRosa
				parameters
						.add(conn.createDataField("p_prgRosa", java.sql.Types.BIGINT, new BigInteger(p_prgRosaIesima)));
				command2.setAsInputParameters(paramIndex++);
				// 3. p_cdnLavoratore
				parameters.add(conn.createDataField("p_cdnLavoratore", java.sql.Types.BIGINT, cdnLavoratore));
				command2.setAsInputParameters(paramIndex++);
				// 4. p_prgNominativo
				parameters.add(conn.createDataField("p_prgNominativo", java.sql.Types.BIGINT, null));
				command2.setAsInputParameters(paramIndex++);
				// 5. p_cdnUtente
				parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, cdnUtente));
				command2.setAsInputParameters(paramIndex++);
				// 6. p_codmonoisee
				parameters.add(conn.createDataField("p_codmonoisee", java.sql.Types.VARCHAR, p_codMonoISEE));
				command2.setAsInputParameters(paramIndex++);
				// 7. p_numvaloreisee
				parameters.add(conn.createDataField("p_numvaloreisee", java.sql.Types.BIGINT, p_valoreIsee));
				command2.setAsInputParameters(paramIndex++);
				// 8. p_NUMANNOPROTISTANZA
				parameters.add(conn.createDataField("p_NUMANNOPROTISTANZA", java.sql.Types.BIGINT,
						new BigDecimal(candidatura.getIstanza().getAnnoprotocollo())));
				command2.setAsInputParameters(paramIndex++);
				// 9. p_STRPROTISTANZA
				parameters.add(conn.createDataField("p_STRPROTISTANZA", java.sql.Types.VARCHAR,
						candidatura.getIstanza().getProtocollo()));
				command2.setAsInputParameters(paramIndex++);
				// 10. p_STRIDISTANZA
				parameters.add(conn.createDataField("p_STRIDISTANZA", java.sql.Types.VARCHAR,
						candidatura.getIstanza().getIdistanza()));
				command2.setAsInputParameters(paramIndex++);
				// parametri di Output
				// 11. p_errCode
				parameters.add(conn.createDataField("p_errCode", java.sql.Types.BIGINT, null));
				command2.setAsOutputParameters(paramIndex++);

				// Chiamata alla Stored Procedure
				dr2 = command2.execute(parameters);
				CompositeDataResult cdr2 = (CompositeDataResult) dr2.getDataObject();
				List outputParams2 = cdr2.getContainedDataResult();
				// Reperisco i valori di output della stored
				// 0. Codice di Ritorno
				PunctualDataResult pdr2 = (PunctualDataResult) outputParams2.get(0);
				DataField df2 = pdr2.getPunctualDatafield();
				codiceRit = df2.getStringValue();
				// 1. ErrCodeOut
				pdr2 = (PunctualDataResult) outputParams2.get(1);
				df2 = pdr2.getPunctualDatafield();
				errCode = df2.getStringValue();
				if (!codiceRit.equals("0")) {
					checkSuccess = false;
				} else {
					checkSuccess = true;
				}

			} else if (!codiceRit.equals("0")) {

				switch (Integer.parseInt(codiceRit)) {
				case 1: // XXX valutare errore da inserire
					msgCode = MessageCodes.IDO.ERR_INS_LAV_RNG;
					msg = "Inserimento Adesione: errore insert do_nominativo";
					checkSuccess = false;
					break;
				case 2: // XXX ci deve essere almeno un record dello stato occ
						// da storicizzare
					msgCode = MessageCodes.IDO.ERR_INS_STORIA_STATO_OCC;
					msg = "Inserimento Adesione: errore insert as_storia_stato_occ";
					checkSuccess = false;
					break;
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Inserimento Adesion: sqlCode=" + errCode;
					checkSuccess = false;
					break;
				case -2: // // errore data pubblicazione e/o data chiamata
					msgCode = MessageCodes.IDO.ERR_DATAPUBBLICAZIONE_DATACHIAMATA;
					msg = "La data inizio pubblicazione dell'asta deve essere precedente o uguale alla data chiamata.";
					checkSuccess = false;
					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Inserimento Adesion: errore di ritorno non ammesso. SqlCode=" + errCode;
					checkSuccess = false;
				}

				_logger.debug(msg);

			} else {
				checkSuccess = true;
			}

		} catch (Exception e) {
			_logger.error("insertOrUpdateAdesioneASOnline", e);
		} finally {
			// Utils.releaseResources(null, command2, dr2);
			// Utils.releaseResources(conn, command, dr);
		}

		return checkSuccess;
	}

	public it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResponseIstanzaArt16 getResponseXsd() {
		return responseXsd;
	}

	String getXmlCandidatura(it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType candidatura)
			throws JAXBException {
		try {
			JAXBContext jc = JAXBContext
					.newInstance(it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType.class);
			Marshaller marshaller = jc.createMarshaller();
			StringWriter writer = new StringWriter();
			marshaller.marshal(candidatura, writer);
			String strXmlCandidatura = writer.getBuffer().toString();
			return strXmlCandidatura;
		} catch (JAXBException ej) {
			throw ej;
		}
	}

	it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType getCandidaturaFromXml(
			String xmlCandidatura) throws JAXBException {
		JAXBContext jaxbContext;
		it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType candidatura = null;
		try {
			jaxbContext = JAXBContext
					.newInstance(it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			candidatura = (it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType) jaxbUnmarshaller
					.unmarshal(new StringReader(xmlCandidatura));
		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto CandidaturaType dall'xml");
		}
		return candidatura;
	}

	it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResponseIstanzaArt16 geResponseFromXML(String xmlResponse)
			throws JAXBException {
		JAXBContext jaxbContext;
		it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResponseIstanzaArt16 respone = null;
		try {
			jaxbContext = JAXBContext
					.newInstance(it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResponseIstanzaArt16.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			respone = (it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResponseIstanzaArt16) jaxbUnmarshaller
					.unmarshal(new StringReader(xmlResponse));
		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto ResponseIstanzaArt16 dall'xml");
		}
		return respone;
	}

	BigDecimal getPrgRisultatoIstanza(MultipleTransactionQueryExecutor transExec) {
		BigDecimal prgRisultato = null;

		try {

			Object[] args = null;
			SourceBean risultatoSB = (SourceBean) transExec.executeQuery("GET_NEW_DO_RISULTATO_ISTANZA", args,
					TransactionQueryExecutor.SELECT);

			if (risultatoSB != null && risultatoSB.containsAttribute("ROW")) {
				prgRisultato = SourceBeanUtils.getAttrBigDecimal(risultatoSB, "ROW.PRGRISULTATO");
			}

		} catch (com.engiweb.framework.error.EMFInternalError e) {
			_logger.error("Errore recupero nuovo PRGISTANZA", e);
		}

		return prgRisultato;
	}

	private String getXmlResponse(it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResponseIstanzaArt16 res)
			throws JAXBException {
		try {
			JAXBContext jc = JAXBContext
					.newInstance(it.eng.sil.coop.webservices.art16online.istanze.xsd.types.ResponseIstanzaArt16.class);
			Marshaller marshaller = jc.createMarshaller();
			StringWriter writer = new StringWriter();
			marshaller.marshal(res, writer);
			String strXmlResponse = writer.getBuffer().toString();
			return strXmlResponse;
		} catch (JAXBException ej) {
			throw ej;
		}
	}

	private BigDecimal getPrgPermSogg(MultipleTransactionQueryExecutor trans) {

		BigDecimal prgPermessoSoggiorno = null;

		try {

			Object[] args = null;
			SourceBean permSoggiorno = (SourceBean) trans.executeQuery("GET_NEW_AM_EX_PERM_SOGG", args,
					TransactionQueryExecutor.SELECT);

			if (permSoggiorno != null && permSoggiorno.containsAttribute("ROW")) {
				prgPermessoSoggiorno = SourceBeanUtils.getAttrBigDecimal(permSoggiorno, "ROW.prgPermSogg");
			}

		} catch (com.engiweb.framework.error.EMFInternalError e) {
			_logger.error("Errore recupero nuovo prgPermessoSoggiorno", e);
		}

		return prgPermessoSoggiorno;
	}

	private BigDecimal getPrgCaricoIstanza(MultipleTransactionQueryExecutor trans) {

		BigDecimal prgIstanza = null;

		try {

			Object[] args = null;
			SourceBean istanzaSB = (SourceBean) trans.executeQuery("GET_NEW_DO_CARICA_ISTANZA", args,
					TransactionQueryExecutor.SELECT);

			if (istanzaSB != null && istanzaSB.containsAttribute("ROW")) {
				prgIstanza = SourceBeanUtils.getAttrBigDecimal(istanzaSB, "ROW.PRGISTANZA");
			}

		} catch (com.engiweb.framework.error.EMFInternalError e) {
			_logger.error("Errore recupero nuovo PRGISTANZA", e);
		}

		return prgIstanza;
	}

	private Date StringToDate(String dob) throws ParseException {
		// Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		// Parsing the given String to Date object
		Date date = formatter.parse(dob);
		System.out.println("Date object value: " + date);
		return date;
	}

	private void saveBLOB(DataConnection conn, BigDecimal prgIstanza, String xmlCandidatura) throws Exception {

		SQLCommand selectCommand = null;
		DataResult dr = null;
		OutputStream outStream = null;
		InputStream inStream = null;
		try {
			String stmt = SQLStatements.getStatement("DO_CARICA_ISTANZA_UPLOAD");
			selectCommand = conn.createSelectCommand(stmt);

			ArrayList inputParameters = new ArrayList(1);
			inputParameters.add(conn.createDataField("prgDocumento", Types.BIGINT, prgIstanza));

			dr = selectCommand.execute(inputParameters);

			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();

			DataField df = sdr.getDataRow().getColumn("TXTLISTACANDIDATURA");
			BLOB resultBlob = (BLOB) df.getObjectValue();

			outStream = resultBlob.getBinaryOutputStream();

			// Buffer to hold chunks of data to being written to the Blob.
			int chunk = resultBlob.getChunkSize();
			byte[] buffer = new byte[chunk];

			// Read a chunk of data from the sample file input stream,
			// and write the chunk to the Blob column output stream.
			// Repeat till file has been fully read.
			int nread = 0; // Number of bytes read

			byte[] content = xmlCandidatura.getBytes();

			int size = content.length;
			byte[] b = new byte[size];
			inStream = new ByteArrayInputStream(content);

			while ((nread = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, nread); // Write to Blob
			}
			outStream.flush();
		} catch (Exception e) {
			throw e; // Rilancia qualsiasi eccezione
		} finally {
			// Rilascia eventuali risorse allocate
			if (inStream != null) {
				try {
					inStream.close();
				} catch (Exception e) {
				}
			}
			if (outStream != null) {
				try {
					outStream.close();
				} catch (Exception e) {
				}
			}
			Utils.releaseResources(null, selectCommand, dr);
		}
	}
}
