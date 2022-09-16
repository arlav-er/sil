package com.engiweb.framework.paginator.smart;

import com.engiweb.framework.base.SourceBean;

/**
 * @author Administrator
 * 
 *         To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates.
 *         To enable and disable the creation of type comments go to Window>Preferences>Java>Code Generation.
 */
public interface IFaceRowHandler extends IFaceRowProvider {
	/**
	 * Esegue l'inserimento di una riga nella lista. Ritorna l'esito dell'operazione.
	 * <p>
	 * 
	 * @return <code>boolean</code> esito dell'operazione
	 */
	boolean insertRow();

	/**
	 * Esegue l'operazione di update dei dati di dettaglio relativi all'ultima riga recuperata o individuata con il
	 * comando <code>absolute(int).
	 */
	boolean updateRow();

	/**
	 * Esegue l'operazione di select dei dati di dettaglio relativi all'ultima riga recuperata o individuata con il
	 * comando <code>absolute(int).
	 * <p>
	 * &#64;return <code>SourceBean</code> che contiene i dati richiesti.
	 */
	SourceBean selectRow();

	/**
	 * Cancella l'ultima riga recuperata o individuata con il comando <code>absolute(int).
	 * <p>
	 * &#64;return <code>boolean</code> esito dell'operazione
	 */
	boolean deleteRow();
} // public interface IFaceRowHandler extends IFaceRowProvider
