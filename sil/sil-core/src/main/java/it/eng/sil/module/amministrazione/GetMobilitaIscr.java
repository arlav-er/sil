package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.StatementFromPatto;

/**
 * 
 * Utilizzata in passato da .......... Ora gestisce la query dinamica per la associazione col patto
 */
public class GetMobilitaIscr extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		StatementFromPatto pt = new StatementFromPatto("am_mobilita_iscr", "AM_MB_IS", "prgMobilitaIscr");
		String cdnLav = (String) request.getAttribute("cdnLavoratore");
		pt.setParameter("cdnLavoratore", cdnLav);
		pt.addCondition("AND NVL (trunc(am_mobilita_iscr.datfine), trunc(SYSDATE)) >= trunc(SYSDATE)");
		enableMergeOnSelect(pt);
		doSelect(request, response);

	}
	// end service
}
// end class GetMobilitaIscr
