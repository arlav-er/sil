package it.eng.sil.myauthservice.model.ejb.business;

import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sil.myauthservice.model.ejb.business.sms.Sms;
import it.eng.sil.myauthservice.model.ejb.business.sms.SmsException;
import it.eng.sil.myauthservice.rest.client.sms.ProviderSMSFactory;
import it.eng.sil.myauthservice.rest.client.sms.ProviderSms;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.manager.profilatura.PfPrincipalEJB;

/**
 * Session Bean implementation class CreaRichiestaOTP
 */
@Stateless
@LocalBean
public class SMSNotificationEJB {
	protected static Logger log = Logger.getLogger(SMSNotificationEJB.class.getName());
	
	@EJB
	ProviderSMSFactory providerSMSFactory;
	
	@EJB
	PfPrincipalEJB pfPrincipalHome;

	public String send(Integer principalId, Sms sms) throws SmsException, MyCasNoResultException {
		// per eventuali profilature del messaggio
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		
		Utils.validaPfPrincipalDest(pfPrincipal);

		String result = "";
		String testoSMS = sms.getText();
		String numCell = normalizzaNumCell(sms.getCellNumber());

		// replace caratteri non validi
		String regExp = "[^A-Za-z0-9 !\",£$%&()/=?\\\\*èéìòàù§_:;'+.\\-@#<>]";
		testoSMS = testoSMS.replaceAll("°", ".");
		testoSMS = testoSMS.replaceAll(regExp, "");

	  
		
		ProviderSms smsProvider = providerSMSFactory.getProviderSMS( );
		smsProvider.sendSms(numCell, testoSMS);

		

		//result = restClient.sendSms(numCell, testoSMS);
		log.info(String.format("SMS per OTP inviato correttaemnte a TIM. [cell: '%s', Testo: '%s']", numCell, testoSMS));
		return result;
	}

	/**
	 * La funzione di normalizzazione sostituisce il carattere '00' con '+'
	 * 
	 * @param cell
	 *            numero non normalizzato
	 * @return numero normalizzato
	 */
	private String normalizzaNumCell(String cell) {
		if (cell.startsWith("00"))
			return "+" + cell.substring(2);

		if (cell.startsWith("+"))
			return cell;

		return "+39" + cell;

	}

}
