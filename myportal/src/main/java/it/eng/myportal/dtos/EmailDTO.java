package it.eng.myportal.dtos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.DoTirocini;
import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.MsgMessaggioAtipico;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.Provincia;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

/**
 * Data transfer object del CV, sezione Lingua
 * 
 * @author iescone
 * 
 * @see AbstractUpdatablePkDTO
 * @see ICurriculumSection
 * @see IHasUniqueValue
 */

public class EmailDTO implements IDTO, Serializable {

	private static final long serialVersionUID = 5638575348777751939L;

	private static final Log log = LogFactory.getLog(EmailDTO.class);

	private static String DEFAULT_FROM = ConstantsSingleton.EMAIL_FROM;
	private static String CANDIDATO_FROM = ConstantsSingleton.CANDIDATO_FROM;
	private static String context = ConstantsSingleton.COD_REGIONE == 22 ? "Trentino Lavoro" : "Lavoro per Te";
	private static String EMAIL_FOOTER_REGISTER = ConstantsSingleton.EMAIL_FOOTER_REGISTER;

	private String from;
	private List<String> tos;
	private List<String> ccs;
	private List<String> ccns;
	private String subject;
	private String messageBody;

	public EmailDTO() {
		super();
		tos = new ArrayList<String>();
		ccs = new ArrayList<String>();
		ccns = new ArrayList<String>();
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getTos() {
		return tos;
	}

	public void setTos(List<String> tos) {
		this.tos = tos;
	}

	public List<String> getCcs() {
		return ccs;
	}

	public void setCcs(List<String> ccs) {
		this.ccs = ccs;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public List<String> getCcns() {
		return ccns;
	}

	public void setCcns(List<String> ccns) {
		this.ccns = ccns;
	}

	public static EmailDTO buildRegistrationEmail(RegisterUtenteDTO registerUtenteDTO) {
		return buildRegistrationEmail(registerUtenteDTO, null);
	}

	/**
	 * Costruisce un'email da inviare all'utente per la conferma della registrazione sul portale.
	 * 
	 * @param registerUtenteDTO
	 *            dati dell'utente a cui spedire la mail
	 * @return la mail da inviare
	 */
	public static EmailDTO buildRegistrationEmail(RegisterUtenteDTO registerUtenteDTO, String customTo) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;

		if (customTo != null && !customTo.isEmpty()) {
			email.tos.add(customTo);
		} else {
			email.tos.add(registerUtenteDTO.getEmail());
		}

		email.subject = "Conferma registrazione nuovo account su " + context;
		String link;
		try {
			link = ConstantsSingleton.MYACCOUNT_URL + "/register/confirm/"
					+ EmailDTO.escapeCharacters(registerUtenteDTO.getUsername()) + "/"
					+ EmailDTO.escapeCharacters(registerUtenteDTO.getEmail()) + "/"
					+ registerUtenteDTO.getActivateToken();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		Map<String, String> val = new HashMap<String, String>();
		val.put("cognome", registerUtenteDTO.getCognome());
		val.put("nome", registerUtenteDTO.getNome());
		val.put("username", registerUtenteDTO.getUsername());
		val.put("activationLink", link);
		val.put("mailfrom", ConstantsSingleton.EMAIL_FOOTER_REGISTER);

		InputStream is = Utils.getTemplateEmail("registrazionEmail.html");

		email.messageBody = substitutorPlaceHolder(is, val);

		/*
		 * email.messageBody = "<html><body>" + "<p>Ciao " + registerUtenteDTO.getCognome() + " " +
		 * registerUtenteDTO.getNome() + ",<br/>" + "Grazie per esserti registrato su " + context + ".<br/>";
		 * 
		 * if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_UMBRIA) { email.messageBody += "<br/> "
		 * + "<b>ATTENZIONE! L'ISCRIZIONE AL PORTALE NON GENERA AUTOMATICAMENTE L'ADESIONE A GARANZIA GIOVANI. " +
		 * "PER ADERIRE A GARANZIA GIOVANI, UNA VOLTA ENTRATI OCCORRE CLICCARE SUL PULSANTE  " +
		 * "\"ADERISCI A GARANZIA GIOVANI\" ( Primo box a sinistra in alto immediatamente sotto il logo di LavoroperTe)</b><br/><br/>"
		 * ; }
		 * 
		 * email.messageBody += "L'account con cui ti sei registrato e' <b>" + registerUtenteDTO.getUsername() +
		 * "</b>.<br/>" +
		 * "Prima di procedere con l'attivazione del tuo account e' necessario compiere un ulteriore passaggio.<br/>" +
		 * "<b>Nota bene</b>: devi completare questo passaggio per consentire agli amministratori di attivare il tuo account.<br/>"
		 * +
		 * "Sara' necessario cliccare sul link una sola volta e il tuo account verra' automaticamente aggiornato.<br/>"
		 * + "Per completare la registrazione, " + "clicca sul collegamento qui sotto:<br/>" + "<a href=\"" + link +
		 * "\">" + link + "</a><br/>" +
		 * "Qualora cliccando sul link vi non si arrivi alla pagina di abilitazione account, " +
		 * "accedere manualmente provando a copiare il link nella barra degli indirizzi del proprio browser.<br/>" +
		 * "In caso di problemi con la registrazione contatta gli amministratori all'indirizzo:<br/>" +
		 * "<a href=\"mailto:" + ConstantsSingleton.EMAIL_FOOTER_REGISTER + "\">" +
		 * ConstantsSingleton.EMAIL_FOOTER_REGISTER + "</a><br/><br/>" + "Saluti, l'amministrazione di <br/>" + context
		 * + "</p>" + "</body></html>";
		 */
		return email;
	}

	public static EmailDTO buildResetPwdEmail(RecuperoPasswordDTO data) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(data.getEmail());
		email.subject = "Reset Password di accesso a " + context;
		String link;
		try {
			link = ConstantsSingleton.MYACCOUNT_URL + "/changePassword/" + EmailDTO.escapeCharacters(data.getUserName())
					+ "/" + EmailDTO.escapeCharacters(data.getEmail()) + "/" + data.getPasswordToken();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		email.messageBody = "Ciao " + data.getCognome() + " " + data.getNome() + ",<br/>"
				+ "lo username per il quale si sta recuperando la password e' il seguente: <b>" + data.getUserName()
				+ "</b>.<br/>"
				+ "Per poter accedere alla pagina di recupero password e' necessario cliccare il collegamento qui sotto.<br/>"
				+ "<a href=\"" + link + "\">" + link + "</a><br/>"
				+ "Qualora cliccando sul link vi non si arrivi alla pagina di abilitazione account, "
				+ "accedere manualmente provando a copiare il link nella barra degli indirizzi del proprio browser.<br/>"
				+ "In caso di problemi con la registrazione contatta gli amministratori all'indirizzo:<br/>"
				+ "<a href=\"mailto:" + ConstantsSingleton.EMAIL_FOOTER_REGISTER + "\">"
				+ ConstantsSingleton.EMAIL_FOOTER_REGISTER + "</a><br/><br/>" + "Saluti, l'amministrazione di <br/>"
				+ context;

		return email;
	}

	/**
	 * Costruisce un'email da inviare all'utente per la conferma che la sua registrazione e' avvenuta con successo.
	 * 
	 * @param registerDTO
	 *            dati dell'utente a cui spedire la mail
	 * @param emailServiziOnline
	 * @param codiceRichiestaAutForte
	 * @return la mail da inviare
	 */
	public static EmailDTO buildConfirmRegistrazioneForteEmail(RegisterDTO registerDTO, String codiceRichiestaAutForte,
			String emailServiziOnline) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(registerDTO.getEmail());
		email.subject = "Conferma attivazione account su " + context;
		email.messageBody = "<html><body>" + "<p>Ciao " + registerDTO.getUsername() + ",<br/>"
				+ "Grazie per esserti registrato su " + context + "<br/>" + "Il tuo account e' stato confermato!";

		if (codiceRichiestaAutForte != null) {
			email.messageBody += "<br/>" + "Puoi accedere a " + context + " ed utilizzare le sue funzionalita'. "
					+ "Per l'abilitazione all'utilizzo dei servizi amministrativi per i quali hai fatto "
					+ "richiesta procedi come segue: <b>copia il codice sottostante</b><br/>" + "<b>"
					+ codiceRichiestaAutForte + "</b>" + "<br/>"
					+ "e <b>invialo</b> unitamente ai tuoi dati personali (Nome, Cognome e Codice Fiscale), all'indirizzo<br/>"
					+ "<b>" + emailServiziOnline + "</b>" + "<br/>"
					+ "utilizzando un indirizzo di <b>Posta Elettronica Certificata (PEC)</b> oppure <b>portalo</b> fisicamente al tuo CPI."
					+ "A seguito di cio' riceverai il codice di attivazione definitivo "
					+ "(via mail al tuo indirizzo PEC oppure direttamente presso il CPI) "
					+ "che dovrai inserire tramite il link \"Inserisci codice di attivazione definitivo\" "
					+ "per essere abilitato ad usufruire dei servizi amministrativi.<br/>";
		}

		email.messageBody += "<br/>Se non ricordi o non possiedi una password, segui la procedura di <b>recupero password</b> disponibile sul  portale al seguente indirizzo:<br/><i>"
				+ " " + ConstantsSingleton.BASE_URL
				+ "</i><br/>In caso di problemi con la registrazione contatta gli amministratori all'indirizzo:<br/>"
				+ "<a href=\"mailto:" + ConstantsSingleton.EMAIL_FOOTER_REGISTER + "\">"
				+ ConstantsSingleton.EMAIL_FOOTER_REGISTER + "</a><br/><br/>" + "Saluti, l'amministrazione di <br/>"
				+ context + "</body></html>";
		return email;
	}

	public static EmailDTO buildConfirmRegistrationeEmail(RegisterDTO registerDTO) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(registerDTO.getEmail());
		email.subject = "Conferma attivazione account su " + context;

		email.messageBody = "Ciao " + registerDTO.getUsername() + ",<br/>" + "Grazie per esserti registrato su "
				+ context + "<br/>";
		email.messageBody += "Il tuo account e' stato confermato!<br/><br/>";
		email.messageBody += "Se non ricordi o non possiedi una password, segui la procedura di <b>recupero password</b> disponibile sul  portale al seguente indirizzo:<br/><i>"
				+ " " + ConstantsSingleton.BASE_URL
				+ "</i><br/>In caso di problemi con la registrazione contatta gli amministratori all'indirizzo:<br/>"
				+ "<a href=\"mailto:" + ConstantsSingleton.EMAIL_FOOTER_REGISTER + "\">"
				+ ConstantsSingleton.EMAIL_FOOTER_REGISTER + "</a><br/><br/>" + "Saluti, l'amministrazione di <br/>"
				+ context + "</body></html>";

		return email;
	}

