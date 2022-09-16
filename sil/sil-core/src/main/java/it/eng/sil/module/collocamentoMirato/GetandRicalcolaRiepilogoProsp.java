/*
 * Creato il 18-dic-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author riccardi
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class GetandRicalcolaRiepilogoProsp extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(GetandRicalcolaRiepilogoProsp.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		ReportOperationResult ror = new ReportOperationResult(this, response);
		boolean checkCalcolo = false;
		BigDecimal prgProspInf = null;

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			this.setSectionQuerySelect("SELECT_PROSPETTO");
			SourceBean rowProsp = doSelect(request, response);

			if (rowProsp.containsAttribute("ROW.PRGPROSPETTOINF")) {
				prgProspInf = (BigDecimal) rowProsp.getAttribute("ROW.PRGPROSPETTOINF");
			}

			if (prgProspInf != null) {
				request.setAttribute("PRGPROSPETTOINF", prgProspInf);
				response.delContainedAttributes();
				// if (request.containsAttribute("ricalcoloProsp") &&
				// ((String)request.getAttribute("ricalcoloProsp")).equals("1")) {
				checkCalcolo = getRicalcoloRiepilogo(transExec, request, response);
				if (!checkCalcolo) {
					throw new Exception("Errore durante il ricalcolo del riepilogo prospetto. Operazione interrotta");
				}
				// }
				this.setSectionQuerySelect("SELECT_RIEPILOGO");
				doSelect(request, response);
				transExec.commitTransaction();
			} else {
				transExec.rollBackTransaction();
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"GetandRicalcolaRiepilogoProsp::service(): Impossibile ricalcolare il riepilogo prospetto!", ex);

			ror.reportFailure(MessageCodes.General.OPERATION_FAIL);
			if (transExec != null) {
				try {
					transExec.rollBackTransaction();
				} catch (EMFInternalError e) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"GetandRicalcolaRiepilogoProsp::service(): Impossibile ricalcolare il riepilogo prospetto!",
							ex);

				}
			}
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

}