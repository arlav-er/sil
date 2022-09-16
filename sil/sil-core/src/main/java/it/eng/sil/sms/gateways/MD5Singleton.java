/*
 * Creato il 6-ott-04
 * Author: vuoto
 * 
 */
package it.eng.sil.sms.gateways;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Singleton {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MD5Singleton.class.getName());

	private static final String PROVIDER = "MD5";

	private static MD5Singleton _instance = null;
	private MessageDigest md5 = null;

	public static MD5Singleton getInstance() throws InvalidKeyException, NoSuchAlgorithmException {
		if (_instance == null) {
			synchronized (MD5Singleton.class) {
				if (_instance == null)
					_instance = new MD5Singleton();
			}
		}
		return _instance;
	}

	private MD5Singleton() throws InvalidKeyException, NoSuchAlgorithmException {
		try {
			md5 = MessageDigest.getInstance(PROVIDER);
		} catch (NoSuchAlgorithmException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "AsymmetricProviderSingleton - NoSuchAlgorithmException", ex);

			throw ex;
		}
	}

	public String enCrypt(String value) {

		char[] charArray = value.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = this.md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}

		return hexValue.toString();

	}

	public static void main(String[] args) {

		try {

			MD5Singleton i = MD5Singleton.getInstance();
			for (int j = 0; j < 10000; j++) {
				System.out.println(i.enCrypt("Franco Vuoto n. " + j));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}