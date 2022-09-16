package it.eng.sil.coop.webservices.mystage.patto;

public class MyStageException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String codiceFiscale;

	private String respCode;
	private String respDesc;

	public MyStageException(String message) {
		super(message);
	}

	public MyStageException(String _codiceFiscale, String _respCode, String _respDesc) {
		this(_codiceFiscale, _respCode, _respDesc, null);
	}

	public MyStageException(String codiceFiscale, String respCode, String respDesc, Throwable cause) {
		super("codiceFiscale:" + codiceFiscale + ", respCode:" + respCode + ", respDesc:" + respDesc, cause);
		this.codiceFiscale = codiceFiscale;
		this.respCode = respCode;
		this.respDesc = respDesc;
	}

	public MyStageException(String message, Throwable cause) {
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
