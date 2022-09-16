package it.eng.myportal.dtos;

import java.io.Serializable;
import java.util.Date;


public class GenericFiltroDecodeDTO implements IDecode,Serializable{
	
	private static final long serialVersionUID = -1001689160388578459L;
	protected String id;
	protected String descrizione;
	protected String tipoCodifica;
	
	protected Date dtInizioVal;
	protected Date dtFineVal;
	
	public GenericFiltroDecodeDTO() {
		super();
	}
	
	public GenericFiltroDecodeDTO(String id) {
		super();
		this.id = id;
	}

	public GenericFiltroDecodeDTO(String codice, String descrizione, String tipoCodifica) {
		this.id = codice;
		this.descrizione = descrizione;
		this.tipoCodifica = tipoCodifica;
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
		if (o == null) return false;
		if (this == o) return true;
		if (!o.getClass().isAssignableFrom(this.getClass())) {
			return false;
		}

		 String otherId = ((GenericFiltroDecodeDTO) o ).getId();
		if (otherId != null && this.getId() != null)
			return otherId.equals(this.getId());
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		if (this.getId() != null) return this.getId().hashCode();
		else return 0;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Attenzione! Questo metodo viene utilizzato da selectItems di JSF e non può essere modificato!
	 * A meno che qualcuno non trovi il modo di dire al componente SelectItem di non usare questo metodo
	 * per determinare il codice identificativo dell'oggetto stesso.
	 * 
	 */
	public String toString() {
		return id;
	}

	public String getTipoCodifica() {
		return tipoCodifica;
	}

	public void setTipoCodifica(String tipoCodifica) {
		this.tipoCodifica = tipoCodifica;
	}

	
}
