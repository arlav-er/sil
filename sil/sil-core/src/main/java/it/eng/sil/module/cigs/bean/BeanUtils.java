/**
 * 
 */
package it.eng.sil.module.cigs.bean;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author User1
 * 
 */
public class BeanUtils {
	private static final String XSD_DATE_FORMAT = "yyyy-MM-dd";
	private static final String SQL_DATE_FORMAT = "dd/MM/yyyy";
	private static final String ROW = "ROW";
	// private static final String ROWS = "ROWS";

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(BeanUtils.class);

	public SourceBean sb;

	private DateFormat date2xsd, sql2date;

	public BeanUtils(SourceBean sb) {
		super();
		this.sb = sb;
		date2xsd = new SimpleDateFormat(XSD_DATE_FORMAT);
		sql2date = new SimpleDateFormat(SQL_DATE_FORMAT);
		sql2date.setLenient(false);
	}

	public String getStringNotNull(String name) {
		if (logger.isDebugEnabled()) {
			logger.debug("getStringNotNull(String) - start");
		}

		String returnString = StringUtils.getAttributeStrNotNull(sb, name);
		if (logger.isDebugEnabled()) {
			logger.debug("getStringNotNull(String) - end");
		}
		return returnString;
	}

	public String getStringNotNull4Html(String name) {
		return StringUtils.formatValue4Html(getStringNotNull(name));
	}

	public BigDecimal getBigDecimal0(String name) {
		if (logger.isDebugEnabled()) {
			logger.debug("getBigDecimal0(String) - start");
		}

		BigDecimal returnBigDecimal = getBigDecimal(sb, name, BigDecimal.ZERO);
		if (logger.isDebugEnabled()) {
			logger.debug("getBigDecimal0(String) - end");
		}
		return returnBigDecimal;
	}

	public BigDecimal getBigDecimal(String name, BigDecimal failSafe) {
		if (logger.isDebugEnabled()) {
			logger.debug("getBigDecimal(String, BigDecimal) - start");
		}

		BigDecimal returnBigDecimal = getBigDecimal(sb, name, failSafe);
		if (logger.isDebugEnabled()) {
			logger.debug("getBigDecimal(String, BigDecimal) - end");
		}
		return returnBigDecimal;
	}

	public static String getObjectToString(SourceBean sb, String name, String failSafe) {
		Object nameobj = sb.getAttribute(name);
		if (nameobj != null) {
			return nameobj.toString();
		}
		return failSafe;
	}

	public String getObjectToString(String name, String failSafe) {
		return getObjectToString(sb, name, failSafe);
	}

	public String getObjectToString(String attrName) {
		return getObjectToString(attrName, "");
	}

	public static String getObjectToString4Html(SourceBean sb, String name, String failSafe) {
		return StringUtils.formatValue4Html(getObjectToString(sb, name, failSafe));
	}

	public static BigDecimal getBigDecimal(SourceBean sb, String name, BigDecimal failSafe) {
		if (logger.isDebugEnabled()) {
			logger.debug("getBigDecimal(SourceBean, String, BigDecimal) - start");
		}

		BigDecimal bd;
		Object nameobj = sb.getAttribute(name);
		if (nameobj != null) {
			if (nameobj instanceof BigDecimal) {
				bd = (BigDecimal) nameobj;
			} else if (nameobj instanceof String) {
				bd = new BigDecimal(nameobj.toString());
			} else
				bd = failSafe;
		} else
			bd = failSafe;

		if (logger.isDebugEnabled()) {
			logger.debug("getBigDecimal(SourceBean, String, BigDecimal) - end");
		}
		return bd;
	}

