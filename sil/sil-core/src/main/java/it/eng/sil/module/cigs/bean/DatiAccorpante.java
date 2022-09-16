package it.eng.sil.module.cigs.bean;

import java.math.BigDecimal;

/**
 * Bean d'appoggio con i dati anagrafici del lavoratore accorpante
 */
public class DatiAccorpante {
	private BigDecimal prgLavoratoreAccorpante;
	private String codiceFiscale;
	private String nome;
	private String cognome;
	private String sesso;
	private String comuneNascita;
	private String dataNascita;

	public DatiAccorpante() {
	}

	public BigDecimal getPrgLavoratoreAccorpante() {
		return prgLavoratoreAccorpante;
	}

	public void setPrgLavoratoreAccorpante(BigDecimal prgLavoratoreAccorpante) {
		this.prgLavoratoreAccorpante = prgLavoratoreAccorpante;
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

	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	public String getComuneNascita() {
		return comuneNascita;
	}

	public void setComuneNascita(String comuneNascita) {
		this.comuneNascita = comuneNascita;
	}

	public String getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(String dataNascita) {
		this.dataNascita = dataNascita;
	}
}