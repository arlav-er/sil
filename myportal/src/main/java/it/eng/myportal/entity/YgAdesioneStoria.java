package it.eng.myportal.entity;

import it.eng.myportal.entity.decodifiche.DeStatoAdesione;
import it.eng.myportal.entity.decodifiche.min.DeStatoAdesioneMin;

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
@Table(name = "yg_adesione_storia", schema = "myportal")
public class YgAdesioneStoria extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = -1041002698677462792L;

	private Integer idYgAdesioneStoria;
	private YgAdesione ygAdesione;
	private DeStatoAdesioneMin deStatoAdesioneMin;
	private DeStatoAdesione deStatoAdesione;
	private Date dtmStatoAdesione;
	private Date dtmInizio;
	private Date dtmFine;

	@Id
	@SequenceGenerator(name = "yg_adesione_storia_id_yg_adesione_storia_seq", sequenceName = "yg_adesione_storia_id_yg_adesione_storia_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "yg_adesione_storia_id_yg_adesione_storia_seq")
	@Column(name = "id_yg_adesione_storia", unique = true, nullable = false)
	public Integer getIdYgAdesioneStoria() {
		return idYgAdesioneStoria;
	}

	public void setIdYgAdesioneStoria(Integer idYgAdesioneStoria) {
		this.idYgAdesioneStoria = idYgAdesioneStoria;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_yg_adesione", nullable = false)
	public YgAdesione getYgAdesione() {
		return ygAdesione;
	}

	public void setYgAdesione(YgAdesione ygAdesione) {
		this.ygAdesione = ygAdesione;
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
	@JoinColumn(name = "cod_stato_adesione")
	public DeStatoAdesione getDeStatoAdesione() {
		return deStatoAdesione;
	}

	public void setDeStatoAdesione(DeStatoAdesione deStatoAdesione) {
		this.deStatoAdesione = deStatoAdesione;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dtm_stato_adesione", nullable = false, length = 13)
	public Date getDtmStatoAdesione() {
		return dtmStatoAdesione;
	}

	public void setDtmStatoAdesione(Date dtmStatoAdesione) {
		this.dtmStatoAdesione = dtmStatoAdesione;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dtm_inizio", nullable = false, length = 13)
	public Date getDtmInizio() {
		return dtmInizio;
	}

	public void setDtmInizio(Date dtmInizio) {
		this.dtmInizio = dtmInizio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dtm_fine", nullable = false, length = 13)
	public Date getDtmFine() {
		return dtmFine;
	}

	public void setDtmFine(Date dtmFine) {
		this.dtmFine = dtmFine;
	}
}
