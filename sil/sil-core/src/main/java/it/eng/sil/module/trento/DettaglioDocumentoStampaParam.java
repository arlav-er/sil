package it.eng.sil.module.trento;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AbstractSimpleModule;

public class DettaglioDocumentoStampaParam extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DettaglioDocumentoStampaParam.class.getName());

	private final String className = StringUtils.getClassName(this);

	public void service(SourceBean request, SourceBean response) {

		_logger.debug(className + ".service() INIZIO");

		try {

			String prgDoc = (String) request.getAttribute("prgDocumento");

			SourceBean row = doSelect(request, response);

			if (row == null)
				throw new Exception("impossibile leggere le informazioni del documento");

			Documento doc = new Documento();
			doc.setPrgDocumento(new BigDecimal(prgDoc));
			doc.selectStampaParam();

			response.setAttribute("documento", doc);
			response.setAttribute("prgDocumento", doc.getPrgDocumento());

			SourceBeanUtils.setAttrIfFilled(response, "cdnLavoratore", request, "cdnLavoratore");
			SourceBeanUtils.setAttrIfFilled(response, "pagina", request, "pagina");
			SourceBeanUtils.setAttrIfFilled(response, "cdnFunzione", request, "cdnFunzione");

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);

		}
		_logger.debug(className + ".service() FINE");

	}

}