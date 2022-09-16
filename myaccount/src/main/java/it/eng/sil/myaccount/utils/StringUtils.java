package it.eng.sil.myaccount.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility per la gestione delle stringhe.
 *
 */
public abstract class StringUtils {

    public static final String STRING_EMPTY = ""; // stringa vuota

    public static final String HTML_CODE_AMP = "&amp;"; // &
    public static final String HTML_CODE_QUOT = "&quot;"; // "
    public static final String HTML_CODE_LT = "&lt;"; // <
    public static final String HTML_CODE_GT = "&gt;"; // >
    public static final String HTML_CODE_APOS = "&apos;"; // '

	//public static final SimpleDateFormat dataSemplice = new SimpleDateFormat("dd/MM/yyyy");
    /**
     * Efficient string replace function. Replaces instances of the substring
     * find with replace in the string subject. karl@xk72.com
     *
     * @param subject The string to search for and replace in.
     * @param find The substring to search for.
     * @param replace The string to replace instances of the string find with.
     */
    public static String replace(String subject, String find, String replace) {
        if (subject == null) {
            return null;
        }

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
     * isDigit Controlla se il carattere � un numero compreso tra 0 e 9 (se � un
     * digit)
     *
     * @param c
     * @return true: se il carattere � un numero compreso tra 0 e 9 false: se il
     * carattere non � un numero compreso tra 0 e 9
     * @author Finessi_M
     */
    public static boolean isDigit(char c) {
        return ((c >= '0') && (c <= '9'));
    }

    /**
     * removeWhiteSpace Rimuove gli spazi bianchi all'inizio di una stringa ed
     * elimina gli spazi bianchi ripetuti all'interno della una stringa es: ' DE
     * MICHELE ' diventa 'DE MICHELE '
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
                        if (firstWhiteSpace) {
                            firstWhiteSpace = false;
                        } else {
                            sb.deleteCharAt(i);
                            i--;
                        }
                    } else {
                        firstWhiteSpace = true;
                    }
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
     * Se la stringa � nulla, rende una stringa vuota Altrimenti rende la
     * stringa inalterata.
     *
     * @author Luigi Antenucci
     */
    public static final String notNull(String str) {
        return (str == null) ? STRING_EMPTY : str;
    }

    /**
     * Rende TRUE se la stringa passata � nulla o vuota.
     *
     * @author Luigi Antenucci
     */
    public static final boolean isEmpty(String str) {
        return (str == null) || (str.length() == 0);
    }

    /**
     * Rende TRUE se la stringa passata � composta da almeno un carattere (�
     * non-nulla e non-vuota). E' il negato del valore reso dalla "isEmpty()"
     *
     * @author Luigi Antenucci
     */
    public static final boolean isFilled(String str) {
        return (str != null) && (str.length() > 0);
    }

    /**
     * Rende TRUE se la stringa passata � nulla o vuota o contiene solo spazi.
     *
     * @author Luigi Antenucci
     */
    public static final boolean isEmptyNoBlank(String str) {
        return (str == null) || (str.length() == 0) || (str.trim().length() == 0);
    }

    /**
     * Rende TRUE se la stringa passata � composta da almeno un carattere (�
     * non-nulla e non-vuota) escludendo gli spazi vuoti (ossia se ne fa il
     * TRIM). E' il negato del valore reso dalla "isEmptyNoBlank()"
     *
     * @author Luigi Antenucci
     */
    public static final boolean isFilledNoBlank(String str) {
        return (str != null) && (str.length() > 0) && (str.trim().length() > 0);
    }

    /**
     * Rende la "toString()" dell'oggetto passato. Nel caso obj sia null o che
     * il suo "toString" sia null, viene reso il valore passato come default in
     * defValue (che per default vale null).
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
        if (obj == null) {
            return defValue;
        } else if (obj instanceof String) {
            return (String) obj;
        } else {
            String str = obj.toString();
            if (str == null) {
                str = defValue;
            }
            return str;
        }
    }

    /**
     * Rende il nome di classe corto (senza la qualificazione del pacchetto) per
     * l'oggetto passato.
     *
     * @author Luigi Antenucci
     */
    public static final String getClassName(Object ojbect) {
        String name = null;
        if (ojbect != null) {
            String fullName;
            if (ojbect instanceof Class) {
                fullName = ((Class) ojbect).getName();
            } else {
                fullName = ojbect.getClass().getName();
            }

            int posPunto = fullName.lastIndexOf(".") + 1;
            if ((posPunto > 0) && (posPunto < fullName.length())) {
                name = fullName.substring(posPunto);
            }
        }
        return name;
    }

    /**
     * Formatta un numero in una stringa allineandolo a destra su quantiChar
     * caratteri. Se la conversione in stringa eccede quantiChar caratteri,
     * verr� resa inalterata.
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
        if (quantiSpazi > 0) { // cio�: quantiChar > numStrLen
            char[] charArr = new char[quantiSpazi];
            Arrays.fill(charArr, spazio);
            String spazi = String.copyValueOf(charArr);
            return spazi + numStr;
        } else { // quantiSpazi <= 0 cio�: quantiChar < numStrLen
            return numStr;
        }
    }

    /**
     * Formatta la stringa VALUE affinch� possa essere inserita in un campo di
     * INPUT-TEXT di HTML. Sostituisce gli apici doppi, le e-commerciali e i
     * maggiore/minore con i corrispondenti valori di "&..;"
     *
     * @author Luigi Antenucci
     */
    public static final String formatValue4Html(String value) {

        if (value == null) {
            return STRING_EMPTY;
        }

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
            } // (quelli aggiunti in pi�)
        }
        return value4html.toString();
    }

    /**
     * Formatta la stringa VALUE affinch� possa essere inserita all'interno di
     * una stringa contenente una query SQL. Raddoppia gli apici singoli.
     *
     * @author Luigi Antenucci
     */
    public static final String formatValue4Sql(String value) {

        return replace(value, "'", "''"); // apice --> raddoppio
    }

    /**
     * Formatta la stringa VALUE affinch� possa essere inserita all'interno di
     * una stringa di JavaScript. Antepone il carattere backslash (che � il car.
     * di escaping) ai caratteri di doppio apice, di backslash e di invio.
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

//	public static Vector split(String value, String delimitatore) {
//		Vector ris = new Vector();
//		String strAppoggio = value;
//		int index = 0;
//		String strSottoStringa = "";
//		if ((value != null) && (!value.equals(""))) {
//			while (index >= 0) {
//				index = strAppoggio.indexOf(delimitatore);
//				if (index < 0) {
//					strSottoStringa = strAppoggio;
//				} else {
//					strSottoStringa = strAppoggio.substring(0, index);
//					strAppoggio = strAppoggio.substring(index + 1, strAppoggio.length());
//				}
//				ris.add(strSottoStringa);
//			}
//		}
//		return ris;
//	}
    public static boolean isMailCorretta(String email) {

        Pattern p = Pattern
                .compile("([A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*@[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*)?");
        Matcher m = p.matcher(email);
        boolean matchFound = m.matches();
        if (!matchFound) {
            return false;
        }

        return true;
    }

    public static final String formatDate(Date valueData, String formatoData) {
        SimpleDateFormat fd = new SimpleDateFormat(formatoData);
        return fd.format(valueData);
    }

    /**
     * DB fields should rely on enums, so this method should not be used
     *
     * @param in
     * @return
     */
    @Deprecated
    public static Boolean opz2Boolean(String in) {
        try {
            if ("S".compareToIgnoreCase(in) == 0) {
                return true;
            } else if ("SI".compareToIgnoreCase(in) == 0) {
                return true;
            } else if ("1".compareToIgnoreCase(in) == 0) {
                return true;
            } else if ("Sì".compareToIgnoreCase(in) == 0) {
                return true;
            }

            return false;
        } catch (NullPointerException e) {
            // torno null e lascio che altri metodi si smazzino la cosa
        }
        return null;

    }

    /**
     * toUppercase and Adds the special character % at the beginning and end of
     * string (utility for filter composition)
     *
     * @param value
     * @return
     */
    public static final String upperAndPercent(String value) {
        return value == null ? null : new StringBuilder().append("%").append(value.toUpperCase()).append("%")
                .toString();
    }

    /**
     * toUppercase and Adds the special character % at the beginning and end of
     * string (utility for filter composition)
     *
     * @param value
     * @return
     */
    public static final String addPercent(String value) {
        return value == null ? null : new StringBuilder().append("%").append(value).append("%").toString();
    }

    public static final String findBetween(String value, String first, String second) {
        String result = null;
        if (value != null) {
            Pattern myPattern = Pattern.compile("\\" + first + "(.*?)\\" + second, Pattern.CASE_INSENSITIVE);
            Matcher m = myPattern.matcher(value);
            while (m.find()) {
                result = m.group(1);
            }
        }
        return result;
    }

    public static final String findeBetweenQuotes(String tagName, String value) {
        String result = null;
        if (value != null) {
            Pattern myPattern = Pattern.compile(tagName + "=\"(.+?)\"", Pattern.CASE_INSENSITIVE);
            Matcher m = myPattern.matcher(value);
            while (m.find()) {
                result = m.group(1);
            }
        }

        return result;
    }

    public static final String getExtensionFromMimeType(String mimeType) {
        return mimeType.substring(mimeType.lastIndexOf("/") + 1);
    }
}
