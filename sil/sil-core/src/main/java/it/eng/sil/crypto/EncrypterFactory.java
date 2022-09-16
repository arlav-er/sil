package it.eng.sil.crypto;

/**
 * Factory per gli algoritmi di cifratura
 * 
 * @author Luigi Antenucci
 */
public abstract class EncrypterFactory {

	public static final Encrypter create() throws EncrypterException {
		return new DesEncrypter();
	}
}