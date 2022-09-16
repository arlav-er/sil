package it.eng.myportal.rest.app;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.myportal.ejb.stateless.app.FeedbackEjb;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.helper.AppRequestValidator;
import it.eng.myportal.utils.Utils;

@Stateless
@Path("/nocas/app/feedback")
public class Feedback {

	private static final String ROOT_APP_PATH = "/secure/nocas/app/feedback/";
	private static final String JSON_ADD_ASSISTENZA = "addAssistenza/";
	private static final String JSON_ADD_VALUTAZIONE = "addValutazione/";
	private static final String PATH_ADD_VALUTAZIONE = ROOT_APP_PATH + JSON_ADD_VALUTAZIONE;
	private static final String PATH_ADD_ASSISTENZA = ROOT_APP_PATH + JSON_ADD_ASSISTENZA;

	@EJB
	FeedbackEjb feedbackEjb;

	@POST
	@Path(JSON_ADD_ASSISTENZA + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String addAssistenza(@PathParam("tokenSecurity") String tokenSecurity, @FormParam("token") String token,
			@FormParam("codTipo") String codTipo, @FormParam("messaggio") String messaggio) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_ADD_ASSISTENZA);
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
			// CodTipo
			validator.setParam(codTipo);
			validator.checkMaxLengthCodice().checkPatternAlphaNumNoSpace();
			// Messaggio
			validator.setParam(messaggio);
			validator.checkMaxLengthMessaggio().checkPatternMessaggio();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);

				ret = feedbackEjb.addAssistenza(username, codTipo, messaggio);
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
	@Path(JSON_ADD_VALUTAZIONE + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String addValutazione(@PathParam("tokenSecurity") String tokenSecurity, @FormParam("token") String token,
			@FormParam("numStelle") Short numStelle, @FormParam("messaggio") String messaggio) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_ADD_VALUTAZIONE);
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
			// Messaggio
			validator.setParam(messaggio);
			validator.checkMaxLengthMsgValutazione().checkPatternMessaggio();

			if (checkerSec.isOk()) {

				String username = null;
				if (!StringUtils.isBlank(token)) {
					// Valutazione con login, recupero dello username
					username = Utils.getUsernameFromTokenUtente(token);
				}

				ret = feedbackEjb.addValutazione(username, numStelle, messaggio);
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
