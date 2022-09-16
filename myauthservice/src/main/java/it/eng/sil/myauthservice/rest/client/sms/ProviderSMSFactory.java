package it.eng.sil.myauthservice.rest.client.sms;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import it.eng.sil.myauthservice.model.ConstantsSingleton;

/**
 * Torna client REST o SOAP a seconda della regione
 * 
 * @author Ale
 *
 */
@Stateless
public class ProviderSMSFactory {

	protected final Logger log = Logger.getLogger(ProviderSMSFactory.class.getName());
	@EJB
	ConstantsSingleton constantsSingleton;

	public ProviderSms getProviderSMS() {

		switch ((String) constantsSingleton.get("sms.provider")) {

		case "tim-rest-2017":// ER
		{
			String prefix = "sms.provider." + "tim-rest-2017.";

			String baseUrl = (String) constantsSingleton.get(prefix + "BASE_URL");
			String username = (String) constantsSingleton.get(prefix + "USERNAME");
			String password = (String) constantsSingleton.get(prefix + "PASSWORD");
			String token = (String) constantsSingleton.get(prefix + "TOKEN");
			String alias = (String) constantsSingleton.get(prefix + "ALIAS");

			ClientRestTIM2017 restClient;

			restClient = new ClientRestTIM2017();

			restClient.setUsername(username);
			restClient.setPassword(password);
			restClient.setToken(token);
			restClient.setAlias(alias);
			restClient.setBaseUrl(baseUrl);

			return restClient;
		}

		case "consip-rest-2019":
		// umbria e calabria
		{
			String prefix = "sms.provider." + "consip-rest-2019.";

			String token = (String) constantsSingleton.get(prefix + "TOKEN");
			String username = (String) constantsSingleton.get(prefix + "USERNAME");
			String password = (String) constantsSingleton.get(prefix + "PASSWORD");
			String alias = (String) constantsSingleton.get(prefix + "ALIAS");

			ClientRestConsip2019 restClient;

			restClient = new ClientRestConsip2019();

			restClient.setUsername(username);
			restClient.setPassword(password);
			restClient.setToken(token);
			restClient.setAlias(alias);

			return restClient;
		}
		case "amxsms":// PAT
		{
			String pre = "sms.provider." + "amxsms.";
			String postSms = (String) constantsSingleton.get(pre + "postSmsUrl");
			String sender = (String) constantsSingleton.get(pre + "sender");
			String username = (String) constantsSingleton.get(pre + "username");
			String password = (String) constantsSingleton.get(pre + "password");
			
			ClientSoapAmx tnCli = new ClientSoapAmx(postSms, sender, username, password);
			return tnCli;
		}

		case "vdasms":// VDA
			// TODO

			break;

		default:
			log.log(Level.SEVERE, "GRAVE: PROVIDER SMS NON CONFIGURATO");
		}

		throw new UnsupportedOperationException(
				"Nessun provider per invio SMS definito, regione= " + constantsSingleton.getCodRegione());
	}
}
