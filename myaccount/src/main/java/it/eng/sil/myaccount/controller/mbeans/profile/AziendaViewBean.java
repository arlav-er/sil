package it.eng.sil.myaccount.controller.mbeans.profile;

import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpProfilaturaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.sare.UtenteAziendaSareEJB;
import it.eng.sil.myaccount.model.enums.TipoAbilitato;
import it.eng.sil.myaccount.utils.Utils;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoAbilitatoEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoDelegatoEJB;
import it.eng.sil.mycas.utils.ProfilaturaCryptoUtil;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean
public class AziendaViewBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = -4330843777739139874L;

	protected static Log log = LogFactory.getLog(AziendaViewBean.class);

	@EJB
	private AziendaInfoEJB aziendaInfoEJB;

	@EJB
	private DeComuneEJB deComuneEJB;

	@EJB
	private DeProvinciaEJB deProvinciaEJB;

	@EJB
	private DeTipoAbilitatoEJB deTipoAbilitatoEJB;

	@EJB
	private DeTipoDelegatoEJB deTipoDelegatoEJB;

	@EJB
	private PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@EJB
	private UtenteAziendaSareEJB utenteAziendaSareEJB;

	@EJB
	private GpProfilaturaMyAccountEJB gpProfilaturaEJB;

	private AziendaInfo aziendaInfo;

	private Boolean showAgenziaSomministrazioneSection;
	private Boolean showDatiSoggettoAbilitatoSection;
	private String urlStampaSare;

	@Override
	protected void initPostConstruct() {
		loadUser();

		// Controllo se l'utente ha bisogno delle profilature di default
		try {
			gpProfilaturaEJB.checkProfilatureDefault(aziendaInfo.getIdPfPrincipal(), GpDeMacroTipoEnum.AZI);
		} catch (MyCasException e) {
			log.error("ERRORE durante l'inserimento delle profilatura di default: " + e.toString());
		}

		// Inizializzo altre variabili
		extraRicSareSections();
		initUrlStampaSare();
	}

	private void initUrlStampaSare() {
		Integer idAzienda = aziendaInfo.getIdPfPrincipal();
		String tokenSecurity = Utils.buildTokenSecurity(String.valueOf(idAzienda));

		this.urlStampaSare = String.format("%s?aziendaInfoId=%d&token=%s", constantsSingleton.getPortaleURL()
				+ "/secure/rest/services/getAccreditamentoAzienda", idAzienda, tokenSecurity);
	}

	public void loadUser() {
		try {
			aziendaInfo = aziendaInfoEJB.findById(accountInfoBean.getUserInfo().getIdPfPrincipal());
			aziendaInfo.getPfPrincipal().setGpProfilaturaList(
					gpProfilaturaEJB.findForIdPfPrincipal(aziendaInfo.getIdPfPrincipal()));

			if (aziendaInfo.getDeComuneSede() != null)
				aziendaInfo.setDeComuneSede(deComuneEJB.findById(aziendaInfo.getDeComuneSede().getCodCom()));

			if (aziendaInfo.getDeComuneNascitaRic() != null)
				aziendaInfo
						.setDeComuneNascitaRic(deComuneEJB.findById(aziendaInfo.getDeComuneNascitaRic().getCodCom()));

			if (aziendaInfo.getDeComuneRichiedente() != null)
				aziendaInfo.setDeComuneRichiedente(deComuneEJB.findById(aziendaInfo.getDeComuneRichiedente()
						.getCodCom()));

			if (aziendaInfo.getDeProvincia() != null)
				aziendaInfo.setDeProvincia(deProvinciaEJB.findById(aziendaInfo.getDeProvincia().getCodProvincia()));

			if (aziendaInfo.getDeTipoAbilitato() != null)
				aziendaInfo.setDeTipoAbilitato(deTipoAbilitatoEJB.findById(aziendaInfo.getDeTipoAbilitato()
						.getCodTipoAbilitato()));

			if (aziendaInfo.getDeTipoDelegato() != null)
				aziendaInfo.setDeTipoDelegato(deTipoDelegatoEJB.findById(aziendaInfo.getDeTipoDelegato()
						.getCodTipoDelegato()));

			if (aziendaInfo.getDeComuneSedeLegale() != null)
				aziendaInfo
						.setDeComuneSedeLegale(deComuneEJB.findById(aziendaInfo.getDeComuneSedeLegale().getCodCom()));

			if (aziendaInfo.getDeComuneIscrizione() != null)
				aziendaInfo
						.setDeComuneIscrizione(deComuneEJB.findById(aziendaInfo.getDeComuneIscrizione().getCodCom()));

		} catch (MyCasNoResultException e) {
			throw new RuntimeException("Errore nel caricamento dell'azienda", e);
		}
	}

	public void extraRicSareSections() {
		if (aziendaInfo.getDeTipoAbilitato() != null) {
			String val = aziendaInfo.getDeTipoAbilitato().getCodTipoAbilitato();

			if (TipoAbilitato.AGENZIA_SOMMINISTRAZIONE.getCodice().equals(val)) {
				showAgenziaSomministrazioneSection = true;
				showDatiSoggettoAbilitatoSection = false;
			} else if (TipoAbilitato.SOGGETTO_ABILITATO.getCodice().equals(val)) {
				showAgenziaSomministrazioneSection = false;
				showDatiSoggettoAbilitatoSection = true;
			}
		}
	}

	public String getTokenProfilatura(GpRuoloGruppo ruoloGruppo) {
		try {
			return ProfilaturaCryptoUtil.cryptUserRuoloGruppoToken(ruoloGruppo.getIdGpRuoloGruppo(), aziendaInfo
					.getPfPrincipal().getUsername());
		} catch (Exception e) {
			log.error("Errore durante la creazione del token: " + e);
			addJSWarnMessage("Errore durante la creazione di un token.");
			return "N.A.";
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		UploadedFile file = event.getFile();
		aziendaInfo.setLogo(file.getContents());
		log.debug("Photo uploaded: " + file.getFileName());
		try {
			aziendaInfoEJB.merge(aziendaInfo.getIdPfPrincipal(), aziendaInfo);
		} catch (Exception e) {
			String msgko = utils.getUiProperty("msg.account.upload_photo.ko");
			log.error(msgko, e);
			addJSDangerMessage(msgko);
		}
		String msgok = utils.getUiProperty("msg.account.upload_photo.ok");
		addJSSuccessMessage(msgok);
	}

	public void checkOldPasswordValidator(FacesContext facesContext, UIComponent uiComponent, Object newValue) {
		String oldPassword = newValue.toString();
		String oldPasswordEncrypted = Utils.SHA1.encrypt(oldPassword);

		String dbUserPasswordEncrypted = aziendaInfo.getPfPrincipal().getPassWord();

		if (!dbUserPasswordEncrypted.equals(oldPasswordEncrypted)) {
			addErrorMessageToComponent("forgetP.oldPasswordNotCorrect", "changePasswordForm:oldPassword");
		}
	}

	public void changePassword() {
		try {
			pfPrincipalMyAccountEJB.changePasswordAzienda(aziendaInfo);

			String msgok = utils.getUiProperty("msg.updated");
			addJSSuccessMessage(msgok);
		} catch (Exception e) {
			String msgko = utils.getUiProperty("msg.updated.ko");
			log.error(msgko, e);
			addJSDangerMessage(msgko);
		}

	}

	public void removeProfilePic() {
		aziendaInfo.setLogo(null);
		try {
			aziendaInfoEJB.merge(aziendaInfo.getIdPfPrincipal(), aziendaInfo);
		} catch (Exception e) {
			String msgko = utils.getUiProperty("msg.account.upload_photo.ko");
			log.error(msgko, e);
			addJSDangerMessage(msgko);
		}

		String msgok = utils.getUiProperty("msg.account.upload_photo.ok");
		addJSSuccessMessage(msgok);
	}

	public String getPortaleURL() {
		return constantsSingleton.getPortaleURL();
	}

	public String getRegionePortale() {
		return constantsSingleton.getNomeRegione();
	}

	public String getUrlStampaSare() {
		return urlStampaSare;
	}

	public void setUrlStampaSare(String urlStampaSare) {
		this.urlStampaSare = urlStampaSare;
	}

	public AziendaInfo getAziendaInfo() {
		return aziendaInfo;
	}

	public void setAziendaInfo(AziendaInfo aziendaInfo) {
		this.aziendaInfo = aziendaInfo;
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
}
