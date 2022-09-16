package it.eng.sil.myauthservice.rest.server.otp;

import javax.ejb.ApplicationException;

import it.eng.sil.base.exceptions.OtpRemoteException;
@ApplicationException
public class OTPException extends  OtpRemoteException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3979002300061109283L;

	public OTPException(String string) {
		super(string);
		// TODO Auto-generated constructor stub
	}


}
