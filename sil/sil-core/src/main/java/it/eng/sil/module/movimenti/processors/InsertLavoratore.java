package it.eng.sil.module.movimenti.processors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * Processor che inserisce un lavoratore nel DB se non è gia presente. Effettua la ricerca a partire dal codice fiscale.
 * Ritorna nel record il cdnLavoratore associato al lavoratore (trovato o inserito) associandolo alla proprieta
 * CDNLAVORATORE.
 * <p>
 * 
 * @author Paolo Roccetti
 **/
public class InsertLavoratore implements RecordProcessor {

	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query di insert */
	private TransactionQueryExecutor trans;
	/** Identificatore utente */
	private BigDecimal userId;
	/** Processore per l'inserimento dei dati */
	private RecordProcessor insert;
	/** Processore per la ricerca del codComDom del lavoratore a partire dal CODCPI */
	private RecordProcessor findCodComDom;
	/** Processore per l'inserimento dei dati sul titolo di studio del Lavoratore */
	private RecordProcessor insertTitoloStudio;
	/** Processore che imposta in AN_LAV_STORIA_INF i dati sul cpi del lavoratore */
	private RecordProcessor updateCpiLav;
	// DAVIDE 31/05/2007: modifica inseguito a adozione comunicazioni obbligatorie UNICO
	/** Processore per l'inserimento dei dati sul permesso di soggiorno */
	private RecordProcessor insertInfoPermSogg;
	/** Processore per la verifica delle catene movimenti */
	private RecordProcessor controllaCateneMovimenti;
	// SourceBean contenente i dati di TS_GENERALE
	private SourceBean infoGenerali;

	/**
	 * Costruttore, occorre specificare un identificatore utente per inserire i dati nella tabella. Utilizza un
	 * processore di classe InsertData per inserire i dati in tabella quando necessario. Per configurarlo utilizza il
	 * file passato come ultimo parametro
	 * <p>
	 * 
	 * @param name
	 *            Nome del processore
	 * @param transexec
	 *            TransactionQueryExecutor da utilizzare per le query
	 * @param user
	 *            Identificatore utente per inserire i dati
	 * @param configFileName
	 *            file di configurazione per l'InsertData contenuto in questo processor
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
	 *                Se l'identificatore utente è nullo.
	 */
	public InsertLavoratore(String name, TransactionQueryExecutor transexec, BigDecimal user, String configFileName,
			SourceBean _infoGenerali) throws SAXException, FileNotFoundException, IOException,
			ParserConfigurationException, NullPointerException {
		this.name = name;
		trans = transexec;
		if (user == null) {
			throw new NullPointerException("Identificatore utente nullo");
		}
		userId = user;
		// Provo ad istanziare il processore InsertData per l'inserimento dei dati
		insert = new InsertData("Inserimento Lavoratore", transexec, configFileName, "INSERT_LAVORATORE", user);
		// Provo ad istanziare il processore per la ricerca del codComDom
		findCodComDom = new SelectComDomLavFromCPI("Ricerca Comune di Domicilio", transexec);

		/*
		 * Tolto il 31/05/2007 visto che il 28/09/2004 Paolo Roccetti ha toloto la gestione su indicazioni agenzia AERL
		 * (vedi dopo) //Provo ad istanziare il processore per l'inserimento del titolo di studio insertTitoloStudio =
		 * new InsertData("Inserimento Titolo di Studio", transexec, configFileName, "INSERT_TITOLO_STUDIO", user);
		 */
		// Provo ad istanziare il processore per l'inserimento delle informazioni sul permesso di soggiorno
		insertInfoPermSogg = new InsertData("Inserimento Permesso di Soggiorno", transexec, configFileName,
				"INSERT_INFO_EXTRACOM", user);
		// Provo ad istanziare il processore per l'update del CPI lavoratore
		updateCpiLav = new UpdateCpiLav("Aggiornamento Cpi Lavoratore", transexec, user);
		controllaCateneMovimenti = new ControllaCateneMovimenti("Controlla Catene Errate", transexec);

		this.infoGenerali = _infoGenerali;
	}

