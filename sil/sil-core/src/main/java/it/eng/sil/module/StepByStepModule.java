package it.eng.sil.module;

import java.util.Vector;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.util.StepByStepConst;

/**
 * Generica classe (astratta) di un processo da eseguirsi "passo passo". Creata durante la costruzione della "esporta
 * migrazioni" ma potrebbe essere usata in altri contesti.
 * 
 * Responsabilità di questa classe astratta è: - creare e mantenere in sessione l'istanza della classe
 * "StepByStepMarchable" opportuna per tutto e solo il tempo necessario all'operazione. - gestire il recupero del
 * "comando" da eseguire e generare quello futuro, che sarà inserito nella JSP per dare l'ordine successivo.
 * 
 * Il ciclo di esecuzioni si basa su un parametro "COMANDO" letto dalla request (proviene dalla JSP) e su un nuovo
 * parametro "COMANDO" scritto nella response che indica quale sarà il passo successivo da compiere (nell'immediato
 * futuro). Alla prima chiamata ci si aspetta un "COMANDO" nullo oppure valorizzato a "INFO" (si recuperano le info in
 * modo da andare in una JSP che le mostrè all'utente e gli chiederà se procedere o meno all'operazione); nella
 * response si metterà il comando futuro a "INIZIO". Se l'utente vorrà eseguire l'operazione basterà una SUBMIT della
 * pagina avendo in "COMANDO" valorizzato proprio a "INIZIO". Una volta iniziata l'operazione, verrà iterata in
 * continuazione, ossia il comando futuro sarà sempre "SUCC" (successivo). Quando si elabora l'ultimo elemento della
 * sequenza, il comando futuro viene impostato non più a "SUCC" ma a "INFO" per indicare la fine delle operazioni.
 * 
 * La pagina JSP deve leggere: - comandoPrecedente (letto da request) - comandoFuturo (letto da response del modulo) e
 * decidere in che stato ci troviamo: 1- se comandoPrecedente è nullo/vuoto: + mostro pagina di attendesa
 * inizializzazione 2- altrimenti se comandoPrecedente è "INFO": + mostro info vecchie con tasto di inizio nuova 3-
 * altrimenti (comandoPrecedente è "INIZIO" o "SUCC") guardo il comandoFuturo 3a- se comandoFuturo è "SUCC": + mostra
 * info elaborazione e barra di progresso 3b- altrimenti (comandoFuturo è "INFO"): + mostra totali di fine operazione.
 * 
 * @author Luigi Antenucci
 */
public abstract class StepByStepModule extends AbstractModule implements StepByStepConst {

	protected static final String SESSION_EM_NAME = "STEP_BY_STEP_MARCHABLE";

	protected SourceBean request, response;
	protected SessionContainer session;

	public void service(SourceBean request, SourceBean response) throws Exception {

		this.request = request;
		this.response = response;
		this.session = (SessionContainer) getRequestContainer().getSessionContainer();

		try {

			// Leggo comando
			String comando = SourceBeanUtils.getAttrStrNotNull(request, PARAM_COMANDO);

			// Elaboro il comando
			if (comando.equalsIgnoreCase(COMANDO_INFO)) {
				doInfo();
			} else if (comando.equalsIgnoreCase(COMANDO_INIZIO)) {
				doInizio();
			} else if (comando.equalsIgnoreCase(COMANDO_SUCC)) {
				doSucc();
			} else { // comando vuoto: mostro pagina "bianca" con
						// "inizializzazione"
				putInResponse(PARAM_COMANDO, COMANDO_INFO);
			}
		} catch (EMFUserError ue) {
			getErrorHandler().addError(ue);
		} catch (Exception ex) {
			String message = ex.getMessage();
			if (StringUtils.isFilled(message)) {
				int errCode = MessageCodes.General.RIPORTA_ERRORE_INTERNO;
				Vector paramV = new Vector(1);
				paramV.add(message);
				EMFUserError ue = new EMFUserError(EMFErrorSeverity.BLOCKING, errCode, paramV);
				getErrorHandler().addError(ue);
			}
		}
	}

