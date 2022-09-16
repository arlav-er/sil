package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.sil.bean.Documento;

/**
 * @author vuoto
 */

public class DettagliDocumento extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DettagliDocumento.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		String prgDoc = (String) request.getAttribute("prgDocumento");

		Documento doc = new Documento();
		doc.setPrgDocumento(new BigDecimal(prgDoc));
		try {

			doc.select();
			response.setAttribute("documento", doc);

			String cdnLavoratore = (String) request.getAttribute("cdnLavoratore");
			String cdfFunzione = (String) request.getAttribute("CDNFUNZIONE");

			// Parametri non recuperabili in altro modo nella jsp
			// per via del multipart...
			response.setAttribute("cdnLavoratore", cdnLavoratore);
			response.setAttribute("CDNFUNZIONE", cdfFunzione);

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);

		}

	}

}