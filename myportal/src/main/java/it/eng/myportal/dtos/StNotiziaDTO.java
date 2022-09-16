package it.eng.myportal.dtos;

import java.util.Date;

/**
 * Classe per visualizzare le Notizie in HomePage
 * 
 * @author Rodi A.
 *
 */
public class StNotiziaDTO extends AbstractUpdatablePkDTO implements Comparable<StNotiziaDTO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -359826010329220764L;
	private Date dtmPubblicazione;
	private Date dtmScadenza;
	private String codTipoMessaggio;
	private String oggetto;
	private String contenuto;
	private Integer ordine;

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

	public Integer getOrdine() {
		return ordine;
	}

	public void setOrdine(Integer ordine) {
		this.ordine = ordine;
	}

	/**
	 * Ordina, nell'ordine, Data Inserimento, Data Pubblicazione del Record
	 */
	@Override
	public int compareTo(StNotiziaDTO o) {
		if (o == null) {
			return 1;
		}
		int onDtInserimento = this.getDtmIns().compareTo(o.getDtmIns());
		if (onDtInserimento == 0) {
			return this.getDtmPubblicazione().compareTo(o.getDtmPubblicazione()) > 0 ? -1 : 1;
		} else {
			return onDtInserimento > 0 ? -1 : 1;
		}
	}

}
