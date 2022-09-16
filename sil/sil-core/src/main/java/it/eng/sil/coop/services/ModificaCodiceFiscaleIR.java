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
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.Bulk_NotificaLavoratoreSILMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.coop.utils.IRMessageCodes;
import it.eng.sil.coop.utils.MessageBundle;

/**
 * @author savino
 */
public class ModificaCodiceFiscaleIR implements IFaceService {

	private static final Logger log = Logger.getLogger(ModificaCodiceFiscaleIR.class.getName());

	private String dataSourceJNDI = null;
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
		String poloMittente = message.getStringProperty("Polomittente");

		String codiceFiscale = (String) in.get(0);
		String codiceFiscaleNuovo = (String) in.get(1);
		String codProvinciaOp = (String) in.get(2);
		String codCpi = (String) in.get(3);
		String cognome = (String) in.get(4);
		String nome = (String) in.get(5);
		String dataNascita = (String) in.get(6);
		String codComNascita = (String) in.get(7);
		String codMonoTipoCpi = (String) in.get(8);
		String dataInizio = (String) in.get(9);

		dataConnection = getConnection();

		String codiceRit = "-1";
		int ret = 0;
		try {
			String statement = "{ call ? := PG_INDICE_REGIONALE_LAVORATORE.modificaCodiceFiscaleIR(?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";

			command = dataConnection.prepareCall(statement);

			// imposto i parametri
			command.registerOutParameter(1, Types.VARCHAR);
			command.setString(2, codiceFiscale);
			command.setString(3, codiceFiscaleNuovo); // lavoratore da cancellare
			command.setString(4, cognome);
			command.setString(5, nome);
			command.setString(6, dataNascita);
			command.setString(7, codComNascita);
			command.setString(8, codProvinciaOp); // serve solo questo
			command.setString(9, codMonoTipoCpi);
			command.setString(10, codCpi); // cpi competente del lavoratore.
			command.setString(11, dataInizio);

			command.setString(12, cdnGruppo);
			command.setString(13, cdnProfilo);
			command.setString(14, strMittente);
			command.setString(15, cdnUtente);

			dr = command.executeQuery();

			codiceRit = command.getString(1);
			stato = codiceRit;
			ret = Integer.parseInt(codiceRit);
			log.info("Modifica codice fiscale completata con successo. Codice di ritorno:" + ret);
		} catch (Exception ex) {
			ret = -1;
			log.fatal("ECCEZIONE NELL'ACCESSO AL DB ", ex);
			stato = ex.getMessage();
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
				/*
				 * throw new CoopApplicationException_Lavoratore(
				 * "Attenzione: è giunta all'Indice Regionale una richiesta di modifica codice fiscale per il lavoratore "
				 * +codiceFiscale
				 * +". L'inserimento sull'Indice Regionale è fallito per cause tecniche: si invita a riprovare",
				 * codiceFiscaleNuovo);
				 */
			}
		}
		if (ret < 0 || ret == 5 || ret == 3 || ret == 2) {
			// String contenutoMsg = "";
			Vector param = new Vector();
			param.add(codiceFiscale);
			param.add(codiceFiscaleNuovo);
			switch (ret) {
			case -1:
				// contenutoMsg =
				// "Attenzione: è giunta all'Indice Regionale una richiesta di modifica del codice fiscale del
				// lavoratore "+codiceFiscale+" che diventa "+codiceFiscaleNuovo+". L'operazione sull'Indice Regionale è
				// fallita. Si consiglia di verificare il master";
				log.warn(MessageBundle.getMessage(IRMessageCodes.NOTIFICHE_IR.MODIFICA_CF_FAIL, param));
				throw new CoopApplicationException_Lavoratore(IRMessageCodes.NOTIFICHE_IR.MODIFICA_CF_FAIL, param,
						codiceFiscaleNuovo);
			case 2:
			case 3:
				// contenutoMsg =
				// "Attenzione: è giunta all'Indice Regionale una richiesta di modifica del codice fiscale del
				// lavoratore "+codiceFiscale+" che diventa "+codiceFiscaleNuovo+". L'operazione sull'Indice Regionale
				// e' avvenuta correttamente. Il lavoratore "+codiceFiscale+ " e' stato cancellato.";

				// notifica sul lavoratore 2 a tutti gli altri poli.
				TestataMessageTO testataMessaggio = new TestataMessageTO();
				testataMessaggio.setPoloMittente(poloMittente);
				testataMessaggio.setCdnUtente(cdnUtente);
				testataMessaggio.setCdnGruppo(cdnGruppo);
				testataMessaggio.setCdnProfilo(cdnProfilo);
				testataMessaggio.setStrMittente(strMittente);

				Bulk_NotificaLavoratoreSILMessage notificaAll = new Bulk_NotificaLavoratoreSILMessage();
				notificaAll.setTestata(testataMessaggio);

				notificaAll.setCodiceFiscale(codiceFiscale);
				notificaAll.setContenutoMessaggio(IRMessageCodes.NOTIFICHE_IR.MODIFICA_CF_CANCELLATO, param);

				OutQ outQ = new OutQ();

				notificaAll.setDataSourceJndi(dataSourceJNDI);
				notificaAll.send(outQ);
				log.warn(MessageBundle.getMessage(IRMessageCodes.NOTIFICHE_IR.MODIFICA_CF_CANCELLATO, param));
				break;

			default: // il caso 5 non dovrebbe presentarsi
				// contenutoMsg="Errore non previsto";
				log.warn(MessageBundle.getMessage(IRMessageCodes.NOTIFICHE_IR.MODIFICA_CF_FAIL, param));
				throw new CoopApplicationException_Lavoratore(IRMessageCodes.NOTIFICHE_IR.MODIFICA_CF_FAIL, param,
						codiceFiscaleNuovo);
			}
		}

	}

	public Connection getConnection() {
		EjbDbConnection ejbDbConnection = new EjbDbConnection();
		dataSourceJNDI = ejbDbConnection.getDataSourceJndi();

		return ejbDbConnection.getConnection();
	}

	public String getStato() {
		return stato;
	}

}
