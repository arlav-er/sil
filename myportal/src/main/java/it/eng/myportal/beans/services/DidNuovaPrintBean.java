package it.eng.myportal.beans.services;

import it.eng.myportal.dtos.ConferimentoDidDTO;
import it.eng.myportal.dtos.DeCondizioneOccupazMinDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.home.ConferimentoDidHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeCondizioneOccupazMinHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "didNuovaPrintBean")
@ViewScoped
public class DidNuovaPrintBean {

	@EJB
	private ConferimentoDidHome conferimentoDidHome;

	@EJB
	private DeCpiHome deCpiHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private DeCondizioneOccupazMinHome deCondizioneOccupazMinHome;

	private final static String COD_CITTADINANZA_MIN_ITALIANA = "A02";

	private ConferimentoDidDTO did;
	private DeCpiDTO entePromotore;
	private UtenteCompletoDTO utenteCompleto;

	@PostConstruct
	public void postConstruct() {
		String idConferimentoDidString = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("idConferimentoDid");

		if (idConferimentoDidString != null) {
			did = conferimentoDidHome.findDTOById(Integer.parseInt(idConferimentoDidString));
			entePromotore = deCpiHome.findDTOByCodMin(did.getCodEntePromotore());
			setUtenteCompleto(utenteInfoHome.findDTOCompletoById(did.getIdPfPrincipal()));
		}
	}

	public String getCondizioneOccupazionaleCalcDesc() {
		DeCondizioneOccupazMinDTO dto = deCondizioneOccupazMinHome.findDTOById(did.getCondizioneOccupazCalc());
		if (dto != null) {
			return dto.getDescrizione();
		} else {
			return "";
		}
	}

	/** Mostro la DeCittadinanzaMin solo se la cittadinanza non è italiana */
	public boolean showCittadinanzaMin() {
		return (did.getDeCittadinanzaMinDTO() != null && !did.getDeCittadinanzaMinDTO().getId()
				.equals(COD_CITTADINANZA_MIN_ITALIANA));
	}

	public ConferimentoDidDTO getDid() {
		return did;
	}

	public void setDid(ConferimentoDidDTO did) {
		this.did = did;
	}

	public DeCpiDTO getEntePromotore() {
		return entePromotore;
	}

	public void setEntePromotore(DeCpiDTO entePromotore) {
		this.entePromotore = entePromotore;
	}

	public UtenteCompletoDTO getUtenteCompleto() {
		return utenteCompleto;
	}

	public void setUtenteCompleto(UtenteCompletoDTO utenteCompleto) {
		this.utenteCompleto = utenteCompleto;
	}

}
