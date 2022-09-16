package it.eng.sil.myaccount.controller.mbeans.register;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import it.eng.sil.base.enums.DeSistemaEnum;
import it.eng.sil.myaccount.controller.mbeans.AbstractBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.auth.GamificationClientMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.decodifiche.DeComuneMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.UtenteInfoEJB;
import it.eng.sil.myaccount.model.ejb.stateless.utils.EmailManager;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.utils.Utils;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;

@ManagedBean(name = "registerCittadino")
@ViewScoped
public class RegisterCittadinoBean extends AbstractBackingBean implements Serializable {

	private static final long serialVersionUID = 3244337317831294533L;

	private UtenteInfo untenteInfo;

	@EJB
	private DeComuneMyAccountEJB deComuneEJB;

	@EJB
	private DeProvinciaEJB deProvinciaEJB;

	@EJB
	private EmailManager emailManager;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@EJB
	private UtenteInfoEJB utenteInfoEJB;

	@EJB
	private GamificationClientMyAccountEJB gamificationClientEJB;

	@EJB
	ConstantsSingleton costanti;

	private Boolean editProvincia;

	private static final String CARTA_PER_TITOLO_SOGGIORNO_SATE = "5";
	private static final String RINNOVO_TITOLO_SOGGIORNO_SATE = "3";
	private static final String INATTESA_TITOLO_SOGGIORNO_SATE = "4";

	private Boolean rinnovoTitoloSoggiorno;

	private Boolean acceptTerms;
	private Boolean inRinnovoTip;
	private Boolean inAttesaPerTip;
	private Boolean cartaPerTip;

	@Override
	protected void initPostConstruct() {
		untenteInfo = new UtenteInfo();
		untenteInfo.setPfPrincipal(new PfPrincipal());
		if (costanti.isUmbria())
			untenteInfo.getPfPrincipal().setFlagAbilitaPec(false);
	}

	public void canAbilitatoServizi() {
		if (editProvincia != null && editProvincia) {
			untenteInfo.getPfPrincipal().setFlagAbilitatoServizi(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("PF('cantAbilitatoServiziWV').show()");
		} else if (editProvincia == null) {
			untenteInfo.getPfPrincipal().setFlagAbilitatoServizi(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("PF('cantAbilitatoServiziWV').show()");
		}
	}

	public void comuneDomicilioChanged() {
		if (deComuneEJB.comuneDomicilioInRegione(untenteInfo.getDeComuneDomicilio())) {
			untenteInfo.setDeProvincia(untenteInfo.getDeComuneDomicilio().getDeProvincia());
			editProvincia = false;
		} else {
			editProvincia = true;
			untenteInfo.getPfPrincipal().setFlagAbilitatoServizi(Boolean.FALSE);
		}
	}

	public void titoloSoggiornoChanged() {
		String codTitoloSoggiorno = untenteInfo.getDocumentoSoggiorno().getCodTitoloSoggiorno();
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

	public boolean cfObbligatorio(){
		return constantsSingleton.isCfObbligatorio();
	}
	public String save() {
		log.debug("Registrazione cittadino: save()");
		PfPrincipal pfPrincipalRegister = null;
		try {
			boolean isLavoroPerTeInstallato = checkAbilitazioneSistemaVisibile(DeSistemaEnum.LXTE.toString());
			pfPrincipalRegister = untenteInfo.getPfPrincipal();
			pfPrincipalRegister.setConfirmationToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));

			if (!constantsSingleton.isAbilitatoServAmministrativi()) {
				pfPrincipalRegister.setFlagAbilitatoServizi(false);
			}

			if (!pfPrincipalRegister.getFlagAbilitatoServizi()) {
				pfPrincipalRegister.setFlagAbilitaPec(null);
			}

			if (pfPrincipalRegister.getFlagAbilitatoServizi() && pfPrincipalRegister.getFlagAbilitaPec()) {
				pfPrincipalRegister.setRichiestaRegForteToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
			}

			Integer idPfPrincipalNew = utenteInfoEJB.register(pfPrincipalRegister, untenteInfo, false,
					isLavoroPerTeInstallato);
			log.info("Registrato cittadino con ID: " + idPfPrincipalNew);
			untenteInfo.getPfPrincipal().setIdPfPrincipal(idPfPrincipalNew);
			untenteInfo.setIdPfPrincipal(idPfPrincipalNew);

			// Assegno all'utente un badge di completamento parziale del profilo
			gamificationClientEJB.assignProfiloCompletoBadge(untenteInfo.getIdPfPrincipal(),
					gamificationClientEJB.calcolaProfiloCompletoValueBadge(untenteInfo));

			// Se l'utente ha già fatto richiesta di abilitazione ai servizi, gli assegno un badge parziale.
			if (untenteInfo.getPfPrincipal().getFlagAbilitaPec() != null) {
				gamificationClientEJB.assignServiziAmministrativiBadge(untenteInfo.getIdPfPrincipal(),
						ConstantsSingleton.Gamification.BADGE_HALF);
			}

			// Mando una mail di attivazione.
			String codProvinciaRiferimento = untenteInfo.getDeProvincia() == null ? null
					: untenteInfo.getDeProvincia().getCodProvincia();
			String activationLink = constantsSingleton.getMyAccountURL() + "/register/confirm/"
					+ pfPrincipalRegister.getUsername() + "/" + pfPrincipalRegister.getEmail() + "/"
					+ pfPrincipalRegister.getConfirmationToken();
			Boolean checkEmail = emailManager.sendRegisterCittadino(pfPrincipalRegister.getNome(),
					pfPrincipalRegister.getUsername(), activationLink, pfPrincipalRegister.getEmail(),
					codProvinciaRiferimento);

			if (!checkEmail) {
				return null;
			} else {
				return "/public/register/success.xhtml?faces-redirect=true&email=" + pfPrincipalRegister.getEmail();
			}

		} catch (Exception e) {
			log.error("Save went wrong!", e);
			return null;
		}
	}

	/**
	 * Per ora il messaggio di help sulla email viene mostrato solo in Umbria.
	 */
	public boolean mostraEmailHelp() {
		return constantsSingleton.isUmbria();
	}

	/**
	 * Per ora alcuni campi sono obbligatori solo per l'Umbria.
	 */
	public boolean campiObbligatoriUmbria() {
		return constantsSingleton.isUmbria();
	}

	public UtenteInfo getUntenteInfo() {
		return untenteInfo;
	}

	public void setUntenteInfo(UtenteInfo untenteInfo) {
		this.untenteInfo = untenteInfo;
	}

	public Boolean getEditProvincia() {
		return editProvincia;
	}

	public void setEditProvincia(Boolean editProvincia) {
		this.editProvincia = editProvincia;
	}

	public Boolean getRinnovoTitoloSoggiorno() {
		return rinnovoTitoloSoggiorno;
	}

	public void setRinnovoTitoloSoggiorno(Boolean rinnovoTitoloSoggiorno) {
		this.rinnovoTitoloSoggiorno = rinnovoTitoloSoggiorno;
	}

	public Boolean getAcceptTerms() {
		return acceptTerms;
	}

	public void setAcceptTerms(Boolean acceptTerms) {
		this.acceptTerms = acceptTerms;
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

	public String getLoginURL() {
		return constantsSingleton.getPortaleURL();
	}

	public String currentRegione() {
		return constantsSingleton.getCodRegione();
	}
}
