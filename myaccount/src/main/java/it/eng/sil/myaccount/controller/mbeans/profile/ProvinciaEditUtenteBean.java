package it.eng.sil.myaccount.controller.mbeans.profile;

import it.eng.sil.base.enums.DeSistemaEnum;
import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.auth.GamificationClientMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.decodifiche.DeComuneMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.UtenteInfoEJB;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "provinciaEditUtenteBean")
@ViewScoped
public class ProvinciaEditUtenteBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = 2530728093699917279L;

	private UtenteInfo utenteInfo;
	String oldPecEmail;
	String oldEmail;
	private Boolean rinnovoTitoloSoggiorno;
	private Boolean inRinnovoTip;
	private Boolean inAttesaPerTip;
	private Boolean cartaPerTip;

	private static final String CARTA_PER_TITOLO_SOGGIORNO_SATE = "5";
	private static final String RINNOVO_TITOLO_SOGGIORNO_SATE = "3";
	private static final String INATTESA_TITOLO_SOGGIORNO_SATE = "4";

	@EJB
	private UtenteInfoEJB utenteInfoEJB;

	@EJB
	private DeComuneMyAccountEJB deComuneEJB;

	@EJB
	private GamificationClientMyAccountEJB gamificationClientEJB;

	@Override
	protected void initPostConstruct() {
		try {
			loadUser();
		} catch (MyCasNoResultException e) {
			log.error("Impossibile caricare l'utente: " + e.getMessage());
		}
		comuneDomicilioChanged();
		titoloSoggiornoChanged();
	}

	public void loadUser() throws MyCasNoResultException {
		try {
			utenteInfo = utenteInfoEJB.loadUser(Integer.parseInt(getRequestParameter("utenteId")));
			log.debug("Loading User with ID:" + accountInfoBean.getUserInfo().getIdPfPrincipal());
			oldPecEmail = utenteInfo.getEmailPEC();
			oldEmail = utenteInfo.getPfPrincipal().getEmail();
		} catch (NumberFormatException e) {
			log.error(e);
			throw new MyCasNoResultException("Errore nel formato dell utente da caricare: "
					+ getRequestParameter("utenteId"));
		}
	}

	public void comuneDomicilioChanged() {
		if (deComuneEJB.comuneDomicilioInRegione(utenteInfo.getDeComuneDomicilio())) {
			utenteInfo.setDeProvincia(utenteInfo.getDeComuneDomicilio().getDeProvincia());
		} else {
			utenteInfo.getPfPrincipal().setFlagAbilitatoServizi(Boolean.FALSE);
		}
	}

	public void titoloSoggiornoChanged() {
		if (utenteInfo.getDocumentoSoggiorno() != null) {
			String codTitoloSoggiorno = utenteInfo.getDocumentoSoggiorno().getCodTitoloSoggiorno();
			if (constantsSingleton.isTrento()) {
				setRinnovoTitoloSoggiorno(codTitoloSoggiorno.equals(RINNOVO_TITOLO_SOGGIORNO_SATE));
			}
			if (codTitoloSoggiorno.equals(RINNOVO_TITOLO_SOGGIORNO_SATE)) {
				setInRinnovoTip(Boolean.TRUE);
				setInAttesaPerTip(Boolean.FALSE);
				setCartaPerTip(Boolean.FALSE);
			} else if (codTitoloSoggiorno.equals(INATTESA_TITOLO_SOGGIORNO_SATE)) {
				setInRinnovoTip(Boolean.FALSE);
				setInAttesaPerTip(Boolean.TRUE);
				setCartaPerTip(Boolean.FALSE);
			} else if (codTitoloSoggiorno.equals(CARTA_PER_TITOLO_SOGGIORNO_SATE)) {
				setInRinnovoTip(Boolean.FALSE);
				setInAttesaPerTip(Boolean.FALSE);
				setCartaPerTip(Boolean.TRUE);
			} else {
				setInRinnovoTip(Boolean.FALSE);
				setInAttesaPerTip(Boolean.FALSE);
				setCartaPerTip(Boolean.FALSE);
			}
		}
	}

	public void provinciaRiferimentoChanged() {
		if (!utenteInfo.getDeProvincia().equals(utenteInfo.getDeComuneDomicilio().getDeProvincia())) {
			String warningMessage = utils.getUiProperty("prof.comuneVSprovWarning");
			addWarnMessage("profileEditForm:comuneDomicilio", warningMessage);
		}
	}

	public void sync() {
		try {
			utenteInfoEJB.updateProfile(utenteInfo);

			// Aggiorno l'achievement 'profilo completato'
			gamificationClientEJB.assignProfiloCompletoBadge(utenteInfo.getIdPfPrincipal(),
					gamificationClientEJB.calcolaProfiloCompletoValueBadge(utenteInfo));

			// AGGIORNAMENTO YG SUL PORTALE
			// RIPRISTINATA VERSIONE: Carmela 2014/06/25
			boolean isLavoroPerTeInstallato = checkAbilitazioneSistemaVisibile(DeSistemaEnum.LXTE.toString());
			if (isLavoroPerTeInstallato) {
				utenteInfoEJB.updateYG(utenteInfo);
			}
			String msgok = utils.getUiProperty("msg.updated");
			addJSSuccessMessage(msgok);
		} catch (MyCasException e) {
			String msgko = utils.getUiProperty("msg.updated.ko");
			log.error(msgko, e);
			addJSDangerMessage(msgko);
			return;
		}
	}

	public UtenteInfo getUtenteInfo() {
		return utenteInfo;
	}

	public void setUtenteInfo(UtenteInfo utenteInfo) {
		this.utenteInfo = utenteInfo;
	}

	public String getOldPecEmail() {
		return oldPecEmail;
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

	public Boolean getRinnovoTitoloSoggiorno() {
		return rinnovoTitoloSoggiorno;
	}

	public void setRinnovoTitoloSoggiorno(Boolean rinnovoTitoloSoggiorno) {
		this.rinnovoTitoloSoggiorno = rinnovoTitoloSoggiorno;
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
}
