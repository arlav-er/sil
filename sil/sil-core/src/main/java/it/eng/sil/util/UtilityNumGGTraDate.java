package it.eng.sil.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;

import it.eng.sil.Values;

/**
 * Classe per il calcolo del numero di giorni, lavorativi, che intercorrono tra due date. (Effettua il calcolo dei
 * festivi solo considerando le domeniche)
 */
public class UtilityNumGGTraDate {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UtilityNumGGTraDate.class.getName());

	private static final String FORMATO_DATA = "dd/MM/yyyy";
	private static SimpleDateFormat df = new SimpleDateFormat(FORMATO_DATA);

	public UtilityNumGGTraDate() {
	}

	/**
	 * Effettua il calcolo dei giorniche intercorrono tra due date considerando o meno anche le domeniche a seconda del
	 * CODTIPOAZIENDA. Se codTipoAss ha come codmonotipo=A: le domeniche non devono essere escluse dal calcolo.
	 * (DataComunicazione (data1) - datainiziomov (data2))
	 */
	public static int getDateDiffNL(String data1, String data2, String codTipoAss) {
		int diff = 0;
		int nl = 0;
		if ((data1.length() == 10) && (data2.length() == 10)) {
			diff = (getNumRitardo(data1, data2));

			try {
				if (!codTipoAss.equals("AGR")) {
					nl = getNumGiorniNL(data1, data2, diff);
					diff -= nl;
				}
			} catch (Exception e) {
			}
			if (diff < 0)
				diff = 0;
		}
		return diff;
	}

	/**
	 * Metodo per il calcolo dei giorni di riatardo tra due date.
	 */
	public static int getNumRitardo(String data1, String data2) {
		GregorianCalendar gd1 = null;
		GregorianCalendar gd2 = null;
		long dayDiff = 0;

		gd1 = trasformaInGregorian(data1);
		gd2 = trasformaInGregorian(data2);
		// Num gg che intercorrono tra due date
		dayDiff = (gd1.getTime().getTime() - gd2.getTime().getTime()) / (1000 * 60 * 60 * 24);

		return (int) dayDiff;
	}

	/**
	 * Metodo per il calcolo delle domeniche che intercorrono tra due date. bassa; prende inoltre in input il numero di
	 * giorni che intercorrono tra le due date.
	 */
	public static int getNumGiorniNL(String dat1, String dat2, int diff) {
		int count = 0;
		int i = 0;
		int giorno = 0;
		GregorianCalendar tmpMin = null;
		GregorianCalendar tmpMax = null;
		GregorianCalendar gd1 = trasformaInGregorian(dat1);
		GregorianCalendar gd2 = trasformaInGregorian(dat2);

		if ((gd1 != null) && (gd2 != null)) {
			tmpMin = (GregorianCalendar) gd1.clone();
			tmpMax = (GregorianCalendar) gd2.clone();
			if (gd1.before(gd2)) {
				tmpMin = (GregorianCalendar) gd1.clone();
				tmpMax = (GregorianCalendar) gd2.clone();
			} else if (gd1.after(gd2)) {
				tmpMin = (GregorianCalendar) gd2.clone();
				tmpMax = (GregorianCalendar) gd1.clone();
			}

			while (!tmpMin.after(tmpMax)) {
				giorno = tmpMin.get(Calendar.DATE);
				if ((tmpMin.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) && (i != 0)) {
					count += 1;
					break;
				}
				// Incremento di un giorno alla volta tmpMin
				tmpMin.set(Calendar.DATE, (giorno + 1));
				i++;
			}
			if (count > 0) {
				while (!tmpMin.after(tmpMax)) {
					giorno = tmpMin.get(Calendar.DATE);
					// Incremento di una settimana alla volta
					tmpMin.set(Calendar.DATE, (giorno + 7));
					if (!tmpMin.after(tmpMax)) {
						count += 1;
					}
				}
			}
		}
		return count;
	}

	/**
	 * Restituisce un data come un GregorianCalendar
	 */
	public static GregorianCalendar trasformaInGregorian(String data) {
		int giorno, mese, anno;
		GregorianCalendar gd1 = null;

		if (data.length() == 10) {
			giorno = Integer.parseInt(data.substring(0, 2));
			mese = Integer.parseInt(data.substring(3, 5));
			anno = Integer.parseInt(data.substring(6, 10));
			gd1 = new GregorianCalendar(anno, mese - 1, giorno);
		}

		return gd1;
	}

	public static String sommaGiorni(String data, int num) {
		GregorianCalendar f = trasformaInGregorian(data);
		f.set(Calendar.DATE, (f.get(Calendar.DATE) + num));

		return df.format(f.getTime());
	}

	public static String sottraiGiorni(String data, int num) {
		GregorianCalendar f = trasformaInGregorian(data);
		f.set(Calendar.DATE, (f.get(Calendar.DATE) - num));

		return df.format(f.getTime());
	}

	public static boolean isAzAgricola(String codTipoAss) throws Exception {
		boolean ritVal = false;
		DataConnection conn = null;
		DBAccess dbaccess = null;
		SQLCommand selectCommand = null;
		DataResult dr = null;

		try {
			dbaccess = new DBAccess();
			// Apertura connessione e in izio transazione...
			conn = dbaccess.getConnection(Values.DB_SIL_DATI);

			DataRow[] datarows = null;
			Object[] obj = null;
			obj = new Object[1];
			obj[0] = codTipoAss;

			datarows = dbaccess.selectQuery(obj, "RECUPERO_COD_MONOTIPOASS", conn);
			DataRow row = datarows[0];
			DataField df = row.getColumn("CODMONOTIPO");
			if (df != null) {
				String cod = df.getStringValue();
				if ((cod != null) && cod.equalsIgnoreCase("A"))
					ritVal = true;
			}
		} catch (Exception emf) {
			_logger.debug("UtilityNumGGTraDate"
					+ "::insert() Errore durante la fase di controllo di se si tratta di un'azienda agricola o meno"
					+ emf.toString());

			throw emf;
		}
		// In conclusione in ogni caso rilascio la connessione
		finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(conn, null, null);
			return ritVal;
		}
	}

}