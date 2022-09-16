package it.eng.sil.util.amministrazione.impatti;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.inet.report.Engine;

import it.eng.afExt.utils.FileUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.bean.Documento;
import it.eng.sil.util.ConnessionePerReport;

/**
 * OUTPUT: Parte di gestione dell'output relativa all'"EsportaMigrazioni"
 * 
 * NB: la classe NON E' "PUBLIC" ma è visibile solo all'interno del pacchetto poiché viene usata dalla classe
 * EsportaMigrazioni.
 * 
 * @author Luigi Antenucci
 */
class EsportaMigrazioniOutput implements Serializable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EsportaMigrazioniOutput.class.getName());

	private static final String thisClassName = StringUtils.getClassName(EsportaMigrazioniOutput.class);

	private static final String PREFIX_FILE = "T";
	private static final String PREFIX_SUBDIR = "T";

	private static final String OUT_ON_TXT = "T";
	private static final String OUT_ON_PDF = "P";
	private static final String OUT_ON_TUTTI = "E";

	/** Variabili di configurazione ricevute da EsportaMigrazioni. */
	private String savetoPath;
	private boolean appendToTxtFile;
	private Date adesso;
	private String dqmNGiorni;
	private boolean useDefaultLog;
	private boolean statusToConsole; // riepilogo su console

	/** Riferimento al transposer in uso */
	private EsportaMigrazioniTrans emTrans;

	/** Nome (completo) del report da usare come template dei file PDF generati */
	private String engineReportFile;

	/** Variabile con il CPI dato all'inizializzazione (valido fino a fine ciclo) */
	private transient EsportaMigrazioni.CpiElem cpiElem;

	/**
	 * Variabili per ricordarsi (tra apertura e chiusura) su cosa si sta scrivendo
	 */
	private transient boolean outOnTxt;

	/** Variabile per mantenere il file su cui scrivere i record. */
	private transient PrintWriter fileTxt;

	/**
	 * COSTRUTTORE
	 */
	public EsportaMigrazioniOutput(String savetoPath, boolean appendToTxtFile, Date adesso, String transposerFile,
			String reportFile, boolean useDefaultLog, boolean statusToConsole)
			throws FileNotFoundException, SAXException {
		this.savetoPath = savetoPath;
		this.appendToTxtFile = appendToTxtFile;
		this.useDefaultLog = useDefaultLog;
		this.statusToConsole = statusToConsole;

		outOnTxt = false;

		this.adesso = adesso;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(adesso);

		int intNGiorni = calendar.get(Calendar.DAY_OF_YEAR);
		this.dqmNGiorni = String.valueOf(intNGiorni);
		if (intNGiorni <= 9)
			this.dqmNGiorni = "0" + this.dqmNGiorni;
		if (intNGiorni <= 99)
			this.dqmNGiorni = "0" + this.dqmNGiorni;

		this.engineReportFile = "file:" + FileUtils.appendFileSeparator(ConfigSingleton.getRootPath())
				+ "\\WEB-INF\\report\\" + reportFile;
		try {
			emTrans = new EsportaMigrazioniTrans(transposerFile);
		} catch (FileNotFoundException fex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					this.getClass().getName() + "::service " + "File non trovato: " + transposerFile, fex);

			throw new FileNotFoundException("File non trovato: " + transposerFile);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service "
					+ "Errore durante il caricamento del file di configurazione: " + transposerFile, ex);

			throw new SAXException(ex.getMessage());
		}
	}

	/**
	 * Inizializza l'oggetto sulla base del CPI passato. Tutte le azioni successive usano i dati del CPI.
	 */
	public void initForCpi(EsportaMigrazioni.CpiElem cpiElem) {

		this.cpiElem = cpiElem;
	}

	/**
	 * Prendo dal CPI una stringa di caratteri. Avendo la stringa "123456789" (il CPI è di 9 caratteri), rende la
	 * stringa che comincia col carattere di posizione daQuale per una lunghezza di perQuanti. Es. daQuale=7 e
	 * perQuanti=3 rende "789"
	 */
	private String getCodeFromCpi(String xxxCpi, int daQuale, int perQuanti) {
		if (xxxCpi == null)
			return null;

		int lung = xxxCpi.length();
		int begin = daQuale - 1;
		if ((begin >= 0) && (begin < lung)) {

			int end = begin + perQuanti;
			if (end > lung)
				end = lung; // al max a fine stringa

			return xxxCpi.substring(begin, end);
		} else
			return "";
	}

	/**
	 * Rende il nome della directory di output in base alla chiave passata. Esempio di nome reso: "TBO100" (T + BO +
	 * 100)
	 */
	private String getDirectoryName(String xxxProv, String xxxCpi) {

		// VERSIONE PRIMA DEL 29-03-05:
		// Prendo dal CPI i caratteri "789" della stringa "123456789".
		// return PREFIX_SUBDIR + xxxProv + getCodeFromCpi(xxxCpi, 7, 3);

		// GG 29-03-05 - Modifica definitiva alla regola di generazione dei nomi
		// dir e file:
		// Prendo dal CPI i caratteri "6789" della stringa "123456789".
		return PREFIX_SUBDIR + xxxProv + getCodeFromCpi(xxxCpi, 6, 4);
	}

	/**
	 * Rende il nome del file TXT di output in base alla chiave passata. Esempio di nome reso: "TBO10116.TXT" (T + BO +
	 * 10 + 116) [10 := 100 senza ultimo car]
	 */
	private String getFileName(String xxxProv, String xxxCpi) {

		// VERSIONE PRIMA DEL 22/12/2004:
		// Prendo dal CPI i caratteri "78" della stringa "123456789".
		// return PREFIX_FILE + xxxProv + getCodeFromCpi(xxxCpi, 7, 2) +
		// dqmNGiorni;

		// GG 22/12/2004:
		// Prendo dal CPI i caratteri "67" della stringa "123456789".
		return PREFIX_FILE + xxxProv + getCodeFromCpi(xxxCpi, 6, 2) + dqmNGiorni;
	}

	/**
	 * Apre il file di testo di output. Se già aperto, lo chiude.
	 */
	public void openOutputFileTxt() throws IOException {

		if (!isEnabledTxt())
			return;

		// DEFINIZIONE DELLA DIRECTORY DI OUTPUT (con radice "savetoPath")
		String xxxProv = cpiElem.getSiglaProvCpi();
		String xxxCpi = cpiElem.getCodCpi();

		// Prendo directory di output (a cui aggiungo barra finale)
		String outputPath = getDirectoryName(xxxProv, xxxCpi) + File.separatorChar;
		String outputPathAbsolute = savetoPath + outputPath;

		// Creazione della directory se non esiste
		FileUtils.creaPathSeNonEsiste(outputPathAbsolute);

		// DEFINIZIONE DEL FILE TXT DI OUTPUT
		String outputFile = getFileName(xxxProv, xxxCpi) + ".TXT";
		String outputPathFile = outputPathAbsolute + outputFile;

		writeLog("Esportazione dati sul file di testo [" + outputPath + outputFile + "]");
		writeOut(outputPath + outputFile + "\t[");

		// APERTURA (IN SCRITTURA) DEL NUOVO FILE DI TESTO
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(outputPathFile, appendToTxtFile); // false=overwrite
																	// (true=append)
			bw = new BufferedWriter(fw);
			fileTxt = new PrintWriter(bw);
			outOnTxt = true;
			// writeLog("File di testo aperto correttamente");
		} catch (IOException ioe) {
			fileTxt = null;
			outOnTxt = false; // annullo apertura!

			// Chiudo eventuali stream aperti
			// (NB: *non* in FINALLY perché non li devo chiudere se va tutto
			// bene).
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception ignored) {
				}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (Exception ignored) {
				}
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service "
					+ "ERRORE !!! NON POSSO SCRIVERE SUL FILE DI TESTO [" + outputPathFile + "]", ioe);

			throw ioe;
		}
	}

	/**
	 * Esegue l'esportazione di UNA singola migrazione. Viene chiamata enne volte dalla "doGeneraTxtDaAppoggioPerCpi()".
	 */
	public void appendOutputFileTxt(SourceBean row) throws Exception {

		if (!outOnTxt)
			return;

		try {
			// Eseguo transposizione: da ROW a STRINGA-COMPOSTA
			String rowTrans = emTrans.transpose(row);
			// può generare eccezione in caso di errore

			// Scrittura linea di dati su file.
			fileTxt.print(rowTrans);
			fileTxt.print('\r'); // \r = 0Dh = return
			fileTxt.print('\n'); // \n = 0Ah = newline

			// Controllo errore (e stampa in riassunto)
			String warning = emTrans.getLastTransposeObblFieldsWarning();
			if (StringUtils.isEmpty(warning)) {
				writeOut("o");
			} else {
				writeOut("w");
				writeLog("   ATTENZIONE: seguenti CAMPI OBBLIGATORI non sono valorizzati: " + warning);
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service ", e);

			writeOut("x");
			throw e;
		}
	}

	/**
	 * Chiude il file di testo di output (se aperto).
	 */
	public void closeOutputFileTxt() {

		if (!outOnTxt)
			return;

		writeOut("]\n");
		try {
			fileTxt.flush();
			fileTxt.close();
			fileTxt = null;
			outOnTxt = false;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service "
					+ "ERRORE !!! Non ho potuto chiudere correttamente il file di testo di output", e);

		}
	}

	/**
	 * Esegue la generazione completa del report relativo a TUTTE le migrazioni del cpi impostato (vedi "initForCpi").
	 * Viene chiamata UNA sola volta dalla "doGeneraPdfDaAppoggioPerCpi()". NB: se "isEnabledPdf()" vale FALSE, non
	 * viene fatto nulla! PS: viene usata la connessione passata!
	 */
	public void generateFullOutputFilePdf(DataConnection connection) throws Exception {

		if (!isEnabledPdf())
			return;

		// DEFINIZIONE DELLA DIRECTORY DI OUTPUT (con radice "savetoPath")
		String xxxProv = cpiElem.getSiglaProvCpi();
		String xxxCpi = cpiElem.getCodCpi();

		// Prendo directory di output (a cui aggiungo barra finale)
		String outputPath = getDirectoryName(xxxProv, xxxCpi) + File.separatorChar;
		String outputPathAbsolute = savetoPath + outputPath;

		// Creazione della directory se non esiste
		FileUtils.creaPathSeNonEsiste(outputPathAbsolute);

		// DEFINIZIONE DEL FILE TXT DI OUTPUT
		String outputFile = getFileName(xxxProv, xxxCpi) + ".PDF";
		String outputPathFile = outputPathAbsolute + outputFile;

		writeLog("Esportazione dati sul file PDF [" + outputPath + outputFile + "]");

		try {
			Engine eng;
			try {
				eng = new Engine(Engine.EXPORT_PDF); // Set the export format
														// in PDF
				eng.setReportFile(engineReportFile);
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						this.getClass().getName() + "::service " + "Errore recuperando il file template RPT", e);

				throw e;
			}

			// Imposto manualmente la connessione da usare
			try {
				// DataConnection connection =
				// com.engiweb.framework.dbaccess.DataConnectionManager.getInstance().getConnection();
				Connection intConnection = connection.getInternalConnection();
				ConnessionePerReport silConnection = new ConnessionePerReport(intConnection);

				eng.setConnection(silConnection);
				for (int i = 0; i < eng.getSubReportCount(); i++) {
					eng.getSubReport(i).setConnection(silConnection);
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						this.getClass().getName() + "::service " + "Errore assegnando la connessione all'engine", e);

				throw e;
			}

			// Assegno codice del CPI da usare per estrazione dati da tabella
			// appoggio
			eng.setPrompt(cpiElem.getCodCpi(), 0);

			// GG 26-01-05: sostituisco dinamicamente le immagini "segnaposto"
			// nel report.
			Documento.sostituisciImmaginiSegnaposto(eng);

			// Generazione del report (in memoria)
			eng.execute();

			// Scrittura del report su disco
			FileOutputStream fos = null;
			try {
				File pdfFile = new File(outputPathFile);
				fos = new FileOutputStream(pdfFile);
				for (int p = 1; p <= eng.getPageCount(); p++) {
					fos.write(eng.getPageData(p));
				}
			} catch (IOException e) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						this.getClass().getName() + "::service " + "Errore scrivendo su file il report generato", e);

				throw e;
			} finally {
				if (fos != null)
					fos.close();
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					this.getClass().getName() + "::service " + "Errore generando il PDF", e);

			throw e;
		}
	}

	private void writeLog(String str) {
		if (useDefaultLog) {
			_logger.debug(str);

		}
	}

	private void writeOut(String str) {
		if (statusToConsole) {
			System.out.print(str);
		}
	}

	/**
	 * Rende TRUE se per il CPI di riferimento - vedi "initForCpi" - è impostata la generazione del file di TXT. Il
	 * valore reso dipende solo dalla configurazione del CPI e non dallo stato corrente del file.
	 */
	public boolean isEnabledTxt() {
		String codTipoFile = cpiElem.getCodTipoFile();
		boolean isDefinedTxt = codTipoFile.equalsIgnoreCase(OUT_ON_TXT) || codTipoFile.equalsIgnoreCase(OUT_ON_TUTTI);
		String email = cpiElem.getEmail();
		return isDefinedTxt && StringUtils.isFilled(email);
	}

	/**
	 * Simile alla "isEnabledTxt" ma per il file PDF.
	 */
	public boolean isEnabledPdf() {
		String codTipoFile = cpiElem.getCodTipoFile();
		boolean isDefinedPdf = codTipoFile.equalsIgnoreCase(OUT_ON_PDF) || codTipoFile.equalsIgnoreCase(OUT_ON_TUTTI);
		boolean isDefinedTxt = codTipoFile.equalsIgnoreCase(OUT_ON_TXT) || codTipoFile.equalsIgnoreCase(OUT_ON_TUTTI);
		String email = cpiElem.getEmail();
		return isDefinedPdf || // NOTA: ho in OR il controllo se è abilitato
								// TXT ma NON ha l'email
				(isDefinedTxt && StringUtils.isEmpty(email));

	}

}