package it.eng.sil.publisher.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * Publisher Java per le Conoscenze Info.
 * 
 * @author Corrado Vaccari
 */
public class ConoscenzeInfoPub implements com.engiweb.framework.presentation.PublisherDispatcherIFace {

	// Nomi dei publisher
	private static final String LIST_CONOSCENZE_INFO_PUB = "LIST_CONOSCENZE_INFO_PUB";
	private static final String LOAD_CONOSCENZA_INFO_PUB = "LOAD_CONOSCENZA_INFO_PUB";
	private static final String DELETE_CONOSCENZA_INFO_PUB = "DELETE_CONOSCENZA_INFO_PUB";
	private static final String INSERT_CONOSCENZA_INFO_PUB = "INSERT_CONOSCENZA_INFO_PUB";
	private static final String UPDATE_CONOSCENZA_INFO_PUB = "UPDATE_CONOSCENZA_INFO_PUB";
	private static final String LIST_DETTAGLI_CONOSCENZE_INFO_PUB = "LIST_DETTAGLI_CONOSCENZE_INFO_PUB";

	// Nomi dei moduli
	private static final String LIST_CONOSCENZE_INFO_MODULE = "M_ListConoscenzeInfo";
	private static final String LOAD_CONOSCENZA_INFO_MODULE = "M_LoadConoscenzaInfo";
	private static final String DELETE_CONOSCENZA_INFO_MODULE = "M_DeleteConoscenzaInfo";
	private static final String INSERT_CONOSCENZA_INFO_MODULE = "M_InsertConoscenzaInfo";
	private static final String UPDATE_CONOSCENZA_INFO_MODULE = "M_UpdateConoscenzaInfo";
	private static final String LIST_DETTAGLI_CONOSCENZE_INFO_MODULE = "M_ListDettagliConoscenzaInfo";

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {

		SourceBean serviceResponse = responseContainer.getServiceResponse();

		String module = (String) requestContainer.getServiceRequest().getAttribute("MODULE");

		if ((module == null) || (LIST_CONOSCENZE_INFO_MODULE.equalsIgnoreCase(module)))
			return LIST_CONOSCENZE_INFO_PUB;

		if (LOAD_CONOSCENZA_INFO_MODULE.equalsIgnoreCase(module))
			return LOAD_CONOSCENZA_INFO_PUB;

		if (DELETE_CONOSCENZA_INFO_MODULE.equalsIgnoreCase(module))
			return DELETE_CONOSCENZA_INFO_PUB;

		if (INSERT_CONOSCENZA_INFO_MODULE.equalsIgnoreCase(module))
			return INSERT_CONOSCENZA_INFO_PUB;

		if (UPDATE_CONOSCENZA_INFO_MODULE.equalsIgnoreCase(module))
			return LIST_CONOSCENZE_INFO_PUB;

		if (LIST_DETTAGLI_CONOSCENZE_INFO_MODULE.equalsIgnoreCase(module))
			return LIST_DETTAGLI_CONOSCENZE_INFO_PUB;

		return "";
	}

}
