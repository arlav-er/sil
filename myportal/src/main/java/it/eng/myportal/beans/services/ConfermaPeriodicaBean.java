package it.eng.myportal.beans.services;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * BackingBean per la pagina relativa al servizio "conferma periodica"
 * 
 * 
 * @author Enrico
 * 
 */
@ManagedBean
@ViewScoped
public class ConfermaPeriodicaBean extends AbstractServiceBaseBean {

	private boolean canLoadService = false;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		if (!utenteInfo.getAcceptedInformativaConfermaPeriodica()) {
			addErrorMessage("services.denied.conferma_periodica");
			redirectHome();
		} else {
			canLoadService = true;
		}
	}

	public boolean isCanLoadService() {
		return canLoadService;
	}

	public void setCanLoadService(boolean canLoadService) {
		this.canLoadService = canLoadService;
	}
}
