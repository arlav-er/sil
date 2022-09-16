package it.eng.myportal.dtos;

import it.eng.myportal.enums.AzioneServizio;

public class ClInvioComunicazioneDTO extends AbstractUpdatablePkDTO{
	
	
	private static final long serialVersionUID = 2687938453339504129L;
	
	private String codComunicazione;
	private String fileComunicazione;
	private DeStatoInvioClDTO stato;
	private AzioneServizio azioneServizio;
	private String descrizioneErrore;
	private Boolean flagInviato;
	private String mittente;
	private String destinatario;
	
	
	public String getCodComunicazione() {
		return codComunicazione;
	}
	public void setCodComunicazione(String codComunicazione) {
		this.codComunicazione = codComunicazione;
	}
	public String getFileComunicazione() {
		return fileComunicazione;
	}
	public void setFileComunicazione(String fileComunicazione) {
		this.fileComunicazione = fileComunicazione;
	}
	public DeStatoInvioClDTO getStato() {
		return stato;
	}
	public void setStato(DeStatoInvioClDTO stato) {
		this.stato = stato;
	}
	public AzioneServizio getAzioneServizio() {
		return azioneServizio;
	}
	public void setAzioneServizio(AzioneServizio azioneServizio) {
		this.azioneServizio = azioneServizio;
	}
	public String getDescrizioneErrore() {
		return descrizioneErrore;
	}
	public void setDescrizioneErrore(String descrizioneErrore) {
		this.descrizioneErrore = descrizioneErrore;
	}
	public Boolean getFlagInviato() {
		return flagInviato;
	}
	public void setFlagInviato(Boolean flagInviato) {
		this.flagInviato = flagInviato;
	}
	public String getMittente() {
		return mittente;
	}
	public String getDestinatario() {
		return destinatario;
	}
	public void setMittente(String mittente) {
		this.mittente = mittente;
	}
	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}
	
	
	
}
