package it.eng.sil.sms.mdb;

import java.rmi.RemoteException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tim.smashng.fe.ws.services.AeInviaMessaggiERequest;
import com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse;
import com.tim.smashng.fe.ws.services.SmashAe4EndpointProxy;

import bean.webservices.smash.RisultatoInvioBean;
import ejb.webservices.smash.SmashaePortTypeProxy;
import it.eng.sil.rest.tim.RestClient;
import it.eng.sil.rest.tim.RestClientConsip2019;
import it.eng.sil.sms.SmsRetryableException;
import it.infotn.www.AMXSMSService.AMXSMSServiceSVC.InvioSMS_port_typeProxy;
import it.infotn.www.SMS_EAI.InvioSMS_types.SMSMessage_type;
import it.infotn.www.SMS_EAI.InvioSMS_types.SMSResult_type;
import it.infotn.www.SMS_EAI.InvioSMS_types.SMSResult_typeSMS;
import it.infotn.www.SMS_EAI.InvioSMS_types.SMS_type;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/SMSOut") })
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SMSOut implements MessageListener {

	private Log log = LogFactory.getLog(this.getClass());

	public void onMessage(Message message) {
		log.info("SMSOut chiamata su onMessage");
		if (message instanceof MapMessage) {
			try {
				log.info("Message " + message.getJMSMessageID() + " redelivery: " + message.getJMSRedelivered());

				String gateway = ((MapMessage) message).getStringProperty("gateway");

				if ("TIM-CONSIP-2019".equals(gateway)) {
					sendByTimConsip2019(message);
				} else if ("TIM-SSC2".equals(gateway)) {
					sendByTimSSc2(message);
				} else if ("AMXSMS".equals(gateway)) {
					sendByAmx(message);
				} else if ("SMASHAE4".equals(gateway)) {
					sendBySmashAe4(message);
				} else {
					sendByInfoTIM(message);
				}
				return;
			} catch (JMSException e) {
				log.error("Errore JMS. Inserisco l'SMS nella coda dei morti.", e);
			}
		}

	}

	private void sendByAmx(Message message) throws JMSException {
		// recupera i parametri del messaggio
		String testoSMS = ((MapMessage) message).getStringProperty("text");
		String endpoint = ((MapMessage) message).getStringProperty("endpoint");
		String numCell = ((MapMessage) message).getStringProperty("number");
		String sender = ((MapMessage) message).getStringProperty("sender");

		// replace caratteri non validi
		String regExp = "[^A-Za-z0-9 !\",£$%&()/=?\\\\*èéìòàù§_:;'+.\\-@#<>]";
		testoSMS = testoSMS.replaceAll("°", ".");
		testoSMS = testoSMS.replaceAll(regExp, "");

		// proxy
		InvioSMS_port_typeProxy proxy = new InvioSMS_port_typeProxy();
		proxy.setEndpoint(endpoint);

		// sms
		SMSMessage_type sms = new SMSMessage_type();
		sms.setText(testoSMS);

		String[] mobileDest = new String[1];
		mobileDest[0] = numCell;
		sms.setMobileDest(mobileDest);

		SMS_type request = new SMS_type(sender, sms);

		// /////////////////
		// *** INVIO ***
		// /////////////////

		String testo_inviato = null;
		String dest_inviato = null;
		String msg_id = null;

		SMSResult_type result = null;

		try {
			result = proxy.sendSMS(request);
		} catch (it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type ex) {

			String errorCode = ex.getErrorCode();
			String errorDescription = ex.getErrorDescription();

			log.error(String.format("Errore di invio dell'SMS! sender: '%s'testo inviato: '%s' cell: '%s' ", sender,
					testoSMS, numCell));
			log.error(String.format("error_code:'%s' error_description:'%s' ", errorCode, errorDescription));
			return;
		} catch (RemoteException ex) {
			// Eccezione che comporta il tentativo di reinvio del messaggio

			log.error("*** Errore da parte del server durante l'invio dell'SMS.");
			log.error(ex);
			log.error("*** Sender: '" + sender + "', Testo SMS: '" + testoSMS + "', destinatario: " + numCell);

			throw new SmsRetryableException("Errore nell'invio: " + ex.getMessage(), ex);
		}

		// //////////////////////////////////////////////////
		// OK
		// //////////////////////////////////////////////////

		SMSResult_typeSMS sms_inviato = result.getSMS();

		testo_inviato = sms_inviato.getText();
		dest_inviato = sms_inviato.getMobileDest(0);
		msg_id = sms_inviato.getMsgID();

		log.info(String.format("testo inviato:'%s' destinatario:'%s' message_id:'%s' ", testo_inviato, dest_inviato,
				msg_id));

	}

	@Deprecated
	private void sendByInfoTIM(Message message) throws JMSException {
		// recupera i parametri del messaggio
		String testoSMS = ((MapMessage) message).getStringProperty("text");

		// replace caratteri non validi
		String regExp = "[^A-Za-z0-9 !\",£$%&()/=?\\\\*èéìòàù§_:;'+.\\-@#<>]";
		testoSMS = testoSMS.replaceAll("°", ".");
		testoSMS = testoSMS.replaceAll(regExp, "");

		String endpoint = ((MapMessage) message).getStringProperty("endpoint");
		String numCell = ((MapMessage) message).getStringProperty("number");
		String contractCode = ((MapMessage) message).getStringProperty("contractCode");
		String msisdnRef = ((MapMessage) message).getStringProperty("msisdnRef");
		String tipoRef = ((MapMessage) message).getStringProperty("tipoRef");
		String nomeRef = ((MapMessage) message).getStringProperty("nomeRef");
		String cognomeRef = ((MapMessage) message).getStringProperty("cognomeRef");
		String passwordRef = ((MapMessage) message).getStringProperty("passwordRef");
		SmashaePortTypeProxy proxy = new SmashaePortTypeProxy();
		proxy.setEndpoint(endpoint);

		try {
			// proxy.invioSingoloSMS("1", testoSMS, numCell, false); //invia al
			// web-service
			RisultatoInvioBean ret = proxy.invioSpot(testoSMS, numCell, contractCode, msisdnRef, tipoRef, nomeRef,
					cognomeRef, passwordRef);

			// se l'esito e' OK ho finito
			if ("OK".equals(ret.getEsito())) {
				log.info("SMS inviato correttamente.");
			}
			// altrimenti lo emtto tra i morti
			else {
				log.error("Errore da parte del server durante l'invio dell'SMS.");
				log.error("Esito:" + ret.getEsito() + " , Messaggio: " + ret.getMessaggio());
				// log.error("Inserisco l'SMS nella coda dei morti.");
				// putInDeath(message, "queue/DeathSMS");
			}
		} catch (RemoteException e) {
			// Eccezione che comporta il tentativo di reinvio del messaggio

			log.error("*** Errore da parte del server durante l'invio dell'SMS.");
			log.error(e);
			log.error("*** Testo SMS: '" + testoSMS + "', destinatario: " + numCell);

			throw new SmsRetryableException("Errore nell'invio: " + e.getMessage(), e);
		}
	}

	private void sendBySmashAe4(Message message) throws JMSException {
		// recupera i parametri del messaggio
		String testoSMS = ((MapMessage) message).getStringProperty("text");

		// replace caratteri non validi
		String regExp = "[^A-Za-z0-9 !\",£$%&()/=?\\\\*èéìòàù§_:;'+.\\-@#<>]";
		testoSMS = testoSMS.replaceAll("°", ".");
		testoSMS = testoSMS.replaceAll(regExp, "");

		String endpoint = ((MapMessage) message).getStringProperty("endpoint");
		String numCell = ((MapMessage) message).getStringProperty("number");
		String contractCode = ((MapMessage) message).getStringProperty("contractCode");

		String username = ((MapMessage) message).getStringProperty("username");
		String password = ((MapMessage) message).getStringProperty("password");

		SmashAe4EndpointProxy proxy = new SmashAe4EndpointProxy();
		proxy.setEndpoint(endpoint);

		try {
			String[] destinatari = new String[1];
			destinatari[0] = numCell;

			AeInviaMessaggiERequest req = new AeInviaMessaggiERequest(contractCode, password, username, destinatari, "",
					"10", "", testoSMS);
			AeInviaMessaggiResponse response = proxy.inviaMessaggiMittenteTestuale(req);

			// se l'esito e' OK ho finito
			if (response.getErrorCode() == 0) {
				log.info("SMS inviato correttamente.");
			} else {
				log.error("*** Errore da parte del server durante l'invio dell'SMS.");
				log.error(
						"*** ErrorCode: " + response.getErrorDetail() + ", ErrorDetails: " + response.getErrorDetail());
				log.error("*** Testo SMS: '" + testoSMS + "', destinatario: " + numCell);

			}
		} catch (RemoteException e) {
			// Eccezione che comporta il tentativo di reinvio del messaggio

			log.error("*** Errore da parte del server durante l'invio dell'SMS.");
			log.error(e);
			log.error("*** Testo SMS: '" + testoSMS + "', destinatario: " + numCell);

			throw new SmsRetryableException("Errore nell'invio: " + e.getMessage(), e);
		}
	}

	private void sendByTimSSc2(Message message) throws JMSException {
		// recupera i parametri del messaggio
		String testoSMS = ((MapMessage) message).getStringProperty("text");
		String numCell = ((MapMessage) message).getStringProperty("number");

		// replace caratteri non validi
		String regExp = "[^A-Za-z0-9 !\",£$%&()/=?\\\\*èéìòàù§_:;'+.\\-@#<>]";
		testoSMS = testoSMS.replaceAll("°", ".");
		testoSMS = testoSMS.replaceAll(regExp, "");

		String username = ((MapMessage) message).getStringProperty("username");
		String password = ((MapMessage) message).getStringProperty("password");
		String baseUrl = ((MapMessage) message).getStringProperty("baseUrl");
		String token = ((MapMessage) message).getStringProperty("token");
		String alias = ((MapMessage) message).getStringProperty("alias");

		RestClient restClient = new RestClient();

		try {
			restClient.setBaseUrl(baseUrl);
			restClient.setUsername(username);
			restClient.setPassword(password);
			restClient.setToken(token);
			restClient.setAlias(alias);

			restClient.sendSms(numCell, testoSMS);
			log.info("SMS inviato correttamente.");

		} catch (SmsRetryableException e) {
			log.error("*** Errore da parte del server durante l'invio dell'SMS.");
			log.error(e);
			log.error("*** Testo SMS: '" + testoSMS + "', destinatario: " + numCell);

			throw e;
		}
	}

	private void sendByTimConsip2019(Message message) throws JMSException {
		// recupera i parametri del messaggio
		String testoSMS = ((MapMessage) message).getStringProperty("text");
		String numCell = ((MapMessage) message).getStringProperty("number");

		// replace caratteri non validi
		String regExp = "[^A-Za-z0-9 !\",£$%&()/=?\\\\*èéìòàù§_:;'+.\\-@#<>]";
		testoSMS = testoSMS.replaceAll("°", ".");
		testoSMS = testoSMS.replaceAll(regExp, "");

		String username = ((MapMessage) message).getStringProperty("username");
		String password = ((MapMessage) message).getStringProperty("password");
		String token = ((MapMessage) message).getStringProperty("token");
		String alias = ((MapMessage) message).getStringProperty("alias");

		RestClientConsip2019 restClient = new RestClientConsip2019();

		try {
			restClient.setUsername(username);
			restClient.setPassword(password);
			restClient.setToken(token);
			restClient.setAlias(alias);

			restClient.sendSms(numCell, testoSMS);
			log.info(String.format("SMS inviato correttaemnte a TIM. [cell: '%s', Testo: '%s']", numCell, testoSMS));

		} catch (SmsRetryableException e) {
			log.error("*** Errore da parte del server durante l'invio dell'SMS.");
			log.error(e);
			log.error("*** Testo SMS: '" + testoSMS + "', destinatario: " + numCell);

			throw e;
		}
	}

}
