package it.eng.sil.myaccount.model.utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Singleton
@Startup
public class StartupSingleton {
	
	protected static Log log = LogFactory.getLog(StartupSingleton.class);
	
	@EJB
	ConstantsSingleton constantsSingleton;
	
	
	@PostConstruct
	public void postConstruct() {
		setupProxy();
	}

	private void setupProxy() {
		if (constantsSingleton.getProxyActive()) {
			
			log.info( String.format("Per l'accesso ad Internet si usa un proxy server: %s:%s", 
											constantsSingleton.getProxyAddress(), 
											constantsSingleton.getProxyPort()));
			
			if (constantsSingleton.getProxyAuth()) {
				
				log.info( String.format("Il proxy server necessita di autenticazione: %s:%s", 
											constantsSingleton.getProxyUsername(), 
											"*******"));
				
				Authenticator.setDefault(new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(constantsSingleton.getProxyUsername(),
								constantsSingleton.getProxyPassword().toCharArray());
					}
				});

				System.setProperty("http.proxyUser", constantsSingleton.getProxyUsername());
				System.setProperty("http.proxyPassword", constantsSingleton.getProxyPassword());
				System.setProperty("https.proxyUser", constantsSingleton.getProxyUsername());
				System.setProperty("https.proxyPassword", constantsSingleton.getProxyPassword());
			}
			System.setProperty("http.proxyHost", constantsSingleton.getProxyAddress());
			System.setProperty("http.proxyPort", constantsSingleton.getProxyPort());
			System.setProperty("https.proxyHost", constantsSingleton.getProxyAddress());
			System.setProperty("https.proxyPort", constantsSingleton.getProxyPort());

			if (constantsSingleton.getNonProxyHosts()!=null){
				log.info( String.format("Non si passa per il proxy server per i seguenti host: %s", 
						constantsSingleton.getNonProxyHosts()));

				System.setProperty("http.nonProxyHosts", constantsSingleton.getNonProxyHosts());
				System.setProperty("https.nonProxyHosts", constantsSingleton.getNonProxyHosts());
			}

		}else{
			log.info("Nessun proxy server impostato per l'accesso ad Internet" );
		}
	}
}