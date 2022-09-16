package it.eng.sil.myauthservice.model.ejb.business.onesignal;



public abstract class ProviderNotification {

	static final String ONE_SIGNAL_PROVIDER_ID = "ONE_SIGNAL";
	public static final String LAVORO_PER_TE_PROJECT_ID = "LAVORO_PER_TE";

	String providerId;
	String projectId;

	abstract public Object send(String headings, String subtitle, String contents, String email,
			AdditionalDataNotification additionalData, String deliveryTimeOfDay) throws ProviderNotificationException;

	abstract public Object view(String sid) throws ProviderNotificationException;
}
