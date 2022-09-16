package it.eng.sil.module.presel;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoFacade;
import it.eng.sil.util.collectionexecutor.Action;
import it.eng.sil.util.collectionexecutor.ActionException;

public class SaveTitolo extends AbstractSimpleModule {

	class SaveTitoloAction implements Action {

		private SaveTitolo module;

		SaveTitoloAction(SaveTitolo module) {
			this.module = module;
		}

		/**
		 * Esegue l'action. L'action è rappresentata dall'esecuzione del metodo doSaveTitolo di SaveTitolo, che quindi
		 * funziona da delegato.
		 * 
		 * @param request
		 * @param response
		 * @exception ActionException
		 *                Invocata se si verifica un problema durante l'esecuzione dell'action
		 * @exception SourceBeanException
		 */
		public void execute(SourceBean request, SourceBean response) throws ActionException, SourceBeanException {

			if (module.doSaveTitolo(request, response) == false) {

				// Se si è verificato un problema, in questo modo il
				// CollectionExecutor
				// viene avvertito.
				throw new ActionException();
			}
		}
	}

	public void service(SourceBean request, SourceBean response) {

		PattoFacade facade = new PattoFacade();
		if (facade.withPatto(request)) {

			SaveTitoloAction saveTitolo = new SaveTitoloAction(this);
			facade.doUpdate(this, getPool(), request, response, saveTitolo);

		} else {

			doSaveTitolo(request, response);
		}
	}

	private boolean doSaveTitolo(SourceBean request, SourceBean response) {

		boolean do_update = true;

		// Non voglio che doSelect produca alcun
		// messaggio di successo
		int messageIdSuccess = this.disableMessageIdSuccess();

		SourceBean beanSelect = doSelect(request, response, false);

		this.setMessageIdSuccess(messageIdSuccess);

		Vector vect = beanSelect.getAttributeAsVector("ROW");

		boolean isElemEsistente = (beanSelect != null) && (vect.size() > 0);
		if (isElemEsistente) {

			String prgSelect = ((SourceBean) vect.get(0)).getAttribute("prgStudio").toString();
			String prgRequest = request.getAttribute("prgStudio").toString();

			if (!prgSelect.equals(prgRequest)) {

				ReportOperationResult reportOperation = new ReportOperationResult(this, response);
				reportOperation.reportFailure(this.getMessageIdElementDuplicate());

				do_update = false;
			}
		}

		if (do_update) {

			setSectionQueryUpdate("QUERY_UPDATE_DISASSERT");
			messageIdSuccess = this.disableMessageIdSuccess();
			int messageIdFail = this.disableMessageIdFail();

			doUpdate(request, response);

			this.setMessageIdSuccess(messageIdSuccess);
			this.setMessageIdFail(messageIdFail);

			setSectionQueryUpdate("QUERY_UPDATE_TITOLO");
			do_update = doUpdate(request, response);
		}

		return do_update;
	}

}