package it.eng.myportal.rest.app;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import it.eng.myportal.ejb.stateless.app.InformativeEjb;
import it.eng.myportal.ejb.stateless.app.PercorsoLavoratoreEjb;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.helper.AppRequestValidator;
import it.eng.myportal.utils.Utils;

@Stateless
@Path("nocas/app/servamm")
public class ServAmm {
	private static final String MEDIA_TYPE_APPLICATION_PDF = "application/pdf";

	protected static Log log = LogFactory.getLog(ServAmm.class);

	private static final String JSON_GET_PERCORSO_LAV_CONTEXT = "getPercorsoLav/";
	private static final String JSON_GET_STAMPA_PERCORSO_LAV_CONTEXT = "getStampaPercorsoLav/";
	private static final String JSON_SET_INFORMATIVE_LAV_CONTEXT = "setInformativeLav/";
	private static final String JSON_GET_INFORMATIVA_PERCORSO_LAV = "getInformativaPercorsoLavoratore/";
	private static final String JSON_GET_INFORMATIVA_DID = "getInformativaDid/";

	private static final String ROOT_APP_PATH = "/secure/nocas/app/servamm/";

	private static final String PATH_GET_PERCORSO_LAV = ROOT_APP_PATH + JSON_GET_PERCORSO_LAV_CONTEXT;
	private static final String PATH_GET_STAMPA_PERCORSO_LAV = ROOT_APP_PATH + JSON_GET_STAMPA_PERCORSO_LAV_CONTEXT;
	private static final String PATH_SET_INFORMATIVE_LAV = ROOT_APP_PATH + JSON_SET_INFORMATIVE_LAV_CONTEXT;
	private static final String PATH_GET_INFORMATIVA_PERCORSO_LAV = ROOT_APP_PATH + JSON_GET_INFORMATIVA_PERCORSO_LAV;
	private static final String PATH_GET_INFORMATIVA_DID = ROOT_APP_PATH + JSON_GET_INFORMATIVA_DID;

	private static final String TXT_PERCORSO_LAV_KEY = "cittadino.servizi.percorso_lavoratore.descrizione";
	private static final String TXT_DID_KEY = "cittadino.servizi.did.descrizione";

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	PercorsoLavoratoreEjb percorsoLavoratoreEjb;

	@EJB
	InformativeEjb informativeEjb;

	@POST
	@Path(JSON_SET_INFORMATIVE_LAV_CONTEXT + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String setInformativeLav(@PathParam("tokenSecurity") String tokenSecurity, @FormParam("token") String token,
			@FormParam("acceptInforPercorsoLav") Boolean acceptInforPercorsoLav,
			@FormParam("acceptInforDid") Boolean acceptInforDid) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_SET_INFORMATIVE_LAV);
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

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				ret = informativeEjb.setInformativeLav(username, acceptInforPercorsoLav, acceptInforDid);
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

	@POST
	@Path(JSON_GET_INFORMATIVA_PERCORSO_LAV + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String getInformativaPercorsoLavoratore(@PathParam("tokenSecurity") String tokenSecurity,
			@FormParam("token") String token) {
		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_GET_INFORMATIVA_PERCORSO_LAV);
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

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				ret = informativeEjb.getInformativaUtente(username, TXT_PERCORSO_LAV_KEY);
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

	@POST
	@Path(JSON_GET_INFORMATIVA_DID + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String getInformativaDid(@PathParam("tokenSecurity") String tokenSecurity,
			@FormParam("token") String token) {
		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_GET_INFORMATIVA_DID);
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

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				ret = informativeEjb.getInformativaUtente(username, TXT_DID_KEY);
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

	@GET
	@Path(JSON_GET_PERCORSO_LAV_CONTEXT + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String getPercorsoLav(@PathParam("tokenSecurity") String tokenSecurity, @QueryParam("token") String token,
			@QueryParam("da") String strDa, @QueryParam("a") String strA, @QueryParam("maxRappLav") int maxRappLav,
			@QueryParam("offset") int offset) {

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_GET_PERCORSO_LAV);
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
			// strDa
			validator.setParam(strDa);
			validator.checkMaxLengthDate().checkPatternDate();
			// strA
			validator.setParam(strA);
			validator.checkMaxLengthDate().checkPatternDate();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
				if (pfPrincipal != null) {
					return percorsoLavoratoreEjb.getPercorsoLavoratore(username, strDa, strA, maxRappLav, offset)
							.toString();
				} else {
					return checkerSec.renderWrongParameters().toString();
				}
			} else {
				return checkerSec.renderTokenSec().toString();
			}
		} catch (DecoderException e) {
			return checkerSec.renderWrongParameters().toString();
		} catch (AppEjbException e) {
			throw new WebApplicationException(e.createJsonResponse());
		} finally {
			jamonMonitor.stop();
		}
	}

	@GET
	@Path(JSON_GET_STAMPA_PERCORSO_LAV_CONTEXT + "{tokenSecurity}")
	@Produces(MEDIA_TYPE_APPLICATION_PDF)
	public byte[] getStampaPercorsoLav(@PathParam("tokenSecurity") String tokenSecurity,
			@QueryParam("token") String token, @QueryParam("da") String strDa, @QueryParam("a") String strA) {

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_GET_STAMPA_PERCORSO_LAV);
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
			// strDa
			validator.setParam(strDa);
			validator.checkMaxLengthDate().checkPatternDate();
			// strA
			validator.setParam(strA);
			validator.checkMaxLengthDate().checkPatternDate();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
				if (pfPrincipal != null) {
					return percorsoLavoratoreEjb.getStampaPercorsoLavoratore(username, strDa, strA);
				} else {
					Response response = createJsonResponse(checkerSec.renderWrongParameters().toString());
					throw new WebApplicationException(response);
				}
			} else {
				Response response = createJsonResponse(checkerSec.renderTokenSec().toString());
				throw new WebApplicationException(response);
			}
		} catch (DecoderException e) {
			Response response = createJsonResponse(checkerSec.renderWrongParameters().toString());
			throw new WebApplicationException(response);
		} catch (AppEjbException e) {
			throw new WebApplicationException(e.createJsonResponse());
		} catch (Exception e) {
			Response response = createJsonResponse(e.getMessage());
			throw new WebApplicationException(response);
		} finally {
			jamonMonitor.stop();
		}
	}

	private Response createJsonResponse(String msg) {
		ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
		builder.entity(msg);
		Response response = builder.build();
		return response;
	}

}
