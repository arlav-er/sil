package it.eng.sil.module;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;

/**
 * Questo modulo è in grado di simulare un servizio interattivo, ovvero un servizio che può arrestrare temporaneamente
 * la propria esecuzione, chiedere all'utente i dati di cui eventualmente ha bisogno e riprendere l'esecuzione dal punto
 * in cui era stata interrotta. A questo scopo il codice del servizio da eseguire non è contenuto nel metodo service(),
 * ma in un'altra classe che implementa l'interfaccia java.lang.Runnable. Per semplificare la gestione esiste una
 * implementazione standard di tale interfaccia nella classe it.eng.sil.module.AbstractInteractiveRunner, che fornisce
 * alcuni metodi ed un costruttore di default lasciando al programmatore solo il compito di implementare il metodo
 * "public void service()" della classe.
 * <p>
 * Per il corretto funzionamento di questo Modulo sarà necessario istanziarlo a livello di sessione, quindi se ne
 * raccomanda l'uso solo quando strettamente necessario. L'istanziazione a livello di sessione può essere ottenuta
 * attraverso l'attributo keep_instance="true" nella dichiarazione del modulo nella definizione della page che utilizza
 * il modulo. Quando l'elaborazione del modulo termina il suo stato viene automaticamente resettato per permettere
 * l'esecuzione delle elaborazioni successive.
 * <p>
 * Le richieste all'utente possono essere visualizzate nella stessa page di destinazione (utilizzando una logica di
 * selezione inserita nella page) o in una pagina diversa (utilizzando un publisher che a seconda del risultato dei
 * moduli decida quale pagina jsp invocare). L'importante è che quando l'utente ha risposto alla richiesta venga
 * reinvocato il modulo che l'ha generata in modo da consentire a quest'ultimo di riprendere l'esecuzione. La sequenza
 * di invocazioni di un modulo all'interno della stessa richiesta di servizio sarà identificata da due parametri: -
 * SERVICECONTEXTID -> Identificativo del servizio - REQUESTCONTEXTID -> Identificativo della richiesta essi vengono
 * generati automaticamente dal modulo ad ogni richiesta di servizio e dovranno essere inseriti dalla pagina di
 * richiesta utente nella risposta di quest'ultimo. Se all'invocazione del modulo non vengono trovati significa che
 * l'invocazione è esterna al contesto di servizio attuale (se presente) e che lo stato del modulo dovrà essere
 * resettato per intraprendere un nuovo contesto di servizio. Questo è necessario ad esempio nel caso che l'utente non
 * risponda alla richiesta di informazioni e intraprenda una navigazione differente.
 * <p>
 * Per eseguire le richieste questo modulo mette a disposizione il metodo: SourceBean askToUser(...) Il metodo esegue la
 * richiesta e fornisce il SourceBean della risposta utente al servizio in maniera sincrona, questo svincola il
 * programmatore del servizio dalla gestione delle risposte parziali e dal salvataggio dello stato corrente
 * dell'elaborazione.
 * <p>
 * Per la scelta della classe che fornisce il servizio, e che implementa l'interfaccia runnable, il modulo utilizza il
 * metodo: Runnable getRunnableService(...), all'interno del quale il programmatore dovrà istanziare e ritornare
 * l'oggetto Runnable necessario all'esecuzione del servizio.
 * <p>
 * 
 * Un esempio di utilizzo è il seguente: 1) Definizione del modulo a livello di sessione nella definizione della page:
 * <PAGE distributed="false" name="MyInteractivePage" scope="SESSION"> <MODULES>
 * <MODULE keep_instance="true" name="MyInteractiveModule"/> ... <PAGE/>
 * 
 * 2) Creazione della classe del modulo: public class myInteractiveModule extends AbstractInteractiveModule { ...
 * //Implementazione del metodo per la creazione del runnableService Runnable getRunnableService(SourceBean req,
 * SourceBean resp, AbstractSimpleModule module) { Runnable runnableService = new MyRunnableService(req, resp, module);
 * return runnableService; } ... }
 * 
 * 3) Creazione della classe del servizio runnable: public class MyRunnableService extends AbstractInteractiveRunner {
 * ... //Implementazione del metodo di servizio public void service() { ... //può accedere alla request e alla response
 * attraverso gli oggetti request e //response settati dal costruttore di AbstractInteractiveRunner
 * request.getAttribute(...); response.setAttribute(...); ... //può accedere ai metodi definiti
 * dall'AbstractSimpleModule attraverso l'oggetto //serviceUtils settato dal costruttore serviceUtils.doInsert(...); ...
 * //Può eseguire richieste all'utente e ottenere la risposta attraverso il metodo askToUser() SourceBean
 * myRequestParameters = new SourceBean(...); myRequestParameters.add("parameter1", "P1Value"); ... SourceBean response
 * = null; try { response = serviceUtils.askToUser(myRequestParameters); } catch (InterruptedException ie) { ... //può
 * segnalare che l'alaborazione è terminata con il metodo done() SourceBean myResult = new SourceBean(...);
 * serviceUtils.done(myResult); return; } ... }
 * 
 * 4) Definizione del codice per la richiesta all'utente, il SourceBean contenente i parametri per la richiesta
 * all'utente sarà accessibile come risposta del modulo che ha eseguito la richiesta. I paramteri di risposta per il
 * modulo costituiranno la serviceRequest successiva. Ad esempio nel caso sopra: ... <% String parameter1 =
 * request.getAttribute("MYMODULE.Parameter1"); %> ... //nell'HTML definisco la risposta dell'utente come parametro di
 * input <input type="text" name="PARAMERTORISPOSTA1" value=""/> ... //Combo, checkbox ecc...
 * 
 * All'invio della request successiva i dati della request verranno automaticamente passati come SourceBean di risposta
 * del metodo askToUser(...) chiamato. Occorre indicare anche i parametri SERVICECONTEXTID e REQUESTCONTEXTID per
 * confermare al modulo che si tratta della risposta alla richiesta utente formulata.
 * 
 * <p>
 * 
 * @author Paolo Roccetti
 */