	private static String escapeCharacters(String parameter) throws UnsupportedEncodingException {
		return URLEncoder.encode(parameter, "UTF-8");
	}

	public static EmailDTO buildRegistrationEmail(RegisterAziendaDTO registerAziendaDTO) {
		return buildRegistrationEmail(registerAziendaDTO, null);
	}

	/**
	 * Costruisce un'email da inviare all'azienda per la conferma della registrazione sul portale.
	 * 
	 * @param registerAziendaDTO
	 *            dati dell'azienda a cui spedire la mail
	 * @return la mail da inviare
	 */
	public static EmailDTO buildRegistrationEmail(RegisterAziendaDTO registerAziendaDTO, String customTo) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		if (customTo != null && !customTo.isEmpty()) {
			email.tos.add(customTo);
		} else {
			email.tos.add(registerAziendaDTO.getEmail());
		}
		email.subject = "Conferma registrazione nuovo account azienda su " + context;
		String link;
		try {
			link = ConstantsSingleton.MYACCOUNT_URL + "/register/confirm/"
					+ EmailDTO.escapeCharacters(registerAziendaDTO.getUsername()) + "/"
					+ EmailDTO.escapeCharacters(registerAziendaDTO.getEmail()) + "/"
					+ registerAziendaDTO.getActivateToken();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		Map<String, String> val = new HashMap<String, String>();
		val.put("cognome", registerAziendaDTO.getCognome());
		val.put("nome", registerAziendaDTO.getNome());
		val.put("username", registerAziendaDTO.getUsername());
		val.put("activationLink", link);
		val.put("mailfrom", ConstantsSingleton.EMAIL_FOOTER_REGISTER);

		InputStream is = Utils.getTemplateEmail("registrazionEmailAzienda.html");

		email.messageBody = substitutorPlaceHolder(is, val);

		/*
		 * email.messageBody = "Ciao " + registerAziendaDTO.getNome() + " " + registerAziendaDTO.getCognome() + ",<br/>"
		 * + "Grazie per esserti registrato su " + context + ".<br/>" + "L'account con cui ti sei registrato e' <b>" +
		 * registerAziendaDTO.getUsername() + "</b>.<br/>" +
		 * "Per completare la registrazione clicca sul link qui sotto una sola volta e il tuo account verra' automaticamente attivato<br/>"
		 * + "<a href=\"" + link + "\">" + link + "</a>"; if (ConstantsSingleton.COD_REGIONE ==
		 * ConstantsSingleton.COD_REGIONE_RER) { email.messageBody += "<br/><br/>" +
		 * "Qualora cliccando sul link vi non si arrivi alla pagina di abilitazione account, " +
		 * "accedere manualmente provando a copiare il link nella barra degli indirizzi del proprio browser.<br/>" +
		 * "Se hai richiesto anche l'accreditamento al SARE, una volta attivato l'account, entrando nel portale dalla sezione Profili,<br/>"
		 * + "dovrai stampare il modulo di accreditamento, firmarlo e inviarlo alla Provincia competente.<br/>" +
		 * "Nella stampa troverai l'elenco dei documenti da allegare al modulo.<br/>" +
		 * "Successivamente la Provincia verifichera' la tua richiesta e, dopo averti abilitato,<br/>" +
		 * "potrai accedere al SARE direttamente dalla tua pagina personale del Portale."; } email.messageBody +=
		 * "<br/>In caso di problemi con la registrazione contatta gli amministratori all'indirizzo:<br/>" +
		 * "<a href=\"mailto:" + ConstantsSingleton.EMAIL_FOOTER_REGISTER + "\">" +
		 * ConstantsSingleton.EMAIL_FOOTER_REGISTER + "</a><br/><br/>" + "Saluti, l'amministrazione di<br/> " + context;
		 */

		return email;
	}

	public static EmailDTO buildRegistrationEmailRichiestaPersonale(RegisterAziendaDTO registerAziendaDTO) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(registerAziendaDTO.getEmail());
		email.subject = "Conferma registrazione nuovo account azienda su " + context;

		String link;
		try {
			link = ConstantsSingleton.MYACCOUNT_URL + "/register/confirm/"
					+ EmailDTO.escapeCharacters(registerAziendaDTO.getUsername()) + "/"
					+ EmailDTO.escapeCharacters(registerAziendaDTO.getEmail()) + "/"
					+ registerAziendaDTO.getActivateToken();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		Map<String, String> val = new HashMap<String, String>();
		val.put("cognome", registerAziendaDTO.getCognome());
		val.put("nome", registerAziendaDTO.getNome());
		val.put("username", registerAziendaDTO.getUsername());
		val.put("password", registerAziendaDTO.getPassword());
		val.put("activationLink", link);
		val.put("mailfrom", ConstantsSingleton.EMAIL_FOOTER_REGISTER);

		InputStream is = Utils.getTemplateEmail("registrazionEmailRichiestaPersonale.html");

		email.messageBody = substitutorPlaceHolder(is, val);

		return email;
	}

	/**
	 * Costruisce un'email da inviare all'utente per la conferma della registrazione sul portale.
	 * 
	 * @param registerUtenteDTO
	 *            dati dell'utente a cui spedire la mail
	 * @return la mail da inviare
	 */
	public static EmailDTO buildRegistrationEmailCurriculum(RegisterUtenteDTO registerUtenteDTO) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(registerUtenteDTO.getEmail());
		email.subject = "Conferma registrazione nuovo account su " + context;
		String link;
		try {
			link = ConstantsSingleton.MYACCOUNT_URL + "/register/confirm/"
					+ EmailDTO.escapeCharacters(registerUtenteDTO.getUsername()) + "/"
					+ EmailDTO.escapeCharacters(registerUtenteDTO.getEmail()) + "/"
					+ registerUtenteDTO.getActivateToken();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		Map<String, String> val = new HashMap<String, String>();
		val.put("cognome", registerUtenteDTO.getCognome());
		val.put("nome", registerUtenteDTO.getNome());
		val.put("username", registerUtenteDTO.getUsername());
		val.put("password", registerUtenteDTO.getPassword());
		val.put("activationLink", link);
		val.put("mailfrom", ConstantsSingleton.EMAIL_FOOTER_REGISTER);

		InputStream is = Utils.getTemplateEmail("registrazionEmailCurriculum.html");

		email.messageBody = substitutorPlaceHolder(is, val);
		return email;
	}

	public static EmailDTO buildRegistrationEmailCurriculumDaSil(RegisterUtenteDTO registerUtenteDTO) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(registerUtenteDTO.getEmail());
		email.subject = "Conferma registrazione nuovo account su " + context;
		String link;
		try {
			link = ConstantsSingleton.MYACCOUNT_URL + "/register/confirm/"
					+ EmailDTO.escapeCharacters(registerUtenteDTO.getUsername()) + "/"
					+ EmailDTO.escapeCharacters(registerUtenteDTO.getEmail()) + "/"
					+ registerUtenteDTO.getActivateToken();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		Map<String, String> val = new HashMap<String, String>();
		val.put("cognome", registerUtenteDTO.getCognome());
		val.put("nome", registerUtenteDTO.getNome());
		val.put("username", registerUtenteDTO.getUsername());
		val.put("password", registerUtenteDTO.getPassword());
		val.put("activationLink", link);
		val.put("mailfrom", ConstantsSingleton.EMAIL_FOOTER_REGISTER);

		InputStream is = Utils.getTemplateEmail("registrazionEmailCurriculumDaSil.html");

		email.messageBody = substitutorPlaceHolder(is, val);
		return email;
	}

	public static EmailDTO buildVacancyImportataEmail(PfPrincipalDTO pfPrincipalDTO, VaDatiVacancyDTO vaDatiVacancyDTO,
			boolean isUpdate, String provenienza) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(pfPrincipalDTO.getEmail());
		String messageSubjTempl = "%1s offerta di lavoro";
		String messageBodyTempl = "Spett. %1s,\n " + "le comunichiamo che e' stata %2s sul portale " + context
				+ " l'offerta di lavoro %3s presa in gestione dal CPI di %4s<br/>" + "Cordiali saluti <br/>" + context;
		String tipo = "inserita";
		if (isUpdate) {
			tipo = "aggiornata ";
		}

		email.subject = String.format(messageSubjTempl, tipo);
		email.messageBody = String.format(messageBodyTempl, pfPrincipalDTO.getNome(), tipo,
				vaDatiVacancyDTO.getAttivitaPrincipaleEscape(), provenienza);

