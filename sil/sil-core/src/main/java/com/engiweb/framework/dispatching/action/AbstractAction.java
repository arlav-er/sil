package com.engiweb.framework.dispatching.action;

import java.io.Serializable;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.init.InitializerIFace;

/**
 * La classe <code>AbstractAction</code> &egrave; la superclasse di tutte quelle actions che non necessitano di operare
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
 * <em>
 * EMFErrorHandler
 * </code>
 *  :Il gestore degli errori.
 *  &lt;/blockquote&gt;
 * </pre>
 * 
 * Nella configurazione XML dell'action è possibile scrivere uno stream XML(Config) che verrà passato all'istanza
 * dell'action dopo la sua creazione.
 *
 * @author Luigi Bellio
 * @see com.engiweb.framework.base.RequestContainer
 * @see com.engiweb.framework.base.ResponseContainer
 * @see com.engiweb.framework.error.EMFErrorHandler
 */
public abstract class AbstractAction extends DefaultRequestContext
		implements InitializerIFace, ActionIFace, Serializable {
	private SourceBean _config = null;
	private String _action = null;

	public AbstractAction() {
		super();
		_config = null;
		_action = null;
	} // public AbstractAction()

	/**
	 * Rende disponbile alla action lo stream XML di configurazione. Questo è a carico dell' Application Framework e
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
	 * <p>
	 * 
	 * @return <code>SourceBean</code> un'istanza contenente lo stream XML di configurazione.
	 * @see SourceBean
	 */
	public SourceBean getConfig() {
		return _config;
	} // public SourceBean getConfig()

	/**
	 * Permette di recuperare il nome logico dell'action.
	 * 
	 * @return <code>String</code> il nome logico dell'action.
	 */
	public String getAction() {
		return _action;
	} // public String getAction()

	/**
	 * Rende disponbile alla action il proprio nome logico . Questo è a carico dell' Application Framework.
	 * 
	 * @param action
	 *            nome logico della action.
	 */
	public void setAction(String action) {
		_action = action;
	} // public void setAction(String action)
} // public abstract class AbstractAction extends DefaultRequestContext
	// implements InitializerIFace, ActionIFace, Serializable
