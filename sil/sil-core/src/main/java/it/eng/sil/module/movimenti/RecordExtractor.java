package it.eng.sil.module.movimenti;

import java.util.Iterator;

/*
 * Interfaccia per l'estrazione dei record dalla sorgente. Si tratta di un Iterator che ritorna
 * ad ogni chiamata di next un oggetto che implementa l'interfaccia Map.
 * */
public interface RecordExtractor extends Iterator {
	/**
	 * Numero di record da processare
	 */
	public int getNumRecord();
}