package it.eng.sil.myauthservice.rest.client.sms;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.sil.myauthservice.model.ejb.business.sms.SmsException;

public class ClientRestConsip2019 extends ClientRestTIM2017 {

	protected static Logger LOG = Logger.getLogger(ClientRestConsip2019.class.getName());

	final String AUTH_TOKEN_URL = "";
	final String POST_SMS_URL = "";

	public ClientRestConsip2019() {

	}

	public void sendSms(String cellNumb, String msg) throws SmsException {

		String tokenJwt = getTokenJwtByRemoteInvoke();
		LOG.info("TOKEN JWT:" + tokenJwt);
		sendSmsByRemoteInvoke(tokenJwt, cellNumb, msg);
		LOG.info("Inviato con successo");

	}

	private String getTokenJwtByRemoteInvoke() throws SmsException {
		GetMethod getMethod = new GetMethod(AUTH_TOKEN_URL);

		String auth = getToken() + "/" + getUsername() + ":" + getPassword();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
		String authHeader = "Basic " + new String(encodedAuth);

		getMethod.setRequestHeader("Authorization", authHeader);

		try {
			client.executeMethod(getMethod);
			return getMethod.getResponseBodyAsString();

		} catch (JSONException | IOException e) {
			throw new SmsException(e);
		}

	}

	private void sendSmsByRemoteInvoke(String tokenJwt, String cellNumb, String msg) throws SmsException {
		PostMethod postMethod = new PostMethod(POST_SMS_URL);
		postMethod.setRequestHeader("X-Token", tokenJwt);

		try {

			String payloadSend = makeJsonPayload(cellNumb, msg).toString();

			StringPart stringPart = new StringPart("payloadSend", payloadSend.toString());
			stringPart.setContentType("application/json");
			Part[] parts = { stringPart };

			postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));

			int statusCode = this.client.executeMethod(postMethod);
			if (statusCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
				throw new SmsException("HTTP ERROR [ " + statusCode + "] " + postMethod.getStatusLine());
			}

			String jsonString = postMethod.getResponseBodyAsString();
			System.out.println(jsonString);
			JSONObject json = new JSONObject(jsonString);
			String message = json.getString("message");

			if (!"OK".equalsIgnoreCase(message)) {
				String errMsg = String.format("ERRORE NELL'INVIO DELL'SMS: response=%s", jsonString);
				throw new SmsException(errMsg);
			}

		} catch (JSONException | IOException e) {
			throw new SmsException(e);
		}

		finally {
			postMethod.releaseConnection();
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

}
