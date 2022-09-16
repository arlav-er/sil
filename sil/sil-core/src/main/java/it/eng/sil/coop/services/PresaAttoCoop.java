/*
 * Created on Aug 8, 2006
 *
 */
package it.eng.sil.coop.services;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.sil.coop.CoopApplicationException_Lavoratore;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.NotificaLavoratoreSILMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.coop.utils.CoopMessageCodes;
import it.eng.sil.coop.utils.MessageBundle;
import it.eng.sil.coop.utils.QueryExecutor;

/**
 * @author savino
 */
public class PresaAttoCoop implements IFaceService {
	private static final Logger log = Logger.getLogger(PresaAttoCoop.class.getName());
	private Connection dataConnection;
	private String dataSourceJNDI;
	// utile per test automatici (non utilizzata nell'applicazione)
	private String stato;

	// cpi di riferimento della provincia
	private String codCpiPoloLocale, desProvinciaPoloLocale, codProvinciaPoloLocale;
	private String codProvinciaPoloRemoto, desProvinciaPoloRemoto;
	// cpi dal quale il lavoratore si trasferisce, diventera' il vecchio cpi competente
	private String codCpiCompVecchio, desCpiCompVecchio;
	private String cdnLavoratore;
	private String codiceFiscale;

	// nuove informazioni del lavoratore
	private String cognomeNuovo, nomeNuovo, indirizzoDomNuovo, dataNascNuovo, desComNascNuovo, codComNascNuovo,
			codComDomNuovo, desComDomNuovo, codCpiCompNuovo, desCpiCompNuovo, dataTrasferimento/* , cdnUt */;

	private String prgPresaAtto;

	private boolean lavoratorePresenteInLocale;
	private boolean cpiCompetenteSulLavoratore;
	private boolean lavoratoreInElencoAnagrafico;
	private boolean presaAttoAutomatica;

