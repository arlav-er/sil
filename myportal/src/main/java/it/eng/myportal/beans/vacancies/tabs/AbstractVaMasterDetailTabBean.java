package it.eng.myportal.beans.vacancies.tabs;

import it.eng.myportal.beans.AbstractMasterDetailTabBean;
import it.eng.myportal.beans.vacancies.VacancyBean;
import it.eng.myportal.dtos.IHasPrimaryKey;
import it.eng.myportal.dtos.IVacancySection;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.VaDatiVacancyHome;

import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

/**
 * Classe astratta una lista ed un dettaglio delle Vacancy
 * 
 * @author Rodi A.
 * 
 * @param <DTO>
 *            Classe del DTO associato alla tab.
 */
public abstract class AbstractVaMasterDetailTabBean<DTO extends IVacancySection & IHasPrimaryKey<Integer>> extends
		AbstractMasterDetailTabBean<DTO> {

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	/**
	 * Id della testata della vacancy a cui è associata la tab.
	 */
	protected int vacancyId;

	/**
	 * Determina se la vacancy in questione sta usando decodifiche SIL (se sì, mostro i campi "nuovi").
	 */
	protected boolean usaDecodificheSil;

	@ManagedProperty(value = "#{vacancyBean}")
	protected VacancyBean vacancyBean;

	public VacancyBean getVacancyBean() {
		return vacancyBean;
	}

	public void setVacancyBean(VacancyBean vacancyBean) {
		this.vacancyBean = vacancyBean;
	}

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		vacancyId = Integer.parseInt(map.get("id"));

		if (vacancyId != vacancyBean.getData().getId().intValue()) {
			log.error("Violazione di sicurezza per manipolazione di parametri [vacancyId]");
			redirectGrave("generic.manipulation_error");
			return;
		}

		try {
			list = retrieveData();
			data = buildNewDataIstance();
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}

		// Controllo se la vacancy usa le decodifiche SIL
		usaDecodificheSil = VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(vacancyBean.getData().getOpzTipoDecodifiche());
	}

	@Override
	protected void saveData() {
		data.setIdVaDatiVacancy(vacancyId);
		data.setDtmMod(new Date());
		data = homePersist(getHome(), data);
	}

	@Override
	public void update() {
		VaDatiVacancyDTO vaDatiVacancyDTO = vaDatiVacancyHome.findDTOById(vacancyId);

		/*
		 * se la vacancy e' sincronizzata con cliclavoro ne aggiorno i dati
		 */
		if (vaDatiVacancyHome.isSincronizzatoClicLavoro(vaDatiVacancyDTO.getId())) {
			vaDatiVacancyHome.mergeClicLavoro(session.getPrincipalId(), vacancyId);
		}

		super.update();

		return;
	}

	@Override
	public void save() {
		VaDatiVacancyDTO vaDatiVacancyDTO = vaDatiVacancyHome.findDTOById(vacancyId);

		/*
		 * se la vacancy e' sincronizzata con cliclavoro ne aggiorno i dati
		 */
		if (vaDatiVacancyHome.isSincronizzatoClicLavoro(vaDatiVacancyDTO.getId())) {
			vaDatiVacancyHome.mergeClicLavoro(session.getPrincipalId(), vacancyId);
		}

		super.save();

		return;
	}

	@Override
	public void delete() {
		VaDatiVacancyDTO vaDatiVacancyDTO = vaDatiVacancyHome.findDTOById(vacancyId);

		/*
		 * se la vacancy e' sincronizzata con cliclavoro ne aggiorno i dati
		 */
		if (vaDatiVacancyHome.isSincronizzatoClicLavoro(vaDatiVacancyDTO.getId())) {
			vaDatiVacancyHome.mergeClicLavoro(session.getPrincipalId(), vacancyId);
		}

		super.delete();

		return;
	}

	public boolean usaDecodificheSil() {
		return usaDecodificheSil;
	}
}
