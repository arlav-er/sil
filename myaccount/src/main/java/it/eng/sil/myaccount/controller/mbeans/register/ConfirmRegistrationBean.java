package it.eng.sil.myaccount.controller.mbeans.register;

import it.eng.sil.myaccount.controller.mbeans.AbstractBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.UtenteInfoEJB;
import it.eng.sil.myaccount.model.ejb.stateless.utils.EmailManager;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeCpiEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class ConfirmRegistrationBean extends AbstractBackingBean implements Serializable {

	private static final long serialVersionUID = -2996073539058837444L;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@EJB
	UtenteInfoEJB utenteInfoEJB;

	@EJB
	AziendaInfoEJB aziendaInfoEJB;

	@EJB
	DeCpiEJB deCpiEJB;

	@EJB
	DeComuneEJB deComuneEJB;

	@EJB
	DeProvinciaEJB deProvinciaEJB;

	private boolean success;
	private boolean richiestaAutForteNoPec;
	private boolean codiceRichiestaAutForte;
	private boolean userAlreadyExists;
	private boolean error;
	private String codiceAttivazioneServiziAmmi;
	private String emailCpi;
	private String emailServiziOnlineCpi;
	private String emailAbilitazioneNoPecCpi;

	@EJB
	private EmailManager emailManager;

	@Override
	protected void initPostConstruct() {
	}

	public void confirm() {
		String username = getRequestParameter("username");
		String email = getRequestParameter("email");
		String token = getRequestParameter("token");
		log.info("confirm registration for " + username + " " + email + " token: " + token);

		PfPrincipal pfPrincipal = pfPrincipalMyAccountEJB.findByUsername(username);
		if (pfPrincipal == null) {
			success = Boolean.FALSE;
			error = Boolean.TRUE;
			userAlreadyExists = Boolean.FALSE;
			richiestaAutForteNoPec = Boolean.FALSE;
			codiceRichiestaAutForte = Boolean.FALSE;
			return;
		}

		if (pfPrincipal.getFlagAbilitato()) {
			success = Boolean.FALSE;
			error = Boolean.FALSE;
			userAlreadyExists = Boolean.TRUE;
			richiestaAutForteNoPec = Boolean.FALSE;
			codiceRichiestaAutForte = Boolean.FALSE;
			return;
		}

		if (token == null || !token.equals(pfPrincipal.getConfirmationToken())) {
			success = Boolean.FALSE;
			error = Boolean.TRUE;
			userAlreadyExists = Boolean.FALSE;
			codiceRichiestaAutForte = Boolean.FALSE;
			return;
		}

		// update flagabilitato utente
		try {
			success = pfPrincipalMyAccountEJB.confirmUser(pfPrincipal);

			if (!pfPrincipal.getFlagAbilitatoServizi() && pfPrincipal.getFlagAbilitaPec() != null
					&& pfPrincipal.getFlagAbilitaPec() == false) {
				richiestaAutForteNoPec = true;
			}

			if (!pfPrincipal.getFlagAbilitatoServizi() && pfPrincipal.getRichiestaRegForteToken() != null) {
				codiceRichiestaAutForte = true;
			}

			userAlreadyExists = Boolean.FALSE;
			error = Boolean.FALSE;

			if (pfPrincipal.isUtente()) {
				UtenteInfo utenteInfo = utenteInfoEJB.findById(pfPrincipal.getIdPfPrincipal());
				emailCpi = utenteInfoEJB.recuperaMailCpiServiziAmministrativi(utenteInfo);
				emailServiziOnlineCpi = utenteInfoEJB.recuperaMailServiziOnlineCpiServiziAmministrativi(utenteInfo);
				emailAbilitazioneNoPecCpi = utenteInfoEJB
						.recuperaMailAbilitazioneNoPecCpiServiziAmministrativi(utenteInfo);

				if (codiceRichiestaAutForte) {
					setCodiceAttivazioneServiziAmmi(pfPrincipal.getRichiestaRegForteToken());
				}

				String codProvinciaRiferimento = utenteInfo.getDeProvincia() == null ? null : utenteInfo
						.getDeProvincia().getCodProvincia();
				emailManager.sendConfirmCittadino(pfPrincipal.getUsername(), pfPrincipal.getEmail(),
						codProvinciaRiferimento);
			}

			if (pfPrincipal.isAzienda()) {
				AziendaInfo aziendaInfo = aziendaInfoEJB.findById(pfPrincipal.getIdPfPrincipal());
				String codProvinciaRiferimento = aziendaInfo.getDeProvincia() == null ? null : aziendaInfo
						.getDeProvincia().getCodProvincia();
				emailManager.sendConfirmAzienda(pfPrincipal.getUsername(), pfPrincipal.getEmail(),
						codProvinciaRiferimento);
			}

		} catch (MyCasException e) {
			log.error("confirm():" + e);
			success = Boolean.FALSE;
			error = Boolean.TRUE;
			userAlreadyExists = Boolean.FALSE;
			codiceRichiestaAutForte = Boolean.FALSE;

			throw new RuntimeException("Errore nella conferma della registrazione", e);
		}
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isCodiceRichiestaAutForte() {
		return codiceRichiestaAutForte;
	}

	public void setCodiceRichiestaAutForte(boolean codiceRichiestaAutForte) {
		this.codiceRichiestaAutForte = codiceRichiestaAutForte;
	}

	public boolean isUserAlreadyExists() {
		return userAlreadyExists;
	}

	public void setUserAlreadyExists(boolean userAlreadyExists) {
		this.userAlreadyExists = userAlreadyExists;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getCodiceAttivazioneServiziAmmi() {
		return codiceAttivazioneServiziAmmi;
	}

	public void setCodiceAttivazioneServiziAmmi(String codiceAttivazioneServiziAmmi) {
		this.codiceAttivazioneServiziAmmi = codiceAttivazioneServiziAmmi;
	}

	public String getLoginURL() {
		return constantsSingleton.getPortaleURL();
	}

	public String getEmailCpi() {
		return emailCpi;
	}

	public void setEmailCpi(String emailCpi) {
		this.emailCpi = emailCpi;
	}

	public boolean isRichiestaAutForteNoPec() {
		return richiestaAutForteNoPec;
	}

	public void setRichiestaAutForteNoPec(boolean richiestaAutForteNoPec) {
		this.richiestaAutForteNoPec = richiestaAutForteNoPec;
	}

	public String getEmailServiziOnlineCpi() {
		return emailServiziOnlineCpi;
	}

	public void setEmailServiziOnlineCpi(String emailServiziOnlineCpi) {
		this.emailServiziOnlineCpi = emailServiziOnlineCpi;
	}

	public String getEmailAbilitazioneNoPecCpi() {
		return emailAbilitazioneNoPecCpi;
	}

	public void setEmailAbilitazioneNoPecCpi(String emailAbilitazioneNoPecCpi) {
		this.emailAbilitazioneNoPecCpi = emailAbilitazioneNoPecCpi;
	}

}
