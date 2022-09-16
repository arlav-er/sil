package it.eng.sil.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

/**
 * Cerca una stringa in tutti i file di una directory e sottodirectory. Utile per trovare quali file contengono una
 * stringa e quali no.
 * 
 * @author Luigi Antenucci
 */
public class _SearchStringInFiles {

	private static String[] _trova = {
			/*
			 * "<af:linkScript path=\"../../js/\"", "<af:linkScript path=\"../../../js/\""
			 */
			/*
			 * "<%@ include file =\"../global/noCaching.inc\"", "<%@ include file =\"../../global/noCaching.inc\"", "<%@
			 * include file=\"../global/noCaching.inc\"", "<%@ include file=\"../../global/noCaching.inc\""
			 */
			"<%@ include file =\"../global/getCommonObjects.inc\"",
			"<%@ include file =\"../../global/getCommonObjects.inc\"",
			"<%@ include file=\"../global/getCommonObjects.inc\"",
			"<%@ include file=\"../../global/getCommonObjects.inc\"" };

	private static String _base = "C:\\Progetti\\SIL\\sviluppo\\applicazioni\\sil\\web\\jsp";

	public static void main(String[] args) {

		System.out.println("*** Search String In Files ***");
		System.out.println("config:");

		if (args.length > 0) {
			_trova = new String[1];
			_trova[0] = args[0];
		}
		System.out.print("- trova  [");
		for (int t = 0; t < _trova.length; t++) {
			if (t > 0)
				System.out.print(",");
			System.out.print(_trova[t]);
		}
		System.out.println("]");

		if (args.length > 1)
			_base = args[1];
		System.out.println("- base   [" + _base + "]");

		System.out.println();

		_SearchStringInFiles ssif = new _SearchStringInFiles(_trova, _base);
		ssif.doSearchStringInDir();
	}

	// --------------------------------------------------------------------

	private String[] trova;
	private File base;

	public _SearchStringInFiles(String[] trova, String base) {
		this.trova = trova;
		this.base = new File(base);
	}

	public void doSearchStringInDir() {
		doSearchStringInDir(base);
	}

	protected void doSearchStringInDir(File dir) {

		spazzolaFile(dir);

		spazzolaDir(dir);
	}

	protected void spazzolaFile(File dir) {
		// DEBUG: System.out.println("<" + dir.getAbsolutePath() + ">");

		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				String name = f.getName();
				return f.isFile() && !name.equalsIgnoreCase("vssver.scc") && (name.indexOf(".jsp") >= 0);
			};
		});
		for (int i = 0; i < files.length; i++) {
			// DEBUG: System.out.println(files[i].getAbsoluteFile());
			doSearchStringInFile(files[i]);
		}
	}

	protected void spazzolaDir(File dir) {
		// Recupera tutte le directory e le spazzola ricorsivamente
		File[] dirs = dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				String name = f.getName();
				return f.isDirectory() && !name.equals(".") && !name.equals("..");
			};
		});
		for (int i = 0; i < dirs.length; i++) {
			doSearchStringInDir(dirs[i]);
		}
	}

	protected void doSearchStringInFile(File file) {
		FileReader fr = null;
		BufferedReader reader = null;
		boolean trovato = false;

		try {
			// apro INPUT
			fr = new FileReader(file);
			reader = new BufferedReader(fr);

			// Leggo la prima linea e ciclo
			String line = reader.readLine();
			while ((line != null) && !trovato) {

				// Processo la linea
				int t = 0;
				while ((t < trova.length) && !trovato) {
					trovato = line.indexOf(trova[t]) >= 0;
					t++;
				}

				// Leggo la successiva linea
				line = reader.readLine();
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// Chiude tutto ciò che ha eventualmente aperto
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
				}
			}
		}

		System.out.println("[" + (trovato ? 'v' : ' ') + "] " + file.getAbsoluteFile());
	}

}
