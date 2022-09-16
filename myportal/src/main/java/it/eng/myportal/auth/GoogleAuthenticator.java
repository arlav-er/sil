package it.eng.myportal.auth;

import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.Auth;

import java.io.IOException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleAuthenticator {

	private static final String AUTHORIZE_TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
	private static final String AUTHORIZE_URL = "https://accounts.google.com/o/oauth2/auth";
	private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/userinfo/v2/me";

	private static final String SCOPE = "https://www.googleapis.com/auth/plus.login "
			+ "https://www.googleapis.com/auth/plus.me " 
			+ "https://www.googleapis.com/auth/userinfo.email "
			+ "https://www.googleapis.com/auth/userinfo.profile";

	private static final Log log = LogFactory.getLog(GoogleAuthenticator.class);

	private HttpClient httpClient;
	String URL_CALLBACK = ConstantsSingleton.BASE_URL + "/Google/LoginServlet";

	public GoogleAuthenticator() {
		httpClient = new HttpClient();

		if (ConstantsSingleton.Proxy.ACTIVE) {
			if (ConstantsSingleton.Proxy.USE_AUTHENTICATION){
				String username = ConstantsSingleton.Proxy.USERNAME;
				String password = new String(ConstantsSingleton.Proxy.PASSWORD);
				Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
				httpClient.getState().setProxyCredentials(AuthScope.ANY, defaultcreds);
			}
			httpClient.getHostConfiguration().setProxy(ConstantsSingleton.Proxy.ADDRESS, ConstantsSingleton.Proxy.PORT);
			
		}

	}

	public String make01treq() {

		NameValuePair[] data = { new NameValuePair("redirect_uri", URL_CALLBACK),
				new NameValuePair("response_type", "code"), new NameValuePair("client_id", Auth.GOOGLE_ID),
				new NameValuePair("scope", SCOPE), new NameValuePair("approval_prompt", "force"),
				new NameValuePair("access_type", "offline") };

		String rURL = AUTHORIZE_URL + "?";
		for (int i = 0; i < data.length; i++) {
			rURL += data[i].getName() + "=" + data[i].getValue() + "&";
		}

		log.info("URL di redirezione: " + rURL);

		return rURL;

	}

	public String make02req(String code) throws JSONException, IOException {

		PostMethod post = new PostMethod(AUTHORIZE_TOKEN_URL);

		NameValuePair[] data = { new NameValuePair("code", code), new NameValuePair("redirect_uri", URL_CALLBACK),
				new NameValuePair("client_id", Auth.GOOGLE_ID), new NameValuePair("client_secret", Auth.GOOGLE_SECRET),
				new NameValuePair("grant_type", "authorization_code") };

		post.addParameters(data);
		String risposta = null;

		try {
			httpClient.executeMethod(post);
			risposta = post.getResponseBodyAsString();
			log.debug(risposta);
		} finally {
			post.releaseConnection();
		}
		String authorization = null;
		JSONObject json = new JSONObject(risposta);
		String access_token = json.getString("access_token");
		String token_type = json.getString("token_type");
		authorization = token_type + " " + access_token;

		return authorization;

	}

	public String make03req(String code) throws IOException {

		GetMethod post = new GetMethod(PROTECTED_RESOURCE_URL);

		post.addRequestHeader("Authorization", code);
		String risposta = null;

		httpClient.executeMethod(post);
		risposta = post.getResponseBodyAsString();
		log.debug(risposta);

		return risposta;

	}

}
