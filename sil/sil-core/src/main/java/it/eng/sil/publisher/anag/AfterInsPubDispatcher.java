package it.eng.sil.publisher.anag;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;

/**
 * Publisher Java per l'anagrafica Se l'inserimento è andato a buon fine andiamo nella pagina di dettaglio altrimenti
 * torniamo nella pagina di inserimento (in questo modo non perdiamo i parametri della request e non dobbiamo ricorrere
 * alla sessione)
 */
public class AfterInsPubDispatcher implements com.engiweb.framework.presentation.PublisherDispatcherIFace {

	// Nomi dei publisher
	private static final String INSERIMENTO = "NewAnag_AnagraficaJSP";
	private static final String DETTAGLIO = "Anag_AnagraficaJSP";

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {
		SourceBean serviceResponse = responseContainer.getServiceResponse();
		SourceBean request = requestContainer.getServiceRequest();

		String insertResponse = StringUtils.getAttributeStrNotNull(serviceResponse,
				"M_INSERTLAVORATOREANAGINDIRIZZI.operationResult");

		if (insertResponse.equalsIgnoreCase("SUCCESS")) {
			return DETTAGLIO;
		} else {
			return INSERIMENTO;
		}

	}

}// class AfterInsPubDispatcher
