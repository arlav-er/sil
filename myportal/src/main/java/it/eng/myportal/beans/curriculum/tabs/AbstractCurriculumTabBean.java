package it.eng.myportal.beans.curriculum.tabs;

import it.eng.myportal.beans.AbstractEditableBean;
import it.eng.myportal.beans.CurriculumVitaeBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.ICurriculumSection;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDiffusioneHome;
import it.eng.myportal.entity.home.local.ICurriculumEntityHome;

import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Classe astratta per gestire una tab qualsiasi del curriculum. Questo BackingBean viene utilizzato nelle pagine di
 * editing del CV dell'utente<br/>
 * 
 * <b>Elenco restrizioni di sicurezza</b>
 * <ul>
 * <li>L'utente connesso deve essere un utente.</li>
 * <li>L'utente deve essere proprietario del cv che sta caricando. (controllo l'id del cv)</li>
 * </ul>
 * 
 * @author Rodi A.
 * 
 * @param <DTO>
 *            Classe del DTO associato alla tab.
 */
public abstract class AbstractCurriculumTabBean<DTO extends ICurriculumSection> extends AbstractEditableBean<DTO> {

	private static final String CURRICULUM_ID = "curriculumId";

	/**
	 * Id della testata del curriculum a cui è associata la tab.
	 */
	protected int curriculumId;

	/**
	 * Determina se è necessario eseguire uno switch alla tab successiva in seguito al salvataggio.
	 */
	protected boolean switchtab;

	/**
	 * Determina se il curriculum in questione usa le decodifiche SIL.
	 */
	protected boolean usaDecodificheSil;

	@ManagedProperty(value = "#{curriculumVitaeBean}")
	protected CurriculumVitaeBean curriculumVitaeBean;

	private String curriculumIdStr;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	DeAmbitoDiffusioneHome deAmbitoDiffusioneHome;

	/**
	 * Metodo eseguito successivamente alla costruzione del BB. Il metodo recupera i dati da DB e, se presenti, imposta
	 * la variabile 'saved' a true, altrimenti, costruisce un nuovo DTO. Richiama quindi un eventuale metodo
	 * postContruct, implementato nella classe concreta, con cui proseguire la costruzione del BB
	 */
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		// le tab devono essere sempre in editing
		editing = true;
	}

	public CurriculumVitaeBean getCurriculumVitaeBean() {
		return curriculumVitaeBean;
	}

	public void setCurriculumVitaeBean(CurriculumVitaeBean curriculumVitaeBean) {
		this.curriculumVitaeBean = curriculumVitaeBean;
	}

	public boolean isSwitchtab() {
		return switchtab;
	}

	public void setSwitchtab(boolean switchtab) {
		this.switchtab = switchtab;
	}

	public abstract ICurriculumEntityHome<DTO> getHome();

	@Override
	protected DTO retrieveData() {
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		curriculumId = Integer.parseInt(StringUtils.defaultString(map.get("id"), curriculumIdStr));
		putParamsIntoSession();

		if (curriculumId != curriculumVitaeBean.getData().getId().intValue()) {
			log.error("Violazione di sicurezza per manipolazione di parametri [curriculumId]");
			redirectGrave("generic.manipulation_error");
			return null;
		}

		if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(curriculumVitaeBean.getData().getOpzTipoDecodifiche())) {
			usaDecodificheSil = true;
		}

		DTO ret = getHome().findDTOByCurriculumId(curriculumId);
		return ret;
	}

	@Override
	protected void saveData() {
		data.setIdCvDatiPersonali(curriculumId);
		data.setDtmMod(new Date());
		data = homePersist(getHome(), data);
		switchtab = false;
		addInfoMessage("data.created");
		log.debug("Scheda salvata.");
		return;
	}

	@Override
	protected void updateData() {
		data.setDtmMod(new Date());
		data = homeMerge(getHome(), data);
		addInfoMessage("data.updated");
		log.debug("Aggiornata la scheda.");

		return;
	}

	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters ret = super.generateRestoreParams();
		ret.put(CURRICULUM_ID, curriculumId);
		return ret;
	}

	@Override
	public void ricreaStatoDaSessione(RestoreParameters restoreParams) {
		super.ricreaStatoDaSessione(restoreParams);
		curriculumIdStr = ObjectUtils.toString(restoreParams.get(CURRICULUM_ID));
	}

	@Override
	public void popolaRedoBySess() {
		setRedoBySess(curriculumVitaeBean.isRedoBySess());
	}

	@Override
	public String getToken() {
		return curriculumVitaeBean.getToken();
	}

	@Override
	public void save() {
		CvDatiPersonaliDTO cvDatiPersonaliDTO = cvDatiPersonaliHome.findDTOById(curriculumId);

		/*
		 * se il CV e' sincronizzato con cliclavoro ne aggiorno i dati
		 */
		if (cvDatiPersonaliDTO.isSincronizzatoCliclavoro()) {
			cvDatiPersonaliHome.mergeClicLavoro(session.getPrincipalId(), curriculumId);
		}

		super.save();

		editing = true; // le tab devono essere sempre in editing
		return;
	}

	@Override
	public void update() {
		CvDatiPersonaliDTO cvDatiPersonaliDTO = cvDatiPersonaliHome.findDTOById(curriculumId);

		/*
		 * se il CV e' sincronizzato con cliclavoro ne aggiorno i dati
		 */
		if (cvDatiPersonaliDTO.isSincronizzatoCliclavoro()) {
			cvDatiPersonaliHome.mergeClicLavoro(session.getPrincipalId(), curriculumId);
		}

		super.update();

		editing = true; // le tab devono essere sempre in editing
		return;
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
		CvDatiPersonaliDTO cvDatiPersonaliDTO = cvDatiPersonaliHome.findDTOById(curriculumId);

		/*
		 * se il CV e' sincronizzato con cliclavoro ne aggiorno i dati
		 */
		if (cvDatiPersonaliDTO.isSincronizzatoCliclavoro()) {
			cvDatiPersonaliHome.mergeClicLavoro(session.getPrincipalId(), curriculumId);
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
}