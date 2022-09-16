package it.eng.myportal.beans.print.fbconvenzione;

import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.FbConvenzioneDTO;
import it.eng.myportal.dtos.FbConvenzioneSedeDTO;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.FbConvenzioneHome;
import it.eng.myportal.entity.home.FbConvenzioneSedeHome;
import it.eng.myportal.entity.home.UtenteInfoHome;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "fbConvenzionePrintBean")
@ViewScoped
public class FbConvenzionePrintBean {

	@EJB
	FbConvenzioneHome fbConvenzioneHome;

	@EJB
	FbConvenzioneSedeHome fbConvenzioneSedeHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	private FbConvenzioneDTO fbConvenzioneDTO;
	private AziendaInfoDTO aziendaInfoDTO;
	private List<FbConvenzioneSedeDTO> fbConvenzioneSediList;

	@PostConstruct
	public void postConstruct() {
		String idFbConvenzioneString = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("idFbConvenzione");

		if (idFbConvenzioneString != null) {
			fbConvenzioneDTO = fbConvenzioneHome.findDTOById(Integer.parseInt(idFbConvenzioneString));
		}

		if (fbConvenzioneDTO != null && fbConvenzioneDTO.getIdPrincipalIns() != null) {
			aziendaInfoDTO = aziendaInfoHome.findDTOById(fbConvenzioneDTO.getIdPrincipalIns());
			fbConvenzioneSediList = fbConvenzioneSedeHome.findByConvenzioneId(fbConvenzioneDTO.getId());
		}
	}

	public boolean hasSedeLegale() {
		return aziendaInfoDTO != null && aziendaInfoDTO.getSedeLegale() != null
				&& aziendaInfoDTO.getSedeLegale().getIndirizzo() != null
				&& aziendaInfoDTO.getSedeLegale().getComune() != null
				&& aziendaInfoDTO.getSedeLegale().getCap() != null;
	}

	public FbConvenzioneDTO getFbConvenzione() {
		return fbConvenzioneDTO;
	}

	public void setFbConvenzione(FbConvenzioneDTO fbConvenzioneDTO) {
		this.fbConvenzioneDTO = fbConvenzioneDTO;
	}

	public AziendaInfoDTO getAziendaInfoDTO() {
		return aziendaInfoDTO;
	}

	public void setAziendaInfoDTO(AziendaInfoDTO aziendaInfoDTO) {
		this.aziendaInfoDTO = aziendaInfoDTO;
	}

	public List<FbConvenzioneSedeDTO> getFbConvenzioneSediList() {
		return fbConvenzioneSediList;
	}

	public void setFbConvenzioneSediList(List<FbConvenzioneSedeDTO> fbConvenzioneSediList) {
		this.fbConvenzioneSediList = fbConvenzioneSediList;
	}
}
