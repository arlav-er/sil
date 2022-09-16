package it.eng.sil.publisher.anag;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

public class AnagRicercaPub implements com.engiweb.framework.presentation.PublisherDispatcherIFace {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AnagRicercaPub.class.getName());
	private static final String PUBLISTA = "AnagMainJSPPublisher";
	private static final String PUBREDIR = "RedirAnagDettaglio";

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {

		SourceBean request = requestContainer.getServiceRequest();

		if (request.containsAttribute("cdnLavoratore") && request.getAttribute("cdnLavoratore") != null) {

			_logger.debug("PUB_REDIRECTOR" + "- cdnlavoratore: " + (String) request.getAttribute("cdnLavoratore"));

			return PUBREDIR;
		}

		else {
			return PUBLISTA;
		}

	}// getPublisherName
}// class
