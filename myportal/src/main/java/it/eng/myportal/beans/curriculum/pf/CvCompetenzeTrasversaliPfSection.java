package it.eng.myportal.beans.curriculum.pf;

import java.util.HashSet;
import java.util.List;

import it.eng.myportal.entity.CvCompetenzeTrasv;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.home.CvCompetenzeTrasvHome;

public class CvCompetenzeTrasversaliPfSection extends CurriculumVitaeBaseFormSection
		implements ICurriculumVitaeSection {

	private CvCompetenzeTrasvHome cvCompetenzeTrasvHome;
	private CvCompetenzeTrasv cvCompetenzeTrasv;

	// Costruttore
	public CvCompetenzeTrasversaliPfSection(CurriculumVitaePfBean cvBean, CvCompetenzeTrasvHome cvCompetenzeTrasvHome) {
		super(cvBean);
		this.cvCompetenzeTrasvHome = cvCompetenzeTrasvHome;
		// initSection();
	}

	@Override
	public void initSection() {
		List<CvCompetenzeTrasv> cvCompetenzeTrasversS = cvCompetenzeTrasvHome
				.findProperByCurriculumId(getCurriculumVitaePfBean().getCvDatiPersonali().getIdCvDatiPersonali());
		if (cvCompetenzeTrasversS.size() == 0) {
			cvCompetenzeTrasv = new CvCompetenzeTrasv();
		} else if (cvCompetenzeTrasversS.size() == 1) {
			cvCompetenzeTrasv = cvCompetenzeTrasversS.get(0);
		} else {
			log.error("GRAVE: sono presenti più istanze di cvCompetenzeTrasversS per il curriculum vitae con id: "
					+ getCurriculumVitaePfBean().getCvDatiPersonali().getIdCvDatiPersonali());
		}
	}

	@Override
	public void sync() {
		CvDatiPersonali cvDatiPersonali = getCurriculumVitaePfBean().getCvDatiPersonali();
		cvCompetenzeTrasv.setCvDatiPersonali(cvDatiPersonali);
		if (cvCompetenzeTrasv.getDtmIns() == null) {
			cvCompetenzeTrasv = cvCompetenzeTrasvHome.persist(cvCompetenzeTrasv,
					getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());
		} else {
			cvCompetenzeTrasv = cvCompetenzeTrasvHome.merge(cvCompetenzeTrasv,
					getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente());
		}
		getCurriculumVitaePfBean().updateDataScadenza(cvDatiPersonali);

		// refresh sull oggetto principale
		HashSet<CvCompetenzeTrasv> compHash = new HashSet<CvCompetenzeTrasv>();
		compHash.add(cvCompetenzeTrasv);
		cvDatiPersonali.setCvCompetenzeTrasvs(compHash);
		// merge per dtmMod e Dtscadenza
		getCurriculumVitaePfBean().updateDataScadenza(cvDatiPersonali);
		getCurriculumVitaePfBean().setCvDatiPersonali(getCurriculumVitaePfBean().getCvDatiPersonaliHome()
				.merge(cvDatiPersonali, getCurriculumVitaePfBean().getUtenteCompletoDTO().getIdUtente()));
		curriculumVitaePfBean.calcCompletionPercentage();

	}

	public CvCompetenzeTrasv getCvCompetenzeTrasv() {
		return cvCompetenzeTrasv;
	}

	public void setCvCompetenzeTrasv(CvCompetenzeTrasv cvCompetenzeTrasv) {
		this.cvCompetenzeTrasv = cvCompetenzeTrasv;
	}

}