	public void send(Message msg) throws CoopApplicationException_Lavoratore, JMSException {
		ObjectMessage message = (ObjectMessage) msg;
		Serializable arrObj = message.getObject();
		List in = (ArrayList) arrObj;

		String cdnUtente = message.getStringProperty("cdnUtente");
		String cdnGruppo = message.getStringProperty("cdnGruppo");
		String cdnProfilo = message.getStringProperty("cdnProfilo");
		String strMittente = message.getStringProperty("strMittente");
		String poloMittente = message.getStringProperty("Polomittente");
		String poloDestinatario = message.getStringProperty("Destinazione");
		codProvinciaPoloLocale = poloDestinatario;
		codProvinciaPoloRemoto = poloMittente;
		desProvinciaPoloRemoto = strMittente; // il nome della provincia

		String xmlRequest = (String) in.get(0);
		// cdnUt = poloMittente;

		SourceBean sb = null;
		String messaggioRicevuto;
		SourceBean datiLavoratore;
		try {
			SourceBean wsRequest = SourceBean.fromXMLString(xmlRequest);

			sb = (SourceBean) wsRequest.getAttribute("Provenienza");
			codCpiCompNuovo = (String) sb.getAttribute("codiceCPI");
			desCpiCompNuovo = (String) sb.getAttribute("descrizione");
			datiLavoratore = (SourceBean) wsRequest.getAttribute("servizio.dati");

			sb = (SourceBean) wsRequest.getAttribute("messaggio");
			messaggioRicevuto = (String) sb.getAttribute("testo");

			codiceFiscale = (String) datiLavoratore.getAttribute("CODICEFISCALE");
			dataTrasferimento = (String) datiLavoratore.getAttribute("DATATRASF");
			codComDomNuovo = (String) datiLavoratore.getAttribute("CODCOMUNEDOM");
			desComDomNuovo = (String) datiLavoratore.getAttribute("comunedomicilio");
			indirizzoDomNuovo = (String) datiLavoratore.getAttribute("INDIRIZZODOM");
			cognomeNuovo = (String) datiLavoratore.getAttribute("cognome");
			nomeNuovo = (String) datiLavoratore.getAttribute("nome");
			desComNascNuovo = (String) datiLavoratore.getAttribute("comunenascita");
			codComNascNuovo = (String) datiLavoratore.getAttribute("codcomnasc");
			dataNascNuovo = (String) datiLavoratore.getAttribute("datanascita");

			dataConnection = getConnection();
			// avvio la transazione
			dataConnection.setAutoCommit(false);
			// 1. lettura info base per iniziare
			leggiInfoBase();
			// 2. leggo il cdnlavoratore se esiste
			// cercaLavoratore();
			if (this.lavoratorePresenteInLocale && this.lavoratoreInElencoAnagrafico) {
				// come prima cosa inserisco il messaggio ricevuto come evidenza
				inserisciEvidenzaInLocale(messaggioRicevuto);
				if (this.cpiCompetenteSulLavoratore) {
					// inserimento richiesta in ca_prea_atto
					inserisciRichiesta();
					if (presaAttoAutomatica()) {
						// delego ad un'altra classe il compito di eseguire tutte le operazioni della presa atto
						// automatica
						SourceBean request = new SourceBean("SERVICE_REQUEST");
						SourceBean response = new SourceBean("SERVICE_RESPONSE");
						// ------------------------------
						// per prima cosa inizializzo la request con i dati ricevuti dal servizio
						request.setBean(datiLavoratore);
						// N.B questa istruzione cancella tutti gli attributi di request e li sostituisce con quelli di
						// datiLavoratore
						// mantenendo il name del SourceBean (SERVICE_REQUEST). Si tratta di una specie di
						// cloneObject().
						// -------------------------------
						TestataMessageTO testataPoloTrasferimento = new TestataMessageTO();
						testataPoloTrasferimento.setCdnGruppo(cdnGruppo);
						testataPoloTrasferimento.setCdnProfilo(cdnProfilo);
						testataPoloTrasferimento.setCdnUtente(cdnUtente);
						testataPoloTrasferimento.setDestinazione(null);
						testataPoloTrasferimento.setPoloMittente(cdnUtente);
						testataPoloTrasferimento.setStrMittente(desProvinciaPoloRemoto);
						testataPoloTrasferimento.setServizio(null);

						// reperisco le informazioni dal db
						TestataMessageTO testataPoloPresaAtto = new TestataMessageTO();
						testataPoloPresaAtto.setCdnUtente(codProvinciaPoloLocale);
						testataPoloPresaAtto.setPoloMittente(codProvinciaPoloLocale);
						testataPoloPresaAtto.setStrMittente(desProvinciaPoloLocale);
						// NOTA 11/09/2006 Savino: qual e' il profilo associato all'utente di cooperazione? NESSUNO PER
						// ORA
						// testataPoloPresaAtto.setCdnGruppo("0");
						// testataPoloPresaAtto.setCdnProfilo("0");

						request.setAttribute("POLO_PRESA_ATTO", testataPoloPresaAtto);
						request.setAttribute("POLO_TRASFERIMENTO", testataPoloTrasferimento);

						request.setAttribute("CPI_PRESA_ATTO.codcpi", codCpiCompVecchio);
						request.setAttribute("CPI_PRESA_ATTO.descrizione", desCpiCompVecchio);
						request.setAttribute("CPI_TRASFERIMENTO.codcpi", codCpiCompNuovo);
						request.setAttribute("CPI_TRASFERIMENTO.descrizione", desCpiCompNuovo);

						// la chiamata a setBean cancella tutti gli attributi presenti
						/*
						 * request.setAttribute("cdnutente", cdnUtente); request.setAttribute("cdnGruppo", cdnGruppo);
						 * request.setAttribute("cdnProfilo", cdnProfilo); request.setAttribute("strMittente",
						 * strMittente); request.setAttribute("PoloMittente", poloMittente);
						 * request.setAttribute("Destinazione", poloDestinatario);
						 */
						request.setAttribute("codCpiTitNuovo", codCpiCompNuovo);
						request.setAttribute("cdnut", "190");
						request.setAttribute("prgPresaAtto", prgPresaAtto);
						// request.setAttribute("descCpiLocale", this.descCpiLocale);
						// request.setAttribute("codCpiLocale", this.codCpiPoloLocale);

						EseguiPresaAttoAutomatica eseguiPAA = new EseguiPresaAttoAutomatica();
						eseguiPAA.setConnection(dataConnection);
						eseguiPAA.setDataSourceJNDI(dataSourceJNDI);
						// in caso di errore viene lanciata una eccezione (nella response non viene inserito niente)
						eseguiPAA.service(request, response);
					} else {
						/*
						 * String msgEvidenza = "La richiesta di presa atto pervenuta dal centro per l'impiego di "
						 * +desCpiCompNuovo+" deve essere gestita manualmente in quanto" +
						 * " non e' attiva la presa atto automatica.";
						 */
						Vector v = new Vector();
						v.add(desCpiCompNuovo);
						String msgEvidenza = MessageBundle
								.getMessage(CoopMessageCodes.NOTIFICHE_COOP.PRESA_ATTO_COOP_NON_ATTIVA, v);
						inserisciEvidenzaInLocale(msgEvidenza);
					}
				}
			}
			if (esistonoNotificheDaInviare())
				inviaNotificaPresaAttoNonPossibile();

			dataConnection.commit();
		} catch (Exception ex) {
			if (dataConnection != null)
				try {
					dataConnection.rollback();
				} catch (SQLException e) {
					log.error("Eccezione nella rollback", e);
				}
			log.error("Presa atto in cooperazione ", ex);
			stato = ex.getMessage();
			// inviare una notifica di errore al polo mittente
			Vector param = new Vector(5);
			param.add(desProvinciaPoloLocale);
			param.add(cognomeNuovo);
			param.add(nomeNuovo);
			param.add(codiceFiscale);
			// param.add(codCpiCompVecchio + " : " + desCpiCompVecchio);
			param.add(codCpiCompNuovo + " : " + desCpiCompNuovo);

			throw new CoopApplicationException_Lavoratore(CoopMessageCodes.NOTIFICHE_COOP.PRESA_ATTO_TRASFERIMENTO_FAIL,
					param, codiceFiscale);
		} finally {
			if (dataConnection != null)
				try {
					dataConnection.close();
				} catch (Exception ex) {
					log.error("Eccezione nella chiusura della connessione al db ", ex);
				}
		}
	}

