package it.eng.sil.module.sap.errori;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.tags.Util;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.dbaccess.sql.SmartScrollableDataResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.ProfileDataFilter;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.util.xml.XMLValidator;

public class DynamicStatementListModuleErrori extends AbstractModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2547713696081978638L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicStatementListModuleErrori.class.getName());
	protected boolean skipParamSession = false;

	@SuppressWarnings("rawtypes")
	public void service(SourceBean request, SourceBean response) {

		DataConnection dc = null;
		DataConnectionManager dcm = null;
		String className = this.getClass().getName();
		SmartScrollableDataResult smartDataResult = null;

		int pagedRows = Integer.parseInt((String) getConfig().getAttribute("ROWS"));

		if (pagedRows < 0) {
			pagedRows = Integer.MAX_VALUE;
		}

		String pool = (String) getConfig().getAttribute("POOL");

		Monitor monitor = null;

		// Connection connection = null;
		String statement = "";
		try {

			SourceBean query = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");
			String statementProviderClassName = (String) query.getAttribute("STATEMENT_PROVIDER.CLASS");

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

			// INSTANZIA LA CLASSE CHE RITORNA LE QUERY
			monitor = MonitorFactory.start("DynamicStatementListModule:[" + statementProviderClassName + "]");
			Object statementProvider = Class.forName(statementProviderClassName).newInstance();

			// String statement = "";
			if (statementProvider instanceof IDynamicStatementProvider) {
				statement = ((IDynamicStatementProvider) statementProvider).getStatement(getRequestContainer(),
						getConfig());
			} else if (statementProvider instanceof IDynamicStatementProvider2) {
				statement = ((IDynamicStatementProvider2) statementProvider).getStatement(request, response);
			}

			/*******************************************************************
			 * *********** Aggancio al filtro delle lista
			 ******************************************************************/

			SessionContainer session = RequestContainer.getRequestContainer().getSessionContainer();
			User user = (User) session.getAttribute(User.USERID);

			String page = (String) request.getAttribute("PAGE");

			ProfileDataFilter filtroLista = new ProfileDataFilter(user, page);

			_logger.debug(className + " ::Statement prima della sostituzione\r\n" + statement + "]");

			String filtroSQL = null;

			if (!skipParamSession) {
				filtroSQL = filtroLista.getSqlDiLista();
			}

			if (filtroSQL != null) {
				_logger.debug(className + " ::Filtro di lista = [" + filtroSQL + "]");

				if (statement.indexOf("order by") != -1) {
					statement = Util.replace(statement, "order by", " " + filtroSQL + " ORDER BY ");
				} else if (statement.indexOf("ORDER BY") != -1) {
					statement = Util.replace(statement, "ORDER BY", " " + filtroSQL + " ORDER BY ");
				} else {
					statement = statement + " " + filtroSQL;
				}

				_logger.debug(className + " ::Statement dopo della sostituzione\r\n:" + statement + "]");

			}
			/*******************************************************************
			 * *********** FINE Aggancio al filtro delle lista
			 ******************************************************************/

			dcm = DataConnectionManager.getInstance();
			/*
			 * if (dcm == null) { _logger.error( className + "::service: dcm null"); }
			 */
			dc = dcm.getConnection(pool);

			/*
			 * if (dc == null) { _logger.error( className + "::service: dc null"); }
			 */
			Connection connection = dc.getInternalConnection();
			Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery(statement);

			smartDataResult = new SmartScrollableDataResult(dc, rs, statement, false);
			SourceBean rowsSourceBean = smartDataResult.getSourceBean(pageNumber, pagedRows);
			if (rowsSourceBean != null) {
				Vector risultati = rowsSourceBean.getAttributeAsVector("ROW");
				Iterator iter = risultati.iterator();
				while (iter.hasNext()) {
					SourceBean current = (SourceBean) iter.next();
					String note = Utils.notNull(current.getAttribute("STRNOTE").toString());
					if (StringUtils.isFilledNoBlank(note)) {
						String descrizioneAnomalia = "";
						if (note.toLowerCase().trim().contains("<listaanomalie>")) {
							Document doc = XMLValidator.parseXmlFile(note);
							XPath xpath = XPathFactory.newInstance().newXPath();
							// recupero valori
							XPathExpression exprCodAnomalia = xpath.compile("/ListaAnomalie/Anomalia/CodiceAnomalia");
							XPathExpression exprDescrAnomalia = xpath
									.compile("/ListaAnomalie/Anomalia/DescrizioneAnomalia");
							Object anomalie = exprCodAnomalia.evaluate(doc, XPathConstants.NODESET);
							Object anomalieDescr = exprDescrAnomalia.evaluate(doc, XPathConstants.NODESET);
							NodeList anomalieList = (NodeList) anomalie;
							NodeList anomalieDescrList = (NodeList) anomalieDescr;
							for (int i = 0; i < anomalieList.getLength(); i++) {
								Node anomaliaNode = anomalieList.item(i);
								String codiceAnomalia = anomaliaNode.getFirstChild().getNodeValue();
								Node anomaliaDescrNode = anomalieDescrList.item(i);
								if (anomaliaDescrNode != null && anomaliaDescrNode.getFirstChild() != null) {
									String descrAnomaliaXml = anomaliaDescrNode.getFirstChild().getNodeValue();
									if (StringUtils.isFilledNoBlank(descrAnomaliaXml)) {
										descrizioneAnomalia += codiceAnomalia + " - " + descrAnomaliaXml + "<br/>";
									}
								}

							}
							note = descrizioneAnomalia;
						}
					}
					current.updAttribute("STRNOTE", note);
				}
			}

			response.setAttribute(rowsSourceBean);

		} catch (Exception e) {

			if (statement != null) {
				_logger.fatal(className + " ::Statement eseguito:\r\n" + statement + "]");

			}

			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::service: " + e.getMessage(), e);

		} finally {
			try {
				if (smartDataResult != null) {
					// NB: la "close" rilascia anche la connessione.
					smartDataResult.close();
				} else {
					if (dc != null)
						dc.close();
				}

			} catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::service: " + ex.getMessage(), ex);

			} finally {
				monitor.stop();
			}
		}
	}

}
