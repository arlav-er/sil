package it.eng.sil.crypto;

import java.util.Base64;

/**
 * Conversioni di stringhe da/a Base-64.
 * 
 * @author Luigi Antenucci
 */
public abstract class ConvBase64 {

	/**
	 * Rende la codifica in Base64 del contenuto del buffer passato.
	 */
	public static final String codificaBuf(byte[] buffer) {

		return Base64.getEncoder().encodeToString(buffer);
	}

	public static final String codificaStr(String str) {

		byte[] buffer = str.getBytes();
		return codificaBuf(buffer);
	}

	/**
	 * Rende la decodifica dalla Base64 del contenuto della stringa passata.
	 */
	public static final byte[] decodificaBuf(String strB64) {

		return Base64.getDecoder().decode(strB64);

	}

	public static final String decodificaStr(String strB64) {

		byte[] buffer = decodificaBuf(strB64);
		String str = new String(buffer);
		return str;
	}

}
