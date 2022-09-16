package com.engiweb.framework.dispatching.coordinator;

import com.engiweb.framework.dispatching.service.RequestContextIFace;

/**
 * L'interfaccia <code>DispatcherIFace</code> identifica i servizi che un dispatcher deve erogare. Il dispatcher deve
 * implementare nel metodo <code>acceptsURL(RequestContextIFace requestContext)</code> la logica con la quale stabilisce
 * se è in grado di gestire una richiesta di servizio. Nel metodo
 * <code>getCoordinator(RequestContextIFace requestContext)</code> il dispatcher deve ritornare l'istanza di
 * <code>CoordinatorIFace</code> che gestirà la richiesta.
 * 
 * @author Luigi Bellio
 * @see CoordinatorIFace
 */
public interface DispatcherIFace {
	/**
	 * Ritorna un <code>boolean</code> che avrà il seguente significato: <em>true</em> se il dispatcher accetta di
	 * gestire la richiesta <em>false</em> se il dispatcher non accetta di gestire la richiesta
	 * 
	 * @return <code>boolean</code> che indica se il dispatcher accetta di gestire la richiesta.
	 */
	boolean acceptsURL(RequestContextIFace requestContext);

	String getBusinessType(RequestContextIFace requestContext);

	String getBusinessName(RequestContextIFace requestContext);

	/**
	 * Ritorna un <code>CoordinatorIFace</code> che avrà il compito di gestire la richiesta.
	 * 
	 * @param requestContext
	 *            il contesto del servizio
	 * @return <code>CoordinatorIFace</code> il gestore del servizio.
	 */
	CoordinatorIFace getCoordinator(RequestContextIFace requestContext);
} // public interface DispatcherIFace
