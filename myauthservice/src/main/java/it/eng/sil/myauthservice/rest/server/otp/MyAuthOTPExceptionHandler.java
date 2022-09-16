package it.eng.sil.myauthservice.rest.server.otp;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.sil.base.exceptions.OtpRemoteException;
import it.eng.sil.myauthservice.model.ejb.business.sms.SmsException;

/**
 * Ri-mappatura degli errori applicativi su codici errore http
 * 
 * @see http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpStatus.html
 * @author pegoraro
 *
 */
@Provider
public class MyAuthOTPExceptionHandler implements ExceptionMapper<Exception> {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Response toResponse(Exception exception) {
		if (exception instanceof OtpRemoteException)
			return Response.status(Status.SERVICE_UNAVAILABLE).entity(exception.getMessage()).build();
		else if (exception instanceof OtpSmsRemoteException)
			return Response.status(Status.SERVICE_UNAVAILABLE).entity(exception.getMessage()).build();
		else if (exception instanceof SmsException)
			return Response.status(Status.SERVICE_UNAVAILABLE).entity(exception.getMessage()).build();
		if (exception instanceof OTPException)
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
		else
			log.error("GRAVE: eccezione inattesa " + exception.getClass());
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
	}
}