package it.eng.sil.module.presel;

/**
 * Gestisce l'inserimento di uno o più orari in una o più mansioni.
 * 
 * Tutte l'elaborazione viene fatta nella super classe.
 * 
 * @author vaccari
 * @created November 13, 2003
 */
public class InsertOrarioInMansione extends AbstractInsertDisponibilita {

	protected String getCodeFieldName() {

		return "CODORARIO";
	}
}
