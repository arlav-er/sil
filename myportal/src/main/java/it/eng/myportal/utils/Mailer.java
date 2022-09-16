package it.eng.myportal.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.EmailDTO;

public class Mailer {
	
	private static final Log log = LogFactory.getLog(Mailer.class);
	
	//singleton
	private static final Mailer instance = new Mailer();
	
	private Mailer() {}
	
	public static Mailer getInstance() {
		return instance;
	}

	/**
	 * fa l'escape dei caratteri non consentiti nell'url
	 * 
	 * @param parameter
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String escapeCharacters(String parameter) throws UnsupportedEncodingException {
		return URLEncoder.encode(parameter, "utf8");
	}
	
	public void putInQueue(ConnectionFactory connectionFactory,Queue emailQueue, EmailDTO email) {
		Connection theConnection = null;
        javax.jms.Session theSession = null;
        MessageProducer theProducer = null;
        try {
        	log.info("Invio email: oggetto:"+ email.getSubject() + "; to:"+ toString(email.getTos()) +";");
        	
        	theConnection = connectionFactory.createConnection();
        	theSession = theConnection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            theProducer = theSession.createProducer(emailQueue);
            ObjectMessage theMessage = theSession.createObjectMessage();            
            theMessage.setObject(email);
            theProducer.send(theMessage);
	    } catch (Exception inException) {
	    	log.error("Errore durante l'invio mail: " + inException.getMessage() + " - errore: " + inException); 
	    } finally {
	    	try {
				theProducer.close();
				theSession.close();
				theConnection.close();
			} catch (NullPointerException e) {				
				log.error("Errore durante la chiusura delle connessioni: " + e.getMessage());
			} catch (JMSException e) {				
				log.error("Errore durante la chiusura delle connessioni: " + e.getMessage());
			}
	    	
	    }
		
	}

	protected void addAttachments(String[] attachments, Multipart multipart) throws MessagingException,
			AddressException {
		for (int i = 0; i <= attachments.length - 1; i++) {
			String filename = attachments[i];
			MimeBodyPart attachmentBodyPart = new MimeBodyPart();

			// use a JAF FileDataSource as it does MIME type detection
			DataSource source = new FileDataSource(filename);
			attachmentBodyPart.setDataHandler(new DataHandler(source));

			// assume that the filename you want to send is the same as the
			// actual file name - could alter this to remove the file path
			attachmentBodyPart.setFileName(filename);

			// add the attachment
			multipart.addBodyPart(attachmentBodyPart);
		}
	}

	protected String toString(List<String> list) {
		if (list == null) return null;
		StringBuilder ret = new StringBuilder("");
		for (String el : list) {			
			ret.append(el + ";");
		}
		return ret.toString();
	}

}
