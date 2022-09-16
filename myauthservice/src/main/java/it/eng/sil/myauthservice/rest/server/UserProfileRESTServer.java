package it.eng.sil.myauthservice.rest.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import it.eng.sil.base.business.GestioneProfiloRemoteClient;
import it.eng.sil.base.exceptions.ProfilaturaException;
import it.eng.sil.base.pojo.auth.BasicInfoPOJO;
import it.eng.sil.base.pojo.auth.gp.DestinatarioPOJO;
import it.eng.sil.base.pojo.auth.gp.GpComponentePOJO;
import it.eng.sil.base.pojo.auth.gp.GpGruppoPOJO;
import it.eng.sil.base.pojo.auth.gp.GpRuoloGruppoPOJO;
import it.eng.sil.base.pojo.auth.gp.GpRuoloPOJO;
import it.eng.sil.base.pojo.auth.gp.UserProfiloPOJO;
import it.eng.sil.base.pojo.decodifiche.DeComunePOJO;
import it.eng.sil.myauthservice.exception.ProfilaturaInvalidParameterException;
import it.eng.sil.myauthservice.exception.ProfilaturaMissingParameterException;
import it.eng.sil.myauthservice.exception.ProfilaturaNoResultException;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.business.GestioneProfiloClientLocalImplEJB;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpProfilatura;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpGruppoEJB;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpProfilaturaEJB;
import it.eng.sil.mycas.model.manager.profilatura.PfPrincipalEJB;
import javassist.NotFoundException;

/**
 * Questa classe espone i WS di profilatura unica ragion d'essere di questo .war, per il resto semplice wrapper di
 * myauth-commons
 * 
 * @author pegoraro
 *
 */
@Path("/userprofile")
public class UserProfileRESTServer extends GestioneProfiloRemoteClient {
	protected static Logger log = Logger.getLogger(UserProfileRESTServer.class.getName());

	@Inject
	GestioneProfiloClientLocalImplEJB gestioneProfiloClientLocalImplEJB;

	@Inject
	PfPrincipalEJB pfPrincipalHome;

	@Inject
	GpGruppoEJB gpGruppoEJB;

	@Inject
	DeComuneEJB deComuneEJB;

	@Inject
	GpProfilaturaEJB gpProfilaturaEJB;

	/**
	 * Restituisce il profilo, comprensivo di lista ruolo/gruppo possibile In questo modo l'applicazione può permettere
	 * all'utente la scelta del "cappello" con cui entrare nel modulo
	 * 
	 * @param username
	 * @return
	 * @throws MyCasException
	 */
	@GET
	@Path("getProfilo")
	@Produces("application/json; charset=UTF-8")
	public UserProfiloPOJO getProfilo(@QueryParam("username") String username,
			@QueryParam("idPfPrincipal") Integer idPfPrincipal) throws ProfilaturaException {
		if ((username == null || !(username.length() > 0)) && (idPfPrincipal == null)) {
			throw new ProfilaturaMissingParameterException(
					"Questo WS richiede il parametro username (username dell'utente) oppure il suo id (idPfPrincipal)");
		} else if (username == null || !(username.length() > 0)) {
			try {
				return gestioneProfiloClientLocalImplEJB.getUserInfoGp(idPfPrincipal);
			} catch (MyCasNoResultException e) {
				throw new ProfilaturaNoResultException("Profilo non trovato per utente BY id: " + username);
			}
		} else {
			try {
				String decodedUsername = URLDecoder.decode(username, "UTF-8");
				return gestioneProfiloClientLocalImplEJB.getUserInfoGp(decodedUsername);
			} catch (UnsupportedEncodingException e) {
				throw new ProfilaturaException("Errore durante la getProfilo: " + e.toString());
			} catch (MyCasNoResultException e) {
				throw new ProfilaturaNoResultException("Profilo non trovato per utente BY (decoded)username: " + username);
			}
		}

	}

