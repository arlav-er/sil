package it.eng.myportal.rest.services;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.dtos.WsStampaDTO;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsStampaHome;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateless
@Path("rest/services/")
public class Stampe {

	protected static Log log = LogFactory.getLog(Stampe.class);

	@EJB
	WsStampaHome wsStampaHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	
	@GET
	@Path("get_stampa")
	@Produces("application/pdf")
	public Response getStampa(@QueryParam("stampa_id") Integer wsStampaId, @Context HttpServletRequest request) {
		try {
			Principal up = request.getUserPrincipal();
			if (up == null) { 
				throw new Exception("Non sei autenticato");
			}
			String username = AuthUtil.removeSocialPrefix(up.getName());
			WsStampaDTO wsStampa = wsStampaHome.findDTOById(wsStampaId);
			if (wsStampa.getIdPfPrincipal().intValue() != utenteInfoHome.findDTOByUsername(username).getId().intValue()) {
				throw new Exception("Non disponi dei permessi per ottenere questa stampa");
			}
			ByteArrayInputStream bis = new ByteArrayInputStream(wsStampa.getContenuto());
			ResponseBuilder responseBuilder = Response.ok(bis);
			String fname = "attachment; filename=stampa.pdf";
			responseBuilder.header("Content-Disposition", fname);
			Response response = responseBuilder.build();
			return response;
		} catch (IOException e) {
			log.error(e);
			ResponseBuilder responseBuilder = Response.serverError();
			return responseBuilder.build();
		} catch (Exception e) {
			log.error(e);
			ResponseBuilder responseBuilder = Response.serverError();
			return responseBuilder.build();
		}
	}


	

}
