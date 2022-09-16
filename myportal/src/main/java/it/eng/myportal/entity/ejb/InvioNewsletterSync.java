package it.eng.myportal.entity.ejb;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.NamingException;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.primefaces.model.UploadedFile;

import it.eng.myportal.beans.MailVO;

@Stateless
public class InvioNewsletterSync {

	// private static final Log log = LogFactory.getLog(InvioNewsletterSync.class);

	@Resource(mappedName = "java:jboss/mail/Default")
	private Session session;

	public InvioNewsletterSync() {

	}

	public void sendMail(MailVO mailVo) throws MessagingException, AddressException, NamingException {

		// Define a new mail message
		javax.mail.Message mail = new MimeMessage(session);
		mail.setFrom(new InternetAddress(mailVo.getFrom()));
		if (mailVo.getToList() != null) {
			for (String receiver : mailVo.getToList()) {
				mail.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(receiver));
			}

		}

		if (mailVo.getCCNList() != null) {
			for (String receiver : mailVo.getCCNList()) {
				mail.addRecipient(javax.mail.Message.RecipientType.BCC, new InternetAddress(receiver));
			}
		}

		Multipart multipart = new MimeMultipart();
		MimeBodyPart textBodyPart = new MimeBodyPart();
		textBodyPart.setContent(mailVo.getBody(), "text/html");
		multipart.addBodyPart(textBodyPart);

		for (final UploadedFile upload : mailVo.getUploadedFileList()) {

			MimeBodyPart attachmentBodyPart = new MimeBodyPart();
			DataSource ds = new ByteArrayDataSource(upload.getContents(), upload.getContentType());
			attachmentBodyPart.setFileName(upload.getFileName());
			attachmentBodyPart.setDataHandler(new DataHandler(ds));
			multipart.addBodyPart(attachmentBodyPart);
		}

		mail.setContent(multipart);
		mail.setSubject(mailVo.getSubject());

		Transport.send(mail);

	}

}
