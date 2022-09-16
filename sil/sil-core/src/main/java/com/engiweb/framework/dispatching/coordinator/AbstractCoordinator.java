package com.engiweb.framework.dispatching.coordinator;

import java.io.Serializable;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;

/**
 * La classe <code>AbstractCoordinator</code> &egrave; la superclasse di tutti i coordinatori. Ogni coordinator &egrave;
 * in grado di gestire particolari richieste di servizi;due coordinators offerti dal framework sono ModuleCoordinator(in
 * grado di gestire la modalità a PAGE) e ActionCoordinator(in grado di gestire la modalità ad ACTION). Questa classe
 * mette a disposizione i metodi per recuperare le instanze delle seguenti classi : <blockquote>
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
 * @author Luigi Bellio
 * @see com.engiweb.framework.base.RequestContainer
 * @see com.engiweb.framework.base.ResponseContainer
 * @see com.engiweb.framework.error.EMFErrorHandler
 */
public abstract class AbstractCoordinator extends DefaultRequestContext implements CoordinatorIFace, Serializable {
	private String _businessType = null;
	private String _businessName = null;

	public AbstractCoordinator(String businessType, String businessName) {
		_businessType = businessType;
		_businessName = businessName;
	} // public AbstractCoordinator(String businessType, String businessName)

	/**
	 * Il metodo service viene invocato dopo la creazione di un'istanza del coordinator.
	 * 
	 * @param serviceRequest
	 *            nome del <code>SourceBean</code>
	 * @param serviceResponse
	 *            nome del <code>SourceBean</code>
	 * @exception Exception
	 *                viene lanciata se l'esecuzione del servizio viene bloccata da una anomalia non gestibile.
	 * @see com.engiweb.framework.base.SourceBean
	 * @author Luigi Bellio
	 */
	public abstract void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception;

	public String getBusinessType() {
		return _businessType;
	} // public String getBusinessType()

	public String getBusinessName() {
		return _businessName;
	} // public String getBusinessName()
} // public abstract class AbstractCoordinator extends DefaultRequestContext
	// implements CoordinatorIFace, Serializable
