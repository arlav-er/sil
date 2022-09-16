package it.eng.myportal.rest.app;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.myportal.ejb.stateless.app.UsersEjb;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.helper.AppRequestValidator;
import it.eng.myportal.rest.app.helper.MultiPartHelper;
import it.eng.myportal.rest.app.pojo.UploadedFile;
import it.eng.myportal.utils.Utils;

/**
 * Servlet per la login
 * 
 * @author
 *
 */
@Stateless
@Path("nocas/app/users")
public class Users {

	@EJB
	UsersEjb usersEjb;

	protected static Log log = LogFactory.getLog(Users.class);

	private static final String JSON_LOGIN = "login/";
	private static final String ROOT_APP_PATH = "/secure/nocas/app/users/";
	private static final String PATH_LOGIN = ROOT_APP_PATH + JSON_LOGIN;
	private static final String JSON_GET_USER_INFO = "getUserInfo/";
	private static final String PATH_GET_USER_INFO = ROOT_APP_PATH + JSON_GET_USER_INFO;
	private static final String JSON_UPDATE_USER = "update/";
	private static final String PATH_UPDATE_USER = ROOT_APP_PATH + JSON_UPDATE_USER;
	private static final String JSON_ADD_FOTO = "addFoto/";
	private static final String PATH_ADD_FOTO = ROOT_APP_PATH + JSON_ADD_FOTO;
	private static final String JSON_DELETE_FOTO = "delFoto/";
	private static final String PATH_DELETE_FOTO = ROOT_APP_PATH + JSON_DELETE_FOTO;

	@POST
	@Path(JSON_LOGIN + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String login(@PathParam("tokenSecurity") String tokenSecurity, @FormParam("username") String username,
			@FormParam("password") String password) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_LOGIN);

		try {
			/*
			 * ------------------------------------- Validazione parametri request -------------------------------------
			 */
			AppRequestValidator validator = new AppRequestValidator();
			// TokenSecurity
			validator.setParam(tokenSecurity);
			validator.checkMaxLengthTokenSecurity().checkPatternAlphaNumNoSpace();
			// Username
			validator.setParam(username);
			validator.checkMaxLengthLogin().checkPatternAlphaNumNoSpace();
			// Password
			validator.setParam(password);
			validator.checkMaxLengthLogin().checkPatternAlphaNumNoSpace();

			if (checkerSec.isOk()) {
				ret = usersEjb.login(username, password);
			} else {
				ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
				builder.entity(checkerSec.renderTokenSec().toString());
				Response response = builder.build();

				throw new WebApplicationException(response);
			}
		} catch (AppEjbException e) {
			throw new WebApplicationException(e.createJsonResponse());
		} finally {
			jamonMonitor.stop();
		}
		return ret;
	}

	@POST
	@Path(JSON_GET_USER_INFO + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String getUserInfo(@PathParam("tokenSecurity") String tokenSecurity, @FormParam("token") String token) {

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_GET_USER_INFO);

		String ret = null;

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
				ret = usersEjb.getUserInfo(username);
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
	@Path(JSON_UPDATE_USER + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String updateUser(@PathParam("tokenSecurity") String tokenSecurity, @FormParam("token") String token,
			@FormParam("nome") String nome, @FormParam("cognome") String cognome,
			@FormParam("dtNascita") String dtNascita, @FormParam("email") String email) {

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_UPDATE_USER);

		String ret = null;

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
			// Nome
			validator.setParam(nome);
			validator.checkMaxLengthCosaDove().checkPatternAlphaNumSpace();
			// Cognome
			validator.setParam(cognome);
			validator.checkMaxLengthCosaDove().checkPatternAlphaNumSpace();
			// DtNascita
			validator.setParam(dtNascita);
			validator.checkMaxLengthDate().checkPatternDate();
			// Email
			validator.setParam(email);
			validator.checkMaxLengthCosaDove().checkEmail();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				ret = usersEjb.updateUser(username, nome, cognome, dtNascita, email);
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
	@Path(JSON_ADD_FOTO + "{tokenSecurity}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/json; charset=UTF-8")
	public String addFoto(@PathParam("tokenSecurity") String tokenSecurity, MultipartFormDataInput input) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_ADD_FOTO);

		try {
			// Extract Params
			MultiPartHelper multiPartHelper = new MultiPartHelper(input.getFormDataMap());
			UploadedFile cv = multiPartHelper.extractFileFromMultiPart("uploadedFile");
			String token = multiPartHelper.extractStringFromMultiPart("token");

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
			// Dimensione file
			validator.setParamData(cv.getData());
			validator.checkMaxLengthFileFoto();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				ret = usersEjb.updateFoto(username, cv.getData());
			} else {
				ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
				builder.entity(checkerSec.renderTokenSec().toString());
				Response response = builder.build();

				throw new WebApplicationException(response);
			}
		} catch (DecoderException | IOException e) {
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
	@Path(JSON_DELETE_FOTO + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String delFoto(@PathParam("tokenSecurity") String tokenSecurity, @QueryParam("token") String token) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_DELETE_FOTO);

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
				ret = usersEjb.updateFoto(username, null);
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
