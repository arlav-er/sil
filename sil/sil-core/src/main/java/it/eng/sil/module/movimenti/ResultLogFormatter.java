/*
 * Creato il 10-nov-04
 */
package it.eng.sil.module.movimenti;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant;
import oracle.sql.BLOB;

/**
 * Classe che consente di recuperare formattare e paginare correttamente i risultati della validazione/inserimento
 * movimenti. E' anch'esso residente in sessione
 * <p/>
 * 
 * @author roccetti
 */
public class ResultLogFormatter {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ResultLogFormatter.class.getName());

	/** indica la pagina corrente */
	private int currentPage = 1;
	/** Indica il numero di risultati da visualizzare per pagina */
	private int pageSize = 10;
	/** Progressivo del gruppo di validazioni da consultare */
	private BigDecimal prgValidazioneMassiva = null;
	private Vector vettoreRisultatiValidazione = null;

	/**
	 * Costruttore
	 */
	public ResultLogFormatter() {
	}

	/**
	 * Costruttore per la consultazione di un gruppo di risultati
	 */
	public ResultLogFormatter(BigDecimal prgValidazioneMassiva) throws LogException {
		if (prgValidazioneMassiva == null)
			throw new LogException("Progressivo della validazione massiva nullo!");
		this.prgValidazioneMassiva = prgValidazioneMassiva;
	}

	/**
	 * Metodo per il recupero di un singolo risultato già formattato secondo le specifiche di GraficaUtils
	 */
	public SourceBean getSingleResult(BigDecimal prgResult) throws LogException, SourceBeanException {
		// Recupero dati
		SourceBean singleResult = getSimpleResult(prgResult);

		// Formattazione
		SourceBean records = new SourceBean("RECORDS");
		SourceBean record = (SourceBean) singleResult.getAttribute("LOG.RECORD");
		String result = (String) record.getAttribute("RESULT");
		records.setAttribute(record);
		records.setAttribute("PROCESSED", new Integer(1));
		records.setAttribute("NUMRECORD", new Integer(1));
		records.setAttribute("CORRECTPROCESSED",
				new Integer((result.equalsIgnoreCase("OK") || (result.equalsIgnoreCase("WARNING"))) ? 1 : 0));
		records.setAttribute("ERRORPROCESSED", new Integer((result.equalsIgnoreCase("ERROR") ? 1 : 0)));
		return records;
	}

	/**
	 * Metodo per il recupero di un singolo risultato non formattato, restituisce il SourceBean di nome RESULT che
	 * contiene: il tag ROW della riga del risultato e il tag LOG del BLOB associato
	 */
	private SourceBean getSimpleResult(BigDecimal prgResult) throws LogException {
		// Generazione array argomenti
		Object[] args = new Object[1];
		args[0] = prgResult;
		// Esecuzione query
		Object queryRes = null;
		try {
			SourceBean result = new SourceBean("RESULT");
			queryRes = QueryExecutor.executeQuery("GET_SINGLE_RESULT", args, "SELECT", Values.DB_SIL_DATI);
			if (queryRes == null || !(queryRes instanceof SourceBean)) {
				throw new LogException("Impossibile recuperare il risultato numero " + prgResult);
			}
			result.setAttribute((SourceBean) queryRes);
			// Recupero dati Log
			result.setAttribute(readBLOB(prgResult));
			return result;
		} catch (Exception e) {
			// Eseguo il log e rilancio l'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile recuperare il risultato numero " + prgResult,
					(Exception) e);

			throw new LogException("Impossibile recuperare il risultato numero " + prgResult);
		}
	}

	/**
	 * Metodo per il recupero di un singolo risultato non formattato, restituisce il SourceBean di nome RESULT che
	 * contiene: il tag ROW della riga del risultato
	 */
	private SourceBean getSimpleResult(BigDecimal prgValidazioneMassiva, BigDecimal prgValidato, BigDecimal prgApp,
			int contesto) throws LogException, SourceBeanException {
		SourceBean result = null;
		SourceBean sbRisultatoValidazione = null;
		switch (contesto) {
		case MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA:
			String statement = "";
			SourceBean queryRes = null;
			boolean trovatoRis = false;
			queryRes = new SourceBean("ROWS");
			if (prgValidato != null) {
				// Recupero dati del risultato della mobilita validata
				for (int i = 0; vettoreRisultatiValidazione != null && i < vettoreRisultatiValidazione.size(); i++) {
					SourceBean sbApp = (SourceBean) vettoreRisultatiValidazione.get(i);
					BigDecimal prg = (BigDecimal) sbApp.getAttribute("PRGMOBILITAISCR");
					if (prg != null && prg.equals(prgValidato)) {
						trovatoRis = true;
						queryRes.setAttribute(sbApp);
					} else {
						if (trovatoRis) {
							break;
						}
					}
				}
			} else {
				// Recupero dati del risultato della mobilita non validata
				for (int i = 0; vettoreRisultatiValidazione != null && i < vettoreRisultatiValidazione.size(); i++) {
					SourceBean sbApp = (SourceBean) vettoreRisultatiValidazione.get(i);
					BigDecimal prg = (BigDecimal) sbApp.getAttribute("PRGMOBILITAISCRAPP");
					if (prg != null && prg.equals(prgApp)) {
						trovatoRis = true;
						queryRes.setAttribute(sbApp);
					} else {
						if (trovatoRis) {
							break;
						}
					}
				}
			}
			try {
				result = new SourceBean("RESULT");
				Vector vettRis = queryRes.getAttributeAsVector("ROW");
				if (vettRis.size() > 0) {
					SourceBean sbRows = new SourceBean("ROWS");
					SourceBean sbRow = new SourceBean("ROW");
					SourceBean sbApp = (SourceBean) vettRis.get(0);
					sbRow.setAttribute("PRGMOBILITARIS", sbApp.getAttribute("PRGMOBILITARIS").toString());
					sbRow.setAttribute("PRGMOBILITARISDETT", sbApp.getAttribute("PRGMOBILITARISDETT").toString());
					sbRow.setAttribute("VALIDATO", (BigDecimal) sbApp.getAttribute("VALIDATO"));
					if (sbApp.getAttribute("PRGMOBILITAISCR") != null) {
						sbRow.setAttribute("PRGMOBILITAISCR", sbApp.getAttribute("PRGMOBILITAISCR").toString());
					} else {
						if (sbApp.getAttribute("PRGMOBILITAISCRAPP") != null) {
							sbRow.setAttribute("PRGMOBILITAISCRAPP",
									sbApp.getAttribute("PRGMOBILITAISCRAPP").toString());
						}
					}
					sbRows.setAttribute(sbRow);
					result.setAttribute(sbRows);
				}
				sbRisultatoValidazione = readDetailResult(queryRes,
						MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA);
				result.setAttribute(sbRisultatoValidazione);
			} catch (Exception e) {
				// Eseguo il log e rilancio l'eccezione
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"Impossibile recuperare il risultato numero " + prgValidazioneMassiva, (Exception) e);

				throw new LogException("Impossibile recuperare il risultato numero " + prgValidazioneMassiva);
			}
		}
		return result;
	}

	/**
	 * Metodo per il recupero delle informazioni sul gruppo di risultati
	 */
	private SourceBean getInfoGroup(BigDecimal prgValMassiva) throws LogException {
		// Generazione array argomenti
		Object[] args = new Object[1];
		args[0] = prgValMassiva;
		// Esecuzione query
		Object queryRes;
		try {
			queryRes = QueryExecutor.executeQuery("GET_INFO_VAL_MASSIVA", args, "SELECT", Values.DB_SIL_DATI);
			if (queryRes == null || !(queryRes instanceof SourceBean)) {
				throw new LogException("Impossibile recuperare le informazioni sul gruppo di risultati");
			}
		} catch (Exception e) {
			// Eseguo il log e rilancio l'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile recuperare le informazioni" + " sul gruppo di risultati numero " + prgValMassiva,
					(Exception) e);

			throw new LogException(
					"Impossibile recuperare le informazioni sul gruppo di risultati " + "	numero " + prgValMassiva);
		}
		return (SourceBean) queryRes;
	}

	/**
	 * Metodo per il recupero delle informazioni sul gruppo di risultati
	 */
	private SourceBean getInfoGroup(BigDecimal prgValMassiva, int contesto) throws LogException {
		Object queryRes = null;
		switch (contesto) {
		case MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA:
			// Generazione array argomenti
			Object[] args = new Object[1];
			args[0] = prgValMassiva;
			// Esecuzione query
			try {
				queryRes = QueryExecutor.executeQuery("GET_INFO_VAL_MASSIVA_MOBILITA", args, "SELECT",
						Values.DB_SIL_DATI);
				if (queryRes == null || !(queryRes instanceof SourceBean)) {
					throw new LogException("Impossibile recuperare le informazioni sul gruppo di risultati");
				}
			} catch (Exception e) {
				// Eseguo il log e rilancio l'eccezione
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"Impossibile recuperare le informazioni" + " sul gruppo di risultati numero " + prgValMassiva,
						(Exception) e);

				throw new LogException("Impossibile recuperare le informazioni sul gruppo di risultati " + "	numero "
						+ prgValMassiva);
			}
		}
		return (SourceBean) queryRes;
	}

	/**
	 * Metodo per il recupero dei dati del gruppo di risultati, non recupera i BLOB collegati
	 */
	private SourceBean getAllResults(BigDecimal prgValMassiva) throws LogException {
		// Generazione array argomenti
		Object[] args = new Object[1];
		args[0] = prgValMassiva;
		// Esecuzione query
		Object queryRes;
		SourceBean result = null;
		try {
			queryRes = QueryExecutor.executeQuery("GET_DATI_VAL_MASSIVA", args, "SELECT", Values.DB_SIL_DATI);
			if (queryRes == null || !(queryRes instanceof SourceBean)) {
				throw new LogException("Impossibile recuperare il gruppo di risultati numero " + prgValMassiva);
			} else
				return (SourceBean) queryRes;

		} catch (Exception e) {
			// Eseguo il log e rilancio l'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile recuperare il gruppo di risultati numero " + prgValMassiva, (Exception) e);

			throw new LogException("Impossibile recuperare il gruppo di risultati numero " + prgValMassiva);
		}
	}

	/**
	 * Metodo per il recupero dei dati del gruppo di risultati
	 */
	private SourceBean getAllResults(BigDecimal prgValMassiva, int contesto) throws LogException {
		Object queryRes = null;
		switch (contesto) {
		case MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA:
			// Generazione array argomenti
			Object[] args = new Object[1];
			args[0] = prgValMassiva;
			// Esecuzione query
			SourceBean result = null;
			try {
				queryRes = QueryExecutor.executeQuery("GET_DATI_VAL_MASSIVA_MOBILITA", args, "SELECT",
						Values.DB_SIL_DATI);
				if (queryRes == null || !(queryRes instanceof SourceBean)) {
					throw new LogException("Impossibile recuperare il gruppo di risultati numero " + prgValMassiva);
				}
				vettoreRisultatiValidazione = ((SourceBean) queryRes).getAttributeAsVector("ROW");
			} catch (Exception e) {
				// Eseguo il log e rilancio l'eccezione
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"Impossibile recuperare il gruppo di risultati numero " + prgValMassiva, (Exception) e);

				throw new LogException("Impossibile recuperare il gruppo di risultati numero " + prgValMassiva);
			}
		}
		return (SourceBean) queryRes;
	}

	/**
	 * Metodo per il settaggio del numero di righe per pagina
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		// Ritorno alla prima pagina onde evitare situazioni assurde se il
		// numero di record per pagina cresce...
		currentPage = 1;
	}

	/**
	 * Metodo per il recupero del numero di righe per pagina
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Metodi per la navigazione tra le pagine
	 */
	public SourceBean getFirstPage() throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroup(this.prgValidazioneMassiva);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResults(this.prgValidazioneMassiva);
		// Seleziono la prima pagina
		currentPage = 1;
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageData(allResults);
		// formattazione dei risultati
		return format(globalInfo, allResults, pageData);
	}

	/**
	 * Metodi per la navigazione tra le pagine
	 */
	public SourceBean getFirstPage(int contesto) throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroup(this.prgValidazioneMassiva, contesto);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResults(this.prgValidazioneMassiva, contesto);
		SourceBean newAllResults = eliminaDuplicati(allResults);
		// Seleziono la prima pagina
		currentPage = 1;
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageData(newAllResults, contesto);
		// formattazione dei risultati
		return format(globalInfo, newAllResults, pageData, contesto);
	}

	public SourceBean getLastPage() throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroup(this.prgValidazioneMassiva);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResults(this.prgValidazioneMassiva);
		// Seleziono l'ultima pagina dei risultati
		int numRecords = allResults.getAttributeAsVector("ROW").size();
		currentPage = numRecords / pageSize + (numRecords % pageSize > 0 ? 1 : 0);
		// Estrazione dati della pagina
		// SourceBean[] pageData = getPageData(allResults);
		// 29/11/2005 - Stefy per eliminare il problema che si verifica
		// a spot sulla richiesta dell'ultima pagina mentre viene eseguita la
		// validazione massiva
		SourceBean[] pageData = null;
		try {
			pageData = getPageData(allResults);
		} catch (Exception ex) {
			if (currentPage > 1) {
				currentPage = currentPage - 1;
			} else {
				currentPage = 1;
			}
			_logger.debug("Sostituzione valore ultima pagina validazione:" + currentPage);

			pageData = getPageData(allResults);
		}
		// formattazione dei risultati
		return format(globalInfo, allResults, pageData);
	}

	public SourceBean getLastPage(int contesto) throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroup(this.prgValidazioneMassiva, contesto);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResults(this.prgValidazioneMassiva, contesto);
		SourceBean newAllResults = eliminaDuplicati(allResults);
		// Seleziono l'ultima pagina dei risultati
		int numRecords = newAllResults.getAttributeAsVector("ROW").size();
		currentPage = numRecords / pageSize + (numRecords % pageSize > 0 ? 1 : 0);
		SourceBean[] pageData = null;
		try {
			pageData = getPageData(newAllResults, contesto);
		} catch (Exception ex) {
			if (currentPage > 1) {
				currentPage = currentPage - 1;
			} else {
				currentPage = 1;
			}
			_logger.debug("Sostituzione valore ultima pagina validazione:" + currentPage);

			pageData = getPageData(newAllResults, contesto);
		}
		// formattazione dei risultati
		return format(globalInfo, newAllResults, pageData, contesto);
	}

	public SourceBean getPreviousPage() throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroup(this.prgValidazioneMassiva);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResults(this.prgValidazioneMassiva);
		// Seleziono la pagina precedente
		if (currentPage > 1)
			currentPage = currentPage - 1;
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageData(allResults);
		// formattazione dei risultati
		return format(globalInfo, allResults, pageData);
	}

	public SourceBean getPreviousPage(int contesto) throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroup(this.prgValidazioneMassiva, contesto);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResults(this.prgValidazioneMassiva, contesto);
		SourceBean newAllResults = eliminaDuplicati(allResults);
		// Seleziono la pagina precedente
		if (currentPage > 1)
			currentPage = currentPage - 1;
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageData(newAllResults, contesto);
		// formattazione dei risultati
		return format(globalInfo, newAllResults, pageData, contesto);
	}

	public SourceBean getNextPage() throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroup(this.prgValidazioneMassiva);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResults(this.prgValidazioneMassiva);
		// Selezione della pagina successiva
		int numRecords = allResults.getAttributeAsVector("ROW").size();
		int lastPage = numRecords / pageSize + (numRecords % pageSize > 0 ? 1 : 0);
		if ((currentPage < lastPage)) {
			currentPage += 1;
		}
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageData(allResults);
		// formattazione dei risultati
		return format(globalInfo, allResults, pageData);
	}

	public SourceBean getNextPage(int contesto) throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroup(this.prgValidazioneMassiva, contesto);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResults(this.prgValidazioneMassiva, contesto);
		SourceBean newAllResults = eliminaDuplicati(allResults);
		// Selezione della pagina successiva
		int numRecords = newAllResults.getAttributeAsVector("ROW").size();
		int lastPage = numRecords / pageSize + (numRecords % pageSize > 0 ? 1 : 0);
		if ((currentPage < lastPage)) {
			currentPage += 1;
		}
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageData(newAllResults, contesto);
		// formattazione dei risultati
		return format(globalInfo, newAllResults, pageData, contesto);
	}

	public SourceBean getPageNumber(int i) throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroup(this.prgValidazioneMassiva);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResults(this.prgValidazioneMassiva);
		// Selezione della pagina successiva
		int numRecords = allResults.getAttributeAsVector("ROW").size();
		int lastPage = numRecords / pageSize + (numRecords % pageSize > 0 ? 1 : 0);
		// if ((currentPage < lastPage)) {currentPage +=1;}
		if ((1 <= i) && (i <= lastPage)) {
			currentPage = i;
		} else {
			currentPage = 1;
		}
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageData(allResults);
		// formattazione dei risultati
		return format(globalInfo, allResults, pageData);
	}

	public SourceBean getPageNumber(int i, int contesto) throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroup(this.prgValidazioneMassiva, contesto);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResults(this.prgValidazioneMassiva, contesto);
		SourceBean newAllResults = eliminaDuplicati(allResults);
		// Selezione della pagina successiva
		int numRecords = newAllResults.getAttributeAsVector("ROW").size();
		int lastPage = numRecords / pageSize + (numRecords % pageSize > 0 ? 1 : 0);
		if ((1 <= i) && (i <= lastPage)) {
			currentPage = i;
		} else {
			currentPage = 1;
		}
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageData(newAllResults, contesto);
		// formattazione dei risultati
		return format(globalInfo, newAllResults, pageData, contesto);
	}

	public SourceBean getSamePage() throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroup(this.prgValidazioneMassiva);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResults(this.prgValidazioneMassiva);
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageData(allResults);
		// formattazione dei risultati
		return format(globalInfo, allResults, pageData);
	}

	public SourceBean getSamePage(int contesto) throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroup(this.prgValidazioneMassiva, contesto);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResults(this.prgValidazioneMassiva, contesto);
		SourceBean newAllResults = eliminaDuplicati(allResults);
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageData(newAllResults, contesto);
		// formattazione dei risultati
		return format(globalInfo, newAllResults, pageData, contesto);
	}

	/**
	 * Metodo per la selezione dei log dei record della pagina a partire dal SourceBean di tutti i risultati
	 */
	private SourceBean[] getPageData(SourceBean result) {
		// Selezione dei record basandosi sul numero di record per pagina e sul
		// numero di pagina selezionata
		Vector allResults = result.getAttributeAsVector("ROW");
		List recordsPage = allResults.subList((currentPage - 1) * pageSize,
				(currentPage * pageSize) >= allResults.size() ? allResults.size() : currentPage * pageSize);

		// Per i soli record rimasti estrazione dei dati non formattati
		SourceBean[] logData = new SourceBean[recordsPage.size()];
		Iterator iterator = recordsPage.iterator();
		int j = 0;
		while (iterator.hasNext()) {
			BigDecimal prgRisultato = (BigDecimal) ((SourceBean) iterator.next()).getAttribute("PRGRISULTATO");
			try {
				logData[j] = getSimpleResult(prgRisultato);
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"Impossibile recuperare il log relativo al risultato numero " + prgRisultato, e);

			}
			j++;
		}
		return logData;
	}

	/**
	 * Metodo per la selezione dei log dei record della pagina a partire dal SourceBean di tutti i risultati
	 */
	private SourceBean[] getPageData(SourceBean result, int contesto) {
		SourceBean[] logData = null;
		switch (contesto) {
		case MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA:
			SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
			// Selezione dei record basandosi sul numero di record per pagina e
			// sul numero di pagina selezionata
			Vector allResults = result.getAttributeAsVector("ROW");
			List recordsPage = allResults.subList((currentPage - 1) * pageSize,
					(currentPage * pageSize) >= allResults.size() ? allResults.size() : currentPage * pageSize);
			// Per i soli record rimasti estrazione dei dati non formattati
			logData = new SourceBean[recordsPage.size()];
			int j = 0;
			BigDecimal prg = null;
			for (int i = 0; i < recordsPage.size(); i++) {
				try {
					SourceBean record = (SourceBean) recordsPage.get(i);
					logData[j] = getSimpleResult(this.prgValidazioneMassiva,
							(BigDecimal) record.getAttribute("PRGMOBILITAISCR"),
							(BigDecimal) record.getAttribute("PRGMOBILITAISCRAPP"), contesto);
					j++;
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Impossibile recuperare il log relativo al risultato numero " + prgValidazioneMassiva, e);

				}
			}
		}
		return logData;
	}

	/**
	 * Metodo di formattazione dei risultati come previsto dalla specifica di GraficaUtils
	 */
	private SourceBean format(SourceBean infoGlobali, SourceBean allResult, SourceBean[] pageLogs)
			throws SourceBeanException {

		_logger.debug(infoGlobali.toXML(false));

		_logger.debug(allResult.toXML(false));

		for (int i = 0; i < pageLogs.length; i++) {
			_logger.debug(pageLogs[i].toXML(false));

		}

		SourceBean records = new SourceBean("RECORDS");
		Vector all = allResult.getAttributeAsVector("ROW");
		int processedTotali = all.size();
		int processedOk = 0;
		int processedError = 0;

		// Impostazione dati globali del processo di validazione
		for (int i = 0; i < all.size(); i++) {
			if (((SourceBean) all.get(i)).containsAttribute("PRGMOVIMENTO")) {
				processedOk += 1;
			} else {
				processedError += 1;
			}
		}
		int numRecord = ((BigDecimal) infoGlobali.getAttribute("ROW.NUMRECORD")).toBigInteger().intValue();
		records.setAttribute("PRGVALIDAZIONEMASSIVA", prgValidazioneMassiva.toString());
		records.setAttribute("NUMRECORD", new Integer(numRecord));
		records.setAttribute("FLGSTOPUTENTE", (String) infoGlobali.getAttribute("ROW.FLGSTOPUTENTE"));
		records.setAttribute("PROCESSED", new Integer(processedTotali));
		records.setAttribute("CORRECTPROCESSED", new Integer(processedOk));
		records.setAttribute("ERRORPROCESSED", new Integer(processedError));

		// Impostazione dati generali della singola pagina
		records.setAttribute("CURRENTPAGE", new Integer(currentPage));
		records.setAttribute("NUMPAGES",
				new Integer(processedTotali / pageSize + (processedTotali % pageSize > 0 ? 1 : 0)));
		records.setAttribute("NUMERODA", new Integer(((currentPage - 1) * pageSize) + 1));
		records.setAttribute("NUMEROA",
				new Integer((currentPage * pageSize) >= processedTotali ? processedTotali : currentPage * pageSize));

		// Impostazione risultati della pagina
		for (int i = 0; i < pageLogs.length; i++) {
			// Estrazione risposta
			SourceBean record = (SourceBean) pageLogs[i].getAttribute("LOG.RECORD");
			if (record != null) {
				record.setAttribute((SourceBean) pageLogs[i].getAttribute("ROWS"));
				// Se il movimento è già stato validato successivamente tolgo il
				// recordId
				BigDecimal validato = (BigDecimal) pageLogs[i].getAttribute("ROWS.ROW.VALIDATO");
				if ((new Integer(validato.toBigInteger().intValue())).compareTo(new Integer(0)) > 0) {
					record.delAttribute("RECORDID");
				}
				// Inserisco il risultato nella pagina
				records.setAttribute(record);
			}
		}
		return records;
	}

	/**
	 * Metodo di formattazione dei risultati come previsto dalla specifica di GraficaUtils
	 */
	private SourceBean format(SourceBean infoGlobali, SourceBean allResult, SourceBean[] pageLogs, int contesto)
			throws SourceBeanException {
		_logger.debug(infoGlobali.toXML(false));

		_logger.debug(allResult.toXML(false));

		for (int i = 0; i < pageLogs.length; i++) {
			_logger.debug(pageLogs[i].toXML(false));

		}
		SourceBean records = null;
		switch (contesto) {
		case MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA:
			records = new SourceBean("RECORDS");
			Vector all = allResult.getAttributeAsVector("ROW");
			int processedTotali = all.size();
			int processedOk = 0;
			int processedError = 0;
			// Impostazione dati globali del processo di validazione
			for (int i = 0; i < all.size(); i++) {
				if (((SourceBean) all.get(i)).containsAttribute("PRGMOBILITAISCR")) {
					processedOk += 1;
				} else {
					processedError += 1;
				}
			}
			int numRecord = ((BigDecimal) infoGlobali.getAttribute("ROW.NUMRECORD")).toBigInteger().intValue();
			records.setAttribute("PRGVALIDAZIONEMASSIVA", prgValidazioneMassiva.toString());
			records.setAttribute("NUMRECORD", new Integer(numRecord));
			records.setAttribute("FLGSTOPUTENTE", (String) infoGlobali.getAttribute("ROW.FLGSTOPUTENTE"));
			records.setAttribute("PROCESSED", new Integer(processedTotali));
			records.setAttribute("CORRECTPROCESSED", new Integer(processedOk));
			records.setAttribute("ERRORPROCESSED", new Integer(processedError));

			// Impostazione dati generali della singola pagina
			records.setAttribute("CURRENTPAGE", new Integer(currentPage));
			records.setAttribute("NUMPAGES",
					new Integer(processedTotali / pageSize + (processedTotali % pageSize > 0 ? 1 : 0)));
			records.setAttribute("NUMERODA", new Integer(((currentPage - 1) * pageSize) + 1));
			records.setAttribute("NUMEROA", new Integer(
					(currentPage * pageSize) >= processedTotali ? processedTotali : currentPage * pageSize));

			// Impostazione risultati della pagina
			for (int i = 0; i < pageLogs.length; i++) {
				// Estrazione risposta
				SourceBean record = (SourceBean) pageLogs[i].getAttribute("LOG.RECORD");
				if (record != null) {
					// Se la mobilità è già stata validata tolgo il recordId
					BigDecimal validato = (BigDecimal) pageLogs[i].getAttribute("ROWS.ROW.VALIDATO");
					if ((new Integer(validato.toBigInteger().intValue())).compareTo(new Integer(0)) > 0) {
						record.delAttribute("RECORDID");
					}
					// Inserisco il risultato nella pagina
					records.setAttribute(record);
				}
			}
		}
		return records;
	}

	// INIZIO METODI VISUALIZZAZIONE RISULTATI COPIA MASSIVA PROSPETTI
	private SourceBean getInfoGroupProsp(BigDecimal prgValMassiva) throws LogException {
		// Generazione array argomenti
		Object[] args = new Object[1];
		args[0] = prgValMassiva;
		// Esecuzione query
		Object queryRes;
		try {
			queryRes = QueryExecutor.executeQuery("GET_INFO_COPIA_MASSIVA_PROSPETTI", args, "SELECT",
					Values.DB_SIL_DATI);
			if (queryRes == null || !(queryRes instanceof SourceBean)) {
				throw new LogException("Impossibile recuperare le informazioni sul gruppo di risultati");
			}
		} catch (Exception e) {
			// Eseguo il log e rilancio l'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile recuperare le informazioni" + " sul gruppo di risultati numero " + prgValMassiva,
					(Exception) e);

			throw new LogException(
					"Impossibile recuperare le informazioni sul gruppo di risultati " + "	numero " + prgValMassiva);
		}
		return (SourceBean) queryRes;
	}

	private SourceBean getAllResultsProsp(BigDecimal prgValMassiva) throws LogException {
		Object queryRes = null;
		// Generazione array argomenti
		Object[] args = new Object[1];
		args[0] = prgValMassiva;
		// Esecuzione query
		try {
			queryRes = QueryExecutor.executeQuery("GET_DATI_COPIA_MASSIVA_PROSPETTI", args, "SELECT",
					Values.DB_SIL_DATI);
			if (queryRes == null || !(queryRes instanceof SourceBean)) {
				throw new LogException("Impossibile recuperare il gruppo di risultati numero " + prgValMassiva);
			}
			vettoreRisultatiValidazione = ((SourceBean) queryRes).getAttributeAsVector("ROW");
		} catch (Exception e) {
			// Eseguo il log e rilancio l'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile recuperare il gruppo di risultati numero " + prgValMassiva, (Exception) e);

			throw new LogException("Impossibile recuperare il gruppo di risultati numero " + prgValMassiva);
		}
		return (SourceBean) queryRes;
	}

	public SourceBean createIdProsp(SourceBean dettProspetto, Object prgProspettoNew, Object prgProspetto)
			throws SourceBeanException {
		String ragSocAZ = "";
		String codFiscaleAz = "";
		String indirizzoAz = "";
		String provinciaProsp = "";
		String fascia = "";
		String obbligo68 = "";
		SourceBean sbProsp = new SourceBean("IDPROSPETTO");
		ragSocAZ = StringUtils.getAttributeStrNotNull(dettProspetto, "RAGSOCAZ");
		codFiscaleAz = StringUtils.getAttributeStrNotNull(dettProspetto, "CODFISCAZ");
		indirizzoAz = StringUtils.getAttributeStrNotNull(dettProspetto, "INDIRIZZOAZ");
		provinciaProsp = StringUtils.getAttributeStrNotNull(dettProspetto, "PROVINCIAPROSPETTO");
		fascia = StringUtils.getAttributeStrNotNull(dettProspetto, "FASCIA");
		obbligo68 = StringUtils.getAttributeStrNotNull(dettProspetto, "OBBLIGOL68");

		if (!ragSocAZ.equals("")) {
			sbProsp.setAttribute("ragSocAzienda", ragSocAZ);
		}

		if (!codFiscaleAz.equals("")) {
			sbProsp.setAttribute("strCodiceFiscaleAz", codFiscaleAz);
		}

		if (!indirizzoAz.equals("")) {
			sbProsp.setAttribute("indirizzoAz", indirizzoAz);
		}

		if (!provinciaProsp.equals("")) {
			sbProsp.setAttribute("provinciaProsp", provinciaProsp);
		}

		if (!fascia.equals("")) {
			sbProsp.setAttribute("fasciaAZ", fascia);
		}

		if (!obbligo68.equals("")) {
			sbProsp.setAttribute("obbligoL68", obbligo68);
		}

		return sbProsp;
	}

	private SourceBean readDetailResultProsp(SourceBean risultatoProsp) throws LogException, SourceBeanException {
		SourceBean sbAll = null;
		SourceBean sbRecord = null;
		SourceBean sbProsp = null;
		SourceBean sbProcessor = null;
		boolean recordError = false;
		boolean recordWarnig = false;

		sbAll = new SourceBean("LOG");
		sbRecord = new SourceBean("RECORD");
		Vector vettRis = risultatoProsp.getAttributeAsVector("ROW");
		if (vettRis.size() > 0) {
			SourceBean sbApp = (SourceBean) vettRis.get(0);
			Object prgProspetto = sbApp.getAttribute("PRGPROSPETTOINF");
			Object prgProspettoNew = sbApp.getAttribute("PRGPROSPETTONEW");

			sbProsp = createIdProsp(sbApp, prgProspettoNew, prgProspetto);

			sbRecord.setAttribute(sbProsp);

			if (prgProspetto != null) {
				sbRecord.setAttribute("RECORDID", prgProspetto.toString());
			}
			if (prgProspettoNew != null) {
				sbRecord.setAttribute("PRGPROSPETTONEW", prgProspettoNew.toString());
			}
		}
		for (int i = 0; i < vettRis.size(); i++) {
			SourceBean sbApp = (SourceBean) vettRis.get(i);

			String codiceErrore = sbApp.getAttribute("CODRISULTATO") != null
					? (String) sbApp.getAttribute("CODRISULTATO")
					: "";
			String tipoErrore = "";

			if (ProspettiConstant.mapResultError.containsKey(codiceErrore)
					|| ProspettiConstant.mapResultWarning.containsKey(codiceErrore)) {
				sbProcessor = new SourceBean("PROCESSOR");
				if (ProspettiConstant.mapResultError.containsKey(codiceErrore)) {
					tipoErrore = "ERROR";
					recordError = true;
				} else {
					tipoErrore = "WARNING";
					recordWarnig = true;
				}
				sbProcessor.setAttribute("RESULT", tipoErrore);
				SourceBean sbDetailError = new SourceBean(tipoErrore);

				sbDetailError.setAttribute("CODE", codiceErrore);
				String dettaglioErrore = sbApp.containsAttribute("STRDESCRIZIONE")
						? sbApp.getAttribute("STRDESCRIZIONE").toString()
						: "";
				sbDetailError.setAttribute("DETTAGLIO", dettaglioErrore);
				sbProcessor.setAttribute(sbDetailError);

				sbRecord.setAttribute(sbProcessor);
			}
		}
		if (recordError) {
			sbRecord.setAttribute("RESULT", "ERROR");
		} else {
			if (recordWarnig) {
				sbRecord.setAttribute("RESULT", "WARNING");
			} else {
				sbRecord.setAttribute("RESULT", "OK");
			}
		}
		sbAll.setAttribute(sbRecord);

		return sbAll;
	}

	private SourceBean getSimpleResultProsp(BigDecimal prgValidazioneMassiva, BigDecimal prgProspettoNew,
			BigDecimal prgProspetto) throws LogException, SourceBeanException {
		SourceBean result = null;
		SourceBean sbRisultatoValidazione = null;
		SourceBean queryRes = null;
		try {
			queryRes = new SourceBean("ROWS");
			for (int i = 0; vettoreRisultatiValidazione != null && i < vettoreRisultatiValidazione.size(); i++) {
				SourceBean sbApp = (SourceBean) vettoreRisultatiValidazione.get(i);
				BigDecimal prg = (BigDecimal) sbApp.getAttribute("PRGPROSPETTOINF");
				if (prg != null && prg.equals(prgProspetto)) {
					queryRes.setAttribute(sbApp);
					break;
				}
			}
			result = new SourceBean("RESULT");
			Vector vettRis = queryRes.getAttributeAsVector("ROW");
			if (vettRis.size() > 0) {
				SourceBean sbRows = new SourceBean("ROWS");
				SourceBean sbRow = new SourceBean("ROW");
				SourceBean sbApp = (SourceBean) vettRis.get(0);
				sbRow.setAttribute("PRGPICOPIECORSOANNO", sbApp.getAttribute("PRGPICOPIECORSOANNO").toString());
				sbRow.setAttribute("PRGRISULTATO", sbApp.getAttribute("PRGRISULTATO").toString());
				sbRow.setAttribute("VALIDATO", (BigDecimal) sbApp.getAttribute("VALIDATO"));
				if (prgProspetto != null) {
					sbRow.setAttribute("PRGPROSPETTOINF", prgProspetto.toString());
				}
				if (prgProspettoNew != null) {
					sbRow.setAttribute("PRGPROSPETTONEW", prgProspettoNew.toString());
				}
				sbRows.setAttribute(sbRow);
				result.setAttribute(sbRows);
			}
			sbRisultatoValidazione = readDetailResultProsp(queryRes);
			result.setAttribute(sbRisultatoValidazione);
		} catch (Exception e) {
			// Eseguo il log e rilancio l'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile recuperare il risultato numero " + prgValidazioneMassiva, (Exception) e);

			throw new LogException("Impossibile recuperare il risultato numero " + prgValidazioneMassiva);
		}

		return result;
	}

	private SourceBean[] getPageDataProsp(SourceBean result) {
		SourceBean[] logData = null;
		// Selezione dei record basandosi sul numero di record per pagina e sul numero di pagina selezionata
		Vector allResults = result.getAttributeAsVector("ROW");
		List recordsPage = allResults.subList((currentPage - 1) * pageSize,
				(currentPage * pageSize) >= allResults.size() ? allResults.size() : currentPage * pageSize);
		// Per i soli record rimasti estrazione dei dati non formattati
		logData = new SourceBean[recordsPage.size()];
		int j = 0;
		BigDecimal prg = null;
		for (int i = 0; i < recordsPage.size(); i++) {
			try {
				SourceBean record = (SourceBean) recordsPage.get(i);
				logData[j] = getSimpleResultProsp(this.prgValidazioneMassiva,
						(BigDecimal) record.getAttribute("PRGPROSPETTONEW"),
						(BigDecimal) record.getAttribute("PRGPROSPETTOINF"));
				j++;
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"Impossibile recuperare il log relativo al risultato numero " + prgValidazioneMassiva, e);

			}
		}
		return logData;
	}

	private SourceBean formatProsp(SourceBean infoGlobali, SourceBean allResult, SourceBean[] pageLogs)
			throws SourceBeanException {

		_logger.debug(infoGlobali.toXML(false));
		_logger.debug(allResult.toXML(false));

		for (int i = 0; i < pageLogs.length; i++) {
			_logger.debug(pageLogs[i].toXML(false));

		}
		SourceBean records = null;

		records = new SourceBean("RECORDS");
		Vector all = allResult.getAttributeAsVector("ROW");
		int processedTotali = all.size();
		int processedSCOk = 0;
		int processedACOk = 0;
		int processedError = 0;
		// Impostazione dati globali del processo di validazione
		for (int i = 0; i < all.size(); i++) {
			if (((SourceBean) all.get(i)).containsAttribute("PRGPROSPETTONEW")) {
				String codErrore = ((SourceBean) all.get(i)).containsAttribute("CODRISULTATO")
						? ((SourceBean) all.get(i)).getAttribute("CODRISULTATO").toString()
						: "";
				if (codErrore.equalsIgnoreCase(ProspettiConstant.COD_STORICIZZATO_COPIATO)) {
					processedSCOk += 1;
				} else {
					processedACOk += 1;
				}
			} else {
				processedError += 1;
			}
		}
		int numRecord = ((BigDecimal) infoGlobali.getAttribute("ROW.NUMRECORD")).toBigInteger().intValue();
		records.setAttribute("PRGVALIDAZIONEMASSIVA", prgValidazioneMassiva.toString());
		records.setAttribute("NUMRECORD", new Integer(numRecord));
		records.setAttribute("PROCESSED", new Integer(processedTotali));
		records.setAttribute("CORRECTPROCESSEDSC", new Integer(processedSCOk));
		records.setAttribute("CORRECTPROCESSEDAC", new Integer(processedACOk));
		records.setAttribute("ERRORPROCESSED", new Integer(processedError));

		// Impostazione dati generali della singola pagina
		records.setAttribute("CURRENTPAGE", new Integer(currentPage));
		records.setAttribute("NUMPAGES",
				new Integer(processedTotali / pageSize + (processedTotali % pageSize > 0 ? 1 : 0)));
		records.setAttribute("NUMERODA", new Integer(((currentPage - 1) * pageSize) + 1));
		records.setAttribute("NUMEROA",
				new Integer((currentPage * pageSize) >= processedTotali ? processedTotali : currentPage * pageSize));

		// Impostazione risultati della pagina
		for (int i = 0; i < pageLogs.length; i++) {
			// Estrazione risposta
			SourceBean record = (SourceBean) pageLogs[i].getAttribute("LOG.RECORD");
			if (record != null) {
				// Inserisco il risultato nella pagina
				records.setAttribute(record);
			}
		}
		return records;
	}

	public SourceBean getPreviousPageProsp() throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroupProsp(this.prgValidazioneMassiva);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResultsProsp(this.prgValidazioneMassiva);
		// Seleziono la pagina precedente
		if (currentPage > 1) {
			currentPage = currentPage - 1;
		}
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageDataProsp(allResults);
		// formattazione dei risultati
		return formatProsp(globalInfo, allResults, pageData);
	}

	public SourceBean getNextPageProsp() throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroupProsp(this.prgValidazioneMassiva);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResultsProsp(this.prgValidazioneMassiva);
		// Selezione della pagina successiva
		int numRecords = allResults.getAttributeAsVector("ROW").size();
		int lastPage = numRecords / pageSize + (numRecords % pageSize > 0 ? 1 : 0);
		if ((currentPage < lastPage)) {
			currentPage += 1;
		}
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageDataProsp(allResults);
		// formattazione dei risultati
		return formatProsp(globalInfo, allResults, pageData);
	}

	public SourceBean getSamePageProsp() throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroupProsp(this.prgValidazioneMassiva);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResultsProsp(this.prgValidazioneMassiva);
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageDataProsp(allResults);
		// formattazione dei risultati
		return formatProsp(globalInfo, allResults, pageData);
	}

	public SourceBean getLastPageProsp() throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroupProsp(this.prgValidazioneMassiva);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResultsProsp(this.prgValidazioneMassiva);
		// Seleziono l'ultima pagina dei risultati
		int numRecords = allResults.getAttributeAsVector("ROW").size();
		currentPage = numRecords / pageSize + (numRecords % pageSize > 0 ? 1 : 0);
		SourceBean[] pageData = null;
		try {
			pageData = getPageDataProsp(allResults);
		} catch (Exception ex) {
			if (currentPage > 1) {
				currentPage = currentPage - 1;
			} else {
				currentPage = 1;
			}
			_logger.debug("Sostituzione valore ultima pagina validazione:" + currentPage);

			pageData = getPageDataProsp(allResults);
		}
		// formattazione dei risultati
		return formatProsp(globalInfo, allResults, pageData);
	}

	public SourceBean getPageNumberProsp(int i) throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroupProsp(this.prgValidazioneMassiva);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResultsProsp(this.prgValidazioneMassiva);
		// Selezione della pagina successiva
		int numRecords = allResults.getAttributeAsVector("ROW").size();
		int lastPage = numRecords / pageSize + (numRecords % pageSize > 0 ? 1 : 0);
		if ((1 <= i) && (i <= lastPage)) {
			currentPage = i;
		} else {
			currentPage = 1;
		}
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageDataProsp(allResults);
		// formattazione dei risultati
		return formatProsp(globalInfo, allResults, pageData);
	}

	public SourceBean getFirstPageProsp() throws LogException, SourceBeanException {
		// Estrazione delle informazioni globali
		SourceBean globalInfo = getInfoGroupProsp(this.prgValidazioneMassiva);
		// Estrazione di tutti i risultati
		SourceBean allResults = getAllResultsProsp(this.prgValidazioneMassiva);
		currentPage = 1;
		// Estrazione dati della pagina
		SourceBean[] pageData = getPageDataProsp(allResults);
		// formattazione dei risultati
		return formatProsp(globalInfo, allResults, pageData);
	}

	// FINE METODI VISUALIZZAZIONE RISULTATI COPIA MASSIVA PROSPETTI

	/**
	 * Metodo che dato un progressivo di un risultato recupera e restituisce in formato SourceBean il campo BLOB
	 * associato (dopo aver parserizzato l'XML contenuto nel BLOB)
	 */
	private SourceBean readBLOB(BigDecimal prgRisultato) throws LogException {
		// Creazione connessione
		DataConnection conn = null;
		DataResult dr = null;
		SQLCommand selectCommand = null;
		SourceBean blob = null;
		try {
			conn = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);
			// Creazione Statement
			String stmt = SQLStatements.getStatement("READ_BLOB_RISULTATO");
			selectCommand = conn.createSelectCommand(stmt);
			ArrayList inputParameters = new ArrayList(1);
			inputParameters.add(conn.createDataField("prgRisultato", Types.BIGINT, prgRisultato));

			// Esecuzione query
			dr = selectCommand.execute(inputParameters);

			// Recupero BLOB come InputStream
			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
			DataField df = sdr.getDataRow().getColumn("BLBRISULTATO");
			BLOB resultBlob = (BLOB) df.getObjectValue();
			InputStream instream = resultBlob.getBinaryStream();

			// Creazione del SourceBean
			StringBuffer strBuffer = new StringBuffer();
			int chunk = resultBlob.getChunkSize();
			byte[] buffer = new byte[chunk];
			int length;
			while ((length = instream.read(buffer)) != -1) {
				strBuffer.append(new String(buffer, 0, length));
			}
			blob = SourceBean.fromXMLString(strBuffer.toString());
		}

		catch (Exception e) {
			// Eseguo il log e rilancio l'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile ottenere una connessione dal pool", (Exception) e);
			throw new LogException("Impossibile ottenere una connessione dal pool");
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(conn, selectCommand, dr);
		}

		// Ritorno l'eccezione o il blob
		if (blob == null) {
			throw new LogException("Impossibile recuperare il Log memorizzato");
		} else
			return blob;
	}

	private SourceBean readDetailResult(SourceBean risultatoMobilita, int contesto)
			throws LogException, SourceBeanException {
		Object queryRes = null;
		SourceBean sbAll = null;
		SourceBean sbRecord = null;
		SourceBean sbMob = null;
		SourceBean sbProcessor = null;
		boolean recordError = false;
		switch (contesto) {
		case MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA:
			sbAll = new SourceBean("LOG");
			sbRecord = new SourceBean("RECORD");
			Vector vettRis = risultatoMobilita.getAttributeAsVector("ROW");
			if (vettRis.size() > 0) {
				SourceBean sbApp = (SourceBean) vettRis.get(0);
				Object prgMobValidata = sbApp.getAttribute("PRGMOBILITAISCR");
				Object prgMobNonValidata = sbApp.getAttribute("PRGMOBILITAISCRAPP");
				sbMob = createIdMob(sbApp, prgMobValidata, prgMobNonValidata);
				sbRecord.setAttribute(sbMob);
				if (prgMobValidata != null) {
					sbRecord.setAttribute("PRGMOBILITAISCR", sbApp.getAttribute("PRGMOBILITAISCR").toString());
					sbRecord.setAttribute("RECORDID", sbApp.getAttribute("PRGMOBILITAISCR").toString());
				} else {
					sbRecord.setAttribute("PRGMOBILITAISCRAPP", sbApp.getAttribute("PRGMOBILITAISCRAPP").toString());
					sbRecord.setAttribute("RECORDID", sbApp.getAttribute("PRGMOBILITAISCRAPP").toString());
				}
			}
			for (int i = 0; i < vettRis.size(); i++) {
				SourceBean sbApp = (SourceBean) vettRis.get(i);
				sbProcessor = new SourceBean("PROCESSOR");
				String tipoErrore = sbApp.containsAttribute("TIPOERRORE") ? sbApp.getAttribute("TIPOERRORE").toString()
						: "";
				sbProcessor.setAttribute("RESULT", tipoErrore);
				SourceBean sbDetailError = new SourceBean(tipoErrore);
				sbDetailError.setAttribute("CODE", (String) sbApp.getAttribute("CODERRORE"));
				String dettaglioErrore = sbApp.containsAttribute("DESCERRORE")
						? sbApp.getAttribute("DESCERRORE").toString()
						: "";
				dettaglioErrore = dettaglioErrore
						+ (sbApp.containsAttribute("DETTAGLIOERRORE") ? sbApp.getAttribute("DETTAGLIOERRORE").toString()
								: "");
				if (dettaglioErrore.length() > 2000) {
					dettaglioErrore = dettaglioErrore.substring(0, 1997) + "...";
				}
				sbDetailError.setAttribute("DETTAGLIO", dettaglioErrore);
				sbProcessor.setAttribute(sbDetailError);
				if (tipoErrore.equalsIgnoreCase("ERROR")) {
					recordError = true;
				}
				sbRecord.setAttribute(sbProcessor);
			}
			if (recordError) {
				sbRecord.setAttribute("RESULT", "ERROR");
			} else {
				sbRecord.setAttribute("RESULT", "WARNING");
			}
			sbAll.setAttribute(sbRecord);
		}
		return sbAll;
	}

	public SourceBean eliminaDuplicati(SourceBean sbOld) throws SourceBeanException {
		SourceBean sbNew = new SourceBean("ROWS");
		Vector allResults = sbOld.getAttributeAsVector("ROW");
		Vector newAllResults = new Vector();
		Vector vettMobValidate = new Vector();
		Vector vettMobNonValidate = new Vector();
		boolean trovatoElem = false;
		for (int i = 0; i < allResults.size(); i++) {
			trovatoElem = false;
			SourceBean riga = (SourceBean) allResults.get(i);
			BigDecimal prgValidato = (BigDecimal) riga.getAttribute("PRGMOBILITAISCR");
			BigDecimal prgApp = (BigDecimal) riga.getAttribute("PRGMOBILITAISCRAPP");
			if (prgValidato != null) {
				for (int j = 0; j < vettMobValidate.size(); j++) {
					BigDecimal prgTemp = (BigDecimal) vettMobValidate.get(j);
					if (prgTemp.equals(prgValidato)) {
						trovatoElem = true;
						break;
					}
				}
				if (!trovatoElem) {
					vettMobValidate.add(prgValidato);
					sbNew.setAttribute(riga);
				}
			} else {
				if (prgApp != null) {
					for (int j = 0; j < vettMobNonValidate.size(); j++) {
						BigDecimal prgTemp = (BigDecimal) vettMobNonValidate.get(j);
						if (prgTemp.equals(prgApp)) {
							trovatoElem = true;
							break;
						}
					}
					if (!trovatoElem) {
						vettMobNonValidate.add(prgApp);
						sbNew.setAttribute(riga);
					}
				}
			}
		}
		return sbNew;
	}

	private void loadResultValidate() throws SourceBeanException {

	}

	public SourceBean createIdMob(SourceBean dettMob, Object prgMobValidato, Object prgMobNonValidato)
			throws SourceBeanException {
		String codTipoMob = StringUtils.getAttributeStrNotNull(dettMob, "ROW.CODTIPOMOB");
		String cognomeLav = "";
		String nomeLav = "";
		String ragSocAZ = "";
		String indirizzoAZ = "";
		String codFiscLav = "";
		String codCom = "";
		String codFiscaleAz = "";
		SourceBean sbMob = new SourceBean("IDMOB");

		if (prgMobValidato != null) {
			codTipoMob = StringUtils.getAttributeStrNotNull(dettMob, "CODTIPOMOBVAL");
			cognomeLav = StringUtils.getAttributeStrNotNull(dettMob, "STRCOGNOMEVAL");
			nomeLav = StringUtils.getAttributeStrNotNull(dettMob, "STRNOMEVAL");
			ragSocAZ = StringUtils.getAttributeStrNotNull(dettMob, "STRAZRAGIONESOCIALEVAL");
			indirizzoAZ = StringUtils.getAttributeStrNotNull(dettMob, "STRUAINDIRIZZOVAL");
			codFiscLav = StringUtils.getAttributeStrNotNull(dettMob, "STRCODICEFISCALEVAL");
			codFiscaleAz = StringUtils.getAttributeStrNotNull(dettMob, "STRCODICEFISCALEAZVAL");
			codCom = StringUtils.getAttributeStrNotNull(dettMob, "CODCOMUNEAZVAL");
		} else {
			codTipoMob = StringUtils.getAttributeStrNotNull(dettMob, "CODTIPOMOBNONVAL");
			cognomeLav = StringUtils.getAttributeStrNotNull(dettMob, "STRCOGNOMENONVAL");
			nomeLav = StringUtils.getAttributeStrNotNull(dettMob, "STRNOMENONVAL");
			ragSocAZ = StringUtils.getAttributeStrNotNull(dettMob, "STRAZRAGIONESOCIALENONVAL");
			indirizzoAZ = StringUtils.getAttributeStrNotNull(dettMob, "STRUAINDIRIZZONONVAL");
			codFiscLav = StringUtils.getAttributeStrNotNull(dettMob, "STRCODICEFISCALENONVAL");
			codFiscaleAz = StringUtils.getAttributeStrNotNull(dettMob, "STRCODICEFISCALEAZNONVAL");
			codCom = StringUtils.getAttributeStrNotNull(dettMob, "CODCOMUNEAZNONVAL");
		}
		if (!codTipoMob.equals("")) {
			sbMob.setAttribute("codTipoMob", codTipoMob);
		}
		if (!nomeLav.equals("")) {
			sbMob.setAttribute("nomeLav", nomeLav);
		}
		if (!cognomeLav.equals("")) {
			sbMob.setAttribute("cognomeLav", cognomeLav);
		}
		if (!ragSocAZ.equals("")) {
			sbMob.setAttribute("ragSocAzienda", ragSocAZ);
		}
		if (!indirizzoAZ.equals("")) {
			sbMob.setAttribute("indirizzoAzienda", indirizzoAZ);
		}
		if (!codFiscLav.equals("")) {
			sbMob.setAttribute("strCfLavoratore", codFiscLav);
		}
		if (!codCom.equals("")) {
			sbMob.setAttribute("codComuneAz", codCom);
		}
		if (!codFiscaleAz.equals("")) {
			sbMob.setAttribute("strCodiceFiscaleAz", codFiscaleAz);
		}
		return sbMob;
	}

	/**
	 * Restituisce il progressivo della validazione massiva corrente
	 * 
	 * @author roccetti
	 */
	public BigDecimal getPrgValidazioneMassiva() {
		return prgValidazioneMassiva;
	}
}