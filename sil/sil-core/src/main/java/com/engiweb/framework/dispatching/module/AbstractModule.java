package com.engiweb.framework.dispatching.module;

import java.io.Serializable;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.init.InitializerIFace;

/**
 * La classe <code>AbstractModule</code> &egrave; la superclasse di tutti quei moduli che non necessitano di operare
 * esclusivamente nel canale HTTP accedendo direttamente agli oggetti HttpServletRequest,
 * HttpServletResponse,ServletConfig.
 * <p>
 * Questa classe mette a disposizione i metodi per recuperare le instanze delle seguenti classi : <blockquote>
 * 
 * <pre>
 *  <code>
 * RequestContainer
 * </code>
 *  :Il contenitore di oggetti legati ai parametri della richiesta di un servizio.
 * <code>
 * ResponseContainer
 * </code>
 *  :Il contenitore di oggetti legati alla riposta ad una richiesta di un servizio.
 * <code>
 * EMFErrorHandler
 * </code>
 *  :Il gestore degli errori.
 *  &lt;/blockquote&gt;
 * </pre>
 * 
 * Nella configurazione XML del modulo è possibile scrivere uno stream XML(Config) che verrà passato all'istanza del
 * modulo dopo la sua creazione.
 * 
 * @author Luigi Bellio
 * @see com.engiweb.framework.base.RequestContainer
 * @see com.engiweb.framework.base.ResponseContainer
 * @see com.engiweb.framework.error.EMFErrorHandler
 */
public abstract class AbstractModule extends DefaultRequestContext
		implements InitializerIFace, ModuleIFace, Serializable {
	private SourceBean _config = null;
	private String _module = null;
	private String _page = null;
	private SourceBean _sharedData = null;

	public AbstractModule() {
		super();
		_config = null;
		_module = null;
		_page = null;
		_sharedData = null;
	} // public AbstractModule()

	/**
	 * Rende disponibile al modulo lo stream XML di configurazione. Questo è a carico dell' Application Framework e
	 * viene effettuato immediatamente dopo la creazione di un'istanza.
	 * <p>
	 * 
	 * @param name
	 *            nome del <code>SourceBean</code>
	 */
	public void init(SourceBean config) {
		_config = config;
	} // public void init(SourceBean config)

	/**
	 * Permette di recuperare lo stream XML di configurazione.
	 * 
	 * @return <code>SourceBean<code> un'istanza di SourceBean contenente lo stream XML di configurazione.
	 * @see SourceBean
	 */
	public SourceBean getConfig() {
		return _config;
	} // public SourceBean getConfig()

	/**
	 * Permette di recuperare il nome logico del modulo.
	 * 
	 * @return <code>String</code> il nome logico del modulo.
	 */
	public String getModule() {
		return _module;
	} // public String getModule()

	/**
	 * Rende disponbile al modulo il proprio nome logico . Questo è a carico dell' Application Framework.
	 * 
	 * @param module
	 *            nome logico del modulo.
	 */
	public void setModule(String module) {
		_module = module;
	} // public void setModule(String module)

	/**
	 * Permette di recuperare il nome logico della pagina.
	 * 
	 * @return <code>String</code> il nome logico della pagina.
	 * 
	 */
	public String getPage() {
		return _page;
	} // public String getPage()

	/**
	 * Rende disponbile al modulo il nome logico della pagina. Questo è a carico dell' Application Framework.
	 * 
	 * @param module
	 *            nome della pagina.
	 */
	public void setPage(String page) {
		_page = page;
	} // public void setMessage(String message)

	/**
	 * Permette di recuperare il contenitore condiviso da tutti i moduli di una stessa pagina. Un modulo ha la
	 * possibilità di depositare un oggetto in questo contenitore rendendolo disponibile agli altri moduli.
	 * 
	 * @return <code>SourceBean</code> il contenitore condiviso .
	 */
	public SourceBean getSharedData() {
		return _sharedData;
	} // public SourceBean getSharedData()

	/**
	 * Rende disponibile al modulo il contenitore condiviso da tutti i moduli della stessa pagina.
	 * 
	 * @param sharedData
	 *            nome del contenitore.
	 */
	public void setSharedData(SourceBean sharedData) {
		_sharedData = sharedData;
	} // public SourceBean getSharedData(SourceBean sharedData)
} // public abstract class AbstractModule extends DefaultRequestContext
	// implements InitializerIFace, ModuleIFace, Serializable
