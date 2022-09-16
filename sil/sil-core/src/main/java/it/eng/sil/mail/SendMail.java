/*
 * Creato il 18-nov-04
 * Author: vuoto
 * 
 */
package it.eng.sil.mail;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMail {

	String SMTPServer;
	String fromRecipient; // ex sender;
	String toRecipient;
	String ccRecipient;
	String bccRecipient;
	String subject = "";
	String body = "";

	List attachments;
	
	public void send() throws SendMailException, MessagingException {

		if (SMTPServer == null || SMTPServer.equals(""))
			throw new SendMailException("Il server SMPT non è stato impostato");

		if (fromRecipient == null || fromRecipient.equals(""))
			throw new SendMailException("Il mittente (FROM) non è stato indicato");

		if (toRecipient == null || toRecipient.equals(""))
			throw new SendMailException("Il destinarario (TO) non è stato indicato");

		if (subject == null)
			throw new SendMailException("L'oggetto della mail è nullo");

		// Create some properties and get the default Session;
		Properties props = System.getProperties();

		props.put("mail.smtp.host", SMTPServer);
		props.put("user.name", "Nessuno");
		props.put("mail.protocol.host", "NNNNN");
		props.put("mail.protocol.user", "BO");

		Session session = Session.getDefaultInstance(props, null);

		// try {
		// Create a message.
		MimeMessage msg = new MimeMessage(session);

		// extracts the senders and adds them to the message.
		// Sender is a comma-separated list of e-mail addresses as per RFC822.
		{
			InternetAddress[] TheAddresses = InternetAddress.parse(fromRecipient);
			msg.addFrom(TheAddresses);
		}

		// Extract the recipients and assign them to the message.
		// Recipient is a comma-separated list of e-mail addresses as per
		// RFC822.
		{
			InternetAddress[] TheAddresses = InternetAddress.parse(toRecipient);
			msg.addRecipients(Message.RecipientType.TO, TheAddresses);
		}

		// Extract the Cc-recipients and assign them to the message;
		// CcRecipient is a comma-separated list of e-mail addresses as per
		// RFC822
		if (null != ccRecipient) {
			InternetAddress[] TheAddresses = InternetAddress.parse(ccRecipient);
			msg.addRecipients(Message.RecipientType.CC, TheAddresses);
		}

		// Extract the Bcc-recipients and assign them to the message;
		// BccRecipient is a comma-separated list of e-mail addresses as per
		// RFC822
		if (null != bccRecipient) {
			InternetAddress[] TheAddresses = InternetAddress.parse(bccRecipient);
			msg.addRecipients(Message.RecipientType.BCC, TheAddresses);
		}

		// Subject field
		msg.setSubject(subject);

		// Create the Multipart to be added the parts to

		if (null != attachments) {

			Multipart mp = new MimeMultipart();

			// Create and fill the first message part
			{
				MimeBodyPart mbp = new MimeBodyPart();
				mbp.setText(body);

				// Attach the part to the multipart;
				mp.addBodyPart(mbp);
			}

			// Attach the files to the message
			for (int i = 0; i < attachments.size(); i++) {
				MimeBodyPart mbp = new MimeBodyPart();
				File file = (File) attachments.get(i);
				FileDataSource fds = new FileDataSource(file);
				mbp.setDataHandler(new DataHandler(fds));
				mbp.setFileName(fds.getName());
				mp.addBodyPart(mbp);
				msg.setContent(mp);
			}
		} else {
			msg.setText(body);

		}

		// Set the Date: header
		msg.setSentDate(new Date());
		// Send the message;
		Transport.send(msg);
	}

	/**
	 * @return
	 */
	public List getAttachments() {
		return attachments;
	}

	/**
	 * @return
	 */
	public String getBccRecipient() {
		return bccRecipient;
	}

	/**
	 * @return
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @return
	 */
	public String getCcRecipient() {
		return ccRecipient;
	}

	/**
	 * @return
	 */
	public String getFromRecipient() {
		return fromRecipient;
	}

	/**
	 * @return
	 */
	public String getSMTPServer() {
		return SMTPServer;
	}

	/**
	 * @return
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @return
	 */
	public String getToRecipient() {
		return toRecipient;
	}

	/**
	 * @param list
	 */
	public void setAttachments(List list) {
		attachments = list;
	}

	/**
	 * @param string
	 */
	public void setBccRecipient(String string) {
		bccRecipient = string;
	}

	/**
	 * @param string
	 */
	public void setBody(String string) {
		body = string;
	}

	/**
	 * @param string
	 *            lista degli indirizzi email separati da virgola ','
	 */
	public void setCcRecipient(String string) {
		ccRecipient = string;
	}

	/**
	 * @param string
	 */
	public void setFromRecipient(String string) {
		fromRecipient = string;
	}

	/**
	 * @param string
	 */
	public void setSMTPServer(String string) {
		SMTPServer = string;
	}

	/**
	 * @param string
	 */
	public void setSubject(String string) {
		subject = string;
	}

	/**
	 * @param string
	 *            lista degli indirizzi email separati da virgola ','
	 */
	public void setToRecipient(String string) {
		toRecipient = string;
	}

} // End of public class SendMai
