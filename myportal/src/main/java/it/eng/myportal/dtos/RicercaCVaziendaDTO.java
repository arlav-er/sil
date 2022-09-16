package it.eng.myportal.dtos;

import java.util.Date;

/**
 * Classe contenitore delle informazioni su un CV in sessione.<br/>
 * Inserire in questo DTO <b>solo le informazioni che devono essere mantenute in sessione</b>
 * 
 * @author turro
 * @see AbstractUpdatableDTO
 * 
 */

public class RicercaCVaziendaDTO extends AbstractUpdatablePkDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2698965085713786797L;
	
	
	private String descrizione;
	private String comuneDomicilio;
	private Date dataModifica;
	private String attivitaPrincipale;
	/**
	 * Messaggio di contatto inviato dall'utente all'azienda 
	 */
	private Integer idMsgMessaggio;
	private Integer idVaDatiVacancy;
	private Date dataMessaggioAz;
	
	/**
	 * Messaggio di contatto inviato dall'azienda all'utente
	 */
	private Integer idMsgMessaggioAz;
	
	
	
	
	public RicercaCVaziendaDTO() {
		super();
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public RicercaCVaziendaDTO(Integer idPfPrincipal,String descrizione, String comuneDomicilio, Date dataModifica, Integer idVaDatiVacancy, String attivitaPrincipale, Integer idMsgMessaggio,Date dataMessaggioAz, Integer idMsgMessaggioAz) {
		super();
		this.id = idPfPrincipal;
		this.descrizione = descrizione;
		this.comuneDomicilio = comuneDomicilio;
		this.dataModifica = dataModifica;
		this.idVaDatiVacancy = idVaDatiVacancy;
		this.attivitaPrincipale = attivitaPrincipale;
		this.idMsgMessaggio = idMsgMessaggio;
		this.dataMessaggioAz = dataMessaggioAz;
		this.idMsgMessaggioAz = idMsgMessaggioAz;
	}

	public String getComuneDomicilio() {
		return comuneDomicilio;
	}

	public void setComuneDomicilio(String comuneDomicilio) {
		this.comuneDomicilio = comuneDomicilio;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	public String getAttivitaPrincipale() {
		return attivitaPrincipale;
	}

	public void setAttivitaPrincipale(String attivitaPrincipale) {
		this.attivitaPrincipale = attivitaPrincipale;
	}

	public Integer getIdMsgMessaggio() {
		return idMsgMessaggio;
	}

	public void setIdMsgMessaggio(Integer idMsgMessaggio) {
		this.idMsgMessaggio = idMsgMessaggio;
	}

	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public Date getDataMessaggioAz() {
		return dataMessaggioAz;
	}

	public void setDataMessaggioAz(Date dataMessaggioAz) {
		this.dataMessaggioAz = dataMessaggioAz;
	}

	public Integer getIdMsgMessaggioAz() {
		return idMsgMessaggioAz;
	}

	public void setIdMsgMessaggioAz(Integer idMsgMessaggioAz) {
		this.idMsgMessaggioAz = idMsgMessaggioAz;
	}

	
	
}
