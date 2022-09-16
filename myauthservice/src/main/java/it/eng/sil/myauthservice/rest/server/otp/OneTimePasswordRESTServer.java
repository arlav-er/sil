package it.eng.sil.myauthservice.rest.server.otp;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import it.eng.sil.base.enums.EsitoEnum;
import it.eng.sil.base.enums.OTPRequestEnum;
import it.eng.sil.base.exceptions.OtpRemoteException;
import it.eng.sil.base.pojo.EsitoTypePOJO;
import it.eng.sil.myauthservice.model.ConstantsSingleton;
import it.eng.sil.myauthservice.model.ejb.business.SMSNotificationEJB;
import it.eng.sil.myauthservice.model.ejb.business.sms.Sms;
import it.eng.sil.myauthservice.model.ejb.business.sms.SmsException;
import it.eng.sil.myauthservice.model.ejb.business.sms.SmsFormatException;
import it.eng.sil.myauthservice.model.ejb.stateless.InvalidOTPException;
import it.eng.sil.myauthservice.model.ejb.stateless.PfOtpEJB;
import it.eng.sil.myauthservice.model.entity.PfOtp;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.business.AccreditamentoClientEJB;

/**
 * Componente per richiesta/risposta di password OTP
 * 
 * per ora due canali previsti: SMS e PUSH
 * 
 * @author Ale
 *
 */
@Path("/otp")
public class OneTimePasswordRESTServer {
	protected static Logger log = Logger.getLogger(OneTimePasswordRESTServer.class.getName());

	@Inject
	AccreditamentoClientEJB accreditamentoClientEJB;

	@Inject
	ConstantsSingleton constantsSingleton;

	@Inject
	PfOtpEJB pfOtpEJB;

	@Inject
	SMSNotificationEJB smsManagerEJB;

	@GET
	@Path("enabled")
	@Produces("application/json; charset=UTF-8")
	public Boolean isOTPChannelEnabled(@QueryParam("tipoOTP") String tipoOTP) throws OtpRemoteException {
		if (tipoOTP == null || tipoOTP.isEmpty()) {
			throw new OtpRemoteException(
					"Errore isOTPChannelEnabled: tipoOTP obbligatorio per sapere se abilitato o meno");
		}

		return pfOtpEJB.isOTPChannelEnabled(OTPRequestEnum.valueOf(tipoOTP));

	}

	@POST
	@Path("createFirstOtpRequest")
	@Produces("application/json; charset=UTF-8")
	public String createFirstOtpRequest(@FormParam("idPfPrincipal") String idPfPrincipal,
			@FormParam("mobileNum") String mobileNum) throws OtpRemoteException {
		if (mobileNum == null || mobileNum.isEmpty()) {
			throw new OtpRemoteException("Errore createOtpRequest: mobileNum nullo");
		}

		String reqCode;
		try {
			reqCode = pfOtpEJB.createFirstOTPRequest(Integer.valueOf(idPfPrincipal), mobileNum);
		} catch (NumberFormatException | OTPException e) {
			throw new OtpRemoteException("Errore createFirstOtpRequest: idPfPrincipal non valido: " + idPfPrincipal);
		} catch (MyCasException e) {
			throw new OtpRemoteException(
					"Errore createOcreateFirstOtpRequesttpRequest: errore salvataggio OTP: " + e.getMessage());
		} catch (SmsException | SmsFormatException e) {
			throw new OtpRemoteException(
					"Errore createOcreateFirstOtpRequesttpRequest: errore formazione SMS: " + e.getMessage());
		}
		return reqCode;
	}

	@POST
	@Path("createOtpRequest")
	@Produces("application/json; charset=UTF-8")
	public String createOtpRequest(@FormParam("idPfPrincipal") String idPfPrincipal,
			@FormParam("tipoOTP") OTPRequestEnum tipoOTP) throws OtpRemoteException, MyCasException {
		if (idPfPrincipal == null || idPfPrincipal.isEmpty()) {
			throw new OtpRemoteException("Errore createOtpRequest: idPfPrincipal nullo");
		}

		String reqCode;
		try {
			reqCode = pfOtpEJB.createOTPRequest(Integer.valueOf(idPfPrincipal), tipoOTP);
		} catch (NumberFormatException e) {
			throw new OtpRemoteException("Errore createOtpRequest: idPfPrincipal non valido");
		} catch (MyCasException e) {
			throw e;
		} catch (SmsException | SmsFormatException e) {
			throw new OtpSmsRemoteException("Errore createOtpRequest: errore invio SMS: " + e.getMessage());
		}
		return reqCode;
	}

	@POST
	@Path("replyToOtp")
	@Produces("application/json; charset=UTF-8")
	public EsitoTypePOJO replyToOtp(@FormParam("requestCode") String requestCode, @FormParam("secret") String secret)
			throws OtpRemoteException {
		if (requestCode == null || requestCode.isEmpty()) {
			throw new OtpRemoteException("Errore replyToOtp(): requestCode nullo");
		}

		try {
			PfOtp resolved = pfOtpEJB.replyToOtp(requestCode, secret);
			return new EsitoTypePOJO(EsitoEnum.OK, "Valido fino al: " + resolved.getDataScadenza());
		} catch (InvalidOTPException e) {
			return new EsitoTypePOJO(EsitoEnum.KO, e.getMessage());
		} catch (MyCasException e) {
			log.log(Level.SEVERE, "Errore imprevisto: " + e.getMessage());
			return new EsitoTypePOJO(EsitoEnum.KO, "Errore di sistema: " + e.getMessage());
		}

	}

	/**
	 * SOLO TEST NON ESPORRE
	 * 
	 * @param idPfPrincipal
	 * @param text
	 * @param cellNumb
	 * @return
	 * @throws SmsException
	 * @throws SmsFormatException
	 */
	@POST
	@Path("sendSms")
	@Produces("application/json; charset=UTF-8")
	@Deprecated
	public String sendSms(@FormParam("idPfPrincipal") String idPfPrincipal, @FormParam("text") String text,
			@FormParam("cellNumb") String cellNumb) throws SmsException, SmsFormatException {
		if (idPfPrincipal == null || idPfPrincipal.isEmpty()) {
			throw new SmsException("Errore sendSms: idPfPrincipal nullo");
		}
		Sms sms = new Sms(cellNumb, text);
		String result = "";
		log.warning("Uso di API sendSms() di SOLO DEV idPfPrincipalidPfPrincipal: " + idPfPrincipal);
		try {
			result = smsManagerEJB.send(Integer.valueOf(idPfPrincipal), sms);
			return result;
		} catch (NumberFormatException e) {
			throw new SmsException("Errore sendSms: idPfPrincipal non valido");
		} catch (MyCasException e) {
			throw new SmsException("Errore invio SMS: " + e.getMessage());
		}

	}

}
