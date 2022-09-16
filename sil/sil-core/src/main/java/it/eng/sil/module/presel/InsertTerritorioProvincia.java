package it.eng.sil.module.presel;

/**
 * Gestisce l'inserimento della provincia.
 * 
 * Tutte l'elaborazione viene fatta nella super classe.
 * 
 * @author vaccari
 * @created November 13, 2003
 */
public class InsertTerritorioProvincia extends AbstractInsertDisponibilita {

	// In questo caso non c'è un field specifico
	protected String getCodeFieldName() {
		return "";
	}
}
