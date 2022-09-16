package it.eng.sil.module.movimenti;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.afExt.utils.FileUtils;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;

/**
 * Oggetto per il log su un FILE DI LOG GENERICO della validazione massiva dei movimenti.
 * 
 * @author Luigi Antenucci
 */
public abstract class FileResultLogger extends ResultLogger {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(FileResultLogger.class.getName());
	protected String className = StringUtils.getClassName(this);

	private final String FILE_NAME_PREFIX = "valid";
	private final String FILE_NAME_TIME_PATTERN = "_yyyyMMdd_HHmmssSSS";

	private String _fileName = null; // nome del file (per prima definizione)
	private String _pathFileName = null; // nome del path (per prima
											// definizione)

	private FileWriter fw;
	private BufferedWriter bw;
	private PrintWriter pw; // File su cui scrivere!
	private boolean opened = false; // F=chiuso; T=aperto

	/**
	 * Rende il solo suffisso di ESTENSIONE (escluso del carattere di punto) per il nome del file (vedi getFileName). E'
	 * "protected" poiché può essere ridefinita nelle sottoclassi.
	 */
	protected String getFileExt() {
		return "$$$";
	}

	/**
	 * Rende il nome del file di LOG da usare. E' "protected" poiché può essere ridefinita nelle sottoclassi
	 */
	protected String getFileName() {

		if (_fileName == null) {

			DateFormat dateFormat = DateFormat.getDateInstance();
			if (dateFormat instanceof SimpleDateFormat) {
				((SimpleDateFormat) dateFormat).applyPattern(FILE_NAME_TIME_PATTERN);
			}
			String dateStr = dateFormat.format(new Date());

			_fileName = FILE_NAME_PREFIX + dateStr;
		}
		return _fileName;
	}

	/**
	 * Rende il nome del file di LOG da usare E' "protected" poiché può essere ridefinita nelle sottoclassi
	 */
	protected String getPathFileName() {

		if (_pathFileName == null) {

			// NON HO PIU' UN PERCORSO FISSO:
			// String pathName = FileUtils.appendFileSeparator(
			// ConfigSingleton.getRootPath() ) +
			// RELATIVE_PATH_NAME;
			// ORA RECUPERO IL PATH DAL DATABASE:
			DataConnection connection = null;
			SQLCommand sqlCommand = null;
			DataResult dataResult = null;

			try {
				String pool = Values.DB_SIL_DATI;
				DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
				connection = dataConnectionManager.getConnection(pool);

				String sqlStatoSelect = SQLStatements.getStatement("GET_PATH_LOG_VALIDAZIONE");

				sqlCommand = connection.createSelectCommand(sqlStatoSelect);
				dataResult = sqlCommand.execute();
				ScrollableDataResult sdr = (ScrollableDataResult) dataResult.getDataObject();
				if (!sdr.hasRows()) {
					throw new Exception("Nessuna riga recuperata!");
				}

				SourceBean row = sdr.getDataRow().getSourceBean();
				String pathName = SourceBeanUtils.getAttrStrNotNull(row, "STRCONTESTOVALIDAZIONE");

				if (StringUtils.isFilled(pathName)) {

					pathName = FileUtils.appendFileSeparator(pathName);
					FileUtils.checkExistsPath(pathName); // se non esiste
															// lancia eccezione

					// CREO IL NOME DEL PATH+FILE CHE VERRA' USATO D'ORA IN POI
					String name = getFileName();
					String ext = "." + getFileExt();
					File file = FileUtils.createUniqueFileName(pathName, name, ext); // se
																						// non
																						// lo
																						// crea,
																						// lancia
																						// eccezione
					_pathFileName = file.getAbsolutePath(); // ASSEGNAZIONE!

					_logger.debug("USO IL FILE DI LOG [" + _pathFileName + "]");

				}
			} catch (Exception ex) {
				_logger.debug("IMPOSSIBILE CREARE IL FILE DI LOG (eccezione '" + ex.getMessage() + "')");

			} finally {
				Utils.releaseResources(connection, sqlCommand, dataResult);
			}
		}

		return _pathFileName;
	}

