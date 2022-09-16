/*
 * Creato il 6-set-04
 * Author: vuoto
 * 
 */
package it.eng.afExt.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author vuoto
 * 
 */
public class TableDescriptor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(TableDescriptor.class.getName());

	private String nomeTab = null;
	private Connection conn = null;
	private List cols = null;

	public static void main(String[] args) {
	}

	public TableDescriptor(Connection _conn, String _nomeTab) {
		conn = _conn;
		nomeTab = _nomeTab;
	}

	public boolean loadCols() {

		boolean retVal = false;
		try {
			PreparedStatement ps = conn.prepareStatement(
					"select c.COLUMN_NAME from cols c, ts_tablog l where c.table_name = ? and c.table_name=l.strnometabella and l.flglog='S'");

			ps.setString(1, nomeTab);

			ResultSet rs = ps.executeQuery();
			cols = new ArrayList();

			while (rs.next()) {
				retVal = true;

				String nomeCol = rs.getString("COLUMN_NAME");

				cols.add(nomeCol);
			}
			ps.close();
			rs.close();

		} catch (SQLException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "TableDescriptor::loadCols:", ex);

		}

		return retVal;

	}

	public String getInsertIntoLogTabForUpdate(String parteWhere, int cdnUtente) {

		StringBuffer buf = new StringBuffer("INSERT INTO LG_" + nomeTab + "(\r\n");

		Iterator iter = cols.iterator();
		while (iter.hasNext()) {
			String nomeCol = (String) iter.next();
			buf.append(nomeCol + ",\r\n");
		}
		buf.append("PRGLOG, STRTIPOOP, CDNUTLOG, DTMMODLOG )");

		buf.append("SELECT \r\n");

		iter = cols.iterator();
		while (iter.hasNext()) {
			String nomeCol = (String) iter.next();
			buf.append(nomeCol + ",\r\n");
		}
		buf.append(" S_LG_" + nomeTab + ".NEXTVAL , 'U'," + cdnUtente + ", SYSDATE  \r\n");
		buf.append("FROM " + nomeTab + "\r\n");
		buf.append(parteWhere);

		return buf.toString();

	}

	public String getInsertIntoLogTabForDelete(String parteWhere, int cdnUtente) {

		StringBuffer buf = new StringBuffer("INSERT INTO LG_" + nomeTab + "(\r\n");

		Iterator iter = cols.iterator();
		while (iter.hasNext()) {
			String nomeCol = (String) iter.next();
			buf.append(nomeCol + ",\r\n");
		}
		buf.append("PRGLOG, STRTIPOOP, CDNUTLOG, DTMMODLOG )");

		buf.append("SELECT \r\n");

		iter = cols.iterator();
		while (iter.hasNext()) {
			String nomeCol = (String) iter.next();
			buf.append(nomeCol + ",\r\n");
		}
		buf.append(" S_LG_" + nomeTab + ".NEXTVAL , 'D'," + cdnUtente + ", SYSDATE  \r\n");
		buf.append("FROM " + nomeTab + "\r\n");
		buf.append(parteWhere);

		return buf.toString();

	}

}