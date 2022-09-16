package it.eng.sil.myauthservice.rest.client.sms;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.sil.myauthservice.model.ejb.business.sms.SmsException;
import it.eng.sil.myauthservice.rest.client.sms.soap.InvioSMSPortType;
import it.eng.sil.myauthservice.rest.client.sms.soap.SMSMessageType;
import it.eng.sil.myauthservice.rest.client.sms.soap.SMSResultType;
import it.eng.sil.myauthservice.rest.client.sms.soap.SMSServiceServiceagent;
import it.eng.sil.myauthservice.rest.client.sms.soap.SMSType;
import it.eng.sil.myauthservice.rest.client.sms.soap.SendSMSFaultMessage;

/**
 * 
 * Provider SMS SOAP in uso per esempio a trento
 * 
 * @author Ale
 *
 */
public class ClientSoapAmx implements ProviderSms {
	private static Log log = LogFactory.getLog(ClientSoapAmx.class);

	public ClientSoapAmx(String endpoint, String sender, String username, String password) {
		super();
		this.endpoint = endpoint;
		this.sender = sender;
		this.username = username;
		this.password = password;
	}

	private String endpoint;
	private String sender;
	private String username;
	private String password;
	
	@Override
	public void sendSms(String cellNumb, String msg) throws SmsException {
		try {
			SMSServiceServiceagent ss = new SMSServiceServiceagent();
			InvioSMSPortType port = ss.getEndpoint1();

			BindingProvider bp = (BindingProvider) port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
			log.debug("Endpoint impostato: " + endpoint);
			SMSType _sendSMS_request = new SMSType();

			SMSMessageType value = new SMSMessageType();
			value.setText(msg);
			value.getMobileDest().add(cellNumb);

			_sendSMS_request.setSender(this.sender);

			_sendSMS_request.setSMS(value);

			SMSResultType _sendSMS__return = port.sendSMS(_sendSMS_request);
			log.info("Invio sendSMS.result=" + _sendSMS__return);

		} catch (SendSMSFaultMessage e) {
			log.error("sendSms() in ClientSoapAmx Expected exception: SendSMSFaultMessage has occurred.");
			throw new SmsException(e);
		} catch (WebServiceException we) {
			// spiego meglio, questo capita innestato
			String errorDesc = "";
			if (we.getCause() != null) {
				errorDesc += we.getCause().getMessage();
				if (we.getCause().getCause() != null)
					errorDesc += we.getCause().getCause().getMessage();
			}
			errorDesc += we.getMessage();
			log.error("sendSms() in ClientSoapAmx ERRORE CONFIGURAZIONE? " + errorDesc);
			throw new SmsException(errorDesc);
		} catch (Exception e) {
			log.error("ERRORE GENERICO sendSms() in ClientSoapAmx:" + e.getMessage());
			throw new SmsException(e);
		}
	}

}
