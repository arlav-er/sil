package it.eng.myportal.dtos;


import java.util.Date;

/**
 * Classe per visualizzare le Notizie in HomePage
 * @author Rodi A.
 * @deprecated utilizzare StNotiziaDTO
 */
public class NotiziaDTO extends AbstractUpdatablePkDTO  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -359826010329220764L;
	private Date dtmPubblicazione;
	private Date dtmScadenza;
	private String codTipoMessaggio;
	private String oggetto;
	private String contenuto;
	public Date getDtmPubblicazione() {
		return dtmPubblicazione;
	}
	public void setDtmPubblicazione(Date dtmPubblicazione) {
		this.dtmPubblicazione = dtmPubblicazione;
	}
	public Date getDtmScadenza() {
		return dtmScadenza;
	}
	public void setDtmScadenza(Date dtmScadenza) {
		this.dtmScadenza = dtmScadenza;
	}
	public String getCodTipoMessaggio() {
		return codTipoMessaggio;
	}
	public void setCodTipoMessaggio(String codTipoMessaggio) {
		this.codTipoMessaggio = codTipoMessaggio;
	}
	public String getOggetto() {
		return oggetto;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	public String getContenuto() {
		return contenuto;
	}
	public void setContenuto(String contenuto) {
		this.contenuto = contenuto;
	}
	

}
