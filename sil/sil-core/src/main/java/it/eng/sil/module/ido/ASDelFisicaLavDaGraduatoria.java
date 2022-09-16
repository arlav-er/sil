/*
 * Creato il 31-lug-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
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

public class ASDelFisicaLavDaGraduatoria extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ASDelFisicaLavDaGraduatoria.class.getName());

	private String className = this.getClass().getName();
	private static final String TRUE = "TRUE";
	private int messageIdFail = MessageCodes.General.OPERATION_FAIL;

	public void service(SourceBean request, SourceBean response) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			setSectionQueryDelete("DEL_STATO_OCC");
			boolean ok = this.doDelete(request, response);
			if (!ok) {
				throw new Exception("ERRORE ESEGUENDO L'ELIMINAZIONE DELLA STORIA STATO OCC");
			} else {
				setSectionQueryDelete("DEL_PUNTEGGIO_LAV");
				ok = this.doDelete(request, response);
				if (!ok) {
					throw new Exception("ERRORE ESEGUENDO L'ELIMINAZIONE DEL PUNTEGGIO");
				} else {
					setSectionQueryDelete("DELETE_CANDIDATO");
					boolean checkUpd = this.doDelete(request, response);
					if (!checkUpd) {
						throw new Exception("ERRORE ESEGUENDO L'ELIMINAZIONE DEL CANDIDATO");
					}
				}
			}

			boolean calcPosizione = calcolaPosizione(request, response, transExec);

			if (!calcPosizione) {
				throw new Exception("ERRORE ESEGUENDO IL RICALCOLO DEL PUNTEGGIO");
			}

			transExec.commitTransaction();
			reportOperation.reportSuccess(idSuccess);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"ASDelFisicaLavDaGraduatoria::service(): Impossibile inserire eliminare il candidato!", ex);

			reportOperation.reportFailure(idFail);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}

		// doDelete(request,response);
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