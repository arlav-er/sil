package it.eng.sil.util.amministrazione.impatti;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;

/**
 * TRANS(poser): Parte di gestione della trasposizione dei campi di un record all'interno di una stringa.
 * 
 * NB: la classe NON E' "PUBLIC" ma è visibile solo all'interno del pacchetto poiché viene usata dalla classe
 * EsportaMigrazioniOutput.
 * 
 * @author Luigi Antenucci
 */
final class EsportaMigrazioniTrans implements Serializable {

	private static final String thisClassName = StringUtils.getClassName(EsportaMigrazioniTrans.class);

	private static final int BUFFER_SIZE = 1620 - 2;

	private char initChar;
	private char blankChar;
	private List roles; // Lista di oggetti EmtRole

	private transient DateFormat formatData;

	private String obblFieldsWarning; // Per memorizzare il warning sui campi
										// obbligatori

	/**
	 * Inizializzazione: legge il file di configurazione XML dato e popola la lista con tutti gli elementi "ROLE" detti
	 * dal file XML.
	 */
	public EsportaMigrazioniTrans(String mappingFile)
			throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {

		// Apro il file di configurazione
		InputStream configfile = new FileInputStream(mappingFile);

		EmtLoader emtLoader = new EmtLoader();

		// Usa il parser di default (non-validating)
		SAXParserFactory factory = SAXParserFactory.newInstance();

		// Esegue il parsing dell'xml
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(configfile, emtLoader);

	}

	private DateFormat getFormatData() {

		if (formatData == null) {
			// Inizializzo formattatori
			String patternData = "ddMMyyyy";
			formatData = DateFormat.getDateInstance();
			if (formatData instanceof SimpleDateFormat) {
				((SimpleDateFormat) formatData).applyPattern(patternData);
			} else {
				formatData = new SimpleDateFormat(patternData); // MAH
			}
		}
		return formatData;
	}

	/**
	 * Esegue la trasposizione dei campi del record (passato come SourceBean nel parametro 'row') in una stringa (che
	 * verrà resa). Si usano le regole definite nella lista "roles".
	 */
	public final String transpose(SourceBean row) throws Exception {

		// Creo il buffer di caratteri e lo inizializzo tutto con spazi bianchi.
		char[] buffer = new char[BUFFER_SIZE];
		Arrays.fill(buffer, initChar);
		// Sintassi: fill(byte[] a, byte val)

		obblFieldsWarning = "";

		if (row != null) {
			// Spazzolo tutte le 'role'
			Iterator iter = roles.iterator();
			while (iter.hasNext()) {
				// Recupero la regola da applicare
				EmtRole role = (EmtRole) iter.next();

				// Recupero il valore (in stringa) del campo indicato da
				// "field".
				String valueStr = getRowFieldStr(row, role);

				// Il campo del record e' vuoto? Occorre segnalarlo come
				// WARNING?
				if (StringUtils.isEmpty(valueStr)) {

					String obbl = role.getObbl();
					boolean signalWarning;
					if (obbl.equalsIgnoreCase("N") || obbl.equalsIgnoreCase("B"))
						signalWarning = false;
					else if (obbl.equalsIgnoreCase("S"))
						signalWarning = true;
					else {
						String fieldValue = (String) row.getAttribute("CODTIPOMOV");
						// "fieldValue" può essere "AMM", "CES", "PRO" o "TRA".
						// "obbl" può essere "CES", "PRO" o "TRA" o "PRO_TRA" e
						// altre composizioni
						// Ossia se "obbl" ha come sottostringa "fieldValue", il
						// campo è obbligatorio!
						signalWarning = (obbl.indexOf(fieldValue) >= 0);
					}
					// Segnalo il WARNING (se rilevato)
					if (signalWarning) {
						// NON INTERROMPO L'ELABORAZIONE DEL RECORD, MA LO
						// SEGNALERO' NEL LOG.
						if (obblFieldsWarning.length() > 0)
							obblFieldsWarning += ", ";
						obblFieldsWarning += role.getField();
					}
				}

				// In ogni caso, inserisco la stringa nel buffer
				// (accodandole degli spazi bianchi se necessario)
				char[] valueBuf = valueStr.toCharArray();

				int beginIdx = role.getBegin() - 1; // In config partono da 1;
				int endIdx = role.getEnd() - 1; // il buffer si parte da 0

				int maxLen = endIdx - beginIdx + 1; // Dimensione massima da
													// riempire
				int valueLen = valueBuf.length; // Dimensione dei dati letti
				if (valueLen > maxLen)
					valueLen = maxLen; // Ignoro parte dei dati eccedente

				System.arraycopy(valueBuf, 0, buffer, beginIdx, valueLen);
				// Sintassi: arraycopy(srcArr, srcPos, destArr, destPos, length)

				// Riempio con spazi bianchi la zona eventualmente non riempita
				if (valueLen < maxLen) {
					Arrays.fill(buffer, beginIdx + valueLen, endIdx + 1, blankChar);
					// Sintassi: fill(byte[] a, int fromIndex, int toIndex, byte
					// val)
					// (nota: toIndex è escluso!)
				}

			} // while
		} // if row!=null

		return new String(buffer);
	}

