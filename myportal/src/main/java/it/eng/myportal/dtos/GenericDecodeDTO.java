package it.eng.myportal.dtos;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe astratta per DTO di decodifica
 * 
 * @see AbstractHasPrimaryKeyDTO
 * @see IDecode
 * 
 * @author Rodi A.
 * 
 * 
 */
public class GenericDecodeDTO implements IDecode, Serializable {

	private static final long serialVersionUID = -1001689160388578459L;
	protected String id;
	protected String descrizione;

	protected Date dtInizioVal;
	protected Date dtFineVal;
	
	protected Boolean flgIdo;//utile per la gestione dei messaggi di supporto se l'utente in sessione è la provincia

	
	public GenericDecodeDTO() {
		super();
	}

	public GenericDecodeDTO(String id) {
		super();
		this.id = id;
	}

	public GenericDecodeDTO(String codice, String descrizione) {
		this.id = codice;
		this.descrizione = descrizione;
	}
	
	public GenericDecodeDTO(String codice, String descrizione,Boolean flgIdo) {
		this.id = codice;
		this.descrizione = descrizione;
		this.flgIdo = flgIdo;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public Date getDtInizioVal() {
		return dtInizioVal;
	}

	public void setDtInizioVal(Date dtInizioVal) {
		this.dtInizioVal = dtInizioVal;
	}

	public Date getDtFineVal() {
		return dtFineVal;
	}

	public void setDtFineVal(Date dtFineVal) {
		this.dtFineVal = dtFineVal;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (this == o)
			return true;
		if (!o.getClass().isAssignableFrom(this.getClass())) {
			return false;
		}

		String otherId = ((GenericDecodeDTO) o).getId();
		if (otherId != null && this.getId() != null)
			return otherId.equals(this.getId());
		else
			return false;
	}

	@Override
	public int hashCode() {
		if (this.getId() != null)
			return this.getId().hashCode();
		else
			return 0;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Attenzione! Questo metodo viene utilizzato da selectItems di JSF e non può essere modificato! A meno che qualcuno
	 * non trovi il modo di dire al componente SelectItem di non usare questo metodo per determinare il codice
	 * identificativo dell'oggetto stesso.
	 * 
	 */
	public String toString() {
		return id;
	}
	
	public Boolean getFlgIdo() {
		return flgIdo;
	}

	public void setFlgIdo(Boolean flgIdo) {
		this.flgIdo = flgIdo;
	}

}
