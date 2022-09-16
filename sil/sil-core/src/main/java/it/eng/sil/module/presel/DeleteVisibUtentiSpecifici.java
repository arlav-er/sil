package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * Description of the Class
 * 
 * @author mudadu
 */
public class DeleteVisibUtentiSpecifici extends AbstractSimpleModule {
	/**
	 * Description of the Method
	 * 
	 * @param request
	 *            Description of the Parameter
	 * @param response
	 *            Description of the Parameter
	 */
	public void service(SourceBean request, SourceBean response) {
		doDelete(request, response);
	}
}
