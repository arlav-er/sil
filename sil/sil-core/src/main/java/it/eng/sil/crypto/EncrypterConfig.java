package it.eng.sil.crypto;

import java.io.IOException;

/**
 * Visibile solo dal pacchetto.
 * 
 * 
 * @author Luigi Antenucci
 */

class EncrypterConfig {

	public static byte[] getRisorsa() throws IOException {
		String key64 = "YsgLT7bveWs=";
		byte[] keyBuf = ConvBase64.decodificaBuf(key64);

		return keyBuf;

	}
}
