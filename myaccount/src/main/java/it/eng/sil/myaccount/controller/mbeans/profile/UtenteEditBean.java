package it.eng.sil.myaccount.controller.mbeans.profile;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import it.eng.sil.base.enums.DeSistemaEnum;
import it.eng.sil.base.enums.EsitoEnum;
import it.eng.sil.base.exceptions.OtpRemoteException;
import it.eng.sil.base.pojo.EsitoTypePOJO;
import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.auth.GamificationClientMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.auth.OTPClientEJB;
import it.eng.sil.myaccount.model.ejb.stateless.decodifiche.DeComuneMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.UtenteInfoEJB;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.utils.Utils;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.profilatura.PfIdentityProvider;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;
import it.eng.sil.mycas.model.manager.decodifiche.DeCittadinanzaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeCpiEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTitoloSoggiornoEJB;
import it.eng.sil.mycas.model.manager.profilatura.PfIdentityProviderEJB;

@ViewScoped
@ManagedBean
public class UtenteEditBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = 4269765806138037831L;

	@EJB
	private UtenteInfoEJB utenteInfoEJB;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@EJB
	private DeComuneMyAccountEJB deComuneEJB;

	@EJB
	private DeProvinciaEJB deProvinciaEJB;

	@EJB
	private DeCittadinanzaEJB deCittadinanzaEJB;

	@EJB
	private DeTitoloSoggiornoEJB deTitoloSoggiornoEJB;

	@EJB
	private DeCpiEJB deCpiEJB;

	@EJB
	private PfIdentityProviderEJB pfIdentityProviderEJB;

	@EJB
	private GamificationClientMyAccountEJB gamificationClientEJB;

	@EJB
	private ConstantsSingleton constantsSingleton;

	@EJB
	private OTPClientEJB oTPClientEJB;

	// CAS tracker #3120: subito dopo una richiesta di accreditamento forte,
	// non c'è redirect ma si rimane sulla pagina di modifica profilo.
	private boolean noRedirect;
	private boolean richiestaAccreditamentoForte;

	private UtenteInfo utenteInfo;
	private PfIdentityProvider socialNetwork;

	// prevent validator from doing unnecessary check
	private String oldPecEmail;
	private String oldEmail;

	private Boolean rinnovoTitoloSoggiorno;

	private String emailAbilitazionePecCpi;
	private String emailAbilitazioneNoPecCpi;

	private String tempCellurareFirmaOTP;
	private String tempCodeFirmaOTP;

	private static final String CARTA_PER_TITOLO_SOGGIORNO_SATE = "5";
	private static final String RINNOVO_TITOLO_SOGGIORNO_SATE = "3";
	private static final String INATTESA_TITOLO_SOGGIORNO_SATE = "4";

	private Boolean inRinnovoTip;
	private Boolean inAttesaPerTip;
	private Boolean cartaPerTip;
	private Boolean cellulareFirmaOTPSent;

	private String otpRequestCode;

	@Override
	protected void initPostConstruct() {
		initUser();
		initSocialNetwork();
		if (utenteInfo.getDeProvincia() == null)
			comuneDomicilioChanged();
		titoloSoggiornoChanged();
		richiestaAccreditamentoForte = false;
		if (constantsSingleton.isUmbria())
			utenteInfo.getPfPrincipal().setFlagAbilitaPec(Boolean.FALSE);
	}

	public void initUser() {
		try {
			utenteInfo = utenteInfoEJB.loadUser(accountInfoBean.getUserInfo().getIdPfPrincipal());
			setEmailAbilitazionePecCpi(utenteInfoEJB.recuperaMailServiziOnlineCpiServiziAmministrativi(utenteInfo));
			setEmailAbilitazioneNoPecCpi(
					utenteInfoEJB.recuperaMailAbilitazioneNoPecCpiServiziAmministrativi(utenteInfo));
			oldPecEmail = utenteInfo.getEmailPEC();
			oldEmail = utenteInfo.getPfPrincipal().getEmail();
		} catch (MyCasException e) {
			log.error("ERRORE durante la initUser(): " + e);
			// TODO error handling
		}
	}

	private void initSocialNetwork() {
		try {
			socialNetwork = pfIdentityProviderEJB.findByIdPfPrincipal(utenteInfo.getIdPfPrincipal());
		} catch (MyCasException e) {
			// NON E' UN ERRORE: Vuol dire che l'utente non è associato ad un social network.
			socialNetwork = null;
		}
	}

	public void comuneDomicilioChanged() {
		if (deComuneEJB.comuneDomicilioInRegione(utenteInfo.getDeComuneDomicilio())) {
			utenteInfo.setDeProvincia(utenteInfo.getDeComuneDomicilio().getDeProvincia());
			provinciaRiferimentoChanged();
		} else {
			utenteInfo.getPfPrincipal().setFlagAbilitatoServizi(Boolean.FALSE);
		}
	}

	public void titoloSoggiornoChanged() {
		if (utenteInfo.getDocumentoSoggiorno() != null) {
			String codTitoloSoggiorno = utenteInfo.getDocumentoSoggiorno().getCodTitoloSoggiorno();
			if (constantsSingleton.isTrento()) {
				rinnovoTitoloSoggiorno = codTitoloSoggiorno.equals(RINNOVO_TITOLO_SOGGIORNO_SATE);
			}

			if (codTitoloSoggiorno.equals(RINNOVO_TITOLO_SOGGIORNO_SATE)) {
				inRinnovoTip = Boolean.TRUE;
				inAttesaPerTip = Boolean.FALSE;
				cartaPerTip = Boolean.FALSE;
			} else if (codTitoloSoggiorno.equals(INATTESA_TITOLO_SOGGIORNO_SATE)) {
				inRinnovoTip = Boolean.FALSE;
				inAttesaPerTip = Boolean.TRUE;
				cartaPerTip = Boolean.FALSE;
			} else if (codTitoloSoggiorno.equals(CARTA_PER_TITOLO_SOGGIORNO_SATE)) {
				inRinnovoTip = Boolean.FALSE;
				inAttesaPerTip = Boolean.FALSE;
				cartaPerTip = Boolean.TRUE;
			} else {
				inRinnovoTip = Boolean.FALSE;
				inAttesaPerTip = Boolean.FALSE;
				cartaPerTip = Boolean.FALSE;
			}
		}
	}

	public String edit() {
		return null;
	}

	/**
	 * Questo metodo controlla se l'utente può effettuare una richiesta di abilitazione ai servizi amministrativi,
	 * ovvero se il suo comune di domicilio è in Emilia Romagna.
	 */
	public void canAbilitatoServizi() {
		if (!deComuneEJB.comuneDomicilioInRegione(utenteInfo.getDeComuneDomicilio())) {
			RequestContext.getCurrentInstance().execute("PF('cantAbilitatoServiziWV').show()");
			richiestaAccreditamentoForte = false;
		} else {
			noRedirect = true;
		}
	}

	/**
	 * Metodo che gestisce l'upload della foto del profilo. Assegna anche un badge.
	 */
	public void handleFileUpload(FileUploadEvent event) {
		try {
			UploadedFile file = event.getFile();
			utenteInfo.setFoto(file.getContents());
			log.debug("Photo uploaded: " + file.getFileName());
			utenteInfoEJB.merge(0, utenteInfo);

			// Assegno il badge "Immagine profilo inserita" all'utente
			if (constantsSingleton.isGamificationEnabled()) {
				gamificationClientEJB.assignImmagineInseritaBadge(utenteInfo.getIdPfPrincipal(),
						ConstantsSingleton.Gamification.BADGE_COMPLETE);
			}

		} catch (Exception e) {
			String msgko = utils.getUiProperty("msg.account.upload_photo.ko");
			log.error(msgko, e);
			addJSDangerMessage(msgko);
		}

		String msgok = utils.getUiProperty("msg.account.upload_photo.ok");
		addJSSuccessMessage(msgok);
	}

	/**
	 * Questo metodo genera un token per la richiesta di abilitazione forte dell'utente. L'utente dovrà inviare questo
	 * token al CPI per email in modo che il CPI possa poi abilitarlo.
	 */
	public void generateRichiestaToken() {
		try {
			setEmailAbilitazionePecCpi(utenteInfoEJB.generateRichiestaToken(utenteInfo));
			noRedirect = false;
		} catch (MyCasException e) {
			String msgko = utils.getUiProperty("msg.updated.ko");
			log.error(msgko, e);
			addJSDangerMessage(msgko);
		}
	}

	/**
	 * fa partire una password OTP sul canale specificato
	 * 
	 * ATTENZIONE PARTE UN SMS
	 * 
	 */
	public void generateOTPRichiesta() {
		try {
			tempCellurareFirmaOTP = getRequestParameter("tempCellurareFirmaOTP");
			otpRequestCode = oTPClientEJB.createFirstOTPRequest(utenteInfo.getIdPfPrincipal(), tempCellurareFirmaOTP);

			setCellulareFirmaOTPSent(!otpRequestCode.isEmpty());
		} catch (Exception e) {
			String msgko = utils.getUiProperty("msg.updated.ko");
			log.error(msgko, e);
			addJSDangerMessage(msgko);
		}
	}

	/**
	 * Cerifica correttezza OTP inserito
	 * 
	 * @param secret
	 */
	public void replyToOTP() {
		EsitoTypePOJO isOk;
		try {
			isOk = oTPClientEJB.replyOTP(otpRequestCode, tempCodeFirmaOTP);
			boolean passed = isOk.getEsito().equals(EsitoEnum.OK);

			if (passed) {
				utenteInfo.setCellulareOTP(tempCellurareFirmaOTP);
				try {
					utenteInfoEJB.updateProfile(utenteInfo);
				} catch (MyCasException e) {
					String msgko = utils.getUiProperty("msg.updated.ko");
					log.error(msgko, e);
					addJSDangerMessage(msgko);
				}
				RequestContext.getCurrentInstance().addCallbackParam("otpSuccess", "success");
			} else if (isOk.getDescrizione().contains("errato")) {
				RequestContext.getCurrentInstance().addCallbackParam("otpSuccess", "wrongPass");
			} else if (isOk.getDescrizione().contains("scaduto")) {
				RequestContext.getCurrentInstance().addCallbackParam("otpSuccess", "scaduto");
			}
		} catch (OtpRemoteException e1) {
			RequestContext.getCurrentInstance().addCallbackParam("otpSuccess", "failed");
		}

	}

	public void resetOTPSteps() {
		tempCodeFirmaOTP = null;
		tempCellurareFirmaOTP = null;
		cellulareFirmaOTPSent = false;
	}

	/**
	 * Questo metodo restituisce TRUE se l'utente sta effettuando una richiesta di accreditamento forte o se l'ha già
	 * effettuata e sta aspettando la risposta della provincia.
	 */
	public boolean isRichiestaAccreditamentoForteInCorso() {
		return !utenteInfo.getPfPrincipal().getFlagAbilitatoServizi()
				&& (utenteInfo.getPfPrincipal().getRegistrazioneForteToken() != null
						|| utenteInfo.getPfPrincipal().getFlagAbilitaPec() != null)
				|| richiestaAccreditamentoForte;
	}

	/**
	 * Questo metodo restituisce TRUE se l'utente non può più modificare i dati relativi all'abilitazione con PEC
	 * (ovvero ha già ricevuto il token o è già stato abilitato). TODO: Per ora il metodo restituisce semplicemente
	 * FALSE perchè si è deciso che i dati sono sempre modificabili. Non lo elimino del tutto perchè più avanti potremmo
	 * cambiare idea.
	 */
	public boolean isDatiAbilitazionePecBloccati() {
		return false;
		// return utenteInfo.getPfPrincipal().getFlagAbilitatoServizi()
		// || !StringUtils.isEmptyNoBlank(utenteInfo.getPfPrincipal().getRegistrazioneForteToken())
		// || !StringUtils.isEmptyNoBlank(utenteInfo.getPfPrincipal().getRichiestaRegForteToken());
	}

	/**
	 * Conferma le modifiche all'account dell'utente e le salva sul database.
	 */
	public String sync() {
		try {
			utenteInfoEJB.updateProfile(utenteInfo);
			richiestaAccreditamentoForte = false;

			// AGGIORNAMENTO YG SUL PORTALE
			// RIPRISTINATA VERSIONE: Carmela 2014/06/25
			boolean isLavoroPerTeInstallato = checkAbilitazioneSistemaVisibile(DeSistemaEnum.LXTE.toString());
			if (isLavoroPerTeInstallato) {
				utenteInfoEJB.updateYG(utenteInfo);
			}
		} catch (MyCasException e) {
			String msgko = utils.getUiProperty("msg.updated.ko");
			log.error(msgko, e);
			addJSDangerMessage(msgko);
			return null;
		}

		// Aggiorno il badge della percentuale di completamento del profilo
		gamificationClientEJB.assignProfiloCompletoBadge(utenteInfo.getIdPfPrincipal(),
				gamificationClientEJB.calcolaProfiloCompletoValueBadge(utenteInfo));
		// TODO mostrare il badge ottenuto all'utente

		// Se l'utente ha fatto richiesta di registrazione forte ma non l'ha ancora ottenuta, gli assegno mezzo badge.
		if (!utenteInfo.getPfPrincipal().getFlagAbilitatoServizi()
				&& utenteInfo.getPfPrincipal().getFlagAbilitaPec() != null) {
			gamificationClientEJB.assignServiziAmministrativiBadge(utenteInfo.getIdPfPrincipal(),
					ConstantsSingleton.Gamification.BADGE_HALF);
			// TODO mostrare il badge ottenuto all'utente
		}

		// Se l'utente ha fatto richiesta di registrazione forte,
		// rimane su questa pagina in modo da poter recuperare il token e/o
		// l'indirizzo email del CPI.
		if (!noRedirect)
			return "/secure/profile/viewMain.xhtml?faces-redirect=true&saveSucess=yes";
		else {
			initUser();
			String msgok = utils.getUiProperty("msg.updated");
			addJSSuccessMessage(msgok);
			return null;
		}
	}

	public void changePassword() {
		try {
			pfPrincipalMyAccountEJB.changePasswordUtente(utenteInfo);
		} catch (MyCasException e) {
			log.error("ERRORE durante la changePassword(): " + e);
			// TODO Handle exception?
		}
	}

	public String getRenderCodServiziAmministrativi() {
		return Utils.getRenderCodServiziAmministrativi(utenteInfo.getCodServiziAmministrativi());
	}

	public void checkOldPasswordValidator(FacesContext facesContext, UIComponent uiComponent, Object newValue) {
		String oldPassword = newValue.toString();
		String oldPasswordEncrypted = Utils.SHA1.encrypt(oldPassword);

		String dbUserPasswordEncrypted = utenteInfo.getPfPrincipal().getPassWord();

		if (!dbUserPasswordEncrypted.equals(oldPasswordEncrypted)) {
			addErrorMessageToComponent("forgetP.oldPasswordNotCorrect", "changePasswordForm:oldPassword");
		}
	}

	public void removeProfilePic() {
		utenteInfo.setFoto(null);
		try {
			utenteInfoEJB.merge(0, utenteInfo);
		} catch (MyCasException e) {
			String msgko = utils.getUiProperty("msg.account.upload_photo.ko");
			log.error(msgko, e);
			addJSDangerMessage(msgko);
			return;
		}

		String msgok = utils.getUiProperty("msg.account.upload_photo.ok");
		addJSSuccessMessage(msgok);
	}

	/**
	 * Questo metodo genera un warning se l'utente seleziona un comune di domicilio al di fuori della sua provincia di
	 * riferimento.
	 */
	public void provinciaRiferimentoChanged() {
		emailAbilitazioneNoPecCpi = utenteInfoEJB.recuperaMailAbilitazioneNoPecCpiServiziAmministrativi(utenteInfo);
		emailAbilitazionePecCpi = utenteInfoEJB.recuperaMailServiziOnlineCpiServiziAmministrativi(utenteInfo);
		if (!utenteInfo.getDeProvincia().equals(utenteInfo.getDeComuneDomicilio().getDeProvincia())) {
			String warningMessage = utils.getUiProperty("prof.comuneVSprovWarning");
			addWarnMessage("profileEditForm:comuneDomicilio", warningMessage);
		}
	}

	public String getCurrentRegione() {
		return constantsSingleton.getCodRegione();
	}

	public UtenteInfo getUtenteInfo() {
		return utenteInfo;
	}

	public void setUtenteInfo(UtenteInfo utenteInfo) {
		this.utenteInfo = utenteInfo;
	}

	public boolean isRichiestaAccreditamentoForte() {
		return richiestaAccreditamentoForte;
	}

	public void setRichiestaAccreditamentoForte(boolean richiestaAccreditamentoForte) {
		this.richiestaAccreditamentoForte = richiestaAccreditamentoForte;
	}

	public String getOldPecEmail() {
		return oldPecEmail;
	}

	public Boolean getRinnovoTitoloSoggiorno() {
		return rinnovoTitoloSoggiorno;
	}

	public void setRinnovoTitoloSoggiorno(Boolean rinnovoTitoloSoggiorno) {
		this.rinnovoTitoloSoggiorno = rinnovoTitoloSoggiorno;
	}

	public void setOldPecEmail(String oldPecEmail) {
		this.oldPecEmail = oldPecEmail;
	}

	public String getOldEmail() {
		return oldEmail;
	}

	public void setOldEmail(String oldEmail) {
		this.oldEmail = oldEmail;
	}

	public Boolean getInRinnovoTip() {
		return inRinnovoTip;
	}

	public void setInRinnovoTip(Boolean inRinnovoTip) {
		this.inRinnovoTip = inRinnovoTip;
	}

	public Boolean getInAttesaPerTip() {
		return inAttesaPerTip;
	}

	public void setInAttesaPerTip(Boolean inAttesaPerTip) {
		this.inAttesaPerTip = inAttesaPerTip;
	}

	public Boolean getCartaPerTip() {
		return cartaPerTip;
	}

	public void setCartaPerTip(Boolean cartaPerTip) {
		this.cartaPerTip = cartaPerTip;
	}

	public String getEmailAbilitazioneNoPecCpi() {
		return emailAbilitazioneNoPecCpi;
	}

	public void setEmailAbilitazioneNoPecCpi(String emailAbilitazioneNoPecCpi) {
		this.emailAbilitazioneNoPecCpi = emailAbilitazioneNoPecCpi;
	}

	public String getEmailAbilitazionePecCpi() {
		return emailAbilitazionePecCpi;
	}

	public void setEmailAbilitazionePecCpi(String emailAbilitazionePecCpi) {
		this.emailAbilitazionePecCpi = emailAbilitazionePecCpi;
	}

	public PfIdentityProvider getSocialNetwork() {
		return socialNetwork;
	}

	public void setSocialNetwork(PfIdentityProvider socialNetwork) {
		this.socialNetwork = socialNetwork;
	}

	public Boolean getCellulareFirmaOTPSent() {
		return cellulareFirmaOTPSent;
	}

	public void setCellulareFirmaOTPSent(Boolean cellulareFirmaOTPSent) {
		this.cellulareFirmaOTPSent = cellulareFirmaOTPSent;
	}

	public String getTempCellurareFirmaOTP() {
		return tempCellurareFirmaOTP;
	}

	public void setTempCellurareFirmaOTP(String tempCellurareFirmaOTP) {
		this.tempCellurareFirmaOTP = tempCellurareFirmaOTP;
	}

	public String getTempCodeFirmaOTP() {
		return tempCodeFirmaOTP;
	}

	public void setTempCodeFirmaOTP(String tempCodeFirmaOTP) {
		this.tempCodeFirmaOTP = tempCodeFirmaOTP;
	}

}
