package it.eng.sil.module.amministrazione;

import it.eng.sil.module.StepByStepMarchable;
import it.eng.sil.module.StepByStepModule;

/**
 * Processo di Invio mail delle Migrazioni eseguito tramite l'applicazione.
 * 
 * @author Franco Vuoto
 */
public class MailInviaExpModule extends StepByStepModule {

	/* SI VEDA IL COMPORTAMENTO DELLA "StepByStepModule"! */

	/**
	 * Rende la CLASSE concreta dell'oggetto "Marchable": qui si usa la EsportaMigrazioni.
	 */
	protected Class getMarchableClass() {
		return MailInviaMigrazioni.class;
	}

	/**
	 * PASSO 1, INFO
	 */
	protected void doInfoPersonal(StepByStepMarchable marchable) throws Exception {
		// Niente da fare di specifico...
		// (le info sul passo sono inserite in "response" dalle
		// putInResponseInfoStepData()"
	}

	/**
	 * PASSO 2, INIZIO
	 */
	protected void doInizioPersonal(StepByStepMarchable marchable) throws Exception {
		// Niente da fare di specifico...
		// (le info sul passo sono inserite in "response" dalle
		// putInResponseXxxStepInfo()"
	}

	/**
	 * PASSO 3, SUCC (ITERABILE)
	 */
	protected void doSuccPersonal(StepByStepMarchable marchable) throws Exception {
		// Niente da fare di specifico
		// (le info sul passo sono inserite in "response" dalle
		// putInResponseXxxStepInfo()"
	}

	/**
	 * Viene chiamata dopo "info" per poter mettere nella "response" i dati sulla futura azione da eseguire. Sono solo
	 * dati utili all'utente e non influenzano l'iterazione. Non si deve far altro che chiedere i dati al "Marchable" e
	 * inserirle nella risposta con la "putInResponse"()" affinché siano visibili alla pagina JSP.
	 */
	protected void putInResponseInfoStepData(StepByStepMarchable marchable) {
	}

	/**
	 * Viene chiamata dopo "inizio" e dopo ogni "succ" (esclusa l'ultima) per poter mettere nella "response" i dati
	 * sulla futura azione da eseguire. Sono solo dati utili all'utente e non influenzano l'iterazione. Non si deve far
	 * altro che chiedere i dati al "Marchable" e inserirle nella risposta con la "putInResponse"()" affinché siano
	 * visibili alla pagina JSP.
	 */
	protected void putInResponseNextStepData(StepByStepMarchable marchable) {

		MailInviaMigrazioni im = (MailInviaMigrazioni) marchable;
		putInResponse("BagList", im.getBagList());

	}

	/**
	 * Viene chiamata dopo "inizio" e dopo ogni "succ" (esclusa l'ultima) per poter mettere nella "response" i dati
	 * sulla futura azione da eseguire. Sono solo dati utili all'utente e non influenzano l'iterazione. Non si deve far
	 * altro che chiedere i dati al "Marchable" e inserirle nella risposta con la "putInResponse"()" affinché siano
	 * visibili alla pagina JSP.
	 */
	protected void putInResponseEndStepData(StepByStepMarchable marchable) {
		MailInviaMigrazioni im = (MailInviaMigrazioni) marchable;
		putInResponse("BagList", im.getBagList());

	}

}
