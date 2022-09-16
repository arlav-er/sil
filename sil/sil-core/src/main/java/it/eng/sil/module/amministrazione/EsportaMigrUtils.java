package it.eng.sil.module.amministrazione;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.FileUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.Values;

/**
 * Fattorizza i metodi comuni alle classi di EsportaMigrazioni...Module. (classe astratta con metodi di classe, visibile
 * al solo pacchetto)
 * 
 * @author Luigi Antenucci
 */
abstract class EsportaMigrUtils {

	protected static final String TIMESTAMP_PATTERN_JAVA = "dd/MM/yyyy HH:mm:ss";

	/**
	 * Recupero record con i dati correnti
	 */
	public static final SourceBean recuperaDati() throws EMFUserError {

		DataConnection connection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		try {

			SourceBean row1, row2;

			String pool = Values.DB_SIL_DATI; // EX: (String)
												// getConfig().getAttribute("POOL");

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			connection = dataConnectionManager.getConnection(pool);

			// PRIMO PASSO
			try {
				String sqlStatoSelect = getStatement("ESPORTA_MIGRAZIONI_STATO_SELECT_STATO#1");

				sqlCommand = connection.createSelectCommand(sqlStatoSelect);
				dataResult = sqlCommand.execute();
				ScrollableDataResult sdr = (ScrollableDataResult) dataResult.getDataObject();
				if (!sdr.hasRows()) {
					throw new Exception("Nessuna riga recuperata!");
				}

				row1 = sdr.getDataRow().getSourceBean();
			} catch (Exception ex) {
				throw ex;
			} finally {
				Utils.releaseResources(null, sqlCommand, dataResult);
			}

			// SECONDO PASSO
			sqlCommand = null;
			dataResult = null;
			try {
				String sqlStatoSelect = getStatement("ESPORTA_MIGRAZIONI_STATO_SELECT_STATO#2");

				sqlCommand = connection.createSelectCommand(sqlStatoSelect);
				dataResult = sqlCommand.execute();
				ScrollableDataResult sdr = (ScrollableDataResult) dataResult.getDataObject();
				if (sdr.hasRows()) { // NB: se c'è prendo solo il PRIMO
										// RECORD!
					row2 = sdr.getDataRow().getSourceBean();
				} else {
					row2 = new SourceBean("row");
				}
			} catch (Exception ex) {
				throw ex;
			} finally {
				Utils.releaseResources(null, sqlCommand, dataResult);
			}

			mixAttributes(row1, row2); // row1 := row1 U row2

			return row1;
		} catch (Exception ex) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_CANT_GET_STATUS);
		} finally {
			Utils.releaseResources(connection, null, null);
		}
	}

	/**
	 * Rende la query SQL dello statement col nome passato.
	 */
	public static final String getStatement(String statementName) {

		return SQLStatements.getStatement(statementName);
	}

	/**
	 * Prende tutti gli attributi del SBread e li aggiunge a quelli di SBwrite senza eliminare quelli già esistenti.
	 * Genera eccezione se si ha un errore.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final void mixAttributes(SourceBean SBwrite, SourceBean SBread) throws SourceBeanException {
		if (SBread == null)
			return;
		Vector attribs = SBread.getContainedAttributes();
		for (int i = 0; i < attribs.size(); i++) {
			SourceBeanAttribute attrib = (SourceBeanAttribute) attribs.elementAt(i);
			String attribKey = (String) attrib.getKey();
			Object attribVal = attrib.getValue();

			SBwrite.setAttribute(attribKey, attribVal);
		}
	}

	public static final Date convertDate(String dataora) throws ParseException {

		DateFormat dateFormat = it.eng.afExt.utils.DateUtils.getSimpleDateFormatFixBugMem(TIMESTAMP_PATTERN_JAVA);
		Date data = dateFormat.parse(dataora);
		return data;
	}

	public static final String getAndCheckSaveToPath(SourceBean row) throws EMFUserError {
		// Recupero e controllo il path in cui salvare
		String savetoPath = SourceBeanUtils.getAttrStrNotNull(row, "SAVETOPATH");
		savetoPath = FileUtils.appendFileSeparator(savetoPath);
		savetoPath = FileUtils.getWithAbsolutePath(savetoPath);

		try {
			FileUtils.checkExistsPath(savetoPath); // genera eccezione se non
													// esiste

			return savetoPath;
		} catch (Exception ex) {
			Vector paramV = new Vector(1);
			paramV.add(savetoPath);
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_PATH_NOT_DEFINED,
					paramV);
		}
	}

}
