package it.eng.myportal.entity;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * AziendaInfo generated by hbm2java
 */
@Entity
@Table(name = "certificatore_info", schema = "mycas")
public class CertificatoreInfo extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 3312L;

	private PfPrincipal pfPrincipal;
	private Integer idPfPrincipal;

	private String nome;
	private String cognome;
	private String email;

	private String ragioneSociale;
	private String codiceFiscale;
	private String telefono;
	
	private Date dtmInizio;
	private Date dtmFine;	

	public CertificatoreInfo() {
	}

	@Id
	@Column(name = "id_pf_principal", unique = true, nullable = false)
	public Integer getIdPfPrincipal() {
		return this.idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

	@PrimaryKeyJoinColumn
	@OneToOne(fetch = FetchType.LAZY)
	public PfPrincipal getPfPrincipal() {
		return this.pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	@Column(name = "nome")
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "cognome")
	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	@Column(name = "email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dtm_inizio", nullable = false, length = 29)
	public Date getDtmInizio() {
		return this.dtmInizio;
	}

	public void setDtmInizio(Date dtmInizio) {
		this.dtmInizio = dtmInizio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dtm_fine", nullable = false, length = 29)
	public Date getDtmFine() {
		return this.dtmFine;
	}

	public void setDtmFine(Date dtmFine) {
		this.dtmFine = dtmFine;
	}

	@Column(name = "ragione_sociale")
	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	@Column(name = "codice_fiscale")
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	@Column(name = "telefono")
	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}	
}
