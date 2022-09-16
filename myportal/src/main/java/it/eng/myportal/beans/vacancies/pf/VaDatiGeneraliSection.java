package it.eng.myportal.beans.vacancies.pf;

import java.util.Date;

import org.primefaces.context.RequestContext;

import it.eng.myportal.beans.UtilsBean;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.decodifiche.DeBpMansione;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.exception.MyPortalException;

public class VaDatiGeneraliSection extends VacancyBaseFormSection implements IVacancySection {

	private DeBpMansione professione;

	public VaDatiGeneraliSection(VacancyFormPfBean cvBean) {
		super(cvBean);
	}

	@Override
	public void initSection() {
	}

	@Override
	public void sync() {
        Integer idPfPrinc = getVacancyFormPfBean().getUtenteCompletoDTO().getIdPfPrincipal();
		VaDatiVacancy vaDatiVacancyToSave = getVacancyFormPfBean().getVaDatiVacancy();
		vaDatiVacancyToSave.setOpzTipoDecodifiche("S"); 
		VaDatiVacancyHome vacancyHome = getVacancyFormPfBean().getVaDatiVacancyHome();		 
		
		if (vaDatiVacancyToSave.getDtmIns() == null) {
			//New Salvataggio con Allegato
			Integer numRichiesta = vacancyHome.getNextNumRichiesta(vaDatiVacancyToSave.getNumAnno(), vaDatiVacancyToSave.getDeProvenienzaVacancy().getCodProvenienza());
			vaDatiVacancyToSave.setNumRichiesta(numRichiesta);
			try { 
				VaDatiVacancy savedCvDatiPersonali = vacancyHome.persist(vaDatiVacancyToSave,idPfPrinc);
				getVacancyFormPfBean().setVaDatiVacancy(savedCvDatiPersonali);
				RequestContext.getCurrentInstance().addCallbackParam("vacancyId",
						savedCvDatiPersonali.getIdVaDatiVacancy());
			}
			catch (MyPortalException e) {
				log.error(e.getMessage());
				addAlertErrorMessage(e.getCodErrore(), e.getMessage());
			}
			vaDatiVacancyToSave.setAttivitaPrincipale(vaDatiVacancyToSave.getAttivitaPrincipale().toUpperCase());
			addAlertSuccessMessage("Anuncio creato", "Il tuo annuncio è stato creato ma non è ancora visibile.\n" + 
					"Arricchisci l'offerta compilando anche le altre sezioni.\n" + 
					"Clicca sul pulsante Pubblica per renderla visibile.");
		} else {
			//La data di pubblicazione deve essere sempre >= della data corrente in qualsiasi momento sto provoando a modificare la vacancy se in stato LAV
			if((vaDatiVacancyToSave.getCodStatoVacancyEnum() != null && vaDatiVacancyToSave.getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.LAV) ) && (vaDatiVacancyToSave.getDtPubblicazione() != null &&  vaDatiVacancyToSave.getDtPubblicazione().before(UtilsBean.getZeroTimeDate(new Date())))) {
				RequestContext.getCurrentInstance().addCallbackParam("errore", true);
				addAlertErrorMessage("Non è possibile modificare l'offerta di lavoro", "La data di pubblicazione deve essere successiva o uguale alla data corrente");
			}else {
			   VaDatiVacancy mergedCvDatiPersonali = vacancyHome.merge(vaDatiVacancyToSave);
			   getVacancyFormPfBean().setVaDatiVacancy(mergedCvDatiPersonali);
			}
		}
	}

	public void gruppoProfessionaleNodeSelected(org.primefaces.event.NodeSelectEvent event) {
		getVacancyFormPfBean().getVaDatiVacancy().setDeBpMansione((DeBpMansione) event.getTreeNode().getData());
		RequestContext.getCurrentInstance().execute("PF('gruppoProfessionaleWV').selectionComplete();");
	}

	public DeBpMansione getProfessione() {
		return professione;
	}

	public void setProfessione(DeBpMansione professione) {
		this.professione = professione;
	}
 
	public void warnFutureDtPubb() {
		VaDatiVacancy vaDatiVacancy  = getVacancyFormPfBean().getVaDatiVacancy();
		if (vaDatiVacancy.getDtPubblicazione() != null && vaDatiVacancy.getDtPubblicazione().after(new Date())) {
			addJSSuccessMessage("Stai programmando la pubblicazione dell'offerta in una <b>data futura</b>.\n" + 
					"L'annuncio sarà visualizzato dai cittadini solo a partire dal giorno della data pubblicazione indicata");
		}
	}

}
