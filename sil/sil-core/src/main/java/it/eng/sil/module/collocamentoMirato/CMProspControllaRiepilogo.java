package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class CMProspControllaRiepilogo extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CMProspControllaRiepilogo.class.getName());

	private String className = this.getClass().getName();

	/**
	 * Dopo aver chiamato il metodo per ricalcolare i dati esegue la verifica dei dati prioma di storicizzare il
	 * prospetto
	 * 
	 */
	public void service(SourceBean request, SourceBean response) {
		ReportOperationResult ror = new ReportOperationResult(this, response);
		boolean checkCalcolo = false;
		boolean checkControllo = false;
		boolean ret = false;
		disableMessageIdSuccess();

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			// eseguo la query per aggiornare i dati del prosp
			this.setSectionQueryUpdate("UPDATE_RIEPILOGO");
			ret = doUpdate(request, response);
			if (ret) {
				checkCalcolo = getRicalcoloRiepilogo(transExec, request, response);

				if (checkCalcolo) {
					checkControllo = getControlloRiepilogo(transExec, request, response);

					if (checkControllo) {
						transExec.commitTransaction();
					} else {
						throw new Exception(
								"Errore durante il controllo del riepilogo prospetto. Operazione interrotta");
					}
				} else {
					throw new Exception("Errore durante il ricalcolo del riepilogo prospetto. Operazione interrotta");
				}
			} else {
				throw new Exception("Errore durante l'aggiornamento del riepilogo prospetto. Operazione interrotta");
			}

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CMProspControllaRiepilogo::service(): Impossibile calcolare il riepilogo prospetto!", ex);

			ror.reportFailure(idFail);
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL);
			if (transExec != null) {
				try {
					transExec.rollBackTransaction();
				} catch (EMFInternalError e) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"CMProspControllaRiepilogo::service(): Impossibile calcolare il riepilogo prospetto!", ex);

				}
			}
			errors = true;
		}
	}

	/**
	 * Esegue il ricalcolo dei dati del riepilogo, utilizzata per calcolare la scopertura La procedura oltre a
	 * calcolarli ad una certa data (data riferimento se è presente oppure alla data odierna) esegue l'aggiornamento
	 * della tabella CM_PROSPETTO_INF
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public boolean getRicalcoloRiepilogo(TransactionQueryExecutor txExecutor, SourceBean request, SourceBean response) {
		ArrayList retList = null;
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);
		String codiceRit = "-1";
		boolean check = false;
		try {
			String pool = (String) getConfig().getAttribute("POOL");
			conn = txExecutor.getDataConnection();

			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			SQLCommand stmt = null;
			DataResult res = null;
			ScrollableDataResult sdr = null;
			SourceBean rowsSourceBean = null;
			String p_prgProspettoInf = "";
			String p_flgConvenzione = "S";

			Object prgProspettoInf = request.getAttribute("PRGPROSPETTOINF");
			if (prgProspettoInf == null)
				throw new Exception();
			if (prgProspettoInf instanceof BigDecimal) {
				p_prgProspettoInf = ((BigDecimal) prgProspettoInf).toString();
			} else if (prgProspettoInf instanceof String) {
				p_prgProspettoInf = (String) prgProspettoInf;
			}

			statementSB = (SourceBean) getConfig().getAttribute("RICALCOLO_PROSPETTO");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);
			parameters = new ArrayList(3);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("risultato", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgProspettoInf
			parameters.add(conn.createDataField("p_prgProspettoInf", java.sql.Types.BIGINT,
					new BigInteger(p_prgProspettoInf)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_flagconvenzione
			parameters.add(conn.createDataField("p_flgConvenzione", java.sql.Types.VARCHAR, p_flgConvenzione));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			PunctualDataResult cdr = (PunctualDataResult) dr.getDataObject();
			DataField df = cdr.getPunctualDatafield();
			// Reperisco i valori di output della stored
			// 0. Codice di Ritorno
			codiceRit = df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);

			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				check = false;
			} else {
				check = true;
			}
		} catch (Exception e) {
			String msg = "Errore nel ricalcolo del prospetto ";
			ror.reportFailure(e, className, msg);
			check = false;
		}
		return check;
	}

	/**
	 * Esegue il controllo dei dati del riepilogo, utilizzata per la verifica della storicizzazione
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public boolean getControlloRiepilogo(TransactionQueryExecutor txExecutor, SourceBean request, SourceBean response) {
		ArrayList retList = null;
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);
		String codiceRit = "-1";
		boolean check = true;
		try {
			String pool = (String) getConfig().getAttribute("POOL");
			conn = txExecutor.getDataConnection();

			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			SQLCommand stmt = null;
			DataResult res = null;
			ScrollableDataResult sdr = null;
			SourceBean rowsSourceBean = null;
			String p_prgProspettoInf = "";
			String p_flgConvenzione = "S";

			Object prgProspettoInf = request.getAttribute("PRGPROSPETTOINF");
			if (prgProspettoInf == null)
				throw new Exception();
			if (prgProspettoInf instanceof BigDecimal) {
				p_prgProspettoInf = ((BigDecimal) prgProspettoInf).toString();
			} else if (prgProspettoInf instanceof String) {
				p_prgProspettoInf = (String) prgProspettoInf;
			}

			statementSB = (SourceBean) getConfig().getAttribute("CONTROLLA_PROSPETTO");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);
			parameters = new ArrayList(15);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("risultato", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgProspettoInf
			parameters.add(conn.createDataField("p_prgProspettoInf", java.sql.Types.BIGINT,
					new BigInteger(p_prgProspettoInf)));
			command.setAsInputParameters(paramIndex++);

			// preparazione dei Parametri di OUTPUT per i due warning
			// 3
			parameters.add(conn.createDataField("err_percEsonero", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 4
			parameters.add(conn.createDataField("err_percCompensazione", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 5
			parameters.add(conn.createDataField("err_numCompInProv", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 6
			parameters.add(conn.createDataField("err_numCompArt18", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 7
			parameters.add(conn.createDataField("err_numCompRiduz", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 8
			parameters.add(conn.createDataField("err_numCompensEcc", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 9
			parameters.add(conn.createDataField("err_dataConsegnaProspetto", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 10
			parameters.add(conn.createDataField("err_dataProspetto", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 11
			parameters.add(conn.createDataField("err_dataRiferimento", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 12
			parameters.add(conn.createDataField("err_checkProspStoriciz", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 13. checkScopertura
			parameters.add(conn.createDataField("checkScopertura", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 14. checkExistForza
			parameters.add(conn.createDataField("checkExistForza", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);
			// 15. checkSospensione
			parameters.add(conn.createDataField("checkSospensione", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();

			// 1 err_percEsonero
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			String err_percEsonero = df.getStringValue();
			// 2 err_percCompensazione
			pdr = (PunctualDataResult) outputParams.get(2);
			df = pdr.getPunctualDatafield();
			String err_percCompensazione = df.getStringValue();
			// 3 err_numCompInProv
			pdr = (PunctualDataResult) outputParams.get(3);
			df = pdr.getPunctualDatafield();
			String err_numCompInProv = df.getStringValue();
			// 4 err_numCompArt18
			pdr = (PunctualDataResult) outputParams.get(4);
			df = pdr.getPunctualDatafield();
			String err_numCompArt18 = df.getStringValue();
			// 5 err_numCompRiduz
			pdr = (PunctualDataResult) outputParams.get(5);
			df = pdr.getPunctualDatafield();
			String err_numCompRiduz = df.getStringValue();
			// 6 err_numCompensEcc
			pdr = (PunctualDataResult) outputParams.get(6);
			df = pdr.getPunctualDatafield();
			String err_numCompensEcc = df.getStringValue();
			// 7 err_dataConsegnaProspetto
			pdr = (PunctualDataResult) outputParams.get(7);
			df = pdr.getPunctualDatafield();
			String err_dataConsegnaProspetto = df.getStringValue();
			// 8 err_dataProspetto
			pdr = (PunctualDataResult) outputParams.get(8);
			df = pdr.getPunctualDatafield();
			String err_dataProspetto = df.getStringValue();
			// 9 err_dataRiferimento
			pdr = (PunctualDataResult) outputParams.get(9);
			df = pdr.getPunctualDatafield();
			String err_dataRiferimento = df.getStringValue();
			// 10 err_checkProspStoriciz
			pdr = (PunctualDataResult) outputParams.get(10);
			df = pdr.getPunctualDatafield();
			String err_checkProspStoriciz = df.getStringValue();
			// 11. checkScopertura
			pdr = (PunctualDataResult) outputParams.get(11);
			df = pdr.getPunctualDatafield();
			String checkScopertura = df.getStringValue();
			// 12. checkExistForza
			pdr = (PunctualDataResult) outputParams.get(12);
			df = pdr.getPunctualDatafield();
			String checkExistForza = df.getStringValue();
			// 13. checkSospensione
			pdr = (PunctualDataResult) outputParams.get(13);
			df = pdr.getPunctualDatafield();
			String checkSospensione = df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			row.setAttribute("err_percEsonero", err_percEsonero == null ? "0" : err_percEsonero);
			row.setAttribute("err_percCompensazione", err_percCompensazione == null ? "0" : err_percCompensazione);
			row.setAttribute("err_numCompInProv", err_numCompInProv == null ? "0" : err_numCompInProv);
			row.setAttribute("err_numCompArt18", err_numCompArt18 == null ? "0" : err_numCompArt18);
			row.setAttribute("err_numCompRiduz", err_numCompRiduz == null ? "0" : err_numCompRiduz);
			row.setAttribute("err_numCompensEcc", err_numCompensEcc == null ? "0" : err_numCompensEcc);
			row.setAttribute("err_dataConsegnaProspetto",
					err_dataConsegnaProspetto == null ? "0" : err_dataConsegnaProspetto);
			row.setAttribute("err_dataProspetto", err_dataProspetto == null ? "0" : err_dataProspetto);
			row.setAttribute("err_dataRiferimento", err_dataRiferimento == null ? "0" : err_dataRiferimento);
			row.setAttribute("err_checkProspStoriciz", err_checkProspStoriciz == null ? "0" : err_checkProspStoriciz);
			row.setAttribute("CHECKSCOPERTURA", checkScopertura == null ? "0" : checkScopertura);
			row.setAttribute("CHECKEXISTFORZA", checkExistForza == null ? "0" : checkExistForza);
			row.setAttribute("CHECKSOSPENSIONE", checkSospensione == null ? "0" : checkSospensione);
			SourceBean rows = new SourceBean("ROWS");
			rows.setAttribute(row);
			/// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0, debugLevel = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Storicizza prospetto: errore generico";
					// debugLevel = TracerSingleton.CRITICAL;
					break;
				case 1:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Storicizza prospetto: errore generico";
					// debugLevel = TracerSingleton.CRITICAL;
					break;
				/*
				 * case 2: msgCode = MessageCodes.CollocamentoMirato.ERROR_NO_PERC_ESONERO; msg =
				 * "Storicizza prospetto: mancanza percentuale esonero"; debugLevel = TracerSingleton.CRITICAL; break;
				 * case 3: msgCode = MessageCodes.CollocamentoMirato.ERROR_NO_COMPENSAZIONE_TERRITORIALE; msg =
				 * "Storicizza prospetto: mancanza di compensazione territoriale"; debugLevel =
				 * TracerSingleton.CRITICAL; break; case 4: msgCode =
				 * MessageCodes.CollocamentoMirato.ERROR_DATA_CONSEGNA_OBBLIGATORIA; msg =
				 * "Storicizza prospetto: data consegna prospetto assente"; debugLevel = TracerSingleton.CRITICAL;
				 * break; case 5: msgCode = MessageCodes.CollocamentoMirato.ERROR_DATA_RIFERIMENTO_OBBLIGATORIA; msg =
				 * "Storicizza prospetto: data consegna prospetto assente"; debugLevel = TracerSingleton.CRITICAL;
				 * break; case 6: msgCode = MessageCodes.CollocamentoMirato.ERROR_DATA_PROSPETTO_OBBLIGATORIA; msg =
				 * "Storicizza prospetto: data consegna prospetto assente"; debugLevel = TracerSingleton.CRITICAL;
				 * break; case 7: msgCode = MessageCodes.CollocamentoMirato.ERROR_PROSPETTO_STORICIZ_DUPLICATO; msg =
				 * "Storicizza prospetto: prospetto storicizzato esistente"; debugLevel = TracerSingleton.CRITICAL;
				 * break; case 8: msgCode = MessageCodes.CollocamentoMirato.ERROR_COERENZA_CATEGORIA_COMP_TERR; msg =
				 * "Controllo prospetto: errore coerenza numero compensazioni e categoria prospetto"; debugLevel =
				 * TracerSingleton.CRITICAL; break; case 9: msgCode =
				 * MessageCodes.CollocamentoMirato.ERROR_COERENZA_CATEGORIA_COMP_TERR_ART18; msg =
				 * "Controllo prospetto: errore coerenza art18 e categoria prospetto"; debugLevel =
				 * TracerSingleton.CRITICAL; break;
				 */
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Controllo prospetto: errore di ritorno non ammesso.";
					// debugLevel = TracerSingleton.CRITICAL;
				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			}

			response.setAttribute((SourceBean) rows);
		} catch (Exception e) {
			String msg = "Errore nel storicizzazione prospetto ";
			ror.reportFailure(e, className, msg);
			check = false;
		}
		return check;
	}

}