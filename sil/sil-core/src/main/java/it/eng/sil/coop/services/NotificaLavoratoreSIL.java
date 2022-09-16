/*
 * Created on 06-Apr-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
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

import com.engiweb.framework.dbaccess.DataConnectionManager;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class NotificaLavoratoreSIL implements IFaceService {

	private static final Logger log = Logger.getLogger(NotificaLavoratoreSIL.class.getName());

	private Connection dataConnection = null;
	private String statement = null;
	private CallableStatement command = null;
	private ResultSet dr = null;

	/**
	 * 
	 */
	public NotificaLavoratoreSIL() {

	}

	public void send(javax.jms.Message msg) throws JMSException {

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
		String contenutoMessaggio = (String) in.get(1);

		int codiceRit = 0; // errore interno:
							// 0: tutto ok
							// -99: errore non meglio specificato

		// EjbDbConnection ejbDbConnection=new EjbDbConnection();
		// dataConnection=ejbDbConnection.getConnection();

		try {
			// TODO Savino: NEW DB_CONNECTION
			dataConnection = DataConnectionManager.getInstance().getConnection().getInternalConnection();

			String statement = "{ call ?:=PG_COOP.putNotificaLavoratore(?,?) }";
			command = dataConnection.prepareCall(statement);

			// imposto i parametri
			command.registerOutParameter(1, Types.TINYINT);

			command.setString(2, codiceFiscale);
			command.setString(3, contenutoMessaggio);

			// eseguo!!
			// Chiamata alla Stored Procedure
			dr = command.executeQuery();

			codiceRit = command.getInt(1);

		} catch (Exception ex) {
			// System.out.println("NotificaLavoratoreSIL >>>>>> ECCEZIONE NELL'ACCESSO AL DB " + ex.getMessage());
			log.fatal("ECCEZIONE NELL'ACCESSO AL DB " + ex.getMessage());
			ex.printStackTrace();
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
				// System.out.println("NotificaLavoratoreSIL >>>>>> Eccezione nella chiusura delle connessioni al db " +
				// ex.getMessage());
				log.fatal("Eccezione nella chiusura delle connessioni al db " + ex.getMessage());
				// ex.printStackTrace();
			}
		}

	}

}
