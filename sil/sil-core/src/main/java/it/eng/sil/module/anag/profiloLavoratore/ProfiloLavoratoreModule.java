package it.eng.sil.module.anag.profiloLavoratore;

import java.math.BigDecimal;
import java.util.HashMap;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.anag.profiloLavoratore.bean.ProfiloLavoratore;
import it.eng.sil.module.voucher.Properties;
import it.eng.sil.security.User;

public class ProfiloLavoratoreModule extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1740625232970197147L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProfiloLavoratoreModule.class.getName());

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		TransactionQueryExecutor tqe = null;
		DataConnection conn = null;
		try {
			String controlloCalcolo = StringUtils.getAttributeStrNotNull(serviceRequest, "TIPO_OPERAZIONE");
			String prgProfiloLavStr = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGLAVORATOREPROFILO");
			String linguettaStr = StringUtils.getAttributeStrNotNull(serviceRequest, "numlinguetta");
			String cdnLavoratoreStr = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
			BigDecimal cdnLavoratore = new BigDecimal(cdnLavoratoreStr);
			SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);
			BigDecimal userid = new BigDecimal(user.getCodut());
			String codCpiRif = user.getCodRif();
			boolean esito = false;
			ProfiloLavoratore profLav = null;
			if (StringUtils.isEmptyNoBlank(prgProfiloLavStr)) {
				// caso nuovo
				profLav = new ProfiloLavoratore(tqe, cdnLavoratore, codCpiRif, userid);
			} else {
				// caso update
				BigDecimal prgProfiloLav = new BigDecimal(prgProfiloLavStr);
				profLav = new ProfiloLavoratore(tqe, prgProfiloLav, cdnLavoratore, codCpiRif, userid);
			}
			Integer controlloDomande = profLav.controlloCoerenzaRisposte(serviceRequest);
			if (controlloDomande.intValue() != 0) {
				if (StringUtils.isFilledNoBlank(prgProfiloLavStr)) {
					serviceResponse.setAttribute("PRGLAVORATOREPROFILO", profLav.getPrgLavoratoreProfilo());
				}
				switch (Math.abs(controlloDomande.intValue())) {
				case Properties.ERRORE_21:
					reportOperation.reportFailure(MessageCodes.ProfiloLavoratore.ERRORE_21);
					break;
				case Properties.ERRORE_45_INV:
					reportOperation.reportFailure(MessageCodes.ProfiloLavoratore.ERRORE_45_INV);
					break;
				default:
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				}
			} else {

				tqe = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				tqe.initTransaction();

				// caso CALCOLA PROFILO
				if (StringUtils.isFilledNoBlank(controlloCalcolo)) {
					if (StringUtils.isEmptyNoBlank(prgProfiloLavStr)) {
						reportOperation.reportFailure(MessageCodes.ProfiloLavoratore.CNE_PRGPROFILO_NOT_EXISTS);
					} else {
						// PROFILO ESISTENTE
						serviceResponse.setAttribute("PRGLAVORATOREPROFILO", profLav.getPrgLavoratoreProfilo());
						conn = tqe.getDataConnection();

						if (controlloCalcolo.equalsIgnoreCase(Decodifica.CALCOLA_PERSONALITA)) {
							HashMap<String, String> esitoPersonalita = profLav.calcolaScorePersonalita(conn);
							if (esitoPersonalita != null) {
								if (esitoPersonalita.containsKey("OK_SCORE_PERS")) {
									serviceResponse.setAttribute("OK_SCORE_PERS", "OK_SCORE_PERS");
									serviceResponse.setAttribute(Decodifica.ScorePersonalita.AMICALITA,
											(esitoPersonalita.get(Decodifica.ScorePersonalita.AMICALITA) == null) ? ""
													: esitoPersonalita.get(Decodifica.ScorePersonalita.AMICALITA));
									serviceResponse.setAttribute(Decodifica.ScorePersonalita.COSCIENZOSITA,
											(esitoPersonalita.get(Decodifica.ScorePersonalita.COSCIENZOSITA) == null)
													? ""
													: esitoPersonalita.get(Decodifica.ScorePersonalita.COSCIENZOSITA));
									serviceResponse.setAttribute(Decodifica.ScorePersonalita.STAB_EMOTIVA,
											(esitoPersonalita.get(Decodifica.ScorePersonalita.STAB_EMOTIVA) == null)
													? ""
													: esitoPersonalita.get(Decodifica.ScorePersonalita.STAB_EMOTIVA));
									serviceResponse.setAttribute(Decodifica.ScorePersonalita.EXTRAVERSIONE,
											(esitoPersonalita.get(Decodifica.ScorePersonalita.EXTRAVERSIONE) == null)
													? ""
													: esitoPersonalita.get(Decodifica.ScorePersonalita.EXTRAVERSIONE));
									serviceResponse.setAttribute(Decodifica.ScorePersonalita.APERTURA,
											(esitoPersonalita.get(Decodifica.ScorePersonalita.APERTURA) == null) ? ""
													: esitoPersonalita.get(Decodifica.ScorePersonalita.APERTURA));
									if (StringUtils.isFilledNoBlank(
											esitoPersonalita.get(Decodifica.ScorePersonalita.COMPLETEZZA_PROFILO))) {
										if (esitoPersonalita.get(Decodifica.ScorePersonalita.COMPLETEZZA_PROFILO)
												.equals(Decodifica.ScorePersonalita.ESITO_COMPLETO)) {
											reportOperation.reportSuccess(
													MessageCodes.ProfiloLavoratore.SCORE_PERSONALITA_SUCCESSO);
										} else if (esitoPersonalita.get(Decodifica.ScorePersonalita.COMPLETEZZA_PROFILO)
												.equals(Decodifica.ScorePersonalita.ESITO_INCOMPLETO)) {
											reportOperation
													.reportSuccess(MessageCodes.ProfiloLavoratore.PERS_INCOMPLETA);
										}
									} else {
										reportOperation.reportSuccess(
												MessageCodes.ProfiloLavoratore.SCORE_PERSONALITA_SUCCESSO);
									}
								}
							} else {
								reportOperation.reportFailure(MessageCodes.ProfiloLavoratore.ERRORE_CALCOLO);
							}
							tqe.commitTransaction();
						} else {

							Integer esitoCalcolabilita = profLav.calcolabilitaProfilo(conn);

							if (esitoCalcolabilita.intValue() == 0) {
								boolean isUpdate = controlloCalcolo.equalsIgnoreCase(Decodifica.CALCOLA_SALVA_PROFILO)
										? true
										: false;
								HashMap<String, String> esitoCalcolo = profLav.calcolaValoreProfilo(conn, isUpdate);
								if (esitoCalcolo != null) {
									if (esitoCalcolo.containsKey("CALCOLO_OK")) {
										serviceResponse.setAttribute("CALCOLO_OK", "CALCOLO_OK");
										serviceResponse.setAttribute("Numdim01",
												(esitoCalcolo.get("Numdim01") == null) ? ""
														: esitoCalcolo.get("Numdim01"));
										serviceResponse.setAttribute("Numdim02",
												(esitoCalcolo.get("Numdim02") == null) ? ""
														: esitoCalcolo.get("Numdim02"));
										serviceResponse.setAttribute("Numdim03",
												(esitoCalcolo.get("Numdim03") == null) ? ""
														: esitoCalcolo.get("Numdim03"));
										serviceResponse.setAttribute("Numdim04",
												(esitoCalcolo.get("Numdim04") == null) ? ""
														: esitoCalcolo.get("Numdim04"));
										serviceResponse.setAttribute("Numdim05",
												(esitoCalcolo.get("Numdim05") == null) ? ""
														: esitoCalcolo.get("Numdim05"));
										serviceResponse.setAttribute("Numdim06",
												(esitoCalcolo.get("Numdim06") == null) ? ""
														: esitoCalcolo.get("Numdim06"));
										serviceResponse.setAttribute("Numdim07",
												(esitoCalcolo.get("Numdim07") == null) ? ""
														: esitoCalcolo.get("Numdim07"));
										serviceResponse.setAttribute("indiceProfilo",
												(esitoCalcolo.get("indiceProfilo") == null) ? ""
														: esitoCalcolo.get("indiceProfilo"));
										serviceResponse.setAttribute("VALORE_CALCOLO",
												(esitoCalcolo.get("NUMVALOREPROFILO") == null) ? ""
														: esitoCalcolo.get("NUMVALOREPROFILO"));

										reportOperation.reportSuccess(
												MessageCodes.ProfiloLavoratore.CALCOLO_ESEGUITO_SUCCESSO);
										tqe.commitTransaction();
										if (isUpdate) {
											reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
										}
									} else if (esitoCalcolo.containsKey("CALCOLO_KO")) {
										reportOperation.reportFailure(MessageCodes.ProfiloLavoratore.ERRORE_CALCOLO);
										tqe.rollBackTransaction();
									}
								}

								// }
							} else {
								switch (Math.abs(esitoCalcolabilita.intValue())) {
								case Properties.ERRORE_CALCOLO_ESISTENTE:
									reportOperation
											.reportFailure(MessageCodes.ProfiloLavoratore.ERRORE_CALCOLO_ESISTENTE);
									break;
								case Properties.ERRORE_RISPOSTE_INSUFFICIENTI:
									reportOperation.reportFailure(
											MessageCodes.ProfiloLavoratore.ERRORE_RISPOSTE_INSUFFICIENTI);
									break;
								case Properties.ERRORE_21:
									reportOperation.reportFailure(MessageCodes.ProfiloLavoratore.ERRORE_21);
									break;
								case Properties.ERRORE_44:
									reportOperation.reportFailure(MessageCodes.ProfiloLavoratore.ERRORE_44);
									break;
								case Properties.ERRORE_45:
									reportOperation.reportFailure(MessageCodes.ProfiloLavoratore.ERRORE_45);
									break;
								case Properties.ERRORE_17_18:
									reportOperation.reportFailure(MessageCodes.ProfiloLavoratore.ERRORE_17_18);
									break;
								default:
									reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
								}
								tqe.commitTransaction();
							}
						}

					}
				} else {
					// caso SALVA/AGGIORNA PROFILO
					int linguetta = (new Integer(linguettaStr)).intValue();
					esito = profLav.salvaProfilo(serviceRequest, linguetta);
					if (esito) {
						serviceResponse.setAttribute("PRGLAVORATOREPROFILO", profLav.getPrgLavoratoreProfilo());
						if (StringUtils.isEmptyNoBlank(prgProfiloLavStr)) {
							reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
						} else {
							reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
						}
						tqe.commitTransaction();
					} else {
						if (StringUtils.isEmptyNoBlank(prgProfiloLavStr)) {
							reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
						} else {
							serviceResponse.setAttribute("PRGLAVORATOREPROFILO", profLav.getPrgLavoratoreProfilo());
							reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
						}
						tqe.rollBackTransaction();
					}
				}

			}
		} catch (Exception ex) {
			tqe.rollBackTransaction();
			it.eng.sil.util.TraceWrapper.debug(_logger, "ProfiloLavoratoreModule::service()", ex);
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
		}

	}

}
