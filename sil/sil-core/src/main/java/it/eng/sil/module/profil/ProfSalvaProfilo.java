package it.eng.sil.module.profil;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class ProfSalvaProfilo extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		doUpdate(request, response);
	}
}// class ProfSalvaProfilo
