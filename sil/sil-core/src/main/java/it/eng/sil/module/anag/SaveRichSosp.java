/*
 * Creato il 26-ott-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

/**
 * @author melandri
 *
 *  * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;

import it.eng.afExt.utils.MessageCodes; //di errore:"dati salvati corrrettamente" "..erroneamente" etc.
import it.eng.afExt.utils.ReportOperationResult; //Servono per per gestire i messaggi
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class SaveRichSosp extends AbstractSimpleModule {
	private final String className = StringUtils.getClassName(this);
	private TransactionQueryExecutor transExec;
	BigDecimal userid;

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			this.setSectionQueryUpdate("QUERY_UPDATE_RICH_SOSP");
			ret = doUpdate(request, response);

			String aggProsp = (String) request.getAttribute("aggProsp");
			if (aggProsp.equals("1")) {
				ret = gestisciProspetto(request, reportOperation);
			}

			if (!ret) {
				throw new Exception("impossibile aggiornare CM_RICH_SOSPENSIONE in transazione");
			}

			transExec.commitTransaction();
			this.setMessageIdSuccess(MessageCodes.General.UPDATE_SUCCESS);
			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			transExec.rollBackTransaction();
			this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL);
			reportOperation.reportFailure(e, "service()", "aggiornamento in transazione");
		} finally {
		}
	}

	/**
	 * Automatismo per l'aggiornamento dei lavoratori L68 del prospetto associato alla sospensione (ultimo prospetto in
	 * corso d'anno per la stessa azienda della sospensione
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private boolean gestisciProspetto(SourceBean request, ReportOperationResult reportOperation) throws Exception {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;

		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		String pool = (String) getConfig().getAttribute("POOL");
		conn = transExec.getDataConnection();
		SourceBean statementSB = null;
		String statement = "";
		String sqlStr = "";
		String codiceRit = "";

		int paramIndex = 0;
		ArrayList parameters = null;

		statementSB = (SourceBean) getConfig().getAttribute("CM_AGG_PROSP_DA_SOSPENSIONE");
		statement = statementSB.getAttribute("STATEMENT").toString();
		sqlStr = SQLStatements.getStatement(statement);
		command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		String p_prgRichSospensione = (String) request.getAttribute("prgRichSospensione");
		String p_cdnUtente = String.valueOf(user.getCodut());

		parameters = new ArrayList(4);
		// 1.Parametro di Ritorno
		parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);
		// preparazione dei Parametri di Input
		// 2. p_prgRichSospensione
		parameters.add(conn.createDataField("p_prgRichSospensione", java.sql.Types.BIGINT,
				new BigInteger(p_prgRichSospensione)));
		command.setAsInputParameters(paramIndex++);
		// 3. p_cdnUtente
		parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
		command.setAsInputParameters(paramIndex++);
		// 4.out_annoProspetto
		parameters.add(conn.createDataField("out_annoProsp", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);

		// Chiamata alla Stored Procedure
		dr = command.execute(parameters);
		// Reperisco il valore di output della stored
		CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
		List outputParams = cdr.getContainedDataResult();
		PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
		DataField df = pdr.getPunctualDatafield();
		codiceRit = df.getStringValue();

		// 1. p_out_annoProspetto
		pdr = (PunctualDataResult) outputParams.get(1);
		df = pdr.getPunctualDatafield();
		String outAnnoProspetto = df.getStringValue();
		if (outAnnoProspetto == null) {
			outAnnoProspetto = "";
		}
		Vector paramErrore = new Vector();
		paramErrore.add(outAnnoProspetto);

		if (("-1").equals(codiceRit)) {
			reportOperation.reportWarning(MessageCodes.CollocamentoMirato.ERRORE_AGGIORNAMENTO_L68_PROSPETTO, className,
					"gestione aggiornamento prospetto da sospensione");
			return true;
		} else if (("-2").equals(codiceRit)) {
			reportOperation.reportWarning(MessageCodes.CollocamentoMirato.ERRORE_AGG_L68_PROSPETTO_INESISTENTE,
					className, "gestione aggiornamento prospetto da sospensione");
			return true;
		} else {
			// viene aggiunto il messaggio di avvenuto aggiornamento prospetto
			reportOperation.reportWarning(MessageCodes.CollocamentoMirato.WARNING_L68_PROSPETTO_AGGIORNATO, className,
					"gestione aggiornamento prospetto da sospensione", paramErrore);
			return true;
		}
	}
}
