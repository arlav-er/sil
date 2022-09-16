package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.StatementFromPatto;

public class GetLavoratoreTitoli extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {

		StatementFromPatto pt = new StatementFromPatto("PR_STUDIO", "prs", "PR_STU", "prgstudio");
		String cdnLav = (String) request.getAttribute("CDNLAVORATORE");
		pt.setParameter("cdnLavoratore", cdnLav);
		enableMergeOnSelect(pt);
		doSelect(request, response);
	}
}