	/**
	 * Rende la CLASSE concreta dell'oggetto "Marchable". La classe resa dev'essere una sottoclasse di
	 * "StepByStepWalker". Non deve rendere l'oggetto creato ma la sua classe. Esempio: return EsportaMigrazioni.class;
	 */
	protected abstract Class getMarchableClass();

	/**
	 * Crea un nuovo oggetto "esecutore", lo inizializza e lo rende.
	 */
	private StepByStepMarchable newAndInitMarchable() throws Exception {

		// Creo oggetto "Marchable"
		// Anzichè fare un qualcosa come:
		// EsportaMigrazioni em = new EsportaMigrazioni();
		// non sapendo la classe a tempo di compilazione, lo faccio dinamico:
		Class marchableClass = getMarchableClass();
		StepByStepMarchable marchable;
		try {
			marchable = (StepByStepMarchable) marchableClass.newInstance();
		} catch (Exception e) {
			throw new Exception(
					"createNewMarchable(): impossibile creare un oggetto di classe '" + marchableClass.getName() + "'");
		}

		// Inizializzo
		SourceBean config = getConfig();
		marchable.initMarchable(config); // può generare eccezione

		/*
		 * DEBUG: per sapere quanto occupa in dimensione "scritta" la classe "esecutore".
		 */
		String serializeOn = (String) config.getAttribute("serializeOn");
		if (serializeOn != null) {
			try {
				java.io.FileOutputStream fout = new java.io.FileOutputStream(serializeOn);
				java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(fout);
				oos.writeObject(marchable);
				oos.close();
				// RECUPERO IL NUMERO DI VERSIONE DELL'ESPORTATORE
				java.io.ObjectStreamClass obj = java.io.ObjectStreamClass.lookup(marchable.getClass());
				System.out.println(
						"Versione Marchable " + marchable.getClass().getName() + " = " + obj.getSerialVersionUID());
			} catch (Exception e) {
			}
		}

		return marchable;
	}

	/**
	 * PASSO 1 GENERALE, INFO: per mostrare all'utente le informazioni prima di cominciare il processo iterativo
	 * (l'utente deciderà se cominciarlo o meno). Questo metodo fa: - crea e inizializza l'oggetto "esecutore" (in base
	 * alla classe concreta resa da "getMarchableClass()") - richiesta a esso di info e loro inserimento in risposta
	 * (questo sarà fatto dal metodo concreto "doInfoPersonal()") - basta (NON viene salvato l'oggetto in sessione
	 * poiché l'utente potrebbe non volere o non potere cominciare l'esecuzione!). - mette in response il comando futuro
	 * "INIZIO" (ma se l'esecutore NON ha alcun passo da fare - ossia hasNextStepMarchable() rende false - si mette il
	 * comando futuro "INFO"!)
	 */
	private final void doInfo() throws Exception {

		StepByStepMarchable marchable = newAndInitMarchable();

		try {
			doInfoPersonal(marchable); // può lanciare eccezione

			putInResponseInfoStepData(marchable); // specifica

			// Imposto quale sarà il "comando" futuro.
			putInResponse(PARAM_COMANDO, COMANDO_INIZIO);
		} catch (Exception e) {
			putInResponse(PARAM_COMANDO, COMANDO_INFO);
			throw e;
		}
	}

	/**
	 * PASSO 1 STEP, INFO: in genere si fa: - richiesta a esso di info - inserimento info in risposta (con
	 * "putInResponse()")
	 */
	protected abstract void doInfoPersonal(StepByStepMarchable marchable) throws Exception;

