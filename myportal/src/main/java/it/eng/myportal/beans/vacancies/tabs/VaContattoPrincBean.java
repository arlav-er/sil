package it.eng.myportal.beans.vacancies.tabs;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpServletRequest;

import it.eng.myportal.beans.ICheckCliclavoro;
import it.eng.myportal.dtos.VaContattoDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.dtos.VaPubblicazioneDTO;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaPubblicazioneHome;
import it.eng.myportal.entity.home.VaVacancyClHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.local.IVaContattoHome;
import it.eng.myportal.entity.home.local.IVacancyEntityHome;

/**
 * Bean della tab "Contatto Principale" dell'inserimento/modifica vacancy
 * 
 * @author Enrico D'Angelo
 * 
 */
@ManagedBean
@ViewScoped
public class VaContattoPrincBean extends AbstractVacancyTabBean<VaContattoDTO> implements ICheckCliclavoro {

	/**
	 * Injection degli EJB che mi servono a recuperare i dati dal DB
	 */
	@EJB
	IVaContattoHome vaContattoHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	VaVacancyClHome vaVacancyClHome;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	VaPubblicazioneHome vaPubblicazioneHome;

	private VaPubblicazioneDTO vaPubblicazioneDTO;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		vaPubblicazioneDTO = vaPubblicazioneHome.findDTOByVacancyId(vacancyId);
		if (vaPubblicazioneDTO == null) {
			vaPubblicazioneDTO = new VaPubblicazioneDTO();
			vaPubblicazioneDTO.setDtmIns(new Date());
			vaPubblicazioneDTO.setDtmMod(new Date());
			vaPubblicazioneDTO.setIdPrincipalIns(this.getSession().getPrincipalId());
			vaPubblicazioneDTO.setIdPrincipalMod(this.getSession().getPrincipalId());
			vaPubblicazioneDTO.setIdVaDatiVacancy(vacancyId);
		}
	}

	@Override
	protected void saveData() {
		super.saveData();
		if (vaPubblicazioneDTO.getId() != null) {
			vaPubblicazioneDTO = vaPubblicazioneHome.mergeDTO(vaPubblicazioneDTO, getSession().getPrincipalId());
		} else {
			vaPubblicazioneDTO = vaPubblicazioneHome.persistDTO(vaPubblicazioneDTO, getSession().getPrincipalId());
		}
	}

	@Override
	protected void updateData() {
		super.updateData();
		if (vaPubblicazioneDTO.getId() != null) {
			vaPubblicazioneDTO = vaPubblicazioneHome.mergeDTO(vaPubblicazioneDTO, getSession().getPrincipalId());
		} else {
			vaPubblicazioneDTO = vaPubblicazioneHome.persistDTO(vaPubblicazioneDTO, getSession().getPrincipalId());
		}
	}

	@Override
	public VaContattoDTO buildNewDataIstance() {
		return new VaContattoDTO();
	}

	@Override
	public IVacancyEntityHome<VaContattoDTO> getHome() {
		return vaContattoHome;
	}

	public void checkDatiCliclavoroListener(ComponentSystemEvent event) {
		vacancyBean.checkDatiCliclavoro(event, this);
	}

	@Override
	public boolean checkDatiCliclavoroSpecifico(ComponentSystemEvent event) {
		VaDatiVacancyDTO vaDatiVacancyDTO = vaDatiVacancyHome.findDTOById(vacancyId);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		/*
		 * ritorna true se ho cliccato sul pulsante "Cancella". Fa schifo, lo so!
		 */
		boolean deleteAction = ((HttpServletRequest) facesContext.getExternalContext().getRequest()).getParameterMap()
				.get("javax.faces.partial.execute")[0].contains("deleteButton");

		UIComponent components = event.getComponent();

		UIInput emailComponent = (UIInput) components.findComponent("contatto_principale:mail:inputText");
		String email = (String) emailComponent.getValue();

		if (!vaDatiVacancyDTO.isOriginariaSIL()
				|| (vaDatiVacancyDTO.isOriginariaSIL() && vaDatiVacancyDTO.getIdPfPrincipalAziendaPalese() == null && !vaDatiVacancyDTO
						.getVisibilita())) {
			if (deleteAction || email == null || email.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getCheckMessageCliclavoro() {
		return "cliclavoro.va.warn.email_contatto";
	}

	public VaPubblicazioneDTO getVaPubblicazioneDTO() {
		return vaPubblicazioneDTO;
	}

	public void setVaPubblicazioneDTO(VaPubblicazioneDTO vaPubblicazioneDTO) {
		this.vaPubblicazioneDTO = vaPubblicazioneDTO;
	}
	
	@Override
	public boolean isShowDeleteButton() {
		VaDatiVacancyDTO vaDatiVacancyDTO = vaDatiVacancyHome.findDTOById(vacancyId);
		if(utils.isPAT() && CodStatoVacancyEnum.PUB.equals(vaDatiVacancyDTO.getCodStatoVacancyEnum())){
			return false;
		}else{
			return showDeleteButton;
		}
	}
}
