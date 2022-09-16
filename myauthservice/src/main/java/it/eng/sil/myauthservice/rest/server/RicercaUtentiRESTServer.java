package it.eng.sil.myauthservice.rest.server;

import it.eng.sil.base.exceptions.ProfilaturaException;
import it.eng.sil.base.pojo.auth.BasicInfoPOJO;
import it.eng.sil.myauthservice.exception.ProfilaturaMissingParameterException;
import it.eng.sil.myauthservice.exception.ProfilaturaNoResultException;
import it.eng.sil.mycas.model.business.RicercaUtentiClientLocalEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpGruppoEJB;
import it.eng.sil.mycas.model.manager.profilatura.PfPrincipalEJB;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Questa classe espone i WS di profilatura unica ragion d'essere di questo .war, per il resto semplice wrapper di
 * myauth-commons
 * 
 * @author pegoraro
 *
 */
@Path("/usersearch")
public class RicercaUtentiRESTServer {
	protected static Logger log = Logger.getLogger(RicercaUtentiRESTServer.class.getName());

	@Inject
	RicercaUtentiClientLocalEJB ricercaUtentiClientLocalEJB;

	@Inject
	PfPrincipalEJB pfPrincipalHome;

	@Inject
	GpGruppoEJB gpGruppoEJB;

	@Inject
	DeComuneEJB deComuneEJB;

	@GET
	@Produces("application/json; charset=UTF-8")
	public String getList() {

		return "Servizio di ricerca cittadini e aziende";
	}

	@GET
	@Path("cittadino")
	@Produces("application/json; charset=UTF-8")
	public List<BasicInfoPOJO> searchUtenteCittadino(@QueryParam("query") String queryStr,
			@QueryParam("limit") Integer limit) throws ProfilaturaException {
		if (queryStr == null || !(queryStr.length() > 0)) {
			throw new ProfilaturaMissingParameterException(
					"Questo WS richiede il parametro query (hint per l'utente - cognome o CF)");
		}

		try {
			String decodedQuery = URLDecoder.decode(queryStr, "UTF-8");
			return ricercaUtentiClientLocalEJB.searchCittadino(decodedQuery, limit);
		} catch (UnsupportedEncodingException e) {
			throw new ProfilaturaException("Errore searchUtenteCittadino, errore decoding query: " + e.toString());
		} catch (ProfilaturaException e) {
			throw new ProfilaturaNoResultException("Profilo non trovato per utente: " + queryStr);
		}

	}

	@GET
	@Path("azienda")
	@Produces("application/json; charset=UTF-8")
	public List<BasicInfoPOJO> searchUtenteAzienda(@QueryParam("query") String queryStr,
			@QueryParam("limit") Integer limit) throws ProfilaturaException {
		if (queryStr == null || !(queryStr.length() > 0)) {
			throw new ProfilaturaMissingParameterException(
					"Questo WS richiede il parametro query (hint per l'azienda - Ragione sociale o CF)");
		}

		try {
			String decodedQuery = URLDecoder.decode(queryStr, "UTF-8");
			return ricercaUtentiClientLocalEJB.searchAzienda(decodedQuery, limit);
		} catch (UnsupportedEncodingException e) {
			throw new ProfilaturaException("Errore searchUtenteAzienda, errore decoding query: " + e.toString());
		} catch (ProfilaturaException e) {
			throw new ProfilaturaNoResultException("Profilo non trovato per utente: " + queryStr);
		}

	}

	@GET
	@Path("utenti")
	@Produces("application/json; charset=UTF-8")
	public List<BasicInfoPOJO> searchUtente(@QueryParam("query") String queryStr, @QueryParam("limit") Integer limit)
			throws ProfilaturaException {
		if (queryStr == null || !(queryStr.length() > 0)) {
			throw new ProfilaturaMissingParameterException(
					"Questo WS richiede il parametro query (hint per azienda/cittadino - Cognome, Ragione sociale o CF)");
		}

		try {
			return ricercaUtentiClientLocalEJB.searchUtente(queryStr, limit);
		} catch (ProfilaturaException e) {
			throw new ProfilaturaNoResultException("Profilo non trovato per query: " + queryStr);
		}

	}
}
