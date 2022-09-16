/*
 * Creato il 26-ott-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module;

public class ContestoComune extends com.engiweb.framework.dispatching.service.DefaultRequestContext {
	private com.engiweb.framework.dispatching.action.AbstractAction action;
	private com.engiweb.framework.dispatching.module.AbstractModule module;
	private com.engiweb.framework.dispatching.action.AbstractHttpAction httpAction;

	public ContestoComune(com.engiweb.framework.dispatching.module.AbstractModule module) {
		this.module = module;
	}

	public ContestoComune(com.engiweb.framework.dispatching.action.AbstractAction action) {
		this.action = action;
	}

	public ContestoComune(com.engiweb.framework.dispatching.action.AbstractHttpAction httpAction) {
		this.httpAction = httpAction;
	}

	public com.engiweb.framework.base.RequestContainer getRequestContainer() {
		if (action != null)
			return action.getRequestContainer();
		else if (module != null)
			return module.getRequestContainer();
		else
			return httpAction.getRequestContainer();
	}

	public com.engiweb.framework.base.ResponseContainer getResponseContainer() {
		if (action != null)
			return action.getResponseContainer();
		else if (module != null)
			return module.getResponseContainer();
		else
			return httpAction.getResponseContainer();
	}

	public void setRequestContext(com.engiweb.framework.dispatching.service.RequestContextIFace requestContext) {

	}

	public com.engiweb.framework.error.EMFErrorHandler getErrorHandler() {
		if (action != null)
			return action.getErrorHandler();
		else if (module != null)
			return module.getErrorHandler();
		else
			return httpAction.getErrorHandler();
	}

	public com.engiweb.framework.base.SourceBean getServiceRequest() {
		if (action != null)
			return action.getServiceRequest();
		else if (module != null)
			return module.getServiceRequest();
		else
			return httpAction.getServiceRequest();
	}

	public com.engiweb.framework.base.SourceBean getServiceResponse() {
		if (action != null)
			return action.getServiceResponse();
		else if (module != null)
			return module.getServiceResponse();
		else
			return httpAction.getServiceResponse();
	}

	public com.engiweb.framework.base.SourceBean getConfig() {
		if (action != null)
			return action.getConfig();
		else if (module != null)
			return module.getConfig();
		else
			return httpAction.getConfig();
	}
}