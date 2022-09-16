package it.eng.myportal.beans.statistic;

import java.math.BigInteger;

public class StatsDTO {

	private BigInteger num;
	private String codice;
	private String descrizione;	

	public StatsDTO() {
	}

	public StatsDTO(BigInteger num, String codice, String descrizione) {
		super();
		this.num = num;
		this.codice = codice;
		this.descrizione = descrizione;
	}

	public BigInteger getNum() {
		return num;
	}

	public void setNum(BigInteger num) {
		this.num = num;
	}

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

}
