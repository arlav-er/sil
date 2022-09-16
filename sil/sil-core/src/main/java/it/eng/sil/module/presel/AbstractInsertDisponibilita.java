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

/**
 * Classe di base per i moduli di inserimento delle disponibilità (orati, turni ecc.). Per ogni mansione selezionata
 * viene inserito l'elemento (orario, turno ecc), gestendo anche il caso di elemento già esistente.
 * 
 * @author Corrado Vaccari
 * @created November 11, 2003
 */
public abstract class AbstractInsertDisponibilita extends AbstractSimpleModule {

	private final static String PRGMANSIONE = "PRGMANSIONE";

	/**
	 * Metodo service.
	 * 
	 * @param request
	 * @param response
	 */
	public void service(SourceBean request, SourceBean response) {

		boolean problemi = true;

		// codeFieldName è differente per ogni "disponibilità"
		// (CODCONTRATTO per i Contratti ecc.).
		// Se non è presente allora significa
		// che l'oggetto elementiExecutor non è necessario
		String codeFieldName = getCodeFieldName();

		Action insertElementAction = new InsertElementAction(this);

		Collection elencoMansioni = AbstractSimpleModule.getArgumentValues(request, PRGMANSIONE);

		CollectionExecutor mansioniExecutor = null;
		// Indica il massimo numero di esecuzioni possibili, ad esempio se ho da
		// inserire 3 contratti in 2 mansioni avrà 3 * 2 esecuzioni.
		int numEsecuzioni = 1;
		if (codeFieldName.length() > 0) {

			Collection elencoElementi = AbstractSimpleModule.getArgumentValues(request, codeFieldName);

			// Gli elementi sono turni, orari ecc ecc
			CollectionExecutor elementiExecutor = new CollectionExecutor(elencoElementi, insertElementAction,
					codeFieldName);
			mansioniExecutor = new CollectionExecutor(elencoMansioni, elementiExecutor, PRGMANSIONE);

			numEsecuzioni = elencoElementi.size();
		} else {

			// In questo caso per ogni mansione viene inserito direttamente
			// l'elemento
			mansioniExecutor = new CollectionExecutor(elencoMansioni, insertElementAction, PRGMANSIONE);
		}

		numEsecuzioni *= elencoMansioni.size();

		int prevIdSuccess = 0;
		int prevIdElementDuplicate = 0;
		// Se numEsecuzioni è 1 allora verrà inserito il messaggio standard
		// di quella unica esecuzione.
		if (numEsecuzioni > 1) {

			// Per evitare che siano inseriti tanti messaggi di successo
			// quanti sono i record legati agli elementi inseriti,
			// oppure che ci siano messaggi di "elemento duplicato"
			// se in alcuni elementi esistevano già,
			// si inibiscono questi messaggi e se ne inserisce uno finale.
			prevIdSuccess = disableMessageIdSuccess();
			prevIdElementDuplicate = disableMessageIdElementDuplicate();
		}

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {

			mansioniExecutor.execute(request, response);
			problemi = false;
		} catch (ActionException aExp) {
			// Se si arriva qui c'è stato un problema durante l'esecuzione
			// di una azione (come l'inserimento di un record)
			// e quindi le successive azioni non verranno eseguite
			problemi = true;
		} catch (SourceBeanException sbExp) {

			reportOperation.reportFailure(this.getMessageIdFail(), sbExp, "service", "View exception for detail");

			problemi = true;
		}

		if (numEsecuzioni > 1) {

			setMessageIdSuccess(prevIdSuccess);
			setMessageIdElementDuplicate(prevIdElementDuplicate);

			if (!problemi) {

				// In caso di problemi, un messaggio viene già inserito
				// e visualizzato col tag showErrors dal metodo
				// doInsert... richiamato
				reportSuccess(reportOperation);
			}
		}
	}

	/**
	 * Il codice tornato è differente per ogni "disponibilità" e sarà CODCONTRATTO per i Contratti ecc
	 * 
	 * Ogni modulo di inserimento derivato deve ridefinire questo metodo.
	 * 
	 * @return The codeFieldName value
	 */
	protected abstract String getCodeFieldName();
}
