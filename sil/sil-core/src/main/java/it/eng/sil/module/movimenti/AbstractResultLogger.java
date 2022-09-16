/*
 * Creato il 4-nov-04
 */
package it.eng.sil.module.movimenti;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import oracle.sql.BLOB;

/**
 * Oggetto astratto che mette a fattor comune alcune funzionalità per i ResultLogger
 * <p/>
 * 
 * @author roccetti
 */
public abstract class AbstractResultLogger extends ResultLogger {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractResultLogger.class.getName());

	/** Codice utente */
	private BigDecimal user = null;

	// Costruttore per le sottoclassi
	protected AbstractResultLogger(BigDecimal user) {
		this.user = user;
	}

	/**
	 * Esegue il log di una risposta
	 */
	public void logResultImplGen(BigDecimal prgValidazioneMassiva, BigDecimal prgID, BigDecimal prgIDApp,
			SourceBean result, int contesto) throws LogException {
		switch (contesto) {
		case MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA:
			// Recupero nuova sequence
			Vector risultati = (result != null) ? result.getAttributeAsVector("RECORD.PROCESSOR") : null;
			if (risultati != null) {
				if (prgID != null)
					prgIDApp = null;
				SourceBean risProcessor = null;
				SourceBean warningProcessor = null;
				SourceBean errorProcessor = null;
				Vector vettWarningProcessor = null;
				Vector vettErrorProcessor = null;
				boolean isError = false;
				String codErrore = "";
				String strDettaglio = "";
				BigDecimal prgRisultato = null;
				for (int i = 0; i < risultati.size(); i++) {
					risProcessor = (SourceBean) risultati.get(i);
					isError = risProcessor.containsAttribute("RESULT")
							&& risProcessor.getAttribute("RESULT").toString().equalsIgnoreCase("ERROR");
					vettWarningProcessor = risProcessor.getAttributeAsVector("WARNING");
					for (int j = 0; j < vettWarningProcessor.size(); j++) {
						warningProcessor = (SourceBean) vettWarningProcessor.get(j);
						codErrore = warningProcessor.getAttribute("CODE").toString();
						strDettaglio = warningProcessor.containsAttribute("DETTAGLIO")
								? warningProcessor.getAttribute("DETTAGLIO").toString()
								: "";
						prgRisultato = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_AM_MOBILITA_RIS_DETT");
						if (prgRisultato == null)
							throw new LogException("Impossibile ottenere un progressivo per il log del risultato");
						// Generazione array argomenti
						Object[] args = new Object[9];
						args[0] = prgRisultato;
						args[1] = prgValidazioneMassiva;
						args[2] = prgID;
						args[3] = prgIDApp;
						args[4] = codErrore;
						args[5] = "00000000";
						args[6] = user;
						args[7] = user;
						args[8] = strDettaglio;
						TransactionQueryExecutor trans = null;
						// Esecuzione query
						try {
							trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
							trans.initTransaction();
							Object queryRes = trans.executeQuery("SET_RESULT_VAL_MASS_MOBILITA", args, "INSERT");
							if (queryRes == null
									|| !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
								throw new LogException("Impossibile eseguire il log del risultato");
							}
							trans.commitTransaction();
						} catch (Exception e) {
							if (trans != null) {
								try {
									trans.rollBackTransaction();
								} catch (Exception ex) {
								}
							}
							// Eseguo il log e rilancio l'eccezione
							it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile eseguire il log del risultato",
									(Exception) e);

							// throw new LogException("Impossibile eseguire il
							// log del risultato");
						}
					}
					if (isError) {
						vettErrorProcessor = risProcessor.getAttributeAsVector("ERROR");
						for (int j = 0; j < vettErrorProcessor.size(); j++) {
							errorProcessor = (SourceBean) vettErrorProcessor.get(j);
							codErrore = errorProcessor.getAttribute("CODE").toString();
							strDettaglio = errorProcessor.containsAttribute("DETTAGLIO")
									? errorProcessor.getAttribute("DETTAGLIO").toString()
									: "";
							prgRisultato = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_AM_MOBILITA_RIS_DETT");
							if (prgRisultato == null)
								throw new LogException("Impossibile ottenere un progressivo per il log del risultato");
							// Generazione array argomenti
							Object[] args = new Object[9];
							args[0] = prgRisultato;
							args[1] = prgValidazioneMassiva;
							args[2] = prgID;
							args[3] = prgIDApp;
							args[4] = codErrore;
							args[5] = "00000001";
							args[6] = user;
							args[7] = user;
							args[8] = strDettaglio;
							TransactionQueryExecutor trans = null;
							// Esecuzione query
							try {
								trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
								trans.initTransaction();
								Object queryRes = trans.executeQuery("SET_RESULT_VAL_MASS_MOBILITA", args, "INSERT");
								if (queryRes == null || !(queryRes instanceof Boolean
										&& ((Boolean) queryRes).booleanValue() == true)) {
									throw new LogException("Impossibile eseguire il log del risultato");
								}
								trans.commitTransaction();
							} catch (Exception e) {
								if (trans != null) {
									try {
										trans.rollBackTransaction();
									} catch (Exception ex) {
									}
								}
								// Eseguo il log e rilancio l'eccezione
								it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile eseguire il log del risultato",
										(Exception) e);

								// throw new LogException("Impossibile eseguire
								// il log del risultato");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Esegue il log di una risposta
	 */
	public void logResultImpl(BigDecimal prgValidazioneMassiva, BigDecimal prgMovimento, BigDecimal prgMovimentoApp,
			SourceBean result) throws LogException {
		// Recupero nuova sequence
		BigDecimal prgRisultato = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_AM_RISULTATO_MOVIMENTO");
		if (prgRisultato == null)
			throw new LogException("Impossibile ottenere un progressivo per" + " il log del risultato");
		// Recupero eventuale codice errore
		String codiceErrore = null;
		Vector listaProcessor = result.getAttributeAsVector("RECORD.PROCESSOR");
		if (listaProcessor != null && listaProcessor.size() > 0) {
			for (int i = 0; i < listaProcessor.size(); i++) {
				SourceBean processorCurrent = (SourceBean) listaProcessor.get(i);
				String codErroreCurr = processorCurrent.containsAttribute("ERROR.code")
						? processorCurrent.getAttribute("ERROR.code").toString()
						: null;
				if (codErroreCurr != null) {
					codiceErrore = codErroreCurr;
					break;
				}
			}
		}
		// Generazione array argomenti
		Object[] args = new Object[6];
		args[0] = prgRisultato;
		args[1] = prgValidazioneMassiva;
		args[2] = prgMovimento;
		args[3] = prgMovimentoApp;
		args[4] = user;
		args[5] = codiceErrore;
		// Esecuzione query
		TransactionQueryExecutor trans = null;
		try {
			// 12/06/2008 savino: FASE 2 trasferimento ramo azienda
			// questo metodo viene chiamato all'interno di una transazione nella quale fa parte anche l'importazione del
			// movimento
			// nella tabella di appoggio, per cui e' necessario usare la stessa transazione altrimenti il movimento
			// nella
			// am_movimento_appoggio non verrebbe trovato.
			// ------------- SOLO NEL CASO DI IMPORTAZIONE COMUNICAZIONE OBBLIGATORIA DA WEB SERVICE TRASFERIMENTO RAMO
			// AZIENDA
			// L'OGGETTO MULTIPLE TRANSACTION QUERY EXECUTOR WRAPPER E' PRESENTE NEL REQUEST CONTAINER
			trans = (TransactionQueryExecutor) RequestContainer.getRequestContainer().getAttribute("TQE_OBJECT");
			if (trans == null) {
				trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				trans.initTransaction();
			}

			Object queryRes = trans.executeQuery("SET_RESULT", args, "INSERT");
			if (queryRes == null || !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
				throw new LogException("Impossibile eseguire il log del risultato");
			}
			// Eseguo l'inserimento del risultato nel BLOB
			writeBLOB(prgRisultato, result, trans);
			trans.commitTransaction();
		} catch (Exception e) {
			if (trans != null) {
				try {
					trans.rollBackTransaction();
				} catch (Exception ex) {
				}
			}
			// Eseguo il log e rilancio l'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile eseguire il log del risultato", (Exception) e);

			throw new LogException("Impossibile eseguire il log del risultato");
		}
		// NON PIU': return prgRisultato;
	}

	public void logResultImplGenCopiaProsp(BigDecimal prgValidazioneMassiva, String codRisultato, String descRisultato,
			BigDecimal prgIDNew, BigDecimal prgID, SourceBean result) throws LogException {

		// Recupero progressivo dalla sequence
		BigDecimal prgRisultato = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_CM_PI_RISULTATO_COPIE");
		if (prgRisultato == null) {
			throw new LogException("Impossibile ottenere un progressivo per il log del risultato");
		}

		// Generazione array argomenti
		Object[] args = new Object[7];
		args[0] = prgRisultato;
		args[1] = prgValidazioneMassiva;
		args[2] = prgID;
		args[3] = prgIDNew;
		args[4] = codRisultato;
		args[5] = descRisultato;
		args[6] = user;
		TransactionQueryExecutor trans = null;
		// Esecuzione query
		try {
			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			trans.initTransaction();
			Object queryRes = trans.executeQuery("SET_RESULT_DETTAGLIO_COPIA_PROSPETTO", args, "INSERT");
			if (queryRes == null || !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
				throw new LogException("Impossibile eseguire il log del risultato");
			}
			trans.commitTransaction();
		} catch (Exception e) {
			if (trans != null) {
				try {
					trans.rollBackTransaction();
				} catch (Exception ex) {
				}
			}
			// Eseguo il log
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile eseguire il log del risultato", (Exception) e);
		}
	}

	/**
	 * Metodo che deve essere implementato nelle sottoclassi, a seconda del tipo di logger
	 */
	public abstract void setStopUser() throws LogException;

	public abstract void setStopUser(int contesto) throws LogException;

	/**
	 * Metodo che dato un progressivo di un risultato scrive nel campo BLOB associato i dati contenuti nel SourceBean
	 * passato come seconda argomento
	 */
	protected void writeBLOB(BigDecimal prgRisultato, SourceBean result, TransactionQueryExecutor trans)
			throws LogException {

		// Creazione connessione
		DataConnection conn = trans.getDataConnection();

		// Creazione Statement
		String stmt = SQLStatements.getStatement("WRITE_BLOB_RISULTATO");
		SQLCommand selectCommand = conn.createSelectCommand(stmt);
		ArrayList inputParameters = new ArrayList(1);
		inputParameters.add(conn.createDataField("prgRisultato", Types.BIGINT, prgRisultato));

		try {
			// Esecuzione query
			DataResult dr = selectCommand.execute(inputParameters);

			// Recupero puntatore OutputStream per scrittura BLOB
			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
			DataField df = sdr.getDataRow().getColumn("BLBRISULTATO");
			BLOB resultBlob = (BLOB) df.getObjectValue();
			OutputStream outStream = resultBlob.getBinaryOutputStream();

			// Scrittura del SourceBean nel BLOB
			byte[] byteRes = result.toXML(false).getBytes();
			int chunk = resultBlob.getChunkSize();
			for (int i = 0; i * chunk < byteRes.length; i++) {
				int chunkLength = ((i + 1) * chunk < byteRes.length ? chunk : byteRes.length - (i * chunk));
				byte[] b = new byte[chunkLength];
				for (int j = 0; j < chunkLength; j++) {
					b[j] = byteRes[(i * chunk) + j];
				}
				outStream.write(b, 0, chunkLength);
			}
			outStream.flush();
			outStream.close();
			return;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile scrivere il log del risultato", e);

			throw new LogException("Impossibile scrivere il log del risultato");
		}
	}
}