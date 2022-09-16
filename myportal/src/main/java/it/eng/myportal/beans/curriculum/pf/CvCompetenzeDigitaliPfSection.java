package it.eng.myportal.beans.curriculum.pf;

import java.util.List;

import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvInformatica;
import it.eng.myportal.entity.home.CvInformaticaHome;

public class CvCompetenzeDigitaliPfSection extends CurriculumVitaeBaseFormSection implements ICurriculumVitaeSection {

	private CvInformatica cvInformatica;
	private CvInformaticaHome cvInformaticaHome;
	
	public CvCompetenzeDigitaliPfSection(CurriculumVitaePfBean cvBean, CvInformaticaHome cvInformaticaHome) {
		super(cvBean);
		this.cvInformaticaHome = cvInformaticaHome;
		// initSection();
	}

	@Override
	public void initSection() {
		List<CvInformatica> cvInformaticas = cvInformaticaHome.findProperByCurriculumId(getCurriculumVitaePfBean().getCvDatiPersonali().getIdCvDatiPersonali());
		if(cvInformaticas.size() == 0) {
			cvInformatica = new CvInformatica();
		}else if(cvInformaticas.size() == 1) {
			cvInformatica = cvInformaticas.get(0);
		}else {
			log.error("GRAVE: sono presenti più istanze di cvInformatica per il curriculum vitae con id: " + getCurriculumVitaePfBean().getCvDatiPersonali().getIdCvDatiPersonali());
		}
	}

	@Override
	public void sync() {
		CvDatiPersonali cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();
		cvInformatica.setIdCvDatiPersonali(cvDatiPersonali.getIdCvDatiPersonali());
		cvInformatica.setCvDatiPersonali(cvDatiPersonali);
		if(cvInformatica.getDtmIns()==null) {
			cvInformatica = cvInformaticaHome.persist(cvInformatica, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());			
		} else {
			cvInformatica = cvInformaticaHome.merge(cvInformatica, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());			
		}
		getCurriculumVitaePfBean().updateDataScadenza(cvDatiPersonali);
		getCurriculumVitaePfBean().setCvDatiPersonali(getCurriculumVitaePfBean().getCvDatiPersonaliHome()
				.merge(cvDatiPersonali, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente()));
		curriculumVitaePfBean.calcCompletionPercentage();
	}

	public CvInformatica getCvInformatica() {
		return cvInformatica;
	}

	public void setCvInformatica(CvInformatica cvInformatica) {
		this.cvInformatica = cvInformatica;
	}
	
}