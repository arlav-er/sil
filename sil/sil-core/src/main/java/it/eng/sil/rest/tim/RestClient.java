package it.eng.sil.rest.tim;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
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

import it.eng.sil.sms.SmsRetryableException;

public class RestClient {
	private static final String STATUS_OK = "status='OK'";

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RestClient.class.getName());

	protected java.net.http.HttpClient client;

	protected String username;
	protected String password;
	protected String alias;
	protected String token;

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

	public RestClient() {
		client = HttpClient.newHttpClient();
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

	public void sendSms(String cellNumb, String msg) throws SmsRetryableException {

		_logger.debug("RestClient prima dell'invio dell'SMS" + this.toString());

		Map<Object, Object> data = new LinkedHashMap<>();

		data.put("username", username);
		data.put("password", password);
		data.put("token", token);
		data.put("alias", alias);
		data.put("msisdn", cellNumb);
		data.put("testo", msg);

		try {
			String boundary = new BigInteger(256, new Random()).toString();

			HttpRequest request = HttpRequest.newBuilder()
					.header("Content-Type", "multipart/form-data;boundary=" + boundary)
					.POST(ofMimeMultipartData(data, boundary)).uri(URI.create(baseUrl)).build();

			HttpResponse<String> vtResponse = client.send(request, BodyHandlers.ofString());

			if (vtResponse.statusCode() != 200) {
				throw new SmsRetryableException("Errore http: " + vtResponse.statusCode());
			}

			String body = vtResponse.body();
			if (body.indexOf(STATUS_OK) <= 0) {
				String errMsg = String.format("ERRORE NELL'INVIO DELL'SMS: response=%s", body);
				throw new SmsRetryableException(errMsg);
			}

		} catch (IOException | InterruptedException e) {
			// Eccezioni che comportano il tentativo di reinvio del messaggio
			throw new SmsRetryableException("Errore nell'invio: " + e.getMessage(), e);
		}

	}

	@Override
	public String toString() {

		StringBuilder buf = new StringBuilder();
		buf.append("username=" + username + "\n");
		buf.append("password=" + password + "\n");
		buf.append("alias=" + alias + "\n");
		buf.append("token=" + token + "\n");
		buf.append("baseUrl=" + baseUrl + "\n");

		return "restClient ( \n " + buf.toString() + ")";

	}

	public static BodyPublisher ofMimeMultipartData(Map<Object, Object> data, String boundary) {
		var byteArrays = new ArrayList<byte[]>();

		byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=")
				.getBytes(StandardCharsets.UTF_8);

		for (Map.Entry<Object, Object> entry : data.entrySet()) {
			byteArrays.add(separator);

			byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n")
					.getBytes(StandardCharsets.UTF_8));
		}
		byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));

		return BodyPublishers.ofByteArrays(byteArrays);
	}

}