	/**
	 * Apre il file di log (se già aperto non fa nulla!)
	 */
	private void openFile() throws IOException {

		if (opened)
			return;

		String pathFileName = getPathFileName();

		if (pathFileName == null)
			return; // ignoro apertura se non so dove scrivere i dati

		try {
			// Apro OUTPUT in append
			fw = new FileWriter(pathFileName, true); // false=overwrite;
														// true=append
			bw = new BufferedWriter(fw);
			pw = new PrintWriter(bw);
			opened = true;
		} catch (IOException ex) {
			if (pw != null) {
				try {
					pw.close();
				} catch (Exception e) {
				}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
				}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (Exception e) {
				}
			}
			throw ex;
		}
	}

	/**
	 * Chiude il file di log (se mai aperto non fa nulla!)
	 */
	private final void closeFile() {

		if (opened) {
			if (pw != null) {
				try {
					pw.close();
				} catch (Exception e) {
				}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
				}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (Exception e) {
				}
			}
			opened = false;
		}
	}

	/**
	 * Sulla deallocazione dell'oggetto, chiudo il file (se e' ancora aperto)
	 */
	protected void finalize() throws Throwable {
		try {
			closeFile();
		} finally {
			super.finalize();
		}
	}

	/**
	 * Rende lo stato di apertura/chiusura del file.
	 */
	private boolean isFileOpen() {
		return opened;
	}

	/**
	 * Scrive una stringa di messaggio sul file. Se il file non è aperto lo apre. può lanciare eccezione se non riesce a
	 * aprirlo o a scriverci sopra.
	 */
	private void writeToFile(String message) throws IOException, LogException {

		if (!opened) {
			openFile();
			// Se non riesco a aprirlo, esco senza fare il log
			if (!opened) {
				throw new LogException("Impossibile scrivere il log sul file");
			}
		}
		// SCRIVO
		pw.println(message);
		pw.flush();
	}

	/**
	 * Simile alla writeToFile ma lancia una LogException. In realtà APRE il file, SCRIVE (in append) e poi lo CHIUDE.
	 * Questo poiché il "ResultLogger" non prevede apertura e chiusura risorse.
	 */
	private void writeOnceToFile(String message) throws LogException {

		try {
			openFile(); // lancia LogException se non lo apre
			writeToFile(message);
			closeFile();
		} catch (LogException le) {
			throw le;
		} catch (IOException ioe) {
			throw new LogException("Impossibile scrivere il log sul file '" + getPathFileName()
					+ "' - (IOException message='" + ioe.getMessage() + "')");
		}
	}

	/**
	 * Esegue il logging sul file
	 */
	public final void logResultImpl(BigDecimal prgValidazioneMassiva, BigDecimal prgMovimento,
			BigDecimal prgMovimentoApp, SourceBean result) throws LogException {

		// Creo e scrivo il messaggio
		writeOnceToFile(getResultMessage(result));
	}

	public final void logResultImplGen(BigDecimal prgValidazioneMassiva, BigDecimal prgID, BigDecimal prgIDApp,
			SourceBean result, int contesto) throws LogException {
	}

	public final void logResultImplGenCopiaProsp(BigDecimal prgValidazioneMassiva, String codRisultato,
			String descRisultato, BigDecimal prgIDNew, BigDecimal prgID, SourceBean result) throws LogException {
	}

	public final void setStopUser() throws LogException {

		// Scrivo il messaggio di stop
		writeOnceToFile(getStopMessage());
	}

	public final void setStopUser(int contesto) throws LogException {
	}

	/**
	 * Dal SourceBean del risultato della validazione (resultSB) crea una stringa contenente le info di LOG e la rende.
	 * può essere fatto l'OVERLOAD nelle sottoclassi per ridefinire cosa stampare e come.
	 */
	protected abstract String getResultMessage(SourceBean resultSB);

	/**
	 * Rende il messaggio per lo STOP d'utente.
	 */
	protected abstract String getStopMessage();

}