package it.eng.myportal.dtos;

import java.util.Date;

public class PercorsoLavoratoreElementDTO {

	private Date dataDa;
	private String dataDaStr;
	private Date dataA;
	private String dataAStr;
	private String tipo;
	private String descrizione;

	public Date getDataDa() {
		return dataDa;
	}

	public void setDataDa(Date dataDa) {
		this.dataDa = dataDa;
	}

	public Date getDataA() {
		return dataA;
	}

	public void setDataA(Date dataA) {
		this.dataA = dataA;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDataDaStr() {
		return dataDaStr;
	}

	public void setDataDaStr(String dataDaStr) {
		this.dataDaStr = dataDaStr;
	}

	public String getDataAStr() {
		return dataAStr;
	}

	public void setDataAStr(String dataAStr) {
		this.dataAStr = dataAStr;
	}

}
