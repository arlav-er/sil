package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.StatementFromPatto;

public class GetLavoratoreEspLav extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {

		StatementFromPatto pt = new StatementFromPatto("PR_ESP_LAVORO", "EL", "PR_ESP_L", "PRGESPLAVORO");
		String cdnLav = (String) request.getAttribute("CDNLAVORATORE");
		pt.setParameter("cdnLavoratore", cdnLav);
		pt.neededMansione(true);
		enableMergeOnSelect(pt);
		doSelect(request, response);
	}
}