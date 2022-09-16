package it.eng.myportal.beans;

import it.eng.myportal.dtos.PatronatoDTO;
import it.eng.myportal.entity.home.WsEndpointHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

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
public class PatronatoHomeBean extends AbstractHomepageBean {

	private String tokenSARE;
	private String urlSARE;	
	
	
	@EJB
	WsEndpointHome wsEndpointHome;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		try {
			if (session.isPatronato()) {
				// Clean-up delle informazioni in sessione
				session.getParams().clear();
				PatronatoDTO regione = session.getConnectedPatronato();

				tokenSARE = null;				
				
				log.debug("Costruito il Bean per Home Page Patronato!");
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

}
