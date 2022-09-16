/*
 * Creato il 02-feb-04
 * Author: rolfini
 * 
 */
package it.eng.sil.module.presel;

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

public class GetVisEvidenzaXML extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetVisEvidenzaXML.class.getName());

	private String className = this.getClass().getName();

	public GetVisEvidenzaXML() {
	}

	public void service(SourceBean request, SourceBean response) {

		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		String prgTipoEvidenza = (String) request.getAttribute("PRGTIPOEVIDENZA");

		if (prgTipoEvidenza != null) {
			try {
				DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
				dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

				String statement = SQLStatements
						.getStatement(getConfig().getAttribute("SELECT_QUERY.statement").toString());
				sqlCommand = dataConnection.createSelectCommand(statement);

				List inputParameter = new ArrayList();

				inputParameter.add(dataConnection.createDataField("prgTipoEvidenza", Types.VARCHAR, prgTipoEvidenza));

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