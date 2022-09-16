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
public class CampiDettaglioTotaliBudget extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -273839195745029431L;

	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CampiDettaglioTotaliBudget.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		_logger.debug("Sono dentro la classe CampiDettaglioTotaliBudget " + className);

		_logger.debug("Il valore della request:::: " + serviceRequest);

		String anno = (String) serviceRequest.getAttribute("anno");

	}

}
