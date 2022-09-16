package it.eng.sil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigurazioneJMS extends ConfigAbstract {

	private static final String PROP_CONFIG_NAME = "config.filename";
	private static final String DEFAULT_CONFIG_FILENAME = "IDX.properties";
	private static ConfigurazioneJMS instance = null;

	private static Logger logger = Logger.getLogger(ConfigurazioneJMS.class.getName());

	public static ConfigurazioneJMS getInstance() throws IOException {

		if (instance == null) {
			synchronized (ConfigurazioneJMS.class) {
				if (instance == null) {
					instance = new ConfigurazioneJMS();
				}
			}
		}
		return instance; // rende l'istanza
	}

	/**
	 * Costruttore - si inizializza caricando le proprietà dal file di configurazione
	 */
	private ConfigurazioneJMS() throws IOException {

		properties = new Properties();

		InputStream propsFile = null;
		String configFileName = "jms.properties";
		/*
		 * if (configFileName==null || "".equals(configFileName)){ logger.
		 * warn("Variabile di sistema 'config.filename' non impostata sul server. Si utilizza percio' il default (" +
		 * DEFAULT_CONFIG_FILENAME + ")"); configFileName=DEFAULT_CONFIG_FILENAME; }
		 */

		logger.info("File di configurazione: " + configFileName);

		try {

			propsFile = ConfigurazioneJMS.class.getResourceAsStream("/" + configFileName);
			// leggo il file con le proprietà
			properties.load(propsFile);

		} catch (IOException ex) {
			logger.fatal(ex);
			throw new IOException("Errore leggendo il file di configurazione '" + configFileName + "'. Errore interno:"
					+ ex.getMessage());
		} finally {
			if (propsFile != null) {
				try {
					propsFile.close();
				} catch (IOException ignoreMe) {
					logger.error("Errore nel tentativo di chiusura del file di properties.", ignoreMe);
				}
			}
		}
	}

	/** Contiene le proprietà lette */
	private Properties properties = null;

	/**
	 * Rende il valore (senza spazi a inizio e fine: trim) della proprietà.
	 */
	public String getProperty(String key) {
		String value = properties.getProperty(key);
		if (value != null)
			value = value.trim();
		return value;
	}

}
