package it.eng.sil.module.modelli.tda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class DeleteAssociazioneAzioni extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7564545317521706703L;

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		doDelete(serviceRequest, serviceResponse);
	}

}
