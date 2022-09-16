/*
 * Creato il 6-set-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.coop.services;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.movimenti.Field;
import it.eng.sil.module.movimenti.RecordExtractor;

/**
 * Crea l'oggetto <b>records</b> utilizzato dai processors dei movimenti.<br>
 * Se un attributo del SourceBean di input e' presente nel file di configurazione
 * <b>web\WEB-INF\conf\import\processors\&lt;configFileName&gt;</b> allora verra' inserito nella Map records con la
 * chiave specificata nel file di configurazione, altrimenti verra' inserito con la stessa chiave usata nel SourceBean.
 * <b>N.B. da notare che la chiave nella classe HashMap e' case sensitive.</b> I dati verranno inseriti nella tabella
 * am_movimenti_appoggio solo se le chiavi di accesso nell'oggetto records saranno le stesse presenti nel file di
 * configurazione web\WEB-INF\conf\import\processors\InsertAM_MOVIMENTI_APPOGGIO.xml (chiavi sempre case sensitive).
 * 
 * @author giuliani
 */
public class SourceBeanExtractor extends DefaultHandler implements RecordExtractor {

	private SourceBean sBean;
	private Vector movimenti = null;
	private int numMovToProc = 0;
	private Vector fields = new Vector();

	/*
	 * public SourceBeanExtractor(SourceBean sBean, String configFileName) throws Exception { new SourceBeanExtractor(
	 * sBean, configFileName, "ROW"); }
	 */
	/**
	 * @param sBean
	 *            e' il SourceBean in cui sono presenti i dati da ricopiare in records
	 * @param configFileName
	 *            e' il file di configurazione che mappa i dati del tracciato sare.
	 * @param rootKey
	 *            e' la busta del SourceBean dove sono presenti gli attributi dei dati di un movimento. <b>N.B. Il
	 *            SourceBean potrebbe contenere piu' movimenti.<b>
	 */
	public SourceBeanExtractor(SourceBean sBean, String configFileName, String rootKey) throws Exception {
		if (sBean == null)
			throw new Exception();
		this.sBean = sBean;
		movimenti = sBean.getAttributeAsVector(rootKey);

		// Apro il file di configurazione
		InputStream configfile = new FileInputStream(configFileName);

		// Creo il parser per processarlo
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(true);
		SAXParser parser = spf.newSAXParser();
		parser.parse(configfile, this);
		// System.out.println("FIELDS:: "+ fields.toString());
	}

	public int getNumRecord() {
		return movimenti.size();
	}

	public boolean hasNext() {
		if ((numMovToProc + 1) > movimenti.size()) {
			return false;
		}
		return true;
	}

	public Object next() {
		Map movMap = new HashMap();
		SourceBean mov;

		try {
			// Recupero il movimento corrente da processare
			mov = (SourceBean) movimenti.get(numMovToProc);

			// Recupero i campi contenuti nel movimento...
			Iterator iter = fields.iterator();
			String campo;
			String valore;

			while (iter.hasNext()) {
				Field f = ((Field) iter.next());
				campo = f.getName();
				if (mov.containsAttribute(campo)) {
					valore = (String) mov.getAttribute(campo);
					// ...e li metto nella Map
					movMap.put(campo, valore);

					// Rimuovo l'atttributo già memeorizzato
					try {
						mov.delAttribute(campo);
					} catch (SourceBeanException e1) {
						// TODO Blocco catch generato automaticamente
						e1.printStackTrace();
					}
				}
			}

			// Recupero i campi rimanenti contenuti nel movimento...
			Vector campi = mov.getContainedAttributes();
			iter = campi.iterator();

			while (iter.hasNext()) {
				SourceBeanAttribute sBA = ((SourceBeanAttribute) iter.next());
				campo = sBA.getKey();
				valore = (String) sBA.getValue();

				// ...e li metto nella Map
				movMap.put(campo, valore);
			}

			if (movMap.isEmpty()) {
				return null;
			} else {
				numMovToProc++;
				return movMap;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw e;
		}
	}

	public void remove() {
		// TODO Stub di metodo generato automaticamente

	}

	/** Metodi per la configurazione interna dell'estrattore */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		// Se sono nel tracciato corretto ed incontro un tag FIELD inserisco un nuovo oggetto nel vettore dei fields
		if (qName.equals("FIELD")) {
			String name = attributes.getValue("name");
			String init = attributes.getValue("init");
			String ends = attributes.getValue("ends");
			if (name == null || init == null || ends == null) {
				throw new SAXException("Attributi non corretti nel tag FIELD [name=" + name + "] nel tracciato ");
			}
			try {
				fields.add(new Field(name, (new Integer(init)).intValue(), (new Integer(ends)).intValue()));
			} catch (Exception e) {
				throw new SAXException("Errore nell'indicazione del tag FIELD [name=" + name + "] nel tracciato");
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		// Se ho raggiunto la fine del file e non ho una configurazione valida lancio un'eccezione
		// if (qName.equals("TRACES")) { throw new SAXException("Tracciato non trovato"); }
	}
}
