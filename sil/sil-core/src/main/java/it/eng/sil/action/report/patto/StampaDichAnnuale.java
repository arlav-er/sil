package it.eng.sil.action.report.patto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.firma.grafometrica.FirmaDocumenti;
import it.eng.sil.module.pi3.Pi3Constants;
import it.eng.sil.module.pi3.ProtocolloPi3DBManager;
import it.eng.sil.security.User;

public class StampaDichAnnuale extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaDichAnnuale.class.getName());
	private TransactionQueryExecutor txExecutor = null;
	private int errorCodeInterno = 0;

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		String dataOdierna = DateUtils.getNow();
		String dataDichAnnuale = null;
		SourceBean row = null;
		Boolean result = null;

		try {
			dataDichAnnuale = request.getAttribute("DATDICHANNUALE") != null
					? request.getAttribute("DATDICHANNUALE").toString()
					: null;

			if (dataDichAnnuale == null) {
				response.updAttribute("messageResult", "La data dichiarazione è obbligatoria.");
				errorCodeInterno = MessageCodes.General.OPERATION_FAIL;
				throw new Exception();
			}

			// controllo data futura
			if (DateUtils.compare(dataDichAnnuale, dataOdierna) > 0) {
				response.updAttribute("messageResult",
						"Non e' possibile rilasciare una dichiarazione con data futura.");
				errorCodeInterno = MessageCodes.ConfermaAnnualeDid.DICH_FUTURA;
				throw new Exception();
			}

			int annoDichAnnuale = DateUtils.getAnno(dataDichAnnuale);
			String dataFineDoc = "31/12/" + annoDichAnnuale;
			// controllo dichiarazione pregressa (non bloccante)
			int annoAttuale = DateUtils.getAnno();
			if (annoDichAnnuale < annoAttuale) {
				String confirmIns = request.getAttribute("confirmOperation") != null
						? request.getAttribute("confirmOperation").toString()
						: "";
				if (confirmIns.equalsIgnoreCase("false")) {
					response.updAttribute("operationResult", "WARNING");
					response.updAttribute("messageResult",
							"Attenzione, si tratta di una dichiarazione pregressa, vuoi proseguire?");
					return;
				}
			}

			txExecutor = new TransactionQueryExecutor(getPool());
			txExecutor.initTransaction();

			// controllo anno della did deve essere <= anno data dichiarazione annuale
			String dataDichiarazione = request.getAttribute("dataDichDid") != null
					? request.getAttribute("dataDichDid").toString()
					: null;
			if (dataDichiarazione != null) {
				int annoDid = DateUtils.getAnno(dataDichiarazione);
				if (annoDid >= annoDichAnnuale) {
					response.updAttribute("messageResult",
							"Non e' possibile rilasciare una dichiarazione nello stesso anno(o anno precedente) della did.");
					errorCodeInterno = MessageCodes.ConfermaAnnualeDid.DICH_ANNO_DID;
					throw new Exception();
				}
			}

			// controllo esistenza di una dichiarazione annuale nello stesso anno
			row = null;
			row = doSelect(request, response, "QUERY_SELECT_ESISTENZA_DICH");
			if (row.containsAttribute("ROW.DATDICHANNUALE")) {
				response.updAttribute("messageResult",
						"E' stata gia' rilasciata una dichiarazione nel corso dello stesso anno.");
				errorCodeInterno = MessageCodes.ConfermaAnnualeDid.DICH_ESISTENTE_ANNO;
				throw new Exception();
			}

			// inserimento della dichiarazione annuale e protocollazione documento associato
			row = null;
			row = doSelect(request, response, "GET_AM_DID_ANNUALE_NEXTVAL");
			Object prgDidAnnuale = row.getAttribute("row.DO_NEXTVAL");
			request.setAttribute("PRGDICHANNUALE", prgDidAnnuale);
			result = (Boolean) doInsert(request, response, "QUERY_INSERT_DICH");

			if (result.booleanValue()) {
				String apriFile = (String) request.getAttribute("apriFileBlob");
				if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
					BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
					this.openDocument(request, response, prgDoc);
				} else {
					setStrDescrizione("Dichiarazione annuale lavoratore");
					String tipoFile = (String) request.getAttribute("tipoFile");
					if (tipoFile != null)
						setStrNomeDoc("DichiarazioneAnnualeDisponibilita." + tipoFile);
					else
						setStrNomeDoc("DichiarazioneAnnualeDisponibilita.pdf");

					setReportPath("patto/DichImmDisp_TN_CC.rpt");
					Map prompts = new HashMap();
					prompts.put("par_DichDisp", request.getAttribute("PRGDICHDISP"));
					prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
					User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
					prompts.put("codCpi", user.getCodRif());
					prompts.put("dataDichAnnuale", dataDichAnnuale);
					// solo se e' richiesta la protocollazione i parametri vengono
					// inseriti nella Map
					addPromptFieldsProtocollazione(prompts, request);
					// ora si chiede di usare il passaggio dei parametri per nome e
					// non per posizione (col vettore, passaggio di default)
					setPromptFields(prompts);
					setStrChiavetabella(prgDidAnnuale.toString());
					setDatInizio(dataDichAnnuale);
					setDatFine(dataFineDoc);
					boolean risDoc = insertDocument(request, response, txExecutor);
					if (risDoc) {
						txExecutor.commitTransaction();

						// GESTIONE FIRMA GRAFOMETRICA
						if (request.getAttribute("firmaGrafometrica") != null
								&& request.getAttribute("firmaGrafometrica").toString().equals("OK")) {

							try {

								Documento actualDocument = getDocumento();
								Documento prgDocumento = new Documento(actualDocument.getPrgDocumento());
								actualDocument.setNumKloDocumento(prgDocumento.getNumKloDocumento());
								_logger.debug("[DID Annuale] --> firmaGrafometrica --> NumKlo: "
										+ actualDocument.getNumKloDocumento());

								// VERIFICA SE IL TIPO DI DOCUMENTO E' PREDISPOSTO PER ESSERE FIRMATO GRAFOMETRICAMENTE
								ProtocolloPi3DBManager dbManager = new ProtocolloPi3DBManager();
								// boolean isDocumentFirmabile =
								// dbManager.isAllegatoDocumentoFirmato(actualDocument.getPrgDocumento().toString());
								BigDecimal prgTemplateStampa = dbManager
										.getPrgTemplateStampa(Pi3Constants.PI3_DOCUMENT_TYPE_DID_ANNUALE);
								boolean isDocumentTypeFirmabile = dbManager
										.isDocumentTypeGraphSignature(prgTemplateStampa.toString());

								if (isDocumentTypeFirmabile) {

									TransactionQueryExecutor txExecutorFirma = null;
									ArrayList messagesWarning = new ArrayList();
									try {
										FirmaDocumenti firma = new FirmaDocumenti();
										String ipOperatore = request.getAttribute("ipOperatore").toString();
										txExecutorFirma = new TransactionQueryExecutor(getPool());
										boolean esitoFirma = firma.firmaDocumento(request, response, user,
												txExecutorFirma, actualDocument, ipOperatore);
										if (!esitoFirma) {
											// response.updAttribute("messageWarning", "Errore durante la firma
											// grafometrica del documento.");
											errorCodeInterno = MessageCodes.FirmaGrafometrica.WS_OPERATION_FAIL;
											messagesWarning.add(MessageCodes.FirmaGrafometrica.WS_OPERATION_FAIL);
											throw new Exception();
										} else {
											setOperationSuccess(request, response);
										}
									} catch (Exception firmaEx) {

										if (errorCodeInterno != MessageCodes.FirmaGrafometrica.WS_OPERATION_FAIL) {
											// response.updAttribute("messageWarning", "Il Servizio di Firma
											// Grafometrica non è al momento disponibile. Procedere con la firma
											// autografa del documento");
											errorCodeInterno = MessageCodes.FirmaGrafometrica.WS_NON_RAGGIUNGIBILE;
											messagesWarning.add(MessageCodes.FirmaGrafometrica.WS_NON_RAGGIUNGIBILE);
										}
										setOperationSuccessWithWarning(request, response, messagesWarning);
										return;
										// throw new Exception(firmaEx);

									} finally {
										if (txExecutorFirma != null) {
											txExecutorFirma.closeConnTransaction();
											_logger.debug(
													"STAMPA DICHIARAZIONE ANNUALE - Gestione Firma Grafometrica - TransactionQueryExecutor close...");
										}
									}

								} else {
									it.eng.sil.util.TraceWrapper.debug(_logger,
											"DichiarazioneDisponibilita Annuale:service(): Tipo del Documento non firmabile grafometricamente, si procede alla trasformazione del documento normalmente senza applicazione dei campi firma",
											null);
								}

							} catch (Exception firmaEx) {
								it.eng.sil.util.TraceWrapper.error(_logger,
										"DichiarazioneDisponibilita Annuale:service():errore recupero firmabilita' grafometrica del tipo di documento",
										firmaEx);
								setOperationFail(request, response, firmaEx);
							}

						} else {
							setOperationSuccess(request, response);
						}

					} else {
						String erroreCodice = (String) response.getAttribute("errorCode");
						if (!StringUtils.isEmpty(erroreCodice)) {
							if (erroreCodice.equalsIgnoreCase(MessageCodes.General.CONCORRENZA + "")) {
								errorCodeInterno = MessageCodes.General.CONCORRENZA;
								response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.CONCORRENZA);
								throw new Exception();
							}
						} else {

							response.updAttribute("messageResult", "Errore durante l'inserimento del documento.");
							errorCodeInterno = MessageCodes.General.OPERATION_FAIL;
							throw new Exception();
						}
					}
				}
			} else {
				response.updAttribute("messageResult", "Errore durante l'inserimento del documento.");
				errorCodeInterno = MessageCodes.General.OPERATION_FAIL;
				throw new Exception();
			}
		}

		catch (EMFInternalError emf) {
			if (txExecutor != null) {
				try {
					txExecutor.rollBackTransaction();
				} catch (EMFInternalError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			_logger.error(emf.getMessage());

			if (emf.getNativeException() instanceof SQLException) {
				if (((SQLException) emf.getNativeException()).getErrorCode() == MessageCodes.General.CONCORRENZA) {
					setOperationFail(request, response, MessageCodes.General.CONCORRENZA);
					try {
						response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.CONCORRENZA);
					} catch (SourceBeanException e) {
						_logger.error(e.getMessage());
					}
				} else {
					setOperationFail(request, response, MessageCodes.General.OPERATION_FAIL);
					try {
						response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.OPERATION_FAIL);
					} catch (SourceBeanException e) {
						_logger.error(e.getMessage());
					}
				}
			} else {
				setOperationFail(request, response, MessageCodes.General.OPERATION_FAIL);
				try {
					response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.OPERATION_FAIL);
				} catch (SourceBeanException e) {
					_logger.error(e.getMessage());
				}
			}
		}

		catch (Exception e) {
			try {
				if (txExecutor != null) {
					txExecutor.rollBackTransaction();
				}
				if (errorCodeInterno == 0) {
					response.updAttribute("messageResult", e.toString());
				}
			} catch (Exception rbe) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "Impossibile eseguire la rollBack", rbe);
			}
			if (errorCodeInterno == 0) {
				setOperationFail(request, response, e);
			} else {
				setOperationFail(request, response, errorCodeInterno);
			}
			return;
		}
	}

	protected String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

	public SourceBean doSelect(SourceBean request, SourceBean response, String queryName) throws Exception {
		SourceBean statement = getSelectStatement(queryName);

		SourceBean beanRows = null;
		beanRows = (SourceBean) txExecutor.executeQuery(getRequestContainer(), getResponseContainer(), statement,
				"SELECT");
		try {
			response.setAttribute("SELECT_OK", "TRUE");
		} catch (Exception ex) {
			response.setAttribute("SELECT_FAIL", "TRUE");
		}

		return beanRows;
	}

	public Object doInsert(SourceBean request, SourceBean response, String queryName) throws Exception {
		SourceBean statement = getSelectStatement(queryName);

		Boolean esito = new Boolean(false);

		try {
			esito = (Boolean) txExecutor.executeQuery(getRequestContainer(), getResponseContainer(), statement,
					"INSERT");
			response.setAttribute("INSERT_OK", "TRUE");
		} catch (Exception ex) {
			response.setAttribute("INSERT_FAIL", "TRUE");
			return esito;
		}
		if ((esito == null) || !esito.booleanValue()) {

			return esito;
		}
		return esito;
	}

	protected SourceBean getSelectStatement(String queryName) {
		SourceBean beanQuery = null;
		beanQuery = (SourceBean) getConfig().getAttribute(queryName);

		return beanQuery;
	}

}
