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
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import it.eng.myportal.entity.decodifiche.DeRegione;

@Entity
@Table(name = "yg_impostazioni", schema = "myportal")
@NamedQuery(name = "findYgImpostazioniByCodRegione", query = "select y from YgImpostazioni y where y.deRegione.codRegione = :cod_regione_portale")
public class YgImpostazioni implements java.io.Serializable {
	private static final long serialVersionUID = 257731L;
	private Integer id;
	private Boolean flgCreazioneAccount;
	private Boolean flgInvioSms;
	private Date dtInizioAdesione;
	private Date dtFineAdesione;
	private DeRegione deRegione;
	private Boolean flgAbilitazioneAmbiente;
	private Boolean flgAbilitazioneParData;
	private String codCheckYgStop;
	private String strMessErrCheckYg;
	private Boolean flgInvioCodRegione;

	public YgImpostazioni() {
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_inizio_adesione", nullable = true, length = 13)
	public Date getDtInizioAdesione() {
		return this.dtInizioAdesione;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_fine_adesione", nullable = true, length = 13)
	public Date getDtFineAdesione() {
		return this.dtFineAdesione;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_creazione_account", length = 1)
	public Boolean getFlgCreazioneAccount() {
		return this.flgCreazioneAccount;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_abilitazione_ambiente", length = 1)
	public Boolean getFlgAbilitazioneAmbiente() {
		return this.flgAbilitazioneAmbiente;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_invio_sms", length = 1)
	public Boolean getFlgInvioSms() {
		return this.flgInvioSms;
	}

	@Id
	@SequenceGenerator(name = "yg_impostazioni_id_seq", sequenceName = "yg_impostazioni_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "yg_impostazioni_id_seq")
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setFlgCreazioneAccount(Boolean flgCreazioneAccount) {
		this.flgCreazioneAccount = flgCreazioneAccount;
	}

	public void setFlgInvioSms(Boolean flgInvioSms) {
		this.flgInvioSms = flgInvioSms;
	}

	public void setFlgAbilitazioneAmbiente(Boolean flgAbilitazioneAmbiente) {
		this.flgAbilitazioneAmbiente = flgAbilitazioneAmbiente;
	}

	public void setDtInizioAdesione(Date dtInizioAdesione) {
		this.dtInizioAdesione = dtInizioAdesione;
	}

	public void setDtFineAdesione(Date dtFineAdesione) {
		this.dtFineAdesione = dtFineAdesione;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_regione")
	public DeRegione getDeRegione() {
		return this.deRegione;
	}

	public void setDeRegione(DeRegione deRegione) {
		this.deRegione = deRegione;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_abilitazione_par_data", length = 1)
	public Boolean getFlgAbilitazioneParData() {
		return flgAbilitazioneParData;
	}

	public void setFlgAbilitazioneParData(Boolean flgAbilitazioneParData) {
		this.flgAbilitazioneParData = flgAbilitazioneParData;
	}

	@Column(name = "cod_checkYG_stop", nullable = true, length = 2)
	public String getCodCheckYgStop() {
		return codCheckYgStop;
	}

	public void setCodCheckYgStop(String codCheckYgStop) {
		this.codCheckYgStop = codCheckYgStop;
	}

	@Column(name = "str_mess_err_checkYG", nullable = true, length = 200)
	public String getStrMessErrCheckYg() {
		return strMessErrCheckYg;
	}

	public void setStrMessErrCheckYg(String strMessErrCheckYg) {
		this.strMessErrCheckYg = strMessErrCheckYg;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_invio_codRegione", length = 1)
	public Boolean getFlgInvioCodRegione() {
		return flgInvioCodRegione;
	}

	public void setFlgInvioCodRegione(Boolean flgInvioCodRegione) {
		this.flgInvioCodRegione = flgInvioCodRegione;
	}
}
