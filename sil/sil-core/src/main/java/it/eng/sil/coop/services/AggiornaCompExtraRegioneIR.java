/*
 * Creato il 21-apr-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
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

/**
 * @author riccardi
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class AggiornaCompExtraRegioneIR implements IFaceService {

	private static final Logger log = Logger.getLogger(AggiornaCompetenzaIR.class.getName());

	private Connection dataConnection = null;
	private String statement = null;
	private CallableStatement command = null;
	private ResultSet dr = null;

	/**
	 * 
	 */
	public AggiornaCompExtraRegioneIR() {

	}

	public void send(javax.jms.Message msg) throws CoopApplicationException_Lavoratore, JMSException {

		ObjectMessage message = (ObjectMessage) msg;
		Serializable arrObj = message.getObject();
		List in = (ArrayList) arrObj;

		String operazione = message.getStringProperty("Servizio");
		String poloMittente = message.getStringProperty("Polomittente");
		String cdnUtente = message.getStringProperty("cdnUtente");
		String cdnGruppo = message.getStringProperty("cdnGruppo");
		String cdnProfilo = message.getStringProperty("cdnProfilo");
		String strMittente = message.getStringProperty("strMittente");

		String codiceFiscale = (String) in.get(0);
		;
		String codCpi = (String) in.get(1);
		String codMonoTipoCpi = (String) in.get(2);
		String cpiComp = (String) in.get(3);
		String datInizio = (String) in.get(4);
		String dataValidita = (String) in.get(5);
		String cognome = (String) in.get(6);
		String nome = (String) in.get(7);
		String datNasc = (String) in.get(8);
		String codComNas = (String) in.get(9);

		int codiceRit = 0;

		EjbDbConnection ejbDbConnection = new EjbDbConnection();
		dataConnection = ejbDbConnection.getConnection();

		try {

			String statement = "{ call ?:=PG_INDICE_REGIONALE_LAVORATORE.aggiornaCompetenzaIR (?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
			command = dataConnection.prepareCall(statement);

			// imposto i parametri
			command.registerOutParameter(1, Types.INTEGER);
			command.setString(2, codiceFiscale);
			command.setString(3, codCpi);
			command.setString(4, codMonoTipoCpi);
			command.setString(5, cpiComp);
			command.setString(6, datInizio);
			command.setString(7, dataValidita);
			command.setString(8, cognome);
			command.setString(9, nome);
			command.setString(10, datNasc);
			command.setString(11, codComNas);
			command.setString(12, cdnGruppo);
			command.setString(13, cdnProfilo);
			command.setString(14, strMittente);
			command.setString(15, cdnUtente);

			// eseguo!!
			// Chiamata alla Stored Procedure
			dr = command.executeQuery();

			codiceRit = command.getInt(1);// valori possibili:
											// 0: OK!

			log.debug("Aggiorna competenza extra regione del lavoratore " + codiceFiscale + ". codiceRit=" + codiceRit);
		} catch (Exception ex) {
			log.fatal("Aggiorna competenza extra regione lavoratore " + codiceFiscale + " codiceRit" + codiceRit
					+ ". Eccezione nell'aggiornamento dei dati", ex);
			// ex.printStackTrace();
			codiceRit = -1;
			// throw new CoopApplicationException_Lavoratore("Attenzione: è giunta all'Indice Regionale una richiesta di
			// aggiornamento della competenza per il lavoratore. L'aggiornamento sull'Indice Regionale è fallito per
			// cause tecniche: si invita a riprovare.", codiceFiscale);
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
				// System.out.println("AggiornaCompetenzaIR Eccezione nella chiusura delle connessioni al db");
				log.fatal("Aggiorna competenza extra regione lavoratore " + codiceFiscale + " codiceRit" + codiceRit
						+ ". Eccezione nella chiusura delle connessioni al db", ex);
				// ex.printStackTrace();
			}
		}

		switch (codiceRit) {
		case 0: {
			// nessun problema
			break;
		}
		case -1: {
			throw new CoopApplicationException_Lavoratore(IRMessageCodes.NOTIFICHE_IR.AGGIONA_COMPETENZA_ER_FAIL,
					codiceFiscale);
		}
		case -2: {
			// throw new CoopApplicationException_Lavoratore("Attenzione: è giunta all'Indice Regionale una richiesta di
			// aggiornamento della competenza per il lavoratore. Il lavoratore sull'Indice Regionale non era presente
			// quindi è stato inserito.", codiceFiscale);
			break;
		}
		case -3: {
			// throw new CoopApplicationException_Lavoratore("Attenzione: è giunta all'Indice Regionale una richiesta di
			// aggiornamento della competenza per il lavoratore. L'aggiornamento sull'Indice Regionale è fallito: la
			// competenza amministrativa del lavoratore è già persa.", codiceFiscale);
			break;
		}
		default: {
			throw new CoopApplicationException_Lavoratore(IRMessageCodes.NOTIFICHE_IR.AGGIONA_COMPETENZA_ER_FAIL,
					codiceFiscale);
		}
		}

	}

}
