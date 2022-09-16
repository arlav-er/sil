package it.eng.sil.myauthservice.rest.server;

import it.eng.sil.base.exceptions.AccreditamentoRemoteException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MyAuthAccreditamentoExceptionHandler implements ExceptionMapper<AccreditamentoRemoteException> {

	@Override
	public Response toResponse(AccreditamentoRemoteException exception) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
	}

}
