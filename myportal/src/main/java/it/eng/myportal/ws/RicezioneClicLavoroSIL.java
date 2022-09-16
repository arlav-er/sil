package it.eng.myportal.ws;

import it.eng.myportal.cliclavoro.candidatura.Candidatura;
import it.eng.myportal.cliclavoro.vacancy.Vacancy;
import it.eng.myportal.entity.ClInvioComunicazione;
import it.eng.myportal.entity.ejb.ClicLavoroEjb;
import it.eng.myportal.entity.ejb.ClicLavoroVacancyEjb;
import it.eng.myportal.entity.home.ClInvioComunicazioneHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.enums.AzioneServizio;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.exception.EmailPfPrincipalException;
import it.eng.myportal.exception.MyPortalException;

import java.util.Date;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WebService(serviceName = "RicezioneClicLavoroSIL")
public class RicezioneClicLavoroSIL {

	protected final Log log = LogFactory.getLog(this.getClass());

	@EJB
	ClicLavoroEjb clicLavoroEjb;

	@EJB
	ClicLavoroVacancyEjb clicLavoroVacancyEjb;

	@EJB
	ClInvioComunicazioneHome clInvioComunicazioneHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	WsEndpointHome wsEndpointHome;

	@WebMethod(operationName = "inserisciCandidaturaSil")
	public String inserisciCandidaturaSil(String username, String password, String xmlCandidatura) {
		try {
			checkCredenziali(username, password);
		} catch (Exception e) {
			log.error("checkUtente " + e);
			return creaMessaggio("inserisciCandidaturaSil", "500", e.getMessage());
		}

		if (xmlCandidatura == null || ("").equals(xmlCandidatura)) {
			return creaMessaggio("inserisciCandidaturaSil", "999", "Dati candidatura nulli");
		} else {
			// unmarshal dell'xml
			try {
				Candidatura candidatura = clicLavoroEjb.convertToCandidatura(xmlCandidatura);
				String codiceComunicazione = candidatura.getDatiSistema().getCodicecandidatura();

				// Gestisco questa candidatura senza passare dal timer.
				gestisciCandidaturaSincrono(xmlCandidatura, codiceComunicazione, "INVIOCANDIDATURA");
			}catch (EmailPfPrincipalException me) {
				log.error("errore nel recupero informazioni dall'xml " + me);
				return creaMessaggio("inserisciCandidaturaSil", "1", me.getMessage());
			} 
			catch (Exception e) {
				log.error("errore nel recupero informazioni dall'xml " + e);
				return creaMessaggio("inserisciCandidaturaSil", "999", e.getMessage());
			}
		}
		return creaMessaggio("inserisciCandidatura", "0", "OK");
	}

	@WebMethod(operationName = "inserisciCandidatura")
	public String inserisciCandidatura(String username, String password, String xmlCandidatura) {
		try {
			checkCredenziali(username, password);
		} catch (Exception e) {
			log.error("checkUtente " + e);
			return creaMessaggio("inserisciVacancy", "500", e.getMessage());
		}

		if (xmlCandidatura == null || ("").equals(xmlCandidatura)) {
			return creaMessaggio("inserisciCandidatura", "999", "Dati candidatura nulli");
		} else {
			// unmarshal dell'xml
			try {

				Candidatura candidatura = clicLavoroEjb.convertToCandidatura(xmlCandidatura);
				String codiceComunicazione = candidatura.getDatiSistema().getCodicecandidatura();
				// inserisco l'xml nella tabella di invio/ricezione
				setClComunicazioneInvio(xmlCandidatura, codiceComunicazione, "INVIOCANDIDATURA");

			} catch (Exception e) {
				log.error("errore nel recupero informazioni dall'xml " + e);
				return creaMessaggio("inserisciCandidatura", "999", e.getMessage());
			}
		}

		log.debug("XML da SIL -- " + xmlCandidatura + " -- ");
		return creaMessaggio("inserisciCandidatura", "0", "OK");
	}

	@WebMethod(operationName = "inserisciVacancy")
	public String inserisciVacancy(String username, String password, String xmlVacancy) {
		try {
			checkCredenziali(username, password);
		} catch (Exception e) {
			log.error("checkUtente " + e);
			return creaMessaggio("inserisciVacancy", "500", e.getMessage());
		}

		if (xmlVacancy == null || ("").equals(xmlVacancy)) {
			return creaMessaggio("inserisciVacancy", "501", "Dati vacancy nulli");
		}

		// unmarshal dell'xml
		try {
			Vacancy vac = clicLavoroVacancyEjb.convertToVacancy(xmlVacancy);
			String codiceComunicazione = vac.getDatiSistema().getCodiceofferta();
			// inserisco l'xml nella tabella di invio/ricezione
			setClComunicazioneInvio(xmlVacancy, codiceComunicazione, "INVIOVACANCY");

		} catch (Exception e) {
			log.error("errore nel recupero informazioni dall'xml " + e);
			return creaMessaggio("inserisciCandidatura", "999", e.getMessage());
		}

		log.debug("XML da SIL -- " + xmlVacancy + " -- ");
		return creaMessaggio("inserisciVacancy", "0", "OK");
	}

