package it.eng.sil.module.ido;

import java.math.BigDecimal;
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
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

 

public class ASUpdatePunteggiRichiestaAdesione extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8693844140195113296L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ASUpdatePunteggiRichiestaAdesione.class
			.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws EMFInternalError {

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

			updatePunteggiRichiestaAdesione(request, response);

			transExec.commitTransaction();
			ror.reportSuccess(idSuccess);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "ASUpdatePunteggiRichiestaAdesione::service(): Impossibile aggiornare l'adesione!", ex);

			ror.reportFailure(idFail);
			ror.reportFailure(MessageCodes.General.UPDATE_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}
	}

	public void updatePunteggiRichiestaAdesione(SourceBean request, SourceBean response) {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);
		
		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = dcm.getConnection(pool);

			statementSB = (SourceBean) getConfig().getAttribute("AS_UPDATE_PUNTEGGI_ADESIONE");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);
			
			String p_cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "CDNLAVORATORE");
			String p_prgNominatico = StringUtils.getAttributeStrNotNull(request, "PRGNOMINATIVO");
			String p_codMonoDid = StringUtils.getAttributeStrNotNull(request, "codMonoDid");
			String p_codMonoIsee =  StringUtils.getAttributeStrNotNull(request, "codMonoIsee");
			//TODO Aggiungere lettura parametri aggiuntivi VDA
			BigInteger p_PersoneACarico = null;
			String p_numcaricofam = StringUtils.getAttributeStrNotNull(request, "numCaricoFam");
			if (p_numcaricofam != null && !p_numcaricofam.equals("")){
				p_PersoneACarico = new BigInteger(p_numcaricofam);
			}
			String p_flgprofessionalita =  StringUtils.getAttributeStrNotNull(request, "flgProfessionalita");
			String p_flgfamdec =  StringUtils.getAttributeStrNotNull(request, "flgFamDec");
			BigDecimal cdnUser = (BigDecimal)this.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");

			parameters = new ArrayList(9);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgNominatico
			parameters.add(conn.createDataField("p_prgNominativo", java.sql.Types.BIGINT, new BigInteger(p_prgNominatico)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, cdnUser));
			command.setAsInputParameters(paramIndex++);
			// 4. p_codMonoDid
			parameters.add(conn.createDataField("p_codMonoDid", java.sql.Types.VARCHAR, p_codMonoDid));
			command.setAsInputParameters(paramIndex++);
			// 5. p_codMonoIsee
			parameters.add(conn.createDataField("p_codMonoIsee", java.sql.Types.VARCHAR, p_codMonoIsee));
			command.setAsInputParameters(paramIndex++);
			// 6. p_numcaricofam
			parameters.add(conn.createDataField("p_numcaricofam", java.sql.Types.BIGINT, p_PersoneACarico));
			command.setAsInputParameters(paramIndex++);
			// 7. p_flgprofessionalita
			parameters.add(conn.createDataField("p_flgprofessionalita", java.sql.Types.VARCHAR, p_flgprofessionalita));
			command.setAsInputParameters(paramIndex++);
			// 8. p_flgfamdec
			parameters.add(conn.createDataField("p_flgfamdec", java.sql.Types.VARCHAR, p_flgfamdec));
			command.setAsInputParameters(paramIndex++);
			
			// parametri di Output
			// 9. p_errCode
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
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Update Adesione: sqlCode=" + errCode;
					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Update Adesione: errore di ritorno non ammesso. SqlCode=" + errCode;
				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				if(request.containsAttribute("PROV")){
					ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				}
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
			Utils.releaseResources(conn, command, dr);
		}

	}
}
