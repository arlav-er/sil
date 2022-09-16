package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class DeleteAssegnazioneModule extends AbstractSimpleModule {
	public DeleteAssegnazioneModule() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		doDelete(request, response);
	}
}