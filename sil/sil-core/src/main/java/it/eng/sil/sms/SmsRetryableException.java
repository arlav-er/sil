package it.eng.sil.sms;

/**
 * Eccezione della gerarchia RuntimeException per gestire il reinvio del messaggio in eccezione
 * 
 * @author OMenghini
 *
 */
public class SmsRetryableException extends RuntimeException {

	private static final long serialVersionUID = 6284699589758045048L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SmsRetryableException.class.getName());

	public SmsRetryableException() {
		super();
	}

	public SmsRetryableException(String message, Throwable cause) {
		super(message, cause);
		_logger.error(message, cause);
	}

	public SmsRetryableException(String message) {
		super(message);
		_logger.error(message);
	}

	public SmsRetryableException(Throwable cause) {
		super(cause);
		_logger.error(cause);
	}

}
