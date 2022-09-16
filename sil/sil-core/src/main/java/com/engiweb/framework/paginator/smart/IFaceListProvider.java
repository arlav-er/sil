package com.engiweb.framework.paginator.smart;

import com.engiweb.framework.base.SourceBean;

/**
 * L'interfaccia <code>IFaceListProvider</code> definisce i metodi per la gestione di una lista paginata.
 * 
 * @version 1.0, 15/03/2003
 */
public interface IFaceListProvider {
	public static final int LAST = -1;

	/**
	 * Notifica al componente di ricaricare i dati dei quali fa cacheing.
	 * <p>
	 * 
	 * @see AbstractListProvider#init(SourceBean)
	 */
	void reload();

	/**
	 * Restituisce il <em>page provider</em> associato.
	 * 
	 * @return <code>IFacePageProvider</code>
	 * @see IFacePageProvider
	 */
	IFacePageProvider getPageProvider();

	/**
	 * Imposta il <em>page provider</em> associato.
	 * 
	 * @param pageProvider
	 *            <code>IFacePageProvider</code>
	 * @see IFacePageProvider
	 */
	void setPageProvider(IFacePageProvider pageProvider);

	/**
	 * Aggiunge un oggetto nella sezione STATIC_DATA associata a ciascuna pagina pubblicata.
	 * 
	 * @param data
	 *            <code>Object</code>
	 */
	void addStaticData(Object data);

	/**
	 * Aggiunge un oggetto nella sezione DYNAMIC_DATA associata a ciascuna pagina pubblicata.
	 * 
	 * @param data
	 *            <code>Object</code>
	 */
	void addDynamicData(Object data);

	/**
	 * Svuota la sezione DYNAMIC_DATA associata a ciascuna pagina pubblicata.
	 */
	void clearDynamicData();

	/**
	 * Ritorna un <code>Object</code> che rappresenta la pagina di indice <em>page</em> richiesto.
	 * 
	 * @param page
	 *            <code>int</code>
	 * @return <code>Object</code>
	 */
	Object getListPage(int i);

	/**
	 * Ritorna il numero di pagina corrente.
	 * 
	 * @return <code>int</code>
	 */
	int getCurrentPage();
} // public interface IFaceListProvider
