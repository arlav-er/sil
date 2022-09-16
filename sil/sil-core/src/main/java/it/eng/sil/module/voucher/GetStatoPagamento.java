/**
 * 
 */
package it.eng.sil.module.voucher;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author Fatale
 *
 */
public class GetStatoPagamento extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5562843365738973717L;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		// TODO Auto-generated method stub
		doSelect(serviceRequest, serviceResponse);
	}

}
