package it.eng.sil.myauthservice.model.ejb.stateless;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.RandomStringUtils;

import it.eng.sil.base.enums.OTPRequestEnum;
import it.eng.sil.base.exceptions.OtpRemoteException;
import it.eng.sil.base.utils.ConstantsBaseCommons;
import it.eng.sil.base.utils.StringUtils;
import it.eng.sil.myauthservice.model.ConstantsSingleton;
import it.eng.sil.myauthservice.model.ejb.business.OneSignalNotificationEJB;
import it.eng.sil.myauthservice.model.ejb.business.SMSNotificationEJB;
import it.eng.sil.myauthservice.model.ejb.business.onesignal.AdditionalDataNotification;
import it.eng.sil.myauthservice.model.ejb.business.onesignal.NoRecipientException;
import it.eng.sil.myauthservice.model.ejb.business.onesignal.ProviderNotificationException;
import it.eng.sil.myauthservice.model.ejb.business.sms.Sms;
import it.eng.sil.myauthservice.model.ejb.business.sms.SmsException;
import it.eng.sil.myauthservice.model.ejb.business.sms.SmsFormatException;
import it.eng.sil.myauthservice.model.entity.AppNotifica;
import it.eng.sil.myauthservice.model.entity.PfOtp;
import it.eng.sil.myauthservice.model.entity.StatoNotifica;
import it.eng.sil.myauthservice.model.entity.TipoNotificaEnum;
import it.eng.sil.myauthservice.rest.server.otp.OTPException;
import it.eng.sil.myauthservice.rest.server.otp.OtpSmsRemoteException;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.manager.AbstractTabellaGestioneEJB;
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
public class PfOtpEJB extends AbstractTabellaGestioneEJB<PfOtp, Integer> {

	@EJB
	ConstantsSingleton constantsSingleton;

	@EJB
	OneSignalNotificationEJB oneSignalNotificationEJB;

	@EJB
	SMSNotificationEJB smsNotificationEJB;

	@EJB
	PfPrincipalEJB pfPrincipalEJB;

	@EJB
	AppNotificaEJB appNotificaEJB;

	@Override
	public String getFriendlyName() {
		return "Tabella di pass usa e getta";
	}

	@Override
	public Class<PfOtp> getEntityClass() {
		return PfOtp.class;
	}

	@Override
	public PfOtp persist(Integer idPrincipal, PfOtp entity) throws MyCasException {
		// autogenerare il codice richiesta
		int length = 7;
		boolean useLetters = true;
		boolean useNumbers = false;
		String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
		entity.setOtpReqCode(generatedString);

		Integer minuti = constantsSingleton.getDurataTokenOTP();
		Calendar dtScad = Calendar.getInstance();
		dtScad.add(Calendar.MINUTE, minuti);
		entity.setDataScadenza(dtScad.getTime());

		String otpSecret = RandomStringUtils.randomNumeric(5);
		entity.setOtpSecret(otpSecret);

		return super.persist(idPrincipal, entity);
	}

	public String createFirstOTPRequest(Integer idPfPrincipal, String mobileNum)
			throws OTPException, MyCasException, SmsFormatException, SmsException {
		PfOtp newRow = new PfOtp();
		newRow.setTipoOtp(OTPRequestEnum.SMS);

		PfOtp refreshed = persist(idPfPrincipal, newRow);
		try {
			PfPrincipal utente = pfPrincipalEJB.findById(idPfPrincipal);
			log.info("Creazione prima richiesta OTP per : " + utente.getIdPfPrincipal());
		} catch (MyCasNoResultException e) {
			throw new OTPException("idPfPrincipal SCONOSCIUTO: " + idPfPrincipal);
		}
		String otpHint = "Il tuo codice di verifica: " + refreshed.getOtpSecret();

		// invio SMS con codice OTP
		Sms toSend = new Sms(mobileNum, otpHint);
		smsNotificationEJB.send(idPfPrincipal, toSend);

		return refreshed.getOtpReqCode();
	}

