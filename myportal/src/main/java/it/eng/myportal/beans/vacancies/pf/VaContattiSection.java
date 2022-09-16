package it.eng.myportal.beans.vacancies.pf;

import it.eng.myportal.entity.VaContatto;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaPubblicazione;
import it.eng.myportal.entity.home.VaContattoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;

public class VaContattiSection extends VacancyBaseFormSection implements IVacancySection {

	VaContatto vaContatto;
	VaDatiVacancy vaDatiVacancy;
	VaPubblicazione vaPubblicazione;

	private VaContattoHome vaContattoHome;

	public VaContattiSection(VacancyFormPfBean vaBean, VaContattoHome vaContattoHome) {
		super(vaBean);
		this.vaContattoHome = vaContattoHome;
	}

	@Override
	public void initSection() {

		Integer vacancyId = getVacancyFormPfBean().getVaDatiVacancy().getIdVaDatiVacancy();
		vaDatiVacancy = getVacancyFormPfBean().getVaDatiVacancy();
		if (vaDatiVacancy.getVaContattoPrinc() != null) {
			vaContatto = vaContattoHome.findById(vaDatiVacancy.getVaContattoPrinc().getIdVaContatto());
		} else {
			vaContatto = new VaContatto();
		}
		
		vaPubblicazione = getVacancyFormPfBean().vaPubblicazioneHome.findByVacancyId(vacancyId);
		if(vaPubblicazione == null ) {
			vaPubblicazione = new VaPubblicazione();		
		}
	}

	@Override
	public void sync() {

		Integer idPfPrinc = getVacancyFormPfBean().getUtenteCompletoDTO().getIdPfPrincipal();
		VaDatiVacancy vaDatiVacancy = getVacancyFormPfBean().getVaDatiVacancy();
		VaDatiVacancyHome homeEJB = getVacancyFormPfBean().getVaDatiVacancyHome();

		if (vaContatto.getDtmIns() != null)
			vaContatto = getVacancyFormPfBean().vaContattoHome.merge(vaContatto, idPfPrinc);
		else
			vaContatto = getVacancyFormPfBean().vaContattoHome.persist(vaContatto, idPfPrinc);

		// Aggiungo/Modifico contatto su VaDatiVacancy
		vaDatiVacancy.setVaContattoPrinc(vaContatto);

		// aggiornare VaDatiVacancy
		getVacancyFormPfBean().setVaDatiVacancy(homeEJB.merge(vaDatiVacancy, idPfPrinc));

		// aggiornare VaPubblicazione - solo se Informazioni Contatto presenti
			vaPubblicazione.setVaDatiVacancy(vaDatiVacancy);
			if (vaPubblicazione.getDtmIns() != null) {
				getVacancyFormPfBean().vaPubblicazioneHome.merge(vaPubblicazione, idPfPrinc);
			} else {
				getVacancyFormPfBean().vaPubblicazioneHome.persist(vaPubblicazione, idPfPrinc);
			}	
	}

	public VaContatto getVaContatto() {
		return vaContatto;
	}

	public void setVaContatto(VaContatto vaContatto) {
		this.vaContatto = vaContatto;
	}

	public VaPubblicazione getVaPubblicazione() {
		return vaPubblicazione;
	}

	public void setVaPubblicazione(VaPubblicazione vaPubblicazione) {
		this.vaPubblicazione = vaPubblicazione;
	}
}
