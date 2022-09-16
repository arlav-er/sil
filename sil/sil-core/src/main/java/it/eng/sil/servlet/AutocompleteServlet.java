package it.eng.sil.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;

public class AutocompleteServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -219254810717661022L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AutocompleteServlet.class.getName());

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String testo = request.getParameter("term");
		String queryName = "";
		String prefissoNomeQuery = request.getParameter("prefixQueryName");
		if (StringUtils.isFilledNoBlank(prefissoNomeQuery)) {
			queryName = prefissoNomeQuery.concat("_AUTOCOMPLETE");
		}
		String category = request.getParameter("category");
		boolean isCategory = false;
		if (StringUtils.isFilledNoBlank(category)) {
			isCategory = true;
		}
		_logger.debug("autocomplete search term: " + testo);
		QueryExecutorObject qExec = null;
		DataConnection dc = null;
		try {

			qExec = getQueryExecutorObject();
			dc = qExec.getDataConnection();
			qExec.setStatement(SQLStatements.getStatement(queryName));
			qExec.setType(QueryExecutorObject.SELECT);
			List<DataField> params = new ArrayList<DataField>();
			if (prefissoNomeQuery.toUpperCase().contains("STUDIO")) {
				params.add(dc.createDataField("descrParlante", Types.VARCHAR, testo));
			}
			if (prefissoNomeQuery.toUpperCase().contains("PROFIL")) {
				String strCdnUser = request.getParameter("cdnUser");
				params.add(dc.createDataField("cdnUtente", Types.NUMERIC, new BigDecimal(strCdnUser)));
			}

			params.add(dc.createDataField("descr", Types.VARCHAR, testo));

			if (prefissoNomeQuery.toUpperCase().contains("PROFIL")) {
				params.add(dc.createDataField("descrGruppo", Types.VARCHAR, testo));
			}

			qExec.setInputParameters(params);
			SourceBean resultSet = (SourceBean) qExec.exec();

			JSONArray jsonResultArray = getItemsJSONArray(resultSet, isCategory);
			JSONObject matchingItems = new JSONObject();
			matchingItems.put("matchingItems", jsonResultArray);
			response.setContentType("application/json");
			PrintWriter printer = response.getWriter();
			printer.println(matchingItems);
			printer.close();
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("servlet autocomplete", e);
		}
	}

	public static QueryExecutorObject getQueryExecutorObject() throws NamingException, SQLException, EMFInternalError {
		InitialContext ctx = new InitialContext();
		Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);
		DataConnection dc = null;
		QueryExecutorObject qExec;
		if (objs instanceof DataSource) {
			DataSource ds = (DataSource) objs;
			Connection conn = ds.getConnection();
			dc = new DataConnection(conn, "2", new OracleSQLMapper());
			qExec = new QueryExecutorObject();

			qExec.setRequestContainer(null);
			qExec.setResponseContainer(null);
			qExec.setDataConnection(dc);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setTransactional(false);
			qExec.setDontForgetException(false);
		} else {
			_logger.error("Impossibile ottenere una connessione");
			return null;
		}
		return qExec;
	}

	private JSONArray getItemsJSONArray(SourceBean sbResultSet, boolean isGruppo) throws JSONException {
		JSONArray jsonArrayResult = new JSONArray();
		if (sbResultSet.containsAttribute("ROW")) {
			@SuppressWarnings("rawtypes")
			Vector rows = sbResultSet.getAttributeAsVector("ROW");
			for (int i = 0; i < rows.size(); i++) {
				SourceBean row = (SourceBean) rows.get(i);

				String codiceItem = (String) row.getAttribute("codice");
				String descrizione = (String) row.getAttribute("value");
				String gruppo = null;
				if (isGruppo) {
					gruppo = (String) row.getAttribute("GRUPPO");
				}

				JSONObject jo = new JSONObject();
				jo.put("id", codiceItem);
				jo.put("value", descrizione);
				if (isGruppo) {
					jo.put("category", gruppo);
				}

				jsonArrayResult.put(jo);
			}

		}
		return jsonArrayResult;
	}

}
