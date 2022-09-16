package it.eng.myportal.dtos;

public class ConfigurazioneWsEndpointDTO implements IDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7405058701475724957L;

	private String wsUrl;

	private String descrizioneServizio;
	
	private String descrProvincia;

	public String getDescrProvincia() {
		return descrProvincia;
	}

	public void setDescrProvincia(String descrProvincia) {
		this.descrProvincia = descrProvincia;
	}

	public ConfigurazioneWsEndpointDTO() {
		super();
	}

	public String getWsUrl() {
		return wsUrl;
	}

	public void setWsUrl(String wsUrl) {
		this.wsUrl = wsUrl;
	}

	public String getDescrizioneServizio() {
		return descrizioneServizio;
	}

	public void setDescrizioneServizio(String descrizioneServizio) {
		this.descrizioneServizio = descrizioneServizio;
	}

}
