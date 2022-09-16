package it.eng.sil.module.presel;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoFacade;
import it.eng.sil.util.collectionexecutor.Action;
import it.eng.sil.util.collectionexecutor.ActionException;

public class InsertTitolo extends AbstractSimpleModule {

	class InsertTitoloAction implements Action {

		private InsertTitolo module;

		InsertTitoloAction(InsertTitolo module) {
			this.module = module;
		}

		/**
		 * Esegue l'action. L'action è rappresentata dall'esecuzione del metodo doInsertTitolo di InsertTitolo, che
		 * quindi funziona da delegato.
		 * 
		 * @param request
		 * @param response
		 * @exception ActionException
		 *                Invocata se si verifica un problema durante l'esecuzione dell'action
		 * @exception SourceBeanException
		 */
		public void execute(SourceBean request, SourceBean response) throws ActionException, SourceBeanException {

			if (module.doInsertTitolo(request, response) == false) {

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

			InsertTitoloAction insertTitolo = new InsertTitoloAction(this);
			facade.doInsert(this, getPool(), request, response, "PRGSTUDIO", insertTitolo);

		} else {

			doInsertTitolo(request, response);
		}
	}

	private boolean doInsertTitolo(SourceBean request, SourceBean response) {

		int messageIdSuccess = this.disableMessageIdSuccess();
		SourceBean beanSelect = doSelect(request, response, false);
		this.setMessageIdSuccess(messageIdSuccess);

		Vector vect = beanSelect.getAttributeAsVector("ROW");

		boolean ris = false;

		boolean isElemEsistente = (beanSelect != null) && (vect.size() > 0);
		if (isElemEsistente) {

			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			reportOperation.reportFailure(this.getMessageIdElementDuplicate());

		} else {

			if ("S".equals(request.getAttribute("flgPrincipale"))) {

				messageIdSuccess = this.disableMessageIdSuccess();
				int messageIdFail = this.disableMessageIdFail();

				doUpdate(request, response);

				this.setMessageIdSuccess(messageIdSuccess);
				this.setMessageIdFail(messageIdFail);
			}

			ris = doInsert(request, response);
		}

		return ris;
	}
}