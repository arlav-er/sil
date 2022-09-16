/*
 * Creato il 9-nov-04
 * Author: vuoto
 * 
 */
package it.eng.sil.bean.menu;

/**
 * @author vuoto
 * 
 */
public class Util {

	public static String unmarkup(String desc) {
		if (desc != null) {
			desc = desc.replace('\"', '\'');
			desc = desc.replace('>', ' ');
			desc = desc.replace('<', ' ');
			desc = desc.replace('&', ' ');
		}
		return desc;
	}

}
