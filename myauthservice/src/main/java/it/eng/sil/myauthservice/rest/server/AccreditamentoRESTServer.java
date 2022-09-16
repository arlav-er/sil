package it.eng.sil.myauthservice.rest.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import it.eng.sil.base.exceptions.AccreditamentoRemoteException;
import it.eng.sil.base.pojo.accreditamento.SedeAccreditataPOJO;
import it.eng.sil.mycas.model.business.AccreditamentoClientEJB;

@Path("/accreditamento")
public class AccreditamentoRESTServer {
	protected static Logger log = Logger.getLogger(AccreditamentoRESTServer.class.getName());

	@Inject
	AccreditamentoClientEJB accreditamentoClientEJB;

	@POST
	@Path("putSedeAccreditata")
	@Produces("application/json; charset=UTF-8")
	public SedeAccreditataPOJO putSedeAccreditata(@FormParam("idPfPrincipal") String idPfPrincipal,
			@FormParam("denominazione") String denominazione, @FormParam("codComune") String codComune,
			@FormParam("cap") String cap, @FormParam("indirizzo") String indirizzo,
			@FormParam("latitudine") Double latitudine, @FormParam("longitudine") Double longitudine,
			@FormParam("numBando") String bando) throws AccreditamentoRemoteException {
		if (idPfPrincipal == null || idPfPrincipal.isEmpty()) {
			throw new AccreditamentoRemoteException("Errore putSedeAccreditata: idPfPrincipal nullo");
		}

		String indirizzoDecoded = "";
		String denominazioneDecoded = "";
		try {
			indirizzoDecoded = URLDecoder.decode(indirizzo, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new AccreditamentoRemoteException(
					"Errore putSedeAccreditata, errore decoding indirizzo: " + e.toString());
		}

		try {
			denominazioneDecoded = URLDecoder.decode(denominazione, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new AccreditamentoRemoteException(
					"Errore putSedeAccreditata, errore decoding denominazione: " + e.toString());
		}

		return accreditamentoClientEJB.putSedeAccreditata(null, Integer.parseInt(idPfPrincipal), denominazioneDecoded,
				codComune, cap, indirizzoDecoded, latitudine, longitudine, bando);
	}

	@GET
	@Path("getSediAccreditate")
	@Produces("application/json; charset=UTF-8")
	public List<SedeAccreditataPOJO> getSediAccreditate(@QueryParam("idPfPrincipal") String idPfPrincipal)
			throws AccreditamentoRemoteException {
		if (idPfPrincipal == null || idPfPrincipal.isEmpty()) {
			throw new AccreditamentoRemoteException("Errore getSediAccreditate: idPfPrincipal nullo");
		}

		return accreditamentoClientEJB.getSediAccreditate(null, Integer.parseInt(idPfPrincipal));
	}

	@GET
	@Path("getSediAccreditateByProvincia")
	@Produces("application/json; charset=UTF-8")
	public List<SedeAccreditataPOJO> getSediAccreditateByProvincia(@QueryParam("codProvincia") String codProvincia)
			throws AccreditamentoRemoteException {
		if (codProvincia == null || codProvincia.isEmpty()) {
			throw new AccreditamentoRemoteException("Errore getSediAccreditateByProvincia: codProvincia nullo");
		}

		return accreditamentoClientEJB.getSediAccreditateByCodProvincia(null, codProvincia);
	}

	@GET
	@Path("getSediAccreditateByAreaAndProvincia")
	@Produces("application/json; charset=UTF-8")
	public List<SedeAccreditataPOJO> getSediAccreditateByAreaAndProvincia(
			@QueryParam("codProvincia") String codProvincia, @QueryParam("codArea") String codArea)
			throws AccreditamentoRemoteException {
		if (codProvincia == null || codProvincia.isEmpty()) {
			throw new AccreditamentoRemoteException("Errore getSediAccreditateByAreaAndProvincia: codProvincia nullo");
		}
		if (codArea == null || codArea.isEmpty()) {
			throw new AccreditamentoRemoteException("Errore getSediAccreditateByAreaAndProvincia: codArea nullo");
		}

		return accreditamentoClientEJB.getSediAccreditateByAreaAndCodProvincia(null, codProvincia, codArea);
	}

	@GET
	@Path("getSediAccreditateByDistretto")
	@Produces("application/json; charset=UTF-8")
	public List<SedeAccreditataPOJO> getSediAccreditateByIdGpGruppoDistretto(
			@QueryParam("idDistretto") Integer idDistretto) throws AccreditamentoRemoteException {
		if (idDistretto == null) {
			throw new AccreditamentoRemoteException("Errore getSediAccreditate: idDistretto nullo");
		}

		return accreditamentoClientEJB.getSediAccreditateByIdGpGruppoDistretto(null, idDistretto);
	}

	@GET
	@Path("getSediAccreditateByGruppoOrPadre")
	@Produces("application/json; charset=UTF-8")
	public List<SedeAccreditataPOJO> getSediAccreditateByIdGpGruppoOrPadre(@QueryParam("idGpGruppo") Integer idGpGruppo,
			@QueryParam("idGpGruppoPadre") Integer idGpGruppoPadre) throws AccreditamentoRemoteException {
		if (idGpGruppo == null) {
			throw new AccreditamentoRemoteException("Errore getSediAccreditateByIdGpGruppoOrPadre: idGpGruppo nullo");
		}
		if (idGpGruppoPadre == null) {
			throw new AccreditamentoRemoteException("Errore getSediAccreditateByIdGpGruppoOrPadre: idGpGruppoPadre nullo");
		}

		return accreditamentoClientEJB.getSediAccreditateByIdGpGruppoOrPadre(null, idGpGruppo, idGpGruppoPadre);
	}

	@GET
	@Path("getSediAccreditateByAreaAndDistretto")
	@Produces("application/json; charset=UTF-8")
	public List<SedeAccreditataPOJO> getSediAccreditateByAreaAndIdGpGruppoDistretto(
			@QueryParam("idDistretto") Integer idDistretto, @QueryParam("codArea") String codArea)
			throws AccreditamentoRemoteException {
		if (idDistretto == null) {
			throw new AccreditamentoRemoteException(
					"Errore getSediAccreditateByAreaAndIdGpGruppoDistretto: idDistretto nullo");
		}
		if (codArea == null) {
			throw new AccreditamentoRemoteException(
					"Errore getSediAccreditateByAreaAndIdGpGruppoDistretto: codArea nullo");
		}

		return accreditamentoClientEJB.getSediAccreditateByAreaAndIdGpGruppoDistretto(null, idDistretto, codArea);
	}

	@GET
	@Path("disattivaSedeAccreditata")
	@Produces("application/json; charset=UTF-8")
	public int disattivaSedeAccreditata(@QueryParam("idSedeAccreditata") String idSedeAccreditata,
			@QueryParam("idPfPrincipal") String idPfPrincipal) throws AccreditamentoRemoteException {
		if (idSedeAccreditata == null || idSedeAccreditata.isEmpty()) {
			throw new AccreditamentoRemoteException("Errore disattivaSedeAccreditata: idSedeAccreditata nullo");
		}

		if (idPfPrincipal == null || idPfPrincipal.isEmpty()) {
			throw new AccreditamentoRemoteException("Errore disattivaSedeAccreditata: idPfPrincipal nullo");
		}

		accreditamentoClientEJB.disattivaSedeAccreditata(null, Integer.parseInt(idSedeAccreditata),
				Integer.parseInt(idPfPrincipal));
		return 1;
	}

	@GET
	@Path("disattivaSediAccreditate")
	@Produces("application/json; charset=UTF-8")
	public int disattivaSediAccreditate(@QueryParam("idPfPrincipal") String idPfPrincipal)
			throws AccreditamentoRemoteException {
		if (idPfPrincipal == null || idPfPrincipal.isEmpty()) {
			throw new AccreditamentoRemoteException("Errore disattivaSediAccreditate: idPfPrincipal nullo");
		}

		accreditamentoClientEJB.disattivaSediAccreditate(null, Integer.parseInt(idPfPrincipal));
		return 1;
	}
}
