package it.eng.myportal.rest.app.helper;

import it.eng.myportal.utils.ConstantsSingleton;

public class ProviderNotificationFactory {

	public static ProviderNotification getProviderNotification(String projectId) {

		if (!ConstantsSingleton.App.ONESIGNAL_APP_ID.isEmpty()) {
			return new OneSignalNotification(projectId);
		}

		throw new UnsupportedOperationException("Nessun provider per invio notifiche definito");
	}
}
