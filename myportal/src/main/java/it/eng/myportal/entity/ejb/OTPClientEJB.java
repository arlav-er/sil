package it.eng.myportal.entity.ejb;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.sil.base.business.OneTimePasswordRemoteClient;
import it.eng.sil.base.enums.EsitoEnum;
import it.eng.sil.base.enums.OTPRequestEnum;
import it.eng.sil.base.exceptions.OtpRemoteException;
import it.eng.sil.base.pojo.EsitoTypePOJO;

@Stateless
public class OTPClientEJB {
	protected static Log log = LogFactory.getLog(OTPClientEJB.class);
	OneTimePasswordRemoteClient on = new OneTimePasswordRemoteClient();
 
	public Boolean isChannelAvailable(OTPRequestEnum OTPType) {
		return on.isOtpChannelAvailable( ConstantsSingleton.getOTPEnabledURL() , OTPType);
	}

	public String createOTPRequest(Integer idPfPrincipal, OTPRequestEnum OTPType) throws OtpRemoteException {
		return on.createOtpRequest(ConstantsSingleton.getOTPRequestURL(), "" + idPfPrincipal, OTPType);
	}

	public boolean replyOTP(String reqCode, String secret) {
		try {
			log.info("Invocazione remota replyOTP verso: " + ConstantsSingleton.getOTPReplyURL() + " per reqCode: "
					+ reqCode);
			EsitoTypePOJO ret = on.replyToOtp(ConstantsSingleton.getOTPReplyURL(), reqCode, secret);
			return ret.getEsito().equals(EsitoEnum.OK);
		} catch (OtpRemoteException e) {
			log.error("Errore replyOTP: " + e.getMessage());
			return false;
		}
	}

}