	/**
	 * Ottieni oggetti profilati
	 * 
	 * @param idPfPrincipal
	 * @return
	 */
	@GET
	@Path("getListaComponenti")
	@Produces("application/json; charset=UTF-8")
	public List<GpComponentePOJO> getListaComponenti(@QueryParam("idGpRuoloGruppo") String idGpRuoloGruppo)
			throws ProfilaturaException {
		Integer work = null;
		if (idGpRuoloGruppo == null)
			throw new ProfilaturaMissingParameterException("Parametro idGpRuoloGruppo nullo");

		try {
			work = Integer.parseInt(idGpRuoloGruppo);
		} catch (NumberFormatException e) {
			throw new ProfilaturaInvalidParameterException("idGpRuoloGruppo deve essere un numero intero");
		}

		try {
			return gestioneProfiloClientLocalImplEJB.getListaComponenti(work);
		} catch (MyCasNoResultException e) {
			throw new ProfilaturaNoResultException("Ruolo-gruppo non trovato: " + idGpRuoloGruppo);
		}

	}

	/**
	 * Ottieni oggetti profilati
	 * 
	 * @param idPfPrincipal
	 * @return
	 */
	@GET
	@Path("getAttributeOwners")
	@Produces("application/json; charset=UTF-8")
	public List<DestinatarioPOJO> getAttributeOwners(@QueryParam("idGpGruppo") String idGpGruppo,
			@QueryParam("codTokenAttributo") String codTokenAttributo) throws ProfilaturaException {
		Integer work = null;
		if (idGpGruppo == null || !(idGpGruppo.length() > 0))
			throw new ProfilaturaMissingParameterException("Il parametro idGpGruppo è obbligatorio");
		if (codTokenAttributo == null || !(codTokenAttributo.length() > 0))
			throw new ProfilaturaMissingParameterException("Il parametro codTokenAttributo è obbligatorio");

		try {
			work = Integer.parseInt(idGpGruppo);
		} catch (NumberFormatException e) {
			throw new ProfilaturaInvalidParameterException("idGpGruppo deve essere un numero intero");
		}

		try {
			return gestioneProfiloClientLocalImplEJB.getAttributeOwners(codTokenAttributo, work);
		} catch (MyCasNoResultException e) {
			throw new ProfilaturaNoResultException("Gruppo non trovato: " + idGpGruppo);
		}

	}

	/**
	 * Ottieni NUOVO Gruppo
	 * 
	 * @param idPfPrincipal
	 * @return
	 * @throws NotFoundException
	 * @throws MyCasNoResultException
	 */
	@GET
	@Path("getGpGruppo")
	@Produces("application/json; charset=UTF-8")
	public GpGruppoPOJO getGpGruppo(@QueryParam("idGpGruppo") String idGpGruppo) throws ProfilaturaException {
		Integer work = null;
		if (idGpGruppo == null || !(idGpGruppo.length() > 0))
			throw new ProfilaturaMissingParameterException("Parametro idGpGruppo nullo");

		try {
			work = Integer.parseInt(idGpGruppo);
		} catch (NumberFormatException e) {
			throw new ProfilaturaInvalidParameterException("idGpGruppo deve essere un numero intero");
		}

		try {
			return gestioneProfiloClientLocalImplEJB.retrieveGruppo(work);
		} catch (MyCasNoResultException e) {
			throw new ProfilaturaNoResultException("Gruppo non trovato: " + idGpGruppo);
		}
	}

	/**
	 * Ottieni Gruppi per comune /MyAuthService/rest/userprofile/getGpGruppoByCodComune?codComune=G535
	 * 
	 * @param idPfPrincipal
	 * @return
	 * @throws NotFoundException
	 * @throws MyCasNoResultException
	 */
	@GET
	@Path("getGpGruppoByCodComune")
	@Produces("application/json; charset=UTF-8")
	public List<GpGruppoPOJO> getGpGruppoByCodComune(@QueryParam("codComune") String codCom)
			throws ProfilaturaException {
		if (codCom == null || !(codCom.length() > 0))
			throw new ProfilaturaMissingParameterException("Parametro codComune nullo");

		try {
			deComuneEJB.findById(codCom);
		} catch (Exception e) {
			throw new ProfilaturaInvalidParameterException("Non esiste questo comune: " + codCom);
		}

		try {
			return gestioneProfiloClientLocalImplEJB.getGpGruppoByCodComuneImplLocale(codCom);
		} catch (MyCasNoResultException e) {
			throw new ProfilaturaNoResultException("Gruppo non trovato per comune: " + codCom);
		}
	}

