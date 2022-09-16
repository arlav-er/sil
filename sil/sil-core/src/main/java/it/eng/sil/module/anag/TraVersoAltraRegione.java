/*
 * Creato il 1-lug-04
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

/**
 * @author roccetti
 * 
 *         !!!! QUESTA CLASSE PER ORA NON è UTILIZZATA !!! Classe che effettua le operazioni di aggiornamento del DB
 *         locale per il trasferimento di un lavoratore verso un CPI al di fuori della regione.
 */
public class TraVersoAltraRegione {

	/**
	 * @see com.engiweb.framework.dispatching.module.AbstractModule#service(com.engiweb.framework.base.SourceBean,
	 *      com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {

		// TODO: Codice in transazione per il trasferimento verso altra regione

		// Controllo della coerenza dati con indice regionale (se è
		// contattabile)

		// Chiusura delle informazioni storiche del lavoratore (con Stored
		// Procedure)

		// Chiusura della DID se presente

		// Chiusura dell'elenco anagrafico

		// Chiusura del patto se presente
	}
}
