package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.QueryStrategyException;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.patto.bean.PacchettoAdulti;
import it.eng.sil.util.ExceptionUtils;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

public class InsertPattoLav extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertPattoLav.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		Object res = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		boolean inserisci = true;
		try {
			disableMessageIdSuccess();
			disableMessageIdFail();
			enableSimpleQuery(true);

			String flgPatto297 = request.containsAttribute("flgPatto297")
					? request.getAttribute("flgPatto297").toString()
					: "";
			String dataNascitaLav = "";
			String dataAdesionePA = "";
			boolean iscrDisabile = false;
			String dataInizioValINAT = "";
			String dataInizioValMGGU = "";
			String dataDichiarazioneDid = "";
			String mesiAnzianitaDidSup24 = "";
			String oggi = DateUtils.getNow();
			boolean isOkISEE = false;
			BigDecimal numValoreIsee = null;
			Object cdnLavoratore = request.getAttribute("cdnLavoratore");
			boolean isOkOver30 = false;
			boolean isOkOver45 = false;
			boolean isOkIncAtt = false;
			boolean isAnzianitaOver24Mesi = false;
			String dataInizioNaspi = "";
			String decImportoAr = "";
			String strNoteAttivazione = "";

			String dataStipula = request.containsAttribute("datStipula") ? request.getAttribute("datStipula").toString()
					: "";
			String datScadConferma = request.containsAttribute("datScadConferma")
					? request.getAttribute("datScadConferma").toString()
					: null;

			Vector<String> programmi = PattoBean.checkProgrammiInserimentoPatto(
					new BigDecimal(cdnLavoratore.toString()), dataStipula, datScadConferma, null);

			if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_ASSEGNO_RICOLLOCAZIONE)) {
				dataInizioNaspi = request.getAttribute("datInizioNaspi").toString();
				decImportoAr = request.getAttribute("decAssegnoNaspi").toString();
				strNoteAttivazione = request.getAttribute("noteElemAttivazione").toString();
			} else {
				dataInizioNaspi = null;
				decImportoAr = null;
				strNoteAttivazione = null;
				if (request.containsAttribute("datInizioNaspi")) {
					request.delAttribute("datInizioNaspi");
				}
				if (request.containsAttribute("decAssegnoNaspi")) {
					request.delAttribute("decAssegnoNaspi");
				}
				if (request.containsAttribute("noteElemAttivazione")) {
					request.delAttribute("noteElemAttivazione");
				}
			}

			if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)) {
				SourceBean rowGenerale = DBLoad.getInfoGenerali();
				if (rowGenerale != null && rowGenerale.containsAttribute("DATSTIPULAMGGU")) {
					dataInizioValMGGU = rowGenerale.getAttribute("DATSTIPULAMGGU").toString();
					if (dataInizioValMGGU == null || dataInizioValMGGU.equals("")
							|| DateUtils.compare(dataInizioValMGGU, oggi) > 0) {
						inserisci = false;
						reportOperation.reportFailure(MessageCodes.Patto.ERR_MISURA_MGGU_NON_VALIDA);
					}
				}
			}

			if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_INTERVENTO_OCCUPAZIONE)) {
				setSectionQuerySelect("GET_STATO_OCC");
				SourceBean sbSO = doSelect(request, response, false);
				if (sbSO.getAttribute("row.prgstatoOccupaz") != null) {
					// BigDecimal mesiAnz = (BigDecimal) sbSO.getAttribute("row.MESI_ANZ");
					// if (mesiAnz.compareTo(new BigDecimal(4)) < 0) {
					String statoOccRagg = sbSO.getAttribute("row.codstatooccupazragg").toString();
					boolean isDisoccupato = statoOccRagg.equalsIgnoreCase("D") || statoOccRagg.equalsIgnoreCase("I");
					if (!isDisoccupato) {
						inserisci = false;
						reportOperation.reportFailure(MessageCodes.Patto.ERR_ANZIANITA_POC);
					}
				}
			}

			if (inserisci) {
				if (flgPatto297.equals("N")) {
					if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_30)
							|| PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_45)
							|| PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_INCLUSIONE_ATTIVA)) {
						inserisci = false;
						reportOperation.reportFailure(MessageCodes.Patto.ERR_ACCORDO_GENERICO_MISURA_CONCORDATA);
					} else {
						// Se sto inserendo un accordo generico elimino i campi relativi alla gestione dei voucher
						if (request.containsAttribute("CODVCHPROFILING")) {
							request.delAttribute("CODVCHPROFILING");
						}
						if (request.containsAttribute("CODVCHPROFILINGPROFILO")) {
							request.delAttribute("CODVCHPROFILINGPROFILO");
						}
						if (request.containsAttribute("decDoteProcessoAssegnato")) {
							request.delAttribute("decDoteProcessoAssegnato");
						}
						if (request.containsAttribute("decDoteProcessoResidua")) {
							request.delAttribute("decDoteProcessoResidua");
						}
						if (request.containsAttribute("decDoteRisultatoAssegnato")) {
							request.delAttribute("decDoteRisultatoAssegnato");
						}
						if (request.containsAttribute("decDoteRisultatoResidua")) {
							request.delAttribute("decDoteRisultatoResidua");
						}
						// Se sto inserendo un accordo generico e lo stato occupazionale del lavoratore è I o D, allora
						// devo bloccare
						Object params[] = new Object[] { cdnLavoratore };
						SourceBean rowStatoOcc = (SourceBean) QueryExecutor
								.executeQuery("GET_INFO_GENERALI_STATO_OCC_APERTO", params, "SELECT", "SIL_DATI");
						if (rowStatoOcc != null) {
							rowStatoOcc = rowStatoOcc.containsAttribute("ROW")
									? (SourceBean) rowStatoOcc.getAttribute("ROW")
									: rowStatoOcc;
							String statoOccRagg = rowStatoOcc.getAttribute("codstatooccupazragg") != null
									? rowStatoOcc.getAttribute("codstatooccupazragg").toString()
									: "";
							if (statoOccRagg.equalsIgnoreCase(Properties.RAGG_INOCCUPATO)
									|| statoOccRagg.equalsIgnoreCase(Properties.RAGG_DISOCCUPATO)) {
								inserisci = false;
								reportOperation.reportFailure(MessageCodes.Patto.ERR_ACCORDO_IN_297);
							}
						}
					}
				} else {
					if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_30)
							|| PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_45)
							|| PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_INCLUSIONE_ATTIVA)) {
						PacchettoAdulti pa = new PacchettoAdulti(new BigDecimal(cdnLavoratore.toString()),
								PattoBean.BANDO_UMBRIA_ATTIVA);
						dataAdesionePA = pa.caricaMaxDataAdesionePortale();
						if (dataAdesionePA == null || dataAdesionePA.equals("")) {
							dataAdesionePA = pa.caricaDataAdesione();
						}
						if (dataAdesionePA == null || dataAdesionePA.equals("")) {
							inserisci = false;
							reportOperation.reportFailure(MessageCodes.Patto.ERR_NO_DATA_ADESIONE_PA);
						} else {
							RequestContainer requestContainer = getRequestContainer();
							SessionContainer sessionContainer = (SessionContainer) requestContainer
									.getSessionContainer();
							String encryptKey = (String) sessionContainer.getAttribute("_ENCRYPTER_KEY_");
							SourceBean rowLav = PattoBean.caricaInfoLavoratore(encryptKey, cdnLavoratore,
									dataAdesionePA);
							Vector rows = rowLav.getAttributeAsVector("ROW");
							if ((rows != null) && !rows.isEmpty()) {
								int size = rows.size();
								for (int i = 0; i < size; i++) {
									SourceBean row = (SourceBean) rows.get(i);
									Object prgIscrizione = row.getAttribute("PRGCMISCR");
									String raggTipoIscrizione = (String) row.getAttribute("CODMONOTIPORAGG");
									dataNascitaLav = (String) row.getAttribute("DATNASC");
									dataInizioValINAT = (String) row.getAttribute("DATINIZIOINCATT");
									numValoreIsee = (BigDecimal) row.getAttribute("NUMVALOREISEE");
									dataDichiarazioneDid = (String) row.getAttribute("DATADID");
									mesiAnzianitaDidSup24 = (String) row.getAttribute("ANZIANITADIDSUP24MESI");
									if (dataDichiarazioneDid != null && !dataDichiarazioneDid.equals("")
											&& new Integer(mesiAnzianitaDidSup24).intValue() > 0) {
										isAnzianitaOver24Mesi = true;
									}
									if (numValoreIsee != null
											&& numValoreIsee.compareTo(PattoBean.ISEE_PACCHETTO_ADULTI) < 0) {
										isOkISEE = true;
									}
									if (prgIscrizione != null && raggTipoIscrizione != null
											&& raggTipoIscrizione.equalsIgnoreCase(Properties.CM_RAGG_DISABILE)) {
										iscrDisabile = true;
									}
								}
							}

							if (dataDichiarazioneDid == null || dataDichiarazioneDid.equals("")) {
								inserisci = false;
								reportOperation.reportFailure(MessageCodes.Patto.ERR_NO_DID_APERTA_ADESIONE_PA);
							} else {
								if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_30)) {
									if (iscrDisabile) {
										inserisci = false;
										reportOperation.reportFailure(
												MessageCodes.PacchettoAdulti.ERR_ISCRIZIONE_DISABILE_PRESENTE);
									} else {
										if (Controlli.maggioreDiUno(dataNascitaLav, 30, dataAdesionePA)
												&& Controlli.minoreDiUno(dataNascitaLav, 45, dataAdesionePA)) {
											isOkOver30 = true;
										} else {
											if (Controlli.maggioreDiUno(dataNascitaLav, 45, dataAdesionePA)
													&& !isAnzianitaOver24Mesi) {
												isOkOver30 = true;
											}
										}
										if (!isOkOver30) {
											inserisci = false;
											reportOperation.reportFailure(MessageCodes.Patto.ERR_ETA_LAV_PATTO_OVER30);
										}
									}
								} else {
									if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_45)) {
										if (iscrDisabile
												&& Controlli.maggioreDiUno(dataNascitaLav, 30, dataAdesionePA)) {
											isOkOver45 = true;
										} else {
											if (!iscrDisabile
													&& Controlli.maggioreDiUno(dataNascitaLav, 45, dataAdesionePA)
													&& isAnzianitaOver24Mesi) {
												isOkOver45 = true;
											}
										}
										if (!isOkOver45) {
											inserisci = false;
											reportOperation.reportFailure(MessageCodes.Patto.ERR_ETA_LAV_PATTO_OVER45);
										}
									} else {
										if (PattoBean.checkMisuraProgramma(programmi,
												PattoBean.DB_MISURE_INCLUSIONE_ATTIVA)) {
											if (dataInizioValINAT == null || dataInizioValINAT.equals("")
													|| DateUtils.compare(dataInizioValINAT, oggi) > 0) {
												inserisci = false;
												reportOperation.reportFailure(
														MessageCodes.Patto.ERR_MISURA_INCLUSIONE_ATTIVA_NON_VALIDA);
											} else {
												if (iscrDisabile) {
													inserisci = false;
													reportOperation.reportFailure(
															MessageCodes.PacchettoAdulti.ERR_ISCRIZIONE_DISABILE_PRESENTE);
												} else {
													if (Controlli.maggioreDiUno(dataNascitaLav, 30, dataAdesionePA)
															&& isOkISEE) {
														isOkIncAtt = true;
													}
													if (!isOkIncAtt) {
														inserisci = false;
														reportOperation.reportFailure(
																MessageCodes.Patto.ERR_MISURA_INCLUSIONE_ATTIVA_ISEE);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			if (inserisci) {
				String codMotivoFineAtto = request.containsAttribute("codMotivoFineAtto")
						? request.getAttribute("codMotivoFineAtto").toString()
						: "";
				if (codMotivoFineAtto.equalsIgnoreCase(Properties.CHIUSURA_AUTOMATICA_ACCORDO)) {
					reportOperation.reportFailure(MessageCodes.Patto.MOTIVO_CHIUSURA_PATTO_ACCORDO_NON_POSSIBILE);
				} else {
					String codStatoAttoPatto = request.containsAttribute("codStatoAtto")
							? request.getAttribute("codStatoAtto").toString()
							: "";
					if (!codStatoAttoPatto.equalsIgnoreCase("PR")) {
						if (request.containsAttribute("prgstatooccupaz")) {
							request.delAttribute("prgstatooccupaz");
						}
					}
					String codVchProfilingLavoratore = request.containsAttribute("CODVCHPROFILINGPROFILO")
							? request.getAttribute("CODVCHPROFILINGPROFILO").toString()
							: "";

					if (!codVchProfilingLavoratore.equals("")) {
						String[] profiling = codVchProfilingLavoratore.split("--");
						request.setAttribute("PRGLAVORATOREPROFILO", profiling[0]);
						request.updAttribute("CODVCHPROFILING", profiling[1]);
						if (request.containsAttribute("decDoteProcessoAssegnato")) {
							request.updAttribute("decDoteProcessoResidua",
									request.getAttribute("decDoteProcessoAssegnato"));
						}
						if (request.containsAttribute("decDoteRisultatoAssegnato")) {
							request.updAttribute("decDoteRisultatoResidua",
									request.getAttribute("decDoteRisultatoAssegnato"));
						}
					} else {
						if (request.containsAttribute("CODVCHPROFILING")) {
							request.delAttribute("CODVCHPROFILING");
						}
						if (request.containsAttribute("PRGLAVORATOREPROFILO")) {
							request.delAttribute("PRGLAVORATOREPROFILO");
						}
						if (request.containsAttribute("decDoteProcessoAssegnato")) {
							request.delAttribute("decDoteProcessoAssegnato");
						}
						if (request.containsAttribute("decDoteProcessoResidua")) {
							request.delAttribute("decDoteProcessoResidua");
						}
						if (request.containsAttribute("decDoteRisultatoAssegnato")) {
							request.delAttribute("decDoteRisultatoAssegnato");
						}
						if (request.containsAttribute("decDoteRisultatoResidua")) {
							request.delAttribute("decDoteRisultatoResidua");
						}
					}
					this.setSectionQuerySelect("SELECT_NEW_PRGPATTOLAV");// SELECT_NEW_PRGPATTOLAV
					SourceBean tmpPrg = doSelect(request, response);
					String newPrgPattoLav = "";
					if (tmpPrg != null) {
						newPrgPattoLav = tmpPrg.getAttribute("ROW.prgPattoLavoratore").toString();
						if (StringUtils.isFilledNoBlank(newPrgPattoLav)) {
							String numConfigSchedaPartecipante = request.containsAttribute("CODCONFIGURAZIONE_SCHEDA")
									? request.getAttribute("CODCONFIGURAZIONE_SCHEDA").toString()
									: Properties.DEFAULT_CONFIG;
							SourceBean schedaPartUltimoPatto = null;
							SourceBean svantaggiPartUltimoPatto = null;
							if (!numConfigSchedaPartecipante.equalsIgnoreCase(Properties.DEFAULT_CONFIG)) {
								// dall'ultimo patto lavoratore
								this.setSectionQuerySelect("SELECT_SCHEDA_PARTECIPANTE");
								schedaPartUltimoPatto = doSelect(request, response);

								this.setSectionQuerySelect("SELECT_SVANTAGGI_PARTECIPANTE");
								svantaggiPartUltimoPatto = doSelect(request, response);
							}

							if (request.containsAttribute("PRGPATTOLAVORATORE")) {
								request.delAttribute("PRGPATTOLAVORATORE");
							}
							request.setAttribute("PRGPATTOLAVORATORE", newPrgPattoLav.trim());

							this.setSectionQueryInsert("QUERY_INSERT");
							res = doInsert(request, response, true);
							ExceptionUtils.controlloDateRecordPrecedente(res,
									MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_ESISTENTE);
							reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
							if (!numConfigSchedaPartecipante.equalsIgnoreCase(Properties.DEFAULT_CONFIG)
									&& schedaPartUltimoPatto.containsAttribute("ROW")) {
								if (request.containsAttribute("PRGPATTOLAVORATORE")) {
									request.delAttribute("PRGPATTOLAVORATORE");
								}
								request.setAttribute("PRGPATTOLAVORATORE", newPrgPattoLav.trim());

								if (request.containsAttribute("CODCONTRATTO")) {
									request.updAttribute("CODCONTRATTO",
											Utils.notNull(schedaPartUltimoPatto.getAttribute("ROW.CODCONTRATTO")));
								} else {
									request.setAttribute("CODCONTRATTO",
											Utils.notNull(schedaPartUltimoPatto.getAttribute("ROW.CODCONTRATTO")));
								}
								if (request.containsAttribute("CODDURATA")) {
									request.updAttribute("CODDURATA",
											Utils.notNull(schedaPartUltimoPatto.getAttribute("ROW.CODDURATA")));
								} else {
									request.setAttribute("CODDURATA",
											Utils.notNull(schedaPartUltimoPatto.getAttribute("ROW.CODDURATA")));
								}
								if (request.containsAttribute("CODSTUDIO")) {
									request.updAttribute("CODSTUDIO",
											Utils.notNull(schedaPartUltimoPatto.getAttribute("ROW.CODSTUDIO")));
								} else {
									request.setAttribute("CODSTUDIO",
											Utils.notNull(schedaPartUltimoPatto.getAttribute("ROW.CODSTUDIO")));
								}
								if (request.containsAttribute("CODOCCUPAZIONE")) {
									request.updAttribute("CODOCCUPAZIONE",
											Utils.notNull(schedaPartUltimoPatto.getAttribute("ROW.CODOCCUPAZIONE")));
								} else {
									request.setAttribute("CODOCCUPAZIONE",
											Utils.notNull(schedaPartUltimoPatto.getAttribute("ROW.CODOCCUPAZIONE")));
								}
								if (request.containsAttribute("STRNOTESCHEDA")) {
									request.updAttribute("STRNOTESCHEDA",
											Utils.notNull(schedaPartUltimoPatto.getAttribute("ROW.STRNOTESCHEDA")));
								} else {
									request.setAttribute("STRNOTESCHEDA",
											Utils.notNull(schedaPartUltimoPatto.getAttribute("ROW.STRNOTESCHEDA")));
								}
								if (request.containsAttribute("NUMKLOPARTECIPANTE")) {
									request.updAttribute("NUMKLOPARTECIPANTE", Utils
											.notNull(schedaPartUltimoPatto.getAttribute("ROW.NUMKLOPARTECIPANTE")));
								} else {
									request.setAttribute("NUMKLOPARTECIPANTE", Utils
											.notNull(schedaPartUltimoPatto.getAttribute("ROW.NUMKLOPARTECIPANTE")));
								}
								if (request.containsAttribute("FLGCONFERMA")) {
									request.delAttribute("FLGCONFERMA");
								}
								this.setSectionQueryInsert("QUERY_INSERT_SCHEDA_PARTECIPANTE");
								Object resScheda = doInsert(request, response, true);
								ExceptionUtils.controlloDateRecordPrecedente(resScheda,
										MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_ESISTENTE);

								Vector<SourceBean> codiciSvantaggioBean = svantaggiPartUltimoPatto
										.getAttributeAsVector("ROW");
								for (int j = 0; j < codiciSvantaggioBean.size(); j++) {
									String codiceSvantaggio = (String) ((SourceBean) codiciSvantaggioBean.get(j))
											.getAttribute("CODSVANTAGGIO");
									request.delAttribute("CODSVANTAGGIO");
									request.setAttribute("CODSVANTAGGIO", codiceSvantaggio);
									this.setSectionQueryInsert("QUERY_INSERT_SVANTAGGIO");
									boolean ret = doInsert(request, response);

									if (!ret) {
										throw new Exception("impossibile inserire in or_scheda_svantaggio");
									}
								}
							}
							// gestione collega azioni consentite al patto che ho inserito
							TransactionQueryExecutor transExec = null;
							UtilsConfig utility = new UtilsConfig(PattoBean.CONF_COLLEGA_AZ_PATTO);
							String configAz = utility.getConfigurazioneDefault_Custom();
							if (configAz.equals(Properties.CUSTOM_CONFIG)) {
								try {
									transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
									transExec.initTransaction();
									PattoBean.collegaAzioniImpegniPatto(new BigDecimal(newPrgPattoLav.trim()),
											transExec);
									transExec.commitTransaction();
								} catch (Exception e) {
									if (transExec != null) {
										transExec.rollBackTransaction();
									}
									it.eng.sil.util.TraceWrapper.debug(_logger, "service()", e);
									reportOperation.reportFailure(MessageCodes.Patto.ASSOC_PATTO_PERC_CONCORD);
								}
							}
						} else {
							inserisci = false;
							throw new Exception();
						}
					} else {
						inserisci = false;
						throw new Exception();
					}

				}
			}
		} catch (QueryStrategyException qse) {
			reportOperation.reportFailure(MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_ESISTENTE);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", e);

			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "service()",
					"errore durante l'inserimento di un nuovo patto.");
		}
	}

}
// InsertPattoLav
