package it.eng.sil.myauthservice.rest.client.sms;

import it.eng.sil.myauthservice.model.ejb.business.sms.SmsException;

public abstract interface ProviderSms {

	abstract public void sendSms(String cellNumb, String msg) throws SmsException;

}
