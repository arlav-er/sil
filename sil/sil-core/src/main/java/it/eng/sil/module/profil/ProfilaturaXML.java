/*
 * Creato il 31-mag-04
 * Author: vuoto
 * 
 */
package it.eng.sil.module.profil;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.sil.Values;

public class ProfilaturaXML extends AbstractModule {

	private static final long serialVersionUID = -4642083808811785252L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProfilaturaXML.class.getName());

	public ProfilaturaXML() {
	}

	public void service(SourceBean request, SourceBean response) {

		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		String cdnProfiloStr = (String) request.getAttribute("CDNPROFILO");
		if (cdnProfiloStr != null) {
			BigDecimal cdnProfilo = new BigDecimal(cdnProfiloStr);
			try {
				DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
				dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

				String statement = SQLStatements.getStatement("PROFILATURA_XML");
				sqlCommand = dataConnection.createSelectCommand(statement);

				List<DataField> inputParameter = new ArrayList<DataField>();

				inputParameter.add(dataConnection.createDataField("cdnProfilo", Types.BIGINT, cdnProfilo));
				inputParameter.add(dataConnection.createDataField("cdnProfilo", Types.BIGINT, cdnProfilo));
				inputParameter.add(dataConnection.createDataField("cdnProfilo", Types.BIGINT, cdnProfilo));
				inputParameter.add(dataConnection.createDataField("cdnProfilo", Types.BIGINT, cdnProfilo));

				dataResult = sqlCommand.execute(inputParameter);
				ScrollableDataResult sdr = (ScrollableDataResult) dataResult.getDataObject();

				DataField df = sdr.getDataRow().getColumn("DOCXML");
				String xml = df.getStringValue();

				StringBuffer buf = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
				buf.append(xml);

				response.setAttribute("bufXML", buf);

			} catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "service", (Exception) ex);

			} finally {
				com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
			}

		}

	}

}