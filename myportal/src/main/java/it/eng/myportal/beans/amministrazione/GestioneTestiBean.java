package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.PfTestiDTO;
import it.eng.myportal.entity.home.PfTestiHome;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**

 * 
 */
@ManagedBean
@ViewScoped
public class GestioneTestiBean extends AbstractBaseBean {

	
	protected static Log log = LogFactory.getLog(GestioneTestiBean.class);


	private List<PfTestiDTO> listaTesti;
	private PfTestiDTO nuovoTesto;
	private PfTestiDTO testoCorrente;
	
	
	
	@EJB
	PfTestiHome pfTestiHome;
	
	@PostConstruct
	public void postConstruct() {
		if (!"amministratore".equals(getSession().getUsername())) {
		 redirectPublicIndex();
		}				
		listaTesti = pfTestiHome.findAllDTO();
		nuovoTesto = new PfTestiDTO();
		testoCorrente = new PfTestiDTO();
	}

	public List<PfTestiDTO> getListaTesti() {
		return listaTesti;
	}

	public void setListaTesti(List<PfTestiDTO> listaTesti) {
		this.listaTesti = listaTesti;
	}

	public PfTestiDTO getNuovaTesto() {
		return nuovoTesto;
	}

	public void setNuovoTesto(PfTestiDTO nuovoTesto) {
		this.nuovoTesto = nuovoTesto;
	}

	public void rimuoviTesto() {
		Map<String, String> map = getRequestParameterMap();
		String id = map.get("id");
		for (PfTestiDTO element : listaTesti) {
			if (element.getId() != null && element.getId().toString().equals(id)) {
				pfTestiHome.removeById(Integer.parseInt(id), new Integer(0));
				break;
			}
		}
		listaTesti = pfTestiHome.findAllDTO();
	}
	
	public void caricaTesto() {
		Map<String, String> map = getRequestParameterMap();
		String id = map.get("id");
		testoCorrente = pfTestiHome.findDTOById(Integer.parseInt(id));
	}
	
	public void inserisciTesto() {
		
		pfTestiHome.persistDTO(nuovoTesto, 0);
		
	}

	public void aggiornaTesto() {
		if (testoCorrente.getId() == null) {
			return;
		}
		pfTestiHome.mergeDTO(testoCorrente, 0);
		listaTesti = pfTestiHome.findAllDTO();
		
	}
	
	public PfTestiDTO getTestoCorrente() {
		return testoCorrente;
	}

	public void setTestoCorrente(PfTestiDTO testoCorrente) {
		this.testoCorrente = testoCorrente;
	}
	
	public void pulisciTestoCorrente() {
		testoCorrente = new PfTestiDTO();
	}
	
}
