package it.eng.sil.action.report.anagrafica;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.module.coop.AggiornaCompetenzaIR;
import it.eng.sil.module.coop.PresaAttoCoop;
import it.eng.sil.module.firma.grafometrica.FirmaDocumenti;
import it.eng.sil.module.pi3.Pi3Constants;
import it.eng.sil.module.pi3.ProtocolloPi3DBManager;
import it.eng.sil.security.User;
import it.eng.sil.util.ExceptionUtils;
import it.eng.sil.util.UtilityNumGGTraDate;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

/**
 * @author rolfini featuring roccetti (TraIntraProvinciale), savino (page to action ), riccardi (cooperazione)
 */
public class Trasferimento extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Trasferimento.class.getName());

	private it.eng.afExt.utils.TransactionQueryExecutor txExecutor;
	private int codiceCoop = 0;
	private int codicePoloInCoop = 0;
	private Documento doc = new Documento();
	int errorCode = MessageCodes.General.OPERATION_FAIL;

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// si controllo il tipo di operazione voluta
		boolean soloStampa = request.containsAttribute("stampa_report");
		SourceBean copiaRequest = (SourceBean) request.cloneObject();
		boolean poloInCoop = false;
		if (!soloStampa) {
			// ////////////////////////////////////////
			// si provvede a trasferire il lavoratore
			try {
				txExecutor = new it.eng.afExt.utils.TransactionQueryExecutor(getPool());
				txExecutor.initTransaction();
				boolean trasferisci = trasferisci(request, response);
				if (!trasferisci)
					throw new Exception("fallito trasferimento lavoratore");
				// //////////////////////////////////
				// se e' stata richiesta la gestione della cooperazione
				// applicativa si procede
				boolean forzaCoop = false;
				if (forzaCoop || request.containsAttribute("avanti_coop")) {
					final AggiornaCompetenzaIR aggiornaCoop = new AggiornaCompetenzaIR() {
						// viene fatto l'override di questo metodo per
						// intercettare l'inserimento nella serviceResponse
						// degli errori generati dal modulo della cooperazione.
						// Questo per evitare che la jsp
						// che deve visualizzare il report possa considerare
						// l'errore di questo modulo come un errore
						// del report non mostrando il pdf.
						public void setFailureMessage(int code) {
							Trasferimento.this.codiceCoop = code;
						}
					};
					// /////
					// passo il contesto della action al modulo
					// AggiornaCompetenzaIR
					aggiornaCoop.setRequestContext(this);
					aggiornaCoop.service(request, response);
					// si controllano i tipi di errore generati dal modulo di
					// cooperazione applicativa.
					// bisogna decidere cosa fare al ricaricamento della pagina.
					// N.B. si devono mettere nel SessionContainer cio' che si
					// inserirebbe normalmente nella response.
					// dato che la prossima pagina visualizzata sara' comunque
					// quella del report.
					if (codiceCoop > 0) {
						// per ora non si fa niente, lancio solo la eccezione
						// getRequestContainer().getSessionContainer().setAttribute("trasferito_ccop",
						// "false");
						reportOperation.reportFailure(codiceCoop);
						throw new Exception("fallita cooperazione applicativa");
					} else {
						// getRequestContainer().getSessionContainer().setAttribute("trasferito_ccop",
						// "true");
						;
					}
					if (request.containsAttribute("Coop") && request.getAttribute("Coop").equals("In cooperazione")) {
						poloInCoop = true;
						final PresaAttoCoop presaAttoCoop = new PresaAttoCoop() {
							public void setFailureMessage(int code) {
								Trasferimento.this.codicePoloInCoop = code;
							}
						};
						presaAttoCoop.setRequestContext(this);
						presaAttoCoop.service(request, response);
						if (codicePoloInCoop > 0) {
							reportOperation.reportFailure(codicePoloInCoop);
							throw new Exception("fallita invio richiesta Presa Atto in cooperazione applicativa");
						}
						request.setAttribute("flgStampaDoc", "S");
						aggiornaInfoStampa(request, response, true);
					}
				}
				txExecutor.commitTransaction();
				getRequestContainer().getSessionContainer().setAttribute("trasferito", "true");
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore durante il trasferimento", e);

				try {
					txExecutor.rollBackTransaction();
					response.updAttribute("operationResult", "ERROR");
					getRequestContainer().getSessionContainer().setAttribute("trasferito", "false");
					getRequestContainer().getSessionContainer().setAttribute("SERVICE_REQUEST", copiaRequest);
					reportOperation.reportFailure(errorCode, e, "Trasferimento::service()", "");
				} catch (Exception rbe) {
					it.eng.sil.util.TraceWrapper.fatal(_logger, "Impossibile eseguire la rollBack", rbe);

				}
				return;
			}
		} // fine trasferimento
		try {
			// //////////////////////////
			// generazione del report: se si verifica un errore in questa fase
			// viene lanciata una eccezione
			if (soloStampa) {
				// il trasferimento e' gia' stato eseguito con successo
				// precedentemente
				getRequestContainer().getSessionContainer().setAttribute("trasferito", "true");
			}
			impostaDatiStampa(request, response);
			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");
			if (salva != null && salva.equalsIgnoreCase("true")) {
				SourceBean row = doSelect(request, response, "GET_INFO_STAMPA_TRASF");
				checkStampaPossibile(row, reportOperation);
				Object prg = row.getAttribute("row.prgLavStoriaInf");
				setStrChiavetabella(prg.toString());
				txExecutor = new TransactionQueryExecutor(getPool());
				txExecutor.initTransaction();
				insert(txExecutor);

				request.setAttribute("flgStampaTrasf", "S");
				aggiornaInfoStampa(request, response, false);
				txExecutor.commitTransaction();
				// GESTIONE FIRMA GRAFOMETRICA
				if (request.getAttribute("firmaGrafometrica") != null
						&& request.getAttribute("firmaGrafometrica").toString().equals("true")) {

					try {

						Documento actualDocument = getDocumento();
						Documento prgDocumento = new Documento(actualDocument.getPrgDocumento());
						actualDocument.setNumKloDocumento(prgDocumento.getNumKloDocumento());
						_logger.debug("[Trasferimento Lavoratore] --> firmaGrafometrica --> NumKlo: "
								+ actualDocument.getNumKloDocumento());

						// VERIFICA SE IL TIPO DI DOCUMENTO E' PREDISPOSTO PER ESSERE FIRMATO GRAFOMETRICAMENTE
						ProtocolloPi3DBManager dbManager = new ProtocolloPi3DBManager();
						// boolean isDocumentFirmabile =
						// dbManager.isAllegatoDocumentoFirmato(actualDocument.getPrgDocumento().toString());
						BigDecimal prgTemplateStampa = dbManager
								.getPrgTemplateStampa(Pi3Constants.PI3_DOCUMENT_TYPE_RICHIESTA_TRASFERIMENTO);
						boolean isDocumentTypeFirmabile = dbManager
								.isDocumentTypeGraphSignature(prgTemplateStampa.toString());

						if (isDocumentTypeFirmabile) {

							TransactionQueryExecutor txExecutorFirma = null;
							ArrayList messagesWarning = new ArrayList();
							try {
								FirmaDocumenti firma = new FirmaDocumenti();
								User user = (User) getRequestContainer().getSessionContainer()
										.getAttribute(User.USERID);
								txExecutorFirma = new TransactionQueryExecutor(getPool(), this);
								String ipOperatore = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(request,
										"ipOperatore");
								boolean esitoFirma = firma.firmaDocumento(request, response, user, txExecutorFirma,
										actualDocument, ipOperatore);
								if (!esitoFirma) {
									// response.updAttribute("messageResult", "Errore durante la firma grafometrica del
									// documento.");
									// throw new Exception("Errore durante la firma grafometrica del documento.");
									messagesWarning.add(MessageCodes.FirmaGrafometrica.WS_OPERATION_FAIL);
									throw new Exception();
								}
							} catch (Exception firmaEx) {
								if (!messagesWarning.contains(MessageCodes.FirmaGrafometrica.WS_OPERATION_FAIL)) {
									// response.updAttribute("messageWarning", "Il Servizio di Firma Grafometrica non è
									// al momento disponibile. Procedere con la firma autografa del documento");
									messagesWarning.add(MessageCodes.FirmaGrafometrica.WS_NON_RAGGIUNGIBILE);
								}
								setOperationSuccessWithWarning(request, response, messagesWarning);
								return;

							} finally {
								if (txExecutorFirma != null) {
									txExecutorFirma.closeConnTransaction();
									_logger.debug(
											"TRASFERIMENTO - Gestione Firma Grafometrica - TransactionQueryExecutor close...");
								}
							}

						} else {
							it.eng.sil.util.TraceWrapper.debug(_logger,
									"Richiesta di Trasferimento:service(): Tipo del Documento non firmabile grafometricamente, si procede alla trasformazione del documento normalmente senza applicazione dei campi firma",
									null);
						}

					} catch (Exception firmaEx) {
						it.eng.sil.util.TraceWrapper.error(_logger,
								"Richiesta di Trasferimento:service():errore recupero firmabilita' grafometrica del tipo di documento",
								firmaEx);
						setOperationFail(request, response, firmaEx);
					}

				}

				setOperationSuccess(request, response);

				boolean pippo = false;
				if (pippo)
					throw new Exception("Trasferimento.java interrotto per test");
				// txExecutor.commitTransaction();
			} else if (apri != null && apri.equalsIgnoreCase("true"))
				showDocument(request, response);
			// info per la jsp del trasferimento: il report e' stato generato e
			// protocollato con successo
			getRequestContainer().getSessionContainer().setAttribute("reportResult", "SUCCESS");
			// ////// se si vuole far fallire l' operazione basta porre a true
			// la variabile pippo
			if (poloInCoop) {
				getRequestContainer().getSessionContainer().setAttribute("richDocStampata", "true");
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
					reportOperation.reportFailure(MessageCodes.General.CONCORRENZA);
					try {
						response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.CONCORRENZA);
					} catch (SourceBeanException e) {
						_logger.error(e.getMessage());
					}
				} else {
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
					try {
						response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.OPERATION_FAIL);
					} catch (SourceBeanException e) {
						_logger.error(e.getMessage());
					}
				}
			} else {
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				try {
					response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.OPERATION_FAIL);
				} catch (SourceBeanException e) {
					_logger.error(e.getMessage());
				}
			}

		}

		catch (Exception e) {
			// si e' verificato un errore durante la generazione della stampa
			// (il modulo di cooperazione
			// cattura tutte le eccezioni)
			try {
				if (txExecutor != null) {
					// si stava protocollando il documento
					txExecutor.rollBackTransaction();
					// quando verra' ricaricata la pagina (tramite il tasto
					// indietro) verranno ricaricati
					// i dati della request ottenuta dal sessionContainer
					getRequestContainer().getSessionContainer().setAttribute("SERVICE_REQUEST", request);
					getRequestContainer().getSessionContainer().setAttribute("reportResult", "ERROR");
					if (poloInCoop) {
						getRequestContainer().getSessionContainer().setAttribute("richDocStampata", "ERROR");
					}
					setOperationFail(request, response, e);
					// ora bisogna registrare in an_lav_storia_inf che non e'
					// stato possibile stampare
					// e protocollare il report. Al caricamento della pagina
					// degli indirizzi se si trova il flag
					// a 'N' verra' viualizzato il pulsante di stampa. Anche se
					// cade il browser in un secondo momento
					// sara' possibile generare la stampa.
					// request.setAttribute("flgStampaTrasf", "N");
					// aggiornaInfoStampa(request, response);
					// si aggiorna il record di an_lav_storia_inf
				} else {
					setOperationFail(request, response, e);
				}
				// response.updAttribute("operationResult", "ERROR");
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						"errore nella stampa/protocollazione del documento di trasferimento/presa atto", e);

			} catch (Exception rbe) {
				try {
					setOperationFail(request, response, rbe);
				} catch (Exception e1) {
					it.eng.sil.util.TraceWrapper.fatal(_logger,
							"errore nella stampa/protocollazione del documento di trasferimento/presa atto - impossibile impostare errore nella response",
							e1);

				}
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						"errore nella stampa/protocollazione del documento di trasferimento/presa atto - rollBack fallita.",
						rbe);

			}
		}
	}

	/**
	 * @param request
	 * @param response
	 */
	private void checkStampaPossibile(SourceBean row, ReportOperationResult reportOperation) throws Exception {
		// vedo se la stampa non sia gia' stata eseguita
		//
		String flgStampaTrasf = (String) row.getAttribute("row.flgStampaTrasf");
		String flgStampaDoc = (String) row.getAttribute("row.flgStampaDoc");
		if (flgStampaTrasf != null && flgStampaTrasf.equals("N") && flgStampaDoc != null)
			;
		else {
			reportOperation.reportFailure(MessageCodes.Trasferimento.STAMPA_TRASF_ESEGUITA);
			throw new Exception("Stampa del trasferimento già fatta");
		}
	}

	/**
	 * Aggiornamento delle informazioni relative alla stampa del trasferimento del lavoratore. Se il trasferimento
	 * avviene e la stampa fallisce -> an_lav_storia_inf.flgStampaPatto = 'N' Se il trasferimento avviene e la stampa ha
	 * successo -> an_lav_storia_inf.flgStampaPatto = 'S'
	 * 
	 * @param request
	 * @param response
	 */
	private void aggiornaInfoStampa(SourceBean request, SourceBean response, boolean poloInCoop) throws Exception {
		int errCode = 0;
		// try {
		errCode = MessageCodes.General.GET_ROW_FAIL;
		SourceBean row = null;
		if (poloInCoop) {
			row = doSelectinTransaction(request, response, "GET_INFO_STAMPA_TRASF");
		} else {
			row = doSelect(request, response, "GET_INFO_STAMPA_TRASF");
		}
		errCode = MessageCodes.General.UPDATE_FAIL;
		BigDecimal numKlo = (BigDecimal) row.getAttribute("row.numklolavstoriainf");
		Object prgLavStoriaInf = row.getAttribute("row.prgLavStoriaInf");
		numKlo = numKlo.add(new BigDecimal(1));
		request.updAttribute("numKloLavStoriaInf", numKlo);
		request.updAttribute("prgLavStoriaInf", prgLavStoriaInf);
		Boolean ret = new Boolean(true);
		if (poloInCoop) {
			ret = (Boolean) doUpdate(request, response, "UPDATE_INFO_STAMPA_COOP");
		} else {
			ret = (Boolean) doUpdate(request, response, "UPDATE_INFO_STAMPA");
		}
		if (!ret.booleanValue())
			throw new Exception("impossibile aggiornare l'informazione di stampa del trasferimento di un lavoratore");
		// txExecutor.commitTransaction();
		/*
		 * } catch (Exception e) { try { txExecutor.rollBackTransaction(); } catch (Exception rbe) {
		 * it.eng.sil.util.TraceWrapper.fatal( _logger,"errore nel tentativo di eseguire la rollback per il fallimento
		 * dell'aggiornamento del'esito della stampa in an_lav_storia_inf", rbe); } it.eng.sil.util.TraceWrapper.fatal(
		 * _logger,"errore nella registrazione dell'esita della stampa in an_lav_storia_inf", e); // inserisco l'errore
		 * nella session: in questo modo quando la pagina verra' ricaricata verra' aggiunto // al container degli errori
		 * e visualizzato dal tag <af:error/> getRequestContainer().getSessionContainer().setAttribute(
		 * "errore_in_trasferimento", new EMFUserError(EMFErrorSeverity.ERROR, errCode)); }
		 */
	}

	public SourceBean doSelect(SourceBean request, SourceBean response, String queryName) throws Exception {
		String pool = getPool();
		SourceBean statement = getSelectStatement(queryName);

		SourceBean beanRows = null;
		beanRows = (SourceBean) com.engiweb.framework.util.QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");
		try {

			response.setAttribute("SELECT_OK", "TRUE");
		} catch (Exception ex) {
			response.setAttribute("SELECT_FAIL", "TRUE");
		}

		return beanRows;
	}

	public SourceBean doSelectinTransaction(SourceBean request, SourceBean response, String queryName)
			throws Exception {
		String pool = getPool();
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

	public Object doUpdate(SourceBean request, SourceBean response, String queryName) throws Exception {
		String pool = getPool();
		SourceBean statement = getSelectStatement(queryName);

		Boolean beanRows = null;
		beanRows = (Boolean) txExecutor.executeQuery(getRequestContainer(), getResponseContainer(), statement,
				"UPDATE");
		try {
			response.setAttribute("UPDATE_OK", "TRUE");
		} catch (Exception ex) {
			response.setAttribute("UPDATE_FAIL", "TRUE");
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

	protected String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

	/**
	 * 
	 */
	protected SourceBean getSelectStatement(String queryName) {
		SourceBean beanQuery = null;
		beanQuery = (SourceBean) getConfig().getAttribute(queryName);

		return beanQuery;
	}

	private boolean trasferisci(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int errCode = 0;
		// Segnalazione soli errori/problemi
		// ////disableMessageIdSuccess();

		boolean isPattoAperto = false;
		// Controllo se ho un patto da chiudere
		String prgPattoLav = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(request, "PRGPATTOLAVORATORE");
		if (!prgPattoLav.equals("") && !prgPattoLav.equalsIgnoreCase("null")) {
			isPattoAperto = true;
		}

		// Dopo ogni operazione raccoglie il risultato
		Boolean result = null;
		// setSectionQuerySelect("GET_DID_VALIDA");
		SourceBean did = doSelect(request, response, "GET_DID_VALIDA");
		// aggiorno domicilio lavoratore
		// setMessageIdFail(MessageCodes.Trasferimento.ERR_UPDATE_DOM_LAV);
		errCode = MessageCodes.Trasferimento.ERR_UPDATE_DOM_LAV;
		// setSectionQueryUpdate("QUERY_UPDATE_DOM_LAV");
		result = (Boolean) doUpdate(request, response, "QUERY_UPDATE_DOM_LAV");

		if (!result.booleanValue()) {
			// txExecutor.rollBackTransaction();
			reportOperation.reportFailure(errCode);
			return false;
		}

		// Esecuzione della query su AN_LAV_STORIA_INF (Il record precedente
		// viene chiuso con una stored procedure)
		// setMessageIdFail(MessageCodes.Trasferimento.ERR_CLOSE_AN_LAV_S);
		errCode = MessageCodes.Trasferimento.ERR_CLOSE_AN_LAV_S;
		// setSectionQueryInsert("QUERY_INSERT_AN_LAV_S");
		boolean isTrasferimentoInter = false;
		boolean isDisoccupato = false;

		isTrasferimentoInter = (request.getAttribute("isInterProvincia").equals("SI")
				|| request.getAttribute("isInterRegione").equals("SI"));

		isDisoccupato = (((String) request.getAttribute("codStatoOccupazRagg")).equalsIgnoreCase("D")
				|| ((String) request.getAttribute("codStatoOccupazRagg")).equalsIgnoreCase("I"));

		// if (isDisoccupato && isTrasferimentoInter) {
		// request.updAttribute("DATDICHIARAZIONE",(String)request.getAttribute("datAnzianitaDisoc"));
		// }

		result = (Boolean) doInsert(request, response, "QUERY_INSERT_AN_LAV_S");

		if (!result.booleanValue()) {
			// txExecutor.rollBackTransaction();
			reportOperation.reportFailure(errCode);
			return false;
		}

		/*
		 * if (request.containsAttribute("numKloElencoAnag") &&
		 * !((String)request.getAttribute("numKloElencoAnag")).equalsIgnoreCase("null")) { //Chiudo il record precedente
		 * su AM_ELENCO_ANAGRAFICO //setMessageIdFail(MessageCodes.Trasferimento.ERR_CLOSE_AM_EL_ANAG); errCode =
		 * MessageCodes.Trasferimento.ERR_CLOSE_AM_EL_ANAG; //setSectionQueryUpdate("QUERY_UPDATE_AM_EL_ANAG"); result =
		 * (Boolean)doUpdate(request, response, "QUERY_UPDATE_AM_EL_ANAG");
		 * 
		 * if (!result.booleanValue()) { txExecutor.rollBackTransaction(); //reportOperation.reportFailure(errCode);
		 * return false; } }
		 */

		// Apro un nuovo record su AM_ELENCO_ANAGRAFICO
		// setSectionQueryNextVal("GET_AM_ELENCO_ANAGRAFICO_NEXTVAL");
		SourceBean row = doSelect(request, response, "GET_AM_ELENCO_ANAGRAFICO_NEXTVAL");
		Object prgELencoAnag = row.getAttribute("row.DO_NEXTVAL");
		// setMessageIdFail(MessageCodes.Trasferimento.ERR_OPEN_AM_EL_ANAG);
		errCode = MessageCodes.Trasferimento.ERR_OPEN_AM_EL_ANAG;
		// setSectionQueryInsert("QUERY_INSERT_AM_EL_ANAG");
		request.updAttribute("PRGELENCOANAGRAFICO", prgELencoAnag);
		result = (Boolean) doInsert(request, response, "QUERY_INSERT_AM_EL_ANAG");
		//
		if (isPattoAperto) {
			if (!result.booleanValue()) {
				reportOperation.reportFailure(errCode);
				// txExecutor.rollBackTransaction();
				return false;
			}

			// Chiudo il record precedente su AM_PATTO_LAVORATORE
			// setMessageIdFail(it.eng.afExt.utils.MessageCodes.Trasferimento.ERR_CLOSE_AM_PATTO_LAV);
			errCode = MessageCodes.Trasferimento.ERR_CLOSE_AM_PATTO_LAV;
			// setSectionQueryUpdate("QUERY_UPDATE_AM_PATTO_LAV");
			result = (Boolean) doUpdate(request, response, "QUERY_UPDATE_AM_PATTO_LAV");
			if (!result.booleanValue()) {
				reportOperation.reportFailure(errCode);
				return false;
			}
		}

		if (isTrasferimentoInter) {
			Object res = null;
			String datAnzianitaDisoc = (String) request.getAttribute("datAnzianitaDisoc");
			String datNuovoStato = (String) request.getAttribute("datTrasferimento");
			if (request.containsAttribute("datInizio")) {
				request.updAttribute("datInizio", datNuovoStato);
			} else {
				request.setAttribute("datInizio", datNuovoStato);
			}

			if (request.containsAttribute("datScadConferma")) {
				request.updAttribute("datScadConferma", UtilityNumGGTraDate.sommaGiorni(datNuovoStato, 60));
			} else {
				request.setAttribute("datScadConferma", UtilityNumGGTraDate.sommaGiorni(datNuovoStato, 60));
			}

			if (request.containsAttribute("datScadErogazServizi")) {
				request.updAttribute("datScadErogazServizi", UtilityNumGGTraDate.sommaGiorni(datNuovoStato, 90));
			} else {
				request.setAttribute("datScadErogazServizi", UtilityNumGGTraDate.sommaGiorni(datNuovoStato, 90));
			}

			if (request.containsAttribute("CODMONOPROVENIENZA")) {
				request.updAttribute("CODMONOPROVENIENZA", "T");
			} else {
				request.setAttribute("CODMONOPROVENIENZA", "T");
			}

			if (datAnzianitaDisoc != null && !datAnzianitaDisoc.equals("")) {
				if (((String) request.getAttribute("DATCALCOLOANZIANITA")).equals("")) {
					request.updAttribute("DATCALCOLOANZIANITA",
							UtilityNumGGTraDate.sottraiGiorni(datAnzianitaDisoc, 1));
				}

				if (((String) request.getAttribute("DATCALCOLOMESISOSP")).equals("")) {
					request.updAttribute("DATCALCOLOMESISOSP", UtilityNumGGTraDate.sottraiGiorni(datAnzianitaDisoc, 1));
				}
			}

			if (((String) request.getAttribute("NUMANZIANITAPREC297")).equals("")) {
				request.updAttribute("NUMANZIANITAPREC297", "0");
			}

			if (((String) request.getAttribute("NUMMESISOSP")).equals("")) {
				request.updAttribute("NUMMESISOSP", "0");
			}

			/*
			 * 4/7/2005 - Correzione anomalia che permetteva di avere due stati occ. alla stessa data
			 * 
			 */

			Vector vectSo = DBLoad.getStatiOccupazionali(request.getAttribute("cdnLavoratore"), txExecutor);
			StatoOccupazionaleBean so = vectSo.size() > 0
					? new StatoOccupazionaleBean((SourceBean) vectSo.get(vectSo.size() - 1))
					: null;
			if (so != null && (DateUtils.compare(so.getDataInizio(), datNuovoStato) == 0)) {
				BigDecimal numklostatooccupaz = so.getNumKlo().add(new BigDecimal(1));
				BigDecimal prgStatoOcc = so.getPrgStatoOccupaz();
				request.setAttribute("numklostatooccupaz", numklostatooccupaz);
				request.setAttribute("prgStatoOccupaz", prgStatoOcc);
				res = doUpdate(request, response, "QUERY_UPDATE_STATO_OCC");
			} else {
				SourceBean rowNextVal = doSelect(request, response, "GET_NEXT_AM_STATO_OCC");
				if (rowNextVal.containsAttribute("ROW.NEXTVAL")) {
					Object prgStatoOcc = rowNextVal.getAttribute("ROW.NEXTVAL");
					request.setAttribute("prgStatoOccupaz", prgStatoOcc);
				} else
					throw new Exception("Impossibile leggere il progressivo per la did");

				res = doInsert(request, response, "QUERY_INSERT_STATO_OCC");
			}
			/*
			 * 4/7/2005 - Fine correzione anomalia
			 * 
			 */
			boolean ret = ((Boolean) res).booleanValue();
			if (!ret) {
				errorCode = MessageCodes.Trasferimento.ERR_OPEN_STATO_OCC;
				throw new Exception("impossibile inserire il record nella tabella AM_STATO_OCCUPAZ");
			}

			ExceptionUtils.controlloDateRecordPrecedente(res,
					MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
			reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);

		}

		if (did != null && did.containsAttribute("row")) {
			// setSectionQueryUpdate("UPDATE_DICH_DISP");
			// setMessageIdFail(MessageCodes.Trasferimento.ERR_UPD_AM_DICH_DISP);
			errCode = MessageCodes.Trasferimento.ERR_UPD_AM_DICH_DISP;
			request.updAttribute("prgdichdisponibilita", did.getAttribute("row.prgdichdisponibilita"));
			BigDecimal numKlo = (BigDecimal) did.getAttribute("row.numklodichdisp");
			request.updAttribute("numklodichdisp", numKlo.add(new BigDecimal(1)));
			result = (Boolean) doUpdate(request, response, "UPDATE_DICH_DISP");
			if (!result.booleanValue()) {
				reportOperation.reportFailure(errCode);
				// txExecutor.rollBackTransaction();
				return false;
			}
		} else {
			if (isTrasferimentoInter && isDisoccupato)
				inserisciDid(request, response, txExecutor);
		}
		reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		return true;
	}

	private void inserisciDid(SourceBean request, SourceBean response, TransactionQueryExecutor txExecutor)
			throws Exception {
		boolean ret = false;
		Object res = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		// TransactionQueryExecutor transExec = null;
		// int idSuccess = this.disableMessageIdSuccess();
		// int idFail = this.disableMessageIdFail();
		boolean canUpdate = false;

		if (!request.containsAttribute("datAnzianitaDisoc")
				|| ((String) request.getAttribute("datAnzianitaDisoc")).equals("")) {
			// non inserisco la DID
		} else {

			String strCodUltimoContratto = null;
			SourceBean rowUltimoMovimento = doSelect(request, response, "QUERY_GET_ULTIMO_MOV");
			// rowUltimoMovimento =
			// (SourceBean)rowUltimoMovimento.getAttribute("ROW");
			if (rowUltimoMovimento != null && rowUltimoMovimento.containsAttribute("ROW")) {
				strCodUltimoContratto = (String) rowUltimoMovimento.getAttribute("ROW.CODCONTRATTO");
				request.setAttribute("CodUltimoContratto", strCodUltimoContratto);
			}

			// boolean codControllo = DIDManager.inserimentoDID(request,
			// getRequestContainer(), response, txExecutor);

			// if (!codControllo) {
			// throw new
			// ControlliException(MessageCodes.DID.CONTROLLI_INSERIMENTO_NON_SUPERATI);
			// }

			// this.setSectionQuerySelect("QUERY_NEXTVAL");
			// SourceBean row = doSelect(request, response, false);
			SourceBean row = doSelect(request, response, "QUERY_NEXTVAL");
			if (row.containsAttribute("ROW.DO_NEXTVAL")) {
				Object o = row.getAttribute("ROW.DO_NEXTVAL");
				request.updAttribute("PRGDICHDISPONIBILITA", o);
			} else
				throw new Exception("Impossibile leggere il progressivo per la did");

			// setSectionQueryInsert("QUERY_INSERT");
			// old call: res = this.doInsert(request, response, true);
			/*
			 * 15/10/2004 In inserimento DID (non protocollata) lo stato occupazionale attribuito sarà null in modo da
			 * risolvere un problema di dereferenzazione in fase di ricostruzione storia per gli impatti amministrativi.
			 */
			// if (request.containsAttribute("PRGSTATOOCCUPAZ"))
			// request.delAttribute("PRGSTATOOCCUPAZ");
			request.setAttribute("CODSTATOATTO", "PR");
			request.setAttribute("CODTIPODICHDISP", "ID");
			// res = this.doInsert(request, response, true);
			res = doInsert(request, response, "QUERY_INSERT_DISPO");
			ExceptionUtils.controlloDateRecordPrecedente(res,
					MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
			ret = ((Boolean) res).booleanValue();

			if (!ret) {
				errorCode = MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO;
				throw new Exception("impossibile inserire il record nella tabella am_dich_sisponibilita");
			}
			// txExecutor.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
			scriviDocumentoDid(request, response, txExecutor);

		}
	}

	private void scriviDocumentoDid(SourceBean request, SourceBean response, TransactionQueryExecutor txExecutor)
			throws Exception {
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		BigDecimal userid = new BigDecimal(user.getCodut());

		doc.setCdnLavoratore(new BigDecimal((String) (request.getAttribute("cdnLavoratore"))));
		doc.setPagina("DispoDettaglioPage");
		doc.setChiaveTabella(((BigDecimal) request.getAttribute("PRGDICHDISPONIBILITA")).toString());
		doc.setCdnUtMod(new BigDecimal(user.getCodut()));
		doc.setCdnUtIns(new BigDecimal(user.getCodut()));
		doc.setStrEnteRilascio((String) request.getAttribute("CODCPIORIG")); // CPI
																				// di
																				// provenienza
		doc.setCodCpi(user.getCodRif());
		doc.setCodMonoIO("I");
		doc.setCodTipoDocumento("IM");
		doc.setCodModalitaAcqril(null);
		doc.setPrgAzienda(null);
		doc.setPrgUnita(null);
		doc.setCodTipoFile(null);
		doc.setDatFine(null);
		doc.setDtmIns(DateUtils.getNow());
		doc.setDtmMod(DateUtils.getNow());
		doc.setNumAnnoProt(null);
		doc.setFlgAutocertificazione("N");
		doc.setStrDescrizione("Dichiarazione immediata disponibilità");
		doc.setFlgDocAmm("S");
		doc.setFlgDocIdentifP("N");
		doc.setDatInizio((String) request.getAttribute("DATDICHIARAZIONE"));
		doc.setDatAcqril(DateUtils.getNow());
		doc.setDatProtocollazione(DateUtils.getNow());
		doc.insert(txExecutor);
	}

	private void impostaDatiStampa(SourceBean request, SourceBean response) {

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			try {
				setStrDescrizione("Trasferimento lavoratore");

				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("TrasferimentoLavoratore." + tipoFile);
				else
					setStrNomeDoc("TrasferimentoLavoratore.pdf");

				/*
				 * Per la regione Valle d'Aosta il report è personalizzato nell'intestazione Si è deciso di suddividere
				 * le personalizzazioni per regioni in diversi file di report ATTENZIONE: in caso di bugfix apportare la
				 * modifica in tutti i file di report.
				 */
				AccessoSemplificato _db = new AccessoSemplificato(this);
				SourceBean beanRows = null;
				_db.setSectionQuerySelect("GET_CODREGIONE");
				beanRows = _db.doSelect(request, response, false);

				String regione = (String) beanRows.getAttribute("ROW.CODREGIONE");

				// nel caso della VALLE D'AOSTA (codregione=2) è stata creata una nuova stampa
				if (regione.equals("2"))
					setReportPath("Anagrafica/Trasferimento_VDA_CC.rpt");
				else
					setReportPath("Anagrafica/Trasferimento_CC.rpt");

				// impostare il codice del tipo di
				// documento**********************************
				// *******************************************************
				// setCodTipoDocumento("");

				// impostazione parametri del report
				Map prompts = new HashMap();
				prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));

				// solo se e' richiesta la protocollazione i parametri vengono
				// inseriti nella Map
				addPromptFieldsProtocollazione(prompts, request);
				// ora si chiede di usare il passaggio dei parametri per nome e
				// non per posizione (col vettore, passaggio di default)
				setPromptFields(prompts);

				String tipoDoc = (String) request.getAttribute("tipoDoc");
				if (tipoDoc != null)
					setCodTipoDocumento(tipoDoc);
			} catch (EMFUserError ue) {
				setOperationFail(request, response, ue);
				reportFailure(ue, "^^^.service()", "");
			}
		}
	}
}
