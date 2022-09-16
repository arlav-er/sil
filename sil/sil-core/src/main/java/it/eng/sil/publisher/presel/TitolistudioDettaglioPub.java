package it.eng.sil.publisher.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

public class TitolistudioDettaglioPub implements com.engiweb.framework.presentation.PublisherDispatcherIFace {
	private static final String PUBMAIN = "CurrStudiMainJSPPublisher";
	private static final String PUBDETTAGLIO = "CurrStudiTitoloPageJSPPublisher";

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {
		SourceBean request = requestContainer.getServiceRequest();
		boolean errorOccurred = responseContainer.getErrorHandler().getErrors().size() > 0;
		if (errorOccurred)
			return PUBMAIN;
		else
			return PUBDETTAGLIO;
	} // getPublisherName
} // class
