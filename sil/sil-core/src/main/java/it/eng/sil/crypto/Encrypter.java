package it.eng.sil.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import org.apache.log4j.Logger;

/**
 * Un generico algoritmo di cifratura per la nostra applicazione.
 * 
 * @author Luigi Antenucci
 */
public abstract class Encrypter {

	private static final String className = "Encrypter";

	protected static Logger log = Logger.getLogger(Encrypter.class.getName());

	static {
		initClass();
	}

	/**
	 * Inizializzazione dei componenti di classe "secretKey" e "paramSpec", a seconda del valore del parametro di
	 * configurazione "des_encrypter.enable".
	 */
	private static void initClass() {

		try {

			log.debug(className + ": initClass(): classe inizializzata");
		} catch (Exception e) {
			log.error("ERRORE INIZIALIZZAZIONE CLASSE: " + e.getMessage(), e);
		}
	}

	/**
	 * Elementi di base della cifratura/decifratura (per l'istanza)
	 */
	protected Cipher eCipher = null;
	protected Cipher dCipher = null;

	/**
	 * Rende TRUE se la cifratura è abilitata. L'abilitazione avviene attraverso un parametro di configurazione.
	 */

	/*
	 * ######################## Metodi di incapsulazione ########################
	 */

	/**
	 * Rende l'OutputStream dato incapsulato in un CipherOutputStream
	 */
	public OutputStream getCipherOutputStream(OutputStream out) throws EncrypterException {
		if (eCipher == null)
			throw new EncrypterException("Oggetto di cifratura/decifratura non inizializzato");
		return new CipherOutputStream(out, eCipher);
	}

	/**
	 * Rende l'InputStream dato incapsulato in un CipherInputStream
	 */
	public InputStream getCipherInputStream(InputStream in) throws EncrypterException {
		if (dCipher == null)
			throw new EncrypterException("Oggetto di cifratura/decifratura non inizializzato");
		return new CipherInputStream(in, dCipher);
	}

	/*
	 * ################### Metodi su un blocco ###################
	 */

	/**
	 * Cifra il contenuto del buffer di byte "bufTxtChiaro" usando il certificato passato e rende il risultato
	 * sottoforma di un analogo buffer di byte.
	 */
	public byte[] encryptBuffer(byte[] bufTxtChiaro) throws EncrypterException {

		if (eCipher == null)
			throw new EncrypterException("Oggetto di cifratura/decifratura non inizializzato");
		try {
			byte[] bufTxtCifrato = eCipher.doFinal(bufTxtChiaro, 0, bufTxtChiaro.length);
			return bufTxtCifrato;
		} catch (GeneralSecurityException e) {
			throw new EncrypterException(e.getMessage());
		}
	}

	public byte[] encryptBuffer(String strTxtChiaro) throws EncrypterException {

		return encryptBuffer(strTxtChiaro.getBytes());
	}

	/**
	 * Decifra il contenuto del buffer di byte "bufTxtCifrato" usando la chiave privata passata e rende il risultato
	 * sottoforma di un analogo buffer di byte. Per ottenere la chiave privata vedere "getPrivateKeyFromKS".
	 */
	public byte[] decryptBuffer(byte[] bufTxtCifrato) throws EncrypterException {

		if (dCipher == null)
			throw new EncrypterException("Oggetto di cifratura/decifratura non inizializzato");
		try {
			byte[] bufTxtChiaro = dCipher.doFinal(bufTxtCifrato, 0, bufTxtCifrato.length);
			return bufTxtChiaro;
		} catch (GeneralSecurityException e) {
			throw new EncrypterException(e.getMessage());
		}
	}

	public byte[] decryptBuffer(String strTxtCifrato) throws EncrypterException {

		return decryptBuffer(strTxtCifrato.getBytes());
	}

	/*
	 * ####################### Metodi su interi stream #######################
	 */

	public void encryptStream(InputStream in, OutputStream out) throws EncrypterException, IOException {

		// Bytes written to out will be encrypted
		out = getCipherOutputStream(out);

		// Buffer used to transport the bytes from one stream to another
		byte[] buf = new byte[1024];

		// Read in the cleartext bytes and write to out to encrypt
		int numRead = 0;
		while ((numRead = in.read(buf)) >= 0) {
			out.write(buf, 0, numRead);
		}
		out.close();
	}

	public void decryptStream(InputStream in, OutputStream out) throws EncrypterException, IOException {

		// Bytes read from in will be decrypted
		in = getCipherInputStream(in);

		// Buffer used to transport the bytes from one stream to another
		byte[] buf = new byte[1024];

		// Read in the decrypted bytes and write the cleartext to out
		int numRead = 0;
		while ((numRead = in.read(buf)) >= 0) {
			out.write(buf, 0, numRead);
		}
		out.close();
	}

}
