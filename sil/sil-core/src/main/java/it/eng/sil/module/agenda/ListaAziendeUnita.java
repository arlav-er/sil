package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFErrorHandler;

public class ListaAziendeUnita extends AbstractModule {
	public ListaAziendeUnita() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		EMFErrorHandler engErrorHandler = getErrorHandler();
	}
}