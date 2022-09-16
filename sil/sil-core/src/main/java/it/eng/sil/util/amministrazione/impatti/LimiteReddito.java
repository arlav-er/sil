package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;

/**
 * Classe che mantiene i dati del limite del reddito di un certo anno (quello attuale)
 */
public class LimiteReddito {
	/**
	 * Proprieta' che permette di implementare il singleton pattern
	 */
	private static LimiteReddito singleton;
	private double quotaBaseFissa;
	private double limiteCM;
	private double limiteLD;
	private double limiteLA;
	/**
	 * il modificatore "volatile" e' necessario per prevenire letture della variabile non corrette da un thread nel
	 * mentre un altro thread sta' midificandone il valore
	 */
	volatile private int anno;
	//
	volatile private String date;
	//
	public static final int DIPENDENTE = 0;
	public static final int AUTONOMO = 1;
	public static final int CM = 2;

	/**
	 * Calcola il limite a seconda del tipo contratto (alla fine fa il riproporzionamento del limite)
	 */
	public static double calcola(int tipo, String dataInizio) throws Exception {

		check();
		double limite = 0, ret = 0;
		switch (tipo) {
		case LimiteReddito.AUTONOMO:
			limite = singleton.limiteLA;
			break;
		case LimiteReddito.DIPENDENTE:
			limite = singleton.limiteLD;
			break;
		case LimiteReddito.CM:
			limite = singleton.limiteCM;
			break;
		}
		double quotaVar = limite - singleton.quotaBaseFissa;
		if (quotaVar < 0) {
			quotaVar = 0;
		}
		ret = singleton.quotaBaseFissa + ((quotaVar) * getPercentualeGG(dataInizio));
		return ret;
	}

	/**
	 * 
	 * @param tipo
	 * @return
	 * @throws Exception
	 */
	public static double get(int tipo) throws Exception {
		check();
		double limite = 0;
		switch (tipo) {
		case LimiteReddito.AUTONOMO:
			limite = singleton.limiteLA;
			break;
		case LimiteReddito.DIPENDENTE:
			limite = singleton.limiteLD;
			break;
		case LimiteReddito.CM:
			limite = singleton.limiteCM;
			break;
		}
		return limite;
	}

	/**
	 * 
	 * @throws Exception
	 */
	private static void check() throws Exception {
		String date = DateUtils.getNow();
		if (singleton == null || !(singleton.date != null && !singleton.date.equals(date)))
			load(date); // leggo i dati dal db
	}

	/**
	 * legge i dati del limite reddito dal db ed istanzia un oggetto di questo stessa classe (singleton pattern)
	 * 
	 * @param date
	 * @throws Exception
	 */
	synchronized private static void load(String date) throws Exception {
		// non e' il massimo ma comunque dovrebbe funzionare: se il secondo
		// thread che ha chiamato load() vi accede
		// e il precedente thread lo ha gia' eseguito avremo lo stato della
		// classe gia' aggiornato quindi e' inutile
		// andare a rileggere le info dal db.
		if (singleton != null && singleton.date != null && singleton.date.equals(date))
			return;
		singleton = new LimiteReddito();
		SourceBean row = null;
		if (DatiDiTest.TEST) {
			row = getRowDiTest();
		} else {
			String stm = "select * from ts_limite_reddito where numanno = " + DateUtils.getAnno(date);
			row = DBLoad.getLimiteReddito();
		}
		// a questo punto debbo leggere i dati dal db......
		singleton.limiteCM = ((BigDecimal) row.getAttribute("NUMLIMITEREDDITOCM")).doubleValue();
		singleton.limiteLA = ((BigDecimal) row.getAttribute("NUMLIMITEREDDITOLA")).doubleValue();
		singleton.limiteLD = ((BigDecimal) row.getAttribute("NUMLIMITEREDDITOLD")).doubleValue();
		singleton.quotaBaseFissa = ((BigDecimal) row.getAttribute("NUMQUOTABASEFISSA")).doubleValue();
		// conviene valorizzare l' anno per ultimo
		singleton.anno = ((BigDecimal) row.getAttribute("NUMANNO")).intValue();
		singleton.date = date;

	}

	/**
	 * 
	 * @param dataInizio
	 * @return
	 * @throws Exception
	 */
	public static double getPercentualeGG(String dataInizio) throws Exception {
		check();
		int gg = Controlli.getNumeroGiorniDiLavoro(dataInizio, "31/12/" + singleton.anno);
		gg = (gg >= 365) ? 365 : gg;
		return ((double) (gg)) / 365;
	}

	/**
	 * Serve semplicemente per testare la classe
	 * 
	 * @return
	 */
	private static SourceBean getRowDiTest() {
		SourceBean row = null;
		try {
			// rowTest = new SourceBean ("TEST");
			row = new SourceBean("ROW");
			// rowTest.setAttribute(row);
			row.setAttribute("NUMLIMITEREDDITOCM", new BigDecimal("10000"));
			row.setAttribute("NUMLIMITEREDDITOLA", new BigDecimal("4500"));
			row.setAttribute("NUMLIMITEREDDITOLD", new BigDecimal("7500"));
			row.setAttribute("NUMQUOTABASEFISSA", new BigDecimal("3000"));
			row.setAttribute("NUMANNO", new BigDecimal("2004"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return row;
	}
}