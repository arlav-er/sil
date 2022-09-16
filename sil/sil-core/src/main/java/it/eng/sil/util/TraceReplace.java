package it.eng.sil.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Category;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import it.eng.afExt.utils.StringUtils;

/**
 * Rimappa tutte le invocazioni di TracerSingleton con quelle a log4j Per ora non gestisce le classi annidate e multiple
 * nello stesso file
 * 
 * @author Alessandro Pegoraro
 */

public class TraceReplace {
	private final static String searchPattern = "TracerSingleton.log";
	private final static String oldImportStatement = "import com.engiweb.framework.tracing.TracerSingleton";
	private final static String newImportStatement = "import org.apache.log4j.*;";
	// --------------------------------------------------------------------
	private File inBase;
	private File outBase;
	// Contatori numero files processati
	private int processed;
	private int copied;
	private int substcall;
	private int skipped;
	private int total;
	private Logger filelogger;

	static Category logger = Logger.getLogger("TraceReplace");

	// Logger logger;
	public TraceReplace(String inBase, String outBase) {
		this.inBase = new File(inBase);
		this.outBase = new File(outBase);
		// BasicConfigurator.configure();
		this.filelogger = Logger.getLogger("TraceReplace.fileout");
		FileAppender appender = null;
		try {
			appender = new FileAppender(new PatternLayout(), "cat.deeper");
		} catch (Exception e) {
		}
		filelogger.addAppender(appender);

		// logger.setLevel(Level.WARN);
		// filelogger.setLevel(Level.WARN);

	}

	/**
	 * Ritorna la severità dell'errore mappata su log4j. Lavora solo con le stringhe senza interpretarle
	 * 
	 * @param severity
	 * @return String il nuovo valore mappato (corrisponde al metodo da invocare)
	 * 
	 */
	private static String getMName(String severity) {
		// default is "debug"
		String levelInt = "debug";
		if (severity.compareTo("TracerSingleton.INFORMATION") == 0)
			levelInt = "info";
		if (severity.compareTo("TracerSingleton.WARNING") == 0)
			levelInt = "warn";
		if (severity.compareTo("TracerSingleton.MINOR") == 0)
			levelInt = "error";
		if (severity.compareTo("TracerSingleton.MAJOR") == 0)
			levelInt = "error";
		if (severity.compareTo("TracerSingleton.CRITICAL") == 0)
			levelInt = "fatal";
		if (severity.compareTo("TracerSingleton.DEBUG") == 0)
			levelInt = "debug";
		return levelInt;
	}

	public void doReplaceInDir() {
		doReplaceInDir(inBase, outBase);
	}

	/**
	 * Scandisce files e Directory
	 * 
	 * @param dir
	 *            cartella da esaminare
	 * @param out
	 *            cartella su cui copiare i files
	 */
	protected void doReplaceInDir(File dir, File out) {
		processaFile(dir, out);
		processaDir(dir);
	}

