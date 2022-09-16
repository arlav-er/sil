package it.eng.afExt.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;

/**
 * @author Finessi_M
 * 
 *         To change the template for this generated type comment go to Window>Preferences>Java>Code Generation>Code and
 *         Comments
 */
public class NumberUtils {

	/**
	 * Verifica se la stringa passata è un numero valido
	 * 
	 * @param num
	 * @return true se è un numero valido, false altrimenti
	 */
	public static boolean isValidNumber(String num) {
		boolean toRet = false;
		try {
			Double.parseDouble(num);
			toRet = true;
		} catch (Exception e) {

		}
		return toRet;
	}

	/**
	 * Verifica che il numero passato abbia la lunghezza della parte intera <= intMaxSize e la lunghezza della parte
	 * decimale <= decimalMaxSize
	 * 
	 * @param num
	 *            il numero da controllare
	 * @param intMaxSize
	 *            lunghezza massima parte intera
	 * @param decimalMaxSize
	 *            lunghezza massima parte decimale
	 * @param decimalSep
	 *            carattere separatore della parte decimale da quella intera ('.'|',')
	 * @return
	 */
	public static boolean isValidDecimal(String num, int intMaxSize, int decimalMaxSize, char decimalSep) {
		boolean toRet = false;

		if (num == null) {
			return false;
		}

		String numStr = "" + num;
		int sepIdx = numStr.indexOf(decimalSep);
		int intLength = 0;
		int decimalLength = 0;

		if (sepIdx < 0) {
			intLength = numStr.length();
			if (!isValidNumber(num))
				return false;
		} else {
			intLength = sepIdx;
			decimalLength = numStr.length() - sepIdx - 1;
			// verifica se rappresenta un numero senza decimali
			/*
			 * if(decimalLength==1 && ((num.doubleValue() - num.longValue()) == 0) ){ decimalLength=0; }
			 */
		}

		if (intLength <= intMaxSize && decimalLength <= decimalMaxSize) {
			toRet = true;
		}

		return toRet;
	}

	/**
	 * Verifica che il numero passato abbia la lunghezza della parte intera <= intMaxSize e la lunghezza della parte
	 * decimale <= decimalMaxSize
	 * 
	 * @param num
	 *            il numero da controllare
	 * @param intMaxSize
	 *            lunghezza massima parte intera
	 * @param decimalMaxSize
	 *            lunghezza massima parte decimale
	 * @param decimalSep
	 *            carattere separatore della parte decimale da quella intera ('.'|',')
	 * @return
	 */
	/*
	 * public static boolean isValidDecimal(String num,int intMaxSize, int decimalMaxSize,char decimalSep){ boolean
	 * toRet=false; //System.out.println(isValidNumber(num)); //if (isValidNumber(num)){ toRet= isValidDecimal(new
	 * Double(num),intMaxSize,decimalMaxSize,decimalSep); //}
	 * 
	 * return toRet; }
	 */

	/**
	 * Formatta un double in una stringa di testo. Il double si viene formattato con: '.' separatore delle migliaia ','
	 * separatore decimale. viene eseguito eventualmente l'arrotondamento a 2 cifre decimali.0
	 */
	public static String FormatDouble(double d) {
		String s = "0";
		try {
			DecimalFormatSymbols sym = new DecimalFormatSymbols();
			sym.setDecimalSeparator(',');
			sym.setGroupingSeparator('.');

			DecimalFormat fmt = new DecimalFormat("#,###.##", sym);

			s = fmt.format(d, new StringBuffer(), new FieldPosition(0)).toString();
		} catch (Exception E) {
			// viene ritornato s con il valore di inizializzazione
		}
		;
		return s;
	}

	/*
	 * public static void main(String args[]){ FormatDouble(20500.1314); }
	 */
}// end of class
