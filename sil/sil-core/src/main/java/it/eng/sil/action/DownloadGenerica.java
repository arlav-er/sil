package it.eng.sil.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.dispatching.action.AbstractAction;

import it.eng.sil.bean.Documento;
import it.eng.sil.security.User;
import oracle.sql.BLOB;

/**
 * @author savino
 * 
 */

public class DownloadGenerica extends AbstractAction {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DownloadGenerica.class.getName());
	private Object prgInfo;

	private File tempFile;

	private final String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {

		// TracerSingleton.log(className,TracerSingleton.DEBUG,className +
		// "::service()");

		// recupero eventuali parametri...
		this.prgInfo = (String) request.getAttribute("PRGINFO");
		String asAttachment = (String) request.getAttribute("asAttachment");

		// recupera sessione eed utente connesso
		// RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		BigDecimal userid = new BigDecimal(user.getCodut());

		// leggo il blob
		// creo il file

		// creazione nuovo documento

		String nomeFile = "PercorsoLavoratore.pdf";

		try {
			if (prgInfo == null)
				throw new Exception("il progressivo del record da cui caricare il blob e' null");

			readBLOB();

			Documento doc = new Documento();
			doc.setStrNomeDoc(nomeFile);
			doc.setTempFile(this.tempFile);

			if (asAttachment == null)
				asAttachment = "false";
			request.delAttribute("apri");
			request.setAttribute("apri", "true");

			response.setAttribute("theDocument", doc);
			response.setAttribute("asAttachment", asAttachment);
			response.setAttribute("operationResult", "SUCCESS");
		} catch (Exception ex) {
			response.setAttribute("operationResult", "ERROR");
			_logger.warn(className + "::service()\r\n" + ex);

		}

	}

	/**
	 * Legge il blob e crea un file temporaneo impostando la proprieta' tempFile.
	 */
	public void readBLOB() throws Exception {

		// lettura da DB
		SQLCommand selectCommand = null;
		DataResult dr = null;
		OutputStream outStream = null;
		InputStream inStream = null;
		DataConnection conn = null;

		try {
			conn = DataConnectionManager.getInstance().getConnection();

			// String stmt = SQLStatements.getStatement("DOCUMENTI_DOWNLOAD");
			String stmt = "select blbfile from ca_info_trasferimento where prginfotrasferimento = ?";
			selectCommand = conn.createSelectCommand(stmt);

			ArrayList inputParameters = new ArrayList(1);
			inputParameters.add(conn.createDataField("prgInfo", Types.BIGINT, prgInfo));

			dr = selectCommand.execute(inputParameters);

			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();

			DataField df = sdr.getDataRow().getColumn(0);
			BLOB resultBlob = (BLOB) df.getObjectValue();

			inStream = resultBlob.getBinaryStream();

			int chunk = resultBlob.getChunkSize();
			byte[] buffer = new byte[chunk];
			int length;

			this.tempFile = File.createTempFile("~rpt", ".out");

			outStream = new FileOutputStream(tempFile);

			// Fetch data
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}
		} catch (Exception e) {
			throw e; // Rilancia qualsiasi eccezione
		} finally {
			// Rilascia eventuali risorse allocate
			if (inStream != null) {
				try {
					inStream.close();
				} catch (Exception e) {
				}
			}
			if (outStream != null) {
				try {
					outStream.close();
				} catch (Exception e) {
				}
			}
			Utils.releaseResources(conn, selectCommand, dr);
		}
	}
}