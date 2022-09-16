package it.eng.sil.module.budget;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

public class CercaSoggettiAccreditatiVoucher extends AbstractModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2853476908583731166L;

	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CercaSoggettiAccreditatiVoucher.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		_logger.debug("dentro la classe " + className);
		RequestContainer reqCont = getRequestContainer();
		SessionContainer session = reqCont.getSessionContainer();
		session.delAttribute("tipoRicercaSogAcc");
	}

}
