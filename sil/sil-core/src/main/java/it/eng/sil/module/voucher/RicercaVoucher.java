/**
 * 
 */
package it.eng.sil.module.voucher;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

/**
 * @author Fatale
 *
 */
public class RicercaVoucher extends AbstractModule {

	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicercaVoucher.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 2688817536382586561L;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		// TODO Auto-generated method stub
		_logger.debug("Sono dentro la classe RicercaVoucher nome classe " + className);

		_logger.debug("Impostazione dei dati della query");

		// HttpServletRequest request = getHttpRequest();
		/*
		 * RequestContainer requestContainer = getRequestContainer(); SessionContainer sessionContainer =
		 * (SessionContainer) requestContainer.getSessionContainer(); User user = (User)
		 * getRequestContainer().getSessionContainer().getAttribute(User.USERID); StringBuffer query_totale = new
		 * StringBuffer("");
		 */

	}

}
