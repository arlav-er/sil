package it.eng.sil.module.documenti;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.bean.Documento;

/**
 * @author vuoto / Antenucci
 */

public class DettagliDocumento extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DettagliDocumento.class.getName());

	private final String className = StringUtils.getClassName(this);

	public void service(SourceBean request, SourceBean response) {

		_logger.debug(className + ".service() INIZIO");
		String prgDoc = (String) request.getAttribute("prgDocumento");

		Documento doc = new Documento();
		doc.setPrgDocumento(new BigDecimal(prgDoc));
		try {

			doc.select();
			response.setAttribute("documento", doc);
			response.setAttribute("prgDocumento", doc.getPrgDocumento());

			// Parametri non recuperabili in altro modo nella jsp
			// per via del multipart...

			SourceBeanUtils.setAttrIfFilled(response, "cdnLavoratore", request, "cdnLavoratore");
			SourceBeanUtils.setAttrIfFilled(response, "prgAzienda", request, "prgAzienda");
			SourceBeanUtils.setAttrIfFilled(response, "prgUnita", request, "prgUnita");
			SourceBeanUtils.setAttrIfFilled(response, "lookLavoratore", request, "lookLavoratore");
			SourceBeanUtils.setAttrIfFilled(response, "lookAzienda", request, "lookAzienda");
			SourceBeanUtils.setAttrIfFilled(response, "contesto", request, "contesto");
			SourceBeanUtils.setAttrIfFilled(response, "pagina", request, "pagina");
			SourceBeanUtils.setAttrIfFilled(response, "popUp", request, "popUp");
			SourceBeanUtils.setAttrIfFilled(response, "cdnFunzione", request, "cdnFunzione");
			SourceBeanUtils.setAttrIfFilled(response, "goBackListPage", request, "goBackListPage");

			// Il parametro "QUERY_STRING" non è definito, così nella JSP sarà
			// impostato
			// con il contenuto corrente della queryString della pagina.

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);

		}
		_logger.debug(className + ".service() FINE");

	}

}