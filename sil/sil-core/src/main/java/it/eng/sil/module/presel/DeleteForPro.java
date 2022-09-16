package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoFacade;

/**
 * Description of the Class
 * 
 * @author cavaciocchi
 */
public class DeleteForPro extends AbstractSimpleModule {
	/**
	 * Description of the Method
	 * 
	 * @param request
	 *            Description of the Parameter
	 * @param response
	 *            Description of the Parameter
	 */
	public void service(SourceBean request, SourceBean response) {

		PattoFacade facade = new PattoFacade();
		if (facade.withPatto(request)) {

			facade.doDelete(this, getPool(), request, response);

		} else {

			doDelete(request, response);
		}
	}
}
