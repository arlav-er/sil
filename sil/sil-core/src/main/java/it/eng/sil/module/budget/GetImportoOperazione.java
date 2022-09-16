/**
 * 
 */
package it.eng.sil.module.budget;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author Fatale
 *
 */
public class GetImportoOperazione extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		doSelect(serviceRequest, serviceResponse);
	}

}
