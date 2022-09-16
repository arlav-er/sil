package it.eng.sil.myauthservice.model.ejb.business;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import it.eng.sil.myauthservice.model.ConstantsSingleton;
import it.eng.sil.myauthservice.model.ejb.business.onesignal.AdditionalDataNotification;
import it.eng.sil.myauthservice.model.ejb.business.onesignal.OneSignalException;
import it.eng.sil.myauthservice.model.ejb.business.onesignal.ProviderNotification;
import it.eng.sil.myauthservice.model.ejb.business.onesignal.ProviderNotificationException;
import it.eng.sil.myauthservice.model.ejb.business.onesignal.ProviderNotificationFactory;
import it.eng.sil.myauthservice.model.entity.PfOtp;
import it.eng.sil.myauthservice.model.entity.StatoNotifica;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;

/**
 * 
 * @author
 */
@Stateless
public class OneSignalNotificationEJB {
	protected static Logger log = Logger.getLogger(OneSignalNotificationEJB.class.getName());
	@EJB
	private DeProvinciaEJB provinciaHome;

	@EJB
	private ConstantsSingleton constantsSingleton;

	/**
	 * Invio della notifica
	 * 
	 * @param data
	 * @param additionalData
	 * @param deliveryTimeOfDay
	 * @return
	 * @throws ProviderNotificationException
	 */
	public String sendOTPNotification(PfOtp generatedOTPCode, PfPrincipal pfPrincipalDest, String titolo,
			String sottotitolo, String messaggio, AdditionalDataNotification additionalData, String deliveryTimeOfDay)
			throws ProviderNotificationException, OneSignalException {

		/*
		 * -------------------- Invio Notifica al Provider --------------------
		 */
		ProviderNotification provider = ProviderNotificationFactory
				.getProviderNotification(ProviderNotification.LAVORO_PER_TE_PROJECT_ID, constantsSingleton);

		validaPfPrincipalDest(pfPrincipalDest);

		String sidNotifica = (String) provider.send(titolo, sottotitolo, messaggio,
				pfPrincipalDest != null ? pfPrincipalDest.getEmail() : null, additionalData, deliveryTimeOfDay);
		log.info(String.format("invio PUSH per OTP oneSignal. [dest: '%s', Testo: '%s']", pfPrincipalDest.getEmail(),
				messaggio));
		return sidNotifica;
	}

	/**
	 * Recupero degli stati con rispettivi contatori
	 * 
	 * @param sidNotifica
	 * @return
	 * @throws ProviderNotificationException
	 */
	@SuppressWarnings("unchecked")
	private HashMap<StatoNotifica, Integer> getStatoNotificaCount(String sidNotifica)
			throws ProviderNotificationException {
		/*
		 * -------------------- Recupero dello stato notifica --------------
		 */
		HashMap<StatoNotifica, Integer> stato = null;

		if (sidNotifica != null) {
			ProviderNotification provider = ProviderNotificationFactory
					.getProviderNotification(ProviderNotification.LAVORO_PER_TE_PROJECT_ID, constantsSingleton);
			stato = (HashMap<StatoNotifica, Integer>) provider.view(sidNotifica);
		}

		return stato;
	}

	public StatoNotifica getStatoNotifica(String sidNotifica) throws ProviderNotificationException {
		HashMap<StatoNotifica, Integer> stato = getStatoNotificaCount(sidNotifica);

		return getStatoNotifica(stato);

	}

	private StatoNotifica getStatoNotifica(HashMap<StatoNotifica, Integer> stato) {
		StatoNotifica ret = null;

		if (stato != null) {
			if (stato.containsKey(StatoNotifica.R) && stato.get(StatoNotifica.R) > 0) {
				ret = StatoNotifica.R;
			} else if (stato.containsKey(StatoNotifica.S) && stato.get(StatoNotifica.S) > 0) {
				ret = StatoNotifica.S;
			} else if (stato.containsKey(StatoNotifica.D) && stato.get(StatoNotifica.D) > 0) {
				ret = StatoNotifica.D;
			} else if (stato.containsKey(StatoNotifica.F) && stato.get(StatoNotifica.F) > 0) {
				ret = StatoNotifica.F;
			}
		}

		return ret;
	}

	private void validaPfPrincipalDest(PfPrincipal pfPrincipalDest) {
		if (pfPrincipalDest == null) {
			throw new IllegalArgumentException("Il destinatario non è presente");
		}

		// TODO
		// SELECT da myportal.pf_identity_device

		// Validazione destinatario notifiche (utente cittadino)
		if (!pfPrincipalDest.isUtente()) {
			throw new IllegalArgumentException("Il destinatario non è un cittadino");
		} else if (pfPrincipalDest.getUtenteInfo() == null
				|| pfPrincipalDest.getUtenteInfo().getDeProvincia() == null) {
			throw new IllegalArgumentException("Il destinatario non ha la provincia di appartenenza");
		}
	}

}
