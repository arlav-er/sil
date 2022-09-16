package it.eng.sil.myauthservice.model;

import java.net.URL;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.sil.base.utils.ConstantsBaseCommons;

@Singleton
@Startup
public class ConstantsSingleton extends Properties {
	protected static Log log = LogFactory.getLog(ConstantsSingleton.class);
	private static final long serialVersionUID = 6624047442299329210L;

	public static final String FILE_CONST_PROPS = "/it/eng/sil/myauthservice/resources/const.properties";
	private static final String FILE_COMMON_CONST_PROPS = "/it/eng/sil/myauthservice/resources/common.properties";

	public static final int TOKEN_LENGTH = 24;
	public static final int TOKEN_PASSWORD_LENGTH = 8;

	/**
	 * Serve solo per lo sviluppo LOCALE, dove la regione viene decisa da local.prefix sullo standalone
	 * 
	 * @param file
	 * @return
	 */
	public static String getDevelopmentConstFile(String file) {
		// file per lo sviluppo
		String localPrefix = System.getProperty("local.prefix");

		if (localPrefix == null || "".equals(localPrefix)) {
			return file;//sui sistemi esterni non e` definita
		}
		int lastDot = file.lastIndexOf("/") + 1;
		// ad esempio er.const.properties
		return String.format("%s%s.const.properties", file.substring(0, lastDot), localPrefix);
	}

	/**
	 * Il caricamento delle properties avviene su due file: common e specifico.
	 */
	@PostConstruct
	private void postConstruct() {

		// Si carica prima il file di proprietà comuni
		URL urlPropCommon = Thread.currentThread().getContextClassLoader().getResource(FILE_COMMON_CONST_PROPS);
		try {
			this.load(urlPropCommon.openStream());
		} catch (java.io.IOException e) {
			throw new RuntimeException("GRAVE Impossibile caricare il file " + FILE_COMMON_CONST_PROPS);
		}

		String urlStr = getDevelopmentConstFile(FILE_CONST_PROPS);
		// Si carica dopo il file delle proprietà relativo all'ambiente
		URL urlSpecificheRegione = Thread.currentThread().getContextClassLoader().getResource(urlStr);
		Properties specProps = new Properties();
		try {
			specProps.load(urlSpecificheRegione.openStream());
		} catch (java.io.IOException e) {
			throw new RuntimeException("Impossibile caricare il file " + urlStr);
		} catch (NullPointerException npe) {
			throw new RuntimeException(
					"Impossibile caricare il file " + urlStr + " forse manca local.prefix nello standalone.xml?");

		}
		// si fa la fusione
		this.putAll(specProps);

		log.info("ConstantSingleton completo. Gamification enabled=" + isGamificationEnabled());

	}

	public String getCodRegione() {
		return getProperty("regione.cod");
	}

	public String getNomeRegione() {
		return getProperty("regione.nome");
	}

	public String getBuildVersion() {
		return getProperty("build.version");
	}

	/**
	 * GAMIFICATION URLs
	 * 
	 * @return
	 */

	public String getGamificationBaseURL() {
		return getProperty("gamification.base_url");
	}

	public String getGamificationBadgeListURL() {
		return getGamificationBaseURL() + getProperty("gamification.getbadgelist.endpoint");
	}

	public String getGamificationBadgeURL() {
		return getGamificationBaseURL() + getProperty("gamification.getbadge.endpoint");
	}

	public String getGamificationUserBadgeURL() {
		return getGamificationBaseURL() + getProperty("gamification.getuserbadgelist.endpoint");
	}

	public String getGamificationAssignBadgeURL() {
		return getGamificationBaseURL() + getProperty("gamification.assignbadge.endpoint");
	}

	public String getContext() {
		return getProperty("contesto.app");
	}

	public String getTitle() {
		return getProperty("title.app");
	}

	public Boolean isCalabria() {
		return getCodRegione().equals("" + ConstantsBaseCommons.CALABRIA);
	}

	public Boolean isPuglia() {
		return getCodRegione().equals("" + ConstantsBaseCommons.PUGLIA);
	}

	public Boolean isTrento() {
		return getCodRegione().equals("" + ConstantsBaseCommons.TRENTO);
	}

	public Boolean isEmiliaRomagna() {
		return getCodRegione().equals("" + ConstantsBaseCommons.EMILIA_ROMAGNA);
	}

	public Boolean isUmbria() {
		return getCodRegione().equals("" + ConstantsBaseCommons.UMBRIA);
	}

	public Boolean isVDA() {
		return getCodRegione().equals("" + ConstantsBaseCommons.VDA);
	}

	public Integer getDurataTokenOTP() {
		return Integer.valueOf(getProperty("otp.durata.minuti"));
	}

	public String getOneSignalAppId() {
		return getProperty("onesignal.appId");
	}

	public String getOneSignalAuthorization() {
		return getProperty("onesignal.authorization");
	}

	public String getOneSignalNotificationEndpoint() {
		return getProperty("onesignal.notification.endpoint");
	}
	
	public String getSmsProvider() {
		return getProperty("sms.provider");
	}

	public String getUsernameSms() {
		return getProperty("sms.username");
	}

	public String getPasswordSms() {
		return getProperty("sms.password");
	}

	public String getTokenSms() {
		return getProperty("sms.token");
	}

	public String getAliasSms() {
		return getProperty("sms.alias");
	}

	public String getAuthTokenUrlSms() {
		return getProperty("sms.AUTH_TOKEN_URL");
	}

	public String getPostSmsUrlSms() {
		return getProperty("sms.POST_SMS_URL");
	}

	public Boolean isGamificationEnabled() {
		try {
			String prop = getProperty("gamification.enabled");
			return Boolean.valueOf(prop);
		} catch (Exception e) {
			log.warn("ATTENZIONE ERRORE FILE configurazione (non booleano?): gamification.enabled: " + e);
			return false;
		}
	}

	public String getTitoloNotificaOTP() {
		return getProperty("push.titolo_notifica_otp");
	}

	public String getSottotitoloNotificaOTP() {
		return getProperty("push.sottotitolo_notifica_otp");
	}

	public String getMessaggioNotificaOTP() {
		return getProperty("push.messaggio_notifica_otp");
	}

}
