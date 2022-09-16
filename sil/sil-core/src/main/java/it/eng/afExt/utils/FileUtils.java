package it.eng.afExt.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import com.engiweb.framework.configuration.ConfigSingleton;

/**
 * Utilità sui file.
 * 
 * @author Luigi Antenucci
 */
public abstract class FileUtils {

	/**
	 * Controlla se il path esiste. Se esiste, ritorna; se non esiste lancia eccezione.
	 */
	public static final void checkExistsPath(String path) throws IOException {

		File pathFile = new File(path);
		if (!pathFile.isDirectory()) { // rende FALSE se non esiste o non è una
										// directory
			throw new IOException("Percorso '" + path + "' non esistente");
		}
	}

	/**
	 * Controlla se il file esiste. Se esiste, ritorna; se non esiste lancia eccezione.
	 */
	public static final void checkExistsFile(String filename) throws IOException {

		File file = new File(filename);
		if (!file.isFile()) { // rende FALSE se non esiste o non è un file
								// "normale"
			throw new IOException("File '" + filename + "' non esistente o non corretto");
		}
	}

	/**
	 * Crea il path passato nel caso non esista.
	 */
	public static final void creaPathSeNonEsiste(String path) throws SecurityException {

		File pathFile = new File(path);
		if (!pathFile.isDirectory()) { // rende FALSE se non esiste o non è una
										// directory
			boolean ok = pathFile.mkdirs();
			if (!ok) {
				throw new SecurityException("Can't create the directory structure");
			}
		}
	}

	/**
	 * Rende lo stesso path passato ma con un "file-separator" come ultimo carattere, se non è già presente.
	 */
	public static final String appendFileSeparator(String path) {
		if (path == null)
			return path;

		char c = path.charAt(path.length() - 1);
		if ((c == File.separatorChar) || (c == '/') || (c == '\\'))
			return path;
		else
			return path + File.separator;
	}

	/**
	 * Se la stringa è un riferimento assoluto a un file o directory, la rende tale e quale; altrimenti la rende con
	 * aggiunto davanti il percorso di base dell'applicativo (es. C:\Progetti\SIL\webapp\").
	 */
	public static final String getWithAbsolutePath(String currPath) {
		File path = new File(currPath);
		if (path.isAbsolute()) {
			return currPath;
		} else {
			String dirWebapp = appendFileSeparator(ConfigSingleton.getRootPath());
			return dirWebapp + currPath;
		}
	}

	/**
	 * Controllo che nella directory passata e nelle sue sottodirectory non si sia alcun file (esecuzione ricorsiva).
	 * maxLivello = 0, controlla nella sola dir; maxLivello = 1, controlla anche nelle dir fino a profondità 1,
	 * ...ecc... maxLivello = -1, controlla lintero albero
	 */
	public static final void checkNoFilesInPath(String dirStr, int maxLivello) throws IOException {
		checkNoFilesInPath(new File(dirStr), 0, maxLivello);
	}

	public static final void checkNoFilesInPath(File dir, int maxLivello) throws IOException {
		checkNoFilesInPath(dir, 0, maxLivello);
	}

	private static final void checkNoFilesInPath(File dir, int livello, int maxLivello) throws IOException {

		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				String name = f.getName();
				return (!name.equals(".") && !name.equals(".."));

			};
		});
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				if ((livello < maxLivello) || (maxLivello == -1)) {
					checkNoFilesInPath(files[i], livello + 1, maxLivello);
				}
			} else {
				throw new IOException("There is at least one file!");
			}
		}
	}

	/**
	 * Cancella tutti i file nella directory passata e nelle sue sottodirectory (esecuzione ricorsiva). Nota: *non*
	 * cancella la directory passata ma solo tutto il suo contenuto! Rende TRUE se ha cancellato tutto; FALSE se
	 * qualcosa non è stato cancellato.
	 */
	public static final boolean deleteAllFilesInPath(String dirStr, int maxLivello, boolean removeDirs) {
		return deleteAllFilesInPath(new File(dirStr), 0, maxLivello, removeDirs);
	}

	public static final boolean deleteAllFilesInPath(File dir, int maxLivello, boolean removeDirs) {
		return deleteAllFilesInPath(dir, 0, maxLivello, removeDirs);
	}

	private static final boolean deleteAllFilesInPath(File dir, int livello, int maxLivello, boolean removeDirs) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			boolean successAll = true;
			for (int i = 0; i < files.length; i++) {
				boolean success = true;
				if (files[i].isDirectory()) {
					if ((livello < maxLivello) || (maxLivello == -1)) {
						success = deleteAllFilesInPath(files[i], livello + 1, maxLivello, removeDirs); // Ricorsione
					}
				} else {
					success = files[i].delete();
				}
				successAll = successAll && success;
			}
			// Se errore, esco con false (non potrà certo cancellare la dir.)
			if (!successAll)
				return false;
		}
		// Ora la dir. è vuota e posso (eventualmente) cancellarla (ma solo se
		// non sono al livello zero: non cancello la dir radice)
		if (removeDirs && (livello > 0))
			return dir.delete();
		else
			return true;
	}

	/**
	 * Crea e rende un nuovo file univoco col nome dato (path+name+ext). Se il file esiste già, accora al nome del file
	 * un progressivo numerico. Il parametro facoltativo indica il limite superiore del progressivo numerico
	 * (default=1000). Es: createUniqueFileName("C:\\TEMP\\", "prova", ".txt") può creare "prova.txt" o "prova_2.txt" o
	 * "prova_3.txt", ecc... Rende il file (come oggetto File) creato, oppure lancia un'eccezione.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final File createUniqueFileName(String path, String name, String ext) throws Exception {
		return createUniqueFileName(path, name, ext, 1000);
	}

	public static final File createUniqueFileName(String path, String name, String ext, int tentaMax) throws Exception {

		int tenta = 0;
		Exception lastException = null;
		String filename = path + name + ext;
		do {
			tenta++;
			if (tenta > 1) {
				filename = path + name + "_" + String.valueOf(tenta) + ext;
			}

			try {
				File f = new File(filename);
				boolean creato = f.createNewFile(); // Creo il file come nuovo
													// (se possibile)
				// rende true=file non esisteva e l'ha creato; false:file già
				// esistente
				if (creato) {
					return f; // Rendo il file creato
				}
			} catch (Exception e) {
				lastException = e;
			}
		} while (tenta < tentaMax);

		// Se sono qui, NON ho creato alcun file
		if (lastException != null)
			throw lastException;
		else
			throw new IOException(
					"createUniqueFileName: can't able to create the file '" + path + name + "_*" + ext + "'!");
	}

}
