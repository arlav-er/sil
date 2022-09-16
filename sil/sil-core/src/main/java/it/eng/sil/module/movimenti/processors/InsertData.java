package it.eng.sil.module.movimenti.processors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.trasferimentoRamoAz.TrasferimentoRamoAzRequestParams;

/**
 * Inserisce i dati indicati nella tabella indicata<br>
 * La tabella e l'esatta sequenza dei dati da inserire (e il loro formato) possono essere configurati dinamicamente nel
 * costruttore.
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class InsertData extends DefaultHandler implements RecordProcessor {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertData.class.getName());
	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query di insert */
	private TransactionQueryExecutor trans;
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

	/**
	 * Costruttore.
	 * 
	 * @param name
	 *            Nome del processore
	 * @param transexec
	 *            transaction query executor da utilizzare per la query
	 * @param configFileName
	 *            Nome del file per la configurazione del processore, deve essere conforme alla specifica riportata nel
	 *            file WEB_INF/conf/import/InsertData.dtd
	 * @param insertName
	 *            Nome del tag insert che contiene i dati per l'inserimento da effettuare, un file di configurazione puo
	 *            contenere diversi tag insert.
	 * @param user
	 *            Codice dell'utente che effettua l'inserimento. Se è null l'utente non viene inserito.
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
	public InsertData(String name, TransactionQueryExecutor transexec, String configFileName, String insertName,
			BigDecimal user) throws SAXException, FileNotFoundException, IOException, ParserConfigurationException,
			NullPointerException {
		this.name = name;
		trans = transexec;
		if (insertName == null) {
			throw new NullPointerException("table da riempire nulla");
		}

		this.insertName = insertName;
		this.userId = user;

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
	 * Processa il record e inserisce i dati in tabella.
	 * 
	 * @return Un SourceBean con la seguente struttura in DTD: <br>
	 *         &gt;!ELEMENT PROCESSOR (MESSAGE?)&lt; Se vuoto l'elaborazione e andata a buon fine<br>
	 *         &gt;!ELEMENT MESSAGE EMPTY&lt; Se presente ci sono stati errori o warning<br>
	 *         <br>
	 *         &gt;!ATTLIST PROCESSOR name CDATA #REQUIRED&lt; indica il nome del processore<br>
	 *         &gt;!ATTLIST PROCESSOR class CDATA #REQUIRED&lt; indica la classe del processore<br>
	 *         &gt;!ATTLIST PROCESSOR RESULT (ERROR|WARNING) #REQUIRED&lt; indica il livello di problemi riscontrato
	 *         <br>
	 *         &gt;!ATTLIST MESSAGE text CDATA #REQUIRED&lt; indica il messaggio da riportare all'utente<br>
	 *         <p>
	 *         <p>
	 * @exception SourceBeanException
	 *                Se avviene un errore nella creazione del SourceBean di risposta
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException {
		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_NULL_RECORD),
					null, warnings, null);
		}

		Statement stmt = null;
		try {

			Object keyObj;
			// Guardo se mi devo procurare una nuova chiave o se la devo
			// estrarre dal record
			if (!keyInRecord) {
				// Creo la nuova chiave
				keyObj = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_" + tableName);
			} else {
				keyObj = record.get(keyData);
			}
			// controllo se ho la chiave
			if (keyObj == null) {
				if (errorLevel.equalsIgnoreCase("ERROR")) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
							"Impossibile ottenere un progressivo per inserire il record nella tabella: " + tableName
									+ ".",
							warnings, null);
				} else {
					warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA,
							"Impossibile ottenere un progressivo per inserire il record nella tabella: " + tableName
									+ "."));
					return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
				}
			}

			// La converto in string per la query
			String key;
			if (keyFormat.equalsIgnoreCase("number")) {
				key = keyObj.toString();
			} else {
				key = "'" + keyObj.toString() + "'";
			}

			// Preparo le stringhe per la query
			String columns = " ( " + keyName;
			String values = "( " + key + " ";
			// mancinid: Trasformazione Ramo Azienda
			// Setto la chiave nella session nel caso di insert into AM_MOVIMENTO_APPOGGIO:
			if (tableName.equalsIgnoreCase(TrasferimentoRamoAzRequestParams.TABELLA_MOVIMENTO_APPOGGIO)) {
				RequestContainer rc = RequestContainer.getRequestContainer();
				SessionContainer sc = rc.getSessionContainer();
				sc.delAttribute(TrasferimentoRamoAzRequestParams.PRGMOVIMENTOAPP);
				sc.setAttribute(TrasferimentoRamoAzRequestParams.PRGMOVIMENTOAPP, key);
			}

			// Processo il vettore delle colonne e creo le stringhe per la query
			for (int i = 0; i < cols.size(); i++) {
				Column c = (Column) cols.get(i);

				Object val = "null";

				// ...togliere
				// TracerSingleton.log(Values.APP_NAME,
				// TracerSingleton.CRITICAL, "::InsertData: column -name="+
				// c.getName()+" -data="+ c.getData());
				// System.out.println("::InsertData: column -name="+
				// c.getName()+" -data="+ c.getData());

				// Recupero il valore della colonna
				if (c.isAbsolute()) {
					val = c.getData();
				} else {
					val = record.containsKey(c.getData()) ? record.get(c.getData()) : "null";
				}

				// ...togliere
				// TracerSingleton.log(Values.APP_NAME,
				// TracerSingleton.CRITICAL, "::InsertData: column -value=" +
				// val);
				// System.out.println("::InsertData: column -value=" + val);

				// System.out.println("InsertData: colonne: " + c.getName() +
				// ","+ c.getData() + "," + c.getFormat());
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
					if (errorLevel.equalsIgnoreCase("ERROR")) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
								"Impossibile inserire null nel campo " + c.getDescription() + ". Inserimento fallito. ",
								warnings, null);
					} else {
						warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA,
								"Impossibile inserire null nel campo " + c.getDescription()
										+ ". Inserimento fallito. "));
						return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
					}
				} else if (!strval.equalsIgnoreCase("null")) {
					// Se non è nullo lo inserisco nella query
					columns = columns + ", " + c.getName();
					values = values + ", " + strval;

					// ...togliere
					// TracerSingleton.log(Values.APP_NAME,
					// TracerSingleton.CRITICAL, "::InsertData: -column="+
					// columns +"\n -value=" + val);
					// System.out.println("::InsertData: -column="+ columns +"\n
					// -value=" + val);
				}
				/*
				 * else { String nomeCampo = c.getName(); String nomeData = c.getData(); String valueUC
				 * =(String)record.get(nomeCampo.toUpperCase()); //System.out.println("il campo " + nomeCampo + " non e'
				 * presente nel record. UpperCase? valore="+valueUC); }
				 */
			}

			// Controllo se devo inserire i dati dell'utente
			if (specifyUser) {
				columns = columns + ", cdnUtIns, dtmIns, cdnUtMod, dtmMod ";
				values = values + ", " + userId + ", SYSDATE, " + userId + ", SYSDATE ";
			}

			columns = columns + ")";
			values = values + ")";

			// Creo la query
			String statement = "INSERT INTO " + tableName + " " + columns + " VALUES " + values;

			// Ne faccio il tracing
			_logger.debug(classname + "::processRecord(): " + statement);

			// Inserisco il record
			Object result = null;
			try {
				result = trans.executeQueryByStringStatement(statement, null, TransactionQueryExecutor.INSERT);
			} catch (EMFInternalError emfie) {
				_logger.fatal("::processRecord():", (Exception) emfie);
				// Tratto l'errore nell'inserimento
				if (errorLevel.equalsIgnoreCase("ERROR")) {
					_logger.fatal("::processRecord():", (Exception) emfie);
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA), null, warnings, null);
				} else {
					warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA, null));
					return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
				}
			}

			// Se ho un'eccezione nel risultato lo segnalo
			if (result instanceof Exception) {
				_logger.fatal("::processRecord():", (Exception) result);
				// Tratto l'errore nella query
				if (errorLevel.equalsIgnoreCase("ERROR")) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA), null, warnings, null);
				} else {
					warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA, null));
					return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
				}
			} else if (result instanceof Boolean && ((Boolean) result).booleanValue() == true) {
				// Se la chiave è nuova la inserisco dove indicato dalla
				// configurazione
				if (!keyInRecord) {
					record.put(keyData, keyObj);
				}
				// Controllo se ho avuto warnings e le riporto
				if (warnings.size() > 0) {
					return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
				} else {
					return null;
				}
			} else {
				if (errorLevel.equalsIgnoreCase("ERROR")) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
							"Impossibile inserire il record nella tabella: " + tableName + ".", warnings, null);
				} else {
					warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA,
							"Impossibile inserire il record nella tabella: " + tableName + "."));
					return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
				}
			}
		} catch (Exception e) {
			_logger.fatal("::processRecord():", e);
			// Tratto l'errore globale
			if (errorLevel.equalsIgnoreCase("ERROR")) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA), e.getMessage(), warnings, null);
			} else {
				warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA, e.getMessage()));
				return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
			}
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
			Column c = new Column(name, format, nullable, type, data, descr);
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
}