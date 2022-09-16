/*
 * Creato il 17-mar-05
 * Author: rolfini
 * 
 */
package it.eng.sil.bean.SilProLabor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.InputSource;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

/**
 * @author rolfini
 * 
 */
public class FileSystemManager {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(FileSystemManager.class.getName());

	private ProlaborFilesFilter filtro; // filtro per il reperimento dei file
										// prodotti da ProLabor (finiscono per
										// P.XML)
	private String urlScambio;
	private String fileNameDest = ""; // memorizza il nome del file di
										// risposta che deve essere prodotto. Il
										// nome del file di risposta
	// è creato basandosi sul nome del file di input in processazione

	private String fileNameRenamed = "";

	/**
	 * 
	 */
	protected FileSystemManager(ConfigSingleton config) {
		SourceBean urlScambioConfig = (SourceBean) config.getFilteredSourceBeanAttribute("SILPROLABOR.DIR", "NAME",
				"SCAMBIO");
		this.urlScambio = (String) urlScambioConfig.getAttribute("PATH");

	}

	/**
	 * listFiles Funzione di ausilio del thread del demone. Tramite il filtro implementato nella classe
	 * ProLaborFilesFilter. Il filtro permette di identificare i files prodotti da ProLabor, ovvero tutti quelli che
	 * finiscono per "P.XML".
	 * 
	 * @param directory
	 * @return vettore di nomi di file che rispettano il filtro impostato
	 * @throws IOException
	 */
	protected String[] listFiles() throws IOException {

		File dir = new File(urlScambio);
		if (!dir.isDirectory()) {
			_logger.debug("SilProLabor.DemoneCooperazione::listFiles: directory non valida ");

			throw new IllegalArgumentException("listFiles: directory non valida");
		}
		filtro = new ProlaborFilesFilter();
		return dir.list(filtro);
	}

	/**
	 * readFile Legge il file il cui nome è specificato nel parametro fileIn. Interpreta l'XML in esso contenuto
	 * (tramite la classe SourceBean) e lo restituisce come SourceBean.
	 * 
	 * In caso di errore lancia un'eccezione.
	 * 
	 * @param fileIn
	 *            nome del file da leggere
	 * @return XML letto nel file sotto forma di SourceBean
	 * @throws Exception
	 */
	private SourceBean readFile(String fileIn) throws Exception {
		SourceBean tracciato = null;

		try {
			InputSource stream = new InputSource(new FileReader(fileIn)); // leggo
																			// il
																			// file
																			// di
																			// nome
																			// fileIn
			tracciato = SourceBean.fromXMLStream(stream); // traduco l'XML in
															// esso contenuto e
															// lo trasformo in
															// SourceBean
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Errore nella lettura del file: " + fileIn);
		}

		return tracciato;
	}

	protected SourceBean leggiFile(String filename) {

		// gestione dei files da trattare
		File fileIn = new File(urlScambio + File.separator + filename);

		/*
		 * //scrivo il log: writeLog("Inizio elaborazione " + filenames[i]);
		 */

		// imposto il nome del file di destinazione (utile per notificare
		// eventuali problemi)
		fileNameDest = filename.substring(0, 7) + "I.XML";
		fileNameRenamed = filename.substring(0, 7) + "S.XML";
		SourceBean argSourceBean = null;

		try {
			argSourceBean = readFile(urlScambio + File.separator + filename);
		} catch (Exception ex) {
			ex.printStackTrace();
			/*
			 * writeLog("\t***ELABORAZIONE FALLITA: XML malformato nel file: " + filenames[i]);
			 * writeLog("\t***Exception prodotta: " + ex.getMessage() ); writeLog("Fine elaborazione " + filenames[i]);
			 */
			// writeErrorMessage();
			// return;
		} finally {
			// cancello il file (proLabor capisce che è in fase di elaborazione)
			fileIn.delete();

		}

		return argSourceBean;

	}

	protected void rinominaFile() {

		File fileToRename = new File(urlScambio + File.separator + fileNameDest);
		File fileRenamed = new File(urlScambio + File.separator + fileNameRenamed);

		fileToRename.renameTo(fileRenamed);

	}

	protected void writeTracciato(String header, oracle.sql.CLOB body, String footer) throws Exception {
		// imposto il file output
		try {
			File fileDest = new File(urlScambio + File.separator + fileNameDest);
			FileOutputStream destination = new FileOutputStream(fileDest);
			DataOutputStream d = new DataOutputStream(destination);

			// scrivo l'header nel file
			d.writeBytes(header);
			if (body != null) {
				// se il tracciato prodotto dalla stored non è nullo,
				// lo appendo all'header prodotto.
				InputStream instream = body.getAsciiStream();

				int chunk = body.getChunkSize();
				byte[] buffer = new byte[chunk];
				int length;

				// Fetch data
				while ((length = instream.read(buffer)) != -1) {
					d.write(buffer, 0, length);
				}
				instream.close();
			}

			d.writeBytes(footer);
			d.close();
			destination.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Errore nella scrittura del tracciato");
		} finally {
			// rinomino il file
			this.rinominaFile();
		}
	}

	protected void writeTracciato(String header, String body, String footer) throws Exception {
		try {
			File fileDest = new File(urlScambio + File.separator + fileNameDest);
			FileOutputStream destination = new FileOutputStream(fileDest);
			DataOutputStream d = new DataOutputStream(destination);
			d.writeBytes(header + body + footer);
			destination.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Errore nella scrittura del tracciato");
		} finally {
			// rinomino il file
			this.rinominaFile();
		}
	}

	// ***********************************

}