	/**
	 * verifica le credenziali di accesso per il WS di autenticazione
	 * 
	 * da gestire il recupero dei dati TABELLA o file di PROPERTIES
	 * 
	 * @param _login
	 * @param _pwd
	 * @throws Exception
	 */
	private void checkCredenziali(String login, String pwd) throws Exception {
		String user[] = null;
		user = wsEndpointHome.getWebServiceUser(TipoServizio.SIL_CLICLAVORO);

		String userLocal = user[0];
		String pwdLocal = user[1];

		if (!login.equals(userLocal)) {
			throw new Exception("Username o Password errati");
		}

		if (!pwd.equals(pwdLocal)) {
			throw new Exception("Username o Password errati");
		}
	}

	private String creaMessaggio(String operazione, String esito, String msg) {
		return esito;
	}

	private ClInvioComunicazione setClComunicazioneInvio(String xml, String codComunicazione, String tipoAzione) {
		ClInvioComunicazione comunicazione = new ClInvioComunicazione();
		comunicazione.setFileComunicazione(xml);
		if (("INVIOCANDIDATURA").equalsIgnoreCase(tipoAzione)) {
			comunicazione.setAzioneServizio(AzioneServizio.INVIO_CANDIDATURA);
		} else if (("INVIOVACANCY").equalsIgnoreCase(tipoAzione)) {
			comunicazione.setAzioneServizio(AzioneServizio.INVIO_VACANCY);
		}
		comunicazione.setCodComunicazione(codComunicazione);
		comunicazione.setFlagInviato(false);
		comunicazione.setDeStatoInvioCl(deStatoInvioClHome.findById("PA"));
		comunicazione.setMittente(codComunicazione.substring(0, 5));
		comunicazione.setDestinatario("MYPORTAL");

		comunicazione.setDtmIns(new Date());
		comunicazione.setDtmMod(new Date());
		comunicazione.setPfPrincipalIns(pfPrincipalHome.findById(0));
		comunicazione.setPfPrincipalMod(pfPrincipalHome.findById(0));

		clInvioComunicazioneHome.persist(comunicazione);

		return comunicazione;
	}

	/**
	 * Questo metodo gestisce l'invio di una candidatura in modo sincrono: invece di aspettare il timer, subito dopo
	 * aver inserito la riga in ClInvioComunicazione chiama anche il metodo per gestirla.
	 */
	private void gestisciCandidaturaSincrono(String xml, String codComunicazione, String tipoAzione)
			throws JAXBException {
		// Inserisco ClInvioComunicazione con flgInviato = true, in modo che il timer non possa mai beccarla.
		ClInvioComunicazione comunicazione = new ClInvioComunicazione();
		comunicazione.setFileComunicazione(xml);
		if (("INVIOCANDIDATURA").equalsIgnoreCase(tipoAzione)) {
			comunicazione.setAzioneServizio(AzioneServizio.INVIO_CANDIDATURA);
		} else if (("INVIOVACANCY").equalsIgnoreCase(tipoAzione)) {
			comunicazione.setAzioneServizio(AzioneServizio.INVIO_VACANCY);
		}
		comunicazione.setCodComunicazione(codComunicazione);
		comunicazione.setFlagInviato(true);
		comunicazione.setDeStatoInvioCl(deStatoInvioClHome.findById("PA"));
		comunicazione.setMittente(codComunicazione.substring(0, 5));
		comunicazione.setDestinatario("MYPORTAL");

		comunicazione.setDtmIns(new Date());
		comunicazione.setDtmMod(new Date());
		comunicazione.setPfPrincipalIns(pfPrincipalHome.findById(0));
		comunicazione.setPfPrincipalMod(pfPrincipalHome.findById(0));

		clInvioComunicazioneHome.persist(comunicazione);

		// Seconda parte (che di solito fa il timer): effettivo inserimento del curriculum.
		Candidatura candidatura = clicLavoroEjb.convertToCandidatura(comunicazione.getFileComunicazione());
		clicLavoroEjb.riceviCandidaturaCliclavoro(candidatura, comunicazione.getMittente());

		// Aggiorno la comunicazione
		comunicazione.setDeStatoInvioCl(deStatoInvioClHome.findById("MI"));
		clInvioComunicazioneHome.merge(comunicazione);
	}
}
