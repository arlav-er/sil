package it.eng.myportal.beans.curriculum.tabs;

import it.eng.myportal.dtos.CvAltreInfoDTO;
import it.eng.myportal.dtos.DeSezioneInfoDTO;
import it.eng.myportal.entity.home.CvAltreInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeAbilitazioneGenHome;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeSezioneInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeTrasfertaHome;
import it.eng.myportal.entity.home.local.ICurriculumEntityHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@ViewScoped
public class CvAltreInformazioniBean extends AbstractCurriculumTabBean<CvAltreInfoDTO> {

	@EJB
	private CvAltreInfoHome cvAltreInfoHome;
	
	@EJB
	private DeAbilitazioneGenHome deAbilitazioneGenHome;
	
	@EJB
	private DeAgevolazioneHome deAgevolazioneHome;
	
	@EJB
	private DeTrasfertaHome deTrasfertaHome;
	
	@EJB
	DeSezioneInfoHome deSezioneInfoHome;
	
	DeSezioneInfoDTO info;
	
	private List<SelectItem> patentiOptions;
	private List<SelectItem> tipiTrasferta;
	private List<SelectItem> agevolazioni;
	
		
	@Override
	protected CvAltreInfoDTO buildNewDataIstance() {
		return new CvAltreInfoDTO();
	}

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		boolean addBlank = true;
		setPatentiOptions(deAbilitazioneGenHome.selectPatentiItems(addBlank));
		setTipiTrasferta(deTrasfertaHome.getListItems(true));
		agevolazioni = deAgevolazioneHome.getListItems(false);
		
		info = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_CV_ALTRE_INFORMAZIONI);
	}

	public List<SelectItem> getPatentiOptions() {
		return patentiOptions;
	}

	public void setPatentiOptions(List<SelectItem> patentiOptions) {
		this.patentiOptions = patentiOptions;
	}

	@Override
	public ICurriculumEntityHome<CvAltreInfoDTO> getHome() {
		return cvAltreInfoHome;
	}

	public List<SelectItem> getTipiTrasferta() {
		return tipiTrasferta;
	}

	public void setTipiTrasferta(List<SelectItem> tipiTrasferta) {
		this.tipiTrasferta = tipiTrasferta;
	}

	public List<SelectItem> getAgevolazioni() {
		return agevolazioni;
	}

	public void setAgevolazioni(List<SelectItem> agevolazioni) {
		this.agevolazioni = agevolazioni;
	}

	public DeSezioneInfoDTO getInfo() {
		return info;
	}

	public void setInfo(DeSezioneInfoDTO info) {
		this.info = info;
	}
}
