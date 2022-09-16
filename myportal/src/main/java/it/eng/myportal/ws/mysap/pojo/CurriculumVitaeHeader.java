package it.eng.myportal.ws.mysap.pojo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CurriculumVitaeHeader {

	private int id;
	private String descrizione;
	private Boolean flagEliminato;
	private Boolean flagInviato;
	
	
	public CurriculumVitaeHeader() {
	}


	public int getId() {
		return id;
	}
	public String getDescrizione() {
		return descrizione;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CurriculumVitaeHeader other = (CurriculumVitaeHeader) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime + id;

	}


	public Boolean getFlagEliminato() {
		return flagEliminato;
	}


//	public Boolean isEliminato() {
//		return flagEliminato;
//	}

	
	public void setFlagEliminato(Boolean flagEliminato) {
		this.flagEliminato = flagEliminato;
	}


	public Boolean getFlagInviato() {
		return flagInviato;
	}

	
//	public Boolean isInviato() {
//		return flagInviato;
//	}


	public void setFlagInviato(Boolean flagInviato) {
		this.flagInviato = flagInviato;
	}



}
