package it.eng.myportal.rest;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;

/**
 * 
 * @author OMenghini
 *
 */
@Provider
@ServerInterceptor
@HeaderDecoratorPrecedence
public class CORSFilter implements PostProcessInterceptor {

	@Override
	public void postProcess(ServerResponse arg0) {
		MultivaluedMap<String, Object> headers = arg0.getMetadata();

		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
		headers.add("Access-Control-Allow-Credentials", "true");
		headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
		headers.add("Access-Control-Max-Age", "1209600");

	}

}
