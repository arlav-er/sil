package it.eng.sil.myaccount.controller.mbeans.register;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.sil.base.enums.DeSistemaEnum;
import it.eng.sil.myaccount.controller.mbeans.AbstractBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.myaccount.model.ejb.stateless.sare.UtenteAziendaSareEJB;
import it.eng.sil.myaccount.model.ejb.stateless.utils.EmailManager;
import it.eng.sil.myaccount.model.enums.TipoAbilitato;
import it.eng.sil.myaccount.model.enums.TipoDelegato;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.utils.Utils;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeTipoDelegato;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoDelegatoEJB;

@ManagedBean(name = "registerAzienda")
@ViewScoped
public class RegisterAziendaBean extends AbstractBackingBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private DeTipoDelegatoEJB delegatoEJB;

	@EJB
	AziendaInfoEJB aziendaInfoEJB;

	@EJB
	EmailManager emailManager;

	@EJB
	UtenteAziendaSareEJB utenteAziendaSareEJB;

	private DeTipoDelegato soggettoIntermediazione;
	private DeTipoDelegato promotoreTirocini;

	private AziendaInfo aziendaInfo;

	// tipo richiedente Show/Hide game
	private Boolean editTipoDelegato;
	private Boolean editSoggettoAbilitato;
	private Boolean editAgenziaSomministrazione;
	private Boolean showAgenziaSomministrazioneSection;
	private Boolean showDatiSoggettoAbilitatoSection;

	private Boolean acceptTerms;

	public AziendaInfo getAziendaInfo() {
		return aziendaInfo;
	}

	public void setAziendaInfo(AziendaInfo aziendaInfo) {
		this.aziendaInfo = aziendaInfo;
	}

	public Boolean getEditTipoDelegato() {
		return editTipoDelegato;
	}

	public void setEditTipoDelegato(Boolean editTipoDelegato) {
		this.editTipoDelegato = editTipoDelegato;
	}

	public Boolean getEditSoggettoAbilitato() {
		return editSoggettoAbilitato;
	}

	public void setEditSoggettoAbilitato(Boolean editSoggettoAbilitato) {
		this.editSoggettoAbilitato = editSoggettoAbilitato;
	}

	public Boolean getEditAgenziaSomministrazione() {
		return editAgenziaSomministrazione;
	}

	public void setEditAgenziaSomministrazione(Boolean editAgenziaSomministrazione) {
		this.editAgenziaSomministrazione = editAgenziaSomministrazione;
	}

	public Boolean getShowAgenziaSomministrazioneSection() {
		return showAgenziaSomministrazioneSection;
	}

	public void setShowAgenziaSomministrazioneSection(Boolean showAgenziaSomministrazioneSection) {
		this.showAgenziaSomministrazioneSection = showAgenziaSomministrazioneSection;
	}

	public Boolean getShowDatiSoggettoAbilitatoSection() {
		return showDatiSoggettoAbilitatoSection;
	}

	public void setShowDatiSoggettoAbilitatoSection(Boolean showDatiSoggettoAbilitatoSection) {
		this.showDatiSoggettoAbilitatoSection = showDatiSoggettoAbilitatoSection;
	}

	public Boolean getAcceptTerms() {
		return acceptTerms;
	}

	public void setAcceptTerms(Boolean acceptTerms) {
		this.acceptTerms = acceptTerms;
	}

	@Override
	protected void initPostConstruct() {
		aziendaInfo = new AziendaInfo();
		aziendaInfo.setPfPrincipal(new PfPrincipal());
		acceptTerms = Boolean.FALSE;

	}

	public void tipoAbilitatoChanged() {
		String val = aziendaInfo.getDeTipoAbilitato().getCodTipoAbilitato();
		if (val == null) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaInfo.setDeTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (TipoAbilitato.DATORE_PRIVATO.getCodice().equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaInfo.setDeTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (TipoAbilitato.DATORE_PUBBLICO.getCodice().equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaInfo.setDeTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (TipoAbilitato.AGENZIA_SOMMINISTRAZIONE.getCodice().equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = true;
			aziendaInfo.setDeTipoDelegato(null);
			showAgenziaSomministrazioneSection = true;
			showDatiSoggettoAbilitatoSection = false;
		} else if (TipoAbilitato.SOGGETTO_ABILITATO.getCodice().equals(val)) {
			editTipoDelegato = true;
			editSoggettoAbilitato = true;
			editAgenziaSomministrazione = false;
			aziendaInfo.setDeTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = true;
		} else if (TipoAbilitato.AGENZIA_LAVORO.getCodice().equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;

			if (soggettoIntermediazione == null) {
				try {
					soggettoIntermediazione = delegatoEJB.findById(TipoDelegato.SOGGETTO_INTERMEDIAZIONE.getCodice());
				} catch (MyCasNoResultException e) {
					log.error("Can't find TipoDelegato with id: " + TipoDelegato.SOGGETTO_INTERMEDIAZIONE.getCodice(),
							e);
				}
			}

			aziendaInfo.setDeTipoDelegato(soggettoIntermediazione);

		} else if (TipoAbilitato.SOGGETTO_TIROCINI.getCodice().equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;

			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;

			if (promotoreTirocini == null) {
				try {
					promotoreTirocini = delegatoEJB.findById(TipoDelegato.PROMOTORE_TIROCINI.getCodice());
				} catch (MyCasNoResultException e) {
					log.error("Can't find TipoDelegato with id: " + TipoDelegato.PROMOTORE_TIROCINI.getCodice(), e);
				}
			}

			aziendaInfo.setDeTipoDelegato(promotoreTirocini);
		} else {
			log.error("Errore nella gestione del tipo delegato. Tipo '" + val + "' sconosciuto.");
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaInfo.setDeTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		}
	}

	public String currentRegione() {
		return constantsSingleton.getCodRegione();
	}

	public String save() {
		log.debug("save called");
		PfPrincipal pfPrincipalRegister = null;

		try {
			boolean isLavoroPerTeInstallato = checkAbilitazioneSistemaVisibile(DeSistemaEnum.LXTE.toString());
			pfPrincipalRegister = aziendaInfo.getPfPrincipal();
			pfPrincipalRegister.setFlagAbilitatoServizi(false);
			pfPrincipalRegister.setNome(aziendaInfo.getNomeRic());
			pfPrincipalRegister.setCognome(aziendaInfo.getCognomeRic());
			pfPrincipalRegister.setConfirmationToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));

			if (!constantsSingleton.isAbilitatoSare()) {
				pfPrincipalRegister.setFlagAbilitatoSare(false);
			}

			aziendaInfo.setFlagValida(false);

			Integer idPfPrincipal = aziendaInfoEJB.register(pfPrincipalRegister, aziendaInfo, false,
					isLavoroPerTeInstallato, editSoggettoAbilitato, editAgenziaSomministrazione);

			log.info("Registrata azienda con ID: " + idPfPrincipal);
			// inserimento portlets tramite chiamata a servizio rest (solo se il
			// sistema myportal è installato)
			// if (isLavoroPerTeInstallato) {
			// aziendaInfoEJB.addPortlet(idPfPrincipal);
			// }

			String codProvinciaRiferimento = aziendaInfo.getDeProvincia() == null ? null : aziendaInfo.getDeProvincia()
					.getCodProvincia();
			String activationLink = constantsSingleton.getMyAccountURL() + "/register/confirm/"
					+ pfPrincipalRegister.getUsername() + "/" + pfPrincipalRegister.getEmail() + "/"
					+ pfPrincipalRegister.getConfirmationToken();
			Boolean checkEmail = emailManager.sendRegisterAzienda(pfPrincipalRegister.getNome(),
					pfPrincipalRegister.getUsername(), activationLink, pfPrincipalRegister.getEmail(),
					codProvinciaRiferimento);

			if (!checkEmail) {
				return null;
			} else {
				return "/public/register/success.xhtml?faces-redirect=true&email=" + pfPrincipalRegister.getEmail();
			}
		} catch (EJBException | MyCasNoResultException e) {
			String msgko = utils.getUiProperty("msg.insert.ko");
			addJSDangerMessage(msgko);

			log.error("Errore salvataggio azienda!", e);
			return null;
		}

	}

	public String getLoginURL() {
		return constantsSingleton.getPortaleURL();
	}
}
