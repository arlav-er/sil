package it.eng.sil.module.coop;

import com.engiweb.framework.base.SourceBean;

/**
 * @author savino
 */
public class CoopDoInsertFKMansione extends CoopDoInsertModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CoopDoInsertFKMansione.class.getName());

	public boolean doInsertModule(SourceBean serviceRequest, SourceBean serviceResponse) {
		boolean res = false;
		SourceBean mansione = doSelect(serviceRequest, serviceResponse, false);
		try {
			Object prgMansione = mansione.getAttribute("row.prgMansione");
			serviceRequest.updAttribute("prgMansione", prgMansione);
			res = doInsert(serviceRequest, serviceResponse);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile recuperare il prgMansione. Inserimento non possibile.", e);

		}
		return res;
	}
}