	/**
	 * Rende la stringa con i nomi dei campi obbligatori NON riempiti che sono stati incontrati nella precedente
	 * operazione di "transpose()". Se rende stringa vuota vuol dire che non si sono stati errori e tutti i campi
	 * obbligatori sono stati trovati valorizzati.
	 */
	public final String getLastTransposeObblFieldsWarning() {
		return obblFieldsWarning;
	}

	/**
	 * Rende l'attributo "field" del SourceBean "row" rappresentato come Stringa (eventualmente convertito
	 * opportunamente in una stringa). Se il valore è già una stringa, lo rende tale e quale; altrimenti guarda la
	 * classe effettiva dell'oggetto: - se è un Date, lo rende in formato "GGMMAAAA"; se non è nessuna di quelle sopra,
	 * rende ciò che rende un ".toString()".
	 * 
	 * Se il campo "fixed" della "role" è valorizzato, si renderà sempre tale valore ignorando il contenuto del
	 * SourceBean "row".
	 * 
	 * Se il campo "obbl" vale 'B' (bianchi), si renderà sempre una stringa vuota ignorando il contenuto del SourceBean
	 * "row".
	 * 
	 * NB: non rende mai stringa nulla: tuttalpiù vuota!
	 */
	private String getRowFieldStr(SourceBean row, EmtRole role) {

		// Controllo del campo "fixed": usa un valore fisso
		String fixedValue = role.getFixed();
		if (StringUtils.isFilled(fixedValue)) {
			return fixedValue;
		} else if (role.getObbl().equalsIgnoreCase("B")) {
			return "";
		} else {

			// Recupero dal RECORD il valore del campo "field" dato dalla regola
			Object fieldValue = row.getAttribute(role.getField());

			// Formattazione del campo (se non è già una stringa)
			String ret;
			if ((fieldValue == null) || (fieldValue instanceof String)) {
				ret = (String) fieldValue;
			} else {
				if (fieldValue instanceof Date) {
					ret = getFormatData().format((Date) fieldValue);
				} else {
					// System.out.println("CLASS:
					// "+fieldValue.getClass().getName());
					// System.out.println(" str: "+fieldValue.toString());
					ret = fieldValue.toString();
				}
			}
			return StringUtils.notNull(ret);
		}
	}

	/**
	 * Classe per mantenere la tripletta di attributi del tag "ROLE" letto dal file di configurazione.
	 */
	private class EmtRole implements Serializable {

		private final int begin, end;
		private final String field;
		private final String fixed;
		private final String obbl;

		public EmtRole(int begin, int end, String field, String fixed, String obbl) {
			this.begin = begin;
			this.end = end;
			this.field = field;
			this.fixed = fixed;
			this.obbl = obbl;
		}

		public int getBegin() {
			return begin;
		}

		public int getEnd() {
			return end;
		}

		public String getField() {
			return field;
		}

