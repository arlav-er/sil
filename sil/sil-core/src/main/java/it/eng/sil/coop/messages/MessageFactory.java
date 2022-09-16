/*
 * Created on 07-Feb-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.messages;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;

import org.apache.log4j.Logger;

import it.eng.sil.coop.messages.dbmessages.DbMessage;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class MessageFactory {

	private static final Logger log = Logger.getLogger(MessageFactory.class.getName());

	// connessioni e variabili del db
	private Connection dataConnection = null;
	private String statement = null;
	private CallableStatement command = null;
	private ResultSet dr = null;

	/**
	 * @param poloMittente
	 * @param utente
	 * @param gruppo
	 * @param profilo
	 * @param nomeCognomeMittente
	 */

	public MessageFactory(Connection connection) {
		this.dataConnection = connection;
	}

	/***
	 * getDbMessage
	 * 
	 * preleva l'eventuale messaggio dal db
	 * 
	 * @return dbMessage ritorna il messaggio (null se vuoto)
	 */
	public DbMessage getDbMessage() throws Exception {

		int result = 0;
		Clob contenutoMessaggio = null;

		DbMessage dbMessage = null;

		try {
			String statement = "{ call ?:=PG_COOP.pollDb2JMSMessage(?,?,?,?,?,?,?,?,?,?) }";
			command = dataConnection.prepareCall(statement);

			// imposto i parametri
			command.registerOutParameter(1, Types.TINYINT); // result
			command.registerOutParameter(2, Types.NUMERIC); // prgMsg
			command.registerOutParameter(3, Types.VARCHAR); // destinazione
			command.registerOutParameter(4, Types.VARCHAR); // servizio
			command.registerOutParameter(5, Types.VARCHAR); // poloMittente
			command.registerOutParameter(6, Types.VARCHAR); // utente
			command.registerOutParameter(7, Types.VARCHAR); // gruppo
			command.registerOutParameter(8, Types.VARCHAR); // profilo
			command.registerOutParameter(9, Types.VARCHAR); // mittente
			command.registerOutParameter(10, Types.NUMERIC); // maxRedeliveries
			command.registerOutParameter(11, Types.CLOB); // contenutoMessaggio

			// eseguo!!
			// Chiamata alla Stored Procedure
			dr = command.executeQuery();
			result = command.getInt(1);
			if (result != -1) { // se non abbiamo risultati la stored restituisce -1
				TestataMessageTO testata = new TestataMessageTO();
				dbMessage = new DbMessage();

				dbMessage.setPrgMsg(command.getInt(2));
				testata.setDestinazione(command.getString(3));
				testata.setServizio(command.getString(4));
				testata.setPoloMittente(command.getString(5));
				testata.setCdnUtente(command.getString(6));
				testata.setCdnGruppo(command.getString(7));
				testata.setCdnProfilo(command.getString(8));
				testata.setStrMittente(command.getString(9));
				testata.setMaxRedeliveries(command.getInt(10));
				dbMessage.setContenutoMessaggio(command.getClob(11));

				dbMessage.setTestata(testata);

			}

		} catch (Exception ex) {
			// System.out.println("MessageFactory::getDbMessage Errore nella ricezione del messaggio dal DB" +
			// ex.getMessage());
			log.fatal("Errore nella ricezione del messaggio dal DB" + ex.getMessage());
			ex.printStackTrace();
			throw new Exception("Errore nella ricezione del messaggio dal DB");
		} finally {
			try {
				if (dr != null) {
					dr.close();
				}
				if (command != null) {
					command.close();
				}
			} catch (Exception ex) {
				// System.out.println("MessageFactory::delDbMessage Errore nella chiusura della connessione al DB" +
				// ex.getMessage());
				log.fatal("Errore nella chiusura della connessione al DB" + ex.getMessage());
				// ex.printStackTrace();
			}
		}

		return dbMessage;

	}

	public int delDbMessage(DbMessage dbMessage) throws Exception {

		int prgMsg = dbMessage.getPrgMsg();
		int result = 0;

		try {
			System.out.println("Cancellazione - prg: " + prgMsg);
			String statement = "{ call ?:=PG_COOP.delDb2JMSMessage(?) }";
			command = dataConnection.prepareCall(statement);

			// imposto i parametri
			command.registerOutParameter(1, Types.TINYINT); // result
			command.setInt(2, prgMsg); // prgMsg

			// eseguo!!
			// Chiamata alla Stored Procedure
			dr = command.executeQuery();

			result = command.getInt(1);

		} catch (Exception ex) {
			// System.out.println("MessageFactory::delDbMessage Errore nella cancellazione del messaggio dal DB " +
			// ex.getMessage());
			log.fatal("Errore nella cancellazione del messaggio dal DB " + ex.getMessage());
			ex.printStackTrace();
			throw new Exception("Errore nella cancellazione del messaggio dal DB");
		} finally {
			try {
				if (dr != null) {
					dr.close();
				}
				if (command != null) {
					command.close();
				}
			} catch (Exception ex) {
				// System.out.println("MessageFactory::delDbMessage Errore nella chiusura della connessione al DB" +
				// ex.getMessage());
				log.fatal(
						"MessageFactory::delDbMessage Errore nella chiusura della connessione al DB" + ex.getMessage());
				// ex.printStackTrace();
			}
		}
		return result;
	}

}
