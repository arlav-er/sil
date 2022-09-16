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
import it.eng.sil.coop.webservices.myportal.ws.RicezioneClicLavoroSILProxy;
import it.eng.sil.coop.wsClient.messageReceiver.ServiceParameters;

public class InvioVacancyMyPortalMessage extends AbstractMessage implements IFaceMessage {

	public static final String END_POINT_NAME = "InvioClicLavoroMyPortal";

	private static final Logger log = Logger.getLogger(InvioVacancyMyPortalMessage.class.getName());

	private final int maxRedeliveries = 100;

	private String datiVacancyXml = null;
	private String usernameWs = null;
	private String passwordWs = null;
	private String statoVacancy = null;
	private String prgRichiestaAz;

	public InvioVacancyMyPortalMessage() {
	}

	public InvioVacancyMyPortalMessage(String usernameWs, String passwordWs, String statoVacancy,
			String prgRichiestaAz) {
		this.usernameWs = usernameWs;
		this.passwordWs = passwordWs;
		this.statoVacancy = statoVacancy;
		this.prgRichiestaAz = prgRichiestaAz;
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
			testata.setServizio("InvioVacancyMyPortal");
			testata.setMaxRedeliveries(maxRedeliveries);
			Serializable arrObj = msg.getObject();

		} catch (Exception ex) {
			log.fatal("Errore nella lettura dei parametri di instradamento "
					+ "e/o autenticazione.\nMessaggio malformato " + ex.getMessage());

			throw new Exception("Messaggio malformato nei dati di instradamento/autenticazione");
		}

		try {
			// ora leggo il contenuto applicativo
			Serializable arrObj = msg.getObject();
			if (arrObj instanceof ArrayList) {
				ArrayList<String> appParam = (ArrayList<String>) arrObj;
				prgRichiestaAz = (String) appParam.get(0);
				usernameWs = (String) appParam.get(1);
				passwordWs = (String) appParam.get(2);
				statoVacancy = (String) appParam.get(3);
				datiVacancyXml = (String) appParam.get(4);
			}
		} catch (Exception ex) {
			log.fatal("Errore nella lettura del contenuto applicativo. "
					+ "Il contenuto potrebbe essere completamente o parzialmente mancante " + ex.getMessage());
			// non esco dalla computazione. Accetto anche un contenuto nullo.
		}

	}

	public void send(IFaceQueue Q) throws Exception {
		ArrayList appParam = new ArrayList();

		appParam.add(prgRichiestaAz);
		appParam.add(usernameWs);
		appParam.add(passwordWs);
		appParam.add(statoVacancy);
		appParam.add(datiVacancyXml);

		super.send(Q, appParam);
	}

	public void callWebservice() throws Exception {

		ServiceParameters param = new ServiceParameters();
		param.setServizio("InvioVacancyMyPortal");
		param.put("xmlRequest", datiVacancyXml);

		RicezioneClicLavoroSILProxy proxy = new RicezioneClicLavoroSILProxy();
		EndPoint endPoint = new EndPoint();
		endPoint.init(dataSourceJndi, END_POINT_NAME);
		String address = endPoint.getUrl();
		log.debug("Endpoint address: " + address);
		proxy.setEndpoint(address);
		String stato = statoVacancy;
		try {
			String risposta = proxy.inserisciVacancy(usernameWs, passwordWs, datiVacancyXml);
			log.debug("Valore tornato dal WS myportal: " + risposta);

			if ("0".equals(risposta)) {
				if ("PA".equalsIgnoreCase(statoVacancy)) {
					stato = "PI";
				} else {
					stato = "VI";
				}
			}

		} catch (Exception e) {
			log.error("Errore nel web service di myportal", e);
		}

		/*
		 * se risposta OK setto il CODSTATOINVIOCL a PI se prima era PA o VI se era VA se risposta KO lo lascio così
		 * (chiedere a Novella)
		 * 
		 */
		int ret = updateEsitoVacancy(stato);
		log.debug("aggiornamento stato vacancy=" + ret);

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
		return datiVacancyXml;
	}

	public void setDatiRichiestaXml(String datiRichiestaXml) {
		this.datiVacancyXml = datiRichiestaXml;
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

	public String getStatoVacancy() {
		return statoVacancy;
	}

	public void setStatoVacancy(String statoVacancy) {
		this.statoVacancy = statoVacancy;
	}

	private int updateEsitoVacancy(String stato) throws AxisFault {
		String prg = null;
		if (prgRichiestaAz != null) {
			prg = prgRichiestaAz.toString();
		}
		Connection conn = null;
		ResultSet dr = null;
		Statement command = null;
		// attivo la connessione
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(dataSourceJndi);

			conn = ds.getConnection();

			String statement = "UPDATE CL_VACANCY SET CODSTATOINVIOCL = '" + stato
					+ "', NUMKLOVACANCY=NUMKLOVACANCY+1 WHERE trunc(DATSCADENZA) >= trunc(SYSDATE) AND PRGRICHIESTAAZ ="
					+ prg;
			log.info(statement);
			command = conn.createStatement();

			int ret = command.executeUpdate(statement);

			return ret;

		} catch (Exception ex) {
			log.fatal("Errore nell'aggiornamento dello stato invio vacancy", ex);
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
		serviceParam.put("prgRichiestaAz", prgRichiestaAz);
		serviceParam.put("statoCandidatura", statoVacancy);
		testata.setServizio(serviceParam.getServizio());
		setDatiRichiestaXml((String) serviceParam.get("dati"));
	}

}
