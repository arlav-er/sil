package it.eng.sil.util.batch;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.movimenti.processors.Column;
import it.eng.sil.module.movimenti.processors.Warning;

public class ScriptGenerator extends DefaultHandler {
	private String name;
	/** Colonne della tabella da inserire */
	private ArrayList cols = new ArrayList();
	/** Dati per la chiave */
	private String keyName;
	private String keyFormat;
	private boolean keyInRecord;
	private boolean specifyUser;
	private String keyData;
	/** Dati per la tabella */
	private String errorLevel;
	private String tableName;
	private BigDecimal userId;
	/** Formattazione per le date */
	private SimpleDateFormat slashFormatter = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat simpleFormatter = new SimpleDateFormat("ddMMyyyy");

	// Variabili per la configurazione
	private String configFileName = "";
	private boolean ininsert = false;
	private boolean found = false;
	private String insertName;

	private ArrayList warnings = null;

	public ScriptGenerator(String configFileName, String insertName, String name, BigDecimal userId)
			throws SAXException, ParserConfigurationException, IOException {
		this.configFileName = configFileName;
		this.insertName = insertName;
		this.name = name;
		this.userId = userId;

		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(true);
		SAXParser parser = spf.newSAXParser();
		parser.parse(this.configFileName, this);

		warnings = new ArrayList();
	}

	public String generaScript(Map record, Boolean isContextAvvCVE) throws SourceBeanException {
		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return "";
		}

