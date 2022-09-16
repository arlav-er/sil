package com.engiweb.framework.paginator.smart;

import java.util.Collection;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.engiweb.framework.init.InitializerIFace;

/**
 * La classe <code>AbstractPageProvider</code> fornisce i metodi per la gestione di una pagina.
 * 
 * @version 1.0, 15/03/2003
 */
public abstract class AbstractPageProvider extends DefaultRequestContext
		implements InitializerIFace, IFacePageProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractPageProvider.class.getName());
	private SourceBean _config = null;
	private IFaceRowProvider _rowProvider = null;
	private boolean _toBeReloaded = false;
	private int _size = 10;
	private int _currentPage = 0;
	private int _pages = 0;

	/**
	 * Costruisce un <code>AbstractPageProvider</code>. Ogni azione di inizializzazione &egrave; demandata al metodo
	 * <code>init(SourceBean
	 * config)
	 * </code>.
	 * <p>
	 * 
	 * @see AbstractPageProvider#init(SourceBean)
	 */
	public AbstractPageProvider() {
		super();
		_config = null;
		_rowProvider = null;
		_toBeReloaded = false;
		_size = 10;
		_currentPage = 0;
		_pages = 0;
	} // public AbstractPageProvider()

	public void setRequestContext(RequestContextIFace requestContext) {
		super.setRequestContext(requestContext);
		if (_rowProvider != null) {
			RequestContextIFace rowProviderRequestContext = (RequestContextIFace) _rowProvider;
			rowProviderRequestContext.setRequestContext(requestContext);
		} // if (_pageProvider != null)
	} // public void setRequestContext(RequestContextIFace requestContext)

	/**
	 * Questo metodo viene chiamato dal framework per inizializzare il componente. L'argomento passato &egrave; il
	 * <code>SourceBean</code> costruito a partire dal XML contenuto nell'eventuale sezione <em>config</em> relativa
	 * allo specifico <em>page provider</em> nel file /WEB-INF/conf/page_providers.xml. La sezione <em>config</em>
	 * &egrave; accessibile anche via <blockquote>
	 * 
	 * <pre>
	 * ConfigSingleton configure = ConfigSingleton.getInstance();
	 * SourceBean config = (SourceBean) configure.getAttribute(&quot;PAGE_PROVIDERS.PAGE_PROVIDER.CONFIG&quot;);
	 * </pre>
	 * 
	 * </blockquote>
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
	 * Questo metodo serve per notificare al componente di ricaricare i dati relativi alla pagina. La sua
	 * implementazione di default prevede soltanto la chiamata alla funzione <code>reload()</code> del <em>row
	 * provider</em> associato. Un <em>page provider</em> che svolga attivit&agrave; di cacheing dovrebbe fare
	 * l'override di questo metodo per aggiornare in questo metodo la propria cache.
	 */
	public void reload() {
		_logger.debug("AbstractPageProvider::reload: invocato");

		if (_rowProvider == null)
			_logger.debug("AbstractListProvider::reload: _rowProvider nullo");

		else
			_rowProvider.reload();
		toBeReloaded(true);
	} // public void reload()

	public void toBeReloaded(boolean toReload) {
		_toBeReloaded = toReload;
	} // public void toBeReloaded(boolean toReload)

	public boolean hasToBeReloaded() {
		return _toBeReloaded;
	} // public boolean hasToBeReloaded()

	/**
	 * Restituisce il <em>row provider</em> associato.
	 * 
	 * @return <code>IFaceRowProvider</code>
	 * @see IFaceRowProvider
	 */
	public IFaceRowProvider getRowProvider() {
		return _rowProvider;
	} // public IFaceRowProvider getRowProvider()

	/**
	 * Imposta il <em>row provider</em> associato.
	 * 
	 * @param rowProvider
	 *            <code>IFaceRowProvider</code>
	 * @see IFaceRowProvider
	 */
	public void setRowProvider(IFaceRowProvider rowProvider) {
		_logger.debug("AbstractPageProvider::setRowProvider: invocato");

		_rowProvider = rowProvider;
	} // public void setRowProvider(IFaceRowProvider rowProvider)

	/**
	 * Ritorna il numero di righe per pagina.
	 * 
	 * @return <code>int</code>
	 */
	public int getPageSize() {
		return _size;
	} // public int getPageSize()

	/**
	 * Imposta il numero di righe per pagina.
	 * 
	 * @param size
	 *            <code>int</code>
	 */
	public void setPageSize(int size) {
		_logger.debug("AbstractPageProvider::setPageSize: size [" + size + "]");

		_size = size;
	} // public void setPageSize(int size)

	/**
	 * Ritorna il numero di pagine previste in ragione del numero di righe fornite dal <em>row provider</em>.
	 * 
	 * @return <code>int</code>
	 */
	public int pages() {
		return _pages;
	} // public int pages()

	public void setPages() {
		if (_rowProvider == null) {
			_logger.debug("AbstractPageProvider::setPages: _rowProvider nullo");

			_pages = 0;
			return;
		} // if (_rowProvider == null)
		int rows = _rowProvider.rows();
		if (rows == 0) {
			_pages = 0;
			return;
		} // if (rows == 0)
		_pages = (rows - 1) / _size + 1;
	} // public void setPages()

	/**
	 * Ritorna un vettore con tutte le righe che formano la pagina di indice <em>page</em> richiesto.
	 * 
	 * @param page
	 *            <code>int</code>
	 * @return <code>Collection</code>
	 */
	public abstract Collection getPage(int page);

	public int getCurrentPage() {
		return _currentPage;
	} // public int getCurrentPage()

	public void setCurrentPage(int currentPage) {
		_currentPage = currentPage;
	} // public void setCurrentPage(int currentPage)
} // public abstract class AbstractPageProvider extends DefaultRequestContext
	// implements InitializerIFace, IFacePageProvider
