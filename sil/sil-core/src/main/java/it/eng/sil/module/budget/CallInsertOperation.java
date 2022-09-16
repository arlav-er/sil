package it.eng.sil.module.budget;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

public class CallInsertOperation extends AbstractModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CallInsertOperation.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		_logger.debug("Sono dentro la classe CallInsertOperation");

	}

}