		public String getFixed() {
			return fixed;
		}

		public String getObbl() {
			return obbl;
		}

		/**
		 * Rende TRUE se il "role" è valido (ossia se l'intervallo tra begin-end è definito correttamente).
		 */
		public boolean isValid() {
			return (begin <= end) && (begin >= 1) && (end <= BUFFER_SIZE);
		}

		/**
		 * Rende TRUE se il "role" è "fuori" da quello passato (ossia se gli intervalli tra begin-end NON si
		 * sovrappongono).
		 */
		public boolean isOut(EmtRole f) {
			return (f.begin > end) || (f.end < begin);
		}

	} // classe EmtRole

	/**
	 * Classe per il caricamento del file di configurazione del traspositore. Side-effect: popola la lista "roles" del
	 * suo EsportaMigrazioniTrans.
	 */
	private class EmtLoader extends DefaultHandler {

		private static final String ROLES = "ROLES";
		private static final String ROLE = "ROLE";

		private static final String OBBL_N = "N";

		private int rolePos;

		public void startDocument() throws SAXException {
			initChar = '.';
			blankChar = ' ';
			roles = new ArrayList();

			rolePos = 0;
		}

		public void startElement(String namespaceURI, String lName, // local
																	// name
				String qName, // qualified name
				Attributes attrs) throws SAXException {

			if (qName.equalsIgnoreCase(ROLE)) {

				rolePos++;

				try {
					int begin = getAttributeInt(attrs, "begin");
					int end = getAttributeInt(attrs, "end");
					String field = getAttribute(attrs, "field");
					String fixed = getAttributeOpzionale(attrs, "fixed");
					String obbl = getAttributeOpzionale(attrs, "obbl", OBBL_N);
					// (possono generare SAXException)

					// Creo l'oggetto con i dati del "role"
					EmtRole role = new EmtRole(begin, end, field, fixed, obbl);

					// CONTROLLI DI VALIDITA'
					if (!role.isValid()) {
						throw new SAXException("Valori di BEGIN/END non corretti");
					}

					Iterator iter = roles.iterator();
					while (iter.hasNext()) {
						EmtRole roleOld = (EmtRole) iter.next();
						if (!roleOld.isOut(role)) {
							throw new SAXException("L'intervallo si sovrappone a uno esistente, ");
						}
					}

					// Se sono ancora qua, tutto va bene: inserisco il campo
					// nella lista.
					roles.add(role);
				} catch (SAXException spe) {
					throw new SAXException(spe.getMessage() + " nel campo " + ROLE + " in posizione " + rolePos);
				}
			} else if (qName.equalsIgnoreCase(ROLES)) {
				initChar = getAttributeOpzionaleChar(attrs, "initChar", initChar);
				blankChar = getAttributeOpzionaleChar(attrs, "blankChar", blankChar);
			}
		}

		private String getAttributeOpzionale(Attributes attrs, String name) throws SAXException {
			return StringUtils.notNull(attrs.getValue(name));
		}

		private String getAttributeOpzionale(Attributes attrs, String name, String predefinito) throws SAXException {
			String value = attrs.getValue(name);
			if (StringUtils.isEmpty(value))
				return predefinito;
			else
				return value;
		}

		private String getAttribute(Attributes attrs, String name) throws SAXException {
			String value = attrs.getValue(name);
			if (value == null) {
				throw new SAXException("Manca l'attributo " + name);
			}
			return value;
		}

		private int getAttributeInt(Attributes attrs, String name) throws SAXException {
			String value = getAttribute(attrs, name);
			try {
				return Integer.parseInt(value);
			} catch (Exception ex) {
				throw new SAXException("Numero non corretto nell'attributo " + name);
			}
		}

		private char getAttributeOpzionaleChar(Attributes attrs, String name, char predefinito) throws SAXException {
			String value = attrs.getValue(name);
			if (value == null)
				return predefinito;
			else
				return Character.toUpperCase(value.charAt(0));
		}

	} // classe EmtLoader

} // classe EsportaMigrazioniTrans
