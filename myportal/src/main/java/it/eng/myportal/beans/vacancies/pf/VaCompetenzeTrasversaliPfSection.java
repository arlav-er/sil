package it.eng.myportal.beans.vacancies.pf;

import it.eng.myportal.entity.VaCompetenzeTrasv;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.VaDatiVacancyHome;

public class VaCompetenzeTrasversaliPfSection extends VacancyBaseFormSection implements IVacancySection {

	private VaCompetenzeTrasv vaCompetenzeTrasv;

	public VaCompetenzeTrasversaliPfSection(VacancyFormPfBean cvBean) {
		super(cvBean);
		vaCompetenzeTrasv = new VaCompetenzeTrasv();
	}

	@Override
	public void initSection() {
		Integer vacancyId = getVacancyFormPfBean().getVaDatiVacancy().getIdVaDatiVacancy();
		VaDatiVacancy vaDatiVacancy = getVacancyFormPfBean().getVaDatiVacancy();
		
		vaCompetenzeTrasv = getVacancyFormPfBean().vaCompetenzeTrasvHome.findByVacancyId(vacancyId);
		if (vaCompetenzeTrasv == null)//se non c'e` , non c'e`
			vaCompetenzeTrasv = new VaCompetenzeTrasv();
		
		vaCompetenzeTrasv.setVaDatiVacancy(vaDatiVacancy);
		vaDatiVacancy.setVaCompetenzeTrasv(vaCompetenzeTrasv);

	}

	public VaCompetenzeTrasv getVaCompetenzeTrasv() {
		return vaCompetenzeTrasv;
	}

	public void setVaCompetenzeTrasv(VaCompetenzeTrasv vaCompetenzeTrasvi) {
		this.vaCompetenzeTrasv = vaCompetenzeTrasvi;
	}

	@Override
	public void sync() {
		Integer idPfPrinc = getVacancyFormPfBean().getUtenteCompletoDTO().getIdPfPrincipal();
		VaDatiVacancyHome homeEJB = getVacancyFormPfBean().getVaDatiVacancyHome();
		
		if (vaCompetenzeTrasv.getDtmIns() != null)
			vaCompetenzeTrasv = getVacancyFormPfBean().vaCompetenzeTrasvHome.merge(vaCompetenzeTrasv, idPfPrinc);
		else
			vaCompetenzeTrasv = getVacancyFormPfBean().vaCompetenzeTrasvHome.persist(vaCompetenzeTrasv, idPfPrinc);
		
		 // aggiornare VaDatiVacancy
		getVacancyFormPfBean().getVaDatiVacancy().setVaCompetenzeTrasv(vaCompetenzeTrasv);
     	homeEJB.merge(getVacancyFormPfBean().getVaDatiVacancy(), idPfPrinc);		
	}

}
