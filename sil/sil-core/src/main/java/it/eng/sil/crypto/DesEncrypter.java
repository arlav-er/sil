package it.eng.sil.crypto;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Esegue una cifratura/decifratura in DES.
 * 
 * @author Franco Vuoto e Luigi Antenucci
 * @version 3.3
 */
public class DesEncrypter extends Encrypter {

	private static final String className = "DesEncrypter";

	/** Parametri di configurazione degli oggetti di cifratura */
	private static final String ALG_DES = "DES";
	private static final String ALG_MODE_PADDING = "DES/CBC/PKCS5Padding";
	// sintassi: "algorithm/mode/padding"

	// L'array di 8-byte per l'inizializzazione
	protected static final byte[] IV = new byte[] { (byte) 0xEE, (byte) 0xF2, (byte) 0x14, (byte) 0xCA, (byte) 0xAA,
			(byte) 0x17, (byte) 0x7B, (byte) 0x55 };

	private static SecretKey secretKey;
	private static AlgorithmParameterSpec paramSpec;

	/* PARTE DI INIZIALIZZAZIONE DELLA CLASSE (inizializza gli elementi di classe) */
	static {
		initClass();
	}

	/**
	 * Inizializzazione dei componenti di classe a seconda dei valori di configurazione
	 */
	private static void initClass() {

		log.debug(className + ": initClass()");

		try {
			// Carica la chiave segreta per l'algoritmo di cifratura
			secretKey = new SecretKeySpec(EncrypterConfig.getRisorsa(), ALG_DES);

			// Genera i parametri specifici per l'algoritmo di cifratura
			paramSpec = new IvParameterSpec(IV);

			log.debug(className + ": initClass(): classe inizializzata");
		} catch (Exception e) {
			log.error("ERRORE INIZIALIZZAZIONE CLASSE " + className + ": " + e.getMessage(), e);
		}
	}

	/**
	 * Costruttore, visibile solo nel pacchetto
	 */
	public DesEncrypter() throws EncrypterException {

		if (log.isDebugEnabled())
			log.debug(className + ": costruttore");

		try {

			// Inizializzazione cifratura
			// (nota: CBC richiede un vettore di inizializzazione)
			eCipher = Cipher.getInstance(ALG_MODE_PADDING);
			eCipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);

			// Inizializzazione decifratura
			dCipher = Cipher.getInstance(ALG_MODE_PADDING);
			dCipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);

			if (log.isInfoEnabled())
				log.info(className + ": costruttore, istanza creata");
		} catch (Exception e) {
			eCipher = null;
			dCipher = null;
			log.error("Errore nel costruttore della classe " + className + ": " + e.getMessage(), e);
			throw new EncrypterException("Errore nell'inizializzazione del componente di cifratura");
		}
	}

	public static void main(String[] args) {
		if (args.length == 0) {

			System.out.println("USAGE: java it.eng.sil.crypto.DesEncrypter pwdDaCrittare ...");
			// System.exit(0);
			return;
		}

		DesEncrypter des;
		try {
			for (int i = 0; i < args.length; i++) {

				des = new DesEncrypter();

				String strTxtChiaro = args[i];
				// System.out.println(strTxtChiaro);

				byte[] bufTxtCifrato = des.encryptBuffer(strTxtChiaro);
				String strTxtCifratoB64 = ConvBase64.codificaBuf(bufTxtCifrato);

				// DECODIFICA dalla BASE64
				byte[] bufTxtCifrato2 = ConvBase64.decodificaBuf(strTxtCifratoB64);

				// DECIFRO
				byte[] bufTxtChiaro2 = des.decryptBuffer(bufTxtCifrato2);
				String strTxtChiaro2 = new String(bufTxtChiaro2);
				
				StringBuffer str = new StringBuffer();
				str.append(args[i] + " --> " + strTxtCifratoB64 + " --> " + strTxtChiaro2);
				
				if (args[i].equals(strTxtChiaro2)) {
					str.append(" ----- OK. Le chiavi corrispondono!! -----");
				} else {
					str.append(" ----- KO. GRAVE!! Le chiavi non corrispondono!! -----");
				}
				
				System.out.println(str.toString());
			}

		} catch (EncrypterException e) {

			e.printStackTrace();
		}

	}

}