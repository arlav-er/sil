package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.StatementFromPatto;

public class SelectIndisp extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		StatementFromPatto pt = new StatementFromPatto("PR_INDISPONIBILITA", "PR_IND", "PRGINDISPONIBILITA");
		String cdnLav = (String) request.getAttribute("CDNLAVORATORE");
		pt.setParameter("cdnLavoratore", cdnLav);
		enableMergeOnSelect(pt);
		doSelect(request, response);
	}
}