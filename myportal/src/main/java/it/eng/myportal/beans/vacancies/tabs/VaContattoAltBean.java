package it.eng.myportal.beans.vacancies.tabs;

import it.eng.myportal.beans.ICheckCliclavoro;
import it.eng.myportal.dtos.VaContattoDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaVacancyClHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.local.IVaContattoHome;
import it.eng.myportal.entity.home.local.IVacancyEntityHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpServletRequest;

/**
 * Bean della tab "Contatto Alternativo" dell'inserimento/modifica vacancy
 * 
 * @author Enrico D'Angelo
 * 
 */
@ManagedBean
@ViewScoped
public class VaContattoAltBean extends AbstractVacancyTabBean<VaContattoDTO> implements ICheckCliclavoro {

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

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		if (vaContattoHome.findDTOByVacancyId(vacancyId) != null) {
			data.setActive(true);
		} else {
			data = new VaContattoDTO();
			data.setActive(false);
		}

		/*
		 * Se il contatto alternativo non e' attivo (cioe' non e' stato
		 * specificato un contatto principale) configuro lapagina affinche' non
		 * mostri i pulsanti
		 */
		if (!data.getActive()) {
			saved = false;
			editing = false;
			addInfoMessage("vacancy.contact_required");
		}
	}

	/**
	 * Costruisco un'istaziona vuota del DTO
	 * 
	 * @return VaCompetenzeTrasvDTO
	 */
	@Override
	public VaContattoDTO buildNewDataIstance() {
		return new VaContattoDTO();
	}

	@Override
	protected VaContattoDTO retrieveData() {
		return vaContattoHome.findAltDTOByVacancyId(vacancyId);
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
		 * ritorna true se ho cliccato sul pulsante "Cancella". Fa schifo, lo
		 * so!
		 */
		boolean deleteAction = ((HttpServletRequest) facesContext.getExternalContext().getRequest()).getParameterMap()
				.get("javax.faces.partial.execute")[0].contains("deleteButton");

		UIComponent components = event.getComponent();

		UIInput emailComponent = (UIInput) components.findComponent("contatto_alternativo:mail:inputText");
		String email = (String) emailComponent.getValue();

		if (vaDatiVacancyDTO.isOriginariaSIL() && vaDatiVacancyDTO.getIdPfPrincipalAziendaPalese() != null
				&& vaDatiVacancyDTO.getVisibilita()) {
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
}
