/*
 * Creato il 25-feb-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti;

import java.io.InputStream;
import java.sql.Types;
import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import oracle.sql.BLOB;

/**
 * @author giuliani
 * 
 *         Classe associata al modulo che si occupa di reperire i dati dell'ultima validazione di un movimento non
 *         ancora validato (e quindi ancora presente nella tabella di appoggio)
 */
public class MovGetRisultatiUltimaValidazione extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(MovGetRisultatiUltimaValidazione.class.getName());

	/**
	 * Recupera dalla tabella AM_RISULTATO_MOVIMENTO
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		String prgMovimentoApp = StringUtils.getAttributeStrNotNull(serviceRequest, "prgMovimentoApp");
		// Creazione connessione
		DataConnection conn = null;
		DataResult dr = null;
		SQLCommand selectCommand = null;
		SourceBean blob = null;
		SourceBean sb = null;
		try {
			conn = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);

			// Creazione Statement
			String stmt = SQLStatements.getStatement("READ_BLOB_RISULTATO_VALIDAZIONE");
			selectCommand = conn.createSelectCommand(stmt);
			ArrayList inputParameters = new ArrayList(1);
			inputParameters.add(conn.createDataField("prgMovimentoApp", Types.VARCHAR, prgMovimentoApp));
			inputParameters.add(conn.createDataField("prgMovimentoApp", Types.VARCHAR, prgMovimentoApp));

			// Esecuzione query
			dr = selectCommand.execute(inputParameters);

			// Recupero BLOB come InputStream
			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
			int numRows = sdr.getRowsNumber();
			if (numRows == 1) {

				DataRow dRow = sdr.getDataRow();
				DataField df = null;
				String param = "";

				df = dRow.getColumn("BLBRISULTATO");
				BLOB resultBlob = (BLOB) df.getObjectValue();
				InputStream instream = resultBlob.getBinaryStream();

				// Creazione del SourceBean dal BOLB
				StringBuffer strBuffer = new StringBuffer();
				int chunk = resultBlob.getChunkSize();
				byte[] buffer = new byte[chunk];
				int length;
				while ((length = instream.read(buffer)) != -1) {
					strBuffer.append(new String(buffer, 0, length));
				}
				blob = SourceBean.fromXMLString(strBuffer.toString());

				// Aggionta delle altre colonne della select nel SourceBean ROW
				sb = dRow.getSourceBean();

			} else if (numRows == 0) {
				blob = new SourceBean("LOG");
				sb = new SourceBean("ROW");
			} else if (numRows > 1) {
				blob = new SourceBean("LOG");
				blob.setAttribute("ERROR", "Troppi record estratti");
				_logger.debug("Sono stati estratti più di un record relativi all'ultima validazione");

			}
		} catch (Exception e) {
			// Eseguo il log e rilancio l'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile recuperare il log memorizzato", (Exception) e);
			throw new LogException("mpossibile recuperare il Log memorizzato");
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(conn, selectCommand, dr);
		}

		// Ritorno l'eccezione o il blob
		if (blob == null) {
			throw new LogException("Impossibile recuperare il Log memorizzato");
		} else {
			serviceResponse.setAttribute(blob);
			serviceResponse.setAttribute(sb);
		}

	}
}