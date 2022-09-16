/*
 * Creato il 14-mag-04
 * Author: roccetti
 * 
 */
package it.eng.sil.module.movimenti;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.movimenti.processors.Column;

/**
 * Estrae i dati dalla request/sessione/DB per effettuare la validazione "manuale" dei movimenti
 * <p>
 * 
 * @author roccetti
 * 
 */
public class ManualExtractor extends DefaultHandler implements RecordExtractor {
	// Contenitore dati movimento
	private Map data = null;
	private boolean available = false;

	/** Formattazione per le date */
	private SimpleDateFormat slashFormatter = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat simpleFormatter = new SimpleDateFormat("ddMMyyyy");

	// Indicazione dei campi number
	ArrayList fields = null;

	public ManualExtractor(SourceBean requestOrigin, SourceBean DBOrigin, SourceBean sessionOrigin,
			String configFileName) throws NullPointerException, SourceBeanException, IOException, FileNotFoundException,
			SAXException, ParserConfigurationException {

		// Apro il file di configurazione
		InputStream configfile = new FileInputStream(configFileName);

		// Creo il parser per processarlo
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(true);
		SAXParser parser = spf.newSAXParser();
		parser.parse(configfile, this);

		// Creo l'Hashtable per la validazione
		data = raccogliDati(requestOrigin, sessionOrigin, DBOrigin);
		available = true;
	}

	/**
	 * Indica se ci sono ancora record da estrarre dalla sorgente.
	 * <p>
	 * 
	 * @return true se ci sono ancora record da estrarre, false se non è possibile estrarre alcun altro record
	 * 
	 */
	public boolean hasNext() {
		return available;
	}

	/**
	 * Estrae il record successivo dalla sorgente, lo elabora e restituisce una Hashtable contenente i dati indicati nel
	 * file di configurazione caricato
	 * <p>
	 * 
	 * @return Un oggetto di tipo Hashtable che contiene i dati estratti dal record.
	 */
	public Object next() throws NoSuchElementException {
		if (available) {
			available = false;
			return data;
		} else
			throw new NoSuchElementException();
	}

	/**
	 * Numero di record da processare
	 */
	public int getNumRecord() {
		return 1;
	}

	/**
	 * Operazione non supportata, lancerà una UnsupportedOperationException
	 */
	public void remove() throws UnsupportedOperationException, IllegalStateException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Crea la Map del Record da validare
	 */
	protected Map raccogliDati(SourceBean req, SourceBean ses, SourceBean db) {

		Map record = new Hashtable();

		// Per ogni campo indicato nell'array dei campi recupero i dati dalle
		// tre fonti in ordine e lo converto nel formato opportuno
		for (int i = 0; i < fields.size(); i++) {
			Column c = (Column) fields.get(i);
			Object val = null;
			// Recupero il valore della colonna
			if (c.isAbsolute()) {
				val = c.getData();
			} else {
				// Estraggo il campo dalle tre sorgenti
				if (req != null) {
					val = req.getAttribute(c.getData());
				}
				if (/* ( */val == null /*
										 * || (!c.isNullable() && val.equals("")))
										 */ && ses != null) {
					val = ses.getAttribute(c.getData());
				}
				// if (/*(*/val == null /*|| (!c.isNullable() &&
				// val.equals("")))*/ && db != null) {
				// 08/04/2005 Stefy & Davide
				// Ripristino del controllo sui campi NOT NULLABLE per problema
				// sull'azienda utilizzatrice
				// in caso di validazione manuale se questa non presente nel DB:
				// manca il campo CODAZINTTIPOAZIENDA
				if ((val == null || (!c.isNullable() && val.equals(""))) && db != null) {
					val = db.getAttribute(c.getData());
				}
			}

			// Se non è nullo trasformo a seconda del formato
			if (!(val == null) && !val.equals("")) {
				// formato numerico
				if (c.getFormat().equalsIgnoreCase("number")) {
					if (val instanceof BigDecimal) {
						record.put(c.getName(), val);
					} else {
						try {
							BigDecimal decVal = new BigDecimal(val.toString());
							// Se non ho errori è un numero
							record.put(c.getName().toUpperCase(), decVal);
						} catch (NumberFormatException e) {
							// non posso trasformare l'oggetto in BigDecimal,
							// non lo inserisco (sarà nullo)
						}

					}
				}

				// formato data, è l'unico non tipato, a causa dei controlli
				else if (c.getFormat().equalsIgnoreCase("date")) {
					// Controllo se è un oggetto Date
					if (val instanceof java.util.Date) {
						record.put(c.getName(), slashFormatter.format(val));
					} else {
						// Se non lo è lo trasformo in Date con il formattatore
						// (sperando che sia una stringa)
						String strDate = val.toString();
						Date datVal = null;
						try {
							// Se contiene o meno gli slash lo formatto di
							// conseguenza
							if (strDate.length() == 8) {
								datVal = simpleFormatter.parse(strDate);
							} else if (strDate.length() == 10) {
								datVal = slashFormatter.parse(strDate);
							} else {
								throw new ParseException("Data non gestibile", i);
							}
							// Se la conversione è andata bene lo inserisco
							// ritrasformato
							// in String
							record.put(c.getName().toUpperCase(), slashFormatter.format(datVal));
						} catch (ParseException e) {
							// Non è una data valida
							// lo annullo
						}
					}
				}

				// formato carattere
				else if (c.getFormat().equalsIgnoreCase("char")) {
					record.put(c.getName().toUpperCase(), val.toString());
				}

				// formato flag
				else if (c.getFormat().equalsIgnoreCase("flag")) {
					// Controllo che sia S o N
					if (val.toString().trim().equalsIgnoreCase("S") || val.toString().trim().equalsIgnoreCase("N")) {
						// Lo aggiungo al record
						record.put(c.getName().toUpperCase(), val.toString());
					} else {
						// Lo annullo perché non è un flag valido
					}
				}
			}
		}
		return record;
	}

	/** Metodi e variabili per la configurazione interna dell'estrattore */
	boolean inrecord = false;
	boolean done = false;

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		// Se ho incontrato l'elemento iniziale
		if (qName.equals("RECORD") && !done) {
			inrecord = true;
			fields = new ArrayList();
		}

		// Se incontro un tag FIELD inserisco un nuovo oggetto nel vettore dei
		// fields
		if (inrecord && qName.equals("FIELD")) {
			String name = attributes.getValue("name");
			String format = attributes.getValue("format");
			String type = attributes.getValue("type");
			String data = attributes.getValue("data");
			String nullable = attributes.getValue("nullable");
			String origin = attributes.getValue("origin");
			String description = attributes.getValue("description");
			if ((name == null) || (data == null)) {
				throw new SAXException("Attributi non corretti nel tag FIELD [name=" + name + "] ");
			}
			try {
				fields.add(new Column(name, format, nullable, type, data, description));
			} catch (Exception e) {
				throw new SAXException(
						"Errore nell'indicazione del tag FIELD [name=" + name + "] Dettagli: " + e.getMessage());
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		// Se ho raggiunto la fine del tracciato corretto esco dalla modalita
		// intrace e ignoro il resto
		if (qName.equals("RECORD") && inrecord) {
			inrecord = false;
			done = true;
		}
	}

	public void endDocument() throws SAXException {
		if (!done) {
			throw new SAXException("Configurazione dell'estrattore impossibile,"
					+ " controllare il formato del file di configuazione");
		}
	}

	public Map getData() {
		return data;
	}

}
