package it.eng.sil.module.coop;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author savino
 * 
 */
public class ChiudiSchedaLavoratore extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ChiudiSchedaLavoratore.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		try {
			getRequestContainer().getSessionContainer().delAttribute(GetDatiPersonali.SCHEDA_LAVORATORE_COOP_ID);
			_logger.debug("COOP: la scheda lavoratore e' stata cancellata dalla sessione");

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"COOP: impossibila cancellare la scheda lavoratore dalla sessione", e);

		}
	}

}