package it.eng.sil.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import org.jasig.cas.adaptors.jdbc.QueryDatabaseAuthenticationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SHA1 {
	private static final Logger LOG = LoggerFactory.getLogger(SHA1.class);
	public static String encrypt(String message) {

		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			String msg = String.format("Errore nella generazione dello SHA1 per la stringa '%s'", message);
			LOG.error(msg, e);
			throw new RuntimeException(e);
		}
		m.update(message.getBytes());
		Formatter formatter = new Formatter();
		for (byte b : m.digest()) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}
	
	public static boolean compare(String plainMessage, String encryptedMessage) {
		String encryptedPlainMessage = encrypt(plainMessage);
		if(encryptedPlainMessage != null || encryptedPlainMessage.equals("")) {
			return encryptedMessage.equals(encryptedPlainMessage);
		}
		return false;
	}
	
//	public static void main(String[] args){
//		
//		System.out.println(encrypt("aaaa"));
//	}
	
}


