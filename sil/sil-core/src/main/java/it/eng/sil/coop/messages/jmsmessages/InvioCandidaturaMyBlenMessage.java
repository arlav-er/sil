package it.eng.sil.coop.messages.jmsmessages;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import javax.jms.ObjectMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.axis.AxisFault;
import org.apache.log4j.Logger;

import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.coop.messages.AbstractMessage;
import it.eng.sil.coop.messages.IFaceMessage;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.queues.IFaceQueue;
import it.eng.sil.coop.wsClient.messageReceiver.ServiceParameters;

public class InvioCandidaturaMyBlenMessage extends AbstractMessage implements IFaceMessage {

	public static final String END_POINT_NAME = "InvioClicLavoroMyBlen";

	private static final Logger log = Logger.getLogger(InvioCandidaturaMyBlenMessage.class.getName());

	private final int maxRedeliveries = 100;

	private String datiCandidaturaXml = null;
	private String usernameWs = null;
	private String passwordWs = null;
	private String statoCandidatura = null;
	private String prgCandidatura;

	public InvioCandidaturaMyBlenMessage() {
	}

	public InvioCandidaturaMyBlenMessage(String usernameWs, String passwordWs, String statoCandidatura,
			String prgCandidatura) {
		this.usernameWs = usernameWs;
		this.passwordWs = passwordWs;
		this.statoCandidatura = statoCandidatura;
		this.prgCandidatura = prgCandidatura;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeit.eng.sil.coop.messages.IFaceMessage#setObjectMessage(javax.jms. ObjectMessage)
	 */
	public void setObjectMessage(ObjectMessage msg) throws Exception {

		try {
			// leggo i parametri di autenticazione/instradamento
			// dall'ObjectMessage
			// e li setto nell'oggetto messaggio
			testata.setDestinazione(msg.getStringProperty("Destinazione"));
			testata.setServizio("InvioCandidaturaMyBlen");

			/*
			 * testata.setPoloMittente( msg.getStringProperty("Polomittente")); testata.setCdnUtente(
			 * msg.getStringProperty("cdnUtente")); testata.setCdnGruppo( msg.getStringProperty("cdnGruppo"));
			 * testata.setCdnProfilo( msg.getStringProperty("cdnProfilo")); testata.setStrMittente(
			 * msg.getStringProperty("strMittente"));
			 */
			testata.setMaxRedeliveries(maxRedeliveries);
			Serializable arrObj = msg.getObject();

		} catch (Exception ex) {
			// Eccezione nella lettura dei parametri OBBLIGATORI. Gravissimo!
			// System.out.println("PutLavoratoreIRMessage Errore nella lettura dei parametri di instradamento "
			// +
			// "e/o autenticazione.\nMessaggio malformato " + ex.getMessage());
			log.fatal("Errore nella lettura dei parametri di instradamento "
					+ "e/o autenticazione.\nMessaggio malformato " + ex.getMessage());

			throw new Exception("Messaggio malformato nei dati di instradamento/autenticazione");
		}

		try {
			// ora leggo il contenuto applicativo
			Serializable arrObj = msg.getObject();
			if (arrObj instanceof ArrayList) {
				ArrayList<String> appParam = (ArrayList<String>) arrObj;
				prgCandidatura = (String) appParam.get(0);
				usernameWs = (String) appParam.get(1);
				passwordWs = (String) appParam.get(2);
				statoCandidatura = (String) appParam.get(3);
				datiCandidaturaXml = (String) appParam.get(4);
			}
		} catch (Exception ex) {
			// Eccezione nella lettura del contenuto applicativo.
			// System.out.println("PutLavoratoreIRMessage Errore nella lettura del contenuto applicativo. "
			// +
			// "Il contenuto potrebbe essere completamente o parzialmente mancante "
			// + ex.getMessage());
			log.fatal("Errore nella lettura del contenuto applicativo. "
					+ "Il contenuto potrebbe essere completamente o parzialmente mancante " + ex.getMessage());
			// non esco dalla computazione. Accetto anche un contenuto nullo.
		}

	}

	public void send(IFaceQueue Q) throws Exception {
		ArrayList appParam = new ArrayList();

		appParam.add(prgCandidatura);
		appParam.add(usernameWs);
		appParam.add(passwordWs);
		appParam.add(statoCandidatura);
		appParam.add(datiCandidaturaXml);

		super.send(Q, appParam);
	}

	public void callWebservice() throws Exception {

		ServiceParameters param = new ServiceParameters();
		/*
		 * param.setCdnGruppo(testata.getCdnGruppo()); param.setCdnProfilo(testata.getCdnProfilo());
		 * param.setCdnUtente(testata.getCdnUtente()); param.setDestinazione(testata.getDestinazione());
		 * param.setPoloMittente(testata.getPoloMittente()); param.setStrMittente(testata.getStrMittente());
		 */

		param.setServizio("InvioCandidaturaMyBlen");
		param.put("xmlRequest", datiCandidaturaXml);

		it.eng.sil.coop.webservices.myblen.ws.RicezioneClicLavoroSILProxy proxy = new it.eng.sil.coop.webservices.myblen.ws.RicezioneClicLavoroSILProxy();
		EndPoint endPoint = new EndPoint();
		endPoint.init(dataSourceJndi, END_POINT_NAME);
		String address = endPoint.getUrl();
		log.debug("Endpoint address: " + address);
		proxy.setEndpoint(address);
		String stato = statoCandidatura;
		try {
			String risposta = proxy.inserisciCandidatura(usernameWs, passwordWs, datiCandidaturaXml);
			log.debug("Valore tornato dal WS myblen: " + risposta);

			if ("0".equals(risposta)) {
				if ("PA".equalsIgnoreCase(statoCandidatura)) {
					stato = "PI";
				} else {
					stato = "VI";
				}
			}

		} catch (Exception e) {
			log.error("Errore nel web service di myblen", e);
		}

		/*
		 * se risposta OK setto il CODSTATOINVIOCL a PI se prima era PA o VI se era VA se risposta KO lo lascio così
		 * (chiedere a Novella)
		 * 
		 */
		int ret = updateEsitoCandidatura(stato);
		log.debug("aggiornamento stato candidatura" + ret);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.sil.coop.messages.AbstractMessage#setTestata(it.eng.sil.coop.messages .TestataMessageTO)
	 */
	public void setTestata(TestataMessageTO _testata) {

		super.setTestata(_testata);
		testata.setMaxRedeliveries(maxRedeliveries);

	}

	public String getDatiRichiestaXml() {
		return datiCandidaturaXml;
	}

	public void setDatiRichiestaXml(String datiRichiestaXml) {
		this.datiCandidaturaXml = datiRichiestaXml;
	}

	public String getUsernameWs() {
		return usernameWs;
	}

	public void setUsernameWs(String usernameWs) {
		this.usernameWs = usernameWs;
	}

	public String getPasswordWs() {
		return passwordWs;
	}

	public void setPasswordWs(String passwordWs) {
		this.passwordWs = passwordWs;
	}

	public String getStatoCandidatura() {
		return statoCandidatura;
	}

	public void setStatoCandidatura(String statoCandidatura) {
		this.statoCandidatura = statoCandidatura;
	}

	private int updateEsitoCandidatura(String stato) throws AxisFault {
		String prg = null;
		if (prgCandidatura != null) {
			prg = prgCandidatura.toString();
		}
		Connection conn = null;
		ResultSet dr = null;
		Statement command = null;
		// attivo la connessione
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(dataSourceJndi);

			conn = ds.getConnection();

			String statement = "UPDATE CL_CANDIDATURA SET CODSTATOINVIOCL = '" + stato
					+ "', NUMKLOCANDIDATURA=NUMKLOCANDIDATURA+1 WHERE PRGCANDIDATURA =" + prg;
			log.info(statement);
			command = conn.createStatement();

			int ret = command.executeUpdate(statement);

			return ret;

		} catch (Exception ex) {
			log.fatal("Errore nell'aggiornamento dello stato invio candidatura", ex);
			return -1;
		} finally {
			try {
				if (dr != null) {
					dr.close();
				}
				if (command != null) {
					command.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ex) {
				log.fatal("Eccezione nella chiusura delle connessioni al db ", ex);
			}
		}
	}

	public void setServiceParameters(Map param) {

		ServiceParameters serviceParam = new ServiceParameters(param);
		serviceParam.put("prgCandidatura", prgCandidatura);
		serviceParam.put("statoCandidatura", statoCandidatura);
		testata.setServizio(serviceParam.getServizio());
		setDatiRichiestaXml((String) serviceParam.get("dati"));
	}

}
