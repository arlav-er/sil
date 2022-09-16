package com.engiweb.framework.configuration;

import java.io.File;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.init.InitializerManager;

/**
 * Questa classe offre i servizi per recuperare da tutti i files di configurazione XML dell'applicazione il valore dei
 * parametri in esso contenuti.
 * 
 * @author Luigi Bellio
 * @see com.engiweb.framework.base.sourceBean
 */
public final class ConfigSingleton extends SourceBean {
	private static String _rootPath = null;
	private static String _engConfigFile = null;
	private static ConfigSingleton _instance = null;
	private static final String AF_ROOT_PATH = "";
	private static final String AF_CONFIG_FILE = "/WEB-INF/conf/master.xml";
	private static final String CONFIGURATOR = "CONFIGURATOR";
	private static final String CONFIGURATOR_PATH = "PATH";
	public static String LINE_SEPARATOR = System.getProperty("line.separator");

	// 30/08/2006 FV
	private static String afContext = null;
	private static String hostname = null;

	/**
	 * In questo costruttore tutti i files XML di configurazione vengono letti e con le informazioni in essi contenute
	 * vengono create delle istanze di SourceBean; questo permette di accedere alle informazioni utilizzando i metodi
	 * della classe SourceBean.
	 * 
	 * @param engConfigFile
	 *            percorso e nome del file XML da leggere.
	 */
	private ConfigSingleton(String engConfigFile) throws Exception {
		super(SourceBean.fromXMLFile(engConfigFile));
		Object configuratorsObject = getAttribute(CONFIGURATOR);
		if (configuratorsObject == null)
			return;
		if (configuratorsObject instanceof SourceBean) {
			String configuratorName = (String) ((SourceBean) configuratorsObject).getAttribute(CONFIGURATOR_PATH);
			if (configuratorName == null)
				return;
			setAttribute(new ConfigSingleton(configuratorName));
			return;
		} // if (configurators instanceof SourceBean)
		if (configuratorsObject instanceof Vector) {
			Vector configuratorsVector = (Vector) configuratorsObject;
			for (int i = 0; i < configuratorsVector.size(); i++) {
				Object configuratorObject = configuratorsVector.elementAt(i);
				if (configuratorObject instanceof SourceBean) {
					String configuratorName = (String) ((SourceBean) configuratorObject)
							.getAttribute(CONFIGURATOR_PATH);
					if (configuratorName == null)
						continue;
					try {
						setAttribute(new ConfigSingleton(configuratorName));
					} // try
					catch (Exception ex) {
						System.out.println("ConfigSingleton::ConfigSingleton: errore creazione configuratore ["
								+ configuratorName + "]");
						ex.printStackTrace();
					} // catch(Exception ex) try
				} // if (configuratorObject instanceof SourceBean)
			} // for (int i = 0; i < configuratorsVector.size(); i++)
		} // if (configuratorsObject instanceof Vector)
	} // private ConfigSingleton(String engConfigFile) throws Exception

	public static ConfigSingleton getInstance() {
		if (_instance == null) {
			synchronized (ConfigSingleton.class) {
				if (_instance == null) {
					try {
						_instance = new ConfigSingleton(getConfigFileName());
					} // try
					catch (Exception ex) {
						System.out.println("ConfigSingleton::getInstance: errore creazione configuratore ["
								+ getConfigFileName() + "]");
						ex.printStackTrace();
					} // catch(Exception ex) try
				} // if (_instance == null)
			} // synchronized(ConfigSingleton.class)
		} // if (_instance == null)
		return _instance;
	} // public static ConfigSingleton getInstance()

	// FV & AS 12-09-2006 Oltre a cricare i file del Fw, viene invocato il
	// manager degli inizializzatori
	// utile se si e' fuori da contesto web

