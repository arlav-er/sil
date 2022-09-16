package it.eng.myportal.beans;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.myportal.dtos.RegioneDTO;
import it.eng.myportal.entity.home.RegioneHome;
import it.eng.myportal.entity.home.WsEndpointHome;

/**
 * BackingBean dell'HomePage della Regione.<br/>
 * 
 * <br/>
 * <b>Elenco restrizioni di sicurezza</b>
 * <ul>
 * <li>L'utente connesso deve essere del CPI della provincia.</li>
 * </ul>
 * 
 */
@ManagedBean
@ViewScoped
public class RegioneHomeBean extends AbstractHomepageBean {

	private String tokenSARE;
	private String urlSARE;
	private String urlMyStage;

	@EJB
	private RegioneHome regioneHome;

	@EJB
	WsEndpointHome wsEndpointHome;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		try {
			if (session.isRegione()) {
				// Clean-up delle informazioni in sessione
				session.getParams().clear();
				RegioneDTO regione = session.getConnectedRegione();

				tokenSARE = null;
				urlMyStage = session.getUrlMyStage();

				log.debug("Costruito il Bean per Home Page Regione!");
			} else {
				addErrorMessage("regione.is_not");
				redirectHome();
			}
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	/**
	 * @return the tokenSARE
	 */
	public String getTokenSARE() {
		return tokenSARE;
	}

	/**
	 * @param tokenSARE
	 *            the tokenSARE to set
	 */
	public void setTokenSARE(String tokenSARE) {
		this.tokenSARE = tokenSARE;
	}

	public String getUrlSARE() {
		return urlSARE;
	}

	public void setUrlSARE(String urlSARE) {
		this.urlSARE = urlSARE;
	}

	public String getUrlMyStage() {
		return urlMyStage;
	}

	public void setUrlMyStage(String urlMyStage) {
		this.urlMyStage = urlMyStage;
	}
 
}
