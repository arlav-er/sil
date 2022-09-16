package it.eng.myportal.beans.curriculum.tabs;

import it.eng.myportal.dtos.CvIstruzioneDTO;
import it.eng.myportal.dtos.DeSezioneInfoDTO;
import it.eng.myportal.entity.home.AbstractCurriculumEntityListHome;
import it.eng.myportal.entity.home.CvIstruzioneHome;
import it.eng.myportal.entity.home.decodifiche.DeSezioneInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoTitoloHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

/**
 * Bean che implementa una tab con un master-detail all'interno
 * 
 * @author Rodi A.
 *
 */
@ManagedBean
@ViewScoped
public class CvIstruzioneBean extends AbstractCvMasterDetailTabBean<CvIstruzioneDTO> {

	@EJB
	CvIstruzioneHome cvIstruzioneHome;

	@EJB
	DeSezioneInfoHome deSezioneInfoHome;

	@EJB
	DeStatoTitoloHome deStatoTitoloHome;

	DeSezioneInfoDTO info;

	private List<SelectItem> statoTitoloOptions;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		info = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_CV_INSTRUZIONE);
		statoTitoloOptions = deStatoTitoloHome.getListItems(true);
	}

	/**
	 * Costruisco un'istaziona vuota del DTO
	 * 
	 * @return CvIstruzioneDTO
	 */
	@Override
	public CvIstruzioneDTO buildNewDataIstance() {
		return new CvIstruzioneDTO();
	}

	@Override
	protected AbstractCurriculumEntityListHome<?, CvIstruzioneDTO> getHome() {
		return cvIstruzioneHome;
	}

	public DeSezioneInfoDTO getInfo() {
		return info;
	}

	public void setInfo(DeSezioneInfoDTO info) {
		this.info = info;
	}

	public List<SelectItem> getStatoTitoloOptions() {
		return statoTitoloOptions;
	}

	public void setStatoTitoloOptions(List<SelectItem> statoTitoloOptions) {
		this.statoTitoloOptions = statoTitoloOptions;
	}
}
