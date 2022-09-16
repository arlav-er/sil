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
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.myportal.ejb.stateless.app.CurriculumUtenteEjb;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.helper.AppRequestValidator;
import it.eng.myportal.rest.app.helper.MultiPartHelper;
import it.eng.myportal.rest.app.pojo.UploadedFile;
import it.eng.myportal.utils.Utils;

/**
 * Servlet per recuperare la lista dei curriculum dell'utente collegato
 * 
 * @author
 *
 */
@Stateless
@Path("/nocas/app/curriculum")
public class CurriculumUtente {

	@EJB
	CurriculumUtenteEjb curriculumUtenteEjb;

	private static final String ROOT_APP_PATH = "/secure/nocas/app/curriculum/";
	private static final String JSON_LIST = "list/";
	private static final String JSON_DETTAGLIO = "dettaglio/";
	private static final String JSON_CAN_CREATE_CV = "canCreate/";
	private static final String JSON_ADD_CV = "add/";
	private static final String JSON_DEL_CV = "del/";
	private static final String JSON_UPLOAD_CV = "upload/";
	private static final String JSON_DOWNLOAD_CV = "download/";

	private static final String JSON_DEL_CV_ALLEGATO = "delCvAllegato/";

	private static final String PATH_LIST = ROOT_APP_PATH + JSON_LIST;
	private static final String PATH_DETTAGLIO = ROOT_APP_PATH + JSON_DETTAGLIO;
	private static final String PATH_ADD = ROOT_APP_PATH + JSON_ADD_CV;
	private static final String PATH_DEL = ROOT_APP_PATH + JSON_DEL_CV;

	private static final String PATH_UPLOAD = ROOT_APP_PATH + JSON_UPLOAD_CV;
	private static final String PATH_DOWNLOAD = ROOT_APP_PATH + JSON_DOWNLOAD_CV;
	private static final String PATH_DEL_CV_ALLEGATO = ROOT_APP_PATH + JSON_DEL_CV_ALLEGATO;
	private static final String PATH_CAN_CREATE = ROOT_APP_PATH + JSON_CAN_CREATE_CV;

	@GET
	@Path(JSON_LIST + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String index(@PathParam("tokenSecurity") String tokenSecurity, @QueryParam("token") String token) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_LIST);
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
				ret = curriculumUtenteEjb.getAllCurriculumUtente(username);
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
	@Path(JSON_DETTAGLIO + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String getCurriculum(@PathParam("tokenSecurity") String tokenSecurity, @QueryParam("token") String token,
			@QueryParam("id") String idCv) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_DETTAGLIO);

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
			// IdCv
			validator.setParam(idCv);
			validator.checkMaxLengthNumeric().checkPatternNumeric();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);

				ret = curriculumUtenteEjb.dettaglioCurriculumUtente(username, new Integer(idCv));
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
	@Path("/canCreate/{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String canCreate(@PathParam("tokenSecurity") String tokenSecurity, @QueryParam("token") String token) {

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_CAN_CREATE);

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
				ret = curriculumUtenteEjb.canCreate(username);
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
	@Path(JSON_ADD_CV + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String addCurriculumVitae(@PathParam("tokenSecurity") String tokenSecurity, @FormParam("token") String token,
			@FormParam("nomeCv") String nomeCv, @FormParam("codComNascita") String codComNascita,
			@FormParam("codComDomicilio") String codComDomicilio, @FormParam("email") String email,
			@FormParam("telefono") String telefono, @FormParam("cellulare") String cellulare // può essere null
	) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_ADD);

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
			// nomeCv è la descrizione della tabella cv_dati_personali
			validator.setParam(nomeCv);
			validator.checkMaxLengthDesRicSalv();
			// codComNascita
			validator.setParam(codComNascita);
			validator.checkMaxLengthCodCom();
			// codComDomicilio
			validator.setParam(codComDomicilio);
			validator.checkMaxLengthCodCom();
			// email
			validator.setParam(email);
			validator.checkEmail();
			// telefono
			validator.setParam(telefono);
			validator.checkMaxLengthTelefono();
			// cellulare
			if (null != cellulare) {
				validator.setParam(cellulare);
				validator.checkMaxLengthTelefono();
			}

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);

				ret = curriculumUtenteEjb.addCurriculumUtente(username, nomeCv, codComNascita, codComDomicilio, email,
						telefono, cellulare);
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
	@Path(JSON_DEL_CV + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String delCurriculumVitae(@PathParam("tokenSecurity") String tokenSecurity,
			@QueryParam("token") String token, @QueryParam("id") String idCv) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_DEL);

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
			// IdCv
			validator.setParam(idCv);
			validator.checkMaxLengthNumeric().checkPatternNumeric();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);

				ret = curriculumUtenteEjb.delCurriculumUtente(username, new Integer(idCv));
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
	@Path(JSON_UPLOAD_CV + "{tokenSecurity}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/json; charset=UTF-8")
	public String uploadFileCurriculumVitae(@PathParam("tokenSecurity") String tokenSecurity,
			MultipartFormDataInput input) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_UPLOAD);

		try {
			// Extract Params
			MultiPartHelper multiPartHelper = new MultiPartHelper(input.getFormDataMap());
			UploadedFile cv = multiPartHelper.extractFileFromMultiPart("uploadedFile");
			String token = multiPartHelper.extractStringFromMultiPart("token");
			String idCvDatiPersonali = multiPartHelper.extractStringFromMultiPart("idCvDatiPersonali");

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
			// IdCv
			validator.setParam(idCvDatiPersonali);
			validator.checkMaxLengthNumeric().checkPatternNumeric();
			// Nome file
			validator.setParam(cv.getFilename());
			validator.checkMaxLengthTokenSecurity().checkPatternMessaggio();
			// Dimensione file
			validator.setParamData(cv.getData());
			validator.checkMaxLengthFileCv();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				ret = curriculumUtenteEjb.uploadFileCurriculumVitae(username, Integer.valueOf(idCvDatiPersonali),
						cv.getFilename(), cv.getData());
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
	@Path(JSON_DOWNLOAD_CV + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String downloadFileCurriculumVitae(@PathParam("tokenSecurity") String tokenSecurity,
			@QueryParam("token") String token, @QueryParam("id") String idCv) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_DOWNLOAD);

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
			// IdCv
			validator.setParam(idCv);
			validator.checkMaxLengthNumeric().checkPatternNumeric();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				ret = curriculumUtenteEjb.downloadFileCurriculumVitae(username, Integer.valueOf(idCv));
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
	@Path(JSON_DEL_CV_ALLEGATO + "{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String delCvAllegato(@PathParam("tokenSecurity") String tokenSecurity, @QueryParam("token") String token,
			@QueryParam("id") String idCvAllegato) {

		String ret = null;

		String nomeMetodo = new Object() {
		}.getClass().getEnclosingMethod().getName();
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + "." + nomeMetodo);

		CheckerSec checkerSec = new CheckerSec(tokenSecurity, PATH_DEL_CV_ALLEGATO);

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
			// IdCv
			validator.setParam(idCvAllegato);
			validator.checkMaxLengthNumeric().checkPatternNumeric();

			if (checkerSec.isOk()) {
				String username = Utils.getUsernameFromTokenUtente(token);
				ret = curriculumUtenteEjb.delCvAllegato(username, new Integer(idCvAllegato));
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
