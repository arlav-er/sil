package com.engiweb.framework.dispatching.module;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.ServiceIFace;

/**
 * La classe <code>ModuleIFace</code> &egrave; l'interfaccia che viene implementata da tutti gli oggetti di tipo module.
 */
public interface ModuleIFace extends ServiceIFace {
	/**
	 * Ritorna il nome del modulo.
	 * 
	 * @return il nome del modulo.
	 */
	String getModule();

	/**
	 * Questo metodo permette di impostare il nome del modulo.
	 * 
	 * @param il
	 *            nome del modulo.
	 */
	void setModule(String module);

	/**
	 * Ritorna il nome della pagina.
	 * 
	 * @return il nome della pagina.
	 */
	String getPage();

	/**
	 * Questo metodo permette di impostare il nome della pagina.
	 * 
	 * @param il
	 *            nome della pagina.
	 */
	void setPage(String page);

	/**
	 * Questo metodo permette di impostare il contenitore condiviso da tutti i moduli della stessa pagina.Questo
	 * contenitore viene ricreato ad ogni richiesta di servizio.
	 * 
	 * @param sharedData
	 *            il contenitore condiviso.
	 */
	void setSharedData(SourceBean sharedData);
} // public interface ModuleIFace extends ServiceIFace