	/**
	 * PASSO 2, INIZIO: inizializzazione della successiva esecuzione iterata. Questo metodo fa: - crea e inizializza
	 * l'oggetto "esecutore" (in base alla classe concreta resa da "getMarchableClass()") - ne salva il riferimento in
	 * sessione (così che possa essere recuperato a ogni iterazione del passo 3) - richiede a esso le info le mette
	 * nella response (questo sarà fatto dal metodo concreto "doInizioPersonal()") - esegue la
	 * "putInResponseInfoComandoFuturo()"
	 */
	private final void doInizio() throws Exception {

		StepByStepMarchable marchable = newAndInitMarchable();

		// Salvo in sessione il riferimento all'oggetto EM.
		session.setAttribute(SESSION_EM_NAME, marchable);

		try {
			marchable.callbackOnInizioMarchable(); // callback all'inizio

			doInizioPersonal(marchable); // può lanciare eccezione

			if (!marchable.hasNextStepMarchable()) { // Se non ce n'è nessuno

				marchable.callbackOnLastSuccMarchable(); // callback poiché è
															// la fine
			}

			putInResponseInfoComandoFuturo(marchable);
		} catch (Exception e) {
			putInResponseInfoComandoOnError(marchable, e);
			throw e;
		}
	}

	/**
	 * PASSO 2 STEP, INIZIO: in genere si fa: - nulla, visto che le info sul passo futuro sono inserite in "response" in
	 * modo automatico dai metodi "putInResponseXxxStepInfo()". - si può prevedere un'ulteriore inizializzazione
	 * dell'esecutore proprio qua (per es. si possono fare nuovi controlli).
	 */
	protected abstract void doInizioPersonal(StepByStepMarchable marchable) throws Exception;

	/**
	 * PASSO 3, SUCC (ITERABILE): viene eseguita l'operazione relativa al passo successivo (reperito per es nella fase
	 * di "inizio"). Si usa l'oggetto "esecutore" che è presente in sessione. Si deve controllare se ci sarà o meno una
	 * nuova iterazione da fare e tramite un parametro in risposta si avvisa la JSP sul da farsi. Questo metodo fa: -
	 * recupera dalla sessione l'oggetto "esecutore" (se manca, lancia eccezione) - richiede a esso le info le mette
	 * nella response (questo sarà fatto dal metodo concreto "doInizioPersonal()") - esegue la
	 * "putInResponseInfoComandoFuturo()"
	 */
	private final void doSucc() throws Exception {

		// Recupero dalla sessione il riferimento all'oggetto EM (se manca:
		// errore).
		StepByStepMarchable marchable = (StepByStepMarchable) session.getAttribute(SESSION_EM_NAME);
		if (marchable == null) {
			throw new Exception("doSucc(): Non ho potuto recuperare l'oggetto Esecutore dalla sessione!");
		}

		try {
			// Ho ancora almeno un CPI da elaborare?
			if (marchable.hasNextStepMarchable()) {

				marchable.doNextStepMarchable();

				if (!marchable.hasNextStepMarchable()) { // Ce ne sarà un
															// altro? Se no:

					marchable.callbackOnLastSuccMarchable(); // callback
																// sull'ultimo
				}
			}
			doSuccPersonal(marchable); // può lanciare eccezione

			putInResponseInfoComandoFuturo(marchable);
		} catch (Exception e) {
			putInResponseInfoComandoOnError(marchable, e);
			throw e;
		}
	}

	/**
	 * PASSO 3 STEP, SUCC (ITERABILE): in genere si fa: - nulla, visto che le info sul passo futuro sono inserite in
	 * "response" in modo automatico dai metodi "putInResponseXxxStepInfo()". (viene chiamata dopo la
	 * "marchable.doNextStepMarchable()")
	 */
	protected abstract void doSuccPersonal(StepByStepMarchable marchable) throws Exception;

	/**
	 * Salva in response sotto l'attributo dato un certo oggetto (se presente). Non genera alcuna eccezione in caso di
	 * errore. E' "protected": usabile dalle sottoclassi. NB: se c'è già, rimuove l'attributo prima di inserirne il
	 * nuovo valore.
	 */
	protected void putInResponse(String attribute, Object value) {

		if (value != null) {
			try {
				response.delAttribute(attribute);
				response.setAttribute(attribute, value);
			} catch (SourceBeanException e) {
			}
		}
	}

