package com.engiweb.framework.transcoding;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;

import com.engiweb.framework.configuration.ConfigSingleton;

/**
 * Questa classe effettua la transcodifica: data una stringa contente XML, applica un XSL(già processato, ovvero
 * un'istanza di Templates) e produce HTML, WML etc Esempio di utilizzo della classe: Transcoder transcoder = new
 * Transcoder(); //out può essere un'istanza di PrintWriter o OutputStream(vedere doc Xalan) Result result = new
 * StreamResult(out); transcoder.setXSL(template); transcoder.perform(list.perform().toXML(), result);
 * 
 * @author Daniela Butano - daniela.butano@engiweb.com
 * @version 1.0
 */
public class Transcoder {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Transcoder.class.getName());
	private static final String TRACE_FILE_NAME = "transcoder";
	private static final String TRACE_FILE_PATH = "/tmp/";
	private static boolean _traceFileOpened = false;
	private static boolean _debug = false;
	private static FileWriter _fileWriter = null;
	private static PrintWriter _printWriter = null;

	// private static PrintTraceListener _printTraceListener = null;

	private Transcoder() {
		super();
	} // private Transcoder()

	public static void perform(String xml, Templates xslTemplate, Result out) {
		_logger.debug("Transcoder::perform: xml\n" + xml);

		Source xmlSource = new StreamSource(new StringReader(xml));
		try {
			Transformer transformer = xslTemplate.newTransformer();
			_logger.debug("Transcoder::perform: transformer [" + transformer + "]");

			/*
			 * if (transformer instanceof TransformerImpl) { if (!_traceFileOpened) { synchronized(Transcoder.class) {
			 * if (!_traceFileOpened) openTraceFile(); } // synchronized (Transcoder.class) } // if (!_traceFileOpened)
			 * _logger.debug( "Transcoder::perform: _debug [" + _debug + "]");
			 * 
			 * if (_debug) { TransformerImpl transformerImpl = (TransformerImpl)transformer; TraceManager traceManager =
			 * transformerImpl.getTraceManager(); traceManager.addTraceListener(_printTraceListener); _logger.debug(
			 * "Transcoder::perform: trace listener aggiunto");
			 * 
			 * synchronized(Transcoder.class) { transformer.transform(xmlSource, out); } // synchronized
			 * (Transcoder.class) } // if (_debug) else transformer.transform(xmlSource, out); } // if (transformer
			 * instanceof TransformerImpl) else
			 */

			transformer.transform(xmlSource, out);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Transcoder::perform: ", ex);

		} // catch (Exception ex)
	} // public static void perform(String xml, Templates xslTemplate, Result
		// out)

	private static void openTraceFile() {
		_logger.debug("Transcoder::openTraceFile: invocato");

		ConfigSingleton configure = ConfigSingleton.getInstance();
		String traceFilePath = (String) configure.getAttribute("TRACING.TRACE_PATH");
		if (traceFilePath == null)
			traceFilePath = TRACE_FILE_PATH;
		SimpleDateFormat formatter = new SimpleDateFormat("yMMdd");
		String dateString = formatter.format(new Date());
		String traceFileName = TRACE_FILE_NAME;
		String debug = (String) configure.getAttribute("TRACING.DEBUG");
		_debug = ((debug != null) && debug.equalsIgnoreCase("TRUE"));
		String fileName = "";
		if (ConfigSingleton.getRootPath() != null)
			fileName += ConfigSingleton.getRootPath();
		fileName += traceFilePath + traceFileName + dateString + ".log";
		try {
			_fileWriter = new FileWriter(fileName);
			_printWriter = new PrintWriter(_fileWriter, true);

		} // try
		catch (IOException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Transcoder::openTraceFile: ", ex);

			_debug = false;
			_fileWriter = null;
			_printWriter = null;

		} // catch (IOException ex)
		_traceFileOpened = true;
	} // private static void openTraceFile()

	private static void closeTraceFile() {
		_logger.debug("Transcoder::closeTraceFile: invocato");

		_debug = false;
		try {
			_printWriter.close();
			_fileWriter.close();
		} // try
		catch (IOException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Transcoder::closeTraceFile: ", ex);

		} // catch (IOException ex)
		_printWriter = null;
		_fileWriter = null;
		_traceFileOpened = false;
	} // private static void closeTraceFile()
} // public class Transcoder
