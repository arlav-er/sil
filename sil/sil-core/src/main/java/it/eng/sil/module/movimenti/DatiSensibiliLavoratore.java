/*
 * Creato il Nov 9, 2004
 *
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.util.Utils;

/**
 * @author savino
 * 
 *         Questa classe genera il codice js che verra' utilizzato da una funzione js per mostrare a video alcune
 *         informazioni del lavoratore.
 */
public class DatiSensibiliLavoratore {
	public static String toJS(SourceBean row) {
		return toJS(row, "");
	}

	/**
	 * Genera il codice js che imposta il valore delle proprieta' della variabile datiSensibiliLavoratore. Es.
	 * "window.opener.datiSensibiliLavoratore.etaMinoreDi15 = true;"
	 * 
	 * @param row
	 * @param frameSrc
	 *            non deve essere null. Es. "window.opener." oppure semplicemente blank ("")
	 * @return
	 */
	public static String toJS(SourceBean row, String frameSrc) {
		String ret = "";
		if (row == null)
			return ret;
		String etaMinoreDi15 = "false", codCMTipoIscr = null, codTipoMob = null, provincia = null, contesto = null;
		String codMobilita = null;
		String codSospensione = null;
		String codMonoTipoRagg = null;
		String iscrizionePossibile = null;
		// in base alla provincia questi controlli potrebbero essere fatti o
		// meno
		provincia = (String) row.getAttribute("provincia");
		contesto = (String) row.getAttribute("contesto");
		if (!controlla(provincia, contesto))
			return ret;
		// dati da controllare
		etaMinoreDi15 = (String) row.getAttribute("etaMinoreDi15");
		if (etaMinoreDi15 == null)
			etaMinoreDi15 = "false";
		iscrizionePossibile = (String) row.getAttribute("iscrizionePossibile");
		codCMTipoIscr = (String) row.getAttribute("CODCMTIPOISCR");
		codTipoMob = (String) row.getAttribute("CODTIPOMOB");
		codMobilita = (String) row.getAttribute("CODMOBILITA");
		codSospensione = (String) row.getAttribute("CODSOSPENSIONE");
		codMonoTipoRagg = (String) row.getAttribute("codMonoTipoRagg");
		ret = frameSrc + "datiSensibiliLavoratore = new DatiLavoratore();";
		ret += frameSrc + "datiSensibiliLavoratore.etaMinoreDi15 = " + etaMinoreDi15 + ";";
		ret += frameSrc + "datiSensibiliLavoratore.inMobilita = " + Utils.notNull(codMobilita).equals("A") + ";";
		ret += frameSrc + "datiSensibiliLavoratore.inSosp = " + Utils.notNull(codSospensione).equals("S") + ";";
		ret += frameSrc + "datiSensibiliLavoratore.disabile = " + Utils.notNull(codMonoTipoRagg).equals("D") + ";";
		ret += frameSrc + "datiSensibiliLavoratore.inCollMirato = " + iscrizionePossibile + ";";
		return ret;
	}

	/**
	 * Se per una provincia e in un contesto (per esempio anagrafica lav.) il controllo non va fatto allora ..... Per il
	 * momento questo controllo non viene utilizzato.
	 */
	public static boolean controlla(String provincia, String contesto) {
		return true;
	}

}
