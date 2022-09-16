package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.StatementFromPatto;

public class LoadMansione extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		StatementFromPatto pt = new StatementFromPatto("pr_mansione", "ma", "PR_MAN", "prgmansione");
		String cdnLav = (String) request.getAttribute("CDNLAVORATORE");
		pt.setParameter("cdnLavoratore", cdnLav);
		enableMergeOnSelect(pt);
		doSelect(request, response);
	}
}