package it.eng.sil.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;

/**
 * Classe che si occupa di cryptare e decryptare il parametro passato a seconda della chiave che recupera dalla sassione
 * 
 * @author coticone
 * 
 */
public class EncryptDecryptUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EncryptDecryptUtils.class.getName());

	public static final String thisClassName = StringUtils.getClassName(EncryptDecryptUtils.class);

	public synchronized static String encrypt(String inputString) {

		_logger.debug(thisClassName + "::encrypt() CALLED...");

		String returnValue = null;
		DataConnection conn = null;
		try {

			String keyEncrypt = getEncrypterKey();

			DBAccess dbaccess = new DBAccess();
			conn = dbaccess.getConnection(Values.DB_SIL_DATI);

			StoredProcedureCommand command = null;
			DataResult dr = null;

			String sqlFunction = SQLStatements.getStatement("GET_ENCRYPT_PARAMETER");
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlFunction);
			ArrayList parameters = new ArrayList(3);
			int paramIndex = 0;

			// Parametro di Ritorno
			parameters.add(conn.createDataField("risultato", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// 2.
			parameters.add(conn.createDataField("inputString", java.sql.Types.VARCHAR, inputString));
			command.setAsInputParameters(paramIndex++);
			// 3.
			parameters.add(conn.createDataField("key", java.sql.Types.VARCHAR, keyEncrypt));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			// Reperisco il valore di output della stored
			DataField df = pdr.getPunctualDatafield();
			returnValue = df.getStringValue();

		} catch (EMFInternalError xe) {
			LogUtils.logError("EncryptDecryptUtils.encrypt", "errore durante la cryptazione del parametro", xe);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(conn, null, null);
		}

		return returnValue;
	}

	public synchronized static String decrypt(String inputString) {

		_logger.debug(thisClassName + "::dencrypt() CALLED...");

		String returnValue = null;
		DataConnection conn = null;
		try {

			String keyEncrypt = getEncrypterKey();

			DBAccess dbaccess = new DBAccess();
			conn = dbaccess.getConnection(Values.DB_SIL_DATI);

			StoredProcedureCommand command = null;
			DataResult dr = null;

			String sqlFunction = SQLStatements.getStatement("GET_DECRYPT_PARAMETER");
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlFunction);
			ArrayList parameters = new ArrayList(3);
			int paramIndex = 0;

			// Parametro di Ritorno
			parameters.add(conn.createDataField("risultato", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// 1.
			parameters.add(conn.createDataField("inputString", java.sql.Types.VARCHAR, inputString));
			command.setAsInputParameters(paramIndex++);
			// 2.
			parameters.add(conn.createDataField("key", java.sql.Types.VARCHAR, keyEncrypt));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			// Reperisco il valore di output della stored
			DataField df = pdr.getPunctualDatafield();
			returnValue = df.getStringValue();

		} catch (EMFInternalError xe) {
			LogUtils.logError("EncryptDecryptUtils.decrypt", "errore durante la decryptazione del parametro", xe);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(conn, null, null);
		}

		return returnValue;
	}

	/**
	 * Recupera la chiave di cifratura
	 * 
	 * @return
	 */
	private static String getEncrypterKey() {
		String keyEncrypt;
		RequestContainer r = RequestContainer.getRequestContainer();
		if (r != null) {
			SessionContainer sessionContainer = r.getSessionContainer();
			keyEncrypt = (String) sessionContainer.getAttribute("_ENCRYPTER_KEY_");
		} else {
			keyEncrypt = System.getProperty("_ENCRYPTER_KEY_");
		}
		return keyEncrypt;
	}

	private static String byteArrayToHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(text.getBytes("utf-8"), 0, text.length());
		sha1hash = md.digest();
		return byteArrayToHexString(sha1hash);
	}

}