/*
 * Creato il 13-ott-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.presel;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author olivieri
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ValidCurCambiaStatoOccupaz extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ValidCurCambiaStatoOccupaz.class.getName());
	private TransactionQueryExecutor transExec = null;

	public void service(SourceBean request, SourceBean response) throws Exception {

		boolean ret = false;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		String strTipoValidita = StringUtils.getAttributeStrNotNull(request, "CODTIPOVALIDITA");
		String cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "cdnLavoratore");
		boolean flgCambiaStatoOccupaz = strTipoValidita.equalsIgnoreCase("DL");

		String strDataInizioCurr = StringUtils.getAttributeStrNotNull(request, "DATINIZIOCURR");
		String strDataFineCurr = StringUtils.getAttributeStrNotNull(request, "DATFINECURR");
		if (DateUtils.compare(DateUtils.getNow(), strDataInizioCurr) < 0
				|| DateUtils.compare(DateUtils.getNow(), strDataFineCurr) > 0) {
			flgCambiaStatoOccupaz = false;
		}

		if (request.containsAttribute("NO_CAMBIO_STATO_OCCUPAZ")) {
			flgCambiaStatoOccupaz = false;
		}
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);
			transExec.initTransaction();

			if (flgCambiaStatoOccupaz) {
				this.setSectionQuerySelect("GET_STATO_OCC");
				SourceBean beanSelect = doSelect(request, response, false);
				Vector vect = beanSelect.getAttributeAsVector("ROW");
				int size = vect.size();
				boolean isElemEsistente = (beanSelect != null) && (vect.size() > 0);
				// cambio stato occupazionale
				if (isElemEsistente) {
					String datInizio = StringUtils.getAttributeStrNotNull(beanSelect, "ROW.DATINIZIO");
					String sysdate = DateUtils.getNow();
					BigDecimal numKlo = (BigDecimal) beanSelect.getAttribute("ROW.NUMKLOSTATOOCCUPAZ");
					numKlo = numKlo.add(new BigDecimal("1"));
					BigDecimal prgStatoOcc = (BigDecimal) beanSelect.getAttribute("ROW.PRGSTATOOCCUPAZ");

					request.setAttribute("codStatoOcc", "A1");
					request.setAttribute("DATINIZIO", DateUtils.getNow());
					request.setAttribute("CODMONOPROVENIENZA", "A");
					request.setAttribute("NUMKLOSTATOOCCUPAZ", numKlo);
					request.setAttribute("PRGSTATOOCCUPAZ", prgStatoOcc);

					if (DateUtils.compare(datInizio, sysdate) == 0) {
						this.setSectionQuerySelect("GET_STATO_OCC_PREC");
						beanSelect = doSelect(request, response, false);
						if (beanSelect != null) {
							String codStatoOccupaz = StringUtils.getAttributeStrNotNull(beanSelect,
									"ROW.CODSTATOOCCUPAZ");
							if (!codStatoOccupaz.equals("") && codStatoOccupaz.equals("A1")) {
								this.setSectionQueryDelete("DELETE_STATO_OCC_DA_CURR");
								if (!doDelete(request, response)) {
									_logger.debug(this.getClass().getName()
											+ "::service: fallita cancellazione stato occupazionale");

									transExec.rollBackTransaction();
									settaResponse(request, response);
									reportOperation.reportFailure(
											MessageCodes.StatoOccupazionale.FALLITO_CAMBIO_STATO_OCCUPAZIONALE);
									return;
								}
								numKlo = (BigDecimal) beanSelect.getAttribute("ROW.NUMKLOSTATOOCCUPAZ");
								numKlo = numKlo.add(new BigDecimal("1"));
								request.updAttribute("NUMKLOSTATOOCCUPAZ", numKlo);
								request.setAttribute("DATFINE", "");
								request.updAttribute("PRGSTATOOCCUPAZ",
										(BigDecimal) beanSelect.getAttribute("ROW.PRGSTATOOCCUPAZ"));
							}
						}
						this.setSectionQueryUpdate("UPDATE_STATO_OCC_DA_CURR");
						if (!doUpdate(request, response)) {
							_logger.debug(
									this.getClass().getName() + "::service: fallito aggiornamento stato occupazionale");

							transExec.rollBackTransaction();
							settaResponse(request, response);
							reportOperation
									.reportFailure(MessageCodes.StatoOccupazionale.FALLITO_CAMBIO_STATO_OCCUPAZIONALE);
							return;
						}
					} else {
						this.setSectionQueryInsert("INSERT_NUOVO_STATO_OCC");
						if (!doInsert(request, response)) {
							_logger.debug(
									this.getClass().getName() + "::service: fallito inserimento stato occupazionale");

							transExec.rollBackTransaction();
							settaResponse(request, response);
							reportOperation
									.reportFailure(MessageCodes.StatoOccupazionale.FALLITO_CAMBIO_STATO_OCCUPAZIONALE);
							return;
						}
					}
					response.setAttribute("FLGSTATOOCCUPAZMODIFICATO", "TRUE");
				}
			}

			if (request.containsAttribute("inserisci")) { // gestione
															// inserimento
															// validità
															// curriculum

				// -----------------------------------------------------------------------------------------------------------------
				BigDecimal PRGVALIDITA = doNextVal(request, response);

				if (PRGVALIDITA == null) {
					throw new Exception("Impossibile leggere S_PR_VALIDITA.NEXTVAL");
				}

				request.delAttribute("PRGVALIDITA");
				request.setAttribute("PRGVALIDITA", PRGVALIDITA.toString());
				// -----------------------------------------------------------------------------------------------------------------

				this.setSectionQuerySelect("GET_VALIDITA_CURRICULUM");
				SourceBean beanSelect = doSelect(request, response, false);
				Vector vect = beanSelect.getAttributeAsVector("ROW");
				boolean isElemEsistente = (beanSelect != null) && (vect.size() > 0);

				if (isElemEsistente) {
					_logger.fatal(this.getClass().getName() + "::service: fallito inserimento validità curriculum ");

					transExec.rollBackTransaction();
					reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
					reportOperation.reportFailure(MessageCodes.General.ELEMENT_DUPLICATED);
					response.setAttribute("ERR_INSERT_CURR", "TRUE");
				} else {

					// -----------------------------------------------------------------------------------------------------------------
					request.delAttribute("PRGVALIDITA");
					request.setAttribute("PRGVALIDITA", PRGVALIDITA.toString());
					// -----------------------------------------------------------------------------------------------------------------

					this.setSectionQueryInsert("INSERT_VAL_CURRICULUM");
					ret = doInsert(request, response);
					if (ret) {
						transExec.commitTransaction();
						reportOperation.reportSuccess(idSuccess);
					} else {
						_logger.fatal(
								this.getClass().getName() + "::service: fallito inserimento validità curriculum ");

						transExec.rollBackTransaction();
					}
				}
			} else { // gestione aggiornamento validità curriculum
				if (request.containsAttribute("SALVA")) {
					this.setSectionQueryUpdate("UPDATE_VAL_CURRICULUM");
					ret = doUpdate(request, response);
					if (ret) {
						transExec.commitTransaction();
						reportOperation.reportSuccess(idSuccess);
					} else {
						_logger.fatal(
								this.getClass().getName() + "::service: fallito aggiornamento validità curriculum ");

						transExec.rollBackTransaction();
					}
				}
			}
		}

		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					this.getClass().getName() + "::service: fallita gestione validità curriculum ", ex);

			transExec.rollBackTransaction();
		}
	}

	protected void settaResponse(SourceBean request, SourceBean response) throws Exception {
		response.setAttribute("ERR_STATO_OCCUPAZ", "TRUE");
		response.setAttribute("CODTIPOVALIDITA", request.getAttribute("CODTIPOVALIDITA"));
		response.setAttribute("CODSTATOLAV", request.getAttribute("CODSTATOLAV"));
		response.setAttribute("DATINIZIOCURR", request.getAttribute("DATINIZIOCURR"));
		response.setAttribute("DATFINECURR", request.getAttribute("DATFINECURR"));
	}
}