public abstract class AbstractInteractiveModule extends AbstractSimpleModule {
	private String className = this.getClass().getName();
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(AbstractInteractiveModule.class.getName());
	/** Variabili per la gestione dello stato tra le diverse invocazioni */
	/** Indica se ho un servizio in esecuzione o se devo ancora iniziare */
	private boolean inService = false;
	private int servicecontextid = 0;
	private int requestcontextid = 0;
	/** Indica se devo eseguire una richiesta per l'utente */
	private boolean userRequest = false;
	/**
	 * Indica se ho appena completato un servizio e se devo raccoglierne il risultato
	 */
	private boolean serviceDone = false;
	/** contiene la richiesta da inviare all'utente */
	private SourceBean userRequestParameters = null;
	/** contiene la risposta da restituire al servizio */
	private SourceBean userResponseParameters = null;
	/** Contiene il risultato finale dell'elaborazione */
	private SourceBean result = null;
	/** Thread delegato all'esecuzione delle richieste di servizio */
	private Thread service = null;

	/**
	 * Questo metodo istanzia il Thread che si occupa di eseguire il servizio, gli passa i parametri necessari e si
	 * mette in attesa delle richieste del servizio, quando ne riceve una Non può essere invocato dal thread che esegue
	 * il servizio
	 */
	public void service(SourceBean request, SourceBean response) throws SourceBeanException {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Controllo se la chiamata proviene dal Thread che sta eseguendo il
		// servizio, in questo caso
		// per evitare blocchi esco subito
		if (Thread.currentThread() == service) {
			return;
		}

		// Se non sto già eseguendo un servizio ne creo uno nuovo
		if (!inService) {
			inService = true;
			// Ottengo l'oggetto che implementa il servizio
			Runnable runnableService = getRunnableService(request, response, this);
			Thread newService = new Thread(runnableService, "Servitore interattivo " + (servicecontextid + 1));
			// Sincronizzo il nuovo servizio chiamando il metodo start()
			try {
				synchronized (this) {
					// A questo punto il vecchio Thread, se per caso fosse
					// ancora in esecuzione,
					// perde tutti i diritti di accedere al modulo.
					service = newService;
					service.start();
					// Creo un nuovo servicecontextid
					servicecontextid += 1;
					// Attendo il risveglio da parte del servizio
					this.wait();
				}
			} catch (InterruptedException ie) {
				_logger.warn(className + "::sevice():InterruptedException non prevista.", ie);
				// Segnalo all'utente l'anomalia e termino
				reportOperation.reportFailure(MessageCodes.General.UNEXPECTED_INTERRUPT);
				// Non posso fermare il thread perché rischio un deadlock, posso
				// solo
				// resettare lo stato del modulo e terminare
				inService = false;
				serviceDone = false;
				userRequest = false;
				userRequestParameters = null;
				userResponseParameters = null;
				result = null;
				return;
			}
		}

		// Ciclo sulle richieste del servizio all'utente in attesa della fine
		// del servizio o di
		// invocazioni non collegate con questo servizio
		while (!serviceDone) {
			// Caso di richiesta per l'utente
			if (userRequest) {
				userRequest = false;
				// Recupero i parametri per la richiesta
				if (userRequestParameters != null) {
					response.setAttribute(userRequestParameters);
				}
				// genero una nuova richiesta di servizio
				synchronized (this) {
					requestcontextid += 1;
				}
				// Passo alla pagina il servicecontextid e il requestid
				response.setAttribute("SERVICECONTEXTID", String.valueOf(servicecontextid));
				response.setAttribute("REQUESTCONTEXTID", String.valueOf(requestcontextid));
				// termino per eseguire la page di richiesta per l'utente
				return;
			}

			// Estraggo dalla request eventuali parametri
			String sid = StringUtils.getAttributeStrNotNull(request, "SERVICECONTEXTID").trim();
			String rid = StringUtils.getAttributeStrNotNull(request, "REQUESTCONTEXTID").trim();

			// Guardo se si tratta della risposta utente o di una diversa
			// invocazione
			if (String.valueOf(servicecontextid).equalsIgnoreCase(sid)
					&& String.valueOf(requestcontextid).equalsIgnoreCase(rid)) {
				// Caso di risposta dell'utente
				// setto la request come risposta per il servizio
				userResponseParameters = request;
				// Mi sincronizzo con il servizio
				try {
					synchronized (this) {
						// Notifico al servizio che è arrivata la risposta
						this.notify();
						// Attendo il risveglio da parte del servizio
						this.wait();
					}
				} catch (InterruptedException ie) {
					_logger.warn(className + "::sevice():InterruptedException non prevista.", ie);
					// Segnalo all'utente l'anomalia e termino
					reportOperation.reportFailure(MessageCodes.General.UNEXPECTED_INTERRUPT);
					// Non posso fermare il thread perché rischio un deadlock,
					// posso solo
					// resettare lo stato del modulo e terminare
					inService = false;
					serviceDone = false;
					userRequest = false;
					userRequestParameters = null;
					userResponseParameters = null;
					result = null;
					return;
				}
			} else {
				// invocazione non collegata con la precedente
				// Notifico al Thread sospeso che il contesto è cambiato.
				service.interrupt();

				// Creo un nuovo contesto di servizio
				serviceDone = false;
				userRequest = false;
				userRequestParameters = null;
				userResponseParameters = null;
				result = null;
				// Ottengo l'oggetto che implementa il nuovo servizio
				Runnable runnableService = getRunnableService(request, response, this);
				Thread newService = new Thread(runnableService, "Servitore interattivo " + (servicecontextid + 1));
				// Sincronizzo il nuovo servizio chiamando il metodo start()
				try {
					synchronized (this) {
						// Istanzio il Thread per l'esecuzione del servizio
						// runnable
						// A questo punto il vecchio Thread, se per caso fosse
						// ancora in esecuzione,
						// perde tutti i diritti di accedere al modulo.
						service = newService;
						service.start();
						// Creo un nuovo servicecontextid
						servicecontextid += 1;
						// Attendo il risveglio da parte del servizio
						this.wait();
					}
				} catch (InterruptedException ie) {
					_logger.warn(className + "::sevice():InterruptedException non prevista.", ie);
					// Segnalo all'utente l'anomalia e termino
					reportOperation.reportFailure(MessageCodes.General.UNEXPECTED_INTERRUPT);
					// Non posso fermare il thread perché rischio un deadlock,
					// posso solo
					// resettare lo stato del modulo e terminare
					inService = false;
					serviceDone = false;
					userRequest = false;
					userRequestParameters = null;
					userResponseParameters = null;
					result = null;
					return;
				}

			}
		}

		// Caso di fine servizio
		if (serviceDone) {
			// Recupero il risultato e lo setto nella response
			if (result != null) {
				response.setAttribute(result);
				// Aggiungo l'indicazione di fine del servizio e
				// l'identificativo del servizio
				response.setAttribute("SERVICECONTEXTID", new Integer(servicecontextid));
				response.setAttribute("SERVICEDONE", "true");
			}
			// Resetto lo stato del modulo per la prossima invocazione del
			// servizio
			inService = false;
			serviceDone = false;
			userRequest = false;
			userRequestParameters = null;
			userResponseParameters = null;
			result = null;
			// Non cancello l'associazione con il thread in quanto se esso è
			// ancora in esecuzione rischia
			// di andare a richiamare il metodo service creando casini, la
			// cancellerà solo quando mi arriverà
			// una nuova richiesta di servizio e creerà un nuovo thread.
		}
	}

