package it.eng.myportal.beans.services;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * BackingBean per la pagina relativa allo stato occupazionale.
 * Lo stato occupazionale non viene ricercato all'interno del BackingBean
 * ma attraverso il servizio REST 'StatoOccupazionaleRest' una volta caricata la pagina.
 * 
 * @author Rodi A.
 *
 */
@ManagedBean
@ViewScoped
public class StatoOccupazionaleBean extends AbstractServiceBaseBean {
	
	private boolean canLoadService = false;
			
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		if (!utenteInfo.getAcceptedInformativaStatoOcc()) {
			addErrorMessage("services.denied.stato_occ");
			redirectHome();
		}
		else {
			canLoadService = true;
		}
	}

	public boolean isCanLoadService() {
		return canLoadService;
	}

	public void setCanLoadService(boolean canLoadService) {
		this.canLoadService = canLoadService;
	}
	
	public void methodVoid() {
		log.debug("INFO: serve per la popup come metodo void della action!!!");
	}
}
