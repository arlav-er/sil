package it.eng.myportal.beans.services;

import it.eng.myportal.dtos.WsStampaDTO;
import it.eng.myportal.entity.home.WsStampaHome;
import it.eng.myportal.enums.TipoStampa;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class RinnovoPattoBean extends AbstractServiceBaseBean {

	private boolean canLoadService = false;

	private List<WsStampaDTO> stampe = new ArrayList<WsStampaDTO>();

	@EJB
	private WsStampaHome wsStampaHome;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		// if (!utenteInfo.getAcceptedInformativaRinnovoPatto()) {
		// addErrorMessage("services.denied.rinnovo_patto");
		// redirectHome();
		// } else {
		canLoadService = true;
		stampe = wsStampaHome.getStampeDTO(TipoStampa.RINNOVO_PATTO.getCodice(), getSession().getPrincipalId());
		// }
	}

	public boolean isCanLoadService() {
		return canLoadService;
	}

	public void setCanLoadService(boolean canLoadService) {
		this.canLoadService = canLoadService;
	}

	public List<WsStampaDTO> getStampe() {
		return stampe;
	}

	public void setStampe(List<WsStampaDTO> stampe) {
		this.stampe = stampe;
	}
}
