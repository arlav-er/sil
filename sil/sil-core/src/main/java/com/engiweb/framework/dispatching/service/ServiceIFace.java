package com.engiweb.framework.dispatching.service;

import com.engiweb.framework.base.SourceBean;

/**
 * Questa interfaccia viene implementata da tutti gli oggetti di business .
 */
public interface ServiceIFace {
	/**
	 * Questo metodo viene invocato dall'application framework per eseguire la logica di business implemetata
	 * dell'oggetto stesso.
	 * 
	 * @param serviceRequest
	 *            il <code>SourceBean</code> contenente i parametri della richiesta.
	 * @param serviceResponse
	 *            il <code>SourceBean</code> che dovrà essere popolato con gli oggetti legati alla risposta.
	 */
	void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception;
} // public interface ServiceIFace
