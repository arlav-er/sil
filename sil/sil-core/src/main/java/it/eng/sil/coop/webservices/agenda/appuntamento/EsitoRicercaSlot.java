package it.eng.sil.coop.webservices.agenda.appuntamento;

import java.math.BigDecimal;

public class EsitoRicercaSlot {

	boolean trovato;
	BigDecimal prgSlot;
	String codErrore;
	String descrizioneErrore;
	String data;
	String ora;
	String durata;
	String codServizio;
	String descrizioneServizio;

	public boolean isTrovato() {
		return trovato;
	}

	public void setTrovato(boolean trovato) {
		this.trovato = trovato;
	}

	public BigDecimal getPrgSlot() {
		return prgSlot;
	}

	public void setPrgSlot(BigDecimal prgSlot) {
		this.prgSlot = prgSlot;
	}

	public String getCodErrore() {
		return codErrore;
	}

	public void setCodErrore(String codErrore) {
		this.codErrore = codErrore;
	}

	public String getDescrizioneErrore() {
		return descrizioneErrore;
	}

	public void setDescrizioneErrore(String descrizioneErrore) {
		this.descrizioneErrore = descrizioneErrore;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOra() {
		return ora;
	}

	public void setOra(String ora) {
		this.ora = ora;
	}

	public String getDurata() {
		return durata;
	}

	public void setDurata(String durata) {
		this.durata = durata;
	}

	public String getCodServizio() {
		return codServizio;
	}

	public void setCodServizio(String codServizio) {
		this.codServizio = codServizio;
	}

	public String getDescrizioneServizio() {
		return descrizioneServizio;
	}

	public void setDescrizioneServizio(String descrizioneServizio) {
		this.descrizioneServizio = descrizioneServizio;
	}

}
