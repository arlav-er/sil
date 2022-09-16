/*
 * StyleUtils.java
 *
 * Created on 21 gennaio 2004, 7.47
 */

package it.eng.sil.action.report.utils.inet;

/**
 * Metodi di utilità per gestire lo stile
 * 
 * @author Andrea Savino
 */
public class StyleUtils {
	/**
	 * Converte da mm a twip.
	 * 
	 * @param x
	 *            il valore in <strong>mm</strong> da convertire in <strong>twip</strong>
	 * @return il valore convertito
	 */
	public static int toTwips(int x) {
		return (int) (Style.TWIPS_TO_MM * (float) x);
	}

	/**
	 * Converte il valore da twip a mm.
	 * 
	 * @param x
	 *            il valore in <strong>twip</strong> da convertire in <strong>mm</strong>
	 * @return il valore convertito
	 */
	public static int toMM(int x) {
		return (int) (x / Style.TWIPS_TO_MM);
	}
}
