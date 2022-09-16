package it.eng.myportal.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Obfuscation {
	protected final Log log = LogFactory.getLog(this.getClass());

	private String algorithm1;
	private String algorithm2;
	private IvParameterSpec iv;
	private Cipher cipher;
	private SecretKey key;

	public Obfuscation(String secretString, String algorithm1, String algorithm2, IvParameterSpec iv) {
		this.algorithm1 = algorithm1;
		this.algorithm2 = algorithm2;
		this.iv = iv;

		key = new SecretKeySpec(secretString.getBytes(), this.algorithm1);
		try {
			cipher = Cipher.getInstance(this.algorithm2);
		} catch (NoSuchAlgorithmException e) {
			log.error(e);
		} catch (NoSuchPaddingException e) {
			log.error(e);
		}
	}

	/**
	 * 
	 * @param bytes
	 * 
	 * 
	 * @return
	 */

	public byte[] encrypt(byte[] bytes) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			return cipher.doFinal(bytes);
		} catch (InvalidKeyException e) {
			log.error(e);
		} catch (InvalidAlgorithmParameterException e) {
			log.error(e);
		} // normally you could leave out the IvParameterSpec argument, but not with Oracle
		catch (IllegalBlockSizeException e) {
			log.error(e);
		} catch (BadPaddingException e) {
			log.error(e);
		}

		return null;
	}

	public byte[] decrypt(byte[] bytes) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			// normally you could leave out the IvParameterSpec argument, but not with Oracle

			return cipher.doFinal(bytes);
		} catch (InvalidKeyException e) {
			log.error(e);
		} catch (InvalidAlgorithmParameterException e) {
			log.error(e);
		} // normally you could leave out the IvParameterSpec argument, but not with Oracle
		catch (IllegalBlockSizeException e) {
			log.error(e);
		} catch (BadPaddingException e) {
			log.error(e);
		}

		return null;
	}
}
