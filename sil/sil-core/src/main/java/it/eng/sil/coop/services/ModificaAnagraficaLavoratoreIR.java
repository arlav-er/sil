package it.eng.sil.coop.services;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
public class ModificaAnagraficaLavoratoreIR implements IFaceService {

	private static final Logger log = Logger.getLogger(ModificaAnagraficaLavoratoreIR.class.getName());

	private Connection dataConnection = null;
	private CallableStatement command = null;
	private ResultSet dr = null;

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
		String cognome = (String) in.get(1);
		String nome = (String) in.get(2);
		String dataNascita = (String) in.get(3);
		String codComNascita = (String) in.get(4);
		String codCpi = (String) in.get(5);
		String codProvinciaPolo = (String) in.get(6);
		String dataInizio = (String) in.get(7);
		String codMonoTipoCpi = (String) in.get(8);

		dataConnection = getConnection();

		String codiceRit = "-1";
		int ret = 0;
		try {
			String statement = "{ call ? := PG_INDICE_REGIONALE_LAVORATORE.modificaAnagraficaLavoratoreIR(?,?,?,?,?,?,?,?,?,?,?,?,?) }";

			command = dataConnection.prepareCall(statement);

			// imposto i parametri
			command.registerOutParameter(1, Types.VARCHAR);
			command.setString(2, codiceFiscale);
			command.setString(3, cognome);
			command.setString(4, nome);
			command.setString(5, dataNascita);
			command.setString(6, codComNascita);
			command.setString(7, codCpi);
			command.setString(8, codProvinciaPolo);
			command.setString(9, dataInizio);
			command.setString(10, codMonoTipoCpi);
			command.setString(11, cdnGruppo);
			command.setString(12, cdnProfilo);
			command.setString(13, strMittente);
			command.setString(14, cdnUtente);

			dr = command.executeQuery();

			codiceRit = command.getString(1);
			stato = codiceRit;
			ret = Integer.parseInt(codiceRit);
			log.debug("Modifica anagrafica lavoratore " + codiceFiscale + " codiceRit=" + codiceRit);
		} catch (Exception e) {
			log.fatal("Modifica anagrafica lavoratore " + codiceFiscale + ". ECCEZIONE NELL'ACCESSO AL DB ", e);
			stato = e.getMessage();
			// e.printStackTrace();
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
				log.fatal("Modifica anagrafica lavoratore " + codiceFiscale
						+ ". Eccezione nella chiusura delle connessioni al db ", ex);
				// ex.printStackTrace();

				/*
				 * throw new CoopApplicationException_Lavoratore(
				 * "Attenzione: è giunta all'Indice Regionale una richiesta di modifica dati anagrafici "+
				 * "per il lavoratore "+codiceFiscale +". L'aggiornamento sull'Indice Regionale è fallito"+
				 * " per cause tecniche: si invita a riprovare", codiceFiscale);
				 */
			}
		}
		if (ret < 0) {
			Vector param = new Vector();
			param.add(codiceFiscale);
			/*
			 * String contenutoMsg = ""; switch (ret) { case -1 : contenutoMsg =
			 * "Attenzione: è giunta all'Indice Regionale una richiesta di modifica dati anagrafici "+
			 * "per il lavoratore "+codiceFiscale +". L'aggiornamento sull'Indice Regionale è fallito"+
			 * " per cause tecniche: si invita a riprovare. Controllare il Master"; break; default: contenutoMsg =
			 * "Attenzione: è giunta all'Indice Regionale una richiesta di modifica dati anagrafici "+
			 * "per il lavoratore "+codiceFiscale +". L'aggiornamento sull'Indice Regionale è fallito"+
			 * " per cause tecniche: si invita a riprovare. Controllare il Master"; }
			 */
			log.error(MessageBundle.getMessage(IRMessageCodes.NOTIFICHE_IR.MODIFICA_ANAG_FAIL, null));
			throw new CoopApplicationException_Lavoratore(IRMessageCodes.NOTIFICHE_IR.MODIFICA_ANAG_FAIL,
					codiceFiscale);
		}

	}

	public Connection getConnection() {
		return new EjbDbConnection().getConnection();
	}

	public String getStato() {
		return stato;
	}

}