	/**
	 * Metodo che permette di ottenere l'oggetto che implementa il servizio
	 */
	public abstract Runnable getRunnableService(SourceBean request, SourceBean response,
			AbstractInteractiveModule module);

	/**
	 * Metodo per l'esecuzione delle richieste all'utente Funziona solo se il Thread che lo invoca è quello che esegue
	 * il servizio
	 * <p>
	 * 
	 * @thows InterruptedException se l'utente non ha risposto e il contesto è cambiato. Di solito a questo punto il
	 *        Thread deve terminare l'esecuzione (ma potrebbe dover fare alcune operazioni conclusive: rilascio lock,
	 *        chiusura connessioni ecc... quindi non lo si può terminare direttamente, si può solo comunicargli che
	 *        l'elaborazione è abortita.)
	 */
	public SourceBean askToUser(SourceBean requestParameters) throws InterruptedException {

		// Controllo che il Thread che chiama il metodo sia quello che sta
		// eseguendo il servizio
		// e di essere ancora in servizio (e che il Thread del servizio non
		// abbia già chiamato la fine
		// del servizio), altrimenti termino per evitare blocchi
		if ((!inService || (inService && serviceDone)) || (Thread.currentThread() != service)) {
			return null;
		}

		userRequest = true;
		// setto i parametri e azzero la risposta
		userRequestParameters = requestParameters;
		userResponseParameters = null;
		// Sicronizzazione con il modulo
		synchronized (this) {
			// Notifico al servizio che c'è una richiesta
			this.notify();
			_logger.debug(
					className + "::askToUser():" + Thread.currentThread().getName() + " sospeso per richiesta utente!");
			// Attendo il risveglio da parte del servizio o il timeout
			this.wait();
		}
		_logger.debug(className + "::askToUser():" + Thread.currentThread().getName()
				+ " riavviato con risposta utente: " + userResponseParameters);
		// Quando esco ho la risposta dell'utente (o nulla se è scattato il
		// timeout)
		return userResponseParameters;
	}

	/**
	 * Metodo di fine elaborazione, quando viene chiamato setta la risposta per il servizio e obbliga il Thread che lo
	 * chiama (che deve essere quello che esegue il servizio) a terminare.
	 */
	public void done(SourceBean result) {

		// Controllo che il Thread che chiama il metodo sia quello che sta
		// eseguendo il servizio
		// e di essere ancora in servizio (e che il Thread del servizio non
		// abbia già chiamato la fine
		// del servizio), altrimenti termino per evitare blocchi
		if ((!inService || (inService && serviceDone)) || (Thread.currentThread() != service)) {
			return;
		}

		serviceDone = true;
		this.result = result;
		synchronized (this) {
			if (_logger.isDebugEnabled())
				_logger.debug(className + "::done():" + Thread.currentThread().getName() + " terminato con risultato: "
						+ result.toString());
			this.notify();
		}
	}

}
