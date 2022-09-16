package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class ListAssMansioni extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ListAssMansioni.class.getName());

	public void service(SourceBean request, SourceBean response) {
		String pool = getPool();

		setSectionQuerySelect("QUERY_SELECT_DE_REL_MANSIONE");
		SourceBean sbMansioni = doSelect(request, response, false);
		java.util.Vector vettMansioni = sbMansioni.getAttributeAsVector("ROW");

		try {
			if (request.getAttribute("CODMANSIONE") != null) {
				while (request.containsAttribute("CODMANSIONE"))
					request.delAttribute("CODMANSIONE");
			}
			if (request.getAttribute("CODGRUPPO") != null) {
				while (request.containsAttribute("CODGRUPPO"))
					request.delAttribute("CODGRUPPO");
			}
			request.setAttribute("CODMANSIONE", "");

			for (int i = 0; i < vettMansioni.size(); i++) {
				SourceBean sbMansione = (SourceBean) vettMansioni.get(i);
				request.updAttribute("CODMANSIONE", sbMansione.getAttribute("CODMANSIONE"));

				setSectionQuerySelect("QUERY_SELECT_DE_REL_MANSIONE_SIMILI");
				SourceBean sbGruppoAssociato = doSelect(request, response, false);
				sbMansione.setAttribute("GRUPPO_ASSOCIATO", sbGruppoAssociato);
			}
		} catch (Exception ex) {
			_logger.debug("sil.module.presel.ListAssMansioni" + "::Errore in setAttribute" + ex.getMessage());

			ex.printStackTrace();
		}

		try {
			response.setAttribute("MANSIONI", sbMansioni);
		} catch (Exception ex) {
			_logger.debug("sil.module.presel.ListAssMansioni" + "::Errore in setAttribute" + ex.getMessage());

			ex.printStackTrace();
		}
	}
}