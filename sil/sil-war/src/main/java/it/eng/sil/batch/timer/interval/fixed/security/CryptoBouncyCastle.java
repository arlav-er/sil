package it.eng.sil.batch.timer.interval.fixed.security;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;

public class CryptoBouncyCastle {

	private String key = null;
	private byte[] initializationVector = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public class CryptoBouncyCastleException extends Exception {

		private static final long serialVersionUID = 4435623413765179521L;

		public CryptoBouncyCastleException(String message) {
			super(message);
		}

	}

	public CryptoBouncyCastle(String key) {
		this.key = key;
	}

	public CryptoBouncyCastle(String key, byte[] initializationVector) {
		this.key = key;
		this.initializationVector = initializationVector;
	}

	public String encrypt(String message) throws CryptoBouncyCastleException {
		return encrypt(message, key, initializationVector);
	}

	public String decrypt(String message) throws CryptoBouncyCastleException {
		return decrypt(message, key, initializationVector);
	}

	public String encrypt(String message, String key, byte[] iv) throws CryptoBouncyCastleException {

		// 1) encrypt

		byte[] encryptedBytes = null;
		byte[] encryptedStringEncodedBase64 = null;

		try {
			encryptedBytes = encrypt(message.getBytes(), key.getBytes(), iv);
		} catch (DataLengthException e) {
			throw new CryptoBouncyCastleException(e.getMessage());
		} catch (IllegalStateException e) {
			throw new CryptoBouncyCastleException(e.getMessage());
		} catch (InvalidCipherTextException e) {
			throw new CryptoBouncyCastleException(e.getMessage());
		}

		// 2) encrypted -> base 64

		encryptedStringEncodedBase64 = Base64.encode(encryptedBytes);

		return new String(encryptedStringEncodedBase64);

	}

	public String decrypt(String message, String key, byte[] iv) throws CryptoBouncyCastleException {

		byte[] decryptedBytes = null;
		byte[] decryptedStringEncodedBase64 = null;

		// 1) encrypted -> base 64

		decryptedStringEncodedBase64 = Base64.decode(message);

		// 2) decrypt

		try {
			decryptedBytes = decrypt(decryptedStringEncodedBase64, key.getBytes(), iv);
		} catch (DataLengthException e) {
			throw new CryptoBouncyCastleException(e.getMessage());
		} catch (IllegalStateException e) {
			throw new CryptoBouncyCastleException(e.getMessage());
		} catch (InvalidCipherTextException e) {
			throw new CryptoBouncyCastleException(e.getMessage());
		}

		return new String(decryptedBytes);

	}

	private byte[] decrypt(byte[] cipher, byte[] key, byte[] iv)
			throws DataLengthException, IllegalStateException, InvalidCipherTextException {

		BlockCipherPadding blockCipherPadding = new PKCS7Padding();
		PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new RijndaelEngine(128)),
				blockCipherPadding);
		CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(key), iv);
		aes.init(false, ivAndKey);
		return cipherData(aes, cipher);

	}

	private byte[] encrypt(byte[] plain, byte[] key, byte[] iv)
			throws DataLengthException, IllegalStateException, InvalidCipherTextException {

		BlockCipherPadding blockCipherPadding = new PKCS7Padding();
		PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new RijndaelEngine(128)),
				blockCipherPadding);
		CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(key), iv);
		aes.init(true, ivAndKey);
		return cipherData(aes, plain);

	}

	private byte[] cipherData(PaddedBufferedBlockCipher cipher, byte[] data) throws InvalidCipherTextException {

		int minSize = cipher.getOutputSize(data.length);
		byte[] outBuf = new byte[minSize];
		int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);
		int length2 = cipher.doFinal(outBuf, length1);
		int actualLength = length1 + length2;
		byte[] result = new byte[actualLength];
		System.arraycopy(outBuf, 0, result, 0, result.length);
		return result;

	}

}