	/**
	 * @param messaggio
	 */
	private void inserisciEvidenzaInLocale(String messaggio) throws SQLException {
		CallableStatement command = null;
		try {
			String statement = "{ call ?:=PG_COOP.putNotificaLavoratore(?,?,?) }";
			command = dataConnection.prepareCall(statement);
			command.registerOutParameter(1, Types.TINYINT);
			command.setString(2, codiceFiscale);
			command.setString(3, messaggio);
			command.setString(4, "190");
			command.execute();
			// se si verifica un errore viene lanciata una eccezione
			// la funzione ritorna sempre 0!!
			// codiceRit=command.getInt(1);
		} finally {
			if (command != null)
				command.close();
		}
	}

	private boolean esistonoNotificheDaInviare() {
		return !(lavoratorePresenteInLocale && cpiCompetenteSulLavoratore && lavoratoreInElencoAnagrafico);
	}

	private void inviaNotificaPresaAttoNonPossibile() throws Exception {
		/*
		 * String contenutoMsg="La richiesta di presa atto pervenuta al c" +
		 * "entro per l'impiego "+codCpiPoloLocale+" di "+desProvinciaPoloLocale+" " +
		 * " relativa al lavoratore "+cognomeNuovo+" "+ nomeNuovo + " nato a " +desComNascNuovo + " il " +
		 * dataNascNuovo+ " domiciliato in " + indirizzoDomNuovo + " nel comune di " + desComDomNuovo +
		 * " in data (trasferimento) " + dataTrasferimento + " " + "non puo' essere elaborata in quanto ";
		 */
		Vector v = new Vector(7);
		v.add(codCpiPoloLocale);
		v.add(desProvinciaPoloLocale);
		v.add(cognomeNuovo + " " + nomeNuovo);
		v.add(desComNascNuovo);
		v.add(dataNascNuovo);
		v.add(indirizzoDomNuovo);
		v.add(desComDomNuovo);
		v.add(dataTrasferimento);

		int codiceMessaggio = CoopMessageCodes.NOTIFICHE_COOP.PRESA_ATTO_LAV_NON_PRESENTE;
		if (lavoratorePresenteInLocale && lavoratoreInElencoAnagrafico && !cpiCompetenteSulLavoratore)
			codiceMessaggio = CoopMessageCodes.NOTIFICHE_COOP.PRESA_ATTO_CPI_NON_COMPETENTE;
		if (lavoratorePresenteInLocale && !lavoratoreInElencoAnagrafico)
			codiceMessaggio = CoopMessageCodes.NOTIFICHE_COOP.PRESA_ATTO_LAV_NON_IN_ELENCO_ANAG;
		String messaggioNotifica = MessageBundle.getMessage(codiceMessaggio, v);
		TestataMessageTO testataMessaggio = new TestataMessageTO();
		testataMessaggio.setDestinazione(codProvinciaPoloRemoto);
		testataMessaggio.setPoloMittente(codProvinciaPoloLocale);
		NotificaLavoratoreSILMessage notificaLavoratoreSILMessage = new NotificaLavoratoreSILMessage();
		notificaLavoratoreSILMessage.setTestata(testataMessaggio);
		notificaLavoratoreSILMessage.setDataSourceJndi(dataSourceJNDI);
		notificaLavoratoreSILMessage.setCodiceFiscale(codiceFiscale);
		notificaLavoratoreSILMessage.setContenutoMessaggio(messaggioNotifica);
		OutQ outQ = new OutQ();
		notificaLavoratoreSILMessage.send(outQ);
	}

