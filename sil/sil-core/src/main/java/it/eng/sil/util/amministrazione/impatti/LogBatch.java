package it.eng.sil.util.amministrazione.impatti;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe per la creazione del file di log per il batch
 */
public class LogBatch {

	private String directory;
	private String nomeFile;

	private SimpleDateFormat simpleDateFormat;

	public LogBatch(String nomeFile, String dir) {

		this.nomeFile = nomeFile;
		directory = dir;

		simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	}

	/**
	 * WriteLog Scrive sul file di log "SIL.log" il messaggio specificato nel parametro "message". Se il log esiste,
	 * appende il messaggio, altrimenti crea un file di log nuovo e scrive il messaggio in esso. Il formato del
	 * messaggio è il seguente: [DATA] [ORA] [MESSAGGIO]
	 * 
	 * @param message
	 *            messaggio da scrivere nel log
	 * @return nothing
	 */
	public void writeLog(String message) {
		String logName = directory + File.separator + nomeFile;

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logName, true)))) {
			String data = simpleDateFormat.format(new Date());
			String logRow = data + "   " + message;

			writer.write(logRow);
			writer.newLine();
		} catch (IOException e) {
			System.out.println("Errore irreversibile dell'applicazione");
		}
	}

	/**
	 * Metodo per la scrittura di tutte le variabili d'ambiente in un apposito file, in modo da poter controllare il
	 * settaggio del server
	 * 
	 * @return nulla
	 */
	public void writeLogVarJava(String message) {
		String logName = directory + File.separator + "ConfigVarBatch.log";

		try (FileOutputStream destination = new FileOutputStream(logName, true);
				DataOutputStream d = new DataOutputStream(destination)) {
			String logRow = message + "\r\n";
			d.writeBytes(logRow);
		} catch (IOException ex) {
			System.out.println("Errore irreversibile dell'applicazione");
		}
	}

	/**
	 * Legge il contenuto di un file
	 */
	public String readFile() {
		String campo = "";
		String logName = directory + File.separator + nomeFile;

		try (FileInputStream src = new FileInputStream(logName);
				BufferedReader d = new BufferedReader(new InputStreamReader(src))) {
			String campoTmp = d.readLine();
			while (campoTmp != null) {
				campo = campo + campoTmp + "\r\n";
				campoTmp = d.readLine();
			}
		} catch (IOException ex) {
			System.out.println("Errore irreversibile dell'applicazione");
		}
		return campo;
	}

}