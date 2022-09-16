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
public class GetAzioneVoucher extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6038162471019753530L;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		doSelect(serviceRequest, serviceResponse);

	}

}
