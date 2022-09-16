/*
 * Creato il 27-lug-04
 */
package it.eng.sil.module.movimenti.processors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * @author roccetti
 * 
 *         Processor che esegue l'update delle informazioni sul DB delle unità aziendali già presenti sul DB, funziona
 *         come il processor insertData, ma esegue per prima cosa un confronto tra i dati della map e quelli sul DB, se
 *         trova differenze in campi che debbono essere aggiornati procede all'aggiornamento Per sapere quali campi
 *         aggiornare si basa sulla tabella opportuna del DB (TS_CONFIG_VALIDAZIONE)
 */
public class UpdateUnitaAzienda extends DefaultHandler implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UpdateUnitaAzienda.class.getName());

	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query di insert */
	private TransactionQueryExecutor trans;
	/** Colonne della tabella da inserire */
	private ArrayList cols = new ArrayList();
	/** Dati per la chiave */
	private String keyDataAzienda;
	private String keyDataUnita;
	/** Dati per la tabella */
	private String errorLevel;
	private String tableName;
	private BigDecimal userId;

	/** campi da modificare */
	private ArrayList updatingFieldsNames = new ArrayList();

	/** Formattazione per le date */
	private SimpleDateFormat slashFormatter = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat simpleFormatter = new SimpleDateFormat("ddMMyyyy");
	// Variabili per la configurazione
	private String configFileName = "";
	private boolean ininsert = false;
	private boolean found = false;
	private String updateType;

	/**
	 * Costruttore.
	 * 
	 * @param name
	 *            Nome del processore
	 * @param transexec
	 *            TransactionQueryExecutor da utilizzare per la query
	 * @param configFileName
	 *            Nome del file per la configurazione del processore, deve essere conforme alla specifica riportata nel
	 *            file WEB_INF/conf/import/InsertData.dtd
	 * @param insertName
	 *            Nome del tag insert che contiene i dati per l'inserimento da effettuare, un file di configurazione puo
	 *            contenere diversi tag insert.
	 * @param user
	 *            Codice dell'utente che effettua l'aggiornamento. Se è null l'utente non viene inserito.
	 *            <p>
	 * @exception SAXException
	 *                Se avviene qualche errore nel parsing del file di configurazione.
	 * @exception FileNotFoundException
	 *                Se il file di configurazione non viene trovato.
	 * @exception IOException
	 *                Se avviene un errore in lettura del file di configurazione.
	 * @exception ParserConfigurationException
	 *                Se avviene un errore nella configurazione del parser.
	 * @exception NullPointerException
	 *                Se la table è nulla.
	 */
	public UpdateUnitaAzienda(String name, TransactionQueryExecutor transexec, String configFileName, String updateType,
			BigDecimal user) throws SAXException, FileNotFoundException, IOException, ParserConfigurationException,
			NullPointerException, SQLException {
		this.name = name;
		trans = transexec;

		this.updateType = updateType;
		this.userId = user;

		// Cerco sul DB di quali dati mi devo occupare
		findUpdatingFields(updateType);

		// Apro il file di configurazione
		InputStream configfile = new FileInputStream(configFileName);
		this.configFileName = configFileName;
		// Creo il parser per processarlo
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(true);
		SAXParser parser = spf.newSAXParser();
		parser.parse(configfile, this);
	}

	/**
	 * Processor che aggiorna i dati della testata aziendale: -Cerca i dati correnti sul DB -Guarda quali sono
	 * modificabili -Aggiorna i dati sul DB
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_NULL_RECORD),
					null, warnings, null);
		}

		// Se non ci sono campi da aggiornare ritorno null
		if (cols.size() == 0) {
			return null;
		}

		Statement stmt = null;
		try {

			Object keyObjAzienda = record.get(keyDataAzienda);
			Object keyObjUnita = record.get(keyDataUnita);
			// controllo se ho le chiavi
			if (keyObjAzienda == null || keyObjUnita == null) {
				if (errorLevel.equalsIgnoreCase("ERROR")) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
							"Impossibile ottenere una chiave per aggiornare il record nella tabella: " + tableName
									+ ".",
							warnings, null);
				} else {
					warnings.add(new Warning(MessageCodes.ImportMov.ERR_UPDATE_DATA,
							"Impossibile ottenere una chiave per aggiornare il record nella tabella: " + tableName
									+ "."));
					return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
				}
			}

			// Le converto in string per la query
			String keyAzienda = keyObjAzienda.toString();
			String keyUnita = keyObjUnita.toString();

			// Preparo le stringhe per la query
			String columns = " cdnUtMod = " + userId.toString() + ", dtmMod = SYSDATE"
					+ ", NUMKLOUNITAAZIENDA = NUMKLOUNITAAZIENDA + 1 ";

			// Indica se c'è almeno un campo da aggiornare, altrimenti non
			// faccio la query
			boolean update = false;

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
							warnings.add(new Warning(MessageCodes.ImportMov.ERR_UPDATE_DATA,
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
								warnings.add(new Warning(MessageCodes.ImportMov.ERR_UPDATE_DATA,
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
							warnings.add(new Warning(MessageCodes.ImportMov.ERR_UPDATE_DATA,
									"Impossibile convertire in flag il valore " + val.toString()
											+ " per inserirlo nella colonna " + c.getDescription()
											+ ". Valore posto a null."));
						} else {
							// lo trasformo per la query
							strval = " '" + val.toString().trim() + "' ";
						}
					}
				}

				// Se non è nullo lo inserisco nella query
				if (!strval.equalsIgnoreCase("null")) {
					columns = columns + ", " + c.getName() + " = " + strval;
					update = true;
				}
			}

			// Controllo se sto aggiornando almeno un campo
			if (update) {

				// Creo la query
				String statement = "UPDATE " + tableName + " SET " + columns + " WHERE PRGAZIENDA = " + keyAzienda
						+ " AND PRGUNITA = " + keyUnita;

				// Ne faccio il tracing
				_logger.debug(classname + "::processRecord(): " + statement);

				// Aggiorno il record
				Object result = null;
				try {
					result = trans.executeQueryByStringStatement(statement, null, TransactionQueryExecutor.UPDATE);
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile aggiornare il record in: " + tableName, e);

					// Se ho avuto problemi nella query lo segnalo
					if (errorLevel.equalsIgnoreCase("ERROR")) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
								"Impossibile aggiornare l'unita aziendale", warnings, null);
					} else {
						warnings.add(new Warning(MessageCodes.ImportMov.ERR_UPDATE_DATA,
								"Impossibile aggiornare l'unita aziendale"));
						return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
					}
				}

				// Esamino il risultato
				if (result instanceof Boolean && ((Boolean) result).booleanValue() == true) {
					// Controllo se ho avuto warnings e le riporto
					if (warnings.size() > 0) {
						return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
					} else {
						return null;
					}
				} else if (result instanceof Exception) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile aggiornare il record in: " + tableName,
							(Exception) result);

					// Se ho avuto problemi nella query lo segnalo
					if (errorLevel.equalsIgnoreCase("ERROR")) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
								"Impossibile aggiornare l'unita aziendale", warnings, null);
					} else {
						warnings.add(new Warning(MessageCodes.ImportMov.ERR_UPDATE_DATA,
								"Impossibile aggiornare l'unita aziendale"));
						return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
					}
				} else {
					_logger.debug("Impossibile aggiornare il record in: " + tableName);

					// Se ho avuto problemi nella query lo segnalo
					if (errorLevel.equalsIgnoreCase("ERROR")) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
								"Impossibile aggiornare l'unita aziendale", warnings, null);
					} else {
						warnings.add(new Warning(MessageCodes.ImportMov.ERR_UPDATE_DATA,
								"Impossibile aggiornare l'unita aziendale"));
						return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
					}
				}
			} else
				return null; // if(update)
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile aggiornare il record in: " + tableName, e);

			// Tratto l'errore globale
			if (errorLevel.equalsIgnoreCase("ERROR")) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA), "Impossibile aggiornare l'unita aziendale",
						warnings, null);
			} else {
				warnings.add(new Warning(MessageCodes.ImportMov.ERR_UPDATE_DATA,
						"Impossibile aggiornare l'unita aziendale"));
				return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
			}
		}
	}

	/** Esegue la ricerca sul DB dei nomi dei campi da aggiornare */
	private void findUpdatingFields(String updateType) throws SQLException {

		String query = "SELECT STRNOMECAMPO FROM TS_CONFIG_VALIDAZIONE WHERE STRTIPOAGGIORNAMENTO = '" + updateType
				+ "'";
		SourceBean result = null;
		try {
			result = ProcessorsUtils.executeSelectQuery(query, trans);
		} catch (Exception e) {
			throw new SQLException("Eccezione nella ricerca dei campi da aggiornare per l'unita aziendale");
		}

		// Esamino il risultato
		Vector rows = result.getAttributeAsVector("ROW");
		for (int i = 0; i < rows.size(); i++) {
			updatingFieldsNames.add((String) ((SourceBean) rows.get(i)).getAttribute("STRNOMECAMPO"));
		}
	}

	/** Metodo per la configurazione */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// Se trovo il tag UPDATE controllo di essere entrato nel tag indicato
		// nel costruttore
		if (qName.equalsIgnoreCase("INSERT")) {
			if (attributes.getValue("name").equalsIgnoreCase(updateType)) {
				ininsert = true;
				tableName = attributes.getValue("table");
				errorLevel = attributes.getValue("errorLevel");
			}
		}

		// Se trovo il tag KEY estraggo i valori
		if (ininsert && qName.equalsIgnoreCase("KEY")) {
			keyDataUnita = attributes.getValue("data");
		}

		// Se trovo una colonna annoto i dati in un oggetto Column, ma solo se
		// il nome della colonna è tra
		// quelli che devo modificare
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
			// Se la colonna è quella del prgAzienda prendo l'attributo data
			// come seconda chiave
			if (name.equalsIgnoreCase("PRGAZIENDA")) {
				keyDataAzienda = data;
			} else if (updatingFieldsNames.contains(name)) {
				Column c = new Column(name, format, nullable, type, data, descr);
				cols.add(c);
			}
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

}