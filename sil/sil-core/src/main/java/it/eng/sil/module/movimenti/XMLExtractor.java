package it.eng.sil.module.movimenti;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Estare i record dal DB e li ritorna come Map Java.
 */
public class XMLExtractor extends DefaultHandler implements RecordExtractor {
	/** Indica il numero del prossimo elemento da restituire nell'array seguente */
	private int counter = 0;
	/** Array di Map da ritornare */
	private Map[] records = null;

	/**
	 * Costruttore, esegue subito il parsing della stringa passata per controllarne la correttezza formale. Se incontra
	 * problemi restituisce la SAXException ottenuta.
	 */
	public XMLExtractor(String XMLString) throws SAXException, ParserConfigurationException, IOException {

		// Creo il parser per processare la stringa passata
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(true);
		SAXParser parser = spf.newSAXParser();
		parser.parse(new InputSource(new StringReader(XMLString)), this);
	}

	/**
	 * Indica se ci sono ancora record da estrarre dalla sorgente.
	 * <p>
	 * 
	 * @return true se ci sono ancora record da estrarre, false altrimenti
	 * 
	 */
	public boolean hasNext() {
		return counter < records.length;
	}

	/**
	 * Estrae il record successivo dalla sorgente, lo elabora e restituisce una Hashtable contenente i dati del record
	 * con i nomi indicati.
	 * <p>
	 * 
	 * @return Un oggetto di tipo Hashtable che contiene i dati estratti dal record.
	 */
	public Object next() throws NoSuchElementException {
		if (counter < records.length) {
			int c = counter;
			counter += 1;
			records[c].put("RECORDNUMBER", new Integer(counter));
			records[c].put("CODMONOPROV", "S");
			return records[c];
		} else
			throw new NoSuchElementException();
	}

	/**
	 * Numero di record totali da processare
	 */
	public int getNumRecord() {
		return records.length;
	}

	/**
	 * Operazione non supportata, lancerà una UnsupportedOperationException
	 */
	public void remove() throws UnsupportedOperationException, IllegalStateException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Metodi ed oggetti di configurazione
	 */
	private Map currentRecord = null;
	private ArrayList recordsArrayList = new ArrayList();

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		// Se incontro un tag RECORD devo inizializzare il sistema di
		// configurazione
		if (qName.equalsIgnoreCase("RECORD")) {
			currentRecord = new Hashtable();
			currentRecord.put("CONTEXT", "importa");
			String version = attributes.getValue("version");
			currentRecord.put("version", version);
		}

		// Se incontro un tag FIELD devo aggiungere i dati alla map corrente
		if (qName.equalsIgnoreCase("FIELD")) {
			// Estraggo i valori degli attributi
			String name = attributes.getValue("name");
			String format = attributes.getValue("format");
			String data = attributes.getValue("data");

			// Inserisco nel record il campo se non è vuoto
			if (name != null && data != null && !name.equals("") && !data.equals("")) {
				currentRecord.put(name, data);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		// Se incontro la fine del record devo aggiungere la Map all'ArryaList
		if (qName.equalsIgnoreCase("RECORD")) {
			recordsArrayList.add(currentRecord);
		}

		// Se incontro la fine dei records devo creare la Map per l'estrazione
		if (qName.equalsIgnoreCase("RECORDS")) {
			records = new Hashtable[recordsArrayList.size()];
			for (int i = 0; i < records.length; i++) {
				records[i] = (Map) recordsArrayList.get(i);
			}
		}
	}

}