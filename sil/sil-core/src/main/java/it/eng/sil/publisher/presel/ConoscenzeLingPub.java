package it.eng.sil.publisher.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * Publisher Java per le Conoscenze Linguistiche.
 * 
 * @author Paolo Cavaciocchi
 */
public class ConoscenzeLingPub implements com.engiweb.framework.presentation.PublisherDispatcherIFace {

	// Nomi dei publisher
	private static final String LIST_CONOSCENZE_LING_PUB = "LIST_CONOSCENZE_LING_PUB";
	private static final String LOAD_CONOSCENZA_LING_PUB = "LOAD_CONOSCENZA_LING_PUB";
	private static final String DELETE_CONOSCENZA_LING_PUB = "DELETE_CONOSCENZA_LING_PUB";
	private static final String INSERT_CONOSCENZA_LING_PUB = "INSERT_CONOSCENZA_LING_PUB";
	private static final String UPDATE_CONOSCENZA_LING_PUB = "UPDATE_CONOSCENZA_LING_PUB";
	private static final String LIST_DETTAGLI_CONOSCENZE_LING_PUB = "LIST_DETTAGLI_CONOSCENZE_LING_PUB";
	private static final String VERIFICA_PRE_INSERT_CONOSCENZA_LING_PUB = "VERIFICA_PRE_INSERT_CONOSCENZA_LING_PUB";

	// Nomi dei moduli
	private static final String LIST_CONOSCENZE_LING_MODULE = "M_ListConoscenzeLing";
	private static final String LOAD_CONOSCENZA_LING_MODULE = "M_LoadConoscenzaLing";
	private static final String DELETE_CONOSCENZA_LING_MODULE = "M_DeleteConoscenzaLing";
	private static final String INSERT_CONOSCENZA_LING_MODULE = "M_InsertConoscenzaLing";
	private static final String UPDATE_CONOSCENZA_LING_MODULE = "M_UpdateConoscenzaLing";
	private static final String LIST_DETTAGLI_CONOSCENZE_LING_MODULE = "M_ListDettagliConoscenzaLing";
	private static final String VERIFICA_PRE_INSERT_CONOSCENZA_LING_MODULE = "M_VerificaPreInsertConoscenzaLing";

	private static final String LISTDETTAGLI_FROM_NUOVA_CONOLING = "LISTDETTAGLI_FROM_NUOVA_CONOLING";
	private static final String LISTDETTAGLI_FROM_MODIFICA_CONOLING = "LISTDETTAGLI_FROM_MODIFICA_CONOLING";

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {

		SourceBean serviceResponse = responseContainer.getServiceResponse();

		String module = (String) requestContainer.getServiceRequest().getAttribute("MODULE");

		if ((module == null) || (LIST_CONOSCENZE_LING_MODULE.equalsIgnoreCase(module)))
			return LIST_CONOSCENZE_LING_PUB;

		if (LOAD_CONOSCENZA_LING_MODULE.equalsIgnoreCase(module))
			return LOAD_CONOSCENZA_LING_PUB;

		if (DELETE_CONOSCENZA_LING_MODULE.equalsIgnoreCase(module))
			return DELETE_CONOSCENZA_LING_PUB;

		if (VERIFICA_PRE_INSERT_CONOSCENZA_LING_MODULE.equalsIgnoreCase(module))
			return VERIFICA_PRE_INSERT_CONOSCENZA_LING_PUB;

		if (INSERT_CONOSCENZA_LING_MODULE.equalsIgnoreCase(module))
			return INSERT_CONOSCENZA_LING_PUB;

		if (UPDATE_CONOSCENZA_LING_MODULE.equalsIgnoreCase(module)) {

			// Se l'update è andato a buon fine, passa alla lista
			// altrimenti rimane all'update
			String esitoUpdate = (String) serviceResponse.getAttribute(UPDATE_CONOSCENZA_LING_MODULE + ".UPDATE_OK");
			return "true".equalsIgnoreCase(esitoUpdate) ? LIST_CONOSCENZE_LING_PUB : UPDATE_CONOSCENZA_LING_PUB;
		}

		if (LIST_DETTAGLI_CONOSCENZE_LING_MODULE.equalsIgnoreCase(module))
			return LIST_DETTAGLI_CONOSCENZE_LING_PUB;

		return "";
	}

}
