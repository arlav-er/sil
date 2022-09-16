package it.eng.myportal.beans.amministrazione.pojo;

import java.io.Serializable;
import java.util.Date;

public class UtentePojo implements Serializable {

	private static final long serialVersionUID = -3576934256748093731L;

	private String confirmationToken;
	private Date dtScadenza;
	private String email;
	private boolean flagAbilitato;
	private boolean flagAbilitatoSare;
	private boolean flagAbilitatoServizi;
	private int idPfPrincipal;
	private String provinciaRiferimento;
	private String socialNetwork;
	private String username;

	public UtentePojo() {
	}

	public String getConfirmationToken() {
		return confirmationToken;
	}

	public Date getDtScadenza() {
		return dtScadenza;
	}

	public String getEmail() {
		return email;
	}

	public int getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public String getProvinciaRiferimento() {
		return provinciaRiferimento;
	}

	public String getSocialNetwork() {
		return socialNetwork;
	}

	public String getUsername() {
		return username;
	}

	public boolean isFlagAbilitato() {
		return flagAbilitato;
	}

	public boolean isFlagAbilitatoSare() {
		return flagAbilitatoSare;
	}

	public boolean isFlagAbilitatoServizi() {
		return flagAbilitatoServizi;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	public void setDtScadenza(Date dtScadenza) {
		this.dtScadenza = dtScadenza;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFlagAbilitato(boolean flagAbilitato) {
		this.flagAbilitato = flagAbilitato;
	}

	public void setFlagAbilitatoSare(boolean flagAbilitatoSare) {
		this.flagAbilitatoSare = flagAbilitatoSare;
	}

	public void setFlagAbilitatoServizi(boolean flagAbilitatoServizi) {
		this.flagAbilitatoServizi = flagAbilitatoServizi;
	}

	public void setIdPfPrincipal(int idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

	public void setProvinciaRiferimento(String provinciaRiferimento) {
		this.provinciaRiferimento = provinciaRiferimento;
	}

	public void setSocialNetwork(String socialNetwork) {
		this.socialNetwork = socialNetwork;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
