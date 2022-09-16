package it.eng.sil.coop.services.logger;

import java.io.Writer;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import oracle.sql.CLOB;

public class DBErrorLogger {
	public void log(Logger classicLogger, String codMonoTipo, String codComunicazione, String txtTracciatoXml,
			String strsqlcode, String strsqlerrm) throws Exception {
		Writer outStream = null;
		TransactionQueryExecutor transExec = null;

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			SourceBean row = (SourceBean) transExec.executeQuery("GET_NEXT_PRGWSERR", new Object[] {}, "SELECT");

			// Recupero prgWsErr
			BigDecimal prgWsErr = (BigDecimal) row.getAttribute("ROW.PRGWSERR");

			if (strsqlerrm == null)
				strsqlerrm = "";

			transExec
					.executeQuery("INSERT_LOG_COMUNICAZIONE",
							new Object[] { prgWsErr, codMonoTipo, codComunicazione, strsqlcode,
									(strsqlerrm.length() < 1000) ? strsqlerrm : strsqlerrm.substring(0, 1000) },
							"INSERT");

			row = (SourceBean) transExec.executeQuery("WRITE_CLOB_COMUNICAZIONE", new Object[] { prgWsErr }, "SELECT");
			CLOB resultClob = (CLOB) row.getAttribute("ROW.TXTTRACCIATOXML");

			outStream = resultClob.getCharacterOutputStream();

			int chunk = resultClob.getChunkSize();

			String strMessage = txtTracciatoXml;
			String subStr = "";

			while (strMessage.length() > chunk) {
				subStr = strMessage.substring(0, chunk);
				strMessage = strMessage.substring(chunk - 1);
				outStream.write(subStr, 0, subStr.length());
			}
			outStream.write(strMessage, 0, strMessage.length());

			outStream.flush();

			transExec.commitTransaction();

		} catch (Exception e) {
			try {
				if (transExec != null)
					transExec.rollBackTransaction();
			} catch (EMFInternalError e1) {
				throw new Exception("DBErrorLogger: problema con la rollback");
			}
			throw e;
		}
	}
}