package it.eng.myportal.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * BackingBean che gestisce la pagina degli errori.
 * La pagina prende in input l'elenco dei codici errore e li mostra all'utente.
 * 
 * @author Rodi
 * 
 *  
 */
@ManagedBean
@ViewScoped
public class ErrorsBean {
		
	/**
	 * Elenco degli errori, divisi da ',', che arriva dalla GET 
	 */
	private String errorsList = "";
	
	
	/**
	 * Quando accedo alla pagina distruggo (per punizione)
	 * la sessione all'utente.
	 */
	@PostConstruct
	public void postConstruct() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		if (session != null)
			session.invalidate();		
	}
	
	/**
	 * Elenco degli errori trasformato in lista
	 */
	private List<String> errors = new ArrayList<String>();
	
	/**
	 * Metodo richiamato al caricamento della pagina,
	 * dopo aver setttato i viewParam (errorsList) 
	 * ma prima di renderizzare la pagina.
	 */
	public void fetchErrors() {
		errors = Arrays.asList(errorsList.split(","));
	}
	
	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public String getErrorsList() {
		return errorsList;
	}

	public void setErrorsList(String errorsList) {
		this.errorsList = errorsList;
	}
	
}
