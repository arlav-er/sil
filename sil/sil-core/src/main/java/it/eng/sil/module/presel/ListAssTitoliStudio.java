package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class ListAssTitoliStudio extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ListAssTitoliStudio.class.getName());

	public void service(SourceBean request, SourceBean response) {
		String pool = getPool();

		setSectionQuerySelect("QUERY_SELECT_DE_REL_TITOLO");
		SourceBean sbTitoli = doSelect(request, response, false);
		java.util.Vector vettTitoli = sbTitoli.getAttributeAsVector("ROW");

		try {
			if (request.getAttribute("CODTITOLO") != null) {
				while (request.containsAttribute("CODTITOLO"))
					request.delAttribute("CODTITOLO");
			}
			if (request.getAttribute("CODTITOLOSIMILE") != null) {
				while (request.containsAttribute("CODTITOLOSIMILE"))
					request.delAttribute("CODTITOLOSIMILE");
			}
			request.setAttribute("CODTITOLO", "");

			for (int i = 0; i < vettTitoli.size(); i++) {
				SourceBean sbTitolo = (SourceBean) vettTitoli.get(i);
				request.updAttribute("CODTITOLO", sbTitolo.getAttribute("CODTITOLO"));

				setSectionQuerySelect("QUERY_SELECT_DE_REL_TITOLO_SIMILI");
				SourceBean sbTitoliSimili = doSelect(request, response, false);
				sbTitolo.setAttribute("TITOLI_SIMILI", sbTitoliSimili);
			}
		} catch (Exception ex) {
			_logger.debug("sil.module.presel.ListAssTitoliStudio" + "::Errore in setAttribute" + ex.getMessage());

			ex.printStackTrace();
		}

		try {
			response.setAttribute("TITOLI", sbTitoli);
		} catch (Exception ex) {
			_logger.debug("sil.module.presel.ListAssTitoliStudio" + "::Errore in setAttribute" + ex.getMessage());

			ex.printStackTrace();
		}

	}
}