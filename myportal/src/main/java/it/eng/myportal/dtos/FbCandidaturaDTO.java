package it.eng.myportal.dtos;

public class FbCandidaturaDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = -7695702458585795780L;

	private Integer idFbSchedaFabbisogno;
	private String nome;
	private String cognome;
	private String codiceFiscale;
	private String email;
	private boolean flagIdoneo;

	// Lo metto qui per avercelo a front-end
	private String soggettoPromotore;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSoggettoPromotore() {
		return soggettoPromotore;
	}

	public void setSoggettoPromotore(String soggettoPromotore) {
		this.soggettoPromotore = soggettoPromotore;
	}

	public Integer getIdFbSchedaFabbisogno() {
		return idFbSchedaFabbisogno;
	}

	public void setIdFbSchedaFabbisogno(Integer idFbSchedaFabbisogno) {
		this.idFbSchedaFabbisogno = idFbSchedaFabbisogno;
	}

	public boolean isFlagIdoneo() {
		return flagIdoneo;
	}

	public void setFlagIdoneo(boolean flagIdoneo) {
		this.flagIdoneo = flagIdoneo;
	}

}
