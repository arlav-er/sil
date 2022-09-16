package it.eng.sil.batch.timer;

/**
 * Classe per la gestione del timer abilitato/non abilitato rispetto alla proprietà batch.enabled
 * 
 * @author OMenghini
 *
 */
public abstract class EnabledTimerBatch {

	/**
	 * Metodo che verifica l'abilitazione del timer per avvio batch rispetto alla proprieta di sistema
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		boolean ret = false;

		String batchProperty = "batch.enabled";

		String value = System.getProperty(batchProperty);
		ret = Boolean.valueOf(value);

		return ret;
	}

}
