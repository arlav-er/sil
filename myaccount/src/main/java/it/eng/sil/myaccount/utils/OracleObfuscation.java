package it.eng.sil.myaccount.utils;

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

public class OracleObfuscation {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private String algorithm1 = "DES";//magical mystery constant
	private String algorithm2 = "DES/CBC/NoPadding";//magical mystery constant
	private IvParameterSpec iv = new IvParameterSpec( new byte [] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 } );
	private Cipher cipher;
	private SecretKey key;

	public OracleObfuscation ( String secretString )  {
		key = new SecretKeySpec( secretString.getBytes( ), algorithm1 );
		try {
			cipher = Cipher.getInstance( algorithm2 );
		} catch (NoSuchAlgorithmException e) {
			log.error(e);
		} catch (NoSuchPaddingException e) {
			log.error(e);
		}
	}

	public byte [] encrypt ( byte [] bytes ) {
		try {
			cipher.init( Cipher.ENCRYPT_MODE, key, iv );
			return cipher.doFinal( bytes );
		} catch (InvalidKeyException e) {
			log.error(e);
		} catch (InvalidAlgorithmParameterException e) {
			log.error(e);
		} //normally you could leave out the IvParameterSpec argument, but not with Oracle
		catch (IllegalBlockSizeException e) {
			log.error(e);
		} catch (BadPaddingException e) {
			log.error(e);
		}

		return null;
	}

	public byte [] decrypt ( byte [] bytes )  {
		try {
			cipher.init( Cipher.DECRYPT_MODE, key, iv ); 
			//normally you could leave out the IvParameterSpec argument, but not with Oracle

			return cipher.doFinal( bytes );
		} catch (InvalidKeyException e) {
			log.error(e);
		} catch (InvalidAlgorithmParameterException e) {
			log.error(e);
		} //normally you could leave out the IvParameterSpec argument, but not with Oracle
		catch (IllegalBlockSizeException e) {
			log.error(e);
		} catch (BadPaddingException e) {
			log.error(e);
		}
		
		return null;
	}				
}