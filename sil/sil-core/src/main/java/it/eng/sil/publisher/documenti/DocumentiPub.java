package it.eng.sil.publisher.documenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;

/**
 * Publisher Java per i documenti
 */
public class DocumentiPub implements com.engiweb.framework.presentation.PublisherDispatcherIFace {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DocumentiPub.class.getName());

	// Nomi dei publisher
	private static final String DOWLOAD_FILE = "REPORT_DOWNLOAD";
	private static final String ESITO_OPERAZIONE = "ESITO_OPERAZIONE_DOC";

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {
		SourceBean serviceResponse = responseContainer.getServiceResponse();
		SourceBean request = requestContainer.getServiceRequest();

		String opResult = StringUtils.getAttributeStrNotNull(serviceResponse, "operationResult");
		String apri = StringUtils.getAttributeStrNotNull(request, "apri");

		/*
		 * try { if(opResult.equals("")) opResult="SUCCESS"; serviceResponse.setAttribute("operationResult",opResult); }
		 * catch(Exception ex) it.eng.sil.util.TraceWrapper.debug( _logger,this.getClass().getName() +
		 * "::getPublisherName()", ex); }
		 */

		if (apri.equalsIgnoreCase("true") && opResult.equalsIgnoreCase("SUCCESS"))
			return DOWLOAD_FILE;
		else
			return ESITO_OPERAZIONE;
	}

}// class DocumentiPub
