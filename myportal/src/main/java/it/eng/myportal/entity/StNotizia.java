package it.eng.myportal.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author Rodi A.
 *
 */
@Entity
@Table(name = "st_notizia", schema = "mycas")
public class StNotizia extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1565237583454419281L;
	private Integer idStNotizia;
	private Date dtmPubblicazione;
	private Date dtmScadenza;
	private String codTipoMessaggio;
	private String oggetto;
	private String contenuto;
	private Integer ordine;

	public StNotizia() {
	}

	@Id
	@SequenceGenerator(name = "mycas.st_notizia_id_st_notizia_seq", sequenceName = "mycas.st_notizia_id_st_notizia_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mycas.st_notizia_id_st_notizia_seq")
	@Column(name = "id_st_notizia", unique = true, nullable = false)
	public Integer getIdStNotizia() {
		return this.idStNotizia;
	}

	public void setIdStNotizia(Integer idNotizia) {
		this.idStNotizia = idNotizia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dtm_pubblicazione", nullable = false, length = 29)
	public Date getDtmPubblicazione() {
		return this.dtmPubblicazione;
	}

	public void setDtmPubblicazione(Date dtmPubblicazione) {
		this.dtmPubblicazione = dtmPubblicazione;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dtm_scadenza", nullable = false, length = 29)
	public Date getDtmScadenza() {
		return this.dtmScadenza;
	}

	public void setDtmScadenza(Date dtmScadenza) {
		this.dtmScadenza = dtmScadenza;
	}

	@Column(name = "cod_tipo_messaggio", nullable = false, length = 8)
	public String getCodTipoMessaggio() {
		return this.codTipoMessaggio;
	}

	public void setCodTipoMessaggio(String codTipoMessaggio) {
		this.codTipoMessaggio = codTipoMessaggio;
	}

	@Column(name = "oggetto", nullable = false)
	public String getOggetto() {
		return this.oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	@Column(name = "contenuto", nullable = false, length = 18000)
	public String getContenuto() {
		return this.contenuto;
	}

	public void setContenuto(String contenuto) {
		this.contenuto = contenuto;
	}

	@Column(name = "ordine")
	public Integer getOrdine() {
		return ordine;
	}

	public void setOrdine(Integer ordine) {
		this.ordine = ordine;
	}

}
