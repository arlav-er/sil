/*
 * Created on 9-nov-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.webservices.madreperla.servizi;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author esposito
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class ValidazioneErrorHandler implements ErrorHandler {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ValidazioneErrorHandler.class.getName());
	private int errors;
	private int fatals;
	private int warnings;

	public ValidazioneErrorHandler() {
		errors = 0;
		fatals = 0;
		warnings = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException e) throws SAXException {
		// TODO Auto-generated method stub
		errors++;
		// TracerSingleton.log("Madreperla", TracerSingleton.CRITICAL, "Madreperla:validaXml:errore", e);
		printInfo(e);
		throw e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException e) throws SAXException {
		// TODO Auto-generated method stub
		fatals++;
		// TracerSingleton.log("Madreperla", TracerSingleton.CRITICAL, "Madreperla:validaXml:errore fatale", e);
		printInfo(e);
		throw e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	public void warning(SAXParseException e) throws SAXException {
		// TODO Auto-generated method stub
		warnings++;
		// TracerSingleton.log("Madreperla", TracerSingleton.CRITICAL, "Madreperla:validaXml:warning", e);
		printInfo(e);
		throw e;
	}

	private void printInfo(SAXParseException e) {
		_logger.debug("Madreperla:validaXml:Linea " + e.getLineNumber());

		_logger.debug("Madreperla:validaXml:Colonna " + e.getColumnNumber());

		_logger.debug("Madreperla:validaXml:Messaggio " + e.getMessage());

	}

	public boolean IsError() {
		if ((errors == 0) && (warnings == 0) && (fatals == 0))
			return false;
		else
			return true;
	}
}