package it.eng.sil.myauthservice.rest.server;

import it.eng.sil.base.exceptions.ProfilaturaException;
import it.eng.sil.myauthservice.exception.ProfilaturaInvalidParameterException;
import it.eng.sil.myauthservice.exception.ProfilaturaMissingParameterException;
import it.eng.sil.myauthservice.exception.ProfilaturaNoResultException;

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
public class MyAuthProfileExceptionHandler implements ExceptionMapper<ProfilaturaException> {
	@Override
	public Response toResponse(ProfilaturaException exception) {
		if (exception instanceof ProfilaturaNoResultException)
			return Response.status(Status.NOT_FOUND).entity(exception.getMessage()).build();
		else if (exception instanceof ProfilaturaInvalidParameterException)
			return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
		else if (exception instanceof ProfilaturaMissingParameterException)
			return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
		else
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
	}
}