package it.eng.sil.crypto;

import java.io.InputStream;
import java.io.ObjectInputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Classe di utilità per la Secret Key DES Visibile solo dal pacchetto.
 * 
 * @author Luigi Antenucci
 */
public abstract class DesEncSecretKey {

	/**
	 * Genera una nuova chiave per l'algoritmo di cifratura.
	 */
	public static final SecretKey generateSecretKey() throws Exception {

		KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey;
	}

	/**
	 * Dato un file, lo apre e ne legge la chiave contenuta e la rende.
	 */
	public static final SecretKey loadSecretKey(InputStream is) throws Exception {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(is);
			SecretKey secretKey = (SecretKey) ois.readObject();
			ois.close();
			return secretKey;
		} catch (Exception e) {
			throw new Exception("Impossibile caricare la chiave dallo stream");
		} finally {
			if (ois != null)
				try {
					ois.close();
				} catch (Exception e) {
				}
		}
	}

}
