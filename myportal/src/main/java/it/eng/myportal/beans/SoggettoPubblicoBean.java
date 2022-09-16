package it.eng.myportal.beans;

import it.eng.myportal.dtos.CertificatoreInfoDTO;
import it.eng.myportal.dtos.SoggettoPubblicoDTO;
import it.eng.myportal.entity.home.SoggettoPubblicoHome;

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

@ManagedBean
@ViewScoped
public class SoggettoPubblicoBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(SoggettoPubblicoBean.class);

	@EJB
	private SoggettoPubblicoHome soggettoPubblicoHome;

	private SoggettoPubblicoDTO data;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		log.debug("Costruito il Bean per Informazioni Certificatore!");
		try {
			if (session.isSoggettoPubblico()) {
				retrieveData();
			} else {
				addErrorMessage("soggetto.is_not");
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
		data = getSession().getConnectedSoggetto();
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
		setData(homeMerge(soggettoPubblicoHome, getData()));
		getSession().refreshSession();
	}

	/**
	 * @return the data
	 */
	public SoggettoPubblicoDTO getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(SoggettoPubblicoDTO data) {
		this.data = data;
	}

	public Integer getPfPrincipalId() {
		return getSession().getPrincipalId();
	}

}
