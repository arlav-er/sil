package it.eng.sil.coop.webservices.rinnovaPatto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.AttachmentPart;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;
import com.inet.report.Engine;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.bean.Documento;
import it.eng.sil.coop.webservices.bean.UserBean;
import it.eng.sil.coop.webservices.rinnovaPatto.input.RinnovoPatto;
import it.eng.sil.coop.webservices.rinnovaPatto.output.Esito;
import it.eng.sil.coop.webservices.rinnovaPatto.output.ObjectFactory;
import it.eng.sil.security.User;
import it.eng.sil.util.ConnessionePerReport;
import it.eng.sil.util.xml.XMLValidator;

public class RinnovaPatto extends AbstractSimpleReport implements RinnovaPattoInterface {

	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RinnovaPatto.class.getName());

	private static final BigDecimal userSP = new BigDecimal("150");
	private static final BigDecimal operatoreSPortale = new BigDecimal("0");
	private static final String statoProtocollato = "PR";
	private static final String servizioDaPortale = "DP";
	private static final String esitoInCorso = "CC";
	private static final String esitoProposto = "PRO";
	private static final String esitoConcluso = "FC";
	private static final String esitoRendicontatoErogato = "E";
	private static final String esitoRendicontatoPrenotato = "P";
	private static final String codLstTabAzionePatto = "OR_PER";
	private static final String flgpatto297 = "S";
	private static final String tipoFilePDF = "PDF";
	private static final String tipoDoc = "PT297";
	private static final String ConfermaPeriodica = "CONFERMA PERIODICA";
	private static final String ServizioConfermaPeriodica = "primo colloquio d.l.gs. 181/2000";
	private static final String noteColloquio = "colloquio inserito per rinnovo patto da portale Lavoro per Te";

	private final String SCHEMA_XSD_INPUT = "rinnovaPatto_in.xsd";
	private static ObjectFactory factory = new ObjectFactory();

	private static final String ESITO_OK = "00";
	private static final String DESC_OK = "RINNOVO EFFETTUATO";

	private static final String COD_ERRORE_XML = "01";
	private static final String DESC_ERRORE_XML = "ERRORE VALIDAZIONE DATI INPUT";

	private static final String COD_ERRORE_LAV_ASSENTE = "02";
	private static final String DESC_ERRORE_LAV_ASSENTE = "LAVORATORE NON PRESENTE IN ANAGRAFICA";

	private static final String COD_ERRORE_PATTO_ASSENTE = "03";
	private static final String DESC_ERRORE_PATTO_ASSENTE = "PATTO DA RINNOVARE ASSENTE";

	private static final String COD_ERRORE_PATTI_MULTIPLI = "04";
	private static final String DESC_ERRORE_PATTI_MULTIPLI = "PATTI MULTIPILI";

	private static final String COD_ERRORE_APPUNTAMENTO = "05";
	private static final String DESC_ERRORE_APPUNTAMENTO = "PRESENTE APPUNTAMENTO";

	private static final String COD_ERRORE_AZIONI = "06";
	private static final String DESC_ERRORE_AZIONI = "AZIONI NON COMPATIBILI CON IL RINNOVO";

	private static final String COD_ERRORE_CHIUSURA_PATTO = "07";
	private static final String DESC_ERRORE_CHIUSURA_PATTO = "CHIUSURA PATTO FALLITA";

	private static final String COD_ERRORE_PROGRESSIVO_NUOVO_PATTO = "08";
	private static final String DESC_ERRORE_PROGRESSIVO_NUOVO_PATTO = "ERRORE RECUPERO PROGRESSIVO NUOVO PATTO";

	private static final String COD_ERRORE_INSERISCI_NUOVO_PATTO = "09";
	private static final String DESC_ERRORE_INSERISCI_NUOVO_PATTO = "INSERIMENTO NUOVO PATTO FALLITO";

	private static final String COD_ERRORE_AGGIORNA_AZIONI = "10";
	private static final String DESC_ERRORE_AGGIORNA_AZIONI = "ERRORE AGGIORNAMENTO AZIONI";

	private static final String COD_ERRORE_PROTOCOLLAZIONE = "11";
	private static final String DESC_ERRORE_PROTOCOLLAZIONE = "ERRORE PROTOCOLLAZIONE DOCUMENTO";

	private static final String COD_ERRORE_RICEVUTA = "12";
	private static final String DESC_ERRORE_RICEVUTA = "ERRORE RICEVUTA RINNOVO PATTO";

	private static final String COD_ERRORE_GENERICO = "99";
	private static final String DESC_ERRORE_GENERICO = "ERRORE GENERICO";

	public String putRinnovaPatto(String inputXML) throws java.rmi.RemoteException, Exception {
		String codFisc = null;
		TransactionQueryExecutor transExec = null;
		RinnovoPatto rinnovo = null;
		RequestContainer requestContainer = null;
		SessionContainer sessionContainer = null;
		SourceBean request = null;
		SourceBean response = null;
		UserBean usrSP = null;

		_logger.info("Il servizio rinnova patto e' stato chiamato");

		try {
			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + "rinnovaPatto" + File.separator + SCHEMA_XSD_INPUT);
			String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFile);
			if (validityErrors != null && validityErrors.length() > 0) {
				String validityError = "Errore di validazione xml: " + validityErrors;
				_logger.error(validityError);
				_logger.warn(inputXML);
				return ritornoEsito(COD_ERRORE_XML, DESC_ERRORE_XML);
			}

			rinnovo = convertToRinnovoPattoInput(inputXML);
			codFisc = rinnovo.getCodiceFiscale();

			try {
				BigDecimal cdnLav = getLavoratore(codFisc);
				if (cdnLav == null) {
					return ritornoEsito(COD_ERRORE_LAV_ASSENTE, DESC_ERRORE_LAV_ASSENTE);
				}

				Vector patti = getPatto297(cdnLav);
				if (patti == null || patti.size() == 0) {
					return ritornoEsito(COD_ERRORE_PATTO_ASSENTE, DESC_ERRORE_PATTO_ASSENTE);
				}

				if (patti != null && patti.size() > 1) {
					return ritornoEsito(COD_ERRORE_PATTI_MULTIPLI, DESC_ERRORE_PATTI_MULTIPLI);
				}

				SourceBean patto = (SourceBean) patti.get(0);
				BigDecimal prgPattoLav = (BigDecimal) patto.getAttribute("prgpattolavoratore");
				String codCpi = patto.containsAttribute("codcpi") ? patto.getAttribute("codcpi").toString() : null;
				BigDecimal prgDid = (BigDecimal) patto.getAttribute("prgdichdisponibilita");
				BigDecimal numklo = (BigDecimal) patto.getAttribute("numklopattolavoratore");
				String codTipoPatto = patto.containsAttribute("codtipopatto")
						? patto.getAttribute("codtipopatto").toString()
						: null;
				String codServizio = patto.containsAttribute("codservizio")
						? patto.getAttribute("codservizio").toString()
						: null;
				String codCodificaPatto = patto.containsAttribute("codcodificapatto")
						? patto.getAttribute("codcodificapatto").toString()
						: null;
				String datStipula = patto.containsAttribute("datstipula") ? patto.getAttribute("datstipula").toString()
						: null;
				String datScadConferma = patto.containsAttribute("datScadConferma")
						? patto.getAttribute("datScadConferma").toString()
						: null;

				if (getAppuntamenti(cdnLav, codCpi)) {
					return ritornoEsito(COD_ERRORE_APPUNTAMENTO, DESC_ERRORE_APPUNTAMENTO);
				}

				Vector azioni = getAzioniRinnovo(cdnLav, datStipula, datScadConferma);
				if (azioni == null || azioni.size() == 0) {
					return ritornoEsito(COD_ERRORE_AZIONI, DESC_ERRORE_AZIONI);
				}

				String dataStipula = DateUtils.getNow();
				String dataChiusura = DateUtils.giornoPrecedente(dataStipula);
				int annoDataStimataNew = DateUtils.getAnno(dataStipula) + 1;
				Vector vettDataStimata = StringUtils.split(dataStipula, "/");
				String dataStimataNew = vettDataStimata.get(0).toString() + "/" + vettDataStimata.get(1).toString()
						+ "/" + annoDataStimataNew;
				String dataScadenzaNew = dataStimataNew;

				usrSP = new UserBean(userSP, cdnLav);
				requestContainer = new RequestContainer();
				sessionContainer = new SessionContainer(true);
				sessionContainer.setAttribute("_CDUT_", userSP);
				sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
				sessionContainer.setAttribute(User.USERID, usrSP.getUser());
				requestContainer.setSessionContainer(sessionContainer);
				request = new SourceBean("SERVICE_REQUEST");
				response = new SourceBean("SERVICE_RESPONSE");
				request.setAttribute("datStipula", dataStipula);
				request.setAttribute("cdnLavoratore", cdnLav.toString());
				requestContainer.setServiceRequest(request);
				RequestContainer.setRequestContainer(requestContainer);

				transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				transExec.initTransaction();

				boolean isOK = chiudiPatto(prgPattoLav, dataChiusura, numklo, transExec);
				if (!isOK) {
					transExec.rollBackTransaction();
					return ritornoEsito(COD_ERRORE_CHIUSURA_PATTO, DESC_ERRORE_CHIUSURA_PATTO);
				}

				BigDecimal prgPattoNew = getProgressivoPatto(transExec);
				if (prgPattoNew == null) {
					transExec.rollBackTransaction();
					return ritornoEsito(COD_ERRORE_PROGRESSIVO_NUOVO_PATTO, DESC_ERRORE_PROGRESSIVO_NUOVO_PATTO);
				}

				isOK = inserisciPatto(prgPattoNew, dataStipula, dataScadenzaNew, cdnLav, codCpi, prgDid, codTipoPatto,
						servizioDaPortale, codCodificaPatto, transExec);
				if (!isOK) {
					transExec.rollBackTransaction();
					return ritornoEsito(COD_ERRORE_INSERISCI_NUOVO_PATTO, DESC_ERRORE_INSERISCI_NUOVO_PATTO);
				}

				try {
					int numAzioni = azioni.size();
					BigDecimal prgpercorso = null;
					BigDecimal prgcolloquio = null;
					BigDecimal prgPercorsoNew = null;
					BigDecimal prgcolloquioConfermaPeriodica = null;
					BigDecimal prgAzioneConfermaPeriodica = null;

					for (int i = 0; i < numAzioni; i++) {
						SourceBean azione = (SourceBean) azioni.get(i);
						prgpercorso = (BigDecimal) azione.getAttribute("prgpercorso");
						prgcolloquio = (BigDecimal) azione.getAttribute("prgcolloquio");
						BigDecimal prgazioni = (BigDecimal) azione.getAttribute("prgazioni");
						String codesito = azione.getAttribute("codesito") != null
								? azione.getAttribute("codesito").toString()
								: "";
						String descAzione = azione.getAttribute("descAzione") != null
								? azione.getAttribute("descAzione").toString()
								: "";

						if (!descAzione.equalsIgnoreCase(ConfermaPeriodica)) {
							if (codesito.equalsIgnoreCase(esitoInCorso) || codesito.equalsIgnoreCase(esitoProposto)) {
								isOK = concludiAzione(prgpercorso, prgcolloquio, dataChiusura, transExec);
								if (!isOK) {
									transExec.rollBackTransaction();
									return ritornoEsito(COD_ERRORE_AGGIORNA_AZIONI, DESC_ERRORE_AGGIORNA_AZIONI);
								}

								prgPercorsoNew = getProgressivoPercorso(transExec);
								if (prgPercorsoNew == null) {
									transExec.rollBackTransaction();
									return ritornoEsito(COD_ERRORE_AGGIORNA_AZIONI, DESC_ERRORE_AGGIORNA_AZIONI);
								}

								isOK = inserisciAzione(prgPercorsoNew, prgcolloquio, codesito,
										esitoRendicontatoPrenotato, dataStipula, prgazioni, transExec);
								if (!isOK) {
									transExec.rollBackTransaction();
									return ritornoEsito(COD_ERRORE_AGGIORNA_AZIONI, DESC_ERRORE_AGGIORNA_AZIONI);
								}
								isOK = collegaAzionePatto(prgPattoNew, codLstTabAzionePatto, prgPercorsoNew, transExec);
								if (!isOK) {
									transExec.rollBackTransaction();
									return ritornoEsito(COD_ERRORE_AGGIORNA_AZIONI, DESC_ERRORE_AGGIORNA_AZIONI);
								}
							}
						} else {
							// Conferma Periodica
							prgcolloquioConfermaPeriodica = prgcolloquio;
							prgAzioneConfermaPeriodica = prgazioni;
							if (codesito.equalsIgnoreCase(esitoInCorso) || codesito.equalsIgnoreCase(esitoProposto)) {
								isOK = concludiAzione(prgpercorso, prgcolloquio, dataChiusura, transExec);
								if (!isOK) {
									transExec.rollBackTransaction();
									return ritornoEsito(COD_ERRORE_AGGIORNA_AZIONI, DESC_ERRORE_AGGIORNA_AZIONI);
								}
							}
						}
					}

					// Inserimento Conferma Periodica
					if (prgcolloquioConfermaPeriodica == null) {
						prgAzioneConfermaPeriodica = getPrgAzioneConfermaPeriodica(transExec);
						String codServizioColloquio = getServizioConfermaPeriodica(transExec);
						// Inserimento colloquio per la Conferma Periodica
						prgcolloquioConfermaPeriodica = getProgressivoColloquio(transExec);
						if (prgcolloquioConfermaPeriodica == null) {
							transExec.rollBackTransaction();
							return ritornoEsito(COD_ERRORE_AGGIORNA_AZIONI, DESC_ERRORE_AGGIORNA_AZIONI);
						}
						isOK = inserisciColloquio(prgcolloquioConfermaPeriodica, cdnLav, codServizioColloquio,
								dataStipula, codCpi, transExec);
						if (!isOK) {
							transExec.rollBackTransaction();
							return ritornoEsito(COD_ERRORE_AGGIORNA_AZIONI, DESC_ERRORE_AGGIORNA_AZIONI);
						}
						isOK = inserisciSchedaColloquio(prgcolloquioConfermaPeriodica, transExec);
						if (!isOK) {
							transExec.rollBackTransaction();
							return ritornoEsito(COD_ERRORE_AGGIORNA_AZIONI, DESC_ERRORE_AGGIORNA_AZIONI);
						}
					}

					prgPercorsoNew = getProgressivoPercorso(transExec);
					if (prgPercorsoNew == null) {
						transExec.rollBackTransaction();
						return ritornoEsito(COD_ERRORE_AGGIORNA_AZIONI, DESC_ERRORE_AGGIORNA_AZIONI);
					}

					isOK = inserisciAzione(prgPercorsoNew, prgcolloquioConfermaPeriodica, esitoInCorso,
							esitoRendicontatoPrenotato, dataStimataNew, prgAzioneConfermaPeriodica, transExec);
					if (!isOK) {
						transExec.rollBackTransaction();
						return ritornoEsito(COD_ERRORE_AGGIORNA_AZIONI, DESC_ERRORE_AGGIORNA_AZIONI);
					}
					isOK = collegaAzionePatto(prgPattoNew, codLstTabAzionePatto, prgPercorsoNew, transExec);
					if (!isOK) {
						transExec.rollBackTransaction();
						return ritornoEsito(COD_ERRORE_AGGIORNA_AZIONI, DESC_ERRORE_AGGIORNA_AZIONI);
					}
				} catch (Throwable e) {
					if (transExec != null) {
						transExec.rollBackTransaction();
					}
					_logger.error("Errore nel servizio", e);
					return ritornoEsito(COD_ERRORE_AGGIORNA_AZIONI, DESC_ERRORE_AGGIORNA_AZIONI);
				}

				try {
					eseguiStampaPattoConApi(transExec, codCpi, cdnLav, dataStipula, prgPattoNew, requestContainer,
							request, response);
				} catch (Throwable e) {
					if (transExec != null) {
						transExec.rollBackTransaction();
					}
					_logger.error("Errore nel servizio", e);
					return ritornoEsito(COD_ERRORE_PROTOCOLLAZIONE, DESC_ERRORE_PROTOCOLLAZIONE);
				}

				try {
					SourceBean rowProt = getInfoProtocollazionePattoRinnovato(prgPattoNew, transExec);
					String numProtocollo = (String) rowProt.getAttribute("numprotocollo");
					String numAnno = (String) rowProt.getAttribute("numannoprot");
					String dataProt = (String) rowProt.getAttribute("datoraprotocollo");

					SourceBean rowCpi = getDescrizioneCpi(codCpi);
					String descCpi = (String) rowCpi.getAttribute("strdescrizione");
					String responsabileCpi = rowCpi.getAttribute("strresponsabile") != null
							? rowCpi.getAttribute("strresponsabile").toString()
							: "";

					byte[] data = getStampaRicevuta(codCpi, cdnLav, datStipula, dataScadenzaNew, numProtocollo, numAnno,
							dataProt, descCpi, responsabileCpi, request, response);

					if (data != null) {
						DataHandler dh = new DataHandler(data, "application/octet-stream");
						if (dh != null) {
							MessageContext msgContext = MessageContext.getCurrentContext();
							Message rspmsg = msgContext.getResponseMessage();
							_logger.debug("org.apache.axis.attachments.Attachments.SEND_TYPE_MIME : "
									+ org.apache.axis.attachments.Attachments.SEND_TYPE_MIME);
							int inputAttachmentType = rspmsg.getAttachmentsImpl().getSendType();
							rspmsg.getAttachmentsImpl().setSendType(inputAttachmentType);
							AttachmentPart attachPart = rspmsg.createAttachmentPart(dh);
							rspmsg.addAttachmentPart(attachPart);

							transExec.commitTransaction();

							return ritornoEsito(ESITO_OK, DESC_OK);
						} else {
							transExec.rollBackTransaction();
							return ritornoEsito(COD_ERRORE_RICEVUTA, DESC_ERRORE_RICEVUTA);
						}
					} else {
						transExec.rollBackTransaction();
						return ritornoEsito(COD_ERRORE_RICEVUTA, DESC_ERRORE_RICEVUTA);
					}

				} catch (Throwable e) {
					if (transExec != null) {
						transExec.rollBackTransaction();
					}
					_logger.error("Errore nel servizio", e);
					return ritornoEsito(COD_ERRORE_RICEVUTA, DESC_ERRORE_RICEVUTA);
				}
			} catch (Throwable e) {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				_logger.error("Errore nel servizio", e);
				return ritornoEsito(COD_ERRORE_GENERICO, DESC_ERRORE_GENERICO);
			}

		} catch (Throwable e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "RinnovaPatto:putRinnovaPatto", e);
			return ritornoEsito(COD_ERRORE_GENERICO, DESC_ERRORE_GENERICO);
		}

	}

	private BigDecimal getLavoratore(String cf) throws Exception {
		BigDecimal cdnLav = null;
		Object[] params = new Object[] { cf };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("WS_GET_CDN_LAVORATORE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			cdnLav = (BigDecimal) row.getAttribute("cdnlavoratore");

		}
		return cdnLav;
	}

	private SourceBean getDescrizioneCpi(String codCpi) throws Exception {
		Object[] params = new Object[] { codCpi };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_INFO_CPI_STAMPE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
		}
		return row;
	}

	private Vector getPatto297(BigDecimal cdnLav) throws Exception {
		Vector patti = null;
		Object[] params = new Object[] { cdnLav };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("WS_GET_RINNOVA_PATTO", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			patti = row.getAttributeAsVector("ROW");
		}
		return patti;
	}

	private boolean getAppuntamenti(BigDecimal cdnLav, String codCpi) throws Exception {
		boolean existApp = false;
		Object[] params = new Object[] { cdnLav, codCpi };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("WS_GET_APPUNTAMENTI_RINNOVA_PATTO", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			int numAppuntamenti = row.getAttribute("numAppuntamenti") != null
					? new Integer(row.getAttribute("numAppuntamenti").toString()).intValue()
					: 0;
			if (numAppuntamenti > 0) {
				existApp = true;
			}
		}
		return existApp;
	}

	private Vector getAzioniRinnovo(BigDecimal cdnLav, String datStipula, String datScadConferma) throws Exception {
		Vector azioni = null;
		Object[] params = new Object[] { cdnLav, datStipula, datScadConferma, datStipula, datScadConferma, datStipula,
				datScadConferma };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("WS_GET_AZIONI_RINNOVA_PATTO", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			azioni = row.getAttributeAsVector("ROW");
		}
		return azioni;
	}

	private BigDecimal getPrgAzioneConfermaPeriodica(TransactionQueryExecutor txExec) throws Exception {
		BigDecimal prgAzione = null;
		Object[] params = new Object[] { ConfermaPeriodica };
		SourceBean row = (SourceBean) txExec.executeQuery("WS_GET_PRG_AZIONE_CONFERMA_PERIODICA", params, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgAzione = (BigDecimal) row.getAttribute("prgazioni");
		}
		return prgAzione;
	}

	private String getServizioConfermaPeriodica(TransactionQueryExecutor txExec) throws Exception {
		String codServizio = null;
		Object[] params = new Object[] { ServizioConfermaPeriodica };
		SourceBean row = (SourceBean) txExec.executeQuery("WS_GET_SERVIZIO_AZIONE_CONFERMA_PERIODICA", params,
				"SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			codServizio = (String) row.getAttribute("codservizio");
		}
		return codServizio;
	}

	private boolean chiudiPatto(BigDecimal prgPattoLav, String dataChiusura, BigDecimal numklo,
			TransactionQueryExecutor txExec) throws Exception {
		boolean isOK = false;
		String strNote = "Chiuso per Rinnovo Patto da portale Lavoro per Te";
		numklo = numklo.add(new BigDecimal(1));
		Object[] params = new Object[] { dataChiusura, dataChiusura, null, userSP, strNote, numklo, prgPattoLav };
		Boolean res = (Boolean) txExec.executeQuery("WS_RINNOVO_CHIUSURA_PATTO", params, "UPDATE");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	private boolean concludiAzione(BigDecimal prgpercorso, BigDecimal prgcolloquio, String dataEffettiva,
			TransactionQueryExecutor txExec) throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { esitoConcluso, esitoRendicontatoErogato, dataEffettiva, dataEffettiva, userSP,
				prgpercorso, prgcolloquio };
		Boolean res = (Boolean) txExec.executeQuery("WS_RINNOVO_AGGIORNA_ESITO_AZIONE", params, "UPDATE");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	private boolean inserisciAzione(BigDecimal prgPercorsoNew, BigDecimal prgcolloquio, String codEsito,
			String codEsitoRendicont, String dataStimata, BigDecimal prgazioni, TransactionQueryExecutor txExec)
			throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgPercorsoNew, prgcolloquio, dataStimata, prgazioni, codEsito,
				codEsitoRendicont, userSP, userSP };
		Boolean res = (Boolean) txExec.executeQuery("WS_RINNOVO_INSERISCI_AZIONE", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	private boolean inserisciColloquio(BigDecimal prgColloquio, BigDecimal cdnLav, String codServizio,
			String dataColloquio, String codCpi, TransactionQueryExecutor txExec) throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgColloquio, cdnLav, dataColloquio, codServizio, operatoreSPortale, codCpi,
				noteColloquio, userSP, userSP };
		Boolean res = (Boolean) txExec.executeQuery("WS_RINNOVO_INSERISCI_COLLOQUIO", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	private boolean inserisciSchedaColloquio(BigDecimal prgColloquio, TransactionQueryExecutor txExec)
			throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgColloquio };
		Boolean res = (Boolean) txExec.executeQuery("WS_RINNOVO_INSERISCI_SCHEDA_COLLOQUIO", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	private boolean collegaAzionePatto(BigDecimal prgPatto, String codLstTab, BigDecimal prgPercorso,
			TransactionQueryExecutor txExec) throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgPatto, codLstTab, prgPercorso };
		Boolean res = (Boolean) txExec.executeQuery("WS_RINNOVO_COLLEGA_AZIONE_PATTO", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	private BigDecimal getProgressivoPatto(TransactionQueryExecutor txExec) throws Exception {
		BigDecimal progressivo = null;
		SourceBean row = (SourceBean) txExec.executeQuery("GET_NEW_PATTOLAV", null, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			progressivo = (BigDecimal) row.getAttribute("prgPattoLavoratore");

		}
		return progressivo;
	}

	private BigDecimal getProgressivoColloquio(TransactionQueryExecutor txExec) throws Exception {
		BigDecimal progressivo = null;
		SourceBean row = (SourceBean) txExec.executeQuery("OR_COLLOQUIO_NEXTVAL", null, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			progressivo = (BigDecimal) row.getAttribute("do_nextval");

		}
		return progressivo;
	}

	private BigDecimal getProgressivoPercorso(TransactionQueryExecutor txExec) throws Exception {
		BigDecimal progressivo = null;
		SourceBean row = (SourceBean) txExec.executeQuery("OR_PERCORSO_CONCORDATO_NEXTVAL", null, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			progressivo = (BigDecimal) row.getAttribute("do_nextval");

		}
		return progressivo;
	}

	private SourceBean getInfoProtocollazionePattoRinnovato(BigDecimal prgPatto, TransactionQueryExecutor txExec)
			throws Exception {
		Object[] params = new Object[] { prgPatto };
		SourceBean row = (SourceBean) txExec.executeQuery("WS_GET_INFO_PROTOCOLLAZIONE_PATTO_RINNOVATO", params,
				"SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
		}
		return row;
	}

	private boolean inserisciPatto(BigDecimal prgPattoLav, String dataStipula, String dataScadenza, BigDecimal cdnLav,
			String codCpi, BigDecimal prgDid, String codTipoPatto, String codServizio, String codCodificaPatto,
			TransactionQueryExecutor txExec) throws Exception {
		boolean isOK = false;
		String codStatoAtto = statoProtocollato;
		String strNote = "Patto Rinnovato in data " + dataStipula + " da portale Lavoro per Te";

		Object[] params = new Object[] { prgPattoLav, dataStipula, cdnLav, prgDid, codCpi, codStatoAtto, dataScadenza,
				flgpatto297, codTipoPatto, codServizio, codCodificaPatto, strNote, userSP, userSP };
		Boolean res = (Boolean) txExec.executeQuery("WS_RINNOVO_INSERT_PATTO", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	private void eseguiStampaPattoConApi(TransactionQueryExecutor txExec, String codCpi, BigDecimal cdnLavoratore,
			String dataStipula, BigDecimal prgPattoLav, RequestContainer requestContainer, SourceBean request,
			SourceBean response) throws Exception {

		Documento doc = getDocumento();
		doc.setChiaveTabella(prgPattoLav.toString());
		doc.setTipoProt("S");
		String docInOut = "O";

		SourceBean rowProt = (SourceBean) txExec.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");
		if (rowProt == null) {
			throw new Exception("impossibile recuperare il numero di protocollo");
		}
		rowProt = (rowProt.containsAttribute("ROW") ? (SourceBean) rowProt.getAttribute("ROW") : rowProt);
		BigDecimal numAnnoProt = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
		BigDecimal numProtocollo = (BigDecimal) rowProt.getAttribute("NUMPROTOCOLLO");
		String datProtocollazione = (String) rowProt.getAttribute("DATAORAPROT");

		doc.setNumAnnoProt(numAnnoProt);
		doc.setNumProtocollo(numProtocollo);
		doc.setDatProtocollazione(datProtocollazione);
		doc.setCodStatoAtto(statoProtocollato);
		doc.setDatInizio(dataStipula);
		doc.setCodTipoDocumento(tipoDoc);
		doc.setCodCpi(codCpi);
		doc.setCdnLavoratore(cdnLavoratore);
		doc.setPrgAzienda(null);
		doc.setPrgUnita(null);
		doc.setStrDescrizione("");
		doc.setFlgDocAmm("S");
		doc.setFlgDocIdentifP("N");
		doc.setStrNumDoc(null);
		doc.setStrEnteRilascio(codCpi);
		doc.setCodMonoIO(docInOut);
		doc.setDatAcqril(dataStipula);
		doc.setCodModalitaAcqril(null);
		doc.setCodTipoFile(null);
		doc.setStrNomeDoc("patto.pdf");
		doc.setDatFine(null);
		doc.setStrNote("");
		doc.setStrDescrizione("Patto lavoratore");
		doc.setFlgAutocertificazione("N");
		doc.setPagina("PattoLavDettaglioPage");
		doc.setCdnUtIns(userSP);
		doc.setCdnUtMod(userSP);
		setReportPath("patto/patto_CC.rpt");

		request.setAttribute("numProt", numProtocollo.toString());
		request.setAttribute("annoProt", numAnnoProt.toString());
		request.setAttribute("dataOraProt", datProtocollazione);
		request.setAttribute("docInOut", docInOut);
		request.setAttribute("codStatoAtto", statoProtocollato);

		com.inet.report.Engine eng = makeEngine(request, response, txExec);
		Map prompts = new HashMap();

		String descrizioneInOut = "Output";
		prompts.put("numProt", numProtocollo);
		prompts.put("numAnnoProt", numAnnoProt);
		prompts.put("dataProt", datProtocollazione);
		prompts.put("docInOut", descrizioneInOut);

		setPromptFields(prompts);

		if (insertDocument(request, response, txExec, eng)) {
			if ((numProtocollo != null) && numAnnoProt != null) {
				updateInfoProtocolloPatto(request, response, txExec);
			}
		} else {
			throw new Exception("stampa del patto fallita");
		}
	}

	private void updateInfoProtocolloPatto(SourceBean request, SourceBean response, TransactionQueryExecutor txExec)
			throws Exception {
		String dataProtocollo = (String) request.getAttribute("dataOraProt");

		Object cdnLav = request.getAttribute("cdnLavoratore");
		Object[] params = new Object[] { cdnLav };
		SourceBean pattoAperto = (SourceBean) txExec.executeQuery("GET_PATTO_APERTO", params, "SELECT");
		pattoAperto = (pattoAperto.containsAttribute("ROW") ? (SourceBean) pattoAperto.getAttribute("ROW")
				: pattoAperto);
		Object prgPatto = pattoAperto.getAttribute("row.prgPattoLavoratore");

		Object[] paramsUpd = new Object[] { dataProtocollo, prgPatto };
		Boolean res = (Boolean) txExec.executeQuery("UPDATE_DAT_PROT_INF_LEGATE", paramsUpd, "UPDATE");

		Object[] paramsUpdProt = new Object[] { dataProtocollo, request.getAttribute("PRGSTATOOCCUPAZ"), cdnLav };
		res = (Boolean) txExec.executeQuery("UPDATE_PATTO_DAT_ULTIMO_PROT", paramsUpdProt, "UPDATE");
	}

	private Engine makeEngine(SourceBean request, SourceBean response, TransactionQueryExecutor txExec)
			throws Exception {

		Object cdnLav = request.getAttribute("cdnLavoratore");

		Object[] params = new Object[] { cdnLav, cdnLav, cdnLav, cdnLav, cdnLav };
		SourceBean statoOccupazionale = (SourceBean) txExec.executeQuery("GET_STATO_OCCUPAZ", params, "SELECT");
		request.setAttribute("PRGSTATOOCCUPAZ", statoOccupazionale.getAttribute("ROW.PRGSTATOOCCUPAZ"));

		SourceBean operatore = (SourceBean) txExec.executeQuery("GET_OPERATORE", new Object[] { userSP }, "SELECT");
		SourceBean appuntamenti = (SourceBean) txExec.executeQuery("GET_APPUNTAMENTI_ADR", new Object[] { cdnLav },
				"SELECT");
		SourceBean azioniConcordate = (SourceBean) txExec.executeQuery("GET_PRESTAZIONI_AZIONI",
				new Object[] { cdnLav }, "SELECT");
		SourceBean ambitoProfessionale = (SourceBean) txExec.executeQuery("GET_MANSIONI",
				new Object[] { cdnLav, cdnLav, cdnLav, cdnLav, cdnLav, cdnLav, cdnLav, cdnLav, cdnLav }, "SELECT");
		SourceBean ambitoDocumento = (SourceBean) txExec.executeQuery("GET_TIPODOC", new Object[] { tipoDoc },
				"SELECT");
		SourceBean impegni = (SourceBean) txExec.executeQuery("GET_IMPEGNI_LEGATI_AL_PATTO", new Object[] { cdnLav },
				"SELECT");
		SourceBean infoGenerali = (SourceBean) txExec.executeQuery("GET_PATTO_INFO_GENERALI", new Object[] { cdnLav },
				"SELECT");
		SourceBean cat181 = (SourceBean) txExec.executeQuery("GET_181_CAT", new Object[] { cdnLav }, "SELECT");
		SourceBean laurea = (SourceBean) txExec.executeQuery("GET_LAUREA_X_CAT181", new Object[] { cdnLav }, "SELECT");
		SourceBean movimenti = (SourceBean) txExec.executeQuery("GET_MOVIMENTI_LAVORATORE", new Object[] { cdnLav },
				"SELECT");
		SourceBean documentiIdentitaSourceBean = (SourceBean) txExec.executeQuery("GET_PATTO_DOCUMENTO_IDENTITA",
				new Object[] { cdnLav }, "SELECT");

		Vector documentiIdentitaVector = documentiIdentitaSourceBean.getAttributeAsVector("ROW");
		SourceBean documentoIdentita = null;
		if (documentiIdentitaVector != null && documentiIdentitaVector.size() > 0) {
			documentoIdentita = (SourceBean) documentiIdentitaVector.get(0);
		}
		SourceBean lastConferimentoBean = null;
		SourceBean configPrivacy = (SourceBean) txExec.executeQuery("CONFIG_PRIVACY", null, "SELECT");
		String privacy = (String) configPrivacy.getAttribute("ROW.VALORENUM");
		SourceBean soggettiAcc = (SourceBean) txExec.executeQuery("GET_SOGGACCREDITATO_REPORT", new Object[] { cdnLav },
				"SELECT");

		Vector appuntaments = appuntamenti.getAttributeAsVector("ROW");
		Vector azioniConcordats = azioniConcordate.getAttributeAsVector("ROW");
		Vector ambitoProfs = ambitoProfessionale.getAttributeAsVector("ROW");
		Vector entiAccreditati = soggettiAcc.getAttributeAsVector("ROW");
		Vector infoGeneraliV = infoGenerali.getAttributeAsVector("ROW");
		if (infoGeneraliV.size() == 0) {
			// recupero le info generali considerando l'associazione patto con la mobilità
			infoGenerali = (SourceBean) txExec.executeQuery("GET_PATTO_MOBILITA_INFO_GENERALI", new Object[] { cdnLav },
					"SELECT");
			infoGeneraliV = infoGenerali.getAttributeAsVector("ROW");
		}

		Vector impegniV = impegni.getAttributeAsVector("ROW");
		// generazione del report tramite api crystalclear
		String tipoFile = tipoFilePDF;
		SourceBean beanRows = (SourceBean) txExec.executeQuery("GET_CODREGIONE", null, "SELECT");
		String regione = (String) beanRows.getAttribute("ROW.CODREGIONE");

		Class report = Class.forName("it.eng.sil.action.report.patto.ApiPatto");
		Method inizializzaMethod = report.getMethod("inizializza",
				new Class[] { SourceBean.class, SourceBean.class, Vector.class, SourceBean.class, Vector.class,
						Vector.class, Vector.class, String.class, String.class, SourceBean.class, Vector.class,
						Vector.class, String.class, String.class, String.class, String.class, SourceBean.class,
						SourceBean.class, String.class, Vector.class, TransactionQueryExecutor.class });

		Method getEngineMethod = report.getMethod("getEngine", new Class[] {});

		SourceBean infoGen = (SourceBean) infoGeneraliV.get(0);
		SourceBean statoOcc = (SourceBean) statoOccupazionale.getAttribute("ROW");
		SourceBean categoria181 = it.eng.sil.util.amministrazione.impatti.DBLoad.getRowAttribute(cat181);
		String ambito = (String) ambitoDocumento.getAttribute("ROW.RIFERIMENTO");
		Vector titoloStudio = laurea.getAttributeAsVector("ROW");
		Vector mov = movimenti.getAttributeAsVector("ROW");
		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;

		String strParam = null;
		strParam = (String) request.getAttribute("dataOraProt");

		String docInOut = null;
		docInOut = (String) request.getAttribute("docInOut");

		Object o = report.newInstance();

		inizializzaMethod.invoke(o,
				new Object[] { infoGen, statoOcc, appuntaments, operatore, azioniConcordats, ambitoProfs, impegniV,
						installAppPath, tipoFile, categoria181, titoloStudio, mov, ambito, strParam, docInOut, regione,
						documentoIdentita, lastConferimentoBean, privacy, entiAccreditati, null });

		return (Engine) getEngineMethod.invoke(o, new Object[] {});
	}

	private byte[] getStampaRicevuta(String codCpi, BigDecimal cdnLavoratore, String dataStipula, String dataScadenza,
			String numProtocollo, String numAnnoProt, String dataProt, String descrizioneCpi, String responsabileCpi,
			SourceBean request, SourceBean response) {

		DataConnection conn = null;
		ConnessionePerReport miaConn = null;
		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			conn = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			File ftemp = new File(ConfigSingleton.getRootPath() + "/WEB-INF/report/patto/Ricevuta_Rinnovo_CC.rpt");
			com.inet.report.Engine engine = new Engine(Engine.EXPORT_PDF);
			String rptPath = "file:" + ftemp.getAbsolutePath();
			engine.setReportFile(rptPath);

			miaConn = new ConnessionePerReport(conn.getInternalConnection());
			engine.setConnection(miaConn);
			engine.setCatalog(conn.getInternalConnection().getCatalog());
			for (int i = 0; i < engine.getSubReportCount(); i++) {
				engine.getSubReport(i).setConnection(miaConn);
				engine.setCatalog(conn.getInternalConnection().getCatalog());
			}

			engine.setPrompt("cdnLavoratore", cdnLavoratore.toString());
			engine.setPrompt("codCPI", codCpi);
			engine.setPrompt("dataStipulaOld", dataStipula);
			engine.setPrompt("dataScadenzaNew", dataScadenza);
			engine.setPrompt("numProtocollo", numProtocollo);
			engine.setPrompt("numAnnoProt", numAnnoProt);
			engine.setPrompt("descrizioneCPI", descrizioneCpi);
			engine.setPrompt("dataProtocollazione", dataProt);
			engine.setPrompt("responsabileCPI", responsabileCpi);

			//
			engine.execute();

			ByteArrayOutputStream fout = new ByteArrayOutputStream();
			fout.write(engine.getPageData(1));

			return fout.toByteArray();
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (EMFInternalError emf) {
				_logger.error(emf);
			}
		}
	}

	private RinnovoPatto convertToRinnovoPattoInput(String xmlInput) throws JAXBException {
		JAXBContext jaxbContext;
		RinnovoPatto inputObj = null;
		try {
			jaxbContext = JAXBContext.newInstance(RinnovoPatto.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			inputObj = (RinnovoPatto) jaxbUnmarshaller.unmarshal(new StringReader(xmlInput));
		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return inputObj;
	}

	private static String convertInputToString(Esito msg) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(Esito.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(msg, writer);
		String xml = writer.getBuffer().toString();
		return xml;
	}

	private static String ritornoEsito(String codice, String descErrore) {
		String xmlRinnova = "";
		Esito esito = null;
		try {
			esito = factory.createEsito();
			esito.setCodice(codice);
			esito.setDescrizione(descErrore);
			xmlRinnova = convertInputToString(esito);
		} catch (Throwable e) {
			_logger.error("Errore creazione input XML", e);
		}
		return xmlRinnova;
	}

}
