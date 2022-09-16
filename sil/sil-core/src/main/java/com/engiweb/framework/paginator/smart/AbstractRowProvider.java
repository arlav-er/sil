package com.engiweb.framework.paginator.smart;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.init.InitializerIFace;

/**
 * La classe <code>AbstractRowProvider</code> fornisce i metodi per la gestione di una sorgente di righe per la
 * paginazione.
 * <p>
 * 
 * @version 1.0, 15/03/2003
 */
public abstract class AbstractRowProvider extends DefaultRequestContext implements InitializerIFace, IFaceRowProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractRowProvider.class.getName());
	private SourceBean _config = null;
	private boolean _toBeReloaded = false;
	private boolean _isOpen = false;
	private int _currentRow = 0;
	private int _rows = 0;

	/**
	 * Costruisce un <code>AbstractRowProvider</code>. Questo costruttore &egrave; vuoto ed ogni azione di
	 * inizializzazione &egrave; demandata al metodo <code>init(SourceBean config)</code> in
	 * <code>AbstractRowProvider</code>.
	 * <p>
	 * 
	 * @see AbstractRowProvider#init(SourceBean)
	 */
	public AbstractRowProvider() {
		super();
		_config = null;
		_toBeReloaded = false;
		_isOpen = false;
		_currentRow = 0;
		_rows = 0;
	} // public AbstractRowProvider()

	/**
	 * Questo metodo viene chiamato dal framework per inizializzare il componente. L'argomento passato &egrave; il
	 * <code>SourceBean</code> costruito a partire dal XML contenuto nell'eventuale sezione <em>config</em> relativa
	 * allo specifico <em>row provider</em> nel file /WEB-INF/conf/row_providers.xml. La sezione <em>config</em>
	 * &egrave; accessibile anche via <blockquote>
	 * 
	 * <pre>
	 * ConfigSingleton configure = ConfigSingleton.getInstance();
	 * SourceBean config = (SourceBean) configure.getAttribute(&quot;PAGE_PROVIDERS.PAGE_PROVIDER.CONFIG&quot;);
	 * </pre>
	 * 
	 * </blockquote>
	 * <p>
	 * 
	 * @param config
	 *            <code>SourceBean</code> la configurazione del componente.
	 */
	public void init(SourceBean config) {
		_config = config;
	} // public void init(Object config)

	public SourceBean getConfig() {
		return _config;
	} // public SourceBean getConfig()

	/**
	 * Questo metodo serve per notificare al componente di ricaricare i dati relativi alla lista.
	 */
	public void reload() {
		toBeReloaded(true);
	} // public void reload()

	public void toBeReloaded(boolean toReload) {
		_toBeReloaded = toReload;
	} // public void toBeReloaded(boolean toReload)

	public boolean hasToBeReloaded() {
		return _toBeReloaded;
	} // public boolean hasToBeReloaded()

	/**
	 * Questo metodo serve per notificare al componente l'inizio delle operazioni di lettura.
	 */
	public void open() {
		toOpen(true);
	} // public void open()

	/**
	 * Questo metodo serve per notificare al componente la fine delle operazioni di lettura.
	 */
	public void close() {
		toOpen(false);
	} // public void close()

	public void toOpen(boolean open) {
		_isOpen = open;
	} // public void toOpen(boolean open)

	public boolean isOpen() {
		return _isOpen;
	} // public boolean isOpen()

	/**
	 * Questo metodo serve per posizionare il <em>cursore</em> di lettura alla riga individuata da <em>row</em>.
	 * <p>
	 * 
	 * @param row
	 *            <code>int</code> il numero della riga su cui posizionarsi.
	 */
	public void absolute(int row) {
		_logger.debug("AbstractRowProvider::absolute: metodo non implementato !");

	} // public void absolute(int row)

	/**
	 * Ritorna un <code>Object</code> che rappresenta la riga individuata da <em>row</em>.
	 * <p>
	 * 
	 * @return <code>Object</code> la riga individuata da <em>row</em>.
	 * @param row
	 *            <code>int</code> il numero della riga da ritornare.
	 */
	public Object getRow(int row) {
		_logger.debug("AbstractRowProvider::getRow: metodo non implementato !");

		return null;
	} // public Object getRow(int row)

	/**
	 * Ritorna un <code>Object</code> che rappresenta la riga successiva all'ultima recuperata o individuata con il
	 * comando <code>absolute(int)
	 * </code>.
	 * <p>
	 * 
	 * @return <code>Object</code> la riga individuata da <em>row</em>.
	 * @param row
	 *            <code>int</code> il numero della riga da ritornare.
	 */
	public Object getNextRow() {
		_logger.debug("AbstractRowProvider::getNextRow: metodo non implementato !");

		return null;
	} // public Object getNextRow()

	public int getCurrentRow() {
		return _currentRow;
	} // public int getCurrentRow()

	public void setCurrentRow(int currentRow) {
		_currentRow = currentRow;
	} // public void setCurrentRow(int currentRow)

	/**
	 * Ritorna il numero di righe fornite dal <em>row provider</em>.
	 * <p>
	 * 
	 * @return <code>int</code>
	 */
	public int rows() {
		return _rows;
	} // public int rows()

	public void setRows(int rows) {
		_rows = rows;
	} // public void setRows(int rows)
} // public abstract class AbstractRowProvider implements IFacePageProvider
