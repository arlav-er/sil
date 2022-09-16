/**
 * @author Finessi_M 28-apr-2003
 * Classe di utilità usata per aggiungere dei SourceBean di tipo <MESSAGE TEXT="...." CODE="..."> ad un altro
 * SourceBean che funge da contenitore.
 */

package it.eng.afExt.utils;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.message.MessageBundle;

public class MessageAppender {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MessageAppender.class.getName());

	public static final String USER_MESSAGE = "USER_MESSAGE";
	public static final String TEXT = "TEXT";
	public static final String CODE = "CODE";

	/**
	 * Method appendMessage. Aggiunge un SourceBean riportante un messaggio + un eventuale codice al SourceBean src
	 * 
	 * @param src
	 * @param text
	 * @param code
	 */
	public static void appendMessage(SourceBean src, String text, String code) {
		try {
			if (src != null) {
				SourceBean msgSB = new SourceBean(USER_MESSAGE);

				if (text != null)
					msgSB.setAttribute(TEXT, text);
				if (code != null)
					msgSB.setAttribute(CODE, code);

				src.setAttribute(msgSB);
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "MessageAppender::appendMessage()", ex);

			// System.out.println(e);
		}
	}

	/**
	 * Reperisce il messaggio attraverso un codice
	 * 
	 * @param src
	 * @param code
	 */
	public static void appendMessage(SourceBean src, int code) {
		appendMessage(src, code, null);
	}

	/**
	 * Reperisce il messaggio attraverso un codice e sostituisce i parametri al posto dei placeholders "%"
	 * 
	 * @param src
	 * @param code
	 * @param params
	 */
	public static void appendMessage(SourceBean src, int code, Vector params) {

		String codeStr = String.valueOf(code);
		String description = MessageBundle.getMessage(codeStr);

		if ((params != null) && (description != null)) {
			for (int i = 0; i < params.size(); i++) {
				String toParse = description;
				String replacing = "%" + i;
				String replaced = (String) params.elementAt(i);
				StringBuffer parsed = new StringBuffer();
				int parameterIndex = toParse.indexOf(replacing);
				while (parameterIndex != -1) {
					parsed.append(toParse.substring(0, parameterIndex));
					parsed.append(replaced);
					toParse = toParse.substring(parameterIndex + replacing.length(), toParse.length());
					parameterIndex = toParse.indexOf(replacing);
				} // while (parameterIndex != -1)
				parsed.append(toParse);
				description = parsed.toString();
			} // for (int i = 0; i < params.size(); i++)

		} // if ((params != null) && (description != null))

		try {
			SourceBean msgSB = new SourceBean(USER_MESSAGE);
			msgSB.setAttribute(TEXT, description);
			msgSB.setAttribute(CODE, codeStr);

			src.setAttribute(msgSB);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "MessageAppender::appendMessage()", ex);

			// System.out.println(e);
		}

	} // private void init(String severity, int code, Vector params, Object
		// additionalInfo)

}// end of class
