package it.eng.myportal.dtos;

import java.util.Date;

/**
 * Classe DTO con tutte le informazioni ulteriori su un'azienda.
 * 
 * @author Turro
 * 
 */
public class ConsulenteInfoDTO extends AbstractUpdatablePkDTO implements IUpdatable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private DeTipoPraticaDTO deTipoPratica;
	private DeAtpEnteConsulenteDTO sindacato;
	private String nome;
	private String cognome;
	private String username;
	private String email;
	private Integer monteOre;
	private Integer oreSvolte;
	private String opzPrivilegio;
	private String opzAtipicoPIva;
	private String stileSelezionato;
	private Date inizio;
	private Date fine;

	public String getStileSelezionato() {
		return stileSelezionato;
	}

	public void setStileSelezionato(String stileSelezionato) {
		this.stileSelezionato = stileSelezionato;
	}

	public DeAtpEnteConsulenteDTO getSindacato() {
		return sindacato;
	}

	public void setSindacato(DeAtpEnteConsulenteDTO sindacato) {
		this.sindacato = sindacato;
	}

	public Integer getMonteOre() {
		return monteOre;
	}

	public void setMonteOre(Integer monteOre) {
		this.monteOre = monteOre;
	}

	public Integer getOreSvolte() {
		return oreSvolte;
	}

	public void setOreSvolte(Integer oreSvolte) {
		this.oreSvolte = oreSvolte;
	}

	public String getOpzPrivilegio() {
		return opzPrivilegio;
	}

	public void setOpzPrivilegio(String opzPrivilegio) {
		this.opzPrivilegio = opzPrivilegio;
	}

	public String getOpzAtipicoPIva() {
		return opzAtipicoPIva;
	}

	public void setOpzAtipicoPIva(String opzAtipicoPIva) {
		this.opzAtipicoPIva = opzAtipicoPIva;
	}

	public DeTipoPraticaDTO getDeTipoPratica() {
		return deTipoPratica;
	}

	public void setDeTipoPratica(DeTipoPraticaDTO deTipoPratica) {
		this.deTipoPratica = deTipoPratica;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
