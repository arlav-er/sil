package it.eng.sil.myauthservice.model.ejb.business.onesignal;

import it.eng.sil.myauthservice.model.ConstantsSingleton;

public class ProviderNotificationFactory {

	public static ProviderNotification getProviderNotification(String projectId, ConstantsSingleton cons) {

		if (!cons.getOneSignalAppId().isEmpty()) {
			return new OneSignalNotification(projectId, cons);
		}

		throw new UnsupportedOperationException("Nessun provider per invio notifiche definito");
	}
}
