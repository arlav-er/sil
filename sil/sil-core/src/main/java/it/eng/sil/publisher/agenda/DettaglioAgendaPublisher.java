package it.eng.sil.publisher.agenda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.error.EMFErrorHandler;

/**
 * @author Paolo Cavaciocchi
 * 
 *         Publisher per la lista degli appuntamenti
 */
public class DettaglioAgendaPublisher implements com.engiweb.framework.presentation.PublisherDispatcherIFace {

	private static final String INSERT_DETTAGLIO_AGENDA = "INSERT_DETTAGLIO_AGENDA";
	private static final String ERRORE_AGENDA = "ERRORE_AGENDA";

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {

		EMFErrorHandler errorHandler = responseContainer.getErrorHandler();

		// EMFErrorHandler engErrorHandler = getErrorHandler();

		boolean noError = (errorHandler != null) && (errorHandler.isOK());

		if (noError) {
			return INSERT_DETTAGLIO_AGENDA;
		} else {
			return ERRORE_AGENDA;
		}

		/*
		 * SourceBean serviceResponse = responseContainer.getServiceResponse(); String a=serviceResponse.toXML();
		 * 
		 * SourceBean esito=(SourceBean) serviceResponse.getAttribute("INSERT_DETTAGLIO_AGENDA.USER_MESSAGE");
		 */
		// se esiste il true ESITODELETE si scegli come publisher il dettaglio
		// if(module_mode != null && module_mode.equals("DELETE")){
		/*
		 * }else{ return DETTAGLIO_AGENDA; }
		 */
	}

}// end of class
