package it.eng.myportal.entity;

import java.util.Calendar;
import java.util.Date;

// Generated Apr 20, 2012 10:40:45 AM by Hibernate Tools 3.4.0.CR1
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.enums.StatoAccettazionePattoEnum;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * @author Antimo V.
 */
@Entity
@Table(name = "patto_sil", schema = "myportal")
@NamedQueries({ 
	@NamedQuery(name = "findPattoSilByIdPrincipal", query = "SELECT pf FROM PattoSil pf WHERE pf.pfPrincipal.idPfPrincipal = :idPfPrincipal")})
public class PattoSil extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 257731L;

	private Integer idPattoSil;
	private PfPrincipal pfPrincipal;
	private String codFis;
	private DeProvincia deProvincia;
	private Date tsInvio;
	private Date tsAccettazione;
	private Date dtPatto;
	private Boolean flgPresaVisione;
	private StatoAccettazionePattoEnum codStatoAccettazione;
	private TipoAccettazioneEnum tipoAccettazione;
	private byte[] pdfPatto;
	private String numProtocollo;
	private Integer numAnnoProtocollo;

	@Id
	@SequenceGenerator(name = "patto_sil_id_patto_sil_seq", sequenceName = "patto_sil_id_patto_sil_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patto_sil_id_patto_sil_seq")
	@Column(name = "id_patto_sil", unique = true, nullable = false)
	public Integer getIdPattoSil() {
		return this.idPattoSil;
	}

	public void setIdPattoSil(Integer idPattoSil) {
		this.idPattoSil = idPattoSil;
	}
	
	/**
	 * Configurare qui scadenza max OTP patto
	 * @return
	 */
	@Transient
	public Date getDataMaxAccettazione() {
		Calendar maxAccettazione = Calendar.getInstance();
		maxAccettazione.setTime(getTsInvio());
		maxAccettazione.add(Calendar.HOUR, ConstantsSingleton.getNumOreMaxValidazionePatto());
		return maxAccettazione.getTime();
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return this.pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	@Column(name = "cod_fis", length = 16)
	public String getCodFis() {
		return codFis;
	}

	public void setCodFis(String codFis) {
		this.codFis = codFis;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_provincia", nullable = false)
	public DeProvincia getDeProvincia() {
		return this.deProvincia;
	}

	public void setDeProvincia(DeProvincia deProvincia) {
		this.deProvincia = deProvincia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ts_invio", nullable = false, length = 29)
	public Date getTsInvio() {
		return tsInvio;
	}

	public void setTsInvio(Date tsInvio) {
		this.tsInvio = tsInvio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ts_accettazione", length = 29)
	public Date getTsAccettazione() {
		return tsAccettazione;
	}

	public void setTsAccettazione(Date tsAccettazione) {
		this.tsAccettazione = tsAccettazione;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_accettazione", length = 4)
	public TipoAccettazioneEnum getTipoAccettazione() {
		return tipoAccettazione;
	}

	public void setTipoAccettazione(TipoAccettazioneEnum tipoAccettazione) {
		this.tipoAccettazione = tipoAccettazione;
	}

	@Column(name = "pdf_patto")
	public byte[] getPdfPatto() {
		return pdfPatto;
	}

	public void setPdfPatto(byte[] pdfPatto) {
		this.pdfPatto = pdfPatto;
	}

	@Column(name = "num_protocollo", length = 50)
	public String getNumProtocollo() {
		return numProtocollo;
	}

	public void setNumProtocollo(String numProtocollo) {
		this.numProtocollo = numProtocollo;
	}

	@Column(name = "num_anno_protocollo")
	public Integer getNumAnnoProtocollo() {
		return numAnnoProtocollo;
	}

	public void setNumAnnoProtocollo(Integer numAnnoProtocollo) {
		this.numAnnoProtocollo = numAnnoProtocollo;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_patto", nullable = false)
	public Date getDtPatto() {
		return dtPatto;
	}

	public void setDtPatto(Date dtPatto) {
		this.dtPatto = dtPatto;
	}

	@Column(name = "flg_presa_visione")
	public Boolean getFlgPresaVisione() {
		return flgPresaVisione;
	}

	public void setFlgPresaVisione(Boolean flgPresaVisione) {
		this.flgPresaVisione = flgPresaVisione;
	}
	@Enumerated(EnumType.STRING)
	@Column(name = "cod_stato_accettazione", length = 1)
	public StatoAccettazionePattoEnum getCodStatoAccettazione() {
		return codStatoAccettazione;
	}

	public void setCodStatoAccettazione(StatoAccettazionePattoEnum codStatoAccettazione) {
		this.codStatoAccettazione = codStatoAccettazione;
	}

}