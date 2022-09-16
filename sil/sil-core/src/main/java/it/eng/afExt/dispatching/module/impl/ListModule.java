package it.eng.afExt.dispatching.module.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.ContextScooping;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.afExt.dbaccess.sql.SmartScrollableDataResult;

/**
 * Si occupa dell'esecuzione e della predisposizione della paginazione di query che restituiscano liste. La query è
 * eseguita tramite un prepared statement, bypassando la gestione del recordset del framework. Il recordset gestito è un
 * raffinamento dei ResultSet standard JDBC.
 * 
 * Per gestire la paginazione basta dichiarare, nel file modules.xml, il modulo come nell'esempio seguente:
 * 
 * <MODULE name="M_RicercaUnitaAzienda" class="it.eng.afExt.dispatching.module.impl.ListModule">
 * <CONFIG pool="SIL_DATI" rows="15" title=""> <QUERIES> <SELECT_QUERY statement="[statement]">
 * <PARAMETER scope="[scope]" type="[type]" value="[value]"/> [...] </SELECT_QUERY> </QUERIES> <COLUMNS>
 * <COLUMN name="[...]" label="[...]"/> [...] </COLUMNS> <CAPTIONS>
 * <SELECT_CAPTION image="" confirm="[true|false]" label="[label]">
 * <PARAMETER scope="[scope]" type="[type]" value="[value]"/> [...] </SELECT_CAPTION>
 *
 * <CAPTION image="[image]" confirm="[true|false]" label="[label]">
 * <PARAMETER scope="[scope]" type="[type]" value="[value]"/> [...] </CAPTION> [...] </CAPTIONS> <BUTTONS>
 * <INSERT_BUTTON name="inserisci" label="Inserisci una nuova unità" >
 * <PARAMETER scope="[scope]" type="[type]" value="[value]"/> [...] </INSERT_BUTTON> </BUTTONS> [...] </CONFIG>
 * </MODULE>
 * 
 * @author Alessio Rolfini & Franco Vuoto
 * @see it.eng.afExt.dbaccess.sql.SmartScrollableDataResult
 * @see it.eng.afExt.dispatching.module.impl.DynamicStatementListModule
 */

public class ListModule extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ListModule.class.getName());

	public void service(SourceBean request, SourceBean response) {

		int rowsNumber = 0;
		int totalPages = 0;
		DataConnection dc = null;
		DataConnectionManager dcm = null;
		String className = this.getClass().getName();
		SmartScrollableDataResult smartDataResult = null;

		int pagedRows = Integer.parseInt((String) getConfig().getAttribute("ROWS"));

		if (pagedRows < 0) {
			pagedRows = Integer.MAX_VALUE;
		}

		String pool = (String) getConfig().getAttribute("POOL");

		// ListIFace list = new GenericList();
		Monitor monitor = null;
		try {

			SourceBean query = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");
			String token = (String) query.getAttribute("STATEMENT");

			String statement = SQLStatements.getStatement(token);

			monitor = MonitorFactory.start("ListModule:[" + token + "]");

			String strPageNumber = (String) getServiceRequest().getAttribute("LIST_PAGE");
			String strMessage = (String) getServiceRequest().getAttribute("MESSAGE");

			int pageNumber = 1;

			if (strPageNumber != null) {
				pageNumber = new Integer(strPageNumber).intValue();
			} else {
				if ((strMessage != null) && (!strMessage.equalsIgnoreCase("LIST_FIRST"))) {
					pageNumber = -1;
				}
			}

			dcm = DataConnectionManager.getInstance();
			if (dcm == null) {
				_logger.error(className + "::execute: dcm null");

			}
			dc = dcm.getConnection(pool);

			if (dc == null) {
				_logger.error(className + "::service: dc null");

			}

			Connection connection = dc.getInternalConnection();

			PreparedStatement pstmt = connection.prepareStatement(statement, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			ResponseContainer responseContainer = getResponseContainer();
			RequestContainer requestContainer = getRequestContainer();

			// List inputParameters = new ArrayList();
			Vector parameters = query.getAttributeAsVector("PARAMETER");

			for (int i = 0; i < parameters.size(); i++) {
				SourceBean parameter = (SourceBean) parameters.elementAt(i);
				String parameterType = (String) parameter.getAttribute("TYPE");
				String parameterValue = (String) parameter.getAttribute("VALUE");
				String parameterScope = (String) parameter.getAttribute("SCOPE");
				String inParameterValue = null;

				if (parameterType.equalsIgnoreCase("ABSOLUTE")) {
					inParameterValue = parameterValue;
				} else {
					Object parameterValueObject = ContextScooping.getScopedParameter(requestContainer,
							responseContainer, parameterValue, parameterScope);

					if (parameterValueObject != null) {
						inParameterValue = parameterValueObject.toString();
					}
				}

				if (inParameterValue == null) {
					inParameterValue = "";
				}

				pstmt.setString(i + 1, inParameterValue);
			}

			ResultSet rs = pstmt.executeQuery();

			smartDataResult = new SmartScrollableDataResult(dc, rs, false);

			SourceBean rowsSourceBean = smartDataResult.getSourceBean(pageNumber, pagedRows);

			rowsNumber = smartDataResult.getRowsNumber();
			totalPages = smartDataResult.getTotalPages();

			response.setAttribute(rowsSourceBean);
		} catch (Exception e) {
			_logger.error(className + "::service: " + e.getMessage());

		} finally {
			try {
				if (smartDataResult != null) {
					// NB: la "close" rilascia anche la connessione.
					smartDataResult.close();
				} else {
					if (dc != null) {
						dc.close();
					}
				}

			} catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::service: " + ex.getMessage(), ex);

			} finally {
				monitor.stop();
			}
		}
	}

}