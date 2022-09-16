package it.eng.myportal.beans;

import it.eng.myportal.dtos.CoordinatoreInfoDTO;
import it.eng.myportal.entity.home.CoordinatoreInfoHome;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * BackingBean della pagina delle preferenze del coordinatore.<br/>
 * <br/>
 * 
 * <b>Elenco restrizioni di sicurezza</b>
 * <ul>
 * <li>L'utente connesso deve essere un coordinatore</li>
 * </ul>
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class CoordinatoreInfoBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(CoordinatoreInfoBean.class);

	@EJB
	private CoordinatoreInfoHome coordinatoreInfoHome;

	private CoordinatoreInfoDTO data;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		log.debug("Costruito il Bean per Informazioni Coordinatore!");
		try {
			if (session.isCoordinatore()) {
				retrieveData();
			} else {
				addErrorMessage("coordinatore.is_not");
				redirectHome();
			}
		} catch (EJBException e) {
			// in caso di errori durante il recupero dei dati ritorna
			// all'HomePage
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	private void retrieveData() {
		data = getSession().getConnectedCoordinatore();
	}

	/**
	 * Metodo collegato al bottone 'Salva' del detail
	 * 
	 */
	public void save() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".save");
		try {
			saveData();
			addInfoMessage("data.updated");
		} catch (EJBException e) {
			gestisciErrore(e, "data.error_saving");
		} finally {
			jamonMonitor.stop();
		}
	}

	private void saveData() {
		Date now = new Date();
		data.setDtmMod(now);
		setData(homeMerge(coordinatoreInfoHome, getData()));
		getSession().refreshSession();
	}

	/**
	 * @return the data
	 */
	public CoordinatoreInfoDTO getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(CoordinatoreInfoDTO data) {
		this.data = data;
	}

	public Integer getPfPrincipalId() {
		return getSession().getPrincipalId();
	}

}
