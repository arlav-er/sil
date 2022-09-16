package it.eng.afExt.utils;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

/**
 * Metodi di utilità su SourceBean e loro attributi.
 * 
 * @author Luigi Antenucci
 */
public abstract class SourceBeanUtils {

	private static final String STRING_EMPTY = "";
	private static final String STRING_TRUE = "true";
	private static final String STRING_TRUE2 = "S";

	/**
	 * Rende TRUE se l'attributo attr del SourceBean è nullo o è una stringa che contiene "true" o "S"; rende FALSE
	 * altrimenti. Nota: se il SourceBean è nullo rende FALSE.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final boolean isTrueOrNull(SourceBean sb, String attr) {
		if (sb == null)
			return false;
		else {
			String attrVal = (String) sb.getAttribute(attr);
			return (attrVal == null) || attrVal.equalsIgnoreCase(STRING_TRUE) || attrVal.equalsIgnoreCase(STRING_TRUE2);
		}
	}

	/**
	 * Rende TRUE se l'attributo attr del SourceBean è una stringa NON NULLA che contiene "true" o "S"; rende FALSE
	 * altrimenti. Nota: se il SourceBean è nullo rende FALSE.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final boolean isTrue(SourceBean sb, String attr) {
		if (sb == null)
			return false;
		else {
			String attrVal = (String) sb.getAttribute(attr);
			return (attrVal != null)
					&& (attrVal.equalsIgnoreCase(STRING_TRUE) || attrVal.equalsIgnoreCase(STRING_TRUE2));
		}
	}

	/**
	 * Rende TRUE se l'attributo attr del SourceBean è nullo. Nota: se il SourceBean è nullo rende TRUE.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final boolean isNull(SourceBean sb, String attr) {
		if (sb == null)
			return true;
		else {
			Object attrVal = sb.getAttribute(attr);
			return (attrVal == null);
		}
	}

	/**
	 * Rende il valore dell'attributo attr del SourceBean sb. Nota: se il SourceBean è nullo rende NULL. Utile per
	 * evitare "null pointer exception".
	 * 
	 * @author Luigi Antenucci
	 */
	public static final Object getAttr(SourceBean sb, String attr) {
		if (sb != null)
			return sb.getAttribute(attr);
		else
			return null;
	}

	/**
	 * Rende il valore dell'attributo attr del SourceBean sb dopo un casting a String. Nota1: se il SourceBean è nullo
	 * rende il defValue passato (si evita il "null pointer exception"). Nota2: se l'attributo non esiste o è nullo,
	 * rende il defValue. Nota3: se l'attributo è un oggetto NON String, ne viene reso il "toString()".
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String getAttrStr(SourceBean sb, String attr, String defValue) {
		if (sb == null)
			return defValue;
		else {
			Object attrValue = sb.getAttribute(attr);
			if (attrValue == null)
				return defValue;
			else if (attrValue instanceof String)
				return (String) attrValue;
			else {
				String strVal = attrValue.toString();
				if (strVal == null)
					strVal = defValue;
				return strVal;
			}
		}
	}

	/**
	 * Rende il valore dell'attributo attr del SourceBean sb dopo un casting a String. Nota: Se il SourceBean è nullo
	 * oppure se l'attributo non esiste o il suo valore è nullo, rende null. Nota: se l'attributo è un oggetto NON
	 * String, ne viene reso il "toString()".
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String getAttrStr(SourceBean sb, String attr) {
		return getAttrStr(sb, attr, null);
	}

	/**
	 * Come la getAttrStr() ma rende sempre stringa vuota anziché nulla.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String getAttrStrNotNull(SourceBean sb, String attr) {
		return getAttrStr(sb, attr, STRING_EMPTY);
	}

	/**
	 * Questa funzione estrai l'attributo come stringa e sostituisce eventuali apici singoli con doppi apici
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String getAttrStrAvoidInjection(SourceBean sb, String attr) {
		return getAttrStr(sb, attr, STRING_EMPTY).replace("'", "''");
	}

	/**
	 * Rende il valore dell'attributo attr del SourceBean sb dopo una conversione da String a boolean (se la stringa
	 * vale "TRUE" o "S" viene reso il booleano true). Nota: se il SourceBean è nullo o l'attributo è NULL viene reso il
	 * valore di default defValue.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final boolean getAttrBoolean(SourceBean sb, String attr, boolean defValue) {
		try {
			return getAttrBoolean(sb, attr);
		} catch (EMFInternalError e) {
			return defValue;
		}
	}

	/**
	 * Come sopra, ma se il SourceBean è nullo o non esiste l'attributo, lancia un'eccezione.
	 */
	public static final boolean getAttrBoolean(SourceBean sb, String attr) throws EMFInternalError {
		Object attrValue = getAttr(sb, attr);
		if (attrValue != null) {
			try {
				if (attrValue instanceof Boolean) {
					return ((Boolean) attrValue).booleanValue();
				}
				String strValue = attrValue.toString();
				return (strValue.equalsIgnoreCase(STRING_TRUE) || strValue.equalsIgnoreCase(STRING_TRUE2));
			} catch (Exception ignore) {
			}
		}
		throw new EMFInternalError(EMFErrorSeverity.ERROR, "getAttrBoolean() di '" + attr + "' non trovato!");
	}

