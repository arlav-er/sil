package it.eng.sil.util.amministrazione.impatti;

/**
 * @author Landi Giovanni, 15/03/2005
 */
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

public class MobilitaException extends EMFUserError {
	private int code = 0;

	/**
	 * int _code il codice mappato nel file messages_it_IT.properties
	 */
	public MobilitaException(int _code) {
		super(EMFErrorSeverity.ERROR, _code);
		code = _code;
	}

	public int getCode() {
		return code;
	}
}