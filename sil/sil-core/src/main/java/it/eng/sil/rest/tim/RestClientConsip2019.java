package it.eng.sil.rest.tim;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.sil.sms.SmsRetryableException;

public class RestClientConsip2019 extends RestClient {

	static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RestClientConsip2019.class.getName());

	final String AUTH_TOKEN_URL = "";
	final String POST_SMS_URL = "";

	public RestClientConsip2019() {

	}

	public void sendSms(String cellNumb, String msg) throws SmsRetryableException {

		String tokenJwt = getTokenJwtByRemoteInvoke();
		LOG.debug("TOKEN JWT:" + tokenJwt);

		sendSmsByRemoteInvoke(tokenJwt, cellNumb, msg);
		LOG.debug("Inviato con successo");

	}

	private String getTokenJwtByRemoteInvoke() throws SmsRetryableException {

		String auth = getToken() + "/" + getUsername() + ":" + getPassword();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
		String authHeader = "Basic " + new String(encodedAuth);

		var request = HttpRequest.newBuilder().GET().uri(URI.create(AUTH_TOKEN_URL))
				.setHeader("Authorization", authHeader).build();

		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			return response.body();
		} catch (IOException | InterruptedException e) {
			// Eccezioni che comportano il tentativo di reinvio del messaggio
			throw new SmsRetryableException("HttpException: " + e.getMessage(), e);
		}
	}

	private void sendSmsByRemoteInvoke(String tokenJwt, String cellNumb, String msg) throws SmsRetryableException {

		// PostMethod postMethod = new PostMethod(POST_SMS_URL);
		// postMethod.setRequestHeader("X-Token", tokenJwt);

		try {

			String payloadSend = makeJsonPayload(cellNumb, msg).toString();

			String boundary = new BigInteger(256, new Random()).toString();

			Map<Object, Object> data = new LinkedHashMap<>();

			data.put("payloadSend", payloadSend);

			HttpRequest request = HttpRequest.newBuilder()
					.header("Content-Type", "multipart/form-data;boundary=" + boundary).header("X-Token", tokenJwt)
					.POST(ofMimeMultipartDataJson(data, boundary)).uri(URI.create(POST_SMS_URL)).build();

			if (LOG.isDebugEnabled()) {
				LOG.debug(payloadSend);
			}

			// StringPart stringPart = new StringPart("payloadSend", payloadSend.toString());
			// stringPart.setContentType("application/json");
			// Part[] parts = {
			// stringPart };
			//
			// postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));

			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			// return response.body();

			int statusCode = response.statusCode();
			if (statusCode >= 300) {
				throw new SmsRetryableException("HTTP ERROR [ " + statusCode + "] " + statusCode);
			}

			String jsonString = response.body();
			// System.out.println(jsonString);
			JSONObject json = new JSONObject(jsonString);
			String message = json.getString("message");

			if (!"OK".equalsIgnoreCase(message)) {
				String errMsg = String.format("ERRORE NELL'INVIO DELL'SMS: response=%s", jsonString);
				throw new SmsRetryableException(errMsg);
			}
		} catch (JSONException e) {
			// L'eccezione Json non si rilancia, la si logga solamente
			LOG.error("Errore in composizione JSONOject: " + e.getMessage(), e);
		} catch (IOException | InterruptedException e) {
			// Eccezioni che comportano il tentativo di reinvio del messaggio
			throw new SmsRetryableException("Exception: " + e.getMessage(), e);
		}

	}

	private JSONObject makeJsonPayload(String cellNumb, String msg) throws JSONException {

		JSONObject campaignChannelValue = new JSONObject();
		JSONObject campaignMessageValue = new JSONObject();

		campaignMessageValue
				// .put("messageType", JSONObject.NULL)
				.put("messageContent", msg)
				// .put("template", JSONObject.NULL)
				// .put("file", "string")
				.put("msisdn", cellNumb)
		// .put("listMsisdn", JSONObject.NULL)
		// .put("fileMsisdn", "null")
		// .put("name", JSONObject.NULL)
		// .put("surname", JSONObject.NULL)
		// .put("extra", JSONObject.NULL)
		// .put("status", "string")
		// .put("delivered", "string");
		;

		campaignChannelValue.put("channelName", "UCP")
		// .put("zone", JSONObject.NULL)
		// .put("zoneType", JSONObject.NULL)
		// .put("collectionTime", JSONObject.NULL)
		;

		JSONObject json = new JSONObject().put("alias", getAlias())
				// .put("startDate", JSONObject.NULL)
				// .put("endDate", JSONObject.NULL)
				// .put("period", JSONObject.NULL)
				// .put("recurrencesNumber", JSONObject.NULL)
				// .put("recurrencesInterval", JSONObject.NULL)
				// .put("validityPeriod", JSONObject.NULL)
				.put("sentNotify", false).put("deliveredNotify", false)
				// .put("msisdnToNotify", JSONObject.NULL)
				.put("sr", true).put("campaignChannel", campaignChannelValue)
				.put("campaignMessage", campaignMessageValue);

		return json;

	}

	public static BodyPublisher ofMimeMultipartDataJson(Map<Object, Object> data, String boundary) {
		var byteArrays = new ArrayList<byte[]>();

		byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=")
				.getBytes(StandardCharsets.UTF_8);

		for (Map.Entry<Object, Object> entry : data.entrySet()) {
			byteArrays.add(separator);

			byteArrays.add(("\"" + entry.getKey() + "\"\r\n" + "Content-Type: application/json\r\n" +

					"\r\n" + entry.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
		}
		byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));

		return BodyPublishers.ofByteArrays(byteArrays);
	}

}
