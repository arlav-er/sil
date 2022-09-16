package it.eng.sil.myaccount.model.ejb.mdb;

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

import it.eng.sil.myaccount.model.pojo.notification.EmailPOJO;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;

@MessageDriven(name = "EmailListener", activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "AUTO_ACKNOWLEDGE"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = ConstantsSingleton.MYACCOUNT_MAIL_QUEUE) })
public class EmailListener implements MessageListener {

	private static final Log log = LogFactory.getLog(EmailListener.class);

	@Resource(mappedName = "java:jboss/mail/Default")
	private Session session;

	public EmailListener() {

	}

	@Override
	public void onMessage(Message inMessage) {
		EmailPOJO email = null;
		if (inMessage instanceof ObjectMessage) {
			try {
				email = (EmailPOJO) ((ObjectMessage) inMessage).getObject();
				sendMail(email);
				log.info("Mail CONSEGNATA all'SMTP Server. Mail: " + email);
			} catch (Exception e) {
				log.error("Mail NON CONSEGNATA all'SMTP Server!. Mail: " + email,e);
			}
		}
	}

	private void sendMail(EmailPOJO email) throws MessagingException,
			AddressException, NamingException, JMSException {

		if (!email.isReadyForSending()) {
			throw new MessagingException(
					"Almeno uno dei parametri della mail non e' stato specificato. Invio abortito!");
		}

		javax.mail.Message mail = new MimeMessage(session);
		mail.setFrom(new InternetAddress(email.getFrom()));
		if (email.getTos() != null) {
			for (String receiver : email.getTos()) {
				mail.addRecipient(javax.mail.Message.RecipientType.TO,
						new InternetAddress(receiver));
			}
		}

		if (email.getCcs() != null) {
			for (String receiver : email.getCcs()) {
				mail.addRecipient(javax.mail.Message.RecipientType.CC,
						new InternetAddress(receiver));
			}
		}

		if (email.getCcns() != null) {
			for (String receiver : email.getCcns()) {
				mail.addRecipient(javax.mail.Message.RecipientType.BCC,
						new InternetAddress(receiver));
			}
		}

		mail.setSubject(email.getSubject());

		String body = email.getMessageBody();
		if (body == null) {
			body = "";
		}

		mail.setContent(body, "text/html");

		Transport.send(mail);
	}
}
