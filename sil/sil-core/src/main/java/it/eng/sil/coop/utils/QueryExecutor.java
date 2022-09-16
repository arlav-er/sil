/*
 * Created on Aug 7, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;

/**
 * @author savino
 *
 */
public class QueryExecutor {
	private Connection connection;

	private OracleSQLMapper sqlMapper = new OracleSQLMapper();
	private String[] rsColNames;
	private int[] rsColTypes;

	public QueryExecutor(Connection conn) {
		this.connection = conn;
	}

	/**
	 * 
	 * @param query
	 *            select statement
	 * @param params
	 *            array di parametri per la PreparedStatement (solo tipo String)
	 * @return il SourceBean nel formato ROWS[.ROW]
	 * @throws Exception
	 *             in caso di un qualsiasi errore sql o di creazione del SourceBean
	 */
	public SourceBean executeQuery(String query, String[] params) throws Exception {
		Statement stm = null;
		ResultSet rs = null;
		if (params != null && params.length > 0) {
			stm = connection.prepareStatement(query);

			for (int i = 0; i < params.length; i++) {
				((PreparedStatement) stm).setString(i + 1, params[i]);
			}
			rs = ((PreparedStatement) stm).executeQuery();
		} else {
			stm = connection.createStatement();
			rs = stm.executeQuery(query);
		}
		if (rs == null)
			throw new SQLException("ResultSet null");
		// generazione SourceBean
		ResultSetMetaData rsmd = rs.getMetaData();

		// Recupero info sulle colonne
		int colNum = rsmd.getColumnCount();
		rsColNames = new String[colNum];
		rsColTypes = new int[colNum];
		for (int i = 0; i < colNum; i++) {
			rsColNames[i] = rsmd.getColumnName(i + 1);
			rsColTypes[i] = rsmd.getColumnType(i + 1);
		}

		SourceBean result = new SourceBean("ROWS");
		while (rs.next())
			result.setAttribute(getDataRow(rs).getSourceBean());
		rs.close();
		// lascio la connessione aperta per successive chiamate
		return result;

	}

	public DataRow getDataRow(ResultSet rs) throws Exception {

		try {
			DataRow dataRow = new DataRow(rsColNames.length);

			for (int i = 0; i < rsColNames.length; i++) {
				dataRow.addColumn(i, createDataField(rsColNames[i], rsColTypes[i], rs.getObject(rsColNames[i])));
			}
			return dataRow;
		} catch (SQLException sqle) {
			// throw new EMFInternalError(EMFErrorSeverity.ERROR, thisClassName+".getDataRow()", sqle);
			throw sqle;
		}
	}

	private DataField createDataField(String name, int type, Object value) {

		if (value instanceof String)
			return new DataField(name, type, (String) value, sqlMapper);
		else
			return new DataField(name, type, value, sqlMapper);
	}

	public int executeUpdate(String query, String[] params) throws Exception {
		Statement stm = null;
		int res = 0;
		if (params != null && params.length > 0) {
			stm = connection.prepareStatement(query);
			for (int i = 0; i < params.length; i++) {
				((PreparedStatement) stm).setString(i + 1, params[i]);
			}
			res = ((PreparedStatement) stm).executeUpdate();
		} else {
			stm = connection.createStatement();
			res = stm.executeUpdate(query);
		}
		return res;
	}
}
