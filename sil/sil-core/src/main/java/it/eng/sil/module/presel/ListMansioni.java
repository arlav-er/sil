package it.eng.sil.module.presel;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.StatementFromPatto;

public class ListMansioni extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		StatementFromPatto pt = new StatementFromPatto("pr_mansione", "man", "PR_MAN", "prgmansione");
		// pt.addCondition("");
		String cdnLav = (String) request.getAttribute("CDNLAVORATORE");
		pt.setParameter("cdnLavoratore", cdnLav);
		enableMergeOnSelect(pt);
		setSectionQuerySelect("LOAD_MANSIONI");
		doSelect(request, response);
		disableMergeOnSelect();
		Vector beanVec = (Vector) response.getAttributeAsVector("ROWS.ROW");
		// conservo il prgMansione originale
		String prgMansione = (String) request.getAttribute("PRGMANSIONE");
		if (prgMansione == null)
			request.setAttribute("PRGMANSIONE", ""); // imposta prgMansione
														// fittizio
		for (int i = 0; i < beanVec.size(); i++) {
			SourceBean beanRow = (SourceBean) beanVec.elementAt(i);
			request.updAttribute("PRGMANSIONE", beanRow.getAttribute("PRGMANSIONE"));
			this.setSectionQuerySelect("GET_ESP_COLLEGATA");
			SourceBean res = this.doSelect(request, response, false);
			Vector vec = (Vector) res.getAttributeAsVector("ROW");
			if (!vec.isEmpty()) {
				beanRow.setAttribute("ESP_COLLEGATA", "TRUE");
			} else {
				beanRow.setAttribute("ESP_COLLEGATA", "FALSE");
			}
		}
		if (prgMansione != null) {
			request.updAttribute("PRGMANSIONE", prgMansione);
		} else {
			request.delAttribute("PRGMANSIONE");
		}

	}
}