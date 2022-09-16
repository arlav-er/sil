package it.eng.myportal.beans;

import it.eng.myportal.dtos.CertificatoreInfoDTO;
import it.eng.myportal.entity.home.CertificatoreInfoHome;

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
public class CertificatoreInfoBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(CertificatoreInfoBean.class);

	@EJB
	private CertificatoreInfoHome certificatoreInfoHome;

	private CertificatoreInfoDTO data;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		log.debug("Costruito il Bean per Informazioni Certificatore!");
		try {
			if (session.isCertificatore()) {
				retrieveData();
			} else {
				addErrorMessage("certificatore.is_not");
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
		data = getSession().getConnectedCertificatore();
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
		setData(homeMerge(certificatoreInfoHome, getData()));
		getSession().refreshSession();
	}

	/**
	 * @return the data
	 */
	public CertificatoreInfoDTO getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(CertificatoreInfoDTO data) {
		this.data = data;
	}

	public Integer getPfPrincipalId() {
		return getSession().getPrincipalId();
	}

}
