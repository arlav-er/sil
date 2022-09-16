package it.eng.myportal.beans.vacancies.pf;

import it.eng.myportal.entity.VaAltreInfo;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaEsperienze;
import it.eng.myportal.entity.VaPubblicazione;
import it.eng.myportal.entity.home.VaAltreInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaEsperienzeHome;

public class VaAltreInfoSection extends VacancyBaseFormSection implements IVacancySection {

	private VaEsperienzeHome vaEsperienzeHome;

	private VaAltreInfoHome vaAltreInfoHome;
	private VaDatiVacancy vaDatiVacancy;
	private VaAltreInfo vaAltreInfo;
	private VaEsperienze vaEsperienze;
	private Integer vacancyId;

	VaPubblicazione vaPubblicazione;


	public VaAltreInfoSection(VacancyFormPfBean cvBean, VaAltreInfoHome vaAltreInfoHome,
			VaEsperienzeHome vaEsperienzeHome) {
		super(cvBean);
		this.vaAltreInfoHome = vaAltreInfoHome;
		this.vaEsperienzeHome = vaEsperienzeHome;

	}

	@Override
	public void initSection() {
		vacancyId = getVacancyFormPfBean().getVaDatiVacancy().getIdVaDatiVacancy();
		vaDatiVacancy = getVacancyFormPfBean().getVaDatiVacancy();

		vaAltreInfo = vaAltreInfoHome.findByVacancyId(vacancyId);
		if (vaAltreInfo == null) {
			vaAltreInfo = new VaAltreInfo();
		} else
			vaAltreInfo.setVaDatiVacancy(vaDatiVacancy);

		vaEsperienze = vaEsperienzeHome.findByVacancyId(vacancyId);
		if (vaEsperienze == null)
			vaEsperienze = new VaEsperienze();
		else
			vaEsperienze.setVaDatiVacancy(vaDatiVacancy);
		
		vaPubblicazione = getVacancyFormPfBean().vaPubblicazioneHome.findByVacancyId(vacancyId);
		if(vaPubblicazione == null ) {
			vaPubblicazione = new VaPubblicazione();		
		}
	}

	public void handleChange() {
		if (vaAltreInfo.getOpzTrasferta() !=null && vaAltreInfo.getOpzTrasferta().equalsIgnoreCase("N")) {
			vaAltreInfo.setInfoTrasferta("");
		}
/*
		if (vaAltreInfo.getOpzFuorisede() !=null && vaAltreInfo.getOpzFuorisede().equalsIgnoreCase("N")) {
			vaAltreInfo.setInfoTrasferta("");
		}
*/		
		if (vaEsperienze.getOpzEsperienza()!=null && vaEsperienze.getOpzEsperienza().equalsIgnoreCase("N")) {
			vaEsperienze.setNumAnniEsperienza(null);
		}
		if (vaAltreInfo.getFlagMezzoPubblico() != null && !vaAltreInfo.getFlagMezzoPubblico()) {
			vaAltreInfo.setInfoMezzoPubblico("");
		}
	}

	public VaAltreInfo getVaAltreInfo() {
		return vaAltreInfo;
	}

	public void setVaAltreInfo(VaAltreInfo vaAltreInfo) {
		this.vaAltreInfo = vaAltreInfo;
	}

	public VaEsperienze getVaEsperienze() {
		return vaEsperienze;
	}

	public void setVaEsperienze(VaEsperienze vaEsperienze) {
		this.vaEsperienze = vaEsperienze;
	}

	@Override
	public void sync() {
		VaDatiVacancyHome homeEJB = getVacancyFormPfBean().getVaDatiVacancyHome();
		Integer idPfPrinc = getVacancyFormPfBean().getUtenteCompletoDTO().getIdPfPrincipal();

		vaAltreInfo.setVaDatiVacancy(this.getVacancyFormPfBean().getVaDatiVacancy());
		vaEsperienze.setVaDatiVacancy(this.getVacancyFormPfBean().getVaDatiVacancy());

		if (vaAltreInfo.getDtmIns() == null) {
			vaAltreInfo = vaAltreInfoHome.persist(vaAltreInfo, idPfPrinc);
			log.info("Inserimento Altre Info:" + vaAltreInfo.getIdVaDatiVacancy());

		} else {
			vaAltreInfo = vaAltreInfoHome.merge(vaAltreInfo, idPfPrinc);
			log.info("Update Altre Info:" + vaAltreInfo.getIdVaDatiVacancy());

		}

		if (vaEsperienze.getDtmIns() == null) {
			vaEsperienze = vaEsperienzeHome.persist(vaEsperienze, idPfPrinc);
			log.info("Inserimento Esperienze:" + vaEsperienze.getIdVaEsperienze());
		} else {
			vaEsperienze = vaEsperienzeHome.merge(vaEsperienze, idPfPrinc);
			log.info("Update Esperienze:" + vaEsperienze.getIdVaEsperienze());
		}
		getVacancyFormPfBean().getVaDatiVacancy().setVaEsperienze(vaEsperienze);
		getVacancyFormPfBean().getVaDatiVacancy().setVaAltreInfo(vaAltreInfo);
		
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

	public VaPubblicazione getVaPubblicazione() {
		return vaPubblicazione;
	}

	public void setVaPubblicazione(VaPubblicazione vaPubblicazione) {
		this.vaPubblicazione = vaPubblicazione;
	}
}
