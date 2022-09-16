/*
 * Creato il 6-ago-04
 */
package it.eng.sil.bean.importazioneMovimenti;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.sil.Values;
import it.eng.sil.module.movimenti.Importer;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordExtractor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.TXTExtractor;
import it.eng.sil.module.movimenti.processors.IdentificaMovimento;
import it.eng.sil.module.movimenti.processors.InsertAvvPerCVE;
import it.eng.sil.module.movimenti.processors.InsertData;
import it.eng.sil.module.movimenti.processors.SelectFieldsMovimentoAppoggio;
import it.eng.sil.module.movimenti.processors.TransformCodTempo;
import it.eng.sil.util.batch.BatchRunnable;

/**
 * @author roccetti
 * 
 *         Demone che esegue l'importazione dei movimenti secondo il tracciato SARE da una directory specificata nel
 *         file di configurazione ImportazioneMovimenti.xml
 * 
 */
public class DemoneImportazione implements BatchRunnable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DemoneImportazione.class.getName());

	private String urlOrigine = null; // directory di origine
	private String urlProcessati = null; // directory di destinazione
	private String urlNonImportati = null; // directory per i file non
											// processati dall'importer
	private String urlErrati = null; // directory per i MOVIMENTI errati
	private String sep = ", ";

	private SARETrackFilesFilter filtro; // filtro per il reperimento dei
											// file prodotti nel tracciato SARE
	private ConfigSingleton configure;
	private String applicationName = "Demone Importazione "; // Nome
																// dell'applicazione
																// da usare nel
																// LOG
	private String version = "DEFAULT"; // Indica la versione del tracciato SARE
										// da utilizzare per l'estrattore

	// Anche questo può essere specificato nel Signleton di configurazione
	// (come specificato nel file TXTRecordMapping.xml)

	/**
	 * COSTRUTTORE Legge il singleton di configurazione.
	 */
	public DemoneImportazione() throws InstantiationException {

		// System.out.println("\nDemoneImportazione:: START.......\n");
		configure = ConfigSingleton.getInstance();
		SourceBean ImportaMovimentiConfig = (SourceBean) configure.getAttribute("IMPORTAMOVIMENTI");
		if (ImportaMovimentiConfig == null) {
			_logger.debug("it.eng.sil.bean.ImportazioneMovimenti.DemoneImportazione: configurazione non trovata ");

			throw new InstantiationException("Configurazione non trovata");
		}

		// Recupero directory da cui prelevare i file
		urlOrigine = (String) ImportaMovimentiConfig.getAttribute("SOURCE.sourcePath");
		if (urlOrigine == null) {
			_logger.debug("it.eng.sil.bean.ImportazioneMovimenti.DemoneImportazione: "
					+ "configurazione della directory di origine non trovata, "
					+ "controllare l'attributo sourcePath dell'elemento SOURCE nel file XML di configurazione");

			throw new InstantiationException("Configurazione della directory di origine non trovata");
		}

		// Recupero directory in cui spostare i file importati
		urlProcessati = (String) ImportaMovimentiConfig.getAttribute("SOURCE.destinationPath");
		if (urlProcessati == null) {
			_logger.debug("it.eng.sil.bean.ImportazioneMovimenti.DemoneImportazione: "
					+ "configurazione della directory di destinazione non trovata, "
					+ "controllare l'attributo destinationPath dell'elemento SOURCE nel file XML di configurazione");

			throw new InstantiationException("Configurazione della directory di destinazione non trovata");
		}

		// Recupero directory in cui spostare i file non ancora importati
		urlNonImportati = (String) ImportaMovimentiConfig.getAttribute("SOURCE.notImportedPath");
		if (urlNonImportati == null) {
			_logger.debug("it.eng.sil.bean.ImportazioneMovimenti.DemoneImportazione: "
					+ "configurazione della directory di destinazione non trovata, "
					+ "controllare l'attributo destinationPath dell'elemento SOURCE nel file XML di configurazione");

			throw new InstantiationException("Configurazione della directory di destinazione non trovata");
		}

		urlErrati = (String) ImportaMovimentiConfig.getAttribute("SOURCE.erratiPath");
		if (urlErrati == null) {
			_logger.debug("it.eng.sil.bean.ImportazioneMovimenti.DemoneImportazione: "
					+ "configurazione della directory di destinazione non trovata, "
					+ "controllare l'attributo destinationPath dell'elemento SOURCE nel file XML di configurazione");

			throw new InstantiationException("Configurazione della directory di destinazione non trovata");
		}

		// Controllo che le due directory siano diverse
		if (urlOrigine.equalsIgnoreCase(urlProcessati)) {
			_logger.debug("it.eng.sil.bean.ImportazioneMovimenti.DemoneImportazione: "
					+ "configurazione della directory di destinazione errata, "
					+ "la directory di destinazione non può essere coincidente con quella di origine");

			throw new InstantiationException(
					"Configurazione della directory di destinazione " + "coincidente con quella di origine");
		}

		// Controllo che le due directory siano diverse
		if (urlOrigine.equalsIgnoreCase(urlNonImportati)) {
			_logger.debug("it.eng.sil.bean.ImportazioneMovimenti.DemoneImportazione: "
					+ "configurazione della directory di destinazione errata, "
					+ "la directory di destinazione non può essere coincidente con quella di origine");

			throw new InstantiationException("Configurazione della directory dei movimenti non importati "
					+ "coincidente con quella di origine");
		}

		// Controllo che le due directory siano diverse
		if (urlNonImportati.equalsIgnoreCase(urlProcessati)) {
			_logger.debug("it.eng.sil.bean.ImportazioneMovimenti.DemoneImportazione: "
					+ "configurazione della directory di destinazione errata, "
					+ "la directory di destinazione non può essere coincidente con quella di origine");

			throw new InstantiationException("Configurazione della directory  dei movimenti non importati "
					+ "coincidente con quella di destinazione");
		}

		// Settaggio versione da utilizzare
		String v = (String) ImportaMovimentiConfig.getAttribute("SOURCE.version");
		if (v != null && !v.equals("")) {
			version = v;
		}

		// Scrivo la configurazione sul log
		_logger.debug(" CONFIGURAZIONE DEL DEMONE DI IMPORTAZIONE: ");

		it.eng.sil.util.TraceWrapper.debug(_logger, "Importazione movimenti", ImportaMovimentiConfig);

	}

	// ***********************************
	// * FUNZIONE DI GESTIONE FILESYSTEM *
	// ***********************************
	/**
	 * listFiles Funzione di ausilio del thread del demone. Tramite il filtro implementato nella classe
	 * SARETrackFilesFilter. Il filtro permette di identificare i files che rispettano il tracciato SARE
	 * 
	 * @param directory
	 * @return vettore di nomi di file che rispettano il filtro impostato
	 * @throws IOException
	 */
	private String[] listFiles(String directory) throws IOException {

		File dir = new File(directory);
		if (!dir.isDirectory()) {
			_logger.debug(
					"it.eng.sil.bean.ImportazioneMovimenti.DemoneImportazione:" + ":listFiles: directory non valida ");

			throw new IllegalArgumentException("listFiles: directory non valida");
		}
		filtro = new SARETrackFilesFilter();
		return dir.list(filtro);
	}

	/**
	 * Sposta il contenuto del File temporaneo nella directory di destinazione
	 */
	private void moveFile(String fileName, File tempFile, String urlDiDestinazione) {

		// Apro il file di destinazione
		File destinationFile = new File(urlDiDestinazione + File.separator + fileName);

		// Controllo se il file di destinazione esiste già
		if (destinationFile.exists()) {
			_logger.debug("Il file di destinazione " + destinationFile.getName() + " esiste già! "
					+ "Il contenuto verrà inserito alla fine del file");

		} else {
			// Lo creo
			try {
				if (!destinationFile.createNewFile()) {
					_logger.debug("Impossibile creare il file " + destinationFile.getName());

					return;
				}
			} catch (IOException e) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"Impossibile creare il file " + destinationFile.getName() + ": " + e.getMessage(), e);

				return;
			}
		}

		// Copio i dati dal file temporaneo in quello di destinazione
		try {
			FileWriter dest = new FileWriter(urlDiDestinazione + File.separator + fileName, true);
			FileReader source = new FileReader(tempFile);
			char[] c = new char[1];
			while (source.read(c) != -1) {
				dest.write(c);
			}
			dest.close();
			source.close();
		} catch (IOException e) {
			_logger.debug("Impossibile copiare il file " + tempFile.getName() + ": " + e.getMessage());

			return;
		}

		tempFile.delete();
	}

	/**
	 * Estrae l'identificativo dell'utente che si occupa di eseguire l'importazione batch
	 */
	private BigDecimal getUserCode() {
		SQLCommand command = null;
		DataResult dr = null;
		BigDecimal usercode = null;
		DataConnectionManager dcm = null;
		DataConnection conn = null;

		try {

			// Prendo una connessione dal pool
			dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(Values.DB_SIL_DATI);

			// creo la query
			SourceBean statementSB = (SourceBean) configure.getFilteredSourceBeanAttribute("STATEMENTS.STATEMENT",
					"NAME", "GETUSERCODE_IMPORTMOV_BATCH");
			String statement = statementSB.getAttribute("QUERY").toString();
			command = conn.createSelectCommand(statement);
			ArrayList parameters = new ArrayList();

			// eseguo
			dr = command.execute(parameters);

			// Recupero risultato
			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
			SourceBean result = sdr.getSourceBean();
			usercode = (BigDecimal) result.getAttribute("ROW.CDNUT");

			sdr.close();

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "it.eng.sil.bean.ImportazioneMovimenti.DemoneImportazione: "
					+ ":getUserCode: eccezione durante il recupero del codice utente", e);

		} finally {
			Utils.releaseResources(conn, command, dr);
		}

		return usercode;
	}

	/**
	 * start
	 * 
	 * Procedura principale per l'esecuzione del demone. Cicla ogni minuto circa cercando, nella directory designata per
	 * lo scambio dei dati, i file prodotti da SARE. Ogni file trovato viene preso in carico e i movimenti contenuti
	 * vengono importati nel SIL quanto accade è tracciato nell'apposito log.
	 * 
	 */
	public void start() {
		Thread t = new Thread() {
			public void run() {
				String[] filenames = null;
				try {

					File fileCSV = new File(urlErrati + "\\MovimentiNonImportati.CSV");
					FileOutputStream outStream = null;
					PrintStream prtStr = null;

					while (true) {

						filenames = listFiles(urlOrigine);

						if (filenames.length > 0) {
							// Ho dei file da processare, apro la connessione e
							// creo gli oggetti per l'importazione

							outStream = new FileOutputStream(fileCSV);
							prtStr = new PrintStream(outStream);
							// Nome delle colonne del file CSV
							prtStr.println("Nome file" + sep + "Tipo mov" + sep + "Lavoratore" + sep + "Azienda" + sep
									+ "Ind. dell\'azienda" + sep);

							// Recupero il codice dell'utente di importazione
							// batch
							BigDecimal cdnut = getUserCode();
							if (cdnut == null) {
								_logger.debug(
										"Impossibile ottenere il codice utente da utilizzare per l'importazione.");

								return;
							}

							// MultipleTransactionQueryExecutor per le query
							MultipleTransactionQueryExecutor trans = new MultipleTransactionQueryExecutor(
									Values.DB_SIL_DATI);
							// System.out.println("# connession after NEW::" +
							// trans.numberOfConnection);

							// Istanzio l'importatore
							Importer importer = new Importer();

							// Creo il path dei file di configurazione
							String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
									+ File.separator + "conf" + File.separator + "import" + File.separator;

							// Istanzio e configuro i processori
							RecordProcessor procCodTempo = new TransformCodTempo("Trasforma_Tempo_Avviamento");
							RecordProcessor selectCorrectFields = new SelectFieldsMovimentoAppoggio(
									"Selezione_Dati_Movimento", "importazione_file_txt", trans);
							// RecordProcessor selectCodContr = new
							// SelectCodContratto("Seleziona_Codice_Contratto",
							// dataconn);
							String configproc = configbase + "processors" + File.separator;
							RecordProcessor insertMovApp;
							RecordProcessor insertAvvPerCve;
							try {
								insertMovApp = new InsertData("Inserisci_Movimento_Appoggio", trans,
										configproc + "InsertAM_MOVIMENTI_APPOGGIO.xml", "INSERT_MOVIMENTO_APPOGGIO",
										cdnut);
								insertAvvPerCve = new InsertAvvPerCVE("Inserisci_Avviamento_per_CVE", trans,
										configproc + "insertMovimentoAppPerCVE.xml", "INSERT_AVVIAMENTO_PER_CVE",
										cdnut);
							} catch (Exception e) {
								it.eng.sil.util.TraceWrapper.debug(_logger,
										"Impossibile istanziare i RecordProcessor per l'inserimento dei record", e);

								// chiudo la connessione del
								// MultipleTransactionQueryExecutor
								trans.closeConnection();
								System.out.println("# connession after EXCEPTION::" + trans.numberOfConnection);
								return;
							}

							// Li aggiungo all'importer
							importer.addProcessor(new IdentificaMovimento());
							importer.addProcessor(procCodTempo);
							importer.addProcessor(selectCorrectFields);
							// importer.addProcessor(selectCodContr);
							importer.addProcessor(insertAvvPerCve);
							importer.addProcessor(insertMovApp);

							// Ciclo sui files da importare
							for (int i = 0; i < filenames.length; i++) {

								// Rinomino il file sorgente
								File fileIn = new File(urlOrigine + File.separator + filenames[i]);

								File tempFile = new File(urlNonImportati + File.separator + filenames[i]);
								// Se il file temporaneo esiste già lo cancello
								if (tempFile.exists()) {
									tempFile.delete();
									_logger.debug(
											"File temporaneo già esistente " + tempFile.getName() + " cancellato");

								}
								// Con l'struzione seguente sposto il file
								// fileIn da dove si trova fileIn a dove ho
								// creato tempFile
								boolean renamed = fileIn.renameTo(tempFile);

								// moveFile(fileIn,urlNonImportati+"\\temp_" +
								// filenames[i]);
								// Se è andato tutto bene lo processo
								if (renamed) {

									// scrivo il log di inizio elaborazione del
									// file
									_logger.debug("Inizio elaborazione " + filenames[i]);

									// Istanzio l'estrattore
									FileInputStream input = new java.io.FileInputStream(tempFile);
									RecordExtractor extr;
									try {
										extr = new TXTExtractor(input, configbase + "TXTRecordMapping.xml", version);
									} catch (Exception e) {
										it.eng.sil.util.TraceWrapper.debug(_logger,
												"Impossibile istanziare l'estrattore per i record", e);

										// chiudo la connessione del
										// MultipleTransactionQueryExecutor
										trans.closeConnection();
										return;
									}

									// Imposto gli oggetti nell'importer
									importer.setRecordExtractor(extr);

									// Processo i records

									SourceBean result = importer.importRecords(trans);
									// Chiudo lo stream di input
									input.close();

									// copio il file nella directory di
									// destinazione e cancello il file originale
									// moveFile(filenames[i], tempFile,
									// urlProcessati);

									File procFile = null;
									if (result.containsAttribute("ERRORPROCESSED")) {
										Integer numMovErrati = (Integer) result.getAttribute("ERRORPROCESSED");
										if (numMovErrati.intValue() > 0) {
											procFile = new File(urlErrati + File.separator + filenames[i]);

											// Scrivo i dati del movimento nel
											// file CSV
											Vector vettIdMov = result.getAttributeAsVector("RECORD.IDMOV");
											for (int j = 0; j < vettIdMov.size(); j++) {
												SourceBean idMov = (SourceBean) vettIdMov.get(j);
												String codTipoMov = (String) idMov.getAttribute("codTipoMov");
												String nomeLav = (String) idMov.getAttribute("nomeLav");
												String cognomeLav = (String) idMov.getAttribute("cognomeLav");
												String indirizzoAzienda = (String) idMov
														.getAttribute("indirizzoAzienda");
												String ragSocAzienda = (String) idMov.getAttribute("ragSocAzienda");
												prtStr.println(tempFile.getName() + sep + codTipoMov + sep + nomeLav
														+ " - " + cognomeLav + sep + ragSocAzienda + sep
														+ indirizzoAzienda + sep);
											}

										} else {
											procFile = new File(urlProcessati + File.separator + filenames[i]);
										}
									} else {
										procFile = new File(urlProcessati + File.separator + filenames[i]);
									}

									// Se il file temporaneo esiste già lo
									// cancello
									if (procFile.exists()) {
										procFile.delete();
										_logger.debug(
												"File temporaneo già esistente " + tempFile.getName() + " cancellato");

									}
									// Con l'struzione seguente sposto il file
									// tempFile da dove si trova fileIn a dove
									// ho creato procFile
									renamed = tempFile.renameTo(procFile);

									// Scrivo il risultato della processazione
									// sul log
									// TracerSingleton.log(applicationName,
									// TracerSingleton.INFORMATION,
									// result.toXML(false,true));

									// Scrivo il log di fine elaborazione del
									// file

								} else {
									_logger.debug(
											"Errori nello spostamento del file, impossibile processare i record nel file "
													+ tempFile.getName());

								}
							}

							// Rilascio la connessione
							trans.closeConnection();
							// System.out.println("# connession after close::" +
							// trans.numberOfConnection);
						}
						sleep(60000);
					}
				} catch (Exception ex) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "Elaborazione fallita ", ex);

				}
			}
		};

		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
		_logger.debug("Demone partito (running)");

		try {
			t.join();
		} catch (InterruptedException ie) {
		}
		_logger.debug("Demone terminato (terminated)");

	}

}