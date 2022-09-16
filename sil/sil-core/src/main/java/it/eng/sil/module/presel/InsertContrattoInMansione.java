package it.eng.sil.module.presel;

/**
 * Gestisce l'inserimento di uno o più contratti in una o più mansioni.
 * 
 * Tutte l'elaborazione viene fatta nella super classe.
 * 
 * @author vaccari
 * @created November 13, 2003
 */
public class InsertContrattoInMansione extends AbstractInsertDisponibilita {

	protected String getCodeFieldName() {

		return "CODCONTRATTO";
	}
}
