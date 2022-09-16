package it.eng.sil.module.anag;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

public class UltimaAdesioneSistema extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {
		SourceBean row = doSelect(request, response);
		String dataAdesioneSistema = Utils.notNull(row.getAttribute("row.datLastAdesione"));
		if (!dataAdesioneSistema.equals("")) {
			RequestContainer.getRequestContainer().setAttribute("YG_DATA_ADESIONE_SISTEMA", dataAdesioneSistema);
		}
	}
}