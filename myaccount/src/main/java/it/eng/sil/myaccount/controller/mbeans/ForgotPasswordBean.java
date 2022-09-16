package it.eng.sil.myaccount.controller.mbeans;

import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.utils.EmailManager;
import it.eng.sil.myaccount.utils.Utils;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.profilatura.PfIdentityProvider;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.manager.profilatura.PfIdentityProviderEJB;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ViewScoped
@ManagedBean
public class ForgotPasswordBean extends AbstractBackingBean implements Serializable {

	private static final long serialVersionUID = 8715025433295223647L;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@EJB
	PfIdentityProviderEJB pfIdentityProviderEJB;

	@EJB
	private EmailManager emailManager;

	private Boolean emailPhase;
	private Boolean answerPhase;
	private Boolean emailSentPhase;

	private String usernameOrPassword;
	private String question;
	private String answer;

	private PfPrincipal utente;

	@Override
	protected void initPostConstruct() {
		emailPhase = Boolean.TRUE;
		answerPhase = Boolean.FALSE;
		emailSentPhase = Boolean.FALSE;
	}

	public void emailPhaseAction() {
		List<PfPrincipal> utentiRecuperati = pfPrincipalMyAccountEJB.verifyEmailOrUsername(usernameOrPassword);

		if (utentiRecuperati == null || utentiRecuperati.isEmpty()) {
			// Se la mail non è associata a nessun utente, do un messaggio di errore.
			addErrorMessageToComponent("forgotP.usernameOrEmailNotFound", "forgotForm:email");
			return;
		}

		if (utentiRecuperati.size() > 1) {
			// Se ho recuperato più di un utente, do un messaggio di errore.
			addErrorMessageToComponent("forgotP.tooManyUsersFound", "forgotForm:email");
			return;
		}

		// Se l'utente è associato ad un social network, interrompo la procedura.
		utente = utentiRecuperati.get(0);
		try {
			PfIdentityProvider socialNetwork = pfIdentityProviderEJB.findByIdPfPrincipal(utente.getIdPfPrincipal());
			String errorMessageText = "Questo username o indirizzo E-mail è collegato ad un'utenza "
					+ socialNetwork.getCodTipoProvider();
			FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessageText,
					errorMessageText);
			FacesContext.getCurrentInstance().addMessage("forgotForm:email", errorMessage);
			return;
		} catch (MyCasException e) {
			// OK! Vuol dire che l'utente non è associato ad un social network.
		}

		question = utente.getDomanda();
		emailPhase = Boolean.FALSE;
		answerPhase = Boolean.TRUE;
		emailSentPhase = Boolean.FALSE;
	}

	public void answerPhaseAction() {
		if (answer != null && utente.getRisposta().equalsIgnoreCase(answer)) { // answer is correct
			try {
				emailPhase = Boolean.FALSE;
				answerPhase = Boolean.FALSE;

				// modifica password
				pfPrincipalMyAccountEJB.getTokenRecuperaPassword(utente);

				// Cerco la provincia di riferimento dell'utente in base al tipo.
				String codProvinciaRiferimento = null;
				if (utente.getUtenteInfo() != null && utente.getUtenteInfo().getDeProvincia() != null) {
					codProvinciaRiferimento = utente.getUtenteInfo().getDeProvincia().getCodProvincia();
				} else if (utente.getAziendaInfo() != null && utente.getAziendaInfo().getDeProvincia() != null) {
					codProvinciaRiferimento = utente.getAziendaInfo().getDeProvincia().getCodProvincia();
				}

				// Invio la mail di attivazione.
				String activationLink = constantsSingleton.getMyAccountURL() + "/changePassword/"
						+ Utils.escapeCharacters(utente.getUsername()) + "/"
						+ Utils.escapeCharacters(utente.getEmail()) + "/" + utente.getPasswordToken();
				Boolean checkEmail = emailManager.sendRecuperaPassword(utente.getNome(), utente.getUsername(),
						activationLink, utente.getEmail(), codProvinciaRiferimento);

				emailSentPhase = checkEmail;
			} catch (Exception e) {
				throw new EJBException(e);
			}
		} else {// answer is NOT correct
			addErrorMessageToComponent("forgetP.answerNotCorrect", "forgotForm:answer");
		}
	}

	public String getLoginURL() {
		return constantsSingleton.getPortaleURL();
	}

	public String getLoginSocialeURL() {
		return constantsSingleton.getResURL();
	}

	public Boolean getEmailPhase() {
		return emailPhase;
	}

	public void setEmailPhase(Boolean emailPhase) {
		this.emailPhase = emailPhase;
	}

	public Boolean getAnswerPhase() {
		return answerPhase;
	}

	public void setAnswerPhase(Boolean answerPhase) {
		this.answerPhase = answerPhase;
	}

	public Boolean getEmailSentPhase() {
		return emailSentPhase;
	}

	public void setEmailSentPhase(Boolean emailSentPhase) {
		this.emailSentPhase = emailSentPhase;
	}

	public String getUsernameOrPassword() {
		return usernameOrPassword;
	}

	public void setUsernameOrPassword(String usernameOrPassword) {
		this.usernameOrPassword = usernameOrPassword;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public PfPrincipal getUtente() {
		return utente;
	}

	public void setUtente(PfPrincipal utente) {
		this.utente = utente;
	}

}
