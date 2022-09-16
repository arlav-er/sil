package it.eng.myportal.beans;

import it.eng.myportal.dtos.DeTitoloDTO;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Backing Bean .
 *  
 */
@ManagedBean
@ViewScoped
public class AlberoTitoloBean {

	/**
	 * Logger per registrare informazioni.
	 */
	protected static Log log = LogFactory.getLog(AlberoTitoloBean.class);

	@EJB
	private DeTitoloHome deTitoloHome;

	private DeTitoloDTO deTitoloDTO;
	
	@PostConstruct
	public void postConstruct() {				
		log.debug("Costruito il Bean per il CurriculuVitae!");
	}
	
	/**
	 * @return the deTitoloHome
	 */
	public DeTitoloHome getDeTitoloHome() {
		return deTitoloHome;
	}

	/**
	 * @return the deTitoloDTO
	 */
	public DeTitoloDTO getDeTitoloDTO() {
		return deTitoloDTO;
	}

	/**
	 * @param deTitoloHome the deTitoloHome to set
	 */
	public void setDeTitoloHome(DeTitoloHome deTitoloHome) {
		this.deTitoloHome = deTitoloHome;
	}

	/**
	 * @param deTitoloDTO the deTitoloDTO to set
	 */
	public void setDeTitoloDTO(DeTitoloDTO deTitoloDTO) {
		this.deTitoloDTO = deTitoloDTO;
	}
	

}
