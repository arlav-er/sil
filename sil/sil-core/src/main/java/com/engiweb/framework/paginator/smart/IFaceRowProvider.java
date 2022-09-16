package com.engiweb.framework.paginator.smart;

import com.engiweb.framework.base.SourceBean;

public interface IFaceRowProvider {
	public static final int LAST = -1;

	/**
	 * Notifica al componente di ricaricare i dati dei quali fa cacheing.
	 * <p>
	 * 
	 * @see AbstractRowtProvider#init(SourceBean)
	 */
	void reload();

	/**
	 * Questo metodo serve per notificare al componente l'inizio delle operazioni di lettura.
	 */
	void open();

	/**
	 * Questo metodo serve per posizionare il <em>cursore</em> di lettura alla riga individuata da <em>row</em>.
	 * <p>
	 * 
	 * @param row
	 *            <code>int</code> il numero della riga su cui posizionarsi.
	 */
	void absolute(int row);

	/**
	 * Ritorna un <code>Object</code> che rappresenta la riga individuata da <em>row</em>.
	 * <p>
	 * 
	 * @return <code>Object</code> la riga individuata da <em>row</em>.
	 * @param row
	 *            <code>int</code> il numero della riga da ritornare.
	 */
	Object getRow(int row);

	/**
	 * Ritorna un <code>Object</code> che rappresenta la riga successiva all'ultima recuperata o individuata con il
	 * comando <code>absolute(int)
	 * </code>.
	 * <p>
	 * 
	 * @return <code>Object</code> la riga individuata da <em>row</em>.
	 * @param row
	 *            <code>int</code> il numero della riga da ritornare.
	 */
	Object getNextRow();

	int getCurrentRow();

	/**
	 * Questo metodo serve per notificare al componente la fine delle operazioni di lettura.
	 */
	void close();

	/**
	 * Ritorna il numero di righe fornite dal <em>row provider</em>.
	 * <p>
	 * 
	 * @return <code>int</code>
	 */
	int rows();
} // public interface IFaceRowProvider
