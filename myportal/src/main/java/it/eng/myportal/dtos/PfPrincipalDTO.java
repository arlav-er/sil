package it.eng.myportal.dtos;

import java.util.Date;

public class PfPrincipalDTO extends AbstractUpdatablePkDTO {

	private static final long serialVersionUID = -4859881211011496741L;
	private String username;
	private String passWord;
	private Date dtScadenza;
	private Boolean flagAbilitato;
	private Boolean flagAbilitatoServizi;
	private Boolean flagAbilitatoSare;
	private Boolean flagAbilitaPec;

	private String nome;
	private String cognome;
	private String email;
	private String confirmationToken;
	private Date dtmConfirm;
	private String stileSelezionato;
	private String domanda;
	private String risposta;
	private String recuperoPasswordToken;
	private String registrazioneForteToken;
	private String richiestaRegForteToken;
	private String telefonoUtente;
	private String codRuoloPortale;

	public PfPrincipalDTO() {

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public Date getDtScadenza() {
		return dtScadenza;
	}

	public void setDtScadenza(Date dtScadenza) {
		this.dtScadenza = dtScadenza;
	}

	public Boolean getFlagAbilitato() {
		return flagAbilitato;
	}

	public void setFlagAbilitato(Boolean flagAbilitato) {
		this.flagAbilitato = flagAbilitato;
	}

	public Boolean getFlagAbilitatoServizi() {
		return flagAbilitatoServizi;
	}

	public void setFlagAbilitatoServizi(Boolean flagAbilitatoServizi) {
		this.flagAbilitatoServizi = flagAbilitatoServizi;
	}

	public Boolean getFlagAbilitatoSare() {
		return flagAbilitatoSare;
	}

	public void setFlagAbilitatoSare(Boolean flagAbilitatoSare) {
		this.flagAbilitatoSare = flagAbilitatoSare;
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getConfirmationToken() {
		return confirmationToken;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	public Date getDtmConfirm() {
		return dtmConfirm;
	}

	public void setDtmConfirm(Date dtmConfirm) {
		this.dtmConfirm = dtmConfirm;
	}

	public String getStileSelezionato() {
		return stileSelezionato;
	}

	public void setStileSelezionato(String stileSelezionato) {
		this.stileSelezionato = stileSelezionato;
	}

	public String getDomanda() {
		return domanda;
	}

	public void setDomanda(String domanda) {
		this.domanda = domanda;
	}

	public String getRisposta() {
		return risposta;
	}

	public void setRisposta(String risposta) {
		this.risposta = risposta;
	}

	public String getRegistrazioneForteToken() {
		return registrazioneForteToken;
	}

	public void setRegistrazioneForteToken(String registrazioneForteToken) {
		this.registrazioneForteToken = registrazioneForteToken;
	}

	public String getRichiestaRegForteToken() {
		return richiestaRegForteToken;
	}

	public void setRichiestaRegForteToken(String richiestaRegForteToken) {
		this.richiestaRegForteToken = richiestaRegForteToken;
	}

	public String getRecuperoPasswordToken() {
		return recuperoPasswordToken;
	}

	public void setRecuperoPasswordToken(String recuperoPasswordToken) {
		this.recuperoPasswordToken = recuperoPasswordToken;
	}

	public String getTelefonoUtente() {
		return telefonoUtente;
	}

	public void setTelefonoUtente(String telefonoUtente) {
		this.telefonoUtente = telefonoUtente;
	}

	public Boolean getFlagAbilitaPec() {
		return flagAbilitaPec;
	}

	public void setFlagAbilitaPec(Boolean flagAbilitaPec) {
		this.flagAbilitaPec = flagAbilitaPec;
	}

	public String getCodRuoloPortale() {
		return codRuoloPortale;
	}

	public void setCodRuoloPortale(String codRuoloPortale) {
		this.codRuoloPortale = codRuoloPortale;
	}
}
