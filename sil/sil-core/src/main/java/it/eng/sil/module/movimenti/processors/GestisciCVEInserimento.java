/*
 * Creato il 29-dic-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti.processors;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.ResultLogger;
import it.eng.sil.module.movimenti.SingleResultLogger;
import it.eng.sil.module.movimenti.ValidatorAssunzioneCVE;

/**
 * @author giuliani
 *
 *         Questo processor gestisce l'inserimento dell'avviamento da cessazione veloce nel caso di inserimento manuale
 *         del movimento
 */
public class GestisciCVEInserimento implements RecordProcessor {

	/** Dati per la risposta */
	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query di select */
	private TransactionQueryExecutor trans;
	private BigDecimal user = null;
	private SourceBean sbInfoGenerale = null;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GestisciCVEInserimento.class.getName());

	/**
	 * Costruttore
	 * <p>
	 * 
	 * @param name
	 *            Nome del processore
	 * @param transexec
	 *            TransactionQueryExecutor da utilizzare per le query
	 */
	public GestisciCVEInserimento(String name, SourceBean sb, TransactionQueryExecutor transexec, BigDecimal user) {
		this.name = name;
		this.trans = transexec;
		this.user = user;
		this.sbInfoGenerale = sb;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see it.eng.sil.module.movimenti.RecordProcessor#processRecord(java.util.Map)
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", warnings, nested);
		}

		// Creo la Map per il movimento di avviamento da inserire
		Map recordAvv = null;
		try {
			recordAvv = getMapAvv(record);
		} catch (MovimentoAVVDaCVEException CVEex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Errore nel reperimento delle informazioni per l'inserimento dell'avviamento da cessazione veloce",
					CVEex);
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.ImportMov.ERR_INSERT_AVV_CEV), CVEex.getMessageDescrption(), null, null);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Errore GENERICO nel reperimento delle informazioni per l'inserimento dell'avviamento da cessazione veloce",
					e);
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.ImportMov.ERR_INSERT_AVV_CEV), null, null, null);
		}

		// ***Istanzio il meccanismo di validazione dell'assunzione da CVE e lo configuro per l'inserimento***//
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator;

		// creazione del validatore per l'assunzione e configurazione dei processori
		ValidatorAssunzioneCVE validator = new ValidatorAssunzioneCVE();

		// Path dei file di configurazione
		String processorbase = configbase + "processors" + File.separator;

		// Aggiungo i processori al validator (quelli che non servono non li inserisco)
		try {
			// Controlli sulle autorizzazioni
			validator.addProcessor(new ControlloPermessi("Autorizzazione per impatti", trans));
			// controllo dei dati sensibili del lavoratore
			validator.addProcessor(new ProcControlloMbCmEtaLav("Controllo dati lavoratore", trans));
			// Processore per il controllo dell'esistenza di movimenti simili a quello in inserimento
			validator.addProcessor(new ControlloMovimentoSimile("Controllo movimenti simili", trans, sbInfoGenerale));
			// Processore che controlla i dati del movimento.
			validator.addProcessor(new ControllaMovimenti(sbInfoGenerale, trans, user));
			// Processore per ulteriori controlli che di solito sono svolti nella jsp
			validator.addProcessor(new CrossController("Controllore Incrociato"));
			// Processore per l'esecuzione degli impatti
			validator.addProcessor(new EseguiImpatti("Esecuzione impatti", sbInfoGenerale, trans, user));
			// Inserimento Movimento
			validator.addProcessor(new InsertData("Inserimento Movimento", trans, processorbase + "insertMovimento.xml",
					"INSERT_MOVIMENTO", user));
			// Processore per l'inserimento in am_movimento_apprendist
			validator.addProcessor(new InsertApprendistato(user, trans));
			// Processore per l'inserimento in am_movimento_apprendist delle info relative al tirocinio
			validator.addProcessor(new InsertTirocinio(user, trans, sbInfoGenerale));
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile configurare i processori per l'inserimento dell'avviamento da cessazione veloce, "
							+ "controllare i file XML di configurazione.",
					e);
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.ImportMov.ERR_INSERT_AVV_CEV), null, null, null);
		}

		_logger.debug(
				"DefaultScrollableDataResult::SmartScrollableDataResult: INIZIO DELLA PROCESSAZIONE DELL'AVVIAMENTO LEGATO ALLA CESSAZIONE...");
		// Esegue la processazione dell'avviamento e ritorna il risultato
		// NON FACCIO LOG SU FILE:
		// ResultLogger resultLogger = new HtmlResultLogger();
		// resultLogger.addChildResultLogger(new SingleResultLogger()); // (x)->Html->DB
		ResultLogger resultLogger = new SingleResultLogger();

		SourceBean resultAvv = validator.validaAvvDaCVE(recordAvv, resultLogger, trans);
		// Tracciamento del risultato dell'avviamento
		// TracerSingleton.log(Values.APP_NAME, TracerSingleton.DEBUG, resultAvv.toXML(false));
		// TracerSingleton.log(Values.APP_NAME, TracerSingleton.DEBUG, "...FINE DELLA PROCESSAZIONE DELL'AVVIAMENTO
		// LEGATO ALLA CESSAZIONE");

		// Esamino il risultato
		if (validator.getStopAvvVeloce()) {
			// Warning e Stop durante la validazione dell'avviamento veloce. Devo andare avanti cercando di inserire il
			// movimento orfano.
			// Tale situazione si può verificare per gli avviamenti veloci per cui non c'è la competenza (Trasferimento
			// lavoratore)
			Warning w = new Warning(MessageCodes.ImportMov.ERR_INSERT_AVV_CEV,
					"Si cerca di inserire il movimento orfano.");
			warnings.add(w);
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested, true);
		} else {
			String resultCode = (String) resultAvv.getAttribute("RESULT");
			if ("ERROR".equalsIgnoreCase(resultCode)) {
				// Ho avuto errori, inserisco la risposta del processor che ha dato errore in quella del processor
				SourceBean errorProcessor = (SourceBean) resultAvv.getFilteredSourceBeanAttribute("PROCESSOR", "RESULT",
						"ERROR");
				String dettaglio = (String) errorProcessor.getAttribute("ERROR.messagecode");
				SourceBean result = ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_INSERT_AVV_CEV), dettaglio, null, null);
				// Se ho confirm o alert le riporto nella risposta all'utente
				if (errorProcessor.containsAttribute("ALERT")) {
					result.setAttribute((SourceBean) errorProcessor.getAttribute("ALERT"));
				}
				if (errorProcessor.containsAttribute("CONFIRM")) {
					result.setAttribute((SourceBean) errorProcessor.getAttribute("CONFIRM"));
				}
				return result;
			} else {
				// Map per l'inserimento del documento di avviamento (appena inserito) da cessazione veloce
				record.put("MAPCVE", recordAvv);
				// Imposto nella Map della cessazione che ho un precedente e che si tratta del movimento appena inserito
				record.put("COLLEGATO", "precedente");
				record.put("PRGMOVIMENTOPREC", recordAvv.get("PRGMOVIMENTO"));
				return null;
			}
		}
	}

	/**
	 * Crea il movimento di avviamento da inserire
	 */
	private Map getMapAvv(Map record) throws MovimentoAVVDaCVEException {
		Map recordAvv = new Hashtable(record);

		String flgAssPropria = (String) record.get("FLGASSPROPRIA");
		boolean notAssPropria = "N".equalsIgnoreCase(flgAssPropria);

		// I seguenti campi vengono impostati ai default nell'inserimento
		recordAvv.put("CODTIPOMOV", "AVV"); // Avviamento
		recordAvv.put("DATINIZIOMOV", record.get("DATAINIZIOAVVCEV"));// Data di avviamento dichiarata nella 'jsp
																		// Cessazione'
		recordAvv.put("DATCOMUNICAZ", record.get("DATAINIZIOAVVCEV"));// Data di comunicazione la stessa del movimento
		recordAvv.put("NUMGGTRAMOVCOMUNICAZIONE", new BigDecimal(0));

		// Nella vecchia gestione i campi CODCONTRATTO STRMATRICOLA CODTIPOASS e CODMONOTEMPO venivano impostati
		// di default in quanto non era possibile modificarli da una cessazione. Con la nuova gestione possono
		// essere modificati quindi gli viene settato lo stesso valore inserito in una cessazione
		recordAvv.put("CODCONTRATTO", record.get("CODCONTRATTO"));

		// Nel caso di assunzione in somministrazione il campo matricola è obbligatorio
		if (record.containsKey("CODAZTIPOAZIENDA") && record.get("CODAZTIPOAZIENDA").toString().equalsIgnoreCase("INT")
				&& notAssPropria) {
			recordAvv.put("STRMATRICOLA", record.get("STRMATRICOLA"));
		}
		recordAvv.put("CODTIPOASS", record.get("CODTIPOASS"));
		recordAvv.put("CODMONOTEMPO", record.get("CODMONOTEMPO"));

		// questa era la vecchia gestione dei movimenti
		// recordAvv.put("CODCONTRATTO", "LP");//Lavoro dipendente a tempo indeterminato
		// recordAvv.put("STRMATRICOLA", "ND"); //Matr. non dichiarata
		// recordAvv.put("CODTIPOASS", "A.01.00");//Comunicazione assunzione (ord-agr)
		// recordAvv.put("CODMONOTEMPO", "I"); //Tempo indeterminato

		recordAvv.put("CODSTATOATTO", "PR");// Protocollatao (non sarebbe melio PA?!?!? mha...)
		if (record.containsKey("VALIDAZIONE_MOBILITA")
				&& record.get("VALIDAZIONE_MOBILITA").toString().equals("true")) {
			recordAvv.put("CODMOTANNULLAMENTO", "MOB");// Da validazione mobilità
		} else {
			recordAvv.put("CODMOTANNULLAMENTO", "CVE");// Da Cessazione VEloce -> CVE
		}
		recordAvv.put("COLLEGATO", "nessuno");// L'avviamento non è collegato a nulla
		if (record.get("DATAINIZIOAVVCEV") != null) {
			recordAvv.put("DATAINIZIOAVV", record.get("DATAINIZIOAVVCEV")); // Imposto la data di inizio rapporto
			recordAvv.put("DATINIZIOMOVPREC", record.get("DATAINIZIOAVVCEV"));
		} else {
			throw new MovimentoAVVDaCVEException(
					"Non è stato inserita la data di inizio dell'avviamento.Impossibile inserire l'avviamento da cessazione veloce.");
		}
		// codccnl non è più obbligatorio per i movimenti
		if (record.get("CODAZCCNL") != null) {
			recordAvv.put("CODCCNL", record.get("CODAZCCNL")); // prevalorizzo sulla base di quello dell'azienda
		}
		// else {
		// throw new MovimentoAVVDaCVEException("Non è stato inserito il codice CCNL dell'unita azienda.");
		// }

		// I seguenti campi debbono essere cancellati perché non attinenti all'avviamento ma alla cessazione originale
		recordAvv.remove("CODMVCESSAZIONE");
		recordAvv.remove("DATFINEMOV");
		recordAvv.remove("PRGMOVIMENTOPREC");
		recordAvv.remove("FLGMODTEMPO");
		recordAvv.remove("FLGMODREDDITO");
		recordAvv.remove("DATFINEMOVEFFETTIVA");
		recordAvv.remove("DATFINEMOVPREC");
		recordAvv.remove("STRNOTE");
		recordAvv.remove("PERMETTIIMPATTI");
		// recordAvv.remove("CONFERMA_CONTROLLO_MOV_SIMILI");
		recordAvv.remove("DATINIZIOMOVSUPREDDITO");
		recordAvv.remove("PRGDICHLAV");
		recordAvv.remove("TIPODICHSANATA");
		recordAvv.remove("DATSITSANATA");
		recordAvv.remove("DECRETRIBUZIONEMENSANATA");
		recordAvv.remove("DATAINIZIOAVVCEV");
		recordAvv.remove("PRGMOVIMENTOAPPCVE");

		return recordAvv;
	}

	/**
	 * Classe creeata appositamente per gesstire gli errori derivanti del reperimento dei dati per l'inserimento
	 * dell'AVV da cessazione veloce.
	 * 
	 * @author Davide Giuliani, 26/08/2005
	 */
	public class MovimentoAVVDaCVEException extends Exception {

		private int code = 0;
		private String message = "";

		public MovimentoAVVDaCVEException(int _code) {
			super();
			code = _code;
		}

		public MovimentoAVVDaCVEException(String msg) {
			super();
			message = msg;
		}

		public MovimentoAVVDaCVEException(int _code, String msg) {
			super();
			code = _code;
			message = msg;
		}

		public int getMessageIdFail() {
			return code;
		}

		public String getMessageDescrption() {
			return message;
		}

	}

}
