/*
 * Creato il 16-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.sms.gateways;

import java.util.Iterator;

import it.eng.sil.mail.SendMail;
import it.eng.sil.sms.Sms;
import it.eng.sil.sms.SmsException;

/**
 * @author vuoto
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class SilerGtw extends BaseGateway {
	private String from = null;
	private String to = null;
	private String smtp = null;
	private String oggetto = null;

	public SilerGtw() {
		super();
	}

	public void send() throws SmsException {
		from = (String) parameters.get("FROM");
		to = (String) parameters.get("TO");
		smtp = (String) parameters.get("SMTP");
		oggetto = (String) parameters.get("OGGETTO");

		SendMail sm = new SendMail();

		sm.setSMTPServer(smtp);
		sm.setFromRecipient(from);
		// sm.setToRecipient(to);
		sm.setSubject(oggetto);

		StringBuffer buf = new StringBuffer();

		Iterator iter = smsList.iterator();
		while (iter.hasNext()) {

			Sms sms = (Sms) iter.next();
			buf.append("*I*");
			buf.append(sms.getCellNumber());
			buf.append(":");
			buf.append(sms.getText());

			buf.append("*V*CODICE_VALIDAZIONE*F*\r\n");
		}

		sm.setBody(buf.toString());

		try {

			// invio al mittente per ricevuta
			sm.setToRecipient(from);
			sm.send();
			sm.setToRecipient(to);
			sm.send();

		} catch (Exception e) {

			e.printStackTrace();
			throw new SmsException(e.getMessage());
		}

	}

	@Override
	public boolean isReachable() {
		return true;
	}

}
