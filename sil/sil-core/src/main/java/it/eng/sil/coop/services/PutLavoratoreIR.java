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

import it.eng.sil.coop.CoopApplicationException_Lavoratore;
import it.eng.sil.coop.ejbUtils.db.EjbDbConnection;
import it.eng.sil.coop.utils.IRMessageCodes;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class PutLavoratoreIR implements IFaceService {

	private static final Logger log = Logger.getLogger(PutLavoratoreIR.class.getName());

	private Connection dataConnection = null;
	private String statement = null;
	private CallableStatement command = null;
	private ResultSet dr = null;

	/**
	 * 
	 */
	public PutLavoratoreIR() {

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
		String nome = (String) in.get(1);
		String cognome = (String) in.get(2);
		String dataNascita = (String) in.get(3);
		String comune = (String) in.get(4);
		String dataInizio = (String) in.get(5);
		String codCpi = (String) in.get(6);
		String codMonoTipoCpi = (String) in.get(7);
		String cpiComp = (String) in.get(8);

		int codiceRit = 0; // errore interno:
							// 0: tutto ok
							// -1: errore di chiave duplicata
							// -99: errore non meglio specificato
		int errCode = 0;// mappa direttamente l'errore Oracle
						// prende i seguenti valori: 0 = tutto ok
						// -1 = DUP_VAL_ON_INDEX (chiave duplicata)
						//
		// TracerSingleton.log("ServiziLavoratoreDispatcher", TracerSingleton.DEBUG ,
		// "ServiziLavoratoreIRExecutor::putLavoratoreIR " + "invocato" );

		EjbDbConnection ejbDbConnection = new EjbDbConnection();
		dataConnection = ejbDbConnection.getConnection();

		try {

			String statement = "{ call ?:=PG_INDICE_REGIONALE_LAVORATORE.pdPutLavoratoreIR(?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
			command = dataConnection.prepareCall(statement);

			// imposto i parametri
			command.registerOutParameter(1, Types.TINYINT);

			command.setString(2, codiceFiscale);
			command.setString(3, cognome);
			command.setString(4, nome);
			command.setString(5, dataNascita);
			command.setString(6, comune);
			command.setString(7, dataInizio);
			command.setString(8, cdnGruppo);
			command.setString(9, cdnProfilo);
			command.setString(10, strMittente);
			command.setString(11, cdnUtente);
			command.setString(12, codCpi);
			command.setString(13, codMonoTipoCpi);
			command.setString(14, cpiComp);
			command.registerOutParameter(15, Types.VARCHAR);

			// eseguo!!
			// Chiamata alla Stored Procedure
			dr = command.executeQuery();

			codiceRit = command.getInt(1);
			errCode = command.getInt(15);

		} catch (Exception ex) {
			// System.out.println("PutLavoratoreIR >>>>>> ECCEZIONE NELL'ACCESSO AL DB " + ex.getMessage());
			log.fatal("Inserimento lavoratore " + codiceFiscale + ". ECCEZIONE NELL'ACCESSO AL DB ", ex);
			// ex.printStackTrace();
			codiceRit = -2;
			// throw new CoopApplicationException_Lavoratore("Attenzione: è giunta all'Indice Regionale una richiesta di
			// inserimento per il lavoratore. L'inserimento sull'Indice Regionale è fallito per cause tecniche: si
			// invita a riprovare", codiceFiscale );

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
				// System.out.println("PutLavoratoreIR >>>>>> Eccezione nella chiusura delle connessioni al db " +
				// ex.getMessage());
				log.fatal("Inserimento lavoratore " + codiceFiscale
						+ ". Eccezione nella chiusura delle connessioni al db " + ex.getMessage(), ex);
				// ex.printStackTrace();
				// throw new CoopApplicationException_Lavoratore("Attenzione: è giunta all'Indice Regionale una
				// richiesta di inserimento per il lavoratore. L'inserimento sull'Indice Regionale è fallito per cause
				// tecniche: si invita a riprovare", codiceFiscale);
			}
		}

		if (codiceRit < 0) { // abbiamo sicuramente un errore, da comunicare al chiamante per l'invocazione
								// del layer delle notifiche
			switch (codiceRit) {
			case -1: {
				// System.out.println("PutLavoratoreIR >>>>>> LAVORATORE GIA' PRESENTE ");
				log.warn("LAVORATORE " + codiceFiscale + " GIA' PRESENTE");
				throw new CoopApplicationException_Lavoratore(IRMessageCodes.NOTIFICHE_IR.INSERIMENTO_LAV_PRESENTE,
						codiceFiscale);
			}
			default: { // se -2 errore catturato dal primo blocco catch o errore generico nella procedure
				log.error("IMPOSSIBILE INSERIRE IL LAVORATORE" + codiceFiscale + " : SQLCODE=" + errCode);
				throw new CoopApplicationException_Lavoratore(IRMessageCodes.NOTIFICHE_IR.INSERIMENTO_LAV_FAIL,
						codiceFiscale);
			}
			}
		} else { // tutto ok
			// System.out.println("PutLavoratoreIR Inserimento avvenuto con successo");
			log.debug("Inserimento lavoratore " + codiceFiscale + " avvenuto con successo");

		}

	}

}
