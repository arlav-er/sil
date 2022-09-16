/*
 * Created on May 31, 2007
 */
package it.eng.sil.coordinamento.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.engiweb.framework.configuration.ConfigSingleton;

/**
 * @author savino
 */
public class ConfigurazioneCoordinamentoRegionale extends ConfigAbstract {

	private static final String CONFIG_FILENAME = "/servizi_coordinamento.properties";
	private static ConfigurazioneCoordinamentoRegionale instance = null;

	private static Logger logger = Logger.getLogger(ConfigurazioneCoordinamentoRegionale.class.getName());

	/**
	 * Rende l'istanza
	 */
	public static synchronized ConfigurazioneCoordinamentoRegionale getInstance() throws IOException {

		if (instance == null) {
			instance = new ConfigurazioneCoordinamentoRegionale();
		}
		return instance; // rende l'istanza
	}

	/**
	 * Costruttore - si inizializza caricando le proprietà dal file di configurazione
	 */
	private ConfigurazioneCoordinamentoRegionale() throws IOException {

		properties = new Properties();

		InputStream propsFile = null;
		try {
			String propsPath = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
					+ File.separator + CONFIG_FILENAME;

			propsFile = new FileInputStream(propsPath);

			// leggo il file con le proprietà
			properties.load(propsFile);

		} catch (Exception ex) {
			logger.fatal(ex);
			throw new IOException("Errore leggendo il file di configurazione '" + CONFIG_FILENAME + "'. Errore interno:"
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