package it.eng.sil.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspWriter;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.sil.Values;
import it.eng.sil.module.profil.ProfilXMLSalva;
import oracle.jdbc.OracleConnection;

public class Utils {

	public static String notNull(String input) {
		if (input == null) {
			return "";
		} else {
			return input;
		}
	}

	public static String notNull(Object input) {
		if (input == null) {
			return "";
		} else {
			String tmp = input.toString();
			if (tmp == null) {
				return "";
			} else {
				return tmp;
			}
		}
	}

	public static String long2string(Long inputLong) {
		if (inputLong == null) {
			return "";
		} else {
			return inputLong.toString();
		}
	}

	public final static String capitalize(String in) {
		if (in == null || in.length() <= 0)
			return in;
		String result = (in.substring(0, 1)).toUpperCase() + in.substring(1).toLowerCase();
		return result;
	}

	/**
	 * Esegue il dump dell'oggetto indicato sul JspWriter della JSP, includendo anche la classe dell'oggetto.
	 * 
	 * @param nameObj
	 *            Nome da assegnare all'oggetto nel dump
	 * @param obj
	 *            Oggetto di cui dare il dump
	 * @param out
	 *            Destinazione del dump
	 * @throws IllegalArgumentException
	 *             Se manca nameObj oppure out
	 */
	public static void dumpObject(String nameObj, Object obj, JspWriter out) throws IOException {

		if (nameObj == null)
			throw new IllegalArgumentException("Name object required");

		if (out == null)
			throw new IllegalArgumentException("JspWriter required");

		if (obj == null) {
			out.println("{" + nameObj + " is null}");
			return;
		}

		out.println("{" + nameObj + " [" + obj + "], " + obj.getClass() + "}");
	}

	/**
	 * This method checks if a String contains only numbers
	 */
	public static boolean containsOnlyNumbers(String str) {

		// It can't contain only numbers if it's null or empty...
		if (str == null || "".equals(str))
			return true;

		for (int i = 0; i < str.length(); i++) {
			// If we find a non-digit character we return false.
			if (!Character.isDigit(str.charAt(i)))
				return false;
		}

		return true;
	}

	/**
	 * Restituisce il lavore della configurazione per il tipo che viene passato come parametro. *
	 * 
	 * @param cod_tipo_config
	 * @return SourceBean così formato:<br/>
	 *         &lt;ROWS&gt;<br/>
	 *         &lt;ROW CODPRIVINCIASIL=".." NUM=".."&#047;&gt;<br/>
	 *         &lt;&#047;ROWS&gt;<br/>
	 *         eseguire result.getAttribute("ROW.NUM"); per ottenere il codice della configurazione
	 * 
	 * @see it.eng.sil.module.config.GetConfigValue
	 * @throws EMFInternalError
	 */
	public static SourceBean getConfigValue(String cod_tipo_config) throws EMFInternalError {

		String parametroConf = cod_tipo_config;
		SourceBean result = null;
		DataConnection dataConnection = null;
		DataConnectionManager dataConnectionManager = null;
		QueryExecutorObject ex = null;

		try {
			ex = new QueryExecutorObject();

			dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			ex.setDataConnection(dataConnection);
			ex.setTokenStatement("ST_GetConfig");
			ex.setType(QueryExecutorObject.SELECT);

			List<DataField> inPars = new ArrayList<DataField>();
			inPars.add(dataConnection.createDataField("COD_TIPO_CONFIG", Types.VARCHAR, parametroConf));

			ex.setInputParameters(inPars);

			result = (SourceBean) ex.exec();
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, null, null);
		}
		return result;
	}

	public static OracleConnection getOracleConnection(Connection conFromPool) throws SQLException {
	
		try {
			Class[] parms = null;
			Method method = (conFromPool.getClass()).getMethod("getUnderlyingConnection", parms);
			return (OracleConnection) method.invoke(conFromPool, parms);
	
		} catch (Exception e) {
			ProfilXMLSalva._logger.fatal(e);
			throw new RuntimeException(e);
		}
	}
}
