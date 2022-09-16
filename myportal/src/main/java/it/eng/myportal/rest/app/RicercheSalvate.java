package it.eng.myportal.rest.app;

import java.util.List;

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
import org.json.JSONException;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.myportal.ejb.stateless.app.InformativeEjb;
import it.eng.myportal.ejb.stateless.app.PercorsoLavoratoreEjb;
import it.eng.myportal.ejb.stateless.app.RicercheSalvateEjb;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.helper.AppRequestValidator;
import it.eng.myportal.rest.app.pojo.RicercheSalvatePojo;
import it.eng.myportal.rest.app.pojo.RicercheSalvatePojoList;
import it.eng.myportal.utils.Utils;

@Stateless
@Path("nocas/app/vacancies2")
public class RicercheSalvate {

	private static final String JSON_GET_RICERCHE_SALVATE = "getRicercheSalvate/";
	private static final String JSON_DETTAGLIO_RICERCHE_SALVATE = "dettaglioRicercheSalvate/";
	private static final String JSON_ADD_RICERCHE_SALVATE = "addRicercheSalvate/";
	private static final String JSON_DEL_RICERCHE_SALVATE = "delRicercheSalvate/";
	private static final String ROOT_APP_PATH = "/secure/nocas/app/vacancies2/";
	private static final String PATH_GET_RICERCHE_SALVATE = ROOT_APP_PATH + JSON_GET_RICERCHE_SALVATE;
	private static final String PATH_DETTAGLIO_RICERCHE_SALVATE = ROOT_APP_PATH + JSON_DETTAGLIO_RICERCHE_SALVATE;
	private static final String PATH_ADD_RICERCHE_SALVATE = ROOT_APP_PATH + JSON_ADD_RICERCHE_SALVATE;
	private static final String PATH_DEL_RICERCHE_SALVATE = ROOT_APP_PATH + JSON_DEL_RICERCHE_SALVATE;

	@EJB
	PercorsoLavoratoreEjb percorsoLavoratoreEjb;
	@EJB
	InformativeEjb informativeEjb;
	@EJB
	RicercheSalvateEjb ricercheSalvateEjb;
	@EJB
	private RvTestataHome rvTestataHome;

	protected static Log log = LogFactory.getLog(RicercheSalvate.class);

	@GET
	@Path(JSON_GET_RICERCHE_SALVATE + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public RicercheSalvatePojoList getRicercheSalvate(@PathParam("tokenSecurity") String tokenSecurity,
			@QueryParam("token") String token, @QueryParam("start") Integer start, @QueryParam("rows") Integer rows) {

		RicercheSalvatePojoList ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_GET_RICERCHE_SALVATE);
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
				ret = ricercheSalvateEjb.getRicercheSalvate(username, start, rows);
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
	@Path(JSON_ADD_RICERCHE_SALVATE + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String addRicercheSalvate(@PathParam("tokenSecurity") String tokenSecurity, @FormParam("token") String token,
			@FormParam("descrizione") String descrizione, @FormParam("cosa") String cosa,
			@FormParam("dove") String dove, @FormParam("codMansione") List<String> codMansione,
			@FormParam("codContratto") List<String> codContratto, @FormParam("codOrario") List<String> codOrario,
			@FormParam("codSettore") List<String> codSettore, @FormParam("codLingua") List<String> codLingua,
			@FormParam("codTitoloStudio") List<String> codTitoloStudio,
			@FormParam("codPatente") List<String> codPatente) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_ADD_RICERCHE_SALVATE);
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
			// Descrizione
			validator.setParam(descrizione);
			validator.checkMaxLengthDesRicSalv();
			// Cosa
			validator.setParam(cosa);
			validator.checkMaxLengthCosaDove().checkPatternAlphaNumSpace();
			// Dove
			validator.setParam(dove);
			validator.checkMaxLengthCosaDove().checkPatternAlphaNumSpace();

			// codMansione
			AppRequestValidator validatorColl = new AppRequestValidator();
			validatorColl.setParams(codMansione);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();
			// codContratto
			validatorColl.setParams(codContratto);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();
			// codOrario
			validatorColl.setParams(codOrario);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();
			// codSettore
			validatorColl.setParams(codSettore);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();
			// codLingua
			validatorColl.setParams(codLingua);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();
			// codTitoloStudio
			validatorColl.setParams(codTitoloStudio);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();
			// codPatente
			validatorColl.setParams(codPatente);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);

				RicercheSalvatePojo ricercaSalvata = new RicercheSalvatePojo(descrizione, cosa, dove, codContratto,
						codLingua, codMansione, codOrario, codPatente, codSettore, codTitoloStudio);
				ret = ricercheSalvateEjb.addRicercheSalvate(username, ricercaSalvata);
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
	@Path(JSON_DETTAGLIO_RICERCHE_SALVATE + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public RicercheSalvatePojo dettaglioRicercheSalvate(@PathParam("tokenSecurity") String tokenSecurity,
			@QueryParam("token") String token, @QueryParam("id") Integer idRicercaSalvata) {

		RicercheSalvatePojo ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_DETTAGLIO_RICERCHE_SALVATE);
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
				ret = ricercheSalvateEjb.dettaglioRicercheSalvate(username, idRicercaSalvata);
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
	@Path(JSON_DEL_RICERCHE_SALVATE + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String delRicercheSalvate(@PathParam("tokenSecurity") String tokenSecurity,
			@QueryParam("token") String token, @QueryParam("idRicercaSalvata") Integer idRicercaSalvata) {
		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_DEL_RICERCHE_SALVATE);
		JSONObject obj = new JSONObject();
		try {
			// ------------------------------------- Validazione parametri request -------------------------------------
			AppRequestValidator validator = new AppRequestValidator();
			// TokenSecurity
			validator.setParam(tokenSecurity);
			validator.checkMaxLengthTokenSecurity().checkPatternAlphaNumNoSpace();
			// Token
			validator.setParam(token);
			validator.checkMaxLengthToken().checkPatternAlphaNumNoSpace();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				ricercheSalvateEjb.delRicercheSalvate(username, idRicercaSalvata);
				obj.put(CheckerSec.STATUS, CheckerSec.OK);
				return obj.toString();

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
		} catch (JSONException e) {
			log.error(e);
			return obj.toString();
		} finally {
			jamonMonitor.stop();
		}
	}
}
