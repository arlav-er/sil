package it.eng.myportal.auth;

import it.eng.myportal.utils.ConstantsSingleton;

public class SimpleProxyAuthenticator {

	private String username;
	private String password;
	private String host;
	private String port;

	private static boolean proxyActive = false;

	public SimpleProxyAuthenticator() {

		if (ConstantsSingleton.Proxy.ACTIVE && !proxyActive) {

			
			host = ConstantsSingleton.Proxy.ADDRESS;
			port = String.valueOf(ConstantsSingleton.Proxy.PORT);

			if (ConstantsSingleton.Proxy.USE_AUTHENTICATION){
				
				username = ConstantsSingleton.Proxy.USERNAME;
				password = String.valueOf(ConstantsSingleton.Proxy.PASSWORD);
				System.setProperty("http.proxyUser", username);
				System.setProperty("http.proxyPassword", password);
				System.setProperty("https.proxyUser", username);
				System.setProperty("https.proxyPassword", password);
			}

			System.setProperty("http.proxyHost", host);
			System.setProperty("http.proxyPort", port);

			System.setProperty("https.proxyHost", host);
			System.setProperty("https.proxyPort", port);

			proxyActive = true;

		}

	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public boolean isProxyActivated() {
		return proxyActive;
	}
}