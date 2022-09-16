/**
 * 
 */
package it.eng.sil.module.budget;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

/**
 * @author Fatale
 *
 */
public class CercaBudgetPerCPI extends AbstractModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5425722938497644247L;

	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CercaBudgetPerCPI.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		_logger.debug("Sono dentro la classe CercaBudgetPerCPI nome classe " + className);

		_logger.debug("Impostazione dei dati della query");
		RequestContainer requestContainer = getRequestContainer();
		// SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();

		SessionContainer session = requestContainer.getSessionContainer();
		session.delAttribute("tipoRicerca");

	}

}
