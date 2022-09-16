package it.eng.sil.module.movimenti.processors;

public class ControlliDecretoApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -984259385479851947L;

	private Integer errorCode;
	private String errorDetail;

	public ControlliDecretoApplicationException(Integer errCodVariazione) {
		super();
		this.errorCode = errCodVariazione;
	}

	public ControlliDecretoApplicationException(Integer errorCode, String errorDetail) {
		super();
		this.errorCode = errorCode;
		this.errorDetail = errorDetail;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public String getErrorDetail() {
		return errorDetail;
	}

	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}

}
