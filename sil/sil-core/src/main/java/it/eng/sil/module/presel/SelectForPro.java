package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.StatementFromPatto;

/**
 * Description of the Class
 * 
 * @author cavaciocchi
 */
public class SelectForPro extends AbstractSimpleModule {
	/**
	 * Description of the Method
	 * 
	 * @param request
	 *            Description of the Parameter
	 * @param response
	 *            Description of the Parameter
	 */
	public void service(SourceBean request, SourceBean response) {

		StatementFromPatto pt = new StatementFromPatto("PR_CORSO", "C", "PR_COR", "PRGCORSO");
		String cdnLav = (String) request.getAttribute("CDNLAVORATORE");
		pt.setParameter("cdnLavoratore", cdnLav);
		enableMergeOnSelect(pt);
		doSelect(request, response);
	}
}
