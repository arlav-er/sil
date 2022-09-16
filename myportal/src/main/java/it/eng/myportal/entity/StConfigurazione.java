package it.eng.myportal.entity;

// Generated Apr 20, 2012 10:40:45 AM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Rodi A.
 */
@Entity
@Table(name = "st_configurazione", schema = "myportal")
@Deprecated//USARE TS_OPZIONI
public class StConfigurazione extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 257731L;
	private Date dtAggiornamentoMonit;
	private Boolean flagAggiornamento;
	private Integer idConfigurazione;
	private String versione;
	private Date dtAvvioYg;
	private Date dtBatchYg;
	private Date dtBatchConfMassivoYg;
	private Boolean flgRegUtenteAbilitatoYg;
	private Date dtAvvioClic;
	private Date dtFineConfermaDid;
	private Date dtFineDisabilDidApp;
 
	@Id
	@SequenceGenerator(name = "st_configurazione_id_configurazione_seq", sequenceName = "st_configurazione_id_configurazione_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "st_configurazione_id_configurazione_seq")
	@Column(name = "id_configurazione", unique = true, nullable = false)
	public Integer getIdConfigurazione() {
		return this.idConfigurazione;
	}
	
	public void setIdConfigurazione(Integer idConfigurazione) {
		this.idConfigurazione = idConfigurazione;
	}

	/*@Temporal(TemporalType.DATE)
	@Column(name = "dt_aggiornamento_monit", nullable = true, length = 13)
	public Date getDtAggiornamentoMonit() {
		return this.dtAggiornamentoMonit;
	}
	
	public void setDtAggiornamentoMonit(Date dtAggiornamentoMonit) {
		this.dtAggiornamentoMonit = dtAggiornamentoMonit;
	}*/

	/*@Type(type = "yes_no")
	@Column(name = "flag_aggiornamento", length = 1)
	public Boolean getFlagAggiornamento() {
		return this.flagAggiornamento;
	}
	public void setFlagAggiornamento(Boolean flagAggiornamento) {
		this.flagAggiornamento = flagAggiornamento;
	}*/

	/*@Column(name = "versione", length = 50)
	public String getVersione() {
		return this.versione;
	}

	public void setVersione(String versione) {
		this.versione = versione;
	}*/

	/*@Temporal(TemporalType.DATE)
	@Column(name = "dt_avvio_yg", nullable = true, length = 13)
	public Date getDtAvvioYg() {
		return dtAvvioYg;
	}

	public void setDtAvvioYg(Date dtAvvioYg) {
		this.dtAvvioYg = dtAvvioYg;
	}*/

	/*@Temporal(TemporalType.DATE)
	@Column(name = "dt_batch_yg", nullable = true, length = 13)
	public Date getDtBatchYg() {
		return dtBatchYg;
	}

	public void setDtBatchYg(Date dtBatchYg) {
		this.dtBatchYg = dtBatchYg;
	}*/

	/*@Type(type = "yes_no")
	@Column(name = "flg_reg_utente_abilitato_yg", length = 1)
	public Boolean getFlgRegUtenteAbilitatoYg() {
		return flgRegUtenteAbilitatoYg;
	}

	public void setFlgRegUtenteAbilitatoYg(Boolean flgRegUtenteAbilitatoYg) {
		this.flgRegUtenteAbilitatoYg = flgRegUtenteAbilitatoYg;
	}*/

	/*@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_batch_conf_massivo_yg", nullable = false, length = 29)
	public Date getDtBatchConfMassivoYg() {
		return dtBatchConfMassivoYg;
	}

	public void setDtBatchConfMassivoYg(Date dtBatchConfMassivoYg) {
		this.dtBatchConfMassivoYg = dtBatchConfMassivoYg;
	}*/

	/*@Temporal(TemporalType.DATE)
	@Column(name = "dt_avvio_clic", nullable = true, length = 13)
	public Date getDtAvvioClic() {
		return dtAvvioClic;
	}

	public void setDtAvvioClic(Date dtAvvioClic) {
		this.dtAvvioClic = dtAvvioClic;
	}*/

	/*@Temporal(TemporalType.DATE)
	@Column(name = "dt_fine_conferma_did", nullable = true, length = 13)
	public Date getDtFineConfermaDid() {
		return dtFineConfermaDid;
	}

	public void setDtFineConfermaDid(Date dtFineConfermaDid) {
		this.dtFineConfermaDid = dtFineConfermaDid;
	}*/

	/*@Temporal(TemporalType.DATE)
	@Column(name = "dt_fine_disabil_did_app", nullable = true, length = 13)
	public Date getdtFineDisabilDidApp() {
		return dtFineDisabilDidApp;
	}

	public void setDtFineDisabilDidApp(Date dtFineDisabilDidApp) {
		this.dtFineDisabilDidApp = dtFineDisabilDidApp;
	}*/
}
