package it.eng.myportal.utils;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.sil.base.utils.StringUtils;

/**
 * Singleton contenente le costanti del progetto e fornisce l'accesso a quelle del file const.properties
 * 
 * @author Rodi A.
 * 
 */

public final class ConstantsSingleton {

	/**
	 * Log di classe
	 */
	private static final Log log = LogFactory.getLog(ConstantsSingleton.class);

	/**
	 * Valori costanti caricati
	 */
	private static Properties values = new Properties();

	/**
	 * Il singleton
	 */
	private static ConstantsSingleton singleton = new ConstantsSingleton();

	public static final String HOME_REGIONE = "/faces/secure/regione/home.xhtml";

	public static final String HOME_CERTIFICATORE = "/faces/secure/certificatore/home.xhtml";

	public static final String HOME_PROVINCIA = "/faces/secure/provincia/home.xhtml";

	public static final String HOME_UTENTE = "/faces/secure/utente/home.xhtml";

	public static final String HOME_AZIENDA = "/faces/secure/azienda/home.xhtml";

	public static final String HOME_CONDIVISA = "/faces/secure/home.xhtml";

	public static final String HOME_ADMIN = "/faces/secure/amministrazione/index.xhtml";

	public static final String HOME_PATRONATO = "/faces/secure/patronato/home.xhtml";

	public static final String HOME_SOGGETTO_PUBBLICO = "/faces/secure/soggetto_pubblico/home.xhtml";

	public static final Integer TITLE_DESCRIPTION_MAX_LENGTH = 20;

	public static final String EDIT_PF_CV_IDO = "/faces/secure/utente/curriculum/edit_pf.xhtml";
	public static final String VIEW_PF_CV_IDO = "/faces/secure/utente/curriculum/view_pf.xhtml";

	public static final String EDIT_PF_VA_IDO = "/faces/secure/azienda/vacancies/edit_pf.xhtml";
	public static final String VIEW_PF_VA_IDO = "/faces/secure/azienda/vacancies/view_pf.xhtml";

	// Data scadenza pubblicazione, non può eccedere un numero di 30 giorni solo per PAT; per gli altri ambienti è 60
	public static final String VACANCIES_PAT_MAX_SCADENZA = "30";
	public static final String VACANCIES_MAX_SCADENZA = "60";
	public static final String VACANCIES_MAX_SCADENZAIDO = "15";
	public static final String REPORT_DIR = "REPORT_DIR";
	public final static String RESP_RISPOSTA = "Risposta";
	public final static String RESP_ESITO = "Esito";
	public final static String RESP_CODICE = "codice";
	public final static String RESP_DESCRIZIONE = "Descrizione";
	public final static String RESP_ELEMENT = "element";
	public final static String RESP_DATADA = "DataDa";
	public final static String RESP_DATAA = "DataA";
	public final static String RESP_TIPO = "Tipo";
	public final static String RESP_DESCRIZIONE_ESITO = "descrizione";
	public final static int ESITO_POSITIVO = 0;

	public final static String CHECK_ACCETT_COND_SERV = "Accettazione condizioni di servizio";

	private ConstantsSingleton() {
		loadConfig();
	}

	/**
	 * 
	 * @author vuoto costanti usate per l'app.
	 */

	public static class App {
		public static final String KEY = get("app.key");
		public static final String APP_TOKEN_UTENTE_AES_KEY = get("app.token.utente.aes.key");

		// Configurazioni notifiche OneSignal
		public static final String ONESIGNAL_NOTIFICATION_URL = get("onesignal.notification.endpoint");
		public static final String ONESIGNAL_APP_ID = get("onesignal.appId");
		public static final String ONESIGNAL_AUTHORIZATION = get("onesignal.authorization");
		public static final String ONESIGNAL_NOTIFICATION_DELIVERY_TIME_OF_DAY_BATCH = get(
				"onesignal.notification.delivery_time_of_day.batch");
		public static final String ONESIGNAL_NOTIFICATION_DELIVERY_TIME_OF_DAY = get(
				"onesignal.notification.delivery_time_of_day");

		public static final String MSG_RICHIESTA_ASSISTENZA = ConstantsSingleton.MsgMessaggio.ESPERTO;
		public static final String MSG_RICHIESTA_ASSISTENZA_OGGETTO_KEY = "[App]";
		public static final String MSG_RICHIESTA_ASSISTENZA_OGGETTO = MSG_RICHIESTA_ASSISTENZA_OGGETTO_KEY
				+ " Richiesta di assistenza";

		public static final Boolean CRUSCOTTO_MYPORTAL_VISIBLE = getBoolean("app.cruscotto.myportal.visible");
		/*
		 * Messaggi notifica informativa e OTP patto
		 */
		public static final String PATTO_INFORMA_TITOLO = get("app.patto.informa.titolo");
		public static final String PATTO_INFORMA_SOTTOTITOLO = get("app.patto.informa.sottotitolo");
		public static final String PATTO_INFORMA_MSG = get("app.patto.informa.messaggio");

		public static final String PATTO_OTP_TITOLO = get("app.patto.otp.titolo");
		public static final String PATTO_OTP_SOTTOTITOLO = get("app.patto.otp.sottotitolo");
		public static final String PATTO_OTP_MSG = get("app.patto.otp.messaggio");
		public static final Boolean NUOVO_IDO = getBoolean("app.nuovoIdo");
	}

	public static class Kiosk {
		public static final String KEY = get("kiosk.key");
	}

	/**
	 * Calcola e inizializza tutto ciò che serve per utilizzare un proxy. Estrae la configurazione dal const.properties
	 * ed inizializza anche un oggetto Proxy pronto per essere utilizzato
	 * 
	 * @author Rodi A.
	 * 
	 */

