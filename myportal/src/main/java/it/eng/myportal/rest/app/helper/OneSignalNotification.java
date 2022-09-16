package it.eng.myportal.rest.app.helper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.rest.app.exception.NoRecipientException;
import it.eng.myportal.rest.app.exception.OneSignalException;
import it.eng.myportal.rest.app.exception.ProviderNotificationException;
import it.eng.myportal.utils.ConstantsSingleton;

public class OneSignalNotification extends ProviderNotification {

	private static Log log = LogFactory.getLog(OneSignalNotification.class);

	private static final String CONTENT_TYPE_KEY = "Content-Type";
	private static final String AUTHORIZATION_KEY = "Authorization";

	private static final String APP_ID_KEY = "app_id";
	private static final String HEADINGS_KEY = "headings";
	private static final String SUBTITLE_KEY = "subtitle";
	private static final String CONTENTS_KEY = "contents";
	// English must be included in the hash
	private static final String LANGUAGE_KEY = "en";

	private static final String FIELD_KEY = "field";
	private static final String FIELD_VALUE_TAG = "tag";
	private static final String TAG_KEY = "key";
	private static final String TAG_VALUE = "value";
	private static final String RELATION_KEY = "relation";
	private static final String RELATION_VALUE = "=";
	private static final String TAG_VALUE_EMAIL = "email";
	private static final String FILTERS_KEY = "filters";

	private static final String CONTENT_TYPE_VALUE = "application/json; charset=UTF-8";

	// AppId OneSignal
	private String appId = null;
	// Authorization key
	private String authorizationKey = null;
	// Destination Url OneSignal
	private String destinationUrl = null;

	public OneSignalNotification(String projectId) {
		this.providerId = ProviderNotification.ONE_SIGNAL_PROVIDER_ID;
		this.projectId = projectId;

		setProviderConfiguration();
	}

	private void setProviderConfiguration() {
		switch (this.projectId) {
		case ProviderNotification.LAVORO_PER_TE_PROJECT_ID:
			this.appId = ConstantsSingleton.App.ONESIGNAL_APP_ID;
			this.authorizationKey = ConstantsSingleton.App.ONESIGNAL_AUTHORIZATION;
			this.destinationUrl = ConstantsSingleton.App.ONESIGNAL_NOTIFICATION_URL;
			break;
		default:
			break;
		}
	}