	/**
	 * Processa tutti i files di una Directory e lancia la traduzione delle chiamate al tracer
	 * 
	 * @param dir
	 * @param outDir
	 */
	protected void processaFile(File dir, File outDir) {
		// System.out.println("processo i file in:" + dir.getAbsolutePath() +
		// ">");
		// System.out.println("li metto in in:" + outDir.getAbsolutePath()
		// +"\\"+ dir.getName());
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				String name = f.getName();
				return f.isFile();
			};
		});
		for (int i = 0; i < files.length; i++) {
			total++;
			// DEBUG: System.out.println(files[i].getAbsoluteFile());
			File newDir = null;
			// primociclo --> non va aggiunto il nome della dir perché è già
			// nella base
			if (dir.getAbsolutePath().compareTo(inBase.toString()) == 0) {
				boolean success = new File(outDir.getAbsolutePath()).mkdir();
				newDir = new File(outDir.getAbsolutePath());
				// negli altri casi prendo la base + il path
			} else {
				boolean success = new File(outDir.getAbsolutePath() + "\\" + dir.getName()).mkdir();
				newDir = new File(outDir.getAbsolutePath() + "\\" + dir.getName());
			}
			doReplaceInFile(files[i], newDir);
		}
	}

	/**
	 * Processa una directory per rilanciare ricorsivamente la traduzione sulle sotto-directory. Duplica la gerarchia in
	 * base al valore statico outBase
	 * 
	 * @param targetDir
	 *            il path della directory da scandire
	 * 
	 */
	protected void processaDir(File targetDir) {
		// Recupera tutte le directory e le spazzola ricorsivamente
		// System.out.println("processo directory:" + dir);
		File[] dirs = targetDir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				String name = f.getName();
				return f.isDirectory() && !name.equals(".") && !name.equals("..");
			};
		});
		for (int i = 0; i < dirs.length; i++) {
			String relDir = getRelDir(targetDir);
			boolean success = new File(outBase + relDir).mkdir();
			// System.out.println("creo :" + outBase + relDir);
			// System.out.println("SUCCESS="+success);
			File newDir = new File(outBase + "\\" + relDir);
			doReplaceInDir(dirs[i], newDir);
		}
	}

	/**
	 * Tronca il path per estrarre la posizione "dopo" la radice (inBase)
	 * 
	 * @param absPath
	 *            il path assoluto da troncare
	 * @return String il path della directory in input troncando la base
	 * 
	 */
	protected String getRelDir(File absPath) {
		String absolute = absPath.getAbsolutePath();
		return absolute.substring(inBase.getAbsolutePath().length(), absolute.length());
	}

	/**
	 * Copia un file, quando il codice non contiene chiamate al tracer Esso viene semplicemente ricopiato come sta
	 * Lavora solo con le stringhe senza interpretarle
	 * 
	 * @param in
	 *            il File in input
	 * @param out
	 *            il nuovo file
	 * 
	 */
	protected void copyFile(File in, File out) throws Exception {
		logger.debug("copio: " + in.getAbsolutePath() + " in:" + out.getAbsolutePath());
		try {
			FileInputStream fis = new FileInputStream(in);
			FileOutputStream fos = new FileOutputStream(out);
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
			fis.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("FATAL: copy error");
			System.exit(-1);
		}
	}

	/**
	 * Ricostruisce su una stringa la chiamata al tracer, se il metodo è invocato su una solo linea di codice ritorna
	 * subito
	 * 
	 * @param line
	 *            prima riga della chiamata da rimpiazzare
	 * @param input
	 *            stream in fase di traduzione
	 * @return String la chiamata al vecchio tracer su un'unica riga
	 * 
	 */
	protected String reLineCall(String firstLine, BufferedReader input) {
		String current = null;
		String oldcall = firstLine.trim();
		String newcall = "";
		try {
			// Leggo la prima linea e ciclo
			// System.out.println("oldcall:"+oldcall);
			if ((oldcall != null)
					&& (!oldcall.trim().endsWith(";") && !StringUtils.replace(oldcall, " ", "").endsWith(";}"))) {
				current = input.readLine();
				while ((current != null)
						&& (!current.trim().endsWith(";") && !StringUtils.replace(current, " ", "").endsWith(";}"))) {
					oldcall = oldcall.concat(current.trim());
					// System.out.println("concateno:" + current.trim());
					current = input.readLine();
				}
				// ultima riga della chiamata
				if (current != null) {
					oldcall = oldcall.concat(current.trim());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("reLineCall Read error");
		}
		// System.out.println("Vecchia chiamata: " + oldcall);
		return translateCall(oldcall);
	}

	/**
	 * Rimappa la chiamata a TracerSingleton con log4j Interpreta le chiamate a tre o quattro parametri
	 * 
	 * @param oldcall
	 *            la vecchia chiamata al tracersingleton già trimmata su un'unica riga
	 * @return String la nuova chiamata rimappata
	 */
	protected String translateCall(String oldcall) {
		/* open e close delimitano gli argomenti della chiamata al tracer */
		int open = oldcall.indexOf("(", oldcall.indexOf("Singleton.log"));
		int close = oldcall.lastIndexOf(")");
		/* Se è commentata salta la traduzione */
		if (oldcall.startsWith("//"))
			return oldcall;
		String params = oldcall.substring(open, close);
		// System.out.println("Translate:"+params);
		String severity = "";
		String message = "";
		String except = "";
		String newcall = "";
		// Stato del parsing, all'interno delle virgolette o meno
		boolean inside = false;
		int index = 0;
		// Ci sono al massimo 4 parametri, dunque tre virgole
		int[] commas = new int[3];
		int commaindex = 0;
		try {
			while (index < params.length()) {
				char now = params.charAt(index);
				if (now == '"')
					inside = changeStat(inside);
				if ((now == ',') && (!inside)) {
					commas[commaindex] = index;
					commaindex++;
				}
				index++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FATAL: reLineCall: Parsing error");
			System.exit(-2);
		}
		severity = params.substring(commas[0] + 1, commas[1]);
		// System.out.println("severity:" + severity);
		// Chiamata a 2 parametri
		if (commas[2] != 0) {
			message = params.substring(commas[1] + 1, commas[2]);
			except = params.substring(commas[2] + 1, params.length());
			// newcall = "_logger."+getMName(severity)+"(" + message + ", " +
			// except + ");\n";
			// Passa attrraverso il wrapper
			// if (getMName(severity).compareTo("debug") == 0)
			newcall = "it.eng.sil.util.TraceWrapper." + getMName(severity) + "( _logger," + message + ", " + except
					+ ");\n";
			// else
			// newcall = "_logger." + getMName(severity) + "(" + message + ", " + except + ");\n";
			// Chiamata a 1 parametro
		} else {
			message = params.substring(commas[1] + 1, params.length());
			newcall = "_logger." + getMName(severity) + "( " + message + ");\n";
		}
		// System.out.println("message:" + message);
		substcall++;
		return newcall;
	}

	// Inverte un booleano
	protected boolean changeStat(boolean st) {
		if (st) {
			st = false;
		} else
			st = true;
		return st;
	}

	/**
	 * Verifica le condizioni prima di lanciare la sostituzione delle chiamate all'interno dei files
	 * 
	 * @param input
	 *            il file da esaminare
	 * @param outDir
	 *            la cartella in output
	 * 
	 */
	protected void doReplaceInFile(File input, File outDir) {
		FileReader fr = null;
		FileWriter fw = null;
		String classname = "";
		BufferedReader reader = null;
		BufferedWriter writer = null;
		boolean trovato = false;
		boolean classfound = false;
		boolean importinserted = false;
		File outFile = new File(outDir.getAbsolutePath() + "\\" + input.getName());
		/*
		 * verifico ci sia roba da fare e salto questa classe medesima i files saltati verranno poi semplicemente
		 * copiati
		 */
		boolean istojump = false;
		if (!doSearchStringInFile(input))
			istojump = true;
		if (input.getName().startsWith("TraceReplace."))
			istojump = true;
		if (input.getName().endsWith(".jsp"))
			istojump = true;
		if (countClassesInFile(input) > 1) {
			skipped++;
			istojump = true;
			filelogger.log(Level.WARN, "jumping:" + input.getAbsolutePath());
			// System.out.println("DUBLE:" + input.getAbsolutePath());

		}
		if (istojump) {
			// System.out.println("Salto: " + input.getName());
			try {
				copyFile(input, outFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			copied++;
			return;
		}
		filelogger.info("processo:" + outFile.getAbsolutePath());
		try {
			// apro INPUT
			fr = new FileReader(input);
			reader = new BufferedReader(fr);
			fw = new FileWriter(outFile);
			writer = new BufferedWriter(fw);
			// Trovo il nome della classe
			classname = input.getName();
			classname = classname.substring(0, classname.lastIndexOf("."));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("doReplaceInFile: Open Error");
		}
		try {
			// Leggo la prima linea e ciclo
			String line = reader.readLine();
			while (line != null) {
				// Processo la linea
				// Gestione aggiunta import, per rimpiazzare invece che
				// aggiungere sostituire
				// la costante stringa con la variabile oldimportstatement
				if (line.indexOf("import ") >= 0 && !importinserted) {
					writer.write(line);
					writer.newLine();
					String newimport = newImportStatement;
					writer.write(newimport);
					line = reader.readLine();
					writer.newLine();
					importinserted = true;
					// cicla
					continue;
				}
				if ((line.indexOf("class ") >= 0) && (line.indexOf("//") < 0)) {
					// System.out.println(line);
					// cerca la graffa di apertura
					while (line.indexOf("{") < 0) {
						writer.write(line);
						line = reader.readLine();
					}
					// Dichiarazione nuovo logger
					String declaration = "\t static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("
							+ classname + ".class.getName());";
					writer.write(line);
					writer.newLine();
					writer.newLine();
					writer.write(declaration);
					line = reader.readLine();
					writer.newLine();
					classfound = true;
					// cicla
					continue;
				}
				// Sostituzione chiamata tracer
				if (line.indexOf(searchPattern) >= 0) {
					// Riallinea la chiamata e la traduce
					String newcall = reLineCall(line, reader);
					writer.write(newcall);
					line = reader.readLine();
					writer.newLine();
					continue;
				}
				writer.write(line);
				// Leggo la successiva linea
				line = reader.readLine();
				// Ultima riga, no new line
				if (line != null) {
					writer.newLine();
				}
			}
		} catch (IOException ex) {
			System.out.println("doReplaceInFile: Read/Write Error");
			ex.printStackTrace();
		} finally {
			// Chiude tutto ciò che ha eventualmente aperto
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
				}
			}
			if (writer != null) {
				try {
					writer.close();
					processed++;
				} catch (Exception e) {
				}
			}
		}
	}

	protected boolean doSearchStringInFile(File file) {
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
				while ((t < searchPattern.length()) && !trovato) {
					trovato = line.indexOf(searchPattern) >= 0;
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
		return trovato;
	}

	/**
	 * Conta le occorrenze delle classi definite in un file
	 * 
	 */
	protected int countClassesInFile(File file) {
		FileReader fr = null;
		BufferedReader reader = null;
		int trovato = 0;
		try {
			// apro INPUT
			fr = new FileReader(file);
			reader = new BufferedReader(fr);
			// Leggo la prima linea e ciclo
			String line = reader.readLine();
			while (line != null) {
				if ((line.indexOf(" class ") >= 0) && (line.indexOf("//") < 0))
					trovato++;
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
		return trovato;
	}

	public static void main(String[] args) {
		if (null == args || args.length < 2) {
			logger.error("usage:");
			logger.error("\tTraceReplace  <inputdir> <outputdir>");
			logger.error("\t\t");
			logger.error("\tQuesta classe ricopia la gerarchia <inputdir> sostituendo le chiamate al TracerSingleton ");
			logger.error("\tcon log4j e ricrea su <outputdir> la stessa gerarchia. ");
			System.exit(1);
		}
		System.out.println("*** Replace Tracer ***");
		System.out.println("config:");
		System.out.println("input Directory: " + args[0]);
		System.out.println("output Directory " + args[1]);

		TraceReplace replacer = new TraceReplace(args[0], args[1]);
		replacer.doReplaceInDir();

		System.out
				.println("FINISHED!!   - Total Files: " + replacer.total + "  Files simply copied: " + replacer.copied);
		System.out.println("             - Files Processed: " + replacer.processed + "  Files skipped(nested class): "
				+ replacer.skipped);
		System.out.println("             - Methods substituted: " + replacer.substcall);
		return;
	}
}