	/**
	 * Ottieni Gruppi per comune /MyAuthService/rest/userprofile/getGpGruppoByCodComune?codComune=G535
	 * 
	 * @param idPfPrincipal
	 * @return
	 * @throws NotFoundException
	 * @throws MyCasNoResultException
	 */
	@GET
	@Path("getGpGruppoChildren")
	@Produces("application/json; charset=UTF-8")
	public List<GpGruppoPOJO> getGpGruppoChildren(@QueryParam("idGpGruppoPadre") String idGpGruppoPadre)
			throws ProfilaturaException {
		Integer work = null;
		if (idGpGruppoPadre == null || !(idGpGruppoPadre.length() > 0))
			throw new ProfilaturaMissingParameterException("Parametro idGpGruppoPadre nullo");

		try {
			work = Integer.parseInt(idGpGruppoPadre);
		} catch (NumberFormatException e) {
			throw new ProfilaturaInvalidParameterException("idGpGruppoPadre deve essere un numero intero");
		}

		try {
			gpGruppoEJB.findById(work);
		} catch (Exception e) {
			throw new ProfilaturaInvalidParameterException("Non esiste questo gruppo, quindi non può avere figli: "
					+ idGpGruppoPadre);
		}

		try {
			return gestioneProfiloClientLocalImplEJB.retrieveGruppoDescendant(work);
		} catch (MyCasNoResultException e) {
			throw new ProfilaturaNoResultException("GruppoPadre non trovato per id: " + idGpGruppoPadre);
		}
	}

	/**
	 * Ottieni Ruolo-Gruppo
	 * 
	 * @param idPfPrincipal
	 * @return
	 * @throws NotFoundException
	 * @throws MyCasNoResultException
	 */
	@GET
	@Path("getGpRuoloGruppo")
	@Produces("application/json; charset=UTF-8")
	public GpRuoloGruppoPOJO getGpRuoloGruppo(@QueryParam("idGpRuoloGruppo") String idGpGruppo)
			throws ProfilaturaException {
		Integer work = null;
		if (idGpGruppo == null || !(idGpGruppo.length() > 0))
			throw new ProfilaturaMissingParameterException("Parametro idGpRuoloGruppo nullo");

		try {
			work = Integer.parseInt(idGpGruppo);
		} catch (NumberFormatException e) {
			throw new ProfilaturaInvalidParameterException("idGpRuoloGruppo deve essere un numero intero");
		}

		try {

			return gestioneProfiloClientLocalImplEJB.retrieveRuoloGruppo(work);
		} catch (MyCasNoResultException e) {
			throw new ProfilaturaNoResultException("Gruppo non trovato: " + idGpGruppo);
		}
	}

	@GET
	@Path("getBasicInfo")
	@Produces("application/json; charset=UTF-8")
	public BasicInfoPOJO getBasicInfo(@QueryParam("id") Integer idPfPrincipal) throws ProfilaturaException {
		if (idPfPrincipal == null) {
			throw new ProfilaturaMissingParameterException(
					"Questo WS richiede il parametro id (id_pf_principal dell'utente)");
		}
		try {
			return gestioneProfiloClientLocalImplEJB.getBasicInfo(idPfPrincipal);
		} catch (MyCasNoResultException e) {
			throw new ProfilaturaNoResultException("Utente non trovato per la getBasicInfo, id: " + idPfPrincipal);
		}
	}
 

