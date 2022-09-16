package it.eng.sil.module.documenti;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.bean.Documento;

/**
 * @author vuoto / Antenucci
 */
public class NuovoDocumento extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(NuovoDocumento.class.getName());

	private final String className = StringUtils.getClassName(this);

	public void service(SourceBean request, SourceBean response) {

		_logger.debug(className + ".service() INIZIO");

		try {

			Documento doc = new Documento();

			response.setAttribute("documento", doc);

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
			SourceBeanUtils.setAttrIfFilled(response, "strChiaveTabella", request, "strChiaveTabella");

			response.setAttribute("QUERY_STRING", "");
			// GG 13/1/05 - "QUERY_STRING" non definito: serve per sapere che si
			// è partiti da
			// una pagina di NUOVO e quindi si dovrà "ricreare" la queryString
			// di dettaglio!

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);

		}
		_logger.debug(className + ".service() FINE");

	}

}