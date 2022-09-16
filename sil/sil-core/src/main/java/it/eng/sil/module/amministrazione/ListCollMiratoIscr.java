package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.StatementFromPatto;

public class ListCollMiratoIscr extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		StatementFromPatto pt = new StatementFromPatto("AM_CM_ISCR", "I", "AM_CM_IS", "prgcmiscr");
		pt.addCondition("AND I.DATDATAFINE is null");
		String cdnLav = (String) request.getAttribute("CDNLAVORATORE");
		pt.setParameter("cdnLavoratore", cdnLav);
		enableMergeOnSelect(pt);
		doSelect(request, response);
	}
}