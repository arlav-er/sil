/*
 * Creato il 21-nov-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util;

/**
 * @author melandri
 * 
 * Calcolo dei fiorni festivi compresi tra due date esclusi i sabati e le
 * domeniche
 */

import java.util.Date;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

public class GiorniFestivi {

	public GiorniFestivi() {
	}

	public int getNumGiorniFestivi(String DATINIZIOVALIDITA, String DATPAGAMENTO, SourceBean serviceResponse) {
		int numFestivi = 0;
		String numgg = "";
		String nummm = "";
		String numaaaa = "";

		int giorno1 = Integer.parseInt(DATINIZIOVALIDITA.substring(0, 2));
		int mese1 = Integer.parseInt(DATINIZIOVALIDITA.substring(3, 5)) - 1;
		int anno1 = Integer.parseInt(DATINIZIOVALIDITA.substring(6)) - 1900;
		Date datInizioVal = new Date(anno1, mese1, giorno1);

		int giorno2 = Integer.parseInt(DATPAGAMENTO.substring(0, 2));
		int mese2 = Integer.parseInt(DATPAGAMENTO.substring(3, 5)) - 1;
		int anno2 = Integer.parseInt(DATPAGAMENTO.substring(6)) - 1900;
		Date datPagam = new Date(anno2, mese2, giorno2);

		SourceBean rowFest = null;
		Vector rowsFest = null;

		if (serviceResponse.containsAttribute("stampa")) {
			rowsFest = serviceResponse.getAttributeAsVector("GIORNIFESTIVI.ROWS.ROW");
		} else {
			rowsFest = serviceResponse.getAttributeAsVector("M_Calcola_NumFestivi.ROWS.ROW");
		}
		int i, num_gg, num_mm, num_aa;
		int festiviPerAnno = 0;
		int diffAnni = Integer.parseInt(DATPAGAMENTO.substring(6)) - Integer.parseInt(DATINIZIOVALIDITA.substring(6));

		for (i = 0; i < rowsFest.size(); i++) {
			rowFest = (SourceBean) rowsFest.elementAt(i);

			numgg = Utils.notNull(rowFest.getAttribute("numgg"));
			nummm = Utils.notNull(rowFest.getAttribute("nummm"));
			numaaaa = Utils.notNull(rowFest.getAttribute("numaaaa"));

			if (!numaaaa.equals("")) {
				num_gg = Integer.parseInt(rowFest.getAttribute("numgg").toString());
				num_mm = Integer.parseInt(rowFest.getAttribute("nummm").toString()) - 1;
				num_aa = Integer.parseInt(rowFest.getAttribute("numaaaa").toString()) - 1900;
				Date date = new Date(num_aa, num_mm, num_gg);
				if ((date.before(datPagam) || date.equals(datPagam))
						&& (date.after(datInizioVal) || date.equals(datInizioVal)))
					numFestivi++;
			} else {
				// contatore festivi per ogni anno ---> festiviPerAnno
				festiviPerAnno++;
				num_gg = Integer.parseInt(rowFest.getAttribute("numgg").toString());
				num_mm = Integer.parseInt(rowFest.getAttribute("nummm").toString()) - 1;
				if (diffAnni == 0) {
					Date date = new Date(anno1, num_mm, num_gg);
					if ((date.before(datPagam) || date.equals(datPagam))
							&& (date.after(datInizioVal) || date.equals(datInizioVal)))
						numFestivi++;
				} else {
					Date dateIn = new Date(anno1, num_mm, num_gg);
					Date dateFineAnno = new Date(anno1, 11, 31);
					if ((dateIn.before(dateFineAnno) || dateIn.equals(dateFineAnno))
							&& (dateIn.after(datInizioVal) || dateIn.equals(datInizioVal)))
						numFestivi++;

					Date dateFin = new Date(anno2, num_mm, num_gg);
					Date dateInAnno = new Date(anno2, 0, 1);
					if ((dateFin.before(datPagam) || dateFin.equals(datPagam))
							&& (dateFin.after(dateInAnno) || dateFin.equals(dateInAnno)))
						numFestivi++;
				}
			}
		} // for
		if (diffAnni != 0)
			numFestivi = numFestivi + festiviPerAnno * (diffAnni - 1);

		return numFestivi;
	}
}