	/**
	 * in CA_PRESA_ATTO
	 */
	private void inserisciRichiesta() throws Exception {
		QueryExecutor qExec = new QueryExecutor(dataConnection);
		String query = "select to_char(S_CA_PRESA_ATTO.NEXTVAL) as nextval from dual";
		SourceBean prg = qExec.executeQuery(query, null);
		prgPresaAtto = (String) prg.getAttribute("row.nextval");
		query = "INSERT INTO CA_PRESA_ATTO " + "( " + "PRGPRESAATTO    , CDNLAVORATORE    , "
				+ "STRCODICEFISCALE, STRCOGNOME       , STRNOME, "
				+ "DATNASC         , CODCOMNASC       , STRCOMUNENASC, DATTRASFERIMENTO , "
				+ "CODCPIRICH      , CODSTATOPRESAATTO, " + "CODCOMDOM, STRCOMUNEDOM, STRINDIRIZZODOM,"
				+ "CDNUTINS, DTMINS, CDNUTMOD, DTMMOD" + " ) VALUES ( "
				+ "?, (SELECT L.CDNLAVORATORE FROM AN_LAVORATORE L WHERE L.STRCODICEFISCALE=UPPER(?)), "
				+ "?, UPPER(?), UPPER(?), " + "TO_DATE( ?, 'DD/MM/YYYY'),  ?, ?, TO_DATE(?, 'DD/MM/YYYY'), "
				+ "?, ?, ?, ?, ?," + "190, SYSDATE, 190, SYSDATE)";

		String[] params = new String[] { prgPresaAtto, codiceFiscale, codiceFiscale, cognomeNuovo, nomeNuovo,
				dataNascNuovo, codComNascNuovo, desComNascNuovo, dataTrasferimento, codCpiCompNuovo, "AT",
				codComDomNuovo, desComDomNuovo, indirizzoDomNuovo };
		if (qExec.executeUpdate(query, params) != 1)
			throw new Exception("inserimento richiesta presa atto fallita");
	}

	private boolean presaAttoAutomatica() {
		String coopAttiva = System.getProperty("cooperazione.presaAtto.enabled");
		this.presaAttoAutomatica = ((coopAttiva != null) && (coopAttiva.equalsIgnoreCase("true")));
		// this.presaAttoAutomatica=false;
		return this.presaAttoAutomatica;
	}

