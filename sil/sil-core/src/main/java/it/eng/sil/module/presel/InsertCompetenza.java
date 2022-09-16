package it.eng.sil.module.presel;

import java.util.Collection;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.collectionexecutor.Action;
import it.eng.sil.util.collectionexecutor.ActionException;
import it.eng.sil.util.collectionexecutor.CollectionExecutor;
import it.eng.sil.util.collectionexecutor.InsertElementAction;

public class InsertCompetenza extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		boolean ok = true;
		int numeroElementi = 0;
		int prevIdSuccess = 0;
		int prevIdElementDuplicate = 0;

		Action insertCompetenzaAction = new InsertElementAction(this);

		Collection elencoCompetenze = AbstractSimpleModule.getArgumentValues(request, "codCompetenza");

		CollectionExecutor competenzeExecutor = new CollectionExecutor(elencoCompetenze, insertCompetenzaAction,
				"codCompetenza");

		numeroElementi = elencoCompetenze.size();
		if (numeroElementi > 1) {
			prevIdSuccess = disableMessageIdSuccess();
			prevIdElementDuplicate = disableMessageIdElementDuplicate();
		}

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		try {

			competenzeExecutor.execute(request, response);
		} catch (ActionException aExp) {
			ok = false;
		} catch (SourceBeanException sbExp) {
			ok = false;
		}

		if (numeroElementi > 1) {

			setMessageIdSuccess(prevIdSuccess);
			setMessageIdElementDuplicate(prevIdElementDuplicate);

			if (ok) {

				// In caso di problemi, un messaggio viene già inserito
				// e visualizzato col tag showErrors dal metodo
				// doInsert... richiamato
				reportSuccess(reportOperation);
			}
		}

	}

}