package it.eng.myportal.beans.curriculum;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Backing Bean associato al CurriculumVitae.
 *
 * @author Rodi A.
 *
 */
@ManagedBean
@ViewScoped
public class VacancyCompatibiliBean extends AbstractBaseBean {

	/**
	 * Logger per registrare informazioni.
	 */
	protected static Log log = LogFactory.getLog(VacancyCompatibiliBean.class);

	private static final String CURRICULUM_ID = "curriculumId";

	@EJB
	private CvDatiPersonaliHome cvDatiPersonaliHome;
	@EJB
	private VaDatiVacancyHome vaDatiVacancyHome;

	private List<VaDatiVacancyDTO> vaDatiVacancyDTOs;

	private int curriculumId;

	private long numVacancyCompatibili;

	private String curriculumIdStr;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		log.debug("Costruito il Bean per il CurriculuVitae!");

		CvDatiPersonaliDTO data = null;

		if (session.isUtente()) {
			Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			try {
				curriculumId = Integer.parseInt(StringUtils.defaultString(map.get("id"), curriculumIdStr));
				putParamsIntoSession();
			} catch (NumberFormatException e) {
				redirectGrave("generic.manipulation_error");
				// return null;
			}

			data = cvDatiPersonaliHome.findDTOById(curriculumId);
			vaDatiVacancyDTOs = vaDatiVacancyHome.findDTOByCurriculumId(curriculumId);
			numVacancyCompatibili = vaDatiVacancyHome.countVacancyCompatibiliByCurriculumId(curriculumId);
			// /////////////////////////////////////////////////////////////////////////
			// *** Controllo di sicurezza ***
			// Il CV richiesto o una sua sezione devono appartenere all'utente
			// loggato
			// /////////////////////////////////////////////////////////////////////////
			Integer idUtente = data.getIdPfPrincipal();
			if (idUtente.intValue() != session.getConnectedUtente().getId()) {
				redirectGrave("generic.manipulation_error");
				// return null;
			}

			// return data;
		} else {
			log.error("Tentativo di accedere alla sezione utente.");
			addErrorMessage("user.is_not");
			redirectHome();
			// return null;
		}
	}

	/**
	 * @return the vaDatiVacancyDTOs
	 */
	public List<VaDatiVacancyDTO> getVaDatiVacancyDTOs() {
		return vaDatiVacancyDTOs;
	}

	/**
	 * @param vaDatiVacancyDTOs
	 *            the vaDatiVacancyDTOs to set
	 */
	public void setVaDatiVacancyDTOs(List<VaDatiVacancyDTO> vaDatiVacancyDTOs) {
		this.vaDatiVacancyDTOs = vaDatiVacancyDTOs;
	}

	/**
	 * @return the numVacancyCompatibili
	 */
	public long getNumVacancyCompatibili() {
		return numVacancyCompatibili;
	}

	/**
	 * @param numVacancyCompatibili
	 *            the numVacancyCompatibili to set
	 */
	public void setNumVacancyCompatibili(long numVacancyCompatibili) {
		this.numVacancyCompatibili = numVacancyCompatibili;
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
}
