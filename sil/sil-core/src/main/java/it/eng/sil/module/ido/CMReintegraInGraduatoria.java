package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

/**
 * 
 * Reintegro del lavoratore in do_nominativo dalla lista dei lavoratori cancellati
 * 
 * @author iescone
 * 
 */
public class CMReintegraInGraduatoria extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CMReintegraInGraduatoria.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws EMFInternalError {

		RequestContainer reqCont = getRequestContainer();
		ResponseContainer resCont = getResponseContainer();
		ReportOperationResult ror = new ReportOperationResult(this, response);

		disableMessageIdSuccess();

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		boolean success = false;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			// calcolo i mesi di anzianità del lavoratore
			String cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "CDNLAVORATORE");
			String prgrosa = StringUtils.getAttributeStrNotNull(request, "PRGROSA");
			int numMesiAnzianita = 0;
			String dataAnzianitaCM = ""; // datdatainizio
			String dataPubblicaz = ""; // datdatainizio
			String dataChiamataCM = ""; // dataChiamataCM
			String dataSospensioneCM = ""; // datsospensione
			int mesiSospCM = 0; // nummesisospesterni
			String dataAnzPregressaOrdinaria = "";

			/*
			 * verifica versione graduatoria in ts_generale presenza parametro CODMONOTIPOGRAD 1: calcolo punteggio
			 * normale 2: calcolo punteggio con locomozione e patente
			 */
			String codMonoTipoGrad = "1";
			SourceBean querySelectTipoGrad = (SourceBean) getConfig().getAttribute("CHECK_VERSIONE_GRADUATORIA");
			SourceBean datiTipoGradCM = (SourceBean) transExec.executeQuery(reqCont, resCont, querySelectTipoGrad,
					"SELECT");
			if (datiTipoGradCM != null) {
				Vector rowsTipoGradCM = datiTipoGradCM.getAttributeAsVector("ROW");
				if (rowsTipoGradCM.size() == 1) {
					SourceBean rowtTpo = (SourceBean) rowsTipoGradCM.get(0);
					codMonoTipoGrad = (String) rowtTpo.getAttribute("CODMONOTIPOGRADCM");
				}
			}

			request.updAttribute("PRGROSA", prgrosa);

			// recupero la data di anzianità in base al tipo di incrocio
			SourceBean querySelect1 = (SourceBean) getConfig().getAttribute("CM_DATI_ISCRIZIONE");
			SourceBean datiCM1 = (SourceBean) transExec.executeQuery(reqCont, resCont, querySelect1, "SELECT");
			Vector datiCMRows = datiCM1.getContainedAttributes();
			if (!datiCMRows.isEmpty()) {
				Vector rowCM1 = datiCM1.getAttributeAsVector("ROW");

				if (rowCM1.size() == 1) {
					SourceBean row1 = (SourceBean) rowCM1.get(0);
					String codMonoTipoRag = (String) row1.getAttribute("CODMONOTIPORAGG");
					dataAnzPregressaOrdinaria = row1.getAttribute("datanzordpregressa") == null ? ""
							: (String) row1.getAttribute("datanzordpregressa");
					if (("D").equalsIgnoreCase(codMonoTipoRag)) {
						dataAnzianitaCM = row1.getAttribute("DATANZIANITA68") == null ? ""
								: (String) row1.getAttribute("DATANZIANITA68");
					} else if (("A").equalsIgnoreCase(codMonoTipoRag)) {
						dataAnzianitaCM = row1.getAttribute("DATANZIANITA68") == null ? ""
								: (String) row1.getAttribute("DATANZIANITA68");
					} else {
						dataAnzianitaCM = "";
						numMesiAnzianita = -6;
					}
				} else {
					dataAnzianitaCM = "";
					numMesiAnzianita = -6;
				}
			} else {
				numMesiAnzianita = -6;
			}

			if (numMesiAnzianita >= 0) {
				// nuova versione: calcola i mesi di anzianità fino alla
				// data di inizio pubblicazione della richiesta oppure fino
				// a sysdate
				SourceBean querySelect = (SourceBean) getConfig()
						.getAttribute("CM_DATAPUBBLICAZIONE_RICHIESTA_DA_ROSA");
				SourceBean datiCM = (SourceBean) transExec.executeQuery(reqCont, resCont, querySelect, "SELECT");
				if (datiCM != null) {
					Vector rowCM = datiCM.getAttributeAsVector("ROW");
					if (rowCM.size() == 1) {
						SourceBean row = (SourceBean) rowCM.get(0);
						dataPubblicaz = row.getAttribute("DATPUBBLICAZIONE") == null ? ""
								: (String) row.getAttribute("DATPUBBLICAZIONE");
						dataChiamataCM = row.getAttribute("DATCHIAMATACM") == null ? ""
								: (String) row.getAttribute("DATCHIAMATACM");

						// gestione tipo calcolo punteggio a senconda del parametro nel ts_generale
						if (("2").equalsIgnoreCase(codMonoTipoGrad)) {
							if (dataChiamataCM != null && !("").equalsIgnoreCase(dataChiamataCM)) {
								// calcolo i mesi di anzianità
								numMesiAnzianita = DBLoad.calcolaAnzianitaCM(cdnLavoratore, dataAnzianitaCM, "", 0,
										dataChiamataCM, dataAnzPregressaOrdinaria);
							} else {
								dataChiamataCM = "";
								numMesiAnzianita = -7;
							}
						} else {
							if (dataPubblicaz != null && !("").equalsIgnoreCase(dataPubblicaz)) {
								// calcolo i mesi di anzianità
								numMesiAnzianita = DBLoad.calcolaAnzianitaCM(cdnLavoratore, dataAnzianitaCM, "", 0,
										dataPubblicaz, dataAnzPregressaOrdinaria);
							} else {
								dataPubblicaz = "";
								numMesiAnzianita = -5;
							}
						}
					} else {
						dataPubblicaz = "";
						dataChiamataCM = "";
						if (("2").equalsIgnoreCase(codMonoTipoGrad)) {
							numMesiAnzianita = -7;
						} else {
							numMesiAnzianita = -5;
						}
					}
				}
			}
			// Cancello il lavoratore dalla lista dei cancellati dalla rosa
			setSectionQueryDelete("DELETE_CANDIDATO_CANC");
			boolean deleteLavorat = this.doDelete(request, response);
			if (deleteLavorat) {
				success = insertAdesioneCM(transExec, request, response, prgrosa, numMesiAnzianita);

				if (success) {
					ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
					transExec.commitTransaction();
					ror.reportSuccess(idSuccess);
				} else {
					transExec.rollBackTransaction();
				}
			} else {
				transExec.rollBackTransaction();

				int msgCode = MessageCodes.General.OPERATION_FAIL;
				String msg = "Reintegra in Graduatoria: errore di cancellazione lavoratore cancellato.";
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);
			}

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CMReintegraInGraduatoria::service(): Impossibile reintegrare in graduatoria!", ex);

			ror.reportFailure(idFail);
			ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}

	}

	public boolean insertAdesioneCM(TransactionQueryExecutor txExecutor, SourceBean request, SourceBean response,
			String p_prgRosaIesima, int numMesiAnzianita) {

		boolean checkSuccess = false;
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			conn = txExecutor.getDataConnection();
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			SourceBean row = new SourceBean("ROW");

			// calcolo il punteggio se i mesi di anzianità sono stati calcolati
			// altrimenti setto l'errore sull'iscrizione al collocamento mirato
			if (numMesiAnzianita >= 0) {

				statementSB = (SourceBean) getConfig().getAttribute("CM_INSERT_ADESIONE");
				statement = statementSB.getAttribute("STATEMENT").toString();
				sqlStr = SQLStatements.getStatement(statement);
				command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

				String p_prgRosa = p_prgRosaIesima;
				String p_cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "CDNLAVORATORE");
				String p_cdnUtente = StringUtils.getAttributeStrNotNull(request, "P_CDNUTENTE");
				String p_cdnGruppo = StringUtils.getAttributeStrNotNull(request, "P_CDNGRUPPO");
				String p_prgSpiContatto = "0";
				// String p_prgSpiContatto = StringUtils.getAttributeStrNotNull(request, "prgSpi");
				String p_qualifica = StringUtils.getAttributeStrNotNull(request, "QUALIFICA");

				SessionContainer session = getRequestContainer().getSessionContainer();
				String encrypterKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

				parameters = new ArrayList(10);
				// 1.Parametro di Ritorno
				parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// preparazione dei Parametri di Input
				// 2. p_prgRosa
				parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.BIGINT, new BigInteger(p_prgRosa)));
				command.setAsInputParameters(paramIndex++);
				// 3. p_cdnLavoratore
				parameters.add(conn.createDataField("p_cdnLavoratore", java.sql.Types.BIGINT,
						new BigInteger(p_cdnLavoratore)));
				command.setAsInputParameters(paramIndex++);
				// 4. p_mesiAnzianita
				parameters.add(conn.createDataField("p_mesianzianita", java.sql.Types.BIGINT,
						new BigInteger("" + numMesiAnzianita)));
				command.setAsInputParameters(paramIndex++);
				// 5. p_prgSpiContatto
				parameters.add(conn.createDataField("p_prgSpiContatto", java.sql.Types.BIGINT,
						new BigInteger(p_prgSpiContatto)));
				command.setAsInputParameters(paramIndex++);
				// 6. p_cdnUtente
				parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
				command.setAsInputParameters(paramIndex++);
				// 7. p_cdnGruppo
				parameters.add(conn.createDataField("p_cdnGruppo", java.sql.Types.BIGINT, new BigInteger(p_cdnGruppo)));
				command.setAsInputParameters(paramIndex++);
				// 8
				parameters.add(conn.createDataField("encrypterKey", java.sql.Types.VARCHAR, encrypterKey));
				command.setAsInputParameters(paramIndex++);
				// 9
				parameters.add(conn.createDataField("p_qualifica", java.sql.Types.VARCHAR, p_qualifica));
				command.setAsInputParameters(paramIndex++);
				// parametri di Output
				// 10. p_errCode
				parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
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

				row.setAttribute("CodiceRit", codiceRit);
			} else {
				codiceRit = "-2";
				row.setAttribute("CodiceRit", codiceRit);
			}

			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case 1: // XXX valutare errore da inserire
					msgCode = MessageCodes.IDO.ERR_INS_LAV_RNG;
					msg = "Reintegra in Graduatoria: errore insert do_nominativo";
					break;
				case 2: // XXX ci deve essere almeno un record dello stato occ
						// da storicizzare
					msgCode = MessageCodes.IDO.ERR_INS_STORIA_STATO_OCC;
					msg = "Reintegra in Graduatoria: errore insert as_storia_stato_occ";
					break;
				case 3: // XXX da inserire il msg errore!!!!!!
					msgCode = MessageCodes.IDO.ERR_INS_ADESIONE_MORE_PROFILO;
					msg = "Reintegra in Graduatoria: errore non è possibile aderire a più profili per lo stesso tipo";
					break;
				case 4:
					msgCode = MessageCodes.IDO.ERR_INS_ADESIONE_ART8;
					msg = "Reintegra in Graduatoria: errore non è iscritto all'art.8";
					break;
				case 5:
					msgCode = MessageCodes.IDO.ERR_INS_ADESIONE_ART18;
					msg = "Reintegra in Graduatoria: errore non è iscritto all'art.18";
					break;
				case 6:
					msgCode = MessageCodes.IDO.ERR_INS_ADESIONE_NO_REDDITO;
					msg = "Reintegra in Graduatoria: errore non ha dichiarato reddito";
					break;
				case 7:
					msgCode = MessageCodes.IDO.ERR_INS_ADESIONE_NO_DISOC_INOC;
					msg = "Reintegra in Graduatoria: errore non è inoccupato o disoccupato";
					break;
				case 9:
					msgCode = MessageCodes.IDO.ERR_INS_ADESIONE_NO_ESITO;
					msg = "Non esiste una dichiarazione di reddito ISEE recente. La dichiarazione non può essere più vecchia di 12 mesi dalla data della chiamata";
					break;
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Reintegra in Graduatoria: sqlCode=" + errCode;
					break;
				case -2: // errore iscrizione al CM
					if (numMesiAnzianita == -1) {
						msgCode = MessageCodes.CollocamentoMirato.ERROR_LAVORATORE_NON_DISOCCUPATO_INOCCUPATO;
						msg = "Il lavoratore non è disoccupato o inoccupato";
					} else if (numMesiAnzianita == -2) {
						msgCode = MessageCodes.CollocamentoMirato.ERROR_DATANZIANITA_COLL_ORDINARIO_NON_PRESENTE;
						msg = "La data anzianità del collocamento ordinario non è presente: è necessario ricalcolare gli impatti del lavoratore";
					} else if (numMesiAnzianita == -3) {
						msgCode = MessageCodes.CollocamentoMirato.ERROR_DAT_ANZ_CM_MINORE_DAT_ANZ_COLL_ORDINARIO;
						msg = "La data anzianità del collocamento mirato è minore della data anzianità del collocamento ordinario";
					} else if (numMesiAnzianita == -4) {
						msgCode = MessageCodes.CollocamentoMirato.ERROR_DAT_SOSP_CM_MINORE_DAT_ANZ_CM;
						msg = "La data sospensione del collocamento mirato è minore della data anzianità del collocamento mirato";
					} else if (numMesiAnzianita == -5) {
						msgCode = MessageCodes.CollocamentoMirato.ERROR_ADESIONE_NO_DATA_PUBBLICAZIONE;
						msg = "Non è stato valorizzata la data pubblicazione della richiesta";
					} else if (numMesiAnzianita == -6) {
						msgCode = MessageCodes.ImportMov.ERR_AVV_COLL;
						msg = "Non è in collocamento mirato";
					} else if (numMesiAnzianita == -7) {
						msgCode = MessageCodes.CollocamentoMirato.ERROR_ADESIONE_NO_DATA_CHIAMATA;
						msg = "Non è stato valorizzata la data chiamata CM della richiesta";
					}
					// msgCode = MessageCodes.IDO.ERR_MESI_ANZ_CALC_PUNTEGGIO;
					// msg = "Calcola Punteggio: sqlCode="+errCode;
					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Reintegra in Graduatoria: errore di ritorno non ammesso. SqlCode=" + errCode;

				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				checkSuccess = true;
			}
			response.setAttribute((SourceBean) row);

		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, className, msg);
		}

		return checkSuccess;
	}
}