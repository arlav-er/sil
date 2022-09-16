package it.eng.afExt.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

/**
 * Classe utile per convertire i record che provengono da un ResultSet (classica classe Java SQL) in un SourceBean
 * (classe nativa del framework).
 * 
 * @author Luigi Antenucci
 */
public class ResultSet2SourceBean {

	private static final String thisClassName = StringUtils.getClassName(ResultSet2SourceBean.class);

	private DataConnection dataConnection;
	private ResultSet resultSet;

	private String[] rsColNames;
	private int[] rsColTypes;

	/**
	 * Costruttore. Crea l'oggetto e precarica la struttura del ResultSet dato. NOTA BENE: la connessione è una
	 * DataConnection (del FrameWork) mentre i dati sono in un ResultSet (di Java SQL).
	 */
	public ResultSet2SourceBean(DataConnection conn, ResultSet rs) throws SQLException {

		resultSet = rs; // Salvo riferimento a oggetto in esame
		dataConnection = conn;

		ResultSetMetaData rsmd = resultSet.getMetaData();

		// Recupero info sulle colonne
		int colNum = rsmd.getColumnCount();
		rsColNames = new String[colNum];
		rsColTypes = new int[colNum];
		for (int i = 0; i < colNum; i++) {
			rsColNames[i] = rsmd.getColumnName(i + 1);
			rsColTypes[i] = rsmd.getColumnType(i + 1);
		}
	}

	/**
	 * Rende sottoforma di DataRow la riga su cui è posizionato il cursore. Si usa il RecordSet passato al momento della
	 * costruzione dell'oggetto. NB: NON si sposterà il cursore: non esegue la "rs.next".
	 */
	public DataRow getDataRow() throws EMFInternalError {

		try {
			DataRow dataRow = new DataRow(rsColNames.length);

			for (int i = 0; i < rsColNames.length; i++) {

				dataRow.addColumn(i, dataConnection.createDataField(rsColNames[i], rsColTypes[i],
						resultSet.getObject(rsColNames[i])));
			}
			return dataRow;
		} catch (SQLException sqle) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR, thisClassName + ".getDataRow()", sqle);
		}
	}

	/**
	 * Rende sottoforma di SourceBean la riga su cui è posizionato il cursore. Si usa il RecordSet passato al momento
	 * della costruzione dell'oggetto. NB: NON si sposterà il cursore: non esegue la "rs.next".
	 */
	public SourceBean getSourceBean() throws EMFInternalError {

		return getDataRow().getSourceBean();
	}

}
