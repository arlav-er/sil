/**
 * Creato il 29/09/2004
 */
package it.eng.sil.util.amministrazione.impatti;

import com.engiweb.framework.base.SourceBean;

/**
 * @author landi Interfaccia che contiene le proprietà e i metodi che un evento amministrativo (DID e MOVIMENTO) deve
 *         garantire. Ogni singolo evento avrà associata una classe contenitore che implementa questa interfaccia e una
 *         classe management per la sua gestione.
 */
public interface EventoAmministrativo {
	public static final String DATA_NORMATIVA_DEFAULT = "30/01/2003";
	public static final int ANNO_DATA_NORMATIVA_DEFAULT = 2003;

	/**
	 * Metodo che dovrà restituire il tipo di evento amministrativo.
	 * 
	 * @return
	 */
	public int getTipoEventoAmministrativo();

	/**
	 * Metodo che restituisce la data di inizio dell'evento registrato nel sistema.
	 * 
	 * @return
	 */

	/**
	 * Metodo che restituisce lo stato occupazionale associato all'evento amministrativo.
	 */
	public StatoOccupazionaleBean getStatoOccupazionale();

	public String getDataInizio();

	/**
	 * Metodo che restituisce la data fine dell'evento registrato nel sistema. Se l'evento amministrativo è ancora
	 * valido, allora il metodo restituirà null.
	 * 
	 * @return
	 */
	public String getDataFine();

	/**
	 * Metodo restituisce true se l'evento corrispondente è stato protocollato e false altrimenti.
	 * 
	 * @return
	 */
	public boolean isProtocollato();

	/**
	 * Metodo restituisce true se la data inizio dell'evento è successiva alla data odierna e false altrimenti.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isDataFutura() throws Exception;

	/**
	 * Metodo restituisce true se la data inizio dell'evento è precedente alla data di inizio validità del decreto 150
	 * (30/01/2003 oppure la data presente in configurazione).
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isPrecNormativa(String dataRifNormativa) throws Exception;

	/**
	 * Restituisce la descrizione
	 * 
	 * @return String che rappresenta la descrizione dell'evento amministrativo (es. Did - Movimento)
	 * @author Togna Cosimo
	 */
	public String getDescrizione();

	/**
	 * Cerca un evento amministrativo in una lista di eventi presa in input
	 * 
	 * @param prgEvento
	 *            progressivo dell'evento da ricercare
	 * @param eventi
	 *            eventi dai quali cercare
	 * @return un SourceBean che rappresenta l'evento trovato
	 * @throws ControlliException
	 *             lanciata nel caso in cui il movimento da cercare non è presente nella lista
	 * @author Togna Cosimo
	 */
	public SourceBean cercaEventoAmministrativo(Object prgEvento, java.util.Collection eventi)
			throws ControlliException;

	public void setStatoOccupazionale(StatoOccupazionaleBean newStatoOcc) throws Exception;

	public static final int DID = 0;
	public static final int CHIUSURA_DID = 1;
	public static final int AVVIAMENTO = 2;
	public static final int TRASFORMAZIONE = 3;
	public static final int PROROGA = 4;
	public static final int CESSAZIONE = 5;
	public static final int PATTO = 6;
	public static final int MOBILITA = 7;
	public static final int CHIUSURA_MOBILITA = 8;

}
