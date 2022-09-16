package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.entity.ejb.AddAzVacancyClicLavoroEjb;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 * 
 */
@ManagedBean
@ViewScoped
public class AggiornaVacancyClBean {

	private String numeroVacancy;
	
	@EJB
	AddAzVacancyClicLavoroEjb addAzVacancyClicLavoroEjb;
	
	public AggiornaVacancyClBean() {
		super();
	}
		
	@PostConstruct
	protected void postConstruct() {		
	}

	public void aggiorna() {
		try {			
			Integer num = new Integer(numeroVacancy);			
			addAzVacancyClicLavoroEjb.aggiornaVacancyClicLavoro(num);
			
			FacesMessage message = new FacesMessage();
			message.setSummary("Aggiornamento effettuato correttamente");
			message.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);			
		} catch (RuntimeException e) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Errore durante l'aggiornamento delle vacancy. Spiacenti");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public String getNumeroVacancy() {
		return numeroVacancy;
	}

	public void setNumeroVacancy(String numeroVacancy) {
		this.numeroVacancy = numeroVacancy;
	}



}
