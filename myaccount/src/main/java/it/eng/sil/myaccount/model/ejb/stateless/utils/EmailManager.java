package it.eng.sil.myaccount.model.ejb.stateless.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import it.eng.sil.myaccount.model.pojo.notification.EmailPOJO;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.model.utils.EmailConstants;
import it.eng.sil.myaccount.utils.HTMLEntities;
import it.eng.sil.myaccount.utils.UrlGenerator;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;

@Stateless
public class EmailManager {
	private static final Log log = LogFactory.getLog(EmailManager.class);

	@EJB
	ConstantsSingleton constantsSingleton;

	@EJB
	EmailConstants emailConstants;

	@EJB
	DeProvinciaEJB deProvinciaEJB;

	// TODO rivedere la persistenza delle code
	@Resource(name = "InVmConnectionFactory", mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "myaccount_email_queue", mappedName = "java:/" + ConstantsSingleton.MYACCOUNT_MAIL_QUEUE)
	private Queue emailQueue;

	public String generateEmail(String url, Map<String, String> params) {
		UrlGenerator urlGenerator = new UrlGenerator(url, params);
		WebClient webClient = new WebClient();
		webClient.waitForBackgroundJavaScript(30 * 1000);

		HtmlPage page = null;
		try {

			page = webClient.getPage(urlGenerator.getUrl());
		} catch (FailingHttpStatusCodeException | IOException e) {
			String errMsg = "Errore nella generazione della pagina [" + urlGenerator.getUrl() + "]";
			log.error(errMsg, e);
			throw new RuntimeException(errMsg, e);
		}
		return HTMLEntities.htmlentities(page.asXml());
	}

	public void pushToEmailNotificationQueue(EmailPOJO email) {

		if (email.isReadyForSending()) {
			Connection theConnection = null;
			javax.jms.Session theSession = null;
			MessageProducer theProducer = null;
			try {
				log.info("Mail da inviare: " + email);

				theConnection = connectionFactory.createConnection();
				theSession = theConnection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
				theProducer = theSession.createProducer(emailQueue);
				ObjectMessage theMessage = theSession.createObjectMessage();
				theMessage.setObject(email);
				theProducer.send(theMessage);
				log.info("Mail inserita in coda");
			} catch (Exception inException) {
				log.error("Errore nell'inserimento della mail " + email.getSubject() + " nella coda di invio ",
						inException);
			} finally {
				try {
					theProducer.close();
					theSession.close();
					theConnection.close();
				} catch (NullPointerException | JMSException e) {
					log.error("Errore durante la chiusura delle connessioni", e);
				}

			}
		} else {
			log.warn("Mail non accodata perche' non valida: " + email);
		}
	}

	public Boolean sendRegisterCittadino(String name, String username, String activationLink, String emailCittadino,
			String codProvinciaRegistrazione) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", name);
		map.put("username", username);
		map.put("activationLink", activationLink);
		map.put("mailto", emailCittadino);
		map.put("mailfrom", findEmailProblemiRegistrazione(codProvinciaRegistrazione));

		ArrayList<String> tos = new ArrayList<String>();
		tos.add(emailCittadino);

		EmailPOJO email = new EmailPOJO();
		email.setFrom(emailConstants.getEmailFrom());
		email.setTos(tos);
		email.setSubject("Conferma registrazione nuovo account su " + constantsSingleton.getContext());

