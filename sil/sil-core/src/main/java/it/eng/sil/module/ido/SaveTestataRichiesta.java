package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
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

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.QueryStrategyException;
import it.eng.sil.security.User;
import it.eng.sil.util.Sottosistema;

public class SaveTestataRichiesta extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SaveTestataRichiesta.class.getName());

	private String className = this.getClass().getName();
	private int messageIdFail = MessageCodes.General.OPERATION_FAIL;
	private static final String TRUE = "TRUE";

	public void service(SourceBean request, SourceBean response) throws EMFInternalError {

		// INIT-PARTE-TEMP
		if (Sottosistema.CM.isOff()) {
			// END-PARTE-TEMP
			doUpdateAS(request, response);
			// INIT-PARTE-TEMP
		} else {
			// END-PARTE-TEMP
			doUpdateASCM(request, response);
			// INIT-PARTE-TEMP
		}
		// procedura per art 16 on line
		controllaUpdateAsOnline(request, response);
		// END-PARTE-TEMP

	}

	/**
	 * permette l'aggiornamento dei dati della richiesta
	 * 
	 * @param request
	 * @param response
	 */
	private void doUpdateASCM(SourceBean request, SourceBean response) throws EMFInternalError {

		ReportOperationResult ror = new ReportOperationResult(this, response);

		// checknum = 1 -> il numero posti era valorizzato
		// checknum = 0 -> il numero posti non era valorizzato
		String checkNumAs = StringUtils.getAttributeStrNotNull(request, "checkNumAs");
		String checkNumLsu = StringUtils.getAttributeStrNotNull(request, "checkNumLsu");
		String checkNumMilitare = StringUtils.getAttributeStrNotNull(request, "checkNumMilitare");

		String numAs = StringUtils.getAttributeStrNotNull(request, "numPostoAS");
		String numLsu = StringUtils.getAttributeStrNotNull(request, "numPostoLSU");
		String numMilitare = StringUtils.getAttributeStrNotNull(request, "numPostoMilitare");

		if (("0").equalsIgnoreCase(numLsu) || ("").equalsIgnoreCase(numLsu) || numLsu == null) {
			try {
				request.updAttribute("tipoLSU", "");
			} catch (SourceBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Collocamento Mirato
		String codMonoTipoGrad = StringUtils.getAttributeStrNotNull(request, "codMonoTipoGrad");
		String checkNumPostiCM = StringUtils.getAttributeStrNotNull(request, "checkNumPostiCM");
		String numPostiCM = StringUtils.getAttributeStrNotNull(request, "numPostiCM");

		int messageIdSuccess = this.getMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		boolean success = true;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			// eseguo il controllo per il tiporosa (se ci sono adesioni per quel
			// tipo)
			// solo se l'utente ha modificato a 0 il numero posti (AS, LSU,
			// Militari)
			/*
			 * possibili valori per tipo incrocio sono : 5 Art.16 L.56/87 6 Riserve ex militari 7 Riserve ex LSU 8
			 * Riserve in mobilità (eliminato in questa fase)
			 */
			if (("1").equalsIgnoreCase(checkNumAs) && (("").equalsIgnoreCase(numAs) || ("0").equalsIgnoreCase(numAs))) {
				success = checkTipoRosaAS(request, response, "5");
			}

			if (success) {
				if (("1").equalsIgnoreCase(checkNumMilitare)
						&& (("").equalsIgnoreCase(numMilitare) || ("0").equalsIgnoreCase(numMilitare))) {
					success = checkTipoRosaAS(request, response, "6");
				}
			}

			if (success) {
				if (("1").equalsIgnoreCase(checkNumLsu)
						&& (("").equalsIgnoreCase(numLsu) || ("0").equalsIgnoreCase(numLsu))) {
					success = checkTipoRosaAS(request, response, "7");
				}
			}

			// collocamento mirato
			if (success) {
				if (("1").equalsIgnoreCase(checkNumPostiCM)) {
					success = checkTipoRosaAS(request, response, "10");
					if (!success) {
						throw new Exception("aggiornamento richiesta fallito. Operazione interrotta");
					}
					success = checkTipoRosaAS(request, response, "11");
					if (!success) {
						throw new Exception("aggiornamento richiesta fallito. Operazione interrotta");
					}
					success = checkTipoRosaAS(request, response, "12");
					if (!success) {
						throw new Exception("aggiornamento richiesta fallito. Operazione interrotta");
					}
				}
			}

			if (success) {
				this.setSectionQueryUpdate("QUERY_CM");
				boolean esito = doUpdate(request, response);
			}

			transExec.commitTransaction();
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Aggiorna richiesta::service(): Impossibile aggiornare la richiesta!", ex);

			// ror.reportFailure(idFail);
			// ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}

	}

	/**
	 * permette l'aggiornamento dei dati della richiesta anche con i campi riguardanti l'Art.16
	 * 
	 * @param request
	 * @param response
	 */
	private void doUpdateAS(SourceBean request, SourceBean response) throws EMFInternalError {

		ReportOperationResult ror = new ReportOperationResult(this, response);

		// checknum = 1 -> il numero posti era valorizzato
		// checknum = 0 -> il numero posti non era valorizzato
		String checkNumAs = StringUtils.getAttributeStrNotNull(request, "checkNumAs");
		String checkNumLsu = StringUtils.getAttributeStrNotNull(request, "checkNumLsu");
		String checkNumMilitare = StringUtils.getAttributeStrNotNull(request, "checkNumMilitare");

		String numAs = StringUtils.getAttributeStrNotNull(request, "numPostoAS");
		String numLsu = StringUtils.getAttributeStrNotNull(request, "numPostoLSU");
		String numMilitare = StringUtils.getAttributeStrNotNull(request, "numPostoMilitare");

		if (("0").equalsIgnoreCase(numLsu) || ("").equalsIgnoreCase(numLsu) || numLsu == null) {
			try {
				request.updAttribute("tipoLSU", "");
			} catch (SourceBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		int messageIdSuccess = this.getMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		boolean success = true;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			// eseguo il controllo per il tiporosa (se ci sono adesioni per quel
			// tipo)
			// solo se l'utente ha modificato a 0 il numero posti (AS, LSU,
			// Militari)
			/*
			 * possibili valori per tipo incrocio sono : 5 Art.16 L.56/87 6 Riserve ex militari 7 Riserve ex LSU 8
			 * Riserve in mobilità (eliminato in questa fase)
			 */
			if (("1").equalsIgnoreCase(checkNumAs) && (("").equalsIgnoreCase(numAs) || ("0").equalsIgnoreCase(numAs))) {
				success = checkTipoRosaAS(request, response, "5");
			}

			if (success) {
				if (("1").equalsIgnoreCase(checkNumMilitare)
						&& (("").equalsIgnoreCase(numMilitare) || ("0").equalsIgnoreCase(numMilitare))) {
					success = checkTipoRosaAS(request, response, "6");
				}
			}

			if (success) {
				if (("1").equalsIgnoreCase(checkNumLsu)
						&& (("").equalsIgnoreCase(numLsu) || ("0").equalsIgnoreCase(numLsu))) {
					success = checkTipoRosaAS(request, response, "7");
				}
			}

			if (success) {
				this.setSectionQueryUpdate("QUERY_AS");
				boolean esito = doUpdate(request, response);
			}

			transExec.commitTransaction();
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"ASAssociaRichiesteAdesione::service(): Impossibile inserire l'adesione!", ex);

			// ror.reportFailure(idFail);
			// ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}

	}

	public boolean checkTipoRosaAS(SourceBean request, SourceBean response, String tipoIncrocio) {

		boolean checkSuccess = false;
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

			statementSB = (SourceBean) getConfig().getAttribute("AS_CHECK_TIPO_ROSA");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "prgRichiestaAZ");

			parameters = new ArrayList(4);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgRichiestaAz
			parameters.add(
					conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT, new BigInteger(p_prgRichiestaAz)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_prgTipoIcrocio
			parameters
					.add(conn.createDataField("p_prgTipoIcrocio", java.sql.Types.BIGINT, new BigInteger(tipoIncrocio)));
			command.setAsInputParameters(paramIndex++);

			// parametri di Output
			// 4. p_errCode
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

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case 1: // errore: Presenza di adesioni
					msgCode = MessageCodes.IDO.ERR_UPD_RICH_ADESIONI;
					msg = "SaveTestataRichiesta - check tipo rosa: errore ";

					break;
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "SaveTestataRichiestan - check tipo rosa: sqlCode=" + errCode;

					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "SaveTestataRichiesta - check tipo rosa: errore di ritorno non ammesso. SqlCode=" + errCode;

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
		} finally {
			Utils.releaseResources(null, command2, dr2);
			Utils.releaseResources(conn, command, dr);
		}

		return checkSuccess;
	}

	/**
	 * permette l'aggiornamento dei campi per retrocompatibilità senza l'aggiunta dei campi per l'Art.16
	 * 
	 * @param request
	 * @param response
	 */
	private boolean doUpdateOld(SourceBean request, SourceBean response) {
		ReportOperationResult ror = new ReportOperationResult(this, response);

		SourceBean statement = (SourceBean) getConfig().getAttribute("SAVE_TESTATA_RICHIESTA_OLD");

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		Boolean esito = new Boolean(false);

		try {
			esito = (Boolean) queryStrategy.executeQuery(statement, "UPDATE");
		} catch (QueryStrategyException ex) {
			reportOperation.reportFailure(this.messageIdFail, ex, "doUpdate", "queryStrategy.executeQuery failed");

			return false;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("request [");
		if (request != null)
			sb.append(request.toXML(false));
		sb.append("] esito [");
		sb.append(esito);
		sb.append("]");

		if ((esito != null) && !esito.booleanValue()) {
			sb.append(" se non c'è un errore precedente a questo log,");
			sb.append("il problema potrebbe essere che il record da aggiornare");
			sb.append("non è stato trovato, vedere i valori dei parametri usati");
			sb.append("per la query di UPDATE.");
		}

		LogUtils.logDebug("doUpdate", sb.toString(), this);

		if ((esito == null) || !esito.booleanValue()) {
			reportOperation.reportFailure(this.messageIdFail);

			return false;
		}

		try {
			response.setAttribute(UPDATE_OK, TRUE);
		} catch (SourceBeanException ex) {
			reportOperation.reportFailure(this.messageIdFail, ex, "doUpdate", "Cannot assign UPDATE_OK in response");
		}

		reportSuccess(reportOperation);

		return true;

	}

	public boolean controllaUpdateAsOnline(SourceBean request, SourceBean response) {
		DataConnection conn = null;
		DataResult dr = null;

		StoredProcedureCommand command = null;

		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			SessionContainer session = RequestContainer.getRequestContainer().getSessionContainer();
			User user = (User) session.getAttribute(User.USERID);

			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();

			ArrayList parameters = null;
			conn = dcm.getConnection(pool);

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ?:= PG_INCROCIO.updateAsOnlineRosa(?, ?, ?, ?) }");

			String codiceRit = "";
			String errCode = "";
			String p_prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			String p_cdnUtente = String.valueOf(user.getCodut());

			parameters = new ArrayList(5);

			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(0);
			// preparazione dei Parametri di Input
			// 2. p_prgRichiestaAz
			parameters.add(conn.createDataField("prgParDoRichiestaAz", java.sql.Types.BIGINT,
					new BigInteger(p_prgRichiestaAz)));
			command.setAsInputParameters(1);
			// 3 flagAsOnline
			parameters.add(conn.createDataField("flagParAsOnline", java.sql.Types.VARCHAR,
					request.getAttribute("FLGASONLINE")));
			command.setAsInputParameters(2);
			// 3. p_cdnUtente
			parameters.add(conn.createDataField("cdnParUtMod", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
			command.setAsInputParameters(3);
			// parametri di Output
			// 4. p_errCode
			parameters.add(conn.createDataField("p_errCode", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(4);

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

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case 1: // errore: codEvasione
					msgCode = MessageCodes.IDO.ERR_ASONLINE_NO_AS_O_NULL;
					msg = "SaveTestataRichiesta - non è possibile modificare i dati relativi ad asta articolo 16, codEvasione deve essere null o AS";

					break;
				case 2: // errore: Presenza di candidati in graduatoria
					msgCode = MessageCodes.IDO.ERR_ASONLINE_CANDIDATI;
					msg = "SaveTestataRichiesta - non è possibile modificare i dati relativi ad asta articolo 16, la graduatoria ha candidati";

					break;
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "SaveTestataRichiestan - aggiornamento asonline: sqlCode=" + errCode;

					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "SaveTestataRichiesta - aggiornamento asonline: errore di ritorno non ammesso. SqlCode="
							+ errCode;

				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				return true;
			}

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

			Utils.releaseResources(conn, command, dr);
		}

	}

}