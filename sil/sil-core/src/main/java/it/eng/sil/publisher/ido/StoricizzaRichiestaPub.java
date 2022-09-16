package it.eng.sil.publisher.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * Publisher Java per le Storicizzazioni.
 * 
 * @author Gianmichele Siano
 */
public class StoricizzaRichiestaPub implements com.engiweb.framework.presentation.PublisherDispatcherIFace {

	// Nomi dei publisher
	private static final String IdoRichiestaRicercaJSPPublisher = "IdoRichiestaRicercaJSPPublisher";
	private static final String GEST_INCROCIO_PUB = "GEST_INCROCIO_PUB";

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {

		SourceBean serviceResponse = responseContainer.getServiceResponse();

		String CodiceRit = (String) responseContainer.getServiceResponse()
				.getAttribute("M_STORICIZZARICHIESTA.ROW.CodiceRit");

		if (CodiceRit.equalsIgnoreCase("-1"))
			return GEST_INCROCIO_PUB;
		if (!CodiceRit.equalsIgnoreCase("-1"))
			return GEST_INCROCIO_PUB;

		return "";
	}

}
