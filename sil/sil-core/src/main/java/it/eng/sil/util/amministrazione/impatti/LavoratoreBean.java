/*
 * Creato il 30-giu-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util.amministrazione.impatti;

import java.util.List;
import java.util.Vector;

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author Togna
 * @author Landi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

public class LavoratoreBean {

	private List movimenti;
	private List did;
	private Object cdnLavoratore;
	private List patti;
	private List mobilita;
	private List statiOccupazionali;
	private List iscrizioniCM;
	private TransactionQueryExecutor transactionQueryExecutor;

	/**
	 * @param cdnLavoratore
	 * @param transactionQueryExecutor
	 */
	public LavoratoreBean(Object cdnLavoratore, TransactionQueryExecutor transactionQueryExecutor) throws Exception {
		setCdnLavoratore(cdnLavoratore);
		setTransactionQueryExecutor(transactionQueryExecutor);
		List movimenti = MovimentoBean.getMovimenti(getCdnLavoratore(), getTransactionQueryExecutor());
		// da togliere e inserire i controlli nella nuova query
		// GET_MOVIMENTI_LAVORATORE_VALIDI
		movimenti = Controlli.togliMovNonProtocollati(new Vector(movimenti));
		movimenti = Controlli.togliMovimentoInDataFutura(new Vector(movimenti));
		setMovimenti(movimenti);
		setDid(DidBean.getDid(getCdnLavoratore(), getTransactionQueryExecutor()));
		setStatiOccupazionali(
				StatoOccupazionaleBean.getStatiOccupazionali(getCdnLavoratore(), getTransactionQueryExecutor()));
		setPatti(PattoBean.getPatti(getCdnLavoratore(), getTransactionQueryExecutor()));
	}

	/**
	 * 
	 * @param transactionQueryExecutor
	 * @throws Exception
	 */
	public LavoratoreBean(String contesto, Object cdnLavoratore, TransactionQueryExecutor transactionQueryExecutor)
			throws Exception {
		setTransactionQueryExecutor(transactionQueryExecutor);
		setCdnLavoratore(cdnLavoratore);
		if (contesto.equals("CM")) {
			setCM(CmBean.getIscrizioniCM(getCdnLavoratore(), getTransactionQueryExecutor()));
		}
	}

	/**
	 * @return
	 */
	public List getCM() {
		return iscrizioniCM;
	}

	/**
	 * @return
	 */
	public List getDid() {
		return did;
	}

	/**
	 * @return
	 */
	public List getMobilita() {
		return mobilita;
	}

	/**
	 * @return
	 */
	public List getMovimenti() {
		return movimenti;
	}

	/**
	 * @return
	 */
	public List getPatti() {
		return patti;
	}

	/**
	 * @return
	 */
	public List getStatiOccupazionali() {
		return statiOccupazionali;
	}

	/**
	 * @param object
	 */
	private void setCdnLavoratore(Object object) {
		cdnLavoratore = object;
	}

	/**
	 * @param list
	 */
	private void setCM(List list) {
		iscrizioniCM = list;
	}

	/**
	 * 
	 * @param list
	 */
	private void setDid(List list) {
		did = list;
	}

	/**
	 * @param list
	 */
	public void setMobilita(List list) {
		mobilita = list;
	}

	/**
	 * @param list
	 */
	private void setMovimenti(List list) {
		movimenti = list;
	}

	/**
	 * @param list
	 */
	private void setPatti(List list) {
		patti = list;
	}

	/**
	 * @param list
	 */
	private void setStatiOccupazionali(List list) {
		statiOccupazionali = list;
	}

	/**
	 * @param executor
	 */
	private void setTransactionQueryExecutor(TransactionQueryExecutor executor) {
		transactionQueryExecutor = executor;
	}

	/**
	 * @return
	 */
	public Object getCdnLavoratore() {
		return cdnLavoratore;
	}

	/**
	 * @return
	 */
	private TransactionQueryExecutor getTransactionQueryExecutor() {
		return transactionQueryExecutor;
	}

}
