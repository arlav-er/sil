/*
 * Creato il 6-ott-04
 * Author: vuoto
 * 
 */
package it.eng.sil.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AsymmetricProviderSingleton {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(AsymmetricProviderSingleton.class.getName());
	private static final String PROVIDER = "HmacSHA1";

	private static AsymmetricProviderSingleton _instance = null;
	private Mac mac = null;

	public static AsymmetricProviderSingleton getInstance() {
		if (_instance == null) {
			synchronized (AsymmetricProviderSingleton.class) {
				if (_instance == null)
					_instance = new AsymmetricProviderSingleton();
			}
		}
		return _instance;
	}

	private AsymmetricProviderSingleton() {
		SecretKey key = new SecretKeySpec(keyBytes, PROVIDER);
		try {
			mac = Mac.getInstance(PROVIDER);
			mac.init(key);
		} catch (InvalidKeyException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "AsymmetricProviderSingleton - InvalidKeyException", ex);

		} catch (NoSuchAlgorithmException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "AsymmetricProviderSingleton - NoSuchAlgorithmException", ex);

		}
	}

	public String enCrypt(String value) {
		byte[] result = mac.doFinal(value.getBytes());
		return Base64.getEncoder().encodeToString(result);
	}

	private static byte[] keyBytes = { (byte) 0x06, (byte) 0xAB, (byte) 0x12, (byte) 0xE4, (byte) 0xE4, (byte) 0xE4,
			(byte) 0xE4, (byte) 0x12, (byte) 0x13, (byte) 0xE4, (byte) 0x12, (byte) 0xCC, (byte) 0xEF, (byte) 0xE4,
			(byte) 0x06, (byte) 0x07, (byte) 0xE4, (byte) 0x07, (byte) 0x12, (byte) 0xCD, (byte) 0xE4, (byte) 0x07,
			(byte) 0xFE, (byte) 0xFF, (byte) 0x07, (byte) 0xE4, (byte) 0x08 };

	public static void main(String[] args) {

		AsymmetricProviderSingleton i = AsymmetricProviderSingleton.getInstance();
		for (int j = 0; j < 10000; j++) {
			System.out.println(i.enCrypt("Franco Vuoto n. " + j));
		}

	}
}