	@Override
	public Object send(String headings, String subtitle, String contents, String email,
			AdditionalDataNotification additionalData, String deliveryTimeOfDay) throws ProviderNotificationException {

		JSONObject requestBody = null;

		try {
			requestBody = createRequestBody(headings, subtitle, contents, email, additionalData, deliveryTimeOfDay);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONObject response = this.sendRequest(null, "POST", requestBody);

		return formatResponseSend(response);
	}

	@Override
	public Object view(String sid) throws ProviderNotificationException {
		JSONObject response = this.sendRequest(sid + "?app_id=" + this.appId, "GET", null);

		return this.formatResponseView(response);
	}

	private JSONObject createRequestBody(String headings, String subtitle, String contents, String email,
			AdditionalDataNotification additionalData, String deliveryTimeOfDay) throws JSONException {

		// AppId
		JSONObject obj = new JSONObject();
		obj.put(APP_ID_KEY, this.appId);

		// Headings
		if (StringUtils.isNotBlank(headings)) {
			JSONObject headingsJ = new JSONObject();
			headingsJ.put(LANGUAGE_KEY, headings);
			obj.put(HEADINGS_KEY, headingsJ);
		}

		// Subtitle
		if (StringUtils.isNotBlank(subtitle)) {
			JSONObject subtitleJ = new JSONObject();
			subtitleJ.put(LANGUAGE_KEY, subtitle);
			obj.put(SUBTITLE_KEY, subtitleJ);
		}

		// Contents
		JSONObject contentsJ = new JSONObject();
		contentsJ.put(LANGUAGE_KEY, contents);
		obj.put(CONTENTS_KEY, contentsJ);

		// Filters
		// Presenza dell'email -> invio di una notifica personale
		if (email != null && !email.isEmpty()) {
			JSONObject emailJ = new JSONObject();
			emailJ.put(FIELD_KEY /* "field" */, FIELD_VALUE_TAG /* "tag" */);
			emailJ.put(TAG_KEY /* "key" */, TAG_VALUE_EMAIL/* "email" */);
			emailJ.put(RELATION_KEY /* "relation" */, RELATION_VALUE /* "=" */);
			emailJ.put(TAG_VALUE /* "value" */, email);

			JSONArray filtersJ = new JSONArray();
			filtersJ.put(emailJ);
			obj.put(FILTERS_KEY, filtersJ);
		} else {
			// Notifica broadcast: invio a tutti gli utenti registrati
			obj.put("included_segments", "Subscribed Users");
		}

		// Data: Eventuale mappa di dati da passare
		if (additionalData != null) {
			obj.put("data", additionalData.toJSONObject());
		}

		// Invio delayed: formato es."11:49AM"
		if (!StringUtils.isBlank(deliveryTimeOfDay)) {
			obj.put("delayed_option", "timezone");
			obj.put("delivery_time_of_day", deliveryTimeOfDay);
		}

		return obj;
	}

	private JSONObject retrieveJSonResponse(HttpURLConnection con)
			throws IOException, JSONException, OneSignalException {

		JSONObject ret = null;

		Integer httpResponse = con.getResponseCode();

		if (httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
			// Request OK
			Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
			ret = new JSONObject(scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "");
			scanner.close();

			if (log.isDebugEnabled())
				log.debug("URL OneSignal: " + con.getURL().toString() + "; Response code: " + httpResponse
						+ "; Response body: " + ret.toString());
		} else {
			// Request KO
			Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
			ret = new JSONObject(scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "");
			scanner.close();

			if (log.isDebugEnabled())
				log.debug("URL OneSignal: " + con.getURL().toString() + "; Response code: " + httpResponse
						+ "; Response body: " + ret.toString());

			throw new OneSignalException("Errore durante la chiamata OneSignal - HttpResponse: " + httpResponse);
		}

		return ret;
	}

	/**
	 * Metodo di invio della request al provider OneSignal
	 */
	private JSONObject sendRequest(String urlParams, String requestedMethod, JSONObject requestBody)
			throws ProviderNotificationException {
		JSONObject ret = null;
		HttpURLConnection con = null;

		try {
			// Creazione connessione
			con = this.createConnection(urlParams, requestedMethod);

			if (requestBody != null) {
				String strJsonBody = requestBody.toString();

				// Invio body richiesta
				byte[] sendBytes = strJsonBody.getBytes("UTF-8");
				con.setFixedLengthStreamingMode(sendBytes.length);

				OutputStream outputStream = con.getOutputStream();
				outputStream.write(sendBytes);
				outputStream.close();
			}

			// Recupero risposta JSON
			ret = retrieveJSonResponse(con);

		} catch (IOException e) {
			String errorLog = "GRAVE: Errore di IO verso "
					+ (con != null ? (con.getURL() + " - Metodo " + con.getRequestMethod()) : "");
			log.error(errorLog, e);
			throw new OneSignalException("Errore durante la chiamata OneSignal", e);
		} catch (JSONException e) {
			throw new OneSignalException("Errore durante la chiamata OneSignal", e);
		} finally {
			if (con != null)
				con.disconnect();
		}

		return ret;
	}

	private HttpURLConnection createConnection(String urlParams, String requestedMethod)
			throws ProviderNotificationException {
		HttpURLConnection con = null;

		try {
			URL url = new URL(destinationUrl + (urlParams != null ? "/" + urlParams : ""));

			if (ConstantsSingleton.Proxy.ACTIVE) {
				con = (HttpURLConnection) url.openConnection(ConstantsSingleton.Proxy.PROXY);
			} else {
				con = (HttpURLConnection) url.openConnection();
			}

			con.setUseCaches(false);
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestProperty(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
			con.setRequestProperty(AUTHORIZATION_KEY, authorizationKey);
			con.setRequestMethod(requestedMethod);

		} catch (MalformedURLException e) {
			throw new OneSignalException("Errore durante la chiamata OneSignal", e);
		} catch (IOException e) {
			throw new OneSignalException("Errore durante la chiamata OneSignal", e);
		}

		return con;
	}

	private String formatResponseSend(JSONObject jsonObject) throws ProviderNotificationException {
		String ret = null;

		if (jsonObject != null) {
			try {
				if (jsonObject.has("id"))
					ret = jsonObject.getString("id");

				Integer numRecipients = null;
				if (jsonObject.has("recipients"))
					numRecipients = jsonObject.getInt("recipients");

				JSONArray errors = null;
				if (jsonObject.has("errors"))
					errors = jsonObject.getJSONArray("errors");
				if (numRecipients == null || numRecipients.compareTo(0) == 0) {
					throw new NoRecipientException(errors.toString());
				} else if (ret == null || ret.isEmpty()) {
					throw new OneSignalException(
							"Errore durante la chiamata OneSignal - " + errors != null ? errors.toString() : "");
				}

			} catch (JSONException e) {
				throw new OneSignalException("JSONException durante la chiamata OneSignal - " + e.getMessage());
			}
		} else {
			throw new OneSignalException("Errore generico durante la chiamata OneSignal");
		}

		return ret;
	}

	private HashMap<StatoNotifica, Integer> formatResponseView(JSONObject jsonObject) {
		HashMap<StatoNotifica, Integer> ret = new HashMap<StatoNotifica, Integer>();

		Integer read = this.formatResponseView(jsonObject, "converted");
		if (read != null)
			ret.put(StatoNotifica.R, read);

		Integer delay = this.formatResponseView(jsonObject, "remaining");
		if (delay != null)
			ret.put(StatoNotifica.D, delay);

		Integer fail = this.formatResponseView(jsonObject, "failed");
		if (fail != null)
			ret.put(StatoNotifica.F, fail);

		Integer send = this.formatResponseView(jsonObject, "successful");
		if (send != null)
			ret.put(StatoNotifica.S, send);

		return ret;
	}

	private Integer formatResponseView(JSONObject jsonObject, String name) {
		Integer ret = null;

		try {
			ret = jsonObject.getInt(name);
		} catch (NullPointerException e) {
			// The specified name doesn't have any mapping
		} catch (JSONException e) {
			log.error("GRAVE:" + e.getMessage());
		}
		return ret;
	}
}
