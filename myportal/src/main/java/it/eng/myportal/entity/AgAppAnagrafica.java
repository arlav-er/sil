package it.eng.myportal.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.eng.myportal.entity.decodifiche.DeComune;

@Entity
@Table(name = "ag_app_anagrafica", schema = "myportal")
public class AgAppAnagrafica extends AbstractEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1090862754373201152L;

	private Integer idAgAppAnagrafica;
	private AgAppuntamento agAppuntamento;
	private String codiceFiscale;
	private String codMonoTipoSogg;
	private String nome;
	private String cognome;
	private String sesso;
	private Date dtNascita;
	private DeComune deComNascita;
	private String ragioneSociale;
	private DeComune deComune;
	private String indirizzo;
	private String cellulare;

	@Id
	@SequenceGenerator(name = "ag_app_anagrafica_id_ag_app_anagrafica_seq", sequenceName = "ag_app_anagrafica_id_ag_app_anagrafica_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ag_app_anagrafica_id_ag_app_anagrafica_seq")
	@Column(name = "id_ag_app_anagrafica", unique = true, nullable = false)
	public Integer getIdAgAppAnagrafica() {
		return idAgAppAnagrafica;
	}

	public void setIdAgAppAnagrafica(Integer idAgAppAnagrafica) {
		this.idAgAppAnagrafica = idAgAppAnagrafica;
	}

	@OneToOne
	@JoinColumn(name = "id_ag_appuntamento", nullable = false)
	public AgAppuntamento getAgAppuntamento() {
		return agAppuntamento;
	}

	public void setAgAppuntamento(AgAppuntamento agAppuntamento) {
		this.agAppuntamento = agAppuntamento;
	}

	@Column(name = "codice_fiscale", length = 16, nullable = false)
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	@Column(name = "cod_mono_tipo_sogg", length = 1, nullable = false)
	public String getCodMonoTipoSogg() {
		return codMonoTipoSogg;
	}

	public void setCodMonoTipoSogg(String codMonoTipoSogg) {
		this.codMonoTipoSogg = codMonoTipoSogg;
	}

	@Column(name = "nome", length = 50, nullable = false)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "cognome", length = 30, nullable = false)
	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	@Column(name = "sesso", length = 1)
	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_nascita")
	public Date getDtNascita() {
		return dtNascita;
	}

	public void setDtNascita(Date dtNascita) {
		this.dtNascita = dtNascita;
	}

	@ManyToOne
	@JoinColumn(name = "cod_com_nascita")
	public DeComune getDeComNascita() {
		return deComNascita;
	}

	public void setDeComNascita(DeComune deComNascita) {
		this.deComNascita = deComNascita;
	}

	@Column(name = "ragione_sociale", length = 100)
	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	@ManyToOne
	@JoinColumn(name = "cod_comune")
	public DeComune getDeComune() {
		return deComune;
	}

	public void setDeComune(DeComune deComune) {
		this.deComune = deComune;
	}

	@Column(name = "indirizzo", length = 100)
	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	@Column(name = "cellulare", length = 50)
	public String getCellulare() {
		return cellulare;
	}

	public void setCellulare(String cellulare) {
		this.cellulare = cellulare;
	}
}
