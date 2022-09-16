package com.engiweb.framework.dispatching.action;

import com.engiweb.framework.dispatching.service.ServiceIFace;

/**
 * La classe <code>ActionIFace</code> &egrave; l'interfaccia che viene implementata da tutti gli oggetti di tipo action.
 */
public interface ActionIFace extends ServiceIFace {
	/**
	 * Permette di recuperare il nome logico dell'action.
	 * 
	 * @return <code>String<code> il nome logico dell'action.
	 */
	String getAction();

	/**
	 * Rende disponbile alla action il proprio nome logico . Questo è a carico dell' Application Framework.
	 * 
	 * @param action
	 *            nome logico della action.
	 */
	void setAction(String action);
} // public interface ActionIFace extends ServiceIFace
