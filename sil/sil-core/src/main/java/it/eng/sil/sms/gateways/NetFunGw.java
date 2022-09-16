/*
 * Created on 8-gen-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.sms.gateways;

import java.util.Iterator;

import it.eng.sil.mail.SendMail;
import it.eng.sil.sms.Sms;
import it.eng.sil.sms.SmsException;

public class NetFunGw extends BaseGateway {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(NetFunGw.class.getName());
	private String from = null;
	private String to = null;
	private String smtp = null;
	private String userID = null;
	private String mittenteGw = null;

	public NetFunGw() {
		super();
	}

	public void send() throws SmsException {
		from = (String) parameters.get("FROM");
		to = (String) parameters.get("TO");
		smtp = (String) parameters.get("SMTP");
		userID = (String) parameters.get("USERID");
		mittenteGw = (String) parameters.get("MITTENTE");

		SendMail sm = new SendMail();

		sm.setSMTPServer(smtp);
		sm.setFromRecipient(from);
		sm.setSubject(userID);

		StringBuffer buf = new StringBuffer();
		buf.append("<<");

		Iterator iter = smsList.iterator();
		try {
			while (iter.hasNext()) {

				Sms sms = (Sms) iter.next();

				buf.append(sms.getText());

				buf.append("[[");
				buf.append(mittenteGw);
				buf.append("]]");

				buf.append("[");
				String normCell = normalizzaNumCell(sms.getCellNumber());
				buf.append(normCell);
				buf.append("]");

				if (normCell.startsWith("+3933") || normCell.startsWith("+3934"))
					buf.append("((4))");

				if (iter.hasNext())
					buf.append("\r\n|||\r\n");
			}

			buf.append(">>");

			sm.setBody(buf.toString());

			sm.setToRecipient(to);
			sm.send();

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "QueryExecutor::executeQuery:", (Exception) e);

			throw new SmsException(e.getMessage());
		}

	}

	String normalizzaNumCell(String cell) {
		if (cell.startsWith("+"))
			return cell;

		if (cell.startsWith("00"))
			return "+" + cell.substring(2);

		return "+39" + cell;

	}

	@Override
	public boolean isReachable() {
		// String smtp = (String) parameters.get("SMTP");
		// try {
		// InetAddress addr = InetAddress.getByName(smtp);
		// boolean ret = addr.isReachable(2000);
		// return ret;
		// } catch (UnknownHostException e) {
		// _logger.warn("Host '" + smtp + "' unreachable");
		// return false;
		// } catch (IOException e) {
		// _logger.warn("Host '" + smtp + "' unreachable");
		// return false;
		// }
		return true;
	}

}