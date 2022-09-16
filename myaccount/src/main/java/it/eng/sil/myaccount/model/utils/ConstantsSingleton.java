package it.eng.sil.myaccount.model.utils;

import java.net.URL;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.sil.base.enums.OTPRequestEnum;
import it.eng.sil.base.exceptions.GamificationException;
import it.eng.sil.myaccount.model.ejb.stateless.auth.GamificationClientMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.auth.OTPClientEJB;
import it.eng.sil.myaccount.utils.LocalPrefResolver;

@Singleton
@Startup
public class ConstantsSingleton extends Properties {
	protected static Log log = LogFactory.getLog(ConstantsSingleton.class);
	private static final long serialVersionUID = 6624047442299329210L;

	public static final String MYACCOUNT_MAIL_QUEUE = "queue/myaccount_email_queue";

	public static final String FILE_CONST_PROPS = "/it/eng/sil/myaccount/resources/const.properties";
	private static final String FILE_COMMON_CONST_PROPS = "/it/eng/sil/myaccount/resources/common.properties";

	public static final String UI_BASE_BUNDLE = "it.eng.sil.myaccount.resources.ui";
	public static final String UI_VDA_BUNDLE = "it.eng.sil.myaccount.resources.ui_vda";
	public static final String UI_UMBRIA_BUNDLE = "it.eng.sil.myaccount.resources.ui_umbria";
	public static final String UI_PAT_BUNDLE = "it.eng.sil.myaccount.resources.ui_pat";
	public static final String UI_CALABRIA_BUNDLE = "it.eng.sil.myaccount.resources.ui_calabria";
	public static final String CF_OBBLIGATORIO = "false";

	public static final int TOKEN_LENGTH = 24;
	public static final int TOKEN_PASSWORD_LENGTH = 8;

	private Boolean gamificationEnabled;
	private Boolean otpPushEnabled;
	private Boolean otpSmsEnabled;

	@EJB
	GamificationClientMyAccountEJB gamificationEJB;

	@EJB
	OTPClientEJB oTPClientEJB;

	public static class Gamification {
		public static final double BADGE_COMPLETE = 1.0;
		public static final double BADGE_ABSENT = 0.0;
		public static final double BADGE_HALF = 0.5;
	}

	public static class GpRuoloConstants {
		public static final String SUPERUSER_SARE = "Superuser SARE";
		public static final String GESTIONE_CO = "Gestione CO";
		public static final String VISUALIZZAZIONE_CO = "Visualizzazione CO";
		public static final String COD_AUTORIZZAZIONE_SARE_CONCESSA = "4";
	}

	public static class DeRuoloPortaleConstants {
		public static final String CITTADINO = "CITT";
		public static final String AZIENDA = "AZIENDE";
		public static final String PROVINCIA = "PROV";
		public static final String REGIONE = "REGIONI";
		public static final String CPI = "CPI";
		public static final String SOGGETTO_PUBBLICO = "SOGGPUBB";
		public static final String SYSTEM = "SYSTEM";
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
			throw new RuntimeException("Impossibile caricare il file " + FILE_COMMON_CONST_PROPS);
		}

		// Si carica dopo il file delle proprietà relativo all'ambiente
		URL urlSpecificheRegione = Thread.currentThread().getContextClassLoader()
				.getResource(LocalPrefResolver.getCustomizedFile(FILE_CONST_PROPS));
		Properties specProps = new Properties();
		try {
			specProps.load(urlSpecificheRegione.openStream());
		} catch (java.io.IOException e) {
			throw new RuntimeException("Impossibile caricare il file " + FILE_CONST_PROPS);
		} catch (NullPointerException npe) {
			throw new RuntimeException("Impossibile caricare il file " + FILE_CONST_PROPS
					+ " forse manca local.prefix nello standalone.xml?");

		}

		// si fa la fusione
		this.putAll(specProps);

		// Prendo la proprietà "gamificationEnabled" da MyAuthServer
		try {
			gamificationEnabled = gamificationEJB.isGamificationEnabled(getGamificationEnabledURL());
			log.info("ConstantSingleton completo. Gamification enabled=" + isGamificationEnabled());
		} catch (GamificationException e) {
			gamificationEnabled = false;
			log.error("Errore nella isGamificationEnabled del ConstantsSingleton: " + e.toString());
		}
		