	/**
	 * Rende il valore dell'attributo attr del SourceBean sb dopo una conversione da String a int. Nota: se il
	 * SourceBean è nullo o l'attributo è NULL oppure non contiene un valore numerico convertibile in INTEGER, viene
	 * reso il valore di default defValue.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final int getAttrInt(SourceBean sb, String attr, int defValue) {
		try {
			return getAttrInt(sb, attr);
		} catch (EMFInternalError e) {
			return defValue;
		}
	}

	/**
	 * Come sopra, ma se il SourceBean è nullo o non esiste l'attributo, lancia un'eccezione.
	 */
	public static final int getAttrInt(SourceBean sb, String attr) throws EMFInternalError {
		Object attrValue = getAttr(sb, attr);
		if (attrValue != null) {
			try {
				if (attrValue instanceof Number) {
					return ((Number) attrValue).intValue();
				}
				return Integer.parseInt(attrValue.toString());
			} catch (NumberFormatException ignore) {
			}
		}
		throw new EMFInternalError(EMFErrorSeverity.ERROR, "getAttrInt() di '" + attr + "' non trovato!");
	}

	/**
	 * Rende il valore dell'attributo attr del SourceBean sb dopo una conversione da String a long. Nota: se il
	 * SourceBean è nullo o l'attributo è NULL oppure non contiene un valore numerico convertibile in LONG, viene reso
	 * il valore di default defValue.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final long getAttrLong(SourceBean sb, String attr, long defValue) {
		try {
			return getAttrLong(sb, attr);
		} catch (EMFInternalError e) {
			return defValue;
		}
	}

	/**
	 * Come sopra, ma se il SourceBean è nullo o non esiste l'attributo, lancia un'eccezione.
	 */
	public static final long getAttrLong(SourceBean sb, String attr) throws EMFInternalError {
		Object attrValue = getAttr(sb, attr);
		if (attrValue != null) {
			try {
				if (attrValue instanceof Number) {
					return ((Number) attrValue).longValue();
				}
				return Long.parseLong(attrValue.toString());
			} catch (NumberFormatException ignore) {
			}
		}
		throw new EMFInternalError(EMFErrorSeverity.ERROR, "getAttrLong() di '" + attr + "' non trovato!");
	}

	/**
	 * Rende il valore dell'attributo attr del SourceBean sb dopo una conversione da String a Double. Nota: se il
	 * SourceBean è nullo o l'attributo è NULL oppure non contiene un valore numerico convertibile in DOUBLE, viene reso
	 * il valore di default defValue.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final double getAttrDouble(SourceBean sb, String attr, double defValue) {
		try {
			return getAttrDouble(sb, attr);
		} catch (EMFInternalError e) {
			return defValue;
		}
	}

	/**
	 * Come sopra, ma se il SourceBean è nullo o non esiste l'attributo, lancia un'eccezione.
	 */
	public static final double getAttrDouble(SourceBean sb, String attr) throws EMFInternalError {
		Object attrValue = getAttr(sb, attr);
		if (attrValue != null) {
			try {
				if (attrValue instanceof Number) {
					return ((Number) attrValue).doubleValue();
				}
				return Double.parseDouble(attrValue.toString());
			} catch (NumberFormatException ignore) {
			}
		}
		throw new EMFInternalError(EMFErrorSeverity.ERROR, "getAttrDouble() di '" + attr + "' non trovato!");
	}

	/**
	 * Rende il valore dell'attributo attr del SourceBean sb dopo una conversione da String a BigDecimal. Nota: se il
	 * SourceBean è nullo o l'attributo è NULL oppure non contiene un valore numerico convertibile in BigDecimal, viene
	 * reso il valore di default defValue.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final BigDecimal getAttrBigDecimal(SourceBean sb, String attr, BigDecimal defValue) {
		try {
			return getAttrBigDecimal(sb, attr);
		} catch (EMFInternalError e) {
			return defValue;
		}
	}

	/**
	 * Come sopra, ma se il SourceBean è nullo o non esiste l'attributo, lancia un'eccezione.
	 */
	public static final BigDecimal getAttrBigDecimal(SourceBean sb, String attr) throws EMFInternalError {
		Object attrValue = getAttr(sb, attr);
		if (attrValue != null) {
			try {
				if (attrValue instanceof BigDecimal) {
					return (BigDecimal) attrValue;
				}
				if (attrValue instanceof Number) {
					return new BigDecimal(((Number) attrValue).doubleValue());
				}
				return new BigDecimal(attrValue.toString());
			} catch (NumberFormatException ignore) {
			}
		}
		throw new EMFInternalError(EMFErrorSeverity.ERROR, "getAttrBigDecimal() di '" + attr + "' non trovato!");
	}

	/**
	 * Imposta il valore di un attributo ma SOLO SE non viene passato un NULL o una stringa vuota. Se valueObj è null
	 * oppure è di classe String ma è vuoto, non si esegue il "set" dell'attributo.
	 */
	public static final void setAttrIfFilled(SourceBean sb, String attr, Object valueObj) throws SourceBeanException {

		if (isNotFilled(valueObj))
			return;
		sb.setAttribute(attr, valueObj);
	}

	/**
	 * Simile alla "setAttrIfFilled" ma il valueObj è letto da un attributo di un altro SourceBean di ORIgine.
	 */
	public static final void setAttrIfFilled(SourceBean sb, String attr, SourceBean sbOri, String attrOri)
			throws SourceBeanException {

		Object valueObj = sbOri.getAttribute(attrOri);
		setAttrIfFilled(sb, attr, valueObj);
	}

	private static boolean isNotFilled(Object valueObj) {
		return (valueObj == null) || ((valueObj instanceof String) && (((String) valueObj).length() == 0));
	}

}
