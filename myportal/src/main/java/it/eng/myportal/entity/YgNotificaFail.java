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
@Table(name = "yg_notifica_fail", schema = "myportal")
public class YgNotificaFail extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = -6677104901110242237L;

	private Integer idYgNotificaFail;
	private String codiceFiscale;
	private Date dtAdesione;
	private String codRegioneAdesioneMin;
	private String codStatoAdesioneMin;
	private Date dtmStatoAdesioneMin;
	private String codErrore;
	private String strMessErrore;
	private YgAdesione ygAdesione;
	private String codMonoTipoNotifica;

	@Id
	@SequenceGenerator(name = "yg_notifica_fail_id_yg_notifica_fail_seq", sequenceName = "yg_notifica_fail_id_yg_notifica_fail_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "yg_notifica_fail_id_yg_notifica_fail_seq")
	@Column(name = "id_yg_notifica_fail", unique = true, nullable = false)
	public Integer getIdYgNotificaFail() {
		return idYgNotificaFail;
	}

	public void setIdYgNotificaFail(Integer idYgNotificaFail) {
		this.idYgNotificaFail = idYgNotificaFail;
	}

	@Column(name = "codice_fiscale", length = 16, nullable = true)
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_adesione", length = 13, nullable = true)
	public Date getDtAdesione() {
		return dtAdesione;
	}

	public void setDtAdesione(Date dtAdesione) {
		this.dtAdesione = dtAdesione;
	}

	@Column(name = "cod_regione_adesione_min", length = 8, nullable = true)
	public String getCodRegioneAdesioneMin() {
		return codRegioneAdesioneMin;
	}

	public void setCodRegioneAdesioneMin(String codRegioneAdesioneMin) {
		this.codRegioneAdesioneMin = codRegioneAdesioneMin;
	}

	@Column(name = "cod_stato_adesione_min", length = 8, nullable = true)
	public String getCodStatoAdesioneMin() {
		return codStatoAdesioneMin;
	}

	public void setCodStatoAdesioneMin(String codStatoAdesioneMin) {
		this.codStatoAdesioneMin = codStatoAdesioneMin;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dtm_stato_adesione_min", length = 13, nullable = true)
	public Date getDtmStatoAdesioneMin() {
		return dtmStatoAdesioneMin;
	}

	public void setDtmStatoAdesioneMin(Date dtmStatoAdesioneMin) {
		this.dtmStatoAdesioneMin = dtmStatoAdesioneMin;
	}

	@Column(name = "cod_errore", length = 8, nullable = false)
	public String getCodErrore() {
		return codErrore;
	}

	public void setCodErrore(String codErrore) {
		this.codErrore = codErrore;
	}

	@Column(name = "str_mess_errore", length = 2000, nullable = true)
	public String getStrMessErrore() {
		return strMessErrore;
	}

	public void setStrMessErrore(String strMessErrore) {
		this.strMessErrore = strMessErrore;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_yg_adesione", nullable = true)
	public YgAdesione getYgAdesione() {
		return ygAdesione;
	}

	public void setYgAdesione(YgAdesione ygAdesione) {
		this.ygAdesione = ygAdesione;
	}

	@Column(name = "cod_mono_tipo_notifica", length = 1, nullable = false)
	public String getCodMonoTipoNotifica() {
		return codMonoTipoNotifica;
	}

	public void setCodMonoTipoNotifica(String codMonoTipoNotifica) {
		this.codMonoTipoNotifica = codMonoTipoNotifica;
	}
}
