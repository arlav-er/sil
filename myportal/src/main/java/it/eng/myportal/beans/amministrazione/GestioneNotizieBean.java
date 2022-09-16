package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.StNotiziaDTO;
import it.eng.myportal.entity.home.StNotiziaHome;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**

 * 
 */
@ManagedBean
@ViewScoped
public class GestioneNotizieBean extends AbstractBaseBean {

	protected static Log log = LogFactory.getLog(GestioneNotizieBean.class);

	private List<StNotiziaDTO> listaNotizie;
	private StNotiziaDTO notizia;

	private static int adminIdPfPrincipal = 0;
	private LazyStNotiziaHomeModel searchResult;

	@EJB
	private StNotiziaHome notiziaHome;

	/**
	 * Metodo chiamato alla creazione del bean. Inizializza la lista delle notizie.
	 */
	@PostConstruct
	public void postConstruct() {
		if (!"amministratore".equals(getSession().getUsername())) {
			redirectPublicIndex();
		}
		listaNotizie = notiziaHome.findAllDTO();
		//System.out.println("Final size =" + listaNotizie.size());
		Collections.sort(listaNotizie);
		searchResult = new LazyStNotiziaHomeModel(listaNotizie);
	}

	/**
	 * Metodo chiamato all'inserimento di una nuova notizia.
	 */
	public void sync() {
        FacesContext context = FacesContext.getCurrentInstance();
		try {
			if (notizia.getId() != null) {
				notizia = notiziaHome.mergeDTO(notizia, adminIdPfPrincipal);
				context.addMessage(null, 
						new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Notizia modificata."));
				listaNotizie.remove(notizia);
			} else {
				notizia = notiziaHome.persistDTO(notizia, adminIdPfPrincipal);
				context.addMessage(null, 
						new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Nuova notizia inserita."));
			}
			listaNotizie.add(notizia);
			Collections.sort(listaNotizie); 
		} catch (Exception e) {
			context.addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errore", "Errore durante l'inserimento."));
		}
	}

	/**
	 * Metodo chiamato all'eliminazione di una notizia.
	 */
	public void eliminaNotizia() {
        FacesContext context = FacesContext.getCurrentInstance();
		try {
			notiziaHome.removeById(notizia.getId(), adminIdPfPrincipal);
			listaNotizie.remove(notizia);
			context.addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Notizia eliminata."));
		} catch (Exception e) {
			context.addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errore", "Errore durante l'eliminazione."));
		}
	}
	
	/*
	 * ================== DA QUI IN POI CI SONO NORMALI GETTER E SETTER ====================
	 */
	public List<StNotiziaDTO> getListaNotizie() {
		return listaNotizie;
	}

	public void setListaNotizie(List<StNotiziaDTO> listaNotizie) {
		this.listaNotizie = listaNotizie;
	}

	public StNotiziaDTO getNotizia() {
		return notizia;
	}

	public void setNotizia(StNotiziaDTO notizia) {
		this.notizia = notizia;
	}

	public void nuovaNotizia() {
		notizia = new StNotiziaDTO();
	}

	public LazyStNotiziaHomeModel getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(LazyStNotiziaHomeModel searchResult) {
		this.searchResult = searchResult;
	}
	
}
