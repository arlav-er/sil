package it.eng.sil.publisher.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

public class AbilitazioniDettaglioPub implements com.engiweb.framework.presentation.PublisherDispatcherIFace {
	private static final String PUBMAIN = "CurrAbilMainPageJSPPublisher";
	private static final String PUBDETTAGLIO = "CurrAbilitazionePageJSPPublisher";

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
