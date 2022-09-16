package it.eng.afExt.utils;

public class PosInpsUtils {
	/**
	 * Metodo per il controllo di corretteza della posizione INPS
	 */

	PosInpsUtils() {
	}

	public static boolean controllaInps(String strPosInps) {
		boolean contInpsOk = true;
		int codProvProg[] = new int[8];
		int controCod[] = new int[2];
		long sommaDisp = 0;
		long sommaPari = 0;

		// Lunghezza codice = 15
		if (strPosInps.length() != 15) {
			contInpsOk = false;
		}
		try {
			// validità codice provincia
			if ((Integer.parseInt(strPosInps.substring(0, 2)) < 1)
					|| (Integer.parseInt(strPosInps.substring(0, 2)) > 95)) {
				contInpsOk = false;
			}
			// Progressivo maggiore di zero
			if (Integer.parseInt(strPosInps.substring(2, 8)) < 0) {
				contInpsOk = false;
			}
		} catch (Exception ex) {
			contInpsOk = false;
			return contInpsOk;
		}
		if (contInpsOk) {
			for (int i = 0; i < 8; i++) {
				codProvProg[i] = Integer.parseInt(strPosInps.substring(i, (i + 1)));
			}

			// Prelievo delle cifre di controllo
			controCod[0] = Integer.parseInt(strPosInps.substring(8, 9));
			controCod[1] = Integer.parseInt(strPosInps.substring(9, 10));
			try {
				for (int i = 0; i < 8; i++) {
					if ((i % 2) == 0) { /*
										 * Si sommano le cifre di posto dispari, considerando che la variabile i parte
										 * da zero
										 */
						sommaDisp += codProvProg[i];
					} else {
						/*
						 * Si sommano le cifre di posto pari, considerando che la variabile i parte da zero
						 */
						sommaPari += codProvProg[i];
					}
				}
			} catch (Exception ex) {
				contInpsOk = false;
				return contInpsOk;
			}
			String appoggio = "";
			// Controllo contro codice
			if (sommaDisp >= 10) {
				appoggio = String.valueOf(sommaDisp).substring(1, 2);
			} else {
				appoggio = String.valueOf(sommaDisp).substring(0, 1);
			}
			if (controCod[0] != Integer.parseInt(appoggio)) {
				contInpsOk = false;
			}
			if (sommaPari >= 10) {
				appoggio = String.valueOf(sommaPari).substring(1, 2);
			} else {
				appoggio = String.valueOf(sommaPari).substring(0, 1);
			}
			if (controCod[1] != Integer.parseInt(appoggio)) {
				contInpsOk = false;
			}
		}
		return contInpsOk;
	}
}