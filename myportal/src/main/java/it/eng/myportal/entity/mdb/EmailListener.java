package it.eng.myportal.entity.mdb;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.EmailDTO;


/**
 * TODO gestire l'errore di invio email adeguatamente attraverso una coda dei morti.
 * 
 * @author Rodi A.
 *
 */
@MessageDriven(name = "EmailListener", activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "AUTO_ACKNOWLEDGE"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/email_queue") })
public class EmailListener implements MessageListener {

	private static final Log log = LogFactory.getLog(EmailListener.class);
	
	@Resource(mappedName = "java:jboss/mail/Default")
	private Session session;
	

	public EmailListener() {

	}

	protected String toString(List<String> list) {
		if (list == null || list.isEmpty()) {
			return "";
		}
		StringBuilder ret = new StringBuilder("");
		for (String el : list) {			
			ret.append(el + ";");
		}
		return ret.toString();
	}
	
	@Override
	public void onMessage(Message inMessage) {
		if (inMessage instanceof ObjectMessage) {
			EmailDTO email = null;
			try {
				email = (EmailDTO) ((ObjectMessage) inMessage).getObject();				
				sendMail(email);
				log.info("Mail oggetto: '"+email.getSubject()+"' to: {" + toString(email.getTos()) + "} cc: {"+toString(email.getCcs())+"} ccn: {"+toString(email.getCcns())+"} consegnata all'SMTP Server!");
			} catch (Exception e) {
				log.error("Mail oggetto: '"+email.getSubject()+"' to: {" + toString(email.getTos()) + "} cc: {"+toString(email.getCcs())+"} ccn: {"+toString(email.getCcns())+"} NON CONSEGNATA all'SMTP Server! - errore: " + e.getMessage());
			} 
		}
	}

	/**
	 * Si occupa di spedire le email ai vari destinatari
	 * 
	 * @param from
	 * @param tos
	 * @param ccs
	 * @param subject
	 * @param message_text
	 * @param attachment_filename
	 * @param attachment_text
	 * @throws MessagingException
	 * @throws AddressException
	 * @throws NamingException
	 * @throws JMSException
	 */
	public void sendMail(EmailDTO email) throws MessagingException, AddressException, NamingException, JMSException {

		if ((email == null) || (email.getTos() == null)) {
			throw new MessagingException();
		}

		// Define a new mail message
		javax.mail.Message mail = new MimeMessage(session);
		mail.setFrom(new InternetAddress(email.getFrom()));
		for (String receiver : email.getTos()) {
			mail.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(receiver));
		}
		
		if (email.getCcs() != null) {
			for (String receiver : email.getCcs()) {
				mail.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(receiver));
			}
		}

		if (email.getCcns() != null) {
			for (String receiver : email.getCcns()) {
				mail.addRecipient(javax.mail.Message.RecipientType.BCC, new InternetAddress(receiver));
			}
		}
		
		mail.setSubject(email.getSubject());

		mail.setContent(email.getMessageBody(),"text/html");

		Transport.send(mail);
		

	}

}