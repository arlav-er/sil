package it.eng.sil.action.report.consenso;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.base.XMLObject;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.module.consenso.Consenso;
import it.eng.sil.module.consenso.ConsensoFirmaBean;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

public class InserisciConsenso extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InserisciConsenso.class.getName());

	@Override
	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		_logger.debug("[InserisciConsenso] INIT");

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");

		if ((apriFile != null) && apriFile.equalsIgnoreCase("true")) {

			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));

			_logger.debug("[InserisciConsenso] apriFile...");
			_logger.debug("[InserisciConsenso] prgDoc: " + prgDoc);

			this.openDocument(request, response, prgDoc);

		} else {

			try {

				String cdnLavoratore = (String) request.getAttribute("cdnLavoratore");

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");

				if (salva != null && salva.equalsIgnoreCase("true")) {

					_logger.debug("[InserisciConsenso] salva documento");

					try {

						BigDecimal isPresentPrgDocConsenso = existDocumentoConsenso(cdnLavoratore, "DICH");
						_logger.debug("[InserisciConsenso] existDocumentoConsenso (cdnLavoratore: " + cdnLavoratore
								+ "): " + isPresentPrgDocConsenso);

						String tipoFile = (String) request.getAttribute("tipoFile");

						if (tipoFile != null) {
							setStrNomeDoc("adesioneFirmaGrafometrica." + tipoFile);
						} else {
							setStrNomeDoc("adesioneFirmaGrafometrica.pdf");
						}

						// documento di input
						setCodMonoIO("I");
						setStrDescrizione("Adesione Firma Grafometrica");

						setReportPath("Consenso/adesione_firma_grafometrica_CC.rpt");// DEFAULT

						setFlgAutocertificazione("");
						setFlgDocIdentifP("");
						setFlgDocAmm("");

						if (isPresentPrgDocConsenso.compareTo(BigDecimal.ZERO) == 0) {
							/**
							 * 
							 * INSERIMENTO CONSENSO - CRYSTAL CLEAR
							 * 
							 */
							_logger.debug("[InserisciConsenso] trasform document by Crystal Clear processing...");

							// impostazione parametri del report
							Map prompts = new HashMap();

							prompts.put("cdnLavoratore", cdnLavoratore);

							// solo se e' richiesta la protocollazione i parametri vengono inseriti nella Map
							addPromptFieldsProtocollazione(prompts, request);

							// ora si chiede di usare il passaggio dei parametri per nome e
							// non per posizione (col vettore, passaggio di default)
							setPromptFields(prompts);

							// // Settaggio strchiavetabella
							// String strChiaveTabella = (String) request.getAttribute("strChiaveTabella");
							// if ((strChiaveTabella != null) && !strChiaveTabella.equals("")) {
							// setStrChiavetabella(strChiaveTabella);
							// }

							boolean documentoInserito = insertDocument(request, response);
							_logger.debug(
									"[InserisciConsenso] documento by Crystal Clear inserito: " + documentoInserito);

							if (documentoInserito) {

								/**
								 * 
								 * INSERIMENTO CONSENSO - WEB SERVICE / DB
								 * 
								 */
								_logger.debug(
										"[InserisciConsenso] inserimento Consenso attraverso Web Services processing...");

								SessionContainer sessionContainer = getRequestContainer().getSessionContainer();

								User user = (User) sessionContainer.getAttribute(User.USERID);

								String dataRaccoltaOggi = (String) request.getAttribute("dataRaccoltaOggi");

								AccessoSemplificato _db = new AccessoSemplificato(this);
								_db.setSectionQuerySelect("GET_INFO_GEN_LAVORATORE");
								SourceBean beanRows = _db.doSelect(request, response, false);
								if (beanRows == null)
									throw new EMFUserError(
											"[InserisciConsenso] errore nel recupero delle informazioni sul Lavoratore",
											0);
								String codiceFiscale = Utils.notNull(beanRows.getAttribute("row.STRCODICEFISCALE"));

								/** Inizializza parametri **/
								Map<String, Object> params = new HashMap<String, Object>();
								params.put("codiceFiscaleLavoratore", codiceFiscale);
								params.put("cdnLavoratore", cdnLavoratore);
								params.put("prgAzienda", "");
								params.put("cdnUtins", user.getCodut());
								params.put("cdnUtmod", user.getCodut());
								params.put("dataRaccoltaOggi", dataRaccoltaOggi);

								Consenso consenso = new Consenso(params);
								try {
									SourceBean resConsenso = consenso.inserisciConsenso();
								} catch (RemoteException e) {
									_logger.error(
											"[InserisciConsenso] -> [RemoteException] errore nell'inserimento del Consenso nel WS/DB");
									throw new EMFUserError(
											"[InserisciConsenso] -> [RemoteException] errore nell'inserimento del Consenso nel WS/DB",
											0);
								} catch (ServiceException e) {
									_logger.error(
											"[InserisciConsenso] -> [ServiceException] errore nell'inserimento del Consenso nel WS/DB");
									throw new EMFUserError(
											"[InserisciConsenso] -> [ServiceException] errore nell'inserimento del Consenso nel WS/DB",
											0);
								} catch (SourceBeanException e) {
									_logger.error(
											"[InserisciConsenso] -> [SourcebeanException] errore nell'inserimento del Consenso nel WS/DB");
									throw new EMFUserError(
											"[InserisciConsenso] -> [SourcebeanException] errore nell'inserimento del Consenso nel WS/DB",
											0);
								}

							} else {
								_logger.error(
										"[InserisciConsenso] -> [Error] errore nella protocollazione documento Adesione del Consenso");
								throw new EMFUserError(
										"[InserisciConsenso] -> [Error] errore nella protocollazione documento Adesione del Consenso",
										0);
							}

						} else {

							_logger.debug(
									"[InserisciConsenso] protocollazione effettuata precedentemente, si procede alla sola visualizzazione del documento...");
							// showDocument(request, response);
							openDocument(request, response, isPresentPrgDocConsenso);

							/**
							 * 
							 * INSERIMENTO CONSENSO - WEB SERVICE / DB
							 * 
							 */
							_logger.debug(
									"[InserisciConsenso] inserimento Consenso attraverso Web Services processing (protocollazione effettuata precedentemente)...");

							SessionContainer sessionContainer = getRequestContainer().getSessionContainer();

							User user = (User) sessionContainer.getAttribute(User.USERID);

							String dataRaccoltaOggi = (String) request.getAttribute("dataRaccoltaOggi");

							AccessoSemplificato _db = new AccessoSemplificato(this);
							_db.setSectionQuerySelect("GET_INFO_GEN_LAVORATORE");
							SourceBean beanRows = _db.doSelect(request, response, false);
							if (beanRows == null)
								throw new EMFUserError(
										"[InserisciConsenso] errore nel recupero delle informazioni sul Lavoratore", 0);
							String codiceFiscale = Utils.notNull(beanRows.getAttribute("row.STRCODICEFISCALE"));

							/** Inizializza parametri **/
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("codiceFiscaleLavoratore", codiceFiscale);
							params.put("cdnLavoratore", cdnLavoratore);
							params.put("prgAzienda", "");
							params.put("cdnUtins", user.getCodut());
							params.put("cdnUtmod", user.getCodut());
							params.put("dataRaccoltaOggi", dataRaccoltaOggi);

							Consenso consenso = new Consenso(params);
							try {
								SourceBean resConsenso = consenso.inserisciConsenso();
							} catch (RemoteException e) {
								_logger.error(
										"[InserisciConsenso] -> [RemoteException] errore nell'inserimento del Consenso nel WS/DB");
								throw new EMFUserError(
										"[InserisciConsenso] -> [RemoteException] errore nell'inserimento del Consenso nel WS/DB",
										0);
							} catch (ServiceException e) {
								_logger.error(
										"[InserisciConsenso] -> [ServiceException] errore nell'inserimento del Consenso nel WS/DB");
								throw new EMFUserError(
										"[InserisciConsenso] -> [ServiceException] errore nell'inserimento del Consenso nel WS/DB",
										0);
							} catch (SourceBeanException e) {
								_logger.error(
										"[InserisciConsenso] -> [SourcebeanException] errore nell'inserimento del Consenso nel WS/DB");
								throw new EMFUserError(
										"[InserisciConsenso] -> [SourcebeanException] errore nell'inserimento del Consenso nel WS/DB",
										0);
							}

						}

					} catch (Exception e1) {
						_logger.error(
								"[InserisciConsenso] -> [Exception] errore nella verifica della Procollazione esistente del Consenso. Errore: "
										+ e1.getMessage());
						throw new EMFUserError(
								"[InserisciConsenso] -> [Exception] errore nella verifica della Procollazione esistente del Consenso. Errore: "
										+ e1.getMessage(),
								0);
					}

				} else if (apri != null && apri.equalsIgnoreCase("true")) {

					_logger.debug("[InserisciConsenso] apri documento");
					showDocument(request, response);

				}

			} catch (EMFUserError ue) {

				setOperationFail(request, response, ue);
				// reportFailure(ue, "DichiarazioneDisponibilita.service()", "");

				it.eng.sil.util.TraceWrapper.debug(_logger, "", (XMLObject) ue);

				setOperationFail(request, response, ue);

				reportOperation.reportFailure(ue, "InserisciConsenso.service()",
						"la visualizzazione dei dati della protocollazione non e' possibile a causa della mancanza di un parametro necessario al report.");

				return;

			}
		}

	}

	private BigDecimal existDocumentoConsenso(String cdnlavoratore, String codtipodocumento) throws Exception {

		_logger.debug("EXIST_DOCUMENT_CONSENSO");

		BigDecimal prgDocumentoConsenso = new BigDecimal(0);

		ConsensoFirmaBean bean = new ConsensoFirmaBean();

		Object[] sqlparams = new Object[2];
		sqlparams[0] = cdnlavoratore;
		sqlparams[1] = codtipodocumento;

		if (_logger.isDebugEnabled()) {
			_logger.debug("cdnlavoratore: " + cdnlavoratore);
			_logger.debug("codtipodocumento: " + codtipodocumento);
		}

		SourceBean row = (SourceBean) QueryExecutor.executeQuery("EXIST_DOCUMENT_CONSENSO", sqlparams, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);

			BigDecimal prgDocumento = ((BigDecimal) row.getAttribute("prgdocumento"));

			if (_logger.isDebugEnabled()) {
				_logger.debug("prgDocumento: " + prgDocumento);
			}

			if (prgDocumento != null) {
				prgDocumentoConsenso = prgDocumento;
			}
		}

		return prgDocumentoConsenso;
	}

}
