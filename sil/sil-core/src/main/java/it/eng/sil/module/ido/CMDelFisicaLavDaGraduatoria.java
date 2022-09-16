/*
 * Creato il 31-lug-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class CMDelFisicaLavDaGraduatoria extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CMDelFisicaLavDaGraduatoria.class.getName());

	private String className = this.getClass().getName();
	private static final String TRUE = "TRUE";
	private int messageIdFail = MessageCodes.General.OPERATION_FAIL;

	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer reqCont = getRequestContainer();
		ResponseContainer resCont = getResponseContainer();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			SourceBean queryDelete1 = (SourceBean) getConfig().getAttribute("DEL_PUNTEGGIO_LAV_CM");
			Boolean ok = (Boolean) transExec.executeQuery(reqCont, resCont, queryDelete1, "DELETE");
			// setSectionQueryDelete("DEL_PUNTEGGIO_LAV");
			// this.doDelete(request, response);
			if (!ok.booleanValue()) {
				throw new Exception("ERRORE ESEGUENDO L'ELIMINAZIONE DEL PUNTEGGIO");
			} else {
				SourceBean queryDelete2 = (SourceBean) getConfig().getAttribute("DELETE_CANDIDATO");
				Boolean checkUpd = (Boolean) transExec.executeQuery(reqCont, resCont, queryDelete2, "DELETE");
				// setSectionQueryDelete("DELETE_CANDIDATO");
				// boolean checkUpd = this.doDelete(request,response);
				if (!checkUpd.booleanValue()) {
					throw new Exception("ERRORE ESEGUENDO L'ELIMINAZIONE DEL CANDIDATO");
				}
			}

			transExec.commitTransaction();
			reportOperation.reportSuccess(idSuccess);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CMDelFisicaLavDaGraduatoria::service(): Impossibile inserire eliminare il candidato!", ex);

			reportOperation.reportFailure(idFail);
			// reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}

		// doDelete(request,response);
	}

}