package it.eng.myportal.exception;

import java.util.List;

import javax.ejb.ApplicationException;
import javax.ejb.EJBException;

/**
 * Questo è l'errore applicativo di default del portale. E' corretto lanciarlo
 * per descrivere un errore nei dati, nella loro semantica o coerenza MA NON per
 * wrappare altre eccezioni
 * 
 * 11/2014 deprecati warnings e aggiunto @ApplicationException
 * 
 * @author pegoraro
 * 
 */
@ApplicationException
public class MyPortalException extends EJBException {

	private static final long serialVersionUID = 6457572213251421792L;

	private String codErrore;

	private boolean message;
	private String strMessaggio;

	/* Lista di messaggi di warning */
	private List<String> warnings;

	public MyPortalException(String codErrore, Exception ex) {
		this(codErrore, ex, false);
	}

	public MyPortalException(String codErrore) {
		this(codErrore, false);
	}

	public MyPortalException(String message, Exception ex, boolean isMessage) {
		this(message, ex, isMessage, null);
	}

	public MyPortalException(String message, boolean isMessage) {
		super();
		this.message = isMessage;
		if (isMessage) {
			this.strMessaggio = message;
		} else {
			this.codErrore = message;
		}
	}

	public MyPortalException(String codErrore, String message) {
		super();

		this.strMessaggio = message;
		this.codErrore = codErrore;

	}

	public MyPortalException(String codErrore, Exception ex, List<String> warnings) {
		this(codErrore, ex, false, warnings);
	}

	public MyPortalException(String codErrore, List<String> warnings) {
		this(codErrore, false, warnings);
	}

	public MyPortalException(String message, Exception ex, boolean isMessage, List<String> warnings) {
		super(ex);
		this.message = isMessage;
		if (isMessage) {
			this.strMessaggio = message;
		} else {
			this.codErrore = message;
		}
		this.warnings = warnings;
	}

	public MyPortalException(String message, boolean isMessage, List<String> warnings) {
		super();
		this.message = isMessage;
		if (isMessage) {
			this.strMessaggio = message;
		} else {
			this.codErrore = message;
		}
		this.warnings = warnings;
	}

	public String getCodErrore() {
		return codErrore;
	}

	public void setCodErrore(String codErrore) {
		this.codErrore = codErrore;
	}

	public String getStrMessaggio() {
		return strMessaggio;
	}

	public void setStrMessaggio(String messaggio) {
		this.strMessaggio = messaggio;
	}

	public boolean isMessage() {
		return message;
	}

	public void setMessage(boolean message) {
		this.message = message;
	}

}
