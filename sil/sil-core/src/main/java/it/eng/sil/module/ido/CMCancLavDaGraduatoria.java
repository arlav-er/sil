package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * 
 * Eliminazione del lavoratore in do_nominativo inserendo il motivo
 * 
 * @author coticone
 *
 */
public class CMCancLavDaGraduatoria extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CMCancLavDaGraduatoria.class.getName());

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

			setSectionQueryInsert("INSERT_MOTIVO_CANC");
			boolean insMotivoCanc = this.doInsert(request, response);
			if (!insMotivoCanc) {
				throw new Exception("ERRORE ESEGUENDO L'INSERIMENTO DEL MOTIVO DI CANCELLAZIONE DEL CANDIDATO");
			} else {
				setSectionQueryDelete("DEL_PUNTEGGIO_LAV_CM");
				boolean ok = this.doDelete(request, response);
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

			transExec.commitTransaction();
			reportOperation.reportSuccess(idSuccess);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CMCancLavDaGraduatoria::service(): Impossibile inserire eliminare il candidato!", ex);

			reportOperation.reportFailure(idFail);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}

	}

}