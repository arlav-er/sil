package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Sottosistema;

public class SaveStatoRich extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SaveStatoRich.class.getName());

	private String className = this.getClass().getName();
	private static final String TRUE = "TRUE";
	private int messageIdFail = MessageCodes.General.OPERATION_FAIL;

	public void service(SourceBean request, SourceBean response) throws Exception {

		boolean goUpdate = true;

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		disableMessageIdSuccess();
		// per Art. 16 se modalità di evasione è ASTA ART 16
		// controllare se è presente la data chiamata
		String codModalita = StringUtils.getAttributeStrNotNull(request, "CODEVASIONE");
		String codStatoRich = StringUtils.getAttributeStrNotNull(request, "cdnStatoRich");
		if ("AS".equalsIgnoreCase(codModalita)) {
			this.setSectionQuerySelect("QUERY_SELECT_AS");
			SourceBean sourceData = doSelect(request, response, false);
			// Non voglio che doSelect produca alcun
			// messaggio di successo

			Object dataChiamata = sourceData.getAttribute("ROW.DATCHIAMATA");
			if (dataChiamata == null) {
				// errore
				reportOperation.reportFailure(MessageCodes.IDO.CHECK_DATA_CHIAMATA);

				goUpdate = false;

			}
		}

		// INIT-PARTE-TEMP
		if (Sottosistema.CM.isOff()) {
			// END-PARTE-TEMP

			// INIT-PARTE-TEMP
		} else {
			// END-PARTE-TEMP

			if ("CMA".equalsIgnoreCase(codModalita)) {
				this.setSectionQuerySelect("QUERY_SELECT_CM");
				SourceBean sourceData = doSelect(request, response, false);
				// Non voglio che doSelect produca alcun
				// messaggio di successo

				Object codTipoGrad = sourceData.getAttribute("ROW.CODMONOTIPOGRAD");
				if (codTipoGrad == null) {
					// errore
					reportOperation.reportFailure(MessageCodes.IDO.CHECK_CM_TIPO_GRAD);
					goUpdate = false;
				} else if ("G".equalsIgnoreCase((String) codTipoGrad)) {
					reportOperation.reportFailure(MessageCodes.IDO.CHECK_CM_TIPO_GRAD);
					goUpdate = false;
				}

				if (goUpdate) {
					Object dataChiamataCM = sourceData.getAttribute("ROW.DATCHIAMATACM");
					if (dataChiamataCM == null) {
						// errore
						reportOperation.reportFailure(MessageCodes.IDO.CHECK_CM_DATA_CHIAMATA);
						goUpdate = false;

					}
				}
			}

			if ("CMG".equalsIgnoreCase(codModalita)) {
				this.setSectionQuerySelect("QUERY_SELECT_CM");
				SourceBean sourceData = doSelect(request, response, false);
				// Non voglio che doSelect produca alcun
				// messaggio di successo

				Object codTipoGrad = sourceData.getAttribute("ROW.CODMONOTIPOGRAD");
				if (codTipoGrad == null) {
					// errore
					reportOperation.reportFailure(MessageCodes.IDO.CHECK_CM_TIPO_GRAD);
					goUpdate = false;
				} else if ("D".equalsIgnoreCase((String) codTipoGrad) || "A".equalsIgnoreCase((String) codTipoGrad)) {
					reportOperation.reportFailure(MessageCodes.IDO.CHECK_CM_TIPO_GRAD);
					goUpdate = false;
				}

				if (goUpdate) {
					String codTipoLista = (String) sourceData.getAttribute("ROW.CODTIPOLISTA");
					if (codTipoLista == null || ("").equals(codTipoLista)) {
						reportOperation.reportFailure(MessageCodes.IDO.CHECK_CM_TIPO_LISTA);
						goUpdate = false;
					}
				}
			}

			// INIT-PARTE-TEMP
		}
		// END-PARTE-TEMP

		/*
		 * se è valorizzata la data si procede all'update! si possono avere due situazioni: 1) se viene verificato che
		 * il codModalità è AS e lo stato di evasione è Inserita si procede alla transazione per l'aggiornamento dello
		 * statoRichiesta e l'inserimento attraverso la storeProcedure dell'incrocio e della rosa 2) se codModalità è
		 * diverso da AS e lo stato di evasione è Inserita oppure lo codModalità è AS e lo stato è diverso da Inserita
		 * viene effettuato solo l'aggiornamento
		 */
		if (goUpdate) {

			if ("AS".equalsIgnoreCase(codModalita) && "1".equalsIgnoreCase(codStatoRich)) {
				int idSuccess = this.disableMessageIdSuccess();
				int idFail = this.disableMessageIdFail();
				boolean errors = false;
				TransactionQueryExecutor transExec = null;
				try {
					transExec = new TransactionQueryExecutor(getPool());
					enableTransactions(transExec);
					transExec.initTransaction();

					boolean ok = matchCreaRosaNominativaAS(request, response);
					if (!ok) {
						throw new Exception("ERRORE ESEGUENDO LA CREAZIONE DELLA ROSA GREZZA");
					} else {
						boolean checkUpd = this.doUpdate(request, response);
						if (!checkUpd) {
							throw new Exception("ERRORE ESEGUENDO L'AGGIORNAMENTO DELLO STATO DELLA RICHIESTA");
						}
					}

					transExec.commitTransaction();
					reportOperation.reportSuccess(idSuccess);
				} catch (Exception ex) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "::service(): Impossibile inserire lo stato richiesta!",
							ex);

					reportOperation.reportFailure(idFail);
					reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
					if (transExec != null) {
						transExec.rollBackTransaction();
					}
					errors = true;
				}
			} else if ((("CMA").equalsIgnoreCase(codModalita) || ("CMG").equalsIgnoreCase(codModalita))
					&& "1".equalsIgnoreCase(codStatoRich)) {
				int idSuccess = this.disableMessageIdSuccess();
				int idFail = this.disableMessageIdFail();
				boolean errors = false;
				TransactionQueryExecutor transExec = null;
				try {
					transExec = new TransactionQueryExecutor(getPool());
					enableTransactions(transExec);
					transExec.initTransaction();

					this.setSectionQuerySelect("QUERY_SELECT_CM");
					SourceBean sourceData = doSelect(request, response, false);
					String codTipoGrad = (String) sourceData.getAttribute("ROW.CODMONOTIPOGRAD");

					boolean ok = matchCreaRosaNominativaCM(request, response, codTipoGrad);
					if (!ok) {
						throw new Exception("ERRORE ESEGUENDO LA CREAZIONE DELLA ROSA GREZZA");
					} else {
						boolean checkUpd = this.doUpdate(request, response);
						if (!checkUpd) {
							throw new Exception("ERRORE ESEGUENDO L'AGGIORNAMENTO DELLO STATO DELLA RICHIESTA");
						}
					}

					transExec.commitTransaction();
					reportOperation.reportSuccess(idSuccess);
				} catch (Exception ex) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "::service(): Impossibile inserire lo stato richiesta!",
							ex);

					reportOperation.reportFailure(idFail);
					reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
					if (transExec != null) {
						transExec.rollBackTransaction();
					}
					errors = true;
				}
			} else {
				boolean checkUpdate = this.doUpdate(request, response);

				if (checkUpdate) {
					reportOperation.reportFailure(MessageCodes.General.UPDATE_SUCCESS);
				}
			}
		}
	}

	/**
	 * Il metodo gestisce la chiamata alla storedProcedure PG_INCROCIO.AScreaRosaNomGrezza per la creazione
	 * dell'incrocio per ogni profilo per i tipi di incrocio relativi all'Art. 16 (interruttore AS) e della rosa
	 * corrispondente. In caso affermativo la funzione SQL ritorna 0. In caso di errore vengono gestiti i relativi
	 * messaggi: -1 -> operazione fallita 2 -> la richiesta e' chiusa 3 -> la richiesta e' chius
	 * 
	 * @param request
	 * @param response
	 */
	public boolean matchCreaRosaNominativaAS(SourceBean request, SourceBean response) {
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

			statementSB = (SourceBean) getConfig().getAttribute("AS_CREA_ROSA");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			String p_cdnUtente = StringUtils.getAttributeStrNotNull(request, "P_CDNUTENTE");
			String codEvasione = StringUtils.getAttributeStrNotNull(request, "CODEVASIONE");

			parameters = new ArrayList(7);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgRichiestaAz
			parameters.add(
					conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT, new BigInteger(p_prgRichiestaAz)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_codEvasione
			parameters.add(conn.createDataField("p_codEvasione", java.sql.Types.VARCHAR, codEvasione));
			command.setAsInputParameters(paramIndex++);
			// 4. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.VARCHAR, p_cdnUtente));
			command.setAsInputParameters(paramIndex++);
			// parametri di Output
			// 5. p_errCode
			parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 6. p_out_prgIncrocio
			parameters.add(conn.createDataField("p_out_prgIncrocio", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 7. p_out_prgRosa
			parameters.add(conn.createDataField("p_out_prgRosa", java.sql.Types.VARCHAR, null));
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
			// 1. p_out_prgIncrocio
			pdr = (PunctualDataResult) outputParams.get(2);
			df = pdr.getPunctualDatafield();
			prgIncrocio = (String) df.getStringValue();
			if (prgIncrocio == null) {
				prgIncrocio = "";
			}
			// 2. p_out_prgRosa
			pdr = (PunctualDataResult) outputParams.get(3);
			df = pdr.getPunctualDatafield();
			prgRosa = (String) df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case 1: // non è stata inserita nessuna mansione nella richiesta
					msgCode = MessageCodes.IDO.ERR_CRNG_NO_MANSIONE;
					msg = "Creazione rosa nominativa grezza fallita: manca la mansione nella richiesta";

					break;
				case 2: // la richiesta è chiusa totalmente
					msgCode = MessageCodes.IDO.ERR_CRNG_RICH_CHIUSA_TOT;
					msg = "Creazione rosa nominativa grezza fallita: la richiesta e' chiusa totalmente";

					break;
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Creazione rosa nominativa grezza fallita: sqlCode=" + errCode;

					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Creazione rosa nominativa grezza fallita: errore di ritorno non ammesso. SqlCode=" + errCode;

				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				// response.setAttribute("MATCH_PRGROSA", prgRosa);
				ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}
			response.setAttribute((SourceBean) row);
			return true;
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, className, msg);
			return false;
		} finally {
			Utils.releaseResources(null, command2, dr2);
			Utils.releaseResources(conn, command, dr);
		}

	}

	/**
	 * Il metodo gestisce la chiamata alla storedProcedure PG_COLL_MIRATO.CMcreaRosaNomGrezza per la creazione
	 * dell'incrocio per ogni profilo per i tipi di incrocio relativi al CM (interruttore CM) e della rosa
	 * corrispondente. In caso affermativo la funzione SQL ritorna 0. In caso di errore vengono gestiti i relativi
	 * messaggi: -1 -> operazione fallita 2 -> la richiesta e' chiusa 3 -> la richiesta e' chius
	 * 
	 * @param request
	 * @param response
	 */
	public boolean matchCreaRosaNominativaCM(SourceBean request, SourceBean response, String codTipoGrad) {
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

			statementSB = (SourceBean) getConfig().getAttribute("CM_CREA_ROSA");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			String codEvasione = StringUtils.getAttributeStrNotNull(request, "CODEVASIONE");
			String p_cdnUtente = StringUtils.getAttributeStrNotNull(request, "P_CDNUTENTE");

			parameters = new ArrayList(8);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgRichiestaAz
			parameters.add(
					conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT, new BigInteger(p_prgRichiestaAz)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_codMonoTipoGrad
			parameters.add(conn.createDataField("p_codMonoTipoGrad", java.sql.Types.VARCHAR, codTipoGrad));
			command.setAsInputParameters(paramIndex++);
			// 4. p_codEvasione
			parameters.add(conn.createDataField("p_codEvasione", java.sql.Types.VARCHAR, codEvasione));
			command.setAsInputParameters(paramIndex++);
			// 5. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.VARCHAR, p_cdnUtente));
			command.setAsInputParameters(paramIndex++);
			// parametri di Output
			// 6. p_errCode
			parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 7. p_out_prgIncrocio
			parameters.add(conn.createDataField("p_out_prgIncrocio", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 8. p_out_prgRosa
			parameters.add(conn.createDataField("p_out_prgRosa", java.sql.Types.VARCHAR, null));
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
			// 1. p_out_prgIncrocio
			pdr = (PunctualDataResult) outputParams.get(2);
			df = pdr.getPunctualDatafield();
			prgIncrocio = (String) df.getStringValue();
			if (prgIncrocio == null) {
				prgIncrocio = "";
			}
			// 2. p_out_prgRosa
			pdr = (PunctualDataResult) outputParams.get(3);
			df = pdr.getPunctualDatafield();
			prgRosa = (String) df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case 1: // non è stata inserita nessuna mansione nella richiesta
					msgCode = MessageCodes.IDO.ERR_CRNG_NO_MANSIONE;
					msg = "Creazione rosa nominativa grezza fallita: manca la mansione nella richiesta";

					break;
				case 2: // la richiesta è chiusa totalmente
					msgCode = MessageCodes.IDO.ERR_CRNG_RICH_CHIUSA_TOT;
					msg = "Creazione rosa nominativa grezza fallita: la richiesta e' chiusa totalmente";

					break;
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Creazione rosa nominativa grezza fallita: sqlCode=" + errCode;

					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Creazione rosa nominativa grezza fallita: errore di ritorno non ammesso. SqlCode=" + errCode;

				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				// response.setAttribute("MATCH_PRGROSA", prgRosa);
				ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}
			response.setAttribute((SourceBean) row);
			return true;
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, className, msg);
			return false;
		} finally {
			Utils.releaseResources(null, command2, dr2);
			Utils.releaseResources(conn, command, dr);
		}

	}
}