	public static ConfigSingleton getInstanceAndInit() {
		if (_instance == null) {
			synchronized (ConfigSingleton.class) {
				if (_instance == null) {
					try {
						ConfigSingleton _instance_tmp = new ConfigSingleton(getConfigFileName());
						InitializerManager.init();
						_instance = _instance_tmp;
					} // try
					catch (Exception ex) {
						System.out.println("ConfigSingleton::getInstance: errore creazione configuratore ["
								+ getConfigFileName() + "]");
						ex.printStackTrace();
					} // catch(Exception ex) try
				} // if (_instance == null)
			} // synchronized(ConfigSingleton.class)
		} // if (_instance == null)
		return _instance;
	} // public static ConfigSingleton getInstance()

	public static void release() {
		if (_instance != null) {
			synchronized (ConfigSingleton.class) {
				_instance = null;
			} // synchronized (ConfigSingleton.class)
		} // if (_instance != null)
	} // public static void release()

	/**
	 * Ritorna il rootPath dell'installazione dell'applicativo. Questo percorso viene utilizzato dal framework per
	 * recuperare le risorse(esempio i files XML).L'informazione del rootPath può essere resa disponibile al framework
	 * anche impostando una variabile di ambiente con nome "AF_ROOT_PATH".
	 * 
	 * @return <code>String<code> specifica il rootPath.
	 */
	public static String getRootPath() {
		if (_rootPath == null) {
			synchronized (ConfigSingleton.class) {
				if (_rootPath == null)
					_rootPath = System.getProperty("AF_ROOT_PATH");
				if (_rootPath == null)
					_rootPath = AF_ROOT_PATH;
			} // synchronized(ConfigSingleton.class)
		} // if (_rootPath == null)
		return _rootPath;
	} // public static String getRootPath()

	/**
	 * Questo metodo permette di impostare il rootPath.
	 * 
	 * @param rootPath
	 *            stringa che rappresenta il rootpath.
	 */
	public static void setRootPath(String rootPath) {
		synchronized (ConfigSingleton.class) {
			_rootPath = rootPath;
		} // synchronized(ConfigSingleton.class)
	} // protected static void setRootPath(String rootPath)

	/**
	 * Ritorna il nome del file XML contenente i riferimenti agli altri files XML.Il nome del file XML master può essere
	 * resa disponibile al framework anche impostando una variabile di ambiente con nome "AF_CONFIG_FILE".
	 * 
	 * @return <code>String<code> specifica il nome del file XML master.
	 */
	public static String getConfigFileName() {
		if (_engConfigFile == null) {
			synchronized (ConfigSingleton.class) {
				if (_engConfigFile == null)
					_engConfigFile = System.getProperty("AF_CONFIG_FILE");
				if (_engConfigFile == null)
					_engConfigFile = AF_CONFIG_FILE;
			} // synchronized(ConfigSingleton.class)
		} // if (_engConfigFile == null)
		return _engConfigFile;
	} // public static String getConfigFileName()

	/**
	 * Questo metodo permette di impostare il nome del file XML master.
	 * 
	 * @param specifica
	 *            il nome del file XML master.
	 */
	public static void setConfigFileName(String engConfigFile) {
		synchronized (ConfigSingleton.class) {
			_engConfigFile = engConfigFile;
		} // synchronized(ConfigSingleton.class)
	} // protected static void setConfigFileName(String engConfigFile)

	/**
	 * @return
	 */
	public static String getContextName() {

		if (afContext == null) {
			synchronized (ConfigSingleton.class) {
				afContext = System.getProperty("CONTEXT_NAME");
			}
		}
		return afContext;
	}

	/**
	 * @param string
	 */
	public static void setContextName(String string) {
		afContext = string;
	}

	public static String getHostname() {
		if (hostname == null) {

			try {
				java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
				hostname = localMachine.getHostName();
			} catch (java.net.UnknownHostException uhe) {
				uhe.printStackTrace();
			}
		}
		return hostname;
	}

	public static String getLogBatchPath() {
		String path = System.getProperty("jboss.server.log.dir");

		path += File.separator + "batch";

		return path;
	}

} // public final class ConfigSingleton extends SourceBean
