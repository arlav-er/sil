package com.engiweb.framework.dbaccess;

import java.io.FileReader;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.engiweb.framework.configuration.ConfigSingleton;

/**
 * Questa Classe ha la responsabilità di effettuare il parser del file indicato dal Tag
 * <DATA_ACCESS_CONFIGURATION_FILE_PATH> nel file xml principale dell'applicazione e di mettere a disposizione del
 * sosttosistema di accesso ai dati queste infomrazioni in particolare è possibile:
 * <li>Recuperare l'oggetto <B>ConnectionPoolDescriptor</B> dato un nome simbolico
 * <li>Recuperare la lista dei nomi dei pool registrati dall'applicativo
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class Configurator {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Configurator.class.getName());

	private Configurator() {
		ConfigSingleton baseConfigurator = ConfigSingleton.getInstance();
		String engDBConfigFile = (String) baseConfigurator.getAttribute("DATA_ACCESS_CONFIGURATION_FILE_PATH");

		if (engDBConfigFile == null) {
			_logger.debug(
					"Configurator::Configurator: parametro FRAMEWORK.DATA_ACCESS_CONFIGURATION_FILE_PATH non valorizzato");

			return;
		}

		_logger.debug("Configurator::Configurator: DATA_ACCESS_CONFIGURATION_FILE_PATH = " + engDBConfigFile);

		// FV 31/08/2006

		if ((engDBConfigFile != null) && engDBConfigFile.toLowerCase().indexOf("[$hostname_context]") > 0) {

			String hostname_context = ConfigSingleton.getHostname() + "_" + ConfigSingleton.getContextName();
			engDBConfigFile = replace(engDBConfigFile, "[$hostname_context]", hostname_context);

			System.out.println("Percorso relativo file XML di accesso dati:" + engDBConfigFile);

		}

		_logger.debug("Configurator::Configurator: DATA_ACCESS_CONFIGURATION_FILE_PATH = " + engDBConfigFile);

		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
	        SAXParser sp = spf.newSAXParser();
	        XMLReader parser = sp.getXMLReader();
			
			InputSource stream = new InputSource(new FileReader(ConfigSingleton.getRootPath() + engDBConfigFile));
			_contentHandler = new ConfiguratorContentHandler();
			parser.setContentHandler(_contentHandler);
			parser.parse(stream);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Configurator::Configurator:", ex);

		} // catch (Exception ex) try
	} // private Configurator()

	/**
	 * The method for gettinng Instance of Configurator
	 * 
	 * @return the singleton unique istance of <B>Configurator</B>
	 */
	public static Configurator getInstance() {
		if (_instance == null) {
			synchronized (Configurator.class) {
				if (_instance == null)
					_instance = new Configurator();
			} // synchronized(Configurator.class)
		} // if (instance == null)
		return _instance;
	} // public static Configurator getInstance()

	/**
	 * The method for getting the list of names of the pool registered in the data-access subsystem
	 * 
	 * @return a <B>List</B> of <B>String</B> representing the names of the pools registered
	 */
	public List getRegisteredConnectionPoolNames() {
		return _contentHandler.getRegisteredConnectionPoolNames();
	} // public List getRegisteredConnectionPoolNames()

	/**
	 * This method is used for get get a Pool Descriptor Object given the pool name
	 * 
	 * @return a <B>List</B> of <B>String</B> representing the names of the pools registered
	 */
	public ConnectionPoolDescriptor getConnectionPoolDescriptor(String connectionPoolName) {
		return _contentHandler.getConnectionPoolDescriptor(connectionPoolName);
	} // public ConnectionPoolDescriptor getConnectionPoolDescriptor(String
		// connectionPoolName)

	/**
	 * This method is used for retrieve the timeStamp format String
	 * 
	 * @return <B>String</B> representing the timeStamp format String
	 */
	public synchronized String getTimeStampFormat() {
		return _contentHandler.getTimeStampFormat();
	} // public synchronized String getTimeStampFormatString(){

	/**
	 * This method is used for retrieve the date format String
	 * 
	 * @return <B>String</B> representing the date format String
	 */
	public synchronized String getDateFormat() {
		return _contentHandler.getDateFormat();
	} // public synchronized String getDateFormatString()

	// 30/08/2006 FV

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

	private static Configurator _instance = null;
	private ConfiguratorContentHandler _contentHandler = null;
} // public class Configurator
