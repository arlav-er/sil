package it.eng.sil.util.collectionexecutor;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * Action per l'inserimento di un record.
 * 
 * Ad esempio nelle Disponibilità l'elemento potrebbe essere un turno, un orario ecc.
 * 
 * @author Corrado Vaccari
 * @created November 13, 2003
 */
public class InsertElementAction implements Action {

	private AbstractSimpleModule module;

	public InsertElementAction(AbstractSimpleModule module) {
		this.module = module;
	}

	/**
	 * Esegue l'inserimento dell'elemento (orario, turno ecc.) sul database.
	 * 
	 * @param request
	 * @param response
	 * @exception ActionException
	 * @exception SourceBeanException
	 */
	public void execute(SourceBean request, SourceBean response) throws ActionException, SourceBeanException {

		if (false == module.doInsertNoDuplicate(request, response)) {

			// Questa excepion interrompe l'inserimento degli elementi
			// per evitare che lo stesso problema possa
			// ripetersi n volte.
			throw new ActionException();
		}
	}
}
