package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.patto.bean.PacchettoAdulti;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

public class SavePattoLav extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SavePattoLav.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		Object res = null;
		boolean checkIntersezioneDate = false;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		String datStipulaPatto = request.containsAttribute("DATSTIPULA") ? request.getAttribute("DATSTIPULA").toString()
				: "";
		String datStipulaPattoOrig = request.containsAttribute("DATSTIPULAORIG")
				? request.getAttribute("DATSTIPULAORIG").toString()
				: "";
		String prgPattoLav = StringUtils.getAttributeStrNotNull(request, "PRGPATTOLAVORATORE");
		String configVoucher = StringUtils.getAttributeStrNotNull(request, "CONFIGVOUCHER");
		String flgPatto297 = request.containsAttribute("flgPatto297") ? request.getAttribute("flgPatto297").toString()
				: "";
		String codTipoPatto = request.containsAttribute("codTipoPatto")
				? request.getAttribute("codTipoPatto").toString()
				: "";
		String codTipoPattoOrig = request.containsAttribute("CODTIPOPATTOORIG")
				? request.getAttribute("CODTIPOPATTOORIG").toString()
				: "";
		String dataNascitaLav = "";
		boolean iscrDisabile = false;
		String dataInizioValINAT = "";
		String dataInizioValMGGU = "";
		String dataAdesionePA = "";
		String dataDichiarazioneDid = "";
		String mesiAnzianitaDidSup24 = "";
		String oggi = DateUtils.getNow();
		boolean isOkISEE = false;
		BigDecimal numValoreIsee = null;
		boolean isOkOver30 = false;
		boolean isOkOver45 = false;
		boolean isOkIncAtt = false;
		boolean isAnzianitaOver24Mesi = false;
		String dataInizioNaspi = "";
		String decImportoAr = "";
		String strNoteAttivazione = "";
		Object cdnLavoratore = request.getAttribute("cdnLavoratore");
		Vector<String> programmi = PattoBean.checkProgrammi(new BigDecimal(prgPattoLav), null);

		try {
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
						reportOperation.reportFailure(MessageCodes.Patto.ERR_MISURA_MGGU_NON_VALIDA);
						return;
					}
				}
			}

			if (!datStipulaPattoOrig.equals("") && !datStipulaPatto.equals("")
					&& DateUtils.compare(datStipulaPatto, datStipulaPattoOrig) < 0) {
				setSectionQuerySelect("QUERY_SELECT");
				SourceBean row = doSelect(request, response);
				if (row == null) {
					throw new Exception("Impossibile leggere i patti storicizzati");
				}

				String dataFineMax = row.containsAttribute("ROW.DATFINEMAX")
						? row.getAttribute("ROW.DATFINEMAX").toString()
						: "";
				if (!dataFineMax.equals("") && DateUtils.compare(dataFineMax, datStipulaPatto) > 0) {
					checkIntersezioneDate = true;
					throw new Exception("Data stipula minore della data di chiusura del record storicizzato");
				}
			}

			String codMotivoFineAtto = request.containsAttribute("codMotivoFineAtto")
					? request.getAttribute("codMotivoFineAtto").toString()
					: "";
			if (codMotivoFineAtto.equalsIgnoreCase(Properties.CHIUSURA_AUTOMATICA_ACCORDO)) {
				reportOperation.reportFailure(MessageCodes.Patto.MOTIVO_CHIUSURA_PATTO_ACCORDO_NON_POSSIBILE);
				return;
			}

			if (flgPatto297.equals("N")) {
				if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_30)
						|| PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_45)
						|| PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_INCLUSIONE_ATTIVA)) {
					reportOperation.reportFailure(MessageCodes.Patto.ERR_ACCORDO_GENERICO_MISURA_CONCORDATA);
					return;
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
						reportOperation.reportFailure(MessageCodes.Patto.ERR_NO_DATA_ADESIONE_PA);
						return;
					}
					RequestContainer requestContainer = getRequestContainer();
					SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
					String encryptKey = (String) sessionContainer.getAttribute("_ENCRYPTER_KEY_");
					SourceBean rowLav = PattoBean.caricaInfoLavoratore(encryptKey, cdnLavoratore, dataAdesionePA);
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
							if (numValoreIsee != null && numValoreIsee.compareTo(PattoBean.ISEE_PACCHETTO_ADULTI) < 0) {
								isOkISEE = true;
							}
							if (prgIscrizione != null && raggTipoIscrizione != null
									&& raggTipoIscrizione.equalsIgnoreCase(Properties.CM_RAGG_DISABILE)) {
								iscrDisabile = true;
							}
						}
					}

					if (dataDichiarazioneDid == null || dataDichiarazioneDid.equals("")) {
						reportOperation.reportFailure(MessageCodes.Patto.ERR_NO_DID_APERTA_ADESIONE_PA);
						return;
					}
				}

				if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_30)) {
					if (iscrDisabile) {
						reportOperation.reportFailure(MessageCodes.PacchettoAdulti.ERR_ISCRIZIONE_DISABILE_PRESENTE);
						return;
					} else {
						if (Controlli.maggioreDiUno(dataNascitaLav, 30, dataAdesionePA)
								&& Controlli.minoreDiUno(dataNascitaLav, 45, dataAdesionePA)) {
							isOkOver30 = true;
						} else {
							if (Controlli.maggioreDiUno(dataNascitaLav, 45, dataAdesionePA) && !isAnzianitaOver24Mesi) {
								isOkOver30 = true;
							}
						}
						if (!isOkOver30) {
							reportOperation.reportFailure(MessageCodes.Patto.ERR_ETA_LAV_PATTO_OVER30);
							return;
						}
					}
				} else {
					if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_45)) {
						if (iscrDisabile && Controlli.maggioreDiUno(dataNascitaLav, 30, dataAdesionePA)) {
							isOkOver45 = true;
						} else {
							if (!iscrDisabile && Controlli.maggioreDiUno(dataNascitaLav, 45, dataAdesionePA)
									&& isAnzianitaOver24Mesi) {
								isOkOver45 = true;
							}
						}
						if (!isOkOver45) {
							reportOperation.reportFailure(MessageCodes.Patto.ERR_ETA_LAV_PATTO_OVER45);
							return;
						}
					} else {
						if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_INCLUSIONE_ATTIVA)) {
							if (dataInizioValINAT == null || dataInizioValINAT.equals("")
									|| DateUtils.compare(dataInizioValINAT, oggi) > 0) {
								reportOperation
										.reportFailure(MessageCodes.Patto.ERR_MISURA_INCLUSIONE_ATTIVA_NON_VALIDA);
								return;
							} else {
								if (iscrDisabile) {
									reportOperation.reportFailure(
											MessageCodes.PacchettoAdulti.ERR_ISCRIZIONE_DISABILE_PRESENTE);
									return;
								} else {
									if (Controlli.maggioreDiUno(dataNascitaLav, 30, dataAdesionePA) && isOkISEE) {
										isOkIncAtt = true;
									}
									if (!isOkIncAtt) {
										reportOperation
												.reportFailure(MessageCodes.Patto.ERR_MISURA_INCLUSIONE_ATTIVA_ISEE);
										return;
									}
								}
							}
						}
					}
				}
			}

			// Inizio Controlli commentati - Domenico 10/10/2016
			// if (!codTipoPattoOrig.equals("") && !codTipoPatto.equalsIgnoreCase(codTipoPattoOrig)) {
			// SourceBean rowAzioni = PattoBean.caricaNumeroAzioniCollegate(request.getAttribute("PRGPATTOLAVORATORE"));
			// if (codTipoPatto.equalsIgnoreCase(PattoBean.DB_MISURE_OVER_30)) {
			// if (rowAzioni != null) {
			// BigDecimal numAzO4IN = (BigDecimal)rowAzioni.getAttribute("numAzO4IN");
			// if (numAzO4IN != null && numAzO4IN.intValue() > 0) {
			// reportOperation.reportFailure(MessageCodes.Patto.ERR_CAMBIO_MISURACONCORDATA_PATTO);
			// return;
			// }
			// }
			// }
			// else {
			// if (codTipoPatto.equalsIgnoreCase(PattoBean.DB_MISURE_OVER_45)) {
			// if (rowAzioni != null) {
			// BigDecimal numAzO3IN = (BigDecimal)rowAzioni.getAttribute("numAzO3IN");
			// if (numAzO3IN != null && numAzO3IN.intValue() > 0) {
			// reportOperation.reportFailure(MessageCodes.Patto.ERR_CAMBIO_MISURACONCORDATA_PATTO);
			// return;
			// }
			// }
			// }
			// else {
			// if (codTipoPatto.equalsIgnoreCase(PattoBean.DB_MISURE_INCLUSIONE_ATTIVA)) {
			// if (rowAzioni != null) {
			// BigDecimal numAzO3O4 = (BigDecimal)rowAzioni.getAttribute("numAzO3O4");
			// if (numAzO3O4 != null && numAzO3O4.intValue() > 0) {
			// reportOperation.reportFailure(MessageCodes.Patto.ERR_CAMBIO_MISURACONCORDATA_PATTO);
			// return;
			// }
			// }
			// }
			// else {
			// if (rowAzioni != null) {
			// BigDecimal numAzO3O4IN = (BigDecimal)rowAzioni.getAttribute("numAzO3O4IN");
			// if (numAzO3O4IN != null && numAzO3O4IN.intValue() > 0) {
			// reportOperation.reportFailure(MessageCodes.Patto.ERR_CAMBIO_MISURACONCORDATA_PATTO);
			// return;
			// }
			// }
			// }
			// }
			// }
			// }
			// Fine Controlli commentati - Domenico 10/10/2016

			Vector profiliLav = null;
			String codVchProfiling = StringUtils.getAttributeStrNotNull(request, "CODVCHPROFILING");
			String codVchProfilingLavoratore = request.containsAttribute("CODVCHPROFILINGPROFILO")
					? request.getAttribute("CODVCHPROFILINGPROFILO").toString()
					: "";

			if (!codVchProfilingLavoratore.equals("")) {
				String[] profiling = codVchProfilingLavoratore.split("--");
				codVchProfiling = profiling[1];
				request.setAttribute("PRGLAVORATOREPROFILO", profiling[0]);
				request.updAttribute("CODVCHPROFILING", codVchProfiling);
			} else {
				// codVchProfiling vecchio valorizzato e la tendina degli indici calcolati non è vuota ma l'operatore
				// non passa il profilo, devo BLOCCARE
				if (!codVchProfiling.equals("")) {
					profiliLav = PattoBean.loadProfiliCalcolati(cdnLavoratore);
					if (profiliLav.size() > 0) {
						reportOperation.reportFailure(MessageCodes.Patto.ERR_PROFILO_LAVORATORE_EMPTY);
						return;
					}
				}
			}

			if (request.containsAttribute("INDICESVANTAGGIO150")
					&& !request.getAttribute("INDICESVANTAGGIO150").toString().equals("")) {
				if (request.getAttribute("NUMPROFILING") == null
						|| request.getAttribute("NUMPROFILING").toString().equals("")) {
					setSectionQuerySelect("QUERY_SELECT_PROFILING_150");
					SourceBean rowProfiling = doSelect(request, response);
					if (rowProfiling != null) {
						BigDecimal numProfilingFascia = rowProfiling.containsAttribute("ROW.NUMPROFILING")
								? (BigDecimal) rowProfiling.getAttribute("ROW.NUMPROFILING")
								: null;
						if (numProfilingFascia != null) {
							if (request.containsAttribute("NUMPROFILING")) {
								request.delAttribute("NUMPROFILING");
							}
							request.setAttribute("NUMPROFILING", numProfilingFascia);
						}
					}
				}
			}
			// Calcolo dell'impegnato e dello speso dei voucher a Processo e a Risultato per il patto, se prevista la
			// gestione dei Voucher
			if (configVoucher.equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {

				String doteAProcessoAssegnata = StringUtils.getAttributeStrNotNull(request, "decDoteProcessoAssegnato");
				String doteARisultatoAssegnata = StringUtils.getAttributeStrNotNull(request,
						"decDoteRisultatoAssegnato");

				if (codVchProfiling.equals("")) {
					doteAProcessoAssegnata = "0";
					doteARisultatoAssegnata = "0";
				}

				DataConnection conn = null;
				DataResult dr = null;
				StoredProcedureCommand command = null;

				try {

					String pool = (String) getConfig().getAttribute("POOL");
					DataConnectionManager dcm = DataConnectionManager.getInstance();
					conn = dcm.getConnection(pool);

					command = (StoredProcedureCommand) conn
							.createStoredProcedureCommand("{ call PG_VOUCHER.calcolaResiduoDoti(?,?,?,?,?,?,?) }");

					int paramIndex = 0;
					ArrayList parameters = new ArrayList(7);

					// preparazione dei Parametri di Input
					parameters.add(
							conn.createDataField("doteAProcesso", Types.DOUBLE, new Double(doteAProcessoAssegnata)));
					command.setAsInputParameters(paramIndex++);

					parameters.add(
							conn.createDataField("doteARisultato", Types.DOUBLE, new Double(doteARisultatoAssegnata)));
					command.setAsInputParameters(paramIndex++);

					parameters.add(conn.createDataField("prgpattolavoratore", Types.BIGINT, prgPattoLav));
					command.setAsInputParameters(paramIndex++);

					// Parametro di output
					parameters.add(conn.createDataField("residuoAProcesso", Types.DOUBLE, null));
					command.setAsOutputParameters(paramIndex++);

					parameters.add(conn.createDataField("residuoARisultato", Types.DOUBLE, null));
					command.setAsOutputParameters(paramIndex++);

					parameters.add(conn.createDataField("errCodeOut", Types.VARCHAR, null));
					command.setAsOutputParameters(paramIndex++);

					parameters.add(conn.createDataField("messaggioErr", Types.VARCHAR, null));
					command.setAsOutputParameters(paramIndex++);

					// Chiamata alla Stored Procedure
					dr = command.execute(parameters);
					CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
					List outputParams = cdr.getContainedDataResult();
					// Recupero i valori di output della stored
					// 0. residuoAProcesso
					PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
					DataField df = pdr.getPunctualDatafield();
					Object residuoAProcesso = df.getObjectValue();
					// 1. residuoARisultato
					pdr = (PunctualDataResult) outputParams.get(1);
					df = pdr.getPunctualDatafield();
					Object residuoARisultato = df.getObjectValue();
					// 2. errCodeOut
					pdr = (PunctualDataResult) outputParams.get(2);
					df = pdr.getPunctualDatafield();
					String errCodeOut = df.getStringValue();

					if (errCodeOut.equalsIgnoreCase("00")) {
						Float residuoP = new Float(residuoAProcesso.toString());
						Float residuoR = new Float(residuoARisultato.toString());

						if (residuoP >= 0 && residuoR >= 0) {
							if (codVchProfiling.equals("") && residuoP == 0 && residuoR == 0) {
								if (request.containsAttribute("decDoteProcessoResidua")) {
									request.delAttribute("decDoteProcessoResidua");
								}
								if (request.containsAttribute("decDoteRisultatoResidua")) {
									request.delAttribute("decDoteRisultatoResidua");
								}
							} else {
								request.updAttribute("decDoteProcessoResidua", residuoP.toString());
								request.updAttribute("decDoteRisultatoResidua", residuoR.toString());
							}
							res = doUpdate(request, response, true);

							reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
						} else {
							reportOperation.reportFailure(MessageCodes.Patto.ERR_DOTE_PROCESSO_RISULTATO);
						}

					} else {
						reportOperation.reportFailure(MessageCodes.Patto.ERR_CALCOLO_RESIDUO_DOTI);
					}
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "Errore in aggiornamento patto:calcolo residuo voucher",
							e);
					reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
				} finally {
					Utils.releaseResources(conn, command, dr);
				}
			} else {
				res = doUpdate(request, response, true);
				reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
			}

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore in aggiornamento patto.", e);

			if (checkIntersezioneDate) {
				reportOperation.reportFailure(MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
			} else {
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
			}
		}
	}
}// SavePattoLav