	/*
	 * private boolean cercaLavoratore() throws Exception { QueryExecutor qExec = new QueryExecutor(dataConnection);
	 * String query =
	 * "select to_char(cdnlavoratore) as cdnLavoratore from an_lavoratore where strcodicefiscale = upper(?)"; String[]
	 * params = new String[]{codiceFiscale}; SourceBean row = qExec.executeQuery(query, params); if (row !=null &&
	 * row.getAttribute("row.cdnLavoratore")!=null) { this.lavoratorePresenteInLocale=true; cdnLavoratore =
	 * (String)row.getAttribute("row.cdnLavoratore"); } else this.lavoratorePresenteInLocale=false; return
	 * this.lavoratorePresenteInLocale; }
	 */
	/**
	 * legge il codcpi del polo locale e la sua descrizione, la descrizione del nuovo comune di domicilio, l'attuale
	 * codcpicomp registrato nel db locale e che diventera' il vecchio cpi comp. Imposta le proprieta'
	 * lavoratorePresenteInLocale e cpiCompetenteSulLavoratore
	 * 
	 * @throws Exception
	 *             in caso di errore
	 */
	private void leggiInfoBase() throws Exception {
		SourceBean row = null;
		QueryExecutor qExec = new QueryExecutor(dataConnection);
		String query = "select de_provincia.codcpicapoluogo, de_provincia.strdenominazione "
				+ " from ts_generale, de_provincia where codprovinciasil = codprovincia";

		row = qExec.executeQuery(query, null);
		if (row != null && row.getAttribute("row.codcpicapoluogo") != null) {
			codCpiPoloLocale = (String) row.getAttribute("row.codcpicapoluogo");
			desProvinciaPoloLocale = (String) row.getAttribute("row.strdenominazione");
		} else
			throw new Exception("Impossibile leggere le informazioni di base del cpi");

		query = "select inf.codcpitit, de_cpi.STRDESCRIZIONE, inf.codmonotipocpi , ea.DATINIZIO                            "
				+ "  from an_lav_storia_inf inf inner join  an_lavoratore lav on (inf.cdnlavoratore = lav.cdnlavoratore)     "
				+ "       inner join de_cpi on (inf.codcpitit = de_cpi.CODCPI)                                               "
				+ "       left join  am_elenco_anagrafico ea on (lav.cdnlavoratore = ea.cdnlavoratore and ea.datcan is null) "
				+ " where lav.strcodicefiscale = ? and inf.datfine is null ";
		String[] params = new String[] { codiceFiscale };
		row = qExec.executeQuery(query, params);
		String codMonoTipoCpi = null;
		if (row != null && row.getAttribute("row.codcpitit") != null) {
			codCpiCompVecchio = (String) row.getAttribute("row.codcpitit");
			desCpiCompVecchio = (String) row.getAttribute("row.strdescrizione");
			codMonoTipoCpi = (String) row.getAttribute("row.codMonoTipoCpi");
			this.lavoratorePresenteInLocale = true;
			this.cpiCompetenteSulLavoratore = "C".equals(codMonoTipoCpi);
			this.lavoratoreInElencoAnagrafico = row.getAttribute("row.datinizio") != null;
		} else { // throw new Exception("Impossibile leggere le informazioni di base del lavoratore");
			this.lavoratorePresenteInLocale = false;
		}

		/*
		 * non e' piu' necessaria perche' l'informazione viene ora passata dal polo mittente
		 * 
		 * query = "select de_comune.strdenominazione from de_comune where de_comune.codcom = ?"; params = new
		 * String[]{codComDomNuovo}; row = qExec.executeQuery(query, params); if (row !=null &&
		 * row.getAttribute("row.strdenominazione")!=null) { desComDomNuovo =
		 * (String)row.getAttribute("row.strdenominazione"); } else throw new
		 * Exception("Impossibile leggere la descrizione del comune del nuovo domicilio");
		 */
	}

	/*
	 * private boolean cpiCompetenteSulLavoratore() throws Exception { String query =
	 * "select decode (codmonotipocpi, 'C', 'SI', 'NO') as competente " + "from an_lav_storia_inf, an_lavoratore " +
	 * "where an_lav_storia_inf.cdnlavoratore = an_lavoratore.cdnlavoratore " +
	 * "and an_lav_storia_inf.datfine is null and an_lavoratore.strcodicefiscale=upper(?)"; String[] params = new
	 * String[]{codiceFiscale}; QueryExecutor qExec = new QueryExecutor(dataConnection); SourceBean row =
	 * qExec.executeQuery(query, params); this.cpiCompetenteSulLavoratore = (row!=null &&
	 * "SI".equals(row.getAttribute("row.competente"))); return this.cpiCompetenteSulLavoratore; }
	 */
	public Connection getConnection() throws EMFInternalError {
		// EjbDbConnection ejbDbConnection = new EjbDbConnection();
		// dataSourceJNDI = ejbDbConnection.getDataSourceJndi();
		// TODO Savino: NEW DB_ACCESS
		dataSourceJNDI = new DataSourceJNDI().getJndi();
		return DataConnectionManager.getInstance().getConnection().getInternalConnection();
		// return ejbDbConnection.getConnection();
	}

	public String getStato() {
		return stato;
	}
}
