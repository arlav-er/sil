package it.eng.myportal.dtos;

public class ConfigurazioneDatabaseDTO implements IDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6368199624932258635L;

	private String descrizione;

	private String url;

	private String username;

	private String numMaxConnessioni;

	private String numConnAperte;

	public ConfigurazioneDatabaseDTO() {
		super();
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNumMaxConnessioni() {
		return numMaxConnessioni;
	}

	public void setNumMaxConnessioni(String numMaxConnessioni) {
		this.numMaxConnessioni = numMaxConnessioni;
	}

	public String getNumConnAperte() {
		return numConnAperte;
	}

	public void setNumConnAperte(String numConnAperte) {
		this.numConnAperte = numConnAperte;
	}

}
