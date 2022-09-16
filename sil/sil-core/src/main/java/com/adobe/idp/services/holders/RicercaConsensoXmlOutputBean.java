package com.adobe.idp.services.holders;

import java.io.Serializable;
import java.util.Date;

public class RicercaConsensoXmlOutputBean implements Serializable {

	private static final long serialVersionUID = -7023476616517909585L;

	private int codiceOutput;
	private String descrizioneOutput;

	private String stato;
	private Date dataRaccolta;
	private Date dataRevoca;

	public int getCodiceOutput() {
		return codiceOutput;
	}

	public void setCodiceOutput(int codiceOutput) {
		this.codiceOutput = codiceOutput;
	}

	public String getDescrizioneOutput() {
		return descrizioneOutput;
	}

	public void setDescrizioneOutput(String descrizioneOutput) {
		this.descrizioneOutput = descrizioneOutput;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public Date getDataRaccolta() {
		return dataRaccolta;
	}

	public void setDataRaccolta(Date dataRaccolta) {
		this.dataRaccolta = dataRaccolta;
	}

	public Date getDataRevoca() {
		return dataRevoca;
	}

	public void setDataRevoca(Date dataRevoca) {
		this.dataRevoca = dataRevoca;
	}

}
