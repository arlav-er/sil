package it.eng.myportal.dtos;

import java.io.Serializable;

public class ElementoClassificaDTO implements Serializable {

	private static final long serialVersionUID = -9113672725741024780L;
	private String codice;
	private String descrizione;
	private Integer totale;
	private Float percentuale;
	
	public String getCodice() {
		return codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public Integer getTotale() {
		return totale;
	}
	public void setTotale(Integer totale) {
		this.totale = totale;
	}
	
	public Float getPercentuale() {
		return percentuale;
	}
	public void setPercentuale(Float percentuale) {
		this.percentuale = percentuale;
	}
}
