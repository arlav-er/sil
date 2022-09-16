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
public class GetStatoVoucher extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8832397062488855303L;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		// TODO Auto-generated method stub
		doSelect(serviceRequest, serviceResponse);
	}

}