	private SourceBean aggiornaSede(TransactionQueryExecutor trans, String consulenteLav, int cdnUtente) {
		if (logger.isDebugEnabled()) {
			logger.debug("aggiornaSede(TransactionQueryExecutor, String, int) - start");
		}

		try {
			Object prgazienda = sb.getAttribute("prgazienda");
			Object prgunita = sb.getAttribute("prgunita");
			BigDecimal numkloUnitaAzienda = (BigDecimal) sb.getAttribute("numklounitaazienda");
			numkloUnitaAzienda = numkloUnitaAzienda.add(BigDecimal.ONE);
			Object[] paramsUAz = new Object[5];
			paramsUAz[0] = consulenteLav;
			paramsUAz[1] = numkloUnitaAzienda;
			paramsUAz[2] = cdnUtente;
			paramsUAz[3] = prgazienda;
			paramsUAz[4] = prgunita;
			boolean risultato = ((Boolean) trans.executeQuery("UPDATE_UNITA_AZIENDA_CIGS2_SARE", paramsUAz, "INSERT"))
					.booleanValue();
			if (!risultato) {
				throw new Exception("Errore aggiornamento unità aziendale");
			}
			sb.updAttribute("numklounitaazienda", numkloUnitaAzienda);

			if (logger.isDebugEnabled()) {
				logger.debug("aggiornaSede(TransactionQueryExecutor, String, int) - end");
			}
			return sb;
		} catch (Exception e) {
			logger.error("aggiornaSede(TransactionQueryExecutor, String, int)", e);

			if (logger.isDebugEnabled()) {
				logger.debug("aggiornaSede(TransactionQueryExecutor, String, int) - end");
			}
			return null;
		}
	}

	public void appendOrNil(StringBuilder s, String tagName) {
		this.appendOrNil(s, tagName, tagName);
	}

	public void appendOrNil(StringBuilder s, String tagName, String attrName) {
		String value = this.getObjectToString4Html(attrName);
		this.appendValueOrNil(s, tagName, value);
	}

	/**
	 * @param s
	 * @param tagName
	 *            - fa anche da attrName
	 */
	public void append(StringBuilder s, String tagName) {
		this.append(s, tagName, tagName);
	}

	/**
	 * @param s
	 * @param tagName
	 *            - fa anche da attrName
	 */
	public void appendTrim(StringBuilder s, String tagName) {
		this.appendTrim(s, tagName, tagName);
	}

	/**
	 * @param s
	 *            - StringBuilder a cui appendere il tag
	 * @param tagName
	 *            - nome del tag
	 * @param attrName
	 *            - nome dell'attributo che contiene il valore
	 */
	public void append(StringBuilder s, String tagName, String attrName) {
		String value = this.getObjectToString4Html(attrName);
		this.appendValue(s, tagName, value);
	}

	/**
	 * @param s
	 *            - StringBuilder a cui appendere il tag
	 * @param tagName
	 *            - nome del tag
	 * @param attrName
	 *            - nome dell'attributo che contiene il valore
	 */
	public void appendTrim(StringBuilder s, String tagName, String attrName) {
		String value = this.getObjectToString4Html(attrName);
		this.appendValueTrim(s, tagName, value);
	}

	/**
	 * 
	 * 
	 * @param s
	 * @param tagName
	 * @param attrDateName
	 * @throws ParseException
	 *             - nel caso che non si riesca a fare il parsing della data estratta dal db - formato richiesto :
	 *             {@link BeanUtils} {@link #SQL_DATE_FORMAT}
	 * 
	 */
	public void appendDate(StringBuilder s, String tagName, String attrDateName) throws ParseException {
		Date sDate = getDateBySql(attrDateName);
		String dValue = date2xsd.format(sDate);
		this.appendValue(s, tagName, dValue);
	}

	public Date getDateBySql(String attrDateName) throws ParseException {
		String value = this.getObjectToString4Html(attrDateName);
		Date sDate = sql2date.parse(value);
		return sDate;
	}

	public Date getDateBySql(String attrDateName, Date failSafe) throws ParseException {
		String value = this.getObjectToString4Html(attrDateName);
		if (StringUtils.isEmptyNoBlank(value)) {
			return failSafe;
		}
		Date sDate = sql2date.parse(value);
		return sDate;
	}

