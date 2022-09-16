package it.eng.myportal.dtos;

import java.util.Date;


public class CertificatoreInfoDTO extends AbstractUpdatablePkDTO implements IUpdatable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	
	private PfPrincipalDTO principal;
	private String nome;
	private String cognome;
	private String email;
	private String stileSelezionato;
	private Date inizio;
	private Date fine;
	
	private String ragioneSociale;
	private String codiceFiscale;
	private String telefono;
	
	
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
	public String getStileSelezionato() {
		return stileSelezionato;
	}
	public void setStileSelezionato(String stileSelezionato) {
		this.stileSelezionato = stileSelezionato;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public PfPrincipalDTO getPrincipal() {
		return principal;
	}
	public void setPrincipal(PfPrincipalDTO principal) {
		this.principal = principal;
	}
	public Date getInizio() {
		return inizio;
	}
	public void setInizio(Date inizio) {
		this.inizio = inizio;
	}
	public Date getFine() {
		return fine;
	}
	public void setFine(Date fine) {
		this.fine = fine;
	}

	
	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

}
