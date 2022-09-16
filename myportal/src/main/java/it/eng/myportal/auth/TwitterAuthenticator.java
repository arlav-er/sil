package it.eng.myportal.auth;

import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.Auth;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.oauth.OAuthService;

public class TwitterAuthenticator {

	public OAuthService getoAuthService() {
		return oAuthService;
	}

	private OAuthService oAuthService;

	public TwitterAuthenticator() {
		oAuthService = buildOAuthService(Auth.TWITTER_ID, Auth.TWITTER_SECRET);

	}

	private OAuthService buildOAuthService(String clientId, String clientSecret) {

		return new ServiceBuilder().apiKey(clientId).apiSecret(clientSecret)
				.callback(ConstantsSingleton.BASE_URL + "/Twitter/LoginServlet").provider(TwitterApi.class).build();
	}
}