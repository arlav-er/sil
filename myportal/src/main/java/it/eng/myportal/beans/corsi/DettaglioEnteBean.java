package it.eng.myportal.beans.corsi;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.OrEnteCorsoDTO;
import it.eng.myportal.entity.home.OrEnteCorsoHome;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * BackingBean della pagina di ricerca dei corsi ministeriali
 * 
 * @author Enrico D'Angelo
 * 
 */
@ManagedBean
@ViewScoped
public class DettaglioEnteBean extends AbstractBaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5707184558130094752L;
	
	@EJB
	OrEnteCorsoHome orEnteCorsoHome;

	private OrEnteCorsoDTO data;
	
	public OrEnteCorsoDTO getData() {
		return data;
	}

	public void setData(OrEnteCorsoDTO data) {
		this.data = data;
	}

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		try {
			Map<String, String> map = getRequestParameterMap();
			int orEnteCorsoId = Integer.parseInt(map.get("id"));
			
			data = orEnteCorsoHome.findDTOById(orEnteCorsoId);
		} catch (EJBException e) { // in caso di errori durante il recupero dei dati ritorna all'HomePage
			addErrorMessage("data.error_loading",e);
			redirectHome();
		}
	}
}
