package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class ASEscludiDaRosa extends AbstractSimpleModule {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6760373312710114988L;

	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ASEscludiDaRosa.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult ror = new ReportOperationResult(this, response);

		disableMessageIdSuccess();

		disableMessageIdFail();

		TransactionQueryExecutor transExec = null;

		String prgNominativo = (String) request.getAttribute("PRGNOMINATIVO");

		try {
			transExec = new TransactionQueryExecutor(getPool());
			// effettuare tutte le operazioni invocate che vanno a modificare le tabelle nella transazione istanziata
			this.enableTransactions(transExec);

			transExec.initTransaction();

			boolean res = doUpdate(request, response);

			if (res) {
				// aggiungi la chiamata a calcola posizione
				// se ii calcola posizione va bene allora commit transazione e messaggio success
				// se ii calcola posizione non va bene allora rollback transazione e messaggio fail
				boolean calcPosizione = calcolaPosizione(request, response, transExec);
				if (calcPosizione) {
					transExec.commitTransaction();
					_logger.info("Esclusione graduatoria avvenuta con successo - PRGNOMINATIVO: " + prgNominativo);
					ror.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
				} else {
					transExec.rollBackTransaction();
					_logger.info("Esclusione graduatoria fallita - PRGNOMINATIVO: " + prgNominativo);
					ror.reportFailure(MessageCodes.IDO.ERR_AGGIORNAMENTO_ESCLUSIONE_LOGICA_POSIZIONE);
				}
			} else {
				transExec.rollBackTransaction();
				_logger.info("Esclusione graduatoria fallita - PRGNOMINATIVO: " + prgNominativo);
				ror.reportFailure(MessageCodes.IDO.ERR_AGGIORNAMENTO_ESCLUSIONE_LOGICA);
			}
		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			ror.reportFailure(MessageCodes.General.UPDATE_FAIL);
		}
	}

	public boolean calcolaPosizione(SourceBean request, SourceBean response, TransactionQueryExecutor transExec) {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");

		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			String codiceRit = "";
			String errCode = "";
			int paramIndex = 0;
			ArrayList parameters = null;

			command = (StoredProcedureCommand) transExec.getDataConnection()
					.createStoredProcedureCommand("{ call ? := PG_INCROCIO.ASCalcolaPosizione(?,?) }");

			String p_prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			String p_prgTipoIncrocio = StringUtils.getAttributeStrNotNull(request, "PRGTIPOINCROCIO");

			parameters = new ArrayList(3);
			// 1.Parametro di Ritorno
			parameters.add(transExec.getDataConnection().createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 2. p_prgRichiestaAz
			parameters.add(transExec.getDataConnection().createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT,
					new BigInteger(p_prgRichiestaAz)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_prgTipoIncrocio
			parameters.add(transExec.getDataConnection().createDataField("p_prgTipoIncrocio", java.sql.Types.BIGINT,
					new BigInteger(p_prgTipoIncrocio)));
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
				ror.reportFailure(msgCode);
				_logger.debug(msg);
				return false;

			} else {
				ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				return true;
			}
			// response.setAttribute((SourceBean) row);

		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";

			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, className, msg);
		} finally {
			Utils.releaseResources(null, command2, dr2);
			Utils.releaseResources(null, command, dr);
		}
		return false;

	}

}