	/**
	 * Processa il record. Cerca il lavoratore a partire dal Codice Fiscale, se non lo trova lo inserisce. Ritorna il
	 * cdnLavoratore nella proprieta CDNLAVORATORE del Record.
	 * <p>
	 * 
	 * @exception SourceBeanException
	 *                Se avviene un errore nella creazione del SourceBean di risposta
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();
		// Codice della provincia SIL
		String codProvinciaSil = (String) infoGenerali.getAttribute("CODPROVINCIASIL");

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", warnings, nested);
		}

		// Se ho già il progressivo del lavoratore non devo fare altro.
		BigDecimal cdnLavoratore = (BigDecimal) record.get("CDNLAVORATORE");
		boolean lavoratoreEsiste = true;
		if (cdnLavoratore == null) {

			// Controllo che i parametri necessari per la query ci siano
			String codfisc = (String) record.get("STRCODICEFISCALE");
			String cognome = (String) record.get("STRCOGNOME");
			String nome = (String) record.get("STRNOME");

			if (codfisc == null) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
						"Impossibile trovare il Codice Fiscale del lavoratore.", warnings, nested);
			}
			codfisc = codfisc.toUpperCase();
			// Creo la query
			String statement = "SELECT cdnLavoratore FROM AN_LAVORATORE WHERE UPPER(strCodiceFiscale) = '" + codfisc
					+ "'";
			SourceBean result = null;
			try {
				result = ProcessorsUtils.executeSelectQuery(statement, trans);
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "Impossibile cercare il lavoratore.",
						warnings, nested);
			}

			// Esamino il risultato
			BigDecimal cdnLav = (BigDecimal) result.getAttribute("ROW.cdnLavoratore");

			// Se il lavoratore non è presente lo aggiungo, altrimenti ritorno il cdnLavoratore trovato nel record
			if (cdnLav == null) {
				lavoratoreEsiste = false;
				// deduco il CodComDomLav dal CodCPILav
				SourceBean comDomResult = findCodComDom.processRecord(record);
				if (comDomResult != null) {
					nested.add(comDomResult);
					if (ProcessorsUtils.isError(comDomResult)) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
								"Impossibile inserire il Lavoratore", warnings, nested);
					}
				}

				// Modifica 18/07/2011 : tracciati varDatori manca il codcittadinanza, e nel caso in cui la validazione
				// comporti
				// l'inserimento del lavoratore, lo setto a NT per evitare che la validazione si blocchi (sul db è un
				// campo obbligatorio)
				String codCittadinanzaLav = (String) record.get("CODCITTADINANZA");
				if (codCittadinanzaLav == null) {
					record.put("CODCITTADINANZA", "NT");
				}

				// Il lavoratore non è presente, lo inserisco; ci pensa il processor insert ad aggiungere il
				// cdnLavoratore nel record
				SourceBean insertLav = insert.processRecord(record);
				if (insertLav != null) {
					nested.add(insertLav);
					if (ProcessorsUtils.isError(insertLav)) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
								"Impossibile inserire il Lavoratore", warnings, nested);
					}
				}

				// Segnalo con una Warning che ho aggiunto il lavoratore
				warnings.add(new Warning(MessageCodes.ImportMov.AGGIUNTO_LAV, "Il lavoratore '" + cognome + " " + nome
						+ "' con codice fiscale: " + codfisc + " è stato aggiunto al DB."));

				// Inserisco nella Map una chiave che serivrà al processor (FINALE) che invia la comunicazione all'IR,
				// per capire che è avvenuto l'inserimento. La comunicazione all'IR NON può avvenire in questo punto
				// perché l'inserimento del movimento potrebbe fallire ed avvenire un roll-back sul DB. In questo caso
				// potrebbe accadere che un lavoratore sia presente sull'IR ma non sia su nessun DB locale.
				record.put("INSNUOVOLAV", "TRUE");

				// DAVIDE 31/05/2007 Se il lavoratore è nuovo inserisco le informazioni sul permesso di soggiorno
				if (record.containsKey("CODTIPODOCEX") && record.containsKey("STRNUMDOCEX")
						&& record.containsKey("CODMOTIVOPERMSOGGEX")) {
					String codTipoDoc = (String) record.get("CODTIPODOCEX");
					String strNumDoc = (String) record.get("STRNUMDOCEX");
					String codMotivoPerm = (String) record.get("CODMOTIVOPERMSOGGEX");
					if (codTipoDoc != null && !codTipoDoc.equals("") && strNumDoc != null && !strNumDoc.equals("")
							&& codMotivoPerm != null && !codMotivoPerm.equals("")) {
						// Per i valori importati in precedenza, il rinnovo era tradotto con la lettera M.
						// Trattato il rinnnovo con R
						if (codTipoDoc.equalsIgnoreCase("M")) {
							codTipoDoc = "R";
						}

						if (codTipoDoc.equalsIgnoreCase("N")) {
							codTipoDoc = "1";
							record.put("CODTIPODOCEX", codTipoDoc);
						} else {
							if (codTipoDoc.equalsIgnoreCase("S")) {
								codTipoDoc = "2";
								record.put("CODTIPODOCEX", codTipoDoc);
							} else {
								if (codTipoDoc.equalsIgnoreCase("R")) {
									codTipoDoc = "3";
									record.put("CODTIPODOCEX", codTipoDoc);
								}
							}
						}
						SourceBean infoPermSogg = insertInfoPermSogg.processRecord(record);
						if (infoPermSogg != null) {
							nested.add(infoPermSogg);
							if (ProcessorsUtils.isError(infoPermSogg)) {
								// warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA, "Impossibile
								// inserire il permesso di soggiorno"));
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
										"Impossibile inserire il permesso di soggiorno", warnings, nested);
							}
						}
					}
				} // Fine inseriemnto permesso di soggiorno

			} else {
				// inserisco il cdnLavoratore trovato nel record
				record.put("CDNLAVORATORE", cdnLav);
			}
		}

		// Controllo esistenza CPI ****************************************
		// Prendo il codcpilav del tracciato e sostituisco l'ultima cifra con 0.
		// mi servirà nel caso in cui il codcpilav del tracciato non è presente sul DB
		String codcpilav = (String) record.get("CODCPILAV");
		String statement = null;
		String statementVerifiche = null;
		SourceBean result = null;
		if (codcpilav != null && !codcpilav.equals("")) {
			String codcpilav_tracciato = codcpilav.substring(0, codcpilav.length() - 1) + "0";
			statement = "select STRDESCRIZIONE from de_cpi where codcpi = '" + codcpilav + "'";
			result = null;
			try {
				result = ProcessorsUtils.executeSelectQuery(statement, trans);
				// Se il codcpilav non è presente sul DB
				if (!result.containsAttribute("ROW.STRDESCRIZIONE")) {
					// Verifico se è presente il codice con l'ultima cifra sostituita
					statement = "select STRDESCRIZIONE from de_cpi where codcpi = '" + codcpilav_tracciato + "'";
					result = ProcessorsUtils.executeSelectQuery(statement, trans);
					// Se nemmeno questo codice è presente sul DB allora lancio l'errore
					if (!result.containsAttribute("ROW.STRDESCRIZIONE")) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
								"Centro per l'impiego associato al lavoratore non presente nel DB", warnings, nested);
					} else {
						// Altrimenti sostituisco il codcpi originale (ma non presente sul DB) con quello modificato.
						record.remove("CODCPILAV");
						record.put("CODCPILAV", codcpilav_tracciato);
					}
				}
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
						"Impossibile cercare il CPI associato al lavoratore", warnings, nested);
			}
		}
		// fine modifica

		// String codcpilav = null;
		// String statement = null;
		// SourceBean result = null;

		// Se il lavoratore esisteva già e ho un cpilav nel record controllo la corrispondenza dei CPI
		if (lavoratoreEsiste && record.containsKey("CODCPILAV")) {

			codcpilav = (String) record.get("CODCPILAV");

			// Creo la query
			statement = "SELECT CODMONOTIPOCPI, CODCPIORIG, CODCPITIT, FLG181, TO_CHAR(DATANZIANITADISOC,'DD/MM/YYYY') DATANZIANITADISOC, "
					+ "NUMANZIANITAPREC297, NUMMESISOSP, CODSTATOOCCUPAZORIG, STRNOTE, FLGSTAMPATRASF, FLGSTAMPADOC, TO_CHAR(DATDICHIARAZIONE,'DD/MM/YYYY') DATDICHIARAZIONE, STRCODICEFISCALEOLD, "
					+ "CODMONOCALCOLOANZIANITAPREC297, TO_CHAR(DATCALCOLOANZIANITA,'DD/MM/YYYY') DATCALCOLOANZIANITA, TO_CHAR(DATCALCOLOMESISOSP,'DD/MM/YYYY') DATCALCOLOMESISOSP "
					+ "FROM AN_LAV_STORIA_INF WHERE CDNLAVORATORE=" + record.get("CDNLAVORATORE")
					+ " AND DATFINE IS NULL";
			result = null;
			try {
				result = ProcessorsUtils.executeSelectQuery(statement, trans);
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
						"Impossibile cercare il CPI del lavoratore", warnings, nested);
			}

			// Esamino il risultato
			String codcpitit = result.containsAttribute("ROW.CODCPITIT")
					? result.getAttribute("ROW.CODCPITIT").toString()
					: "";
			String codcpiorig = result.containsAttribute("ROW.CODCPIORIG")
					? result.getAttribute("ROW.CODCPIORIG").toString()
					: "";
			String codmonotipocpi = result.containsAttribute("ROW.CODMONOTIPOCPI")
					? result.getAttribute("ROW.CODMONOTIPOCPI").toString()
					: "";
			String flg181 = result.containsAttribute("ROW.FLG181") ? result.getAttribute("ROW.FLG181").toString() : "";
			String dataAnzDisoc = result.containsAttribute("ROW.DATANZIANITADISOC")
					? result.getAttribute("ROW.DATANZIANITADISOC").toString()
					: "";
			BigDecimal numAnzPrec297 = (BigDecimal) result.getAttribute("ROW.NUMANZIANITAPREC297");
			BigDecimal numMesiSosp = (BigDecimal) result.getAttribute("ROW.NUMMESISOSP");
			String codStatoOccupazOrig = result.containsAttribute("ROW.CODSTATOOCCUPAZORIG")
					? result.getAttribute("ROW.CODSTATOOCCUPAZORIG").toString()
					: "";
			String strNote = result.containsAttribute("ROW.STRNOTE") ? result.getAttribute("ROW.STRNOTE").toString()
					: "";
			String flgStampaTrasf = result.containsAttribute("ROW.FLGSTAMPATRASF")
					? result.getAttribute("ROW.FLGSTAMPATRASF").toString()
					: "";
			String flgStampaDoc = result.containsAttribute("ROW.FLGSTAMPADOC")
					? result.getAttribute("ROW.FLGSTAMPADOC").toString()
					: "";
			String codMonoCalcAnzPrec297 = result.containsAttribute("ROW.CODMONOCALCOLOANZIANITAPREC297")
					? result.getAttribute("ROW.CODMONOCALCOLOANZIANITAPREC297").toString()
					: "";
			String datCalcAnz = result.containsAttribute("ROW.DATCALCOLOANZIANITA")
					? result.getAttribute("ROW.DATCALCOLOANZIANITA").toString()
					: "";
			String datCalcMesiSosp = result.containsAttribute("ROW.DATCALCOLOMESISOSP")
					? result.getAttribute("ROW.DATCALCOLOMESISOSP").toString()
					: "";
			String datDichiarazione = result.containsAttribute("ROW.DATDICHIARAZIONE")
					? result.getAttribute("ROW.DATDICHIARAZIONE").toString()
					: "";
			String strCodFiscOld = result.containsAttribute("ROW.STRCODICEFISCALEOLD")
					? result.getAttribute("ROW.STRCODICEFISCALEOLD").toString()
					: "";

			statementVerifiche = "SELECT CODPROVINCIA FROM DE_CPI WHERE CODCPI='" + codcpilav + "'";
			result = null;
			try {
				result = ProcessorsUtils.executeSelectQuery(statementVerifiche, trans);
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
						"Impossibile trovare la provincia corrispondente al CPI del lavoratore ", warnings, nested);
			}
			String codProvinciaMov = (String) result.getAttribute("ROW.CODPROVINCIA");

			statementVerifiche = null;
			statementVerifiche = "SELECT CODPROVINCIA FROM DE_CPI WHERE CODCPI='" + codcpiorig + "'";
			result = null;
			try {
				result = ProcessorsUtils.executeSelectQuery(statementVerifiche, trans);
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
						"Impossibile trovare la provincia corrispondente al CPI del lavoratore ", warnings, nested);
			}
			String codProvinciaCurrDB = (String) result.getAttribute("ROW.CODPROVINCIA");
			if (codmonotipocpi != null && codmonotipocpi.equalsIgnoreCase("T")
					&& !codProvinciaMov.equals(codProvinciaSil) && !codProvinciaCurrDB.equals(codProvinciaSil)
					&& !codcpiorig.equalsIgnoreCase(codcpilav)) {
				String statementInsert = "INSERT INTO AN_LAV_STORIA_INF "
						+ "(PRGLAVSTORIAINF, CDNLAVORATORE, DATINIZIO, "
						+ "CODCPITIT, CODMONOTIPOCPI, CODCOMDOM, CDNUTMODSCHEDAANAGPROF, "
						+ "DTMMODSCHEDAANAGPROF, CODMONOTIPOORIG, DATTRASFERIMENTO, CODCPIORIG, "
						+ "FLG181, DATANZIANITADISOC, NUMANZIANITAPREC297, NUMMESISOSP, CODSTATOOCCUPAZORIG, "
						+ "STRNOTE, CODMONOCALCOLOANZIANITAPREC297, DATCALCOLOANZIANITA, DATCALCOLOMESISOSP, "
						+ "CDNUTINS, DTMINS, CDNUTMOD, DTMMOD, FLGSTAMPATRASF, FLGSTAMPADOC, DATDICHIARAZIONE, STRCODICEFISCALEOLD, CODCPIORIGPREC) "
						+ "VALUES (S_AN_LAV_STORIA_INF.NEXTVAL, TO_NUMBER(" + record.get("CDNLAVORATORE") + ")"
						+ ", sysdate, " + "'" + codcpitit + "', " + "'T', '" + record.get("CODCOMDOM") + "', " + userId
						+ ", sysdate, " + "'V', sysdate, '" + codcpilav + "', " + "'" + flg181 + "', " + "to_date('"
						+ dataAnzDisoc + "', 'dd/mm/yyyy'), " + "TO_NUMBER(" + numAnzPrec297 + "), " + "TO_NUMBER("
						+ numMesiSosp + "), " + "'" + codStatoOccupazOrig + "', " + "'" + strNote + "', " + "'"
						+ codMonoCalcAnzPrec297 + "', " + "to_date('" + datCalcAnz + "', 'dd/mm/yyyy'), " + "to_date('"
						+ datCalcMesiSosp + "', 'dd/mm/yyyy'), " + userId + ", sysdate, " + userId + ", sysdate, " + "'"
						+ flgStampaTrasf + "', " + "'" + flgStampaDoc + "', " + "to_date('" + datDichiarazione
						+ "', 'dd/mm/yyyy'), " + "'" + strCodFiscOld + "', " + "'" + codcpiorig + "')";
				Object insertResult = null;
				try {
					insertResult = trans.executeQueryByStringStatement(statementInsert, null,
							TransactionQueryExecutor.INSERT);
				} catch (Exception e) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_INSERT_LAV),
							"Impossibile aggiornare le informazioni del Cpi del lavoratore. Dettagli: "
									+ e.getMessage(),
							warnings, nested);
				}
				String statementUpdate = "UPDATE AN_LAVORATORE  " + "SET CODCOMDOM= '" + record.get("CODCOMDOM") + "', "
						+ "STRINDIRIZZODOM = '" + StringUtils.formatValue4Sql((String) record.get("STRINDIRIZZODOM"))
						+ "', " + "STRCAPDOM = '" + record.get("STRCAPDOM") + "', " + "CDNUTMOD = " + userId + ", "
						+ "DTMMOD = SYSDATE, " + "NUMKLOLAVORATORE = NUMKLOLAVORATORE + 1 " + "WHERE CDNLAVORATORE="
						+ record.get("CDNLAVORATORE").toString();

				Object updateResult = null;
				try {
					updateResult = trans.executeQueryByStringStatement(statementUpdate, null,
							TransactionQueryExecutor.UPDATE);
				} catch (Exception e) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_INSERT_LAV),
							"Impossibile aggiornare le informazioni del lavoratore. Dettagli: " + e.getMessage(),
							warnings, nested);
				}

			}

			// Se ho trovato qualcosa controllo la corrispondenza
			if (codcpitit != null && codmonotipocpi != null) {
				if ((codmonotipocpi.equalsIgnoreCase("C") || codmonotipocpi.equalsIgnoreCase("T"))
						&& !codcpilav.equalsIgnoreCase(codcpitit)) {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_CODCPILAV,
							"Il centro per l'impiego del lavoratore indicato nel movimento è differente da quello memorizzato nel DB."));
				} else if (codmonotipocpi.equalsIgnoreCase("E")) {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_CODCPILAV,
							"Il centro per l'impiego del lavoratore indicato nel DB è esterno alla provincia"));
				}
			}
		}

		// Se il lavoratore non esiste devo impostare il codCpi corretto in an_lav_storia_inf
		if (!lavoratoreEsiste) {
			SourceBean updateCpiLavResult = updateCpiLav.processRecord(record);
			if (updateCpiLavResult != null) {
				nested.add(updateCpiLavResult);
				if (ProcessorsUtils.isError(updateCpiLavResult)) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
							"Impossibile aggiornare i dati del CPI del Lavoratore", warnings, nested);
				}
			}
		} else {
			SourceBean controllaCateneMovResult = controllaCateneMovimenti.processRecord(record);
			if (controllaCateneMovResult != null) {
				nested.add(controllaCateneMovResult);
				if (ProcessorsUtils.isError(controllaCateneMovResult)) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_RICERCA_LAV), "", warnings, nested);
				}
			}
		}

		/*
		 * Tolto il 28/09/2004 da Paolo Roccetti su indicazioni agenzia AERL //Gestione del titolo di studio del
		 * lavoratore String codTitoloStudio = (String) record.get("CODTIPOTITOLO"); if (codTitoloStudio != null &&
		 * !codTitoloStudio.equals("")) { boolean insertTitoloStudioLav = true; if (lavoratoreEsiste) { //Controllo se
		 * ha già un titolo di studio
		 * 
		 * //Creo la query String statement = "SELECT * FROM PR_STUDIO WHERE CDNLAVORATORE=" +
		 * record.get("CDNLAVORATORE"); SourceBean result = null; try { result =
		 * ProcessorsUtils.executeSelectQuery(statement, trans); } catch (Exception e) { return
		 * ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
		 * "Impossibile cercare il titolo di studio del lavoratore.", warnings, nested); }
		 * 
		 * //Esamino il risultato Vector v = result.getAttributeAsVector("ROW"); if (v.size() > 0) { //Ho almento un
		 * record, non registro nulla insertTitoloStudioLav = false; } }
		 * 
		 * //Cerco di aggiungere il titolo di studio if (insertTitoloStudioLav) { record.put("CODTIPOTITOLOGENERICO" ,
		 * codTitoloStudio.substring(0,2) + "0000"); SourceBean insertTitStudio =
		 * insertTitoloStudio.processRecord(record); if (insertTitStudio != null) { nested.add(insertTitStudio); if
		 * (ProcessorsUtils.isError(insertTitStudio)) { return ProcessorsUtils.createResponse(name, classname, new
		 * Integer(MessageCodes.ImportMov.ERR_INSERT_DATA), "Impossibile inserire il titolo di studio del Lavoratore",
		 * warnings, nested); } } } }
		 */

		// Se ho warning o risultati annidati li inserisco nella risposta, altrimenti non ritorno nulla.
		if ((warnings.size() > 0) || (nested.size() > 0)) {
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		} else {
			return null;
		}

	}
}