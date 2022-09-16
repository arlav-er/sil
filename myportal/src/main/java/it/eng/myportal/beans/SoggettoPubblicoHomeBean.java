package it.eng.myportal.beans;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.myportal.dtos.SoggettoPubblicoDTO;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.WsEndpointHome;


@ManagedBean
@ViewScoped
public class SoggettoPubblicoHomeBean extends AbstractHomepageBean {

	private String tokenSARE;
	private String urlSARE;
	private String urlMyStage;
	
	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	WsEndpointHome wsEndpointHome;
	
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		try {
			if (session.isSoggettoPubblico()) {
				session.getParams().clear();
				SoggettoPubblicoDTO cert = session.getConnectedSoggetto();
				
				tokenSARE = null;
				urlMyStage =  session.getUrlMyStage();
				
			} else {
				addErrorMessage("soggetto.is_not");
				redirectHome();
			}
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	public String getTokenSARE() {
		return tokenSARE;
	}

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
