package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * <p>
 * 
 * Classe che incorpora la logica di gestione del Patto per l'integrazione all'interno dei moduli.
 * </p>
 * <p>
 * 
 * Note di implementazione<br>
 * Il modulo che utilizza il patto deve
 * <ol>
 * <li>Istanziare un TransactionQueryExecutor</li>
 * <li>Istanziare un PattoManager e passargli, se richiesto, il TransactionQueryExecutor</li>
 * <li>Richiamare dal modulo e dal PattoManager i metodi di AbstractSimpleModule da cui il modulo è derivato</li>
 * </ol>
 * </p>
 * 
 * @author Andrea Savino & Corrado Vaccari
 * @created December 2, 2003
 */
public class PattoManager {

	/**
	 * Description of the Field
	 */
	public final static String STM_DELETE = "DELETE_LAV_PATTO_SCELTA";
	/**
	 * Description of the Field
	 */
	public final static String STM_INSERT = "INSERT_LAV_PATTO_SCELTA";

	private AbstractSimpleModule module;
	private TransactionQueryExecutor transQueryExec;

	/**
	 * Costruttore per usare il patto con le transazioni.
	 * 
	 * @param module
	 *            Modulo a cui il patto è legato
	 * @param transQueryExec
	 *            Gestore transazioni da utilizzare
	 */
	public PattoManager(AbstractSimpleModule module, TransactionQueryExecutor transQueryExec) {
		this.module = module;
		this.transQueryExec = transQueryExec;

		this.module.enableTransactions(transQueryExec);
	}

	/**
	 * Costruttore per usare il patto senza le transazioni.
	 * 
	 * @param module
	 *            Modulo a cui il patto è legato
	 */
	public PattoManager(AbstractSimpleModule module) {
		this.module = module;
	}

	/**
	 * Esegue la logica del patto.
	 * 
	 * @param request
	 *            Description of the Parameter
	 * @param response
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public boolean execute(SourceBean request, SourceBean response) {
		// ...
		boolean res = false;

		// SourceBean ris=null;
		try {
			if (isInsert(request)) {
				this.module.setSectionQueryInsert(STM_INSERT);
				res = this.module.doInsert(request, response);
			} else {
				this.module.setSectionQueryDelete(STM_DELETE);
				res = this.module.doDelete(request, response);
			}
		} catch (Exception e) {
			res = false;
		}

		return res;
	}

	/**
	 * Determina se si deve eseguire una operazione con gestione della transazione oppure se l' operazione col db e' non
	 * transazionale.
	 * 
	 * @param request
	 *            Richiesta coma arriva al modulo client
	 * @return true se si deve eseguire una operazione con gestione della transazione, altrimenti false
	 */
	public static boolean withPatto(SourceBean request) {

		String op = (String) request.getAttribute("operazioneColPatto");
		if ((op == null) || ((Integer.parseInt(op) < -1) && (Integer.parseInt(op) > 1))) {

			return false;
		}

		return (!op.equals("0"));
	}

	/**
	 * Determina se si deve inserire un record di associazione nella tabella am_lav_patto_scelta, false se si deve
	 * cancellare il record di associazione nella tabella am_lav_patto_scelta.
	 * 
	 * @param request
	 *            Richiesta coma arriva al modulo client
	 * @return Risultato del controllo
	 */
	private boolean isInsert(SourceBean request) {
		String op = (String) request.getAttribute("operazioneColPatto");

		if (op.equals("1")) {
			return false;
		} else if (op.equals("-1")) {
			return true;
		} else {
			throw new IllegalArgumentException(
					"impossibile scegliere se bisogna cancellare od inseriere una associazione col patto");
		}
	}

	public static boolean esisteVoucher(TransactionQueryExecutor transExec, Object prgLavPattoscelta) throws Exception {
		boolean exists = false;
		Object params[] = new Object[1];
		params[0] = prgLavPattoscelta;
		SourceBean row = (SourceBean) transExec.executeQuery("GET_Voucher_Azione_Patto", params, "SELECT");
		row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
		Integer countVoucher = new Integer(row.getAttribute("countVoucher").toString());
		if (countVoucher.intValue() > 0) {
			exists = true;
		}
		return exists;
	}
}
