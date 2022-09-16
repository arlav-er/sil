/*
 * Creato il 4-gen-05
 */
package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.util.amministrazione.impatti.Controlli;

/**
 * Questo RecordProcessor controlla la durata dei movimenti a Tempo Determinato e lacia le opportune segnalazioni nel
 * caso tale durata sia superiore ai limiti impostati nella tabella TS_GENERALE.
 * <p>
 * 
 * @author roccetti
 */
public class ControlloDurataTD implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ControlloDurataTD.class.getName());

	/** Dati per la risposta */
	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query di select */
	private TransactionQueryExecutor trans;

	/**
	 * Numero di mesi oltre il quale viene generata la segnalazione o la richiesta di conferma
	 */
	Integer numMinimoMesiAvviso = null;
	/** Numero di mesi oltre il quale viene bloccata la registrazione */
	Integer numMinimoMesiBlocco = null;

	/**
	 * Costruttore, recupera dal DB il limite del numero di mesi per le segnalazioni
	 */
	public ControlloDurataTD(String name) throws Exception {
		this.name = name;
		// Recupero del numero di mesi
		recuperaDurateMinime();
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();

		// Recupero variabili utilizzate
		String codMonoTempo = (String) record.get("CODMONOTEMPO");
		String contesto = (String) record.get("CONTEXT");
		String flgAutorizzaDurataTD = (String) record.get("FLGAUTORIZZADURATATD");
		String codTipoMov = (String) record.get("CODTIPOMOV");
		String codMonoTipoMov = record.get("CODMONOTIPO") != null ? record.get("CODMONOTIPO").toString() : "";

		// Devo controllare solo i movimenti a TD che non siano cessazioni
		if (!"D".equalsIgnoreCase(codMonoTempo) || "CES".equalsIgnoreCase(codTipoMov))
			return null;

		// Calcolo la durata di mesi di lavoro per il movimento corrente
		String datInizioAvv = (String) record.get("DATAINIZIOAVV");
		String datFineMov = (String) record.get("DATFINEMOV");
		Integer numMesiLavoro = null;
		try {
			numMesiLavoro = new Integer(Controlli.numeroMesiDiLavoro(datInizioAvv, datFineMov));
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"ControlloDurataTD::processRecord(): Fallito calcolo mesi di lavoro!!!", e);

			// Segnalo all'utente l'impossibilità di effettuare la verifica e
			// blocco la processazione
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.ImportMov.ERR_CALCOLO_MESI_LAVORO), null, warnings, null);
		}
		// La durata massima di un avviamento di apprendistato è 72 mesi (bloccante).
		// Il rapporto globale (comprensivo di proroghe) può durare di più.
		Integer numMaxMesiPerApprendistatoAvv = new Integer("72");
		if (codMonoTipoMov.equalsIgnoreCase("A") && codTipoMov.equalsIgnoreCase("AVV")) {
			if (numMesiLavoro.compareTo(numMaxMesiPerApprendistatoAvv) > 0) {
				return ProcessorsUtils.createResponse("Controllo durata TD", classname,
						new Integer(MessageCodes.InfoMovimento.ERR_DURATA_MIN_TD),
						"La durata del rapporto di lavoro di un avviamento di apprendistato non può essere "
								+ "superiore a " + numMaxMesiPerApprendistatoAvv + " mesi.",
						warnings, null);
			}
		}

		// CONTROLLO DURATA DEL MOVIMENTO A TD PER I TIROCINI
		Integer numMaxMesiPerTirocini = new Integer("24");
		if (codMonoTipoMov.equalsIgnoreCase("T")) {
			if (numMesiLavoro.compareTo(numMaxMesiPerTirocini) > 0) {
				// CONTROLLO POST FASE 3 : Modificare blocco in warning se tirocinio supera 24 mesi
				/*
				 * return ProcessorsUtils.createResponse("Controllo durata TD", classname, new Integer(
				 * MessageCodes.InfoMovimento.ERR_DURATA_MIN_TD),
				 * "La durata del rapporto di lavoro per i tirocini non può essere superiore a " + numMaxMesiPerTirocini
				 * + " mesi.", warnings, null);
				 */
				warnings.add(new Warning(MessageCodes.InfoMovimento.ERR_DURATA_MIN_TD,
						"La durata del rapporto di lavoro per i tirocini è superiore a " + numMaxMesiPerTirocini
								+ " mesi."));
			}
		}

		// TODO: inserire in questo punto l'implementazione dei controlli
		// bloccanti sui limiti di durata del TD

		/***********************************************************************
		 * CONTROLLO BLOCCANTE SULLA DURATA DEL MOVIMENTO TD
		 */
		if (numMinimoMesiBlocco != null) {
			// Se è superiore al limite lancio l'errore bloccante
			if (numMesiLavoro.compareTo(numMinimoMesiBlocco) > 0) {
				warnings.add(new Warning(MessageCodes.InfoMovimento.ERR_DURATA_MIN_TD,
						"La durata del rapporto di lavoro è superiore a " + numMinimoMesiBlocco + " mesi."));
			}
		}

		/***********************************************************************
		 * CONTROLLO NON BLOCCANTE PER SEGNALAZIONE SU DURATA DEL TD
		 */

		// Se il limite minimo è nullo non faccio i controlli
		if (numMinimoMesiAvviso == null)
			return null;

		// Se l'utente ha dato l'ok non rifaccio i controlli
		if ("S".equalsIgnoreCase(flgAutorizzaDurataTD)) {
			if (warnings.size() > 0) {
				return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
			} else {
				return null;
			}
		}
		// Se è superiore al limite lancio le leganlazioni a seconda del
		// contesto
		if (numMesiLavoro.compareTo(numMinimoMesiAvviso) > 0) {
			if ("validazioneMassiva".equalsIgnoreCase(contesto) || "valida".equalsIgnoreCase(contesto)
					|| "validaArchivio".equalsIgnoreCase(contesto)) {
				// Se sono in validazione massiva o manuale aggiungo un warning
				// e basta
				warnings.add(new Warning(MessageCodes.ImportMov.WAR_DURATA_MESI_LAVORO,
						"La durata del movimento è superiore a " + numMinimoMesiAvviso.intValue() + " mesi."));
				return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
			} else {
				// Negli altri contesti devo chiedere all'utente se vuole
				// proseguire
				SourceBean response = ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_RICHIESTA_CONFERMA_MESI_LAVORO), null, warnings, null);
				ProcessorsUtils.addConfirm(response,
						"La durata del movimento è superiore a " + numMinimoMesiAvviso.intValue() + " mesi.\\n"
								+ "Si desidera proseguire nella registrazione del movimento?",
						"proseguiDopoAutorizzazioneDurataTD", new String[0], true);
				return response;
			}
		} else {
			return null;
		}
	}

	/**
	 * Metodo privato che recupera dal DB le impostazioni sulle durate del rapporto di lavoro utilizzate nei controlli
	 */
	private void recuperaDurateMinime() throws Exception {

		// Esecuzione Query
		Object resultObj = QueryExecutor.executeQuery("GET_DURATE_LAVORI_TD", null, "SELECT", Values.DB_SIL_DATI);

		// Controllo il risultato
		if (resultObj == null) {
			_logger.debug("ControlloDurataTD::recuperaDurateMinime(): Errore nel recupero delle durate "
					+ "minime del rapporto di lavoro");

			throw new Exception("Errore nel recupero della durata minima del rapporto di lavoro");
		}

		// Recupero dati
		SourceBean row = (SourceBean) ((SourceBean) resultObj).getAttribute("ROW");
		BigDecimal num = (BigDecimal) row.getAttribute("numMinMesiAvvisoTD");
		BigDecimal blocco = (BigDecimal) row.getAttribute("NUMMINMESIBLOCCOTD");
		if (num != null)
			numMinimoMesiAvviso = new Integer(num.intValue());
		if (blocco != null)
			numMinimoMesiBlocco = new Integer(blocco.intValue());
	}
}