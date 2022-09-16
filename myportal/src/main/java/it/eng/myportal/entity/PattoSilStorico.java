package it.eng.myportal.entity;

import java.util.Date;

// Generated Apr 20, 2012 10:40:45 AM by Hibernate Tools 3.4.0.CR1
import javax.persistence.*;

import it.eng.myportal.entity.enums.StatoAccettazionePattoEnum;

/**
 * @author Antimo V.
 */
@Entity
@Table(name = "patto_sil_storico", schema = "myportal")
@NamedQueries({
		@NamedQuery(name = "findPattoSilStoricoById", query = "SELECT pf FROM PattoSilStorico pf WHERE pf.idPattoSil = :idPattoSil")})
public class PattoSilStorico extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 257731L;

	private Integer idPattoSilStorico;
	private Integer idPattoSil;
	private Date tsInvio;
	private Date tsAccettazione;
	private StatoAccettazionePattoEnum codStatoAccettazione;
	private TipoAccettazioneEnum tipoAccettazione;

	@Id
	@SequenceGenerator(name = "patto_sil_storico_id_patto_sil_storico_seq", sequenceName = "patto_sil_storico_id_patto_sil_storico_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patto_sil_storico_id_patto_sil_storico_seq")
	@Column(name = "id_patto_sil_storico", unique = true, nullable = false)
	public Integer getIdPattoSilStorico() {
		return this.idPattoSilStorico;
	}

	public void setIdPattoSilStorico(Integer idPattoSilStorico) {
		this.idPattoSilStorico = idPattoSilStorico;
	}
	
	@Column(name = "id_patto_sil", nullable = false)
	public Integer getIdPattoSil() {
		return this.idPattoSil;
	}

	public void setIdPattoSil(Integer idPattoSil) {
		this.idPattoSil = idPattoSil;
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

	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "cod_stato_accettazione", length = 1)
	public StatoAccettazionePattoEnum getCodStatoAccettazione() {
		return codStatoAccettazione;
	}

	public void setCodStatoAccettazione(StatoAccettazionePattoEnum codStatoAccettazione) {
		this.codStatoAccettazione = codStatoAccettazione;
	}

}