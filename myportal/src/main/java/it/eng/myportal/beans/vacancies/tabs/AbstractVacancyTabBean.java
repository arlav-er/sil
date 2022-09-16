package it.eng.myportal.beans.vacancies.tabs;

import it.eng.myportal.beans.AbstractEditableBean;
import it.eng.myportal.beans.vacancies.VacancyBean;
import it.eng.myportal.dtos.IVacancySection;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.local.IVacancyEntityHome;

import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

/**
 * Classe astratta per gestire una tab qualsiasi.
 * 
 * @author iescone
 * 
 * @param <DTO>
 *            Classe del DTO associato alla tab.
 */

public abstract class AbstractVacancyTabBean<DTO extends IVacancySection> extends AbstractEditableBean<DTO> {

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	/**
	 * Id della testata della vacancy a cui 衡ssociata la tab.
	 */
	protected int vacancyId;

	/**
	 * Determina se 衮ecessario eseguire uno switch alla tab successiva in seguito al salvataggio.
	 */
	protected boolean switchtab;

	/**
	 * Determina se la vacancy in questione sta usando decodifiche SIL (se sì, mostro i campi "nuovi").
	 */
	protected boolean usaDecodificheSil;

	@ManagedProperty(value = "#{vacancyBean}")
	protected VacancyBean vacancyBean;

	public AbstractVacancyTabBean() {

	}

	public VacancyBean getVacancyBean() {
		return vacancyBean;
	}

	public boolean isSwitchtab() {
		return switchtab;
	}

	/**
	 * Metodo eseguito successivamente alla costruzione del BB. Il metodo recupera i dati da DB e, se presenti, imposta
	 * la variabile 'saved' a true, altrimenti, costruisce un nuovo DTO. Richiama quindi un eventuale metodo
	 * postContruct, implementato nella classe concreta, con cui proseguire la costruzione del BB
	 */
	@Override
	@PostConstruct
	protected void postConstruct() {
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		vacancyId = Integer.parseInt(map.get("id"));

		if (vacancyId != vacancyBean.getData().getId().intValue()) {
			log.error("Violazione di sicurezza per manipolazione di parametri [vacancyId]");
			redirectGrave("generic.manipulation_error");
			return;
		}
		super.postConstruct();
		// le tab devono essere sempre in editing
		editing = true;
		// Controllo se la vacancy usa le decodifiche SIL
		usaDecodificheSil = VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(vacancyBean.getData().getOpzTipoDecodifiche());
	}

	public void setSwitchtab(boolean switchtab) {
		this.switchtab = switchtab;
	}

	public void setVacancyBean(VacancyBean vacancyBean) {
		this.vacancyBean = vacancyBean;
	}

	protected abstract IVacancyEntityHome<DTO> getHome();

	@Override
	protected DTO retrieveData() {
		DTO ret = getHome().findDTOByVacancyId(vacancyId);
		return ret;
	}

	@Override
	protected void saveData() {
		data.setIdVaDatiVacancy(vacancyId);
		data.setDtmMod(new Date());
		data = homePersist(getHome(), data);
		switchtab = false;
		log.debug("Scheda salvata.");
		addInfoMessage("data.created");
		return;
	}

	@Override
	protected void updateData() {
		data.setDtmMod(new Date());
		data = homeMerge(getHome(), data);
		log.debug("Aggiornata la scheda.");
		addInfoMessage("data.updated");
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

		editing = true; // le tab devono essere sempre in editing
		return;
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
		editing = true; // le tab devono essere sempre in editing
	}

	@Override
	public void dontedit() {
		super.dontedit();
		editing = true; // le tab devono essere sempre in editing
	}

	/**
	 * Metodo collegato al bottone 'Elimina' del dialog modale del master
	 */
	public void delete() {
		VaDatiVacancyDTO vaDatiVacancyDTO = vaDatiVacancyHome.findDTOById(vacancyId);

		/*
		 * se la vacancy e' sincronizzata con cliclavoro ne aggiorno i dati
		 */
		if (vaDatiVacancyHome.isSincronizzatoClicLavoro(vaDatiVacancyDTO.getId())) {
			vaDatiVacancyHome.mergeClicLavoro(session.getPrincipalId(), vacancyId);
		}

		deleteItem();

		editing = true; // le tab devono essere sempre in editing
		saved = false;
		return;
	}

	private void deleteItem() {
		Map<String, String> map = getRequestParameterMap();
		Integer deleteId = Integer.parseInt(map.get("id"));
		try {
			getHome().removeById(deleteId, session.getPrincipalId());

			editing = true;
			saved = false;
			addInfoMessage("data.deleted");

			data = buildNewDataIstance();

		} catch (EJBException e) {
			gestisciErrore(e, "data.error_deleting");
		}
	}

	public boolean usaDecodificheSil() {
		return usaDecodificheSil;
	}
}