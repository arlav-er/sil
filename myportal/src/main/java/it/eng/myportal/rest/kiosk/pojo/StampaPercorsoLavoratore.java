package it.eng.myportal.rest.kiosk.pojo;

public class StampaPercorsoLavoratore {

	private final static int ESITO_POSITIVO = 0;
	private final static int ESITO_NOT_FOUND = 4;

	private String codProvincia;
	private Integer codEsito;
	private String descrizione;
	private Exception exception;
	private byte[] pdfContent;

	public StampaPercorsoLavoratore(String codProvincia, Integer codEsito) {
		super();
		this.codProvincia = codProvincia;
		this.codEsito = codEsito;
	}

	public StampaPercorsoLavoratore(String codProvincia, Integer codEsito, String descrizione) {
		super();
		this.codProvincia = codProvincia;
		this.codEsito = codEsito;
		this.descrizione = descrizione;
	}

	public StampaPercorsoLavoratore(String codProvincia, Exception exception) {
		super();
		this.codProvincia = codProvincia;
		this.exception = exception;
	}

	public String getCodProvincia() {
		return codProvincia;
	}

	public Integer getCodEsito() {
		return codEsito;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public Exception getException() {
		return exception;
	}

	public byte[] getPdfContent() {
		return pdfContent;
	}

	public void setPdfContent(byte[] pdfContent) {
		this.pdfContent = pdfContent;
	}

	public boolean isEsitoPositivo() {
		boolean ret = false;

		if (this.codEsito != null && this.codEsito == ESITO_POSITIVO) {
			ret = true;
		}

		return ret;
	}

	public boolean isNotFound() {
		boolean ret = false;

		if (this.codEsito != null && this.codEsito == ESITO_NOT_FOUND) {
			ret = true;
		}

		return ret;
	}

}
