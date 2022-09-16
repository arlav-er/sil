package it.eng.sil.myauthservice.rest.server;

import it.eng.sil.base.exceptions.GamificationBadgeValueException;
import it.eng.sil.base.exceptions.GamificationDisabledException;
import it.eng.sil.base.exceptions.GamificationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Ri-mappatura degli errori applicativi su codici errore http
 * 
 * @see http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpStatus.html
 * @author pegoraro
 *
 */
@Provider
public class MyAuthGamificationExceptionHandler implements ExceptionMapper<GamificationException> {
	@Override
	public Response toResponse(GamificationException exception) {
		if (exception instanceof GamificationDisabledException) {
			return Response.status(Status.FORBIDDEN).entity(exception.getMessage()).build();
		} else if (exception instanceof GamificationBadgeValueException) {
			return Response.status(Status.NOT_ACCEPTABLE).entity(exception.getMessage()).build();
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
		}
	}
}