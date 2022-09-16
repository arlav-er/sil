package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.MobilitaException;
import it.eng.sil.util.amministrazione.impatti.ProTrasfoException;

public class EffettuaScorrimentoMobilita extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(EffettuaScorrimentoMobilita.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean scorrimento = false;
		String cdnLav = request.containsAttribute("cdnLavoratore") ? request.getAttribute("cdnLavoratore").toString()
				: "";
		String datInizio = request.containsAttribute("datInizioMob") ? request.getAttribute("datInizioMob").toString()
				: "";
		try {
			transExec = new TransactionQueryExecutor(getPool());
			transExec.initTransaction();
			// chiamata per eventuale scorrimento
			scorrimento = UtilsMobilita.scorrimentoDaEventi(request, cdnLav, datInizio, transExec);
			transExec.commitTransaction();
			this.setMessageIdSuccess(idSuccess);
			this.setMessageIdFail(idFail);
			reportOperation.reportSuccess(idSuccess);
			if (scorrimento) {
				reportOperation.reportSuccess(MessageCodes.ScorrimentoMobilita.OPERAZIONE_OK);
			}
		}

		catch (MobilitaException me) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "Movimenti non compatibili con la mobilità.", (Exception) me);

			UtilsMobilita.addConfirm(request, response, me.getCode());
		}

		catch (ControlliException ce) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "Movimenti non compatibili con la did.", (Exception) ce);

			UtilsMobilita.addConfirm(request, response, ce.getCode());
		}

		catch (ProTrasfoException proTrasfEx) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nella ricostruzione della storia.",
					(Exception) proTrasfEx);

			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			int code = proTrasfEx.getCode();
			if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA) {
				reportOperation.reportFailure(MessageCodes.StatoOccupazionale.PROROGA_NON_COLLEGATA);
			} else {
				reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA);
			}
		}

		catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
		}
	}

}