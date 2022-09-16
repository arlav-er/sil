package it.eng.sil.publisher.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * Publisher Java per la formazione professionale.
 * 
 * @author Paolo Cavaciocchi
 */
public class GestisciReportPub implements com.engiweb.framework.presentation.PublisherDispatcherIFace {

	// Nomi dei publisher
	private static final String REPORT_PIENO = "GestioneReportPienoPubbPagePub";

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {

		SourceBean serviceResponse = responseContainer.getServiceResponse();

		String module = (String) requestContainer.getServiceRequest().getAttribute("MODULE");

		return REPORT_PIENO;
	}
}
