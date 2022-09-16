package com.engiweb.framework.presentation;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import com.engiweb.framework.configuration.ConfigSingleton;

public class StylesheetFile {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StylesheetFile.class.getName());
	private String _fileName = null;
	private long _fileDate = 0;
	private Templates _template = null;

	public StylesheetFile() {
		_logger.debug("StylesheetFile::StylesheetFile: invocato");

	} // public StylesheetFile()

	public void setFileName(String fileName) {
		_fileName = fileName;
	} // public void setFileName (String fileName)

	public Templates getTemplate() {
		if (_fileName == null) {
			_logger.debug("StylesheetFile::getTemplate: _fileName nullo");

			return null;
		} // if (_fileName == null)
		ConfigSingleton configure = ConfigSingleton.getInstance();
		// verifico se ricaricare gli xsl
		String reload = (String) configure.getAttribute("COMMON.XSL_RELOAD");
		if ((_template == null) || ((reload != null) && reload.equalsIgnoreCase("TRUE"))) {
			// Ricarico le pagine XSL con la data di registrazione diversa da
			// quella caricata
			try {
				File stylesheet = new File(configure.getRootPath() + _fileName);
				long currentFileDate = stylesheet.lastModified();
				// se la data del file in cache è diversa da quello su file
				// System
				// allora sostitusco l'oggetto in cache.
				if (_fileDate != currentFileDate)
					loadStylesheet();
			} // try
			catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "StylesheetFile::getTemplate:", ex);

			} // catch (Excpetion ex)
		} // if ((_template == null) || ((reload != null) &&
			// reload.equalsIgnoreCase("TRUE")))
		return _template;
	} // public Templates getTemplate()

	// Questo metodo mi permette di caricare un solo stylesheet
	public void loadStylesheet() {
		_logger.debug("StylesheetFile::loadStylesheet: invocato");

		ConfigSingleton configure = ConfigSingleton.getInstance();
		try {
			// nuova istanza per la lettura dei file XSL
			File stylesheet = new File(configure.getRootPath() + _fileName);
			_fileDate = stylesheet.lastModified();
			Source xslSource = new StreamSource(stylesheet);

			/*
			 * System.setProperty("javax.xml.transform.TransformerFactory",
			 * "org.apache.xalan.processor.TransformerFactoryImpl"); TransformerFactory tFactory =
			 * TransformerFactory.newInstance();
			 */

			TransformerFactory tFactory = TransformerFactory.newInstance();
			_template = tFactory.newTemplates(xslSource);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "FileStylesheet::loadStylesheet:", ex);

		} // catch (Excpetion ex) try
	} // public void loadStylesheet()
} // public class StylesheetFile
