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

import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.decodifiche.DeTipoAppuntamento;

@Entity
@Table(name = "ag_appuntamento", schema = "myportal")
public class AgAppuntamento extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = -7477631781296208850L;

	private Integer idAgAppuntamento;
	private PfPrincipal pfPrincipal;
	private Date dtAppuntamento;
	private String oraAppuntamento;
	private DeCpi deCpi;
	private String denominazioneCpi;
	private String indirizzoCpiStampa;
	private String siglaOperatore;
	private String ambiente;
	private DeTipoAppuntamento deTipoAppuntamento;
	private YgGaranziaOver idYgGaranziaOver;

	private YgAdesione ygAdesione;

	@Id
	@SequenceGenerator(name = "ag_appuntamento_id_ag_appuntamento_seq", sequenceName = "ag_appuntamento_id_ag_appuntamento_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ag_appuntamento_id_ag_appuntamento_seq")
	@Column(name = "id_ag_appuntamento", unique = true, nullable = false)
	public Integer getIdAgAppuntamento() {
		return this.idAgAppuntamento;
	}

	public void setIdAgAppuntamento(Integer idAgAppuntamento) {
		this.idAgAppuntamento = idAgAppuntamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return this.pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_yg_adesione", nullable = true)
	public YgAdesione getYgAdesione() {
		return this.ygAdesione;
	}

	// ade, regno della morte
	public void setYgAdesione(YgAdesione ade) {
		this.ygAdesione = ade;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_appuntamento", nullable = false)
	public Date getDtAppuntamento() {
		return this.dtAppuntamento;
	}

	public void setDtAppuntamento(Date dtAppuntamento) {
		this.dtAppuntamento = dtAppuntamento;
	}

	@Column(name = "ora_appuntamento", length = 5, nullable = false)
	public String getOraAppuntamento() {
		return oraAppuntamento;
	}

	public void setOraAppuntamento(String oraAppuntamento) {
		this.oraAppuntamento = oraAppuntamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_cpi", nullable = false)
	public DeCpi getDeCpi() {
		return deCpi;
	}

	public void setDeCpi(DeCpi deCpi) {
		this.deCpi = deCpi;
	}

	@Column(name = "denominazione_cpi", length = 100, nullable = true)
	public String getDenominazioneCpi() {
		return denominazioneCpi;
	}

	public void setDenominazioneCpi(String denominazioneCpi) {
		this.denominazioneCpi = denominazioneCpi;
	}

	@Column(name = "indirizzo_cpi_stampa", length = 600, nullable = true)
	public String getIndirizzoCpiStampa() {
		return indirizzoCpiStampa;
	}

	public void setIndirizzoCpiStampa(String indirizzoCpiStampa) {
		this.indirizzoCpiStampa = indirizzoCpiStampa;
	}

	@Column(name = "sigla_operatore", length = 80, nullable = true)
	public String getSiglaOperatore() {
		return siglaOperatore;
	}

	public void setSiglaOperatore(String siglaOperatore) {
		this.siglaOperatore = siglaOperatore;
	}

	@Column(name = "ambiente", length = 100, nullable = true)
	public String getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_tipo_appuntamento", nullable = false)
	public DeTipoAppuntamento getDeTipoAppuntamento() {
		return deTipoAppuntamento;
	}

	public void setDeTipoAppuntamento(DeTipoAppuntamento deTipoAppuntamento) {
		this.deTipoAppuntamento = deTipoAppuntamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_yg_garanzia_over", nullable = true)
	public YgGaranziaOver getIdYgGaranziaOver() {
		return idYgGaranziaOver;
	}

	public void setIdYgGaranziaOver(YgGaranziaOver idYgGaranziaOver) {
		this.idYgGaranziaOver = idYgGaranziaOver;
	}
}
