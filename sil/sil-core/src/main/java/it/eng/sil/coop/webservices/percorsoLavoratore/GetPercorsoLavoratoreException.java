package it.eng.sil.coop.webservices.percorsoLavoratore;

public class GetPercorsoLavoratoreException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String codiceFiscale;

	private String respCode;
	private String respDesc;

	public GetPercorsoLavoratoreException(String message) {
		super(message);
	}

	public GetPercorsoLavoratoreException(String _codiceFiscale, String _respCode, String _respDesc) {
		this(_codiceFiscale, _respCode, _respDesc, null);
	}

	public GetPercorsoLavoratoreException(String codiceFiscale, String respCode, String respDesc, Throwable cause) {
		super("codiceFiscale:" + codiceFiscale + ", respCode:" + respCode + ", respDesc:" + respDesc, cause);
		this.codiceFiscale = codiceFiscale;
		this.respCode = respCode;
		this.respDesc = respDesc;
	}

	public GetPercorsoLavoratoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public String getRespCode() {
		return respCode;
	}

	public String getRespDesc() {
		return respDesc;
	}

}
