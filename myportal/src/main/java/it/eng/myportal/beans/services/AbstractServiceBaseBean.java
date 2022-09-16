package it.eng.myportal.beans.services;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.UtenteInfoDTO;
import it.eng.myportal.entity.home.UtenteInfoHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

/**
 * Classe astratta di base per i BackingBean delle pagine 'sicure'.
 * Il BackingBean si ocupa esclusivamente di controllare che l'utente sia autorizzato
 * all'accesso ai servizi al cittadino effettuando una redirect in caso di errore.
 * @author Rodi A.
 *
 */
public abstract class AbstractServiceBaseBean extends AbstractBaseBean {

	@EJB
	UtenteInfoHome utenteInfoHome;
		
	protected UtenteInfoDTO utenteInfo;
	
	
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		if (!getSession().isUtente() ||
			!getSession().getConnectedUtente().getAbilitatoServizi()) {
			addErrorMessage("services.denied");
			redirectHome();
		}
		utenteInfo = utenteInfoHome.findDTOById(getSession().getConnectedUtente().getId()); 
	}
	
	public UtenteInfoDTO getUtenteInfo() {
		return utenteInfo;
	}

	public void setUtenteInfo(UtenteInfoDTO utenteInfo) {
		this.utenteInfo = utenteInfo;
	}
}
