package it.eng.sil.myauthservice.model.ejb.stateless;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sil.myauthservice.model.ConstantsSingleton;
import it.eng.sil.myauthservice.model.ejb.business.OneSignalNotificationEJB;
import it.eng.sil.myauthservice.model.ejb.business.SMSNotificationEJB;
import it.eng.sil.myauthservice.model.ejb.business.onesignal.AdditionalDataNotification;
import it.eng.sil.myauthservice.model.entity.AppNotifica;
import it.eng.sil.myauthservice.model.entity.TipoNotificaEnum;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;
import it.eng.sil.mycas.model.manager.AbstractTabellaGestioneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;
import it.eng.sil.mycas.model.manager.profilatura.PfPrincipalEJB;

/**
 * Session Bean implementation class CreaRichiestaOTP
 * 
 * Verrebbe voglia di metterla sulla commons, ma sta meglio qui. Il servizio OTP
 * e` soplo remoto
 * 
 * 
 */
@Stateless
@LocalBean
public class AppNotificaEJB extends AbstractTabellaGestioneEJB<AppNotifica, Integer> {

	@EJB
	ConstantsSingleton constantsSingleton;

	@EJB
	DeProvinciaEJB deProvinciaEJB;

	@EJB
	OneSignalNotificationEJB oneSignalNotificationEJB;

	@EJB
	SMSNotificationEJB smsNotificationEJB;

	@EJB
	PfPrincipalEJB pfPrincipalEJB;

	@Override
	public String getFriendlyName() {
		return "Tabella di pass usa e getta";
	}

	@Override
	public Class<AppNotifica> getEntityClass() {
		return AppNotifica.class;
	}

	/**
	 * 
	 * @param titolo
	 * @param sottotitolo
	 * @param messaggio
	 * @param deliveryTimeOfDay
	 * @param idPrincipalDest
	 * @param tipoNotifica
	 * @param idPrincipalIns
	 * @return
	 * @throws MyCasException         nella persist
	 * @throws MyCasNoResultException GRAVE: non trova utente/provincia
	 */
	public AppNotifica persist(String titolo, String sottotitolo, String messaggio, Integer idPrincipalDest,
			TipoNotificaEnum tipoNotifica, Integer idPrincipalIns,AdditionalDataNotification additionalData) throws MyCasException, MyCasNoResultException {

		PfPrincipal pfPrincipalDest = null;
		DeProvincia deProvinciaRif = null;
		if (idPrincipalDest != null) {
			pfPrincipalDest = pfPrincipalEJB.findById(idPrincipalDest);
			deProvinciaRif = getDeProvinciaRiferimento(pfPrincipalDest);
		}

		// Inserimento e invio della notifica
		AppNotifica appNotifica = new AppNotifica(titolo, sottotitolo, messaggio, deProvinciaRif, pfPrincipalDest,
				pfPrincipalEJB.findById(idPrincipalIns), null /* sidNotifica */, tipoNotifica /* tipoNotifica */,
				null /* statoNotifica */);

		// this.persistAndSend(appNotifica, additionalData, deliveryTimeOfDay);
		if (additionalData != null) {
			// Serializzazione JSON dati addizionali
			appNotifica.setAdditionalData(additionalData.toJSONObject().toString());
		}
		return persist(idPrincipalIns, appNotifica);
	}

	private static final DeProvincia getDeProvinciaRiferimento(PfPrincipal pfPrincipal) {
		/*
		 * La provincia di riferimento per le notifiche puntuale e per feedback viene
		 * determinata a partire dall'utente cittadino destinatario o mittente (per
		 * feedback) della stessa. Viene verificata la provincia riferimento, comune di
		 * domicilio e comune di nascita.
		 */
		DeProvincia ret = null;

		if (pfPrincipal.isUtente()) {
			UtenteInfo utenteInfo = pfPrincipal.getUtenteInfo();

			if (utenteInfo.getDeProvincia() != null) {
				ret = utenteInfo.getDeProvincia();
			} else if (utenteInfo.getDeComuneDomicilio() != null) {
				ret = utenteInfo.getDeComuneDomicilio().getDeProvincia();
			} else if (utenteInfo.getDeComuneNascita() != null) {
				ret = utenteInfo.getDeComuneNascita().getDeProvincia();
			} else {
				throw new EJBException(
						"Impossibile determinare la provincia di riferimento per l'utente in quanto non presente'"
								+ utenteInfo.getPfPrincipal().getUsername());
			}
		} else {
			throw new EJBException(
					"Impossibile determinare la provincia di riferimento, tipologia utente non corretta");
		}
		return ret;
	}

}
