/*
 * Creato il 4-nov-04
 */
package it.eng.sil.module.movimenti;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * Oggetto che esegue il log dell'inserimento / validazione manuale di un movimento
 * <p/>
 * 
 * @author roccetti
 */
public class SingleResultLogger extends AbstractResultLogger {

	/**
	 * Costruttore
	 */
	public SingleResultLogger() {
		super((BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_"));
	}

	/**
	 * Imposta una risposta in uno slot di cui viene passato il progressivo
	 */
	public void logResultImpl(BigDecimal prgValidazioneMassiva, BigDecimal prgMovimento, BigDecimal prgMovimentoApp,
			SourceBean result) throws LogException {
		super.logResultImpl(null, prgMovimento, prgMovimentoApp, result);
	}

	/**
	 * Metodo per l'interruzione da parte dell'utente (non fa nulla)
	 */
	public void setStopUser() {
	}

	public void setStopUser(int contesto) {
	}

}
