package it.eng.myportal.beans.vacancies.tabs;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import it.eng.myportal.beans.ICheckCliclavoro;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.dtos.VaRapportoDiLavoroDTO;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaRapportoDiLavoroHome;
import it.eng.myportal.entity.home.VaVacancyClHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioSilHome;
import it.eng.myportal.entity.home.decodifiche.DeRetribuzioneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.decodifiche.DeTurnoHome;
import it.eng.myportal.entity.home.decodifiche.DeTurnoSilHome;
import it.eng.myportal.entity.home.local.IVacancyEntityHome;

/**
 * Bean del Rapporto di lavoro
 * 
 * @author iescone, rodi
 * 
 */
@ManagedBean
@ViewScoped
public class VaRapportoDiLavoroBean extends AbstractVacancyTabBean<VaRapportoDiLavoroDTO> implements ICheckCliclavoro {

	@EJB
	DeRetribuzioneHome deRetribuzioneHome;

	@EJB
	DeTurnoHome deTurnoHome;

	@EJB
	DeOrarioHome deOrarioHome;

	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	VaVacancyClHome vaVacancyClHome;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	DeTurnoSilHome deTurnoSilHome;

	@EJB
	DeOrarioSilHome deOrarioSilHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;

	private List<SelectItem> retribuzioni;
	private List<SelectItem> turni;
	private List<SelectItem> orari;
	private List<SelectItem> tipologieContratto;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		retribuzioni = deRetribuzioneHome.getListItems(true);

		if (usaDecodificheSil) {
			turni = deTurnoSilHome.getListItems(false);
			orari = deOrarioSilHome.getListItems(false);
			/* prendo anche le codifiche scadute con il secondo parametro a true */
			tipologieContratto = deContrattoSilHome.getListItems(false, true);
		} else {
			turni = deTurnoHome.getListItems(false);
			orari = deOrarioHome.getListItems(false);
			/* prendo anche le codifiche scadute con il secondo parametro a true */
			tipologieContratto = deContrattoHome.getListItems(false, true);
		}
	}

	//@EJB(mappedName = "java:module/VaRapportoDiLavoroHome")
	//IVacancyEntityHome<VaRapportoDiLavoroDTO> vaRapportoDiLavoroHome;
	@EJB
	VaRapportoDiLavoroHome vaRapportoDiLavoroHome;

	/**
	 * Costruisco un'istaziona vuota del DTO
	 * 
	 * @return VaCompetenzeTrasvDTO
	 */
	@Override
	public VaRapportoDiLavoroDTO buildNewDataIstance() {
		return new VaRapportoDiLavoroDTO();
	}

	@Override
	public IVacancyEntityHome<VaRapportoDiLavoroDTO> getHome() {
		return vaRapportoDiLavoroHome;
	}

	public List<SelectItem> getRetribuzioni() {
		return retribuzioni;
	}

	public void setRetribuzioni(List<SelectItem> retribuzioni) {
		this.retribuzioni = retribuzioni;
	}

	public List<SelectItem> getTurni() {
		return turni;
	}

	public void setTurni(List<SelectItem> turni) {
		this.turni = turni;
	}

	public List<SelectItem> getOrari() {
		return orari;
	}

	public void setOrari(List<SelectItem> orari) {
		this.orari = orari;
	}

	public List<SelectItem> getTipologieContratto() {
		return tipologieContratto;
	}

	public void setTipologieContratto(List<SelectItem> tipologieContratto) {
		this.tipologieContratto = tipologieContratto;
	}

	public void checkDatiCliclavoroListener(ComponentSystemEvent event) {
		vacancyBean.checkDatiCliclavoro(event, this);
	}

	@Override
	public boolean checkDatiCliclavoroSpecifico(ComponentSystemEvent event) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		UIComponent components = event.getComponent();
		UIInput component = (UIInput) components.findComponent("rapporto_di_lavoro:tipologieContratto:inputText");
		List<String> tipologieContratto = (List<String>) component.getValue();
		/*
		 * ritorna true se ho cliccato sul pulsante "Cancella". Fa schifo, lo so!
		 */
		boolean deleteAction = ((HttpServletRequest) facesContext.getExternalContext().getRequest()).getParameterMap()
				.get("javax.faces.partial.execute")[0].contains("deleteButton");
		if (deleteAction || tipologieContratto == null || tipologieContratto.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public String getCheckMessageCliclavoro() {
		return "cliclavoro.va.warn.tipologie_contratto";
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
