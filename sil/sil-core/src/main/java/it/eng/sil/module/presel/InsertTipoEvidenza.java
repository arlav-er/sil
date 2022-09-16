package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/*
 * 
 * @author rolfini
 *
 */

public class InsertTipoEvidenza extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		doInsertNoDuplicate(request, response);

	}

}
