package it.eng.myportal.rest.app;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.myportal.ejb.stateless.app.ComuniEJB;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.helper.AppRequestValidator;
import it.eng.myportal.utils.Utils;

@Stateless
@Path("/nocas/app/general/decodifiche/comuni")
public class Comuni {

	protected static Log log = LogFactory.getLog(Comuni.class);

	private static final String JSON_GET_COMUNI = "/";
	private static final String ROOT_APP_PATH = "/secure/nocas/app/general/decodifiche/comuni";
	private static final String PATH_GET_COMUNI = ROOT_APP_PATH + JSON_GET_COMUNI;

	@EJB
	private ComuniEJB comuniEJB;

	@GET
	@Path("/{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String getComuni(@PathParam("tokenSecurity") String tokenSecurity, @QueryParam("token") String token,
			@QueryParam("denominazione") String denominazione,
			@QueryParam("soloValidi") @DefaultValue("Y") String soloValidi) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_GET_COMUNI);
		try {
			/*
			 * ------------------------------------- Validazione parametri request -------------------------------------
			 */
			AppRequestValidator validator = new AppRequestValidator();
			// TokenSecurity
			validator.setParam(tokenSecurity);
			validator.checkMaxLengthTokenSecurity().checkPatternAlphaNumNoSpace();
			// Token
			validator.setParam(token);
			validator.checkMaxLengthToken().checkPatternAlphaNumNoSpace();
			// Denominazione
			validator.setParam(denominazione);
			validator.checkMaxLengthCosaDove();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				if (log.isDebugEnabled()) {
					log.debug(String.format("username: %s, denominazione: %s", username, denominazione));
				}
				ret = comuniEJB.getComuni(username, denominazione, soloValidi);
			} else {
				ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
				builder.entity(checkerSec.renderTokenSec().toString());
				Response response = builder.build();

				throw new WebApplicationException(response);
			}
		} catch (DecoderException e) {
			ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
			builder.entity(checkerSec.renderWrongParameters().toString());
			Response response = builder.build();

			throw new WebApplicationException(response);
		} catch (AppEjbException e) {
			throw new WebApplicationException(e.createJsonResponse());
		} finally {
			jamonMonitor.stop();
		}
		return ret;
	}

}
