package com.engiweb.framework.dispatching.coordinator;

import com.engiweb.framework.dispatching.service.ServiceIFace;

/**
 * Questa interfaccia definisce i metodi che devono implementare tutte le classi che hanno il ruolo di coordinators.
 * 
 * @author Luigi Bellio
 */
public interface CoordinatorIFace extends ServiceIFace {
	/**
	 * Permette di recuperare il tipo di business . Esempio : "PAGE" o "ACTION".
	 * <p>
	 * 
	 * @return <code>String</code> la tipologia di business.
	 */
	String getBusinessType();

	/**
	 * Permette di recuperare il nome logico del business .
	 * <p>
	 * 
	 * @return <code>String</code> il nome logico del business.
	 */
	String getBusinessName();
} // public interface CoordinatorIFace extends ServiceIFace
