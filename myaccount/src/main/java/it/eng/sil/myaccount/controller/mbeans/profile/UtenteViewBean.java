package it.eng.sil.myaccount.controller.mbeans.profile;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.gamification.GpBadgeMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpProfilaturaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.UtenteInfoEJB;
import it.eng.sil.myaccount.utils.Utils;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.business.gamification.GamificationClientEJB;
import it.eng.sil.mycas.model.entity.gamification.DeBadge;
import it.eng.sil.mycas.model.entity.profilatura.PfIdentityProvider;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;
import it.eng.sil.mycas.model.manager.decodifiche.DeCittadinanzaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTitoloSoggiornoEJB;
import it.eng.sil.mycas.model.manager.profilatura.PfIdentityProviderEJB;

@ViewScoped
@ManagedBean
public class UtenteViewBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = 4269765806138037831L;

	@EJB
	private UtenteInfoEJB utenteInfoEJB;

	@EJB
	private DeComuneEJB deComuneEJB;

	@EJB
	private DeCittadinanzaEJB deCittadinanzaEJB;

	@EJB
	private DeTitoloSoggiornoEJB deTitoloSoggiornoEJB;

	@EJB
	private DeProvinciaEJB deProvinciaEJB;

	@EJB
	private PfIdentityProviderEJB pfIdentityProviderEJB;

	@EJB
	private GamificationClientEJB gamificationEJB;

	@EJB
	private GpBadgeMyAccountEJB gpBadgeEJB;

	@EJB
	private GpProfilaturaMyAccountEJB gpProfilaturaEJB;

	private UtenteInfo utenteInfo;
	private PfIdentityProvider socialNetwork;
	private DeBadge lastAchievedBadge;

	@Override
	protected void initPostConstruct() {
		loadUser();

		// Provo a identificare il social network dell'utente.
		try {
			socialNetwork = pfIdentityProviderEJB.findByIdPfPrincipal(utenteInfo.getIdPfPrincipal());
		} catch (MyCasException e) {
			// Non è un errore: l'utente non è associato ad un social network.
			socialNetwork = null;
		}

		// Se l'utente ha almeno un badge, prendo l'ultimo badge che ha ottenuto
		lastAchievedBadge = gpBadgeEJB.findLastAchievedBadge(utenteInfo.getIdPfPrincipal());

		// Controllo se l'utente ha bisogno delle profilature di default
		try {
			gpProfilaturaEJB.checkProfilatureDefault(utenteInfo.getIdPfPrincipal(), GpDeMacroTipoEnum.CIT);
		} catch (MyCasException e) {
			log.error("ERRORE durante l'inserimento delle profilatura di default: " + e.toString());
		}
	}

	public void loadUser() {
		try {
			utenteInfo = utenteInfoEJB.findById(accountInfoBean.getUserInfo().getIdPfPrincipal());

			if (utenteInfo.getDeComuneNascita() != null)
				utenteInfo.setDeComuneNascita(deComuneEJB.findById(utenteInfo.getDeComuneNascita().getCodCom()));

			if (utenteInfo.getDeComuneDomicilio() != null)
				utenteInfo.setDeComuneDomicilio(deComuneEJB.findById(utenteInfo.getDeComuneDomicilio().getCodCom()));

			if (utenteInfo.getDeComuneResidenza() != null)
				utenteInfo.setDeComuneResidenza(deComuneEJB.findById(utenteInfo.getDeComuneResidenza().getCodCom()));

			if (utenteInfo.getDeCittadinanza() != null)
				utenteInfo.setDeCittadinanza(deCittadinanzaEJB.findById(utenteInfo.getDeCittadinanza()
						.getCodCittadinanza()));

			if (utenteInfo.getDocumentoSoggiorno() != null)
				utenteInfo.setDocumentoSoggiorno(deTitoloSoggiornoEJB.findById(utenteInfo.getDocumentoSoggiorno()
						.getCodTitoloSoggiorno()));

			if (utenteInfo.getDeProvincia() != null)
				utenteInfo.setDeProvincia(deProvinciaEJB.findById(utenteInfo.getDeProvincia().getCodProvincia()));
		} catch (MyCasNoResultException e) {
			throw new RuntimeException("Errore nel caricamento dell'utente", e);
		}
	}

	public UtenteInfo getUtenteInfo() {
		return utenteInfo;
	}
	
	public String getRenderCodServiziAmministrativi() {
		return Utils.getRenderCodServiziAmministrativi(utenteInfo.getCodServiziAmministrativi());
	}

	public void setUtenteInfo(UtenteInfo utenteInfo) {
		this.utenteInfo = utenteInfo;
	}

	public PfIdentityProvider getSocialNetwork() {
		return socialNetwork;
	}

	public void setSocialNetwork(PfIdentityProvider socialNetwork) {
		this.socialNetwork = socialNetwork;
	}

	public DeBadge getLastAchievedBadge() {
		return lastAchievedBadge;
	}

	public void setLastAchievedBadge(DeBadge lastAchievedBadge) {
		this.lastAchievedBadge = lastAchievedBadge;
	}

}
