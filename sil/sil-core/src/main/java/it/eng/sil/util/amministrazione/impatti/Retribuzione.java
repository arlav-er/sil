/*
 * Creato il 13-ottobre-04
 *
 */
package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

/**
 * @author savino
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class Retribuzione {
	/**
	 * Ritorna la retribuzione da considerare per il calcolo del reddito. Se e' stata fatta la dichiarazione per sanare
	 * la situazione amministrativa (oppure e' in corso) allora viene utilizzato il campo DECRETRIBUZIONEMENSANATA, o il
	 * valore opportuno in base al tipo di dichiarazione (ger|dett, sup|mancato sup)
	 * 
	 * @param m
	 * @return
	 * @throws Exception
	 */
	public static BigDecimal getRetribuzioneMen(MovimentoBean m) throws Exception {
		/*
		 * BigDecimal retribuzione = (BigDecimal)m.getAttribute(Movimento.DB_RETRIBUZIONE); BigDecimal
		 * retribuzioneSanata = (BigDecimal)m.getAttribute(Movimento.DB_RETRIBUZIONE_SANATA); // prelievo le info sullo
		 * stato dell'operazione
		 */
		return getRetribuzioneMen(m.getSource());
	}

	public static BigDecimal getRetribuzioneMen(SourceBean m) throws Exception {
		Object o = m.getAttribute(MovimentoBean.DB_RETRIBUZIONE);
		BigDecimal retribuzione = null;
		if (o instanceof String)
			retribuzione = new BigDecimal((String) o);
		else
			retribuzione = (BigDecimal) o;

		if (m.getAttribute(MovimentoBean.DB_COD_TIPO_DICH) != null
				&& !m.getAttribute(MovimentoBean.DB_COD_TIPO_DICH).toString().equals("")) {

			Object objRetSan = m.getAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA);
			if (objRetSan == null || objRetSan.equals("")) {
				retribuzione = null;
			} else {
				if (objRetSan instanceof String)
					retribuzione = new BigDecimal((String) objRetSan);
				else
					retribuzione = (BigDecimal) objRetSan;
			}
		}
		return retribuzione;
	}

	public static int compare(BigDecimal r1, BigDecimal r2) throws Exception {
		int ret = r1.intValue() > r2.intValue() ? 1 : (r1.intValue() == r2.intValue() ? 0 : -1);
		return ret;
	}

	public static boolean equals(BigDecimal r1, BigDecimal r2) throws Exception {
		boolean ret = !(r1 != r2);
		if (!ret)
			ret = r1 != null && r2 != null && compare(r1, r2) == 0;
		return ret;
	}
}