	/**
	 * Salva in response il valore di "infoCpiSucc" e di "comando" futuri. Se esiste almeno un CPI da elaborare nella
	 * successiva iterazione, - salva il valore di "infoCpiSucc" e imposta il comando a "SUCC"; Altrimenti (fine
	 * elaborazione) - non salva alcun valore di "infoCpiSucc" e imposta il comando a "INFO" (=STOP) - inoltre mette in
	 * response tutte le info riassuntive (totali).
	 */
	private void putInResponseInfoComandoFuturo(StepByStepMarchable marchable) {

		// Aggiungo sempre la percentuale di progresso (per la progress-bar).
		putInResponse(PARAM_PROGPERC, new Double(marchable.getProgPercMarchable()));

		if (marchable.hasNextStepMarchable()) {

			putInResponseNextStepData(marchable); // specifica

			// Imposto quale sarà il "comando" futuro.
			putInResponse(PARAM_COMANDO, COMANDO_SUCC);
		} else {
			// Tutti i totali in response
			putInResponseEndStepData(marchable); // specifica

			// Nessun'altra iterazione possibile: STOP
			// (in realtà uso COMANDO_INFO, ma come comando FUTURO!)
			putInResponse(PARAM_COMANDO, COMANDO_INFO);

			// Se non ho più CPI da elaborare, ho finito il giro:
			// rimuovo dalla sessione il riferimento all'oggetto EM.
			session.delAttribute(SESSION_EM_NAME);
		}
	}

	/**
	 * Simile a "putInResponseInfoComandoFuturo" ma serve per mettere in request i dati dopo che si è avuto un errore e
	 * l'elaborazione passo passo deve essere interrotta. In pratica fa come se l'esecutore non avesse più passi da
	 * eseguire. (rimuove anche l'esecutore dalla sessione).
	 */
	private void putInResponseInfoComandoOnError(StepByStepMarchable marchable, Exception e) {

		marchable.callbackOnErrorMarchable(e);

		// Aggiungo sempre la percentuale di progresso (per la progress-bar).
		putInResponse(PARAM_PROGPERC, new Double(marchable.getProgPercMarchable()));

		// Tutti i totali in response
		putInResponseEndStepData(marchable); // specifica

		// Nessun'altra iterazione possibile: STOP
		// (in realtà uso COMANDO_INFO, ma come comando FUTURO!)
		putInResponse(PARAM_COMANDO, COMANDO_INFO);

		// Se non ho più CPI da elaborare, ho finito il giro:
		// rimuovo dalla sessione il riferimento all'oggetto EM.
		session.delAttribute(SESSION_EM_NAME);
	}

	/**
	 * Viene chiamata dopo "info" per poter mettere nella "response" i dati sulla futura azione da eseguire. Sono solo
	 * dati utili all'utente e non influenzano l'iterazione. Non si deve far altro che chiedere i dati al "Marchable" e
	 * inserirle nella risposta con la "putInResponse"()" affinché siano visibili alla pagina JSP.
	 */
	protected abstract void putInResponseInfoStepData(StepByStepMarchable marchable);

	/**
	 * Viene chiamata dopo "inizio" e dopo ogni "succ" (esclusa l'ultima) per poter mettere nella "response" i dati
	 * sulla futura azione da eseguire. Sono solo dati utili all'utente e non influenzano l'iterazione. Non si deve far
	 * altro che chiedere i dati al "Marchable" e inserirle nella risposta con la "putInResponse"()" affinché siano
	 * visibili alla pagina JSP.
	 */
	protected abstract void putInResponseNextStepData(StepByStepMarchable marchable);

	/**
	 * Viene chiamata quando è stata eseguita l'ultima "succ" per poter mettere nella "response" i dati dell'esecuzione
	 * complessiva (come per esempio il numero di record usati, le somme totali, ecc). Sono solo dati utili all'utente e
	 * non influenzano l'iterazione. Non si deve far altro che chiedere i dati al "Marchable" e inserirle nella risposta
	 * con la "putInResponse"()" affinché siano visibili alla pagina JSP.
	 */
	protected abstract void putInResponseEndStepData(StepByStepMarchable marchable);

}
