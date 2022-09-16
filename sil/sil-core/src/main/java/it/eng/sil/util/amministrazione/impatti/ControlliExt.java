package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;

public class ControlliExt {
	private static Map movimenti = null;

	static {
		movimenti = new HashMap();
		movimenti.put(MovimentoBean.COD_ASSUNZIONE, new BigInteger(String.valueOf(MovimentoBean.ASSUNZIONE)));
		movimenti.put(MovimentoBean.COD_CESSAZIONE, new BigInteger(String.valueOf(MovimentoBean.CESSAZIONE)));
		movimenti.put(MovimentoBean.COD_PROROGA, new BigInteger(String.valueOf(MovimentoBean.PROROGA)));
		movimenti.put(MovimentoBean.COD_TRASFORMAZIONE, new BigInteger(String.valueOf(MovimentoBean.TRASFORMAZIONE)));
	}

	private static Map contratti = null;

	static {
		contratti = new HashMap();
		contratti.put(Contratto.COD_AUTONOMO, new BigInteger(String.valueOf(Contratto.AUTONOMO)));
		contratti.put(Contratto.COD_COCOCO, new BigInteger(String.valueOf(Contratto.COCOCO)));
		contratti.put(Contratto.COD_DIP_TD, new BigInteger(String.valueOf(Contratto.DIP_TD)));
		contratti.put(Contratto.COD_DIP_TI, new BigInteger(String.valueOf(Contratto.DIP_TI)));
	}

	public static final boolean TEST = false;

