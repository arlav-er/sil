package it.eng.myportal.beans;

import it.eng.myportal.dtos.CertificatoreInfoDTO;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.WsEndpointHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


@ManagedBean
@ViewScoped
public class CertificatoreHomeBean extends AbstractHomepageBean {

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
			if (session.isCertificatore()) {
				session.getParams().clear();
				CertificatoreInfoDTO cert = session.getConnectedCertificatore();
				
				tokenSARE = null;
				urlMyStage =  wsEndpointHome.getMyStageAddress();
				
			} else {
				addErrorMessage("certificatore.is_not");
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
