package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoFacade;
import it.eng.sil.util.collectionexecutor.Action;
import it.eng.sil.util.collectionexecutor.ActionException;

public class SaveEspLav extends AbstractSimpleModule {

	class SaveEspLavAction implements Action {

		/**
		 * Esegue l'action. L'action è rappresentata dall'esecuzione del metodo doSaveEspLav di SaveEspLav, che quindi
		 * funziona da delegato.
		 * 
		 * @param request
		 * @param response
		 * @exception ActionException
		 *                Invocata se si verifica un problema durante l'esecuzione dell'action
		 * @exception SourceBeanException
		 */
		public void execute(SourceBean request, SourceBean response) throws ActionException, SourceBeanException {

			if (doSaveEspLav(request, response) == false) {

				// Se si è verificato un problema, in questo modo il
				// CollectionExecutor
				// viene avvertito.
				throw new ActionException();
			}
		}
	}

	public void service(SourceBean request, SourceBean response) {
		boolean atecoValido = false;

		String codAteco = request.containsAttribute("codAteco") ? request.getAttribute("codAteco").toString() : "";
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		atecoValido = it.eng.sil.module.anag.AtecoUtils.checkValiditaAteco(codAteco, false);

		if (!atecoValido) {
			reportOperation.reportFailure(idFail);
			reportOperation.reportFailure(MessageCodes.UnitaAzienda.ATECO_NON_VALIDO);
			return;
		}

		this.setMessageIdSuccess(MessageCodes.General.OPERATION_SUCCESS);

		PattoFacade facade = new PattoFacade();
		if (facade.withPatto(request)) {

			SaveEspLavAction saveEspLav = new SaveEspLavAction();
			facade.doUpdate(this, getPool(), request, response, saveEspLav);

		} else {

			doSaveEspLav(request, response);
		}
	}

	private boolean doSaveEspLav(SourceBean request, SourceBean response) {
		if (doUpdate(request, response)) {
			String inserimentoMansione = request.containsAttribute("InserimentoMansione")
					? request.getAttribute("InserimentoMansione").toString()
					: "0";
			if (inserimentoMansione.equals("1")) {
				setSectionQueryUpdate("QUERY_UPDATE_FLAG_ESP");
				int messageIdSuccess = this.disableMessageIdSuccess();
				int messageIdFail = this.disableMessageIdFail();
				doUpdate(request, response);
				this.setMessageIdSuccess(messageIdSuccess);
				this.setMessageIdFail(messageIdFail);
			}
			return true;
		}

		return false;
	}

}