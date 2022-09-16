package it.eng.myportal.beans.curriculum.pf;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.CvAltreInfo;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.decodifiche.DeTrasferta;
import it.eng.myportal.entity.home.CvAltreInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeAbilitazioneGenHome;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeSezioneInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeTrasfertaHome;

/**
 * Sezione "altre info" del CV. estende CurriculumVitaeBaseFormSection da cui il riferim,ento al master,
 * CurriculumVitaePfBean
 * 
 * @author Ale
 *
 */
public class CvAltreInfoPfSection extends CurriculumVitaeBaseFormSection implements ICurriculumVitaeSection {

	protected static Log log = LogFactory.getLog(CvAltreInfoPfSection.class);

	private CvAltreInfoHome cvAltreInfoHome;

	private DeAbilitazioneGenHome deAbilitazioneGenHome;

	private DeAgevolazioneHome deAgevolazioneHome;

	private DeTrasfertaHome deTrasfertaHome;

	private DeSezioneInfoHome deSezioneInfoHome;

	private CvAltreInfo cvAltreInfo;
	private DeTrasferta deTrasferta;

	private List<DeTrasferta> deTrasfertas = new ArrayList<DeTrasferta>();

	public CvAltreInfoPfSection(CurriculumVitaePfBean pf, CvAltreInfoHome cvAltreInfoHome,
			DeAbilitazioneGenHome deAbilitazioneGenHome, DeAgevolazioneHome deAgevolazioneHome,
			DeTrasfertaHome deTrasfertaHome, DeSezioneInfoHome deSezioneInfoHome) {
		super(pf);
		this.cvAltreInfoHome = cvAltreInfoHome;
		this.deAbilitazioneGenHome = deAbilitazioneGenHome;
		this.deAgevolazioneHome = deAgevolazioneHome;
		this.deTrasfertaHome = deTrasfertaHome;
		this.deSezioneInfoHome = deSezioneInfoHome;

		// initSection();
	}

	@Override
	public void initSection() {
		initCvAltreInfo();

		deTrasfertas = deTrasfertaHome.findAll();
	}

	private void initCvAltreInfo() {
		List<CvAltreInfo> cvAltreInfos = cvAltreInfoHome
				.findProperByCurriculumId(getCurriculumVitaePfBean().getCvDatiPersonali().getIdCvDatiPersonali());
		if (cvAltreInfos.isEmpty()) {
			cvAltreInfo = new CvAltreInfo();
		} else if (cvAltreInfos.size() == 1) {
			cvAltreInfo = cvAltreInfos.get(0);
			deTrasferta = cvAltreInfo.getDeTrasferta();
		} else {
			log.error("GRAVE: sono presenti più istanze di cvAltreInfo per il curriculum vitae con id: "
					+ getCurriculumVitaePfBean().getCvDatiPersonali().getIdCvDatiPersonali());
		}
	}

	public CvAltreInfo getCvAltreInfo() {
		return cvAltreInfo;
	}

	public void setCvAltreInfo(CvAltreInfo cvAltreInfo) {
		this.cvAltreInfo = cvAltreInfo;
	}

	public List<DeTrasferta> getDeTrasfertas() {
		return deTrasfertas;
	}

	public void setDeTrasfertas(List<DeTrasferta> deTrasfertas) {
		this.deTrasfertas = deTrasfertas;
	}

	public DeTrasferta getDeTrasferta() {
		return deTrasferta;
	}

	public void setDeTrasferta(DeTrasferta deTrasferta) {
		this.deTrasferta = deTrasferta;
	}

	public void reset() {

	}

	@Override
	public void sync() {
		// Gestione salvataggio agevolazioni, abilitazioni e trasferte

		if (deTrasferta != null) {
			DeTrasferta deTrasfertaTemp = deTrasfertaHome.findById(deTrasferta.getCodTrasferta());
			cvAltreInfo.setDeTrasferta(deTrasfertaTemp);
		}

		CvDatiPersonali cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();
		cvAltreInfo.setIdCvDatiPersonali(cvDatiPersonali.getIdCvDatiPersonali());
		cvAltreInfo.setCvDatiPersonali(cvDatiPersonali);
		if (cvAltreInfo.getDtmIns() == null) {
			cvAltreInfo = cvAltreInfoHome.persist(cvAltreInfo,
					getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());
		} else {
			cvAltreInfo = cvAltreInfoHome.merge(cvAltreInfo,
					getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());
		}
		cvDatiPersonali.setCvAltreInfo(cvAltreInfo);
		// merge per dtmMod e Dtscadenza
		getCurriculumVitaePfBean().updateDataScadenza(cvDatiPersonali);
		getCurriculumVitaePfBean().setCvDatiPersonali(getCurriculumVitaePfBean().getCvDatiPersonaliHome()
				.merge(cvDatiPersonali, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente()));
		curriculumVitaePfBean.calcCompletionPercentage();
	}

}
