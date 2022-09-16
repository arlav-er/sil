package it.eng.sil.module.presel;

/**
 * Gestisce l'inserimento del comune.
 * 
 * Tutte l'elaborazione viene fatta nella super classe.
 * 
 * @author vaccari
 * @created November 13, 2003
 */
public class InsertTerritorioComune extends AbstractInsertDisponibilita {

	// In questo caso non c'è un field specifico
	protected String getCodeFieldName() {
		return "";
	}
}
