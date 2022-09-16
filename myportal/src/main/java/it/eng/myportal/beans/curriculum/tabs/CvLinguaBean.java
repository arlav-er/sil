package it.eng.myportal.beans.curriculum.tabs;

import it.eng.myportal.dtos.CvLinguaDTO;
import it.eng.myportal.dtos.DeSezioneInfoDTO;
import it.eng.myportal.entity.home.AbstractCurriculumEntityListHome;
import it.eng.myportal.entity.home.CvLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinSilHome;
import it.eng.myportal.entity.home.decodifiche.DeModalitaLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeSezioneInfoHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

/**
 * Bean che implementa una tab con un master-detail all'interno
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class CvLinguaBean extends AbstractCvMasterDetailTabBean<CvLinguaDTO> {
	@EJB
	CvLinguaHome cvLinguaHome;

	@EJB
	DeGradoLinHome deGradoLinHome;

	@EJB
	DeGradoLinSilHome deGradoLinSilHome;

	@EJB
	DeSezioneInfoHome deSezioneInfoHome;

	@EJB
	DeModalitaLinguaHome deModalitaLinguaHome;

	DeSezioneInfoDTO info;
	private List<SelectItem> gradiLinguaOptions;
	private List<SelectItem> modalitaLinguaOptions;

	@PostConstruct
	@Override
	protected void postConstruct() {
		super.postConstruct();
		info = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_CV_LINGUE);

		modalitaLinguaOptions = deModalitaLinguaHome.getListItems(true);
		if (curriculumVitaeBean.usaDecodificheSil()) {
			setGradiLinguaOptions(deGradoLinSilHome.getListItems(true));
		} else {
			setGradiLinguaOptions(deGradoLinHome.getListItems(true));
		}

	}

	/**
	 * Costruisco un'istaziona vuota del DTO
	 * 
	 * @return CvLinguaDTO
	 */
	@Override
	public CvLinguaDTO buildNewDataIstance() {
		return new CvLinguaDTO();
	}

	public List<SelectItem> getGradiLinguaOptions() {
		return gradiLinguaOptions;
	}

	public void setGradiLinguaOptions(List<SelectItem> gradiLinguaOptions) {
		this.gradiLinguaOptions = gradiLinguaOptions;
	}

	public DeSezioneInfoDTO getInfo() {
		return info;
	}

	public void setInfo(DeSezioneInfoDTO info) {
		this.info = info;
	}

	@Override
	protected AbstractCurriculumEntityListHome<?, CvLinguaDTO> getHome() {
		return cvLinguaHome;
	}

	@Override
	protected List<CvLinguaDTO> retrieveData() {
		return cvLinguaHome.findDTOByCurriculumId(curriculumId);
	}

	@Override
	public void save() {
		if (checkGradoLingua()) {
			addErrorMessage("lingua.no_grado");
			return;
		}
		super.save();
	}

	@Override
	public void update() {
		if (checkGradoLingua()) {
			addErrorMessage("lingua.no_grado");
			return;
		}
		super.update();
	}

	/**
	 * Se non madrelingua devono essere compilati i 3 singoli gradi di conoscenza lingua
	 * 
	 * @return
	 */
	private boolean checkGradoLingua() {
		return (!checkMadreLingua())
				&& (StringUtils.isBlank(data.getCodGradoLinguaLetto())
						|| StringUtils.isBlank(data.getCodGradoLinguaParlato()) || StringUtils.isBlank(data
						.getCodGradoLinguaScritto()));
	}

	private boolean checkMadreLingua() {
		if (data.isMadrelingua()) {
			data.setCodGradoLinguaLetto("");
			data.setCodGradoLinguaScritto("");
			data.setCodGradoLinguaParlato("");
			return true;
		} else
			return false;
	}

	public List<SelectItem> getModalitaLinguaOptions() {
		return modalitaLinguaOptions;
	}

	public void setModalitaLinguaOptions(List<SelectItem> modalitaLinguaOptions) {
		this.modalitaLinguaOptions = modalitaLinguaOptions;
	}
}
