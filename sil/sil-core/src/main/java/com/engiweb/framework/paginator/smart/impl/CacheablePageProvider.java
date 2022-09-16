package com.engiweb.framework.paginator.smart.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.paginator.smart.AbstractListProvider;
import com.engiweb.framework.paginator.smart.AbstractPageProvider;
import com.engiweb.framework.paginator.smart.IFacePageProvider;
import com.engiweb.framework.paginator.smart.IFaceRowProvider;

/**
 * La classe <code>CacheablePageProvider</code> &egrave; l'implementazione di default di
 * <code>AbstractPageProvider</code>. Questa implementazione memorizza in anticipo le pagine per accelerare la
 * navigazione della lista.
 * 
 * @version 1.0, 15/03/2003
 * @see AbstractListProvider
 */
public class CacheablePageProvider extends AbstractPageProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CacheablePageProvider.class.getName());
	// prima pagina compresa, iniziando da 1
	private int _startPage = 0;
	// ultima pagina compresa, iniziando da 1
	private int _endPage = 0;
	private ArrayList _pages = null;
	private int _sidePages = 2;

	/**
	 * Costruisce un <code>CacheablePageProvider</code>. Ogni azione di inizializzazione &egrave; demandata al metodo
	 * <code>init(SourceBean
	 * config)
	 * </code>.
	 * <p>
	 * 
	 * @see AbstractPageProvider#init(SourceBean)
	 */
	public CacheablePageProvider() {
		super();
		_startPage = 0;
		_endPage = 0;
		_pages = null;
		_sidePages = 2;
	} // public CacheablePageProvider()

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
	 * </blockquote> I parametri attesi nella sezione config sono il numero di riche per pagina (ROWS) ed il numero di
	 * pagine da mantenere in cache (CACHESIZE). Di seguito &egrave; riportato un esempio di file di configurazione
	 * <blockquote>
	 * 
	 * <pre>
	 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;utf-8&quot;?&gt;
	 * &lt;PAGE_PROVIDERS&gt;
	 * &lt;PAGE_PROVIDER
	 * name=&quot;CacheablePageProvider&quot;
	 * class=&quot;com.engiweb.framework.paginator.CacheablePageProvider&quot;&gt;
	 * &lt;CONFIG rows=&quot;5&quot; cacheSize=&quot;5&quot;/&gt;
	 * &lt;/PAGE_PROVIDER&gt;
	 * &lt;/PAGE_PROVIDERS&gt;
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param config
	 *            <code>SourceBean</code> la configurazione del componente.
	 */
	public void init(SourceBean config) {
		_logger.debug("CacheablePageProvider::init: invocato");

		super.init(config);
		int pageSize = 10;
		try {
			String pageSizeStr = (String) config.getAttribute("PAGE_SIZE");
			pageSize = Integer.parseInt(pageSizeStr);
		} // try
		catch (NumberFormatException nfe) {
			_logger.warn("CacheablePageProvider::init: PAGE_SIZE non valido, default [" + pageSize + "]");

		} // catch (NumberFormatException nfe)
		setPageSize(pageSize);
		int sidePages = 2;
		try {
			String sidePagesStr = (String) config.getAttribute("SIDE_PAGES");
			sidePages = Integer.parseInt(sidePagesStr);
		} // try
		catch (NumberFormatException nfe) {
			_logger.warn("CacheablePageProvider::init: SIDE_PAGES non valido, default [" + sidePages + "]");

		} // catch (NumberFormatException nfe)
		_sidePages = sidePages;
	} // public void init(Object config)

	/**
	 * Ritorna un vettore con tutte le righe che formano la pagina di indice <em>page</em> richiesto. Se la pagina
	 * richiesta non &egrave; in cache provvede a caricare tanto la pagina quanto le pagine a questa vicine in modo da
	 * anticipare le richieste dell'utente.
	 * 
	 * @param page
	 *            <code>int</code>
	 * @return <code>Collection</code>
	 */
	public Collection getPage(int page) {
		if ((page < _startPage) || (page > _endPage) || hasToBeReloaded() || (page == IFacePageProvider.LAST))
			retrievingPages(page);
		if (_startPage == 0) {
			setCurrentPage(0);
			return new ArrayList();
		} // if (_startPage == 0)
		if ((page > _endPage) || (page == IFacePageProvider.LAST))
			page = _endPage;
		setCurrentPage(page);
		_logger.debug("CacheablePageProvider::getPage: page [" + page + "]");

		return (Collection) _pages.get(page - _startPage);
	} // public Collection getPage(int page)

	/**
	 * Carica nella cache la pagina richiesta e quelle ad essa prossime fino a riempire la cache. Questo metodo viene
	 * invocato da <code>getPage(int page)</code>
	 * 
	 * @param page
	 *            <code>int</code>
	 */
	protected void retrievingPages(int page) {
		_logger.debug("CacheablePageProvider::retrivingPages: invocato");

		toBeReloaded(false);
		IFaceRowProvider rowProvider = getRowProvider();
		if (rowProvider == null) {
			_logger.warn("CacheablePageProvider::retrivingPages: row provider nullo !");

			return;
		} // if (rowProvider == null)
		try {
			rowProvider.open();
			setPages();
			int rows = rowProvider.rows();
			if (rows == 0) {
				_startPage = 0;
				_endPage = 0;
				return;
			} // if (rows == 0)
			int lastPage = (rows - 1) / getPageSize() + 1;
			if ((page > lastPage) || (page == IFacePageProvider.LAST)) {
				page = lastPage - _sidePages;
				if (page < 1)
					page = 1;
			} // if ((page > lastPage) || (page == IFacePageProvider.LAST))
			_startPage = page - _sidePages;
			if (_startPage < 1)
				_startPage = 1;
			// prima riga compresa, iniziando da 1
			int startRow = (_startPage - 1) * getPageSize() + 1;
			// ultima riga compresa, iniziando da 1
			int endRow = (page + _sidePages) * getPageSize();
			if (endRow > rows)
				endRow = rows;
			_endPage = (endRow - 1) / getPageSize() + 1;
			ArrayList currentPage = new ArrayList();
			rowProvider.absolute(startRow);
			_pages = new ArrayList();
			for (int i = startRow; i <= endRow; i++) {
				Object row = rowProvider.getNextRow();
				if (((i - startRow) % getPageSize()) == 0)
					if (currentPage.size() != 0) {
						_pages.add(currentPage);
						currentPage = new ArrayList();
					} // if (currentPage.size() != 0)
				currentPage.add(row);
			} // for (int i = startRow; i <= endRow; i++)
			if (currentPage.size() != 0)
				_pages.add(currentPage);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "CacheablePageProvider::retrivingPages:", ex);

		} // catch (Exception ex)
		finally {
			rowProvider.close();
		} // finally
	} // protected void retrievingPages(int page)
} // public class CacheablePageProvider extends AbstractPageProvider
