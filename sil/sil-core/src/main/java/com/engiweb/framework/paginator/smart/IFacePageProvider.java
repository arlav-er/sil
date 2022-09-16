package com.engiweb.framework.paginator.smart;

import java.util.Collection;

import com.engiweb.framework.base.SourceBean;

public interface IFacePageProvider {
	public static final int LAST = -1;

	/**
	 * Notifica al componente di ricaricare i dati dei quali fa cacheing.
	 * <p>
	 * 
	 * @see AbstractPageProvider#init(SourceBean)
	 */
	void reload();

	/**
	 * Restituisce il <em>row provider</em> associato.
	 * 
	 * @return <code>IFaceRowProvider</code>
	 * @see IFaceRowProvider
	 */
	IFaceRowProvider getRowProvider();

	/**
	 * Imposta il <em>row provider</em> associato.
	 * 
	 * @param rowProvider
	 *            <code>IFaceRowProvider</code>
	 * @see IFaceRowProvider
	 */
	void setRowProvider(IFaceRowProvider rowProvider);

	/**
	 * Ritorna il numero di righe per pagina.
	 * 
	 * @return <code>int</code>
	 */
	int getPageSize();

	/**
	 * Imposta il numero di righe per pagina.
	 * 
	 * @param size
	 *            <code>int</code>
	 */
	void setPageSize(int size);

	/**
	 * Ritorna il numero di pagine previste.
	 * 
	 * @return <code>int</code>
	 */
	int pages();

	/**
	 * Ritorna una collezione con tutte le righe che formano la pagina di indice <em>page</em> richiesto.
	 * 
	 * @param page
	 *            <code>int</code>
	 * @return <code>Vector</code>
	 */
	Collection getPage(int page);

	int getCurrentPage();
} // public interface IFacePageProvider
