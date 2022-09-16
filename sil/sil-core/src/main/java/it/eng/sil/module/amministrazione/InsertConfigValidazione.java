package it.eng.sil.module.amministrazione;

import java.util.Collection;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.collectionexecutor.Action;
import it.eng.sil.util.collectionexecutor.ActionException;
import it.eng.sil.util.collectionexecutor.CollectionExecutor;
import it.eng.sil.util.collectionexecutor.InsertElementAction;

public class InsertConfigValidazione extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {

		boolean ok = true;
		int numeroElementi = 0;
		int prevIdSuccess = 0;
		int prevIdElementDuplicate = 0;

		Action insertConfigValidazioneAction = new InsertElementAction(this);

		Collection elencoCampiAggiornamento = AbstractSimpleModule.getArgumentValues(request, "STRNOMECAMPO");

		CollectionExecutor campiAggiornamentoExecutor = new CollectionExecutor(elencoCampiAggiornamento,
				insertConfigValidazioneAction, "STRNOMECAMPO");

		numeroElementi = elencoCampiAggiornamento.size();
		if (numeroElementi > 1) {
			prevIdSuccess = disableMessageIdSuccess();
			prevIdElementDuplicate = disableMessageIdElementDuplicate();
		}

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		try {

			campiAggiornamentoExecutor.execute(request, response);
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