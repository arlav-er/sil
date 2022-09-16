package it.eng.sil.coop.webservices.agenda.appuntamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.mail.SendMail;
import it.eng.sil.mail.SendMailException;

public class EmailUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EmailUtils.class.getName());

	/**
	 * recupera il server smtp a partire dalla taballa ts_generale
	 * 
	 * @return stringa server smtp (es. smtp.eng.it)
	 */
	public static String recuperaSmtpServerEmail() {

		String retVal = null;
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		try {

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("RecuperaSmtpServer");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			retVal = (String) sb.getAttribute("ROW.smtpserver");

		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			_logger.error("Errore: recupero smtp server email appuntamento did online");
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}

		return retVal;

	}

	/**
	 * 
	 * @return Array di stringhe: [0] oggetto, [1] mittente
	 */

	public static String[] recuperaSpecsEmail(String codiceRichiesta, String identificativoProvincia) {

		String[] retVal = new String[] { "", "", "", "", "", "" };

		try {

			Object[] args = new Object[2];
			args[0] = codiceRichiesta;
			args[1] = identificativoProvincia;
			SourceBean sourceBean = (SourceBean) QueryExecutor.executeQuery("SELECT_SPECS_EMAIL_APPUNTAMENTO_ONLINE",
					args, TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

			if (sourceBean != null && sourceBean.containsAttribute("ROW")) {

				retVal[0] = (String) sourceBean.getAttribute("ROW.STROGGETTO");
				retVal[1] = (String) sourceBean.getAttribute("ROW.STREMAILMITTENTE");
				retVal[2] = (String) sourceBean.getAttribute("ROW.CODTIPOEMAIL");
				retVal[3] = (String) sourceBean.getAttribute("ROW.STRDESTINATARI");
				retVal[4] = (String) sourceBean.getAttribute("ROW.STRDESTCC");
				retVal[5] = (String) sourceBean.getAttribute("ROW.STRDESTCCN");

			}

		} catch (Exception e) {
			_logger.error("Errore recupero codTipoSms", e);
		}

		return retVal;

	}

	public static boolean inviaEmail(String strEmailLav, String oggettoEmail, String mittenteEmail, String mailMessage,
			String altriDestinatari, String altriDestinatariNascosti) {

		boolean isEmailSpedita = false;

		String to = strEmailLav;
		if (altriDestinatari != null && !"".equalsIgnoreCase(altriDestinatari)) {
			to += "," + altriDestinatari;
		}

		String bcc = mittenteEmail;
		if (altriDestinatariNascosti != null && !"".equalsIgnoreCase(altriDestinatariNascosti)) {
			bcc += "," + altriDestinatariNascosti;
		}

		SendMail sendMail = new SendMail();
		sendMail.setSMTPServer(recuperaSmtpServerEmail());
		sendMail.setFromRecipient(mittenteEmail);
		sendMail.setToRecipient(to);
		sendMail.setBccRecipient(bcc);
		sendMail.setSubject(oggettoEmail);
		sendMail.setBody(mailMessage);

		try {

			sendMail.send();
			isEmailSpedita = true;

		} catch (SendMailException e) {
			_logger.error("Errore: invio email appuntamento did online (a)", e);
		} catch (MessagingException e) {
			_logger.error("Errore: invio email appuntamento did online (b)", e);
		}

		return isEmailSpedita;

	}

	public static boolean insertContattoAndSendEmail(BigDecimal cdnLavoratore, String cellLavoratore, String codCpi,
			String destinatarioEmail, String strNomeLav, String strCognomeLav, String strEmailLav, String strDataApp,
			String strOraApp, String strDurataApp, String strDescrizioneCpi, String strIndirizzoCpi, String strTelCpi,
			String prgSpi, boolean isAppuntamentoOk, boolean isSpiDisponibile, String strNomeSpi, String strCognomeSpi,
			String strTelSpi, String codiceRichiesta, String identificativoProvincia, String mailMessage,
			String mittenteEmail, String oggettoEmail, String altriDestinatari, String altriDestinatariNascosti) {

		boolean success = false;

		EsitoContatto esitoContatto = ContattoUtils.inserisciContatto(cdnLavoratore, destinatarioEmail, mittenteEmail,
				oggettoEmail, mailMessage, codCpi, strDataApp, strOraApp, cellLavoratore, prgSpi);

		if (esitoContatto.isInserito()) {

			boolean isEmailSpedita = inviaEmail(strEmailLav, oggettoEmail, mittenteEmail, mailMessage, altriDestinatari,
					altriDestinatariNascosti);

			if (isEmailSpedita) {

				boolean isContattoAggiornato = ContattoUtils.aggiornaContatto(esitoContatto.getPrgContatto());

				if (isContattoAggiornato) {
					success = true;
				}

			}

		}

		return success;

	}

}
