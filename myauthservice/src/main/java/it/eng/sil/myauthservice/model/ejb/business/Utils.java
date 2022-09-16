package it.eng.sil.myauthservice.model.ejb.business;

import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;

public class Utils {
	public static void validaPfPrincipalDest(PfPrincipal pfPrincipalDest) {
		if (pfPrincipalDest == null) {
			throw new IllegalArgumentException("Il destinatario non è presente");
		}

		// Validazione destinatario notifiche (utente cittadino)
		if (!pfPrincipalDest.isUtente()) {
			throw new IllegalArgumentException("Il destinatario non è un cittadino");
		} else if (pfPrincipalDest.getUtenteInfo() == null
				|| pfPrincipalDest.getUtenteInfo().getDeProvincia() == null) {
			throw new IllegalArgumentException("Il destinatario non ha la provincia di appartenenza");
		}
	}
}
