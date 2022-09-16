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
public class GetElencoSoggettiAccVoucher extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6955605232392935727L;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		doSelect(serviceRequest, serviceResponse);
	}

}
