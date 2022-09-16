package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
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
import it.eng.sil.security.User;
import it.eng.sil.util.EncryptDecryptUtils;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

/*
 *    
 * @author coticone
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CMCalcolaPunteggio extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CMCalcolaPunteggio.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws EMFInternalError {

		RequestContainer reqCont = getRequestContainer();
		ResponseContainer resCont = getResponseContainer();
		ReportOperationResult ror = new ReportOperationResult(this, response);

		disableMessageIdSuccess();

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			String prgTipoIncrocio = (String) request.getAttribute("PRGTIPOINCROCIO");

			// calcolo i mesi di anzianità
			Object cdnLavoratore = request.getAttribute("CDNLAVORATORE");
			int numMesiAnzianita = 0;
			String dataAnzianitaCM = ""; // datdatainizio
			String dataSospensioneCM = ""; // datsospensione
			String dataPubblicaz = ""; // dataPubblicazioneRichiesta
			String dataChiamataCM = ""; // dataChiamataCM
			int mesiSospCM = 0; // nummesisospesterni
			String dataAnzPregressaOrdinaria = "";

			if (!("12").equalsIgnoreCase(prgTipoIncrocio)) {

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

				// nuova versione: calcola i mesi di anzianità dalla data di
				// inizio pubblicazione della richiesta
				SourceBean querySelect1 = (SourceBean) getConfig().getAttribute("CM_DATI_ISCRIZIONE");
				SourceBean datiCM1 = (SourceBean) transExec.executeQuery(reqCont, resCont, querySelect1, "SELECT");
				if (datiCM1 != null) {
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

					if (numMesiAnzianita >= 0) {
						SourceBean querySelect = (SourceBean) getConfig()
								.getAttribute("CM_DATAPUBBLICAZIONE_RICHIESTA");
						SourceBean datiCM = (SourceBean) transExec.executeQuery(reqCont, resCont, querySelect,
								"SELECT");
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
										numMesiAnzianita = DBLoad.calcolaAnzianitaCM(cdnLavoratore, dataAnzianitaCM, "",
												0, dataChiamataCM, dataAnzPregressaOrdinaria);
									} else {
										dataChiamataCM = "";
										numMesiAnzianita = -7;
									}
								} else {
									if (dataPubblicaz != null && !("").equalsIgnoreCase(dataPubblicaz)) {
										// calcolo i mesi di anzianità
										numMesiAnzianita = DBLoad.calcolaAnzianitaCM(cdnLavoratore, dataAnzianitaCM, "",
												0, dataPubblicaz, dataAnzPregressaOrdinaria);
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
				}
			}

			calcolaPunteggio(request, response, numMesiAnzianita);

			transExec.commitTransaction();
			ror.reportSuccess(idSuccess);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CMCalcolaPunteggio::service(): Impossibile calcolare il punteggio!", ex);

			ror.reportFailure(idFail);
			// ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}
	}

	/**
	 * @param request
	 * @param response
	 * @param cdnLavoratore
	 * @param dataAnzianitaCM
	 * @param dataSospensioneCM
	 * @param mesiSospCM
	 */
	private void calcolaPunteggio(SourceBean request, SourceBean response, int numMesiAnzianita) {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		DataConnection conn = null;
		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			String prgIncrocio = "";
			String prgRosa = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = dcm.getConnection(pool);
			SourceBean row = new SourceBean("ROW");

			// calcolo il punteggio se i mesi di anzianità sono stati calcolati
			// altrimenti setto l'errore sull'iscrizione al collocamento mirato
			if (numMesiAnzianita >= 0) {

				statementSB = (SourceBean) getConfig().getAttribute("CM_CALCOLO_PUNTEGGIO_CANDIDATO");
				statement = statementSB.getAttribute("STATEMENT").toString();
				sqlStr = SQLStatements.getStatement(statement);
				command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

				String p_prgNominatico = StringUtils.getAttributeStrNotNull(request, "PRGNOMINATIVO");
				String tipoGraduatoria = "CM";
				String p_cdnUtente = String.valueOf(user.getCodut());
				String cdnLavoratoreDecrypt = StringUtils.getAttributeStrNotNull(request, "CDNLAVORATORE");
				String cdnLavoratoreEncrypt = EncryptDecryptUtils.encrypt(cdnLavoratoreDecrypt);

				parameters = new ArrayList(7);
				// 1.Parametro di Ritorno
				parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 2. prgNominativo
				parameters.add(
						conn.createDataField("prgNominativo", java.sql.Types.BIGINT, new BigInteger(p_prgNominatico)));
				command.setAsInputParameters(paramIndex++);
				// 3. p_tipoGraduatoria
				parameters.add(conn.createDataField("tipoGraduatoria", java.sql.Types.VARCHAR, tipoGraduatoria));
				command.setAsInputParameters(paramIndex++);
				// 4. p_mesiAnzianita
				parameters.add(conn.createDataField("p_mesianzianita", java.sql.Types.BIGINT,
						new BigInteger("" + numMesiAnzianita)));
				command.setAsInputParameters(paramIndex++);
				// 5. p_cdnUtente
				parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
				command.setAsInputParameters(paramIndex++);
				// 6. p_cdnLavoratore
				parameters.add(conn.createDataField("p_cdnLavoratore", java.sql.Types.VARCHAR, cdnLavoratoreEncrypt));
				command.setAsInputParameters(paramIndex++);
				// 7. p_errCode
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
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Calcola Punteggio: sqlCode=" + errCode;

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
						msg = "Non è stata valorizzata la data pubblicazione della richiesta";
					} else if (numMesiAnzianita == -6) {
						msgCode = MessageCodes.CollocamentoMirato.ERROR_DATANZIANITA_COLL_MIRATO_NON_PRESENTE;
						msg = "La data anzianità del collocamento mirato non è presente";
					} else if (numMesiAnzianita == -7) {
						msgCode = MessageCodes.CollocamentoMirato.ERROR_ADESIONE_NO_DATA_CHIAMATA;
						msg = "Non è stata valorizzata la data chiamata della richiesta";
					}

					// msgCode = MessageCodes.IDO.ERR_MESI_ANZ_CALC_PUNTEGGIO;
					// msg = "Calcola Punteggio: sqlCode="+errCode;

					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Calcola Punteggio: errore di ritorno non ammesso. SqlCode=" + errCode;

				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}
			response.setAttribute((SourceBean) row);
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, className, msg);
		} finally {
			Utils.releaseResources(null, command2, dr2);
			Utils.releaseResources(conn, command, dr);
		}

	}

}