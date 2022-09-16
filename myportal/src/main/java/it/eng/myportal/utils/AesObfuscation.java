package it.eng.myportal.utils;

import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Hex;

public class AesObfuscation extends Obfuscation {

	private static final String ALGORITHM1_AES = "AES";
	private static final String ALGORITHM2_AES = "AES/CBC/NoPadding";

	// IV Parameter must be 16 bytes long
	private static IvParameterSpec iv = new IvParameterSpec(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
	public AesObfuscation(String secretString) {
		super(secretString, ALGORITHM1_AES, ALGORITHM2_AES, iv);
	}
}

