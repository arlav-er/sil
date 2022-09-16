package it.eng.sil.publisher.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * Publisher Java per la formazione professionale.
 * 
 * @author Paolo Cavaciocchi
 */
public class ForProPub implements com.engiweb.framework.presentation.PublisherDispatcherIFace {

	// Nomi dei publisher
	private static final String LIST_FOR_PRO_PUB = "LIST_FOR_PRO_PUB";
	private static final String LOAD_FOR_PRO_PUB = "LOAD_FOR_PRO_PUB";
	private static final String DELETE_FOR_PRO_PUB = "DELETE_FOR_PRO_PUB";
	private static final String INSERT_FOR_PRO_PUB = "INSERT_FOR_PRO_PUB";
	private static final String UPDATE_FOR_PRO_PUB = "UPDATE_FOR_PRO_PUB";
	private static final String LIST_CORSO_FOR_PRO_PUB = "LIST_CORSO_FOR_PRO_PUB";

	// Nomi dei moduli
	private static final String LIST_FOR_PRO_MODULE = "M_ListForPro";
	private static final String LOAD_FOR_PRO_MODULE = "M_LoadForPro";
	private static final String DELETE_FOR_PRO_MODULE = "M_DeleteForPro";
	private static final String INSERT_FOR_PRO_MODULE = "M_InsertForPro";
	private static final String UPDATE_FOR_PRO_MODULE = "M_UpdateForPro";
	private static final String LIST_CORSO_FOR_PRO_MODULE = "M_ListForProCorso";

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {

		SourceBean serviceResponse = responseContainer.getServiceResponse();

		String module = (String) requestContainer.getServiceRequest().getAttribute("MODULE");

		if ((module == null) || (LIST_FOR_PRO_MODULE.equalsIgnoreCase(module)))
			return LIST_FOR_PRO_PUB;

		if (LOAD_FOR_PRO_MODULE.equalsIgnoreCase(module))
			return LOAD_FOR_PRO_PUB;

		if (DELETE_FOR_PRO_MODULE.equalsIgnoreCase(module))
			return DELETE_FOR_PRO_PUB;

		if (INSERT_FOR_PRO_MODULE.equalsIgnoreCase(module))
			return INSERT_FOR_PRO_PUB;

		if (UPDATE_FOR_PRO_MODULE.equalsIgnoreCase(module)) {

			// Se l'update è andato a buon fine, passa alla lista *non più (AR
			// 27/10)
			// altrimenti rimane all'update *rimane sempre all'update (AR 27/10)
			String esitoUpdate = (String) serviceResponse.getAttribute(UPDATE_FOR_PRO_MODULE + ".UPDATE_OK");
			// return LIST_FOR_PRO_PUB; * rimane sempre all'update (AR 27/10)
			return LOAD_FOR_PRO_PUB;
		}

		if (LIST_CORSO_FOR_PRO_MODULE.equalsIgnoreCase(module))
			return LIST_CORSO_FOR_PRO_PUB;

		return "";
	}
}
