package it.eng.sil.module.movimenti;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Classe che estrae i dati da un record di tipo TXT a campi di lunghezza fissa e li inserisce in una HashTable.
 * L'esatta associazione tra dati e campi e la lunghezza dei record sono stabilite alla creazione dalle proprieta
 * passate al costruttore
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class TXTExtractor extends DefaultHandler implements RecordExtractor {

	// Definizione delle variabili necessarie all'estrazione dei dati
	private int lunghezzaRecord;
	private Vector fields = new Vector();
	// Insieme dei caratteri da scartare del tracciato
	private char[] discardChars = { '\u001A' };

	// Altre variabili
	private String className = this.getClass().getName();
	private BufferedReader input;
	private String nextRecord;
	private boolean calledNext;
	private boolean lastHasNext;
	private int numRecord = 0;

	// Variabili per la configurazione
	private String version = "DEFAULT";
	boolean intrace = false;
	boolean finded = false;

	/**
	 * Costruttore che accetta in ingresso lo stream dei dati da importare, il nome di un file XML di configurazione e
	 * il nome della versione del tracciato da cui estrarre i dati. <br>
	 * L'estrattore è in grado di configurarsi a partire dalle informazioni del file di configurazione (che deve essere
	 * compatibile con la dtd contenuta in TXTRecordMapping.dtd).
	 * <p>
	 * 
	 * @param source
	 *            La sorgente dei dati da estrarre sotto forma di InputStream
	 * @param configFileName
	 *            il nome del file XML di configurazione
	 * @param traceVersion
	 *            il nome della versione del tracciato da estrarre (se non specificata sarà al valore "DEFAULT")
	 *            <p>
	 * @exception SAXException
	 *                Se si verificano eccezioni nel parsing del file di configurazione
	 * @exception IOException
	 *                Quando si verificano problemi nella lettura del file di configurazione
	 * @exception FileNotFoundException
	 *                Se non viene trovato il file di configuazione
	 */
	public TXTExtractor(InputStream source, String configFileName, String traceVersion)
			throws SAXException, FileNotFoundException, IOException, ParserConfigurationException {
		if (version != null) {
			this.version = traceVersion;
		}

		// Apro il file di configurazione
		InputStream configfile = new FileInputStream(configFileName);

		// Creo il parser per processarlo
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(true);
		SAXParser parser = spf.newSAXParser();
		parser.parse(configfile, this);

		// setto gli altri parametri
		this.input = new BufferedReader(new InputStreamReader(source));
		nextRecord = null;
		calledNext = true;
		lastHasNext = true;
	}

	/**
	 * Indica se ci sono ancora record da estrarre dalla sorgente.
	 * <p>
	 * 
	 * @return true se ci sono ancora record da estrarre, false se l'estrazione del record successivo non andrà a buon
	 *         fine
	 * 
	 */
	public boolean hasNext() {

		// Se è stata chiamata la next eseguo un'altra estrazione, altrimenti
		// ritorno il risultato precedente
		if (!calledNext) {
			return lastHasNext;
		} else {
			calledNext = false;

			// Leggo il record successivo e lo memorizzo in nextRecord
			try {
				nextRecord = this.estrai();
				lastHasNext = true;
			} catch (Exception e) {
				nextRecord = null;
				lastHasNext = false;
			}

			// Ritorno il risultato;
			return lastHasNext;
		}
	}

	/**
	 * Estrae il record successivo dalla sorgente, lo elabora e restituisce una Hashtable contenente i dati indicati nel
	 * file di configurazione caricato
	 * <p>
	 * 
	 * @return Un oggetto di tipo Hashtable che contiene i dati estratti dal record.
	 */
	public Object next() throws NoSuchElementException {

		// Se il file è già finito in precedenza ritorno la
		// NoSuchElementException
		if (!lastHasNext) {
			throw new NoSuchElementException();
		} else if (!calledNext) {

			// Non ho ancora chiamato la next sull'elemento corrente, quindi non
			// ne devo estrarre un'altro!
			calledNext = true;

			// Elaboro il record corrente
			return this.elabora();
		} else {

			// Chiamo la hasNext per estrarre il prossimo elemento
			boolean has = hasNext();
			if (has = false) {
				throw new NoSuchElementException();
			}
			// Se il prossimo elemento esiste lo elaboro e ritorno il risultato
			else
				return this.elabora();
		}
	}

	/**
	 * Numero di record totali da processare, viene scoperto solo alla fine.
	 */
	public int getNumRecord() {
		return numRecord;
	}

	/**
	 * Operazione non supportata, lancerà una UnsupportedOperationException
	 */
	public void remove() throws UnsupportedOperationException, IllegalStateException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Estrae il record successivo, se riesce lo ritorna sotto forma di string, altrimenti lancia un'eccezione.
	 */
	private String estrai() throws IOException, NoSuchElementException {

		// Estraggo la linea seguente dalla sorgente
		String line = input.readLine();
		if (line == null) {
			throw new NoSuchElementException();
		} else {
			// Scorro la linea e scarto i caratteri di controllo indicati nella
			// configurazione
			StringBuffer b = new StringBuffer();
			for (int i = 0; i < line.length(); i++) {
				boolean discard = false;
				for (int j = 0; j < discardChars.length; j++) {
					if (line.charAt(i) == discardChars[j]) {
						discard = true;
					}
				}
				if (!discard) {
					b.append(line.charAt(i));
				}
			}
			// Controllo che non sia vuota
			if (b.length() > 0) {
				numRecord += 1;
				return b.toString();
			} else {
				throw new NoSuchElementException();
			}
		}
	}

	/**
	 * Elabora il record corrente secondo quanto indicato nel costruttore e restituisce la Hashtable creata. Inserisce
	 * solo i campi diversi dalla stringa vuota; può capitare che il tracciato sia più corto del previsto, in questo
	 * caso i campi non trovati vengono saltati.
	 * 
	 */
	private Hashtable elabora() {

		Hashtable recordTable = new Hashtable();
		recordTable.put("CONTEXT", "importa");
		recordTable.put("CODMONOPROV", "F");
		if (fields == null) {
			recordTable.put("Record", nextRecord);
		} else {
			for (int i = 0; i < fields.size(); i++) {
				Object o = fields.get(i);
				if (o instanceof Field) {
					Field f = (Field) o;
					// Per leggere il campo devo avere almeno il suo carattere
					// iniziale,
					// cioè occorre che il record sia più lungo del carattere
					// iniziale del campo
					if (nextRecord.length() > f.getInit()) {
						// Se il record finisce prima del campo estraggo solo i
						// dati fino alla fine del record,
						// e non fino alla fine del campo
						int endIndex = (nextRecord.length() >= f.getEnds() ? f.getEnds() : nextRecord.length());
						String value = nextRecord.substring(f.getInit(), endIndex);
						if (!value.trim().equals("")) {
							recordTable.put(f.getName(), value.trim());
						}
					}
				}
			}
		}
		return recordTable;
	}

	/** Metodi per la configurazione interna dell'estrattore */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		// controllo se ho raggiunto la versione di tracciato che devo importare
		if (qName.equals("TRACE")) {
			String currentversion = attributes.getValue("version");
			if (currentversion.equals(this.version)) {
				intrace = true;
				finded = true;
			}
		}

		// Se sono nel tracciato corretto e ho incontrato l'elemento iniziale
		// inserisco la lunghezza dei record nella variabile lunghezzaRecord
		if (intrace && qName.equals("RECORD")) {
			try {
				String lunghezza = attributes.getValue("length");
				lunghezzaRecord = new Integer(lunghezza).intValue();

			} catch (Exception e) {
				throw new SAXException(
						"Errore nell'indicazione della lunghezza del record nel tracciato [version=" + version + "]");
			}
		}

		// Se sono nel tracciato corretto ed incontro un tag FIELD inserisco un
		// nuovo oggetto nel vettore dei fields
		if (intrace && qName.equals("FIELD")) {
			String name = attributes.getValue("name");
			String init = attributes.getValue("init");
			String ends = attributes.getValue("ends");
			if (name == null || init == null || ends == null) {
				throw new SAXException(
						"Attributi non corretti nel tag FIELD [name=" + name + "] nel tracciato " + version);
			}
			try {
				fields.add(new Field(name, (new Integer(init)).intValue(), (new Integer(ends)).intValue()));
			} catch (Exception e) {
				throw new SAXException(
						"Errore nell'indicazione del tag FIELD [name=" + name + "] nel tracciato" + version);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		// Se ho raggiunto la fine del tracciato corretto esco dalla modalita
		// intrace e ignoro il resto
		if (qName.equals("TRACE") && intrace) {
			intrace = false;
		}

		// Se ho raggiunto la fine del file e non ho una configurazione valida
		// lancio un'eccezione
		if (qName.equals("TRACES") && !finded) {
			throw new SAXException("Tracciato non trovato [version=" + version + "]");
		}
	}
}