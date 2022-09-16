package it.eng.sil.myauthservice.rest.client.sms;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.sil.myauthservice.model.ejb.business.sms.SmsException;


public class ClientRestTIM2017 implements ProviderSms {
	protected static Logger LOG = Logger.getLogger(ClientRestTIM2017.class.getName());
	

	protected String username;
	protected String password;
	protected String alias;
	protected String token;
	protected HttpClient client;

	private String baseUrl;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public ClientRestTIM2017() {
		client = new HttpClient();
	}

	public String getAlias() {
		return alias;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}
 

	public void sendSms(String cellNumb, String msg) throws SmsException {

		LOG.info("RestClient prima dell'invio dell'SMS" + this.toString());
		
		PostMethod postMethod = new PostMethod(baseUrl);
		postMethod.setRequestHeader("accept", "application/json");

		Part[] parts = { new StringPart("username", username), new StringPart("password", password),
				new StringPart("token", token), new StringPart("alias", alias), new StringPart("msisdn", cellNumb),
				new StringPart("testo", msg),

		};
		postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));

		try {

			int statusCode = this.client.executeMethod(postMethod);
			if (statusCode != HttpStatus.SC_OK) {
				throw new SmsException("Errore http: " + postMethod.getStatusLine());
			}

			String jsonString = postMethod.getResponseBodyAsString();
			JSONObject json = new JSONObject(jsonString);
			String status = json.getString("status");

			if (!"OK".equalsIgnoreCase(status)) {
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


	@Override
	public String toString() {
		
		StringBuilder buf = new StringBuilder();
		buf.append("username="+ username + "\n" );
		buf.append("password="+ password + "\n" );
		buf.append("alias="+ alias + "\n" );
		buf.append("token="+ token + "\n" );
		buf.append("baseUrl="+ baseUrl + "\n" );
		
		return "restClient ( \n " + buf.toString() + ")";
		
	}
	
}
