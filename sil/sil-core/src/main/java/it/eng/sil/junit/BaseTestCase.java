/*
 * Creato il 17-feb-05
 * Author: vuoto
 * 
 */
package it.eng.sil.junit;

import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.init.InitializerManager;

/**
 * @author vuoto
 * 
 */
public class BaseTestCase {
	private static BaseTestCase instance = null;

	public final static String ROOT_PATH = "C:\\EclipseJBoss\\SilWeb\\WebContent\\";

	private BaseTestCase() {

		try {
			init();
		} catch (Exception e) {
			// // TODO Blocco catch generato automaticamente
			e.printStackTrace();
		}
	}

	private void init() throws Exception {

		String configFileName = "WEB-INF/conf/junit/master_junit.xml";

		ConfigSingleton.setRootPath(ROOT_PATH);
		ConfigSingleton.setConfigFileName(configFileName);
		InitializerManager.init();

	}

	public static BaseTestCase getInstance() {

		if (instance == null) {
			// accesso protetto da monitor di classe
			synchronized (BaseTestCase.class) {
				if (instance == null) {
					// invocazione del costruttore (protetto)
					instance = new BaseTestCase();
					// TracerSingleton.enable();
				}
			}
		}

		return instance; // rende l'istanza

	}

}
