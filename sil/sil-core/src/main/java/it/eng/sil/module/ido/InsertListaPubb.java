package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;

public class InsertListaPubb extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		setSectionQuerySelect("QUERY_NEXT_VAL");
		SourceBean row = doSelect(request, response, false);
		request.setAttribute("PRGELENCOGIORNALE", row.getAttribute("row.nextval"));
		setSectionQueryInsert("QUERY_INSERT");
		setSectionQuerySelect("QUERY_SELECT");
		doInsertNoDuplicate(request, response);
		response.setAttribute("PRGELENCOGIORNALE", row.getAttribute("row.nextval"));
	}
}