package it.eng.sil.module.pi3;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.nttdata._2012.Pi3.ClassificationScheme;
import com.nttdata._2012.Pi3.Correspondent;
import com.nttdata._2012.Pi3.Document;
import com.nttdata._2012.Pi3.Template;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.bean.Documento;

public class ProtocolloPi3Manager {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProtocolloPi3Manager.class.getName());
	private final String className = StringUtils.getClassName(this);

	DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

	public boolean inviaProtocolloPi3(CreaDocumentPi3Bean creaDocumentPi3Bean) throws Exception {

		_logger.debug("[ProtocolloPi3Manager] ---> inviaProtocolloPi3 <---");

		boolean protocolloInviato = false;

		Correspondent mittentePi3Added = null;

		// Database e Web Service Manager...
		ProtocolloPi3DBManager dbManager = new ProtocolloPi3DBManager();
		ProtocolloPi3WsManager wsManager = new ProtocolloPi3WsManager();

		// Verifica se il Documento/Pratica e' stato gia' precedentemente inviato alla Protocollazione Pi3, e quindi e'
		// presente nelle nostre tabelle di SPIL
		String prgDocumento = creaDocumentPi3Bean.getDocumentSil().getPrgDocumento().toString();
		String nrPratica = creaDocumentPi3Bean.getNrPraticaSPIL(); // numero anno protocollo SPIL + numero protocollo
																	// documento principale

		_logger.debug(
				"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> [INPUT creaDocumentPi3Bean: prgDocumento MAIN DOCUMENT]: "
						+ prgDocumento);
		_logger.debug(
				"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> [INPUT creaDocumentPi3Bean: nrPratica MAIN DOCUMENT]:"
						+ nrPratica);

		boolean isPraticaAlreadyProcessed = dbManager.isPraticaAlreadyProcessedIntoPi3(nrPratica);

		_logger.debug("[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> isPraticaAlreadyProcessed: "
				+ isPraticaAlreadyProcessed);

		// Se non e' stato ancora inviato, si procede al primo invio...
		if (!isPraticaAlreadyProcessed) {
			// Verifica se l'Utente (Mittente) e' gia' presente nella Rubrica dei Correspondent in Pi3
			if (creaDocumentPi3Bean.getUtenteMittente() != null) {
				// ProtocolloPi3Bean pi3bean =
				// dbManager.getUtenteMittenteFromProtocolloPi3(creaDocumentPi3Bean.getUtenteMittente().getIdUtenteSPIL());
				ProtocolloPi3Bean pi3bean = dbManager
						.getUtenteFromProtocolloPi3(creaDocumentPi3Bean.getUtenteMittente().getIdUtenteSPIL());

				/// Se non e' presente nel nostro database SPIL l'Utente viene ricreato in Pi3 per la prima volta...
				if (StringUtils.isEmpty(pi3bean.getStrMittentePi3())
						&& StringUtils.isEmpty(pi3bean.getStrDestinatarioPi3())) {

					try {

						// Utente SPIL aggiunto nella Rubrica Pi3
						mittentePi3Added = wsManager.addCorrespondent(creaDocumentPi3Bean.getUtenteMittente());

						_logger.debug("[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Mittente (id SPIL = "
								+ creaDocumentPi3Bean.getUtenteMittente().getIdUtenteSPIL() + ") "
								+ creaDocumentPi3Bean.getUtenteMittente().getNome() + " "
								+ creaDocumentPi3Bean.getUtenteMittente().getCognome()
								+ " : appena aggiunto nella Rubrica Pi3");

					} catch (Exception ex) {

						// Recupera il TIPO DI ERRORE ('ERR' - generico nei dati - oppure 'TIM' - Timeout del servizio)
						String typeError = getTypeErrorFromException(ex);

						if (!ex.getMessage().equalsIgnoreCase("Il codice del corrispondente esiste")) {
							_logger.error(
									"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> addCorrespondent MITTENTE (id SPIL = "
											+ creaDocumentPi3Bean.getUtenteMittente().getIdUtenteSPIL() + ") "
											+ creaDocumentPi3Bean.getUtenteMittente().getNome() + " "
											+ creaDocumentPi3Bean.getUtenteMittente().getCognome()
											+ " [WS] ---> ERROR: Disallineamento Utenza fra SPIL e Pi3: "
											+ ex.getMessage() + " in Pi3 ma non in SPIL");
							protocolloInviato = false;

							throw new Pi3Exception(
									"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> addCorrespondent MITTENTE (id SPIL = "
											+ creaDocumentPi3Bean.getUtenteMittente().getIdUtenteSPIL() + ") "
											+ creaDocumentPi3Bean.getUtenteMittente().getNome() + " "
											+ creaDocumentPi3Bean.getUtenteMittente().getCognome()
											+ " [WS] ---> ERROR: Disallineamento Utenza fra SPIL e Pi3: "
											+ ex.getMessage() + " in Pi3 ma non in SPIL",
									ex, typeError, creaDocumentPi3Bean);

						} else {
							_logger.error(
									"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> addCorrespondent MITTENTE (id SPIL = "
											+ creaDocumentPi3Bean.getUtenteMittente().getIdUtenteSPIL() + ") "
											+ creaDocumentPi3Bean.getUtenteMittente().getNome() + " "
											+ creaDocumentPi3Bean.getUtenteMittente().getCognome()
											+ " [WS] ---> ERROR: " + ex.getMessage());
							protocolloInviato = false;

							throw new Pi3Exception(
									"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> addCorrespondent MITTENTE (id SPIL = "
											+ creaDocumentPi3Bean.getUtenteMittente().getIdUtenteSPIL() + ") "
											+ creaDocumentPi3Bean.getUtenteMittente().getNome() + " "
											+ creaDocumentPi3Bean.getUtenteMittente().getCognome()
											+ " [WS] ---> ERROR: " + ex.getMessage(),
									ex, typeError, creaDocumentPi3Bean);
						}
					}

				} else {

					try {
						// Se gia' presente nella Rubrica Pi3 viene recuperato l'Utente Mittente direttamente da l'
						// mittentePi3Added = wsManager.getCorrespondent(pi3bean.getStrMittentePi3());

						if (!StringUtils.isEmpty(pi3bean.getStrMittentePi3())) {
							mittentePi3Added = wsManager.getCorrespondent(pi3bean.getStrMittentePi3());
						} else if (!StringUtils.isEmpty(pi3bean.getStrDestinatarioPi3())) {
							mittentePi3Added = wsManager.getCorrespondent(pi3bean.getStrDestinatarioPi3());
						}

						if (!StringUtils.isEmpty(creaDocumentPi3Bean.getUtenteMittente().getIdUtenteSPIL())) {
							_logger.debug("[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Mittente (id SPIL = "
									+ creaDocumentPi3Bean.getUtenteMittente().getIdUtenteSPIL() + " / id PI3 = "
									+ creaDocumentPi3Bean.getUtenteMittente().getIdCorrespondentINFTRENT() + ") "
									+ creaDocumentPi3Bean.getUtenteMittente().getNome() + " "
									+ creaDocumentPi3Bean.getUtenteMittente().getCognome()
									+ " : gia' presente nella Rubrica Pi3");
						}

					} catch (Exception ex) {

						// Recupera il TIPO DI ERRORE ('ERR' - generico nei dati - oppure 'TIM' - Timeout del servizio)
						String typeError = getTypeErrorFromException(ex);

						_logger.error(
								"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> getCorrespondent MITTENTE (id SPIL = "
										+ creaDocumentPi3Bean.getUtenteMittente().getIdUtenteSPIL() + ") "
										+ creaDocumentPi3Bean.getUtenteMittente().getNome() + " "
										+ creaDocumentPi3Bean.getUtenteMittente().getCognome() + " [WS] ---> ERROR: "
										+ ex.getMessage());
						protocolloInviato = false;

						throw new Pi3Exception(
								"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> getCorrespondent MITTENTE (id SPIL = "
										+ creaDocumentPi3Bean.getUtenteMittente().getIdUtenteSPIL() + ") "
										+ creaDocumentPi3Bean.getUtenteMittente().getNome() + " "
										+ creaDocumentPi3Bean.getUtenteMittente().getCognome() + " [WS] ---> ERROR: "
										+ ex.getMessage(),
								ex, typeError, creaDocumentPi3Bean);
					}

				}

			} /*
				 * else{ _logger.
				 * error("[ProtocolloPi3Manager] -> inviaProtocolloPi3 ---> ERROR: nessun mittente da inviare a Pi3");
				 * protocolloInviato = false;
				 * 
				 * throw new
				 * Exception("[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> creaDocumentPi3Bean.getUtenteMittente() ---> ERROR: Nessun Utente Mittente impostato. Impossibile processare l'invio della Protocollazione Pi3"
				 * ); }
				 */

			// Verifica se i Destinatari sono gia' presenti nella Rubrica dei Correspondent in Pi3
			Correspondent[] lstDestinatariPi3Added = new Correspondent[0];
			if (creaDocumentPi3Bean.getLstUtentiDestinatari() != null) {

				lstDestinatariPi3Added = new Correspondent[creaDocumentPi3Bean.getLstUtentiDestinatari().size()];

				int k = 0;
				for (UtentePi3Bean destinatario : creaDocumentPi3Bean.getLstUtentiDestinatari()) {
					// ProtocolloPi3Bean pi3bean =
					// dbManager.getUtenteDestinatarioFromProtocolloPi3(destinatario.getIdUtenteSPIL());
					ProtocolloPi3Bean pi3bean = dbManager.getUtenteFromProtocolloPi3(destinatario.getIdUtenteSPIL());

					// Se non e' presente nel nostro SPIL l'Utente Destinatario viene ricreato in Pi3 per la prima
					// volta...
					// if (StringUtils.isEmpty(pi3bean.getStrDestinatarioPi3())){
					if (StringUtils.isEmpty(pi3bean.getStrMittentePi3())
							&& StringUtils.isEmpty(pi3bean.getStrDestinatarioPi3())) {

						UtentePi3Bean utente = new UtentePi3Bean();
						utente.setNome(destinatario.getNome());
						utente.setCognome(destinatario.getCognome());
						utente.setCodeCorrespondentINFTRENT(destinatario.getCodeCorrespondentINFTRENT()); // cdnLavoratore
																											// o
																											// prgAzienza
																											// o
																											// prgUnita
																											// o codCpi
						utente.setDescriptionCorrespondentINFTRENT(destinatario.getDescriptionCorrespondentINFTRENT()); // Codice
																														// Fiscale
																														// o
																														// Ragione
																														// Sociale
						utente.setCorrespondentTypeINFTRENT(destinatario.getCorrespondentTypeINFTRENT()); // P = persona
																											// - U =
																											// unita
																											// organizzativa

						try {
							lstDestinatariPi3Added[k] = wsManager.addCorrespondent(utente);

							_logger.debug("[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Destinatario (id SPIL = "
									+ destinatario.getIdUtenteSPIL() + ") " + destinatario.getNome() + " "
									+ destinatario.getCognome() + " : appena aggiunto nella Rubrica Pi3");
						} catch (Exception ex) {

							// Recupera il TIPO DI ERRORE ('ERR' - generico nei dati - oppure 'TIM' - Timeout del
							// servizio)
							String typeError = getTypeErrorFromException(ex);

							if (!ex.getMessage().equalsIgnoreCase("Il codice del corrispondente esiste")) {
								_logger.error(
										"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> addCorrespondent DESTINATARIO ["
												+ k + "] (id SPIL = " + destinatario.getIdUtenteSPIL() + ") "
												+ destinatario.getNome() + " " + destinatario.getCognome()
												+ " [WS] ---> ERROR: Disallineamento Utenza fra SPIL e Pi3: "
												+ ex.getMessage() + " in Pi3 ma non in SPIL");

								protocolloInviato = false;

								throw new Pi3Exception(
										"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> addCorrespondent DESTINATARIO ["
												+ k + "] (id SPIL = " + destinatario.getIdUtenteSPIL() + ") "
												+ destinatario.getNome() + " " + destinatario.getCognome()
												+ " [WS] ---> ERROR: Disallineamento Utenza fra SPIL e Pi3: "
												+ ex.getMessage() + " in Pi3 ma non in SPIL",
										ex, typeError, creaDocumentPi3Bean);

							} else {
								_logger.error(
										"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> addCorrespondent DESTINATARIO ["
												+ k + "] (id SPIL = " + destinatario.getIdUtenteSPIL() + ") "
												+ destinatario.getNome() + " " + destinatario.getCognome()
												+ " [WS] ---> ERROR: " + ex.getMessage());

								protocolloInviato = false;

								throw new Pi3Exception(
										"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> addCorrespondent DESTINATARIO ["
												+ k + "] (id SPIL = " + destinatario.getIdUtenteSPIL() + ") "
												+ destinatario.getNome() + " " + destinatario.getCognome()
												+ " [WS] ---> ERROR: " + ex.getMessage(),
										ex, typeError, creaDocumentPi3Bean);

							}

						}

						k++;

					} else {

						try {

							// Se gia' presente nella Rubrica Pi3 viene recuperato l'Utente Destinatario direttamente da
							// l'
							// lstDestinatariPi3Added[k] = wsManager.getCorrespondent(pi3bean.getStrDestinatarioPi3());

							if (!StringUtils.isEmpty(pi3bean.getStrMittentePi3())) {
								lstDestinatariPi3Added[k] = wsManager.getCorrespondent(pi3bean.getStrMittentePi3());
							} else if (!StringUtils.isEmpty(pi3bean.getStrDestinatarioPi3())) {
								lstDestinatariPi3Added[k] = wsManager.getCorrespondent(pi3bean.getStrDestinatarioPi3());
							}

							if (!StringUtils.isEmpty(destinatario.getIdUtenteSPIL())) {
								_logger.debug("[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> Destinatario (id SPIL = "
										+ destinatario.getIdUtenteSPIL() + " / id PI3 = "
										+ destinatario.getIdCorrespondentINFTRENT() + ") " + destinatario.getNome()
										+ " " + destinatario.getCognome() + " : gia' presente nella Rubrica Pi3");
							}
						} catch (Exception ex) {

							// Recupera il TIPO DI ERRORE ('ERR' - generico nei dati - oppure 'TIM' - Timeout del
							// servizio)
							String typeError = getTypeErrorFromException(ex);

							_logger.error(
									"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> getCorrespondent DESTINATARIO ["
											+ k + "] (id SPIL = " + destinatario.getIdUtenteSPIL() + ") "
											+ destinatario.getNome() + " " + destinatario.getCognome()
											+ " [WS] ---> ERROR: " + ex.getMessage());

							protocolloInviato = false;

							throw new Pi3Exception(
									"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> getCorrespondent DESTINATARIO ["
											+ k + "] (id SPIL = " + destinatario.getIdUtenteSPIL() + ") "
											+ destinatario.getNome() + " " + destinatario.getCognome()
											+ " [WS] ---> ERROR: " + ex.getMessage(),
									ex, typeError, creaDocumentPi3Bean);

						}

					}
				}

			} else {
				if (lstDestinatariPi3Added.length == 0) {
					_logger.debug(
							"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> nessun destinatario da inviare a Pi3");
				}
			}

			try {
				creaDocumentPi3Bean.getDocumentPi3().setSender(mittentePi3Added);
				creaDocumentPi3Bean.getDocumentPi3().setRecipients(lstDestinatariPi3Added);

				ClassificationScheme classificationScheme = wsManager.getActiveClassificationScheme();
				creaDocumentPi3Bean.setClassificationScheme(classificationScheme);
				creaDocumentPi3Bean.setProject(
						wsManager.getProject(creaDocumentPi3Bean.getCodeProject(), classificationScheme.getId()));

				if (creaDocumentPi3Bean.isTemplateDoc()) {
					creaDocumentPi3Bean.getDocumentPi3().setTemplate(getTemplateAndSetField(wsManager));
				}

			} catch (Exception ex) {

				// Recupera il TIPO DI ERRORE ('ERR' - generico nei dati - oppure 'TIM' - Timeout del servizio)
				String typeError = getTypeErrorFromException(ex);

				_logger.error(
						"[ProtocolloPi3Manager] -> inviaProtocolloPi3 ---> ERROR: Nessuna Pratica inviata per la Protocollazione Pi3. Problemi nel recupero del ClassificationSchemeId o Project dai Web Services di Informatica Trentina: "
								+ ex.getMessage());
				protocolloInviato = false;
				throw new Pi3Exception(
						"[ProtocolloPi3Manager] -> inviaProtocolloPi3 ---> ERROR: Nessuna Pratica inviata per la Protocollazione Pi3. Problemi nel recupero del ClassificationSchemeId o Project dai Web Services di Informatica Trentina: "
								+ ex.getMessage(),
						ex, typeError, creaDocumentPi3Bean);

			}

			Document documentPi3Inviato = null;
			BigDecimal prgDocPi3 = new BigDecimal(0);
			// if (creaDocumentPi3Bean.getDocumentSil().getTempFile().length() != 0){

			try {
				// Protocolla la Pratica SPIL in Pi3
				documentPi3Inviato = wsManager.createDocumentAndAddInProject(creaDocumentPi3Bean);
			} catch (Exception ex) {

				// Recupera il TIPO DI ERRORE ('ERR' - generico nei dati - oppure 'TIM' - Timeout del servizio)
				String typeError = getTypeErrorFromException(ex);

				_logger.error(
						"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> createDocumentAndAddInProject [WS] -> ---> ERROR: Nessuna Pratica inviata per la Protocollazione Pi3: "
								+ ex.getMessage());
				protocolloInviato = false;
				throw new Pi3Exception(
						"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> creaDocumentPi3Bean.getUtenteMittente() ---> ERROR: Nessuna Pratica inviata per la Protocollazione Pi3: "
								+ ex.getMessage(),
						ex, typeError, creaDocumentPi3Bean);
			}

			_logger.debug("[ProtocolloPi3Manager] -> createDocumentAndAddInProject [WS] -> documentPi3Inviato");
			if (documentPi3Inviato != null) {
				_logger.debug(
						"[ProtocolloPi3Manager] -> createDocumentAndAddInProject [WS] -> documentPi3Inviato SIGNATURE (se gia recuperata): "
								+ documentPi3Inviato.getSignature());
			}

			// Execute Transmission Document se il Documento e' di tipo in ENTRATA ed e' PREDISPOSED
			// if(creaDocumentPi3Bean.getDocumentPi3().getDocumentType().equals(Pi3Constants.PI3_DOCUMENT_TYPE_ENTRATA)){
			if (creaDocumentPi3Bean.isDocInEntrata()) {
				if (creaDocumentPi3Bean.isDocumentoFirmabile() && creaDocumentPi3Bean.isDocumentoFirmato()
						&& creaDocumentPi3Bean.isConsensoAttivo()) {
					_logger.debug(
							"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> nessuna chiamata effettuata al servizio di executeTransmissionDocument");
				} else {
					boolean isExecutedTransmissionDocument = wsManager
							.executeTransmissionDocument(documentPi3Inviato.getId());

					_logger.debug("[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> executeTransmissionDocument: "
							+ isExecutedTransmissionDocument);
				}
			}

			// Inserisce il documento Pi3 appena protocollato nel DB SPIL (AM_PROTOCOLLO_PITRE)
			ProtocolloPi3Bean protocolloPi3Bean = new ProtocolloPi3Bean();

			protocolloPi3Bean.setStrSegnatura(documentPi3Inviato.getSignature());
			protocolloPi3Bean.setStridDoc(documentPi3Inviato.getDocNumber());

			// controllo il tipo di documento. Il patto non prevede la data di protocollo
			if (creaDocumentPi3Bean.getDocumentType() == null
					|| !creaDocumentPi3Bean.getDocumentType().equals(Pi3Constants.PI3_DOCUMENT_TYPE_PATTO)) {
				if ((creaDocumentPi3Bean.isDocumentoFirmabile() && creaDocumentPi3Bean.isDocumentoFirmato()
						&& creaDocumentPi3Bean.isConsensoAttivo())
						|| (creaDocumentPi3Bean.isDocInUscita()
								&& creaDocumentPi3Bean.getDocumentSil().getTempFile().length() != 0)) {
					if (!StringUtils.isEmpty(documentPi3Inviato.getProtocolDate())) {
						protocolloPi3Bean.setDataProt(format.parse(documentPi3Inviato.getProtocolDate()));
					}
				}
			}

			if (creaDocumentPi3Bean.getUtenteMittente() != null) {
				protocolloPi3Bean.setStrMittente(creaDocumentPi3Bean.getUtenteMittente().getIdUtenteSPIL());
			}

			// protocolloPi3Bean.setStrOggetto(documentPi3Inviato.getObject());
			protocolloPi3Bean.setStrOggetto(creaDocumentPi3Bean.getDocumentPi3().getObject());
			// protocolloPi3Bean.setPrgTitolario(new BigDecimal(classificationScheme.getId()));
			protocolloPi3Bean.setPrgTitolario(new BigDecimal(creaDocumentPi3Bean.getPrgTitolario()));
			protocolloPi3Bean.setCdnUtMod(creaDocumentPi3Bean.getCdnUtMod());
			protocolloPi3Bean.setCdnUtins(creaDocumentPi3Bean.getCdnUtins());
			protocolloPi3Bean.setDtMins(creaDocumentPi3Bean.getDtMins());
			protocolloPi3Bean.setDtmMod(creaDocumentPi3Bean.getDtmMod());
			protocolloPi3Bean.setStrNumPratica(creaDocumentPi3Bean.getNrPraticaSPIL());
			// protocolloPi3Bean.setStrMittentePi3(documentPi3Inviato.getSender().getId());

			if (mittentePi3Added != null) {
				protocolloPi3Bean.setStrMittentePi3(mittentePi3Added.getId());
			}

			// boolean isInsertProtocolloPi3OnSil = dbManager.insertProtocolloPi3(protocolloPi3Bean);
			prgDocPi3 = dbManager.insertProtocolloPi3(protocolloPi3Bean);
			_logger.debug(
					"[ProtocolloPi3Manager] -> insertProtocolloPi3 [MAIN DOCUMENT] [DB] -> isInsertProtocolloPi3OnSil -> prgDocPi3: "
							+ prgDocPi3);

			// Inserisce la lista dei Destinatari nel DB SPIL (AM_PROT_PITRE_DEST)
			if (creaDocumentPi3Bean.getLstUtentiDestinatari() != null) {

				int j = 0;
				for (UtentePi3Bean destinatario : creaDocumentPi3Bean.getLstUtentiDestinatari()) {

					DestinatarioPi3Bean destinatarioPi3Bean = new DestinatarioPi3Bean();
					Correspondent correspondentPi3 = lstDestinatariPi3Added[j];

					_logger.debug(
							"[ProtocolloPi3Manager] -> insertProtocollazionePi3Destinatario [DB] -> destinatario [" + j
									+ "]: " + destinatario.getNome() + " " + destinatario.getCognome() + " [SPIL ID:"
									+ destinatario.getIdUtenteSPIL() + " / PI3 ID:" + correspondentPi3.getId() + "]");

					destinatarioPi3Bean.setPrgProtPi3(prgDocPi3);
					destinatarioPi3Bean.setCodiceMotivoTrasmissioneInterna(null); // TODO
					destinatarioPi3Bean.setStrDestinatarioPi3(correspondentPi3.getId());
					destinatarioPi3Bean.setStrDestinatarioSil(destinatario.getIdUtenteSPIL());
					destinatarioPi3Bean.setFlgDestinatario(destinatario.getFlgDestinatarioPrincipale());

					boolean isDestinatarioInserited = dbManager
							.insertProtocollazionePi3Destinatario(destinatarioPi3Bean);
					j++;

					_logger.debug(
							"[ProtocolloPi3Manager] -> insertProtocollazionePi3Destinatario [DB] -> isDestinatarioInserited ["
									+ j + "]: " + isDestinatarioInserited);
				}
			}

			// Inserisce il 'Main Document' Pi3 appena protocollato nel DB SPIL (AM_PROTOCOLLO_DOCUMENTO_PITRE)
			ProtocolloDocumentoPi3Bean protocolloDocumentoPi3Bean = new ProtocolloDocumentoPi3Bean();

			protocolloDocumentoPi3Bean.setPrgProtPitre(prgDocPi3);
			protocolloDocumentoPi3Bean.setFlgPrincipale(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_FLG_PRINCIPALE_SI);
			protocolloDocumentoPi3Bean.setPrgDocumento(creaDocumentPi3Bean.getDocumentSil().getPrgDocumento());
			protocolloDocumentoPi3Bean.setCdnUtMod(creaDocumentPi3Bean.getCdnUtMod());
			protocolloDocumentoPi3Bean.setCdnUtins(creaDocumentPi3Bean.getCdnUtins());
			protocolloDocumentoPi3Bean.setDtMins(creaDocumentPi3Bean.getDtMins());
			protocolloDocumentoPi3Bean.setDtmMod(creaDocumentPi3Bean.getDtmMod());

			// if (!StringUtils.isEmpty(documentPi3Inviato.getCreationDate())){
			// protocolloDocumentoPi3Bean.setDatInvio(format.parse(documentPi3Inviato.getCreationDate()));
			// } else{
			protocolloDocumentoPi3Bean.setDatInvio(new Date());
			// }

			if (StringUtils.isEmpty(documentPi3Inviato.getSignature())) {
				if (creaDocumentPi3Bean.isDocumentoFirmabile()) {

					// TODO: va sostituito con if(creaDocumentPi3Bean.isDocRepertoriato()){
					if (creaDocumentPi3Bean.getDocumentPi3().getDocumentType()
							.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_NON_PROTOCOLLATO)
							&& creaDocumentPi3Bean.getTipoDelTrattamento()
									.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_REPERTORIO)) {
						if (creaDocumentPi3Bean.isDocumentoFirmato()) {
							protocolloDocumentoPi3Bean
									.setCodStatoInvio(Pi3Constants.PI3_DOCUMENT_SEND_STATE_REPERTORIATO);
						} else {
							protocolloDocumentoPi3Bean.setCodStatoInvio(
									Pi3Constants.PI3_DOCUMENT_SEND_STATE_REPERTORIATO_SENZA_DOC_FIRMATO);
						}
					} else {
						// protocolloDocumentoPi3Bean.setCodStatoInvio(Pi3Constants.PI3_DOCUMENT_SEND_STATE_IN_ATTESA_DI_PROTOCOLLAZIONE);
						protocolloDocumentoPi3Bean.setCodStatoInvio(Pi3Constants.PI3_DOCUMENT_SEND_STATE_PREDISPOSED);
					}
				} else {
					protocolloDocumentoPi3Bean.setCodStatoInvio(Pi3Constants.PI3_DOCUMENT_SEND_STATE_PREDISPOSED);
				}

			} else {
				protocolloDocumentoPi3Bean.setCodStatoInvio(Pi3Constants.PI3_DOCUMENT_SEND_STATE_PROTOCOLLATO);
			}

			protocolloDocumentoPi3Bean.setFlgNotificaAnnullamento(null);

			boolean isInsertProtocolloDocumentoMainPi3OnSil = dbManager
					.insertProtocolloDocumentoPi3(protocolloDocumentoPi3Bean);
			_logger.debug(
					"[ProtocolloPi3Manager] -> insertProtocolloDocumentoPi3 [MAIN DOCUMENT] [DB] -> isInsertProtocolloDocumentoMainPi3OnSil: "
							+ isInsertProtocolloDocumentoMainPi3OnSil);

			/*
			 * } else{
			 * 
			 * _logger.
			 * error("[ProtocolloPi3Manager] -> inviaProtocolloPi3 ---> ERROR: MAIN DOCUMENT NON INVIATO POICHE' IL FILE HA LUNGHEZZA ZERO"
			 * ); protocolloInviato = false;
			 * 
			 * throw new
			 * Pi3Exception("[ProtocolloPi3Manager] -> inviaProtocolloPi3 ---> ERROR: MAIN DOCUMENT NON INVIATO POICHE' IL FILE HA LUNGHEZZA ZERO"
			 * , null, Pi3Constants.PI3_DOCUMENT_SEND_STATE_ERRORE_NEI_DATI_INVIATI, creaDocumentPi3Bean); }
			 */

			if (creaDocumentPi3Bean.getLstDocumentiAllegati() == null) {
				// if(creaDocumentPi3Bean.getLstDocumentiAllegati().size() == 0) {
				_logger.debug("[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> nessun allegato da inviare a Pi3");
				// }
			}

			// Protocolla gli Allegati (Documenti Associati della Pratica SPIL) in Pi3, associandoli al 'Main Document'
			if (creaDocumentPi3Bean.getLstDocumentiAllegati() != null) {
				if (creaDocumentPi3Bean.getLstDocumentiAllegati().size() == 0) {
					_logger.debug("[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> nessun allegato da inviare a Pi3");

				} else {

					int j = 0;
					for (Documento documentoSil : creaDocumentPi3Bean.getLstDocumentiAllegati()) {
						boolean allegatoProtocollatoPi3 = false;

						if (documentoSil.getTempFile().length() != 0) {

							try {
								allegatoProtocollatoPi3 = wsManager.addAttachesFileInProject(documentPi3Inviato.getId(),
										documentoSil, "ALLEGATO -" + documentoSil.getStrNomeDoc()
												+ "- DEL MAIN DOCUMENT " + documentPi3Inviato.getId());
								_logger.debug(
										"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> addAttachesFileInProject [WS] [allegato nr: "
												+ j + "] -> MAIN DOCUMENT PI3 ID: " + documentPi3Inviato.getId()
												+ " - FILE ALLEGATO SIL ID (PRG_DOCUMENTO): "
												+ documentoSil.getPrgDocumento() + " --> allegatoProtocollatoPi3: "
												+ allegatoProtocollatoPi3);
							} catch (Exception ex) {
								_logger.error(
										"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> addAttachesFileInProject [WS] [allegato nr: "
												+ j + "] -> MAIN DOCUMENT PI3 ID: " + documentPi3Inviato.getId()
												+ " - FILE ALLEGATO SIL ID (PRG_DOCUMENTO): "
												+ documentoSil.getPrgDocumento() + " --> allegatoProtocollatoPi3: "
												+ "ERROR: ALLEGATO NON INVIATO ALLA PROTOCOLLAZIONE PI3 - ERROR: "
												+ ex.getMessage());
							}

						} else {
							_logger.warn(
									"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> addAttachesFileInProject [WS] [allegato nr: "
											+ j + "] -> MAIN DOCUMENT PI3 ID: " + documentPi3Inviato.getId()
											+ " - FILE ALLEGATO SIL ID (PRG_DOCUMENTO): "
											+ documentoSil.getPrgDocumento() + " --> allegatoProtocollatoPi3: "
											+ "WARNING: ALLEGATO NON INVIATO ALLA PROTOCOLLAZIONE PI3 POICHE' IL FILE HA LUNGHEZZA 0");
						}

						// Inserisce il 'File Allegato' Pi3 appena protocollato (o andato in errore) nel DB SPIL
						// (AM_PROTOCOLLO_DOCUMENTO_PITRE)
						ProtocolloDocumentoPi3Bean protocolloDocumentoAllegatoPi3Bean = new ProtocolloDocumentoPi3Bean();

						protocolloDocumentoAllegatoPi3Bean.setPrgProtPitre(prgDocPi3);

						protocolloDocumentoAllegatoPi3Bean
								.setFlgPrincipale(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_FLG_PRINCIPALE_NO);
						// Se l'Allegato e' di TIPO FILE XML...
						if (!StringUtils.isEmpty(documentoSil.getStrNote())) {
							if (documentoSil.getStrNote()
									.equalsIgnoreCase(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_XML)) {
								protocolloDocumentoAllegatoPi3Bean
										.setFlgPrincipale(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_FLG_PRINCIPALE_XML);
							}
						}

						protocolloDocumentoAllegatoPi3Bean.setPrgDocumento(documentoSil.getPrgDocumento());
						protocolloDocumentoAllegatoPi3Bean.setCdnUtMod(creaDocumentPi3Bean.getCdnUtMod());
						protocolloDocumentoAllegatoPi3Bean.setCdnUtins(creaDocumentPi3Bean.getCdnUtins());
						protocolloDocumentoAllegatoPi3Bean.setDtMins(creaDocumentPi3Bean.getDtMins());
						protocolloDocumentoAllegatoPi3Bean.setDtmMod(creaDocumentPi3Bean.getDtmMod());

						if (!StringUtils.isEmpty(documentPi3Inviato.getProtocolDate())) {
							protocolloDocumentoAllegatoPi3Bean
									.setDatInvio(format.parse(documentPi3Inviato.getProtocolDate()));
						} else {
							protocolloDocumentoAllegatoPi3Bean.setDatInvio(new Date());
						}

						if (allegatoProtocollatoPi3) {
							protocolloDocumentoAllegatoPi3Bean
									.setCodStatoInvio(Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_AGGIUNTO);
						} else {
							protocolloDocumentoAllegatoPi3Bean
									.setCodStatoInvio(Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_PRESA_VISIONE);
						}

						// VERIFICA SE L'ALLEGATO E' IN FASE DI 'PRESA VISIONE'...
						// flgDocAmm (proprieta' di 'appoggio' dell'oggetto Documento) = flgPresaVisione
						if (!StringUtils.isEmpty(documentoSil.getFlgDocAmm())) {
							if (documentoSil.getFlgDocAmm().equalsIgnoreCase("S")) {
								protocolloDocumentoAllegatoPi3Bean.setCodStatoInvio(
										Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_PRESA_VISIONE);
							}
						}

						// ... ALTRIMENTI LO INSERISCE COME ALLEGATO IN FASE DI 'CARICAMENTO SUCCESSIVO'
						// flgDocIdentifP (proprieta' di 'appoggio' dell'oggetto Documento) = flgCaricamentoSuccessivo
						if (!StringUtils.isEmpty(documentoSil.getFlgDocIdentifP())) {
							if (documentoSil.getFlgDocIdentifP().equalsIgnoreCase("S")) {
								protocolloDocumentoAllegatoPi3Bean.setCodStatoInvio(
										Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_CARICAMENTO_SUCCESSIVO);
							}
						}

						protocolloDocumentoAllegatoPi3Bean.setFlgNotificaAnnullamento(null);

						boolean isInsertProtocolloDocumentoAllegatoPi3OnSil = dbManager
								.insertProtocolloDocumentoPi3(protocolloDocumentoAllegatoPi3Bean);

						_logger.debug(
								"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> addAttachesFileInProject [DB] [allegato nr: "
										+ j + "] -> MAIN DOCUMENT PI3 ID: " + documentPi3Inviato.getId()
										+ " - FILE ALLEGATO SIL ID (PRG_DOCUMENTO): " + documentoSil.getPrgDocumento()
										+ " --> isInsertProtocolloDocumentoAllegatoPi3OnSil: "
										+ isInsertProtocolloDocumentoAllegatoPi3OnSil);

						j++;
					}

				}
			}

		} else {
			_logger.warn(
					"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> la pratica non verra processata poiche e' stata gia inviata precedentemente per la protocollazione");
			throw new Exception("Pratica gia' inviata in precedenza");
		}

		return protocolloInviato;

	}

	public void inviaNuoviAllegati(ProtocolloPi3Bean protocolloPi3Bean, ArrayList<Documento> lstNewDocAllegati)
			throws Exception {

		/**
		 * 
		 * VERIFICHE DATI PASSATI IN INPUT
		 *
		 */
		if (protocolloPi3Bean == null) {
			_logger.error("[ProtocolloPi3Manager] -> inviaNuoviAllegati -> protocolloPi3Bean non impostato in input");
			throw new Exception("protocolloPi3Bean non impostato in input");
		}

		if (lstNewDocAllegati == null) {
			_logger.error("[ProtocolloPi3Manager] -> inviaNuoviAllegati -> Nessun nuovo allegato impostato in input");
			throw new Exception("Nessun nuovo allegato impostato in input");
		}

		if (lstNewDocAllegati.size() == 0) {
			_logger.error("[ProtocolloPi3Manager] -> inviaNuoviAllegati -> Nessun nuovo allegato impostato in input");
			throw new Exception("Nessun nuovo allegato impostato in input");
		}

		ProtocolloPi3DBManager dbManager = new ProtocolloPi3DBManager();
		ProtocolloPi3WsManager wsManager = new ProtocolloPi3WsManager();

		int j = 0;
		for (Documento documentoSil : lstNewDocAllegati) {

			boolean allegatoProtocollatoPi3 = false;

			if (documentoSil.getTempFile().length() != 0) {

				try {
					allegatoProtocollatoPi3 = wsManager.addAttachesFileInProject(protocolloPi3Bean.getStridDoc(),
							documentoSil, "ALLEGATO -" + documentoSil.getStrNomeDoc() + "- DEL MAIN DOCUMENT "
									+ protocolloPi3Bean.getStridDoc());
					_logger.debug(
							"[ProtocolloPi3Manager] -> inviaNuoviAllegati -> addAttachesFileInProject [WS] [allegato nr: "
									+ j + "] -> MAIN DOCUMENT PI3 ID: " + protocolloPi3Bean.getStridDoc()
									+ " - FILE ALLEGATO SIL ID (PRG_DOCUMENTO): " + documentoSil.getPrgDocumento()
									+ " --> allegatoProtocollatoPi3: " + allegatoProtocollatoPi3);
				} catch (Exception ex) {
					_logger.error(
							"[ProtocolloPi3Manager] -> inviaNuoviAllegati -> addAttachesFileInProject [WS] [allegato nr: "
									+ j + "] -> MAIN DOCUMENT PI3 ID: " + protocolloPi3Bean.getStridDoc()
									+ " - FILE ALLEGATO SIL ID (PRG_DOCUMENTO): " + documentoSil.getPrgDocumento()
									+ " --> allegatoProtocollatoPi3: "
									+ "ERROR: ALLEGATO NON INVIATO ALLA PROTOCOLLAZIONE PI3 - ERROR: "
									+ ex.getMessage());
				}

				// Inserisce il 'File Allegato' Pi3 appena protocollato (o andato in errore) nel DB SPIL
				// (AM_PROTOCOLLO_DOCUMENTO_PITRE)
				ProtocolloDocumentoPi3Bean protocolloDocumentoAllegatoPi3Bean = new ProtocolloDocumentoPi3Bean();

				protocolloDocumentoAllegatoPi3Bean.setPrgProtPitre(protocolloPi3Bean.getPrgProtPitre());

				protocolloDocumentoAllegatoPi3Bean
						.setFlgPrincipale(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_FLG_PRINCIPALE_NO);

				protocolloDocumentoAllegatoPi3Bean.setPrgDocumento(documentoSil.getPrgDocumento());
				protocolloDocumentoAllegatoPi3Bean.setCdnUtMod(documentoSil.getCdnUtMod());
				protocolloDocumentoAllegatoPi3Bean.setCdnUtins(documentoSil.getCdnUtIns());

				if (documentoSil.getDtmIns() != null) {
					protocolloDocumentoAllegatoPi3Bean.setDtMins(format.parse(documentoSil.getDtmIns()));
				}
				if (documentoSil.getDtmMod() != null) {
					protocolloDocumentoAllegatoPi3Bean.setDtmMod(format.parse(documentoSil.getDtmMod()));
				}

				if (protocolloPi3Bean.getDataProt() != null) {
					protocolloDocumentoAllegatoPi3Bean.setDatInvio(protocolloPi3Bean.getDataProt());
				} else {
					protocolloDocumentoAllegatoPi3Bean.setDatInvio(new Date());
				}

				if (allegatoProtocollatoPi3) {
					protocolloDocumentoAllegatoPi3Bean
							.setCodStatoInvio(Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_AGGIUNTO);
				} else {
					protocolloDocumentoAllegatoPi3Bean
							.setCodStatoInvio(Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_PRESA_VISIONE);
				}

				// VERIFICA SE L'ALLEGATO E' IN FASE DI 'PRESA VISIONE'...
				// flgDocAmm (proprieta' di 'appoggio' dell'oggetto Documento) = flgPresaVisione
				if (!StringUtils.isEmpty(documentoSil.getFlgDocAmm())) {
					if (documentoSil.getFlgDocAmm().equalsIgnoreCase("S")) {
						protocolloDocumentoAllegatoPi3Bean
								.setCodStatoInvio(Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_PRESA_VISIONE);
					}
				}

				// ... ALTRIMENTI LO INSERISCE COME ALLEGATO IN FASE DI 'CARICAMENTO SUCCESSIVO'
				// flgDocIdentifP (proprieta' di 'appoggio' dell'oggetto Documento) = flgCaricamentoSuccessivo
				if (!StringUtils.isEmpty(documentoSil.getFlgDocIdentifP())) {
					if (documentoSil.getFlgDocIdentifP().equalsIgnoreCase("S")) {
						protocolloDocumentoAllegatoPi3Bean.setCodStatoInvio(
								Pi3Constants.PI3_DOCUMENT_SEND_STATE_ALLEGATO_NOTA_CARICAMENTO_SUCCESSIVO);
					}
				}

				protocolloDocumentoAllegatoPi3Bean.setFlgNotificaAnnullamento(null);

				boolean isInsertProtocolloDocumentoAllegatoPi3OnSil = dbManager
						.insertProtocolloDocumentoPi3(protocolloDocumentoAllegatoPi3Bean);

				_logger.debug(
						"[ProtocolloPi3Manager] -> inviaNuoviAllegati -> addAttachesFileInProject [DB] [allegato nr: "
								+ j + "] -> MAIN DOCUMENT PI3 ID: " + protocolloPi3Bean.getStridDoc()
								+ " - FILE ALLEGATO SIL ID (PRG_DOCUMENTO): " + documentoSil.getPrgDocumento()
								+ " --> isInsertProtocolloDocumentoAllegatoPi3OnSil: "
								+ isInsertProtocolloDocumentoAllegatoPi3OnSil);

			} else {
				_logger.warn(
						"[ProtocolloPi3Manager] -> inviaNuoviAllegati -> addAttachesFileInProject [WS] [allegato nr: "
								+ j + "] -> MAIN DOCUMENT PI3 ID: " + protocolloPi3Bean.getStridDoc()
								+ " - FILE ALLEGATO SIL ID (PRG_DOCUMENTO): " + documentoSil.getPrgDocumento()
								+ " --> allegatoProtocollatoPi3: "
								+ "WARNING: ALLEGATO NON INVIATO ALLA PROTOCOLLAZIONE PI3 POICHE' IL FILE HA LUNGHEZZA 0");
			}

			j++;

		}

	}

	private String getTypeErrorFromException(Exception ex) {

		String typeError = Pi3Constants.PI3_DOCUMENT_SEND_STATE_ERRORE_NEI_DATI_INVIATI;

		if (ex instanceof SocketTimeoutException) {
			typeError = Pi3Constants.PI3_DOCUMENT_SEND_STATE_ERRORE_INDISPONIBILITA_SERVIZIO;
			_logger.error("[ProtocolloPi3Manager] -> getTypeErrorFromException -> SocketTimeoutException");
		} else if (ex instanceof ConnectException) {
			typeError = Pi3Constants.PI3_DOCUMENT_SEND_STATE_ERRORE_INDISPONIBILITA_SERVIZIO;
			_logger.error("[ProtocolloPi3Manager] -> getTypeErrorFromException -> ConnectException");
		} else if (ex.getMessage().contains("Proxy Error")) {
			typeError = Pi3Constants.PI3_DOCUMENT_SEND_STATE_ERRORE_INDISPONIBILITA_SERVIZIO;
			_logger.error("[ProtocolloPi3Manager] -> getTypeErrorFromException -> Proxy Error");
		} else if (ex.getMessage().contains("Read timed out")) {
			typeError = Pi3Constants.PI3_DOCUMENT_SEND_STATE_ERRORE_INDISPONIBILITA_SERVIZIO;
			_logger.error("[ProtocolloPi3Manager] -> getTypeErrorFromException -> Read timed out");
		}

		return typeError;
	}

	// Imposta la proprieta' per Repertoriare il Documento in Pi3 (se sono impostati su Pi3 i Diritti di Creazione e
	// Modifica)
	private Template getTemplateAndSetField(ProtocolloPi3WsManager wsManager) throws Exception {

		_logger.debug("[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> getTemplateAndSetField");

		Template template = wsManager.getTemplateDoc();

		if (template.getFields() != null) {
			if (template.getFields().length > 0) {
				if (template.getFields()[0] != null) {
					UtilsConfig utilsConfig = new UtilsConfig(Pi3Constants.PI3_DOCUMENT_WS_INPUT_CODE_RF);
					String codeRF = utilsConfig.getValoreConfigurazione();

					template.getFields()[0].setCounterToTrigger(true);
					template.getFields()[0].setCodeRegisterOrRF(codeRF);
					template.getFields()[0].setValue("0");

					_logger.debug(
							"[ProtocolloPi3Manager] -> inviaProtocolloPi3 -> getTemplateAndSetField -> setCounterToTrigger = true - setCodeRegisterOrRF = "
									+ codeRF + " - setValue = 0");
				}
			}
		}

		return template;

	}

}
