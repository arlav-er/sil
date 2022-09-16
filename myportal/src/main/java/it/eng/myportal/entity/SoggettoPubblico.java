package it.eng.myportal.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "soggetto_pubblico", schema = "mycas")
public class SoggettoPubblico extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 1009803886820008167L;
	private PfPrincipal pfPrincipal;
	private Integer idSoggettoPubblico;
	
	private String nome;
	private String cognome;
	private String email;
	
	private String ragioneSociale;
	private String codiceFiscale;
	private String telefono;

	private Date dtmInizio;
	private Date dtmFine;	
	
	public SoggettoPubblico() {
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return this.pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipalByIdPfPrincipal) {
		this.pfPrincipal = pfPrincipalByIdPfPrincipal;
	}

	@Id
	@SequenceGenerator(name = "soggetto_pubblico_id_soggetto_pubblico_seq", sequenceName = "mycas.soggetto_pubblico_id_soggetto_pubblico_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "soggetto_pubblico_id_soggetto_pubblico_seq")
	@Column(name = "id_soggetto_pubblico", unique = true, nullable = false)
	public Integer getIdSoggettoPubblico() {
		return this.idSoggettoPubblico;
	}

	public void setidSoggettoPubblico(Integer idSoggettoPubblico) {
		this.idSoggettoPubblico = idSoggettoPubblico;
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
