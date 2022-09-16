package it.eng.myportal.auth;

import it.eng.myportal.utils.ConstantsSingleton;

import java.io.IOException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

public class HttpClientHelper {
	
	HttpClient httpClient = null;
	
	public HttpClientHelper() {

		httpClient = new HttpClient();

		if (ConstantsSingleton.Proxy.ACTIVE) {
			if (ConstantsSingleton.Proxy.USE_AUTHENTICATION){
				String geocoderUsername = ConstantsSingleton.Proxy.USERNAME;
				String geocoderPassword = new String(ConstantsSingleton.Proxy.PASSWORD);
				Credentials defaultcreds = new UsernamePasswordCredentials(geocoderUsername, geocoderPassword);
				httpClient.getState().setProxyCredentials(AuthScope.ANY, defaultcreds);
			}
			httpClient.getHostConfiguration().setProxy(ConstantsSingleton.Proxy.ADDRESS, ConstantsSingleton.Proxy.PORT);
		}
	
	}

	public byte[] retrieveImage(String urlImage) throws IOException {

		
	//	client.getParams().setParameter("http.useragent", "Test Client");
	//	client.getParams().setParameter("http.connection.timeout",
	//			new Integer(5000));

		GetMethod method = new GetMethod();

		method.setFollowRedirects(true);
		method.setURI(new URI(urlImage, true));
		int returnCode = httpClient.executeMethod(method);
		
		if (returnCode != HttpStatus.SC_OK) {
			method.releaseConnection();
			throw new IOException(
					"Problemi nel recupero dell'immagine alla seguente URL: "
							+ urlImage + " Codice di ritorno: " + returnCode);
		}

		byte[] imageData = method.getResponseBody();
		
		
//		FileOutputStream f = new FileOutputStream("/tmp/immagine.jpg");
//		f.write(imageData);
//		f.close();
		
		method.releaseConnection();

		return imageData;

	}
	
		
}