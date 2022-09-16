package it.eng.myportal.auth;

import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.Auth;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.oauth.OAuthService;

public class FacebookAuthenticator {

	public static final String STATE = "state";

	public OAuthService getoAuthService() {
		return oAuthService;
	}

	private OAuthService oAuthService;

	public FacebookAuthenticator() {
		oAuthService = buildOAuthService(Auth.FACEBOOK_ID, Auth.FACEBOOK_SECRET);

	}

	private OAuthService buildOAuthService(String clientId, String clientSecret) {
		return new ServiceBuilder().apiKey(clientId).apiSecret(clientSecret)
				.callback(ConstantsSingleton.BASE_URL + "/Facebook/LoginServlet").provider(FacebookApi.class).build();
	}
}