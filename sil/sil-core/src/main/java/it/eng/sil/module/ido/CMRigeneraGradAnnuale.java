package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
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
import it.eng.sil.security.User;

public class CMRigeneraGradAnnuale extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CMRigeneraGradAnnuale.class.getName());
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

			int numMesiAnzianita = 0;
			String dataAnzianitaCM = ""; // datdatainizio

			transExec.initTransaction();
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

			// rigenera la graduatoria e i nominativi in graduatoria
			String prgGraduatoria = rigeneraGradAnnuale(request, response, transExec);

			request.delAttribute("PRGGRADUATORIA");
			request.setAttribute("PRGGRADUATORIA", prgGraduatoria);

			/*
			 * // recupero dei nominativi per calcolare i mesi di anzianità SourceBean querySelect1 = (SourceBean)
			 * getConfig().getAttribute("CM_LAV_GRAD_ANNUALE"); SourceBean datiLavoratori = (SourceBean)
			 * transExec.executeQuery(reqCont, resCont, querySelect1, "SELECT"); if (datiLavoratori != null) { Vector
			 * lavoratoriVect = datiLavoratori.getAttributeAsVector("ROW");
			 * 
			 * for (int i = 0; i < lavoratoriVect.size(); i++) { numMesiAnzianita = 0; SourceBean lavoratoriSB =
			 * (SourceBean)lavoratoriVect.get(i); Object cdnLavoratore = lavoratoriSB.getAttribute("CDNLAVORATORE");
			 * Object prgGradNominativo = lavoratoriSB.getAttribute("PRGGRADNOMINATIVO"); String datRiferimento =
			 * (String)lavoratoriSB.getAttribute("DATRIFERIMENTO");
			 * 
			 * request.delAttribute("CDNLAVORATORE"); request.setAttribute("CDNLAVORATORE", cdnLavoratore);
			 * request.delAttribute("PRGGRADNOMINATIVO"); request.setAttribute("PRGGRADNOMINATIVO", prgGradNominativo);
			 * 
			 * // recupero i mesi di anzianità da passare al calcolo del punteggio SourceBean queryIscrCM = (SourceBean)
			 * getConfig().getAttribute("CM_GET_DATI_ISCR_GRAD_ANNUALE"); SourceBean datiCM1 = (SourceBean)
			 * transExec.executeQuery(reqCont, resCont, queryIscrCM, "SELECT"); if (datiCM1 != null) { Vector rowCM1 =
			 * datiCM1.getAttributeAsVector("ROW");
			 * 
			 * if (rowCM1.size() == 1) { SourceBean row1 = (SourceBean) rowCM1.get(0); String codMonoTipoRag = (String)
			 * row1.getAttribute("CODMONOTIPORAGG"); if (("D").equalsIgnoreCase(codMonoTipoRag)) { dataAnzianitaCM =
			 * row1.getAttribute("DATANZIANITA68") == null ? "" : (String) row1 .getAttribute("DATANZIANITA68"); } else
			 * if (("A").equalsIgnoreCase(codMonoTipoRag)) { dataAnzianitaCM = row1.getAttribute("DATANZIANITA68") ==
			 * null ? "" : (String) row1 .getAttribute("DATANZIANITA68"); } else { dataAnzianitaCM = "";
			 * numMesiAnzianita = -1; } } else { dataAnzianitaCM = ""; numMesiAnzianita = -1; }
			 * 
			 * if (numMesiAnzianita >= 0) { // calcolo i mesi di anzianità numMesiAnzianita =
			 * DBLoad.calcolaAnzianitaCM(cdnLavoratore, dataAnzianitaCM, "", 0, datRiferimento);
			 * 
			 * } else { numMesiAnzianita = -1; } }
			 * 
			 * // aggiorno i mesi di anzianità sulla tabella cm_grad_nominativa
			 * request.delAttribute("NUMMESIANZIANITA"); request.setAttribute("NUMMESIANZIANITA", numMesiAnzianita);
			 * 
			 * SourceBean updateMesiAnz = (SourceBean) getConfig().getAttribute("UPDATE_MESI_ANZ_NOMINATIVO"); boolean
			 * esito = (Boolean) transExec.executeQuery(getRequestContainer(),getResponseContainer(), updateMesiAnz,
			 * "UPDATE"); if (!esito) { // aggiornamento mesi anzianità fallito throw new Exception(); }
			 * 
			 * 
			 * } }
			 */
			// calcola il punteggio
			// calcolaPunteggio(request, response, transExec, prgGraduatoria);

			// calcolo posizione nominativi in gradutaoria
			calcolaPosizione(request, response, transExec, prgGraduatoria);

			reqCont.setServiceRequest(request);
			RequestContainer.setRequestContainer(reqCont);

			transExec.commitTransaction();
			ror.reportSuccess(idSuccess);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CMRigeneraGradAnnuale::service(): Impossibile rigenerare la graduatoria!", ex);

			// ror.reportFailure(idFail);
			// ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}
	}

	private String rigeneraGradAnnuale(SourceBean request, SourceBean response, TransactionQueryExecutor txExec)
			throws Exception {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		String p_prgGraduatoria = "";

		SourceBean statementSB = null;
		String statement = "";
		String sqlStr = "";
		String codiceRit = "";

		// parametri
		String old_prgGraduatoria = StringUtils.getAttributeStrNotNull(request, "PRGGRADUATORIA");
		String p_cdnUtente = String.valueOf(user.getCodut());

		int paramIndex = 0;
		ArrayList parameters = null;
		conn = txExec.getDataConnection();

		statementSB = (SourceBean) getConfig().getAttribute("CM_RIGENERA_GRAD_ANNUALE");
		statement = statementSB.getAttribute("STATEMENT").toString();
		sqlStr = SQLStatements.getStatement(statement);
		command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		String encryptKey = (String) getRequestContainer().getSessionContainer().getAttribute("_ENCRYPTER_KEY_");

		parameters = new ArrayList(5);
		// 1.Parametro di Ritorno
		parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);
		// 2 prgGraduatoria OLD
		parameters
				.add(conn.createDataField("prgGraduatoria", java.sql.Types.BIGINT, new BigInteger(old_prgGraduatoria)));
		command.setAsInputParameters(paramIndex++);
		// 3 chiave crypt
		parameters.add(conn.createDataField("p_key", java.sql.Types.VARCHAR, encryptKey));
		command.setAsInputParameters(paramIndex++);
		// 4 cdnutente
		parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
		command.setAsInputParameters(paramIndex++);
		// 5 prg nuova graduatoria
		parameters.add(conn.createDataField("out_p_prggraduatoria", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);

		// Chiamata alla Stored Procedure
		dr = command.execute(parameters);

		CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
		List outputParams = cdr.getContainedDataResult();
		// 0. Codice di Ritorno
		PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
		DataField df = pdr.getPunctualDatafield();
		codiceRit = df.getStringValue();
		// 1. ErrCodeOut
		pdr = (PunctualDataResult) outputParams.get(1);
		df = pdr.getPunctualDatafield();
		p_prgGraduatoria = df.getStringValue();

		SourceBean row = new SourceBean("ROW");
		row.setAttribute("CodiceRit", codiceRit);
		// Predispongo la Response
		if (!codiceRit.equals("0")) {
			int msgCode = 0;
			String msg = null;
			switch (Integer.parseInt(codiceRit)) {
			case -1: // errore generico sql
				msgCode = MessageCodes.General.OPERATION_FAIL;
				msg = "Insert graduatoria annuale";

				break;
			default:
				msgCode = MessageCodes.General.OPERATION_FAIL;
				msg = "Insert graduatoria annuale: errore di ritorno non ammesso.";

			}
			response.setAttribute("error", "true");
			response.setAttribute((SourceBean) row);
			ror.reportFailure(msgCode);
			_logger.debug(msg);

		} else {
			response.setAttribute("PRGGRADUATORIA", p_prgGraduatoria);
			// ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		}

		return p_prgGraduatoria;
	}

	/*
	 * private void calcolaPunteggio(SourceBean request, SourceBean response, TransactionQueryExecutor txExec, String
	 * prgGraduatoria) throws Exception {
	 * 
	 * User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@"); DataConnection conn =
	 * null; DataResult dr = null; StoredProcedureCommand command = null; ReportOperationResult ror = new
	 * ReportOperationResult(this, response);
	 * 
	 * SourceBean statementSB = null; String statement = ""; String sqlStr = ""; String codiceRit = ""; String errCode =
	 * ""; int paramIndex = 0; ArrayList parameters = null; conn = txExec.getDataConnection(); SourceBean row = new
	 * SourceBean("ROW");
	 * 
	 * statementSB = (SourceBean) getConfig().getAttribute("CM_CALC_PUNTEGGIO_ANNUALE"); statement =
	 * statementSB.getAttribute("STATEMENT").toString(); sqlStr = SQLStatements.getStatement(statement); command =
	 * (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);
	 * 
	 * String p_cdnUtente = String.valueOf(user.getCodut()); String encryptKey = (String)
	 * getRequestContainer().getSessionContainer().getAttribute("_ENCRYPTER_KEY_"); String p_flgRigenera = "S";
	 * 
	 * parameters = new ArrayList(6); // 1.Parametro di Ritorno parameters.add(conn.createDataField("codiceRit",
	 * java.sql.Types.VARCHAR, null)); command.setAsOutputParameters(paramIndex++); // 2. prgGraduatoria
	 * parameters.add(conn.createDataField("prgGradNominativo", java.sql.Types.BIGINT, new BigInteger(prgGraduatoria)));
	 * command.setAsInputParameters(paramIndex++); // 3 .encrypterkey parameters.add(conn.createDataField("p_key",
	 * java.sql.Types.VARCHAR, encryptKey)); command.setAsInputParameters(paramIndex++); // 4 flag graduatoria da
	 * rigenerare parameters.add(conn.createDataField("p_flgRigenera", java.sql.Types.VARCHAR, p_flgRigenera));
	 * command.setAsInputParameters(paramIndex++); // 5. p_cdnUtente parameters.add(conn.createDataField("p_cdnUtente",
	 * java.sql.Types.BIGINT, new BigInteger(p_cdnUtente))); command.setAsInputParameters(paramIndex++); // 6. p_errCode
	 * parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
	 * command.setAsOutputParameters(paramIndex++);
	 * 
	 * // Chiamata alla Stored Procedure dr = command.execute(parameters); CompositeDataResult cdr =
	 * (CompositeDataResult) dr.getDataObject(); List outputParams = cdr.getContainedDataResult(); // Reperisco i valori
	 * di output della stored // 0. Codice di Ritorno PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
	 * DataField df = pdr.getPunctualDatafield(); codiceRit = df.getStringValue(); // 1. ErrCodeOut pdr =
	 * (PunctualDataResult) outputParams.get(1); df = pdr.getPunctualDatafield(); errCode = df.getStringValue();
	 * 
	 * row.setAttribute("CodiceRit", codiceRit);
	 * 
	 * // Predispongo la Response if (!codiceRit.equals("0")) { int msgCode = 0; String msg = null; switch
	 * (Integer.parseInt(codiceRit)) { case -1: // errore generico sql msgCode = MessageCodes.General.OPERATION_FAIL;
	 * msg = "Calcola Punteggio: sqlCode=" + errCode;
	 * 
	 * break; default: msgCode = MessageCodes.General.OPERATION_FAIL; msg =
	 * "Calcola Punteggio: errore di ritorno non ammesso. SqlCode=" + errCode;
	 * 
	 * } response.setAttribute("error", "true"); response.setAttribute((SourceBean) row); ror.reportFailure(msgCode);
	 * _logger.debug(msg);
	 * 
	 * } }
	 */
	private void calcolaPosizione(SourceBean request, SourceBean response, TransactionQueryExecutor txExec,
			String prgGraduatoria) throws Exception {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		SourceBean statementSB = null;
		String statement = "";
		String sqlStr = "";
		String codiceRit = "";
		String errCode = "";
		int paramIndex = 0;
		ArrayList parameters = null;
		conn = txExec.getDataConnection();

		statementSB = (SourceBean) getConfig().getAttribute("CM_CALC_POSIZIONE_ANNUALE");
		statement = statementSB.getAttribute("STATEMENT").toString();
		sqlStr = SQLStatements.getStatement(statement);
		command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		String encryptKey = (String) getRequestContainer().getSessionContainer().getAttribute("_ENCRYPTER_KEY_");

		parameters = new ArrayList(3);
		// 1.Parametro di Ritorno
		parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);
		// 2. p_prgRichiestaAz
		parameters.add(conn.createDataField("p_prgGraduatoria", java.sql.Types.BIGINT, new BigInteger(prgGraduatoria)));
		command.setAsInputParameters(paramIndex++);
		// 4. p_key
		parameters.add(conn.createDataField("p_key", java.sql.Types.VARCHAR, encryptKey));
		command.setAsInputParameters(paramIndex++);

		// Chiamata alla Stored Procedure
		dr = command.execute(parameters);

		PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
		DataField df = pdr.getPunctualDatafield();
		codiceRit = df.getStringValue();

		SourceBean row = new SourceBean("ROW");
		row.setAttribute("CodiceRit", codiceRit);
		// Predispongo la Response
		if (!codiceRit.equals("0")) {
			int msgCode = 0;
			String msg = null;
			switch (Integer.parseInt(codiceRit)) {
			case -1: // errore generico sql
				msgCode = MessageCodes.General.OPERATION_FAIL;
				msg = "Calcola POSIZIONE: sqlCode=" + errCode;

				break;
			default:
				msgCode = MessageCodes.General.OPERATION_FAIL;
				msg = "Calcola POSIZIONE: errore di ritorno non ammesso. SqlCode=" + errCode;

			}
			response.setAttribute("error", "true");
			response.setAttribute((SourceBean) row);
			ror.reportFailure(msgCode);
			_logger.debug(msg);

		}
	}
}