	@GET
	@Path("getComuniInDistretto")
	@Produces("application/json; charset=UTF-8")
	//DEPRECATO, Usare DistrettiRESTSrv
	public List<DeComunePOJO> getComuniInDistretto(@QueryParam("idDistretto") Integer idDistretto)
			throws ProfilaturaException {
		if (idDistretto == null) {
			throw new ProfilaturaMissingParameterException("Questo WS richiede il parametro idDistretto");
		}

		try {
			return gestioneProfiloClientLocalImplEJB.getComuniInDistrettoByProfilatura(idDistretto);
		} catch (MyCasNoResultException e) {
			throw new ProfilaturaNoResultException("Errore durante la getComuniInDistretto per distretto: "
					+ idDistretto + " : " + e.toString());
		}
	}

	/**
	 * Ottieni gruppi per descrizione
	 * 
	 * @param idPfPrincipal
	 * @return
	 */
	@GET
	@Path("getGpGruppoByDesc")
	@Produces("application/json; charset=UTF-8")
	public List<GpGruppoPOJO> getGpGruppoByDesc(@QueryParam("descrizione") String desc) throws ProfilaturaException {
		if (desc == null || desc.length() == 0) {
			throw new ProfilaturaMissingParameterException("Parametro descrizione nullo");
		}

		try {
			String decodedDesc = URLDecoder.decode(desc, "UTF-8");
			return gestioneProfiloClientLocalImplEJB.retrieveGruppoByDesc(decodedDesc);
		} catch (UnsupportedEncodingException e) {
			throw new ProfilaturaException("Errore decoding durante la getGpGruppoByDesc: " + e.toString());
		}

	}

	/**
	 * Ottieni ruoli per descrizione
	 * 
	 * @param idPfPrincipal
	 * @return
	 */
	@GET
	@Path("getGpRuoloByDesc")
	@Produces("application/json; charset=UTF-8")
	public List<GpRuoloPOJO> getGpRuoloByDesc(@QueryParam("descrizione") String desc) throws ProfilaturaException {
		if (desc == null)
			throw new ProfilaturaMissingParameterException("Parametro descrizione nullo");

		try {
			String decodedDesc = URLDecoder.decode(desc, "UTF-8");
			return gestioneProfiloClientLocalImplEJB.retrieveRuoloByDesc(decodedDesc);
		} catch (UnsupportedEncodingException e) {
			throw new ProfilaturaException("Errore decoding durante la getGpGruppoByDesc: " + e.toString());
		}

	}

	@GET
	@Path("putProfilatura")
	@Produces("application/json; charset=UTF-8")
	public GpRuoloGruppoPOJO putProfilatura(@QueryParam("idGpRuolo") Integer idGpRuolo,
			@QueryParam("idGpGruppo") Integer idGpGruppo, @QueryParam("idPfPrincipal") Integer idPfPrincipal)
			throws ProfilaturaException {

		if (idGpRuolo == null)
			throw new ProfilaturaMissingParameterException("Parametro idGpRuolo nullo");
		if (idGpGruppo == null)
			throw new ProfilaturaMissingParameterException("Parametro idGpGruppo nullo");
		if (idPfPrincipal == null)
			throw new ProfilaturaMissingParameterException(
					"Parametro idPfPrincipal nullo: utente a cui assegnare la profilatura");
		try {
			GpProfilatura created = gestioneProfiloClientLocalImplEJB.addProfilatura(idPfPrincipal, idGpRuolo,
					idGpGruppo);
			log.info("Creata profilatura gruppo: " + created.getGpRuoloGruppo().getGpGruppo().getDescrizione()
					+ " - Ruolo: " + created.getGpRuoloGruppo().getGpRuolo().getDescrizione());
			return gpProfilaturaEJB.findRuoloGruppoPOJO(created.getIdGpProfilatura());
		} catch (MyCasException e) {
			log.info("Errore in creazione profilatura: " + e.getMessage());
			throw new ProfilaturaException(e);
		}

	}

}