		try {
			email.setMessageBody(generateEmail(
					constantsSingleton.getMyAccountLocalURL()
							+ emailConstants.getEmailRegisterCittadino(constantsSingleton.getCodRegione()), map));
			pushToEmailNotificationQueue(email);
		} catch (Exception exception) {
			log.error(exception);
			return false;
		}
		return true;
	}

	public Boolean sendConfirmCittadino(String username, String emailCittadino, String codProvinciaRegistrazione) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("mailfrom", findEmailProblemiRegistrazione(codProvinciaRegistrazione));
		map.put("urlPortale", constantsSingleton.getPortaleURL());

		ArrayList<String> tos = new ArrayList<String>();
		tos.add(emailCittadino);

		EmailPOJO email = new EmailPOJO();
		email.setFrom(emailConstants.getEmailFrom());
		email.setTos(tos);
		email.setSubject("Conferma attivazione account su " + constantsSingleton.getContext());
		email.setMessageBody(generateEmail(
				constantsSingleton.getMyAccountLocalURL()
						+ emailConstants.getEmailConfirmUtente(constantsSingleton.getCodRegione()), map));

		pushToEmailNotificationQueue(email);

		return true;
	}

	public Boolean sendConfirmAzienda(String username, String emailAzienda, String codProvinciaRegistrazione) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("mailfrom", findEmailProblemiRegistrazione(codProvinciaRegistrazione));
		map.put("urlPortale", constantsSingleton.getPortaleURL());

		ArrayList<String> tos = new ArrayList<String>();
		tos.add(emailAzienda);

		EmailPOJO email = new EmailPOJO();
		email.setFrom(emailConstants.getEmailFrom());
		email.setTos(tos);
		email.setSubject("Conferma attivazione account su " + constantsSingleton.getContext());
		email.setMessageBody(generateEmail(
				constantsSingleton.getMyAccountLocalURL()
						+ emailConstants.getEmailConfirmAzienda(constantsSingleton.getCodRegione()), map));

		pushToEmailNotificationQueue(email);

		return true;
	}

	public Boolean sendRegisterAzienda(String name, String username, String activationLink, String emailCittadino,
			String codProvinciaRegistrazione) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", name);
		map.put("username", username);
		map.put("activationLink", activationLink);
		map.put("mailto", emailCittadino);
		map.put("mailfrom", findEmailProblemiRegistrazione(codProvinciaRegistrazione));

		ArrayList<String> tos = new ArrayList<String>();
		tos.add(emailCittadino);

		EmailPOJO email = new EmailPOJO();
		email.setFrom(emailConstants.getEmailFrom());
		email.setTos(tos);
		email.setSubject("Conferma registrazione nuovo account su " + constantsSingleton.getContext());

		try {
			email.setMessageBody(generateEmail(
					constantsSingleton.getMyAccountLocalURL()
							+ emailConstants.getEmailRegisterAzienda(constantsSingleton.getCodRegione()), map));
			pushToEmailNotificationQueue(email);
		} catch (Exception exception) {
			log.error(exception);
			return false;
		}
		return true;
	}

	public Boolean sendRecuperaPassword(String name, String username, String resetLink, String emailTo,
			String codProvinciaRegistrazione) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", name);
		map.put("username", username);
		map.put("resetLink", resetLink);
		map.put("mailto", emailTo);
		map.put("mailfrom", findEmailProblemiRegistrazione(codProvinciaRegistrazione));

		ArrayList<String> tos = new ArrayList<String>();
		tos.add(emailTo);

		EmailPOJO email = new EmailPOJO();
		email.setFrom(emailConstants.getEmailFrom());
		email.setTos(tos);
		email.setSubject("Reset Password di accesso a " + constantsSingleton.getContext());

		try {
			email.setMessageBody(generateEmail(
					constantsSingleton.getMyAccountLocalURL()
							+ emailConstants.getEmailRecuperaPassword(constantsSingleton.getCodRegione()), map));
			pushToEmailNotificationQueue(email);
		} catch (Exception exception) {
			log.error(exception);
			return false;
		}
		return true;
	}

	public Boolean sendEmailValidaAzienda(String name, String username, String password, String activationLink,
			String emailCittadino, String codProvinciaRegistrazione) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", name);
		map.put("username", username);
		map.put("password", password);
		map.put("activationLink", activationLink);
		map.put("mailto", emailCittadino);
		map.put("mailfrom", findEmailProblemiRegistrazione(codProvinciaRegistrazione));

		ArrayList<String> tos = new ArrayList<String>();
		tos.add(emailCittadino);

		EmailPOJO email = new EmailPOJO();
		email.setFrom(emailConstants.getEmailFrom());
		email.setTos(tos);
		email.setSubject("Conferma registrazione nuovo account su " + constantsSingleton.getContext());

		try {
			email.setMessageBody(generateEmail(
					constantsSingleton.getMyAccountLocalURL()
							+ emailConstants.getEmailValidaAzienda(constantsSingleton.getCodRegione()), map));
			pushToEmailNotificationQueue(email);
		} catch (Exception exception) {
			log.error(exception);
			return false;
		}
		return true;
	}

	/**
	 * Manda una mail che avvisa l'utente della ricezione di una notifica su MyPortal.
	 */
	public Boolean sendEmailNotificaRicevuta(String username, String emailAzienda, String codProvinciaRegistrazione) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("mailfrom", findEmailProblemiRegistrazione(codProvinciaRegistrazione));
		map.put("urlPortale", constantsSingleton.getPortaleURL());

		ArrayList<String> tos = new ArrayList<String>();
		tos.add(emailAzienda);

		EmailPOJO email = new EmailPOJO();
		email.setFrom(emailConstants.getEmailFrom());
		email.setTos(tos);
		email.setSubject("Ricevuta notifica da " + constantsSingleton.getContext());
		email.setMessageBody(generateEmail(
				constantsSingleton.getMyAccountLocalURL()
						+ emailConstants.getEmailNotificaRicevuta(constantsSingleton.getCodRegione()), map));

		pushToEmailNotificationQueue(email);
		return true;
	}

	/**
	 * Manda una mail che avvisa un utente che ha appena ricevuto l'abilitazione ai servizi amministrativi.
	 */
	public Boolean sendEmailAccreditamentoForteNoPec(String nomeUtente, String emailUtente,
			String codProvinciaRegistrazione) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", nomeUtente);
		map.put("mailfrom", findEmailProblemiRegistrazione(codProvinciaRegistrazione));
		map.put("urlPortale", constantsSingleton.getPortaleURL());

		ArrayList<String> tos = new ArrayList<String>();
		tos.add(emailUtente);

		EmailPOJO email = new EmailPOJO();
		email.setFrom(emailConstants.getEmailFrom());
		email.setTos(tos);
		email.setSubject("Conferma abilitazione servizi avanzati " + constantsSingleton.getContext());
		email.setMessageBody(generateEmail(
				constantsSingleton.getMyAccountLocalURL()
						+ emailConstants.getEmailAccreditamentoForteNoPec(constantsSingleton.getCodRegione()), map));

		pushToEmailNotificationQueue(email);
		return true;
	}

	/**
	 * Manda una email ad un utente dopo che l'operatore del CPI ha confermato il token di richiesta di abilitazione ai
	 * servizi amministrativi. La mail comprende il token di risposta.
	 */
	public Boolean sendEmailAccreditamentoForteConPec(String nomeUtente, String emailUtente, String tokenRisposta,
			String codProvinciaRegistrazione) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", nomeUtente);
		map.put("mailfrom", findEmailProblemiRegistrazione(codProvinciaRegistrazione));
		map.put("urlPortale", constantsSingleton.getPortaleURL());
		map.put("token", tokenRisposta);

		ArrayList<String> tos = new ArrayList<String>();
		tos.add(emailUtente);

		EmailPOJO email = new EmailPOJO();
		email.setFrom(emailConstants.getEmailFrom());
		email.setTos(tos);
		email.setSubject("Conferma abilitazione servizi avanzati " + constantsSingleton.getContext());
		email.setMessageBody(generateEmail(
				constantsSingleton.getMyAccountLocalURL()
						+ emailConstants.getEmailAccreditamentoForteConPec(constantsSingleton.getCodRegione()), map));

		pushToEmailNotificationQueue(email);
		return true;
	}

	/**
	 * Helper: trova l'indirizzo email da contattare in caso di problemi di registrazione, in base alla provincia.
	 */
	private String findEmailProblemiRegistrazione(String codProvincia) {
		String emailProblemiRegistrazione = null;
		try {
			if (codProvincia != null) {
				DeProvincia provinciaRegistrazione = deProvinciaEJB.findById(codProvincia);
				if (provinciaRegistrazione.getContatti() != null) {
					emailProblemiRegistrazione = provinciaRegistrazione.getContatti().getEmailProblemiRegistrazione();
				}
			}
		} catch (MyCasException e) {
			emailProblemiRegistrazione = null;
		}

		if (emailProblemiRegistrazione != null) {
			return emailProblemiRegistrazione;
		} else {
			return emailConstants.getEmailFrom();
		}
	}
}
