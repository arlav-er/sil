package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.StatementFromPatto;

public class GetExPermSoggiorno extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		StatementFromPatto pt = new StatementFromPatto("am_ex_perm_sogg", "AM_EX_PS", "prgPermSogg");
		// pt.addCondition("");
		String cdnLav = (String) request.getAttribute("cdnLavoratore");
		pt.setParameter("cdnLavoratore", cdnLav);
		enableMergeOnSelect(pt);
		doSelect(request, response);
	}
}