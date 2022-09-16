/*
 * Creato il 4-nov-04
 */
package it.eng.sil.module.movimenti;

import java.math.BigDecimal;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;

/**
 * Oggetto per il log su DB della validazione massiva dei movimenti
 * <p/>
 * 
 * @author roccetti
 */
public class MultipleResultLogger extends AbstractResultLogger {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MultipleResultLogger.class.getName());

	/**
	 * Identificativo del gruppo di risposta a cui è legato il MultipleResultLogger
	 */
	private final BigDecimal prgValidazioneMassiva;
	/** Riferimento alla sessione */
	private SessionContainer sessione = null;

	// il contesto di cui si vuole effettuare il log

	/**
	 * Costruttore per un nuovo gruppo di risultati, mette in sessione l'oggetto per il recupero dei risultati stessi.
	 * <p/>
	 * 
	 * @param numRecords
	 *            numero di record da processare
	 * @param sessione
	 *            riferimento alla sessione (per recupero codice utente)
	 * @throws LogException
	 *             quando non si riece a creare il nuovo gruppo di risultati della validazione massiva
	 */
	public MultipleResultLogger(int numRecords, SessionContainer sessione) throws LogException {
		super((BigDecimal) sessione.getAttribute("_CDUT_"));
		this.sessione = sessione;
		// Recupero nuova sequence
		this.prgValidazioneMassiva = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_AM_VALIDAZIONE_MASSIVA");
		if (prgValidazioneMassiva == null)
			throw new LogException("Impossibile ottenere un progressivo per il log" + " del gruppo di risultati");
		// Generazione array argomenti
		Object[] args = new Object[3];
		args[0] = this.prgValidazioneMassiva;
		args[1] = new BigDecimal(numRecords);
		args[2] = (BigDecimal) sessione.getAttribute("_CDUT_");
		// Esecuzione query
		try {
			Object queryRes = QueryExecutor.executeQuery("INSERT_NUOVA_VAL_MASSIVA", args, "INSERT",
					Values.DB_SIL_DATI);
			if (!(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
				throw new LogException("Impossibile creare il nuovo gruppo di risultati");
			}
		} catch (Exception e) {
			// Eseguo il log e rilancio l'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile creare il nuovo gruppo di risultati",
					(Exception) e);

			throw new LogException("Impossibile creare il nuovo gruppo di risultati");
		}
	}

	/**
	 * Costruttore per un nuovo gruppo di risultati, mette in sessione l'oggetto per il recupero dei risultati stessi.
	 * <p/>
	 * 
	 * @param numRecords
	 *            numero di record da processare
	 * @param sessione
	 *            riferimento alla sessione (per recupero codice utente)
	 * @param contesto
	 *            indica il contesto di cui si vuole memorizzare i risultati
	 * @throws LogException
	 *             quando non si riece a creare il nuovo gruppo di risultati della validazione massiva
	 */
	public MultipleResultLogger(int numRecords, SessionContainer sessione, int contesto) throws Exception {
		super((BigDecimal) sessione.getAttribute("_CDUT_"));
		BigDecimal cdnut = (BigDecimal) sessione.getAttribute("_CDUT_");
		this.sessione = sessione;
		TransactionQueryExecutor trans = null;
		switch (contesto) {
		case MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA:
			try {
				trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				trans.initTransaction();
				Object[] argsRis = new Object[1];
				argsRis[0] = cdnut;
				SourceBean sbRisultatoOld = (SourceBean) trans
						.executeQuery("GET_RISULTATO_ULTIMA_VALIDAZIONE_MOB_UTENTE", argsRis, "SELECT");
				if (sbRisultatoOld == null || !(sbRisultatoOld instanceof SourceBean)) {
					throw new LogException("Impossibile leggere il vecchio gruppo di risultati");
				}
				Object prgRis = sbRisultatoOld.getAttribute("ROW.PRGMOBILITARIS");
				if (prgRis != null) {
					Object[] argsDelRis = new Object[1];
					argsDelRis[0] = prgRis;
					Object queryDeleteRisDett = trans.executeQuery("DELETE_RISULTATO_DETT_VECCHIA_VALIDAZIONE_MOB",
							argsDelRis, "DELETE");
					if (queryDeleteRisDett == null || !(queryDeleteRisDett instanceof Boolean
							&& ((Boolean) queryDeleteRisDett).booleanValue() == true)) {
						throw new LogException(
								"Impossibile cancellare il dettaglio dei risultati della vecchia copia prospetti");
					}
					Object queryDeleteRis = trans.executeQuery("DELETE_RISULTATO_VECCHIA_VALIDAZIONE_MOB", argsDelRis,
							"DELETE");
					if (queryDeleteRisDett == null || !(queryDeleteRisDett instanceof Boolean
							&& ((Boolean) queryDeleteRisDett).booleanValue() == true)) {
						throw new LogException(
								"Impossibile cancellare la testata dei risultati della vecchia validazione");
					}
				}
				this.prgValidazioneMassiva = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_AM_MOBILITA_RIS");
				if (prgValidazioneMassiva == null) {
					throw new LogException("Impossibile ottenere un progressivo per il log del gruppo di risultati");
				}
				// Generazione array argomenti
				Object[] args = new Object[6];
				args[0] = this.prgValidazioneMassiva;
				args[1] = "VMSMB000"; // ambito applicativo
				args[2] = new BigDecimal(numRecords);
				args[3] = "N";
				args[4] = cdnut;
				args[5] = cdnut;
				Object queryRes = trans.executeQuery("INSERT_NUOVA_VAL_MASSIVA_MOBILITA", args, "INSERT");
				if (!(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
					throw new LogException("Impossibile creare il nuovo gruppo di risultati");
				}
				trans.commitTransaction();
			} catch (Exception e) {
				if (trans != null)
					trans.rollBackTransaction();
				// Eseguo il log e rilancio l'eccezione
				it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile creare il nuovo gruppo di risultati",
						(Exception) e);

				throw new LogException("Impossibile creare il nuovo gruppo di risultati");
			}
			break;

		default:
			throw new LogException("Impossibile creare il nuovo gruppo di risultati per il contesto " + contesto);
		}
	}

	public MultipleResultLogger(int numRecords, SessionContainer sessione, String numAnnoXCopia, String numAnnoVerifica,
			String codMonoCategoria) throws Exception {
		super((BigDecimal) sessione.getAttribute("_CDUT_"));
		BigDecimal cdnut = (BigDecimal) sessione.getAttribute("_CDUT_");
		this.sessione = sessione;
		TransactionQueryExecutor trans = null;
		try {
			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			trans.initTransaction();
			Object[] argsRis = new Object[1];
			argsRis[0] = cdnut;
			SourceBean sbRisultatoOld = (SourceBean) trans.executeQuery("GET_RISULTATO_ULTIMA_COPIA_PROSP_UTENTE",
					argsRis, "SELECT");
			if (sbRisultatoOld == null || !(sbRisultatoOld instanceof SourceBean)) {
				throw new LogException("Impossibile leggere il vecchio gruppo di risultati");
			}
			Object prgRis = sbRisultatoOld.getAttribute("ROW.PRGPICOPIECORSOANNO");
			if (prgRis != null) {
				Object[] argsDelRis = new Object[1];
				argsDelRis[0] = prgRis;
				Object queryDeleteRisDett = trans.executeQuery("DELETE_RISULTATO_DETT_VECCHIA_COPIA_PROSP", argsDelRis,
						"DELETE");
				if (queryDeleteRisDett == null || !(queryDeleteRisDett instanceof Boolean
						&& ((Boolean) queryDeleteRisDett).booleanValue() == true)) {
					throw new LogException(
							"Impossibile cancellare il dettaglio dei risultati della vecchia copia prospetti");
				}
				Object queryDeleteRis = trans.executeQuery("DELETE_RISULTATO_VECCHIA_COPIA_PROSP", argsDelRis,
						"DELETE");
				if (queryDeleteRisDett == null || !(queryDeleteRisDett instanceof Boolean
						&& ((Boolean) queryDeleteRisDett).booleanValue() == true)) {
					throw new LogException(
							"Impossibile cancellare la testata dei risultati della vecchia copia prospetti");
				}
			}

			this.prgValidazioneMassiva = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_CM_PI_COPIE_CORSO_ANNO");
			if (prgValidazioneMassiva == null) {
				throw new LogException("Impossibile ottenere un progressivo per il log del gruppo di risultati");
			}

			Object[] args = new Object[6];
			args[0] = this.prgValidazioneMassiva;
			args[1] = new BigDecimal(numRecords);
			args[2] = cdnut;
			args[3] = numAnnoXCopia.equals("") ? null : new BigDecimal(numAnnoXCopia);
			args[4] = numAnnoVerifica.equals("") ? null : new BigDecimal(numAnnoVerifica);
			args[5] = codMonoCategoria;

			Object queryRes = trans.executeQuery("INSERT_NUOVA_COPIA_MASSIVA_PROSPETTI", args, "INSERT");
			if (!(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
				throw new LogException("Impossibile creare il nuovo gruppo di risultati");
			}
			trans.commitTransaction();
		} catch (Exception e) {
			if (trans != null) {
				trans.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile creare il nuovo gruppo di risultati",
					(Exception) e);
			throw new LogException("Impossibile creare il nuovo gruppo di risultati");
		}

	}

	/**
	 * Esegue il log di una risposta
	 */
	public void logResultImpl(BigDecimal prgValMassiva, BigDecimal prgMovimento, BigDecimal prgMovimentoApp,
			SourceBean result) throws LogException {
		super.logResultImpl(prgValidazioneMassiva, prgMovimento, prgMovimentoApp, result);
	}

	/**
	 * Metodo per il log dell'interruzione da parte dell'utente
	 */
	public void setStopUser() throws LogException {
		// Generazione array argomenti
		Object[] args = new Object[1];
		args[0] = this.prgValidazioneMassiva;
		// Esecuzione query
		try {
			Object queryRes = QueryExecutor.executeQuery("SET_STOP_UTENTE", args, "UPDATE", Values.DB_SIL_DATI);
			if (queryRes == null || !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
				throw new LogException("Impossibile eseguire il log dell'interruzione utente");
			}
		} catch (Exception e) {
			// Eseguo il log e rilancio l'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile eseguire il log dell'interruzione utente",
					(Exception) e);

			throw new LogException("Impossibile eseguire il log dell'interruzione utente");
		}
	}

	/**
	 * Metodo per il log dell'interruzione da parte dell'utente
	 */
	public void setStopUser(int contesto) throws LogException {
		switch (contesto) {
		case MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA:
			// Generazione array argomenti
			Object[] args = new Object[1];
			args[0] = this.prgValidazioneMassiva;
			// Esecuzione query
			try {
				Object queryRes = QueryExecutor.executeQuery("SET_STOP_VALIDAZIONE_MOBILITA_UTENTE", args, "UPDATE",
						Values.DB_SIL_DATI);
				if (queryRes == null || !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
					throw new LogException("Impossibile eseguire il log dell'interruzione utente");
				}
			} catch (Exception e) {
				// Eseguo il log e rilancio l'eccezione
				it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile eseguire il log dell'interruzione utente",
						(Exception) e);

				throw new LogException("Impossibile eseguire il log dell'interruzione utente");
			}
		}
	}

	/**
	 * Metodo per il recupero del progressivo della validazione massiva
	 */
	public BigDecimal getPrgValidazioneMassiva() {
		return prgValidazioneMassiva;
	}
}