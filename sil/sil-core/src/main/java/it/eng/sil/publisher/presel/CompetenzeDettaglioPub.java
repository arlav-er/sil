package it.eng.sil.publisher.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

public class CompetenzeDettaglioPub implements com.engiweb.framework.presentation.PublisherDispatcherIFace {
	private static final String PUBMAIN = "CurrCompMainPageJSPPublisher";
	private static final String PUBDETTAGLIO = "CurrCompetenzaPageJSPPublisher";

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {
		SourceBean request = requestContainer.getServiceRequest();

		if (request.containsAttribute("salva")) {
			return PUBMAIN;
		}

		else {
			return PUBDETTAGLIO;
		}

	}// getPublisherName
}// class
