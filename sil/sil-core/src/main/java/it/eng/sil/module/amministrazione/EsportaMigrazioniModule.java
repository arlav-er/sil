package it.eng.sil.module.amministrazione;

import it.eng.sil.module.StepByStepMarchable;
import it.eng.sil.module.StepByStepModule;
import it.eng.sil.util.amministrazione.impatti.EsportaMigrazioni;

/**
 * Processo di Esporta Migrazioni eseguito tramite l'applicazione. Si usa la classe
 * it.eng.sil.util.amministrazione.EsportaMigrazioni.
 * 
 * @author Luigi Antenucci
 */
public class EsportaMigrazioniModule extends StepByStepModule {

	/* SI VEDA IL COMPORTAMENTO DELLA "StepByStepModule"! */

	/**
	 * Rende la CLASSE concreta dell'oggetto "Marchable": qui si usa la EsportaMigrazioni.
	 */
	protected Class getMarchableClass() {
		return EsportaMigrazioni.class;
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
		EsportaMigrazioni em = (EsportaMigrazioni) marchable;

		// Metto in risposta le info ottenute da "get" sull'oggetto EM.
		putInResponse("codProvinciaSil", em.getCodProvinciaSil());
		putInResponse("descProvinciaSil", em.getDescProvinciaSil());

		putInResponse("flgInvio", new Boolean(em.getFlgInvio()));
		putInResponse("flgInCorso", new Boolean(em.getFlgInCorso()));

		// Metto in risposta le info della precedente elaborazione.
		putInResponse("dataUltimaMigrazione", em.getDataUltimaMigrazione());
		putInResponse("dataLancio", em.getDataLancio());

		putInResponse("isNeverDoneMigr", new Boolean(em.isNeverDoneMigr()));

		putInResponse("precNumCpiElab", new Integer(em.getPrecNumCpiElab()));
		putInResponse("precNumMovimentiElab", new Integer(em.getPrecNumMovimentiElab()));
		putInResponse("strPrecNumSecElaborazione", em.getStrPrecNumSecElaborazione());
		putInResponse("precFattaDaUtente", em.getPrecFattaDaUtente());
	}

	/**
	 * Viene chiamata dopo "inizio" e dopo ogni "succ" (esclusa l'ultima) per poter mettere nella "response" i dati
	 * sulla futura azione da eseguire. Sono solo dati utili all'utente e non influenzano l'iterazione. Non si deve far
	 * altro che chiedere i dati al "Marchable" e inserirle nella risposta con la "putInResponse"()" affinché siano
	 * visibili alla pagina JSP.
	 */
	protected void putInResponseNextStepData(StepByStepMarchable marchable) {
		EsportaMigrazioni em = (EsportaMigrazioni) marchable;

		// Metto in risposta il successivo CODCPI che sarà elaborato (se
		// esiste).
		// (è solo un'informazione per l'utente: non comanda l'esecuzione
		// successiva)
		putInResponse("infoCpiSucc", em.getInfoCpiSucc());

		putInResponseEndStepData(marchable);
	}

	/**
	 * Viene chiamata dopo "inizio" e dopo ogni "succ" (esclusa l'ultima) per poter mettere nella "response" i dati
	 * sulla futura azione da eseguire. Sono solo dati utili all'utente e non influenzano l'iterazione. Non si deve far
	 * altro che chiedere i dati al "Marchable" e inserirle nella risposta con la "putInResponse"()" affinché siano
	 * visibili alla pagina JSP.
	 */
	protected void putInResponseEndStepData(StepByStepMarchable marchable) {
		EsportaMigrazioni em = (EsportaMigrazioni) marchable;

		putInResponse("dataQuestaMigrazione", em.getDataQuestaMigrazione());
		putInResponse("dataQuestoLancio", em.getDataLancio());
		putInResponse("numCpiElab", new Integer(em.getNumCpiElab()));
		putInResponse("numMovimentiElab", new Integer(em.getNumMovimentiElab()));
		putInResponse("strNumSecElaborazione", em.getStrNumSecElaborazione());
	}

}
