package com.engiweb.framework.base;

/**
 * Definisce l'interfaccia che dev'essere implementata da tutti gli oggetti che vogliono clonare il proprio stato.
 * 
 * @author Luigi Bellio
 */
public interface CloneableObject {
	/**
	 * Questo metodo dev'essere implementato inserendo la logica di clonazione dello stato dell'oggetto
	 * 
	 * @author Luigi Bellio
	 * @return <code>CloneableObject<code> una copia dell'oggetto.
	 */
	public CloneableObject cloneObject();
} // public interface CloneableObject
