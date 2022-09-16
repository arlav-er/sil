package it.eng.myportal.dtos;

import java.util.Date;

/**
 * Classe contenitore delle informazioni su una Provincia in sessione.<br/>
 * Inserire in questo DTO <b>solo le informazioni che devono essere mantenute in sessione</b>
 * 
 * @author Rodi A.
 *
 */
public class ProvinciaDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = 1615195624709382694L;

	private Integer idPfPrincipal;
	private DeProvinciaDTO provincia;
	private String stileSelezionato;
	private Boolean flagTematica;
	private Boolean flagLavoro;
	private String nome;
	private String cognome;
	private String username;
	private String email;
	private Date dtInizio;
	private Date dtFine;
	private String opzPrivilegio;
	private Boolean flagAtipico;
	private Boolean flagPiva;

	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

	public DeProvinciaDTO getProvincia() {
		return provincia;
	}

	public void setProvincia(DeProvinciaDTO provincia) {
		this.provincia = provincia;
	}

	public String getStileSelezionato() {
		return stileSelezionato;
	}

	public void setStileSelezionato(String stileSelezionato) {
		this.stileSelezionato = stileSelezionato;
	}

	public Boolean getFlagTematica() {
		return flagTematica;
	}

	public void setFlagTematica(Boolean flagTematica) {
		this.flagTematica = flagTematica;
	}

	public Boolean getFlagLavoro() {
		return flagLavoro;
	}

	public void setFlagLavoro(Boolean flagLavoro) {
		this.flagLavoro = flagLavoro;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getDtInizio() {
		return dtInizio;
	}

	public void setDtInizio(Date dtInizio) {
		this.dtInizio = dtInizio;
	}

	public Date getDtFine() {
		return dtFine;
	}

	public void setDtFine(Date dtFine) {
		this.dtFine = dtFine;
	}

	public String getOpzPrivilegio() {
		return opzPrivilegio;
	}

	public void setOpzPrivilegio(String opzPrivilegio) {
		this.opzPrivilegio = opzPrivilegio;
	}

	public Boolean getFlagAtipico() {
		return flagAtipico;
	}

	public void setFlagAtipico(Boolean flagAtipico) {
		this.flagAtipico = flagAtipico;
	}

	public Boolean getFlagPiva() {
		return flagPiva;
	}

	public void setFlagPiva(Boolean flagPiva) {
		this.flagPiva = flagPiva;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
