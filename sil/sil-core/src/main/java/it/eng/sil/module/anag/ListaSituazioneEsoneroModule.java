package it.eng.sil.module.anag;
/*
 * Ottiene il sommario giornaliero degli appuntamenti
 * Questa classe è la DynamicStatementListModule modificata
 * per restituire una lista di una sola pagina
 * 
 * @author: Stefania Orioli
 * 
 */

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.afExt.dbaccess.sql.SmartScrollableDataResult;
import it.eng.afExt.dispatching.module.impl.ListModule;
import it.eng.sil.util.RichEsonSituazAzienda;

public class ListaSituazioneEsoneroModule extends ListModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ListaSituazioneEsoneroModule.class.getName());

	public ListaSituazioneEsoneroModule() {
	}

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

			// SourceBean query = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");
			// String token = (String) query.getAttribute("STATEMENT");

			String statement = SQLStatements.getStatement("LISTA_SITUAZIONE_RICH_ESONERO");

			monitor = MonitorFactory.start("ListModule:[" + statement + "]");

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

			// Connection connection = dc.getInternalConnection();

			// PreparedStatement pstmt = connection.prepareStatement(statement, ResultSet.TYPE_SCROLL_INSENSITIVE,
			// ResultSet.CONCUR_READ_ONLY);

			ResponseContainer responseContainer = getResponseContainer();
			RequestContainer requestContainer = getRequestContainer();

			// List inputParameters = new ArrayList();
			// Vector parameters = query.getAttributeAsVector("PARAMETER");

			/*
			 * for (int i = 0; i < parameters.size(); i++) { SourceBean parameter = (SourceBean)
			 * parameters.elementAt(i); String parameterType = (String) parameter.getAttribute("TYPE"); String
			 * parameterValue = (String) parameter.getAttribute("VALUE"); String parameterScope = (String)
			 * parameter.getAttribute("SCOPE"); String inParameterValue = null;
			 * 
			 * if (parameterType.equalsIgnoreCase("ABSOLUTE")) { inParameterValue = parameterValue; } else { Object
			 * parameterValueObject = ContextScooping.getScopedParameter( requestContainer, responseContainer,
			 * parameterValue, parameterScope);
			 * 
			 * if (parameterValueObject != null) { inParameterValue = parameterValueObject.toString(); } }
			 * 
			 * if (inParameterValue == null) { inParameterValue = ""; }
			 * 
			 * pstmt.setString(i + 1, inParameterValue); }
			 */

			// ResultSet rs = pstmt.executeQuery();

			// smartDataResult = new SmartScrollableDataResult(dc, rs, false);

			// SourceBean rowsSourceBean = smartDataResult.getSourceBean(pageNumber, pagedRows);

			String p_prgRichEsonero = (String) request.getAttribute("PRGRICHESONERO");
			String p_dataCalcolo = (String) request.getAttribute("DATPAGAMENTO");

			SourceBean rowsSourceBean = RichEsonSituazAzienda.getListaCalcoloSituazione(p_prgRichEsonero,
					p_dataCalcolo);

			rowsNumber = 0;
			totalPages = 0;

			response.setAttribute(rowsSourceBean);
		} catch (Exception e) {
			_logger.error(className + "::service: " + e.getMessage());

		} finally {

			Utils.releaseResources(dc, null, null);

		}
	}
}