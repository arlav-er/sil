/*
 * Creato il 20-nov-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util;

/*
 * Classe che calcola il numero di sabati e domeniche comprese tra due date. La
 * data minore è esclusa dal calcolo, quella maggiore è inclusa.
 */

import java.util.Date;

public class GiorniNL {
	private String data1;
	private String data2;
	private int diffDate;

	public GiorniNL() {
	}

	// (Data fine,Data inizio) date in formato String "DD/MM/YYYY"
	public int getNumGiorniNL(String data1, String data2) {
		int numNL = 0;

		int giorno1 = Integer.parseInt(data1.substring(0, 2));
		int mese1 = Integer.parseInt(data1.substring(3, 5)) - 1;
		int anno1 = Integer.parseInt(data1.substring(6)) - 1900;
		Date date1 = new Date(anno1, mese1, giorno1);

		int giorno2 = Integer.parseInt(data2.substring(0, 2));
		int mese2 = Integer.parseInt(data2.substring(3, 5)) - 1;
		int anno2 = Integer.parseInt(data2.substring(6)) - 1900;
		Date date2 = new Date(anno2, mese2, giorno2);

		int dayDataFin = date1.getDay();
		int dayDataIn = date2.getDay();

		UtilityNumGGTraDate cmpDate = new UtilityNumGGTraDate();
		this.diffDate = cmpDate.getNumRitardo(data1, data2);
		int numDom = cmpDate.getNumGiorniNL(data1, data2, this.diffDate);

		int numSab = 0;

		if ((dayDataFin == 6 && dayDataIn == 6) || (dayDataFin != 6 && dayDataIn != 6)) {
			numSab = numDom;
		} else if (dayDataFin != 6 && dayDataIn == 6) {
			numSab = numDom - 1;
		} else if (dayDataFin == 6 && dayDataIn != 6) {
			numSab = numDom + 1;
		}

		numNL = numSab + numDom;

		return numNL;
	}

	public int getDiffDate() {
		return this.diffDate;
	}

	public void setDiffDate(int diffDate) {
		this.diffDate = diffDate;
	}
}