	public void appendDateIfNotEmpty(StringBuilder s, String tagName, String attrDateName) throws ParseException {
		String value = this.getObjectToString4Html(attrDateName);
		if (!StringUtils.isEmptyNoBlank(value)) {
			this.appendDate(s, tagName, attrDateName);
		}
	}

	public void appendValueIfNotEmpty(StringBuilder s, String tagName, String value) {
		if (!StringUtils.isEmptyNoBlank(value)) {
			this.appendValue(s, tagName, value);
		}
	}

	public void appendValueTrimOrNil(StringBuilder s, String tagName, String value) {
		s.append("<tns:");
		s.append(tagName);
		if (StringUtils.isEmptyNoBlank(value)) {
			s.append(" xsi:nil=\"true\"/>");
		} else {
			s.append(">");
			s.append(value.trim());
			s.append("</tns:");
			s.append(tagName);
			s.append(">");
		}
	}

	public void appendValueOrNil(StringBuilder s, String tagName, String value) {
		s.append("<tns:");
		s.append(tagName);
		if (StringUtils.isEmptyNoBlank(value)) {
			s.append(" xsi:nil=\"true\"/>");
		} else {
			s.append(">");
			s.append(value);
			s.append("</tns:");
			s.append(tagName);
			s.append(">");
		}
	}

	public void appendValueTrim(StringBuilder s, String tagName, String value) {
		s.append("<tns:");
		s.append(tagName);
		s.append(">");
		// TODO Dovrebbe essere sempre != null!
		if (value != null) {
			s.append(value.trim());
		}
		s.append("</tns:");
		s.append(tagName);
		s.append(">");
	}

	public void appendValue(StringBuilder s, String tagName, String value) {
		s.append("<tns:");
		s.append(tagName);
		s.append(">");
		s.append(value);
		s.append("</tns:");
		s.append(tagName);
		s.append(">");
	}

	public String getObjectToString4Html(String attrName) {
		return getObjectToString4Html(sb, attrName, "");
	}

	public void appendIfNotEmpty(StringBuilder s, String tagName, String attrName) {
		String stringNotNull = this.getObjectToString4Html(attrName);
		this.appendValueIfNotEmpty(s, tagName, stringNotNull);
	}

	public void appendIfNotEmpty(StringBuilder s, String tagName) {
		this.appendIfNotEmpty(s, tagName, tagName);
	}

	public static boolean isSedeConLavoratoriCollegati(TransactionQueryExecutor transExec, String prgaccordo,
			String prgazienda, String prgunita) throws EMFInternalError {
		if (logger.isDebugEnabled()) {
			logger.debug("isSedeConLavoratoriCollegati(TransactionQueryExecutor, String, String, String) - start");
		}

		SourceBean assocs = (SourceBean) transExec.executeQuery("GET_PRGLAVORATORI_ASSEGNATI_SEDE",
				new Object[] { prgaccordo, prgazienda, prgunita }, "SELECT");
		if (assocs == null) {
			// TODO può mai accadere?
		}
		if (assocs.containsAttribute(ROW)) {
			return true;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("isSedeConLavoratoriCollegati(TransactionQueryExecutor, String, String, String) - end");
		}
		return false;
	}

	public void appendDateIfNotEmpty(StringBuilder s, String tagName) throws ParseException {
		this.appendDateIfNotEmpty(s, tagName, tagName);

	}

	public void appendDate(StringBuilder s, String tagName) throws ParseException {
		this.appendDate(s, tagName, tagName);
	}

	public void appendDateOrNil(StringBuilder s, String tagName) throws ParseException {
		this.appendDateOrNil(s, tagName, tagName);

	}

	public void appendDateOrNil(StringBuilder s, String tagName, String attrDateName) throws ParseException {
		String value = this.getObjectToString4Html(attrDateName);
		if (StringUtils.isEmptyNoBlank(value)) {
			this.appendValueOrNil(s, tagName, "");
		} else {
			Date sDate = sql2date.parse(value);
			String dValue = date2xsd.format(sDate);
			this.appendValue(s, tagName, dValue);
		}

	}

}
