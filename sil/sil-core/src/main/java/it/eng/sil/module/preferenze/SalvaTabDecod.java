package it.eng.sil.module.preferenze;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class SalvaTabDecod extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public SalvaTabDecod() {
	}

	public void service(SourceBean request, SourceBean response) {

		if (request.containsAttribute("NUOVO")) {
			doDynamicInsert(request, response);

		} else if (request.containsAttribute("SALVA")) {
			doDynamicUpdate(request, response);
		}
	}

}
