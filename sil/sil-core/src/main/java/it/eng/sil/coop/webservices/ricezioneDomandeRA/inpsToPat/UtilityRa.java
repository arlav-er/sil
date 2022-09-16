package it.eng.sil.coop.webservices.ricezioneDomandeRA.inpsToPat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.engiweb.framework.configuration.ConfigSingleton;

public class UtilityRa {

	public static final String XSD_DOMANDA = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "nuovoRedditoAttivazione" + File.separator
			+ "Collaborazione_NRA_INPS_To_PAT.xsd";
	public static final String XSD_COMMON = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "nuovoRedditoAttivazione" + File.separator
			+ "Collaborazione_NRA_PAT_INPSCommon.xsd";

	public static Schema getXsdSchema(String xsdFile, String xsdCommonFile) throws SAXException, FileNotFoundException {
		String schemaLang = "http://www.w3.org/2001/XMLSchema";

		// get validation driver:
		SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

		InputStream isCommon = new FileInputStream(xsdCommonFile);
		StreamSource streamSourceCommon = new StreamSource(isCommon);

		InputStream is = new FileInputStream(xsdFile);
		StreamSource streamSource1 = new StreamSource(is);

		Schema schema = factory.newSchema(new Source[] { streamSourceCommon, streamSource1 });

		return schema;
	}

	public static XMLGregorianCalendar toXMLGregorianCalendarDateTime(String dateString)
			throws DatatypeConfigurationException, ParseException {

		XMLGregorianCalendar xc = null;
		GregorianCalendar gc = new GregorianCalendar();
		DateFormat dateFormat = it.eng.afExt.utils.DateUtils.getSimpleDateFormatFixBugMem("dd/MM/yyyy HH:mm:ss");
		Date date = dateFormat.parse(dateString);
		gc.setTime(date);

		xc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

		return xc;
	}

	public static XMLGregorianCalendar toXMLGregorianCalendarDate(String dateString)
			throws DatatypeConfigurationException, ParseException {
		GregorianCalendar gc = new GregorianCalendar();
		Date date = new SimpleDateFormat("dd/mm/yyyy").parse(dateString);
		gc.setTime(date);

		XMLGregorianCalendar xc = null;

		xc = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(gc.get(Calendar.YEAR),
				gc.get(Calendar.MONTH) + 1, gc.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);

		return xc;
	}

}
