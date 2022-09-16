package it.eng.afExt.utils;

public class PatInailUtils {

	PatInailUtils() {
	}

	/**
	 * Metodo per il controllo di corretteza del pat INAIL
	 */
	public static boolean controllaInail(String strPatInail) {
		String confronto = "0123456789";
		boolean contInailOk = true;
		int tPesi[] = { 7, 1, 3, 5, 7, 1, 3, 5 };
		int ckS[] = new int[2];
		int somma = 0;
		int nCod = 0;
		int nCC1 = 0;
		int nCC2 = 0;

		try {
			// Lunghezza pat inail
			if (strPatInail.length() != 10) {
				contInailOk = false;
			}
		} catch (Exception ex) {
			contInailOk = false;
			return contInailOk;
		}
		// Se sono pat non ancora rilasciati o pat agricoli vanno accettati e
		// non
		// faccio alcun altro controllo
		if (strPatInail.equals("0000000000") || strPatInail.equals("9999999999")) {
			contInailOk = false;
		}
		if (contInailOk) {
			// Prelievo valori immessi per il CheckSum
			ckS[0] = Integer.parseInt(strPatInail.substring(8, 9));
			ckS[1] = Integer.parseInt(strPatInail.substring(9, 10));
			// ckS = strPatInail.substring(8,10).toCharArray();

			String chrCons = "";
			// Calcolo checkSum
			for (int j = 0; j < 8; j++) {
				chrCons = strPatInail.substring(j, (j + 1));
				if (confronto.indexOf(chrCons, 1) == -1) {
					// non trovato
					contInailOk = false;
					break;
				} else {
					try {
						somma += Integer.parseInt(chrCons) * tPesi[j];
					} catch (Exception ex) {
						contInailOk = false;
						return contInailOk;
					}
				}
			}
			try {
				if (somma > 100) {
					nCod = (100 - (somma % 100));
				} else {
					nCod = (100 - somma);
				}
				nCC1 = (nCod % 10);
				nCC2 = (nCod % 11);
				if (nCC2 == 10) {
					nCC2 = 1;
				}
			} catch (Exception ex) {
				contInailOk = false;
				return contInailOk;
			}
			if ((ckS[0] != nCC1) || (ckS[1] != nCC2)) {
				contInailOk = false;
			}
		}
		return contInailOk;
	}

}