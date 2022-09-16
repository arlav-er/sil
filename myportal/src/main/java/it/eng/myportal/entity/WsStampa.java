package it.eng.myportal.entity;

import it.eng.myportal.entity.decodifiche.DeTipoStampa;

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

/**
 * WsStampa. Entity che contiene le varie stampe prodotte per i diversi utente
 * 
 * @author Rodi A.
 */
@Entity
@Table(name = "ws_stampa", schema = "myportal")
public class WsStampa extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 842723L;
	private Integer idWsStampa;
	private PfPrincipal pfPrincipal;
	private DeTipoStampa deTipoStampa;
	private byte[] contenuto;
	private Boolean flgDidRischioDisoccupazione;
	private Date dtDidLicenziamento;
	private Date dtDidLetteraLicenziamento;

	public WsStampa() {
	}

	@Id
	@SequenceGenerator(name = "ws_stampa_id_ws_stampa_seq", sequenceName = "ws_stampa_id_ws_stampa_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ws_stampa_id_ws_stampa_seq")
	@Column(name = "id_ws_stampa", unique = true, nullable = false)
	public Integer getIdWsStampa() {
		return this.idWsStampa;
	}

	public void setIdWsStampa(Integer idWsStampa) {
		this.idWsStampa = idWsStampa;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return this.pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipalByIdPfPrincipal) {
		this.pfPrincipal = pfPrincipalByIdPfPrincipal;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_tipo_stampa", nullable = false)
	public DeTipoStampa getDeTipoStampa() {
		return this.deTipoStampa;
	}

	public void setDeTipoStampa(DeTipoStampa deTipoStampa) {
		this.deTipoStampa = deTipoStampa;
	}

	@Column(name = "contenuto", nullable = false)
	public byte[] getContenuto() {
		return this.contenuto;
	}

	public void setContenuto(byte[] contenuto) {
		this.contenuto = contenuto;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_did_rischio_disoccupazione", nullable = true, length = 1)
	public Boolean getFlgDidRischioDisoccupazione() {
		return flgDidRischioDisoccupazione;
	}

	public void setFlgDidRischioDisoccupazione(Boolean flgDidRischioDisoccupazione) {
		this.flgDidRischioDisoccupazione = flgDidRischioDisoccupazione;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_did_licenziamento", nullable = true, length = 13)
	public Date getDtDidLicenziamento() {
		return dtDidLicenziamento;
	}

	public void setDtDidLicenziamento(Date dtDidLicenziamento) {
		this.dtDidLicenziamento = dtDidLicenziamento;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_did_lettera_licenziamento", nullable = true, length = 13)
	public Date getDtDidLetteraLicenziamento() {
		return dtDidLetteraLicenziamento;
	}

	public void setDtDidLetteraLicenziamento(Date dtDidLetteraLicenziamento) {
		this.dtDidLetteraLicenziamento = dtDidLetteraLicenziamento;
	}
}