		return email;
	}

	public static EmailDTO buildConfermaAbilitazioneForteEmail(PfPrincipalDTO pfPrincipalDTO) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(pfPrincipalDTO.getEmail());
		email.subject = "Conferma abilitazione ai servizi avanzati su " + context;

		Map<String, String> val = new HashMap<String, String>();
		val.put("cognome", pfPrincipalDTO.getCognome());
		val.put("nome", pfPrincipalDTO.getNome());

		InputStream is = Utils.getTemplateEmail("confermaAbilitazioneForteEmail.html");

		email.messageBody = substitutorPlaceHolder(is, val);

		/*
		 * email.messageBody = "Ciao " + pfPrincipalDTO.getNome() + ",<br/>" +
		 * "Ti confermiamo l'abilitazione ai servizi avanzati di " + context + "!" + "<br/>" +
		 * "Saluti, l'amministrazione di<br/>" + " " + context;
		 */
		return email;
	}

	public static EmailDTO buildAppuntamentoGaranziaOverEmail(PfPrincipalDTO utente, AppuntamentoDTO appuntamento) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(utente.getEmail());
		email.subject = "Promemoria Appuntamento CPI";

		Map<String, String> val = new HashMap<String, String>();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		val.put("cognome", utente.getCognome());
		val.put("nome", utente.getNome());
		val.put("cpi", appuntamento.getAmbienteCpi());
		val.put("data", dateFormatter.format(appuntamento.getData()));
		val.put("orario", appuntamento.getOrario());

		InputStream is = Utils.getTemplateEmail("appuntamentoGaranziaOverEmail.html");

		email.messageBody = substitutorPlaceHolder(is, val);
		return email;
	}

	public static EmailDTO buildRegistrazioneForteEmail(UtenteCompletoDTO data, String registrazioneForteToken) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(data.getEmail());
		email.subject = "Richiesta conferma abilitazione ai servizi";
		String link = ConstantsSingleton.BASE_URL + "/faces/secure/utente/servizi/confermaRegistrazione.xhtml?" + "t="
				+ registrazioneForteToken;

		email.messageBody = "Ciao " + data.getNome() + ",<br/>"
				+ "Per poter confermare l'attivazione dei servizi avanzati occorre cliccare sul seguente link:<br/>"
				+ link + "<br/>"
				+ "In caso di problemi con l'attivazione dei servizi contatta gli amministratori all'indirizzo:<br/>"
				+ ConstantsSingleton.EMAIL_FOOTER_REGISTER + "<br/>" + "Saluti, l'amministrazione di<br/>" + " "
				+ context;

		return email;
	}

	public static EmailDTO buildErrorsClicLavoroEmail(String elencoErrori) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(ConstantsSingleton.EMAIL_ERR_DEBUG);
		email.subject = "[CLICLAVORO] Report errori cliclavoro";

		email.messageBody = "<html><body> Ciao,<br/>"
				+ "Elenco problematiche riscontrate nella gestione della metodologia di CLICLAVORO:<br/>" + elencoErrori
				+ "<br/>" + "Saluti, l'amministrazione di<br/>" + " " + context;

		return email;
	}

	public static EmailDTO buildVacancyScadenzaEmail(List<NotificaScadenzaVacancyDTO> elencoVac) {
		String htmlVacancyItems = "";
		final InputStream templateVacancyItem = Utils.getTemplateEmail("vacancyScadenzaEmailItem.html");
		String testoMail = getTextMailFromTemplate(templateVacancyItem);
		Map<String, String> mapRigaVacancy = new HashMap<String, String>();
		for (NotificaScadenzaVacancyDTO current : elencoVac) {
			mapRigaVacancy.clear();
			mapRigaVacancy.put("numAnno", current.getNumAnno().toString());
			mapRigaVacancy.put("numRichiesta", current.getNumRichiesta().toString());
			String attivitaPrincipale = current.getAttivitaPrincipale();
			if (attivitaPrincipale.length() > 30) {
				attivitaPrincipale = attivitaPrincipale.substring(0, 27) + "...";
			}
			mapRigaVacancy.put("attivitaPrincipale", attivitaPrincipale);
			String link = ConstantsSingleton.BASE_URL + "/faces/secure/azienda/vacancies/edit.xhtml?id="
					+ current.getIdVaDatiVacancy().toString();
			mapRigaVacancy.put("linkVacancy", link);
			htmlVacancyItems += substitutorPlaceHolder(testoMail, mapRigaVacancy);
		}

		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(elencoVac.get(0).getEmail());
		email.subject = ConstantsSingleton.getOggettoEmailVacancyScadenza();

		InputStream templateEmail = Utils.getTemplateEmail("vacancyScadenzaEmail.html");
		Map<String, String> mapEmail = new HashMap<String, String>();
		mapEmail.put("listaVacancy", htmlVacancyItems);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		mapEmail.put("dtScadenzaPubblicazione", dateFormat.format(elencoVac.get(0).getDtScadenzaPubblicazione()));
		email.messageBody = substitutorPlaceHolder(templateEmail, mapEmail);

		return email;
	}
	
	public static EmailDTO buildVacancyInLavEmail(List<NotificaInLavVacancyDTO> elencoVac, int numGGInLav) {
		String htmlVacancyItems = "";
		final InputStream templateVacancyItem = Utils.getTemplateEmail("vacancyInLavEmailItem.html");		
		String testoMail = getTextMailFromTemplate(templateVacancyItem);
		Map<String, String> mapRigaVacancy = new HashMap<String, String>();
		for (NotificaInLavVacancyDTO current : elencoVac) {
			mapRigaVacancy.clear();
			mapRigaVacancy.put("numAnno", current.getNumAnno().toString());
			mapRigaVacancy.put("numRichiesta", current.getNumRichiesta().toString());
			String attivitaPrincipale = current.getAttivitaPrincipale();
			if (attivitaPrincipale.length() > 30) {
				attivitaPrincipale = attivitaPrincipale.substring(0, 27) + "...";
			}
			mapRigaVacancy.put("attivitaPrincipale", attivitaPrincipale);
			String link = null;
			if (!(ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER)) {
				link = ConstantsSingleton.BASE_URL + "/faces/secure/azienda/vacancies/edit.xhtml?id="
						+ current.getIdVaDatiVacancy().toString();

			} else {
				link = ConstantsSingleton.BASE_URL + "/faces/secure/azienda/vacancies/edit_pf.xhtml?id="
						+ current.getIdVaDatiVacancy().toString();
			}

			mapRigaVacancy.put("linkVacancy", link);
			htmlVacancyItems += substitutorPlaceHolder(testoMail, mapRigaVacancy);
		}

		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(elencoVac.get(0).getEmail());
		email.subject = ConstantsSingleton.getOggettoEmailVacancyInLav();

		InputStream templateEmail = Utils.getTemplateEmail("vacancyInLavEmail.html");		
		Map<String, String> mapEmail = new HashMap<String, String>();
		mapEmail.put("listaVacancy", htmlVacancyItems);
		mapEmail.put("numGGInLav", Integer.toString(numGGInLav));

		email.messageBody = substitutorPlaceHolder(templateEmail, mapEmail);

		return email;
	}

	/**
	 * email inviata al coordinatore quando un cittadino effettua una nuova richiesta
	 * 
	 * @param pratica
	 * @param listaCoordinatori
	 * @return
	 */
	public static EmailDTO buildEmailCoordinatore(MsgMessaggioAtipico pratica, List<PfPrincipal> listaCoordinatori) {
		EmailDTO email = new EmailDTO();
		String tipoConsulenza = null;
		if (ConstantsSingleton.DeTipoConsulenza.ATIPICO.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
			tipoConsulenza = "\"atipico\"";
		} else {
			tipoConsulenza = "P. IVA";
		}
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
		List<String> listaIndirizzi = new ArrayList<String>();

		for (PfPrincipal p : listaCoordinatori) {
			if (p.isCoordinatore()) {
				if (Utils.isMailCorretta(p.getCoordinatoreInfo().getEmail())) {
					listaIndirizzi.add(p.getCoordinatoreInfo().getEmail());
				}
			} else {
				if (Utils.isMailCorretta(p.getEmail())) {
					listaIndirizzi.add(p.getEmail());
				}
			}
		}

		PfPrincipal from = pratica.getMsgMessaggio().getPfPrincipalFrom();
		String testoProvincia = null;
		String mittente = null;
		DeProvincia provincia = null;
		if (from.getAziendaInfo() != null) {
			mittente = "Nome: " + from.getNome() + "<br/>" + "Cognome: " + from.getCognome();
			provincia = from.getAziendaInfo().getDeProvincia();
			if (provincia != null) {
				testoProvincia = "Provincia: " + provincia.getDenominazione();
			} else {
				testoProvincia = "Provincia: "
						+ from.getAziendaInfo().getDeComuneSede().getDeProvincia().getDenominazione();
			}
		} else {
			mittente = "Nome: " + from.getNome() + "<br/>" + "Cognome: " + from.getCognome();
			testoProvincia = "Provincia: "
					+ from.getUtenteInfo().getDeComuneDomicilio().getDeProvincia().getDenominazione();
		}

		email.from = DEFAULT_FROM;
		email.tos = listaIndirizzi;

		// Da ottobre 2017, l'Emilia Romagna non vuole riferimenti alla parola "atipico" nelle mail.
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			email.subject = "Nuova richiesta di consulenza per lavoro";
			email.messageBody = "Gentile Coordinatore,<br/><br/>" + "nella sezione Consulenza per il lavoro di "
					+ context + " &egrave; pervenuta una nuova " + "richiesta effettuata da:<br/><br/>" + mittente
					+ "<br/>" + "e-mail: " + from.getEmail() + "<br/>" + testoProvincia + "<br/>"
					+ "Data/ora richiesta: " + df.format(pratica.getDtmIns()) + "<br/><br/>"
					+ "Cordiali saluti,<br/>Servizio assistenza tecnica<br/> Consulenza per il lavoro.";
		} else {
			email.subject = "Consulenza per il lavoro " + tipoConsulenza + " Nuova richiesta di consulenza per lavoro "
					+ tipoConsulenza;
			email.messageBody = "Gentile Coordinatore,<br/><br/>" + "nella sezione Consulenza per il lavoro "
					+ tipoConsulenza + " di " + context + " &egrave; pervenuta una nuova "
					+ "richiesta effettuata da:<br/><br/>" + mittente + "<br/>" + "e-mail: " + from.getEmail() + "<br/>"
					+ testoProvincia + "<br/>" + "Data/ora richiesta: " + df.format(pratica.getDtmIns()) + "<br/><br/>"
					+ "Cordiali saluti,<br/>Servizio assistenza tecnica<br/> Consulenza per il lavoro "
					+ tipoConsulenza;
		}

		return email;
	}

	/**
	 * Email mandata dal coordinatore al CPI
	 * 
	 * @param pratica
	 * @return
	 */
	public static EmailDTO buildEmailCPI(MsgMessaggioAtipico pratica, List<PfPrincipal> utenti) {
		EmailDTO email = new EmailDTO();
		String tipoConsulenza = null;
		if (ConstantsSingleton.DeTipoConsulenza.ATIPICO.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
			tipoConsulenza = "\"atipico\"";
		} else {
			tipoConsulenza = "P. IVA";
		}
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
		List<String> listaIndirizzi = new ArrayList<String>();

		// MARCO
		for (PfPrincipal princ : utenti) {
			// viene inviata la mail alla provincia solo se è abilitata per lo stesso tipo
			// di consulenza
			if (princ.isProvincia()) {
				Provincia prov = princ.getProvinciasForIdPfPrincipal().iterator().next();
				boolean checkInvioMail = false;
				if (ConstantsSingleton.DeTipoConsulenza.ATIPICO
						.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
					checkInvioMail = prov.getFlagTematica();
				} else {
					checkInvioMail = prov.getFlagLavoro();
				}
				if (checkInvioMail) {
					if (Utils.isMailCorretta(princ.getEmail())) {
						listaIndirizzi.add(princ.getEmail());

					}
				}
			} else {
				if (Utils.isMailCorretta(princ.getEmail())) {
					listaIndirizzi.add(princ.getEmail());
				}
			}
		}

		PfPrincipal from = pratica.getMsgMessaggio().getPfPrincipalFrom();
		String testoProvincia = null;

		DeProvincia provincia = null;
		if (from.getAziendaInfo() != null) {

			provincia = from.getAziendaInfo().getDeProvincia();
			if (provincia != null) {
				testoProvincia = "Provincia: " + provincia.getDenominazione();
			} else {
				testoProvincia = "Provincia: "
						+ from.getAziendaInfo().getDeComuneSede().getDeProvincia().getDenominazione();
			}
		} else {

			testoProvincia = from.getUtenteInfo().getDeComuneDomicilio().getDeProvincia().getDenominazione();
		}
		email.from = DEFAULT_FROM;
		email.tos = listaIndirizzi;

		// Da ottobre 2017, l'Emilia Romagna non vuole riferimenti alla parola "atipico" nelle mail.
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			email.subject = "Nuova richiesta di consulenza per lavoro";
			email.messageBody = "Gentile operatore,<br/><br/>" + "nella sezione Consulenza per il lavoro di " + context
					+ " &egrave; pervenuta " + "una nuova richiesta di consulenza:<br/><br/>ID: "
					+ pratica.getIdMsgMessaggio() + "<br/>" + testoProvincia + "<br/>" + "Data/ora richiesta: "
					+ df.format(pratica.getDtmIns()) + "<br/><br/>"
					+ "Cordiali saluti,<br/>Il Coordinamento della<br/> Consulenza per il lavoro.";
		} else {
			email.subject = "Consulenza per il lavoro " + tipoConsulenza + " Nuova richiesta di consulenza per lavoro "
					+ tipoConsulenza;
			email.messageBody = "Gentile operatore,<br/><br/>" + "nella sezione Consulenza per il lavoro "
					+ tipoConsulenza + " di " + context + " &egrave; pervenuta "
					+ "una nuova richiesta di consulenza:<br/><br/>ID: " + pratica.getIdMsgMessaggio() + "<br/>"
					+ testoProvincia + "<br/>" + "Data/ora richiesta: " + df.format(pratica.getDtmIns()) + "<br/><br/>"
					+ "Cordiali saluti,<br/>Il Coordinamento della<br/> Consulenza per il lavoro " + tipoConsulenza;
		}

		return email;
	}

	/**
	 * Mail inviata dal CPI al consulente
	 * 
	 * @param pratica
	 * @param listaConsulenti
	 * @return
	 */
	public static EmailDTO buildEmailCPItoConsulente(MsgMessaggioAtipico pratica, List<PfPrincipal> listaConsulenti,
			String provinciaCPI) {
		EmailDTO email = new EmailDTO();
		String tipoConsulenza = null;
		if (ConstantsSingleton.DeTipoConsulenza.ATIPICO.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
			tipoConsulenza = "\"atipico\"";
		} else {
			tipoConsulenza = "P. IVA";
		}
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
		List<String> listaIndirizzi = new ArrayList<String>();

		for (PfPrincipal p : listaConsulenti) {
			// per inviare la mail il consulente deve poter visualizzare la
			// pratica
			if (p.canViewPraticaAtipici(pratica)) {
				if (Utils.isMailCorretta(p.getEmail())) {
					listaIndirizzi.add(p.getEmail());
				}
			}
		}
		PfPrincipal from = pratica.getMsgMessaggio().getPfPrincipalFrom();
		String testoProvincia = null;
		String firma = null;
		DeProvincia provincia = null;
		if (from.getAziendaInfo() != null) {

			provincia = from.getAziendaInfo().getDeProvincia();
			if (provincia != null) {
				testoProvincia = "Provincia: " + provincia.getDenominazione();
				firma = "Centro per l'impiego di " + provincia.getDenominazione();
			} else {
				testoProvincia = "Provincia: "
						+ from.getAziendaInfo().getDeComuneSede().getDeProvincia().getDenominazione();
				firma = "Centro per l'impiego di " + provinciaCPI;
			}
		} else {

			testoProvincia = "Provincia: "
					+ from.getUtenteInfo().getDeComuneDomicilio().getDeProvincia().getDenominazione();
			firma = "Centro per l'impiego di " + provinciaCPI;
		}
		email.from = DEFAULT_FROM;
		email.tos = listaIndirizzi;

		// Da ottobre 2017, l'Emilia Romagna non vuole riferimenti alla parola "atipico" nelle mail.
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			email.subject = "Nuova richiesta di consulenza per lavoro";
			email.messageBody = "Gentile Consulente,<br/><br/>" + "nella sezione Consulenza per il lavoro di " + context
					+ " &egrave; " + "pervenuta una nuova richiesta di consulenza:<br/><br/>ID: "
					+ pratica.getIdMsgMessaggio() + "<br/>" + testoProvincia + "<br/>" + "Data/ora richiesta: "
					+ df.format(pratica.getDtmIns()) + "<br/><br/>" + "Cordiali saluti,<br/>" + firma;
		} else {
			email.subject = "Consulenza per il lavoro " + tipoConsulenza + " Nuova richiesta di consulenza per lavoro "
					+ tipoConsulenza;
			email.messageBody = "Gentile Consulente,<br/><br/>" + "nella sezione Consulenza per il lavoro "
					+ tipoConsulenza + " di " + context + " &egrave; "
					+ "pervenuta una nuova richiesta di consulenza:<br/><br/>ID: " + pratica.getIdMsgMessaggio()
					+ "<br/>" + testoProvincia + "<br/>" + "Data/ora richiesta: " + df.format(pratica.getDtmIns())
					+ "<br/><br/>" + "Cordiali saluti,<br/>" + firma;
		}

		return email;
	}

	/**
	 * Mail inviata dal CPI al consulente
	 * 
	 * @param pratica
	 * @param listaConsulenti
	 * @return
	 */
	public static EmailDTO buildEmailRifiutaRispostaCPItoConsulente(MsgMessaggioAtipico pratica,
			List<PfPrincipal> listaConsulenti, String denominazioneProvincia) {
		EmailDTO email = new EmailDTO();
		String tipoConsulenza = null;
		if (ConstantsSingleton.DeTipoConsulenza.ATIPICO.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
			tipoConsulenza = "\"atipico\"";
		} else {
			tipoConsulenza = "P. IVA";
		}
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
		List<String> listaIndirizzi = new ArrayList<String>();

		for (PfPrincipal p : listaConsulenti) {
			// per inviare la mail il consulente deve poter visualizzare la
			// pratica
			if (p.canViewPraticaAtipici(pratica)) {
				if (Utils.isMailCorretta(p.getEmail())) {
					listaIndirizzi.add(p.getEmail());
				}
			}
		}
		email.from = DEFAULT_FROM;
		email.tos = listaIndirizzi;

		// Da ottobre 2017, l'Emilia Romagna non vuole riferimenti alla parola "atipico" nelle mail.
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			email.subject = "Consulenza per il lavoro - " + context + ". Rifiuto della risposta alla Pratica ID "
					+ pratica.getIdMsgMessaggio();
			email.messageBody = "Gentile consulente,<br/><br/>la risposta alla pratica:<br/><br/>" + "ID: "
					+ pratica.getIdMsgMessaggio() + "<br/>" + "Provincia: " + denominazioneProvincia
					+ "<br/>Data/ora richiesta: " + df.format(pratica.getDtmIns())
					+ "<br/> &egrave; stata rifiutata dall'Operatore del " + "Centro per l'impiego di "
					+ denominazioneProvincia + "  <br/><br/>"
					+ "Cordiali saluti, <br/>Il Coordinamento <br/>della Consulenza per il lavoro.";
		} else {
			email.subject = "Consulenza per il lavoro " + tipoConsulenza + " - " + context
					+ ". Rifiuto della risposta alla Pratica ID " + pratica.getIdMsgMessaggio();
			email.messageBody = "Gentile consulente,<br/><br/>la risposta alla pratica:<br/><br/>" + "ID: "
					+ pratica.getIdMsgMessaggio() + "<br/>" + "Provincia: " + denominazioneProvincia
					+ "<br/>Data/ora richiesta: " + df.format(pratica.getDtmIns())
					+ "<br/> &egrave; stata rifiutata dall'Operatore del " + "Centro per l'impiego di "
					+ denominazioneProvincia + "  <br/><br/>"
					+ "Cordiali saluti, <br/>Il Coordinamento <br/>della Consulenza per il lavoro " + tipoConsulenza;
		}

		return email;
	}

	/**
	 * Mail inviata in risposta dal consulente al cpi
	 * 
	 * @param pratica
	 * @param risposta
	 * @return
	 */

	public static EmailDTO buildEmailConsulenteToCPI(MsgMessaggioAtipico pratica, MsgMessaggio risposta,
			List<PfPrincipal> utenti) {

		EmailDTO email = new EmailDTO();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
		String tipoConsulenza = null;
		if (ConstantsSingleton.DeTipoConsulenza.ATIPICO.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
			tipoConsulenza = "\"atipico\"";
		} else {
			tipoConsulenza = "P. IVA";
		}

		List<String> listaIndirizzi = new ArrayList<String>();

		for (PfPrincipal princ : utenti) {
			// viene inviata la mail alla provincia solo se è abilitata per lo stesso tipo
			// di consulenza
			if (princ.isProvincia()) {
				Provincia prov = princ.getProvinciasForIdPfPrincipal().iterator().next();
				boolean checkInvioMail = false;
				if (ConstantsSingleton.DeTipoConsulenza.ATIPICO
						.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
					checkInvioMail = prov.getFlagTematica();
				} else {
					checkInvioMail = prov.getFlagLavoro();
				}
				if (checkInvioMail) {
					if (Utils.isMailCorretta(princ.getEmail())) {
						listaIndirizzi.add(princ.getEmail());
					}
				}
			} else {
				if (Utils.isMailCorretta(princ.getEmail())) {
					listaIndirizzi.add(princ.getEmail());
				}
			}
		}

		email.from = DEFAULT_FROM;
		email.tos = listaIndirizzi;

		// Da ottobre 2017, l'Emilia Romagna non vuole riferimenti alla parola "atipico" nelle mail.
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			email.subject = "Consulenza per il lavoro - " + context + ". Risposta alla richiesta di consulenza ID "
					+ pratica.getIdMsgMessaggio();

			email.messageBody = "Gentile operatore,<br/><br/>nella sezione Consulenza per il lavoro di " + context
					+ " &egrave; disponibile la " + "risposta alla richiesta di consulenza:<br/><br/>" + "ID: "
					+ pratica.getIdMsgMessaggio() + "<br/>Provincia: " + pratica.getDeProvincia().getDenominazione()
					+ "<br/>Data/ora richiesta: " + df.format(pratica.getDtmIns()) + "<br/><br/>"
					+ "Cordiali saluti,<br/>Il consulente";
		} else {
			email.subject = "Consulenza per il lavoro " + tipoConsulenza + " - " + context
					+ ". Risposta alla richiesta di consulenza ID " + pratica.getIdMsgMessaggio();
			email.messageBody = "Gentile operatore,<br/><br/>nella sezione Consulenza per il lavoro " + tipoConsulenza
					+ " di " + context + " &egrave; disponibile la "
					+ "risposta alla richiesta di consulenza:<br/><br/>" + "ID: " + pratica.getIdMsgMessaggio()
					+ "<br/>Provincia: " + pratica.getDeProvincia().getDenominazione() + "<br/>Data/ora richiesta: "
					+ df.format(pratica.getDtmIns()) + "<br/><br/>" + "Cordiali saluti,<br/>Il consulente";
		}

		return email;
	}

	/**
	 * Mail inviata dal consulente al cpi in caso di rifiuto
	 * 
	 * @param pratica
	 * @param risposta
	 * @return
	 */

	public static EmailDTO buildEmailRifiutoConsulenteToCPI(MsgMessaggioAtipico pratica, MsgMessaggio risposta,
			List<PfPrincipal> utenti) {
		EmailDTO email = new EmailDTO();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
		String tipoConsulenza = null;
		if (ConstantsSingleton.DeTipoConsulenza.ATIPICO.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
			tipoConsulenza = "\"atipico\"";
		} else {
			tipoConsulenza = "P. IVA";
		}

		List<String> listaIndirizzi = new ArrayList<String>();

		for (PfPrincipal princ : utenti) {
			// viene inviata la mail alla provincia solo se è abilitata per lo stesso tipo
			// di consulenza
			if (princ.isProvincia()) {
				Provincia prov = princ.getProvinciasForIdPfPrincipal().iterator().next();
				boolean checkInvioMail = false;
				if (ConstantsSingleton.DeTipoConsulenza.ATIPICO
						.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
					checkInvioMail = prov.getFlagTematica();
				} else {
					checkInvioMail = prov.getFlagLavoro();
				}
				if (checkInvioMail) {
					if (Utils.isMailCorretta(princ.getEmail())) {
						listaIndirizzi.add(princ.getEmail());
					}
				}
			} else {
				if (Utils.isMailCorretta(princ.getEmail())) {
					listaIndirizzi.add(princ.getEmail());
				}
			}
		}

		email.from = DEFAULT_FROM;
		email.tos = listaIndirizzi;

		// Da ottobre 2017, l'Emilia Romagna non vuole riferimenti alla parola "atipico" nelle mail.
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			email.subject = "Consulenza per il lavoro - " + context + ". Rifiuto dell'assegnazione della Pratica ID "
					+ pratica.getIdMsgMessaggio();

			email.messageBody = "Gentile Operatore,<br/>" + "la pratica:<br/><br/>" + "ID: "
					+ pratica.getIdMsgMessaggio() + "<br/>" + "Provincia: "
					+ pratica.getDeProvincia().getDenominazione() + "<br/>" + "Data/ora richiesta: "
					+ df.format(pratica.getDtmIns()) + "<br/><br/>&egrave; stata rifiutata dal Consulente."
					+ "<br/><br/>Cordiali saluti,<br/>Il Coordinamento<br/> della Consulenza per il lavoro.";
		} else {
			email.subject = "Consulenza per il lavoro " + tipoConsulenza + " - " + context
					+ ". Rifiuto dell'assegnazione della Pratica ID " + pratica.getIdMsgMessaggio();

			email.messageBody = "Gentile Operatore,<br/>" + "la pratica:<br/><br/>" + "ID: "
					+ pratica.getIdMsgMessaggio() + "<br/>" + "Provincia: "
					+ pratica.getDeProvincia().getDenominazione() + "<br/>" + "Data/ora richiesta: "
					+ df.format(pratica.getDtmIns()) + "<br/><br/>&egrave; stata rifiutata dal Consulente."
					+ "<br/><br/>Cordiali saluti,<br/>Il Coordinamento<br/> della Consulenza per il lavoro "
					+ tipoConsulenza;
		}

		return email;
	}

	/**
	 * Email inviata al Cittadino contestualmente all'inoltro dal coordinatore al cpi
	 * 
	 * @param pratica
	 * @param mittente
	 * @return
	 */
	public static EmailDTO buildEmailConfermaCittadino(MsgMessaggioAtipico pratica) {
		EmailDTO email = new EmailDTO();
		String tipoConsulenza = null;
		if (ConstantsSingleton.DeTipoConsulenza.ATIPICO.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
			tipoConsulenza = "\"atipico\"";
		} else {
			tipoConsulenza = "P. IVA";
		}
		PfPrincipal cittadino = pratica.getMsgMessaggio().getPfPrincipalFrom();

		email.from = DEFAULT_FROM;
		List<String> lista = new ArrayList<String>();
		lista.add(cittadino.getEmail());
		email.tos = lista;

		// Da ottobre 2017, l'Emilia Romagna non vuole riferimenti alla parola "atipico" nelle mail.
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			email.subject = "Consulenza per il lavoro - " + context
					+ " - Conferma acquisizione della tua richiesta di consulenza";
			email.messageBody = "Gentile " + pratica.getMsgMessaggio().getPfPrincipalFrom().getNome() + " "
					+ pratica.getMsgMessaggio().getPfPrincipalFrom().getCognome() + ",<br/><br/>"
					+ "grazie di esserti rivolto al servizio di Consulenza per il lavoro. <br/>"
					+ "La tua richiesta sta per essere presa in carico dagli esperti del Centro per l'Impiego. <br/>"
					+ "I tempi di risposta variano a seconda della complessit&agrave; del quesito, tuttavia non dovrebbero superare la settimana lavorativa.<br/><br/>"
					+ "Cordiali saluti, <br/>Il Coordinamento della Consulenza per il lavoro " + tipoConsulenza;
		} else {
			email.subject = "Consulenza per il lavoro " + tipoConsulenza + " - " + context
					+ " - Conferma acquisizione della tua richiesta di consulenza";
			email.messageBody = "Gentile " + pratica.getMsgMessaggio().getPfPrincipalFrom().getNome() + " "
					+ pratica.getMsgMessaggio().getPfPrincipalFrom().getCognome() + ",<br/><br/>"
					+ "grazie di esserti rivolto al servizio di Consulenza per il lavoro " + tipoConsulenza + ". <br/>"
					+ "La tua richiesta sta per essere presa in carico dagli esperti del Centro per l'Impiego. <br/>"
					+ "I tempi di risposta variano a seconda della complessit&agrave; del quesito, tuttavia non dovrebbero superare la settimana lavorativa.<br/><br/>"
					+ "Cordiali saluti, <br/>Il Coordinamento della Consulenza per il lavoro " + tipoConsulenza;
		}

		return email;
	}

	public static EmailDTO buildEmailRispostaCittadino(MsgMessaggioAtipico pratica, String mittente) {
		EmailDTO email = new EmailDTO();
		String tipoConsulenza = null;
		if (ConstantsSingleton.DeTipoConsulenza.ATIPICO.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
			tipoConsulenza = "\"atipico\"";
		} else {
			tipoConsulenza = "P. IVA";
		}
		PfPrincipal cittadino = pratica.getMsgMessaggio().getPfPrincipalFrom();

		email.from = DEFAULT_FROM;
		List<String> lista = new ArrayList<String>();
		lista.add(cittadino.getEmail());
		email.tos = lista;

		// Da ottobre 2017, l'Emilia Romagna non vuole riferimenti alla parola "atipico" nelle mail.
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			email.subject = "Consulenza per il lavoro - " + context + ". Risposta alla tua richiesta di "
					+ "consulenza (Pratica Lavoro ID " + pratica.getIdMsgMessaggio() + " )";

			email.messageBody = "Gentile " + pratica.getMsgMessaggio().getPfPrincipalFrom().getNome() + " "
					+ pratica.getMsgMessaggio().getPfPrincipalFrom().getCognome() + ",<br/><br/>"
					+ "grazie per aver utilizzato il nostro servizio di Consulenza per il lavoro. "
					+ "Sulla tua scrivania di " + context
					+ " &egrave; disponibile la risposta al quesito da te posto.<br/><br/>"
					+ "Cordiali saluti,<br/>Centro per l'impiego di " + mittente;
		} else {
			email.subject = "Consulenza per il lavoro " + tipoConsulenza + " - " + context
					+ ". Risposta alla tua richiesta di " + "consulenza (Pratica Lavoro " + tipoConsulenza + " ID "
					+ pratica.getIdMsgMessaggio() + " )";

			email.messageBody = "Gentile " + pratica.getMsgMessaggio().getPfPrincipalFrom().getNome() + " "
					+ pratica.getMsgMessaggio().getPfPrincipalFrom().getCognome() + ",<br/><br/>"
					+ "grazie per aver utilizzato il nostro servizio di Consulenza per il lavoro atipico. "
					+ "Sulla tua scrivania di " + context
					+ " &egrave; disponibile la risposta al quesito da te posto.<br/><br/>"
					+ "Cordiali saluti,<br/>Centro per l'impiego di " + mittente;
		}

		return email;
	}

	public static EmailDTO buildEmailRispostaCoordinatore(MsgMessaggioAtipico pratica, String mittente) {
		EmailDTO email = new EmailDTO();
		String tipoConsulenza = null;
		if (ConstantsSingleton.DeTipoConsulenza.ATIPICO.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
			tipoConsulenza = "\"atipico\"";
		} else {
			tipoConsulenza = "P. IVA";
		}
		PfPrincipal coordinatore = pratica.getMsgMessaggio().getInoltrati().get(0).getPfPrincipalFrom();

		email.from = DEFAULT_FROM;
		List<String> lista = new ArrayList<String>();
		if (coordinatore.isCoordinatore()) {
			if (Utils.isMailCorretta(coordinatore.getCoordinatoreInfo().getEmail())) {
				lista.add(coordinatore.getCoordinatoreInfo().getEmail());
			}
		} else {
			if (Utils.isMailCorretta(coordinatore.getEmail())) {
				lista.add(coordinatore.getEmail());
			}
		}

		email.tos = lista;

		// Da ottobre 2017, l'Emilia Romagna non vuole riferimenti alla parola "atipico" nelle mail.
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			email.subject = "Consulenza per il lavoro - " + context + ". Risposta alla tua richiesta di "
					+ "consulenza (Pratica Lavoro ID " + pratica.getIdMsgMessaggio() + " )";

			email.messageBody = "Gentile " + coordinatore.getNome() + " " + coordinatore.getCognome() + ",<br/><br/>"
					+ "nella sezione online di Atipici e Atipiche in rete &egrave; pervenuta una nuova"
					+ "risposta:<br/><br/>" + "Oggetto richiesta: " + pratica.getMsgMessaggio().getOggetto() + "<br/>"
					+ "<br/>" + "Data/ora richiesta: " + pratica.getDtmIns() + "<br/><br/>"
					+ "Cordiali saluti,<br/>Centro per l'impiego di " + mittente;
		} else {
			email.subject = "Consulenza per il lavoro " + tipoConsulenza + " - " + context
					+ ". Risposta alla tua richiesta di " + "consulenza (Pratica Lavoro " + tipoConsulenza + " ID "
					+ pratica.getIdMsgMessaggio() + " )";

			email.messageBody = "Gentile " + coordinatore.getNome() + " " + coordinatore.getCognome() + ",<br/><br/>"
					+ "nella sezione online di Atipici e Atipiche in rete &egrave; pervenuta una nuova"
					+ "risposta:<br/><br/>" + "Oggetto richiesta: " + pratica.getMsgMessaggio().getOggetto() + "<br/>"
					+ "<br/>" + "Data/ora richiesta: " + pratica.getDtmIns() + "<br/><br/>"
					+ "Cordiali saluti,<br/>Centro per l'impiego di " + mittente;
		}

		return email;
	}

	/**
	 * Mail che viene inviata al coordinatore quando il CPI rifiuta la pratica
	 * 
	 * @param pratica
	 * @param mittente
	 * @return
	 */
	public static EmailDTO buildEmailRifiutoCoordinatore(MsgMessaggioAtipico pratica) {
		EmailDTO email = new EmailDTO();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
		String tipoConsulenza = null;
		if (ConstantsSingleton.DeTipoConsulenza.ATIPICO.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
			tipoConsulenza = "\"atipico\"";
		} else {
			tipoConsulenza = "P. IVA";
		}

		PfPrincipal coordinatore = pratica.getMsgMessaggio().getInoltrati().get(0).getPfPrincipalFrom();

		email.from = DEFAULT_FROM;
		List<String> lista = new ArrayList<String>();
		if (coordinatore.isCoordinatore()) {
			if (Utils.isMailCorretta(coordinatore.getCoordinatoreInfo().getEmail())) {
				lista.add(coordinatore.getCoordinatoreInfo().getEmail());
			}
		} else {
			if (Utils.isMailCorretta(coordinatore.getEmail())) {
				lista.add(coordinatore.getEmail());
			}
		}
		email.tos = lista;

		// Da ottobre 2017, l'Emilia Romagna non vuole riferimenti alla parola "atipico" nelle mail.
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			email.subject = "Consulenza per il lavoro - " + context + " - "
					+ "Rifiuto dell’assegnazione della Pratica ID " + pratica.getIdMsgMessaggio();

			email.messageBody = "Gentile Coordinatore,<br/><br/>" + "la pratica:<br/><br/>" + "ID: "
					+ pratica.getIdMsgMessaggio() + "<br/>" + "Provincia: "
					+ pratica.getDeProvincia().getDenominazione() + "<br/>" + "Data/ora richiesta: "
					+ df.format(pratica.getDtmIns()) + "<br/>&egrave; stata rifiutata dall'Operatore."
					+ "<br/><br/>Cordiali saluti,<br/>Servizio assistenza tecnica<br/>Consulenza per il lavoro.";
		} else {
			email.subject = "Consulenza per il lavoro " + tipoConsulenza + " - " + context + " - "
					+ "Rifiuto dell’assegnazione della Pratica ID " + pratica.getIdMsgMessaggio();

			email.messageBody = "Gentile Coordinatore,<br/><br/>" + "la pratica:<br/><br/>" + "ID: "
					+ pratica.getIdMsgMessaggio() + "<br/>" + "Provincia: "
					+ pratica.getDeProvincia().getDenominazione() + "<br/>" + "Data/ora richiesta: "
					+ df.format(pratica.getDtmIns()) + "<br/>&egrave; stata rifiutata dall'Operatore."
					+ "<br/><br/>Cordiali saluti,<br/>Servizio assistenza tecnica<br/>Consulenza per il lavoro "
					+ tipoConsulenza;
		}

		return email;
	}

	/**
	 * Mail da inviare al rittadino in caso di risposta negativa del coordinatore
	 * 
	 * @param pratica
	 * @param mittente
	 * @return
	 */
	public static EmailDTO buildEmailRifiutoCittadino(MsgMessaggioAtipico pratica) {
		EmailDTO email = new EmailDTO();
		String tipoConsulenza;
		if (ConstantsSingleton.DeTipoConsulenza.ATIPICO.equals(pratica.getDeTipoConsulenza().getCodTipoConsulente())) {
			tipoConsulenza = "\"atipico\"";
		} else {
			tipoConsulenza = "P. IVA";
		}

		PfPrincipal cittadino = pratica.getMsgMessaggio().getPfPrincipalFrom();
		email.from = DEFAULT_FROM;
		List<String> lista = new ArrayList<String>();
		lista.add(cittadino.getEmail());
		email.tos = lista;

		// Da ottobre 2017, l'Emilia Romagna non vuole riferimenti alla parola "atipico" nelle mail.
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			email.subject = "Consulenza per il lavoro - " + context;
			email.messageBody = "Gentile " + pratica.getMsgMessaggio().getPfPrincipalFrom().getNome() + " "
					+ pratica.getMsgMessaggio().getPfPrincipalFrom().getCognome() + ",<br/><br/>"
					+ "grazie di esserti rivolto al servizio di Consulenza per il lavoro.<br/><br/>"
					+ "Siamo spiacenti, ma la tua richiesta / la tua tipologia di lavoro non rientra tra quelle gestite nell'ambito di questo servizio di consulenza."
					+ "<br/><br/>Cordiali saluti,<br/>Il Coordinamento della<br/>Consulenza per il lavoro.";
		} else {
			email.subject = "Consulenza per il lavoro " + tipoConsulenza + " - " + context;
			email.messageBody = "Gentile " + pratica.getMsgMessaggio().getPfPrincipalFrom().getNome() + " "
					+ pratica.getMsgMessaggio().getPfPrincipalFrom().getCognome() + ",<br/><br/>"
					+ "grazie di esserti rivolto al servizio di Consulenza per il lavoro " + tipoConsulenza
					+ ".<br/><br/>"
					+ "Siamo spiacenti, ma la tua richiesta / la tua tipologia di lavoro non rientra tra quelle gestite nell'ambito di questo servizio di consulenza."
					+ "<br/><br/>Cordiali saluti,<br/>Il Coordinamento della<br/>Consulenza per il lavoro "
					+ tipoConsulenza;
		}

		return email;
	}

	public static EmailDTO buildRegistrationEmailAtipici(RegisterDTO register, boolean insPwd) {
		return buildRegistrationEmailAtipici(register, insPwd, null);
	}

	public static EmailDTO buildRegistrationEmailAtipici(RegisterDTO register, boolean insPwd, String customTo) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;

		if (customTo != null && !customTo.isEmpty()) {
			email.tos.add(customTo);
		} else {
			email.tos.add(register.getEmail());
		}

		email.subject = "Conferma registrazione nuovo account su " + context;
		String link;
		try {
			link = ConstantsSingleton.MYACCOUNT_URL + "/register/confirm/"
					+ EmailDTO.escapeCharacters(register.getUsername()) + "/"
					+ EmailDTO.escapeCharacters(register.getEmail()) + "/" + register.getActivateToken();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		String utente = "";
		if (insPwd) {
			utente = "Username: " + register.getUsername() + "<br/>" + "Password: " + register.getPassword() + "<br/>";
		} else {
			utente = "Username: " + register.getUsername() + "<br/>";
		}

		email.messageBody = "<html><body>" + "<p>Ciao " + register.getCognome() + " " + register.getNome() + ",<br/>"
				+ "Grazie per esserti registrato su MyPortal.<br/>" + "Le tue credenziali di accesso al Portale "
				+ ConstantsSingleton.BASE_URL + " sono:<br/> " + utente
				+ "Prima di procedere con l'attivazione del tuo account e' necessario compiere un ulteriore passaggio.<br/>"
				+ "<b>Nota bene</b>: devi completare questo passaggio per consentire agli amministratori di attivare il tuo account.<br/>"
				+ "Sara' necessario cliccare sul link una sola vota e il tuo account verra' automaticamente aggiornato.<br/>"
				+ "Per completare la registrazione, clicca sul collegamento qui sotto:<br/>" + "<a href=\"" + link
				+ "\">" + link + "</a><br/>"
				+ "Qualora cliccando sul link non si arrivi alla pagina di abilitazione account, "
				+ "accedere manualmente provando a copiare il link nella barra degli indirizzi del proprio browser.<br/>"
				+ "In caso di problemi con la registrazione, contatta gli amministratori all'indirizzo:<br/>"
				+ "<a href=\"mailto:" + ConstantsSingleton.EMAIL_FOOTER_REGISTER + "\">"
				+ ConstantsSingleton.EMAIL_FOOTER_REGISTER + "</a><br/><br/>" + "Saluti, l'amministrazione di <br/>"
				+ context + "</p>" + "</body></html>";

		return email;
	}

	public static EmailDTO buildRegistrationEmailFromSil(RegisterUtenteDTO dto) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(dto.getEmail());
		email.subject = "Conferma registrazione nuovo account su " + context;
		String link;
		try {
			link = ConstantsSingleton.MYACCOUNT_URL + "/register/confirm/"
					+ EmailDTO.escapeCharacters(dto.getUsername()) + "/" + EmailDTO.escapeCharacters(dto.getEmail())
					+ "/" + dto.getActivateToken();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		Map<String, String> val = new HashMap<String, String>();
		val.put("cognome", dto.getCognome());
		val.put("nome", dto.getNome());
		val.put("username", dto.getUsername());
		val.put("password", dto.getPassword());
		val.put("activationLink", link);
		val.put("mailfrom", ConstantsSingleton.EMAIL_FOOTER_REGISTER);

		InputStream is = Utils.getTemplateEmail("registrazionEmailFromSil.html");

		email.messageBody = substitutorPlaceHolder(is, val);

		return email;
	}

	public static EmailDTO buildRegistrationEmailWS(PfPrincipal principal, List<String> listaDestinatari,
			String password) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos = new ArrayList<String>();
		email.tos.add(principal.getEmail());
		for (String d : listaDestinatari) {
			email.ccs.add(d);

		}
		email.subject = "Attivazione account su " + context;
		String link;
		try {
			link = ConstantsSingleton.MYACCOUNT_URL + "/register/confirm/"
					+ EmailDTO.escapeCharacters(principal.getUsername()) + "/"
					+ EmailDTO.escapeCharacters(principal.getEmail()) + "/" + principal.getConfirmationToken();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		Map<String, String> val = new HashMap<String, String>();
		val.put("cognome", principal.getCognome());
		val.put("nome", principal.getNome());
		val.put("username", principal.getUsername());
		val.put("password", password);
		val.put("activationLink", link);
		val.put("mailfrom", ConstantsSingleton.EMAIL_FOOTER_REGISTER);

		InputStream is = Utils.getTemplateEmail("registrazionEmailWs.html");

		email.messageBody = substitutorPlaceHolder(is, val);


		return email;
	}

	public static EmailDTO buildInvioNewsletter(String fromEmail, String toEmail, List<String> listaDestinatari,
			String oggetto, String corpoMail) {
		EmailDTO email = new EmailDTO();
		email.from = fromEmail;
		email.tos = new ArrayList<String>();
		email.tos.add(toEmail);
		email.ccns = new ArrayList<String>();
		for (String emailTo : listaDestinatari) {
			if (Utils.isMailCorretta(emailTo)) {
				email.ccns.add(emailTo);
			}
		}

		email.subject = oggetto;
		email.messageBody = corpoMail;

		return email;
	}

	public static EmailDTO buildRegistrationEmailYg(RegisterUtenteDTO registerUtenteDTO, Boolean flgAbilitato) {

		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(registerUtenteDTO.getEmail());
		email.subject = "Garanzia Giovani: registrazione nuovo account su " + context;

		String link;
		try {
			link = ConstantsSingleton.MYACCOUNT_URL + "/register/confirm/"
					+ EmailDTO.escapeCharacters(registerUtenteDTO.getUsername()) + "/"
					+ EmailDTO.escapeCharacters(registerUtenteDTO.getEmail()) + "/"
					+ registerUtenteDTO.getActivateToken();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		if (!flgAbilitato) {
			Map<String, String> val = new HashMap<String, String>();
			if (ConstantsSingleton.COD_REGIONE != ConstantsSingleton.COD_REGIONE_TRENTO) {
				val.put("cognome", registerUtenteDTO.getCognome());
				val.put("nome", registerUtenteDTO.getNome());
				val.put("username", registerUtenteDTO.getUsername());
				val.put("password", registerUtenteDTO.getPassword());
				val.put("activationLink", link);
				val.put("mailfrom", ConstantsSingleton.EMAIL_FOOTER_REGISTER);
			} else {
				val.put("username", registerUtenteDTO.getUsername());
				val.put("password", registerUtenteDTO.getPassword());
				val.put("activationLink", link);
				val.put("mailfrom", ConstantsSingleton.EMAIL_FOOTER_REGISTER);
			}

			InputStream is = Utils.getTemplateEmail("registrazionEmailYgDaAbilitare.html");

			email.messageBody = substitutorPlaceHolder(is, val);
		} else {
			Map<String, String> val = new HashMap<String, String>();
			if (ConstantsSingleton.COD_REGIONE != ConstantsSingleton.COD_REGIONE_TRENTO) {
				val.put("cognome", registerUtenteDTO.getCognome());
				val.put("nome", registerUtenteDTO.getNome());
				val.put("mailfrom", ConstantsSingleton.EMAIL_FOOTER_REGISTER);
			} else {
				val.put("mailfrom", ConstantsSingleton.EMAIL_FOOTER_REGISTER);
			}

			InputStream is = Utils.getTemplateEmail("registrazionEmailYgAbilitato.html");

			email.messageBody = substitutorPlaceHolder(is, val);
		}

		return email;
	}

	public static EmailDTO buildEmailDelegaTirocini(DoTirocini doTirocini, String emailCpiSend) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataFormattata = dateFormat.format(doTirocini.getDataRichiesta());
		String luogo = "";
		String titoloStudio = "";
		String personaRif = "";
		String mansione = "";
		String mansioneMin = "";
		if (doTirocini.getDeComune() != null) {
			luogo += " - nel comune di " + doTirocini.getDeComune().getDenominazione();
		}

		if (doTirocini.getDeProvincia() != null) {
			luogo += " - nella provincia di " + doTirocini.getDeProvincia().getDenominazione();
		}

		if (doTirocini.getDeTitolo() != null) {
			titoloStudio = doTirocini.getDeTitolo().getDescrizione();
		}

		if (doTirocini.getNomeRif() != null) {
			personaRif += doTirocini.getNomeRif();
		}
		if (doTirocini.getCognomeRif() != null) {
			personaRif += doTirocini.getCognomeRif();
		}

		if (doTirocini.getDeMansione() != null) {
			mansione = doTirocini.getDeMansione().getDescrizione();
		}
		if (doTirocini.getDeMansioneMin() != null) {
			mansioneMin = doTirocini.getDeMansioneMin().getDescrizione();
		}

		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;
		email.tos.add(emailCpiSend);
		email.subject = "Richiesta preselezione tirocinanti su " + context;
		email.messageBody = "<html><body>" + " <p>" + "		Il sottoscritto <u>" + doTirocini.getPfPrincipal().getNome()
				+ " " + doTirocini.getPfPrincipal().getCognome() + "</u>, in qualita' di <u>"
				+ doTirocini.getTipoDelegatoRifAzienda() + "</u>" + "		dell'Azienda <u>"
				+ doTirocini.getPfPrincipal().getAziendaInfo().getRagioneSociale()
				+ "</u>, <br /> chiede al vostro Servizio per l'Impiego di effettuare "
				+ " una ricerca tra i giovani iscritti al Piano Garanzia Giovani  "
				+ " che abbiano requisiti idonei ad essere ospitati all'interno di un tirocinio dalle seguenti caratteristiche:"
				+ "	</p>" + "	" + "	<ul>" + "		<li><span>N. di posizioni richieste:</span>"
				+ doTirocini.getNumFigureProf() + "</li>" + "		<li><span>Luogo di lavoro:</span> " + luogo
				+ "</li>" + "		<li><span>Qualifica SRQ:</span> " + doTirocini.getDeQualificaSrq().getDescrizione()
				+ "</li>" + "		<li><span>Gruppo professionale:</span> " + mansione + "</li>"
				+ "		<li><span>Qualifica ISTAT:</span> " + mansioneMin + "</li>"
				+ "		<li><span>Titolo di studio:</span> " + titoloStudio + "</li>" + "	</ul>" + "" + ""
				+ "		<p>" + "			Richiede quindi che il Soggetto Promotore <u>" + doTirocini.getRagSocRif()
				+ "</u> facente parte dell'elenco di cui all'Allegato 3 "
				+ "	della D.G.R . 985/2014, e da me autorizzato a effettuare le attivita' di preselezione per la ricerca indicata, "
				+ "	riceva gli elenchi dei Curricula dei giovani da Voi estratti secondo i parametri indicati."
				+ "		</p>"
				+ "		<p>Si riportano di seguito, per completezza, i dati di riferimento del Soggetto promotore indicato:</p>"
				+ "	" + "		<ul>" + "			<li><span>Ragione Sociale:</span>" + doTirocini.getRagSocRif()
				+ "</li>" + "			<li><span>Persona di riferimento:</span>" + personaRif + " </li>"
				+ "			<li><span>Indirizzo E-mail:</span>" + doTirocini.getEmailRif() + "</li>" + "		 </ul>"
				+ " <p> " + "		Data <strong>" + dataFormattata + "</strong>" + "	</p>" + " </body></html>";

		return email;
	}

	private static String getTextMailFromTemplate(InputStream is) {
		BufferedReader reader;
		String testo = "";
		try {
			InputStreamReader r = new InputStreamReader(is);
			reader = new BufferedReader(r);

			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			testo = sb.toString();
		} catch (Exception e) {
			log.error("errore nel recupero del template mail html");
		}
		return testo;
	}

	private static String substitutorPlaceHolder(InputStream is, Map<String, String> values) {
		String testoMail = getTextMailFromTemplate(is);
		return substitutorPlaceHolder(testoMail, values);
	}

	private static String substitutorPlaceHolder(String testoMail, Map<String, String> mapRigaVacancy) {
		StrSubstitutor sub = new StrSubstitutor(mapRigaVacancy, "{{", "}}");
		return sub.replace(testoMail);
	}

	public static EmailDTO buildRegistrationEmailCertificatore(RegisterDTO register, boolean insPwd, String customTo) {
		EmailDTO email = new EmailDTO();
		email.from = DEFAULT_FROM;

		if (customTo != null && !customTo.isEmpty()) {
			email.tos.add(customTo);
		} else {
			email.tos.add(register.getEmail());
		}

		email.subject = "Conferma registrazione nuovo account su " + context;
		String link;
		try {
			link = ConstantsSingleton.MYACCOUNT_URL + "/register/confirm/"
					+ EmailDTO.escapeCharacters(register.getUsername()) + "/"
					+ EmailDTO.escapeCharacters(register.getEmail()) + "/" + register.getActivateToken();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		String utente = "";
		if (insPwd) {
			utente = "Username: " + register.getUsername() + "<br/>" + "Password: " + register.getPassword() + "<br/>";
		} else {
			utente = "Username: " + register.getUsername() + "<br/>";
		}

		email.messageBody = "<html><body>" + "<p>Gentile utente, <br/>"
				+ "da lunedi 16/03/2015 sara' attiva la nuova piattaforma per la gestione dei tirocini, <br/>"
				+ "attraverso la quale e' possibile procedere alla compilazione online di tutte le informazioni che attualmente vengono redatte attraverso appositi moduli cartacei, <br/>"
				+ "all'interno del portale regionale dei servizi per il lavoro - Lavoro per Te. <br/>"
				+ ConstantsSingleton.BASE_URL + "<br/>Per poter accedere alle funzionalita' della nuova piattaforma, "
				+ "cosi' da consentire l'invio alla Regione della convenzione e del progetto formativo per l'attivazione di un tirocinio, "
				+ "dovrai accedere a Lavoro per Te con le credenziali che ti sono state create come soggetto accreditato all'erogazione del Sistema Regionale di Formalizzare e Certificazione delle Competenze:<br/> "
				+ utente
				+ "In quanto soggetto certificatore potrai visualizzare i progetti formativi, e le relative convenzioni, <br/>"
				+ "per i quali sei stato indicato come soggetto certificatore, verificarne i contenuti con il soggetto promotore e dare la tua approvazione.<br/>"
				+ "Puoi trovare maggiori informazioni sui tirocini all’indirizzo http://formazionelavoro.regione.emilia-romagna.it/tirocini <br/>"
				+ "Prima di procedere con l'attivazione del tuo account e' necessario compiere un ulteriore passaggio.<br/>"
				+ "<b>Nota bene</b>: devi completare questo passaggio per consentire agli amministratori di attivare il tuo account.<br/>"
				+ "Sara' necessario cliccare sul link una sola vota e il tuo account verra' automaticamente aggiornato.<br/>"
				+ "Per completare la registrazione, clicca sul collegamento qui sotto:<br/>" + "<a href=\"" + link
				+ "\">" + link + "</a><br/>"
				+ "Qualora cliccando sul link non si arrivi alla pagina di abilitazione account, "
				+ "accedere manualmente provando a copiare il link nella barra degli indirizzi del proprio browser.<br/>"
				+ "In caso di problemi con la registrazione, contatta gli amministratori all'indirizzo:<br/>"
				+ "<a href=\"mailto:" + ConstantsSingleton.EMAIL_FOOTER_REGISTER + "\">"
				+ ConstantsSingleton.EMAIL_FOOTER_REGISTER + "</a><br/><br/>" + "Saluti, l'amministrazione di <br/>"
				+ context + "</p>" + "</body></html>";

		return email;
	}

	public static EmailDTO buildSegnalaCandidaturaEmail(UtenteDTO utenteDTO, String nominativoAmico, String emailAmico,
			Integer idVacancy, String titleVacancy) {
		String urlDettVacancy = ConstantsSingleton.getWelcomepageEndpoint() + "/vacancy/view/" + idVacancy;
		EmailDTO emailDTO = new EmailDTO();
		emailDTO.setFrom(ConstantsSingleton.getEmailNotificaTrovaLavoro());
		emailDTO.setSubject("Segnalazione annuncio");
		emailDTO.tos.add(emailAmico);

		Map<String, String> val = new HashMap<String, String>();
		val.put("nominativoAmico", nominativoAmico);
		val.put("vacancyLink", urlDettVacancy);
		val.put("vacancyTitle", titleVacancy);
		val.put("registrationLink", ConstantsSingleton.BASE_URL);
		val.put("mittente", utenteDTO.getNome() + " " + utenteDTO.getCognome());
		InputStream is = Utils.getTemplateEmail("segnalaVacancyEmail.html");
		emailDTO.messageBody = substitutorPlaceHolder(is, val);
		return emailDTO;
	}

	public static EmailDTO buildContattaCandidatoEmail(String dest, String subj, String payload) {
		
		EmailDTO emailDTO = new EmailDTO();
		emailDTO.setFrom(CANDIDATO_FROM);
		emailDTO.setSubject(subj);
		emailDTO.tos.add(dest);

		Map<String, String> val = new HashMap<String, String>();
		val.put("payload", payload); 
		 
		InputStream is = Utils.getTemplateEmail("contattaCandidatoEmail.html");
		emailDTO.messageBody = substitutorPlaceHolder(is, val);
		return emailDTO;
	}

}
