package it.eng.sil.module.ido;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;

public class InserisciContAuto extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		String numRichiesta = request.getAttribute("NUMRICHIESTA") != null
				? request.getAttribute("NUMRICHIESTA").toString()
				: "";
		String numAnnoRich = request.getAttribute("NUMANNO") != null ? request.getAttribute("NUMANNO").toString() : "";
		BigDecimal numRichiestaOrig = null;

		try {
			if (!numRichiesta.equals("") && !numAnnoRich.equals("")) {
				this.setSectionQuerySelect("GET_RICHIETA_ORIG");
				SourceBean rowRich = doSelect(request, response);
				if (rowRich != null) {
					rowRich = rowRich.containsAttribute("ROW") ? (SourceBean) rowRich.getAttribute("ROW") : rowRich;
					numRichiestaOrig = (BigDecimal) rowRich.getAttribute("NUMRICHIESTAORIG");
					if (numRichiestaOrig != null) {
						request.delAttribute("NUMRICHIESTA");
						request.setAttribute("NUMRICHIESTA", numRichiestaOrig);
					}
				}
			}

			doInsert(request, response);

			if (numRichiestaOrig != null && !numRichiesta.equals("")) {
				request.delAttribute("NUMRICHIESTA");
				request.setAttribute("NUMRICHIESTA", numRichiesta);
			}
		} catch (Exception e) {
			if (numRichiestaOrig != null && !numRichiesta.equals("")) {
				request.delAttribute("NUMRICHIESTA");
				request.setAttribute("NUMRICHIESTA", numRichiesta);
			}
		}
	}
}