	public static final class Proxy {
		public static final String ADDRESS = get("proxy.address");
		public static final Integer PORT = getInt("proxy.port");
		public static final String USERNAME = get("proxy.username");
		public static final Boolean ACTIVE = getBoolean("proxy.active");
		public static final char[] PASSWORD = get("proxy.password").toCharArray();
		public static final Boolean USE_AUTHENTICATION = getBoolean("proxy.authentication");
		public static final java.net.Proxy PROXY = new java.net.Proxy(java.net.Proxy.Type.HTTP,
				new InetSocketAddress(ADDRESS, PORT));
		static {
			if (USE_AUTHENTICATION) {
				Authenticator.setDefault(new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(USERNAME, PASSWORD);
					}
				});
			}
		}

	}

	/**
	 * Ricarica le costanti definite su const.properties
	 */
	public static void reload() {
		singleton.loadConfig();
		log.debug("Riconfigurazione eseguita!");
	}

	/**
	 * Carica le costanti
	 */
	private void loadConfig() {
		URL urlCommon = Thread.currentThread().getContextClassLoader().getResource("conf/common.properties");
		URL url = Thread.currentThread().getContextClassLoader().getResource("conf/const.properties");
		try {
			ConstantsSingleton.values.load(urlCommon.openStream());
			ConstantsSingleton.values.load(url.openStream());
		} catch (java.io.IOException e) {
			log.fatal("Errore durante l'inizializzazione del ConstantsSingleton. Impossibile avviare l'applicazione.",
					e);
		}

		// TODO eseguire UPDATE su MYCAS.DE_SISTEMA_MYCAS.VERSIONE_POM passando getBuildVersion()
	}

	private static String get(String key) {
		String ret = (String) ConstantsSingleton.values.get(key);
		return ret;
	}

	private static Integer getInt(String key) {
		String ret = (String) ConstantsSingleton.values.get(key);
		return Integer.parseInt(ret);
	}

	private static Boolean getBoolean(String key) {
		String ret = (String) ConstantsSingleton.values.get(key);
		return Boolean.parseBoolean(ret);
	}

	public static String getBuildVersion() {
		return get("build.version");
	}

	public static final String JNDI_BASE = "java:module/";

	public static final int DEFAULT_BUFFER_SIZE = 20096;
	public static final int TOKEN_LENGTH = 24;
	public static final int TOKEN_PASSWORD_LENGTH = 8;

	public static final String TMP_DIR = System.getProperty("java.io.tmpdir");

	/**
	 * Definisce il formato della data dopo la conversione
	 */
	public static final String DATE_FORMAT_AFTER_CONVERSION = "EEE MMM dd HH:mm:ss zzz yyyy";

	/**
	 * Definisce il formato della data prima della conversione
	 */
	public static final String DATE_FORMAT_BEFORE_CONVERSION = "dd/MM/yyyy";

	public static final int DEFAULT_BUCKET_SIZE = 10;

	public static final String STILE_CSS_DEFAULT = "er-lavoro";
	public static final String STILE_CSS_MYPORTAL = "myportal";
	public static final String STILE_CSS_VDA = "vda";
	public static final String STILE_CSS_UMBRIA = "umbria";
	public static final String STILE_CSS_CALABRIA = "calabria";

	public static class FileUpload {
		public static final String FILE_NAME = "file_name";
		public static final String ORIGINAL_FILE_NAME = "original_file_name";

		public static final String STATUS_CODE = "statusCode";

		public static final int MAX_FOTO_SIZE = (1024 * 500);
		public static final int MAX_ATTACHMENT_SIZE = (5 * 1024 * 1024);
		public static final String ERROR_REASON = "error_code";
		public static final String ATTACHMENT_ID = "attachment_id";
	}

	/**
	 * Costanti che si riferiscono alle immagini. Foto cittadino e logo azienda
	 * 
	 * @author enrico
	 * 
	 */
	public static class Img {
		public static final Integer TYPE_FOTO = Integer.parseInt(get("img.Type.Foto"));
		public static final Integer TYPE_LOGO = Integer.parseInt(get("img.Type.Logo"));

		public static final String IMG_SMALL = "SMALL";
		public static final String IMG_MEDIUM = "MEDIUM";
		public static final String IMG_NORMAL = "NORMAL";

		public static final Integer IMG_PX_NORMAL = 200;
		public static final Integer IMG_PX_MEDIUM = 150;
		public static final Integer IMG_PX_SMALL = 30;

		/* foto di default dell'utente se non ne ha inviata una */
		public static final String DEFAULT_IMG_UTENTE = "/resources/images/Img_CV_Default.png";

		/* foto di default dell'azienda se non ne ha inviata una */
		public static final String DEFAULT_IMG_AZIENDA = "/resources/images/blank_icon.png";
	}

	/**
	 * Definisce il numero massimo dei Curriculum da inserire
	 */
	public static final int CVMAXCURR = getInt("curriculum.max");
	final public static int CURR_YEAR = Calendar.getInstance().get(Calendar.YEAR);

	/**
	 * Definisce il numero di candidature da visualizzare nella home page dell'utente
	 */
	public static final int ACULTIMECANDIDATURE = getInt("candidature.ultime");

	/**
	 * Definisce il numero massimo delle lettere di accompagnamento da inserire
	 */
	public static final int LETTEREMAXCURR = getInt("lettere.max");

	/**
	 * Indirizzo URL di base
	 */
	public static final String BASE_URL = get("system.base_url");

	/**
	 * 
	 * @author Rodi A. Costanti per i valori dei campi 'opz_' su DB
	 * 
	 */
	public final static String SI = "Y";
	public final static String NO = "N";
	public final static String INDIFFERENTE = "I";
	public final static String PREFERIBILE = "P";

	/**
	 * Proprietà server SMTP
	 */
	public static final String EMAIL_FROM = get("mail.registrazione");
	// l'invio email dalla lista dei candidati c'è solo nel nuovo ido rer
	public static final String CANDIDATO_FROM = StringUtils.isEmptyNoBlank(get("mail.from.contattaCandidato"))
			? get("mail.registrazione")
			: get("mail.from.contattaCandidato");
	public static final String EMAIL_FOOTER_REGISTER = get("mail.footer_registrazione");
	public static final String EMAIL_ERR_DEBUG = get("mail.err.debug");

	/*
	 * public static final String SMTP_HOST = get("mail.smtp_host"); public static final String SMTP_USER =
	 * get("mail.user_name"); public static final String SMTP_PROT_HOST = get("mail.protocol_host"); public static final
	 * String SMTP_PROT_USER = get("mail.protocol_user");
	 */

	public static final int MIN_YEAR = 1900;

	public static final Integer MAX_NUM_TENTATIVI = getInt("login.max_num_tentativi");
	public static final Integer WAIT_SECONDS = getInt("login.wait_seconds");

	public static final Integer COD_REGIONE = getInt("regione.cod");
	public static final Integer COD_REGIONE_MIN = getInt("regione.cod_min");
	public static final Integer COD_REGIONE_RER = 8;
	public static final Integer COD_REGIONE_UMBRIA = 10;
	public static final Integer COD_REGIONE_TRENTO = 22;
	public static final Integer COD_REGIONE_PUGLIA = 16;
	public static final Integer COD_REGIONE_VDA = 2;
	public static final Integer COD_REGIONE_CALABRIA = 18;
	public static final String DESC_REGIONE = get("regione.descrizione");

	public static String COD_PROVINCIA_PERUGIA = "54";
	public static String COD_PROVINCIA_TERNI = "55";

	public final static String opzValue(String codice) {
		if (SI.equals(codice)) {
			return "Si";
		} else if (NO.equals(codice)) {
			return "No";
		} else if (INDIFFERENTE.equals(codice)) {
			return "Indifferente";
		} else if (PREFERIBILE.equals(codice)) {
			return "Preferibile";
		}
		return "Codice non valido";
	}

	public final static boolean isYgStoricoEnabled() {
		String ygStoricoEnabled = get("yg.storicoEnabled");
		if (ygStoricoEnabled != null && !ygStoricoEnabled.isEmpty()) {
			return Boolean.parseBoolean(ygStoricoEnabled);
		} else {
			return false;
		}
	}

	public static class InputText {
		public static final int MAXLENGTH = getInt("inputText.maxLength");
		public static final int SIZE = getInt("inputText.size");
	}

	public static class SvSezione {
		public static final String SEZ_OGNISEZIONE = get("vetrina.sezione.ogniSezione");
		public static final String SEZ_CHISIAMO = get("vetrina.sezione.chiSiamo");
		public static final String SEZ_DOVESIAMO = get("vetrina.sezione.doveSiamo");
		public static final String SEZ_MISSION = get("vetrina.sezione.mission");
		public static final String SEZ_STORIA = get("vetrina.sezione.storia");
		public static final String SEZ_PROFILIRICHIESTI = get("vetrina.sezione.profiliRichiesti");
		public static final String SEZ_FORMAZCRESCITA = get("vetrina.sezione.formazioneCrescita");
	}

	public static class Register {
		public static final String FLGNONATTIVO = "N";
		public static final String FLGATTIVO = "S";
		public static final Integer ACTIVATION_ERROR = -1;
		public static final Integer USER_ACTIVATED = getInt("registration.userActivated");
		public static final Integer USER_NOT_FOUND = getInt("registration.userNotFound");
		public static final Integer USER_ALREADY_ACTIVATED = getInt("registration.userAlreadyActivated");
		public static final Integer WRONG_TOKEN = getInt("registration.wrongToken");
		public static final Integer WRONG_EMAIL = getInt("registration.wrongEmail");
		public static final String CAPTCHA_PUBLIC_KEY = get("recaptcha.publicKey");
		public static final String CAPTCHA_PRIVATE_KEY = get("recaptcha.privateKey");

	}

	public static class Candidatura {
		public static final String TXT_MSG_INVIA_CANDIDATURA = get("candidatura.testMessaggio");
	}

	public static class RvRicercaVacancy {
		public static final String MANSIONE = get("rvRicercaVacancy.mansione");
		public static final String CONTRATTO = get("rvRicercaVacancy.contratto");
		public static final String ORARIO = get("rvRicercaVacancy.orario");
		public static final String ESPERIENZA = get("rvRicercaVacancy.esperienza");
		public static final String SETTORE = get("rvRicercaVacancy.settore");
		public static final String LINGUA = get("rvRicercaVacancy.lingua");
		public static final String TITOLO_STUDIO = get("rvRicercaVacancy.titoloStudio");
		public static final String DISP_TRASFERTE = get("rvRicercaVacancy.dispTrasferte");
		public static final String PATENTE = get("rvRicercaVacancy.patente");

		public static final String MANSIONEISTAT = get("rvRicercaVacancy.mansioneistat");
		public static final String CONTRATTOSIL = get("rvRicercaVacancy.contrattosil");
		public static final String ORARIOSILIDO = get("rvRicercaVacancy.orariosilido");
		public static final String PATENTESIL = get("rvRicercaVacancy.patentesil");

		public static final String CODMANSIONE = get("rvRicercaVacancy.codmansione");
		public static final String CODCONTRATTO = get("rvRicercaVacancy.codcontratto");
		public static final String CODORARIO = get("rvRicercaVacancy.codorario");
		public static final String CODESPERIENZA = get("rvRicercaVacancy.codesperienza");
		public static final String CODSETTORE = get("rvRicercaVacancy.codsettore");
		public static final String CODLINGUA = get("rvRicercaVacancy.codlingua");
		public static final String CODTITOLO_STUDIO = get("rvRicercaVacancy.codtitoloStudio");
		public static final String CODDISP_TRASFERTE = get("rvRicercaVacancy.coddispTrasferte");
		public static final String CODPATENTE = get("rvRicercaVacancy.codpatente");

		public static final String CODMANSIONEISTAT = get("rvRicercaVacancy.codmansioneistat");
		public static final String CODCONTRATTOSIL = get("rvRicercaVacancy.codcontrattosil");
		public static final String CODORARIOSIL = get("rvRicercaVacancy.codorariosil");
		public static final String CODPATENTESIL = get("rvRicercaVacancy.codpatentesil");

		public static final String CODDESCMANSIONE = get("rvRicercaVacancy.coddescmansione");
		public static final String CODDESCCONTRATTO = get("rvRicercaVacancy.coddesccontratto");
		public static final String CODDESCORARIO = get("rvRicercaVacancy.coddescorario");
		public static final String CODDESCSETTORE = get("rvRicercaVacancy.coddescsettore");
		public static final String CODDESCLINGUA = get("rvRicercaVacancy.coddesclingua");
		public static final String CODDESCTITOLO_STUDIO = get("rvRicercaVacancy.coddesctitolo");
		public static final String CODDESCPATENTE = get("rvRicercaVacancy.coddescPatente");

		public static final String CODDESCMANSIONEISTAT = get("rvRicercaVacancy.coddescmansioneistat");
		public static final String CODDESCCONTRATTOSIL = get("rvRicercaVacancy.coddesccontrattosil");
		public static final String CODDESCORARIOSILIDO = get("rvRicercaVacancy.coddescorariosilido");
		public static final String CODDESCPATENTESIL = get("rvRicercaVacancy.coddescpatentesil");
	}

	public static class MsgMessaggio {
		public static final String NOTIFICA = "NOTIF";
		public static final String SUPPORTO = "SUPP";
		public static final String ESPERTO = "ESP";
		public static final String CERTIFICATO = "CERT";
		public static final String PRIMO_CONTATTO = "03";
		public static final String RISPOSTA_CONTATTO = "04";
		public static final Integer GIORNI_SCADENZA = 30;

	}

	public static class PfAttivita {
		public static final String MSG_SUPPORTO = "SUPP";
		public static final String MSG_CERTIFICATO = "CERT";
		public static final String MSG_ESPERTO = "ESP";
		public static final String MSG_PRIMO_CONTATTO = "PCONT";
	}

	public static class DeTipoAbilitato {
		public static final String DATORE_PRIVATO = "01";
		public static final String DATORE_PUBBLICO = "02";
		public static final String AGENZIA_SOMMINISTRAZIONE = "03";
		public static final String SOGGETTO_ABILITATO = "04";
		public static final String AGENZIA_LAVORO = "05";
		public static final String SOGGETTO_TIROCINI = "06";
	}

	public static class DeAutorizzazioneSare {
		public static final String NUOVA = "0";
		public static final String SOSPESA = "2";
		public static final String RESPINTA = "3";
		public static final String CONCESSA = "4";
	}

	public static class DeTipoDelegato {
		public static final String SOGGETTO_INTERMEDIAZIONE = "009";
		public static final String PROMOTORE_TIROCINI = "010";
	}

	public static class DeStatoInvioCl {
		public static final String IN_ATTESA_PRIMO_INVIO = "PA";
		public static final String IN_ATTESA_INVIO_CHIUSURA = "CA";
		public static final String IN_ATTESA_INVIO_VARIAZIONE = "VA";
		public static final String IN_ATTESA_INVIO = "MA";
		public static final String PRIMO_INVIO_COMPLETATO = "PI";
		public static final String CHIUSURA_INVIATA = "CI";
		public static final String VARIAZIONE_INVIATA = "VI";
		public static final String INVIATA = "MI";
		public static final String PRIMO_INVIO_COMPLETATO_ERRORE = "PE";
		public static final String CHIUSURA_INVIATA_ERRORE = "CE";
		public static final String VARIAZIONE_INVIATA_ERRORE = "VE";
		public static final String INVIATA_ERRORE = "ME";
		public static final String NON_SINCRONIZZATO_CLICLAVORO = "NO_COD";
	}

	public static class TipoSoggetto {
		public static final Integer AZIENDA = 1;
		public static final Integer CITTADINO = 2;
		public static final Integer INTERMEDIARIO = 3;
	}

	public static class TipoMessaggio {
		public static final String PRIMO_CONTATTO = "03";
		public static final String RISPOSTA_CONTATTO = "04";
		public static final String ATIPICI = "ATIP";

	}

	public static class TipoQuesito {
		public static final String LAVORO = "LAVORO";
		public static final String TEMATICA = "TEMATICA";

	}

	public static class DeTipoConsulenza {
		public static final String ATIPICO = "ATIPICO";
		public static final String PARTITA_IVA = "P_IVA";
	}

	public static class GpGruppo {
		public static final String CITTADINO_DEFAULT = "Cittadino di default";
		public static final String AZIENDA_DEFAULT = "Azienda di default";
		public static final String PROVINCIA_DEFAULT = "Provincia di default";
		public static final String REGIONE_DEFAULT = "Regione di default";
	}

	public static class GpRuolo {
		public static final String ENTE_ACCREDITATO_DESC = "Ente Accreditato";

		public static class MyStage {
			public static final String ENTE_OSPITANTE_DESC = "Ospitante";
			public static final String ENTE_PROMOTORE_TIR_DESC = "Promotore";
		}

	}

	public static class GpAttributo {
		public static final String A_FB_CONVENZIONI_QUADRO = "A_QUADRO_ACC";
		public static final String A_FB_CHECKLIST = "A_FABB_CHKLST";
		public static final String A_FABB_SCHEDA = "A_FABB_SCHEDA";
		public static final String A_ENTE_ACCREDITATO = "A_ACCREDITATO";
		public static final String A_ATTUATORE_L1 = "A_ATTUATORE_L1";
		public static final String A_ATTUATORE_L2 = "A_ATTUATORE_L2";
		public static final String A_ATTUATORE_L3 = "A_ATTUATORE_L3";
		public static final String A_ATTUATORE_L4 = "A_ATTUATORE_L4";

		public static final String A_ATTUATORE_ASSISTER = "A_ATTUATORE";
	}

	public static class DeStatoFbChecklist {
		public static final String IN_LAVORAZIONE = "LAV";
		public static final String PUBBLICATA = "PUB";
		public static final String VALIDATA = "VAL";
		public static final String CHIUSA = "CLS";
		public static final String REVOCATA = "REV";
	}

	public static class DeStatoFbScheda {
		public static final String IN_LAVORAZIONE = "LAV";
		public static final String PUBBLICATA = "PUB";
		public static final String CHIUSA = "CLS";
	}

	public static class DeStatoFbConvenzione {
		public static final String IN_LAVORAZIONE = "LAV";
		public static final String CONFERMATA = "CONF";
		public static final String PROTOCOLLATA = "PR";
		public static final String REVOCATA = "REV";
	}

	public static class DeTipoFbConvenzione {
		public static final String TIROCINI = "TR";
		public static final String MULTIMISURA = "PAL";
		public static final String DOTE = "DOTE";
	}

	public static class DeStatoPratica {
		/**
		 * Il CPI ha risposto al cittadino
		 */
		public static final String CHIUSA = "CHIUSA";
		/**
		 * Il consulente rifiuta la pratica
		 */
		public static final String CON_RIF = "CON_RIF";
		/**
		 * Il consulente risponde alla pratica
		 */
		public static final String CON_RISP = "CON_RISP";
		/**
		 * Pratica rifiutata dal CPI al consulente
		 */
		public static final String CPI_RIF = "CPI_RIF";
		/**
		 * Pratica inoltrata ai consulenti
		 */
		public static final String INOL_CON = "INOL_CON";
		/**
		 * Pratica inoltrata dal coordinatore al CPI
		 */
		public static final String INOL_CPI = "INOL_CPI";
		/**
		 * Pratica inviata dal lavoratore al coordinatore
		 */
		public static final String NUOVA = "NUOVA";
		/**
		 * Pratica Rifiutata dal Coordinatore al cittadino
		 */
		public static final String RIFIUTO = "RIFIUTO";
	}

	public static class AzioneServizioCl {
		public static final String INVIO_CANDIDATURA = "invioCandidatura";
		public static final String INVIO_VACANCY = "invioVacancy";
		public static final String INVIO_MESSAGGIO = "invioMessaggio";
		public static final String CHIUSURA_INVIATA = "CI";
		public static final String IN_ATTESA_INVIO_VARIAZIONE = "VA";
		public static final String IN_ATTESA_INVIO = "MA";
	}

	public static class DeMotivoChiusura {
		public static final String RITIRO_OFFERTA = "03";
	}

	/*
	 * public static enum DeTipoAbilitatoEnum { DATORE_PRIVATO("01"), DATORE_PUBBLICO("02"),
	 * AGENZIA_SOMMINISTRAZIONE("03"), SOGGETTO_ABILITATO("04"), AGENZIA_LAVORO("05"), SOGGETTO_TIROCINI("06");
	 * 
	 * private String codice;
	 * 
	 * DeTipoAbilitatoEnum(String codice) { this.codice = codice; }
	 * 
	 * public String getCodice() { return codice; } }
	 */

	public static class DeTrasferta {
		public static final String NO_TRASFERTE = "1";
		public static final String AMBITO_PROVINCIALE = "2";
		public static final String AMBITO_REGIONALE = "3";
		public static final String AMBITO_NAZIONALE = "4";
		public static final String AMBITO_EUROPEO = "5";
		public static final String AMBITO_INTERNAZIONALE = "6";
	}

	public static class DeIdoneitaCandidatura {
		public static final String NON_ESAMINATO = "-1";
		public static final String NON_IDONEO = "0";
		public static final String PUNTEGGIO_1 = "1";
		public static final String PUNTEGGIO_2 = "2";
		public static final String PUNTEGGIO_3 = "3";
		public static final String PUNTEGGIO_4 = "4";
		public static final String PUNTEGGIO_5 = "5";
	}

	public static class DeRuolo {
		public static final String DEFAULT = "default";
		public static final String OPERATORE_GENERALE = "operatore generale";

	}

	public static class DeAmbitoDiffusione {
		public static final String REGIONALE = "01";
		public static final String NAZIONALE = "02";
		public static final String EUROPEO = "03";

	}

	public static class DeContrattoSil {
		public static final String TIROCINIO = "TI";

	}

	public static class DeServizioInfo {
		public static final String COD_CV_INSTRUZIONE = "cvIstruzione";
		public static final String COD_CV_FORMAZIONE = "cvFormazione";
		public static final String COD_CV_ESPERIENZE_PROFESSIONALI = "cvEsperienzeProfessionali";
		public static final String COD_CV_LINGUE = "cvLingue";
		public static final String COD_CV_CONOSCENZE_INFORMATICHE = "cvInformatica";
		public static final String COD_CV_ABILITAZIONI = "cvAbilitazioni";
		public static final String COD_CV_COMPETENZE_TRASVERSALI = "cvCompetenzeTrasversali";
		public static final String COD_CV_ALTRE_INFORMAZIONI = "cvAltreInfo";
		public static final String COD_CV_PROFESSIONI_DESIDERATE = "cvProfessioniDesiderate";
		public static final String COD_LETTERA_BREVE_PRESENTAZIONE = "letteraBrevePresentazione";
		public static final String COD_LETTERA_MOTIVAZIONI_OBIETTIVI = "letteraMotivazioniObiettivi";
		public static final String COD_LETTERA_BENEFICI = "letteraBenefici";
		public static final String COD_LETTERA_PUNTI_FORZA = "letteraPuntiForza";
		public static final String COD_LETTERA_SALUTI = "letteraSaluti";
		public static final String COD_RICERCA_VACANCY_COSA = "ricercaVacancyCosa";
	}

	public static class DeTemaConsulenza {
		public static enum utente {
			CITTADINO, AZIENDA
		}
	}

	public enum LivelloLinguaEnum {
		SCOLASTICO, MEDIO, BUONO, OTTIMO, TECNICO, ECCELLENTE;
	}

	public static class DeProvenienza {
		public static final String COD_MYPORTAL = "MYPORTAL";
		public static final String COD_SIL_GENERIC = "SIL";
		public static final String COD_MINISTERO = "MIN";
		public static final String COD_SILPG = "SILPG";
		public static final String COD_SILTR = "SILTR";
		public static final String COD_APP = "APP";
	}

	public static class Evasione {
		public static final String PUBB_ANONIMA = "DFA";
		public static final String PUBB_ANONIMA_PRESELEZIONE = "DRA";
		public static final String PUBB_PALESE = "DFD";
		public static final String PUBB_PALESE_PRESELEZIONE = "DPR";
		public static final String PUBB_PALESE_DESCR = "Pubblicazione Palese";
	}

	public static class Lingua {
		public static final String MADRELINGUA = "5";

	}

	public static class DeTipoComunicazioneCl {
		public static final String INVIO = "01";
		public static final String CHIUSURA = "02";
		public static final String CONTATTO = "03";
		public static final String RISPOSTA = "04";
	}

	public static class PtScrivania {
		public static final String LEFT_COLUMN = "L";
		public static final String RIGHT_COLUMN = "R";
	}

	public static class TipoPoi {
		public static final String CV = "CV";
		public static final String VACANCY = "VACANCY";
	}

	public static class GoogleMap {
		public static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

		public static String getKey() {
			return get("maps.google.apikey");
		}
	}

	public static class OpenStreetMap {
		public static final String GEOCODE_URL = "https://nominatim.openstreetmap.org";
		public static final String ZOOM = "13";
	}

	public static class Date {
		public static final String FORMAT = get("date.format");
	}

	public static class Icar {
		public static final String MYPORTALTYPE = "myportal";
		public static final String TYPE = "ICAR";
		public static final String ICARTOKEN = "t";
		public static final String UNIQUE_ID = "userID";
		public static final String CITTA_NASC = "luogoNascita";
		public static final String STATO_NASC = "statoNascita";
		public static final String AUTH_METOD = "authenticationMetod";
		public static final String EMAIL = "emailAddress";
		public static final String TRUST_ALTO = "urn:oasis:names:tc:SAML:2.0:ac:classes:Smartcard";
		public static final String TRUST_BASSO = "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport";
		public static final String CODICEFISCALE = "CodiceFiscale";
		public static final String DATA_NASC = "dataNascita";

		public static final String CITTA_DOM = "cittaDomicilio";
		public static final String INDIRIZZO_DOM = "indirizzoDomicilio";

		public static final String TEL = "telefono";
		public static final String CELL = "cellulare";
		public static final String X509 = "x509";

	}

	public static class Spid {
		public static final String TYPE = "SPID";
		public static final String TOKEN = "t";
		public static final String UNIQUE_ID = "spidcode";

		// public static final String spidcode = "spidcode"; // =PTIT43ME5XXXX
		public static final String name = "name"; // =franco luigi
		public static final String familyname = "familyname"; // =vuoto
		public static final String placeofbirth = "placeofbirth"; // =B903
		public static final String countyofbirth = "countyofbirth"; // =CS
		public static final String dateofbirth = "dateofbirth"; // =1969-06-25
		public static final String gender = "gender"; // =M
		public static final String fiscalnumber = "fiscalnumber"; // =TINIT-VTUFXXXXXXXXX

		public static final String idcard = "idcard"; // =patenteGuida U1XXXXXXN mctcBOLOGNA 2018-04-16 2028-06-21
		public static final String mobilephone = "mobilephone"; // =+393284444444
		public static final String email = "email"; // =franco.vuoto@gmail.com
		public static final String address = "address"; // =VIA ETTORE XXXXX 2 40132 BOLOGNA BO
		public static final String expirationdate = "expirationdate"; // =2025-06-22

	}

	public static class Federa {
		public static final String HEADER = "FEDERA_HEADER";
		public static final String CODICEFISCALE = "codicefiscale";
		public static final String DOMAIN = "federauserdomain";
		public static final String LOGGEDSECURITY = "abc";
		public static final String LOGGED = "logged?";
		public static final String MYPORTALTYPE = "myportal";
		public static final String TYPE = "federa";
		public static final String TRUSTLEVEL = "trustlevel";
		public static final String TRUSTALTO = "alto";
		public static final String TRUSTBASSO = "basso";

		public static final String URL_SERVIZI_FEDERATI = get("federa.address");

	}

	public static class Twitter {
		public static final String DOMAIN = "twitteruserdomain";
		public static final String HEADER = "TWITTER_HEADER";
		public static final String TYPE = "twitter";
	}

	public static class Google {
		public static final String DOMAIN = "googleuserdomain";
		public static final String HEADER = "GOOGLE_HEADER";
		public static final String TYPE = "google";
	}

	public static class Linkedin {
		public static final String DOMAIN = "linkedinuserdomain";
		public static final String HEADER = "LINKEDIN_HEADER";
		public static final String TYPE = "linkedin";
	}

	public static class Facebook {
		public static final String DOMAIN = "facebookuserdomain";
		public static final String HEADER = "FACEBOOK_HEADER";
		public static final String TYPE = "facebook";
	}

	public static class Auth {
		public static final String IMAGE = "image";
		public static final String ID = "id";
		public static final String EMAIL = "emailaddress";
		public static final String NOME = "nome";
		public static final String COGNOME = "cognome";
		public static final String TYPE = "type";

		public static final String TWITTER_ID = get("twitter.id");
		public static final String TWITTER_SECRET = get("twitter.secret");
		public static final String GOOGLE_ID = get("google.id");
		public static final String GOOGLE_SECRET = get("google.secret");
		public static final String LINKEDIN_ID = get("linkedin.id");
		public static final String LINKEDIN_SECRET = get("linkedin.secret");
		public static final String FACEBOOK_ID = get("facebook.id");
		public static final String FACEBOOK_SECRET = get("facebook.secret");
	}

	public static enum AzioneCliclavoro {
		SINCRONIZZO, CHIUSURA, MODIFICA_CV, MODIFICA_VA, CLICLAVORO_OK, CLICLAVORO_ERR
	}

	public static final String LINK_VIEW_NOTIFICHE_CVRER = "utente/curriculum/view_pf.xhtml?id=";
	public static final String LINK_VIEW_NOTIFICHE_OTHER = "utente/curriculum/view.xhtml?id=";
	public static final String LINK_VIEW_CONTATTI_CVRER = "/faces/secure/utente/curriculum/view_pf.xhtml";
	public static final String LINK_VIEW_CONTATTI_OTHER = "/faces/secure/utente/curriculum/view.xhtml";
	public static final String LINK_VIEW_NOTIFICHE_VARER = "azienda/vacancies/view_pf.xhtml?id=";
	public static final String LINK_VIEW_NOTIFICHE_VAOTHER = "azienda/vacancies/visualizza.xhtml?id=";
	public static final String LINK_VIEW_CONTATTI_VARER = "/faces/secure/azienda/vacancies/view_pf.xhtml";
	public static final String LINK_VIEW_CONTATTI_VAOTHER = "/faces/secure/azienda/vacancies/visualizza.xhtml";

	public static final String USERNAME_CLICLAVORO_CANDIDATURE = "cliclavoro";
	public static final String USERNAME_CLICLAVORO_VACANCY = "az_cliclavoro";

	public static class GetCittadino {

		public static final String RISPOSTA_DETTAGLIO_CITTADINO = "RispostaDettaglioCittadino";

		public static final String DATI_ACCOUNT = "DatiAccount";
		public static final String ACCOUNT_CITTADINO = "AccountCittadino";
		public static final String DETTAGLIO_CITTADINO = "DettaglioCittadino";

		public static final String ABILITATOSERVIZI = "AbilitatoServiziAmministrativi";
		public static final String ABILITATO = "Abilitato";
		public static final String COD_STATUS = "codStatus";

	}

	public static class XmlCommons {
		public static final String ESITO = "Esito";
		public static final String CODICE = "codice";
		public static final String DESCRIZIONE = "descrizione";
		public static final String RISPOSTA_ACCOUNT_CITTADINO = "RispostaAccountCittadino";
		public static final String IDPFPRINCIPAL = "idPfPrincipal";
		public static final String USERNAME = "username";
		public static final String NOME = "nome";
		public static final String COGNOME = "cognome";
		public static final String EMAIL = "email";
		public static final String COMUNE_NASCITA = "comuneNascita";
		public static final String COMUNE_DOMICILIO = "comuneDomicilio";
		public static final String INDIRIZZO_DOMICILIO = "indirizzoDomicilio";
		public static final String CODICE_FISCALE = "codiceFiscale";
		public static final String DATA_NASCITA = "dataNascita";
		public static final String CITTADINANZA = "cittadinanza";
		public static final String DOCUMENTO_IDENTITA = "documentoIdentita";
		public static final String NUMERO_DOCUMENTO = "numeroDocumento";
		public static final String DATA_SCADENZA_DOCUMENTO = "dtScadenzaDocumento";
	}

	public static class PutCittadino {
		public static final String REINVIO_MAIL = "reinvioMailAccreditamento";
		public static final String DESTINATARIO = "destinatarioCC";
		public static final String RISPOSTA_ACCOUNT_CITTADINO = "RispostaputAccountCittadino";
		public static final String RISPOSTA_REINVIO_MAIL = "RispostaputReinvioMail";
		public static final String PUT_ACCOUNT_CITTADINO = "putAccountCittadino";
		public static final String COD_PROVINCIA_SIL = "codProvinciaSil";

	}

	public static class MyPortalExceptionErrorCode {
		public static final String COMUNICAZIONE_PREC_NON_TROVATA = "COM_PREC_NOT_FOUND";
	}

	public static class BasicAuthPDD {
		public static final String BASIC_AUTH_ABILITATA = get("pdd.notifiche.basic-auth.abilitata");
		public static final String BASIC_AUTH_USERNAME = get("pdd.notifiche.basic-auth.user");
		public static final String BASIC_AUTH_PASSWORD = get("pdd.notifiche.basic-auth.password");

		public static final String BASIC_AUTH_YOUTHGUARANTEE_ABILITATA = get("pdd.notifiche.yg.basic-auth.abilitata");
		public static final String BASIC_AUTH_YOUTHGUARANTEE_USERNAME = get("pdd.notifiche.yg.basic-auth.user");
		public static final String BASIC_AUTH_YOUTHGUARANTEE_PASSWORD = get("pdd.notifiche.yg.basic-auth.password");

		public static final String BASIC_AUTH_SAP_ABILITATA = get("pdd.notifiche.sap.basic-auth.abilitata");
		public static final String BASIC_AUTH_SAP_USERNAME = get("pdd.notifiche.sap.basic-auth.user");
		public static final String BASIC_AUTH_SAP_PASSWORD = get("pdd.notifiche.sap.basic-auth.password");

		public static final String BASIC_AUTH_DID_ABILITATA = get("pdd.notifiche.did.basic-auth.abilitata");
		public static final String BASIC_AUTH_DID_USERNAME = get("pdd.notifiche.did.basic-auth.user");
		public static final String BASIC_AUTH_DID_PASSWORD = get("pdd.notifiche.did.basic-auth.password");
	}

	public static class Gamification {
		public static final String ASSIGN_BADGE_ENDPOINT = get("auth.endpoint")
				+ get("auth.gamification.assignBadge.endpoint");
		public static final String IS_ENABLED_ENDPOINT = get("auth.endpoint")
				+ get("auth.gamification.enabled.endpoint");
		public static final String COD_BADGE_DID = "DIDREQ";
		public static final String COD_BADGE_CURRICULUM = "CURRVITA";
		public static final String COD_BADGE_YG = "ISCRYG";
		public static final String COD_BADGE_LETTERA = "LETTPRES";
	}

	// indirizzo per endpoint MyCAS
	public static final String AUTHENTICATE_URL = get("auth.endpoint") + get("auth.utente.profilo.endpoint");
	public static final String PROFILE_URL = get("auth.endpoint") + get("auth.profilatura.endpoint");
	public static final String MYACCOUNT_URL = get("auth.myaccount.endpoint");
	public static final String MYCAS_URL = get("auth.mycas.endpoint");

	public static String getSolrUrl() {
		return get("solr.endpoint");
	}

	public static String getAttributeOwnersUrl() {
		return get("auth.endpoint") + get("auth.profilatura.attributeOwners");
	}

	public static String getGpGruppoByDescUrl() {
		return get("auth.endpoint") + get("auth.profilatura.getGruppoByDesc");
	}

	public static String getGpRuoloByDescUrl() {
		return get("auth.endpoint") + get("auth.profilatura.getRuoloByDesc");
	}

	public static String getProfiloUrl() {
		return get("auth.endpoint") + get("auth.utente.profilo.endpoint");
	}

	public static String putProfilaturaUrl() {
		return get("auth.endpoint") + get("auth.profilatura.putProfilatura");
	}

	public static String putSedeAccreditataUrl() {
		return get("auth.endpoint") + get("auth.accreditamento.putSedeAccreditata");
	}

	public static String disattivaSedeAccreditataUrl() {
		return get("auth.endpoint") + get("auth.accreditamento.disattivaSedeAccreditata");
	}

	/*
	 * public static boolean isMyPortalAppInstallata() { return App.KEY != null && App.KEY.length() > 0 &&
	 * App.ONESIGNAL_APP_ID != null && App.ONESIGNAL_APP_ID.length() > 0; }
	 */

	/**
	 * La property "vacancy.decodifiche_sil" controlla quale tipo di decodifica usare. Se non c'è, uso le ministeriali
	 */
	public static boolean usaDecodificheSilPerVacancy() {
		String property = get("vacancy.decodifiche_sil");
		if (property != null)
			return Boolean.parseBoolean(property);
		else
			return false;
	}

	/**
	 * La property "curriculum.decodifiche_sil" controlla quale tipo di decodifica usare per la sezione curriculum. Se
	 * non c'è, uso le ministeriali
	 */
	public static boolean usaDecodificheSilPerCurriculum() {
		String property = get("curriculum.decodifiche_sil");
		if (property != null)
			return Boolean.parseBoolean(property);
		else
			return false;
	}

	public static int getNumOreMaxValidazionePatto() {
		String property = get("pattosil.durata_max_accettazione");
		if (property != null)
			return Integer.parseInt(property);
		else
			return 24;// un giorno, di default
	}

	/**
	 * Lo stile CSS da usare per le pagine pubbliche, e per gli utenti che non hanno specificato uno stile particolare
	 * sul DB (ovvero tutti, dato che quella funzionalità non è implementata).
	 */
	public static String getDefaultCssStyle() {
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_VDA)) {
			return STILE_CSS_VDA;
		} else if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA)) {
			return STILE_CSS_UMBRIA;
		} else {
			return STILE_CSS_MYPORTAL;
		}
	}

	/**
	 * Recupera l'URL a cui andare per fare logout.
	 */
	public static String getMyCasLogoutUrl() {
		return get("auth.mycas.endpoint") + get("auth.mycas.logout");
	}

	// Indirizzi di altri sistemi
	// public static final String MYSEMINAR_URL = get("myseminar.endpoint");
	public static final String MYAGENDA_URL = get("myagenda.endpoint");
	public static final String ASSISTER_URL = get("assister.endpoint");
	public static final String MYSTAGE_URL = get("mystage.endpoint");
	public static final String KNOWAGE_URL = get("knowage.endpoint");
	public static final String ACCREDITAMENTO_URL = get("accreditamento.endpoint");
	public static final String RES_URL = get("res.endpoint");
	public static final String O2L_URL = get("o2l.endpoint");

	public static final String CONTESTO_APP = get("contesto.app");
	public static final String TITLE_ID_APP = get("title.id.app");
	public static final String TITLE_APP = get("title.app");

	public static final String DEFAULT_KEY = get("default.key");

	public static final int MAX_NUM_TENTATIVI_PRESA_APPUNTAMENTO = getInt("appuntamento.max_num_tentativi");

	public static final Integer MAX_PORTLET_VACANCIES = 5;

	public static class DeRuoloPortale {
		public static final String CITTADINO = "CITT";
		public static final String SYSTEM = "SYSTEM";
		public static final String AZIENDA = "AZIENDE";
		public static final String COORDINA = "COORDINA";
		public static final String CONSULEN = "CONSULEN";
		public static final String REGIONE = "REGIONI";
		public static final String PROVINCIA = "PROV";
		public static final String CERTIFIC = "CERTIFIC";
		public static final String PATRONATO = "PATR";
		public static final String SOGGETTOPUBBLICO = "SOGGPUBB";
		public static final String CPI = "CPI";
	}

	public static boolean isSysBetweenDtScadDtPubbStatic(java.util.Date dtScadPubb, java.util.Date dtPubb) {

		if (dtScadPubb == null && dtPubb == null)
			return false;

		// considero tutto il giorno, fino alle 23:59:59
		Calendar dtScad = Calendar.getInstance();
		dtScad.setTime(dtScadPubb);
		dtScad.set(Calendar.HOUR, 23);
		dtScad.set(Calendar.MINUTE, 59);
		dtScad.set(Calendar.SECOND, 59);

		java.util.Date now = new java.util.Date();

		// sysdate between dt_pubblicazione and dt_scadenza_pubblicazione
		return (dtPubb.before(now) && dtScad.getTime().after(now));
		 
	}

	public static class PtPortlet {
		public static final List<String> PORTLET_NASCOSTE = Arrays.asList("_portlet_assister_prov",
				"_portlet_assister");
	}

	public static class Sistema {

		public static class Sare {
			public static final String CODSISTEMA = "SARE";
		}

		public static class LavoroPerTe {
			public static final String CODSISTEMA = "LXTE";
		}

	}

	public static class DeBandoProgramma {
		public static final String COD_REI = "REI";
		public static final String COD_UMBAT = "UMBAT";
	}

	public static final String getOggettoEmailVacancyScadenza() {
		return get("emailVacancyScadenza.oggettoEmail");
	}
	
	public static final String getOggettoEmailVacancyInLav() {
		return get("emailVacancyInLav.oggettoEmail");
	}
	
	public static final String getWelcomepageEndpoint() {
		return get("welcomepage.endpoint");
	}

	public static final String getEmailNotificaTrovaLavoro() {
		return get("trovalavoro.notification.mail.from");
	}

	public static final String COD_STATO_ADESIONE_MIN_A = "A";

	public static final Long DEFAULT_NUMGG_VAC_SCADENZA = 15l;
	public static final Long DEFAULT_NUMGG_VAC_IN_LAV = 3l;
	public static final Long DEFAULT_NUMGG_CV_IN_SCADENZA = 3l;

	public static String getOTPRequestURL() {
		return get("auth.endpoint") + get("auth.otp.request");
	}

	public static String getOTPReplyURL() {
		return get("auth.endpoint") + get("auth.otp.reply");
	}

	public static String getOTPEnabledURL() {
		return get("auth.endpoint") + get("auth.otp.enabled");
	}

	public static double getCenterPositionLat() {
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_PUGLIA)) {
			return 40.64;
		}
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA)) {
			return 43.121034;
		}
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_VDA)) {
			return 45.740693;
		}
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_TRENTO)) {
			return 46.070849;
		}
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_CALABRIA)) {
			return 39.20;
		}
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			return 44.5;
		}
		// Default return RER coordinate latitudine
		return 44.5;
	}

	public static double getCenterPositionLng() {
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_PUGLIA)) {
			return 17.45;
		}
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA)) {
			return 12.413635;
		}
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_VDA)) {
			return 7.41806;
		}
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_TRENTO)) {
			return 11.120853;
		}
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_CALABRIA)) {
			return 16.50;
		}
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			return 11.0;
		}
		// Default return RER coordinate longitudine
		return 11.0;
	}

}
