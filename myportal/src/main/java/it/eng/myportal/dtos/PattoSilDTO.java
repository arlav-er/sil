package it.eng.myportal.dtos;

import java.util.Date;

import it.eng.myportal.entity.TipoAccettazioneEnum;
import it.eng.myportal.entity.enums.StatoAccettazionePattoEnum;

/**
 * @author Antimo V.
 */
public class PattoSilDTO extends AbstractUpdatablePkDTO implements IDTO {
	private static final long serialVersionUID = 257731L;

	private Integer id;
	private PfPrincipalDTO pfPrincipalDTO;
	private String codFis;
	private DeProvinciaDTO deProvinciaDTO;
	private Date tsInvio;
	private Date tsAccettazione;
	private TipoAccettazioneEnum tipoAccettazione;
	
	private byte[] pdfPatto;
	
	private Date dtPatto;
	private Boolean flgPresaVisione;
	private StatoAccettazionePattoEnum codStatoAccettazione;

	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public PfPrincipalDTO getPfPrincipalDTO() {
		return this.pfPrincipalDTO;
	}

	public void setPfPrincipalDTO(PfPrincipalDTO pfPrincipalDTO) {
		this.pfPrincipalDTO = pfPrincipalDTO;
	}

	public String getCodFis() {
		return codFis;
	}

	public void setCodFis(String codFis) {
		this.codFis = codFis;
	}

	public DeProvinciaDTO getDeProvinciaDTO() {
		return this.deProvinciaDTO;
	}

	public void setDeProvinciaDTO(DeProvinciaDTO deProvincia) {
		this.deProvinciaDTO = deProvincia;
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

	public byte[] getPdfPatto() {
		return pdfPatto;
	}

	public void setPdfPatto(byte[] pdfPatto) {
		this.pdfPatto = pdfPatto;
	}

	public Date getDtPatto() {
		return dtPatto;
	}

	public void setDtPatto(Date dtPatto) {
		this.dtPatto = dtPatto;
	}

	public Boolean getFlgPresaVisione() {
		return flgPresaVisione;
	}

	public void setFlgPresaVisione(Boolean flgPresaVisione) {
		this.flgPresaVisione = flgPresaVisione;
	}

	public StatoAccettazionePattoEnum getCodStatoAccettazione() {
		return codStatoAccettazione;
	}

	public void setCodStatoAccettazione(StatoAccettazionePattoEnum codStatoAccettazione) {
		this.codStatoAccettazione = codStatoAccettazione;
	}

}