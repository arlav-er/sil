package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.message.MessageBundle;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;

/**
 * Classe di utilità per i processor.
 */
public class ProcessorsUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProcessorsUtils.class.getName());
	static final String ERROR = "ERROR";
	static final String WARNING = "WARNING";
	static final String WARNING_STOP = "WARNING_STOP";

	public ProcessorsUtils() {
	}

	/**
	 * Crea automaticamente una risposta a partire da nome, classe, parametri di un eventuale Error (code, message), un
	 * arrayList di oggetti Warning da includere come tag MESSAGE (con attributio text e code...) e un arrayList di
	 * SourceBean da includere come risposte annidate.
	 * <p>
	 * 
	 * @param processorName
	 *            nome del processore
	 * @param processorClass
	 *            classe del processore
	 * @param errorCode
	 *            Integer che contiene l'int che rappresenta il codice dell'errore (se è null si hanno solo warnings e/o
	 *            risposte annidate)
	 * @param errorMessage
	 *            Messaggio dell'errore per l'utente (se è null non viene inserito)
	 * @param warnings
	 *            ArrayList di oggetti Warning che rappresentano le warning incontrate (se è null o vuoto non vengono
	 *            inserite)
	 * @param nestedResult
	 *            ArrayList di oggetti SourceBean che rappresentano le risposte annidate da inserire (se è nullo o vuoto
	 *            non vengono inserite)
	 */
	public static SourceBean createResponse(String processorName, String processorClass, Integer errorCode,
			String errorMessage, ArrayList warnings, ArrayList nestedResults) throws SourceBeanException {
		return createResponse(processorName, processorClass, errorCode, errorMessage, warnings, nestedResults, false);
	}

	public static SourceBean createResponse(String processorName, String processorClass, Integer errorCode,
			String errorMessage, ArrayList warnings, ArrayList nestedResults, boolean stop) throws SourceBeanException

	{
		_logger.debug(processorClass + "::processRecord:" + errorMessage);

		SourceBean result = new SourceBean("PROCESSOR");
		result.setAttribute("name", processorName);
		result.setAttribute("class", processorClass);

		// Setto il risultato in base all'error o solo alle warnings
		if (errorCode != null) {
			result.setAttribute("RESULT", "ERROR");
			// Aggiungo il messaggio per l'errore
			SourceBean errormsg = new SourceBean("ERROR");
			errormsg.setAttribute("code", errorCode);
			errormsg.setAttribute("messagecode", MessageBundle.getMessage(errorCode.toString()));
			if (errorMessage != null && !errorMessage.trim().equals("")) {
				errormsg.setAttribute("dettaglio", errorMessage);
			}
			result.setAttribute(errormsg);
		} else if (stop) {
			result.setAttribute("RESULT", ProcessorsUtils.WARNING_STOP);
		} else {
			result.setAttribute("RESULT", ProcessorsUtils.WARNING);
		}

		// Aggiungo le warning come delle buste xml e non come ArrayList
		if (warnings != null && (warnings.size() > 0)) {
			getUnicoSourceBean(warnings, result, "WARNING");
		}

		// Aggiungo le risposte annidate come delle buste xml e non come
		// ArrayList
		if (nestedResults != null && (nestedResults.size() > 0)) {
			processNested(nestedResults, result);
		}

		it.eng.sil.util.TraceWrapper.debug(_logger, "processa Record",
				processorClass + "[" + processorName + "]::processRecord(): " + result.toXML(false, true));

		return result;
	}

	/**
	 * Metodo che estrae il nome del processore che ha generato la risposta, se non lo trova ritorna null
	 */
	public static String getName(SourceBean result) {
		return (String) result.getAttribute("MESSAGE.name");
	}

	/**
	 * Metodo che estrae la classe del processore che ha generato la risposta, se non lo trova ritorna null
	 */
	public static String getClass(SourceBean result) {
		return (String) result.getAttribute("MESSAGE.class");
	}

	/**
	 * Metodo che controlla se il sourceBean passato come parametro contiene un error (ritorna true) o solo warnings
	 * (ritorna false)
	 */
	public static boolean isError(SourceBean result) {

		// Recupero il risultato globale
		String strRes = StringUtils.getAttributeStrNotNull(result, "RESULT");
		if (strRes.trim().equalsIgnoreCase("ERROR")) {
			return true;
		} else
			return false;
	}

	/**
	 * Metodo che controlla se il sourceBean passato come parametro contiene un LOGERROR (ritorna true) o no (ritorna
	 * false)
	 */
	public static boolean isLogError(SourceBean result) {
		return result.containsAttribute("LOGERROR");
	}

	/**
	 * Metodo che imposta un errore di log nel SourceBean passato come parametro
	 * 
	 */
	public static void setLogError(SourceBean result, int code) throws SourceBeanException {
		SourceBean errorLog = new SourceBean("LOGERROR");
		errorLog.setAttribute("code", new Integer(code));
		result.setAttribute(errorLog);
	}

	/**
	 * Metodo che controlla se il sourceBean passato come parametro contiene almeno una warning (ritorna true), se non
	 * la trova ritorna false
	 */
	public static boolean isWarning(SourceBean result) {

		// Recupero il risultato globale
		String strRes = StringUtils.getAttributeStrNotNull(result, "RESULT");
		if (strRes.trim().equalsIgnoreCase(ProcessorsUtils.WARNING)
				|| strRes.trim().equalsIgnoreCase(ProcessorsUtils.WARNING_STOP)) {
			return true;
		} else
			return false;
	}

	/**
	 * DAVIDE Metodo che controlla se il sourceBean passato come parametro contiene una warning che arresto del processo
	 * (ritorna true), se non la trova ritorna false
	 */
	public static boolean isWarningAndSTOP(SourceBean result) {

		// Recupero il risultato globale
		String strRes = StringUtils.getAttributeStrNotNull(result, "RESULT");
		if (strRes.trim().equalsIgnoreCase(ProcessorsUtils.WARNING_STOP)) {
			return true;
		} else
			return false;
	}

	/**
	 * Metodo che estrae il codice di un eventuale errore, se non lo trova ritorna null
	 */
	public static Integer getErrorCode(SourceBean result) {
		return (Integer) result.getAttribute("ERROR.code");
	}

	/**
	 * Metodo che estrae il testo di un eventuale errore, se non lo trova ritorna null
	 */
	public static String getErrorText(SourceBean result) {
		return (String) result.getAttribute("ERROR.dettaglio");
	}

	/**
	 * Metodo che estrae l'ArrayList delle warnings, se non lo trova ritorna null
	 */
	public static ArrayList getWarnings(SourceBean result) {
		// return (ArrayList) result.getAttribute("WARNINGS.list");
		Warning w = null;
		ArrayList warnings = new ArrayList();
		Vector v = (Vector) result.getAttributeAsVector("WARNING");
		if ((v != null) && v.size() > 0) {
			for (int k = 0; k < v.size(); k++) {
				SourceBean sb = (SourceBean) v.get(k);
				String code = (String) sb.getAttribute("code");
				String dettaglio = (String) sb.getAttribute("dettaglio");
				w = new Warning(Integer.parseInt(code), dettaglio);
				warnings.add(w);
			} // for(int k=0; k<v.size(); k++)
		} // if ((v!=null) && v.size()>0)

		return warnings;
	}

	/**
	 * Metodo che estrae l'ArrayList delle risposte annidate, se non lo trova ritorna null
	 */
	public static ArrayList getNestedResults(SourceBean result) {
		return (ArrayList) result.getAttribute("NESTED.list");
	}

	/**
	 * Metodo che dato un'ArrayList di SourceBean estrae gli attributi necessari e li aggiunge al SourceBean di
	 * destinazione (dato in input)
	 */
	public static void getUnicoSourceBean(ArrayList elementi, SourceBean source, String tag) {
		Warning w = null;
		SourceBean sb = null;
		// SourceBean result = source;

		if (tag.equals("WARNING")) {
			// Si tratta di warning: le buste sono sempre le stesse
			for (int i = 0; i < elementi.size(); i++) {
				try {
					w = (Warning) elementi.get(i);
					sb = new SourceBean(tag);
					sb.setAttribute("code", Integer.toString(w.getCode()));
					sb.setAttribute("messagecode", MessageBundle.getMessage(Integer.toString(w.getCode())));
					sb.setAttribute("dettaglio", w.getMessage());
					// Aggiungo la busta Warning al SourceBean finale
					source.setAttribute(sb);
				} catch (Exception ex) {
					// In caso di errori restituisco null
					_logger.debug(
							"Errore nella costruzione del SourceBean della risposta in ProcessorsUtils.(Warnings)");

					source = null;
				}
			}
		}
	}

	/**
	 * Metodo che processa la lista delle risposte annidate, aggiungendo i tag al SouceBean risultato.
	 */
	public static void processNested(ArrayList elementi, SourceBean source) throws SourceBeanException {
		Iterator iterator = elementi.iterator();
		SourceBean wrn = null;
		SourceBean err = null;
		SourceBean alt = null;
		SourceBean cnf = null;

		int j = 0;
		int i = 0;
		while (iterator.hasNext()) {
			i++;
			// Prendo il tag processor
			SourceBean processor = (SourceBean) iterator.next();

			// Estraggo i dati generali del processore
			String result = (String) processor.getAttribute("RESULT");
			String name = (String) processor.getAttribute("name");
			String classname = (String) processor.getAttribute("class");

			// Se è un errore ne estraggo l'eventuale messaggio e il codice
			String errorText = null;
			String messageCode = null;
			if (result.equalsIgnoreCase("ERROR")) {
				/*
				 * Se incontro un errore nelle risposte annidate, setto il tag ERROR del SourceBean padre con le
				 * informazioni appena lette.
				 */
				err = (SourceBean) processor.getAttribute("ERROR");
				source.delAttribute("ERROR");
				source.setAttribute(err);
				/*
				 * source.setAttribute("code",StringUtils.getAttributeStrNotNull( processor, "ERROR.code"));
				 * source.setAttribute("messagecode",StringUtils .getAttributeStrNotNull(processor,
				 * "ERROR.messagecode")); source .setAttribute("dettaglio",StringUtils.getAttributeStrNotNull
				 * (processor, "ERROR.dettaglio"));
				 */
			} // if (result.equalsIgnoreCase("ERROR"))

			// Warning
			Vector vec = processor.getAttributeAsVector("WARNING");
			if ((vec != null) && vec.size() > 0) {
				for (j = 0; j < vec.size(); j++) {
					wrn = (SourceBean) vec.get(j);
					String cdo = (String) wrn.getAttribute("code");
					if ((cdo != null) && !cdo.equals("")) {
						// Aggiungo la busta Warning al SourceBean finale
						source.setAttribute(wrn);
					} // if((cdo!=null) && !cdo.equals(""))
				} // for(int i=0;j<vec.size();j++)
			} // if ((vec!=null) && vec.size()>0)

			// Alert
			Vector vecAl = processor.getAttributeAsVector("ALERT");
			if ((vecAl != null) && vecAl.size() > 0) {
				for (j = 0; j < vecAl.size(); j++) {
					alt = (SourceBean) vecAl.get(j);
					String txt = (String) alt.getAttribute("text");
					if ((txt != null) && !txt.equals("")) {
						// Aggiungo la busta Alert al SourceBean finale
						source.setAttribute(alt);
					} // if((txt!=null) && !txt.equals(""))
				} // for(j=0;j<vecConf.size();j++)
			} // if ((vecConf!=null) && vecConf.size()>0)

			// Confirm
			Vector vecConf = processor.getAttributeAsVector("CONFIRM");
			if ((vecConf != null) && vecConf.size() > 0) {
				for (j = 0; j < vecConf.size(); j++) {
					cnf = (SourceBean) vecConf.get(j);
					String funz = (String) cnf.getAttribute("jsfunction");
					if ((funz != null) && !funz.equals("")) {
						// Aggiungo la busta Confirm al SourceBean finale
						source.setAttribute(cnf);
					} // if((funz!=null) && !funz.equals(""))
				} // for(j=0;j<vecConf.size();j++)
			} // if ((vecConf!=null) && vecConf.size()>0)

		} // while (iterator.hasNext())
	}

	/**
	 * Metodo per aggiungere il tag ALERT al SourceBean dato in input
	 * 
	 * @param source
	 *            , SourceBean sorgente, dove viene aggiunto il tag
	 * @param text
	 *            , messaggio che deve comaparire
	 */
	public static void addAlert(SourceBean source, String text, boolean showIfErrors) throws SourceBeanException {
		SourceBean sb = new SourceBean("ALERT");
		if (text != null && !text.equals("")) {
			sb.setAttribute("text", text);
			sb.setAttribute("showiferrors", String.valueOf(showIfErrors));
			source.setAttribute(sb);
		}
	}

	/**
	 * Metodo per aggiungere il tag CONFIRM al SourceBean dato in input
	 * 
	 * @param source
	 *            , SourceBean sorgente, dove viene aggiunto il tag
	 * @param text
	 *            , messaggio che deve comaparire
	 * @param jsFunction
	 *            , funzione javaScript da associare al confirm
	 * @param param
	 *            [], parametri che accetta la funzione javaScript
	 */
	public static void addConfirm(SourceBean source, String text, String jsFunction, String[] params,
			boolean showIfErrors) throws SourceBeanException {
		SourceBean sb = new SourceBean("CONFIRM");
		if ((text != null) && !text.equals("")) {
			sb.setAttribute("text", text);
			sb.setAttribute("showiferrors", String.valueOf(showIfErrors));
			if ((jsFunction != null) && !jsFunction.equals("") && (params.length >= 0)) {
				sb.setAttribute("jsfunction", jsFunction);
				for (int j = 0; j < params.length; j++) {
					SourceBean param = new SourceBean("PARAM");
					param.setAttribute("value", params[j]);
					sb.setAttribute(param);
				} // for(int j=0; j<params.length; j++)
				source.setAttribute(sb);
			} // if ((jsFunction!=null) && !jsFunction.equals("") &&
				// (params.length>0))
		} // if (text!=null && !text.equals(""))
	}

	/**
	 * Crea un tag particolare per identificare il movimento nella validazione massiva, il tag creato ha nome IDMOV e
	 * contiene: - il codice del tipo di movimento (attributo codTipoMov) - il nome e il cognome del lavoratore
	 * interessato (attributi nomeLav e cognomeLav) - la ragione sociale dell'azienda interessata (attributo
	 * ragSocAzienda)
	 * <p/>
	 * !!!ATTENZIONE!!! Non utilizzare questo metodo da processor annidati all'interno di altri, altrimenti
	 * l'identificazione non funzionerà correttamente.
	 */
	public static SourceBean createIdMov(String codtipomov, String nomeLav, String cognomeLav, String ragSocAz,
			String indirAz, String strCFlav, String numAnnoProt) throws SourceBeanException {

		SourceBean sb = new SourceBean("IDMOV");

		if (codtipomov != null && !codtipomov.equals("")) {
			sb.setAttribute("codTipoMov", codtipomov);
		}
		if (nomeLav != null && !nomeLav.equals("")) {
			sb.setAttribute("nomeLav", nomeLav);
		}
		if (cognomeLav != null && !cognomeLav.equals("")) {
			sb.setAttribute("cognomeLav", cognomeLav);
		}
		if (ragSocAz != null && !ragSocAz.equals("")) {
			sb.setAttribute("ragSocAzienda", ragSocAz);
		}
		if (indirAz != null && !indirAz.equals("")) {
			sb.setAttribute("indirizzoAzienda", indirAz);
		}
		if (strCFlav != null && !strCFlav.equals("")) {
			sb.setAttribute("strCfLavoratore", strCFlav);
		}
		if (numAnnoProt != null && !numAnnoProt.equals("")) {
			sb.setAttribute("strNumAnnoProt", numAnnoProt);
		}
		return sb;
	}

	/**
	 * Metodo per l'esecuzione delle query di select, basta passare il testo della query e il TransactionQueryExecutor.
	 * Ritorna il sourceBean dei risultati, gestisce tutti gli errori di recupero dati riportando un opportuna Exception
	 * che può essere ritornata all'utente come "ERR_REC_DATI_DB", Inoltre effettua automaticamente il tracing nel log.
	 * E' il metodo base da utilizzare per effettuare una query di select nei processors. nella stringa dell'eccezione
	 * viene riportato il messaggio di un eventuale problema riscontrato.
	 */
	public static SourceBean executeSelectQuery(String query, TransactionQueryExecutor trans) throws Exception {
		Object result = null;
		try {
			result = trans.executeQueryByStringStatement(query, null, TransactionQueryExecutor.SELECT);
		} catch (Exception e) {
			// Traccio l'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"ProcessorsUtils::executeSelectQuery(): eccezione nella query di SELECT [" + query + "]", e);

			// la rilancio
			throw e;
		}

		// Esamino il risultato
		if (result instanceof SourceBean) {
			return (SourceBean) result;
		} else if (result instanceof Exception) {
			// Traccio l'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"ProcessorsUtils::executeSelectQuery(): eccezione nella query di SELECT [" + query + "]",
					(Exception) result);

			// La rilancio
			throw (Exception) result;
		} else {
			// Risultato inatteso, lo traccio nel DB
			_logger.debug("ProcessorsUtils::executeSelectQuery(): eccezione nella query di SELECT [" + query + "]: "
					+ result.toString());

			// Segnalo all'utente che qualcosa è andato male
			throw new Exception("Eccezione nella ricerca sul DB");
		}
	}

	public static SourceBean createIdMob(String codtipomob, String nomeLav, String cognomeLav, String ragSocAz,
			String indirAz, String strCFlav, String numAnnoProt) throws SourceBeanException {

		SourceBean sb = new SourceBean("IDMOB");

		if (codtipomob != null && !codtipomob.equals("")) {
			sb.setAttribute("codTipoMob", codtipomob);
		}
		if (nomeLav != null && !nomeLav.equals("")) {
			sb.setAttribute("nomeLav", nomeLav);
		}
		if (cognomeLav != null && !cognomeLav.equals("")) {
			sb.setAttribute("cognomeLav", cognomeLav);
		}
		if (ragSocAz != null && !ragSocAz.equals("")) {
			sb.setAttribute("ragSocAzienda", ragSocAz);
		}
		if (indirAz != null && !indirAz.equals("")) {
			sb.setAttribute("indirizzoAzienda", indirAz);
		}
		if (strCFlav != null && !strCFlav.equals("")) {
			sb.setAttribute("strCfLavoratore", strCFlav);
		}
		if (numAnnoProt != null && !numAnnoProt.equals("")) {
			sb.setAttribute("strNumAnnoProt", numAnnoProt);
		}
		return sb;
	}

	/**
	 * Effettua la query sulla tabella TS_CONFIG_LOC per verificare l'esistenza del flag <b>FORZAVAL = TRUE</b>
	 * 
	 * @return true se vi è un record false se la query non ha trovato nessun record
	 */
	public static boolean checkForzaValidazione(TransactionQueryExecutor trans) {

		String selectquery = "SELECT CODTIPOCONFIG, upper(STRVALORE) " + " FROM TS_CONFIG_LOC "
				+ " WHERE upper(CODTIPOCONFIG) = 'FORZAVAL' "
				+ " AND upper(STRVALORE) = 'TRUE' AND STRCODRIF = (SELECT CODPROVINCIASIL FROM TS_GENERALE) ";
		SourceBean result = null;
		try {
			result = executeSelectQuery(selectquery, trans);
		} catch (Exception e) {
			return false;
		}
		// Esamino il risultato
		Vector rows = result.getAttributeAsVector("ROW");
		if (rows.size() == 0) {
			return false;
		} else {
			// se vi è un record vuol dire che l'attributo FORZAVAL = true
			return true;
		}
	}

	/**
	 * Effettua la query sulla tabella TS_CONFIG_LOC per verificare l'esistenza del flag <b>FORZAMOD = TRUE</b>
	 * 
	 * @return true se vi è un record false se la query non ha trovato nessun record
	 */

	public static boolean checkForzaModifiche(TransactionQueryExecutor trans) {

		String selectquery = "SELECT CODTIPOCONFIG, upper(STRVALORE) " + " FROM TS_CONFIG_LOC "
				+ " WHERE upper(CODTIPOCONFIG) = 'FORZAMOD' "
				+ " AND upper(STRVALORE) = 'TRUE' AND STRCODRIF = (SELECT CODPROVINCIASIL FROM TS_GENERALE) ";
		SourceBean result = null;
		try {
			result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
		} catch (Exception e) {
			return false;
		}
		// Esamino il risultato
		Vector rows = result.getAttributeAsVector("ROW");
		if (rows.size() == 0) {
			return false;
		} else {
			// se vi è un record vuol dire che l'attributo FORZAMOD = true
			return true;
		}
	}

	/**
	 * Questo metodo controlla se posso inserire un movimento non collegato, se ciò non è possibile ritorna un Integer
	 * che indica il motivo, altrimenti ritorna null
	 * 
	 * @param codTipoMov
	 * @param codMonoTempo
	 * @param cdnLav
	 * @param datInizioMov
	 * @param trans
	 * @return
	 */
	public static Integer checkInserimentoScollegato(String codTipoMov, String codMonoTempo, BigDecimal cdnLav,
			String datInizioMov, TransactionQueryExecutor trans) {
		SourceBean result = null;
		// Se è una trasformazione deve essere a TI
		if (codTipoMov.equalsIgnoreCase("TRA") && !codMonoTempo.equalsIgnoreCase("I")) {
			return new Integer(MessageCodes.ImportMov.ERR_INS_SCOLL_NON_TI);
		}
		// Non può trattarsi di una proroga
		if (codTipoMov.equalsIgnoreCase("PRO")) {
			return new Integer(MessageCodes.ImportMov.ERR_INS_SCOLL_PRO);
		}

		// Controllo se nel momento di inizio del movimento c'era una DID valida
		String insScollQuery = "SELECT DISP.PRGDICHDISPONIBILITA PRGDICHDISP FROM AM_DICH_DISPONIBILITA DISP, "
				+ "AM_ELENCO_ANAGRAFICO EA WHERE DISP.PRGELENCOANAGRAFICO = EA.PRGELENCOANAGRAFICO AND EA.DATCAN IS NULL "
				+ " AND EA.CDNLAVORATORE = " + cdnLav + " AND DISP.CODSTATOATTO = 'PR' ";
		if (codTipoMov.equalsIgnoreCase("TRA")) {
			insScollQuery = insScollQuery + " AND (DISP.DATFINE IS NULL OR DISP.DATFINE >= TO_DATE('" + datInizioMov
					+ "', 'DD/MM/YYYY'))";
		} else if (codTipoMov.equalsIgnoreCase("CES")) {
			insScollQuery = insScollQuery + " AND DISP.DATFINE IS NULL AND TO_DATE('" + datInizioMov
					+ "', 'DD/MM/YYYY') >= DISP.DATDICHIARAZIONE";
		}

		result = null;
		try {
			result = executeSelectQuery(insScollQuery, trans);
		} catch (Exception e) {
			// Errore, impossibile capire se l'inserimento non collegato è
			// permesso, di base lo vieto
			return new Integer(MessageCodes.ImportMov.ERR_QUERY_INS_SCOLL);
		}

		// Esamino il risultato
		if (result.containsAttribute("ROW.PRGDICHDISP")) {
			// Ho trovato almeno una did, l'inserimento scollegato non è
			// permesso
			return new Integer(MessageCodes.ImportMov.ERR_INS_SCOLLEGATO);
		} else {
			// Permetto l'inserimento non collegato ritornando senza errori
			return null;
		}
	}

	/**
	 * Cerca l'azienda in base al codice fiscale
	 * 
	 * @param codiceFisc
	 * @param trans
	 * @return
	 * 
	 */
	public static Vector cercaTrasfAziendaPrec(String codiceFisc, TransactionQueryExecutor trans) {
		codiceFisc = codiceFisc.toUpperCase();
		Vector ris = new Vector();
		String selectquery = "SELECT prgAzienda FROM AN_AZIENDA WHERE UPPER(strCodiceFiscale) = '" + codiceFisc + "' ";
		SourceBean result = null;
		try {
			result = executeSelectQuery(selectquery, trans);
		} catch (Exception e) {
			return ris;
		}
		// Esamino il risultato
		if (result != null) {
			ris = result.getAttributeAsVector("ROW");
		}
		return ris;
	}

	/**
	 * Chiamata nella validazione manuale in caso di traferimenti d'azienda; cerca l'azienda indentificata dal codice
	 * fiscale dell'azienda precedente valorizzatto nel movimento che si sta validando
	 * 
	 * @param prgMovimentoApp
	 * @return
	 */
	public static Vector cercaTrasfAziendaPrec(Object prgMovimentoApp, String context) {
		Vector ris = new Vector();
		Object[] params = new Object[] { prgMovimentoApp };
		SourceBean result = null;
		try {
			if (context.equalsIgnoreCase("validaArchivio")) {
				result = (SourceBean) QueryExecutor.executeQuery("GET_INFO_AZIENDA_PREC_DA_MOVIMENTO_ARCHIVIO", params,
						"SELECT", Values.DB_SIL_DATI);
			} else {
				result = (SourceBean) QueryExecutor.executeQuery("GET_INFO_AZIENDA_PREC_DA_MOVIMENTO_APPOGGIO", params,
						"SELECT", Values.DB_SIL_DATI);
			}
		} catch (Exception e) {
			return ris;
		}
		// Esamino il risultato
		if (result != null) {
			String codFiscaleAzPrec = result.getAttribute("ROW.STRCODICEFISCALEAZPREC") != null
					? result.getAttribute("ROW.STRCODICEFISCALEAZPREC").toString().toUpperCase()
					: "";
			result = null;
			params = new Object[] { codFiscaleAzPrec };
			try {
				result = (SourceBean) QueryExecutor.executeQuery("GET_AZIENDA_FROM_CODICEFISCALE", params, "SELECT",
						Values.DB_SIL_DATI);
			} catch (Exception e) {
				return ris;
			}
			if (result != null) {
				ris = result.getAttributeAsVector("ROW");
			}
		}
		return ris;
	}

	/**
	 * Restituisce un vettore che ha size = 0, oppure è composto da tutte le unità aziendali relative ad una certa
	 * azienda. In coda al vettore si aggiunge comune e indirizzo unità azienda contenuti nel movimento identificato da
	 * prgMovimentoApp
	 * 
	 * @param prgMovimentoApp
	 * @return
	 */
	public static Vector cercaTrasfAziendaPrecValManuale(Object prgMovimentoApp, String context) {
		Vector ris = new Vector();
		Object[] params = new Object[] { prgMovimentoApp };
		SourceBean result = null;
		try {
			if (context.equalsIgnoreCase("validaArchivio")) {
				result = (SourceBean) QueryExecutor.executeQuery("GET_INFO_AZIENDA_PREC_DA_MOVIMENTO_ARCHIVIO", params,
						"SELECT", Values.DB_SIL_DATI);
			} else {
				result = (SourceBean) QueryExecutor.executeQuery("GET_INFO_AZIENDA_PREC_DA_MOVIMENTO_APPOGGIO", params,
						"SELECT", Values.DB_SIL_DATI);
			}
		} catch (Exception e) {
			return ris;
		}
		if (result != null) {
			String codFiscaleAzPrec = result.getAttribute("ROW.STRCODICEFISCALEAZPREC") != null
					? result.getAttribute("ROW.STRCODICEFISCALEAZPREC").toString().toUpperCase()
					: "";
			String codComAzPrec = result.getAttribute("ROW.CODCOMAZPREC") != null
					? result.getAttribute("ROW.CODCOMAZPREC").toString()
					: "";
			String indAzPrec = result.getAttribute("ROW.STRINDIRIZZOAZPREC") != null
					? result.getAttribute("ROW.STRINDIRIZZOAZPREC").toString()
					: "";
			if (!codFiscaleAzPrec.equals("")) {
				result = null;
				params = new Object[] { codFiscaleAzPrec };
				try {
					result = (SourceBean) QueryExecutor.executeQuery("GET_INFO_AZIENDA_PREC_PER_TRASFERIMENTO", params,
							"SELECT", Values.DB_SIL_DATI);
				} catch (Exception e) {
					return ris;
				}
				// Esamino il risultato
				if (result != null) {
					ris = result.getAttributeAsVector("ROW");
					ris.add(ris.size(), codComAzPrec);
					ris.add(ris.size(), indAzPrec);
				}
			}
		}
		return ris;
	}

	/**
	 * Restituisce le unità produttive di una data azienda
	 * 
	 * @param prgAzienda
	 * @param trans
	 * @return
	 */
	public static Vector getListaUnitaAzienda(BigDecimal prgAzienda, TransactionQueryExecutor trans) {
		Vector ris = new Vector();
		SourceBean result = null;
		Object[] params = new Object[] { prgAzienda };
		try {
			result = (SourceBean) trans.executeQuery("GET_LISTA_UNITA_AZIENDA", params, "SELECT");
		} catch (Exception e) {
			return ris;
		}
		// Esamino il risultato
		if (result != null) {
			ris = result.getAttributeAsVector("ROW");
		}
		return ris;
	}

	/**
	 * Restituisce il codice provincia del SIL
	 * 
	 * @param trans
	 * @return
	 */
	public static String getProvinciaSil(TransactionQueryExecutor trans) {
		String statementVerifiche = null;
		statementVerifiche = "SELECT CODPROVINCIASIL FROM TS_GENERALE";
		SourceBean result = null;
		try {

			result = ProcessorsUtils.executeSelectQuery(statementVerifiche, trans);

		} catch (Exception e) {

			return null;

		}

		return (String) result.getAttribute("ROW.CODPROVINCIASIL");
	}

	/**
	 * Cerca l'unita Produttiva o la sede legale a partire dai dati forniti.
	 * <p>
	 * 
	 * @return La lista di prgUnita (BigDecimal) che matchano con gli argomenti.
	 */
	public static Vector findUnita(BigDecimal prgAzienda, String codiceComune, TransactionQueryExecutor trans)
			throws SQLException {
		ArrayList prgList = new ArrayList();
		SourceBean result = null;

		if (prgAzienda == null || codiceComune == null) {
			return new Vector();
		}

		codiceComune = codiceComune.toUpperCase();

		// Controllo se l'unita è presente nel DB con la query
		String statementUnitaAz = "SELECT prgUnita, strIndirizzo FROM AN_UNITA_AZIENDA WHERE prgAzienda = " + prgAzienda
				+ " AND UPPER(codCom) = '" + codiceComune + "' order by prgUnita";

		try {
			result = ProcessorsUtils.executeSelectQuery(statementUnitaAz, trans);
		} catch (Exception e) {
			throw new SQLException();
		}

		return result.getAttributeAsVector("ROW");

	}

	/**
	 * Controlla se l'indirizzo dell'unita passato corrisponde a quello su DB.
	 * <p>
	 * 
	 * @return la lista dei progressivi dell'unita per i quali l'indirizzo passato coincide
	 */
	public static ArrayList checkIndirizzo(Vector prgUnitaList, String indirizzo) {

		if (prgUnitaList == null || prgUnitaList.size() == 0 || indirizzo == null) {
			return new ArrayList();
		}

		ArrayList unitaList = new ArrayList();

		for (int i = 0; i < prgUnitaList.size(); i++) {
			SourceBean s = (SourceBean) prgUnitaList.get(i);

			if (s.getAttribute("strIndirizzo").toString().equalsIgnoreCase(indirizzo)) {
				unitaList.add(s.getAttribute("prgUnita"));
			}

		}

		return unitaList;

	}

	/**
	 * vettore contenente progressivi unità azienda (size >= 1)
	 * 
	 * @param prgUnitaList
	 * @return
	 */
	public static ArrayList findPrimaUnita(Vector prgUnitaList) {
		ArrayList listProg = new ArrayList();

		SourceBean s = (SourceBean) prgUnitaList.get(0);

		listProg.add(s.getAttribute("prgUnita"));

		return listProg;

	}

	public static String getRegioneAzienda(Object prgAzienda, Object prgUnita, TransactionQueryExecutor trans)
			throws Exception {
		String codRegioneAz = "";
		String queryRegioneAzienda = "select prov.codRegione from an_unita_azienda uaz, de_comune com, de_provincia prov where "
				+ "uaz.prgazienda = " + prgAzienda + " and uaz.prgunita = " + prgUnita
				+ " and uaz.codcom = com.codcom and com.codprovincia = prov.codprovincia (+)";
		// Eseguo la query
		SourceBean result = executeSelectQuery(queryRegioneAzienda, trans);
		// Controllo risultato
		codRegioneAz = result.containsAttribute("ROW.codRegione") ? (String) result.getAttribute("ROW.codRegione") : "";
		return codRegioneAz;
	}

	@SuppressWarnings("unchecked")
	public static List<String> cercaCodVariazioneValidi(String codTipoMovimento, TransactionQueryExecutor trans)
			throws Exception {
		List<String> results = new ArrayList<String>();
		String sqlQuery = "SELECT CODVARIAZIONE FROM DE_CODICE_VARIAZIONE WHERE CODTIPOMOV = '" + codTipoMovimento
				+ "'";
		SourceBean bean = ProcessorsUtils.executeSelectQuery(sqlQuery, trans);
		if (bean != null) {
			Vector<SourceBeanAttribute> rows = bean.getAttributeAsVector("ROW");
			for (Iterator iterator = rows.iterator(); iterator.hasNext();) {
				SourceBean current = (SourceBean) iterator.next();
				String str = (String) current.getAttribute("CODVARIAZIONE");
				results.add(str);
			}
		}
		return results;

	}

	public static SourceBean cercaTipoContratto(String codTipoContratto, TransactionQueryExecutor trans)
			throws Exception {
		String sqlQuery = "SELECT DE_TIPO_CONTRATTO.* FROM DE_TIPO_CONTRATTO WHERE  DE_TIPO_CONTRATTO.CODTIPOCONTRATTO = '"
				+ codTipoContratto + "'";
		// Eseguo la query
		SourceBean result = executeSelectQuery(sqlQuery, trans);
		return result;
	}

	public static Boolean flagToBooleanConverter(String flag) {
		if (flag == null)
			return null;
		return flag.equals("S") ? Boolean.TRUE : Boolean.FALSE;
	}
}