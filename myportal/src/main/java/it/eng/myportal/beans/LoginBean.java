package it.eng.myportal.beans;

import it.eng.myportal.dtos.StNotiziaDTO;
import it.eng.myportal.entity.home.StNotiziaHome;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ManagedBean
@ViewScoped
public class LoginBean {

	
	private static final Log log = LogFactory.getLog(LoginBean.class);
	
	private Boolean login = true;
	
	private String errorMessage = "";
	
	private List<StNotiziaDTO> news;
	
	@EJB
	StNotiziaHome  notiziaHome;
	
	public LoginBean() {
		super();
	}

	@PostConstruct
	public void postConstruct() {
		ExternalContext ex = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> map = ex.getRequestMap();
		errorMessage = (String) map.get("error_msg");		
		news = notiziaHome.findAllCurrentDTO();
	}

	public Boolean getLogin() {
		return login;
	}

	public void setLogin(Boolean login) {
		this.login = login;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<StNotiziaDTO> getNews() {
		return news;
	}

	public void setNews(List<StNotiziaDTO> news) {
		this.news = news;
	}


	

}
