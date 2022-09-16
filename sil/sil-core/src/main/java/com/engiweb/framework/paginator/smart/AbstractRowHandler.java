package com.engiweb.framework.paginator.smart;

import com.engiweb.framework.base.SourceBean;

/**
 * La classe <code>AbstractRowHandler</code> estende la classe <code>AbstractRowProvider</code> ed aggiunge i metodi per
 * eseguire istruzioni di <em>insert</em> e <em>delete</em> di una riga ele istruzioni di <em>select</em> e
 * <em>update</em> dei dati di dettaglio di una riga.
 * <p>
 * 
 * @version 1.0, 15/03/2003
 */
public abstract class AbstractRowHandler extends AbstractRowProvider implements IFaceRowHandler {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractRowHandler.class.getName());

	/**
	 * Costruisce un <code>AbstractRowProvider</code>. Questo costruttore &egrave; vuoto ed ogni azione di
	 * inizializzazione &egrave; demandata al metodo <code>init(SourceBean config)</code> in
	 * <code>AbstractRowProvider</code>.
	 * <p>
	 * 
	 * @see AbstractRowHandler#init(SourceBean)
	 */
	public AbstractRowHandler() {
		super();
	} // public AbstractRowHandler()

	/**
	 * Esegue l'inserimento di una riga nella lista. Ritorna l'esito dell'operazione.
	 * <p>
	 * 
	 * @return <code>boolean</code> esito dell'operazione
	 */
	public boolean insertRow() {
		_logger.debug("AbstractRowProvider::insertRow: metodo non implementato !");

		return false;
	} // public boolean insertRow()

	/**
	 * Esegue l'operazione di update dei dati di dettaglio relativi all'ultima riga recuperata o individuata con il
	 * comando <code>absolute(int).
	 */
	public boolean updateRow() {
		_logger.debug("AbstractRowProvider::updateRow: metodo non implementato !");

		return false;
	} // public boolean updateRow()

	/**
	 * Esegue l'operazione di select dei dati di dettaglio relativi all'ultima riga recuperata o individuata con il
	 * comando <code>absolute(int).
	 * <p>
	 * &#64;return <code>SourceBean</code> che contiene i dati richiesti.
	 */
	public SourceBean selectRow() {
		_logger.debug("AbstractRowProvider::selectRow: metodo non implementato !");

		return null;
	} // public SourceBean selectRow()

	/**
	 * Cancella l'ultima riga recuperata o individuata con il comando <code>absolute(int).
	 * <p>
	 * &#64;return <code>boolean</code> esito dell'operazione
	 */
	public boolean deleteRow() {
		_logger.debug("AbstractRowProvider::deleteRow: metodo non implementato !");

		return false;
	} // public boolean deleteRow()
} // public abstract class AbstractRowHandler extends AbstractRowProvider
	// implements IFaceRowHandler
