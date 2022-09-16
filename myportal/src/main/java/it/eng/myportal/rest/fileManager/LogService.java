package it.eng.myportal.rest.fileManager;

import java.io.File;
import java.io.FileInputStream;
import java.security.Principal;

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
@Path("/")
public class LogService {
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	protected Log log = LogFactory.getLog(this.getClass());
	/**
	 * Restituisce la foto dell'utente corrispondente all'id passato come parametro.
	 *
	 * @param curriculumId
	 *            Integer id dell'utente del quale si vuole la foto
	 * @param request
	 *            HttpServletRequest
	 * @return Response
	 */
	@GET
	@Path("getLog")
	@Produces("application/text")
	public Response getLog(@QueryParam("fileName") String fileName,	@Context HttpServletRequest request) {

		ResponseBuilder response;
		try {
			Principal up = request.getUserPrincipal();
			if (up == null) throw new Exception();
			String username = up.getName();
			if (!"amministratore".equals(username)) throw new Exception();
						
			File file = new File(System.getProperty("jboss.server.log.dir"));
			FileInputStream fis = new FileInputStream(new File(file,fileName));
			

			response = Response.ok(fis);
			String fname = "attachment; filename="+fileName;
			response.header(CONTENT_DISPOSITION, fname);
			
			return response.build();

		} catch (Exception e) {
			log.error(e);
			response = Response.serverError();
			return response.build();
		} 		
	}

}