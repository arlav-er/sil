package it.eng.sil.publisher.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * Publisher Java per la formazione professionale.
 * 
 * @author Paolo Cavaciocchi
 */
public class AltCredPub implements com.engiweb.framework.presentation.PublisherDispatcherIFace {

	// Nomi dei publisher
	private static final String LIST_ALT_CRED_PUB = "LIST_ALT_CRED_PUB";
	private static final String LOAD_ALT_CRED_PUB = "LOAD_ALT_CRED_PUB";
	private static final String DELETE_ALT_CRED_PUB = "DELETE_ALT_CRED_PUB";
	private static final String INSERT_ALT_CRED_PUB = "INSERT_ALT_CRED_PUB";
	private static final String UPDATE_ALT_CRED_PUB = "UPDATE_ALT_CRED_PUB";

	// Nomi dei moduli
	private static final String LIST_ALT_CRED_MODULE = "M_ListAltCred";
	private static final String LOAD_ALT_CRED_MODULE = "M_LoadAltCred";
	private static final String DELETE_ALT_CRED_MODULE = "M_DeleteAltCred";
	private static final String INSERT_ALT_CRED_MODULE = "M_InsertAltCred";
	private static final String UPDATE_ALT_CRED_MODULE = "M_UpdateAltCred";

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {

		SourceBean serviceResponse = responseContainer.getServiceResponse();

		String module = (String) requestContainer.getServiceRequest().getAttribute("MODULE");

		if ((module == null) || (LIST_ALT_CRED_MODULE.equalsIgnoreCase(module)))
			return LIST_ALT_CRED_PUB;

		if (LOAD_ALT_CRED_MODULE.equalsIgnoreCase(module))
			return LOAD_ALT_CRED_PUB;

		if (DELETE_ALT_CRED_MODULE.equalsIgnoreCase(module))
			return DELETE_ALT_CRED_PUB;

		if (INSERT_ALT_CRED_MODULE.equalsIgnoreCase(module))
			return INSERT_ALT_CRED_PUB;

		if (UPDATE_ALT_CRED_MODULE.equalsIgnoreCase(module)) {

			// Se l'update è andato a buon fine, passa alla lista
			// altrimenti rimane all'update
			String esitoUpdate = (String) serviceResponse.getAttribute(UPDATE_ALT_CRED_MODULE + ".UPDATE_OK");
			return "true".equalsIgnoreCase(esitoUpdate) ? LIST_ALT_CRED_PUB : UPDATE_ALT_CRED_PUB;
		}

		return "";
	}

}
