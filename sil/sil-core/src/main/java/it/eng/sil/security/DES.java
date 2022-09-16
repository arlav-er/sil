/*
 * Created on 16-ott-06
 * @author vuoto
 *
 */

package it.eng.sil.security;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DES {

	// Chiave DES
	private final static byte[] DESKeyBytes = { 121, -5, 2, 44, 86, -127, 48, 90 };

	// Per il modo CBC IV
	private final static byte[] ivBytes = { 45, 81, -57, -1, 54, 117, 106, 64 };

	private Cipher encryptCipher;
	private Cipher decryptCipher;

	private static DES _instance = null;

	public static DES getInstance() throws Exception {
		if (_instance == null) {
			synchronized (DES.class) {
				if (_instance == null) {
					_instance = new DES(DESKeyBytes, ivBytes, "ASCII");

				}

			}
		}
		return _instance;
	}

	private DES(byte[] keyBytes, byte[] ivBytes, String characterEncoding) throws Exception {
		SecretKey key = new SecretKeySpec(keyBytes, "DES");
		IvParameterSpec iv = new IvParameterSpec(ivBytes);
		this.encryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding", "SunJCE");
		this.encryptCipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key, iv);
		this.decryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding", "SunJCE");
		this.decryptCipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, iv);
	}

	private DES() {

	}

	public String encrypt(String password) throws Exception {
		byte[] passwordBytes = password.getBytes();
		byte[] encryptedPasswordBytes = this.encryptCipher.doFinal(passwordBytes);
		String encodedEncryptedPassword = Base64.getEncoder().encodeToString(encryptedPasswordBytes);
		return encodedEncryptedPassword;
	}

	public String decrypt(String encodedEncryptedPassword) throws Exception {
		byte[] encryptedPasswordBytes = Base64.getDecoder().decode(encodedEncryptedPassword);
		byte[] passwordBytes = this.decryptCipher.doFinal(encryptedPasswordBytes);
		String recoveredPassword = new String(passwordBytes);
		return recoveredPassword;
	}

}
