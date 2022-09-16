package it.eng.myportal.ws;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.cliclavoro.candidatura.Candidatura;
import it.eng.myportal.cliclavoro.messaggio.Messaggio;
import it.eng.myportal.cliclavoro.vacancy.Vacancy;
import it.eng.myportal.entity.ejb.ClicLavoroEjb;
import it.eng.myportal.entity.ejb.ClicLavoroMessaggioEjb;
import it.eng.myportal.entity.ejb.ClicLavoroVacancyEjb;
import it.eng.myportal.entity.home.ClInvioComunicazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.gov.lavoro.servizi.cliclavoro.types.RichiestaInvioCandidaturaType;
import it.gov.lavoro.servizi.cliclavoro.types.RichiestaInvioMessaggioType;
import it.gov.lavoro.servizi.cliclavoro.types.RichiestaInvioVacancyType;
import it.gov.lavoro.servizi.cliclavoro.types.RispostaInvioCandidaturaType;
import it.gov.lavoro.servizi.cliclavoro.types.RispostaInvioMessaggioType;
import it.gov.lavoro.servizi.cliclavoro.types.RispostaInvioVacancyType;

/**
 * il portale espone dei servizi che vengono invocati da anpal dovrebbero inserire un record sulla tabella
 * cl_comunicazione e inseriscono l'xml che cliclavoro(anpal) ci manda invocando clicLavoroVacancyEjb.setClComunicazioneInvioFromPDD
 * 
 * poi i nostri timer ogni 10 minuti partono e recupearno questi xml che cliclavoro ci manda
 * 
 * @author Ale
 */
@org.apache.cxf.interceptor.InInterceptors(interceptors = { "it.eng.myportal.ws.interceptor.PDDBasicAuthInterceptor" })
@WebService(serviceName = "NotificheClicLavoro")
public class NotificheClicLavoro implements ICliclavoroWS {
	protected final Log logger = LogFactory.getLog(this.getClass());

	@PersistenceContext
	protected EntityManager entityManager;

	@EJB
	ClicLavoroEjb clicLavoroEjb;

	@EJB
	ClicLavoroVacancyEjb clicLavoroVacancyEjb;

	@EJB
	ClInvioComunicazioneHome clInvioComunicazioneHome;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	ClicLavoroMessaggioEjb clicLavoroMessaggioEjb;

	/**
	 * inserisce i dati della vacancy XML sul DB di MyPortal
	 * 
	 * 
	 * @param datiXml
	 * @throws Exception
	 */
	private void invioVacancy(String datiXml) {
		Vacancy vac = null;
		try {
			vac = clicLavoroVacancyEjb.convertToVacancy(datiXml);
		} catch (JAXBException e) {
			logger.error("Errore conversione della richiesta dalla pdd", e);
			clicLavoroVacancyEjb.setClComunicazioneInvioFromPDD(datiXml, "0", "INVIOVACANCY");
		}
		String codiceComunicazione = vac.getDatiSistema().getCodiceofferta();
		// inserisco l'xml nella tabella di invio/ricezione
		clicLavoroVacancyEjb.setClComunicazioneInvioFromPDD(datiXml, codiceComunicazione, "INVIOVACANCY");
	}

	/**
	 * inserisce i dati della vacancy XML sul DB di MyPortal
	 * 
	 * 
	 * @param datiXml
	 * @throws Exception
	 */
	private void invioCandidatura(String datiXml) {
		Candidatura candidatura = null;
		try {
			candidatura = clicLavoroEjb.convertToCandidatura(datiXml);
		} catch (JAXBException e) {
			logger.error("Errore conversione della richiesta dalla pdd", e);
			clicLavoroVacancyEjb.setClComunicazioneInvioFromPDD(datiXml, "0", "INVIOCANDIDATURA");
		}
		String codiceComunicazione = candidatura.getDatiSistema().getCodicecandidatura();
		// inserisco l'xml nella tabella di invio/ricezione
		clicLavoroVacancyEjb.setClComunicazioneInvioFromPDD(datiXml, codiceComunicazione, "INVIOCANDIDATURA");
	}

	/**
	 * inserisce i dati della vacancy XML sul DB di MyPortal
	 * 
	 * 
	 * @param datiXml
	 * @throws Exception
	 */
	private void invioMessaggio(String datiXml) {
		Messaggio msg = null;
		try {
			msg = clicLavoroMessaggioEjb.convertToMessaggio(datiXml);
		} catch (JAXBException e) {
			logger.error("Errore conversione della richiesta dalla pdd", e);
			clicLavoroVacancyEjb.setClComunicazioneInvioFromPDD(datiXml, "0", "INVIOMESSAGGIO");
		}
		String codiceComunicazione = msg.getDatiSistema().getCodicemessaggio();
		clicLavoroVacancyEjb.setClComunicazioneInvioFromPDD(datiXml, codiceComunicazione, "INVIOMESSAGGIO");
	}

	@Override
	public RispostaInvioCandidaturaType invioCandidatura(RichiestaInvioCandidaturaType parameters) {
		String comunicazioneXML = parameters.getCandidaturaXML();
		RispostaInvioCandidaturaType risposta = new RispostaInvioCandidaturaType();
		try {
			logger.info("Candidatura ricevuta");
			invioCandidatura(comunicazioneXML);
			risposta.setTipoRisposta("OK");
			risposta.setDescrEsito("Candidatura ricevuta correttamente");
		} catch (Exception e) {
			logger.error("Impossibile elaborare la richiesta dalla pdd", e);
			risposta.setTipoRisposta("KO");
			risposta.setDescrEsito("Errore nella ricezione della Candidatura");
		}
		return risposta;
	}

	@Override
	public RispostaInvioMessaggioType invioMessaggio(RichiestaInvioMessaggioType parameters) {
		String comunicazioneXML = parameters.getMessaggioXML();
		RispostaInvioMessaggioType risposta = new RispostaInvioMessaggioType();
		try {
			logger.info("Messaggio ricevuto");
			invioMessaggio(comunicazioneXML);
			risposta.setTipoRisposta("OK");
			risposta.setDescrEsito("Messaggio ricevuto correttamente");
		} catch (Exception e) {
			logger.error("Impossibile elaborare la richiesta dalla pdd", e);
			risposta.setTipoRisposta("KO");
			risposta.setDescrEsito("Errore nella ricezione del Messaggio");
		}
		return risposta;
	}

	@Override
	public RispostaInvioVacancyType invioVacancy(RichiestaInvioVacancyType parameters) {

		RispostaInvioVacancyType risposta = new RispostaInvioVacancyType();
		String comunicazioneXML = parameters.getVacancyXML();
		try {
			logger.info("Offerta ricevuta");
			invioVacancy(comunicazioneXML);
			risposta.setTipoRisposta("OK");
			risposta.setDescrEsito("Offerta ricevuta correttamente");
		} catch (Exception e) {
			logger.error("Impossibile elaborare la richiesta dalla pdd", e);
			risposta.setTipoRisposta("KO");
			risposta.setDescrEsito("Errore nella ricezione della Offerta");
		}
		return risposta;
	}
}
