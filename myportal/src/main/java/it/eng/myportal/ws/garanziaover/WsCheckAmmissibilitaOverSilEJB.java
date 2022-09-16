package it.eng.myportal.ws.garanziaover;

import it.eng.myportal.dtos.YgGaranziaOverDTO;
import it.eng.myportal.entity.WsEndpoint;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.YgGaranziaOverHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.sil.coop.webservices.requisitiAdesione.VerificaRequisitiGaranziaOverProxy;
import it.eng.sil.coop.webservices.requisitiAdesione.VerificaRequisitiGaranziaOverRequest;
import it.eng.sil.coop.webservices.requisitiAdesione.VerificaRequisitiGaranziaOverResponse;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for WS Import Anagrafica from SIL.
 * 
 */
@Stateless
@LocalBean
public class WsCheckAmmissibilitaOverSilEJB {
	private static final Log log = LogFactory.getLog(WsCheckAmmissibilitaOverSilEJB.class);

	@PersistenceContext
	private EntityManager entityManager;

	@EJB
	private WsEndpointHome wsEndpointHome;

	@EJB
	private YgGaranziaOverHome ygGaranziaOverHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	/**
	 * Invoca il servizio VerificaRequisitiGaranziaOver. Prima lo chiama verso l'endpoint legato alla provincia di
	 * riferimento dell'utente. Se questa chiamata dà esito negativo, lo chiama verso tutti gli altri endpoint legati
	 * alle province della regione in cui sta operando il sistema. Se almeno una chiamata restituisce esito positivo,
	 * quell'esito viene "tenuto" come buono. Altrimenti viene restituito l'esito negativo della prima chiamata.
	 * 
	 */
	public VerificaRequisitiGaranziaOverResponse checkAmmissibilita(YgGaranziaOverDTO adesione, String codiceFiscale,
			Date dataAdesione, Boolean flagAmmortizzatori, String codProvinciaRiferimento)
			throws DatatypeConfigurationException {
		VerificaRequisitiGaranziaOverRequest request = new VerificaRequisitiGaranziaOverRequest();

		// Creo la richiesta.
		GregorianCalendar dataAdesioneCal = new GregorianCalendar();
		dataAdesioneCal.setTime(dataAdesione);
		request.setCodiceFiscale(codiceFiscale);
		request.setDataAdesione(dataAdesioneCal);
		request.setPercettoreAmmortizzatori(flagAmmortizzatori ? "S" : "N");

		// Per prima cosa, provo a chiamare questo servizio nel SIL della provincia di riferimento dell'utente.
		VerificaRequisitiGaranziaOverResponse firstResponse = null;
		try {
			WsEndpoint provRiferimentoEndpoint = wsEndpointHome.findByTipoServizioAndProvincia(
					TipoServizio.CHECK_GARANZIA_OVER, codProvinciaRiferimento);
			firstResponse = callWebservice(request, provRiferimentoEndpoint);
			if (firstResponse.getCodice() == 0) {
				// La prima chiamata ha avuto successo, il metodo termina qui.
				adesione.setDeProvincia(deProvinciaHome.findDTOById(codProvinciaRiferimento));
				ygGaranziaOverHome.mergeDTO(adesione, adesione.getIdPrincipalIns());
				return firstResponse;
			}
		} catch (Exception e) {
			log.warn("Errore durante la chiamata a CheckGaranziaOver per la provincia " + codProvinciaRiferimento
					+ ": " + e);
		}

		// Prendo la lista di tutti gli endpoint della regione del sistema.
		List<WsEndpoint> altriEndpoint = wsEndpointHome.findByTipoServizioAndRegione(TipoServizio.CHECK_GARANZIA_OVER,
				ConstantsSingleton.COD_REGIONE.toString());
		for (WsEndpoint altroEndpoint : altriEndpoint) {
			if (!altroEndpoint.getDeProvincia().getCodProvincia().equals(codProvinciaRiferimento)) {
				// Provo tutti gli endpoint della regione che NON siano quello della prov. di riferimento.
				try {
					VerificaRequisitiGaranziaOverResponse altraResponse = callWebservice(request, altroEndpoint);
					if (altraResponse.getCodice() == 0) {
						// Se una chiamata ha esito positivo, interrompo tutto e rispondo subito.
						adesione.setDeProvincia(deProvinciaHome.findDTOById(altroEndpoint.getDeProvincia()
								.getCodProvincia()));
						adesione.setDeCpiAdesione(null);
						ygGaranziaOverHome.mergeDTO(adesione, adesione.getIdPrincipalIns());
						return altraResponse;
					}
				} catch (Exception e) {
					log.warn("Errore durante la chiamata a CheckGaranziaOver per la provincia "
							+ altroEndpoint.getDeProvincia().getCodProvincia() + ": " + e);
				}
			}
		}

		// Se non ho trovato neanche un endpoint che mi dia esito positivo, restituisco la prima risposta fallita.
		return firstResponse;
	}

	/**
	 * Fa una singola chiamata ad un endpoint chiamando il servizio VerificaRequisitiGaranziaOver.
	 */
	private VerificaRequisitiGaranziaOverResponse callWebservice(VerificaRequisitiGaranziaOverRequest request,
			WsEndpoint endpoint) throws RemoteException {
		VerificaRequisitiGaranziaOverProxy service = new VerificaRequisitiGaranziaOverProxy();
		service.setEndpoint(endpoint.getAddress());

		log.info("Inizio chiamata verso endpoint: ");
		return service.getVerificaRequisitiGaranziaOver(request);
	}
}
