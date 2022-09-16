package it.eng.myportal.beans.vacancies.pf;

import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaInformatica;
import it.eng.myportal.entity.home.VaDatiVacancyHome;


public class VaCompetenzeDigitaliPfSection extends VacancyBaseFormSection implements IVacancySection {

	private VaInformatica vaInformatica;

	public VaCompetenzeDigitaliPfSection(VacancyFormPfBean cvBean) {
		super(cvBean);
        vaInformatica = new VaInformatica();
	}

	@Override
	public void initSection() {

		Integer vacancyId = getVacancyFormPfBean().getVaDatiVacancy().getIdVaDatiVacancy();
		VaDatiVacancy vaDatiVacancy = getVacancyFormPfBean().getVaDatiVacancy();

		vaInformatica = getVacancyFormPfBean().vaInformaticaHome.findByVacancyId(vacancyId);
		if (vaInformatica == null)// se non c'e` , non c'e`
			vaInformatica = new VaInformatica();

		vaInformatica.setVaDatiVacancy(vaDatiVacancy);

	}

	@Override
	public void sync() {
		getVacancyFormPfBean().getVaDatiVacancy().setVaInformatica(vaInformatica);
		vaInformatica.setVaDatiVacancy(getVacancyFormPfBean().getVaDatiVacancy());
		VaDatiVacancyHome homeEJB = getVacancyFormPfBean().getVaDatiVacancyHome();
		Integer idPfPrinc = getVacancyFormPfBean().getUtenteCompletoDTO().getIdPfPrincipal();
		if (vaInformatica.getDtmIns() != null)
			vaInformatica = getVacancyFormPfBean().vaInformaticaHome.merge(vaInformatica, idPfPrinc);
		else
			vaInformatica = getVacancyFormPfBean().vaInformaticaHome.persist(vaInformatica, idPfPrinc);

        setVaInformatica(getVacancyFormPfBean().vaInformaticaHome.findById(idPfPrinc));            	
		// aggiornare VaDatiVacancy
		getVacancyFormPfBean().getVaDatiVacancy().setVaInformatica(vaInformatica);
    	homeEJB.merge(getVacancyFormPfBean().getVaDatiVacancy(), idPfPrinc);		
	}

	public VaInformatica getVaInformatica() {
		return vaInformatica;
	}

	public void setVaInformatica(VaInformatica vaInformatica) {
		this.vaInformatica = vaInformatica;
	}

}
