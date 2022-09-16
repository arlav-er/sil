package it.eng.sil.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.AbstractAction;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.bean.Documento;

/**
 * @author vuoto
 * 
 */

public class Download extends AbstractAction {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Download.class.getName());
	private final String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {

		_logger.debug(className + "::service()");

		// recupero eventuali parametri...
		String prgDocumento = (String) request.getAttribute("PRGDOCUMENTO");
		String asAttachment = (String) request.getAttribute("asAttachment");

		// recupero gestione errori Protocolla Documento
		String erroreProtocollazione = (String) request.getAttribute("ErroreProtocollazione");
		String arraylistStringed = (String) request.getAttribute("warnings");

		if (!StringUtils.isEmpty(erroreProtocollazione)) {

			response.setAttribute("operationResult", "ERROR");
			response.setAttribute("errorCode", erroreProtocollazione);

		} else {

			// creazione nuovo documento
			Documento doc = new Documento();

			doc.setPrgDocumento(new BigDecimal(prgDocumento));
			try {
				doc.selectExtBlob();
				response.setAttribute("theDocument", doc);
				if (asAttachment == null)
					asAttachment = "false";
				response.setAttribute("asAttachment", asAttachment);
				request.delAttribute("apri");
				request.setAttribute("apri", "true");
				response.setAttribute("operationResult", "SUCCESS");

				if (!StringUtils.isEmpty(arraylistStringed)) {
					ArrayList warningReport = new ArrayList(
							Arrays.asList(arraylistStringed.substring(1, arraylistStringed.length() - 1).split(", ")));

					response.updAttribute("WARNINGREPORT", warningReport);
				}

			} catch (Exception ex) {
				response.setAttribute("operationResult", "ERROR");
				_logger.warn(className + "::service()\r\n" + ex);

			}

		}

	}

}