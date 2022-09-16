package it.eng.sil.coop.services;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import it.eng.sil.coop.CoopApplicationException_Lavoratore;
import it.eng.sil.coop.ejbUtils.db.EjbDbConnection;
import it.eng.sil.coop.utils.IRMessageCodes;
import it.eng.sil.coop.utils.MessageBundle;

/**
 * @author savino
 */
public class ModificaCPICompIR implements IFaceService {

	private static final Logger log = Logger.getLogger(ModificaCPICompIR.class.getName());

	private Connection dataConnection = null;
	private CallableStatement command = null;
	private ResultSet dr = null;
	// variabile di stato comoda per il test statico
	private String stato;

	public void send(javax.jms.Message msg) throws CoopApplicationException_Lavoratore, JMSException {

		ObjectMessage message = (ObjectMessage) msg;
		Serializable arrObj = message.getObject();
		List in = (ArrayList) arrObj;

		String cdnUtente = message.getStringProperty("cdnUtente");
		String cdnGruppo = message.getStringProperty("cdnGruppo");
		String cdnProfilo = message.getStringProperty("cdnProfilo");
		String strMittente = message.getStringProperty("strMittente");
		String codiceFiscale = (String) in.get(0);
		String codProvinciaOp = (String) in.get(1);
		String codCpiNuovo = (String) in.get(2);
		String dataRichiesta = (String) in.get(3);
		String cognome = (String) in.get(4);
		String nome = (String) in.get(5);
		String codComNascita = (String) in.get(6);
		String dataNascita = (String) in.get(7);
		String codCpiPrec = (String) in.get(8);

		dataConnection = getConnection();

		String codiceRit = "-1";
		int ret = 0;
		try {
			String statement = "{ call ? := PG_INDICE_REGIONALE_LAVORATORE.modificaCPICompIR (?,?,?,?,?,?,?,?,?,?,?,?,?) }";

			command = dataConnection.prepareCall(statement);

			// imposto i parametri
			int i = 1;
			command.registerOutParameter(i++, Types.VARCHAR);
			command.setString(i++, codiceFiscale);
			command.setString(i++, codProvinciaOp);
			command.setString(i++, dataRichiesta);
			command.setString(i++, cognome);
			command.setString(i++, nome);
			command.setString(i++, dataNascita);
			command.setString(i++, codComNascita);
			command.setString(i++, codCpiPrec);
			command.setString(i++, codCpiNuovo);

			command.setString(i++, cdnGruppo);
			command.setString(i++, cdnProfilo);
			command.setString(i++, strMittente);
			command.setString(i++, cdnUtente);

			dr = command.executeQuery();

			codiceRit = command.getString(1);
			ret = Integer.parseInt(codiceRit);
			stato = codiceRit;
			log.debug("Modifica cpi tramite forzatura per il lavoratore " + codiceFiscale + " codiceRit=" + codiceRit);
		} catch (Exception ex) {
			log.fatal(
					"Modifica cpi tramite forzatura per il lavoratore " + codiceFiscale
							+ ". ECCEZIONE NELL'ACCESSO AL DB ."
							+ toString(cdnUtente, cdnGruppo, cdnProfilo, strMittente, codiceFiscale, codProvinciaOp,
									codCpiNuovo, dataRichiesta, cognome, nome, codComNascita, dataNascita, codCpiPrec),
					ex);
			// ex.printStackTrace();
			stato = ex.getMessage();
			ret = -1;
		} finally {
			try {
				if (dr != null) {
					dr.close();
				}
				if (command != null) {
					command.close();
				}
				if (dataConnection != null) {
					dataConnection.close();
				}
			} catch (Exception ex) {
				log.fatal("Eccezione nella chiusura delle connessioni al db ", ex);
				// ex.printStackTrace();
			}
		}
		// i casi 1 e 2 non esistono piu' (vedere procedura ...)
		if (ret < 0 /* || ret == 1 || ret == 2 */) {
			/*
			 * String contenutoMsg =
			 * "Attenzione: è giunta all'Indice Regionale una richiesta di modifica del cpi competente per il lavoratore a seguito di una forzatura del CPI in locale. "
			 * ; switch (ret) { case -1 : contenutoMsg
			 * +="L'aggiornamento sull'Indice Regionale è fallito per motivi tecnici. Si consiglia di verificare il master."
			 * ; break; case 1 : case 2: contenutoMsg
			 * +="L'aggiornamento sull'Indice Regionale non e' avvenuto perché  il tipo di forzatura effettuata è incongruente rispetto a quanto presente sull'IR."
			 * ; break; default: // non dovrebbe presentarsi ma non si sa mai.... contenutoMsg
			 * +="L'aggiornamento sull'Indice Regionale è fallito per motivi tecnici. Si consiglia di verificare il master."
			 * ; }
			 */

			log.error(MessageBundle.getMessage(IRMessageCodes.NOTIFICHE_IR.MODIFICA_CPI_FAIL, null));
			throw new CoopApplicationException_Lavoratore(IRMessageCodes.NOTIFICHE_IR.MODIFICA_CPI_FAIL, codiceFiscale);
		}

	}

	public String getStato() {
		return stato;
	}

	public Connection getConnection() {
		return new EjbDbConnection().getConnection();
	}

	public String toString(String cdnUtente, String cdnGruppo, String cdnProfilo, String strMittente,
			String codiceFiscale, String codProvinciaOp, String codCpiNuovo, String dataRichiesta, String cognome,
			String nome, String codComNascita, String dataNascita, String codCpiPrec) {
		StringBuffer s = new StringBuffer("Servizio ").append(getClass().getName()).append(" - ")
				.append("cdnGruppo        =").append(cdnGruppo).append(",cdnProfilo       =").append(cdnProfilo)
				.append(",strMittente      =").append(strMittente).append(",codiceFiscale    =").append(codiceFiscale)
				.append(",codProvinciaOp   =").append(codProvinciaOp).append(",codCpiNuovo      =").append(codCpiNuovo)
				.append(",dataRichiesta    =").append(dataRichiesta).append(",cognome          =").append(cognome)
				.append(",nome             =").append(nome).append(",codComNascita    =").append(codComNascita)
				.append(",dataNascita      =").append(dataNascita).append(",codCpiPrec       =").append(codCpiPrec);
		return s.toString();
	}
}
