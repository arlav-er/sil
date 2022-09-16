package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class CMProspAnnulla extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		this.setSectionQueryUpdate("QUERY_UPDATE");
		doUpdate(serviceRequest, serviceResponse);

	}
}
