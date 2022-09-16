/*
 * Creato il 6-ago-04
 */
package it.eng.sil.bean.importazioneMovimenti;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author roccetti
 * 
 *         Filtro per decidere se un determinato file contiene un tracciato SARE da importare nel SIL
 */
public class SARETrackFilesFilter implements FilenameFilter {

	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 
	 */
	public SARETrackFilesFilter() {
		super();
	}

	/**
	 * 
	 */
	public boolean accept(File dir, String name) {
		try {
			// Controllo se il nome rispetta il formato sare t_yyyyMMdd.txt
			String prefix = name.substring(0, 2);
			String date = name.substring(2, 10);
			Date d = formatter.parse(date);
			if (prefix.equalsIgnoreCase("t_")) {
				return true;
			} else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

}
