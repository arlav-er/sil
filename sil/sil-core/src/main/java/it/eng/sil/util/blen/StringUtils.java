package it.eng.sil.util.blen;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.engiweb.framework.base.SourceBean;

/**
 * Utilità per la gestione delle stringhe.
 * 
 * @author Alessio Rolfini / Luigi Antenucci
 */

public abstract class StringUtils {

	public static final String STRING_EMPTY = ""; // stringa vuota

	public static final String HTML_CODE_AMP = "&amp;"; // &
	public static final String HTML_CODE_QUOT = "&quot;"; // "
	public static final String HTML_CODE_LT = "&lt;"; // <
	public static final String HTML_CODE_GT = "&gt;"; // >
	public static final String HTML_CODE_APOS = "&apos;"; // '

	/**
	 * Efficient string replace function. Replaces instances of the substring find with replace in the string subject.
	 * karl@xk72.com
	 * 
	 * @param subject
	 *            The string to search for and replace in.
	 * @param find
	 *            The substring to search for.
	 * @param replace
	 *            The string to replace instances of the string find with.
	 */
	public static String replace(String subject, String find, String replace) {
		if (subject == null)
			return null;

		StringBuffer buf = new StringBuffer();
		int l = find.length();
		int s = 0;
		int i = subject.indexOf(find);
		while (i != -1) {
			buf.append(subject.substring(s, i));
			buf.append(replace);
			s = i + l;
			i = subject.indexOf(find, s);
		}
		buf.append(subject.substring(s));
		return buf.toString();
	}

	/**
	 * isDigit Controlla se il carattere è un numero compreso tra 0 e 9 (se è un digit)
	 * 
	 * @param c
	 * @return true: se il carattere è un numero compreso tra 0 e 9 false: se il carattere non è un numero compreso tra
	 *         0 e 9
	 * @author Finessi_M
	 */
	public static boolean isDigit(char c) {
		return ((c >= '0') && (c <= '9'));
	}

	/**
	 * Metodo che estrae dal sourceBean l'attributo indicato, che deve essere di tipo stringa, ritornandolo.
	 * 
	 * Nel caso il risultato sia null, ritorna una stringa vuota.
	 * 
	 * Ad esempio un costrutto del genere, molto comune nelle pagine JSP:
	 * 
	 * String tipo= beanConoscenza.getAttribute("DESC_TIPO"); if ( tipo == null ) tipo= "";
	 * 
	 * Diventerà:
	 * 
	 * String tipo= LogUtils.getAttributeStrNotNull(beanConoscenza, "DESC_TIPO");
	 * 
	 * @param beanSrc
	 *            SourceBean da cui estrarre l'attributo
	 * @param attributeName
	 *            Stringa col nome dell'attributo da estrarre
	 * @return La stringa corrispondente al valore dell'attributo
	 * @throws IllegalArgumentException
	 *             se gli argomenti sono errati
	 * @throws ClassCastException
	 *             se il dato contenuto nell'attributo non è di tipo stringa
	 * 
	 * @author Corrado Vaccari
	 */
	public static String getAttributeStrNotNull(SourceBean beanSrc, String attributeName) {

		if (beanSrc == null)
			throw new IllegalArgumentException("The source bean is missing ");

		if ((attributeName == null) || (attributeName.length() == 0))
			throw new IllegalArgumentException("The attribute name is missing ");

		String result = (String) beanSrc.getAttribute(attributeName);
		return (result != null ? result : "");
	}

	/**
	 * removeWhiteSpace Rimuove gli spazi bianchi all'inizio di una stringa ed elimina gli spazi bianchi ripetuti
	 * all'interno della una stringa es: ' DE MICHELE ' diventa 'DE MICHELE '
	 * 
	 * @param str
	 * @return stringa priva di spazi bianchi ripetuti
	 * @author Giuliani Davide
	 */
	public static String removeDoubleWhiteSpace(String str) {
		// str = str.trim();
		if (str != null) {
			if (!str.equals("")) {
				StringBuffer sb = new StringBuffer(str);
				char c;
				boolean firstWhiteSpace = true;
				for (int i = 0; i < sb.length(); i++) {
					c = sb.charAt(i);
					if (c == ' ') {
						if (firstWhiteSpace)
							firstWhiteSpace = false;
						else {
							sb.deleteCharAt(i);
							i--;
						}
					} else
						firstWhiteSpace = true;
				}
				if (sb.charAt(0) == ' ') {
					sb.deleteCharAt(0);
				}

				return sb.toString();
			}
			return "";
		}
		return null;
	}

	/**
	 * Se la stringa è nulla, rende una stringa vuota Altrimenti rende la stringa inalterata.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String notNull(String str) {
		return (str == null) ? STRING_EMPTY : str;
	}

	/**
	 * Rende TRUE se la stringa passata è nulla o vuota.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0);
	}

	/**
	 * Rende TRUE se la stringa passata è composta da almeno un carattere (è non-nulla e non-vuota). E' il negato del
	 * valore reso dalla "isEmpty()"
	 * 
	 * @author Luigi Antenucci
	 */
	public static final boolean isFilled(String str) {
		return (str != null) && (str.length() > 0);
	}

	/**
	 * Rende TRUE se la stringa passata è nulla o vuota o contiene solo spazi.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final boolean isEmptyNoBlank(String str) {
		return (str == null) || (str.length() == 0) || (str.trim().length() == 0);
	}

	/**
	 * Rende TRUE se la stringa passata è composta da almeno un carattere (è non-nulla e non-vuota) escludendo gli spazi
	 * vuoti (ossia se ne fa il TRIM). E' il negato del valore reso dalla "isEmptyNoBlank()"
	 * 
	 * @author Luigi Antenucci
	 */
	public static final boolean isFilledNoBlank(String str) {
		return (str != null) && (str.length() > 0) && (str.trim().length() > 0);
	}