	/**
	 * metodo invocato quando di deve stabilire il limite reddito in presenza di movimenti con tipo contratto = lavoro
	 * autonomo.
	 * 
	 * @param dataRif
	 * @param movimenti
	 * @return true se esiste almeno un movimento con contratto = Contratto.DIP_TD oppure contratto = Contratto.DIP_TI
	 *         nell'anno della dataRif .
	 * @throws Exception
	 */
	public static boolean getMovimentiLavoroDipendente(String dataRif, Vector movimenti) throws Exception {
		boolean ris = false;
		int annoMovimentoRif = DateUtils.getAnno(dataRif);
		int annoInizioMov = 0;
		int annoFineMov = 0;
		int ret = -1;
		SourceBean movimento = null;
		String dataInizioMov = "";
		String dataFineMov = "";
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			movimento = (SourceBean) movimenti.get(i);
			dataInizioMov = movimento.containsAttribute(MovimentoBean.DB_DATA_INIZIO)
					? movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString()
					: "";
			if (DateUtils.compare(dataInizioMov, dataRif) > 0) {
				break;
			}
			dataFineMov = movimento.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
					? movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
					: "";
			annoInizioMov = DateUtils.getAnno(dataInizioMov);
			if (!dataFineMov.equals("")) {
				annoFineMov = DateUtils.getAnno(dataFineMov);
			}
			if ((annoInizioMov == annoMovimentoRif) || (dataFineMov.equals("") && annoInizioMov < annoMovimentoRif)
					|| (annoFineMov == annoMovimentoRif)) {
				ret = Contratto.getTipoContratto(movimento);
				if (ret == Contratto.DIP_TD || ret == Contratto.DIP_TI) {
					ris = true;
					break;
				}
			}
		}
		return ris;
	}

	public static boolean getMovimentiLavoroParaSubordinato(String dataRif, Vector movimenti) throws Exception {
		boolean ris = false;
		int annoMovimentoRif = DateUtils.getAnno(dataRif);
		int annoInizioMov = 0;
		int annoFineMov = 0;
		SourceBean movimento = null;
		String dataInizioMov = "";
		String dataFineMov = "";
		String flgSospensione = "";
		String codMonoTipoAss = "";
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			movimento = (SourceBean) movimenti.get(i);
			dataInizioMov = movimento.containsAttribute(MovimentoBean.DB_DATA_INIZIO)
					? movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString()
					: "";
			if (DateUtils.compare(dataInizioMov, dataRif) > 0) {
				break;
			}
			dataFineMov = movimento.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
					? movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
					: "";
			annoInizioMov = DateUtils.getAnno(dataInizioMov);
			if (!dataFineMov.equals("")) {
				annoFineMov = DateUtils.getAnno(dataFineMov);
			}
			if ((annoInizioMov == annoMovimentoRif) || (dataFineMov.equals("") && annoInizioMov < annoMovimentoRif)
					|| (annoFineMov == annoMovimentoRif)) {
				flgSospensione = movimento.containsAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014)
						? movimento.getAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014).toString()
						: "";
				codMonoTipoAss = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
						? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
						: "";
				if ((!codMonoTipoAss.equalsIgnoreCase(MovimentoBean.CODMONOTIPOAUTONOMO))
						&& (!flgSospensione.equalsIgnoreCase(MovimentoBean.SI))) {
					ris = true;
					break;
				}
			}
		}
		return ris;
	}

	public static boolean getMovimentiLavoroAutonomo(String dataRif, Vector movimenti) throws Exception {
		boolean ris = false;
		int annoMovimentoRif = DateUtils.getAnno(dataRif);
		int annoInizioMov = 0;
		int annoFineMov = 0;
		SourceBean movimento = null;
		String dataInizioMov = "";
		String dataFineMov = "";
		String codMonoTipoAss = "";
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			movimento = (SourceBean) movimenti.get(i);
			dataInizioMov = movimento.containsAttribute(MovimentoBean.DB_DATA_INIZIO)
					? movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString()
					: "";
			if (DateUtils.compare(dataInizioMov, dataRif) > 0) {
				break;
			}
			dataFineMov = movimento.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
					? movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
					: "";
			annoInizioMov = DateUtils.getAnno(dataInizioMov);
			if (!dataFineMov.equals("")) {
				annoFineMov = DateUtils.getAnno(dataFineMov);
			}
			if ((annoInizioMov == annoMovimentoRif) || (dataFineMov.equals("") && annoInizioMov < annoMovimentoRif)
					|| (annoFineMov == annoMovimentoRif)) {
				codMonoTipoAss = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
						? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
						: "";
				if (codMonoTipoAss.equalsIgnoreCase(MovimentoBean.CODMONOTIPOAUTONOMO)) {
					ris = true;
					break;
				}
			}
		}
		return ris;
	}

	/**
	 * Calcola il numero di giorni commerciali tra dataFine e dataInizio nell'arco dell' anno attuale Data Fine settata
	 * al 31/12/2004 ma poi numeroGiorniDiLavoroCommerciali calcola il numero di giorni commerciali.
	 * 
	 * @param dataInizio
	 * @param dataFine
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public static int getNumeroGiorniDiLavoro(String dataInizio, String dataFine, String dataRif) throws Exception {
		if (dataInizio == null) {
			return -1;
		}
		// se il movimento non è nell'anno della did (anno data inizio e anno data fine sono diversi
		// anno DID allora numero di giorni di lavoro = 0)
		if ((DateUtils.getAnno(dataRif) > DateUtils.getAnno(dataInizio)) && (dataFine != null && !dataFine.equals("")
				&& DateUtils.getAnno(dataRif) > DateUtils.getAnno(dataFine))) {
			return 0;
		}

		if (DateUtils.getAnno(dataRif) != DateUtils.getAnno(dataInizio))
			dataInizio = "01/01/" + DateUtils.getAnno(dataRif);
		/***************************/

		if (dataFine == null || dataFine.equals("") || DateUtils.getAnno(dataRif) != DateUtils.getAnno(dataFine))
			dataFine = "31/12/" + DateUtils.getAnno(dataRif);

		int giorniTotali = Controlli.numeroGiorniDiLavoroCommerciali(dataInizio, dataFine);
		return giorniTotali;
	}

	/**
	 * Calcola il numero di giorni commerciali nell'anno successivo a partire dall'inizio dell'anno successivo alla data
	 * dataRif fino alla dataFine.
	 * 
	 * @param dataFine
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public static int getNumeroGiorniDiLavoroAnnoSuccessivo(String dataFine, String dataRif) throws Exception {
		if (dataFine == null || dataFine.equals("") || DateUtils.getAnno(dataRif) + 1 < DateUtils.getAnno(dataFine))
			dataFine = "31/12/" + (DateUtils.getAnno(dataRif) + 1);

		int giorni = 0;
		String dataInizio = "01/01/" + (DateUtils.getAnno(dataRif) + 1);
		if (DateUtils.compare(dataInizio, dataFine) < 0)
			giorni = Controlli.numeroGiorniDiLavoroCommerciali(dataInizio, dataFine);
		if (giorni < 0)
			giorni = 0;
		return giorni;
	}

	/**
	 * Calcola il numero di giorni commerciali tra dataFine e dataRif Data Fine settata al
	 * 31/12/DateUtils.getAnno(dataRif) se in input è null Viene invocata in presenza di proroghe o trasformazioni a
	 * cavallo di 2 anni.
	 * 
	 * @param dataFine
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public static int getNumeroGiorniDiLavoroAnnoSuccProTra(String dataFine, String dataRif) throws Exception {
		int annoInizio = DateUtils.getAnno(dataRif);
		if (dataFine == null || dataFine.equals(""))
			dataFine = "31/12/" + annoInizio;
		int giorni = 0;
		String dataInizio = dataRif;
		int annoFine = DateUtils.getAnno(dataFine);
		if (annoFine > annoInizio) {
			dataFine = "31/12/" + annoInizio;
		}
		if (DateUtils.compare(dataInizio, dataFine) < 0)
			giorni = Controlli.numeroGiorniDiLavoroCommerciali(dataInizio, dataFine);
		if (giorni < 0)
			giorni = 0;
		return giorni;
	}

	/**
	 * crea dalla request il vettore dei movimenti a partire da quello selezionato nella pagina jsp
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static SourceBean[] ricreaMovimenti(SourceBean request) throws Exception {
		String rigaSelezionata = (String) request.getAttribute("checkSelezionato"); // 0..n
		String numeroMovimenti = (String) request.getAttribute("numeroMovimenti");
		BigDecimal cdnLavoratore = new BigDecimal((String) request.getAttribute("cdnLavoratore"));
		int i = Integer.parseInt(rigaSelezionata);
		int n = Integer.parseInt(numeroMovimenti);
		Vector v = new Vector(n - i + 1);
		SourceBean row = null;
		for (; i < n; i++) {
			row = new SourceBean("ROW");
			String dataInizio = (String) request.getAttribute("dataInizio_" + i);
			String dataFine = (String) request.getAttribute("dataFine_" + i);
			String numKloMov = (String) request.getAttribute("numKloMov_" + i);
			String retribuzione = (String) request.getAttribute("retribuzione_" + i);
			String prgMovimento = (String) request.getAttribute("prgMovimento_" + i);
			String prgStatoOccupaz = (String) request.getAttribute("prgStatoOccupaz_" + i);
			String codTipoMov = (String) request.getAttribute("CODTIPOMOV_" + i);
			String codTipoAss = (String) request.getAttribute("CODTIPOASS_" + i);
			String codMonoTempo = (String) request.getAttribute("codMonoTempo_" + i);

			if (dataInizio != null && dataInizio.length() > 0)
				row.setAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizio);
			if (dataFine != null && dataFine.length() > 0)
				row.setAttribute(MovimentoBean.DB_DATA_FINE, dataFine);
			if (numKloMov != null && numKloMov.length() > 0)
				row.setAttribute(MovimentoBean.DB_NUM_K_LOCK, new BigDecimal(numKloMov));
			if (retribuzione != null && retribuzione.length() > 0)
				row.setAttribute(MovimentoBean.DB_RETRIBUZIONE, new BigDecimal(retribuzione));
			if (prgMovimento != null && prgMovimento.length() > 0)
				row.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO, new BigDecimal(prgMovimento));
			if (prgStatoOccupaz != null && prgStatoOccupaz.length() > 0)
				row.setAttribute("prgStatoOccupaz", new BigDecimal(prgStatoOccupaz));
			if (codTipoMov != null && codTipoMov.length() > 0)
				row.setAttribute("codTipoMov", codTipoMov);
			if (codTipoAss != null && codTipoAss.length() > 0)
				row.setAttribute("CODTIPOASS", codTipoAss);
			if (codMonoTempo != null && codMonoTempo.length() > 0)
				row.setAttribute("codMonoTempo", codMonoTempo);
			if (cdnLavoratore != null)
				row.setAttribute("cdnLavoratore", cdnLavoratore);

			v.add(row);
		}
		return (SourceBean[]) v.toArray(new SourceBean[0]);
	}

	/**
	 * Richiesta della pagina jsp
	 * 
	 * @return
	 */
	public static int getMesiAnzianita() {
		return 0;
	}

	/**
	 * Richiesta della pagina jsp
	 * 
	 * @return
	 */
	public static String getTipoAnzianita() {
		return null;
	}

	/**
	 * Richiesta della pagina jsp
	 * 
	 * @return
	 */
	public static boolean donanInInserimemntoLavorativo() {
		return false;
	}

	private SourceBean[] movimentiLetti;
	private HashMap redditi = null;

	/**
	 * 
	 * @return
	 */
	public boolean redditoCalcolato() {
		return (redditi != null);
	}

	private StatoOccupazionaleBean statoOccupazionaleValido;

	/**
	 * Scorre i movimenti ed estrae quelli che sono attivi nella data dataRif
	 * 
	 * @param dataRif
	 * @param dataMov
	 * @param prgMov
	 * @return
	 * @throws Exception
	 */
	public Vector getMovimenti(String dataRif, String dataMov, BigDecimal prgMov) throws Exception {
		Vector v = new Vector(movimentiLetti.length);
		for (int i = 0; i < movimentiLetti.length; i++) {
			SourceBean movimento = movimentiLetti[i];
			BigDecimal prg = (BigDecimal) movimento.getAttribute("prgMovimento");
			String dataInizio = (String) movimento.getAttribute("datInizioMov");
			String dataFine = (String) movimento.getAttribute("datFineMov");

			if (((DateUtils.compare(dataInizio, dataRif) >= 0
					&& DateUtils.compare(dataInizio, "31/12/" + DateUtils.getAnno(dataRif)) <= 0)
					|| (DateUtils.compare(dataInizio, dataRif) < 0
							&& (dataFine == null || DateUtils.compare(dataFine, dataRif) > 0)))
					&& (DateUtils.compare(dataInizio, dataMov) <= 0)) {
				if (DateUtils.compare(dataInizio, dataMov) == 0 && prgMov.longValue() <= prgMov.longValue())
					continue;
				else
					v.add(movimento);
			}
		}
		return v;
	}

	/**
	 * Utilizzo questa classe per impostare alcuni dati che andrebbero letti dal db. Questo per il test con JUnit
	 */
	public static class DatiDiTest {
		public static Vector movimenti;
		public static SourceBean statoOccupazionale;

		public static void setMovimento(Vector m) {
			DatiDiTest.movimenti = m;
		}

		public static void setStatoOccupazionale(SourceBean row) {
			DatiDiTest.statoOccupazionale = row;
		}
	}

	/**
	 * 
	 * @param request
	 * @throws Exception
	 */
	public ControlliExt(SourceBean request) throws Exception {
		movimentiLetti = ricreaMovimenti(request);
	}

	/**
	 * 
	 * @param anno
	 * @return
	 * @throws Exception
	 */
	private long getReddito(String anno) throws Exception {
		String r = (String) redditi.get(anno);
		return Long.parseLong(r);
	}

	/**
	 * 
	 * @param anno
	 * @return
	 * @throws Exception
	 */
	private long getReddito(int anno) throws Exception {
		return getReddito(String.valueOf(anno));
	}

	/**
	 * Dalla request vengono ricostruiti i movimenti passati dal client. Nella HashMap ci sono i redditi accessibili
	 * tramite la chiave stringa anno valore stringa
	 * 
	 * @param request
	 * @param estrazioneReddito
	 * @throws Exception
	 */
	public ControlliExt(SourceBean request, boolean estrazioneReddito) throws Exception {
		if (estrazioneReddito) {
			this.redditi = estraiRedditi(request);
			String data = getAnnoBaseReddito(request);
			Vector v = DBLoad.getMovimentiDa(data, new BigDecimal((String) request.getAttribute("cdnLavoratore")));
			this.movimentiLetti = (SourceBean[]) v.toArray(new SourceBean[0]);
		} else
			this.movimentiLetti = ricreaMovimenti(request);
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static String getAnnoBaseReddito(SourceBean request) throws Exception {
		String data = (String) request.getAttribute("anniReddito");
		if (data.indexOf(",") > 0)
			data = data.substring(0, data.indexOf(","));
		data = "01/01/" + data;
		return data;
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public HashMap estraiRedditi(SourceBean request) throws Exception {
		String anni = (String) request.getAttribute("anniReddito");
		int ind = Integer.parseInt((String) request.getAttribute("checkSelezionato"));
		HashMap map = new HashMap();
		StringTokenizer st = new StringTokenizer(anni, ",");
		while (st.hasMoreTokens()) {
			String anno = (String) st.nextToken();
			map.put(anno, request.getAttribute("reddito_" + ind++));
			// tutto questo e' assolutamente prolisso
		}
		return map;
	}

	/**
	 * 
	 * @return
	 */
	public StatoOccupazionaleBean getStatoOccupazionaleValido() {
		return this.statoOccupazionaleValido;
	}

	/**
	 * 
	 * @return
	 */
	public SourceBean[] getMovimenti() {
		return this.movimentiLetti;
	}

	public static void main(String[] a) throws Exception {

		ControlliExt c = new ControlliExt(null);

		c.movimentiLetti = new SourceBean[3];
		c.movimentiLetti[0] = new SourceBean("TEST");
		c.movimentiLetti[0].setAttribute("datInizioMov", "06/02/2004");
		c.movimentiLetti[0].setAttribute("datFineMov", "06/09/2004");

		c.movimentiLetti[1] = new SourceBean("TEST");
		c.movimentiLetti[1].setAttribute("datInizioMov", "06/02/2004");
		c.movimentiLetti[1].setAttribute("datFineMov", "06/05/2004");

		c.movimentiLetti[2] = new SourceBean("TEST");
		c.movimentiLetti[2].setAttribute("datInizioMov", "06/06/2004");
		c.movimentiLetti[2].setAttribute("datFineMov", "06/09/2004");

		Vector v = c.getMovimenti("02/05/2004", "02/05/2004", new BigDecimal("1"));
		for (int i = 0; i < v.size(); i++) {
			SourceBean s = (SourceBean) v.get(i);
			System.out.println(s.getAttribute("datfineMov"));
		}
	}
}
