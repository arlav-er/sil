package it.eng.sil.coop.webservices.agenda.appuntamento;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;

public class ContattoUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ContattoUtils.class.getName());

	public static String recuperaPrgSpiPerContatto(BigDecimal cdnUt) {

		String retVal = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		try {

			Object[] inputParameters = new Object[1];
			inputParameters[0] = cdnUt;
			SourceBean sourceBean = (SourceBean) QueryExecutor.executeQuery("IDO_GET_PRGSPIUT", inputParameters,
					TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);
			if (sourceBean != null && sourceBean.containsAttribute("ROW")) {
				retVal = SourceBeanUtils.getAttrStr(sourceBean, "ROW.PRGSPI");
			}

		} catch (Exception e) {
			_logger.error("Errore: recupero prgspi appuntamento online", e);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(null, sqlCommand, dataResult);
		}

		return retVal;

	}

	public static EsitoContatto inserisciContatto(BigDecimal cdnLavoratore, String destinatarioEmail,
			String mittenteEmail, String oggettoEmail, String mailMessage, String codCpi, String strData, String strOra,
			String cellLavoratore, String prgSpi) {

		EsitoContatto contatto = new EsitoContatto();

		TransactionQueryExecutor txExecutor = null;
		BigDecimal prgContatto = null;

		try {

			Object[] obj = new Object[13];

			txExecutor = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			txExecutor.initTransaction();
			SourceBean rowsD = (SourceBean) txExecutor.executeQuery("NEXT_S_AG_CONTATTO", null, "SELECT");
			prgContatto = (BigDecimal) rowsD.getAttribute("ROW.KEY");

			Date oggi = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String strDataContatto = formatter.format(oggi);
			formatter = new SimpleDateFormat("HH:mm");
			String strOraContatto = formatter.format(oggi);

			String txtContatto =

					"Inviata a: " + destinatarioEmail + ", " + mittenteEmail + "\nOggetto: " + oggettoEmail + "\n\n"
							+ mailMessage;

			// Imposto i parametri
			obj[0] = prgContatto;
			obj[1] = codCpi;
			obj[2] = strDataContatto;
			obj[3] = strOraContatto;
			obj[4] = prgSpi;
			obj[5] = txtContatto;
			obj[6] = "O";
			obj[7] = "3"; // tipo contatto EMAIL
			obj[8] = cdnLavoratore;
			obj[9] = "150"; // TODO: ok 150?
			obj[10] = "150"; // TODO: ok 150?
			obj[11] = cellLavoratore;
			obj[12] = "N";

			Boolean result = (Boolean) txExecutor.executeQuery("INSERT_AG_CONTATTO", obj, "INSERT");

			if (result && result.booleanValue()) {
				txExecutor.commitTransaction();
				contatto.setPrgContatto(prgContatto);
				contatto.setInserito(true);
			} else {
				txExecutor.rollBackTransaction();
				_logger.error("Errore: inserimento contatto email appuntamento");
			}

		} catch (Exception e) {

			_logger.error("Errore: inserimento contatto email appuntamento", e);

			try {
				txExecutor.rollBackTransaction();
			} catch (EMFInternalError e1) {
				_logger.error("Errore: inserimento contatto email appuntamento", e1);
			}

		}

		return contatto;

	}

	public static boolean aggiornaContatto(BigDecimal prgContatto) {

		boolean isContattoAggiornato = false;

		Object[] obj = new Object[2];
		obj[0] = "S";
		obj[1] = prgContatto;

		try {

			// nello statement compare la parola SMS, ma può essere
			// utilizzato anche per l'aggiornamento di contatti EMAIL
			QueryExecutor.executeQuery("UPDATE_CONTATTO_SMS_INVIATO", obj, "UPDATE", Values.DB_SIL_DATI);
			isContattoAggiornato = true;

		} catch (Exception e) {
			_logger.error("Errore: aggiornamento contatto email appuntamento did online", e);
		}

		return isContattoAggiornato;

	}

}
