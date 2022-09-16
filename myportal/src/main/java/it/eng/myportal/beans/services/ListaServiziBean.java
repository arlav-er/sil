package it.eng.myportal.beans.services;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.myportal.dtos.UtenteInfoDTO;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * BackingBean per la pagina relativa allo stato occupazionale. Lo stato occupazionale non viene ricercato all'interno
 * del BackingBean ma attraverso il servizio REST 'StatoOccupazionaleRest' una volta caricata la pagina.
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class ListaServiziBean extends AbstractServiceBaseBean {

	private boolean canLoadService = false;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		canLoadService = true;
		utenteInfo = utenteInfoHome.findDTOById(getSession().getConnectedUtente().getId());
	}

	public boolean isCanLoadService() {
		return canLoadService;
	}

	public void setCanLoadService(boolean canLoadService) {
		this.canLoadService = canLoadService;
	}

	public String acceptInformativaStatoOcc() {
		utenteInfo.setAcceptedInformativaStatoOcc(true);
		homeMerge(utenteInfoHome, utenteInfo);
		return "servizi_stato_occ";
	}
	

	public String acceptInformativaPercLav() {
		utenteInfo.setAcceptedInformativaPercLav(true);
		homeMerge(utenteInfoHome, utenteInfo);
		return "servizi_perc_lav";
	}

	public String acceptInformativaDid() {
		utenteInfo.setAcceptedInformativaDid(true);
		homeMerge(utenteInfoHome, utenteInfo);
		redirect("/faces/secure/utente/servizi/did_nuova/did_list.xhtml");
		return null;
	}

	public String acceptInformativaConfermaPeriodica() {
		utenteInfo.setAcceptedInformativaConfermaPeriodica(true);
		homeMerge(utenteInfoHome, utenteInfo);
		return "servizi_conferma_periodica";
	}

	public String acceptInformativaRinnovoPatto() {
		utenteInfo.setAcceptedInformativaRinnovoPatto(true);
		homeMerge(utenteInfoHome, utenteInfo);
		return "servizi_rinnovo_patto";
	}

	public boolean showMessaggioUmbria() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA);
	}

	public UtenteInfoDTO getUtenteInfo() {
		return utenteInfo;
	}

	public void setUtenteInfo(UtenteInfoDTO utenteInfo) {
		this.utenteInfo = utenteInfo;
	}
}