	/**
	 * Rende la "toString()" dell'oggetto passato. Nel caso obj sia null o che il suo "toString" sia null, viene reso il
	 * valore passato come default in defValue (che per default vale null).
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String getStringValue(Object obj) {
		return getStringValue(obj, null);
	}

	public static final String getStringValueNotNull(Object obj) {
		return getStringValue(obj, STRING_EMPTY);
	}

	public static final String getStringValue(Object obj, String defValue) {
		if (obj == null)
			return defValue;
		else if (obj instanceof String)
			return (String) obj;
		else {
			String str = obj.toString();
			if (str == null)
				str = defValue;
			return str;
		}
	}

	/**
	 * Rende il nome di classe corto (senza la qualificazione del pacchetto) per l'oggetto passato.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String getClassName(Object ojbect) {
		String name = null;
		if (ojbect != null) {
			String fullName;
			if (ojbect instanceof Class)
				fullName = ((Class) ojbect).getName();
			else
				fullName = ojbect.getClass().getName();

			int posPunto = fullName.lastIndexOf(".") + 1;
			if ((posPunto > 0) && (posPunto < fullName.length())) {
				name = fullName.substring(posPunto);
			}
		}
		return name;
	}

	/**
	 * Formatta un numero in una stringa allineandolo a destra su quantiChar caratteri. Se la conversione in stringa
	 * eccede quantiChar caratteri, verrà resa inalterata.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String allineaDestra(int numero, int quantiChar) {
		return allineaDestra(numero, quantiChar, ' ');
	}

	public static final String allineaDestra(int numero, int quantiChar, char spazio) {

		NumberFormat numberFormatter = NumberFormat.getNumberInstance();
		String numStr = numberFormatter.format(numero);

		int numStrLen = numStr.length();
		int quantiSpazi = quantiChar - numStrLen;
		if (quantiSpazi > 0) { // cioè: quantiChar > numStrLen
			char[] charArr = new char[quantiSpazi];
			Arrays.fill(charArr, spazio);
			String spazi = String.copyValueOf(charArr);
			return spazi + numStr;
		} else { // quantiSpazi <= 0 cioè: quantiChar < numStrLen
			return numStr;
		}
	}

	/**
	 * Formatta la stringa VALUE affinché possa essere inserita in un campo di INPUT-TEXT di HTML. Sostituisce gli apici
	 * doppi, le e-commerciali e i maggiore/minore con i corrispondenti valori di "&..;"
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String formatValue4Html(String value) {

		if (value == null)
			return STRING_EMPTY;

		StringBuffer value4html = new StringBuffer(value);
		int pos = 0;
		while (pos < value4html.length()) {
			char c = value4html.charAt(pos);
			// System.out.println("ESAMINO ["+c+"]");
			String replace = null;
			switch (c) {
			case '&':
				replace = HTML_CODE_AMP; // usa "&amp;"
				break;
			case '"':
				replace = HTML_CODE_QUOT; // usa "&quot;"
				break;
			case '<':
				replace = HTML_CODE_LT; // usa "&lt;"
				break;
			case '>':
				replace = HTML_CODE_GT; // usa "&gt;"
				break;
			case '\'':
				replace = HTML_CODE_APOS; // usa "&apos;"
				break;
			}
			if (replace == null) {
				pos++;
			} else {
				// System.out.println("SOSTITUISCO CON ["+replace+"]");
				value4html.deleteCharAt(pos); // rimuovo il carattere
				value4html.insert(pos, replace); // rimpiazzo con la stringa
				pos += replace.length(); // mi sposto di LUNG caratteri
											// avanti
			} // (quelli aggiunti in più)
		}
		return value4html.toString();
	}

	/**
	 * Formatta la stringa VALUE affinché possa essere inserita all'interno di una stringa contenente una query SQL.
	 * Raddoppia gli apici singoli.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String formatValue4Sql(String value) {

		return replace(value, "'", "''"); // apice --> raddoppio
	}

	/**
	 * Formatta la stringa VALUE affinché possa essere inserita all'interno di una stringa di JavaScript. Antepone il
	 * carattere backslash (che è il car. di escaping) ai caratteri di doppio apice, di backslash e di invio.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String formatValue4Javascript(String value) {
		value = replace(value, "\\", "\\\\"); // backslash --> doppio
												// backslash
		value = replace(value, "\"", "\\\""); // doppio-apice --> backslash +
												// doppio-apice
		value = replace(value, "\n", "\\n"); // invio --> backslash + invio
		value = replace(value, "\'", "\\'"); // apice --> backslash + apice
		return value;
	}

	public static Vector split(String value, String delimitatore) {
		Vector ris = new Vector();
		String strAppoggio = value;
		int index = 0;
		String strSottoStringa = "";
		if ((value != null) && (!value.equals(""))) {
			while (index >= 0) {
				index = strAppoggio.indexOf(delimitatore);
				if (index < 0) {
					strSottoStringa = strAppoggio;
				} else {
					strSottoStringa = strAppoggio.substring(0, index);
					strAppoggio = strAppoggio.substring(index + 1, strAppoggio.length());
				}
				ris.add(strSottoStringa);
			}
		}
		return ris;
	}

	public static boolean isMailCorretta(String email) {

		Pattern p = Pattern.compile(
				"([A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*@[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*)?");
		Matcher m = p.matcher(email);
		boolean matchFound = m.matches();
		if (!matchFound) {
			return false;
		}

		return true;
	}

}
