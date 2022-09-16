package it.eng.myportal.dtos;

import java.util.Date;

import it.eng.myportal.entity.TipoAccettazioneEnum;
import it.eng.myportal.entity.enums.StatoAccettazionePattoEnum;

/**
 * @author Antimo V.
 */
public class PattoSilStoricoDTO extends AbstractUpdatablePkDTO implements IDTO {
	private static final long serialVersionUID = 257731L;

	private Integer id;
	private Integer idPatto;
	private Date tsInvio;
	private Date tsAccettazione;
	private TipoAccettazioneEnum tipoAccettazione;
	private StatoAccettazionePattoEnum codStatoAccettazione;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdPatto() {
		return idPatto;
	}

	public void setIdPatto(Integer idPatto) {
		this.idPatto = idPatto;
	}

	public Date getTsInvio() {
		return tsInvio;
	}

	public void setTsInvio(Date tsInvio) {
		this.tsInvio = tsInvio;
	}

	public Date getTsAccettazione() {
		return tsAccettazione;
	}

	public void setTsAccettazione(Date tsAccettazione) {
		this.tsAccettazione = tsAccettazione;
	}

	public TipoAccettazioneEnum getTipoAccettazione() {
		return tipoAccettazione;
	}

	public void setTipoAccettazione(TipoAccettazioneEnum tipoAccettazione) {
		this.tipoAccettazione = tipoAccettazione;
	}

	public StatoAccettazionePattoEnum getCodStatoAccettazione() {
		return codStatoAccettazione;
	}

	public void setCodStatoAccettazione(StatoAccettazionePattoEnum codStatoAccettazione) {
		this.codStatoAccettazione = codStatoAccettazione;
	}

}