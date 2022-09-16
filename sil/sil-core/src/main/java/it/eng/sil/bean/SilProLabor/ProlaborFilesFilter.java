/*
 * Creato il 29-Jun-04
 * Author: rolfini
 * 
 */
package it.eng.sil.bean.SilProLabor;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author rolfini
 * 
 */
public class ProlaborFilesFilter implements FilenameFilter {

	/**
	 * 
	 */
	public ProlaborFilesFilter() {
		super();
		// TODO Stub di costruttore generato automaticamente
	}

	public boolean accept(File dir, String name) {
		if (name.endsWith("P.XML"))
			return true;
		else
			return false;
	}

}
