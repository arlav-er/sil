package com.engiweb.framework.paginator.smart;

import java.util.ArrayList;
import java.util.Collection;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.engiweb.framework.init.InitializerIFace;

/**
 * La classe <code>AbstractListProvider</code> fornisce i metodi per la gestione di una lista.
 * 
 * @version 1.0, 15/03/2003
 */
public abstract class AbstractListProvider extends DefaultRequestContext
		implements InitializerIFace, IFaceListProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractListProvider.class.getName());
	private SourceBean _config = null;
	private IFacePageProvider _pageProvider = null;
	private boolean _toBeReloaded = false;
	private int _currentPage = 0;
	private ArrayList _staticData = null;
	private ArrayList _dynamicData = null;

	/**
	 * Costruisce un <code>AbstractListProvider</code>. Questo costruttore &egrave; vuoto ed ogni azione di
	 * inizializzazione &egrave; demandata al metodo <code>init(SourceBean  config)</code>.
	 * <p>
	 * 
	 * @see AbstractListProvider#init(SourceBean)
	 */
	public AbstractListProvider() {
		super();
		_config = null;
		_pageProvider = null;
		_currentPage = 0;
		_staticData = new ArrayList();
		_dynamicData = new ArrayList();
	} // public AbstractListProvider()

	public void setRequestContext(RequestContextIFace requestContext) {
		super.setRequestContext(requestContext);
		if (_pageProvider != null) {
			RequestContextIFace pageProviderRequestContext = (RequestContextIFace) _pageProvider;
			pageProviderRequestContext.setRequestContext(requestContext);
		} // if (_pageProvider != null)
	} // public void setRequestContext(RequestContextIFace requestContext)

	/**
	 * Questo metodo viene chiamato dal framework per inizializzare il componente. L'argomento passato &egrave; il
	 * <code>SourceBean</code> costruito a partire dal XML contenuto nell'eventuale sezione <em>config</em> relativa
	 * allo specifico <em>list provider</em> nel file /WEB-INF/conf/list_providers.xml. La sezione <em>config</em>
	 * &egrave; accessibile anche via <blockquote>
	 * 
	 * <pre>
	 * ConfigSingleton configure = ConfigSingleton.getInstance();
	 * SourceBean config = (SourceBean) configure.getAttribute(&quot;LIST_PROVIDERS.LIST_PROVIDER.CONFIG&quot;);
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
	 * Questo metodo serve per notificare al componente di ricaricare i dati relativi alla lista. La sua implementazione
	 * di default prevede soltanto la chiamata alla funzione <code>reload()</code> del <em>page provider</em> associato.
	 * Un <em>list provider</em> che svolga attivit&agrave; di cacheing dovrebbe fare l'override di questo metodo per
	 * aggiornare in questo metodo la propria cache.
	 */
	public void reload() {
		_logger.debug("AbstractListProvider::reload: invocato");

		if (_pageProvider == null)
			_logger.debug("AbstractListProvider::reload: _pageProvider nullo");

		else
			_pageProvider.reload();
		toBeReloaded(true);
	} // public void reload()

	public void toBeReloaded(boolean toReload) {
		_toBeReloaded = toReload;
	} // public void toBeReloaded(boolean toReload)

	public boolean hasToBeReloaded() {
		return _toBeReloaded;
	} // public boolean hasToBeReloaded()

	/**
	 * Restituisce il <em>page provider</em> associato.
	 * 
	 * @return <code>IFacePageProvider</code>
	 * @see IFacePageProvider
	 */
	public IFacePageProvider getPageProvider() {
		return _pageProvider;
	} // public IFacePageProvider getPageProvider()

	/**
	 * Imposta il <em>page provider</em> associato.
	 * 
	 * @param pageProvider
	 *            <code>IFacePageProvider</code>
	 * @see IFacePageProvider
	 */
	public void setPageProvider(IFacePageProvider pageProvider) {
		_logger.debug("AbstractListProvider::setPageProvider: invocato");

		_pageProvider = pageProvider;
	} // public void setPageProvider(IFacePageProvider pageProvider)

	/**
	 * Aggiunge un oggetto nella sezione STATIC_DATA associata a ciascuna pagina pubblicata.
	 * 
	 * @param data
	 *            <code>Object</code>
	 */
	public void addStaticData(Object data) {
		_logger.debug("AbstractListProvider::addStaticData: invocato");

		_staticData.add(data);
	} // public void addStaticData(Object data)

	/**
	 * Ritorna un vettore degli oggetti destinati alla pubblicazione nella sezione STATIC_DATA.
	 * 
	 * @return <code>Collection</code>
	 */
	public Collection getStaticData() {
		return _staticData;
	} // public Collection getStaticData()

	/**
	 * Aggiunge un oggetto nella sezione DYNAMIC_DATA associata a ciascuna pagina pubblicata.
	 * 
	 * @param data
	 *            <code>Object</code>
	 */
	public void addDynamicData(Object data) {
		_logger.debug("AbstractListProvider::addDynamicData: invocato");

		_dynamicData.add(data);
	} // public void addDynamicData(Object data)

	/**
	 * Ritorna un vettore degli oggetti destinati alla pubblicazione nella sezione DYNAMIC_DATA.
	 * 
	 * @return <code>Collection</code>
	 */
	public Collection getDynamicData() {
		return _dynamicData;
	} // public Collection getDynamicData()

	/**
	 * Svuota la sezione DYNAMIC_DATA associata a ciascuna pagina pubblicata.
	 */
	public void clearDynamicData() {
		_logger.debug("AbstractListProvider::clearDynamicData: invocato");

		_dynamicData.clear();
	} // public void clearDynamicData()

	/**
	 * Ritorna un <code>Object</code> che rappresenta la pagina di indice <em>page</em> richiesto.
	 * 
	 * @param page
	 *            <code>int</code>
	 * @return <code>Object</code>
	 */
	public abstract Object getListPage(int page);

	/**
	 * Ritorna il numero di pagina corrente.
	 * 
	 * @return <code>int</code>
	 */
	public int getCurrentPage() {
		return _currentPage;
	} // public int getCurrentPage()

	public void setCurrentPage(int currentPage) {
		_currentPage = currentPage;
	} // public void setCurrentPage(int currentPage)
} // public abstract class AbstractListProvider implements InitializerIFace,
	// IFaceListProvider
