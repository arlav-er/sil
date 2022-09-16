package it.eng.myportal.beans.curriculum.tabs;

import it.eng.myportal.dtos.CvFormazioneDTO;
import it.eng.myportal.dtos.DeSezioneInfoDTO;
import it.eng.myportal.entity.home.AbstractCurriculumEntityListHome;
import it.eng.myportal.entity.home.CvFormazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDisciplinareHome;
import it.eng.myportal.entity.home.decodifiche.DeSezioneInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoCorsoSilHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

/**
 * BackingBean della tab FORMAZIONE Estende la classe astratta per le tab contenenti un master-detail.
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class CvFormazioneBean extends AbstractCvMasterDetailTabBean<CvFormazioneDTO> {

	@EJB
	CvFormazioneHome cvFormazioneHome;

	@EJB
	DeSezioneInfoHome deSezioneInfoHome;

	@EJB
	DeAmbitoDisciplinareHome deAmbitoDisciplinareHome;

	@EJB
	DeTipoCorsoSilHome deTipoCorsoHome;

	DeSezioneInfoDTO info;
	private List<SelectItem> ambitoDisciplinareOptions;
	private List<SelectItem> tipoCorsoOptions;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		info = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_CV_FORMAZIONE);
		ambitoDisciplinareOptions = deAmbitoDisciplinareHome.getListItems(true);
		tipoCorsoOptions = deTipoCorsoHome.getListItems(true);
	}

	/**
	 * Costruisco un'istaziona vuota del DTO
	 * 
	 * @return CvFormazioneDTO
	 */
	public CvFormazioneDTO buildNewDataIstance() {
		return new CvFormazioneDTO();
	}

	@Override
	protected AbstractCurriculumEntityListHome<?, CvFormazioneDTO> getHome() {
		return cvFormazioneHome;
	}

	public DeSezioneInfoDTO getInfo() {
		return info;
	}

	public void setInfo(DeSezioneInfoDTO info) {
		this.info = info;
	}

	public List<SelectItem> getAmbitoDisciplinareOptions() {
		return ambitoDisciplinareOptions;
	}

	public void setAmbitoDisciplinareOptions(List<SelectItem> ambitoDisciplinareOptions) {
		this.ambitoDisciplinareOptions = ambitoDisciplinareOptions;
	}

	public List<SelectItem> getTipoCorsoOptions() {
		return tipoCorsoOptions;
	}

	public void setTipoCorsoOptions(List<SelectItem> tipoCorsoOptions) {
		this.tipoCorsoOptions = tipoCorsoOptions;
	}
}
