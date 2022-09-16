package it.eng.sil.myaccount.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Utils {

	private static Log log = LogFactory.getLog(Utils.class);

	public static class MD5 {

		public static String encrypt(String message) {
			try {
				MessageDigest m = MessageDigest.getInstance("MD5");
				m.update(message.getBytes());
				return String.format("%032x", new BigInteger(1, m.digest()));
			} catch (Exception e) {
				return null;
			}
		}
	}

	public static class SHA1 {

		public static String encrypt(String message) {
			Formatter formatter = new Formatter();
			try {
				MessageDigest m = MessageDigest.getInstance("SHA1");
				m.update(message.getBytes());

				for (byte b : m.digest()) {
					formatter.format("%02x", b);
				}
				return formatter.toString();
			} catch (Exception e) {
				return null;
			} finally {
				formatter.close();
			}
		}

		/*
		 * public static boolean compare(String plainMessage, String encryptedMessage) { String encryptedPlainMessage =
		 * encrypt(plainMessage); if (encryptedPlainMessage != null && !encryptedPlainMessage.equals("")) { return
		 * encryptedMessage.equals(encryptedPlainMessage); } return false; }
		 */
	}

	public static String buildTokenSecurity(String completeUrl) {
		char[] pretto = { 't', 'o', 'd', 'o', 'd', 'a', 'd', 'e', 'f', 'i', 'n', 'i', 'r' };
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataFormattata = dateFormat.format(new Date());
		String token = new String(pretto) + dataFormattata + completeUrl + completeUrl.length();
		String encrypted = SHA1.encrypt(token);
		log.debug("token chiaro=" + token);
		log.debug("token criptato=" + encrypted);
		return encrypted;
	}

	/**
	 * Genera una stringa casuale
	 * 
	 * @param iLen
	 *            lunghezza della stringa generata
	 * @returnla stringa generata da pegoraro recion
	 */
	public static String randomString(int iLen) {
		String sChrs = "qwertyuioplgdonbfdndvnxcvbnm123456221113";
		StringBuilder sRnd = new StringBuilder("");
		for (int i = 0; i < iLen; i++) {
			int randomPoz = (int) (Math.floor(Math.random() * sChrs.length()));
			sRnd.append(sChrs.substring(randomPoz, randomPoz + 1));
		}
		return sRnd.toString();
	}

	public static String escapeCharacters(String parameter) throws UnsupportedEncodingException {
		return URLEncoder.encode(parameter, "UTF-8");
	}

	public static boolean isTokenSecurity(String value, String completeUrl) {

		char[] pretto = { 't', 'o', 'd', 'o', 'd', 'a', 'd', 'e', 'f', 'i', 'n', 'i', 'r' };
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataFormattata = dateFormat.format(new Date());
		String token = new String(pretto) + dataFormattata + completeUrl + completeUrl.length();
		log.info("token chiaro=" + token);

		String encrypted = SHA1.encrypt(token);

		log.info("token criptato=" + encrypted);
		log.info("input token criptato=" + value);

		return value.equals(encrypted);
	}

	public static String getTokenUtente(String value) {

		OracleObfuscation desObf = new OracleObfuscation("TODO DA DEFINIRE");
		byte[] encrypted = desObf.encrypt((value.toString()).getBytes());
		// Encrypt
		String userCript = new String(Hex.encodeHex(encrypted));

		return userCript;
	}

	public static String getUserFromTokenUtente(String encryptTokenUtente) {

		OracleObfuscation desObf = new OracleObfuscation("TODO DA DEFINIRE");
		String user = null;
		byte[] decoded;
		try {
			decoded = Hex.decodeHex(encryptTokenUtente.toCharArray());
			user = new String(desObf.decrypt(decoded));

		} catch (DecoderException e) {
			log.error("Errore decrypt token criptato=" + encryptTokenUtente);
		}

		return user;
	}

	public static String getRenderCodServiziAmministrativi(String codServiziAmministrativi) {
		StringBuilder sb = new StringBuilder(codServiziAmministrativi);
		sb.insert(2, "-");
		return sb.toString();
	}
}
