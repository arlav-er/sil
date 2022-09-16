/*
 * Created on 27-nov-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.sms.gateways;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
public class SmsItaliaGw extends BaseGateway {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SmsItaliaGw.class.getName());
	private String from = null;
	private String to = null;
	private String smtp = null;
	private String oggetto = null;

	public SmsItaliaGw() {
		super();
	}

	public void send() throws SmsException {
		from = (String) parameters.get("FROM");
		to = (String) parameters.get("TO");
		smtp = (String) parameters.get("SMTP");
		// oggetto = (String) parameters.get("OGGETTO");
		String password = (String) parameters.get("PASSWORD");

		SendMail sm = new SendMail();

		sm.setSMTPServer(smtp);
		sm.setFromRecipient(from);
		// sm.setToRecipient(to);
		sm.setSubject("");

		StringBuffer buf = new StringBuffer();

		Iterator iter = smsList.iterator();
		try {
			while (iter.hasNext()) {

				Sms sms = (Sms) iter.next();
				buf.append("*IM*");
				buf.append(sms.getCellNumber());
				buf.append(":");
				buf.append(sms.getText());

				buf.append("*V*" + calcolaCodValidazione(password, sms.getCellNumber()) + "*F*\r\n");
			}

			sm.setBody(buf.toString());

			// invio al mittente per ricevuta
			// sm.setToRecipient(from);
			// sm.send();

			sm.setToRecipient(to);
			sm.send();

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "QueryExecutor::executeQuery:", (Exception) e);

			throw new SmsException(e.getMessage());
		}

	}

	private String calcolaCodValidazione(String seme, String cellNum)
			throws InvalidKeyException, NoSuchAlgorithmException {

		String p = seme.substring(0, 4);
		String g = cellNum.substring((cellNum.length() - 4));

		String value = p + g;
		// System.out.println(value);

		String md5 = MD5Singleton.getInstance().enCrypt(value);

		return "M_" + md5.substring(md5.length() - 11);

	}

	@Override
	public boolean isReachable() {
		return true;
	}

}
