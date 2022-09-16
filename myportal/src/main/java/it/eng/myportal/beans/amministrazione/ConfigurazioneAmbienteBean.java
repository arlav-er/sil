package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.dtos.ConfigurazioneDatabaseDTO;
import it.eng.myportal.dtos.ConfigurazioneWsEndpointDTO;
import it.eng.myportal.entity.home.ConfigurazioneAmbienteHome;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class ConfigurazioneAmbienteBean {

	@EJB
	private ConfigurazioneAmbienteHome configurazioneAmbienteHome;

	private List<ConfigurazioneDatabaseDTO> confDatabaseDtos = new ArrayList<ConfigurazioneDatabaseDTO>();
	private List<ConfigurazioneWsEndpointDTO> wsEndpoints = new ArrayList<ConfigurazioneWsEndpointDTO>();

	public ConfigurazioneAmbienteBean() {
		super();
	}

	@PostConstruct
	protected void postConstruct() {
		confDatabaseDtos = configurazioneAmbienteHome.getConfigurazioniHibernate();
		// ws endpoint
		wsEndpoints = configurazioneAmbienteHome.getWebServiceEndpoint();
	}

	public List<ConfigurazioneDatabaseDTO> getConfDatabaseDtos() {
		return confDatabaseDtos;
	}

	public void setConfDatabaseDtos(List<ConfigurazioneDatabaseDTO> confDatabaseDtos) {
		this.confDatabaseDtos = confDatabaseDtos;
	}

	public List<ConfigurazioneWsEndpointDTO> getWsEndpoints() {
		return wsEndpoints;
	}

	public void setWsEndpoints(List<ConfigurazioneWsEndpointDTO> wsEndpoints) {
		this.wsEndpoints = wsEndpoints;
	}

}
