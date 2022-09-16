package it.eng.myportal.utils;

import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Hex;

public class OracleObfuscation extends Obfuscation {

	private static final String ALGORITHM1_DES = "DES"; // Setto l'algoritmo di cifratura

	private static final String ALGORITHM2_DES = "DES/CBC/NoPadding";

	private static IvParameterSpec iv = new IvParameterSpec(
			new byte[] { 0x07, 0x04, 0x01, 0x00, 0x02, 0x03, 0x04, 0x05 });

	public OracleObfuscation(String secretString) {
		super(secretString, ALGORITHM1_DES, ALGORITHM2_DES, iv);
	}

	public static void main(String[] args) throws Exception {
		OracleObfuscation desObf = new OracleObfuscation("TODOTODO");
		byte[] encrypted = desObf
				.encrypt("aziendasaretest|cee84916f65114c7a9c0f6194a2112c840e5a653|20/10/2017     ".getBytes());
		// Encrypt
		String userCript = new String(Hex.encodeHex(encrypted));
		System.out.println(userCript);

		// Decrypt
		byte[] decoded = Hex.decodeHex(userCript.toCharArray());
		String userDecript = new String(desObf.decrypt(decoded));

		System.out.println(userDecript);
	}
}