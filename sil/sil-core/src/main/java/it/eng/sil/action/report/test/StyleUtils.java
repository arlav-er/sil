/*
 * StyleUtils.java
 *
 * Created on 21 gennaio 2004, 7.47
 */

package it.eng.sil.action.report.test;

/**
 * 
 * @author Administrator
 */
public class StyleUtils {

	/** Creates a new instance of StyleUtils */
	public StyleUtils() {
	}

	public static int toInch(int x) {
		return (int) (Style.INCH_TO_MM * (float) x);
	}

	public static int toMM(int x) {
		return (int) (x / Style.INCH_TO_MM);
	}
}
