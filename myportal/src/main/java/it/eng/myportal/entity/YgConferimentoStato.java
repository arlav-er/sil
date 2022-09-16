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

import org.hibernate.annotations.Type;

import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.min.DeStatoAdesioneMin;

@Entity
@Table(name = "yg_conferimento_stato", schema = "myportal")
public class YgConferimentoStato extends AbstractEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 598311438438020426L;
	private Integer idYgConferimentoStato;
	private String codiceFiscale;
	private DeProvincia deProvincia;
	private DeStatoAdesioneMin deStatoAdesioneMin;
	private YgAdesione ygAdesione;
	private Boolean flgProcessato;
	private Date dtmInvio;
	private String codEsito;
	private String strMessErrore;

	@Id
	@SequenceGenerator(name = "yg_conferimento_stato_id_yg_conferimento_stato_seq", sequenceName = "yg_conferimento_stato_id_yg_conferimento_stato_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "yg_conferimento_stato_id_yg_conferimento_stato_seq")
	@Column(name = "id_yg_conferimento_stato", unique = true, nullable = false)
	public Integer getIdYgConferimentoStato() {
		return idYgConferimentoStato;
	}

	public void setIdYgConferimentoStato(Integer idYgConferimentoStato) {
		this.idYgConferimentoStato = idYgConferimentoStato;
	}

	@Column(name = "codice_fiscale", length = 16, nullable = false)
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_provincia", nullable = true)
	public DeProvincia getDeProvincia() {
		return deProvincia;
	}

	public void setDeProvincia(DeProvincia deProvincia) {
		this.deProvincia = deProvincia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_stato_adesione_min", nullable = false)
	public DeStatoAdesioneMin getDeStatoAdesioneMin() {
		return deStatoAdesioneMin;
	}

	public void setDeStatoAdesioneMin(DeStatoAdesioneMin deStatoAdesioneMin) {
		this.deStatoAdesioneMin = deStatoAdesioneMin;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_yg_adesione", nullable = true)
	public YgAdesione getYgAdesione() {
		return ygAdesione;
	}

	public void setYgAdesione(YgAdesione ygAdesione) {
		this.ygAdesione = ygAdesione;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_processato", length = 1, nullable = true)
	public Boolean getFlgProcessato() {
		return flgProcessato;
	}

	public void setFlgProcessato(Boolean flgProcessato) {
		this.flgProcessato = flgProcessato;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dtm_invio", length = 13, nullable = true)
	public Date getDtmInvio() {
		return dtmInvio;
	}

	public void setDtmInvio(Date dtmInvio) {
		this.dtmInvio = dtmInvio;
	}

	@Column(name = "cod_esito", length = 2, nullable = true)
	public String getCodEsito() {
		return codEsito;
	}

	public void setCodEsito(String codEsito) {
		this.codEsito = codEsito;
	}

	@Column(name = "str_mess_errore", length = 2000, nullable = true)
	public String getStrMessErrore() {
		return strMessErrore;
	}

	public void setStrMessErrore(String strMessErrore) {
		this.strMessErrore = strMessErrore;
	}

}
