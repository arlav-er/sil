package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;

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
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

/*
 * 
 * @author coticone
 *      
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ASSalvaApprovaGrad extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ASSalvaApprovaGrad.class.getName());

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

			salvaApprovazione(request, response);

			transExec.commitTransaction();
			ror.reportSuccess(idSuccess);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"ASSalvaApprovaGrad::service(): Impossibile salvare approvazione graduatoria!", ex);

			ror.reportFailure(idFail);
			ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}
	}

	public void salvaApprovazione(SourceBean request, SourceBean response) {

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
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = dcm.getConnection(pool);

			statementSB = (SourceBean) getConfig().getAttribute("AS_SALVA_APPROVAZ");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			String p_prgTipoIncrocio = StringUtils.getAttributeStrNotNull(request, "PRGTIPOINCROCIO");
			String p_prgapprovazionegrad = StringUtils.getAttributeStrNotNull(request, "PRGAPPROVAZIONEGRAD");
			if (p_prgapprovazionegrad == null || ("").equals(p_prgapprovazionegrad)) {
				p_prgapprovazionegrad = "0";
			}
			String p_numDetermina = StringUtils.getAttributeStrNotNull(request, "NUMDETERMINA");
			String p_datProtocollazione = StringUtils.getAttributeStrNotNull(request, "DATPROTOCOLLAZIONE");
			String p_datPubblicazione = StringUtils.getAttributeStrNotNull(request, "DATPUBBLICAZIONE");
			String p_cdnUtente = String.valueOf(user.getCodut());

			parameters = new ArrayList(8);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 2. p_prgRichiestaAz
			parameters.add(
					conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT, new BigInteger(p_prgRichiestaAz)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_prgRichiestaAz
			parameters.add(conn.createDataField("p_prgTipoIncrocio", java.sql.Types.BIGINT,
					new BigInteger(p_prgTipoIncrocio)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_prgapprovazionegrad
			parameters.add(conn.createDataField("p_prgapprovazionegrad", java.sql.Types.BIGINT,
					new BigInteger(p_prgapprovazionegrad)));
			command.setAsInputParameters(paramIndex++);
			// 5. p_numDetermina
			parameters.add(conn.createDataField("p_numDetermina", java.sql.Types.VARCHAR, p_numDetermina));
			command.setAsInputParameters(paramIndex++);
			// 6. p_datProtocollo
			parameters.add(conn.createDataField("p_datProtocollazione", java.sql.Types.VARCHAR, p_datProtocollazione));
			command.setAsInputParameters(paramIndex++);
			// 7. p_datPubblicazione
			parameters.add(conn.createDataField("p_datPubblicazione", java.sql.Types.VARCHAR, p_datPubblicazione));
			command.setAsInputParameters(paramIndex++);
			// 8. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
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
					msg = "Salva approvaz grad: sqlCode=" + errCode;
					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Salva approvaz grad: errore di ritorno non ammesso. SqlCode=" + errCode;
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