	public String createOTPRequest(Integer idPfPrincipal, OTPRequestEnum tipoOTP)
			throws OTPException, MyCasException, SmsException, SmsFormatException {

		// check installata APP
		if (tipoOTP.equals(OTPRequestEnum.PUSH) && StringUtils.isEmptyNoBlank(constantsSingleton.getOneSignalAppId()))
			throw new OTPException("Errore di sistema: PUSH non supportate per questa configurazione");

		if (tipoOTP.equals(OTPRequestEnum.SMS) && StringUtils.isEmptyNoBlank(constantsSingleton.getAuthTokenUrlSms()))
			throw new OtpSmsRemoteException("Errore di sistema: SMS non supportati per questa configurazione");

		PfOtp newRow = new PfOtp();
		newRow.setTipoOtp(tipoOTP);

		try {
			PfPrincipal utente = pfPrincipalEJB.findById(idPfPrincipal);

			PfOtp refreshed = persist(idPfPrincipal, newRow);
			log.info("richiesta OTP generata (pre invio) reqcode: " + refreshed.getOtpReqCode() + " sec: "
					+ refreshed.getOtpSecret());
			switch (tipoOTP) {
			case PUSH:
				// invio PUSH con codice OTP
				try {

					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy 'ore' HH:mm");
					String dtaMaxAccettazioneFormatted = df.format(refreshed.getDataScadenza());

					MessageFormat formatter = new MessageFormat(constantsSingleton.getSottotitoloNotificaOTP());
					Object[] paramsSottotitolo = { dtaMaxAccettazioneFormatted };
					String sottotitolo = formatter.format(paramsSottotitolo);

					formatter = new MessageFormat(constantsSingleton.getMessaggioNotificaOTP());
					Object[] paramsMsg = { refreshed.getOtpSecret(), sottotitolo };
					String messaggio = formatter.format(paramsMsg);

					// Dati addizionali
					AdditionalDataNotification additionalData = new AdditionalDataNotification();

					// (pre) persist app notifica
					AppNotifica preNotifica = appNotificaEJB.persist(constantsSingleton.getTitoloNotificaOTP(),
							sottotitolo, messaggio, idPfPrincipal, TipoNotificaEnum.OTP_PATTO, idPfPrincipal,
							additionalData);

					// invio effettivo
					String sidNotifica = oneSignalNotificationEJB.sendOTPNotification(refreshed, utente,
							constantsSingleton.getTitoloNotificaOTP(), sottotitolo, messaggio, additionalData, null);

					log.info("notifica push per OTP inviata : " + sidNotifica);

					try {
						// recupera stato notifica
						preNotifica.setStato(oneSignalNotificationEJB.getStatoNotifica(sidNotifica));
						preNotifica.setSidNotifica(sidNotifica);
						appNotificaEJB.merge(ConstantsBaseCommons.Users.ADMINISTRATOR, preNotifica);
					} catch (Exception e) {
						log.error("errore aggiornamento app_notifica:" + sidNotifica);
					}
				} catch (NoRecipientException e) {
					log.error("GRAVE :Impossibile inviare PUSH verso: " + utente.getIdPfPrincipal()
							+ ": destinatario SCONOSCIUTO a ONESIGNAL");
					throw new OTPException("destinatario SCONOSCIUTO a ONESIGNAL");
				} catch (ProviderNotificationException e) {
					log.error("GRAVE :Impossibile inviare PUSH verso: " + utente.getIdPfPrincipal() + ": "
							+ e.getMessage());
					throw new OTPException(e.getMessage());
				}
				break;
			case SMS:
				String otpHint = "Il tuo codice di verifica: " + refreshed.getOtpSecret();
				// invio SMS con codice OTP
				Sms toSend = new Sms(utente.getUtenteInfo().getCellulareOTP(), otpHint);
				smsNotificationEJB.send(idPfPrincipal, toSend);
				break;
			default:
				log.fatal("GRAVE: tipo OTP non previsto: " + tipoOTP);
				break;
			}

			return refreshed.getOtpReqCode();
		} catch (MyCasNoResultException e) {
			throw new OTPException("errore creazione OTP, idPfPrincipal SCONOSCIUTO: " + idPfPrincipal);
		} catch (Exception e) {
			throw new OTPException("errore creazione OTP: " + e.getMessage());
		}
	}

	public PfOtp replyToOtp(String requestCode, String secret) throws InvalidOTPException, MyCasException {
		PfOtp target = findByRequestCode(requestCode);

		if (target == null)
			throw new InvalidOTPException("request code OTP inesistente");

		if (!secret.equals(target.getOtpSecret()))
			throw new InvalidOTPException("codice OTP errato");

		if (target.getDataScadenza().before(new Date()))
			throw new InvalidOTPException("codice OTP scaduto");

		target.setDataValidazione(new Date());
		merge(ConstantsBaseCommons.Users.ADMINISTRATOR, target);
		return target;
	}

	private PfOtp findByRequestCode(String requestCode) {
		TypedQuery<PfOtp> query = entityMan.createNamedQuery("PfOtp.findByRequestCode", PfOtp.class);
		query.setParameter("reqCode", requestCode);
		return query.getSingleResult();
	}

	public Boolean isOTPChannelEnabled(OTPRequestEnum tipoOTP) throws OtpRemoteException {
		if (tipoOTP == null)
			throw new OtpRemoteException("Tipo canale NULLO");
		// check installata APP
		if (tipoOTP.equals(OTPRequestEnum.PUSH))
			return !StringUtils.isEmptyNoBlank(constantsSingleton.getOneSignalAppId());

		else if (tipoOTP.equals(OTPRequestEnum.SMS))
			return !StringUtils.isEmptyNoBlank(constantsSingleton.getSmsProvider());
		else
			throw new OtpRemoteException("Tipo canale sconosciuto: " + tipoOTP);
	}

}
