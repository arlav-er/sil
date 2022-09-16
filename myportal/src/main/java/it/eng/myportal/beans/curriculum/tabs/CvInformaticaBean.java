package it.eng.myportal.beans.curriculum.tabs;

import it.eng.myportal.dtos.CvInformaticaDTO;
import it.eng.myportal.dtos.DeSezioneInfoDTO;
import it.eng.myportal.entity.home.CvInformaticaHome;
import it.eng.myportal.entity.home.decodifiche.DeSezioneInfoHome;
import it.eng.myportal.entity.home.local.ICurriculumEntityHome;
import it.eng.myportal.utils.ConstantsSingleton;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Bean dell'Istruzione
 * 
 * @author Rodi A.
 *
 */
@ManagedBean
@ViewScoped
public class CvInformaticaBean extends AbstractCurriculumTabBean<CvInformaticaDTO> {
		
	/**
	 * Injection degli EJB che mi servono a recuperare i dati dal DB
	 */
	
	@EJB
	CvInformaticaHome cvInformaticaHome;
	
	@EJB
	DeSezioneInfoHome deSezioneInfoHome;
	
	DeSezioneInfoDTO info;
		
	/**
	 * Costruisco un'istaziona vuota del DTO
	 * 
	 * @return CvInformaticaDTO
	 */	
	@Override
	public CvInformaticaDTO buildNewDataIstance() {		
		return new CvInformaticaDTO();
	}
	
	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		info = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_CV_CONOSCENZE_INFORMATICHE);
	}

	@Override
	public ICurriculumEntityHome<CvInformaticaDTO> getHome() {		
		return cvInformaticaHome;
	}

	public DeSezioneInfoDTO getInfo() {
		return info;
	}

	public void setInfo(DeSezioneInfoDTO info) {
		this.info = info;
	}
}
