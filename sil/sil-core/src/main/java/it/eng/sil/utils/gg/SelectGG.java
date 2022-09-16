package it.eng.sil.utils.gg;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class SelectGG extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7315415597490302420L;

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		disableMessageIdFail();
		disableMessageIdSuccess();
		doSelect(serviceRequest, serviceResponse);
	}

}