		log.info("ConstantSingleton completo. MyAuth local enabled=" + isMyAuthLocalCallMode());
	}

	public String getOTPEnabledURL() {
		return getMyAuthServiceURL() + getProperty("auth.otp.enabled");
	}

	public String getCodRegione() {
		return getProperty("regione.cod");
	}

	public String getNomeRegione() {
		return getProperty("regione.nome");
	}

	public String getAuthenticateURL() {
		return getMyAuthServiceURL() + getProperty("auth.utente.endpoint");
	}

	public String getOTPRequestURL() {
		return getMyAuthServiceURL() + getProperty("auth.otp.request");
	}

	public String getOTPReplyURL() {
		return getMyAuthServiceURL() + getProperty("auth.otp.reply");
	}

	public String getProfileURL() {
		return getMyAuthServiceURL() + getProperty("auth.profilatura.endpoint");
	}

	public String getMyAuthJamonURL() {
		return getMyAuthServiceURL() + getProperty("auth.stats.jamon.endpoint");
	}

	public String getMyAuthCacheStatsURL() {
		return getMyAuthServiceURL() + getProperty("auth.stats.cache.endpoint");
	}

	public String getMyAuthServiceURL() {
		return getProperty("myauth.base_url");
	}

	public String getMyAuthGetInfoURL() {
		return getMyAuthServiceURL() + getProperty("auth.utente.endpoint");
	}

	public String getPortaleURL() {
		return getProperty("myportal.base_url");
	}

	public String getCasURL() {
		return getProperty("mycas.base_url");
	}

	public String getCasLogoutURL() {
		return getCasURL() + "/logout";
	}

	public String getMyAccountURL() {
		return getProperty("myaccount.base_url");
	}

	public String getResURL() {
		return getProperty("res.base_url");
	}

	/**
	 * GAMIFICATION URLs
	 * 
	 * @return
	 */

	public String getGamificationBadgeListURL() {
		return getMyAuthServiceURL() + getProperty("gamification.getbadgelist.endpoint");
	}

	public String getGamificationEnabledURL() {
		return getMyAuthServiceURL() + getProperty("gamification.enabled.endpoint");
	}

	public String getGamificationBadgeURL() {
		return getMyAuthServiceURL() + getProperty("gamification.getbadge.endpoint");
	}

	public String getGamificationUserBadgeURL() {
		return getMyAuthServiceURL() + getProperty("gamification.getuserbadgelist.endpoint");
	}

	public String getGamificationAssignBadgeURL() {
		return getMyAuthServiceURL() + getProperty("gamification.assignbadge.endpoint");
	}

	public String getMyAccountLocalURL() {
		if (getProperty("myaccount.local_url") != null)
			return getProperty("myaccount.local_url");
		else
			return getMyAccountURL();
	}

	/**
	 * Local mode doesn't use http mode
	 * 
	 * @return
	 */
	public boolean isMyAuthLocalCallMode() {
		try {
			String prop = getProperty("auth.local_access");
			return Boolean.valueOf(prop);
		} catch (Exception e) {
			log.warn("OVERRIDE REMOTO per errore configurazione: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Se il SARE è disabilitato (per esempio ora in VDA), non chiamo nessuno dei suoi WS.
	 */
	public boolean isSareDisabled() {
		try {
			String prop = getProperty("sare.disabled");
			if (prop != null)
				return Boolean.valueOf(prop);
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isCfObbligatorio() {
		try {
			String prop = getProperty("cf.obbligatorio");

			if (prop != null && prop.equals(CF_OBBLIGATORIO))
				return false;
			else
				return true;

		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * Se è attiva la creazione automatica di profilature per MySare
	 */
	public boolean isMySareProfilaturaEnabled() {
		try {
			String prop = getProperty("mysare.profilaturaEnabled");
			if (prop != null)
				return Boolean.valueOf(prop);
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	public String getContext() {
		return getProperty("contesto.app");
	}

	public String getTitle() {
		return getProperty("title.app");
	}

	@Deprecated
	// use commons
	public static final String USERNAME_CLICLAVORO_CANDIDATURE = "cliclavoro";
	@Deprecated
	// use commons
	public static final String USERNAME_CLICLAVORO_VACANCY = "az_cliclavoro";

	public static final Integer USERID_ADIMIN = 0;

	public static class DeTipoServizio {
		public static final String SARE = "sare";
	}

	/**
	 * Calcola e inizializza tutto ciò che serve per utilizzare un proxy. Estrae la configurazione dal const.properties
	 * ed inizializza anche un oggetto Proxy pronto per essere utilizzato
	 * 
	 * @author Rodi A.
	 * 
	 */

	public Boolean getCaptchaRequired() {
		return Boolean.parseBoolean(getProperty("captcha.required", "true"));
	}

	public String getNonProxyHosts() {
		return getProperty("proxy.nonProxyHosts");
	}

	public String getProxyAddress() {
		return getProperty("proxy.address");
	}

	public String getProxyPort() {
		return getProperty("proxy.port");
	}

	public String getProxyUsername() {
		return getProperty("proxy.username");
	}

	public String getProxyPassword() {
		return getProperty("proxy.password");
	}

	public Boolean getProxyActive() {
		return Boolean.parseBoolean(getProperty("proxy.active"));
	}

	public Boolean getProxyAuth() {
		return Boolean.parseBoolean(getProperty("proxy.authentication"));
	}

	public Boolean isTrento() {
		return getCodRegione().equals("22");
	}

	public Boolean isEmiliaRomagna() {
		return getCodRegione().equals("8");
	}

	public Boolean isUmbria() {
		return getCodRegione().equals("10");
	}

	public Boolean isCalabria() {
		return getCodRegione().equals("18");
	}

	public Boolean isVDA() {
		return getCodRegione().equals("2");
	}

	public Boolean isPuglia() {
		return getCodRegione().equals("16");
	}

	public String getBuildVersion() {
		return getProperty("build.version");
	}

	/**
	 * check sezione sare visibile
	 * 
	 * @return
	 */
	public Boolean isAbilitatoSare() {
		if (isUmbria()) {
			return true;
		} else if (isTrento()) {
			return true;
		} else if (isEmiliaRomagna()) {
			return true;
		} else if (isVDA()) {
			return true;
		} else if (isCalabria()) {
			return true;
		}

		return false;
	}

	public Boolean isGamificationEnabled() {
		if (gamificationEnabled == null) {
			try {
				gamificationEnabled = gamificationEJB.isGamificationEnabled(getGamificationEnabledURL());
			} catch (GamificationException e) {
				log.error("Errore nella isGamificationEnabled della ConstantsSingleton: " + e.toString());
				gamificationEnabled = false;
			}
		}

		return gamificationEnabled;
	}

	public Boolean isOTPAbilitato(OTPRequestEnum sulCanale) {

		switch (sulCanale) {
		case PUSH:
			//if (otpPushEnabled == null)
				otpPushEnabled = oTPClientEJB.isEnabled(sulCanale);
			return otpPushEnabled;

		case SMS:
			//if (otpSmsEnabled == null)
				otpSmsEnabled = oTPClientEJB.isEnabled(sulCanale);
			return otpSmsEnabled;

		default:
			log.error("GRAVE: canale non previsto: " + sulCanale);
			return false;
		}
	}

	public boolean isPortingNuovoSareEnabled() {
		String prop = getProperty("nuovosare.portingEnabled");
		if (prop != null)
			return Boolean.valueOf(prop);
		else
			return false;
	}

	public boolean isSuperUserNuovoSareEnabled() {
		String prop = getProperty("nuovosare.superuserEnabled");
		if (prop != null)
			return Boolean.valueOf(prop);
		else
			return false;
	}

	/**
	 * Check se la sezione "servizi amministrativi" è visibile (di default, lo è).
	 */
	public Boolean isAbilitatoServAmministrativi() {
		try {
			String prop = getProperty("serviziAmministrativi.abilitato");
			return (prop != null) ? Boolean.valueOf(prop) : true;
		} catch (Exception e) {
			log.error("Errore nella property serviziAmministrativi.abilitato");
			return true;
		}
	}

	public String getEmailFrom() {
		return getProperty("mailFrom.registrazione");
	}

	public String getDefaultPassword() {
		return getProperty("userProfile.defaultPassword");
	}
}
