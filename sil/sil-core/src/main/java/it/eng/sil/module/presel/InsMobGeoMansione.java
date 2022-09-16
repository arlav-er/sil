package it.eng.sil.module.presel;

import it.eng.afExt.utils.MessageCodes;

/**
 * Gestisce l'inserimento di una mobilità geografica in una o più mansioni.
 * 
 * Tutte l'elaborazione viene fatta nella super classe.
 * 
 * @author vaccari / Antenucci
 * @created November 13, 2003
 */
public class InsMobGeoMansione extends AbstractInsertDisponibilita {

	// In questo caso non c'è un field specifico
	protected String getCodeFieldName() {
		return "";
	}

	/**
	 * GG 20-01-05: Sovrascrivo il metodo affinché non faccia nulla. Faccio così perché VOGLIO mostrare un messaggio di
	 * ERRORE per ogni elemento GIA' ESISTENTE.
	 */
	public int disableMessageIdElementDuplicate() {
		setMessageIdElementDuplicate(MessageCodes.General.ELEMENT_DUPLICATED_WITH_KEY);
		setKeyForElementDuplicate("STRDESCRIZIONE");
		return getMessageIdElementDuplicate();
	}

}
