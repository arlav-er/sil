package it.eng.myportal.beans.curriculum.tabs;

import it.eng.myportal.beans.AbstractMasterDetailTabBean;
import it.eng.myportal.beans.CurriculumVitaeBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.ICurriculumSection;
import it.eng.myportal.dtos.IHasPrimaryKey;
import it.eng.myportal.entity.home.AbstractCurriculumEntityListHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDiffusioneHome;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Classe astratta per gestire una tab master/detail del curriculum. Questo BackingBean viene utilizzato nelle pagine di
 * editing del CV dell'utente<br/>
 * 
 * <b>Elenco restrizioni di sicurezza</b>
 * <ul>
 * <li>L'utente connesso deve essere un utente.</li>
 * <li>TODO L'utente deve essere proprietario del cv che sta caricando. (controllo l'id del cv)</li>
 * </ul>
 * 
 * @author Rodi A.
 * 
 * @param <DTO>
 *            Classe del DTO associato alla tab.
 */
public abstract class AbstractCvMasterDetailTabBean<DTO extends ICurriculumSection & IHasPrimaryKey<Integer>> extends
		AbstractMasterDetailTabBean<DTO> {

	private static final String CURRICULUM_ID = "curriculumId";

	/**
	 * Id del curriculum
	 */
	protected int curriculumId;

	@ManagedProperty(value = "#{curriculumVitaeBean}")
	protected CurriculumVitaeBean curriculumVitaeBean;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	DeAmbitoDiffusioneHome deAmbitoDiffusioneHome;

	private String curriculumIdStr;

	public CurriculumVitaeBean getCurriculumVitaeBean() {
		return curriculumVitaeBean;
	}

	public void setCurriculumVitaeBean(CurriculumVitaeBean curriculumVitaeBean) {
		this.curriculumVitaeBean = curriculumVitaeBean;
	}

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		curriculumId = Integer.parseInt(StringUtils.defaultString(map.get("id"), curriculumIdStr));
		putParamsIntoSession();

		if (curriculumId != curriculumVitaeBean.getData().getId().intValue()) {
			log.error("Violazione di sicurezza per manipolazione di parametri [curriculumId]");
			redirectGrave("generic.manipulation_error");
		}

		try {
			list = retrieveData();
			data = buildNewDataIstance();
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	/**
	 * Salva la sezione del CV, impostandole il cv di riferimento e richiamando il servizio dell'EJB.
	 */
	@Override
	protected void saveData() {
		data.setIdCvDatiPersonali(curriculumId);
		data.setDtmMod(new Date());
		data = homePersist(getHome(), data);

		return;
	}

	@Override
	protected List<DTO> retrieveData() {
		return getHome().findDTOByCurriculumId(curriculumId);
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

	protected abstract AbstractCurriculumEntityListHome<?, DTO> getHome();

	@Override
	public DTO buildNewDataIstance() {
		return null;
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
	public void update() {
		CvDatiPersonaliDTO cvDatiPersonaliDTO = cvDatiPersonaliHome.findDTOById(curriculumId);

		/*
		 * se il CV e' sincronizzato con cliclavoro ne aggiorno i dati
		 */
		if (cvDatiPersonaliDTO.isSincronizzatoCliclavoro()) {
			cvDatiPersonaliHome.mergeClicLavoro(session.getPrincipalId(), curriculumId);
		}

		super.update();

		return;
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
		list = retrieveData();
		return;
	}

	@Override
	public void delete() {
		CvDatiPersonaliDTO cvDatiPersonaliDTO = cvDatiPersonaliHome.findDTOById(curriculumId);

		/*
		 * se il CV e' sincronizzato con cliclavoro ne aggiorno i dati
		 */
		if (cvDatiPersonaliDTO.isSincronizzatoCliclavoro()) {
			cvDatiPersonaliHome.mergeClicLavoro(session.getPrincipalId(), curriculumId);
		}

		super.delete();

		return;
	}
}
