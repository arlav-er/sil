package it.eng.sil.myauthservice.rest.server;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import it.eng.sil.base.enums.DeSistemaEnum;
import it.eng.sil.base.exceptions.GamificationDisabledException;
import it.eng.sil.base.exceptions.GamificationException;
import it.eng.sil.base.exceptions.GamificationRemoteException;
import it.eng.sil.base.pojo.gamification.DeBadgePojo;
import it.eng.sil.base.pojo.gamification.GpBadgePojo;
import it.eng.sil.myauthservice.model.ConstantsSingleton;
import it.eng.sil.mycas.model.business.gamification.GamificationEJBLocalClientImpl;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.profilatura.PfPrincipalEJB;

/**
 * Questa classe espone i WS di gamification.
 * La gamification si basa sul badge (medaglia) che gli utenti conseguono in seguito a determinate azioni 
 * svolte sui moduli. Con queste chiamate si "assegnano" nuovi badge o si ottiene la lista dei conseguiti
 * da MyAuthService
 * 
 * @author pegoraro
 *
 */
@Path("/gamification")
public class GamificationRESTServer {
	protected static Logger log = Logger.getLogger(GamificationRESTServer.class.getName());

	@Inject
	PfPrincipalEJB pfPrincipalHome;

	@Inject
	DeComuneEJB deComuneEJB;

	@Inject
	GamificationEJBLocalClientImpl badgeEJBLocalImpl;

	@Inject
	ConstantsSingleton constantSingleton;

	@GET
	@Path("getUserBadgeList")
	@Produces("application/json; charset=UTF-8")
	public List<GpBadgePojo> getUserBadgeList(@QueryParam("idPfPrincipal") String idPfPrincipal,
			@QueryParam("codSistema") String codSistema) throws GamificationException {
		Integer work = null;
		DeSistemaEnum sistemaEnum = null;

		if (!isGamificationEnabled()) {
			throw new GamificationDisabledException(
					"ERRORE getUserBadgeList: la gamification è DISABILITATA su MyAuth!");
		}

		if (idPfPrincipal == null || !(idPfPrincipal.length() > 0))
			throw new GamificationRemoteException("ERRORE getUserBadgeList: Parametro idPfPrincipal nullo");

		try {
			work = Integer.parseInt(idPfPrincipal);
		} catch (NumberFormatException e) {
			throw new GamificationRemoteException("ERRORE getUserBadgeList: idPfPrincipal deve essere un numero intero");
		}

		if (codSistema != null) {
			try {
				sistemaEnum = DeSistemaEnum.valueOf(codSistema);
			} catch (IllegalArgumentException e) {
				throw new GamificationRemoteException("ERRORE getUserBadgeList: codSistema non Tovato!");
			}
		}

		return badgeEJBLocalImpl.retrieveUserBadges(sistemaEnum, work, null);
	}

	@GET
	@Path("assignBadgeTest")
	@Produces("application/json; charset=UTF-8")
	public GpBadgePojo assignBadgeTest(@QueryParam("codBadge") String codBadge,
			@QueryParam("idPfPrincipal") Integer idPfPrincipal, @QueryParam("value") Double value)
			throws GamificationException {

		if (!isGamificationEnabled()) {
			throw new GamificationDisabledException("ERRORE assignBadgeTest: la gamification è DISABILITATA su MyAuth!");
		}

		if (codBadge == null || !(codBadge.length() > 0))
			throw new GamificationRemoteException("Errore in assignBadgeTest: Parametro codBadge nullo");

		if (idPfPrincipal == null)
			throw new GamificationRemoteException("Errore in assignBadgeTest: Parametro idPfPrincipal nullo");

		if (value == null)
			throw new GamificationRemoteException("Errore in assignBadgeTest: Parametro value nullo");

		throw new GamificationRemoteException(
				"Metodo assignBadgeTest invocato in modalità GET con parametri idPfPrincipal: " + idPfPrincipal
						+ ", codBadge: " + codBadge + ", value: " + value
						+ ". \n \n Metodo disponibile solo in POST!!1!");
	}

	@POST
	@Path("assignBadge")
	@Produces("application/json; charset=UTF-8")
	public GpBadgePojo assignBadge(@QueryParam("codBadge") String codBadge,
			@QueryParam("idPfPrincipal") Integer idPfPrincipal, @QueryParam("value") Double value)
			throws GamificationException {

		if (!isGamificationEnabled()) {
			throw new GamificationDisabledException("ERRORE assignBadge: la gamification è DISABILITATA su MyAuth!");
		}

		if (codBadge == null || !(codBadge.length() > 0))
			throw new GamificationRemoteException("ERRORE assignBadge: Parametro codBadge nullo");

		if (idPfPrincipal == null)
			throw new GamificationRemoteException("ERRORE assignBadge: Parametro idPfPrincipal nullo");

		if (value == null)
			throw new GamificationRemoteException("ERRORE assignBadge: Parametro value nullo");

		return badgeEJBLocalImpl.newBadge(codBadge, idPfPrincipal, value);
	}
	@GET
	@Path("getBadgeList")
	@Produces("application/json; charset=UTF-8")
	public List<DeBadgePojo> getBadgeList(@QueryParam("codSistema") String codSistema) throws GamificationException {
		DeSistemaEnum sistemaEnum = null;

		if (!isGamificationEnabled()) {
			throw new GamificationDisabledException("ERRORE getBadgeList: la gamification è DISABILITATA su MyAuth!");
		}

		if (codSistema != null) {
			try {
				sistemaEnum = DeSistemaEnum.valueOf(codSistema);
			} catch (IllegalArgumentException e) {
				throw new GamificationRemoteException("ERRORE getBadgeList: codSistema non Tovato!");
			}
		}

		return badgeEJBLocalImpl.retrieveBadges(sistemaEnum, null);
	}

	@GET
	@Path("enabled")
	@Produces("application/json; charset=UTF-8")
	public Boolean isGamificationEnabled() {
		return constantSingleton.isGamificationEnabled();
	}

	@GET
	@Path("getBadge")
	@Produces("application/json; charset=UTF-8")
	public DeBadgePojo getBadge(@QueryParam("codSistema") String codSistema, @QueryParam("codBadge") String codBadge)
			throws GamificationException {
		if (!constantSingleton.isGamificationEnabled()) {
			throw new GamificationDisabledException("Gamification DISABILITATA su MYAUTH");
		}

		if (codBadge == null || !(codBadge.length() > 0))
			throw new GamificationRemoteException("ERRORE getBadge: parametro codBadge nullo");

		DeSistemaEnum sistemaEnum = null;
		if (codSistema != null) {
			try {
				sistemaEnum = DeSistemaEnum.valueOf(codSistema);
			} catch (IllegalArgumentException e) {
				throw new GamificationRemoteException("ERRORE getBadge: codSistema non Tovato!");
			}
		}

		return badgeEJBLocalImpl.retrieveBadge(sistemaEnum, codBadge);
	}

}
