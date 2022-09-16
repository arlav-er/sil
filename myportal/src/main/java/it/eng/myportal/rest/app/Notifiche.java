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

import it.eng.myportal.ejb.stateless.app.NotificheEjb;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.helper.AppRequestValidator;
import it.eng.myportal.rest.app.pojo.NotifichePojoList;
import it.eng.myportal.utils.Utils;

/**
 * Servlet per recuperare la lista delle notifiche legate ad uno specifico utente
 * 
 * @author
 *
 */
@Stateless
@Path("/nocas/app/notifiche")
public class Notifiche {

	protected static Log log = LogFactory.getLog(Notifiche.class);

	private static final String JSON_GET_NOTIFICHE_BROADCAST = "getNotificheBroadcast/";
	private static final String JSON_GET_NOTIFICHE_PERSONALI = "getNotifichePersonali/";
	private static final String JSON_SET_NOTIFICA_CLICKED = "setClicked/";
	private static final String ROOT_APP_PATH = "/secure/nocas/app/notifiche/";
	private static final String PATH_GET_NOTIFICHE_BROADCAST = ROOT_APP_PATH + JSON_GET_NOTIFICHE_BROADCAST;
	private static final String PATH_GET_NOTIFICHE_PERSONALI = ROOT_APP_PATH + JSON_GET_NOTIFICHE_PERSONALI;
	private static final String PATH_SET_NOTIFICA_CLICKED = ROOT_APP_PATH + JSON_SET_NOTIFICA_CLICKED;

	@EJB
	NotificheEjb notificheEjb;

	@GET
	@Path(JSON_GET_NOTIFICHE_BROADCAST + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public NotifichePojoList getNotificheBroadcast(@PathParam("tokenSecurity") String tokenSecurity,
			@QueryParam("token") String token, @QueryParam("start") Integer start, @QueryParam("rows") Integer rows) {

		NotifichePojoList ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_GET_NOTIFICHE_BROADCAST);
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
				ret = notificheEjb.getNotificheBroadcast(username, start, rows);
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
	@Path(JSON_GET_NOTIFICHE_PERSONALI + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public NotifichePojoList getNotifichePersonali(@PathParam("tokenSecurity") String tokenSecurity,
			@QueryParam("token") String token, @QueryParam("start") Integer start, @QueryParam("rows") Integer rows) {

		NotifichePojoList ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_GET_NOTIFICHE_PERSONALI);
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
				ret = notificheEjb.getNotifichePersonali(username, start, rows);
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
	@Path(JSON_SET_NOTIFICA_CLICKED + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String setClicked(@PathParam("tokenSecurity") String tokenSecurity, @FormParam("token") String token,
			@FormParam("idNotifica") String sid) {
		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_SET_NOTIFICA_CLICKED);
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
			// Sid
			validator.setParam(sid);
			validator.checkMaxLengthToken().checkPatternAlphaNumNoSpace();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				if (log.isDebugEnabled()) {
					log.debug(String.format("username: %s, sid: %s", username, sid));
				}
				ret = notificheEjb.setClicked(username, sid);
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
