package it.eng.sil.coop.webservices.firmagrafometrica.custom;

import java.io.Serializable;

public class ServiceXmlInputBean implements Serializable {

	private static final long serialVersionUID = 6277273655488944641L;

	private String serviceProvider;
	private String idDocumento;
	private String tipoDocumento;
	private String nomeCittadino;
	private String cognomeCittadino;
	private String codiceFiscaleCittadino;
	private String codiceUtenteOperatore;
	private String numKloDocumento;

	public String getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public String getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(String idDocumento) {
		this.idDocumento = idDocumento;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getNomeCittadino() {
		return nomeCittadino;
	}

	public void setNomeCittadino(String nomeCittadino) {
		this.nomeCittadino = nomeCittadino;
	}

	public String getCognomeCittadino() {
		return cognomeCittadino;
	}

	public void setCognomeCittadino(String cognomeCittadino) {
		this.cognomeCittadino = cognomeCittadino;
	}

	public String getCodiceFiscaleCittadino() {
		return codiceFiscaleCittadino;
	}

	public void setCodiceFiscaleCittadino(String codiceFiscaleCittadino) {
		this.codiceFiscaleCittadino = codiceFiscaleCittadino;
	}

	public String getCodiceUtenteOperatore() {
		return codiceUtenteOperatore;
	}

	public void setCodiceUtenteOperatore(String codiceUtenteOperatore) {
		this.codiceUtenteOperatore = codiceUtenteOperatore;
	}

	public String getNumKloDocumento() {
		return numKloDocumento;
	}

	public void setNumKloDocumento(String numKloDocumento) {
		this.numKloDocumento = numKloDocumento;
	}

}
