package it.eng.sil.util;

import java.util.Iterator;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * Classe molto specializzata che gestisce l'operazione di inserire un certo tipo di elementi in una categoria di entità
 * su database.
 * 
 * Ad esempio gestisce l'inserimento di un certo tipo di contratto (element) in tutti i tipi di mansioni presenti con
 * almento un contratto (category).
 * 
 * Esempi: - Inserire un certo tipo di contratto in tutte le mansioni di un certo lavoratore - Inserire un certo tipo di
 * turno in tutte le mansioni di un certo lavoratore - Inserire un certo tipo di orario in tutte le mansioni di un certo
 * lavoratore - Inserire un certo tipo di territorio in tutte le mansioni di un certo lavoratore
 * 
 * @author Corrado Vaccari
 */
public class InsertElementInCategoryHandler {

	private AbstractSimpleModule module;
	private SourceBean request;
	private SourceBean response;
	private String selectCategoryQuery;
	private String categorySelectorField;

	public InsertElementInCategoryHandler(AbstractSimpleModule module, SourceBean request, SourceBean response,
			String selectCategoryQuery, String categorySelectorField) {

		this.module = module;
		this.request = request;
		this.response = response;
		this.selectCategoryQuery = selectCategoryQuery;
		this.categorySelectorField = categorySelectorField;
	}

	/**
	 * Inserisce il contratto in tutte le mansioni presenti con almeno un altro tipo di contratto.
	 * 
	 * @param request
	 * @param response
	 */
	public void doInsertInCategory() {

		// Per evitare che siano inseriti tanti messaggi di successo
		// quanti sono i record legati alle mansioni inseriti,
		// oppure che ci siano messaggi di "elemento duplicato"
		// se in alcune mansioni esisteva già quel contratto,
		// si inibiscono questi messaggi
		// e se ne inserisce uno finale.

		int prevIdSuccess = module.setMessageIdSuccess(0);
		int prevIdElementDuplicate = module.setMessageIdElementDuplicate(0);

		String prevQuery = module.setSectionQuerySelect(selectCategoryQuery);
		SourceBean beanSelectAll = module.doSelect(request, response);
		module.setSectionQuerySelect(prevQuery);

		boolean problemi = false;

		Vector vectMansioni = (beanSelectAll != null) ? beanSelectAll.getAttributeAsVector("ROW") : null;
		if ((vectMansioni != null) && (vectMansioni.size() > 0)) {

			Object prevPrgMansione = request.getAttribute(categorySelectorField);

			try {

				for (Iterator iter = vectMansioni.iterator(); iter.hasNext();) {

					Object prgMansione = ((SourceBean) iter.next()).getAttribute(categorySelectorField);

					request.updAttribute(categorySelectorField, prgMansione);
					if (!module.doInsertNoDuplicate(request, response)) {

						// Blocco il ciclo per evitare che lo stesso problema
						// possa
						// ripetersi n volte.
						problemi = true;
						break;
					}
				}

				request.updAttribute(categorySelectorField, prevPrgMansione);
			} catch (SourceBeanException exp) {

				LogUtils.logError("InsertElementInCategoryHandler.doInsertInCategory", "request.setAttribute failed",
						exp, module);
			}
		}

		module.setMessageIdSuccess(prevIdSuccess);
		module.setMessageIdElementDuplicate(prevIdElementDuplicate);

		if (problemi == false) {

			// In caso di problemi, un messaggio viene già inserito
			// e visualizzato col tag showErrors
			ReportOperationResult reportOperation = new ReportOperationResult(module, response);
			module.reportSuccess(reportOperation);
		}
	}
}