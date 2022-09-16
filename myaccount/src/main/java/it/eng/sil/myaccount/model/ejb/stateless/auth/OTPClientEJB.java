package it.eng.sil.myaccount.model.ejb.stateless.auth;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.sil.base.business.OneTimePasswordRemoteClient;
import it.eng.sil.base.enums.OTPRequestEnum;
import it.eng.sil.base.exceptions.OtpRemoteException;
import it.eng.sil.base.pojo.EsitoTypePOJO;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;

@Stateless
public class OTPClientEJB extends OneTimePasswordRemoteClient {
	protected static Log log = LogFactory.getLog(OTPClientEJB.class);
	OneTimePasswordRemoteClient on = new OneTimePasswordRemoteClient();

	@EJB
	ConstantsSingleton constantsSingleton;

	public Boolean isEnabled(OTPRequestEnum tipoOTP) {
		return on.isOtpChannelAvailable(constantsSingleton.getOTPEnabledURL(), tipoOTP);
	}

	public String createFirstOTPRequest(Integer idPfPrincipal, String mobileNum) throws OtpRemoteException {
		String ret = on.createOtpFirstRequest(constantsSingleton.getOTPRequestURL(), "" + idPfPrincipal, mobileNum);
		log.info("Invocazione remota createFirstOTPRequest ha prodotto reqcode: " + ret);
		return ret;
	}

	public EsitoTypePOJO replyOTP(String reqCode, String secret) throws OtpRemoteException {

		log.info("Invocazione remota replyOTP verso: " + constantsSingleton.getOTPReplyURL() + " per reqCode: "
				+ reqCode);
		EsitoTypePOJO ret = on.replyToOtp(constantsSingleton.getOTPReplyURL(), reqCode, secret);
		return ret;

	}

}
