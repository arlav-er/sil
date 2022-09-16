package it.eng.myportal.dtos;

import java.util.Date;

public class AppuntamentoFilterDTO implements IDTO {
	private static final long serialVersionUID = -1875966129372820510L;

	private Boolean ricercaEsatta;
	private String codiceFiscale;
	private String nome;
	private String cognome;
	private Date dataDal;
	private Date dataAl;

	public AppuntamentoFilterDTO() {
		ricercaEsatta = false;
	}

	public Boolean getRicercaEsatta() {
		return ricercaEsatta;
	}

	public void setRicercaEsatta(Boolean ricercaEsatta) {
		this.ricercaEsatta = ricercaEsatta;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public Date getDataDal() {
		return dataDal;
	}

	public void setDataDal(Date dataDal) {
		this.dataDal = dataDal;
	}

	public Date getDataAl() {
		return dataAl;
	}

	public void setDataAl(Date dataAl) {
		this.dataAl = dataAl;
	}

}
