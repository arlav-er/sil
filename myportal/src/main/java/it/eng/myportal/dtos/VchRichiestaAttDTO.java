package it.eng.myportal.dtos;

import java.util.Date;

public class VchRichiestaAttDTO extends AbstractUpdatablePkDTO{

	private static final long serialVersionUID = 1L;
	private Date dtRichiesta;
	private String codiceFiscaleCitt;
	private VchEntiAccreditatiDTO vchEntiAccreditati;
	private Integer codAttivazione;
	private DeWsVchEsitoDTO deWsVchEsito;
	private String erroreNonCodificato;
	
	//Getter and setter
	public Date getDtRichiesta() {
		return dtRichiesta;
	}
	public void setDtRichiesta(Date dtRichiesta) {
		this.dtRichiesta = dtRichiesta;
	}
	public String getCodiceFiscaleCitt() {
		return codiceFiscaleCitt;
	}
	public void setCodiceFiscaleCitt(String codiceFiscaleCitt) {
		this.codiceFiscaleCitt = codiceFiscaleCitt;
	}
	public Integer getCodAttivazione() {
		return codAttivazione;
	}
	public VchEntiAccreditatiDTO getVchEntiAccreditati() {
		return vchEntiAccreditati;
	}
	public void setVchEntiAccreditati(VchEntiAccreditatiDTO vchEntiAccreditati) {
		this.vchEntiAccreditati = vchEntiAccreditati;
	}
	public void setCodAttivazione(Integer codAttivazione) {
		this.codAttivazione = codAttivazione;
	}
	public DeWsVchEsitoDTO getDeWsVchEsito() {
		return deWsVchEsito;
	}
	public void setDeWsVchEsito(DeWsVchEsitoDTO dewsVchEsito) {
		this.deWsVchEsito = dewsVchEsito;
	}
	public String getErroreNonCodificato() {
		return erroreNonCodificato;
	}
	public void setErroreNonCodificato(String erroreNonCodificato) {
		this.erroreNonCodificato = erroreNonCodificato;
	}
	
}