		try {
			// Preparo le stringhe per la query

			// String columns = " ( PRGMOVIMENTOAPP, PRGMOVIMENTOAPPCVE";
			// if(isContextAvvCVE) values = "( prg1, prg2";
			// else values = "( S_AM_MOVIMENTO_APPOGGIO.NEXTVAL, null";
			String columns = " ( PRGMOVIMENTOAPP";
			String values = "( S_AM_MOVIMENTO_APPOGGIO.NEXTVAL";
			// Processo il vettore delle colonne e creo le stringhe per la query
			for (int i = 0; i < cols.size(); i++) {
				Column c = (Column) cols.get(i);

				Object val = "null";

				// Recupero il valore della colonna
				if (c.isAbsolute()) {
					val = c.getData();
				} else {
					val = record.containsKey(c.getData()) ? record.get(c.getData()) : "null";
				}

				String strval = "null";
				// Se non è nullo trasformo il valore in stringa a seconda del
				// formato
				if (!val.toString().equalsIgnoreCase("null")) {
					// formato numerico
					if (c.getFormat().equalsIgnoreCase("number")) {
						// Controllo che sia un numero
						try {
							Integer.parseInt(val.toString());
							// Se non ho errori è un numero e lo trasformo in
							// Stringa per la query
							strval = val.toString();
						} catch (NumberFormatException e) {
							// Segnalo che non è un numero
							warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA,
									"Impossibile convertire in numero il valore " + val.toString()
											+ " per inserirlo nella colonna " + c.getDescription()
											+ ". Valore posto a null."));
						}
					} else if (c.getFormat().equalsIgnoreCase("float")) {
						try {
							String strFloat = (val.toString()).replace(',', '.');
							Float.parseFloat(strFloat);
							// Se non ho errori è un numero e lo trasformo in
							// Stringa per la query
							strval = strFloat;
						} catch (NumberFormatException e) {
							// Segnalo che non è un numero
							warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA,
									"Impossibile convertire in numero il valore " + val.toString()
											+ " per inserirlo nella colonna " + c.getDescription()
											+ ". Valore posto a null."));
						}
					} else if (c.getFormat().equalsIgnoreCase("long")) {
						try {
							Long.parseLong(val.toString());
							// Se non ho errori è un numero e lo trasformo in
							// Stringa per la query
							strval = val.toString();
						} catch (NumberFormatException e) {
							// Segnalo che non è un numero
							warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA,
									"Impossibile convertire in numero il valore " + val.toString()
											+ " per inserirlo nella colonna " + c.getDescription()
											+ ". Valore posto a null."));
						}
					}
					// formato data
					else if (c.getFormat().equalsIgnoreCase("date")) {
						// Controllo se non è un oggetto Date
						if (!(val instanceof java.util.Date)) {
							// Se non lo è lo trasformo in Date con il
							// formattatore
							// (sperando che sia una stringa)
							String strDate = val.toString();
							try {
								// Se contiene o meno gli slash lo formatto di
								// conseguenza
								if (strDate.length() == 8) {
									val = simpleFormatter.parse(strDate);
								} else if (strDate.length() == 10) {
									val = slashFormatter.parse(strDate);
								} else {
									// Non posso trattare date di formato
									// diverso
									throw new ParseException("Data in formato non riconosciuto: " + strDate, 0);
								}
								// Se la conversione è andata bene lo trasformo
								// per la query
								strval = " TO_DATE('" + slashFormatter.format(val) + "', 'DD/MM/YYYY') ";
							} catch (ParseException e) {
								// Segnalo che non è una data valida
								warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA,
										"Impossibile convertire in data il valore " + val.toString()
												+ " per inserirlo nella colonna " + c.getDescription()
												+ ". Valore posto a null."));
							}
						} else {
							// Ho già una date, la formatto per la query
							strval = " TO_DATE('" + slashFormatter.format(val) + "', 'DD/MM/YYYY') ";
						}
					}
					// formato carattere
					else if (c.getFormat().equalsIgnoreCase("char")) {
						strval = " '" + StringUtils.replace(val.toString().trim(), "'", "''") + "' ";
					}
					// formato flag
					else if (c.getFormat().equalsIgnoreCase("flag")) {
						// Controllo che sia S o N
						if (!(val.toString().trim().equalsIgnoreCase("S")
								|| val.toString().trim().equalsIgnoreCase("N"))) {
							// Segnalo che non è un flag valido
							warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA,
									"Impossibile convertire in flag il valore " + val.toString()
											+ " per inserirlo nella colonna " + c.getDescription()
											+ ". Valore posto a null."));
						} else {
							// lo trasformo per la query
							strval = " '" + val.toString().trim() + "' ";
						}
					}
				}

				// Se il valore strval è null controllo se il campo è
				// annullabile, altrimenti lancio una warning / errore
				if (strval.equalsIgnoreCase("null") && !c.isNullable()) {
					warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA,
							"Impossibile inserire null nel campo " + c.getDescription() + ". Inserimento fallito. "));
					return "";
				} else if (!strval.equalsIgnoreCase("null")) {
					// Se non è nullo lo inserisco nella query
					columns = columns + ", " + c.getName();
					values = values + ", " + strval;
				}
			}

			// Controllo se devo inserire i dati dell'utente
			if (specifyUser) {
				columns = columns + ", CDNUTINS, DTMINS, CDNUTMOD, DTMMOD ";
				values = values + ", " + userId + ", SYSDATE, " + userId + ", SYSDATE ";
			}

			columns = columns + ")";
			values = values + ")";

			// Creo la query
			String statement = "INSERT INTO " + tableName + " " + columns + " VALUES " + values;

			return statement;

		} catch (Exception e) {
			warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA, e.getMessage()));
			return "";
		}
	}

	/** Metodo per la configurazione */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// Se trovo il tag INSERT controllo di essere entrato nel tag indicato
		// nel costruttore
		if (qName.equalsIgnoreCase("INSERT")) {
			if (attributes.getValue("name").equalsIgnoreCase(insertName)) {
				ininsert = true;
				tableName = attributes.getValue("table");
				errorLevel = attributes.getValue("errorLevel");
				specifyUser = attributes.getValue("specifyUser").equalsIgnoreCase("true") ? true : false;
			}
		}

		// Se trovo il tag KEY estraggo i valori
		if (ininsert && qName.equalsIgnoreCase("KEY")) {
			keyName = attributes.getValue("name");
			keyFormat = attributes.getValue("format");
			keyInRecord = attributes.getValue("type").equalsIgnoreCase("record") ? true : false;
			keyData = attributes.getValue("data");
		}

		// Se trovo una colonna annoto i dati in un oggetto Column
		if (ininsert && qName.equalsIgnoreCase("COLUMN")) {
			String name = attributes.getValue("name").trim();
			String format = attributes.getValue("format");
			String nullable = attributes.getValue("nullable");
			String type = attributes.getValue("type");
			String data = attributes.getValue("data").trim();
			String descr = attributes.getValue("description");
			if (name.equalsIgnoreCase("") || data.equalsIgnoreCase("")) {
				throw new SAXException("Attributo name o data vuoto, controllare il file di configurazione "
						+ configFileName + ". \n");
			}
			Column c = new Column(name, format, nullable, type, data.toUpperCase(), descr);
			cols.add(c);
		}
	}

	/** Metodo per la configurazione */
	public void endElement(String uri, String localName, String qName) throws SAXException {

		// Se trovo il tag TABLE corrispondente a quello di ingresso esco dal
		// tracciato.
		if (ininsert && qName.equalsIgnoreCase("INSERT")) {
			ininsert = false;
			found = true;
		}
	}

	/** Metodo per la configurazione */
	public void endDocument() throws SAXException {
		// Se esco dal parsing controllo di aver configurato il processore,
		// altrimenti lancio eccezione
		if (!found) {
			throw new SAXException("Configurazione del processore non trovata, controllare il file di configurazione "
					+ configFileName + ". \n");
		}
	}

	public ArrayList getWarnings() {
		return this.warnings;
	}

	public Hashtable sbToHash(SourceBean result) throws SQLException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		Hashtable recordTable = new Hashtable();

		// estraggo gli attributi dal SourceBean e popolo la map
		Vector v = result.getContainedAttributes();
		for (int i = 0; i < v.size(); i++) {
			SourceBeanAttribute attribute = (SourceBeanAttribute) v.get(i);
			// Ritrovo il nome della colonna
			String colName = attribute.getKey();
			// Ritrovo il dato associato
			Object colValue = attribute.getValue();
			// Se non è nullo lo inserisco in tabella
			if ((colName != null) && (colValue != null)) {
				// Se è una data la riformatto a stringa DD/MM/YYYY, è
				// necessario per come sono stati
				// implementati i controlli
				if (colValue instanceof java.util.Date) {
					colValue = formatter.format(colValue);
				}
				recordTable.put(colName, colValue);
			}
		}
		// recordTable.put("CONTEXT", "validazioneMassiva");
		return recordTable;
	}
}
