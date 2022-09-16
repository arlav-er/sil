package it.eng.sil.myauthservice.rest.server;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import it.eng.sil.base.exceptions.ProfilaturaException;
import it.eng.sil.base.pojo.auth.gp.GpGruppoPOJO;
import it.eng.sil.base.pojo.decodifiche.DeComunePOJO;
import it.eng.sil.myauthservice.exception.ProfilaturaMissingParameterException;
import it.eng.sil.myauthservice.exception.ProfilaturaNoResultException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.business.GestioneProfiloClientLocalImplEJB;

@Path("/distretti")
public class DistrettiRESTServer {

	@Inject
	GestioneProfiloClientLocalImplEJB gestioneProfiloClientLocalImplEJB;
	@GET
	@Path("getDistrettoList")
	@Produces("application/json; charset=UTF-8")
	public List<GpGruppoPOJO> getDistrettoList() throws ProfilaturaException {
		return gestioneProfiloClientLocalImplEJB.getDistrettoList();
	}

	@GET
	@Path("getComuniInDistretto")
	@Produces("application/json; charset=UTF-8")
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
	
	@GET
	@Path("getComuniInProvincia")
	@Produces("application/json; charset=UTF-8")
	public List<DeComunePOJO> getComuniInProvincia(@QueryParam("idGpGruppo") Integer idGpGruppoProvincia)
			throws ProfilaturaException {
		if (idGpGruppoProvincia == null) {
			throw new ProfilaturaMissingParameterException("Questo WS richiede il parametro idGpGruppo (della provincia)");
		}

		try {
			return gestioneProfiloClientLocalImplEJB.getComuniInProvinciaByProfilatura(idGpGruppoProvincia);
		} catch (MyCasNoResultException e) {
			throw new ProfilaturaNoResultException("Errore durante la getComuniInProvincia per provincia: "
					+ idGpGruppoProvincia + " : " + e.toString());
		}
	}
}
